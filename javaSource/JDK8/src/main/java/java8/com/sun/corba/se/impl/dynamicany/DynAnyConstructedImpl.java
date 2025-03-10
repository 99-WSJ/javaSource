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

package java8.com.sun.corba.se.impl.dynamicany;

import com.sun.corba.se.impl.corba.TypeCodeImpl;
import com.sun.corba.se.impl.dynamicany.DynAnyUtil;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.DynamicAny.DynAny;
import org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;

abstract class DynAnyConstructedImpl extends com.sun.corba.se.impl.dynamicany.DynAnyImpl
{
    protected static final byte REPRESENTATION_NONE = 0;
    protected static final byte REPRESENTATION_TYPECODE = 1;
    protected static final byte REPRESENTATION_ANY = 2;
    protected static final byte REPRESENTATION_COMPONENTS = 4;

    protected static final byte RECURSIVE_UNDEF = -1;
    protected static final byte RECURSIVE_NO = 0;
    protected static final byte RECURSIVE_YES = 1;

    protected static final DynAny[] emptyComponents = new DynAny[0];
    //
    // Instance variables
    //

    // Constructed DynAnys maintain an ordered collection of component DynAnys.
    DynAny[] components = emptyComponents;
    byte representations = REPRESENTATION_NONE;
    byte isRecursive = RECURSIVE_UNDEF;

    //
    // Constructors
    //

    private DynAnyConstructedImpl() {
        this(null, (Any)null, false);
    }

    protected DynAnyConstructedImpl(ORB orb, Any any, boolean copyValue) {
        super(orb, any, copyValue);
        //System.out.println(this + " constructed with any " + any);
        if (this.any != null) {
            representations = REPRESENTATION_ANY;
        }
        // set the current position to 0 if any has components, otherwise to -1.
        index = 0;
    }

    protected DynAnyConstructedImpl(ORB orb, TypeCode typeCode) {
        // assertion: typeCode has been checked to be valid for this particular subclass.
        // note: We don't copy TypeCodes since they are considered immutable.
        super(orb, typeCode);
        if (typeCode != null) {
            representations = REPRESENTATION_TYPECODE;
        }
        // set the current position to 0 if any has components, otherwise to -1.
        index = NO_INDEX;

        // _REVISIT_ Would need REPRESENTATION_TYPECODE for lazy initialization
        //if ( ! isRecursive()) {
        //    initializeComponentsFromTypeCode();
        //}
    }

    protected boolean isRecursive() {
        if (isRecursive == RECURSIVE_UNDEF) {
            TypeCode typeCode = any.type();
            if (typeCode instanceof TypeCodeImpl) {
                if (((TypeCodeImpl)typeCode).is_recursive())
                    isRecursive = RECURSIVE_YES;
                else
                    isRecursive = RECURSIVE_NO;
            } else {
                // No way to find out unless the TypeCode spec changes.
                isRecursive = RECURSIVE_NO;
            }
        }
        return (isRecursive == RECURSIVE_YES);
    }

    //
    // DynAny traversal methods
    //

