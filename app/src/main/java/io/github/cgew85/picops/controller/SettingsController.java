package io.github.cgew85.picops.controller;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Create and edit application settings
 */
public class SettingsController {
    private static final String SETTINGS = "LocalSettingsFile";
    private static SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private static SettingsController settingsController = null;
    private Context context;

    public static SettingsController getReadWriteSettings(Context context) {
        if (settingsController == null) {
            settingsController = new SettingsController(context);
        }

        return settingsController;
    }

    private SettingsController(Context context) {
        this.context = context;
    }

    public boolean addSetting(String key, String value) {
        boolean status = false;

        settings = context.getSharedPreferences(SETTINGS, 0);
        if (!settings.contains(key)) {
            editor = settings.edit();
            editor.putString(key, value);
            status = editor.commit();
        }

        return status;
    }

    public boolean addSetting(String key, boolean value) {
        boolean status = false;

        settings = context.getSharedPreferences(SETTINGS, 0);
        if (!settings.contains(key)) {
            editor = settings.edit();
            editor.putBoolean(key, value);
            status = editor.commit();
        }

        return status;
    }

    public String getStringSetting(String key) {
        String retValue;

        settings = context.getSharedPreferences(SETTINGS, 0);
        retValue = settings.getString(key, "err");

        return retValue;
    }

    public boolean checkIfSettingAlreadyExists(String key) {
        boolean rueckgabe;

        settings = context.getSharedPreferences(SETTINGS, 0);
        rueckgabe = settings.contains(key);

        return rueckgabe;
    }

    /**
     * Change setting safely, removing it first and putting it back in
     * with a new value. Needed for session creation.
     *
     * @param key   the key
     * @param value the value
     */
    public void changeSetting(String key, String value) {
        settings = context.getSharedPreferences(SETTINGS, 0);
        if (settings.contains(key)) {
            editor = settings.edit();
            // Entfernen des vorigen Key/Value-Paares
            editor.remove(key);
            editor.apply();

            // Anlegen eines neuen Key/Value-Paares
            editor.putString(key, value);
            editor.apply();
        } else {
            addSetting(key, value);
        }
    }

}
