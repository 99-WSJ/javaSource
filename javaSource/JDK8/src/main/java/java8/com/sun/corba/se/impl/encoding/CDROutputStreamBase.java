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
package java8.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.encoding.BufferManagerWrite;
import com.sun.corba.se.impl.encoding.ByteBufferWithInfo;
import com.sun.corba.se.impl.encoding.CDROutputStream;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import org.omg.CORBA.Any;
import org.omg.CORBA.Principal;
import org.omg.CORBA.TypeCode;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.ByteBuffer;

/**
 * Describes CDROutputStream delegates and provides some
 * implementation.  Non-default constructors are avoided in
 * the delegation to separate instantiation from initialization,
 * so we use init methods.
 */
abstract class CDROutputStreamBase extends java.io.OutputStream
{
    protected CDROutputStream parent;

    // Required by parent CDROutputStream
    public void setParent(CDROutputStream parent) {
        this.parent = parent;
    }

    public void init(org.omg.CORBA.ORB orb,
                     BufferManagerWrite bufferManager,
                     byte streamFormatVersion) {
        init(orb, false, bufferManager, streamFormatVersion, true);
    }

    // See EncapsOutputStream, the only one that uses the
    // non-pooled ByteBuffers, for additional info.
    protected abstract void init(org.omg.CORBA.ORB orb,
                                 boolean littleEndian,
                                 BufferManagerWrite bufferManager,
                                 byte streamFormatVersion,
                                 boolean usePooledByteBuffers);

    public abstract void write_boolean(boolean value);
    public abstract void write_char(char value);
    public abstract void write_wchar(char value);
    public abstract void write_octet(byte value);
    public abstract void write_short(short value);
    public abstract void write_ushort(short value);
    public abstract void write_long(int value);
    public abstract void write_ulong(int value);
    public abstract void write_longlong(long value);
    public abstract void write_ulonglong(long value);
    public abstract void write_float(float value);
    public abstract void write_double(double value);
    public abstract void write_string(String value);
    public abstract void write_wstring(String value);
    public abstract void write_boolean_array(boolean[] value, int offset, int length);
    public abstract void write_char_array(char[] value, int offset, int length);
    public abstract void write_wchar_array(char[] value, int offset, int length);
    public abstract void write_octet_array(byte[] value, int offset, int length);
    public abstract void write_short_array(short[] value, int offset, int length);
    public abstract void write_ushort_array(short[] value, int offset, int length);
    public abstract void write_long_array(int[] value, int offset, int length);
    public abstract void write_ulong_array(int[] value, int offset, int length);
    public abstract void write_longlong_array(long[] value, int offset, int length);
    public abstract void write_ulonglong_array(long[] value, int offset, int length);
    public abstract void write_float_array(float[] value, int offset, int length);
    public abstract void write_double_array(double[] value, int offset, int length);
    public abstract void write_Object(org.omg.CORBA.Object value);
    public abstract void write_TypeCode(TypeCode value);
    public abstract void write_any(Any value);
    public abstract void write_Principal(Principal value);
    public void write(int b) throws IOException {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }
    public abstract void write_fixed(BigDecimal value);
    public void write_Context(org.omg.CORBA.Context ctx,
                              org.omg.CORBA.ContextList contexts) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public abstract org.omg.CORBA.ORB orb();

    // org.omg.CORBA_2_3.portable.OutputStream
    public abstract void write_value(Serializable value);
    public abstract void write_value(Serializable value, Class clz);
    public abstract void write_value(Serializable value, String repository_id);
    public abstract void write_value(Serializable value,
                                     org.omg.CORBA.portable.BoxedValueHelper factory);
    public abstract void write_abstract_interface(Object obj);

    // java.io.OutputStream
//     public abstract void write(byte b[]) throws IOException;
//     public abstract void write(byte b[], int off, int len) throws IOException;
//     public abstract void flush() throws IOException;
//     public abstract void close() throws IOException;

    // com.sun.corba.se.impl.encoding.MarshalOutputStream
    public abstract void start_block();
    public abstract void end_block();
    public abstract void putEndian();
    public abstract void writeTo(java.io.OutputStream s)
        throws IOException;
    public abstract byte[] toByteArray();

    // org.omg.CORBA.DataOutputStream
    public abstract void write_Abstract (Object value);
    public abstract void write_Value (Serializable value);
    public abstract void write_any_array(Any[] seq, int offset, int length);

    // org.omg.CORBA.portable.ValueBase
    public abstract String[] _truncatable_ids();

    // Needed by request and reply messages for GIOP versions >= 1.2 only.
    abstract void setHeaderPadding(boolean headerPadding);

    // Required by IIOPOutputStream and other subclasses
    public abstract int getSize();

    public abstract int getIndex();
    public abstract void setIndex(int value);

    public abstract ByteBuffer getByteBuffer();
    public abstract void setByteBuffer(ByteBuffer byteBuffer);

    public abstract boolean isLittleEndian();

    public abstract ByteBufferWithInfo getByteBufferWithInfo();
    public abstract void setByteBufferWithInfo(ByteBufferWithInfo bbwi);

    public abstract BufferManagerWrite getBufferManager();

    public abstract void write_fixed(BigDecimal bigDecimal, short digits, short scale);
    public abstract void writeOctetSequenceTo(org.omg.CORBA.portable.OutputStream s);

    public abstract GIOPVersion getGIOPVersion();

    public abstract void writeIndirection(int tag, int posIndirectedTo);

    abstract void freeInternalCaches();

    abstract void printBuffer();

    abstract void alignOnBoundary(int octetBoundary);

    // org.omg.CORBA.portable.ValueOutputStream

    public abstract void start_value(String rep_id);

    public abstract void end_value();
}
