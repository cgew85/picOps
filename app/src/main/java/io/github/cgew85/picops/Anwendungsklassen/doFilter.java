/*
 * Number of total filters/effects:
 * 
 * 		public static Bitmap bildSpiegelungVertikal(Bitmap bitmapIn)
 * 		public static Bitmap bildSpiegelung(Bitmap bitmapIn, int type)
 * 		public static Bitmap rundeEcken(Bitmap bitmapIn, float round)
 * 		public static Bitmap verstaerkenFarbtyp(Bitmap bitmapIn, int type, float percent)
 * 		public static Bitmap glaetten(Bitmap bitmapIn, double value)
 * 		public static Bitmap imageSharpening(Bitmap bitmapIn)
 * 		public static Bitmap scharfzeichnen(Bitmap bitmapIn, double weight)
 * 		public static Bitmap gaussianBlur(Bitmap bitmapIn)
 * 		public static Bitmap doGreyscale(Bitmap bitmapIn)
 * 		public static Bitmap createContrast(Bitmap bitmapIn, double value)
 * 		public static Bitmap doGamma (Bitmap bitmapIn, double red, double green, double blue)
 * 		public static Bitmap decreaseColorDepth(Bitmap bitmapIn, int bitOffset)
 * 		public static Bitmap createSepiaToningEffect(Bitmap bitmapIn, int depth, double red, double green, double blue)
 * 		public static Bitmap doBrightness(Bitmap bitmapIn, int value)
 * 		public static Bitmap rotateImage(Bitmap bitmapIn, float degree)
 * 		public static Bitmap doColorFilter(Bitmap bitmapIn, double red, double green, double blue)
 * 		public static Bitmap hardLightMode(Bitmap bitmapIn)
 * 		public static Bitmap binaryImage(Bitmap bitmapIn)
 * 		public static Bitmap boxBlur(Bitmap bitmapIn, int range)
 * 		public static Bitmap alphaBlending(Bitmap bitmapIn)
 * 		public static Bitmap histogrammAusgleich(Bitmap bitmapIn)
 * 		public static Bitmap blend2images(Bitmap bitmapIn1, Bitmap bitmapIn2)
 * 		public static Bitmap addBorder(Bitmap bitmapIn, double value)
 * 		-------------------------------------
 * 		23 filters/effects @ 18.9.2013
 * 
 */
package io.github.cgew85.picops.Anwendungsklassen;

import android.graphics.*;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.Shader.TileMode;
import android.util.Log;
import io.github.cgew85.picops.Grenzklassen.Faltungsmaske;
import io.github.cgew85.picops.Grenzklassen.PascalschesDreieck;

import java.util.ArrayList;

/**
 * The Class doFilter.
 */
public class doFilter {

    /**
     * The Constant FLIP_VERTICAL. -> F�r Spiegelung
     */
    public static final int FLIP_VERTICAL = 1;

    /**
     * The Constant FLIP_HORIZONTAL. -> F�r Spiegelung
     */
    public static final int FLIP_HORIZONTAL = 2;

    /**
     * The Constant PI.
     */
    public static final double PI = 3.14159d;

    /**
     * The Constant FULL_CIRCLE_DEGREE.
     */
    public static final double FULL_CIRCLE_DEGREE = 360d;

    /**
     * The Constant HALF_CIRCLE_DEGREE.
     */
    public static final double HALF_CIRCLE_DEGREE = 180d;

    /**
     * The Constant RANGE.
     */
    public static final double RANGE = 256d;

    /**
     * Instantiates a new do filter.
     */
    public doFilter() {
    }

    public static ArrayList<String> getAllFilterNames() {
        ArrayList<String> allFilterNames = new ArrayList<String>();

        allFilterNames.add("verstaerkenFarbtyp");
        allFilterNames.add("glaetten");
        allFilterNames.add("imageSharpening");
        allFilterNames.add("scharfzeichnen");
        allFilterNames.add("gaussianBlur");
        allFilterNames.add("doGreyscale");
        allFilterNames.add("createContrast");
        allFilterNames.add("createContrastSW");
        allFilterNames.add("doGamma");
        allFilterNames.add("decreaseColorDepth");
        allFilterNames.add("doBrightness");
        allFilterNames.add("doColorFilter");
        allFilterNames.add("hardLightMode");
        allFilterNames.add("binaryImage");
        allFilterNames.add("boxBlur");
        allFilterNames.add("alphaBlending");
        allFilterNames.add("histogrammAusgleich");

        return allFilterNames;
    }

    public static ArrayList<String> getAllEffectNames() {
        ArrayList<String> allEffectNames = new ArrayList<String>();

        /** Alle implementiert **/
        allEffectNames.add("bildSpiegelungVertikal");
        allEffectNames.add("bildSpiegelung");
        allEffectNames.add("rundeEcken");
        allEffectNames.add("createSepiaToningEffect");
        allEffectNames.add("rotateImage");
        allEffectNames.add("addBorder");

        return allEffectNames;
    }

    /**
     * Effekt
     * addBorder
     *
     * @param bitmapIn Eingangsbitmap
     * @param value    Groesse des Rahmens
     * @retunr Ausgabebitmap
     */

    public static Bitmap addBorder(Bitmap bitmapIn, double value) {
        int width = bitmapIn.getWidth();
        int height = bitmapIn.getHeight();
        int x, y, border;
        border = (int) (((width * value) + (height * value)) / 2);
        Log.d("INFO", "@addBorder -> border: " + border);
        Bitmap bitmapOut = Bitmap.createBitmap(width, height, bitmapIn.getConfig());
        Canvas canvas = new Canvas();
        canvas.setBitmap(bitmapOut);
        canvas.drawBitmap(bitmapIn, 0, 0, null);

        for (x = 0; x < width; x++) {
            for (y = 0; y < height; y++) {
                if (((x < border) || (x > width - border)) || ((y < border) || (y > height - border))) {
                    Paint paint = new Paint();
                    paint.setARGB(127, 0, 0, 0);
                    canvas.drawPoint(x, y, paint);
                }
                if (((x == border) || (x == width - border)) || ((y == border) || (y == height - border))) {
                    Paint paint = new Paint();
                    paint.setARGB(127, 255, 255, 255);
                    canvas.drawPoint(x, y, paint);
                }
            }
        }

        return bitmapOut;
    }

