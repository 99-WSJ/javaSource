/*
 * Copyright (c) 1999, 2004, Oracle and/or its affiliates. All rights reserved.
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

package java8.javax.naming.ldap;

import javax.naming.ReferralException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.ldap.Control;
import java.util.Hashtable;

/**
 * This abstract class is used to represent an LDAP referral exception.
 * It extends the base <tt>ReferralException</tt> by providing a
 * <tt>getReferralContext()</tt> method that accepts request controls.
 * LdapReferralException is an abstract class. Concrete implementations of it
 * determine its synchronization and serialization properties.
 *<p>
 * A <tt>Control[]</tt> array passed as a parameter to
 * the <tt>getReferralContext()</tt> method is owned by the caller.
 * The service provider will not modify the array or keep a reference to it,
 * although it may keep references to the individual <tt>Control</tt> objects
 * in the array.
 *
 * @author Rosanna Lee
 * @author Scott Seligman
 * @author Vincent Ryan
 * @since 1.3
 */

public abstract class LdapReferralException extends ReferralException {
    /**
     * Constructs a new instance of LdapReferralException using the
     * explanation supplied. All other fields are set to null.
     *
     * @param   explanation     Additional detail about this exception. Can be null.
     * @see Throwable#getMessage
     */
    protected LdapReferralException(String explanation) {
        super(explanation);
    }

    /**
      * Constructs a new instance of LdapReferralException.
      * All fields are set to null.
      */
    protected LdapReferralException() {
        super();
    }

    /**
     * Retrieves the context at which to continue the method using the
     * context's environment and no controls.
     * The referral context is created using the environment properties of
     * the context that threw the <tt>ReferralException</tt> and no controls.
     *<p>
     * This method is equivalent to
     *<blockquote><pre>
     * getReferralContext(ctx.getEnvironment(), null);
     *</pre></blockquote>
     * where <tt>ctx</tt> is the context that threw the <tt>ReferralException.</tt>
     *<p>
     * It is overridden in this class for documentation purposes only.
     * See <tt>ReferralException</tt> for how to use this method.
     *
     * @return The non-null context at which to continue the method.
     * @exception NamingException If a naming exception was encountered.
     * Call either <tt>retryReferral()</tt> or <tt>skipReferral()</tt>
     * to continue processing referrals.
     */
    public abstract Context getReferralContext() throws NamingException;

    /**
     * Retrieves the context at which to continue the method using
     * environment properties and no controls.
     * The referral context is created using <tt>env</tt> as its environment
     * properties and no controls.
     *<p>
     * This method is equivalent to
     *<blockquote><pre>
     * getReferralContext(env, null);
     *</pre></blockquote>
     *<p>
     * It is overridden in this class for documentation purposes only.
     * See <tt>ReferralException</tt> for how to use this method.
     *
     * @param env The possibly null environment to use when retrieving the
     *          referral context. If null, no environment properties will be used.
     *
     * @return The non-null context at which to continue the method.
     * @exception NamingException If a naming exception was encountered.
     * Call either <tt>retryReferral()</tt> or <tt>skipReferral()</tt>
     * to continue processing referrals.
     */
    public abstract Context
        getReferralContext(Hashtable<?,?> env)
        throws NamingException;

    /**
     * Retrieves the context at which to continue the method using
     * request controls and environment properties.
     * Regardless of whether a referral is encountered directly during a
     * context operation, or indirectly, for example, during a search
     * enumeration, the referral exception should provide a context
     * at which to continue the operation.
     * To continue the operation, the client program should re-invoke
     * the method using the same arguments as the original invocation.
     *<p>
     * <tt>reqCtls</tt> is used when creating the connection to the referred
     * server. These controls will be used as the connection request controls for
     * the context and context instances
     * derived from the context.
     * <tt>reqCtls</tt> will also be the context's request controls for
     * subsequent context operations. See the <tt>LdapContext</tt> class
     * description for details.
     *<p>
     * This method should be used instead of the other two overloaded forms
     * when the caller needs to supply request controls for creating
     * the referral context. It might need to do this, for example, when
     * it needs to supply special controls relating to authentication.
     *<p>
     * Service provider implementors should read the "Service Provider" section
     * in the <tt>LdapContext</tt> class description for implementation details.
     *
     * @param reqCtls The possibly null request controls to use for the new context.
     * If null or the empty array means use no request controls.
     * @param env The possibly null environment properties to use when
     * for the new context. If null, the context is initialized with no environment
     * properties.
     * @return The non-null context at which to continue the method.
     * @exception NamingException If a naming exception was encountered.
     * Call either <tt>retryReferral()</tt> or <tt>skipReferral()</tt>
     * to continue processing referrals.
     */
    public abstract Context
        getReferralContext(Hashtable<?,?> env,
                           Control[] reqCtls)
        throws NamingException;

    private static final long serialVersionUID = -1668992791764950804L;
}
