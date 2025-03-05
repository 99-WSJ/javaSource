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

package java8.sun.corba.se.impl.orb;

import com.sun.corba.se.spi.orb.ORBVersion;
import org.omg.CORBA.portable.OutputStream;

public class ORBVersionImpl implements ORBVersion {
    private byte orbType ;

    public ORBVersionImpl( byte orbType )
    {
        this.orbType = orbType ;
    }

    public static final ORBVersion FOREIGN = new com.sun.corba.se.impl.orb.ORBVersionImpl(
        ORBVersion.FOREIGN ) ;

    public static final ORBVersion OLD = new com.sun.corba.se.impl.orb.ORBVersionImpl(
        ORBVersion.OLD ) ;

    public static final ORBVersion NEW = new com.sun.corba.se.impl.orb.ORBVersionImpl(
        ORBVersion.NEW ) ;

    public static final ORBVersion JDK1_3_1_01 = new com.sun.corba.se.impl.orb.ORBVersionImpl(
        ORBVersion.JDK1_3_1_01 ) ;

    public static final ORBVersion NEWER = new com.sun.corba.se.impl.orb.ORBVersionImpl(
        ORBVersion.NEWER ) ;

    public static final ORBVersion PEORB = new com.sun.corba.se.impl.orb.ORBVersionImpl(
        ORBVersion.PEORB ) ;

    public byte getORBType()
    {
        return orbType ;
    }

    public void write( OutputStream os )
    {
        os.write_octet( (byte)orbType ) ;
    }

    public String toString()
    {
        return "ORBVersionImpl[" + Byte.toString( orbType ) + "]" ;
    }

    public boolean equals( Object obj )
    {
        if (!(obj instanceof ORBVersion))
            return false ;

        ORBVersion version = (ORBVersion)obj ;
        return version.getORBType() == orbType ;
    }

    public int hashCode()
    {
        return orbType ;
    }

    public boolean lessThan(ORBVersion version) {
        return orbType < version.getORBType();
    }

    public int compareTo(Object obj) {
        // The Comparable interface says that this
        // method throws a ClassCastException if the
        // given object's type prevents it from being
        // compared.
        return getORBType() - ((ORBVersion)obj).getORBType();
    }
}
