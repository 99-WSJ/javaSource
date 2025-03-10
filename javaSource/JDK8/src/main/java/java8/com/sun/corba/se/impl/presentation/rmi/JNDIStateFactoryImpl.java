/*
 * Copyright (c) 2004, Oracle and/or its affiliates. All rights reserved.
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

package java8.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import com.sun.jndi.cosnaming.CNCtx;

import javax.naming.ConfigurationException;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.spi.StateFactory;
import javax.rmi.PortableRemoteObject;
import java.lang.reflect.Field;
import java.rmi.Remote;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Hashtable;

/**
  * StateFactory that turns java.rmi.Remote objects to org.omg.CORBA.Object.
  * This version works either with standard RMI-IIOP or Dynamic RMI-IIOP.
  * Based on the original com.sun.jndi.cosnaming.RemoteToCorba and
  * com.sun.jndi.toolkit.corba.CorbaUtils.
  *
  * @author Ken Cavanaugh
  */

public class JNDIStateFactoryImpl implements StateFactory
{
    private static final Field orbField ;

    static {
        orbField = (Field) AccessController.doPrivileged(
            new PrivilegedAction() {
                public Object run() {
                    Field fld = null ;
                    try {
                        Class cls = CNCtx.class ;
                        fld = cls.getDeclaredField( "_orb" ) ;
                        fld.setAccessible( true ) ;
                    } catch (Exception exc) {
                        // XXX log exception at FINE
                    }
                    return fld ;
                }
            }
        ) ;
    }

    public JNDIStateFactoryImpl()
    {
    }

    /**
     * Returns the CORBA object for a Remote object.
     * If input is not a Remote object, or if Remote object uses JRMP, return null.
     * If the RMI-IIOP library is not available, throw ConfigurationException.
     *
     * @param orig The object to turn into a CORBA object. If not Remote,
     *             or if is a JRMP stub or impl, return null.
     * @param name Ignored
     * @param ctx The non-null CNCtx whose ORB to use.
     * @param env Ignored
     * @return The CORBA object for <tt>orig</tt> or null.
     * @exception ConfigurationException If the CORBA object cannot be obtained
     *    due to configuration problems
     * @exception NamingException If some other problem prevented a CORBA
     *    object from being obtained from the Remote object.
     */
    public Object getStateToBind(Object orig, Name name, Context ctx,
        Hashtable<?,?> env) throws NamingException
    {
        if (orig instanceof org.omg.CORBA.Object)
            return orig ;

        if (!(orig instanceof Remote))
            // Not for this StateFactory
            return null ;

        ORB orb = getORB( ctx ) ;
        if (orb == null)
            // Wrong kind of context, so just give up and let another StateFactory
            // try to satisfy getStateToBind.
            return null ;

        Remote stub = null;

        try {
            stub = PortableRemoteObject.toStub( (Remote)orig ) ;
        } catch (Exception exc) {
            // XXX log at FINE level?
            // Wrong sort of object: just return null to allow another StateFactory
            // to handle this.  This can happen easily because this StateFactory
            // is specified for the application, not the service context provider.
            return null ;
        }

        if (StubAdapter.isStub( stub )) {
            try {
                StubAdapter.connect( stub, orb ) ;
            } catch (Exception exc) {
                if (!(exc instanceof java.rmi.RemoteException)) {
                    // XXX log at FINE level?
                    // Wrong sort of object: just return null to allow another StateFactory
                    // to handle this call.
                    return null ;
                }

                // ignore RemoteException because stub might have already
                // been connected
            }
        }

        return stub ;
    }

    // This is necessary because the _orb field is package private in
    // com.sun.jndi.cosnaming.CNCtx.  This is not an ideal solution.
    // The best solution for our ORB is to change the CosNaming provider
    // to use the StubAdapter.  But this has problems as well, because
    // other vendors may use the CosNaming provider with a different ORB
    // entirely.
    private ORB getORB( Context ctx )
    {
        ORB orb = null ;

        try {
            orb = (ORB)orbField.get( ctx ) ;
        } catch (Exception exc) {
            // XXX log this exception at FINE level
            // ignore the exception and return null.
            // Note that the exception may be because ctx
            // is not a CosNaming context.
        }

        return orb ;
    }
}
