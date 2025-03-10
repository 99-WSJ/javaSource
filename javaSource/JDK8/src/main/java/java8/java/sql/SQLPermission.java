/*
 * Copyright (c) 1999, 2013, Oracle and/or its affiliates. All rights reserved.
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


package java8.java.sql;

import java.security.BasicPermission;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;

/**
 * The permission for which the <code>SecurityManager</code> will check
 * when code that is running an application with a
 * <code>SecurityManager</code> enabled, calls the
 * {@code DriverManager.deregisterDriver} method,
 * <code>DriverManager.setLogWriter</code> method,
 * <code>DriverManager.setLogStream</code> (deprecated) method,
 * {@code SyncFactory.setJNDIContext} method,
 * {@code SyncFactory.setLogger} method,
 * {@code Connection.setNetworktimeout} method,
 * or the <code>Connection.abort</code> method.
 * If there is no <code>SQLPermission</code> object, these methods
 * throw a <code>java.lang.SecurityException</code> as a runtime exception.
 * <P>
 * A <code>SQLPermission</code> object contains
 * a name (also referred to as a "target name") but no actions
 * list; there is either a named permission or there is not.
 * The target name is the name of the permission (see below). The
 * naming convention follows the  hierarchical property naming convention.
 * In addition, an asterisk
 * may appear at the end of the name, following a ".", or by itself, to
 * signify a wildcard match. For example: <code>loadLibrary.*</code>
 * and <code>*</code> signify a wildcard match,
 * while <code>*loadLibrary</code> and <code>a*b</code> do not.
 * <P>
 * The following table lists all the possible <code>SQLPermission</code> target names.
 * The table gives a description of what the permission allows
 * and a discussion of the risks of granting code the permission.
 *
 *
 * <table border=1 cellpadding=5 summary="permission target name, what the permission allows, and associated risks">
 * <tr>
 * <th>Permission Target Name</th>
 * <th>What the Permission Allows</th>
 * <th>Risks of Allowing this Permission</th>
 * </tr>
 *
 * <tr>
 *   <td>setLog</td>
 *   <td>Setting of the logging stream</td>
 *   <td>This is a dangerous permission to grant.
 * The contents of the log may contain usernames and passwords,
 * SQL statements, and SQL data.</td>
 * </tr>
 * <tr>
 * <td>callAbort</td>
 *   <td>Allows the invocation of the {@code Connection} method
 *   {@code abort}</td>
 *   <td>Permits an application to terminate a physical connection to a
 *  database.</td>
 * </tr>
 * <tr>
 * <td>setSyncFactory</td>
 *   <td>Allows the invocation of the {@code SyncFactory} methods
 *   {@code setJNDIContext} and {@code setLogger}</td>
 *   <td>Permits an application to specify the JNDI context from which the
 *   {@code SyncProvider} implementations can be retrieved from and the logging
 *   object to be used by the {@code SyncProvider} implementation.</td>
 * </tr>
 *
 * <tr>
 * <td>setNetworkTimeout</td>
 *   <td>Allows the invocation of the {@code Connection} method
 *   {@code setNetworkTimeout}</td>
 *   <td>Permits an application to specify the maximum period a
 * <code>Connection</code> or
 * objects created from the <code>Connection</code>
 * will wait for the database to reply to any one request.</td>
 * <tr>
 * <td>deregisterDriver</td>
 *   <td>Allows the invocation of the {@code DriverManager}
 * method {@code deregisterDriver}</td>
 *   <td>Permits an application to remove a JDBC driver from the list of
 * registered Drivers and release its resources.</td>
 * </tr>
 * </table>
 *<p>
 * @since 1.3
 * @see BasicPermission
 * @see Permission
 * @see Permissions
 * @see PermissionCollection
 * @see SecurityManager
 *
 */

public final class SQLPermission extends BasicPermission {

    /**
     * Creates a new <code>SQLPermission</code> object with the specified name.
     * The name is the symbolic name of the <code>SQLPermission</code>.
     *
     * @param name the name of this <code>SQLPermission</code> object, which must
     * be either {@code  setLog}, {@code callAbort}, {@code setSyncFactory},
     *  {@code deregisterDriver}, or {@code setNetworkTimeout}
     * @throws NullPointerException if <code>name</code> is <code>null</code>.
     * @throws IllegalArgumentException if <code>name</code> is empty.

     */

    public SQLPermission(String name) {
        super(name);
    }

    /**
     * Creates a new <code>SQLPermission</code> object with the specified name.
     * The name is the symbolic name of the <code>SQLPermission</code>; the
     * actions <code>String</code> is currently unused and should be
     * <code>null</code>.
     *
     * @param name the name of this <code>SQLPermission</code> object, which must
     * be either {@code  setLog}, {@code callAbort}, {@code setSyncFactory},
     *  {@code deregisterDriver}, or {@code setNetworkTimeout}
     * @param actions should be <code>null</code>
     * @throws NullPointerException if <code>name</code> is <code>null</code>.
     * @throws IllegalArgumentException if <code>name</code> is empty.

     */

    public SQLPermission(String name, String actions) {
        super(name, actions);
    }

    /**
     * Private serial version unique ID to ensure serialization
     * compatibility.
     */
    static final long serialVersionUID = -1439323187199563495L;

}
