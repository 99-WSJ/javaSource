/*
 * Copyright (c) 1999, 2003, Oracle and/or its affiliates. All rights reserved.
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

package java8.sun.corba.se.spi.servicecontext;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.servicecontext.ServiceContext;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA_2_3.portable.InputStream;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

/** Internal class used to hold data about a service context class.
*/
public class ServiceContextData {
    private void dprint( String msg )
    {
        ORBUtility.dprint( this, msg ) ;
    }

    private void throwBadParam( String msg, Throwable exc )
    {
        BAD_PARAM error = new BAD_PARAM( msg ) ;
        if (exc != null)
            error.initCause( exc ) ;
        throw error ;
    }

    public ServiceContextData( Class cls )
    {
        if (ORB.ORBInitDebug)
            dprint( "ServiceContextData constructor called for class " + cls ) ;

        scClass = cls ;

        try {
            if (ORB.ORBInitDebug)
                dprint( "Finding constructor for " + cls ) ;

            // Find the appropriate constructor in cls
            Class[] args = new Class[2] ;
            args[0] = InputStream.class ;
            args[1] = GIOPVersion.class;
            try {
                scConstructor = cls.getConstructor( args ) ;
            } catch (NoSuchMethodException nsme) {
                throwBadParam( "Class does not have an InputStream constructor", nsme ) ;
            }

            if (ORB.ORBInitDebug)
                dprint( "Finding SERVICE_CONTEXT_ID field in " + cls ) ;

            // get the ID from the public static final int SERVICE_CONTEXT_ID
            Field fld = null ;
            try {
                fld = cls.getField( "SERVICE_CONTEXT_ID" ) ;
            } catch (NoSuchFieldException nsfe) {
                throwBadParam( "Class does not have a SERVICE_CONTEXT_ID member", nsfe ) ;
            } catch (SecurityException se) {
                throwBadParam( "Could not access SERVICE_CONTEXT_ID member", se ) ;
            }

            if (ORB.ORBInitDebug)
                dprint( "Checking modifiers of SERVICE_CONTEXT_ID field in " + cls ) ;

            int mod = fld.getModifiers() ;
            if (!Modifier.isPublic(mod) || !Modifier.isStatic(mod) ||
                !Modifier.isFinal(mod) )
                throwBadParam( "SERVICE_CONTEXT_ID field is not public static final", null ) ;

            if (ORB.ORBInitDebug)
                dprint( "Getting value of SERVICE_CONTEXT_ID in " + cls ) ;

            try {
                scId = fld.getInt( null ) ;
            } catch (IllegalArgumentException iae) {
                throwBadParam( "SERVICE_CONTEXT_ID not convertible to int", iae ) ;
            } catch (IllegalAccessException iae2) {
                throwBadParam( "Could not access value of SERVICE_CONTEXT_ID", iae2 ) ;
            }
        } catch (BAD_PARAM nssc) {
            if (ORB.ORBInitDebug)
                dprint( "Exception in ServiceContextData constructor: " + nssc ) ;
            throw nssc ;
        } catch (Throwable thr) {
            if (ORB.ORBInitDebug)
                dprint( "Unexpected Exception in ServiceContextData constructor: " +
                        thr ) ;
        }

        if (ORB.ORBInitDebug)
            dprint( "ServiceContextData constructor completed" ) ;
    }

    /** Factory method used to create a ServiceContext object by
     * unmarshalling it from the InputStream.
     */
    public ServiceContext makeServiceContext(InputStream is, GIOPVersion gv)
    {
        Object[] args = new Object[2];
        args[0] = is ;
        args[1] = gv;
        ServiceContext sc = null ;

        try {
            sc = (ServiceContext)(scConstructor.newInstance( args )) ;
        } catch (IllegalArgumentException iae) {
            throwBadParam( "InputStream constructor argument error", iae ) ;
        } catch (IllegalAccessException iae2) {
            throwBadParam( "InputStream constructor argument error", iae2 ) ;
        } catch (InstantiationException ie) {
            throwBadParam( "InputStream constructor called for abstract class", ie ) ;
        } catch (InvocationTargetException ite) {
            throwBadParam( "InputStream constructor threw exception " +
                ite.getTargetException(), ite ) ;
        }

        return sc ;
    }

    int getId()
    {
        return scId ;
    }

    public String toString()
    {
        return "ServiceContextData[ scClass=" + scClass + " scConstructor=" +
            scConstructor + " scId=" + scId + " ]" ;
    }

    private Class       scClass ;
    private Constructor scConstructor ;
    private int         scId ;
}
