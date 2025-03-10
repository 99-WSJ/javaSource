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

import sun.java2d.cmm.Profile;
import sun.java2d.cmm.ProfileDeferralInfo;

import java.awt.color.ICC_Profile;
import java.awt.color.ProfileDataException;

/**
 *
 * The ICC_ProfileRGB class is a subclass of the ICC_Profile class
 * that represents profiles which meet the following criteria:
 * <ul>
 * <li>The profile's color space type is RGB.</li>
 * <li>The profile includes the <code>redColorantTag</code>,
 * <code>greenColorantTag</code>, <code>blueColorantTag</code>,
 * <code>redTRCTag</code>, <code>greenTRCTag</code>,
 * <code>blueTRCTag</code>, and <code>mediaWhitePointTag</code> tags.</li>
 * </ul>
 * The <code>ICC_Profile</code> <code>getInstance</code> method will
 * return an <code>ICC_ProfileRGB</code> object when these conditions are met.
 * Three-component, matrix-based input profiles and RGB display profiles are
 * examples of this type of profile.
 * <p>
 * This profile class provides color transform matrices and lookup tables
 * that Java or native methods can use directly to
 * optimize color conversion in some cases.
 * <p>
 * To transform from a device profile color space to the CIEXYZ Profile
 * Connection Space, each device color component is first linearized by
 * a lookup through the corresponding tone reproduction curve (TRC).
 * The resulting linear RGB components are converted to the CIEXYZ PCS
 * using a a 3x3 matrix constructed from the RGB colorants.
 * <pre>
 *
 * &nbsp;               linearR = redTRC[deviceR]
 *
 * &nbsp;               linearG = greenTRC[deviceG]
 *
 * &nbsp;               linearB = blueTRC[deviceB]
 *
 * &nbsp; _      _       _                                             _   _         _
 * &nbsp;[  PCSX  ]     [  redColorantX  greenColorantX  blueColorantX  ] [  linearR  ]
 * &nbsp;[        ]     [                                               ] [           ]
 * &nbsp;[  PCSY  ]  =  [  redColorantY  greenColorantY  blueColorantY  ] [  linearG  ]
 * &nbsp;[        ]     [                                               ] [           ]
 * &nbsp;[_ PCSZ _]     [_ redColorantZ  greenColorantZ  blueColorantZ _] [_ linearB _]
 *
 * </pre>
 * The inverse transform is performed by converting PCS XYZ components to linear
 * RGB components through the inverse of the above 3x3 matrix, and then converting
 * linear RGB to device RGB through inverses of the TRCs.
 */



