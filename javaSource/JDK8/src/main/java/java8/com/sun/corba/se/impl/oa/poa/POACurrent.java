/*
 * Copyright (c) 1997, 2003, Oracle and/or its affiliates. All rights reserved.
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

package java8.sun.corba.se.impl.oa.poa;

import com.sun.corba.se.impl.logging.POASystemException;
import com.sun.corba.se.spi.logging.CORBALogDomains;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.CompletionStatus;
import org.omg.PortableServer.CurrentPackage.NoContext;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.ServantLocatorPackage.CookieHolder;

import java.util.EmptyStackException;

// XXX Needs to be turned into LocalObjectImpl.

public class POACurrent extends org.omg.CORBA.portable.ObjectImpl
    implements org.omg.PortableServer.Current
{
    private ORB orb;
    private POASystemException wrapper ;

    public POACurrent(ORB orb)
    {
        this.orb = orb;
        wrapper = POASystemException.get( orb,
            CORBALogDomains.OA_INVOCATION ) ;
    }

    public String[] _ids()
    {
        String[] ids = new String[1];
        ids[0] = "IDL:omg.org/PortableServer/Current:1.0";
        return ids;
    }

    //
    // Standard OMG operations.
    //

    public POA get_POA()
        throws
            NoContext
    {
        POA poa = (POA)(peekThrowNoContext().oa());
        throwNoContextIfNull(poa);
        return poa;
    }

    public byte[] get_object_id()
        throws
            NoContext
    {
        byte[] objectid = peekThrowNoContext().id();
        throwNoContextIfNull(objectid);
        return objectid;
    }

    //
    // Implementation operations used by POA package.
    //

    public ObjectAdapter getOA()
    {
        ObjectAdapter oa = peekThrowInternal().oa();
        throwInternalIfNull(oa);
        return oa;
    }

    public byte[] getObjectId()
    {
        byte[] objectid = peekThrowInternal().id();
        throwInternalIfNull(objectid);
        return objectid;
    }

    Servant getServant()
    {
        Servant servant = (Servant)(peekThrowInternal().getServantContainer());
        // If is OK for the servant to be null.
        // This could happen if POAImpl.getServant is called but
        // POAImpl.internalGetServant throws an exception.
        return servant;
    }

    CookieHolder getCookieHolder()
    {
        CookieHolder cookieHolder = peekThrowInternal().getCookieHolder();
        throwInternalIfNull(cookieHolder);
        return cookieHolder;
    }

    // This is public so we can test the stack balance.
    // It is not a security hole since this same info can be obtained from
    // PortableInterceptors.
    public String getOperation()
    {
        String operation = peekThrowInternal().getOperation();
        throwInternalIfNull(operation);
        return operation;
    }

    void setServant(Servant servant)
    {
        peekThrowInternal().setServant( servant );
    }

    //
    // Class utilities.
    //

    private OAInvocationInfo peekThrowNoContext()
        throws
            NoContext
    {
        OAInvocationInfo invocationInfo = null;
        try {
            invocationInfo = orb.peekInvocationInfo() ;
        } catch (EmptyStackException e) {
            throw new NoContext();
        }
        return invocationInfo;
    }

    private OAInvocationInfo peekThrowInternal()
    {
        OAInvocationInfo invocationInfo = null;
        try {
            invocationInfo = orb.peekInvocationInfo() ;
        } catch (EmptyStackException e) {
            // The completion status is maybe because this could happen
            // after the servant has been invoked.
            throw wrapper.poacurrentUnbalancedStack( e ) ;
        }
        return invocationInfo;
    }

    private void throwNoContextIfNull(Object o)
        throws
            NoContext
    {
        if ( o == null ) {
            throw new NoContext();
        }
    }

    private void throwInternalIfNull(Object o)
    {
        if ( o == null ) {
            throw wrapper.poacurrentNullField( CompletionStatus.COMPLETED_MAYBE ) ;
        }
    }
}
