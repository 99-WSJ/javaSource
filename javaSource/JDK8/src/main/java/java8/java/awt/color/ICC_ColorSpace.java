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

/*
 **********************************************************************
 **********************************************************************
 **********************************************************************
 *** COPYRIGHT (c) Eastman Kodak Company, 1997                      ***
 *** As  an unpublished  work pursuant to Title 17 of the United    ***
 *** States Code.  All rights reserved.                             ***
 **********************************************************************
 **********************************************************************
 **********************************************************************/

package java8.java.awt.color;

import sun.java2d.cmm.CMSManager;
import sun.java2d.cmm.ColorTransform;
import sun.java2d.cmm.PCMM;

import java.awt.color.CMMException;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_Profile;


/**
 *
 * The ICC_ColorSpace class is an implementation of the abstract
 * ColorSpace class.  This representation of
 * device independent and device dependent color spaces is based on the
 * International Color Consortium Specification ICC.1:2001-12, File Format for
 * Color Profiles (see <A href="http://www.color.org">http://www.color.org</A>).
 * <p>
 * Typically, a Color or ColorModel would be associated with an ICC
 * Profile which is either an input, display, or output profile (see
 * the ICC specification).  There are other types of ICC Profiles, e.g.
 * abstract profiles, device link profiles, and named color profiles,
 * which do not contain information appropriate for representing the color
 * space of a color, image, or device (see ICC_Profile).
 * Attempting to create an ICC_ColorSpace object from an inappropriate ICC
 * Profile is an error.
 * <p>
 * ICC Profiles represent transformations from the color space of
 * the profile (e.g. a monitor) to a Profile Connection Space (PCS).
 * Profiles of interest for tagging images or colors have a
 * PCS which is one of the device independent
 * spaces (one CIEXYZ space and two CIELab spaces) defined in the
 * ICC Profile Format Specification.  Most profiles of interest
 * either have invertible transformations or explicitly specify
 * transformations going both directions.  Should an ICC_ColorSpace
 * object be used in a way requiring a conversion from PCS to
 * the profile's native space and there is inadequate data to
 * correctly perform the conversion, the ICC_ColorSpace object will
 * produce output in the specified type of color space (e.g. TYPE_RGB,
 * TYPE_CMYK, etc.), but the specific color values of the output data
 * will be undefined.
 * <p>
 * The details of this class are not important for simple applets,
 * which draw in a default color space or manipulate and display
 * imported images with a known color space.  At most, such applets
 * would need to get one of the default color spaces via
 * ColorSpace.getInstance().
 * @see ColorSpace
 * @see ICC_Profile
 */



public class ICC_ColorSpace extends ColorSpace {

    static final long serialVersionUID = 3455889114070431483L;

    private ICC_Profile    thisProfile;
    private float[] minVal;
    private float[] maxVal;
    private float[] diffMinMax;
    private float[] invDiffMinMax;
    private boolean needScaleInit = true;

    // {to,from}{RGB,CIEXYZ} methods create and cache these when needed
    private transient ColorTransform this2srgb;
    private transient ColorTransform srgb2this;
    private transient ColorTransform this2xyz;
    private transient ColorTransform xyz2this;


    /**
    * Constructs a new ICC_ColorSpace from an ICC_Profile object.
    * @param profile the specified ICC_Profile object
    * @exception IllegalArgumentException if profile is inappropriate for
    *            representing a ColorSpace.
    */
    public ICC_ColorSpace (ICC_Profile profile) {
        super (profile.getColorSpaceType(), profile.getNumComponents());

        int profileClass = profile.getProfileClass();

        /* REMIND - is NAMEDCOLOR OK? */
        if ((profileClass != ICC_Profile.CLASS_INPUT) &&
            (profileClass != ICC_Profile.CLASS_DISPLAY) &&
            (profileClass != ICC_Profile.CLASS_OUTPUT) &&
            (profileClass != ICC_Profile.CLASS_COLORSPACECONVERSION) &&
            (profileClass != ICC_Profile.CLASS_NAMEDCOLOR) &&
            (profileClass != ICC_Profile.CLASS_ABSTRACT)) {
            throw new IllegalArgumentException("Invalid profile type");
        }

        thisProfile = profile;
        setMinMax();
    }

