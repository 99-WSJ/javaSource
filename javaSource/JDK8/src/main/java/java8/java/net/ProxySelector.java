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

import sun.security.util.SecurityConstants;

import java.io.IOException;
import java.net.NetPermission;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URI;
import java.util.List;

/**
 * Selects the proxy server to use, if any, when connecting to the
 * network resource referenced by a URL. A proxy selector is a
 * concrete sub-class of this class and is registered by invoking the
 * {@link java.net.ProxySelector#setDefault setDefault} method. The
 * currently registered proxy selector can be retrieved by calling
 * {@link java.net.ProxySelector#getDefault getDefault} method.
 *
 * <p> When a proxy selector is registered, for instance, a subclass
 * of URLConnection class should call the {@link #select select}
 * method for each URL request so that the proxy selector can decide
 * if a direct, or proxied connection should be used. The {@link
 * #select select} method returns an iterator over a collection with
 * the preferred connection approach.
 *
 * <p> If a connection cannot be established to a proxy (PROXY or
 * SOCKS) servers then the caller should call the proxy selector's
 * {@link #connectFailed connectFailed} method to notify the proxy
 * selector that the proxy server is unavailable. </p>
 *
 * <P>The default proxy selector does enforce a
 * <a href="doc-files/net-properties.html#Proxies">set of System Properties</a>
 * related to proxy settings.</P>
 *
 * @author Yingxian Wang
 * @author Jean-Christophe Collet
 * @since 1.5
 */
public abstract class ProxySelector {
    /**
     * The system wide proxy selector that selects the proxy server to
     * use, if any, when connecting to a remote object referenced by
     * an URL.
     *
     * @see #setDefault(java.net.ProxySelector)
     */
    private static java.net.ProxySelector theProxySelector;

    static {
        try {
            Class<?> c = Class.forName("sun.net.spi.DefaultProxySelector");
            if (c != null && java.net.ProxySelector.class.isAssignableFrom(c)) {
                theProxySelector = (java.net.ProxySelector) c.newInstance();
            }
        } catch (Exception e) {
            theProxySelector = null;
        }
    }

    /**
     * Gets the system-wide proxy selector.
     *
     * @throws  SecurityException
     *          If a security manager has been installed and it denies
     * {@link NetPermission}{@code ("getProxySelector")}
     * @see #setDefault(java.net.ProxySelector)
     * @return the system-wide {@code ProxySelector}
     * @since 1.5
     */
    public static java.net.ProxySelector getDefault() {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(SecurityConstants.GET_PROXYSELECTOR_PERMISSION);
        }
        return theProxySelector;
    }

    /**
     * Sets (or unsets) the system-wide proxy selector.
     *
     * Note: non-standard protocol handlers may ignore this setting.
     *
     * @param ps The HTTP proxy selector, or
     *          {@code null} to unset the proxy selector.
     *
     * @throws  SecurityException
     *          If a security manager has been installed and it denies
     * {@link NetPermission}{@code ("setProxySelector")}
     *
     * @see #getDefault()
     * @since 1.5
     */
    public static void setDefault(java.net.ProxySelector ps) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(SecurityConstants.SET_PROXYSELECTOR_PERMISSION);
        }
        theProxySelector = ps;
    }

    /**
     * Selects all the applicable proxies based on the protocol to
     * access the resource with and a destination address to access
     * the resource at.
     * The format of the URI is defined as follow:
     * <UL>
     * <LI>http URI for http connections</LI>
     * <LI>https URI for https connections
     * <LI>{@code socket://host:port}<br>
     *     for tcp client sockets connections</LI>
     * </UL>
     *
     * @param   uri
     *          The URI that a connection is required to
     *
     * @return  a List of Proxies. Each element in the
     *          the List is of type
     *          {@link Proxy Proxy};
     *          when no proxy is available, the list will
     *          contain one element of type
     *          {@link Proxy Proxy}
     *          that represents a direct connection.
     * @throws IllegalArgumentException if the argument is null
     */
    public abstract List<Proxy> select(URI uri);

    /**
     * Called to indicate that a connection could not be established
     * to a proxy/socks server. An implementation of this method can
     * temporarily remove the proxies or reorder the sequence of
     * proxies returned by {@link #select(URI)}, using the address
     * and the IOException caught when trying to connect.
     *
     * @param   uri
     *          The URI that the proxy at sa failed to serve.
     * @param   sa
     *          The socket address of the proxy/SOCKS server
     *
     * @param   ioe
     *          The I/O exception thrown when the connect failed.
     * @throws IllegalArgumentException if either argument is null
     */
    public abstract void connectFailed(URI uri, SocketAddress sa, IOException ioe);
}
