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

import javax.swing.*;
import java.awt.*;
import java.beans.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.plaf.synth.ColorType;
import javax.swing.plaf.synth.Region;
import javax.swing.plaf.synth.SynthConstants;
import javax.swing.plaf.synth.SynthContext;
import javax.swing.plaf.synth.SynthLookAndFeel;
import javax.swing.plaf.synth.SynthStyle;
import javax.swing.plaf.synth.SynthUI;
import javax.swing.text.View;

/**
 * Provides the Synth L&amp;F UI delegate for
 * {@link JButton}.
 *
 * @author Scott Violet
 * @since 1.7
 */
public class SynthButtonUI extends BasicButtonUI implements
                                 PropertyChangeListener, SynthUI {
    private SynthStyle style;

    /**
     * Creates a new UI object for the given component.
     *
     * @param c component to create UI object for
     * @return the UI object
     */
    public static ComponentUI createUI(JComponent c) {
        return new javax.swing.plaf.synth.SynthButtonUI();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void installDefaults(AbstractButton b) {
        updateStyle(b);

        LookAndFeel.installProperty(b, "rolloverEnabled", Boolean.TRUE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void installListeners(AbstractButton b) {
        super.installListeners(b);
        b.addPropertyChangeListener(this);
    }

    void updateStyle(AbstractButton b) {
        SynthContext context = getContext(b, SynthConstants.ENABLED);
        SynthStyle oldStyle = style;
        style = SynthLookAndFeel.updateStyle(context, this);
        if (style != oldStyle) {
            if (b.getMargin() == null ||
                                (b.getMargin() instanceof UIResource)) {
                Insets margin = (Insets)style.get(context,getPropertyPrefix() +
                                                  "margin");

                if (margin == null) {
                    // Some places assume margins are non-null.
                    margin = SynthLookAndFeel.EMPTY_UIRESOURCE_INSETS;
                }
                b.setMargin(margin);
            }

            Object value = style.get(context, getPropertyPrefix() + "iconTextGap");
            if (value != null) {
                        LookAndFeel.installProperty(b, "iconTextGap", value);
            }

            value = style.get(context, getPropertyPrefix() + "contentAreaFilled");
            LookAndFeel.installProperty(b, "contentAreaFilled",
                                        value != null? value : Boolean.TRUE);

            if (oldStyle != null) {
                uninstallKeyboardActions(b);
                installKeyboardActions(b);
            }

        }
        context.dispose();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void uninstallListeners(AbstractButton b) {
        super.uninstallListeners(b);
        b.removePropertyChangeListener(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void uninstallDefaults(AbstractButton b) {
        SynthContext context = getContext(b, ENABLED);

        style.uninstallDefaults(context);
        context.dispose();
        style = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SynthContext getContext(JComponent c) {
        return getContext(c, getComponentState(c));
    }

    SynthContext getContext(JComponent c, int state) {
        Region region = SynthLookAndFeel.getRegion(c);
        return SynthContext.getContext(SynthContext.class, c, region,
                                       style, state);
    }

    /**
     * Returns the current state of the passed in <code>AbstractButton</code>.
     */
    private int getComponentState(JComponent c) {
        int state = ENABLED;

        if (!c.isEnabled()) {
            state = DISABLED;
        }
        if (SynthLookAndFeel.getSelectedUI() == this) {
            return SynthLookAndFeel.getSelectedUIState() | SynthConstants.ENABLED;
        }
        AbstractButton button = (AbstractButton) c;
        ButtonModel model = button.getModel();

        if (model.isPressed()) {
            if (model.isArmed()) {
                state = PRESSED;
            }
            else {
                state = MOUSE_OVER;
            }
        }
        if (model.isRollover()) {
            state |= MOUSE_OVER;
        }
        if (model.isSelected()) {
            state |= SELECTED;
        }
        if (c.isFocusOwner() && button.isFocusPainted()) {
            state |= FOCUSED;
        }
        if ((c instanceof JButton) && ((JButton)c).isDefaultButton()) {
            state |= DEFAULT;
        }
        return state;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getBaseline(JComponent c, int width, int height) {
        if (c == null) {
            throw new NullPointerException("Component must be non-null");
        }
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException(
                    "Width and height must be >= 0");
        }
        AbstractButton b = (AbstractButton)c;
        String text = b.getText();
        if (text == null || "".equals(text)) {
            return -1;
        }
        Insets i = b.getInsets();
        Rectangle viewRect = new Rectangle();
        Rectangle textRect = new Rectangle();
        Rectangle iconRect = new Rectangle();
        viewRect.x = i.left;
        viewRect.y = i.top;
        viewRect.width = width - (i.right + viewRect.x);
        viewRect.height = height - (i.bottom + viewRect.y);

        // layout the text and icon
        SynthContext context = getContext(b);
        FontMetrics fm = context.getComponent().getFontMetrics(
            context.getStyle().getFont(context));
        context.getStyle().getGraphicsUtils(context).layoutText(
            context, fm, b.getText(), b.getIcon(),
            b.getHorizontalAlignment(), b.getVerticalAlignment(),
            b.getHorizontalTextPosition(), b.getVerticalTextPosition(),
            viewRect, iconRect, textRect, b.getIconTextGap());
        View view = (View)b.getClientProperty(BasicHTML.propertyKey);
        int baseline;
        if (view != null) {
            baseline = BasicHTML.getHTMLBaseline(view, textRect.width,
                                                 textRect.height);
            if (baseline >= 0) {
                baseline += textRect.y;
            }
        }
        else {
            baseline = textRect.y + fm.getAscent();
        }
        context.dispose();
        return baseline;
    }

    // ********************************
    //          Paint Methods
    // ********************************

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
        paintBackground(context, g, c);
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
        AbstractButton b = (AbstractButton)context.getComponent();

        g.setColor(context.getStyle().getColor(context,
                                               ColorType.TEXT_FOREGROUND));
        g.setFont(style.getFont(context));
        context.getStyle().getGraphicsUtils(context).paintText(
            context, g, b.getText(), getIcon(b),
            b.getHorizontalAlignment(), b.getVerticalAlignment(),
            b.getHorizontalTextPosition(), b.getVerticalTextPosition(),
            b.getIconTextGap(), b.getDisplayedMnemonicIndex(),
            getTextShiftOffset(context));
    }

    void paintBackground(SynthContext context, Graphics g, JComponent c) {
        if (((AbstractButton) c).isContentAreaFilled()) {
            context.getPainter().paintButtonBackground(context, g, 0, 0,
                                                       c.getWidth(),
                                                       c.getHeight());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paintBorder(SynthContext context, Graphics g, int x,
                            int y, int w, int h) {
        context.getPainter().paintButtonBorder(context, g, x, y, w, h);
    }

    /**
     * Returns the default icon. This should not callback
     * to the JComponent.
     *
     * @param b button the icon is associated with
     * @return default icon
     */
    protected Icon getDefaultIcon(AbstractButton b) {
        SynthContext context = getContext(b);
        Icon icon = context.getStyle().getIcon(context, getPropertyPrefix() + "icon");
        context.dispose();
        return icon;
    }

    /**
     * Returns the Icon to use for painting the button. The icon is chosen with
     * respect to the current state of the button.
     *
     * @param b button the icon is associated with
     * @return an icon
     */
    protected Icon getIcon(AbstractButton b) {
        Icon icon = b.getIcon();
        ButtonModel model = b.getModel();

        if (!model.isEnabled()) {
            icon = getSynthDisabledIcon(b, icon);
        } else if (model.isPressed() && model.isArmed()) {
            icon = getPressedIcon(b, getSelectedIcon(b, icon));
        } else if (b.isRolloverEnabled() && model.isRollover()) {
            icon = getRolloverIcon(b, getSelectedIcon(b, icon));
        } else if (model.isSelected()) {
            icon = getSelectedIcon(b, icon);
        } else {
            icon = getEnabledIcon(b, icon);
        }
        if(icon == null) {
            return getDefaultIcon(b);
        }
        return icon;
    }

    /**
     * This method will return the icon that should be used for a button.  We
     * only want to use the synth icon defined by the style if the specific
     * icon has not been defined for the button state and the backup icon is a
     * UIResource (we set it, not the developer).
     *
     * @param b button
     * @param specificIcon icon returned from the button for the specific state
     * @param defaultIcon fallback icon
     * @param state the synth state of the button
     */
    private Icon getIcon(AbstractButton b, Icon specificIcon, Icon defaultIcon,
            int state) {
        Icon icon = specificIcon;
        if (icon == null) {
            if (defaultIcon instanceof UIResource) {
                icon = getSynthIcon(b, state);
                if (icon == null) {
                    icon = defaultIcon;
                }
            } else {
                icon = defaultIcon;
            }
        }
        return icon;
    }

    private Icon getSynthIcon(AbstractButton b, int synthConstant) {
        return style.getIcon(getContext(b, synthConstant), getPropertyPrefix() + "icon");
    }

    private Icon getEnabledIcon(AbstractButton b, Icon defaultIcon) {
        if (defaultIcon == null) {
            defaultIcon = getSynthIcon(b, SynthConstants.ENABLED);
        }
        return defaultIcon;
    }

    private Icon getSelectedIcon(AbstractButton b, Icon defaultIcon) {
        return getIcon(b, b.getSelectedIcon(), defaultIcon,
                SynthConstants.SELECTED);
    }

    private Icon getRolloverIcon(AbstractButton b, Icon defaultIcon) {
        ButtonModel model = b.getModel();
        Icon icon;
        if (model.isSelected()) {
            icon = getIcon(b, b.getRolloverSelectedIcon(), defaultIcon,
                    SynthConstants.MOUSE_OVER | SynthConstants.SELECTED);
        } else {
            icon = getIcon(b, b.getRolloverIcon(), defaultIcon,
                    SynthConstants.MOUSE_OVER);
        }
        return icon;
    }

    private Icon getPressedIcon(AbstractButton b, Icon defaultIcon) {
        return getIcon(b, b.getPressedIcon(), defaultIcon,
                SynthConstants.PRESSED);
    }

    private Icon getSynthDisabledIcon(AbstractButton b, Icon defaultIcon) {
        ButtonModel model = b.getModel();
        Icon icon;
        if (model.isSelected()) {
            icon = getIcon(b, b.getDisabledSelectedIcon(), defaultIcon,
                    SynthConstants.DISABLED | SynthConstants.SELECTED);
        } else {
            icon = getIcon(b, b.getDisabledIcon(), defaultIcon,
                    SynthConstants.DISABLED);
        }
        return icon;
    }

    /**
     * Returns the amount to shift the text/icon when painting.
     */
    private int getTextShiftOffset(SynthContext state) {
        AbstractButton button = (AbstractButton)state.getComponent();
        ButtonModel model = button.getModel();

        if (model.isArmed() && model.isPressed() &&
                               button.getPressedIcon() == null) {
            return state.getStyle().getInt(state, getPropertyPrefix() +
                                           "textShiftOffset", 0);
        }
        return 0;
    }

    // ********************************
    //          Layout Methods
    // ********************************

    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getMinimumSize(JComponent c) {
        if (c.getComponentCount() > 0 && c.getLayout() != null) {
            return null;
        }
        AbstractButton b = (AbstractButton)c;
        SynthContext ss = getContext(c);
        Dimension size = ss.getStyle().getGraphicsUtils(ss).getMinimumSize(
               ss, ss.getStyle().getFont(ss), b.getText(), getSizingIcon(b),
               b.getHorizontalAlignment(), b.getVerticalAlignment(),
               b.getHorizontalTextPosition(),
               b.getVerticalTextPosition(), b.getIconTextGap(),
               b.getDisplayedMnemonicIndex());

        ss.dispose();
        return size;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getPreferredSize(JComponent c) {
        if (c.getComponentCount() > 0 && c.getLayout() != null) {
            return null;
        }
        AbstractButton b = (AbstractButton)c;
        SynthContext ss = getContext(c);
        Dimension size = ss.getStyle().getGraphicsUtils(ss).getPreferredSize(
               ss, ss.getStyle().getFont(ss), b.getText(), getSizingIcon(b),
               b.getHorizontalAlignment(), b.getVerticalAlignment(),
               b.getHorizontalTextPosition(),
               b.getVerticalTextPosition(), b.getIconTextGap(),
               b.getDisplayedMnemonicIndex());

        ss.dispose();
        return size;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getMaximumSize(JComponent c) {
        if (c.getComponentCount() > 0 && c.getLayout() != null) {
            return null;
        }

        AbstractButton b = (AbstractButton)c;
        SynthContext ss = getContext(c);
        Dimension size = ss.getStyle().getGraphicsUtils(ss).getMaximumSize(
               ss, ss.getStyle().getFont(ss), b.getText(), getSizingIcon(b),
               b.getHorizontalAlignment(), b.getVerticalAlignment(),
               b.getHorizontalTextPosition(),
               b.getVerticalTextPosition(), b.getIconTextGap(),
               b.getDisplayedMnemonicIndex());

        ss.dispose();
        return size;
    }

    /**
     * Returns the Icon used in calculating the
     * preferred/minimum/maximum size.
     */
    protected Icon getSizingIcon(AbstractButton b) {
        Icon icon = getEnabledIcon(b, b.getIcon());
        if (icon == null) {
            icon = getDefaultIcon(b);
        }
        return icon;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void propertyChange(PropertyChangeEvent e) {
        if (SynthLookAndFeel.shouldUpdateStyle(e)) {
            updateStyle((AbstractButton)e.getSource());
        }
    }
}
