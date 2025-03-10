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

import com.sun.security.auth.UnixNumericGroupPrincipal;

import java.security.Principal;

/**
 * <p> This class implements the <code>Principal</code> interface
 * and represents a user's Solaris group identification number (GID).
 *
 * <p> Principals such as this <code>SolarisNumericGroupPrincipal</code>
 * may be associated with a particular <code>Subject</code>
 * to augment that <code>Subject</code> with an additional
 * identity.  Refer to the <code>Subject</code> class for more information
 * on how to achieve this.  Authorization decisions can then be based upon
 * the Principals associated with a <code>Subject</code>.

 * @deprecated As of JDK&nbsp;1.4, replaced by
 *             {@link UnixNumericGroupPrincipal}.
 *             This class is entirely deprecated.
 *
 * @see Principal
 * @see javax.security.auth.Subject
 */
@jdk.Exported(false)
@Deprecated
public class SolarisNumericGroupPrincipal implements
                                        Principal,
                                        java.io.Serializable {

    private static final long serialVersionUID = 2345199581042573224L;

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
     * @serial
     */
    private boolean primaryGroup;

    /**
     * Create a <code>SolarisNumericGroupPrincipal</code> using a
     * <code>String</code> representation of the user's
     * group identification number (GID).
     *
     * <p>
     *
     * @param name the user's group identification number (GID)
     *                  for this user. <p>
     *
     * @param primaryGroup true if the specified GID represents the
     *                  primary group to which this user belongs.
     *
     * @exception NullPointerException if the <code>name</code>
     *                  is <code>null</code>.
     */
    public SolarisNumericGroupPrincipal(String name, boolean primaryGroup) {
        if (name == null)
            throw new NullPointerException(rb.getString("provided.null.name"));

        this.name = name;
        this.primaryGroup = primaryGroup;
    }

    /**
     * Create a <code>SolarisNumericGroupPrincipal</code> using a
     * long representation of the user's group identification number (GID).
     *
     * <p>
     *
     * @param name the user's group identification number (GID) for this user
     *                  represented as a long. <p>
     *
     * @param primaryGroup true if the specified GID represents the
     *                  primary group to which this user belongs.
     *
     */
    public SolarisNumericGroupPrincipal(long name, boolean primaryGroup) {
        this.name = (new Long(name)).toString();
        this.primaryGroup = primaryGroup;
    }

    /**
     * Return the user's group identification number (GID) for this
     * <code>SolarisNumericGroupPrincipal</code>.
     *
     * <p>
     *
     * @return the user's group identification number (GID) for this
     *          <code>SolarisNumericGroupPrincipal</code>
     */
    public String getName() {
        return name;
    }

    /**
     * Return the user's group identification number (GID) for this
     * <code>SolarisNumericGroupPrincipal</code> as a long.
     *
     * <p>
     *
     * @return the user's group identification number (GID) for this
     *          <code>SolarisNumericGroupPrincipal</code> as a long.
     */
    public long longValue() {
        return ((new Long(name)).longValue());
    }

    /**
     * Return whether this group identification number (GID) represents
     * the primary group to which this user belongs.
     *
     * <p>
     *
     * @return true if this group identification number (GID) represents
     *          the primary group to which this user belongs,
     *          or false otherwise.
     */
    public boolean isPrimaryGroup() {
        return primaryGroup;
    }

    /**
     * Return a string representation of this
     * <code>SolarisNumericGroupPrincipal</code>.
     *
     * <p>
     *
     * @return a string representation of this
     *          <code>SolarisNumericGroupPrincipal</code>.
     */
    public String toString() {
        return((primaryGroup ?
            rb.getString
            ("SolarisNumericGroupPrincipal.Primary.Group.") + name :
            rb.getString
            ("SolarisNumericGroupPrincipal.Supplementary.Group.") + name));
    }

    /**
     * Compares the specified Object with this
     * <code>SolarisNumericGroupPrincipal</code>
     * for equality.  Returns true if the given object is also a
     * <code>SolarisNumericGroupPrincipal</code> and the two
     * SolarisNumericGroupPrincipals
     * have the same group identification number (GID).
     *
     * <p>
     *
     * @param o Object to be compared for equality with this
     *          <code>SolarisNumericGroupPrincipal</code>.
     *
     * @return true if the specified Object is equal equal to this
     *          <code>SolarisNumericGroupPrincipal</code>.
     */
    public boolean equals(Object o) {
        if (o == null)
            return false;

        if (this == o)
            return true;

        if (!(o instanceof com.sun.security.auth.SolarisNumericGroupPrincipal))
            return false;
        com.sun.security.auth.SolarisNumericGroupPrincipal that = (com.sun.security.auth.SolarisNumericGroupPrincipal)o;

        if (this.getName().equals(that.getName()) &&
            this.isPrimaryGroup() == that.isPrimaryGroup())
            return true;
        return false;
    }

    /**
     * Return a hash code for this <code>SolarisNumericGroupPrincipal</code>.
     *
     * <p>
     *
     * @return a hash code for this <code>SolarisNumericGroupPrincipal</code>.
     */
    public int hashCode() {
        return toString().hashCode();
    }
}