    /**
    * Returns the ICC_Profile for this ICC_ColorSpace.
    * @return the ICC_Profile for this ICC_ColorSpace.
    */
    public ICC_Profile getProfile() {
        return thisProfile;
    }

    /**
     * Transforms a color value assumed to be in this ColorSpace
     * into a value in the default CS_sRGB color space.
     * <p>
     * This method transforms color values using algorithms designed
     * to produce the best perceptual match between input and output
     * colors.  In order to do colorimetric conversion of color values,
     * you should use the <code>toCIEXYZ</code>
     * method of this color space to first convert from the input
     * color space to the CS_CIEXYZ color space, and then use the
     * <code>fromCIEXYZ</code> method of the CS_sRGB color space to
     * convert from CS_CIEXYZ to the output color space.
     * See {@link #toCIEXYZ(float[]) toCIEXYZ} and
     * {@link #fromCIEXYZ(float[]) fromCIEXYZ} for further information.
     * <p>
     * @param colorvalue a float array with length of at least the number
     *      of components in this ColorSpace.
     * @return a float array of length 3.
     * @throws ArrayIndexOutOfBoundsException if array length is not
     * at least the number of components in this ColorSpace.
     */
    public float[]    toRGB (float[] colorvalue) {

        if (this2srgb == null) {
            ColorTransform[] transformList = new ColorTransform [2];
            java.awt.color.ICC_ColorSpace srgbCS =
                (java.awt.color.ICC_ColorSpace) ColorSpace.getInstance (CS_sRGB);
            PCMM mdl = CMSManager.getModule();
            transformList[0] = mdl.createTransform(
                thisProfile, ColorTransform.Any, ColorTransform.In);
            transformList[1] = mdl.createTransform(
                srgbCS.getProfile(), ColorTransform.Any, ColorTransform.Out);
            this2srgb = mdl.createTransform(transformList);
            if (needScaleInit) {
                setComponentScaling();
            }
        }

        int nc = this.getNumComponents();
        short tmp[] = new short[nc];
        for (int i = 0; i < nc; i++) {
            tmp[i] = (short)
                ((colorvalue[i] - minVal[i]) * invDiffMinMax[i] + 0.5f);
        }
        tmp = this2srgb.colorConvert(tmp, null);
        float[] result = new float [3];
        for (int i = 0; i < 3; i++) {
            result[i] = ((float) (tmp[i] & 0xffff)) / 65535.0f;
        }
        return result;
    }

    /**
     * Transforms a color value assumed to be in the default CS_sRGB
     * color space into this ColorSpace.
     * <p>
     * This method transforms color values using algorithms designed
     * to produce the best perceptual match between input and output
     * colors.  In order to do colorimetric conversion of color values,
     * you should use the <code>toCIEXYZ</code>
     * method of the CS_sRGB color space to first convert from the input
     * color space to the CS_CIEXYZ color space, and then use the
     * <code>fromCIEXYZ</code> method of this color space to
     * convert from CS_CIEXYZ to the output color space.
     * See {@link #toCIEXYZ(float[]) toCIEXYZ} and
     * {@link #fromCIEXYZ(float[]) fromCIEXYZ} for further information.
     * <p>
     * @param rgbvalue a float array with length of at least 3.
     * @return a float array with length equal to the number of
     *       components in this ColorSpace.
     * @throws ArrayIndexOutOfBoundsException if array length is not
     * at least 3.
     */
    public float[]    fromRGB(float[] rgbvalue) {

        if (srgb2this == null) {
            ColorTransform[] transformList = new ColorTransform [2];
            java.awt.color.ICC_ColorSpace srgbCS =
                (java.awt.color.ICC_ColorSpace) ColorSpace.getInstance (CS_sRGB);
            PCMM mdl = CMSManager.getModule();
            transformList[0] = mdl.createTransform(
                srgbCS.getProfile(), ColorTransform.Any, ColorTransform.In);
            transformList[1] = mdl.createTransform(
                thisProfile, ColorTransform.Any, ColorTransform.Out);
            srgb2this = mdl.createTransform(transformList);
            if (needScaleInit) {
                setComponentScaling();
            }
        }

        short tmp[] = new short[3];
        for (int i = 0; i < 3; i++) {
            tmp[i] = (short) ((rgbvalue[i] * 65535.0f) + 0.5f);
        }
        tmp = srgb2this.colorConvert(tmp, null);
        int nc = this.getNumComponents();
        float[] result = new float [nc];
        for (int i = 0; i < nc; i++) {
            result[i] = (((float) (tmp[i] & 0xffff)) / 65535.0f) *
                        diffMinMax[i] + minVal[i];
        }
        return result;
    }


