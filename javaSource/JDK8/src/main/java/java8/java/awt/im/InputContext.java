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

package java8.java.awt.im;

import sun.awt.im.InputMethodContext;

import java.awt.*;
import java.awt.im.InputMethodRequests;
import java.beans.Transient;
import java.lang.Character.Subset;
import java.util.Locale;

/**
 * Provides methods to control text input facilities such as input
 * methods and keyboard layouts.
 * Two methods handle both input methods and keyboard layouts: selectInputMethod
 * lets a client component select an input method or keyboard layout by locale,
 * getLocale lets a client component obtain the locale of the current input method
 * or keyboard layout.
 * The other methods more specifically support interaction with input methods:
 * They let client components control the behavior of input methods, and
 * dispatch events from the client component to the input method.
 *
 * <p>
 * By default, one InputContext instance is created per Window instance,
 * and this input context is shared by all components within the window's
 * container hierarchy. However, this means that only one text input
 * operation is possible at any one time within a window, and that the
 * text needs to be committed when moving the focus from one text component
 * to another. If this is not desired, text components can create their
 * own input context instances.
 *
 * <p>
 * The Java Platform supports input methods that have been developed in the Java
 * programming language, using the interfaces in the {@link java.awt.im.spi} package,
 * and installed into a Java SE Runtime Environment as extensions. Implementations
 * may also support using the native input methods of the platforms they run on;
 * however, not all platforms and locales provide input methods. Keyboard layouts
 * are provided by the host platform.
 *
 * <p>
 * Input methods are <em>unavailable</em> if (a) no input method written
 * in the Java programming language has been installed and (b) the Java Platform implementation
 * or the underlying platform does not support native input methods. In this case,
 * input contexts can still be created and used; their behavior is specified with
 * the individual methods below.
 *
 * @see Component#getInputContext
 * @see Component#enableInputMethods
 * @author JavaSoft Asia/Pacific
 * @since 1.2
 */

public class InputContext {

    /**
     * Constructs an InputContext.
     * This method is protected so clients cannot instantiate
     * InputContext directly. Input contexts are obtained by
     * calling {@link #getInstance}.
     */
    protected InputContext() {
        // real implementation is in sun.awt.im.InputContext
    }

    /**
     * Returns a new InputContext instance.
     */
    public static java.awt.im.InputContext getInstance() {
        return new InputMethodContext();
    }

    /**
     * Attempts to select an input method or keyboard layout that
     * supports the given locale, and returns a value indicating whether such
     * an input method or keyboard layout has been successfully selected. The
     * following steps are taken until an input method has been selected:
     *
     * <ul>
     * <li>
     * If the currently selected input method or keyboard layout supports the
     * requested locale, it remains selected.</li>
     *
     * <li>
     * If there is no input method or keyboard layout available that supports
     * the requested locale, the current input method or keyboard layout remains
     * selected.</li>
     *
     * <li>
     * If the user has previously selected an input method or keyboard layout
     * for the requested locale from the user interface, then the most recently
     * selected such input method or keyboard layout is reselected.</li>
     *
     * <li>
     * Otherwise, an input method or keyboard layout that supports the requested
     * locale is selected in an implementation dependent way.</li>
     *
     * </ul>
     * Before switching away from an input method, any currently uncommitted text
     * is committed. If no input method or keyboard layout supporting the requested
     * locale is available, then false is returned.
     *
     * <p>
     * Not all host operating systems provide API to determine the locale of
     * the currently selected native input method or keyboard layout, and to
     * select a native input method or keyboard layout by locale.
     * For host operating systems that don't provide such API,
     * <code>selectInputMethod</code> assumes that native input methods or
     * keyboard layouts provided by the host operating system support only the
     * system's default locale.
     *
     * <p>
     * A text editing component may call this method, for example, when
     * the user changes the insertion point, so that the user can
     * immediately continue typing in the language of the surrounding text.
     *
     * @param locale The desired new locale.
     * @return true if the input method or keyboard layout that's active after
     *         this call supports the desired locale.
     * @exception NullPointerException if <code>locale</code> is null
     */
    public boolean selectInputMethod(Locale locale) {
        // real implementation is in sun.awt.im.InputContext
        return false;
    }

    /**
     * Returns the current locale of the current input method or keyboard
     * layout.
     * Returns null if the input context does not have a current input method
     * or keyboard layout or if the current input method's
     * {@link java.awt.im.spi.InputMethod#getLocale()} method returns null.
     *
     * <p>
     * Not all host operating systems provide API to determine the locale of
     * the currently selected native input method or keyboard layout.
     * For host operating systems that don't provide such API,
     * <code>getLocale</code> assumes that the current locale of all native
     * input methods or keyboard layouts provided by the host operating system
     * is the system's default locale.
     *
     * @return the current locale of the current input method or keyboard layout
     * @since 1.3
     */
    public Locale getLocale() {
        // real implementation is in sun.awt.im.InputContext
        return null;
    }

