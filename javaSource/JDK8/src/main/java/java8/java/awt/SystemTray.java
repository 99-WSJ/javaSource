/*
 * Copyright (c) 2005, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java8.java.awt;

import sun.awt.AWTAccessor;
import sun.awt.AppContext;
import sun.awt.HeadlessToolkit;
import sun.awt.SunToolkit;
import sun.security.util.SecurityConstants;

import java.awt.GraphicsEnvironment;
import java.awt.TrayIcon;
import java.awt.peer.SystemTrayPeer;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Vector;

/**
 * The <code>SystemTray</code> class represents the system tray for a
 * desktop.  On Microsoft Windows it is referred to as the "Taskbar
 * Status Area", on Gnome it is referred to as the "Notification
 * Area", on KDE it is referred to as the "System Tray".  The system
 * tray is shared by all applications running on the desktop.
 *
 * <p> On some platforms the system tray may not be present or may not
 * be supported, in this case {@link java.awt.SystemTray#getSystemTray()}
 * throws {@link UnsupportedOperationException}.  To detect whether the
 * system tray is supported, use {@link java.awt.SystemTray#isSupported}.
 *
 * <p>The <code>SystemTray</code> may contain one or more {@link
 * TrayIcon TrayIcons}, which are added to the tray using the {@link
 * #add} method, and removed when no longer needed, using the
 * {@link #remove}.  <code>TrayIcon</code> consists of an
 * image, a popup menu and a set of associated listeners.  Please see
 * the {@link TrayIcon} class for details.
 *
 * <p>Every Java application has a single <code>SystemTray</code>
 * instance that allows the app to interface with the system tray of
 * the desktop while the app is running.  The <code>SystemTray</code>
 * instance can be obtained from the {@link #getSystemTray} method.
 * An application may not create its own instance of
 * <code>SystemTray</code>.
 *
 * <p>The following code snippet demonstrates how to access
 * and customize the system tray:
 * <pre>
 * <code>
 *     {@link TrayIcon} trayIcon = null;
 *     if (SystemTray.isSupported()) {
 *         // get the SystemTray instance
 *         SystemTray tray = SystemTray.{@link #getSystemTray};
 *         // load an image
 *         {@link java.awt.Image} image = {@link Toolkit#getImage(String) Toolkit.getDefaultToolkit().getImage}(...);
 *         // create a action listener to listen for default action executed on the tray icon
 *         {@link java.awt.event.ActionListener} listener = new {@link java.awt.event.ActionListener ActionListener}() {
 *             public void {@link java.awt.event.ActionListener#actionPerformed actionPerformed}({@link java.awt.event.ActionEvent} e) {
 *                 // execute default action of the application
 *                 // ...
 *             }
 *         };
 *         // create a popup menu
 *         {@link PopupMenu} popup = new {@link PopupMenu#PopupMenu PopupMenu}();
 *         // create menu item for the default action
 *         MenuItem defaultItem = new MenuItem(...);
 *         defaultItem.addActionListener(listener);
 *         popup.add(defaultItem);
 *         /// ... add other items
 *         // construct a TrayIcon
 *         trayIcon = new {@link TrayIcon#TrayIcon(java.awt.Image, String, PopupMenu) TrayIcon}(image, "Tray Demo", popup);
 *         // set the TrayIcon properties
 *         trayIcon.{@link TrayIcon#addActionListener(java.awt.event.ActionListener) addActionListener}(listener);
 *         // ...
 *         // add the tray image
 *         try {
 *             tray.{@link java.awt.SystemTray#add(TrayIcon) add}(trayIcon);
 *         } catch (AWTException e) {
 *             System.err.println(e);
 *         }
 *         // ...
 *     } else {
 *         // disable tray option in your application or
 *         // perform other actions
 *         ...
 *     }
 *     // ...
 *     // some time later
 *     // the application state has changed - update the image
 *     if (trayIcon != null) {
 *         trayIcon.{@link TrayIcon#setImage(java.awt.Image) setImage}(updatedImage);
 *     }
 *     // ...
 * </code>
 * </pre>
 *
 * @since 1.6
 * @see TrayIcon
 *
 * @author Bino George
 * @author Denis Mikhalkin
 * @author Sharon Zakhour
 * @author Anton Tarasov
 */
public class SystemTray {
    private static java.awt.SystemTray systemTray;
    private int currentIconID = 0; // each TrayIcon added gets a unique ID

    transient private SystemTrayPeer peer;