    /**
     * Transforms a color value assumed to be in this ColorSpace
     * into the CS_CIEXYZ conversion color space.
     * <p>
     * This method transforms color values using relative colorimetry,
     * as defined by the ICC Specification.  This
     * means that the XYZ values returned by this method are represented
     * relative to the D50 white point of the CS_CIEXYZ color space.
     * This representation is useful in a two-step color conversion
     * process in which colors are transformed from an input color
     * space to CS_CIEXYZ and then to an output color space.  This
     * representation is not the same as the XYZ values that would
     * be measured from the given color value by a colorimeter.
     * A further transformation is necessary to compute the XYZ values
     * that would be measured using current CIE recommended practices.
     * The paragraphs below explain this in more detail.
     * <p>
     * The ICC standard uses a device independent color space (DICS) as the
     * mechanism for converting color from one device to another device.  In
     * this architecture, colors are converted from the source device's color
     * space to the ICC DICS and then from the ICC DICS to the destination
     * device's color space.  The ICC standard defines device profiles which
     * contain transforms which will convert between a device's color space
     * and the ICC DICS.  The overall conversion of colors from a source
     * device to colors of a destination device is done by connecting the
     * device-to-DICS transform of the profile for the source device to the
     * DICS-to-device transform of the profile for the destination device.
     * For this reason, the ICC DICS is commonly referred to as the profile
     * connection space (PCS).  The color space used in the methods
     * toCIEXYZ and fromCIEXYZ is the CIEXYZ PCS defined by the ICC
     * Specification.  This is also the color space represented by
     * ColorSpace.CS_CIEXYZ.
     * <p>
     * The XYZ values of a color are often represented as relative to some
     * white point, so the actual meaning of the XYZ values cannot be known
     * without knowing the white point of those values.  This is known as
     * relative colorimetry.  The PCS uses a white point of D50, so the XYZ
     * values of the PCS are relative to D50.  For example, white in the PCS
     * will have the XYZ values of D50, which is defined to be X=.9642,
     * Y=1.000, and Z=0.8249.  This white point is commonly used for graphic
     * arts applications, but others are often used in other applications.
     * <p>
     * To quantify the color characteristics of a device such as a printer
     * or monitor, measurements of XYZ values for particular device colors
     * are typically made.  For purposes of this discussion, the term
     * device XYZ values is used to mean the XYZ values that would be
     * measured from device colors using current CIE recommended practices.
     * <p>
     * Converting between device XYZ values and the PCS XYZ values returned
     * by this method corresponds to converting between the device's color
     * space, as represented by CIE colorimetric values, and the PCS.  There
     * are many factors involved in this process, some of which are quite
     * subtle.  The most important, however, is the adjustment made to account
     * for differences between the device's white point and the white point of
     * the PCS.  There are many techniques for doing this and it is the
     * subject of much current research and controversy.  Some commonly used
     * methods are XYZ scaling, the von Kries transform, and the Bradford
     * transform.  The proper method to use depends upon each particular
     * application.
     * <p>
     * The simplest method is XYZ scaling.  In this method each device XYZ
     * value is  converted to a PCS XYZ value by multiplying it by the ratio
     * of the PCS white point (D50) to the device white point.
     * <pre>
     *
     * Xd, Yd, Zd are the device XYZ values
     * Xdw, Ydw, Zdw are the device XYZ white point values
     * Xp, Yp, Zp are the PCS XYZ values
     * Xd50, Yd50, Zd50 are the PCS XYZ white point values
     *
     * Xp = Xd * (Xd50 / Xdw)
     * Yp = Yd * (Yd50 / Ydw)
     * Zp = Zd * (Zd50 / Zdw)
     *
     * </pre>
     * <p>
     * Conversion from the PCS to the device would be done by inverting these
     * equations:
     * <pre>
     *
     * Xd = Xp * (Xdw / Xd50)
     * Yd = Yp * (Ydw / Yd50)
     * Zd = Zp * (Zdw / Zd50)
     *
     * </pre>
     * <p>
     * Note that the media white point tag in an ICC profile is not the same
     * as the device white point.  The media white point tag is expressed in
     * PCS values and is used to represent the difference between the XYZ of
     * device illuminant and the XYZ of the device media when measured under
     * that illuminant.  The device white point is expressed as the device
     * XYZ values corresponding to white displayed on the device.  For
     * example, displaying the RGB color (1.0, 1.0, 1.0) on an sRGB device
     * will result in a measured device XYZ value of D65.  This will not
     * be the same as the media white point tag XYZ value in the ICC
     * profile for an sRGB device.
     * <p>
     * @param colorvalue a float array with length of at least the number
     *        of components in this ColorSpace.
     * @return a float array of length 3.
     * @throws ArrayIndexOutOfBoundsException if array length is not
     * at least the number of components in this ColorSpace.
     */
    public float[]    toCIEXYZ(float[] colorvalue) {

        if (this2xyz == null) {
            ColorTransform[] transformList = new ColorTransform [2];
            java.awt.color.ICC_ColorSpace xyzCS =
                (java.awt.color.ICC_ColorSpace) ColorSpace.getInstance (CS_CIEXYZ);
            PCMM mdl = CMSManager.getModule();
            try {
                transformList[0] = mdl.createTransform(
                    thisProfile, ICC_Profile.icRelativeColorimetric,
                    ColorTransform.In);
            } catch (CMMException e) {
                transformList[0] = mdl.createTransform(
                    thisProfile, ColorTransform.Any, ColorTransform.In);
            }
            transformList[1] = mdl.createTransform(
                xyzCS.getProfile(), ColorTransform.Any, ColorTransform.Out);
            this2xyz = mdl.createTransform (transformList);
            if (needScaleInit) {
                setComponentScaling();
            }
        }

        int nc = this.getNumComponents();
        short tmp[] = new short[nc];
        for (int i = 0; i < nc; i++) {
            tmp[i] = (short)
                ((colorvalue[i] - minVal[i]) * invDiffMinMax[i] + 0.5f);
        }
        tmp = this2xyz.colorConvert(tmp, null);
        float ALMOST_TWO = 1.0f + (32767.0f / 32768.0f);
        // For CIEXYZ, min = 0.0, max = ALMOST_TWO for all components
        float[] result = new float [3];
        for (int i = 0; i < 3; i++) {
            result[i] = (((float) (tmp[i] & 0xffff)) / 65535.0f) * ALMOST_TWO;
        }
        return result;
    }


