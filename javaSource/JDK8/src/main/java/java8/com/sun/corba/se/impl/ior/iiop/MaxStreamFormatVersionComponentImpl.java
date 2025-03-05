/*
 * Copyright (c) 2002, 2004, Oracle and/or its affiliates. All rights reserved.
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

/**
 */
package java8.sun.corba.se.impl.ior.iiop;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.ior.TaggedComponentBase;
import com.sun.corba.se.spi.ior.iiop.MaxStreamFormatVersionComponent;
import org.omg.CORBA_2_3.portable.OutputStream;
import org.omg.IOP.TAG_RMI_CUSTOM_MAX_STREAM_FORMAT;

// Java to IDL ptc 02-01-12 1.4.11
// TAG_RMI_CUSTOM_MAX_STREAM_FORMAT
public class MaxStreamFormatVersionComponentImpl extends TaggedComponentBase
    implements MaxStreamFormatVersionComponent
{
    private byte version;

    public static final com.sun.corba.se.impl.ior.iiop.MaxStreamFormatVersionComponentImpl singleton
        = new com.sun.corba.se.impl.ior.iiop.MaxStreamFormatVersionComponentImpl();

    public boolean equals(Object obj)
    {
        if (!(obj instanceof com.sun.corba.se.impl.ior.iiop.MaxStreamFormatVersionComponentImpl))
            return false ;

        com.sun.corba.se.impl.ior.iiop.MaxStreamFormatVersionComponentImpl other =
            (com.sun.corba.se.impl.ior.iiop.MaxStreamFormatVersionComponentImpl)obj ;

        return version == other.version ;
    }

    public int hashCode()
    {
        return version ;
    }

    public String toString()
    {
        return "MaxStreamFormatVersionComponentImpl[version=" + version + "]" ;
    }

    public MaxStreamFormatVersionComponentImpl()
    {
        version = ORBUtility.getMaxStreamFormatVersion();
    }

    public MaxStreamFormatVersionComponentImpl(byte streamFormatVersion) {
        version = streamFormatVersion;
    }

    public byte getMaxStreamFormatVersion()
    {
        return version;
    }

    public void writeContents(OutputStream os)
    {
        os.write_octet(version);
    }

    public int getId()
    {
        return TAG_RMI_CUSTOM_MAX_STREAM_FORMAT.value;
    }
}
