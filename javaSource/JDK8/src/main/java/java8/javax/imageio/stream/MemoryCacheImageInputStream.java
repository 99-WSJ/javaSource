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

import java.io.InputStream;
import java.io.IOException;
import com.sun.imageio.stream.StreamFinalizer;
import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;

import javax.imageio.stream.ImageInputStreamImpl;
import javax.imageio.stream.MemoryCache;

/**
 * An implementation of <code>ImageInputStream</code> that gets its
 * input from a regular <code>InputStream</code>.  A memory buffer is
 * used to cache at least the data between the discard position and
 * the current read position.
 *
 * <p> In general, it is preferable to use a
 * <code>FileCacheImageInputStream</code> when reading from a regular
 * <code>InputStream</code>.  This class is provided for cases where
 * it is not possible to create a writable temporary file.
 *
 */
public class MemoryCacheImageInputStream extends ImageInputStreamImpl {

    private InputStream stream;

    private javax.imageio.stream.MemoryCache cache = new javax.imageio.stream.MemoryCache();

    /** The referent to be registered with the Disposer. */
    private final Object disposerReferent;

    /** The DisposerRecord that resets the underlying MemoryCache. */
    private final DisposerRecord disposerRecord;

    /**
     * Constructs a <code>MemoryCacheImageInputStream</code> that will read
     * from a given <code>InputStream</code>.
     *
     * @param stream an <code>InputStream</code> to read from.
     *
     * @exception IllegalArgumentException if <code>stream</code> is
     * <code>null</code>.
     */
    public MemoryCacheImageInputStream(InputStream stream) {
        if (stream == null) {
            throw new IllegalArgumentException("stream == null!");
        }
        this.stream = stream;

        disposerRecord = new StreamDisposerRecord(cache);
        if (getClass() == javax.imageio.stream.MemoryCacheImageInputStream.class) {
            disposerReferent = new Object();
            Disposer.addRecord(disposerReferent, disposerRecord);
        } else {
            disposerReferent = new StreamFinalizer(this);
        }
    }

    public int read() throws IOException {
        checkClosed();
        bitOffset = 0;
        long pos = cache.loadFromStream(stream, streamPos+1);
        if (pos >= streamPos+1) {
            return cache.read(streamPos++);
        } else {
            return -1;
        }
    }

    public int read(byte[] b, int off, int len) throws IOException {
        checkClosed();

        if (b == null) {
            throw new NullPointerException("b == null!");
        }
        if (off < 0 || len < 0 || off + len > b.length || off + len < 0) {
            throw new IndexOutOfBoundsException
                ("off < 0 || len < 0 || off+len > b.length || off+len < 0!");
        }

        bitOffset = 0;

        if (len == 0) {
            return 0;
        }

        long pos = cache.loadFromStream(stream, streamPos+len);

        len = (int)(pos - streamPos);  // In case stream ended early

        if (len > 0) {
            cache.read(b, off, len, streamPos);
            streamPos += len;
            return len;
        } else {
            return -1;
        }
    }

    public void flushBefore(long pos) throws IOException {
        super.flushBefore(pos); // this will call checkClosed() for us
        cache.disposeBefore(pos);
    }

    /**
     * Returns <code>true</code> since this
     * <code>ImageInputStream</code> caches data in order to allow
     * seeking backwards.
     *
     * @return <code>true</code>.
     *
     * @see #isCachedMemory
     * @see #isCachedFile
     */
    public boolean isCached() {
        return true;
    }

    /**
     * Returns <code>false</code> since this
     * <code>ImageInputStream</code> does not maintain a file cache.
     *
     * @return <code>false</code>.
     *
     * @see #isCached
     * @see #isCachedMemory
     */
    public boolean isCachedFile() {
        return false;
    }

    /**
     * Returns <code>true</code> since this
     * <code>ImageInputStream</code> maintains a main memory cache.
     *
     * @return <code>true</code>.
     *
     * @see #isCached
     * @see #isCachedFile
     */
    public boolean isCachedMemory() {
        return true;
    }

    /**
     * Closes this <code>MemoryCacheImageInputStream</code>, freeing
     * the cache.  The source <code>InputStream</code> is not closed.
     */
    public void close() throws IOException {
        super.close();
        disposerRecord.dispose(); // this resets the MemoryCache
        stream = null;
        cache = null;
    }

    /**
     * {@inheritDoc}
     */
    protected void finalize() throws Throwable {
        // Empty finalizer: for performance reasons we instead use the
        // Disposer mechanism for ensuring that the underlying
        // MemoryCache is reset prior to garbage collection
    }

    private static class StreamDisposerRecord implements DisposerRecord {
        private javax.imageio.stream.MemoryCache cache;

        public StreamDisposerRecord(javax.imageio.stream.MemoryCache cache) {
            this.cache = cache;
        }

        public synchronized void dispose() {
            if (cache != null) {
                cache.reset();
                cache = null;
            }
        }
    }
}
