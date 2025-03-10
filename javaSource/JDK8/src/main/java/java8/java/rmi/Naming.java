/*
 * Copyright (c) 1996, 2005, Oracle and/or its affiliates. All rights reserved.
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
package java8.java.rmi;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * The <code>Naming</code> class provides methods for storing and obtaining
 * references to remote objects in a remote object registry.  Each method of
 * the <code>Naming</code> class takes as one of its arguments a name that
 * is a <code>java.lang.String</code> in URL format (without the
 * scheme component) of the form:
 *
 * <PRE>
 *    //host:port/name
 * </PRE>
 *
 * <P>where <code>host</code> is the host (remote or local) where the registry
 * is located, <code>port</code> is the port number on which the registry
 * accepts calls, and where <code>name</code> is a simple string uninterpreted
 * by the registry. Both <code>host</code> and <code>port</code> are optional.
 * If <code>host</code> is omitted, the host defaults to the local host. If
 * <code>port</code> is omitted, then the port defaults to 1099, the
 * "well-known" port that RMI's registry, <code>rmiregistry</code>, uses.
 *
 * <P><em>Binding</em> a name for a remote object is associating or
 * registering a name for a remote object that can be used at a later time to
 * look up that remote object.  A remote object can be associated with a name
 * using the <code>Naming</code> class's <code>bind</code> or
 * <code>rebind</code> methods.
 *
 * <P>Once a remote object is registered (bound) with the RMI registry on the
 * local host, callers on a remote (or local) host can lookup the remote
 * object by name, obtain its reference, and then invoke remote methods on the
 * object.  A registry may be shared by all servers running on a host or an
 * individual server process may create and use its own registry if desired
 * (see <code>java.rmi.registry.LocateRegistry.createRegistry</code> method
 * for details).
 *
 * @author  Ann Wollrath
 * @author  Roger Riggs
 * @since   JDK1.1
 * @see     Registry
 * @see     LocateRegistry
 * @see     LocateRegistry#createRegistry(int)
 */
public final class Naming {
    /**
     * Disallow anyone from creating one of these
     */
    private Naming() {}

    /**
     * Returns a reference, a stub, for the remote object associated
     * with the specified <code>name</code>.
     *
     * @param name a name in URL format (without the scheme component)
     * @return a reference for a remote object
     * @exception NotBoundException if name is not currently bound
     * @exception RemoteException if registry could not be contacted
     * @exception AccessException if this operation is not permitted
     * @exception MalformedURLException if the name is not an appropriately
     *  formatted URL
     * @since JDK1.1
     */
    public static Remote lookup(String name)
        throws NotBoundException,
            MalformedURLException,
            RemoteException
    {
        ParsedNamingURL parsed = parseURL(name);
        Registry registry = getRegistry(parsed);

        if (parsed.name == null)
            return registry;
        return registry.lookup(parsed.name);
    }

    /**
     * Binds the specified <code>name</code> to a remote object.
     *
     * @param name a name in URL format (without the scheme component)
     * @param obj a reference for the remote object (usually a stub)
     * @exception AlreadyBoundException if name is already bound
     * @exception MalformedURLException if the name is not an appropriately
     *  formatted URL
     * @exception RemoteException if registry could not be contacted
     * @exception AccessException if this operation is not permitted (if
     * originating from a non-local host, for example)
     * @since JDK1.1
     */
    public static void bind(String name, Remote obj)
        throws AlreadyBoundException,
            MalformedURLException,
            RemoteException
    {
        ParsedNamingURL parsed = parseURL(name);
        Registry registry = getRegistry(parsed);

        if (obj == null)
            throw new NullPointerException("cannot bind to null");

        registry.bind(parsed.name, obj);
    }

    /**
     * Destroys the binding for the specified name that is associated
     * with a remote object.
     *
     * @param name a name in URL format (without the scheme component)
     * @exception NotBoundException if name is not currently bound
     * @exception MalformedURLException if the name is not an appropriately
     *  formatted URL
     * @exception RemoteException if registry could not be contacted
     * @exception AccessException if this operation is not permitted (if
     * originating from a non-local host, for example)
     * @since JDK1.1
     */
    public static void unbind(String name)
        throws RemoteException,
            NotBoundException,
            MalformedURLException
    {
        ParsedNamingURL parsed = parseURL(name);
        Registry registry = getRegistry(parsed);

        registry.unbind(parsed.name);
    }

    /**
     * Rebinds the specified name to a new remote object. Any existing
     * binding for the name is replaced.
     *
     * @param name a name in URL format (without the scheme component)
     * @param obj new remote object to associate with the name
     * @exception MalformedURLException if the name is not an appropriately
     *  formatted URL
     * @exception RemoteException if registry could not be contacted
     * @exception AccessException if this operation is not permitted (if
     * originating from a non-local host, for example)
     * @since JDK1.1
     */
    public static void rebind(String name, Remote obj)
        throws RemoteException, MalformedURLException
    {
        ParsedNamingURL parsed = parseURL(name);
        Registry registry = getRegistry(parsed);

        if (obj == null)
            throw new NullPointerException("cannot bind to null");

        registry.rebind(parsed.name, obj);
    }

