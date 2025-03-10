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

package java8.sun.corba.se.impl.protocol.giopmsgheaders;

import com.sun.corba.se.impl.protocol.giopmsgheaders.FragmentMessage;
import com.sun.corba.se.impl.protocol.giopmsgheaders.MessageHandler;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * This is the base interface for different message type interfaces.
 *
 * @author Ram Jeyaraman 05/14/2000
 */

public interface Message {

    // Generic constants

    int defaultBufferSize = 1024;
    int GIOPBigEndian = 0;
    int GIOPLittleEndian = 1;
    int GIOPBigMagic =    0x47494F50;
    int GIOPLittleMagic = 0x504F4947;
    int GIOPMessageHeaderLength = 12;

    // Other useful constants

    byte LITTLE_ENDIAN_BIT = 0x01;
    byte MORE_FRAGMENTS_BIT = 0x02;
    byte FLAG_NO_FRAG_BIG_ENDIAN = 0x00;
    static final byte TRAILING_TWO_BIT_BYTE_MASK = 0x3;
    static final byte THREAD_POOL_TO_USE_MASK = 0x3F;

    // Encoding related constants

    byte CDR_ENC_VERSION = 0x00;
    byte JAVA_ENC_VERSION = 0x01;

    // Message types

    byte GIOPRequest = 0;
    byte GIOPReply = 1;
    byte GIOPCancelRequest = 2;
    byte GIOPLocateRequest = 3;
    byte GIOPLocateReply = 4;
    byte GIOPCloseConnection = 5;
    byte GIOPMessageError = 6;
    byte GIOPFragment = 7; // 1.1 & 1.2:

    // Accessor methods

    GIOPVersion getGIOPVersion();
    byte getEncodingVersion();
    boolean isLittleEndian();
    boolean moreFragmentsToFollow();
    int getType();
    int getSize();
    ByteBuffer getByteBuffer();
    int getThreadPoolToUse();

    // Mutator methods

    void read(org.omg.CORBA.portable.InputStream istream);
    void write(org.omg.CORBA.portable.OutputStream ostream);

    void setSize(ByteBuffer byteBuffer, int size);

    FragmentMessage createFragmentMessage();

    void callback(MessageHandler handler) throws IOException;

    void setByteBuffer(ByteBuffer byteBuffer);
    void setEncodingVersion(byte version);
}
