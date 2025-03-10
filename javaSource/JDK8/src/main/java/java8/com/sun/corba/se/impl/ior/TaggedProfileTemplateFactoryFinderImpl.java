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

package java8.sun.corba.se.impl.ior;

import com.sun.corba.se.impl.ior.IdentifiableFactoryFinderBase;
import com.sun.corba.se.spi.ior.Identifiable;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA_2_3.portable.InputStream;

/**
 * @author
 */
public class TaggedProfileTemplateFactoryFinderImpl extends
    IdentifiableFactoryFinderBase
{
    public TaggedProfileTemplateFactoryFinderImpl( ORB orb )
    {
        super( orb ) ;
    }

    public Identifiable handleMissingFactory( int id, InputStream is)
    {
        throw wrapper.taggedProfileTemplateFactoryNotFound( new Integer(id) ) ;
    }
}
