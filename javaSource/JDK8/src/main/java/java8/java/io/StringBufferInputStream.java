/*
 * Copyright (c) 1995, 2004, Oracle and/or its affiliates. All rights reserved.
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

package java8.java.io;

import java.io.InputStream;

/**
 * This class allows an application to create an input stream in
 * which the bytes read are supplied by the contents of a string.
 * Applications can also read bytes from a byte array by using a
 * <code>ByteArrayInputStream</code>.
 * <p>
 * Only the low eight bits of each character in the string are used by
 * this class.
 *
 * @author     Arthur van Hoff
 * @see        java.io.ByteArrayInputStream
 * @see        java.io.StringReader
 * @since      JDK1.0
 * @deprecated This class does not properly convert characters into bytes.  As
 *             of JDK&nbsp;1.1, the preferred way to create a stream from a
 *             string is via the <code>StringReader</code> class.
 */
@Deprecated
public
class StringBufferInputStream extends InputStream {
    /**
     * The string from which bytes are read.
     */
    protected String buffer;

    /**
     * The index of the next character to read from the input stream buffer.
     *
     * @see        java.io.StringBufferInputStream#buffer
     */
    protected int pos;

    /**
     * The number of valid characters in the input stream buffer.
     *
     * @see        java.io.StringBufferInputStream#buffer
     */
    protected int count;

    /**
     * Creates a string input stream to read data from the specified string.
     *
     * @param      s   the underlying input buffer.
     */
    public StringBufferInputStream(String s) {
        this.buffer = s;
        count = s.length();
    }

    /**
     * Reads the next byte of data from this input stream. The value
     * byte is returned as an <code>int</code> in the range
     * <code>0</code> to <code>255</code>. If no byte is available
     * because the end of the stream has been reached, the value
     * <code>-1</code> is returned.
     * <p>
     * The <code>read</code> method of
     * <code>StringBufferInputStream</code> cannot block. It returns the
     * low eight bits of the next character in this input stream's buffer.
     *
     * @return     the next byte of data, or <code>-1</code> if the end of the
     *             stream is reached.
     */
    public synchronized int read() {
        return (pos < count) ? (buffer.charAt(pos++) & 0xFF) : -1;
    }

    /**
     * Reads up to <code>len</code> bytes of data from this input stream
     * into an array of bytes.
     * <p>
     * The <code>read</code> method of
     * <code>StringBufferInputStream</code> cannot block. It copies the
     * low eight bits from the characters in this input stream's buffer into
     * the byte array argument.
     *
     * @param      b     the buffer into which the data is read.
     * @param      off   the start offset of the data.
     * @param      len   the maximum number of bytes read.
     * @return     the total number of bytes read into the buffer, or
     *             <code>-1</code> if there is no more data because the end of
     *             the stream has been reached.
     */
    public synchronized int read(byte b[], int off, int len) {
        if (b == null) {
            throw new NullPointerException();
        } else if ((off < 0) || (off > b.length) || (len < 0) ||
                   ((off + len) > b.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        }
        if (pos >= count) {
            return -1;
        }
        if (pos + len > count) {
            len = count - pos;
        }
        if (len <= 0) {
            return 0;
        }
        String  s = buffer;
        int cnt = len;
        while (--cnt >= 0) {
            b[off++] = (byte)s.charAt(pos++);
        }

        return len;
    }

    /**
     * Skips <code>n</code> bytes of input from this input stream. Fewer
     * bytes might be skipped if the end of the input stream is reached.
     *
     * @param      n   the number of bytes to be skipped.
     * @return     the actual number of bytes skipped.
     */
    public synchronized long skip(long n) {
        if (n < 0) {
            return 0;
        }
        if (n > count - pos) {
            n = count - pos;
        }
        pos += n;
        return n;
    }

    /**
     * Returns the number of bytes that can be read from the input
     * stream without blocking.
     *
     * @return     the value of <code>count&nbsp;-&nbsp;pos</code>, which is the
     *             number of bytes remaining to be read from the input buffer.
     */
    public synchronized int available() {
        return count - pos;
    }

    /**
     * Resets the input stream to begin reading from the first character
     * of this input stream's underlying buffer.
     */
    public synchronized void reset() {
        pos = 0;
    }
}
