/*
 * Copyright (c) 2000, 2013, Oracle and/or its affiliates. All rights reserved.
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
package java8.javax.print.attribute.standard;

import java.util.HashMap;
import java.util.Vector;

import javax.print.attribute.Size2DSyntax;
import javax.print.attribute.Attribute;
import javax.print.attribute.standard.MediaSizeName;

/**
 * Class MediaSize is a two-dimensional size valued printing attribute class
 * that indicates the dimensions of the medium in a portrait orientation, with
 * the X dimension running along the bottom edge and the Y dimension running
 * along the left edge. Thus, the Y dimension must be greater than or equal to
 * the X dimension. Class MediaSize declares many standard media size
 * values, organized into nested classes for ISO, JIS, North American,
 * engineering, and other media.
 * <P>
 * MediaSize is not yet used to specify media. Its current role is
 * as a mapping for named media (see {@link MediaSizeName MediaSizeName}).
 * Clients can use the mapping method
 * <code>MediaSize.getMediaSizeForName(MediaSizeName)</code>
 * to find the physical dimensions of the MediaSizeName instances
 * enumerated in this API. This is useful for clients which need this
 * information to format {@literal &} paginate printing.
 * <P>
 *
 * @author  Phil Race, Alan Kaminsky
 */
public class MediaSize extends Size2DSyntax implements Attribute {

    private static final long serialVersionUID = -1967958664615414771L;

    private MediaSizeName mediaName;

    private static HashMap mediaMap = new HashMap(100, 10);

    private static Vector sizeVector = new Vector(100, 10);

    /**
     * Construct a new media size attribute from the given floating-point
     * values.
     *
     * @param  x  X dimension.
     * @param  y  Y dimension.
     * @param  units
     *     Unit conversion factor, e.g. <CODE>Size2DSyntax.INCH</CODE> or
     *     <CODE>Size2DSyntax.MM</CODE>.
     *
     * @exception  IllegalArgumentException
     *   (Unchecked exception) Thrown if {@code x < 0} or {@code y < 0} or
     *   {@code units < 1} or {@code x > y}.
     */
    public MediaSize(float x, float y,int units) {
        super (x, y, units);
        if (x > y) {
            throw new IllegalArgumentException("X dimension > Y dimension");
        }
        sizeVector.add(this);
    }

    /**
     * Construct a new media size attribute from the given integer values.
     *
     * @param  x  X dimension.
     * @param  y  Y dimension.
     * @param  units
     *     Unit conversion factor, e.g. <CODE>Size2DSyntax.INCH</CODE> or
     *     <CODE>Size2DSyntax.MM</CODE>.
     *
     * @exception  IllegalArgumentException
     *   (Unchecked exception) Thrown if {@code x < 0} or {@code y < 0} or
     *   {@code units < 1} or {@code x > y}.
     */
    public MediaSize(int x, int y,int units) {
        super (x, y, units);
        if (x > y) {
            throw new IllegalArgumentException("X dimension > Y dimension");
        }
        sizeVector.add(this);
    }

   /**
     * Construct a new media size attribute from the given floating-point
     * values.
     *
     * @param  x  X dimension.
     * @param  y  Y dimension.
     * @param  units
     *     Unit conversion factor, e.g. <CODE>Size2DSyntax.INCH</CODE> or
     *     <CODE>Size2DSyntax.MM</CODE>.
     * @param media a media name to associate with this MediaSize
     *
     * @exception  IllegalArgumentException
     *   (Unchecked exception) Thrown if {@code x < 0} or {@code y < 0} or
     *   {@code units < 1} or {@code x > y}.
     */
    public MediaSize(float x, float y,int units, MediaSizeName media) {
        super (x, y, units);
        if (x > y) {
            throw new IllegalArgumentException("X dimension > Y dimension");
        }
        if (media != null && mediaMap.get(media) == null) {
            mediaName = media;
            mediaMap.put(mediaName, this);
        }
        sizeVector.add(this);
    }

    /**
     * Construct a new media size attribute from the given integer values.
     *
     * @param  x  X dimension.
     * @param  y  Y dimension.
     * @param  units
     *     Unit conversion factor, e.g. <CODE>Size2DSyntax.INCH</CODE> or
     *     <CODE>Size2DSyntax.MM</CODE>.
     * @param media a media name to associate with this MediaSize
     *
     * @exception  IllegalArgumentException
     *   (Unchecked exception) Thrown if {@code x < 0} or {@code y < 0} or
     *   {@code units < 1} or {@code x > y}.
     */
    public MediaSize(int x, int y,int units, MediaSizeName media) {
        super (x, y, units);
        if (x > y) {
            throw new IllegalArgumentException("X dimension > Y dimension");
        }
        if (media != null && mediaMap.get(media) == null) {
            mediaName = media;
            mediaMap.put(mediaName, this);
        }
        sizeVector.add(this);
    }

