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

package java8.sun.corba.se.spi.encoding;

import com.sun.corba.se.impl.encoding.BufferManagerWrite;
import com.sun.corba.se.impl.encoding.CDROutputStream;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.transport.CorbaConnection;


public abstract class CorbaOutputObject
    extends CDROutputStream
    implements OutputObject
{
    public CorbaOutputObject(
        ORB orb, GIOPVersion version, byte encodingVersion,
        boolean littleEndian, BufferManagerWrite bufferManager,
        byte streamFormatVersion, boolean usePooledByteBuffers)
    {
        super(orb, version, encodingVersion, littleEndian, bufferManager,
              streamFormatVersion, usePooledByteBuffers);
    }

    public abstract void writeTo(CorbaConnection connection)
        throws java.io.IOException;
}
