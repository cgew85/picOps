package io.github.cgew85.picops;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import android.widget.SeekBar.OnSeekBarChangeListener;
import io.github.cgew85.picops.Anwendungsklassen.*;
import io.github.cgew85.picops.Grenzklassen.logEntry;

import java.io.*;
import java.util.ArrayList;

public class EffectsTabActivity extends ListActivity {
    private ArrayList<String> localListEffectNames = doFilter.getAllEffectNames();
    private ArrayAdapter<String> listAdapter;
    Bitmap bitmap = null;
    private int fragmentWidth, fragmentHeight;
    simpleCounterForTempFileName counter = simpleCounterForTempFileName.getInstance();
    static int degree;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filterlist);

        listAdapter = new ArrayAdapter<String>(EffectsTabActivity.this, R.layout.effectslist_item, localListEffectNames);
        setListAdapter(listAdapter);
    }

    @Override
    protected void onListItemClick(ListView l, final View v, int position, long id) {
        /** Daten aus dem Intent holen **/
        fragmentWidth = getIntent().getIntExtra("fragmentWidth", 0);
        fragmentHeight = getIntent().getIntExtra("fragmentHeight", 0);
        Log.d("INFO", "Daten aus Intent in EffectTab");
        Log.d("INFO", "fragmentWidth: " + fragmentWidth);
        Log.d("INFO", "fragmentHeight: " + fragmentHeight);

        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }

        super.onListItemClick(l, v, position, id);
        String s = localListEffectNames.get(position);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        /** Insert logic here **/
        if (s.equals("bildSpiegelungVertikal")) {
            ProgressDialog mDialog = new ProgressDialog(v.getContext());
            mDialog.setMessage("Please wait...");
            mDialog.setCancelable(false);
            mDialog.show();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (getFilePath.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), 0).equals("")) {
                        bitmap = doFilter.bildSpiegelungVertikal(scaleImage.decodeSampledBitmapFromResource(getFilePath.getInstance().returnAbsoluteFilePath(v.getContext()), fragmentWidth, fragmentHeight));
                    } else {
                        bitmap = doFilter.bildSpiegelungVertikal(scaleImage.decodeSampledBitmapFromResource(getFilePath.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), counter.getCounter() - 1), fragmentWidth, fragmentHeight));
                    }
                    String directory = Environment.getExternalStorageDirectory().toString();
                    OutputStream fos = null;
                    File file = new File(directory, "/picOps/" + readWriteSettings.getRWSettings().getStringSetting(v.getContext(), "Session") + "-" + counter.getCounter() + ".JPEG");
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
                    addLogEntry("bildSpiegelungVertikal", "");
                    System.gc();
                    Intent intent = new Intent(v.getContext(), BearbeitungsActivity.class);
                    startActivity(intent);
                }
            }).start();
        } else if (s.equals("rundeEcken")) {
            ProgressDialog mDialog = new ProgressDialog(v.getContext());
            mDialog.setMessage("Please wait...");
            mDialog.setCancelable(false);
            mDialog.show();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (getFilePath.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), 0).equals("")) {
                        bitmap = doFilter.rundeEcken(scaleImage.decodeSampledBitmapFromResource(getFilePath.getInstance().returnAbsoluteFilePath(v.getContext()), fragmentWidth, fragmentHeight), 90f);
                    } else {
                        bitmap = doFilter.rundeEcken(scaleImage.decodeSampledBitmapFromResource(getFilePath.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), counter.getCounter() - 1), fragmentWidth, fragmentHeight), 90f);
                    }
                    String directory = Environment.getExternalStorageDirectory().toString();
                    OutputStream fos = null;
                    File file = new File(directory, "/picOps/" + readWriteSettings.getRWSettings().getStringSetting(v.getContext(), "Session") + "-" + counter.getCounter() + ".JPEG");
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
                    addLogEntry("rundeEcken", "round:" + 90f);
                    System.gc();
                    Intent intent = new Intent(v.getContext(), BearbeitungsActivity.class);
                    startActivity(intent);
                }
            }).start();
        } else if (s.equals("bildSpiegelung")) {
            String[] choice = {"Horizontal", "Vertical"};
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setTitle("Chose mirroring type")
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
                                        if (getFilePath.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), 0).equals("")) {
                                            bitmap = doFilter.bildSpiegelung(scaleImage.decodeSampledBitmapFromResource(getFilePath.getInstance().returnAbsoluteFilePath(v.getContext()), fragmentWidth, fragmentHeight), 2);
                                        } else {
                                            bitmap = doFilter.bildSpiegelung(scaleImage.decodeSampledBitmapFromResource(getFilePath.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), counter.getCounter() - 1), fragmentWidth, fragmentHeight), 2);
                                        }
                                        String directory = Environment.getExternalStorageDirectory().toString();
                                        OutputStream fos = null;
                                        File file = new File(directory, "/picOps/" + readWriteSettings.getRWSettings().getStringSetting(v.getContext(), "Session") + "-" + counter.getCounter() + ".JPEG");
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
                                        addLogEntry("bildSpiegelung", "type:" + 2);
                                        System.gc();
                                        Intent intent = new Intent(v.getContext(), BearbeitungsActivity.class);
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
                                        if (getFilePath.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), 0).equals("")) {
                                            bitmap = doFilter.bildSpiegelung(scaleImage.decodeSampledBitmapFromResource(getFilePath.getInstance().returnAbsoluteFilePath(v.getContext()), fragmentWidth, fragmentHeight), 1);
                                        } else {
                                            bitmap = doFilter.bildSpiegelung(scaleImage.decodeSampledBitmapFromResource(getFilePath.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), counter.getCounter() - 1), fragmentWidth, fragmentHeight), 1);
                                        }
                                        String directory = Environment.getExternalStorageDirectory().toString();
                                        OutputStream fos = null;
                                        File file = new File(directory, "/picOps/" + readWriteSettings.getRWSettings().getStringSetting(v.getContext(), "Session") + "-" + counter.getCounter() + ".JPEG");
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
                                        addLogEntry("bildSpiegelung", "type:" + 1);
                                        System.gc();
                                        Intent intent = new Intent(v.getContext(), BearbeitungsActivity.class);
                                        startActivity(intent);
                                    }
                                }).start();
                            }
                        }
                    });
            Dialog dialog = builder.create();
            dialog.show();
        } else if (s.equals("createSepiaToningEffect")) {
            /** depth = 125 **/
            final int depth = 10;

            ProgressDialog mDialog = new ProgressDialog(v.getContext());
            mDialog.setMessage("Please wait...");
            mDialog.setCancelable(false);
            mDialog.show();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (getFilePath.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), 0).equals("")) {
                        bitmap = doFilter.createSepiaToningEffect(scaleImage.decodeSampledBitmapFromResource(getFilePath.getInstance().returnAbsoluteFilePath(v.getContext()), fragmentWidth, fragmentHeight), depth);
                    } else {
                        bitmap = doFilter.createSepiaToningEffect(scaleImage.decodeSampledBitmapFromResource(getFilePath.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), counter.getCounter() - 1), fragmentWidth, fragmentHeight), depth);
                    }
                    String directory = Environment.getExternalStorageDirectory().toString();
                    OutputStream fos = null;
                    File file = new File(directory, "/picOps/" + readWriteSettings.getRWSettings().getStringSetting(v.getContext(), "Session") + "-" + counter.getCounter() + ".JPEG");
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
                    addLogEntry("createSepiaToningEffect", "depth:" + depth);
                    System.gc();
                    Intent intent = new Intent(v.getContext(), BearbeitungsActivity.class);
                    startActivity(intent);
                }
            }).start();
        } else if (s.equals("rotateImage")) {
            String[] choice = {"90�", "180�", "270�"};

            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setTitle("Rotate Image").setItems(choice, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if (which == 0) {
                        degree = 90;
                    } else if (which == 1) {
                        degree = 180;
                    } else if (which == 2) {
                        degree = 270;
                    }

                    ProgressDialog mDialog = new ProgressDialog(v.getContext());
                    mDialog.setMessage("Please wait...");
                    mDialog.setCancelable(false);
                    mDialog.show();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (getFilePath.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), 0).equals("")) {
                                bitmap = doFilter.rotateImage(scaleImage.decodeSampledBitmapFromResource(getFilePath.getInstance().returnAbsoluteFilePath(v.getContext()), fragmentWidth, fragmentHeight), degree);
                            } else {
                                bitmap = doFilter.rotateImage(scaleImage.decodeSampledBitmapFromResource(getFilePath.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), counter.getCounter() - 1), fragmentWidth, fragmentHeight), degree);
                            }
                            String directory = Environment.getExternalStorageDirectory().toString();
                            OutputStream fos = null;
                            File file = new File(directory, "/picOps/" + readWriteSettings.getRWSettings().getStringSetting(v.getContext(), "Session") + "-" + counter.getCounter() + ".JPEG");
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
                            addLogEntry("rotateImage", "degree:" + degree);
                            System.gc();
                            Intent intent = new Intent(v.getContext(), BearbeitungsActivity.class);
                            startActivity(intent);
                        }
                    }).start();
                }
            }).create().show();
        } else if (s.equals("addBorder")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            LayoutInflater inflater = EffectsTabActivity.this.getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_border, null);
            final SeekBar seekbar = (SeekBar) view.findViewById(R.id.borderSeekBar);
            final TextView textview = (TextView) view.findViewById(R.id.borderTextView);
            builder.setView(view).setTitle("Border size").setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final double valueBorder = (seekbar.getProgress()) * 0.1;


                    ProgressDialog mDialog = new ProgressDialog(v.getContext());
                    mDialog.setMessage("Please wait...");
                    mDialog.setCancelable(false);
                    mDialog.show();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (getFilePath.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), 0).equals("")) {
                                bitmap = doFilter.addBorder(scaleImage.decodeSampledBitmapFromResource(getFilePath.getInstance().returnAbsoluteFilePath(v.getContext()), fragmentWidth, fragmentHeight), valueBorder);
                            } else {
                                bitmap = doFilter.addBorder(scaleImage.decodeSampledBitmapFromResource(getFilePath.getInstance().returnAbsoluteFilePathWorkingCopy(v.getContext(), counter.getCounter() - 1), fragmentWidth, fragmentHeight), valueBorder);
                            }
                            String directory = Environment.getExternalStorageDirectory().toString();
                            OutputStream fos = null;
                            File file = new File(directory, "/picOps/" + readWriteSettings.getRWSettings().getStringSetting(v.getContext(), "Session") + "-" + counter.getCounter() + ".JPEG");
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
                            addLogEntry("addBorder", "value:" + valueBorder);
                            System.gc();
                            Intent intent = new Intent(v.getContext(), BearbeitungsActivity.class);
                            startActivity(intent);
                        }
                    }).start();

                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    textview.setText("Selected value: " + progress * 10 + " %");
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

        }

        Toast.makeText(EffectsTabActivity.this, "Selected Filter: " + s, Toast.LENGTH_LONG).show();
    }

    private void addLogEntry(String name, String values) {
        logEntry entry = new logEntry(name, values);
        logEntryListManager manager = logEntryListManager.getInstance();
        manager.addLogEntry(entry);
        Log.d("INFO", "Logging - name: " + name + " values: " + values);
    }
}
