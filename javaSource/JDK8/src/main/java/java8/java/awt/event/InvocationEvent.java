/*
 * Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java8.java.awt.event;

import sun.awt.AWTAccessor;

import java.awt.*;
import java.awt.event.AWTEventListener;

/**
 * An event which executes the <code>run()</code> method on a <code>Runnable
 * </code> when dispatched by the AWT event dispatcher thread. This class can
 * be used as a reference implementation of <code>ActiveEvent</code> rather
 * than declaring a new class and defining <code>dispatch()</code>.<p>
 *
 * Instances of this class are placed on the <code>EventQueue</code> by calls
 * to <code>invokeLater</code> and <code>invokeAndWait</code>. Client code
 * can use this fact to write replacement functions for <code>invokeLater
 * </code> and <code>invokeAndWait</code> without writing special-case code
 * in any <code>AWTEventListener</code> objects.
 * <p>
 * An unspecified behavior will be caused if the {@code id} parameter
 * of any particular {@code InvocationEvent} instance is not
 * in the range from {@code INVOCATION_FIRST} to {@code INVOCATION_LAST}.
 *
 * @author      Fred Ecks
 * @author      David Mendenhall
 *
 * @see         ActiveEvent
 * @see         java.awt.EventQueue#invokeLater
 * @see         java.awt.EventQueue#invokeAndWait
 * @see         AWTEventListener
 *
 * @since       1.2
 */
public class InvocationEvent extends AWTEvent implements ActiveEvent {

    static {
        AWTAccessor.setInvocationEventAccessor(new AWTAccessor.InvocationEventAccessor() {
            @Override
            public void dispose(java.awt.event.InvocationEvent invocationEvent) {
                invocationEvent.finishedDispatching(false);
            }
        });
    }

    /**
     * Marks the first integer id for the range of invocation event ids.
     */
    public static final int INVOCATION_FIRST = 1200;

    /**
     * The default id for all InvocationEvents.
     */
    public static final int INVOCATION_DEFAULT = INVOCATION_FIRST;

    /**
     * Marks the last integer id for the range of invocation event ids.
     */
    public static final int INVOCATION_LAST = INVOCATION_DEFAULT;

    /**
     * The Runnable whose run() method will be called.
     */
    protected Runnable runnable;

    /**
     * The (potentially null) Object whose notifyAll() method will be called
     * immediately after the Runnable.run() method has returned or thrown an exception
     * or after the event was disposed.
     *
     * @see #isDispatched
     */
    protected volatile Object notifier;

    /**
     * The (potentially null) Runnable whose run() method will be called
     * immediately after the event was dispatched or disposed.
     *
     * @see #isDispatched
     * @since 1.8
     */
    private final Runnable listener;

    /**
     * Indicates whether the <code>run()</code> method of the <code>runnable</code>
     * was executed or not.
     *
     * @see #isDispatched
     * @since 1.7
     */
    private volatile boolean dispatched = false;

    /**
     * Set to true if dispatch() catches Throwable and stores it in the
     * exception instance variable. If false, Throwables are propagated up
     * to the EventDispatchThread's dispatch loop.
     */
    protected boolean catchExceptions;

    /**
     * The (potentially null) Exception thrown during execution of the
     * Runnable.run() method. This variable will also be null if a particular
     * instance does not catch exceptions.
     */
    private Exception exception = null;

    /**
     * The (potentially null) Throwable thrown during execution of the
     * Runnable.run() method. This variable will also be null if a particular
     * instance does not catch exceptions.
     */
    private Throwable throwable = null;

    /**
     * The timestamp of when this event occurred.
     *
     * @serial
     * @see #getWhen
     */
    private long when;

    /*
     * JDK 1.1 serialVersionUID.
     */
    private static final long serialVersionUID = 436056344909459450L;

    /**
     * Constructs an <code>InvocationEvent</code> with the specified
     * source which will execute the runnable's <code>run</code>
     * method when dispatched.
     * <p>This is a convenience constructor.  An invocation of the form
     * <tt>InvocationEvent(source, runnable)</tt>
     * behaves in exactly the same way as the invocation of
     * <tt>{@link #InvocationEvent(Object, Runnable, Object, boolean) InvocationEvent}(source, runnable, null, false)</tt>.
     * <p> This method throws an <code>IllegalArgumentException</code>
     * if <code>source</code> is <code>null</code>.
     *
     * @param source    The <code>Object</code> that originated the event
     * @param runnable  The <code>Runnable</code> whose <code>run</code>
     *                  method will be executed
     * @throws IllegalArgumentException if <code>source</code> is null
     *
     * @see #getSource()
     * @see #InvocationEvent(Object, Runnable, Object, boolean)
     */
    public InvocationEvent(Object source, Runnable runnable) {
        this(source, INVOCATION_DEFAULT, runnable, null, null, false);
    }

