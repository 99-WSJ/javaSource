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

package java8.sun.security.auth;

import com.sun.security.auth.UnixNumericUserPrincipal;

import java.security.Principal;

/**
 * <p> This class implements the <code>Principal</code> interface
 * and represents a user's Solaris identification number (UID).
 *
 * <p> Principals such as this <code>SolarisNumericUserPrincipal</code>
 * may be associated with a particular <code>Subject</code>
 * to augment that <code>Subject</code> with an additional
 * identity.  Refer to the <code>Subject</code> class for more information
 * on how to achieve this.  Authorization decisions can then be based upon
 * the Principals associated with a <code>Subject</code>.
 * @deprecated As of JDK&nbsp;1.4, replaced by
 *             {@link UnixNumericUserPrincipal}.
 *             This class is entirely deprecated.
 *
 * @see Principal
 * @see javax.security.auth.Subject
 */
@jdk.Exported(false)
@Deprecated
public class SolarisNumericUserPrincipal implements
                                        Principal,
                                        java.io.Serializable {

    private static final long serialVersionUID = -3178578484679887104L;

    private static final java.util.ResourceBundle rb =
          java.security.AccessController.doPrivileged
          (new java.security.PrivilegedAction<java.util.ResourceBundle>() {
              public java.util.ResourceBundle run() {
                  return (java.util.ResourceBundle.getBundle
                                ("sun.security.util.AuthResources"));
              }
           });


    /**
     * @serial
     */
    private String name;

    /**
     * Create a <code>SolarisNumericUserPrincipal</code> using a
     * <code>String</code> representation of the
     * user's identification number (UID).
     *
     * <p>
     *
     * @param name the user identification number (UID) for this user.
     *
     * @exception NullPointerException if the <code>name</code>
     *                  is <code>null</code>.
     */
    public SolarisNumericUserPrincipal(String name) {
        if (name == null)
            throw new NullPointerException(rb.getString("provided.null.name"));

        this.name = name;
    }

    /**
     * Create a <code>SolarisNumericUserPrincipal</code> using a
     * long representation of the user's identification number (UID).
     *
     * <p>
     *
     * @param name the user identification number (UID) for this user
     *                  represented as a long.
     */
    public SolarisNumericUserPrincipal(long name) {
        this.name = (new Long(name)).toString();
    }

    /**
     * Return the user identification number (UID) for this
     * <code>SolarisNumericUserPrincipal</code>.
     *
     * <p>
     *
     * @return the user identification number (UID) for this
     *          <code>SolarisNumericUserPrincipal</code>
     */
    public String getName() {
        return name;
    }

    /**
     * Return the user identification number (UID) for this
     * <code>SolarisNumericUserPrincipal</code> as a long.
     *
     * <p>
     *
     * @return the user identification number (UID) for this
     *          <code>SolarisNumericUserPrincipal</code> as a long.
     */
    public long longValue() {
        return ((new Long(name)).longValue());
    }

    /**
     * Return a string representation of this
     * <code>SolarisNumericUserPrincipal</code>.
     *
     * <p>
     *
     * @return a string representation of this
     *          <code>SolarisNumericUserPrincipal</code>.
     */
    public String toString() {
        return(rb.getString("SolarisNumericUserPrincipal.") + name);
    }

    /**
     * Compares the specified Object with this
     * <code>SolarisNumericUserPrincipal</code>
     * for equality.  Returns true if the given object is also a
     * <code>SolarisNumericUserPrincipal</code> and the two
     * SolarisNumericUserPrincipals
     * have the same user identification number (UID).
     *
     * <p>
     *
     * @param o Object to be compared for equality with this
     *          <code>SolarisNumericUserPrincipal</code>.
     *
     * @return true if the specified Object is equal equal to this
     *          <code>SolarisNumericUserPrincipal</code>.
     */
    public boolean equals(Object o) {
        if (o == null)
            return false;

        if (this == o)
            return true;

        if (!(o instanceof com.sun.security.auth.SolarisNumericUserPrincipal))
            return false;
        com.sun.security.auth.SolarisNumericUserPrincipal that = (com.sun.security.auth.SolarisNumericUserPrincipal)o;

        if (this.getName().equals(that.getName()))
            return true;
        return false;
    }

    /**
     * Return a hash code for this <code>SolarisNumericUserPrincipal</code>.
     *
     * <p>
     *
     * @return a hash code for this <code>SolarisNumericUserPrincipal</code>.
     */
    public int hashCode() {
        return name.hashCode();
    }
}
