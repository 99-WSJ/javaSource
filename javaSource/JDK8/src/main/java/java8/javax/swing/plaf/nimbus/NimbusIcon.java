/*
 * Copyright (c) 2005, 2006, Oracle and/or its affiliates. All rights reserved.
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
package java8.javax.swing.plaf.nimbus;

import javax.swing.Painter;
import sun.swing.plaf.synth.SynthIcon;

import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import javax.swing.plaf.nimbus.NimbusStyle;
import javax.swing.plaf.synth.SynthContext;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.plaf.UIResource;

/**
 * An icon that delegates to a painter.
 * @author rbair
 */
class NimbusIcon extends SynthIcon {
    private int width;
    private int height;
    private String prefix;
    private String key;

    NimbusIcon(String prefix, String key, int w, int h) {
        this.width = w;
        this.height = h;
        this.prefix = prefix;
        this.key = key;
    }

    @Override
    public void paintIcon(SynthContext context, Graphics g, int x, int y,
                          int w, int h) {
        Painter painter = null;
        if (context != null) {
            painter = (Painter)context.getStyle().get(context, key);
        }
        if (painter == null){
            painter = (Painter) UIManager.get(prefix + "[Enabled]." + key);
        }

        if (painter != null && context != null) {
            JComponent c = context.getComponent();
            boolean rotate = false;
            boolean flip = false;
            //translatex and translatey are additional translations that
            //must occur on the graphics context when rendering a toolbar
            //icon
            int translatex = 0;
            int translatey = 0;
            if (c instanceof JToolBar) {
                JToolBar toolbar = (JToolBar)c;
                rotate = toolbar.getOrientation() == JToolBar.VERTICAL;
                flip = !toolbar.getComponentOrientation().isLeftToRight();
                Object o = NimbusLookAndFeel.resolveToolbarConstraint(toolbar);
                //we only do the +1 hack for UIResource borders, assuming
                //that the border is probably going to be our border
                if (toolbar.getBorder() instanceof UIResource) {
                    if (o == BorderLayout.SOUTH) {
                        translatey = 1;
                    } else if (o == BorderLayout.EAST) {
                        translatex = 1;
                    }
                }
            } else if (c instanceof JMenu) {
                flip = ! c.getComponentOrientation().isLeftToRight();
            }
            if (g instanceof Graphics2D){
                Graphics2D gfx = (Graphics2D)g;
                gfx.translate(x, y);
                gfx.translate(translatex, translatey);
                if (rotate) {
                    gfx.rotate(Math.toRadians(90));
                    gfx.translate(0, -w);
                    painter.paint(gfx, context.getComponent(), h, w);
                    gfx.translate(0, w);
                    gfx.rotate(Math.toRadians(-90));
                } else if (flip){
                    gfx.scale(-1, 1);
                    gfx.translate(-w,0);
                    painter.paint(gfx, context.getComponent(), w, h);
                    gfx.translate(w,0);
                    gfx.scale(-1, 1);
                } else {
                    painter.paint(gfx, context.getComponent(), w, h);
                }
                gfx.translate(-translatex, -translatey);
                gfx.translate(-x, -y);
            } else {
                // use image if we are printing to a Java 1.1 PrintGraphics as
                // it is not a instance of Graphics2D
                BufferedImage img = new BufferedImage(w,h,
                        BufferedImage.TYPE_INT_ARGB);
                Graphics2D gfx = img.createGraphics();
                if (rotate) {
                    gfx.rotate(Math.toRadians(90));
                    gfx.translate(0, -w);
                    painter.paint(gfx, context.getComponent(), h, w);
                } else if (flip){
                    gfx.scale(-1, 1);
                    gfx.translate(-w,0);
                    painter.paint(gfx, context.getComponent(), w, h);
                } else {
                    painter.paint(gfx, context.getComponent(), w, h);
                }
                gfx.dispose();
                g.drawImage(img,x,y,null);
                img = null;
            }
        }
    }

    /**
     * Implements the standard Icon interface's paintIcon method as the standard
     * synth stub passes null for the context and this will cause us to not
     * paint any thing, so we override here so that we can paint the enabled
     * state if no synth context is available
     */
    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Painter painter = (Painter)UIManager.get(prefix + "[Enabled]." + key);
        if (painter != null){
            JComponent jc = (c instanceof JComponent) ? (JComponent)c : null;
            Graphics2D gfx = (Graphics2D)g;
            gfx.translate(x, y);
            painter.paint(gfx, jc , width, height);
            gfx.translate(-x, -y);
        }
    }

    @Override
    public int getIconWidth(SynthContext context) {
        if (context == null) {
            return width;
        }
        JComponent c = context.getComponent();
        if (c instanceof JToolBar && ((JToolBar)c).getOrientation() == JToolBar.VERTICAL) {
            //we only do the -1 hack for UIResource borders, assuming
            //that the border is probably going to be our border
            if (c.getBorder() instanceof UIResource) {
                return c.getWidth() - 1;
            } else {
                return c.getWidth();
            }
        } else {
            return scale(context, width);
        }
    }

    @Override
    public int getIconHeight(SynthContext context) {
        if (context == null) {
            return height;
        }
        JComponent c = context.getComponent();
        if (c instanceof JToolBar){
            JToolBar toolbar = (JToolBar)c;
            if (toolbar.getOrientation() == JToolBar.HORIZONTAL) {
                //we only do the -1 hack for UIResource borders, assuming
                //that the border is probably going to be our border
                if (toolbar.getBorder() instanceof UIResource) {
                    return c.getHeight() - 1;
                } else {
                    return c.getHeight();
                }
            } else {
                return scale(context, width);
            }
        } else {
            return scale(context, height);
        }
    }

    /**
     * Scale a size based on the "JComponent.sizeVariant" client property of the
     * component that is using this icon
     *
     * @param context The synthContext to get the component from
     * @param size The size to scale
     * @return The scaled size or original if "JComponent.sizeVariant" is not
     *          set
     */
    private int scale(SynthContext context, int size) {
        if (context == null || context.getComponent() == null){
            return size;
        }
        // The key "JComponent.sizeVariant" is used to match Apple's LAF
        String scaleKey = (String) context.getComponent().getClientProperty(
                "JComponent.sizeVariant");
        if (scaleKey != null) {
            if (NimbusStyle.LARGE_KEY.equals(scaleKey)) {
                size *= NimbusStyle.LARGE_SCALE;
            } else if (NimbusStyle.SMALL_KEY.equals(scaleKey)) {
                size *= NimbusStyle.SMALL_SCALE;
            } else if (NimbusStyle.MINI_KEY.equals(scaleKey)) {
                // mini is not quite as small for icons as full mini is
                // just too tiny
                size *= NimbusStyle.MINI_SCALE + 0.07;
            }
        }
        return size;
    }
}