    public DynAny current_component()
        throws TypeMismatch
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (index == NO_INDEX) {
            return null;
        }
        return (checkInitComponents() ? components[index] : null);
    }

    public int component_count() {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        return (checkInitComponents() ? components.length : 0);
    }

    public boolean next() {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (checkInitComponents() == false) {
            return false;
        }
        index++;
        if (index >= 0 && index < components.length) {
            return true;
        } else {
            index = NO_INDEX;
            return false;
        }
    }

    public boolean seek(int newIndex) {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (newIndex < 0) {
            this.index = NO_INDEX;
            return false;
        }
        if (checkInitComponents() == false) {
            return false;
        }
        if (newIndex < components.length) {
            index = newIndex;
            return true;
        }
        return false;
    }

    public void rewind() {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        this.seek(0);
    }

    //
    // Utility methods
    //

    protected void clearData() {
        super.clearData();
        // _REVISIT_ What about status?
        components = emptyComponents;
        index = NO_INDEX;
        representations = REPRESENTATION_NONE;
    }

    protected void writeAny(OutputStream out) {
        // If all we got is TypeCode representation (no value)
        // then we don't want to force creating a default value
        //System.out.println(this + " checkInitAny before writeAny");
        checkInitAny();
        super.writeAny(out);
    }

    // Makes sure that the components representation is initialized
    protected boolean checkInitComponents() {
        if ((representations & REPRESENTATION_COMPONENTS) == 0) {
            if ((representations & REPRESENTATION_ANY) != 0) {
                if (initializeComponentsFromAny()) {
                    representations |= REPRESENTATION_COMPONENTS;
                } else {
                    return false;
                }
            } else if ((representations & REPRESENTATION_TYPECODE) != 0) {
                if (initializeComponentsFromTypeCode()) {
                    representations |= REPRESENTATION_COMPONENTS;
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    // Makes sure that the Any representation is initialized
    protected void checkInitAny() {
        if ((representations & REPRESENTATION_ANY) == 0) {
            //System.out.println(this + " checkInitAny: reps does not have REPRESENTATION_ANY");
            if ((representations & REPRESENTATION_COMPONENTS) != 0) {
                //System.out.println(this + " checkInitAny: reps has REPRESENTATION_COMPONENTS");
                if (initializeAnyFromComponents()) {
                    representations |= REPRESENTATION_ANY;
                }
            } else if ((representations & REPRESENTATION_TYPECODE) != 0) {
                //System.out.println(this + " checkInitAny: reps has REPRESENTATION_TYPECODE");
                if (representations == REPRESENTATION_TYPECODE && isRecursive())
                    return;
                if (initializeComponentsFromTypeCode()) {
                    representations |= REPRESENTATION_COMPONENTS;
                }
                if (initializeAnyFromComponents()) {
                    representations |= REPRESENTATION_ANY;
                }
            }
        } else {
            //System.out.println(this + " checkInitAny: reps != REPRESENTATION_ANY");
        }
        return;
    }

    protected abstract boolean initializeComponentsFromAny();
    protected abstract boolean initializeComponentsFromTypeCode();

    // Collapses the whole DynAny hierarchys values into one single streamed Any
    protected boolean initializeAnyFromComponents() {
        //System.out.println(this + " initializeAnyFromComponents");
        OutputStream out = any.create_output_stream();
        for (int i=0; i<components.length; i++) {
            if (components[i] instanceof com.sun.corba.se.impl.dynamicany.DynAnyImpl) {
                ((com.sun.corba.se.impl.dynamicany.DynAnyImpl)components[i]).writeAny(out);
            } else {
                // Not our implementation. Nothing we can do to prevent copying.
                components[i].to_any().write_value(out);
            }
        }
        any.read_value(out.create_input_stream(), any.type());
        return true;
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
        clearData();
        super.assign(dyn_any);
        representations = REPRESENTATION_ANY;
        index = 0;
    }

    public void from_any (Any value)
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        clearData();
        super.from_any(value);
        representations = REPRESENTATION_ANY;
        index = 0;
    }

    // Spec: Returns a copy of the internal Any
    public Any to_any() {
        //System.out.println(this + " to_any ");
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        checkInitAny();
        // Anys value may still be uninitialized if DynAny was initialized by TypeCode only
        return DynAnyUtil.copy(any, orb);
    }

    public boolean equal (DynAny dyn_any) {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (dyn_any == this) {
            return true;
        }
        if ( ! any.type().equal(dyn_any.type())) {
            return false;
        }
        // This changes the current position of dyn_any.
        // Make sure that our position isn't changed.
        if (checkInitComponents() == false) {
            return false;
        }
        DynAny currentComponent = null;
        try {
            // Remember the current position to restore it later
            currentComponent = dyn_any.current_component();
            for (int i=0; i<components.length; i++) {
                if (dyn_any.seek(i) == false)
                    return false;
                //System.out.println(this + " comparing component " + i + "=" + components[i] +
                //                   " of type " + components[i].type().kind().value());
                if ( ! components[i].equal(dyn_any.current_component())) {
                    //System.out.println("Not equal component " + i);
                    return false;
                }
            }
        } catch (TypeMismatch tm) {
            // impossible, we checked the type codes already
        } finally {
            // Restore the current position of the other DynAny
            DynAnyUtil.set_current_component(dyn_any, currentComponent);
        }
        return true;
    }

    public void destroy() {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (status == STATUS_DESTROYABLE) {
            status = STATUS_DESTROYED;
            for (int i=0; i<components.length; i++) {
                if (components[i] instanceof com.sun.corba.se.impl.dynamicany.DynAnyImpl) {
                    ((com.sun.corba.se.impl.dynamicany.DynAnyImpl)components[i]).setStatus(STATUS_DESTROYABLE);
                }
                components[i].destroy();
            }
        }
    }

    public DynAny copy() {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        checkInitAny();
        try {
            return DynAnyUtil.createMostDerivedDynAny(any, orb, true);
        } catch (InconsistentTypeCode ictc) {
            return null; // impossible
        }
    }

    // getter / setter methods

    public void insert_boolean(boolean value)
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (index == NO_INDEX)
            throw new InvalidValue();
        DynAny currentComponent = current_component();
        if (DynAnyUtil.isConstructedDynAny(currentComponent))
            throw new TypeMismatch();
        currentComponent.insert_boolean(value);
    }

    public void insert_octet(byte value)
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (index == NO_INDEX)
            throw new InvalidValue();
        DynAny currentComponent = current_component();
        if (DynAnyUtil.isConstructedDynAny(currentComponent))
            throw new TypeMismatch();
        currentComponent.insert_octet(value);
    }

    public void insert_char(char value)
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (index == NO_INDEX)
            throw new InvalidValue();
        DynAny currentComponent = current_component();
        if (DynAnyUtil.isConstructedDynAny(currentComponent))
            throw new TypeMismatch();
        currentComponent.insert_char(value);
    }

    public void insert_short(short value)
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (index == NO_INDEX)
            throw new InvalidValue();
        DynAny currentComponent = current_component();
        if (DynAnyUtil.isConstructedDynAny(currentComponent))
            throw new TypeMismatch();
        currentComponent.insert_short(value);
    }

    public void insert_ushort(short value)
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (index == NO_INDEX)
            throw new InvalidValue();
        DynAny currentComponent = current_component();
        if (DynAnyUtil.isConstructedDynAny(currentComponent))
            throw new TypeMismatch();
        currentComponent.insert_ushort(value);
    }

    public void insert_long(int value)
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (index == NO_INDEX)
            throw new InvalidValue();
        DynAny currentComponent = current_component();
        if (DynAnyUtil.isConstructedDynAny(currentComponent))
            throw new TypeMismatch();
        currentComponent.insert_long(value);
    }

    public void insert_ulong(int value)
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (index == NO_INDEX)
            throw new InvalidValue();
        DynAny currentComponent = current_component();
        if (DynAnyUtil.isConstructedDynAny(currentComponent))
            throw new TypeMismatch();
        currentComponent.insert_ulong(value);
    }

    public void insert_float(float value)
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (index == NO_INDEX)
            throw new InvalidValue();
        DynAny currentComponent = current_component();
        if (DynAnyUtil.isConstructedDynAny(currentComponent))
            throw new TypeMismatch();
        currentComponent.insert_float(value);
    }

    public void insert_double(double value)
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (index == NO_INDEX)
            throw new InvalidValue();
        DynAny currentComponent = current_component();
        if (DynAnyUtil.isConstructedDynAny(currentComponent))
            throw new TypeMismatch();
        currentComponent.insert_double(value);
    }

    public void insert_string(String value)
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (index == NO_INDEX)
            throw new InvalidValue();
        DynAny currentComponent = current_component();
        if (DynAnyUtil.isConstructedDynAny(currentComponent))
            throw new TypeMismatch();
        currentComponent.insert_string(value);
    }

    public void insert_reference(org.omg.CORBA.Object value)
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (index == NO_INDEX)
            throw new InvalidValue();
        DynAny currentComponent = current_component();
        if (DynAnyUtil.isConstructedDynAny(currentComponent))
            throw new TypeMismatch();
        currentComponent.insert_reference(value);
    }

    public void insert_typecode(TypeCode value)
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (index == NO_INDEX)
            throw new InvalidValue();
        DynAny currentComponent = current_component();
        if (DynAnyUtil.isConstructedDynAny(currentComponent))
            throw new TypeMismatch();
        currentComponent.insert_typecode(value);
    }

    public void insert_longlong(long value)
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (index == NO_INDEX)
            throw new InvalidValue();
        DynAny currentComponent = current_component();
        if (DynAnyUtil.isConstructedDynAny(currentComponent))
            throw new TypeMismatch();
        currentComponent.insert_longlong(value);
    }

    public void insert_ulonglong(long value)
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (index == NO_INDEX)
            throw new InvalidValue();
        DynAny currentComponent = current_component();
        if (DynAnyUtil.isConstructedDynAny(currentComponent))
            throw new TypeMismatch();
        currentComponent.insert_ulonglong(value);
    }

    public void insert_wchar(char value)
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (index == NO_INDEX)
            throw new InvalidValue();
        DynAny currentComponent = current_component();
        if (DynAnyUtil.isConstructedDynAny(currentComponent))
            throw new TypeMismatch();
        currentComponent.insert_wchar(value);
    }

    public void insert_wstring(String value)
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (index == NO_INDEX)
            throw new InvalidValue();
        DynAny currentComponent = current_component();
        if (DynAnyUtil.isConstructedDynAny(currentComponent))
            throw new TypeMismatch();
        currentComponent.insert_wstring(value);
    }

    public void insert_any(Any value)
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (index == NO_INDEX)
            throw new InvalidValue();
        DynAny currentComponent = current_component();
        if (DynAnyUtil.isConstructedDynAny(currentComponent))
            throw new TypeMismatch();
        currentComponent.insert_any(value);
    }

    public void insert_dyn_any (DynAny value)
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (index == NO_INDEX)
            throw new InvalidValue();
        DynAny currentComponent = current_component();
        if (DynAnyUtil.isConstructedDynAny(currentComponent))
            throw new TypeMismatch();
        currentComponent.insert_dyn_any(value);
    }

    public void insert_val(java.io.Serializable value)
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (index == NO_INDEX)
            throw new InvalidValue();
        DynAny currentComponent = current_component();
        if (DynAnyUtil.isConstructedDynAny(currentComponent))
            throw new TypeMismatch();
        currentComponent.insert_val(value);
    }

    public java.io.Serializable get_val()
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (index == NO_INDEX)
            throw new InvalidValue();
        DynAny currentComponent = current_component();
        if (DynAnyUtil.isConstructedDynAny(currentComponent))
            throw new TypeMismatch();
        return currentComponent.get_val();
    }

    public boolean get_boolean()
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (index == NO_INDEX)
            throw new InvalidValue();
        DynAny currentComponent = current_component();
        if (DynAnyUtil.isConstructedDynAny(currentComponent))
            throw new TypeMismatch();
        return currentComponent.get_boolean();
    }

    public byte get_octet()
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (index == NO_INDEX)
            throw new InvalidValue();
        DynAny currentComponent = current_component();
        if (DynAnyUtil.isConstructedDynAny(currentComponent))
            throw new TypeMismatch();
        return currentComponent.get_octet();
    }

    public char get_char()
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (index == NO_INDEX)
            throw new InvalidValue();
        DynAny currentComponent = current_component();
        if (DynAnyUtil.isConstructedDynAny(currentComponent))
            throw new TypeMismatch();
        return currentComponent.get_char();
    }

    public short get_short()
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (index == NO_INDEX)
            throw new InvalidValue();
        DynAny currentComponent = current_component();
        if (DynAnyUtil.isConstructedDynAny(currentComponent))
            throw new TypeMismatch();
        return currentComponent.get_short();
    }

    public short get_ushort()
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (index == NO_INDEX)
            throw new InvalidValue();
        DynAny currentComponent = current_component();
        if (DynAnyUtil.isConstructedDynAny(currentComponent))
            throw new TypeMismatch();
        return currentComponent.get_ushort();
    }

    public int get_long()
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (index == NO_INDEX)
            throw new InvalidValue();
        DynAny currentComponent = current_component();
        if (DynAnyUtil.isConstructedDynAny(currentComponent))
            throw new TypeMismatch();
        return currentComponent.get_long();
    }

    public int get_ulong()
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (index == NO_INDEX)
            throw new InvalidValue();
        DynAny currentComponent = current_component();
        if (DynAnyUtil.isConstructedDynAny(currentComponent))
            throw new TypeMismatch();
        return currentComponent.get_ulong();
    }

    public float get_float()
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (index == NO_INDEX)
            throw new InvalidValue();
        DynAny currentComponent = current_component();
        if (DynAnyUtil.isConstructedDynAny(currentComponent))
            throw new TypeMismatch();
        return currentComponent.get_float();
    }

    public double get_double()
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (index == NO_INDEX)
            throw new InvalidValue();
        DynAny currentComponent = current_component();
        if (DynAnyUtil.isConstructedDynAny(currentComponent))
            throw new TypeMismatch();
        return currentComponent.get_double();
    }

    public String get_string()
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (index == NO_INDEX)
            throw new InvalidValue();
        DynAny currentComponent = current_component();
        if (DynAnyUtil.isConstructedDynAny(currentComponent))
            throw new TypeMismatch();
        return currentComponent.get_string();
    }

    public org.omg.CORBA.Object get_reference()
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (index == NO_INDEX)
            throw new InvalidValue();
        DynAny currentComponent = current_component();
        if (DynAnyUtil.isConstructedDynAny(currentComponent))
            throw new TypeMismatch();
        return currentComponent.get_reference();
    }

    public TypeCode get_typecode()
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (index == NO_INDEX)
            throw new InvalidValue();
        DynAny currentComponent = current_component();
        if (DynAnyUtil.isConstructedDynAny(currentComponent))
            throw new TypeMismatch();
        return currentComponent.get_typecode();
    }

    public long get_longlong()
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (index == NO_INDEX)
            throw new InvalidValue();
        DynAny currentComponent = current_component();
        if (DynAnyUtil.isConstructedDynAny(currentComponent))
            throw new TypeMismatch();
        return currentComponent.get_longlong();
    }

    public long get_ulonglong()
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (index == NO_INDEX)
            throw new InvalidValue();
        DynAny currentComponent = current_component();
        if (DynAnyUtil.isConstructedDynAny(currentComponent))
            throw new TypeMismatch();
        return currentComponent.get_ulonglong();
    }

    public char get_wchar()
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (index == NO_INDEX)
            throw new InvalidValue();
        DynAny currentComponent = current_component();
        if (DynAnyUtil.isConstructedDynAny(currentComponent))
            throw new TypeMismatch();
        return currentComponent.get_wchar();
    }

    public String get_wstring()
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (index == NO_INDEX)
            throw new InvalidValue();
        DynAny currentComponent = current_component();
        if (DynAnyUtil.isConstructedDynAny(currentComponent))
            throw new TypeMismatch();
        return currentComponent.get_wstring();
    }

    public Any get_any()
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (index == NO_INDEX)
            throw new InvalidValue();
        DynAny currentComponent = current_component();
        if (DynAnyUtil.isConstructedDynAny(currentComponent))
            throw new TypeMismatch();
        return currentComponent.get_any();
    }

    public DynAny get_dyn_any()
        throws TypeMismatch,
               InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        if (index == NO_INDEX)
            throw new InvalidValue();
        DynAny currentComponent = current_component();
        if (DynAnyUtil.isConstructedDynAny(currentComponent))
            throw new TypeMismatch();
        return currentComponent.get_dyn_any();
    }
}
