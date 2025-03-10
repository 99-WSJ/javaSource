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
import org.omg.DynamicAny.DynAny;
import org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;

public class DynAnyBasicImpl extends com.sun.corba.se.impl.dynamicany.DynAnyImpl
{
    //
    // Constructors
    //

    private DynAnyBasicImpl() {
        this(null, (Any)null, false);
    }

    protected DynAnyBasicImpl(ORB orb, Any any, boolean copyValue) {
        super(orb, any, copyValue);
        // set the current position to 0 if any has components, otherwise to -1.
        index = NO_INDEX;
    }

    protected DynAnyBasicImpl(ORB orb, TypeCode typeCode) {
        super(orb, typeCode);
        // set the current position to 0 if any has components, otherwise to -1.
        index = NO_INDEX;
    }

    //
    // DynAny interface methods
    //

    public void assign (DynAny dyn_any)
        throws TypeMismatch
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        super.assign(dyn_any);
        index = NO_INDEX;
    }

    public void from_any (Any value)
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        super.from_any(value);
        index = NO_INDEX;
    }

    // Spec: Returns a copy of the internal Any
    public Any to_any() {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        return DynAnyUtil.copy(any, orb);
    }

    public boolean equal (DynAny dyn_any) {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (dyn_any == this) {
            return true;
        }
        // If the other DynAny is a constructed one we don't want it to have
        // to create its Any representation just for this test.
        if ( ! any.type().equal(dyn_any.type())) {
            return false;
        }
        //System.out.println("Comparing anys");
        return any.equal(getAny(dyn_any));
    }

    public void destroy() {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (status == STATUS_DESTROYABLE) {
            status = STATUS_DESTROYED;
        }
    }

    public DynAny copy() {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        // The flag "true" indicates copying the Any value
        try {
            return DynAnyUtil.createMostDerivedDynAny(any, orb, true);
        } catch (InconsistentTypeCode ictc) {
            return null; // impossible
        }
    }

    public DynAny current_component()
        throws TypeMismatch
    {
        return null;
    }

    public int component_count() {
        return 0;
    }

    public boolean next() {
        return false;
    }

    public boolean seek(int index) {
        return false;
    }

    public void rewind() {
    }

    public void insert_boolean(boolean value)
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (any.type().kind().value() != TCKind._tk_boolean)
            throw new TypeMismatch();
        any.insert_boolean(value);
    }

    public void insert_octet(byte value)
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (any.type().kind().value() != TCKind._tk_octet)
            throw new TypeMismatch();
        any.insert_octet(value);
    }

    public void insert_char(char value)
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (any.type().kind().value() != TCKind._tk_char)
            throw new TypeMismatch();
        any.insert_char(value);
    }

    public void insert_short(short value)
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (any.type().kind().value() != TCKind._tk_short)
            throw new TypeMismatch();
        any.insert_short(value);
    }

    public void insert_ushort(short value)
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (any.type().kind().value() != TCKind._tk_ushort)
            throw new TypeMismatch();
        any.insert_ushort(value);
    }

    public void insert_long(int value)
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (any.type().kind().value() != TCKind._tk_long)
            throw new TypeMismatch();
        any.insert_long(value);
    }

    public void insert_ulong(int value)
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (any.type().kind().value() != TCKind._tk_ulong)
            throw new TypeMismatch();
        any.insert_ulong(value);
    }

    public void insert_float(float value)
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (any.type().kind().value() != TCKind._tk_float)
            throw new TypeMismatch();
        any.insert_float(value);
    }

    public void insert_double(double value)
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (any.type().kind().value() != TCKind._tk_double)
            throw new TypeMismatch();
        any.insert_double(value);
    }

    public void insert_string(String value)
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (any.type().kind().value() != TCKind._tk_string)
            throw new TypeMismatch();
        if (value == null)
            throw new InvalidValue();
        // Throw InvalidValue if this is a bounded string and the length is exceeded
        try {
            if (any.type().length() > 0 && any.type().length() < value.length())
                throw new InvalidValue();
        } catch (BadKind bad) { // impossible
        }
        any.insert_string(value);
    }

    public void insert_reference(org.omg.CORBA.Object value)
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (any.type().kind().value() != TCKind._tk_objref)
            throw new TypeMismatch();
        any.insert_Object(value);
    }

    public void insert_typecode(TypeCode value)
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (any.type().kind().value() != TCKind._tk_TypeCode)
            throw new TypeMismatch();
        any.insert_TypeCode(value);
    }

    public void insert_longlong(long value)
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (any.type().kind().value() != TCKind._tk_longlong)
            throw new TypeMismatch();
        any.insert_longlong(value);
    }

    public void insert_ulonglong(long value)
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (any.type().kind().value() != TCKind._tk_ulonglong)
            throw new TypeMismatch();
        any.insert_ulonglong(value);
    }

    public void insert_wchar(char value)
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (any.type().kind().value() != TCKind._tk_wchar)
            throw new TypeMismatch();
        any.insert_wchar(value);
    }

    public void insert_wstring(String value)
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (any.type().kind().value() != TCKind._tk_wstring)
            throw new TypeMismatch();
        if (value == null)
            throw new InvalidValue();
        // Throw InvalidValue if this is a bounded string and the length is exceeded
        try {
            if (any.type().length() > 0 && any.type().length() < value.length())
                throw new InvalidValue();
        } catch (BadKind bad) { // impossible
        }
        any.insert_wstring(value);
    }

    public void insert_any(Any value)
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (any.type().kind().value() != TCKind._tk_any)
            throw new TypeMismatch();
        any.insert_any(value);
    }

    public void insert_dyn_any (DynAny value)
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (any.type().kind().value() != TCKind._tk_any)
            throw new TypeMismatch();
        // _REVISIT_ Copy value here?
        any.insert_any(value.to_any());
    }

    public void insert_val(java.io.Serializable value)
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        int kind = any.type().kind().value();
        if (kind != TCKind._tk_value && kind != TCKind._tk_value_box)
            throw new TypeMismatch();
        any.insert_Value(value);
    }

    public java.io.Serializable get_val()
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        int kind = any.type().kind().value();
        if (kind != TCKind._tk_value && kind != TCKind._tk_value_box)
            throw new TypeMismatch();
        return any.extract_Value();
    }

    public boolean get_boolean()
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (any.type().kind().value() != TCKind._tk_boolean)
            throw new TypeMismatch();
        return any.extract_boolean();
    }

    public byte get_octet()
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (any.type().kind().value() != TCKind._tk_octet)
            throw new TypeMismatch();
        return any.extract_octet();
    }

    public char get_char()
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (any.type().kind().value() != TCKind._tk_char)
            throw new TypeMismatch();
        return any.extract_char();
    }

    public short get_short()
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (any.type().kind().value() != TCKind._tk_short)
            throw new TypeMismatch();
        return any.extract_short();
    }

    public short get_ushort()
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (any.type().kind().value() != TCKind._tk_ushort)
            throw new TypeMismatch();
        return any.extract_ushort();
    }

    public int get_long()
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (any.type().kind().value() != TCKind._tk_long)
            throw new TypeMismatch();
        return any.extract_long();
    }

    public int get_ulong()
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (any.type().kind().value() != TCKind._tk_ulong)
            throw new TypeMismatch();
        return any.extract_ulong();
    }

    public float get_float()
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (any.type().kind().value() != TCKind._tk_float)
            throw new TypeMismatch();
        return any.extract_float();
    }

    public double get_double()
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (any.type().kind().value() != TCKind._tk_double)
            throw new TypeMismatch();
        return any.extract_double();
    }

    public String get_string()
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (any.type().kind().value() != TCKind._tk_string)
            throw new TypeMismatch();
        return any.extract_string();
    }

    public org.omg.CORBA.Object get_reference()
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (any.type().kind().value() != TCKind._tk_objref)
            throw new TypeMismatch();
        return any.extract_Object();
    }

    public TypeCode get_typecode()
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (any.type().kind().value() != TCKind._tk_TypeCode)
            throw new TypeMismatch();
        return any.extract_TypeCode();
    }

    public long get_longlong()
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (any.type().kind().value() != TCKind._tk_longlong)
            throw new TypeMismatch();
        return any.extract_longlong();
    }

    public long get_ulonglong()
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (any.type().kind().value() != TCKind._tk_ulonglong)
            throw new TypeMismatch();
        return any.extract_ulonglong();
    }

    public char get_wchar()
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (any.type().kind().value() != TCKind._tk_wchar)
            throw new TypeMismatch();
        return any.extract_wchar();
    }

    public String get_wstring()
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (any.type().kind().value() != TCKind._tk_wstring)
            throw new TypeMismatch();
        return any.extract_wstring();
    }

    public Any get_any()
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (any.type().kind().value() != TCKind._tk_any)
            throw new TypeMismatch();
        return any.extract_any();
    }

    public DynAny get_dyn_any()
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (any.type().kind().value() != TCKind._tk_any)
            throw new TypeMismatch();
        // _REVISIT_ Copy value here?
        try {
            return DynAnyUtil.createMostDerivedDynAny(any.extract_any(), orb, true);
        } catch (InconsistentTypeCode ictc) {
            // The spec doesn't allow us to throw back this exception
            // incase the anys any if of type Principal, native or abstract interface.
            return null;
        }
    }
}
