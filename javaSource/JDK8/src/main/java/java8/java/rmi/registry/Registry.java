/*
 * Copyright (c) 1996, 2001, Oracle and/or its affiliates. All rights reserved.
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
package java8.java.rmi.registry;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;

/**
 * <code>Registry</code> is a remote interface to a simple remote
 * object registry that provides methods for storing and retrieving
 * remote object references bound with arbitrary string names.  The
 * <code>bind</code>, <code>unbind</code>, and <code>rebind</code>
 * methods are used to alter the name bindings in the registry, and
 * the <code>lookup</code> and <code>list</code> methods are used to
 * query the current name bindings.
 *
 * <p>In its typical usage, a <code>Registry</code> enables RMI client
 * bootstrapping: it provides a simple means for a client to obtain an
 * initial reference to a remote object.  Therefore, a registry's
 * remote object implementation is typically exported with a
 * well-known address, such as with a well-known {@link
 * java.rmi.server.ObjID#REGISTRY_ID ObjID} and TCP port number
 * (default is {@link #REGISTRY_PORT 1099}).
 *
 * <p>The {@link LocateRegistry} class provides a programmatic API for
 * constructing a bootstrap reference to a <code>Registry</code> at a
 * remote address (see the static <code>getRegistry</code> methods)
 * and for creating and exporting a <code>Registry</code> in the
 * current VM on a particular local address (see the static
 * <code>createRegistry</code> methods).
 *
 * <p>A <code>Registry</code> implementation may choose to restrict
 * access to some or all of its methods (for example, methods that
 * mutate the registry's bindings may be restricted to calls
 * originating from the local host).  If a <code>Registry</code>
 * method chooses to deny access for a given invocation, its
 * implementation may throw {@link AccessException}, which
 * (because it extends {@link RemoteException}) will be
 * wrapped in a {@link java.rmi.ServerException} when caught by a
 * remote client.
 *
 * <p>The names used for bindings in a <code>Registry</code> are pure
 * strings, not parsed.  A service which stores its remote reference
 * in a <code>Registry</code> may wish to use a package name as a
 * prefix in the name binding to reduce the likelihood of name
 * collisions in the registry.
 *
 * @author      Ann Wollrath
 * @author      Peter Jones
 * @since       JDK1.1
 * @see         LocateRegistry
 */
public interface Registry extends Remote {

    /** Well known port for registry. */
    public static final int REGISTRY_PORT = 1099;

    /**
     * Returns the remote reference bound to the specified
     * <code>name</code> in this registry.
     *
     * @param   name the name for the remote reference to look up
     *
     * @return  a reference to a remote object
     *
     * @throws  NotBoundException if <code>name</code> is not currently bound
     *
     * @throws  RemoteException if remote communication with the
     * registry failed; if exception is a <code>ServerException</code>
     * containing an <code>AccessException</code>, then the registry
     * denies the caller access to perform this operation
     *
     * @throws  AccessException if this registry is local and it denies
     * the caller access to perform this operation
     *
     * @throws  NullPointerException if <code>name</code> is <code>null</code>
     */
    public Remote lookup(String name)
        throws RemoteException, NotBoundException, AccessException;

    /**
     * Binds a remote reference to the specified <code>name</code> in
     * this registry.
     *
     * @param   name the name to associate with the remote reference
     * @param   obj a reference to a remote object (usually a stub)
     *
     * @throws  AlreadyBoundException if <code>name</code> is already bound
     *
     * @throws  RemoteException if remote communication with the
     * registry failed; if exception is a <code>ServerException</code>
     * containing an <code>AccessException</code>, then the registry
     * denies the caller access to perform this operation (if
     * originating from a non-local host, for example)
     *
     * @throws  AccessException if this registry is local and it denies
     * the caller access to perform this operation
     *
     * @throws  NullPointerException if <code>name</code> is
     * <code>null</code>, or if <code>obj</code> is <code>null</code>
     */
    public void bind(String name, Remote obj)
        throws RemoteException, AlreadyBoundException, AccessException;

    /**
     * Removes the binding for the specified <code>name</code> in
     * this registry.
     *
     * @param   name the name of the binding to remove
     *
     * @throws  NotBoundException if <code>name</code> is not currently bound
     *
     * @throws  RemoteException if remote communication with the
     * registry failed; if exception is a <code>ServerException</code>
     * containing an <code>AccessException</code>, then the registry
     * denies the caller access to perform this operation (if
     * originating from a non-local host, for example)
     *
     * @throws  AccessException if this registry is local and it denies
     * the caller access to perform this operation
     *
     * @throws  NullPointerException if <code>name</code> is <code>null</code>
     */
    public void unbind(String name)
        throws RemoteException, NotBoundException, AccessException;

    /**
     * Replaces the binding for the specified <code>name</code> in
     * this registry with the supplied remote reference.  If there is
     * an existing binding for the specified <code>name</code>, it is
     * discarded.
     *
     * @param   name the name to associate with the remote reference
     * @param   obj a reference to a remote object (usually a stub)
     *
     * @throws  RemoteException if remote communication with the
     * registry failed; if exception is a <code>ServerException</code>
     * containing an <code>AccessException</code>, then the registry
     * denies the caller access to perform this operation (if
     * originating from a non-local host, for example)
     *
     * @throws  AccessException if this registry is local and it denies
     * the caller access to perform this operation
     *
     * @throws  NullPointerException if <code>name</code> is
     * <code>null</code>, or if <code>obj</code> is <code>null</code>
     */
    public void rebind(String name, Remote obj)
        throws RemoteException, AccessException;

    /**
     * Returns an array of the names bound in this registry.  The
     * array will contain a snapshot of the names bound in this
     * registry at the time of the given invocation of this method.
     *
     * @return  an array of the names bound in this registry
     *
     * @throws  RemoteException if remote communication with the
     * registry failed; if exception is a <code>ServerException</code>
     * containing an <code>AccessException</code>, then the registry
     * denies the caller access to perform this operation
     *
     * @throws  AccessException if this registry is local and it denies
     * the caller access to perform this operation
     */
    public String[] list() throws RemoteException, AccessException;
}
