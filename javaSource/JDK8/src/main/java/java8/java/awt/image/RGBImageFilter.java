/*
 * Copyright (c) 1995, 2013, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package java8.java.awt.image;

import java.awt.image.ColorModel;
import java.awt.image.FilteredImageSource;

/**
 * This class provides an easy way to create an ImageFilter which modifies
 * the pixels of an image in the default RGB ColorModel.  It is meant to
 * be used in conjunction with a FilteredImageSource object to produce
 * filtered versions of existing images.  It is an abstract class that
 * provides the calls needed to channel all of the pixel data through a
 * single method which converts pixels one at a time in the default RGB
 * ColorModel regardless of the ColorModel being used by the ImageProducer.
 * The only method which needs to be defined to create a useable image
 * filter is the filterRGB method.  Here is an example of a definition
 * of a filter which swaps the red and blue components of an image:
 * <pre>{@code
 *
 *      class RedBlueSwapFilter extends RGBImageFilter {
 *          public RedBlueSwapFilter() {
 *              // The filter's operation does not depend on the
 *              // pixel's location, so IndexColorModels can be
 *              // filtered directly.
 *              canFilterIndexColorModel = true;
 *          }
 *
 *          public int filterRGB(int x, int y, int rgb) {
 *              return ((rgb & 0xff00ff00)
 *                      | ((rgb & 0xff0000) >> 16)
 *                      | ((rgb & 0xff) << 16));
 *          }
 *      }
 *
 * }</pre>
 *
 * @see FilteredImageSource
 * @see ImageFilter
 * @see ColorModel#getRGBdefault
 *
 * @author      Jim Graham
 */
public abstract class RGBImageFilter extends ImageFilter {

    /**
     * The <code>ColorModel</code> to be replaced by
     * <code>newmodel</code> when the user calls
     * {@link #substituteColorModel(ColorModel, ColorModel) substituteColorModel}.
     */
    protected ColorModel origmodel;

    /**
     * The <code>ColorModel</code> with which to
     * replace <code>origmodel</code> when the user calls
     * <code>substituteColorModel</code>.
     */
    protected ColorModel newmodel;

    /**
     * This boolean indicates whether or not it is acceptable to apply
     * the color filtering of the filterRGB method to the color table
     * entries of an IndexColorModel object in lieu of pixel by pixel
     * filtering.  Subclasses should set this variable to true in their
     * constructor if their filterRGB method does not depend on the
     * coordinate of the pixel being filtered.
     * @see #substituteColorModel
     * @see #filterRGB
     * @see IndexColorModel
     */
    protected boolean canFilterIndexColorModel;

    /**
     * If the ColorModel is an IndexColorModel and the subclass has
     * set the canFilterIndexColorModel flag to true, we substitute
     * a filtered version of the color model here and wherever
     * that original ColorModel object appears in the setPixels methods.
     * If the ColorModel is not an IndexColorModel or is null, this method
     * overrides the default ColorModel used by the ImageProducer and
     * specifies the default RGB ColorModel instead.
     * <p>
     * Note: This method is intended to be called by the
     * <code>ImageProducer</code> of the <code>Image</code> whose pixels
     * are being filtered. Developers using
     * this class to filter pixels from an image should avoid calling
     * this method directly since that operation could interfere
     * with the filtering operation.
     * @see ImageConsumer
     * @see ColorModel#getRGBdefault
     */
    public void setColorModel(ColorModel model) {
        if (canFilterIndexColorModel && (model instanceof IndexColorModel)) {
            ColorModel newcm = filterIndexColorModel((IndexColorModel)model);
            substituteColorModel(model, newcm);
            consumer.setColorModel(newcm);
        } else {
            consumer.setColorModel(ColorModel.getRGBdefault());
        }
    }

    /**
     * Registers two ColorModel objects for substitution.  If the oldcm
     * is encountered during any of the setPixels methods, the newcm
     * is substituted and the pixels passed through
     * untouched (but with the new ColorModel object).
     * @param oldcm the ColorModel object to be replaced on the fly
     * @param newcm the ColorModel object to replace oldcm on the fly
     */
    public void substituteColorModel(ColorModel oldcm, ColorModel newcm) {
        origmodel = oldcm;
        newmodel = newcm;
    }

    /**
     * Filters an IndexColorModel object by running each entry in its
     * color tables through the filterRGB function that RGBImageFilter
     * subclasses must provide.  Uses coordinates of -1 to indicate that
     * a color table entry is being filtered rather than an actual
     * pixel value.
     * @param icm the IndexColorModel object to be filtered
     * @exception NullPointerException if <code>icm</code> is null
     * @return a new IndexColorModel representing the filtered colors
     */
    public IndexColorModel filterIndexColorModel(IndexColorModel icm) {
        int mapsize = icm.getMapSize();
        byte r[] = new byte[mapsize];
        byte g[] = new byte[mapsize];
        byte b[] = new byte[mapsize];
        byte a[] = new byte[mapsize];
        icm.getReds(r);
        icm.getGreens(g);
        icm.getBlues(b);
        icm.getAlphas(a);
        int trans = icm.getTransparentPixel();
        boolean needalpha = false;
        for (int i = 0; i < mapsize; i++) {
            int rgb = filterRGB(-1, -1, icm.getRGB(i));
            a[i] = (byte) (rgb >> 24);
            if (a[i] != ((byte)0xff) && i != trans) {
                needalpha = true;
            }
            r[i] = (byte) (rgb >> 16);
            g[i] = (byte) (rgb >> 8);
            b[i] = (byte) (rgb >> 0);
        }
        if (needalpha) {
            return new IndexColorModel(icm.getPixelSize(), mapsize,
                                       r, g, b, a);
        } else {
            return new IndexColorModel(icm.getPixelSize(), mapsize,
                                       r, g, b, trans);
        }
    }

