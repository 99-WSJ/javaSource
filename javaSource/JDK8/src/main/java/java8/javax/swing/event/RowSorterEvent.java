/*
 * Copyright (c) 2005, Oracle and/or its affiliates. All rights reserved.
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

import javax.swing.RowSorter;

/**
 * <code>RowSorterEvent</code> provides notification of changes to
 * a <code>RowSorter</code>.  Two types of notification are possible:
 * <ul>
 * <li><code>Type.SORT_ORDER_CHANGED</code>: indicates the sort order has
 *     changed.  This is typically followed by a notification of:
 * <li><code>Type.SORTED</code>: indicates the contents of the model have
 *     been transformed in some way.  For example, the contents may have
 *     been sorted or filtered.
 * </ul>
 *
 * @see RowSorter
 * @since 1.6
 */
public class RowSorterEvent extends java.util.EventObject {
    private Type type;
    private int[] oldViewToModel;

    /**
     * Enumeration of the types of <code>RowSorterEvent</code>s.
     *
     * @since 1.6
     */
    public enum Type {
        /**
         * Indicates the sort order has changed.
         */
        SORT_ORDER_CHANGED,

        /**
         * Indicates the contents have been newly sorted or
         * transformed in some way.
         */
        SORTED
    }

    /**
     * Creates a <code>RowSorterEvent</code> of type
     * <code>SORT_ORDER_CHANGED</code>.
     *
     * @param source the source of the change
     * @throws IllegalArgumentException if <code>source</code> is
     *         <code>null</code>
     */
    public RowSorterEvent(RowSorter source) {
        this(source, Type.SORT_ORDER_CHANGED, null);
    }

    /**
     * Creates a <code>RowSorterEvent</code>.
     *
     * @param source the source of the change
     * @param type the type of event
     * @param previousRowIndexToModel the mapping from model indices to
     *        view indices prior to the sort, may be <code>null</code>
     * @throws IllegalArgumentException if source or <code>type</code> is
     *         <code>null</code>
     */
    public RowSorterEvent(RowSorter source, Type type,
                          int[] previousRowIndexToModel) {
        super(source);
        if (type == null) {
            throw new IllegalArgumentException("type must be non-null");
        }
        this.type = type;
        this.oldViewToModel = previousRowIndexToModel;
    }

    /**
     * Returns the source of the event as a <code>RowSorter</code>.
     *
     * @return the source of the event as a <code>RowSorter</code>
     */
    public RowSorter getSource() {
        return (RowSorter)super.getSource();
    }

    /**
     * Returns the type of event.
     *
     * @return the type of event
     */
    public Type getType() {
        return type;
    }

    /**
     * Returns the location of <code>index</code> in terms of the
     * model prior to the sort.  This method is only useful for events
     * of type <code>SORTED</code>.  This method will return -1 if the
     * index is not valid, or the locations prior to the sort have not
     * been provided.
     *
     * @param index the index in terms of the view
     * @return the index in terms of the model prior to the sort, or -1 if
     *         the location is not valid or the mapping was not provided.
     */
    public int convertPreviousRowIndexToModel(int index) {
        if (oldViewToModel != null && index >= 0 &&
                index < oldViewToModel.length) {
            return oldViewToModel[index];
        }
        return -1;
    }

    /**
     * Returns the number of rows before the sort.  This method is only
     * useful for events of type <code>SORTED</code> and if the
     * last locations have not been provided will return 0.
     *
     * @return the number of rows in terms of the view prior to the sort
     */
    public int getPreviousRowCount() {
        return (oldViewToModel == null) ? 0 : oldViewToModel.length;
    }
}