    /**
     * Constructs an <code>InvocationEvent</code> with the specified
     * source which will execute the runnable's <code>run</code>
     * method when dispatched.  If notifier is non-<code>null</code>,
     * <code>notifyAll()</code> will be called on it
     * immediately after <code>run</code> has returned or thrown an exception.
     * <p>An invocation of the form <tt>InvocationEvent(source,
     * runnable, notifier, catchThrowables)</tt>
     * behaves in exactly the same way as the invocation of
     * <tt>{@link #InvocationEvent(Object, int, Runnable, Object, boolean) InvocationEvent}(source, InvocationEvent.INVOCATION_DEFAULT, runnable, notifier, catchThrowables)</tt>.
     * <p>This method throws an <code>IllegalArgumentException</code>
     * if <code>source</code> is <code>null</code>.
     *
     * @param source            The <code>Object</code> that originated
     *                          the event
     * @param runnable          The <code>Runnable</code> whose
     *                          <code>run</code> method will be
     *                          executed
     * @param notifier          The {@code Object} whose <code>notifyAll</code>
     *                          method will be called after
     *                          <code>Runnable.run</code> has returned or
     *                          thrown an exception or after the event was
     *                          disposed
     * @param catchThrowables   Specifies whether <code>dispatch</code>
     *                          should catch Throwable when executing
     *                          the <code>Runnable</code>'s <code>run</code>
     *                          method, or should instead propagate those
     *                          Throwables to the EventDispatchThread's
     *                          dispatch loop
     * @throws IllegalArgumentException if <code>source</code> is null
     *
     * @see #getSource()
     * @see     #InvocationEvent(Object, int, Runnable, Object, boolean)
     */
    public InvocationEvent(Object source, Runnable runnable, Object notifier,
                           boolean catchThrowables) {
        this(source, INVOCATION_DEFAULT, runnable, notifier, null, catchThrowables);
    }

    /**
     * Constructs an <code>InvocationEvent</code> with the specified
     * source which will execute the runnable's <code>run</code>
     * method when dispatched.  If listener is non-<code>null</code>,
     * <code>listener.run()</code> will be called immediately after
     * <code>run</code> has returned, thrown an exception or the event
     * was disposed.
     * <p>This method throws an <code>IllegalArgumentException</code>
     * if <code>source</code> is <code>null</code>.
     *
     * @param source            The <code>Object</code> that originated
     *                          the event
     * @param runnable          The <code>Runnable</code> whose
     *                          <code>run</code> method will be
     *                          executed
     * @param listener          The <code>Runnable</code>Runnable whose
     *                          <code>run()</code> method will be called
     *                          after the {@code InvocationEvent}
     *                          was dispatched or disposed
     * @param catchThrowables   Specifies whether <code>dispatch</code>
     *                          should catch Throwable when executing
     *                          the <code>Runnable</code>'s <code>run</code>
     *                          method, or should instead propagate those
     *                          Throwables to the EventDispatchThread's
     *                          dispatch loop
     * @throws IllegalArgumentException if <code>source</code> is null
     */
    public InvocationEvent(Object source, Runnable runnable, Runnable listener,
                           boolean catchThrowables)  {
        this(source, INVOCATION_DEFAULT, runnable, null, listener, catchThrowables);
    }

    /**
     * Constructs an <code>InvocationEvent</code> with the specified
     * source and ID which will execute the runnable's <code>run</code>
     * method when dispatched.  If notifier is non-<code>null</code>,
     * <code>notifyAll</code> will be called on it immediately after
     * <code>run</code> has returned or thrown an exception.
     * <p>This method throws an
     * <code>IllegalArgumentException</code> if <code>source</code>
     * is <code>null</code>.
     *
     * @param source            The <code>Object</code> that originated
     *                          the event
     * @param id     An integer indicating the type of event.
     *                     For information on allowable values, see
     *                     the class description for {@link java.awt.event.InvocationEvent}
     * @param runnable          The <code>Runnable</code> whose
     *                          <code>run</code> method will be executed
     * @param notifier          The <code>Object</code> whose <code>notifyAll</code>
     *                          method will be called after
     *                          <code>Runnable.run</code> has returned or
     *                          thrown an exception or after the event was
     *                          disposed
     * @param catchThrowables   Specifies whether <code>dispatch</code>
     *                          should catch Throwable when executing the
     *                          <code>Runnable</code>'s <code>run</code>
     *                          method, or should instead propagate those
     *                          Throwables to the EventDispatchThread's
     *                          dispatch loop
     * @throws IllegalArgumentException if <code>source</code> is null
     * @see #getSource()
     * @see #getID()
     */
    protected InvocationEvent(Object source, int id, Runnable runnable,
                              Object notifier, boolean catchThrowables) {
        this(source, id, runnable, notifier, null, catchThrowables);
    }

