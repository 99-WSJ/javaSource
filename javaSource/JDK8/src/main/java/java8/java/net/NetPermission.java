/*
 * Copyright (c) 1997, 2013, Oracle and/or its affiliates. All rights reserved.
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

import java.security.BasicPermission;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;

/**
 * This class is for various network permissions.
 * A NetPermission contains a name (also referred to as a "target name") but
 * no actions list; you either have the named permission
 * or you don't.
 * <P>
 * The target name is the name of the network permission (see below). The naming
 * convention follows the  hierarchical property naming convention.
 * Also, an asterisk
 * may appear at the end of the name, following a ".", or by itself, to
 * signify a wildcard match. For example: "foo.*" and "*" signify a wildcard
 * match, while "*foo" and "a*b" do not.
 * <P>
 * The following table lists all the possible NetPermission target names,
 * and for each provides a description of what the permission allows
 * and a discussion of the risks of granting code the permission.
 *
 * <table border=1 cellpadding=5 summary="Permission target name, what the permission allows, and associated risks">
 * <tr>
 * <th>Permission Target Name</th>
 * <th>What the Permission Allows</th>
 * <th>Risks of Allowing this Permission</th>
 * </tr>
 * <tr>
 *   <td>allowHttpTrace</td>
 *   <td>The ability to use the HTTP TRACE method in HttpURLConnection.</td>
 *   <td>Malicious code using HTTP TRACE could get access to security sensitive
 *   information in the HTTP headers (such as cookies) that it might not
 *   otherwise have access to.</td>
 *   </tr>
 *
 * <tr>
 *   <td>getCookieHandler</td>
 *   <td>The ability to get the cookie handler that processes highly
 *   security sensitive cookie information for an Http session.</td>
 *   <td>Malicious code can get a cookie handler to obtain access to
 *   highly security sensitive cookie information. Some web servers
 *   use cookies to save user private information such as access
 *   control information, or to track user browsing habit.</td>
 *   </tr>
 *
 * <tr>
 *  <td>getNetworkInformation</td>
 *  <td>The ability to retrieve all information about local network interfaces.</td>
 *  <td>Malicious code can read information about network hardware such as
 *  MAC addresses, which could be used to construct local IPv6 addresses.</td>
 * </tr>
 *
 * <tr>
 *   <td>getProxySelector</td>
 *   <td>The ability to get the proxy selector used to make decisions
 *   on which proxies to use when making network connections.</td>
 *   <td>Malicious code can get a ProxySelector to discover proxy
 *   hosts and ports on internal networks, which could then become
 *   targets for attack.</td>
 * </tr>
 *
 * <tr>
 *   <td>getResponseCache</td>
 *   <td>The ability to get the response cache that provides
 *   access to a local response cache.</td>
 *   <td>Malicious code getting access to the local response cache
 *   could access security sensitive information.</td>
 *   </tr>
 *
 * <tr>
 *   <td>requestPasswordAuthentication</td>
 *   <td>The ability
 * to ask the authenticator registered with the system for
 * a password</td>
 *   <td>Malicious code may steal this password.</td>
 * </tr>
 *
 * <tr>
 *   <td>setCookieHandler</td>
 *   <td>The ability to set the cookie handler that processes highly
 *   security sensitive cookie information for an Http session.</td>
 *   <td>Malicious code can set a cookie handler to obtain access to
 *   highly security sensitive cookie information. Some web servers
 *   use cookies to save user private information such as access
 *   control information, or to track user browsing habit.</td>
 *   </tr>
 *
 * <tr>
 *   <td>setDefaultAuthenticator</td>
 *   <td>The ability to set the
 * way authentication information is retrieved when
 * a proxy or HTTP server asks for authentication</td>
 *   <td>Malicious
 * code can set an authenticator that monitors and steals user
 * authentication input as it retrieves the input from the user.</td>
 * </tr>
 *
 * <tr>
 *   <td>setProxySelector</td>
 *   <td>The ability to set the proxy selector used to make decisions
 *   on which proxies to use when making network connections.</td>
 *   <td>Malicious code can set a ProxySelector that directs network
 *   traffic to an arbitrary network host.</td>
 * </tr>
 *
 * <tr>
 *   <td>setResponseCache</td>
 *   <td>The ability to set the response cache that provides access to
 *   a local response cache.</td>
 *   <td>Malicious code getting access to the local response cache
 *   could access security sensitive information, or create false
 *   entries in the response cache.</td>
 *   </tr>
 *
 * <tr>
 *   <td>specifyStreamHandler</td>
 *   <td>The ability
 * to specify a stream handler when constructing a URL</td>
 *   <td>Malicious code may create a URL with resources that it would
normally not have access to (like file:/foo/fum/), specifying a
stream handler that gets the actual bytes from someplace it does
have access to. Thus it might be able to trick the system into
creating a ProtectionDomain/CodeSource for a class even though
that class really didn't come from that location.</td>
 * </tr>
 * </table>
 *
 * @see BasicPermission
 * @see Permission
 * @see Permissions
 * @see PermissionCollection
 * @see SecurityManager
 *
 *
 * @author Marianne Mueller
 * @author Roland Schemers
 */

public final class NetPermission extends BasicPermission {
    private static final long serialVersionUID = -8343910153355041693L;

    /**
     * Creates a new NetPermission with the specified name.
     * The name is the symbolic name of the NetPermission, such as
     * "setDefaultAuthenticator", etc. An asterisk
     * may appear at the end of the name, following a ".", or by itself, to
     * signify a wildcard match.
     *
     * @param name the name of the NetPermission.
     *
     * @throws NullPointerException if {@code name} is {@code null}.
     * @throws IllegalArgumentException if {@code name} is empty.
     */

    public NetPermission(String name)
    {
        super(name);
    }

    /**
     * Creates a new NetPermission object with the specified name.
     * The name is the symbolic name of the NetPermission, and the
     * actions String is currently unused and should be null.
     *
     * @param name the name of the NetPermission.
     * @param actions should be null.
     *
     * @throws NullPointerException if {@code name} is {@code null}.
     * @throws IllegalArgumentException if {@code name} is empty.
     */

    public NetPermission(String name, String actions)
    {
        super(name, actions);
    }
}
