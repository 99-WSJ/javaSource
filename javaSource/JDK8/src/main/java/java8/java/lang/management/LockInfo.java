/*
 * Copyright (c) 2005, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java8.java.lang.management;

import sun.management.LockInfoCompositeData;

import javax.management.openmbean.CompositeData;
import java.util.concurrent.locks.AbstractOwnableSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Information about a <em>lock</em>.  A lock can be a built-in object monitor,
 * an <em>ownable synchronizer</em>, or the {@link Condition Condition}
 * object associated with synchronizers.
 * <p>
 * <a name="OwnableSynchronizer">An ownable synchronizer</a> is
 * a synchronizer that may be exclusively owned by a thread and uses
 * {@link AbstractOwnableSynchronizer AbstractOwnableSynchronizer}
 * (or its subclass) to implement its synchronization property.
 * {@link ReentrantLock ReentrantLock} and
 * {@link ReentrantReadWriteLock ReentrantReadWriteLock} are
 * two examples of ownable synchronizers provided by the platform.
 *
 * <h3><a name="MappedType">MXBean Mapping</a></h3>
 * <tt>LockInfo</tt> is mapped to a {@link CompositeData CompositeData}
 * as specified in the {@link #from from} method.
 *
 * @see AbstractOwnableSynchronizer
 * @see Condition
 *
 * @author  Mandy Chung
 * @since   1.6
 */

public class LockInfo {

    private String className;
    private int    identityHashCode;

    /**
     * Constructs a <tt>LockInfo</tt> object.
     *
     * @param className the fully qualified name of the class of the lock object.
     * @param identityHashCode the {@link System#identityHashCode
     *                         identity hash code} of the lock object.
     */
    public LockInfo(String className, int identityHashCode) {
        if (className == null) {
            throw new NullPointerException("Parameter className cannot be null");
        }
        this.className = className;
        this.identityHashCode = identityHashCode;
    }

    /**
     * package-private constructors
     */
    LockInfo(Object lock) {
        this.className = lock.getClass().getName();
        this.identityHashCode = System.identityHashCode(lock);
    }

    /**
     * Returns the fully qualified name of the class of the lock object.
     *
     * @return the fully qualified name of the class of the lock object.
     */
    public String getClassName() {
        return className;
    }

    /**
     * Returns the identity hash code of the lock object
     * returned from the {@link System#identityHashCode} method.
     *
     * @return the identity hash code of the lock object.
     */
    public int getIdentityHashCode() {
        return identityHashCode;
    }

    /**
     * Returns a {@code LockInfo} object represented by the
     * given {@code CompositeData}.
     * The given {@code CompositeData} must contain the following attributes:
     * <blockquote>
     * <table border summary="The attributes and the types the given CompositeData contains">
     * <tr>
     *   <th align=left>Attribute Name</th>
     *   <th align=left>Type</th>
     * </tr>
     * <tr>
     *   <td>className</td>
     *   <td><tt>java.lang.String</tt></td>
     * </tr>
     * <tr>
     *   <td>identityHashCode</td>
     *   <td><tt>java.lang.Integer</tt></td>
     * </tr>
     * </table>
     * </blockquote>
     *
     * @param cd {@code CompositeData} representing a {@code LockInfo}
     *
     * @throws IllegalArgumentException if {@code cd} does not
     *   represent a {@code LockInfo} with the attributes described
     *   above.
     * @return a {@code LockInfo} object represented
     *         by {@code cd} if {@code cd} is not {@code null};
     *         {@code null} otherwise.
     *
     * @since 1.8
     */
    public static java.lang.management.LockInfo from(CompositeData cd) {
        if (cd == null) {
            return null;
        }

        if (cd instanceof LockInfoCompositeData) {
            return ((LockInfoCompositeData) cd).getLockInfo();
        } else {
            return LockInfoCompositeData.toLockInfo(cd);
        }
    }

    /**
     * Returns a string representation of a lock.  The returned
     * string representation consists of the name of the class of the
     * lock object, the at-sign character `@', and the unsigned
     * hexadecimal representation of the <em>identity</em> hash code
     * of the object.  This method returns a string equals to the value of:
     * <blockquote>
     * <pre>
     * lock.getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(lock))
     * </pre></blockquote>
     * where <tt>lock</tt> is the lock object.
     *
     * @return the string representation of a lock.
     */
    public String toString() {
        return className + '@' + Integer.toHexString(identityHashCode);
    }
}
