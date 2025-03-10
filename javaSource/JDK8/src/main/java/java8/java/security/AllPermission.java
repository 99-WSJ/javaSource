/*
 * Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java8.java.security;

import sun.security.util.SecurityConstants;

import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Enumeration;

/**
 * The AllPermission is a permission that implies all other permissions.
 * <p>
 * <b>Note:</b> Granting AllPermission should be done with extreme care,
 * as it implies all other permissions. Thus, it grants code the ability
 * to run with security
 * disabled.  Extreme caution should be taken before granting such
 * a permission to code.  This permission should be used only during testing,
 * or in extremely rare cases where an application or applet is
 * completely trusted and adding the necessary permissions to the policy
 * is prohibitively cumbersome.
 *
 * @see Permission
 * @see AccessController
 * @see Permissions
 * @see PermissionCollection
 * @see SecurityManager
 *
 *
 * @author Roland Schemers
 *
 * @serial exclude
 */

public final class AllPermission extends Permission {

    private static final long serialVersionUID = -2916474571451318075L;

    /**
     * Creates a new AllPermission object.
     */
    public AllPermission() {
        super("<all permissions>");
    }


    /**
     * Creates a new AllPermission object. This
     * constructor exists for use by the {@code Policy} object
     * to instantiate new Permission objects.
     *
     * @param name ignored
     * @param actions ignored.
     */
    public AllPermission(String name, String actions) {
        this();
    }

    /**
     * Checks if the specified permission is "implied" by
     * this object. This method always returns true.
     *
     * @param p the permission to check against.
     *
     * @return return
     */
    public boolean implies(Permission p) {
         return true;
    }

    /**
     * Checks two AllPermission objects for equality. Two AllPermission
     * objects are always equal.
     *
     * @param obj the object we are testing for equality with this object.
     * @return true if <i>obj</i> is an AllPermission, false otherwise.
     */
    public boolean equals(Object obj) {
        return (obj instanceof java.security.AllPermission);
    }

    /**
     * Returns the hash code value for this object.
     *
     * @return a hash code value for this object.
     */

    public int hashCode() {
        return 1;
    }

    /**
     * Returns the canonical string representation of the actions.
     *
     * @return the actions.
     */
    public String getActions() {
        return "<all actions>";
    }

    /**
     * Returns a new PermissionCollection object for storing AllPermission
     * objects.
     * <p>
     *
     * @return a new PermissionCollection object suitable for
     * storing AllPermissions.
     */
    public PermissionCollection newPermissionCollection() {
        return new java.security.AllPermissionCollection();
    }

}

/**
 * A AllPermissionCollection stores a collection
 * of AllPermission permissions. AllPermission objects
 * must be stored in a manner that allows them to be inserted in any
 * order, but enable the implies function to evaluate the implies
 * method in an efficient (and consistent) manner.
 *
 * @see Permission
 * @see Permissions
 *
 *
 * @author Roland Schemers
 *
 * @serial include
 */

final class AllPermissionCollection
    extends PermissionCollection
    implements java.io.Serializable
{

    // use serialVersionUID from JDK 1.2.2 for interoperability
    private static final long serialVersionUID = -4023755556366636806L;

    private boolean all_allowed; // true if any all permissions have been added

    /**
     * Create an empty AllPermissions object.
     *
     */

    public AllPermissionCollection() {
        all_allowed = false;
    }

    /**
     * Adds a permission to the AllPermissions. The key for the hash is
     * permission.path.
     *
     * @param permission the Permission object to add.
     *
     * @exception IllegalArgumentException - if the permission is not a
     *                                       AllPermission
     *
     * @exception SecurityException - if this AllPermissionCollection object
     *                                has been marked readonly
     */

    public void add(Permission permission) {
        if (! (permission instanceof java.security.AllPermission))
            throw new IllegalArgumentException("invalid permission: "+
                                               permission);
        if (isReadOnly())
            throw new SecurityException("attempt to add a Permission to a readonly PermissionCollection");

        all_allowed = true; // No sync; staleness OK
    }

    /**
     * Check and see if this set of permissions implies the permissions
     * expressed in "permission".
     *
     * @param permission the Permission object to compare
     *
     * @return always returns true.
     */

    public boolean implies(Permission permission) {
        return all_allowed; // No sync; staleness OK
    }

    /**
     * Returns an enumeration of all the AllPermission objects in the
     * container.
     *
     * @return an enumeration of all the AllPermission objects.
     */
    public Enumeration<Permission> elements() {
        return new Enumeration<Permission>() {
            private boolean hasMore = all_allowed;

            public boolean hasMoreElements() {
                return hasMore;
            }

            public Permission nextElement() {
                hasMore = false;
                return SecurityConstants.ALL_PERMISSION;
            }
        };
    }
}
