/*
 * Copyright (c) 1998, 2010, Oracle and/or its affiliates. All rights reserved.
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

package java8.com.sun.java.swing.plaf.windows;

import java8.sun.java.swing.plaf.windows.XPStyle;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import java.awt.*;

import static com.sun.java.swing.plaf.windows.TMSchema.Part;
import static com.sun.java.swing.plaf.windows.TMSchema.State;
import static com.sun.java.swing.plaf.windows.XPStyle.Skin;

/**
 * Factory object that can vend Borders appropriate for the Windows 95 L & F.
 * @author Rich Schiavi
 */

public class WindowsBorders {

    /**
     * Returns a  border instance for a Windows Progress Bar
     * @since 1.4
     */
    public static Border getProgressBarBorder() {
        UIDefaults table = UIManager.getLookAndFeelDefaults();
        Border progressBarBorder = new BorderUIResource.CompoundBorderUIResource(
                                         new com.sun.java.swing.plaf.windows.WindowsBorders.ProgressBarBorder(
                                              table.getColor("ProgressBar.shadow"),
                                              table.getColor("ProgressBar.highlight")),
                                              new EmptyBorder(1,1,1,1)
                                        );
        return progressBarBorder;
    }

    /**
     * Returns a border instance for a Windows ToolBar
     *
     * @return a border used for the toolbar
     * @since 1.4
     */
    public static Border getToolBarBorder() {
        UIDefaults table = UIManager.getLookAndFeelDefaults();
        Border toolBarBorder = new com.sun.java.swing.plaf.windows.WindowsBorders.ToolBarBorder(
                                        table.getColor("ToolBar.shadow"),
                                        table.getColor("ToolBar.highlight"));
        return toolBarBorder;
    }

    /**
     * Returns an new instance of a border used to indicate which cell item
     * has focus.
     *
     * @return a border to indicate which cell item has focus
     * @since 1.4
     */
    public static Border getFocusCellHighlightBorder() {
        return new ComplementDashedBorder();
    }

    public static Border getTableHeaderBorder() {
        UIDefaults table = UIManager.getLookAndFeelDefaults();
        Border tableHeaderBorder = new BorderUIResource.CompoundBorderUIResource(
                           new BasicBorders.ButtonBorder(
                                           table.getColor("Table.shadow"),
                                           table.getColor("Table.darkShadow"),
                                           table.getColor("Table.light"),
                                           table.getColor("Table.highlight")),
                                     new BasicBorders.MarginBorder());
        return tableHeaderBorder;
    }

    public static Border getInternalFrameBorder() {
        UIDefaults table = UIManager.getLookAndFeelDefaults();
        Border internalFrameBorder = new
            BorderUIResource.CompoundBorderUIResource(
                BorderFactory.createBevelBorder(BevelBorder.RAISED,
                    table.getColor("InternalFrame.borderColor"),
                    table.getColor("InternalFrame.borderHighlight"),
                    table.getColor("InternalFrame.borderDarkShadow"),
                    table.getColor("InternalFrame.borderShadow")),
                new com.sun.java.swing.plaf.windows.WindowsBorders.InternalFrameLineBorder(
                    table.getColor("InternalFrame.activeBorderColor"),
                    table.getColor("InternalFrame.inactiveBorderColor"),
                    table.getInt("InternalFrame.borderWidth")));

        return internalFrameBorder;
    }

    public static class ProgressBarBorder extends AbstractBorder implements UIResource {
        protected Color shadow;
        protected Color highlight;

        public ProgressBarBorder(Color shadow, Color highlight) {
            this.highlight = highlight;
            this.shadow = shadow;
        }

        public void paintBorder(Component c, Graphics g, int x, int y,
                                int width, int height) {
            g.setColor(shadow);
            g.drawLine(x,y, width-1,y); // draw top
            g.drawLine(x,y, x,height-1); // draw left
            g.setColor(highlight);
            g.drawLine(x,height-1, width-1,height-1); // draw bottom
            g.drawLine(width-1,y, width-1,height-1); // draw right
        }

        public Insets getBorderInsets(Component c, Insets insets) {
            insets.set(1,1,1,1);
            return insets;
        }
    }

    /**
     * A border for the ToolBar. If the ToolBar is floatable then the handle grip is drawn
     * <p>
     * @since 1.4
     */
    public static class ToolBarBorder extends AbstractBorder implements UIResource, SwingConstants {
        protected Color shadow;
        protected Color highlight;

        public ToolBarBorder(Color shadow, Color highlight) {
            this.highlight = highlight;
            this.shadow = shadow;
        }

