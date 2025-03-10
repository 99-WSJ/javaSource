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

import java.util.Locale;

import javax.print.attribute.Attribute;
import javax.print.attribute.EnumSyntax;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.standard.MultipleDocumentHandling;

/**
 * Class JobSheets is a printing attribute class, an enumeration, that
 * determines which job start and end sheets, if any, must be printed with a
 * job. Class JobSheets declares keywords for standard job sheets values.
 * Implementation- or site-defined names for a job sheets attribute may also be
 * created by defining a subclass of class JobSheets.
 * <P>
 * The effect of a JobSheets attribute on multidoc print jobs (jobs with
 * multiple documents) may be affected by the {@link MultipleDocumentHandling
 * MultipleDocumentHandling} job attribute, depending on the meaning of the
 * particular JobSheets value.
 * <P>
 * <B>IPP Compatibility:</B>  The category name returned by
 * <CODE>getName()</CODE> is the IPP attribute name.  The
 * enumeration's integer value is the IPP enum value.  The
 * <code>toString()</code> method returns the IPP string representation of
 * the attribute value. For a subclass, the attribute value must be
 * localized to give the IPP name and natural language values.
 * <P>
 *
 * @author  Alan Kaminsky
 */
public class JobSheets extends EnumSyntax
        implements PrintRequestAttribute, PrintJobAttribute {

    private static final long serialVersionUID = -4735258056132519759L;

    /**
     * No job sheets are printed.
     */
    public static final javax.print.attribute.standard.JobSheets NONE = new javax.print.attribute.standard.JobSheets(0);

    /**
     * One or more site specific standard job sheets are printed. e.g. a
     * single start sheet is printed, or both start and end sheets are
     * printed.
     */
    public static final javax.print.attribute.standard.JobSheets STANDARD = new javax.print.attribute.standard.JobSheets(1);

    /**
     * Construct a new job sheets enumeration value with the given integer
     * value.
     *
     * @param  value  Integer value.
     */
    protected JobSheets(int value) {
        super (value);
    }

    private static final String[] myStringTable = {
        "none",
        "standard"
    };

    private static final javax.print.attribute.standard.JobSheets[] myEnumValueTable = {
        NONE,
        STANDARD
    };

    /**
     * Returns the string table for class JobSheets.
     */
    protected String[] getStringTable() {
        return (String[])myStringTable.clone();
    }

    /**
     * Returns the enumeration value table for class JobSheets.
     */
    protected EnumSyntax[] getEnumValueTable() {
        return (EnumSyntax[])myEnumValueTable.clone();
    }

    /**
     * Get the printing attribute class which is to be used as the "category"
     * for this printing attribute value.
     * <P>
     * For class JobSheets and any vendor-defined subclasses, the category is
     * class JobSheets itself.
     *
     * @return  Printing attribute class (category), an instance of class
     *          {@link Class java.lang.Class}.
     */
    public final Class<? extends Attribute> getCategory() {
        return javax.print.attribute.standard.JobSheets.class;
    }

    /**
     * Get the name of the category of which this attribute value is an
     * instance.
     * <P>
     * For class JobSheets and any vendor-defined subclasses, the category
     * name is <CODE>"job-sheets"</CODE>.
     *
     * @return  Attribute category name.
     */
    public final String getName() {
        return "job-sheets";
    }

}
