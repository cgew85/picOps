package io.github.cgew85.picops.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import io.github.cgew85.picops.R;
import io.github.cgew85.picops.controller.FileHandlingController;
import io.github.cgew85.picops.controller.FilterController;
import io.github.cgew85.picops.controller.LogEntryListManager;
import io.github.cgew85.picops.controller.SettingsController;
import io.github.cgew85.picops.controller.ScaleImage;
import io.github.cgew85.picops.controller.SimpleCounterForTempFileName;
import io.github.cgew85.picops.model.LogEntry;

public class FilterTabActivity extends ListActivity {
    private ArrayList<String> localListFilterNames = FilterController.getAllFilterNames();
    Bitmap bitmap = null;
    private int fragmentWidth, fragmentHeight;
    SimpleCounterForTempFileName counter = SimpleCounterForTempFileName.getInstance();
    static int type;
    static float percent;
    static int valueGlaetten;
    static int valueSchaerfen;
    static double valueCreateContrast;
    static double valueDoGamma;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filterlist);

        ArrayAdapter<String> listAdapter = new ArrayAdapter<>(FilterTabActivity.this, R.layout.filterlist_item, localListFilterNames);
        setListAdapter(listAdapter);
    }

    @Override
    protected void onListItemClick(ListView l, final View v, int position, long id) {
        fragmentWidth = getIntent().getIntExtra("fragmentWidth", 0);
        fragmentHeight = getIntent().getIntExtra("fragmentHeight", 0);
        Log.d("INFO", "Daten aus Intent in FilterTab");
        Log.d("INFO", "fragmentWidth: " + fragmentWidth);
        Log.d("INFO", "fragmentHeight: " + fragmentHeight);

        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }

        super.onListItemClick(l, v, position, id);
        String s = localListFilterNames.get(position);

        switch (s) {
            case "imageSharpening": {
                ProgressDialog mDialog = new ProgressDialog(v.getContext());
                mDialog.setMessage("Please wait...");
                mDialog.setCancelable(false);
                mDialog.show();
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        if (FileHandlingController.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), 0).equals("")) {
                            bitmap = FilterController.imageSharpening(ScaleImage.decodeSampledBitmapFromResource(FileHandlingController.getInstance().returnAbsoluteFilePath(v.getContext()), fragmentWidth, fragmentHeight), 20, 20);
                        } else {
                            bitmap = FilterController.imageSharpening(ScaleImage.decodeSampledBitmapFromResource(FileHandlingController.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), counter.getCounter() - 1), fragmentWidth, fragmentHeight), 20, 20);
                        }
                        String directory = Environment.getExternalStorageDirectory().toString();
                        OutputStream fos = null;
                        File file = new File(directory, "/picOps/" + SettingsController.getReadWriteSettings(v.getContext()).getStringSetting("Session") + "-" + counter.getCounter() + ".JPEG");
                        try {
                            fos = new FileOutputStream(file);
                            BufferedOutputStream bos = new BufferedOutputStream(fos);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);

                            try {
                                bos.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            try {
                                bos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        counter.increaseCounter();
                        bitmap.recycle();
                        bitmap = null;
                        addLogEntry("imageSharpening", "");
                        System.gc();
                        Intent intent = new Intent(v.getContext(), EditingActivity.class);
                        startActivity(intent);
                    }
                }).start();

                break;
            }
            case "gaussianBlur": {
                ProgressDialog mDialog = new ProgressDialog(v.getContext());
                mDialog.setMessage("Please wait...");
                mDialog.setCancelable(false);
                mDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (FileHandlingController.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), 0).equals("")) {
                            bitmap = FilterController.gaussianBlur(ScaleImage.decodeSampledBitmapFromResource(FileHandlingController.getInstance().returnAbsoluteFilePath(v.getContext()), fragmentWidth, fragmentHeight));
                        } else {
                            bitmap = FilterController.gaussianBlur(ScaleImage.decodeSampledBitmapFromResource(FileHandlingController.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), counter.getCounter() - 1), fragmentWidth, fragmentHeight));
                        }
                        String directory = Environment.getExternalStorageDirectory().toString();
                        OutputStream fos = null;
                        File file = new File(directory, "/picOps/" + SettingsController.getReadWriteSettings(v.getContext()).getStringSetting("Session") + "-" + counter.getCounter() + ".JPEG");
                        try {
                            fos = new FileOutputStream(file);
                            BufferedOutputStream bos = new BufferedOutputStream(fos);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);

                            try {
                                bos.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            try {
                                bos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        counter.increaseCounter();
                        bitmap.recycle();
                        bitmap = null;
                        addLogEntry("gaussianBlur", "prevWidth:" + fragmentWidth + ";prevHeight:" + fragmentHeight);
                        System.gc();
                        Intent intent = new Intent(v.getContext(), EditingActivity.class);
                        startActivity(intent);
                    }
                }).start();
                break;
            }
            case "doGreyscale": {
                ProgressDialog mDialog = new ProgressDialog(v.getContext());
                mDialog.setMessage("Please wait...");
                mDialog.setCancelable(false);
                mDialog.show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (FileHandlingController.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), 0).equals("")) {
                            bitmap = FilterController.doGreyscale(ScaleImage.decodeSampledBitmapFromResource(FileHandlingController.getInstance().returnAbsoluteFilePath(v.getContext()), fragmentWidth, fragmentHeight));
                        } else {
                            bitmap = FilterController.doGreyscale(ScaleImage.decodeSampledBitmapFromResource(FileHandlingController.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), counter.getCounter() - 1), fragmentWidth, fragmentHeight));
                        }
                        String directory = Environment.getExternalStorageDirectory().toString();
                        OutputStream fos = null;
                        File file = new File(directory, "/picOps/" + SettingsController.getReadWriteSettings(v.getContext()).getStringSetting("Session") + "-" + counter.getCounter() + ".JPEG");
                        try {
                            fos = new FileOutputStream(file);
                            BufferedOutputStream bos = new BufferedOutputStream(fos);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);

                            try {
                                bos.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            try {
                                bos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        counter.increaseCounter();
                        bitmap.recycle();
                        bitmap = null;
                        addLogEntry("doGreyscale", "");
                        System.gc();
                        Intent intent = new Intent(v.getContext(), EditingActivity.class);
                        startActivity(intent);
                    }
                }).start();
                break;
            }
