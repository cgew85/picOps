package io.github.cgew85.picops.Anwendungsklassen;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

/**
 * The Class scaleImage
 * <p>
 * Bilder verkleinert in den Arbeitsspeicher laden.
 */

public class scaleImage {


    /**
     * Instantiates a new scale image.
     */
    public scaleImage() {
    }

    /**
     * Berechnet die Gr��e des Downsamplings des Bildes
     * ausgehend von den originalen Dimensionen
     *
     * @param options   the options
     * @param reqWidth  the req width
     * @param reqHeight the req height
     * @return the int
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            int heightRatio = Math.round((float) height / (float) reqHeight);
            int widthRatio = Math.round((float) width / (float) reqWidth);

            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    /**
     * Downsampling eines Bildes anhand Ursprungsbild
     * und angeforderter H�he und Weite
     *
     * @param res       the res
     * @param resId     the res id
     * @param reqWidth  the req width
     * @param reqHeight the req height
     * @return the bitmap
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true;
        options.inPurgeable = true;
        BitmapFactory.decodeResource(res, resId, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeResource(res, resId, options);
    }

    /**
     * Downsampling eines Bildes anhand Ursprungsbild
     * und angeforderter H�he und Weite
     *
     * @param filepath  Pfad zum Eingabebild
     * @param reqWidth  the req width
     * @param reqHeight the req height
     * @return the bitmap
     */
    public static Bitmap decodeSampledBitmapFromResource(String filepath, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true;
        options.inPurgeable = true;
        BitmapFactory.decodeFile(filepath, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filepath, options);
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();

        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);

        return resizedBitmap;
    }
}
