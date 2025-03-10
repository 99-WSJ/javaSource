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

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DirectColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.math.BigInteger;

/**
 * The <code>IndexColorModel</code> class is a <code>ColorModel</code>
 * class that works with pixel values consisting of a
 * single sample that is an index into a fixed colormap in the default
 * sRGB color space.  The colormap specifies red, green, blue, and
 * optional alpha components corresponding to each index.  All components
 * are represented in the colormap as 8-bit unsigned integral values.
 * Some constructors allow the caller to specify "holes" in the colormap
 * by indicating which colormap entries are valid and which represent
 * unusable colors via the bits set in a <code>BigInteger</code> object.
 * This color model is similar to an X11 PseudoColor visual.
 * <p>
 * Some constructors provide a means to specify an alpha component
 * for each pixel in the colormap, while others either provide no
 * such means or, in some cases, a flag to indicate whether the
 * colormap data contains alpha values.  If no alpha is supplied to
 * the constructor, an opaque alpha component (alpha = 1.0) is
 * assumed for each entry.
 * An optional transparent pixel value can be supplied that indicates a
 * pixel to be made completely transparent, regardless of any alpha
 * component supplied or assumed for that pixel value.
 * Note that the color components in the colormap of an
 * <code>IndexColorModel</code> objects are never pre-multiplied with
 * the alpha components.
 * <p>
 * <a name="transparency">
 * The transparency of an <code>IndexColorModel</code> object is
 * determined by examining the alpha components of the colors in the
 * colormap and choosing the most specific value after considering
 * the optional alpha values and any transparent index specified.
 * The transparency value is <code>Transparency.OPAQUE</code>
 * only if all valid colors in
 * the colormap are opaque and there is no valid transparent pixel.
 * If all valid colors
 * in the colormap are either completely opaque (alpha = 1.0) or
 * completely transparent (alpha = 0.0), which typically occurs when
 * a valid transparent pixel is specified,
 * the value is <code>Transparency.BITMASK</code>.
 * Otherwise, the value is <code>Transparency.TRANSLUCENT</code>, indicating
 * that some valid color has an alpha component that is
 * neither completely transparent nor completely opaque
 * (0.0 &lt; alpha &lt; 1.0).
 * </a>
 *
 * <p>
 * If an <code>IndexColorModel</code> object has
 * a transparency value of <code>Transparency.OPAQUE</code>,
 * then the <code>hasAlpha</code>
 * and <code>getNumComponents</code> methods
 * (both inherited from <code>ColorModel</code>)
 * return false and 3, respectively.
 * For any other transparency value,
 * <code>hasAlpha</code> returns true
 * and <code>getNumComponents</code> returns 4.
 *
 * <p>
 * <a name="index_values">
 * The values used to index into the colormap are taken from the least
 * significant <em>n</em> bits of pixel representations where
 * <em>n</em> is based on the pixel size specified in the constructor.
 * For pixel sizes smaller than 8 bits, <em>n</em> is rounded up to a
 * power of two (3 becomes 4 and 5,6,7 become 8).
 * For pixel sizes between 8 and 16 bits, <em>n</em> is equal to the
 * pixel size.
 * Pixel sizes larger than 16 bits are not supported by this class.
 * Higher order bits beyond <em>n</em> are ignored in pixel representations.
 * Index values greater than or equal to the map size, but less than
 * 2<sup><em>n</em></sup>, are undefined and return 0 for all color and
 * alpha components.
 * </a>
 * <p>
 * For those methods that use a primitive array pixel representation of
 * type <code>transferType</code>, the array length is always one.
 * The transfer types supported are <code>DataBuffer.TYPE_BYTE</code> and
 * <code>DataBuffer.TYPE_USHORT</code>.  A single int pixel
 * representation is valid for all objects of this class, since it is
 * always possible to represent pixel values used with this class in a
 * single int.  Therefore, methods that use this representation do
 * not throw an <code>IllegalArgumentException</code> due to an invalid
 * pixel value.
 * <p>
 * Many of the methods in this class are final.  The reason for
 * this is that the underlying native graphics code makes assumptions
 * about the layout and operation of this class and those assumptions
 * are reflected in the implementations of the methods here that are
 * marked final.  You can subclass this class for other reasons, but
 * you cannot override or modify the behaviour of those methods.
 *
 * @see ColorModel
 * @see ColorSpace
 * @see DataBuffer
 *
 */
public class IndexColorModel extends ColorModel {
    private int rgb[];
    private int map_size;
    private int pixel_mask;
    private int transparent_index = -1;
    private boolean allgrayopaque;
    private BigInteger validBits;

    private sun.awt.image.BufImgSurfaceData.ICMColorData colorData = null;

    private static int[] opaqueBits = {8, 8, 8};
    private static int[] alphaBits = {8, 8, 8, 8};

    static private native void initIDs();
    static {
        ColorModel.loadLibraries();
        initIDs();
    }
    /**
     * Constructs an <code>IndexColorModel</code> from the specified
     * arrays of red, green, and blue components.  Pixels described
     * by this color model all have alpha components of 255
     * unnormalized (1.0&nbsp;normalized), which means they
     * are fully opaque.  All of the arrays specifying the color
     * components must have at least the specified number of entries.
     * The <code>ColorSpace</code> is the default sRGB space.
     * Since there is no alpha information in any of the arguments
     * to this constructor, the transparency value is always
     * <code>Transparency.OPAQUE</code>.
     * The transfer type is the smallest of <code>DataBuffer.TYPE_BYTE</code>
     * or <code>DataBuffer.TYPE_USHORT</code> that can hold a single pixel.
     * @param bits      the number of bits each pixel occupies
     * @param size      the size of the color component arrays
     * @param r         the array of red color components
     * @param g         the array of green color components
     * @param b         the array of blue color components
     * @throws IllegalArgumentException if <code>bits</code> is less
     *         than 1 or greater than 16
     * @throws IllegalArgumentException if <code>size</code> is less
     *         than 1
     */
    public IndexColorModel(int bits, int size,
                           byte r[], byte g[], byte b[]) {
        super(bits, opaqueBits,
              ColorSpace.getInstance(ColorSpace.CS_sRGB),
              false, false, OPAQUE,
              ColorModel.getDefaultTransferType(bits));
        if (bits < 1 || bits > 16) {
            throw new IllegalArgumentException("Number of bits must be between"
                                               +" 1 and 16.");
        }
        setRGBs(size, r, g, b, null);
        calculatePixelMask();
    }

