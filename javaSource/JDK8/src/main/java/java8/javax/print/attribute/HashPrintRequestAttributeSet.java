/*
 * Copyright (c) 2000, 2003, Oracle and/or its affiliates. All rights reserved.
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


package java8.javax.print.attribute;

import javax.print.attribute.HashAttributeSet;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.PrintRequestAttributeSet;
import java.io.Serializable;

/**
 * Class HashPrintRequestAttributeSet inherits its implementation from
 * class {@link HashAttributeSet HashAttributeSet} and enforces the
 * semantic restrictions of interface
 * {@link PrintRequestAttributeSet PrintRequestAttributeSet}.
 * <P>
 *
 * @author  Alan Kaminsky
 */
public class HashPrintRequestAttributeSet extends HashAttributeSet
    implements PrintRequestAttributeSet, Serializable {

    private static final long serialVersionUID = 2364756266107751933L;

    /**
     * Construct a new, empty print request attribute set.
     */
    public HashPrintRequestAttributeSet() {
        super (PrintRequestAttribute.class);
    }

    /**
     * Construct a new print request attribute set,
     * initially populated with the given value.
     *
     * @param  attribute  Attribute value to add to the set.
     *
     * @exception  NullPointerException
     *     (unchecked exception) Thrown if <CODE>attribute</CODE> is null.
     */
    public HashPrintRequestAttributeSet(PrintRequestAttribute attribute) {
        super (attribute, PrintRequestAttribute.class);
    }

    /**
     * Construct a new print request attribute set, initially populated with
     * the values from the given array. The new attribute set is populated
     * by adding the elements of <CODE>attributes</CODE> array to the set in
     * sequence, starting at index 0. Thus, later array elements may replace
     * earlier array elements if the array contains duplicate attribute
     * values or attribute categories.
     *
     * @param  attributes  Array of attribute values to add to the set.
     *                     If null, an empty attribute set is constructed.
     *
     * @exception  NullPointerException
     *     (unchecked exception)
     * Thrown if any element of <CODE>attributes</CODE> is null.
     */
    public HashPrintRequestAttributeSet(PrintRequestAttribute[] attributes) {
        super (attributes, PrintRequestAttribute.class);
    }


    /**
     * Construct a new attribute set, initially populated with the
     * values from the  given set where the members of the attribute set
     * are restricted to the <code>(PrintRequestAttributeSe</code> interface.
     *
     * @param  attributes set of attribute values to initialise the set. If
     *                    null, an empty attribute set is constructed.
     *
     * @exception  ClassCastException
     *     (unchecked exception) Thrown if any element of
     * <CODE>attributes</CODE> is not an instance of
     * <CODE>(PrintRequestAttributeSe</CODE>.
     */
    public HashPrintRequestAttributeSet(PrintRequestAttributeSet attributes)
    {
        super(attributes, PrintRequestAttribute.class);
    }


}
