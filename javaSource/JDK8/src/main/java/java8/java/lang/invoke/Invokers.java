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

import sun.invoke.empty.Empty;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.WrongMethodTypeException;
import java.util.Arrays;

import static java.lang.invoke.LambdaForm.Name;
import static java.lang.invoke.LambdaForm.NamedFunction;
import static java.lang.invoke.MethodHandleNatives.Constants.REF_invokeVirtual;
import static java.lang.invoke.MethodHandleStatics.newInternalError;
import static java.lang.invoke.MethodHandles.Lookup.IMPL_LOOKUP;

/**
 * Construction and caching of often-used invokers.
 * @author jrose
 */
class Invokers {
    // exact type (sans leading taget MH) for the outgoing call
    private final MethodType targetType;

    // FIXME: Get rid of the invokers that are not useful.

    // exact invoker for the outgoing call
    private /*lazy*/ MethodHandle exactInvoker;
    private /*lazy*/ MethodHandle basicInvoker;  // invokeBasic (unchecked exact)

    // erased (partially untyped but with primitives) invoker for the outgoing call
    // FIXME: get rid of
    private /*lazy*/ MethodHandle erasedInvoker;
    // FIXME: get rid of
    /*lazy*/ MethodHandle erasedInvokerWithDrops;  // for InvokeGeneric

    // general invoker for the outgoing call
    private /*lazy*/ MethodHandle generalInvoker;

    // general invoker for the outgoing call, uses varargs
    private /*lazy*/ MethodHandle varargsInvoker;

    // general invoker for the outgoing call; accepts a trailing Object[]
    private final /*lazy*/ MethodHandle[] spreadInvokers;

    // invoker for an unbound callsite
    private /*lazy*/ MethodHandle uninitializedCallSite;

    /** Compute and cache information common to all collecting adapters
     *  that implement members of the erasure-family of the given erased type.
     */
    /*non-public*/ Invokers(MethodType targetType) {
        this.targetType = targetType;
        this.spreadInvokers = new MethodHandle[targetType.parameterCount()+1];
    }

    /*non-public*/ MethodHandle exactInvoker() {
        MethodHandle invoker = exactInvoker;
        if (invoker != null)  return invoker;
        invoker = makeExactOrGeneralInvoker(true);
        exactInvoker = invoker;
        return invoker;
    }

    /*non-public*/ MethodHandle generalInvoker() {
        MethodHandle invoker = generalInvoker;
        if (invoker != null)  return invoker;
        invoker = makeExactOrGeneralInvoker(false);
        generalInvoker = invoker;
        return invoker;
    }

    private MethodHandle makeExactOrGeneralInvoker(boolean isExact) {
        MethodType mtype = targetType;
        MethodType invokerType = mtype.invokerType();
        int which = (isExact ? MethodTypeForm.LF_EX_INVOKER : MethodTypeForm.LF_GEN_INVOKER);
        java.lang.invoke.LambdaForm lform = invokeHandleForm(mtype, false, which);
        MethodHandle invoker = java.lang.invoke.BoundMethodHandle.bindSingle(invokerType, lform, mtype);
        String whichName = (isExact ? "invokeExact" : "invoke");
        invoker = invoker.withInternalMemberName(MemberName.makeMethodHandleInvoke(whichName, mtype));
        assert(checkInvoker(invoker));
        maybeCompileToBytecode(invoker);
        return invoker;
    }

    /** If the target type seems to be common enough, eagerly compile the invoker to bytecodes. */
    private void maybeCompileToBytecode(MethodHandle invoker) {
        final int EAGER_COMPILE_ARITY_LIMIT = 10;
        if (targetType == targetType.erase() &&
            targetType.parameterCount() < EAGER_COMPILE_ARITY_LIMIT) {
            invoker.form.compileToBytecode();
        }
    }

    /*non-public*/ MethodHandle basicInvoker() {
        MethodHandle invoker = basicInvoker;
        if (invoker != null)  return invoker;
        MethodType basicType = targetType.basicType();
        if (basicType != targetType) {
            // double cache; not used significantly
            return basicInvoker = basicType.invokers().basicInvoker();
        }
        MemberName method = invokeBasicMethod(basicType);
        invoker = java.lang.invoke.DirectMethodHandle.make(method);
        assert(checkInvoker(invoker));
        basicInvoker = invoker;
        return invoker;
    }