    /**
     * Constructs an <code>IndexColorModel</code> from the given arrays
     * of red, green, and blue components.  Pixels described by this color
     * model all have alpha components of 255 unnormalized
     * (1.0&nbsp;normalized), which means they are fully opaque, except
     * for the indicated pixel to be made transparent.  All of the arrays
     * specifying the color components must have at least the specified
     * number of entries.
     * The <code>ColorSpace</code> is the default sRGB space.
     * The transparency value may be <code>Transparency.OPAQUE</code> or
     * <code>Transparency.BITMASK</code> depending on the arguments, as
     * specified in the <a href="#transparency">class description</a> above.
     * The transfer type is the smallest of <code>DataBuffer.TYPE_BYTE</code>
     * or <code>DataBuffer.TYPE_USHORT</code> that can hold a
     * single pixel.
     * @param bits      the number of bits each pixel occupies
     * @param size      the size of the color component arrays
     * @param r         the array of red color components
     * @param g         the array of green color components
     * @param b         the array of blue color components
     * @param trans     the index of the transparent pixel
     * @throws IllegalArgumentException if <code>bits</code> is less than
     *          1 or greater than 16
     * @throws IllegalArgumentException if <code>size</code> is less than
     *          1
     */
    public IndexColorModel(int bits, int size,
                           byte r[], byte g[], byte b[], int trans) {
        super(bits, opaqueBits,
              ColorSpace.getInstance(ColorSpace.CS_sRGB),
              false, false, OPAQUE,
              ColorModel.getDefaultTransferType(bits));
        if (bits < 1 || bits > 16) {
            throw new IllegalArgumentException("Number of bits must be between"
                                               +" 1 and 16.");
        }
        setRGBs(size, r, g, b, null);
        setTransparentPixel(trans);
        calculatePixelMask();
    }

    /**
     * Constructs an <code>IndexColorModel</code> from the given
     * arrays of red, green, blue and alpha components.  All of the
     * arrays specifying the components must have at least the specified
     * number of entries.
     * The <code>ColorSpace</code> is the default sRGB space.
     * The transparency value may be any of <code>Transparency.OPAQUE</code>,
     * <code>Transparency.BITMASK</code>,
     * or <code>Transparency.TRANSLUCENT</code>
     * depending on the arguments, as specified
     * in the <a href="#transparency">class description</a> above.
     * The transfer type is the smallest of <code>DataBuffer.TYPE_BYTE</code>
     * or <code>DataBuffer.TYPE_USHORT</code> that can hold a single pixel.
     * @param bits      the number of bits each pixel occupies
     * @param size      the size of the color component arrays
     * @param r         the array of red color components
     * @param g         the array of green color components
     * @param b         the array of blue color components
     * @param a         the array of alpha value components
     * @throws IllegalArgumentException if <code>bits</code> is less
     *           than 1 or greater than 16
     * @throws IllegalArgumentException if <code>size</code> is less
     *           than 1
     */
    public IndexColorModel(int bits, int size,
                           byte r[], byte g[], byte b[], byte a[]) {
        super (bits, alphaBits,
               ColorSpace.getInstance(ColorSpace.CS_sRGB),
               true, false, TRANSLUCENT,
               ColorModel.getDefaultTransferType(bits));
        if (bits < 1 || bits > 16) {
            throw new IllegalArgumentException("Number of bits must be between"
                                               +" 1 and 16.");
        }
        setRGBs (size, r, g, b, a);
        calculatePixelMask();
    }

    /**
     * Constructs an <code>IndexColorModel</code> from a single
     * array of interleaved red, green, blue and optional alpha
     * components.  The array must have enough values in it to
     * fill all of the needed component arrays of the specified
     * size.  The <code>ColorSpace</code> is the default sRGB space.
     * The transparency value may be any of <code>Transparency.OPAQUE</code>,
     * <code>Transparency.BITMASK</code>,
     * or <code>Transparency.TRANSLUCENT</code>
     * depending on the arguments, as specified
     * in the <a href="#transparency">class description</a> above.
     * The transfer type is the smallest of
     * <code>DataBuffer.TYPE_BYTE</code> or <code>DataBuffer.TYPE_USHORT</code>
     * that can hold a single pixel.
     *
     * @param bits      the number of bits each pixel occupies
     * @param size      the size of the color component arrays
     * @param cmap      the array of color components
     * @param start     the starting offset of the first color component
     * @param hasalpha  indicates whether alpha values are contained in
     *                  the <code>cmap</code> array
     * @throws IllegalArgumentException if <code>bits</code> is less
     *           than 1 or greater than 16
     * @throws IllegalArgumentException if <code>size</code> is less
     *           than 1
     */
    public IndexColorModel(int bits, int size, byte cmap[], int start,
                           boolean hasalpha) {
        this(bits, size, cmap, start, hasalpha, -1);
        if (bits < 1 || bits > 16) {
            throw new IllegalArgumentException("Number of bits must be between"
                                               +" 1 and 16.");
        }
    }

    /**
     * Constructs an <code>IndexColorModel</code> from a single array of
     * interleaved red, green, blue and optional alpha components.  The
     * specified transparent index represents a pixel that is made
     * entirely transparent regardless of any alpha value specified
     * for it.  The array must have enough values in it to fill all
     * of the needed component arrays of the specified size.
     * The <code>ColorSpace</code> is the default sRGB space.
     * The transparency value may be any of <code>Transparency.OPAQUE</code>,
     * <code>Transparency.BITMASK</code>,
     * or <code>Transparency.TRANSLUCENT</code>
     * depending on the arguments, as specified
     * in the <a href="#transparency">class description</a> above.
     * The transfer type is the smallest of
     * <code>DataBuffer.TYPE_BYTE</code> or <code>DataBuffer.TYPE_USHORT</code>
     * that can hold a single pixel.
     * @param bits      the number of bits each pixel occupies
     * @param size      the size of the color component arrays
     * @param cmap      the array of color components
     * @param start     the starting offset of the first color component
     * @param hasalpha  indicates whether alpha values are contained in
     *                  the <code>cmap</code> array
     * @param trans     the index of the fully transparent pixel
     * @throws IllegalArgumentException if <code>bits</code> is less than
     *               1 or greater than 16
     * @throws IllegalArgumentException if <code>size</code> is less than
     *               1
     */
    public IndexColorModel(int bits, int size, byte cmap[], int start,
                           boolean hasalpha, int trans) {
        // REMIND: This assumes the ordering: RGB[A]
        super(bits, opaqueBits,
              ColorSpace.getInstance(ColorSpace.CS_sRGB),
              false, false, OPAQUE,
              ColorModel.getDefaultTransferType(bits));

        if (bits < 1 || bits > 16) {
            throw new IllegalArgumentException("Number of bits must be between"
                                               +" 1 and 16.");
        }
        if (size < 1) {
            throw new IllegalArgumentException("Map size ("+size+
                                               ") must be >= 1");
        }
        map_size = size;
        rgb = new int[calcRealMapSize(bits, size)];
        int j = start;
        int alpha = 0xff;
        boolean allgray = true;
        int transparency = OPAQUE;
        for (int i = 0; i < size; i++) {
            int r = cmap[j++] & 0xff;
            int g = cmap[j++] & 0xff;
            int b = cmap[j++] & 0xff;
            allgray = allgray && (r == g) && (g == b);
            if (hasalpha) {
                alpha = cmap[j++] & 0xff;
                if (alpha != 0xff) {
                    if (alpha == 0x00) {
                        if (transparency == OPAQUE) {
                            transparency = BITMASK;
                        }
                        if (transparent_index < 0) {
                            transparent_index = i;
                        }
                    } else {
                        transparency = TRANSLUCENT;
                    }
                    allgray = false;
                }
            }
            rgb[i] = (alpha << 24) | (r << 16) | (g << 8) | b;
        }
        this.allgrayopaque = allgray;
        setTransparency(transparency);
        setTransparentPixel(trans);
        calculatePixelMask();
    }

