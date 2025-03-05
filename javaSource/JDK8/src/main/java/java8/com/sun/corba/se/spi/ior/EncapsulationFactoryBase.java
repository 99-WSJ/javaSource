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

package java8.com.sun.corba.se.spi.ior;

import com.sun.corba.se.impl.ior.EncapsulationUtility;
import com.sun.corba.se.spi.ior.Identifiable;
import com.sun.corba.se.spi.ior.IdentifiableFactory;
import org.omg.CORBA_2_3.portable.InputStream;

public abstract class EncapsulationFactoryBase implements IdentifiableFactory {
    private int id ;

    public int getId()
    {
        return id ;
    }

    public EncapsulationFactoryBase( int id )
    {
        this.id = id ;
    }

    public final Identifiable create( InputStream in )
    {
        InputStream is = EncapsulationUtility.getEncapsulationStream( in ) ;
        return readContents( is ) ;
    }

    protected abstract Identifiable readContents( InputStream is ) ;
}
