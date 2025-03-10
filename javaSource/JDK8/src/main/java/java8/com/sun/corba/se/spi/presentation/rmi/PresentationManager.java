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

package java8.com.sun.corba.se.spi.presentation.rmi;

import com.sun.corba.se.spi.orbutil.proxy.InvocationHandlerFactory;
import com.sun.corba.se.spi.presentation.rmi.DynamicMethodMarshaller;
import com.sun.corba.se.spi.presentation.rmi.IDLNameTranslator;

import javax.rmi.CORBA.Tie;
import java.lang.reflect.Method;
import java.util.Map;


/** Provides access to RMI-IIOP stubs and ties.
 * Any style of stub and tie generation may be used.
 * This includes compiler generated stubs and runtime generated stubs
 * as well as compiled and reflective ties.  There is normally
 * only one instance of this interface per VM.  The instance
 * is obtained from the static method
 * com.sun.corba.se.spi.orb.ORB.getPresentationManager.
 * <p>
 * Note that
 * the getClassData and getDynamicMethodMarshaller methods
 * maintain caches to avoid redundant computation.
 */
public interface PresentationManager
{
    /** Creates StubFactory and Tie instances.
     */
    public interface StubFactoryFactory
    {
        /** Return the standard name of a stub (according to the RMI-IIOP specification
         * and rmic).  This is needed so that the name of a stub is known for
         * standalone clients of the app server.
         */
        String getStubName( String className ) ;

        /** Create a stub factory for stubs for the interface whose type is given by
         * className.  className may identify either an IDL interface or an RMI-IIOP
         * interface.
         * @param className The name of the remote interface as a Java class name.
         * @param isIDLStub True if className identifies an IDL stub, else false.
         * @param remoteCodeBase The CodeBase to use for loading Stub classes, if
         * necessary (may be null or unused).
         * @param expectedClass The expected stub type (may be null or unused).
         * @param classLoader The classLoader to use (may be null).
         */
        com.sun.corba.se.spi.presentation.rmi.PresentationManager.StubFactory createStubFactory(String className,
                                                                                                boolean isIDLStub, String remoteCodeBase, Class expectedClass,
                                                                                                ClassLoader classLoader);

        /** Return a Tie for the given class.
         */
        Tie getTie( Class cls ) ;

        /** Return whether or not this StubFactoryFactory creates StubFactory
         * instances that create dynamic stubs and ties.  At the top level,
         * true indicates that rmic -iiop is not needed for generating stubs
         * or ties.
         */
        boolean createsDynamicStubs() ;
    }

    /** Creates the actual stub needed for RMI-IIOP remote
     * references.
     */
    public interface StubFactory
    {
        /** Create a new dynamic stub.  It has the type that was
         * used to create this factory.
         */
        org.omg.CORBA.Object makeStub() ;

        /** Return the repository ID information for all Stubs
         * created by this stub factory.
         */
        String[] getTypeIds() ;
    }

    public interface ClassData
    {
        /** Get the class used to create this ClassData instance
         */
        Class getMyClass() ;

        /** Get the IDLNameTranslator for the class used to create
         * this ClassData instance.
         */
        IDLNameTranslator getIDLNameTranslator() ;

        /** Return the array of repository IDs for all of the remote
         * interfaces implemented by this class.
         */
        String[] getTypeIds() ;

        /** Get the InvocationHandlerFactory that is used to create
         * an InvocationHandler for dynamic stubs of the type of the
         * ClassData.
         */
        InvocationHandlerFactory getInvocationHandlerFactory() ;

        /** Get the dictionary for this ClassData instance.
         * This is used to hold class-specific information for a Class
         * in the class data.  This avoids the need to create other
         * caches for accessing the information.
         */
        Map getDictionary() ;
    }

    /** Get the ClassData for a particular class.
     * This class may be an implementation class, in which
     * case the IDLNameTranslator handles all Remote interfaces implemented by
     * the class.  If the class implements more than one remote interface, and not
     * all of the remote interfaces are related by inheritance, then the type
     * IDs have the implementation class as element 0.
     */
    ClassData getClassData( Class cls ) ;

    /** Given a particular method, return a DynamicMethodMarshaller
     * for that method.  This is used for dynamic stubs and ties.
     */
    DynamicMethodMarshaller getDynamicMethodMarshaller( Method method ) ;

    /** Return the registered StubFactoryFactory.
     */
    StubFactoryFactory getStubFactoryFactory( boolean isDynamic ) ;

    /** Register the StubFactoryFactory.  Note that
     * a static StubFactoryFactory is always required for IDL.  The
     * dynamic stubFactoryFactory is optional.
     */
    void setStubFactoryFactory( boolean isDynamic, StubFactoryFactory sff ) ;

    /** Equivalent to getStubFactoryFactory( true ).getTie( null ).
     * Provided for compatibility with earlier versions of PresentationManager
     * as used in the app server.  The class argument is ignored in
     * the dynamic case, so this is safe.
     */
    Tie getTie() ;

    /** Returns the value of the com.sun.CORBA.ORBUseDynamicStub
     * property.
     */
    boolean useDynamicStubs() ;
}
