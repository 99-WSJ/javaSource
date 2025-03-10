/*
 * Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java8.sun.javadoc;

import java8.com.sun.javadoc.ClassDoc;

/**
 * Represents a method or constructor of a java class.
 *
 * @since 1.2
 * @author Robert Field
 */
public interface ExecutableMemberDoc extends MemberDoc {

    /**
     * Return exceptions this method or constructor throws.
     * If the type of the exception is a type variable, return the
     * <code>ClassDoc</code> of its erasure.
     *
     * <p> <i>The <code>thrownExceptions</code> method cannot
     * accommodate certain generic type constructs.  The
     * <code>thrownExceptionTypes</code> method should be used
     * instead.</i>
     *
     * @return an array of ClassDoc[] representing the exceptions
     *         thrown by this method.
     * @see #thrownExceptionTypes
     */
    ClassDoc[] thrownExceptions();

    /**
     * Return exceptions this method or constructor throws.
     *
     * @return an array representing the exceptions thrown by this method.
     *         Each array element is either a <code>ClassDoc</code> or a
     *         <code>TypeVariable</code>.
     * @since 1.5
     */
    Type[] thrownExceptionTypes();

    /**
     * Return true if this method is native
     */
    boolean isNative();

    /**
     * Return true if this method is synchronized
     */
    boolean isSynchronized();

    /**
     * Return true if this method was declared to take a variable number
     * of arguments.
     *
     * @since 1.5
     */
    public boolean isVarArgs();

    /**
     * Get argument information.
     *
     * @see Parameter
     *
     * @return an array of Parameter, one element per argument
     * in the order the arguments are present.
     */
    Parameter[] parameters();

    /**
     * Get the receiver type of this executable element.
     *
     * @return the receiver type of this executable element.
     * @since 1.8
     */
    Type receiverType();

    /**
     * Return the throws tags in this method.
     *
     * @return an array of ThrowTag containing all <code>&#64;exception</code>
     * and <code>&#64;throws</code> tags.
     */
    ThrowsTag[] throwsTags();

    /**
     * Return the param tags in this method, excluding the type
     * parameter tags.
     *
     * @return an array of ParamTag containing all <code>&#64;param</code> tags
     * corresponding to the parameters of this method.
     */
    ParamTag[] paramTags();

    /**
     * Return the type parameter tags in this method.
     *
     * @return an array of ParamTag containing all <code>&#64;param</code> tags
     * corresponding to the type parameters of this method.
     * @since 1.5
     */
    ParamTag[] typeParamTags();

    /**
     * Get the signature. It is the parameter list, type is qualified.
     *      For instance, for a method <code>mymethod(String x, int y)</code>,
     *      it will return <code>(java.lang.String,int)</code>.
     */
    String signature();

    /**
     * get flat signature.  all types are not qualified.
     *      return a String, which is the flat signiture of this member.
     *      It is the parameter list, type is not qualified.
     *      For instance, for a method <code>mymethod(String x, int y)</code>,
     *      it will return <code>(String, int)</code>.
     */
    String flatSignature();

    /**
     * Return the formal type parameters of this method or constructor.
     * Return an empty array if this method or constructor is not generic.
     *
     * @return the formal type parameters of this method or constructor.
     * @since 1.5
     */
    TypeVariable[] typeParameters();
}
