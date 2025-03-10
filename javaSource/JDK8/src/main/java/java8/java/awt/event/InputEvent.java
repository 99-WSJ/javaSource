/*
 * Copyright (c) 1996, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java8.java.awt.event;

import sun.awt.AWTAccessor;
import sun.security.util.SecurityConstants;
import sun.util.logging.PlatformLogger;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Arrays;

/**
 * The root event class for all component-level input events.
 *
 * Input events are delivered to listeners before they are
 * processed normally by the source where they originated.
 * This allows listeners and component subclasses to "consume"
 * the event so that the source will not process them in their
 * default manner.  For example, consuming mousePressed events
 * on a Button component will prevent the Button from being
 * activated.
 *
 * @author Carl Quinn
 *
 * @see KeyEvent
 * @see KeyAdapter
 * @see MouseEvent
 * @see MouseAdapter
 * @see MouseMotionAdapter
 *
 * @since 1.1
 */
public abstract class InputEvent extends ComponentEvent {

    private static final PlatformLogger logger = PlatformLogger.getLogger("java.awt.event.InputEvent");

    /**
     * The Shift key modifier constant.
     * It is recommended that SHIFT_DOWN_MASK be used instead.
     */
    public static final int SHIFT_MASK = Event.SHIFT_MASK;

    /**
     * The Control key modifier constant.
     * It is recommended that CTRL_DOWN_MASK be used instead.
     */
    public static final int CTRL_MASK = Event.CTRL_MASK;

    /**
     * The Meta key modifier constant.
     * It is recommended that META_DOWN_MASK be used instead.
     */
    public static final int META_MASK = Event.META_MASK;

    /**
     * The Alt key modifier constant.
     * It is recommended that ALT_DOWN_MASK be used instead.
     */
    public static final int ALT_MASK = Event.ALT_MASK;

    /**
     * The AltGraph key modifier constant.
     */
    public static final int ALT_GRAPH_MASK = 1 << 5;

    /**
     * The Mouse Button1 modifier constant.
     * It is recommended that BUTTON1_DOWN_MASK be used instead.
     */
    public static final int BUTTON1_MASK = 1 << 4;

    /**
     * The Mouse Button2 modifier constant.
     * It is recommended that BUTTON2_DOWN_MASK be used instead.
     * Note that BUTTON2_MASK has the same value as ALT_MASK.
     */
    public static final int BUTTON2_MASK = Event.ALT_MASK;

    /**
     * The Mouse Button3 modifier constant.
     * It is recommended that BUTTON3_DOWN_MASK be used instead.
     * Note that BUTTON3_MASK has the same value as META_MASK.
     */
    public static final int BUTTON3_MASK = Event.META_MASK;

    /**
     * The Shift key extended modifier constant.
     * @since 1.4
     */
    public static final int SHIFT_DOWN_MASK = 1 << 6;

    /**
     * The Control key extended modifier constant.
     * @since 1.4
     */
    public static final int CTRL_DOWN_MASK = 1 << 7;

    /**
     * The Meta key extended modifier constant.
     * @since 1.4
     */
    public static final int META_DOWN_MASK = 1 << 8;

    /**
     * The Alt key extended modifier constant.
     * @since 1.4
     */
    public static final int ALT_DOWN_MASK = 1 << 9;

    /**
     * The Mouse Button1 extended modifier constant.
     * @since 1.4
     */
    public static final int BUTTON1_DOWN_MASK = 1 << 10;

    /**
     * The Mouse Button2 extended modifier constant.
     * @since 1.4
     */
    public static final int BUTTON2_DOWN_MASK = 1 << 11;

    /**
     * The Mouse Button3 extended modifier constant.
     * @since 1.4
     */
    public static final int BUTTON3_DOWN_MASK = 1 << 12;

    /**
     * The AltGraph key extended modifier constant.
     * @since 1.4
     */
    public static final int ALT_GRAPH_DOWN_MASK = 1 << 13;

    /**
     * An array of extended modifiers for additional buttons.
     * @see getButtonDownMasks
     * There are twenty buttons fit into 4byte space.
     * one more bit is reserved for FIRST_HIGH_BIT.
     * @since 7.0
     */
    private static final int [] BUTTON_DOWN_MASK = new int [] { BUTTON1_DOWN_MASK,
                                                               BUTTON2_DOWN_MASK,
                                                               BUTTON3_DOWN_MASK,
                                                               1<<14, //4th phisical button (this is not a wheel!)
                                                               1<<15, //(this is not a wheel!)
                                                               1<<16,
                                                               1<<17,
                                                               1<<18,
                                                               1<<19,
                                                               1<<20,
                                                               1<<21,
                                                               1<<22,
                                                               1<<23,
                                                               1<<24,
                                                               1<<25,
                                                               1<<26,
                                                               1<<27,
                                                               1<<28,
                                                               1<<29,
                                                               1<<30};

