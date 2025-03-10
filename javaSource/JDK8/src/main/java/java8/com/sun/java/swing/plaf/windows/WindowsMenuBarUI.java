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

import com.sun.java.swing.plaf.windows.TMSchema.Part;
import com.sun.java.swing.plaf.windows.TMSchema.State;
import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;
import com.sun.java.swing.plaf.windows.WindowsMenuItemUI;
import com.sun.java.swing.plaf.windows.XPStyle.Skin;

import javax.swing.*;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuBarUI;
import java.awt.*;
import java.awt.event.*;

/**
 * Windows rendition of the component.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 */
public class WindowsMenuBarUI extends BasicMenuBarUI
{
    /* to be accessed on the EDT only */
    private WindowListener windowListener = null;
    private HierarchyListener hierarchyListener = null;
    private Window window = null;

    public static ComponentUI createUI(JComponent x) {
        return new com.sun.java.swing.plaf.windows.WindowsMenuBarUI();
    }

    @Override
    protected void uninstallListeners() {
        uninstallWindowListener();
        if (hierarchyListener != null) {
            menuBar.removeHierarchyListener(hierarchyListener);
            hierarchyListener = null;
        }
        super.uninstallListeners();
    }
    private void installWindowListener() {
        if (windowListener == null) {
            Component component = menuBar.getTopLevelAncestor();
            if (component instanceof Window) {
                window = (Window) component;
                windowListener = new WindowAdapter() {
                    @Override
                    public void windowActivated(WindowEvent e) {
                        menuBar.repaint();
                    }
                    @Override
                    public void windowDeactivated(WindowEvent e) {
                        menuBar.repaint();
                    }
                };
                ((Window) component).addWindowListener(windowListener);
            }
        }
    }
    private void uninstallWindowListener() {
        if (windowListener != null && window != null) {
            window.removeWindowListener(windowListener);
        }
        window = null;
        windowListener = null;
    }
    @Override
    protected void installListeners() {
        if (WindowsLookAndFeel.isOnVista()) {
            installWindowListener();
            hierarchyListener =
                new HierarchyListener() {
                    public void hierarchyChanged(HierarchyEvent e) {
                        if ((e.getChangeFlags()
                                & HierarchyEvent.DISPLAYABILITY_CHANGED) != 0) {
                            if (menuBar.isDisplayable()) {
                                installWindowListener();
                            } else {
                                uninstallWindowListener();
                            }
                        }
                    }
            };
            menuBar.addHierarchyListener(hierarchyListener);
        }
        super.installListeners();
    }

    protected void installKeyboardActions() {
        super.installKeyboardActions();
        ActionMap map = SwingUtilities.getUIActionMap(menuBar);
        if (map == null) {
            map = new ActionMapUIResource();
            SwingUtilities.replaceUIActionMap(menuBar, map);
        }
        map.put("takeFocus", new TakeFocus());
    }

    /**
     * Action that activates the menu (e.g. when F10 is pressed).
     * Unlike BasicMenuBarUI.TakeFocus, this Action will not show menu popup.
     */
    private static class TakeFocus extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            JMenuBar menuBar = (JMenuBar)e.getSource();
            JMenu menu = menuBar.getMenu(0);
            if (menu != null) {
                MenuSelectionManager msm =
                    MenuSelectionManager.defaultManager();
                MenuElement path[] = new MenuElement[2];
                path[0] = (MenuElement)menuBar;
                path[1] = (MenuElement)menu;
                msm.setSelectedPath(path);

                // show mnemonics
                WindowsLookAndFeel.setMnemonicHidden(false);
                WindowsLookAndFeel.repaintRootPane(menuBar);
            }
        }
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        if (WindowsMenuItemUI.isVistaPainting()) {
            XPStyle xp = XPStyle.getXP();
            Skin skin;
            skin = xp.getSkin(c, Part.MP_BARBACKGROUND);
            int width = c.getWidth();
            int height = c.getHeight();
            State state =  isActive(c) ? State.ACTIVE : State.INACTIVE;
            skin.paintSkin(g, 0, 0, width, height, state);
        } else {
            super.paint(g, c);
        }
    }

    /**
     * Checks if component belongs to an active window.
     * @param c component to check
     * @return true if component belongs to an active window
     */
    static boolean isActive(JComponent c) {
        JRootPane rootPane = c.getRootPane();
        if (rootPane != null) {
            Component component = rootPane.getParent();
            if (component instanceof Window) {
                return ((Window) component).isActive();
            }
        }
        return true;
    }
}
