/*
 * Copyright (c) 1996, 2000, Oracle and/or its affiliates. All rights reserved.
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

package java8.java.lang;

/**
 * Signals that an unexpected exception has occurred in a static initializer.
 * An <code>ExceptionInInitializerError</code> is thrown to indicate that an
 * exception occurred during evaluation of a static initializer or the
 * initializer for a static variable.
 *
 * <p>As of release 1.4, this exception has been retrofitted to conform to
 * the general purpose exception-chaining mechanism.  The "saved throwable
 * object" that may be provided at construction time and accessed via
 * the {@link #getException()} method is now known as the <i>cause</i>,
 * and may be accessed via the {@link java.lang.Throwable#getCause()} method, as well
 * as the aforementioned "legacy method."
 *
 * @author  Frank Yellin
 * @since   JDK1.1
 */
public class ExceptionInInitializerError extends LinkageError {
    /**
     * Use serialVersionUID from JDK 1.1.X for interoperability
     */
    private static final long serialVersionUID = 1521711792217232256L;

    /**
     * This field holds the exception if the
     * ExceptionInInitializerError(Throwable thrown) constructor was
     * used to instantiate the object
     *
     * @serial
     *
     */
    private java.lang.Throwable exception;

    /**
     * Constructs an <code>ExceptionInInitializerError</code> with
     * <code>null</code> as its detail message string and with no saved
     * throwable object.
     * A detail message is a String that describes this particular exception.
     */
    public ExceptionInInitializerError() {
        initCause(null);  // Disallow subsequent initCause
    }

    /**
     * Constructs a new <code>ExceptionInInitializerError</code> class by
     * saving a reference to the <code>Throwable</code> object thrown for
     * later retrieval by the {@link #getException()} method. The detail
     * message string is set to <code>null</code>.
     *
     * @param thrown The exception thrown
     */
    public ExceptionInInitializerError(java.lang.Throwable thrown) {
        initCause(null);  // Disallow subsequent initCause
        this.exception = thrown;
    }

    /**
     * Constructs an ExceptionInInitializerError with the specified detail
     * message string.  A detail message is a String that describes this
     * particular exception. The detail message string is saved for later
     * retrieval by the {@link java.lang.Throwable#getMessage()} method. There is no
     * saved throwable object.
     *
     *
     * @param s the detail message
     */
    public ExceptionInInitializerError(String s) {
        super(s);
        initCause(null);  // Disallow subsequent initCause
    }

    /**
     * Returns the exception that occurred during a static initialization that
     * caused this error to be created.
     *
     * <p>This method predates the general-purpose exception chaining facility.
     * The {@link java.lang.Throwable#getCause()} method is now the preferred means of
     * obtaining this information.
     *
     * @return the saved throwable object of this
     *         <code>ExceptionInInitializerError</code>, or <code>null</code>
     *         if this <code>ExceptionInInitializerError</code> has no saved
     *         throwable object.
     */
    public java.lang.Throwable getException() {
        return exception;
    }

    /**
     * Returns the cause of this error (the exception that occurred
     * during a static initialization that caused this error to be created).
     *
     * @return  the cause of this error or <code>null</code> if the
     *          cause is nonexistent or unknown.
     * @since   1.4
     */
    public java.lang.Throwable getCause() {
        return exception;
    }
}
