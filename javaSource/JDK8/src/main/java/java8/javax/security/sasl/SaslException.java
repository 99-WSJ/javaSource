/*
 * Copyright (c) 1999, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java8.javax.security.sasl;

import java.io.IOException;

/**
 * This class represents an error that has occurred when using SASL.
 *
 * @since 1.5
 *
 * @author Rosanna Lee
 * @author Rob Weltman
 */

public class SaslException extends IOException {
    /**
     * The possibly null root cause exception.
     * @serial
     */
    // Required for serialization interoperability with JSR 28
    private Throwable _exception;

    /**
     * Constructs a new instance of {@code SaslException}.
     * The root exception and the detailed message are null.
     */
    public SaslException () {
        super();
    }

    /**
     * Constructs a new instance of {@code SaslException} with a detailed message.
     * The root exception is null.
     * @param detail A possibly null string containing details of the exception.
     *
     * @see Throwable#getMessage
     */
    public SaslException (String detail) {
        super(detail);
    }

    /**
     * Constructs a new instance of {@code SaslException} with a detailed message
     * and a root exception.
     * For example, a SaslException might result from a problem with
     * the callback handler, which might throw a NoSuchCallbackException if
     * it does not support the requested callback, or throw an IOException
     * if it had problems obtaining data for the callback. The
     * SaslException's root exception would be then be the exception thrown
     * by the callback handler.
     *
     * @param detail A possibly null string containing details of the exception.
     * @param ex A possibly null root exception that caused this exception.
     *
     * @see Throwable#getMessage
     * @see #getCause
     */
    public SaslException (String detail, Throwable ex) {
        super(detail);
        if (ex != null) {
            initCause(ex);
        }
    }

    /*
     * Override Throwable.getCause() to ensure deserialized object from
     * JSR 28 would return same value for getCause() (i.e., _exception).
     */
    public Throwable getCause() {
        return _exception;
    }

    /*
     * Override Throwable.initCause() to match getCause() by updating
     * _exception as well.
     */
    public Throwable initCause(Throwable cause) {
        super.initCause(cause);
        _exception = cause;
        return this;
    }

    /**
     * Returns the string representation of this exception.
     * The string representation contains
     * this exception's class name, its detailed message, and if
     * it has a root exception, the string representation of the root
     * exception. This string representation
     * is meant for debugging and not meant to be interpreted
     * programmatically.
     * @return The non-null string representation of this exception.
     * @see Throwable#getMessage
     */
    // Override Throwable.toString() to conform to JSR 28
    public String toString() {
        String answer = super.toString();
        if (_exception != null && _exception != this) {
            answer += " [Caused by " + _exception.toString() + "]";
        }
        return answer;
    }

    /** Use serialVersionUID from JSR 28 RI for interoperability */
    private static final long serialVersionUID = 4579784287983423626L;
}
