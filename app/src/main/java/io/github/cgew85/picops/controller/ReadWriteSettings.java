package io.github.cgew85.picops.controller;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * The Class ReadWriteSettings
 * <p>
 * Anlegen oder Ab�ndern appinterner Einstellungen.
 */
public class ReadWriteSettings {
    /**
     * The Constant SETTINGS.
     */
    public static final String SETTINGS = "LocalSettingsFile";

    /**
     * The settings.
     */
    private static SharedPreferences settings;

    /**
     * The editor.
     */
    private SharedPreferences.Editor editor;

    /**
     * The rw settings.
     */
    private static ReadWriteSettings rwSettings = null;

    /**
     * Instantiates a new read write settings.
     */
    private ReadWriteSettings() {
    }

    /**
     * Gets the rW settings (Singleton-Pattern).
     *
     * @return the rW settings
     */
    public static synchronized ReadWriteSettings getRWSettings() {
        if (rwSettings == null) {
            rwSettings = new ReadWriteSettings();
        }

        return rwSettings;
    }

    /**
     * Adds String setting, but checks if the setting isn't present already.
     *
     * @param context the context
     * @param key     the key
     * @param value   the value
     * @return true, if successful
     */
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

    /**
     * Adds boolean setting, but checks if the setting isn't present already.
     *
     * @param context the context
     * @param key     the key
     * @param value   the value
     * @return true, if successful
     */
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

    /**
     * Gets a String setting.
     *
     * @param context the context
     * @param key     the key
     * @return the string setting
     */
    public String getStringSetting(Context context, String key) {
        String retValue = "";

        settings = context.getSharedPreferences(SETTINGS, 0);
        retValue = settings.getString(key, "err");

        return retValue;
    }

    /**
     * Check if setting already exists.
     *
     * @param context the context
     * @param key     the key
     * @return true, falls schon vorhanden
     */
    public boolean checkIfSettingAlreadyExists(Context context, String key) {
        boolean rueckgabe = false;

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
     * @return true, if successful
     */
    public void changeSetting(Context context, String key, String value) {
        settings = context.getSharedPreferences(SETTINGS, 0);
        if (settings.contains(key)) {
            editor = settings.edit();
            /** Entfernen des vorigen Key/Value-Paares **/
            editor.remove(key);
            editor.commit();

            /** Anlegen eines neuen Key/Value-Paares **/
            editor.putString(key, value);
            editor.commit();
        } else {
            addSetting(context, key, value);
        }
    }

}
