package io.github.cgew85.picops.controller;

public class SimpleCounterForTempFileName {

    private static SimpleCounterForTempFileName instance = null;
    private static int counter = 0;

    public static int getCounter() {
        return counter;
    }

    public static void setCounter(int counter) {
        SimpleCounterForTempFileName.counter = counter;
    }

    public static SimpleCounterForTempFileName getInstance() {
        if (instance == null)
            instance = new SimpleCounterForTempFileName();

        return instance;
    }

    public static void increaseCounter() {
        counter += 1;
    }

    public static void decreaseCounter() {
        counter -= 1;
    }
}
