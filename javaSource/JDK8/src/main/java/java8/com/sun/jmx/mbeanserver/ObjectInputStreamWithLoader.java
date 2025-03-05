/*
 * Copyright (c) 1999, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java8.sun.jmx.mbeanserver;

// Java import

import sun.reflect.misc.ReflectUtil;

import java.io.*;

/**
 * This class deserializes an object in the context of a specific class loader.
 *
 * @since 1.5
 */
class ObjectInputStreamWithLoader extends ObjectInputStream {


    private ClassLoader loader;


    /**
     * @exception IOException Signals that an I/O exception of some
     * sort has occurred.
     * @exception StreamCorruptedException The object stream is corrupt.
     */
    public ObjectInputStreamWithLoader(InputStream in, ClassLoader theLoader)
            throws IOException {
        super(in);
        this.loader = theLoader;
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass aClass)
            throws IOException, ClassNotFoundException {
        if (loader == null) {
            return super.resolveClass(aClass);
        } else {
            String name = aClass.getName();
            ReflectUtil.checkPackageAccess(name);
            // Query the class loader ...
            return Class.forName(name, false, loader);
        }
    }
}
