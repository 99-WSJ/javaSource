/*
 * Copyright (c) 1999, 2006, Oracle and/or its affiliates. All rights reserved.
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

package java8.java.lang.reflect;

import java.lang.reflect.InvocationHandler;

/**
 * Thrown by a method invocation on a proxy instance if its invocation
 * handler's {@link InvocationHandler#invoke invoke} method throws a
 * checked exception (a {@code Throwable} that is not assignable
 * to {@code RuntimeException} or {@code Error}) that
 * is not assignable to any of the exception types declared in the
 * {@code throws} clause of the method that was invoked on the
 * proxy instance and dispatched to the invocation handler.
 *
 * <p>An {@code UndeclaredThrowableException} instance contains
 * the undeclared checked exception that was thrown by the invocation
 * handler, and it can be retrieved with the
 * {@code getUndeclaredThrowable()} method.
 * {@code UndeclaredThrowableException} extends
 * {@code RuntimeException}, so it is an unchecked exception
 * that wraps a checked exception.
 *
 * <p>As of release 1.4, this exception has been retrofitted to
 * conform to the general purpose exception-chaining mechanism.  The
 * "undeclared checked exception that was thrown by the invocation
 * handler" that may be provided at construction time and accessed via
 * the {@link #getUndeclaredThrowable()} method is now known as the
 * <i>cause</i>, and may be accessed via the {@link
 * Throwable#getCause()} method, as well as the aforementioned "legacy
 * method."
 *
 * @author      Peter Jones
 * @see         InvocationHandler
 * @since       1.3
 */
public class UndeclaredThrowableException extends RuntimeException {
    static final long serialVersionUID = 330127114055056639L;

    /**
     * the undeclared checked exception that was thrown
     * @serial
     */
    private Throwable undeclaredThrowable;

    /**
     * Constructs an {@code UndeclaredThrowableException} with the
     * specified {@code Throwable}.
     *
     * @param   undeclaredThrowable the undeclared checked exception
     *          that was thrown
     */
    public UndeclaredThrowableException(Throwable undeclaredThrowable) {
        super((Throwable) null);  // Disallow initCause
        this.undeclaredThrowable = undeclaredThrowable;
    }

    /**
     * Constructs an {@code UndeclaredThrowableException} with the
     * specified {@code Throwable} and a detail message.
     *
     * @param   undeclaredThrowable the undeclared checked exception
     *          that was thrown
     * @param   s the detail message
     */
    public UndeclaredThrowableException(Throwable undeclaredThrowable,
                                        String s)
    {
        super(s, null);  // Disallow initCause
        this.undeclaredThrowable = undeclaredThrowable;
    }

    /**
     * Returns the {@code Throwable} instance wrapped in this
     * {@code UndeclaredThrowableException}, which may be {@code null}.
     *
     * <p>This method predates the general-purpose exception chaining facility.
     * The {@link Throwable#getCause()} method is now the preferred means of
     * obtaining this information.
     *
     * @return the undeclared checked exception that was thrown
     */
    public Throwable getUndeclaredThrowable() {
        return undeclaredThrowable;
    }

    /**
     * Returns the cause of this exception (the {@code Throwable}
     * instance wrapped in this {@code UndeclaredThrowableException},
     * which may be {@code null}).
     *
     * @return  the cause of this exception.
     * @since   1.4
     */
    public Throwable getCause() {
        return undeclaredThrowable;
    }
}
