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
import java.awt.MenuComponent;
import java.awt.event.KeyEvent;
import java.awt.peer.MenuPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Enumeration;
import java.util.Vector;

/**
 * A <code>Menu</code> object is a pull-down menu component
 * that is deployed from a menu bar.
 * <p>
 * A menu can optionally be a <i>tear-off</i> menu. A tear-off menu
 * can be opened and dragged away from its parent menu bar or menu.
 * It remains on the screen after the mouse button has been released.
 * The mechanism for tearing off a menu is platform dependent, since
 * the look and feel of the tear-off menu is determined by its peer.
 * On platforms that do not support tear-off menus, the tear-off
 * property is ignored.
 * <p>
 * Each item in a menu must belong to the <code>MenuItem</code>
 * class. It can be an instance of <code>MenuItem</code>, a submenu
 * (an instance of <code>Menu</code>), or a check box (an instance of
 * <code>CheckboxMenuItem</code>).
 *
 * @author Sami Shaio
 * @see     MenuItem
 * @see     CheckboxMenuItem
 * @since   JDK1.0
 */
public class Menu extends MenuItem implements MenuContainer, Accessible {

    static {
        /* ensure that the necessary native libraries are loaded */
        Toolkit.loadLibraries();
        if (!GraphicsEnvironment.isHeadless()) {
            initIDs();
        }

        AWTAccessor.setMenuAccessor(
            new AWTAccessor.MenuAccessor() {
                public Vector<MenuComponent> getItems(java.awt.Menu menu) {
                    return menu.items;
                }
            });
    }

    /**
     * A vector of the items that will be part of the Menu.
     *
     * @serial
     * @see #countItems()
     */
    Vector<MenuComponent> items = new Vector<>();

    /**
     * This field indicates whether the menu has the
     * tear of property or not.  It will be set to
     * <code>true</code> if the menu has the tear off
     * property and it will be set to <code>false</code>
     * if it does not.
     * A torn off menu can be deleted by a user when
     * it is no longer needed.
     *
     * @serial
     * @see #isTearOff()
     */
    boolean             tearOff;

    /**
     * This field will be set to <code>true</code>
     * if the Menu in question is actually a help
     * menu.  Otherwise it will be set to <code>
     * false</code>.
     *
     * @serial
     */
    boolean             isHelpMenu;

    private static final String base = "menu";
    private static int nameCounter = 0;

    /*
     * JDK 1.1 serialVersionUID
     */
     private static final long serialVersionUID = -8809584163345499784L;

    /**
     * Constructs a new menu with an empty label. This menu is not
     * a tear-off menu.
     * @exception HeadlessException if GraphicsEnvironment.isHeadless()
     * returns true.
     * @see GraphicsEnvironment#isHeadless
     * @since      JDK1.1
     */
    public Menu() throws HeadlessException {
        this("", false);
    }

    /**
     * Constructs a new menu with the specified label. This menu is not
     * a tear-off menu.
     * @param       label the menu's label in the menu bar, or in
     *                   another menu of which this menu is a submenu.
     * @exception HeadlessException if GraphicsEnvironment.isHeadless()
     * returns true.
     * @see GraphicsEnvironment#isHeadless
     */
    public Menu(String label) throws HeadlessException {
        this(label, false);
    }

    /**
     * Constructs a new menu with the specified label,
     * indicating whether the menu can be torn off.
     * <p>
     * Tear-off functionality may not be supported by all
     * implementations of AWT.  If a particular implementation doesn't
     * support tear-off menus, this value is silently ignored.
     * @param       label the menu's label in the menu bar, or in
     *                   another menu of which this menu is a submenu.
     * @param       tearOff   if <code>true</code>, the menu
     *                   is a tear-off menu.
     * @exception HeadlessException if GraphicsEnvironment.isHeadless()
     * returns true.
     * @see GraphicsEnvironment#isHeadless
     * @since       JDK1.0.
     */
    public Menu(String label, boolean tearOff) throws HeadlessException {
        super(label);
        this.tearOff = tearOff;
    }

