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
import javax.print.attribute.standard.*;
import javax.print.attribute.standard.JobKOctets;
import javax.print.attribute.standard.JobKOctetsSupported;
import javax.print.attribute.standard.JobState;

/**
 * Class JobKOctetsProcessed is an integer valued printing attribute class that
 * specifies the total number of print data octets processed so far in K octets,
 * i.e., in units of 1024 octets. The value must be rounded up, so that a job
 * between 1 and 1024 octets inclusive must be indicated as being 1K octets,
 * 1025 to 2048 inclusive must be 2K, etc. For a multidoc print job (a job with
 * multiple documents), the JobKOctetsProcessed value is computed by adding up
 * the individual documents' number of octets processed so far, then rounding up
 * to the next K octets value.
 * <P>
 * The JobKOctetsProcessed attribute describes the progress of the job. This
 * attribute is intended to be a counter. That is, the JobKOctetsProcessed value
 * for a job that has not started processing must be 0. When the job's {@link
 * JobState JobState} is PROCESSING or PROCESSING_STOPPED, the
 * JobKOctetsProcessed value is intended to increase as the job is processed; it
 * indicates the amount of the job that has been processed at the time the Print
 * Job's attribute set is queried or at the time a print job event is reported.
 * When the job enters the COMPLETED, CANCELED, or ABORTED states, the
 * JobKOctetsProcessed value is the final value for the job.
 * <P>
 * For implementations where multiple copies are produced by the interpreter
 * with only a single pass over the data, the final value of the
 * JobKOctetsProcessed attribute must be equal to the value of the {@link
 * JobKOctets JobKOctets} attribute. For implementations where multiple copies
 * are produced by the interpreter by processing the data for each copy, the
 * final value must be a multiple of the value of the {@link JobKOctets
 * JobKOctets} attribute.
 * <P>
 * <B>IPP Compatibility:</B> The integer value gives the IPP integer value. The
 * category name returned by <CODE>getName()</CODE> gives the IPP attribute
 * name.
 * <P>
 *
 * @see JobKOctets
 * @see JobKOctetsSupported
 * @see JobImpressionsCompleted
 * @see JobMediaSheetsCompleted
 *
 * @author  Alan Kaminsky
 */
public final class JobKOctetsProcessed extends IntegerSyntax
        implements PrintJobAttribute {

    private static final long serialVersionUID = -6265238509657881806L;

    /**
     * Construct a new job K octets processed attribute with the given integer
     * value.
     *
     * @param  value  Integer value.
     *
     * @exception  IllegalArgumentException
     *  (Unchecked exception) Thrown if <CODE>value</CODE> is less than 0.
     */
    public JobKOctetsProcessed(int value) {
        super (value, 0, Integer.MAX_VALUE);
    }

    /**
     * Returns whether this job K octets processed attribute is equivalent to
     * the passed in object. To be equivalent, all of the following conditions
     * must be true:
     * <OL TYPE=1>
     * <LI>
     * <CODE>object</CODE> is not null.
     * <LI>
     * <CODE>object</CODE> is an instance of class JobKOctetsProcessed.
     * <LI>
     * This job K octets processed attribute's value and
     * <CODE>object</CODE>'s value are equal.
     * </OL>
     *
     * @param  object  Object to compare to.
     *
     * @return  True if <CODE>object</CODE> is equivalent to this job K
     *          octets processed attribute, false otherwise.
     */
    public boolean equals(Object object) {
        return(super.equals (object) &&
               object instanceof javax.print.attribute.standard.JobKOctetsProcessed);
    }

    /**
     * Get the printing attribute class which is to be used as the "category"
     * for this printing attribute value.
     * <P>
     * For class JobKOctetsProcessed, the category is class
     * JobKOctetsProcessed itself.
     *
     * @return  Printing attribute class (category), an instance of class
     *          {@link Class java.lang.Class}.
     */
    public final Class<? extends Attribute> getCategory() {
        return javax.print.attribute.standard.JobKOctetsProcessed.class;
    }

    /**
     * Get the name of the category of which this attribute value is an
     * instance.
     * <P>
     * For class JobKOctetsProcessed, the category name is
     * <CODE>"job-k-octets-processed"</CODE>.
     *
     * @return  Attribute category name.
     */
    public final String getName() {
        return "job-k-octets-processed";
    }

}