    /**
     * Get the media name, if any, for this size.
     *
     * @return the name for this media size, or null if no name was
     * associated with this size (an anonymous size).
     */
    public MediaSizeName getMediaSizeName() {
        return mediaName;
    }

    /**
     * Get the MediaSize for the specified named media.
     *
     * @param media - the name of the media for which the size is sought
     * @return size of the media, or null if this media is not associated
     * with any size.
     */
    public static javax.print.attribute.standard.MediaSize getMediaSizeForName(MediaSizeName media) {
        return (javax.print.attribute.standard.MediaSize)mediaMap.get(media);
    }

    /**
     * The specified dimensions are used to locate a matching MediaSize
     * instance from amongst all the standard MediaSize instances.
     * If there is no exact match, the closest match is used.
     * <p>
     * The MediaSize is in turn used to locate the MediaSizeName object.
     * This method may return null if the closest matching MediaSize
     * has no corresponding Media instance.
     * <p>
     * This method is useful for clients which have only dimensions and
     * want to find a Media which corresponds to the dimensions.
     * @param x - X dimension
     * @param y - Y dimension.
     * @param  units
     *     Unit conversion factor, e.g. <CODE>Size2DSyntax.INCH</CODE> or
     *     <CODE>Size2DSyntax.MM</CODE>
     * @return MediaSizeName matching these dimensions, or null.
     * @exception IllegalArgumentException if {@code x <= 0},
     *      {@code y <= 0}, or {@code units < 1}.
     *
     */
    public static MediaSizeName findMedia(float x, float y, int units) {

        javax.print.attribute.standard.MediaSize match = javax.print.attribute.standard.MediaSize.ISO.A4;

        if (x <= 0.0f || y <= 0.0f || units < 1) {
            throw new IllegalArgumentException("args must be +ve values");
        }

        double ls = x * x + y * y;
        double tmp_ls;
        float []dim;
        float diffx = x;
        float diffy = y;

        for (int i=0; i < sizeVector.size() ; i++) {
            javax.print.attribute.standard.MediaSize mediaSize = (javax.print.attribute.standard.MediaSize)sizeVector.elementAt(i);
            dim = mediaSize.getSize(units);
            if (x == dim[0] && y == dim[1]) {
                match = mediaSize;
                break;
            } else {
                diffx = x - dim[0];
                diffy = y - dim[1];
                tmp_ls = diffx * diffx + diffy * diffy;
                if (tmp_ls < ls) {
                    ls = tmp_ls;
                    match = mediaSize;
                }
            }
        }

        return match.getMediaSizeName();
    }

    /**
     * Returns whether this media size attribute is equivalent to the passed
     * in object.
     * To be equivalent, all of the following conditions must be true:
     * <OL TYPE=1>
     * <LI>
     * <CODE>object</CODE> is not null.
     * <LI>
     * <CODE>object</CODE> is an instance of class MediaSize.
     * <LI>
     * This media size attribute's X dimension is equal to
     * <CODE>object</CODE>'s X dimension.
     * <LI>
     * This media size attribute's Y dimension is equal to
     * <CODE>object</CODE>'s Y dimension.
     * </OL>
     *
     * @param  object  Object to compare to.
     *
     * @return  True if <CODE>object</CODE> is equivalent to this media size
     *          attribute, false otherwise.
     */
    public boolean equals(Object object) {
        return (super.equals(object) && object instanceof javax.print.attribute.standard.MediaSize);
    }

    /**
     * Get the printing attribute class which is to be used as the "category"
     * for this printing attribute value.
     * <P>
     * For class MediaSize and any vendor-defined subclasses, the category is
     * class MediaSize itself.
     *
     * @return  Printing attribute class (category), an instance of class
     *          {@link Class java.lang.Class}.
     */
    public final Class<? extends Attribute> getCategory() {
        return javax.print.attribute.standard.MediaSize.class;
    }

    /**
     * Get the name of the category of which this attribute value is an
     * instance.
     * <P>
     * For class MediaSize and any vendor-defined subclasses, the category
     * name is <CODE>"media-size"</CODE>.
     *
     * @return  Attribute category name.
     */
    public final String getName() {
        return "media-size";
    }

