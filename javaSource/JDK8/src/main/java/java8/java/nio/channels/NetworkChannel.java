/*
 * Copyright (c) 2007, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java8.java.nio.channels;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.nio.channels.AlreadyBoundException;
import java.nio.channels.Channel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.UnsupportedAddressTypeException;
import java.util.Set;

/**
 * A channel to a network socket.
 *
 * <p> A channel that implements this interface is a channel to a network
 * socket. The {@link #bind(SocketAddress) bind} method is used to bind the
 * socket to a local {@link SocketAddress address}, the {@link #getLocalAddress()
 * getLocalAddress} method returns the address that the socket is bound to, and
 * the {@link #setOption(SocketOption,Object) setOption} and {@link
 * #getOption(SocketOption) getOption} methods are used to set and query socket
 * options.  An implementation of this interface should specify the socket options
 * that it supports.
 *
 * <p> The {@link #bind bind} and {@link #setOption setOption} methods that do
 * not otherwise have a value to return are specified to return the network
 * channel upon which they are invoked. This allows method invocations to be
 * chained. Implementations of this interface should specialize the return type
 * so that method invocations on the implementation class can be chained.
 *
 * @since 1.7
 */

public interface NetworkChannel
    extends Channel
{
    /**
     * Binds the channel's socket to a local address.
     *
     * <p> This method is used to establish an association between the socket and
     * a local address. Once an association is established then the socket remains
     * bound until the channel is closed. If the {@code local} parameter has the
     * value {@code null} then the socket will be bound to an address that is
     * assigned automatically.
     *
     * @param   local
     *          The address to bind the socket, or {@code null} to bind the socket
     *          to an automatically assigned socket address
     *
     * @return  This channel
     *
     * @throws  AlreadyBoundException
     *          If the socket is already bound
     * @throws  UnsupportedAddressTypeException
     *          If the type of the given address is not supported
     * @throws  ClosedChannelException
     *          If the channel is closed
     * @throws  IOException
     *          If some other I/O error occurs
     * @throws  SecurityException
     *          If a security manager is installed and it denies an unspecified
     *          permission. An implementation of this interface should specify
     *          any required permissions.
     *
     * @see #getLocalAddress
     */
    java.nio.channels.NetworkChannel bind(SocketAddress local) throws IOException;

    /**
     * Returns the socket address that this channel's socket is bound to.
     *
     * <p> Where the channel is {@link #bind bound} to an Internet Protocol
     * socket address then the return value from this method is of type {@link
     * java.net.InetSocketAddress}.
     *
     * @return  The socket address that the socket is bound to, or {@code null}
     *          if the channel's socket is not bound
     *
     * @throws  ClosedChannelException
     *          If the channel is closed
     * @throws  IOException
     *          If an I/O error occurs
     */
    SocketAddress getLocalAddress() throws IOException;

    /**
     * Sets the value of a socket option.
     *
     * @param   <T>
     *          The type of the socket option value
     * @param   name
     *          The socket option
     * @param   value
     *          The value of the socket option. A value of {@code null} may be
     *          a valid value for some socket options.
     *
     * @return  This channel
     *
     * @throws  UnsupportedOperationException
     *          If the socket option is not supported by this channel
     * @throws  IllegalArgumentException
     *          If the value is not a valid value for this socket option
     * @throws  ClosedChannelException
     *          If this channel is closed
     * @throws  IOException
     *          If an I/O error occurs
     *
     * @see java.net.StandardSocketOptions
     */
    <T> java.nio.channels.NetworkChannel setOption(SocketOption<T> name, T value) throws IOException;

    /**
     * Returns the value of a socket option.
     *
     * @param   <T>
     *          The type of the socket option value
     * @param   name
     *          The socket option
     *
     * @return  The value of the socket option. A value of {@code null} may be
     *          a valid value for some socket options.
     *
     * @throws  UnsupportedOperationException
     *          If the socket option is not supported by this channel
     * @throws  ClosedChannelException
     *          If this channel is closed
     * @throws  IOException
     *          If an I/O error occurs
     *
     * @see java.net.StandardSocketOptions
     */
    <T> T getOption(SocketOption<T> name) throws IOException;

    /**
     * Returns a set of the socket options supported by this channel.
     *
     * <p> This method will continue to return the set of options even after the
     * channel has been closed.
     *
     * @return  A set of the socket options supported by this channel
     */
    Set<SocketOption<?>> supportedOptions();
}
