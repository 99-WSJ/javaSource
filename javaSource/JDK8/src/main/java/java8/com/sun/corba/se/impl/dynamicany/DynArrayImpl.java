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

package java8.sun.corba.se.impl.dynamicany;

import com.sun.corba.se.impl.dynamicany.DynAnyUtil;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.DynamicAny.DynAny;
import org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynArray;

public class DynArrayImpl extends DynAnyCollectionImpl implements DynArray
{
    //
    // Constructors
    //

    private DynArrayImpl() {
        this(null, (Any)null, false);
    }

    protected DynArrayImpl(ORB orb, Any any, boolean copyValue) {
        super(orb, any, copyValue);
    }

    protected DynArrayImpl(ORB orb, TypeCode typeCode) {
        super(orb, typeCode);
    }

    // Initializes components and anys representation
    // from the Any representation
    protected boolean initializeComponentsFromAny() {
        // This typeCode is of kind tk_array.
        TypeCode typeCode = any.type();
        int length = getBound();
        TypeCode contentType = getContentType();
        InputStream input;

        try {
            input = any.create_input_stream();
        } catch (BAD_OPERATION e) {
            return false;
        }

        components = new DynAny[length];
        anys = new Any[length];

        for (int i=0; i<length; i++) {
            // _REVISIT_ Could use read_xxx_array() methods on InputStream for efficiency
            // but only for primitive types
            anys[i] = DynAnyUtil.extractAnyFromStream(contentType, input, orb);
            try {
                // Creates the appropriate subtype without copying the Any
                components[i] = DynAnyUtil.createMostDerivedDynAny(anys[i], orb, false);
            } catch (InconsistentTypeCode itc) { // impossible
            }
        }
        return true;
    }

    // Initializes components and anys representation
    // from the internal TypeCode information with default values.
    // This is not done recursively, only one level.
    // More levels are initialized lazily, on demand.
    protected boolean initializeComponentsFromTypeCode() {
        // This typeCode is of kind tk_array.
        TypeCode typeCode = any.type();
        int length = getBound();
        TypeCode contentType = getContentType();

        components = new DynAny[length];
        anys = new Any[length];

        for (int i=0; i<length; i++) {
            createDefaultComponentAt(i, contentType);
        }
        return true;
    }

    //
    // DynArray interface methods
    //

    // Initializes the elements of the array.
    // If value does not contain the same number of elements as the array dimension,
    // the operation raises InvalidValue.
    // If one or more elements have a type that is inconsistent with the DynArrays TypeCode,
    // the operation raises TypeMismatch.
    // This operation does not change the current position.
/*
    public void set_elements (org.omg.CORBA.Any[] value)
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;
*/

    //
    // Utility methods
    //

    protected void checkValue(Object[] value)
        throws InvalidValue
    {
        if (value == null || value.length != getBound()) {
            throw new InvalidValue();
        }
    }
}
