/*
 * Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java8.javax.swing.plaf.metal;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.*;
import javax.swing.plaf.metal.MetalBorders;
import javax.swing.plaf.metal.MetalComboBoxButton;
import javax.swing.plaf.metal.MetalComboBoxEditor;
import javax.swing.plaf.metal.MetalComboBoxIcon;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalUtils;
import java.io.Serializable;
import java.beans.*;


/**
 * Metal UI for JComboBox
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running
 * the same version of Swing.  As of 1.4, support for long term storage
 * of all JavaBeans&trade;
 * has been added to the <code>java.beans</code> package.
 * Please see {@link XMLEncoder}.
 *
 * @see MetalComboBoxEditor
 * @see MetalComboBoxButton
 * @author Tom Santos
 */
public class MetalComboBoxUI extends BasicComboBoxUI {

    public static ComponentUI createUI(JComponent c) {
        return new javax.swing.plaf.metal.MetalComboBoxUI();
    }

    public void paint(Graphics g, JComponent c) {
        if (MetalLookAndFeel.usingOcean()) {
            super.paint(g, c);
        }
    }

    /**
     * If necessary paints the currently selected item.
     *
     * @param g Graphics to paint to
     * @param bounds Region to paint current value to
     * @param hasFocus whether or not the JComboBox has focus
     * @throws NullPointerException if any of the arguments are null.
     * @since 1.5
     */
    public void paintCurrentValue(Graphics g, Rectangle bounds,
                                  boolean hasFocus) {
        // This is really only called if we're using ocean.
        if (MetalLookAndFeel.usingOcean()) {
            bounds.x += 2;
            bounds.width -= 3;
            if (arrowButton != null) {
                Insets buttonInsets = arrowButton.getInsets();
                bounds.y += buttonInsets.top;
                bounds.height -= (buttonInsets.top + buttonInsets.bottom);
            }
            else {
                bounds.y += 2;
                bounds.height -= 4;
            }
            super.paintCurrentValue(g, bounds, hasFocus);
        }
        else if (g == null || bounds == null) {
            throw new NullPointerException(
                "Must supply a non-null Graphics and Rectangle");
        }
    }

