/*
 * Copyright (c) 1997, 2004, Oracle and/or its affiliates. All rights reserved.
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
package java8.javax.swing.undo;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;
import java.util.*;

/**
 * A concrete subclass of AbstractUndoableEdit, used to assemble little
 * UndoableEdits into great big ones.
 *
 * @author Ray Ryan
 */
public class CompoundEdit extends AbstractUndoableEdit {
    /**
     * True if this edit has never received <code>end</code>.
     */
    boolean inProgress;

    /**
     * The collection of <code>UndoableEdit</code>s
     * undone/redone en masse by this <code>CompoundEdit</code>.
     */
    protected Vector<UndoableEdit> edits;

    public CompoundEdit() {
        super();
        inProgress = true;
        edits = new Vector<UndoableEdit>();
    }

    /**
     * Sends <code>undo</code> to all contained
     * <code>UndoableEdits</code> in the reverse of
     * the order in which they were added.
     */
    public void undo() throws CannotUndoException {
        super.undo();
        int i = edits.size();
        while (i-- > 0) {
            UndoableEdit e = edits.elementAt(i);
            e.undo();
        }
    }

    /**
     * Sends <code>redo</code> to all contained
     * <code>UndoableEdit</code>s in the order in
     * which they were added.
     */
    public void redo() throws CannotRedoException {
        super.redo();
        Enumeration cursor = edits.elements();
        while (cursor.hasMoreElements()) {
            ((UndoableEdit)cursor.nextElement()).redo();
        }
    }

    /**
     * Returns the last <code>UndoableEdit</code> in
     * <code>edits</code>, or <code>null</code>
     * if <code>edits</code> is empty.
     */
    protected UndoableEdit lastEdit() {
        int count = edits.size();
        if (count > 0)
            return edits.elementAt(count-1);
        else
            return null;
    }

    /**
     * Sends <code>die</code> to each subedit,
     * in the reverse of the order that they were added.
     */
    public void die() {
        int size = edits.size();
        for (int i = size-1; i >= 0; i--)
        {
            UndoableEdit e = edits.elementAt(i);
//          System.out.println("CompoundEdit(" + i + "): Discarding " +
//                             e.getUndoPresentationName());
            e.die();
        }
        super.die();
    }

    /**
     * If this edit is <code>inProgress</code>,
     * accepts <code>anEdit</code> and returns true.
     *
     * <p>The last edit added to this <code>CompoundEdit</code>
     * is given a chance to <code>addEdit(anEdit)</code>.
     * If it refuses (returns false), <code>anEdit</code> is
     * given a chance to <code>replaceEdit</code> the last edit.
     * If <code>anEdit</code> returns false here,
     * it is added to <code>edits</code>.
     *
     * @param anEdit the edit to be added
     * @return true if the edit is <code>inProgress</code>;
     *  otherwise returns false
     */
    public boolean addEdit(UndoableEdit anEdit) {
        if (!inProgress) {
            return false;
        } else {
            UndoableEdit last = lastEdit();

            // If this is the first subedit received, just add it.
            // Otherwise, give the last one a chance to absorb the new
            // one.  If it won't, give the new one a chance to absorb
            // the last one.

            if (last == null) {
                edits.addElement(anEdit);
            }
            else if (!last.addEdit(anEdit)) {
                if (anEdit.replaceEdit(last)) {
                    edits.removeElementAt(edits.size()-1);
                }
                edits.addElement(anEdit);
            }

            return true;
        }
    }

    /**
     * Sets <code>inProgress</code> to false.
     *
     * @see #canUndo
     * @see #canRedo
     */
    public void end() {
        inProgress = false;
    }

    /**
     * Returns false if <code>isInProgress</code> or if super
     * returns false.
     *
     * @see     #isInProgress
     */
    public boolean canUndo() {
        return !isInProgress() && super.canUndo();
    }

    /**
     * Returns false if <code>isInProgress</code> or if super
     * returns false.
     *
     * @see     #isInProgress
     */
    public boolean canRedo() {
        return !isInProgress() && super.canRedo();
    }

    /**
     * Returns true if this edit is in progress--that is, it has not
     * received end. This generally means that edits are still being
     * added to it.
     *
     * @see     #end
     */
    public boolean isInProgress() {
        return inProgress;
    }

    /**
     * Returns true if any of the <code>UndoableEdit</code>s
     * in <code>edits</code> do.
     * Returns false if they all return false.
     */
    public boolean  isSignificant() {
        Enumeration cursor = edits.elements();
        while (cursor.hasMoreElements()) {
            if (((UndoableEdit)cursor.nextElement()).isSignificant()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns <code>getPresentationName</code> from the
     * last <code>UndoableEdit</code> added to
     * <code>edits</code>. If <code>edits</code> is empty,
     * calls super.
     */
    public String getPresentationName() {
        UndoableEdit last = lastEdit();
        if (last != null) {
            return last.getPresentationName();
        } else {
            return super.getPresentationName();
        }
    }

    /**
     * Returns <code>getUndoPresentationName</code>
     * from the last <code>UndoableEdit</code>
     * added to <code>edits</code>.
     * If <code>edits</code> is empty, calls super.
     */
    public String getUndoPresentationName() {
        UndoableEdit last = lastEdit();
        if (last != null) {
            return last.getUndoPresentationName();
        } else {
            return super.getUndoPresentationName();
        }
    }

    /**
     * Returns <code>getRedoPresentationName</code>
     * from the last <code>UndoableEdit</code>
     * added to <code>edits</code>.
     * If <code>edits</code> is empty, calls super.
     */
    public String getRedoPresentationName() {
        UndoableEdit last = lastEdit();
        if (last != null) {
            return last.getRedoPresentationName();
        } else {
            return super.getRedoPresentationName();
        }
    }

    /**
     * Returns a string that displays and identifies this
     * object's properties.
     *
     * @return a String representation of this object
     */
    public String toString()
    {
        return super.toString()
            + " inProgress: " + inProgress
            + " edits: " + edits;
    }
}
