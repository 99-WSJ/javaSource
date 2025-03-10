/*
 * Copyright (c) 1995, 2013, Oracle and/or its affiliates. All rights reserved.
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
package java8.java.awt;

import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import java.awt.AWTKeyStroke;
import java.awt.GraphicsEnvironment;
import java.awt.TextComponent;
import java.awt.peer.TextAreaPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * A <code>TextArea</code> object is a multi-line region
 * that displays text. It can be set to allow editing or
 * to be read-only.
 * <p>
 * The following image shows the appearance of a text area:
 * <p>
 * <img src="doc-files/TextArea-1.gif" alt="A TextArea showing the word 'Hello!'"
 * style="float:center; margin: 7px 10px;">
 * <p>
 * This text area could be created by the following line of code:
 *
 * <hr><blockquote><pre>
 * new TextArea("Hello", 5, 40);
 * </pre></blockquote><hr>
 * <p>
 * @author      Sami Shaio
 * @since       JDK1.0
 */
public class TextArea extends TextComponent {

    /**
     * The number of rows in the <code>TextArea</code>.
     * This parameter will determine the text area's height.
     * Guaranteed to be non-negative.
     *
     * @serial
     * @see #getRows()
     * @see #setRows(int)
     */
    int rows;

    /**
     * The number of columns in the <code>TextArea</code>.
     * A column is an approximate average character
     * width that is platform-dependent.
     * This parameter will determine the text area's width.
     * Guaranteed to be non-negative.
     *
     * @serial
     * @see  #setColumns(int)
     * @see  #getColumns()
     */
    int columns;

    private static final String base = "text";
    private static int nameCounter = 0;

    /**
     * Create and display both vertical and horizontal scrollbars.
     * @since JDK1.1
     */
    public static final int SCROLLBARS_BOTH = 0;

    /**
     * Create and display vertical scrollbar only.
     * @since JDK1.1
     */
    public static final int SCROLLBARS_VERTICAL_ONLY = 1;

    /**
     * Create and display horizontal scrollbar only.
     * @since JDK1.1
     */
    public static final int SCROLLBARS_HORIZONTAL_ONLY = 2;

    /**
     * Do not create or display any scrollbars for the text area.
     * @since JDK1.1
     */
    public static final int SCROLLBARS_NONE = 3;

    /**
     * Determines which scrollbars are created for the
     * text area. It can be one of four values :
     * <code>SCROLLBARS_BOTH</code> = both scrollbars.<BR>
     * <code>SCROLLBARS_HORIZONTAL_ONLY</code> = Horizontal bar only.<BR>
     * <code>SCROLLBARS_VERTICAL_ONLY</code> = Vertical bar only.<BR>
     * <code>SCROLLBARS_NONE</code> = No scrollbars.<BR>
     *
     * @serial
     * @see #getScrollbarVisibility()
     */
    private int scrollbarVisibility;

    /**
     * Cache the Sets of forward and backward traversal keys so we need not
     * look them up each time.
     */
    private static Set<AWTKeyStroke> forwardTraversalKeys, backwardTraversalKeys;

    /*
     * JDK 1.1 serialVersionUID
     */
     private static final long serialVersionUID = 3692302836626095722L;

    /**
     * Initialize JNI field and method ids
     */
    private static native void initIDs();

    static {
        /* ensure that the necessary native libraries are loaded */
        Toolkit.loadLibraries();
        if (!GraphicsEnvironment.isHeadless()) {
            initIDs();
        }
        forwardTraversalKeys = KeyboardFocusManager.initFocusTraversalKeysSet(
            "ctrl TAB",
            new HashSet<AWTKeyStroke>());
        backwardTraversalKeys = KeyboardFocusManager.initFocusTraversalKeysSet(
            "ctrl shift TAB",
            new HashSet<AWTKeyStroke>());
    }

    /**
     * Constructs a new text area with the empty string as text.
     * This text area is created with scrollbar visibility equal to
     * {@link #SCROLLBARS_BOTH}, so both vertical and horizontal
     * scrollbars will be visible for this text area.
     * @exception HeadlessException if
     *    <code>GraphicsEnvironment.isHeadless</code> returns true
     * @see GraphicsEnvironment#isHeadless()
     */
    public TextArea() throws HeadlessException {
        this("", 0, 0, SCROLLBARS_BOTH);
    }