    /**
     * A method to access an array of extended modifiers for additional buttons.
     * @since 7.0
     */
    private static int [] getButtonDownMasks(){
        return Arrays.copyOf(BUTTON_DOWN_MASK, BUTTON_DOWN_MASK.length);
    }


    /**
     * A method to obtain a mask for any existing mouse button.
     * The returned mask may be used for different purposes. Following are some of them:
     * <ul>
     * <li> {@link java.awt.Robot#mousePress(int) mousePress(buttons)} and
     *      {@link java.awt.Robot#mouseRelease(int) mouseRelease(buttons)}
     * <li> as a {@code modifiers} parameter when creating a new {@link MouseEvent} instance
     * <li> to check {@link MouseEvent#getModifiersEx() modifiersEx} of existing {@code MouseEvent}
     * </ul>
     * @param button is a number to represent a button starting from 1.
     * For example,
     * <pre>
     * int button = InputEvent.getMaskForButton(1);
     * </pre>
     * will have the same meaning as
     * <pre>
     * int button = InputEvent.getMaskForButton(MouseEvent.BUTTON1);
     * </pre>
     * because {@link MouseEvent#BUTTON1 MouseEvent.BUTTON1} equals to 1.
     * If a mouse has three enabled buttons(see {@link java.awt.MouseInfo#getNumberOfButtons() MouseInfo.getNumberOfButtons()})
     * then the values from the left column passed into the method will return
     * corresponding values from the right column:
     * <PRE>
     *    <b>button </b>   <b>returned mask</b>
     *    {@link MouseEvent#BUTTON1 BUTTON1}  {@link MouseEvent#BUTTON1_DOWN_MASK BUTTON1_DOWN_MASK}
     *    {@link MouseEvent#BUTTON2 BUTTON2}  {@link MouseEvent#BUTTON2_DOWN_MASK BUTTON2_DOWN_MASK}
     *    {@link MouseEvent#BUTTON3 BUTTON3}  {@link MouseEvent#BUTTON3_DOWN_MASK BUTTON3_DOWN_MASK}
     * </PRE>
     * If a mouse has more than three enabled buttons then more values
     * are admissible (4, 5, etc.). There is no assigned constants for these extended buttons.
     * The button masks for the extra buttons returned by this method have no assigned names like the
     * first three button masks.
     * <p>
     * This method has the following implementation restriction.
     * It returns masks for a limited number of buttons only. The maximum number is
     * implementation dependent and may vary.
     * This limit is defined by the relevant number
     * of buttons that may hypothetically exist on the mouse but it is greater than the
     * {@link java.awt.MouseInfo#getNumberOfButtons() MouseInfo.getNumberOfButtons()}.
     * <p>
     * @throws IllegalArgumentException if {@code button} is less than zero or greater than the number
     *         of button masks reserved for buttons
     * @since 7.0
     * @see java.awt.MouseInfo#getNumberOfButtons()
     * @see Toolkit#areExtraMouseButtonsEnabled()
     * @see MouseEvent#getModifiers()
     * @see MouseEvent#getModifiersEx()
     */
    public static int getMaskForButton(int button) {
        if (button <= 0 || button > BUTTON_DOWN_MASK.length) {
            throw new IllegalArgumentException("button doesn\'t exist " + button);
        }
        return BUTTON_DOWN_MASK[button - 1];
    }

    // the constant below MUST be updated if any extra modifier
    // bits are to be added!
    // in fact, it is undesirable to add modifier bits
    // to the same field as this may break applications
    // see bug# 5066958
    static final int FIRST_HIGH_BIT = 1 << 31;

    static final int JDK_1_3_MODIFIERS = SHIFT_DOWN_MASK - 1;
    static final int HIGH_MODIFIERS = ~( FIRST_HIGH_BIT - 1 );

    /**
     * The input event's Time stamp in UTC format.  The time stamp
     * indicates when the input event was created.
     *
     * @serial
     * @see #getWhen()
     */
    long when;

