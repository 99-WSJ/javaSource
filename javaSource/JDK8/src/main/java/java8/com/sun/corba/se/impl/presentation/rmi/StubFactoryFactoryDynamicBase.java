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

package java8.com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.presentation.rmi.ReflectiveTie;
import com.sun.corba.se.impl.presentation.rmi.StubFactoryFactoryBase;
import com.sun.corba.se.spi.logging.CORBALogDomains;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.portable.IDLEntity;

import javax.rmi.CORBA.Tie;
import javax.rmi.CORBA.Util;
import java.rmi.Remote;

public abstract class StubFactoryFactoryDynamicBase extends
    StubFactoryFactoryBase
{
    protected final ORBUtilSystemException wrapper ;

    public StubFactoryFactoryDynamicBase()
    {
        wrapper = ORBUtilSystemException.get(
            CORBALogDomains.RPC_PRESENTATION ) ;
    }

    public PresentationManager.StubFactory createStubFactory(
        String className, boolean isIDLStub, String remoteCodeBase,
        Class expectedClass, ClassLoader classLoader)
    {
        Class cls = null ;

        try {
            cls = Util.loadClass( className, remoteCodeBase, classLoader ) ;
        } catch (ClassNotFoundException exc) {
            throw wrapper.classNotFound3(
                CompletionStatus.COMPLETED_MAYBE, exc, className ) ;
        }

        PresentationManager pm = ORB.getPresentationManager() ;

        if (IDLEntity.class.isAssignableFrom( cls ) &&
            !Remote.class.isAssignableFrom( cls )) {
            // IDL stubs must always use static factories.
            PresentationManager.StubFactoryFactory sff =
                pm.getStubFactoryFactory( false ) ;
            PresentationManager.StubFactory sf =
                sff.createStubFactory( className, true, remoteCodeBase,
                    expectedClass, classLoader ) ;
            return sf ;
        } else {
            PresentationManager.ClassData classData = pm.getClassData( cls ) ;
            return makeDynamicStubFactory( pm, classData, classLoader ) ;
        }
    }

    public abstract PresentationManager.StubFactory makeDynamicStubFactory(
        PresentationManager pm, PresentationManager.ClassData classData,
        ClassLoader classLoader ) ;

    public Tie getTie( Class cls )
    {
        PresentationManager pm = ORB.getPresentationManager() ;
        return new ReflectiveTie( pm, wrapper ) ;
    }

    public boolean createsDynamicStubs()
    {
        return true ;
    }
}