    private static final TrayIcon[] EMPTY_TRAY_ARRAY = new TrayIcon[0];

    static {
        AWTAccessor.setSystemTrayAccessor(
            new AWTAccessor.SystemTrayAccessor() {
                public void firePropertyChange(java.awt.SystemTray tray,
                                               String propertyName,
                                               Object oldValue,
                                               Object newValue) {
                    tray.firePropertyChange(propertyName, oldValue, newValue);
                }
            });
    }

    /**
     * Private <code>SystemTray</code> constructor.
     *
     */
    private SystemTray() {
        addNotify();
    }

    /**
     * Gets the <code>SystemTray</code> instance that represents the
     * desktop's tray area.  This always returns the same instance per
     * application.  On some platforms the system tray may not be
     * supported.  You may use the {@link #isSupported} method to
     * check if the system tray is supported.
     *
     * <p>If a SecurityManager is installed, the AWTPermission
     * {@code accessSystemTray} must be granted in order to get the
     * {@code SystemTray} instance. Otherwise this method will throw a
     * SecurityException.
     *
     * @return the <code>SystemTray</code> instance that represents
     * the desktop's tray area
     * @throws UnsupportedOperationException if the system tray isn't
     * supported by the current platform
     * @throws HeadlessException if
     * <code>GraphicsEnvironment.isHeadless()</code> returns <code>true</code>
     * @throws SecurityException if {@code accessSystemTray} permission
     * is not granted
     * @see #add(TrayIcon)
     * @see TrayIcon
     * @see #isSupported
     * @see SecurityManager#checkPermission
     * @see AWTPermission
     */
    public static java.awt.SystemTray getSystemTray() {
        checkSystemTrayAllowed();
        if (GraphicsEnvironment.isHeadless()) {
            throw new HeadlessException();
        }

        initializeSystemTrayIfNeeded();

        if (!isSupported()) {
            throw new UnsupportedOperationException(
                "The system tray is not supported on the current platform.");
        }

        return systemTray;
    }

