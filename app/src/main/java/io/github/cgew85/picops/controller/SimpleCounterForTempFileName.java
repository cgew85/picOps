package io.github.cgew85.picops.controller;

/**
 * The Class SimpleCounterForTempFileName.
 */
public class SimpleCounterForTempFileName {

    /**
     * The instance.
     */
    private static SimpleCounterForTempFileName instance = null;

    /**
     * The counter.
     */
    private static int counter = 0;

    /**
     * Gets the counter.
     *
     * @return the counter
     */
    public static int getCounter() {
        return counter;
    }

    /**
     * Sets the counter.
     *
     * @param counter the new counter
     */
    public static void setCounter(int counter) {
        SimpleCounterForTempFileName.counter = counter;
    }

    /**
     * Instantiates a new simple counter for temp file name.
     */
    private SimpleCounterForTempFileName() {
    }

    /**
     * Gets the single instance of SimpleCounterForTempFileName.
     *
     * @return single instance of SimpleCounterForTempFileName
     */
    public static SimpleCounterForTempFileName getInstance() {
        if (instance == null)
            instance = new SimpleCounterForTempFileName();

        return instance;
    }

    /**
     * Increase counter.
     */
    public static void increaseCounter() {
        counter += 1;
    }

    /**
     * Decrease counter.
     */
    public static void decreaseCounter() {
        counter -= 1;
    }
}
