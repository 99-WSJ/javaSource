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

import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;

/**
 * A <CODE>ColorModel</CODE> class that works with pixel values that
 * represent color and alpha information as separate samples and that
 * store each sample in a separate data element.  This class can be
 * used with an arbitrary <CODE>ColorSpace</CODE>.  The number of
 * color samples in the pixel values must be same as the number of
 * color components in the <CODE>ColorSpace</CODE>. There may be a
 * single alpha sample.
 * <p>
 * For those methods that use
 * a primitive array pixel representation of type <CODE>transferType</CODE>,
 * the array length is the same as the number of color and alpha samples.
 * Color samples are stored first in the array followed by the alpha
 * sample, if present.  The order of the color samples is specified
 * by the <CODE>ColorSpace</CODE>.  Typically, this order reflects the
 * name of the color space type. For example, for <CODE>TYPE_RGB</CODE>,
 * index 0 corresponds to red, index 1 to green, and index 2 to blue.
 * <p>
 * The translation from pixel sample values to color/alpha components for
 * display or processing purposes is based on a one-to-one correspondence of
 * samples to components.
 * Depending on the transfer type used to create an instance of
 * <code>ComponentColorModel</code>, the pixel sample values
 * represented by that instance may be signed or unsigned and may
 * be of integral type or float or double (see below for details).
 * The translation from sample values to normalized color/alpha components
 * must follow certain rules.  For float and double samples, the translation
 * is an identity, i.e. normalized component values are equal to the
 * corresponding sample values.  For integral samples, the translation
 * should be only a simple scale and offset, where the scale and offset
 * constants may be different for each component.  The result of
 * applying the scale and offset constants is a set of color/alpha
 * component values, which are guaranteed to fall within a certain
 * range.  Typically, the range for a color component will be the range
 * defined by the <code>getMinValue</code> and <code>getMaxValue</code>
 * methods of the <code>ColorSpace</code> class.  The range for an
 * alpha component should be 0.0 to 1.0.
 * <p>
 * Instances of <code>ComponentColorModel</code> created with transfer types
 * <CODE>DataBuffer.TYPE_BYTE</CODE>, <CODE>DataBuffer.TYPE_USHORT</CODE>,
 * and <CODE>DataBuffer.TYPE_INT</CODE> have pixel sample values which
 * are treated as unsigned integral values.
 * The number of bits in a color or alpha sample of a pixel value might not
 * be the same as the number of bits for the corresponding color or alpha
 * sample passed to the
 * <code>ComponentColorModel(ColorSpace, int[], boolean, boolean, int, int)</code>
 * constructor.  In
 * that case, this class assumes that the least significant n bits of a sample
 * value hold the component value, where n is the number of significant bits
 * for the component passed to the constructor.  It also assumes that
 * any higher-order bits in a sample value are zero.  Thus, sample values
 * range from 0 to 2<sup>n</sup> - 1.  This class maps these sample values
 * to normalized color component values such that 0 maps to the value
 * obtained from the <code>ColorSpace's</code> <code>getMinValue</code>
 * method for each component and 2<sup>n</sup> - 1 maps to the value
 * obtained from <code>getMaxValue</code>.  To create a
 * <code>ComponentColorModel</code> with a different color sample mapping
 * requires subclassing this class and overriding the
 * <code>getNormalizedComponents(Object, float[], int)</code> method.
 * The mapping for an alpha sample always maps 0 to 0.0 and
 * 2<sup>n</sup> - 1 to 1.0.
 * <p>
 * For instances with unsigned sample values,
 * the unnormalized color/alpha component representation is only
 * supported if two conditions hold.  First, sample value value 0 must
 * map to normalized component value 0.0 and sample value 2<sup>n</sup> - 1
 * to 1.0.  Second the min/max range of all color components of the
 * <code>ColorSpace</code> must be 0.0 to 1.0.  In this case, the
 * component representation is the n least
 * significant bits of the corresponding sample.  Thus each component is
 * an unsigned integral value between 0 and 2<sup>n</sup> - 1, where
 * n is the number of significant bits for a particular component.
 * If these conditions are not met, any method taking an unnormalized
 * component argument will throw an <code>IllegalArgumentException</code>.
 * <p>
 * Instances of <code>ComponentColorModel</code> created with transfer types
 * <CODE>DataBuffer.TYPE_SHORT</CODE>, <CODE>DataBuffer.TYPE_FLOAT</CODE>, and
 * <CODE>DataBuffer.TYPE_DOUBLE</CODE> have pixel sample values which
 * are treated as signed short, float, or double values.
 * Such instances do not support the unnormalized color/alpha component
 * representation, so any methods taking such a representation as an argument
 * will throw an <code>IllegalArgumentException</code> when called on one
 * of these instances.  The normalized component values of instances
 * of this class have a range which depends on the transfer
 * type as follows: for float samples, the full range of the float data
 * type; for double samples, the full range of the float data type
 * (resulting from casting double to float); for short samples,
 * from approximately -maxVal to +maxVal, where maxVal is the per
 * component maximum value for the <code>ColorSpace</code>
 * (-32767 maps to -maxVal, 0 maps to 0.0, and 32767 maps
 * to +maxVal).  A subclass may override the scaling for short sample
 * values to normalized component values by overriding the
 * <code>getNormalizedComponents(Object, float[], int)</code> method.
 * For float and double samples, the normalized component values are
 * taken to be equal to the corresponding sample values, and subclasses
 * should not attempt to add any non-identity scaling for these transfer
 * types.
 * <p>
 * Instances of <code>ComponentColorModel</code> created with transfer types
 * <CODE>DataBuffer.TYPE_SHORT</CODE>, <CODE>DataBuffer.TYPE_FLOAT</CODE>, and
 * <CODE>DataBuffer.TYPE_DOUBLE</CODE>
 * use all the bits of all sample values.  Thus all color/alpha components
 * have 16 bits when using <CODE>DataBuffer.TYPE_SHORT</CODE>, 32 bits when
 * using <CODE>DataBuffer.TYPE_FLOAT</CODE>, and 64 bits when using
 * <CODE>DataBuffer.TYPE_DOUBLE</CODE>.  When the
 * <code>ComponentColorModel(ColorSpace, int[], boolean, boolean, int, int)</code>
 * form of constructor is used with one of these transfer types, the
 * bits array argument is ignored.
 * <p>
 * It is possible to have color/alpha sample values
 * which cannot be reasonably interpreted as component values for rendering.
 * This can happen when <code>ComponentColorModel</code> is subclassed to
 * override the mapping of unsigned sample values to normalized color
 * component values or when signed sample values outside a certain range
 * are used.  (As an example, specifying an alpha component as a signed
 * short value outside the range 0 to 32767, normalized range 0.0 to 1.0, can
 * lead to unexpected results.) It is the
 * responsibility of applications to appropriately scale pixel data before
 * rendering such that color components fall within the normalized range
 * of the <code>ColorSpace</code> (obtained using the <code>getMinValue</code>
 * and <code>getMaxValue</code> methods of the <code>ColorSpace</code> class)
 * and the alpha component is between 0.0 and 1.0.  If color or alpha
 * component values fall outside these ranges, rendering results are
 * indeterminate.
 * <p>
 * Methods that use a single int pixel representation throw
 * an <CODE>IllegalArgumentException</CODE>, unless the number of components
 * for the <CODE>ComponentColorModel</CODE> is one and the component
 * value is unsigned -- in other words,  a single color component using
 * a transfer type of <CODE>DataBuffer.TYPE_BYTE</CODE>,
 * <CODE>DataBuffer.TYPE_USHORT</CODE>, or <CODE>DataBuffer.TYPE_INT</CODE>
 * and no alpha.
 * <p>
 * A <CODE>ComponentColorModel</CODE> can be used in conjunction with a
 * <CODE>ComponentSampleModel</CODE>, a <CODE>BandedSampleModel</CODE>,
 * or a <CODE>PixelInterleavedSampleModel</CODE> to construct a
 * <CODE>BufferedImage</CODE>.
 *
 * @see ColorModel
 * @see ColorSpace
 * @see ComponentSampleModel
 * @see BandedSampleModel
 * @see PixelInterleavedSampleModel
 * @see BufferedImage
 *
 */
public class ComponentColorModel extends ColorModel {

    /**
     * <code>signed</code>  is <code>true</code> for <code>short</code>,
     * <code>float</code>, and <code>double</code> transfer types; it
     * is <code>false</code> for <code>byte</code>, <code>ushort</code>,
     * and <code>int</code> transfer types.
     */
    private boolean signed; // true for transfer types short, float, double
                            // false for byte, ushort, int
    private boolean is_sRGB_stdScale;
    private boolean is_LinearRGB_stdScale;
    private boolean is_LinearGray_stdScale;
    private boolean is_ICCGray_stdScale;
    private byte[] tosRGB8LUT;
    private byte[] fromsRGB8LUT8;
    private short[] fromsRGB8LUT16;
    private byte[] fromLinearGray16ToOtherGray8LUT;
    private short[] fromLinearGray16ToOtherGray16LUT;
    private boolean needScaleInit;
    private boolean noUnnorm;
    private boolean nonStdScale;
    private float[] min;
    private float[] diffMinMax;
    private float[] compOffset;
    private float[] compScale;

    /**
     * Constructs a <CODE>ComponentColorModel</CODE> from the specified
     * parameters. Color components will be in the specified
     * <CODE>ColorSpace</CODE>.  The supported transfer types are
     * <CODE>DataBuffer.TYPE_BYTE</CODE>, <CODE>DataBuffer.TYPE_USHORT</CODE>,
     * <CODE>DataBuffer.TYPE_INT</CODE>,
     * <CODE>DataBuffer.TYPE_SHORT</CODE>, <CODE>DataBuffer.TYPE_FLOAT</CODE>,
     * and <CODE>DataBuffer.TYPE_DOUBLE</CODE>.
     * If not null, the <CODE>bits</CODE> array specifies the
     * number of significant bits per color and alpha component and its
     * length should be at least the number of components in the
     * <CODE>ColorSpace</CODE> if there is no alpha
     * information in the pixel values, or one more than this number if
     * there is alpha information.  When the <CODE>transferType</CODE> is
     * <CODE>DataBuffer.TYPE_SHORT</CODE>, <CODE>DataBuffer.TYPE_FLOAT</CODE>,
     * or <CODE>DataBuffer.TYPE_DOUBLE</CODE> the <CODE>bits</CODE> array
     * argument is ignored.  <CODE>hasAlpha</CODE> indicates whether alpha
     * information is present.  If <CODE>hasAlpha</CODE> is true, then
     * the boolean <CODE>isAlphaPremultiplied</CODE>
     * specifies how to interpret color and alpha samples in pixel values.
     * If the boolean is true, color samples are assumed to have been
     * multiplied by the alpha sample. The <CODE>transparency</CODE>
     * specifies what alpha values can be represented by this color model.
     * The acceptable <code>transparency</code> values are
     * <CODE>OPAQUE</CODE>, <CODE>BITMASK</CODE> or <CODE>TRANSLUCENT</CODE>.
     * The <CODE>transferType</CODE> is the type of primitive array used
     * to represent pixel values.
     *
     * @param colorSpace       The <CODE>ColorSpace</CODE> associated
     *                         with this color model.
     * @param bits             The number of significant bits per component.
     *                         May be null, in which case all bits of all
     *                         component samples will be significant.
     *                         Ignored if transferType is one of
     *                         <CODE>DataBuffer.TYPE_SHORT</CODE>,
     *                         <CODE>DataBuffer.TYPE_FLOAT</CODE>, or
     *                         <CODE>DataBuffer.TYPE_DOUBLE</CODE>,
     *                         in which case all bits of all component
     *                         samples will be significant.
     * @param hasAlpha         If true, this color model supports alpha.
     * @param isAlphaPremultiplied If true, alpha is premultiplied.
     * @param transparency     Specifies what alpha values can be represented
     *                         by this color model.
     * @param transferType     Specifies the type of primitive array used to
     *                         represent pixel values.
     *
     * @throws IllegalArgumentException If the <CODE>bits</CODE> array
     *         argument is not null, its length is less than the number of
     *         color and alpha components, and transferType is one of
     *         <CODE>DataBuffer.TYPE_BYTE</CODE>,
     *         <CODE>DataBuffer.TYPE_USHORT</CODE>, or
     *         <CODE>DataBuffer.TYPE_INT</CODE>.
     * @throws IllegalArgumentException If transferType is not one of
     *         <CODE>DataBuffer.TYPE_BYTE</CODE>,
     *         <CODE>DataBuffer.TYPE_USHORT</CODE>,
     *         <CODE>DataBuffer.TYPE_INT</CODE>,
     *         <CODE>DataBuffer.TYPE_SHORT</CODE>,
     *         <CODE>DataBuffer.TYPE_FLOAT</CODE>, or
     *         <CODE>DataBuffer.TYPE_DOUBLE</CODE>.
     *
     * @see ColorSpace
     * @see java.awt.Transparency
     */
    public ComponentColorModel (ColorSpace colorSpace,
                                int[] bits,
                                boolean hasAlpha,
                                boolean isAlphaPremultiplied,
                                int transparency,
                                int transferType) {
        super (bitsHelper(transferType, colorSpace, hasAlpha),
               bitsArrayHelper(bits, transferType, colorSpace, hasAlpha),
               colorSpace, hasAlpha, isAlphaPremultiplied, transparency,
               transferType);
        switch(transferType) {
            case DataBuffer.TYPE_BYTE:
            case DataBuffer.TYPE_USHORT:
            case DataBuffer.TYPE_INT:
                signed = false;
                needScaleInit = true;
                break;
            case DataBuffer.TYPE_SHORT:
                signed = true;
                needScaleInit = true;
                break;
            case DataBuffer.TYPE_FLOAT:
            case DataBuffer.TYPE_DOUBLE:
                signed = true;
                needScaleInit = false;
                noUnnorm = true;
                nonStdScale = false;
                break;
            default:
                throw new IllegalArgumentException("This constructor is not "+
                         "compatible with transferType " + transferType);
        }
        setupLUTs();
    }

