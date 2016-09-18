package io.github.cgew85.picops.controller;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.Log;
import io.github.cgew85.picops.model.LogEntry;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The Class SaveImageToSD.
 */
public class SaveImageToSD {

    private static final String SUFFIX_JPEG = ".JPEG";
    private Bitmap bitmap = null;
    private String DIRECTORY_PICTURES = "Pictures";

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    /**
     * Apply step to picture.
     *
     * @param sessionID the session id
     * @param context   the context
     */
    public void applyStepToPicture(int sessionID, Context context) {
        // File Objekt anlegen - zeigt auf Originalfoto
        String directory = Environment.getExternalStorageDirectory().getAbsolutePath().concat("/picOps/");

        // Originalbild
        File file = new File(directory + sessionID + SUFFIX_JPEG);
        // Zielbild
        File outputFile = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES), sessionID + SUFFIX_JPEG);
        int inSampleSize = 0;

        try {
            inSampleSize = getBestSampleSize(file, outputFile);
            Log.d("INFO", "inSampleSize: " + inSampleSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("INFO", "Filename: " + file.getAbsolutePath());

        file.renameTo(outputFile);

        // Filter/Effekte anwenden
        LogEntryListManager manager = LogEntryListManager.getInstance();
        for (LogEntry logEntry : manager.getList()) {
            applyFiltersEffects(outputFile, logEntry, context, sessionID, inSampleSize);
        }
    }

    private void writeFile(final File file) {
        try {
            OutputStream fos = new FileOutputStream(file);
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
    }

    /**
     * Apply filters effects.
     *
     * @param file         the file
     * @param entry        the entry
     * @param context      the context
     * @param sessionID    the session id
     * @param inSampleSize the in sample size
     */
    private synchronized void applyFiltersEffects(final File file, LogEntry entry, Context context, final int sessionID, final int inSampleSize) {
        // Zun�chst Auswertung welcher Filter/Effekt angewendet werden muss
        Log.d("INFO", "In applyFiltersEffects - inSampleSize: " + inSampleSize);
        String tempName = entry.getName();
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }

        switch (tempName) {
            case "imageSharpening": {
                final ProgressDialog mDialog = new ProgressDialog(context);
                mDialog.setMessage("Applying imageSharpening...");
                mDialog.setCancelable(false);
                mDialog.show();
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("INFO", "In imageSharpening");
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = inSampleSize;

                        FileInputStream inStream = null;
                        try {
                            inStream = new FileInputStream(file);
                            Log.d("INFO", "Stream offen auf File: " + file.getAbsolutePath());
                        } catch (FileNotFoundException e1) {
                            e1.printStackTrace();
                        }

                        Bitmap inBitmap = BitmapFactory.decodeStream(inStream, null, options);
                        Bitmap bitmap = Bitmap.createBitmap(FilterController.imageSharpening(inBitmap, 20, 20));

                        try {
                            inStream.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        Log.d("INFO", "Stream geschlossen");
                        Log.d("INFO", "In run");
                        File outputFile = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES), sessionID + SUFFIX_JPEG);
                        writeFile(outputFile);
                        Log.d("INFO", "Pre recycle");
                        bitmap.recycle();
                        inBitmap.recycle();
                        mDialog.dismiss();
                    }
                });
                break;
            }
            case "gaussianBlur": {
                /** Auswertung der Values **/
                String[] values = entry.getValues().split(";");
                String[] width = values[0].split(":");
                final int origWidth = Integer.parseInt(width[1]);

                String[] height = values[1].split(":");
                final int origHeight = Integer.parseInt(height[1]);

                final ProgressDialog mDialog = new ProgressDialog(context);
                mDialog.setMessage("Applying Gaussian Blur...");
                mDialog.setCancelable(false);
                mDialog.show();
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("INFO", "In gaussianBlur");
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = inSampleSize;

                        FileInputStream inStream = null;
                        try {
                            inStream = new FileInputStream(file);
                            Log.d("INFO", "Stream offen auf File: " + file.getAbsolutePath());
                        } catch (FileNotFoundException e1) {
                            e1.printStackTrace();
                        }

                        Bitmap inBitmap = BitmapFactory.decodeStream(inStream, null, options);
                        Log.d("INFO", "in gaussianBlur -> alte Dimensionen: " + inBitmap.getWidth() + " / " + inBitmap.getHeight());
                        Log.d("INFO", "in gaussianBlur -> neue Dimensionen: " + inBitmap.getWidth() + " / " + inBitmap.getHeight());
                        Bitmap bitmap = Bitmap.createBitmap(FilterController.gaussianBlurForOutput(inBitmap, origWidth, origHeight, inBitmap.getWidth(), inBitmap.getHeight(), 3));

                        try {
                            inStream.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        Log.d("INFO", "Stream geschlossen");
                        Log.d("INFO", "In run");
                        File outputFile = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES), sessionID + SUFFIX_JPEG);
                        writeFile(outputFile);
                        Log.d("INFO", "Pre recycle");
                        bitmap.recycle();
                        inBitmap.recycle();
                        mDialog.dismiss();
                    }
                });
                break;
            }
            case "doGreyscale": {
                final ProgressDialog mDialog = new ProgressDialog(context);
                mDialog.setMessage("Applying Greyscale...");
                mDialog.setCancelable(false);
                mDialog.show();
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("INFO", "doGreyscale");
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = inSampleSize;

                        FileInputStream inStream = null;
                        try {
                            inStream = new FileInputStream(file);
                            Log.d("INFO", "Stream offen auf File: " + file.getAbsolutePath());
                        } catch (FileNotFoundException e1) {
                            e1.printStackTrace();
                        }

                        Bitmap inBitmap = BitmapFactory.decodeStream(inStream, null, options);
                        Bitmap bitmap = Bitmap.createBitmap(FilterController.doGreyscale(inBitmap));

                        try {
                            inStream.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        Log.d("INFO", "Stream geschlossen");
                        //bitmap = Bitmap.createBitmap(FilterController.unsharpMask(BitmapFactory.decodeFile(file.getAbsolutePath())));

                        Log.d("INFO", "In run");
                        File outputFile = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES), sessionID + SUFFIX_JPEG);
                        writeFile(outputFile);
                        Log.d("INFO", "Pre recycle");
                        bitmap.recycle();
                        inBitmap.recycle();
                        mDialog.dismiss();
                    }
                });
                break;
            }
            case "verstaerkenFarbtyp": {
                /** Auswertung der Values **/
                String[] values = entry.getValues().split(";");
                String[] type = values[0].split(":");
                String[] percent = values[1].split(":");
                final int typeValue = Integer.parseInt(type[1]);
                final float percentValue = Float.parseFloat(percent[1]);

                final ProgressDialog mDialog = new ProgressDialog(context);
                mDialog.setMessage("Applying Farbtypverstaerkung...");
                mDialog.setCancelable(false);
                mDialog.show();
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("INFO", "In verstaerkenFarbtyp");
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = inSampleSize;

                        FileInputStream inStream = null;
                        try {
                            inStream = new FileInputStream(file);
                            Log.d("INFO", "Stream offen auf File: " + file.getAbsolutePath());
                        } catch (FileNotFoundException e1) {
                            e1.printStackTrace();
                        }

                        Bitmap inBitmap = BitmapFactory.decodeStream(inStream, null, options);
                        Bitmap bitmap = Bitmap.createBitmap(FilterController.verstaerkenFarbtyp(inBitmap, typeValue, percentValue));

                        try {
                            inStream.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        Log.d("INFO", "Stream geschlossen");
                        //bitmap = Bitmap.createBitmap(FilterController.unsharpMask(BitmapFactory.decodeFile(file.getAbsolutePath())));

                        Log.d("INFO", "In run");
                        File outputFile = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES), sessionID + SUFFIX_JPEG);
                        writeFile(outputFile);
                        Log.d("INFO", "Pre recycle");
                        bitmap.recycle();
                        inBitmap.recycle();
                        mDialog.dismiss();
                    }
                });
                break;
            }
            case "smoothing": {
                /** Auswertung der Values **/
                String[] values = entry.getValues().split(";");
                String[] glaettenValue = values[0].split(":");
                final int glaettungsMaske = Integer.parseInt(glaettenValue[1]);

                String[] strFragmentWidth = values[1].split(":");
                final int fragmentWidth = Integer.parseInt(strFragmentWidth[1]);

                String[] strFragmentHeight = values[2].split(":");
                final int fragmentHeight = Integer.parseInt(strFragmentHeight[1]);

                final ProgressDialog mDialog = new ProgressDialog(context);
                mDialog.setMessage("Applying Glaetten...");
                mDialog.setCancelable(false);
                mDialog.show();
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("INFO", "In smoothing");
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = inSampleSize;

                        FileInputStream inStream = null;
                        try {
                            inStream = new FileInputStream(file);
                            Log.d("INFO", "Stream offen auf File: " + file.getAbsolutePath());
                        } catch (FileNotFoundException e1) {
                            e1.printStackTrace();
                        }

                        Bitmap inBitmap = BitmapFactory.decodeStream(inStream, null, options);
                        Bitmap bitmap = Bitmap.createBitmap(FilterController.glaettenFuerExport(inBitmap, glaettungsMaske, fragmentWidth, fragmentHeight, inBitmap.getWidth(), inBitmap.getHeight()));

                        try {
                            inStream.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        Log.d("INFO", "Stream geschlossen");
                        //bitmap = Bitmap.createBitmap(FilterController.unsharpMask(BitmapFactory.decodeFile(file.getAbsolutePath())));

                        Log.d("INFO", "In run");
                        File outputFile = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES), sessionID + SUFFIX_JPEG);
                        writeFile(outputFile);
                        Log.d("INFO", "Pre recycle");
                        bitmap.recycle();
                        inBitmap.recycle();
                        mDialog.dismiss();
                    }
                });
                break;
            }
            case "sharpening": {
                final ProgressDialog mDialog = new ProgressDialog(context);
                mDialog.setMessage("Applying Scharfzeichnen...");
                mDialog.setCancelable(false);
                mDialog.show();
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("INFO", "In sharpening");
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = inSampleSize;

                        FileInputStream inStream = null;
                        try {
                            inStream = new FileInputStream(file);
                            Log.d("INFO", "Stream offen auf File: " + file.getAbsolutePath());
                        } catch (FileNotFoundException e1) {
                            e1.printStackTrace();
                        }

                        Bitmap inBitmap = BitmapFactory.decodeStream(inStream, null, options);
                        Bitmap bitmap = Bitmap.createBitmap(FilterController.sharpening(inBitmap));

                        try {
                            inStream.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        Log.d("INFO", "Stream geschlossen");
                        Log.d("INFO", "In run");
                        File outputFile = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES), sessionID + SUFFIX_JPEG);
                        writeFile(outputFile);
                        Log.d("INFO", "Pre recycle");
                        bitmap.recycle();
                        inBitmap.recycle();
                        mDialog.dismiss();
                    }
                });
                break;
            }
            case "createContrast": {
                /** Auswertung der Values **/
                String[] values = entry.getValues().split(":");
                final double value = Double.parseDouble(values[1]);

                final ProgressDialog mDialog = new ProgressDialog(context);
                mDialog.setMessage("Applying Create Contrast...");
                mDialog.setCancelable(false);
                mDialog.show();
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("INFO", "In createContrast");
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = inSampleSize;

                        FileInputStream inStream = null;
                        try {
                            inStream = new FileInputStream(file);
                            Log.d("INFO", "Stream offen auf File: " + file.getAbsolutePath());
                        } catch (FileNotFoundException e1) {
                            e1.printStackTrace();
                        }

                        Bitmap inBitmap = BitmapFactory.decodeStream(inStream, null, options);
                        Bitmap bitmap = Bitmap.createBitmap(FilterController.createContrast(inBitmap, value));

                        try {
                            inStream.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        Log.d("INFO", "Stream geschlossen");
                        Log.d("INFO", "In run");
                        File outputFile = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES), sessionID + SUFFIX_JPEG);
                        writeFile(outputFile);
                        Log.d("INFO", "Pre recycle");
                        bitmap.recycle();
                        inBitmap.recycle();
                        mDialog.dismiss();
                    }
                });
                break;
            }
            case "createContrastSW": {
                /** Auswertung der Values **/
                String[] values = entry.getValues().split(":");
                final double value = Double.parseDouble(values[1]);

                final ProgressDialog mDialog = new ProgressDialog(context);
                mDialog.setMessage("Applying Create Contrast SW...");
                mDialog.setCancelable(false);
                mDialog.show();
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("INFO", "In createContrastSW");
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = inSampleSize;

                        FileInputStream inStream = null;
                        try {
                            inStream = new FileInputStream(file);
                            Log.d("INFO", "Stream offen auf File: " + file.getAbsolutePath());
                        } catch (FileNotFoundException e1) {
                            e1.printStackTrace();
                        }

                        Bitmap inBitmap = BitmapFactory.decodeStream(inStream, null, options);
                        Bitmap bitmap = Bitmap.createBitmap(FilterController.createContrastSW(inBitmap, value));

                        try {
                            inStream.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        Log.d("INFO", "Stream geschlossen");
                        Log.d("INFO", "In run");
                        File outputFile = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES), sessionID + SUFFIX_JPEG);
                        writeFile(outputFile);
                        Log.d("INFO", "Pre recycle");
                        bitmap.recycle();
                        inBitmap.recycle();
                        mDialog.dismiss();
                    }
                });
                break;
            }
            case "doGamma": {
                /** Auswertung der Values **/
                String[] values = entry.getValues().split(";");
                String[] redValue = values[0].split(":");
                final double red = Double.parseDouble(redValue[1]);

                String[] greenValue = values[1].split(":");
                final double green = Double.parseDouble(greenValue[1]);

                String[] blueValue = values[2].split(":");
                final double blue = Double.parseDouble(blueValue[1]);

                final ProgressDialog mDialog = new ProgressDialog(context);
                mDialog.setMessage("Applying Do Gamma...");
                mDialog.setCancelable(false);
                mDialog.show();
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("INFO", "In doGamma");
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = inSampleSize;

                        FileInputStream inStream = null;
                        try {
                            inStream = new FileInputStream(file);
                            Log.d("INFO", "Stream offen auf File: " + file.getAbsolutePath());
                        } catch (FileNotFoundException e1) {
                            e1.printStackTrace();
                        }

                        Bitmap inBitmap = BitmapFactory.decodeStream(inStream, null, options);
                        Bitmap bitmap = Bitmap.createBitmap(FilterController.doGamma(inBitmap, red, green, blue));

                        try {
                            inStream.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        Log.d("INFO", "Stream geschlossen");
                        Log.d("INFO", "In run");
                        File outputFile = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES), sessionID + SUFFIX_JPEG);
                        writeFile(outputFile);
                        Log.d("INFO", "Pre recycle");
                        bitmap.recycle();
                        inBitmap.recycle();
                        mDialog.dismiss();
                    }
                });
                break;
            }
            case "decreaseColorDepth": {
                /** Auswertung der Values **/
                String[] values = entry.getValues().split(":");
                final int bitOffset = Integer.parseInt(values[1]);

                final ProgressDialog mDialog = new ProgressDialog(context);
                mDialog.setMessage("Applying Decrease Colordepth...");
                mDialog.setCancelable(false);
                mDialog.show();
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("INFO", "In decreaseColorDepth");
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = inSampleSize;

                        FileInputStream inStream = null;
                        try {
                            inStream = new FileInputStream(file);
                            Log.d("INFO", "Stream offen auf File: " + file.getAbsolutePath());
                        } catch (FileNotFoundException e1) {
                            e1.printStackTrace();
                        }

                        Bitmap inBitmap = BitmapFactory.decodeStream(inStream, null, options);
                        Bitmap bitmap = Bitmap.createBitmap(FilterController.decreaseColorDepth(inBitmap, bitOffset));

                        try {
                            inStream.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        Log.d("INFO", "Stream geschlossen");
                        Log.d("INFO", "In run");
                        File outputFile = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES), sessionID + SUFFIX_JPEG);
                        writeFile(outputFile);
                        Log.d("INFO", "Pre recycle");
                        bitmap.recycle();
                        inBitmap.recycle();
                        mDialog.dismiss();
                    }
                });
                break;
            }
            case "doBrightness": {
                /** Auswertung der Values **/
                String[] values = entry.getValues().split(":");
                final int value = Integer.parseInt(values[1]);

                final ProgressDialog mDialog = new ProgressDialog(context);
                mDialog.setMessage("Applying Do Brightness...");
                mDialog.setCancelable(false);
                mDialog.show();
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("INFO", "In doBrightness");
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = inSampleSize;

                        FileInputStream inStream = null;
                        try {
                            inStream = new FileInputStream(file);
                            Log.d("INFO", "Stream offen auf File: " + file.getAbsolutePath());
                        } catch (FileNotFoundException e1) {
                            e1.printStackTrace();
                        }

                        Bitmap inBitmap = BitmapFactory.decodeStream(inStream, null, options);
                        Bitmap bitmap = Bitmap.createBitmap(FilterController.doBrightness(inBitmap, value));

                        try {
                            inStream.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        Log.d("INFO", "Stream geschlossen");
                        Log.d("INFO", "In run");
                        File outputFile = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES), sessionID + SUFFIX_JPEG);
                        writeFile(outputFile);
                        Log.d("INFO", "Pre recycle");
                        bitmap.recycle();
                        inBitmap.recycle();
                        mDialog.dismiss();
                    }
                });
                break;
            }
            case "doColorFilter": {
                /** Auswertung der Values **/
                String[] values = entry.getValues().split(";");
                String[] redValue = values[0].split(":");
                final double red = Double.parseDouble(redValue[1]);

                String[] greenValue = values[1].split(":");
                final double green = Double.parseDouble(greenValue[1]);

                String[] blueValue = values[2].split(":");
                final double blue = Double.parseDouble(blueValue[1]);

                final ProgressDialog mDialog = new ProgressDialog(context);
                mDialog.setMessage("Applying Do Color Filter...");
                mDialog.setCancelable(false);
                mDialog.show();
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("INFO", "In doColorFilter");
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = inSampleSize;

                        FileInputStream inStream = null;
                        try {
                            inStream = new FileInputStream(file);
                            Log.d("INFO", "Stream offen auf File: " + file.getAbsolutePath());
                        } catch (FileNotFoundException e1) {
                            e1.printStackTrace();
                        }

                        Bitmap inBitmap = BitmapFactory.decodeStream(inStream, null, options);
                        Bitmap bitmap = Bitmap.createBitmap(FilterController.doColorFilter(inBitmap, red, green, blue));

                        try {
                            inStream.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        Log.d("INFO", "Stream geschlossen");
                        Log.d("INFO", "In run");
                        File outputFile = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES), sessionID + SUFFIX_JPEG);
                        writeFile(outputFile);
                        Log.d("INFO", "Pre recycle");
                        bitmap.recycle();
                        inBitmap.recycle();
                        mDialog.dismiss();
                    }
                });
                break;
            }
        }
        switch (tempName) {
            case "bildSpiegelungVertikal": {
                final ProgressDialog mDialog = new ProgressDialog(context);
                mDialog.setMessage("Applying bildSpiegelungVertikal...");
                mDialog.setCancelable(false);
                mDialog.show();
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("INFO", "In bildSpiegelungVertikal");
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = inSampleSize;

                        FileInputStream inStream = null;
                        try {
                            inStream = new FileInputStream(file);
                            Log.d("INFO", "Stream offen auf File: " + file.getAbsolutePath());
                        } catch (FileNotFoundException e1) {
                            e1.printStackTrace();
                        }

                        Bitmap inBitmap = BitmapFactory.decodeStream(inStream, null, options);
                        Bitmap bitmap = Bitmap.createBitmap(FilterController.bildSpiegelungVertikal((inBitmap)));

                        try {
                            inStream.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        Log.d("INFO", "Stream geschlossen");
                        Log.d("INFO", "In run");
                        File outputFile = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES), sessionID + SUFFIX_JPEG);
                        writeFile(outputFile);
                        Log.d("INFO", "Pre recycle");
                        bitmap.recycle();
                        inBitmap.recycle();
                        mDialog.dismiss();
                    }
                });
                break;
            }
            case "roundCorners": {
                final ProgressDialog mDialog = new ProgressDialog(context);
                mDialog.setMessage("Applying Runde Ecken...");
                mDialog.setCancelable(false);
                mDialog.show();
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("INFO", "In roundCorners");
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = inSampleSize;

                        FileInputStream inStream = null;
                        try {
                            inStream = new FileInputStream(file);
                            Log.d("INFO", "Stream offen auf File: " + file.getAbsolutePath());
                        } catch (FileNotFoundException e1) {
                            e1.printStackTrace();
                        }

                        Bitmap inBitmap = BitmapFactory.decodeStream(inStream, null, options);
                        Bitmap bitmap = Bitmap.createBitmap(FilterController.roundCorners(inBitmap, 90f));

                        try {
                            inStream.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        Log.d("INFO", "Stream geschlossen");
                        Log.d("INFO", "In run");
                        File outputFile = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES), sessionID + SUFFIX_JPEG);
                        writeFile(outputFile);
                        Log.d("INFO", "Pre recycle");
                        bitmap.recycle();
                        inBitmap.recycle();
                        mDialog.dismiss();
                    }
                });
                break;
            }
            case "bildSpiegelung": {
                /** Auswertung der Values **/
                String[] values = entry.getValues().split(":");
                final int valueSpiegelung = Integer.parseInt(values[1]);

                final ProgressDialog mDialog = new ProgressDialog(context);
                mDialog.setMessage("Applying bildSpiegelung...");
                mDialog.setCancelable(false);
                mDialog.show();
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("INFO", "In bildSpiegelung");
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = inSampleSize;

                        FileInputStream inStream = null;
                        try {
                            inStream = new FileInputStream(file);
                            Log.d("INFO", "Stream offen auf File: " + file.getAbsolutePath());
                        } catch (FileNotFoundException e1) {
                            e1.printStackTrace();
                        }

                        Bitmap inBitmap = BitmapFactory.decodeStream(inStream, null, options);
                        Bitmap bitmap = Bitmap.createBitmap(FilterController.bildSpiegelung(inBitmap, valueSpiegelung));

                        try {
                            inStream.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        Log.d("INFO", "Stream geschlossen");
                        Log.d("INFO", "In run");
                        File outputFile = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES), sessionID + SUFFIX_JPEG);
                        writeFile(outputFile);
                        Log.d("INFO", "Pre recycle");
                        bitmap.recycle();
                        inBitmap.recycle();
                        mDialog.dismiss();
                    }
                });
                break;
            }
            case "createSepiaToningEffect": {
                /** Auswertung der Values **/
                String[] values = entry.getValues().split(":");
                final int depthValue = Integer.parseInt(values[1]);

                final ProgressDialog mDialog = new ProgressDialog(context);
                mDialog.setMessage("Applying createSepiaToningEffect...");
                mDialog.setCancelable(false);
                mDialog.show();
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("INFO", "In createSepiaToningEffect");
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = inSampleSize;

                        FileInputStream inStream = null;
                        try {
                            inStream = new FileInputStream(file);
                            Log.d("INFO", "Stream offen auf File: " + file.getAbsolutePath());
                        } catch (FileNotFoundException e1) {
                            e1.printStackTrace();
                        }

                        Bitmap inBitmap = BitmapFactory.decodeStream(inStream, null, options);
                        Bitmap bitmap = Bitmap.createBitmap(FilterController.createSepiaToningEffect(inBitmap, depthValue));

                        try {
                            inStream.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        Log.d("INFO", "Stream geschlossen");
                        Log.d("INFO", "In run");
                        File outputFile = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES), sessionID + SUFFIX_JPEG);
                        writeFile(outputFile);
                        Log.d("INFO", "Pre recycle");
                        bitmap.recycle();
                        inBitmap.recycle();
                        mDialog.dismiss();
                    }
                });
                break;
            }
            case "rotateImage": {
                /** Auswertung der Values **/
                String[] values = entry.getValues().split(":");
                final int degree = Integer.parseInt(values[1]);

                final ProgressDialog mDialog = new ProgressDialog(context);
                mDialog.setMessage("Applying rotateImage...");
                mDialog.setCancelable(false);
                mDialog.show();
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("INFO", "In rotateImage");
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = inSampleSize;

                        FileInputStream inStream = null;
                        try {
                            inStream = new FileInputStream(file);
                            Log.d("INFO", "Stream offen auf File: " + file.getAbsolutePath());
                        } catch (FileNotFoundException e1) {
                            e1.printStackTrace();
                        }

                        Bitmap inBitmap = BitmapFactory.decodeStream(inStream, null, options);
                        Bitmap bitmap = Bitmap.createBitmap(FilterController.rotateImage(inBitmap, degree));

                        try {
                            inStream.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        Log.d("INFO", "Stream geschlossen");
                        Log.d("INFO", "In run");
                        File outputFile = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES), sessionID + SUFFIX_JPEG);
                        writeFile(outputFile);
                        Log.d("INFO", "Pre recycle");
                        bitmap.recycle();
                        inBitmap.recycle();
                        mDialog.dismiss();
                    }
                });
                break;
            }
            case "boxBlur": {
                /** Auswertung der Values **/
                String[] values = entry.getValues().split(":");
                final int value = Integer.parseInt(values[1]);

                final ProgressDialog mDialog = new ProgressDialog(context);
                mDialog.setMessage("Applying boxBlur...");
                mDialog.setCancelable(false);
                mDialog.show();
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("INFO", "In boxBlur");
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = inSampleSize;

                        FileInputStream inStream = null;
                        try {
                            inStream = new FileInputStream(file);
                            Log.d("INFO", "Stream offen auf File: " + file.getAbsolutePath());
                        } catch (FileNotFoundException e1) {
                            e1.printStackTrace();
                        }

                        Bitmap inBitmap = BitmapFactory.decodeStream(inStream, null, options);
                        Bitmap bitmap = Bitmap.createBitmap(FilterController.boxBlur(inBitmap, value));

                        try {
                            inStream.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        Log.d("INFO", "Stream geschlossen");
                        Log.d("INFO", "In run");
                        File outputFile = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES), sessionID + SUFFIX_JPEG);
                        writeFile(outputFile);
                        Log.d("INFO", "Pre recycle");
                        bitmap.recycle();
                        inBitmap.recycle();
                        mDialog.dismiss();
                    }
                });
                break;
            }
            case "hardLightMode": {
                final ProgressDialog mDialog = new ProgressDialog(context);
                mDialog.setMessage("Applying hardLightMode...");
                mDialog.setCancelable(false);
                mDialog.show();
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("INFO", "In hardLightMode");
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = inSampleSize;

                        FileInputStream inStream = null;
                        try {
                            inStream = new FileInputStream(file);
                            Log.d("INFO", "Stream offen auf File: " + file.getAbsolutePath());
                        } catch (FileNotFoundException e1) {
                            e1.printStackTrace();
                        }

                        Bitmap inBitmap = BitmapFactory.decodeStream(inStream, null, options);
                        Bitmap bitmap = Bitmap.createBitmap(FilterController.hardLightMode(inBitmap));

                        try {
                            inStream.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        Log.d("INFO", "Stream geschlossen");
                        Log.d("INFO", "In run");
                        File outputFile = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES), sessionID + SUFFIX_JPEG);
                        writeFile(outputFile);
                        Log.d("INFO", "Pre recycle");
                        bitmap.recycle();
                        inBitmap.recycle();
                        mDialog.dismiss();
                    }
                });
                break;
            }
            case "binaryImage": {
                final ProgressDialog mDialog = new ProgressDialog(context);
                mDialog.setMessage("Applying binaryImage...");
                mDialog.setCancelable(false);
                mDialog.show();
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("INFO", "In binaryImage");
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = inSampleSize;

                        FileInputStream inStream = null;
                        try {
                            inStream = new FileInputStream(file);
                            Log.d("INFO", "Stream offen auf File: " + file.getAbsolutePath());
                        } catch (FileNotFoundException e1) {
                            e1.printStackTrace();
                        }

                        Bitmap inBitmap = BitmapFactory.decodeStream(inStream, null, options);
                        Bitmap bitmap = Bitmap.createBitmap(FilterController.binaryImage(inBitmap));

                        try {
                            inStream.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        Log.d("INFO", "Stream geschlossen");
                        Log.d("INFO", "In run");
                        File outputFile = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES), sessionID + SUFFIX_JPEG);
                        writeFile(outputFile);
                        Log.d("INFO", "Pre recycle");
                        bitmap.recycle();
                        inBitmap.recycle();
                        mDialog.dismiss();
                    }
                });
                break;
            }
            case "alphaBlending": {
                final ProgressDialog mDialog = new ProgressDialog(context);
                mDialog.setMessage("Applying alphaBlending...");
                mDialog.setCancelable(false);
                mDialog.show();
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("INFO", "In alphaBlending");
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = inSampleSize;

                        FileInputStream inStream = null;
                        try {
                            inStream = new FileInputStream(file);
                            Log.d("INFO", "Stream offen auf File: " + file.getAbsolutePath());
                        } catch (FileNotFoundException e1) {
                            e1.printStackTrace();
                        }

                        Bitmap inBitmap = BitmapFactory.decodeStream(inStream, null, options);
                        Bitmap bitmap = Bitmap.createBitmap(FilterController.alphaBlending(inBitmap));

                        try {
                            inStream.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        Log.d("INFO", "Stream geschlossen");
                        Log.d("INFO", "In run");
                        File outputFile = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES), sessionID + SUFFIX_JPEG);
                        writeFile(outputFile);
                        Log.d("INFO", "Pre recycle");
                        bitmap.recycle();
                        inBitmap.recycle();
                        mDialog.dismiss();
                    }
                });
                break;
            }
            case "histogrammAusgleich": {
                final ProgressDialog mDialog = new ProgressDialog(context);
                mDialog.setMessage("Applying histogrammAusgleich...");
                mDialog.setCancelable(false);
                mDialog.show();
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("INFO", "In histogrammAusgleich");
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = inSampleSize;

                        FileInputStream inStream = null;
                        try {
                            inStream = new FileInputStream(file);
                            Log.d("INFO", "Stream offen auf File: " + file.getAbsolutePath());
                        } catch (FileNotFoundException e1) {
                            e1.printStackTrace();
                        }

                        Bitmap inBitmap = BitmapFactory.decodeStream(inStream, null, options);
                        Bitmap bitmap = Bitmap.createBitmap(FilterController.histogrammAusgleich(inBitmap));

                        try {
                            inStream.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        Log.d("INFO", "Stream geschlossen");
                        Log.d("INFO", "In run");
                        File outputFile = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES), sessionID + SUFFIX_JPEG);
                        writeFile(outputFile);
                        Log.d("INFO", "Pre recycle");
                        bitmap.recycle();
                        inBitmap.recycle();
                        mDialog.dismiss();
                    }
                });
                break;
            }
            case "addBorder": {
                /** Auswertung der Values **/
                String[] values = entry.getValues().split(":");
                final double valueBorder = Double.parseDouble(values[1]);

                final ProgressDialog mDialog = new ProgressDialog(context);
                mDialog.setMessage("Applying addBorder...");
                mDialog.setCancelable(false);
                mDialog.show();
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("INFO", "In addBorder");
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = inSampleSize;

                        FileInputStream inStream = null;
                        try {
                            inStream = new FileInputStream(file);
                            Log.d("INFO", "Stream offen auf File: " + file.getAbsolutePath());
                        } catch (FileNotFoundException e1) {
                            e1.printStackTrace();
                        }

                        Bitmap inBitmap = BitmapFactory.decodeStream(inStream, null, options);
                        Bitmap bitmap = Bitmap.createBitmap(FilterController.addBorder(inBitmap, valueBorder));

                        try {
                            inStream.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        Log.d("INFO", "Stream geschlossen");
                        Log.d("INFO", "In run");
                        File outputFile = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES), sessionID + SUFFIX_JPEG);
                        writeFile(outputFile);
                        Log.d("INFO", "Pre recycle");
                        bitmap.recycle();
                        inBitmap.recycle();
                        mDialog.dismiss();
                    }
                });
                break;
            }
        }
    }

    /**
     * Gets the best sample size.
     *
     * @param inFile  the in file
     * @param outFile the out file
     * @return the best sample size
     * @throws FileNotFoundException the file not found exception
     * @throws IOException           Signals that an I/O exception has occurred.
     */
    private int getBestSampleSize(File inFile, File outFile) throws IOException {
        FileInputStream inStream;
        FileOutputStream outStream = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        Matrix matrix = new Matrix();

        for (options.inSampleSize = 1; options.inSampleSize <= 32; options.inSampleSize++) {
            try {
                inStream = new FileInputStream(inFile);
                Bitmap originalBitmap = BitmapFactory.decodeStream(inStream, null, options);
                Bitmap outputBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getHeight(), matrix, true);
                outStream = new FileOutputStream(outFile);
                outStream.close();
                originalBitmap.recycle();
                outputBitmap.recycle();

                return options.inSampleSize;
            } catch (OutOfMemoryError e) {
                /** Schleifendurchlauf fortsetzen **/
            } finally {
                if (outStream != null) {
                    try {
                        outStream.close();
                    } catch (IOException ignored) {

                    }
                }
            }
        }

        return -1;
    }
}
