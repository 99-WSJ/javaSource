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
import javax.print.attribute.EnumSyntax;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.standard.JobStateReasons;
import javax.print.attribute.standard.PrinterState;
import javax.print.attribute.standard.PrinterStateReasons;

/**
 * JobState is a printing attribute class, an enumeration, that identifies
 * the current state of a print job. Class JobState defines standard job state
 * values. A  Print Service implementation only needs to report those job
 * states which are appropriate for the particular implementation; it does not
 * have to report every defined job state. The {@link JobStateReasons
 * JobStateReasons} attribute augments the JobState attribute to give more
 * detailed information about the job in the given job state.
 * <P>
 * <B>IPP Compatibility:</B> The category name returned by
 * <CODE>getName()</CODE> is the IPP attribute name.  The enumeration's
 * integer value is the IPP enum value.  The <code>toString()</code> method
 * returns the IPP string representation of the attribute value.
 * <P>
 *
 * @author  Alan Kaminsky
 */

public class JobState extends EnumSyntax implements PrintJobAttribute {

    private static final long serialVersionUID = 400465010094018920L;

    /**
     * The job state is unknown.
     */
    public static final javax.print.attribute.standard.JobState UNKNOWN = new javax.print.attribute.standard.JobState(0);

    /**
     * The job is a candidate to start processing, but is not yet processing.
     */
    public static final javax.print.attribute.standard.JobState PENDING = new javax.print.attribute.standard.JobState(3);

    /**
     * The job is not a candidate for processing for any number of reasons but
     * will return to the PENDING state as soon as the reasons are no longer
     * present. The job's {@link JobStateReasons JobStateReasons} attribute must
     * indicate why the job is no longer a candidate for processing.
     */
    public static final javax.print.attribute.standard.JobState PENDING_HELD = new javax.print.attribute.standard.JobState(4);

    /**
     * The job is processing. One or more of the following activities is
     * occurring:
     * <OL TYPE=1>
     * <LI>
     * The job is using, or is attempting to use, one or more purely software
     * processes that are analyzing, creating, or interpreting a PDL, etc.
     * <P>
     * <LI>
     * The job is using, or is attempting to use, one or more hardware
     * devices that are interpreting a PDL, making marks on a medium, and/or
     * performing finishing, such as stapling, etc.
     * <P>
     * <LI>
     * The printer has made the job ready for printing, but the output
     * device is not yet printing it, either because the job hasn't reached the
     * output device or because the job is queued in the output device or some
     * other spooler, awaiting the output device to print it.
     * </OL>
     * <P>
     * When the job is in the PROCESSING state, the entire job state includes
     * the detailed status represented in the printer's {@link PrinterState
     * PrinterState} and {@link PrinterStateReasons PrinterStateReasons}
     * attributes.
     * <P>
     * Implementations may, though they need not, include additional values in
     * the job's {@link JobStateReasons JobStateReasons} attribute to indicate
     * the progress of the job, such as adding the JOB_PRINTING value to
     * indicate when the output device is actually making marks on paper and/or
     * the PROCESSING_TO_STOP_POINT value to indicate that the printer is in the
     * process of canceling or aborting the job.
     */
    public static final javax.print.attribute.standard.JobState PROCESSING = new javax.print.attribute.standard.JobState(5);

    /**
     * The job has stopped while processing for any number of reasons and will
     * return to the PROCESSING state as soon as the reasons are no longer
     * present.
     * <P>
     * The job's {@link JobStateReasons JobStateReasons} attribute may indicate
     * why the job has stopped processing. For example, if the output device is
     * stopped, the PRINTER_STOPPED value may be included in the job's {@link
     * JobStateReasons JobStateReasons} attribute.
     * <P>
     * <I>Note:</I> When an output device is stopped, the device usually
     * indicates its condition in human readable form locally at the device. A
     * client can obtain more complete device status remotely by querying the
     * printer's {@link PrinterState PrinterState} and {@link
     * PrinterStateReasons PrinterStateReasons} attributes.
     */
    public static final javax.print.attribute.standard.JobState PROCESSING_STOPPED = new javax.print.attribute.standard.JobState(6);

