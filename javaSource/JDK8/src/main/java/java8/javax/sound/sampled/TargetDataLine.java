/*
 * Copyright (c) 1999, 2003, Oracle and/or its affiliates. All rights reserved.
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

package java8.javax.sound.sampled;

import javax.sound.sampled.*;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;

/**
 * A target data line is a type of <code>{@link DataLine}</code> from which
 * audio data can be read.  The most common example is a data line that gets
 * its data from an audio capture device.  (The device is implemented as a
 * mixer that writes to the target data line.)
 * <p>
 * Note that the naming convention for this interface reflects the relationship
 * between the line and its mixer.  From the perspective of an application,
 * a target data line may act as a source for audio data.
 * <p>
 * The target data line can be obtained from a mixer by invoking the
 * <code>{@link Mixer#getLine getLine}</code>
 * method of <code>Mixer</code> with an appropriate
 * <code>{@link Info}</code> object.
 * <p>
 * The <code>TargetDataLine</code> interface provides a method for reading the
 * captured data from the target data line's buffer.Applications
 * that record audio should read data from the target data line quickly enough
 * to keep the buffer from overflowing, which could cause discontinuities in
 * the captured data that are perceived as clicks.  Applications can use the
 * <code>{@link DataLine#available available}</code> method defined in the
 * <code>DataLine</code> interface to determine the amount of data currently
 * queued in the data line's buffer.  If the buffer does overflow,
 * the oldest queued data is discarded and replaced by new data.
 *
 * @author Kara Kytle
 * @see Mixer
 * @see DataLine
 * @see SourceDataLine
 * @since 1.3
 */
public interface TargetDataLine extends DataLine {


    /**
     * Opens the line with the specified format and requested buffer size,
     * causing the line to acquire any required system resources and become
     * operational.
     * <p>
     * The buffer size is specified in bytes, but must represent an integral
     * number of sample frames.  Invoking this method with a requested buffer
     * size that does not meet this requirement may result in an
     * IllegalArgumentException.  The actual buffer size for the open line may
     * differ from the requested buffer size.  The value actually set may be
     * queried by subsequently calling <code>{@link DataLine#getBufferSize}</code>
     * <p>
     * If this operation succeeds, the line is marked as open, and an
     * <code>{@link LineEvent.Type#OPEN OPEN}</code> event is dispatched to the
     * line's listeners.
     * <p>
     * Invoking this method on a line that is already open is illegal
     * and may result in an <code>IllegalStateException</code>.
     * <p>
     * Some lines, once closed, cannot be reopened.  Attempts
     * to reopen such a line will always result in a
     * <code>LineUnavailableException</code>.
     *
     * @param format the desired audio format
     * @param bufferSize the desired buffer size, in bytes.
     * @throws LineUnavailableException if the line cannot be
     * opened due to resource restrictions
     * @throws IllegalArgumentException if the buffer size does not represent
     * an integral number of sample frames,
     * or if <code>format</code> is not fully specified or invalid
     * @throws IllegalStateException if the line is already open
     * @throws SecurityException if the line cannot be
     * opened due to security restrictions
     *
     * @see #open(AudioFormat)
     * @see Line#open
     * @see Line#close
     * @see Line#isOpen
     * @see LineEvent
     */
    public void open(AudioFormat format, int bufferSize) throws LineUnavailableException;


    /**
     * Opens the line with the specified format, causing the line to acquire any
     * required system resources and become operational.
     *
     * <p>
     * The implementation chooses a buffer size, which is measured in bytes but
     * which encompasses an integral number of sample frames.  The buffer size
     * that the system has chosen may be queried by subsequently calling <code>{@link DataLine#getBufferSize}</code>
     * <p>
     * If this operation succeeds, the line is marked as open, and an
     * <code>{@link LineEvent.Type#OPEN OPEN}</code> event is dispatched to the
     * line's listeners.
     * <p>
     * Invoking this method on a line that is already open is illegal
     * and may result in an <code>IllegalStateException</code>.
     * <p>
     * Some lines, once closed, cannot be reopened.  Attempts
     * to reopen such a line will always result in a
     * <code>LineUnavailableException</code>.
     *
     * @param format the desired audio format
     * @throws LineUnavailableException if the line cannot be
     * opened due to resource restrictions
     * @throws IllegalArgumentException if <code>format</code>
     * is not fully specified or invalid
     * @throws IllegalStateException if the line is already open
     * @throws SecurityException if the line cannot be
     * opened due to security restrictions
     *
     * @see #open(AudioFormat, int)
     * @see Line#open
     * @see Line#close
     * @see Line#isOpen
     * @see LineEvent
     */
    public void open(AudioFormat format) throws LineUnavailableException;


    /**
     * Reads audio data from the data line's input buffer.   The requested
     * number of bytes is read into the specified array, starting at
     * the specified offset into the array in bytes.  This method blocks until
     * the requested amount of data has been read.  However, if the data line
     * is closed, stopped, drained, or flushed before the requested amount has
     * been read, the method no longer blocks, but returns the number of bytes
     * read thus far.
     * <p>
     * The number of bytes that can be read without blocking can be ascertained
     * using the <code>{@link DataLine#available available}</code> method of the
     * <code>DataLine</code> interface.  (While it is guaranteed that
     * this number of bytes can be read without blocking, there is no guarantee
     * that attempts to read additional data will block.)
     * <p>
     * The number of bytes to be read must represent an integral number of
     * sample frames, such that:
     * <br>
     * <center><code>[ bytes read ] % [frame size in bytes ] == 0</code></center>
     * <br>
     * The return value will always meet this requirement.  A request to read a
     * number of bytes representing a non-integral number of sample frames cannot
     * be fulfilled and may result in an IllegalArgumentException.
     *
     * @param b a byte array that will contain the requested input data when
     * this method returns
     * @param off the offset from the beginning of the array, in bytes
     * @param len the requested number of bytes to read
     * @return the number of bytes actually read
     * @throws IllegalArgumentException if the requested number of bytes does
     * not represent an integral number of sample frames.
     * or if <code>len</code> is negative.
     * @throws ArrayIndexOutOfBoundsException if <code>off</code> is negative,
     * or <code>off+len</code> is greater than the length of the array
     * <code>b</code>.
     *
     * @see SourceDataLine#write
     * @see DataLine#available
     */
    public int read(byte[] b, int off, int len);

    /**
     * Obtains the number of sample frames of audio data that can be read from
     * the target data line without blocking.  Note that the return value
     * measures sample frames, not bytes.
     * @return the number of sample frames currently available for reading
     * @see SourceDataLine#availableWrite
     */
    //public int availableRead();
}
