/*
 * Copyright (c) 2000, 2007, Oracle and/or its affiliates. All rights reserved.
 *
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
 *
 */

// -- This file was mechanically generated: Do not edit! -- //

package java8.java.nio.channels;


import java.nio.channels.SocketChannel;

/**
 * Unchecked exception thrown when an attempt is made to connect a {@link
 * SocketChannel} for which a non-blocking connection operation is already in
 * progress.
 *
 * @since 1.4
 */

public class ConnectionPendingException
    extends IllegalStateException
{

    private static final long serialVersionUID = 2008393366501760879L;

    /**
     * Constructs an instance of this class.
     */
    public ConnectionPendingException() { }

}