    /**
     * Constructs a <CODE>ComponentColorModel</CODE> from the specified
     * parameters. Color components will be in the specified
     * <CODE>ColorSpace</CODE>.  The supported transfer types are
     * <CODE>DataBuffer.TYPE_BYTE</CODE>, <CODE>DataBuffer.TYPE_USHORT</CODE>,
     * <CODE>DataBuffer.TYPE_INT</CODE>,
     * <CODE>DataBuffer.TYPE_SHORT</CODE>, <CODE>DataBuffer.TYPE_FLOAT</CODE>,
     * and <CODE>DataBuffer.TYPE_DOUBLE</CODE>.  The number of significant
     * bits per color and alpha component will be 8, 16, 32, 16, 32,  or 64,
     * respectively.  The number of color components will be the
     * number of components in the <CODE>ColorSpace</CODE>.  There will be
     * an alpha component if <CODE>hasAlpha</CODE> is <CODE>true</CODE>.
     * If <CODE>hasAlpha</CODE> is true, then
     * the boolean <CODE>isAlphaPremultiplied</CODE>
     * specifies how to interpret color and alpha samples in pixel values.
     * If the boolean is true, color samples are assumed to have been
     * multiplied by the alpha sample. The <CODE>transparency</CODE>
     * specifies what alpha values can be represented by this color model.
     * The acceptable <code>transparency</code> values are
     * <CODE>OPAQUE</CODE>, <CODE>BITMASK</CODE> or <CODE>TRANSLUCENT</CODE>.
     * The <CODE>transferType</CODE> is the type of primitive array used
     * to represent pixel values.
     *
     * @param colorSpace       The <CODE>ColorSpace</CODE> associated
     *                         with this color model.
     * @param hasAlpha         If true, this color model supports alpha.
     * @param isAlphaPremultiplied If true, alpha is premultiplied.
     * @param transparency     Specifies what alpha values can be represented
     *                         by this color model.
     * @param transferType     Specifies the type of primitive array used to
     *                         represent pixel values.
     *
     * @throws IllegalArgumentException If transferType is not one of
     *         <CODE>DataBuffer.TYPE_BYTE</CODE>,
     *         <CODE>DataBuffer.TYPE_USHORT</CODE>,
     *         <CODE>DataBuffer.TYPE_INT</CODE>,
     *         <CODE>DataBuffer.TYPE_SHORT</CODE>,
     *         <CODE>DataBuffer.TYPE_FLOAT</CODE>, or
     *         <CODE>DataBuffer.TYPE_DOUBLE</CODE>.
     *
     * @see ColorSpace
     * @see java.awt.Transparency
     * @since 1.4
     */
    public ComponentColorModel (ColorSpace colorSpace,
                                boolean hasAlpha,
                                boolean isAlphaPremultiplied,
                                int transparency,
                                int transferType) {
        this(colorSpace, null, hasAlpha, isAlphaPremultiplied,
             transparency, transferType);
    }

    private static int bitsHelper(int transferType,
                                  ColorSpace colorSpace,
                                  boolean hasAlpha) {
        int numBits = DataBuffer.getDataTypeSize(transferType);
        int numComponents = colorSpace.getNumComponents();
        if (hasAlpha) {
            ++numComponents;
        }
        return numBits * numComponents;
    }

    private static int[] bitsArrayHelper(int[] origBits,
                                         int transferType,
                                         ColorSpace colorSpace,
                                         boolean hasAlpha) {
        switch(transferType) {
            case DataBuffer.TYPE_BYTE:
            case DataBuffer.TYPE_USHORT:
            case DataBuffer.TYPE_INT:
                if (origBits != null) {
                    return origBits;
                }
                break;
            default:
                break;
        }
        int numBits = DataBuffer.getDataTypeSize(transferType);
        int numComponents = colorSpace.getNumComponents();
        if (hasAlpha) {
            ++numComponents;
        }
        int[] bits = new int[numComponents];
        for (int i = 0; i < numComponents; i++) {
            bits[i] = numBits;
        }
        return bits;
    }

    private void setupLUTs() {
        // REMIND: there is potential to accelerate sRGB, LinearRGB,
        // LinearGray, ICCGray, and non-ICC Gray spaces with non-standard
        // scaling, if that becomes important
        //
        // NOTE: The is_xxx_stdScale and nonStdScale booleans are provisionally
        // set here when this method is called at construction time.  These
        // variables may be set again when initScale is called later.
        // When setupLUTs returns, nonStdScale is true if (the transferType
        // is not float or double) AND (some minimum ColorSpace component
        // value is not 0.0 OR some maximum ColorSpace component value
        // is not 1.0).  This is correct for the calls to
        // getNormalizedComponents(Object, float[], int) from initScale().
        // initScale() may change the value nonStdScale based on the
        // return value of getNormalizedComponents() - this will only
        // happen if getNormalizedComponents() has been overridden by a
        // subclass to make the mapping of min/max pixel sample values
        // something different from min/max color component values.
        if (is_sRGB) {
            is_sRGB_stdScale = true;
            nonStdScale = false;
        } else if (ColorModel.isLinearRGBspace(colorSpace)) {
            // Note that the built-in Linear RGB space has a normalized
            // range of 0.0 - 1.0 for each coordinate.  Usage of these
            // LUTs makes that assumption.
            is_LinearRGB_stdScale = true;
            nonStdScale = false;
            if (transferType == DataBuffer.TYPE_BYTE) {
                tosRGB8LUT = ColorModel.getLinearRGB8TosRGB8LUT();
                fromsRGB8LUT8 = ColorModel.getsRGB8ToLinearRGB8LUT();
            } else {
                tosRGB8LUT = ColorModel.getLinearRGB16TosRGB8LUT();
                fromsRGB8LUT16 = ColorModel.getsRGB8ToLinearRGB16LUT();
            }
        } else if ((colorSpaceType == ColorSpace.TYPE_GRAY) &&
                   (colorSpace instanceof ICC_ColorSpace) &&
                   (colorSpace.getMinValue(0) == 0.0f) &&
                   (colorSpace.getMaxValue(0) == 1.0f)) {
            // Note that a normalized range of 0.0 - 1.0 for the gray
            // component is required, because usage of these LUTs makes
            // that assumption.
            ICC_ColorSpace ics = (ICC_ColorSpace) colorSpace;
            is_ICCGray_stdScale = true;
            nonStdScale = false;
            fromsRGB8LUT16 = ColorModel.getsRGB8ToLinearRGB16LUT();
            if (ColorModel.isLinearGRAYspace(ics)) {
                is_LinearGray_stdScale = true;
                if (transferType == DataBuffer.TYPE_BYTE) {
                    tosRGB8LUT = ColorModel.getGray8TosRGB8LUT(ics);
                } else {
                    tosRGB8LUT = ColorModel.getGray16TosRGB8LUT(ics);
                }
            } else {
                if (transferType == DataBuffer.TYPE_BYTE) {
                    tosRGB8LUT = ColorModel.getGray8TosRGB8LUT(ics);
                    fromLinearGray16ToOtherGray8LUT =
                        ColorModel.getLinearGray16ToOtherGray8LUT(ics);
                } else {
                    tosRGB8LUT = ColorModel.getGray16TosRGB8LUT(ics);
                    fromLinearGray16ToOtherGray16LUT =
                        ColorModel.getLinearGray16ToOtherGray16LUT(ics);
                }
            }
        } else if (needScaleInit) {
            // if transferType is byte, ushort, int, or short and we
            // don't already know the ColorSpace has minVlaue == 0.0f and
            // maxValue == 1.0f for all components, we need to check that
            // now and setup the min[] and diffMinMax[] arrays if necessary.
            nonStdScale = false;
            for (int i = 0; i < numColorComponents; i++) {
                if ((colorSpace.getMinValue(i) != 0.0f) ||
                    (colorSpace.getMaxValue(i) != 1.0f)) {
                    nonStdScale = true;
                    break;
                }
            }
            if (nonStdScale) {
                min = new float[numColorComponents];
                diffMinMax = new float[numColorComponents];
                for (int i = 0; i < numColorComponents; i++) {
                    min[i] = colorSpace.getMinValue(i);
                    diffMinMax[i] = colorSpace.getMaxValue(i) - min[i];
                }
            }
        }
    }

    private void initScale() {
        // This method is called the first time any method which uses
        // pixel sample value to color component value scaling information
        // is called if the transferType supports non-standard scaling
        // as defined above (byte, ushort, int, and short), unless the
        // method is getNormalizedComponents(Object, float[], int) (that
        // method must be overridden to use non-standard scaling).  This
        // method also sets up the noUnnorm boolean variable for these
        // transferTypes.  After this method is called, the nonStdScale
        // variable will be true if getNormalizedComponents() maps a
        // sample value of 0 to anything other than 0.0f OR maps a
        // sample value of 2^^n - 1 (2^^15 - 1 for short transferType)
        // to anything other than 1.0f.  Note that this can be independent
        // of the colorSpace min/max component values, if the
        // getNormalizedComponents() method has been overridden for some
        // reason, e.g. to provide greater dynamic range in the sample
        // values than in the color component values.  Unfortunately,
        // this method can't be called at construction time, since a
        // subclass may still have uninitialized state that would cause
        // getNormalizedComponents() to return an incorrect result.
        needScaleInit = false; // only needs to called once
        if (nonStdScale || signed) {
            // The unnormalized form is only supported for unsigned
            // transferTypes and when the ColorSpace min/max values
            // are 0.0/1.0.  When this method is called nonStdScale is
            // true if the latter condition does not hold.  In addition,
            // the unnormalized form requires that the full range of
            // the pixel sample values map to the full 0.0 - 1.0 range
            // of color component values.  That condition is checked
            // later in this method.
            noUnnorm = true;
        } else {
            noUnnorm = false;
        }
        float[] lowVal, highVal;
        switch (transferType) {
        case DataBuffer.TYPE_BYTE:
            {
                byte[] bpixel = new byte[numComponents];
                for (int i = 0; i < numColorComponents; i++) {
                    bpixel[i] = 0;
                }
                if (supportsAlpha) {
                    bpixel[numColorComponents] =
                        (byte) ((1 << nBits[numColorComponents]) - 1);
                }
                lowVal = getNormalizedComponents(bpixel, null, 0);
                for (int i = 0; i < numColorComponents; i++) {
                    bpixel[i] = (byte) ((1 << nBits[i]) - 1);
                }
                highVal = getNormalizedComponents(bpixel, null, 0);
            }
            break;
        case DataBuffer.TYPE_USHORT:
            {
                short[] uspixel = new short[numComponents];
                for (int i = 0; i < numColorComponents; i++) {
                    uspixel[i] = 0;
                }
                if (supportsAlpha) {
                    uspixel[numColorComponents] =
                        (short) ((1 << nBits[numColorComponents]) - 1);
                }
                lowVal = getNormalizedComponents(uspixel, null, 0);
                for (int i = 0; i < numColorComponents; i++) {
                    uspixel[i] = (short) ((1 << nBits[i]) - 1);
                }
                highVal = getNormalizedComponents(uspixel, null, 0);
            }
            break;
        case DataBuffer.TYPE_INT:
            {
                int[] ipixel = new int[numComponents];
                for (int i = 0; i < numColorComponents; i++) {
                    ipixel[i] = 0;
                }
                if (supportsAlpha) {
                    ipixel[numColorComponents] =
                        ((1 << nBits[numColorComponents]) - 1);
                }
                lowVal = getNormalizedComponents(ipixel, null, 0);
                for (int i = 0; i < numColorComponents; i++) {
                    ipixel[i] = ((1 << nBits[i]) - 1);
                }
                highVal = getNormalizedComponents(ipixel, null, 0);
            }
            break;
        case DataBuffer.TYPE_SHORT:
            {
                short[] spixel = new short[numComponents];
                for (int i = 0; i < numColorComponents; i++) {
                    spixel[i] = 0;
                }
                if (supportsAlpha) {
                    spixel[numColorComponents] = 32767;
                }
                lowVal = getNormalizedComponents(spixel, null, 0);
                for (int i = 0; i < numColorComponents; i++) {
                    spixel[i] = 32767;
                }
                highVal = getNormalizedComponents(spixel, null, 0);
            }
            break;
        default:
            lowVal = highVal = null;  // to keep the compiler from complaining
            break;
        }
        nonStdScale = false;
        for (int i = 0; i < numColorComponents; i++) {
            if ((lowVal[i] != 0.0f) || (highVal[i] != 1.0f)) {
                nonStdScale = true;
                break;
            }
        }
        if (nonStdScale) {
            noUnnorm = true;
            is_sRGB_stdScale = false;
            is_LinearRGB_stdScale = false;
            is_LinearGray_stdScale = false;
            is_ICCGray_stdScale = false;
            compOffset = new float[numColorComponents];
            compScale = new float[numColorComponents];
            for (int i = 0; i < numColorComponents; i++) {
                compOffset[i] = lowVal[i];
                compScale[i] = 1.0f / (highVal[i] - lowVal[i]);
            }
        }
    }

    private int getRGBComponent(int pixel, int idx) {
        if (numComponents > 1) {
            throw new
                IllegalArgumentException("More than one component per pixel");
        }
        if (signed) {
            throw new
                IllegalArgumentException("Component value is signed");
        }
        if (needScaleInit) {
            initScale();
        }
        // Since there is only 1 component, there is no alpha

        // Normalize the pixel in order to convert it
        Object opixel = null;
        switch (transferType) {
        case DataBuffer.TYPE_BYTE:
            {
                byte[] bpixel = { (byte) pixel };
                opixel = bpixel;
            }
            break;
        case DataBuffer.TYPE_USHORT:
            {
                short[] spixel = { (short) pixel };
                opixel = spixel;
            }
            break;
        case DataBuffer.TYPE_INT:
            {
                int[] ipixel = { pixel };
                opixel = ipixel;
            }
            break;
        }
        float[] norm = getNormalizedComponents(opixel, null, 0);
        float[] rgb = colorSpace.toRGB(norm);

        return (int) (rgb[idx] * 255.0f + 0.5f);
    }

    /**
     * Returns the red color component for the specified pixel, scaled
     * from 0 to 255 in the default RGB ColorSpace, sRGB.  A color conversion
     * is done if necessary.  The pixel value is specified as an int.
     * The returned value will be a non pre-multiplied value.
     * If the alpha is premultiplied, this method divides
     * it out before returning the value (if the alpha value is 0,
     * the red value will be 0).
     *
     * @param pixel The pixel from which you want to get the red color component.
     *
     * @return The red color component for the specified pixel, as an int.
     *
     * @throws IllegalArgumentException If there is more than
     * one component in this <CODE>ColorModel</CODE>.
     * @throws IllegalArgumentException If the component value for this
     * <CODE>ColorModel</CODE> is signed
     */
    public int getRed(int pixel) {
        return getRGBComponent(pixel, 0);
    }

