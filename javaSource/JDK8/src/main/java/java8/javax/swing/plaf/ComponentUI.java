/*
 * Copyright (c) 1997, 2011, Oracle and/or its affiliates. All rights reserved.
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

package java8.javax.swing.plaf;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.accessibility.Accessible;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;


/**
 * The base class for all UI delegate objects in the Swing pluggable
 * look and feel architecture.  The UI delegate object for a Swing
 * component is responsible for implementing the aspects of the
 * component that depend on the look and feel.
 * The <code>JComponent</code> class
 * invokes methods from this class in order to delegate operations
 * (painting, layout calculations, etc.) that may vary depending on the
 * look and feel installed.  <b>Client programs should not invoke methods
 * on this class directly.</b>
 *
 * @see JComponent
 * @see javax.swing.UIManager
 *
 */
public abstract class ComponentUI {
    /**
     * Sole constructor. (For invocation by subclass constructors,
     * typically implicit.)
     */
    public ComponentUI() {
    }

    /**
     * Configures the specified component appropriately for the look and feel.
     * This method is invoked when the <code>ComponentUI</code> instance is being installed
     * as the UI delegate on the specified component.  This method should
     * completely configure the component for the look and feel,
     * including the following:
     * <ol>
     * <li>Install default property values for color, fonts, borders,
     *     icons, opacity, etc. on the component.  Whenever possible,
     *     property values initialized by the client program should <i>not</i>
     *     be overridden.
     * <li>Install a <code>LayoutManager</code> on the component if necessary.
     * <li>Create/add any required sub-components to the component.
     * <li>Create/install event listeners on the component.
     * <li>Create/install a <code>PropertyChangeListener</code> on the component in order
     *     to detect and respond to component property changes appropriately.
     * <li>Install keyboard UI (mnemonics, traversal, etc.) on the component.
     * <li>Initialize any appropriate instance data.
     * </ol>
     * @param c the component where this UI delegate is being installed
     *
     * @see #uninstallUI
     * @see JComponent#setUI
     * @see JComponent#updateUI
     */
    public void installUI(JComponent c) {
    }

    /**
     * Reverses configuration which was done on the specified component during
     * <code>installUI</code>.  This method is invoked when this
     * <code>UIComponent</code> instance is being removed as the UI delegate
     * for the specified component.  This method should undo the
     * configuration performed in <code>installUI</code>, being careful to
     * leave the <code>JComponent</code> instance in a clean state (no
     * extraneous listeners, look-and-feel-specific property objects, etc.).
     * This should include the following:
     * <ol>
     * <li>Remove any UI-set borders from the component.
     * <li>Remove any UI-set layout managers on the component.
     * <li>Remove any UI-added sub-components from the component.
     * <li>Remove any UI-added event/property listeners from the component.
     * <li>Remove any UI-installed keyboard UI from the component.
     * <li>Nullify any allocated instance data objects to allow for GC.
     * </ol>
     * @param c the component from which this UI delegate is being removed;
     *          this argument is often ignored,
     *          but might be used if the UI object is stateless
     *          and shared by multiple components
     *
     * @see #installUI
     * @see JComponent#updateUI
     */
    public void uninstallUI(JComponent c) {
    }

    /**
     * Paints the specified component appropriately for the look and feel.
     * This method is invoked from the <code>ComponentUI.update</code> method when
     * the specified component is being painted.  Subclasses should override
     * this method and use the specified <code>Graphics</code> object to
     * render the content of the component.
     *
     * @param g the <code>Graphics</code> context in which to paint
     * @param c the component being painted;
     *          this argument is often ignored,
     *          but might be used if the UI object is stateless
     *          and shared by multiple components
     *
     * @see #update
     */
    public void paint(Graphics g, JComponent c) {
    }

