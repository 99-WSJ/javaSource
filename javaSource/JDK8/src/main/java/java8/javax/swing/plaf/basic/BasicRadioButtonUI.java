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

package java8.javax.swing.plaf.basic;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.plaf.basic.BasicToggleButtonUI;
import javax.swing.text.View;
import sun.swing.SwingUtilities2;
import sun.awt.AppContext;


/**
 * RadioButtonUI implementation for BasicRadioButtonUI
 *
 * @author Jeff Dinkins
 */
public class BasicRadioButtonUI extends BasicToggleButtonUI
{
    private static final Object BASIC_RADIO_BUTTON_UI_KEY = new Object();

    protected Icon icon;

    private boolean defaults_initialized = false;

    private final static String propertyPrefix = "RadioButton" + ".";

    // ********************************
    //        Create PLAF
    // ********************************
    public static ComponentUI createUI(JComponent b) {
        AppContext appContext = AppContext.getAppContext();
        javax.swing.plaf.basic.BasicRadioButtonUI radioButtonUI =
                (javax.swing.plaf.basic.BasicRadioButtonUI) appContext.get(BASIC_RADIO_BUTTON_UI_KEY);
        if (radioButtonUI == null) {
            radioButtonUI = new javax.swing.plaf.basic.BasicRadioButtonUI();
            appContext.put(BASIC_RADIO_BUTTON_UI_KEY, radioButtonUI);
        }
        return radioButtonUI;
    }

    protected String getPropertyPrefix() {
        return propertyPrefix;
    }

    // ********************************
    //        Install PLAF
    // ********************************
    protected void installDefaults(AbstractButton b){
        super.installDefaults(b);
        if(!defaults_initialized) {
            icon = UIManager.getIcon(getPropertyPrefix() + "icon");
            defaults_initialized = true;
        }
    }

    // ********************************
    //        Uninstall PLAF
    // ********************************
    protected void uninstallDefaults(AbstractButton b){
        super.uninstallDefaults(b);
        defaults_initialized = false;
    }

    public Icon getDefaultIcon() {
        return icon;
    }


    /* These Dimensions/Rectangles are allocated once for all
     * RadioButtonUI.paint() calls.  Re-using rectangles
     * rather than allocating them in each paint call substantially
     * reduced the time it took paint to run.  Obviously, this
     * method can't be re-entered.
     */
    private static Dimension size = new Dimension();
    private static Rectangle viewRect = new Rectangle();
    private static Rectangle iconRect = new Rectangle();
    private static Rectangle textRect = new Rectangle();

    /**
     * paint the radio button
     */
    public synchronized void paint(Graphics g, JComponent c) {
        AbstractButton b = (AbstractButton) c;
        ButtonModel model = b.getModel();

        Font f = c.getFont();
        g.setFont(f);
        FontMetrics fm = SwingUtilities2.getFontMetrics(c, g, f);

        Insets i = c.getInsets();
        size = b.getSize(size);
        viewRect.x = i.left;
        viewRect.y = i.top;
        viewRect.width = size.width - (i.right + viewRect.x);
        viewRect.height = size.height - (i.bottom + viewRect.y);
        iconRect.x = iconRect.y = iconRect.width = iconRect.height = 0;
        textRect.x = textRect.y = textRect.width = textRect.height = 0;

        Icon altIcon = b.getIcon();
        Icon selectedIcon = null;
        Icon disabledIcon = null;

        String text = SwingUtilities.layoutCompoundLabel(
            c, fm, b.getText(), altIcon != null ? altIcon : getDefaultIcon(),
            b.getVerticalAlignment(), b.getHorizontalAlignment(),
            b.getVerticalTextPosition(), b.getHorizontalTextPosition(),
            viewRect, iconRect, textRect,
            b.getText() == null ? 0 : b.getIconTextGap());

        // fill background
        if(c.isOpaque()) {
            g.setColor(b.getBackground());
            g.fillRect(0,0, size.width, size.height);
        }


        // Paint the radio button
        if(altIcon != null) {

            if(!model.isEnabled()) {
                if(model.isSelected()) {
                   altIcon = b.getDisabledSelectedIcon();
                } else {
                   altIcon = b.getDisabledIcon();
                }
            } else if(model.isPressed() && model.isArmed()) {
                altIcon = b.getPressedIcon();
                if(altIcon == null) {
                    // Use selected icon
                    altIcon = b.getSelectedIcon();
                }
            } else if(model.isSelected()) {
                if(b.isRolloverEnabled() && model.isRollover()) {
                        altIcon = b.getRolloverSelectedIcon();
                        if (altIcon == null) {
                                altIcon = b.getSelectedIcon();
                        }
                } else {
                        altIcon = b.getSelectedIcon();
                }
            } else if(b.isRolloverEnabled() && model.isRollover()) {
                altIcon = b.getRolloverIcon();
            }

            if(altIcon == null) {
                altIcon = b.getIcon();
            }

            altIcon.paintIcon(c, g, iconRect.x, iconRect.y);

        } else {
            getDefaultIcon().paintIcon(c, g, iconRect.x, iconRect.y);
        }


        // Draw the Text
        if(text != null) {
            View v = (View) c.getClientProperty(BasicHTML.propertyKey);
            if (v != null) {
                v.paint(g, textRect);
            } else {
                paintText(g, b, textRect, text);
            }
            if(b.hasFocus() && b.isFocusPainted() &&
               textRect.width > 0 && textRect.height > 0 ) {
                paintFocus(g, textRect, size);
            }
        }
    }