    /**
     * Returns the green color component for the specified pixel, scaled
     * from 0 to 255 in the default RGB ColorSpace, sRGB.  A color conversion
     * is done if necessary.  The pixel value is specified as an int.
     * The returned value will be a non
     * pre-multiplied value. If the alpha is premultiplied, this method
     * divides it out before returning the value (if the alpha value is 0,
     * the green value will be 0).
     *
     * @param pixel The pixel from which you want to get the green color component.
     *
     * @return The green color component for the specified pixel, as an int.
     *
     * @throws IllegalArgumentException If there is more than
     * one component in this <CODE>ColorModel</CODE>.
     * @throws IllegalArgumentException If the component value for this
     * <CODE>ColorModel</CODE> is signed
     */
    public int getGreen(int pixel) {
        return getRGBComponent(pixel, 1);
    }

    /**
     * Returns the blue color component for the specified pixel, scaled
     * from 0 to 255 in the default RGB ColorSpace, sRGB.  A color conversion
     * is done if necessary.  The pixel value is specified as an int.
     * The returned value will be a non
     * pre-multiplied value. If the alpha is premultiplied, this method
     * divides it out before returning the value (if the alpha value is 0,
     * the blue value will be 0).
     *
     * @param pixel The pixel from which you want to get the blue color component.
     *
     * @return The blue color component for the specified pixel, as an int.
     *
     * @throws IllegalArgumentException If there is more than
     * one component in this <CODE>ColorModel</CODE>.
     * @throws IllegalArgumentException If the component value for this
     * <CODE>ColorModel</CODE> is signed
     */
    public int getBlue(int pixel) {
        return getRGBComponent(pixel, 2);
    }

    /**
     * Returns the alpha component for the specified pixel, scaled
     * from 0 to 255.   The pixel value is specified as an int.
     *
     * @param pixel The pixel from which you want to get the alpha component.
     *
     * @return The alpha component for the specified pixel, as an int.
     *
     * @throws IllegalArgumentException If there is more than
     * one component in this <CODE>ColorModel</CODE>.
     * @throws IllegalArgumentException If the component value for this
     * <CODE>ColorModel</CODE> is signed
     */
    public int getAlpha(int pixel) {
        if (supportsAlpha == false) {
            return 255;
        }
        if (numComponents > 1) {
            throw new
                IllegalArgumentException("More than one component per pixel");
        }
        if (signed) {
            throw new
                IllegalArgumentException("Component value is signed");
        }

        return (int) ((((float) pixel) / ((1<<nBits[0])-1)) * 255.0f + 0.5f);
    }

    /**
     * Returns the color/alpha components of the pixel in the default
     * RGB color model format.  A color conversion is done if necessary.
     * The returned value will be in a non pre-multiplied format. If
     * the alpha is premultiplied, this method divides it out of the
     * color components (if the alpha value is 0, the color values will be 0).
     *
     * @param pixel The pixel from which you want to get the color/alpha components.
     *
     * @return The color/alpha components for the specified pixel, as an int.
     *
     * @throws IllegalArgumentException If there is more than
     * one component in this <CODE>ColorModel</CODE>.
     * @throws IllegalArgumentException If the component value for this
     * <CODE>ColorModel</CODE> is signed
     */
    public int getRGB(int pixel) {
        if (numComponents > 1) {
            throw new
                IllegalArgumentException("More than one component per pixel");
        }
        if (signed) {
            throw new
                IllegalArgumentException("Component value is signed");
        }

        return (getAlpha(pixel) << 24)
            | (getRed(pixel) << 16)
            | (getGreen(pixel) << 8)
            | (getBlue(pixel) << 0);
    }

    private int extractComponent(Object inData, int idx, int precision) {
        // Extract component idx from inData.  The precision argument
        // should be either 8 or 16.  If it's 8, this method will return
        // an 8-bit value.  If it's 16, this method will return a 16-bit
        // value for transferTypes other than TYPE_BYTE.  For TYPE_BYTE,
        // an 8-bit value will be returned.

        // This method maps the input value corresponding to a
        // normalized ColorSpace component value of 0.0 to 0, and the
        // input value corresponding to a normalized ColorSpace
        // component value of 1.0 to 2^n - 1 (where n is 8 or 16), so
        // it is appropriate only for ColorSpaces with min/max component
        // values of 0.0/1.0.  This will be true for sRGB, the built-in
        // Linear RGB and Linear Gray spaces, and any other ICC grayscale
        // spaces for which we have precomputed LUTs.

        boolean needAlpha = (supportsAlpha && isAlphaPremultiplied);
        int alp = 0;
        int comp;
        int mask = (1 << nBits[idx]) - 1;

        switch (transferType) {
            // Note: we do no clamping of the pixel data here - we
            // assume that the data is scaled properly
            case DataBuffer.TYPE_SHORT: {
                short sdata[] = (short[]) inData;
                float scalefactor = (float) ((1 << precision) - 1);
                if (needAlpha) {
                    short s = sdata[numColorComponents];
                    if (s != (short) 0) {
                        return (int) ((((float) sdata[idx]) /
                                       ((float) s)) * scalefactor + 0.5f);
                    } else {
                        return 0;
                    }
                } else {
                    return (int) ((sdata[idx] / 32767.0f) * scalefactor + 0.5f);
                }
            }
            case DataBuffer.TYPE_FLOAT: {
                float fdata[] = (float[]) inData;
                float scalefactor = (float) ((1 << precision) - 1);
                if (needAlpha) {
                    float f = fdata[numColorComponents];
                    if (f != 0.0f) {
                        return (int) (((fdata[idx] / f) * scalefactor) + 0.5f);
                    } else {
                        return 0;
                    }
                } else {
                    return (int) (fdata[idx] * scalefactor + 0.5f);
                }
            }
            case DataBuffer.TYPE_DOUBLE: {
                double ddata[] = (double[]) inData;
                double scalefactor = (double) ((1 << precision) - 1);
                if (needAlpha) {
                    double d = ddata[numColorComponents];
                    if (d != 0.0) {
                        return (int) (((ddata[idx] / d) * scalefactor) + 0.5);
                    } else {
                        return 0;
                    }
                } else {
                    return (int) (ddata[idx] * scalefactor + 0.5);
                }
            }
            case DataBuffer.TYPE_BYTE:
               byte bdata[] = (byte[])inData;
               comp = bdata[idx] & mask;
               precision = 8;
               if (needAlpha) {
                   alp = bdata[numColorComponents] & mask;
               }
            break;
            case DataBuffer.TYPE_USHORT:
               short usdata[] = (short[])inData;
               comp = usdata[idx] & mask;
               if (needAlpha) {
                   alp = usdata[numColorComponents] & mask;
               }
            break;
            case DataBuffer.TYPE_INT:
               int idata[] = (int[])inData;
               comp = idata[idx];
               if (needAlpha) {
                   alp = idata[numColorComponents];
               }
            break;
            default:
               throw new
                   UnsupportedOperationException("This method has not "+
                   "been implemented for transferType " + transferType);
        }
        if (needAlpha) {
            if (alp != 0) {
                float scalefactor = (float) ((1 << precision) - 1);
                float fcomp = ((float) comp) / ((float)mask);
                float invalp = ((float) ((1<<nBits[numColorComponents]) - 1)) /
                               ((float) alp);
                return (int) (fcomp * invalp * scalefactor + 0.5f);
            } else {
                return 0;
            }
        } else {
            if (nBits[idx] != precision) {
                float scalefactor = (float) ((1 << precision) - 1);
                float fcomp = ((float) comp) / ((float)mask);
                return (int) (fcomp * scalefactor + 0.5f);
            }
            return comp;
        }
    }

    private int getRGBComponent(Object inData, int idx) {
        if (needScaleInit) {
            initScale();
        }
        if (is_sRGB_stdScale) {
            return extractComponent(inData, idx, 8);
        } else if (is_LinearRGB_stdScale) {
            int lutidx = extractComponent(inData, idx, 16);
            return tosRGB8LUT[lutidx] & 0xff;
        } else if (is_ICCGray_stdScale) {
            int lutidx = extractComponent(inData, 0, 16);
            return tosRGB8LUT[lutidx] & 0xff;
        }

        // Not CS_sRGB, CS_LINEAR_RGB, or any TYPE_GRAY ICC_ColorSpace
        float[] norm = getNormalizedComponents(inData, null, 0);
        // Note that getNormalizedComponents returns non-premultiplied values
        float[] rgb = colorSpace.toRGB(norm);
        return (int) (rgb[idx] * 255.0f + 0.5f);
    }

    /**
     * Returns the red color component for the specified pixel, scaled
     * from 0 to 255 in the default RGB ColorSpace, sRGB.  A color conversion
     * is done if necessary.  The <CODE>pixel</CODE> value is specified by an array
     * of data elements of type <CODE>transferType</CODE> passed in as an object
     * reference. The returned value will be a non pre-multiplied value. If the
     * alpha is premultiplied, this method divides it out before returning
     * the value (if the alpha value is 0, the red value will be 0). Since
     * <code>ComponentColorModel</code> can be subclassed, subclasses
     * inherit the implementation of this method and if they don't override
     * it then they throw an exception if they use an unsupported
     * <code>transferType</code>.
     *
     * @param inData The pixel from which you want to get the red color component,
     * specified by an array of data elements of type <CODE>transferType</CODE>.
     *
     * @return The red color component for the specified pixel, as an int.
     *
     * @throws ClassCastException If <CODE>inData</CODE> is not a primitive array
     * of type <CODE>transferType</CODE>.
     * @throws ArrayIndexOutOfBoundsException if <CODE>inData</CODE> is not
     * large enough to hold a pixel value for this
     * <CODE>ColorModel</CODE>.
     * @throws UnsupportedOperationException If the transfer type of
     * this <CODE>ComponentColorModel</CODE>
     * is not one of the supported transfer types:
     * <CODE>DataBuffer.TYPE_BYTE</CODE>, <CODE>DataBuffer.TYPE_USHORT</CODE>,
     * <CODE>DataBuffer.TYPE_INT</CODE>, <CODE>DataBuffer.TYPE_SHORT</CODE>,
     * <CODE>DataBuffer.TYPE_FLOAT</CODE>, or <CODE>DataBuffer.TYPE_DOUBLE</CODE>.
     */
    public int getRed(Object inData) {
        return getRGBComponent(inData, 0);
    }


    /**
     * Returns the green color component for the specified pixel, scaled
     * from 0 to 255 in the default RGB <CODE>ColorSpace</CODE>, sRGB.
     * A color conversion is done if necessary.  The <CODE>pixel</CODE> value
     * is specified by an array of data elements of type <CODE>transferType</CODE>
     * passed in as an object reference. The returned value is a non pre-multiplied
     * value. If the alpha is premultiplied, this method divides it out before
     * returning the value (if the alpha value is 0, the green value will be 0).
     * Since <code>ComponentColorModel</code> can be subclassed,
     * subclasses inherit the implementation of this method and if they
     * don't override it then they throw an exception if they use an
     * unsupported <code>transferType</code>.
     *
     * @param inData The pixel from which you want to get the green color component,
     * specified by an array of data elements of type <CODE>transferType</CODE>.
     *
     * @return The green color component for the specified pixel, as an int.
     *
     * @throws ClassCastException If <CODE>inData</CODE> is not a primitive array
     * of type <CODE>transferType</CODE>.
     * @throws ArrayIndexOutOfBoundsException if <CODE>inData</CODE> is not
     * large enough to hold a pixel value for this
     * <CODE>ColorModel</CODE>.
     * @throws UnsupportedOperationException If the transfer type of
     * this <CODE>ComponentColorModel</CODE>
     * is not one of the supported transfer types:
     * <CODE>DataBuffer.TYPE_BYTE</CODE>, <CODE>DataBuffer.TYPE_USHORT</CODE>,
     * <CODE>DataBuffer.TYPE_INT</CODE>, <CODE>DataBuffer.TYPE_SHORT</CODE>,
     * <CODE>DataBuffer.TYPE_FLOAT</CODE>, or <CODE>DataBuffer.TYPE_DOUBLE</CODE>.
     */
    public int getGreen(Object inData) {
        return getRGBComponent(inData, 1);
    }


    /**
     * Returns the blue color component for the specified pixel, scaled
     * from 0 to 255 in the default RGB <CODE>ColorSpace</CODE>, sRGB.
     * A color conversion is done if necessary.  The <CODE>pixel</CODE> value is
     * specified by an array of data elements of type <CODE>transferType</CODE>
     * passed in as an object reference. The returned value is a non pre-multiplied
     * value. If the alpha is premultiplied, this method divides it out before
     * returning the value (if the alpha value is 0, the blue value will be 0).
     * Since <code>ComponentColorModel</code> can be subclassed,
     * subclasses inherit the implementation of this method and if they
     * don't override it then they throw an exception if they use an
     * unsupported <code>transferType</code>.
     *
     * @param inData The pixel from which you want to get the blue color component,
     * specified by an array of data elements of type <CODE>transferType</CODE>.
     *
     * @return The blue color component for the specified pixel, as an int.
     *
     * @throws ClassCastException If <CODE>inData</CODE> is not a primitive array
     * of type <CODE>transferType</CODE>.
     * @throws ArrayIndexOutOfBoundsException if <CODE>inData</CODE> is not
     * large enough to hold a pixel value for this
     * <CODE>ColorModel</CODE>.
     * @throws UnsupportedOperationException If the transfer type of
     * this <CODE>ComponentColorModel</CODE>
     * is not one of the supported transfer types:
     * <CODE>DataBuffer.TYPE_BYTE</CODE>, <CODE>DataBuffer.TYPE_USHORT</CODE>,
     * <CODE>DataBuffer.TYPE_INT</CODE>, <CODE>DataBuffer.TYPE_SHORT</CODE>,
     * <CODE>DataBuffer.TYPE_FLOAT</CODE>, or <CODE>DataBuffer.TYPE_DOUBLE</CODE>.
     */
    public int getBlue(Object inData) {
        return getRGBComponent(inData, 2);
    }

