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
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.PrintJobAttribute;

/**
 * Class Destination is a printing attribute class, a URI, that is used to
 * indicate an alternate destination for the spooled printer formatted
 * data. Many PrintServices will not support the notion of a destination
 * other than the printer device, and so will not support this attribute.
 * <p>
 * A common use for this attribute will be applications which want
 * to redirect output to a local disk file : eg."file:out.prn".
 * Note that proper construction of "file:" scheme URI instances should
 * be performed using the <code>toURI()</code> method of class
 * {@link java.io.File File}.
 * See the documentation on that class for more information.
 * <p>
 * If a destination URI is specified in a PrintRequest and it is not
 * accessible for output by the PrintService, a PrintException will be thrown.
 * The PrintException may implement URIException to provide a more specific
 * cause.
 * <P>
 * <B>IPP Compatibility:</B> Destination is not an IPP attribute.
 * <P>
 *
 * @author  Phil Race.
 */
public final class Destination extends URISyntax
        implements PrintJobAttribute, PrintRequestAttribute {

    private static final long serialVersionUID = 6776739171700415321L;

    /**
     * Constructs a new destination attribute with the specified URI.
     *
     * @param  uri  URI.
     *
     * @exception  NullPointerException
     *     (unchecked exception) Thrown if <CODE>uri</CODE> is null.
     */
    public Destination(URI uri) {
        super (uri);
    }

    /**
     * Returns whether this destination attribute is equivalent to the
     * passed in object. To be equivalent, all of the following conditions
     * must be true:
     * <OL TYPE=1>
     * <LI>
     * <CODE>object</CODE> is not null.
     * <LI>
     * <CODE>object</CODE> is an instance of class Destination.
     * <LI>
     * This destination attribute's URI and <CODE>object</CODE>'s URI
     * are equal.
     * </OL>
     *
     * @param  object  Object to compare to.
     *
     * @return  True if <CODE>object</CODE> is equivalent to this destination
     *         attribute, false otherwise.
     */
    public boolean equals(Object object) {
        return (super.equals(object) &&
                object instanceof javax.print.attribute.standard.Destination);
    }

    /**
     * Get the printing attribute class which is to be used as the "category"
     * for this printing attribute value.
     * <P>
     * For class Destination, the category is class Destination itself.
     *
     * @return  Printing attribute class (category), an instance of class
     *          {@link Class java.lang.Class}.
     */
    public final Class<? extends Attribute> getCategory() {
        return javax.print.attribute.standard.Destination.class;
    }

    /**
     * Get the name of the category of which this attribute value is an
     * instance.
     * <P>
     * For class Destination, the category name is <CODE>"spool-data-destination"</CODE>.
     *
     * @return  Attribute category name.
     */
    public final String getName() {
        return "spool-data-destination";
    }

}
