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

package java8.java.net;

import java.net.StandardSocketOptions;

/**
 * A socket option associated with a socket.
 *
 * <p> In the {@link java.nio.channels channels} package, the {@link
 * java.nio.channels.NetworkChannel} interface defines the {@link
 * java.nio.channels.NetworkChannel#setOption(java.net.SocketOption,Object) setOption}
 * and {@link java.nio.channels.NetworkChannel#getOption(java.net.SocketOption) getOption}
 * methods to set and query the channel's socket options.
 *
 * @param   <T>     The type of the socket option value.
 *
 * @since 1.7
 *
 * @see StandardSocketOptions
 */

public interface SocketOption<T> {

    /**
     * Returns the name of the socket option.
     *
     * @return the name of the socket option
     */
    String name();

    /**
     * Returns the type of the socket option value.
     *
     * @return the type of the socket option value
     */
    Class<T> type();
}
