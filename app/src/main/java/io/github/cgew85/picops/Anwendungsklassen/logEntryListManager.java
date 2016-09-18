package io.github.cgew85.picops.Anwendungsklassen;

import android.util.Log;
import io.github.cgew85.picops.Grenzklassen.logEntry;

import java.util.ArrayList;
import java.util.List;

public class logEntryListManager {
    private static logEntryListManager instance = null;
    private static List<logEntry> logEntryList = new ArrayList<logEntry>();

    private logEntryListManager() {
    }

    public static logEntryListManager getInstance() {
        if (instance == null) {
            instance = new logEntryListManager();
        }

        return instance;
    }

    public void addLogEntry(logEntry entry) {
        logEntryList.add(entry);
        Log.d("INFO", "Objekt eingefuegt");
    }

    public void removeLastEntry() {
        logEntryList.remove(logEntryList.size() - 1);
        Log.d("INFO", "Objekt entfernt");
    }

    public int getNumberOfEntries() {
        return logEntryList.size();
    }

    public void clearList() {
        logEntryList.clear();
        Log.d("INFO", "Objektliste geleert");
    }

    public List<logEntry> getList() {
        return logEntryList;
    }
}
