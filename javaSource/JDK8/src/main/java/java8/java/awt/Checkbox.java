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

import javax.accessibility.*;
import java.awt.AWTEvent;
import java.awt.AWTEventMulticaster;
import java.awt.CheckboxGroup;
import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.ItemSelectable;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.peer.CheckboxPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EventListener;


/**
 * A check box is a graphical component that can be in either an
 * "on" (<code>true</code>) or "off" (<code>false</code>) state.
 * Clicking on a check box changes its state from
 * "on" to "off," or from "off" to "on."
 * <p>
 * The following code example creates a set of check boxes in
 * a grid layout:
 *
 * <hr><blockquote><pre>
 * setLayout(new GridLayout(3, 1));
 * add(new Checkbox("one", null, true));
 * add(new Checkbox("two"));
 * add(new Checkbox("three"));
 * </pre></blockquote><hr>
 * <p>
 * This image depicts the check boxes and grid layout
 * created by this code example:
 * <p>
 * <img src="doc-files/Checkbox-1.gif" alt="The following context describes the graphic."
 * style="float:center; margin: 7px 10px;">
 * <p>
 * The button labeled <code>one</code> is in the "on" state, and the
 * other two are in the "off" state. In this example, which uses the
 * <code>GridLayout</code> class, the states of the three check
 * boxes are set independently.
 * <p>
 * Alternatively, several check boxes can be grouped together under
 * the control of a single object, using the
 * <code>CheckboxGroup</code> class.
 * In a check box group, at most one button can be in the "on"
 * state at any given time. Clicking on a check box to turn it on
 * forces any other check box in the same group that is on
 * into the "off" state.
 *
 * @author      Sami Shaio
 * @see         java.awt.GridLayout
 * @see         CheckboxGroup
 * @since       JDK1.0
 */
public class Checkbox extends Component implements ItemSelectable, Accessible {

    static {
        /* ensure that the necessary native libraries are loaded */
        Toolkit.loadLibraries();
        if (!GraphicsEnvironment.isHeadless()) {
            initIDs();
        }
    }

    /**
     * The label of the Checkbox.
     * This field can be null.
     * @serial
     * @see #getLabel()
     * @see #setLabel(String)
     */
    String label;

    /**
     * The state of the <code>Checkbox</code>.
     * @serial
     * @see #getState()
     * @see #setState(boolean)
     */
    boolean state;

    /**
     * The check box group.
         * This field can be null indicating that the checkbox
         * is not a group checkbox.
         * @serial
     * @see #getCheckboxGroup()
     * @see #setCheckboxGroup(CheckboxGroup)
     */
    CheckboxGroup group;

    transient ItemListener itemListener;

    private static final String base = "checkbox";
    private static int nameCounter = 0;

    /*
     * JDK 1.1 serialVersionUID
     */
    private static final long serialVersionUID = 7270714317450821763L;

    /**
     * Helper function for setState and CheckboxGroup.setSelectedCheckbox
     * Should remain package-private.
     */
    void setStateInternal(boolean state) {
        this.state = state;
        CheckboxPeer peer = (CheckboxPeer)this.peer;
        if (peer != null) {
            peer.setState(state);
        }
    }

    /**
     * Creates a check box with an empty string for its label.
     * The state of this check box is set to "off," and it is not
     * part of any check box group.
     * @exception HeadlessException if GraphicsEnvironment.isHeadless()
     * returns true
     * @see GraphicsEnvironment#isHeadless
     */
    public Checkbox() throws HeadlessException {
        this("", false, null);
    }

    /**
     * Creates a check box with the specified label.  The state
     * of this check box is set to "off," and it is not part of
     * any check box group.
     *
     * @param     label   a string label for this check box,
     *                        or <code>null</code> for no label.
     * @exception HeadlessException if
     *      <code>GraphicsEnvironment.isHeadless</code>
     *      returns <code>true</code>
     * @see GraphicsEnvironment#isHeadless
     */
    public Checkbox(String label) throws HeadlessException {
        this(label, false, null);
    }

    /**
     * Creates a check box with the specified label
     * and sets the specified state.
     * This check box is not part of any check box group.
     *
     * @param     label   a string label for this check box,
     *                        or <code>null</code> for no label
     * @param     state    the initial state of this check box
     * @exception HeadlessException if
     *     <code>GraphicsEnvironment.isHeadless</code>
     *     returns <code>true</code>
     * @see GraphicsEnvironment#isHeadless
     */
    public Checkbox(String label, boolean state) throws HeadlessException {
        this(label, state, null);
    }

