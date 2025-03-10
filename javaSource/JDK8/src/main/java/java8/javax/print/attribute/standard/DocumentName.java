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
import javax.print.attribute.DocAttribute;
import javax.print.attribute.standard.JobName;

/**
 * Class DocumentName is a printing attribute class, a text attribute, that
 * specifies the name of a document. DocumentName is an attribute of the print
 * data (the doc), not of the Print Job. A document's name is an arbitrary
 * string defined by the client.
 * However if a JobName is not specified, the DocumentName should be used
 * instead, which implies that supporting specification of DocumentName
 * requires reporting of JobName and vice versa.
 * See {@link JobName JobName} for more information.
 * <P>
 * <B>IPP Compatibility:</B> The string value gives the IPP name value. The
 * locale gives the IPP natural language. The category name returned by
 * <CODE>getName()</CODE> gives the IPP attribute name.
 * <P>
 *
 * @author  Alan Kaminsky
 */
public final class DocumentName extends TextSyntax implements DocAttribute {

    private static final long serialVersionUID = 7883105848533280430L;

    /**
     * Constructs a new document name attribute with the given document name
     * and locale.
     *
     * @param  documentName  Document name.
     * @param  locale        Natural language of the text string. null
     * is interpreted to mean the default locale as returned
     * by <code>Locale.getDefault()</code>
     *
     * @exception  NullPointerException
     *   (unchecked exception) Thrown if <CODE>documentName</CODE> is null.
     */
    public DocumentName(String documentName, Locale locale) {
        super (documentName, locale);
    }

    /**
     * Returns whether this document name attribute is equivalent to the
     * passed in object.
     * To be equivalent, all of the following conditions must be true:
     * <OL TYPE=1>
     * <LI>
     * <CODE>object</CODE> is not null.
     * <LI>
     * <CODE>object</CODE> is an instance of class DocumentName.
     * <LI>
     * This document name attribute's underlying string and
     * <CODE>object</CODE>'s underlying string are equal.
     * <LI>
     * This document name attribute's locale and <CODE>object</CODE>'s locale
     * are equal.
     * </OL>
     *
     * @param  object  Object to compare to.
     *
     * @return  True if <CODE>object</CODE> is equivalent to this document
     *          name attribute, false otherwise.
     */
    public boolean equals(Object object) {
        return (super.equals (object) && object instanceof javax.print.attribute.standard.DocumentName);
    }

    /**
     * Get the printing attribute class which is to be used as the "category"
     * for this printing attribute value.
     * <P>
     * For class DocumentName, the category is class DocumentName itself.
     *
     * @return  Printing attribute class (category), an instance of class
     *          {@link Class java.lang.Class}.
     */
    public final Class<? extends Attribute> getCategory() {
        return javax.print.attribute.standard.DocumentName.class;
    }

    /**
     * Get the name of the category of which this attribute value is an
     * instance.
     * <P>
     * For class DocumentName, the category name is <CODE>"document-name"</CODE>.
     *
     * @return  Attribute category name.
     */
    public final String getName() {
        return "document-name";
    }

}