    /**
     * Effekt
     * blend2images
     *
     * @param bitmapIn1 Eingangsbitmap1
     * @param bitmapIn2 Eingangsbitmap2
     * @return Ausgabebitmap
     */
    public static Bitmap blend2Images(Bitmap bitmapIn1, Bitmap bitmapIn2) {
        Canvas canvas = new Canvas();
        int width1, width2, height1, height2, outputWidth, outputHeight, pixel;
        double alphaStep;
        width1 = bitmapIn1.getWidth();
        width2 = bitmapIn2.getWidth();
        height1 = bitmapIn1.getHeight();
        height2 = bitmapIn2.getHeight();

        outputWidth = Math.max(width1, width2);
        outputHeight = Math.max(height1, height2);

        float factor = outputWidth / outputHeight;

        /** factor  ->  Aspect Ratio **/
        if (factor > 1) {
            if ((outputWidth > 640) || (outputHeight > 480)) {
                outputWidth = 640;
                outputHeight = (int) (640 / factor);
            }
        } else if (factor < 1) {
            if ((outputWidth > 480) || (outputHeight > 640)) {
                outputWidth = 480;
                outputHeight = (int) (480 / factor);
            }
        } else if (factor == 1) {
            if (outputWidth > 640) {
                outputWidth = 640;
                outputHeight = 640;
            }
        }
        /** Ende Aspect Ratio **/

        /**  Abfangen unm�glicher Werte **/
        if ((outputWidth > 640) || (outputHeight > 640)) {
            int biggerValue = Math.max(outputWidth, outputHeight);
            if (biggerValue == outputWidth) {
                outputWidth = 640;
                outputHeight = 480;
            } else {
                outputHeight = 640;
                outputWidth = 480;
            }
        }

        Log.d("INFO", "@blend2images -> Output Sizes: ( width / height ) - ( " + outputWidth + " / " + outputHeight + " ) @ factor: " + factor);
        Bitmap bitmapOut = Bitmap.createBitmap(outputWidth, outputHeight, Config.ARGB_8888);
        canvas.setBitmap(bitmapOut);

        Log.d("INFO", "@blend2images -> Pre-resize: bitmap1 ( " + bitmapIn1.getWidth() + " / " + bitmapIn1.getHeight() + " )");
        Log.d("INFO", "@blend2images -> Pre-resize: bitmap2 ( " + bitmapIn2.getWidth() + " / " + bitmapIn2.getHeight() + " )");
        bitmapIn1 = scaleImage.getResizedBitmap(bitmapIn1, outputHeight, outputWidth);
        bitmapIn2 = scaleImage.getResizedBitmap(bitmapIn2, outputHeight, outputWidth);
        Log.d("INFO", "@blend2images -> resize: bitmap1 ( " + bitmapIn1.getWidth() + " / " + bitmapIn1.getHeight() + " )");
        Log.d("INFO", "@blend2images -> resize: bitmap2 ( " + bitmapIn2.getWidth() + " / " + bitmapIn2.getHeight() + " )");

        int[] pixels1 = new int[outputWidth * outputHeight];
        int[] pixels2 = new int[outputWidth * outputHeight];
        bitmapIn1.getPixels(pixels1, 0, outputWidth, 0, 0, outputWidth, outputHeight);
        bitmapIn2.getPixels(pixels2, 0, outputWidth, 0, 0, outputWidth, outputHeight);
        Log.d("INFO", "@blend2images -> arrays: pixels1 - " + pixels1.length + " pixels2 -" + pixels2.length);

        alphaStep = 255.0 / (double) outputWidth;

        Log.d("INFO", "@blend2images -> alphaStep: " + alphaStep);
        for (int x = 0; x < outputWidth; x++) {
            for (int y = 0; y < outputHeight; y++) {
                int index = y * outputWidth + x;
                pixel = pixels1[index];
                int alpha = Color.alpha(pixel);
                alpha = (int) (alpha - alphaStep);
                pixel = Color.argb(alpha, Color.red(pixel), Color.green(pixel), Color.blue(pixel));
                pixels1[index] = pixel;
            }
        }

        for (int x = 0; x < outputWidth; x++) {
            for (int y = 0; y < outputHeight; y++) {
                int index = y * outputWidth + x;
                pixel = pixels2[index];
                int alpha = Color.alpha(pixel);
                alpha = (int) (x * alphaStep);
                pixel = Color.argb(alpha, Color.red(pixel), Color.green(pixel), Color.blue(pixel));
                pixels2[index] = pixel;
            }
        }

        canvas.drawBitmap(pixels1, 0, outputWidth, 0, 0, outputWidth, outputHeight, true, null);
        canvas.drawBitmap(pixels2, 0, outputWidth, 0, 0, outputWidth, outputHeight, true, null);
        bitmapIn1.recycle();
        bitmapIn1 = null;
        bitmapIn2.recycle();
        bitmapIn2 = null;
        pixels1 = null;
        pixels2 = null;
        canvas = null;

        return bitmapOut;
    }

    /**
     * Filter
     * Histogrammausgleich
     *
     * @param bitmapIn Eingangsbitmap
     * @return Ausgabebitmap
     */
    private static ArrayList<int[]> createHistogram(Bitmap bitmapIn) {
        int[] redHistogram = new int[256];
        int[] greenHistogram = new int[256];
        int[] blueHistogram = new int[256];
        int pixel, red, green, blue;

        /** Vorf�llen mit 0 **/
        for (int i = 0; i < redHistogram.length; i++) {
            redHistogram[i] = greenHistogram[i] = blueHistogram[i] = 0;
        }

        for (int x = 0; x < bitmapIn.getWidth(); x++) {
            for (int y = 0; y < bitmapIn.getHeight(); y++) {
                pixel = bitmapIn.getPixel(x, y);
                red = Color.red(pixel);
                green = Color.green(pixel);
                blue = Color.blue(pixel);
                redHistogram[red] += 1;
                greenHistogram[green] += 1;
                blueHistogram[blue] += 1;
            }
        }

        ArrayList<int[]> histogram = new ArrayList<int[]>();
        histogram.add(redHistogram);
        histogram.add(greenHistogram);
        histogram.add(blueHistogram);

        return histogram;
    }

