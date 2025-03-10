/*
 * Copyright (c) 2000, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java8.java.beans;

import java.beans.ConstructorProperties;
import java.beans.Statement;

/**
 * An <code>Expression</code> object represents a primitive expression
 * in which a single method is applied to a target and a set of
 * arguments to return a result - as in <code>"a.getFoo()"</code>.
 * <p>
 * In addition to the properties of the super class, the
 * <code>Expression</code> object provides a <em>value</em> which
 * is the object returned when this expression is evaluated.
 * The return value is typically not provided by the caller and
 * is instead computed by dynamically finding the method and invoking
 * it when the first call to <code>getValue</code> is made.
 *
 * @see #getValue
 * @see #setValue
 *
 * @since 1.4
 *
 * @author Philip Milne
 */
public class Expression extends Statement {

    private static Object unbound = new Object();

    private Object value = unbound;

    /**
     * Creates a new {@link java.beans.Expression} object
     * for the specified target object to invoke the method
     * specified by the name and by the array of arguments.
     * <p>
     * The {@code target} and the {@code methodName} values should not be {@code null}.
     * Otherwise an attempt to execute this {@code Expression}
     * will result in a {@code NullPointerException}.
     * If the {@code arguments} value is {@code null},
     * an empty array is used as the value of the {@code arguments} property.
     *
     * @param target  the target object of this expression
     * @param methodName  the name of the method to invoke on the specified target
     * @param arguments  the array of arguments to invoke the specified method
     *
     * @see #getValue
     */
    @ConstructorProperties({"target", "methodName", "arguments"})
    public Expression(Object target, String methodName, Object[] arguments) {
        super(target, methodName, arguments);
    }

    /**
     * Creates a new {@link java.beans.Expression} object with the specified value
     * for the specified target object to invoke the  method
     * specified by the name and by the array of arguments.
     * The {@code value} value is used as the value of the {@code value} property,
     * so the {@link #getValue} method will return it
     * without executing this {@code Expression}.
     * <p>
     * The {@code target} and the {@code methodName} values should not be {@code null}.
     * Otherwise an attempt to execute this {@code Expression}
     * will result in a {@code NullPointerException}.
     * If the {@code arguments} value is {@code null},
     * an empty array is used as the value of the {@code arguments} property.
     *
     * @param value  the value of this expression
     * @param target  the target object of this expression
     * @param methodName  the name of the method to invoke on the specified target
     * @param arguments  the array of arguments to invoke the specified method
     *
     * @see #setValue
     */
    public Expression(Object value, Object target, String methodName, Object[] arguments) {
        this(target, methodName, arguments);
        setValue(value);
    }

    /**
     * {@inheritDoc}
     * <p>
     * If the invoked method completes normally,
     * the value it returns is copied in the {@code value} property.
     * Note that the {@code value} property is set to {@code null},
     * if the return type of the underlying method is {@code void}.
     *
     * @throws NullPointerException if the value of the {@code target} or
     *                              {@code methodName} property is {@code null}
     * @throws NoSuchMethodException if a matching method is not found
     * @throws SecurityException if a security manager exists and
     *                           it denies the method invocation
     * @throws Exception that is thrown by the invoked method
     *
     * @see java.lang.reflect.Method
     * @since 1.7
     */
    @Override
    public void execute() throws Exception {
        setValue(invoke());
    }

    /**
     * If the value property of this instance is not already set,
     * this method dynamically finds the method with the specified
     * methodName on this target with these arguments and calls it.
     * The result of the method invocation is first copied
     * into the value property of this expression and then returned
     * as the result of <code>getValue</code>. If the value property
     * was already set, either by a call to <code>setValue</code>
     * or a previous call to <code>getValue</code> then the value
     * property is returned without either looking up or calling the method.
     * <p>
     * The value property of an <code>Expression</code> is set to
     * a unique private (non-<code>null</code>) value by default and
     * this value is used as an internal indication that the method
     * has not yet been called. A return value of <code>null</code>
     * replaces this default value in the same way that any other value
     * would, ensuring that expressions are never evaluated more than once.
     * <p>
     * See the <code>execute</code> method for details on how
     * methods are chosen using the dynamic types of the target
     * and arguments.
     *
     * @see Statement#execute
     * @see #setValue
     *
     * @return The result of applying this method to these arguments.
     * @throws Exception if the method with the specified methodName
     * throws an exception
     */
    public Object getValue() throws Exception {
        if (value == unbound) {
            setValue(invoke());
        }
        return value;
    }

    /**
     * Sets the value of this expression to <code>value</code>.
     * This value will be returned by the getValue method
     * without calling the method associated with this
     * expression.
     *
     * @param value The value of this expression.
     *
     * @see #getValue
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /*pp*/ String instanceName(Object instance) {
        return instance == unbound ? "<unbound>" : super.instanceName(instance);
    }

    /**
     * Prints the value of this expression using a Java-style syntax.
     */
    public String toString() {
        return instanceName(value) + "=" + super.toString();
    }
}
