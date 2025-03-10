/*
 * Copyright (c) 2003, 2004, Oracle and/or its affiliates. All rights reserved.
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

import com.sun.corba.se.impl.presentation.rmi.StubFactoryDynamicBase;
import com.sun.corba.se.spi.orbutil.proxy.InvocationHandlerFactory;
import com.sun.corba.se.spi.orbutil.proxy.LinkedInvocationHandler;
import com.sun.corba.se.spi.presentation.rmi.DynamicStub;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;

import java.lang.reflect.Proxy;

public class StubFactoryProxyImpl extends StubFactoryDynamicBase
{
    public StubFactoryProxyImpl( PresentationManager.ClassData classData,
        ClassLoader loader )
    {
        super( classData, loader ) ;
    }

    public org.omg.CORBA.Object makeStub()
    {
        // Construct the dynamic proxy that implements this stub
        // using the composite handler
        InvocationHandlerFactory factory = classData.getInvocationHandlerFactory() ;
        LinkedInvocationHandler handler =
            (LinkedInvocationHandler)factory.getInvocationHandler() ;
        Class[] interfaces = factory.getProxyInterfaces() ;
        DynamicStub stub = (DynamicStub)Proxy.newProxyInstance( loader, interfaces,
            handler ) ;
        handler.setProxy( (Proxy)stub ) ;
        return stub ;
    }
}