    /**
     * Filters a buffer of pixels in the default RGB ColorModel by passing
     * them one by one through the filterRGB method.
     * @param x the X coordinate of the upper-left corner of the region
     *          of pixels
     * @param y the Y coordinate of the upper-left corner of the region
     *          of pixels
     * @param w the width of the region of pixels
     * @param h the height of the region of pixels
     * @param pixels the array of pixels
     * @param off the offset into the <code>pixels</code> array
     * @param scansize the distance from one row of pixels to the next
     *        in the array
     * @see ColorModel#getRGBdefault
     * @see #filterRGB
     */
    public void filterRGBPixels(int x, int y, int w, int h,
                                int pixels[], int off, int scansize) {
        int index = off;
        for (int cy = 0; cy < h; cy++) {
            for (int cx = 0; cx < w; cx++) {
                pixels[index] = filterRGB(x + cx, y + cy, pixels[index]);
                index++;
            }
            index += scansize - w;
        }
        consumer.setPixels(x, y, w, h, ColorModel.getRGBdefault(),
                           pixels, off, scansize);
    }

    /**
     * If the ColorModel object is the same one that has already
     * been converted, then simply passes the pixels through with the
     * converted ColorModel. Otherwise converts the buffer of byte
     * pixels to the default RGB ColorModel and passes the converted
     * buffer to the filterRGBPixels method to be converted one by one.
     * <p>
     * Note: This method is intended to be called by the
     * <code>ImageProducer</code> of the <code>Image</code> whose pixels
     * are being filtered. Developers using
     * this class to filter pixels from an image should avoid calling
     * this method directly since that operation could interfere
     * with the filtering operation.
     * @see ColorModel#getRGBdefault
     * @see #filterRGBPixels
     */
    public void setPixels(int x, int y, int w, int h,
                          ColorModel model, byte pixels[], int off,
                          int scansize) {
        if (model == origmodel) {
            consumer.setPixels(x, y, w, h, newmodel, pixels, off, scansize);
        } else {
            int filteredpixels[] = new int[w];
            int index = off;
            for (int cy = 0; cy < h; cy++) {
                for (int cx = 0; cx < w; cx++) {
                    filteredpixels[cx] = model.getRGB((pixels[index] & 0xff));
                    index++;
                }
                index += scansize - w;
                filterRGBPixels(x, y + cy, w, 1, filteredpixels, 0, w);
            }
        }
    }

    /**
     * If the ColorModel object is the same one that has already
     * been converted, then simply passes the pixels through with the
     * converted ColorModel, otherwise converts the buffer of integer
     * pixels to the default RGB ColorModel and passes the converted
     * buffer to the filterRGBPixels method to be converted one by one.
     * Converts a buffer of integer pixels to the default RGB ColorModel
     * and passes the converted buffer to the filterRGBPixels method.
     * <p>
     * Note: This method is intended to be called by the
     * <code>ImageProducer</code> of the <code>Image</code> whose pixels
     * are being filtered. Developers using
     * this class to filter pixels from an image should avoid calling
     * this method directly since that operation could interfere
     * with the filtering operation.
     * @see ColorModel#getRGBdefault
     * @see #filterRGBPixels
     */
    public void setPixels(int x, int y, int w, int h,
                          ColorModel model, int pixels[], int off,
                          int scansize) {
        if (model == origmodel) {
            consumer.setPixels(x, y, w, h, newmodel, pixels, off, scansize);
        } else {
            int filteredpixels[] = new int[w];
            int index = off;
            for (int cy = 0; cy < h; cy++) {
                for (int cx = 0; cx < w; cx++) {
                    filteredpixels[cx] = model.getRGB(pixels[index]);
                    index++;
                }
                index += scansize - w;
                filterRGBPixels(x, y + cy, w, 1, filteredpixels, 0, w);
            }
        }
    }

    /**
     * Subclasses must specify a method to convert a single input pixel
     * in the default RGB ColorModel to a single output pixel.
     * @param x the X coordinate of the pixel
     * @param y the Y coordinate of the pixel
     * @param rgb the integer pixel representation in the default RGB
     *            color model
     * @return a filtered pixel in the default RGB color model.
     * @see ColorModel#getRGBdefault
     * @see #filterRGBPixels
     */
    public abstract int filterRGB(int x, int y, int rgb);
}