    /**
     * Constructs an <code>IndexColorModel</code> from an array of
     * ints where each int is comprised of red, green, blue, and
     * optional alpha components in the default RGB color model format.
     * The specified transparent index represents a pixel that is made
     * entirely transparent regardless of any alpha value specified
     * for it.  The array must have enough values in it to fill all
     * of the needed component arrays of the specified size.
     * The <code>ColorSpace</code> is the default sRGB space.
     * The transparency value may be any of <code>Transparency.OPAQUE</code>,
     * <code>Transparency.BITMASK</code>,
     * or <code>Transparency.TRANSLUCENT</code>
     * depending on the arguments, as specified
     * in the <a href="#transparency">class description</a> above.
     * @param bits      the number of bits each pixel occupies
     * @param size      the size of the color component arrays
     * @param cmap      the array of color components
     * @param start     the starting offset of the first color component
     * @param hasalpha  indicates whether alpha values are contained in
     *                  the <code>cmap</code> array
     * @param trans     the index of the fully transparent pixel
     * @param transferType the data type of the array used to represent
     *           pixel values.  The data type must be either
     *           <code>DataBuffer.TYPE_BYTE</code> or
     *           <code>DataBuffer.TYPE_USHORT</code>.
     * @throws IllegalArgumentException if <code>bits</code> is less
     *           than 1 or greater than 16
     * @throws IllegalArgumentException if <code>size</code> is less
     *           than 1
     * @throws IllegalArgumentException if <code>transferType</code> is not
     *           one of <code>DataBuffer.TYPE_BYTE</code> or
     *           <code>DataBuffer.TYPE_USHORT</code>
     */
    public IndexColorModel(int bits, int size,
                           int cmap[], int start,
                           boolean hasalpha, int trans, int transferType) {
        // REMIND: This assumes the ordering: RGB[A]
        super(bits, opaqueBits,
              ColorSpace.getInstance(ColorSpace.CS_sRGB),
              false, false, OPAQUE,
              transferType);

        if (bits < 1 || bits > 16) {
            throw new IllegalArgumentException("Number of bits must be between"
                                               +" 1 and 16.");
        }
        if (size < 1) {
            throw new IllegalArgumentException("Map size ("+size+
                                               ") must be >= 1");
        }
        if ((transferType != DataBuffer.TYPE_BYTE) &&
            (transferType != DataBuffer.TYPE_USHORT)) {
            throw new IllegalArgumentException("transferType must be either" +
                "DataBuffer.TYPE_BYTE or DataBuffer.TYPE_USHORT");
        }

        setRGBs(size, cmap, start, hasalpha);
        setTransparentPixel(trans);
        calculatePixelMask();
    }

    /**
     * Constructs an <code>IndexColorModel</code> from an
     * <code>int</code> array where each <code>int</code> is
     * comprised of red, green, blue, and alpha
     * components in the default RGB color model format.
     * The array must have enough values in it to fill all
     * of the needed component arrays of the specified size.
     * The <code>ColorSpace</code> is the default sRGB space.
     * The transparency value may be any of <code>Transparency.OPAQUE</code>,
     * <code>Transparency.BITMASK</code>,
     * or <code>Transparency.TRANSLUCENT</code>
     * depending on the arguments, as specified
     * in the <a href="#transparency">class description</a> above.
     * The transfer type must be one of <code>DataBuffer.TYPE_BYTE</code>
     * <code>DataBuffer.TYPE_USHORT</code>.
     * The <code>BigInteger</code> object specifies the valid/invalid pixels
     * in the <code>cmap</code> array.  A pixel is valid if the
     * <code>BigInteger</code> value at that index is set, and is invalid
     * if the <code>BigInteger</code> bit  at that index is not set.
     * @param bits the number of bits each pixel occupies
     * @param size the size of the color component array
     * @param cmap the array of color components
     * @param start the starting offset of the first color component
     * @param transferType the specified data type
     * @param validBits a <code>BigInteger</code> object.  If a bit is
     *    set in the BigInteger, the pixel at that index is valid.
     *    If a bit is not set, the pixel at that index
     *    is considered invalid.  If null, all pixels are valid.
     *    Only bits from 0 to the map size are considered.
     * @throws IllegalArgumentException if <code>bits</code> is less
     *           than 1 or greater than 16
     * @throws IllegalArgumentException if <code>size</code> is less
     *           than 1
     * @throws IllegalArgumentException if <code>transferType</code> is not
     *           one of <code>DataBuffer.TYPE_BYTE</code> or
     *           <code>DataBuffer.TYPE_USHORT</code>
     *
     * @since 1.3
     */
    public IndexColorModel(int bits, int size, int cmap[], int start,
                           int transferType, BigInteger validBits) {
        super (bits, alphaBits,
               ColorSpace.getInstance(ColorSpace.CS_sRGB),
               true, false, TRANSLUCENT,
               transferType);

        if (bits < 1 || bits > 16) {
            throw new IllegalArgumentException("Number of bits must be between"
                                               +" 1 and 16.");
        }
        if (size < 1) {
            throw new IllegalArgumentException("Map size ("+size+
                                               ") must be >= 1");
        }
        if ((transferType != DataBuffer.TYPE_BYTE) &&
            (transferType != DataBuffer.TYPE_USHORT)) {
            throw new IllegalArgumentException("transferType must be either" +
                "DataBuffer.TYPE_BYTE or DataBuffer.TYPE_USHORT");
        }

        if (validBits != null) {
            // Check to see if it is all valid
            for (int i=0; i < size; i++) {
                if (!validBits.testBit(i)) {
                    this.validBits = validBits;
                    break;
                }
            }
        }

        setRGBs(size, cmap, start, true);
        calculatePixelMask();
    }

    private void setRGBs(int size, byte r[], byte g[], byte b[], byte a[]) {
        if (size < 1) {
            throw new IllegalArgumentException("Map size ("+size+
                                               ") must be >= 1");
        }
        map_size = size;
        rgb = new int[calcRealMapSize(pixel_bits, size)];
        int alpha = 0xff;
        int transparency = OPAQUE;
        boolean allgray = true;
        for (int i = 0; i < size; i++) {
            int rc = r[i] & 0xff;
            int gc = g[i] & 0xff;
            int bc = b[i] & 0xff;
            allgray = allgray && (rc == gc) && (gc == bc);
            if (a != null) {
                alpha = a[i] & 0xff;
                if (alpha != 0xff) {
                    if (alpha == 0x00) {
                        if (transparency == OPAQUE) {
                            transparency = BITMASK;
                        }
                        if (transparent_index < 0) {
                            transparent_index = i;
                        }
                    } else {
                        transparency = TRANSLUCENT;
                    }
                    allgray = false;
                }
            }
            rgb[i] = (alpha << 24) | (rc << 16) | (gc << 8) | bc;
        }
        this.allgrayopaque = allgray;
        setTransparency(transparency);
    }

