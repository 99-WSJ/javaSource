/*
 * Copyright (c) 1997, 2004, Oracle and/or its affiliates. All rights reserved.
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

package java8.javax.accessibility;

import javax.accessibility.Accessible;
import javax.accessibility.AccessibleBundle;
import javax.accessibility.AccessibleComponent;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleSelection;
import java.util.Vector;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * <P>Class AccessibleState describes a component's particular state.  The actual
 * state of the component is defined as an AccessibleStateSet, which is a
 * composed set of AccessibleStates.
 * <p>The toDisplayString method allows you to obtain the localized string
 * for a locale independent key from a predefined ResourceBundle for the
 * keys defined in this class.
 * <p>The constants in this class present a strongly typed enumeration
 * of common object roles.  A public constructor for this class has been
 * purposely omitted and applications should use one of the constants
 * from this class.  If the constants in this class are not sufficient
 * to describe the role of an object, a subclass should be generated
 * from this class and it should provide constants in a similar manner.
 *
 * @author      Willie Walker
 * @author      Peter Korn
 */
public class AccessibleState extends AccessibleBundle {

    // If you add or remove anything from here, make sure you
    // update AccessibleResourceBundle.java.

    /**
     * Indicates a window is currently the active window.  This includes
     * windows, dialogs, frames, etc.  In addition, this state is used
     * to indicate the currently active child of a component such as a
     * list, table, or tree.  For example, the active child of a list
     * is the child that is drawn with a rectangle around it.
     * @see AccessibleRole#WINDOW
     * @see AccessibleRole#FRAME
     * @see AccessibleRole#DIALOG
     */
    public static final javax.accessibility.AccessibleState ACTIVE
            = new javax.accessibility.AccessibleState("active");

    /**
     * Indicates this object is currently pressed.  This is usually
     * associated with buttons and indicates the user has pressed a
     * mouse button while the pointer was over the button and has
     * not yet released the mouse button.
     * @see AccessibleRole#PUSH_BUTTON
     */
    public static final javax.accessibility.AccessibleState PRESSED
            = new javax.accessibility.AccessibleState("pressed");

    /**
     * Indicates that the object is armed.  This is usually used on buttons
     * that have been pressed but not yet released, and the mouse pointer
     * is still over the button.
     * @see AccessibleRole#PUSH_BUTTON
     */
    public static final javax.accessibility.AccessibleState ARMED
            = new javax.accessibility.AccessibleState("armed");

    /**
     * Indicates the current object is busy.  This is usually used on objects
     * such as progress bars, sliders, or scroll bars to indicate they are
     * in a state of transition.
     * @see AccessibleRole#PROGRESS_BAR
     * @see AccessibleRole#SCROLL_BAR
     * @see AccessibleRole#SLIDER
     */
    public static final javax.accessibility.AccessibleState BUSY
            = new javax.accessibility.AccessibleState("busy");

    /**
     * Indicates this object is currently checked.  This is usually used on
     * objects such as toggle buttons, radio buttons, and check boxes.
     * @see AccessibleRole#TOGGLE_BUTTON
     * @see AccessibleRole#RADIO_BUTTON
     * @see AccessibleRole#CHECK_BOX
     */
    public static final javax.accessibility.AccessibleState CHECKED
            = new javax.accessibility.AccessibleState("checked");

    /**
     * Indicates the user can change the contents of this object.  This
     * is usually used primarily for objects that allow the user to
     * enter text.  Other objects, such as scroll bars and sliders,
     * are automatically editable if they are enabled.
     * @see #ENABLED
     */
    public static final javax.accessibility.AccessibleState EDITABLE
            = new javax.accessibility.AccessibleState("editable");

    /**
     * Indicates this object allows progressive disclosure of its children.
     * This is usually used with hierarchical objects such as trees and
     * is often paired with the EXPANDED or COLLAPSED states.
     * @see #EXPANDED
     * @see #COLLAPSED
     * @see AccessibleRole#TREE
     */
    public static final javax.accessibility.AccessibleState EXPANDABLE
            = new javax.accessibility.AccessibleState("expandable");

    /**
     * Indicates this object is collapsed.  This is usually paired with the
     * EXPANDABLE state and is used on objects that provide progressive
     * disclosure such as trees.
     * @see #EXPANDABLE
     * @see #EXPANDED
     * @see AccessibleRole#TREE
     */
    public static final javax.accessibility.AccessibleState COLLAPSED
            = new javax.accessibility.AccessibleState("collapsed");

