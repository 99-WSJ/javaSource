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
package java8.javax.swing.event;

import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import java.util.EventObject;
import java.awt.event.MouseEvent;
import java.awt.Component;


/**
 * MenuDragMouseEvent is used to notify interested parties that
 * the menu element has received a MouseEvent forwarded to it
 * under drag conditions.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running
 * the same version of Swing.  As of 1.4, support for long term storage
 * of all JavaBeans&trade;
 * has been added to the <code>java.beans</code> package.
 * Please see {@link java.beans.XMLEncoder}.
 *
 * @author Georges Saab
 */
@SuppressWarnings("serial")
public class MenuDragMouseEvent extends MouseEvent {
    private MenuElement path[];
    private MenuSelectionManager manager;

    /**
     * Constructs a MenuDragMouseEvent object.
     * <p>Absolute coordinates xAbs and yAbs are set to source's location on screen plus
     * relative coordinates x and y. xAbs and yAbs are set to zero if the source is not showing.
     *
     * @param source        the Component that originated the event
     *                      (typically <code>this</code>)
     * @param id            an int specifying the type of event, as defined
     *                      in {@link MouseEvent}
     * @param when          a long identifying the time the event occurred
     * @param modifiers     an int specifying any modifier keys held down,
     *                      as specified in {@link java.awt.event.InputEvent}
     * @param x             an int specifying the horizontal position at which
     *                      the event occurred, in pixels
     * @param y             an int specifying the vertical position at which
     *                      the event occurred, in pixels
     * @param clickCount    an int specifying the number of mouse-clicks
     * @param popupTrigger  a boolean -- true if the event {should?/did?}
     *                      trigger a popup
     * @param p             an array of MenuElement objects specifying a path
     *                        to a menu item affected by the drag
     * @param m             a MenuSelectionManager object that handles selections
     * @see MouseEvent#MouseEvent(Component, int, long, int, int, int, int, int, int, boolean, int)
     */
    public MenuDragMouseEvent(Component source, int id, long when,
                              int modifiers, int x, int y, int clickCount,
                              boolean popupTrigger, MenuElement p[],
                              MenuSelectionManager m) {
        super(source, id, when, modifiers, x, y, clickCount, popupTrigger);
        path = p;
        manager = m;
    }

    /**
     * Constructs a MenuDragMouseEvent object.
     * <p>Even if inconsistent values for relative and absolute coordinates are
     * passed to the constructor, the MenuDragMouseEvent instance is still
     * created.
     * @param source        the Component that originated the event
     *                      (typically <code>this</code>)
     * @param id            an int specifying the type of event, as defined
     *                      in {@link MouseEvent}
     * @param when          a long identifying the time the event occurred
     * @param modifiers     an int specifying any modifier keys held down,
     *                      as specified in {@link java.awt.event.InputEvent}
     * @param x             an int specifying the horizontal position at which
     *                      the event occurred, in pixels
     * @param y             an int specifying the vertical position at which
     *                      the event occurred, in pixels
     * @param xAbs          an int specifying the horizontal absolute position at which
     *                      the event occurred, in pixels
     * @param yAbs          an int specifying the vertical absolute position at which
     *                      the event occurred, in pixels
     * @param clickCount    an int specifying the number of mouse-clicks
     * @param popupTrigger  a boolean -- true if the event {should?/did?}
     *                      trigger a popup
     * @param p             an array of MenuElement objects specifying a path
     *                        to a menu item affected by the drag
     * @param m             a MenuSelectionManager object that handles selections
     * @see MouseEvent#MouseEvent(Component, int, long, int, int, int, int, int, int, boolean, int)
     * @since 1.6
     */
    public MenuDragMouseEvent(Component source, int id, long when,
                              int modifiers, int x, int y, int xAbs,
                              int yAbs, int clickCount,
                              boolean popupTrigger, MenuElement p[],
                              MenuSelectionManager m) {
        super(source, id, when, modifiers, x, y, xAbs, yAbs, clickCount,
              popupTrigger, MouseEvent.NOBUTTON);
        path = p;
        manager = m;
    }

    /**
     * Returns the path to the selected menu item.
     *
     * @return an array of MenuElement objects representing the path value
     */
    public MenuElement[] getPath() {
        return path;
    }

    /**
     * Returns the current menu selection manager.
     *
     * @return a MenuSelectionManager object
     */
    public MenuSelectionManager getMenuSelectionManager() {
        return manager;
    }
}
