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
import javax.print.attribute.SetOfIntegerSyntax;
import javax.print.attribute.SupportedValuesAttribute;
import javax.print.attribute.standard.Copies;

/**
 * Class CopiesSupported is a printing attribute class, a set of integers, that
 * gives the supported values for a {@link Copies Copies} attribute. It is
 * restricted to a single contiguous range of integers; multiple non-overlapping
 * ranges are not allowed.
 * <P>
 * <B>IPP Compatibility:</B> The CopiesSupported attribute's canonical array
 * form gives the lower and upper bound for the range of copies to be included
 * in an IPP "copies-supported" attribute. See class {@link
 * SetOfIntegerSyntax SetOfIntegerSyntax} for an
 * explanation of canonical array form. The category name returned by
 * <CODE>getName()</CODE> gives the IPP attribute name.
 * <P>
 *
 * @author  Alan Kaminsky
 */
public final class CopiesSupported extends SetOfIntegerSyntax
        implements SupportedValuesAttribute {

    private static final long serialVersionUID = 6927711687034846001L;

    /**
     * Construct a new copies supported attribute containing a single integer.
     * That is, only the one value of Copies is supported.
     *
     * @param  member  Set member.
     *
     * @exception  IllegalArgumentException
     *  (Unchecked exception) Thrown if <CODE>member</CODE> is less than 1.
     */
    public CopiesSupported(int member) {
        super (member);
        if (member < 1) {
            throw new IllegalArgumentException("Copies value < 1 specified");
        }
    }

    /**
     * Construct a new copies supported attribute containing a single range of
     * integers. That is, only those values of Copies in the one range are
     * supported.
     *
     * @param  lowerBound  Lower bound of the range.
     * @param  upperBound  Upper bound of the range.
     *
     * @exception  IllegalArgumentException
     *     (Unchecked exception) Thrown if a null range is specified or if a
     *     non-null range is specified with <CODE>lowerBound</CODE> less than
     *     1.
     */
    public CopiesSupported(int lowerBound, int upperBound) {
        super(lowerBound, upperBound);

        if (lowerBound > upperBound) {
            throw new IllegalArgumentException("Null range specified");
        } else if (lowerBound < 1) {
            throw new IllegalArgumentException("Copies value < 1 specified");
        }
    }

    /**
     * Returns whether this copies supported attribute is equivalent to the
     * passed in object. To be equivalent, all of the following conditions must
     * be true:
     * <OL TYPE=1>
     * <LI>
     * <CODE>object</CODE> is not null.
     * <LI>
     * <CODE>object</CODE> is an instance of class CopiesSupported.
     * <LI>
     * This copies supported attribute's members and <CODE>object</CODE>'s
     * members are the same.
     * </OL>
     *
     * @param  object  Object to compare to.
     *
     * @return  True if <CODE>object</CODE> is equivalent to this copies
     *          supported attribute, false otherwise.
     */
    public boolean equals(Object object) {
        return super.equals (object) && object instanceof javax.print.attribute.standard.CopiesSupported;
    }

    /**
     * Get the printing attribute class which is to be used as the "category"
     * for this printing attribute value.
     * <P>
     * For class CopiesSupported, the category
     * is class CopiesSupported itself.
     *
     * @return  Printing attribute class (category), an instance of class
     *          {@link Class java.lang.Class}.
     */
    public final Class<? extends Attribute> getCategory() {
        return javax.print.attribute.standard.CopiesSupported.class;
    }

    /**
     * Get the name of the category of which this attribute value is an
     * instance.
     * <P>
     * For class CopiesSupported, the category
     * name is <CODE>"copies-supported"</CODE>.
     *
     * @return  Attribute category name.
     */
    public final String getName() {
        return "copies-supported";
    }

}
