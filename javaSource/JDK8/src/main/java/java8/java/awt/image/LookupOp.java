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

import sun.awt.image.ImagingLib;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * This class implements a lookup operation from the source
 * to the destination.  The LookupTable object may contain a single array
 * or multiple arrays, subject to the restrictions below.
 * <p>
 * For Rasters, the lookup operates on bands.  The number of
 * lookup arrays may be one, in which case the same array is
 * applied to all bands, or it must equal the number of Source
 * Raster bands.
 * <p>
 * For BufferedImages, the lookup operates on color and alpha components.
 * The number of lookup arrays may be one, in which case the
 * same array is applied to all color (but not alpha) components.
 * Otherwise, the number of lookup arrays may
 * equal the number of Source color components, in which case no
 * lookup of the alpha component (if present) is performed.
 * If neither of these cases apply, the number of lookup arrays
 * must equal the number of Source color components plus alpha components,
 * in which case lookup is performed for all color and alpha components.
 * This allows non-uniform rescaling of multi-band BufferedImages.
 * <p>
 * BufferedImage sources with premultiplied alpha data are treated in the same
 * manner as non-premultiplied images for purposes of the lookup.  That is,
 * the lookup is done per band on the raw data of the BufferedImage source
 * without regard to whether the data is premultiplied.  If a color conversion
 * is required to the destination ColorModel, the premultiplied state of
 * both source and destination will be taken into account for this step.
 * <p>
 * Images with an IndexColorModel cannot be used.
 * <p>
 * If a RenderingHints object is specified in the constructor, the
 * color rendering hint and the dithering hint may be used when color
 * conversion is required.
 * <p>
 * This class allows the Source to be the same as the Destination.
 *
 * @see LookupTable
 * @see RenderingHints#KEY_COLOR_RENDERING
 * @see RenderingHints#KEY_DITHERING
 */

public class LookupOp implements BufferedImageOp, RasterOp {
    private LookupTable ltable;
    private int numComponents;
    RenderingHints hints;

    /**
     * Constructs a <code>LookupOp</code> object given the lookup
     * table and a <code>RenderingHints</code> object, which might
     * be <code>null</code>.
     * @param lookup the specified <code>LookupTable</code>
     * @param hints the specified <code>RenderingHints</code>,
     *        or <code>null</code>
     */
    public LookupOp(LookupTable lookup, RenderingHints hints) {
        this.ltable = lookup;
        this.hints  = hints;
        numComponents = ltable.getNumComponents();
    }

    /**
     * Returns the <code>LookupTable</code>.
     * @return the <code>LookupTable</code> of this
     *         <code>LookupOp</code>.
     */
    public final LookupTable getTable() {
        return ltable;
    }

