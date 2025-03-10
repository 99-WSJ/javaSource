/*
 * Copyright (c) 2002, 2006, Oracle and/or its affiliates. All rights reserved.
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
import javax.swing.plaf.synth.Region;
import javax.swing.plaf.synth.SynthContext;
import javax.swing.plaf.synth.SynthStyle;
import javax.swing.plaf.synth.SynthUI;
import javax.swing.text.JTextComponent;
import javax.swing.border.*;
import javax.swing.plaf.UIResource;

/**
 * SynthBorder is a border that delegates to a Painter. The Insets
 * are determined at construction time.
 *
 * @author Scott Violet
 */
class SynthBorder extends AbstractBorder implements UIResource {
    private SynthUI ui;
    private Insets insets;

    SynthBorder(SynthUI ui, Insets insets) {
        this.ui = ui;
        this.insets = insets;
    }

    SynthBorder(SynthUI ui) {
        this(ui, null);
    }

    public void paintBorder(Component c, Graphics g, int x, int y,
                            int width, int height) {
        JComponent jc = (JComponent)c;
        SynthContext context = ui.getContext(jc);
        SynthStyle style = context.getStyle();
        if (style == null) {
            assert false: "SynthBorder is being used outside after the UI " +
                          "has been uninstalled";
            return;
        }
        ui.paintBorder(context, g, x, y, width, height);
        context.dispose();
    }

    /**
     * Reinitializes the insets parameter with this Border's current Insets.
     * @param c the component for which this border insets value applies
     * @param insets the object to be reinitialized
     * @return the <code>insets</code> object
     */
    public Insets getBorderInsets(Component c, Insets insets) {
        if (this.insets != null) {
            if (insets == null) {
                insets = new Insets(this.insets.top, this.insets.left,
                                  this.insets.bottom, this.insets.right);
            }
            else {
                insets.top    = this.insets.top;
                insets.bottom = this.insets.bottom;
                insets.left   = this.insets.left;
                insets.right  = this.insets.right;
            }
        }
        else if (insets == null) {
            insets = new Insets(0, 0, 0, 0);
        }
        else {
            insets.top = insets.bottom = insets.left = insets.right = 0;
        }
        if (c instanceof JComponent) {
            Region region = Region.getRegion((JComponent)c);
            Insets margin = null;
            if ((region == Region.ARROW_BUTTON || region == Region.BUTTON ||
                 region == Region.CHECK_BOX ||
                 region == Region.CHECK_BOX_MENU_ITEM ||
                 region == Region.MENU || region == Region.MENU_ITEM ||
                 region == Region.RADIO_BUTTON ||
                 region == Region.RADIO_BUTTON_MENU_ITEM ||
                 region == Region.TOGGLE_BUTTON) &&
                       (c instanceof AbstractButton)) {
                margin = ((AbstractButton)c).getMargin();
            }
            else if ((region == Region.EDITOR_PANE ||
                      region == Region.FORMATTED_TEXT_FIELD ||
                      region == Region.PASSWORD_FIELD ||
                      region == Region.TEXT_AREA ||
                      region == Region.TEXT_FIELD ||
                      region == Region.TEXT_PANE) &&
                        (c instanceof JTextComponent)) {
                margin = ((JTextComponent)c).getMargin();
            }
            else if (region == Region.TOOL_BAR && (c instanceof JToolBar)) {
                margin = ((JToolBar)c).getMargin();
            }
            else if (region == Region.MENU_BAR && (c instanceof JMenuBar)) {
                margin = ((JMenuBar)c).getMargin();
            }
            if (margin != null) {
                insets.top += margin.top;
                insets.bottom += margin.bottom;
                insets.left += margin.left;
                insets.right += margin.right;
            }
        }
        return insets;
    }

    /**
     * This default implementation returns false.
     * @return false
     */
    public boolean isBorderOpaque() {
        return false;
    }
}
