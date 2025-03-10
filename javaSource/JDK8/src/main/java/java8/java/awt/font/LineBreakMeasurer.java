/*
 * Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
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
 * (C) Copyright Taligent, Inc. 1996 - 1997, All Rights Reserved
 * (C) Copyright IBM Corp. 1996 - 1998, All Rights Reserved
 *
 * The original version of this source code and documentation is
 * copyrighted and owned by Taligent, Inc., a wholly-owned subsidiary
 * of IBM. These materials are provided under terms of a License
 * Agreement between Taligent and Sun. This technology is protected
 * by multiple US and International patents.
 *
 * This notice and attribution to Taligent may not be removed.
 * Taligent is a registered trademark of Taligent, Inc.
 *
 */

package java8.java.awt.font;

import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.font.TextMeasurer;
import java.text.AttributedCharacterIterator;
import java.text.BreakIterator;

/**
 * The <code>LineBreakMeasurer</code> class allows styled text to be
 * broken into lines (or segments) that fit within a particular visual
 * advance.  This is useful for clients who wish to display a paragraph of
 * text that fits within a specific width, called the <b>wrapping
 * width</b>.
 * <p>
 * <code>LineBreakMeasurer</code> is constructed with an iterator over
 * styled text.  The iterator's range should be a single paragraph in the
 * text.
 * <code>LineBreakMeasurer</code> maintains a position in the text for the
 * start of the next text segment.  Initially, this position is the
 * start of text.  Paragraphs are assigned an overall direction (either
 * left-to-right or right-to-left) according to the bidirectional
 * formatting rules.  All segments obtained from a paragraph have the
 * same direction as the paragraph.
 * <p>
 * Segments of text are obtained by calling the method
 * <code>nextLayout</code>, which returns a {@link TextLayout}
 * representing the text that fits within the wrapping width.
 * The <code>nextLayout</code> method moves the current position
 * to the end of the layout returned from <code>nextLayout</code>.
 * <p>
 * <code>LineBreakMeasurer</code> implements the most commonly used
 * line-breaking policy: Every word that fits within the wrapping
 * width is placed on the line. If the first word does not fit, then all
 * of the characters that fit within the wrapping width are placed on the
 * line.  At least one character is placed on each line.
 * <p>
 * The <code>TextLayout</code> instances returned by
 * <code>LineBreakMeasurer</code> treat tabs like 0-width spaces.  Clients
 * who wish to obtain tab-delimited segments for positioning should use
 * the overload of <code>nextLayout</code> which takes a limiting offset
 * in the text.
 * The limiting offset should be the first character after the tab.
 * The <code>TextLayout</code> objects returned from this method end
 * at the limit provided (or before, if the text between the current
 * position and the limit won't fit entirely within the  wrapping
 * width).
 * <p>
 * Clients who are laying out tab-delimited text need a slightly
 * different line-breaking policy after the first segment has been
 * placed on a line.  Instead of fitting partial words in the
 * remaining space, they should place words which don't fit in the
 * remaining space entirely on the next line.  This change of policy
 * can be requested in the overload of <code>nextLayout</code> which
 * takes a <code>boolean</code> parameter.  If this parameter is
 * <code>true</code>, <code>nextLayout</code> returns
 * <code>null</code> if the first word won't fit in
 * the given space.  See the tab sample below.
 * <p>
 * In general, if the text used to construct the
 * <code>LineBreakMeasurer</code> changes, a new
 * <code>LineBreakMeasurer</code> must be constructed to reflect
 * the change.  (The old <code>LineBreakMeasurer</code> continues to
 * function properly, but it won't be aware of the text change.)
 * Nevertheless, if the text change is the insertion or deletion of a
 * single character, an existing <code>LineBreakMeasurer</code> can be
 * 'updated' by calling <code>insertChar</code> or
 * <code>deleteChar</code>. Updating an existing
 * <code>LineBreakMeasurer</code> is much faster than creating a new one.
 * Clients who modify text based on user typing should take advantage
 * of these methods.
 * <p>
 * <strong>Examples</strong>:<p>
 * Rendering a paragraph in a component
 * <blockquote>
 * <pre>{@code
 * public void paint(Graphics graphics) {
 *
 *     Point2D pen = new Point2D(10, 20);
 *     Graphics2D g2d = (Graphics2D)graphics;
 *     FontRenderContext frc = g2d.getFontRenderContext();
 *
 *     // let styledText be an AttributedCharacterIterator containing at least
 *     // one character
 *
 *     LineBreakMeasurer measurer = new LineBreakMeasurer(styledText, frc);
 *     float wrappingWidth = getSize().width - 15;
 *
 *     while (measurer.getPosition() < fStyledText.length()) {
 *
 *         TextLayout layout = measurer.nextLayout(wrappingWidth);
 *
 *         pen.y += (layout.getAscent());
 *         float dx = layout.isLeftToRight() ?
 *             0 : (wrappingWidth - layout.getAdvance());
 *
 *         layout.draw(graphics, pen.x + dx, pen.y);
 *         pen.y += layout.getDescent() + layout.getLeading();
 *     }
 * }
 * }</pre>
 * </blockquote>
 * <p>
 * Rendering text with tabs.  For simplicity, the overall text
 * direction is assumed to be left-to-right
 * <blockquote>
 * <pre>{@code
 * public void paint(Graphics graphics) {
 *
 *     float leftMargin = 10, rightMargin = 310;
 *     float[] tabStops = { 100, 250 };
 *
 *     // assume styledText is an AttributedCharacterIterator, and the number
 *     // of tabs in styledText is tabCount
 *
 *     int[] tabLocations = new int[tabCount+1];
 *
 *     int i = 0;
 *     for (char c = styledText.first(); c != styledText.DONE; c = styledText.next()) {
 *         if (c == '\t') {
 *             tabLocations[i++] = styledText.getIndex();
 *         }
 *     }
 *     tabLocations[tabCount] = styledText.getEndIndex() - 1;
 *
 *     // Now tabLocations has an entry for every tab's offset in
 *     // the text.  For convenience, the last entry is tabLocations
 *     // is the offset of the last character in the text.
 *
 *     LineBreakMeasurer measurer = new LineBreakMeasurer(styledText);
 *     int currentTab = 0;
 *     float verticalPos = 20;
 *
 *     while (measurer.getPosition() < styledText.getEndIndex()) {
 *
 *         // Lay out and draw each line.  All segments on a line
 *         // must be computed before any drawing can occur, since
 *         // we must know the largest ascent on the line.
 *         // TextLayouts are computed and stored in a Vector;
 *         // their horizontal positions are stored in a parallel
 *         // Vector.
 *
 *         // lineContainsText is true after first segment is drawn
 *         boolean lineContainsText = false;
 *         boolean lineComplete = false;
 *         float maxAscent = 0, maxDescent = 0;
 *         float horizontalPos = leftMargin;
 *         Vector layouts = new Vector(1);
 *         Vector penPositions = new Vector(1);
 *
 *         while (!lineComplete) {
 *             float wrappingWidth = rightMargin - horizontalPos;
 *             TextLayout layout =
 *                     measurer.nextLayout(wrappingWidth,
 *                                         tabLocations[currentTab]+1,
 *                                         lineContainsText);
 *
 *             // layout can be null if lineContainsText is true
 *             if (layout != null) {
 *                 layouts.addElement(layout);
 *                 penPositions.addElement(new Float(horizontalPos));
 *                 horizontalPos += layout.getAdvance();
 *                 maxAscent = Math.max(maxAscent, layout.getAscent());
 *                 maxDescent = Math.max(maxDescent,
 *                     layout.getDescent() + layout.getLeading());
 *             } else {
 *                 lineComplete = true;
 *             }
 *
 *             lineContainsText = true;
 *
 *             if (measurer.getPosition() == tabLocations[currentTab]+1) {
 *                 currentTab++;
 *             }
 *
 *             if (measurer.getPosition() == styledText.getEndIndex())
 *                 lineComplete = true;
 *             else if (horizontalPos >= tabStops[tabStops.length-1])
 *                 lineComplete = true;
 *
 *             if (!lineComplete) {
 *                 // move to next tab stop
 *                 int j;
 *                 for (j=0; horizontalPos >= tabStops[j]; j++) {}
 *                 horizontalPos = tabStops[j];
 *             }
 *         }
 *
 *         verticalPos += maxAscent;
 *
 *         Enumeration layoutEnum = layouts.elements();
 *         Enumeration positionEnum = penPositions.elements();
 *
 *         // now iterate through layouts and draw them
 *         while (layoutEnum.hasMoreElements()) {
 *             TextLayout nextLayout = (TextLayout) layoutEnum.nextElement();
 *             Float nextPosition = (Float) positionEnum.nextElement();
 *             nextLayout.draw(graphics, nextPosition.floatValue(), verticalPos);
 *         }
 *
 *         verticalPos += maxDescent;
 *     }
 * }
 * }</pre>
 * </blockquote>
 * @see TextLayout
 */

