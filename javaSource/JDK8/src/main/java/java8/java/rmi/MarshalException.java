/*
 * Copyright (c) 1996, 1998, Oracle and/or its affiliates. All rights reserved.
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

package java8.java.rmi;

import java.rmi.RemoteException;

/**
 * A <code>MarshalException</code> is thrown if a
 * <code>java.io.IOException</code> occurs while marshalling the remote call
 * header, arguments or return value for a remote method call.  A
 * <code>MarshalException</code> is also thrown if the receiver does not
 * support the protocol version of the sender.
 *
 * <p>If a <code>MarshalException</code> occurs during a remote method call,
 * the call may or may not have reached the server.  If the call did reach the
 * server, parameters may have been deserialized.  A call may not be
 * retransmitted after a <code>MarshalException</code> and reliably preserve
 * "at most once" call semantics.
 *
 * @author  Ann Wollrath
 * @since   JDK1.1
 */
public class MarshalException extends RemoteException {

    /* indicate compatibility with JDK 1.1.x version of class */
    private static final long serialVersionUID = 6223554758134037936L;

    /**
     * Constructs a <code>MarshalException</code> with the specified
     * detail message.
     *
     * @param s the detail message
     * @since JDK1.1
     */
    public MarshalException(String s) {
        super(s);
    }

    /**
     * Constructs a <code>MarshalException</code> with the specified
     * detail message and nested exception.
     *
     * @param s the detail message
     * @param ex the nested exception
     * @since JDK1.1
     */
    public MarshalException(String s, Exception ex) {
        super(s, ex);
    }
}
