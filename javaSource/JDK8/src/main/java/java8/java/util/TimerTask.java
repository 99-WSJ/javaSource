/*
 * Copyright (c) 1999, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java8.java.util;

import java.util.Date;
import java.util.Timer;

/**
 * A task that can be scheduled for one-time or repeated execution by a Timer.
 *
 * @author  Josh Bloch
 * @see     Timer
 * @since   1.3
 */

public abstract class TimerTask implements Runnable {
    /**
     * This object is used to control access to the TimerTask internals.
     */
    final Object lock = new Object();

    /**
     * The state of this task, chosen from the constants below.
     */
    int state = VIRGIN;

    /**
     * This task has not yet been scheduled.
     */
    static final int VIRGIN = 0;

    /**
     * This task is scheduled for execution.  If it is a non-repeating task,
     * it has not yet been executed.
     */
    static final int SCHEDULED   = 1;

    /**
     * This non-repeating task has already executed (or is currently
     * executing) and has not been cancelled.
     */
    static final int EXECUTED    = 2;

    /**
     * This task has been cancelled (with a call to TimerTask.cancel).
     */
    static final int CANCELLED   = 3;

    /**
     * Next execution time for this task in the format returned by
     * System.currentTimeMillis, assuming this task is scheduled for execution.
     * For repeating tasks, this field is updated prior to each task execution.
     */
    long nextExecutionTime;

    /**
     * Period in milliseconds for repeating tasks.  A positive value indicates
     * fixed-rate execution.  A negative value indicates fixed-delay execution.
     * A value of 0 indicates a non-repeating task.
     */
    long period = 0;

    /**
     * Creates a new timer task.
     */
    protected TimerTask() {
    }

    /**
     * The action to be performed by this timer task.
     */
    public abstract void run();

    /**
     * Cancels this timer task.  If the task has been scheduled for one-time
     * execution and has not yet run, or has not yet been scheduled, it will
     * never run.  If the task has been scheduled for repeated execution, it
     * will never run again.  (If the task is running when this call occurs,
     * the task will run to completion, but will never run again.)
     *
     * <p>Note that calling this method from within the <tt>run</tt> method of
     * a repeating timer task absolutely guarantees that the timer task will
     * not run again.
     *
     * <p>This method may be called repeatedly; the second and subsequent
     * calls have no effect.
     *
     * @return true if this task is scheduled for one-time execution and has
     *         not yet run, or this task is scheduled for repeated execution.
     *         Returns false if the task was scheduled for one-time execution
     *         and has already run, or if the task was never scheduled, or if
     *         the task was already cancelled.  (Loosely speaking, this method
     *         returns <tt>true</tt> if it prevents one or more scheduled
     *         executions from taking place.)
     */
    public boolean cancel() {
        synchronized(lock) {
            boolean result = (state == SCHEDULED);
            state = CANCELLED;
            return result;
        }
    }

    /**
     * Returns the <i>scheduled</i> execution time of the most recent
     * <i>actual</i> execution of this task.  (If this method is invoked
     * while task execution is in progress, the return value is the scheduled
     * execution time of the ongoing task execution.)
     *
     * <p>This method is typically invoked from within a task's run method, to
     * determine whether the current execution of the task is sufficiently
     * timely to warrant performing the scheduled activity:
     * <pre>{@code
     *   public void run() {
     *       if (System.currentTimeMillis() - scheduledExecutionTime() >=
     *           MAX_TARDINESS)
     *               return;  // Too late; skip this execution.
     *       // Perform the task
     *   }
     * }</pre>
     * This method is typically <i>not</i> used in conjunction with
     * <i>fixed-delay execution</i> repeating tasks, as their scheduled
     * execution times are allowed to drift over time, and so are not terribly
     * significant.
     *
     * @return the time at which the most recent execution of this task was
     *         scheduled to occur, in the format returned by Date.getTime().
     *         The return value is undefined if the task has yet to commence
     *         its first execution.
     * @see Date#getTime()
     */
    public long scheduledExecutionTime() {
        synchronized(lock) {
            return (period < 0 ? nextExecutionTime + period
                               : nextExecutionTime - period);
        }
    }
}
