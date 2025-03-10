/*
 * Copyright (c) 1998, 2003, Oracle and/or its affiliates. All rights reserved.
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

package java8.com.sun.corba.se.impl.legacy.connection;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.legacy.connection.LegacyServerSocketEndPointInfo;
import com.sun.corba.se.spi.legacy.connection.LegacyServerSocketManager;
import com.sun.corba.se.spi.logging.CORBALogDomains;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.INTERNAL;

import java.util.Collection;
import java.util.Iterator;

public class LegacyServerSocketManagerImpl
    implements
        LegacyServerSocketManager
{
    protected ORB orb;
    private ORBUtilSystemException wrapper ;

    public LegacyServerSocketManagerImpl(ORB orb)
    {
        this.orb = orb;
        wrapper = ORBUtilSystemException.get( orb,
            CORBALogDomains.RPC_TRANSPORT ) ;
    }

    ////////////////////////////////////////////////////
    //
    // LegacyServerSocketManager
    //

    // Only used in ServerManagerImpl.
    public int legacyGetTransientServerPort(String type)
    {
        return legacyGetServerPort(type, false);
    }

    // Only used by POAPolicyMediatorBase.
    public synchronized int legacyGetPersistentServerPort(String socketType)
    {
        if (orb.getORBData().getServerIsORBActivated()) {
            // this server is activated by orbd
            return legacyGetServerPort(socketType, true);
        } else if (orb.getORBData().getPersistentPortInitialized()) {
            // this is a user-activated server
            return orb.getORBData().getPersistentServerPort();
        } else {
            throw wrapper.persistentServerportNotSet(
                CompletionStatus.COMPLETED_MAYBE);
        }
    }

    // Only used by PI IORInfoImpl.
    public synchronized int legacyGetTransientOrPersistentServerPort(
        String socketType)
    {
            return legacyGetServerPort(socketType,
                                       orb.getORBData()
                                       .getServerIsORBActivated());
    }

    // Used in RepositoryImpl, ServerManagerImpl, POAImpl,
    // POAPolicyMediatorBase, TOAImpl.
    // To get either default or bootnaming endpoint.
    public synchronized LegacyServerSocketEndPointInfo legacyGetEndpoint(
        String name)
    {
        Iterator iterator = getAcceptorIterator();
        while (iterator.hasNext()) {
            LegacyServerSocketEndPointInfo endPoint = cast(iterator.next());
            if (endPoint != null && name.equals(endPoint.getName())) {
                return endPoint;
            }
        }
        throw new INTERNAL("No acceptor for: " + name);
    }

    // Check to see if the given port is equal to any of the ORB Server Ports.
    // XXX Does this need to change for the multi-homed case?
    // Used in IIOPProfileImpl, ORBImpl.
    public boolean legacyIsLocalServerPort(int port)
    {
        Iterator iterator = getAcceptorIterator();
        while (iterator.hasNext()) {
            LegacyServerSocketEndPointInfo endPoint = cast(iterator.next());
            if (endPoint != null && endPoint.getPort() == port) {
                return true;
            }
        }
        return false;
    }

    ////////////////////////////////////////////////////
    //
    // Implementation.
    //

    private int legacyGetServerPort (String socketType, boolean isPersistent)
    {
        Iterator endpoints = getAcceptorIterator();
        while (endpoints.hasNext()) {
            LegacyServerSocketEndPointInfo ep = cast(endpoints.next());
            if (ep != null && ep.getType().equals(socketType)) {
                if (isPersistent) {
                    return ep.getLocatorPort();
                } else {
                    return ep.getPort();
                }
            }
        }
        return -1;
    }

    private Iterator getAcceptorIterator()
    {
        Collection acceptors =
            orb.getCorbaTransportManager().getAcceptors(null, null);
        if (acceptors != null) {
            return acceptors.iterator();
        }

        throw wrapper.getServerPortCalledBeforeEndpointsInitialized() ;
    }

    private LegacyServerSocketEndPointInfo cast(Object o)
    {
        if (o instanceof LegacyServerSocketEndPointInfo) {
            return (LegacyServerSocketEndPointInfo) o;
        }
        return null;
    }

    protected void dprint(String msg)
    {
        ORBUtility.dprint("LegacyServerSocketManagerImpl", msg);
    }
}

// End of file.
