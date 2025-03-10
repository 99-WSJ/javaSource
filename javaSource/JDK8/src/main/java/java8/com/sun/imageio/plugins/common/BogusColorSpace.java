/*
 * Copyright (c) 2003, Oracle and/or its affiliates. All rights reserved.
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

import java.awt.color.ColorSpace;

/**
 * A dummy <code>ColorSpace</code> to enable <code>ColorModel</code>
 * for image data which do not have an innate color representation.
 */
public class BogusColorSpace extends ColorSpace {
    /**
     * Return the type given the number of components.
     *
     * @param numComponents The number of components in the
     * <code>ColorSpace</code>.
     * @exception IllegalArgumentException if <code>numComponents</code>
     * is less than 1.
     */
    private static int getType(int numComponents) {
        if(numComponents < 1) {
            throw new IllegalArgumentException("numComponents < 1!");
        }

        int type;
        switch(numComponents) {
        case 1:
            type = ColorSpace.TYPE_GRAY;
            break;
        default:
            // Based on the constant definitions TYPE_2CLR=12 through
            // TYPE_FCLR=25. This will return unknown types for
            // numComponents > 15.
            type = numComponents + 10;
        }

        return type;
    }

    /**
     * Constructs a bogus <code>ColorSpace</code>.
     *
     * @param numComponents The number of components in the
     * <code>ColorSpace</code>.
     * @exception IllegalArgumentException if <code>numComponents</code>
     * is less than 1.
     */
    public BogusColorSpace(int numComponents) {
        super(getType(numComponents), numComponents);
    }

    //
    // The following methods simply copy the input array to the
    // output array while otherwise attempting to adhere to the
    // specified behavior of the methods vis-a-vis exceptions.
    //

    public float[] toRGB(float[] colorvalue) {
        if(colorvalue.length < getNumComponents()) {
            throw new ArrayIndexOutOfBoundsException
                ("colorvalue.length < getNumComponents()");
        }

        float[] rgbvalue = new float[3];

        System.arraycopy(colorvalue, 0, rgbvalue, 0,
                         Math.min(3, getNumComponents()));

        return colorvalue;
    }

    public float[] fromRGB(float[] rgbvalue) {
        if(rgbvalue.length < 3) {
            throw new ArrayIndexOutOfBoundsException
                ("rgbvalue.length < 3");
        }

        float[] colorvalue = new float[getNumComponents()];

        System.arraycopy(rgbvalue, 0, colorvalue, 0,
                         Math.min(3, colorvalue.length));

        return rgbvalue;
    }

    public float[] toCIEXYZ(float[] colorvalue) {
        if(colorvalue.length < getNumComponents()) {
            throw new ArrayIndexOutOfBoundsException
                ("colorvalue.length < getNumComponents()");
        }

        float[] xyzvalue = new float[3];

        System.arraycopy(colorvalue, 0, xyzvalue, 0,
                         Math.min(3, getNumComponents()));

        return colorvalue;
    }

    public float[] fromCIEXYZ(float[] xyzvalue) {
        if(xyzvalue.length < 3) {
            throw new ArrayIndexOutOfBoundsException
                ("xyzvalue.length < 3");
        }

        float[] colorvalue = new float[getNumComponents()];

        System.arraycopy(xyzvalue, 0, colorvalue, 0,
                         Math.min(3, colorvalue.length));

        return xyzvalue;
    }
}