    /**
     * Class MediaSize.ISO includes {@link javax.print.attribute.standard.MediaSize MediaSize} values for ISO
     * media.
     * <P>
     */
    public final static class ISO {
        /**
         * Specifies the ISO A0 size, 841 mm by 1189 mm.
         */
        public static final javax.print.attribute.standard.MediaSize
            A0 = new javax.print.attribute.standard.MediaSize(841, 1189, Size2DSyntax.MM, MediaSizeName.ISO_A0);
        /**
         * Specifies the ISO A1 size, 594 mm by 841 mm.
         */
        public static final javax.print.attribute.standard.MediaSize
            A1 = new javax.print.attribute.standard.MediaSize(594, 841, Size2DSyntax.MM, MediaSizeName.ISO_A1);
        /**
         * Specifies the ISO A2 size, 420 mm by 594 mm.
         */
        public static final javax.print.attribute.standard.MediaSize
            A2 = new javax.print.attribute.standard.MediaSize(420, 594, Size2DSyntax.MM, MediaSizeName.ISO_A2);
        /**
         * Specifies the ISO A3 size, 297 mm by 420 mm.
         */
        public static final javax.print.attribute.standard.MediaSize
            A3 = new javax.print.attribute.standard.MediaSize(297, 420, Size2DSyntax.MM, MediaSizeName.ISO_A3);
        /**
         * Specifies the ISO A4 size, 210 mm by 297 mm.
         */
        public static final javax.print.attribute.standard.MediaSize
            A4 = new javax.print.attribute.standard.MediaSize(210, 297, Size2DSyntax.MM, MediaSizeName.ISO_A4);
        /**
         * Specifies the ISO A5 size, 148 mm by 210 mm.
         */
        public static final javax.print.attribute.standard.MediaSize
            A5 = new javax.print.attribute.standard.MediaSize(148, 210, Size2DSyntax.MM, MediaSizeName.ISO_A5);
        /**
         * Specifies the ISO A6 size, 105 mm by 148 mm.
         */
        public static final javax.print.attribute.standard.MediaSize
            A6 = new javax.print.attribute.standard.MediaSize(105, 148, Size2DSyntax.MM, MediaSizeName.ISO_A6);
        /**
         * Specifies the ISO A7 size, 74 mm by 105 mm.
         */
        public static final javax.print.attribute.standard.MediaSize
            A7 = new javax.print.attribute.standard.MediaSize(74, 105, Size2DSyntax.MM, MediaSizeName.ISO_A7);
        /**
         * Specifies the ISO A8 size, 52 mm by 74 mm.
         */
        public static final javax.print.attribute.standard.MediaSize
            A8 = new javax.print.attribute.standard.MediaSize(52, 74, Size2DSyntax.MM, MediaSizeName.ISO_A8);
        /**
         * Specifies the ISO A9 size, 37 mm by 52 mm.
         */
        public static final javax.print.attribute.standard.MediaSize
            A9 = new javax.print.attribute.standard.MediaSize(37, 52, Size2DSyntax.MM, MediaSizeName.ISO_A9);
        /**
         * Specifies the ISO A10 size, 26 mm by 37 mm.
         */
        public static final javax.print.attribute.standard.MediaSize
            A10 = new javax.print.attribute.standard.MediaSize(26, 37, Size2DSyntax.MM, MediaSizeName.ISO_A10);
        /**
         * Specifies the ISO B0 size, 1000 mm by 1414 mm.
         */
        public static final javax.print.attribute.standard.MediaSize
            B0 = new javax.print.attribute.standard.MediaSize(1000, 1414, Size2DSyntax.MM, MediaSizeName.ISO_B0);
        /**
         * Specifies the ISO B1 size, 707 mm by 1000 mm.
         */
        public static final javax.print.attribute.standard.MediaSize
            B1 = new javax.print.attribute.standard.MediaSize(707, 1000, Size2DSyntax.MM, MediaSizeName.ISO_B1);
        /**
         * Specifies the ISO B2 size, 500 mm by 707 mm.
         */
        public static final javax.print.attribute.standard.MediaSize
            B2 = new javax.print.attribute.standard.MediaSize(500, 707, Size2DSyntax.MM, MediaSizeName.ISO_B2);
        /**
         * Specifies the ISO B3 size, 353 mm by 500 mm.
         */
        public static final javax.print.attribute.standard.MediaSize
            B3 = new javax.print.attribute.standard.MediaSize(353, 500, Size2DSyntax.MM, MediaSizeName.ISO_B3);
        /**
         * Specifies the ISO B4 size, 250 mm by 353 mm.
         */
        public static final javax.print.attribute.standard.MediaSize
            B4 = new javax.print.attribute.standard.MediaSize(250, 353, Size2DSyntax.MM, MediaSizeName.ISO_B4);
        /**
         * Specifies the ISO B5 size, 176 mm by 250 mm.
         */
        public static final javax.print.attribute.standard.MediaSize
            B5 = new javax.print.attribute.standard.MediaSize(176, 250, Size2DSyntax.MM, MediaSizeName.ISO_B5);
        /**
         * Specifies the ISO B6 size, 125 mm by 176 mm.
         */
        public static final javax.print.attribute.standard.MediaSize
            B6 = new javax.print.attribute.standard.MediaSize(125, 176, Size2DSyntax.MM, MediaSizeName.ISO_B6);
        /**
         * Specifies the ISO B7 size, 88 mm by 125 mm.
         */
        public static final javax.print.attribute.standard.MediaSize
            B7 = new javax.print.attribute.standard.MediaSize(88, 125, Size2DSyntax.MM, MediaSizeName.ISO_B7);
        /**
         * Specifies the ISO B8 size, 62 mm by 88 mm.
         */
        public static final javax.print.attribute.standard.MediaSize
            B8 = new javax.print.attribute.standard.MediaSize(62, 88, Size2DSyntax.MM, MediaSizeName.ISO_B8);
        /**
         * Specifies the ISO B9 size, 44 mm by 62 mm.
         */
        public static final javax.print.attribute.standard.MediaSize
            B9 = new javax.print.attribute.standard.MediaSize(44, 62, Size2DSyntax.MM, MediaSizeName.ISO_B9);
        /**
         * Specifies the ISO B10 size, 31 mm by 44 mm.
         */
        public static final javax.print.attribute.standard.MediaSize
            B10 = new javax.print.attribute.standard.MediaSize(31, 44, Size2DSyntax.MM, MediaSizeName.ISO_B10);
        /**
         * Specifies the ISO C3 size, 324 mm by 458 mm.
         */
        public static final javax.print.attribute.standard.MediaSize
            C3 = new javax.print.attribute.standard.MediaSize(324, 458, Size2DSyntax.MM, MediaSizeName.ISO_C3);
        /**
         * Specifies the ISO C4 size, 229 mm by 324 mm.
         */
        public static final javax.print.attribute.standard.MediaSize
            C4 = new javax.print.attribute.standard.MediaSize(229, 324, Size2DSyntax.MM, MediaSizeName.ISO_C4);
        /**
         * Specifies the ISO C5 size, 162 mm by 229 mm.
         */
        public static final javax.print.attribute.standard.MediaSize
            C5 = new javax.print.attribute.standard.MediaSize(162, 229, Size2DSyntax.MM, MediaSizeName.ISO_C5);
        /**
         * Specifies the ISO C6 size, 114 mm by 162 mm.
         */
        public static final javax.print.attribute.standard.MediaSize
            C6 = new javax.print.attribute.standard.MediaSize(114, 162, Size2DSyntax.MM, MediaSizeName.ISO_C6);
        /**
         * Specifies the ISO Designated Long size, 110 mm by 220 mm.
         */
        public static final javax.print.attribute.standard.MediaSize
            DESIGNATED_LONG = new javax.print.attribute.standard.MediaSize(110, 220, Size2DSyntax.MM,
                                            MediaSizeName.ISO_DESIGNATED_LONG);

