/*
 * Copyright (c) 1997, 2008, Oracle and/or its affiliates. All rights reserved.
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
import javax.swing.undo.StateEditable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * <P>StateEdit is a general edit for objects that change state.
 * Objects being edited must conform to the StateEditable interface.</P>
 *
 * <P>This edit class works by asking an object to store it's state in
 * Hashtables before and after editing occurs.  Upon undo or redo the
 * object is told to restore it's state from these Hashtables.</P>
 *
 * A state edit is used as follows:
 * <PRE>
 *      // Create the edit during the "before" state of the object
 *      StateEdit newEdit = new StateEdit(myObject);
 *      // Modify the object
 *      myObject.someStateModifyingMethod();
 *      // "end" the edit when you are done modifying the object
 *      newEdit.end();
 * </PRE>
 *
 * <P><EM>Note that when a StateEdit ends, it removes redundant state from
 * the Hashtables - A state Hashtable is not guaranteed to contain all
 * keys/values placed into it when the state is stored!</EM></P>
 *
 * @see StateEditable
 *
 * @author Ray Ryan
 */

public class StateEdit
        extends AbstractUndoableEdit {

    protected static final String RCSID = "$Id: StateEdit.java,v 1.6 1997/10/01 20:05:51 sandipc Exp $";

    //
    // Attributes
    //

    /**
     * The object being edited
     */
    protected StateEditable object;

    /**
     * The state information prior to the edit
     */
    protected Hashtable<Object,Object> preState;

    /**
     * The state information after the edit
     */
    protected Hashtable<Object,Object> postState;

    /**
     * The undo/redo presentation name
     */
    protected String undoRedoName;

    //
    // Constructors
    //

    /**
     * Create and return a new StateEdit.
     *
     * @param anObject The object to watch for changing state
     *
     * @see javax.swing.undo.StateEdit
     */
    public StateEdit(StateEditable anObject) {
        super();
        init (anObject,null);
    }

    /**
     * Create and return a new StateEdit with a presentation name.
     *
     * @param anObject The object to watch for changing state
     * @param name The presentation name to be used for this edit
     *
     * @see javax.swing.undo.StateEdit
     */
    public StateEdit(StateEditable anObject, String name) {
        super();
        init (anObject,name);
    }

    protected void init (StateEditable anObject, String name) {
        this.object = anObject;
        this.preState = new Hashtable<Object, Object>(11);
        this.object.storeState(this.preState);
        this.postState = null;
        this.undoRedoName = name;
    }


    //
    // Operation
    //


    /**
     * Gets the post-edit state of the StateEditable object and
     * ends the edit.
     */
    public void end() {
        this.postState = new Hashtable<Object, Object>(11);
        this.object.storeState(this.postState);
        this.removeRedundantState();
    }

    /**
     * Tells the edited object to apply the state prior to the edit
     */
    public void undo() {
        super.undo();
        this.object.restoreState(preState);
    }

    /**
     * Tells the edited object to apply the state after the edit
     */
    public void redo() {
        super.redo();
        this.object.restoreState(postState);
    }

    /**
     * Gets the presentation name for this edit
     */
    public String getPresentationName() {
        return this.undoRedoName;
    }


    //
    // Internal support
    //

    /**
     * Remove redundant key/values in state hashtables.
     */
    protected void removeRedundantState() {
        Vector<Object> uselessKeys = new Vector<Object>();
        Enumeration myKeys = preState.keys();

        // Locate redundant state
        while (myKeys.hasMoreElements()) {
            Object myKey = myKeys.nextElement();
            if (postState.containsKey(myKey) &&
                postState.get(myKey).equals(preState.get(myKey))) {
                uselessKeys.addElement(myKey);
            }
        }

        // Remove redundant state
        for (int i = uselessKeys.size()-1; i >= 0; i--) {
            Object myKey = uselessKeys.elementAt(i);
            preState.remove(myKey);
            postState.remove(myKey);
        }
    }

} // End of class StateEdit