    /**
     * Returns the alpha component for the specified pixel, scaled from
     * 0 to 255.  The pixel value is specified by an array of data
     * elements of type <CODE>transferType</CODE> passed in as an
     * object reference.  Since <code>ComponentColorModel</code> can be
     * subclassed, subclasses inherit the
     * implementation of this method and if they don't override it then
     * they throw an exception if they use an unsupported
     * <code>transferType</code>.
     *
     * @param inData The pixel from which you want to get the alpha component,
     * specified by an array of data elements of type <CODE>transferType</CODE>.
     *
     * @return The alpha component for the specified pixel, as an int.
     *
     * @throws ClassCastException If <CODE>inData</CODE> is not a primitive array
     * of type <CODE>transferType</CODE>.
     * @throws ArrayIndexOutOfBoundsException if <CODE>inData</CODE> is not
     * large enough to hold a pixel value for this
     * <CODE>ColorModel</CODE>.
     * @throws UnsupportedOperationException If the transfer type of
     * this <CODE>ComponentColorModel</CODE>
     * is not one of the supported transfer types:
     * <CODE>DataBuffer.TYPE_BYTE</CODE>, <CODE>DataBuffer.TYPE_USHORT</CODE>,
     * <CODE>DataBuffer.TYPE_INT</CODE>, <CODE>DataBuffer.TYPE_SHORT</CODE>,
     * <CODE>DataBuffer.TYPE_FLOAT</CODE>, or <CODE>DataBuffer.TYPE_DOUBLE</CODE>.
     */
    public int getAlpha(Object inData) {
        if (supportsAlpha == false) {
            return 255;
        }

        int alpha = 0;
        int aIdx = numColorComponents;
        int mask = (1 << nBits[aIdx]) - 1;

        switch (transferType) {
            case DataBuffer.TYPE_SHORT:
                short sdata[] = (short[])inData;
                alpha = (int) ((sdata[aIdx] / 32767.0f) * 255.0f + 0.5f);
                return alpha;
            case DataBuffer.TYPE_FLOAT:
                float fdata[] = (float[])inData;
                alpha = (int) (fdata[aIdx] * 255.0f + 0.5f);
                return alpha;
            case DataBuffer.TYPE_DOUBLE:
                double ddata[] = (double[])inData;
                alpha = (int) (ddata[aIdx] * 255.0 + 0.5);
                return alpha;
            case DataBuffer.TYPE_BYTE:
               byte bdata[] = (byte[])inData;
               alpha = bdata[aIdx] & mask;
            break;
            case DataBuffer.TYPE_USHORT:
               short usdata[] = (short[])inData;
               alpha = usdata[aIdx] & mask;
            break;
            case DataBuffer.TYPE_INT:
               int idata[] = (int[])inData;
               alpha = idata[aIdx];
            break;
            default:
               throw new
                   UnsupportedOperationException("This method has not "+
                   "been implemented for transferType " + transferType);
        }

        if (nBits[aIdx] == 8) {
            return alpha;
        } else {
            return (int)
                ((((float) alpha) / ((float) ((1 << nBits[aIdx]) - 1))) *
                 255.0f + 0.5f);
        }
    }

    /**
     * Returns the color/alpha components for the specified pixel in the
     * default RGB color model format.  A color conversion is done if
     * necessary.  The pixel value is specified by an
     * array of data elements of type <CODE>transferType</CODE> passed
     * in as an object reference.
     * The returned value is in a non pre-multiplied format. If
     * the alpha is premultiplied, this method divides it out of the
     * color components (if the alpha value is 0, the color values will be 0).
     * Since <code>ComponentColorModel</code> can be subclassed,
     * subclasses inherit the implementation of this method and if they
     * don't override it then they throw an exception if they use an
     * unsupported <code>transferType</code>.
     *
     * @param inData The pixel from which you want to get the color/alpha components,
     * specified by an array of data elements of type <CODE>transferType</CODE>.
     *
     * @return The color/alpha components for the specified pixel, as an int.
     *
     * @throws ClassCastException If <CODE>inData</CODE> is not a primitive array
     * of type <CODE>transferType</CODE>.
     * @throws ArrayIndexOutOfBoundsException if <CODE>inData</CODE> is not
     * large enough to hold a pixel value for this
     * <CODE>ColorModel</CODE>.
     * @throws UnsupportedOperationException If the transfer type of
     * this <CODE>ComponentColorModel</CODE>
     * is not one of the supported transfer types:
     * <CODE>DataBuffer.TYPE_BYTE</CODE>, <CODE>DataBuffer.TYPE_USHORT</CODE>,
     * <CODE>DataBuffer.TYPE_INT</CODE>, <CODE>DataBuffer.TYPE_SHORT</CODE>,
     * <CODE>DataBuffer.TYPE_FLOAT</CODE>, or <CODE>DataBuffer.TYPE_DOUBLE</CODE>.
     * @see ColorModel#getRGBdefault
     */
    public int getRGB(Object inData) {
        if (needScaleInit) {
            initScale();
        }
        if (is_sRGB_stdScale || is_LinearRGB_stdScale) {
            return (getAlpha(inData) << 24)
                | (getRed(inData) << 16)
                | (getGreen(inData) << 8)
                | (getBlue(inData));
        } else if (colorSpaceType == ColorSpace.TYPE_GRAY) {
            int gray = getRed(inData); // Red sRGB component should equal
                                       // green and blue components
            return (getAlpha(inData) << 24)
                | (gray << 16)
                | (gray <<  8)
                | gray;
        }
        float[] norm = getNormalizedComponents(inData, null, 0);
        // Note that getNormalizedComponents returns non-premult values
        float[] rgb = colorSpace.toRGB(norm);
        return (getAlpha(inData) << 24)
            | (((int) (rgb[0] * 255.0f + 0.5f)) << 16)
            | (((int) (rgb[1] * 255.0f + 0.5f)) << 8)
            | (((int) (rgb[2] * 255.0f + 0.5f)) << 0);
    }

