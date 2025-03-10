/*
 * Copyright (c) 1997, 2013, Oracle and/or its affiliates. All rights reserved.
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
package java8.javax.swing;

import javax.swing.*;
import java.awt.*;


/**
 * This class has been obsoleted by the 1.4 focus APIs. While client code may
 * still use this class, developers are strongly encouraged to use
 * <code>java.awt.KeyboardFocusManager</code> and
 * <code>java.awt.DefaultKeyboardFocusManager</code> instead.
 * <p>
 * Please see
 * <a href="http://docs.oracle.com/javase/tutorial/uiswing/misc/focus.html">
 * How to Use the Focus Subsystem</a>,
 * a section in <em>The Java Tutorial</em>, and the
 * <a href="../../java/awt/doc-files/FocusSpec.html">Focus Specification</a>
 * for more information.
 *
 * @see <a href="../../java/awt/doc-files/FocusSpec.html">Focus Specification</a>
 *
 * @author Arnaud Weber
 * @author David Mendenhall
 */
public abstract class FocusManager extends DefaultKeyboardFocusManager {

    /**
     * This field is obsolete, and its use is discouraged since its
     * specification is incompatible with the 1.4 focus APIs.
     * The current FocusManager is no longer a property of the UI.
     * Client code must query for the current FocusManager using
     * <code>KeyboardFocusManager.getCurrentKeyboardFocusManager()</code>.
     * See the Focus Specification for more information.
     *
     * @see KeyboardFocusManager#getCurrentKeyboardFocusManager
     * @see <a href="../../java/awt/doc-files/FocusSpec.html">Focus Specification</a>
     */
    public static final String FOCUS_MANAGER_CLASS_PROPERTY =
        "FocusManagerClassName";

    private static boolean enabled = true;

    /**
     * Returns the current <code>KeyboardFocusManager</code> instance
     * for the calling thread's context.
     *
     * @return this thread's context's <code>KeyboardFocusManager</code>
     * @see #setCurrentManager
     */
    public static javax.swing.FocusManager getCurrentManager() {
        KeyboardFocusManager manager =
            KeyboardFocusManager.getCurrentKeyboardFocusManager();
        if (manager instanceof javax.swing.FocusManager) {
            return (javax.swing.FocusManager)manager;
        } else {
            return new DelegatingDefaultFocusManager(manager);
        }
    }

    /**
     * Sets the current <code>KeyboardFocusManager</code> instance
     * for the calling thread's context. If <code>null</code> is
     * specified, then the current <code>KeyboardFocusManager</code>
     * is replaced with a new instance of
     * <code>DefaultKeyboardFocusManager</code>.
     * <p>
     * If a <code>SecurityManager</code> is installed,
     * the calling thread must be granted the <code>AWTPermission</code>
     * "replaceKeyboardFocusManager" in order to replace the
     * the current <code>KeyboardFocusManager</code>.
     * If this permission is not granted,
     * this method will throw a <code>SecurityException</code>,
     * and the current <code>KeyboardFocusManager</code> will be unchanged.
     *
     * @param aFocusManager the new <code>KeyboardFocusManager</code>
     *     for this thread's context
     * @see #getCurrentManager
     * @see DefaultKeyboardFocusManager
     * @throws SecurityException if the calling thread does not have permission
     *         to replace the current <code>KeyboardFocusManager</code>
     */
    public static void setCurrentManager(javax.swing.FocusManager aFocusManager)
        throws SecurityException
    {
        // Note: This method is not backward-compatible with 1.3 and earlier
        // releases. It now throws a SecurityException in an applet, whereas
        // in previous releases, it did not. This issue was discussed at
        // length, and ultimately approved by Hans.
        KeyboardFocusManager toSet =
            (aFocusManager instanceof DelegatingDefaultFocusManager)
                ? ((DelegatingDefaultFocusManager)aFocusManager).getDelegate()
                : aFocusManager;
        KeyboardFocusManager.setCurrentKeyboardFocusManager(toSet);
    }

    /**
     * Changes the current <code>KeyboardFocusManager</code>'s default
     * <code>FocusTraversalPolicy</code> to
     * <code>DefaultFocusTraversalPolicy</code>.
     *
     * @see DefaultFocusTraversalPolicy
     * @see KeyboardFocusManager#setDefaultFocusTraversalPolicy
     * @deprecated as of 1.4, replaced by
     * <code>KeyboardFocusManager.setDefaultFocusTraversalPolicy(FocusTraversalPolicy)</code>
     */
    @Deprecated
    public static void disableSwingFocusManager() {
        if (enabled) {
            enabled = false;
            KeyboardFocusManager.getCurrentKeyboardFocusManager().
                setDefaultFocusTraversalPolicy(
                    new DefaultFocusTraversalPolicy());
        }
    }

    /**
     * Returns whether the application has invoked
     * <code>disableSwingFocusManager()</code>.
     *
     * @see #disableSwingFocusManager
     * @deprecated As of 1.4, replaced by
     *   <code>KeyboardFocusManager.getDefaultFocusTraversalPolicy()</code>
     */
    @Deprecated
    public static boolean isFocusManagerEnabled() {
        return enabled;
    }
}
