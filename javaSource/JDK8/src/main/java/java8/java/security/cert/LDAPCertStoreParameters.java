/*
 * Copyright (c) 2000, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java8.java.security.cert;

import java.security.cert.CertStore;
import java.security.cert.CertStoreParameters;

/**
 * Parameters used as input for the LDAP {@code CertStore} algorithm.
 * <p>
 * This class is used to provide necessary configuration parameters (server
 * name and port number) to implementations of the LDAP {@code CertStore}
 * algorithm.
 * <p>
 * <b>Concurrent Access</b>
 * <p>
 * Unless otherwise specified, the methods defined in this class are not
 * thread-safe. Multiple threads that need to access a single
 * object concurrently should synchronize amongst themselves and
 * provide the necessary locking. Multiple threads each manipulating
 * separate objects need not synchronize.
 *
 * @since       1.4
 * @author      Steve Hanna
 * @see         CertStore
 */
public class LDAPCertStoreParameters implements CertStoreParameters {

    private static final int LDAP_DEFAULT_PORT = 389;

    /**
     * the port number of the LDAP server
     */
    private int port;

    /**
     * the DNS name of the LDAP server
     */
    private String serverName;

    /**
     * Creates an instance of {@code LDAPCertStoreParameters} with the
     * specified parameter values.
     *
     * @param serverName the DNS name of the LDAP server
     * @param port the port number of the LDAP server
     * @exception NullPointerException if {@code serverName} is
     * {@code null}
     */
    public LDAPCertStoreParameters(String serverName, int port) {
        if (serverName == null)
            throw new NullPointerException();
        this.serverName = serverName;
        this.port = port;
    }

    /**
     * Creates an instance of {@code LDAPCertStoreParameters} with the
     * specified server name and a default port of 389.
     *
     * @param serverName the DNS name of the LDAP server
     * @exception NullPointerException if {@code serverName} is
     * {@code null}
     */
    public LDAPCertStoreParameters(String serverName) {
        this(serverName, LDAP_DEFAULT_PORT);
    }

    /**
     * Creates an instance of {@code LDAPCertStoreParameters} with the
     * default parameter values (server name "localhost", port 389).
     */
    public LDAPCertStoreParameters() {
        this("localhost", LDAP_DEFAULT_PORT);
    }

    /**
     * Returns the DNS name of the LDAP server.
     *
     * @return the name (not {@code null})
     */
    public String getServerName() {
        return serverName;
    }

    /**
     * Returns the port number of the LDAP server.
     *
     * @return the port number
     */
    public int getPort() {
        return port;
    }

    /**
     * Returns a copy of this object. Changes to the copy will not affect
     * the original and vice versa.
     * <p>
     * Note: this method currently performs a shallow copy of the object
     * (simply calls {@code Object.clone()}). This may be changed in a
     * future revision to perform a deep copy if new parameters are added
     * that should not be shared.
     *
     * @return the copy
     */
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            /* Cannot happen */
            throw new InternalError(e.toString(), e);
        }
    }

    /**
     * Returns a formatted string describing the parameters.
     *
     * @return a formatted string describing the parameters
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("LDAPCertStoreParameters: [\n");

        sb.append("  serverName: " + serverName + "\n");
        sb.append("  port: " + port + "\n");
        sb.append("]");
        return sb.toString();
    }
}
