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

package java8.java.awt;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.beans.ConstructorProperties;

/**
 * The <code>GradientPaint</code> class provides a way to fill
 * a {@link Shape} with a linear color gradient pattern.
 * If {@link Point} P1 with {@link Color} C1 and <code>Point</code> P2 with
 * <code>Color</code> C2 are specified in user space, the
 * <code>Color</code> on the P1, P2 connecting line is proportionally
 * changed from C1 to C2.  Any point P not on the extended P1, P2
 * connecting line has the color of the point P' that is the perpendicular
 * projection of P on the extended P1, P2 connecting line.
 * Points on the extended line outside of the P1, P2 segment can be colored
 * in one of two ways.
 * <ul>
 * <li>
 * If the gradient is cyclic then the points on the extended P1, P2
 * connecting line cycle back and forth between the colors C1 and C2.
 * <li>
 * If the gradient is acyclic then points on the P1 side of the segment
 * have the constant <code>Color</code> C1 while points on the P2 side
 * have the constant <code>Color</code> C2.
 * </ul>
 *
 * @see Paint
 * @see Graphics2D#setPaint
 * @version 10 Feb 1997
 */

public class GradientPaint implements Paint {
    Point2D.Float p1;
    Point2D.Float p2;
    Color color1;
    Color color2;
    boolean cyclic;

    /**
     * Constructs a simple acyclic <code>GradientPaint</code> object.
     * @param x1 x coordinate of the first specified
     * <code>Point</code> in user space
     * @param y1 y coordinate of the first specified
     * <code>Point</code> in user space
     * @param color1 <code>Color</code> at the first specified
     * <code>Point</code>
     * @param x2 x coordinate of the second specified
     * <code>Point</code> in user space
     * @param y2 y coordinate of the second specified
     * <code>Point</code> in user space
     * @param color2 <code>Color</code> at the second specified
     * <code>Point</code>
     * @throws NullPointerException if either one of colors is null
     */
    public GradientPaint(float x1,
                         float y1,
                         Color color1,
                         float x2,
                         float y2,
                         Color color2) {
        if ((color1 == null) || (color2 == null)) {
            throw new NullPointerException("Colors cannot be null");
        }

        p1 = new Point2D.Float(x1, y1);
        p2 = new Point2D.Float(x2, y2);
        this.color1 = color1;
        this.color2 = color2;
    }

    /**
     * Constructs a simple acyclic <code>GradientPaint</code> object.
     * @param pt1 the first specified <code>Point</code> in user space
     * @param color1 <code>Color</code> at the first specified
     * <code>Point</code>
     * @param pt2 the second specified <code>Point</code> in user space
     * @param color2 <code>Color</code> at the second specified
     * <code>Point</code>
     * @throws NullPointerException if either one of colors or points
     * is null
     */
    public GradientPaint(Point2D pt1,
                         Color color1,
                         Point2D pt2,
                         Color color2) {
        if ((color1 == null) || (color2 == null) ||
            (pt1 == null) || (pt2 == null)) {
            throw new NullPointerException("Colors and points should be non-null");
        }

        p1 = new Point2D.Float((float)pt1.getX(), (float)pt1.getY());
        p2 = new Point2D.Float((float)pt2.getX(), (float)pt2.getY());
        this.color1 = color1;
        this.color2 = color2;
    }

    /**
     * Constructs either a cyclic or acyclic <code>GradientPaint</code>
     * object depending on the <code>boolean</code> parameter.
     * @param x1 x coordinate of the first specified
     * <code>Point</code> in user space
     * @param y1 y coordinate of the first specified
     * <code>Point</code> in user space
     * @param color1 <code>Color</code> at the first specified
     * <code>Point</code>
     * @param x2 x coordinate of the second specified
     * <code>Point</code> in user space
     * @param y2 y coordinate of the second specified
     * <code>Point</code> in user space
     * @param color2 <code>Color</code> at the second specified
     * <code>Point</code>
     * @param cyclic <code>true</code> if the gradient pattern should cycle
     * repeatedly between the two colors; <code>false</code> otherwise
     */
    public GradientPaint(float x1,
                         float y1,
                         Color color1,
                         float x2,
                         float y2,
                         Color color2,
                         boolean cyclic) {
        this (x1, y1, color1, x2, y2, color2);
        this.cyclic = cyclic;
    }

