/*
 * Copyright (c) 1997, 2005, Oracle and/or its affiliates. All rights reserved.
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
import java.awt.image.Raster;
import java.awt.image.RasterOp;
import java.awt.image.WritableRaster;
import java.util.Arrays;

/**
 * This class performs an arbitrary linear combination of the bands
 * in a <CODE>Raster</CODE>, using a specified matrix.
 * <p>
 * The width of the matrix must be equal to the number of bands in the
 * source <CODE>Raster</CODE>, optionally plus one.  If there is one more
 * column in the matrix than the number of bands, there is an implied 1 at the
 * end of the vector of band samples representing a pixel.  The height
 * of the matrix must be equal to the number of bands in the destination.
 * <p>
 * For example, a 3-banded <CODE>Raster</CODE> might have the following
 * transformation applied to each pixel in order to invert the second band of
 * the <CODE>Raster</CODE>.
 * <pre>
 *   [ 1.0   0.0   0.0    0.0  ]     [ b1 ]
 *   [ 0.0  -1.0   0.0  255.0  ]  x  [ b2 ]
 *   [ 0.0   0.0   1.0    0.0  ]     [ b3 ]
 *                                   [ 1 ]
 * </pre>
 *
 * <p>
 * Note that the source and destination can be the same object.
 */
public class BandCombineOp implements  RasterOp {
    float[][] matrix;
    int nrows = 0;
    int ncols = 0;
    RenderingHints hints;

    /**
     * Constructs a <CODE>BandCombineOp</CODE> with the specified matrix.
     * The width of the matrix must be equal to the number of bands in
     * the source <CODE>Raster</CODE>, optionally plus one.  If there is one
     * more column in the matrix than the number of bands, there is an implied
     * 1 at the end of the vector of band samples representing a pixel.  The
     * height of the matrix must be equal to the number of bands in the
     * destination.
     * <p>
     * The first subscript is the row index and the second
     * is the column index.  This operation uses none of the currently
     * defined rendering hints; the <CODE>RenderingHints</CODE> argument can be
     * null.
     *
     * @param matrix The matrix to use for the band combine operation.
     * @param hints The <CODE>RenderingHints</CODE> object for this operation.
     * Not currently used so it can be null.
     */
    public BandCombineOp (float[][] matrix, RenderingHints hints) {
        nrows = matrix.length;
        ncols = matrix[0].length;
        this.matrix = new float[nrows][];
        for (int i=0; i < nrows; i++) {
            /* Arrays.copyOf is forgiving of the source array being
             * too short, but it is also faster than other cloning
             * methods, so we provide our own protection for short
             * matrix rows.
             */
            if (ncols > matrix[i].length) {
                throw new IndexOutOfBoundsException("row "+i+" too short");
            }
            this.matrix[i] = Arrays.copyOf(matrix[i], ncols);
        }
        this.hints  = hints;
    }

    /**
     * Returns a copy of the linear combination matrix.
     *
     * @return The matrix associated with this band combine operation.
     */
    public final float[][] getMatrix() {
        float[][] ret = new float[nrows][];
        for (int i = 0; i < nrows; i++) {
            ret[i] = Arrays.copyOf(matrix[i], ncols);
        }
        return ret;
    }

