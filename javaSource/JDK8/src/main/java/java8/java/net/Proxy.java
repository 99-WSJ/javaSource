/*
 * Copyright (c) 2003, 2013, Oracle and/or its affiliates. All rights reserved.
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

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * This class represents a proxy setting, typically a type (http, socks) and
 * a socket address.
 * A {@code Proxy} is an immutable object.
 *
 * @see     java.net.ProxySelector
 * @author Yingxian Wang
 * @author Jean-Christophe Collet
 * @since   1.5
 */
public class Proxy {

    /**
     * Represents the proxy type.
     *
     * @since 1.5
     */
    public enum Type {
        /**
         * Represents a direct connection, or the absence of a proxy.
         */
        DIRECT,
        /**
         * Represents proxy for high level protocols such as HTTP or FTP.
         */
        HTTP,
        /**
         * Represents a SOCKS (V4 or V5) proxy.
         */
        SOCKS
    };

    private Type type;
    private SocketAddress sa;

    /**
     * A proxy setting that represents a {@code DIRECT} connection,
     * basically telling the protocol handler not to use any proxying.
     * Used, for instance, to create sockets bypassing any other global
     * proxy settings (like SOCKS):
     * <P>
     * {@code Socket s = new Socket(Proxy.NO_PROXY);}
     *
     */
    public final static java.net.Proxy NO_PROXY = new java.net.Proxy();

    // Creates the proxy that represents a {@code DIRECT} connection.
    private Proxy() {
        type = Type.DIRECT;
        sa = null;
    }

    /**
     * Creates an entry representing a PROXY connection.
     * Certain combinations are illegal. For instance, for types Http, and
     * Socks, a SocketAddress <b>must</b> be provided.
     * <P>
     * Use the {@code Proxy.NO_PROXY} constant
     * for representing a direct connection.
     *
     * @param type the {@code Type} of the proxy
     * @param sa the {@code SocketAddress} for that proxy
     * @throws IllegalArgumentException when the type and the address are
     * incompatible
     */
    public Proxy(Type type, SocketAddress sa) {
        if ((type == Type.DIRECT) || !(sa instanceof InetSocketAddress))
            throw new IllegalArgumentException("type " + type + " is not compatible with address " + sa);
        this.type = type;
        this.sa = sa;
    }

    /**
     * Returns the proxy type.
     *
     * @return a Type representing the proxy type
     */
    public Type type() {
        return type;
    }

    /**
     * Returns the socket address of the proxy, or
     * {@code null} if its a direct connection.
     *
     * @return a {@code SocketAddress} representing the socket end
     *         point of the proxy
     */
    public SocketAddress address() {
        return sa;
    }

    /**
     * Constructs a string representation of this Proxy.
     * This String is constructed by calling toString() on its type
     * and concatenating " @ " and the toString() result from its address
     * if its type is not {@code DIRECT}.
     *
     * @return  a string representation of this object.
     */
    public String toString() {
        if (type() == Type.DIRECT)
            return "DIRECT";
        return type() + " @ " + address();
    }

        /**
     * Compares this object against the specified object.
     * The result is {@code true} if and only if the argument is
     * not {@code null} and it represents the same proxy as
     * this object.
     * <p>
     * Two instances of {@code Proxy} represent the same
     * address if both the SocketAddresses and type are equal.
     *
     * @param   obj   the object to compare against.
     * @return  {@code true} if the objects are the same;
     *          {@code false} otherwise.
     * @see InetSocketAddress#equals(Object)
     */
    public final boolean equals(Object obj) {
        if (obj == null || !(obj instanceof java.net.Proxy))
            return false;
        java.net.Proxy p = (java.net.Proxy) obj;
        if (p.type() == type()) {
            if (address() == null) {
                return (p.address() == null);
            } else
                return address().equals(p.address());
        }
        return false;
    }

    /**
     * Returns a hashcode for this Proxy.
     *
     * @return  a hash code value for this Proxy.
     */
    public final int hashCode() {
        if (address() == null)
            return type().hashCode();
        return type().hashCode() + address().hashCode();
    }
}