    private void setRGBs(int size, int cmap[], int start, boolean hasalpha) {
        map_size = size;
        rgb = new int[calcRealMapSize(pixel_bits, size)];
        int j = start;
        int transparency = OPAQUE;
        boolean allgray = true;
        BigInteger validBits = this.validBits;
        for (int i = 0; i < size; i++, j++) {
            if (validBits != null && !validBits.testBit(i)) {
                continue;
            }
            int cmaprgb = cmap[j];
            int r = (cmaprgb >> 16) & 0xff;
            int g = (cmaprgb >>  8) & 0xff;
            int b = (cmaprgb      ) & 0xff;
            allgray = allgray && (r == g) && (g == b);
            if (hasalpha) {
                int alpha = cmaprgb >>> 24;
                if (alpha != 0xff) {
                    if (alpha == 0x00) {
                        if (transparency == OPAQUE) {
                            transparency = BITMASK;
                        }
                        if (transparent_index < 0) {
                            transparent_index = i;
                        }
                    } else {
                        transparency = TRANSLUCENT;
                    }
                    allgray = false;
                }
            } else {
                cmaprgb |= 0xff000000;
            }
            rgb[i] = cmaprgb;
        }
        this.allgrayopaque = allgray;
        setTransparency(transparency);
    }

    private int calcRealMapSize(int bits, int size) {
        int newSize = Math.max(1 << bits, size);
        return Math.max(newSize, 256);
    }

    private BigInteger getAllValid() {
        int numbytes = (map_size+7)/8;
        byte[] valid = new byte[numbytes];
        java.util.Arrays.fill(valid, (byte)0xff);
        valid[0] = (byte)(0xff >>> (numbytes*8 - map_size));

        return new BigInteger(1, valid);
    }

    /**
     * Returns the transparency.  Returns either OPAQUE, BITMASK,
     * or TRANSLUCENT
     * @return the transparency of this <code>IndexColorModel</code>
     * @see Transparency#OPAQUE
     * @see Transparency#BITMASK
     * @see Transparency#TRANSLUCENT
     */
    public int getTransparency() {
        return transparency;
    }

    /**
     * Returns an array of the number of bits for each color/alpha component.
     * The array contains the color components in the order red, green,
     * blue, followed by the alpha component, if present.
     * @return an array containing the number of bits of each color
     *         and alpha component of this <code>IndexColorModel</code>
     */
    public int[] getComponentSize() {
        if (nBits == null) {
            if (supportsAlpha) {
                nBits = new int[4];
                nBits[3] = 8;
            }
            else {
                nBits = new int[3];
            }
            nBits[0] = nBits[1] = nBits[2] = 8;
        }
        return nBits.clone();
    }

    /**
     * Returns the size of the color/alpha component arrays in this
     * <code>IndexColorModel</code>.
     * @return the size of the color and alpha component arrays.
     */
    final public int getMapSize() {
        return map_size;
    }

    /**
     * Returns the index of a transparent pixel in this
     * <code>IndexColorModel</code> or -1 if there is no pixel
     * with an alpha value of 0.  If a transparent pixel was
     * explicitly specified in one of the constructors by its
     * index, then that index will be preferred, otherwise,
     * the index of any pixel which happens to be fully transparent
     * may be returned.
     * @return the index of a transparent pixel in this
     *         <code>IndexColorModel</code> object, or -1 if there
     *         is no such pixel
     */
    final public int getTransparentPixel() {
        return transparent_index;
    }

    /**
     * Copies the array of red color components into the specified array.
     * Only the initial entries of the array as specified by
     * {@link #getMapSize() getMapSize} are written.
     * @param r the specified array into which the elements of the
     *      array of red color components are copied
     */
    final public void getReds(byte r[]) {
        for (int i = 0; i < map_size; i++) {
            r[i] = (byte) (rgb[i] >> 16);
        }
    }

    /**
     * Copies the array of green color components into the specified array.
     * Only the initial entries of the array as specified by
     * <code>getMapSize</code> are written.
     * @param g the specified array into which the elements of the
     *      array of green color components are copied
     */
    final public void getGreens(byte g[]) {
        for (int i = 0; i < map_size; i++) {
            g[i] = (byte) (rgb[i] >> 8);
        }
    }

    /**
     * Copies the array of blue color components into the specified array.
     * Only the initial entries of the array as specified by
     * <code>getMapSize</code> are written.
     * @param b the specified array into which the elements of the
     *      array of blue color components are copied
     */
    final public void getBlues(byte b[]) {
        for (int i = 0; i < map_size; i++) {
            b[i] = (byte) rgb[i];
        }
    }

    /**
     * Copies the array of alpha transparency components into the
     * specified array.  Only the initial entries of the array as specified
     * by <code>getMapSize</code> are written.
     * @param a the specified array into which the elements of the
     *      array of alpha components are copied
     */
    final public void getAlphas(byte a[]) {
        for (int i = 0; i < map_size; i++) {
            a[i] = (byte) (rgb[i] >> 24);
        }
    }

    /**
     * Converts data for each index from the color and alpha component
     * arrays to an int in the default RGB ColorModel format and copies
     * the resulting 32-bit ARGB values into the specified array.  Only
     * the initial entries of the array as specified by
     * <code>getMapSize</code> are
     * written.
     * @param rgb the specified array into which the converted ARGB
     *        values from this array of color and alpha components
     *        are copied.
     */
    final public void getRGBs(int rgb[]) {
        System.arraycopy(this.rgb, 0, rgb, 0, map_size);
    }

    private void setTransparentPixel(int trans) {
        if (trans >= 0 && trans < map_size) {
            rgb[trans] &= 0x00ffffff;
            transparent_index = trans;
            allgrayopaque = false;
            if (this.transparency == OPAQUE) {
                setTransparency(BITMASK);
            }
        }
    }

    private void setTransparency(int transparency) {
        if (this.transparency != transparency) {
            this.transparency = transparency;
            if (transparency == OPAQUE) {
                supportsAlpha = false;
                numComponents = 3;
                nBits = opaqueBits;
            } else {
                supportsAlpha = true;
                numComponents = 4;
                nBits = alphaBits;
            }
        }
    }

    /**
     * This method is called from the constructors to set the pixel_mask
     * value, which is based on the value of pixel_bits.  The pixel_mask
     * value is used to mask off the pixel parameters for methods such
     * as getRed(), getGreen(), getBlue(), getAlpha(), and getRGB().
     */
    private final void calculatePixelMask() {
        // Note that we adjust the mask so that our masking behavior here
        // is consistent with that of our native rendering loops.
        int maskbits = pixel_bits;
        if (maskbits == 3) {
            maskbits = 4;
        } else if (maskbits > 4 && maskbits < 8) {
            maskbits = 8;
        }
        pixel_mask = (1 << maskbits) - 1;
    }

