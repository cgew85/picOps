package io.github.cgew85.picops.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.Menu;
import android.widget.Button;
import android.widget.ImageView;
import io.github.cgew85.picops.R;
import io.github.cgew85.picops.controller.*;
import io.github.cgew85.picops.model.Device;
import io.github.sporklibrary.Spork;
import io.github.sporklibrary.android.annotations.BindClick;
import io.github.sporklibrary.android.annotations.BindLayout;
import io.github.sporklibrary.android.annotations.BindView;

import java.io.*;

@BindLayout(R.layout.activity_pixel)
public class SelectionActivity extends Activity {

    private static final int IMAGE_CAPTURE = 1;
    private static final int RESULT_LOAD_IMAGE = 2;
    private Uri outputFileUri;
    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.btnImageCapture)
    private Button buttonImageCapture;

    @BindView(R.id.btnGallerySelect)
    private Button buttonGallerySelect;

    @BindView(R.id.btnOpenImage)
    private Button buttonOpenImage;

    private ImageView imageViewSmall;

    @BindView(R.id.btnMultiPictureEditing)
    private Button buttonMultiImageEditing;

    private boolean bmpLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Spork.bind(this);

        buttonOpenImage.setEnabled(false);

        // Device-Analyse
        Device device = new Device(getWindowManager().getDefaultDisplay());
        Log.d("INFO", "Height: " + device.getDeviceDisplayHeight() + " Width: " + device.getDeviceDisplayWidth());
    }

    @BindClick(R.id.btnGallerySelect)
    private void selectImageFromGallery() {
        buttonGallerySelect.setBackgroundResource(R.drawable.buttononclick);
        buttonGallerySelect.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
        buttonGallerySelect.setBackgroundResource(R.drawable.button);
        Log.d("INFO", "Button clicked");
    }

    @BindClick(R.id.btnImageCapture)
    private void captureImage() {
        buttonImageCapture.setBackgroundResource(R.drawable.buttononclick);
        buttonImageCapture.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        startCamera();
        buttonImageCapture.setBackgroundResource(R.drawable.button);
    }

    @BindClick(R.id.btnMultiPictureEditing)
    private void multiImageEditing(final Button button) {
        buttonMultiImageEditing.setBackgroundResource(R.drawable.buttononclick);
        buttonMultiImageEditing.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        Intent i = new Intent(button.getContext(), EditingActivity.class);
        i.putExtra("Selector", "Fragment2");
        button.getContext().startActivity(i);
    }

    @BindClick(R.id.btnOpenImage)
    private void openImage(final Button button) {
        buttonOpenImage.setBackgroundResource(R.drawable.buttononclick);
        buttonOpenImage.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

        Intent i = new Intent(button.getContext(), EditingActivity.class);
        button.getContext().startActivity(i);
        SimpleCounterForTempFileName counter = SimpleCounterForTempFileName.getInstance();
        counter.setCounter(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pixel, menu);
        return true;
    }

    private void startCamera() {
        File file = new File(Environment.getExternalStorageDirectory() + "/picOps/" + ReadWriteSettings.getReadWriteSettings().getStringSetting(this, "Session") + ".JPEG");
        outputFileUri = Uri.fromFile(file);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(intent, IMAGE_CAPTURE);
    }

    /**
     * Guckt bei Result welche Eingabe reinkommt (Cam oder Gal)
     * <p>
     * int requestCode - Code, hier zur Unterscheidung zwischen Aufrufern
     * int resultCode - RESULT_OK -> abfangen
     * Intent data - zum Abrufen von Daten die dem Intent hinzugef�gt wurden
     **/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.gc();
        Log.d("INFO", "In onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);

        // if-branch f�r Kamera-Input
        if (requestCode == IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                imageViewSmall = (ImageView) findViewById(R.id.smallImagePreview);
                scaleImageFromCamInput(GetFilePath.getInstance().returnAbsoluteFilePath(this));
                String[] params = {GetFilePath.getInstance().returnAbsoluteFilePath(this), String.valueOf(imageViewSmall.getWidth()), String.valueOf(imageViewSmall.getHeight())};
                loadBitmap(params, imageViewSmall);
                bmpLoaded = true;
            } else {
                int rowsDeleted = getContentResolver().delete(outputFileUri, null, null);
                Log.d(TAG, rowsDeleted + " rows deleted");
            }
        }
        // if-branch f�r Galerie-Auswahl
        if (requestCode == RESULT_LOAD_IMAGE) {
            if (resultCode == RESULT_OK) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                Log.d("INFO", "Import into ExternalStorage");

                //Verzeichnis anlegen
                //TODO: Verzeichnis anlegen nach check auf ext/int
                String directory = Environment.getExternalStorageDirectory().toString();
                File directoryOnExternalDevice = new File(directory + "/picOps/");
                directoryOnExternalDevice.mkdirs();
                OutputStream fos = null;
                File file = new File(directory, "/picOps/" + ReadWriteSettings.getReadWriteSettings().getStringSetting(this, "Session") + ".JPEG");

                // Einsetzen der Skalierung bei zu gro�en Bitmaps
                ImageScaler mImageScaler = new ImageScaler();
                Bitmap bmp = mImageScaler.checkImageSizeAndScale(picturePath);

                try {
                    fos = new FileOutputStream(file);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
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
                Log.d("INFO", "AbsoluteFilePath(): " + file.getAbsolutePath());

                bmpLoaded = true;
                bmp.recycle();
                bmp = null;

                // ImagePreview anlegen und f�llen
                imageViewSmall = (ImageView) findViewById(R.id.smallImagePreview);
                String[] params = {GetFilePath.getInstance().returnAbsoluteFilePath(this), String.valueOf(imageViewSmall.getWidth()), String.valueOf(imageViewSmall.getHeight())};
                loadBitmap(params, imageViewSmall);
            }
        }
        //Button erst clickbar machen, wenn Bild geladen wurde
        if (bmpLoaded) {
            buttonOpenImage.setEnabled(true);
            buttonOpenImage.setBackgroundResource(R.drawable.button);
        }

    }

    private void loadBitmap(String[] params, ImageView iv) {
        BitmapWorkerTask task = new BitmapWorkerTask(iv);
        task.execute(params);
    }

    /**
     * Implementation Forced-Navigation
     **/
    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Exit app?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(builder.getContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("EXIT", true);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        Dialog dialog = builder.create();
        dialog.show();
    }

    private static void scaleImageFromCamInput(String filepath) {
        ImageScaler mImageScaler = new ImageScaler();
        Bitmap localBitmap = mImageScaler.checkImageSizeAndScale(filepath);
        File file = new File(filepath);
        file.delete();
        file = new File(filepath);
        try {
            OutputStream fos = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            localBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
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
        Log.d("INFO", "@scaleImageFromCamInput -> New Sizes: " + localBitmap.getWidth() + " / " + localBitmap.getHeight());
        localBitmap.recycle();
    }
}

		