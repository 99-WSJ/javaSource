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
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.TypeCodePackage.Bounds;
import org.omg.CORBA.portable.InputStream;
import org.omg.DynamicAny.DynAny;
import org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
import org.omg.DynamicAny.NameDynAnyPair;
import org.omg.DynamicAny.NameValuePair;

abstract class DynAnyComplexImpl extends com.sun.corba.se.impl.dynamicany.DynAnyConstructedImpl
{
    //
    // Instance variables
    //

    String[] names = null;
    // Instance variables components and names above are kept in sync
    // with these two arrays at all times.
    NameValuePair[] nameValuePairs = null;
    NameDynAnyPair[] nameDynAnyPairs = null;

    //
    // Constructors
    //

    private DynAnyComplexImpl() {
        this(null, (Any)null, false);
    }

    protected DynAnyComplexImpl(ORB orb, Any any, boolean copyValue) {
        // We can be sure that typeCode is of kind tk_struct
        super(orb, any, copyValue);
        // Initialize components lazily, on demand.
        // This is an optimization in case the user is only interested in storing Anys.
    }

    protected DynAnyComplexImpl(ORB orb, TypeCode typeCode) {
        // We can be sure that typeCode is of kind tk_struct
        super(orb, typeCode);
        // For DynAnyComplex, the operation sets the current position to -1
        // for empty exceptions and to zero for all other TypeCodes.
        // The members (if any) are (recursively) initialized to their default values.
        index = 0;
    }

    //
    // DynAny interface methods
    //

    // _REVISIT_ Overridden to provide more efficient copying.
    // Copies all the internal representations which is faster than reconstructing them.
/*
    public org.omg.DynamicAny.DynAny copy() {
        if (status == STATUS_DESTROYED) {
            throw new OBJECT_NOT_EXIST();
        }
        DynAnyComplexImpl returnValue = null;
        if ((representations & REPRESENTATION_ANY) != 0) {
            // The flag "true" indicates copying the Any value
            returnValue = (DynAnyComplexImpl)DynAnyUtil.createMostDerivedDynAny(any, orb, true);
        }
        if ((representations & REPRESENTATION_COMPONENTS) != 0) {
        }
        return returnValue;
    }
*/

    //
    // Complex methods
    //