    /**
     * Constructs either a cyclic or acyclic <code>GradientPaint</code>
     * object depending on the <code>boolean</code> parameter.
     * @param pt1 the first specified <code>Point</code>
     * in user space
     * @param color1 <code>Color</code> at the first specified
     * <code>Point</code>
     * @param pt2 the second specified <code>Point</code>
     * in user space
     * @param color2 <code>Color</code> at the second specified
     * <code>Point</code>
     * @param cyclic <code>true</code> if the gradient pattern should cycle
     * repeatedly between the two colors; <code>false</code> otherwise
     * @throws NullPointerException if either one of colors or points
     * is null
     */
    @ConstructorProperties({ "point1", "color1", "point2", "color2", "cyclic" })
    public GradientPaint(Point2D pt1,
                         Color color1,
                         Point2D pt2,
                         Color color2,
                         boolean cyclic) {
        this (pt1, color1, pt2, color2);
        this.cyclic = cyclic;
    }

    /**
     * Returns a copy of the point P1 that anchors the first color.
     * @return a {@link Point2D} object that is a copy of the point
     * that anchors the first color of this
     * <code>GradientPaint</code>.
     */
    public Point2D getPoint1() {
        return new Point2D.Float(p1.x, p1.y);
    }

    /**
     * Returns the color C1 anchored by the point P1.
     * @return a <code>Color</code> object that is the color
     * anchored by P1.
     */
    public Color getColor1() {
        return color1;
    }

    /**
     * Returns a copy of the point P2 which anchors the second color.
     * @return a {@link Point2D} object that is a copy of the point
     * that anchors the second color of this
     * <code>GradientPaint</code>.
     */
    public Point2D getPoint2() {
        return new Point2D.Float(p2.x, p2.y);
    }

    /**
     * Returns the color C2 anchored by the point P2.
     * @return a <code>Color</code> object that is the color
     * anchored by P2.
     */
    public Color getColor2() {
        return color2;
    }

    /**
     * Returns <code>true</code> if the gradient cycles repeatedly
     * between the two colors C1 and C2.
     * @return <code>true</code> if the gradient cycles repeatedly
     * between the two colors; <code>false</code> otherwise.
     */
    public boolean isCyclic() {
        return cyclic;
    }

    /**
     * Creates and returns a {@link PaintContext} used to
     * generate a linear color gradient pattern.
     * See the {@link Paint#createContext specification} of the
     * method in the {@link Paint} interface for information
     * on null parameter handling.
     *
     * @param cm the preferred {@link ColorModel} which represents the most convenient
     *           format for the caller to receive the pixel data, or {@code null}
     *           if there is no preference.
     * @param deviceBounds the device space bounding box
     *                     of the graphics primitive being rendered.
     * @param userBounds the user space bounding box
     *                   of the graphics primitive being rendered.
     * @param xform the {@link AffineTransform} from user
     *              space into device space.
     * @param hints the set of hints that the context object can use to
     *              choose between rendering alternatives.
     * @return the {@code PaintContext} for
     *         generating color patterns.
     * @see Paint
     * @see PaintContext
     * @see ColorModel
     * @see Rectangle
     * @see Rectangle2D
     * @see AffineTransform
     * @see RenderingHints
     */
    public PaintContext createContext(ColorModel cm,
                                      Rectangle deviceBounds,
                                      Rectangle2D userBounds,
                                      AffineTransform xform,
                                      RenderingHints hints) {

        return new java.awt.GradientPaintContext(cm, p1, p2, xform,
                                        color1, color2, cyclic);
    }

    /**
     * Returns the transparency mode for this <code>GradientPaint</code>.
     * @return an integer value representing this <code>GradientPaint</code>
     * object's transparency mode.
     * @see Transparency
     */
    public int getTransparency() {
        int a1 = color1.getAlpha();
        int a2 = color2.getAlpha();
        return (((a1 & a2) == 0xff) ? OPAQUE : TRANSLUCENT);
    }

}
