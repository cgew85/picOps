package io.github.cgew85.picops.Grenzklassen;

import android.util.DisplayMetrics;
import android.view.Display;

/**
 * The Class Device
 * <p>
 * Speichern ger�tebezogener Daten
 */
public class Device {

    /**
     * The device display width.
     */
    private int deviceDisplayWidth = 0;

    /**
     * The device display height.
     */
    private int deviceDisplayHeight = 0;

    /**
     * Instantiates a new device.
     */
    public Device(Display display) {
        setDeviceDisplayDimensions(display);
    }

    /**
     * Gets the device display width.
     *
     * @return the device display width
     */
    public int getDeviceDisplayWidth() {
        return deviceDisplayWidth;
    }

    /**
     * Gets the device display height.
     *
     * @return the device display height
     */
    public int getDeviceDisplayHeight() {
        return deviceDisplayHeight;
    }

    /**
     * Sets the device display dimensions.
     *
     * @param display the device display
     */
    public void setDeviceDisplayDimensions(Display display) {
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        deviceDisplayWidth = metrics.widthPixels;
        deviceDisplayHeight = metrics.heightPixels;
    }


}