    public String current_member_name ()
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if( ! checkInitComponents() || index < 0 || index >= names.length) {
            throw new InvalidValue();
        }
        return names[index];
    }

    public TCKind current_member_kind ()
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if( ! checkInitComponents() || index < 0 || index >= components.length) {
            throw new InvalidValue();
        }
        return components[index].type().kind();
    }

    // Creates references to the parameter instead of copying it.
    public void set_members (NameValuePair[] value)
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (value == null || value.length == 0) {
            clearData();
            return;
        }

        Any memberAny;
        DynAny memberDynAny = null;
        String memberName;
        // We know that this is of kind tk_struct
        TypeCode expectedTypeCode = any.type();

        int expectedMemberCount = 0;
        try {
            expectedMemberCount = expectedTypeCode.member_count();
        } catch (BadKind badKind) { // impossible
        }
        if (expectedMemberCount != value.length) {
            clearData();
            throw new InvalidValue();
        }

        allocComponents(value);

        for (int i=0; i<value.length; i++) {
            if (value[i] != null) {
                memberName = value[i].id;
                String expectedMemberName = null;
                try {
                    expectedMemberName = expectedTypeCode.member_name(i);
                } catch (BadKind badKind) { // impossible
                } catch (Bounds bounds) { // impossible
                }
                if ( ! (expectedMemberName.equals(memberName) || memberName.equals(""))) {
                    clearData();
                    // _REVISIT_ More info
                    throw new TypeMismatch();
                }
                memberAny = value[i].value;
                TypeCode expectedMemberType = null;
                try {
                    expectedMemberType = expectedTypeCode.member_type(i);
                } catch (BadKind badKind) { // impossible
                } catch (Bounds bounds) { // impossible
                }
                if (! expectedMemberType.equal(memberAny.type())) {
                    clearData();
                    // _REVISIT_ More info
                    throw new TypeMismatch();
                }
                try {
                    // Creates the appropriate subtype without copying the Any
                    memberDynAny = DynAnyUtil.createMostDerivedDynAny(memberAny, orb, false);
                } catch (InconsistentTypeCode itc) {
                    throw new InvalidValue();
                }
                addComponent(i, memberName, memberAny, memberDynAny);
            } else {
                clearData();
                // _REVISIT_ More info
                throw new InvalidValue();
            }
        }
        index = (value.length == 0 ? NO_INDEX : 0);
        representations = REPRESENTATION_COMPONENTS;
    }

    // Creates references to the parameter instead of copying it.
    public void set_members_as_dyn_any (NameDynAnyPair[] value)
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (value == null || value.length == 0) {
            clearData();
            return;
        }

        Any memberAny;
        DynAny memberDynAny;
        String memberName;
        // We know that this is of kind tk_struct
        TypeCode expectedTypeCode = any.type();

        int expectedMemberCount = 0;
        try {
            expectedMemberCount = expectedTypeCode.member_count();
        } catch (BadKind badKind) { // impossible
        }
        if (expectedMemberCount != value.length) {
            clearData();
            throw new InvalidValue();
        }

        allocComponents(value);

        for (int i=0; i<value.length; i++) {
            if (value[i] != null) {
                memberName = value[i].id;
                String expectedMemberName = null;
                try {
                    expectedMemberName = expectedTypeCode.member_name(i);
                } catch (BadKind badKind) { // impossible
                } catch (Bounds bounds) { // impossible
                }
                if ( ! (expectedMemberName.equals(memberName) || memberName.equals(""))) {
                    clearData();
                    // _REVISIT_ More info
                    throw new TypeMismatch();
                }
                memberDynAny = value[i].value;
                memberAny = getAny(memberDynAny);
                TypeCode expectedMemberType = null;
                try {
                    expectedMemberType = expectedTypeCode.member_type(i);
                } catch (BadKind badKind) { // impossible
                } catch (Bounds bounds) { // impossible
                }
                if (! expectedMemberType.equal(memberAny.type())) {
                    clearData();
                    // _REVISIT_ More info
                    throw new TypeMismatch();
                }

                addComponent(i, memberName, memberAny, memberDynAny);
            } else {
                clearData();
                // _REVISIT_ More info
                throw new InvalidValue();
            }
        }
        index = (value.length == 0 ? NO_INDEX : 0);
        representations = REPRESENTATION_COMPONENTS;
    }

    //
    // Utility methods
    //

    private void allocComponents(int length) {
        components = new DynAny[length];
        names = new String[length];
        nameValuePairs = new NameValuePair[length];
        nameDynAnyPairs = new NameDynAnyPair[length];
        for (int i=0; i<length; i++) {
            nameValuePairs[i] = new NameValuePair();
            nameDynAnyPairs[i] = new NameDynAnyPair();
        }
    }

    private void allocComponents(NameValuePair[] value) {
        components = new DynAny[value.length];
        names = new String[value.length];
        nameValuePairs = value;
        nameDynAnyPairs = new NameDynAnyPair[value.length];
        for (int i=0; i<value.length; i++) {
            nameDynAnyPairs[i] = new NameDynAnyPair();
        }
    }

    private void allocComponents(NameDynAnyPair[] value) {
        components = new DynAny[value.length];
        names = new String[value.length];
        nameValuePairs = new NameValuePair[value.length];
        for (int i=0; i<value.length; i++) {
            nameValuePairs[i] = new NameValuePair();
        }
        nameDynAnyPairs = value;
    }

    private void addComponent(int i, String memberName, Any memberAny, DynAny memberDynAny) {
        components[i] = memberDynAny;
        names[i] = (memberName != null ? memberName : "");
        nameValuePairs[i].id = memberName;
        nameValuePairs[i].value = memberAny;
        nameDynAnyPairs[i].id = memberName;
        nameDynAnyPairs[i].value = memberDynAny;
        if (memberDynAny instanceof com.sun.corba.se.impl.dynamicany.DynAnyImpl)
            ((com.sun.corba.se.impl.dynamicany.DynAnyImpl)memberDynAny).setStatus(STATUS_UNDESTROYABLE);
    }

    // Initializes components, names, nameValuePairs and nameDynAnyPairs representation
    // from the Any representation
    protected boolean initializeComponentsFromAny() {
        // This typeCode is of kind tk_struct.
        TypeCode typeCode = any.type();
        TypeCode memberType = null;
        Any memberAny;
        DynAny memberDynAny = null;
        String memberName = null;
        int length = 0;

        try {
            length = typeCode.member_count();
        } catch (BadKind badKind) { // impossible
        }

        InputStream input = any.create_input_stream();

        allocComponents(length);

        for (int i=0; i<length; i++) {
            try {
                memberName = typeCode.member_name(i);
                memberType = typeCode.member_type(i);
            } catch (BadKind badKind) { // impossible
            } catch (Bounds bounds) { // impossible
            }
            memberAny = DynAnyUtil.extractAnyFromStream(memberType, input, orb);
            try {
                // Creates the appropriate subtype without copying the Any
                memberDynAny = DynAnyUtil.createMostDerivedDynAny(memberAny, orb, false);
                // _DEBUG_
                //System.out.println("Created DynAny for " + memberName +
                //                   ", type " + memberType.kind().value());
            } catch (InconsistentTypeCode itc) { // impossible
            }
            addComponent(i, memberName, memberAny, memberDynAny);
        }
        return true;
    }

    // Initializes components, names, nameValuePairs and nameDynAnyPairs representation
    // from the internal TypeCode information with default values
    // This is not done recursively, only one level.
    // More levels are initialized lazily, on demand.
    protected boolean initializeComponentsFromTypeCode() {
        // This typeCode is of kind tk_struct.
        TypeCode typeCode = any.type();
        TypeCode memberType = null;
        Any memberAny;
        DynAny memberDynAny = null;
        String memberName;
        int length = 0;

        try {
            length = typeCode.member_count();
        } catch (BadKind badKind) { // impossible
        }

        allocComponents(length);

        for (int i=0; i<length; i++) {
            memberName = null;
            try {
                memberName = typeCode.member_name(i);
                memberType = typeCode.member_type(i);
            } catch (BadKind badKind) { // impossible
            } catch (Bounds bounds) { // impossible
            }
            try {
                memberDynAny = DynAnyUtil.createMostDerivedDynAny(memberType, orb);
                // _DEBUG_
                //System.out.println("Created DynAny for " + memberName +
                //                   ", type " + memberType.kind().value());
/*
                if (memberDynAny instanceof DynAnyConstructedImpl) {
                    if ( ! ((DynAnyConstructedImpl)memberDynAny).isRecursive()) {
                        // This is the recursive part
                        ((DynAnyConstructedImpl)memberDynAny).initializeComponentsFromTypeCode();
                    }
                } // Other implementations have their own way of dealing with implementing the spec.
*/
            } catch (InconsistentTypeCode itc) { // impossible
            }
            // get a hold of the default initialized Any without copying
            memberAny = getAny(memberDynAny);
            addComponent(i, memberName, memberAny, memberDynAny);
        }
        return true;
    }

    // It is probably right not to destroy the released component DynAnys.
    // Some other DynAny or a user variable might still hold onto them
    // and if not then the garbage collector will take care of it.
    protected void clearData() {
        super.clearData();
        names = null;
        nameValuePairs = null;
        nameDynAnyPairs = null;
    }
}
