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
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import javax.swing.plaf.synth.*;
import javax.swing.plaf.synth.SynthLabelUI;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * Provides the Synth L&amp;F UI delegate for
 * {@link JList}.
 *
 * @author Scott Violet
 * @since 1.7
 */
public class SynthListUI extends BasicListUI
                         implements PropertyChangeListener, SynthUI {
    private SynthStyle style;
    private boolean useListColors;
    private boolean useUIBorder;

    /**
     * Creates a new UI object for the given component.
     *
     * @param list component to create UI object for
     * @return the UI object
     */
    public static ComponentUI createUI(JComponent list) {
        return new javax.swing.plaf.synth.SynthListUI();
    }

    /**
     * Notifies this UI delegate to repaint the specified component.
     * This method paints the component background, then calls
     * the {@link #paint} method.
     *
     * <p>In general, this method does not need to be overridden by subclasses.
     * All Look and Feel rendering code should reside in the {@code paint} method.
     *
     * @param g the {@code Graphics} object used for painting
     * @param c the component being painted
     * @see #paint
     */
    @Override
    public void update(Graphics g, JComponent c) {
        SynthContext context = getContext(c);

        SynthLookAndFeel.update(context, g);
        context.getPainter().paintListBackground(context,
                          g, 0, 0, c.getWidth(), c.getHeight());
        context.dispose();
        paint(g, c);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paintBorder(SynthContext context, Graphics g, int x,
                            int y, int w, int h) {
        context.getPainter().paintListBorder(context, g, x, y, w, h);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void installListeners() {
        super.installListeners();
        list.addPropertyChangeListener(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void propertyChange(PropertyChangeEvent e) {
        if (SynthLookAndFeel.shouldUpdateStyle(e)) {
            updateStyle((JList)e.getSource());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void uninstallListeners() {
        super.uninstallListeners();
        list.removePropertyChangeListener(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void installDefaults() {
        if (list.getCellRenderer() == null ||
                 (list.getCellRenderer() instanceof UIResource)) {
            list.setCellRenderer(new SynthListCellRenderer());
        }
        updateStyle(list);
    }

    private void updateStyle(JComponent c) {
        SynthContext context = getContext(list, ENABLED);
        SynthStyle oldStyle = style;

        style = SynthLookAndFeel.updateStyle(context, this);

        if (style != oldStyle) {
            context.setComponentState(SELECTED);
            Color sbg = list.getSelectionBackground();
            if (sbg == null || sbg instanceof UIResource) {
                list.setSelectionBackground(style.getColor(
                                 context, ColorType.TEXT_BACKGROUND));
            }

            Color sfg = list.getSelectionForeground();
            if (sfg == null || sfg instanceof UIResource) {
                list.setSelectionForeground(style.getColor(
                                 context, ColorType.TEXT_FOREGROUND));
            }

            useListColors = style.getBoolean(context,
                                  "List.rendererUseListColors", true);
            useUIBorder = style.getBoolean(context,
                                  "List.rendererUseUIBorder", true);

            int height = style.getInt(context, "List.cellHeight", -1);
            if (height != -1) {
                list.setFixedCellHeight(height);
            }
            if (oldStyle != null) {
                uninstallKeyboardActions();
                installKeyboardActions();
            }
        }
        context.dispose();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void uninstallDefaults() {
        super.uninstallDefaults();

        SynthContext context = getContext(list, ENABLED);

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
        return SynthLookAndFeel.getComponentState(c);
    }


    private class SynthListCellRenderer extends DefaultListCellRenderer.UIResource {
        @Override public String getName() {
            return "List.cellRenderer";
        }

        @Override public void setBorder(Border b) {
            if (useUIBorder || b instanceof SynthBorder) {
                super.setBorder(b);
            }
        }

        @Override public Component getListCellRendererComponent(JList list, Object value,
                  int index, boolean isSelected, boolean cellHasFocus) {
            if (!useListColors && (isSelected || cellHasFocus)) {
                SynthLookAndFeel.setSelectedUI((SynthLabelUI)SynthLookAndFeel.
                             getUIOfType(getUI(), SynthLabelUI.class),
                                   isSelected, cellHasFocus, list.isEnabled(), false);
            }
            else {
                SynthLookAndFeel.resetSelectedUI();
            }

            super.getListCellRendererComponent(list, value, index,
                                               isSelected, cellHasFocus);
            return this;
        }

        @Override public void paint(Graphics g) {
            super.paint(g);
            SynthLookAndFeel.resetSelectedUI();
        }
    }
}
