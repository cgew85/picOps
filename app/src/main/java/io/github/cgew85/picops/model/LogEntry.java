package io.github.cgew85.picops.model;

public class LogEntry {
    private String name;
    private String values;

    public LogEntry() {
    }

    public LogEntry(String name, String values) {
        this.name = name;
        this.values = values;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValues() {
        return values;
    }

    public void setValues(String values) {
        this.values = values;
    }


}
