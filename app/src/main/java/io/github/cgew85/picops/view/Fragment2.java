package io.github.cgew85.picops.view;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import io.github.cgew85.picops.R;
import io.github.cgew85.picops.controller.BitmapWorkerTask;
import io.github.cgew85.picops.controller.FilterController;
import io.github.cgew85.picops.controller.ImageScalingController;
import io.github.cgew85.picops.controller.ScaleImage;

public class Fragment2 extends Fragment {
    private static final int RESULT_LOAD_IMAGE = 2;

    private ImageView image1;
    private ImageView image2;
    boolean flag = false;
    private View rootView;
    private Context context;
    private int selection = 0;
    private Bitmap bitmapOut = null;
    private String DIRECTORY_PICTURES = "Pictures";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment2, container, false);
        setHasOptionsMenu(false);
        image1 = (ImageView) rootView.findViewById(R.id.img1);
        image2 = (ImageView) rootView.findViewById(R.id.img2);
        setHasOptionsMenu(true);

        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /** Bild 1 laden **/
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                selection = 1;
                startActivityForResult(i, RESULT_LOAD_IMAGE);
                Log.d("INFO", "Image 1 clicked");
            }
        });

        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /** Bild 2 laden **/
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                selection = 2;
                startActivityForResult(i, RESULT_LOAD_IMAGE);
                Log.d("INFO", "Image 2 clicked");
            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Auto-generated method stub

        menu.clear();
        menu.add("Save").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (bitmapOut == null) {
                    Toast.makeText(getActivity(), "No Image(s) selected", Toast.LENGTH_LONG).show();
                } else {
                    Log.d("INFO", "@onMenuItemClick -> Export");
                    OutputStream fos = null;
                    File outputFile = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES), bitmapOut.hashCode() + ".JPEG");
                    try {
                        fos = new FileOutputStream(outputFile);
                        BufferedOutputStream bos = new BufferedOutputStream(fos);
                        bitmapOut.compress(Bitmap.CompressFormat.JPEG, 100, bos);

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
                    Toast.makeText(getActivity(), "File saved", Toast.LENGTH_LONG).show();
                    image1.setImageBitmap(null);
                    image2.setImageBitmap(null);
                    Intent i = new Intent(getActivity(), SelectionActivity.class);
                    startActivity(i);
                }
                bitmapOut.recycle();
                bitmapOut = null;
                return false;
            }
        }).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        inflater.inflate(R.menu.pixel, menu);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent intent = getActivity().getIntent();
        Log.d("INFO", "@ MultiImageEditing -> Anfrage von: " + selection);
        Log.d("INFO", "@ MultiImageEditing -> onActivityResult");
        if (requestCode == RESULT_LOAD_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                /** Einf�gen in den ExternalStorage **/
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                Log.d("INFO", "Import into ExternalStorage");

                //TODO: Verzeichnis anlegen nach check auf ext/int
                String directory = Environment.getExternalStorageDirectory().toString();
                File directoryOnExternalDevice = new File(directory + "/picOps/");
                directoryOnExternalDevice.mkdirs();
                OutputStream fos = null;
                //File file = new File(directory,"/picOps/"+SettingsController.getReadWriteSettings().getStringSetting(this, "Session")+".JPEG");
                File file = new File(directory, "/picOps/img" + selection + ".JPEG");

                /** Bilder skalieren **/
                ImageScalingController mImageScalingController = new ImageScalingController();
                Bitmap bmp = mImageScalingController.checkImageSizeAndScale(picturePath);

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

                bmp.recycle();
                bmp = null;

                //TODO: Zentrieren des Previews umsetzen -> RelativeLayout
                /** ImagePreview anlegen und f�llen **/
                String filepath = Environment.getExternalStorageDirectory().toString().concat("/picOps/").concat("img" + selection + ".JPEG");
                File imgfile = new File(filepath);
                if ((selection == 1) && (file.exists() && (file.isFile()))) {
                    String[] params = {imgfile.getAbsolutePath(), "200", "200"};
                    loadBitmap(params, image1);
                }
                if ((selection == 2) && (file.exists() && (file.isFile()))) {
                    String[] params = {imgfile.getAbsolutePath(), "200", "200"};
                    loadBitmap(params, image2);
                }
                String file1 = Environment.getExternalStorageDirectory().toString().concat("/picOps/").concat("img" + 1 + ".JPEG");
                String file2 = Environment.getExternalStorageDirectory().toString().concat("/picOps/").concat("img" + 2 + ".JPEG");
                File imgFile1 = new File(file1);
                File imgFile2 = new File(file2);
                if ((imgFile1.exists()) && (imgFile1.isFile()) && (imgFile2.exists()) && (imgFile2.isFile())) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    Bitmap bitmap1 = null;
                    Bitmap bitmap2 = null;
                    for (options.inSampleSize = 1; options.inSampleSize < 32; options.inSampleSize++) {
                        try {
                            bitmap1 = BitmapFactory.decodeFile(imgFile1.getAbsolutePath(), options);
                            bitmap2 = BitmapFactory.decodeFile(imgFile2.getAbsolutePath(), options);
                            break;
                        } catch (OutOfMemoryError e) {
                            //...
                        }
                    }
                    bitmapOut = FilterController.blend2Images(bitmap1, bitmap2);
                    ImageView iv = (ImageView) rootView.findViewById(R.id.combinedImages);
                    iv.setImageBitmap(ScaleImage.getResizedBitmap(bitmapOut, 400, 400));
                    bitmap1.recycle();
                    bitmap2.recycle();
                    imgFile1.delete();
                    imgFile2.delete();
                }
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void loadBitmap(String[] params, ImageView iv) {
        BitmapWorkerTask task = new BitmapWorkerTask(iv);
        task.execute(params);
    }
}