    private InvocationEvent(Object source, int id, Runnable runnable,
                            Object notifier, Runnable listener, boolean catchThrowables) {
        super(source, id);
        this.runnable = runnable;
        this.notifier = notifier;
        this.listener = listener;
        this.catchExceptions = catchThrowables;
        this.when = System.currentTimeMillis();
    }
    /**
     * Executes the Runnable's <code>run()</code> method and notifies the
     * notifier (if any) when <code>run()</code> has returned or thrown an exception.
     *
     * @see #isDispatched
     */
    public void dispatch() {
        try {
            if (catchExceptions) {
                try {
                    runnable.run();
                }
                catch (Throwable t) {
                    if (t instanceof Exception) {
                        exception = (Exception) t;
                    }
                    throwable = t;
                }
            }
            else {
                runnable.run();
            }
        } finally {
            finishedDispatching(true);
        }
    }

    /**
     * Returns any Exception caught while executing the Runnable's <code>run()
     * </code> method.
     *
     * @return  A reference to the Exception if one was thrown; null if no
     *          Exception was thrown or if this InvocationEvent does not
     *          catch exceptions
     */
    public Exception getException() {
        return (catchExceptions) ? exception : null;
    }

    /**
     * Returns any Throwable caught while executing the Runnable's <code>run()
     * </code> method.
     *
     * @return  A reference to the Throwable if one was thrown; null if no
     *          Throwable was thrown or if this InvocationEvent does not
     *          catch Throwables
     * @since 1.5
     */
    public Throwable getThrowable() {
        return (catchExceptions) ? throwable : null;
    }

    /**
     * Returns the timestamp of when this event occurred.
     *
     * @return this event's timestamp
     * @since 1.4
     */
    public long getWhen() {
        return when;
    }

    /**
     * Returns {@code true} if the event is dispatched or any exception is
     * thrown while dispatching, {@code false} otherwise. The method should
     * be called by a waiting thread that calls the {@code notifier.wait()} method.
     * Since spurious wakeups are possible (as explained in {@link Object#wait()}),
     * this method should be used in a waiting loop to ensure that the event
     * got dispatched:
     * <pre>
     *     while (!event.isDispatched()) {
     *         notifier.wait();
     *     }
     * </pre>
     * If the waiting thread wakes up without dispatching the event,
     * the {@code isDispatched()} method returns {@code false}, and
     * the {@code while} loop executes once more, thus, causing
     * the awakened thread to revert to the waiting mode.
     * <p>
     * If the {@code notifier.notifyAll()} happens before the waiting thread
     * enters the {@code notifier.wait()} method, the {@code while} loop ensures
     * that the waiting thread will not enter the {@code notifier.wait()} method.
     * Otherwise, there is no guarantee that the waiting thread will ever be woken
     * from the wait.
     *
     * @return {@code true} if the event has been dispatched, or any exception
     * has been thrown while dispatching, {@code false} otherwise
     * @see #dispatch
     * @see #notifier
     * @see #catchExceptions
     * @since 1.7
     */
    public boolean isDispatched() {
        return dispatched;
    }

    /**
     * Called when the event was dispatched or disposed
     * @param dispatched true if the event was dispatched
     *                   false if the event was disposed
     */
    private void finishedDispatching(boolean dispatched) {
        this.dispatched = dispatched;

        if (notifier != null) {
            synchronized (notifier) {
                notifier.notifyAll();
            }
        }

        if (listener != null) {
            listener.run();
        }
    }

    /**
     * Returns a parameter string identifying this event.
     * This method is useful for event-logging and for debugging.
     *
     * @return  A string identifying the event and its attributes
     */
    public String paramString() {
        String typeStr;
        switch(id) {
            case INVOCATION_DEFAULT:
                typeStr = "INVOCATION_DEFAULT";
                break;
            default:
                typeStr = "unknown type";
        }
        return typeStr + ",runnable=" + runnable + ",notifier=" + notifier +
            ",catchExceptions=" + catchExceptions + ",when=" + when;
    }
}
