package io.github.cgew85.picops.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.ImageView;
import io.github.cgew85.picops.R;
import io.github.cgew85.picops.controller.*;
import io.github.cgew85.picops.model.Device;
import io.github.cgew85.picops.model.ImageDetails;

import java.io.*;

/**
 * The Class PixelActivity.
 */
public class AuswahlActivity extends Activity {
    /**
     * The Constant IMAGE_CAPTURE.
     */
    private static final int IMAGE_CAPTURE = 1;

    /**
     * The Constant RESULT_LOAD_IMAGE.
     */
    private static final int RESULT_LOAD_IMAGE = 2;

    /**
     * The output file uri.
     */
    private Uri outputFileUri;

    /**
     * The Constant TITLE.
     */
    private static final String TITLE = "Test-Title";

    /**
     * The Constant DESCRIPTION.
     */
    private static final String DESCRIPTION = "Testaufnahme";

    /**
     * The Constant TAG.
     */
    private static final String TAG = MainActivity.class.getSimpleName();

    /**
     * The btn image capture.
     */
    Button btnImageCapture;

    /**
     * The btn gallery select.
     */
    Button btnGallerySelect;

    /**
     * The btn open image.
     */
    Button btnOpenImage;

    /**
     * The small image preview.
     */
    ImageView smallImagePreview;

    Button btnMultiImageEditing;

    boolean bmpLoaded = false;

    /**
     * The read write settings.
     */
    ReadWriteSettings settings = ReadWriteSettings.getRWSettings();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /** Ausblenden der Action- und der Statusbar **/
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_pixel);

        /** Session holen **/
        //Session session = Session.getSession();
        //Log.d("INFO", "Session: "+String.valueOf(session.getSessionID()));
        //Log.d("INFO", "Session(SP): "+settings.getStringSetting(this, "Session"));

        /** Views anmelden **/
        btnImageCapture = (Button) findViewById(R.id.btnImageCapture);
        btnGallerySelect = (Button) findViewById(R.id.btnGallerySelect);
        btnOpenImage = (Button) findViewById(R.id.btnOpenImage);
        btnOpenImage.setEnabled(false);
        btnMultiImageEditing = (Button) findViewById(R.id.btnMultiPictureEditing);

        /** Bild im Editiermodus �ffnen **/
        btnOpenImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnOpenImage.setBackgroundResource(R.drawable.buttononclick);
                btnOpenImage.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

                Intent i = new Intent(v.getContext(), BearbeitungsActivity.class);
                v.getContext().startActivity(i);
                SimpleCounterForTempFileName counter = SimpleCounterForTempFileName.getInstance();
                counter.setCounter(0);
            }
        });

        /** Multi-Image Editing **/
        btnMultiImageEditing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnMultiImageEditing.setBackgroundResource(R.drawable.buttononclick);
                btnMultiImageEditing.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                Intent i = new Intent(v.getContext(), BearbeitungsActivity.class);
                i.putExtra("Selector", "Fragment2");
                v.getContext().startActivity(i);
            }
        });

        /** Aufnahme starten **/
        btnImageCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnImageCapture.setBackgroundResource(R.drawable.buttononclick);
                btnImageCapture.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                startCamera();
                btnImageCapture.setBackgroundResource(R.drawable.button);
            }
        });

        /** Galerie �ffnen **/
        btnGallerySelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                btnGallerySelect.setBackgroundResource(R.drawable.buttononclick);
                btnGallerySelect.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
                btnGallerySelect.setBackgroundResource(R.drawable.button);
                Log.d("INFO", "Button clicked");
            }
        });

        /** Device-Analyse **/
        Device device = new Device(getWindowManager().getDefaultDisplay());
        Log.d("INFO", "Height: " + device.getDeviceDisplayHeight() + " Width: " + device.getDeviceDisplayWidth());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pixel, menu);
        return true;
    }

    /**
     * Img info.
     *
     * @param id the id
     */
    protected void imgInfo(int id) {
        ImageDetails details = new ImageDetails();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), id, options);
        details.setImageHeight(options.outHeight);
        details.setImageWidth(options.outWidth);
        details.setImageType(options.outMimeType);

        Log.d("INFO", "Height: " + details.getImageHeight() + " Width: " + details.getImageWidth() + " Type: " + details.getImageType());
    }


    /**
     * Start camera.
     */
    private void startCamera() {
        File file = new File(Environment.getExternalStorageDirectory() + "/picOps/" + ReadWriteSettings.getRWSettings().getStringSetting(this, "Session") + ".JPEG");
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

        /** if-branch f�r Kamera-Input **/
        if (requestCode == IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                smallImagePreview = (ImageView) findViewById(R.id.smallImagePreview);
                scaleImageFromCamInput(GetFilePath.getInstance().returnAbsoluteFilePath(this));
                String[] params = {GetFilePath.getInstance().returnAbsoluteFilePath(this), String.valueOf(smallImagePreview.getWidth()), String.valueOf(smallImagePreview.getHeight())};
                loadBitmap(params, smallImagePreview);
                bmpLoaded = true;
            } else {
                int rowsDeleted = getContentResolver().delete(outputFileUri, null, null);
                Log.d(TAG, rowsDeleted + " rows deleted");
            }
        }
        /** if-branch f�r Galerie-Auswahl **/
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

                /** Verzeichnis anlegen **/
                //TODO: Verzeichnis anlegen nach check auf ext/int
                String directory = Environment.getExternalStorageDirectory().toString();
                File directoryOnExternalDevice = new File(directory + "/picOps/");
                directoryOnExternalDevice.mkdirs();
                OutputStream fos = null;
                File file = new File(directory, "/picOps/" + ReadWriteSettings.getRWSettings().getStringSetting(this, "Session") + ".JPEG");

                /** Einsetzen der Skalierung bei zu gro�en Bitmaps**/
                CheckImageForScaling mCheckImageForScaling = new CheckImageForScaling();
                Bitmap bmp = mCheckImageForScaling.checkImageSizeAndScale(picturePath);

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

                /** ImagePreview anlegen und f�llen **/
                smallImagePreview = (ImageView) findViewById(R.id.smallImagePreview);
                String[] params = {GetFilePath.getInstance().returnAbsoluteFilePath(this), String.valueOf(smallImagePreview.getWidth()), String.valueOf(smallImagePreview.getHeight())};
                loadBitmap(params, smallImagePreview);
            }
        }
        /** Button erst clickbar machen, wenn Bild geladen wurde **/
        if (bmpLoaded) {
            btnOpenImage.setEnabled(true);
            btnOpenImage.setBackgroundResource(R.drawable.button);
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
                       /*
                       Intent intent = new Intent(Intent.ACTION_MAIN);
                	   intent.addCategory(Intent.CATEGORY_HOME);
                	   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                	   startActivity(intent);
                	   finish();
                	   */
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        /** Do nothing **/
                    }
                });
        Dialog dialog = builder.create();
        dialog.show();
    }

    private static void scaleImageFromCamInput(String filepath) {
        CheckImageForScaling mCheckImageForScaling = new CheckImageForScaling();
        Bitmap localBitmap = mCheckImageForScaling.checkImageSizeAndScale(filepath);
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
        localBitmap = null;
    }
}

		