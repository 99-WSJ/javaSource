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

import javax.print.attribute.Attribute;
import javax.print.attribute.IntegerSyntax;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.standard.JobImpressions;
import javax.print.attribute.standard.JobImpressionsSupported;
import javax.print.attribute.standard.JobKOctetsProcessed;
import javax.print.attribute.standard.JobMediaSheetsCompleted;
import javax.print.attribute.standard.JobState;

/**
 * Class JobImpressionsCompleted is an integer valued printing attribute class
 * that specifies the number of impressions completed for the job so far. For
 * printing devices, the impressions completed includes interpreting, marking,
 * and stacking the output.
 * <P>
 * The JobImpressionsCompleted attribute describes the progress of the job. This
 * attribute is intended to be a counter. That is, the JobImpressionsCompleted
 * value for a job that has not started processing must be 0. When the job's
 * {@link JobState JobState} is PROCESSING or PROCESSING_STOPPED, the
 * JobImpressionsCompleted value is intended to increase as the job is
 * processed; it indicates the amount of the job that has been processed at the
 * time the Print Job's attribute set is queried or at the time a print job
 * event is reported. When the job enters the COMPLETED, CANCELED, or ABORTED
 * states, the JobImpressionsCompleted value is the final value for the job.
 * <P>
 * <B>IPP Compatibility:</B> The integer value gives the IPP integer value. The
 * category name returned by <CODE>getName()</CODE> gives the IPP attribute
 * name.
 * <P>
 *
 * @see JobImpressions
 * @see JobImpressionsSupported
 * @see JobKOctetsProcessed
 * @see JobMediaSheetsCompleted
 *
 * @author  Alan Kaminsky
 */
public final class JobImpressionsCompleted extends IntegerSyntax
        implements PrintJobAttribute {

    private static final long serialVersionUID = 6722648442432393294L;

    /**
     * Construct a new job impressions completed attribute with the given
     * integer value.
     *
     * @param  value  Integer value.
     *
     * @exception  IllegalArgumentException
     *  (Unchecked exception) Thrown if <CODE>value</CODE> is less than 0.
     */
    public JobImpressionsCompleted(int value) {
        super (value, 0, Integer.MAX_VALUE);
    }

    /**
     * Returns whether this job impressions completed attribute is equivalent
     * tp the passed in object. To be equivalent, all of the following
     * conditions must be true:
     * <OL TYPE=1>
     * <LI>
     * <CODE>object</CODE> is not null.
     * <LI>
     * <CODE>object</CODE> is an instance of class JobImpressionsCompleted.
     * <LI>
     * This job impressions completed attribute's value and
     * <CODE>object</CODE>'s value are equal.
     * </OL>
     *
     * @param  object  Object to compare to.
     *
     * @return  True if <CODE>object</CODE> is equivalent to this job
     *          impressions completed attribute, false otherwise.
     */
    public boolean equals(Object object) {
        return(super.equals (object) &&
               object instanceof javax.print.attribute.standard.JobImpressionsCompleted);
    }

    /**
     * Get the printing attribute class which is to be used as the "category"
     * for this printing attribute value.
     * <P>
     * For class JobImpressionsCompleted, the category is class
     * JobImpressionsCompleted itself.
     *
     * @return  Printing attribute class (category), an instance of class
     *          {@link Class java.lang.Class}.
     */
    public final Class<? extends Attribute> getCategory() {
        return javax.print.attribute.standard.JobImpressionsCompleted.class;
    }

    /**
     * Get the name of the category of which this attribute value is an
     * instance.
     * <P>
     * For class JobImpressionsCompleted, the category name is
     * <CODE>"job-impressions-completed"</CODE>.
     *
     * @return  Attribute category name.
     */
    public final String getName() {
        return "job-impressions-completed";
    }

}
