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

package java8.com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.pept.transport.ConnectionCache;
import com.sun.corba.se.spi.logging.CORBALogDomains;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.transport.CorbaConnection;
import com.sun.corba.se.spi.transport.CorbaConnectionCache;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author Harold Carr
 */
public abstract class CorbaConnectionCacheBase
    implements
        ConnectionCache,
        CorbaConnectionCache
{
    protected ORB orb;
    protected long timestamp = 0;
    protected String cacheType;
    protected String monitoringName;
    protected ORBUtilSystemException wrapper;

    protected CorbaConnectionCacheBase(ORB orb, String cacheType,
                                       String monitoringName)
    {
        this.orb = orb;
        this.cacheType = cacheType;
        this.monitoringName = monitoringName;
        wrapper =ORBUtilSystemException.get(orb,CORBALogDomains.RPC_TRANSPORT);
        registerWithMonitoring();
        dprintCreation();
    }

    ////////////////////////////////////////////////////
    //
    // pept.transport.ConnectionCache
    //

    public String getCacheType()
    {
        return cacheType;
    }

    public synchronized void stampTime(Connection c)
    {
        // _REVISIT_ Need to worry about wrap around some day
        c.setTimeStamp(timestamp++);
    }

    public long numberOfConnections()
    {
        synchronized (backingStore()) {
            return values().size();
        }
    }

    public void close() {
        synchronized (backingStore()) {
            for (Object obj : values()) {
                ((CorbaConnection)obj).closeConnectionResources() ;
            }
        }
    }

    public long numberOfIdleConnections()
    {
        long count = 0;
        synchronized (backingStore()) {
            Iterator connections = values().iterator();
            while (connections.hasNext()) {
                if (! ((Connection)connections.next()).isBusy()) {
                    count++;
                }
            }
        }
        return count;
    }

    public long numberOfBusyConnections()
    {
        long count = 0;
        synchronized (backingStore()) {
            Iterator connections = values().iterator();
            while (connections.hasNext()) {
                if (((Connection)connections.next()).isBusy()) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Discarding least recently used Connections that are not busy
     *
     * This method must be synchronized since one WorkerThread could
     * be reclaming connections inside the synchronized backingStore
     * block and a second WorkerThread (or a SelectorThread) could have
     * already executed the if (numberOfConnections <= .... ). As a
     * result the second thread would also attempt to reclaim connections.
     *
     * If connection reclamation becomes a performance issue, the connection
     * reclamation could make its own task and consequently executed in
     * a separate thread.
     * Currently, the accept & reclaim are done in the same thread, WorkerThread
     * by default. It could be changed such that the SelectorThread would do
     * it for SocketChannels and WorkerThreads for Sockets by updating the
     * ParserTable.
     */
    synchronized public boolean reclaim()
    {
        try {
            long numberOfConnections = numberOfConnections();

            if (orb.transportDebugFlag) {
                dprint(".reclaim->: " + numberOfConnections
                        + " ("
                        + orb.getORBData().getHighWaterMark()
                        + "/"
                        + orb.getORBData().getLowWaterMark()
                        + "/"
                        + orb.getORBData().getNumberToReclaim()
                        + ")");
            }

            if (numberOfConnections <= orb.getORBData().getHighWaterMark() ||
                numberOfConnections < orb.getORBData().getLowWaterMark()) {
                return false;
            }

            Object backingStore = backingStore();
            synchronized (backingStore) {

                 // REVISIT - A less expensive alternative connection reclaiming
                 //           algorithm could be investigated.

                for (int i=0; i < orb.getORBData().getNumberToReclaim(); i++) {
                    Connection toClose = null;
                    long lru = Long.MAX_VALUE;
                    Iterator iterator = values().iterator();

                    // Find least recently used and not busy connection in cache
                    while ( iterator.hasNext() ) {
                        Connection c = (Connection) iterator.next();
                        if ( !c.isBusy() && c.getTimeStamp() < lru ) {
                            toClose = c;
                            lru = c.getTimeStamp();
                        }
                    }

                    if ( toClose == null ) {
                        return false;
                    }

                    try {
                        if (orb.transportDebugFlag) {
                            dprint(".reclaim: closing: " + toClose);
                        }
                        toClose.close();
                    } catch (Exception ex) {
                        // REVISIT - log
                    }
                }

                if (orb.transportDebugFlag) {
                    dprint(".reclaim: connections reclaimed ("
                            + (numberOfConnections - numberOfConnections()) + ")");
                }
            }

            // XXX is necessary to do a GC to reclaim
            // closed network connections ??
            // java.lang.System.gc();

            return true;
        } finally {
            if (orb.transportDebugFlag) {
                dprint(".reclaim<-: " + numberOfConnections());
            }
        }
    }

    ////////////////////////////////////////////////////
    //
    // spi.transport.ConnectionCache
    //

    public String getMonitoringName()
    {
        return monitoringName;
    }

    ////////////////////////////////////////////////////
    //
    // Implementation
    //

    // This is public so folb.Server test can access it.
    public abstract Collection values();

    protected abstract Object backingStore();

    protected abstract void registerWithMonitoring();

    protected void dprintCreation()
    {
        if (orb.transportDebugFlag) {
            dprint(".constructor: cacheType: " + getCacheType()
                   + " monitoringName: " + getMonitoringName());
        }
    }

    protected void dprintStatistics()
    {
        if (orb.transportDebugFlag) {
            dprint(".stats: "
                   + numberOfConnections() + "/total "
                   + numberOfBusyConnections() + "/busy "
                   + numberOfIdleConnections() + "/idle"
                   + " ("
                   + orb.getORBData().getHighWaterMark() + "/"
                   + orb.getORBData().getLowWaterMark() + "/"
                   + orb.getORBData().getNumberToReclaim()
                   + ")");
        }
    }

    protected void dprint(String msg)
    {
        ORBUtility.dprint("CorbaConnectionCacheBase", msg);
    }
}

// End of file.
