/*
 * Copyright (c) 1997, 2013, Oracle and/or its affiliates. All rights reserved.
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
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.awt.image.ImagingOpException;
import java.awt.image.RasterFormatException;
import java.awt.image.RasterOp;
import java.lang.annotation.Native;

/**
 * This class uses an affine transform to perform a linear mapping from
 * 2D coordinates in the source image or <CODE>Raster</CODE> to 2D coordinates
 * in the destination image or <CODE>Raster</CODE>.
 * The type of interpolation that is used is specified through a constructor,
 * either by a <CODE>RenderingHints</CODE> object or by one of the integer
 * interpolation types defined in this class.
 * <p>
 * If a <CODE>RenderingHints</CODE> object is specified in the constructor, the
 * interpolation hint and the rendering quality hint are used to set
 * the interpolation type for this operation.  The color rendering hint
 * and the dithering hint can be used when color conversion is required.
 * <p>
 * Note that the following constraints have to be met:
 * <ul>
 * <li>The source and destination must be different.
 * <li>For <CODE>Raster</CODE> objects, the number of bands in the source must
 * be equal to the number of bands in the destination.
 * </ul>
 * @see AffineTransform
 * @see BufferedImageFilter
 * @see RenderingHints#KEY_INTERPOLATION
 * @see RenderingHints#KEY_RENDERING
 * @see RenderingHints#KEY_COLOR_RENDERING
 * @see RenderingHints#KEY_DITHERING
 */
public class AffineTransformOp implements BufferedImageOp, RasterOp {
    private AffineTransform xform;
    RenderingHints hints;

    /**
     * Nearest-neighbor interpolation type.
     */
    @Native public static final int TYPE_NEAREST_NEIGHBOR = 1;

    /**
     * Bilinear interpolation type.
     */
    @Native public static final int TYPE_BILINEAR = 2;

    /**
     * Bicubic interpolation type.
     */
    @Native public static final int TYPE_BICUBIC = 3;

    int interpolationType = TYPE_NEAREST_NEIGHBOR;

    /**
     * Constructs an <CODE>AffineTransformOp</CODE> given an affine transform.
     * The interpolation type is determined from the
     * <CODE>RenderingHints</CODE> object.  If the interpolation hint is
     * defined, it will be used. Otherwise, if the rendering quality hint is
     * defined, the interpolation type is determined from its value.  If no
     * hints are specified (<CODE>hints</CODE> is null),
     * the interpolation type is {@link #TYPE_NEAREST_NEIGHBOR
     * TYPE_NEAREST_NEIGHBOR}.
     *
     * @param xform The <CODE>AffineTransform</CODE> to use for the
     * operation.
     *
     * @param hints The <CODE>RenderingHints</CODE> object used to specify
     * the interpolation type for the operation.
     *
     * @throws ImagingOpException if the transform is non-invertible.
     * @see RenderingHints#KEY_INTERPOLATION
     * @see RenderingHints#KEY_RENDERING
     */
    public AffineTransformOp(AffineTransform xform, RenderingHints hints){
        validateTransform(xform);
        this.xform = (AffineTransform) xform.clone();
        this.hints = hints;

        if (hints != null) {
            Object value = hints.get(hints.KEY_INTERPOLATION);
            if (value == null) {
                value = hints.get(hints.KEY_RENDERING);
                if (value == hints.VALUE_RENDER_SPEED) {
                    interpolationType = TYPE_NEAREST_NEIGHBOR;
                }
                else if (value == hints.VALUE_RENDER_QUALITY) {
                    interpolationType = TYPE_BILINEAR;
                }
            }
            else if (value == hints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR) {
                interpolationType = TYPE_NEAREST_NEIGHBOR;
            }
            else if (value == hints.VALUE_INTERPOLATION_BILINEAR) {
                interpolationType = TYPE_BILINEAR;
            }
            else if (value == hints.VALUE_INTERPOLATION_BICUBIC) {
                interpolationType = TYPE_BICUBIC;
            }
        }
        else {
            interpolationType = TYPE_NEAREST_NEIGHBOR;
        }
    }

    /**
     * Constructs an <CODE>AffineTransformOp</CODE> given an affine transform
     * and the interpolation type.
     *
     * @param xform The <CODE>AffineTransform</CODE> to use for the operation.
     * @param interpolationType One of the integer
     * interpolation type constants defined by this class:
     * {@link #TYPE_NEAREST_NEIGHBOR TYPE_NEAREST_NEIGHBOR},
     * {@link #TYPE_BILINEAR TYPE_BILINEAR},
     * {@link #TYPE_BICUBIC TYPE_BICUBIC}.
     * @throws ImagingOpException if the transform is non-invertible.
     */
    public AffineTransformOp(AffineTransform xform, int interpolationType) {
        validateTransform(xform);
        this.xform = (AffineTransform)xform.clone();
        switch(interpolationType) {
            case TYPE_NEAREST_NEIGHBOR:
            case TYPE_BILINEAR:
            case TYPE_BICUBIC:
                break;
        default:
            throw new IllegalArgumentException("Unknown interpolation type: "+
                                               interpolationType);
        }
        this.interpolationType = interpolationType;
    }

