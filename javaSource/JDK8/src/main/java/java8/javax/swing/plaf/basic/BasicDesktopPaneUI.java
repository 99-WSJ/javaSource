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

package java8.javax.swing.plaf.basic;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.LazyActionMap;

import java.beans.*;

import java.awt.event.*;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Graphics;
import java.awt.KeyboardFocusManager;
import java.awt.*;
import java.util.Vector;
import sun.swing.DefaultLookup;
import sun.swing.UIAction;
import sun.awt.AppContext;

/**
 * Basic L&amp;F for a desktop.
 *
 * @author Steve Wilson
 */
public class BasicDesktopPaneUI extends DesktopPaneUI {
    // Old actions forward to an instance of this.
    private static final Actions SHARED_ACTION = new Actions();
    private static Dimension minSize = new Dimension(0,0);
    private static Dimension maxSize = new Dimension(Integer.MAX_VALUE,
            Integer.MAX_VALUE);
    private Handler handler;
    private PropertyChangeListener pcl;

    protected JDesktopPane desktop;
    protected DesktopManager desktopManager;

    /**
     * As of Java 2 platform v1.3 this previously undocumented field is no
     * longer used.
     * Key bindings are now defined by the LookAndFeel, please refer to
     * the key bindings specification for further details.
     *
     * @deprecated As of 1.3.
     */
    @Deprecated
    protected KeyStroke minimizeKey;
    /**
     * As of Java 2 platform v1.3 this previously undocumented field is no
     * longer used.
     * Key bindings are now defined by the LookAndFeel, please refer to
     * the key bindings specification for further details.
     *
     * @deprecated As of 1.3.
     */
    @Deprecated
    protected KeyStroke maximizeKey;
    /**
     * As of Java 2 platform v1.3 this previously undocumented field is no
     * longer used.
     * Key bindings are now defined by the LookAndFeel, please refer to
     * the key bindings specification for further details.
     *
     * @deprecated As of 1.3.
     */
    @Deprecated
    protected KeyStroke closeKey;
    /**
     * As of Java 2 platform v1.3 this previously undocumented field is no
     * longer used.
     * Key bindings are now defined by the LookAndFeel, please refer to
     * the key bindings specification for further details.
     *
     * @deprecated As of 1.3.
     */
    @Deprecated
    protected KeyStroke navigateKey;
    /**
     * As of Java 2 platform v1.3 this previously undocumented field is no
     * longer used.
     * Key bindings are now defined by the LookAndFeel, please refer to
     * the key bindings specification for further details.
     *
     * @deprecated As of 1.3.
     */
    @Deprecated
    protected KeyStroke navigateKey2;

    public static ComponentUI createUI(JComponent c) {
        return new javax.swing.plaf.basic.BasicDesktopPaneUI();
    }

    public BasicDesktopPaneUI() {
    }

    public void installUI(JComponent c)   {
        desktop = (JDesktopPane)c;
        installDefaults();
        installDesktopManager();
        installListeners();
        installKeyboardActions();
    }

    public void uninstallUI(JComponent c) {
        uninstallKeyboardActions();
        uninstallListeners();
        uninstallDesktopManager();
        uninstallDefaults();
        desktop = null;
        handler = null;
    }

    protected void installDefaults() {
        if (desktop.getBackground() == null ||
            desktop.getBackground() instanceof UIResource) {
            desktop.setBackground(UIManager.getColor("Desktop.background"));
        }
        LookAndFeel.installProperty(desktop, "opaque", Boolean.TRUE);
    }

    protected void uninstallDefaults() { }

    /**
     * Installs the <code>PropertyChangeListener</code> returned from
     * <code>createPropertyChangeListener</code> on the
     * <code>JDesktopPane</code>.
     *
     * @since 1.5
     * @see #createPropertyChangeListener
     */
    protected void installListeners() {
        pcl = createPropertyChangeListener();
        desktop.addPropertyChangeListener(pcl);
    }

    /**
     * Uninstalls the <code>PropertyChangeListener</code> returned from
     * <code>createPropertyChangeListener</code> from the
     * <code>JDesktopPane</code>.
     *
     * @since 1.5
     * @see #createPropertyChangeListener
     */
    protected void uninstallListeners() {
        desktop.removePropertyChangeListener(pcl);
        pcl = null;
    }

    protected void installDesktopManager() {
        desktopManager = desktop.getDesktopManager();
        if(desktopManager == null) {
            desktopManager = new BasicDesktopManager();
            desktop.setDesktopManager(desktopManager);
        }
    }