    /**
     * Notifies this UI delegate that it is time to paint the specified
     * component.  This method is invoked by <code>JComponent</code>
     * when the specified component is being painted.
     *
     * <p>By default this method fills the specified component with
     * its background color if its {@code opaque} property is {@code true},
     * and then immediately calls {@code paint}. In general this method need
     * not be overridden by subclasses; all look-and-feel rendering code should
     * reside in the {@code paint} method.
     *
     * @param g the <code>Graphics</code> context in which to paint
     * @param c the component being painted;
     *          this argument is often ignored,
     *          but might be used if the UI object is stateless
     *          and shared by multiple components
     *
     * @see #paint
     * @see JComponent#paintComponent
     */
    public void update(Graphics g, JComponent c) {
        if (c.isOpaque()) {
            g.setColor(c.getBackground());
            g.fillRect(0, 0, c.getWidth(),c.getHeight());
        }
        paint(g, c);
    }

    /**
     * Returns the specified component's preferred size appropriate for
     * the look and feel.  If <code>null</code> is returned, the preferred
     * size will be calculated by the component's layout manager instead
     * (this is the preferred approach for any component with a specific
     * layout manager installed).  The default implementation of this
     * method returns <code>null</code>.
     *
     * @param c the component whose preferred size is being queried;
     *          this argument is often ignored,
     *          but might be used if the UI object is stateless
     *          and shared by multiple components
     *
     * @see JComponent#getPreferredSize
     * @see java.awt.LayoutManager#preferredLayoutSize
     */
    public Dimension getPreferredSize(JComponent c) {
        return null;
    }

    /**
     * Returns the specified component's minimum size appropriate for
     * the look and feel.  If <code>null</code> is returned, the minimum
     * size will be calculated by the component's layout manager instead
     * (this is the preferred approach for any component with a specific
     * layout manager installed).  The default implementation of this
     * method invokes <code>getPreferredSize</code> and returns that value.
     *
     * @param c the component whose minimum size is being queried;
     *          this argument is often ignored,
     *          but might be used if the UI object is stateless
     *          and shared by multiple components
     *
     * @return a <code>Dimension</code> object or <code>null</code>
     *
     * @see JComponent#getMinimumSize
     * @see java.awt.LayoutManager#minimumLayoutSize
     * @see #getPreferredSize
     */
    public Dimension getMinimumSize(JComponent c) {
        return getPreferredSize(c);
    }

    /**
     * Returns the specified component's maximum size appropriate for
     * the look and feel.  If <code>null</code> is returned, the maximum
     * size will be calculated by the component's layout manager instead
     * (this is the preferred approach for any component with a specific
     * layout manager installed).  The default implementation of this
     * method invokes <code>getPreferredSize</code> and returns that value.
     *
     * @param c the component whose maximum size is being queried;
     *          this argument is often ignored,
     *          but might be used if the UI object is stateless
     *          and shared by multiple components
     * @return a <code>Dimension</code> object or <code>null</code>
     *
     * @see JComponent#getMaximumSize
     * @see java.awt.LayoutManager2#maximumLayoutSize
     */
    public Dimension getMaximumSize(JComponent c) {
        return getPreferredSize(c);
    }

    /**
     * Returns <code>true</code> if the specified <i>x,y</i> location is
     * contained within the look and feel's defined shape of the specified
     * component. <code>x</code> and <code>y</code> are defined to be relative
     * to the coordinate system of the specified component.  Although
     * a component's <code>bounds</code> is constrained to a rectangle,
     * this method provides the means for defining a non-rectangular
     * shape within those bounds for the purpose of hit detection.
     *
     * @param c the component where the <i>x,y</i> location is being queried;
     *          this argument is often ignored,
     *          but might be used if the UI object is stateless
     *          and shared by multiple components
     * @param x the <i>x</i> coordinate of the point
     * @param y the <i>y</i> coordinate of the point
     *
     * @see JComponent#contains
     * @see Component#contains
     */
    @SuppressWarnings("deprecation")
    public boolean contains(JComponent c, int x, int y) {
        return c.inside(x, y);
    }

