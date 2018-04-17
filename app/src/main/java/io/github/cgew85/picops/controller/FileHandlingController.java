package io.github.cgew85.picops.controller;

import android.content.Context;
import android.os.Environment;

import java.io.File;

public class FileHandlingController {
    private static final String DIRECTORY_PIC_OPS = "/picOps/";
    private static final String EMPTY_STRING = "";
    private static FileHandlingController instance = null;

    private FileHandlingController() {
    }

    public static FileHandlingController getInstance() {
        if (instance == null) instance = new FileHandlingController();
        return instance;
    }

    public String returnAbsoluteFilePath(Context context) {
        String path = EMPTY_STRING;

        // Aiming for a directory in your ExternalStorage, in this case /picOps/
        File directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath().concat(DIRECTORY_PIC_OPS));
        // The file you want the file path from
        File file;
        // Check if the directory exists at all
        if (directory.isDirectory()) {
            // Put all filenames into an array
            String[] filenames = directory.list();
            // Safety check
            if (filenames != null) {
                if (filenames.length > 0) {
                    // Browsing the array for the filename we are looking for
                    for (String filename : filenames) {
                        if (filename.equals(SettingsController.getReadWriteSettings(context).getStringSetting("Session") + ".JPEG")) {
                            // Create a file object in order to get the absolute path
                            file = new File(directory.getAbsolutePath().concat("/").concat(filename));
                            // Another security check
                            if (file.exists()) {
                                // Getting the absolute file path
                                path = file.getAbsolutePath();
                            }
                        }
                    }
                }
            }
        }
        return path;
    }

    public String returnAbsoluteFilePathWorkingCopy(Context context, int counter) {
        String path = EMPTY_STRING;

        // Aiming for a directory in your ExternalStorage, in this case /picOps/
        File directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath().concat(DIRECTORY_PIC_OPS));
        // The file you want the file path from
        File file;
        // Check if the directory exists at all
        if (directory.isDirectory()) {
            // Put all filenames into an array
            String[] filenames = directory.list();
            // Safety check
            if (filenames.length > 0 && !filenames.equals(null)) {
                // Browsing the array for the filename we are looking for
                for (String filename : filenames) {
                    if (filename.equals(SettingsController.getReadWriteSettings(context).getStringSetting("Session") + "-" + counter + ".JPEG")) {
                        // Create a file object in order to get the absolute path
                        file = new File(directory.getAbsolutePath().concat("/").concat(filename));
                        // Another security check
                        if (file.exists()) {
                            // Getting the absolute file path
                            path = file.getAbsolutePath();
                        }
                    }
                }
            }
        }
        return path;
    }
}
