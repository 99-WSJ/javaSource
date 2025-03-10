/*
 * Copyright (c) 1998, 1999, Oracle and/or its affiliates. All rights reserved.
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


package java8.java.awt.im.spi;

import java.awt.*;
import java.awt.im.spi.InputMethod;
import java.util.Locale;

/**
 * Defines methods that provide sufficient information about an input method
 * to enable selection and loading of that input method.
 * The input method itself is only loaded when it is actually used.
 *
 * @since 1.3
 */

public interface InputMethodDescriptor {

    /**
     * Returns the locales supported by the corresponding input method.
     * The locale may describe just the language, or may also include
     * country and variant information if needed.
     * The information is used to select input methods by locale
     * ({@link java.awt.im.InputContext#selectInputMethod(Locale)}). It may also
     * be used to sort input methods by locale in a user-visible
     * list of input methods.
     * <p>
     * Only the input method's primary locales should be returned.
     * For example, if a Japanese input method also has a pass-through
     * mode for Roman characters, typically still only Japanese would
     * be returned. Thus, the list of locales returned is typically
     * a subset of the locales for which the corresponding input method's
     * implementation of {@link InputMethod#setLocale} returns true.
     * <p>
     * If {@link #hasDynamicLocaleList} returns true, this method is
     * called each time the information is needed. This
     * gives input methods that depend on network resources the chance
     * to add or remove locales as resources become available or
     * unavailable.
     *
     * @return the locales supported by the input method
     * @exception AWTException if it can be determined that the input method
     * is inoperable, for example, because of incomplete installation.
     */
    Locale[] getAvailableLocales() throws AWTException;

    /**
     * Returns whether the list of available locales can change
     * at runtime. This may be the case, for example, for adapters
     * that access real input methods over the network.
     */
    boolean hasDynamicLocaleList();

    /**
     * Returns the user-visible name of the corresponding
     * input method for the given input locale in the language in which
     * the name will be displayed.
     * <p>
     * The inputLocale parameter specifies the locale for which text
     * is input.
     * This parameter can only take values obtained from this descriptor's
     * {@link #getAvailableLocales} method or null. If it is null, an
     * input locale independent name for the input method should be
     * returned.
     * <p>
     * If a name for the desired display language is not available, the
     * method may fall back to some other language.
     *
     * @param inputLocale the locale for which text input is supported, or null
     * @param displayLanguage the language in which the name will be displayed
     */
    String getInputMethodDisplayName(Locale inputLocale, Locale displayLanguage);

    /**
     * Returns an icon for the corresponding input method.
     * The icon may be used by a user interface for selecting input methods.
     * <p>
     * The inputLocale parameter specifies the locale for which text
     * is input.
     * This parameter can only take values obtained from this descriptor's
     * {@link #getAvailableLocales} method or null. If it is null, an
     * input locale independent icon for the input method should be
     * returned.
     * <p>
     * The icon's size should be 16&times;16 pixels.
     *
     * @param inputLocale the locale for which text input is supported, or null
     * @return an icon for the corresponding input method, or null
     */
    Image getInputMethodIcon(Locale inputLocale);

    /**
     * Creates a new instance of the corresponding input method.
     *
     * @return a new instance of the corresponding input method
     * @exception Exception any exception that may occur while creating the
     * input method instance
     */
    InputMethod createInputMethod() throws Exception;
}
