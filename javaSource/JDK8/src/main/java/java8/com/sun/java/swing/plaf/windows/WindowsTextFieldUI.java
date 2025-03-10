/*
 * Copyright (c) 1997, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java8.sun.java.swing.plaf.windows;

import com.sun.java.swing.plaf.windows.WindowsTextUI;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.TextUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTextFieldUI;
import javax.swing.text.*;
import java.awt.*;


/**
 * Provides the Windows look and feel for a text field.  This
 * is basically the following customizations to the default
 * look-and-feel.
 * <ul>
 * <li>The border is beveled (using the standard control color).
 * <li>The background is white by default.
 * <li>The highlight color is a dark color, blue by default.
 * <li>The foreground color is high contrast in the selected
 *  area, white by default.  The unselected foreground is black.
 * <li>The cursor blinks at about 1/2 second intervals.
 * <li>The entire value is selected when focus is gained.
 * <li>Shift-left-arrow and shift-right-arrow extend selection
 * <li>Ctrl-left-arrow and ctrl-right-arrow act like home and
 *   end respectively.
 * </ul>
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @author  Timothy Prinzing
 */
public class WindowsTextFieldUI extends BasicTextFieldUI
{
    /**
     * Creates a UI for a JTextField.
     *
     * @param c the text field
     * @return the UI
     */
    public static ComponentUI createUI(JComponent c) {
        return new com.sun.java.swing.plaf.windows.WindowsTextFieldUI();
    }

    /**
     * Paints a background for the view.  This will only be
     * called if isOpaque() on the associated component is
     * true.  The default is to paint the background color
     * of the component.
     *
     * @param g the graphics context
     */
    protected void paintBackground(Graphics g) {
        super.paintBackground(g);
    }

    /**
     * Creates the caret for a field.
     *
     * @return the caret
     */
    protected Caret createCaret() {
        return new WindowsFieldCaret();
    }

    /**
     * WindowsFieldCaret has different scrolling behavior than
     * DefaultCaret.
     */
    static class WindowsFieldCaret extends DefaultCaret implements UIResource {

        public WindowsFieldCaret() {
            super();
        }

        /**
         * Adjusts the visibility of the caret according to
         * the windows feel which seems to be to move the
         * caret out into the field by about a quarter of
         * a field length if not visible.
         */
        protected void adjustVisibility(Rectangle r) {
            SwingUtilities.invokeLater(new SafeScroller(r));
        }

        /**
         * Gets the painter for the Highlighter.
         *
         * @return the painter
         */
        protected Highlighter.HighlightPainter getSelectionPainter() {
            return WindowsTextUI.WindowsPainter;
        }


        private class SafeScroller implements Runnable {
            SafeScroller(Rectangle r) {
                this.r = r;
            }

            public void run() {
                JTextField field = (JTextField) getComponent();
                if (field != null) {
                    TextUI ui = field.getUI();
                    int dot = getDot();
                    // PENDING: We need to expose the bias in DefaultCaret.
                    Position.Bias bias = Position.Bias.Forward;
                    Rectangle startRect = null;
                    try {
                        startRect = ui.modelToView(field, dot, bias);
                    } catch (BadLocationException ble) {}

                    Insets i = field.getInsets();
                    BoundedRangeModel vis = field.getHorizontalVisibility();
                    int x = r.x + vis.getValue() - i.left;
                    int quarterSpan = vis.getExtent() / 4;
                    if (r.x < i.left) {
                        vis.setValue(x - quarterSpan);
                    } else if (r.x + r.width > i.left + vis.getExtent()) {
                        vis.setValue(x - (3 * quarterSpan));
                    }
                    // If we scroll, our visual location will have changed,
                    // but we won't have updated our internal location as
                    // the model hasn't changed. This checks for the change,
                    // and if necessary, resets the internal location.
                    if (startRect != null) {
                        try {
                            Rectangle endRect;
                            endRect = ui.modelToView(field, dot, bias);
                            if (endRect != null && !endRect.equals(startRect)){
                                damage(endRect);
                            }
                        } catch (BadLocationException ble) {}
                    }
                }
            }

            private Rectangle r;
        }
    }

}
