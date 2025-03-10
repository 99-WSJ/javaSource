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

package java8.java.rmi.activation;

import java.rmi.activation.ActivationGroup;
import java.rmi.activation.ActivationGroupDesc;
import java.rmi.activation.ActivationSystem;
import java.rmi.server.UID;

/**
 * The identifier for a registered activation group serves several
 * purposes: <ul>
 * <li>identifies the group uniquely within the activation system, and
 * <li>contains a reference to the group's activation system so that the
 * group can contact its activation system when necessary.</ul><p>
 *
 * The <code>ActivationGroupID</code> is returned from the call to
 * <code>ActivationSystem.registerGroup</code> and is used to identify
 * the group within the activation system. This group id is passed
 * as one of the arguments to the activation group's special constructor
 * when an activation group is created/recreated.
 *
 * @author      Ann Wollrath
 * @see         ActivationGroup
 * @see         ActivationGroupDesc
 * @since       1.2
 */
public class ActivationGroupID implements java.io.Serializable {
    /**
     * @serial The group's activation system.
     */
    private ActivationSystem system;

    /**
     * @serial The group's unique id.
     */
    private UID uid = new UID();

    /** indicate compatibility with the Java 2 SDK v1.2 version of class */
    private  static final long serialVersionUID = -1648432278909740833L;

    /**
     * Constructs a unique group id.
     *
     * @param system the group's activation system
     * @throws UnsupportedOperationException if and only if activation is
     *         not supported by this implementation
     * @since 1.2
     */
    public ActivationGroupID(ActivationSystem system) {
        this.system = system;
    }

    /**
     * Returns the group's activation system.
     * @return the group's activation system
     * @since 1.2
     */
    public ActivationSystem getSystem() {
        return system;
    }

    /**
     * Returns a hashcode for the group's identifier.  Two group
     * identifiers that refer to the same remote group will have the
     * same hash code.
     *
     * @see java.util.Hashtable
     * @since 1.2
     */
    public int hashCode() {
        return uid.hashCode();
    }

    /**
     * Compares two group identifiers for content equality.
     * Returns true if both of the following conditions are true:
     * 1) the unique identifiers are equivalent (by content), and
     * 2) the activation system specified in each
     *    refers to the same remote object.
     *
     * @param   obj     the Object to compare with
     * @return  true if these Objects are equal; false otherwise.
     * @see             java.util.Hashtable
     * @since 1.2
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof java.rmi.activation.ActivationGroupID) {
            java.rmi.activation.ActivationGroupID id = (java.rmi.activation.ActivationGroupID)obj;
            return (uid.equals(id.uid) && system.equals(id.system));
        } else {
            return false;
        }
    }
}
