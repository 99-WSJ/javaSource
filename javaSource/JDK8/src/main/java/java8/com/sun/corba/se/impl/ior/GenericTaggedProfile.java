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

package java8.com.sun.corba.se.impl.ior;

import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import com.sun.corba.se.impl.ior.GenericIdentifiable;
import com.sun.corba.se.spi.ior.*;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA_2_3.portable.InputStream;

/**
 * @author
 */
public class GenericTaggedProfile extends GenericIdentifiable implements TaggedProfile
{
    private ORB orb ;

    public GenericTaggedProfile( int id, InputStream is )
    {
        super( id, is ) ;
        this.orb = (ORB)(is.orb()) ;
    }

    public GenericTaggedProfile( ORB orb, int id, byte[] data )
    {
        super( id, data ) ;
        this.orb = orb ;
    }

    public TaggedProfileTemplate getTaggedProfileTemplate()
    {
        return null ;
    }

    public ObjectId getObjectId()
    {
        return null ;
    }

    public ObjectKeyTemplate getObjectKeyTemplate()
    {
        return null ;
    }

    public ObjectKey getObjectKey()
    {
        return null ;
    }

    public boolean isEquivalent( TaggedProfile prof )
    {
        return equals( prof ) ;
    }

    public void makeImmutable()
    {
        // NO-OP
    }

    public boolean isLocal()
    {
        return false ;
    }

    public org.omg.IOP.TaggedProfile getIOPProfile()
    {
        EncapsOutputStream os =
            sun.corba.OutputStreamFactory.newEncapsOutputStream(orb);
        write( os ) ;
        InputStream is = (InputStream)(os.create_input_stream()) ;
        return org.omg.IOP.TaggedProfileHelper.read( is ) ;
    }
}