    /**
     * Constructs a Checkbox with the specified label, set to the
     * specified state, and in the specified check box group.
     *
     * @param     label   a string label for this check box,
     *                        or <code>null</code> for no label.
     * @param     state   the initial state of this check box.
     * @param     group   a check box group for this check box,
     *                           or <code>null</code> for no group.
     * @exception HeadlessException if
     *     <code>GraphicsEnvironment.isHeadless</code>
     *     returns <code>true</code>
     * @see GraphicsEnvironment#isHeadless
     * @since     JDK1.1
     */
    public Checkbox(String label, boolean state, CheckboxGroup group)
        throws HeadlessException {
        GraphicsEnvironment.checkHeadless();
        this.label = label;
        this.state = state;
        this.group = group;
        if (state && (group != null)) {
            group.setSelectedCheckbox(this);
        }
    }

    /**
     * Creates a check box with the specified label, in the specified
     * check box group, and set to the specified state.
     *
     * @param     label   a string label for this check box,
     *                        or <code>null</code> for no label.
     * @param     group   a check box group for this check box,
     *                           or <code>null</code> for no group.
     * @param     state   the initial state of this check box.
     * @exception HeadlessException if
     *    <code>GraphicsEnvironment.isHeadless</code>
     *    returns <code>true</code>
     * @see GraphicsEnvironment#isHeadless
     * @since     JDK1.1
     */
    public Checkbox(String label, CheckboxGroup group, boolean state)
        throws HeadlessException {
        this(label, state, group);
    }

    /**
     * Constructs a name for this component.  Called by
     * <code>getName</code> when the name is <code>null</code>.
     *
     * @return a name for this component
     */
    String constructComponentName() {
        synchronized (java.awt.Checkbox.class) {
            return base + nameCounter++;
        }
    }

    /**
     * Creates the peer of the Checkbox. The peer allows you to change the
     * look of the Checkbox without changing its functionality.
     *
     * @see     Toolkit#createCheckbox(java.awt.Checkbox)
     * @see     Component#getToolkit()
     */
    public void addNotify() {
        synchronized (getTreeLock()) {
            if (peer == null)
                peer = getToolkit().createCheckbox(this);
            super.addNotify();
        }
    }

    /**
     * Gets the label of this check box.
     *
     * @return   the label of this check box, or <code>null</code>
     *                  if this check box has no label.
     * @see      #setLabel(String)
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets this check box's label to be the string argument.
     *
     * @param    label   a string to set as the new label, or
     *                        <code>null</code> for no label.
     * @see      #getLabel
     */
    public void setLabel(String label) {
        boolean testvalid = false;

        synchronized (this) {
            if (label != this.label && (this.label == null ||
                                        !this.label.equals(label))) {
                this.label = label;
                CheckboxPeer peer = (CheckboxPeer)this.peer;
                if (peer != null) {
                    peer.setLabel(label);
                }
                testvalid = true;
            }
        }

        // This could change the preferred size of the Component.
        if (testvalid) {
            invalidateIfValid();
        }
    }

    /**
     * Determines whether this check box is in the "on" or "off" state.
     * The boolean value <code>true</code> indicates the "on" state,
     * and <code>false</code> indicates the "off" state.
     *
     * @return    the state of this check box, as a boolean value
     * @see       #setState
     */
    public boolean getState() {
        return state;
    }

    /**
     * Sets the state of this check box to the specified state.
     * The boolean value <code>true</code> indicates the "on" state,
     * and <code>false</code> indicates the "off" state.
     *
     * <p>Note that this method should be primarily used to
     * initialize the state of the checkbox.  Programmatically
     * setting the state of the checkbox will <i>not</i> trigger
     * an <code>ItemEvent</code>.  The only way to trigger an
     * <code>ItemEvent</code> is by user interaction.
     *
     * @param     state   the boolean state of the check box
     * @see       #getState
     */
    public void setState(boolean state) {
        /* Cannot hold check box lock when calling group.setSelectedCheckbox. */
        CheckboxGroup group = this.group;
        if (group != null) {
            if (state) {
                group.setSelectedCheckbox(this);
            } else if (group.getSelectedCheckbox() == this) {
                state = true;
            }
        }
        setStateInternal(state);
    }

    /**
     * Returns an array (length 1) containing the checkbox
     * label or null if the checkbox is not selected.
     * @see ItemSelectable
     */
    public Object[] getSelectedObjects() {
        if (state) {
            Object[] items = new Object[1];
            items[0] = label;
            return items;
        }
        return null;
    }

