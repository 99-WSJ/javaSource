/*
 * Copyright (c) 2003, 2010, Oracle and/or its affiliates. All rights reserved.
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

package java8.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.oa.poa.Policies;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.transport.CorbaInboundConnectionCacheImpl;
import com.sun.corba.se.impl.transport.CorbaOutboundConnectionCacheImpl;
import com.sun.corba.se.pept.transport.*;
import com.sun.corba.se.spi.ior.IORTemplate;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.transport.CorbaAcceptor;
import com.sun.corba.se.spi.transport.CorbaTransportManager;

import java.util.*;

/**
 * @author Harold Carr
 */
public class CorbaTransportManagerImpl
    implements
        CorbaTransportManager
{
    protected ORB orb;
    protected List acceptors;
    protected Map outboundConnectionCaches;
    protected Map inboundConnectionCaches;
    protected Selector selector;

    public CorbaTransportManagerImpl(ORB orb)
    {
        this.orb = orb;
        acceptors = new ArrayList();
        outboundConnectionCaches = new HashMap();
        inboundConnectionCaches = new HashMap();
        selector = new com.sun.corba.se.impl.transport.SelectorImpl(orb);
    }

    ////////////////////////////////////////////////////
    //
    // pept TransportManager
    //

    public ByteBufferPool getByteBufferPool(int id)
    {
        throw new RuntimeException();
    }

    public OutboundConnectionCache getOutboundConnectionCache(
        ContactInfo contactInfo)
    {
        synchronized (contactInfo) {
            if (contactInfo.getConnectionCache() == null) {
                OutboundConnectionCache connectionCache = null;
                synchronized (outboundConnectionCaches) {
                    connectionCache = (OutboundConnectionCache)
                        outboundConnectionCaches.get(
                            contactInfo.getConnectionCacheType());
                    if (connectionCache == null) {
                        // REVISIT: Would like to be able to configure
                        // the connection cache type used.
                        connectionCache =
                            new CorbaOutboundConnectionCacheImpl(orb,
                                                                 contactInfo);
                        outboundConnectionCaches.put(
                            contactInfo.getConnectionCacheType(),
                            connectionCache);
                    }
                }
                contactInfo.setConnectionCache(connectionCache);
            }
            return contactInfo.getConnectionCache();
        }
    }

    public Collection getOutboundConnectionCaches()
    {
        return outboundConnectionCaches.values();
    }

    public InboundConnectionCache getInboundConnectionCache(
        Acceptor acceptor)
    {
        synchronized (acceptor) {
            if (acceptor.getConnectionCache() == null) {
                InboundConnectionCache connectionCache = null;
                synchronized (inboundConnectionCaches) {
                    connectionCache = (InboundConnectionCache)
                        inboundConnectionCaches.get(
                            acceptor.getConnectionCacheType());
                    if (connectionCache == null) {
                        // REVISIT: Would like to be able to configure
                        // the connection cache type used.
                        connectionCache =
                            new CorbaInboundConnectionCacheImpl(orb,
                                                                acceptor);
                        inboundConnectionCaches.put(
                            acceptor.getConnectionCacheType(),
                            connectionCache);
                    }
                }
                acceptor.setConnectionCache(connectionCache);
            }
            return acceptor.getConnectionCache();
        }
    }

    public Collection getInboundConnectionCaches()
    {
        return inboundConnectionCaches.values();
    }

    public Selector getSelector(int id)
    {
        return selector;
    }

    public synchronized void registerAcceptor(Acceptor acceptor)
    {
        if (orb.transportDebugFlag) {
            dprint(".registerAcceptor->: " + acceptor);
        }
        acceptors.add(acceptor);
        if (orb.transportDebugFlag) {
            dprint(".registerAcceptor<-: " + acceptor);
        }
    }

    public Collection getAcceptors()
    {
        return getAcceptors(null, null);
    }

    public synchronized void unregisterAcceptor(Acceptor acceptor)
    {
        acceptors.remove(acceptor);
    }

    public void close()
    {
        try {
            if (orb.transportDebugFlag) {
                dprint(".close->");
            }
            for (Object cc : outboundConnectionCaches.values()) {
                ((ConnectionCache)cc).close() ;
            }
            for (Object cc : inboundConnectionCaches.values()) {
                ((ConnectionCache)cc).close() ;
            }
            getSelector(0).close();
        } finally {
            if (orb.transportDebugFlag) {
                dprint(".close<-");
            }
        }
    }

    ////////////////////////////////////////////////////
    //
    // CorbaTransportManager
    //

    public Collection getAcceptors(String objectAdapterManagerId,
                                   ObjectAdapterId objectAdapterId)
    {
        // REVISIT - need to filter based on arguments.

        // REVISIT - initialization will be moved to OA.
        // Lazy initialization of acceptors.
        Iterator iterator = acceptors.iterator();
        while (iterator.hasNext()) {
            Acceptor acceptor = (Acceptor) iterator.next();
            if (acceptor.initialize()) {
                if (acceptor.shouldRegisterAcceptEvent()) {
                    orb.getTransportManager().getSelector(0)
                        .registerForEvent(acceptor.getEventHandler());
                }
            }
        }
        return acceptors;
    }

    // REVISIT - POA specific policies
    public void addToIORTemplate(IORTemplate iorTemplate,
                                 Policies policies,
                                 String codebase,
                                 String objectAdapterManagerId,
                                 ObjectAdapterId objectAdapterId)
    {
        Iterator iterator =
            getAcceptors(objectAdapterManagerId, objectAdapterId).iterator();
        while (iterator.hasNext()) {
            CorbaAcceptor acceptor = (CorbaAcceptor) iterator.next();
            acceptor.addToIORTemplate(iorTemplate, policies, codebase);
        }
    }


    ////////////////////////////////////////////////////
    //
    // implemenation
    //

    protected void dprint(String msg)
    {
        ORBUtility.dprint("CorbaTransportManagerImpl", msg);
    }
}

// End of file.