    protected void paintFocus(Graphics g, Rectangle textRect, Dimension size){
    }


    /* These Insets/Rectangles are allocated once for all
     * RadioButtonUI.getPreferredSize() calls.  Re-using rectangles
     * rather than allocating them in each call substantially
     * reduced the time it took getPreferredSize() to run.  Obviously,
     * this method can't be re-entered.
     */
    private static Rectangle prefViewRect = new Rectangle();
    private static Rectangle prefIconRect = new Rectangle();
    private static Rectangle prefTextRect = new Rectangle();
    private static Insets prefInsets = new Insets(0, 0, 0, 0);

    /**
     * The preferred size of the radio button
     */
    public Dimension getPreferredSize(JComponent c) {
        if(c.getComponentCount() > 0) {
            return null;
        }

        AbstractButton b = (AbstractButton) c;

        String text = b.getText();

        Icon buttonIcon = b.getIcon();
        if(buttonIcon == null) {
            buttonIcon = getDefaultIcon();
        }

        Font font = b.getFont();
        FontMetrics fm = b.getFontMetrics(font);

        prefViewRect.x = prefViewRect.y = 0;
        prefViewRect.width = Short.MAX_VALUE;
        prefViewRect.height = Short.MAX_VALUE;
        prefIconRect.x = prefIconRect.y = prefIconRect.width = prefIconRect.height = 0;
        prefTextRect.x = prefTextRect.y = prefTextRect.width = prefTextRect.height = 0;

        SwingUtilities.layoutCompoundLabel(
            c, fm, text, buttonIcon,
            b.getVerticalAlignment(), b.getHorizontalAlignment(),
            b.getVerticalTextPosition(), b.getHorizontalTextPosition(),
            prefViewRect, prefIconRect, prefTextRect,
            text == null ? 0 : b.getIconTextGap());

        // find the union of the icon and text rects (from Rectangle.java)
        int x1 = Math.min(prefIconRect.x, prefTextRect.x);
        int x2 = Math.max(prefIconRect.x + prefIconRect.width,
                          prefTextRect.x + prefTextRect.width);
        int y1 = Math.min(prefIconRect.y, prefTextRect.y);
        int y2 = Math.max(prefIconRect.y + prefIconRect.height,
                          prefTextRect.y + prefTextRect.height);
        int width = x2 - x1;
        int height = y2 - y1;

        prefInsets = b.getInsets(prefInsets);
        width += prefInsets.left + prefInsets.right;
        height += prefInsets.top + prefInsets.bottom;
        return new Dimension(width, height);
    }
}
