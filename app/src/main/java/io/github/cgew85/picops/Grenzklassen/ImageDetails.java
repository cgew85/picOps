package io.github.cgew85.picops.Grenzklassen;

/**
 * The Class imageDetails
 * <p>
 * Speichern bildbezogener Daten
 */

public class ImageDetails {

    /**
     * The image height.
     */
    private int imageHeight;

    /**
     * The image width.
     */
    private int imageWidth;

    /**
     * The image type.
     */
    private String imageType;

    /**
     * Instantiates a new image details.
     */
    public ImageDetails() {
    }

    /**
     * Gets the image height.
     *
     * @return the image height
     */
    public int getImageHeight() {
        return imageHeight;
    }

    /**
     * Sets the image height.
     *
     * @param imageHeight the new image height
     */
    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    /**
     * Gets the image width.
     *
     * @return the image width
     */
    public int getImageWidth() {
        return imageWidth;
    }

    /**
     * Sets the image width.
     *
     * @param imageWidth the new image width
     */
    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    /**
     * Gets the image type.
     *
     * @return the image type
     */
    public String getImageType() {
        return imageType;
    }

    /**
     * Sets the image type.
     *
     * @param imageType the new image type
     */
    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

}
