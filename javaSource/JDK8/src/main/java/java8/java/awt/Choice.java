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

import javax.accessibility.Accessible;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import java.awt.AWTEvent;
import java.awt.AWTEventMulticaster;
import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.ItemSelectable;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.peer.ChoicePeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EventListener;
import java.util.Vector;


/**
 * The <code>Choice</code> class presents a pop-up menu of choices.
 * The current choice is displayed as the title of the menu.
 * <p>
 * The following code example produces a pop-up menu:
 *
 * <hr><blockquote><pre>
 * Choice ColorChooser = new Choice();
 * ColorChooser.add("Green");
 * ColorChooser.add("Red");
 * ColorChooser.add("Blue");
 * </pre></blockquote><hr>
 * <p>
 * After this choice menu has been added to a panel,
 * it appears as follows in its normal state:
 * <p>
 * <img src="doc-files/Choice-1.gif" alt="The following text describes the graphic"
 * style="float:center; margin: 7px 10px;">
 * <p>
 * In the picture, <code>"Green"</code> is the current choice.
 * Pushing the mouse button down on the object causes a menu to
 * appear with the current choice highlighted.
 * <p>
 * Some native platforms do not support arbitrary resizing of
 * <code>Choice</code> components and the behavior of
 * <code>setSize()/getSize()</code> is bound by
 * such limitations.
 * Native GUI <code>Choice</code> components' size are often bound by such
 * attributes as font size and length of items contained within
 * the <code>Choice</code>.
 * <p>
 * @author      Sami Shaio
 * @author      Arthur van Hoff
 * @since       JDK1.0
 */
public class Choice extends Component implements ItemSelectable, Accessible {
    /**
     * The items for the <code>Choice</code>.
     * This can be a <code>null</code> value.
     * @serial
     * @see #add(String)
     * @see #addItem(String)
     * @see #getItem(int)
     * @see #getItemCount()
     * @see #insert(String, int)
     * @see #remove(String)
     */
    Vector<String> pItems;

    /**
     * The index of the current choice for this <code>Choice</code>
     * or -1 if nothing is selected.
     * @serial
     * @see #getSelectedItem()
     * @see #select(int)
     */
    int selectedIndex = -1;

    transient ItemListener itemListener;

    private static final String base = "choice";
    private static int nameCounter = 0;

    /*
     * JDK 1.1 serialVersionUID
     */
    private static final long serialVersionUID = -4075310674757313071L;

    static {
        /* ensure that the necessary native libraries are loaded */
        Toolkit.loadLibraries();
        /* initialize JNI field and method ids */
        if (!GraphicsEnvironment.isHeadless()) {
            initIDs();
        }
    }

    /**
     * Creates a new choice menu. The menu initially has no items in it.
     * <p>
     * By default, the first item added to the choice menu becomes the
     * selected item, until a different selection is made by the user
     * by calling one of the <code>select</code> methods.
     * @exception HeadlessException if GraphicsEnvironment.isHeadless()
     * returns true
     * @see       GraphicsEnvironment#isHeadless
     * @see       #select(int)
     * @see       #select(String)
     */
    public Choice() throws HeadlessException {
        GraphicsEnvironment.checkHeadless();
        pItems = new Vector<>();
    }

    /**
     * Constructs a name for this component.  Called by
     * <code>getName</code> when the name is <code>null</code>.
     */
    String constructComponentName() {
        synchronized (java.awt.Choice.class) {
            return base + nameCounter++;
        }
    }

    /**
     * Creates the <code>Choice</code>'s peer.  This peer allows us
     * to change the look
     * of the <code>Choice</code> without changing its functionality.
     * @see     Toolkit#createChoice(java.awt.Choice)
     * @see     Component#getToolkit()
     */
    public void addNotify() {
        synchronized (getTreeLock()) {
            if (peer == null)
                peer = getToolkit().createChoice(this);
            super.addNotify();
        }
    }

    /**
     * Returns the number of items in this <code>Choice</code> menu.
     * @return the number of items in this <code>Choice</code> menu
     * @see     #getItem
     * @since   JDK1.1
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
        return pItems.size();
    }

    /**
     * Gets the string at the specified index in this
     * <code>Choice</code> menu.
     * @param      index the index at which to begin
     * @see        #getItemCount
     */
    public String getItem(int index) {
        return getItemImpl(index);
    }

    /*
     * This is called by the native code, so client code can't
     * be called on the toolkit thread.
     */
    final String getItemImpl(int index) {
        return pItems.elementAt(index);
    }

