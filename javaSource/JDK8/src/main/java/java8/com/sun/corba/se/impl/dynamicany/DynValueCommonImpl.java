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

import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
import org.omg.DynamicAny.DynValueCommon;
import org.omg.DynamicAny.NameDynAnyPair;
import org.omg.DynamicAny.NameValuePair;

abstract class DynValueCommonImpl extends DynAnyComplexImpl implements DynValueCommon
{
    //
    // Constructors
    //

    protected boolean isNull;

    private DynValueCommonImpl() {
        this(null, (Any)null, false);
        isNull = true;
    }

    protected DynValueCommonImpl(ORB orb, Any any, boolean copyValue) {
        super(orb, any, copyValue);
        isNull = checkInitComponents();
    }

    protected DynValueCommonImpl(ORB orb, TypeCode typeCode) {
        super(orb, typeCode);
        isNull = true;
    }

    //
    // DynValueCommon methods
    //

    // Returns TRUE if this object represents a null valuetype
    public boolean is_null() {
        return isNull;
    }

    // Changes the representation to a null valuetype.
    public void set_to_null() {
        isNull = true;
        clearData();
    }

    // If this object represents a null valuetype then this operation
    // replaces it with a newly constructed value with its components
    // initialized to default values as in DynAnyFactory::create_dyn_any_from_type_code.
    // If this object represents a non-null valuetype, then this operation has no effect.
    public void set_to_value() {
        if (isNull) {
            isNull = false;
            // the rest is done lazily
        }
        // else: there is nothing to do
    }

    //
    // Methods differing from DynStruct
    //

    // Required to raise InvalidValue if this is a null value type.
    public NameValuePair[] get_members ()
        throws InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (isNull) {
            throw new InvalidValue();
        }
        checkInitComponents();
        return nameValuePairs;
    }

    // Required to raise InvalidValue if this is a null value type.
    public NameDynAnyPair[] get_members_as_dyn_any ()
        throws InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (isNull) {
            throw new InvalidValue();
        }
        checkInitComponents();
        return nameDynAnyPairs;
    }

    //
    // Overridden methods
    //

    // Overridden to change to non-null status.
    public void set_members (NameValuePair[] value)
        throws TypeMismatch,
               InvalidValue
    {
        super.set_members(value);
        // If we didn't get an exception then this must be a valid non-null value
        isNull = false;
    }

    // Overridden to change to non-null status.
    public void set_members_as_dyn_any (NameDynAnyPair[] value)
        throws TypeMismatch,
               InvalidValue
    {
        super.set_members_as_dyn_any(value);
        // If we didn't get an exception then this must be a valid non-null value
        isNull = false;
    }
}
