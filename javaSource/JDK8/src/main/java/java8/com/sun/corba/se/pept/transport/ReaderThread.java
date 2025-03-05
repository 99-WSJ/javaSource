/*
 * Copyright (c) 2003, Oracle and/or its affiliates. All rights reserved.
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

package java8.sun.corba.se.pept.transport;

import com.sun.corba.se.pept.transport.Connection;

/**
 * @author Harold Carr
 */
public interface ReaderThread {
    public Connection getConnection();
    public void close();
}

// End of file.
