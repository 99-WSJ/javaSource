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

import com.sun.corba.se.impl.ior.EncapsulationUtility;
import com.sun.corba.se.spi.ior.Identifiable;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

import java.util.Arrays;

/**
 * @author
 * This is used for unknown components and profiles.  A TAG_MULTICOMPONENT_PROFILE will be represented this way.
 */
public abstract class GenericIdentifiable implements Identifiable
{
    private int id;
    private byte data[];

    public GenericIdentifiable(int id, InputStream is)
    {
        this.id = id ;
        data = EncapsulationUtility.readOctets( is ) ;
    }

    public int getId()
    {
        return id ;
    }

    public void write(OutputStream os)
    {
        os.write_ulong( data.length ) ;
        os.write_octet_array( data, 0, data.length ) ;
    }

    public String toString()
    {
        return "GenericIdentifiable[id=" + getId() + "]" ;
    }

    public boolean equals(Object obj)
    {
        if (obj == null)
            return false ;

        if (!(obj instanceof com.sun.corba.se.impl.ior.GenericIdentifiable))
            return false ;

        com.sun.corba.se.impl.ior.GenericIdentifiable encaps = (com.sun.corba.se.impl.ior.GenericIdentifiable)obj ;

        return (getId() == encaps.getId()) &&
            Arrays.equals( getData(), encaps.getData() ) ;
    }

    public int hashCode()
    {
        int result = 17 ;
        for (int ctr=0; ctr<data.length; ctr++ )
            result = 37*result + data[ctr] ;
        return result ;
    }

    public GenericIdentifiable(int id, byte[] data)
    {
        this.id = id ;
        this.data = (byte[])(data.clone()) ;
    }

    public byte[] getData()
    {
        return data ;
    }
}