public final class LineBreakMeasurer {

    private BreakIterator breakIter;
    private int start;
    private int pos;
    private int limit;
    private TextMeasurer measurer;
    private java.awt.font.CharArrayIterator charIter;

    /**
     * Constructs a <code>LineBreakMeasurer</code> for the specified text.
     *
     * @param text the text for which this <code>LineBreakMeasurer</code>
     *       produces <code>TextLayout</code> objects; the text must contain
     *       at least one character; if the text available through
     *       <code>iter</code> changes, further calls to this
     *       <code>LineBreakMeasurer</code> instance are undefined (except,
     *       in some cases, when <code>insertChar</code> or
     *       <code>deleteChar</code> are invoked afterward - see below)
     * @param frc contains information about a graphics device which is
     *       needed to measure the text correctly;
     *       text measurements can vary slightly depending on the
     *       device resolution, and attributes such as antialiasing; this
     *       parameter does not specify a translation between the
     *       <code>LineBreakMeasurer</code> and user space
     * @see java.awt.font.LineBreakMeasurer#insertChar
     * @see java.awt.font.LineBreakMeasurer#deleteChar
     */
    public LineBreakMeasurer(AttributedCharacterIterator text, FontRenderContext frc) {
        this(text, BreakIterator.getLineInstance(), frc);
    }

