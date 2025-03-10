/*
 * Copyright (c) 2005, 2011, Oracle and/or its affiliates. All rights reserved.
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

package java8.java.util.spi;

import java.util.Locale;
import java.util.spi.LocaleServiceProvider;

/**
 * An abstract class for service providers that
 * provide localized names for the
 * {@link Locale Locale} class.
 *
 * @since        1.6
 */
public abstract class LocaleNameProvider extends LocaleServiceProvider {

    /**
     * Sole constructor.  (For invocation by subclass constructors, typically
     * implicit.)
     */
    protected LocaleNameProvider() {
    }

    /**
     * Returns a localized name for the given <a href="http://www.rfc-editor.org/rfc/bcp/bcp47.txt">
     * IETF BCP47</a> language code and the given locale that is appropriate for
     * display to the user.
     * For example, if <code>languageCode</code> is "fr" and <code>locale</code>
     * is en_US, getDisplayLanguage() will return "French"; if <code>languageCode</code>
     * is "en" and <code>locale</code> is fr_FR, getDisplayLanguage() will return "anglais".
     * If the name returned cannot be localized according to <code>locale</code>,
     * (say, the provider does not have a Japanese name for Croatian),
     * this method returns null.
     * @param languageCode the language code string in the form of two to eight
     *     lower-case letters between 'a' (U+0061) and 'z' (U+007A)
     * @param locale the desired locale
     * @return the name of the given language code for the specified locale, or null if it's not
     *     available.
     * @exception NullPointerException if <code>languageCode</code> or <code>locale</code> is null
     * @exception IllegalArgumentException if <code>languageCode</code> is not in the form of
     *     two or three lower-case letters, or <code>locale</code> isn't
     *     one of the locales returned from
     *     {@link LocaleServiceProvider#getAvailableLocales()
     *     getAvailableLocales()}.
     * @see Locale#getDisplayLanguage(Locale)
     */
    public abstract String getDisplayLanguage(String languageCode, Locale locale);

    /**
     * Returns a localized name for the given <a href="http://www.rfc-editor.org/rfc/bcp/bcp47.txt">
     * IETF BCP47</a> script code and the given locale that is appropriate for
     * display to the user.
     * For example, if <code>scriptCode</code> is "Latn" and <code>locale</code>
     * is en_US, getDisplayScript() will return "Latin"; if <code>scriptCode</code>
     * is "Cyrl" and <code>locale</code> is fr_FR, getDisplayScript() will return "cyrillique".
     * If the name returned cannot be localized according to <code>locale</code>,
     * (say, the provider does not have a Japanese name for Cyrillic),
     * this method returns null. The default implementation returns null.
     * @param scriptCode the four letter script code string in the form of title-case
     *     letters (the first letter is upper-case character between 'A' (U+0041) and
     *     'Z' (U+005A) followed by three lower-case character between 'a' (U+0061)
     *     and 'z' (U+007A)).
     * @param locale the desired locale
     * @return the name of the given script code for the specified locale, or null if it's not
     *     available.
     * @exception NullPointerException if <code>scriptCode</code> or <code>locale</code> is null
     * @exception IllegalArgumentException if <code>scriptCode</code> is not in the form of
     *     four title case letters, or <code>locale</code> isn't
     *     one of the locales returned from
     *     {@link LocaleServiceProvider#getAvailableLocales()
     *     getAvailableLocales()}.
     * @see Locale#getDisplayScript(Locale)
     * @since 1.7
     */
    public String getDisplayScript(String scriptCode, Locale locale) {
        return null;
    }

    /**
     * Returns a localized name for the given <a href="http://www.rfc-editor.org/rfc/bcp/bcp47.txt">
     * IETF BCP47</a> region code (either ISO 3166 country code or UN M.49 area
     * codes) and the given locale that is appropriate for display to the user.
     * For example, if <code>countryCode</code> is "FR" and <code>locale</code>
     * is en_US, getDisplayCountry() will return "France"; if <code>countryCode</code>
     * is "US" and <code>locale</code> is fr_FR, getDisplayCountry() will return "Etats-Unis".
     * If the name returned cannot be localized according to <code>locale</code>,
     * (say, the provider does not have a Japanese name for Croatia),
     * this method returns null.
     * @param countryCode the country(region) code string in the form of two
     *     upper-case letters between 'A' (U+0041) and 'Z' (U+005A) or the UN M.49 area code
     *     in the form of three digit letters between '0' (U+0030) and '9' (U+0039).
     * @param locale the desired locale
     * @return the name of the given country code for the specified locale, or null if it's not
     *     available.
     * @exception NullPointerException if <code>countryCode</code> or <code>locale</code> is null
     * @exception IllegalArgumentException if <code>countryCode</code> is not in the form of
     *     two upper-case letters or three digit letters, or <code>locale</code> isn't
     *     one of the locales returned from
     *     {@link LocaleServiceProvider#getAvailableLocales()
     *     getAvailableLocales()}.
     * @see Locale#getDisplayCountry(Locale)
     */
    public abstract String getDisplayCountry(String countryCode, Locale locale);

    /**
     * Returns a localized name for the given variant code and the given locale that
     * is appropriate for display to the user.
     * If the name returned cannot be localized according to <code>locale</code>,
     * this method returns null.
     * @param variant the variant string
     * @param locale the desired locale
     * @return the name of the given variant string for the specified locale, or null if it's not
     *     available.
     * @exception NullPointerException if <code>variant</code> or <code>locale</code> is null
     * @exception IllegalArgumentException if <code>locale</code> isn't
     *     one of the locales returned from
     *     {@link LocaleServiceProvider#getAvailableLocales()
     *     getAvailableLocales()}.
     * @see Locale#getDisplayVariant(Locale)
     */
    public abstract String getDisplayVariant(String variant, Locale locale);
}
