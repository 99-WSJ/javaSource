/*
 * Copyright (c) 2000, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java8.java.nio.channels;

import java.io.IOException;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.WritableByteChannel;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.nio.channels.spi.SelectorProvider;


/**
 * A pair of channels that implements a unidirectional pipe.
 *
 * <p> A pipe consists of a pair of channels: A writable {@link
 * java.nio.channels.Pipe.SinkChannel sink} channel and a readable {@link java.nio.channels.Pipe.SourceChannel source}
 * channel.  Once some bytes are written to the sink channel they can be read
 * from source channel in exactlyAthe order in which they were written.
 *
 * <p> Whether or not a thread writing bytes to a pipe will block until another
 * thread reads those bytes, or some previously-written bytes, from the pipe is
 * system-dependent and therefore unspecified.  Many pipe implementations will
 * buffer up to a certain number of bytes between the sink and source channels,
 * but such buffering should not be assumed.  </p>
 *
 *
 * @author Mark Reinhold
 * @author JSR-51 Expert Group
 * @since 1.4
 */

public abstract class Pipe {

    /**
     * A channel representing the readable end of a {@link java.nio.channels.Pipe}.
     *
     * @since 1.4
     */
    public static abstract class SourceChannel
        extends AbstractSelectableChannel
        implements ReadableByteChannel, ScatteringByteChannel
    {
        /**
         * Constructs a new instance of this class.
         *
         * @param  provider
         *         The selector provider
         */
        protected SourceChannel(SelectorProvider provider) {
            super(provider);
        }

        /**
         * Returns an operation set identifying this channel's supported
         * operations.
         *
         * <p> Pipe-source channels only support reading, so this method
         * returns {@link SelectionKey#OP_READ}.  </p>
         *
         * @return  The valid-operation set
         */
        public final int validOps() {
            return SelectionKey.OP_READ;
        }

    }

    /**
     * A channel representing the writable end of a {@link java.nio.channels.Pipe}.
     *
     * @since 1.4
     */
    public static abstract class SinkChannel
        extends AbstractSelectableChannel
        implements WritableByteChannel, GatheringByteChannel
    {
        /**
         * Initializes a new instance of this class.
         *
         * @param  provider
         *         The selector provider
         */
        protected SinkChannel(SelectorProvider provider) {
            super(provider);
        }

        /**
         * Returns an operation set identifying this channel's supported
         * operations.
         *
         * <p> Pipe-sink channels only support writing, so this method returns
         * {@link SelectionKey#OP_WRITE}.  </p>
         *
         * @return  The valid-operation set
         */
        public final int validOps() {
            return SelectionKey.OP_WRITE;
        }

    }

    /**
     * Initializes a new instance of this class.
     */
    protected Pipe() { }

    /**
     * Returns this pipe's source channel.
     *
     * @return  This pipe's source channel
     */
    public abstract SourceChannel source();

    /**
     * Returns this pipe's sink channel.
     *
     * @return  This pipe's sink channel
     */
    public abstract SinkChannel sink();

    /**
     * Opens a pipe.
     *
     * <p> The new pipe is created by invoking the {@link
     * SelectorProvider#openPipe openPipe} method of the
     * system-wide default {@link SelectorProvider}
     * object.  </p>
     *
     * @return  A new pipe
     *
     * @throws  IOException
     *          If an I/O error occurs
     */
    public static java.nio.channels.Pipe open() throws IOException {
        return SelectorProvider.provider().openPipe();
    }

}