    /**
     * Constructs a new text area with the specified text.
     * This text area is created with scrollbar visibility equal to
     * {@link #SCROLLBARS_BOTH}, so both vertical and horizontal
     * scrollbars will be visible for this text area.
     * @param      text       the text to be displayed; if
     *             <code>text</code> is <code>null</code>, the empty
     *             string <code>""</code> will be displayed
     * @exception HeadlessException if
     *        <code>GraphicsEnvironment.isHeadless</code> returns true
     * @see GraphicsEnvironment#isHeadless()
     */
    public TextArea(String text) throws HeadlessException {
        this(text, 0, 0, SCROLLBARS_BOTH);
    }

    /**
     * Constructs a new text area with the specified number of
     * rows and columns and the empty string as text.
     * A column is an approximate average character
     * width that is platform-dependent.  The text area is created with
     * scrollbar visibility equal to {@link #SCROLLBARS_BOTH}, so both
     * vertical and horizontal scrollbars will be visible for this
     * text area.
     * @param rows the number of rows
     * @param columns the number of columns
     * @exception HeadlessException if
     *     <code>GraphicsEnvironment.isHeadless</code> returns true
     * @see GraphicsEnvironment#isHeadless()
     */
    public TextArea(int rows, int columns) throws HeadlessException {
        this("", rows, columns, SCROLLBARS_BOTH);
    }

    /**
     * Constructs a new text area with the specified text,
     * and with the specified number of rows and columns.
     * A column is an approximate average character
     * width that is platform-dependent.  The text area is created with
     * scrollbar visibility equal to {@link #SCROLLBARS_BOTH}, so both
     * vertical and horizontal scrollbars will be visible for this
     * text area.
     * @param      text       the text to be displayed; if
     *             <code>text</code> is <code>null</code>, the empty
     *             string <code>""</code> will be displayed
     * @param     rows      the number of rows
     * @param     columns   the number of columns
     * @exception HeadlessException if
     *   <code>GraphicsEnvironment.isHeadless</code> returns true
     * @see GraphicsEnvironment#isHeadless()
     */
    public TextArea(String text, int rows, int columns)
        throws HeadlessException {
        this(text, rows, columns, SCROLLBARS_BOTH);
    }

