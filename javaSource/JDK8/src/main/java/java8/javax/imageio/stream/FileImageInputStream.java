/*
 * Copyright (c) 2000, 2007, Oracle and/or its affiliates. All rights reserved.
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import com.sun.imageio.stream.CloseableDisposerRecord;
import com.sun.imageio.stream.StreamFinalizer;
import sun.java2d.Disposer;

import javax.imageio.stream.ImageInputStreamImpl;

/**
 * An implementation of <code>ImageInputStream</code> that gets its
 * input from a <code>File</code> or <code>RandomAccessFile</code>.
 * The file contents are assumed to be stable during the lifetime of
 * the object.
 *
 */
public class FileImageInputStream extends ImageInputStreamImpl {

    private RandomAccessFile raf;

    /** The referent to be registered with the Disposer. */
    private final Object disposerReferent;

    /** The DisposerRecord that closes the underlying RandomAccessFile. */
    private final CloseableDisposerRecord disposerRecord;

    /**
     * Constructs a <code>FileImageInputStream</code> that will read
     * from a given <code>File</code>.
     *
     * <p> The file contents must not change between the time this
     * object is constructed and the time of the last call to a read
     * method.
     *
     * @param f a <code>File</code> to read from.
     *
     * @exception IllegalArgumentException if <code>f</code> is
     * <code>null</code>.
     * @exception SecurityException if a security manager exists
     * and does not allow read access to the file.
     * @exception FileNotFoundException if <code>f</code> is a
     * directory or cannot be opened for reading for any other reason.
     * @exception IOException if an I/O error occurs.
     */
    public FileImageInputStream(File f)
        throws FileNotFoundException, IOException {
        this(f == null ? null : new RandomAccessFile(f, "r"));
    }

    /**
     * Constructs a <code>FileImageInputStream</code> that will read
     * from a given <code>RandomAccessFile</code>.
     *
     * <p> The file contents must not change between the time this
     * object is constructed and the time of the last call to a read
     * method.
     *
     * @param raf a <code>RandomAccessFile</code> to read from.
     *
     * @exception IllegalArgumentException if <code>raf</code> is
     * <code>null</code>.
     */
    public FileImageInputStream(RandomAccessFile raf) {
        if (raf == null) {
            throw new IllegalArgumentException("raf == null!");
        }
        this.raf = raf;

        disposerRecord = new CloseableDisposerRecord(raf);
        if (getClass() == javax.imageio.stream.FileImageInputStream.class) {
            disposerReferent = new Object();
            Disposer.addRecord(disposerReferent, disposerRecord);
        } else {
            disposerReferent = new StreamFinalizer(this);
        }
    }

    public int read() throws IOException {
        checkClosed();
        bitOffset = 0;
        int val = raf.read();
        if (val != -1) {
            ++streamPos;
        }
        return val;
    }

    public int read(byte[] b, int off, int len) throws IOException {
        checkClosed();
        bitOffset = 0;
        int nbytes = raf.read(b, off, len);
        if (nbytes != -1) {
            streamPos += nbytes;
        }
        return nbytes;
    }

    /**
     * Returns the length of the underlying file, or <code>-1</code>
     * if it is unknown.
     *
     * @return the file length as a <code>long</code>, or
     * <code>-1</code>.
     */
    public long length() {
        try {
            checkClosed();
            return raf.length();
        } catch (IOException e) {
            return -1L;
        }
    }

    public void seek(long pos) throws IOException {
        checkClosed();
        if (pos < flushedPos) {
            throw new IndexOutOfBoundsException("pos < flushedPos!");
        }
        bitOffset = 0;
        raf.seek(pos);
        streamPos = raf.getFilePointer();
    }

    public void close() throws IOException {
        super.close();
        disposerRecord.dispose(); // this closes the RandomAccessFile
        raf = null;
    }

    /**
     * {@inheritDoc}
     */
    protected void finalize() throws Throwable {
        // Empty finalizer: for performance reasons we instead use the
        // Disposer mechanism for ensuring that the underlying
        // RandomAccessFile is closed prior to garbage collection
    }
}
