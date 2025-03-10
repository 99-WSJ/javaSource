/*
 * Copyright (c) 1998, 2003, Oracle and/or its affiliates. All rights reserved.
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

package java8.sun.corba.se.spi.protocol;

import com.sun.corba.se.pept.protocol.ServerRequestDispatcher;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.ObjectKey;

/**
 * Server delegate adds behavior on the server-side -- specifically
 * on the dispatch path. A single server delegate instance serves
 * many server objects.  This is the second level of the dispatch
 * on the server side: Acceptor to ServerSubcontract to ServerRequestDispatcher to
 * ObjectAdapter to Servant, although this may be short-circuited.
 * Instances of this class are registered in the subcontract Registry.
 */
public interface CorbaServerRequestDispatcher
    extends ServerRequestDispatcher
{
    /**
     * Handle a locate request.
     */
    public IOR locate(ObjectKey key);
}

// End of file.
