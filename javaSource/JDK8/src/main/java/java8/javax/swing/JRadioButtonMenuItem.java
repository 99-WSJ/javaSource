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
package java8.javax.swing;

import java.util.EventListener;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

import javax.swing.*;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JToggleButton;
import javax.swing.UIDefaults;
import javax.swing.plaf.*;
import javax.accessibility.*;

/**
 * An implementation of a radio button menu item.
 * A <code>JRadioButtonMenuItem</code> is
 * a menu item that is part of a group of menu items in which only one
 * item in the group can be selected. The selected item displays its
 * selected state. Selecting it causes any other selected item to
 * switch to the unselected state.
 * To control the selected state of a group of radio button menu items,
 * use a <code>ButtonGroup</code> object.
 * <p>
 * Menu items can be configured, and to some degree controlled, by
 * <code><a href="Action.html">Action</a></code>s.  Using an
 * <code>Action</code> with a menu item has many benefits beyond directly
 * configuring a menu item.  Refer to <a href="Action.html#buttonActions">
 * Swing Components Supporting <code>Action</code></a> for more
 * details, and you can find more information in <a
 * href="http://docs.oracle.com/javase/tutorial/uiswing/misc/action.html">How
 * to Use Actions</a>, a section in <em>The Java Tutorial</em>.
 * <p>
 * For further documentation and examples see
 * <a
 href="http://docs.oracle.com/javase/tutorial/uiswing/components/menu.html">How to Use Menus</a>,
 * a section in <em>The Java Tutorial.</em>
 * <p>
 * <strong>Warning:</strong> Swing is not thread safe. For more
 * information see <a
 * href="package-summary.html#threading">Swing's Threading
 * Policy</a>.
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
 * @beaninfo
 *   attribute: isContainer false
 * description: A component within a group of menu items which can be selected.
 *
 * @author Georges Saab
 * @author David Karlton
 * @see ButtonGroup
 */
public class JRadioButtonMenuItem extends JMenuItem implements Accessible {
    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "RadioButtonMenuItemUI";

    /**
     * Creates a <code>JRadioButtonMenuItem</code> with no set text or icon.
     */
    public JRadioButtonMenuItem() {
        this(null, null, false);
    }

    /**
     * Creates a <code>JRadioButtonMenuItem</code> with an icon.
     *
     * @param icon the <code>Icon</code> to display on the
     *          <code>JRadioButtonMenuItem</code>
     */
    public JRadioButtonMenuItem(Icon icon) {
        this(null, icon, false);
    }

    /**
     * Creates a <code>JRadioButtonMenuItem</code> with text.
     *
     * @param text the text of the <code>JRadioButtonMenuItem</code>
     */
    public JRadioButtonMenuItem(String text) {
        this(text, null, false);
    }

    /**
     * Creates a radio button menu item whose properties are taken from the
     * <code>Action</code> supplied.
     *
     * @param  a the <code>Action</code> on which to base the radio
     *          button menu item
     *
     * @since 1.3
     */
    public JRadioButtonMenuItem(Action a) {
        this();
        setAction(a);
    }

    /**
     * Creates a radio button menu item with the specified text
     * and <code>Icon</code>.
     *
     * @param text the text of the <code>JRadioButtonMenuItem</code>
     * @param icon the icon to display on the <code>JRadioButtonMenuItem</code>
     */
    public JRadioButtonMenuItem(String text, Icon icon) {
        this(text, icon, false);
    }

    /**
     * Creates a radio button menu item with the specified text
     * and selection state.
     *
     * @param text the text of the <code>CheckBoxMenuItem</code>
     * @param selected the selected state of the <code>CheckBoxMenuItem</code>
     */
    public JRadioButtonMenuItem(String text, boolean selected) {
        this(text);
        setSelected(selected);
    }

    /**
     * Creates a radio button menu item with the specified image
     * and selection state, but no text.
     *
     * @param icon  the image that the button should display
     * @param selected  if true, the button is initially selected;
     *                  otherwise, the button is initially unselected
     */
    public JRadioButtonMenuItem(Icon icon, boolean selected) {
        this(null, icon, selected);
    }

    /**
     * Creates a radio button menu item that has the specified
     * text, image, and selection state.  All other constructors
     * defer to this one.
     *
     * @param text  the string displayed on the radio button
     * @param icon  the image that the button should display
     */
    public JRadioButtonMenuItem(String text, Icon icon, boolean selected) {
        super(text, icon);
        setModel(new JToggleButton.ToggleButtonModel());
        setSelected(selected);
        setFocusable(false);
    }

    /**
     * Returns the name of the L&amp;F class that renders this component.
     *
     * @return the string "RadioButtonMenuItemUI"
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     */
    public String getUIClassID() {
        return uiClassID;
    }

    /**
     * See <code>readObject</code> and <code>writeObject</code> in
     * <code>JComponent</code> for more
     * information about serialization in Swing.
     */
    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        if (getUIClassID().equals(uiClassID)) {
            byte count = JComponent.getWriteObjCounter(this);
            JComponent.setWriteObjCounter(this, --count);
            if (count == 0 && ui != null) {
                ui.installUI(this);
            }
        }
    }


    /**
     * Returns a string representation of this
     * <code>JRadioButtonMenuItem</code>.  This method
     * is intended to be used only for debugging purposes, and the
     * content and format of the returned string may vary between
     * implementations. The returned string may be empty but may not
     * be <code>null</code>.
     *
     * @return  a string representation of this
     *          <code>JRadioButtonMenuItem</code>
     */
    protected String paramString() {
        return super.paramString();
    }

    /**
     * Overriden to return true, JRadioButtonMenuItem supports
     * the selected state.
     */
    boolean shouldUpdateSelectedStateFromAction() {
        return true;
    }

/////////////////
// Accessibility support
////////////////

    /**
     * Gets the AccessibleContext associated with this JRadioButtonMenuItem.
     * For JRadioButtonMenuItems, the AccessibleContext takes the form of an
     * AccessibleJRadioButtonMenuItem.
     * A new AccessibleJRadioButtonMenuItem instance is created if necessary.
     *
     * @return an AccessibleJRadioButtonMenuItem that serves as the
     *         AccessibleContext of this JRadioButtonMenuItem
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleJRadioButtonMenuItem();
        }
        return accessibleContext;
    }

    /**
     * This class implements accessibility support for the
     * <code>JRadioButtonMenuItem</code> class.  It provides an
     * implementation of the Java Accessibility API appropriate to
     * <code>JRadioButtonMenuItem</code> user-interface elements.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases. The current serialization support is
     * appropriate for short term storage or RMI between applications running
     * the same version of Swing.  As of 1.4, support for long term storage
     * of all JavaBeans&trade;
     * has been added to the <code>java.beans</code> package.
     * Please see {@link java.beans.XMLEncoder}.
     */
    protected class AccessibleJRadioButtonMenuItem extends AccessibleJMenuItem {
        /**
         * Get the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of the
         * object
         */
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.RADIO_BUTTON;
        }
    } // inner class AccessibleJRadioButtonMenuItem
}