    private static ArrayList<int[]> histogramEqualizationLookUpTableCreation(Bitmap bitmapIn) {
        int width = bitmapIn.getWidth();
        int height = bitmapIn.getHeight();
        ArrayList<int[]> histogramFromBitmapIn = createHistogram(bitmapIn);
        ArrayList<int[]> localLookUpTable = new ArrayList<int[]>();

        int[] redHistogram = new int[256];
        int[] greenHistogram = new int[256];
        int[] blueHistogram = new int[256];

        /** Vorbelegen mit 0 **/
        for (int i = 0; i < redHistogram.length; i++) {
            redHistogram[i] = greenHistogram[i] = blueHistogram[i] = 0;
        }

        long sumR = 0;
        long sumG = 0;
        long sumB = 0;

        float skalierung = (float) (255.0 / (width * height));

        for (int i = 0; i < redHistogram.length; i++) {
            sumR += histogramFromBitmapIn.get(0)[i];
            int red = (int) (sumR * skalierung);
            if (red > 255) {
                redHistogram[i] = 255;
            } else {
                redHistogram[i] = red;
            }

            sumG += histogramFromBitmapIn.get(1)[i];
            int green = (int) (sumG * skalierung);
            if (green > 255) {
                greenHistogram[i] = 255;
            } else {
                greenHistogram[i] = green;
            }

            sumB += histogramFromBitmapIn.get(2)[i];
            int blue = (int) (sumB * skalierung);
            if (blue > 255) {
                blueHistogram[i] = 255;
            } else {
                blueHistogram[i] = blue;
            }
        }

        localLookUpTable.add(redHistogram);
        localLookUpTable.add(greenHistogram);
        localLookUpTable.add(blueHistogram);

        return localLookUpTable;
    }