//        else if (s.equals("verstaerkenFarbtyp")) {
//            LayoutInflater inflater = FilterTabActivity.this.getLayoutInflater();
//            View view = inflater.inflate(R.layout.dialog_verstaerken_farbtyp, null);
//            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
//            final RadioButton rbRed = (RadioButton) view.findViewById(R.id.radioButton1);
//            final RadioButton rbGreen = (RadioButton) view.findViewById(R.id.radioButton2);
//            final RadioButton rbBlue = (RadioButton) view.findViewById(R.id.radioButton3);
//            final SeekBar seekbar = (SeekBar) view.findViewById(R.id.seekbar1);
//            final TextView textProgress = (TextView) view.findViewById(R.id.textViewProgress);
//            builder.setView(view.findViewById(R.layout.dialog_verstaerken_farbtyp)).setView(view).setPositiveButton("Accept", new DialogInterface.OnClickListener() {
//
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//
//                    if (!rbRed.isChecked() && !rbGreen.isChecked() && !rbBlue.isChecked()) {
//                        Toast.makeText(v.getContext(), "You must select at least one color", Toast.LENGTH_LONG).show();
//                    } else {
//                        //percent = (seekbar.getProgress())/100;
//                        percent = seekbar.getProgress();
//                        if (percent != 0) {
//                            percent = 1;
//                        } else {
//                            percent /= 100;
//                        }
//                        if (rbRed.isChecked()) {
//                            type = 1;
//                        } else if (rbGreen.isChecked()) {
//                            type = 2;
//                        } else if (rbBlue.isChecked()) {
//                            type = 3;
//                        }
//
//                        ProgressDialog mDialog = new ProgressDialog(v.getContext());
//                        mDialog.setMessage("Please wait...");
//                        mDialog.setCancelable(false);
//                        mDialog.show();
//
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (FileHandlingController.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), 0).equals("")) {
//                                    bitmap = FilterController.verstaerkenFarbtyp(ScaleImage.decodeSampledBitmapFromResource(FileHandlingController.getInstance().returnAbsoluteFilePath(v.getContext()), fragmentWidth, fragmentHeight), type, percent);
//                                } else {
//                                    bitmap = FilterController.verstaerkenFarbtyp(ScaleImage.decodeSampledBitmapFromResource(FileHandlingController.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), counter.getCounter() - 1), fragmentWidth, fragmentHeight), type, percent);
//                                }
//                                String directory = Environment.getExternalStorageDirectory().toString();
//                                OutputStream fos = null;
//                                File file = new File(directory, "/picOps/" + SettingsController.getReadWriteSettings().getStringSetting(v.getContext(), "Session") + "-" + counter.getCounter() + ".JPEG");
//                                try {
//                                    fos = new FileOutputStream(file);
//                                    BufferedOutputStream bos = new BufferedOutputStream(fos);
//                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
//
//                                    try {
//                                        bos.flush();
//                                    } catch (IOException e) {
//                                        e.printStackTrace();
//                                    }
//                                    try {
//                                        bos.close();
//                                    } catch (IOException e) {
//                                        e.printStackTrace();
//                                    }
//                                } catch (FileNotFoundException e) {
//                                    e.printStackTrace();
//                                }
//                                counter.increaseCounter();
//                                bitmap.recycle();
//                                bitmap = null;
//                                addLogEntry("verstaerkenFarbtyp", "type:" + type + ";percent:" + percent);
//                                System.gc();
//                                Intent intent = new Intent(v.getContext(), EditingActivity.class);
//                                startActivity(intent);
//                            }
//                        }).start();
//                    }
//                }
//            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.cancel();
//                }
//            });
//            seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
//
//                @Override
//                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                    textProgress.setText("Selected value: " + progress);
//                }
//
//                @Override
//                public void onStartTrackingTouch(SeekBar seekBar) {
//                }
//
//                @Override
//                public void onStopTrackingTouch(SeekBar seekBar) {
//                }
//
//            });
//            Dialog dialog = builder.create();
//            dialog.show();
//        }
            case "smoothing": {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                LayoutInflater inflater = FilterTabActivity.this.getLayoutInflater();
                View view = inflater.inflate(R.layout.dialog_glaetten, null);
                final SeekBar seekbar = (SeekBar) view.findViewById(R.id.glaettenSeekBar);
                final TextView textProgress = (TextView) view.findViewById(R.id.glaettenTextView);
                builder.setView(view).setTitle("Smoothing intensity")
                        .setPositiveButton("Accept", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                valueGlaetten = seekbar.getProgress();

                                ProgressDialog mDialog = new ProgressDialog(v.getContext());
                                mDialog.setMessage("Please wait...");
                                mDialog.setCancelable(false);
                                mDialog.show();

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (FileHandlingController.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), 0).equals("")) {
                                            bitmap = FilterController.smoothing(ScaleImage.decodeSampledBitmapFromResource(FileHandlingController.getInstance().returnAbsoluteFilePath(v.getContext()), fragmentWidth, fragmentHeight), valueGlaetten);
                                        } else {
                                            bitmap = FilterController.smoothing(ScaleImage.decodeSampledBitmapFromResource(FileHandlingController.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), counter.getCounter() - 1), fragmentWidth, fragmentHeight), valueGlaetten);
                                        }
                                        String directory = Environment.getExternalStorageDirectory().toString();
                                        OutputStream fos = null;
                                        File file = new File(directory, "/picOps/" + SettingsController.getReadWriteSettings(v.getContext()).getStringSetting("Session") + "-" + counter.getCounter() + ".JPEG");
                                        try {
                                            fos = new FileOutputStream(file);
                                            BufferedOutputStream bos = new BufferedOutputStream(fos);
                                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);

                                            try {
                                                bos.flush();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            try {
                                                bos.close();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        } catch (FileNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                        counter.increaseCounter();
                                        bitmap.recycle();
                                        bitmap = null;
                                        addLogEntry("smoothing", "value:" + valueGlaetten + ";fragmentWidth:" + fragmentWidth + ";fragmentHeight:" + fragmentHeight);
                                        System.gc();
                                        Intent intent = new Intent(v.getContext(), EditingActivity.class);
                                        startActivity(intent);
                                    }
                                }).start();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        textProgress.setText("Selected value: " + progress);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }

                });

                Dialog dialog = builder.create();
                dialog.show();
                break;
            }
            case "sharpening": {
                ProgressDialog mDialog = new ProgressDialog(v.getContext());
                mDialog.setMessage("Please wait...");
                mDialog.setCancelable(false);
                mDialog.show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (FileHandlingController.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), 0).equals("")) {
                            bitmap = FilterController.sharpening(ScaleImage.decodeSampledBitmapFromResource(FileHandlingController.getInstance().returnAbsoluteFilePath(v.getContext()), fragmentWidth, fragmentHeight));
                        } else {
                            bitmap = FilterController.sharpening(ScaleImage.decodeSampledBitmapFromResource(FileHandlingController.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), counter.getCounter() - 1), fragmentWidth, fragmentHeight));
                        }
                        String directory = Environment.getExternalStorageDirectory().toString();
                        OutputStream fos = null;
                        File file = new File(directory, "/picOps/" + SettingsController.getReadWriteSettings(v.getContext()).getStringSetting("Session") + "-" + counter.getCounter() + ".JPEG");
                        try {
                            fos = new FileOutputStream(file);
                            BufferedOutputStream bos = new BufferedOutputStream(fos);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);

                            try {
                                bos.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            try {
                                bos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        counter.increaseCounter();
                        bitmap.recycle();
                        bitmap = null;
                        addLogEntry("sharpening", "");
                        System.gc();
                        Intent intent = new Intent(v.getContext(), EditingActivity.class);
                        startActivity(intent);
                    }
                }).start();
                break;
            }
            case "createContrast": {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                LayoutInflater inflater = FilterTabActivity.this.getLayoutInflater();
                View view = inflater.inflate(R.layout.dialog_createcontrast, null);
                final SeekBar seekbar = (SeekBar) view.findViewById(R.id.createContrastSeekBar);
                final TextView textProgress = (TextView) view.findViewById(R.id.createContrastTextView);
                builder.setView(view).setTitle("Create Contrast")
                        .setPositiveButton("Accept", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                valueCreateContrast = (double) seekbar.getProgress();

                                ProgressDialog mDialog = new ProgressDialog(v.getContext());
                                mDialog.setMessage("Please wait...");
                                mDialog.setCancelable(false);
                                mDialog.show();

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (FileHandlingController.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), 0).equals("")) {
                                            bitmap = FilterController.createContrast(ScaleImage.decodeSampledBitmapFromResource(FileHandlingController.getInstance().returnAbsoluteFilePath(v.getContext()), fragmentWidth, fragmentHeight), valueCreateContrast);
                                        } else {
                                            bitmap = FilterController.createContrast(ScaleImage.decodeSampledBitmapFromResource(FileHandlingController.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), counter.getCounter() - 1), fragmentWidth, fragmentHeight), valueCreateContrast);
                                        }
                                        String directory = Environment.getExternalStorageDirectory().toString();
                                        OutputStream fos = null;
                                        File file = new File(directory, "/picOps/" + SettingsController.getReadWriteSettings(v.getContext()).getStringSetting("Session") + "-" + counter.getCounter() + ".JPEG");
                                        try {
                                            fos = new FileOutputStream(file);
                                            BufferedOutputStream bos = new BufferedOutputStream(fos);
                                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);

                                            try {
                                                bos.flush();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            try {
                                                bos.close();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        } catch (FileNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                        counter.increaseCounter();
                                        bitmap.recycle();
                                        bitmap = null;
                                        addLogEntry("createContrast", "value:" + valueCreateContrast);
                                        System.gc();
                                        Intent intent = new Intent(v.getContext(), EditingActivity.class);
                                        startActivity(intent);
                                    }
                                }).start();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        textProgress.setText("Selected value: " + progress);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }

                });

                Dialog dialog = builder.create();
                dialog.show();
                break;
            }
            case "createContrastSW": {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                LayoutInflater inflater = FilterTabActivity.this.getLayoutInflater();
                View view = inflater.inflate(R.layout.dialog_createcontrast, null);
                final SeekBar seekbar = (SeekBar) view.findViewById(R.id.createContrastSeekBar);
                final TextView textProgress = (TextView) view.findViewById(R.id.createContrastTextView);
                builder.setView(view).setTitle("Create Contrast (BW)")
                        .setPositiveButton("Accept", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                valueCreateContrast = (double) seekbar.getProgress();

                                ProgressDialog mDialog = new ProgressDialog(v.getContext());
                                mDialog.setMessage("Please wait...");
                                mDialog.setCancelable(false);
                                mDialog.show();

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (FileHandlingController.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), 0).equals("")) {
                                            bitmap = FilterController.createContrastSW(ScaleImage.decodeSampledBitmapFromResource(FileHandlingController.getInstance().returnAbsoluteFilePath(v.getContext()), fragmentWidth, fragmentHeight), valueCreateContrast);
                                        } else {
                                            bitmap = FilterController.createContrastSW(ScaleImage.decodeSampledBitmapFromResource(FileHandlingController.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), counter.getCounter() - 1), fragmentWidth, fragmentHeight), valueCreateContrast);
                                        }
                                        String directory = Environment.getExternalStorageDirectory().toString();
                                        OutputStream fos = null;
                                        File file = new File(directory, "/picOps/" + SettingsController.getReadWriteSettings(v.getContext()).getStringSetting("Session") + "-" + counter.getCounter() + ".JPEG");
                                        try {
                                            fos = new FileOutputStream(file);
                                            BufferedOutputStream bos = new BufferedOutputStream(fos);
                                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);

                                            try {
                                                bos.flush();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            try {
                                                bos.close();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        } catch (FileNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                        counter.increaseCounter();
                                        bitmap.recycle();
                                        bitmap = null;
                                        addLogEntry("createContrastSW", "value:" + valueCreateContrast);
                                        System.gc();
                                        Intent intent = new Intent(v.getContext(), EditingActivity.class);
                                        startActivity(intent);
                                    }
                                }).start();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        textProgress.setText("Selected value: " + progress);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }

                });

                Dialog dialog = builder.create();
                dialog.show();
                break;
            }
            case "doGamma": {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                LayoutInflater inflater = FilterTabActivity.this.getLayoutInflater();
                View view = inflater.inflate(R.layout.dialog_dogamma, null);
                final SeekBar seekbar = (SeekBar) view.findViewById(R.id.doGammaSeekBar);
                final TextView textProgress = (TextView) view.findViewById(R.id.doGammaTextView);
                builder.setView(view).setTitle("Gamma Correction")
                        .setPositiveButton("Accept", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                valueDoGamma = (double) seekbar.getProgress();
                                valueDoGamma /= 50;

                                ProgressDialog mDialog = new ProgressDialog(v.getContext());
                                mDialog.setMessage("Please wait...");
                                mDialog.setCancelable(false);
                                mDialog.show();

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (FileHandlingController.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), 0).equals("")) {
                                            bitmap = FilterController.doGamma(ScaleImage.decodeSampledBitmapFromResource(FileHandlingController.getInstance().returnAbsoluteFilePath(v.getContext()), fragmentWidth, fragmentHeight), valueDoGamma, valueDoGamma, valueDoGamma);
                                        } else {
                                            bitmap = FilterController.doGamma(ScaleImage.decodeSampledBitmapFromResource(FileHandlingController.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), counter.getCounter() - 1), fragmentWidth, fragmentHeight), valueDoGamma, valueDoGamma, valueDoGamma);
                                        }
                                        String directory = Environment.getExternalStorageDirectory().toString();
                                        OutputStream fos = null;
                                        File file = new File(directory, "/picOps/" + SettingsController.getReadWriteSettings(v.getContext()).getStringSetting("Session") + "-" + counter.getCounter() + ".JPEG");
                                        try {
                                            fos = new FileOutputStream(file);
                                            BufferedOutputStream bos = new BufferedOutputStream(fos);
                                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);

                                            try {
                                                bos.flush();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            try {
                                                bos.close();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        } catch (FileNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                        counter.increaseCounter();
                                        bitmap.recycle();
                                        bitmap = null;
                                        addLogEntry("doGamma", "red:" + valueDoGamma + ";green:" + valueDoGamma + ";blue:" + valueDoGamma);
                                        System.gc();
                                        Intent intent = new Intent(v.getContext(), EditingActivity.class);
                                        startActivity(intent);
                                    }
                                }).start();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        textProgress.setText("Selected value: " + progress);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }

                });

                Dialog dialog = builder.create();
                dialog.show();
                break;
            }
            case "decreaseColorDepth": {
                String[] choice = {"32", "64", "128"};
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Chose New Color Depth")
                        .setItems(choice, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    ProgressDialog mDialog = new ProgressDialog(v.getContext());
                                    mDialog.setMessage("Please wait...");
                                    mDialog.setCancelable(false);
                                    mDialog.show();

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (FileHandlingController.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), 0).equals("")) {
                                                bitmap = FilterController.decreaseColorDepth(ScaleImage.decodeSampledBitmapFromResource(FileHandlingController.getInstance().returnAbsoluteFilePath(v.getContext()), fragmentWidth, fragmentHeight), 32);
                                            } else {
                                                bitmap = FilterController.decreaseColorDepth(ScaleImage.decodeSampledBitmapFromResource(FileHandlingController.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), counter.getCounter() - 1), fragmentWidth, fragmentHeight), 32);
                                            }
                                            String directory = Environment.getExternalStorageDirectory().toString();
                                            OutputStream fos = null;
                                            File file = new File(directory, "/picOps/" + SettingsController.getReadWriteSettings(v.getContext()).getStringSetting("Session") + "-" + counter.getCounter() + ".JPEG");
                                            try {
                                                fos = new FileOutputStream(file);
                                                BufferedOutputStream bos = new BufferedOutputStream(fos);
                                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);

                                                try {
                                                    bos.flush();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                                try {
                                                    bos.close();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            } catch (FileNotFoundException e) {
                                                e.printStackTrace();
                                            }
                                            counter.increaseCounter();
                                            bitmap.recycle();
                                            bitmap = null;
                                            addLogEntry("decreaseColorDepth", "bitOffset:" + 32);
                                            System.gc();
                                            Intent intent = new Intent(v.getContext(), EditingActivity.class);
                                            startActivity(intent);
                                        }
                                    }).start();
                                } else if (which == 1) {
                                    ProgressDialog mDialog = new ProgressDialog(v.getContext());
                                    mDialog.setMessage("Please wait...");
                                    mDialog.setCancelable(false);
                                    mDialog.show();

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (FileHandlingController.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), 0).equals("")) {
                                                bitmap = FilterController.decreaseColorDepth(ScaleImage.decodeSampledBitmapFromResource(FileHandlingController.getInstance().returnAbsoluteFilePath(v.getContext()), fragmentWidth, fragmentHeight), 64);
                                            } else {
                                                bitmap = FilterController.decreaseColorDepth(ScaleImage.decodeSampledBitmapFromResource(FileHandlingController.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), counter.getCounter() - 1), fragmentWidth, fragmentHeight), 64);
                                            }
                                            String directory = Environment.getExternalStorageDirectory().toString();
                                            OutputStream fos = null;
                                            File file = new File(directory, "/picOps/" + SettingsController.getReadWriteSettings(v.getContext()).getStringSetting("Session") + "-" + counter.getCounter() + ".JPEG");
                                            try {
                                                fos = new FileOutputStream(file);
                                                BufferedOutputStream bos = new BufferedOutputStream(fos);
                                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);

                                                try {
                                                    bos.flush();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                                try {
                                                    bos.close();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            } catch (FileNotFoundException e) {
                                                e.printStackTrace();
                                            }
                                            counter.increaseCounter();
                                            bitmap.recycle();
                                            bitmap = null;
                                            addLogEntry("decreaseColorDepth", "bitOffset:" + 64);
                                            System.gc();
                                            Intent intent = new Intent(v.getContext(), EditingActivity.class);
                                            startActivity(intent);
                                        }
                                    }).start();
                                } else if (which == 2) {
                                    ProgressDialog mDialog = new ProgressDialog(v.getContext());
                                    mDialog.setMessage("Please wait...");
                                    mDialog.setCancelable(false);
                                    mDialog.show();

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (FileHandlingController.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), 0).equals("")) {
                                                bitmap = FilterController.decreaseColorDepth(ScaleImage.decodeSampledBitmapFromResource(FileHandlingController.getInstance().returnAbsoluteFilePath(v.getContext()), fragmentWidth, fragmentHeight), 128);
                                            } else {
                                                bitmap = FilterController.decreaseColorDepth(ScaleImage.decodeSampledBitmapFromResource(FileHandlingController.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), counter.getCounter() - 1), fragmentWidth, fragmentHeight), 128);
                                            }
                                            String directory = Environment.getExternalStorageDirectory().toString();
                                            OutputStream fos = null;
                                            File file = new File(directory, "/picOps/" + SettingsController.getReadWriteSettings(v.getContext()).getStringSetting("Session") + "-" + counter.getCounter() + ".JPEG");
                                            try {
                                                fos = new FileOutputStream(file);
                                                BufferedOutputStream bos = new BufferedOutputStream(fos);
                                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);

                                                try {
                                                    bos.flush();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                                try {
                                                    bos.close();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            } catch (FileNotFoundException e) {
                                                e.printStackTrace();
                                            }
                                            counter.increaseCounter();
                                            bitmap.recycle();
                                            bitmap = null;
                                            addLogEntry("decreaseColorDepth", "bitOffset:" + 128);
                                            System.gc();
                                            Intent intent = new Intent(v.getContext(), EditingActivity.class);
                                            startActivity(intent);
                                        }
                                    }).start();
                                }
                            }
                        });
                Dialog dialog = builder.create();
                dialog.show();
                break;
            }
            case "doBrightness": {
                String[] choice = {"Brighter", "Darker"};
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Increase / Decrease Brightness")
                        .setItems(choice, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    ProgressDialog mDialog = new ProgressDialog(v.getContext());
                                    mDialog.setMessage("Please wait...");
                                    mDialog.setCancelable(false);
                                    mDialog.show();

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (FileHandlingController.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), 0).equals("")) {
                                                bitmap = FilterController.doBrightness(ScaleImage.decodeSampledBitmapFromResource(FileHandlingController.getInstance().returnAbsoluteFilePath(v.getContext()), fragmentWidth, fragmentHeight), 10);
                                            } else {
                                                bitmap = FilterController.doBrightness(ScaleImage.decodeSampledBitmapFromResource(FileHandlingController.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), counter.getCounter() - 1), fragmentWidth, fragmentHeight), 10);
                                            }
                                            String directory = Environment.getExternalStorageDirectory().toString();
                                            OutputStream fos = null;
                                            File file = new File(directory, "/picOps/" + SettingsController.getReadWriteSettings(v.getContext()).getStringSetting("Session") + "-" + counter.getCounter() + ".JPEG");
                                            try {
                                                fos = new FileOutputStream(file);
                                                BufferedOutputStream bos = new BufferedOutputStream(fos);
                                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);

                                                try {
                                                    bos.flush();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                                try {
                                                    bos.close();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            } catch (FileNotFoundException e) {
                                                e.printStackTrace();
                                            }
                                            counter.increaseCounter();
                                            bitmap.recycle();
                                            bitmap = null;
                                            addLogEntry("doBrightness", "value:" + 10);
                                            System.gc();
                                            Intent intent = new Intent(v.getContext(), EditingActivity.class);
                                            startActivity(intent);
                                        }
                                    }).start();
                                } else if (which == 1) {
                                    ProgressDialog mDialog = new ProgressDialog(v.getContext());
                                    mDialog.setMessage("Please wait...");
                                    mDialog.setCancelable(false);
                                    mDialog.show();

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (FileHandlingController.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), 0).equals("")) {
                                                bitmap = FilterController.doBrightness(ScaleImage.decodeSampledBitmapFromResource(FileHandlingController.getInstance().returnAbsoluteFilePath(v.getContext()), fragmentWidth, fragmentHeight), -10);
                                            } else {
                                                bitmap = FilterController.doBrightness(ScaleImage.decodeSampledBitmapFromResource(FileHandlingController.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), counter.getCounter() - 1), fragmentWidth, fragmentHeight), -10);
                                            }
                                            String directory = Environment.getExternalStorageDirectory().toString();
                                            OutputStream fos = null;
                                            File file = new File(directory, "/picOps/" + SettingsController.getReadWriteSettings(v.getContext()).getStringSetting("Session") + "-" + counter.getCounter() + ".JPEG");
                                            try {
                                                fos = new FileOutputStream(file);
                                                BufferedOutputStream bos = new BufferedOutputStream(fos);
                                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);

                                                try {
                                                    bos.flush();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                                try {
                                                    bos.close();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            } catch (FileNotFoundException e) {
                                                e.printStackTrace();
                                            }
                                            counter.increaseCounter();
                                            bitmap.recycle();
                                            bitmap = null;
                                            addLogEntry("doBrightness", "value:" + (-10));
                                            System.gc();
                                            Intent intent = new Intent(v.getContext(), EditingActivity.class);
                                            startActivity(intent);
                                        }
                                    }).start();
                                }
                            }
                        });
                Dialog dialog = builder.create();
                dialog.show();
                break;
            }
            case "doColorFilter": {
                String[] choice = {"Red", "Green", "Blue"};
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Select color")
                        .setItems(choice, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    ProgressDialog mDialog = new ProgressDialog(v.getContext());
                                    mDialog.setMessage("Please wait...");
                                    mDialog.setCancelable(false);
                                    mDialog.show();

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (FileHandlingController.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), 0).equals("")) {
                                                bitmap = FilterController.doColorFilter(ScaleImage.decodeSampledBitmapFromResource(FileHandlingController.getInstance().returnAbsoluteFilePath(v.getContext()), fragmentWidth, fragmentHeight), 1, 0, 0);
                                            } else {
                                                bitmap = FilterController.doColorFilter(ScaleImage.decodeSampledBitmapFromResource(FileHandlingController.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), counter.getCounter() - 1), fragmentWidth, fragmentHeight), 1, 0, 0);
                                            }
                                            String directory = Environment.getExternalStorageDirectory().toString();
                                            OutputStream fos = null;
                                            File file = new File(directory, "/picOps/" + SettingsController.getReadWriteSettings(v.getContext()).getStringSetting("Session") + "-" + counter.getCounter() + ".JPEG");
                                            try {
                                                fos = new FileOutputStream(file);
                                                BufferedOutputStream bos = new BufferedOutputStream(fos);
                                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);

                                                try {
                                                    bos.flush();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                                try {
                                                    bos.close();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            } catch (FileNotFoundException e) {
                                                e.printStackTrace();
                                            }
                                            counter.increaseCounter();
                                            bitmap.recycle();
                                            bitmap = null;
                                            addLogEntry("doColorFilter", "red:" + 1 + ";green:" + 0 + ";blue:" + 0);
                                            System.gc();
                                            Intent intent = new Intent(v.getContext(), EditingActivity.class);
                                            startActivity(intent);
                                        }
                                    }).start();
                                } else if (which == 1) {
                                    ProgressDialog mDialog = new ProgressDialog(v.getContext());
                                    mDialog.setMessage("Please wait...");
                                    mDialog.setCancelable(false);
                                    mDialog.show();

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (FileHandlingController.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), 0).equals("")) {
                                                bitmap = FilterController.doColorFilter(ScaleImage.decodeSampledBitmapFromResource(FileHandlingController.getInstance().returnAbsoluteFilePath(v.getContext()), fragmentWidth, fragmentHeight), 0, 1, 0);
                                            } else {
                                                bitmap = FilterController.doColorFilter(ScaleImage.decodeSampledBitmapFromResource(FileHandlingController.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), counter.getCounter() - 1), fragmentWidth, fragmentHeight), 0, 1, 0);
                                            }
                                            String directory = Environment.getExternalStorageDirectory().toString();
                                            OutputStream fos = null;
                                            File file = new File(directory, "/picOps/" + SettingsController.getReadWriteSettings(v.getContext()).getStringSetting("Session") + "-" + counter.getCounter() + ".JPEG");
                                            try {
                                                fos = new FileOutputStream(file);
                                                BufferedOutputStream bos = new BufferedOutputStream(fos);
                                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);

                                                try {
                                                    bos.flush();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                                try {
                                                    bos.close();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            } catch (FileNotFoundException e) {
                                                e.printStackTrace();
                                            }
                                            counter.increaseCounter();
                                            bitmap.recycle();
                                            bitmap = null;
                                            addLogEntry("doColorFilter", "red:" + 0 + ";green:" + 1 + ";blue:" + 0);
                                            System.gc();
                                            Intent intent = new Intent(v.getContext(), EditingActivity.class);
                                            startActivity(intent);
                                        }
                                    }).start();
                                } else if (which == 2) {
                                    ProgressDialog mDialog = new ProgressDialog(v.getContext());
                                    mDialog.setMessage("Please wait...");
                                    mDialog.setCancelable(false);
                                    mDialog.show();

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (FileHandlingController.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), 0).equals("")) {
                                                bitmap = FilterController.doColorFilter(ScaleImage.decodeSampledBitmapFromResource(FileHandlingController.getInstance().returnAbsoluteFilePath(v.getContext()), fragmentWidth, fragmentHeight), 0, 0, 1);
                                            } else {
                                                bitmap = FilterController.doColorFilter(ScaleImage.decodeSampledBitmapFromResource(FileHandlingController.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), counter.getCounter() - 1), fragmentWidth, fragmentHeight), 0, 0, 1);
                                            }
                                            String directory = Environment.getExternalStorageDirectory().toString();
                                            OutputStream fos = null;
                                            File file = new File(directory, "/picOps/" + SettingsController.getReadWriteSettings(v.getContext()).getStringSetting("Session") + "-" + counter.getCounter() + ".JPEG");
                                            try {
                                                fos = new FileOutputStream(file);
                                                BufferedOutputStream bos = new BufferedOutputStream(fos);
                                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);

                                                try {
                                                    bos.flush();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                                try {
                                                    bos.close();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            } catch (FileNotFoundException e) {
                                                e.printStackTrace();
                                            }
                                            counter.increaseCounter();
                                            bitmap.recycle();
                                            bitmap = null;
                                            addLogEntry("doColorFilter", "red:" + 0 + ";green:" + 0 + ";blue:" + 1);
                                            System.gc();
                                            Intent intent = new Intent(v.getContext(), EditingActivity.class);
                                            startActivity(intent);
                                        }
                                    }).start();
                                }
                            }


                        });
                Dialog dialog = builder.create();
                dialog.show();
                break;
            }
            case "boxBlur": {
                ProgressDialog mDialog = new ProgressDialog(v.getContext());
                mDialog.setMessage("Please wait...");
                mDialog.setCancelable(false);
                mDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (FileHandlingController.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), 0).equals("")) {
                            bitmap = FilterController.boxBlur(ScaleImage.decodeSampledBitmapFromResource(FileHandlingController.getInstance().returnAbsoluteFilePath(v.getContext()), fragmentWidth, fragmentHeight), 5);
                        } else {
                            bitmap = FilterController.boxBlur(ScaleImage.decodeSampledBitmapFromResource(FileHandlingController.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), counter.getCounter() - 1), fragmentWidth, fragmentHeight), 5);
                        }
                        String directory = Environment.getExternalStorageDirectory().toString();
                        OutputStream fos = null;
                        File file = new File(directory, "/picOps/" + SettingsController.getReadWriteSettings(v.getContext()).getStringSetting("Session") + "-" + counter.getCounter() + ".JPEG");
                        try {
                            fos = new FileOutputStream(file);
                            BufferedOutputStream bos = new BufferedOutputStream(fos);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);

                            try {
                                bos.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            try {
                                bos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        counter.increaseCounter();
                        bitmap.recycle();
                        bitmap = null;
                        addLogEntry("boxBlur", "value:" + 10);
                        System.gc();
                        Intent intent = new Intent(v.getContext(), EditingActivity.class);
                        startActivity(intent);
                    }
                }).start();
                break;
            }
            case "hardLightMode": {
                ProgressDialog mDialog = new ProgressDialog(v.getContext());
                mDialog.setMessage("Please wait...");
                mDialog.setCancelable(false);
                mDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (FileHandlingController.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), 0).equals("")) {
                            bitmap = FilterController.hardLightMode(ScaleImage.decodeSampledBitmapFromResource(FileHandlingController.getInstance().returnAbsoluteFilePath(v.getContext()), fragmentWidth, fragmentHeight));
                        } else {
                            bitmap = FilterController.hardLightMode(ScaleImage.decodeSampledBitmapFromResource(FileHandlingController.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), counter.getCounter() - 1), fragmentWidth, fragmentHeight));
                        }
                        String directory = Environment.getExternalStorageDirectory().toString();
                        OutputStream fos = null;
                        File file = new File(directory, "/picOps/" + SettingsController.getReadWriteSettings(v.getContext()).getStringSetting("Session") + "-" + counter.getCounter() + ".JPEG");
                        try {
                            fos = new FileOutputStream(file);
                            BufferedOutputStream bos = new BufferedOutputStream(fos);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);

                            try {
                                bos.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            try {
                                bos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        counter.increaseCounter();
                        bitmap.recycle();
                        bitmap = null;
                        addLogEntry("hardLightMode", "");
                        System.gc();
                        Intent intent = new Intent(v.getContext(), EditingActivity.class);
                        startActivity(intent);
                    }
                }).start();
                break;
            }
            case "binaryImage": {
                ProgressDialog mDialog = new ProgressDialog(v.getContext());
                mDialog.setMessage("Please wait...");
                mDialog.setCancelable(false);
                mDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (FileHandlingController.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), 0).equals("")) {
                            bitmap = FilterController.binaryImage(ScaleImage.decodeSampledBitmapFromResource(FileHandlingController.getInstance().returnAbsoluteFilePath(v.getContext()), fragmentWidth, fragmentHeight));
                        } else {
                            bitmap = FilterController.binaryImage(ScaleImage.decodeSampledBitmapFromResource(FileHandlingController.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), counter.getCounter() - 1), fragmentWidth, fragmentHeight));
                        }
                        String directory = Environment.getExternalStorageDirectory().toString();
                        OutputStream fos = null;
                        File file = new File(directory, "/picOps/" + SettingsController.getReadWriteSettings(v.getContext()).getStringSetting("Session") + "-" + counter.getCounter() + ".JPEG");
                        try {
                            fos = new FileOutputStream(file);
                            BufferedOutputStream bos = new BufferedOutputStream(fos);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);

                            try {
                                bos.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            try {
                                bos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        counter.increaseCounter();
                        bitmap.recycle();
                        bitmap = null;
                        addLogEntry("binaryImage", "");
                        System.gc();
                        Intent intent = new Intent(v.getContext(), EditingActivity.class);
                        startActivity(intent);
                    }
                }).start();
                break;
            }
            case "alphaBlending": {
                ProgressDialog mDialog = new ProgressDialog(v.getContext());
                mDialog.setMessage("Please wait...");
                mDialog.setCancelable(false);
                mDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (FileHandlingController.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), 0).equals("")) {
                            bitmap = FilterController.alphaBlending(ScaleImage.decodeSampledBitmapFromResource(FileHandlingController.getInstance().returnAbsoluteFilePath(v.getContext()), fragmentWidth, fragmentHeight));
                        } else {
                            bitmap = FilterController.alphaBlending(ScaleImage.decodeSampledBitmapFromResource(FileHandlingController.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), counter.getCounter() - 1), fragmentWidth, fragmentHeight));
                        }
                        String directory = Environment.getExternalStorageDirectory().toString();
                        OutputStream fos = null;
                        File file = new File(directory, "/picOps/" + SettingsController.getReadWriteSettings(v.getContext()).getStringSetting("Session") + "-" + counter.getCounter() + ".JPEG");
                        try {
                            fos = new FileOutputStream(file);
                            BufferedOutputStream bos = new BufferedOutputStream(fos);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);

                            try {
                                bos.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            try {
                                bos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        counter.increaseCounter();
                        bitmap.recycle();
                        bitmap = null;
                        addLogEntry("alphaBlending", "");
                        System.gc();
                        Intent intent = new Intent(v.getContext(), EditingActivity.class);
                        startActivity(intent);
                    }
                }).start();
                break;
            }
            case "histogrammAusgleich": {
                ProgressDialog mDialog = new ProgressDialog(v.getContext());
                mDialog.setMessage("Please wait...");
                mDialog.setCancelable(false);
                mDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (FileHandlingController.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), 0).equals("")) {
                            bitmap = FilterController.histogrammAusgleich(ScaleImage.decodeSampledBitmapFromResource(FileHandlingController.getInstance().returnAbsoluteFilePath(v.getContext()), fragmentWidth, fragmentHeight));
                        } else {
                            bitmap = FilterController.histogrammAusgleich(ScaleImage.decodeSampledBitmapFromResource(FileHandlingController.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), counter.getCounter() - 1), fragmentWidth, fragmentHeight));
                        }
                        String directory = Environment.getExternalStorageDirectory().toString();
                        OutputStream fos = null;
                        File file = new File(directory, "/picOps/" + SettingsController.getReadWriteSettings(v.getContext()).getStringSetting("Session") + "-" + counter.getCounter() + ".JPEG");
                        try {
                            fos = new FileOutputStream(file);
                            BufferedOutputStream bos = new BufferedOutputStream(fos);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);

                            try {
                                bos.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            try {
                                bos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        counter.increaseCounter();
                        bitmap.recycle();
                        bitmap = null;
                        addLogEntry("histogrammAusgleich", "");
                        System.gc();
                        Intent intent = new Intent(v.getContext(), EditingActivity.class);
                        startActivity(intent);
                    }
                }).start();
                break;
            }
        }
        Toast.makeText(FilterTabActivity.this, "Selected Filter: " + s, Toast.LENGTH_LONG).show();
    }

    private void addLogEntry(String name, String values) {
        LogEntry entry = new LogEntry(name, values);
        LogEntryListManager manager = LogEntryListManager.getInstance();
        manager.addLogEntry(entry);
        Log.d("INFO", "Logging - name: " + name + " values: " + values);
    }
}
