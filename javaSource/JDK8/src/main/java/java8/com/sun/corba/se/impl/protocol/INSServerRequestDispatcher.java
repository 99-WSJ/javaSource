/*
 * Copyright (c) 2001, 2003, Oracle and/or its affiliates. All rights reserved.
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
/*
 * Licensed Materials - Property of IBM
 * RMI-IIOP v1.0
 * Copyright IBM Corp. 1998 1999  All Rights Reserved
 *
 */

package java8.sun.corba.se.impl.protocol;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.logging.CORBALogDomains;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;

/**
 * INSServerRequestDispatcher handles all INS related discovery request. The INS Service
 * can be registered using ORB.register_initial_reference().
 * This Singleton subcontract just
 * finds the target IOR and does location forward.
 * XXX PI points are not invoked in either dispatch() or locate() method this
 * should be fixed in Tiger.
 */
public class INSServerRequestDispatcher
    implements CorbaServerRequestDispatcher
{

    private ORB orb = null;
    private ORBUtilSystemException wrapper ;

    public INSServerRequestDispatcher( ORB orb ) {
        this.orb = orb;
        this.wrapper = ORBUtilSystemException.get( orb,
            CORBALogDomains.RPC_PROTOCOL ) ;
    }

    // Need to signal one of OBJECT_HERE, OBJECT_FORWARD, OBJECT_NOT_EXIST.
    public IOR locate(ObjectKey okey) {
        // send a locate forward with the right IOR. If the insKey is not
        // registered then it will throw OBJECT_NOT_EXIST Exception
        String insKey = new String( okey.getBytes(orb) );
        return getINSReference( insKey );
    }

    public void dispatch(MessageMediator mediator)
    {
        CorbaMessageMediator request = (CorbaMessageMediator) mediator;
        // send a locate forward with the right IOR. If the insKey is not
        // registered then it will throw OBJECT_NOT_EXIST Exception
        String insKey = new String( request.getObjectKey().getBytes(orb) );
        request.getProtocolHandler()
            .createLocationForward(request, getINSReference( insKey ), null);
        return;
    }

    /**
     * getINSReference if it is registered in INSObjectKeyMap.
     */
    private IOR getINSReference( String insKey ) {
        IOR entry = ORBUtility.getIOR( orb.getLocalResolver().resolve( insKey ) ) ;
        if( entry != null ) {
            // If entry is not null then the locate is with an INS Object key,
            // so send a location forward with the right IOR.
            return entry;
        }

        throw wrapper.servantNotFound() ;
    }
}
