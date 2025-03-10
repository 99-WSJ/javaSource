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


import java.awt.image.ByteLookupTable;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;

/**
 * This class defines a lookup table object.  The output of a
 * lookup operation using an object of this class is interpreted
 * as an unsigned short quantity.  The lookup table contains short
 * data arrays for one or more bands (or components) of an image,
 * and it contains an offset which will be subtracted from the
 * input values before indexing the arrays.  This allows an array
 * smaller than the native data size to be provided for a
 * constrained input.  If there is only one array in the lookup
 * table, it will be applied to all bands.
 *
 * @see ByteLookupTable
 * @see LookupOp
 */
public class ShortLookupTable extends LookupTable {

    /**
     * Constants
     */

    short data[][];

    /**
     * Constructs a ShortLookupTable object from an array of short
     * arrays representing a lookup table for each
     * band.  The offset will be subtracted from the input
     * values before indexing into the arrays.  The number of
     * bands is the length of the data argument.  The
     * data array for each band is stored as a reference.
     * @param offset the value subtracted from the input values
     *        before indexing into the arrays
     * @param data an array of short arrays representing a lookup
     *        table for each band
     */
    public ShortLookupTable(int offset, short data[][]) {
        super(offset,data.length);
        numComponents = data.length;
        numEntries    = data[0].length;
        this.data = new short[numComponents][];
        // Allocate the array and copy the data reference
        for (int i=0; i < numComponents; i++) {
            this.data[i] = data[i];
        }
    }

    /**
     * Constructs a ShortLookupTable object from an array
     * of shorts representing a lookup table for each
     * band.  The offset will be subtracted from the input
     * values before indexing into the array.  The
     * data array is stored as a reference.
     * @param offset the value subtracted from the input values
     *        before indexing into the arrays
     * @param data an array of shorts
     */
    public ShortLookupTable(int offset, short data[]) {
        super(offset,data.length);
        numComponents = 1;
        numEntries    = data.length;
        this.data     = new short[1][];
        this.data[0]  = data;
    }

    /**
     * Returns the lookup table data by reference.  If this ShortLookupTable
     * was constructed using a single short array, the length of the returned
     * array is one.
     * @return ShortLookupTable data array.
     */
    public final short[][] getTable(){
        return data;
    }

    /**
     * Returns an array of samples of a pixel, translated with the lookup
     * table. The source and destination array can be the same array.
     * Array <code>dst</code> is returned.
     *
     * @param src the source array.
     * @param dst the destination array. This array must be at least as
     *         long as <code>src</code>.  If <code>dst</code> is
     *         <code>null</code>, a new array will be allocated having the
     *         same length as <code>src</code>.
     * @return the array <code>dst</code>, an <code>int</code> array of
     *         samples.
     * @exception ArrayIndexOutOfBoundsException if <code>src</code> is
     *            longer than <code>dst</code> or if for any element
     *            <code>i</code> of <code>src</code>,
     *            {@code (src[i]&0xffff)-offset} is either less than
     *            zero or greater than or equal to the length of the
     *            lookup table for any band.
     */
    public int[] lookupPixel(int[] src, int[] dst){
        if (dst == null) {
            // Need to alloc a new destination array
            dst = new int[src.length];
        }

        if (numComponents == 1) {
            // Apply one LUT to all channels
            for (int i=0; i < src.length; i++) {
                int s = (src[i]&0xffff) - offset;
                if (s < 0) {
                    throw new ArrayIndexOutOfBoundsException("src["+i+
                                                             "]-offset is "+
                                                             "less than zero");
                }
                dst[i] = (int) data[0][s];
            }
        }
        else {
            for (int i=0; i < src.length; i++) {
                int s = (src[i]&0xffff) - offset;
                if (s < 0) {
                    throw new ArrayIndexOutOfBoundsException("src["+i+
                                                             "]-offset is "+
                                                             "less than zero");
                }
                dst[i] = (int) data[i][s];
            }
        }
        return dst;
    }

    /**
     * Returns an array of samples of a pixel, translated with the lookup
     * table. The source and destination array can be the same array.
     * Array <code>dst</code> is returned.
     *
     * @param src the source array.
     * @param dst the destination array. This array must be at least as
     *         long as <code>src</code>.  If <code>dst</code> is
     *         <code>null</code>, a new array will be allocated having the
     *         same length as <code>src</code>.
     * @return the array <code>dst</code>, an <code>int</code> array of
     *         samples.
     * @exception ArrayIndexOutOfBoundsException if <code>src</code> is
     *            longer than <code>dst</code> or if for any element
     *            <code>i</code> of <code>src</code>,
     *            {@code (src[i]&0xffff)-offset} is either less than
     *            zero or greater than or equal to the length of the
     *            lookup table for any band.
     */
    public short[] lookupPixel(short[] src, short[] dst){
        if (dst == null) {
            // Need to alloc a new destination array
            dst = new short[src.length];
        }

        if (numComponents == 1) {
            // Apply one LUT to all channels
            for (int i=0; i < src.length; i++) {
                int s = (src[i]&0xffff) - offset;
                if (s < 0) {
                    throw new ArrayIndexOutOfBoundsException("src["+i+
                                                             "]-offset is "+
                                                             "less than zero");
                }
                dst[i] = data[0][s];
            }
        }
        else {
            for (int i=0; i < src.length; i++) {
                int s = (src[i]&0xffff) - offset;
                if (s < 0) {
                    throw new ArrayIndexOutOfBoundsException("src["+i+
                                                             "]-offset is "+
                                                             "less than zero");
                }
                dst[i] = data[i][s];
            }
        }
        return dst;
    }

}
