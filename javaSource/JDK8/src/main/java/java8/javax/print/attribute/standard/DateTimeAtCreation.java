/*
 * Copyright (c) 2000, 2004, Oracle and/or its affiliates. All rights reserved.
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
package java8.javax.print.attribute.standard;

import java.util.Date;
import javax.print.attribute.Attribute;
import javax.print.attribute.DateTimeSyntax;
import javax.print.attribute.PrintJobAttribute;

/**
 * Class DateTimeAtCreation is a printing attribute class, a date-time
 * attribute, that indicates the date and time at which the Print Job was
 * created.
 * <P>
 * To construct a DateTimeAtCreation attribute from separate values of the year,
 * month, day, hour, minute, and so on, use a {@link java.util.Calendar
 * Calendar} object to construct a {@link Date Date} object, then use
 * the {@link Date Date} object to construct the DateTimeAtCreation
 * attribute. To convert a DateTimeAtCreation attribute to separate values of
 * the year, month, day, hour, minute, and so on, create a {@link
 * java.util.Calendar Calendar} object and set it to the {@link Date
 * Date} from the DateTimeAtCreation attribute.
 * <P>
 * <B>IPP Compatibility:</B> The information needed to construct an IPP
 * "date-time-at-creation" attribute can be obtained as described above. The
 * category name returned by <CODE>getName()</CODE> gives the IPP attribute
 * name.
 * <P>
 *
 * @author  Alan Kaminsky
 */
public final class DateTimeAtCreation   extends DateTimeSyntax
        implements PrintJobAttribute {

    private static final long serialVersionUID = -2923732231056647903L;

    /**
     * Construct a new date-time at creation attribute with the given {@link
     * Date Date} value.
     *
     * @param  dateTime  {@link Date Date} value.
     *
     * @exception  NullPointerException
     *     (unchecked exception) Thrown if <CODE>dateTime</CODE> is null.
     */
    public DateTimeAtCreation(Date dateTime) {
        super (dateTime);
    }

    /**
     * Returns whether this date-time at creation attribute is equivalent to
     * the passed in object. To be equivalent, all of the following conditions
     * must be true:
     * <OL TYPE=1>
     * <LI>
     * <CODE>object</CODE> is not null.
     * <LI>
     * <CODE>object</CODE> is an instance of class DateTimeAtCreation.
     * <LI>
     * This date-time at creation attribute's {@link Date Date} value
     * and <CODE>object</CODE>'s {@link Date Date} value are equal.
     * </OL>
     *
     * @param  object  Object to compare to.
     *
     * @return  True if <CODE>object</CODE> is equivalent to this date-time
     *          at creation attribute, false otherwise.
     */
    public boolean equals(Object object) {
        return(super.equals (object) &&
               object instanceof javax.print.attribute.standard.DateTimeAtCreation);
    }

    /**
     * Get the printing attribute class which is to be used as the "category"
     * for this printing attribute value.
     * <P>
     * For class DateTimeAtCreation, the category is class
     * DateTimeAtCreation itself.
     *
     * @return  Printing attribute class (category), an instance of class
     *          {@link Class java.lang.Class}.
     */
    public final Class<? extends Attribute> getCategory() {
        return javax.print.attribute.standard.DateTimeAtCreation.class;
    }

    /**
     * Get the name of the category of which this attribute value is an
     * instance.
     * <P>
     * For class DateTimeAtCreation, the category name is
     * <CODE>"date-time-at-creation"</CODE>.
     *
     * @return  Attribute category name.
     */
    public final String getName() {
        return "date-time-at-creation";
    }

}