    protected void uninstallDesktopManager() {
        if(desktop.getDesktopManager() instanceof UIResource) {
            desktop.setDesktopManager(null);
        }
        desktopManager = null;
    }

    protected void installKeyboardActions(){
        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        if (inputMap != null) {
            SwingUtilities.replaceUIInputMap(desktop,
                        JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap);
        }
        inputMap = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        if (inputMap != null) {
            SwingUtilities.replaceUIInputMap(desktop,
                        JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
                        inputMap);
        }

        javax.swing.plaf.basic.LazyActionMap.installLazyActionMap(desktop, javax.swing.plaf.basic.BasicDesktopPaneUI.class,
                "DesktopPane.actionMap");
        registerKeyboardActions();
    }

    protected void registerKeyboardActions(){
    }

    protected void unregisterKeyboardActions(){
    }

    InputMap getInputMap(int condition) {
        if (condition == JComponent.WHEN_IN_FOCUSED_WINDOW) {
            return createInputMap(condition);
        }
        else if (condition == JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT) {
            return (InputMap)DefaultLookup.get(desktop, this,
                    "Desktop.ancestorInputMap");
        }
        return null;
    }

    InputMap createInputMap(int condition) {
        if (condition == JComponent.WHEN_IN_FOCUSED_WINDOW) {
            Object[] bindings = (Object[])DefaultLookup.get(desktop,
                    this, "Desktop.windowBindings");

            if (bindings != null) {
                return LookAndFeel.makeComponentInputMap(desktop, bindings);
            }
        }
        return null;
    }

    static void loadActionMap(javax.swing.plaf.basic.LazyActionMap map) {
        map.put(new Actions(Actions.RESTORE));
        map.put(new Actions(Actions.CLOSE));
        map.put(new Actions(Actions.MOVE));
        map.put(new Actions(Actions.RESIZE));
        map.put(new Actions(Actions.LEFT));
        map.put(new Actions(Actions.SHRINK_LEFT));
        map.put(new Actions(Actions.RIGHT));
        map.put(new Actions(Actions.SHRINK_RIGHT));
        map.put(new Actions(Actions.UP));
        map.put(new Actions(Actions.SHRINK_UP));
        map.put(new Actions(Actions.DOWN));
        map.put(new Actions(Actions.SHRINK_DOWN));
        map.put(new Actions(Actions.ESCAPE));
        map.put(new Actions(Actions.MINIMIZE));
        map.put(new Actions(Actions.MAXIMIZE));
        map.put(new Actions(Actions.NEXT_FRAME));
        map.put(new Actions(Actions.PREVIOUS_FRAME));
        map.put(new Actions(Actions.NAVIGATE_NEXT));
        map.put(new Actions(Actions.NAVIGATE_PREVIOUS));
    }

    protected void uninstallKeyboardActions(){
      unregisterKeyboardActions();
      SwingUtilities.replaceUIInputMap(desktop, JComponent.
                                     WHEN_IN_FOCUSED_WINDOW, null);
      SwingUtilities.replaceUIInputMap(desktop, JComponent.
                                     WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, null);
      SwingUtilities.replaceUIActionMap(desktop, null);
    }

    public void paint(Graphics g, JComponent c) {}

    public Dimension getPreferredSize(JComponent c) {return null;}

    public Dimension getMinimumSize(JComponent c) {
        return minSize;
        }
    public Dimension getMaximumSize(JComponent c){
        return maxSize;
    }

    /**
     * Returns the <code>PropertyChangeListener</code> to install on
     * the <code>JDesktopPane</code>.
     *
     * @since 1.5
     * @return The PropertyChangeListener that will be added to track
     * changes in the desktop pane.
     */
    protected PropertyChangeListener createPropertyChangeListener() {
        return getHandler();
    }

    private Handler getHandler() {
        if (handler == null) {
            handler = new Handler();
        }
        return handler;
    }