    /**
     * Transforms a color value assumed to be in the CS_CIEXYZ conversion
     * color space into this ColorSpace.
     * <p>
     * This method transforms color values using relative colorimetry,
     * as defined by the ICC Specification.  This
     * means that the XYZ argument values taken by this method are represented
     * relative to the D50 white point of the CS_CIEXYZ color space.
     * This representation is useful in a two-step color conversion
     * process in which colors are transformed from an input color
     * space to CS_CIEXYZ and then to an output color space.  The color
     * values returned by this method are not those that would produce
     * the XYZ value passed to the method when measured by a colorimeter.
     * If you have XYZ values corresponding to measurements made using
     * current CIE recommended practices, they must be converted to D50
     * relative values before being passed to this method.
     * The paragraphs below explain this in more detail.
     * <p>
     * The ICC standard uses a device independent color space (DICS) as the
     * mechanism for converting color from one device to another device.  In
     * this architecture, colors are converted from the source device's color
     * space to the ICC DICS and then from the ICC DICS to the destination
     * device's color space.  The ICC standard defines device profiles which
     * contain transforms which will convert between a device's color space
     * and the ICC DICS.  The overall conversion of colors from a source
     * device to colors of a destination device is done by connecting the
     * device-to-DICS transform of the profile for the source device to the
     * DICS-to-device transform of the profile for the destination device.
     * For this reason, the ICC DICS is commonly referred to as the profile
     * connection space (PCS).  The color space used in the methods
     * toCIEXYZ and fromCIEXYZ is the CIEXYZ PCS defined by the ICC
     * Specification.  This is also the color space represented by
     * ColorSpace.CS_CIEXYZ.
     * <p>
     * The XYZ values of a color are often represented as relative to some
     * white point, so the actual meaning of the XYZ values cannot be known
     * without knowing the white point of those values.  This is known as
     * relative colorimetry.  The PCS uses a white point of D50, so the XYZ
     * values of the PCS are relative to D50.  For example, white in the PCS
     * will have the XYZ values of D50, which is defined to be X=.9642,
     * Y=1.000, and Z=0.8249.  This white point is commonly used for graphic
     * arts applications, but others are often used in other applications.
     * <p>
     * To quantify the color characteristics of a device such as a printer
     * or monitor, measurements of XYZ values for particular device colors
     * are typically made.  For purposes of this discussion, the term
     * device XYZ values is used to mean the XYZ values that would be
     * measured from device colors using current CIE recommended practices.
     * <p>
     * Converting between device XYZ values and the PCS XYZ values taken as
     * arguments by this method corresponds to converting between the device's
     * color space, as represented by CIE colorimetric values, and the PCS.
     * There are many factors involved in this process, some of which are quite
     * subtle.  The most important, however, is the adjustment made to account
     * for differences between the device's white point and the white point of
     * the PCS.  There are many techniques for doing this and it is the
     * subject of much current research and controversy.  Some commonly used
     * methods are XYZ scaling, the von Kries transform, and the Bradford
     * transform.  The proper method to use depends upon each particular
     * application.
     * <p>
     * The simplest method is XYZ scaling.  In this method each device XYZ
     * value is  converted to a PCS XYZ value by multiplying it by the ratio
     * of the PCS white point (D50) to the device white point.
     * <pre>
     *
     * Xd, Yd, Zd are the device XYZ values
     * Xdw, Ydw, Zdw are the device XYZ white point values
     * Xp, Yp, Zp are the PCS XYZ values
     * Xd50, Yd50, Zd50 are the PCS XYZ white point values
     *
     * Xp = Xd * (Xd50 / Xdw)
     * Yp = Yd * (Yd50 / Ydw)
     * Zp = Zd * (Zd50 / Zdw)
     *
     * </pre>
     * <p>
     * Conversion from the PCS to the device would be done by inverting these
     * equations:
     * <pre>
     *
     * Xd = Xp * (Xdw / Xd50)
     * Yd = Yp * (Ydw / Yd50)
     * Zd = Zp * (Zdw / Zd50)
     *
     * </pre>
     * <p>
     * Note that the media white point tag in an ICC profile is not the same
     * as the device white point.  The media white point tag is expressed in
     * PCS values and is used to represent the difference between the XYZ of
     * device illuminant and the XYZ of the device media when measured under
     * that illuminant.  The device white point is expressed as the device
     * XYZ values corresponding to white displayed on the device.  For
     * example, displaying the RGB color (1.0, 1.0, 1.0) on an sRGB device
     * will result in a measured device XYZ value of D65.  This will not
     * be the same as the media white point tag XYZ value in the ICC
     * profile for an sRGB device.
     * <p>
     * @param colorvalue a float array with length of at least 3.
     * @return a float array with length equal to the number of
     *         components in this ColorSpace.
     * @throws ArrayIndexOutOfBoundsException if array length is not
     * at least 3.
     */
    public float[]    fromCIEXYZ(float[] colorvalue) {

        if (xyz2this == null) {
            ColorTransform[] transformList = new ColorTransform [2];
            java.awt.color.ICC_ColorSpace xyzCS =
                (java.awt.color.ICC_ColorSpace) ColorSpace.getInstance (CS_CIEXYZ);
            PCMM mdl = CMSManager.getModule();
            transformList[0] = mdl.createTransform (
                xyzCS.getProfile(), ColorTransform.Any, ColorTransform.In);
            try {
                transformList[1] = mdl.createTransform(
                    thisProfile, ICC_Profile.icRelativeColorimetric,
                    ColorTransform.Out);
            } catch (CMMException e) {
                transformList[1] = CMSManager.getModule().createTransform(
                thisProfile, ColorTransform.Any, ColorTransform.Out);
            }
            xyz2this = mdl.createTransform(transformList);
            if (needScaleInit) {
                setComponentScaling();
            }
        }

        short tmp[] = new short[3];
        float ALMOST_TWO = 1.0f + (32767.0f / 32768.0f);
        float factor = 65535.0f / ALMOST_TWO;
        // For CIEXYZ, min = 0.0, max = ALMOST_TWO for all components
        for (int i = 0; i < 3; i++) {
            tmp[i] = (short) ((colorvalue[i] * factor) + 0.5f);
        }
        tmp = xyz2this.colorConvert(tmp, null);
        int nc = this.getNumComponents();
        float[] result = new float [nc];
        for (int i = 0; i < nc; i++) {
            result[i] = (((float) (tmp[i] & 0xffff)) / 65535.0f) *
                        diffMinMax[i] + minVal[i];
        }
        return result;
    }

