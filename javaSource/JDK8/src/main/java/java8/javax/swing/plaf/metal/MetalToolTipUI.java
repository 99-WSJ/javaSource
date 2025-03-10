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
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.BasicToolTipUI;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.text.View;


/**
 * A Metal L&amp;F extension of BasicToolTipUI.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running
 * the same version of Swing.  As of 1.4, support for long term storage
 * of all JavaBeans&trade;
 * has been added to the <code>java.beans</code> package.
 * Please see {@link java.beans.XMLEncoder}.
 *
 * @author Steve Wilson
 */
public class MetalToolTipUI extends BasicToolTipUI {

    static javax.swing.plaf.metal.MetalToolTipUI sharedInstance = new javax.swing.plaf.metal.MetalToolTipUI();
    private Font smallFont;
    // Refer to note in getAcceleratorString about this field.
    private JToolTip tip;
    public static final int padSpaceBetweenStrings = 12;
    private String acceleratorDelimiter;

    public MetalToolTipUI() {
        super();
    }

    public static ComponentUI createUI(JComponent c) {
        return sharedInstance;
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        tip = (JToolTip)c;
        Font f = c.getFont();
        smallFont = new Font( f.getName(), f.getStyle(), f.getSize() - 2 );
        acceleratorDelimiter = UIManager.getString( "MenuItem.acceleratorDelimiter" );
        if ( acceleratorDelimiter == null ) { acceleratorDelimiter = "-"; }
    }

    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);
        tip = null;
    }

    public void paint(Graphics g, JComponent c) {
        JToolTip tip = (JToolTip)c;
        Font font = c.getFont();
        FontMetrics metrics = SwingUtilities2.getFontMetrics(c, g, font);
        Dimension size = c.getSize();
        int accelBL;

        g.setColor(c.getForeground());
        // fix for bug 4153892
        String tipText = tip.getTipText();
        if (tipText == null) {
            tipText = "";
        }

        String accelString = getAcceleratorString(tip);
        FontMetrics accelMetrics = SwingUtilities2.getFontMetrics(c, g, smallFont);
        int accelSpacing = calcAccelSpacing(c, accelMetrics, accelString);

        Insets insets = tip.getInsets();
        Rectangle paintTextR = new Rectangle(
            insets.left + 3,
            insets.top,
            size.width - (insets.left + insets.right) - 6 - accelSpacing,
            size.height - (insets.top + insets.bottom));
        View v = (View) c.getClientProperty(BasicHTML.propertyKey);
        if (v != null) {
            v.paint(g, paintTextR);
            accelBL = BasicHTML.getHTMLBaseline(v, paintTextR.width,
                                                  paintTextR.height);
        } else {
            g.setFont(font);
            SwingUtilities2.drawString(tip, g, tipText, paintTextR.x,
                                  paintTextR.y + metrics.getAscent());
            accelBL = metrics.getAscent();
        }

        if (!accelString.equals("")) {
            g.setFont(smallFont);
            g.setColor( MetalLookAndFeel.getPrimaryControlDarkShadow() );
            SwingUtilities2.drawString(tip, g, accelString,
                                       tip.getWidth() - 1 - insets.right
                                           - accelSpacing
                                           + padSpaceBetweenStrings
                                           - 3,
                                       paintTextR.y + accelBL);
        }
    }

    private int calcAccelSpacing(JComponent c, FontMetrics fm, String accel) {
        return accel.equals("")
               ? 0
               : padSpaceBetweenStrings +
                 SwingUtilities2.stringWidth(c, fm, accel);
    }

    public Dimension getPreferredSize(JComponent c) {
        Dimension d = super.getPreferredSize(c);

        String key = getAcceleratorString((JToolTip)c);
        if (!(key.equals(""))) {
            d.width += calcAccelSpacing(c, c.getFontMetrics(smallFont), key);
        }
        return d;
    }

    protected boolean isAcceleratorHidden() {
        Boolean b = (Boolean)UIManager.get("ToolTip.hideAccelerator");
        return b != null && b.booleanValue();
    }

    private String getAcceleratorString(JToolTip tip) {
        this.tip = tip;

        String retValue = getAcceleratorString();

        this.tip = null;
        return retValue;
    }

    // NOTE: This requires the tip field to be set before this is invoked.
    // As MetalToolTipUI is shared between all JToolTips the tip field is
    // set appropriately before this is invoked. Unfortunately this means
    // that subclasses that randomly invoke this method will see varying
    // results. If this becomes an issue, MetalToolTipUI should no longer be
    // shared.
    public String getAcceleratorString() {
        if (tip == null || isAcceleratorHidden()) {
            return "";
        }
        JComponent comp = tip.getComponent();
        if (!(comp instanceof AbstractButton)) {
            return "";
        }

        KeyStroke[] keys = comp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).keys();
        if (keys == null) {
            return "";
        }

        String controlKeyStr = "";

        for (int i = 0; i < keys.length; i++) {
            int mod = keys[i].getModifiers();
            controlKeyStr = KeyEvent.getKeyModifiersText(mod) +
                            acceleratorDelimiter +
                            KeyEvent.getKeyText(keys[i].getKeyCode());
            break;
        }

        return controlKeyStr;
    }

}
