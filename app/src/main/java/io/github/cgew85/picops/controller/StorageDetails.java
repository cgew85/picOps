package io.github.cgew85.picops.controller;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.File;


/**
 * Dient zum W�hlen eines geeigneten Speichers
 * f�r den Arbeitsprozess.
 * <p>
 * Pr�ft, ob externer Speicher vorhanden ist und
 * <p>
 * �berpr�ft dann, welcher Speicher gr��er ist.
 * <p>
 * R�ckgabe erfolgt als String, mit entweder
 * "ext","int", oder "err" als m�gliche
 * R�ckgaben.
 */

public class StorageDetails {

    /**
     * Instantiates a new storage details.
     */
    public StorageDetails() {
    }


    /**
     * Pr�ft, welcher Speicher vorhanden ist und
     * benutzt werden soll. Default ist int.
     * <p>
     * Int or ext storage.
     *
     * @return String mit entweder int,ext oder err
     */
    public String intOrExtStorage() {
        String rueckgabe = "";

        // Check, ob external storage vorhanden ist
        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
        // Check Ende

        long availableInternalStorage = 0;
        long availableExternalStorage = 0;

        String availableExternalStorageOutput = "";
        String availableInternalStorageOutput = "";

        if (mExternalStorageAvailable = true) {
            availableExternalStorage = getAvailableExternalMemorySize();
            availableExternalStorageOutput = formatSize(availableExternalStorage);
        }

        availableInternalStorage = getAvailableInternalMemorySize();
        availableInternalStorageOutput = formatSize(availableInternalStorage);

        Log.d("INFO", "Speicher (intern): " + availableInternalStorageOutput);
        Log.d("INFO", "Speicher (extern): " + availableExternalStorageOutput);

        if (availableInternalStorage > availableExternalStorage) {
            rueckgabe = "int";
        } else if (availableExternalStorage > availableInternalStorage) {
            rueckgabe = "ext";
        } else if (availableExternalStorage == availableInternalStorage) {
            if (availableInternalStorage == 0 && availableExternalStorage == 0) {
                rueckgabe = "err";
            }
        }

        return rueckgabe;
    }

    /**
     * Gibt vorhandenen freien internen Speicher aus.
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
     * Gibt vorhandenen freien externen Speicher aus.
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
     * Formatierung der Ergebnisse von oben.
     *
     * @param size the size
     * @return Stringobjekt mit Gr��enangabe
     */
    private static String formatSize(long size) {
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

        if (suffix != null) {
            resultBuffer.append(suffix);
        }

        return resultBuffer.toString();
    }

}
