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

import javax.accessibility.*;
import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.GraphicsEnvironment;
import java.awt.Menu;
import java.awt.MenuComponent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.peer.MenuItemPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EventListener;

/**
 * All items in a menu must belong to the class
 * <code>MenuItem</code>, or one of its subclasses.
 * <p>
 * The default <code>MenuItem</code> object embodies
 * a simple labeled menu item.
 * <p>
 * This picture of a menu bar shows five menu items:
 * <IMG SRC="doc-files/MenuBar-1.gif" alt="The following text describes this graphic."
 * style="float:center; margin: 7px 10px;">
 * <br style="clear:left;">
 * The first two items are simple menu items, labeled
 * <code>"Basic"</code> and <code>"Simple"</code>.
 * Following these two items is a separator, which is itself
 * a menu item, created with the label <code>"-"</code>.
 * Next is an instance of <code>CheckboxMenuItem</code>
 * labeled <code>"Check"</code>. The final menu item is a
 * submenu labeled <code>"More&nbsp;Examples"</code>,
 * and this submenu is an instance of <code>Menu</code>.
 * <p>
 * When a menu item is selected, AWT sends an action event to
 * the menu item. Since the event is an
 * instance of <code>ActionEvent</code>, the <code>processEvent</code>
 * method examines the event and passes it along to
 * <code>processActionEvent</code>. The latter method redirects the
 * event to any <code>ActionListener</code> objects that have
 * registered an interest in action events generated by this
 * menu item.
 * <P>
 * Note that the subclass <code>Menu</code> overrides this behavior and
 * does not send any event to the frame until one of its subitems is
 * selected.
 *
 * @author Sami Shaio
 */
public class MenuItem extends MenuComponent implements Accessible {

    static {
        /* ensure that the necessary native libraries are loaded */
        Toolkit.loadLibraries();
        if (!GraphicsEnvironment.isHeadless()) {
            initIDs();
        }

        AWTAccessor.setMenuItemAccessor(
            new AWTAccessor.MenuItemAccessor() {
                public boolean isEnabled(java.awt.MenuItem item) {
                    return item.enabled;
                }

                public String getLabel(java.awt.MenuItem item) {
                    return item.label;
                }

                public MenuShortcut getShortcut(java.awt.MenuItem item) {
                    return item.shortcut;
                }

                public String getActionCommandImpl(java.awt.MenuItem item) {
                    return item.getActionCommandImpl();
                }

                public boolean isItemEnabled(java.awt.MenuItem item) {
                    return item.isItemEnabled();
                }
            });
    }

    /**
     * A value to indicate whether a menu item is enabled
     * or not.  If it is enabled, <code>enabled</code> will
     * be set to true.  Else <code>enabled</code> will
     * be set to false.
     *
     * @serial
     * @see #isEnabled()
     * @see #setEnabled(boolean)
     */
    boolean enabled = true;

    /**
     * <code>label</code> is the label of a menu item.
     * It can be any string.
     *
     * @serial
     * @see #getLabel()
     * @see #setLabel(String)
     */
    String label;

    /**
     * This field indicates the command tha has been issued
     * by a  particular menu item.
     * By default the <code>actionCommand</code>
     * is the label of the menu item, unless it has been
     * set using setActionCommand.
     *
     * @serial
     * @see #setActionCommand(String)
     * @see #getActionCommand()
     */
    String actionCommand;

    /**
     * The eventMask is ONLY set by subclasses via enableEvents.
     * The mask should NOT be set when listeners are registered
     * so that we can distinguish the difference between when
     * listeners request events and subclasses request them.
     *
     * @serial
     */
    long eventMask;

    transient ActionListener actionListener;

    /**
     * A sequence of key stokes that ia associated with
     * a menu item.
     * Note :in 1.1.2 you must use setActionCommand()
     * on a menu item in order for its shortcut to
     * work.
     *
     * @serial
     * @see #getShortcut()
     * @see #setShortcut(MenuShortcut)
     * @see #deleteShortcut()
     */
    private MenuShortcut shortcut = null;

    private static final String base = "menuitem";
    private static int nameCounter = 0;

    /*
     * JDK 1.1 serialVersionUID
     */
    private static final long serialVersionUID = -21757335363267194L;