    /**
     * Returns the red color component for the specified pixel, scaled
     * from 0 to 255 in the default RGB ColorSpace, sRGB.  The pixel value
     * is specified as an int.
     * Only the lower <em>n</em> bits of the pixel value, as specified in the
     * <a href="#index_values">class description</a> above, are used to
     * calculate the returned value.
     * The returned value is a non pre-multiplied value.
     * @param pixel the specified pixel
     * @return the value of the red color component for the specified pixel
     */
    final public int getRed(int pixel) {
        return (rgb[pixel & pixel_mask] >> 16) & 0xff;
    }

    /**
     * Returns the green color component for the specified pixel, scaled
     * from 0 to 255 in the default RGB ColorSpace, sRGB.  The pixel value
     * is specified as an int.
     * Only the lower <em>n</em> bits of the pixel value, as specified in the
     * <a href="#index_values">class description</a> above, are used to
     * calculate the returned value.
     * The returned value is a non pre-multiplied value.
     * @param pixel the specified pixel
     * @return the value of the green color component for the specified pixel
     */
    final public int getGreen(int pixel) {
        return (rgb[pixel & pixel_mask] >> 8) & 0xff;
    }

    /**
     * Returns the blue color component for the specified pixel, scaled
     * from 0 to 255 in the default RGB ColorSpace, sRGB.  The pixel value
     * is specified as an int.
     * Only the lower <em>n</em> bits of the pixel value, as specified in the
     * <a href="#index_values">class description</a> above, are used to
     * calculate the returned value.
     * The returned value is a non pre-multiplied value.
     * @param pixel the specified pixel
     * @return the value of the blue color component for the specified pixel
     */
    final public int getBlue(int pixel) {
        return rgb[pixel & pixel_mask] & 0xff;
    }

    /**
     * Returns the alpha component for the specified pixel, scaled
     * from 0 to 255.  The pixel value is specified as an int.
     * Only the lower <em>n</em> bits of the pixel value, as specified in the
     * <a href="#index_values">class description</a> above, are used to
     * calculate the returned value.
     * @param pixel the specified pixel
     * @return the value of the alpha component for the specified pixel
     */
    final public int getAlpha(int pixel) {
        return (rgb[pixel & pixel_mask] >> 24) & 0xff;
    }

    /**
     * Returns the color/alpha components of the pixel in the default
     * RGB color model format.  The pixel value is specified as an int.
     * Only the lower <em>n</em> bits of the pixel value, as specified in the
     * <a href="#index_values">class description</a> above, are used to
     * calculate the returned value.
     * The returned value is in a non pre-multiplied format.
     * @param pixel the specified pixel
     * @return the color and alpha components of the specified pixel
     * @see ColorModel#getRGBdefault
     */
    final public int getRGB(int pixel) {
        return rgb[pixel & pixel_mask];
    }

    private static final int CACHESIZE = 40;
    private int lookupcache[] = new int[CACHESIZE];

    /**
     * Returns a data element array representation of a pixel in this
     * ColorModel, given an integer pixel representation in the
     * default RGB color model.  This array can then be passed to the
     * {@link WritableRaster#setDataElements(int, int, Object) setDataElements}
     * method of a {@link WritableRaster} object.  If the pixel variable is
     * <code>null</code>, a new array is allocated.  If <code>pixel</code>
     * is not <code>null</code>, it must be
     * a primitive array of type <code>transferType</code>; otherwise, a
     * <code>ClassCastException</code> is thrown.  An
     * <code>ArrayIndexOutOfBoundsException</code> is
     * thrown if <code>pixel</code> is not large enough to hold a pixel
     * value for this <code>ColorModel</code>.  The pixel array is returned.
     * <p>
     * Since <code>IndexColorModel</code> can be subclassed, subclasses
     * inherit the implementation of this method and if they don't
     * override it then they throw an exception if they use an
     * unsupported <code>transferType</code>.
     *
     * @param rgb the integer pixel representation in the default RGB
     * color model
     * @param pixel the specified pixel
     * @return an array representation of the specified pixel in this
     *  <code>IndexColorModel</code>.
     * @throws ClassCastException if <code>pixel</code>
     *  is not a primitive array of type <code>transferType</code>
     * @throws ArrayIndexOutOfBoundsException if
     *  <code>pixel</code> is not large enough to hold a pixel value
     *  for this <code>ColorModel</code>
     * @throws UnsupportedOperationException if <code>transferType</code>
     *         is invalid
     * @see WritableRaster#setDataElements
     * @see SampleModel#setDataElements
     */
    public synchronized Object getDataElements(int rgb, Object pixel) {
        int red = (rgb>>16) & 0xff;
        int green = (rgb>>8) & 0xff;
        int blue  = rgb & 0xff;
        int alpha = (rgb>>>24);
        int pix = 0;

        // Note that pixels are stored at lookupcache[2*i]
        // and the rgb that was searched is stored at
        // lookupcache[2*i+1].  Also, the pixel is first
        // inverted using the unary complement operator
        // before storing in the cache so it can never be 0.
        for (int i = CACHESIZE - 2; i >= 0; i -= 2) {
            if ((pix = lookupcache[i]) == 0) {
                break;
            }
            if (rgb == lookupcache[i+1]) {
                return installpixel(pixel, ~pix);
            }
        }

        if (allgrayopaque) {
            // IndexColorModel objects are all tagged as
            // non-premultiplied so ignore the alpha value
            // of the incoming color, convert the
            // non-premultiplied color components to a
            // grayscale value and search for the closest
            // gray value in the palette.  Since all colors
            // in the palette are gray, we only need compare
            // to one of the color components for a match
            // using a simple linear distance formula.

            int minDist = 256;
            int d;
            int gray = (int) (red*77 + green*150 + blue*29 + 128)/256;

            for (int i = 0; i < map_size; i++) {
                if (this.rgb[i] == 0x0) {
                    // For allgrayopaque colormaps, entries are 0
                    // iff they are an invalid color and should be
                    // ignored during color searches.
                    continue;
                }
                d = (this.rgb[i] & 0xff) - gray;
                if (d < 0) d = -d;
                if (d < minDist) {
                    pix = i;
                    if (d == 0) {
                        break;
                    }
                    minDist = d;
                }
            }
        } else if (transparency == OPAQUE) {
            // IndexColorModel objects are all tagged as
            // non-premultiplied so ignore the alpha value
            // of the incoming color and search for closest
            // color match independently using a 3 component
            // Euclidean distance formula.
            // For opaque colormaps, palette entries are 0
            // iff they are an invalid color and should be
            // ignored during color searches.
            // As an optimization, exact color searches are
            // likely to be fairly common in opaque colormaps
            // so first we will do a quick search for an
            // exact match.

            int smallestError = Integer.MAX_VALUE;
            int lut[] = this.rgb;
            int lutrgb;
            for (int i=0; i < map_size; i++) {
                lutrgb = lut[i];
                if (lutrgb == rgb && lutrgb != 0) {
                    pix = i;
                    smallestError = 0;
                    break;
                }
            }

            if (smallestError != 0) {
                for (int i=0; i < map_size; i++) {
                    lutrgb = lut[i];
                    if (lutrgb == 0) {
                        continue;
                    }

                    int tmp = ((lutrgb >> 16) & 0xff) - red;
                    int currentError = tmp*tmp;
                    if (currentError < smallestError) {
                        tmp = ((lutrgb >> 8) & 0xff) - green;
                        currentError += tmp * tmp;
                        if (currentError < smallestError) {
                            tmp = (lutrgb & 0xff) - blue;
                            currentError += tmp * tmp;
                            if (currentError < smallestError) {
                                pix = i;
                                smallestError = currentError;
                            }
                        }
                    }
                }
            }
        } else if (alpha == 0 && transparent_index >= 0) {
            // Special case - transparent color maps to the
            // specified transparent pixel, if there is one

            pix = transparent_index;
        } else {
            // IndexColorModel objects are all tagged as
            // non-premultiplied so use non-premultiplied
            // color components in the distance calculations.
            // Look for closest match using a 4 component
            // Euclidean distance formula.

            int smallestError = Integer.MAX_VALUE;
            int lut[] = this.rgb;
            for (int i=0; i < map_size; i++) {
                int lutrgb = lut[i];
                if (lutrgb == rgb) {
                    if (validBits != null && !validBits.testBit(i)) {
                        continue;
                    }
                    pix = i;
                    break;
                }

                int tmp = ((lutrgb >> 16) & 0xff) - red;
                int currentError = tmp*tmp;
                if (currentError < smallestError) {
                    tmp = ((lutrgb >> 8) & 0xff) - green;
                    currentError += tmp * tmp;
                    if (currentError < smallestError) {
                        tmp = (lutrgb & 0xff) - blue;
                        currentError += tmp * tmp;
                        if (currentError < smallestError) {
                            tmp = (lutrgb >>> 24) - alpha;
                            currentError += tmp * tmp;
                            if (currentError < smallestError &&
                                (validBits == null || validBits.testBit(i)))
                            {
                                pix = i;
                                smallestError = currentError;
                            }
                        }
                    }
                }
            }
        }
        System.arraycopy(lookupcache, 2, lookupcache, 0, CACHESIZE - 2);
        lookupcache[CACHESIZE - 1] = rgb;
        lookupcache[CACHESIZE - 2] = ~pix;
        return installpixel(pixel, pix);
    }

