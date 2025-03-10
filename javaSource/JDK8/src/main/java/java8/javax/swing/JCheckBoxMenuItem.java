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
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.UIDefaults;
import javax.swing.plaf.*;
import javax.accessibility.*;


/**
 * A menu item that can be selected or deselected. If selected, the menu
 * item typically appears with a checkmark next to it. If unselected or
 * deselected, the menu item appears without a checkmark. Like a regular
 * menu item, a check box menu item can have either text or a graphic
 * icon associated with it, or both.
 * <p>
 * Either <code>isSelected</code>/<code>setSelected</code> or
 * <code>getState</code>/<code>setState</code> can be used
 * to determine/specify the menu item's selection state. The
 * preferred methods are <code>isSelected</code> and
 * <code>setSelected</code>, which work for all menus and buttons.
 * The <code>getState</code> and <code>setState</code> methods exist for
 * compatibility with other component sets.
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
 * For further information and examples of using check box menu items,
 * see <a
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
 * description: A menu item which can be selected or deselected.
 *
 * @author Georges Saab
 * @author David Karlton
 */
public class JCheckBoxMenuItem extends JMenuItem implements SwingConstants,
        Accessible
{
    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "CheckBoxMenuItemUI";

    /**
     * Creates an initially unselected check box menu item with no set text or icon.
     */
    public JCheckBoxMenuItem() {
        this(null, null, false);
    }

    /**
     * Creates an initially unselected check box menu item with an icon.
     *
     * @param icon the icon of the CheckBoxMenuItem.
     */
    public JCheckBoxMenuItem(Icon icon) {
        this(null, icon, false);
    }

    /**
     * Creates an initially unselected check box menu item with text.
     *
     * @param text the text of the CheckBoxMenuItem
     */
    public JCheckBoxMenuItem(String text) {
        this(text, null, false);
    }

    /**
     * Creates a menu item whose properties are taken from the
     * Action supplied.
     *
     * @since 1.3
     */
    public JCheckBoxMenuItem(Action a) {
        this();
        setAction(a);
    }

    /**
     * Creates an initially unselected check box menu item with the specified text and icon.
     *
     * @param text the text of the CheckBoxMenuItem
     * @param icon the icon of the CheckBoxMenuItem
     */
    public JCheckBoxMenuItem(String text, Icon icon) {
        this(text, icon, false);
    }

    /**
     * Creates a check box menu item with the specified text and selection state.
     *
     * @param text the text of the check box menu item.
     * @param b the selected state of the check box menu item
     */
    public JCheckBoxMenuItem(String text, boolean b) {
        this(text, null, b);
    }

    /**
     * Creates a check box menu item with the specified text, icon, and selection state.
     *
     * @param text the text of the check box menu item
     * @param icon the icon of the check box menu item
     * @param b the selected state of the check box menu item
     */
    public JCheckBoxMenuItem(String text, Icon icon, boolean b) {
        super(text, icon);
        setModel(new JToggleButton.ToggleButtonModel());
        setSelected(b);
        setFocusable(false);
    }

    /**
     * Returns the name of the L&amp;F class
     * that renders this component.
     *
     * @return "CheckBoxMenuItemUI"
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     */
    public String getUIClassID() {
        return uiClassID;
    }

     /**
      * Returns the selected-state of the item. This method
      * exists for AWT compatibility only.  New code should
      * use isSelected() instead.
      *
      * @return true  if the item is selected
      */
    public boolean getState() {
        return isSelected();
    }

    /**
     * Sets the selected-state of the item. This method
     * exists for AWT compatibility only.  New code should
     * use setSelected() instead.
     *
     * @param b  a boolean value indicating the item's
     *           selected-state, where true=selected
     * @beaninfo
     * description: The selection state of the check box menu item
     *      hidden: true
     */
    public synchronized void setState(boolean b) {
        setSelected(b);
    }


    /**
     * Returns an array (length 1) containing the check box menu item
     * label or null if the check box is not selected.
     *
     * @return an array containing one Object -- the text of the menu item
     *         -- if the item is selected; otherwise null
     */
    public Object[] getSelectedObjects() {
        if (isSelected() == false)
            return null;
        Object[] selectedObjects = new Object[1];
        selectedObjects[0] = getText();
        return selectedObjects;
    }

    /**
     * See readObject() and writeObject() in JComponent for more
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
     * Returns a string representation of this JCheckBoxMenuItem. This method
     * is intended to be used only for debugging purposes, and the
     * content and format of the returned string may vary between
     * implementations. The returned string may be empty but may not
     * be <code>null</code>.
     *
     * @return  a string representation of this JCheckBoxMenuItem.
     */
    protected String paramString() {
        return super.paramString();
    }

    /**
     * Overriden to return true, JCheckBoxMenuItem supports
     * the selected state.
     */
    boolean shouldUpdateSelectedStateFromAction() {
        return true;
    }

/////////////////
// Accessibility support
////////////////

    /**
     * Gets the AccessibleContext associated with this JCheckBoxMenuItem.
     * For JCheckBoxMenuItems, the AccessibleContext takes the form of an
     * AccessibleJCheckBoxMenuItem.
     * A new AccessibleJCheckBoxMenuItem instance is created if necessary.
     *
     * @return an AccessibleJCheckBoxMenuItem that serves as the
     *         AccessibleContext of this AccessibleJCheckBoxMenuItem
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleJCheckBoxMenuItem();
        }
        return accessibleContext;
    }

    /**
     * This class implements accessibility support for the
     * <code>JCheckBoxMenuItem</code> class.  It provides an implementation
     * of the Java Accessibility API appropriate to checkbox menu item
     * user-interface elements.
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
    protected class AccessibleJCheckBoxMenuItem extends AccessibleJMenuItem {
        /**
         * Get the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of the
         * object
         */
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.CHECK_BOX;
        }
    } // inner class AccessibleJCheckBoxMenuItem
}
