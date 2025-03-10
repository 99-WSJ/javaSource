/*
 * Copyright (c) 2008, Oracle and/or its affiliates. All rights reserved.
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

package java8.javax.swing.colorchooser;

import javax.swing.colorchooser.ColorModel;
import javax.swing.colorchooser.ColorModelHSL;

final class ColorModelCMYK extends ColorModel {

    ColorModelCMYK() {
        super("cmyk", "Cyan", "Magenta", "Yellow", "Black", "Alpha"); // NON-NLS: components
    }

    @Override
    void setColor(int color, float[] space) {
        super.setColor(color, space);
        space[4] = space[3];
        RGBtoCMYK(space, space);
    }

    @Override
    int getColor(float[] space) {
        CMYKtoRGB(space, space);
        space[3] = space[4];
        return super.getColor(space);
    }

    /**
     * Converts CMYK components of a color to a set of RGB components.
     *
     * @param cmyk  a float array with length equal to
     *              the number of CMYK components
     * @param rgb   a float array with length of at least 3
     *              that contains RGB components of a color
     * @return a float array that contains RGB components
     */
    private static float[] CMYKtoRGB(float[] cmyk, float[] rgb) {
        if (rgb == null) {
            rgb = new float[3];
        }
        rgb[0] = 1.0f + cmyk[0] * cmyk[3] - cmyk[3] - cmyk[0];
        rgb[1] = 1.0f + cmyk[1] * cmyk[3] - cmyk[3] - cmyk[1];
        rgb[2] = 1.0f + cmyk[2] * cmyk[3] - cmyk[3] - cmyk[2];
        return rgb;
    }

    /**
     * Converts RGB components of a color to a set of CMYK components.
     *
     * @param rgb   a float array with length of at least 3
     *              that contains RGB components of a color
     * @param cmyk  a float array with length equal to
     *              the number of CMYK components
     * @return a float array that contains CMYK components
     */
    private static float[] RGBtoCMYK(float[] rgb, float[] cmyk) {
        if (cmyk == null) {
            cmyk = new float[4];
        }
        float max = ColorModelHSL.max(rgb[0], rgb[1], rgb[2]);
        if (max > 0.0f) {
            cmyk[0] = 1.0f - rgb[0] / max;
            cmyk[1] = 1.0f - rgb[1] / max;
            cmyk[2] = 1.0f - rgb[2] / max;
        }
        else {
            cmyk[0] = 0.0f;
            cmyk[1] = 0.0f;
            cmyk[2] = 0.0f;
        }
        cmyk[3] = 1.0f - max;
        return cmyk;
    }
}
