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

package java8.javax.accessibility;


import java.util.*;
import java.awt.*;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.swing.text.*;


/**
 * <P>The AccessibleText interface should be implemented by all
 * classes that present textual information on the display.  This interface
 * provides the standard mechanism for an assistive technology to access
 * that text via its content, attributes, and spatial location.
 * Applications can determine if an object supports the AccessibleText
 * interface by first obtaining its AccessibleContext (see {@link Accessible})
 * and then calling the {@link AccessibleContext#getAccessibleText} method of
 * AccessibleContext.  If the return value is not null, the object supports this
 * interface.
 *
 * @see Accessible
 * @see Accessible#getAccessibleContext
 * @see AccessibleContext
 * @see AccessibleContext#getAccessibleText
 *
 * @author      Peter Korn
 */
public interface AccessibleText {

    /**
     * Constant used to indicate that the part of the text that should be
     * retrieved is a character.
     *
     * @see #getAtIndex
     * @see #getAfterIndex
     * @see #getBeforeIndex
     */
    public static final int CHARACTER = 1;

    /**
     * Constant used to indicate that the part of the text that should be
     * retrieved is a word.
     *
     * @see #getAtIndex
     * @see #getAfterIndex
     * @see #getBeforeIndex
     */
    public static final int WORD = 2;

    /**
     * Constant used to indicate that the part of the text that should be
     * retrieved is a sentence.
     *
     * A sentence is a string of words which expresses an assertion,
     * a question, a command, a wish, an exclamation, or the performance
     * of an action. In English locales, the string usually begins with
     * a capital letter and concludes with appropriate end punctuation;
     * such as a period, question or exclamation mark. Other locales may
     * use different capitalization and/or punctuation.
     *
     * @see #getAtIndex
     * @see #getAfterIndex
     * @see #getBeforeIndex
     */
    public static final int SENTENCE = 3;

    /**
     * Given a point in local coordinates, return the zero-based index
     * of the character under that Point.  If the point is invalid,
     * this method returns -1.
     *
     * @param p the Point in local coordinates
     * @return the zero-based index of the character under Point p; if
     * Point is invalid return -1.
     */
    public int getIndexAtPoint(Point p);

    /**
     * Determines the bounding box of the character at the given
     * index into the string.  The bounds are returned in local
     * coordinates.  If the index is invalid an empty rectangle is returned.
     *
     * @param i the index into the String
     * @return the screen coordinates of the character's bounding box,
     * if index is invalid return an empty rectangle.
     */
    public Rectangle getCharacterBounds(int i);

    /**
     * Returns the number of characters (valid indicies)
     *
     * @return the number of characters
     */
    public int getCharCount();

    /**
     * Returns the zero-based offset of the caret.
     *
     * Note: That to the right of the caret will have the same index
     * value as the offset (the caret is between two characters).
     * @return the zero-based offset of the caret.
     */
    public int getCaretPosition();

    /**
     * Returns the String at a given index.
     *
     * @param part the CHARACTER, WORD, or SENTENCE to retrieve
     * @param index an index within the text
     * @return the letter, word, or sentence
     */
    public String getAtIndex(int part, int index);

    /**
     * Returns the String after a given index.
     *
     * @param part the CHARACTER, WORD, or SENTENCE to retrieve
     * @param index an index within the text
     * @return the letter, word, or sentence
     */
    public String getAfterIndex(int part, int index);

    /**
     * Returns the String before a given index.
     *
     * @param part the CHARACTER, WORD, or SENTENCE to retrieve
     * @param index an index within the text
     * @return the letter, word, or sentence
     */
    public String getBeforeIndex(int part, int index);

    /**
     * Returns the AttributeSet for a given character at a given index
     *
     * @param i the zero-based index into the text
     * @return the AttributeSet of the character
     */
    public AttributeSet getCharacterAttribute(int i);

    /**
     * Returns the start offset within the selected text.
     * If there is no selection, but there is
     * a caret, the start and end offsets will be the same.
     *
     * @return the index into the text of the start of the selection
     */
    public int getSelectionStart();

    /**
     * Returns the end offset within the selected text.
     * If there is no selection, but there is
     * a caret, the start and end offsets will be the same.
     *
     * @return the index into the text of the end of the selection
     */
    public int getSelectionEnd();

    /**
     * Returns the portion of the text that is selected.
     *
     * @return the String portion of the text that is selected
     */
    public String getSelectedText();
}