        /**
         * Hide all constructors.
         */
        private ISO() {
        }
    }

    /**
     * Class MediaSize.JIS includes {@link javax.print.attribute.standard.MediaSize MediaSize} values for JIS
     * (Japanese) media.      *
     */
    public final static class JIS {

        /**
         * Specifies the JIS B0 size, 1030 mm by 1456 mm.
         */
        public static final javax.print.attribute.standard.MediaSize
            B0 = new javax.print.attribute.standard.MediaSize(1030, 1456, Size2DSyntax.MM, MediaSizeName.JIS_B0);
        /**
         * Specifies the JIS B1 size, 728 mm by 1030 mm.
         */
        public static final javax.print.attribute.standard.MediaSize
            B1 = new javax.print.attribute.standard.MediaSize(728, 1030, Size2DSyntax.MM, MediaSizeName.JIS_B1);
        /**
         * Specifies the JIS B2 size, 515 mm by 728 mm.
         */
        public static final javax.print.attribute.standard.MediaSize
            B2 = new javax.print.attribute.standard.MediaSize(515, 728, Size2DSyntax.MM, MediaSizeName.JIS_B2);
        /**
         * Specifies the JIS B3 size, 364 mm by 515 mm.
         */
        public static final javax.print.attribute.standard.MediaSize
            B3 = new javax.print.attribute.standard.MediaSize(364, 515, Size2DSyntax.MM, MediaSizeName.JIS_B3);
        /**
         * Specifies the JIS B4 size, 257 mm by 364 mm.
         */
        public static final javax.print.attribute.standard.MediaSize
            B4 = new javax.print.attribute.standard.MediaSize(257, 364, Size2DSyntax.MM, MediaSizeName.JIS_B4);
        /**
         * Specifies the JIS B5 size, 182 mm by 257 mm.
         */
        public static final javax.print.attribute.standard.MediaSize
            B5 = new javax.print.attribute.standard.MediaSize(182, 257, Size2DSyntax.MM, MediaSizeName.JIS_B5);
        /**
         * Specifies the JIS B6 size, 128 mm by 182 mm.
         */
        public static final javax.print.attribute.standard.MediaSize
            B6 = new javax.print.attribute.standard.MediaSize(128, 182, Size2DSyntax.MM, MediaSizeName.JIS_B6);
        /**
         * Specifies the JIS B7 size, 91 mm by 128 mm.
         */
        public static final javax.print.attribute.standard.MediaSize
            B7 = new javax.print.attribute.standard.MediaSize(91, 128, Size2DSyntax.MM, MediaSizeName.JIS_B7);
        /**
         * Specifies the JIS B8 size, 64 mm by 91 mm.
         */
        public static final javax.print.attribute.standard.MediaSize
            B8 = new javax.print.attribute.standard.MediaSize(64, 91, Size2DSyntax.MM, MediaSizeName.JIS_B8);
        /**
         * Specifies the JIS B9 size, 45 mm by 64 mm.
         */
        public static final javax.print.attribute.standard.MediaSize
            B9 = new javax.print.attribute.standard.MediaSize(45, 64, Size2DSyntax.MM, MediaSizeName.JIS_B9);
        /**
         * Specifies the JIS B10 size, 32 mm by 45 mm.
         */
        public static final javax.print.attribute.standard.MediaSize
            B10 = new javax.print.attribute.standard.MediaSize(32, 45, Size2DSyntax.MM, MediaSizeName.JIS_B10);
        /**
         * Specifies the JIS Chou ("long") #1 envelope size, 142 mm by 332 mm.
         */
        public static final javax.print.attribute.standard.MediaSize CHOU_1 = new javax.print.attribute.standard.MediaSize(142, 332, Size2DSyntax.MM);
        /**
         * Specifies the JIS Chou ("long") #2 envelope size, 119 mm by 277 mm.
         */
        public static final javax.print.attribute.standard.MediaSize CHOU_2 = new javax.print.attribute.standard.MediaSize(119, 277, Size2DSyntax.MM);
        /**
         * Specifies the JIS Chou ("long") #3 envelope size, 120 mm by 235 mm.
         */
        public static final javax.print.attribute.standard.MediaSize CHOU_3 = new javax.print.attribute.standard.MediaSize(120, 235, Size2DSyntax.MM);
        /**
         * Specifies the JIS Chou ("long") #4 envelope size, 90 mm by 205 mm.
         */
        public static final javax.print.attribute.standard.MediaSize CHOU_4 = new javax.print.attribute.standard.MediaSize(90, 205, Size2DSyntax.MM);
        /**
         * Specifies the JIS Chou ("long") #30 envelope size, 92 mm by 235 mm.
         */
        public static final javax.print.attribute.standard.MediaSize CHOU_30 = new javax.print.attribute.standard.MediaSize(92, 235, Size2DSyntax.MM);
        /**
         * Specifies the JIS Chou ("long") #40 envelope size, 90 mm by 225 mm.
         */
        public static final javax.print.attribute.standard.MediaSize CHOU_40 = new javax.print.attribute.standard.MediaSize(90, 225, Size2DSyntax.MM);
        /**
         * Specifies the JIS Kaku ("square") #0 envelope size, 287 mm by 382 mm.
         */
        public static final javax.print.attribute.standard.MediaSize KAKU_0 = new javax.print.attribute.standard.MediaSize(287, 382, Size2DSyntax.MM);
        /**
         * Specifies the JIS Kaku ("square") #1 envelope size, 270 mm by 382 mm.
         */
        public static final javax.print.attribute.standard.MediaSize KAKU_1 = new javax.print.attribute.standard.MediaSize(270, 382, Size2DSyntax.MM);
        /**
         * Specifies the JIS Kaku ("square") #2 envelope size, 240 mm by 332 mm.
         */
        public static final javax.print.attribute.standard.MediaSize KAKU_2 = new javax.print.attribute.standard.MediaSize(240, 332, Size2DSyntax.MM);
        /**
         * Specifies the JIS Kaku ("square") #3 envelope size, 216 mm by 277 mm.
         */
        public static final javax.print.attribute.standard.MediaSize KAKU_3 = new javax.print.attribute.standard.MediaSize(216, 277, Size2DSyntax.MM);
        /**
         * Specifies the JIS Kaku ("square") #4 envelope size, 197 mm by 267 mm.
         */
        public static final javax.print.attribute.standard.MediaSize KAKU_4 = new javax.print.attribute.standard.MediaSize(197, 267, Size2DSyntax.MM);
        /**
         * Specifies the JIS Kaku ("square") #5 envelope size, 190 mm by 240 mm.
         */
        public static final javax.print.attribute.standard.MediaSize KAKU_5 = new javax.print.attribute.standard.MediaSize(190, 240, Size2DSyntax.MM);
        /**
         * Specifies the JIS Kaku ("square") #6 envelope size, 162 mm by 229 mm.
         */
        public static final javax.print.attribute.standard.MediaSize KAKU_6 = new javax.print.attribute.standard.MediaSize(162, 229, Size2DSyntax.MM);
        /**
         * Specifies the JIS Kaku ("square") #7 envelope size, 142 mm by 205 mm.
         */
        public static final javax.print.attribute.standard.MediaSize KAKU_7 = new javax.print.attribute.standard.MediaSize(142, 205, Size2DSyntax.MM);
        /**
         * Specifies the JIS Kaku ("square") #8 envelope size, 119 mm by 197 mm.
         */
        public static final javax.print.attribute.standard.MediaSize KAKU_8 = new javax.print.attribute.standard.MediaSize(119, 197, Size2DSyntax.MM);
        /**
         * Specifies the JIS Kaku ("square") #20 envelope size, 229 mm by 324 mm.
         */
        public static final javax.print.attribute.standard.MediaSize KAKU_20 = new javax.print.attribute.standard.MediaSize(229, 324, Size2DSyntax.MM);
        /**
         * Specifies the JIS Kaku ("square") A4 envelope size, 228 mm by 312 mm.
         */
        public static final javax.print.attribute.standard.MediaSize KAKU_A4 = new javax.print.attribute.standard.MediaSize(228, 312, Size2DSyntax.MM);
        /**
         * Specifies the JIS You ("Western") #1 envelope size, 120 mm by 176 mm.
         */
        public static final javax.print.attribute.standard.MediaSize YOU_1 = new javax.print.attribute.standard.MediaSize(120, 176, Size2DSyntax.MM);
        /**
         * Specifies the JIS You ("Western") #2 envelope size, 114 mm by 162 mm.
         */
        public static final javax.print.attribute.standard.MediaSize YOU_2 = new javax.print.attribute.standard.MediaSize(114, 162, Size2DSyntax.MM);
        /**
         * Specifies the JIS You ("Western") #3 envelope size, 98 mm by 148 mm.
         */
        public static final javax.print.attribute.standard.MediaSize YOU_3 = new javax.print.attribute.standard.MediaSize(98, 148, Size2DSyntax.MM);
        /**
         * Specifies the JIS You ("Western") #4 envelope size, 105 mm by 235 mm.
         */
        public static final javax.print.attribute.standard.MediaSize YOU_4 = new javax.print.attribute.standard.MediaSize(105, 235, Size2DSyntax.MM);
        /**
         * Specifies the JIS You ("Western") #5 envelope size, 95 mm by 217 mm.
         */
        public static final javax.print.attribute.standard.MediaSize YOU_5 = new javax.print.attribute.standard.MediaSize(95, 217, Size2DSyntax.MM);
        /**
         * Specifies the JIS You ("Western") #6 envelope size, 98 mm by 190 mm.
         */
        public static final javax.print.attribute.standard.MediaSize YOU_6 = new javax.print.attribute.standard.MediaSize(98, 190, Size2DSyntax.MM);
        /**
         * Specifies the JIS You ("Western") #7 envelope size, 92 mm by 165 mm.
         */
        public static final javax.print.attribute.standard.MediaSize YOU_7 = new javax.print.attribute.standard.MediaSize(92, 165, Size2DSyntax.MM);
        /**
         * Hide all constructors.
         */
        private JIS() {
        }
    }

