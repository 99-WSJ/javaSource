/*
 * Copyright (c) 2001, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java8.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.encoding.BufferManagerFactory;
import com.sun.corba.se.impl.encoding.CDRInputStream;
import com.sun.corba.se.impl.encoding.CodeSetConversion;
import com.sun.corba.se.impl.encoding.OSFCodeSetRegistry;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.logging.CORBALogDomains;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.org.omg.SendingContext.CodeBase;
import org.omg.CORBA.CompletionStatus;
import sun.corba.EncapsInputStreamFactory;

import java.nio.ByteBuffer;

/**
 * Encapsulations are supposed to explicitly define their
 * code sets and GIOP version.  The original resolution to issue 2784
 * said that the defaults were UTF-8 and UTF-16, but that was not
 * agreed upon.
 *
 * These streams currently use CDR 1.2 with ISO8859-1 for char/string and
 * UTF16 for wchar/wstring.  If no byte order marker is available,
 * the endianness of the encapsulation is used.
 *
 * When more encapsulations arise that have their own special code
 * sets defined, we can make all constructors take such parameters.
 */
public class EncapsInputStream extends CDRInputStream
{
    private ORBUtilSystemException wrapper ;

    // corba/EncapsOutputStream
    // corba/ORBSingleton
    // iiop/ORB
    public EncapsInputStream(org.omg.CORBA.ORB orb, byte[] buf,
                             int size, boolean littleEndian,
                             GIOPVersion version) {
        super(orb, ByteBuffer.wrap(buf), size, littleEndian,
              version, Message.CDR_ENC_VERSION,
              BufferManagerFactory.newBufferManagerRead(
                                      BufferManagerFactory.GROW,
                                      Message.CDR_ENC_VERSION,
                                      (ORB)orb));

        wrapper = ORBUtilSystemException.get( (ORB)orb,
            CORBALogDomains.RPC_ENCODING ) ;

        performORBVersionSpecificInit();
    }

    public EncapsInputStream(org.omg.CORBA.ORB orb, ByteBuffer byteBuffer,
                             int size, boolean littleEndian,
                             GIOPVersion version) {
        super(orb, byteBuffer, size, littleEndian,
              version, Message.CDR_ENC_VERSION,
              BufferManagerFactory.newBufferManagerRead(
                                      BufferManagerFactory.GROW,
                                      Message.CDR_ENC_VERSION,
                                      (ORB)orb));

        performORBVersionSpecificInit();
    }

    // ior/IdentifiableBase
    // ior/IIOPProfile
    // corba/ORBSingleton
    // iiop/ORB
    public EncapsInputStream(org.omg.CORBA.ORB orb, byte[] data, int size)
    {
        this(orb, data, size, GIOPVersion.V1_2);
    }

    // corba/AnyImpl
    public EncapsInputStream(com.sun.corba.se.impl.encoding.EncapsInputStream eis)
    {
        super(eis);

        wrapper = ORBUtilSystemException.get( (ORB)(eis.orb()),
            CORBALogDomains.RPC_ENCODING ) ;

        performORBVersionSpecificInit();
    }

    // CDREncapsCodec
    // ServiceContext
    //
    // Assumes big endian (can use consumeEndian to read and set
    // the endianness if it is an encapsulation with a byte order
    // mark at the beginning)
    public EncapsInputStream(org.omg.CORBA.ORB orb, byte[] data, int size, GIOPVersion version)
    {
        this(orb, data, size, false, version);
    }

    /**
     * Full constructor with a CodeBase parameter useful for
     * unmarshaling RMI-IIOP valuetypes (technically against the
     * intention of an encapsulation, but necessary due to OMG
     * issue 4795.  Used by ServiceContexts.
     */
    public EncapsInputStream(org.omg.CORBA.ORB orb,
                             byte[] data,
                             int size,
                             GIOPVersion version,
                             CodeBase codeBase) {
        super(orb,
              ByteBuffer.wrap(data),
              size,
              false,
              version, Message.CDR_ENC_VERSION,
              BufferManagerFactory.newBufferManagerRead(
                                      BufferManagerFactory.GROW,
                                      Message.CDR_ENC_VERSION,
                                      (ORB)orb));

        this.codeBase = codeBase;

        performORBVersionSpecificInit();
    }

    public CDRInputStream dup() {
        return EncapsInputStreamFactory.newEncapsInputStream(this);
    }

    protected CodeSetConversion.BTCConverter createCharBTCConverter() {
        return CodeSetConversion.impl().getBTCConverter(OSFCodeSetRegistry.ISO_8859_1);
    }

    protected CodeSetConversion.BTCConverter createWCharBTCConverter() {
        // Wide characters don't exist in GIOP 1.0
        if (getGIOPVersion().equals(GIOPVersion.V1_0))
            throw wrapper.wcharDataInGiop10( CompletionStatus.COMPLETED_MAYBE);

        // In GIOP 1.1, we shouldn't have byte order markers.  Take the order
        // of the stream if we don't see them.
        if (getGIOPVersion().equals(GIOPVersion.V1_1))
            return CodeSetConversion.impl().getBTCConverter(OSFCodeSetRegistry.UTF_16,
                                                            isLittleEndian());

        // Assume anything else adheres to GIOP 1.2 requirements.
        //
        // Our UTF_16 converter will work with byte order markers, and if
        // they aren't present, it will use the provided endianness.
        //
        // With no byte order marker, it's big endian in GIOP 1.2.
        // formal 00-11-03 15.3.16.
        return CodeSetConversion.impl().getBTCConverter(OSFCodeSetRegistry.UTF_16,
                                                        false);
    }

    public CodeBase getCodeBase() {
        return codeBase;
    }

    private CodeBase codeBase;
}
