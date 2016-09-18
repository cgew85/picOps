package io.github.cgew85.picops.controller;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

// This class ensures that the image is loaded outside the ui thread
public class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
    private final WeakReference<ImageView> imageViewReference;

    public BitmapWorkerTask(ImageView imageView) {
        // The ImageView that needs to be set up
        imageViewReference = new WeakReference<>(imageView);
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        // String contains file path
        String data = params[0];
        int imgWidth = Integer.parseInt(params[1]);
        int imgHeight = Integer.parseInt(params[2]);

        return ScaleImage.decodeSampledBitmapFromResource(data, imgWidth, imgHeight);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}