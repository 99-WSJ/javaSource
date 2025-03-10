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
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.DynamicAny.DynAny;
import org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;

abstract class DynAnyCollectionImpl extends com.sun.corba.se.impl.dynamicany.DynAnyConstructedImpl
{
    //
    // Instance variables
    //

    // Keep in sync with DynAny[] components at all times.
    Any[] anys = null;

    //
    // Constructors
    //

    private DynAnyCollectionImpl() {
        this(null, (Any)null, false);
    }

    protected DynAnyCollectionImpl(ORB orb, Any any, boolean copyValue) {
        super(orb, any, copyValue);
    }

    protected DynAnyCollectionImpl(ORB orb, TypeCode typeCode) {
        super(orb, typeCode);
    }

    //
    // Utility methods
    //

    protected void createDefaultComponentAt(int i, TypeCode contentType) {
        try {
            components[i] = DynAnyUtil.createMostDerivedDynAny(contentType, orb);
        } catch (InconsistentTypeCode itc) { // impossible
        }
        // get a hold of the default initialized Any without copying
        anys[i] = getAny(components[i]);
    }

    protected TypeCode getContentType() {
        try {
            return any.type().content_type();
        } catch (BadKind badKind) { // impossible
            return null;
        }
    }

    // This method has a different meaning for sequence and array:
    // For sequence value of 0 indicates an unbounded sequence,
    // values > 0 indicate a bounded sequence.
    // For array any value indicates the boundary.
    protected int getBound() {
        try {
            return any.type().length();
        } catch (BadKind badKind) { // impossible
            return 0;
        }
    }

    //
    // DynAny interface methods
    //

    // _REVISIT_ More efficient copy operation

    //
    // Collection methods
    //

    public Any[] get_elements () {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        return (checkInitComponents() ? anys : null);
    }

    protected abstract void checkValue(Object[] value)
        throws InvalidValue;

    // Initializes the elements of the ordered collection.
    // If value does not contain the same number of elements as the array dimension,
    // the operation raises InvalidValue.
    // If one or more elements have a type that is inconsistent with the collections TypeCode,
    // the operation raises TypeMismatch.
    // This operation does not change the current position.
    public void set_elements (Any[] value)
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        checkValue(value);

        components = new DynAny[value.length];
        anys = value;

        // We know that this is of kind tk_sequence or tk_array
        TypeCode expectedTypeCode = getContentType();
        for (int i=0; i<value.length; i++) {
            if (value[i] != null) {
                if (! value[i].type().equal(expectedTypeCode)) {
                    clearData();
                    // _REVISIT_ More info
                    throw new TypeMismatch();
                }
                try {
                    // Creates the appropriate subtype without copying the Any
                    components[i] = DynAnyUtil.createMostDerivedDynAny(value[i], orb, false);
                    //System.out.println(this + " created component " + components[i]);
                } catch (InconsistentTypeCode itc) {
                    throw new InvalidValue();
                }
            } else {
                clearData();
                // _REVISIT_ More info
                throw new InvalidValue();
            }
        }
        index = (value.length == 0 ? NO_INDEX : 0);
        // Other representations are invalidated by this operation
        representations = REPRESENTATION_COMPONENTS;
    }

    public DynAny[] get_elements_as_dyn_any () {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        return (checkInitComponents() ? components : null);
    }

    // Same semantics as set_elements(Any[])
    public void set_elements_as_dyn_any (DynAny[] value)
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        checkValue(value);

        components = (value == null ? emptyComponents : value);
        anys = new Any[value.length];

        // We know that this is of kind tk_sequence or tk_array
        TypeCode expectedTypeCode = getContentType();
        for (int i=0; i<value.length; i++) {
            if (value[i] != null) {
                if (! value[i].type().equal(expectedTypeCode)) {
                    clearData();
                    // _REVISIT_ More info
                    throw new TypeMismatch();
                }
                anys[i] = getAny(value[i]);
            } else {
                clearData();
                // _REVISIT_ More info
                throw new InvalidValue();
            }
        }
        index = (value.length == 0 ? NO_INDEX : 0);
        // Other representations are invalidated by this operation
        representations = REPRESENTATION_COMPONENTS;
    }
}
