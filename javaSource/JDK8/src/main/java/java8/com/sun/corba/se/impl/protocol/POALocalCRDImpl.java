/*
 * Copyright (c) 2002, 2003, Oracle and/or its affiliates. All rights reserved.
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

package java8.com.sun.corba.se.impl.protocol;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.logging.POASystemException;
import com.sun.corba.se.impl.protocol.LocalClientRequestDispatcherBase;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.logging.CORBALogDomains;
import com.sun.corba.se.spi.oa.OADestroyed;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.ForwardException;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.ServantObject;

public class POALocalCRDImpl extends LocalClientRequestDispatcherBase
{
    private ORBUtilSystemException wrapper ;
    private POASystemException poaWrapper ;

    public POALocalCRDImpl( ORB orb, int scid, IOR ior)
    {
        super( (ORB)orb, scid, ior );
        wrapper = ORBUtilSystemException.get( orb,
            CORBALogDomains.RPC_PROTOCOL ) ;
        poaWrapper = POASystemException.get( orb,
            CORBALogDomains.RPC_PROTOCOL ) ;
    }

    private OAInvocationInfo servantEnter( ObjectAdapter oa ) throws OADestroyed
    {
        oa.enter() ;

        OAInvocationInfo info = oa.makeInvocationInfo( objectId ) ;
        orb.pushInvocationInfo( info ) ;

        return info ;
    }

    private void servantExit( ObjectAdapter oa )
    {
        try {
            oa.returnServant();
        } finally {
            oa.exit() ;
            orb.popInvocationInfo() ;
        }
    }

    // Look up the servant for this request and return it in a
    // ServantObject.  Note that servant_postinvoke is always called
    // by the stub UNLESS this method returns null.  However, in all
    // cases we must be sure that ObjectAdapter.getServant and
    // ObjectAdapter.returnServant calls are paired, as required for
    // Portable Interceptors and Servant Locators in the POA.
    // Thus, this method must call returnServant if it returns null.
    public ServantObject servant_preinvoke(org.omg.CORBA.Object self,
                                           String operation,
                                           Class expectedType)
    {
        ObjectAdapter oa = oaf.find( oaid ) ;
        OAInvocationInfo info = null ;

        try {
            info = servantEnter( oa ) ;
            info.setOperation( operation ) ;
        } catch ( OADestroyed ex ) {
            // Destroyed POAs can be recreated by normal adapter activation.
            // So just reinvoke this method.
            return servant_preinvoke(self, operation, expectedType);
        }

        try {
            try {
                oa.getInvocationServant( info );
                if (!checkForCompatibleServant( info, expectedType ))
                    return null ;
            } catch (Throwable thr) {
                // Cleanup after this call, then throw to allow
                // outer try to handle the exception appropriately.
                servantExit( oa ) ;
                throw thr ;
            }
        } catch ( ForwardException ex ) {
            /* REVISIT
            ClientRequestDispatcher csub = (ClientRequestDispatcher)
                StubAdapter.getDelegate( ex.forward_reference ) ;
            IOR ior = csub.getIOR() ;
            setLocatedIOR( ior ) ;
            */
            RuntimeException runexc = new RuntimeException("deal with this.");
            runexc.initCause( ex ) ;
            throw runexc ;
        } catch ( ThreadDeath ex ) {
            // ThreadDeath on the server side should not cause a client
            // side thread death in the local case.  We want to preserve
            // this behavior for location transparency, so that a ThreadDeath
            // has the same affect in either the local or remote case.
            // The non-colocated case is handled in iiop.ORB.process, which
            // throws the same exception.
            throw wrapper.runtimeexception( ex ) ;
        } catch ( Throwable t ) {
            if (t instanceof SystemException)
                throw (SystemException)t ;

            throw poaWrapper.localServantLookup( t ) ;
        }

        if (!checkForCompatibleServant( info, expectedType )) {
            servantExit( oa ) ;
            return null ;
        }

        return info;
    }

    public void servant_postinvoke(org.omg.CORBA.Object self,
                                   ServantObject servantobj)
    {
        ObjectAdapter oa = orb.peekInvocationInfo().oa() ;
        servantExit( oa ) ;
    }
}

// End of file.
