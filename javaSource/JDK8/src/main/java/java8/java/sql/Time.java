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

package java8.java.sql;

import java.time.Instant;
import java.time.LocalTime;

/**
 * <P>A thin wrapper around the <code>java.util.Date</code> class that allows the JDBC
 * API to identify this as an SQL <code>TIME</code> value. The <code>Time</code>
 * class adds formatting and
 * parsing operations to support the JDBC escape syntax for time
 * values.
 * <p>The date components should be set to the "zero epoch"
 * value of January 1, 1970 and should not be accessed.
 */
public class Time extends java.util.Date {

    /**
     * Constructs a <code>Time</code> object initialized with the
     * given values for the hour, minute, and second.
     * The driver sets the date components to January 1, 1970.
     * Any method that attempts to access the date components of a
     * <code>Time</code> object will throw a
     * <code>java.lang.IllegalArgumentException</code>.
     * <P>
     * The result is undefined if a given argument is out of bounds.
     *
     * @param hour 0 to 23
     * @param minute 0 to 59
     * @param second 0 to 59
     *
     * @deprecated Use the constructor that takes a milliseconds value
     *             in place of this constructor
     */
    @Deprecated
    public Time(int hour, int minute, int second) {
        super(70, 0, 1, hour, minute, second);
    }

    /**
     * Constructs a <code>Time</code> object using a milliseconds time value.
     *
     * @param time milliseconds since January 1, 1970, 00:00:00 GMT;
     *             a negative number is milliseconds before
     *               January 1, 1970, 00:00:00 GMT
     */
    public Time(long time) {
        super(time);
    }

    /**
     * Sets a <code>Time</code> object using a milliseconds time value.
     *
     * @param time milliseconds since January 1, 1970, 00:00:00 GMT;
     *             a negative number is milliseconds before
     *               January 1, 1970, 00:00:00 GMT
     */
    public void setTime(long time) {
        super.setTime(time);
    }

    /**
     * Converts a string in JDBC time escape format to a <code>Time</code> value.
     *
     * @param s time in format "hh:mm:ss"
     * @return a corresponding <code>Time</code> object
     */
    public static java.sql.Time valueOf(String s) {
        int hour;
        int minute;
        int second;
        int firstColon;
        int secondColon;

        if (s == null) throw new IllegalArgumentException();

        firstColon = s.indexOf(':');
        secondColon = s.indexOf(':', firstColon+1);
        if ((firstColon > 0) & (secondColon > 0) &
            (secondColon < s.length()-1)) {
            hour = Integer.parseInt(s.substring(0, firstColon));
            minute =
                Integer.parseInt(s.substring(firstColon+1, secondColon));
            second = Integer.parseInt(s.substring(secondColon+1));
        } else {
            throw new IllegalArgumentException();
        }

        return new java.sql.Time(hour, minute, second);
    }

    /**
     * Formats a time in JDBC time escape format.
     *
     * @return a <code>String</code> in hh:mm:ss format
     */
    @SuppressWarnings("deprecation")
    public String toString () {
        int hour = super.getHours();
        int minute = super.getMinutes();
        int second = super.getSeconds();
        String hourString;
        String minuteString;
        String secondString;

        if (hour < 10) {
            hourString = "0" + hour;
        } else {
            hourString = Integer.toString(hour);
        }
        if (minute < 10) {
            minuteString = "0" + minute;
        } else {
            minuteString = Integer.toString(minute);
        }
        if (second < 10) {
            secondString = "0" + second;
        } else {
            secondString = Integer.toString(second);
        }
        return (hourString + ":" + minuteString + ":" + secondString);
    }

    // Override all the date operations inherited from java.util.Date;

   /**
    * This method is deprecated and should not be used because SQL <code>TIME</code>
    * values do not have a year component.
    *
    * @deprecated
    * @exception IllegalArgumentException if this
    *           method is invoked
    * @see #setYear
    */
    @Deprecated
    public int getYear() {
        throw new IllegalArgumentException();
    }

   /**
    * This method is deprecated and should not be used because SQL <code>TIME</code>
    * values do not have a month component.
    *
    * @deprecated
    * @exception IllegalArgumentException if this
    *           method is invoked
    * @see #setMonth
    */
    @Deprecated
    public int getMonth() {
        throw new IllegalArgumentException();
    }

   /**
    * This method is deprecated and should not be used because SQL <code>TIME</code>
    * values do not have a day component.
    *
    * @deprecated
    * @exception IllegalArgumentException if this
    *           method is invoked
    */
    @Deprecated
    public int getDay() {
        throw new IllegalArgumentException();
    }

   /**
    * This method is deprecated and should not be used because SQL <code>TIME</code>
    * values do not have a date component.
    *
    * @deprecated
    * @exception IllegalArgumentException if this
    *           method is invoked
    * @see #setDate
    */
    @Deprecated
    public int getDate() {
        throw new IllegalArgumentException();
    }

   /**
    * This method is deprecated and should not be used because SQL <code>TIME</code>
    * values do not have a year component.
    *
    * @deprecated
    * @exception IllegalArgumentException if this
    *           method is invoked
    * @see #getYear
    */
    @Deprecated
    public void setYear(int i) {
        throw new IllegalArgumentException();
    }

   /**
    * This method is deprecated and should not be used because SQL <code>TIME</code>
    * values do not have a month component.
    *
    * @deprecated
    * @exception IllegalArgumentException if this
    *           method is invoked
    * @see #getMonth
    */
    @Deprecated
    public void setMonth(int i) {
        throw new IllegalArgumentException();
    }

   /**
    * This method is deprecated and should not be used because SQL <code>TIME</code>
    * values do not have a date component.
    *
    * @deprecated
    * @exception IllegalArgumentException if this
    *           method is invoked
    * @see #getDate
    */
    @Deprecated
    public void setDate(int i) {
        throw new IllegalArgumentException();
    }

   /**
    * Private serial version unique ID to ensure serialization
    * compatibility.
    */
    static final long serialVersionUID = 8397324403548013681L;

    /**
     * Obtains an instance of {@code Time} from a {@link LocalTime} object
     * with the same hour, minute and second time value as the given
     * {@code LocalTime}.
     *
     * @param time a {@code LocalTime} to convert
     * @return a {@code Time} object
     * @exception NullPointerException if {@code time} is null
     * @since 1.8
     */
    @SuppressWarnings("deprecation")
    public static java.sql.Time valueOf(LocalTime time) {
        return new java.sql.Time(time.getHour(), time.getMinute(), time.getSecond());
    }

    /**
     * Converts this {@code Time} object to a {@code LocalTime}.
     * <p>
     * The conversion creates a {@code LocalTime} that represents the same
     * hour, minute, and second time value as this {@code Time}.
     *
     * @return a {@code LocalTime} object representing the same time value
     * @since 1.8
     */
    @SuppressWarnings("deprecation")
    public LocalTime toLocalTime() {
        return LocalTime.of(getHours(), getMinutes(), getSeconds());
    }

   /**
    * This method always throws an UnsupportedOperationException and should
    * not be used because SQL {@code Time} values do not have a date
    * component.
    *
    * @exception UnsupportedOperationException if this method is invoked
    */
    @Override
    public Instant toInstant() {
        throw new UnsupportedOperationException();
    }
}
