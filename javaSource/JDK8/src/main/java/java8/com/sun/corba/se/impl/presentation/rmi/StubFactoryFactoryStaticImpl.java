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
import com.sun.corba.se.impl.presentation.rmi.StubFactoryFactoryBase;
import com.sun.corba.se.impl.presentation.rmi.StubFactoryStaticImpl;
import com.sun.corba.se.impl.util.PackagePrefixChecker;
import com.sun.corba.se.impl.util.Utility;
import com.sun.corba.se.spi.logging.CORBALogDomains;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import org.omg.CORBA.CompletionStatus;

import javax.rmi.CORBA.Tie;
import javax.rmi.CORBA.Util;

public class StubFactoryFactoryStaticImpl extends
    StubFactoryFactoryBase
{
    private ORBUtilSystemException wrapper = ORBUtilSystemException.get(
        CORBALogDomains.RPC_PRESENTATION ) ;

    public PresentationManager.StubFactory createStubFactory(
        String className, boolean isIDLStub, String remoteCodeBase, Class
        expectedClass, ClassLoader classLoader)
    {
        String stubName = null ;

        if (isIDLStub)
            stubName = Utility.idlStubName( className ) ;
        else
            stubName = Utility.stubNameForCompiler( className ) ;

        ClassLoader expectedTypeClassLoader =
            (expectedClass == null ? classLoader :
            expectedClass.getClassLoader());

        // The old code was optimized to try to guess which way to load classes
        // first.  The real stub class name could either be className or
        // "org.omg.stub." + className.  We will compute this as follows:
        // If stubName starts with a "forbidden" package, try the prefixed
        // version first, otherwise try the non-prefixed version first.
        // In any case, try both forms if necessary.

        String firstStubName = stubName ;
        String secondStubName = stubName ;

        if (PackagePrefixChecker.hasOffendingPrefix(stubName))
            firstStubName = PackagePrefixChecker.packagePrefix() + stubName ;
        else
            secondStubName = PackagePrefixChecker.packagePrefix() + stubName ;

        Class clz = null;

        try {
            clz = Util.loadClass( firstStubName, remoteCodeBase,
                expectedTypeClassLoader ) ;
        } catch (ClassNotFoundException e1) {
            // log only at FINE level
            wrapper.classNotFound1( CompletionStatus.COMPLETED_MAYBE,
                e1, firstStubName ) ;
            try {
                clz = Util.loadClass( secondStubName, remoteCodeBase,
                    expectedTypeClassLoader ) ;
            } catch (ClassNotFoundException e2) {
                throw wrapper.classNotFound2(
                    CompletionStatus.COMPLETED_MAYBE, e2, secondStubName ) ;
            }
        }

        // XXX Is this step necessary, or should the Util.loadClass
        // algorithm always produce a valid class if the setup is correct?
        // Does the OMG standard algorithm need to be changed to include
        // this step?
        if ((clz == null) ||
            ((expectedClass != null) && !expectedClass.isAssignableFrom(clz))) {
            try {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                if (cl == null)
                    cl = ClassLoader.getSystemClassLoader();

                clz = cl.loadClass(className);
            } catch (Exception exc) {
                // XXX make this a system exception
                IllegalStateException ise = new IllegalStateException(
                    "Could not load class " + stubName ) ;
                ise.initCause( exc ) ;
                throw ise ;
            }
        }

        return new StubFactoryStaticImpl( clz ) ;
    }

    public Tie getTie( Class cls )
    {
        Class tieClass = null ;
        String className = Utility.tieName(cls.getName());

        // XXX log exceptions at FINE level
        try {
            try {
                //_REVISIT_ The spec does not specify a loadingContext parameter for
                //the following call.  Would it be useful to pass one?
                tieClass = Utility.loadClassForClass(className, Util.getCodebase(cls),
                    null, cls, cls.getClassLoader());
                return (Tie) tieClass.newInstance();
            } catch (Exception err) {
                tieClass = Utility.loadClassForClass(
                    PackagePrefixChecker.packagePrefix() + className,
                    Util.getCodebase(cls), null, cls, cls.getClassLoader());
                return (Tie) tieClass.newInstance();
            }
        } catch (Exception err) {
            return null;
        }

    }

    public boolean createsDynamicStubs()
    {
        return false ;
    }
}
