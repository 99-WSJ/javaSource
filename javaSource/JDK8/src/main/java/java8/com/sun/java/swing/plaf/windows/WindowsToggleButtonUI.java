/*
 * Copyright (c) 1997, 2006, Oracle and/or its affiliates. All rights reserved.
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

package java8.sun.java.swing.plaf.windows;

import com.sun.java.swing.plaf.windows.WindowsButtonUI;
import com.sun.java.swing.plaf.windows.WindowsGraphicsUtils;
import sun.awt.AppContext;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.plaf.basic.BasicToggleButtonUI;
import java.awt.*;


/**
 * A Windows toggle button.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @author Jeff Dinkins
 */
public class WindowsToggleButtonUI extends BasicToggleButtonUI
{
    protected int dashedRectGapX;
    protected int dashedRectGapY;
    protected int dashedRectGapWidth;
    protected int dashedRectGapHeight;

    protected Color focusColor;

    private static final Object WINDOWS_TOGGLE_BUTTON_UI_KEY = new Object();

    private boolean defaults_initialized = false;

    public static ComponentUI createUI(JComponent b) {
        AppContext appContext = AppContext.getAppContext();
        com.sun.java.swing.plaf.windows.WindowsToggleButtonUI windowsToggleButtonUI =
                (com.sun.java.swing.plaf.windows.WindowsToggleButtonUI) appContext.get(WINDOWS_TOGGLE_BUTTON_UI_KEY);
        if (windowsToggleButtonUI == null) {
            windowsToggleButtonUI = new com.sun.java.swing.plaf.windows.WindowsToggleButtonUI();
            appContext.put(WINDOWS_TOGGLE_BUTTON_UI_KEY, windowsToggleButtonUI);
        }
        return windowsToggleButtonUI;
    }


    // ********************************
    //            Defaults
    // ********************************
    protected void installDefaults(AbstractButton b) {
        super.installDefaults(b);
        if(!defaults_initialized) {
            String pp = getPropertyPrefix();
            dashedRectGapX = ((Integer)UIManager.get("Button.dashedRectGapX")).intValue();
            dashedRectGapY = ((Integer)UIManager.get("Button.dashedRectGapY")).intValue();
            dashedRectGapWidth = ((Integer)UIManager.get("Button.dashedRectGapWidth")).intValue();
            dashedRectGapHeight = ((Integer)UIManager.get("Button.dashedRectGapHeight")).intValue();
            focusColor = UIManager.getColor(pp + "focus");
            defaults_initialized = true;
        }

        XPStyle xp = XPStyle.getXP();
        if (xp != null) {
            b.setBorder(xp.getBorder(b, WindowsButtonUI.getXPButtonType(b)));
            LookAndFeel.installProperty(b, "opaque", Boolean.FALSE);
            LookAndFeel.installProperty(b, "rolloverEnabled", Boolean.TRUE);
        }
    }

    protected void uninstallDefaults(AbstractButton b) {
        super.uninstallDefaults(b);
        defaults_initialized = false;
    }


    protected Color getFocusColor() {
        return focusColor;
    }


    // ********************************
    //         Paint Methods
    // ********************************

    private transient Color cachedSelectedColor = null;
    private transient Color cachedBackgroundColor = null;
    private transient Color cachedHighlightColor = null;

    protected void paintButtonPressed(Graphics g, AbstractButton b) {
        if (XPStyle.getXP() == null && b.isContentAreaFilled()) {
            Color oldColor = g.getColor();
            Color c1 = b.getBackground();
            Color c2 = UIManager.getColor("ToggleButton.highlight");
            if (c1 != cachedBackgroundColor || c2 != cachedHighlightColor) {
                int r1 = c1.getRed(), r2 = c2.getRed();
                int g1 = c1.getGreen(), g2 = c2.getGreen();
                int b1 = c1.getBlue(), b2 = c2.getBlue();
                cachedSelectedColor = new Color(
                        Math.min(r1, r2) + Math.abs(r1 - r2) / 2,
                        Math.min(g1, g2) + Math.abs(g1 - g2) / 2,
                        Math.min(b1, b2) + Math.abs(b1 - b2) / 2
                );
                cachedBackgroundColor = c1;
                cachedHighlightColor = c2;
            }
            g.setColor(cachedSelectedColor);
            g.fillRect(0, 0, b.getWidth(), b.getHeight());
            g.setColor(oldColor);
        }
    }

    public void paint(Graphics g, JComponent c) {
        if (XPStyle.getXP() != null) {
            WindowsButtonUI.paintXPButtonBackground(g, c);
        }
        super.paint(g, c);
    }


    /**
     * Overridden method to render the text without the mnemonic
     */
    protected void paintText(Graphics g, AbstractButton b, Rectangle textRect, String text) {
        WindowsGraphicsUtils.paintText(g, b, textRect, text, getTextShiftOffset());
    }

    protected void paintFocus(Graphics g, AbstractButton b,
                              Rectangle viewRect, Rectangle textRect, Rectangle iconRect) {
        g.setColor(getFocusColor());
        BasicGraphicsUtils.drawDashedRect(g, dashedRectGapX, dashedRectGapY,
                                          b.getWidth() - dashedRectGapWidth,
                                          b.getHeight() - dashedRectGapHeight);
    }

    // ********************************
    //          Layout Methods
    // ********************************
    public Dimension getPreferredSize(JComponent c) {
        Dimension d = super.getPreferredSize(c);

        /* Ensure that the width and height of the button is odd,
         * to allow for the focus line if focus is painted
         */
        AbstractButton b = (AbstractButton)c;
        if (d != null && b.isFocusPainted()) {
            if(d.width % 2 == 0) { d.width += 1; }
            if(d.height % 2 == 0) { d.height += 1; }
        }
        return d;
    }
}
