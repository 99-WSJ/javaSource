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
import java.awt.event.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.event.*;
import javax.swing.plaf.basic.*;
import javax.swing.plaf.synth.*;
import javax.swing.plaf.synth.SynthComboPopup;
import javax.swing.plaf.synth.SynthContext;
import javax.swing.plaf.synth.SynthLabelUI;
import javax.swing.plaf.synth.SynthStyle;
import javax.swing.plaf.synth.SynthUI;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * Provides the Synth L&amp;F UI delegate for
 * {@link JComboBox}.
 *
 * @author Scott Violet
 * @since 1.7
 */
public class SynthComboBoxUI extends BasicComboBoxUI implements
                              PropertyChangeListener, SynthUI {
    private SynthStyle style;
    private boolean useListColors;

    /**
     * Used to adjust the location and size of the popup. Very useful for
     * situations such as we find in Nimbus where part of the border is used
     * to paint the focus. In such cases, the border is empty space, and not
     * part of the "visual" border, and in these cases, you'd like the popup
     * to be adjusted such that it looks as if it were next to the visual border.
     * You may want to use negative insets to get the right look.
     */
    Insets popupInsets;

    /**
     * This flag may be set via UIDefaults. By default, it is false, to
     * preserve backwards compatibility. If true, then the combo will
     * "act as a button" when it is not editable.
     */
    private boolean buttonWhenNotEditable;

    /**
     * A flag to indicate that the combo box and combo box button should
     * remain in the PRESSED state while the combo popup is visible.
     */
    private boolean pressedWhenPopupVisible;

    /**
     * When buttonWhenNotEditable is true, this field is used to help make
     * the combo box appear and function as a button when the combo box is
     * not editable. In such a state, you can click anywhere on the button
     * to get it to open the popup. Also, anywhere you hover over the combo
     * will cause the entire combo to go into "rollover" state, and anywhere
     * you press will go into "pressed" state. This also keeps in sync the
     * state of the combo and the arrowButton.
     */
    private ButtonHandler buttonHandler;

    /**
     * Handler for repainting combo when editor component gains/looses focus
     */
    private EditorFocusHandler editorFocusHandler;

    /**
     * If true, then the cell renderer will be forced to be non-opaque when
     * used for rendering the selected item in the combo box (not in the list),
     * and forced to opaque after rendering the selected value.
     */
    private boolean forceOpaque = false;

    /**
     * Creates a new UI object for the given component.
     *
     * @param c component to create UI object for
     * @return the UI object
     */
    public static ComponentUI createUI(JComponent c) {
        return new javax.swing.plaf.synth.SynthComboBoxUI();
    }

    /**
     * {@inheritDoc}
     *
     * Overridden to ensure that ButtonHandler is created prior to any of
     * the other installXXX methods, since several of them reference
     * buttonHandler.
     */
    @Override
    public void installUI(JComponent c) {
        buttonHandler = new ButtonHandler();
        super.installUI(c);
    }

    @Override
    protected void installDefaults() {
        updateStyle(comboBox);
    }

    private void updateStyle(JComboBox comboBox) {
        SynthStyle oldStyle = style;
        SynthContext context = getContext(comboBox, ENABLED);

        style = SynthLookAndFeel.updateStyle(context, this);
        if (style != oldStyle) {
            padding = (Insets) style.get(context, "ComboBox.padding");
            popupInsets = (Insets)style.get(context, "ComboBox.popupInsets");
            useListColors = style.getBoolean(context,
                    "ComboBox.rendererUseListColors", true);
            buttonWhenNotEditable = style.getBoolean(context,
                    "ComboBox.buttonWhenNotEditable", false);
            pressedWhenPopupVisible = style.getBoolean(context,
                    "ComboBox.pressedWhenPopupVisible", false);
            squareButton = style.getBoolean(context,
                    "ComboBox.squareButton", true);

            if (oldStyle != null) {
                uninstallKeyboardActions();
                installKeyboardActions();
            }
            forceOpaque = style.getBoolean(context,
                    "ComboBox.forceOpaque", false);
        }
        context.dispose();

        if(listBox != null) {
            SynthLookAndFeel.updateStyles(listBox);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void installListeners() {
        comboBox.addPropertyChangeListener(this);
        comboBox.addMouseListener(buttonHandler);
        editorFocusHandler = new EditorFocusHandler(comboBox);
        super.installListeners();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void uninstallUI(JComponent c) {
        if (popup instanceof javax.swing.plaf.synth.SynthComboPopup) {
            ((javax.swing.plaf.synth.SynthComboPopup)popup).removePopupMenuListener(buttonHandler);
        }
        super.uninstallUI(c);
        buttonHandler = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void uninstallDefaults() {
        SynthContext context = getContext(comboBox, ENABLED);

        style.uninstallDefaults(context);
        context.dispose();
        style = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void uninstallListeners() {
        editorFocusHandler.unregister();
        comboBox.removePropertyChangeListener(this);
        comboBox.removeMouseListener(buttonHandler);
        buttonHandler.pressed = false;
        buttonHandler.over = false;
        super.uninstallListeners();
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
        // currently we have a broken situation where if a developer
        // takes the border from a JComboBox and sets it on a JTextField
        // then the codepath will eventually lead back to this method
        // but pass in a JTextField instead of JComboBox! In case this
        // happens, we just return the normal synth state for the component
        // instead of doing anything special
        if (!(c instanceof JComboBox)) return SynthLookAndFeel.getComponentState(c);

        JComboBox box = (JComboBox)c;
        if (shouldActLikeButton()) {
            int state = ENABLED;
            if ((!c.isEnabled())) {
                state = DISABLED;
            }
            if (buttonHandler.isPressed()) {
                state |= PRESSED;
            }
            if (buttonHandler.isRollover()) {
                state |= MOUSE_OVER;
            }
            if (box.isFocusOwner()) {
                state |= FOCUSED;
            }
            return state;
        } else {
            // for editable combos the editor component has the focus not the
            // combo box its self, so we should make the combo paint focused
            // when its editor has focus
            int basicState = SynthLookAndFeel.getComponentState(c);
            if (box.isEditable() &&
                     box.getEditor().getEditorComponent().isFocusOwner()) {
                basicState |= FOCUSED;
            }
            return basicState;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ComboPopup createPopup() {
        javax.swing.plaf.synth.SynthComboPopup p = new javax.swing.plaf.synth.SynthComboPopup(comboBox);
        p.addPopupMenuListener(buttonHandler);
        return p;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ListCellRenderer createRenderer() {
        return new SynthComboBoxRenderer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ComboBoxEditor createEditor() {
        return new SynthComboBoxEditor();
    }

    //
    // end UI Initialization
    //======================

    /**
     * {@inheritDoc}
     */
    @Override
    public void propertyChange(PropertyChangeEvent e) {
        if (SynthLookAndFeel.shouldUpdateStyle(e)) {
            updateStyle(comboBox);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected JButton createArrowButton() {
        SynthArrowButton button = new SynthArrowButton(SwingConstants.SOUTH);
        button.setName("ComboBox.arrowButton");
        button.setModel(buttonHandler);
        return button;
    }

    //=================================
    // begin ComponentUI Implementation

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
        context.getPainter().paintComboBoxBackground(context, g, 0, 0,
                                                  c.getWidth(), c.getHeight());
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
        hasFocus = comboBox.hasFocus();
        if ( !comboBox.isEditable() ) {
            Rectangle r = rectangleForCurrentValue();
            paintCurrentValue(g,r,hasFocus);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paintBorder(SynthContext context, Graphics g, int x,
                            int y, int w, int h) {
        context.getPainter().paintComboBoxBorder(context, g, x, y, w, h);
    }

    /**
     * Paints the currently selected item.
     */
    @Override
    public void paintCurrentValue(Graphics g,Rectangle bounds,boolean hasFocus) {
        ListCellRenderer renderer = comboBox.getRenderer();
        Component c;

        c = renderer.getListCellRendererComponent(
                listBox, comboBox.getSelectedItem(), -1, false, false );

        // Fix for 4238829: should lay out the JPanel.
        boolean shouldValidate = false;
        if (c instanceof JPanel)  {
            shouldValidate = true;
        }

        if (c instanceof UIResource) {
            c.setName("ComboBox.renderer");
        }

        boolean force = forceOpaque && c instanceof JComponent;
        if (force) {
            ((JComponent)c).setOpaque(false);
        }

        int x = bounds.x, y = bounds.y, w = bounds.width, h = bounds.height;
        if (padding != null) {
            x = bounds.x + padding.left;
            y = bounds.y + padding.top;
            w = bounds.width - (padding.left + padding.right);
            h = bounds.height - (padding.top + padding.bottom);
        }

        currentValuePane.paintComponent(g, c, comboBox, x, y, w, h, shouldValidate);

        if (force) {
            ((JComponent)c).setOpaque(true);
        }
    }

    /**
     * @return true if this combo box should act as one big button. Typically
     * only happens when buttonWhenNotEditable is true, and comboBox.isEditable
     * is false.
     */
    private boolean shouldActLikeButton() {
        return buttonWhenNotEditable && !comboBox.isEditable();
    }

    /**
     * Returns the default size of an empty display area of the combo box using
     * the current renderer and font.
     *
     * This method was overridden to use SynthComboBoxRenderer instead of
     * DefaultListCellRenderer as the default renderer when calculating the
     * size of the combo box. This is used in the case of the combo not having
     * any data.
     *
     * @return the size of an empty display area
     * @see #getDisplaySize
     */
    @Override
    protected Dimension getDefaultSize() {
        SynthComboBoxRenderer r = new SynthComboBoxRenderer();
        Dimension d = getSizeForComponent(r.getListCellRendererComponent(listBox, " ", -1, false, false));
        return new Dimension(d.width, d.height);
    }

    /**
     * From BasicComboBoxRenderer v 1.18.
     *
     * Be aware that SynthFileChooserUIImpl relies on the fact that the default
     * renderer installed on a Synth combo box is a JLabel. If this is changed,
     * then an assert will fail in SynthFileChooserUIImpl
     */
    private class SynthComboBoxRenderer extends JLabel implements ListCellRenderer<Object>, UIResource {
        public SynthComboBoxRenderer() {
            super();
            setText(" ");
        }

        @Override
        public String getName() {
            // SynthComboBoxRenderer should have installed Name while constructor is working.
            // The setName invocation in the SynthComboBoxRenderer() constructor doesn't work
            // because of the opaque property is installed in the constructor based on the
            // component name (see GTKStyle.isOpaque())
            String name = super.getName();

            return name == null ? "ComboBox.renderer" : name;
        }

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                         int index, boolean isSelected, boolean cellHasFocus) {
            setName("ComboBox.listRenderer");
            SynthLookAndFeel.resetSelectedUI();
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
                if (!useListColors) {
                    SynthLookAndFeel.setSelectedUI(
                         (SynthLabelUI)SynthLookAndFeel.getUIOfType(getUI(),
                         SynthLabelUI.class), isSelected, cellHasFocus,
                         list.isEnabled(), false);
                }
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            setFont(list.getFont());

            if (value instanceof Icon) {
                setIcon((Icon)value);
                setText("");
            } else {
                String text = (value == null) ? " " : value.toString();

                if ("".equals(text)) {
                    text = " ";
                }
                setText(text);
            }

            // The renderer component should inherit the enabled and
            // orientation state of its parent combobox.  This is
            // especially needed for GTK comboboxes, where the
            // ListCellRenderer's state determines the visual state
            // of the combobox.
            if (comboBox != null){
                setEnabled(comboBox.isEnabled());
                setComponentOrientation(comboBox.getComponentOrientation());
            }

            return this;
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            SynthLookAndFeel.resetSelectedUI();
        }
    }


    private static class SynthComboBoxEditor
            extends BasicComboBoxEditor.UIResource {

        @Override public JTextField createEditorComponent() {
            JTextField f = new JTextField("", 9);
            f.setName("ComboBox.textField");
            return f;
        }
    }


    /**
     * Handles all the logic for treating the combo as a button when it is
     * not editable, and when shouldActLikeButton() is true. This class is a
     * special ButtonModel, and installed on the arrowButton when appropriate.
     * It also is installed as a mouse listener and mouse motion listener on
     * the combo box. In this way, the state between the button and combo
     * are in sync. Whenever one is "over" both are. Whenever one is pressed,
     * both are.
     */
    private final class ButtonHandler extends DefaultButtonModel
            implements MouseListener, PopupMenuListener {
        /**
         * Indicates that the mouse is over the combo or the arrow button.
         * This field only has meaning if buttonWhenNotEnabled is true.
         */
        private boolean over;
        /**
         * Indicates that the combo or arrow button has been pressed. This
         * field only has meaning if buttonWhenNotEnabled is true.
         */
        private boolean pressed;

        //------------------------------------------------------------------
        // State Methods
        //------------------------------------------------------------------

        /**
         * <p>Updates the internal "pressed" state. If shouldActLikeButton()
         * is true, and if this method call will change the internal state,
         * then the combo and button will be repainted.</p>
         *
         * <p>Note that this method is called either when a press event
         * occurs on the combo box, or on the arrow button.</p>
         */
        private void updatePressed(boolean p) {
            this.pressed = p && isEnabled();
            if (shouldActLikeButton()) {
                comboBox.repaint();
            }
        }

        /**
         * <p>Updates the internal "over" state. If shouldActLikeButton()
         * is true, and if this method call will change the internal state,
         * then the combo and button will be repainted.</p>
         *
         * <p>Note that this method is called either when a mouseover/mouseoff event
         * occurs on the combo box, or on the arrow button.</p>
         */
        private void updateOver(boolean o) {
            boolean old = isRollover();
            this.over = o && isEnabled();
            boolean newo = isRollover();
            if (shouldActLikeButton() && old != newo) {
                comboBox.repaint();
            }
        }

        //------------------------------------------------------------------
        // DefaultButtonModel Methods
        //------------------------------------------------------------------

        /**
         * @inheritDoc
         *
         * Ensures that isPressed() will return true if the combo is pressed,
         * or the arrowButton is pressed, <em>or</em> if the combo popup is
         * visible. This is the case because a combo box looks pressed when
         * the popup is visible, and so should the arrow button.
         */
        @Override
        public boolean isPressed() {
            boolean b = shouldActLikeButton() ? pressed : super.isPressed();
            return b || (pressedWhenPopupVisible && comboBox.isPopupVisible());
        }

        /**
         * @inheritDoc
         *
         * Ensures that the armed state is in sync with the pressed state
         * if shouldActLikeButton is true. Without this method, the arrow
         * button will not look pressed when the popup is open, regardless
         * of the result of isPressed() alone.
         */
        @Override
        public boolean isArmed() {
            boolean b = shouldActLikeButton() ||
                        (pressedWhenPopupVisible && comboBox.isPopupVisible());
            return b ? isPressed() : super.isArmed();
        }

        /**
         * @inheritDoc
         *
         * Ensures that isRollover() will return true if the combo is
         * rolled over, or the arrowButton is rolled over.
         */
        @Override
        public boolean isRollover() {
            return shouldActLikeButton() ? over : super.isRollover();
        }

        /**
         * @inheritDoc
         *
         * Forwards pressed states to the internal "pressed" field
         */
        @Override
        public void setPressed(boolean b) {
            super.setPressed(b);
            updatePressed(b);
        }

        /**
         * @inheritDoc
         *
         * Forwards rollover states to the internal "over" field
         */
        @Override
        public void setRollover(boolean b) {
            super.setRollover(b);
            updateOver(b);
        }

        //------------------------------------------------------------------
        // MouseListener/MouseMotionListener Methods
        //------------------------------------------------------------------

        @Override
        public void mouseEntered(MouseEvent mouseEvent) {
            updateOver(true);
        }

        @Override
        public void mouseExited(MouseEvent mouseEvent) {
            updateOver(false);
        }

        @Override
        public void mousePressed(MouseEvent mouseEvent) {
            updatePressed(true);
        }

        @Override
        public void mouseReleased(MouseEvent mouseEvent) {
            updatePressed(false);
        }

        @Override
        public void mouseClicked(MouseEvent e) {}

        //------------------------------------------------------------------
        // PopupMenuListener Methods
        //------------------------------------------------------------------

        /**
         * @inheritDoc
         *
         * Ensures that the combo box is repainted when the popup is closed.
         * This avoids a bug where clicking off the combo wasn't causing a repaint,
         * and thus the combo box still looked pressed even when it was not.
         *
         * This bug was only noticed when acting as a button, but may be generally
         * present. If so, remove the if() block
         */
        @Override
        public void popupMenuCanceled(PopupMenuEvent e) {
            if (shouldActLikeButton() || pressedWhenPopupVisible) {
                comboBox.repaint();
            }
        }

        @Override
        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}
        @Override
        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}
    }

    /**
     * Handler for repainting combo when editor component gains/looses focus
     */
    private static class EditorFocusHandler implements FocusListener,
            PropertyChangeListener {
        private JComboBox comboBox;
        private ComboBoxEditor editor = null;
        private Component editorComponent = null;

        private EditorFocusHandler(JComboBox comboBox) {
            this.comboBox = comboBox;
            editor = comboBox.getEditor();
            if (editor != null){
                editorComponent = editor.getEditorComponent();
                if (editorComponent != null){
                    editorComponent.addFocusListener(this);
                }
            }
            comboBox.addPropertyChangeListener("editor",this);
        }

        public void unregister(){
            comboBox.removePropertyChangeListener(this);
            if (editorComponent!=null){
                editorComponent.removeFocusListener(this);
            }
        }

        /** Invoked when a component gains the keyboard focus. */
        public void focusGained(FocusEvent e) {
            // repaint whole combo on focus gain
            comboBox.repaint();
        }

        /** Invoked when a component loses the keyboard focus. */
        public void focusLost(FocusEvent e) {
            // repaint whole combo on focus loss
            comboBox.repaint();
        }

        /**
         * Called when the combos editor changes
         *
         * @param evt A PropertyChangeEvent object describing the event source and
         *            the property that has changed.
         */
        public void propertyChange(PropertyChangeEvent evt) {
            ComboBoxEditor newEditor = comboBox.getEditor();
            if (editor != newEditor){
                if (editorComponent!=null){
                    editorComponent.removeFocusListener(this);
                }
                editor = newEditor;
                if (editor != null){
                    editorComponent = editor.getEditorComponent();
                    if (editorComponent != null){
                        editorComponent.addFocusListener(this);
                    }
                }
            }
        }
    }
}