    /**
     * Returns whether the system tray is supported on the current
     * platform.  In addition to displaying the tray icon, minimal
     * system tray support includes either a popup menu (see {@link
     * TrayIcon#setPopupMenu(PopupMenu)}) or an action event (see
     * {@link TrayIcon#addActionListener(ActionListener)}).
     *
     * <p>Developers should not assume that all of the system tray
     * functionality is supported.  To guarantee that the tray icon's
     * default action is always accessible, add the default action to
     * both the action listener and the popup menu.  See the {@link
     * java.awt.SystemTray example} for an example of how to do this.
     *
     * <p><b>Note</b>: When implementing <code>SystemTray</code> and
     * <code>TrayIcon</code> it is <em>strongly recommended</em> that
     * you assign different gestures to the popup menu and an action
     * event.  Overloading a gesture for both purposes is confusing
     * and may prevent the user from accessing one or the other.
     *
     * @see #getSystemTray
     * @return <code>false</code> if no system tray access is supported; this
     * method returns <code>true</code> if the minimal system tray access is
     * supported but does not guarantee that all system tray
     * functionality is supported for the current platform
     */
    public static boolean isSupported() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        if (toolkit instanceof SunToolkit) {
            // connecting tray to native resource
            initializeSystemTrayIfNeeded();
            return ((SunToolkit)toolkit).isTraySupported();
        } else if (toolkit instanceof HeadlessToolkit) {
            // skip initialization as the init routine
            // throws HeadlessException
            return ((HeadlessToolkit)toolkit).isTraySupported();
        } else {
            return false;
        }
    }

    /**
     * Adds a <code>TrayIcon</code> to the <code>SystemTray</code>.
     * The tray icon becomes visible in the system tray once it is
     * added.  The order in which icons are displayed in a tray is not
     * specified - it is platform and implementation-dependent.
     *
     * <p> All icons added by the application are automatically
     * removed from the <code>SystemTray</code> upon application exit
     * and also when the desktop system tray becomes unavailable.
     *
     * @param trayIcon the <code>TrayIcon</code> to be added
     * @throws NullPointerException if <code>trayIcon</code> is
     * <code>null</code>
     * @throws IllegalArgumentException if the same instance of
     * a <code>TrayIcon</code> is added more than once
     * @throws AWTException if the desktop system tray is missing
     * @see #remove(TrayIcon)
     * @see #getSystemTray
     * @see TrayIcon
     * @see java.awt.Image
     */
    public void add(TrayIcon trayIcon) throws AWTException {
        if (trayIcon == null) {
            throw new NullPointerException("adding null TrayIcon");
        }
        TrayIcon[] oldArray = null, newArray = null;
        Vector<TrayIcon> icons = null;
        synchronized (this) {
            oldArray = systemTray.getTrayIcons();
            icons = (Vector<TrayIcon>)AppContext.getAppContext().get(TrayIcon.class);
            if (icons == null) {
                icons = new Vector<TrayIcon>(3);
                AppContext.getAppContext().put(TrayIcon.class, icons);

            } else if (icons.contains(trayIcon)) {
                throw new IllegalArgumentException("adding TrayIcon that is already added");
            }
            icons.add(trayIcon);
            newArray = systemTray.getTrayIcons();

            trayIcon.setID(++currentIconID);
        }
        try {
            trayIcon.addNotify();
        } catch (AWTException e) {
            icons.remove(trayIcon);
            throw e;
        }
        firePropertyChange("trayIcons", oldArray, newArray);
    }

    /**
     * Removes the specified <code>TrayIcon</code> from the
     * <code>SystemTray</code>.
     *
     * <p> All icons added by the application are automatically
     * removed from the <code>SystemTray</code> upon application exit
     * and also when the desktop system tray becomes unavailable.
     *
     * <p> If <code>trayIcon</code> is <code>null</code> or was not
     * added to the system tray, no exception is thrown and no action
     * is performed.
     *
     * @param trayIcon the <code>TrayIcon</code> to be removed
     * @see #add(TrayIcon)
     * @see TrayIcon
     */
    public void remove(TrayIcon trayIcon) {
        if (trayIcon == null) {
            return;
        }
        TrayIcon[] oldArray = null, newArray = null;
        synchronized (this) {
            oldArray = systemTray.getTrayIcons();
            Vector<TrayIcon> icons = (Vector<TrayIcon>)AppContext.getAppContext().get(TrayIcon.class);
            // TrayIcon with no peer is not contained in the array.
            if (icons == null || !icons.remove(trayIcon)) {
                return;
            }
            trayIcon.removeNotify();
            newArray = systemTray.getTrayIcons();
        }
        firePropertyChange("trayIcons", oldArray, newArray);
    }

    /**
     * Returns an array of all icons added to the tray by this
     * application.  You can't access the icons added by another
     * application.  Some browsers partition applets in different
     * code bases into separate contexts, and establish walls between
     * these contexts.  In such a scenario, only the tray icons added
     * from this context will be returned.
     *
     * <p> The returned array is a copy of the actual array and may be
     * modified in any way without affecting the system tray.  To
     * remove a <code>TrayIcon</code> from the
     * <code>SystemTray</code>, use the {@link
     * #remove(TrayIcon)} method.
     *
     * @return an array of all tray icons added to this tray, or an
     * empty array if none has been added
     * @see #add(TrayIcon)
     * @see TrayIcon
     */
    public TrayIcon[] getTrayIcons() {
        Vector<TrayIcon> icons = (Vector<TrayIcon>)AppContext.getAppContext().get(TrayIcon.class);
        if (icons != null) {
            return (TrayIcon[])icons.toArray(new TrayIcon[icons.size()]);
        }
        return EMPTY_TRAY_ARRAY;
    }

    /**
     * Returns the size, in pixels, of the space that a tray icon will
     * occupy in the system tray.  Developers may use this methods to
     * acquire the preferred size for the image property of a tray icon
     * before it is created.  For convenience, there is a similar
     * method {@link TrayIcon#getSize} in the <code>TrayIcon</code> class.
     *
     * @return the default size of a tray icon, in pixels
     * @see TrayIcon#setImageAutoSize(boolean)
     * @see java.awt.Image
     * @see TrayIcon#getSize()
     */
    public Dimension getTrayIconSize() {
        return peer.getTrayIconSize();
    }

    /**
     * Adds a {@code PropertyChangeListener} to the list of listeners for the
     * specific property. The following properties are currently supported:
     *
     * <table border=1 summary="SystemTray properties">
     * <tr>
     *    <th>Property</th>
     *    <th>Description</th>
     * </tr>
     * <tr>
     *    <td>{@code trayIcons}</td>
     *    <td>The {@code SystemTray}'s array of {@code TrayIcon} objects.
     *        The array is accessed via the {@link #getTrayIcons} method.<br>
     *        This property is changed when a tray icon is added to (or removed
     *        from) the system tray.<br> For example, this property is changed
     *        when the system tray becomes unavailable on the desktop<br>
     *        and the tray icons are automatically removed.</td>
     * </tr>
     * <tr>
     *    <td>{@code systemTray}</td>
     *    <td>This property contains {@code SystemTray} instance when the system tray
     *        is available or <code>null</code> otherwise.<br> This property is changed
     *        when the system tray becomes available or unavailable on the desktop.<br>
     *        The property is accessed by the {@link #getSystemTray} method.</td>
     * </tr>
     * </table>
     * <p>
     * The {@code listener} listens to property changes only in this context.
     * <p>
     * If {@code listener} is {@code null}, no exception is thrown
     * and no action is performed.
     *
     * @param propertyName the specified property
     * @param listener the property change listener to be added
     *
     * @see #removePropertyChangeListener
     * @see #getPropertyChangeListeners
     */
    public synchronized void addPropertyChangeListener(String propertyName,
                                                       PropertyChangeListener listener)
    {
        if (listener == null) {
            return;
        }
        getCurrentChangeSupport().addPropertyChangeListener(propertyName, listener);
    }

    /**
     * Removes a {@code PropertyChangeListener} from the listener list
     * for a specific property.
     * <p>
     * The {@code PropertyChangeListener} must be from this context.
     * <p>
     * If {@code propertyName} or {@code listener} is {@code null} or invalid,
     * no exception is thrown and no action is taken.
     *
     * @param propertyName the specified property
     * @param listener the PropertyChangeListener to be removed
     *
     * @see #addPropertyChangeListener
     * @see #getPropertyChangeListeners
     */
    public synchronized void removePropertyChangeListener(String propertyName,
                                                          PropertyChangeListener listener)
    {
        if (listener == null) {
            return;
        }
        getCurrentChangeSupport().removePropertyChangeListener(propertyName, listener);
    }

    /**
     * Returns an array of all the listeners that have been associated
     * with the named property.
     * <p>
     * Only the listeners in this context are returned.
     *
     * @param propertyName the specified property
     * @return all of the {@code PropertyChangeListener}s associated with
     *         the named property; if no such listeners have been added or
     *         if {@code propertyName} is {@code null} or invalid, an empty
     *         array is returned
     *
     * @see #addPropertyChangeListener
     * @see #removePropertyChangeListener
     */
    public synchronized PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
        return getCurrentChangeSupport().getPropertyChangeListeners(propertyName);
    }


    // ***************************************************************
    // ***************************************************************


    /**
     * Support for reporting bound property changes for Object properties.
     * This method can be called when a bound property has changed and it will
     * send the appropriate PropertyChangeEvent to any registered
     * PropertyChangeListeners.
     *
     * @param propertyName the property whose value has changed
     * @param oldValue the property's previous value
     * @param newValue the property's new value
     */
    private void firePropertyChange(String propertyName,
                                    Object oldValue, Object newValue)
    {
        if (oldValue != null && newValue != null && oldValue.equals(newValue)) {
            return;
        }
        getCurrentChangeSupport().firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * Returns the current PropertyChangeSupport instance for the
     * calling thread's context.
     *
     * @return this thread's context's PropertyChangeSupport
     */
    private synchronized PropertyChangeSupport getCurrentChangeSupport() {
        PropertyChangeSupport changeSupport =
            (PropertyChangeSupport)AppContext.getAppContext().get(java.awt.SystemTray.class);

        if (changeSupport == null) {
            changeSupport = new PropertyChangeSupport(this);
            AppContext.getAppContext().put(java.awt.SystemTray.class, changeSupport);
        }
        return changeSupport;
    }

    synchronized void addNotify() {
        if (peer == null) {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            if (toolkit instanceof SunToolkit) {
                peer = ((SunToolkit)Toolkit.getDefaultToolkit()).createSystemTray(this);
            } else if (toolkit instanceof HeadlessToolkit) {
                peer = ((HeadlessToolkit)Toolkit.getDefaultToolkit()).createSystemTray(this);
            }
        }
    }

    static void checkSystemTrayAllowed() {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkPermission(SecurityConstants.AWT.ACCESS_SYSTEM_TRAY_PERMISSION);
        }
    }

    private static void initializeSystemTrayIfNeeded() {
        synchronized (java.awt.SystemTray.class) {
            if (systemTray == null) {
                systemTray = new java.awt.SystemTray();
            }
        }
    }
}
