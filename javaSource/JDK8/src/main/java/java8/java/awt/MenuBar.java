/*
 * Copyright (c) 1995, 2013, Oracle and/or its affiliates. All rights reserved.
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

import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import java.awt.Menu;
import java.awt.MenuComponent;
import java.awt.event.KeyEvent;
import java.awt.peer.MenuBarPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Enumeration;
import java.util.Vector;

/**
 * The <code>MenuBar</code> class encapsulates the platform's
 * concept of a menu bar bound to a frame. In order to associate
 * the menu bar with a <code>Frame</code> object, call the
 * frame's <code>setMenuBar</code> method.
 * <p>
 * <A NAME="mbexample"></A><!-- target for cross references -->
 * This is what a menu bar might look like:
 * <p>
 * <img src="doc-files/MenuBar-1.gif"
 * alt="Diagram of MenuBar containing 2 menus: Examples and Options.
 * Examples menu is expanded showing items: Basic, Simple, Check, and More Examples."
 * style="float:center; margin: 7px 10px;">
 * <p>
 * A menu bar handles keyboard shortcuts for menu items, passing them
 * along to its child menus.
 * (Keyboard shortcuts, which are optional, provide the user with
 * an alternative to the mouse for invoking a menu item and the
 * action that is associated with it.)
 * Each menu item can maintain an instance of <code>MenuShortcut</code>.
 * The <code>MenuBar</code> class defines several methods,
 * {@link java.awt.MenuBar#shortcuts} and
 * {@link java.awt.MenuBar#getShortcutMenuItem}
 * that retrieve information about the shortcuts a given
 * menu bar is managing.
 *
 * @author Sami Shaio
 * @see        Frame
 * @see        Frame#setMenuBar(java.awt.MenuBar)
 * @see        Menu
 * @see        MenuItem
 * @see        MenuShortcut
 * @since      JDK1.0
 */
public class MenuBar extends MenuComponent implements MenuContainer, Accessible {

    static {
        /* ensure that the necessary native libraries are loaded */
        Toolkit.loadLibraries();
        if (!GraphicsEnvironment.isHeadless()) {
            initIDs();
        }
        AWTAccessor.setMenuBarAccessor(
            new AWTAccessor.MenuBarAccessor() {
                public Menu getHelpMenu(java.awt.MenuBar menuBar) {
                    return menuBar.helpMenu;
                }

                public Vector<Menu> getMenus(java.awt.MenuBar menuBar) {
                    return menuBar.menus;
                }
            });
    }

    /**
     * This field represents a vector of the
     * actual menus that will be part of the MenuBar.
     *
     * @serial
     * @see #countMenus()
     */
    Vector<Menu> menus = new Vector<>();

    /**
     * This menu is a special menu dedicated to
     * help.  The one thing to note about this menu
     * is that on some platforms it appears at the
     * right edge of the menubar.
     *
     * @serial
     * @see #getHelpMenu()
     * @see #setHelpMenu(Menu)
     */
    Menu helpMenu;

    private static final String base = "menubar";
    private static int nameCounter = 0;

    /*
     * JDK 1.1 serialVersionUID
     */
     private static final long serialVersionUID = -4930327919388951260L;

    /**
     * Creates a new menu bar.
     * @exception HeadlessException if GraphicsEnvironment.isHeadless()
     * returns true.
     * @see GraphicsEnvironment#isHeadless
     */
    public MenuBar() throws HeadlessException {
    }

    /**
     * Construct a name for this MenuComponent.  Called by getName() when
     * the name is null.
     */
    String constructComponentName() {
        synchronized (java.awt.MenuBar.class) {
            return base + nameCounter++;
        }
    }

    /**
     * Creates the menu bar's peer.  The peer allows us to change the
     * appearance of the menu bar without changing any of the menu bar's
     * functionality.
     */
    public void addNotify() {
        synchronized (getTreeLock()) {
            if (peer == null)
                peer = Toolkit.getDefaultToolkit().createMenuBar(this);

            int nmenus = getMenuCount();
            for (int i = 0 ; i < nmenus ; i++) {
                getMenu(i).addNotify();
            }
        }
    }

    /**
     * Removes the menu bar's peer.  The peer allows us to change the
     * appearance of the menu bar without changing any of the menu bar's
     * functionality.
     */
    public void removeNotify() {
        synchronized (getTreeLock()) {
            int nmenus = getMenuCount();
            for (int i = 0 ; i < nmenus ; i++) {
                getMenu(i).removeNotify();
            }
            super.removeNotify();
        }
    }