    /**
     * Constructs a <code>LineBreakMeasurer</code> for the specified text.
     *
     * @param text the text for which this <code>LineBreakMeasurer</code>
     *     produces <code>TextLayout</code> objects; the text must contain
     *     at least one character; if the text available through
     *     <code>iter</code> changes, further calls to this
     *     <code>LineBreakMeasurer</code> instance are undefined (except,
     *     in some cases, when <code>insertChar</code> or
     *     <code>deleteChar</code> are invoked afterward - see below)
     * @param breakIter the {@link BreakIterator} which defines line
     *     breaks
     * @param frc contains information about a graphics device which is
     *       needed to measure the text correctly;
     *       text measurements can vary slightly depending on the
     *       device resolution, and attributes such as antialiasing; this
     *       parameter does not specify a translation between the
     *       <code>LineBreakMeasurer</code> and user space
     * @throws IllegalArgumentException if the text has less than one character
     * @see java.awt.font.LineBreakMeasurer#insertChar
     * @see java.awt.font.LineBreakMeasurer#deleteChar
     */
    public LineBreakMeasurer(AttributedCharacterIterator text,
                             BreakIterator breakIter,
                             FontRenderContext frc) {
        if (text.getEndIndex() - text.getBeginIndex() < 1) {
            throw new IllegalArgumentException("Text must contain at least one character.");
        }

        this.breakIter = breakIter;
        this.measurer = new TextMeasurer(text, frc);
        this.limit = text.getEndIndex();
        this.pos = this.start = text.getBeginIndex();

        charIter = new java.awt.font.CharArrayIterator(measurer.getChars(), this.start);
        this.breakIter.setText(charIter);
    }

    /**
     * Returns the position at the end of the next layout.  Does NOT
     * update the current position of this <code>LineBreakMeasurer</code>.
     *
     * @param wrappingWidth the maximum visible advance permitted for
     *    the text in the next layout
     * @return an offset in the text representing the limit of the
     *    next <code>TextLayout</code>.
     */
    public int nextOffset(float wrappingWidth) {
        return nextOffset(wrappingWidth, limit, false);
    }

    /**
     * Returns the position at the end of the next layout.  Does NOT
     * update the current position of this <code>LineBreakMeasurer</code>.
     *
     * @param wrappingWidth the maximum visible advance permitted for
     *    the text in the next layout
     * @param offsetLimit the first character that can not be included
     *    in the next layout, even if the text after the limit would fit
     *    within the wrapping width; <code>offsetLimit</code> must be
     *    greater than the current position
     * @param requireNextWord if <code>true</code>, the current position
     *    that is returned if the entire next word does not fit within
     *    <code>wrappingWidth</code>; if <code>false</code>, the offset
     *    returned is at least one greater than the current position
     * @return an offset in the text representing the limit of the
     *    next <code>TextLayout</code>
     */
    public int nextOffset(float wrappingWidth, int offsetLimit,
                          boolean requireNextWord) {

        int nextOffset = pos;

        if (pos < limit) {
            if (offsetLimit <= pos) {
                    throw new IllegalArgumentException("offsetLimit must be after current position");
            }

            int charAtMaxAdvance =
                            measurer.getLineBreakIndex(pos, wrappingWidth);

            if (charAtMaxAdvance == limit) {
                nextOffset = limit;
            }
            else if (Character.isWhitespace(measurer.getChars()[charAtMaxAdvance-start])) {
                nextOffset = breakIter.following(charAtMaxAdvance);
            }
            else {
            // Break is in a word;  back up to previous break.

                // NOTE:  I think that breakIter.preceding(limit) should be
                // equivalent to breakIter.last(), breakIter.previous() but
                // the authors of BreakIterator thought otherwise...
                // If they were equivalent then the first branch would be
                // unnecessary.
                int testPos = charAtMaxAdvance + 1;
                if (testPos == limit) {
                    breakIter.last();
                    nextOffset = breakIter.previous();
                }
                else {
                    nextOffset = breakIter.preceding(testPos);
                }

                if (nextOffset <= pos) {
                    // first word doesn't fit on line
                    if (requireNextWord) {
                        nextOffset = pos;
                    }
                    else {
                        nextOffset = Math.max(pos+1, charAtMaxAdvance);
                    }
                }
            }
        }

        if (nextOffset > offsetLimit) {
            nextOffset = offsetLimit;
        }

        return nextOffset;
    }