    /**
     * Indicates this object is expanded.  This is usually paired with the
     * EXPANDABLE state and is used on objects that provide progressive
     * disclosure such as trees.
     * @see #EXPANDABLE
     * @see #COLLAPSED
     * @see AccessibleRole#TREE
     */
    public static final javax.accessibility.AccessibleState EXPANDED
            = new javax.accessibility.AccessibleState("expanded");

    /**
     * Indicates this object is enabled.  The absence of this state from an
     * object's state set indicates this object is not enabled.  An object
     * that is not enabled cannot be manipulated by the user.  In a graphical
     * display, it is usually grayed out.
     */
    public static final javax.accessibility.AccessibleState ENABLED
            = new javax.accessibility.AccessibleState("enabled");

    /**
     * Indicates this object can accept keyboard focus, which means all
     * events resulting from typing on the keyboard will normally be
     * passed to it when it has focus.
     * @see #FOCUSED
     */
    public static final javax.accessibility.AccessibleState FOCUSABLE
            = new javax.accessibility.AccessibleState("focusable");

    /**
     * Indicates this object currently has the keyboard focus.
     * @see #FOCUSABLE
     */
    public static final javax.accessibility.AccessibleState FOCUSED
            = new javax.accessibility.AccessibleState("focused");

    /**
     * Indicates this object is minimized and is represented only by an
     * icon.  This is usually only associated with frames and internal
     * frames.
     * @see AccessibleRole#FRAME
     * @see AccessibleRole#INTERNAL_FRAME
     */
    public static final javax.accessibility.AccessibleState ICONIFIED
            = new javax.accessibility.AccessibleState("iconified");

    /**
     * Indicates something must be done with this object before the
     * user can interact with an object in a different window.  This
     * is usually associated only with dialogs.
     * @see AccessibleRole#DIALOG
     */
    public static final javax.accessibility.AccessibleState MODAL
            = new javax.accessibility.AccessibleState("modal");

    /**
     * Indicates this object paints every pixel within its
     * rectangular region. A non-opaque component paints only some of
     * its pixels, allowing the pixels underneath it to "show through".
     * A component that does not fully paint its pixels therefore
     * provides a degree of transparency.
     * @see Accessible#getAccessibleContext
     * @see AccessibleContext#getAccessibleComponent
     * @see AccessibleComponent#getBounds
     */
    public static final javax.accessibility.AccessibleState OPAQUE
            = new javax.accessibility.AccessibleState("opaque");

    /**
     * Indicates the size of this object is not fixed.
     * @see Accessible#getAccessibleContext
     * @see AccessibleContext#getAccessibleComponent
     * @see AccessibleComponent#getSize
     * @see AccessibleComponent#setSize
     */
    public static final javax.accessibility.AccessibleState RESIZABLE
            = new javax.accessibility.AccessibleState("resizable");


    /**
     * Indicates this object allows more than one of its children to
     * be selected at the same time.
     * @see Accessible#getAccessibleContext
     * @see AccessibleContext#getAccessibleSelection
     * @see AccessibleSelection
     */
    public static final javax.accessibility.AccessibleState MULTISELECTABLE
            = new javax.accessibility.AccessibleState("multiselectable");

    /**
     * Indicates this object is the child of an object that allows its
     * children to be selected, and that this child is one of those
     * children that can be selected.
     * @see #SELECTED
     * @see Accessible#getAccessibleContext
     * @see AccessibleContext#getAccessibleSelection
     * @see AccessibleSelection
     */
    public static final javax.accessibility.AccessibleState SELECTABLE
            = new javax.accessibility.AccessibleState("selectable");

    /**
     * Indicates this object is the child of an object that allows its
     * children to be selected, and that this child is one of those
     * children that has been selected.
     * @see #SELECTABLE
     * @see Accessible#getAccessibleContext
     * @see AccessibleContext#getAccessibleSelection
     * @see AccessibleSelection
     */
    public static final javax.accessibility.AccessibleState SELECTED
            = new javax.accessibility.AccessibleState("selected");

