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
package java8.javax.swing.text;

import java.awt.event.ActionEvent;
import java.awt.KeyboardFocusManager;
import java.awt.Component;
import java.util.Hashtable;
import java.util.Enumeration;
import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.swing.text.JTextComponent;

/**
 * An Action implementation useful for key bindings that are
 * shared across a number of different text components.  Because
 * the action is shared, it must have a way of getting it's
 * target to act upon.  This class provides support to try and
 * find a text component to operate on.  The preferred way of
 * getting the component to act upon is through the ActionEvent
 * that is received.  If the Object returned by getSource can
 * be narrowed to a text component, it will be used.  If the
 * action event is null or can't be narrowed, the last focused
 * text component is tried.  This is determined by being
 * used in conjunction with a JTextController which
 * arranges to share that information with a TextAction.
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
 * @author  Timothy Prinzing
 */
public abstract class TextAction extends AbstractAction {

    /**
     * Creates a new JTextAction object.
     *
     * @param name the name of the action
     */
    public TextAction(String name) {
        super(name);
    }

    /**
     * Determines the component to use for the action.
     * This if fetched from the source of the ActionEvent
     * if it's not null and can be narrowed.  Otherwise,
     * the last focused component is used.
     *
     * @param e the ActionEvent
     * @return the component
     */
    protected final JTextComponent getTextComponent(ActionEvent e) {
        if (e != null) {
            Object o = e.getSource();
            if (o instanceof JTextComponent) {
                return (JTextComponent) o;
            }
        }
        return getFocusedComponent();
    }

    /**
     * Takes one list of
     * commands and augments it with another list
     * of commands.  The second list takes precedence
     * over the first list; that is, when both lists
     * contain a command with the same name, the command
     * from the second list is used.
     *
     * @param list1 the first list, may be empty but not
     *              <code>null</code>
     * @param list2 the second list, may be empty but not
     *              <code>null</code>
     * @return the augmented list
     */
    public static final Action[] augmentList(Action[] list1, Action[] list2) {
        Hashtable<String, Action> h = new Hashtable<String, Action>();
        for (Action a : list1) {
            String value = (String)a.getValue(Action.NAME);
            h.put((value!=null ? value:""), a);
        }
        for (Action a : list2) {
            String value = (String)a.getValue(Action.NAME);
            h.put((value!=null ? value:""), a);
        }
        Action[] actions = new Action[h.size()];
        int index = 0;
        for (Enumeration e = h.elements() ; e.hasMoreElements() ;) {
            actions[index++] = (Action) e.nextElement();
        }
        return actions;
    }

    /**
     * Fetches the text component that currently has focus.
     * This allows actions to be shared across text components
     * which is useful for key-bindings where a large set of
     * actions are defined, but generally used the same way
     * across many different components.
     *
     * @return the component
     */
    protected final JTextComponent getFocusedComponent() {
        return JTextComponent.getFocusedComponent();
    }
}
