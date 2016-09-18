package io.github.cgew85.picops.Anwendungsklassen;

/**
 * The Class simpleCounterForTempFileName.
 */
public class simpleCounterForTempFileName {

    /**
     * The instance.
     */
    private static simpleCounterForTempFileName instance = null;

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
        simpleCounterForTempFileName.counter = counter;
    }

    /**
     * Instantiates a new simple counter for temp file name.
     */
    private simpleCounterForTempFileName() {
    }

    /**
     * Gets the single instance of simpleCounterForTempFileName.
     *
     * @return single instance of simpleCounterForTempFileName
     */
    public static simpleCounterForTempFileName getInstance() {
        if (instance == null)
            instance = new simpleCounterForTempFileName();

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
