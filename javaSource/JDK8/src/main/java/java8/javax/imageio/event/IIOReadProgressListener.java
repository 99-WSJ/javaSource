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

package java8.javax.imageio.event;

import java.util.EventListener;
import javax.imageio.ImageReader;
import javax.imageio.event.IIOReadUpdateListener;

/**
 * An interface used by <code>ImageReader</code> implementations to
 * notify callers of their image and thumbnail reading methods of
 * progress.
 *
 * <p> This interface receives general indications of decoding
 * progress (via the <code>imageProgress</code> and
 * <code>thumbnailProgress</code> methods), and events indicating when
 * an entire image has been updated (via the
 * <code>imageStarted</code>, <code>imageComplete</code>,
 * <code>thumbnailStarted</code> and <code>thumbnailComplete</code>
 * methods).  Applications that wish to be informed of pixel updates
 * as they happen (for example, during progressive decoding), should
 * provide an <code>IIOReadUpdateListener</code>.
 *
 * @see IIOReadUpdateListener
 * @see ImageReader#addIIOReadProgressListener
 * @see ImageReader#removeIIOReadProgressListener
 *
 */
public interface IIOReadProgressListener extends EventListener {

    /**
     * Reports that a sequence of read operations is beginning.
     * <code>ImageReader</code> implementations are required to call
     * this method exactly once from their
     * <code>readAll(Iterator)</code> method.
     *
     * @param source the <code>ImageReader</code> object calling this method.
     * @param minIndex the index of the first image to be read.
     */
    void sequenceStarted(ImageReader source, int minIndex);

    /**
     * Reports that a sequence of read operations has completed.
     * <code>ImageReader</code> implementations are required to call
     * this method exactly once from their
     * <code>readAll(Iterator)</code> method.
     *
     * @param source the <code>ImageReader</code> object calling this method.
     */
    void sequenceComplete(ImageReader source);

    /**
     * Reports that an image read operation is beginning.  All
     * <code>ImageReader</code> implementations are required to call
     * this method exactly once when beginning an image read
     * operation.
     *
     * @param source the <code>ImageReader</code> object calling this method.
     * @param imageIndex the index of the image being read within its
     * containing input file or stream.
     */
    void imageStarted(ImageReader source, int imageIndex);

    /**
     * Reports the approximate degree of completion of the current
     * <code>read</code> call of the associated
     * <code>ImageReader</code>.
     *
     * <p> The degree of completion is expressed as a percentage
     * varying from <code>0.0F</code> to <code>100.0F</code>.  The
     * percentage should ideally be calculated in terms of the
     * remaining time to completion, but it is usually more practical
     * to use a more well-defined metric such as pixels decoded or
     * portion of input stream consumed.  In any case, a sequence of
     * calls to this method during a given read operation should
     * supply a monotonically increasing sequence of percentage
     * values.  It is not necessary to supply the exact values
     * <code>0</code> and <code>100</code>, as these may be inferred
     * by the callee from other methods.
     *
     * <p> Each particular <code>ImageReader</code> implementation may
     * call this method at whatever frequency it desires.  A rule of
     * thumb is to call it around each 5 percent mark.
     *
     * @param source the <code>ImageReader</code> object calling this method.
     * @param percentageDone the approximate percentage of decoding that
     * has been completed.
     */
    void imageProgress(ImageReader source, float percentageDone);

    /**
     * Reports that the current image read operation has completed.
     * All <code>ImageReader</code> implementations are required to
     * call this method exactly once upon completion of each image
     * read operation.
     *
     * @param source the <code>ImageReader</code> object calling this
     * method.
     */
    void imageComplete(ImageReader source);

    /**
     * Reports that a thumbnail read operation is beginning.  All
     * <code>ImageReader</code> implementations are required to call
     * this method exactly once when beginning a thumbnail read
     * operation.
     *
     * @param source the <code>ImageReader</code> object calling this method.
     * @param imageIndex the index of the image being read within its
     * containing input file or stream.
     * @param thumbnailIndex the index of the thumbnail being read.
     */
    void thumbnailStarted(ImageReader source,
                          int imageIndex, int thumbnailIndex);

    /**
     * Reports the approximate degree of completion of the current
     * <code>getThumbnail</code> call within the associated
     * <code>ImageReader</code>.  The semantics are identical to those
     * of <code>imageProgress</code>.
     *
     * @param source the <code>ImageReader</code> object calling this method.
     * @param percentageDone the approximate percentage of decoding that
     * has been completed.
     */
    void thumbnailProgress(ImageReader source, float percentageDone);

    /**
     * Reports that a thumbnail read operation has completed.  All
     * <code>ImageReader</code> implementations are required to call
     * this method exactly once upon completion of each thumbnail read
     * operation.
     *
     * @param source the <code>ImageReader</code> object calling this
     * method.
     */
    void thumbnailComplete(ImageReader source);

    /**
     * Reports that a read has been aborted via the reader's
     * <code>abort</code> method.  No further notifications will be
     * given.
     *
     * @param source the <code>ImageReader</code> object calling this
     * method.
     */
    void readAborted(ImageReader source);
}
