package io.github.cgew85.picops;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import io.github.cgew85.picops.Anwendungsklassen.cleanStartUp;
import io.github.cgew85.picops.Anwendungsklassen.readWriteSettings;
import io.github.cgew85.picops.Anwendungsklassen.storageDetails;
import io.github.cgew85.picops.Grenzklassen.Session;

import java.io.File;
import java.io.IOException;

/**
 * The Class MainActivity.
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /** Falls Applikation verlassen werden soll. Neustart beginnt dann wieder hier. **/
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
        /** Ausblenden der Action- und der Statusbar **/
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        Log.d("INFO", "<--- Programmstart --->");

        /** Session holen **/
        Session session = Session.getSession();

        /** SharedPreferences Objekt anlegen **/
        readWriteSettings settings = readWriteSettings.getRWSettings();

        /** Session in SharedPreferences speichern **/
        /** Einstellung f�r Session wird komplett gel�scht und neu erstellt **/
        if (settings.checkIfSettingAlreadyExists(this, "Session")) {
            Log.d("INFO", "Session SP existiert");
        }
        settings.changeSetting(this, "Session", String.valueOf(session.getSessionID()));
        Log.d("INFO", "Session: " + String.valueOf(session.getSessionID()));
        Log.d("INFO", "Session(SP): +" + settings.getStringSetting(this, "Session"));

        /** Check auf First Run **/
        boolean check = settings.checkIfSettingAlreadyExists(this, "First Run");
        if (check == false) {
            //TODO: First run behandeln
            //TODO: Check auf picOps Verzeichnis
            /** Falls firstRun dann entsprechend handeln **/
            settings.addSetting(this, "First Run", true);
        }

        /** Listener f�r Button anlegen **/
        Button buttonLoad = (Button) findViewById(R.id.buttonLoad);
        btnListener listener = new btnListener();
        buttonLoad.setOnClickListener(listener);

        /** Auswahl eines geeigneten Speicherplatzes **/
        storageDetails storageDetails = new storageDetails();
        String storageChoice = storageDetails.intOrExtStorage();
        Log.d("INFO", "Auswahl des Speichers: " + storageChoice);

        /** Settings anlegen **/
        /** Settings f�r Speichernutzung **/
        if (storageChoice.equals("int")) {
            settings.addSetting(this, "Speicherort", "int");
            /** L�schen vorheriger temp. Bilder **/
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
                        try {
                            nomedia.createNewFile();
                            Log.d("INFO", "Creating .nomedia file");
                        } catch (IOException e) {
                            Log.d("INFO", "Could not create .nomedia file");
                        }
                    }
                }
            }).start();
        } else if (storageChoice.equals("ext")) {
            settings.addSetting(this, "Speicherort", "ext");
            /** L�schen vorheriger temp. Bilder **/
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String directory = Environment.getExternalStorageDirectory() + "/picOps/";
                    File file = new File(directory);
                    if (file.exists() && file.isDirectory()) {
                        cleanStartUp cleanDirectory = new cleanStartUp();
                        cleanDirectory.cleanUpOnStart();
                        File nomedia = new File(directory, ".nomedia");
                        if (nomedia.isFile() && nomedia.exists()) {
                            Log.d("INFO", ".nomedia file is present");
                        } else {
                            try {
                                nomedia.createNewFile();
                                Log.d("INFO", ".nomedia file created");
                            } catch (IOException e) {
                                Log.d("INFO", ".nomedia file could not be created");
                            }
                        }
                    } else {
                        file.mkdir();
                        File nomedia = new File(directory, ".nomedia");
                        if (!(file.exists() && file.isFile())) {
                            try {
                                nomedia.createNewFile();
                                Log.d("INFO", "Creating .nomedia file");
                            } catch (IOException e) {
                                Log.d("INFO", "Could not create .nomedia file");
                            }
                        }
                    }
                }
            }).start();
        }
    }
}

/**
 * Subklasse onClickListener
 **/
class btnListener implements OnClickListener {
    /**
     * Start der eigentlichen App
     **/
    public void onClick(final View v) {
        Button startButton = (Button) v.findViewById(R.id.buttonLoad);
        startButton.setBackgroundResource(R.drawable.buttononclick);
        startButton.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

        Intent intent = new Intent(v.getContext(), AuswahlActivity.class);
        readWriteSettings rws = readWriteSettings.getRWSettings();

        /** First Run Setting schreiben **/
        rws.addSetting(v.getContext(), "First Run", false);

        v.getContext().startActivity(intent);
    }
}