public class ICC_ProfileRGB
extends ICC_Profile {

    static final long serialVersionUID = 8505067385152579334L;

    /**
     * Used to get a gamma value or TRC for the red component.
     */
    public static final int REDCOMPONENT = 0;

    /**
     * Used to get a gamma value or TRC for the green component.
     */
    public static final int GREENCOMPONENT = 1;

    /**
     * Used to get a gamma value or TRC for the blue component.
     */
    public static final int BLUECOMPONENT = 2;


    /**
     * Constructs an new <code>ICC_ProfileRGB</code> from a CMM ID.
     *
     * @param p The CMM ID for the profile.
     *
     */
    ICC_ProfileRGB(Profile p) {
        super(p);
    }

    /**
     * Constructs a new <code>ICC_ProfileRGB</code> from a
     * ProfileDeferralInfo object.
     *
     * @param pdi
     */
    ICC_ProfileRGB(ProfileDeferralInfo pdi) {
        super(pdi);
    }


    /**
     * Returns an array that contains the components of the profile's
     * <CODE>mediaWhitePointTag</CODE>.
     *
     * @return A 3-element <CODE>float</CODE> array containing the x, y,
     * and z components of the profile's <CODE>mediaWhitePointTag</CODE>.
     */
    public float[] getMediaWhitePoint() {
        return super.getMediaWhitePoint();
    }


    /**
     * Returns a 3x3 <CODE>float</CODE> matrix constructed from the
     * X, Y, and Z components of the profile's <CODE>redColorantTag</CODE>,
     * <CODE>greenColorantTag</CODE>, and <CODE>blueColorantTag</CODE>.
     * <p>
     * This matrix can be used for color transforms in the forward
     * direction of the profile--from the profile color space
     * to the CIEXYZ PCS.
     *
     * @return A 3x3 <CODE>float</CODE> array that contains the x, y, and z
     * components of the profile's <CODE>redColorantTag</CODE>,
     * <CODE>greenColorantTag</CODE>, and <CODE>blueColorantTag</CODE>.
     */
    public float[][] getMatrix() {
        float[][] theMatrix = new float[3][3];
        float[] tmpMatrix;

        tmpMatrix = getXYZTag(ICC_Profile.icSigRedColorantTag);
        theMatrix[0][0] = tmpMatrix[0];
        theMatrix[1][0] = tmpMatrix[1];
        theMatrix[2][0] = tmpMatrix[2];
        tmpMatrix = getXYZTag(ICC_Profile.icSigGreenColorantTag);
        theMatrix[0][1] = tmpMatrix[0];
        theMatrix[1][1] = tmpMatrix[1];
        theMatrix[2][1] = tmpMatrix[2];
        tmpMatrix = getXYZTag(ICC_Profile.icSigBlueColorantTag);
        theMatrix[0][2] = tmpMatrix[0];
        theMatrix[1][2] = tmpMatrix[1];
        theMatrix[2][2] = tmpMatrix[2];
        return theMatrix;
    }

    /**
     * Returns a gamma value representing the tone reproduction curve
     * (TRC) for a particular component.  The component parameter
     * must be one of REDCOMPONENT, GREENCOMPONENT, or BLUECOMPONENT.
     * <p>
     * If the profile
     * represents the TRC for the corresponding component
     * as a table rather than a single gamma value, an
     * exception is thrown.  In this case the actual table
     * can be obtained through the {@link #getTRC(int)} method.
     * When using a gamma value,
     * the linear component (R, G, or B) is computed as follows:
     * <pre>
     *
     * &nbsp;                                         gamma
     * &nbsp;        linearComponent = deviceComponent
     *
     *</pre>
     * @param component The <CODE>ICC_ProfileRGB</CODE> constant that
     * represents the component whose TRC you want to retrieve
     * @return the gamma value as a float.
     * @exception ProfileDataException if the profile does not specify
     *            the corresponding TRC as a single gamma value.
     */
    public float getGamma(int component) {
    float theGamma;
    int theSignature;

        switch (component) {
        case REDCOMPONENT:
            theSignature = ICC_Profile.icSigRedTRCTag;
            break;

        case GREENCOMPONENT:
            theSignature = ICC_Profile.icSigGreenTRCTag;
            break;

        case BLUECOMPONENT:
            theSignature = ICC_Profile.icSigBlueTRCTag;
            break;

        default:
            throw new IllegalArgumentException("Must be Red, Green, or Blue");
        }

        theGamma = super.getGamma(theSignature);

        return theGamma;
    }

    /**
     * Returns the TRC for a particular component as an array.
     * Component must be <code>REDCOMPONENT</code>,
     * <code>GREENCOMPONENT</code>, or <code>BLUECOMPONENT</code>.
     * Otherwise the returned array
     * represents a lookup table where the input component value
     * is conceptually in the range [0.0, 1.0].  Value 0.0 maps
     * to array index 0 and value 1.0 maps to array index length-1.
     * Interpolation might be used to generate output values for
     * input values that do not map exactly to an index in the
     * array.  Output values also map linearly to the range [0.0, 1.0].
     * Value 0.0 is represented by an array value of 0x0000 and
     * value 1.0 by 0xFFFF.  In other words, the values are really unsigned
     * <code>short</code> values even though they are returned in a
     * <code>short</code> array.
     *
     * If the profile has specified the corresponding TRC
     * as linear (gamma = 1.0) or as a simple gamma value, this method
     * throws an exception.  In this case, the {@link #getGamma(int)}
     * method should be used to get the gamma value.
     *
     * @param component The <CODE>ICC_ProfileRGB</CODE> constant that
     * represents the component whose TRC you want to retrieve:
     * <CODE>REDCOMPONENT</CODE>, <CODE>GREENCOMPONENT</CODE>, or
     * <CODE>BLUECOMPONENT</CODE>.
     *
     * @return a short array representing the TRC.
     * @exception ProfileDataException if the profile does not specify
     *            the corresponding TRC as a table.
     */
    public short[] getTRC(int component) {
    short[] theTRC;
    int theSignature;

        switch (component) {
        case REDCOMPONENT:
            theSignature = ICC_Profile.icSigRedTRCTag;
            break;

        case GREENCOMPONENT:
            theSignature = ICC_Profile.icSigGreenTRCTag;
            break;

        case BLUECOMPONENT:
            theSignature = ICC_Profile.icSigBlueTRCTag;
            break;

        default:
            throw new IllegalArgumentException("Must be Red, Green, or Blue");
        }

        theTRC = super.getTRC(theSignature);

        return theTRC;
    }

}
