/*
 * Copyright (c) 1999, 2006, Oracle and/or its affiliates. All rights reserved.
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

import java.awt.event.ActionEvent;
import java.awt.KeyboardFocusManager;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.LazyActionMap;

import sun.swing.DefaultLookup;
import sun.swing.UIAction;

/**
 * Basic implementation of RootPaneUI, there is one shared between all
 * JRootPane instances.
 *
 * @author Scott Violet
 * @since 1.3
 */
public class BasicRootPaneUI extends RootPaneUI implements
                  PropertyChangeListener {
    private static RootPaneUI rootPaneUI = new javax.swing.plaf.basic.BasicRootPaneUI();

    public static ComponentUI createUI(JComponent c) {
        return rootPaneUI;
    }

    public void installUI(JComponent c) {
        installDefaults((JRootPane)c);
        installComponents((JRootPane)c);
        installListeners((JRootPane)c);
        installKeyboardActions((JRootPane)c);
    }


    public void uninstallUI(JComponent c) {
        uninstallDefaults((JRootPane)c);
        uninstallComponents((JRootPane)c);
        uninstallListeners((JRootPane)c);
        uninstallKeyboardActions((JRootPane)c);
    }

    protected void installDefaults(JRootPane c){
        LookAndFeel.installProperty(c, "opaque", Boolean.FALSE);
    }

    protected void installComponents(JRootPane root) {
    }

    protected void installListeners(JRootPane root) {
        root.addPropertyChangeListener(this);
    }

    protected void installKeyboardActions(JRootPane root) {
        InputMap km = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, root);
        SwingUtilities.replaceUIInputMap(root,
                JComponent.WHEN_IN_FOCUSED_WINDOW, km);
        km = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
                root);
        SwingUtilities.replaceUIInputMap(root,
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, km);

        javax.swing.plaf.basic.LazyActionMap.installLazyActionMap(root, javax.swing.plaf.basic.BasicRootPaneUI.class,
                "RootPane.actionMap");
        updateDefaultButtonBindings(root);
    }

    protected void uninstallDefaults(JRootPane root) {
    }

    protected void uninstallComponents(JRootPane root) {
    }

    protected void uninstallListeners(JRootPane root) {
        root.removePropertyChangeListener(this);
    }

    protected void uninstallKeyboardActions(JRootPane root) {
        SwingUtilities.replaceUIInputMap(root, JComponent.
                                       WHEN_IN_FOCUSED_WINDOW, null);
        SwingUtilities.replaceUIActionMap(root, null);
    }

    InputMap getInputMap(int condition, JComponent c) {
        if (condition == JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT) {
            return (InputMap)DefaultLookup.get(c, this,
                                       "RootPane.ancestorInputMap");
        }

        if (condition == JComponent.WHEN_IN_FOCUSED_WINDOW) {
            return createInputMap(condition, c);
        }
        return null;
    }

    ComponentInputMap createInputMap(int condition, JComponent c) {
        return new RootPaneInputMap(c);
    }

    static void loadActionMap(javax.swing.plaf.basic.LazyActionMap map) {
        map.put(new Actions(Actions.PRESS));
        map.put(new Actions(Actions.RELEASE));
        map.put(new Actions(Actions.POST_POPUP));
    }

    /**
     * Invoked when the default button property has changed. This reloads
     * the bindings from the defaults table with name
     * <code>RootPane.defaultButtonWindowKeyBindings</code>.
     */
    void updateDefaultButtonBindings(JRootPane root) {
        InputMap km = SwingUtilities.getUIInputMap(root, JComponent.
                                               WHEN_IN_FOCUSED_WINDOW);
        while (km != null && !(km instanceof RootPaneInputMap)) {
            km = km.getParent();
        }
        if (km != null) {
            km.clear();
            if (root.getDefaultButton() != null) {
                Object[] bindings = (Object[])DefaultLookup.get(root, this,
                           "RootPane.defaultButtonWindowKeyBindings");
                if (bindings != null) {
                    LookAndFeel.loadKeyBindings(km, bindings);
                }
            }
        }
    }

    /**
     * Invoked when a property changes on the root pane. If the event
     * indicates the <code>defaultButton</code> has changed, this will
     * reinstall the keyboard actions.
     */
    public void propertyChange(PropertyChangeEvent e) {
        if(e.getPropertyName().equals("defaultButton")) {
            JRootPane rootpane = (JRootPane)e.getSource();
            updateDefaultButtonBindings(rootpane);
            if (rootpane.getClientProperty("temporaryDefaultButton") == null) {
                rootpane.putClientProperty("initialDefaultButton", e.getNewValue());
            }
        }
    }


    static class Actions extends UIAction {
        public static final String PRESS = "press";
        public static final String RELEASE = "release";
        public static final String POST_POPUP = "postPopup";

        Actions(String name) {
            super(name);
        }

        public void actionPerformed(ActionEvent evt) {
            JRootPane root = (JRootPane)evt.getSource();
            JButton owner = root.getDefaultButton();
            String key = getName();

            if (key == POST_POPUP) { // Action to post popup
                Component c = KeyboardFocusManager
                        .getCurrentKeyboardFocusManager()
                         .getFocusOwner();

                if(c instanceof JComponent) {
                    JComponent src = (JComponent) c;
                    JPopupMenu jpm = src.getComponentPopupMenu();
                    if(jpm != null) {
                        Point pt = src.getPopupLocation(null);
                        if(pt == null) {
                            Rectangle vis = src.getVisibleRect();
                            pt = new Point(vis.x+vis.width/2,
                                           vis.y+vis.height/2);
                        }
                        jpm.show(c, pt.x, pt.y);
                    }
                }
            }
            else if (owner != null
                     && SwingUtilities.getRootPane(owner) == root) {
                if (key == PRESS) {
                    owner.doClick(20);
                }
            }
        }

        public boolean isEnabled(Object sender) {
            String key = getName();
            if(key == POST_POPUP) {
                MenuElement[] elems = MenuSelectionManager
                        .defaultManager()
                        .getSelectedPath();
                if(elems != null && elems.length != 0) {
                    return false;
                    // We shall not interfere with already opened menu
                }

                Component c = KeyboardFocusManager
                       .getCurrentKeyboardFocusManager()
                        .getFocusOwner();
                if(c instanceof JComponent) {
                    JComponent src = (JComponent) c;
                    return src.getComponentPopupMenu() != null;
                }

                return false;
            }

            if (sender != null && sender instanceof JRootPane) {
                JButton owner = ((JRootPane)sender).getDefaultButton();
                return (owner != null && owner.getModel().isEnabled());
            }
            return true;
        }
    }

    private static class RootPaneInputMap extends ComponentInputMapUIResource {
        public RootPaneInputMap(JComponent c) {
            super(c);
        }
    }
}
