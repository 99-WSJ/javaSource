/*
 * Copyright (c) 2003, 2006, Oracle and/or its affiliates. All rights reserved.
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

package java8.com.sun.jmx.remote.security;

import com.sun.jmx.remote.security.JMXSubjectDomainCombiner;
import com.sun.jmx.remote.util.CacheMap;

import javax.management.remote.SubjectDelegationPermission;
import javax.security.auth.Subject;
import java.security.*;

public class SubjectDelegator {
    private static final int PRINCIPALS_CACHE_SIZE = 10;
    private static final int ACC_CACHE_SIZE = 10;

    private CacheMap<Subject, Principal[]> principalsCache;
    private CacheMap<Subject, AccessControlContext> accCache;

    /* Return the AccessControlContext appropriate to execute an
       operation on behalf of the delegatedSubject.  If the
       authenticatedAccessControlContext does not have permission to
       delegate to that subject, throw SecurityException.  */
    public synchronized AccessControlContext
        delegatedContext(AccessControlContext authenticatedACC,
                         Subject delegatedSubject,
                         boolean removeCallerContext)
            throws SecurityException {

        if (principalsCache == null || accCache == null) {
            principalsCache =
                    new CacheMap<Subject, Principal[]>(PRINCIPALS_CACHE_SIZE);
            accCache =
                    new CacheMap<Subject, AccessControlContext>(ACC_CACHE_SIZE);
        }

        // Retrieve the principals for the given
        // delegated subject from the cache
        //
        Principal[] delegatedPrincipals = principalsCache.get(delegatedSubject);

        // Convert the set of principals stored in the
        // delegated subject into an array of principals
        // and store it in the cache
        //
        if (delegatedPrincipals == null) {
            delegatedPrincipals =
                delegatedSubject.getPrincipals().toArray(new Principal[0]);
            principalsCache.put(delegatedSubject, delegatedPrincipals);
        }

        // Retrieve the access control context for the
        // given delegated subject from the cache
        //
        AccessControlContext delegatedACC = accCache.get(delegatedSubject);

        // Build the access control context to be used
        // when executing code as the delegated subject
        // and store it in the cache
        //
        if (delegatedACC == null) {
            if (removeCallerContext) {
                delegatedACC =
                    JMXSubjectDomainCombiner.getDomainCombinerContext(
                                                              delegatedSubject);
            } else {
                delegatedACC =
                    JMXSubjectDomainCombiner.getContext(delegatedSubject);
            }
            accCache.put(delegatedSubject, delegatedACC);
        }

        // Check if the subject delegation permission allows the
        // authenticated subject to assume the identity of each
        // principal in the delegated subject
        //
        final Principal[] dp = delegatedPrincipals;
        PrivilegedAction<Void> action =
            new PrivilegedAction<Void>() {
                public Void run() {
                    for (int i = 0 ; i < dp.length ; i++) {
                        final String pname =
                            dp[i].getClass().getName() + "." + dp[i].getName();
                        Permission sdp =
                            new SubjectDelegationPermission(pname);
                        AccessController.checkPermission(sdp);
                    }
                    return null;
                }
            };
        AccessController.doPrivileged(action, authenticatedACC);

        return delegatedACC;
    }

    /**
     * Check if the connector server creator can assume the identity of each
     * principal in the authenticated subject, i.e. check if the connector
     * server creator codebase contains a subject delegation permission for
     * each principal present in the authenticated subject.
     *
     * @return {@code true} if the connector server creator can delegate to all
     * the authenticated principals in the subject. Otherwise, {@code false}.
     */
    public static synchronized boolean
        checkRemoveCallerContext(Subject subject) {
        try {
            final Principal[] dp =
                subject.getPrincipals().toArray(new Principal[0]);
            for (int i = 0 ; i < dp.length ; i++) {
                final String pname =
                    dp[i].getClass().getName() + "." + dp[i].getName();
                final Permission sdp =
                    new SubjectDelegationPermission(pname);
                AccessController.checkPermission(sdp);
            }
        } catch (SecurityException e) {
            return false;
        }
        return true;
    }
}
