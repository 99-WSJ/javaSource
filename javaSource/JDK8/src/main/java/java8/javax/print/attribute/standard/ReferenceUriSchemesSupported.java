/*
 * Copyright (c) 2000, 2012, Oracle and/or its affiliates. All rights reserved.
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

/**
 * Class ReferenceUriSchemesSupported is a printing attribute class
 * an enumeration, that indicates a "URI scheme," such as "http:" or "ftp:",
 * that a printer can use to retrieve print data stored at a URI location.
 * If a printer supports doc flavors with a print data representation class of
 * <CODE>"java.net.URL"</CODE>, the printer uses instances of class
 * ReferenceUriSchemesSupported to advertise the URI schemes it can accept.
 * The acceptable URI schemes are included as service attributes in the
 * lookup service; this lets clients search the
 * for printers that can get print data using a certain URI scheme. The
 * acceptable URI schemes can also be queried using the capability methods in
 * interface <code>PrintService</code>. However,
 * ReferenceUriSchemesSupported attributes are used solely for determining
 * acceptable URI schemes, they are never included in a doc's,
 * print request's, print job's, or print service's attribute set.
 * <P>
 * The Internet Assigned Numbers Authority maintains the
 * <A HREF="http://www.iana.org/assignments/uri-schemes.html">official
 * list of URI schemes</A>.
 * <p>
 * Class ReferenceUriSchemesSupported defines enumeration values for widely
 * used URI schemes. A printer that supports additional URI schemes
 * can define them in a subclass of class ReferenceUriSchemesSupported.
 * <P>
 * <B>IPP Compatibility:</B>  The category name returned by
 * <CODE>getName()</CODE> is the IPP attribute name.  The enumeration's
 * integer value is the IPP enum value.  The <code>toString()</code> method
 * returns the IPP string representation of the attribute value.
 * <P>
 *
 * @author  Alan Kaminsky
 */
public class ReferenceUriSchemesSupported
    extends EnumSyntax implements Attribute {

    private static final long serialVersionUID = -8989076942813442805L;

    /**
     * File Transfer Protocol (FTP).
     */
    public static final javax.print.attribute.standard.ReferenceUriSchemesSupported FTP =
        new javax.print.attribute.standard.ReferenceUriSchemesSupported(0);

    /**
     * HyperText Transfer Protocol (HTTP).
     */
    public static final javax.print.attribute.standard.ReferenceUriSchemesSupported HTTP = new javax.print.attribute.standard.ReferenceUriSchemesSupported(1);

    /**
     * Secure HyperText Transfer Protocol (HTTPS).
     */
    public static final javax.print.attribute.standard.ReferenceUriSchemesSupported HTTPS = new javax.print.attribute.standard.ReferenceUriSchemesSupported(2);

    /**
     * Gopher Protocol.
     */
    public static final javax.print.attribute.standard.ReferenceUriSchemesSupported GOPHER = new javax.print.attribute.standard.ReferenceUriSchemesSupported(3);

    /**
     * USENET news.
     */
    public static final javax.print.attribute.standard.ReferenceUriSchemesSupported NEWS = new javax.print.attribute.standard.ReferenceUriSchemesSupported(4);

    /**
     * USENET news using Network News Transfer Protocol (NNTP).
     */
    public static final javax.print.attribute.standard.ReferenceUriSchemesSupported NNTP = new javax.print.attribute.standard.ReferenceUriSchemesSupported(5);

    /**
     * Wide Area Information Server (WAIS) protocol.
     */
    public static final javax.print.attribute.standard.ReferenceUriSchemesSupported WAIS = new javax.print.attribute.standard.ReferenceUriSchemesSupported(6);

    /**
     * Host-specific file names.
     */
    public static final javax.print.attribute.standard.ReferenceUriSchemesSupported FILE = new javax.print.attribute.standard.ReferenceUriSchemesSupported(7);

    /**
     * Construct a new reference URI scheme enumeration value with the given
     * integer value.
     *
     * @param  value  Integer value.
     */
    protected ReferenceUriSchemesSupported(int value) {
        super (value);
    }

    private static final String[] myStringTable = {
        "ftp",
        "http",
        "https",
        "gopher",
        "news",
        "nntp",
        "wais",
        "file",
    };

    private static final javax.print.attribute.standard.ReferenceUriSchemesSupported[] myEnumValueTable = {
        FTP,
        HTTP,
        HTTPS,
        GOPHER,
        NEWS,
        NNTP,
        WAIS,
        FILE,
    };

    /**
     * Returns the string table for class ReferenceUriSchemesSupported.
     */
    protected String[] getStringTable() {
        return myStringTable.clone();
    }

    /**
     * Returns the enumeration value table for class
     * ReferenceUriSchemesSupported.
     */
    protected EnumSyntax[] getEnumValueTable() {
        return (EnumSyntax[])myEnumValueTable.clone();
    }

    /**
     * Get the printing attribute class which is to be used as the "category"
     * for this printing attribute value.
     * <P>
     * For class ReferenceUriSchemesSupported and any vendor-defined
     * subclasses, the category is class ReferenceUriSchemesSupported itself.
     *
     * @return  Printing attribute class (category), an instance of class
     *          {@link Class java.lang.Class}.
     */
    public final Class<? extends Attribute> getCategory() {
        return javax.print.attribute.standard.ReferenceUriSchemesSupported.class;
    }

    /**
     * Get the name of the category of which this attribute value is an
     * instance.
     * <P>
     * For class ReferenceUriSchemesSupported and any vendor-defined
     * subclasses, the category name is
     * <CODE>"reference-uri-schemes-supported"</CODE>.
     *
     * @return  Attribute category name.
     */
    public final String getName() {
        return "reference-uri-schemes-supported";
    }

}
