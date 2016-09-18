package io.github.cgew85.picops.controller;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;

public class UndoLastStep {

    private ReadWriteSettings rwSettings = ReadWriteSettings.getReadWriteSettings();
    private SimpleCounterForTempFileName simpleCounter = SimpleCounterForTempFileName.getInstance();

    public boolean undo(int counter, Context context) {
        boolean success = false;
        String filename = rwSettings.getStringSetting(context, "Session").concat("-" + (counter - 1));
        Log.d("INFO", "Filename to delete: " + filename);
        File directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath().concat("/picOps/"));
        if (directory.exists() && directory.isDirectory()) {
            Log.d("INFO", "undo @ directory");
            String[] allFileNamesInDirectory = directory.list();
            for (String fileName : allFileNamesInDirectory) {
                Log.d("INFO", "undo @ for loop");
                if (fileName.contains(filename)) {
                    Log.d("INFO", "undo @ file found");
                    File fileToDelete = new File(directory + "/" + filename + ".JPEG");
                    Log.d("INFO", "File to delete: " + fileToDelete.getAbsolutePath());
                    success = fileToDelete.delete();
                    if (success) {
                        simpleCounter.decreaseCounter();
                        Log.d("INFO", "Counter decreased");
                    }
                }
            }
        }

        return success;
    }

}
