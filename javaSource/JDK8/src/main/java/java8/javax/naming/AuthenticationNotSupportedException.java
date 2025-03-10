/*
 * Copyright (c) 1999, Oracle and/or its affiliates. All rights reserved.
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

package java8.javax.naming;

import javax.naming.NamingSecurityException;

/**
  * This exception is thrown when
  * the particular flavor of authentication requested is not supported.
  * For example, if the program
  * is attempting to use strong authentication but the directory/naming
  * supports only simple authentication, this exception would be thrown.
  * Identification of a particular flavor of authentication is
  * provider- and server-specific. It may be specified using
  * specific authentication schemes such
  * those identified using SASL, or a generic authentication specifier
  * (such as "simple" and "strong").
  *<p>
  * If the program wants to handle this exception in particular, it
  * should catch AuthenticationNotSupportedException explicitly before
  * attempting to catch NamingException. After catching
  * <code>AuthenticationNotSupportedException</code>, the program could
  * reattempt the authentication using a different authentication flavor
  * by updating the resolved context's environment properties accordingly.
  * <p>
  * Synchronization and serialization issues that apply to NamingException
  * apply directly here.
  *
  * @author Rosanna Lee
  * @author Scott Seligman
  * @since 1.3
  */

public class AuthenticationNotSupportedException extends NamingSecurityException {
    /**
     * Constructs a new instance of AuthenticationNotSupportedException using
     * an explanation. All other fields default to null.
     *
     * @param   explanation     A possibly null string containing additional
     *                          detail about this exception.
     * @see Throwable#getMessage
     */
    public AuthenticationNotSupportedException(String explanation) {
        super(explanation);
    }

    /**
      * Constructs a new instance of AuthenticationNotSupportedException
      * all name resolution fields and explanation initialized to null.
      */
    public AuthenticationNotSupportedException() {
        super();
    }

    /**
     * Use serialVersionUID from JNDI 1.1.1 for interoperability
     */
    private static final long serialVersionUID = -7149033933259492300L;
}
