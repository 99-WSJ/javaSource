/*
 * Copyright (c) 2000, 2004, Oracle and/or its affiliates. All rights reserved.
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

package java8.sun.corba.se.impl.ior.iiop;

import com.sun.corba.se.spi.ior.TaggedComponentBase;
import com.sun.corba.se.spi.ior.iiop.AlternateIIOPAddressComponent;
import com.sun.corba.se.spi.ior.iiop.IIOPAddress;
import org.omg.CORBA_2_3.portable.OutputStream;
import org.omg.IOP.TAG_ALTERNATE_IIOP_ADDRESS;

/**
 * @author Ken Cavanaugh
 */
public class AlternateIIOPAddressComponentImpl extends TaggedComponentBase
    implements AlternateIIOPAddressComponent
{
    private IIOPAddress addr ;

    public boolean equals( Object obj )
    {
        if (!(obj instanceof com.sun.corba.se.impl.ior.iiop.AlternateIIOPAddressComponentImpl))
            return false ;

        com.sun.corba.se.impl.ior.iiop.AlternateIIOPAddressComponentImpl other =
            (com.sun.corba.se.impl.ior.iiop.AlternateIIOPAddressComponentImpl)obj ;

        return addr.equals( other.addr ) ;
    }

    public int hashCode()
    {
        return addr.hashCode() ;
    }

    public String toString()
    {
        return "AlternateIIOPAddressComponentImpl[addr=" + addr + "]" ;
    }

    public AlternateIIOPAddressComponentImpl( IIOPAddress addr )
    {
        this.addr = addr ;
    }

    public IIOPAddress getAddress()
    {
        return addr ;
    }

    public void writeContents(OutputStream os)
    {
        addr.write( os ) ;
    }

    public int getId()
    {
        return TAG_ALTERNATE_IIOP_ADDRESS.value ; // 3 in CORBA 2.3.1 13.6.3
    }
}
