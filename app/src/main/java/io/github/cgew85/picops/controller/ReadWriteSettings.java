package io.github.cgew85.picops.controller;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Create and edit application settings
 */
public class ReadWriteSettings {
    private static final String SETTINGS = "LocalSettingsFile";
    private static SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private static ReadWriteSettings readWriteSettings = null;

    public static synchronized ReadWriteSettings getReadWriteSettings() {
        if (readWriteSettings == null) {
            readWriteSettings = new ReadWriteSettings();
        }

        return readWriteSettings;
    }

    public boolean addSetting(Context context, String key, String value) {
        boolean status = false;

        settings = context.getSharedPreferences(SETTINGS, 0);
        if (!settings.contains(key)) {
            editor = settings.edit();
            editor.putString(key, value);
            status = editor.commit();
        }

        return status;
    }

    public boolean addSetting(Context context, String key, boolean value) {
        boolean status = false;

        settings = context.getSharedPreferences(SETTINGS, 0);
        if (!settings.contains(key)) {
            editor = settings.edit();
            editor.putBoolean(key, value);
            status = editor.commit();
        }

        return status;
    }

    public String getStringSetting(Context context, String key) {
        String retValue;

        settings = context.getSharedPreferences(SETTINGS, 0);
        retValue = settings.getString(key, "err");

        return retValue;
    }

    public boolean checkIfSettingAlreadyExists(Context context, String key) {
        boolean rueckgabe;

        settings = context.getSharedPreferences(SETTINGS, 0);
        rueckgabe = settings.contains(key);

        return rueckgabe;
    }

    /**
     * Change setting safely, removing it first and putting it back in
     * with a new value. Needed for session creation.
     *
     * @param context the context
     * @param key     the key
     * @param value   the value
     */
    public void changeSetting(Context context, String key, String value) {
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
            addSetting(context, key, value);
        }
    }

}
