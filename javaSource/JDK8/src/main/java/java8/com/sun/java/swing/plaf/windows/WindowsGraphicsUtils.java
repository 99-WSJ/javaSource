/*
 * Copyright (c) 2000, 2013, Oracle and/or its affiliates. All rights reserved.
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
import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;
import sun.swing.SwingUtilities2;

import javax.swing.*;
import javax.swing.plaf.UIResource;
import java.awt.*;

import static com.sun.java.swing.plaf.windows.TMSchema.*;

/**
 * A collection of static utility methods used for rendering the Windows look
 * and feel.
 *
 * @author Mark Davidson
 * @since 1.4
 */
public class WindowsGraphicsUtils {

    /**
     * Renders a text String in Windows without the mnemonic.
     * This is here because the WindowsUI hierarchy doesn't match the Component hierarchy. All
     * the overriden paintText methods of the ButtonUI delegates will call this static method.
     * <p>
     * @param g Graphics context
     * @param b Current button to render
     * @param textRect Bounding rectangle to render the text.
     * @param text String to render
     */
    public static void paintText(Graphics g, AbstractButton b,
                                        Rectangle textRect, String text,
                                        int textShiftOffset) {
        FontMetrics fm = SwingUtilities2.getFontMetrics(b, g);

        int mnemIndex = b.getDisplayedMnemonicIndex();
        // W2K Feature: Check to see if the Underscore should be rendered.
        if (WindowsLookAndFeel.isMnemonicHidden() == true) {
            mnemIndex = -1;
        }

        XPStyle xp = XPStyle.getXP();
        if (xp != null && !(b instanceof JMenuItem)) {
            paintXPText(b, g, textRect.x + textShiftOffset,
                        textRect.y + fm.getAscent() + textShiftOffset,
                        text, mnemIndex);
        } else {
            paintClassicText(b, g, textRect.x + textShiftOffset,
                             textRect.y + fm.getAscent() + textShiftOffset,
                             text, mnemIndex);
        }
    }

    static void paintClassicText(AbstractButton b, Graphics g, int x, int y,
                                 String text, int mnemIndex) {
        ButtonModel model = b.getModel();

        /* Draw the Text */
        Color color = b.getForeground();
        if(model.isEnabled()) {
            /*** paint the text normally */
            if(!(b instanceof JMenuItem && model.isArmed())
                && !(b instanceof JMenu && (model.isSelected() || model.isRollover()))) {
                /* We shall not set foreground color for selected menu or
                 * armed menuitem. Foreground must be set in appropriate
                 * Windows* class because these colors passes from
                 * BasicMenuItemUI as protected fields and we can't
                 * reach them from this class */
                g.setColor(b.getForeground());
            }
            SwingUtilities2.drawStringUnderlineCharAt(b, g,text, mnemIndex, x, y);
        } else {        /*** paint the text disabled ***/
            color        = UIManager.getColor("Button.shadow");
            Color shadow = UIManager.getColor("Button.disabledShadow");
            if(model.isArmed()) {
                color = UIManager.getColor("Button.disabledForeground");
            } else {
                if (shadow == null) {
                    shadow = b.getBackground().darker();
                }
                g.setColor(shadow);
                SwingUtilities2.drawStringUnderlineCharAt(b, g, text, mnemIndex,
                                                          x + 1, y + 1);
            }
            if (color == null) {
                color = b.getBackground().brighter();
            }
            g.setColor(color);
            SwingUtilities2.drawStringUnderlineCharAt(b, g, text, mnemIndex, x, y);
        }
    }

    static void paintXPText(AbstractButton b, Graphics g, int x, int y,
                            String text, int mnemIndex) {
        Part part = WindowsButtonUI.getXPButtonType(b);
        State state = WindowsButtonUI.getXPButtonState(b);
        paintXPText(b, part, state, g, x, y, text, mnemIndex);
    }

    static void paintXPText(AbstractButton b, Part part, State state,
            Graphics g, int x, int y, String text, int mnemIndex) {
        XPStyle xp = XPStyle.getXP();
        Color textColor = b.getForeground();

        if (textColor instanceof UIResource) {
            textColor = xp.getColor(b, part, state, Prop.TEXTCOLOR, b.getForeground());
            // to work around an apparent bug in Windows, use the pushbutton
            // color for disabled toolbar buttons if the disabled color is the
            // same as the enabled color
            if (part == Part.TP_BUTTON && state == State.DISABLED) {
                Color enabledColor = xp.getColor(b, part, State.NORMAL,
                                     Prop.TEXTCOLOR, b.getForeground());
                if(textColor.equals(enabledColor)) {
                    textColor = xp.getColor(b, Part.BP_PUSHBUTTON, state,
                                Prop.TEXTCOLOR, textColor);
                }
            }
            // only draw shadow if developer hasn't changed the foreground color
            // and if the current style has text shadows.
            TypeEnum shadowType = xp.getTypeEnum(b, part,
                                                 state, Prop.TEXTSHADOWTYPE);
            if (shadowType == TypeEnum.TST_SINGLE ||
                        shadowType == TypeEnum.TST_CONTINUOUS) {
                Color shadowColor = xp.getColor(b, part, state,
                                                Prop.TEXTSHADOWCOLOR, Color.black);
                Point offset = xp.getPoint(b, part, state, Prop.TEXTSHADOWOFFSET);
                if (offset != null) {
                    g.setColor(shadowColor);
                    SwingUtilities2.drawStringUnderlineCharAt(b, g, text, mnemIndex,
                                                              x + offset.x,
                                                              y + offset.y);
                }
            }
        }

        g.setColor(textColor);
        SwingUtilities2.drawStringUnderlineCharAt(b, g, text, mnemIndex, x, y);
    }

    static boolean isLeftToRight(Component c) {
        return c.getComponentOrientation().isLeftToRight();
    }

    /*
     * Repaints all the components with the mnemonics in the given window and
     * all its owned windows.
     */
    static void repaintMnemonicsInWindow(Window w) {
        if(w == null || !w.isShowing()) {
            return;
        }

        Window[] ownedWindows = w.getOwnedWindows();
        for(int i=0;i<ownedWindows.length;i++) {
            repaintMnemonicsInWindow(ownedWindows[i]);
        }

        repaintMnemonicsInContainer(w);
    }

    /*
     * Repaints all the components with the mnemonics in container.
     * Recursively searches for all the subcomponents.
     */
    static void repaintMnemonicsInContainer(Container cont) {
        Component c;
        for(int i=0; i<cont.getComponentCount(); i++) {
            c = cont.getComponent(i);
            if(c == null || !c.isVisible()) {
                continue;
            }
            if(c instanceof AbstractButton
               && ((AbstractButton)c).getMnemonic() != '\0') {
                c.repaint();
                continue;
            } else if(c instanceof JLabel
                      && ((JLabel)c).getDisplayedMnemonic() != '\0') {
                c.repaint();
                continue;
            }
            if(c instanceof Container) {
                repaintMnemonicsInContainer((Container)c);
            }
        }
    }
}
