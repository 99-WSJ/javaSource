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

package java8.java.security;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.ProtectionDomain;

/**
 * A {@code DomainCombiner} provides a means to dynamically
 * update the ProtectionDomains associated with the current
 * {@code AccessControlContext}.
 *
 * <p> A {@code DomainCombiner} is passed as a parameter to the
 * appropriate constructor for {@code AccessControlContext}.
 * The newly constructed context is then passed to the
 * {@code AccessController.doPrivileged(..., context)} method
 * to bind the provided context (and associated {@code DomainCombiner})
 * with the current execution Thread.  Subsequent calls to
 * {@code AccessController.getContext} or
 * {@code AccessController.checkPermission}
 * cause the {@code DomainCombiner.combine} to get invoked.
 *
 * <p> The combine method takes two arguments.  The first argument represents
 * an array of ProtectionDomains from the current execution Thread,
 * since the most recent call to {@code AccessController.doPrivileged}.
 * If no call to doPrivileged was made, then the first argument will contain
 * all the ProtectionDomains from the current execution Thread.
 * The second argument represents an array of inherited ProtectionDomains,
 * which may be {@code null}.  ProtectionDomains may be inherited
 * from a parent Thread, or from a privileged context.  If no call to
 * doPrivileged was made, then the second argument will contain the
 * ProtectionDomains inherited from the parent Thread.  If one or more calls
 * to doPrivileged were made, and the most recent call was to
 * doPrivileged(action, context), then the second argument will contain the
 * ProtectionDomains from the privileged context.  If the most recent call
 * was to doPrivileged(action), then there is no privileged context,
 * and the second argument will be {@code null}.
 *
 * <p> The {@code combine} method investigates the two input arrays
 * of ProtectionDomains and returns a single array containing the updated
 * ProtectionDomains.  In the simplest case, the {@code combine}
 * method merges the two stacks into one.  In more complex cases,
 * the {@code combine} method returns a modified
 * stack of ProtectionDomains.  The modification may have added new
 * ProtectionDomains, removed certain ProtectionDomains, or simply
 * updated existing ProtectionDomains.  Re-ordering and other optimizations
 * to the ProtectionDomains are also permitted.  Typically the
 * {@code combine} method bases its updates on the information
 * encapsulated in the {@code DomainCombiner}.
 *
 * <p> After the {@code AccessController.getContext} method
 * receives the combined stack of ProtectionDomains back from
 * the {@code DomainCombiner}, it returns a new
 * AccessControlContext that has both the combined ProtectionDomains
 * as well as the {@code DomainCombiner}.
 *
 * @see AccessController
 * @see AccessControlContext
 * @since 1.3
 */
public interface DomainCombiner {

    /**
     * Modify or update the provided ProtectionDomains.
     * ProtectionDomains may be added to or removed from the given
     * ProtectionDomains.  The ProtectionDomains may be re-ordered.
     * Individual ProtectionDomains may be modified (with a new
     * set of Permissions, for example).
     *
     * <p>
     *
     * @param currentDomains the ProtectionDomains associated with the
     *          current execution Thread, up to the most recent
     *          privileged {@code ProtectionDomain}.
     *          The ProtectionDomains are are listed in order of execution,
     *          with the most recently executing {@code ProtectionDomain}
     *          residing at the beginning of the array. This parameter may
     *          be {@code null} if the current execution Thread
     *          has no associated ProtectionDomains.<p>
     *
     * @param assignedDomains an array of inherited ProtectionDomains.
     *          ProtectionDomains may be inherited from a parent Thread,
     *          or from a privileged {@code AccessControlContext}.
     *          This parameter may be {@code null}
     *          if there are no inherited ProtectionDomains.
     *
     * @return a new array consisting of the updated ProtectionDomains,
     *          or {@code null}.
     */
    ProtectionDomain[] combine(ProtectionDomain[] currentDomains,
                                ProtectionDomain[] assignedDomains);
}
