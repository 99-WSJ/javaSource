/*
 * Copyright (c) 2002, 2003, Oracle and/or its affiliates. All rights reserved.
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


package java8.sun.corba.se.impl.protocol;

import com.sun.corba.se.impl.protocol.ServantCacheLocalCRDBase;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.portable.ServantObject;

public class InfoOnlyServantCacheLocalCRDImpl extends ServantCacheLocalCRDBase
{
    public InfoOnlyServantCacheLocalCRDImpl( ORB orb, int scid, IOR ior )
    {
        super( (ORB)orb, scid, ior ) ;
    }

    public ServantObject servant_preinvoke( org.omg.CORBA.Object self,
        String operation, Class expectedType )
    {
        OAInvocationInfo cachedInfo = getCachedInfo() ;
        if (!checkForCompatibleServant( cachedInfo, expectedType ))
            return null ;

        // Note that info is shared across multiple threads
        // using the same subcontract, each of which may
        // have its own operation.  Therefore we need to copy it.
        OAInvocationInfo info =  new OAInvocationInfo(cachedInfo, operation) ;
        orb.pushInvocationInfo( info ) ;

        return info ;
    }

    public void servant_postinvoke(org.omg.CORBA.Object self,
                                   ServantObject servantobj)
    {
        orb.popInvocationInfo() ;
    }
}
