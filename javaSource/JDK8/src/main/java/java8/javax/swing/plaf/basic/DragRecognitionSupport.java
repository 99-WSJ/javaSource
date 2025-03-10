/*
 * Copyright (c) 2005, 2008, Oracle and/or its affiliates. All rights reserved.
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
package java8.javax.swing.plaf.basic;

import java.awt.Toolkit;
import java.awt.event.*;
import java.awt.dnd.DragSource;
import javax.swing.*;
import sun.awt.dnd.SunDragSourceContextPeer;
import sun.awt.AppContext;

/**
 * Drag gesture recognition support for classes that have a
 * <code>TransferHandler</code>. The gesture for a drag in this class is a mouse
 * press followed by movement by <code>DragSource.getDragThreshold()</code>
 * pixels. An instance of this class is maintained per AppContext, and the
 * public static methods call into the appropriate instance.
 *
 * @author Shannon Hickey
 */
class DragRecognitionSupport {
    private int motionThreshold;
    private MouseEvent dndArmedEvent;
    private JComponent component;

    /**
     * This interface allows us to pass in a handler to mouseDragged,
     * so that we can be notified immediately before a drag begins.
     */
    public static interface BeforeDrag {
        public void dragStarting(MouseEvent me);
    }

    /**
     * Returns the DragRecognitionSupport for the caller's AppContext.
     */
    private static javax.swing.plaf.basic.DragRecognitionSupport getDragRecognitionSupport() {
        javax.swing.plaf.basic.DragRecognitionSupport support =
            (javax.swing.plaf.basic.DragRecognitionSupport)AppContext.getAppContext().
                get(javax.swing.plaf.basic.DragRecognitionSupport.class);

        if (support == null) {
            support = new javax.swing.plaf.basic.DragRecognitionSupport();
            AppContext.getAppContext().put(javax.swing.plaf.basic.DragRecognitionSupport.class, support);
        }

        return support;
    }

    /**
     * Returns whether or not the event is potentially part of a drag sequence.
     */
    public static boolean mousePressed(MouseEvent me) {
        return getDragRecognitionSupport().mousePressedImpl(me);
    }

    /**
     * If a dnd recognition has been going on, return the MouseEvent
     * that started the recognition. Otherwise, return null.
     */
    public static MouseEvent mouseReleased(MouseEvent me) {
        return getDragRecognitionSupport().mouseReleasedImpl(me);
    }

    /**
     * Returns whether or not a drag gesture recognition is ongoing.
     */
    public static boolean mouseDragged(MouseEvent me, BeforeDrag bd) {
        return getDragRecognitionSupport().mouseDraggedImpl(me, bd);
    }

    private void clearState() {
        dndArmedEvent = null;
        component = null;
    }

    private int mapDragOperationFromModifiers(MouseEvent me,
                                              TransferHandler th) {

        if (th == null || !SwingUtilities.isLeftMouseButton(me)) {
            return TransferHandler.NONE;
        }

        return SunDragSourceContextPeer.
            convertModifiersToDropAction(me.getModifiersEx(),
                                         th.getSourceActions(component));
    }

    /**
     * Returns whether or not the event is potentially part of a drag sequence.
     */
    private boolean mousePressedImpl(MouseEvent me) {
        component = (JComponent)me.getSource();

        if (mapDragOperationFromModifiers(me, component.getTransferHandler())
                != TransferHandler.NONE) {

            motionThreshold = DragSource.getDragThreshold();
            dndArmedEvent = me;
            return true;
        }

        clearState();
        return false;
    }

    /**
     * If a dnd recognition has been going on, return the MouseEvent
     * that started the recognition. Otherwise, return null.
     */
    private MouseEvent mouseReleasedImpl(MouseEvent me) {
        /* no recognition has been going on */
        if (dndArmedEvent == null) {
            return null;
        }

        MouseEvent retEvent = null;

        if (me.getSource() == component) {
            retEvent = dndArmedEvent;
        } // else component has changed unexpectedly, so return null

        clearState();
        return retEvent;
    }

    /**
     * Returns whether or not a drag gesture recognition is ongoing.
     */
    private boolean mouseDraggedImpl(MouseEvent me, BeforeDrag bd) {
        /* no recognition is in progress */
        if (dndArmedEvent == null) {
            return false;
        }

        /* component has changed unexpectedly, so bail */
        if (me.getSource() != component) {
            clearState();
            return false;
        }

        int dx = Math.abs(me.getX() - dndArmedEvent.getX());
        int dy = Math.abs(me.getY() - dndArmedEvent.getY());
        if ((dx > motionThreshold) || (dy > motionThreshold)) {
            TransferHandler th = component.getTransferHandler();
            int action = mapDragOperationFromModifiers(me, th);
            if (action != TransferHandler.NONE) {
                /* notify the BeforeDrag instance */
                if (bd != null) {
                    bd.dragStarting(dndArmedEvent);
                }
                th.exportAsDrag(component, dndArmedEvent, action);
                clearState();
            }
        }

        return true;
    }
}