    /**
     * Class MediaSize.NA includes {@link javax.print.attribute.standard.MediaSize MediaSize} values for North
     * American media.
     */
    public final static class NA {

        /**
         * Specifies the North American letter size, 8.5 inches by 11 inches.
         */
        public static final javax.print.attribute.standard.MediaSize
            LETTER = new javax.print.attribute.standard.MediaSize(8.5f, 11.0f, Size2DSyntax.INCH,
                                                MediaSizeName.NA_LETTER);
        /**
         * Specifies the North American legal size, 8.5 inches by 14 inches.
         */
        public static final javax.print.attribute.standard.MediaSize
            LEGAL = new javax.print.attribute.standard.MediaSize(8.5f, 14.0f, Size2DSyntax.INCH,
                                               MediaSizeName.NA_LEGAL);
        /**
         * Specifies the North American 5 inch by 7 inch paper.
         */
        public static final javax.print.attribute.standard.MediaSize
            NA_5X7 = new javax.print.attribute.standard.MediaSize(5, 7, Size2DSyntax.INCH,
                                   MediaSizeName.NA_5X7);
        /**
         * Specifies the North American 8 inch by 10 inch paper.
         */
        public static final javax.print.attribute.standard.MediaSize
            NA_8X10 = new javax.print.attribute.standard.MediaSize(8, 10, Size2DSyntax.INCH,
                                   MediaSizeName.NA_8X10);
        /**
         * Specifies the North American Number 9 business envelope size,
         * 3.875 inches by 8.875 inches.
         */
        public static final javax.print.attribute.standard.MediaSize
            NA_NUMBER_9_ENVELOPE =
            new javax.print.attribute.standard.MediaSize(3.875f, 8.875f, Size2DSyntax.INCH,
                          MediaSizeName.NA_NUMBER_9_ENVELOPE);
        /**
         * Specifies the North American Number 10 business envelope size,
         * 4.125 inches by 9.5 inches.
         */
        public static final javax.print.attribute.standard.MediaSize
            NA_NUMBER_10_ENVELOPE =
            new javax.print.attribute.standard.MediaSize(4.125f, 9.5f, Size2DSyntax.INCH,
                          MediaSizeName.NA_NUMBER_10_ENVELOPE);
        /**
         * Specifies the North American Number 11 business envelope size,
         * 4.5 inches by 10.375 inches.
         */
        public static final javax.print.attribute.standard.MediaSize
            NA_NUMBER_11_ENVELOPE =
            new javax.print.attribute.standard.MediaSize(4.5f, 10.375f, Size2DSyntax.INCH,
                          MediaSizeName.NA_NUMBER_11_ENVELOPE);
        /**
         * Specifies the North American Number 12 business envelope size,
         * 4.75 inches by 11 inches.
         */
        public static final javax.print.attribute.standard.MediaSize
            NA_NUMBER_12_ENVELOPE =
            new javax.print.attribute.standard.MediaSize(4.75f, 11.0f, Size2DSyntax.INCH,
                          MediaSizeName.NA_NUMBER_12_ENVELOPE);
        /**
         * Specifies the North American Number 14 business envelope size,
         * 5 inches by 11.5 inches.
         */
        public static final javax.print.attribute.standard.MediaSize
            NA_NUMBER_14_ENVELOPE =
            new javax.print.attribute.standard.MediaSize(5.0f, 11.5f, Size2DSyntax.INCH,
                          MediaSizeName.NA_NUMBER_14_ENVELOPE);

