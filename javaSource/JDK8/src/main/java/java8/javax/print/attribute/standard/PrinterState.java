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
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.standard.PrinterStateReasons;

/**
 * Class PrinterState is a printing attribute class, an enumeration, that
 * identifies the current state of a printer. Class PrinterState defines
 * standard printer state values. A Print Service implementation only needs
 * to report those printer states which are appropriate for the particular
 * implementation; it does not have to report every defined printer state. The
 * {@link PrinterStateReasons PrinterStateReasons} attribute augments the
 * PrinterState attribute to give more detailed information about the printer
 * in  given printer state.
 * <P>
 * <B>IPP Compatibility:</B> The category name returned by
 * <CODE>getName()</CODE> is the IPP attribute name.  The enumeration's
 * integer value is the IPP enum value.  The <code>toString()</code> method
 * returns the IPP string representation of the attribute value.
 * <P>
 *
 * @author  Alan Kaminsky
 */
public final class PrinterState extends EnumSyntax
implements PrintServiceAttribute {

    private static final long serialVersionUID = -649578618346507718L;

    /**
     * The printer state is unknown.
     */
    public static final javax.print.attribute.standard.PrinterState UNKNOWN = new javax.print.attribute.standard.PrinterState(0);

    /**
     * Indicates that new jobs can start processing without waiting.
     */
    public static final javax.print.attribute.standard.PrinterState IDLE = new javax.print.attribute.standard.PrinterState(3);

    /**
     * Indicates that jobs are processing;
     * new jobs will wait before processing.
     */
    public static final javax.print.attribute.standard.PrinterState PROCESSING = new javax.print.attribute.standard.PrinterState(4);

    /**
     * Indicates that no jobs can be processed and intervention is required.
     */
    public static final javax.print.attribute.standard.PrinterState STOPPED = new javax.print.attribute.standard.PrinterState(5);

    /**
     * Construct a new printer state enumeration value with the given integer
     * value.
     *
     * @param  value  Integer value.
     */
    protected PrinterState(int value) {
        super (value);
    }

    private static final String[] myStringTable = {
        "unknown",
        null,
        null,
        "idle",
        "processing",
        "stopped"
    };

    private static final javax.print.attribute.standard.PrinterState[] myEnumValueTable = {
        UNKNOWN,
        null,
        null,
        IDLE,
        PROCESSING,
        STOPPED
    };

    /**
     * Returns the string table for class PrinterState.
     */
    protected String[] getStringTable() {
        return myStringTable;
    }

    /**
     * Returns the enumeration value table for class PrinterState.
     */
    protected EnumSyntax[] getEnumValueTable() {
        return myEnumValueTable;
    }

    /**
     * Get the printing attribute class which is to be used as the "category"
     * for this printing attribute value.
     * <P>
     * For class PrinterState, the category is class PrinterState itself.
     *
     * @return  Printing attribute class (category), an instance of class
     *          {@link Class java.lang.Class}.
     */
    public final Class<? extends Attribute> getCategory() {
        return javax.print.attribute.standard.PrinterState.class;
    }

    /**
     * Get the name of the category of which this attribute value is an
     * instance.
     * <P>
     * For class PrinterState, the category name is <CODE>"printer-state"</CODE>.
     *
     * @return  Attribute category name.
     */
    public final String getName() {
        return "printer-state";
    }

}
