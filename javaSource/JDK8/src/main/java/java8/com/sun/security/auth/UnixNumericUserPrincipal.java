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
 * and represents a user's Unix identification number (UID).
 *
 * <p> Principals such as this <code>UnixNumericUserPrincipal</code>
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
public class UnixNumericUserPrincipal implements
                                        Principal,
                                        java.io.Serializable {
    private static final long serialVersionUID = -4329764253802397821L;

    /**
     * @serial
     */
    private String name;

    /**
     * Create a <code>UnixNumericUserPrincipal</code> using a
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
    public UnixNumericUserPrincipal(String name) {
        if (name == null) {
            java.text.MessageFormat form = new java.text.MessageFormat
                (sun.security.util.ResourcesMgr.getString
                        ("invalid.null.input.value",
                        "sun.security.util.AuthResources"));
            Object[] source = {"name"};
            throw new NullPointerException(form.format(source));
        }

        this.name = name;
    }

    /**
     * Create a <code>UnixNumericUserPrincipal</code> using a
     * long representation of the user's identification number (UID).
     *
     * <p>
     *
     * @param name the user identification number (UID) for this user
     *                  represented as a long.
     */
    public UnixNumericUserPrincipal(long name) {
        this.name = (new Long(name)).toString();
    }

    /**
     * Return the user identification number (UID) for this
     * <code>UnixNumericUserPrincipal</code>.
     *
     * <p>
     *
     * @return the user identification number (UID) for this
     *          <code>UnixNumericUserPrincipal</code>
     */
    public String getName() {
        return name;
    }

    /**
     * Return the user identification number (UID) for this
     * <code>UnixNumericUserPrincipal</code> as a long.
     *
     * <p>
     *
     * @return the user identification number (UID) for this
     *          <code>UnixNumericUserPrincipal</code> as a long.
     */
    public long longValue() {
        return ((new Long(name)).longValue());
    }

    /**
     * Return a string representation of this
     * <code>UnixNumericUserPrincipal</code>.
     *
     * <p>
     *
     * @return a string representation of this
     *          <code>UnixNumericUserPrincipal</code>.
     */
    public String toString() {
        java.text.MessageFormat form = new java.text.MessageFormat
                (sun.security.util.ResourcesMgr.getString
                        ("UnixNumericUserPrincipal.name",
                        "sun.security.util.AuthResources"));
        Object[] source = {name};
        return form.format(source);
    }

    /**
     * Compares the specified Object with this
     * <code>UnixNumericUserPrincipal</code>
     * for equality.  Returns true if the given object is also a
     * <code>UnixNumericUserPrincipal</code> and the two
     * UnixNumericUserPrincipals
     * have the same user identification number (UID).
     *
     * <p>
     *
     * @param o Object to be compared for equality with this
     *          <code>UnixNumericUserPrincipal</code>.
     *
     * @return true if the specified Object is equal equal to this
     *          <code>UnixNumericUserPrincipal</code>.
     */
    public boolean equals(Object o) {
        if (o == null)
            return false;

        if (this == o)
            return true;

        if (!(o instanceof com.sun.security.auth.UnixNumericUserPrincipal))
            return false;
        com.sun.security.auth.UnixNumericUserPrincipal that = (com.sun.security.auth.UnixNumericUserPrincipal)o;

        if (this.getName().equals(that.getName()))
            return true;
        return false;
    }

    /**
     * Return a hash code for this <code>UnixNumericUserPrincipal</code>.
     *
     * <p>
     *
     * @return a hash code for this <code>UnixNumericUserPrincipal</code>.
     */
    public int hashCode() {
        return name.hashCode();
    }
}
