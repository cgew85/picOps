package io.github.cgew85.picops.controller;

import android.os.Environment;

import java.io.File;

public class CleanStartUp {
    public CleanStartUp() {
    }

    public void cleanUpOnStart() {
        String path = Environment.getExternalStorageDirectory() + "/picOps/";
        File directory = new File(path);

        if (directory.listFiles() != null) {
            for (File file : directory.listFiles()) {
                if (!(file.getName().equals(".nomedia"))) {
                    file.delete();
                }

            }
        }
    }
}
