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

package java8.sun.corba.se.impl.legacy.connection;

import com.sun.corba.se.impl.legacy.connection.SocketFactoryContactInfoListIteratorImpl;
import com.sun.corba.se.impl.transport.CorbaContactInfoListImpl;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.orb.ORB;

import java.util.Iterator;

/**
 * @author Harold Carr
 */
public class SocketFactoryContactInfoListImpl
    extends
        CorbaContactInfoListImpl
{
    // XREVISIT - is this used?
    public SocketFactoryContactInfoListImpl(ORB orb)
    {
        super(orb);
    }

    public SocketFactoryContactInfoListImpl(ORB orb, IOR targetIOR)
    {
        super(orb, targetIOR);
    }

    ////////////////////////////////////////////////////
    //
    // pept.transport.ContactInfoList
    //

    public Iterator iterator()
    {
        return new SocketFactoryContactInfoListIteratorImpl(orb, this);
    }
}

// End of file.