    private Object installpixel(Object pixel, int pix) {
        switch (transferType) {
        case DataBuffer.TYPE_INT:
            int[] intObj;
            if (pixel == null) {
                pixel = intObj = new int[1];
            } else {
                intObj = (int[]) pixel;
            }
            intObj[0] = pix;
            break;
        case DataBuffer.TYPE_BYTE:
            byte[] byteObj;
            if (pixel == null) {
                pixel = byteObj = new byte[1];
            } else {
                byteObj = (byte[]) pixel;
            }
            byteObj[0] = (byte) pix;
            break;
        case DataBuffer.TYPE_USHORT:
            short[] shortObj;
            if (pixel == null) {
                pixel = shortObj = new short[1];
            } else {
                shortObj = (short[]) pixel;
            }
            shortObj[0] = (short) pix;
            break;
        default:
            throw new UnsupportedOperationException("This method has not been "+
                             "implemented for transferType " + transferType);
        }
        return pixel;
    }

    /**
     * Returns an array of unnormalized color/alpha components for a
     * specified pixel in this <code>ColorModel</code>.  The pixel value
     * is specified as an int.  If the <code>components</code> array is <code>null</code>,
     * a new array is allocated that contains
     * <code>offset + getNumComponents()</code> elements.
     * The <code>components</code> array is returned,
     * with the alpha component included
     * only if <code>hasAlpha</code> returns true.
     * Color/alpha components are stored in the <code>components</code> array starting
     * at <code>offset</code> even if the array is allocated by this method.
     * An <code>ArrayIndexOutOfBoundsException</code>
     * is thrown if  the <code>components</code> array is not <code>null</code> and is
     * not large enough to hold all the color and alpha components
     * starting at <code>offset</code>.
     * @param pixel the specified pixel
     * @param components the array to receive the color and alpha
     * components of the specified pixel
     * @param offset the offset into the <code>components</code> array at
     * which to start storing the color and alpha components
     * @return an array containing the color and alpha components of the
     * specified pixel starting at the specified offset.
     * @see ColorModel#hasAlpha
     * @see ColorModel#getNumComponents
     */
    public int[] getComponents(int pixel, int[] components, int offset) {
        if (components == null) {
            components = new int[offset+numComponents];
        }

        // REMIND: Needs to change if different color space
        components[offset+0] = getRed(pixel);
        components[offset+1] = getGreen(pixel);
        components[offset+2] = getBlue(pixel);
        if (supportsAlpha && (components.length-offset) > 3) {
            components[offset+3] = getAlpha(pixel);
        }

        return components;
    }

    /**
     * Returns an array of unnormalized color/alpha components for
     * a specified pixel in this <code>ColorModel</code>.  The pixel
     * value is specified by an array of data elements of type
     * <code>transferType</code> passed in as an object reference.
     * If <code>pixel</code> is not a primitive array of type
     * <code>transferType</code>, a <code>ClassCastException</code>
     * is thrown.  An <code>ArrayIndexOutOfBoundsException</code>
     * is thrown if <code>pixel</code> is not large enough to hold
     * a pixel value for this <code>ColorModel</code>.  If the
     * <code>components</code> array is <code>null</code>, a new array
     * is allocated that contains
     * <code>offset + getNumComponents()</code> elements.
     * The <code>components</code> array is returned,
     * with the alpha component included
     * only if <code>hasAlpha</code> returns true.
     * Color/alpha components are stored in the <code>components</code>
     * array starting at <code>offset</code> even if the array is
     * allocated by this method.  An
     * <code>ArrayIndexOutOfBoundsException</code> is also
     * thrown if  the <code>components</code> array is not
     * <code>null</code> and is not large enough to hold all the color
     * and alpha components starting at <code>offset</code>.
     * <p>
     * Since <code>IndexColorModel</code> can be subclassed, subclasses
     * inherit the implementation of this method and if they don't
     * override it then they throw an exception if they use an
     * unsupported <code>transferType</code>.
     *
     * @param pixel the specified pixel
     * @param components an array that receives the color and alpha
     * components of the specified pixel
     * @param offset the index into the <code>components</code> array at
     * which to begin storing the color and alpha components of the
     * specified pixel
     * @return an array containing the color and alpha components of the
     * specified pixel starting at the specified offset.
     * @throws ArrayIndexOutOfBoundsException if <code>pixel</code>
     *            is not large enough to hold a pixel value for this
     *            <code>ColorModel</code> or if the
     *            <code>components</code> array is not <code>null</code>
     *            and is not large enough to hold all the color
     *            and alpha components starting at <code>offset</code>
     * @throws ClassCastException if <code>pixel</code> is not a
     *            primitive array of type <code>transferType</code>
     * @throws UnsupportedOperationException if <code>transferType</code>
     *         is not one of the supported transfer types
     * @see ColorModel#hasAlpha
     * @see ColorModel#getNumComponents
     */
    public int[] getComponents(Object pixel, int[] components, int offset) {
        int intpixel;
        switch (transferType) {
            case DataBuffer.TYPE_BYTE:
               byte bdata[] = (byte[])pixel;
               intpixel = bdata[0] & 0xff;
            break;
            case DataBuffer.TYPE_USHORT:
               short sdata[] = (short[])pixel;
               intpixel = sdata[0] & 0xffff;
            break;
            case DataBuffer.TYPE_INT:
               int idata[] = (int[])pixel;
               intpixel = idata[0];
            break;
            default:
               throw new UnsupportedOperationException("This method has not been "+
                   "implemented for transferType " + transferType);
        }
        return getComponents(intpixel, components, offset);
    }

