package io.github.cgew85.picops.controller;

import android.util.Log;
import io.github.cgew85.picops.model.LogEntry;

import java.util.ArrayList;
import java.util.List;

public class LogEntryListManager {
    private static LogEntryListManager instance = null;
    private static List<LogEntry> logEntryList = new ArrayList<>();

    public static LogEntryListManager getInstance() {
        if (instance == null) instance = new LogEntryListManager();
        return instance;
    }

    public void addLogEntry(LogEntry entry) {
        logEntryList.add(entry);
        Log.d("INFO", "Object added");
    }

    public void removeLastEntry() {
        logEntryList.remove(logEntryList.size() - 1);
        Log.d("INFO", "Object removed");
    }

    public int getNumberOfEntries() {
        return logEntryList.size();
    }

    public void clearList() {
        logEntryList.clear();
        Log.d("INFO", "Object list cleared");
    }

    public List<LogEntry> getList() {
        return logEntryList;
    }
}
