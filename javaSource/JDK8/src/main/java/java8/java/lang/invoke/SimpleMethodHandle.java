/*
 * Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved.
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

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;

import static java.lang.invoke.LambdaForm.basicTypes;

/**
 * A method handle whose behavior is determined only by its LambdaForm.
 * @author jrose
 */
final class SimpleMethodHandle extends MethodHandle {
    private SimpleMethodHandle(MethodType type, java.lang.invoke.LambdaForm form) {
        super(type, form);
    }

    /*non-public*/ static java.lang.invoke.SimpleMethodHandle make(MethodType type, java.lang.invoke.LambdaForm form) {
        return new java.lang.invoke.SimpleMethodHandle(type, form);
    }

    @Override
    MethodHandle bindArgument(int pos, char basicType, Object value) {
        MethodType type2 = type().dropParameterTypes(pos, pos+1);
        java.lang.invoke.LambdaForm form2 = internalForm().bind(1+pos, java.lang.invoke.BoundMethodHandle.SpeciesData.EMPTY);
        return java.lang.invoke.BoundMethodHandle.bindSingle(type2, form2, basicType, value);
    }

    @Override
    MethodHandle dropArguments(MethodType srcType, int pos, int drops) {
        java.lang.invoke.LambdaForm newForm = internalForm().addArguments(pos, srcType.parameterList().subList(pos, pos+drops));
        return new java.lang.invoke.SimpleMethodHandle(srcType, newForm);
    }

    @Override
    MethodHandle permuteArguments(MethodType newType, int[] reorder) {
        java.lang.invoke.LambdaForm form2 = internalForm().permuteArguments(1, reorder, basicTypes(newType.parameterList()));
        return new java.lang.invoke.SimpleMethodHandle(newType, form2);
    }

    @Override
    MethodHandle copyWith(MethodType mt, java.lang.invoke.LambdaForm lf) {
        return new java.lang.invoke.SimpleMethodHandle(mt, lf);
    }

}