    /**
     * Adds an item to this <code>Choice</code> menu.
     * @param      item    the item to be added
     * @exception  NullPointerException   if the item's value is
     *                  <code>null</code>
     * @since      JDK1.1
     */
    public void add(String item) {
        addItem(item);
    }

    /**
     * Obsolete as of Java 2 platform v1.1.  Please use the
     * <code>add</code> method instead.
     * <p>
     * Adds an item to this <code>Choice</code> menu.
     * @param item the item to be added
     * @exception NullPointerException if the item's value is equal to
     *          <code>null</code>
     */
    public void addItem(String item) {
        synchronized (this) {
            insertNoInvalidate(item, pItems.size());
        }

        // This could change the preferred size of the Component.
        invalidateIfValid();
    }

    /**
     * Inserts an item to this <code>Choice</code>,
     * but does not invalidate the <code>Choice</code>.
     * Client methods must provide their own synchronization before
     * invoking this method.
     * @param item the item to be added
     * @param index the new item position
     * @exception NullPointerException if the item's value is equal to
     *          <code>null</code>
     */
    private void insertNoInvalidate(String item, int index) {
        if (item == null) {
            throw new
                NullPointerException("cannot add null item to Choice");
        }
        pItems.insertElementAt(item, index);
        ChoicePeer peer = (ChoicePeer)this.peer;
        if (peer != null) {
            peer.add(item, index);
        }
        // no selection or selection shifted up
        if (selectedIndex < 0 || selectedIndex >= index) {
            select(0);
        }
    }


    /**
     * Inserts the item into this choice at the specified position.
     * Existing items at an index greater than or equal to
     * <code>index</code> are shifted up by one to accommodate
     * the new item.  If <code>index</code> is greater than or
     * equal to the number of items in this choice,
     * <code>item</code> is added to the end of this choice.
     * <p>
     * If the item is the first one being added to the choice,
     * then the item becomes selected.  Otherwise, if the
     * selected item was one of the items shifted, the first
     * item in the choice becomes the selected item.  If the
     * selected item was no among those shifted, it remains
     * the selected item.
     * @param item the non-<code>null</code> item to be inserted
     * @param index the position at which the item should be inserted
     * @exception IllegalArgumentException if index is less than 0
     */
    public void insert(String item, int index) {
        synchronized (this) {
            if (index < 0) {
                throw new IllegalArgumentException("index less than zero.");
            }
            /* if the index greater than item count, add item to the end */
            index = Math.min(index, pItems.size());

            insertNoInvalidate(item, index);
        }

        // This could change the preferred size of the Component.
        invalidateIfValid();
    }

    /**
     * Removes the first occurrence of <code>item</code>
     * from the <code>Choice</code> menu.  If the item
     * being removed is the currently selected item,
     * then the first item in the choice becomes the
     * selected item.  Otherwise, the currently selected
     * item remains selected (and the selected index is
     * updated accordingly).
     * @param      item  the item to remove from this <code>Choice</code> menu
     * @exception  IllegalArgumentException  if the item doesn't
     *                     exist in the choice menu
     * @since      JDK1.1
     */
    public void remove(String item) {
        synchronized (this) {
            int index = pItems.indexOf(item);
            if (index < 0) {
                throw new IllegalArgumentException("item " + item +
                                                   " not found in choice");
            } else {
                removeNoInvalidate(index);
            }
        }

        // This could change the preferred size of the Component.
        invalidateIfValid();
    }

    /**
     * Removes an item from the choice menu
     * at the specified position.  If the item
     * being removed is the currently selected item,
     * then the first item in the choice becomes the
     * selected item.  Otherwise, the currently selected
     * item remains selected (and the selected index is
     * updated accordingly).
     * @param      position the position of the item
     * @throws IndexOutOfBoundsException if the specified
     *          position is out of bounds
     * @since      JDK1.1
     */
    public void remove(int position) {
        synchronized (this) {
            removeNoInvalidate(position);
        }

        // This could change the preferred size of the Component.
        invalidateIfValid();
    }

    /**
     * Removes an item from the <code>Choice</code> at the
     * specified position, but does not invalidate the <code>Choice</code>.
     * Client methods must provide their
     * own synchronization before invoking this method.
     * @param      position   the position of the item
     */
    private void removeNoInvalidate(int position) {
        pItems.removeElementAt(position);
        ChoicePeer peer = (ChoicePeer)this.peer;
        if (peer != null) {
            peer.remove(position);
        }
        /* Adjust selectedIndex if selected item was removed. */
        if (pItems.size() == 0) {
            selectedIndex = -1;
        } else if (selectedIndex == position) {
            select(0);
        } else if (selectedIndex > position) {
            select(selectedIndex-1);
        }
    }