    /**
     * Returns the interpolation type used by this op.
     * @return the interpolation type.
     * @see #TYPE_NEAREST_NEIGHBOR
     * @see #TYPE_BILINEAR
     * @see #TYPE_BICUBIC
     */
    public final int getInterpolationType() {
        return interpolationType;
    }

    /**
     * Transforms the source <CODE>BufferedImage</CODE> and stores the results
     * in the destination <CODE>BufferedImage</CODE>.
     * If the color models for the two images do not match, a color
     * conversion into the destination color model is performed.
     * If the destination image is null,
     * a <CODE>BufferedImage</CODE> is created with the source
     * <CODE>ColorModel</CODE>.
     * <p>
     * The coordinates of the rectangle returned by
     * <code>getBounds2D(BufferedImage)</code>
     * are not necessarily the same as the coordinates of the
     * <code>BufferedImage</code> returned by this method.  If the
     * upper-left corner coordinates of the rectangle are
     * negative then this part of the rectangle is not drawn.  If the
     * upper-left corner coordinates of the  rectangle are positive
     * then the filtered image is drawn at that position in the
     * destination <code>BufferedImage</code>.
     * <p>
     * An <CODE>IllegalArgumentException</CODE> is thrown if the source is
     * the same as the destination.
     *
     * @param src The <CODE>BufferedImage</CODE> to transform.
     * @param dst The <CODE>BufferedImage</CODE> in which to store the results
     * of the transformation.
     *
     * @return The filtered <CODE>BufferedImage</CODE>.
     * @throws IllegalArgumentException if <code>src</code> and
     *         <code>dst</code> are the same
     * @throws ImagingOpException if the image cannot be transformed
     *         because of a data-processing error that might be
     *         caused by an invalid image format, tile format, or
     *         image-processing operation, or any other unsupported
     *         operation.
     */
    public final BufferedImage filter(BufferedImage src, BufferedImage dst) {

        if (src == null) {
            throw new NullPointerException("src image is null");
        }
        if (src == dst) {
            throw new IllegalArgumentException("src image cannot be the "+
                                               "same as the dst image");
        }

        boolean needToConvert = false;
        ColorModel srcCM = src.getColorModel();
        ColorModel dstCM;
        BufferedImage origDst = dst;

        if (dst == null) {
            dst = createCompatibleDestImage(src, null);
            dstCM = srcCM;
            origDst = dst;
        }
        else {
            dstCM = dst.getColorModel();
            if (srcCM.getColorSpace().getType() !=
                dstCM.getColorSpace().getType())
            {
                int type = xform.getType();
                boolean needTrans = ((type&
                                      (xform.TYPE_MASK_ROTATION|
                                       xform.TYPE_GENERAL_TRANSFORM))
                                     != 0);
                if (! needTrans && type != xform.TYPE_TRANSLATION && type != xform.TYPE_IDENTITY)
                {
                    double[] mtx = new double[4];
                    xform.getMatrix(mtx);
                    // Check out the matrix.  A non-integral scale will force ARGB
                    // since the edge conditions can't be guaranteed.
                    needTrans = (mtx[0] != (int)mtx[0] || mtx[3] != (int)mtx[3]);
                }

                if (needTrans &&
                    srcCM.getTransparency() == Transparency.OPAQUE)
                {
                    // Need to convert first
                    ColorConvertOp ccop = new ColorConvertOp(hints);
                    BufferedImage tmpSrc = null;
                    int sw = src.getWidth();
                    int sh = src.getHeight();
                    if (dstCM.getTransparency() == Transparency.OPAQUE) {
                        tmpSrc = new BufferedImage(sw, sh,
                                                  BufferedImage.TYPE_INT_ARGB);
                    }
                    else {
                        WritableRaster r =
                            dstCM.createCompatibleWritableRaster(sw, sh);
                        tmpSrc = new BufferedImage(dstCM, r,
                                                  dstCM.isAlphaPremultiplied(),
                                                  null);
                    }
                    src = ccop.filter(src, tmpSrc);
                }
                else {
                    needToConvert = true;
                    dst = createCompatibleDestImage(src, null);
                }
            }

        }

        if (interpolationType != TYPE_NEAREST_NEIGHBOR &&
            dst.getColorModel() instanceof IndexColorModel) {
            dst = new BufferedImage(dst.getWidth(), dst.getHeight(),
                                    BufferedImage.TYPE_INT_ARGB);
        }
        if (ImagingLib.filter(this, src, dst) == null) {
            throw new ImagingOpException ("Unable to transform src image");
        }

        if (needToConvert) {
            ColorConvertOp ccop = new ColorConvertOp(hints);
            ccop.filter(dst, origDst);
        }
        else if (origDst != dst) {
            java.awt.Graphics2D g = origDst.createGraphics();
            try {
                g.setComposite(AlphaComposite.Src);
                g.drawImage(dst, 0, 0, null);
            } finally {
                g.dispose();
            }
        }

        return origDst;
    }