    /**
     * Construct a name for this MenuComponent.  Called by getName() when
     * the name is null.
     */
    String constructComponentName() {
        synchronized (java.awt.Menu.class) {
            return base + nameCounter++;
        }
    }

    /**
     * Creates the menu's peer.  The peer allows us to modify the
     * appearance of the menu without changing its functionality.
     */
    public void addNotify() {
        synchronized (getTreeLock()) {
            if (peer == null)
                peer = Toolkit.getDefaultToolkit().createMenu(this);
            int nitems = getItemCount();
            for (int i = 0 ; i < nitems ; i++) {
                MenuItem mi = getItem(i);
                mi.parent = this;
                mi.addNotify();
            }
        }
    }

    /**
     * Removes the menu's peer.  The peer allows us to modify the appearance
     * of the menu without changing its functionality.
     */
    public void removeNotify() {
        synchronized (getTreeLock()) {
            int nitems = getItemCount();
            for (int i = 0 ; i < nitems ; i++) {
                getItem(i).removeNotify();
            }
            super.removeNotify();
        }
    }

    /**
     * Indicates whether this menu is a tear-off menu.
     * <p>
     * Tear-off functionality may not be supported by all
     * implementations of AWT.  If a particular implementation doesn't
     * support tear-off menus, this value is silently ignored.
     * @return      <code>true</code> if this is a tear-off menu;
     *                         <code>false</code> otherwise.
     */
    public boolean isTearOff() {
        return tearOff;
    }