    // This next one is called from LambdaForm.NamedFunction.<init>.
    /*non-public*/ static MemberName invokeBasicMethod(MethodType basicType) {
        assert(basicType == basicType.basicType());
        try {
            //Lookup.findVirtual(MethodHandle.class, name, type);
            return IMPL_LOOKUP.resolveOrFail(REF_invokeVirtual, MethodHandle.class, "invokeBasic", basicType);
        } catch (ReflectiveOperationException ex) {
            throw newInternalError("JVM cannot find invoker for "+basicType, ex);
        }
    }

    private boolean checkInvoker(MethodHandle invoker) {
        assert(targetType.invokerType().equals(invoker.type()))
                : Arrays.asList(targetType, targetType.invokerType(), invoker);
        assert(invoker.internalMemberName() == null ||
               invoker.internalMemberName().getMethodType().equals(targetType));
        assert(!invoker.isVarargsCollector());
        return true;
    }

    // FIXME: get rid of
    /*non-public*/ MethodHandle erasedInvoker() {
        MethodHandle xinvoker = exactInvoker();
        MethodHandle invoker = erasedInvoker;
        if (invoker != null)  return invoker;
        MethodType erasedType = targetType.erase();
        invoker = xinvoker.asType(erasedType.invokerType());
        erasedInvoker = invoker;
        return invoker;
    }

    /*non-public*/ MethodHandle spreadInvoker(int leadingArgCount) {
        MethodHandle vaInvoker = spreadInvokers[leadingArgCount];
        if (vaInvoker != null)  return vaInvoker;
        int spreadArgCount = targetType.parameterCount() - leadingArgCount;
        MethodType spreadInvokerType = targetType
            .replaceParameterTypes(leadingArgCount, targetType.parameterCount(), Object[].class);
        if (targetType.parameterSlotCount() <= MethodType.MAX_MH_INVOKER_ARITY) {
            // Factor sinvoker.invoke(mh, a) into ginvoker.asSpreader().invoke(mh, a)
            // where ginvoker.invoke(mh, a*) => mh.invoke(a*).
            MethodHandle genInvoker = generalInvoker();
            vaInvoker = genInvoker.asSpreader(Object[].class, spreadArgCount);
        } else {
            // Cannot build a general invoker here of type ginvoker.invoke(mh, a*[254]).
            // Instead, factor sinvoker.invoke(mh, a) into ainvoker.invoke(filter(mh), a)
            // where filter(mh) == mh.asSpreader(Object[], spreadArgCount)
            MethodHandle arrayInvoker = MethodHandles.exactInvoker(spreadInvokerType);
            MethodHandle makeSpreader;
            try {
                makeSpreader = IMPL_LOOKUP
                    .findVirtual(MethodHandle.class, "asSpreader",
                        MethodType.methodType(MethodHandle.class, Class.class, int.class));
            } catch (ReflectiveOperationException ex) {
                throw newInternalError(ex);
            }
            makeSpreader = MethodHandles.insertArguments(makeSpreader, 1, Object[].class, spreadArgCount);
            vaInvoker = MethodHandles.filterArgument(arrayInvoker, 0, makeSpreader);
        }
        assert(vaInvoker.type().equals(spreadInvokerType.invokerType()));
        maybeCompileToBytecode(vaInvoker);
        spreadInvokers[leadingArgCount] = vaInvoker;
        return vaInvoker;
    }

    /*non-public*/ MethodHandle varargsInvoker() {
        MethodHandle vaInvoker = varargsInvoker;
        if (vaInvoker != null)  return vaInvoker;
        vaInvoker = spreadInvoker(0).asType(MethodType.genericMethodType(0, true).invokerType());
        varargsInvoker = vaInvoker;
        return vaInvoker;
    }

    private static MethodHandle THROW_UCS = null;

