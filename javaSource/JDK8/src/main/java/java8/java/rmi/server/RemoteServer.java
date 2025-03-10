/*
 * Copyright (c) 1996, 2002, Oracle and/or its affiliates. All rights reserved.
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
package java8.java.rmi.server;

import sun.rmi.server.UnicastServerRef;

import java.rmi.server.RemoteObject;
import java.rmi.server.RemoteRef;
import java.rmi.server.ServerNotActiveException;

/**
 * The <code>RemoteServer</code> class is the common superclass to server
 * implementations and provides the framework to support a wide range
 * of remote reference semantics.  Specifically, the functions needed
 * to create and export remote objects (i.e. to make them remotely
 * available) are provided abstractly by <code>RemoteServer</code> and
 * concretely by its subclass(es).
 *
 * @author  Ann Wollrath
 * @since   JDK1.1
 */
public abstract class RemoteServer extends RemoteObject
{
    /* indicate compatibility with JDK 1.1.x version of class */
    private static final long serialVersionUID = -4100238210092549637L;

    /**
     * Constructs a <code>RemoteServer</code>.
     * @since JDK1.1
     */
    protected RemoteServer() {
        super();
    }

    /**
     * Constructs a <code>RemoteServer</code> with the given reference type.
     *
     * @param ref the remote reference
     * @since JDK1.1
     */
    protected RemoteServer(RemoteRef ref) {
        super(ref);
    }

    /**
     * Returns a string representation of the client host for the
     * remote method invocation being processed in the current thread.
     *
     * @return  a string representation of the client host
     *
     * @throws  ServerNotActiveException if no remote method invocation
     * is being processed in the current thread
     *
     * @since   JDK1.1
     */
    public static String getClientHost() throws ServerNotActiveException {
        return sun.rmi.transport.tcp.TCPTransport.getClientHost();
    }

    /**
     * Log RMI calls to the output stream <code>out</code>. If
     * <code>out</code> is <code>null</code>, call logging is turned off.
     *
     * <p>If there is a security manager, its
     * <code>checkPermission</code> method will be invoked with a
     * <code>java.util.logging.LoggingPermission("control")</code>
     * permission; this could result in a <code>SecurityException</code>.
     *
     * @param   out the output stream to which RMI calls should be logged
     * @throws  SecurityException  if there is a security manager and
     *          the invocation of its <code>checkPermission</code> method
     *          fails
     * @see #getLog
     * @since JDK1.1
     */
    public static void setLog(java.io.OutputStream out)
    {
        logNull = (out == null);
        UnicastServerRef.callLog.setOutputStream(out);
    }

    /**
     * Returns stream for the RMI call log.
     * @return the call log
     * @see #setLog
     * @since JDK1.1
     */
    public static java.io.PrintStream getLog()
    {
        return (logNull ? null : UnicastServerRef.callLog.getPrintStream());
    }

    // initialize log status
    private static boolean logNull = !UnicastServerRef.logCalls;
}