    /**
     * The job has been canceled by some human agency, the printer has completed
     * canceling the job, and all job status attributes have reached their final
     * values for the job. While the printer is canceling the job, the job
     * remains in its current state, but the job's {@link JobStateReasons
     * JobStateReasons} attribute should contain the PROCESSING_TO_STOP_POINT
     * value and one of the CANCELED_BY_USER, CANCELED_BY_OPERATOR, or
     * CANCELED_AT_DEVICE values. When the job moves to the CANCELED state, the
     * PROCESSING_TO_STOP_POINT value, if present, must be removed, but the
     * CANCELED_BY_<I>xxx</I> value, if present, must remain.
     */
    public static final javax.print.attribute.standard.JobState CANCELED = new javax.print.attribute.standard.JobState(7);

    /**
     * The job has been aborted by the system (usually while the job was in the
     * PROCESSING or PROCESSING_STOPPED state), the printer has completed
     * aborting the job, and all job status attributes have reached their final
     * values for the job. While the printer is aborting the job, the job
     * remains in its current state, but the job's {@link JobStateReasons
     * JobStateReasons} attribute should contain the PROCESSING_TO_STOP_POINT
     * and ABORTED_BY_SYSTEM values. When the job moves to the ABORTED state,
     * the PROCESSING_TO_STOP_POINT value, if present, must be removed, but the
     * ABORTED_BY_SYSTEM value, if present, must remain.
     */
    public static final javax.print.attribute.standard.JobState ABORTED = new javax.print.attribute.standard.JobState(8);

    /**
     * The job has completed successfully or with warnings or errors after
     * processing, all of the job media sheets have been successfully stacked in
     * the appropriate output bin(s), and all job status attributes have reached
     * their final values for the job. The job's {@link JobStateReasons
     * JobStateReasons} attribute should contain one of these values:
     * COMPLETED_SUCCESSFULLY, COMPLETED_WITH_WARNINGS, or
     * COMPLETED_WITH_ERRORS.
     */
    public static final javax.print.attribute.standard.JobState COMPLETED = new javax.print.attribute.standard.JobState(9);

    // Hidden constructors.

    /**
     * Construct a new job state enumeration value with the given integer value.
     *
     * @param  value  Integer value.
     */
    protected JobState(int value) {
        super (value);
    }

    private static final String[] myStringTable =
    {"unknown",
     null,
     null,
     "pending",
     "pending-held",
     "processing",
     "processing-stopped",
     "canceled",
     "aborted",
     "completed"};

    private static final javax.print.attribute.standard.JobState[] myEnumValueTable =
    {UNKNOWN,
     null,
     null,
     PENDING,
     PENDING_HELD,
     PROCESSING,
     PROCESSING_STOPPED,
     CANCELED,
     ABORTED,
     COMPLETED};

    /**
     * Returns the string table for class JobState.
     */
    protected String[] getStringTable() {
        return myStringTable;
    }

    /**
     * Returns the enumeration value table for class JobState.
     */
    protected EnumSyntax[] getEnumValueTable() {
        return myEnumValueTable;
    }

    /**
     * Get the printing attribute class which is to be used as the "category"
     * for this printing attribute value.
     * <P>
     * For class JobState and any vendor-defined subclasses, the category is
     * class JobState itself.
     *
     * @return  Printing attribute class (category), an instance of class
     *          {@link Class java.lang.Class}.
     */
    public final Class<? extends Attribute> getCategory() {
        return javax.print.attribute.standard.JobState.class;
    }

    /**
     * Get the name of the category of which this attribute value is an
     * instance.
     * <P>
     * For class JobState and any vendor-defined subclasses, the category
     * name is <CODE>"job-state"</CODE>.
     *
     * @return  Attribute category name.
     */
    public final String getName() {
        return "job-state";
    }

}
