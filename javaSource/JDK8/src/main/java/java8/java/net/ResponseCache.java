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
import java.net.CacheResponse;
import java.net.NetPermission;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Represents implementations of URLConnection caches. An instance of
 * such a class can be registered with the system by doing
 * ResponseCache.setDefault(ResponseCache), and the system will call
 * this object in order to:
 *
 *    <ul><li>store resource data which has been retrieved from an
 *            external source into the cache</li>
 *         <li>try to fetch a requested resource that may have been
 *            stored in the cache</li>
 *    </ul>
 *
 * The ResponseCache implementation decides which resources
 * should be cached, and for how long they should be cached. If a
 * request resource cannot be retrieved from the cache, then the
 * protocol handlers will fetch the resource from its original
 * location.
 *
 * The settings for URLConnection#useCaches controls whether the
 * protocol is allowed to use a cached response.
 *
 * For more information on HTTP caching, see <a
 * href="http://www.ietf.org/rfc/rfc2616.txt"><i>RFC&nbsp;2616: Hypertext
 * Transfer Protocol -- HTTP/1.1</i></a>
 *
 * @author Yingxian Wang
 * @since 1.5
 */
public abstract class ResponseCache {

    /**
     * The system wide cache that provides access to a url
     * caching mechanism.
     *
     * @see #setDefault(java.net.ResponseCache)
     * @see #getDefault()
     */
    private static java.net.ResponseCache theResponseCache;

    /**
     * Gets the system-wide response cache.
     *
     * @throws  SecurityException
     *          If a security manager has been installed and it denies
     * {@link NetPermission}{@code ("getResponseCache")}
     *
     * @see #setDefault(java.net.ResponseCache)
     * @return the system-wide {@code ResponseCache}
     * @since 1.5
     */
    public synchronized  static java.net.ResponseCache getDefault() {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(SecurityConstants.GET_RESPONSECACHE_PERMISSION);
        }
        return theResponseCache;
    }

    /**
     * Sets (or unsets) the system-wide cache.
     *
     * Note: non-standard procotol handlers may ignore this setting.
     *
     * @param responseCache The response cache, or
     *          {@code null} to unset the cache.
     *
     * @throws  SecurityException
     *          If a security manager has been installed and it denies
     * {@link NetPermission}{@code ("setResponseCache")}
     *
     * @see #getDefault()
     * @since 1.5
     */
    public synchronized static void setDefault(java.net.ResponseCache responseCache) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(SecurityConstants.SET_RESPONSECACHE_PERMISSION);
        }
        theResponseCache = responseCache;
    }

    /**
     * Retrieve the cached response based on the requesting uri,
     * request method and request headers. Typically this method is
     * called by the protocol handler before it sends out the request
     * to get the network resource. If a cached response is returned,
     * that resource is used instead.
     *
     * @param uri a {@code URI} used to reference the requested
     *            network resource
     * @param rqstMethod a {@code String} representing the request
     *            method
     * @param rqstHeaders - a Map from request header
     *            field names to lists of field values representing
     *            the current request headers
     * @return a {@code CacheResponse} instance if available
     *          from cache, or null otherwise
     * @throws  IOException if an I/O error occurs
     * @throws  IllegalArgumentException if any one of the arguments is null
     *
     * @see     URLConnection#setUseCaches(boolean)
     * @see     URLConnection#getUseCaches()
     * @see     URLConnection#setDefaultUseCaches(boolean)
     * @see     URLConnection#getDefaultUseCaches()
     */
    public abstract CacheResponse
        get(URI uri, String rqstMethod, Map<String, List<String>> rqstHeaders)
        throws IOException;

    /**
     * The protocol handler calls this method after a resource has
     * been retrieved, and the ResponseCache must decide whether or
     * not to store the resource in its cache. If the resource is to
     * be cached, then put() must return a CacheRequest object which
     * contains an OutputStream that the protocol handler will
     * use to write the resource into the cache. If the resource is
     * not to be cached, then put must return null.
     *
     * @param uri a {@code URI} used to reference the requested
     *            network resource
     * @param conn - a URLConnection instance that is used to fetch
     *            the response to be cached
     * @return a {@code CacheRequest} for recording the
     *            response to be cached. Null return indicates that
     *            the caller does not intend to cache the response.
     * @throws IOException if an I/O error occurs
     * @throws IllegalArgumentException if any one of the arguments is
     *            null
     */
    public abstract CacheRequest put(URI uri, URLConnection conn)  throws IOException;
}