    public static Bitmap histogrammAusgleich(Bitmap bitmapIn) {
        int red, green, blue, alpha;
        int pixel = 0;
        int width = bitmapIn.getWidth();
        int height = bitmapIn.getHeight();

        ArrayList<int[]> lookUpTable = histogramEqualizationLookUpTableCreation(bitmapIn);
        Bitmap bitmapOut = Bitmap.createBitmap(width, height, bitmapIn.getConfig());

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                pixel = bitmapIn.getPixel(x, y);
                alpha = Color.alpha(pixel);
                red = Color.red(pixel);
                green = Color.green(pixel);
                blue = Color.blue(pixel);

                red = lookUpTable.get(0)[red];
                green = lookUpTable.get(1)[green];
                blue = lookUpTable.get(2)[blue];

                pixel = Color.argb(alpha, red, green, blue);
                bitmapOut.setPixel(x, y, pixel);
            }
        }

        return bitmapOut;
    }

    /** Ende Histogrammausgleich **/

    /**
     * Filter
     * Alpha blending
     *
     * @return Ausgabebitmap
     * @params bitmapIn Eingangsbitmap
     */
    public static Bitmap alphaBlending(Bitmap bitmapIn) {
        int width = bitmapIn.getWidth();
        int height = bitmapIn.getHeight();
        int alpha, red, green, blue;
        int pixel;
        Bitmap bitmapAlpha = Bitmap.createBitmap(width, height, bitmapIn.getConfig());

        bitmapAlpha = doFilter.doGreyscale(bitmapIn);

        /** Alpha halbieren des Graustufenbildes **/
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                pixel = bitmapAlpha.getPixel(x, y);
                alpha = Color.alpha(pixel);
                alpha /= 2;
                pixel = Color.argb((int) alpha, Color.red(pixel), Color.green(pixel), Color.blue(pixel));
                bitmapAlpha.setPixel(x, y, pixel);
            }
        }

        Bitmap bitmapOut = Bitmap.createBitmap(width, height, bitmapIn.getConfig());

        /** Rot und Gr�n abschw�chen **/
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                pixel = bitmapIn.getPixel(x, y);
                red = Color.red(pixel);
                green = Color.green(pixel);
                red /= 2;
                green /= 2;
                pixel = Color.argb(Color.alpha(pixel), red, green, Color.blue(pixel));
                bitmapOut.setPixel(x, y, pixel);
            }
        }

        Canvas canvas = new Canvas(bitmapOut);
        canvas.drawBitmap(bitmapAlpha, 0, 0, null);
        bitmapAlpha.recycle();
        bitmapAlpha = null;

        return bitmapOut;
    }

    /**
     * Filter
     * boxBlur
     *
     * @param bitmapIn Eingangsbitmap
     * @param range    Radius
     * @return Ausgabebitmap
     */
    public static Bitmap boxBlur(Bitmap bitmapIn, int range) {
        Bitmap bitmapOut = Bitmap.createBitmap(bitmapIn.getWidth(), bitmapIn.getHeight(), Config.ARGB_8888);
        Canvas c = new Canvas(bitmapOut);

        int width = bitmapIn.getWidth();
        int height = bitmapIn.getHeight();

        int[] pixels = new int[bitmapIn.getWidth() * bitmapIn.getHeight()];
        bitmapIn.getPixels(pixels, 0, width, 0, 0, width, height);

        boxBlurHorizontal(pixels, width, height, range / 2);
        boxBlurVertical(pixels, width, height, range / 2);

        c.drawBitmap(pixels, 0, width, 0.0F, 0.0F, width, height, true, null);

        return bitmapOut;
    }

    private static void boxBlurHorizontal(int[] pixels, int width, int height, int halfRange) {
        int index = 0;
        int[] newColors = new int[width];
        for (int y = 0; y < height; y++) {
            int hits = 0;
            long r = 0;
            long g = 0;
            long b = 0;

            for (int x = -halfRange; x < width; x++) {
                int oldPixel = x - halfRange - 1;
                if (oldPixel >= 0) {
                    int color = pixels[index + oldPixel];
                    if (color != 0) {
                        r -= Color.red(color);
                        g -= Color.green(color);
                        b -= Color.blue(color);
                    }
                    hits--;
                }

                int newPixel = x + halfRange;
                if (newPixel < width) {
                    int color = pixels[index + newPixel];
                    if (color != 0) {
                        r += Color.red(color);
                        g += Color.green(color);
                        b += Color.blue(color);
                    }
                    hits++;
                }

                if (x >= 0) {
                    newColors[x] = Color.argb(0xFF, (int) (r / hits), (int) (g / hits), (int) (b / hits));
                }
            }
            for (int x = 0; x < width; x++) {
                pixels[index + x] = newColors[x];
            }
            index += width;
        }
    }

    private static void boxBlurVertical(int[] pixels, int width, int height, int halfRange) {
        int[] newColors = new int[height];
        int oldPixelOffset = -(halfRange + 1) * width;
        int newPixelOffset = (halfRange) * width;

        for (int x = 0; x < width; x++) {
            int hits = 0;
            long r = 0;
            long g = 0;
            long b = 0;
            int index = -halfRange * width + x;
            for (int y = -halfRange; y < height; y++) {
                int oldPixel = y - halfRange - 1;
                if (oldPixel >= 0) {
                    int color = pixels[index + oldPixelOffset];
                    if (color != 0) {
                        r -= Color.red(color);
                        g -= Color.green(color);
                        b -= Color.blue(color);
                    }
                    hits--;
                }
                int newPixel = y + halfRange;
                if (newPixel < height) {
                    int color = pixels[index + newPixelOffset];
                    if (color != 0) {
                        r += Color.red(color);
                        g += Color.green(color);
                        b += Color.blue(color);
                    }
                    hits++;
                }
                if (y >= 0) {
                    newColors[y] = Color.argb(0xFF, (int) (r / hits), (int) (g / hits), (int) (b / hits));
                }
                index += width;
            }
            for (int y = 0; y < height; y++) {
                pixels[y * width + x] = newColors[y];
            }
        }
    }

    /*******************/
    /** Ende Box Blur **/
    /*******************/

    /**
     * Filter
     * Hard light mode
     *
     * @param bitmapIn Eingangsbitmap
     * @return Ausgabebitmap
     */
    public static Bitmap hardLightMode(Bitmap bitmapIn) {
        Bitmap bitmapOut = Bitmap.createBitmap(bitmapIn);
        int pixel, red, green, blue;
        double grey;
        Color color = new Color();

        bitmapOut = doFilter.doGreyscale(bitmapIn);
        for (int x = 0; x < bitmapOut.getWidth(); x++) {
            for (int y = 0; y < bitmapIn.getHeight(); y++) {
                pixel = bitmapIn.getPixel(x, y);
                red = Color.red(pixel);
                green = Color.green(pixel);
                blue = Color.blue(pixel);
                grey = ((red * 0.3f) + (green * 0.59f) + (blue * 0.11f));
                //Log.d("INFO", "Value grey = "+grey+" Value hardLight: "+hardLightLayer(grey,grey));
                red = green = blue = (int) hardLightLayer(grey, grey);
                bitmapOut.setPixel(x, y, color.argb(0xFF, red, green, blue));
            }
        }
        return bitmapOut;
    }

    private static double hardLightLayer(double maskAnt, double imgAnt) {
        if (maskAnt > 128) {
            return 255 - (((255 - (2 * (maskAnt - 128))) * (255 - imgAnt)) / 256);
        } else {
            return (2 * maskAnt * imgAnt) / 256;
        }
    }

    /** Ende Filter Hard Light Mode **/

    /**
     * Filter
     * Bin�rbild
     *
     * @param bitmapIn Eingangsbitmap
     * @return Ausgabebitmap
     */

    public static Bitmap binaryImage(Bitmap bitmapIn) {
        Bitmap bitmapOut = doFilter.doGreyscale(bitmapIn);

        int width = bitmapIn.getWidth();
        int height = bitmapIn.getHeight();
        int pixel, red, green, blue;
        double grey;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                pixel = bitmapOut.getPixel(x, y);
                red = Color.red(pixel);
                green = Color.green(pixel);
                blue = Color.blue(pixel);
                grey = ((red * 0.3f) + (green * 0.59f) + (blue * 0.11f));
                red = green = blue = schwellenOperation(grey);
                bitmapOut.setPixel(x, y, Color.argb(0xFF, red, green, blue));
            }
        }

        return bitmapOut;
    }

    private static int schwellenOperation(double grey) {
        if (grey > 128.0) {
            return 255;
        } else {
            return 0;
        }
    }

    /** Ende Filter Bin�rbild **/

    /**
     * Effekt
     * Bild mit Reflexion unterhalb.
     *
     * @param bitmapIn Eingangsbitmap
     * @return Ausgabebitmap
     */
    public static Bitmap bildSpiegelungVertikal(Bitmap bitmapIn) {
        // Luecke zwischen Bild und Spiegelung
        final int luecke = 4;

        int width = bitmapIn.getWidth();
        int height = bitmapIn.getHeight();

        // Matrix zum Kippen des Bildes (vertikal)
        Matrix matrix = new Matrix();
        matrix.preScale(1.0f, -1.0f);

        // Bitmap mit angepassten Dimensionen und Matrix angewendet
        Bitmap gespiegeltesBild = Bitmap.createBitmap(bitmapIn, 0, height / 2, width, height / 2, matrix, false);

        // Bild mit Dimensionen f�r beide Bilder
        Bitmap gesamtBild = Bitmap.createBitmap(width, (height + height / 2), Config.ARGB_8888);

        // Canvas f�r Bild + Spiegelung + L�cke
        Canvas canvas = new Canvas(gesamtBild);
        canvas.drawBitmap(bitmapIn, 0, 0, null);

        // Zeichnung L�cke
        Paint paint = new Paint();
        canvas.drawRect(0, height, width, height + luecke, paint);

        // Reflexion einf�gen
        canvas.drawBitmap(gespiegeltesBild, 0, height + luecke, null);

        // Linearer Gradient f�r Reflexionseffekt
        Paint paint2 = new Paint();
        LinearGradient lgrad = new LinearGradient(0, bitmapIn.getHeight(), 0, gesamtBild.getHeight() + luecke, 0x70ffffff, 0x00ffffff, TileMode.CLAMP);
        paint2.setShader(lgrad);
        paint2.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        canvas.drawRect(0, height, width, gesamtBild.getHeight() + luecke, paint2);

        return gesamtBild;
    }

    /**
     * Effekt
     * Spiegelung eines Bildes.
     *
     * @param bitmapIn Eingangsbitmap
     * @param type     1 oder 2 f�r vert. oder horiz. Spiegelung
     * @return Ausgabebitmap
     */
    public static Bitmap bildSpiegelung(Bitmap bitmapIn, int type) {
        Matrix matrix = new Matrix();

        if (type == FLIP_VERTICAL) {
            matrix.preScale(1.0f, -1.0f);
        } else if (type == FLIP_HORIZONTAL) {
            matrix.preScale(-1.0f, 1.0f);
        } else {
            return null;
        }

        return Bitmap.createBitmap(bitmapIn, 0, 0, bitmapIn.getWidth(), bitmapIn.getHeight(), matrix, true);
    }

    /**
     * Effekt
     * Ecken abrunden.
     *
     * @param bitmapIn Eingangsbitmap
     * @param round    Rundungsgrad (45)
     * @return Ausgabebitmap
     */
    public static Bitmap rundeEcken(Bitmap bitmapIn, float round) {
        int width = bitmapIn.getWidth();
        int height = bitmapIn.getHeight();
        Bitmap bitmapOut = Bitmap.createBitmap(width, height, bitmapIn.getConfig());

        Canvas canvas = new Canvas(bitmapOut);
        canvas.drawARGB(0, 0, 0, 0);

        // Paintobjekt
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);

        final Rect rechteck = new Rect(0, 0, width, height);
        final RectF rechteckf = new RectF(rechteck);

        // Rechteck auf Canvasobjekt zeichnen
        canvas.drawRoundRect(rechteckf, round, round, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmapIn, rechteck, rechteck, paint);

        return bitmapOut;
    }

    /**
     * Filter
     * Farbkanal verst�rken.
     *
     * @param bitmapIn Eingangsbitmap
     * @param type     Farbkanal (1 = Rot, 2 = Gr�n, 3 = Blau)
     * @param percent  Grad der Verst�rkung (10% Schritten)
     * @return Ausgabebitmap
     */
    public static Bitmap verstaerkenFarbtyp(Bitmap bitmapIn, int type, float percent) {
        int width = bitmapIn.getWidth();
        int height = bitmapIn.getHeight();
        Bitmap bitmapOut = Bitmap.createBitmap(width, height, bitmapIn.getConfig());

        int A, R, G, B;
        int pixel;

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                pixel = bitmapIn.getPixel(x, y);
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);

                if (type == 1) {
                    R = (int) (R * (1 + percent));
                    if (R > 255) R = 255;
                }
                if (type == 2) {
                    G = (int) (G * (1 + percent));
                    if (G > 255) G = 255;
                }
                if (type == 3) {
                    B = (int) (B * (1 + percent));
                    if (B > 255) B = 255;
                }
                bitmapOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        return bitmapOut;
    }

    /**
     * Filter
     * Glaetten des Bildes durch Mittelwertbildung
     *
     * @param bitmapIn Eingangsbitmap
     * @param value    Wert der Gl�ttung
     * @return Ausgabebitmap
     */
    public static Bitmap glaetten(Bitmap bitmapIn, int value) {
        Faltungsmaske faltungsmaske = new Faltungsmaske(value);
        faltungsmaske.setAll(1);
        //faltungsmaske.Maske[1][1] = value;
        faltungsmaske.Factor = faltungsmaske.Maske.length; // was value + 8
        faltungsmaske.Offset = 0; // was 1

        return Faltungsmaske.berechneFaltungMxM(bitmapIn, faltungsmaske);
    }

    /**
     * Filter
     * Glaetten des Bildes durch Mittelwertbildung
     *
     * @param bitmapIn Eingangsbitmap
     * @param value    Wert der Gl�ttung
     * @return Ausgabebitmap
     */
    public static Bitmap glaettenFuerExport(Bitmap bitmapIn, int oldSizeOfMask, int prevWidth, int prevHeight, int curWidth, int curHeight) {
        /** Value: Quadratische Maske mit value x value als Dimensionen **/
        int newSizeOfMask = adjustSizeForMask(oldSizeOfMask, prevWidth, prevHeight, curWidth, curHeight);
        Faltungsmaske faltungsmaske = new Faltungsmaske(newSizeOfMask);
        faltungsmaske.setAll(1);
        //faltungsmaske.Maske[1][1] = value;
        faltungsmaske.Factor = faltungsmaske.Maske.length; // was value + 8
        faltungsmaske.Offset = 0; // was 1

        return Faltungsmaske.berechneFaltungMxM(bitmapIn, faltungsmaske);
    }

    private static int adjustSizeForMask(int oldSizeOfMask, int prevWidth, int prevHeight, int curWidth, int curHeight) {
        int newSizeMask = 0;

        int prevPixels = prevWidth * prevHeight;
        int oldMaskPixels = oldSizeOfMask * oldSizeOfMask;

        double oldRatio = prevPixels / oldMaskPixels;
        float fltNewSizeMask = (float) Math.sqrt(((curWidth * curHeight) / oldRatio));
        newSizeMask = Math.round(fltNewSizeMask);
        Log.d("INFO", "@adjustSizeForMask -> newSize: " + newSizeMask + " was: " + oldSizeOfMask);

        return newSizeMask;
    }

    /**
     * Filter
     * Reduziert Unsch�rfe durch unscharfes Maskieren
     *
     * @param bitmapIn Eingangsbitmap
     * @return Ausgabebitmap
     */
    public static Bitmap imageSharpening(Bitmap bitmapIn, int boxWidth, int boxHeight) {
        int width = bitmapIn.getWidth();
        int height = bitmapIn.getHeight();
        Bitmap bitmapOut = Bitmap.createBitmap(width, height, bitmapIn.getConfig());
        int[] tempPix = new int[width * height];
        bitmapIn.getPixels(tempPix, 0, width, 0, 0, width, height);
        bitmapOut.setPixels(tempPix, 0, width, 0, 0, width, height);

        int left = boxWidth / 2 + 1;
        int top = boxHeight / 2 + 1;
        int right = width - left;
        int bottom = height - top;

        float unsharpMaskAmount = 0.5f;
        int unsharpMaskThreshold = 3;

        int[][] sourcePixels = loadPixelsFromImage(bitmapIn);
        int[][] blurredPixels = new int[width][height];

        inlineApplyBlur(bitmapOut, sourcePixels, blurredPixels, left, top, right, bottom, boxWidth, boxHeight);
        applyUnsharpMask(bitmapOut, sourcePixels, blurredPixels, left, top, right, bottom, unsharpMaskAmount, unsharpMaskThreshold);

        return bitmapOut;
    }

    private static int[][] loadPixelsFromImage(Bitmap bitmapIn) {
        int width = bitmapIn.getWidth();
        int height = bitmapIn.getHeight();
        int[][] pixels = new int[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                pixels[x][y] = bitmapIn.getPixel(x, y);
            }
        }

        return pixels;
    }

    private static void inlineApplyBlur(Bitmap bitmapIn, int[][] sourcePixels, int[][] blurredPixels, int left, int top, int right, int bottom, int filtX, int filtY) {
        for (int x = left; x < right; x++) {
            for (int y = top; y < bottom; y++) {
                blurredPixels[x][y] = blurPixels(sourcePixels, (x - filtX / 2), (y - filtY / 2), (x + filtX / 2), (y + filtY / 2));
                bitmapIn.setPixel(x, y, blurredPixels[x][y]);
            }
        }
    }

    private static int blurPixels(int[][] sourcePixels, int left, int top, int right, int bottom) {
        int red, green, blue;
        red = green = blue = 0;
        int boxSize = (right - left) * (bottom - top);

        for (int x = left; x < right; x++) {
            for (int y = top; y < bottom; y++) {
                int pixel = sourcePixels[x][y];
                red += Color.red(pixel);
                green += Color.green(pixel);
                blue += Color.blue(pixel);
            }
        }

        red /= boxSize;
        green /= boxSize;
        blue /= boxSize;

        return Color.argb(0xFF, red, green, blue);
    }

    private static void applyUnsharpMask(Bitmap bitmapIn, int[][] sourcePixels, int[][] blurredPixels, int left, int top, int right, int bottom, float amount, int threshold) {
        int oRed, oGreen, oBlue;
        oRed = oGreen = oBlue = 0;
        int bRed, bGreen, bBlue;
        bRed = bGreen = bBlue = 0;
        int unsharpMaskPixel = 0;

        for (int x = left; x < right; x++) {
            for (int y = top; y < bottom; y++) {
                int oPixel = sourcePixels[x][y];
                int bPixel = blurredPixels[x][y];

                oRed = Color.red(oPixel);
                oGreen = Color.green(oPixel);
                oBlue = Color.blue(oPixel);
                bRed = Color.red(bPixel);
                bGreen = Color.green(bPixel);
                bBlue = Color.blue(bPixel);

                /** Threshold **/
                if (Math.abs(oRed - bRed) >= threshold) {
                    oRed = (int) (amount * (oRed - bRed) + oRed);
                    oRed = oRed > 255 ? 255 : oRed < 0 ? 0 : oRed;
                }
                if (Math.abs(oGreen - bGreen) >= threshold) {
                    oGreen = (int) (amount * (oGreen - bGreen) + oGreen);
                    oGreen = oGreen > 255 ? 255 : oGreen < 0 ? 0 : oGreen;
                }
                if (Math.abs(oBlue - bBlue) >= threshold) {
                    oBlue = (int) (amount * (oBlue - bBlue) + oBlue);
                    oBlue = oBlue > 255 ? 255 : oBlue < 0 ? 0 : oBlue;
                }

                unsharpMaskPixel = Color.argb(0xFF, oRed, oGreen, oBlue);
                bitmapIn.setPixel(x, y, unsharpMaskPixel);
            }
        }

    }

    /**
     * Filter
     * Scharfzeichnungsfilter.
     *
     * @param bitmapIn Eingangsbitmap
     * @param weight   Gewichtung
     * @return Ausgabebitmap
     */
    public static Bitmap scharfzeichnen(Bitmap bitmapIn) {
        double[][] scharfzeichnerConfig = new double[][]
                {
                        {0, -1, 0},
                        {-1, 5, -1},
                        {0, -1, 0},
                };
        Faltungsmaske faltungsmaske = new Faltungsmaske(3);
        faltungsmaske.applyFaltungskonfiguration(scharfzeichnerConfig);
        faltungsmaske.Factor = 1; //weight - 8; // was weight - 8
        faltungsmaske.Offset = 0;

        return Faltungsmaske.berechneFaltung3x3(bitmapIn, faltungsmaske);
    }

    /**
     * Filter
     * Gau�'scher Weichzeichner.
     *
     * @param bitmapIn Eingangsbitmap
     * @return Ausgabebitmap
     */
    public static Bitmap gaussianBlur(Bitmap bitmapIn) {
        double[][] FaltungsmaskeGaussianBlur = new double[][]
                {
                        {1, 2, 1},
                        {2, 4, 2},
                        {1, 2, 1}
                };
        Faltungsmaske faltungsmaske = new Faltungsmaske(3);
        faltungsmaske.applyFaltungskonfiguration(FaltungsmaskeGaussianBlur);
        faltungsmaske.Factor = 16;
        faltungsmaske.Offset = 0;

        return Faltungsmaske.berechneFaltung3x3(bitmapIn, faltungsmaske);
    }

    /**
     * Filter (nur interner Gebrauch)
     * Gau�'scher Weichzeichner stark
     *
     * @param bitmapIn Eingangsbitmap
     * @return Ausgabebitmap
     */
    public static Bitmap gaussianBlurStrong(Bitmap bitmapIn) {
        double[][] FaltungsmaskeGaussianBlur = new double[][]
                {
                        {1, 4, 6, 4, 1},
                        {4, 16, 24, 16, 4},
                        {6, 24, 36, 24, 6},
                        {4, 16, 24, 16, 4},
                        {1, 4, 6, 4, 1}
                };
        Faltungsmaske faltungsmaske = new Faltungsmaske(5);
        faltungsmaske.applyFaltungskonfigurationBig(FaltungsmaskeGaussianBlur);
        faltungsmaske.Factor = 256;
        faltungsmaske.Offset = 0;

        return Faltungsmaske.berechneFaltung5x5(bitmapIn, faltungsmaske);
    }

    /**
     * Filter
     * Gau�'scher Weichzeichner mit flexiblem Input
     *
     * @param bitmapIn     Eingangsbitmap
     * @param origWidth    Weite des Previewbildes
     * @param origHeight   H�he des Previewbildes
     * @param curWidth     Weite des jetzigen Bildes
     * @param curHeight    H�he des jetzigen Bildes
     * @param usedMaskSize Seitenl�nge der benutzten Maske
     * @return Ausgabebild
     */
    public static Bitmap gaussianBlurForOutput(Bitmap bitmapIn, int origWidth, int origHeight, int curWidth, int curHeight, int usedMaskSize) {
        double[][] faltungsmaskeArray = PascalschesDreieck.generateFaltungsmaske(usedMaskSize, origWidth, origHeight, curWidth, curHeight);
        Faltungsmaske faltungsmaske = new Faltungsmaske(faltungsmaskeArray.length);
        faltungsmaske.applyFaltungskonfigurationBig(faltungsmaskeArray, faltungsmaskeArray.length);
        faltungsmaske.Factor = PascalschesDreieck.koeffZurNorm(faltungsmaskeArray);
        faltungsmaske.Offset = 0.0;

        return Faltungsmaske.berechneFaltungMxM(bitmapIn, faltungsmaske);
    }

    /**
     * Filter
     * Umwandlung eines Eingangsbitmaps in ein Graustufenbild.
     *
     * @param bitmapIn Das Ursprungsbitmap
     * @return Das nach Graustufen umgewandelte Ursprungsbitmap
     */
    public static Bitmap doGreyscale(Bitmap bitmapIn) {
        // Ausgabebitmap erzeugen
        Bitmap bitmapOut = Bitmap.createBitmap(bitmapIn.getWidth(), bitmapIn.getHeight(), bitmapIn.getConfig());

		/* Konstante Faktoren zur Berechnung der Graustufen
         *
		 * 30% Rot, 59% Gr�n, 11% Blau
		 * 
		 */
        final double konstFaktorRot = 0.299;
        final double konstFaktorGruen = 0.587;
        final double konstFaktorBlau = 0.114;

        int A, R, G, B;
        int pixel;

        int width = bitmapIn.getWidth();
        int height = bitmapIn.getHeight();

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                pixel = bitmapIn.getPixel(x, y);
                // Holte Informationen zu den jeweiligen Kan�len
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                // Berechnung des Output-Pixels als Graustufe
                R = G = B = (int) (konstFaktorRot * R + konstFaktorGruen * G + konstFaktorBlau * B);
                bitmapOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        return bitmapOut;
    }

    /**
     * Filter
     * Kontrastfilter.
     *
     * @param bitmapIn Eingangsbitmap
     * @param value    Kontrastwert
     * @return Ausgabebitmap
     */
    public static Bitmap createContrast(Bitmap bitmapIn, double value) {
        int width = bitmapIn.getWidth();
        int height = bitmapIn.getHeight();

        Bitmap bitmapOut = Bitmap.createBitmap(width, height, bitmapIn.getConfig());

        int A, R, G, B;
        int pixel;
        double contrast = Math.pow((100 + value) / 100, 2);

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                pixel = bitmapIn.getPixel(x, y);
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                R = (int) (((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if (R < 0) R = 0;
                else if (R > 255) R = 255;

                G = Color.green(pixel);
                G = (int) (((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if (G < 0) G = 0;
                else if (G > 255) G = 255;

                B = Color.blue(pixel);
                B = (int) (((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if (B < 0) B = 0;
                else if (B > 255) B = 255;

                bitmapOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        return bitmapOut;
    }

    /**
     * Filter
     * Kontrastfilter(SW).
     *
     * @param bitmapIn Eingangsbitmap
     * @param value    Kontrastwert
     * @return Ausgabebitmap
     */
    public static Bitmap createContrastSW(Bitmap bitmapIn, double value) {
        int width = bitmapIn.getWidth();
        int height = bitmapIn.getHeight();

        Bitmap bitmapOut = Bitmap.createBitmap(width, height, bitmapIn.getConfig());

        int A, R, G, B;
        int pixel;
        double contrast = Math.pow((100 + value) / 100, 2);

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                pixel = bitmapIn.getPixel(x, y);
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                R = (int) (((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if (R < 0) R = 0;
                else if (R > 255) R = 255;

                G = Color.red(pixel);
                G = (int) (((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if (G < 0) G = 0;
                else if (G > 255) G = 255;

                B = Color.red(pixel);
                B = (int) (((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if (B < 0) B = 0;
                else if (B > 255) B = 255;

                bitmapOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        return bitmapOut;
    }

    /**
     * Filter
     * Gamma-Korrektur.
     *
     * @param bitmapIn Das Ursprungsbitmap
     * @param red      Eingabewert Rot
     * @param green    Eingabewert Gr�n
     * @param blue     Eingabewert Blau
     * @return Das gammakorrigierte Ausgabebild
     */
    public static Bitmap doGamma(Bitmap bitmapIn, double red, double green, double blue) {
        // Ausgabebitmap erzeugen
        Bitmap bitmapOut = Bitmap.createBitmap(bitmapIn.getWidth(), bitmapIn.getHeight(), bitmapIn.getConfig());

        // Bildgr��e
        int width = bitmapIn.getWidth();
        int height = bitmapIn.getHeight();

        int A, R, G, B;
        int pixel;

        // Konstanten
        final int MAX_SIZE = 256;
        final double MAX_VALUE_DBL = 255.0;
        final int MAX_VALUE_INT = 255;
        final double REVERSE = 1.0;

        int[] gammaR = new int[MAX_SIZE];
        int[] gammaG = new int[MAX_SIZE];
        int[] gammaB = new int[MAX_SIZE];

        for (int i = 0; i < MAX_SIZE; ++i) {
            gammaR[i] = (int) Math.min(MAX_VALUE_INT, (int) ((MAX_VALUE_DBL * Math.pow(i / MAX_VALUE_DBL, REVERSE / red)) + 0.5));
            gammaG[i] = (int) Math.min(MAX_VALUE_INT, (int) ((MAX_VALUE_DBL * Math.pow(i / MAX_VALUE_DBL, REVERSE / green)) + 0.5));
            gammaB[i] = (int) Math.min(MAX_VALUE_INT, (int) ((MAX_VALUE_DBL * Math.pow(i / MAX_VALUE_DBL, REVERSE / blue)) + 0.5));
        }

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                pixel = bitmapIn.getPixel(x, y);
                A = Color.alpha(pixel);

                R = gammaR[Color.red(pixel)];
                G = gammaG[Color.green(pixel)];
                B = gammaB[Color.blue(pixel)];

                // Ins Ausgabebild schreiben
                bitmapOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        return bitmapOut;
    }

    /**
     * Filter
     * Verringern der Farbtiefe.
     *
     * @param bitmapIn  Eingangsbitmap
     * @param bitOffset Bit Offset (32,64,128...)
     * @return Ausgabebitmap
     */
    public static Bitmap decreaseColorDepth(Bitmap bitmapIn, int bitOffset) {
        int width = bitmapIn.getWidth();
        int height = bitmapIn.getHeight();

        Bitmap bitmapOut = Bitmap.createBitmap(width, height, bitmapIn.getConfig());

        int A, R, G, B;
        int pixel;

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                pixel = bitmapIn.getPixel(x, y);
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);

                R = ((R + (bitOffset / 2)) - ((R + (bitOffset / 2)) % bitOffset) - 1);
                if (R < 0) {
                    R = 0;
                }

                G = ((G + (bitOffset / 2)) - ((G + (bitOffset / 2)) % bitOffset) - 1);
                if (G < 0) {
                    G = 0;
                }

                B = ((B + (bitOffset / 2)) - ((B + (bitOffset / 2)) % bitOffset) - 1);
                if (B < 0) {
                    B = 0;
                }

                bitmapOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        return bitmapOut;
    }

    /**
     * Effekt
     * Sepia-Effekt.
     *
     * @param bitmapIn   Eingangsbitmap
     * @param intensitiy Intensit�t
     * @return Ausgabebitmap
     */
    public static Bitmap createSepiaToningEffect(Bitmap bitmapIn, int intensity) {
        int depth = 20;
        int width = bitmapIn.getWidth();
        int height = bitmapIn.getHeight();
        int red, green, blue;
        red = green = blue = 0;

        Bitmap bitmapOut = Bitmap.createBitmap(width, height, bitmapIn.getConfig());

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixel = bitmapIn.getPixel(x, y);
                red = Color.red(pixel);
                green = Color.green(pixel);
                blue = Color.blue(pixel);

                int grey = (red + green + blue) / 3;
                red = green = blue = grey;
                red = red + (depth * 2);
                green += depth;

                if (red > 255) red = 255;
                if (green > 255) green = 255;
                if (blue > 255) blue = 255;

                blue -= intensity;

                if (blue < 0) blue = 0;
                if (blue > 255) blue = 255;

                bitmapOut.setPixel(x, y, Color.argb(0xFF, red, green, blue));
            }
        }
        /*
		final double greyscaleRed = 0.3;
		final double greyscaleGreen = 0.59;
		final double greyscaleBlue = 0.11;
		
		int A, R, G, B;
		int pixel;
		
		for(int x = 0;x < width; ++x)
		{
			for(int y = 0;y < height; ++y)
			{
				pixel = bitmapIn.getPixel(x, y);
				A = Color.alpha(pixel);
				R = Color.red(pixel);
				G = Color.green(pixel);
				B = Color.blue(pixel);
				
				B = G = R = (int)(greyscaleRed * R + greyscaleGreen * G + greyscaleBlue * B);
				
				R += (depth * red);
				if(R > 255)
				{
					R = 255;
				}
				
				G += (depth * green);
				if(G > 255)
				{
					G = 255;
				}
				
				B += (depth * blue);
				if(B > 255)
				{
					B = 255;
				}
				
				bitmapOut.setPixel(x, y, Color.argb(A, R, G, B));
						
			}
		}
		*/
        return bitmapOut;
    }

    /**
     * Filter
     * Helligkeit anpassen.
     *
     * @param bitmapIn Eingangsbitmap
     * @param value    Helligkeitswert
     * @return Ausgabebitmap
     */
    public static Bitmap doBrightness(Bitmap bitmapIn, int value) {
        int width = bitmapIn.getWidth();
        int height = bitmapIn.getHeight();

        Bitmap bitmapOut = Bitmap.createBitmap(width, height, bitmapIn.getConfig());

        int A, R, G, B;
        int pixel;

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                pixel = bitmapIn.getPixel(x, y);
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);

                R += value;
                if (R > 255) R = 255;
                else if (R < 0) R = 0;

                G += value;
                if (G > 255) G = 255;
                else if (G < 0) G = 0;

                B += value;
                if (B > 255) B = 255;
                else if (B < 0) B = 0;

                bitmapOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        return bitmapOut;
    }

    /**
     * Effekt
     * Bilddrehung.
     *
     * @param bitmapIn Eingangsbitmap
     * @param degree   Drehwinkel (90,180,270)
     * @return Ausgabebitmap
     */
    public static Bitmap rotateImage(Bitmap bitmapIn, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);

        return Bitmap.createBitmap(bitmapIn, 0, 0, bitmapIn.getWidth(), bitmapIn.getHeight(), matrix, true);
    }

    /**
     * Filter
     * Farbfilter.
     *
     * @param bitmapIn Eingangsbitmap
     * @param red      Rotwert
     * @param green    Gr�nwert
     * @param blue     Blauwert
     * @return Ausgabebitmap
     */
    public static Bitmap doColorFilter(Bitmap bitmapIn, double red, double green, double blue) {
        int width = bitmapIn.getWidth();
        int height = bitmapIn.getHeight();

        Bitmap bitmapOut = Bitmap.createBitmap(width, height, bitmapIn.getConfig());

        int A, R, G, B;
        int pixel;

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                pixel = bitmapIn.getPixel(x, y);
                A = Color.alpha(pixel);
                R = (int) (Color.red(pixel) * red);
                G = (int) (Color.green(pixel) * green);
                B = (int) (Color.blue(pixel) * blue);
                bitmapOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        return bitmapOut;
    }

}