    /**
     * Returns an array of the names bound in the registry.  The names are
     * URL-formatted (without the scheme component) strings. The array contains
     * a snapshot of the names present in the registry at the time of the
     * call.
     *
     * @param   name a registry name in URL format (without the scheme
     *          component)
     * @return  an array of names (in the appropriate format) bound
     *          in the registry
     * @exception MalformedURLException if the name is not an appropriately
     *  formatted URL
     * @exception RemoteException if registry could not be contacted.
     * @since JDK1.1
     */
    public static String[] list(String name)
        throws RemoteException, MalformedURLException
    {
        ParsedNamingURL parsed = parseURL(name);
        Registry registry = getRegistry(parsed);

        String prefix = "";
        if (parsed.port > 0 || !parsed.host.equals(""))
            prefix += "//" + parsed.host;
        if (parsed.port > 0)
            prefix += ":" + parsed.port;
        prefix += "/";

        String[] names = registry.list();
        for (int i = 0; i < names.length; i++) {
            names[i] = prefix + names[i];
        }
        return names;
    }

    /**
     * Returns a registry reference obtained from information in the URL.
     */
    private static Registry getRegistry(ParsedNamingURL parsed)
        throws RemoteException
    {
        return LocateRegistry.getRegistry(parsed.host, parsed.port);
    }

    /**
     * Dissect Naming URL strings to obtain referenced host, port and
     * object name.
     *
     * @return an object which contains each of the above
     * components.
     *
     * @exception MalformedURLException if given url string is malformed
     */
    private static ParsedNamingURL parseURL(String str)
        throws MalformedURLException
    {
        try {
            return intParseURL(str);
        } catch (URISyntaxException ex) {
            /* With RFC 3986 URI handling, 'rmi://:<port>' and
             * '//:<port>' forms will result in a URI syntax exception
             * Convert the authority to a localhost:<port> form
             */
            MalformedURLException mue = new MalformedURLException(
                "invalid URL String: " + str);
            mue.initCause(ex);
            int indexSchemeEnd = str.indexOf(':');
            int indexAuthorityBegin = str.indexOf("//:");
            if (indexAuthorityBegin < 0) {
                throw mue;
            }
            if ((indexAuthorityBegin == 0) ||
                    ((indexSchemeEnd > 0) &&
                    (indexAuthorityBegin == indexSchemeEnd + 1))) {
                int indexHostBegin = indexAuthorityBegin + 2;
                String newStr = str.substring(0, indexHostBegin) +
                                "localhost" +
                                str.substring(indexHostBegin);
                try {
                    return intParseURL(newStr);
                } catch (URISyntaxException inte) {
                    throw mue;
                } catch (MalformedURLException inte) {
                    throw inte;
                }
            }
            throw mue;
        }
    }

    private static ParsedNamingURL intParseURL(String str)
        throws MalformedURLException, URISyntaxException
    {
        URI uri = new URI(str);
        if (uri.isOpaque()) {
            throw new MalformedURLException(
                "not a hierarchical URL: " + str);
        }
        if (uri.getFragment() != null) {
            throw new MalformedURLException(
                "invalid character, '#', in URL name: " + str);
        } else if (uri.getQuery() != null) {
            throw new MalformedURLException(
                "invalid character, '?', in URL name: " + str);
        } else if (uri.getUserInfo() != null) {
            throw new MalformedURLException(
                "invalid character, '@', in URL host: " + str);
        }
        String scheme = uri.getScheme();
        if (scheme != null && !scheme.equals("rmi")) {
            throw new MalformedURLException("invalid URL scheme: " + str);
        }

        String name = uri.getPath();
        if (name != null) {
            if (name.startsWith("/")) {
                name = name.substring(1);
            }
            if (name.length() == 0) {
                name = null;
            }
        }

        String host = uri.getHost();
        if (host == null) {
            host = "";
            try {
                /*
                 * With 2396 URI handling, forms such as 'rmi://host:bar'
                 * or 'rmi://:<port>' are parsed into a registry based
                 * authority. We only want to allow server based naming
                 * authorities.
                 */
                uri.parseServerAuthority();
            } catch (URISyntaxException use) {
                // Check if the authority is of form ':<port>'
                String authority = uri.getAuthority();
                if (authority != null && authority.startsWith(":")) {
                    // Convert the authority to 'localhost:<port>' form
                    authority = "localhost" + authority;
                    try {
                        uri = new URI(null, authority, null, null, null);
                        // Make sure it now parses to a valid server based
                        // naming authority
                        uri.parseServerAuthority();
                    } catch (URISyntaxException use2) {
                        throw new
                            MalformedURLException("invalid authority: " + str);
                    }
                } else {
                    throw new
                        MalformedURLException("invalid authority: " + str);
                }
            }
        }
        int port = uri.getPort();
        if (port == -1) {
            port = Registry.REGISTRY_PORT;
        }
        return new ParsedNamingURL(host, port, name);
    }

    /**
     * Simple class to enable multiple URL return values.
     */
    private static class ParsedNamingURL {
        String host;
        int port;
        String name;

        ParsedNamingURL(String host, int port, String name) {
            this.host = host;
            this.port = port;
            this.name = name;
        }
    }
}
