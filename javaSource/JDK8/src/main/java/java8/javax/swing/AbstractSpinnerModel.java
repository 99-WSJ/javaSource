/*
 * Copyright (c) 2000, 2008, Oracle and/or its affiliates. All rights reserved.
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

package java8.javax.swing;

import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.Serializable;


/**
 * This class provides the ChangeListener part of the
 * SpinnerModel interface that should be suitable for most concrete SpinnerModel
 * implementations.  Subclasses must provide an implementation of the
 * <code>setValue</code>, <code>getValue</code>, <code>getNextValue</code> and
 * <code>getPreviousValue</code> methods.
 *
 * @see JSpinner
 * @see SpinnerModel
 * @see SpinnerListModel
 * @see SpinnerNumberModel
 * @see SpinnerDateModel
 *
 * @author Hans Muller
 * @since 1.4
 */
public abstract class AbstractSpinnerModel implements SpinnerModel, Serializable
{

    /**
     * Only one ChangeEvent is needed per model instance since the
     * event's only (read-only) state is the source property.  The source
     * of events generated here is always "this".
     */
    private transient ChangeEvent changeEvent = null;


    /**
     * The list of ChangeListeners for this model.  Subclasses may
     * store their own listeners here.
     */
    protected EventListenerList listenerList = new EventListenerList();


    /**
     * Adds a ChangeListener to the model's listener list.  The
     * ChangeListeners must be notified when the models value changes.
     *
     * @param l the ChangeListener to add
     * @see #removeChangeListener
     * @see SpinnerModel#addChangeListener
     */
    public void addChangeListener(ChangeListener l) {
        listenerList.add(ChangeListener.class, l);
    }


    /**
     * Removes a ChangeListener from the model's listener list.
     *
     * @param l the ChangeListener to remove
     * @see #addChangeListener
     * @see SpinnerModel#removeChangeListener
     */
    public void removeChangeListener(ChangeListener l) {
        listenerList.remove(ChangeListener.class, l);
    }


    /**
     * Returns an array of all the <code>ChangeListener</code>s added
     * to this AbstractSpinnerModel with addChangeListener().
     *
     * @return all of the <code>ChangeListener</code>s added or an empty
     *         array if no listeners have been added
     * @since 1.4
     */
    public ChangeListener[] getChangeListeners() {
        return listenerList.getListeners(ChangeListener.class);
    }


    /**
     * Run each ChangeListeners stateChanged() method.
     *
     * @see #setValue
     * @see EventListenerList
     */
    protected void fireStateChanged()
    {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -=2 ) {
            if (listeners[i] == ChangeListener.class) {
                if (changeEvent == null) {
                    changeEvent = new ChangeEvent(this);
                }
                ((ChangeListener)listeners[i+1]).stateChanged(changeEvent);
            }
        }
    }


    /**
     * Return an array of all the listeners of the given type that
     * were added to this model.  For example to find all of the
     * ChangeListeners added to this model:
     * <pre>
     * myAbstractSpinnerModel.getListeners(ChangeListener.class);
     * </pre>
     *
     * @param listenerType the type of listeners to return, e.g. ChangeListener.class
     * @return all of the objects receiving <em>listenerType</em> notifications
     *         from this model
     */
    public <T extends EventListener> T[] getListeners(Class<T> listenerType) {
        return listenerList.getListeners(listenerType);
    }
}