    /**
     * Indicates this object, the object's parent, the object's parent's
     * parent, and so on, are all visible.  Note that this does not
     * necessarily mean the object is painted on the screen.  It might
     * be occluded by some other showing object.
     * @see #VISIBLE
     */
    public static final javax.accessibility.AccessibleState SHOWING
            = new javax.accessibility.AccessibleState("showing");

    /**
     * Indicates this object is visible.  Note: this means that the
     * object intends to be visible; however, it may not in fact be
     * showing on the screen because one of the objects that this object
     * is contained by is not visible.
     * @see #SHOWING
     */
    public static final javax.accessibility.AccessibleState VISIBLE
            = new javax.accessibility.AccessibleState("visible");

    /**
     * Indicates the orientation of this object is vertical.  This is
     * usually associated with objects such as scrollbars, sliders, and
     * progress bars.
     * @see #VERTICAL
     * @see AccessibleRole#SCROLL_BAR
     * @see AccessibleRole#SLIDER
     * @see AccessibleRole#PROGRESS_BAR
     */
    public static final javax.accessibility.AccessibleState VERTICAL
            = new javax.accessibility.AccessibleState("vertical");

    /**
     * Indicates the orientation of this object is horizontal.  This is
     * usually associated with objects such as scrollbars, sliders, and
     * progress bars.
     * @see #HORIZONTAL
     * @see AccessibleRole#SCROLL_BAR
     * @see AccessibleRole#SLIDER
     * @see AccessibleRole#PROGRESS_BAR
     */
    public static final javax.accessibility.AccessibleState HORIZONTAL
            = new javax.accessibility.AccessibleState("horizontal");

    /**
     * Indicates this (text) object can contain only a single line of text
     */
    public static final javax.accessibility.AccessibleState SINGLE_LINE
            = new javax.accessibility.AccessibleState("singleline");

    /**
     * Indicates this (text) object can contain multiple lines of text
     */
    public static final javax.accessibility.AccessibleState MULTI_LINE
            = new javax.accessibility.AccessibleState("multiline");

    /**
     * Indicates this object is transient.  An assistive technology should
     * not add a PropertyChange listener to an object with transient state,
     * as that object will never generate any events.  Transient objects
     * are typically created to answer Java Accessibility method queries,
     * but otherwise do not remain linked to the underlying object (for
     * example, those objects underneath lists, tables, and trees in Swing,
     * where only one actual UI Component does shared rendering duty for
     * all of the data objects underneath the actual list/table/tree elements).
     *
     * @since 1.5
     *
     */
    public static final javax.accessibility.AccessibleState TRANSIENT
            = new javax.accessibility.AccessibleState("transient");

    /**
     * Indicates this object is responsible for managing its
     * subcomponents.  This is typically used for trees and tables
     * that have a large number of subcomponents and where the
     * objects are created only when needed and otherwise remain virtual.
     * The application should not manage the subcomponents directly.
     *
     * @since 1.5
     */
    public static final javax.accessibility.AccessibleState MANAGES_DESCENDANTS
            = new javax.accessibility.AccessibleState("managesDescendants");

    /**
     * Indicates that the object state is indeterminate.  An example
     * is selected text that is partially bold and partially not
     * bold. In this case the attributes associated with the selected
     * text are indeterminate.
     *
     * @since 1.5
     */
    public static final javax.accessibility.AccessibleState INDETERMINATE
           = new javax.accessibility.AccessibleState("indeterminate");

    /**
     * A state indicating that text is truncated by a bounding rectangle
     * and that some of the text is not displayed on the screen.  An example
     * is text in a spreadsheet cell that is truncated by the bounds of
     * the cell.
     *
     * @since 1.5
     */
    static public final javax.accessibility.AccessibleState TRUNCATED
           =  new javax.accessibility.AccessibleState("truncated");

    /**
     * Creates a new AccessibleState using the given locale independent key.
     * This should not be a public method.  Instead, it is used to create
     * the constants in this file to make it a strongly typed enumeration.
     * Subclasses of this class should enforce similar policy.
     * <p>
     * The key String should be a locale independent key for the state.
     * It is not intended to be used as the actual String to display
     * to the user.  To get the localized string, use toDisplayString.
     *
     * @param key the locale independent name of the state.
     * @see AccessibleBundle#toDisplayString
     */
    protected AccessibleState(String key) {
        this.key = key;
    }
}