    /**
     * Removes all items from the choice menu.
     * @see       #remove
     * @since     JDK1.1
     */
    public void removeAll() {
        synchronized (this) {
            if (peer != null) {
                ((ChoicePeer)peer).removeAll();
            }
            pItems.removeAllElements();
            selectedIndex = -1;
        }

        // This could change the preferred size of the Component.
        invalidateIfValid();
    }

    /**
     * Gets a representation of the current choice as a string.
     * @return    a string representation of the currently
     *                     selected item in this choice menu
     * @see       #getSelectedIndex
     */
    public synchronized String getSelectedItem() {
        return (selectedIndex >= 0) ? getItem(selectedIndex) : null;
    }

    /**
     * Returns an array (length 1) containing the currently selected
     * item.  If this choice has no items, returns <code>null</code>.
     * @see ItemSelectable
     */
    public synchronized Object[] getSelectedObjects() {
        if (selectedIndex >= 0) {
            Object[] items = new Object[1];
            items[0] = getItem(selectedIndex);
            return items;
        }
        return null;
    }

    /**
     * Returns the index of the currently selected item.
     * If nothing is selected, returns -1.
     *
     * @return the index of the currently selected item, or -1 if nothing
     *  is currently selected
     * @see #getSelectedItem
     */
    public int getSelectedIndex() {
        return selectedIndex;
    }

    /**
     * Sets the selected item in this <code>Choice</code> menu to be the
     * item at the specified position.
     *
     * <p>Note that this method should be primarily used to
     * initially select an item in this component.
     * Programmatically calling this method will <i>not</i> trigger
     * an <code>ItemEvent</code>.  The only way to trigger an
     * <code>ItemEvent</code> is by user interaction.
     *
     * @param      pos      the position of the selected item
     * @exception  IllegalArgumentException if the specified
     *                            position is greater than the
     *                            number of items or less than zero
     * @see        #getSelectedItem
     * @see        #getSelectedIndex
     */
    public synchronized void select(int pos) {
        if ((pos >= pItems.size()) || (pos < 0)) {
            throw new IllegalArgumentException("illegal Choice item position: " + pos);
        }
        if (pItems.size() > 0) {
            selectedIndex = pos;
            ChoicePeer peer = (ChoicePeer)this.peer;
            if (peer != null) {
                peer.select(pos);
            }
        }
    }

    /**
     * Sets the selected item in this <code>Choice</code> menu
     * to be the item whose name is equal to the specified string.
     * If more than one item matches (is equal to) the specified string,
     * the one with the smallest index is selected.
     *
     * <p>Note that this method should be primarily used to
     * initially select an item in this component.
     * Programmatically calling this method will <i>not</i> trigger
     * an <code>ItemEvent</code>.  The only way to trigger an
     * <code>ItemEvent</code> is by user interaction.
     *
     * @param       str     the specified string
     * @see         #getSelectedItem
     * @see         #getSelectedIndex
     */
    public synchronized void select(String str) {
        int index = pItems.indexOf(str);
        if (index >= 0) {
            select(index);
        }
    }

    /**
     * Adds the specified item listener to receive item events from
     * this <code>Choice</code> menu.  Item events are sent in response
     * to user input, but not in response to calls to <code>select</code>.
     * If l is <code>null</code>, no exception is thrown and no action
     * is performed.
     * <p>Refer to <a href="doc-files/AWTThreadIssues.html#ListenersThreads"
     * >AWT Threading Issues</a> for details on AWT's threading model.
     * @param         l    the item listener
     * @see           #removeItemListener
     * @see           #getItemListeners
     * @see           #select
     * @see           ItemEvent
     * @see           ItemListener
     * @since         JDK1.1
     */
    public synchronized void addItemListener(ItemListener l) {
        if (l == null) {
           return;
        }
        itemListener = AWTEventMulticaster.add(itemListener, l);
        newEventsOnly = true;
    }

    /**
     * Removes the specified item listener so that it no longer receives
     * item events from this <code>Choice</code> menu.
     * If l is <code>null</code>, no exception is thrown and no
     * action is performed.
     * <p>Refer to <a href="doc-files/AWTThreadIssues.html#ListenersThreads"
     * >AWT Threading Issues</a> for details on AWT's threading model.
     * @param         l    the item listener
     * @see           #addItemListener
     * @see           #getItemListeners
     * @see           ItemEvent
     * @see           ItemListener
     * @since         JDK1.1
     */
    public synchronized void removeItemListener(ItemListener l) {
        if (l == null) {
            return;
        }
        itemListener = AWTEventMulticaster.remove(itemListener, l);
    }

