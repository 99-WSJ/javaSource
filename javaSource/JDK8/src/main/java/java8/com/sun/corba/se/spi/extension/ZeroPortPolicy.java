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

package java8.sun.corba.se.spi.extension;

import com.sun.corba.se.impl.orbutil.ORBConstants;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.Policy;

/** Policy used to implement zero IIOP port policy in the POA.
*/
public class ZeroPortPolicy extends LocalObject implements Policy
{
    private static com.sun.corba.se.spi.extension.ZeroPortPolicy policy = new com.sun.corba.se.spi.extension.ZeroPortPolicy( true ) ;

    private boolean flag = true ;

    private ZeroPortPolicy( boolean type )
    {
        this.flag = type ;
    }

    public String toString()
    {
        return "ZeroPortPolicy[" + flag + "]" ;
    }

    public boolean forceZeroPort()
    {
        return flag ;
    }

    public synchronized static com.sun.corba.se.spi.extension.ZeroPortPolicy getPolicy()
    {
        return policy ;
    }

    public int policy_type ()
    {
        return ORBConstants.ZERO_PORT_POLICY ;
    }

    public Policy copy ()
    {
        return this ;
    }

    public void destroy ()
    {
        // NO-OP
    }
}
