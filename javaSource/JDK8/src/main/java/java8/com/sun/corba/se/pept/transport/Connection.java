/*
 * Copyright (c) 2001, 2003, Oracle and/or its affiliates. All rights reserved.
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

import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.pept.transport.Acceptor;
import com.sun.corba.se.pept.transport.ConnectionCache;
import com.sun.corba.se.pept.transport.ContactInfo;
import com.sun.corba.se.pept.transport.EventHandler;


/**
 * <p><code>Connection</code> represents a <em>transport</em> in the
 * PEPt architecture.</p>
 *
 * @author Harold Carr
*/
public interface Connection
{
    /**
     * Used to determine if the <code>Connection</code> should register
     * with the
     * {@link com.sun.corba.se.pept.transport.TransportManager
     * TransportManager}
     * {@link com.sun.corba.se.pept.transport.Selector Selector}
     * to handle read events.
     *
     * For example, an HTTP transport would not register since the requesting
     * thread would just block on read when waiting for the reply.
     *
     * @return <code>true</code> if it should be registered.
     */
    public boolean shouldRegisterReadEvent();

    /**
     * Used to determine if the <code>Connection</code> should register
     * with the
     * {@link com.sun.corba.se.pept.transport.TransportManager
     * TransportManager}
     * {@link com.sun.corba.se.pept.transport.Selector Selector}
     * to handle read events.
     *
     * For example, an HTTP transport would not register since the requesting
     * thread would just block on read when waiting for the reply.
     *
     * @return <code>true</code> if it should be registered.
     */
    public boolean shouldRegisterServerReadEvent(); // REVISIT - why special?

    /**
     * Called to read incoming messages.
     *
     * @return <code>true</code> if the thread calling read can be released.
     */
    public boolean read();

    /**
     * Close the <code>Connection</code>.
     *
     */
    public void close();

    // REVISIT: replace next two with PlugInFactory (implemented by ContactInfo
    // and Acceptor).

    /**
     * Get the
     * {@link Acceptor Acceptor}
     * that created this <code>Connection</code>.
     *
     * @return
     * {@link Acceptor Acceptor}
     */
    public Acceptor getAcceptor();

    /**
     * Get the
     * {@link ContactInfo ContactInfo}
     * that created this <code>Connection</code>.
     *
     * @return
     * {@link ContactInfo ContactInfo}
     */
    public ContactInfo getContactInfo();

    /**
     * Get the
     * {@link EventHandler EventHandler}
     * associated with this <code>Acceptor</code>.
     *
     * @return
     * {@link EventHandler EventHandler}
     */
    public EventHandler getEventHandler();

    /**
     * Indicates whether a
     * {@link ContactInfo ContactInfo}
     * or a
     * {@link Acceptor Acceptor}
     * created the
     * <code>Connection</code>.
     *
     * @return <code>true</code> if <code>Connection</code> an
     * {@link Acceptor Acceptor}
     * created the <code>Connection</code>.
     */
    public boolean isServer();

    /**
     * Indicates if the <code>Connection</code> is in the process of
     * sending or receiving a message.
     *
     * @return <code>true</code> if the <code>Connection</code> is busy.
     */
    public boolean isBusy();

    /**
     * Timestamps are used for connection management, in particular, for
     * reclaiming idle <code>Connection</code>s.
     *
     * @return the "time" the <code>Connection</code> was last used.
     */
    public long getTimeStamp();

    /**
     * Timestamps are used for connection management, in particular, for
     * reclaiming idle <code>Connection</code>s.
     *
     * @param time - the "time" the <code>Connection</code> was last used.
     */
    public void setTimeStamp(long time);

    /**
     * The "state" of the <code>Connection</code>.
     *
     * param state
     */
    public void setState(String state);

    /**
     * Grab a write lock on the <code>Connection</code>.
     *
     * If another thread already has a write lock then the calling
     * thread will block until the lock is released.  The calling
     * thread must call
     * {@link #writeUnlock}
     * when it is done.
     */
    public void writeLock();

    /**
     * Release a write lock on the <code>Connection</code>.
     */
    public void writeUnlock();

    /*
     * Send the data encoded in
     * {@link com.sun.corba.se.pept.encoding.OutputObject OutputObject}
     * on the <code>Connection</code>.
     *
     * @param outputObject
     */
    public void sendWithoutLock(OutputObject outputObject);

    /**
     * Register an invocation's
     * {@link MessageMediator MessageMediator}
     * with the <code>Connection</code>.
     *
     * This is useful in protocols which support fragmentation.
     *
     * @param messageMediator
     */
    public void registerWaiter(MessageMediator messageMediator);

    /**
     * If a message expect's a response then this method is called.
     *
     * This method might block on a read (e.g., HTTP), put the calling
     * thread to sleep while another thread read's the response (e.g., GIOP),
     * or it may use the calling thread to perform the server-side work
     * (e.g., Solaris Doors).
     *
     * @param messageMediator
     */
    public InputObject waitForResponse(MessageMediator messageMediator);

    /**
     * Unregister an invocation's
     * {@link MessageMediator MessageMediator}
     * with the <code>Connection</code>.
     *
     * @param messageMediator
     */
    public void unregisterWaiter(MessageMediator messageMediator);

    public void setConnectionCache(ConnectionCache connectionCache);

    public ConnectionCache getConnectionCache();
}

// End of file.
