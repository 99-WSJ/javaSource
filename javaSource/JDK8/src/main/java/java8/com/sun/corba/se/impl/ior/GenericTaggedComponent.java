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

package java8.sun.corba.se.impl.ior;

import com.sun.corba.se.impl.ior.GenericIdentifiable;
import com.sun.corba.se.spi.ior.TaggedComponent;
import org.omg.CORBA.ORB;
import org.omg.CORBA_2_3.portable.InputStream;

/**
 * @author
 */
public class GenericTaggedComponent extends GenericIdentifiable
    implements TaggedComponent
{
    public GenericTaggedComponent( int id, InputStream is )
    {
        super( id, is ) ;
    }

    public GenericTaggedComponent( int id, byte[] data )
    {
        super( id, data ) ;
    }

    /**
     * @return org.omg.IOP.TaggedComponent
     * @exception
     * @author
     */
    public org.omg.IOP.TaggedComponent getIOPComponent( ORB orb )
    {
        return new org.omg.IOP.TaggedComponent( getId(),
            getData() ) ;
    }
}
