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
 * Copyright (c) 2012, Stephen Colebourne & Michael Nascimento Santos
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
package java8.java.time.chrono;

import sun.util.calendar.CalendarDate;

import java.io.*;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.chrono.Era;
import java.time.chrono.JapaneseChronology;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import java.util.Arrays;
import java.util.Objects;

import static java.time.chrono.JapaneseDate.MEIJI_6_ISODATE;
import static java.time.temporal.ChronoField.ERA;

/**
 * An era in the Japanese Imperial calendar system.
 * <p>
 * This class defines the valid eras for the Japanese chronology.
 * Japan introduced the Gregorian calendar starting with Meiji 6.
 * Only Meiji and later eras are supported;
 * dates before Meiji 6, January 1 are not supported.
 *
 * @implSpec
 * This class is immutable and thread-safe.
 *
 * @since 1.8
 */
public final class JapaneseEra
        implements Era, Serializable {

    // The offset value to 0-based index from the era value.
    // i.e., getValue() + ERA_OFFSET == 0-based index
    static final int ERA_OFFSET = 2;

    static final sun.util.calendar.Era[] ERA_CONFIG;

    /**
     * The singleton instance for the 'Meiji' era (1868-01-01 - 1912-07-29)
     * which has the value -1.
     */
    public static final java.time.chrono.JapaneseEra MEIJI = new java.time.chrono.JapaneseEra(-1, LocalDate.of(1868, 1, 1));
    /**
     * The singleton instance for the 'Taisho' era (1912-07-30 - 1926-12-24)
     * which has the value 0.
     */
    public static final java.time.chrono.JapaneseEra TAISHO = new java.time.chrono.JapaneseEra(0, LocalDate.of(1912, 7, 30));
    /**
     * The singleton instance for the 'Showa' era (1926-12-25 - 1989-01-07)
     * which has the value 1.
     */
    public static final java.time.chrono.JapaneseEra SHOWA = new java.time.chrono.JapaneseEra(1, LocalDate.of(1926, 12, 25));
    /**
     * The singleton instance for the 'Heisei' era (1989-01-08 - current)
     * which has the value 2.
     */
    public static final java.time.chrono.JapaneseEra HEISEI = new java.time.chrono.JapaneseEra(2, LocalDate.of(1989, 1, 8));

    // the number of defined JapaneseEra constants.
    // There could be an extra era defined in its configuration.
    private static final int N_ERA_CONSTANTS = HEISEI.getValue() + ERA_OFFSET + 1;

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 1466499369062886794L;

    // array for the singleton JapaneseEra instances
    private static final java.time.chrono.JapaneseEra[] KNOWN_ERAS;

    static {
        ERA_CONFIG = JapaneseChronology.JCAL.getEras();

        KNOWN_ERAS = new java.time.chrono.JapaneseEra[ERA_CONFIG.length];
        KNOWN_ERAS[0] = MEIJI;
        KNOWN_ERAS[1] = TAISHO;
        KNOWN_ERAS[2] = SHOWA;
        KNOWN_ERAS[3] = HEISEI;
        for (int i = N_ERA_CONSTANTS; i < ERA_CONFIG.length; i++) {
            CalendarDate date = ERA_CONFIG[i].getSinceDate();
            LocalDate isoDate = LocalDate.of(date.getYear(), date.getMonth(), date.getDayOfMonth());
            KNOWN_ERAS[i] = new java.time.chrono.JapaneseEra(i - ERA_OFFSET, isoDate);
        }
    };

    /**
     * The era value.
     * @serial
     */
    private final transient int eraValue;

    // the first day of the era
    private final transient LocalDate since;

    /**
     * Creates an instance.
     *
     * @param eraValue  the era value, validated
     * @param since  the date representing the first date of the era, validated not null
     */
    private JapaneseEra(int eraValue, LocalDate since) {
        this.eraValue = eraValue;
        this.since = since;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the Sun private Era instance corresponding to this {@code JapaneseEra}.
     *
     * @return the Sun private Era instance for this {@code JapaneseEra}.
     */
    sun.util.calendar.Era getPrivateEra() {
        return ERA_CONFIG[ordinal(eraValue)];
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code JapaneseEra} from an {@code int} value.
     * <p>
     * The {@link #SHOWA} era that contains 1970-01-01 (ISO calendar system) has the value 1
     * Later era is numbered 2 ({@link #HEISEI}). Earlier eras are numbered 0 ({@link #TAISHO}),
     * -1 ({@link #MEIJI}), only Meiji and later eras are supported.
     *
     * @param japaneseEra  the era to represent
     * @return the {@code JapaneseEra} singleton, not null
     * @throws DateTimeException if the value is invalid
     */
    public static java.time.chrono.JapaneseEra of(int japaneseEra) {
        if (japaneseEra < MEIJI.eraValue || japaneseEra + ERA_OFFSET - 1 >= KNOWN_ERAS.length) {
            throw new DateTimeException("Invalid era: " + japaneseEra);
        }
        return KNOWN_ERAS[ordinal(japaneseEra)];
    }

    /**
     * Returns the {@code JapaneseEra} with the name.
     * <p>
     * The string must match exactly the name of the era.
     * (Extraneous whitespace characters are not permitted.)
     *
     * @param japaneseEra  the japaneseEra name; non-null
     * @return the {@code JapaneseEra} singleton, never null
     * @throws IllegalArgumentException if there is not JapaneseEra with the specified name
     */
    public static java.time.chrono.JapaneseEra valueOf(String japaneseEra) {
        Objects.requireNonNull(japaneseEra, "japaneseEra");
        for (java.time.chrono.JapaneseEra era : KNOWN_ERAS) {
            if (era.getName().equals(japaneseEra)) {
                return era;
            }
        }
        throw new IllegalArgumentException("japaneseEra is invalid");
    }

    /**
     * Returns an array of JapaneseEras.
     * <p>
     * This method may be used to iterate over the JapaneseEras as follows:
     * <pre>
     * for (JapaneseEra c : JapaneseEra.values())
     *     System.out.println(c);
     * </pre>
     *
     * @return an array of JapaneseEras
     */
    public static java.time.chrono.JapaneseEra[] values() {
        return Arrays.copyOf(KNOWN_ERAS, KNOWN_ERAS.length);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code JapaneseEra} from a date.
     *
     * @param date  the date, not null
     * @return the Era singleton, never null
     */
    static java.time.chrono.JapaneseEra from(LocalDate date) {
        if (date.isBefore(MEIJI_6_ISODATE)) {
            throw new DateTimeException("JapaneseDate before Meiji 6 are not supported");
        }
        for (int i = KNOWN_ERAS.length - 1; i > 0; i--) {
            java.time.chrono.JapaneseEra era = KNOWN_ERAS[i];
            if (date.compareTo(era.since) >= 0) {
                return era;
            }
        }
        return null;
    }

    static java.time.chrono.JapaneseEra toJapaneseEra(sun.util.calendar.Era privateEra) {
        for (int i = ERA_CONFIG.length - 1; i >= 0; i--) {
            if (ERA_CONFIG[i].equals(privateEra)) {
                return KNOWN_ERAS[i];
            }
        }
        return null;
    }

    static sun.util.calendar.Era privateEraFrom(LocalDate isoDate) {
        for (int i = KNOWN_ERAS.length - 1; i > 0; i--) {
            java.time.chrono.JapaneseEra era = KNOWN_ERAS[i];
            if (isoDate.compareTo(era.since) >= 0) {
                return ERA_CONFIG[i];
            }
        }
        return null;
    }

    /**
     * Returns the index into the arrays from the Era value.
     * the eraValue is a valid Era number, -1..2.
     *
     * @param eraValue  the era value to convert to the index
     * @return the index of the current Era
     */
    private static int ordinal(int eraValue) {
        return eraValue + ERA_OFFSET - 1;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the numeric era {@code int} value.
     * <p>
     * The {@link #SHOWA} era that contains 1970-01-01 (ISO calendar system) has the value 1.
     * Later eras are numbered from 2 ({@link #HEISEI}).
     * Earlier eras are numbered 0 ({@link #TAISHO}), -1 ({@link #MEIJI})).
     *
     * @return the era value
     */
    @Override
    public int getValue() {
        return eraValue;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the range of valid values for the specified field.
     * <p>
     * The range object expresses the minimum and maximum valid values for a field.
     * This era is used to enhance the accuracy of the returned range.
     * If it is not possible to return the range, because the field is not supported
     * or for some other reason, an exception is thrown.
     * <p>
     * If the field is a {@link ChronoField} then the query is implemented here.
     * The {@code ERA} field returns the range.
     * All other {@code ChronoField} instances will throw an {@code UnsupportedTemporalTypeException}.
     * <p>
     * If the field is not a {@code ChronoField}, then the result of this method
     * is obtained by invoking {@code TemporalField.rangeRefinedBy(TemporalAccessor)}
     * passing {@code this} as the argument.
     * Whether the range can be obtained is determined by the field.
     * <p>
     * The range of valid Japanese eras can change over time due to the nature
     * of the Japanese calendar system.
     *
     * @param field  the field to query the range for, not null
     * @return the range of valid values for the field, not null
     * @throws DateTimeException if the range for the field cannot be obtained
     * @throws UnsupportedTemporalTypeException if the unit is not supported
     */
    @Override  // override as super would return range from 0 to 1
    public ValueRange range(TemporalField field) {
        if (field == ERA) {
            return JapaneseChronology.INSTANCE.range(ERA);
        }
        return Era.super.range(field);
    }

    //-----------------------------------------------------------------------
    String getAbbreviation() {
        int index = ordinal(getValue());
        if (index == 0) {
            return "";
        }
        return ERA_CONFIG[index].getAbbreviation();
    }

    String getName() {
        return ERA_CONFIG[ordinal(getValue())].getName();
    }

    @Override
    public String toString() {
        return getName();
    }

    //-----------------------------------------------------------------------
    /**
     * Defend against malicious streams.
     *
     * @throws InvalidObjectException always
     */
    private void readObject(ObjectInputStream s) throws InvalidObjectException {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    //-----------------------------------------------------------------------
    /**
     * Writes the object using a
     * <a href="../../../serialized-form.html#java.time.chrono.Ser">dedicated serialized form</a>.
     * @serialData
     * <pre>
     *  out.writeByte(5);        // identifies a JapaneseEra
     *  out.writeInt(getValue());
     * </pre>
     *
     * @return the instance of {@code Ser}, not null
     */
    private Object writeReplace() {
        return new java.time.chrono.Ser(java.time.chrono.Ser.JAPANESE_ERA_TYPE, this);
    }

    void writeExternal(DataOutput out) throws IOException {
        out.writeByte(this.getValue());
    }

    static java.time.chrono.JapaneseEra readExternal(DataInput in) throws IOException {
        byte eraValue = in.readByte();
        return java.time.chrono.JapaneseEra.of(eraValue);
    }

}