    /**
     * Gets the help menu on the menu bar.
     * @return    the help menu on this menu bar.
     */
    public Menu getHelpMenu() {
        return helpMenu;
    }

    /**
     * Sets the specified menu to be this menu bar's help menu.
     * If this menu bar has an existing help menu, the old help menu is
     * removed from the menu bar, and replaced with the specified menu.
     * @param m    the menu to be set as the help menu
     */
    public void setHelpMenu(Menu m) {
        synchronized (getTreeLock()) {
            if (helpMenu == m) {
                return;
            }
            if (helpMenu != null) {
                remove(helpMenu);
            }
            if (m.parent != this) {
                add(m);
            }
            helpMenu = m;
            if (m != null) {
                m.isHelpMenu = true;
                m.parent = this;
                MenuBarPeer peer = (MenuBarPeer)this.peer;
                if (peer != null) {
                    if (m.peer == null) {
                        m.addNotify();
                    }
                    peer.addHelpMenu(m);
                }
            }
        }
    }

    /**
     * Adds the specified menu to the menu bar.
     * If the menu has been part of another menu bar,
     * removes it from that menu bar.
     *
     * @param        m   the menu to be added
     * @return       the menu added
     * @see          java.awt.MenuBar#remove(int)
     * @see          java.awt.MenuBar#remove(MenuComponent)
     */
    public Menu add(Menu m) {
        synchronized (getTreeLock()) {
            if (m.parent != null) {
                m.parent.remove(m);
            }
            menus.addElement(m);
            m.parent = this;

            MenuBarPeer peer = (MenuBarPeer)this.peer;
            if (peer != null) {
                if (m.peer == null) {
                    m.addNotify();
                }
                peer.addMenu(m);
            }
            return m;
        }
    }

    /**
     * Removes the menu located at the specified
     * index from this menu bar.
     * @param        index   the position of the menu to be removed.
     * @see          java.awt.MenuBar#add(Menu)
     */
    public void remove(int index) {
        synchronized (getTreeLock()) {
            Menu m = getMenu(index);
            menus.removeElementAt(index);
            MenuBarPeer peer = (MenuBarPeer)this.peer;
            if (peer != null) {
                m.removeNotify();
                m.parent = null;
                peer.delMenu(index);
            }
        }
    }

    /**
     * Removes the specified menu component from this menu bar.
     * @param        m the menu component to be removed.
     * @see          java.awt.MenuBar#add(Menu)
     */
    public void remove(MenuComponent m) {
        synchronized (getTreeLock()) {
            int index = menus.indexOf(m);
            if (index >= 0) {
                remove(index);
            }
        }
    }

