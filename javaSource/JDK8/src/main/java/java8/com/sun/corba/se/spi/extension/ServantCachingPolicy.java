/*
 * Copyright (c) 2001, 2003, Oracle and/or its affiliates. All rights reserved.
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

package java8.com.sun.corba.se.spi.extension;

import com.sun.corba.se.impl.orbutil.ORBConstants;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.Policy;

/** Policy used to implement servant caching optimization in the POA.
* Creating a POA with an instance pol of this policy where
* pol.getType() &gt; NO_SERVANT_CACHING will cause the servant to be
* looked up in the POA and cached in the LocalClientRequestDispatcher when
* the ClientRequestDispatcher is colocated with the implementation of the
* objref.  This greatly speeds up invocations at the cost of violating the
* POA semantics.  In particular, every request to a particular objref
* must be handled by the same servant.  Note that this is typically the
* case for EJB implementations.
* <p>
* If servant caching is used, there are two different additional
* features of the POA that are expensive:
* <ol>
* <li>POA current semantics
* <li>Proper handling of POA destroy.
* <ol>
* POA current semantics requires maintaining a ThreadLocal stack of
* invocation information that is always available for POACurrent operations.
* Maintaining this stack is expensive on the timescale of optimized co-located
* calls, so the option is provided to turn it off.  Similarly, causing
* POA.destroy() calls to wait for all active calls in the POA to complete
* requires careful tracking of the entry and exit of invocations in the POA.
* Again, tracking this is somewhat expensive.
*/
public class ServantCachingPolicy extends LocalObject implements Policy
{
    /** Do not cache servants in the ClientRequestDispatcher.  This will
     * always support the full POA semantics, including changing the
     * servant that handles requests on a particular objref.
     */
    public static final int NO_SERVANT_CACHING = 0 ;

    /** Perform servant caching, preserving POA current and POA destroy semantics.
    * We will use this as the new default, as the app server is making heavier use
    * now of POA facilities.
    */
    public static final int FULL_SEMANTICS = 1 ;

    /** Perform servant caching, preservent only POA current semantics.
    * At least this level is required in order to support selection of ObjectCopiers
    * for co-located RMI-IIOP calls, as the current copier is stored in
    * OAInvocationInfo, which must be present on the stack inside the call.
    */
    public static final int INFO_ONLY_SEMANTICS =  2 ;

    /** Perform servant caching, not preserving POA current or POA destroy semantics.
    */
    public static final int MINIMAL_SEMANTICS = 3 ;

    private static com.sun.corba.se.spi.extension.ServantCachingPolicy policy = null ;
    private static com.sun.corba.se.spi.extension.ServantCachingPolicy infoOnlyPolicy = null ;
    private static com.sun.corba.se.spi.extension.ServantCachingPolicy minimalPolicy = null ;

    private int type ;

    public String typeToName()
    {
        switch (type) {
            case FULL_SEMANTICS:
                return "FULL" ;
            case INFO_ONLY_SEMANTICS:
                return "INFO_ONLY" ;
            case MINIMAL_SEMANTICS:
                return "MINIMAL" ;
            default:
                return "UNKNOWN(" + type + ")" ;
        }
    }

    public String toString()
    {
        return "ServantCachingPolicy[" + typeToName() + "]" ;
    }

    private ServantCachingPolicy( int type )
    {
        this.type = type ;
    }

    public int getType()
    {
        return type ;
    }

    /** Return the default servant caching policy.
    */
    public synchronized static com.sun.corba.se.spi.extension.ServantCachingPolicy getPolicy()
    {
        return getFullPolicy() ;
    }

    public synchronized static com.sun.corba.se.spi.extension.ServantCachingPolicy getFullPolicy()
    {
        if (policy == null)
            policy = new com.sun.corba.se.spi.extension.ServantCachingPolicy( FULL_SEMANTICS ) ;

        return policy ;
    }

    public synchronized static com.sun.corba.se.spi.extension.ServantCachingPolicy getInfoOnlyPolicy()
    {
        if (infoOnlyPolicy == null)
            infoOnlyPolicy = new com.sun.corba.se.spi.extension.ServantCachingPolicy( INFO_ONLY_SEMANTICS ) ;

        return infoOnlyPolicy ;
    }

    public synchronized static com.sun.corba.se.spi.extension.ServantCachingPolicy getMinimalPolicy()
    {
        if (minimalPolicy == null)
            minimalPolicy = new com.sun.corba.se.spi.extension.ServantCachingPolicy( MINIMAL_SEMANTICS ) ;

        return minimalPolicy ;
    }

    public int policy_type ()
    {
        return ORBConstants.SERVANT_CACHING_POLICY ;
    }

    public Policy copy ()
    {
        return this ;
    }

    public void destroy ()
    {
        // NO-OP
    }
}
