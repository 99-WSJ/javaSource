/*
 * Copyright (c) 2000, 2013, Oracle and/or its affiliates. All rights reserved.
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

import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import com.sun.corba.se.spi.ior.ObjectId;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;
import org.omg.CORBA_2_3.portable.OutputStream;

/**
 * @author
 */
public class ObjectKeyImpl implements ObjectKey
{
    private ObjectKeyTemplate oktemp;
    private ObjectId id;

    public boolean equals( Object obj )
    {
        if (obj == null)
            return false ;

        if (!(obj instanceof com.sun.corba.se.impl.ior.ObjectKeyImpl))
            return false ;

        com.sun.corba.se.impl.ior.ObjectKeyImpl other = (com.sun.corba.se.impl.ior.ObjectKeyImpl)obj ;

        return oktemp.equals( other.oktemp ) &&
            id.equals( other.id ) ;
    }

    public int hashCode()
    {
        return oktemp.hashCode() ^ id.hashCode() ;
    }

    public ObjectKeyTemplate getTemplate()
    {
        return oktemp ;
    }

    public ObjectId getId()
    {
        return id ;
    }

    public ObjectKeyImpl( ObjectKeyTemplate oktemp, ObjectId id )
    {
        this.oktemp = oktemp ;
        this.id = id ;
    }

    public void write( OutputStream os )
    {
        oktemp.write( id, os ) ;
    }

    public byte[] getBytes( org.omg.CORBA.ORB orb )
    {
        EncapsOutputStream os =
            sun.corba.OutputStreamFactory.newEncapsOutputStream((ORB)orb);
        write( os ) ;
        return os.toByteArray() ;
    }

    public CorbaServerRequestDispatcher getServerRequestDispatcher( ORB orb )
    {
        return oktemp.getServerRequestDispatcher( orb, id ) ;
    }
}
