package io.github.cgew85.picops.model;

import android.util.DisplayMetrics;
import android.view.Display;

/**
 * Save device information
 */
public class Device {

    private int deviceDisplayWidth = 0;
    private int deviceDisplayHeight = 0;

    public Device(Display display) {
        setDeviceDisplayDimensions(display);
    }

    public int getDeviceDisplayWidth() {
        return deviceDisplayWidth;
    }

    public int getDeviceDisplayHeight() {
        return deviceDisplayHeight;
    }

    private void setDeviceDisplayDimensions(Display display) {
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        deviceDisplayWidth = metrics.widthPixels;
        deviceDisplayHeight = metrics.heightPixels;
    }


}
