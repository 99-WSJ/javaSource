/*
 * Copyright (c) 2001, 2009, Oracle and/or its affiliates. All rights reserved.
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

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

/**
 * Wrapper for a value from the desktop. The value is lazily looked up, and
 * can be accessed using the <code>UIManager.ActiveValue</code> method
 * <code>createValue</code>. If the underlying desktop property changes this
 * will force the UIs to update all known Frames. You can invoke
 * <code>invalidate</code> to force the value to be fetched again.
 *
 */
// NOTE: Don't rely on this class staying in this location. It is likely
// to move to a different package in the future.
public class DesktopProperty implements UIDefaults.ActiveValue {
    /**
     * Indicates if an updateUI call is pending.
     */
    private static boolean updatePending;

    /**
     * ReferenceQueue of unreferenced WeakPCLs.
     */
    private static final ReferenceQueue<com.sun.java.swing.plaf.windows.DesktopProperty> queue = new ReferenceQueue<com.sun.java.swing.plaf.windows.DesktopProperty>();

    /**
     * PropertyChangeListener attached to the Toolkit.
     */
    private WeakPCL pcl;
    /**
     * Key used to lookup value from desktop.
     */
    private final String key;
    /**
     * Value to return.
     */
    private Object value;
    /**
     * Fallback value in case we get null from desktop.
     */
    private final Object fallback;


    /**
     * Cleans up any lingering state held by unrefeernced
     * DesktopProperties.
     */
    static void flushUnreferencedProperties() {
        WeakPCL pcl;

        while ((pcl = (WeakPCL)queue.poll()) != null) {
            pcl.dispose();
        }
    }


    /**
     * Sets whether or not an updateUI call is pending.
     */
    private static synchronized void setUpdatePending(boolean update) {
        updatePending = update;
    }

    /**
     * Returns true if a UI update is pending.
     */
    private static synchronized boolean isUpdatePending() {
        return updatePending;
    }

    /**
     * Updates the UIs of all the known Frames.
     */
    private static void updateAllUIs() {
        // Check if the current UI is WindowsLookAndfeel and flush the XP style map.
        // Note: Change the package test if this class is moved to a different package.
        Class uiClass = UIManager.getLookAndFeel().getClass();
        if (uiClass.getPackage().equals(com.sun.java.swing.plaf.windows.DesktopProperty.class.getPackage())) {
            XPStyle.invalidateStyle();
        }
        Frame appFrames[] = Frame.getFrames();
        for (Frame appFrame : appFrames) {
            updateWindowUI(appFrame);
        }
    }

    /**
     * Updates the UI of the passed in window and all its children.
     */
    private static void updateWindowUI(Window window) {
        SwingUtilities.updateComponentTreeUI(window);
        Window ownedWins[] = window.getOwnedWindows();
        for (Window ownedWin : ownedWins) {
            updateWindowUI(ownedWin);
        }
    }


    /**
     * Creates a DesktopProperty.
     *
     * @param key Key used in looking up desktop value.
     * @param fallback Value used if desktop property is null.
     */
    public DesktopProperty(String key, Object fallback) {
        this.key = key;
        this.fallback = fallback;
        // The only sure fire way to clear our references is to create a
        // Thread and wait for a reference to be added to the queue.
        // Because it is so rare that you will actually change the look
        // and feel, this stepped is forgoed and a middle ground of
        // flushing references from the constructor is instead done.
        // The implication is that once one DesktopProperty is created
        // there will most likely be n (number of DesktopProperties created
        // by the LookAndFeel) WeakPCLs around, but this number will not
        // grow past n.
        flushUnreferencedProperties();
    }

    /**
     * UIManager.LazyValue method, returns the value from the desktop
     * or the fallback value if the desktop value is null.
     */
    public Object createValue(UIDefaults table) {
        if (value == null) {
            value = configureValue(getValueFromDesktop());
            if (value == null) {
                value = configureValue(getDefaultValue());
            }
        }
        return value;
    }

    /**
     * Returns the value from the desktop.
     */
    protected Object getValueFromDesktop() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();

        if (pcl == null) {
            pcl = new WeakPCL(this, getKey(), UIManager.getLookAndFeel());
            toolkit.addPropertyChangeListener(getKey(), pcl);
        }

        return toolkit.getDesktopProperty(getKey());
    }

    /**
     * Returns the value to use if the desktop property is null.
     */
    protected Object getDefaultValue() {
        return fallback;
    }

    /**
     * Invalidates the current value.
     *
     * @param laf the LookAndFeel this DesktopProperty was created with
     */
    public void invalidate(LookAndFeel laf) {
        invalidate();
    }

    /**
     * Invalides the current value so that the next invocation of
     * <code>createValue</code> will ask for the property again.
     */
    public void invalidate() {
        value = null;
    }

    /**
     * Requests that all components in the GUI hierarchy be updated
     * to reflect dynamic changes in this look&feel.  This update occurs
     * by uninstalling and re-installing the UI objects. Requests are
     * batched and collapsed into a single update pass because often
     * many desktop properties will change at once.
     */
    protected void updateUI() {
        if (!isUpdatePending()) {
            setUpdatePending(true);
            Runnable uiUpdater = new Runnable() {
                public void run() {
                    updateAllUIs();
                    setUpdatePending(false);
                }
            };
            SwingUtilities.invokeLater(uiUpdater);
        }
    }

    /**
     * Configures the value as appropriate for a defaults property in
     * the UIDefaults table.
     */
    protected Object configureValue(Object value) {
        if (value != null) {
            if (value instanceof Color) {
                return new ColorUIResource((Color)value);
            }
            else if (value instanceof Font) {
                return new FontUIResource((Font)value);
            }
            else if (value instanceof UIDefaults.LazyValue) {
                value = ((UIDefaults.LazyValue)value).createValue(null);
            }
            else if (value instanceof UIDefaults.ActiveValue) {
                value = ((UIDefaults.ActiveValue)value).createValue(null);
            }
        }
        return value;
    }

    /**
     * Returns the key used to lookup the desktop properties value.
     */
    protected String getKey() {
        return key;
    }



    /**
     * As there is typically only one Toolkit, the PropertyChangeListener
     * is handled via a WeakReference so as not to pin down the
     * DesktopProperty.
     */
    private static class WeakPCL extends WeakReference<com.sun.java.swing.plaf.windows.DesktopProperty>
                               implements PropertyChangeListener {
        private String key;
        private LookAndFeel laf;

        WeakPCL(com.sun.java.swing.plaf.windows.DesktopProperty target, String key, LookAndFeel laf) {
            super(target, queue);
            this.key = key;
            this.laf = laf;
        }

        public void propertyChange(PropertyChangeEvent pce) {
            com.sun.java.swing.plaf.windows.DesktopProperty property = get();

            if (property == null || laf != UIManager.getLookAndFeel()) {
                // The property was GC'ed, we're no longer interested in
                // PropertyChanges, remove the listener.
                dispose();
            }
            else {
                property.invalidate(laf);
                property.updateUI();
            }
        }

        void dispose() {
            Toolkit.getDefaultToolkit().removePropertyChangeListener(key, this);
        }
    }
}
