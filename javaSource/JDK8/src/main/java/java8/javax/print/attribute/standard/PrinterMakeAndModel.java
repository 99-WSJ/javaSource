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
import javax.print.attribute.TextSyntax;
import javax.print.attribute.PrintServiceAttribute;

/**
 * Class PrinterMakeAndModel is a printing attribute class, a text attribute,
 * that the make and model of the printer.
 * <P>
 * <B>IPP Compatibility:</B> The string value gives the IPP name value. The
 * locale gives the IPP natural language. The category name returned by
 * <CODE>getName()</CODE> gives the IPP attribute name.
 * <P>
 *
 * @author  Alan Kaminsky
 */
public final class PrinterMakeAndModel extends TextSyntax
        implements PrintServiceAttribute {

    private static final long serialVersionUID = 4580461489499351411L;

    /**
     * Constructs a new printer make and model attribute with the given make
     * and model string and locale.
     *
     * @param  makeAndModel  Printer make and model string.
     * @param  locale        Natural language of the text string. null
     * is interpreted to mean the default locale as returned
     * by <code>Locale.getDefault()</code>
     *
     * @exception  NullPointerException
     *    (unchecked exception) Thrown if <CODE>makeAndModel</CODE> is null.
     */
    public PrinterMakeAndModel(String makeAndModel, Locale locale) {
        super (makeAndModel, locale);
    }

    /**
     * Returns whether this printer make and model attribute is equivalent to
     * the passed in object. To be equivalent, all of the following conditions
     * must be true:
     * <OL TYPE=1>
     * <LI>
     * <CODE>object</CODE> is not null.
     * <LI>
     * <CODE>object</CODE> is an instance of class PrinterMakeAndModel.
     * <LI>
     * This printer make and model attribute's underlying string and
     * <CODE>object</CODE>'s underlying string are equal.
     * <LI>
     * This printer make and model attribute's locale and
     * <CODE>object</CODE>'s locale are equal.
     * </OL>
     *
     * @param  object  Object to compare to.
     *
     * @return  True if <CODE>object</CODE> is equivalent to this printer
     *          make and model attribute, false otherwise.
     */
    public boolean equals(Object object) {
        return (super.equals(object) &&
                object instanceof javax.print.attribute.standard.PrinterMakeAndModel);
    }

    /**
     * Get the printing attribute class which is to be used as the "category"
     * for this printing attribute value.
     * <P>
     * For class PrinterMakeAndModel, the
     * category is class PrinterMakeAndModel itself.
     *
     * @return  Printing attribute class (category), an instance of class
     *          {@link Class java.lang.Class}.
     */
    public final Class<? extends Attribute> getCategory() {
        return javax.print.attribute.standard.PrinterMakeAndModel.class;
    }

    /**
     * Get the name of the category of which this attribute value is an
     * instance.
     * <P>
     * For class PrinterMakeAndModel, the
     * category name is <CODE>"printer-make-and-model"</CODE>.
     *
     * @return  Attribute category name.
     */
    public final String getName() {
        return "printer-make-and-model";
    }

}
