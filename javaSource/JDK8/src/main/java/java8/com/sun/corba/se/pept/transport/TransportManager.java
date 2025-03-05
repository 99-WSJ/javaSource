/*
 * Copyright (c) 2003, Oracle and/or its affiliates. All rights reserved.
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

package java8.sun.corba.se.pept.transport;

import com.sun.corba.se.pept.transport.OutboundConnectionCache;
import com.sun.corba.se.pept.transport.Selector;

import java.util.Collection;

/**
 * @author Harold Carr
 */
public interface TransportManager
{
    public ByteBufferPool getByteBufferPool(int id);

    public OutboundConnectionCache getOutboundConnectionCache(
        ContactInfo contactInfo);

    public Collection getOutboundConnectionCaches();

    public InboundConnectionCache getInboundConnectionCache(Acceptor acceptor);

    public Collection getInboundConnectionCaches();

    public Selector getSelector(int id);

    public void registerAcceptor(Acceptor acceptor);

    public Collection getAcceptors();

    public void unregisterAcceptor(Acceptor acceptor);

    public void close();
}

// End of file.
