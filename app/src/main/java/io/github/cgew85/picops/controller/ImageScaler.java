package io.github.cgew85.picops.controller;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ImageScaler {

    /**
     * Check image size and scale if size exceeds limit.
     *
     * @param picturePath the picture path
     * @return the bitmap
     */
    public Bitmap checkImageSizeAndScale(String picturePath) {
        final int MAX_IMAGE_SIZE = 1024;

        File imgFile = new File(picturePath);
        FileInputStream inStream;
        int scaleSize = 1;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        try {
            inStream = new FileInputStream(imgFile);
            BitmapFactory.decodeStream(inStream, null, options);
            inStream.close();
        } catch (IOException e) {
            //..
        }

        Log.d("INFO", "@checkImageSizeAndScale -> Old Size: width: " + options.outWidth + " height: " + options.outHeight);

        //if((options.outWidth > MAX_IMAGE_SIZE)||(options.outHeight > MAX_IMAGE_SIZE))
        while (options.outWidth / scaleSize >= MAX_IMAGE_SIZE || options.outHeight / scaleSize >= MAX_IMAGE_SIZE) {
            scaleSize *= 2;
            //final int heightRatio = Math.round((float)options.outWidth/(float)MAX_IMAGE_SIZE);
            //final int widthRatio = Math.round((float)options.outHeight/(float)MAX_IMAGE_SIZE);
            //scaleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = scaleSize;
        Bitmap bitmapOut = null;

        try {
            inStream = new FileInputStream(imgFile);
            bitmapOut = BitmapFactory.decodeStream(inStream, null, options);
            inStream.close();
        } catch (IOException e) {
            //...
        }

//        Log.d("INFO", "@checkImageSizeAndScale -> New Size: width: " + bitmapOut.getWidth() + " height: " + bitmapOut.getHeight());
        return bitmapOut;
    }
}
