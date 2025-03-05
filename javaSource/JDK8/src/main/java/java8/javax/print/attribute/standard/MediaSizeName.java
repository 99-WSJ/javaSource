/*
 * Copyright (c) 2000, 2003, Oracle and/or its affiliates. All rights reserved.
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

import java.util.Locale;

import javax.print.attribute.EnumSyntax;
import javax.print.attribute.Attribute;
import javax.print.attribute.standard.Media;

/**
 * Class MediaSizeName is a subclass of Media.
 * <P>
 * This attribute can be used instead of specifying MediaName or MediaTray.
 * <p>
 * Class MediaSizeName currently declares a few standard media
 * name values.
 * <P>
 * <B>IPP Compatibility:</B> MediaSizeName is a representation class for
 * values of the IPP "media" attribute which names media sizes.
 * The names of the media sizes correspond to those in the IPP 1.1 RFC
 * <a HREF="http://www.ietf.org/rfc/rfc2911.txt">RFC 2911</a>
 * <P>
 *
 */
public class MediaSizeName extends Media {

    private static final long serialVersionUID = 2778798329756942747L;

    /**
     * A0 size.
     */
    public static final javax.print.attribute.standard.MediaSizeName ISO_A0 = new javax.print.attribute.standard.MediaSizeName(0);
    /**
     * A1 size.
     */
    public static final javax.print.attribute.standard.MediaSizeName ISO_A1 = new javax.print.attribute.standard.MediaSizeName(1);
    /**
     * A2 size.
     */
    public static final javax.print.attribute.standard.MediaSizeName ISO_A2 = new javax.print.attribute.standard.MediaSizeName(2);
    /**
     * A3 size.
     */
    public static final javax.print.attribute.standard.MediaSizeName ISO_A3 = new javax.print.attribute.standard.MediaSizeName(3);
    /**
     * A4 size.
     */
    public static final javax.print.attribute.standard.MediaSizeName ISO_A4 = new javax.print.attribute.standard.MediaSizeName(4);
    /**
     * A5 size.
     */
    public static final javax.print.attribute.standard.MediaSizeName ISO_A5 = new javax.print.attribute.standard.MediaSizeName(5);
    /**
     * A6 size.
     */
    public static final javax.print.attribute.standard.MediaSizeName ISO_A6 = new javax.print.attribute.standard.MediaSizeName(6);
    /**
     * A7 size.
     */
    public static final javax.print.attribute.standard.MediaSizeName ISO_A7 = new javax.print.attribute.standard.MediaSizeName(7);
    /**
     * A8 size.
     */
    public static final javax.print.attribute.standard.MediaSizeName ISO_A8 = new javax.print.attribute.standard.MediaSizeName(8);
    /**
     * A9 size.
     */
    public static final javax.print.attribute.standard.MediaSizeName ISO_A9 = new javax.print.attribute.standard.MediaSizeName(9);
    /**
     * A10 size.
     */
    public static final javax.print.attribute.standard.MediaSizeName ISO_A10 = new javax.print.attribute.standard.MediaSizeName(10);

   /**
     * ISO B0 size.
     */
    public static final javax.print.attribute.standard.MediaSizeName ISO_B0 = new javax.print.attribute.standard.MediaSizeName(11);
    /**
     * ISO B1 size.
     */
    public static final javax.print.attribute.standard.MediaSizeName ISO_B1 = new javax.print.attribute.standard.MediaSizeName(12);
    /**
     * ISO B2 size.
     */
    public static final javax.print.attribute.standard.MediaSizeName ISO_B2 = new javax.print.attribute.standard.MediaSizeName(13);
    /**
     * ISO B3 size.
     */
    public static final javax.print.attribute.standard.MediaSizeName ISO_B3 = new javax.print.attribute.standard.MediaSizeName(14);
    /**
     * ISO B4 size.
     */
    public static final javax.print.attribute.standard.MediaSizeName ISO_B4 = new javax.print.attribute.standard.MediaSizeName(15);
    /**
     * ISO B5 size.
     */
    public static final javax.print.attribute.standard.MediaSizeName ISO_B5 = new javax.print.attribute.standard.MediaSizeName(16);
    /**
     * ISO B6 size.
     */
    public static final javax.print.attribute.standard.MediaSizeName ISO_B6 = new javax.print.attribute.standard.MediaSizeName(17);
    /**
     * ISO B7 size.
     */
    public static final javax.print.attribute.standard.MediaSizeName ISO_B7 = new javax.print.attribute.standard.MediaSizeName(18);
    /**
     * ISO B8 size.
     */
    public static final javax.print.attribute.standard.MediaSizeName ISO_B8 = new javax.print.attribute.standard.MediaSizeName(19);
    /**
     * ISO B9 size.
     */
    public static final javax.print.attribute.standard.MediaSizeName ISO_B9 = new javax.print.attribute.standard.MediaSizeName(20);
    /**
     * ISO B10 size.
     */
    public static final javax.print.attribute.standard.MediaSizeName ISO_B10 = new javax.print.attribute.standard.MediaSizeName(21);