    /**
     * Determines this check box's group.
     * @return     this check box's group, or <code>null</code>
     *               if the check box is not part of a check box group.
     * @see        #setCheckboxGroup(CheckboxGroup)
     */
    public CheckboxGroup getCheckboxGroup() {
        return group;
    }

    /**
     * Sets this check box's group to the specified check box group.
     * If this check box is already in a different check box group,
     * it is first taken out of that group.
     * <p>
     * If the state of this check box is <code>true</code> and the new
     * group already has a check box selected, this check box's state
     * is changed to <code>false</code>.  If the state of this check
     * box is <code>true</code> and the new group has no check box
     * selected, this check box becomes the selected checkbox for
     * the new group and its state is <code>true</code>.
     *
     * @param     g   the new check box group, or <code>null</code>
     *                to remove this check box from any check box group
     * @see       #getCheckboxGroup
     */
    public void setCheckboxGroup(CheckboxGroup g) {
        CheckboxGroup oldGroup;
        boolean oldState;

        /* Do nothing if this check box has already belonged
         * to the check box group g.
         */
        if (this.group == g) {
            return;
        }

        synchronized (this) {
            oldGroup = this.group;
            oldState = getState();

            this.group = g;
            CheckboxPeer peer = (CheckboxPeer)this.peer;
            if (peer != null) {
                peer.setCheckboxGroup(g);
            }
            if (this.group != null && getState()) {
                if (this.group.getSelectedCheckbox() != null) {
                    setState(false);
                } else {
                    this.group.setSelectedCheckbox(this);
                }
            }
        }

        /* Locking check box below could cause deadlock with
         * CheckboxGroup's setSelectedCheckbox method.
         *
         * Fix for 4726853 by kdm@sparc.spb.su
         * Here we should check if this check box was selected
         * in the previous group and set selected check box to
         * null for that group if so.
         */
        if (oldGroup != null && oldState) {
            oldGroup.setSelectedCheckbox(null);
        }
    }

    /**
     * Adds the specified item listener to receive item events from
     * this check box.  Item events are sent to listeners in response
     * to user input, but not in response to calls to setState().
     * If l is null, no exception is thrown and no action is performed.
     * <p>Refer to <a href="doc-files/AWTThreadIssues.html#ListenersThreads"
     * >AWT Threading Issues</a> for details on AWT's threading model.
     *
     * @param         l    the item listener
     * @see           #removeItemListener
     * @see           #getItemListeners
     * @see           #setState
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
     * Removes the specified item listener so that the item listener
     * no longer receives item events from this check box.
     * If l is null, no exception is thrown and no action is performed.
     * <p>Refer to <a href="doc-files/AWTThreadIssues.html#ListenersThreads"
     * >AWT Threading Issues</a> for details on AWT's threading model.
     *
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
     * registered on this checkbox.
     *
     * @return all of this checkbox's <code>ItemListener</code>s
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
     * upon this <code>Checkbox</code>.
     * <code><em>Foo</em>Listener</code>s are registered using the
     * <code>add<em>Foo</em>Listener</code> method.
     *
     * <p>
     * You can specify the <code>listenerType</code> argument
     * with a class literal, such as
     * <code><em>Foo</em>Listener.class</code>.
     * For example, you can query a
     * <code>Checkbox</code> <code>c</code>
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
     *          <code><em>Foo</em>Listener</code>s on this checkbox,
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
     * Processes events on this check box.
     * If the event is an instance of <code>ItemEvent</code>,
     * this method invokes the <code>processItemEvent</code> method.
     * Otherwise, it calls its superclass's <code>processEvent</code> method.
     * <p>Note that if the event parameter is <code>null</code>
     * the behavior is unspecified and may result in an
     * exception.
     *
     * @param         e the event
     * @see           ItemEvent
     * @see           #processItemEvent
     * @since         JDK1.1
     */
    protected void processEvent(AWTEvent e) {
        if (e instanceof ItemEvent) {
            processItemEvent((ItemEvent)e);
            return;
        }
        super.processEvent(e);
    }

    /**
     * Processes item events occurring on this check box by
     * dispatching them to any registered
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
     * @see         #addItemListener
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
     * Returns a string representing the state of this <code>Checkbox</code>.
     * This method is intended to be used only for debugging purposes, and the
     * content and format of the returned string may vary between
     * implementations. The returned string may be empty but may not be
     * <code>null</code>.
     *
     * @return    the parameter string of this check box
     */
    protected String paramString() {
        String str = super.paramString();
        String label = this.label;
        if (label != null) {
            str += ",label=" + label;
        }
        return str + ",state=" + state;
    }


    /* Serialization support.
     */

