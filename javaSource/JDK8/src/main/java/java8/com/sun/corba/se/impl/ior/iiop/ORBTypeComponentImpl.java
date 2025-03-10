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
import com.sun.corba.se.spi.ior.iiop.ORBTypeComponent;
import org.omg.CORBA_2_3.portable.OutputStream;
import org.omg.IOP.TAG_ORB_TYPE;

/**
 * @author Ken Cavanaugh
 */
public class ORBTypeComponentImpl extends TaggedComponentBase
    implements ORBTypeComponent
{
    private int ORBType;

    public boolean equals( Object obj )
    {
        if (!(obj instanceof com.sun.corba.se.impl.ior.iiop.ORBTypeComponentImpl))
            return false ;

        com.sun.corba.se.impl.ior.iiop.ORBTypeComponentImpl other = (com.sun.corba.se.impl.ior.iiop.ORBTypeComponentImpl)obj ;

        return ORBType == other.ORBType ;
    }

    public int hashCode()
    {
        return ORBType ;
    }

    public String toString()
    {
        return "ORBTypeComponentImpl[ORBType=" + ORBType + "]" ;
    }

    public ORBTypeComponentImpl(int ORBType)
    {
        this.ORBType = ORBType ;
    }

    public int getId()
    {
        return TAG_ORB_TYPE.value ; // 0 in CORBA 2.3.1 13.6.3
    }

    public int getORBType()
    {
        return ORBType ;
    }

    public void writeContents(OutputStream os)
    {
        os.write_ulong( ORBType ) ;
    }
}
