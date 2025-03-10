/*
 * Copyright (c) 2004, 2006, Oracle and/or its affiliates. All rights reserved.
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

import com.sun.java.swing.plaf.windows.TMSchema.Part;
import com.sun.java.swing.plaf.windows.TMSchema.State;
import com.sun.java.swing.plaf.windows.WindowsMenuItemUI;
import com.sun.java.swing.plaf.windows.WindowsPopupMenuUI;
import com.sun.java.swing.plaf.windows.XPStyle.Skin;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPopupMenuSeparatorUI;
import java.awt.*;

/**
 * Windows L&F implementation of PopupMenuSeparatorUI.
 *
 * @author Leif Samuelsson
 * @author Igor Kushnirskiy
 */

public class WindowsPopupMenuSeparatorUI extends BasicPopupMenuSeparatorUI {

    public static ComponentUI createUI(JComponent c) {
        return new com.sun.java.swing.plaf.windows.WindowsPopupMenuSeparatorUI();
    }

    public void paint(Graphics g, JComponent c) {
        Dimension s = c.getSize();
        if (WindowsMenuItemUI.isVistaPainting()) {
            int x = 1;
            Component parent = c.getParent();
            if (parent instanceof JComponent) {
                Object gutterOffsetObject =
                    ((JComponent) parent).getClientProperty(
                        WindowsPopupMenuUI.GUTTER_OFFSET_KEY);
                if (gutterOffsetObject instanceof Integer) {
                    /*
                     * gutter offset is in parent's coordinates.
                     * See comment in
                     * WindowsPopupMenuUI.getTextOffset(JComponent)
                     */
                    x = ((Integer) gutterOffsetObject).intValue() - c.getX();
                    x += WindowsPopupMenuUI.getGutterWidth();
                }
            }
            Skin skin = XPStyle.getXP().getSkin(c, Part.MP_POPUPSEPARATOR);
            int skinHeight = skin.getHeight();
            int y = (s.height - skinHeight) / 2;
            skin.paintSkin(g, x, y, s.width - x - 1, skinHeight, State.NORMAL);
        } else {
            int y = s.height / 2;
            g.setColor(c.getForeground());
            g.drawLine(1, y - 1, s.width - 2, y - 1);

            g.setColor(c.getBackground());
            g.drawLine(1, y,     s.width - 2, y);
        }
    }

    public Dimension getPreferredSize(JComponent c) {
        int fontHeight = 0;
        Font font = c.getFont();
        if (font != null) {
            fontHeight = c.getFontMetrics(font).getHeight();
        }

        return new Dimension(0, fontHeight/2 + 2);
    }

}
