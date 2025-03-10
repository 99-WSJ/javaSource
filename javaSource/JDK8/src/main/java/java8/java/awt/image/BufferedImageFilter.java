/*
 * Copyright (c) 1997, 2000, Oracle and/or its affiliates. All rights reserved.
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

import java.awt.image.AffineTransformOp;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.DirectColorModel;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.ImagingOpException;
import java.awt.image.LookupOp;

/**
 * The <code>BufferedImageFilter</code> class subclasses an
 * <code>ImageFilter</code> to provide a simple means of
 * using a single-source/single-destination image operator
 * ({@link BufferedImageOp}) to filter a <code>BufferedImage</code>
 * in the Image Producer/Consumer/Observer
 * paradigm. Examples of these image operators are: {@link ConvolveOp},
 * {@link AffineTransformOp} and {@link LookupOp}.
 *
 * @see ImageFilter
 * @see BufferedImage
 * @see BufferedImageOp
 */

public class BufferedImageFilter extends ImageFilter implements Cloneable {
    BufferedImageOp bufferedImageOp;
    ColorModel model;
    int width;
    int height;
    byte[] bytePixels;
    int[] intPixels;

    /**
     * Constructs a <code>BufferedImageFilter</code> with the
     * specified single-source/single-destination operator.
     * @param op the specified <code>BufferedImageOp</code> to
     *           use to filter a <code>BufferedImage</code>
     * @throws NullPointerException if op is null
     */
    public BufferedImageFilter (BufferedImageOp op) {
        super();
        if (op == null) {
            throw new NullPointerException("Operation cannot be null");
        }
        bufferedImageOp = op;
    }

    /**
     * Returns the <code>BufferedImageOp</code>.
     * @return the operator of this <code>BufferedImageFilter</code>.
     */
    public BufferedImageOp getBufferedImageOp() {
        return bufferedImageOp;
    }

    /**
     * Filters the information provided in the
     * {@link ImageConsumer#setDimensions(int, int) setDimensions } method
     * of the {@link ImageConsumer} interface.
     * <p>
     * Note: This method is intended to be called by the
     * {@link ImageProducer} of the <code>Image</code> whose pixels are
     * being filtered. Developers using this class to retrieve pixels from
     * an image should avoid calling this method directly since that
     * operation could result in problems with retrieving the requested
     * pixels.
     * <p>
     * @param width the width to which to set the width of this
     *        <code>BufferedImageFilter</code>
     * @param height the height to which to set the height of this
     *        <code>BufferedImageFilter</code>
     * @see ImageConsumer#setDimensions
     */
    public void setDimensions(int width, int height) {
        if (width <= 0 || height <= 0) {
            imageComplete(STATICIMAGEDONE);
            return;
        }
        this.width  = width;
        this.height = height;
    }

    /**
     * Filters the information provided in the
     * {@link ImageConsumer#setColorModel(ColorModel) setColorModel} method
     * of the <code>ImageConsumer</code> interface.
     * <p>
     * If <code>model</code> is <code>null</code>, this
     * method clears the current <code>ColorModel</code> of this
     * <code>BufferedImageFilter</code>.
     * <p>
     * Note: This method is intended to be called by the
     * <code>ImageProducer</code> of the <code>Image</code>
     * whose pixels are being filtered.  Developers using this
     * class to retrieve pixels from an image
     * should avoid calling this method directly since that
     * operation could result in problems with retrieving the
     * requested pixels.
     * @param model the {@link ColorModel} to which to set the
     *        <code>ColorModel</code> of this <code>BufferedImageFilter</code>
     * @see ImageConsumer#setColorModel
     */
    public void setColorModel(ColorModel model) {
        this.model = model;
    }

    private void convertToRGB() {
        int size = width * height;
        int newpixels[] = new int[size];
        if (bytePixels != null) {
            for (int i = 0; i < size; i++) {
                newpixels[i] = this.model.getRGB(bytePixels[i] & 0xff);
            }
        } else if (intPixels != null) {
            for (int i = 0; i < size; i++) {
                newpixels[i] = this.model.getRGB(intPixels[i]);
            }
        }
        bytePixels = null;
        intPixels = newpixels;
        this.model = ColorModel.getRGBdefault();
    }

