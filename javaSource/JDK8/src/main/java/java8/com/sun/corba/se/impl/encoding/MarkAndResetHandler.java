/*
 * Copyright (c) 2000, 2003, Oracle and/or its affiliates. All rights reserved.
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
package java8.sun.corba.se.impl.encoding;


import com.sun.corba.se.impl.encoding.ByteBufferWithInfo;

/**
 * Defines an abstraction for a RestorableInputStream to
 * implement mark/reset.
 */
interface MarkAndResetHandler
{
    void mark(com.sun.corba.se.impl.encoding.RestorableInputStream inputStream);

    void fragmentationOccured(ByteBufferWithInfo newFragment);

    void reset();
}
