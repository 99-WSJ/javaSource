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

package java8.javax.swing.plaf.basic;

import sun.swing.SwingUtilities2;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.plaf.ToolTipUI;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;


/**
 * Standard tool tip L&amp;F.
 * <p>
 *
 * @author Dave Moore
 */
public class BasicToolTipUI extends ToolTipUI
{
    static javax.swing.plaf.basic.BasicToolTipUI sharedInstance = new javax.swing.plaf.basic.BasicToolTipUI();
    /**
     * Global <code>PropertyChangeListener</code> that
     * <code>createPropertyChangeListener</code> returns.
     */
    private static PropertyChangeListener sharedPropertyChangedListener;

    private PropertyChangeListener propertyChangeListener;

    public static ComponentUI createUI(JComponent c) {
        return sharedInstance;
    }

    public BasicToolTipUI() {
        super();
    }

    public void installUI(JComponent c) {
        installDefaults(c);
        installComponents(c);
        installListeners(c);
    }

    public void uninstallUI(JComponent c) {
        // REMIND: this is NOT getting called
        uninstallDefaults(c);
        uninstallComponents(c);
        uninstallListeners(c);
    }

    protected void installDefaults(JComponent c){
        LookAndFeel.installColorsAndFont(c, "ToolTip.background",
                                         "ToolTip.foreground",
                                         "ToolTip.font");
        LookAndFeel.installProperty(c, "opaque", Boolean.TRUE);
        componentChanged(c);
    }

   protected void uninstallDefaults(JComponent c){
        LookAndFeel.uninstallBorder(c);
    }

    /* Unfortunately this has to remain private until we can make API additions.
     */
    private void installComponents(JComponent c){
        BasicHTML.updateRenderer(c, ((JToolTip)c).getTipText());
    }

    /* Unfortunately this has to remain private until we can make API additions.
     */
    private void uninstallComponents(JComponent c){
        BasicHTML.updateRenderer(c, "");
    }

    protected void installListeners(JComponent c) {
        propertyChangeListener = createPropertyChangeListener(c);

        c.addPropertyChangeListener(propertyChangeListener);
    }

    protected void uninstallListeners(JComponent c) {
        c.removePropertyChangeListener(propertyChangeListener);

        propertyChangeListener = null;
    }

    /* Unfortunately this has to remain private until we can make API additions.
     */
    private PropertyChangeListener createPropertyChangeListener(JComponent c) {
        if (sharedPropertyChangedListener == null) {
            sharedPropertyChangedListener = new PropertyChangeHandler();
        }
        return sharedPropertyChangedListener;
    }

    public void paint(Graphics g, JComponent c) {
        Font font = c.getFont();
        FontMetrics metrics = SwingUtilities2.getFontMetrics(c, g, font);
        Dimension size = c.getSize();

        g.setColor(c.getForeground());
        // fix for bug 4153892
        String tipText = ((JToolTip)c).getTipText();
        if (tipText == null) {
            tipText = "";
        }

        Insets insets = c.getInsets();
        Rectangle paintTextR = new Rectangle(
            insets.left + 3,
            insets.top,
            size.width - (insets.left + insets.right) - 6,
            size.height - (insets.top + insets.bottom));
        View v = (View) c.getClientProperty(BasicHTML.propertyKey);
        if (v != null) {
            v.paint(g, paintTextR);
        } else {
            g.setFont(font);
            SwingUtilities2.drawString(c, g, tipText, paintTextR.x,
                                  paintTextR.y + metrics.getAscent());
        }
    }

    public Dimension getPreferredSize(JComponent c) {
        Font font = c.getFont();
        FontMetrics fm = c.getFontMetrics(font);
        Insets insets = c.getInsets();

        Dimension prefSize = new Dimension(insets.left+insets.right,
                                           insets.top+insets.bottom);
        String text = ((JToolTip)c).getTipText();

        if ((text == null) || text.equals("")) {
            text = "";
        }
        else {
            View v = (c != null) ? (View) c.getClientProperty("html") : null;
            if (v != null) {
                prefSize.width += (int) v.getPreferredSpan(View.X_AXIS) + 6;
                prefSize.height += (int) v.getPreferredSpan(View.Y_AXIS);
            } else {
                prefSize.width += SwingUtilities2.stringWidth(c,fm,text) + 6;
                prefSize.height += fm.getHeight();
            }
        }
        return prefSize;
    }

    public Dimension getMinimumSize(JComponent c) {
        Dimension d = getPreferredSize(c);
        View v = (View) c.getClientProperty(BasicHTML.propertyKey);
        if (v != null) {
            d.width -= v.getPreferredSpan(View.X_AXIS) - v.getMinimumSpan(View.X_AXIS);
        }
        return d;
    }

    public Dimension getMaximumSize(JComponent c) {
        Dimension d = getPreferredSize(c);
        View v = (View) c.getClientProperty(BasicHTML.propertyKey);
        if (v != null) {
            d.width += v.getMaximumSpan(View.X_AXIS) - v.getPreferredSpan(View.X_AXIS);
        }
        return d;
    }

    /**
     * Invoked when the <code>JCompoment</code> associated with the
     * <code>JToolTip</code> has changed, or at initialization time. This
     * should update any state dependant upon the <code>JComponent</code>.
     *
     * @param c the JToolTip the JComponent has changed on.
     */
    private void componentChanged(JComponent c) {
        JComponent comp = ((JToolTip)c).getComponent();

        if (comp != null && !(comp.isEnabled())) {
            // For better backward compatibility, only install inactive
            // properties if they are defined.
            if (UIManager.getBorder("ToolTip.borderInactive") != null) {
                LookAndFeel.installBorder(c, "ToolTip.borderInactive");
            }
            else {
                LookAndFeel.installBorder(c, "ToolTip.border");
            }
            if (UIManager.getColor("ToolTip.backgroundInactive") != null) {
                LookAndFeel.installColors(c,"ToolTip.backgroundInactive",
                                          "ToolTip.foregroundInactive");
            }
            else {
                LookAndFeel.installColors(c,"ToolTip.background",
                                          "ToolTip.foreground");
            }
        } else {
            LookAndFeel.installBorder(c, "ToolTip.border");
            LookAndFeel.installColors(c, "ToolTip.background",
                                      "ToolTip.foreground");
        }
    }


    private static class PropertyChangeHandler implements
                                 PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            String name = e.getPropertyName();
            if (name.equals("tiptext") || "font".equals(name) ||
                "foreground".equals(name)) {
                // remove the old html view client property if one
                // existed, and install a new one if the text installed
                // into the JLabel is html source.
                JToolTip tip = ((JToolTip) e.getSource());
                String text = tip.getTipText();
                BasicHTML.updateRenderer(tip, text);
            }
            else if ("component".equals(name)) {
                JToolTip tip = ((JToolTip) e.getSource());

                if (tip.getUI() instanceof javax.swing.plaf.basic.BasicToolTipUI) {
                    ((javax.swing.plaf.basic.BasicToolTipUI)tip.getUI()).componentChanged(tip);
                }
            }
        }
    }
}