    /**
      * Get the number of items in this menu.
      * @return     the number of items in this menu.
      * @since      JDK1.1
      */
    public int getItemCount() {
        return countItems();
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>getItemCount()</code>.
     */
    @Deprecated
    public int countItems() {
        return countItemsImpl();
    }

    /*
     * This is called by the native code, so client code can't
     * be called on the toolkit thread.
     */
    final int countItemsImpl() {
        return items.size();
    }

    /**
     * Gets the item located at the specified index of this menu.
     * @param     index the position of the item to be returned.
     * @return    the item located at the specified index.
     */
    public MenuItem getItem(int index) {
        return getItemImpl(index);
    }

    /*
     * This is called by the native code, so client code can't
     * be called on the toolkit thread.
     */
    final MenuItem getItemImpl(int index) {
        return (MenuItem)items.elementAt(index);
    }

    /**
     * Adds the specified menu item to this menu. If the
     * menu item has been part of another menu, removes it
     * from that menu.
     *
     * @param       mi   the menu item to be added
     * @return      the menu item added
     * @see         java.awt.Menu#insert(String, int)
     * @see         java.awt.Menu#insert(MenuItem, int)
     */
    public MenuItem add(MenuItem mi) {
        synchronized (getTreeLock()) {
            if (mi.parent != null) {
                mi.parent.remove(mi);
            }
            items.addElement(mi);
            mi.parent = this;
            MenuPeer peer = (MenuPeer)this.peer;
            if (peer != null) {
                mi.addNotify();
                peer.addItem(mi);
            }
            return mi;
        }
    }

    /**
     * Adds an item with the specified label to this menu.
     *
     * @param       label   the text on the item
     * @see         java.awt.Menu#insert(String, int)
     * @see         java.awt.Menu#insert(MenuItem, int)
     */
    public void add(String label) {
        add(new MenuItem(label));
    }

    /**
     * Inserts a menu item into this menu
     * at the specified position.
     *
     * @param         menuitem  the menu item to be inserted.
     * @param         index     the position at which the menu
     *                          item should be inserted.
     * @see           java.awt.Menu#add(String)
     * @see           java.awt.Menu#add(MenuItem)
     * @exception     IllegalArgumentException if the value of
     *                    <code>index</code> is less than zero
     * @since         JDK1.1
     */

    public void insert(MenuItem menuitem, int index) {
        synchronized (getTreeLock()) {
            if (index < 0) {
                throw new IllegalArgumentException("index less than zero.");
            }

            int nitems = getItemCount();
            Vector<MenuItem> tempItems = new Vector<>();

            /* Remove the item at index, nitems-index times
               storing them in a temporary vector in the
               order they appear on the menu.
            */
            for (int i = index ; i < nitems; i++) {
                tempItems.addElement(getItem(index));
                remove(index);
            }

            add(menuitem);

            /* Add the removed items back to the menu, they are
               already in the correct order in the temp vector.
            */
            for (int i = 0; i < tempItems.size()  ; i++) {
                add(tempItems.elementAt(i));
            }
        }
    }

    /**
     * Inserts a menu item with the specified label into this menu
     * at the specified position.  This is a convenience method for
     * <code>insert(menuItem, index)</code>.
     *
     * @param       label the text on the item
     * @param       index the position at which the menu item
     *                      should be inserted
     * @see         java.awt.Menu#add(String)
     * @see         java.awt.Menu#add(MenuItem)
     * @exception     IllegalArgumentException if the value of
     *                    <code>index</code> is less than zero
     * @since       JDK1.1
     */

    public void insert(String label, int index) {
        insert(new MenuItem(label), index);
    }

    /**
     * Adds a separator line, or a hypen, to the menu at the current position.
     * @see         java.awt.Menu#insertSeparator(int)
     */
    public void addSeparator() {
        add("-");
    }

    /**
     * Inserts a separator at the specified position.
     * @param       index the position at which the
     *                       menu separator should be inserted.
     * @exception   IllegalArgumentException if the value of
     *                       <code>index</code> is less than 0.
     * @see         java.awt.Menu#addSeparator
     * @since       JDK1.1
     */

    public void insertSeparator(int index) {
        synchronized (getTreeLock()) {
            if (index < 0) {
                throw new IllegalArgumentException("index less than zero.");
            }

            int nitems = getItemCount();
            Vector<MenuItem> tempItems = new Vector<>();

            /* Remove the item at index, nitems-index times
               storing them in a temporary vector in the
               order they appear on the menu.
            */
            for (int i = index ; i < nitems; i++) {
                tempItems.addElement(getItem(index));
                remove(index);
            }

            addSeparator();

            /* Add the removed items back to the menu, they are
               already in the correct order in the temp vector.
            */
            for (int i = 0; i < tempItems.size()  ; i++) {
                add(tempItems.elementAt(i));
            }
        }
    }

    /**
     * Removes the menu item at the specified index from this menu.
     * @param       index the position of the item to be removed.
     */
    public void remove(int index) {
        synchronized (getTreeLock()) {
            MenuItem mi = getItem(index);
            items.removeElementAt(index);
            MenuPeer peer = (MenuPeer)this.peer;
            if (peer != null) {
                mi.removeNotify();
                mi.parent = null;
                peer.delItem(index);
            }
        }
    }

    /**
     * Removes the specified menu item from this menu.
     * @param  item the item to be removed from the menu.
     *         If <code>item</code> is <code>null</code>
     *         or is not in this menu, this method does
     *         nothing.
     */
    public void remove(MenuComponent item) {
        synchronized (getTreeLock()) {
            int index = items.indexOf(item);
            if (index >= 0) {
                remove(index);
            }
        }
    }

    /**
     * Removes all items from this menu.
     * @since       JDK1.0.
     */
    public void removeAll() {
        synchronized (getTreeLock()) {
            int nitems = getItemCount();
            for (int i = nitems-1 ; i >= 0 ; i--) {
                remove(i);
            }
        }
    }

    /*
     * Post an ActionEvent to the target of the MenuPeer
     * associated with the specified keyboard event (on
     * keydown).  Returns true if there is an associated
     * keyboard event.
     */
    boolean handleShortcut(KeyEvent e) {
        int nitems = getItemCount();
        for (int i = 0 ; i < nitems ; i++) {
            MenuItem mi = getItem(i);
            if (mi.handleShortcut(e)) {
                return true;
            }
        }
        return false;
    }

    MenuItem getShortcutMenuItem(MenuShortcut s) {
        int nitems = getItemCount();
        for (int i = 0 ; i < nitems ; i++) {
            MenuItem mi = getItem(i).getShortcutMenuItem(s);
            if (mi != null) {
                return mi;
            }
        }
        return null;
    }

    synchronized Enumeration<MenuShortcut> shortcuts() {
        Vector<MenuShortcut> shortcuts = new Vector<>();
        int nitems = getItemCount();
        for (int i = 0 ; i < nitems ; i++) {
            MenuItem mi = getItem(i);
            if (mi instanceof java.awt.Menu) {
                Enumeration<MenuShortcut> e = ((java.awt.Menu)mi).shortcuts();
                while (e.hasMoreElements()) {
                    shortcuts.addElement(e.nextElement());
                }
            } else {
                MenuShortcut ms = mi.getShortcut();
                if (ms != null) {
                    shortcuts.addElement(ms);
                }
            }
        }
        return shortcuts.elements();
    }

    void deleteShortcut(MenuShortcut s) {
        int nitems = getItemCount();
        for (int i = 0 ; i < nitems ; i++) {
            getItem(i).deleteShortcut(s);
        }
    }


    /* Serialization support.  A MenuContainer is responsible for
     * restoring the parent fields of its children.
     */

    /**
     * The menu serialized Data Version.
     *
     * @serial
     */
    private int menuSerializedDataVersion = 1;

    /**
     * Writes default serializable fields to stream.
     *
     * @param s the <code>ObjectOutputStream</code> to write
     * @see AWTEventMulticaster#save(ObjectOutputStream, String, EventListener)
     * @see #readObject(ObjectInputStream)
     */
    private void writeObject(java.io.ObjectOutputStream s)
      throws IOException
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
     * @see #writeObject(ObjectOutputStream)
     */
    private void readObject(ObjectInputStream s)
      throws IOException, ClassNotFoundException, HeadlessException
    {
      // HeadlessException will be thrown from MenuComponent's readObject
      s.defaultReadObject();
      for(int i = 0; i < items.size(); i++) {
        MenuItem item = (MenuItem)items.elementAt(i);
        item.parent = this;
      }
    }

    /**
     * Returns a string representing the state of this <code>Menu</code>.
     * This method is intended to be used only for debugging purposes, and the
     * content and format of the returned string may vary between
     * implementations. The returned string may be empty but may not be
     * <code>null</code>.
     *
     * @return the parameter string of this menu
     */
    public String paramString() {
        String str = ",tearOff=" + tearOff+",isHelpMenu=" + isHelpMenu;
        return super.paramString() + str;
    }

    /**
     * Initialize JNI field and method IDs
     */
    private static native void initIDs();


/////////////////
// Accessibility support
////////////////

    /**
     * Gets the AccessibleContext associated with this Menu.
     * For menus, the AccessibleContext takes the form of an
     * AccessibleAWTMenu.
     * A new AccessibleAWTMenu instance is created if necessary.
     *
     * @return an AccessibleAWTMenu that serves as the
     *         AccessibleContext of this Menu
     * @since 1.3
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleAWTMenu();
        }
        return accessibleContext;
    }

    /**
     * Defined in MenuComponent. Overridden here.
     */
    int getAccessibleChildIndex(MenuComponent child) {
        return items.indexOf(child);
    }

    /**
     * Inner class of Menu used to provide default support for
     * accessibility.  This class is not meant to be used directly by
     * application developers, but is instead meant only to be
     * subclassed by menu component developers.
     * <p>
     * This class implements accessibility support for the
     * <code>Menu</code> class.  It provides an implementation of the
     * Java Accessibility API appropriate to menu user-interface elements.
     * @since 1.3
     */
    protected class AccessibleAWTMenu extends AccessibleAWTMenuItem
    {
        /*
         * JDK 1.3 serialVersionUID
         */
        private static final long serialVersionUID = 5228160894980069094L;

        /**
         * Get the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of the
         * object
         */
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.MENU;
        }

    } // class AccessibleAWTMenu

}