    /**
     * Constructs a new MenuItem with an empty label and no keyboard
     * shortcut.
     * @exception HeadlessException if GraphicsEnvironment.isHeadless()
     * returns true.
     * @see GraphicsEnvironment#isHeadless
     * @since    JDK1.1
     */
    public MenuItem() throws HeadlessException {
        this("", null);
    }

    /**
     * Constructs a new MenuItem with the specified label
     * and no keyboard shortcut. Note that use of "-" in
     * a label is reserved to indicate a separator between
     * menu items. By default, all menu items except for
     * separators are enabled.
     * @param       label the label for this menu item.
     * @exception HeadlessException if GraphicsEnvironment.isHeadless()
     * returns true.
     * @see GraphicsEnvironment#isHeadless
     * @since       JDK1.0
     */
    public MenuItem(String label) throws HeadlessException {
        this(label, null);
    }

    /**
     * Create a menu item with an associated keyboard shortcut.
     * Note that use of "-" in a label is reserved to indicate
     * a separator between menu items. By default, all menu
     * items except for separators are enabled.
     * @param       label the label for this menu item.
     * @param       s the instance of <code>MenuShortcut</code>
     *                       associated with this menu item.
     * @exception HeadlessException if GraphicsEnvironment.isHeadless()
     * returns true.
     * @see GraphicsEnvironment#isHeadless
     * @since       JDK1.1
     */
    public MenuItem(String label, MenuShortcut s) throws HeadlessException {
        this.label = label;
        this.shortcut = s;
    }

    /**
     * Construct a name for this MenuComponent.  Called by getName() when
     * the name is null.
     */
    String constructComponentName() {
        synchronized (java.awt.MenuItem.class) {
            return base + nameCounter++;
        }
    }

    /**
     * Creates the menu item's peer.  The peer allows us to modify the
     * appearance of the menu item without changing its functionality.
     */
    public void addNotify() {
        synchronized (getTreeLock()) {
            if (peer == null)
                peer = Toolkit.getDefaultToolkit().createMenuItem(this);
        }
    }

    /**
     * Gets the label for this menu item.
     * @return  the label of this menu item, or <code>null</code>
                       if this menu item has no label.
     * @see     java.awt.MenuItem#setLabel
     * @since   JDK1.0
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the label for this menu item to the specified label.
     * @param     label   the new label, or <code>null</code> for no label.
     * @see       java.awt.MenuItem#getLabel
     * @since     JDK1.0
     */
    public synchronized void setLabel(String label) {
        this.label = label;
        MenuItemPeer peer = (MenuItemPeer)this.peer;
        if (peer != null) {
            peer.setLabel(label);
        }
    }

