/*
 * Copyright (c) 1996, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java8.java.util.zip;

import sun.nio.ch.DirectBuffer;

import java.nio.ByteBuffer;
import java.util.zip.Checksum;

/**
 * A class that can be used to compute the CRC-32 of a data stream.
 *
 * <p> Passing a {@code null} argument to a method in this class will cause
 * a {@link NullPointerException} to be thrown.
 *
 * @see         Checksum
 * @author      David Connelly
 */
public
class CRC32 implements Checksum {
    private int crc;

    /**
     * Creates a new CRC32 object.
     */
    public CRC32() {
    }


    /**
     * Updates the CRC-32 checksum with the specified byte (the low
     * eight bits of the argument b).
     *
     * @param b the byte to update the checksum with
     */
    public void update(int b) {
        crc = update(crc, b);
    }

    /**
     * Updates the CRC-32 checksum with the specified array of bytes.
     *
     * @throws  ArrayIndexOutOfBoundsException
     *          if {@code off} is negative, or {@code len} is negative,
     *          or {@code off+len} is greater than the length of the
     *          array {@code b}
     */
    public void update(byte[] b, int off, int len) {
        if (b == null) {
            throw new NullPointerException();
        }
        if (off < 0 || len < 0 || off > b.length - len) {
            throw new ArrayIndexOutOfBoundsException();
        }
        crc = updateBytes(crc, b, off, len);
    }

    /**
     * Updates the CRC-32 checksum with the specified array of bytes.
     *
     * @param b the array of bytes to update the checksum with
     */
    public void update(byte[] b) {
        crc = updateBytes(crc, b, 0, b.length);
    }

    /**
     * Updates the checksum with the bytes from the specified buffer.
     *
     * The checksum is updated using
     * buffer.{@link java.nio.Buffer#remaining() remaining()}
     * bytes starting at
     * buffer.{@link java.nio.Buffer#position() position()}
     * Upon return, the buffer's position will
     * be updated to its limit; its limit will not have been changed.
     *
     * @param buffer the ByteBuffer to update the checksum with
     * @since 1.8
     */
    public void update(ByteBuffer buffer) {
        int pos = buffer.position();
        int limit = buffer.limit();
        assert (pos <= limit);
        int rem = limit - pos;
        if (rem <= 0)
            return;
        if (buffer instanceof DirectBuffer) {
            crc = updateByteBuffer(crc, ((DirectBuffer)buffer).address(), pos, rem);
        } else if (buffer.hasArray()) {
            crc = updateBytes(crc, buffer.array(), pos + buffer.arrayOffset(), rem);
        } else {
            byte[] b = new byte[rem];
            buffer.get(b);
            crc = updateBytes(crc, b, 0, b.length);
        }
        buffer.position(limit);
    }

    /**
     * Resets CRC-32 to initial value.
     */
    public void reset() {
        crc = 0;
    }

    /**
     * Returns CRC-32 value.
     */
    public long getValue() {
        return (long)crc & 0xffffffffL;
    }

    private native static int update(int crc, int b);
    private native static int updateBytes(int crc, byte[] b, int off, int len);

    private native static int updateByteBuffer(int adler, long addr,
                                               int off, int len);
}
