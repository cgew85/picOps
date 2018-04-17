package io.github.cgew85.picops.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.widget.Button;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import io.github.cgew85.picops.R;
import io.github.cgew85.picops.controller.SettingsController;
import io.github.cgew85.picops.controller.StartupController;
import io.github.cgew85.picops.controller.StorageDetailsController;
import io.github.cgew85.picops.model.Session;
import spork.Spork;
import spork.android.BindClick;
import spork.android.BindLayout;
import spork.android.BindView;
import spork.inject.Lazy;

@BindLayout(R.layout.activity_main)
public class MainActivity extends Activity {

    @BindView(R.id.buttonLoad)
    private Button buttonStart;

    @Inject
    private Lazy<StartupController> startupController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Spork.bind(this);

        // In case app is supposed to exit, restart starts here
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }

        // Get session
        Session session = Session.getSession();

        // Create SharedPreferences object
        SettingsController settings = SettingsController.getReadWriteSettings(this);

        // Save session in shared preferences
        // Settings for session are completely erased and rewritten
        if (settings.checkIfSettingAlreadyExists("Session")) {
            Log.d("INFO", "Session SP exists");
        }
        settings.changeSetting("Session", String.valueOf(session.getSessionID()));
        Log.d("INFO", "Session: " + String.valueOf(session.getSessionID()));
        Log.d("INFO", "Session(SP): +" + settings.getStringSetting("Session"));

        // Check for first run
        boolean check = settings.checkIfSettingAlreadyExists("First Run");
        if (!check) {
            //TODO: Take care of first run
            //TODO: check for picOps folder
            // In case of first run act accordingly
            settings.addSetting("First Run", true);
        }

        // Check for appropriate save destination
        StorageDetailsController storageDetailsController = new StorageDetailsController();
        String storageChoice = storageDetailsController.intOrExtStorage();
        Log.d("INFO", "Selection of save destination: " + storageChoice);

        // Create settings
        if (storageChoice.equals(StorageDetailsController.INTERNAL_STORAGE)) {
            settings.addSetting("Speicherort", "int");
            startInternalStorageSetup();
        } else if (storageChoice.equals(StorageDetailsController.EXTERNAL_STORAGE)) {
            settings.addSetting("Speicherort", "ext");
            startExternalStorageSetup();
        }
    }

    private void startInternalStorageSetup() {
        // Delete temporary images from earlier runs
        new Thread(new Runnable() {
            @Override
            public void run() {
                String directoryName = getFilesDir() + "/picOps/";
                File file = getDir(directoryName, 0);
                if (file.isDirectory() && file.exists()) {
                    for (File fileItem : file.listFiles()) {
                        if (!(fileItem.getName().equals(".nomedia"))) {
                            deleteFile(fileItem.getAbsolutePath());
                        }

                    }
                }
                File nomedia = new File(file, ".nomedia");
                if (!(file.exists() && file.isFile())) {
                    createNoMediaFile(nomedia);
                }
            }
        }).start();
    }

    private void startExternalStorageSetup() {
        // Delete temporary images from earlier runs
        new Thread(new Runnable() {
            @Override
            public void run() {
                String directory = Environment.getExternalStorageDirectory() + "/picOps/";
                File file = new File(directory);
                if (file.exists() && file.isDirectory()) {
                    startupController.get().cleanUpOnStart();
                    File nomedia = new File(directory, ".nomedia");
                    if (nomedia.isFile() && nomedia.exists()) {
                        Log.d("INFO", ".nomedia file is present");
                    } else {
                        createNoMediaFile(nomedia);
                    }
                } else {
                    file.mkdir();
                    File nomedia = new File(directory, ".nomedia");
                    if (!(file.exists() && file.isFile())) {
                        createNoMediaFile(nomedia);
                    }
                }
            }
        }).start();
    }

    private void createNoMediaFile(final File nomedia) {
        try {
            nomedia.createNewFile();
            Log.d("INFO", "Creating .nomedia file");
        } catch (IOException e) {
            Log.d("INFO", "Could not create .nomedia file");
        }
    }

    @BindClick(R.id.buttonLoad)
    private void start(final Button button) {
        buttonStart.setBackgroundResource(R.drawable.buttononclick);
        buttonStart.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

        Intent intent = new Intent(button.getContext(), SelectionActivity.class);
        // Write first run setting
        SettingsController.getReadWriteSettings(this)
                .addSetting("First Run", false);

        button.getContext().startActivity(intent);
    }
}