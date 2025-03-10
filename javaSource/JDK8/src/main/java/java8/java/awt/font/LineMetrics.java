/*
 * Copyright (c) 1998, 2003, Oracle and/or its affiliates. All rights reserved.
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

package java8.java.awt.font;

/**
* The <code>LineMetrics</code> class allows access to the
* metrics needed to layout characters along a line
* and to layout of a set of lines.  A <code>LineMetrics</code>
* object encapsulates the measurement information associated
* with a run of text.
* <p>
* Fonts can have different metrics for different ranges of
* characters.  The <code>getLineMetrics</code> methods of
* {@link java.awt.Font Font} take some text as an argument
* and return a <code>LineMetrics</code> object describing the
* metrics of the initial number of characters in that text, as
* returned by {@link #getNumChars}.
*/


public abstract class LineMetrics {


    /**
     * Returns the number of characters (<code>char</code> values) in the text whose
     * metrics are encapsulated by this <code>LineMetrics</code>
     * object.
     * @return the number of characters (<code>char</code> values) in the text with which
     *         this <code>LineMetrics</code> was created.
     */
    public abstract int getNumChars();

    /**
     * Returns the ascent of the text.  The ascent
     * is the distance from the baseline
     * to the ascender line.  The ascent usually represents the
     * the height of the capital letters of the text.  Some characters
     * can extend above the ascender line.
     * @return the ascent of the text.
     */
    public abstract float getAscent();

    /**
     * Returns the descent of the text.  The descent
     * is the distance from the baseline
     * to the descender line.  The descent usually represents
     * the distance to the bottom of lower case letters like
     * 'p'.  Some characters can extend below the descender
     * line.
     * @return the descent of the text.
     */
    public abstract float getDescent();

    /**
     * Returns the leading of the text. The
     * leading is the recommended
     * distance from the bottom of the descender line to the
     * top of the next line.
     * @return the leading of the text.
     */
    public abstract float getLeading();

    /**
     * Returns the height of the text.  The
     * height is equal to the sum of the ascent, the
     * descent and the leading.
     * @return the height of the text.
     */
    public abstract float getHeight();

    /**
     * Returns the baseline index of the text.
     * The index is one of
     * {@link java.awt.Font#ROMAN_BASELINE ROMAN_BASELINE},
     * {@link java.awt.Font#CENTER_BASELINE CENTER_BASELINE},
     * {@link java.awt.Font#HANGING_BASELINE HANGING_BASELINE}.
     * @return the baseline of the text.
     */
    public abstract int getBaselineIndex();

    /**
     * Returns the baseline offsets of the text,
     * relative to the baseline of the text.  The
     * offsets are indexed by baseline index.  For
     * example, if the baseline index is
     * <code>CENTER_BASELINE</code> then
     * <code>offsets[HANGING_BASELINE]</code> is usually
     * negative, <code>offsets[CENTER_BASELINE]</code>
     * is zero, and <code>offsets[ROMAN_BASELINE]</code>
     * is usually positive.
     * @return the baseline offsets of the text.
     */
    public abstract float[] getBaselineOffsets();

    /**
     * Returns the position of the strike-through line
     * relative to the baseline.
     * @return the position of the strike-through line.
     */
    public abstract float getStrikethroughOffset();

    /**
     * Returns the thickness of the strike-through line.
     * @return the thickness of the strike-through line.
     */
    public abstract float getStrikethroughThickness();

    /**
     * Returns the position of the underline relative to
     * the baseline.
     * @return the position of the underline.
     */
    public abstract float getUnderlineOffset();

    /**
     * Returns the thickness of the underline.
     * @return the thickness of the underline.
     */
    public abstract float getUnderlineThickness();
}