    /**
     * Returns a data element array representation of a pixel in this
     * <CODE>ColorModel</CODE>, given an integer pixel representation
     * in the default RGB color model.
     * This array can then be passed to the <CODE>setDataElements</CODE>
     * method of a <CODE>WritableRaster</CODE> object.  If the
     * <CODE>pixel</CODE>
     * parameter is null, a new array is allocated.  Since
     * <code>ComponentColorModel</code> can be subclassed, subclasses
     * inherit the implementation of this method and if they don't
     * override it then
     * they throw an exception if they use an unsupported
     * <code>transferType</code>.
     *
     * @param rgb the integer representation of the pixel in the RGB
     *            color model
     * @param pixel the specified pixel
     * @return The data element array representation of a pixel
     * in this <CODE>ColorModel</CODE>.
     * @throws ClassCastException If <CODE>pixel</CODE> is not null and
     * is not a primitive array of type <CODE>transferType</CODE>.
     * @throws ArrayIndexOutOfBoundsException If <CODE>pixel</CODE> is
     * not large enough to hold a pixel value for this
     * <CODE>ColorModel</CODE>.
     * @throws UnsupportedOperationException If the transfer type of
     * this <CODE>ComponentColorModel</CODE>
     * is not one of the supported transfer types:
     * <CODE>DataBuffer.TYPE_BYTE</CODE>, <CODE>DataBuffer.TYPE_USHORT</CODE>,
     * <CODE>DataBuffer.TYPE_INT</CODE>, <CODE>DataBuffer.TYPE_SHORT</CODE>,
     * <CODE>DataBuffer.TYPE_FLOAT</CODE>, or <CODE>DataBuffer.TYPE_DOUBLE</CODE>.
     *
     * @see WritableRaster#setDataElements
     * @see SampleModel#setDataElements
     */
    public Object getDataElements(int rgb, Object pixel) {
        // REMIND: Use rendering hints?

        int red, grn, blu, alp;
        red = (rgb>>16) & 0xff;
        grn = (rgb>>8) & 0xff;
        blu = rgb & 0xff;

        if (needScaleInit) {
            initScale();
        }
        if (signed) {
            // Handle SHORT, FLOAT, & DOUBLE here

            switch(transferType) {
            case DataBuffer.TYPE_SHORT:
                {
                    short sdata[];
                    if (pixel == null) {
                        sdata = new short[numComponents];
                    } else {
                        sdata = (short[])pixel;
                    }
                    float factor;
                    if (is_sRGB_stdScale || is_LinearRGB_stdScale) {
                        factor = 32767.0f / 255.0f;
                        if (is_LinearRGB_stdScale) {
                            red = fromsRGB8LUT16[red] & 0xffff;
                            grn = fromsRGB8LUT16[grn] & 0xffff;
                            blu = fromsRGB8LUT16[blu] & 0xffff;
                            factor = 32767.0f / 65535.0f;
                        }
                        if (supportsAlpha) {
                            alp = (rgb>>24) & 0xff;
                            sdata[3] =
                                (short) (alp * (32767.0f / 255.0f) + 0.5f);
                            if (isAlphaPremultiplied) {
                                factor = alp * factor * (1.0f / 255.0f);
                            }
                        }
                        sdata[0] = (short) (red * factor + 0.5f);
                        sdata[1] = (short) (grn * factor + 0.5f);
                        sdata[2] = (short) (blu * factor + 0.5f);
                    } else if (is_LinearGray_stdScale) {
                        red = fromsRGB8LUT16[red] & 0xffff;
                        grn = fromsRGB8LUT16[grn] & 0xffff;
                        blu = fromsRGB8LUT16[blu] & 0xffff;
                        float gray = ((0.2125f * red) +
                                      (0.7154f * grn) +
                                      (0.0721f * blu)) / 65535.0f;
                        factor = 32767.0f;
                        if (supportsAlpha) {
                            alp = (rgb>>24) & 0xff;
                            sdata[1] =
                                (short) (alp * (32767.0f / 255.0f) + 0.5f);
                            if (isAlphaPremultiplied) {
                                factor = alp * factor * (1.0f / 255.0f);
                            }
                        }
                        sdata[0] = (short) (gray * factor + 0.5f);
                    } else if (is_ICCGray_stdScale) {
                        red = fromsRGB8LUT16[red] & 0xffff;
                        grn = fromsRGB8LUT16[grn] & 0xffff;
                        blu = fromsRGB8LUT16[blu] & 0xffff;
                        int gray = (int) ((0.2125f * red) +
                                          (0.7154f * grn) +
                                          (0.0721f * blu) + 0.5f);
                        gray = fromLinearGray16ToOtherGray16LUT[gray] & 0xffff;
                        factor = 32767.0f / 65535.0f;
                        if (supportsAlpha) {
                            alp = (rgb>>24) & 0xff;
                            sdata[1] =
                                (short) (alp * (32767.0f / 255.0f) + 0.5f);
                            if (isAlphaPremultiplied) {
                                factor = alp * factor * (1.0f / 255.0f);
                            }
                        }
                        sdata[0] = (short) (gray * factor + 0.5f);
                    } else {
                        factor = 1.0f / 255.0f;
                        float norm[] = new float[3];
                        norm[0] = red * factor;
                        norm[1] = grn * factor;
                        norm[2] = blu * factor;
                        norm = colorSpace.fromRGB(norm);
                        if (nonStdScale) {
                            for (int i = 0; i < numColorComponents; i++) {
                                norm[i] = (norm[i] - compOffset[i]) *
                                          compScale[i];
                                // REMIND: need to analyze whether this
                                // clamping is necessary
                                if (norm[i] < 0.0f) {
                                    norm[i] = 0.0f;
                                }
                                if (norm[i] > 1.0f) {
                                    norm[i] = 1.0f;
                                }
                            }
                        }
                        factor = 32767.0f;
                        if (supportsAlpha) {
                            alp = (rgb>>24) & 0xff;
                            sdata[numColorComponents] =
                                (short) (alp * (32767.0f / 255.0f) + 0.5f);
                            if (isAlphaPremultiplied) {
                                factor *= alp * (1.0f / 255.0f);
                            }
                        }
                        for (int i = 0; i < numColorComponents; i++) {
                            sdata[i] = (short) (norm[i] * factor + 0.5f);
                        }
                    }
                    return sdata;
                }
            case DataBuffer.TYPE_FLOAT:
                {
                    float fdata[];
                    if (pixel == null) {
                        fdata = new float[numComponents];
                    } else {
                        fdata = (float[])pixel;
                    }
                    float factor;
                    if (is_sRGB_stdScale || is_LinearRGB_stdScale) {
                        if (is_LinearRGB_stdScale) {
                            red = fromsRGB8LUT16[red] & 0xffff;
                            grn = fromsRGB8LUT16[grn] & 0xffff;
                            blu = fromsRGB8LUT16[blu] & 0xffff;
                            factor = 1.0f / 65535.0f;
                        } else {
                            factor = 1.0f / 255.0f;
                        }
                        if (supportsAlpha) {
                            alp = (rgb>>24) & 0xff;
                            fdata[3] = alp * (1.0f / 255.0f);
                            if (isAlphaPremultiplied) {
                                factor *= fdata[3];
                            }
                        }
                        fdata[0] = red * factor;
                        fdata[1] = grn * factor;
                        fdata[2] = blu * factor;
                    } else if (is_LinearGray_stdScale) {
                        red = fromsRGB8LUT16[red] & 0xffff;
                        grn = fromsRGB8LUT16[grn] & 0xffff;
                        blu = fromsRGB8LUT16[blu] & 0xffff;
                        fdata[0] = ((0.2125f * red) +
                                    (0.7154f * grn) +
                                    (0.0721f * blu)) / 65535.0f;
                        if (supportsAlpha) {
                            alp = (rgb>>24) & 0xff;
                            fdata[1] = alp * (1.0f / 255.0f);
                            if (isAlphaPremultiplied) {
                                fdata[0] *= fdata[1];
                            }
                        }
                    } else if (is_ICCGray_stdScale) {
                        red = fromsRGB8LUT16[red] & 0xffff;
                        grn = fromsRGB8LUT16[grn] & 0xffff;
                        blu = fromsRGB8LUT16[blu] & 0xffff;
                        int gray = (int) ((0.2125f * red) +
                                          (0.7154f * grn) +
                                          (0.0721f * blu) + 0.5f);
                        fdata[0] = (fromLinearGray16ToOtherGray16LUT[gray] &
                                    0xffff) / 65535.0f;
                        if (supportsAlpha) {
                            alp = (rgb>>24) & 0xff;
                            fdata[1] = alp * (1.0f / 255.0f);
                            if (isAlphaPremultiplied) {
                                fdata[0] *= fdata[1];
                            }
                        }
                    } else {
                        float norm[] = new float[3];
                        factor = 1.0f / 255.0f;
                        norm[0] = red * factor;
                        norm[1] = grn * factor;
                        norm[2] = blu * factor;
                        norm = colorSpace.fromRGB(norm);
                        if (supportsAlpha) {
                            alp = (rgb>>24) & 0xff;
                            fdata[numColorComponents] = alp * factor;
                            if (isAlphaPremultiplied) {
                                factor *= alp;
                                for (int i = 0; i < numColorComponents; i++) {
                                    norm[i] *= factor;
                                }
                            }
                        }
                        for (int i = 0; i < numColorComponents; i++) {
                            fdata[i] = norm[i];
                        }
                    }
                    return fdata;
                }
            case DataBuffer.TYPE_DOUBLE:
                {
                    double ddata[];
                    if (pixel == null) {
                        ddata = new double[numComponents];
                    } else {
                        ddata = (double[])pixel;
                    }
                    if (is_sRGB_stdScale || is_LinearRGB_stdScale) {
                        double factor;
                        if (is_LinearRGB_stdScale) {
                            red = fromsRGB8LUT16[red] & 0xffff;
                            grn = fromsRGB8LUT16[grn] & 0xffff;
                            blu = fromsRGB8LUT16[blu] & 0xffff;
                            factor = 1.0 / 65535.0;
                        } else {
                            factor = 1.0 / 255.0;
                        }
                        if (supportsAlpha) {
                            alp = (rgb>>24) & 0xff;
                            ddata[3] = alp * (1.0 / 255.0);
                            if (isAlphaPremultiplied) {
                                factor *= ddata[3];
                            }
                        }
                        ddata[0] = red * factor;
                        ddata[1] = grn * factor;
                        ddata[2] = blu * factor;
                    } else if (is_LinearGray_stdScale) {
                        red = fromsRGB8LUT16[red] & 0xffff;
                        grn = fromsRGB8LUT16[grn] & 0xffff;
                        blu = fromsRGB8LUT16[blu] & 0xffff;
                        ddata[0] = ((0.2125 * red) +
                                    (0.7154 * grn) +
                                    (0.0721 * blu)) / 65535.0;
                        if (supportsAlpha) {
                            alp = (rgb>>24) & 0xff;
                            ddata[1] = alp * (1.0 / 255.0);
                            if (isAlphaPremultiplied) {
                                ddata[0] *= ddata[1];
                            }
                        }
                    } else if (is_ICCGray_stdScale) {
                        red = fromsRGB8LUT16[red] & 0xffff;
                        grn = fromsRGB8LUT16[grn] & 0xffff;
                        blu = fromsRGB8LUT16[blu] & 0xffff;
                        int gray = (int) ((0.2125f * red) +
                                          (0.7154f * grn) +
                                          (0.0721f * blu) + 0.5f);
                        ddata[0] = (fromLinearGray16ToOtherGray16LUT[gray] &
                                    0xffff) / 65535.0;
                        if (supportsAlpha) {
                            alp = (rgb>>24) & 0xff;
                            ddata[1] = alp * (1.0 / 255.0);
                            if (isAlphaPremultiplied) {
                                ddata[0] *= ddata[1];
                            }
                        }
                    } else {
                        float factor = 1.0f / 255.0f;
                        float norm[] = new float[3];
                        norm[0] = red * factor;
                        norm[1] = grn * factor;
                        norm[2] = blu * factor;
                        norm = colorSpace.fromRGB(norm);
                        if (supportsAlpha) {
                            alp = (rgb>>24) & 0xff;
                            ddata[numColorComponents] = alp * (1.0 / 255.0);
                            if (isAlphaPremultiplied) {
                                factor *= alp;
                                for (int i = 0; i < numColorComponents; i++) {
                                    norm[i] *= factor;
                                }
                            }
                        }
                        for (int i = 0; i < numColorComponents; i++) {
                            ddata[i] = norm[i];
                        }
                    }
                    return ddata;
                }
            }
        }

        // Handle BYTE, USHORT, & INT here
        //REMIND: maybe more efficient not to use int array for
        //DataBuffer.TYPE_USHORT and DataBuffer.TYPE_INT
        int intpixel[];
        if (transferType == DataBuffer.TYPE_INT &&
            pixel != null) {
           intpixel = (int[])pixel;
        } else {
            intpixel = new int[numComponents];
        }

        if (is_sRGB_stdScale || is_LinearRGB_stdScale) {
            int precision;
            float factor;
            if (is_LinearRGB_stdScale) {
                if (transferType == DataBuffer.TYPE_BYTE) {
                    red = fromsRGB8LUT8[red] & 0xff;
                    grn = fromsRGB8LUT8[grn] & 0xff;
                    blu = fromsRGB8LUT8[blu] & 0xff;
                    precision = 8;
                    factor = 1.0f / 255.0f;
                } else {
                    red = fromsRGB8LUT16[red] & 0xffff;
                    grn = fromsRGB8LUT16[grn] & 0xffff;
                    blu = fromsRGB8LUT16[blu] & 0xffff;
                    precision = 16;
                    factor = 1.0f / 65535.0f;
                }
            } else {
                precision = 8;
                factor = 1.0f / 255.0f;
            }
            if (supportsAlpha) {
                alp = (rgb>>24)&0xff;
                if (nBits[3] == 8) {
                    intpixel[3] = alp;
                }
                else {
                    intpixel[3] = (int)
                        (alp * (1.0f / 255.0f) * ((1<<nBits[3]) - 1) + 0.5f);
                }
                if (isAlphaPremultiplied) {
                    factor *= (alp * (1.0f / 255.0f));
                    precision = -1;  // force component calculations below
                }
            }
            if (nBits[0] == precision) {
                intpixel[0] = red;
            }
            else {
                intpixel[0] = (int) (red * factor * ((1<<nBits[0]) - 1) + 0.5f);
            }
            if (nBits[1] == precision) {
                intpixel[1] = (int)(grn);
            }
            else {
                intpixel[1] = (int) (grn * factor * ((1<<nBits[1]) - 1) + 0.5f);
            }
            if (nBits[2] == precision) {
                intpixel[2] = (int)(blu);
            }
            else {
                intpixel[2] = (int) (blu * factor * ((1<<nBits[2]) - 1) + 0.5f);
            }
        } else if (is_LinearGray_stdScale) {
            red = fromsRGB8LUT16[red] & 0xffff;
            grn = fromsRGB8LUT16[grn] & 0xffff;
            blu = fromsRGB8LUT16[blu] & 0xffff;
            float gray = ((0.2125f * red) +
                          (0.7154f * grn) +
                          (0.0721f * blu)) / 65535.0f;
            if (supportsAlpha) {
                alp = (rgb>>24) & 0xff;
                if (nBits[1] == 8) {
                    intpixel[1] = alp;
                } else {
                    intpixel[1] = (int) (alp * (1.0f / 255.0f) *
                                         ((1 << nBits[1]) - 1) + 0.5f);
                }
                if (isAlphaPremultiplied) {
                    gray *= (alp * (1.0f / 255.0f));
                }
            }
            intpixel[0] = (int) (gray * ((1 << nBits[0]) - 1) + 0.5f);
        } else if (is_ICCGray_stdScale) {
            red = fromsRGB8LUT16[red] & 0xffff;
            grn = fromsRGB8LUT16[grn] & 0xffff;
            blu = fromsRGB8LUT16[blu] & 0xffff;
            int gray16 = (int) ((0.2125f * red) +
                                (0.7154f * grn) +
                                (0.0721f * blu) + 0.5f);
            float gray = (fromLinearGray16ToOtherGray16LUT[gray16] &
                          0xffff) / 65535.0f;
            if (supportsAlpha) {
                alp = (rgb>>24) & 0xff;
                if (nBits[1] == 8) {
                    intpixel[1] = alp;
                } else {
                    intpixel[1] = (int) (alp * (1.0f / 255.0f) *
                                         ((1 << nBits[1]) - 1) + 0.5f);
                }
                if (isAlphaPremultiplied) {
                    gray *= (alp * (1.0f / 255.0f));
                }
            }
            intpixel[0] = (int) (gray * ((1 << nBits[0]) - 1) + 0.5f);
        } else {
            // Need to convert the color
            float[] norm = new float[3];
            float factor = 1.0f / 255.0f;
            norm[0] = red * factor;
            norm[1] = grn * factor;
            norm[2] = blu * factor;
            norm = colorSpace.fromRGB(norm);
            if (nonStdScale) {
                for (int i = 0; i < numColorComponents; i++) {
                    norm[i] = (norm[i] - compOffset[i]) *
                              compScale[i];
                    // REMIND: need to analyze whether this
                    // clamping is necessary
                    if (norm[i] < 0.0f) {
                        norm[i] = 0.0f;
                    }
                    if (norm[i] > 1.0f) {
                        norm[i] = 1.0f;
                    }
                }
            }
            if (supportsAlpha) {
                alp = (rgb>>24) & 0xff;
                if (nBits[numColorComponents] == 8) {
                    intpixel[numColorComponents] = alp;
                }
                else {
                    intpixel[numColorComponents] =
                        (int) (alp * factor *
                               ((1<<nBits[numColorComponents]) - 1) + 0.5f);
                }
                if (isAlphaPremultiplied) {
                    factor *= alp;
                    for (int i = 0; i < numColorComponents; i++) {
                        norm[i] *= factor;
                    }
                }
            }
            for (int i = 0; i < numColorComponents; i++) {
                intpixel[i] = (int) (norm[i] * ((1<<nBits[i]) - 1) + 0.5f);
            }
        }

        switch (transferType) {
            case DataBuffer.TYPE_BYTE: {
               byte bdata[];
               if (pixel == null) {
                   bdata = new byte[numComponents];
               } else {
                   bdata = (byte[])pixel;
               }
               for (int i = 0; i < numComponents; i++) {
                   bdata[i] = (byte)(0xff&intpixel[i]);
               }
               return bdata;
            }
            case DataBuffer.TYPE_USHORT:{
               short sdata[];
               if (pixel == null) {
                   sdata = new short[numComponents];
               } else {
                   sdata = (short[])pixel;
               }
               for (int i = 0; i < numComponents; i++) {
                   sdata[i] = (short)(intpixel[i]&0xffff);
               }
               return sdata;
            }
            case DataBuffer.TYPE_INT:
                if (maxBits > 23) {
                    // fix 4412670 - for components of 24 or more bits
                    // some calculations done above with float precision
                    // may lose enough precision that the integer result
                    // overflows nBits, so we need to clamp.
                    for (int i = 0; i < numComponents; i++) {
                        if (intpixel[i] > ((1<<nBits[i]) - 1)) {
                            intpixel[i] = (1<<nBits[i]) - 1;
                        }
                    }
                }
                return intpixel;
        }
        throw new IllegalArgumentException("This method has not been "+
                 "implemented for transferType " + transferType);
    }

   /** Returns an array of unnormalized color/alpha components given a pixel
     * in this <CODE>ColorModel</CODE>.
     * An IllegalArgumentException is thrown if the component value for this
     * <CODE>ColorModel</CODE> is not conveniently representable in the
     * unnormalized form.  Color/alpha components are stored
     * in the <CODE>components</CODE> array starting at <CODE>offset</CODE>
     * (even if the array is allocated by this method).
     *
     * @param pixel The pixel value specified as an integer.
     * @param components An integer array in which to store the unnormalized
     * color/alpha components. If the <CODE>components</CODE> array is null,
     * a new array is allocated.
     * @param offset An offset into the <CODE>components</CODE> array.
     *
     * @return The components array.
     *
     * @throws IllegalArgumentException If there is more than one
     * component in this <CODE>ColorModel</CODE>.
     * @throws IllegalArgumentException If this
     * <CODE>ColorModel</CODE> does not support the unnormalized form
     * @throws ArrayIndexOutOfBoundsException If the <CODE>components</CODE>
     * array is not null and is not large enough to hold all the color and
     * alpha components (starting at offset).
     */
    public int[] getComponents(int pixel, int[] components, int offset) {
        if (numComponents > 1) {
            throw new
                IllegalArgumentException("More than one component per pixel");
        }
        if (needScaleInit) {
            initScale();
        }
        if (noUnnorm) {
            throw new
                IllegalArgumentException(
                    "This ColorModel does not support the unnormalized form");
        }
        if (components == null) {
            components = new int[offset+1];
        }

        components[offset+0] = (pixel & ((1<<nBits[0]) - 1));
        return components;
    }

    /**
     * Returns an array of unnormalized color/alpha components given a pixel
     * in this <CODE>ColorModel</CODE>.  The pixel value is specified by an
     * array of data elements of type <CODE>transferType</CODE> passed in as
     * an object reference.
     * An IllegalArgumentException is thrown if the component values for this
     * <CODE>ColorModel</CODE> are not conveniently representable in the
     * unnormalized form.
     * Color/alpha components are stored in the <CODE>components</CODE> array
     * starting at  <CODE>offset</CODE> (even if the array is allocated by
     * this method).  Since <code>ComponentColorModel</code> can be
     * subclassed, subclasses inherit the
     * implementation of this method and if they don't override it then
     * this method might throw an exception if they use an unsupported
     * <code>transferType</code>.
     *
     * @param pixel A pixel value specified by an array of data elements of
     * type <CODE>transferType</CODE>.
     * @param components An integer array in which to store the unnormalized
     * color/alpha components. If the <CODE>components</CODE> array is null,
     * a new array is allocated.
     * @param offset An offset into the <CODE>components</CODE> array.
     *
     * @return The <CODE>components</CODE> array.
     *
     * @throws IllegalArgumentException If this
     * <CODE>ComponentColorModel</CODE> does not support the unnormalized form
     * @throws UnsupportedOperationException in some cases iff the
     * transfer type of this <CODE>ComponentColorModel</CODE>
     * is not one of the following transfer types:
     * <CODE>DataBuffer.TYPE_BYTE</CODE>, <CODE>DataBuffer.TYPE_USHORT</CODE>,
     * or <CODE>DataBuffer.TYPE_INT</CODE>.
     * @throws ClassCastException If <CODE>pixel</CODE> is not a primitive
     * array of type <CODE>transferType</CODE>.
     * @throws IllegalArgumentException If the <CODE>components</CODE> array is
     * not null and is not large enough to hold all the color and alpha
     * components (starting at offset), or if <CODE>pixel</CODE> is not large
     * enough to hold a pixel value for this ColorModel.
     */
    public int[] getComponents(Object pixel, int[] components, int offset) {
        int intpixel[];
        if (needScaleInit) {
            initScale();
        }
        if (noUnnorm) {
            throw new
                IllegalArgumentException(
                    "This ColorModel does not support the unnormalized form");
        }
        if (pixel instanceof int[]) {
            intpixel = (int[])pixel;
        } else {
            intpixel = DataBuffer.toIntArray(pixel);
            if (intpixel == null) {
               throw new UnsupportedOperationException("This method has not been "+
                   "implemented for transferType " + transferType);
            }
        }
        if (intpixel.length < numComponents) {
            throw new IllegalArgumentException
                ("Length of pixel array < number of components in model");
        }
        if (components == null) {
            components = new int[offset+numComponents];
        }
        else if ((components.length-offset) < numComponents) {
            throw new IllegalArgumentException
                ("Length of components array < number of components in model");
        }
        System.arraycopy(intpixel, 0, components, offset, numComponents);

        return components;
    }

