/*
 * Copyright (c) 2000, 2011, Oracle and/or its affiliates. All rights reserved.
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

import com.sun.beans.finder.PersistenceDelegateFinder;

import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.ConstructorProperties;
import java.beans.ExceptionListener;
import java.beans.Expression;
import java.beans.Introspector;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * An <code>Encoder</code> is a class which can be used to create
 * files or streams that encode the state of a collection of
 * JavaBeans in terms of their public APIs. The <code>Encoder</code>,
 * in conjunction with its persistence delegates, is responsible for
 * breaking the object graph down into a series of <code>Statements</code>s
 * and <code>Expression</code>s which can be used to create it.
 * A subclass typically provides a syntax for these expressions
 * using some human readable form - like Java source code or XML.
 *
 * @since 1.4
 *
 * @author Philip Milne
 */

public class Encoder {
    private final PersistenceDelegateFinder finder = new PersistenceDelegateFinder();
    private Map<Object, Expression> bindings = new IdentityHashMap<>();
    private ExceptionListener exceptionListener;
    boolean executeStatements = true;
    private Map<Object, Object> attributes;

    /**
     * Write the specified object to the output stream.
     * The serialized form will denote a series of
     * expressions, the combined effect of which will create
     * an equivalent object when the input stream is read.
     * By default, the object is assumed to be a <em>JavaBean</em>
     * with a nullary constructor, whose state is defined by
     * the matching pairs of "setter" and "getter" methods
     * returned by the Introspector.
     *
     * @param o The object to be written to the stream.
     *
     * @see XMLDecoder#readObject
     */
    protected void writeObject(Object o) {
        if (o == this) {
            return;
        }
        PersistenceDelegate info = getPersistenceDelegate(o == null ? null : o.getClass());
        info.writeObject(o, this);
    }

    /**
     * Sets the exception handler for this stream to <code>exceptionListener</code>.
     * The exception handler is notified when this stream catches recoverable
     * exceptions.
     *
     * @param exceptionListener The exception handler for this stream;
     *       if <code>null</code> the default exception listener will be used.
     *
     * @see #getExceptionListener
     */
    public void setExceptionListener(ExceptionListener exceptionListener) {
        this.exceptionListener = exceptionListener;
    }

    /**
     * Gets the exception handler for this stream.
     *
     * @return The exception handler for this stream;
     *    Will return the default exception listener if this has not explicitly been set.
     *
     * @see #setExceptionListener
     */
    public ExceptionListener getExceptionListener() {
        return (exceptionListener != null) ? exceptionListener : Statement.defaultExceptionListener;
    }

    Object getValue(Expression exp) {
        try {
            return (exp == null) ? null : exp.getValue();
        }
        catch (Exception e) {
            getExceptionListener().exceptionThrown(e);
            throw new RuntimeException("failed to evaluate: " + exp.toString());
        }
    }

    /**
     * Returns the persistence delegate for the given type.
     * The persistence delegate is calculated by applying
     * the following rules in order:
     * <ol>
     * <li>
     * If a persistence delegate is associated with the given type
     * by using the {@link #setPersistenceDelegate} method
     * it is returned.
     * <li>
     * A persistence delegate is then looked up by the name
     * composed of the the fully qualified name of the given type
     * and the "PersistenceDelegate" postfix.
     * For example, a persistence delegate for the {@code Bean} class
     * should be named {@code BeanPersistenceDelegate}
     * and located in the same package.
     * <pre>
     * public class Bean { ... }
     * public class BeanPersistenceDelegate { ... }</pre>
     * The instance of the {@code BeanPersistenceDelegate} class
     * is returned for the {@code Bean} class.
     * <li>
     * If the type is {@code null},
     * a shared internal persistence delegate is returned
     * that encodes {@code null} value.
     * <li>
     * If the type is a {@code enum} declaration,
     * a shared internal persistence delegate is returned
     * that encodes constants of this enumeration
     * by their names.
     * <li>
     * If the type is a primitive type or the corresponding wrapper,
     * a shared internal persistence delegate is returned
     * that encodes values of the given type.
     * <li>
     * If the type is an array,
     * a shared internal persistence delegate is returned
     * that encodes an array of the appropriate type and length,
     * and each of its elements as if they are properties.
     * <li>
     * If the type is a proxy,
     * a shared internal persistence delegate is returned
     * that encodes a proxy instance by using
     * the {@link java.lang.reflect.Proxy#newProxyInstance} method.
     * <li>
     * If the {@link BeanInfo} for this type has a {@link BeanDescriptor}
     * which defined a "persistenceDelegate" attribute,
     * the value of this named attribute is returned.
     * <li>
     * In all other cases the default persistence delegate is returned.
     * The default persistence delegate assumes the type is a <em>JavaBean</em>,
     * implying that it has a default constructor and that its state
     * may be characterized by the matching pairs of "setter" and "getter"
     * methods returned by the {@link Introspector} class.
     * The default constructor is the constructor with the greatest number
     * of parameters that has the {@link ConstructorProperties} annotation.
     * If none of the constructors has the {@code ConstructorProperties} annotation,
     * then the nullary constructor (constructor with no parameters) will be used.
     * For example, in the following code fragment, the nullary constructor
     * for the {@code Foo} class will be used,
     * while the two-parameter constructor
     * for the {@code Bar} class will be used.
     * <pre>
     * public class Foo {
     *     public Foo() { ... }
     *     public Foo(int x) { ... }
     * }
     * public class Bar {
     *     public Bar() { ... }
     *     &#64;ConstructorProperties({"x"})
     *     public Bar(int x) { ... }
     *     &#64;ConstructorProperties({"x", "y"})
     *     public Bar(int x, int y) { ... }
     * }</pre>
     * </ol>
     *
     * @param type  the class of the objects
     * @return the persistence delegate for the given type
     *
     * @see #setPersistenceDelegate
     * @see Introspector#getBeanInfo
     * @see BeanInfo#getBeanDescriptor
     */
    public PersistenceDelegate getPersistenceDelegate(Class<?> type) {
        PersistenceDelegate pd = this.finder.find(type);
        if (pd == null) {
            pd = MetaData.getPersistenceDelegate(type);
            if (pd != null) {
                this.finder.register(type, pd);
            }
        }
        return pd;
    }

