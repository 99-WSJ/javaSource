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

package java8.sun.java.swing.plaf.motif;

import com.sun.java.swing.plaf.motif.MotifGraphicsUtils;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;

/**
 * Class that manages a Motif title bar
 *
 * @since 1.3
 */
public class MotifInternalFrameTitlePane
    extends BasicInternalFrameTitlePane implements LayoutManager, ActionListener, PropertyChangeListener
{
    SystemButton systemButton;
    MinimizeButton minimizeButton;
    MaximizeButton maximizeButton;
    JPopupMenu systemMenu;
    Title title;
    Color color;
    Color highlight;
    Color shadow;

    // The width and height of a title pane button
    public final static int BUTTON_SIZE = 19;  // 17 + 1 pixel border


    public MotifInternalFrameTitlePane(JInternalFrame frame) {
        super(frame);
    }

    protected void installDefaults() {
        setFont(UIManager.getFont("InternalFrame.titleFont"));
        setPreferredSize(new Dimension(100, BUTTON_SIZE));
    }

    protected void uninstallListeners() {
        // Get around protected method in superclass
        super.uninstallListeners();
    }

    protected PropertyChangeListener createPropertyChangeListener() {
        return this;
    }

    protected LayoutManager createLayout() {
        return this;
    }

    JPopupMenu getSystemMenu() {
        return systemMenu;
    }

    protected void assembleSystemMenu() {
        systemMenu = new JPopupMenu();
        JMenuItem mi = systemMenu.add(restoreAction);
        mi.setMnemonic(getButtonMnemonic("restore"));
        mi = systemMenu.add(moveAction);
        mi.setMnemonic(getButtonMnemonic("move"));
        mi = systemMenu.add(sizeAction);
        mi.setMnemonic(getButtonMnemonic("size"));
        mi = systemMenu.add(iconifyAction);
        mi.setMnemonic(getButtonMnemonic("minimize"));
        mi = systemMenu.add(maximizeAction);
        mi.setMnemonic(getButtonMnemonic("maximize"));
        systemMenu.add(new JSeparator());
        mi = systemMenu.add(closeAction);
        mi.setMnemonic(getButtonMnemonic("close"));

        systemButton = new SystemButton();
        systemButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                systemMenu.show(systemButton, 0, BUTTON_SIZE);
            }
        });

        systemButton.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                try {
                    frame.setSelected(true);
                } catch (PropertyVetoException pve) {
                }
                if ((evt.getClickCount() == 2)) {
                    closeAction.actionPerformed(new
                        ActionEvent(evt.getSource(),
                            ActionEvent.ACTION_PERFORMED,
                            null, evt.getWhen(), 0));
                    systemMenu.setVisible(false);
                }
            }
        });
    }

    private static int getButtonMnemonic(String button) {
        try {
            return Integer.parseInt(UIManager.getString(
                    "InternalFrameTitlePane." + button + "Button.mnemonic"));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    protected void createButtons() {
        minimizeButton = new MinimizeButton();
        minimizeButton.addActionListener(iconifyAction);

        maximizeButton = new MaximizeButton();
        maximizeButton.addActionListener(maximizeAction);
    }


    protected void addSubComponents() {
        title = new Title(frame.getTitle());
        title.setFont(getFont());

        add(systemButton);
        add(title);
        add(minimizeButton);
        add(maximizeButton);
    }

    public void paintComponent(Graphics g) {
    }

    void setColors(Color c, Color h, Color s) {
        color = c;
        highlight = h;
        shadow = s;
    }

    public void actionPerformed(ActionEvent e) {
    }

    public void propertyChange(PropertyChangeEvent evt) {
        String prop = evt.getPropertyName();
        JInternalFrame f = (JInternalFrame)evt.getSource();
        boolean value = false;
        if (JInternalFrame.IS_SELECTED_PROPERTY.equals(prop)) {
            repaint();
        } else if (prop.equals("maximizable")) {
            if ((Boolean)evt.getNewValue() == Boolean.TRUE)
                add(maximizeButton);
            else
                remove(maximizeButton);
            revalidate();
            repaint();
        } else if (prop.equals("iconable")) {
            if ((Boolean)evt.getNewValue() == Boolean.TRUE)
                add(minimizeButton);
            else
                remove(minimizeButton);
            revalidate();
            repaint();
        } else if (prop.equals(JInternalFrame.TITLE_PROPERTY)) {
            repaint();
        }
        enableActions();
    }

    public void addLayoutComponent(String name, Component c) {}
    public void removeLayoutComponent(Component c) {}
    public Dimension preferredLayoutSize(Container c)  {
        return minimumLayoutSize(c);
    }

    public Dimension minimumLayoutSize(Container c) {
        return new Dimension(100, BUTTON_SIZE);
    }

    public void layoutContainer(Container c) {
        int w = getWidth();
        systemButton.setBounds(0, 0, BUTTON_SIZE, BUTTON_SIZE);
        int x = w - BUTTON_SIZE;

        if(frame.isMaximizable()) {
            maximizeButton.setBounds(x, 0, BUTTON_SIZE, BUTTON_SIZE);
            x -= BUTTON_SIZE;
        } else if(maximizeButton.getParent() != null) {
            maximizeButton.getParent().remove(maximizeButton);
        }

        if(frame.isIconifiable()) {
            minimizeButton.setBounds(x, 0, BUTTON_SIZE, BUTTON_SIZE);
            x -= BUTTON_SIZE;
        } else if(minimizeButton.getParent() != null) {
            minimizeButton.getParent().remove(minimizeButton);
        }

        title.setBounds(BUTTON_SIZE, 0, x, BUTTON_SIZE);
    }

    protected void showSystemMenu(){
      systemMenu.show(systemButton, 0, BUTTON_SIZE);
    }

    protected void hideSystemMenu(){
      systemMenu.setVisible(false);
    }

    static Dimension buttonDimension = new Dimension(BUTTON_SIZE, BUTTON_SIZE);

    private abstract class FrameButton extends JButton {

        FrameButton() {
            super();
            setFocusPainted(false);
            setBorderPainted(false);
        }

        public boolean isFocusTraversable() {
            return false;
        }

        public void requestFocus() {
            // ignore request.
        }

        public Dimension getMinimumSize() {
            return buttonDimension;
        }

        public Dimension getPreferredSize() {
            return buttonDimension;
        }

        public void paintComponent(Graphics g) {
            Dimension d = getSize();
            int maxX = d.width - 1;
            int maxY = d.height - 1;

            // draw background
            g.setColor(color);
            g.fillRect(1, 1, d.width, d.height);

            // draw border
            boolean pressed = getModel().isPressed();
            g.setColor(pressed ? shadow : highlight);
            g.drawLine(0, 0, maxX, 0);
            g.drawLine(0, 0, 0, maxY);
            g.setColor(pressed ? highlight : shadow);
            g.drawLine(1, maxY, maxX, maxY);
            g.drawLine(maxX, 1, maxX, maxY);
        }
    }

    private class MinimizeButton extends FrameButton {
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(highlight);
            g.drawLine(7, 8, 7, 11);
            g.drawLine(7, 8, 10, 8);
            g.setColor(shadow);
            g.drawLine(8, 11, 10, 11);
            g.drawLine(11, 9, 11, 11);
        }
    }

    private class MaximizeButton extends FrameButton {
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            int max = BUTTON_SIZE - 5;
            boolean isMaxed = frame.isMaximum();
            g.setColor(isMaxed ? shadow : highlight);
            g.drawLine(4, 4, 4, max);
            g.drawLine(4, 4, max, 4);
            g.setColor(isMaxed ? highlight : shadow);
            g.drawLine(5, max, max, max);
            g.drawLine(max, 5, max, max);
        }
    }

    private class SystemButton extends FrameButton {
        public boolean isFocusTraversable() { return false; }
        public void requestFocus() {}

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(highlight);
            g.drawLine(4, 8, 4, 11);
            g.drawLine(4, 8, BUTTON_SIZE - 5, 8);
            g.setColor(shadow);
            g.drawLine(5, 11, BUTTON_SIZE - 5, 11);
            g.drawLine(BUTTON_SIZE - 5, 9, BUTTON_SIZE - 5, 11);
        }
    }

    private class Title extends FrameButton {
        Title(String title) {
            super();
            setText(title);
            setHorizontalAlignment(SwingConstants.CENTER);
            setBorder(BorderFactory.createBevelBorder(
                BevelBorder.RAISED,
                UIManager.getColor("activeCaptionBorder"),
                UIManager.getColor("inactiveCaptionBorder")));

            // Forward mouse events to titlebar for moves.
            addMouseMotionListener(new MouseMotionListener() {
                public void mouseDragged(MouseEvent e) {
                    forwardEventToParent(e);
                }
                public void mouseMoved(MouseEvent e) {
                    forwardEventToParent(e);
                }
            });
            addMouseListener(new MouseListener() {
                public void mouseClicked(MouseEvent e) {
                    forwardEventToParent(e);
                }
                public void mousePressed(MouseEvent e) {
                    forwardEventToParent(e);
                }
                public void mouseReleased(MouseEvent e) {
                    forwardEventToParent(e);
                }
                public void mouseEntered(MouseEvent e) {
                    forwardEventToParent(e);
                }
                public void mouseExited(MouseEvent e) {
                    forwardEventToParent(e);
                }
            });
        }

        void forwardEventToParent(MouseEvent e) {
            getParent().dispatchEvent(new MouseEvent(
                getParent(), e.getID(), e.getWhen(), e.getModifiers(),
                e.getX(), e.getY(),  e.getXOnScreen(),
                e.getYOnScreen(), e.getClickCount(),
                e.isPopupTrigger(),  MouseEvent.NOBUTTON));
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (frame.isSelected()) {
                g.setColor(UIManager.getColor("activeCaptionText"));
            } else {
                g.setColor(UIManager.getColor("inactiveCaptionText"));
            }
            Dimension d = getSize();
            String frameTitle = frame.getTitle();
            if (frameTitle != null) {
                MotifGraphicsUtils.drawStringInRect(frame, g, frameTitle,
                                                    0, 0, d.width, d.height,
                                                    SwingConstants.CENTER);
            }
        }
    }
}
