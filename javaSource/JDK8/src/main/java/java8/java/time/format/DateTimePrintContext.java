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
 * Copyright (c) 2011-2012, Stephen Colebourne & Michael Nascimento Santos
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

import java.time.DateTimeException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.Chronology;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DecimalStyle;
import java.time.temporal.*;
import java.util.Locale;
import java.util.Objects;

import static java.time.temporal.ChronoField.*;

/**
 * Context object used during date and time printing.
 * <p>
 * This class provides a single wrapper to items used in the format.
 *
 * @implSpec
 * This class is a mutable context intended for use from a single thread.
 * Usage of the class is thread-safe within standard printing as the framework creates
 * a new instance of the class for each format and printing is single-threaded.
 *
 * @since 1.8
 */
final class DateTimePrintContext {

    /**
     * The temporal being output.
     */
    private TemporalAccessor temporal;
    /**
     * The formatter, not null.
     */
    private DateTimeFormatter formatter;
    /**
     * Whether the current formatter is optional.
     */
    private int optional;

    /**
     * Creates a new instance of the context.
     *
     * @param temporal  the temporal object being output, not null
     * @param formatter  the formatter controlling the format, not null
     */
    DateTimePrintContext(TemporalAccessor temporal, DateTimeFormatter formatter) {
        super();
        this.temporal = adjust(temporal, formatter);
        this.formatter = formatter;
    }

    private static TemporalAccessor adjust(final TemporalAccessor temporal, DateTimeFormatter formatter) {
        // normal case first (early return is an optimization)
        Chronology overrideChrono = formatter.getChronology();
        ZoneId overrideZone = formatter.getZone();
        if (overrideChrono == null && overrideZone == null) {
            return temporal;
        }

        // ensure minimal change (early return is an optimization)
        Chronology temporalChrono = temporal.query(TemporalQueries.chronology());
        ZoneId temporalZone = temporal.query(TemporalQueries.zoneId());
        if (Objects.equals(overrideChrono, temporalChrono)) {
            overrideChrono = null;
        }
        if (Objects.equals(overrideZone, temporalZone)) {
            overrideZone = null;
        }
        if (overrideChrono == null && overrideZone == null) {
            return temporal;
        }

        // make adjustment
        final Chronology effectiveChrono = (overrideChrono != null ? overrideChrono : temporalChrono);
        if (overrideZone != null) {
            // if have zone and instant, calculation is simple, defaulting chrono if necessary
            if (temporal.isSupported(INSTANT_SECONDS)) {
                Chronology chrono = (effectiveChrono != null ? effectiveChrono : IsoChronology.INSTANCE);
                return chrono.zonedDateTime(Instant.from(temporal), overrideZone);
            }
            // block changing zone on OffsetTime, and similar problem cases
            if (overrideZone.normalized() instanceof ZoneOffset && temporal.isSupported(OFFSET_SECONDS) &&
                    temporal.get(OFFSET_SECONDS) != overrideZone.getRules().getOffset(Instant.EPOCH).getTotalSeconds()) {
                throw new DateTimeException("Unable to apply override zone '" + overrideZone +
                        "' because the temporal object being formatted has a different offset but" +
                        " does not represent an instant: " + temporal);
            }
        }
        final ZoneId effectiveZone = (overrideZone != null ? overrideZone : temporalZone);
        final ChronoLocalDate effectiveDate;
        if (overrideChrono != null) {
            if (temporal.isSupported(EPOCH_DAY)) {
                effectiveDate = effectiveChrono.date(temporal);
            } else {
                // check for date fields other than epoch-day, ignoring case of converting null to ISO
                if (!(overrideChrono == IsoChronology.INSTANCE && temporalChrono == null)) {
                    for (ChronoField f : ChronoField.values()) {
                        if (f.isDateBased() && temporal.isSupported(f)) {
                            throw new DateTimeException("Unable to apply override chronology '" + overrideChrono +
                                    "' because the temporal object being formatted contains date fields but" +
                                    " does not represent a whole date: " + temporal);
                        }
                    }
                }
                effectiveDate = null;
            }
        } else {
            effectiveDate = null;
        }

        // combine available data
        // this is a non-standard temporal that is almost a pure delegate
        // this better handles map-like underlying temporal instances
        return new TemporalAccessor() {
            @Override
            public boolean isSupported(TemporalField field) {
                if (effectiveDate != null && field.isDateBased()) {
                    return effectiveDate.isSupported(field);
                }
                return temporal.isSupported(field);
            }
            @Override
            public ValueRange range(TemporalField field) {
                if (effectiveDate != null && field.isDateBased()) {
                    return effectiveDate.range(field);
                }
                return temporal.range(field);
            }
            @Override
            public long getLong(TemporalField field) {
                if (effectiveDate != null && field.isDateBased()) {
                    return effectiveDate.getLong(field);
                }
                return temporal.getLong(field);
            }
            @SuppressWarnings("unchecked")
            @Override
            public <R> R query(TemporalQuery<R> query) {
                if (query == TemporalQueries.chronology()) {
                    return (R) effectiveChrono;
                }
                if (query == TemporalQueries.zoneId()) {
                    return (R) effectiveZone;
                }
                if (query == TemporalQueries.precision()) {
                    return temporal.query(query);
                }
                return query.queryFrom(this);
            }
        };
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the temporal object being output.
     *
     * @return the temporal object, not null
     */
    TemporalAccessor getTemporal() {
        return temporal;
    }

    /**
     * Gets the locale.
     * <p>
     * This locale is used to control localization in the format output except
     * where localization is controlled by the DecimalStyle.
     *
     * @return the locale, not null
     */
    Locale getLocale() {
        return formatter.getLocale();
    }

    /**
     * Gets the DecimalStyle.
     * <p>
     * The DecimalStyle controls the localization of numeric output.
     *
     * @return the DecimalStyle, not null
     */
    DecimalStyle getDecimalStyle() {
        return formatter.getDecimalStyle();
    }

    //-----------------------------------------------------------------------
    /**
     * Starts the printing of an optional segment of the input.
     */
    void startOptional() {
        this.optional++;
    }

    /**
     * Ends the printing of an optional segment of the input.
     */
    void endOptional() {
        this.optional--;
    }

    /**
     * Gets a value using a query.
     *
     * @param query  the query to use, not null
     * @return the result, null if not found and optional is true
     * @throws DateTimeException if the type is not available and the section is not optional
     */
    <R> R getValue(TemporalQuery<R> query) {
        R result = temporal.query(query);
        if (result == null && optional == 0) {
            throw new DateTimeException("Unable to extract value: " + temporal.getClass());
        }
        return result;
    }

    /**
     * Gets the value of the specified field.
     * <p>
     * This will return the value for the specified field.
     *
     * @param field  the field to find, not null
     * @return the value, null if not found and optional is true
     * @throws DateTimeException if the field is not available and the section is not optional
     */
    Long getValue(TemporalField field) {
        try {
            return temporal.getLong(field);
        } catch (DateTimeException ex) {
            if (optional > 0) {
                return null;
            }
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a string version of the context for debugging.
     *
     * @return a string representation of the context, not null
     */
    @Override
    public String toString() {
        return temporal.toString();
    }

}