    /**
     * Returns an instance of the UI delegate for the specified component.
     * Each subclass must provide its own static <code>createUI</code>
     * method that returns an instance of that UI delegate subclass.
     * If the UI delegate subclass is stateless, it may return an instance
     * that is shared by multiple components.  If the UI delegate is
     * stateful, then it should return a new instance per component.
     * The default implementation of this method throws an error, as it
     * should never be invoked.
     */
    public static javax.swing.plaf.ComponentUI createUI(JComponent c) {
        throw new Error("ComponentUI.createUI not implemented.");
    }

    /**
     * Returns the baseline.  The baseline is measured from the top of
     * the component.  This method is primarily meant for
     * <code>LayoutManager</code>s to align components along their
     * baseline.  A return value less than 0 indicates this component
     * does not have a reasonable baseline and that
     * <code>LayoutManager</code>s should not align this component on
     * its baseline.
     * <p>
     * This method returns -1.  Subclasses that have a meaningful baseline
     * should override appropriately.
     *
     * @param c <code>JComponent</code> baseline is being requested for
     * @param width the width to get the baseline for
     * @param height the height to get the baseline for
     * @throws NullPointerException if <code>c</code> is <code>null</code>
     * @throws IllegalArgumentException if width or height is &lt; 0
     * @return baseline or a value &lt; 0 indicating there is no reasonable
     *                  baseline
     * @see JComponent#getBaseline(int,int)
     * @since 1.6
     */
    public int getBaseline(JComponent c, int width, int height) {
        if (c == null) {
            throw new NullPointerException("Component must be non-null");
        }
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException(
                    "Width and height must be >= 0");
        }
        return -1;
    }

    /**
     * Returns an enum indicating how the baseline of he component
     * changes as the size changes.  This method is primarily meant for
     * layout managers and GUI builders.
     * <p>
     * This method returns <code>BaselineResizeBehavior.OTHER</code>.
     * Subclasses that support a baseline should override appropriately.
     *
     * @param c <code>JComponent</code> to return baseline resize behavior for
     * @return an enum indicating how the baseline changes as the component
     *         size changes
     * @throws NullPointerException if <code>c</code> is <code>null</code>
     * @see JComponent#getBaseline(int, int)
     * @since 1.6
     */
    public Component.BaselineResizeBehavior getBaselineResizeBehavior(
            JComponent c) {
        if (c == null) {
            throw new NullPointerException("Component must be non-null");
        }
        return Component.BaselineResizeBehavior.OTHER;
    }

    /**
     * Returns the number of accessible children in the object.  If all
     * of the children of this object implement <code>Accessible</code>,
     * this
     * method should return the number of children of this object.
     * UIs might wish to override this if they present areas on the
     * screen that can be viewed as components, but actual components
     * are not used for presenting those areas.
     *
     * Note: As of v1.3, it is recommended that developers call
     * <code>Component.AccessibleAWTComponent.getAccessibleChildrenCount()</code> instead
     * of this method.
     *
     * @see #getAccessibleChild
     * @return the number of accessible children in the object
     */
    public int getAccessibleChildrenCount(JComponent c) {
        return SwingUtilities.getAccessibleChildrenCount(c);
    }

    /**
     * Returns the <code>i</code>th <code>Accessible</code> child of the object.
     * UIs might need to override this if they present areas on the
     * screen that can be viewed as components, but actual components
     * are not used for presenting those areas.
     *
     * <p>
     *
     * Note: As of v1.3, it is recommended that developers call
     * <code>Component.AccessibleAWTComponent.getAccessibleChild()</code> instead of
     * this method.
     *
     * @see #getAccessibleChildrenCount
     * @param i zero-based index of child
     * @return the <code>i</code>th <code>Accessible</code> child of the object
     */
    public Accessible getAccessibleChild(JComponent c, int i) {
        return SwingUtilities.getAccessibleChild(c, i);
    }
}