    /**
     * If necessary paints the background of the currently selected item.
     *
     * @param g Graphics to paint to
     * @param bounds Region to paint background to
     * @param hasFocus whether or not the JComboBox has focus
     * @throws NullPointerException if any of the arguments are null.
     * @since 1.5
     */
    public void paintCurrentValueBackground(Graphics g, Rectangle bounds,
                                            boolean hasFocus) {
        // This is really only called if we're using ocean.
        if (MetalLookAndFeel.usingOcean()) {
            g.setColor(MetalLookAndFeel.getControlDarkShadow());
            g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height - 1);
            g.setColor(MetalLookAndFeel.getControlShadow());
            g.drawRect(bounds.x + 1, bounds.y + 1, bounds.width - 2,
                       bounds.height - 3);
            if (hasFocus && !isPopupVisible(comboBox) &&
                    arrowButton != null) {
                g.setColor(listBox.getSelectionBackground());
                Insets buttonInsets = arrowButton.getInsets();
                if (buttonInsets.top > 2) {
                    g.fillRect(bounds.x + 2, bounds.y + 2, bounds.width - 3,
                               buttonInsets.top - 2);
                }
                if (buttonInsets.bottom > 2) {
                    g.fillRect(bounds.x + 2, bounds.y + bounds.height -
                               buttonInsets.bottom, bounds.width - 3,
                               buttonInsets.bottom - 2);
                }
            }
        }
        else if (g == null || bounds == null) {
            throw new NullPointerException(
                "Must supply a non-null Graphics and Rectangle");
        }
    }

    /**
     * Returns the baseline.
     *
     * @throws NullPointerException {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     * @see JComponent#getBaseline(int, int)
     * @since 1.6
     */
    public int getBaseline(JComponent c, int width, int height) {
        int baseline;
        if (MetalLookAndFeel.usingOcean() && height >= 4) {
            height -= 4;
            baseline = super.getBaseline(c, width, height);
            if (baseline >= 0) {
                baseline += 2;
            }
        }
        else {
            baseline = super.getBaseline(c, width, height);
        }
        return baseline;
    }

    protected ComboBoxEditor createEditor() {
        return new MetalComboBoxEditor.UIResource();
    }

    protected ComboPopup createPopup() {
        return super.createPopup();
    }

    protected JButton createArrowButton() {
        boolean iconOnly = (comboBox.isEditable() ||
                            MetalLookAndFeel.usingOcean());
        JButton button = new MetalComboBoxButton( comboBox,
                                                  new MetalComboBoxIcon(),
                                                  iconOnly,
                                                  currentValuePane,
                                                  listBox );
        button.setMargin( new Insets( 0, 1, 1, 3 ) );
        if (MetalLookAndFeel.usingOcean()) {
            // Disabled rollover effect.
            button.putClientProperty(MetalBorders.NO_BUTTON_ROLLOVER,
                                     Boolean.TRUE);
        }
        updateButtonForOcean(button);
        return button;
    }

    /**
     * Resets the necessary state on the ComboBoxButton for ocean.
     */
    private void updateButtonForOcean(JButton button) {
        if (MetalLookAndFeel.usingOcean()) {
            // Ocean renders the focus in a different way, this
            // would be redundant.
            button.setFocusPainted(comboBox.isEditable());
        }
    }

    public PropertyChangeListener createPropertyChangeListener() {
        return new MetalPropertyChangeListener();
    }

    /**
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of {@code MetalComboBoxUI}.
     */
    public class MetalPropertyChangeListener extends PropertyChangeHandler {
        public void propertyChange(PropertyChangeEvent e) {
            super.propertyChange( e );
            String propertyName = e.getPropertyName();

            if ( propertyName == "editable" ) {
                if(arrowButton instanceof MetalComboBoxButton) {
                            MetalComboBoxButton button = (MetalComboBoxButton)arrowButton;
                            button.setIconOnly( comboBox.isEditable() ||
                                    MetalLookAndFeel.usingOcean() );
                }
                        comboBox.repaint();
                updateButtonForOcean(arrowButton);
            } else if ( propertyName == "background" ) {
                Color color = (Color)e.getNewValue();
                arrowButton.setBackground(color);
                listBox.setBackground(color);

            } else if ( propertyName == "foreground" ) {
                Color color = (Color)e.getNewValue();
                arrowButton.setForeground(color);
                listBox.setForeground(color);
            }
        }
    }

    /**
     * As of Java 2 platform v1.4 this method is no longer used. Do not call or
     * override. All the functionality of this method is in the
     * MetalPropertyChangeListener.
     *
     * @deprecated As of Java 2 platform v1.4.
     */
    @Deprecated
    protected void editablePropertyChanged( PropertyChangeEvent e ) { }

    protected LayoutManager createLayoutManager() {
        return new MetalComboBoxLayoutManager();
    }

    /**
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of {@code MetalComboBoxUI}.
     */
    public class MetalComboBoxLayoutManager extends ComboBoxLayoutManager {
        public void layoutContainer( Container parent ) {
            layoutComboBox( parent, this );
        }
        public void superLayout( Container parent ) {
            super.layoutContainer( parent );
        }
    }

    // This is here because of a bug in the compiler.
    // When a protected-inner-class-savvy compiler comes out we
    // should move this into MetalComboBoxLayoutManager.
    public void layoutComboBox( Container parent, MetalComboBoxLayoutManager manager ) {
        if (comboBox.isEditable() && !MetalLookAndFeel.usingOcean()) {
            manager.superLayout( parent );
            return;
        }

        if (arrowButton != null) {
            if (MetalLookAndFeel.usingOcean() ) {
                Insets insets = comboBox.getInsets();
                int buttonWidth = arrowButton.getMinimumSize().width;
                arrowButton.setBounds(javax.swing.plaf.metal.MetalUtils.isLeftToRight(comboBox)
                                ? (comboBox.getWidth() - insets.right - buttonWidth)
                                : insets.left,
                            insets.top, buttonWidth,
                            comboBox.getHeight() - insets.top - insets.bottom);
            }
            else {
                Insets insets = comboBox.getInsets();
                int width = comboBox.getWidth();
                int height = comboBox.getHeight();
                arrowButton.setBounds( insets.left, insets.top,
                                       width - (insets.left + insets.right),
                                       height - (insets.top + insets.bottom) );
            }
        }

        if (editor != null && MetalLookAndFeel.usingOcean()) {
            Rectangle cvb = rectangleForCurrentValue();
            editor.setBounds(cvb);
        }
    }

    /**
     * As of Java 2 platform v1.4 this method is no
     * longer used.
     *
     * @deprecated As of Java 2 platform v1.4.
     */
    @Deprecated
    protected void removeListeners() {
        if ( propertyChangeListener != null ) {
            comboBox.removePropertyChangeListener( propertyChangeListener );
        }
    }

    // These two methods were overloaded and made public. This was probably a
    // mistake in the implementation. The functionality that they used to
    // provide is no longer necessary and should be removed. However,
    // removing them will create an uncompatible API change.

    public void configureEditor() {
        super.configureEditor();
    }

    public void unconfigureEditor() {
        super.unconfigureEditor();
    }

    public Dimension getMinimumSize( JComponent c ) {
        if ( !isMinimumSizeDirty ) {
            return new Dimension( cachedMinimumSize );
        }

        Dimension size = null;

        if ( !comboBox.isEditable() &&
             arrowButton != null) {
            Insets buttonInsets = arrowButton.getInsets();
            Insets insets = comboBox.getInsets();

            size = getDisplaySize();
            size.width += insets.left + insets.right;
            size.width += buttonInsets.right;
            size.width += arrowButton.getMinimumSize().width;
            size.height += insets.top + insets.bottom;
            size.height += buttonInsets.top + buttonInsets.bottom;
        }
        else if ( comboBox.isEditable() &&
                  arrowButton != null &&
                  editor != null ) {
            size = super.getMinimumSize( c );
            Insets margin = arrowButton.getMargin();
            size.height += margin.top + margin.bottom;
            size.width += margin.left + margin.right;
        }
        else {
            size = super.getMinimumSize( c );
        }

        cachedMinimumSize.setSize( size.width, size.height );
        isMinimumSizeDirty = false;

        return new Dimension( cachedMinimumSize );
    }

    /**
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of {@code MetalComboBoxUI}.
     *
     * This class is now obsolete and doesn't do anything and
     * is only included for backwards API compatibility. Do not call or
     * override.
     *
     * @deprecated As of Java 2 platform v1.4.
     */
    @Deprecated
    public class MetalComboPopup extends BasicComboPopup {

        public MetalComboPopup( JComboBox cBox) {
            super( cBox );
        }

        // This method was overloaded and made public. This was probably
        // mistake in the implementation. The functionality that they used to
        // provide is no longer necessary and should be removed. However,
        // removing them will create an uncompatible API change.

        public void delegateFocus(MouseEvent e) {
            super.delegateFocus(e);
        }
    }
}