    /**
     * Returns an array of all of the color/alpha components in unnormalized
     * form, given a normalized component array.  Unnormalized components
     * are unsigned integral values between 0 and 2<sup>n</sup> - 1, where
     * n is the number of bits for a particular component.  Normalized
     * components are float values between a per component minimum and
     * maximum specified by the <code>ColorSpace</code> object for this
     * <code>ColorModel</code>.  An <code>IllegalArgumentException</code>
     * will be thrown if color component values for this
     * <code>ColorModel</code> are not conveniently representable in the
     * unnormalized form.  If the
     * <code>components</code> array is <code>null</code>, a new array
     * will be allocated.  The <code>components</code> array will
     * be returned.  Color/alpha components are stored in the
     * <code>components</code> array starting at <code>offset</code> (even
     * if the array is allocated by this method). An
     * <code>ArrayIndexOutOfBoundsException</code> is thrown if the
     * <code>components</code> array is not <code>null</code> and is not
     * large enough to hold all the color and alpha
     * components (starting at <code>offset</code>).  An
     * <code>IllegalArgumentException</code> is thrown if the
     * <code>normComponents</code> array is not large enough to hold
     * all the color and alpha components starting at
     * <code>normOffset</code>.
     * @param normComponents an array containing normalized components
     * @param normOffset the offset into the <code>normComponents</code>
     * array at which to start retrieving normalized components
     * @param components an array that receives the components from
     * <code>normComponents</code>
     * @param offset the index into <code>components</code> at which to
     * begin storing normalized components from
     * <code>normComponents</code>
     * @return an array containing unnormalized color and alpha
     * components.
     * @throws IllegalArgumentException If this
     * <CODE>ComponentColorModel</CODE> does not support the unnormalized form
     * @throws IllegalArgumentException if the length of
     *          <code>normComponents</code> minus <code>normOffset</code>
     *          is less than <code>numComponents</code>
     */
    public int[] getUnnormalizedComponents(float[] normComponents,
                                           int normOffset,
                                           int[] components, int offset) {
        if (needScaleInit) {
            initScale();
        }
        if (noUnnorm) {
            throw new
                IllegalArgumentException(
                    "This ColorModel does not support the unnormalized form");
        }
        return super.getUnnormalizedComponents(normComponents, normOffset,
                                               components, offset);
    }

    /**
     * Returns an array of all of the color/alpha components in normalized
     * form, given an unnormalized component array.  Unnormalized components
     * are unsigned integral values between 0 and 2<sup>n</sup> - 1, where
     * n is the number of bits for a particular component.  Normalized
     * components are float values between a per component minimum and
     * maximum specified by the <code>ColorSpace</code> object for this
     * <code>ColorModel</code>.  An <code>IllegalArgumentException</code>
     * will be thrown if color component values for this
     * <code>ColorModel</code> are not conveniently representable in the
     * unnormalized form.  If the
     * <code>normComponents</code> array is <code>null</code>, a new array
     * will be allocated.  The <code>normComponents</code> array
     * will be returned.  Color/alpha components are stored in the
     * <code>normComponents</code> array starting at
     * <code>normOffset</code> (even if the array is allocated by this
     * method).  An <code>ArrayIndexOutOfBoundsException</code> is thrown
     * if the <code>normComponents</code> array is not <code>null</code>
     * and is not large enough to hold all the color and alpha components
     * (starting at <code>normOffset</code>).  An
     * <code>IllegalArgumentException</code> is thrown if the
     * <code>components</code> array is not large enough to hold all the
     * color and alpha components starting at <code>offset</code>.
     * @param components an array containing unnormalized components
     * @param offset the offset into the <code>components</code> array at
     * which to start retrieving unnormalized components
     * @param normComponents an array that receives the normalized components
     * @param normOffset the index into <code>normComponents</code> at
     * which to begin storing normalized components
     * @return an array containing normalized color and alpha
     * components.
     * @throws IllegalArgumentException If this
     * <CODE>ComponentColorModel</CODE> does not support the unnormalized form
     */
    public float[] getNormalizedComponents(int[] components, int offset,
                                           float[] normComponents,
                                           int normOffset) {
        if (needScaleInit) {
            initScale();
        }
        if (noUnnorm) {
            throw new
                IllegalArgumentException(
                    "This ColorModel does not support the unnormalized form");
        }
        return super.getNormalizedComponents(components, offset,
                                             normComponents, normOffset);
    }

    /**
     * Returns a pixel value represented as an int in this <CODE>ColorModel</CODE>,
     * given an array of unnormalized color/alpha components.
     *
     * @param components An array of unnormalized color/alpha components.
     * @param offset An offset into the <CODE>components</CODE> array.
     *
     * @return A pixel value represented as an int.
     *
     * @throws IllegalArgumentException If there is more than one component
     * in this <CODE>ColorModel</CODE>.
     * @throws IllegalArgumentException If this
     * <CODE>ComponentColorModel</CODE> does not support the unnormalized form
     */
    public int getDataElement(int[] components, int offset) {
        if (needScaleInit) {
            initScale();
        }
        if (numComponents == 1) {
            if (noUnnorm) {
                throw new
                    IllegalArgumentException(
                    "This ColorModel does not support the unnormalized form");
            }
            return components[offset+0];
        }
        throw new IllegalArgumentException("This model returns "+
                                           numComponents+
                                           " elements in the pixel array.");
    }

    /**
     * Returns a data element array representation of a pixel in this
     * <CODE>ColorModel</CODE>, given an array of unnormalized color/alpha
     * components. This array can then be passed to the <CODE>setDataElements</CODE>
     * method of a <CODE>WritableRaster</CODE> object.
     *
     * @param components An array of unnormalized color/alpha components.
     * @param offset The integer offset into the <CODE>components</CODE> array.
     * @param obj The object in which to store the data element array
     * representation of the pixel. If <CODE>obj</CODE> variable is null,
     * a new array is allocated.  If <CODE>obj</CODE> is not null, it must
     * be a primitive array of type <CODE>transferType</CODE>. An
     * <CODE>ArrayIndexOutOfBoundsException</CODE> is thrown if
     * <CODE>obj</CODE> is not large enough to hold a pixel value
     * for this <CODE>ColorModel</CODE>.  Since
     * <code>ComponentColorModel</code> can be subclassed, subclasses
     * inherit the implementation of this method and if they don't
     * override it then they throw an exception if they use an
     * unsupported <code>transferType</code>.
     *
     * @return The data element array representation of a pixel
     * in this <CODE>ColorModel</CODE>.
     *
     * @throws IllegalArgumentException If the components array
     * is not large enough to hold all the color and alpha components
     * (starting at offset).
     * @throws ClassCastException If <CODE>obj</CODE> is not null and is not a
     * primitive  array of type <CODE>transferType</CODE>.
     * @throws ArrayIndexOutOfBoundsException If <CODE>obj</CODE> is not large
     * enough to hold a pixel value for this <CODE>ColorModel</CODE>.
     * @throws IllegalArgumentException If this
     * <CODE>ComponentColorModel</CODE> does not support the unnormalized form
     * @throws UnsupportedOperationException If the transfer type of
     * this <CODE>ComponentColorModel</CODE>
     * is not one of the following transfer types:
     * <CODE>DataBuffer.TYPE_BYTE</CODE>, <CODE>DataBuffer.TYPE_USHORT</CODE>,
     * or <CODE>DataBuffer.TYPE_INT</CODE>.
     *
     * @see WritableRaster#setDataElements
     * @see SampleModel#setDataElements
     */
    public Object getDataElements(int[] components, int offset, Object obj) {
        if (needScaleInit) {
            initScale();
        }
        if (noUnnorm) {
            throw new
                IllegalArgumentException(
                    "This ColorModel does not support the unnormalized form");
        }
        if ((components.length-offset) < numComponents) {
            throw new IllegalArgumentException("Component array too small"+
                                               " (should be "+numComponents);
        }
        switch(transferType) {
        case DataBuffer.TYPE_INT:
            {
                int[] pixel;
                if (obj == null) {
                    pixel = new int[numComponents];
                }
                else {
                    pixel = (int[]) obj;
                }
                System.arraycopy(components, offset, pixel, 0,
                                 numComponents);
                return pixel;
            }

        case DataBuffer.TYPE_BYTE:
            {
                byte[] pixel;
                if (obj == null) {
                    pixel = new byte[numComponents];
                }
                else {
                    pixel = (byte[]) obj;
                }
                for (int i=0; i < numComponents; i++) {
                    pixel[i] = (byte) (components[offset+i]&0xff);
                }
                return pixel;
            }

        case DataBuffer.TYPE_USHORT:
            {
                short[] pixel;
                if (obj == null) {
                    pixel = new short[numComponents];
                }
                else {
                    pixel = (short[]) obj;
                }
                for (int i=0; i < numComponents; i++) {
                    pixel[i] = (short) (components[offset+i]&0xffff);
                }
                return pixel;
            }

        default:
            throw new UnsupportedOperationException("This method has not been "+
                                        "implemented for transferType " +
                                        transferType);
        }
    }

    /**
     * Returns a pixel value represented as an <code>int</code> in this
     * <code>ColorModel</code>, given an array of normalized color/alpha
     * components.  This method will throw an
     * <code>IllegalArgumentException</code> if pixel values for this
     * <code>ColorModel</code> are not conveniently representable as a
     * single <code>int</code>.  An
     * <code>ArrayIndexOutOfBoundsException</code> is thrown if  the
     * <code>normComponents</code> array is not large enough to hold all the
     * color and alpha components (starting at <code>normOffset</code>).
     * @param normComponents an array of normalized color and alpha
     * components
     * @param normOffset the index into <code>normComponents</code> at which to
     * begin retrieving the color and alpha components
     * @return an <code>int</code> pixel value in this
     * <code>ColorModel</code> corresponding to the specified components.
     * @throws IllegalArgumentException if
     *  pixel values for this <code>ColorModel</code> are not
     *  conveniently representable as a single <code>int</code>
     * @throws ArrayIndexOutOfBoundsException if
     *  the <code>normComponents</code> array is not large enough to
     *  hold all of the color and alpha components starting at
     *  <code>normOffset</code>
     * @since 1.4
     */
    public int getDataElement(float[] normComponents, int normOffset) {
        if (numComponents > 1) {
            throw new
                IllegalArgumentException("More than one component per pixel");
        }
        if (signed) {
            throw new
                IllegalArgumentException("Component value is signed");
        }
        if (needScaleInit) {
            initScale();
        }
        Object pixel = getDataElements(normComponents, normOffset, null);
        switch (transferType) {
        case DataBuffer.TYPE_BYTE:
            {
                byte bpixel[] = (byte[]) pixel;
                return bpixel[0] & 0xff;
            }
        case DataBuffer.TYPE_USHORT:
            {
                short[] uspixel = (short[]) pixel;
                return uspixel[0] & 0xffff;
            }
        case DataBuffer.TYPE_INT:
            {
                int[] ipixel = (int[]) pixel;
                return ipixel[0];
            }
        default:
            throw new UnsupportedOperationException("This method has not been "
                + "implemented for transferType " + transferType);
        }
    }

