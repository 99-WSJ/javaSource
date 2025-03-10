/*
 * Copyright (c) 2002, 2004, Oracle and/or its affiliates. All rights reserved.
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

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.transport.CorbaContactInfoBase;
import com.sun.corba.se.impl.transport.SocketOrChannelConnectionImpl;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import com.sun.corba.se.spi.transport.CorbaTransportManager;
import com.sun.corba.se.spi.transport.SocketInfo;

/**
 * @author Harold Carr
 */
public class SocketOrChannelContactInfoImpl
    extends CorbaContactInfoBase
    implements SocketInfo
{
    protected boolean isHashCodeCached = false;
    protected int cachedHashCode;

    protected String socketType;
    protected String hostname;
    protected int    port;

    // XREVISIT
    // See SocketOrChannelAcceptorImpl.createMessageMediator
    // See SocketFactoryContactInfoImpl.constructor()
    // See SocketOrChannelContactInfoImpl.constructor()
    protected SocketOrChannelContactInfoImpl()
    {
    }

    protected SocketOrChannelContactInfoImpl(
        ORB orb,
        CorbaContactInfoList contactInfoList)
    {
        this.orb = orb;
        this.contactInfoList = contactInfoList;
    }

    public SocketOrChannelContactInfoImpl(
        ORB orb,
        CorbaContactInfoList contactInfoList,
        String socketType,
        String hostname,
        int port)
    {
        this(orb, contactInfoList);
        this.socketType = socketType;
        this.hostname = hostname;
        this.port     = port;
    }

    // XREVISIT
    public SocketOrChannelContactInfoImpl(
        ORB orb,
        CorbaContactInfoList contactInfoList,
        IOR effectiveTargetIOR,
        short addressingDisposition,
        String socketType,
        String hostname,
        int port)
    {
        this(orb, contactInfoList, socketType, hostname, port);
        this.effectiveTargetIOR = effectiveTargetIOR;
        this.addressingDisposition = addressingDisposition;
    }

    ////////////////////////////////////////////////////
    //
    // pept.transport.ContactInfo
    //

    public boolean isConnectionBased()
    {
        return true;
    }

    public boolean shouldCacheConnection()
    {
        return true;
    }

    public String getConnectionCacheType()
    {
        return CorbaTransportManager.SOCKET_OR_CHANNEL_CONNECTION_CACHE;
    }

    public Connection createConnection()
    {
        Connection connection =
            new SocketOrChannelConnectionImpl(orb, this,
                                              socketType, hostname, port);
        return connection;
    }

    ////////////////////////////////////////////////////
    //
    // spi.transport.CorbaContactInfo
    //

    public String getMonitoringName()
    {
        return "SocketConnections";
    }

    ////////////////////////////////////////////////////
    //
    // pept.transport.ContactInfo
    //

    public String getType()
    {
        return socketType;
    }

    public String getHost()
    {
        return hostname;
    }

    public int getPort()
    {
        return port;
    }

    ////////////////////////////////////////////////////
    //
    // java.lang.Object
    //

    public int hashCode()
    {
        if (! isHashCodeCached) {
            cachedHashCode = socketType.hashCode() ^ hostname.hashCode() ^ port;
            isHashCodeCached = true;
        }
        return cachedHashCode;
    }

    public boolean equals(Object obj)
    {
        if (obj == null) {
            return false;
        } else if (!(obj instanceof com.sun.corba.se.impl.transport.SocketOrChannelContactInfoImpl)) {
            return false;
        }

        com.sun.corba.se.impl.transport.SocketOrChannelContactInfoImpl other =
            (com.sun.corba.se.impl.transport.SocketOrChannelContactInfoImpl) obj;

        if (port != other.port) {
            return false;
        }
        if (!hostname.equals(other.hostname)) {
            return false;
        }
        if (socketType == null) {
            if (other.socketType != null) {
                return false;
            }
        } else if (!socketType.equals(other.socketType)) {
            return false;
        }
        return true;
    }

    public String toString()
    {
        return
            "SocketOrChannelContactInfoImpl["
            + socketType + " "
            + hostname + " "
            + port
            + "]";
    }

    ////////////////////////////////////////////////////
    //
    // Implementation
    //

    protected void dprint(String msg)
    {
        ORBUtility.dprint("SocketOrChannelContactInfoImpl", msg);
    }
}

// End of file.