    /**
     * Sets the subsets of the Unicode character set that input methods of this input
     * context should be allowed to input. Null may be passed in to
     * indicate that all characters are allowed. The initial value
     * is null. The setting applies to the current input method as well
     * as input methods selected after this call is made. However,
     * applications cannot rely on this call having the desired effect,
     * since this setting cannot be passed on to all host input methods -
     * applications still need to apply their own character validation.
     * If no input methods are available, then this method has no effect.
     *
     * @param subsets The subsets of the Unicode character set from which characters may be input
     */
    public void setCharacterSubsets(Subset[] subsets) {
        // real implementation is in sun.awt.im.InputContext
    }

    /**
     * Enables or disables the current input method for composition,
     * depending on the value of the parameter <code>enable</code>.
     * <p>
     * An input method that is enabled for composition interprets incoming
     * events for both composition and control purposes, while a
     * disabled input method does not interpret events for composition.
     * Note however that events are passed on to the input method regardless
     * whether it is enabled or not, and that an input method that is disabled
     * for composition may still interpret events for control purposes,
     * including to enable or disable itself for composition.
     * <p>
     * For input methods provided by host operating systems, it is not always possible to
     * determine whether this operation is supported. For example, an input method may enable
     * composition only for some locales, and do nothing for other locales. For such input
     * methods, it is possible that this method does not throw
     * {@link UnsupportedOperationException UnsupportedOperationException},
     * but also does not affect whether composition is enabled.
     *
     * @param enable whether to enable the current input method for composition
     * @throws UnsupportedOperationException if there is no current input
     * method available or the current input method does not support
     * the enabling/disabling operation
     * @see #isCompositionEnabled
     * @since 1.3
     */
    public void setCompositionEnabled(boolean enable) {
        // real implementation is in sun.awt.im.InputContext
    }

    /**
     * Determines whether the current input method is enabled for composition.
     * An input method that is enabled for composition interprets incoming
     * events for both composition and control purposes, while a
     * disabled input method does not interpret events for composition.
     *
     * @return <code>true</code> if the current input method is enabled for
     * composition; <code>false</code> otherwise
     * @throws UnsupportedOperationException if there is no current input
     * method available or the current input method does not support
     * checking whether it is enabled for composition
     * @see #setCompositionEnabled
     * @since 1.3
     */
    @Transient
    public boolean isCompositionEnabled() {
        // real implementation is in sun.awt.im.InputContext
        return false;
    }

    /**
     * Asks the current input method to reconvert text from the
     * current client component. The input method obtains the text to
     * be reconverted from the client component using the
     * {@link InputMethodRequests#getSelectedText InputMethodRequests.getSelectedText}
     * method. The other <code>InputMethodRequests</code> methods
     * must be prepared to deal with further information requests by
     * the input method. The composed and/or committed text will be
     * sent to the client component as a sequence of
     * <code>InputMethodEvent</code>s. If the input method cannot
     * reconvert the given text, the text is returned as committed
     * text in an <code>InputMethodEvent</code>.
     *
     * @throws UnsupportedOperationException if there is no current input
     * method available or the current input method does not support
     * the reconversion operation.
     *
     * @since 1.3
     */
    public void reconvert() {
        // real implementation is in sun.awt.im.InputContext
    }

    /**
     * Dispatches an event to the active input method. Called by AWT.
     * If no input method is available, then the event will never be consumed.
     *
     * @param event The event
     * @exception NullPointerException if <code>event</code> is null
     */
    public void dispatchEvent(AWTEvent event) {
        // real implementation is in sun.awt.im.InputContext
    }

    /**
     * Notifies the input context that a client component has been
     * removed from its containment hierarchy, or that input method
     * support has been disabled for the component. This method is
     * usually called from the client component's
     * {@link Component#removeNotify() Component.removeNotify}
     * method. Potentially pending input from input methods
     * for this component is discarded.
     * If no input methods are available, then this method has no effect.
     *
     * @param client Client component
     * @exception NullPointerException if <code>client</code> is null
     */
    public void removeNotify(Component client) {
        // real implementation is in sun.awt.im.InputContext
    }

    /**
     * Ends any input composition that may currently be going on in this
     * context. Depending on the platform and possibly user preferences,
     * this may commit or delete uncommitted text. Any changes to the text
     * are communicated to the active component using an input method event.
     * If no input methods are available, then this method has no effect.
     *
     * <p>
     * A text editing component may call this in a variety of situations,
     * for example, when the user moves the insertion point within the text
     * (but outside the composed text), or when the component's text is
     * saved to a file or copied to the clipboard.
     *
     */
    public void endComposition() {
        // real implementation is in sun.awt.im.InputContext
    }

    /**
     * Releases the resources used by this input context.
     * Called by AWT for the default input context of each Window.
     * If no input methods are available, then this method
     * has no effect.
     */
    public void dispose() {
        // real implementation is in sun.awt.im.InputContext
    }

    /**
     * Returns a control object from the current input method, or null. A
     * control object provides methods that control the behavior of the
     * input method or obtain information from the input method. The type
     * of the object is an input method specific class. Clients have to
     * compare the result against known input method control object
     * classes and cast to the appropriate class to invoke the methods
     * provided.
     * <p>
     * If no input methods are available or the current input method does
     * not provide an input method control object, then null is returned.
     *
     * @return A control object from the current input method, or null.
     */
    public Object getInputMethodControlObject() {
        // real implementation is in sun.awt.im.InputContext
        return null;
    }

}