    /**
     * Filters the information provided in the <code>setPixels</code>
     * method of the <code>ImageConsumer</code> interface which takes
     * an array of bytes.
     * <p>
     * Note: This method is intended to be called by the
     * <code>ImageProducer</code> of the <code>Image</code> whose pixels
     * are being filtered.  Developers using
     * this class to retrieve pixels from an image should avoid calling
     * this method directly since that operation could result in problems
     * with retrieving the requested pixels.
     * @throws IllegalArgumentException if width or height are less than
     * zero.
     * @see ImageConsumer#setPixels(int, int, int, int, ColorModel, byte[],
                                    int, int)
     */
    public void setPixels(int x, int y, int w, int h,
                          ColorModel model, byte pixels[], int off,
                          int scansize) {
        // Fix 4184230
        if (w < 0 || h < 0) {
            throw new IllegalArgumentException("Width ("+w+
                                                ") and height ("+h+
                                                ") must be > 0");
        }
        // Nothing to do
        if (w == 0 || h == 0) {
            return;
        }
        if (y < 0) {
            int diff = -y;
            if (diff >= h) {
                return;
            }
            off += scansize * diff;
            y += diff;
            h -= diff;
        }
        if (y + h > height) {
            h = height - y;
            if (h <= 0) {
                return;
            }
        }
        if (x < 0) {
            int diff = -x;
            if (diff >= w) {
                return;
            }
            off += diff;
            x += diff;
            w -= diff;
        }
        if (x + w > width) {
            w = width - x;
            if (w <= 0) {
                return;
            }
        }
        int dstPtr = y*width + x;
        if (intPixels == null) {
            if (bytePixels == null) {
                bytePixels = new byte[width*height];
                this.model = model;
            } else if (this.model != model) {
                convertToRGB();
            }
            if (bytePixels != null) {
                for (int sh = h; sh > 0; sh--) {
                    System.arraycopy(pixels, off, bytePixels, dstPtr, w);
                    off += scansize;
                    dstPtr += width;
                }
            }
        }
        if (intPixels != null) {
            int dstRem = width - w;
            int srcRem = scansize - w;
            for (int sh = h; sh > 0; sh--) {
                for (int sw = w; sw > 0; sw--) {
                    intPixels[dstPtr++] = model.getRGB(pixels[off++]&0xff);
                }
                off    += srcRem;
                dstPtr += dstRem;
            }
        }
    }
    /**
     * Filters the information provided in the <code>setPixels</code>
     * method of the <code>ImageConsumer</code> interface which takes
     * an array of integers.
     * <p>
     * Note: This method is intended to be called by the
     * <code>ImageProducer</code> of the <code>Image</code> whose
     * pixels are being filtered.  Developers using this class to
     * retrieve pixels from an image should avoid calling this method
     * directly since that operation could result in problems
     * with retrieving the requested pixels.
     * @throws IllegalArgumentException if width or height are less than
     * zero.
     * @see ImageConsumer#setPixels(int, int, int, int, ColorModel, int[],
                                    int, int)
     */
    public void setPixels(int x, int y, int w, int h,
                          ColorModel model, int pixels[], int off,
                          int scansize) {
        // Fix 4184230
        if (w < 0 || h < 0) {
            throw new IllegalArgumentException("Width ("+w+
                                                ") and height ("+h+
                                                ") must be > 0");
        }
        // Nothing to do
        if (w == 0 || h == 0) {
            return;
        }
        if (y < 0) {
            int diff = -y;
            if (diff >= h) {
                return;
            }
            off += scansize * diff;
            y += diff;
            h -= diff;
        }
        if (y + h > height) {
            h = height - y;
            if (h <= 0) {
                return;
            }
        }
        if (x < 0) {
            int diff = -x;
            if (diff >= w) {
                return;
            }
            off += diff;
            x += diff;
            w -= diff;
        }
        if (x + w > width) {
            w = width - x;
            if (w <= 0) {
                return;
            }
        }

        if (intPixels == null) {
            if (bytePixels == null) {
                intPixels = new int[width * height];
                this.model = model;
            } else {
                convertToRGB();
            }
        }
        int dstPtr = y*width + x;
        if (this.model == model) {
            for (int sh = h; sh > 0; sh--) {
                System.arraycopy(pixels, off, intPixels, dstPtr, w);
                off += scansize;
                dstPtr += width;
            }
        } else {
            if (this.model != ColorModel.getRGBdefault()) {
                convertToRGB();
            }
            int dstRem = width - w;
            int srcRem = scansize - w;
            for (int sh = h; sh > 0; sh--) {
                for (int sw = w; sw > 0; sw--) {
                    intPixels[dstPtr++] = model.getRGB(pixels[off++]);
                }
                off += srcRem;
                dstPtr += dstRem;
            }
        }
    }

