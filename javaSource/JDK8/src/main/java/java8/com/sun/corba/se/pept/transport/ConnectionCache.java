/*
 * Copyright (c) 2001, 2010, Oracle and/or its affiliates. All rights reserved.
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

package java8.com.sun.corba.se.pept.transport;

import com.sun.corba.se.pept.transport.Connection;

/**
 * @author Harold Carr
 */
public interface ConnectionCache
{
    public String getCacheType();

    public void stampTime(Connection connection);

    public long numberOfConnections();

    public long numberOfIdleConnections();

    public long numberOfBusyConnections();

    public boolean reclaim();

    /** Close all connections in the connection cache.
     * This is used as a final cleanup, and will result
     * in abrupt termination of any pending communications.
     */
    public void close();
}

// End of file.
