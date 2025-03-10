/*
 * Copyright (c) 2000, 2002, Oracle and/or its affiliates. All rights reserved.
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

package java8.sun.corba.se.impl.orbutil;

import com.sun.corba.se.impl.io.TypeMismatchException;
import com.sun.corba.se.impl.orbutil.RepositoryIdInterface;

import java.io.Serializable;

/**
 * Factory methods for creating various repository ID strings
 * and instances.
 */
public interface RepositoryIdStrings
{
    String createForAnyType(Class type);

    String createForJavaType(Serializable ser)
        throws TypeMismatchException;

    String createForJavaType(Class clz)
        throws TypeMismatchException;

    String createSequenceRepID(Object ser);

    String createSequenceRepID(Class clazz);

    RepositoryIdInterface getFromString(String repIdString);

    String getClassDescValueRepId();
    String getWStringValueRepId();
}
