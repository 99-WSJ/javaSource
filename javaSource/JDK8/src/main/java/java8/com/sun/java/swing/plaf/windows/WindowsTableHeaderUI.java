/*
 * Copyright (c) 1997, 2010, Oracle and/or its affiliates. All rights reserved.
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

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;
import sun.swing.SwingUtilities2;
import sun.swing.table.DefaultTableCellHeaderRenderer;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;

import static com.sun.java.swing.plaf.windows.TMSchema.Part;
import static com.sun.java.swing.plaf.windows.TMSchema.State;
import static com.sun.java.swing.plaf.windows.XPStyle.Skin;


public class WindowsTableHeaderUI extends BasicTableHeaderUI {
    private TableCellRenderer originalHeaderRenderer;

    public static ComponentUI createUI(JComponent h) {
        return new com.sun.java.swing.plaf.windows.WindowsTableHeaderUI();
    }

    public void installUI(JComponent c) {
        super.installUI(c);

        if (XPStyle.getXP() != null) {
            originalHeaderRenderer = header.getDefaultRenderer();
            if (originalHeaderRenderer instanceof UIResource) {
                header.setDefaultRenderer(new XPDefaultRenderer());
            }
        }
    }

    public void uninstallUI(JComponent c) {
        if (header.getDefaultRenderer() instanceof XPDefaultRenderer) {
            header.setDefaultRenderer(originalHeaderRenderer);
        }
        super.uninstallUI(c);
    }

    @Override
    protected void rolloverColumnUpdated(int oldColumn, int newColumn) {
        if (XPStyle.getXP() != null) {
            header.repaint(header.getHeaderRect(oldColumn));
            header.repaint(header.getHeaderRect(newColumn));
        }
    }

    private class XPDefaultRenderer extends DefaultTableCellHeaderRenderer {
        Skin skin;
        boolean isSelected, hasFocus, hasRollover;
        int column;

        XPDefaultRenderer() {
            setHorizontalAlignment(LEADING);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected,
                                                hasFocus, row, column);
            this.isSelected = isSelected;
            this.hasFocus = hasFocus;
            this.column = column;
            this.hasRollover = (column == getRolloverColumn());
            if (skin == null) {
                skin = XPStyle.getXP().getSkin(header, Part.HP_HEADERITEM);
            }
            Insets margins = skin.getContentMargin();
            Border border = null;
            int contentTop = 0;
            int contentLeft = 0;
            int contentBottom = 0;
            int contentRight = 0;
            if (margins != null) {
                contentTop = margins.top;
                contentLeft = margins.left;
                contentBottom = margins.bottom;
                contentRight = margins.right;
            }
            /* idk:
             * Both on Vista and XP there is some offset to the
             * HP_HEADERITEM content. It does not seem to come from
             * Prop.CONTENTMARGINS. Do not know where it is defined.
             * using some hardcoded values.
             */
            contentLeft += 5;
            contentBottom += 4;
            contentRight += 5;

            /* On Vista sortIcon is painted above the header's text.
             * We use border to paint it.
             */
            Icon sortIcon;
            if (WindowsLookAndFeel.isOnVista()
                && ((sortIcon = getIcon()) instanceof javax.swing.plaf.UIResource
                    || sortIcon == null)) {
                contentTop += 1;
                setIcon(null);
                sortIcon = null;
                SortOrder sortOrder =
                    getColumnSortOrder(table, column);
                if (sortOrder != null) {
                    switch (sortOrder) {
                    case ASCENDING:
                        sortIcon =
                            UIManager.getIcon("Table.ascendingSortIcon");
                        break;
                    case DESCENDING:
                        sortIcon =
                            UIManager.getIcon("Table.descendingSortIcon");
                        break;
                    }
                }
                if (sortIcon != null) {
                    contentBottom = sortIcon.getIconHeight();
                    border = new IconBorder(sortIcon, contentTop, contentLeft,
                                            contentBottom, contentRight);
                } else {
                    sortIcon =
                        UIManager.getIcon("Table.ascendingSortIcon");
                    int sortIconHeight =
                        (sortIcon != null) ? sortIcon.getIconHeight() : 0;
                    if (sortIconHeight != 0) {
                        contentBottom = sortIconHeight;
                    }
                    border =
                        new EmptyBorder(
                            sortIconHeight + contentTop, contentLeft,
                            contentBottom, contentRight);
                }
            } else {
                contentTop += 3;
                border = new EmptyBorder(contentTop, contentLeft,
                                         contentBottom, contentRight);
            }
            setBorder(border);
            return this;
        }

        public void paint(Graphics g) {
            Dimension size = getSize();
            State state = State.NORMAL;
            TableColumn draggedColumn = header.getDraggedColumn();
            if (draggedColumn != null &&
                    column == SwingUtilities2.convertColumnIndexToView(
                            header.getColumnModel(), draggedColumn.getModelIndex())) {
                state = State.PRESSED;
            } else if (isSelected || hasFocus || hasRollover) {
                state = State.HOT;
            }
            /* on Vista there are more states for sorted columns */
            if (WindowsLookAndFeel.isOnVista()) {
                SortOrder sortOrder = getColumnSortOrder(header.getTable(), column);
                if (sortOrder != null) {
                     switch(sortOrder) {
                     case ASCENDING:
                         /* falls through */
                     case DESCENDING:
                         switch (state) {
                         case NORMAL:
                             state = State.SORTEDNORMAL;
                             break;
                         case PRESSED:
                             state = State.SORTEDPRESSED;
                             break;
                         case HOT:
                             state = State.SORTEDHOT;
                             break;
                         default:
                             /* do nothing */
                         }
                     default :
                         /* do nothing */
                     }
                }
            }
            skin.paintSkin(g, 0, 0, size.width-1, size.height-1, state);
            super.paint(g);
        }
    }

    /**
     * A border with an Icon at the middle of the top side.
     * Outer insets can be provided for this border.
     */
    private static class IconBorder implements Border, UIResource{
        private final Icon icon;
        private final int top;
        private final int left;
        private final int bottom;
        private final int right;
        /**
         * Creates this border;
         * @param icon - icon to paint for this border
         * @param top, left, bottom, right - outer insets for this border
         */
        public IconBorder(Icon icon, int top, int left,
                          int bottom, int right) {
            this.icon = icon;
            this.top = top;
            this.left = left;
            this.bottom = bottom;
            this.right = right;
        }
        public Insets getBorderInsets(Component c) {
            return new Insets(icon.getIconHeight() + top, left, bottom, right);
        }
        public boolean isBorderOpaque() {
            return false;
        }
        public void paintBorder(Component c, Graphics g, int x, int y,
                                int width, int height) {
            icon.paintIcon(c, g,
                x + left + (width - left - right - icon.getIconWidth()) / 2,
                y + top);
        }
    }
}
