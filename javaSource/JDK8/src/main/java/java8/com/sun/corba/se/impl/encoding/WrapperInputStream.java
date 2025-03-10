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

package java8.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.corba.TypeCodeImpl;
import com.sun.corba.se.impl.encoding.CDRInputStream;
import com.sun.corba.se.impl.encoding.TypeCodeReader;
import org.omg.CORBA.Any;
import org.omg.CORBA.Principal;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA_2_3.portable.InputStream;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

public class WrapperInputStream extends InputStream implements TypeCodeReader
{
    private CDRInputStream stream;
    private Map typeMap = null;
    private int startPos = 0;

    public WrapperInputStream(CDRInputStream s) {
        super();
        stream = s;
        startPos = stream.getPosition();
    }

    public int read() throws IOException { return stream.read(); }
    public int read(byte b[]) throws IOException { return stream.read(b); }
    public int read(byte b[], int off, int len) throws IOException {
        return stream.read(b, off, len);
    }
    public long skip(long n) throws IOException { return stream.skip(n); }
    public int available() throws IOException { return stream.available(); }
    public void close() throws IOException { stream.close(); }
    public void mark(int readlimit) { stream.mark(readlimit); }
    public void reset() { stream.reset(); }
    public boolean markSupported() { return stream.markSupported(); }
    public int getPosition() { return stream.getPosition(); }
    public void consumeEndian() { stream.consumeEndian(); }
    public boolean read_boolean() { return stream.read_boolean(); }
    public char read_char() { return stream.read_char(); }
    public char read_wchar() { return stream.read_wchar(); }
    public byte read_octet() { return stream.read_octet(); }
    public short read_short() { return stream.read_short(); }
    public short read_ushort() { return stream.read_ushort(); }
    public int read_long() { return stream.read_long(); }
    public int read_ulong() { return stream.read_ulong(); }
    public long read_longlong() { return stream.read_longlong(); }
    public long read_ulonglong() { return stream.read_ulonglong(); }
    public float read_float() { return stream.read_float(); }
    public double read_double() { return stream.read_double(); }
    public String read_string() { return stream.read_string(); }
    public String read_wstring() { return stream.read_wstring(); }

    public void read_boolean_array(boolean[] value, int offset, int length) {
        stream.read_boolean_array(value, offset, length);
    }
    public void read_char_array(char[] value, int offset, int length) {
        stream.read_char_array(value, offset, length);
    }
    public void read_wchar_array(char[] value, int offset, int length) {
        stream.read_wchar_array(value, offset, length);
    }
    public void read_octet_array(byte[] value, int offset, int length) {
        stream.read_octet_array(value, offset, length);
    }
    public void read_short_array(short[] value, int offset, int length) {
        stream.read_short_array(value, offset, length);
    }
    public void read_ushort_array(short[] value, int offset, int length) {
        stream.read_ushort_array(value, offset, length);
    }
    public void read_long_array(int[] value, int offset, int length) {
        stream.read_long_array(value, offset, length);
    }
    public void read_ulong_array(int[] value, int offset, int length) {
        stream.read_ulong_array(value, offset, length);
    }
    public void read_longlong_array(long[] value, int offset, int length) {
        stream.read_longlong_array(value, offset, length);
    }
    public void read_ulonglong_array(long[] value, int offset, int length) {
        stream.read_ulonglong_array(value, offset, length);
    }
    public void read_float_array(float[] value, int offset, int length) {
        stream.read_float_array(value, offset, length);
    }
    public void read_double_array(double[] value, int offset, int length) {
        stream.read_double_array(value, offset, length);
    }

    public org.omg.CORBA.Object read_Object() { return stream.read_Object(); }
    public java.io.Serializable read_value() {return stream.read_value();}
    public TypeCode read_TypeCode() { return stream.read_TypeCode(); }
    public Any read_any() { return stream.read_any(); }
    public Principal read_Principal() { return stream.read_Principal(); }
    public BigDecimal read_fixed() { return stream.read_fixed(); }
    public org.omg.CORBA.Context read_Context() { return stream.read_Context(); }

    public org.omg.CORBA.ORB orb() { return stream.orb(); }

    public void addTypeCodeAtPosition(TypeCodeImpl tc, int position) {
        if (typeMap == null) {
            //if (TypeCodeImpl.debug) System.out.println("Creating typeMap");
            typeMap = new HashMap(16);
        }
        //if (TypeCodeImpl.debug) System.out.println(this + " adding tc " + tc + " at position " + position);
        typeMap.put(new Integer(position), tc);
    }

    public TypeCodeImpl getTypeCodeAtPosition(int position) {
        if (typeMap == null)
            return null;
        //if (TypeCodeImpl.debug) System.out.println("Getting tc " + (TypeCodeImpl)typeMap.get(new Integer(position)) +
            //" at position " + position);
        return (TypeCodeImpl)typeMap.get(new Integer(position));
    }

    public void setEnclosingInputStream(InputStream enclosure) {
        // WrapperInputStream has no enclosure
    }

    public TypeCodeReader getTopLevelStream() {
        // WrapperInputStream has no enclosure
        return this;
    }

    public int getTopLevelPosition() {
        //if (TypeCodeImpl.debug) System.out.println("WrapperInputStream.getTopLevelPosition " +
            //"returning getPosition " + getPosition() + " - startPos " + startPos +
            //" = " + (getPosition() - startPos));
        return getPosition() - startPos;
    }

    public void performORBVersionSpecificInit() {
        // This is never actually called on a WrapperInputStream, but
        // exists to satisfy the interface requirement.
        stream.performORBVersionSpecificInit();
    }

    public void resetCodeSetConverters() {
        stream.resetCodeSetConverters();
    }

    //public void printBuffer() { stream.printBuffer(); }

    public void printTypeMap() {
        System.out.println("typeMap = {");
        List sortedKeys = new ArrayList(typeMap.keySet());
        Collections.sort(sortedKeys);
        Iterator i = sortedKeys.iterator();
        while (i.hasNext()) {
            Integer pos = (Integer)i.next();
            TypeCodeImpl tci = (TypeCodeImpl)typeMap.get(pos);
            System.out.println("  key = " + pos.intValue() + ", value = " + tci.description());
        }
        System.out.println("}");
    }
}
