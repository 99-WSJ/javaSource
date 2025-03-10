/*
 * Copyright (c) 1999, 2003, Oracle and/or its affiliates. All rights reserved.
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

package java8.javax.management;


import javax.management.JMRuntimeException;

/**
 * When a <CODE>java.lang.Error</CODE> occurs in the agent it should be caught and
 * re-thrown as a <CODE>RuntimeErrorException</CODE>.
 *
 * @since 1.5
 */
public class RuntimeErrorException extends JMRuntimeException   {

    /* Serial version */
    private static final long serialVersionUID = 704338937753949796L;

    /**
     * @serial The encapsulated {@link Error}
     */
    private Error error ;

    /**
     * Default constructor.
     *
     * @param e the wrapped error.
     */
    public RuntimeErrorException(Error e) {
      super();
      error = e ;
    }

    /**
     * Constructor that allows a specific error message to be specified.
     *
     * @param e the wrapped error.
     * @param message the detail message.
     */
    public RuntimeErrorException(Error e, String message) {
       super(message);
       error = e ;
    }

    /**
     * Returns the actual {@link Error} thrown.
     *
     * @return the wrapped {@link Error}.
     */
    public Error getTargetError()  {
        return error ;
    }

    /**
     * Returns the actual {@link Error} thrown.
     *
     * @return the wrapped {@link Error}.
     */
    public Throwable getCause() {
        return error;
    }
}
