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

package java8.javax.naming;

import javax.naming.Context;
import javax.naming.NamingException;
import java.util.Hashtable;

/**
 * This abstract class is used to represent a referral exception,
 * which is generated in response to a <em>referral</em>
 * such as that returned by LDAP v3 servers.
 * <p>
 * A service provider provides
 * a subclass of <tt>ReferralException</tt> by providing implementations
 * for <tt>getReferralInfo()</tt> and <tt>getReferralContext()</tt> (and appropriate
 * constructors and/or corresponding "set" methods).
 * <p>
 * The following code sample shows how <tt>ReferralException</tt> can be used.
 * <blockquote>{@code
 *      while (true) {
 *          try {
 *              bindings = ctx.listBindings(name);
 *              while (bindings.hasMore()) {
 *                  b = bindings.next();
 *                  ...
 *              }
 *              break;
 *          } catch (ReferralException e) {
 *              ctx = e.getReferralContext();
 *          }
 *      }
 * }</blockquote>
 *<p>
 * <tt>ReferralException</tt> is an abstract class. Concrete implementations
 * determine its synchronization and serialization properties.
 *<p>
 * An environment parameter passed to the <tt>getReferralContext()</tt>
 * method is owned by the caller.
 * The service provider will not modify the object or keep a reference to it,
 * but may keep a reference to a clone of it.
 *
 * @author Rosanna Lee
 * @author Scott Seligman
 *
 * @since 1.3
 *
 */

public abstract class ReferralException extends NamingException {
    /**
     * Constructs a new instance of ReferralException using the
     * explanation supplied. All other fields are set to null.
     *
     * @param   explanation     Additional detail about this exception. Can be null.
     * @see Throwable#getMessage
     */
    protected ReferralException(String explanation) {
        super(explanation);
    }

    /**
      * Constructs a new instance of ReferralException.
      * All fields are set to null.
      */
    protected ReferralException() {
        super();
    }

    /**
     * Retrieves information (such as URLs) related to this referral.
     * The program may examine or display this information
     * to the user to determine whether to continue with the referral,
     * or to determine additional information needs to be supplied in order
     * to continue with the referral.
     *
     * @return Non-null referral information related to this referral.
     */
    public abstract Object getReferralInfo();

    /**
     * Retrieves the context at which to continue the method.
     * Regardless of whether a referral is encountered directly during a
     * context operation, or indirectly, for example, during a search
     * enumeration, the referral exception should provide a context
     * at which to continue the operation. The referral context is
     * created using the environment properties of the context
     * that threw the ReferralException.
     *
     *<p>
     * To continue the operation, the client program should re-invoke
     * the method using the same arguments as the original invocation.
     *
     * @return The non-null context at which to continue the method.
     * @exception NamingException If a naming exception was encountered.
     * Call either <tt>retryReferral()</tt> or <tt>skipReferral()</tt>
     * to continue processing referrals.
     */
    public abstract Context getReferralContext() throws NamingException;

    /**
     * Retrieves the context at which to continue the method using
     * environment properties.
     * Regardless of whether a referral is encountered directly during a
     * context operation, or indirectly, for example, during a search
     * enumeration, the referral exception should provide a context
     * at which to continue the operation.
     *<p>
     * The referral context is created using <tt>env</tt> as its environment
     * properties.
     * This method should be used instead of the no-arg overloaded form
     * when the caller needs to use different environment properties for
     * the referral context. It might need to do this, for example, when
     * it needs to supply different authentication information to the referred
     * server in order to create the referral context.
     *<p>
     * To continue the operation, the client program should re-invoke
     * the method using the same arguments as the original invocation.
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
     * Discards the referral about to be processed.
     * A call to this method should be followed by a call to
     * <code>getReferralContext</code> to allow the processing of
     * other referrals to continue.
     * The following code fragment shows a typical usage pattern.
     * <blockquote><pre>
     *  } catch (ReferralException e) {
     *      if (!shallIFollow(e.getReferralInfo())) {
     *          if (!e.skipReferral()) {
     *              return;
     *          }
     *      }
     *      ctx = e.getReferralContext();
     *  }
     * </pre></blockquote>
     *
     * @return true If more referral processing is pending; false otherwise.
     */
    public abstract boolean skipReferral();

    /**
     * Retries the referral currently being processed.
     * A call to this method should be followed by a call to
     * <code>getReferralContext</code> to allow the current
     * referral to be retried.
     * The following code fragment shows a typical usage pattern.
     * <blockquote><pre>
     *  } catch (ReferralException e) {
     *      while (true) {
     *          try {
     *              ctx = e.getReferralContext(env);
     *              break;
     *          } catch (NamingException ne) {
     *              if (! shallIRetry()) {
     *                  return;
     *              }
     *              // modify environment properties (env), if necessary
     *              e.retryReferral();
     *          }
     *      }
     *  }
     * </pre></blockquote>
     *
     */
    public abstract void retryReferral();

    /**
     * Use serialVersionUID from JNDI 1.1.1 for interoperability
     */
    private static final long serialVersionUID = -2881363844695698876L;
}