    /**
     * The state of the modifier mask at the time the input
     * event was fired.
     *
     * @serial
     * @see #getModifiers()
     * @see #getModifiersEx()
     * @see KeyEvent
     * @see MouseEvent
     */
    int modifiers;

    /*
     * A flag that indicates that this instance can be used to access
     * the system clipboard.
     */
    private transient boolean canAccessSystemClipboard;

    static {
        /* ensure that the necessary native libraries are loaded */
        java.awt.event.NativeLibLoader.loadLibraries();
        if (!GraphicsEnvironment.isHeadless()) {
            initIDs();
        }
        AWTAccessor.setInputEventAccessor(
            new AWTAccessor.InputEventAccessor() {
                public int[] getButtonDownMasks() {
                    return java.awt.event.InputEvent.getButtonDownMasks();
                }
            });
    }

    /**
     * Initialize JNI field and method IDs for fields that may be
       accessed from C.
     */
    private static native void initIDs();

    /**
     * Constructs an InputEvent object with the specified source component,
     * modifiers, and type.
     * <p> This method throws an
     * <code>IllegalArgumentException</code> if <code>source</code>
     * is <code>null</code>.
     *
     * @param source the object where the event originated
     * @param id           the integer that identifies the event type.
     *                     It is allowed to pass as parameter any value that
     *                     allowed for some subclass of {@code InputEvent} class.
     *                     Passing in the value different from those values result
     *                     in unspecified behavior
     * @param when         a long int that gives the time the event occurred.
     *                     Passing negative or zero value
     *                     is not recommended
     * @param modifiers    a modifier mask describing the modifier keys and mouse
     *                     buttons (for example, shift, ctrl, alt, and meta) that
     *                     are down during the event.
     *                     Only extended modifiers are allowed to be used as a
     *                     value for this parameter (see the {@link java.awt.event.InputEvent#getModifiersEx}
     *                     class for the description of extended modifiers).
     *                     Passing negative parameter
     *                     is not recommended.
     *                     Zero value means that no modifiers were passed
     * @throws IllegalArgumentException if <code>source</code> is null
     * @see #getSource()
     * @see #getID()
     * @see #getWhen()
     * @see #getModifiers()
     */
    InputEvent(Component source, int id, long when, int modifiers) {
        super(source, id);
        this.when = when;
        this.modifiers = modifiers;
        canAccessSystemClipboard = canAccessSystemClipboard();
    }

    private boolean canAccessSystemClipboard() {
        boolean b = false;

        if (!GraphicsEnvironment.isHeadless()) {
            SecurityManager sm = System.getSecurityManager();
            if (sm != null) {
                try {
                    sm.checkPermission(SecurityConstants.AWT.ACCESS_CLIPBOARD_PERMISSION);
                    b = true;
                } catch (SecurityException se) {
                    if (logger.isLoggable(PlatformLogger.Level.FINE)) {
                        logger.fine("InputEvent.canAccessSystemClipboard() got SecurityException ", se);
                    }
                }
            } else {
                b = true;
            }
        }

        return b;
    }

    /**
     * Returns whether or not the Shift modifier is down on this event.
     */
    public boolean isShiftDown() {
        return (modifiers & SHIFT_MASK) != 0;
    }

    /**
     * Returns whether or not the Control modifier is down on this event.
     */
    public boolean isControlDown() {
        return (modifiers & CTRL_MASK) != 0;
    }

    /**
     * Returns whether or not the Meta modifier is down on this event.
     */
    public boolean isMetaDown() {
        return (modifiers & META_MASK) != 0;
    }

    /**
     * Returns whether or not the Alt modifier is down on this event.
     */
    public boolean isAltDown() {
        return (modifiers & ALT_MASK) != 0;
    }

    /**
     * Returns whether or not the AltGraph modifier is down on this event.
     */
    public boolean isAltGraphDown() {
        return (modifiers & ALT_GRAPH_MASK) != 0;
    }

    /**
     * Returns the difference in milliseconds between the timestamp of when this event occurred and
     * midnight, January 1, 1970 UTC.
     */
    public long getWhen() {
        return when;
    }

    /**
     * Returns the modifier mask for this event.
     */
    public int getModifiers() {
        return modifiers & (JDK_1_3_MODIFIERS | HIGH_MODIFIERS);
    }

