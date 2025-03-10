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

import javax.print.attribute.EnumSyntax;
import javax.print.attribute.Attribute;
import javax.print.attribute.standard.PrinterState;
import javax.print.attribute.standard.PrinterStateReason;
import javax.print.attribute.standard.PrinterStateReasons;

/**
 * Class Severity is a printing attribute class, an enumeration, that denotes
 * the severity of a {@link PrinterStateReason PrinterStateReason} attribute.
 * <P>
 * Instances of Severity do not appear in a Print Service's attribute set
 * directly. Rather, a {@link PrinterStateReasons PrinterStateReasons}
 * attribute appears in the Print Service's attribute set.
 *  The {@link PrinterStateReasons
 * PrinterStateReasons} attribute contains zero, one, or more than one {@link
 * PrinterStateReason PrinterStateReason} objects which pertain to the Print
 * Service's status, and each {@link PrinterStateReason PrinterStateReason}
 * object is associated with a Severity level of REPORT (least severe),
 * WARNING, or ERROR (most severe).
 * The printer adds a {@link PrinterStateReason
 * PrinterStateReason} object to the Print Service's
 * {@link PrinterStateReasons PrinterStateReasons} attribute when the
 * corresponding condition becomes true
 * of the printer, and the printer removes the {@link PrinterStateReason
 * PrinterStateReason} object again when the corresponding condition becomes
 * false, regardless of whether the Print Service's overall
 * {@link PrinterState PrinterState} also changed.
 * <P>
 * <B>IPP Compatibility:</B>
 * <code>Severity.toString()</code> returns either "error", "warning", or
 * "report".  The string values returned by
 * each individual {@link PrinterStateReason} and
 * associated {@link javax.print.attribute.standard.Severity} object's <CODE>toString()</CODE>
 * methods, concatenated together with a hyphen (<CODE>"-"</CODE>) in
 * between, gives the IPP keyword value for a {@link PrinterStateReasons}.
 * The category name returned by <CODE>getName()</CODE> gives the IPP
 * attribute name.
 * <P>
 *
 * @author  Alan Kaminsky
 */
public final class Severity extends EnumSyntax implements Attribute {

    private static final long serialVersionUID = 8781881462717925380L;

    /**
     * Indicates that the {@link PrinterStateReason PrinterStateReason} is a
     * "report" (least severe). An implementation may choose to omit some or
     * all reports.
     * Some reports specify finer granularity about the printer state;
     * others serve as a precursor to a warning. A report must contain nothing
     * that could affect the printed output.
     */
    public static final javax.print.attribute.standard.Severity REPORT = new javax.print.attribute.standard.Severity(0);

    /**
     * Indicates that the {@link PrinterStateReason PrinterStateReason} is a
     * "warning." An implementation may choose to omit some or all warnings.
     * Warnings serve as a precursor to an error. A warning must contain
     * nothing  that prevents a job from completing, though in some cases the
     * output may be of lower quality.
     */
    public static final javax.print.attribute.standard.Severity WARNING = new javax.print.attribute.standard.Severity(1);

    /**
     * Indicates that the {@link PrinterStateReason PrinterStateReason} is an
     * "error" (most severe). An implementation must include all errors.
     * If this attribute contains one or more errors, the printer's
     * {@link PrinterState PrinterState} must be STOPPED.
     */
    public static final javax.print.attribute.standard.Severity ERROR = new javax.print.attribute.standard.Severity(2);

    /**
     * Construct a new severity enumeration value with the given integer
     * value.
     *
     * @param  value  Integer value.
     */
    protected Severity(int value) {
        super (value);
    }

    private static final String[] myStringTable = {
        "report",
        "warning",
        "error"
    };

    private static final javax.print.attribute.standard.Severity[] myEnumValueTable = {
        REPORT,
        WARNING,
        ERROR
    };

    /**
     * Returns the string table for class Severity.
     */
    protected String[] getStringTable() {
        return myStringTable;
    }

    /**
     * Returns the enumeration value table for class Severity.
     */
    protected EnumSyntax[] getEnumValueTable() {
        return myEnumValueTable;
    }


    /**
     * Get the printing attribute class which is to be used as the "category"
     * for this printing attribute value.
     * <P>
     * For class Severity, the category is class Severity itself.
     *
     * @return  Printing attribute class (category), an instance of class
     *          {@link Class java.lang.Class}.
     */
    public final Class<? extends Attribute> getCategory() {
        return javax.print.attribute.standard.Severity.class;
    }

    /**
     * Get the name of the category of which this attribute value is an
     * instance.
     * <P>
     * For class Severit, the category name is <CODE>"severity"</CODE>.
     *
     * @return  Attribute category name.
     */
    public final String getName() {
        return "severity";
    }

}