    /*non-public*/ MethodHandle uninitializedCallSite() {
        MethodHandle invoker = uninitializedCallSite;
        if (invoker != null)  return invoker;
        if (targetType.parameterCount() > 0) {
            MethodType type0 = targetType.dropParameterTypes(0, targetType.parameterCount());
            java.lang.invoke.Invokers invokers0 = type0.invokers();
            invoker = MethodHandles.dropArguments(invokers0.uninitializedCallSite(),
                                                  0, targetType.parameterList());
            assert(invoker.type().equals(targetType));
            uninitializedCallSite = invoker;
            return invoker;
        }
        invoker = THROW_UCS;
        if (invoker == null) {
            try {
                THROW_UCS = invoker = IMPL_LOOKUP
                    .findStatic(CallSite.class, "uninitializedCallSite",
                                MethodType.methodType(Empty.class));
            } catch (ReflectiveOperationException ex) {
                throw newInternalError(ex);
            }
        }
        invoker = MethodHandles.explicitCastArguments(invoker, MethodType.methodType(targetType.returnType()));
        invoker = invoker.dropArguments(targetType, 0, targetType.parameterCount());
        assert(invoker.type().equals(targetType));
        uninitializedCallSite = invoker;
        return invoker;
    }

    public String toString() {
        return "Invokers"+targetType;
    }

    static MemberName methodHandleInvokeLinkerMethod(String name,
                                                     MethodType mtype,
                                                     Object[] appendixResult) {
        int which;
        switch (name) {
        case "invokeExact":  which = MethodTypeForm.LF_EX_LINKER; break;
        case "invoke":       which = MethodTypeForm.LF_GEN_LINKER; break;
        default:             throw new InternalError("not invoker: "+name);
        }
        java.lang.invoke.LambdaForm lform;
        if (mtype.parameterSlotCount() <= MethodType.MAX_MH_ARITY - MH_LINKER_ARG_APPENDED) {
            lform = invokeHandleForm(mtype, false, which);
            appendixResult[0] = mtype;
        } else {
            lform = invokeHandleForm(mtype, true, which);
        }
        return lform.vmentry;
    }

    // argument count to account for trailing "appendix value" (typically the mtype)
    private static final int MH_LINKER_ARG_APPENDED = 1;

    /** Returns an adapter for invokeExact or generic invoke, as a MH or constant pool linker.
     * If !customized, caller is responsible for supplying, during adapter execution,
     * a copy of the exact mtype.  This is because the adapter might be generalized to
     * a basic type.
     * @param mtype the caller's method type (either basic or full-custom)
     * @param customized whether to use a trailing appendix argument (to carry the mtype)
     * @param which bit-encoded 0x01 whether it is a CP adapter ("linker") or MHs.invoker value ("invoker");
     *                          0x02 whether it is for invokeExact or generic invoke
     */
    private static java.lang.invoke.LambdaForm invokeHandleForm(MethodType mtype, boolean customized, int which) {
        boolean isCached;
        if (!customized) {
            mtype = mtype.basicType();  // normalize Z to I, String to Object, etc.
            isCached = true;
        } else {
            isCached = false;  // maybe cache if mtype == mtype.basicType()
        }
        boolean isLinker, isGeneric;
        String debugName;
        switch (which) {
        case MethodTypeForm.LF_EX_LINKER:   isLinker = true;  isGeneric = false; debugName = "invokeExact_MT"; break;
        case MethodTypeForm.LF_EX_INVOKER:  isLinker = false; isGeneric = false; debugName = "exactInvoker"; break;
        case MethodTypeForm.LF_GEN_LINKER:  isLinker = true;  isGeneric = true;  debugName = "invoke_MT"; break;
        case MethodTypeForm.LF_GEN_INVOKER: isLinker = false; isGeneric = true;  debugName = "invoker"; break;
        default: throw new InternalError();
        }
        java.lang.invoke.LambdaForm lform;
        if (isCached) {
            lform = mtype.form().cachedLambdaForm(which);
            if (lform != null)  return lform;
        }
        // exactInvokerForm (Object,Object)Object
        //   link with java.lang.invoke.MethodHandle.invokeBasic(MethodHandle,Object,Object)Object/invokeSpecial
        final int THIS_MH      = 0;
        final int CALL_MH      = THIS_MH + (isLinker ? 0 : 1);
        final int ARG_BASE     = CALL_MH + 1;
        final int OUTARG_LIMIT = ARG_BASE + mtype.parameterCount();
        final int INARG_LIMIT  = OUTARG_LIMIT + (isLinker && !customized ? 1 : 0);
        int nameCursor = OUTARG_LIMIT;
        final int MTYPE_ARG    = customized ? -1 : nameCursor++;  // might be last in-argument
        final int CHECK_TYPE   = nameCursor++;
        final int LINKER_CALL  = nameCursor++;
        MethodType invokerFormType = mtype.invokerType();
        if (isLinker) {
            if (!customized)
                invokerFormType = invokerFormType.appendParameterTypes(MemberName.class);
        } else {
            invokerFormType = invokerFormType.invokerType();
        }
        Name[] names = arguments(nameCursor - INARG_LIMIT, invokerFormType);
        assert(names.length == nameCursor)
                : Arrays.asList(mtype, customized, which, nameCursor, names.length);
        if (MTYPE_ARG >= INARG_LIMIT) {
            assert(names[MTYPE_ARG] == null);
            NamedFunction getter = java.lang.invoke.BoundMethodHandle.getSpeciesData("L").getterFunction(0);
            names[MTYPE_ARG] = new Name(getter, names[THIS_MH]);
            // else if isLinker, then MTYPE is passed in from the caller (e.g., the JVM)
        }

        // Make the final call.  If isGeneric, then prepend the result of type checking.
        MethodType outCallType = mtype.basicType();
        Object[] outArgs = Arrays.copyOfRange(names, CALL_MH, OUTARG_LIMIT, Object[].class);
        Object mtypeArg = (customized ? mtype : names[MTYPE_ARG]);
        if (!isGeneric) {
            names[CHECK_TYPE] = new Name(NF_checkExactType, names[CALL_MH], mtypeArg);
            // mh.invokeExact(a*):R => checkExactType(mh, TYPEOF(a*:R)); mh.invokeBasic(a*)
        } else {
            names[CHECK_TYPE] = new Name(NF_checkGenericType, names[CALL_MH], mtypeArg);
            // mh.invokeGeneric(a*):R => checkGenericType(mh, TYPEOF(a*:R)).invokeBasic(a*)
            outArgs[0] = names[CHECK_TYPE];
        }
        names[LINKER_CALL] = new Name(outCallType, outArgs);
        lform = new java.lang.invoke.LambdaForm(debugName, INARG_LIMIT, names);
        if (isLinker)
            lform.compileToBytecode();  // JVM needs a real methodOop
        if (isCached)
            lform = mtype.form().setCachedLambdaForm(which, lform);
        return lform;
    }

