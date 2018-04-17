package io.github.cgew85.picops.controller;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.File;

public class StorageDetailsController {

    public static final String EXTERNAL_STORAGE = "ext";
    public static final String INTERNAL_STORAGE = "int";
    public static final String ERROR = "err";
    private static final String EMPTY_STRING = "";

    /**
     * Checks which storage should be used.
     * @return String with either int,ext or err
     */
    public String intOrExtStorage() {
        String returnValue = EMPTY_STRING;

        // Check for external storage
        boolean externalStorageAvailable;
        String state = Environment.getExternalStorageState();

        externalStorageAvailable = Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
        // End of checking

        long availableInternalStorage;
        long availableExternalStorage = 0;

        String availableExternalStorageOutput = EMPTY_STRING;
        String availableInternalStorageOutput;

        if (externalStorageAvailable) {
            availableExternalStorage = getAvailableExternalMemorySize();
            availableExternalStorageOutput = formatSize(availableExternalStorage);
        }

        availableInternalStorage = getAvailableInternalMemorySize();
        availableInternalStorageOutput = formatSize(availableInternalStorage);

        Log.d("INFO", "Storage (internal): " + availableInternalStorageOutput);
        Log.d("INFO", "Storage (external): " + availableExternalStorageOutput);

        if (availableInternalStorage > availableExternalStorage) {
            returnValue = INTERNAL_STORAGE;
        } else if (availableExternalStorage > availableInternalStorage) {
            returnValue = EXTERNAL_STORAGE;
        } else if (availableExternalStorage == availableInternalStorage) {
            if (availableInternalStorage == 0) {
                returnValue = ERROR;
            }
        }

        return returnValue;
    }

    /**
     * Reads empty internal storage size.
     *
     * @return the available internal memory size
     */
    private long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();

        return availableBlocks * blockSize;
    }

    /**
     * Reads empty external storage size.
     *
     * @return the available external memory size
     */
    private long getAvailableExternalMemorySize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();

        return totalBlocks * blockSize;
    }

    /**
     * Format output of the above methods
     *
     * @param size the size
     * @return Stringobjekt mit Gr��enangabe
     */
    private String formatSize(long size) {
        String suffix = null;

        if (size >= 1024) {
            suffix = "KiB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MiB";
                size /= 1024;
            }
        }
        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));
        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }

        if (suffix != null) resultBuffer.append(suffix);

        return resultBuffer.toString();
    }

}
