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

package java8.java.awt.dnd;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.InvalidDnDOperationException;
import java.util.List;

/**
 * The <code>DropTargetDragEvent</code> is delivered to a
 * <code>DropTargetListener</code> via its
 * dragEnter() and dragOver() methods.
 * <p>
 * The <code>DropTargetDragEvent</code> reports the <i>source drop actions</i>
 * and the <i>user drop action</i> that reflect the current state of
 * the drag operation.
 * <p>
 * <i>Source drop actions</i> is a bitwise mask of <code>DnDConstants</code>
 * that represents the set of drop actions supported by the drag source for
 * this drag operation.
 * <p>
 * <i>User drop action</i> depends on the drop actions supported by the drag
 * source and the drop action selected by the user. The user can select a drop
 * action by pressing modifier keys during the drag operation:
 * <pre>
 *   Ctrl + Shift -&gt; ACTION_LINK
 *   Ctrl         -&gt; ACTION_COPY
 *   Shift        -&gt; ACTION_MOVE
 * </pre>
 * If the user selects a drop action, the <i>user drop action</i> is one of
 * <code>DnDConstants</code> that represents the selected drop action if this
 * drop action is supported by the drag source or
 * <code>DnDConstants.ACTION_NONE</code> if this drop action is not supported
 * by the drag source.
 * <p>
 * If the user doesn't select a drop action, the set of
 * <code>DnDConstants</code> that represents the set of drop actions supported
 * by the drag source is searched for <code>DnDConstants.ACTION_MOVE</code>,
 * then for <code>DnDConstants.ACTION_COPY</code>, then for
 * <code>DnDConstants.ACTION_LINK</code> and the <i>user drop action</i> is the
 * first constant found. If no constant is found the <i>user drop action</i>
 * is <code>DnDConstants.ACTION_NONE</code>.
 *
 * @since 1.2
 */

public class DropTargetDragEvent extends DropTargetEvent {

    private static final long serialVersionUID = -8422265619058953682L;

    /**
     * Construct a <code>DropTargetDragEvent</code> given the
     * <code>DropTargetContext</code> for this operation,
     * the location of the "Drag" <code>Cursor</code>'s hotspot
     * in the <code>Component</code>'s coordinates, the
     * user drop action, and the source drop actions.
     * <P>
     * @param dtc        The DropTargetContext for this operation
     * @param cursorLocn The location of the "Drag" Cursor's
     * hotspot in Component coordinates
     * @param dropAction The user drop action
     * @param srcActions The source drop actions
     *
     * @throws NullPointerException if cursorLocn is null
     * @throws IllegalArgumentException if dropAction is not one of
     *         <code>DnDConstants</code>.
     * @throws IllegalArgumentException if srcActions is not
     *         a bitwise mask of <code>DnDConstants</code>.
     * @throws IllegalArgumentException if dtc is <code>null</code>.
     */

    public DropTargetDragEvent(DropTargetContext dtc, Point cursorLocn, int dropAction, int srcActions)  {
        super(dtc);

        if (cursorLocn == null) throw new NullPointerException("cursorLocn");

        if (dropAction != DnDConstants.ACTION_NONE &&
            dropAction != DnDConstants.ACTION_COPY &&
            dropAction != DnDConstants.ACTION_MOVE &&
            dropAction != DnDConstants.ACTION_LINK
        ) throw new IllegalArgumentException("dropAction" + dropAction);

        if ((srcActions & ~(DnDConstants.ACTION_COPY_OR_MOVE | DnDConstants.ACTION_LINK)) != 0) throw new IllegalArgumentException("srcActions");

        location        = cursorLocn;
        actions         = srcActions;
        this.dropAction = dropAction;
    }

    /**
     * This method returns a <code>Point</code>
     * indicating the <code>Cursor</code>'s current
     * location within the <code>Component'</code>s
     * coordinates.
     * <P>
     * @return the current cursor location in
     * <code>Component</code>'s coords.
     */

    public Point getLocation() {
        return location;
    }


    /**
     * This method returns the current <code>DataFlavor</code>s from the
     * <code>DropTargetContext</code>.
     * <P>
     * @return current DataFlavors from the DropTargetContext
     */

    public DataFlavor[] getCurrentDataFlavors() {
        return getDropTargetContext().getCurrentDataFlavors();
    }

    /**
     * This method returns the current <code>DataFlavor</code>s
     * as a <code>java.util.List</code>
     * <P>
     * @return a <code>java.util.List</code> of the Current <code>DataFlavor</code>s
     */

    public List<DataFlavor> getCurrentDataFlavorsAsList() {
        return getDropTargetContext().getCurrentDataFlavorsAsList();
    }

    /**
     * This method returns a <code>boolean</code> indicating
     * if the specified <code>DataFlavor</code> is supported.
     * <P>
     * @param df the <code>DataFlavor</code> to test
     * <P>
     * @return if a particular DataFlavor is supported
     */

    public boolean isDataFlavorSupported(DataFlavor df) {
        return getDropTargetContext().isDataFlavorSupported(df);
    }

    /**
     * This method returns the source drop actions.
     *
     * @return the source drop actions
     */
    public int getSourceActions() { return actions; }

    /**
     * This method returns the user drop action.
     *
     * @return the user drop action
     */
    public int getDropAction() { return dropAction; }

    /**
     * This method returns the Transferable object that represents
     * the data associated with the current drag operation.
     *
     * @return the Transferable associated with the drag operation
     * @throws InvalidDnDOperationException if the data associated with the drag
     *         operation is not available
     *
     * @since 1.5
     */
    public Transferable getTransferable() {
        return getDropTargetContext().getTransferable();
    }

    /**
     * Accepts the drag.
     *
     * This method should be called from a
     * <code>DropTargetListeners</code> <code>dragEnter</code>,
     * <code>dragOver</code>, and <code>dropActionChanged</code>
     * methods if the implementation wishes to accept an operation
     * from the srcActions other than the one selected by
     * the user as represented by the <code>dropAction</code>.
     *
     * @param dragOperation the operation accepted by the target
     */
    public void acceptDrag(int dragOperation) {
        getDropTargetContext().acceptDrag(dragOperation);
    }

    /**
     * Rejects the drag as a result of examining either the
     * <code>dropAction</code> or the available <code>DataFlavor</code>
     * types.
     */
    public void rejectDrag() {
        getDropTargetContext().rejectDrag();
    }

    /*
     * fields
     */

    /**
     * The location of the drag cursor's hotspot in Component coordinates.
     *
     * @serial
     */
    private Point               location;

    /**
     * The source drop actions.
     *
     * @serial
     */
    private int                 actions;

    /**
     * The user drop action.
     *
     * @serial
     */
    private int                 dropAction;
}
