/*
 * Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
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
/*
 * Licensed Materials - Property of IBM
 * RMI-IIOP v1.0
 * Copyright IBM Corp. 1998 1999  All Rights Reserved
 *
 */

package java8.javax.rmi;

import java.lang.reflect.Method ;

import org.omg.CORBA.INITIALIZE;
import javax.rmi.CORBA.Util;

import java.rmi.RemoteException;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.net.MalformedURLException ;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.rmi.server.RMIClassLoader;

import com.sun.corba.se.impl.orbutil.GetPropertyAction;

/**
 * Server implementation objects may either inherit from
 * javax.rmi.PortableRemoteObject or they may implement a remote interface
 * and then use the exportObject method to register themselves as a server object.
 * The toStub method takes a server implementation and returns a stub that
 * can be used to access that server object.
 * The connect method makes a Remote object ready for remote communication.
 * The unexportObject method is used to deregister a server object, allowing it to become
 * available for garbage collection.
 * The narrow method takes an object reference or abstract interface type and
 * attempts to narrow it to conform to
 * the given interface. If the operation is successful the result will be an
 * object of the specified type, otherwise an exception will be thrown.
 */
public class PortableRemoteObject {

    private static final javax.rmi.CORBA.PortableRemoteObjectDelegate proDelegate;

    private static final String PortableRemoteObjectClassKey =
            "javax.rmi.CORBA.PortableRemoteObjectClass";

    static {
        proDelegate = (javax.rmi.CORBA.PortableRemoteObjectDelegate)
            createDelegate(PortableRemoteObjectClassKey);
    }

    /**
     * Initializes the object by calling <code>exportObject(this)</code>.
     * @exception RemoteException if export fails.
     */
    protected PortableRemoteObject() throws RemoteException {
        if (proDelegate != null) {
            javax.rmi.PortableRemoteObject.exportObject((Remote)this);
        }
    }

    /**
     * Makes a server object ready to receive remote calls. Note
     * that subclasses of PortableRemoteObject do not need to call this
     * method, as it is called by the constructor.
     * @param obj the server object to export.
     * @exception RemoteException if export fails.
     */
    public static void exportObject(Remote obj)
        throws RemoteException {

        // Let the delegate do everything, including error handling.
        if (proDelegate != null) {
            proDelegate.exportObject(obj);
        }
    }

    /**
     * Returns a stub for the given server object.
     * @param obj the server object for which a stub is required. Must either be a subclass
     * of PortableRemoteObject or have been previously the target of a call to
     * {@link #exportObject}.
     * @return the most derived stub for the object.
     * @exception NoSuchObjectException if a stub cannot be located for the given server object.
     */
    public static Remote toStub (Remote obj)
        throws NoSuchObjectException {

        if (proDelegate != null) {
            return proDelegate.toStub(obj);
        }
        return null;
    }

    /**
     * Deregisters a server object from the runtime, allowing the object to become
     * available for garbage collection.
     * @param obj the object to unexport.
     * @exception NoSuchObjectException if the remote object is not
     * currently exported.
     */
    public static void unexportObject(Remote obj)
        throws NoSuchObjectException {

        if (proDelegate != null) {
            proDelegate.unexportObject(obj);
        }

    }

    /**
     * Checks to ensure that an object of a remote or abstract interface type
     * can be cast to a desired type.
     * @param narrowFrom the object to check.
     * @param narrowTo the desired type.
     * @return an object which can be cast to the desired type.
     * @throws ClassCastException if narrowFrom cannot be cast to narrowTo.
     */
    public static Object narrow ( Object narrowFrom,
                                            Class narrowTo)
        throws ClassCastException {

        if (proDelegate != null) {
            return proDelegate.narrow(narrowFrom, narrowTo);
        }
        return null;

    }

    /**
     * Makes a Remote object ready for remote communication. This normally
     * happens implicitly when the object is sent or received as an argument
     * on a remote method call, but in some circumstances it is useful to
     * perform this action by making an explicit call.  See the
     * {@link javax.rmi.CORBA.Stub#connect} method for more information.
     * @param target the object to connect.
     * @param source a previously connected object.
     * @throws RemoteException if <code>source</code> is not connected
     * or if <code>target</code> is already connected to a different ORB than
     * <code>source</code>.
     */
    public static void connect (Remote target, Remote source)
        throws RemoteException {

        if (proDelegate != null) {
            proDelegate.connect(target, source);
        }

    }

    // Same code as in javax.rmi.CORBA.Util. Can not be shared because they
    // are in different packages and the visibility needs to be package for
    // security reasons. If you know a better solution how to share this code
    // then remove it from here.
    private static Object createDelegate(String classKey) {
        String className = (String)
            AccessController.doPrivileged(new GetPropertyAction(classKey));
        if (className == null) {
            Properties props = getORBPropertiesFile();
            if (props != null) {
                className = props.getProperty(classKey);
            }
        }
        if (className == null) {
            return new com.sun.corba.se.impl.javax.rmi.PortableRemoteObject();
        }

        try {
            return (Object) loadDelegateClass(className).newInstance();
        } catch (ClassNotFoundException ex) {
            INITIALIZE exc = new INITIALIZE( "Cannot instantiate " + className);
            exc.initCause( ex ) ;
            throw exc ;
        } catch (Exception ex) {
            INITIALIZE exc = new INITIALIZE( "Error while instantiating" + className);
            exc.initCause( ex ) ;
            throw exc ;
        }

    }

    private static Class loadDelegateClass( String className )  throws ClassNotFoundException
    {
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            return Class.forName(className, false, loader);
        } catch (ClassNotFoundException e) {
            // ignore, then try RMIClassLoader
        }

        try {
            return RMIClassLoader.loadClass(className);
        } catch (MalformedURLException e) {
            String msg = "Could not load " + className + ": " + e.toString();
            ClassNotFoundException exc = new ClassNotFoundException( msg ) ;
            throw exc ;
        }
    }

    /**
     * Load the orb.properties file.
     */
    private static Properties getORBPropertiesFile () {
        return (Properties) AccessController.doPrivileged(new javax.rmi.GetORBPropertiesFileAction());
    }
}

class GetORBPropertiesFileAction implements PrivilegedAction {
    private boolean debug = false ;

    public GetORBPropertiesFileAction () {
    }

    private String getSystemProperty(final String name) {
        // This will not throw a SecurityException because this
        // class was loaded from rt.jar using the bootstrap classloader.
        String propValue = (String) AccessController.doPrivileged(
            new PrivilegedAction() {
                public Object run() {
                    return System.getProperty(name);
                }
            }
        );

        return propValue;
    }

    private void getPropertiesFromFile( Properties props, String fileName )
    {
        try {
            File file = new File( fileName ) ;
            if (!file.exists())
                return ;

            FileInputStream in = new FileInputStream( file ) ;

            try {
                props.load( in ) ;
            } finally {
                in.close() ;
            }
        } catch (Exception exc) {
            if (debug)
                System.out.println( "ORB properties file " + fileName +
                    " not found: " + exc) ;
        }
    }

    public Object run()
    {
        Properties defaults = new Properties() ;

        String javaHome = getSystemProperty( "java.home" ) ;
        String fileName = javaHome + File.separator + "lib" + File.separator +
            "orb.properties" ;

        getPropertiesFromFile( defaults, fileName ) ;

        Properties results = new Properties( defaults ) ;

        String userHome = getSystemProperty( "user.home" ) ;
        fileName = userHome + File.separator + "orb.properties" ;

        getPropertiesFromFile( results, fileName ) ;
        return results ;
    }
}