    /**
     * Checks whether this menu item is enabled.
     * @see        java.awt.MenuItem#setEnabled
     * @since      JDK1.0
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets whether or not this menu item can be chosen.
     * @param      b  if <code>true</code>, enables this menu item;
     *                       if <code>false</code>, disables it.
     * @see        java.awt.MenuItem#isEnabled
     * @since      JDK1.1
     */
    public synchronized void setEnabled(boolean b) {
        enable(b);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>setEnabled(boolean)</code>.
     */
    @Deprecated
    public synchronized void enable() {
        enabled = true;
        MenuItemPeer peer = (MenuItemPeer)this.peer;
        if (peer != null) {
            peer.setEnabled(true);
        }
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>setEnabled(boolean)</code>.
     */
    @Deprecated
    public void enable(boolean b) {
        if (b) {
            enable();
        } else {
            disable();
        }
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>setEnabled(boolean)</code>.
     */
    @Deprecated
    public synchronized void disable() {
        enabled = false;
        MenuItemPeer peer = (MenuItemPeer)this.peer;
        if (peer != null) {
            peer.setEnabled(false);
        }
    }

    /**
     * Get the <code>MenuShortcut</code> object associated with this
     * menu item,
     * @return      the menu shortcut associated with this menu item,
     *                   or <code>null</code> if none has been specified.
     * @see         java.awt.MenuItem#setShortcut
     * @since       JDK1.1
     */
    public MenuShortcut getShortcut() {
        return shortcut;
    }

    /**
     * Set the <code>MenuShortcut</code> object associated with this
     * menu item. If a menu shortcut is already associated with
     * this menu item, it is replaced.
     * @param       s  the menu shortcut to associate
     *                           with this menu item.
     * @see         java.awt.MenuItem#getShortcut
     * @since       JDK1.1
     */
    public void setShortcut(MenuShortcut s) {
        shortcut = s;
        MenuItemPeer peer = (MenuItemPeer)this.peer;
        if (peer != null) {
            peer.setLabel(label);
        }
    }

    /**
     * Delete any <code>MenuShortcut</code> object associated
     * with this menu item.
     * @since      JDK1.1
     */
    public void deleteShortcut() {
        shortcut = null;
        MenuItemPeer peer = (MenuItemPeer)this.peer;
        if (peer != null) {
            peer.setLabel(label);
        }
    }

    /*
     * Delete a matching MenuShortcut associated with this MenuItem.
     * Used when iterating Menus.
     */
    void deleteShortcut(MenuShortcut s) {
        if (s.equals(shortcut)) {
            shortcut = null;
            MenuItemPeer peer = (MenuItemPeer)this.peer;
            if (peer != null) {
                peer.setLabel(label);
            }
        }
    }

    /*
     * The main goal of this method is to post an appropriate event
     * to the event queue when menu shortcut is pressed. However,
     * in subclasses this method may do more than just posting
     * an event.
     */
    void doMenuEvent(long when, int modifiers) {
        Toolkit.getEventQueue().postEvent(
            new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
                            getActionCommand(), when, modifiers));
    }

    /*
     * Returns true if the item and all its ancestors are
     * enabled, false otherwise
     */
    private final boolean isItemEnabled() {
        // Fix For 6185151: Menu shortcuts of all menuitems within a menu
        // should be disabled when the menu itself is disabled
        if (!isEnabled()) {
            return false;
        }
        MenuContainer container = getParent_NoClientCode();
        do {
            if (!(container instanceof Menu)) {
                return true;
            }
            Menu menu = (Menu)container;
            if (!menu.isEnabled()) {
                return false;
            }
            container = menu.getParent_NoClientCode();
        } while (container != null);
        return true;
    }

    /*
     * Post an ActionEvent to the target (on
     * keydown) and the item is enabled.
     * Returns true if there is an associated shortcut.
     */
    boolean handleShortcut(KeyEvent e) {
        MenuShortcut s = new MenuShortcut(e.getKeyCode(),
                             (e.getModifiers() & InputEvent.SHIFT_MASK) > 0);
        MenuShortcut sE = new MenuShortcut(e.getExtendedKeyCode(),
                             (e.getModifiers() & InputEvent.SHIFT_MASK) > 0);
        // Fix For 6185151: Menu shortcuts of all menuitems within a menu
        // should be disabled when the menu itself is disabled
        if ((s.equals(shortcut) || sE.equals(shortcut)) && isItemEnabled()) {
            // MenuShortcut match -- issue an event on keydown.
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                doMenuEvent(e.getWhen(), e.getModifiers());
            } else {
                // silently eat key release.
            }
            return true;
        }
        return false;
    }

    java.awt.MenuItem getShortcutMenuItem(MenuShortcut s) {
        return (s.equals(shortcut)) ? this : null;
    }

    /**
     * Enables event delivery to this menu item for events
     * to be defined by the specified event mask parameter
     * <p>
     * Since event types are automatically enabled when a listener for
     * that type is added to the menu item, this method only needs
     * to be invoked by subclasses of <code>MenuItem</code> which desire to
     * have the specified event types delivered to <code>processEvent</code>
     * regardless of whether a listener is registered.
     *
     * @param       eventsToEnable the event mask defining the event types
     * @see         java.awt.MenuItem#processEvent
     * @see         java.awt.MenuItem#disableEvents
     * @see         Component#enableEvents
     * @since       JDK1.1
     */
    protected final void enableEvents(long eventsToEnable) {
        eventMask |= eventsToEnable;
        newEventsOnly = true;
    }

    /**
     * Disables event delivery to this menu item for events
     * defined by the specified event mask parameter.
     *
     * @param       eventsToDisable the event mask defining the event types
     * @see         java.awt.MenuItem#processEvent
     * @see         java.awt.MenuItem#enableEvents
     * @see         Component#disableEvents
     * @since       JDK1.1
     */
    protected final void disableEvents(long eventsToDisable) {
        eventMask &= ~eventsToDisable;
    }

    /**
     * Sets the command name of the action event that is fired
     * by this menu item.
     * <p>
     * By default, the action command is set to the label of
     * the menu item.
     * @param       command   the action command to be set
     *                                for this menu item.
     * @see         java.awt.MenuItem#getActionCommand
     * @since       JDK1.1
     */
    public void setActionCommand(String command) {
        actionCommand = command;
    }

    /**
     * Gets the command name of the action event that is fired
     * by this menu item.
     * @see         java.awt.MenuItem#setActionCommand
     * @since       JDK1.1
     */
    public String getActionCommand() {
        return getActionCommandImpl();
    }

    // This is final so it can be called on the Toolkit thread.
    final String getActionCommandImpl() {
        return (actionCommand == null? label : actionCommand);
    }

    /**
     * Adds the specified action listener to receive action events
     * from this menu item.
     * If l is null, no exception is thrown and no action is performed.
     * <p>Refer to <a href="doc-files/AWTThreadIssues.html#ListenersThreads"
     * >AWT Threading Issues</a> for details on AWT's threading model.
     *
     * @param      l the action listener.
     * @see        #removeActionListener
     * @see        #getActionListeners
     * @see        ActionEvent
     * @see        ActionListener
     * @since      JDK1.1
     */
    public synchronized void addActionListener(ActionListener l) {
        if (l == null) {
            return;
        }
        actionListener = AWTEventMulticaster.add(actionListener, l);
        newEventsOnly = true;
    }

    /**
     * Removes the specified action listener so it no longer receives
     * action events from this menu item.
     * If l is null, no exception is thrown and no action is performed.
     * <p>Refer to <a href="doc-files/AWTThreadIssues.html#ListenersThreads"
     * >AWT Threading Issues</a> for details on AWT's threading model.
     *
     * @param      l the action listener.
     * @see        #addActionListener
     * @see        #getActionListeners
     * @see        ActionEvent
     * @see        ActionListener
     * @since      JDK1.1
     */
    public synchronized void removeActionListener(ActionListener l) {
        if (l == null) {
            return;
        }
        actionListener = AWTEventMulticaster.remove(actionListener, l);
    }

    /**
     * Returns an array of all the action listeners
     * registered on this menu item.
     *
     * @return all of this menu item's <code>ActionListener</code>s
     *         or an empty array if no action
     *         listeners are currently registered
     *
     * @see        #addActionListener
     * @see        #removeActionListener
     * @see        ActionEvent
     * @see        ActionListener
     * @since 1.4
     */
    public synchronized ActionListener[] getActionListeners() {
        return getListeners(ActionListener.class);
    }

    /**
     * Returns an array of all the objects currently registered
     * as <code><em>Foo</em>Listener</code>s
     * upon this <code>MenuItem</code>.
     * <code><em>Foo</em>Listener</code>s are registered using the
     * <code>add<em>Foo</em>Listener</code> method.
     *
     * <p>
     * You can specify the <code>listenerType</code> argument
     * with a class literal, such as
     * <code><em>Foo</em>Listener.class</code>.
     * For example, you can query a
     * <code>MenuItem</code> <code>m</code>
     * for its action listeners with the following code:
     *
     * <pre>ActionListener[] als = (ActionListener[])(m.getListeners(ActionListener.class));</pre>
     *
     * If no such listeners exist, this method returns an empty array.
     *
     * @param listenerType the type of listeners requested; this parameter
     *          should specify an interface that descends from
     *          <code>java.util.EventListener</code>
     * @return an array of all objects registered as
     *          <code><em>Foo</em>Listener</code>s on this menu item,
     *          or an empty array if no such
     *          listeners have been added
     * @exception ClassCastException if <code>listenerType</code>
     *          doesn't specify a class or interface that implements
     *          <code>java.util.EventListener</code>
     *
     * @see #getActionListeners
     * @since 1.3
     */
    public <T extends EventListener> T[] getListeners(Class<T> listenerType) {
        EventListener l = null;
        if  (listenerType == ActionListener.class) {
            l = actionListener;
        }
        return AWTEventMulticaster.getListeners(l, listenerType);
    }

    /**
     * Processes events on this menu item. If the event is an
     * instance of <code>ActionEvent</code>, it invokes
     * <code>processActionEvent</code>, another method
     * defined by <code>MenuItem</code>.
     * <p>
     * Currently, menu items only support action events.
     * <p>Note that if the event parameter is <code>null</code>
     * the behavior is unspecified and may result in an
     * exception.
     *
     * @param       e the event
     * @see         java.awt.MenuItem#processActionEvent
     * @since       JDK1.1
     */
    protected void processEvent(AWTEvent e) {
        if (e instanceof ActionEvent) {
            processActionEvent((ActionEvent)e);
        }
    }

    // REMIND: remove when filtering is done at lower level
    boolean eventEnabled(AWTEvent e) {
        if (e.id == ActionEvent.ACTION_PERFORMED) {
            if ((eventMask & AWTEvent.ACTION_EVENT_MASK) != 0 ||
                actionListener != null) {
                return true;
            }
            return false;
        }
        return super.eventEnabled(e);
    }

    /**
     * Processes action events occurring on this menu item,
     * by dispatching them to any registered
     * <code>ActionListener</code> objects.
     * This method is not called unless action events are
     * enabled for this component. Action events are enabled
     * when one of the following occurs:
     * <ul>
     * <li>An <code>ActionListener</code> object is registered
     * via <code>addActionListener</code>.
     * <li>Action events are enabled via <code>enableEvents</code>.
     * </ul>
     * <p>Note that if the event parameter is <code>null</code>
     * the behavior is unspecified and may result in an
     * exception.
     *
     * @param       e the action event
     * @see         ActionEvent
     * @see         ActionListener
     * @see         java.awt.MenuItem#enableEvents
     * @since       JDK1.1
     */
    protected void processActionEvent(ActionEvent e) {
        ActionListener listener = actionListener;
        if (listener != null) {
            listener.actionPerformed(e);
        }
    }

    /**
     * Returns a string representing the state of this <code>MenuItem</code>.
     * This method is intended to be used only for debugging purposes, and the
     * content and format of the returned string may vary between
     * implementations. The returned string may be empty but may not be
     * <code>null</code>.
     *
     * @return the parameter string of this menu item
     */
    public String paramString() {
        String str = ",label=" + label;
        if (shortcut != null) {
            str += ",shortcut=" + shortcut;
        }
        return super.paramString() + str;
    }


    /* Serialization support.
     */

    /**
     * Menu item serialized data version.
     *
     * @serial
     */
    private int menuItemSerializedDataVersion = 1;

    /**
     * Writes default serializable fields to stream.  Writes
     * a list of serializable <code>ActionListeners</code>
     * as optional data. The non-serializable listeners are
     * detected and no attempt is made to serialize them.
     *
     * @param s the <code>ObjectOutputStream</code> to write
     * @serialData <code>null</code> terminated sequence of 0
     *   or more pairs; the pair consists of a <code>String</code>
     *   and an <code>Object</code>; the <code>String</code>
     *   indicates the type of object and is one of the following:
     *   <code>actionListenerK</code> indicating an
     *     <code>ActionListener</code> object
     *
     * @see AWTEventMulticaster#save(ObjectOutputStream, String, EventListener)
     * @see #readObject(ObjectInputStream)
     */
    private void writeObject(ObjectOutputStream s)
      throws IOException
    {
      s.defaultWriteObject();

      AWTEventMulticaster.save(s, actionListenerK, actionListener);
      s.writeObject(null);
    }

    /**
     * Reads the <code>ObjectInputStream</code> and if it
     * isn't <code>null</code> adds a listener to receive
     * action events fired by the <code>Menu</code> Item.
     * Unrecognized keys or values will be ignored.
     *
     * @param s the <code>ObjectInputStream</code> to read
     * @exception HeadlessException if
     *   <code>GraphicsEnvironment.isHeadless</code> returns
     *   <code>true</code>
     * @see #removeActionListener(ActionListener)
     * @see #addActionListener(ActionListener)
     * @see #writeObject(ObjectOutputStream)
     */
    private void readObject(ObjectInputStream s)
      throws ClassNotFoundException, IOException, HeadlessException
    {
      // HeadlessException will be thrown from MenuComponent's readObject
      s.defaultReadObject();

      Object keyOrNull;
      while(null != (keyOrNull = s.readObject())) {
        String key = ((String)keyOrNull).intern();

        if (actionListenerK == key)
          addActionListener((ActionListener)(s.readObject()));

        else // skip value for unrecognized key
          s.readObject();
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
     * Gets the AccessibleContext associated with this MenuItem.
     * For menu items, the AccessibleContext takes the form of an
     * AccessibleAWTMenuItem.
     * A new AccessibleAWTMenuItem instance is created if necessary.
     *
     * @return an AccessibleAWTMenuItem that serves as the
     *         AccessibleContext of this MenuItem
     * @since 1.3
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleAWTMenuItem();
        }
        return accessibleContext;
    }

    /**
     * Inner class of MenuItem used to provide default support for
     * accessibility.  This class is not meant to be used directly by
     * application developers, but is instead meant only to be
     * subclassed by menu component developers.
     * <p>
     * This class implements accessibility support for the
     * <code>MenuItem</code> class.  It provides an implementation of the
     * Java Accessibility API appropriate to menu item user-interface elements.
     * @since 1.3
     */
    protected class AccessibleAWTMenuItem extends AccessibleAWTMenuComponent
        implements AccessibleAction, AccessibleValue
    {
        /*
         * JDK 1.3 serialVersionUID
         */
        private static final long serialVersionUID = -217847831945965825L;

        /**
         * Get the accessible name of this object.
         *
         * @return the localized name of the object -- can be null if this
         * object does not have a name
         */
        public String getAccessibleName() {
            if (accessibleName != null) {
                return accessibleName;
            } else {
                if (getLabel() == null) {
                    return super.getAccessibleName();
                } else {
                    return getLabel();
                }
            }
        }

        /**
         * Get the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of the
         * object
         */
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.MENU_ITEM;
        }

        /**
         * Get the AccessibleAction associated with this object.  In the
         * implementation of the Java Accessibility API for this class,
         * return this object, which is responsible for implementing the
         * AccessibleAction interface on behalf of itself.
         *
         * @return this object
         */
        public AccessibleAction getAccessibleAction() {
            return this;
        }

        /**
         * Get the AccessibleValue associated with this object.  In the
         * implementation of the Java Accessibility API for this class,
         * return this object, which is responsible for implementing the
         * AccessibleValue interface on behalf of itself.
         *
         * @return this object
         */
        public AccessibleValue getAccessibleValue() {
            return this;
        }

        /**
         * Returns the number of Actions available in this object.  The
         * default behavior of a menu item is to have one action.
         *
         * @return 1, the number of Actions in this object
         */
        public int getAccessibleActionCount() {
            return 1;
        }

        /**
         * Return a description of the specified action of the object.
         *
         * @param i zero-based index of the actions
         */
        public String getAccessibleActionDescription(int i) {
            if (i == 0) {
                // [[[PENDING:  WDW -- need to provide a localized string]]]
                return "click";
            } else {
                return null;
            }
        }

        /**
         * Perform the specified Action on the object
         *
         * @param i zero-based index of actions
         * @return true if the action was performed; otherwise false.
         */
        public boolean doAccessibleAction(int i) {
            if (i == 0) {
                // Simulate a button click
                Toolkit.getEventQueue().postEvent(
                        new ActionEvent(java.awt.MenuItem.this,
                                        ActionEvent.ACTION_PERFORMED,
                                        java.awt.MenuItem.this.getActionCommand(),
                                        EventQueue.getMostRecentEventTime(),
                                        0));
                return true;
            } else {
                return false;
            }
        }

        /**
         * Get the value of this object as a Number.
         *
         * @return An Integer of 0 if this isn't selected or an Integer of 1 if
         * this is selected.
         * @see javax.swing.AbstractButton#isSelected()
         */
        public Number getCurrentAccessibleValue() {
            return Integer.valueOf(0);
        }

        /**
         * Set the value of this object as a Number.
         *
         * @return True if the value was set.
         */
        public boolean setCurrentAccessibleValue(Number n) {
            return false;
        }

        /**
         * Get the minimum value of this object as a Number.
         *
         * @return An Integer of 0.
         */
        public Number getMinimumAccessibleValue() {
            return Integer.valueOf(0);
        }

        /**
         * Get the maximum value of this object as a Number.
         *
         * @return An Integer of 0.
         */
        public Number getMaximumAccessibleValue() {
            return Integer.valueOf(0);
        }

    } // class AccessibleAWTMenuItem

}