    /*
     * Serialized data version
     * @serial
     */
    private int checkboxSerializedDataVersion = 1;

    /**
     * Writes default serializable fields to stream.  Writes
     * a list of serializable <code>ItemListeners</code>
     * as optional data.  The non-serializable
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
     * item events fired by the <code>Checkbox</code>.
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
     * Initialize JNI field and method ids
     */
    private static native void initIDs();


/////////////////
// Accessibility support
////////////////


    /**
     * Gets the AccessibleContext associated with this Checkbox.
     * For checkboxes, the AccessibleContext takes the form of an
     * AccessibleAWTCheckbox.
     * A new AccessibleAWTCheckbox is created if necessary.
     *
     * @return an AccessibleAWTCheckbox that serves as the
     *         AccessibleContext of this Checkbox
     * @since 1.3
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleAWTCheckbox();
        }
        return accessibleContext;
    }

    /**
     * This class implements accessibility support for the
     * <code>Checkbox</code> class.  It provides an implementation of the
     * Java Accessibility API appropriate to checkbox user-interface elements.
     * @since 1.3
     */
    protected class AccessibleAWTCheckbox extends AccessibleAWTComponent
        implements ItemListener, AccessibleAction, AccessibleValue
    {
        /*
         * JDK 1.3 serialVersionUID
         */
        private static final long serialVersionUID = 7881579233144754107L;

        public AccessibleAWTCheckbox() {
            super();
            java.awt.Checkbox.this.addItemListener(this);
        }

        /**
         * Fire accessible property change events when the state of the
         * toggle button changes.
         */
        public void itemStateChanged(ItemEvent e) {
            java.awt.Checkbox cb = (java.awt.Checkbox) e.getSource();
            if (java.awt.Checkbox.this.accessibleContext != null) {
                if (cb.getState()) {
                    java.awt.Checkbox.this.accessibleContext.firePropertyChange(
                            AccessibleContext.ACCESSIBLE_STATE_PROPERTY,
                            null, AccessibleState.CHECKED);
                } else {
                    java.awt.Checkbox.this.accessibleContext.firePropertyChange(
                            AccessibleContext.ACCESSIBLE_STATE_PROPERTY,
                            AccessibleState.CHECKED, null);
                }
            }
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
         * Returns the number of Actions available in this object.
         * If there is more than one, the first one is the "default"
         * action.
         *
         * @return the number of Actions in this object
         */
        public int getAccessibleActionCount() {
            return 0;  //  To be fully implemented in a future release
        }

        /**
         * Return a description of the specified action of the object.
         *
         * @param i zero-based index of the actions
         */
        public String getAccessibleActionDescription(int i) {
            return null;  //  To be fully implemented in a future release
        }

        /**
         * Perform the specified Action on the object
         *
         * @param i zero-based index of actions
         * @return true if the the action was performed; else false.
         */
        public boolean doAccessibleAction(int i) {
            return false;    //  To be fully implemented in a future release
        }

        /**
         * Get the value of this object as a Number.  If the value has not been
         * set, the return value will be null.
         *
         * @return value of the object
         * @see #setCurrentAccessibleValue
         */
        public Number getCurrentAccessibleValue() {
            return null;  //  To be fully implemented in a future release
        }

        /**
         * Set the value of this object as a Number.
         *
         * @return True if the value was set; else False
         * @see #getCurrentAccessibleValue
         */
        public boolean setCurrentAccessibleValue(Number n) {
            return false;  //  To be fully implemented in a future release
        }

        /**
         * Get the minimum value of this object as a Number.
         *
         * @return Minimum value of the object; null if this object does not
         * have a minimum value
         * @see #getMaximumAccessibleValue
         */
        public Number getMinimumAccessibleValue() {
            return null;  //  To be fully implemented in a future release
        }

        /**
         * Get the maximum value of this object as a Number.
         *
         * @return Maximum value of the object; null if this object does not
         * have a maximum value
         * @see #getMinimumAccessibleValue
         */
        public Number getMaximumAccessibleValue() {
            return null;  //  To be fully implemented in a future release
        }

        /**
         * Get the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of
         * the object
         * @see AccessibleRole
         */
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.CHECK_BOX;
        }

        /**
         * Get the state set of this object.
         *
         * @return an instance of AccessibleState containing the current state
         * of the object
         * @see AccessibleState
         */
        public AccessibleStateSet getAccessibleStateSet() {
            AccessibleStateSet states = super.getAccessibleStateSet();
            if (getState()) {
                states.add(AccessibleState.CHECKED);
            }
            return states;
        }


    } // inner class AccessibleAWTCheckbox

}
