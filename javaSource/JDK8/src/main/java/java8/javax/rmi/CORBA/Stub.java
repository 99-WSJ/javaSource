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

package java8.javax.rmi.CORBA;

import org.omg.CORBA.ORB;
import org.omg.CORBA.INITIALIZE;
import org.omg.CORBA_2_3.portable.ObjectImpl;

import java.io.IOException;
import java.rmi.RemoteException;
import java.io.File;
import java.io.FileInputStream;
import java.net.MalformedURLException ;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;
import java.rmi.server.RMIClassLoader;

import com.sun.corba.se.impl.orbutil.GetPropertyAction;

import javax.rmi.CORBA.GetORBPropertiesFileAction;
import javax.rmi.CORBA.StubDelegate;


/**
 * Base class from which all RMI-IIOP stubs must inherit.
 */
public abstract class Stub extends ObjectImpl
    implements java.io.Serializable {

    private static final long serialVersionUID = 1087775603798577179L;

    // This can only be set at object construction time (no sync necessary).
    private transient StubDelegate stubDelegate = null;
    private static Class stubDelegateClass = null;
    private static final String StubClassKey = "javax.rmi.CORBA.StubClass";

    static {
        Object stubDelegateInstance = createDelegate(StubClassKey);
        if (stubDelegateInstance != null)
            stubDelegateClass = stubDelegateInstance.getClass();
    }


    /**
     * Returns a hash code value for the object which is the same for all stubs
     * that represent the same remote object.
     * @return the hash code value.
     */
    public int hashCode() {

        if (stubDelegate == null) {
            setDefaultDelegate();
        }

        if (stubDelegate != null) {
            return stubDelegate.hashCode(this);
        }

        return 0;
    }

    /**
     * Compares two stubs for equality. Returns <code>true</code> when used to compare stubs
     * that represent the same remote object, and <code>false</code> otherwise.
     * @param obj the reference object with which to compare.
     * @return <code>true</code> if this object is the same as the <code>obj</code>
     *          argument; <code>false</code> otherwise.
     */
    public boolean equals(Object obj) {

        if (stubDelegate == null) {
            setDefaultDelegate();
        }

        if (stubDelegate != null) {
            return stubDelegate.equals(this, obj);
        }

        return false;
    }

    /**
     * Returns a string representation of this stub. Returns the same string
     * for all stubs that represent the same remote object.
     * @return a string representation of this stub.
     */
    public String toString() {


        if (stubDelegate == null) {
            setDefaultDelegate();
        }

        String ior;
        if (stubDelegate != null) {
            ior = stubDelegate.toString(this);
            if (ior == null) {
                return super.toString();
            } else {
                return ior;
            }
        }
        return super.toString();
    }

    /**
     * Connects this stub to an ORB. Required after the stub is deserialized
     * but not after it is demarshalled by an ORB stream. If an unconnected
     * stub is passed to an ORB stream for marshalling, it is implicitly
     * connected to that ORB. Application code should not call this method
     * directly, but should call the portable wrapper method
     * {@link javax.rmi.PortableRemoteObject#connect}.
     * @param orb the ORB to connect to.
     * @exception RemoteException if the stub is already connected to a different
     * ORB, or if the stub does not represent an exported remote or local object.
     */
    public void connect(ORB orb) throws RemoteException {

        if (stubDelegate == null) {
            setDefaultDelegate();
        }

        if (stubDelegate != null) {
            stubDelegate.connect(this, orb);
        }

    }

    /**
     * Serialization method to restore the IOR state.
     */
    private void readObject(java.io.ObjectInputStream stream)
        throws IOException, ClassNotFoundException {

        if (stubDelegate == null) {
            setDefaultDelegate();
        }

        if (stubDelegate != null) {
            stubDelegate.readObject(this, stream);
        }

    }

    /**
     * Serialization method to save the IOR state.
     * @serialData The length of the IOR type ID (int), followed by the IOR type ID
     * (byte array encoded using ISO8859-1), followed by the number of IOR profiles
     * (int), followed by the IOR profiles.  Each IOR profile is written as a
     * profile tag (int), followed by the length of the profile data (int), followed
     * by the profile data (byte array).
     */
    private void writeObject(java.io.ObjectOutputStream stream) throws IOException {

        if (stubDelegate == null) {
            setDefaultDelegate();
        }

        if (stubDelegate != null) {
            stubDelegate.writeObject(this, stream);
        }
    }

    private void setDefaultDelegate() {
        if (stubDelegateClass != null) {
            try {
                 stubDelegate = (StubDelegate) stubDelegateClass.newInstance();
            } catch (Exception ex) {
            // what kind of exception to throw
            // delegate not set therefore it is null and will return default
            // values
            }
        }
    }

    // Same code as in PortableRemoteObject. Can not be shared because they
    // are in different packages and the visibility needs to be package for
    // security reasons. If you know a better solution how to share this code
    // then remove it from PortableRemoteObject. Also in Util.java
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
            return new com.sun.corba.se.impl.javax.rmi.CORBA.StubDelegateImpl();
        }

        try {
            return loadDelegateClass(className).newInstance();
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
        return (Properties) AccessController.doPrivileged(new javax.rmi.CORBA.GetORBPropertiesFileAction());
    }

}