    /**
     * Returns the minimum normalized color component value for the
     * specified component.  For TYPE_XYZ spaces, this method returns
     * minimum values of 0.0 for all components.  For TYPE_Lab spaces,
     * this method returns 0.0 for L and -128.0 for a and b components.
     * This is consistent with the encoding of the XYZ and Lab Profile
     * Connection Spaces in the ICC specification.  For all other types, this
     * method returns 0.0 for all components.  When using an ICC_ColorSpace
     * with a profile that requires different minimum component values,
     * it is necessary to subclass this class and override this method.
     * @param component The component index.
     * @return The minimum normalized component value.
     * @throws IllegalArgumentException if component is less than 0 or
     *         greater than numComponents - 1.
     * @since 1.4
     */
    public float getMinValue(int component) {
        if ((component < 0) || (component > this.getNumComponents() - 1)) {
            throw new IllegalArgumentException(
                "Component index out of range: + component");
        }
        return minVal[component];
    }

    /**
     * Returns the maximum normalized color component value for the
     * specified component.  For TYPE_XYZ spaces, this method returns
     * maximum values of 1.0 + (32767.0 / 32768.0) for all components.
     * For TYPE_Lab spaces,
     * this method returns 100.0 for L and 127.0 for a and b components.
     * This is consistent with the encoding of the XYZ and Lab Profile
     * Connection Spaces in the ICC specification.  For all other types, this
     * method returns 1.0 for all components.  When using an ICC_ColorSpace
     * with a profile that requires different maximum component values,
     * it is necessary to subclass this class and override this method.
     * @param component The component index.
     * @return The maximum normalized component value.
     * @throws IllegalArgumentException if component is less than 0 or
     *         greater than numComponents - 1.
     * @since 1.4
     */
    public float getMaxValue(int component) {
        if ((component < 0) || (component > this.getNumComponents() - 1)) {
            throw new IllegalArgumentException(
                "Component index out of range: + component");
        }
        return maxVal[component];
    }