        /**
         * Specifies the North American 6 inch by 9 inch envelope size.
         */
        public static final javax.print.attribute.standard.MediaSize
            NA_6X9_ENVELOPE = new javax.print.attribute.standard.MediaSize(6.0f, 9.0f, Size2DSyntax.INCH,
                                            MediaSizeName.NA_6X9_ENVELOPE);
        /**
         * Specifies the North American 7 inch by 9 inch envelope size.
         */
        public static final javax.print.attribute.standard.MediaSize
            NA_7X9_ENVELOPE = new javax.print.attribute.standard.MediaSize(7.0f, 9.0f, Size2DSyntax.INCH,
                                            MediaSizeName.NA_7X9_ENVELOPE);
        /**
         * Specifies the North American 9 inch by 11 inch envelope size.
         */
        public static final javax.print.attribute.standard.MediaSize
            NA_9x11_ENVELOPE = new javax.print.attribute.standard.MediaSize(9.0f, 11.0f, Size2DSyntax.INCH,
                                             MediaSizeName.NA_9X11_ENVELOPE);
        /**
         * Specifies the North American 9 inch by 12 inch envelope size.
         */
        public static final javax.print.attribute.standard.MediaSize
            NA_9x12_ENVELOPE = new javax.print.attribute.standard.MediaSize(9.0f, 12.0f, Size2DSyntax.INCH,
                                             MediaSizeName.NA_9X12_ENVELOPE);
        /**
         * Specifies the North American 10 inch by 13 inch envelope size.
         */
        public static final javax.print.attribute.standard.MediaSize
            NA_10x13_ENVELOPE = new javax.print.attribute.standard.MediaSize(10.0f, 13.0f, Size2DSyntax.INCH,
                                              MediaSizeName.NA_10X13_ENVELOPE);
        /**
         * Specifies the North American 10 inch by 14 inch envelope size.
         */
        public static final javax.print.attribute.standard.MediaSize
            NA_10x14_ENVELOPE = new javax.print.attribute.standard.MediaSize(10.0f, 14.0f, Size2DSyntax.INCH,
                                              MediaSizeName.NA_10X14_ENVELOPE);
        /**
         * Specifies the North American 10 inch by 15 inch envelope size.
         */
        public static final javax.print.attribute.standard.MediaSize
            NA_10X15_ENVELOPE = new javax.print.attribute.standard.MediaSize(10.0f, 15.0f, Size2DSyntax.INCH,
                                              MediaSizeName.NA_10X15_ENVELOPE);
        /**
         * Hide all constructors.
         */
        private NA() {
        }
    }

