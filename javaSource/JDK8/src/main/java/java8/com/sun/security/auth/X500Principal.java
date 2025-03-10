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

import sun.security.x509.X500Name;

import java.security.Principal;

/**
 * <p> This class represents an X.500 <code>Principal</code>.
 * X500Principals have names such as,
 * "CN=Duke, OU=JavaSoft, O=Sun Microsystems, C=US"
 * (RFC 1779 style).
 *
 * <p> Principals such as this <code>X500Principal</code>
 * may be associated with a particular <code>Subject</code>
 * to augment that <code>Subject</code> with an additional
 * identity.  Refer to the <code>Subject</code> class for more information
 * on how to achieve this.  Authorization decisions can then be based upon
 * the Principals associated with a <code>Subject</code>.
 *
 * @see Principal
 * @see javax.security.auth.Subject
 * @deprecated A new X500Principal class is available in the Java platform.
 *             This X500Principal classs is entirely deprecated and
 *             is here to allow for a smooth transition to the new
 *             class.
 * @see javax.security.auth.x500.X500Principal
*/
@jdk.Exported(false)
@Deprecated
public class X500Principal implements Principal, java.io.Serializable {

    private static final long serialVersionUID = -8222422609431628648L;

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

    transient private X500Name thisX500Name;

    /**
     * Create a X500Principal with an X.500 Name,
     * such as "CN=Duke, OU=JavaSoft, O=Sun Microsystems, C=US"
     * (RFC 1779 style).
     *
     * <p>
     *
     * @param name the X.500 name
     *
     * @exception NullPointerException if the <code>name</code>
     *                  is <code>null</code>. <p>
     *
     * @exception IllegalArgumentException if the <code>name</code>
     *                  is improperly specified.
     */
    public X500Principal(String name) {
        if (name == null)
            throw new NullPointerException(rb.getString("provided.null.name"));

        try {
            thisX500Name = new X500Name(name);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.toString());
        }

        this.name = name;
    }

    /**
     * Return the Unix username for this <code>X500Principal</code>.
     *
     * <p>
     *
     * @return the Unix username for this <code>X500Principal</code>
     */
    public String getName() {
        return thisX500Name.getName();
    }

    /**
     * Return a string representation of this <code>X500Principal</code>.
     *
     * <p>
     *
     * @return a string representation of this <code>X500Principal</code>.
     */
    public String toString() {
        return thisX500Name.toString();
    }

    /**
     * Compares the specified Object with this <code>X500Principal</code>
     * for equality.
     *
     * <p>
     *
     * @param o Object to be compared for equality with this
     *          <code>X500Principal</code>.
     *
     * @return true if the specified Object is equal equal to this
     *          <code>X500Principal</code>.
     */
    public boolean equals(Object o) {
        if (o == null)
            return false;

        if (this == o)
            return true;

        if (o instanceof com.sun.security.auth.X500Principal) {
            com.sun.security.auth.X500Principal that = (com.sun.security.auth.X500Principal)o;
            try {
                X500Name thatX500Name = new X500Name(that.getName());
                return thisX500Name.equals(thatX500Name);
            } catch (Exception e) {
                // any parsing exceptions, return false
                return false;
            }
        } else if (o instanceof Principal) {
            // this will return 'true' if 'o' is a sun.security.x509.X500Name
            // and the X500Names are equal
            return o.equals(thisX500Name);
        }

        return false;
    }

    /**
     * Return a hash code for this <code>X500Principal</code>.
     *
     * <p>
     *
     * @return a hash code for this <code>X500Principal</code>.
     */
    public int hashCode() {
        return thisX500Name.hashCode();
    }

    /**
     * Reads this object from a stream (i.e., deserializes it)
     */
    private void readObject(java.io.ObjectInputStream s) throws
                                        java.io.IOException,
                                        java.io.NotActiveException,
                                        ClassNotFoundException {

        s.defaultReadObject();

        // re-create thisX500Name
        thisX500Name = new X500Name(name);
    }
}
