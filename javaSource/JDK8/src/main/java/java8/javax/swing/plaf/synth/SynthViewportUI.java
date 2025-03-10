/*
 * Copyright (c) 2002, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java8.javax.swing.plaf.synth;

import java.beans.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.synth.Region;
import javax.swing.plaf.synth.SynthContext;
import javax.swing.plaf.synth.SynthLookAndFeel;
import javax.swing.plaf.synth.SynthStyle;
import javax.swing.plaf.synth.SynthUI;
import java.awt.*;


/**
 * Provides the Synth L&amp;F UI delegate for
 * {@link JViewport}.
 *
 * @since 1.7
 */
public class SynthViewportUI extends ViewportUI
                             implements PropertyChangeListener, SynthUI {
    private SynthStyle style;

    /**
     * Creates a new UI object for the given component.
     *
     * @param c component to create UI object for
     * @return the UI object
     */
    public static ComponentUI createUI(JComponent c) {
        return new javax.swing.plaf.synth.SynthViewportUI();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        installDefaults(c);
        installListeners(c);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);
        uninstallListeners(c);
        uninstallDefaults(c);
    }

    /**
     * Installs defaults for a viewport.
     *
     * @param c a {@code JViewport} object
     */
    protected void installDefaults(JComponent c) {
        updateStyle(c);
    }

    private void updateStyle(JComponent c) {
        SynthContext context = getContext(c, ENABLED);

        // Note: JViewport is special cased as it does not allow for
        // a border to be set. JViewport.setBorder is overriden to throw
        // an IllegalArgumentException. Refer to SynthScrollPaneUI for
        // details of this.
        SynthStyle newStyle = SynthLookAndFeel.getStyle(context.getComponent(),
                                                        context.getRegion());
        SynthStyle oldStyle = context.getStyle();

        if (newStyle != oldStyle) {
            if (oldStyle != null) {
                oldStyle.uninstallDefaults(context);
            }
            context.setStyle(newStyle);
            newStyle.installDefaults(context);
        }
        this.style = newStyle;
        context.dispose();
    }

    /**
     * Installs listeners into the viewport.
     *
     * @param c a {@code JViewport} object
     */
    protected void installListeners(JComponent c) {
        c.addPropertyChangeListener(this);
    }

    /**
     * Uninstalls listeners from the viewport.
     *
     * @param c a {@code JViewport} object
     */
    protected void uninstallListeners(JComponent c) {
        c.removePropertyChangeListener(this);
    }

    /**
     * Uninstalls defaults from a viewport.
     *
     * @param c a {@code JViewport} object
     */
    protected void uninstallDefaults(JComponent c) {
        SynthContext context = getContext(c, ENABLED);
        style.uninstallDefaults(context);
        context.dispose();
        style = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SynthContext getContext(JComponent c) {
        return getContext(c, SynthLookAndFeel.getComponentState(c));
    }

    private SynthContext getContext(JComponent c, int state) {
        return SynthContext.getContext(SynthContext.class, c,
                                       getRegion(c), style, state);
    }

    private Region getRegion(JComponent c) {
        return SynthLookAndFeel.getRegion(c);
    }

    /**
     * Notifies this UI delegate to repaint the specified component.
     * This method paints the component background, then calls
     * the {@link #paint(SynthContext,Graphics)} method.
     *
     * <p>In general, this method does not need to be overridden by subclasses.
     * All Look and Feel rendering code should reside in the {@code paint} method.
     *
     * @param g the {@code Graphics} object used for painting
     * @param c the component being painted
     * @see #paint(SynthContext,Graphics)
     */
    @Override
    public void update(Graphics g, JComponent c) {
        SynthContext context = getContext(c);

        SynthLookAndFeel.update(context, g);
        context.getPainter().paintViewportBackground(context,
                          g, 0, 0, c.getWidth(), c.getHeight());
        paint(context, g);
        context.dispose();
    }

    /**
     * Paints the border. The method is never called,
     * because the {@code JViewport} class does not support a border.
     * This implementation does nothing.
     *
     * @param context a component context
     * @param g the {@code Graphics} to paint on
     * @param x the X coordinate
     * @param y the Y coordinate
     * @param w width of the border
     * @param h height of the border
     */
    @Override
    public void paintBorder(SynthContext context, Graphics g, int x,
                            int y, int w, int h) {
    }

    /**
     * Paints the specified component according to the Look and Feel.
     * <p>This method is not used by Synth Look and Feel.
     * Painting is handled by the {@link #paint(SynthContext,Graphics)} method.
     *
     * @param g the {@code Graphics} object used for painting
     * @param c the component being painted
     * @see #paint(SynthContext,Graphics)
     */
    @Override
    public void paint(Graphics g, JComponent c) {
        SynthContext context = getContext(c);

        paint(context, g);
        context.dispose();
    }

    /**
     * Paints the specified component. This implementation does nothing.
     *
     * @param context context for the component being painted
     * @param g the {@code Graphics} object used for painting
     * @see #update(Graphics,JComponent)
     */
    protected void paint(SynthContext context, Graphics g) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void propertyChange(PropertyChangeEvent e) {
        if (SynthLookAndFeel.shouldUpdateStyle(e)) {
            updateStyle((JComponent)e.getSource());
        }
    }
}