    /**
     * Class MediaSize.Engineering includes {@link javax.print.attribute.standard.MediaSize MediaSize} values
     * for engineering media.
     */
    public final static class Engineering {

        /**
         * Specifies the engineering A size, 8.5 inch by 11 inch.
         */
        public static final javax.print.attribute.standard.MediaSize
            A = new javax.print.attribute.standard.MediaSize(8.5f, 11.0f, Size2DSyntax.INCH,
                              MediaSizeName.A);
        /**
         * Specifies the engineering B size, 11 inch by 17 inch.
         */
        public static final javax.print.attribute.standard.MediaSize
            B = new javax.print.attribute.standard.MediaSize(11.0f, 17.0f, Size2DSyntax.INCH,
                              MediaSizeName.B);
        /**
         * Specifies the engineering C size, 17 inch by 22 inch.
         */
        public static final javax.print.attribute.standard.MediaSize
            C = new javax.print.attribute.standard.MediaSize(17.0f, 22.0f, Size2DSyntax.INCH,
                              MediaSizeName.C);
        /**
         * Specifies the engineering D size, 22 inch by 34 inch.
         */
        public static final javax.print.attribute.standard.MediaSize
            D = new javax.print.attribute.standard.MediaSize(22.0f, 34.0f, Size2DSyntax.INCH,
                              MediaSizeName.D);
        /**
         * Specifies the engineering E size, 34 inch by 44 inch.
         */
        public static final javax.print.attribute.standard.MediaSize
            E = new javax.print.attribute.standard.MediaSize(34.0f, 44.0f, Size2DSyntax.INCH,
                              MediaSizeName.E);
        /**
         * Hide all constructors.
         */
        private Engineering() {
        }
    }

