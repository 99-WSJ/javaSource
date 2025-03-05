/*
 * Copyright (c) 1998, 1999, Oracle and/or its affiliates. All rights reserved.
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

package java8.java.awt.im;


/**
 * Defines additional Unicode subsets for use by input methods.  Unlike the
 * UnicodeBlock subsets defined in the <code>{@link
 * Character.UnicodeBlock}</code> class, these constants do not
 * directly correspond to Unicode code blocks.
 *
 * @since   1.2
 */

public final class InputSubset extends Character.Subset {

    private InputSubset(String name) {
        super(name);
    }

    /**
     * Constant for all Latin characters, including the characters
     * in the BASIC_LATIN, LATIN_1_SUPPLEMENT, LATIN_EXTENDED_A,
     * LATIN_EXTENDED_B Unicode character blocks.
     */
    public static final java.awt.im.InputSubset LATIN
        = new java.awt.im.InputSubset("LATIN");

    /**
     * Constant for the digits included in the BASIC_LATIN Unicode character
     * block.
     */
    public static final java.awt.im.InputSubset LATIN_DIGITS
        = new java.awt.im.InputSubset("LATIN_DIGITS");

    /**
     * Constant for all Han characters used in writing Traditional Chinese,
     * including a subset of the CJK unified ideographs as well as Traditional
     * Chinese Han characters that may be defined as surrogate characters.
     */
    public static final java.awt.im.InputSubset TRADITIONAL_HANZI
        = new java.awt.im.InputSubset("TRADITIONAL_HANZI");

    /**
     * Constant for all Han characters used in writing Simplified Chinese,
     * including a subset of the CJK unified ideographs as well as Simplified
     * Chinese Han characters that may be defined as surrogate characters.
     */
    public static final java.awt.im.InputSubset SIMPLIFIED_HANZI
        = new java.awt.im.InputSubset("SIMPLIFIED_HANZI");

    /**
     * Constant for all Han characters used in writing Japanese, including a
     * subset of the CJK unified ideographs as well as Japanese Han characters
     * that may be defined as surrogate characters.
     */
    public static final java.awt.im.InputSubset KANJI
        = new java.awt.im.InputSubset("KANJI");

    /**
     * Constant for all Han characters used in writing Korean, including a
     * subset of the CJK unified ideographs as well as Korean Han characters
     * that may be defined as surrogate characters.
     */
    public static final java.awt.im.InputSubset HANJA
        = new java.awt.im.InputSubset("HANJA");

    /**
     * Constant for the halfwidth katakana subset of the Unicode halfwidth and
     * fullwidth forms character block.
     */
    public static final java.awt.im.InputSubset HALFWIDTH_KATAKANA
        = new java.awt.im.InputSubset("HALFWIDTH_KATAKANA");

    /**
     * Constant for the fullwidth ASCII variants subset of the Unicode halfwidth and
     * fullwidth forms character block.
     * @since 1.3
     */
    public static final java.awt.im.InputSubset FULLWIDTH_LATIN
        = new java.awt.im.InputSubset("FULLWIDTH_LATIN");

    /**
     * Constant for the fullwidth digits included in the Unicode halfwidth and
     * fullwidth forms character block.
     * @since 1.3
     */
    public static final java.awt.im.InputSubset FULLWIDTH_DIGITS
        = new java.awt.im.InputSubset("FULLWIDTH_DIGITS");

}
