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

package java8.java.security;

import java.security.Guard;
import java.security.GuardedObject;
import java.security.PermissionCollection;
import java.security.Permissions;

/**
 * Abstract class for representing access to a system resource.
 * All permissions have a name (whose interpretation depends on the subclass),
 * as well as abstract functions for defining the semantics of the
 * particular Permission subclass.
 *
 * <p>Most Permission objects also include an "actions" list that tells the actions
 * that are permitted for the object.  For example,
 * for a {@code java.io.FilePermission} object, the permission name is
 * the pathname of a file (or directory), and the actions list
 * (such as "read, write") specifies which actions are granted for the
 * specified file (or for files in the specified directory).
 * The actions list is optional for Permission objects, such as
 * {@code java.lang.RuntimePermission},
 * that don't need such a list; you either have the named permission (such
 * as "system.exit") or you don't.
 *
 * <p>An important method that must be implemented by each subclass is
 * the {@code implies} method to compare Permissions. Basically,
 * "permission p1 implies permission p2" means that
 * if one is granted permission p1, one is naturally granted permission p2.
 * Thus, this is not an equality test, but rather more of a
 * subset test.
 *
 * <P> Permission objects are similar to String objects in that they
 * are immutable once they have been created. Subclasses should not
 * provide methods that can change the state of a permission
 * once it has been created.
 *
 * @see Permissions
 * @see PermissionCollection
 *
 *
 * @author Marianne Mueller
 * @author Roland Schemers
 */

public abstract class Permission implements Guard, java.io.Serializable {

    private static final long serialVersionUID = -5636570222231596674L;

    private String name;

    /**
     * Constructs a permission with the specified name.
     *
     * @param name name of the Permission object being created.
     *
     */

    public Permission(String name) {
        this.name = name;
    }

    /**
     * Implements the guard interface for a permission. The
     * {@code SecurityManager.checkPermission} method is called,
     * passing this permission object as the permission to check.
     * Returns silently if access is granted. Otherwise, throws
     * a SecurityException.
     *
     * @param object the object being guarded (currently ignored).
     *
     * @throws SecurityException
     *        if a security manager exists and its
     *        {@code checkPermission} method doesn't allow access.
     *
     * @see Guard
     * @see GuardedObject
     * @see SecurityManager#checkPermission
     *
     */
    public void checkGuard(Object object) throws SecurityException {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) sm.checkPermission(this);
    }

    /**
     * Checks if the specified permission's actions are "implied by"
     * this object's actions.
     * <P>
     * This must be implemented by subclasses of Permission, as they are the
     * only ones that can impose semantics on a Permission object.
     *
     * <p>The {@code implies} method is used by the AccessController to determine
     * whether or not a requested permission is implied by another permission that
     * is known to be valid in the current execution context.
     *
     * @param permission the permission to check against.
     *
     * @return true if the specified permission is implied by this object,
     * false if not.
     */

    public abstract boolean implies(java.security.Permission permission);

    /**
     * Checks two Permission objects for equality.
     * <P>
     * Do not use the {@code equals} method for making access control
     * decisions; use the {@code implies} method.
     *
     * @param obj the object we are testing for equality with this object.
     *
     * @return true if both Permission objects are equivalent.
     */

    public abstract boolean equals(Object obj);

    /**
     * Returns the hash code value for this Permission object.
     * <P>
     * The required {@code hashCode} behavior for Permission Objects is
     * the following:
     * <ul>
     * <li>Whenever it is invoked on the same Permission object more than
     *     once during an execution of a Java application, the
     *     {@code hashCode} method
     *     must consistently return the same integer. This integer need not
     *     remain consistent from one execution of an application to another
     *     execution of the same application.
     * <li>If two Permission objects are equal according to the
     *     {@code equals}
     *     method, then calling the {@code hashCode} method on each of the
     *     two Permission objects must produce the same integer result.
     * </ul>
     *
     * @return a hash code value for this object.
     */

    public abstract int hashCode();

    /**
     * Returns the name of this Permission.
     * For example, in the case of a {@code java.io.FilePermission},
     * the name will be a pathname.
     *
     * @return the name of this Permission.
     *
     */

    public final String getName() {
        return name;
    }

    /**
     * Returns the actions as a String. This is abstract
     * so subclasses can defer creating a String representation until
     * one is needed. Subclasses should always return actions in what they
     * consider to be their
     * canonical form. For example, two FilePermission objects created via
     * the following:
     *
     * <pre>
     *   perm1 = new FilePermission(p1,"read,write");
     *   perm2 = new FilePermission(p2,"write,read");
     * </pre>
     *
     * both return
     * "read,write" when the {@code getActions} method is invoked.
     *
     * @return the actions of this Permission.
     *
     */

    public abstract String getActions();

    /**
     * Returns an empty PermissionCollection for a given Permission object, or null if
     * one is not defined. Subclasses of class Permission should
     * override this if they need to store their permissions in a particular
     * PermissionCollection object in order to provide the correct semantics
     * when the {@code PermissionCollection.implies} method is called.
     * If null is returned,
     * then the caller of this method is free to store permissions of this
     * type in any PermissionCollection they choose (one that uses a Hashtable,
     * one that uses a Vector, etc).
     *
     * @return a new PermissionCollection object for this type of Permission, or
     * null if one is not defined.
     */

    public PermissionCollection newPermissionCollection() {
        return null;
    }

    /**
     * Returns a string describing this Permission.  The convention is to
     * specify the class name, the permission name, and the actions in
     * the following format: '("ClassName" "name" "actions")', or
     * '("ClassName" "name")' if actions list is null or empty.
     *
     * @return information about this Permission.
     */
    public String toString() {
        String actions = getActions();
        if ((actions == null) || (actions.length() == 0)) { // OPTIONAL
            return "(\"" + getClass().getName() + "\" \"" + name + "\")";
        } else {
            return "(\"" + getClass().getName() + "\" \"" + name +
                 "\" \"" + actions + "\")";
        }
    }
}