    /**
     * Class MediaSize.Other includes {@link javax.print.attribute.standard.MediaSize MediaSize} values for
     * miscellaneous media.
     */
    public final static class Other {
        /**
         * Specifies the executive size, 7.25 inches by 10.5 inches.
         */
        public static final javax.print.attribute.standard.MediaSize
            EXECUTIVE = new javax.print.attribute.standard.MediaSize(7.25f, 10.5f, Size2DSyntax.INCH,
                                      MediaSizeName.EXECUTIVE);
        /**
         * Specifies the ledger size, 11 inches by 17 inches.
         */
        public static final javax.print.attribute.standard.MediaSize
            LEDGER = new javax.print.attribute.standard.MediaSize(11.0f, 17.0f, Size2DSyntax.INCH,
                                   MediaSizeName.LEDGER);

        /**
         * Specifies the tabloid size, 11 inches by 17 inches.
         * @since 1.5
         */
        public static final javax.print.attribute.standard.MediaSize
            TABLOID = new javax.print.attribute.standard.MediaSize(11.0f, 17.0f, Size2DSyntax.INCH,
                                   MediaSizeName.TABLOID);

        /**
         * Specifies the invoice size, 5.5 inches by 8.5 inches.
         */
        public static final javax.print.attribute.standard.MediaSize
            INVOICE = new javax.print.attribute.standard.MediaSize(5.5f, 8.5f, Size2DSyntax.INCH,
                              MediaSizeName.INVOICE);
        /**
         * Specifies the folio size, 8.5 inches by 13 inches.
         */
        public static final javax.print.attribute.standard.MediaSize
            FOLIO = new javax.print.attribute.standard.MediaSize(8.5f, 13.0f, Size2DSyntax.INCH,
                                  MediaSizeName.FOLIO);
        /**
         * Specifies the quarto size, 8.5 inches by 10.83 inches.
         */
        public static final javax.print.attribute.standard.MediaSize
            QUARTO = new javax.print.attribute.standard.MediaSize(8.5f, 10.83f, Size2DSyntax.INCH,
                                   MediaSizeName.QUARTO);
        /**
         * Specifies the Italy envelope size, 110 mm by 230 mm.
         */
        public static final javax.print.attribute.standard.MediaSize
        ITALY_ENVELOPE = new javax.print.attribute.standard.MediaSize(110, 230, Size2DSyntax.MM,
                                       MediaSizeName.ITALY_ENVELOPE);
        /**
         * Specifies the Monarch envelope size, 3.87 inch by 7.5 inch.
         */
        public static final javax.print.attribute.standard.MediaSize
        MONARCH_ENVELOPE = new javax.print.attribute.standard.MediaSize(3.87f, 7.5f, Size2DSyntax.INCH,
                                         MediaSizeName.MONARCH_ENVELOPE);
        /**
         * Specifies the Personal envelope size, 3.625 inch by 6.5 inch.
         */
        public static final javax.print.attribute.standard.MediaSize
        PERSONAL_ENVELOPE = new javax.print.attribute.standard.MediaSize(3.625f, 6.5f, Size2DSyntax.INCH,
                                         MediaSizeName.PERSONAL_ENVELOPE);
        /**
         * Specifies the Japanese postcard size, 100 mm by 148 mm.
         */
        public static final javax.print.attribute.standard.MediaSize
            JAPANESE_POSTCARD = new javax.print.attribute.standard.MediaSize(100, 148, Size2DSyntax.MM,
                                              MediaSizeName.JAPANESE_POSTCARD);
        /**
         * Specifies the Japanese Double postcard size, 148 mm by 200 mm.
         */
        public static final javax.print.attribute.standard.MediaSize
            JAPANESE_DOUBLE_POSTCARD = new javax.print.attribute.standard.MediaSize(148, 200, Size2DSyntax.MM,
                                     MediaSizeName.JAPANESE_DOUBLE_POSTCARD);
        /**
         * Hide all constructors.
         */
        private Other() {
        }
    }

    /* force loading of all the subclasses so that the instances
     * are created and inserted into the hashmap.
     */
    static {
        javax.print.attribute.standard.MediaSize ISOA4 = ISO.A4;
        javax.print.attribute.standard.MediaSize JISB5 = JIS.B5;
        javax.print.attribute.standard.MediaSize NALETTER = NA.LETTER;
        javax.print.attribute.standard.MediaSize EngineeringC = Engineering.C;
        javax.print.attribute.standard.MediaSize OtherEXECUTIVE = Other.EXECUTIVE;
    }
}
