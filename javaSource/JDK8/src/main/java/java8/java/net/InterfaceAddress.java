/*
 * Copyright (c) 2005, 2013, Oracle and/or its affiliates. All rights reserved.
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

import java.net.Inet4Address;
import java.net.InetAddress;

/**
 * This class represents a Network Interface address. In short it's an
 * IP address, a subnet mask and a broadcast address when the address is
 * an IPv4 one. An IP address and a network prefix length in the case
 * of IPv6 address.
 *
 * @see java.net.NetworkInterface
 * @since 1.6
 */
public class InterfaceAddress {
    private InetAddress address = null;
    private Inet4Address broadcast = null;
    private short        maskLength = 0;

    /*
     * Package private constructor. Can't be built directly, instances are
     * obtained through the NetworkInterface class.
     */
    InterfaceAddress() {
    }

    /**
     * Returns an {@code InetAddress} for this address.
     *
     * @return the {@code InetAddress} for this address.
     */
    public InetAddress getAddress() {
        return address;
    }

    /**
     * Returns an {@code InetAddress} for the broadcast address
     * for this InterfaceAddress.
     * <p>
     * Only IPv4 networks have broadcast address therefore, in the case
     * of an IPv6 network, {@code null} will be returned.
     *
     * @return the {@code InetAddress} representing the broadcast
     *         address or {@code null} if there is no broadcast address.
     */
    public InetAddress getBroadcast() {
        return broadcast;
    }

    /**
     * Returns the network prefix length for this address. This is also known
     * as the subnet mask in the context of IPv4 addresses.
     * Typical IPv4 values would be 8 (255.0.0.0), 16 (255.255.0.0)
     * or 24 (255.255.255.0). <p>
     * Typical IPv6 values would be 128 (::1/128) or 10 (fe80::203:baff:fe27:1243/10)
     *
     * @return a {@code short} representing the prefix length for the
     *         subnet of that address.
     */
     public short getNetworkPrefixLength() {
        return maskLength;
    }

    /**
     * Compares this object against the specified object.
     * The result is {@code true} if and only if the argument is
     * not {@code null} and it represents the same interface address as
     * this object.
     * <p>
     * Two instances of {@code InterfaceAddress} represent the same
     * address if the InetAddress, the prefix length and the broadcast are
     * the same for both.
     *
     * @param   obj   the object to compare against.
     * @return  {@code true} if the objects are the same;
     *          {@code false} otherwise.
     * @see     java.net.InterfaceAddress#hashCode()
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof java.net.InterfaceAddress)) {
            return false;
        }
        java.net.InterfaceAddress cmp = (java.net.InterfaceAddress) obj;
        if ( !(address == null ? cmp.address == null : address.equals(cmp.address)) )
            return false;
        if ( !(broadcast  == null ? cmp.broadcast == null : broadcast.equals(cmp.broadcast)) )
            return false;
        if (maskLength != cmp.maskLength)
            return false;
        return true;
    }

    /**
     * Returns a hashcode for this Interface address.
     *
     * @return  a hash code value for this Interface address.
     */
    public int hashCode() {
        return address.hashCode() + ((broadcast != null) ? broadcast.hashCode() : 0) + maskLength;
    }

    /**
     * Converts this Interface address to a {@code String}. The
     * string returned is of the form: InetAddress / prefix length [ broadcast address ].
     *
     * @return  a string representation of this Interface address.
     */
    public String toString() {
        return address + "/" + maskLength + " [" + broadcast + "]";
    }

}
