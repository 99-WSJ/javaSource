/*
 * Copyright (c) 2000, Oracle and/or its affiliates. All rights reserved.
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

package java8.sun.imageio.plugins.png;

import com.sun.imageio.plugins.png.PNGImageReader;

public class RowFilter {

    private static final int abs(int x) {
        return (x < 0) ? -x : x;
    }

    // Returns the sum of absolute differences
    protected static int subFilter(byte[] currRow,
                                   byte[] subFilteredRow,
                                   int bytesPerPixel,
                                   int bytesPerRow) {
        int badness = 0;
        for (int i = bytesPerPixel; i < bytesPerRow + bytesPerPixel; i++) {
            int curr = currRow[i] & 0xff;
            int left = currRow[i - bytesPerPixel] & 0xff;
            int difference = curr - left;
            subFilteredRow[i] = (byte)difference;

            badness += abs(difference);
        }

        return badness;
    }

    // Returns the sum of absolute differences
    protected static int upFilter(byte[] currRow,
                                  byte[] prevRow,
                                  byte[] upFilteredRow,
                                  int bytesPerPixel,
                                  int bytesPerRow) {
        int badness = 0;
        for (int i = bytesPerPixel; i < bytesPerRow + bytesPerPixel; i++) {
            int curr = currRow[i] & 0xff;
            int up = prevRow[i] & 0xff;
            int difference = curr - up;
            upFilteredRow[i] = (byte)difference;

            badness += abs(difference);
        }

        return badness;
    }

    protected final int paethPredictor(int a, int b, int c) {
        int p = a + b - c;
        int pa = abs(p - a);
        int pb = abs(p - b);
        int pc = abs(p - c);

        if ((pa <= pb) && (pa <= pc)) {
            return a;
        } else if (pb <= pc) {
            return b;
        } else {
            return c;
        }
    }

    public int filterRow(int colorType,
                         byte[] currRow,
                         byte[] prevRow,
                         byte[][] scratchRows,
                         int bytesPerRow,
                         int bytesPerPixel) {

        // Use type 0 for palette images
        if (colorType != PNGImageReader.PNG_COLOR_PALETTE) {
            System.arraycopy(currRow, bytesPerPixel,
                             scratchRows[0], bytesPerPixel,
                             bytesPerRow);
            return 0;
        }

        int[] filterBadness = new int[5];
        for (int i = 0; i < 5; i++) {
            filterBadness[i] = Integer.MAX_VALUE;
        }

        {
            int badness = 0;

            for (int i = bytesPerPixel; i < bytesPerRow + bytesPerPixel; i++) {
                int curr = currRow[i] & 0xff;
                badness += curr;
            }

            filterBadness[0] = badness;
        }

        {
            byte[] subFilteredRow = scratchRows[1];
            int badness = subFilter(currRow,
                                    subFilteredRow,
                                    bytesPerPixel,
                                    bytesPerRow);

            filterBadness[1] = badness;
        }

        {
            byte[] upFilteredRow = scratchRows[2];
            int badness = upFilter(currRow,
                                   prevRow,
                                   upFilteredRow,
                                   bytesPerPixel,
                                   bytesPerRow);

            filterBadness[2] = badness;
        }

        {
            byte[] averageFilteredRow = scratchRows[3];
            int badness = 0;

            for (int i = bytesPerPixel; i < bytesPerRow + bytesPerPixel; i++) {
                int curr = currRow[i] & 0xff;
                int left = currRow[i - bytesPerPixel] & 0xff;
                int up = prevRow[i] & 0xff;
                int difference = curr - (left + up)/2;;
                averageFilteredRow[i] = (byte)difference;

                badness += abs(difference);
            }

            filterBadness[3] = badness;
        }

        {
            byte[] paethFilteredRow = scratchRows[4];
            int badness = 0;

            for (int i = bytesPerPixel; i < bytesPerRow + bytesPerPixel; i++) {
                int curr = currRow[i] & 0xff;
                int left = currRow[i - bytesPerPixel] & 0xff;
                int up = prevRow[i] & 0xff;
                int upleft = prevRow[i - bytesPerPixel] & 0xff;
                int predictor = paethPredictor(left, up, upleft);
                int difference = curr - predictor;
                paethFilteredRow[i] = (byte)difference;

                badness += abs(difference);
            }

            filterBadness[4] = badness;
        }

        int minBadness = filterBadness[0];
        int filterType = 0;

        for (int i = 1; i < 5; i++) {
            if (filterBadness[i] < minBadness) {
                minBadness = filterBadness[i];
                filterType = i;
            }
        }

        if (filterType == 0) {
            System.arraycopy(currRow, bytesPerPixel,
                             scratchRows[0], bytesPerPixel,
                             bytesPerRow);
        }

        return filterType;
    }
}
