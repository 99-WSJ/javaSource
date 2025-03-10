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

import java.net.URI;

import javax.print.attribute.Attribute;
import javax.print.attribute.URISyntax;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.standard.PrinterMoreInfoManufacturer;

/**
 * Class PrinterMoreInfo is a printing attribute class, a URI, that is used to
 * obtain more information about this specific printer. For example, this
 * could be an HTTP type URI referencing an HTML page accessible to a web
 * browser. The information obtained from this URI is intended for end user
 * consumption. Features outside the scope of the Print Service API can be
 * accessed from this URI.
 * The information is intended to be specific to this printer instance and
 * site specific services (e.g. job pricing, services offered, end user
 * assistance).
 * <P>
 * In contrast, the {@link PrinterMoreInfoManufacturer
 * PrinterMoreInfoManufacturer} attribute is used to find out more information
 * about this general kind of printer rather than this specific printer.
 * <P>
 * <B>IPP Compatibility:</B> The string form returned by
 * <CODE>toString()</CODE>  gives the IPP uri value.
 * The category name returned by <CODE>getName()</CODE>
 * gives the IPP attribute name.
 * <P>
 *
 * @author  Alan Kaminsky
 */
public final class PrinterMoreInfo extends URISyntax
        implements PrintServiceAttribute {

    private static final long serialVersionUID = 4555850007675338574L;

    /**
     * Constructs a new printer more info attribute with the specified URI.
     *
     * @param  uri  URI.
     *
     * @exception  NullPointerException
     *     (unchecked exception) Thrown if <CODE>uri</CODE> is null.
     */
    public PrinterMoreInfo(URI uri) {
        super (uri);
    }

    /**
     * Returns whether this printer more info attribute is equivalent to the
     * passed in object. To be equivalent, all of the following conditions
     * must be true:
     * <OL TYPE=1>
     * <LI>
     * <CODE>object</CODE> is not null.
     * <LI>
     * <CODE>object</CODE> is an instance of class PrinterMoreInfo.
     * <LI>
     * This printer more info attribute's URI and <CODE>object</CODE>'s URI
     * are equal.
     * </OL>
     *
     * @param  object  Object to compare to.
     *
     * @return  True if <CODE>object</CODE> is equivalent to this printer
     *          more info attribute, false otherwise.
     */
    public boolean equals(Object object) {
        return (super.equals(object) &&
                object instanceof javax.print.attribute.standard.PrinterMoreInfo);
    }

    /**
     * Get the printing attribute class which is to be used as the "category"
     * for this printing attribute value.
     * <P>
     * For class PrinterMoreInfo, the category is class PrinterMoreInfo itself.
     *
     * @return  Printing attribute class (category), an instance of class
     *          {@link Class java.lang.Class}.
     */
    public final Class<? extends Attribute> getCategory() {
        return javax.print.attribute.standard.PrinterMoreInfo.class;
    }

    /**
     * Get the name of the category of which this attribute value is an
     * instance.
     * <P>
     * For class PrinterMoreInfo, the
     * category name is <CODE>"printer-more-info"</CODE>.
     *
     * @return  Attribute category name.
     */
    public final String getName() {
        return "printer-more-info";
    }

}
