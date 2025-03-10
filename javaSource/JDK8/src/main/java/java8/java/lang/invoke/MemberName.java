/*
 * Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java8.java.lang.invoke;

import sun.invoke.util.BytecodeDescriptor;
import sun.invoke.util.VerifyAccess;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandleNatives;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.*;
import java.util.*;

import static java.lang.invoke.MethodHandleNatives.Constants.*;
import static java.lang.invoke.MethodHandleStatics.newIllegalArgumentException;
import static java.lang.invoke.MethodHandleStatics.newInternalError;

/**
 * A {@code MemberName} is a compact symbolic datum which fully characterizes
 * a method or field reference.
 * A member name refers to a field, method, constructor, or member type.
 * Every member name has a simple name (a string) and a type (either a Class or MethodType).
 * A member name may also have a non-null declaring class, or it may be simply
 * a naked name/type pair.
 * A member name may also have non-zero modifier flags.
 * Finally, a member name may be either resolved or unresolved.
 * If it is resolved, the existence of the named
 * <p>
 * Whether resolved or not, a member name provides no access rights or
 * invocation capability to its possessor.  It is merely a compact
 * representation of all symbolic information necessary to link to
 * and properly use the named member.
 * <p>
 * When resolved, a member name's internal implementation may include references to JVM metadata.
 * This representation is stateless and only decriptive.
 * It provides no private information and no capability to use the member.
 * <p>
 * By contrast, a {@linkplain Method} contains fuller information
 * about the internals of a method (except its bytecodes) and also
 * allows invocation.  A MemberName is much lighter than a Method,
 * since it contains about 7 fields to the 16 of Method (plus its sub-arrays),
 * and those seven fields omit much of the information in Method.
 * @author jrose
 */