        public void paintBorder(Component c, Graphics g, int x, int y,
                                int width, int height) {
            if (!(c instanceof JToolBar)) {
                return;
            }
            g.translate(x, y);

            XPStyle xp = XPStyle.getXP();
            if (xp != null) {
                Border xpBorder = xp.getBorder(c, Part.TP_TOOLBAR);
                if (xpBorder != null) {
                    xpBorder.paintBorder(c, g, 0, 0, width, height);
                }
            }
            if (((JToolBar)c).isFloatable()) {
                boolean vertical = ((JToolBar)c).getOrientation() == VERTICAL;

                if (xp != null) {
                    Part part = vertical ? Part.RP_GRIPPERVERT : Part.RP_GRIPPER;
                    Skin skin = xp.getSkin(c, part);
                    int dx, dy, dw, dh;
                    if (vertical) {
                        dx = 0;
                        dy = 2;
                        dw = width - 1;
                        dh = skin.getHeight();
                    } else {
                        dw = skin.getWidth();
                        dh = height - 1;
                        dx = c.getComponentOrientation().isLeftToRight() ? 2 : (width-dw-2);
                        dy = 0;
                    }
                    skin.paintSkin(g, dx, dy, dw, dh, State.NORMAL);

                } else {

                    if (!vertical) {
                        if (c.getComponentOrientation().isLeftToRight()) {
                            g.setColor(shadow);
                            g.drawLine(4, 3, 4, height - 4);
                            g.drawLine(4, height - 4, 2, height - 4);

                            g.setColor(highlight);
                            g.drawLine(2, 3, 3, 3);
                            g.drawLine(2, 3, 2, height - 5);
                        } else {
                            g.setColor(shadow);
                            g.drawLine(width - 3, 3, width - 3, height - 4);
                            g.drawLine(width - 4, height - 4, width - 4, height - 4);

                            g.setColor(highlight);
                            g.drawLine(width - 5, 3, width - 4, 3);
                            g.drawLine(width - 5, 3, width - 5, height - 5);
                        }
                    } else { // Vertical
                        g.setColor(shadow);
                        g.drawLine(3, 4, width - 4, 4);
                        g.drawLine(width - 4, 2, width - 4, 4);

                        g.setColor(highlight);
                        g.drawLine(3, 2, width - 4, 2);
                        g.drawLine(3, 2, 3, 3);
                    }
                }
            }

            g.translate(-x, -y);
        }

        public Insets getBorderInsets(Component c, Insets insets) {
            insets.set(1,1,1,1);
            if (!(c instanceof JToolBar)) {
                return insets;
            }
            if (((JToolBar)c).isFloatable()) {
                int gripInset = (XPStyle.getXP() != null) ? 12 : 9;
                if (((JToolBar)c).getOrientation() == HORIZONTAL) {
                    if (c.getComponentOrientation().isLeftToRight()) {
                        insets.left = gripInset;
                    } else {
                        insets.right = gripInset;
                    }
                } else {
                    insets.top = gripInset;
                }
            }
            return insets;
        }
    }

    /**
     * This class is an implementation of a dashed border.
     * @since 1.4
     */
    public static class DashedBorder extends LineBorder implements UIResource {
        public DashedBorder(Color color) {
            super(color);
        }

        public DashedBorder(Color color, int thickness)  {
            super(color, thickness);
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Color oldColor = g.getColor();
            int i;

            g.setColor(lineColor);
            for(i = 0; i < thickness; i++)  {
                BasicGraphicsUtils.drawDashedRect(g, x+i, y+i, width-i-i, height-i-i);
            }
            g.setColor(oldColor);
        }
    }

    /**
     * A dashed border that paints itself in the complementary color
     * of the component's background color.
     */
    static class ComplementDashedBorder extends LineBorder implements UIResource {
        private Color origColor;
        private Color paintColor;

        public ComplementDashedBorder() {
            super(null);
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Color color = c.getBackground();

            if (origColor != color) {
                origColor = color;
                paintColor = new Color(~origColor.getRGB());
            }

            g.setColor(paintColor);
            BasicGraphicsUtils.drawDashedRect(g, x, y, width, height);
        }
    }

    /**
     * This class is an implementation of the InternalFrameLine border.
     * @since 1.4
     */
    public static class InternalFrameLineBorder extends LineBorder implements
            UIResource {
        protected Color activeColor;
        protected Color inactiveColor;

        public InternalFrameLineBorder(Color activeBorderColor,
                                       Color inactiveBorderColor,
                                       int thickness) {
            super(activeBorderColor, thickness);
            activeColor = activeBorderColor;
            inactiveColor = inactiveBorderColor;
        }

        public void paintBorder(Component c, Graphics g, int x, int y,
                int width, int height) {

            JInternalFrame jif = null;
            if (c instanceof JInternalFrame) {
                jif = (JInternalFrame)c;
            } else if (c instanceof JInternalFrame.JDesktopIcon) {
                jif = ((JInternalFrame.JDesktopIcon)c).getInternalFrame();
            } else {
                return;
            }

            if (jif.isSelected()) {
                // Set the line color so the line border gets the correct
                // color.
                lineColor = activeColor;
                super.paintBorder(c, g, x, y, width, height);
            } else {
                lineColor = inactiveColor;
                super.paintBorder(c, g, x, y, width, height);
            }
        }
    }
}
