/*
 * Copyright (c) 2000, 2001, Oracle and/or its affiliates. All rights reserved.
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
import javax.imageio.ImageWriter;

/**
 * An interface used by <code>ImageWriter</code> implementations to notify
 * callers of their image writing methods of progress.
 *
 * @see ImageWriter#write
 *
 */
public interface IIOWriteProgressListener extends EventListener {

    /**
     * Reports that an image write operation is beginning.  All
     * <code>ImageWriter</code> implementations are required to call
     * this method exactly once when beginning an image write
     * operation.
     *
     * @param source the <code>ImageWriter</code> object calling this
     * method.
     * @param imageIndex the index of the image being written within
     * its containing input file or stream.
     */
    void imageStarted(ImageWriter source, int imageIndex);

    /**
     * Reports the approximate degree of completion of the current
     * <code>write</code> call within the associated
     * <code>ImageWriter</code>.
     *
     * <p> The degree of completion is expressed as an index
     * indicating which image is being written, and a percentage
     * varying from <code>0.0F</code> to <code>100.0F</code>
     * indicating how much of the current image has been output.  The
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
     * <p> Each particular <code>ImageWriter</code> implementation may
     * call this method at whatever frequency it desires.  A rule of
     * thumb is to call it around each 5 percent mark.
     *
     * @param source the <code>ImageWriter</code> object calling this method.
     * @param percentageDone the approximate percentage of decoding that
     * has been completed.
     */
    void imageProgress(ImageWriter source,
                       float percentageDone);

    /**
     * Reports that the image write operation has completed.  All
     * <code>ImageWriter</code> implementations are required to call
     * this method exactly once upon completion of each image write
     * operation.
     *
     * @param source the <code>ImageWriter</code> object calling this method.
     */
    void imageComplete(ImageWriter source);

    /**
     * Reports that a thumbnail write operation is beginning.  All
     * <code>ImageWriter</code> implementations are required to call
     * this method exactly once when beginning a thumbnail write
     * operation.
     *
     * @param source the <code>ImageWrite</code> object calling this method.
     * @param imageIndex the index of the image being written within its
     * containing input file or stream.
     * @param thumbnailIndex the index of the thumbnail being written.
     */
    void thumbnailStarted(ImageWriter source,
                          int imageIndex, int thumbnailIndex);

    /**
     * Reports the approximate degree of completion of the current
     * thumbnail write within the associated <code>ImageWriter</code>.
     * The semantics are identical to those of
     * <code>imageProgress</code>.
     *
     * @param source the <code>ImageWriter</code> object calling this
     * method.
     * @param percentageDone the approximate percentage of decoding that
     * has been completed.
     */
    void thumbnailProgress(ImageWriter source, float percentageDone);

    /**
     * Reports that a thumbnail write operation has completed.  All
     * <code>ImageWriter</code> implementations are required to call
     * this method exactly once upon completion of each thumbnail
     * write operation.
     *
     * @param source the <code>ImageWriter</code> object calling this
     * method.
     */
    void thumbnailComplete(ImageWriter source);

    /**
     * Reports that a write has been aborted via the writer's
     * <code>abort</code> method.  No further notifications will be
     * given.
     *
     * @param source the <code>ImageWriter</code> object calling this
     * method.
     */
    void writeAborted(ImageWriter source);
}
