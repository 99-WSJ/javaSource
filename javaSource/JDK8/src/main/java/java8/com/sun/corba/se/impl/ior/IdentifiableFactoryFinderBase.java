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

package java8.com.sun.corba.se.impl.ior;

import com.sun.corba.se.impl.logging.IORSystemException;
import com.sun.corba.se.spi.ior.Identifiable;
import com.sun.corba.se.spi.ior.IdentifiableFactory;
import com.sun.corba.se.spi.ior.IdentifiableFactoryFinder;
import com.sun.corba.se.spi.logging.CORBALogDomains;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA_2_3.portable.InputStream;

import java.util.HashMap;
import java.util.Map;

public abstract class IdentifiableFactoryFinderBase implements
    IdentifiableFactoryFinder
{
    private ORB orb ;
    private Map map ;
    protected IORSystemException wrapper ;

    protected IdentifiableFactoryFinderBase( ORB orb )
    {
        map = new HashMap() ;
        this.orb = orb ;
        wrapper = IORSystemException.get( orb,
            CORBALogDomains.OA_IOR ) ;
    }

    protected IdentifiableFactory getFactory(int id)
    {
        Integer ident = new Integer( id ) ;
        IdentifiableFactory factory = (IdentifiableFactory)(map.get(
            ident ) ) ;
        return factory ;
    }

    public abstract Identifiable handleMissingFactory( int id, InputStream is ) ;

    public Identifiable create(int id, InputStream is)
    {
        IdentifiableFactory factory = getFactory( id ) ;

        if (factory != null)
            return factory.create( is ) ;
        else
            return handleMissingFactory( id, is ) ;
    }

    public void registerFactory(IdentifiableFactory factory)
    {
        Integer ident = new Integer( factory.getId() ) ;
        map.put( ident, factory ) ;
    }
}