   /**
     * JIS B0 size.
     */
    public static final javax.print.attribute.standard.MediaSizeName JIS_B0 = new javax.print.attribute.standard.MediaSizeName(22);
    /**
     * JIS B1 size.
     */
    public static final javax.print.attribute.standard.MediaSizeName JIS_B1 = new javax.print.attribute.standard.MediaSizeName(23);
    /**
     * JIS B2 size.
     */
    public static final javax.print.attribute.standard.MediaSizeName JIS_B2 = new javax.print.attribute.standard.MediaSizeName(24);
    /**
     * JIS B3 size.
     */
    public static final javax.print.attribute.standard.MediaSizeName JIS_B3 = new javax.print.attribute.standard.MediaSizeName(25);
    /**
     * JIS B4 size.
     */
    public static final javax.print.attribute.standard.MediaSizeName JIS_B4 = new javax.print.attribute.standard.MediaSizeName(26);
    /**
     * JIS B5 size.
     */
    public static final javax.print.attribute.standard.MediaSizeName JIS_B5 = new javax.print.attribute.standard.MediaSizeName(27);
    /**
     * JIS B6 size.
     */
    public static final javax.print.attribute.standard.MediaSizeName JIS_B6 = new javax.print.attribute.standard.MediaSizeName(28);
    /**
     * JIS B7 size.
     */
    public static final javax.print.attribute.standard.MediaSizeName JIS_B7 = new javax.print.attribute.standard.MediaSizeName(29);
    /**
     * JIS B8 size.
     */
    public static final javax.print.attribute.standard.MediaSizeName JIS_B8 = new javax.print.attribute.standard.MediaSizeName(30);
    /**
     * JIS B9 size.
     */
    public static final javax.print.attribute.standard.MediaSizeName JIS_B9 = new javax.print.attribute.standard.MediaSizeName(31);
    /**
     * JIS B10 size.
     */
    public static final javax.print.attribute.standard.MediaSizeName JIS_B10 = new javax.print.attribute.standard.MediaSizeName(32);

    /**
     * ISO C0 size.
     */
    public static final javax.print.attribute.standard.MediaSizeName ISO_C0 = new javax.print.attribute.standard.MediaSizeName(33);
    /**
     * ISO C1 size.
     */
    public static final javax.print.attribute.standard.MediaSizeName ISO_C1 = new javax.print.attribute.standard.MediaSizeName(34);
    /**
     * ISO C2 size.
     */
    public static final javax.print.attribute.standard.MediaSizeName ISO_C2 = new javax.print.attribute.standard.MediaSizeName(35);
    /**
     * ISO C3 size.
     */
    public static final javax.print.attribute.standard.MediaSizeName ISO_C3 = new javax.print.attribute.standard.MediaSizeName(36);
    /**
     * ISO C4 size.
     */
    public static final javax.print.attribute.standard.MediaSizeName ISO_C4 = new javax.print.attribute.standard.MediaSizeName(37);
    /**
     * ISO C5 size.
     */
    public static final javax.print.attribute.standard.MediaSizeName ISO_C5 = new javax.print.attribute.standard.MediaSizeName(38);
    /**
     *   letter size.
     */
    public static final javax.print.attribute.standard.MediaSizeName ISO_C6 = new javax.print.attribute.standard.MediaSizeName(39);
    /**
     *   letter size.
     */
    public static final javax.print.attribute.standard.MediaSizeName NA_LETTER = new javax.print.attribute.standard.MediaSizeName(40);

    /**
     *  legal size .
     */
    public static final javax.print.attribute.standard.MediaSizeName NA_LEGAL = new javax.print.attribute.standard.MediaSizeName(41);

    /**
     *  executive size .
     */
    public static final javax.print.attribute.standard.MediaSizeName EXECUTIVE = new javax.print.attribute.standard.MediaSizeName(42);

    /**
     *  ledger size .
     */
    public static final javax.print.attribute.standard.MediaSizeName LEDGER = new javax.print.attribute.standard.MediaSizeName(43);

    /**
     *  tabloid size .
     */
    public static final javax.print.attribute.standard.MediaSizeName TABLOID = new javax.print.attribute.standard.MediaSizeName(44);

    /**
     *  invoice size .
     */
    public static final javax.print.attribute.standard.MediaSizeName INVOICE = new javax.print.attribute.standard.MediaSizeName(45);

    /**
     *  folio size .
     */
    public static final javax.print.attribute.standard.MediaSizeName FOLIO = new javax.print.attribute.standard.MediaSizeName(46);