    /**
     * Transforms the source <CODE>Raster</CODE> and stores the results in
     * the destination <CODE>Raster</CODE>.  This operation performs the
     * transform band by band.
     * <p>
     * If the destination <CODE>Raster</CODE> is null, a new
     * <CODE>Raster</CODE> is created.
     * An <CODE>IllegalArgumentException</CODE> may be thrown if the source is
     * the same as the destination or if the number of bands in
     * the source is not equal to the number of bands in the
     * destination.
     * <p>
     * The coordinates of the rectangle returned by
     * <code>getBounds2D(Raster)</code>
     * are not necessarily the same as the coordinates of the
     * <code>WritableRaster</code> returned by this method.  If the
     * upper-left corner coordinates of rectangle are negative then
     * this part of the rectangle is not drawn.  If the coordinates
     * of the rectangle are positive then the filtered image is drawn at
     * that position in the destination <code>Raster</code>.
     * <p>
     * @param src The <CODE>Raster</CODE> to transform.
     * @param dst The <CODE>Raster</CODE> in which to store the results of the
     * transformation.
     *
     * @return The transformed <CODE>Raster</CODE>.
     *
     * @throws ImagingOpException if the raster cannot be transformed
     *         because of a data-processing error that might be
     *         caused by an invalid image format, tile format, or
     *         image-processing operation, or any other unsupported
     *         operation.
     */
    public final WritableRaster filter(Raster src, WritableRaster dst) {
        if (src == null) {
            throw new NullPointerException("src image is null");
        }
        if (dst == null) {
            dst = createCompatibleDestRaster(src);
        }
        if (src == dst) {
            throw new IllegalArgumentException("src image cannot be the "+
                                               "same as the dst image");
        }
        if (src.getNumBands() != dst.getNumBands()) {
            throw new IllegalArgumentException("Number of src bands ("+
                                               src.getNumBands()+
                                               ") does not match number of "+
                                               " dst bands ("+
                                               dst.getNumBands()+")");
        }

        if (ImagingLib.filter(this, src, dst) == null) {
            throw new ImagingOpException ("Unable to transform src image");
        }
        return dst;
    }

    /**
     * Returns the bounding box of the transformed destination.  The
     * rectangle returned is the actual bounding box of the
     * transformed points.  The coordinates of the upper-left corner
     * of the returned rectangle might not be (0,&nbsp;0).
     *
     * @param src The <CODE>BufferedImage</CODE> to be transformed.
     *
     * @return The <CODE>Rectangle2D</CODE> representing the destination's
     * bounding box.
     */
    public final Rectangle2D getBounds2D (BufferedImage src) {
        return getBounds2D(src.getRaster());
    }

    /**
     * Returns the bounding box of the transformed destination.  The
     * rectangle returned will be the actual bounding box of the
     * transformed points.  The coordinates of the upper-left corner
     * of the returned rectangle might not be (0,&nbsp;0).
     *
     * @param src The <CODE>Raster</CODE> to be transformed.
     *
     * @return The <CODE>Rectangle2D</CODE> representing the destination's
     * bounding box.
     */
    public final Rectangle2D getBounds2D (Raster src) {
        int w = src.getWidth();
        int h = src.getHeight();

        // Get the bounding box of the src and transform the corners
        float[] pts = {0, 0, w, 0, w, h, 0, h};
        xform.transform(pts, 0, pts, 0, 4);

        // Get the min, max of the dst
        float fmaxX = pts[0];
        float fmaxY = pts[1];
        float fminX = pts[0];
        float fminY = pts[1];
        for (int i=2; i < 8; i+=2) {
            if (pts[i] > fmaxX) {
                fmaxX = pts[i];
            }
            else if (pts[i] < fminX) {
                fminX = pts[i];
            }
            if (pts[i+1] > fmaxY) {
                fmaxY = pts[i+1];
            }
            else if (pts[i+1] < fminY) {
                fminY = pts[i+1];
            }
        }

        return new Rectangle2D.Float(fminX, fminY, fmaxX-fminX, fmaxY-fminY);
    }

