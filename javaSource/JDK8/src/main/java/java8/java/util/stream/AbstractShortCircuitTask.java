/*
 * Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
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
package java8.java.util.stream;

import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Abstract class for fork-join tasks used to implement short-circuiting
 * stream ops, which can produce a result without processing all elements of the
 * stream.
 *
 * @param <P_IN> type of input elements to the pipeline
 * @param <P_OUT> type of output elements from the pipeline
 * @param <R> type of intermediate result, may be different from operation
 *        result type
 * @param <K> type of child and sibling tasks
 * @since 1.8
 */
@SuppressWarnings("serial")
abstract class AbstractShortCircuitTask<P_IN, P_OUT, R,
                                        K extends java.util.stream.AbstractShortCircuitTask<P_IN, P_OUT, R, K>>
        extends java.util.stream.AbstractTask<P_IN, P_OUT, R, K> {
    /**
     * The result for this computation; this is shared among all tasks and set
     * exactly once
     */
    protected final AtomicReference<R> sharedResult;

    /**
     * Indicates whether this task has been canceled.  Tasks may cancel other
     * tasks in the computation under various conditions, such as in a
     * find-first operation, a task that finds a value will cancel all tasks
     * that are later in the encounter order.
     */
    protected volatile boolean canceled;

    /**
     * Constructor for root tasks.
     *
     * @param helper the {@code PipelineHelper} describing the stream pipeline
     *               up to this operation
     * @param spliterator the {@code Spliterator} describing the source for this
     *                    pipeline
     */
    protected AbstractShortCircuitTask(PipelineHelper<P_OUT> helper,
                                       Spliterator<P_IN> spliterator) {
        super(helper, spliterator);
        sharedResult = new AtomicReference<>(null);
    }

    /**
     * Constructor for non-root nodes.
     *
     * @param parent parent task in the computation tree
     * @param spliterator the {@code Spliterator} for the portion of the
     *                    computation tree described by this task
     */
    protected AbstractShortCircuitTask(K parent,
                                       Spliterator<P_IN> spliterator) {
        super(parent, spliterator);
        sharedResult = parent.sharedResult;
    }

    /**
     * Returns the value indicating the computation completed with no task
     * finding a short-circuitable result.  For example, for a "find" operation,
     * this might be null or an empty {@code Optional}.
     *
     * @return the result to return when no task finds a result
     */
    protected abstract R getEmptyResult();

    /**
     * Overrides AbstractTask version to include checks for early
     * exits while splitting or computing.
     */
    @Override
    public void compute() {
        Spliterator<P_IN> rs = spliterator, ls;
        long sizeEstimate = rs.estimateSize();
        long sizeThreshold = getTargetSize(sizeEstimate);
        boolean forkRight = false;
        @SuppressWarnings("unchecked") K task = (K) this;
        AtomicReference<R> sr = sharedResult;
        R result;
        while ((result = sr.get()) == null) {
            if (task.taskCanceled()) {
                result = task.getEmptyResult();
                break;
            }
            if (sizeEstimate <= sizeThreshold || (ls = rs.trySplit()) == null) {
                result = task.doLeaf();
                break;
            }
            K leftChild, rightChild, taskToFork;
            task.leftChild  = leftChild = task.makeChild(ls);
            task.rightChild = rightChild = task.makeChild(rs);
            task.setPendingCount(1);
            if (forkRight) {
                forkRight = false;
                rs = ls;
                task = leftChild;
                taskToFork = rightChild;
            }
            else {
                forkRight = true;
                task = rightChild;
                taskToFork = leftChild;
            }
            taskToFork.fork();
            sizeEstimate = rs.estimateSize();
        }
        task.setLocalResult(result);
        task.tryComplete();
    }


    /**
     * Declares that a globally valid result has been found.  If another task has
     * not already found the answer, the result is installed in
     * {@code sharedResult}.  The {@code compute()} method will check
     * {@code sharedResult} before proceeding with computation, so this causes
     * the computation to terminate early.
     *
     * @param result the result found
     */
    protected void shortCircuit(R result) {
        if (result != null)
            sharedResult.compareAndSet(null, result);
    }

    /**
     * Sets a local result for this task.  If this task is the root, set the
     * shared result instead (if not already set).
     *
     * @param localResult The result to set for this task
     */
    @Override
    protected void setLocalResult(R localResult) {
        if (isRoot()) {
            if (localResult != null)
                sharedResult.compareAndSet(null, localResult);
        }
        else
            super.setLocalResult(localResult);
    }

    /**
     * Retrieves the local result for this task
     */
    @Override
    public R getRawResult() {
        return getLocalResult();
    }

    /**
     * Retrieves the local result for this task.  If this task is the root,
     * retrieves the shared result instead.
     */
    @Override
    public R getLocalResult() {
        if (isRoot()) {
            R answer = sharedResult.get();
            return (answer == null) ? getEmptyResult() : answer;
        }
        else
            return super.getLocalResult();
    }

    /**
     * Mark this task as canceled
     */
    protected void cancel() {
        canceled = true;
    }

    /**
     * Queries whether this task is canceled.  A task is considered canceled if
     * it or any of its parents have been canceled.
     *
     * @return {@code true} if this task or any parent is canceled.
     */
    protected boolean taskCanceled() {
        boolean cancel = canceled;
        if (!cancel) {
            for (K parent = getParent(); !cancel && parent != null; parent = parent.getParent())
                cancel = parent.canceled;
        }

        return cancel;
    }

    /**
     * Cancels all tasks which succeed this one in the encounter order.  This
     * includes canceling all the current task's right sibling, as well as the
     * later right siblings of all its parents.
     */
    protected void cancelLaterNodes() {
        // Go up the tree, cancel right siblings of this node and all parents
        for (@SuppressWarnings("unchecked") K parent = getParent(), node = (K) this;
             parent != null;
             node = parent, parent = parent.getParent()) {
            // If node is a left child of parent, then has a right sibling
            if (parent.leftChild == node) {
                K rightSibling = parent.rightChild;
                if (!rightSibling.canceled)
                    rightSibling.cancel();
            }
        }
    }
}