    /**
     *  quarto size .
     */
    public static final javax.print.attribute.standard.MediaSizeName QUARTO = new javax.print.attribute.standard.MediaSizeName(47);

    /**
     *  Japanese Postcard size.
     */
    public static final javax.print.attribute.standard.MediaSizeName
        JAPANESE_POSTCARD = new javax.print.attribute.standard.MediaSizeName(48);
   /**
     *  Japanese Double Postcard size.
     */
    public static final javax.print.attribute.standard.MediaSizeName
        JAPANESE_DOUBLE_POSTCARD = new javax.print.attribute.standard.MediaSizeName(49);

    /**
     *  A size .
     */
    public static final javax.print.attribute.standard.MediaSizeName A = new javax.print.attribute.standard.MediaSizeName(50);

    /**
     *  B size .
     */
    public static final javax.print.attribute.standard.MediaSizeName B = new javax.print.attribute.standard.MediaSizeName(51);

    /**
     *  C size .
     */
    public static final javax.print.attribute.standard.MediaSizeName C = new javax.print.attribute.standard.MediaSizeName(52);

    /**
     *  D size .
     */
    public static final javax.print.attribute.standard.MediaSizeName D = new javax.print.attribute.standard.MediaSizeName(53);

    /**
     *  E size .
     */
    public static final javax.print.attribute.standard.MediaSizeName E = new javax.print.attribute.standard.MediaSizeName(54);

    /**
     *  ISO designated long size .
     */
    public static final javax.print.attribute.standard.MediaSizeName
        ISO_DESIGNATED_LONG = new javax.print.attribute.standard.MediaSizeName(55);

    /**
     *  Italy envelope size .
     */
    public static final javax.print.attribute.standard.MediaSizeName
        ITALY_ENVELOPE = new javax.print.attribute.standard.MediaSizeName(56);  // DESIGNATED_LONG?

    /**
     *  monarch envelope size .
     */
    public static final javax.print.attribute.standard.MediaSizeName
        MONARCH_ENVELOPE = new javax.print.attribute.standard.MediaSizeName(57);
    /**
     * personal envelope size .
     */
    public static final javax.print.attribute.standard.MediaSizeName
        PERSONAL_ENVELOPE = new javax.print.attribute.standard.MediaSizeName(58);
    /**
     *  number 9 envelope size .
     */
    public static final javax.print.attribute.standard.MediaSizeName
        NA_NUMBER_9_ENVELOPE = new javax.print.attribute.standard.MediaSizeName(59);
    /**
     *  number 10 envelope size .
     */
    public static final javax.print.attribute.standard.MediaSizeName
        NA_NUMBER_10_ENVELOPE = new javax.print.attribute.standard.MediaSizeName(60);
    /**
     *  number 11 envelope size .
     */
    public static final javax.print.attribute.standard.MediaSizeName
        NA_NUMBER_11_ENVELOPE = new javax.print.attribute.standard.MediaSizeName(61);
    /**
     *  number 12 envelope size .
     */
    public static final javax.print.attribute.standard.MediaSizeName
        NA_NUMBER_12_ENVELOPE = new javax.print.attribute.standard.MediaSizeName(62);
    /**
     *  number 14 envelope size .
     */
    public static final javax.print.attribute.standard.MediaSizeName
        NA_NUMBER_14_ENVELOPE = new javax.print.attribute.standard.MediaSizeName(63);
   /**
     *  6x9 North American envelope size.
     */
    public static final javax.print.attribute.standard.MediaSizeName
        NA_6X9_ENVELOPE = new javax.print.attribute.standard.MediaSizeName(64);
   /**
     *  7x9 North American envelope size.
     */
    public static final javax.print.attribute.standard.MediaSizeName
        NA_7X9_ENVELOPE = new javax.print.attribute.standard.MediaSizeName(65);
   /**
     *  9x11 North American envelope size.
     */
    public static final javax.print.attribute.standard.MediaSizeName
        NA_9X11_ENVELOPE = new javax.print.attribute.standard.MediaSizeName(66);
    /**
     *  9x12 North American envelope size.
     */
    public static final javax.print.attribute.standard.MediaSizeName
        NA_9X12_ENVELOPE = new javax.print.attribute.standard.MediaSizeName(67);

    /**
     *  10x13 North American envelope size .
     */
    public static final javax.print.attribute.standard.MediaSizeName
        NA_10X13_ENVELOPE = new javax.print.attribute.standard.MediaSizeName(68);
    /**
     *  10x14North American  envelope size .
     */
    public static final javax.print.attribute.standard.MediaSizeName
        NA_10X14_ENVELOPE = new javax.print.attribute.standard.MediaSizeName(69);
    /**
     *  10x15 North American envelope size.
     */
    public static final javax.print.attribute.standard.MediaSizeName
        NA_10X15_ENVELOPE = new javax.print.attribute.standard.MediaSizeName(70);

