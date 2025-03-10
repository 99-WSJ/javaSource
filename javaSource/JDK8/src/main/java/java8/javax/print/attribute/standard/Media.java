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
import javax.print.attribute.DocAttribute;
import javax.print.attribute.EnumSyntax;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.PrintJobAttribute;

/**
 * Class Media is a printing attribute class that specifies the
 * medium on which to print.
 * <p>
 * Media may be specified in different ways.
 * <ul>
 * <li> it may be specified by paper source - eg paper tray
 * <li> it may be specified by a standard size - eg "A4"
 * <li> it may be specified by a name - eg "letterhead"
 * </ul>
 * Each of these corresponds to the IPP "media" attribute.
 * The current API does not support describing media by characteristics
 * (eg colour, opacity).
 * This may be supported in a later revision of the specification.
 * <p>
 * A Media object is constructed with a value which represents
 * one of the ways in which the Media attribute can be specified.
 * <p>
 * <B>IPP Compatibility:</B>  The category name returned by
 * <CODE>getName()</CODE> is the IPP attribute name.  The enumeration's
 * integer value is the IPP enum value.  The <code>toString()</code> method
 * returns the IPP string representation of the attribute value.
 * <P>
 *
 * @author Phil Race
 */
public abstract class Media extends EnumSyntax
    implements DocAttribute, PrintRequestAttribute, PrintJobAttribute {

    private static final long serialVersionUID = -2823970704630722439L;

    /**
     * Constructs a new media attribute specified by name.
     *
     * @param value         a value
     */
    protected Media(int value) {
           super (value);
    }

    /**
     * Returns whether this media attribute is equivalent to the passed in
     * object. To be equivalent, all of the following conditions must be true:
     * <OL TYPE=1>
     * <LI>
     * <CODE>object</CODE> is not null.
     * <LI>
     * <CODE>object</CODE> is of the same subclass of Media as this object.
     * <LI>
     * The values are equal.
     * </OL>
     *
     * @param  object  Object to compare to.
     *
     * @return  True if <CODE>object</CODE> is equivalent to this media
     *          attribute, false otherwise.
     */
    public boolean equals(Object object) {
        return(object != null && object instanceof javax.print.attribute.standard.Media &&
               object.getClass() == this.getClass() &&
               ((javax.print.attribute.standard.Media)object).getValue() == this.getValue());
    }

    /**
     * Get the printing attribute class which is to be used as the "category"
     * for this printing attribute value.
     * <P>
     * For class Media and any vendor-defined subclasses, the category is
     * class Media itself.
     *
     * @return  Printing attribute class (category), an instance of class
     *          {@link Class java.lang.Class}.
     */
    public final Class<? extends Attribute> getCategory() {
        return javax.print.attribute.standard.Media.class;
    }

    /**
     * Get the name of the category of which this attribute value is an
     * instance.
     * <P>
     * For class Media and any vendor-defined subclasses, the category name is
     * <CODE>"media"</CODE>.
     *
     * @return  Attribute category name.
     */
    public final String getName() {
        return "media";
    }

}
