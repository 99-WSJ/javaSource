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

package java8.sun.security.auth;

import java.security.Principal;

/**
 * <p> This class implements the <code>Principal</code> interface
 * and represents a user's Unix group identification number (GID).
 *
 * <p> Principals such as this <code>UnixNumericGroupPrincipal</code>
 * may be associated with a particular <code>Subject</code>
 * to augment that <code>Subject</code> with an additional
 * identity.  Refer to the <code>Subject</code> class for more information
 * on how to achieve this.  Authorization decisions can then be based upon
 * the Principals associated with a <code>Subject</code>.
 *
 * @see Principal
 * @see javax.security.auth.Subject
 */
@jdk.Exported
public class UnixNumericGroupPrincipal implements
                                        Principal,
                                        java.io.Serializable {

    private static final long serialVersionUID = 3941535899328403223L;

    /**
     * @serial
     */
    private String name;

    /**
     * @serial
     */
    private boolean primaryGroup;

    /**
     * Create a <code>UnixNumericGroupPrincipal</code> using a
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
    public UnixNumericGroupPrincipal(String name, boolean primaryGroup) {
        if (name == null) {
            java.text.MessageFormat form = new java.text.MessageFormat
                (sun.security.util.ResourcesMgr.getString
                        ("invalid.null.input.value",
                        "sun.security.util.AuthResources"));
            Object[] source = {"name"};
            throw new NullPointerException(form.format(source));
        }

        this.name = name;
        this.primaryGroup = primaryGroup;
    }

    /**
     * Create a <code>UnixNumericGroupPrincipal</code> using a
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
    public UnixNumericGroupPrincipal(long name, boolean primaryGroup) {
        this.name = (new Long(name)).toString();
        this.primaryGroup = primaryGroup;
    }

    /**
     * Return the user's group identification number (GID) for this
     * <code>UnixNumericGroupPrincipal</code>.
     *
     * <p>
     *
     * @return the user's group identification number (GID) for this
     *          <code>UnixNumericGroupPrincipal</code>
     */
    public String getName() {
        return name;
    }

    /**
     * Return the user's group identification number (GID) for this
     * <code>UnixNumericGroupPrincipal</code> as a long.
     *
     * <p>
     *
     * @return the user's group identification number (GID) for this
     *          <code>UnixNumericGroupPrincipal</code> as a long.
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
     * <code>UnixNumericGroupPrincipal</code>.
     *
     * <p>
     *
     * @return a string representation of this
     *          <code>UnixNumericGroupPrincipal</code>.
     */
    public String toString() {

        if (primaryGroup) {
            java.text.MessageFormat form = new java.text.MessageFormat
                (sun.security.util.ResourcesMgr.getString
                        ("UnixNumericGroupPrincipal.Primary.Group.name",
                        "sun.security.util.AuthResources"));
            Object[] source = {name};
            return form.format(source);
        } else {
            java.text.MessageFormat form = new java.text.MessageFormat
                (sun.security.util.ResourcesMgr.getString
                    ("UnixNumericGroupPrincipal.Supplementary.Group.name",
                    "sun.security.util.AuthResources"));
            Object[] source = {name};
            return form.format(source);
        }
    }

    /**
     * Compares the specified Object with this
     * <code>UnixNumericGroupPrincipal</code>
     * for equality.  Returns true if the given object is also a
     * <code>UnixNumericGroupPrincipal</code> and the two
     * UnixNumericGroupPrincipals
     * have the same group identification number (GID).
     *
     * <p>
     *
     * @param o Object to be compared for equality with this
     *          <code>UnixNumericGroupPrincipal</code>.
     *
     * @return true if the specified Object is equal equal to this
     *          <code>UnixNumericGroupPrincipal</code>.
     */
    public boolean equals(Object o) {
        if (o == null)
            return false;

        if (this == o)
            return true;

        if (!(o instanceof com.sun.security.auth.UnixNumericGroupPrincipal))
            return false;
        com.sun.security.auth.UnixNumericGroupPrincipal that = (com.sun.security.auth.UnixNumericGroupPrincipal)o;

        if (this.getName().equals(that.getName()) &&
            this.isPrimaryGroup() == that.isPrimaryGroup())
            return true;
        return false;
    }

    /**
     * Return a hash code for this <code>UnixNumericGroupPrincipal</code>.
     *
     * <p>
     *
     * @return a hash code for this <code>UnixNumericGroupPrincipal</code>.
     */
    public int hashCode() {
        return toString().hashCode();
    }
}