    /**
     * Performs a lookup operation on a <code>BufferedImage</code>.
     * If the color model in the source image is not the same as that
     * in the destination image, the pixels will be converted
     * in the destination.  If the destination image is <code>null</code>,
     * a <code>BufferedImage</code> will be created with an appropriate
     * <code>ColorModel</code>.  An <code>IllegalArgumentException</code>
     * might be thrown if the number of arrays in the
     * <code>LookupTable</code> does not meet the restrictions
     * stated in the class comment above, or if the source image
     * has an <code>IndexColorModel</code>.
     * @param src the <code>BufferedImage</code> to be filtered
     * @param dst the <code>BufferedImage</code> in which to
     *            store the results of the filter operation
     * @return the filtered <code>BufferedImage</code>.
     * @throws IllegalArgumentException if the number of arrays in the
     *         <code>LookupTable</code> does not meet the restrictions
     *         described in the class comments, or if the source image
     *         has an <code>IndexColorModel</code>.
     */
    public final BufferedImage filter(BufferedImage src, BufferedImage dst) {
        ColorModel srcCM = src.getColorModel();
        int numBands = srcCM.getNumColorComponents();
        ColorModel dstCM;
        if (srcCM instanceof IndexColorModel) {
            throw new
                IllegalArgumentException("LookupOp cannot be "+
                                         "performed on an indexed image");
        }
        int numComponents = ltable.getNumComponents();
        if (numComponents != 1 &&
            numComponents != srcCM.getNumComponents() &&
            numComponents != srcCM.getNumColorComponents())
        {
            throw new IllegalArgumentException("Number of arrays in the "+
                                               " lookup table ("+
                                               numComponents+
                                               " is not compatible with the "+
                                               " src image: "+src);
        }


        boolean needToConvert = false;

        int width = src.getWidth();
        int height = src.getHeight();

        if (dst == null) {
            dst = createCompatibleDestImage(src, null);
            dstCM = srcCM;
        }
        else {
            if (width != dst.getWidth()) {
                throw new
                    IllegalArgumentException("Src width ("+width+
                                             ") not equal to dst width ("+
                                             dst.getWidth()+")");
            }
            if (height != dst.getHeight()) {
                throw new
                    IllegalArgumentException("Src height ("+height+
                                             ") not equal to dst height ("+
                                             dst.getHeight()+")");
            }

            dstCM = dst.getColorModel();
            if (srcCM.getColorSpace().getType() !=
                dstCM.getColorSpace().getType())
            {
                needToConvert = true;
                dst = createCompatibleDestImage(src, null);
            }

        }

        BufferedImage origDst = dst;

        if (ImagingLib.filter(this, src, dst) == null) {
            // Do it the slow way
            WritableRaster srcRaster = src.getRaster();
            WritableRaster dstRaster = dst.getRaster();

            if (srcCM.hasAlpha()) {
                if (numBands-1 == numComponents || numComponents == 1) {
                    int minx = srcRaster.getMinX();
                    int miny = srcRaster.getMinY();
                    int[] bands = new int[numBands-1];
                    for (int i=0; i < numBands-1; i++) {
                        bands[i] = i;
                    }
                    srcRaster =
                        srcRaster.createWritableChild(minx, miny,
                                                      srcRaster.getWidth(),
                                                      srcRaster.getHeight(),
                                                      minx, miny,
                                                      bands);
                }
            }
            if (dstCM.hasAlpha()) {
                int dstNumBands = dstRaster.getNumBands();
                if (dstNumBands-1 == numComponents || numComponents == 1) {
                    int minx = dstRaster.getMinX();
                    int miny = dstRaster.getMinY();
                    int[] bands = new int[numBands-1];
                    for (int i=0; i < numBands-1; i++) {
                        bands[i] = i;
                    }
                    dstRaster =
                        dstRaster.createWritableChild(minx, miny,
                                                      dstRaster.getWidth(),
                                                      dstRaster.getHeight(),
                                                      minx, miny,
                                                      bands);
                }
            }

            filter(srcRaster, dstRaster);
        }

        if (needToConvert) {
            // ColorModels are not the same
            ColorConvertOp ccop = new ColorConvertOp(hints);
            ccop.filter(dst, origDst);
        }

        return origDst;
    }

    /**
     * Performs a lookup operation on a <code>Raster</code>.
     * If the destination <code>Raster</code> is <code>null</code>,
     * a new <code>Raster</code> will be created.
     * The <code>IllegalArgumentException</code> might be thrown
     * if the source <code>Raster</code> and the destination
     * <code>Raster</code> do not have the same
     * number of bands or if the number of arrays in the
     * <code>LookupTable</code> does not meet the
     * restrictions stated in the class comment above.
     * @param src the source <code>Raster</code> to filter
     * @param dst the destination <code>WritableRaster</code> for the
     *            filtered <code>src</code>
     * @return the filtered <code>WritableRaster</code>.
     * @throws IllegalArgumentException if the source and destinations
     *         rasters do not have the same number of bands, or the
     *         number of arrays in the <code>LookupTable</code> does
     *         not meet the restrictions described in the class comments.
     *
     */
    public final WritableRaster filter (Raster src, WritableRaster dst) {
        int numBands  = src.getNumBands();
        int dstLength = dst.getNumBands();
        int height    = src.getHeight();
        int width     = src.getWidth();
        int srcPix[]  = new int[numBands];

        // Create a new destination Raster, if needed

        if (dst == null) {
            dst = createCompatibleDestRaster(src);
        }
        else if (height != dst.getHeight() || width != dst.getWidth()) {
            throw new
                IllegalArgumentException ("Width or height of Rasters do not "+
                                          "match");
        }
        dstLength = dst.getNumBands();

        if (numBands != dstLength) {
            throw new
                IllegalArgumentException ("Number of channels in the src ("
                                          + numBands +
                                          ") does not match number of channels"
                                          + " in the destination ("
                                          + dstLength + ")");
        }
        int numComponents = ltable.getNumComponents();
        if (numComponents != 1 && numComponents != src.getNumBands()) {
            throw new IllegalArgumentException("Number of arrays in the "+
                                               " lookup table ("+
                                               numComponents+
                                               " is not compatible with the "+
                                               " src Raster: "+src);
        }


        if (ImagingLib.filter(this, src, dst) != null) {
            return dst;
        }

        // Optimize for cases we know about
        if (ltable instanceof ByteLookupTable) {
            byteFilter ((ByteLookupTable) ltable, src, dst,
                        width, height, numBands);
        }
        else if (ltable instanceof ShortLookupTable) {
            shortFilter ((ShortLookupTable) ltable, src, dst, width,
                         height, numBands);
        }
        else {
            // Not one we recognize so do it slowly
            int sminX = src.getMinX();
            int sY = src.getMinY();
            int dminX = dst.getMinX();
            int dY = dst.getMinY();
            for (int y=0; y < height; y++, sY++, dY++) {
                int sX = sminX;
                int dX = dminX;
                for (int x=0; x < width; x++, sX++, dX++) {
                    // Find data for all bands at this x,y position
                    src.getPixel(sX, sY, srcPix);

                    // Lookup the data for all bands at this x,y position
                    ltable.lookupPixel(srcPix, srcPix);

                    // Put it back for all bands
                    dst.setPixel(dX, dY, srcPix);
                }
            }
        }

        return dst;
    }

