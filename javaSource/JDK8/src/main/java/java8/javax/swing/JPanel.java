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

import java.awt.*;

import javax.swing.*;
import javax.swing.UIDefaults;
import javax.swing.plaf.*;
import javax.accessibility.*;

import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;


/**
 * <code>JPanel</code> is a generic lightweight container.
 * For examples and task-oriented documentation for JPanel, see
 * <a
 href="http://docs.oracle.com/javase/tutorial/uiswing/components/panel.html">How to Use Panels</a>,
 * a section in <em>The Java Tutorial</em>.
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
 * description: A generic lightweight container.
 *
 * @author Arnaud Weber
 * @author Steve Wilson
 */
public class JPanel extends JComponent implements Accessible
{
    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "PanelUI";

    /**
     * Creates a new JPanel with the specified layout manager and buffering
     * strategy.
     *
     * @param layout  the LayoutManager to use
     * @param isDoubleBuffered  a boolean, true for double-buffering, which
     *        uses additional memory space to achieve fast, flicker-free
     *        updates
     */
    public JPanel(LayoutManager layout, boolean isDoubleBuffered) {
        setLayout(layout);
        setDoubleBuffered(isDoubleBuffered);
        setUIProperty("opaque", Boolean.TRUE);
        updateUI();
    }

    /**
     * Create a new buffered JPanel with the specified layout manager
     *
     * @param layout  the LayoutManager to use
     */
    public JPanel(LayoutManager layout) {
        this(layout, true);
    }

    /**
     * Creates a new <code>JPanel</code> with <code>FlowLayout</code>
     * and the specified buffering strategy.
     * If <code>isDoubleBuffered</code> is true, the <code>JPanel</code>
     * will use a double buffer.
     *
     * @param isDoubleBuffered  a boolean, true for double-buffering, which
     *        uses additional memory space to achieve fast, flicker-free
     *        updates
     */
    public JPanel(boolean isDoubleBuffered) {
        this(new FlowLayout(), isDoubleBuffered);
    }

    /**
     * Creates a new <code>JPanel</code> with a double buffer
     * and a flow layout.
     */
    public JPanel() {
        this(true);
    }

    /**
     * Resets the UI property with a value from the current look and feel.
     *
     * @see JComponent#updateUI
     */
    public void updateUI() {
        setUI((PanelUI)UIManager.getUI(this));
    }

    /**
     * Returns the look and feel (L&amp;amp;F) object that renders this component.
     *
     * @return the PanelUI object that renders this component
     * @since 1.4
     */
    public PanelUI getUI() {
        return (PanelUI)ui;
    }


    /**
     * Sets the look and feel (L&amp;F) object that renders this component.
     *
     * @param ui  the PanelUI L&amp;F object
     * @see UIDefaults#getUI
     * @since 1.4
     * @beaninfo
     *        bound: true
     *       hidden: true
     *    attribute: visualUpdate true
     *  description: The UI object that implements the Component's LookAndFeel.
     */
    public void setUI(PanelUI ui) {
        super.setUI(ui);
    }

    /**
     * Returns a string that specifies the name of the L&amp;F class
     * that renders this component.
     *
     * @return "PanelUI"
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     * @beaninfo
     *        expert: true
     *   description: A string that specifies the name of the L&amp;F class.
     */
    public String getUIClassID() {
        return uiClassID;
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
     * Returns a string representation of this JPanel. This method
     * is intended to be used only for debugging purposes, and the
     * content and format of the returned string may vary between
     * implementations. The returned string may be empty but may not
     * be <code>null</code>.
     *
     * @return  a string representation of this JPanel.
     */
    protected String paramString() {
        return super.paramString();
    }

/////////////////
// Accessibility support
////////////////

    /**
     * Gets the AccessibleContext associated with this JPanel.
     * For JPanels, the AccessibleContext takes the form of an
     * AccessibleJPanel.
     * A new AccessibleJPanel instance is created if necessary.
     *
     * @return an AccessibleJPanel that serves as the
     *         AccessibleContext of this JPanel
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleJPanel();
        }
        return accessibleContext;
    }

    /**
     * This class implements accessibility support for the
     * <code>JPanel</code> class.  It provides an implementation of the
     * Java Accessibility API appropriate to panel user-interface
     * elements.
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
    protected class AccessibleJPanel extends AccessibleJComponent {

        /**
         * Get the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of the
         * object
         */
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.PANEL;
        }
    }
}
