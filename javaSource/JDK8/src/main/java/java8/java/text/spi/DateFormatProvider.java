/*
 * Copyright (c) 2005, Oracle and/or its affiliates. All rights reserved.
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

package java8.java.text.spi;

import java.text.DateFormat;
import java.util.Locale;
import java.util.spi.LocaleServiceProvider;

/**
 * An abstract class for service providers that
 * provide concrete implementations of the
 * {@link DateFormat DateFormat} class.
 *
 * @since        1.6
 */
public abstract class DateFormatProvider extends LocaleServiceProvider {

    /**
     * Sole constructor.  (For invocation by subclass constructors, typically
     * implicit.)
     */
    protected DateFormatProvider() {
    }

    /**
     * Returns a new <code>DateFormat</code> instance which formats time
     * with the given formatting style for the specified locale.
     * @param style the given formatting style.  Either one of
     *     {@link DateFormat#SHORT DateFormat.SHORT},
     *     {@link DateFormat#MEDIUM DateFormat.MEDIUM},
     *     {@link DateFormat#LONG DateFormat.LONG}, or
     *     {@link DateFormat#FULL DateFormat.FULL}.
     * @param locale the desired locale.
     * @exception IllegalArgumentException if <code>style</code> is invalid,
     *     or if <code>locale</code> isn't
     *     one of the locales returned from
     *     {@link LocaleServiceProvider#getAvailableLocales()
     *     getAvailableLocales()}.
     * @exception NullPointerException if <code>locale</code> is null
     * @return a time formatter.
     * @see DateFormat#getTimeInstance(int, Locale)
     */
    public abstract DateFormat getTimeInstance(int style, Locale locale);

    /**
     * Returns a new <code>DateFormat</code> instance which formats date
     * with the given formatting style for the specified locale.
     * @param style the given formatting style.  Either one of
     *     {@link DateFormat#SHORT DateFormat.SHORT},
     *     {@link DateFormat#MEDIUM DateFormat.MEDIUM},
     *     {@link DateFormat#LONG DateFormat.LONG}, or
     *     {@link DateFormat#FULL DateFormat.FULL}.
     * @param locale the desired locale.
     * @exception IllegalArgumentException if <code>style</code> is invalid,
     *     or if <code>locale</code> isn't
     *     one of the locales returned from
     *     {@link LocaleServiceProvider#getAvailableLocales()
     *     getAvailableLocales()}.
     * @exception NullPointerException if <code>locale</code> is null
     * @return a date formatter.
     * @see DateFormat#getDateInstance(int, Locale)
     */
    public abstract DateFormat getDateInstance(int style, Locale locale);

    /**
     * Returns a new <code>DateFormat</code> instance which formats date and time
     * with the given formatting style for the specified locale.
     * @param dateStyle the given date formatting style.  Either one of
     *     {@link DateFormat#SHORT DateFormat.SHORT},
     *     {@link DateFormat#MEDIUM DateFormat.MEDIUM},
     *     {@link DateFormat#LONG DateFormat.LONG}, or
     *     {@link DateFormat#FULL DateFormat.FULL}.
     * @param timeStyle the given time formatting style.  Either one of
     *     {@link DateFormat#SHORT DateFormat.SHORT},
     *     {@link DateFormat#MEDIUM DateFormat.MEDIUM},
     *     {@link DateFormat#LONG DateFormat.LONG}, or
     *     {@link DateFormat#FULL DateFormat.FULL}.
     * @param locale the desired locale.
     * @exception IllegalArgumentException if <code>dateStyle</code> or
     *     <code>timeStyle</code> is invalid,
     *     or if <code>locale</code> isn't
     *     one of the locales returned from
     *     {@link LocaleServiceProvider#getAvailableLocales()
     *     getAvailableLocales()}.
     * @exception NullPointerException if <code>locale</code> is null
     * @return a date/time formatter.
     * @see DateFormat#getDateTimeInstance(int, int, Locale)
     */
    public abstract DateFormat
        getDateTimeInstance(int dateStyle, int timeStyle, Locale locale);
}