    /**
     *  5x7 North American paper.
     */
    public static final javax.print.attribute.standard.MediaSizeName
        NA_5X7 = new javax.print.attribute.standard.MediaSizeName(71);

    /**
     *  8x10 North American paper.
     */
    public static final javax.print.attribute.standard.MediaSizeName
        NA_8X10 = new javax.print.attribute.standard.MediaSizeName(72);

    /**
     * Construct a new media size enumeration value with the given integer
     * value.
     *
     * @param  value  Integer value.
     */
    protected MediaSizeName(int value) {
        super (value);
    }

    private static final String[] myStringTable = {
                "iso-a0",
                "iso-a1",
                "iso-a2",
                "iso-a3",
                "iso-a4",
                "iso-a5",
                "iso-a6",
                "iso-a7",
                "iso-a8",
                "iso-a9",
                "iso-a10",
                "iso-b0",
                "iso-b1",
                "iso-b2",
                "iso-b3",
                "iso-b4",
                "iso-b5",
                "iso-b6",
                "iso-b7",
                "iso-b8",
                "iso-b9",
                "iso-b10",
                "jis-b0",
                "jis-b1",
                "jis-b2",
                "jis-b3",
                "jis-b4",
                "jis-b5",
                "jis-b6",
                "jis-b7",
                "jis-b8",
                "jis-b9",
                "jis-b10",
                "iso-c0",
                "iso-c1",
                "iso-c2",
                "iso-c3",
                "iso-c4",
                "iso-c5",
                "iso-c6",
                "na-letter",
                "na-legal",
                "executive",
                "ledger",
                "tabloid",
                "invoice",
                "folio",
                "quarto",
                "japanese-postcard",
                "oufuko-postcard",
                "a",
                "b",
                "c",
                "d",
                "e",
                "iso-designated-long",
                "italian-envelope",
                "monarch-envelope",
                "personal-envelope",
                "na-number-9-envelope",
                "na-number-10-envelope",
                "na-number-11-envelope",
                "na-number-12-envelope",
                "na-number-14-envelope",
                "na-6x9-envelope",
                "na-7x9-envelope",
                "na-9x11-envelope",
                "na-9x12-envelope",
                "na-10x13-envelope",
                "na-10x14-envelope",
                "na-10x15-envelope",
                "na-5x7",
                "na-8x10",
        };

    private static final javax.print.attribute.standard.MediaSizeName[] myEnumValueTable = {
                ISO_A0,
                ISO_A1,
                ISO_A2,
                ISO_A3,
                ISO_A4,
                ISO_A5,
                ISO_A6,
                ISO_A7,
                ISO_A8,
                ISO_A9,
                ISO_A10,
                ISO_B0,
                ISO_B1,
                ISO_B2,
                ISO_B3,
                ISO_B4,
                ISO_B5,
                ISO_B6,
                ISO_B7,
                ISO_B8,
                ISO_B9,
                ISO_B10,
                JIS_B0,
                JIS_B1,
                JIS_B2,
                JIS_B3,
                JIS_B4,
                JIS_B5,
                JIS_B6,
                JIS_B7,
                JIS_B8,
                JIS_B9,
                JIS_B10,
                ISO_C0,
                ISO_C1,
                ISO_C2,
                ISO_C3,
                ISO_C4,
                ISO_C5,
                ISO_C6,
                NA_LETTER,
                NA_LEGAL,
                EXECUTIVE,
                LEDGER,
                TABLOID,
                INVOICE,
                FOLIO,
                QUARTO,
                JAPANESE_POSTCARD,
                JAPANESE_DOUBLE_POSTCARD,
                A,
                B,
                C,
                D,
                E,
                ISO_DESIGNATED_LONG,
                ITALY_ENVELOPE,
                MONARCH_ENVELOPE,
                PERSONAL_ENVELOPE,
                NA_NUMBER_9_ENVELOPE,
                NA_NUMBER_10_ENVELOPE,
                NA_NUMBER_11_ENVELOPE,
                NA_NUMBER_12_ENVELOPE,
                NA_NUMBER_14_ENVELOPE,
                NA_6X9_ENVELOPE,
                NA_7X9_ENVELOPE,
                NA_9X11_ENVELOPE,
                NA_9X12_ENVELOPE,
                NA_10X13_ENVELOPE,
                NA_10X14_ENVELOPE,
                NA_10X15_ENVELOPE,
                NA_5X7,
                NA_8X10,
        };


    /**
     * Returns the string table for class MediaSizeName.
     */
    protected String[] getStringTable()
    {
        return (String[])myStringTable.clone();
    }

    /**
     * Returns the enumeration value table for class MediaSizeName.
     */
    protected EnumSyntax[] getEnumValueTable() {
        return (EnumSyntax[])myEnumValueTable.clone();
    }


}