    /**
     * Returns a data element array representation of a pixel in this
     * <code>ColorModel</code>, given an array of normalized color/alpha
     * components.  This array can then be passed to the
     * <code>setDataElements</code> method of a <code>WritableRaster</code>
     * object.  An <code>ArrayIndexOutOfBoundsException</code> is thrown
     * if the <code>normComponents</code> array is not large enough to hold
     * all the color and alpha components (starting at
     * <code>normOffset</code>).  If the <code>obj</code> variable is
     * <code>null</code>, a new array will be allocated.  If
     * <code>obj</code> is not <code>null</code>, it must be a primitive
     * array of type transferType; otherwise, a
     * <code>ClassCastException</code> is thrown.  An
     * <code>ArrayIndexOutOfBoundsException</code> is thrown if
     * <code>obj</code> is not large enough to hold a pixel value for this
     * <code>ColorModel</code>.
     * @param normComponents an array of normalized color and alpha
     * components
     * @param normOffset the index into <code>normComponents</code> at which to
     * begin retrieving color and alpha components
     * @param obj a primitive data array to hold the returned pixel
     * @return an <code>Object</code> which is a primitive data array
     * representation of a pixel
     * @throws ClassCastException if <code>obj</code>
     *  is not a primitive array of type <code>transferType</code>
     * @throws ArrayIndexOutOfBoundsException if
     *  <code>obj</code> is not large enough to hold a pixel value
     *  for this <code>ColorModel</code> or the <code>normComponents</code>
     *  array is not large enough to hold all of the color and alpha
     *  components starting at <code>normOffset</code>
     * @see WritableRaster#setDataElements
     * @see SampleModel#setDataElements
     * @since 1.4
     */
    public Object getDataElements(float[] normComponents, int normOffset,
                                  Object obj) {
        boolean needAlpha = supportsAlpha && isAlphaPremultiplied;
        float[] stdNormComponents;
        if (needScaleInit) {
            initScale();
        }
        if (nonStdScale) {
            stdNormComponents = new float[numComponents];
            for (int c = 0, nc = normOffset; c < numColorComponents;
                 c++, nc++) {
                stdNormComponents[c] = (normComponents[nc] - compOffset[c]) *
                                       compScale[c];
                // REMIND: need to analyze whether this
                // clamping is necessary
                if (stdNormComponents[c] < 0.0f) {
                    stdNormComponents[c] = 0.0f;
                }
                if (stdNormComponents[c] > 1.0f) {
                    stdNormComponents[c] = 1.0f;
                }
            }
            if (supportsAlpha) {
                stdNormComponents[numColorComponents] =
                    normComponents[numColorComponents + normOffset];
            }
            normOffset = 0;
        } else {
            stdNormComponents = normComponents;
        }
        switch (transferType) {
        case DataBuffer.TYPE_BYTE:
            byte[] bpixel;
            if (obj == null) {
                bpixel = new byte[numComponents];
            } else {
                bpixel = (byte[]) obj;
            }
            if (needAlpha) {
                float alpha =
                    stdNormComponents[numColorComponents + normOffset];
                for (int c = 0, nc = normOffset; c < numColorComponents;
                     c++, nc++) {
                    bpixel[c] = (byte) ((stdNormComponents[nc] * alpha) *
                                        ((float) ((1 << nBits[c]) - 1)) + 0.5f);
                }
                bpixel[numColorComponents] =
                    (byte) (alpha *
                            ((float) ((1 << nBits[numColorComponents]) - 1)) +
                            0.5f);
            } else {
                for (int c = 0, nc = normOffset; c < numComponents;
                     c++, nc++) {
                    bpixel[c] = (byte) (stdNormComponents[nc] *
                                        ((float) ((1 << nBits[c]) - 1)) + 0.5f);
                }
            }
            return bpixel;
        case DataBuffer.TYPE_USHORT:
            short[] uspixel;
            if (obj == null) {
                uspixel = new short[numComponents];
            } else {
                uspixel = (short[]) obj;
            }
            if (needAlpha) {
                float alpha =
                    stdNormComponents[numColorComponents + normOffset];
                for (int c = 0, nc = normOffset; c < numColorComponents;
                     c++, nc++) {
                    uspixel[c] = (short) ((stdNormComponents[nc] * alpha) *
                                          ((float) ((1 << nBits[c]) - 1)) +
                                          0.5f);
                }
                uspixel[numColorComponents] =
                    (short) (alpha *
                             ((float) ((1 << nBits[numColorComponents]) - 1)) +
                             0.5f);
            } else {
                for (int c = 0, nc = normOffset; c < numComponents;
                     c++, nc++) {
                    uspixel[c] = (short) (stdNormComponents[nc] *
                                          ((float) ((1 << nBits[c]) - 1)) +
                                          0.5f);
                }
            }
            return uspixel;
        case DataBuffer.TYPE_INT:
            int[] ipixel;
            if (obj == null) {
                ipixel = new int[numComponents];
            } else {
                ipixel = (int[]) obj;
            }
            if (needAlpha) {
                float alpha =
                    stdNormComponents[numColorComponents + normOffset];
                for (int c = 0, nc = normOffset; c < numColorComponents;
                     c++, nc++) {
                    ipixel[c] = (int) ((stdNormComponents[nc] * alpha) *
                                       ((float) ((1 << nBits[c]) - 1)) + 0.5f);
                }
                ipixel[numColorComponents] =
                    (int) (alpha *
                           ((float) ((1 << nBits[numColorComponents]) - 1)) +
                           0.5f);
            } else {
                for (int c = 0, nc = normOffset; c < numComponents;
                     c++, nc++) {
                    ipixel[c] = (int) (stdNormComponents[nc] *
                                       ((float) ((1 << nBits[c]) - 1)) + 0.5f);
                }
            }
            return ipixel;
        case DataBuffer.TYPE_SHORT:
            short[] spixel;
            if (obj == null) {
                spixel = new short[numComponents];
            } else {
                spixel = (short[]) obj;
            }
            if (needAlpha) {
                float alpha =
                    stdNormComponents[numColorComponents + normOffset];
                for (int c = 0, nc = normOffset; c < numColorComponents;
                     c++, nc++) {
                    spixel[c] = (short)
                        (stdNormComponents[nc] * alpha * 32767.0f + 0.5f);
                }
                spixel[numColorComponents] = (short) (alpha * 32767.0f + 0.5f);
            } else {
                for (int c = 0, nc = normOffset; c < numComponents;
                     c++, nc++) {
                    spixel[c] = (short)
                        (stdNormComponents[nc] * 32767.0f + 0.5f);
                }
            }
            return spixel;
        case DataBuffer.TYPE_FLOAT:
            float[] fpixel;
            if (obj == null) {
                fpixel = new float[numComponents];
            } else {
                fpixel = (float[]) obj;
            }
            if (needAlpha) {
                float alpha = normComponents[numColorComponents + normOffset];
                for (int c = 0, nc = normOffset; c < numColorComponents;
                     c++, nc++) {
                    fpixel[c] = normComponents[nc] * alpha;
                }
                fpixel[numColorComponents] = alpha;
            } else {
                for (int c = 0, nc = normOffset; c < numComponents;
                     c++, nc++) {
                    fpixel[c] = normComponents[nc];
                }
            }
            return fpixel;
        case DataBuffer.TYPE_DOUBLE:
            double[] dpixel;
            if (obj == null) {
                dpixel = new double[numComponents];
            } else {
                dpixel = (double[]) obj;
            }
            if (needAlpha) {
                double alpha =
                    (double) (normComponents[numColorComponents + normOffset]);
                for (int c = 0, nc = normOffset; c < numColorComponents;
                     c++, nc++) {
                    dpixel[c] = normComponents[nc] * alpha;
                }
                dpixel[numColorComponents] = alpha;
            } else {
                for (int c = 0, nc = normOffset; c < numComponents;
                     c++, nc++) {
                    dpixel[c] = (double) normComponents[nc];
                }
            }
            return dpixel;
        default:
            throw new UnsupportedOperationException("This method has not been "+
                                        "implemented for transferType " +
                                        transferType);
        }
    }

    /**
     * Returns an array of all of the color/alpha components in normalized
     * form, given a pixel in this <code>ColorModel</code>.  The pixel
     * value is specified by an array of data elements of type transferType
     * passed in as an object reference.  If pixel is not a primitive array
     * of type transferType, a <code>ClassCastException</code> is thrown.
     * An <code>ArrayIndexOutOfBoundsException</code> is thrown if
     * <code>pixel</code> is not large enough to hold a pixel value for this
     * <code>ColorModel</code>.
     * Normalized components are float values between a per component minimum
     * and maximum specified by the <code>ColorSpace</code> object for this
     * <code>ColorModel</code>.  If the
     * <code>normComponents</code> array is <code>null</code>, a new array
     * will be allocated.  The <code>normComponents</code> array
     * will be returned.  Color/alpha components are stored in the
     * <code>normComponents</code> array starting at
     * <code>normOffset</code> (even if the array is allocated by this
     * method).  An <code>ArrayIndexOutOfBoundsException</code> is thrown
     * if the <code>normComponents</code> array is not <code>null</code>
     * and is not large enough to hold all the color and alpha components
     * (starting at <code>normOffset</code>).
     * <p>
     * This method must be overridden by a subclass if that subclass
     * is designed to translate pixel sample values to color component values
     * in a non-default way.  The default translations implemented by this
     * class is described in the class comments.  Any subclass implementing
     * a non-default translation must follow the constraints on allowable
     * translations defined there.
     * @param pixel the specified pixel
     * @param normComponents an array to receive the normalized components
     * @param normOffset the offset into the <code>normComponents</code>
     * array at which to start storing normalized components
     * @return an array containing normalized color and alpha
     * components.
     * @throws ClassCastException if <code>pixel</code> is not a primitive
     *          array of type transferType
     * @throws ArrayIndexOutOfBoundsException if
     *          <code>normComponents</code> is not large enough to hold all
     *          color and alpha components starting at <code>normOffset</code>
     * @throws ArrayIndexOutOfBoundsException if
     *          <code>pixel</code> is not large enough to hold a pixel
     *          value for this <code>ColorModel</code>.
     * @since 1.4
     */
    public float[] getNormalizedComponents(Object pixel,
                                           float[] normComponents,
                                           int normOffset) {
        if (normComponents == null) {
            normComponents = new float[numComponents+normOffset];
        }
        switch (transferType) {
        case DataBuffer.TYPE_BYTE:
            byte[] bpixel = (byte[]) pixel;
            for (int c = 0, nc = normOffset; c < numComponents; c++, nc++) {
                normComponents[nc] = ((float) (bpixel[c] & 0xff)) /
                                     ((float) ((1 << nBits[c]) - 1));
            }
            break;
        case DataBuffer.TYPE_USHORT:
            short[] uspixel = (short[]) pixel;
            for (int c = 0, nc = normOffset; c < numComponents; c++, nc++) {
                normComponents[nc] = ((float) (uspixel[c] & 0xffff)) /
                                     ((float) ((1 << nBits[c]) - 1));
            }
            break;
        case DataBuffer.TYPE_INT:
            int[] ipixel = (int[]) pixel;
            for (int c = 0, nc = normOffset; c < numComponents; c++, nc++) {
                normComponents[nc] = ((float) ipixel[c]) /
                                     ((float) ((1 << nBits[c]) - 1));
            }
            break;
        case DataBuffer.TYPE_SHORT:
            short[] spixel = (short[]) pixel;
            for (int c = 0, nc = normOffset; c < numComponents; c++, nc++) {
                normComponents[nc] = ((float) spixel[c]) / 32767.0f;
            }
            break;
        case DataBuffer.TYPE_FLOAT:
            float[] fpixel = (float[]) pixel;
            for (int c = 0, nc = normOffset; c < numComponents; c++, nc++) {
                normComponents[nc] = fpixel[c];
            }
            break;
        case DataBuffer.TYPE_DOUBLE:
            double[] dpixel = (double[]) pixel;
            for (int c = 0, nc = normOffset; c < numComponents; c++, nc++) {
                normComponents[nc] = (float) dpixel[c];
            }
            break;
        default:
            throw new UnsupportedOperationException("This method has not been "+
                                        "implemented for transferType " +
                                        transferType);
        }

        if (supportsAlpha && isAlphaPremultiplied) {
            float alpha = normComponents[numColorComponents + normOffset];
            if (alpha != 0.0f) {
                float invAlpha = 1.0f / alpha;
                for (int c = normOffset; c < numColorComponents + normOffset;
                     c++) {
                    normComponents[c] *= invAlpha;
                }
            }
        }
        if (min != null) {
            // Normally (i.e. when this class is not subclassed to override
            // this method), the test (min != null) will be equivalent to
            // the test (nonStdScale).  However, there is an unlikely, but
            // possible case, in which this method is overridden, nonStdScale
            // is set true by initScale(), the subclass method for some
            // reason calls this superclass method, but the min and
            // diffMinMax arrays were never initialized by setupLUTs().  In
            // that case, the right thing to do is follow the intended
            // semantics of this method, and rescale the color components
            // only if the ColorSpace min/max were detected to be other
            // than 0.0/1.0 by setupLUTs().  Note that this implies the
            // transferType is byte, ushort, int, or short - i.e. components
            // derived from float and double pixel data are never rescaled.
            for (int c = 0; c < numColorComponents; c++) {
                normComponents[c + normOffset] = min[c] +
                    diffMinMax[c] * normComponents[c + normOffset];
            }
        }
        return normComponents;
    }