    /**
     * Returns the bounding box of the filtered destination image.  Since
     * this is not a geometric operation, the bounding box does not
     * change.
     * @param src the <code>BufferedImage</code> to be filtered
     * @return the bounds of the filtered definition image.
     */
    public final Rectangle2D getBounds2D (BufferedImage src) {
        return getBounds2D(src.getRaster());
    }

    /**
     * Returns the bounding box of the filtered destination Raster.  Since
     * this is not a geometric operation, the bounding box does not
     * change.
     * @param src the <code>Raster</code> to be filtered
     * @return the bounds of the filtered definition <code>Raster</code>.
     */
    public final Rectangle2D getBounds2D (Raster src) {
        return src.getBounds();

    }

    /**
     * Creates a zeroed destination image with the correct size and number of
     * bands.  If destCM is <code>null</code>, an appropriate
     * <code>ColorModel</code> will be used.
     * @param src       Source image for the filter operation.
     * @param destCM    the destination's <code>ColorModel</code>, which
     *                  can be <code>null</code>.
     * @return a filtered destination <code>BufferedImage</code>.
     */
    public BufferedImage createCompatibleDestImage (BufferedImage src,
                                                    ColorModel destCM) {
        BufferedImage image;
        int w = src.getWidth();
        int h = src.getHeight();
        int transferType = DataBuffer.TYPE_BYTE;
        if (destCM == null) {
            ColorModel cm = src.getColorModel();
            Raster raster = src.getRaster();
            if (cm instanceof ComponentColorModel) {
                DataBuffer db = raster.getDataBuffer();
                boolean hasAlpha = cm.hasAlpha();
                boolean isPre    = cm.isAlphaPremultiplied();
                int trans        = cm.getTransparency();
                int[] nbits = null;
                if (ltable instanceof ByteLookupTable) {
                    if (db.getDataType() == db.TYPE_USHORT) {
                        // Dst raster should be of type byte
                        if (hasAlpha) {
                            nbits = new int[2];
                            if (trans == cm.BITMASK) {
                                nbits[1] = 1;
                            }
                            else {
                                nbits[1] = 8;
                            }
                        }
                        else {
                            nbits = new int[1];
                        }
                        nbits[0] = 8;
                    }
                    // For byte, no need to change the cm
                }
                else if (ltable instanceof ShortLookupTable) {
                    transferType = DataBuffer.TYPE_USHORT;
                    if (db.getDataType() == db.TYPE_BYTE) {
                        if (hasAlpha) {
                            nbits = new int[2];
                            if (trans == cm.BITMASK) {
                                nbits[1] = 1;
                            }
                            else {
                                nbits[1] = 16;
                            }
                        }
                        else {
                            nbits = new int[1];
                        }
                        nbits[0] = 16;
                    }
                }
                if (nbits != null) {
                    cm = new ComponentColorModel(cm.getColorSpace(),
                                                 nbits, hasAlpha, isPre,
                                                 trans, transferType);
                }
            }
            image = new BufferedImage(cm,
                                      cm.createCompatibleWritableRaster(w, h),
                                      cm.isAlphaPremultiplied(),
                                      null);
        }
        else {
            image = new BufferedImage(destCM,
                                      destCM.createCompatibleWritableRaster(w,
                                                                            h),
                                      destCM.isAlphaPremultiplied(),
                                      null);
        }

        return image;
    }

