/*
 * Copyright (c) 2005, Oracle and/or its affiliates. All rights reserved.
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

package java8.sun.imageio.plugins.common;

import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.io.IOException;

/**
 * This class contains utility methods that may be useful to ImageReader
 * plugins.  Ideally these methods would be in the ImageReader base class
 * so that all subclasses could benefit from them, but that would be an
 * addition to the existing API, and it is not yet clear whether these methods
 * are universally useful, so for now we will leave them here.
 */
public class ReaderUtil {

    // Helper for computeUpdatedPixels method
    private static void computeUpdatedPixels(int sourceOffset,
                                             int sourceExtent,
                                             int destinationOffset,
                                             int dstMin,
                                             int dstMax,
                                             int sourceSubsampling,
                                             int passStart,
                                             int passExtent,
                                             int passPeriod,
                                             int[] vals,
                                             int offset)
    {
        // We need to satisfy the congruences:
        // dst = destinationOffset + (src - sourceOffset)/sourceSubsampling
        //
        // src - passStart == 0 (mod passPeriod)
        // src - sourceOffset == 0 (mod sourceSubsampling)
        //
        // subject to the inequalities:
        //
        // src >= passStart
        // src < passStart + passExtent
        // src >= sourceOffset
        // src < sourceOffset + sourceExtent
        // dst >= dstMin
        // dst <= dstmax
        //
        // where
        //
        // dst = destinationOffset + (src - sourceOffset)/sourceSubsampling
        //
        // For now we use a brute-force approach although we could
        // attempt to analyze the congruences.  If passPeriod and
        // sourceSubsamling are relatively prime, the period will be
        // their product.  If they share a common factor, either the
        // period will be equal to the larger value, or the sequences
        // will be completely disjoint, depending on the relationship
        // between passStart and sourceOffset.  Since we only have to do this
        // twice per image (once each for X and Y), it seems cheap enough
        // to do it the straightforward way.

        boolean gotPixel = false;
        int firstDst = -1;
        int secondDst = -1;
        int lastDst = -1;

        for (int i = 0; i < passExtent; i++) {
            int src = passStart + i*passPeriod;
            if (src < sourceOffset) {
                continue;
            }
            if ((src - sourceOffset) % sourceSubsampling != 0) {
                continue;
            }
            if (src >= sourceOffset + sourceExtent) {
                break;
            }

            int dst = destinationOffset +
                (src - sourceOffset)/sourceSubsampling;
            if (dst < dstMin) {
                continue;
            }
            if (dst > dstMax) {
                break;
            }

            if (!gotPixel) {
                firstDst = dst; // Record smallest valid pixel
                gotPixel = true;
            } else if (secondDst == -1) {
                secondDst = dst; // Record second smallest valid pixel
            }
            lastDst = dst; // Record largest valid pixel
        }

        vals[offset] = firstDst;

        // If we never saw a valid pixel, set width to 0
        if (!gotPixel) {
            vals[offset + 2] = 0;
        } else {
            vals[offset + 2] = lastDst - firstDst + 1;
        }

        // The period is given by the difference of any two adjacent pixels
        vals[offset + 4] = Math.max(secondDst - firstDst, 1);
    }

    /**
     * A utility method that computes the exact set of destination
     * pixels that will be written during a particular decoding pass.
     * The intent is to simplify the work done by readers in combining
     * the source region, source subsampling, and destination offset
     * information obtained from the <code>ImageReadParam</code> with
     * the offsets and periods of a progressive or interlaced decoding
     * pass.
     *
     * @param sourceRegion a <code>Rectangle</code> containing the
     * source region being read, offset by the source subsampling
     * offsets, and clipped against the source bounds, as returned by
     * the <code>getSourceRegion</code> method.
     * @param destinationOffset a <code>Point</code> containing the
     * coordinates of the upper-left pixel to be written in the
     * destination.
     * @param dstMinX the smallest X coordinate (inclusive) of the
     * destination <code>Raster</code>.
     * @param dstMinY the smallest Y coordinate (inclusive) of the
     * destination <code>Raster</code>.
     * @param dstMaxX the largest X coordinate (inclusive) of the destination
     * <code>Raster</code>.
     * @param dstMaxY the largest Y coordinate (inclusive) of the destination
     * <code>Raster</code>.
     * @param sourceXSubsampling the X subsampling factor.
     * @param sourceYSubsampling the Y subsampling factor.
     * @param passXStart the smallest source X coordinate (inclusive)
     * of the current progressive pass.
     * @param passYStart the smallest source Y coordinate (inclusive)
     * of the current progressive pass.
     * @param passWidth the width in pixels of the current progressive
     * pass.
     * @param passHeight the height in pixels of the current progressive
     * pass.
     * @param passPeriodX the X period (horizontal spacing between
     * pixels) of the current progressive pass.
     * @param passPeriodY the Y period (vertical spacing between
     * pixels) of the current progressive pass.
     *
     * @return an array of 6 <code>int</code>s containing the
     * destination min X, min Y, width, height, X period and Y period
     * of the region that will be updated.
     */
    public static int[] computeUpdatedPixels(Rectangle sourceRegion,
                                             Point destinationOffset,
                                             int dstMinX,
                                             int dstMinY,
                                             int dstMaxX,
                                             int dstMaxY,
                                             int sourceXSubsampling,
                                             int sourceYSubsampling,
                                             int passXStart,
                                             int passYStart,
                                             int passWidth,
                                             int passHeight,
                                             int passPeriodX,
                                             int passPeriodY)
    {
        int[] vals = new int[6];
        computeUpdatedPixels(sourceRegion.x, sourceRegion.width,
                             destinationOffset.x,
                             dstMinX, dstMaxX, sourceXSubsampling,
                             passXStart, passWidth, passPeriodX,
                             vals, 0);
        computeUpdatedPixels(sourceRegion.y, sourceRegion.height,
                             destinationOffset.y,
                             dstMinY, dstMaxY, sourceYSubsampling,
                             passYStart, passHeight, passPeriodY,
                             vals, 1);
        return vals;
    }

    public static int readMultiByteInteger(ImageInputStream iis)
        throws IOException
    {
        int value = iis.readByte();
        int result = value & 0x7f;
        while((value & 0x80) == 0x80) {
            result <<= 7;
            value = iis.readByte();
            result |= (value & 0x7f);
        }
        return result;
    }
}