    /**
     * Returns the next layout, and updates the current position.
     *
     * @param wrappingWidth the maximum visible advance permitted for
     *     the text in the next layout
     * @return a <code>TextLayout</code>, beginning at the current
     *     position, which represents the next line fitting within
     *     <code>wrappingWidth</code>
     */
    public TextLayout nextLayout(float wrappingWidth) {
        return nextLayout(wrappingWidth, limit, false);
    }

    /**
     * Returns the next layout, and updates the current position.
     *
     * @param wrappingWidth the maximum visible advance permitted
     *    for the text in the next layout
     * @param offsetLimit the first character that can not be
     *    included in the next layout, even if the text after the limit
     *    would fit within the wrapping width; <code>offsetLimit</code>
     *    must be greater than the current position
     * @param requireNextWord if <code>true</code>, and if the entire word
     *    at the current position does not fit within the wrapping width,
     *    <code>null</code> is returned. If <code>false</code>, a valid
     *    layout is returned that includes at least the character at the
     *    current position
     * @return a <code>TextLayout</code>, beginning at the current
     *    position, that represents the next line fitting within
     *    <code>wrappingWidth</code>.  If the current position is at the end
     *    of the text used by this <code>LineBreakMeasurer</code>,
     *    <code>null</code> is returned
     */
    public TextLayout nextLayout(float wrappingWidth, int offsetLimit,
                                 boolean requireNextWord) {

        if (pos < limit) {
            int layoutLimit = nextOffset(wrappingWidth, offsetLimit, requireNextWord);
            if (layoutLimit == pos) {
                return null;
            }

            TextLayout result = measurer.getLayout(pos, layoutLimit);
            pos = layoutLimit;

            return result;
        } else {
            return null;
        }
    }

    /**
     * Returns the current position of this <code>LineBreakMeasurer</code>.
     *
     * @return the current position of this <code>LineBreakMeasurer</code>
     * @see #setPosition
     */
    public int getPosition() {
        return pos;
    }

    /**
     * Sets the current position of this <code>LineBreakMeasurer</code>.
     *
     * @param newPosition the current position of this
     *    <code>LineBreakMeasurer</code>; the position should be within the
     *    text used to construct this <code>LineBreakMeasurer</code> (or in
     *    the text most recently passed to <code>insertChar</code>
     *    or <code>deleteChar</code>
     * @see #getPosition
     */
    public void setPosition(int newPosition) {
        if (newPosition < start || newPosition > limit) {
            throw new IllegalArgumentException("position is out of range");
        }
        pos = newPosition;
    }

    /**
     * Updates this <code>LineBreakMeasurer</code> after a single
     * character is inserted into the text, and sets the current
     * position to the beginning of the paragraph.
     *
     * @param newParagraph the text after the insertion
     * @param insertPos the position in the text at which the character
     *    is inserted
     * @throws IndexOutOfBoundsException if <code>insertPos</code> is less
     *         than the start of <code>newParagraph</code> or greater than
     *         or equal to the end of <code>newParagraph</code>
     * @throws NullPointerException if <code>newParagraph</code> is
     *         <code>null</code>
     * @see #deleteChar
     */
    public void insertChar(AttributedCharacterIterator newParagraph,
                           int insertPos) {

        measurer.insertChar(newParagraph, insertPos);

        limit = newParagraph.getEndIndex();
        pos = start = newParagraph.getBeginIndex();

        charIter.reset(measurer.getChars(), newParagraph.getBeginIndex());
        breakIter.setText(charIter);
    }

    /**
     * Updates this <code>LineBreakMeasurer</code> after a single
     * character is deleted from the text, and sets the current
     * position to the beginning of the paragraph.
     * @param newParagraph the text after the deletion
     * @param deletePos the position in the text at which the character
     *    is deleted
     * @throws IndexOutOfBoundsException if <code>deletePos</code> is
     *         less than the start of <code>newParagraph</code> or greater
     *         than the end of <code>newParagraph</code>
     * @throws NullPointerException if <code>newParagraph</code> is
     *         <code>null</code>
     * @see #insertChar
     */
    public void deleteChar(AttributedCharacterIterator newParagraph,
                           int deletePos) {

        measurer.deleteChar(newParagraph, deletePos);

        limit = newParagraph.getEndIndex();
        pos = start = newParagraph.getBeginIndex();

        charIter.reset(measurer.getChars(), start);
        breakIter.setText(charIter);
    }
}
