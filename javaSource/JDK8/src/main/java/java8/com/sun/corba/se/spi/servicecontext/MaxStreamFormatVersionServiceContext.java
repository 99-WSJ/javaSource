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

/**
 */
package java8.sun.corba.se.spi.servicecontext;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.servicecontext.ServiceContext;
import org.omg.CORBA.SystemException;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;
import org.omg.IOP.RMICustomMaxStreamFormat;

public class MaxStreamFormatVersionServiceContext extends ServiceContext {

    private byte maxStreamFormatVersion;

    // The singleton uses the maximum version indicated by our
    // ValueHandler.
    public static final com.sun.corba.se.spi.servicecontext.MaxStreamFormatVersionServiceContext singleton
        = new com.sun.corba.se.spi.servicecontext.MaxStreamFormatVersionServiceContext();

    public MaxStreamFormatVersionServiceContext() {
        maxStreamFormatVersion = ORBUtility.getMaxStreamFormatVersion();
    }

    public MaxStreamFormatVersionServiceContext(byte maxStreamFormatVersion) {
        this.maxStreamFormatVersion = maxStreamFormatVersion;
    }

    public MaxStreamFormatVersionServiceContext(InputStream is,
                                                GIOPVersion gv) {
        super(is, gv) ;

        maxStreamFormatVersion = is.read_octet();
    }

    public static final int SERVICE_CONTEXT_ID = RMICustomMaxStreamFormat.value;
    public int getId() { return SERVICE_CONTEXT_ID; }

    public void writeData(OutputStream os) throws SystemException
    {
        os.write_octet(maxStreamFormatVersion);
    }

    public byte getMaximumStreamFormatVersion()
    {
        return maxStreamFormatVersion;
    }

    public String toString()
    {
        return "MaxStreamFormatVersionServiceContext["
            + maxStreamFormatVersion + "]";
    }
}
