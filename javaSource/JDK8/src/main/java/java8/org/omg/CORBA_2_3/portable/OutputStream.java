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

package java8.org.omg.CORBA_2_3.portable;

import java.io.SerializablePermission;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * OutputStream provides interface for writing of all of the mapped IDL type
 * to the stream. It extends org.omg.CORBA.portable.OutputStream, and defines
 * new methods defined by CORBA 2.3.
 *
 * @see org.omg.CORBA.portable.OutputStream
 * @author  OMG
 * @since   JDK1.2
 */

public abstract class OutputStream extends org.omg.CORBA.portable.OutputStream {

    private static final String ALLOW_SUBCLASS_PROP = "jdk.corba.allowOutputStreamSubclass";
    private static final boolean allowSubclass = AccessController.doPrivileged(
        new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
            String prop = System.getProperty(ALLOW_SUBCLASS_PROP);
                return prop == null ? false :
                           (prop.equalsIgnoreCase("false") ? false : true);
            }
        });

    private static Void checkPermission() {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            if (!allowSubclass)
                sm.checkPermission(new
                    SerializablePermission("enableSubclassImplementation"));
        }
        return null;
    }
    private OutputStream(Void ignore) { }

    /**
     * Create a new instance of this class.
     *
     * throw SecurityException if SecurityManager is installed and
     * enableSubclassImplementation SerializablePermission
     * is not granted or jdk.corba.allowOutputStreamSubclass system
     * property is either not set or is set to 'false'
     */
    public OutputStream() {
        this(checkPermission());
    }

    /**
     * Marshals a value type to the output stream.
     * @param value is the acutal value to write
     */
    public void write_value(java.io.Serializable value) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    /**
     * Marshals a value type to the output stream.
     * @param value is the acutal value to write
     * @param clz is the declared type of the value to be marshaled
     */
    public void write_value(java.io.Serializable value, Class clz) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    /**
     * Marshals a value type to the output stream.
     * @param value is the acutal value to write
     * @param repository_id identifies the type of the value type to
     * be marshaled
     */
    public void write_value(java.io.Serializable value, String repository_id) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    /**
     * Marshals a value type to the output stream.
     * @param value is the acutal value to write
     * @param factory is the instance of the helper to be used for marshaling
     * the boxed value
     */
    public void write_value(java.io.Serializable value, org.omg.CORBA.portable.BoxedValueHelper factory) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    /**
     * Marshals a value object or a stub object.
     * @param obj the actual value object to marshal or the stub to be marshalled
     */
    public void write_abstract_interface(Object obj) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

}