    /**
     * Creates a zeroed-destination <code>Raster</code> with the
     * correct size and number of bands, given this source.
     * @param src the <code>Raster</code> to be transformed
     * @return the zeroed-destination <code>Raster</code>.
     */
    public WritableRaster createCompatibleDestRaster (Raster src) {
        return src.createCompatibleWritableRaster();
    }

    /**
     * Returns the location of the destination point given a
     * point in the source.  If <code>dstPt</code> is not
     * <code>null</code>, it will be used to hold the return value.
     * Since this is not a geometric operation, the <code>srcPt</code>
     * will equal the <code>dstPt</code>.
     * @param srcPt a <code>Point2D</code> that represents a point
     *        in the source image
     * @param dstPt a <code>Point2D</code>that represents the location
     *        in the destination
     * @return the <code>Point2D</code> in the destination that
     *         corresponds to the specified point in the source.
     */
    public final Point2D getPoint2D (Point2D srcPt, Point2D dstPt) {
        if (dstPt == null) {
            dstPt = new Point2D.Float();
        }
        dstPt.setLocation(srcPt.getX(), srcPt.getY());

        return dstPt;
    }

    /**
     * Returns the rendering hints for this op.
     * @return the <code>RenderingHints</code> object associated
     *         with this op.
     */
    public final RenderingHints getRenderingHints() {
        return hints;
    }

    private final void byteFilter(ByteLookupTable lookup, Raster src,
                                  WritableRaster dst,
                                  int width, int height, int numBands) {
        int[] srcPix = null;

        // Find the ref to the table and the offset
        byte[][] table = lookup.getTable();
        int offset = lookup.getOffset();
        int tidx;
        int step=1;

        // Check if it is one lookup applied to all bands
        if (table.length == 1) {
            step=0;
        }

        int x;
        int y;
        int band;
        int len = table[0].length;

        // Loop through the data
        for ( y=0; y < height; y++) {
            tidx = 0;
            for ( band=0; band < numBands; band++, tidx+=step) {
                // Find data for this band, scanline
                srcPix = src.getSamples(0, y, width, 1, band, srcPix);

                for ( x=0; x < width; x++) {
                    int index = srcPix[x]-offset;
                    if (index < 0 || index > len) {
                        throw new
                            IllegalArgumentException("index ("+index+
                                                     "(out of range: "+
                                                     " srcPix["+x+
                                                     "]="+ srcPix[x]+
                                                     " offset="+ offset);
                    }
                    // Do the lookup
                    srcPix[x] = table[tidx][index];
                }
                // Put it back
                dst.setSamples(0, y, width, 1, band, srcPix);
            }
        }
    }

    private final void shortFilter(ShortLookupTable lookup, Raster src,
                                   WritableRaster dst,
                                   int width, int height, int numBands) {
        int band;
        int[] srcPix = null;

        // Find the ref to the table and the offset
        short[][] table = lookup.getTable();
        int offset = lookup.getOffset();
        int tidx;
        int step=1;

        // Check if it is one lookup applied to all bands
        if (table.length == 1) {
            step=0;
        }

        int x = 0;
        int y = 0;
        int index;
        int maxShort = (1<<16)-1;
        // Loop through the data
        for (y=0; y < height; y++) {
            tidx = 0;
            for ( band=0; band < numBands; band++, tidx+=step) {
                // Find data for this band, scanline
                srcPix = src.getSamples(0, y, width, 1, band, srcPix);

                for ( x=0; x < width; x++) {
                    index = srcPix[x]-offset;
                    if (index < 0 || index > maxShort) {
                        throw new
                            IllegalArgumentException("index out of range "+
                                                     index+" x is "+x+
                                                     "srcPix[x]="+srcPix[x]
                                                     +" offset="+ offset);
                    }
                    // Do the lookup
                    srcPix[x] = table[tidx][index];
                }
                // Put it back
                dst.setSamples(0, y, width, 1, band, srcPix);
            }
        }
    }
}