    /*non-public*/ static
    WrongMethodTypeException newWrongMethodTypeException(MethodType actual, MethodType expected) {
        // FIXME: merge with JVM logic for throwing WMTE
        return new WrongMethodTypeException("expected "+expected+" but found "+actual);
    }

    /** Static definition of MethodHandle.invokeExact checking code. */
    /*non-public*/ static
    @ForceInline
    void checkExactType(Object mhObj, Object expectedObj) {
        MethodHandle mh = (MethodHandle) mhObj;
        MethodType expected = (MethodType) expectedObj;
        MethodType actual = mh.type();
        if (actual != expected)
            throw newWrongMethodTypeException(expected, actual);
    }

    /** Static definition of MethodHandle.invokeGeneric checking code.
     * Directly returns the type-adjusted MH to invoke, as follows:
     * {@code (R)MH.invoke(a*) => MH.asType(TYPEOF(a*:R)).invokeBasic(a*)}
     */
    /*non-public*/ static
    @ForceInline
    Object checkGenericType(Object mhObj, Object expectedObj) {
        MethodHandle mh = (MethodHandle) mhObj;
        MethodType expected = (MethodType) expectedObj;
        if (mh.type() == expected)  return mh;
        MethodHandle atc = mh.asTypeCache;
        if (atc != null && atc.type() == expected)  return atc;
        return mh.asType(expected);
        /* Maybe add more paths here.  Possible optimizations:
         * for (R)MH.invoke(a*),
         * let MT0 = TYPEOF(a*:R), MT1 = MH.type
         *
         * if MT0==MT1 or MT1 can be safely called by MT0
         *  => MH.invokeBasic(a*)
         * if MT1 can be safely called by MT0[R := Object]
         *  => MH.invokeBasic(a*) & checkcast(R)
         * if MT1 can be safely called by MT0[* := Object]
         *  => checkcast(A)* & MH.invokeBasic(a*) & checkcast(R)
         * if a big adapter BA can be pulled out of (MT0,MT1)
         *  => BA.invokeBasic(MT0,MH,a*)
         * if a local adapter LA can cached on static CS0 = new GICS(MT0)
         *  => CS0.LA.invokeBasic(MH,a*)
         * else
         *  => MH.asType(MT0).invokeBasic(A*)
         */
    }

