/*
 * Copyright (c) 2000, 2003, Oracle and/or its affiliates. All rights reserved.
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

import java.util.ArrayList;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

/**
 * Package-visible class consolidating common code for
 * <code>MemoryCacheImageInputStream</code> and
 * <code>MemoryCacheImageOutputStream</code>.
 * This class keeps an <code>ArrayList</code> of 8K blocks,
 * loaded sequentially.  Blocks may only be disposed of
 * from the index 0 forward.  As blocks are freed, the
 * corresponding entries in the array list are set to
 * <code>null</code>, but no compacting is performed.
 * This allows the index for each block to never change,
 * and the length of the cache is always the same as the
 * total amount of data ever cached.  Cached data is
 * therefore always contiguous from the point of last
 * disposal to the current length.
 *
 * <p> The total number of blocks resident in the cache must not
 * exceed <code>Integer.MAX_VALUE</code>.  In practice, the limit of
 * available memory will be exceeded long before this becomes an
 * issue, since a full cache would contain 8192*2^31 = 16 terabytes of
 * data.
 *
 * A <code>MemoryCache</code> may be reused after a call
 * to <code>reset()</code>.
 */
class MemoryCache {

    private static final int BUFFER_LENGTH = 8192;

    private ArrayList cache = new ArrayList();

    private long cacheStart = 0L;

    /**
     * The largest position ever written to the cache.
     */
    private long length = 0L;

    private byte[] getCacheBlock(long blockNum) throws IOException {
        long blockOffset = blockNum - cacheStart;
        if (blockOffset > Integer.MAX_VALUE) {
            // This can only happen when the cache hits 16 terabytes of
            // contiguous data...
            throw new IOException("Cache addressing limit exceeded!");
        }
        return (byte[])cache.get((int)blockOffset);
    }

    /**
     * Ensures that at least <code>pos</code> bytes are cached,
     * or the end of the source is reached.  The return value
     * is equal to the smaller of <code>pos</code> and the
     * length of the source.
     */
    public long loadFromStream(InputStream stream, long pos)
        throws IOException {
        // We've already got enough data cached
        if (pos < length) {
            return pos;
        }

        int offset = (int)(length % BUFFER_LENGTH);
        byte [] buf = null;

        long len = pos - length;
        if (offset != 0) {
            buf = getCacheBlock(length/BUFFER_LENGTH);
        }

        while (len > 0) {
            if (buf == null) {
                try {
                    buf = new byte[BUFFER_LENGTH];
                } catch (OutOfMemoryError e) {
                    throw new IOException("No memory left for cache!");
                }
                offset = 0;
            }

            int left = BUFFER_LENGTH - offset;
            int nbytes = (int)Math.min(len, (long)left);
            nbytes = stream.read(buf, offset, nbytes);
            if (nbytes == -1) {
                return length; // EOF
            }

            if (offset == 0) {
                cache.add(buf);
            }

            len -= nbytes;
            length += nbytes;
            offset += nbytes;

            if (offset >= BUFFER_LENGTH) {
                // we've filled the current buffer, so a new one will be
                // allocated next time around (and offset will be reset to 0)
                buf = null;
            }
        }

        return pos;
    }

    /**
     * Writes out a portion of the cache to an <code>OutputStream</code>.
     * This method preserves no state about the output stream, and does
     * not dispose of any blocks containing bytes written.  To dispose
     * blocks, use {@link #disposeBefore <code>disposeBefore()</code>}.
     *
     * @exception IndexOutOfBoundsException if any portion of
     * the requested data is not in the cache (including if <code>pos</code>
     * is in a block already disposed), or if either <code>pos</code> or
     * <code>len</code> is < 0.
     */
    public void writeToStream(OutputStream stream, long pos, long len)
        throws IOException {
        if (pos + len > length) {
            throw new IndexOutOfBoundsException("Argument out of cache");
        }
        if ((pos < 0) || (len < 0)) {
            throw new IndexOutOfBoundsException("Negative pos or len");
        }
        if (len == 0) {
            return;
        }

        long bufIndex = pos/BUFFER_LENGTH;
        if (bufIndex < cacheStart) {
            throw new IndexOutOfBoundsException("pos already disposed");
        }
        int offset = (int)(pos % BUFFER_LENGTH);

        byte[] buf = getCacheBlock(bufIndex++);
        while (len > 0) {
            if (buf == null) {
                buf = getCacheBlock(bufIndex++);
                offset = 0;
            }
            int nbytes = (int)Math.min(len, (long)(BUFFER_LENGTH - offset));
            stream.write(buf, offset, nbytes);
            buf = null;
            len -= nbytes;
        }
    }

    /**
     * Ensure that there is space to write a byte at the given position.
     */
    private void pad(long pos) throws IOException {
        long currIndex = cacheStart + cache.size() - 1;
        long lastIndex = pos/BUFFER_LENGTH;
        long numNewBuffers = lastIndex - currIndex;
        for (long i = 0; i < numNewBuffers; i++) {
            try {
                cache.add(new byte[BUFFER_LENGTH]);
            } catch (OutOfMemoryError e) {
                throw new IOException("No memory left for cache!");
            }
        }
    }

