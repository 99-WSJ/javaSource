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

import javax.print.attribute.DocAttribute;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashAttributeSet;
import java.io.Serializable;

/**
 * Class HashDocAttributeSet provides an attribute set which
 * inherits its implementation from class {@link HashAttributeSet
 * HashAttributeSet} and enforces the semantic restrictions of interface {@link
 * DocAttributeSet DocAttributeSet}.
 * <P>
 *
 * @author  Alan Kaminsky
 */
public class HashDocAttributeSet extends HashAttributeSet
    implements DocAttributeSet, Serializable {

    private static final long serialVersionUID = -1128534486061432528L;

    /**
     * Construct a new, empty hash doc attribute set.
     */
    public HashDocAttributeSet() {
        super (DocAttribute.class);
    }

    /**
     * Construct a new hash doc attribute set,
     * initially populated with the given value.
     *
     * @param  attribute  Attribute value to add to the set.
     *
     * @exception  NullPointerException
     *     (unchecked exception) Thrown if <CODE>attribute</CODE> is null.
     */
    public HashDocAttributeSet(DocAttribute attribute) {
        super (attribute, DocAttribute.class);
    }

    /**
     * Construct a new hash doc attribute set,
     * initially populated with the values from the given array.
     * The new attribute set is populated by
     * adding the elements of <CODE>attributes</CODE> array to the set in
     * sequence, starting at index 0. Thus, later array elements may replace
     * earlier array elements if the array contains duplicate attribute
     * values or attribute categories.
     *
     * @param  attributes  Array of attribute values to add to the set.
     *                     If null, an empty attribute set is constructed.
     *
     * @exception  NullPointerException
     *  (unchecked exception)
     * Thrown if any element of <CODE>attributes</CODE> is null.
     */
    public HashDocAttributeSet(DocAttribute[] attributes) {
        super (attributes, DocAttribute.class);
    }

    /**
     * Construct a new attribute set, initially populated with the
     * values from the  given set where the members of the attribute set
     * are restricted to the <code>DocAttribute</code> interface.
     *
     * @param  attributes set of attribute values to initialise the set. If
     *                    null, an empty attribute set is constructed.
     *
     * @exception  ClassCastException
     *     (unchecked exception) Thrown if any element of
     * <CODE>attributes</CODE> is not an instance of
     * <CODE>DocAttribute</CODE>.
     */
    public HashDocAttributeSet(DocAttributeSet attributes) {
        super(attributes, DocAttribute.class);
    }

}
