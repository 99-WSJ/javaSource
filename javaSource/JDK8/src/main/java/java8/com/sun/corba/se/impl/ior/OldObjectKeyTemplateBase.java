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

import com.sun.corba.se.impl.ior.ObjectKeyFactoryImpl;
import com.sun.corba.se.impl.ior.ObjectKeyTemplateBase;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBVersionFactory;

/**
 * @author Ken Cavanaugh
 */
public abstract class OldObjectKeyTemplateBase extends ObjectKeyTemplateBase
{
    public OldObjectKeyTemplateBase( ORB orb, int magic, int scid, int serverid,
        String orbid, ObjectAdapterId oaid )
    {
        super( orb, magic, scid, serverid, orbid, oaid ) ;

        // set version based on magic
        if (magic == ObjectKeyFactoryImpl.JAVAMAGIC_OLD)
            setORBVersion( ORBVersionFactory.getOLD() ) ;
        else if (magic == ObjectKeyFactoryImpl.JAVAMAGIC_NEW)
            setORBVersion( ORBVersionFactory.getNEW() ) ;
        else // any other magic should not be here
            throw wrapper.badMagic( new Integer( magic ) ) ;
    }
}
