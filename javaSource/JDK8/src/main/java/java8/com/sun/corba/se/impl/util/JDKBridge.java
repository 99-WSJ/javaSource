/*
 * Copyright (c) 1995, 2004, Oracle and/or its affiliates. All rights reserved.
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

package java8.sun.corba.se.impl.util;

import com.sun.corba.se.impl.orbutil.GetPropertyAction;

import java.net.MalformedURLException;
import java.rmi.server.RMIClassLoader;
import java.security.AccessController;

/**
 *  Utility methods for doing various method calls which are used
 *  by multiple classes
 */
public class JDKBridge {

    /**
     * Get local codebase System property (java.rmi.server.codebase).
     * May be null or a space separated array of URLS.
     */
    public static String getLocalCodebase () {
        return localCodebase;
    }

    /**
     * Return true if the system property "java.rmi.server.useCodebaseOnly"
     * is set, false otherwise.
     */
    public static boolean useCodebaseOnly () {
        return useCodebaseOnly;
    }

    /**
     * Returns a class instance for the specified class.
     * @param className the name of the class
     * @param remoteCodebase a space-separated array of urls at which
     * the class might be found. May be null.
     * @param loader a ClassLoader who may be used to
     * load the class if all other methods fail.
     * @return the <code>Class</code> object representing the loaded class.
     * @exception throws ClassNotFoundException if class cannot be loaded.
     */
    public static Class loadClass (String className,
                                   String remoteCodebase,
                                   ClassLoader loader)
        throws ClassNotFoundException {

        if (loader == null) {
            return loadClassM(className,remoteCodebase,useCodebaseOnly);
        } else {
            try {
                return loadClassM(className,remoteCodebase,useCodebaseOnly);
            } catch (ClassNotFoundException e) {
                return loader.loadClass(className);
            }
        }
    }

    /**
     * Returns a class instance for the specified class.
     * @param className the name of the class
     * @param remoteCodebase a space-separated array of urls at which
     * the class might be found. May be null.
     * @return the <code>Class</code> object representing the loaded class.
     * @exception throws ClassNotFoundException if class cannot be loaded.
     */
    public static Class loadClass (String className,
                                   String remoteCodebase)
        throws ClassNotFoundException {
        return loadClass(className,remoteCodebase,null);
    }

    /**
     * Returns a class instance for the specified class.
     * @param className the name of the class
     * @return the <code>Class</code> object representing the loaded class.
     * @exception throws ClassNotFoundException if class cannot be loaded.
     */
    public static Class loadClass (String className)
        throws ClassNotFoundException {
        return loadClass(className,null,null);
    }

    private static final String LOCAL_CODEBASE_KEY = "java.rmi.server.codebase";
    private static final String USE_CODEBASE_ONLY_KEY = "java.rmi.server.useCodebaseOnly";
    private static String localCodebase = null;
    private static boolean useCodebaseOnly;

    static {
        setCodebaseProperties();
    }

    public static final void main (String[] args) {
        System.out.println("1.2 VM");

        /*
                 // If on 1.2, use a policy with all permissions.
                 System.setSecurityManager (new javax.rmi.download.SecurityManager());
                 String targetClass = "[[Lrmic.Typedef;";
                 System.out.println("localCodebase =  "+localCodebase);
                 System.out.println("Trying to load "+targetClass);
                 try {
                 Class clz = loadClass(targetClass,null,localCodebase);
                 System.out.println("Loaded: "+clz);
                 } catch (ClassNotFoundException e) {
                 System.out.println("Caught "+e);
                 }
        */
    }

    /**
     * Set the codebase and useCodebaseOnly properties. This is public
     * only for test code.
     */
    public static synchronized void setCodebaseProperties () {
        String prop = (String)AccessController.doPrivileged(
            new GetPropertyAction(LOCAL_CODEBASE_KEY)
        );
        if (prop != null && prop.trim().length() > 0) {
            localCodebase = prop;
        }

        prop = (String)AccessController.doPrivileged(
            new GetPropertyAction(USE_CODEBASE_ONLY_KEY)
        );
        if (prop != null && prop.trim().length() > 0) {
            useCodebaseOnly = Boolean.valueOf(prop).booleanValue();
        }
    }

    /**
     * Set the default code base. This method is here only
     * for test code.
     */
    public static synchronized void setLocalCodebase(String codebase) {
        localCodebase = codebase;
    }

    private static Class loadClassM (String className,
                            String remoteCodebase,
                            boolean useCodebaseOnly)
        throws ClassNotFoundException {

        try {
            return JDKClassLoader.loadClass(null,className);
        } catch (ClassNotFoundException e) {}
        try {
            if (!useCodebaseOnly && remoteCodebase != null) {
                return RMIClassLoader.loadClass(remoteCodebase,
                                                className);
            } else {
                return RMIClassLoader.loadClass(className);
            }
        } catch (MalformedURLException e) {
            className = className + ": " + e.toString();
        }

        throw new ClassNotFoundException(className);
    }
}
