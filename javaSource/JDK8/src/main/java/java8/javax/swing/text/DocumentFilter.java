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
package java8.javax.swing.text;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
 * <code>DocumentFilter</code>, as the name implies, is a filter for the
 * <code>Document</code> mutation methods. When a <code>Document</code>
 * containing a <code>DocumentFilter</code> is modified (either through
 * <code>insert</code> or <code>remove</code>), it forwards the appropriate
 * method invocation to the <code>DocumentFilter</code>. The
 * default implementation allows the modification to
 * occur. Subclasses can filter the modifications by conditionally invoking
 * methods on the superclass, or invoking the necessary methods on
 * the passed in <code>FilterBypass</code>. Subclasses should NOT call back
 * into the Document for the modification
 * instead call into the superclass or the <code>FilterBypass</code>.
 * <p>
 * When <code>remove</code> or <code>insertString</code> is invoked
 * on the <code>DocumentFilter</code>, the <code>DocumentFilter</code>
 * may callback into the
 * <code>FilterBypass</code> multiple times, or for different regions, but
 * it should not callback into the <code>FilterBypass</code> after returning
 * from the <code>remove</code> or <code>insertString</code> method.
 * <p>
 * By default, text related document mutation methods such as
 * <code>insertString</code>, <code>replace</code> and <code>remove</code>
 * in <code>AbstractDocument</code> use <code>DocumentFilter</code> when
 * available, and <code>Element</code> related mutation methods such as
 * <code>create</code>, <code>insert</code> and <code>removeElement</code> in
 * <code>DefaultStyledDocument</code> do not use <code>DocumentFilter</code>.
 * If a method doesn't follow these defaults, this must be explicitly stated
 * in the method documentation.
 *
 * @see Document
 * @see javax.swing.text.AbstractDocument
 * @see javax.swing.text.DefaultStyledDocument
 *
 * @since 1.4
 */
public class DocumentFilter {
    /**
     * Invoked prior to removal of the specified region in the
     * specified Document. Subclasses that want to conditionally allow
     * removal should override this and only call supers implementation as
     * necessary, or call directly into the <code>FilterBypass</code> as
     * necessary.
     *
     * @param fb FilterBypass that can be used to mutate Document
     * @param offset the offset from the beginning &gt;= 0
     * @param length the number of characters to remove &gt;= 0
     * @exception BadLocationException  some portion of the removal range
     *   was not a valid part of the document.  The location in the exception
     *   is the first bad position encountered.
     */
    public void remove(FilterBypass fb, int offset, int length) throws
                       BadLocationException {
        fb.remove(offset, length);
    }

    /**
     * Invoked prior to insertion of text into the
     * specified Document. Subclasses that want to conditionally allow
     * insertion should override this and only call supers implementation as
     * necessary, or call directly into the FilterBypass.
     *
     * @param fb FilterBypass that can be used to mutate Document
     * @param offset  the offset into the document to insert the content &gt;= 0.
     *    All positions that track change at or after the given location
     *    will move.
     * @param string the string to insert
     * @param attr      the attributes to associate with the inserted
     *   content.  This may be null if there are no attributes.
     * @exception BadLocationException  the given insert position is not a
     *   valid position within the document
     */
    public void insertString(FilterBypass fb, int offset, String string,
                             AttributeSet attr) throws BadLocationException {
        fb.insertString(offset, string, attr);
    }

    /**
     * Invoked prior to replacing a region of text in the
     * specified Document. Subclasses that want to conditionally allow
     * replace should override this and only call supers implementation as
     * necessary, or call directly into the FilterBypass.
     *
     * @param fb FilterBypass that can be used to mutate Document
     * @param offset Location in Document
     * @param length Length of text to delete
     * @param text Text to insert, null indicates no text to insert
     * @param attrs AttributeSet indicating attributes of inserted text,
     *              null is legal.
     * @exception BadLocationException  the given insert position is not a
     *   valid position within the document
     */
    public void replace(FilterBypass fb, int offset, int length, String text,
                        AttributeSet attrs) throws BadLocationException {
        fb.replace(offset, length, text, attrs);
    }


    /**
     * Used as a way to circumvent calling back into the Document to
     * change it. Document implementations that wish to support
     * a DocumentFilter must provide an implementation that will
     * not callback into the DocumentFilter when the following methods
     * are invoked from the DocumentFilter.
     * @since 1.4
     */
    public static abstract class FilterBypass {
        /**
         * Returns the Document the mutation is occurring on.
         *
         * @return Document that remove/insertString will operate on
         */
        public abstract Document getDocument();

        /**
         * Removes the specified region of text, bypassing the
         * DocumentFilter.
         *
         * @param offset the offset from the beginning &gt;= 0
         * @param length the number of characters to remove &gt;= 0
         * @exception BadLocationException some portion of the removal range
         *   was not a valid part of the document.  The location in the
         *   exception is the first bad position encountered.
         */
        public abstract void remove(int offset, int length) throws
                             BadLocationException;

        /**
         * Inserts the specified text, bypassing the
         * DocumentFilter.
         * @param offset  the offset into the document to insert the
         *   content &gt;= 0. All positions that track change at or after the
         *   given location will move.
         * @param string the string to insert
         * @param attr the attributes to associate with the inserted
         *   content.  This may be null if there are no attributes.
         * @exception BadLocationException  the given insert position is not a
         *   valid position within the document
         */
        public abstract void insertString(int offset, String string,
                                          AttributeSet attr) throws
                                   BadLocationException;

        /**
         * Deletes the region of text from <code>offset</code> to
         * <code>offset + length</code>, and replaces it with
         *  <code>text</code>.
         *
         * @param offset Location in Document
         * @param length Length of text to delete
         * @param string Text to insert, null indicates no text to insert
         * @param attrs AttributeSet indicating attributes of inserted text,
         *              null is legal.
         * @exception BadLocationException  the given insert is not a
         *   valid position within the document
         */
        public abstract void replace(int offset, int length, String string,
                                          AttributeSet attrs) throws
                                   BadLocationException;
    }
}
