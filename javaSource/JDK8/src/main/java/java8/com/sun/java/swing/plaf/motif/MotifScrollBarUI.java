/*
 * Copyright (c) 1997, 2003, Oracle and/or its affiliates. All rights reserved.
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
package java8.sun.java.swing.plaf.motif;

import com.sun.java.swing.plaf.motif.MotifScrollBarButton;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;


/**
 * Implementation of ScrollBarUI for the Motif Look and Feel
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @author Rich Schiavi
 * @author Hans Muller
 */
public class MotifScrollBarUI extends BasicScrollBarUI
{

    public static ComponentUI createUI(JComponent c) {
        return new com.sun.java.swing.plaf.motif.MotifScrollBarUI();
    }

    public Dimension getPreferredSize(JComponent c) {
        Insets insets = c.getInsets();
        int dx = insets.left + insets.right;
        int dy = insets.top + insets.bottom;
        return (scrollbar.getOrientation() == JScrollBar.VERTICAL)
            ? new Dimension(dx + 11, dy + 33)
            : new Dimension(dx + 33, dy + 11);
    }

    protected JButton createDecreaseButton(int orientation) {
        return new MotifScrollBarButton(orientation);
    }

    protected JButton createIncreaseButton(int orientation) {
        return new MotifScrollBarButton(orientation);
    }


    public void paintTrack(Graphics g, JComponent c, Rectangle trackBounds)  {
        g.setColor(trackColor);
        g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
    }


    public void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds)
    {

        if(thumbBounds.isEmpty() || !scrollbar.isEnabled())     {
            return;
        }

        int w = thumbBounds.width;
        int h = thumbBounds.height;

        g.translate(thumbBounds.x, thumbBounds.y);
        g.setColor(thumbColor);
        g.fillRect(0, 0, w-1, h-1);

        g.setColor(thumbHighlightColor);
        g.drawLine(0, 0, 0, h-1);
        g.drawLine(1, 0, w-1, 0);

        g.setColor(thumbLightShadowColor);
        g.drawLine(1, h-1, w-1, h-1);
        g.drawLine(w-1, 1, w-1, h-2);

        g.translate(-thumbBounds.x, -thumbBounds.y);
    }
}
