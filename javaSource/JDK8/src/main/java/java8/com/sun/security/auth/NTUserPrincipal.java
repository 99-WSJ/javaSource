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

package java8.com.sun.security.auth;

import java.security.Principal;

/**
 * <p> This class implements the <code>Principal</code> interface
 * and represents a Windows NT user.
 *
 * <p> Principals such as this <code>NTUserPrincipal</code>
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
public class NTUserPrincipal implements Principal, java.io.Serializable {

    private static final long serialVersionUID = -8737649811939033735L;

    /**
     * @serial
     */
    private String name;

    /**
     * Create an <code>NTUserPrincipal</code> with a Windows NT username.
     *
     * <p>
     *
     * @param name the Windows NT username for this user. <p>
     *
     * @exception NullPointerException if the <code>name</code>
     *            is <code>null</code>.
     */
    public NTUserPrincipal(String name) {
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
     * Return the Windows NT username for this <code>NTPrincipal</code>.
     *
     * <p>
     *
     * @return the Windows NT username for this <code>NTPrincipal</code>
     */
    public String getName() {
        return name;
    }

    /**
     * Return a string representation of this <code>NTPrincipal</code>.
     *
     * <p>
     *
     * @return a string representation of this <code>NTPrincipal</code>.
     */
    public String toString() {
        java.text.MessageFormat form = new java.text.MessageFormat
                (sun.security.util.ResourcesMgr.getString
                        ("NTUserPrincipal.name",
                        "sun.security.util.AuthResources"));
        Object[] source = {name};
        return form.format(source);
    }

    /**
     * Compares the specified Object with this <code>NTUserPrincipal</code>
     * for equality.  Returns true if the given object is also a
     * <code>NTUserPrincipal</code> and the two NTUserPrincipals
     * have the same name.
     *
     * <p>
     *
     * @param o Object to be compared for equality with this
     *          <code>NTPrincipal</code>.
     *
     * @return true if the specified Object is equal equal to this
     *          <code>NTPrincipal</code>.
     */
    public boolean equals(Object o) {
            if (o == null)
                return false;

        if (this == o)
            return true;

        if (!(o instanceof com.sun.security.auth.NTUserPrincipal))
            return false;
        com.sun.security.auth.NTUserPrincipal that = (com.sun.security.auth.NTUserPrincipal)o;

            if (name.equals(that.getName()))
                return true;
            return false;
    }

    /**
     * Return a hash code for this <code>NTUserPrincipal</code>.
     *
     * <p>
     *
     * @return a hash code for this <code>NTUserPrincipal</code>.
     */
    public int hashCode() {
            return this.getName().hashCode();
    }
}
