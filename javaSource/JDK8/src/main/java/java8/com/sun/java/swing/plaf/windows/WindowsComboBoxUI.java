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

import com.sun.java.swing.plaf.windows.WindowsBorders;
import com.sun.java.swing.plaf.windows.WindowsGraphicsUtils;
import sun.swing.DefaultLookup;
import sun.swing.StringUIClientPropertyKey;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import static com.sun.java.swing.plaf.windows.TMSchema.Part;
import static com.sun.java.swing.plaf.windows.TMSchema.State;
import static com.sun.java.swing.plaf.windows.XPStyle.Skin;


/**
 * Windows combo box.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @author Tom Santos
 * @author Igor Kushnirskiy
 */

public class WindowsComboBoxUI extends BasicComboBoxUI {

    private static final MouseListener rolloverListener =
        new MouseAdapter() {
            private void handleRollover(MouseEvent e, boolean isRollover) {
                JComboBox comboBox = getComboBox(e);
                com.sun.java.swing.plaf.windows.WindowsComboBoxUI comboBoxUI = getWindowsComboBoxUI(e);
                if (comboBox == null || comboBoxUI == null) {
                    return;
                }
                if (! comboBox.isEditable()) {
                    //mouse over editable ComboBox does not switch rollover
                    //for the arrow button
                    ButtonModel m = null;
                    if (comboBoxUI.arrowButton != null) {
                        m = comboBoxUI.arrowButton.getModel();
                    }
                    if (m != null ) {
                        m.setRollover(isRollover);
                    }
                }
                comboBoxUI.isRollover = isRollover;
                comboBox.repaint();
            }

            public void mouseEntered(MouseEvent e) {
                handleRollover(e, true);
            }

            public void mouseExited(MouseEvent e) {
                handleRollover(e, false);
            }

            private JComboBox getComboBox(MouseEvent event) {
                Object source = event.getSource();
                JComboBox rv = null;
                if (source instanceof JComboBox) {
                    rv = (JComboBox) source;
                } else if (source instanceof XPComboBoxButton) {
                    rv = ((XPComboBoxButton) source)
                        .getWindowsComboBoxUI().comboBox;
                }
                return rv;
            }

            private com.sun.java.swing.plaf.windows.WindowsComboBoxUI getWindowsComboBoxUI(MouseEvent event) {
                JComboBox comboBox = getComboBox(event);
                com.sun.java.swing.plaf.windows.WindowsComboBoxUI rv = null;
                if (comboBox != null
                    && comboBox.getUI() instanceof com.sun.java.swing.plaf.windows.WindowsComboBoxUI) {
                    rv = (com.sun.java.swing.plaf.windows.WindowsComboBoxUI) comboBox.getUI();
                }
                return rv;
            }

        };
    private boolean isRollover = false;