    private class Handler implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            String propertyName = evt.getPropertyName();
            if ("desktopManager" == propertyName) {
                installDesktopManager();
            }
        }
    }

    /**
     * The default DesktopManager installed by the UI.
     */
    private class BasicDesktopManager extends DefaultDesktopManager
            implements UIResource {
    }

    private static class Actions extends UIAction {
        private static String CLOSE = "close";
        private static String ESCAPE = "escape";
        private static String MAXIMIZE = "maximize";
        private static String MINIMIZE = "minimize";
        private static String MOVE = "move";
        private static String RESIZE = "resize";
        private static String RESTORE = "restore";
        private static String LEFT = "left";
        private static String RIGHT = "right";
        private static String UP = "up";
        private static String DOWN = "down";
        private static String SHRINK_LEFT = "shrinkLeft";
        private static String SHRINK_RIGHT = "shrinkRight";
        private static String SHRINK_UP = "shrinkUp";
        private static String SHRINK_DOWN = "shrinkDown";
        private static String NEXT_FRAME = "selectNextFrame";
        private static String PREVIOUS_FRAME = "selectPreviousFrame";
        private static String NAVIGATE_NEXT = "navigateNext";
        private static String NAVIGATE_PREVIOUS = "navigatePrevious";
        private final int MOVE_RESIZE_INCREMENT = 10;
        private static boolean moving = false;
        private static boolean resizing = false;
        private static JInternalFrame sourceFrame = null;
        private static Component focusOwner = null;

        Actions() {
            super(null);
        }

        Actions(String name) {
            super(name);
        }

        public void actionPerformed(ActionEvent e) {
            JDesktopPane dp = (JDesktopPane)e.getSource();
            String key = getName();

            if (CLOSE == key || MAXIMIZE == key || MINIMIZE == key ||
                    RESTORE == key) {
                setState(dp, key);
            }
            else if (ESCAPE == key) {
                if (sourceFrame == dp.getSelectedFrame() &&
                        focusOwner != null) {
                    focusOwner.requestFocus();
                }
                moving = false;
                resizing = false;
                sourceFrame = null;
                focusOwner = null;
            }
            else if (MOVE == key || RESIZE == key) {
                sourceFrame = dp.getSelectedFrame();
                if (sourceFrame == null) {
                    return;
                }
                moving = (key == MOVE) ? true : false;
                resizing = (key == RESIZE) ? true : false;

                focusOwner = KeyboardFocusManager.
                    getCurrentKeyboardFocusManager().getFocusOwner();
                if (!SwingUtilities.isDescendingFrom(focusOwner, sourceFrame)) {
                    focusOwner = null;
                }
                sourceFrame.requestFocus();
            }
            else if (LEFT == key ||
                     RIGHT == key ||
                     UP == key ||
                     DOWN == key ||
                     SHRINK_RIGHT == key ||
                     SHRINK_LEFT == key ||
                     SHRINK_UP == key ||
                     SHRINK_DOWN == key) {
                JInternalFrame c = dp.getSelectedFrame();
                if (sourceFrame == null || c != sourceFrame ||
                        KeyboardFocusManager.
                            getCurrentKeyboardFocusManager().getFocusOwner() !=
                                sourceFrame) {
                    return;
                }
                Insets minOnScreenInsets =
                    UIManager.getInsets("Desktop.minOnScreenInsets");
                Dimension size = c.getSize();
                Dimension minSize = c.getMinimumSize();
                int dpWidth = dp.getWidth();
                int dpHeight = dp.getHeight();
                int delta;
                Point loc = c.getLocation();
                if (LEFT == key) {
                    if (moving) {
                        c.setLocation(
                                loc.x + size.width - MOVE_RESIZE_INCREMENT <
                                    minOnScreenInsets.right ?
                                        -size.width + minOnScreenInsets.right :
                                        loc.x - MOVE_RESIZE_INCREMENT,
                                loc.y);
                    } else if (resizing) {
                        c.setLocation(loc.x - MOVE_RESIZE_INCREMENT, loc.y);
                        c.setSize(size.width + MOVE_RESIZE_INCREMENT,
                                size.height);
                    }
                } else if (RIGHT == key) {
                    if (moving) {
                        c.setLocation(
                                loc.x + MOVE_RESIZE_INCREMENT >
                                    dpWidth - minOnScreenInsets.left ?
                                        dpWidth - minOnScreenInsets.left :
                                        loc.x + MOVE_RESIZE_INCREMENT,
                                loc.y);
                    } else if (resizing) {
                        c.setSize(size.width + MOVE_RESIZE_INCREMENT,
                                size.height);
                    }
                } else if (UP == key) {
                    if (moving) {
                        c.setLocation(loc.x,
                                loc.y + size.height - MOVE_RESIZE_INCREMENT <
                                    minOnScreenInsets.bottom ?
                                        -size.height +
                                            minOnScreenInsets.bottom :
                                        loc.y - MOVE_RESIZE_INCREMENT);
                    } else if (resizing) {
                        c.setLocation(loc.x, loc.y - MOVE_RESIZE_INCREMENT);
                        c.setSize(size.width,
                                size.height + MOVE_RESIZE_INCREMENT);
                    }
                } else if (DOWN == key) {
                    if (moving) {
                        c.setLocation(loc.x,
                                loc.y + MOVE_RESIZE_INCREMENT >
                                    dpHeight - minOnScreenInsets.top ?
                                        dpHeight - minOnScreenInsets.top :
                                        loc.y + MOVE_RESIZE_INCREMENT);
                    } else if (resizing) {
                        c.setSize(size.width,
                                size.height + MOVE_RESIZE_INCREMENT);
                    }
                } else if (SHRINK_LEFT == key && resizing) {
                    // Make sure we don't resize less than minimum size.
                    if (minSize.width < (size.width - MOVE_RESIZE_INCREMENT)) {
                        delta = MOVE_RESIZE_INCREMENT;
                    } else {
                        delta = size.width - minSize.width;
                    }

                    // Ensure that we keep the internal frame on the desktop.
                    if (loc.x + size.width - delta < minOnScreenInsets.left) {
                        delta = loc.x + size.width - minOnScreenInsets.left;
                    }
                    c.setSize(size.width - delta, size.height);
                } else if (SHRINK_RIGHT == key && resizing) {
                    // Make sure we don't resize less than minimum size.
                    if (minSize.width < (size.width - MOVE_RESIZE_INCREMENT)) {
                        delta = MOVE_RESIZE_INCREMENT;
                    } else {
                        delta = size.width - minSize.width;
                    }

                    // Ensure that we keep the internal frame on the desktop.
                    if (loc.x + delta > dpWidth - minOnScreenInsets.right) {
                        delta = (dpWidth - minOnScreenInsets.right) - loc.x;
                    }

                    c.setLocation(loc.x + delta, loc.y);
                    c.setSize(size.width - delta, size.height);
                } else if (SHRINK_UP == key && resizing) {
                    // Make sure we don't resize less than minimum size.
                    if (minSize.height <
                            (size.height - MOVE_RESIZE_INCREMENT)) {
                        delta = MOVE_RESIZE_INCREMENT;
                    } else {
                        delta = size.height - minSize.height;
                    }

                    // Ensure that we keep the internal frame on the desktop.
                    if (loc.y + size.height - delta <
                            minOnScreenInsets.bottom) {
                        delta = loc.y + size.height - minOnScreenInsets.bottom;
                    }

                    c.setSize(size.width, size.height - delta);
                } else if (SHRINK_DOWN == key  && resizing) {
                    // Make sure we don't resize less than minimum size.
                    if (minSize.height <
                            (size.height - MOVE_RESIZE_INCREMENT)) {
                        delta = MOVE_RESIZE_INCREMENT;
                    } else {
                        delta = size.height - minSize.height;
                    }

                    // Ensure that we keep the internal frame on the desktop.
                    if (loc.y + delta > dpHeight - minOnScreenInsets.top) {
                        delta = (dpHeight - minOnScreenInsets.top) - loc.y;
                    }

                    c.setLocation(loc.x, loc.y + delta);
                    c.setSize(size.width, size.height - delta);
                }
            }
            else if (NEXT_FRAME == key || PREVIOUS_FRAME == key) {
                dp.selectFrame((key == NEXT_FRAME) ? true : false);
            }
            else if (NAVIGATE_NEXT == key ||
                     NAVIGATE_PREVIOUS == key) {
                boolean moveForward = true;
                if (NAVIGATE_PREVIOUS == key) {
                    moveForward = false;
                }
                Container cycleRoot = dp.getFocusCycleRootAncestor();

                if (cycleRoot != null) {
                    FocusTraversalPolicy policy =
                        cycleRoot.getFocusTraversalPolicy();
                    if (policy != null && policy instanceof
                            SortingFocusTraversalPolicy) {
                        SortingFocusTraversalPolicy sPolicy =
                            (SortingFocusTraversalPolicy)policy;
                        boolean idc = sPolicy.getImplicitDownCycleTraversal();
                        try {
                            sPolicy.setImplicitDownCycleTraversal(false);
                            if (moveForward) {
                                KeyboardFocusManager.
                                    getCurrentKeyboardFocusManager().
                                        focusNextComponent(dp);
                            } else {
                                KeyboardFocusManager.
                                    getCurrentKeyboardFocusManager().
                                    focusPreviousComponent(dp);
                            }
                        } finally {
                            sPolicy.setImplicitDownCycleTraversal(idc);
                        }
                    }
                }
            }
        }

        private void setState(JDesktopPane dp, String state) {
            if (state == CLOSE) {
                JInternalFrame f = dp.getSelectedFrame();
                if (f == null) {
                    return;
                }
                f.doDefaultCloseAction();
            } else if (state == MAXIMIZE) {
                // maximize the selected frame
                JInternalFrame f = dp.getSelectedFrame();
                if (f == null) {
                    return;
                }
                if (!f.isMaximum()) {
                    if (f.isIcon()) {
                        try {
                            f.setIcon(false);
                            f.setMaximum(true);
                        } catch (PropertyVetoException pve) {}
                    } else {
                        try {
                            f.setMaximum(true);
                        } catch (PropertyVetoException pve) {
                        }
                    }
                }
            } else if (state == MINIMIZE) {
                // minimize the selected frame
                JInternalFrame f = dp.getSelectedFrame();
                if (f == null) {
                    return;
                }
                if (!f.isIcon()) {
                    try {
                        f.setIcon(true);
                    } catch (PropertyVetoException pve) {
                    }
                }
            } else if (state == RESTORE) {
                // restore the selected minimized or maximized frame
                JInternalFrame f = dp.getSelectedFrame();
                if (f == null) {
                    return;
                }
                try {
                    if (f.isIcon()) {
                        f.setIcon(false);
                    } else if (f.isMaximum()) {
                        f.setMaximum(false);
                    }
                    f.setSelected(true);
                } catch (PropertyVetoException pve) {
                }
            }
        }

        public boolean isEnabled(Object sender) {
            if (sender instanceof JDesktopPane) {
                JDesktopPane dp = (JDesktopPane)sender;
                String action = getName();
                if (action == Actions.NEXT_FRAME ||
                    action == Actions.PREVIOUS_FRAME) {
                    return true;
                }
                JInternalFrame iFrame = dp.getSelectedFrame();
                if (iFrame == null) {
                    return false;
                } else if (action == Actions.CLOSE) {
                    return iFrame.isClosable();
                } else if (action == Actions.MINIMIZE) {
                    return iFrame.isIconifiable();
                } else if (action == Actions.MAXIMIZE) {
                    return iFrame.isMaximizable();
                }
                return true;
            }
            return false;
        }
    }


    /**
     * Handles restoring a minimized or maximized internal frame.
     * @since 1.3
     */
    protected class OpenAction extends AbstractAction {
        public void actionPerformed(ActionEvent evt) {
            JDesktopPane dp = (JDesktopPane)evt.getSource();
            SHARED_ACTION.setState(dp, Actions.RESTORE);
        }

        public boolean isEnabled() {
            return true;
        }
    }

    /**
     * Handles closing an internal frame.
     */
    protected class CloseAction extends AbstractAction {
        public void actionPerformed(ActionEvent evt) {
            JDesktopPane dp = (JDesktopPane)evt.getSource();
            SHARED_ACTION.setState(dp, Actions.CLOSE);
        }

        public boolean isEnabled() {
            JInternalFrame iFrame = desktop.getSelectedFrame();
            if (iFrame != null) {
                return iFrame.isClosable();
            }
            return false;
        }
    }

    /**
     * Handles minimizing an internal frame.
     */
    protected class MinimizeAction extends AbstractAction {
        public void actionPerformed(ActionEvent evt) {
            JDesktopPane dp = (JDesktopPane)evt.getSource();
            SHARED_ACTION.setState(dp, Actions.MINIMIZE);
        }

        public boolean isEnabled() {
            JInternalFrame iFrame = desktop.getSelectedFrame();
            if (iFrame != null) {
                return iFrame.isIconifiable();
            }
            return false;
        }
    }

    /**
     * Handles maximizing an internal frame.
     */
    protected class MaximizeAction extends AbstractAction {
        public void actionPerformed(ActionEvent evt) {
            JDesktopPane dp = (JDesktopPane)evt.getSource();
            SHARED_ACTION.setState(dp, Actions.MAXIMIZE);
        }

        public boolean isEnabled() {
            JInternalFrame iFrame = desktop.getSelectedFrame();
            if (iFrame != null) {
                return iFrame.isMaximizable();
            }
            return false;
        }
    }

    /**
     * Handles navigating to the next internal frame.
     */
    protected class NavigateAction extends AbstractAction {
        public void actionPerformed(ActionEvent evt) {
            JDesktopPane dp = (JDesktopPane)evt.getSource();
            dp.selectFrame(true);
        }

        public boolean isEnabled() {
            return true;
        }
    }
}
