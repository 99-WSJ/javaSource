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

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.synth.SynthContext;
import javax.swing.plaf.synth.SynthToggleButtonUI;

/**
 * Provides the Synth L&amp;F UI delegate for
 * {@link JRadioButton}.
 *
 * @author Jeff Dinkins
 * @since 1.7
 */
public class SynthRadioButtonUI extends SynthToggleButtonUI {

    // ********************************
    //        Create PLAF
    // ********************************
    /**
     * Creates a new UI object for the given component.
     *
     * @param b component to create UI object for
     * @return the UI object
     */
    public static ComponentUI createUI(JComponent b) {
        return new javax.swing.plaf.synth.SynthRadioButtonUI();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getPropertyPrefix() {
        return "RadioButton.";
    }

    /**
     * Returns the Icon used in calculating the
     * preferred/minimum/maximum size.
     */
    @Override
    protected Icon getSizingIcon(AbstractButton b) {
        return getIcon(b);
    }

    @Override
    void paintBackground(SynthContext context, Graphics g, JComponent c) {
        context.getPainter().paintRadioButtonBackground(context, g, 0, 0,
                                                c.getWidth(), c.getHeight());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paintBorder(SynthContext context, Graphics g, int x,
                            int y, int w, int h) {
        context.getPainter().paintRadioButtonBorder(context, g, x, y, w, h);
    }
}
