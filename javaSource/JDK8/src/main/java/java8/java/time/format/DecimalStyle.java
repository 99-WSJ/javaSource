/*
 * Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
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

/*
 *
 *
 *
 *
 *
 * Copyright (c) 2008-2012, Stephen Colebourne & Michael Nascimento Santos
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of JSR-310 nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package java8.java.time.format;

import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Localized decimal style used in date and time formatting.
 * <p>
 * A significant part of dealing with dates and times is the localization.
 * This class acts as a central point for accessing the information.
 *
 * @implSpec
 * This class is immutable and thread-safe.
 *
 * @since 1.8
 */
public final class DecimalStyle {

    /**
     * The standard set of non-localized decimal style symbols.
     * <p>
     * This uses standard ASCII characters for zero, positive, negative and a dot for the decimal point.
     */
    public static final java.time.format.DecimalStyle STANDARD = new java.time.format.DecimalStyle('0', '+', '-', '.');
    /**
     * The cache of DecimalStyle instances.
     */
    private static final ConcurrentMap<Locale, java.time.format.DecimalStyle> CACHE = new ConcurrentHashMap<>(16, 0.75f, 2);

    /**
     * The zero digit.
     */
    private final char zeroDigit;
    /**
     * The positive sign.
     */
    private final char positiveSign;
    /**
     * The negative sign.
     */
    private final char negativeSign;
    /**
     * The decimal separator.
     */
    private final char decimalSeparator;

    //-----------------------------------------------------------------------
    /**
     * Lists all the locales that are supported.
     * <p>
     * The locale 'en_US' will always be present.
     *
     * @return a Set of Locales for which localization is supported
     */
    public static Set<Locale> getAvailableLocales() {
        Locale[] l = DecimalFormatSymbols.getAvailableLocales();
        Set<Locale> locales = new HashSet<>(l.length);
        Collections.addAll(locales, l);
        return locales;
    }

    /**
     * Obtains the DecimalStyle for the default
     * {@link Locale.Category#FORMAT FORMAT} locale.
     * <p>
     * This method provides access to locale sensitive decimal style symbols.
     * <p>
     * This is equivalent to calling
     * {@link #of(Locale)
     *     of(Locale.getDefault(Locale.Category.FORMAT))}.
     *
     * @see Locale.Category#FORMAT
     * @return the info, not null
     */
    public static java.time.format.DecimalStyle ofDefaultLocale() {
        return of(Locale.getDefault(Locale.Category.FORMAT));
    }

    /**
     * Obtains the DecimalStyle for the specified locale.
     * <p>
     * This method provides access to locale sensitive decimal style symbols.
     *
     * @param locale  the locale, not null
     * @return the info, not null
     */
    public static java.time.format.DecimalStyle of(Locale locale) {
        Objects.requireNonNull(locale, "locale");
        java.time.format.DecimalStyle info = CACHE.get(locale);
        if (info == null) {
            info = create(locale);
            CACHE.putIfAbsent(locale, info);
            info = CACHE.get(locale);
        }
        return info;
    }

    private static java.time.format.DecimalStyle create(Locale locale) {
        DecimalFormatSymbols oldSymbols = DecimalFormatSymbols.getInstance(locale);
        char zeroDigit = oldSymbols.getZeroDigit();
        char positiveSign = '+';
        char negativeSign = oldSymbols.getMinusSign();
        char decimalSeparator = oldSymbols.getDecimalSeparator();
        if (zeroDigit == '0' && negativeSign == '-' && decimalSeparator == '.') {
            return STANDARD;
        }
        return new java.time.format.DecimalStyle(zeroDigit, positiveSign, negativeSign, decimalSeparator);
    }