    /**
     * Returns the extended modifier mask for this event.
     * <P>
     * Extended modifiers are the modifiers that ends with the _DOWN_MASK suffix,
     * such as ALT_DOWN_MASK, BUTTON1_DOWN_MASK, and others.
     * <P>
     * Extended modifiers represent the state of all modal keys,
     * such as ALT, CTRL, META, and the mouse buttons just after
     * the event occurred.
     * <P>
     * For example, if the user presses <b>button 1</b> followed by
     * <b>button 2</b>, and then releases them in the same order,
     * the following sequence of events is generated:
     * <PRE>
     *    <code>MOUSE_PRESSED</code>:  <code>BUTTON1_DOWN_MASK</code>
     *    <code>MOUSE_PRESSED</code>:  <code>BUTTON1_DOWN_MASK | BUTTON2_DOWN_MASK</code>
     *    <code>MOUSE_RELEASED</code>: <code>BUTTON2_DOWN_MASK</code>
     *    <code>MOUSE_CLICKED</code>:  <code>BUTTON2_DOWN_MASK</code>
     *    <code>MOUSE_RELEASED</code>:
     *    <code>MOUSE_CLICKED</code>:
     * </PRE>
     * <P>
     * It is not recommended to compare the return value of this method
     * using <code>==</code> because new modifiers can be added in the future.
     * For example, the appropriate way to check that SHIFT and BUTTON1 are
     * down, but CTRL is up is demonstrated by the following code:
     * <PRE>
     *    int onmask = SHIFT_DOWN_MASK | BUTTON1_DOWN_MASK;
     *    int offmask = CTRL_DOWN_MASK;
     *    if ((event.getModifiersEx() &amp; (onmask | offmask)) == onmask) {
     *        ...
     *    }
     * </PRE>
     * The above code will work even if new modifiers are added.
     *
     * @since 1.4
     */
    public int getModifiersEx() {
        return modifiers & ~JDK_1_3_MODIFIERS;
    }

    /**
     * Consumes this event so that it will not be processed
     * in the default manner by the source which originated it.
     */
    public void consume() {
        consumed = true;
    }

    /**
     * Returns whether or not this event has been consumed.
     * @see #consume
     */
    public boolean isConsumed() {
        return consumed;
    }

    // state serialization compatibility with JDK 1.1
    static final long serialVersionUID = -2482525981698309786L;

    /**
     * Returns a String describing the extended modifier keys and
     * mouse buttons, such as "Shift", "Button1", or "Ctrl+Shift".
     * These strings can be localized by changing the
     * <code>awt.properties</code> file.
     * <p>
     * Note that passing negative parameter is incorrect,
     * and will cause the returning an unspecified string.
     * Zero parameter means that no modifiers were passed and will
     * cause the returning an empty string.
     *
     * @param modifiers a modifier mask describing the extended
     *                modifier keys and mouse buttons for the event
     * @return a text description of the combination of extended
     *         modifier keys and mouse buttons that were held down
     *         during the event.
     * @since 1.4
     */
    public static String getModifiersExText(int modifiers) {
        StringBuilder buf = new StringBuilder();
        if ((modifiers & java.awt.event.InputEvent.META_DOWN_MASK) != 0) {
            buf.append(Toolkit.getProperty("AWT.meta", "Meta"));
            buf.append("+");
        }
        if ((modifiers & java.awt.event.InputEvent.CTRL_DOWN_MASK) != 0) {
            buf.append(Toolkit.getProperty("AWT.control", "Ctrl"));
            buf.append("+");
        }
        if ((modifiers & java.awt.event.InputEvent.ALT_DOWN_MASK) != 0) {
            buf.append(Toolkit.getProperty("AWT.alt", "Alt"));
            buf.append("+");
        }
        if ((modifiers & java.awt.event.InputEvent.SHIFT_DOWN_MASK) != 0) {
            buf.append(Toolkit.getProperty("AWT.shift", "Shift"));
            buf.append("+");
        }
        if ((modifiers & java.awt.event.InputEvent.ALT_GRAPH_DOWN_MASK) != 0) {
            buf.append(Toolkit.getProperty("AWT.altGraph", "Alt Graph"));
            buf.append("+");
        }

        int buttonNumber = 1;
        for (int mask : java.awt.event.InputEvent.BUTTON_DOWN_MASK){
            if ((modifiers & mask) != 0) {
                buf.append(Toolkit.getProperty("AWT.button"+buttonNumber, "Button"+buttonNumber));
                buf.append("+");
            }
            buttonNumber++;
        }
        if (buf.length() > 0) {
            buf.setLength(buf.length()-1); // remove trailing '+'
        }
        return buf.toString();
    }
}