    /**
     * Returns an array of all the item listeners
     * registered on this choice.
     *
     * @return all of this choice's <code>ItemListener</code>s
     *         or an empty array if no item
     *         listeners are currently registered
     *
     * @see           #addItemListener
     * @see           #removeItemListener
     * @see           ItemEvent
     * @see           ItemListener
     * @since 1.4
     */
    public synchronized ItemListener[] getItemListeners() {
        return getListeners(ItemListener.class);
    }

    /**
     * Returns an array of all the objects currently registered
     * as <code><em>Foo</em>Listener</code>s
     * upon this <code>Choice</code>.
     * <code><em>Foo</em>Listener</code>s are registered using the
     * <code>add<em>Foo</em>Listener</code> method.
     *
     * <p>
     * You can specify the <code>listenerType</code> argument
     * with a class literal, such as
     * <code><em>Foo</em>Listener.class</code>.
     * For example, you can query a
     * <code>Choice</code> <code>c</code>
     * for its item listeners with the following code:
     *
     * <pre>ItemListener[] ils = (ItemListener[])(c.getListeners(ItemListener.class));</pre>
     *
     * If no such listeners exist, this method returns an empty array.
     *
     * @param listenerType the type of listeners requested; this parameter
     *          should specify an interface that descends from
     *          <code>java.util.EventListener</code>
     * @return an array of all objects registered as
     *          <code><em>Foo</em>Listener</code>s on this choice,
     *          or an empty array if no such
     *          listeners have been added
     * @exception ClassCastException if <code>listenerType</code>
     *          doesn't specify a class or interface that implements
     *          <code>java.util.EventListener</code>
     *
     * @see #getItemListeners
     * @since 1.3
     */
    public <T extends EventListener> T[] getListeners(Class<T> listenerType) {
        EventListener l = null;
        if  (listenerType == ItemListener.class) {
            l = itemListener;
        } else {
            return super.getListeners(listenerType);
        }
        return AWTEventMulticaster.getListeners(l, listenerType);
    }

    // REMIND: remove when filtering is done at lower level
    boolean eventEnabled(AWTEvent e) {
        if (e.id == ItemEvent.ITEM_STATE_CHANGED) {
            if ((eventMask & AWTEvent.ITEM_EVENT_MASK) != 0 ||
                itemListener != null) {
                return true;
            }
            return false;
        }
        return super.eventEnabled(e);
    }

    /**
     * Processes events on this choice. If the event is an
     * instance of <code>ItemEvent</code>, it invokes the
     * <code>processItemEvent</code> method. Otherwise, it calls its
     * superclass's <code>processEvent</code> method.
     * <p>Note that if the event parameter is <code>null</code>
     * the behavior is unspecified and may result in an
     * exception.
     *
     * @param      e the event
     * @see        ItemEvent
     * @see        #processItemEvent
     * @since      JDK1.1
     */
    protected void processEvent(AWTEvent e) {
        if (e instanceof ItemEvent) {
            processItemEvent((ItemEvent)e);
            return;
        }
        super.processEvent(e);
    }

    /**
     * Processes item events occurring on this <code>Choice</code>
     * menu by dispatching them to any registered
     * <code>ItemListener</code> objects.
     * <p>
     * This method is not called unless item events are
     * enabled for this component. Item events are enabled
     * when one of the following occurs:
     * <ul>
     * <li>An <code>ItemListener</code> object is registered
     * via <code>addItemListener</code>.
     * <li>Item events are enabled via <code>enableEvents</code>.
     * </ul>
     * <p>Note that if the event parameter is <code>null</code>
     * the behavior is unspecified and may result in an
     * exception.
     *
     * @param       e the item event
     * @see         ItemEvent
     * @see         ItemListener
     * @see         #addItemListener(ItemListener)
     * @see         Component#enableEvents
     * @since       JDK1.1
     */
    protected void processItemEvent(ItemEvent e) {
        ItemListener listener = itemListener;
        if (listener != null) {
            listener.itemStateChanged(e);
        }
    }

    /**
     * Returns a string representing the state of this <code>Choice</code>
     * menu. This method is intended to be used only for debugging purposes,
     * and the content and format of the returned string may vary between
     * implementations. The returned string may be empty but may not be
     * <code>null</code>.
     *
     * @return    the parameter string of this <code>Choice</code> menu
     */
    protected String paramString() {
        return super.paramString() + ",current=" + getSelectedItem();
    }


    /* Serialization support.
     */

