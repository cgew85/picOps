package io.github.cgew85.picops.controller;

import android.os.Environment;

import java.io.File;

public class StartupController {

    private static final String DIRECTORY_PIC_OPS = "/picOps/";
    private static final String NO_MEDIA = ".nomedia";

    public boolean cleanUpOnStart() {
        final String path = Environment.getExternalStorageDirectory() + DIRECTORY_PIC_OPS;
        File directory = new File(path);

        if (directory.listFiles() != null) {
            for (File file : directory.listFiles()) {
                if (!(file.getName().equals(NO_MEDIA))) {
                    return file.delete();
                }
            }
        }

        return false;
    }
}
