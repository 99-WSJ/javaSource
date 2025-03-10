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

import java.util.Calendar;

/**
 * Enumeration of the style of text formatting and parsing.
 * <p>
 * Text styles define three sizes for the formatted text - 'full', 'short' and 'narrow'.
 * Each of these three sizes is available in both 'standard' and 'stand-alone' variations.
 * <p>
 * The difference between the three sizes is obvious in most languages.
 * For example, in English the 'full' month is 'January', the 'short' month is 'Jan'
 * and the 'narrow' month is 'J'. Note that the narrow size is often not unique.
 * For example, 'January', 'June' and 'July' all have the 'narrow' text 'J'.
 * <p>
 * The difference between the 'standard' and 'stand-alone' forms is trickier to describe
 * as there is no difference in English. However, in other languages there is a difference
 * in the word used when the text is used alone, as opposed to in a complete date.
 * For example, the word used for a month when used alone in a date picker is different
 * to the word used for month in association with a day and year in a date.
 *
 * @implSpec
 * This is immutable and thread-safe enum.
 */
public enum TextStyle {
    // ordered from large to small
    // ordered so that bit 0 of the ordinal indicates stand-alone.

    /**
     * Full text, typically the full description.
     * For example, day-of-week Monday might output "Monday".
     */
    FULL(Calendar.LONG_FORMAT, 0),
    /**
     * Full text for stand-alone use, typically the full description.
     * For example, day-of-week Monday might output "Monday".
     */
    FULL_STANDALONE(Calendar.LONG_STANDALONE, 0),
    /**
     * Short text, typically an abbreviation.
     * For example, day-of-week Monday might output "Mon".
     */
    SHORT(Calendar.SHORT_FORMAT, 1),
    /**
     * Short text for stand-alone use, typically an abbreviation.
     * For example, day-of-week Monday might output "Mon".
     */
    SHORT_STANDALONE(Calendar.SHORT_STANDALONE, 1),
    /**
     * Narrow text, typically a single letter.
     * For example, day-of-week Monday might output "M".
     */
    NARROW(Calendar.NARROW_FORMAT, 1),
    /**
     * Narrow text for stand-alone use, typically a single letter.
     * For example, day-of-week Monday might output "M".
     */
    NARROW_STANDALONE(Calendar.NARROW_STANDALONE, 1);

    private final int calendarStyle;
    private final int zoneNameStyleIndex;

    private TextStyle(int calendarStyle, int zoneNameStyleIndex) {
        this.calendarStyle = calendarStyle;
        this.zoneNameStyleIndex = zoneNameStyleIndex;
    }

    /**
     * Returns true if the Style is a stand-alone style.
     * @return true if the style is a stand-alone style.
     */
    public boolean isStandalone() {
        return (ordinal() & 1) == 1;
    }

    /**
     * Returns the stand-alone style with the same size.
     * @return the stand-alone style with the same size
     */
    public java.time.format.TextStyle asStandalone() {
        return java.time.format.TextStyle.values()[ordinal()  | 1];
    }

    /**
     * Returns the normal style with the same size.
     *
     * @return the normal style with the same size
     */
    public java.time.format.TextStyle asNormal() {
        return java.time.format.TextStyle.values()[ordinal() & ~1];
    }

    /**
     * Returns the {@code Calendar} style corresponding to this {@code TextStyle}.
     *
     * @return the corresponding {@code Calendar} style
     */
    int toCalendarStyle() {
        return calendarStyle;
    }

    /**
     * Returns the relative index value to an element of the {@link
     * java.text.DateFormatSymbols#getZoneStrings() DateFormatSymbols.getZoneStrings()}
     * value, 0 for long names and 1 for short names (abbreviations). Note that these values
     * do <em>not</em> correspond to the {@link java.util.TimeZone#LONG} and {@link
     * java.util.TimeZone#SHORT} values.
     *
     * @return the relative index value to time zone names array
     */
    int zoneNameStyleIndex() {
        return zoneNameStyleIndex;
    }
}
