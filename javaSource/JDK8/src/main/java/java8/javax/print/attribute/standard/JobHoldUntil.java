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
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.standard.JobStateReason;
import javax.print.attribute.standard.JobStateReasons;

/**
 * Class JobHoldUntil is a printing attribute class, a date-time attribute, that
 * specifies the exact date and time at which the job must become a candidate
 * for printing.
 * <P>
 * If the value of this attribute specifies a date-time that is in the future,
 * the printer should add the {@link JobStateReason JobStateReason} value of
 * JOB_HOLD_UNTIL_SPECIFIED to the job's {@link JobStateReasons JobStateReasons}
 * attribute, must move the job to the PENDING_HELD state, and must not schedule
 * the job for printing until the specified date-time arrives.
 * <P>
 * When the specified date-time arrives, the printer must remove the {@link
 * JobStateReason JobStateReason} value of JOB_HOLD_UNTIL_SPECIFIED from the
 * job's {@link JobStateReasons JobStateReasons} attribute, if present. If there
 * are no other job state reasons that keep the job in the PENDING_HELD state,
 * the printer must consider the job as a candidate for processing by moving the
 * job to the PENDING state.
 * <P>
 * If the specified date-time has already passed, the job must be a candidate
 * for processing immediately. Thus, one way to make the job immediately become
 * a candidate for processing is to specify a JobHoldUntil attribute constructed
 * like this (denoting a date-time of January 1, 1970, 00:00:00 GMT):
 * <PRE>
 *     JobHoldUntil immediately = new JobHoldUntil (new Date (0L));
 * </PRE>
 * <P>
 * If the client does not supply this attribute in a Print Request and the
 * printer supports this attribute, the printer must use its
 * (implementation-dependent) default JobHoldUntil value at job submission time
 * (unlike most job template attributes that are used if necessary at job
 * processing time).
 * <P>
 * To construct a JobHoldUntil attribute from separate values of the year,
 * month, day, hour, minute, and so on, use a {@link java.util.Calendar
 * Calendar} object to construct a {@link Date Date} object, then use
 * the {@link Date Date} object to construct the JobHoldUntil
 * attribute. To convert a JobHoldUntil attribute to separate values of the
 * year, month, day, hour, minute, and so on, create a {@link java.util.Calendar
 * Calendar} object and set it to the {@link Date Date} from the
 * JobHoldUntil attribute.
 * <P>
 * <B>IPP Compatibility:</B> Although IPP supports a "job-hold-until" attribute
 * specified as a keyword, IPP does not at this time support a "job-hold-until"
 * attribute specified as a date and time. However, the date and time can be
 * converted to one of the standard IPP keywords with some loss of precision;
 * for example, a JobHoldUntil value with today's date and 9:00pm local time
 * might be converted to the standard IPP keyword "night". The category name
 * returned by <CODE>getName()</CODE> gives the IPP attribute name.
 * <P>
 *
 * @author  Alan Kaminsky
 */
public final class JobHoldUntil extends DateTimeSyntax
        implements PrintRequestAttribute, PrintJobAttribute {

    private static final long serialVersionUID = -1664471048860415024L;


    /**
     * Construct a new job hold until date-time attribute with the given
     * {@link Date Date} value.
     *
     * @param  dateTime  {@link Date Date} value.
     *
     * @exception  NullPointerException
     *     (unchecked exception) Thrown if <CODE>dateTime</CODE> is null.
     */
    public JobHoldUntil(Date dateTime) {
        super (dateTime);
    }

    /**
     * Returns whether this job hold until attribute is equivalent to the
     * passed in object. To be equivalent, all of the following conditions
     * must be true:
     * <OL TYPE=1>
     * <LI>
     * <CODE>object</CODE> is not null.
     * <LI>
     * <CODE>object</CODE> is an instance of class JobHoldUntil.
     * <LI>
     * This job hold until attribute's {@link Date Date} value and
     * <CODE>object</CODE>'s {@link Date Date} value are equal.
     * </OL>
     *
     * @param  object  Object to compare to.
     *
     * @return  True if <CODE>object</CODE> is equivalent to this job hold
     *          until attribute, false otherwise.
     */
    public boolean equals(Object object) {
        return (super.equals(object) && object instanceof javax.print.attribute.standard.JobHoldUntil);
    }


    /**
     * Get the printing attribute class which is to be used as the "category"
     * for this printing attribute value.
     * <P>
     * For class JobHoldUntil, the category is class JobHoldUntil itself.
     *
     * @return  Printing attribute class (category), an instance of class
     *          {@link Class java.lang.Class}.
     */
    public final Class<? extends Attribute> getCategory() {
        return javax.print.attribute.standard.JobHoldUntil.class;
    }

    /**
     * Get the name of the category of which this attribute value is an
     * instance.
     * <P>
     * For class JobHoldUntil, the category name is <CODE>"job-hold-until"</CODE>.
     *
     * @return  Attribute category name.
     */
    public final String getName() {
        return "job-hold-until";
    }

}