    /**
     * Forces the raster data to match the state specified in the
     * <CODE>isAlphaPremultiplied</CODE> variable, assuming the data
     * is currently correctly described by this <CODE>ColorModel</CODE>.
     * It may multiply or divide the color raster data by alpha, or
     * do nothing if the data is in the correct state.  If the data needs
     * to be coerced, this method also returns an instance of
     * this <CODE>ColorModel</CODE> with
     * the <CODE>isAlphaPremultiplied</CODE> flag set appropriately.
     * Since <code>ColorModel</code> can be subclassed, subclasses inherit
     * the implementation of this method and if they don't override it
     * then they throw an exception if they use an unsupported
     * <code>transferType</code>.
     *
     * @throws NullPointerException if <code>raster</code> is
     * <code>null</code> and data coercion is required.
     * @throws UnsupportedOperationException if the transfer type of
     * this <CODE>ComponentColorModel</CODE>
     * is not one of the supported transfer types:
     * <CODE>DataBuffer.TYPE_BYTE</CODE>, <CODE>DataBuffer.TYPE_USHORT</CODE>,
     * <CODE>DataBuffer.TYPE_INT</CODE>, <CODE>DataBuffer.TYPE_SHORT</CODE>,
     * <CODE>DataBuffer.TYPE_FLOAT</CODE>, or <CODE>DataBuffer.TYPE_DOUBLE</CODE>.
     */
    public ColorModel coerceData (WritableRaster raster,
                                  boolean isAlphaPremultiplied) {
        if ((supportsAlpha == false) ||
            (this.isAlphaPremultiplied == isAlphaPremultiplied))
        {
            // Nothing to do
            return this;
        }

        int w = raster.getWidth();
        int h = raster.getHeight();
        int aIdx = raster.getNumBands() - 1;
        float normAlpha;
        int rminX = raster.getMinX();
        int rY = raster.getMinY();
        int rX;
        if (isAlphaPremultiplied) {
            switch (transferType) {
                case DataBuffer.TYPE_BYTE: {
                    byte pixel[] = null;
                    byte zpixel[] = null;
                    float alphaScale = 1.0f / ((float) ((1<<nBits[aIdx]) - 1));
                    for (int y = 0; y < h; y++, rY++) {
                        rX = rminX;
                        for (int x = 0; x < w; x++, rX++) {
                            pixel = (byte[])raster.getDataElements(rX, rY,
                                                                   pixel);
                            normAlpha = (pixel[aIdx] & 0xff) * alphaScale;
                            if (normAlpha != 0.0f) {
                                for (int c=0; c < aIdx; c++) {
                                    pixel[c] = (byte)((pixel[c] & 0xff) *
                                                      normAlpha + 0.5f);
                                }
                                raster.setDataElements(rX, rY, pixel);
                            } else {
                                if (zpixel == null) {
                                    zpixel = new byte[numComponents];
                                    java.util.Arrays.fill(zpixel, (byte) 0);
                                }
                                raster.setDataElements(rX, rY, zpixel);
                            }
                        }
                    }
                }
                break;
                case DataBuffer.TYPE_USHORT: {
                    short pixel[] = null;
                    short zpixel[] = null;
                    float alphaScale = 1.0f / ((float) ((1<<nBits[aIdx]) - 1));
                    for (int y = 0; y < h; y++, rY++) {
                        rX = rminX;
                        for (int x = 0; x < w; x++, rX++) {
                            pixel = (short[])raster.getDataElements(rX, rY,
                                                                    pixel);
                            normAlpha = (pixel[aIdx] & 0xffff) * alphaScale;
                            if (normAlpha != 0.0f) {
                                for (int c=0; c < aIdx; c++) {
                                    pixel[c] = (short)
                                        ((pixel[c] & 0xffff) * normAlpha +
                                         0.5f);
                                }
                                raster.setDataElements(rX, rY, pixel);
                            } else {
                                if (zpixel == null) {
                                    zpixel = new short[numComponents];
                                    java.util.Arrays.fill(zpixel, (short) 0);
                                }
                                raster.setDataElements(rX, rY, zpixel);
                            }
                        }
                    }
                }
                break;
                case DataBuffer.TYPE_INT: {
                    int pixel[] = null;
                    int zpixel[] = null;
                    float alphaScale = 1.0f / ((float) ((1<<nBits[aIdx]) - 1));
                    for (int y = 0; y < h; y++, rY++) {
                        rX = rminX;
                        for (int x = 0; x < w; x++, rX++) {
                            pixel = (int[])raster.getDataElements(rX, rY,
                                                                  pixel);
                            normAlpha = pixel[aIdx] * alphaScale;
                            if (normAlpha != 0.0f) {
                                for (int c=0; c < aIdx; c++) {
                                    pixel[c] = (int) (pixel[c] * normAlpha +
                                                      0.5f);
                                }
                                raster.setDataElements(rX, rY, pixel);
                            } else {
                                if (zpixel == null) {
                                    zpixel = new int[numComponents];
                                    java.util.Arrays.fill(zpixel, 0);
                                }
                                raster.setDataElements(rX, rY, zpixel);
                            }
                        }
                    }
                }
                break;
                case DataBuffer.TYPE_SHORT: {
                    short pixel[] = null;
                    short zpixel[] = null;
                    float alphaScale = 1.0f / 32767.0f;
                    for (int y = 0; y < h; y++, rY++) {
                        rX = rminX;
                        for (int x = 0; x < w; x++, rX++) {
                            pixel = (short[]) raster.getDataElements(rX, rY,
                                                                     pixel);
                            normAlpha = pixel[aIdx] * alphaScale;
                            if (normAlpha != 0.0f) {
                                for (int c=0; c < aIdx; c++) {
                                    pixel[c] = (short) (pixel[c] * normAlpha +
                                                        0.5f);
                                }
                                raster.setDataElements(rX, rY, pixel);
                            } else {
                                if (zpixel == null) {
                                    zpixel = new short[numComponents];
                                    java.util.Arrays.fill(zpixel, (short) 0);
                                }
                                raster.setDataElements(rX, rY, zpixel);
                            }
                        }
                    }
                }
                break;
                case DataBuffer.TYPE_FLOAT: {
                    float pixel[] = null;
                    float zpixel[] = null;
                    for (int y = 0; y < h; y++, rY++) {
                        rX = rminX;
                        for (int x = 0; x < w; x++, rX++) {
                            pixel = (float[]) raster.getDataElements(rX, rY,
                                                                     pixel);
                            normAlpha = pixel[aIdx];
                            if (normAlpha != 0.0f) {
                                for (int c=0; c < aIdx; c++) {
                                    pixel[c] *= normAlpha;
                                }
                                raster.setDataElements(rX, rY, pixel);
                            } else {
                                if (zpixel == null) {
                                    zpixel = new float[numComponents];
                                    java.util.Arrays.fill(zpixel, 0.0f);
                                }
                                raster.setDataElements(rX, rY, zpixel);
                            }
                        }
                    }
                }
                break;
                case DataBuffer.TYPE_DOUBLE: {
                    double pixel[] = null;
                    double zpixel[] = null;
                    for (int y = 0; y < h; y++, rY++) {
                        rX = rminX;
                        for (int x = 0; x < w; x++, rX++) {
                            pixel = (double[]) raster.getDataElements(rX, rY,
                                                                      pixel);
                            double dnormAlpha = pixel[aIdx];
                            if (dnormAlpha != 0.0) {
                                for (int c=0; c < aIdx; c++) {
                                    pixel[c] *= dnormAlpha;
                                }
                                raster.setDataElements(rX, rY, pixel);
                            } else {
                                if (zpixel == null) {
                                    zpixel = new double[numComponents];
                                    java.util.Arrays.fill(zpixel, 0.0);
                                }
                                raster.setDataElements(rX, rY, zpixel);
                            }
                        }
                    }
                }
                break;
                default:
                    throw new UnsupportedOperationException("This method has not been "+
                         "implemented for transferType " + transferType);
            }
        }
        else {
            // We are premultiplied and want to divide it out
            switch (transferType) {
                case DataBuffer.TYPE_BYTE: {
                    byte pixel[] = null;
                    float alphaScale = 1.0f / ((float) ((1<<nBits[aIdx]) - 1));
                    for (int y = 0; y < h; y++, rY++) {
                        rX = rminX;
                        for (int x = 0; x < w; x++, rX++) {
                            pixel = (byte[])raster.getDataElements(rX, rY,
                                                                   pixel);
                            normAlpha = (pixel[aIdx] & 0xff) * alphaScale;
                            if (normAlpha != 0.0f) {
                                float invAlpha = 1.0f / normAlpha;
                                for (int c=0; c < aIdx; c++) {
                                    pixel[c] = (byte)
                                        ((pixel[c] & 0xff) * invAlpha + 0.5f);
                                }
                                raster.setDataElements(rX, rY, pixel);
                            }
                        }
                    }
                }
                break;
                case DataBuffer.TYPE_USHORT: {
                    short pixel[] = null;
                    float alphaScale = 1.0f / ((float) ((1<<nBits[aIdx]) - 1));
                    for (int y = 0; y < h; y++, rY++) {
                        rX = rminX;
                        for (int x = 0; x < w; x++, rX++) {
                            pixel = (short[])raster.getDataElements(rX, rY,
                                                                    pixel);
                            normAlpha = (pixel[aIdx] & 0xffff) * alphaScale;
                            if (normAlpha != 0.0f) {
                                float invAlpha = 1.0f / normAlpha;
                                for (int c=0; c < aIdx; c++) {
                                    pixel[c] = (short)
                                        ((pixel[c] & 0xffff) * invAlpha + 0.5f);
                                }
                                raster.setDataElements(rX, rY, pixel);
                            }
                        }
                    }
                }
                break;
                case DataBuffer.TYPE_INT: {
                    int pixel[] = null;
                    float alphaScale = 1.0f / ((float) ((1<<nBits[aIdx]) - 1));
                    for (int y = 0; y < h; y++, rY++) {
                        rX = rminX;
                        for (int x = 0; x < w; x++, rX++) {
                            pixel = (int[])raster.getDataElements(rX, rY,
                                                                  pixel);
                            normAlpha = pixel[aIdx] * alphaScale;
                            if (normAlpha != 0.0f) {
                                float invAlpha = 1.0f / normAlpha;
                                for (int c=0; c < aIdx; c++) {
                                    pixel[c] = (int)
                                        (pixel[c] * invAlpha + 0.5f);
                                }
                                raster.setDataElements(rX, rY, pixel);
                            }
                        }
                    }
                }
                break;
                case DataBuffer.TYPE_SHORT: {
                    short pixel[] = null;
                    float alphaScale = 1.0f / 32767.0f;
                    for (int y = 0; y < h; y++, rY++) {
                        rX = rminX;
                        for (int x = 0; x < w; x++, rX++) {
                            pixel = (short[])raster.getDataElements(rX, rY,
                                                                    pixel);
                            normAlpha = pixel[aIdx] * alphaScale;
                            if (normAlpha != 0.0f) {
                                float invAlpha = 1.0f / normAlpha;
                                for (int c=0; c < aIdx; c++) {
                                    pixel[c] = (short)
                                        (pixel[c] * invAlpha + 0.5f);
                                }
                                raster.setDataElements(rX, rY, pixel);
                            }
                        }
                    }
                }
                break;
                case DataBuffer.TYPE_FLOAT: {
                    float pixel[] = null;
                    for (int y = 0; y < h; y++, rY++) {
                        rX = rminX;
                        for (int x = 0; x < w; x++, rX++) {
                            pixel = (float[])raster.getDataElements(rX, rY,
                                                                    pixel);
                            normAlpha = pixel[aIdx];
                            if (normAlpha != 0.0f) {
                                float invAlpha = 1.0f / normAlpha;
                                for (int c=0; c < aIdx; c++) {
                                    pixel[c] *= invAlpha;
                                }
                                raster.setDataElements(rX, rY, pixel);
                            }
                        }
                    }
                }
                break;
                case DataBuffer.TYPE_DOUBLE: {
                    double pixel[] = null;
                    for (int y = 0; y < h; y++, rY++) {
                        rX = rminX;
                        for (int x = 0; x < w; x++, rX++) {
                            pixel = (double[])raster.getDataElements(rX, rY,
                                                                     pixel);
                            double dnormAlpha = pixel[aIdx];
                            if (dnormAlpha != 0.0) {
                                double invAlpha = 1.0 / dnormAlpha;
                                for (int c=0; c < aIdx; c++) {
                                    pixel[c] *= invAlpha;
                                }
                                raster.setDataElements(rX, rY, pixel);
                            }
                        }
                    }
                }
                break;
                default:
                    throw new UnsupportedOperationException("This method has not been "+
                         "implemented for transferType " + transferType);
            }
        }

        // Return a new color model
        if (!signed) {
            return new java.awt.image.ComponentColorModel(colorSpace, nBits, supportsAlpha,
                                           isAlphaPremultiplied, transparency,
                                           transferType);
        } else {
            return new java.awt.image.ComponentColorModel(colorSpace, supportsAlpha,
                                           isAlphaPremultiplied, transparency,
                                           transferType);
        }

    }

    /**
      * Returns true if <CODE>raster</CODE> is compatible with this
      * <CODE>ColorModel</CODE>; false if it is not.
      *
      * @param raster The <CODE>Raster</CODE> object to test for compatibility.
      *
      * @return <CODE>true</CODE> if <CODE>raster</CODE> is compatible with this
      * <CODE>ColorModel</CODE>, <CODE>false</CODE> if it is not.
      */
    public boolean isCompatibleRaster(Raster raster) {

        SampleModel sm = raster.getSampleModel();

        if (sm instanceof ComponentSampleModel) {
            if (sm.getNumBands() != getNumComponents()) {
                return false;
            }
            for (int i=0; i<nBits.length; i++) {
                if (sm.getSampleSize(i) < nBits[i]) {
                    return false;
                }
            }
            return (raster.getTransferType() == transferType);
        }
        else {
            return false;
        }
    }

    /**
     * Creates a <CODE>WritableRaster</CODE> with the specified width and height,
     * that  has a data layout (<CODE>SampleModel</CODE>) compatible with
     * this <CODE>ColorModel</CODE>.
     *
     * @param w The width of the <CODE>WritableRaster</CODE> you want to create.
     * @param h The height of the <CODE>WritableRaster</CODE> you want to create.
     *
     * @return A <CODE>WritableRaster</CODE> that is compatible with
     * this <CODE>ColorModel</CODE>.
     * @see WritableRaster
     * @see SampleModel
     */
    public WritableRaster createCompatibleWritableRaster (int w, int h) {
        int dataSize = w*h*numComponents;
        WritableRaster raster = null;

        switch (transferType) {
        case DataBuffer.TYPE_BYTE:
        case DataBuffer.TYPE_USHORT:
            raster = Raster.createInterleavedRaster(transferType,
                                                    w, h,
                                                    numComponents, null);
            break;
        default:
            SampleModel sm = createCompatibleSampleModel(w, h);
            DataBuffer db = sm.createDataBuffer();
            raster = Raster.createWritableRaster(sm, db, null);
        }

        return raster;
    }

    /**
     * Creates a <CODE>SampleModel</CODE> with the specified width and height,
     * that  has a data layout compatible with this <CODE>ColorModel</CODE>.
     *
     * @param w The width of the <CODE>SampleModel</CODE> you want to create.
     * @param h The height of the <CODE>SampleModel</CODE> you want to create.
     *
     * @return A <CODE>SampleModel</CODE> that is compatible with this
     * <CODE>ColorModel</CODE>.
     *
     * @see SampleModel
     */
    public SampleModel createCompatibleSampleModel(int w, int h) {
        int[] bandOffsets = new int[numComponents];
        for (int i=0; i < numComponents; i++) {
            bandOffsets[i] = i;
        }
        switch (transferType) {
        case DataBuffer.TYPE_BYTE:
        case DataBuffer.TYPE_USHORT:
            return new PixelInterleavedSampleModel(transferType, w, h,
                                                   numComponents,
                                                   w*numComponents,
                                                   bandOffsets);
        default:
            return new ComponentSampleModel(transferType, w, h,
                                            numComponents,
                                            w*numComponents,
                                            bandOffsets);
        }
    }

    /**
     * Checks whether or not the specified <CODE>SampleModel</CODE>
     * is compatible with this <CODE>ColorModel</CODE>.
     *
     * @param sm The <CODE>SampleModel</CODE> to test for compatibility.
     *
     * @return <CODE>true</CODE> if the <CODE>SampleModel</CODE> is
     * compatible with this <CODE>ColorModel</CODE>, <CODE>false</CODE>
     * if it is not.
     *
     * @see SampleModel
     */
    public boolean isCompatibleSampleModel(SampleModel sm) {
        if (!(sm instanceof ComponentSampleModel)) {
            return false;
        }

        // Must have the same number of components
        if (numComponents != sm.getNumBands()) {
            return false;
        }

        if (sm.getTransferType() != transferType) {
            return false;
        }

        return true;
    }

    /**
     * Returns a <CODE>Raster</CODE> representing the alpha channel of an image,
     * extracted from the input <CODE>Raster</CODE>.
     * This method assumes that <CODE>Raster</CODE> objects associated with
     * this <CODE>ColorModel</CODE> store the alpha band, if present, as
     * the last band of image data. Returns null if there is no separate spatial
     * alpha channel associated with this <CODE>ColorModel</CODE>.
     * This method creates a new <CODE>Raster</CODE>, but will share the data
     * array.
     *
     * @param raster The <CODE>WritableRaster</CODE> from which to extract the
     * alpha  channel.
     *
     * @return A <CODE>WritableRaster</CODE> containing the image's alpha channel.
     *
     */
    public WritableRaster getAlphaRaster(WritableRaster raster) {
        if (hasAlpha() == false) {
            return null;
        }

        int x = raster.getMinX();
        int y = raster.getMinY();
        int[] band = new int[1];
        band[0] = raster.getNumBands() - 1;
        return raster.createWritableChild(x, y, raster.getWidth(),
                                          raster.getHeight(), x, y,
                                          band);
    }

    /**
     * Compares this color model with another for equality.
     *
     * @param obj The object to compare with this color model.
     * @return <CODE>true</CODE> if the color model objects are equal,
     * <CODE>false</CODE> if they are not.
     */
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }

        if (obj.getClass() !=  getClass()) {
            return false;
        }

        return true;
    }

}