    private void setMinMax() {
        int nc = this.getNumComponents();
        int type = this.getType();
        minVal = new float[nc];
        maxVal = new float[nc];
        if (type == ColorSpace.TYPE_Lab) {
            minVal[0] = 0.0f;    // L
            maxVal[0] = 100.0f;
            minVal[1] = -128.0f; // a
            maxVal[1] = 127.0f;
            minVal[2] = -128.0f; // b
            maxVal[2] = 127.0f;
        } else if (type == ColorSpace.TYPE_XYZ) {
            minVal[0] = minVal[1] = minVal[2] = 0.0f; // X, Y, Z
            maxVal[0] = maxVal[1] = maxVal[2] = 1.0f + (32767.0f/ 32768.0f);
        } else {
            for (int i = 0; i < nc; i++) {
                minVal[i] = 0.0f;
                maxVal[i] = 1.0f;
            }
        }
    }

    private void setComponentScaling() {
        int nc = this.getNumComponents();
        diffMinMax = new float[nc];
        invDiffMinMax = new float[nc];
        for (int i = 0; i < nc; i++) {
            minVal[i] = this.getMinValue(i); // in case getMinVal is overridden
            maxVal[i] = this.getMaxValue(i); // in case getMaxVal is overridden
            diffMinMax[i] = maxVal[i] - minVal[i];
            invDiffMinMax[i] = 65535.0f / diffMinMax[i];
        }
        needScaleInit = false;
    }

}
