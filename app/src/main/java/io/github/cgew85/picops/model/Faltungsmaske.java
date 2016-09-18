package io.github.cgew85.picops.model;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

// TODO: Increase performance

/**
 * The Class Faltungsmaske.
 */
public class Faltungsmaske {

    private static final int SIZE = 3;
    private static final int SIZEBIG = 5;

    public double[][] mask;

    public double factor = 1;

    public double offset = 1;

    /**
     * Instantiates a new faltungsmaske.
     *
     * @param size the size
     */
    public Faltungsmaske(int size) {
        mask = new double[size][size];
    }

    /**
     * Sets the all.
     *
     * @param value the new all
     */
    public void setAll(double value) {
        for (int x = 0; x < SIZE; ++x) {
            for (int y = 0; y < SIZE; ++y) {
                mask[x][y] = value;
            }
        }
    }

    /**
     * Apply faltungskonfiguration.
     *
     * @param config the config
     */
    public void applyFaltungskonfiguration(double[][] config) {
        for (int x = 0; x < SIZE; ++x) {
            for (int y = 0; y < SIZE; ++y) {
                mask[x][y] = config[x][y];
            }
        }
    }

    /**
     * Apply faltungskonfiguration.
     *
     * @param config the config
     */
    public void applyFaltungskonfigurationBig(double[][] config) {
        for (int x = 0; x < SIZEBIG; ++x) {
            for (int y = 0; y < SIZEBIG; ++y) {
                mask[x][y] = config[x][y];
            }
        }
    }

    /**
     * Apply faltungskonfiguration.
     *
     * @param config the config
     * @param size   masks size
     */
    public void applyFaltungskonfigurationBig(double[][] config, int size) {
        for (int x = 0; x < size; ++x) {
            for (int y = 0; y < size; ++y) {
                mask[x][y] = config[x][y];
            }
        }
    }

    /**
     * Berechnet Faltung einer 3x3-Faltungsmaske.
     *
     * @param bitmapIn      Eingangsbitmap
     * @param faltungsmaske Faltungsmaske
     * @return Ausgabebitmap
     */
    public static Bitmap berechneFaltung3x3(Bitmap bitmapIn, Faltungsmaske faltungsmaske) {
        int width = bitmapIn.getWidth();
        int height = bitmapIn.getHeight();
        Bitmap bitmapOut = Bitmap.createBitmap(width, height, bitmapIn.getConfig());

        int A, R, G, B;
        int sumR, sumG, sumB;
        int[][] pixels = new int[SIZE][SIZE];

        // im Bitmap
        for (int y = 0; y < height - 2; ++y) {
            for (int x = 0; x < width - 2; ++x) {
                // in der mask
                for (int i = 0; i < SIZE; ++i) {
                    for (int j = 0; j < SIZE; ++j) {
                        pixels[i][j] = bitmapIn.getPixel(x + i, y + j);
                    }
                }
                // Alpha des mittleren Pixels abrufen
                A = Color.alpha(pixels[1][1]);
                sumR = sumG = sumB = 0;
                for (int i = 0; i < SIZE; ++i) {
                    for (int j = 0; j < SIZE; ++j) {
                        sumR += (Color.red(pixels[i][j]) * faltungsmaske.mask[i][j]);
                        sumG += (Color.green(pixels[i][j]) * faltungsmaske.mask[i][j]);
                        sumB += (Color.blue(pixels[i][j]) * faltungsmaske.mask[i][j]);
                    }
                }

                // Endwerte der jeweiligen Farbwerte festlegen (R,G,B)
                R = (int) (sumR / faltungsmaske.factor + faltungsmaske.offset);
                if (R < 0) R = 0;
                else if (R > 255) R = 255;

                G = (int) (sumG / faltungsmaske.factor + faltungsmaske.offset);
                if (G < 0) G = 0;
                else if (G > 255) G = 255;

                B = (int) (sumB / faltungsmaske.factor + faltungsmaske.offset);
                if (B < 0) B = 0;
                else if (B > 255) B = 255;

                // Schreiben der neuen Werte
                bitmapOut.setPixel(x + 1, y + 1, Color.argb(A, R, G, B));
            }
        }

        return bitmapOut;
    }