    /**
     * Overwrites and/or appends the cache from a byte array.
     * The length of the cache will be extended as needed to hold
     * the incoming data.
     *
     * @param b an array of bytes containing data to be written.
     * @param off the starting offset withing the data array.
     * @param len the number of bytes to be written.
     * @param pos the cache position at which to begin writing.
     *
     * @exception NullPointerException if <code>b</code> is <code>null</code>.
     * @exception IndexOutOfBoundsException if <code>off</code>,
     * <code>len</code>, or <code>pos</code> are negative,
     * or if <code>off+len > b.length</code>.
     */
    public void write(byte[] b, int off, int len, long pos)
        throws IOException {
        if (b == null) {
            throw new NullPointerException("b == null!");
        }
        // Fix 4430357 - if off + len < 0, overflow occurred
        if ((off < 0) || (len < 0) || (pos < 0) ||
            (off + len > b.length) || (off + len < 0)) {
            throw new IndexOutOfBoundsException();
        }

        // Ensure there is space for the incoming data
        long lastPos = pos + len - 1;
        if (lastPos >= length) {
            pad(lastPos);
            length = lastPos + 1;
        }

        // Copy the data into the cache, block by block
        int offset = (int)(pos % BUFFER_LENGTH);
        while (len > 0) {
            byte[] buf = getCacheBlock(pos/BUFFER_LENGTH);
            int nbytes = Math.min(len, BUFFER_LENGTH - offset);
            System.arraycopy(b, off, buf, offset, nbytes);

            pos += nbytes;
            off += nbytes;
            len -= nbytes;
            offset = 0; // Always after the first time
        }
    }

    /**
     * Overwrites or appends a single byte to the cache.
     * The length of the cache will be extended as needed to hold
     * the incoming data.
     *
     * @param b an <code>int</code> whose 8 least significant bits
     * will be written.
     * @param pos the cache position at which to begin writing.
     *
     * @exception IndexOutOfBoundsException if <code>pos</code> is negative.
     */
    public void write(int b, long pos) throws IOException {
        if (pos < 0) {
            throw new ArrayIndexOutOfBoundsException("pos < 0");
        }

        // Ensure there is space for the incoming data
        if (pos >= length) {
            pad(pos);
            length = pos + 1;
        }

        // Insert the data.
        byte[] buf = getCacheBlock(pos/BUFFER_LENGTH);
        int offset = (int)(pos % BUFFER_LENGTH);
        buf[offset] = (byte)b;
    }

    /**
     * Returns the total length of data that has been cached,
     * regardless of whether any early blocks have been disposed.
     * This value will only ever increase.
     */
    public long getLength() {
        return length;
    }

    /**
     * Returns the single byte at the given position, as an
     * <code>int</code>.  Returns -1 if this position has
     * not been cached or has been disposed.
     */
    public int read(long pos) throws IOException {
        if (pos >= length) {
            return -1;
        }

        byte[] buf = getCacheBlock(pos/BUFFER_LENGTH);
        if (buf == null) {
            return -1;
        }

        return buf[(int)(pos % BUFFER_LENGTH)] & 0xff;
    }

    /**
     * Copy <code>len</code> bytes from the cache, starting
     * at cache position <code>pos</code>, into the array
     * <code>b</code> at offset <code>off</code>.
     *
     * @exception NullPointerException if b is <code>null</code>
     * @exception IndexOutOfBoundsException if <code>off</code>,
     * <code>len</code> or <code>pos</code> are negative or if
     * <code>off + len > b.length</code> or if any portion of the
     * requested data is not in the cache (including if
     * <code>pos</code> is in a block that has already been disposed).
     */
    public void read(byte[] b, int off, int len, long pos)
        throws IOException {
        if (b == null) {
            throw new NullPointerException("b == null!");
        }
        // Fix 4430357 - if off + len < 0, overflow occurred
        if ((off < 0) || (len < 0) || (pos < 0) ||
            (off + len > b.length) || (off + len < 0)) {
            throw new IndexOutOfBoundsException();
        }
        if (pos + len > length) {
            throw new IndexOutOfBoundsException();
        }

        long index = pos/BUFFER_LENGTH;
        int offset = (int)pos % BUFFER_LENGTH;
        while (len > 0) {
            int nbytes = Math.min(len, BUFFER_LENGTH - offset);
            byte[] buf = getCacheBlock(index++);
            System.arraycopy(buf, offset, b, off, nbytes);

            len -= nbytes;
            off += nbytes;
            offset = 0; // Always after the first time
        }
    }

    /**
     * Free the blocks up to the position <code>pos</code>.
     * The byte at <code>pos</code> remains available.
     *
     * @exception IndexOutOfBoundsException if <code>pos</code>
     * is in a block that has already been disposed.
     */
    public void disposeBefore(long pos) {
        long index = pos/BUFFER_LENGTH;
        if (index < cacheStart) {
            throw new IndexOutOfBoundsException("pos already disposed");
        }
        long numBlocks = Math.min(index - cacheStart, cache.size());
        for (long i = 0; i < numBlocks; i++) {
            cache.remove(0);
        }
        this.cacheStart = index;
    }

    /**
     * Erase the entire cache contents and reset the length to 0.
     * The cache object may subsequently be reused as though it had just
     * been allocated.
     */
    public void reset() {
        cache.clear();
        cacheStart = 0;
        length = 0L;
    }
 }