    /**
     * Filters the information provided in the <code>imageComplete</code>
     * method of the <code>ImageConsumer</code> interface.
     * <p>
     * Note: This method is intended to be called by the
     * <code>ImageProducer</code> of the <code>Image</code> whose pixels
     * are being filtered.  Developers using
     * this class to retrieve pixels from an image should avoid calling
     * this method directly since that operation could result in problems
     * with retrieving the requested pixels.
     * @param status the status of image loading
     * @throws ImagingOpException if there was a problem calling the filter
     * method of the <code>BufferedImageOp</code> associated with this
     * instance.
     * @see ImageConsumer#imageComplete
     */
    public void imageComplete(int status) {
        WritableRaster wr;
        switch(status) {
        case IMAGEERROR:
        case IMAGEABORTED:
            // reinitialize the params
            model  = null;
            width  = -1;
            height = -1;
            intPixels  = null;
            bytePixels = null;
            break;

        case SINGLEFRAMEDONE:
        case STATICIMAGEDONE:
            if (width <= 0 || height <= 0) break;
            if (model instanceof DirectColorModel) {
                if (intPixels == null) break;
                wr = createDCMraster();
            }
            else if (model instanceof IndexColorModel) {
                int[] bandOffsets = {0};
                if (bytePixels == null) break;
                DataBufferByte db = new DataBufferByte(bytePixels,
                                                       width*height);
                wr = Raster.createInterleavedRaster(db, width, height, width,
                                                    1, bandOffsets, null);
            }
            else {
                convertToRGB();
                if (intPixels == null) break;
                wr = createDCMraster();
            }
            BufferedImage bi = new BufferedImage(model, wr,
                                                 model.isAlphaPremultiplied(),
                                                 null);
            bi = bufferedImageOp.filter(bi, null);
            WritableRaster r = bi.getRaster();
            ColorModel cm = bi.getColorModel();
            int w = r.getWidth();
            int h = r.getHeight();
            consumer.setDimensions(w, h);
            consumer.setColorModel(cm);
            if (cm instanceof DirectColorModel) {
                DataBufferInt db = (DataBufferInt) r.getDataBuffer();
                consumer.setPixels(0, 0, w, h,
                                   cm, db.getData(), 0, w);
            }
            else if (cm instanceof IndexColorModel) {
                DataBufferByte db = (DataBufferByte) r.getDataBuffer();
                consumer.setPixels(0, 0, w, h,
                                   cm, db.getData(), 0, w);
            }
            else {
                throw new InternalError("Unknown color model "+cm);
            }
            break;
        }
        consumer.imageComplete(status);
    }

    private final WritableRaster createDCMraster() {
        WritableRaster wr;
        DirectColorModel dcm = (DirectColorModel) model;
        boolean hasAlpha = model.hasAlpha();
        int[] bandMasks = new int[3+(hasAlpha ? 1 : 0)];
        bandMasks[0] = dcm.getRedMask();
        bandMasks[1] = dcm.getGreenMask();
        bandMasks[2] = dcm.getBlueMask();
        if (hasAlpha) {
            bandMasks[3] = dcm.getAlphaMask();
        }
        DataBufferInt db = new DataBufferInt(intPixels, width*height);
        wr = Raster.createPackedRaster(db, width, height, width,
                                       bandMasks, null);
        return wr;
    }

}
