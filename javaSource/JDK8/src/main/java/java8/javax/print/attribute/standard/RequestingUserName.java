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
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.standard.JobOriginatingUserName;

/**
 * Class RequestingUserName is a printing attribute class, a text attribute,
 * that specifies the name of the end user that submitted the print job. A
 * requesting user name is an arbitrary string defined by the client. The
 * printer does not put the client-specified RequestingUserName attribute into
 * the Print Job's attribute set; rather, the printer puts in a {@link
 * JobOriginatingUserName JobOriginatingUserName} attribute.
 * This means that services which support specifying a username with this
 * attribute should also report a JobOriginatingUserName in the job's
 * attribute set. Note that many print services may have a way to independently
 * authenticate the user name, and so may state support for a
 * requesting user name, but in practice will then report the user name
 * authenticated by the service rather than that specified via this
 * attribute.
 * <P>
 * <B>IPP Compatibility:</B> The string value gives the IPP name value. The
 * locale gives the IPP natural language. The category name returned by
 * <CODE>getName()</CODE> gives the IPP attribute name.
 * <P>
 *
 * @author  Alan Kaminsky
 */
public final class RequestingUserName   extends TextSyntax
    implements PrintRequestAttribute {

    private static final long serialVersionUID = -2683049894310331454L;

    /**
     * Constructs a new requesting user name attribute with the given user
     * name and locale.
     *
     * @param  userName  User name.
     * @param  locale    Natural language of the text string. null
     * is interpreted to mean the default locale as returned
     * by <code>Locale.getDefault()</code>
     *
     * @exception  NullPointerException
     *     (unchecked exception) Thrown if <CODE>userName</CODE> is null.
     */
    public RequestingUserName(String userName, Locale locale) {
        super (userName, locale);
    }

    /**
     * Returns whether this requesting user name attribute is equivalent to
     * the passed in object. To be equivalent, all of the following
     * conditions must be true:
     * <OL TYPE=1>
     * <LI>
     * <CODE>object</CODE> is not null.
     * <LI>
     * <CODE>object</CODE> is an instance of class RequestingUserName.
     * <LI>
     * This requesting user name attribute's underlying string and
     * <CODE>object</CODE>'s underlying string are equal.
     * <LI>
     * This requesting user name attribute's locale and
     * <CODE>object</CODE>'s locale are equal.
     * </OL>
     *
     * @param  object  Object to compare to.
     *
     * @return  True if <CODE>object</CODE> is equivalent to this requesting
     *          user name attribute, false otherwise.
     */
    public boolean equals(Object object) {
        return (super.equals(object) &&
                object instanceof javax.print.attribute.standard.RequestingUserName);
    }

    /**
     * Get the printing attribute class which is to be used as the "category"
     * for this printing attribute value.
     * <P>
     * For class RequestingUserName, the
     * category is class RequestingUserName itself.
     *
     * @return  Printing attribute class (category), an instance of class
     *          {@link Class java.lang.Class}.
     */
    public final Class<? extends Attribute> getCategory() {
        return javax.print.attribute.standard.RequestingUserName.class;
    }

    /**
     * Get the name of the category of which this attribute value is an
     * instance.
     * <P>
     * For class RequestingUserName, the
     * category name is <CODE>"requesting-user-name"</CODE>.
     *
     * @return  Attribute category name.
     */
    public final String getName() {
        return "requesting-user-name";
    }

}