    private static final PropertyChangeListener componentOrientationListener =
        new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                String propertyName = e.getPropertyName();
                Object source = null;
                if ("componentOrientation" == propertyName
                    && (source = e.getSource()) instanceof JComboBox
                    && ((JComboBox) source).getUI() instanceof
                        com.sun.java.swing.plaf.windows.WindowsComboBoxUI) {
                    JComboBox comboBox = (JComboBox) source;
                    com.sun.java.swing.plaf.windows.WindowsComboBoxUI comboBoxUI = (com.sun.java.swing.plaf.windows.WindowsComboBoxUI) comboBox.getUI();
                    if (comboBoxUI.arrowButton instanceof XPComboBoxButton) {
                        ((XPComboBoxButton) comboBoxUI.arrowButton).setPart(
                                    (comboBox.getComponentOrientation() ==
                                       ComponentOrientation.RIGHT_TO_LEFT)
                                    ? Part.CP_DROPDOWNBUTTONLEFT
                                    : Part.CP_DROPDOWNBUTTONRIGHT);
                            }
                        }
                    }
                };

    public static ComponentUI createUI(JComponent c) {
        return new com.sun.java.swing.plaf.windows.WindowsComboBoxUI();
    }

    public void installUI( JComponent c ) {
        super.installUI( c );
        isRollover = false;
        comboBox.setRequestFocusEnabled( true );
        if (com.sun.java.swing.plaf.windows.XPStyle.getXP() != null && arrowButton != null) {
            //we can not do it in installListeners because arrowButton
            //is initialized after installListeners is invoked
            comboBox.addMouseListener(rolloverListener);
            arrowButton.addMouseListener(rolloverListener);
        }
    }

    public void uninstallUI(JComponent c ) {
        comboBox.removeMouseListener(rolloverListener);
        if(arrowButton != null) {
            arrowButton.removeMouseListener(rolloverListener);
        }
        super.uninstallUI( c );
    }

    /**
     * {@inheritDoc}
     * @since 1.6
     */
    @Override
    protected void installListeners() {
        super.installListeners();
        com.sun.java.swing.plaf.windows.XPStyle xp = com.sun.java.swing.plaf.windows.XPStyle.getXP();
        //button glyph for LTR and RTL combobox might differ
        if (xp != null
              && xp.isSkinDefined(comboBox, Part.CP_DROPDOWNBUTTONRIGHT)) {
            comboBox.addPropertyChangeListener("componentOrientation",
                                               componentOrientationListener);
        }
    }

    /**
     * {@inheritDoc}
     * @since 1.6
     */
    @Override
    protected void uninstallListeners() {
        super.uninstallListeners();
        comboBox.removePropertyChangeListener("componentOrientation",
                                              componentOrientationListener);
    }

    /**
     * {@inheritDoc}
     * @since 1.6
     */
    protected void configureEditor() {
        super.configureEditor();
        if (com.sun.java.swing.plaf.windows.XPStyle.getXP() != null) {
            editor.addMouseListener(rolloverListener);
        }
    }

    /**
     * {@inheritDoc}
     * @since 1.6
     */
    protected void unconfigureEditor() {
        super.unconfigureEditor();
        editor.removeMouseListener(rolloverListener);
    }

    /**
     * {@inheritDoc}
     * @since 1.6
     */
    public void paint(Graphics g, JComponent c) {
        if (com.sun.java.swing.plaf.windows.XPStyle.getXP() != null) {
            paintXPComboBoxBackground(g, c);
        }
        super.paint(g, c);
    }

    State getXPComboBoxState(JComponent c) {
        State state = State.NORMAL;
        if (!c.isEnabled()) {
            state = State.DISABLED;
        } else if (isPopupVisible(comboBox)) {
            state = State.PRESSED;
        } else if (isRollover) {
            state = State.HOT;
        }
        return state;
    }

    private void paintXPComboBoxBackground(Graphics g, JComponent c) {
        com.sun.java.swing.plaf.windows.XPStyle xp = com.sun.java.swing.plaf.windows.XPStyle.getXP();
        State state = getXPComboBoxState(c);
        Skin skin = null;
        if (! comboBox.isEditable()
              && xp.isSkinDefined(c, Part.CP_READONLY)) {
            skin = xp.getSkin(c, Part.CP_READONLY);
        }
        if (skin == null) {
            skin = xp.getSkin(c, Part.CP_COMBOBOX);
        }
        skin.paintSkin(g, 0, 0, c.getWidth(), c.getHeight(), state);
    }

    /**
     * If necessary paints the currently selected item.
     *
     * @param g Graphics to paint to
     * @param bounds Region to paint current value to
     * @param hasFocus whether or not the JComboBox has focus
     * @throws NullPointerException if any of the arguments are null.
     * @since 1.5
     */
    public void paintCurrentValue(Graphics g, Rectangle bounds,
                                  boolean hasFocus) {
        com.sun.java.swing.plaf.windows.XPStyle xp = com.sun.java.swing.plaf.windows.XPStyle.getXP();
        if ( xp != null) {
            bounds.x += 2;
            bounds.y += 2;
            bounds.width -= 4;
            bounds.height -= 4;
        } else {
            bounds.x += 1;
            bounds.y += 1;
            bounds.width -= 2;
            bounds.height -= 2;
        }
        if (! comboBox.isEditable()
            && xp != null
            && xp.isSkinDefined(comboBox, Part.CP_READONLY)) {
            // On vista for READNLY ComboBox
            // color for currentValue is the same as for any other item

            // mostly copied from javax.swing.plaf.basic.BasicComboBoxUI.paintCurrentValue
            ListCellRenderer renderer = comboBox.getRenderer();
            Component c;
            if ( hasFocus && !isPopupVisible(comboBox) ) {
                c = renderer.getListCellRendererComponent(
                        listBox,
                        comboBox.getSelectedItem(),
                        -1,
                        true,
                        false );
            } else {
                c = renderer.getListCellRendererComponent(
                        listBox,
                        comboBox.getSelectedItem(),
                        -1,
                        false,
                        false );
            }
            c.setFont(comboBox.getFont());
            if ( comboBox.isEnabled() ) {
                c.setForeground(comboBox.getForeground());
                c.setBackground(comboBox.getBackground());
            } else {
                c.setForeground(DefaultLookup.getColor(
                         comboBox, this, "ComboBox.disabledForeground", null));
                c.setBackground(DefaultLookup.getColor(
                         comboBox, this, "ComboBox.disabledBackground", null));
            }
            boolean shouldValidate = false;
            if (c instanceof JPanel)  {
                shouldValidate = true;
            }
            currentValuePane.paintComponent(g, c, comboBox, bounds.x, bounds.y,
                                            bounds.width, bounds.height, shouldValidate);

        } else {
            super.paintCurrentValue(g, bounds, hasFocus);
        }
    }

    /**
     * {@inheritDoc}
     * @since 1.6
     */
    public void paintCurrentValueBackground(Graphics g, Rectangle bounds,
                                            boolean hasFocus) {
        if (com.sun.java.swing.plaf.windows.XPStyle.getXP() == null) {
            super.paintCurrentValueBackground(g, bounds, hasFocus);
        }
    }

    public Dimension getMinimumSize( JComponent c ) {
        Dimension d = super.getMinimumSize(c);
        if (com.sun.java.swing.plaf.windows.XPStyle.getXP() != null) {
            d.width += 5;
        } else {
            d.width += 4;
        }
        d.height += 2;
        return d;
    }

    /**
     * Creates a layout manager for managing the components which make up the
     * combo box.
     *
     * @return an instance of a layout manager
     */
    protected LayoutManager createLayoutManager() {
        return new ComboBoxLayoutManager() {
            public void layoutContainer(Container parent) {
                super.layoutContainer(parent);

                if (com.sun.java.swing.plaf.windows.XPStyle.getXP() != null && arrowButton != null) {
                    Dimension d = parent.getSize();
                    Insets insets = getInsets();
                    int buttonWidth = arrowButton.getPreferredSize().width;
                    arrowButton.setBounds(WindowsGraphicsUtils.isLeftToRight((JComboBox)parent)
                                          ? (d.width - insets.right - buttonWidth)
                                          : insets.left,
                                          insets.top,
                                          buttonWidth, d.height - insets.top - insets.bottom);
                }
            }
        };
    }

    protected void installKeyboardActions() {
        super.installKeyboardActions();
    }

    protected ComboPopup createPopup() {
        return super.createPopup();
    }

    /**
     * Creates the default editor that will be used in editable combo boxes.
     * A default editor will be used only if an editor has not been
     * explicitly set with <code>setEditor</code>.
     *
     * @return a <code>ComboBoxEditor</code> used for the combo box
     * @see JComboBox#setEditor
     */
    protected ComboBoxEditor createEditor() {
        return new WindowsComboBoxEditor();
    }

    /**
     * {@inheritDoc}
     * @since 1.6
     */
    @Override
    protected ListCellRenderer createRenderer() {
        com.sun.java.swing.plaf.windows.XPStyle xp = com.sun.java.swing.plaf.windows.XPStyle.getXP();
        if (xp != null && xp.isSkinDefined(comboBox, Part.CP_READONLY)) {
            return new WindowsComboBoxRenderer();
        } else {
            return super.createRenderer();
        }
    }

    /**
     * Creates an button which will be used as the control to show or hide
     * the popup portion of the combo box.
     *
     * @return a button which represents the popup control
     */
    protected JButton createArrowButton() {
        if (com.sun.java.swing.plaf.windows.XPStyle.getXP() != null) {
            return new XPComboBoxButton();
        } else {
            return super.createArrowButton();
        }
    }

    private class XPComboBoxButton extends com.sun.java.swing.plaf.windows.XPStyle.GlyphButton {
        public XPComboBoxButton() {
            super(null,
                  (! com.sun.java.swing.plaf.windows.XPStyle.getXP().isSkinDefined(comboBox, Part.CP_DROPDOWNBUTTONRIGHT))
                   ? Part.CP_DROPDOWNBUTTON
                   : (comboBox.getComponentOrientation() == ComponentOrientation.RIGHT_TO_LEFT)
                     ? Part.CP_DROPDOWNBUTTONLEFT
                     : Part.CP_DROPDOWNBUTTONRIGHT
                  );
            setRequestFocusEnabled(false);
        }

        @Override
        protected State getState() {
            State rv;
            rv = super.getState();
            if (rv != State.DISABLED
                && comboBox != null && ! comboBox.isEditable()
                && com.sun.java.swing.plaf.windows.XPStyle.getXP().isSkinDefined(comboBox,
                                                 Part.CP_DROPDOWNBUTTONRIGHT)) {
                /*
                 * for non editable ComboBoxes Vista seems to have the
                 * same glyph for all non DISABLED states
                 */
                rv = State.NORMAL;
            }
            return rv;
        }

        public Dimension getPreferredSize() {
            return new Dimension(17, 21);
        }

        void setPart(Part part) {
            setPart(comboBox, part);
        }

        com.sun.java.swing.plaf.windows.WindowsComboBoxUI getWindowsComboBoxUI() {
            return com.sun.java.swing.plaf.windows.WindowsComboBoxUI.this;
        }
    }


    /**
     * Subclassed to add Windows specific Key Bindings.
     * This class is now obsolete and doesn't do anything.
     * Only included for backwards API compatibility.
     * Do not call or override.
     *
     * @deprecated As of Java 2 platform v1.4.
     */
    @Deprecated
    protected class WindowsComboPopup extends BasicComboPopup {

        public WindowsComboPopup( JComboBox cBox ) {
            super( cBox );
        }

        protected KeyListener createKeyListener() {
            return new InvocationKeyHandler();
        }

        protected class InvocationKeyHandler extends BasicComboPopup.InvocationKeyHandler {
            protected InvocationKeyHandler() {
                WindowsComboPopup.this.super();
            }
        }
    }


    /**
     * Subclassed to highlight selected item in an editable combo box.
     */
    public static class WindowsComboBoxEditor
        extends BasicComboBoxEditor.UIResource {

        /**
         * {@inheritDoc}
         * @since 1.6
         */
        protected JTextField createEditorComponent() {
            JTextField editor = super.createEditorComponent();
            Border border = (Border)UIManager.get("ComboBox.editorBorder");
            if (border != null) {
                editor.setBorder(border);
            }
            editor.setOpaque(false);
            return editor;
        }

        public void setItem(Object item) {
            super.setItem(item);
            Object focus = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
            if ((focus == editor) || (focus == editor.getParent())) {
                editor.selectAll();
            }
        }
    }

    /**
     * Subclassed to set opacity {@code false} on the renderer
     * and to show border for focused cells.
     */
    private static class WindowsComboBoxRenderer
          extends BasicComboBoxRenderer.UIResource {
        private static final Object BORDER_KEY
            = new StringUIClientPropertyKey("BORDER_KEY");
        private static final Border NULL_BORDER = new EmptyBorder(0, 0, 0, 0);
        /**
         * {@inheritDoc}
         */
        @Override
        public Component getListCellRendererComponent(
                                                 JList list,
                                                 Object value,
                                                 int index,
                                                 boolean isSelected,
                                                 boolean cellHasFocus) {
            Component rv =
                super.getListCellRendererComponent(list, value, index,
                                                   isSelected, cellHasFocus);
            if (rv instanceof JComponent) {
                JComponent component = (JComponent) rv;
                if (index == -1 && isSelected) {
                    Border border = component.getBorder();
                    Border dashedBorder =
                        new WindowsBorders.DashedBorder(list.getForeground());
                    component.setBorder(dashedBorder);
                    //store current border in client property if needed
                    if (component.getClientProperty(BORDER_KEY) == null) {
                        component.putClientProperty(BORDER_KEY,
                                       (border == null) ? NULL_BORDER : border);
                    }
                } else {
                    if (component.getBorder() instanceof
                          WindowsBorders.DashedBorder) {
                        Object storedBorder = component.getClientProperty(BORDER_KEY);
                        if (storedBorder instanceof Border) {
                            component.setBorder(
                                (storedBorder == NULL_BORDER) ? null
                                    : (Border) storedBorder);
                        }
                        component.putClientProperty(BORDER_KEY, null);
                    }
                }
                if (index == -1) {
                    component.setOpaque(false);
                    component.setForeground(list.getForeground());
                } else {
                    component.setOpaque(true);
                }
            }
            return rv;
        }

    }
}