    /*
     * Choice Serial Data Version.
     * @serial
     */
    private int choiceSerializedDataVersion = 1;

    /**
     * Writes default serializable fields to stream.  Writes
     * a list of serializable <code>ItemListeners</code>
     * as optional data. The non-serializable
     * <code>ItemListeners</code> are detected and
     * no attempt is made to serialize them.
     *
     * @param s the <code>ObjectOutputStream</code> to write
     * @serialData <code>null</code> terminated sequence of 0
     *   or more pairs; the pair consists of a <code>String</code>
     *   and an <code>Object</code>; the <code>String</code> indicates
     *   the type of object and is one of the following:
     *   <code>itemListenerK</code> indicating an
     *     <code>ItemListener</code> object
     *
     * @see AWTEventMulticaster#save(ObjectOutputStream, String, EventListener)
     * @see Component#itemListenerK
     * @see #readObject(ObjectInputStream)
     */
    private void writeObject(ObjectOutputStream s)
      throws IOException
    {
      s.defaultWriteObject();

      AWTEventMulticaster.save(s, itemListenerK, itemListener);
      s.writeObject(null);
    }

    /**
     * Reads the <code>ObjectInputStream</code> and if it
     * isn't <code>null</code> adds a listener to receive
     * item events fired by the <code>Choice</code> item.
     * Unrecognized keys or values will be ignored.
     *
     * @param s the <code>ObjectInputStream</code> to read
     * @exception HeadlessException if
     *   <code>GraphicsEnvironment.isHeadless</code> returns
     *   <code>true</code>
     * @serial
     * @see #removeItemListener(ItemListener)
     * @see #addItemListener(ItemListener)
     * @see GraphicsEnvironment#isHeadless
     * @see #writeObject(ObjectOutputStream)
     */
    private void readObject(ObjectInputStream s)
      throws ClassNotFoundException, IOException, HeadlessException
    {
      GraphicsEnvironment.checkHeadless();
      s.defaultReadObject();

      Object keyOrNull;
      while(null != (keyOrNull = s.readObject())) {
        String key = ((String)keyOrNull).intern();

        if (itemListenerK == key)
          addItemListener((ItemListener)(s.readObject()));

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
     * Gets the <code>AccessibleContext</code> associated with this
     * <code>Choice</code>. For <code>Choice</code> components,
     * the <code>AccessibleContext</code> takes the form of an
     * <code>AccessibleAWTChoice</code>. A new <code>AccessibleAWTChoice</code>
     * instance is created if necessary.
     *
     * @return an <code>AccessibleAWTChoice</code> that serves as the
     *         <code>AccessibleContext</code> of this <code>Choice</code>
     * @since 1.3
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleAWTChoice();
        }
        return accessibleContext;
    }

    /**
     * This class implements accessibility support for the
     * <code>Choice</code> class.  It provides an implementation of the
     * Java Accessibility API appropriate to choice user-interface elements.
     * @since 1.3
     */
    protected class AccessibleAWTChoice extends AccessibleAWTComponent
        implements AccessibleAction
    {
        /*
         * JDK 1.3 serialVersionUID
         */
        private static final long serialVersionUID = 7175603582428509322L;

        public AccessibleAWTChoice() {
            super();
        }

        /**
         * Get the AccessibleAction associated with this object.  In the
         * implementation of the Java Accessibility API for this class,
         * return this object, which is responsible for implementing the
         * AccessibleAction interface on behalf of itself.
         *
         * @return this object
         * @see AccessibleAction
         */
        public AccessibleAction getAccessibleAction() {
            return this;
        }

        /**
         * Get the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of the
         * object
         * @see AccessibleRole
         */
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.COMBO_BOX;
        }

        /**
         * Returns the number of accessible actions available in this object
         * If there are more than one, the first one is considered the "default"
         * action of the object.
         *
         * @return the zero-based number of Actions in this object
         */
        public int getAccessibleActionCount() {
            return 0;  //  To be fully implemented in a future release
        }

        /**
         * Returns a description of the specified action of the object.
         *
         * @param i zero-based index of the actions
         * @return a String description of the action
         * @see #getAccessibleActionCount
         */
        public String getAccessibleActionDescription(int i) {
            return null;  //  To be fully implemented in a future release
        }

        /**
         * Perform the specified Action on the object
         *
         * @param i zero-based index of actions
         * @return true if the action was performed; otherwise false.
         * @see #getAccessibleActionCount
         */
        public boolean doAccessibleAction(int i) {
            return false;  //  To be fully implemented in a future release
        }

    } // inner class AccessibleAWTChoice

}
