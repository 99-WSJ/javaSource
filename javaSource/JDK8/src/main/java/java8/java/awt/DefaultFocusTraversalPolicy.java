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
package java8.java.awt;

import java.awt.Container;
import java.awt.peer.ComponentPeer;


/**
 * A FocusTraversalPolicy that determines traversal order based on the order
 * of child Components in a Container. From a particular focus cycle root, the
 * policy makes a pre-order traversal of the Component hierarchy, and traverses
 * a Container's children according to the ordering of the array returned by
 * <code>Container.getComponents()</code>. Portions of the hierarchy that are
 * not visible and displayable will not be searched.
 * <p>
 * If client code has explicitly set the focusability of a Component by either
 * overriding <code>Component.isFocusTraversable()</code> or
 * <code>Component.isFocusable()</code>, or by calling
 * <code>Component.setFocusable()</code>, then a DefaultFocusTraversalPolicy
 * behaves exactly like a ContainerOrderFocusTraversalPolicy. If, however, the
 * Component is relying on default focusability, then a
 * DefaultFocusTraversalPolicy will reject all Components with non-focusable
 * peers. This is the default FocusTraversalPolicy for all AWT Containers.
 * <p>
 * The focusability of a peer is implementation-dependent. Sun recommends that
 * all implementations for a particular native platform construct peers with
 * the same focusability. The recommendations for Windows and Unix are that
 * Canvases, Labels, Panels, Scrollbars, ScrollPanes, Windows, and lightweight
 * Components have non-focusable peers, and all other Components have focusable
 * peers. These recommendations are used in the Sun AWT implementations. Note
 * that the focusability of a Component's peer is different from, and does not
 * impact, the focusability of the Component itself.
 * <p>
 * Please see
 * <a href="http://docs.oracle.com/javase/tutorial/uiswing/misc/focus.html">
 * How to Use the Focus Subsystem</a>,
 * a section in <em>The Java Tutorial</em>, and the
 * <a href="../../java/awt/doc-files/FocusSpec.html">Focus Specification</a>
 * for more information.
 *
 * @author David Mendenhall
 *
 * @see Container#getComponents
 * @see Component#isFocusable
 * @see Component#setFocusable
 * @since 1.4
 */
public class DefaultFocusTraversalPolicy
    extends ContainerOrderFocusTraversalPolicy
{
    /*
     * serialVersionUID
     */
    private static final long serialVersionUID = 8876966522510157497L;

    /**
     * Determines whether a Component is an acceptable choice as the new
     * focus owner. The Component must be visible, displayable, and enabled
     * to be accepted. If client code has explicitly set the focusability
     * of the Component by either overriding
     * <code>Component.isFocusTraversable()</code> or
     * <code>Component.isFocusable()</code>, or by calling
     * <code>Component.setFocusable()</code>, then the Component will be
     * accepted if and only if it is focusable. If, however, the Component is
     * relying on default focusability, then all Canvases, Labels, Panels,
     * Scrollbars, ScrollPanes, Windows, and lightweight Components will be
     * rejected.
     *
     * @param aComponent the Component whose fitness as a focus owner is to
     *        be tested
     * @return <code>true</code> if aComponent meets the above requirements;
     *         <code>false</code> otherwise
     */
    protected boolean accept(Component aComponent) {
        if (!(aComponent.isVisible() && aComponent.isDisplayable() &&
              aComponent.isEnabled()))
        {
            return false;
        }

        // Verify that the Component is recursively enabled. Disabling a
        // heavyweight Container disables its children, whereas disabling
        // a lightweight Container does not.
        if (!(aComponent instanceof Window)) {
            for (Container enableTest = aComponent.getParent();
                 enableTest != null;
                 enableTest = enableTest.getParent())
            {
                if (!(enableTest.isEnabled() || enableTest.isLightweight())) {
                    return false;
                }
                if (enableTest instanceof Window) {
                    break;
                }
            }
        }

        boolean focusable = aComponent.isFocusable();
        if (aComponent.isFocusTraversableOverridden()) {
            return focusable;
        }

        ComponentPeer peer = aComponent.getPeer();
        return (peer != null && peer.isFocusable());
    }
}
