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

import com.sun.corba.se.spi.ior.ObjectId;
import org.omg.CORBA_2_3.portable.OutputStream;

import java.util.Arrays;

/**
 * @author
 */
public final class ObjectIdImpl implements ObjectId
{
    private byte[] id;

    public boolean equals( Object obj )
    {
        if (!(obj instanceof com.sun.corba.se.impl.ior.ObjectIdImpl))
            return false ;

        com.sun.corba.se.impl.ior.ObjectIdImpl other = (com.sun.corba.se.impl.ior.ObjectIdImpl)obj ;

        return Arrays.equals( this.id, other.id ) ;
    }

    public int hashCode()
    {
        int result = 17 ;
        for (int ctr=0; ctr<id.length; ctr++)
            result = 37*result + id[ctr] ;
        return result ;
    }

    public ObjectIdImpl( byte[] id )
    {
        this.id = id ;
    }

    public byte[] getId()
    {
        return id ;
    }

    public void write( OutputStream os )
    {
        os.write_long( id.length ) ;
        os.write_octet_array( id, 0, id.length ) ;
    }
}
