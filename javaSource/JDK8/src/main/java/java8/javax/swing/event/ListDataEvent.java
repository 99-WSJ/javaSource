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

package java8.javax.swing.event;

import java.util.EventObject;


/**
 * Defines an event that encapsulates changes to a list.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running
 * the same version of Swing.  As of 1.4, support for long term storage
 * of all JavaBeans&trade;
 * has been added to the <code>java.beans</code> package.
 * Please see {@link java.beans.XMLEncoder}.
 *
 * @author Hans Muller
 */
@SuppressWarnings("serial")
public class ListDataEvent extends EventObject
{
    /** Identifies one or more changes in the lists contents. */
    public static final int CONTENTS_CHANGED = 0;
    /** Identifies the addition of one or more contiguous items to the list */
    public static final int INTERVAL_ADDED = 1;
    /** Identifies the removal of one or more contiguous items from the list */
    public static final int INTERVAL_REMOVED = 2;

    private int type;
    private int index0;
    private int index1;

    /**
     * Returns the event type. The possible values are:
     * <ul>
     * <li> {@link #CONTENTS_CHANGED}
     * <li> {@link #INTERVAL_ADDED}
     * <li> {@link #INTERVAL_REMOVED}
     * </ul>
     *
     * @return an int representing the type value
     */
    public int getType() { return type; }

    /**
     * Returns the lower index of the range. For a single
     * element, this value is the same as that returned by {@link #getIndex1}.

     *
     * @return an int representing the lower index value
     */
    public int getIndex0() { return index0; }
    /**
     * Returns the upper index of the range. For a single
     * element, this value is the same as that returned by {@link #getIndex0}.
     *
     * @return an int representing the upper index value
     */
    public int getIndex1() { return index1; }

    /**
     * Constructs a ListDataEvent object. If index0 is &gt;
     * index1, index0 and index1 will be swapped such that
     * index0 will always be &lt;= index1.
     *
     * @param source  the source Object (typically <code>this</code>)
     * @param type    an int specifying {@link #CONTENTS_CHANGED},
     *                {@link #INTERVAL_ADDED}, or {@link #INTERVAL_REMOVED}
     * @param index0  one end of the new interval
     * @param index1  the other end of the new interval
     */
    public ListDataEvent(Object source, int type, int index0, int index1) {
        super(source);
        this.type = type;
        this.index0 = Math.min(index0, index1);
        this.index1 = Math.max(index0, index1);
    }

    /**
     * Returns a string representation of this ListDataEvent. This method
     * is intended to be used only for debugging purposes, and the
     * content and format of the returned string may vary between
     * implementations. The returned string may be empty but may not
     * be <code>null</code>.
     *
     * @since 1.4
     * @return  a string representation of this ListDataEvent.
     */
    public String toString() {
        return getClass().getName() +
        "[type=" + type +
        ",index0=" + index0 +
        ",index1=" + index1 + "]";
    }
}
