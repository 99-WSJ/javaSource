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
 * InputStream provides for the reading of all of the mapped IDL types
 * from the stream. It extends org.omg.CORBA.portable.InputStream.  This
 * class defines new methods that were added for CORBA 2.3.
 *
 * @see org.omg.CORBA.portable.InputStream
 * @author  OMG
 * @since   JDK1.2
 */

public abstract class InputStream extends org.omg.CORBA.portable.InputStream {


    private static final String ALLOW_SUBCLASS_PROP = "jdk.corba.allowInputStreamSubclass";

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

    private InputStream(Void ignore) { }

    /**
     * Create a new instance of this class.
     *
     * throw SecurityException if SecurityManager is installed and
     * enableSubclassImplementation SerializablePermission
     * is not granted or jdk.corba.allowOutputStreamSubclass system
     * property is either not set or is set to 'false'
     */
    public InputStream() {
        this(checkPermission());
    }

    /**
     * Unmarshalls a value type from the input stream.
     * @return the value type unmarshalled from the input stream
     */
    public java.io.Serializable read_value() {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    /**
     * Unmarshalls a value type from the input stream.
     * @param clz is the declared type of the value to be unmarshalled
     * @return the value unmarshalled from the input stream
     */
    public java.io.Serializable read_value(Class clz) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    /**
     * Unmarshalls a value type from the input stream.
     * @param factory is the instance fo the helper to be used for
     * unmarshalling the value type
     * @return the value unmarshalled from the input stream
     */
    public java.io.Serializable read_value(org.omg.CORBA.portable.BoxedValueHelper factory) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    /**
     * Unmarshalls a value type from the input stream.
     * @param rep_id identifies the type of the value to be unmarshalled
     * @return value type unmarshalled from the input stream
     */
    public java.io.Serializable read_value(String rep_id) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    /**
     * Unmarshalls a value type from the input stream.
     * @param value is an uninitialized value which is added to the orb's
     * indirection table before calling Streamable._read() or
     * CustomMarshal.unmarshal() to unmarshal the value.
     * @return value type unmarshalled from the input stream
     */
    public java.io.Serializable read_value(java.io.Serializable value) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    /**
     * Unmarshal the value object or a suitable stub object.
     * @return ORB runtime returns the value object or a suitable stub object.
     */
    public Object read_abstract_interface() {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    /**
     * Unmarshal the class object or the stub class corresponding to the passed type.
     * @param clz is the Class object for the stub class which corresponds to
     * the type that is statically expected.
     * @return ORB runtime returns the value object or a suitable stub object.
     */
    public Object read_abstract_interface(Class clz) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

}