    /**
     * Gets the number of menus on the menu bar.
     * @return     the number of menus on the menu bar.
     * @since      JDK1.1
     */
    public int getMenuCount() {
        return countMenus();
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>getMenuCount()</code>.
     */
    @Deprecated
    public int countMenus() {
        return getMenuCountImpl();
    }

    /*
     * This is called by the native code, so client code can't
     * be called on the toolkit thread.
     */
    final int getMenuCountImpl() {
        return menus.size();
    }

    /**
     * Gets the specified menu.
     * @param      i the index position of the menu to be returned.
     * @return     the menu at the specified index of this menu bar.
     */
    public Menu getMenu(int i) {
        return getMenuImpl(i);
    }

    /*
     * This is called by the native code, so client code can't
     * be called on the toolkit thread.
     */
    final Menu getMenuImpl(int i) {
        return menus.elementAt(i);
    }

    /**
     * Gets an enumeration of all menu shortcuts this menu bar
     * is managing.
     * @return      an enumeration of menu shortcuts that this
     *                      menu bar is managing.
     * @see         MenuShortcut
     * @since       JDK1.1
     */
    public synchronized Enumeration<MenuShortcut> shortcuts() {
        Vector<MenuShortcut> shortcuts = new Vector<>();
        int nmenus = getMenuCount();
        for (int i = 0 ; i < nmenus ; i++) {
            Enumeration<MenuShortcut> e = getMenu(i).shortcuts();
            while (e.hasMoreElements()) {
                shortcuts.addElement(e.nextElement());
            }
        }
        return shortcuts.elements();
    }

    /**
     * Gets the instance of <code>MenuItem</code> associated
     * with the specified <code>MenuShortcut</code> object,
     * or <code>null</code> if none of the menu items being managed
     * by this menu bar is associated with the specified menu
     * shortcut.
     * @param        s the specified menu shortcut.
     * @see          MenuItem
     * @see          MenuShortcut
     * @since        JDK1.1
     */
     public MenuItem getShortcutMenuItem(MenuShortcut s) {
        int nmenus = getMenuCount();
        for (int i = 0 ; i < nmenus ; i++) {
            MenuItem mi = getMenu(i).getShortcutMenuItem(s);
            if (mi != null) {
                return mi;
            }
        }
        return null;  // MenuShortcut wasn't found
     }

    /*
     * Post an ACTION_EVENT to the target of the MenuPeer
     * associated with the specified keyboard event (on
     * keydown).  Returns true if there is an associated
     * keyboard event.
     */
    boolean handleShortcut(KeyEvent e) {
        // Is it a key event?
        int id = e.getID();
        if (id != KeyEvent.KEY_PRESSED && id != KeyEvent.KEY_RELEASED) {
            return false;
        }

        // Is the accelerator modifier key pressed?
        int accelKey = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        if ((e.getModifiers() & accelKey) == 0) {
            return false;
        }

        // Pass MenuShortcut on to child menus.
        int nmenus = getMenuCount();
        for (int i = 0 ; i < nmenus ; i++) {
            Menu m = getMenu(i);
            if (m.handleShortcut(e)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Deletes the specified menu shortcut.
     * @param     s the menu shortcut to delete.
     * @since     JDK1.1
     */
    public void deleteShortcut(MenuShortcut s) {
        int nmenus = getMenuCount();
        for (int i = 0 ; i < nmenus ; i++) {
            getMenu(i).deleteShortcut(s);
        }
    }

    /* Serialization support.  Restore the (transient) parent
     * fields of Menubar menus here.
     */

    /**
     * The MenuBar's serialized data version.
     *
     * @serial
     */
    private int menuBarSerializedDataVersion = 1;

    /**
     * Writes default serializable fields to stream.
     *
     * @param s the <code>ObjectOutputStream</code> to write
     * @see AWTEventMulticaster#save(ObjectOutputStream, String, EventListener)
     * @see #readObject(ObjectInputStream)
     */
    private void writeObject(java.io.ObjectOutputStream s)
      throws ClassNotFoundException,
             IOException
    {
      s.defaultWriteObject();
    }

    /**
     * Reads the <code>ObjectInputStream</code>.
     * Unrecognized keys or values will be ignored.
     *
     * @param s the <code>ObjectInputStream</code> to read
     * @exception HeadlessException if
     *   <code>GraphicsEnvironment.isHeadless</code> returns
     *   <code>true</code>
     * @see GraphicsEnvironment#isHeadless
     * @see #writeObject(java.io.ObjectOutputStream)
     */
    private void readObject(ObjectInputStream s)
      throws ClassNotFoundException, IOException, HeadlessException
    {
      // HeadlessException will be thrown from MenuComponent's readObject
      s.defaultReadObject();
      for (int i = 0; i < menus.size(); i++) {
        Menu m = menus.elementAt(i);
        m.parent = this;
      }
    }

    /**
     * Initialize JNI field and method IDs
     */
    private static native void initIDs();


/////////////////
// Accessibility support
////////////////

    /**
     * Gets the AccessibleContext associated with this MenuBar.
     * For menu bars, the AccessibleContext takes the form of an
     * AccessibleAWTMenuBar.
     * A new AccessibleAWTMenuBar instance is created if necessary.
     *
     * @return an AccessibleAWTMenuBar that serves as the
     *         AccessibleContext of this MenuBar
     * @since 1.3
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleAWTMenuBar();
        }
        return accessibleContext;
    }

    /**
     * Defined in MenuComponent. Overridden here.
     */
    int getAccessibleChildIndex(MenuComponent child) {
        return menus.indexOf(child);
    }

    /**
     * Inner class of MenuBar used to provide default support for
     * accessibility.  This class is not meant to be used directly by
     * application developers, but is instead meant only to be
     * subclassed by menu component developers.
     * <p>
     * This class implements accessibility support for the
     * <code>MenuBar</code> class.  It provides an implementation of the
     * Java Accessibility API appropriate to menu bar user-interface elements.
     * @since 1.3
     */
    protected class AccessibleAWTMenuBar extends AccessibleAWTMenuComponent
    {
        /*
         * JDK 1.3 serialVersionUID
         */
        private static final long serialVersionUID = -8577604491830083815L;

        /**
         * Get the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of the
         * object
         * @since 1.4
         */
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.MENU_BAR;
        }

    } // class AccessibleAWTMenuBar

}
