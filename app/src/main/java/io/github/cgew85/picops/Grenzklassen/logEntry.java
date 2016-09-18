package io.github.cgew85.picops.Grenzklassen;

public class logEntry {
    private String name;
    private String values;

    public logEntry() {
    }

    public logEntry(String name, String values) {
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
