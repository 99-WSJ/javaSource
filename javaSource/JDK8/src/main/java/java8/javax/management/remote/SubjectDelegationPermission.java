/*
 * Copyright (c) 2003, 2007, Oracle and/or its affiliates. All rights reserved.
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

package java8.javax.management.remote;

import java.security.BasicPermission;

/**
 * <p>Permission required by an authentication identity to perform
 * operations on behalf of an authorization identity.</p>
 *
 * <p>A SubjectDelegationPermission contains a name (also referred
 * to as a "target name") but no actions list; you either have the
 * named permission or you don't.</p>
 *
 * <p>The target name is the name of the authorization principal
 * classname followed by a period and the authorization principal
 * name, that is
 * <code>"<em>PrincipalClassName</em>.<em>PrincipalName</em>"</code>.</p>
 *
 * <p>An asterisk may appear by itself, or if immediately preceded
 * by a "." may appear at the end of the target name, to signify a
 * wildcard match.</p>
 *
 * <p>For example, "*", "javax.management.remote.JMXPrincipal.*" and
 * "javax.management.remote.JMXPrincipal.delegate" are valid target
 * names. The first one denotes any principal name from any principal
 * class, the second one denotes any principal name of the concrete
 * principal class <code>javax.management.remote.JMXPrincipal</code>
 * and the third one denotes a concrete principal name
 * <code>delegate</code> of the concrete principal class
 * <code>javax.management.remote.JMXPrincipal</code>.</p>
 *
 * @since 1.5
 */
public final class SubjectDelegationPermission extends BasicPermission {

    private static final long serialVersionUID = 1481618113008682343L;

    /**
     * Creates a new SubjectDelegationPermission with the specified name.
     * The name is the symbolic name of the SubjectDelegationPermission.
     *
     * @param name the name of the SubjectDelegationPermission
     *
     * @throws NullPointerException if <code>name</code> is
     * <code>null</code>.
     * @throws IllegalArgumentException if <code>name</code> is empty.
     */
    public SubjectDelegationPermission(String name) {
        super(name);
    }

    /**
     * Creates a new SubjectDelegationPermission object with the
     * specified name.  The name is the symbolic name of the
     * SubjectDelegationPermission, and the actions String is
     * currently unused and must be null.
     *
     * @param name the name of the SubjectDelegationPermission
     * @param actions must be null.
     *
     * @throws NullPointerException if <code>name</code> is
     * <code>null</code>.
     * @throws IllegalArgumentException if <code>name</code> is empty
     * or <code>actions</code> is not null.
     */
    public SubjectDelegationPermission(String name, String actions) {
        super(name, actions);

        if (actions != null)
            throw new IllegalArgumentException("Non-null actions");
    }
}