    /**
     * Returns a pixel value represented as an int in this
     * <code>ColorModel</code> given an array of unnormalized
     * color/alpha components.  An
     * <code>ArrayIndexOutOfBoundsException</code>
     * is thrown if the <code>components</code> array is not large
     * enough to hold all of the color and alpha components starting
     * at <code>offset</code>.  Since
     * <code>ColorModel</code> can be subclassed, subclasses inherit the
     * implementation of this method and if they don't override it then
     * they throw an exception if they use an unsupported transferType.
     * @param components an array of unnormalized color and alpha
     * components
     * @param offset the index into <code>components</code> at which to
     * begin retrieving the color and alpha components
     * @return an <code>int</code> pixel value in this
     * <code>ColorModel</code> corresponding to the specified components.
     * @throws ArrayIndexOutOfBoundsException if
     *  the <code>components</code> array is not large enough to
     *  hold all of the color and alpha components starting at
     *  <code>offset</code>
     * @throws UnsupportedOperationException if <code>transferType</code>
     *         is invalid
     */
    public int getDataElement(int[] components, int offset) {
        int rgb = (components[offset+0]<<16)
            | (components[offset+1]<<8) | (components[offset+2]);
        if (supportsAlpha) {
            rgb |= (components[offset+3]<<24);
        }
        else {
            rgb |= 0xff000000;
        }
        Object inData = getDataElements(rgb, null);
        int pixel;
        switch (transferType) {
            case DataBuffer.TYPE_BYTE:
               byte bdata[] = (byte[])inData;
               pixel = bdata[0] & 0xff;
            break;
            case DataBuffer.TYPE_USHORT:
               short sdata[] = (short[])inData;
               pixel = sdata[0];
            break;
            case DataBuffer.TYPE_INT:
               int idata[] = (int[])inData;
               pixel = idata[0];
            break;
            default:
               throw new UnsupportedOperationException("This method has not been "+
                   "implemented for transferType " + transferType);
        }
        return pixel;
    }

    /**
     * Returns a data element array representation of a pixel in this
     * <code>ColorModel</code> given an array of unnormalized color/alpha
     * components.  This array can then be passed to the
     * <code>setDataElements</code> method of a <code>WritableRaster</code>
     * object.  An <code>ArrayIndexOutOfBoundsException</code> is
     * thrown if the
     * <code>components</code> array is not large enough to hold all of the
     * color and alpha components starting at <code>offset</code>.
     * If the pixel variable is <code>null</code>, a new array
     * is allocated.  If <code>pixel</code> is not <code>null</code>,
     * it must be a primitive array of type <code>transferType</code>;
     * otherwise, a <code>ClassCastException</code> is thrown.
     * An <code>ArrayIndexOutOfBoundsException</code> is thrown if pixel
     * is not large enough to hold a pixel value for this
     * <code>ColorModel</code>.
     * <p>
     * Since <code>IndexColorModel</code> can be subclassed, subclasses
     * inherit the implementation of this method and if they don't
     * override it then they throw an exception if they use an
     * unsupported <code>transferType</code>
     *
     * @param components an array of unnormalized color and alpha
     * components
     * @param offset the index into <code>components</code> at which to
     * begin retrieving color and alpha components
     * @param pixel the <code>Object</code> representing an array of color
     * and alpha components
     * @return an <code>Object</code> representing an array of color and
     * alpha components.
     * @throws ClassCastException if <code>pixel</code>
     *  is not a primitive array of type <code>transferType</code>
     * @throws ArrayIndexOutOfBoundsException if
     *  <code>pixel</code> is not large enough to hold a pixel value
     *  for this <code>ColorModel</code> or the <code>components</code>
     *  array is not large enough to hold all of the color and alpha
     *  components starting at <code>offset</code>
     * @throws UnsupportedOperationException if <code>transferType</code>
     *         is not one of the supported transfer types
     * @see WritableRaster#setDataElements
     * @see SampleModel#setDataElements
     */
    public Object getDataElements(int[] components, int offset, Object pixel) {
        int rgb = (components[offset+0]<<16) | (components[offset+1]<<8)
            | (components[offset+2]);
        if (supportsAlpha) {
            rgb |= (components[offset+3]<<24);
        }
        else {
            rgb &= 0xff000000;
        }
        return getDataElements(rgb, pixel);
    }