    /**
     * Associates the specified persistence delegate with the given type.
     *
     * @param type  the class of objects that the specified persistence delegate applies to
     * @param delegate  the persistence delegate for instances of the given type
     *
     * @see #getPersistenceDelegate
     * @see Introspector#getBeanInfo
     * @see BeanInfo#getBeanDescriptor
     */
    public void setPersistenceDelegate(Class<?> type, PersistenceDelegate delegate) {
        this.finder.register(type, delegate);
    }

    /**
     * Removes the entry for this instance, returning the old entry.
     *
     * @param oldInstance The entry that should be removed.
     * @return The entry that was removed.
     *
     * @see #get
     */
    public Object remove(Object oldInstance) {
        Expression exp = bindings.remove(oldInstance);
        return getValue(exp);
    }

    /**
     * Returns a tentative value for <code>oldInstance</code> in
     * the environment created by this stream. A persistence
     * delegate can use its <code>mutatesTo</code> method to
     * determine whether this value may be initialized to
     * form the equivalent object at the output or whether
     * a new object must be instantiated afresh. If the
     * stream has not yet seen this value, null is returned.
     *
     * @param  oldInstance The instance to be looked up.
     * @return The object, null if the object has not been seen before.
     */
    public Object get(Object oldInstance) {
        if (oldInstance == null || oldInstance == this ||
            oldInstance.getClass() == String.class) {
            return oldInstance;
        }
        Expression exp = bindings.get(oldInstance);
        return getValue(exp);
    }

    private Object writeObject1(Object oldInstance) {
        Object o = get(oldInstance);
        if (o == null) {
            writeObject(oldInstance);
            o = get(oldInstance);
        }
        return o;
    }

    private Statement cloneStatement(Statement oldExp) {
        Object oldTarget = oldExp.getTarget();
        Object newTarget = writeObject1(oldTarget);

        Object[] oldArgs = oldExp.getArguments();
        Object[] newArgs = new Object[oldArgs.length];
        for (int i = 0; i < oldArgs.length; i++) {
            newArgs[i] = writeObject1(oldArgs[i]);
        }
        Statement newExp = Statement.class.equals(oldExp.getClass())
                ? new Statement(newTarget, oldExp.getMethodName(), newArgs)
                : new Expression(newTarget, oldExp.getMethodName(), newArgs);
        newExp.loader = oldExp.loader;
        return newExp;
    }

    /**
     * Writes statement <code>oldStm</code> to the stream.
     * The <code>oldStm</code> should be written entirely
     * in terms of the callers environment, i.e. the
     * target and all arguments should be part of the
     * object graph being written. These expressions
     * represent a series of "what happened" expressions
     * which tell the output stream how to produce an
     * object graph like the original.
     * <p>
     * The implementation of this method will produce
     * a second expression to represent the same expression in
     * an environment that will exist when the stream is read.
     * This is achieved simply by calling <code>writeObject</code>
     * on the target and all the arguments and building a new
     * expression with the results.
     *
     * @param oldStm The expression to be written to the stream.
     */
    public void writeStatement(Statement oldStm) {
        // System.out.println("writeStatement: " + oldExp);
        Statement newStm = cloneStatement(oldStm);
        if (oldStm.getTarget() != this && executeStatements) {
            try {
                newStm.execute();
            } catch (Exception e) {
                getExceptionListener().exceptionThrown(new Exception("Encoder: discarding statement "
                                                                     + newStm, e));
            }
        }
    }

    /**
     * The implementation first checks to see if an
     * expression with this value has already been written.
     * If not, the expression is cloned, using
     * the same procedure as <code>writeStatement</code>,
     * and the value of this expression is reconciled
     * with the value of the cloned expression
     * by calling <code>writeObject</code>.
     *
     * @param oldExp The expression to be written to the stream.
     */
    public void writeExpression(Expression oldExp) {
        // System.out.println("Encoder::writeExpression: " + oldExp);
        Object oldValue = getValue(oldExp);
        if (get(oldValue) != null) {
            return;
        }
        bindings.put(oldValue, (Expression)cloneStatement(oldExp));
        writeObject(oldValue);
    }

    void clear() {
        bindings.clear();
    }

    // Package private method for setting an attributes table for the encoder
    void setAttribute(Object key, Object value) {
        if (attributes == null) {
            attributes = new HashMap<>();
        }
        attributes.put(key, value);
    }

    Object getAttribute(Object key) {
        if (attributes == null) {
            return null;
        }
        return attributes.get(key);
    }
}