    static MemberName linkToCallSiteMethod(MethodType mtype) {
        java.lang.invoke.LambdaForm lform = callSiteForm(mtype, false);
        return lform.vmentry;
    }

    static MemberName linkToTargetMethod(MethodType mtype) {
        java.lang.invoke.LambdaForm lform = callSiteForm(mtype, true);
        return lform.vmentry;
    }

    // skipCallSite is true if we are optimizing a ConstantCallSite
    private static java.lang.invoke.LambdaForm callSiteForm(MethodType mtype, boolean skipCallSite) {
        mtype = mtype.basicType();  // normalize Z to I, String to Object, etc.
        final int which = (skipCallSite ? MethodTypeForm.LF_MH_LINKER : MethodTypeForm.LF_CS_LINKER);
        java.lang.invoke.LambdaForm lform = mtype.form().cachedLambdaForm(which);
        if (lform != null)  return lform;
        // exactInvokerForm (Object,Object)Object
        //   link with java.lang.invoke.MethodHandle.invokeBasic(MethodHandle,Object,Object)Object/invokeSpecial
        final int ARG_BASE     = 0;
        final int OUTARG_LIMIT = ARG_BASE + mtype.parameterCount();
        final int INARG_LIMIT  = OUTARG_LIMIT + 1;
        int nameCursor = OUTARG_LIMIT;
        final int APPENDIX_ARG = nameCursor++;  // the last in-argument
        final int CSITE_ARG    = skipCallSite ? -1 : APPENDIX_ARG;
        final int CALL_MH      = skipCallSite ? APPENDIX_ARG : nameCursor++;  // result of getTarget
        final int LINKER_CALL  = nameCursor++;
        MethodType invokerFormType = mtype.appendParameterTypes(skipCallSite ? MethodHandle.class : CallSite.class);
        Name[] names = arguments(nameCursor - INARG_LIMIT, invokerFormType);
        assert(names.length == nameCursor);
        assert(names[APPENDIX_ARG] != null);
        if (!skipCallSite)
            names[CALL_MH] = new Name(NF_getCallSiteTarget, names[CSITE_ARG]);
        // (site.)invokedynamic(a*):R => mh = site.getTarget(); mh.invokeBasic(a*)
        final int PREPEND_MH = 0, PREPEND_COUNT = 1;
        Object[] outArgs = Arrays.copyOfRange(names, ARG_BASE, OUTARG_LIMIT + PREPEND_COUNT, Object[].class);
        // prepend MH argument:
        System.arraycopy(outArgs, 0, outArgs, PREPEND_COUNT, outArgs.length - PREPEND_COUNT);
        outArgs[PREPEND_MH] = names[CALL_MH];
        names[LINKER_CALL] = new Name(mtype, outArgs);
        lform = new java.lang.invoke.LambdaForm((skipCallSite ? "linkToTargetMethod" : "linkToCallSite"), INARG_LIMIT, names);
        lform.compileToBytecode();  // JVM needs a real methodOop
        lform = mtype.form().setCachedLambdaForm(which, lform);
        return lform;
    }

    /** Static definition of MethodHandle.invokeGeneric checking code. */
    /*non-public*/ static
    @ForceInline
    Object getCallSiteTarget(Object site) {
        return ((CallSite)site).getTarget();
    }

    // Local constant functions:
    private static final NamedFunction NF_checkExactType;
    private static final NamedFunction NF_checkGenericType;
    private static final NamedFunction NF_asType;
    private static final NamedFunction NF_getCallSiteTarget;
    static {
        try {
            NF_checkExactType = new NamedFunction(java.lang.invoke.Invokers.class
                    .getDeclaredMethod("checkExactType", Object.class, Object.class));
            NF_checkGenericType = new NamedFunction(java.lang.invoke.Invokers.class
                    .getDeclaredMethod("checkGenericType", Object.class, Object.class));
            NF_asType = new NamedFunction(MethodHandle.class
                    .getDeclaredMethod("asType", MethodType.class));
            NF_getCallSiteTarget = new NamedFunction(java.lang.invoke.Invokers.class
                    .getDeclaredMethod("getCallSiteTarget", Object.class));
            NF_checkExactType.resolve();
            NF_checkGenericType.resolve();
            NF_getCallSiteTarget.resolve();
            // bound
        } catch (ReflectiveOperationException ex) {
            throw newInternalError(ex);
        }
    }

}