    /**
     * Transforms the <CODE>Raster</CODE> using the matrix specified in the
     * constructor. An <CODE>IllegalArgumentException</CODE> may be thrown if
     * the number of bands in the source or destination is incompatible with
     * the matrix.  See the class comments for more details.
     * <p>
     * If the destination is null, it will be created with a number of bands
     * equalling the number of rows in the matrix. No exception is thrown
     * if the operation causes a data overflow.
     *
     * @param src The <CODE>Raster</CODE> to be filtered.
     * @param dst The <CODE>Raster</CODE> in which to store the results
     * of the filter operation.
     *
     * @return The filtered <CODE>Raster</CODE>.
     *
     * @throws IllegalArgumentException If the number of bands in the
     * source or destination is incompatible with the matrix.
     */
    public WritableRaster filter(Raster src, WritableRaster dst) {
        int nBands = src.getNumBands();
        if (ncols != nBands && ncols != (nBands+1)) {
            throw new IllegalArgumentException("Number of columns in the "+
                                               "matrix ("+ncols+
                                               ") must be equal to the number"+
                                               " of bands ([+1]) in src ("+
                                               nBands+").");
        }
        if (dst == null) {
            dst = createCompatibleDestRaster(src);
        }
        else if (nrows != dst.getNumBands()) {
            throw new IllegalArgumentException("Number of rows in the "+
                                               "matrix ("+nrows+
                                               ") must be equal to the number"+
                                               " of bands ([+1]) in dst ("+
                                               nBands+").");
        }

        if (ImagingLib.filter(this, src, dst) != null) {
            return dst;
        }

        int[] pixel = null;
        int[] dstPixel = new int[dst.getNumBands()];
        float accum;
        int sminX = src.getMinX();
        int sY = src.getMinY();
        int dminX = dst.getMinX();
        int dY = dst.getMinY();
        int sX;
        int dX;
        if (ncols == nBands) {
            for (int y=0; y < src.getHeight(); y++, sY++, dY++) {
                dX = dminX;
                sX = sminX;
                for (int x=0; x < src.getWidth(); x++, sX++, dX++) {
                    pixel = src.getPixel(sX, sY, pixel);
                    for (int r=0; r < nrows; r++) {
                        accum = 0.f;
                        for (int c=0; c < ncols; c++) {
                            accum += matrix[r][c]*pixel[c];
                        }
                        dstPixel[r] = (int) accum;
                    }
                    dst.setPixel(dX, dY, dstPixel);
                }
            }
        }
        else {
            // Need to add constant
            for (int y=0; y < src.getHeight(); y++, sY++, dY++) {
                dX = dminX;
                sX = sminX;
                for (int x=0; x < src.getWidth(); x++, sX++, dX++) {
                    pixel = src.getPixel(sX, sY, pixel);
                    for (int r=0; r < nrows; r++) {
                        accum = 0.f;
                        for (int c=0; c < nBands; c++) {
                            accum += matrix[r][c]*pixel[c];
                        }
                        dstPixel[r] = (int) (accum+matrix[r][nBands]);
                    }
                    dst.setPixel(dX, dY, dstPixel);
                }
            }
        }

        return dst;
    }

    /**
     * Returns the bounding box of the transformed destination.  Since
     * this is not a geometric operation, the bounding box is the same for
     * the source and destination.
     * An <CODE>IllegalArgumentException</CODE> may be thrown if the number of
     * bands in the source is incompatible with the matrix.  See
     * the class comments for more details.
     *
     * @param src The <CODE>Raster</CODE> to be filtered.
     *
     * @return The <CODE>Rectangle2D</CODE> representing the destination
     * image's bounding box.
     *
     * @throws IllegalArgumentException If the number of bands in the source
     * is incompatible with the matrix.
     */
    public final Rectangle2D getBounds2D (Raster src) {
        return src.getBounds();
    }


    /**
     * Creates a zeroed destination <CODE>Raster</CODE> with the correct size
     * and number of bands.
     * An <CODE>IllegalArgumentException</CODE> may be thrown if the number of
     * bands in the source is incompatible with the matrix.  See
     * the class comments for more details.
     *
     * @param src The <CODE>Raster</CODE> to be filtered.
     *
     * @return The zeroed destination <CODE>Raster</CODE>.
     */
    public WritableRaster createCompatibleDestRaster (Raster src) {
        int nBands = src.getNumBands();
        if ((ncols != nBands) && (ncols != (nBands+1))) {
            throw new IllegalArgumentException("Number of columns in the "+
                                               "matrix ("+ncols+
                                               ") must be equal to the number"+
                                               " of bands ([+1]) in src ("+
                                               nBands+").");
        }
        if (src.getNumBands() == nrows) {
            return src.createCompatibleWritableRaster();
        }
        else {
            throw new IllegalArgumentException("Don't know how to create a "+
                                               " compatible Raster with "+
                                               nrows+" bands.");
        }
    }

    /**
     * Returns the location of the corresponding destination point given a
     * point in the source <CODE>Raster</CODE>.  If <CODE>dstPt</CODE> is
     * specified, it is used to hold the return value.
     * Since this is not a geometric operation, the point returned
     * is the same as the specified <CODE>srcPt</CODE>.
     *
     * @param srcPt The <code>Point2D</code> that represents the point in
     *              the source <code>Raster</code>
     * @param dstPt The <CODE>Point2D</CODE> in which to store the result.
     *
     * @return The <CODE>Point2D</CODE> in the destination image that
     * corresponds to the specified point in the source image.
     */
    public final Point2D getPoint2D (Point2D srcPt, Point2D dstPt) {
        if (dstPt == null) {
            dstPt = new Point2D.Float();
        }
        dstPt.setLocation(srcPt.getX(), srcPt.getY());

        return dstPt;
    }

    /**
     * Returns the rendering hints for this operation.
     *
     * @return The <CODE>RenderingHints</CODE> object associated with this
     * operation.  Returns null if no hints have been set.
     */
    public final RenderingHints getRenderingHints() {
        return hints;
    }
}
