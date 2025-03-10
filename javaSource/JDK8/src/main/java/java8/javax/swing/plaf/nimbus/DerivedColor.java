/*
 * Copyright (c) 2005, 2006, Oracle and/or its affiliates. All rights reserved.
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
package java8.javax.swing.plaf.nimbus;

import javax.swing.UIManager;
import java.awt.Color;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

/**
 * DerivedColor - A color implementation that is derived from a UIManager
 * defaults table color and a set of offsets. It can be rederived at any point
 * by calling rederiveColor(). For example when its parent color changes and it
 * value will update to reflect the new derived color. Property change events
 * are fired for the "rgb" property when the derived color changes.
 *
 * @author Jasper Potts
 */
class DerivedColor extends Color {
    private final String uiDefaultParentName;
    private final float hOffset, sOffset, bOffset;
    private final int aOffset;
    private int argbValue;

    DerivedColor(String uiDefaultParentName, float hOffset, float sOffset, float bOffset, int aOffset) {
        super(0);
        this.uiDefaultParentName = uiDefaultParentName;
        this.hOffset = hOffset;
        this.sOffset = sOffset;
        this.bOffset = bOffset;
        this.aOffset = aOffset;
    }

    public String getUiDefaultParentName() {
        return uiDefaultParentName;
    }

    public float getHueOffset() {
        return hOffset;
    }

    public float getSaturationOffset() {
        return sOffset;
    }

    public float getBrightnessOffset() {
        return bOffset;
    }

    public int getAlphaOffset() {
        return aOffset;
    }

    /**
     * Recalculate the derived color from the UIManager parent color and offsets
     */
    public void rederiveColor() {
        Color src = UIManager.getColor(uiDefaultParentName);
        if (src != null) {
            float[] tmp = Color.RGBtoHSB(src.getRed(), src.getGreen(), src.getBlue(), null);
            // apply offsets
            tmp[0] = clamp(tmp[0] + hOffset);
            tmp[1] = clamp(tmp[1] + sOffset);
            tmp[2] = clamp(tmp[2] + bOffset);
            int alpha = clamp(src.getAlpha() + aOffset);
            argbValue = (Color.HSBtoRGB(tmp[0], tmp[1], tmp[2]) & 0xFFFFFF) | (alpha << 24);
        } else {
            float[] tmp = new float[3];
            tmp[0] = clamp(hOffset);
            tmp[1] = clamp(sOffset);
            tmp[2] = clamp(bOffset);
            int alpha = clamp(aOffset);
            argbValue = (Color.HSBtoRGB(tmp[0], tmp[1], tmp[2]) & 0xFFFFFF) | (alpha << 24);
        }
    }

    /**
     * Returns the RGB value representing the color in the default sRGB {@link java.awt.image.ColorModel}. (Bits 24-31
     * are alpha, 16-23 are red, 8-15 are green, 0-7 are blue).
     *
     * @return the RGB value of the color in the default sRGB <code>ColorModel</code>.
     * @see java.awt.image.ColorModel#getRGBdefault
     * @see #getRed
     * @see #getGreen
     * @see #getBlue
     * @since JDK1.0
     */
    @Override public int getRGB() {
        return argbValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof javax.swing.plaf.nimbus.DerivedColor)) return false;
        javax.swing.plaf.nimbus.DerivedColor that = (javax.swing.plaf.nimbus.DerivedColor) o;
        if (aOffset != that.aOffset) return false;
        if (Float.compare(that.bOffset, bOffset) != 0) return false;
        if (Float.compare(that.hOffset, hOffset) != 0) return false;
        if (Float.compare(that.sOffset, sOffset) != 0) return false;
        if (!uiDefaultParentName.equals(that.uiDefaultParentName)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = uiDefaultParentName.hashCode();
        result = 31 * result + hOffset != +0.0f ?
                Float.floatToIntBits(hOffset) : 0;
        result = 31 * result + sOffset != +0.0f ?
                Float.floatToIntBits(sOffset) : 0;
        result = 31 * result + bOffset != +0.0f ?
                Float.floatToIntBits(bOffset) : 0;
        result = 31 * result + aOffset;
        return result;
    }

    private float clamp(float value) {
        if (value < 0) {
            value = 0;
        } else if (value > 1) {
            value = 1;
        }
        return value;
    }

    private int clamp(int value) {
        if (value < 0) {
            value = 0;
        } else if (value > 255) {
            value = 255;
        }
        return value;
    }

    /**
     * Returns a string representation of this <code>Color</code>. This method
     * is intended to be used only for debugging purposes. The content and
     * format of the returned string might vary between implementations. The
     * returned string might be empty but cannot be <code>null</code>.
     *
     * @return a String representation of this <code>Color</code>.
     */
    @Override
    public String toString() {
        Color src = UIManager.getColor(uiDefaultParentName);
        String s = "DerivedColor(color=" + getRed() + "," + getGreen() + "," + getBlue() +
                " parent=" + uiDefaultParentName +
                " offsets=" + getHueOffset() + "," + getSaturationOffset() + ","
                + getBrightnessOffset() + "," + getAlphaOffset();
        return src == null ? s : s + " pColor=" + src.getRed() + "," + src.getGreen() + "," + src.getBlue();
    }

    static class UIResource extends javax.swing.plaf.nimbus.DerivedColor implements javax.swing.plaf.UIResource {
        UIResource(String uiDefaultParentName, float hOffset, float sOffset,
                   float bOffset, int aOffset) {
            super(uiDefaultParentName, hOffset, sOffset, bOffset, aOffset);
        }

        @Override
        public boolean equals(Object o) {
            return (o instanceof javax.swing.plaf.nimbus.DerivedColor.UIResource) && super.equals(o);
        }

        @Override
        public int hashCode() {
            return super.hashCode() + 7;
        }
    }
}