    /**
     * Constructs a new text area with the specified text,
     * and with the rows, columns, and scroll bar visibility
     * as specified.  All <code>TextArea</code> constructors defer to
     * this one.
     * <p>
     * The <code>TextArea</code> class defines several constants
     * that can be supplied as values for the
     * <code>scrollbars</code> argument:
     * <ul>
     * <li><code>SCROLLBARS_BOTH</code>,
     * <li><code>SCROLLBARS_VERTICAL_ONLY</code>,
     * <li><code>SCROLLBARS_HORIZONTAL_ONLY</code>,
     * <li><code>SCROLLBARS_NONE</code>.
     * </ul>
     * Any other value for the
     * <code>scrollbars</code> argument is invalid and will result in
     * this text area being created with scrollbar visibility equal to
     * the default value of {@link #SCROLLBARS_BOTH}.
     * @param      text       the text to be displayed; if
     *             <code>text</code> is <code>null</code>, the empty
     *             string <code>""</code> will be displayed
     * @param      rows       the number of rows; if
     *             <code>rows</code> is less than <code>0</code>,
     *             <code>rows</code> is set to <code>0</code>
     * @param      columns    the number of columns; if
     *             <code>columns</code> is less than <code>0</code>,
     *             <code>columns</code> is set to <code>0</code>
     * @param      scrollbars  a constant that determines what
     *             scrollbars are created to view the text area
     * @since      JDK1.1
     * @exception HeadlessException if
     *    <code>GraphicsEnvironment.isHeadless</code> returns true
     * @see GraphicsEnvironment#isHeadless()
     */
    public TextArea(String text, int rows, int columns, int scrollbars)
        throws HeadlessException {
        super(text);

        this.rows = (rows >= 0) ? rows : 0;
        this.columns = (columns >= 0) ? columns : 0;

        if (scrollbars >= SCROLLBARS_BOTH && scrollbars <= SCROLLBARS_NONE) {
            this.scrollbarVisibility = scrollbars;
        } else {
            this.scrollbarVisibility = SCROLLBARS_BOTH;
        }

        setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
                              forwardTraversalKeys);
        setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
                              backwardTraversalKeys);
    }

    /**
     * Construct a name for this component.  Called by <code>getName</code>
     * when the name is <code>null</code>.
     */
    String constructComponentName() {
        synchronized (java.awt.TextArea.class) {
            return base + nameCounter++;
        }
    }

    /**
     * Creates the <code>TextArea</code>'s peer.  The peer allows us to modify
     * the appearance of the <code>TextArea</code> without changing any of its
     * functionality.
     */
    public void addNotify() {
        synchronized (getTreeLock()) {
            if (peer == null)
                peer = getToolkit().createTextArea(this);
            super.addNotify();
        }
    }

    /**
     * Inserts the specified text at the specified position
     * in this text area.
     * <p>Note that passing <code>null</code> or inconsistent
     * parameters is invalid and will result in unspecified
     * behavior.
     *
     * @param      str the non-<code>null</code> text to insert
     * @param      pos the position at which to insert
     * @see        TextComponent#setText
     * @see        java.awt.TextArea#replaceRange
     * @see        java.awt.TextArea#append
     * @since      JDK1.1
     */
    public void insert(String str, int pos) {
        insertText(str, pos);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>insert(String, int)</code>.
     */
    @Deprecated
    public synchronized void insertText(String str, int pos) {
        TextAreaPeer peer = (TextAreaPeer)this.peer;
        if (peer != null) {
            peer.insert(str, pos);
        } else {
            text = text.substring(0, pos) + str + text.substring(pos);
        }
    }

    /**
     * Appends the given text to the text area's current text.
     * <p>Note that passing <code>null</code> or inconsistent
     * parameters is invalid and will result in unspecified
     * behavior.
     *
     * @param     str the non-<code>null</code> text to append
     * @see       java.awt.TextArea#insert
     * @since     JDK1.1
     */
    public void append(String str) {
        appendText(str);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>append(String)</code>.
     */
    @Deprecated
    public synchronized void appendText(String str) {
        if (peer != null) {
            insertText(str, getText().length());
        } else {
            text = text + str;
        }
    }

    /**
     * Replaces text between the indicated start and end positions
     * with the specified replacement text.  The text at the end
     * position will not be replaced.  The text at the start
     * position will be replaced (unless the start position is the
     * same as the end position).
     * The text position is zero-based.  The inserted substring may be
     * of a different length than the text it replaces.
     * <p>Note that passing <code>null</code> or inconsistent
     * parameters is invalid and will result in unspecified
     * behavior.
     *
     * @param     str      the non-<code>null</code> text to use as
     *                     the replacement
     * @param     start    the start position
     * @param     end      the end position
     * @see       java.awt.TextArea#insert
     * @since     JDK1.1
     */
    public void replaceRange(String str, int start, int end) {
        replaceText(str, start, end);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>replaceRange(String, int, int)</code>.
     */
    @Deprecated
    public synchronized void replaceText(String str, int start, int end) {
        TextAreaPeer peer = (TextAreaPeer)this.peer;
        if (peer != null) {
            peer.replaceRange(str, start, end);
        } else {
            text = text.substring(0, start) + str + text.substring(end);
        }
    }

    /**
     * Returns the number of rows in the text area.
     * @return    the number of rows in the text area
     * @see       #setRows(int)
     * @see       #getColumns()
     * @since     JDK1
     */
    public int getRows() {
        return rows;
    }

    /**
     * Sets the number of rows for this text area.
     * @param       rows   the number of rows
     * @see         #getRows()
     * @see         #setColumns(int)
     * @exception   IllegalArgumentException   if the value
     *                 supplied for <code>rows</code>
     *                 is less than <code>0</code>
     * @since       JDK1.1
     */
    public void setRows(int rows) {
        int oldVal = this.rows;
        if (rows < 0) {
            throw new IllegalArgumentException("rows less than zero.");
        }
        if (rows != oldVal) {
            this.rows = rows;
            invalidate();
        }
    }

    /**
     * Returns the number of columns in this text area.
     * @return    the number of columns in the text area
     * @see       #setColumns(int)
     * @see       #getRows()
     */
    public int getColumns() {
        return columns;
    }

    /**
     * Sets the number of columns for this text area.
     * @param       columns   the number of columns
     * @see         #getColumns()
     * @see         #setRows(int)
     * @exception   IllegalArgumentException   if the value
     *                 supplied for <code>columns</code>
     *                 is less than <code>0</code>
     * @since       JDK1.1
     */
    public void setColumns(int columns) {
        int oldVal = this.columns;
        if (columns < 0) {
            throw new IllegalArgumentException("columns less than zero.");
        }
        if (columns != oldVal) {
            this.columns = columns;
            invalidate();
        }
    }

    /**
     * Returns an enumerated value that indicates which scroll bars
     * the text area uses.
     * <p>
     * The <code>TextArea</code> class defines four integer constants
     * that are used to specify which scroll bars are available.
     * <code>TextArea</code> has one constructor that gives the
     * application discretion over scroll bars.
     *
     * @return     an integer that indicates which scroll bars are used
     * @see        java.awt.TextArea#SCROLLBARS_BOTH
     * @see        java.awt.TextArea#SCROLLBARS_VERTICAL_ONLY
     * @see        java.awt.TextArea#SCROLLBARS_HORIZONTAL_ONLY
     * @see        java.awt.TextArea#SCROLLBARS_NONE
     * @see        java.awt.TextArea#TextArea(String, int, int, int)
     * @since      JDK1.1
     */
    public int getScrollbarVisibility() {
        return scrollbarVisibility;
    }


    /**
     * Determines the preferred size of a text area with the specified
     * number of rows and columns.
     * @param     rows   the number of rows
     * @param     columns   the number of columns
     * @return    the preferred dimensions required to display
     *                       the text area with the specified
     *                       number of rows and columns
     * @see       Component#getPreferredSize
     * @since     JDK1.1
     */
    public Dimension getPreferredSize(int rows, int columns) {
        return preferredSize(rows, columns);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>getPreferredSize(int, int)</code>.
     */
    @Deprecated
    public Dimension preferredSize(int rows, int columns) {
        synchronized (getTreeLock()) {
            TextAreaPeer peer = (TextAreaPeer)this.peer;
            return (peer != null) ?
                       peer.getPreferredSize(rows, columns) :
                       super.preferredSize();
        }
    }

    /**
     * Determines the preferred size of this text area.
     * @return    the preferred dimensions needed for this text area
     * @see       Component#getPreferredSize
     * @since     JDK1.1
     */
    public Dimension getPreferredSize() {
        return preferredSize();
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>getPreferredSize()</code>.
     */
    @Deprecated
    public Dimension preferredSize() {
        synchronized (getTreeLock()) {
            return ((rows > 0) && (columns > 0)) ?
                        preferredSize(rows, columns) :
                        super.preferredSize();
        }
    }

    /**
     * Determines the minimum size of a text area with the specified
     * number of rows and columns.
     * @param     rows   the number of rows
     * @param     columns   the number of columns
     * @return    the minimum dimensions required to display
     *                       the text area with the specified
     *                       number of rows and columns
     * @see       Component#getMinimumSize
     * @since     JDK1.1
     */
    public Dimension getMinimumSize(int rows, int columns) {
        return minimumSize(rows, columns);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>getMinimumSize(int, int)</code>.
     */
    @Deprecated
    public Dimension minimumSize(int rows, int columns) {
        synchronized (getTreeLock()) {
            TextAreaPeer peer = (TextAreaPeer)this.peer;
            return (peer != null) ?
                       peer.getMinimumSize(rows, columns) :
                       super.minimumSize();
        }
    }

    /**
     * Determines the minimum size of this text area.
     * @return    the preferred dimensions needed for this text area
     * @see       Component#getPreferredSize
     * @since     JDK1.1
     */
    public Dimension getMinimumSize() {
        return minimumSize();
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>getMinimumSize()</code>.
     */
    @Deprecated
    public Dimension minimumSize() {
        synchronized (getTreeLock()) {
            return ((rows > 0) && (columns > 0)) ?
                        minimumSize(rows, columns) :
                        super.minimumSize();
        }
    }

    /**
     * Returns a string representing the state of this <code>TextArea</code>.
     * This method is intended to be used only for debugging purposes, and the
     * content and format of the returned string may vary between
     * implementations. The returned string may be empty but may not be
     * <code>null</code>.
     *
     * @return      the parameter string of this text area
     */
    protected String paramString() {
        String sbVisStr;
        switch (scrollbarVisibility) {
            case SCROLLBARS_BOTH:
                sbVisStr = "both";
                break;
            case SCROLLBARS_VERTICAL_ONLY:
                sbVisStr = "vertical-only";
                break;
            case SCROLLBARS_HORIZONTAL_ONLY:
                sbVisStr = "horizontal-only";
                break;
            case SCROLLBARS_NONE:
                sbVisStr = "none";
                break;
            default:
                sbVisStr = "invalid display policy";
        }

        return super.paramString() + ",rows=" + rows +
            ",columns=" + columns +
          ",scrollbarVisibility=" + sbVisStr;
    }


    /*
     * Serialization support.
     */
    /**
     * The textArea Serialized Data Version.
     *
     * @serial
     */
    private int textAreaSerializedDataVersion = 2;

    /**
     * Read the ObjectInputStream.
     * @exception HeadlessException if
     * <code>GraphicsEnvironment.isHeadless()</code> returns
     * <code>true</code>
     * @see GraphicsEnvironment#isHeadless
     */
    private void readObject(ObjectInputStream s)
      throws ClassNotFoundException, IOException, HeadlessException
    {
        // HeadlessException will be thrown by TextComponent's readObject
        s.defaultReadObject();

        // Make sure the state we just read in for columns, rows,
        // and scrollbarVisibility has legal values
        if (columns < 0) {
            columns = 0;
        }
        if (rows < 0) {
            rows = 0;
        }

        if ((scrollbarVisibility < SCROLLBARS_BOTH) ||
            (scrollbarVisibility > SCROLLBARS_NONE)) {
            this.scrollbarVisibility = SCROLLBARS_BOTH;
        }

        if (textAreaSerializedDataVersion < 2) {
            setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
                                  forwardTraversalKeys);
            setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
                                  backwardTraversalKeys);
        }
    }


/////////////////
// Accessibility support
////////////////


    /**
     * Returns the <code>AccessibleContext</code> associated with
     * this <code>TextArea</code>. For text areas, the
     * <code>AccessibleContext</code> takes the form of an
     * <code>AccessibleAWTTextArea</code>.
     * A new <code>AccessibleAWTTextArea</code> instance is created if necessary.
     *
     * @return an <code>AccessibleAWTTextArea</code> that serves as the
     *         <code>AccessibleContext</code> of this <code>TextArea</code>
     * @since 1.3
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleAWTTextArea();
        }
        return accessibleContext;
    }

    /**
     * This class implements accessibility support for the
     * <code>TextArea</code> class.  It provides an implementation of the
     * Java Accessibility API appropriate to text area user-interface elements.
     * @since 1.3
     */
    protected class AccessibleAWTTextArea extends AccessibleAWTTextComponent
    {
        /*
         * JDK 1.3 serialVersionUID
         */
        private static final long serialVersionUID = 3472827823632144419L;

        /**
         * Gets the state set of this object.
         *
         * @return an instance of AccessibleStateSet describing the states
         * of the object
         * @see AccessibleStateSet
         */
        public AccessibleStateSet getAccessibleStateSet() {
            AccessibleStateSet states = super.getAccessibleStateSet();
            states.add(AccessibleState.MULTI_LINE);
            return states;
        }
    }


}
