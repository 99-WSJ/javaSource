/*
 * Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java8.javax.swing.plaf.metal;

import sun.swing.SwingUtilities2;
import sun.awt.AppContext;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.plaf.*;
import javax.swing.plaf.metal.MetalUtils;

/**
 * MetalButtonUI implementation
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running
 * the same version of Swing.  As of 1.4, support for long term storage
 * of all JavaBeans&trade;
 * has been added to the <code>java.beans</code> package.
 * Please see {@link XMLEncoder}.
 *
 * @author Tom Santos
 */
public class MetalButtonUI extends BasicButtonUI {
    // NOTE: These are not really needed, but at this point we can't pull
    // them. Their values are updated purely for historical reasons.
    protected Color focusColor;
    protected Color selectColor;
    protected Color disabledTextColor;

    private static final Object METAL_BUTTON_UI_KEY = new Object();

    // ********************************
    //          Create PLAF
    // ********************************
    public static ComponentUI createUI(JComponent c) {
        AppContext appContext = AppContext.getAppContext();
        javax.swing.plaf.metal.MetalButtonUI metalButtonUI =
                (javax.swing.plaf.metal.MetalButtonUI) appContext.get(METAL_BUTTON_UI_KEY);
        if (metalButtonUI == null) {
            metalButtonUI = new javax.swing.plaf.metal.MetalButtonUI();
            appContext.put(METAL_BUTTON_UI_KEY, metalButtonUI);
        }
        return metalButtonUI;
    }

    // ********************************
    //          Install
    // ********************************
    public void installDefaults(AbstractButton b) {
        super.installDefaults(b);
    }

    public void uninstallDefaults(AbstractButton b) {
        super.uninstallDefaults(b);
    }

    // ********************************
    //         Create Listeners
    // ********************************
    protected BasicButtonListener createButtonListener(AbstractButton b) {
        return super.createButtonListener(b);
    }


    // ********************************
    //         Default Accessors
    // ********************************
    protected Color getSelectColor() {
        selectColor = UIManager.getColor(getPropertyPrefix() + "select");
        return selectColor;
    }

    protected Color getDisabledTextColor() {
        disabledTextColor = UIManager.getColor(getPropertyPrefix() +
                                               "disabledText");
        return disabledTextColor;
    }

    protected Color getFocusColor() {
        focusColor = UIManager.getColor(getPropertyPrefix() + "focus");
        return focusColor;
    }

    // ********************************
    //          Paint
    // ********************************
    /**
     * If necessary paints the background of the component, then
     * invokes <code>paint</code>.
     *
     * @param g Graphics to paint to
     * @param c JComponent painting on
     * @throws NullPointerException if <code>g</code> or <code>c</code> is
     *         null
     * @see ComponentUI#update
     * @see ComponentUI#paint
     * @since 1.5
     */
    public void update(Graphics g, JComponent c) {
        AbstractButton button = (AbstractButton)c;
        if ((c.getBackground() instanceof UIResource) &&
                  button.isContentAreaFilled() && c.isEnabled()) {
            ButtonModel model = button.getModel();
            if (!javax.swing.plaf.metal.MetalUtils.isToolBarButton(c)) {
                if (!model.isArmed() && !model.isPressed() &&
                        javax.swing.plaf.metal.MetalUtils.drawGradient(
                        c, g, "Button.gradient", 0, 0, c.getWidth(),
                        c.getHeight(), true)) {
                    paint(g, c);
                    return;
                }
            }
            else if (model.isRollover() && javax.swing.plaf.metal.MetalUtils.drawGradient(
                        c, g, "Button.gradient", 0, 0, c.getWidth(),
                        c.getHeight(), true)) {
                paint(g, c);
                return;
            }
        }
        super.update(g, c);
    }

    protected void paintButtonPressed(Graphics g, AbstractButton b) {
        if ( b.isContentAreaFilled() ) {
            Dimension size = b.getSize();
            g.setColor(getSelectColor());
            g.fillRect(0, 0, size.width, size.height);
        }
    }

    protected void paintFocus(Graphics g, AbstractButton b,
                              Rectangle viewRect, Rectangle textRect, Rectangle iconRect){

        Rectangle focusRect = new Rectangle();
        String text = b.getText();
        boolean isIcon = b.getIcon() != null;

        // If there is text
        if ( text != null && !text.equals( "" ) ) {
            if ( !isIcon ) {
                focusRect.setBounds( textRect );
            }
            else {
                focusRect.setBounds( iconRect.union( textRect ) );
            }
        }
        // If there is an icon and no text
        else if ( isIcon ) {
            focusRect.setBounds( iconRect );
        }

        g.setColor(getFocusColor());
        g.drawRect((focusRect.x-1), (focusRect.y-1),
                  focusRect.width+1, focusRect.height+1);

    }


    protected void paintText(Graphics g, JComponent c, Rectangle textRect, String text) {
        AbstractButton b = (AbstractButton) c;
        ButtonModel model = b.getModel();
        FontMetrics fm = SwingUtilities2.getFontMetrics(c, g);
        int mnemIndex = b.getDisplayedMnemonicIndex();

        /* Draw the Text */
        if(model.isEnabled()) {
            /*** paint the text normally */
            g.setColor(b.getForeground());
        }
        else {
            /*** paint the text disabled ***/
            g.setColor(getDisabledTextColor());
        }
        SwingUtilities2.drawStringUnderlineCharAt(c, g,text,mnemIndex,
                                  textRect.x, textRect.y + fm.getAscent());
    }
}
