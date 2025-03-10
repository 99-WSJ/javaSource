/*
 * Copyright (c) 2000, 2013, Oracle and/or its affiliates. All rights reserved.
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
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.standard.PrinterState;
import javax.print.attribute.standard.PrinterStateReasons;

/**
 * Class PrinterIsAcceptingJobs is a printing attribute class, an enumeration,
 * that indicates whether the printer is currently able to accept jobs. This
 * value is independent of the {@link PrinterState PrinterState} and {@link
 * PrinterStateReasons PrinterStateReasons} attributes because its value does
 * not affect the current job; rather it affects future jobs. If the value is
 * NOT_ACCEPTING_JOBS, the printer will reject jobs even when the {@link
 * PrinterState PrinterState} is IDLE. If value is ACCEPTING_JOBS, the Printer
 * will accept jobs even when the {@link PrinterState PrinterState} is STOPPED.
 * <P>
 * <B>IPP Compatibility:</B> The IPP boolean value is "true" for ACCEPTING_JOBS
 * and "false" for NOT_ACCEPTING_JOBS. The category name returned by
 * <CODE>getName()</CODE> is the IPP attribute name.  The enumeration's
 * integer value is the IPP enum value.  The <code>toString()</code> method
 * returns the IPP string representation of the attribute value.
 * <P>
 *
 * @author  Alan Kaminsky
 */
public final class PrinterIsAcceptingJobs extends EnumSyntax
        implements PrintServiceAttribute {

    private static final long serialVersionUID = -5052010680537678061L;

    /**
     * The printer is currently rejecting any jobs sent to it.
     */
    public static final javax.print.attribute.standard.PrinterIsAcceptingJobs
        NOT_ACCEPTING_JOBS = new javax.print.attribute.standard.PrinterIsAcceptingJobs(0);

    /**
     * The printer is currently accepting jobs.
     */
    public static final javax.print.attribute.standard.PrinterIsAcceptingJobs
        ACCEPTING_JOBS = new javax.print.attribute.standard.PrinterIsAcceptingJobs(1);

    /**
     * Construct a new printer is accepting jobs enumeration value with the
     * given integer value.
     *
     * @param  value  Integer value.
     */
    protected PrinterIsAcceptingJobs(int value) {
        super (value);
    }

    private static final String[] myStringTable = {
        "not-accepting-jobs",
        "accepting-jobs"
    };

    private static final javax.print.attribute.standard.PrinterIsAcceptingJobs[] myEnumValueTable = {
        NOT_ACCEPTING_JOBS,
        ACCEPTING_JOBS
    };

    /**
     * Returns the string table for class PrinterIsAcceptingJobs.
     */
    protected String[] getStringTable() {
        return myStringTable;
    }

    /**
     * Returns the enumeration value table for class PrinterIsAcceptingJobs.
     */
    protected EnumSyntax[] getEnumValueTable() {
        return myEnumValueTable;
    }

    /**
     * Get the printing attribute class which is to be used as the "category"
     * for this printing attribute value.
     * <P>
     * For class PrinterIsAcceptingJobs, the
     * category is class PrinterIsAcceptingJobs itself.
     *
     * @return  Printing attribute class (category), an instance of class
     *          {@link Class java.lang.Class}.
     */
    public final Class<? extends Attribute> getCategory() {
        return javax.print.attribute.standard.PrinterIsAcceptingJobs.class;
    }

    /**
     * Get the name of the category of which this attribute value is an
     * instance.
     * <P>
     * For class PrinterIsAcceptingJobs, the
     * category name is <CODE>"printer-is-accepting-jobs"</CODE>.
     *
     * @return  Attribute category name.
     */
    public final String getName() {
        return "printer-is-accepting-jobs";
    }

}