    /**
     * Berechnet Faltung einer 5x5-Faltungsmaske.
     *
     * @param bitmapIn      Eingangsbitmap
     * @param faltungsmaske Faltungsmaske
     * @return Ausgabebitmap
     */
    public static Bitmap berechneFaltung5x5(Bitmap bitmapIn, Faltungsmaske faltungsmaske) {
        int width = bitmapIn.getWidth();
        int height = bitmapIn.getHeight();
        Bitmap bitmapOut = Bitmap.createBitmap(width, height, bitmapIn.getConfig());

        int A, R, G, B;
        int sumR, sumG, sumB;
        int[][] pixels = new int[SIZEBIG][SIZEBIG];

        // im Bitmap
        for (int y = 0; y < height - 4; ++y) {
            for (int x = 0; x < width - 4; ++x) {
                // in der mask
                for (int i = 0; i < SIZEBIG; ++i) {
                    for (int j = 0; j < SIZEBIG; ++j) {
                        pixels[i][j] = bitmapIn.getPixel(x + i, y + j);
                    }
                }
                // Alpha des mittleren Pixels abrufen
                A = Color.alpha(pixels[2][2]);
                sumR = sumG = sumB = 0;
                // Jeweiligen Farbwerte abrufen (R,G,B)
                for (int i = 0; i < SIZEBIG; ++i) {
                    for (int j = 0; j < SIZEBIG; ++j) {
                        sumR += (Color.red(pixels[i][j]) * faltungsmaske.mask[i][j]);
                        sumG += (Color.green(pixels[i][j]) * faltungsmaske.mask[i][j]);
                        sumB += (Color.blue(pixels[i][j]) * faltungsmaske.mask[i][j]);
                    }
                }

                // Endwerte der jeweiligen Farbwerte festlegen (R,G,B)
                R = (int) (sumR / faltungsmaske.factor + faltungsmaske.offset);
                if (R < 0) R = 0;
                else if (R > 255) R = 255;

                G = (int) (sumG / faltungsmaske.factor + faltungsmaske.offset);
                if (G < 0) G = 0;
                else if (G > 255) G = 255;

                B = (int) (sumB / faltungsmaske.factor + faltungsmaske.offset);
                if (B < 0) B = 0;
                else if (B > 255) B = 255;

                // Schreiben der neuen Werte
                bitmapOut.setPixel(x + 1, y + 1, Color.argb(A, R, G, B));
            }
        }

        return bitmapOut;
    }

    /**
     * Berechnet Faltung einer MxM-Faltungsmaske.
     *
     * @param bitmapIn      Eingangsbitmap
     * @param faltungsmaske Faltungsmaske
     * @return Ausgabebitmap
     */
    public static Bitmap berechneFaltungMxM(Bitmap bitmapIn, Faltungsmaske faltungsmaske) {
        int width = bitmapIn.getWidth();
        int height = bitmapIn.getHeight();
        Bitmap bitmapOut = Bitmap.createBitmap(width, height, bitmapIn.getConfig());

        int sizeOfMask = faltungsmaske.mask.length;
        Log.d("INFO", "@berechneFaltungMxM -> Size of Mask: " + sizeOfMask);

        int A, R, G, B;
        int sumR, sumG, sumB;
        int[][] pixels = new int[sizeOfMask][sizeOfMask];

        // im Bitmap
        for (int y = 0; y < height - (sizeOfMask - 1); ++y) {
            for (int x = 0; x < width - (sizeOfMask - 1); ++x) {
                // in der mask
                for (int i = 0; i < sizeOfMask; ++i) {
                    for (int j = 0; j < sizeOfMask; ++j) {
                        pixels[i][j] = bitmapIn.getPixel(x + i, y + j);
                    }
                }
                // Alpha des mittleren Pixels abrufen
                A = Color.alpha(pixels[(int) Math.sqrt(sizeOfMask)][(int) Math.sqrt(sizeOfMask)]);
                sumR = sumG = sumB = 0;
                // Jeweiligen Farbwerte abrufen (R,G,B)
                for (int i = 0; i < sizeOfMask; ++i) {
                    for (int j = 0; j < sizeOfMask; ++j) {
                        sumR += (Color.red(pixels[i][j]) * faltungsmaske.mask[i][j]);
                        sumG += (Color.green(pixels[i][j]) * faltungsmaske.mask[i][j]);
                        sumB += (Color.blue(pixels[i][j]) * faltungsmaske.mask[i][j]);
                    }
                }

                // Endwerte der jeweiligen Farbwerte festlegen (R,G,B)
                R = (int) (sumR / faltungsmaske.factor + faltungsmaske.offset);
                if (R < 0) R = 0;
                else if (R > 255) R = 255;

                G = (int) (sumG / faltungsmaske.factor + faltungsmaske.offset);
                if (G < 0) G = 0;
                else if (G > 255) G = 255;

                B = (int) (sumB / faltungsmaske.factor + faltungsmaske.offset);
                if (B < 0) B = 0;
                else if (B > 255) B = 255;

                // Schreiben der neuen Werte
                bitmapOut.setPixel(x + 1, y + 1, Color.argb(A, R, G, B));
            }
        }

        return bitmapOut;
    }

}
