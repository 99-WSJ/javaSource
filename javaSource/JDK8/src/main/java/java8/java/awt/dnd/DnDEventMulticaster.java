/*
 * Copyright (c) 2001, 2013, Oracle and/or its affiliates. All rights reserved.
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
package java8.java.awt.dnd;

import java.awt.*;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DragSourceMotionListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.EventListener;


/**
 * A class extends <code>AWTEventMulticaster</code> to implement efficient and
 * thread-safe multi-cast event dispatching for the drag-and-drop events defined
 * in the java.awt.dnd package.
 *
 * @since       1.4
 * @see AWTEventMulticaster
 */

class DnDEventMulticaster extends AWTEventMulticaster
    implements DragSourceListener, DragSourceMotionListener {

    /**
     * Creates an event multicaster instance which chains listener-a
     * with listener-b. Input parameters <code>a</code> and <code>b</code>
     * should not be <code>null</code>, though implementations may vary in
     * choosing whether or not to throw <code>NullPointerException</code>
     * in that case.
     *
     * @param a listener-a
     * @param b listener-b
     */
    protected DnDEventMulticaster(EventListener a, EventListener b) {
        super(a,b);
    }

    /**
     * Handles the <code>DragSourceDragEvent</code> by invoking
     * <code>dragEnter</code> on listener-a and listener-b.
     *
     * @param dsde the <code>DragSourceDragEvent</code>
     */
    public void dragEnter(DragSourceDragEvent dsde) {
        ((DragSourceListener)a).dragEnter(dsde);
        ((DragSourceListener)b).dragEnter(dsde);
    }

    /**
     * Handles the <code>DragSourceDragEvent</code> by invoking
     * <code>dragOver</code> on listener-a and listener-b.
     *
     * @param dsde the <code>DragSourceDragEvent</code>
     */
    public void dragOver(DragSourceDragEvent dsde) {
        ((DragSourceListener)a).dragOver(dsde);
        ((DragSourceListener)b).dragOver(dsde);
    }

    /**
     * Handles the <code>DragSourceDragEvent</code> by invoking
     * <code>dropActionChanged</code> on listener-a and listener-b.
     *
     * @param dsde the <code>DragSourceDragEvent</code>
     */
    public void dropActionChanged(DragSourceDragEvent dsde) {
        ((DragSourceListener)a).dropActionChanged(dsde);
        ((DragSourceListener)b).dropActionChanged(dsde);
    }

    /**
     * Handles the <code>DragSourceEvent</code> by invoking
     * <code>dragExit</code> on listener-a and listener-b.
     *
     * @param dse the <code>DragSourceEvent</code>
     */
    public void dragExit(DragSourceEvent dse) {
        ((DragSourceListener)a).dragExit(dse);
        ((DragSourceListener)b).dragExit(dse);
    }

    /**
     * Handles the <code>DragSourceDropEvent</code> by invoking
     * <code>dragDropEnd</code> on listener-a and listener-b.
     *
     * @param dsde the <code>DragSourceDropEvent</code>
     */
    public void dragDropEnd(DragSourceDropEvent dsde) {
        ((DragSourceListener)a).dragDropEnd(dsde);
        ((DragSourceListener)b).dragDropEnd(dsde);
    }

    /**
     * Handles the <code>DragSourceDragEvent</code> by invoking
     * <code>dragMouseMoved</code> on listener-a and listener-b.
     *
     * @param dsde the <code>DragSourceDragEvent</code>
     */
    public void dragMouseMoved(DragSourceDragEvent dsde) {
        ((DragSourceMotionListener)a).dragMouseMoved(dsde);
        ((DragSourceMotionListener)b).dragMouseMoved(dsde);
    }

    /**
     * Adds drag-source-listener-a with drag-source-listener-b and
     * returns the resulting multicast listener.
     *
     * @param a drag-source-listener-a
     * @param b drag-source-listener-b
     */
    public static DragSourceListener add(DragSourceListener a,
                                         DragSourceListener b) {
        return (DragSourceListener)addInternal(a, b);
    }

    /**
     * Adds drag-source-motion-listener-a with drag-source-motion-listener-b and
     * returns the resulting multicast listener.
     *
     * @param a drag-source-motion-listener-a
     * @param b drag-source-motion-listener-b
     */
    public static DragSourceMotionListener add(DragSourceMotionListener a,
                                               DragSourceMotionListener b) {
        return (DragSourceMotionListener)addInternal(a, b);
    }

    /**
     * Removes the old drag-source-listener from drag-source-listener-l
     * and returns the resulting multicast listener.
     *
     * @param l drag-source-listener-l
     * @param oldl the drag-source-listener being removed
     */
    public static DragSourceListener remove(DragSourceListener l,
                                            DragSourceListener oldl) {
        return (DragSourceListener)removeInternal(l, oldl);
    }

    /**
     * Removes the old drag-source-motion-listener from
     * drag-source-motion-listener-l and returns the resulting multicast
     * listener.
     *
     * @param l drag-source-motion-listener-l
     * @param ol the drag-source-motion-listener being removed
     */
    public static DragSourceMotionListener remove(DragSourceMotionListener l,
                                                  DragSourceMotionListener ol) {
        return (DragSourceMotionListener)removeInternal(l, ol);
    }

    /**
     * Returns the resulting multicast listener from adding listener-a
     * and listener-b together.
     * If listener-a is null, it returns listener-b;
     * If listener-b is null, it returns listener-a
     * If neither are null, then it creates and returns
     * a new AWTEventMulticaster instance which chains a with b.
     * @param a event listener-a
     * @param b event listener-b
     */
    protected static EventListener addInternal(EventListener a, EventListener b) {
        if (a == null)  return b;
        if (b == null)  return a;
        return new java.awt.dnd.DnDEventMulticaster(a, b);
    }

    /**
     * Removes a listener from this multicaster and returns the
     * resulting multicast listener.
     * @param oldl the listener to be removed
     */
    protected EventListener remove(EventListener oldl) {
        if (oldl == a)  return b;
        if (oldl == b)  return a;
        EventListener a2 = removeInternal(a, oldl);
        EventListener b2 = removeInternal(b, oldl);
        if (a2 == a && b2 == b) {
            return this;        // it's not here
        }
        return addInternal(a2, b2);
    }

    /**
     * Returns the resulting multicast listener after removing the
     * old listener from listener-l.
     * If listener-l equals the old listener OR listener-l is null,
     * returns null.
     * Else if listener-l is an instance of AWTEventMulticaster,
     * then it removes the old listener from it.
     * Else, returns listener l.
     * @param l the listener being removed from
     * @param oldl the listener being removed
     */
    protected static EventListener removeInternal(EventListener l, EventListener oldl) {
        if (l == oldl || l == null) {
            return null;
        } else if (l instanceof java.awt.dnd.DnDEventMulticaster) {
            return ((java.awt.dnd.DnDEventMulticaster)l).remove(oldl);
        } else {
            return l;           // it's not here
        }
    }

    protected static void save(ObjectOutputStream s, String k, EventListener l)
      throws IOException {
        AWTEventMulticaster.save(s, k, l);
    }
}