    /**
     * Creates a <code>WritableRaster</code> with the specified width
     * and height that has a data layout (<code>SampleModel</code>)
     * compatible with this <code>ColorModel</code>.  This method
     * only works for color models with 16 or fewer bits per pixel.
     * <p>
     * Since <code>IndexColorModel</code> can be subclassed, any
     * subclass that supports greater than 16 bits per pixel must
     * override this method.
     *
     * @param w the width to apply to the new <code>WritableRaster</code>
     * @param h the height to apply to the new <code>WritableRaster</code>
     * @return a <code>WritableRaster</code> object with the specified
     * width and height.
     * @throws UnsupportedOperationException if the number of bits in a
     *         pixel is greater than 16
     * @see WritableRaster
     * @see SampleModel
     */
    public WritableRaster createCompatibleWritableRaster(int w, int h) {
        WritableRaster raster;

        if (pixel_bits == 1 || pixel_bits == 2 || pixel_bits == 4) {
            // TYPE_BINARY
            raster = Raster.createPackedRaster(DataBuffer.TYPE_BYTE,
                                               w, h, 1, pixel_bits, null);
        }
        else if (pixel_bits <= 8) {
            raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,
                                                  w,h,1,null);
        }
        else if (pixel_bits <= 16) {
            raster = Raster.createInterleavedRaster(DataBuffer.TYPE_USHORT,
                                                  w,h,1,null);
        }
        else {
            throw new
                UnsupportedOperationException("This method is not supported "+
                                              " for pixel bits > 16.");
        }
        return raster;
    }

    /**
      * Returns <code>true</code> if <code>raster</code> is compatible
      * with this <code>ColorModel</code> or <code>false</code> if it
      * is not compatible with this <code>ColorModel</code>.
      * @param raster the {@link Raster} object to test for compatibility
      * @return <code>true</code> if <code>raster</code> is compatible
      * with this <code>ColorModel</code>; <code>false</code> otherwise.
      *
      */
    public boolean isCompatibleRaster(Raster raster) {

        int size = raster.getSampleModel().getSampleSize(0);
        return ((raster.getTransferType() == transferType) &&
                (raster.getNumBands() == 1) && ((1 << size) >= map_size));
    }

    /**
     * Creates a <code>SampleModel</code> with the specified
     * width and height that has a data layout compatible with
     * this <code>ColorModel</code>.
     * @param w the width to apply to the new <code>SampleModel</code>
     * @param h the height to apply to the new <code>SampleModel</code>
     * @return a <code>SampleModel</code> object with the specified
     * width and height.
     * @throws IllegalArgumentException if <code>w</code> or
     *         <code>h</code> is not greater than 0
     * @see SampleModel
     */
    public SampleModel createCompatibleSampleModel(int w, int h) {
        int[] off = new int[1];
        off[0] = 0;
        if (pixel_bits == 1 || pixel_bits == 2 || pixel_bits == 4) {
            return new MultiPixelPackedSampleModel(transferType, w, h,
                                                   pixel_bits);
        }
        else {
            return new ComponentSampleModel(transferType, w, h, 1, w,
                                            off);
        }
    }

    /**
     * Checks if the specified <code>SampleModel</code> is compatible
     * with this <code>ColorModel</code>.  If <code>sm</code> is
     * <code>null</code>, this method returns <code>false</code>.
     * @param sm the specified <code>SampleModel</code>,
     *           or <code>null</code>
     * @return <code>true</code> if the specified <code>SampleModel</code>
     * is compatible with this <code>ColorModel</code>; <code>false</code>
     * otherwise.
     * @see SampleModel
     */
    public boolean isCompatibleSampleModel(SampleModel sm) {
        // fix 4238629
        if (! (sm instanceof ComponentSampleModel) &&
            ! (sm instanceof MultiPixelPackedSampleModel)   ) {
            return false;
        }

        // Transfer type must be the same
        if (sm.getTransferType() != transferType) {
            return false;
        }

        if (sm.getNumBands() != 1) {
            return false;
        }

        return true;
    }

    /**
     * Returns a new <code>BufferedImage</code> of TYPE_INT_ARGB or
     * TYPE_INT_RGB that has a <code>Raster</code> with pixel data
     * computed by expanding the indices in the source <code>Raster</code>
     * using the color/alpha component arrays of this <code>ColorModel</code>.
     * Only the lower <em>n</em> bits of each index value in the source
     * <code>Raster</code>, as specified in the
     * <a href="#index_values">class description</a> above, are used to
     * compute the color/alpha values in the returned image.
     * If <code>forceARGB</code> is <code>true</code>, a TYPE_INT_ARGB image is
     * returned regardless of whether or not this <code>ColorModel</code>
     * has an alpha component array or a transparent pixel.
     * @param raster the specified <code>Raster</code>
     * @param forceARGB if <code>true</code>, the returned
     *     <code>BufferedImage</code> is TYPE_INT_ARGB; otherwise it is
     *     TYPE_INT_RGB
     * @return a <code>BufferedImage</code> created with the specified
     *     <code>Raster</code>
     * @throws IllegalArgumentException if the raster argument is not
     *           compatible with this IndexColorModel
     */
    public BufferedImage convertToIntDiscrete(Raster raster,
                                              boolean forceARGB) {
        ColorModel cm;

        if (!isCompatibleRaster(raster)) {
            throw new IllegalArgumentException("This raster is not compatible" +
                 "with this IndexColorModel.");
        }
        if (forceARGB || transparency == TRANSLUCENT) {
            cm = ColorModel.getRGBdefault();
        }
        else if (transparency == BITMASK) {
            cm = new DirectColorModel(25, 0xff0000, 0x00ff00, 0x0000ff,
                                      0x1000000);
        }
        else {
            cm = new DirectColorModel(24, 0xff0000, 0x00ff00, 0x0000ff);
        }

        int w = raster.getWidth();
        int h = raster.getHeight();
        WritableRaster discreteRaster =
                  cm.createCompatibleWritableRaster(w, h);
        Object obj = null;
        int[] data = null;

        int rX = raster.getMinX();
        int rY = raster.getMinY();

        for (int y=0; y < h; y++, rY++) {
            obj = raster.getDataElements(rX, rY, w, 1, obj);
            if (obj instanceof int[]) {
                data = (int[])obj;
            } else {
                data = DataBuffer.toIntArray(obj);
            }
            for (int x=0; x < w; x++) {
                data[x] = rgb[data[x] & pixel_mask];
            }
            discreteRaster.setDataElements(0, y, w, 1, data);
        }

        return new BufferedImage(cm, discreteRaster, false, null);
    }

    /**
     * Returns whether or not the pixel is valid.
     * @param pixel the specified pixel value
     * @return <code>true</code> if <code>pixel</code>
     * is valid; <code>false</code> otherwise.
     * @since 1.3
     */
    public boolean isValid(int pixel) {
        return ((pixel >= 0 && pixel < map_size) &&
                (validBits == null || validBits.testBit(pixel)));
    }

    /**
     * Returns whether or not all of the pixels are valid.
     * @return <code>true</code> if all pixels are valid;
     * <code>false</code> otherwise.
     * @since 1.3
     */
    public boolean isValid() {
        return (validBits == null);
    }

    /**
     * Returns a <code>BigInteger</code> that indicates the valid/invalid
     * pixels in the colormap.  A bit is valid if the
     * <code>BigInteger</code> value at that index is set, and is invalid
     * if the <code>BigInteger</code> value at that index is not set.
     * The only valid ranges to query in the <code>BigInteger</code> are
     * between 0 and the map size.
     * @return a <code>BigInteger</code> indicating the valid/invalid pixels.
     * @since 1.3
     */
    public BigInteger getValidPixels() {
        if (validBits == null) {
            return getAllValid();
        }
        else {
            return validBits;
        }
    }

    /**
     * Disposes of system resources associated with this
     * <code>ColorModel</code> once this <code>ColorModel</code> is no
     * longer referenced.
     */
    public void finalize() {
    }

    /**
     * Returns the <code>String</code> representation of the contents of
     * this <code>ColorModel</code>object.
     * @return a <code>String</code> representing the contents of this
     * <code>ColorModel</code> object.
     */
    public String toString() {
       return new String("IndexColorModel: #pixelBits = "+pixel_bits
                         + " numComponents = "+numComponents
                         + " color space = "+colorSpace
                         + " transparency = "+transparency
                         + " transIndex   = "+transparent_index
                         + " has alpha = "+supportsAlpha
                         + " isAlphaPre = "+isAlphaPremultiplied
                         );
    }
}