    //-----------------------------------------------------------------------
    /**
     * Restricted constructor.
     *
     * @param zeroChar  the character to use for the digit of zero
     * @param positiveSignChar  the character to use for the positive sign
     * @param negativeSignChar  the character to use for the negative sign
     * @param decimalPointChar  the character to use for the decimal point
     */
    private DecimalStyle(char zeroChar, char positiveSignChar, char negativeSignChar, char decimalPointChar) {
        this.zeroDigit = zeroChar;
        this.positiveSign = positiveSignChar;
        this.negativeSign = negativeSignChar;
        this.decimalSeparator = decimalPointChar;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the character that represents zero.
     * <p>
     * The character used to represent digits may vary by culture.
     * This method specifies the zero character to use, which implies the characters for one to nine.
     *
     * @return the character for zero
     */
    public char getZeroDigit() {
        return zeroDigit;
    }

    /**
     * Returns a copy of the info with a new character that represents zero.
     * <p>
     * The character used to represent digits may vary by culture.
     * This method specifies the zero character to use, which implies the characters for one to nine.
     *
     * @param zeroDigit  the character for zero
     * @return  a copy with a new character that represents zero, not null

     */
    public java.time.format.DecimalStyle withZeroDigit(char zeroDigit) {
        if (zeroDigit == this.zeroDigit) {
            return this;
        }
        return new java.time.format.DecimalStyle(zeroDigit, positiveSign, negativeSign, decimalSeparator);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the character that represents the positive sign.
     * <p>
     * The character used to represent a positive number may vary by culture.
     * This method specifies the character to use.
     *
     * @return the character for the positive sign
     */
    public char getPositiveSign() {
        return positiveSign;
    }

    /**
     * Returns a copy of the info with a new character that represents the positive sign.
     * <p>
     * The character used to represent a positive number may vary by culture.
     * This method specifies the character to use.
     *
     * @param positiveSign  the character for the positive sign
     * @return  a copy with a new character that represents the positive sign, not null
     */
    public java.time.format.DecimalStyle withPositiveSign(char positiveSign) {
        if (positiveSign == this.positiveSign) {
            return this;
        }
        return new java.time.format.DecimalStyle(zeroDigit, positiveSign, negativeSign, decimalSeparator);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the character that represents the negative sign.
     * <p>
     * The character used to represent a negative number may vary by culture.
     * This method specifies the character to use.
     *
     * @return the character for the negative sign
     */
    public char getNegativeSign() {
        return negativeSign;
    }

    /**
     * Returns a copy of the info with a new character that represents the negative sign.
     * <p>
     * The character used to represent a negative number may vary by culture.
     * This method specifies the character to use.
     *
     * @param negativeSign  the character for the negative sign
     * @return  a copy with a new character that represents the negative sign, not null
     */
    public java.time.format.DecimalStyle withNegativeSign(char negativeSign) {
        if (negativeSign == this.negativeSign) {
            return this;
        }
        return new java.time.format.DecimalStyle(zeroDigit, positiveSign, negativeSign, decimalSeparator);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the character that represents the decimal point.
     * <p>
     * The character used to represent a decimal point may vary by culture.
     * This method specifies the character to use.
     *
     * @return the character for the decimal point
     */
    public char getDecimalSeparator() {
        return decimalSeparator;
    }

    /**
     * Returns a copy of the info with a new character that represents the decimal point.
     * <p>
     * The character used to represent a decimal point may vary by culture.
     * This method specifies the character to use.
     *
     * @param decimalSeparator  the character for the decimal point
     * @return  a copy with a new character that represents the decimal point, not null
     */
    public java.time.format.DecimalStyle withDecimalSeparator(char decimalSeparator) {
        if (decimalSeparator == this.decimalSeparator) {
            return this;
        }
        return new java.time.format.DecimalStyle(zeroDigit, positiveSign, negativeSign, decimalSeparator);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks whether the character is a digit, based on the currently set zero character.
     *
     * @param ch  the character to check
     * @return the value, 0 to 9, of the character, or -1 if not a digit
     */
    int convertToDigit(char ch) {
        int val = ch - zeroDigit;
        return (val >= 0 && val <= 9) ? val : -1;
    }

    /**
     * Converts the input numeric text to the internationalized form using the zero character.
     *
     * @param numericText  the text, consisting of digits 0 to 9, to convert, not null
     * @return the internationalized text, not null
     */
    String convertNumberToI18N(String numericText) {
        if (zeroDigit == '0') {
            return numericText;
        }
        int diff = zeroDigit - '0';
        char[] array = numericText.toCharArray();
        for (int i = 0; i < array.length; i++) {
            array[i] = (char) (array[i] + diff);
        }
        return new String(array);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this DecimalStyle is equal another DecimalStyle.
     *
     * @param obj  the object to check, null returns false
     * @return true if this is equal to the other date
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof java.time.format.DecimalStyle) {
            java.time.format.DecimalStyle other = (java.time.format.DecimalStyle) obj;
            return (zeroDigit == other.zeroDigit && positiveSign == other.positiveSign &&
                    negativeSign == other.negativeSign && decimalSeparator == other.decimalSeparator);
        }
        return false;
    }

    /**
     * A hash code for this DecimalStyle.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return zeroDigit + positiveSign + negativeSign + decimalSeparator;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a string describing this DecimalStyle.
     *
     * @return a string description, not null
     */
    @Override
    public String toString() {
        return "DecimalStyle[" + zeroDigit + positiveSign + negativeSign + decimalSeparator + "]";
    }

}
