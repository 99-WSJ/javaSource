/*
 * Copyright (c) 2004, 2013, Oracle and/or its affiliates. All rights reserved.
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

import com.sun.corba.se.impl.presentation.rmi.DynamicStubImpl;
import com.sun.corba.se.impl.presentation.rmi.StubInvocationHandlerImpl;
import com.sun.corba.se.spi.orbutil.proxy.*;
import com.sun.corba.se.spi.presentation.rmi.DynamicStub;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class InvocationHandlerFactoryImpl implements InvocationHandlerFactory
{
    private final PresentationManager.ClassData classData ;
    private final PresentationManager pm ;
    private Class[] proxyInterfaces ;

    public InvocationHandlerFactoryImpl( PresentationManager pm,
        PresentationManager.ClassData classData )
    {
        this.classData = classData ;
        this.pm = pm ;

        Class[] remoteInterfaces =
            classData.getIDLNameTranslator().getInterfaces() ;
        proxyInterfaces = new Class[ remoteInterfaces.length + 1 ] ;
        for (int ctr=0; ctr<remoteInterfaces.length; ctr++)
            proxyInterfaces[ctr] = remoteInterfaces[ctr] ;

        proxyInterfaces[remoteInterfaces.length] = DynamicStub.class ;
    }

    private class CustomCompositeInvocationHandlerImpl extends
        CompositeInvocationHandlerImpl implements LinkedInvocationHandler,
        Serializable
    {
        private transient DynamicStub stub ;

        public void setProxy( Proxy proxy )
        {
            ((DynamicStubImpl)stub).setSelf( (DynamicStub)proxy ) ;
        }

        public Proxy getProxy()
        {
            return (Proxy)((DynamicStubImpl)stub).getSelf() ;
        }

        public CustomCompositeInvocationHandlerImpl( DynamicStub stub )
        {
            this.stub = stub ;
        }

        /** Return the stub, which will actually be written to the stream.
         * It will be custom marshalled, with the actual writing done in
         * StubIORImpl.  There is a corresponding readResolve method on
         * DynamicStubImpl which will re-create the full invocation
         * handler on read, and return the invocation handler on the
         * readResolve method.
         */
        public Object writeReplace() throws ObjectStreamException
        {
            return stub ;
        }
    }

    public InvocationHandler getInvocationHandler()
    {
        final DynamicStub stub = new DynamicStubImpl(
            classData.getTypeIds() ) ;

        return getInvocationHandler( stub ) ;
    }

    // This is also used in DynamicStubImpl to implement readResolve.
    InvocationHandler getInvocationHandler( DynamicStub stub )
    {
        // Create an invocation handler for the methods defined on DynamicStub,
        // which extends org.omg.CORBA.Object.  This handler delegates all
        // calls directly to a DynamicStubImpl, which extends
        // org.omg.CORBA.portable.ObjectImpl.
        final InvocationHandler dynamicStubHandler =
            DelegateInvocationHandlerImpl.create( stub ) ;

        // Create an invocation handler that handles any remote interface
        // methods.
        final InvocationHandler stubMethodHandler = new StubInvocationHandlerImpl(
            pm, classData, stub ) ;

        // Create a composite handler that handles the DynamicStub interface
        // as well as the remote interfaces.
        final CompositeInvocationHandler handler =
            new CustomCompositeInvocationHandlerImpl( stub ) ;

        AccessController.doPrivileged(new PrivilegedAction<Void>() {
            @Override
            public Void run() {
        handler.addInvocationHandler( DynamicStub.class,
            dynamicStubHandler ) ;
        handler.addInvocationHandler( org.omg.CORBA.Object.class,
            dynamicStubHandler ) ;
        handler.addInvocationHandler( Object.class,
            dynamicStubHandler ) ;
                return null;
            }
        });


        // If the method passed to invoke is not from DynamicStub or its superclasses,
        // it must be from an implemented interface, so we just handle
        // all of these with the stubMethodHandler.  This used to be
        // done be adding explicit entries for stubMethodHandler for
        // each remote interface, but that does not work correctly
        // for abstract interfaces, since the graph analysis ignores
        // abstract interfaces in order to compute the type ids
        // correctly (see PresentationManagerImpl.NodeImpl.getChildren).
        // Rather than produce more graph traversal code to handle this
        // problem, we simply use a default.
        // This also points to a possible optimization: just use explict
        // checks for the three special classes, rather than a general
        // table lookup that usually fails.
        handler.setDefaultHandler( stubMethodHandler ) ;

        return handler ;
    }

    public Class[] getProxyInterfaces()
    {
        return proxyInterfaces ;
    }
}
