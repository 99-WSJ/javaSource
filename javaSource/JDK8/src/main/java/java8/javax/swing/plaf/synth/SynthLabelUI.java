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

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import javax.swing.plaf.synth.*;
import javax.swing.text.View;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Insets;
import java.awt.Graphics;
import java.awt.FontMetrics;
import java.beans.PropertyChangeEvent;

/**
 * Provides the Synth L&amp;F UI delegate for
 * {@link JLabel}.
 *
 * @author Scott Violet
 * @since 1.7
 */
public class SynthLabelUI extends BasicLabelUI implements SynthUI {
    private SynthStyle style;

    /**
     * Returns the LabelUI implementation used for the skins look and feel.
     *
     * @param c component to create UI object for
     * @return the UI object
     */
    public static ComponentUI createUI(JComponent c){
        return new javax.swing.plaf.synth.SynthLabelUI();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void installDefaults(JLabel c) {
        updateStyle(c);
    }

    void updateStyle(JLabel c) {
        SynthContext context = getContext(c, ENABLED);
        style = SynthLookAndFeel.updateStyle(context, this);
        context.dispose();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void uninstallDefaults(JLabel c){
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
        return getContext(c, getComponentState(c));
    }

    private SynthContext getContext(JComponent c, int state) {
        return SynthContext.getContext(SynthContext.class, c,
                    SynthLookAndFeel.getRegion(c), style, state);
    }

    private int getComponentState(JComponent c) {
        int state = SynthLookAndFeel.getComponentState(c);
        if (SynthLookAndFeel.getSelectedUI() == this &&
                        state == SynthConstants.ENABLED) {
            state = SynthLookAndFeel.getSelectedUIState() | SynthConstants.ENABLED;
        }
        return state;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getBaseline(JComponent c, int width, int height) {
        if (c == null) {
            throw new NullPointerException("Component must be non-null");
        }
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException(
                    "Width and height must be >= 0");
        }
        JLabel label = (JLabel)c;
        String text = label.getText();
        if (text == null || "".equals(text)) {
            return -1;
        }
        Insets i = label.getInsets();
        Rectangle viewRect = new Rectangle();
        Rectangle textRect = new Rectangle();
        Rectangle iconRect = new Rectangle();
        viewRect.x = i.left;
        viewRect.y = i.top;
        viewRect.width = width - (i.right + viewRect.x);
        viewRect.height = height - (i.bottom + viewRect.y);

        // layout the text and icon
        SynthContext context = getContext(label);
        FontMetrics fm = context.getComponent().getFontMetrics(
            context.getStyle().getFont(context));
        context.getStyle().getGraphicsUtils(context).layoutText(
            context, fm, label.getText(), label.getIcon(),
            label.getHorizontalAlignment(), label.getVerticalAlignment(),
            label.getHorizontalTextPosition(), label.getVerticalTextPosition(),
            viewRect, iconRect, textRect, label.getIconTextGap());
        View view = (View)label.getClientProperty(BasicHTML.propertyKey);
        int baseline;
        if (view != null) {
            baseline = BasicHTML.getHTMLBaseline(view, textRect.width,
                                                 textRect.height);
            if (baseline >= 0) {
                baseline += textRect.y;
            }
        }
        else {
            baseline = textRect.y + fm.getAscent();
        }
        context.dispose();
        return baseline;
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
        context.getPainter().paintLabelBackground(context,
                          g, 0, 0, c.getWidth(), c.getHeight());
        paint(context, g);
        context.dispose();
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
     * Paints the specified component.
     *
     * @param context context for the component being painted
     * @param g the {@code Graphics} object used for painting
     * @see #update(Graphics,JComponent)
     */
    protected void paint(SynthContext context, Graphics g) {
        JLabel label = (JLabel)context.getComponent();
        Icon icon = (label.isEnabled()) ? label.getIcon() :
                                          label.getDisabledIcon();

        g.setColor(context.getStyle().getColor(context,
                                               ColorType.TEXT_FOREGROUND));
        g.setFont(style.getFont(context));
        context.getStyle().getGraphicsUtils(context).paintText(
            context, g, label.getText(), icon,
            label.getHorizontalAlignment(), label.getVerticalAlignment(),
            label.getHorizontalTextPosition(), label.getVerticalTextPosition(),
            label.getIconTextGap(), label.getDisplayedMnemonicIndex(), 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paintBorder(SynthContext context, Graphics g, int x,
                            int y, int w, int h) {
        context.getPainter().paintLabelBorder(context, g, x, y, w, h);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getPreferredSize(JComponent c) {
        JLabel label = (JLabel)c;
        Icon icon = (label.isEnabled()) ? label.getIcon() :
                                          label.getDisabledIcon();
        SynthContext context = getContext(c);
        Dimension size = context.getStyle().getGraphicsUtils(context).
            getPreferredSize(
               context, context.getStyle().getFont(context), label.getText(),
               icon, label.getHorizontalAlignment(),
               label.getVerticalAlignment(), label.getHorizontalTextPosition(),
               label.getVerticalTextPosition(), label.getIconTextGap(),
               label.getDisplayedMnemonicIndex());

        context.dispose();
        return size;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getMinimumSize(JComponent c) {
        JLabel label = (JLabel)c;
        Icon icon = (label.isEnabled()) ? label.getIcon() :
                                          label.getDisabledIcon();
        SynthContext context = getContext(c);
        Dimension size = context.getStyle().getGraphicsUtils(context).
            getMinimumSize(
               context, context.getStyle().getFont(context), label.getText(),
               icon, label.getHorizontalAlignment(),
               label.getVerticalAlignment(), label.getHorizontalTextPosition(),
               label.getVerticalTextPosition(), label.getIconTextGap(),
               label.getDisplayedMnemonicIndex());

        context.dispose();
        return size;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getMaximumSize(JComponent c) {
        JLabel label = (JLabel)c;
        Icon icon = (label.isEnabled()) ? label.getIcon() :
                                          label.getDisabledIcon();
        SynthContext context = getContext(c);
        Dimension size = context.getStyle().getGraphicsUtils(context).
               getMaximumSize(
               context, context.getStyle().getFont(context), label.getText(),
               icon, label.getHorizontalAlignment(),
               label.getVerticalAlignment(), label.getHorizontalTextPosition(),
               label.getVerticalTextPosition(), label.getIconTextGap(),
               label.getDisplayedMnemonicIndex());

        context.dispose();
        return size;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void propertyChange(PropertyChangeEvent e) {
        super.propertyChange(e);
        if (SynthLookAndFeel.shouldUpdateStyle(e)) {
            updateStyle((JLabel)e.getSource());
        }
    }
}