/*non-public*/ final class MemberName implements Member, Cloneable {
    private Class<?> clazz;       // class in which the method is defined
    private String   name;        // may be null if not yet materialized
    private Object   type;        // may be null if not yet materialized
    private int      flags;       // modifier bits; see reflect.Modifier
    //@Injected JVM_Method* vmtarget;
    //@Injected int         vmindex;
    private Object   resolution;  // if null, this guy is resolved

    /** Return the declaring class of this member.
     *  In the case of a bare name and type, the declaring class will be null.
     */
    public Class<?> getDeclaringClass() {
        return clazz;
    }

    /** Utility method producing the class loader of the declaring class. */
    public ClassLoader getClassLoader() {
        return clazz.getClassLoader();
    }

    /** Return the simple name of this member.
     *  For a type, it is the same as {@link Class#getSimpleName}.
     *  For a method or field, it is the simple name of the member.
     *  For a constructor, it is always {@code "&lt;init&gt;"}.
     */
    public String getName() {
        if (name == null) {
            expandFromVM();
            if (name == null) {
                return null;
            }
        }
        return name;
    }

    public MethodType getMethodOrFieldType() {
        if (isInvocable())
            return getMethodType();
        if (isGetter())
            return MethodType.methodType(getFieldType());
        if (isSetter())
            return MethodType.methodType(void.class, getFieldType());
        throw new InternalError("not a method or field: "+this);
    }

    /** Return the declared type of this member, which
     *  must be a method or constructor.
     */
    public MethodType getMethodType() {
        if (type == null) {
            expandFromVM();
            if (type == null) {
                return null;
            }
        }
        if (!isInvocable()) {
            throw newIllegalArgumentException("not invocable, no method type");
        }

        {
            // Get a snapshot of type which doesn't get changed by racing threads.
            final Object type = this.type;
            if (type instanceof MethodType) {
                return (MethodType) type;
            }
        }

        // type is not a MethodType yet.  Convert it thread-safely.
        synchronized (this) {
            if (type instanceof String) {
                String sig = (String) type;
                MethodType res = MethodType.fromMethodDescriptorString(sig, getClassLoader());
                type = res;
            } else if (type instanceof Object[]) {
                Object[] typeInfo = (Object[]) type;
                Class<?>[] ptypes = (Class<?>[]) typeInfo[1];
                Class<?> rtype = (Class<?>) typeInfo[0];
                MethodType res = MethodType.methodType(rtype, ptypes);
                type = res;
            }
            // Make sure type is a MethodType for racing threads.
            assert type instanceof MethodType : "bad method type " + type;
        }
        return (MethodType) type;
    }

    /** Return the actual type under which this method or constructor must be invoked.
     *  For non-static methods or constructors, this is the type with a leading parameter,
     *  a reference to declaring class.  For static methods, it is the same as the declared type.
     */
    public MethodType getInvocationType() {
        MethodType itype = getMethodOrFieldType();
        if (isConstructor() && getReferenceKind() == REF_newInvokeSpecial)
            return itype.changeReturnType(clazz);
        if (!isStatic())
            return itype.insertParameterTypes(0, clazz);
        return itype;
    }

    /** Utility method producing the parameter types of the method type. */
    public Class<?>[] getParameterTypes() {
        return getMethodType().parameterArray();
    }

    /** Utility method producing the return type of the method type. */
    public Class<?> getReturnType() {
        return getMethodType().returnType();
    }

    /** Return the declared type of this member, which
     *  must be a field or type.
     *  If it is a type member, that type itself is returned.
     */
    public Class<?> getFieldType() {
        if (type == null) {
            expandFromVM();
            if (type == null) {
                return null;
            }
        }
        if (isInvocable()) {
            throw newIllegalArgumentException("not a field or nested class, no simple type");
        }

        {
            // Get a snapshot of type which doesn't get changed by racing threads.
            final Object type = this.type;
            if (type instanceof Class<?>) {
                return (Class<?>) type;
            }
        }

        // type is not a Class yet.  Convert it thread-safely.
        synchronized (this) {
            if (type instanceof String) {
                String sig = (String) type;
                MethodType mtype = MethodType.fromMethodDescriptorString("()"+sig, getClassLoader());
                Class<?> res = mtype.returnType();
                type = res;
            }
            // Make sure type is a Class for racing threads.
            assert type instanceof Class<?> : "bad field type " + type;
        }
        return (Class<?>) type;
    }

    /** Utility method to produce either the method type or field type of this member. */
    public Object getType() {
        return (isInvocable() ? getMethodType() : getFieldType());
    }

    /** Utility method to produce the signature of this member,
     *  used within the class file format to describe its type.
     */
    public String getSignature() {
        if (type == null) {
            expandFromVM();
            if (type == null) {
                return null;
            }
        }
        if (isInvocable())
            return BytecodeDescriptor.unparse(getMethodType());
        else
            return BytecodeDescriptor.unparse(getFieldType());
    }

    /** Return the modifier flags of this member.
     *  @see Modifier
     */
    public int getModifiers() {
        return (flags & RECOGNIZED_MODIFIERS);
    }

    /** Return the reference kind of this member, or zero if none.
     */
    public byte getReferenceKind() {
        return (byte) ((flags >>> MN_REFERENCE_KIND_SHIFT) & MN_REFERENCE_KIND_MASK);
    }
    private boolean referenceKindIsConsistent() {
        byte refKind = getReferenceKind();
        if (refKind == REF_NONE)  return isType();
        if (isField()) {
            assert(staticIsConsistent());
            assert(java.lang.invoke.MethodHandleNatives.refKindIsField(refKind));
        } else if (isConstructor()) {
            assert(refKind == REF_newInvokeSpecial || refKind == REF_invokeSpecial);
        } else if (isMethod()) {
            assert(staticIsConsistent());
            assert(java.lang.invoke.MethodHandleNatives.refKindIsMethod(refKind));
            if (clazz.isInterface())
                assert(refKind == REF_invokeInterface ||
                       refKind == REF_invokeStatic    ||
                       refKind == REF_invokeSpecial   ||
                       refKind == REF_invokeVirtual && isObjectPublicMethod());
        } else {
            assert(false);
        }
        return true;
    }
    private boolean isObjectPublicMethod() {
        if (clazz == Object.class)  return true;
        MethodType mtype = getMethodType();
        if (name.equals("toString") && mtype.returnType() == String.class && mtype.parameterCount() == 0)
            return true;
        if (name.equals("hashCode") && mtype.returnType() == int.class && mtype.parameterCount() == 0)
            return true;
        if (name.equals("equals") && mtype.returnType() == boolean.class && mtype.parameterCount() == 1 && mtype.parameterType(0) == Object.class)
            return true;
        return false;
    }
    /*non-public*/ boolean referenceKindIsConsistentWith(int originalRefKind) {
        int refKind = getReferenceKind();
        if (refKind == originalRefKind)  return true;
        switch (originalRefKind) {
        case REF_invokeInterface:
            // Looking up an interface method, can get (e.g.) Object.hashCode
            assert(refKind == REF_invokeVirtual ||
                   refKind == REF_invokeSpecial) : this;
            return true;
        case REF_invokeVirtual:
        case REF_newInvokeSpecial:
            // Looked up a virtual, can get (e.g.) final String.hashCode.
            assert(refKind == REF_invokeSpecial) : this;
            return true;
        }
        assert(false) : this+" != "+ java.lang.invoke.MethodHandleNatives.refKindName((byte)originalRefKind);
        return true;
    }
    private boolean staticIsConsistent() {
        byte refKind = getReferenceKind();
        return java.lang.invoke.MethodHandleNatives.refKindIsStatic(refKind) == isStatic() || getModifiers() == 0;
    }
    private boolean vminfoIsConsistent() {
        byte refKind = getReferenceKind();
        assert(isResolved());  // else don't call
        Object vminfo = java.lang.invoke.MethodHandleNatives.getMemberVMInfo(this);
        assert(vminfo instanceof Object[]);
        long vmindex = (Long) ((Object[])vminfo)[0];
        Object vmtarget = ((Object[])vminfo)[1];
        if (java.lang.invoke.MethodHandleNatives.refKindIsField(refKind)) {
            assert(vmindex >= 0) : vmindex + ":" + this;
            assert(vmtarget instanceof Class);
        } else {
            if (java.lang.invoke.MethodHandleNatives.refKindDoesDispatch(refKind))
                assert(vmindex >= 0) : vmindex + ":" + this;
            else
                assert(vmindex < 0) : vmindex;
            assert(vmtarget instanceof java.lang.invoke.MemberName) : vmtarget + " in " + this;
        }
        return true;
    }

    private java.lang.invoke.MemberName changeReferenceKind(byte refKind, byte oldKind) {
        assert(getReferenceKind() == oldKind);
        assert(java.lang.invoke.MethodHandleNatives.refKindIsValid(refKind));
        flags += (((int)refKind - oldKind) << MN_REFERENCE_KIND_SHIFT);
//        if (isConstructor() && refKind != REF_newInvokeSpecial)
//            flags += (IS_METHOD - IS_CONSTRUCTOR);
//        else if (refKind == REF_newInvokeSpecial && isMethod())
//            flags += (IS_CONSTRUCTOR - IS_METHOD);
        return this;
    }

    private boolean testFlags(int mask, int value) {
        return (flags & mask) == value;
    }
    private boolean testAllFlags(int mask) {
        return testFlags(mask, mask);
    }
    private boolean testAnyFlags(int mask) {
        return !testFlags(mask, 0);
    }

    /** Utility method to query if this member is a method handle invocation (invoke or invokeExact). */
    public boolean isMethodHandleInvoke() {
        final int bits = MH_INVOKE_MODS;
        final int negs = Modifier.STATIC;
        if (testFlags(bits | negs, bits) &&
            clazz == MethodHandle.class) {
            return isMethodHandleInvokeName(name);
        }
        return false;
    }
    public static boolean isMethodHandleInvokeName(String name) {
        return name.equals("invoke") || name.equals("invokeExact");
    }
    private static final int MH_INVOKE_MODS = Modifier.NATIVE | Modifier.FINAL | Modifier.PUBLIC;

    /** Utility method to query the modifier flags of this member. */
    public boolean isStatic() {
        return Modifier.isStatic(flags);
    }
    /** Utility method to query the modifier flags of this member. */
    public boolean isPublic() {
        return Modifier.isPublic(flags);
    }
    /** Utility method to query the modifier flags of this member. */
    public boolean isPrivate() {
        return Modifier.isPrivate(flags);
    }
    /** Utility method to query the modifier flags of this member. */
    public boolean isProtected() {
        return Modifier.isProtected(flags);
    }
    /** Utility method to query the modifier flags of this member. */
    public boolean isFinal() {
        return Modifier.isFinal(flags);
    }
    /** Utility method to query whether this member or its defining class is final. */
    public boolean canBeStaticallyBound() {
        return Modifier.isFinal(flags | clazz.getModifiers());
    }
    /** Utility method to query the modifier flags of this member. */
    public boolean isVolatile() {
        return Modifier.isVolatile(flags);
    }
    /** Utility method to query the modifier flags of this member. */
    public boolean isAbstract() {
        return Modifier.isAbstract(flags);
    }
    /** Utility method to query the modifier flags of this member. */
    public boolean isNative() {
        return Modifier.isNative(flags);
    }
    // let the rest (native, volatile, transient, etc.) be tested via Modifier.isFoo

    // unofficial modifier flags, used by HotSpot:
    static final int BRIDGE    = 0x00000040;
    static final int VARARGS   = 0x00000080;
    static final int SYNTHETIC = 0x00001000;
    static final int ANNOTATION= 0x00002000;
    static final int ENUM      = 0x00004000;
    /** Utility method to query the modifier flags of this member; returns false if the member is not a method. */
    public boolean isBridge() {
        return testAllFlags(IS_METHOD | BRIDGE);
    }
    /** Utility method to query the modifier flags of this member; returns false if the member is not a method. */
    public boolean isVarargs() {
        return testAllFlags(VARARGS) && isInvocable();
    }
    /** Utility method to query the modifier flags of this member; returns false if the member is not a method. */
    public boolean isSynthetic() {
        return testAllFlags(SYNTHETIC);
    }

    static final String CONSTRUCTOR_NAME = "<init>";  // the ever-popular

    // modifiers exported by the JVM:
    static final int RECOGNIZED_MODIFIERS = 0xFFFF;

    // private flags, not part of RECOGNIZED_MODIFIERS:
    static final int
            IS_METHOD        = MN_IS_METHOD,        // method (not constructor)
            IS_CONSTRUCTOR   = MN_IS_CONSTRUCTOR,   // constructor
            IS_FIELD         = MN_IS_FIELD,         // field
            IS_TYPE          = MN_IS_TYPE,          // nested type
            CALLER_SENSITIVE = MN_CALLER_SENSITIVE; // @CallerSensitive annotation detected

    static final int ALL_ACCESS = Modifier.PUBLIC | Modifier.PRIVATE | Modifier.PROTECTED;
    static final int ALL_KINDS = IS_METHOD | IS_CONSTRUCTOR | IS_FIELD | IS_TYPE;
    static final int IS_INVOCABLE = IS_METHOD | IS_CONSTRUCTOR;
    static final int IS_FIELD_OR_METHOD = IS_METHOD | IS_FIELD;
    static final int SEARCH_ALL_SUPERS = MN_SEARCH_SUPERCLASSES | MN_SEARCH_INTERFACES;

    /** Utility method to query whether this member is a method or constructor. */
    public boolean isInvocable() {
        return testAnyFlags(IS_INVOCABLE);
    }
    /** Utility method to query whether this member is a method, constructor, or field. */
    public boolean isFieldOrMethod() {
        return testAnyFlags(IS_FIELD_OR_METHOD);
    }
    /** Query whether this member is a method. */
    public boolean isMethod() {
        return testAllFlags(IS_METHOD);
    }
    /** Query whether this member is a constructor. */
    public boolean isConstructor() {
        return testAllFlags(IS_CONSTRUCTOR);
    }
    /** Query whether this member is a field. */
    public boolean isField() {
        return testAllFlags(IS_FIELD);
    }
    /** Query whether this member is a type. */
    public boolean isType() {
        return testAllFlags(IS_TYPE);
    }
    /** Utility method to query whether this member is neither public, private, nor protected. */
    public boolean isPackage() {
        return !testAnyFlags(ALL_ACCESS);
    }
    /** Query whether this member has a CallerSensitive annotation. */
    public boolean isCallerSensitive() {
        return testAllFlags(CALLER_SENSITIVE);
    }

    /** Utility method to query whether this member is accessible from a given lookup class. */
    public boolean isAccessibleFrom(Class<?> lookupClass) {
        return VerifyAccess.isMemberAccessible(this.getDeclaringClass(), this.getDeclaringClass(), flags,
                                               lookupClass, ALL_ACCESS|MethodHandles.Lookup.PACKAGE);
    }

    /** Initialize a query.   It is not resolved. */
    private void init(Class<?> defClass, String name, Object type, int flags) {
        // defining class is allowed to be null (for a naked name/type pair)
        //name.toString();  // null check
        //type.equals(type);  // null check
        // fill in fields:
        this.clazz = defClass;
        this.name = name;
        this.type = type;
        this.flags = flags;
        assert(testAnyFlags(ALL_KINDS));
        assert(this.resolution == null);  // nobody should have touched this yet
        //assert(referenceKindIsConsistent());  // do this after resolution
    }

    /**
     * Calls down to the VM to fill in the fields.  This method is
     * synchronized to avoid racing calls.
     */
    private void expandFromVM() {
        if (type != null) {
            return;
        }
        if (!isResolved()) {
            return;
        }
        java.lang.invoke.MethodHandleNatives.expand(this);
    }

    // Capturing information from the Core Reflection API:
    private static int flagsMods(int flags, int mods, byte refKind) {
        assert((flags & RECOGNIZED_MODIFIERS) == 0);
        assert((mods & ~RECOGNIZED_MODIFIERS) == 0);
        assert((refKind & ~MN_REFERENCE_KIND_MASK) == 0);
        return flags | mods | (refKind << MN_REFERENCE_KIND_SHIFT);
    }
    /** Create a name for the given reflected method.  The resulting name will be in a resolved state. */
    public MemberName(Method m) {
        this(m, false);
    }
    @SuppressWarnings("LeakingThisInConstructor")
    public MemberName(Method m, boolean wantSpecial) {
        m.getClass();  // NPE check
        // fill in vmtarget, vmindex while we have m in hand:
        java.lang.invoke.MethodHandleNatives.init(this, m);
        if (clazz == null) {  // MHN.init failed
            if (m.getDeclaringClass() == MethodHandle.class &&
                isMethodHandleInvokeName(m.getName())) {
                // The JVM did not reify this signature-polymorphic instance.
                // Need a special case here.
                // See comments on MethodHandleNatives.linkMethod.
                MethodType type = MethodType.methodType(m.getReturnType(), m.getParameterTypes());
                int flags = flagsMods(IS_METHOD, m.getModifiers(), REF_invokeVirtual);
                init(MethodHandle.class, m.getName(), type, flags);
                if (isMethodHandleInvoke())
                    return;
            }
            throw new LinkageError(m.toString());
        }
        assert(isResolved() && this.clazz != null);
        this.name = m.getName();
        if (this.type == null)
            this.type = new Object[] { m.getReturnType(), m.getParameterTypes() };
        if (wantSpecial) {
            if (isAbstract())
                throw new AbstractMethodError(this.toString());
            if (getReferenceKind() == REF_invokeVirtual)
                changeReferenceKind(REF_invokeSpecial, REF_invokeVirtual);
            else if (getReferenceKind() == REF_invokeInterface)
                // invokeSpecial on a default method
                changeReferenceKind(REF_invokeSpecial, REF_invokeInterface);
        }
    }
    public java.lang.invoke.MemberName asSpecial() {
        switch (getReferenceKind()) {
        case REF_invokeSpecial:     return this;
        case REF_invokeVirtual:     return clone().changeReferenceKind(REF_invokeSpecial, REF_invokeVirtual);
        case REF_invokeInterface:   return clone().changeReferenceKind(REF_invokeSpecial, REF_invokeInterface);
        case REF_newInvokeSpecial:  return clone().changeReferenceKind(REF_invokeSpecial, REF_newInvokeSpecial);
        }
        throw new IllegalArgumentException(this.toString());
    }
    /** If this MN is not REF_newInvokeSpecial, return a clone with that ref. kind.
     *  In that case it must already be REF_invokeSpecial.
     */
    public java.lang.invoke.MemberName asConstructor() {
        switch (getReferenceKind()) {
        case REF_invokeSpecial:     return clone().changeReferenceKind(REF_newInvokeSpecial, REF_invokeSpecial);
        case REF_newInvokeSpecial:  return this;
        }
        throw new IllegalArgumentException(this.toString());
    }
    /** If this MN is a REF_invokeSpecial, return a clone with the "normal" kind
     *  REF_invokeVirtual; also switch either to REF_invokeInterface if clazz.isInterface.
     *  The end result is to get a fully virtualized version of the MN.
     *  (Note that resolving in the JVM will sometimes devirtualize, changing
     *  REF_invokeVirtual of a final to REF_invokeSpecial, and REF_invokeInterface
     *  in some corner cases to either of the previous two; this transform
     *  undoes that change under the assumption that it occurred.)
     */
    public java.lang.invoke.MemberName asNormalOriginal() {
        byte normalVirtual = clazz.isInterface() ? REF_invokeInterface : REF_invokeVirtual;
        byte refKind = getReferenceKind();
        byte newRefKind = refKind;
        java.lang.invoke.MemberName result = this;
        switch (refKind) {
        case REF_invokeInterface:
        case REF_invokeVirtual:
        case REF_invokeSpecial:
            newRefKind = normalVirtual;
            break;
        }
        if (newRefKind == refKind)
            return this;
        result = clone().changeReferenceKind(newRefKind, refKind);
        assert(this.referenceKindIsConsistentWith(result.getReferenceKind()));
        return result;
    }
    /** Create a name for the given reflected constructor.  The resulting name will be in a resolved state. */
    @SuppressWarnings("LeakingThisInConstructor")
    public MemberName(Constructor<?> ctor) {
        ctor.getClass();  // NPE check
        // fill in vmtarget, vmindex while we have ctor in hand:
        java.lang.invoke.MethodHandleNatives.init(this, ctor);
        assert(isResolved() && this.clazz != null);
        this.name = CONSTRUCTOR_NAME;
        if (this.type == null)
            this.type = new Object[] { void.class, ctor.getParameterTypes() };
    }
    /** Create a name for the given reflected field.  The resulting name will be in a resolved state.
     */
    public MemberName(Field fld) {
        this(fld, false);
    }
    @SuppressWarnings("LeakingThisInConstructor")
    public MemberName(Field fld, boolean makeSetter) {
        fld.getClass();  // NPE check
        // fill in vmtarget, vmindex while we have fld in hand:
        java.lang.invoke.MethodHandleNatives.init(this, fld);
        assert(isResolved() && this.clazz != null);
        this.name = fld.getName();
        this.type = fld.getType();
        assert((REF_putStatic - REF_getStatic) == (REF_putField - REF_getField));
        byte refKind = this.getReferenceKind();
        assert(refKind == (isStatic() ? REF_getStatic : REF_getField));
        if (makeSetter) {
            changeReferenceKind((byte)(refKind + (REF_putStatic - REF_getStatic)), refKind);
        }
    }
    public boolean isGetter() {
        return java.lang.invoke.MethodHandleNatives.refKindIsGetter(getReferenceKind());
    }
    public boolean isSetter() {
        return java.lang.invoke.MethodHandleNatives.refKindIsSetter(getReferenceKind());
    }
    public java.lang.invoke.MemberName asSetter() {
        byte refKind = getReferenceKind();
        assert(java.lang.invoke.MethodHandleNatives.refKindIsGetter(refKind));
        assert((REF_putStatic - REF_getStatic) == (REF_putField - REF_getField));
        byte setterRefKind = (byte)(refKind + (REF_putField - REF_getField));
        return clone().changeReferenceKind(setterRefKind, refKind);
    }
    /** Create a name for the given class.  The resulting name will be in a resolved state. */
    public MemberName(Class<?> type) {
        init(type.getDeclaringClass(), type.getSimpleName(), type,
                flagsMods(IS_TYPE, type.getModifiers(), REF_NONE));
        initResolved(true);
    }

    /**
     * Create a name for a signature-polymorphic invoker.
     * This is a placeholder for a signature-polymorphic instance
     * (of MH.invokeExact, etc.) that the JVM does not reify.
     * See comments on {@link MethodHandleNatives#linkMethod}.
     */
    static java.lang.invoke.MemberName makeMethodHandleInvoke(String name, MethodType type) {
        return makeMethodHandleInvoke(name, type, MH_INVOKE_MODS | SYNTHETIC);
    }
    static java.lang.invoke.MemberName makeMethodHandleInvoke(String name, MethodType type, int mods) {
        java.lang.invoke.MemberName mem = new java.lang.invoke.MemberName(MethodHandle.class, name, type, REF_invokeVirtual);
        mem.flags |= mods;  // it's not resolved, but add these modifiers anyway
        assert(mem.isMethodHandleInvoke()) : mem;
        return mem;
    }

    // bare-bones constructor; the JVM will fill it in
    MemberName() { }

    // locally useful cloner
    @Override protected java.lang.invoke.MemberName clone() {
        try {
            return (java.lang.invoke.MemberName) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw newInternalError(ex);
        }
     }

    /** Get the definition of this member name.
     *  This may be in a super-class of the declaring class of this member.
     */
    public java.lang.invoke.MemberName getDefinition() {
        if (!isResolved())  throw new IllegalStateException("must be resolved: "+this);
        if (isType())  return this;
        java.lang.invoke.MemberName res = this.clone();
        res.clazz = null;
        res.type = null;
        res.name = null;
        res.resolution = res;
        res.expandFromVM();
        assert(res.getName().equals(this.getName()));
        return res;
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz, getReferenceKind(), name, getType());
    }
    @Override
    public boolean equals(Object that) {
        return (that instanceof java.lang.invoke.MemberName && this.equals((java.lang.invoke.MemberName)that));
    }

    /** Decide if two member names have exactly the same symbolic content.
     *  Does not take into account any actual class members, so even if
     *  two member names resolve to the same actual member, they may
     *  be distinct references.
     */
    public boolean equals(java.lang.invoke.MemberName that) {
        if (this == that)  return true;
        if (that == null)  return false;
        return this.clazz == that.clazz
                && this.getReferenceKind() == that.getReferenceKind()
                && Objects.equals(this.name, that.name)
                && Objects.equals(this.getType(), that.getType());
    }

    // Construction from symbolic parts, for queries:
    /** Create a field or type name from the given components:
     *  Declaring class, name, type, reference kind.
     *  The declaring class may be supplied as null if this is to be a bare name and type.
     *  The resulting name will in an unresolved state.
     */
    public MemberName(Class<?> defClass, String name, Class<?> type, byte refKind) {
        init(defClass, name, type, flagsMods(IS_FIELD, 0, refKind));
        initResolved(false);
    }
    /** Create a field or type name from the given components:  Declaring class, name, type.
     *  The declaring class may be supplied as null if this is to be a bare name and type.
     *  The modifier flags default to zero.
     *  The resulting name will in an unresolved state.
     */
    public MemberName(Class<?> defClass, String name, Class<?> type, Void unused) {
        this(defClass, name, type, REF_NONE);
        initResolved(false);
    }
    /** Create a method or constructor name from the given components:  Declaring class, name, type, modifiers.
     *  It will be a constructor if and only if the name is {@code "&lt;init&gt;"}.
     *  The declaring class may be supplied as null if this is to be a bare name and type.
     *  The last argument is optional, a boolean which requests REF_invokeSpecial.
     *  The resulting name will in an unresolved state.
     */
    public MemberName(Class<?> defClass, String name, MethodType type, byte refKind) {
        int initFlags = (name != null && name.equals(CONSTRUCTOR_NAME) ? IS_CONSTRUCTOR : IS_METHOD);
        init(defClass, name, type, flagsMods(initFlags, 0, refKind));
        initResolved(false);
    }
    /** Create a method, constructor, or field name from the given components:
     *  Reference kind, declaring class, name, type.
     */
    public MemberName(byte refKind, Class<?> defClass, String name, Object type) {
        int kindFlags;
        if (java.lang.invoke.MethodHandleNatives.refKindIsField(refKind)) {
            kindFlags = IS_FIELD;
            if (!(type instanceof Class))
                throw newIllegalArgumentException("not a field type");
        } else if (java.lang.invoke.MethodHandleNatives.refKindIsMethod(refKind)) {
            kindFlags = IS_METHOD;
            if (!(type instanceof MethodType))
                throw newIllegalArgumentException("not a method type");
        } else if (refKind == REF_newInvokeSpecial) {
            kindFlags = IS_CONSTRUCTOR;
            if (!(type instanceof MethodType) ||
                !CONSTRUCTOR_NAME.equals(name))
                throw newIllegalArgumentException("not a constructor type or name");
        } else {
            throw newIllegalArgumentException("bad reference kind "+refKind);
        }
        init(defClass, name, type, flagsMods(kindFlags, 0, refKind));
        initResolved(false);
    }
    /** Query whether this member name is resolved to a non-static, non-final method.
     */
    public boolean hasReceiverTypeDispatch() {
        return java.lang.invoke.MethodHandleNatives.refKindDoesDispatch(getReferenceKind());
    }

    /** Query whether this member name is resolved.
     *  A resolved member name is one for which the JVM has found
     *  a method, constructor, field, or type binding corresponding exactly to the name.
     *  (Document?)
     */
    public boolean isResolved() {
        return resolution == null;
    }

    private void initResolved(boolean isResolved) {
        assert(this.resolution == null);  // not initialized yet!
        if (!isResolved)
            this.resolution = this;
        assert(isResolved() == isResolved);
    }

    void checkForTypeAlias() {
        if (isInvocable()) {
            MethodType type;
            if (this.type instanceof MethodType)
                type = (MethodType) this.type;
            else
                this.type = type = getMethodType();
            if (type.erase() == type)  return;
            if (VerifyAccess.isTypeVisible(type, clazz))  return;
            throw new LinkageError("bad method type alias: "+type+" not visible from "+clazz);
        } else {
            Class<?> type;
            if (this.type instanceof Class<?>)
                type = (Class<?>) this.type;
            else
                this.type = type = getFieldType();
            if (VerifyAccess.isTypeVisible(type, clazz))  return;
            throw new LinkageError("bad field type alias: "+type+" not visible from "+clazz);
        }
    }


    /** Produce a string form of this member name.
     *  For types, it is simply the type's own string (as reported by {@code toString}).
     *  For fields, it is {@code "DeclaringClass.name/type"}.
     *  For methods and constructors, it is {@code "DeclaringClass.name(ptype...)rtype"}.
     *  If the declaring class is null, the prefix {@code "DeclaringClass."} is omitted.
     *  If the member is unresolved, a prefix {@code "*."} is prepended.
     */
    @SuppressWarnings("LocalVariableHidesMemberVariable")
    @Override
    public String toString() {
        if (isType())
            return type.toString();  // class java.lang.String
        // else it is a field, method, or constructor
        StringBuilder buf = new StringBuilder();
        if (getDeclaringClass() != null) {
            buf.append(getName(clazz));
            buf.append('.');
        }
        String name = getName();
        buf.append(name == null ? "*" : name);
        Object type = getType();
        if (!isInvocable()) {
            buf.append('/');
            buf.append(type == null ? "*" : getName(type));
        } else {
            buf.append(type == null ? "(*)*" : getName(type));
        }
        byte refKind = getReferenceKind();
        if (refKind != REF_NONE) {
            buf.append('/');
            buf.append(java.lang.invoke.MethodHandleNatives.refKindName(refKind));
        }
        //buf.append("#").append(System.identityHashCode(this));
        return buf.toString();
    }
    private static String getName(Object obj) {
        if (obj instanceof Class<?>)
            return ((Class<?>)obj).getName();
        return String.valueOf(obj);
    }

    public IllegalAccessException makeAccessException(String message, Object from) {
        message = message + ": "+ toString();
        if (from != null)  message += ", from " + from;
        return new IllegalAccessException(message);
    }
    private String message() {
        if (isResolved())
            return "no access";
        else if (isConstructor())
            return "no such constructor";
        else if (isMethod())
            return "no such method";
        else
            return "no such field";
    }
    public ReflectiveOperationException makeAccessException() {
        String message = message() + ": "+ toString();
        ReflectiveOperationException ex;
        if (isResolved() || !(resolution instanceof NoSuchMethodError ||
                              resolution instanceof NoSuchFieldError))
            ex = new IllegalAccessException(message);
        else if (isConstructor())
            ex = new NoSuchMethodException(message);
        else if (isMethod())
            ex = new NoSuchMethodException(message);
        else
            ex = new NoSuchFieldException(message);
        if (resolution instanceof Throwable)
            ex.initCause((Throwable) resolution);
        return ex;
    }

    /** Actually making a query requires an access check. */
    /*non-public*/ static Factory getFactory() {
        return Factory.INSTANCE;
    }
    /** A factory type for resolving member names with the help of the VM.
     *  TBD: Define access-safe public constructors for this factory.
     */
    /*non-public*/ static class Factory {
        private Factory() { } // singleton pattern
        static Factory INSTANCE = new Factory();

        private static int ALLOWED_FLAGS = ALL_KINDS;

        /// Queries
        List<java.lang.invoke.MemberName> getMembers(Class<?> defc,
                                                     String matchName, Object matchType,
                                                     int matchFlags, Class<?> lookupClass) {
            matchFlags &= ALLOWED_FLAGS;
            String matchSig = null;
            if (matchType != null) {
                matchSig = BytecodeDescriptor.unparse(matchType);
                if (matchSig.startsWith("("))
                    matchFlags &= ~(ALL_KINDS & ~IS_INVOCABLE);
                else
                    matchFlags &= ~(ALL_KINDS & ~IS_FIELD);
            }
            final int BUF_MAX = 0x2000;
            int len1 = matchName == null ? 10 : matchType == null ? 4 : 1;
            java.lang.invoke.MemberName[] buf = newMemberBuffer(len1);
            int totalCount = 0;
            ArrayList<java.lang.invoke.MemberName[]> bufs = null;
            int bufCount = 0;
            for (;;) {
                bufCount = java.lang.invoke.MethodHandleNatives.getMembers(defc,
                        matchName, matchSig, matchFlags,
                        lookupClass,
                        totalCount, buf);
                if (bufCount <= buf.length) {
                    if (bufCount < 0)  bufCount = 0;
                    totalCount += bufCount;
                    break;
                }
                // JVM returned to us with an intentional overflow!
                totalCount += buf.length;
                int excess = bufCount - buf.length;
                if (bufs == null)  bufs = new ArrayList<>(1);
                bufs.add(buf);
                int len2 = buf.length;
                len2 = Math.max(len2, excess);
                len2 = Math.max(len2, totalCount / 4);
                buf = newMemberBuffer(Math.min(BUF_MAX, len2));
            }
            ArrayList<java.lang.invoke.MemberName> result = new ArrayList<>(totalCount);
            if (bufs != null) {
                for (java.lang.invoke.MemberName[] buf0 : bufs) {
                    Collections.addAll(result, buf0);
                }
            }
            result.addAll(Arrays.asList(buf).subList(0, bufCount));
            // Signature matching is not the same as type matching, since
            // one signature might correspond to several types.
            // So if matchType is a Class or MethodType, refilter the results.
            if (matchType != null && matchType != matchSig) {
                for (Iterator<java.lang.invoke.MemberName> it = result.iterator(); it.hasNext();) {
                    java.lang.invoke.MemberName m = it.next();
                    if (!matchType.equals(m.getType()))
                        it.remove();
                }
            }
            return result;
        }
        /** Produce a resolved version of the given member.
         *  Super types are searched (for inherited members) if {@code searchSupers} is true.
         *  Access checking is performed on behalf of the given {@code lookupClass}.
         *  If lookup fails or access is not permitted, null is returned.
         *  Otherwise a fresh copy of the given member is returned, with modifier bits filled in.
         */
        private java.lang.invoke.MemberName resolve(byte refKind, java.lang.invoke.MemberName ref, Class<?> lookupClass) {
            java.lang.invoke.MemberName m = ref.clone();  // JVM will side-effect the ref
            assert(refKind == m.getReferenceKind());
            try {
                m = java.lang.invoke.MethodHandleNatives.resolve(m, lookupClass);
                m.checkForTypeAlias();
                m.resolution = null;
            } catch (LinkageError ex) {
                // JVM reports that the "bytecode behavior" would get an error
                assert(!m.isResolved());
                m.resolution = ex;
                return m;
            }
            assert(m.referenceKindIsConsistent());
            m.initResolved(true);
            assert(m.vminfoIsConsistent());
            return m;
        }
        /** Produce a resolved version of the given member.
         *  Super types are searched (for inherited members) if {@code searchSupers} is true.
         *  Access checking is performed on behalf of the given {@code lookupClass}.
         *  If lookup fails or access is not permitted, a {@linkplain ReflectiveOperationException} is thrown.
         *  Otherwise a fresh copy of the given member is returned, with modifier bits filled in.
         */
        public
        <NoSuchMemberException extends ReflectiveOperationException>
        java.lang.invoke.MemberName resolveOrFail(byte refKind, java.lang.invoke.MemberName m, Class<?> lookupClass,
                                                  Class<NoSuchMemberException> nsmClass)
                throws IllegalAccessException, NoSuchMemberException {
            java.lang.invoke.MemberName result = resolve(refKind, m, lookupClass);
            if (result.isResolved())
                return result;
            ReflectiveOperationException ex = result.makeAccessException();
            if (ex instanceof IllegalAccessException)  throw (IllegalAccessException) ex;
            throw nsmClass.cast(ex);
        }
        /** Produce a resolved version of the given member.
         *  Super types are searched (for inherited members) if {@code searchSupers} is true.
         *  Access checking is performed on behalf of the given {@code lookupClass}.
         *  If lookup fails or access is not permitted, return null.
         *  Otherwise a fresh copy of the given member is returned, with modifier bits filled in.
         */
        public java.lang.invoke.MemberName resolveOrNull(byte refKind, java.lang.invoke.MemberName m, Class<?> lookupClass) {
            java.lang.invoke.MemberName result = resolve(refKind, m, lookupClass);
            if (result.isResolved())
                return result;
            return null;
        }
        /** Return a list of all methods defined by the given class.
         *  Super types are searched (for inherited members) if {@code searchSupers} is true.
         *  Access checking is performed on behalf of the given {@code lookupClass}.
         *  Inaccessible members are not added to the last.
         */
        public List<java.lang.invoke.MemberName> getMethods(Class<?> defc, boolean searchSupers,
                                                            Class<?> lookupClass) {
            return getMethods(defc, searchSupers, null, null, lookupClass);
        }
        /** Return a list of matching methods defined by the given class.
         *  Super types are searched (for inherited members) if {@code searchSupers} is true.
         *  Returned methods will match the name (if not null) and the type (if not null).
         *  Access checking is performed on behalf of the given {@code lookupClass}.
         *  Inaccessible members are not added to the last.
         */
        public List<java.lang.invoke.MemberName> getMethods(Class<?> defc, boolean searchSupers,
                                                            String name, MethodType type, Class<?> lookupClass) {
            int matchFlags = IS_METHOD | (searchSupers ? SEARCH_ALL_SUPERS : 0);
            return getMembers(defc, name, type, matchFlags, lookupClass);
        }
        /** Return a list of all constructors defined by the given class.
         *  Access checking is performed on behalf of the given {@code lookupClass}.
         *  Inaccessible members are not added to the last.
         */
        public List<java.lang.invoke.MemberName> getConstructors(Class<?> defc, Class<?> lookupClass) {
            return getMembers(defc, null, null, IS_CONSTRUCTOR, lookupClass);
        }
        /** Return a list of all fields defined by the given class.
         *  Super types are searched (for inherited members) if {@code searchSupers} is true.
         *  Access checking is performed on behalf of the given {@code lookupClass}.
         *  Inaccessible members are not added to the last.
         */
        public List<java.lang.invoke.MemberName> getFields(Class<?> defc, boolean searchSupers,
                                                           Class<?> lookupClass) {
            return getFields(defc, searchSupers, null, null, lookupClass);
        }
        /** Return a list of all fields defined by the given class.
         *  Super types are searched (for inherited members) if {@code searchSupers} is true.
         *  Returned fields will match the name (if not null) and the type (if not null).
         *  Access checking is performed on behalf of the given {@code lookupClass}.
         *  Inaccessible members are not added to the last.
         */
        public List<java.lang.invoke.MemberName> getFields(Class<?> defc, boolean searchSupers,
                                                           String name, Class<?> type, Class<?> lookupClass) {
            int matchFlags = IS_FIELD | (searchSupers ? SEARCH_ALL_SUPERS : 0);
            return getMembers(defc, name, type, matchFlags, lookupClass);
        }
        /** Return a list of all nested types defined by the given class.
         *  Super types are searched (for inherited members) if {@code searchSupers} is true.
         *  Access checking is performed on behalf of the given {@code lookupClass}.
         *  Inaccessible members are not added to the last.
         */
        public List<java.lang.invoke.MemberName> getNestedTypes(Class<?> defc, boolean searchSupers,
                                                                Class<?> lookupClass) {
            int matchFlags = IS_TYPE | (searchSupers ? SEARCH_ALL_SUPERS : 0);
            return getMembers(defc, null, null, matchFlags, lookupClass);
        }
        private static java.lang.invoke.MemberName[] newMemberBuffer(int length) {
            java.lang.invoke.MemberName[] buf = new java.lang.invoke.MemberName[length];
            // fill the buffer with dummy structs for the JVM to fill in
            for (int i = 0; i < length; i++)
                buf[i] = new java.lang.invoke.MemberName();
            return buf;
        }
    }

//    static {
//        System.out.println("Hello world!  My methods are:");
//        System.out.println(Factory.INSTANCE.getMethods(MemberName.class, true, null));
//    }
}
