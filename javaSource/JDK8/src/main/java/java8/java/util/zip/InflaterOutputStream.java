/*
 * Copyright (c) 2006, 2013, Oracle and/or its affiliates. All rights reserved.
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

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.DataFormatException;
import java.util.zip.DeflaterInputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipException;

/**
 * Implements an output stream filter for uncompressing data stored in the
 * "deflate" compression format.
 *
 * @since       1.6
 * @author      David R Tribble (david@tribble.com)
 *
 * @see InflaterInputStream
 * @see DeflaterInputStream
 * @see DeflaterOutputStream
 */

public class InflaterOutputStream extends FilterOutputStream {
    /** Decompressor for this stream. */
    protected final Inflater inf;

    /** Output buffer for writing uncompressed data. */
    protected final byte[] buf;

    /** Temporary write buffer. */
    private final byte[] wbuf = new byte[1];

    /** Default decompressor is used. */
    private boolean usesDefaultInflater = false;

    /** true iff {@link #close()} has been called. */
    private boolean closed = false;

    /**
     * Checks to make sure that this stream has not been closed.
     */
    private void ensureOpen() throws IOException {
        if (closed) {
            throw new IOException("Stream closed");
        }
    }

    /**
     * Creates a new output stream with a default decompressor and buffer
     * size.
     *
     * @param out output stream to write the uncompressed data to
     * @throws NullPointerException if {@code out} is null
     */
    public InflaterOutputStream(OutputStream out) {
        this(out, new Inflater());
        usesDefaultInflater = true;
    }

    /**
     * Creates a new output stream with the specified decompressor and a
     * default buffer size.
     *
     * @param out output stream to write the uncompressed data to
     * @param infl decompressor ("inflater") for this stream
     * @throws NullPointerException if {@code out} or {@code infl} is null
     */
    public InflaterOutputStream(OutputStream out, Inflater infl) {
        this(out, infl, 512);
    }

    /**
     * Creates a new output stream with the specified decompressor and
     * buffer size.
     *
     * @param out output stream to write the uncompressed data to
     * @param infl decompressor ("inflater") for this stream
     * @param bufLen decompression buffer size
     * @throws IllegalArgumentException if {@code bufLen <= 0}
     * @throws NullPointerException if {@code out} or {@code infl} is null
     */
    public InflaterOutputStream(OutputStream out, Inflater infl, int bufLen) {
        super(out);

        // Sanity checks
        if (out == null)
            throw new NullPointerException("Null output");
        if (infl == null)
            throw new NullPointerException("Null inflater");
        if (bufLen <= 0)
            throw new IllegalArgumentException("Buffer size < 1");

        // Initialize
        inf = infl;
        buf = new byte[bufLen];
    }

    /**
     * Writes any remaining uncompressed data to the output stream and closes
     * the underlying output stream.
     *
     * @throws IOException if an I/O error occurs
     */
    public void close() throws IOException {
        if (!closed) {
            // Complete the uncompressed output
            try {
                finish();
            } finally {
                out.close();
                closed = true;
            }
        }
    }

    /**
     * Flushes this output stream, forcing any pending buffered output bytes to be
     * written.
     *
     * @throws IOException if an I/O error occurs or this stream is already
     * closed
     */
    public void flush() throws IOException {
        ensureOpen();

        // Finish decompressing and writing pending output data
        if (!inf.finished()) {
            try {
                while (!inf.finished()  &&  !inf.needsInput()) {
                    int n;

                    // Decompress pending output data
                    n = inf.inflate(buf, 0, buf.length);
                    if (n < 1) {
                        break;
                    }

                    // Write the uncompressed output data block
                    out.write(buf, 0, n);
                }
                super.flush();
            } catch (DataFormatException ex) {
                // Improperly formatted compressed (ZIP) data
                String msg = ex.getMessage();
                if (msg == null) {
                    msg = "Invalid ZLIB data format";
                }
                throw new ZipException(msg);
            }
        }
    }

    /**
     * Finishes writing uncompressed data to the output stream without closing
     * the underlying stream.  Use this method when applying multiple filters in
     * succession to the same output stream.
     *
     * @throws IOException if an I/O error occurs or this stream is already
     * closed
     */
    public void finish() throws IOException {
        ensureOpen();

        // Finish decompressing and writing pending output data
        flush();
        if (usesDefaultInflater) {
            inf.end();
        }
    }

    /**
     * Writes a byte to the uncompressed output stream.
     *
     * @param b a single byte of compressed data to decompress and write to
     * the output stream
     * @throws IOException if an I/O error occurs or this stream is already
     * closed
     * @throws ZipException if a compression (ZIP) format error occurs
     */
    public void write(int b) throws IOException {
        // Write a single byte of data
        wbuf[0] = (byte) b;
        write(wbuf, 0, 1);
    }

    /**
     * Writes an array of bytes to the uncompressed output stream.
     *
     * @param b buffer containing compressed data to decompress and write to
     * the output stream
     * @param off starting offset of the compressed data within {@code b}
     * @param len number of bytes to decompress from {@code b}
     * @throws IndexOutOfBoundsException if {@code off < 0}, or if
     * {@code len < 0}, or if {@code len > b.length - off}
     * @throws IOException if an I/O error occurs or this stream is already
     * closed
     * @throws NullPointerException if {@code b} is null
     * @throws ZipException if a compression (ZIP) format error occurs
     */
    public void write(byte[] b, int off, int len) throws IOException {
        // Sanity checks
        ensureOpen();
        if (b == null) {
            throw new NullPointerException("Null buffer for read");
        } else if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return;
        }

        // Write uncompressed data to the output stream
        try {
            for (;;) {
                int n;

                // Fill the decompressor buffer with output data
                if (inf.needsInput()) {
                    int part;

                    if (len < 1) {
                        break;
                    }

                    part = (len < 512 ? len : 512);
                    inf.setInput(b, off, part);
                    off += part;
                    len -= part;
                }

                // Decompress and write blocks of output data
                do {
                    n = inf.inflate(buf, 0, buf.length);
                    if (n > 0) {
                        out.write(buf, 0, n);
                    }
                } while (n > 0);

                // Check the decompressor
                if (inf.finished()) {
                    break;
                }
                if (inf.needsDictionary()) {
                    throw new ZipException("ZLIB dictionary missing");
                }
            }
        } catch (DataFormatException ex) {
            // Improperly formatted compressed (ZIP) data
            String msg = ex.getMessage();
            if (msg == null) {
                msg = "Invalid ZLIB data format";
            }
            throw new ZipException(msg);
        }
    }
}