    /**
     * Creates a zeroed destination image with the correct size and number of
     * bands.  A <CODE>RasterFormatException</CODE> may be thrown if the
     * transformed width or height is equal to 0.
     * <p>
     * If <CODE>destCM</CODE> is null,
     * an appropriate <CODE>ColorModel</CODE> is used; this
     * <CODE>ColorModel</CODE> may have
     * an alpha channel even if the source <CODE>ColorModel</CODE> is opaque.
     *
     * @param src  The <CODE>BufferedImage</CODE> to be transformed.
     * @param destCM  <CODE>ColorModel</CODE> of the destination.  If null,
     * an appropriate <CODE>ColorModel</CODE> is used.
     *
     * @return The zeroed destination image.
     */
    public BufferedImage createCompatibleDestImage (BufferedImage src,
                                                    ColorModel destCM) {
        BufferedImage image;
        Rectangle r = getBounds2D(src).getBounds();

        // If r.x (or r.y) is < 0, then we want to only create an image
        // that is in the positive range.
        // If r.x (or r.y) is > 0, then we need to create an image that
        // includes the translation.
        int w = r.x + r.width;
        int h = r.y + r.height;
        if (w <= 0) {
            throw new RasterFormatException("Transformed width ("+w+
                                            ") is less than or equal to 0.");
        }
        if (h <= 0) {
            throw new RasterFormatException("Transformed height ("+h+
                                            ") is less than or equal to 0.");
        }

        if (destCM == null) {
            ColorModel cm = src.getColorModel();
            if (interpolationType != TYPE_NEAREST_NEIGHBOR &&
                (cm instanceof IndexColorModel ||
                 cm.getTransparency() == Transparency.OPAQUE))
            {
                image = new BufferedImage(w, h,
                                          BufferedImage.TYPE_INT_ARGB);
            }
            else {
                image = new BufferedImage(cm,
                          src.getRaster().createCompatibleWritableRaster(w,h),
                          cm.isAlphaPremultiplied(), null);
            }
        }
        else {
            image = new BufferedImage(destCM,
                                    destCM.createCompatibleWritableRaster(w,h),
                                    destCM.isAlphaPremultiplied(), null);
        }

        return image;
    }

    /**
     * Creates a zeroed destination <CODE>Raster</CODE> with the correct size
     * and number of bands.  A <CODE>RasterFormatException</CODE> may be thrown
     * if the transformed width or height is equal to 0.
     *
     * @param src The <CODE>Raster</CODE> to be transformed.
     *
     * @return The zeroed destination <CODE>Raster</CODE>.
     */
    public WritableRaster createCompatibleDestRaster (Raster src) {
        Rectangle2D r = getBounds2D(src);

        return src.createCompatibleWritableRaster((int)r.getX(),
                                                  (int)r.getY(),
                                                  (int)r.getWidth(),
                                                  (int)r.getHeight());
    }

    /**
     * Returns the location of the corresponding destination point given a
     * point in the source.  If <CODE>dstPt</CODE> is specified, it
     * is used to hold the return value.
     *
     * @param srcPt The <code>Point2D</code> that represents the source
     *              point.
     * @param dstPt The <CODE>Point2D</CODE> in which to store the result.
     *
     * @return The <CODE>Point2D</CODE> in the destination that corresponds to
     * the specified point in the source.
     */
    public final Point2D getPoint2D (Point2D srcPt, Point2D dstPt) {
        return xform.transform (srcPt, dstPt);
    }

    /**
     * Returns the affine transform used by this transform operation.
     *
     * @return The <CODE>AffineTransform</CODE> associated with this op.
     */
    public final AffineTransform getTransform() {
        return (AffineTransform) xform.clone();
    }

    /**
     * Returns the rendering hints used by this transform operation.
     *
     * @return The <CODE>RenderingHints</CODE> object associated with this op.
     */
    public final RenderingHints getRenderingHints() {
        if (hints == null) {
            Object val;
            switch(interpolationType) {
            case TYPE_NEAREST_NEIGHBOR:
                val = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
                break;
            case TYPE_BILINEAR:
                val = RenderingHints.VALUE_INTERPOLATION_BILINEAR;
                break;
            case TYPE_BICUBIC:
                val = RenderingHints.VALUE_INTERPOLATION_BICUBIC;
                break;
            default:
                // Should never get here
                throw new InternalError("Unknown interpolation type "+
                                         interpolationType);

            }
            hints = new RenderingHints(RenderingHints.KEY_INTERPOLATION, val);
        }

        return hints;
    }

    // We need to be able to invert the transform if we want to
    // transform the image.  If the determinant of the matrix is 0,
    // then we can't invert the transform.
    void validateTransform(AffineTransform xform) {
        if (Math.abs(xform.getDeterminant()) <= Double.MIN_VALUE) {
            throw new ImagingOpException("Unable to invert transform "+xform);
        }
    }
}
