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

package java8.java.io;

import java.security.BasicPermission;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;

/**
 * This class is for Serializable permissions. A SerializablePermission
 * contains a name (also referred to as a "target name") but
 * no actions list; you either have the named permission
 * or you don't.
 *
 * <P>
 * The target name is the name of the Serializable permission (see below).
 *
 * <P>
 * The following table lists all the possible SerializablePermission target names,
 * and for each provides a description of what the permission allows
 * and a discussion of the risks of granting code the permission.
 *
 * <table border=1 cellpadding=5 summary="Permission target name, what the permission allows, and associated risks">
 * <tr>
 * <th>Permission Target Name</th>
 * <th>What the Permission Allows</th>
 * <th>Risks of Allowing this Permission</th>
 * </tr>
 *
 * <tr>
 *   <td>enableSubclassImplementation</td>
 *   <td>Subclass implementation of ObjectOutputStream or ObjectInputStream
 * to override the default serialization or deserialization, respectively,
 * of objects</td>
 *   <td>Code can use this to serialize or
 * deserialize classes in a purposefully malfeasant manner. For example,
 * during serialization, malicious code can use this to
 * purposefully store confidential private field data in a way easily accessible
 * to attackers. Or, during deserialization it could, for example, deserialize
 * a class with all its private fields zeroed out.</td>
 * </tr>
 *
 * <tr>
 *   <td>enableSubstitution</td>
 *   <td>Substitution of one object for another during
 * serialization or deserialization</td>
 *   <td>This is dangerous because malicious code
 * can replace the actual object with one which has incorrect or
 * malignant data.</td>
 * </tr>
 *
 * </table>
 *
 * @see BasicPermission
 * @see Permission
 * @see Permissions
 * @see PermissionCollection
 * @see SecurityManager
 *
 *
 * @author Joe Fialli
 * @since 1.2
 */

/* code was borrowed originally from java.lang.RuntimePermission. */

public final class SerializablePermission extends BasicPermission {

    private static final long serialVersionUID = 8537212141160296410L;

    /**
     * @serial
     */
    private String actions;

    /**
     * Creates a new SerializablePermission with the specified name.
     * The name is the symbolic name of the SerializablePermission, such as
     * "enableSubstitution", etc.
     *
     * @param name the name of the SerializablePermission.
     *
     * @throws NullPointerException if <code>name</code> is <code>null</code>.
     * @throws IllegalArgumentException if <code>name</code> is empty.
     */
    public SerializablePermission(String name)
    {
        super(name);
    }

    /**
     * Creates a new SerializablePermission object with the specified name.
     * The name is the symbolic name of the SerializablePermission, and the
     * actions String is currently unused and should be null.
     *
     * @param name the name of the SerializablePermission.
     * @param actions currently unused and must be set to null
     *
     * @throws NullPointerException if <code>name</code> is <code>null</code>.
     * @throws IllegalArgumentException if <code>name</code> is empty.
     */

    public SerializablePermission(String name, String actions)
    {
        super(name, actions);
    }
}
