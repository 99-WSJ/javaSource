/*
 * Copyright (c) 1999, 2001, Oracle and/or its affiliates. All rights reserved.
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

package java8.javax.imageio.stream;

/**
 * A class representing a mutable reference to an array of bytes and
 * an offset and length within that array.  <code>IIOByteBuffer</code>
 * is used by <code>ImageInputStream</code> to supply a sequence of bytes
 * to the caller, possibly with fewer copies than using the conventional
 * <code>read</code> methods that take a user-supplied byte array.
 *
 * <p> The byte array referenced by an <code>IIOByteBuffer</code> will
 * generally be part of an internal data structure belonging to an
 * <code>ImageReader</code> implementation; its contents should be
 * considered read-only and must not be modified.
 *
 */
public class IIOByteBuffer {

    private byte[] data;

    private int offset;

    private int length;

    /**
     * Constructs an <code>IIOByteBuffer</code> that references a
     * given byte array, offset, and length.
     *
     * @param data a byte array.
     * @param offset an int offset within the array.
     * @param length an int specifying the length of the data of
     * interest within byte array, in bytes.
     */
    public IIOByteBuffer(byte[] data, int offset, int length) {
        this.data = data;
        this.offset = offset;
        this.length = length;
    }

    /**
     * Returns a reference to the byte array.  The returned value should
     * be treated as read-only, and only the portion specified by the
     * values of <code>getOffset</code> and <code>getLength</code> should
     * be used.
     *
     * @return a byte array reference.
     *
     * @see #getOffset
     * @see #getLength
     * @see #setData
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Updates the array reference that will be returned by subsequent calls
     * to the <code>getData</code> method.
     *
     * @param data a byte array reference containing the new data value.
     *
     * @see #getData
     */
    public void setData(byte[] data) {
        this.data = data;
    }

    /**
     * Returns the offset within the byte array returned by
     * <code>getData</code> at which the data of interest start.
     *
     * @return an int offset.
     *
     * @see #getData
     * @see #getLength
     * @see #setOffset
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Updates the value that will be returned by subsequent calls
     * to the <code>getOffset</code> method.
     *
     * @param offset an int containing the new offset value.
     *
     * @see #getOffset
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }

    /**
     * Returns the length of the data of interest within the byte
     * array returned by <code>getData</code>.
     *
     * @return an int length.
     *
     * @see #getData
     * @see #getOffset
     * @see #setLength
     */
    public int getLength() {
        return length;
    }

    /**
     * Updates the value that will be returned by subsequent calls
     * to the <code>getLength</code> method.
     *
     * @param length an int containing the new length value.
     *
     * @see #getLength
     */
    public void setLength(int length) {
        this.length = length;
    }
}
