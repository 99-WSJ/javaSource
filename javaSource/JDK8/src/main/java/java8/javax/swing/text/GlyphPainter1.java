/*
 * Copyright (c) 1999, 2006, Oracle and/or its affiliates. All rights reserved.
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
package java8.javax.swing.text;

import javax.swing.text.BadLocationException;
import javax.swing.text.GlyphView;
import javax.swing.text.ParagraphView;
import javax.swing.text.Position;
import javax.swing.text.Segment;
import javax.swing.text.SegmentCache;
import javax.swing.text.TabExpander;
import javax.swing.text.Utilities;
import javax.swing.text.View;
import java.awt.*;

/**
 * A class to perform rendering of the glyphs.
 * This can be implemented to be stateless, or
 * to hold some information as a cache to
 * facilitate faster rendering and model/view
 * translation.  At a minimum, the GlyphPainter
 * allows a View implementation to perform its
 * duties independent of a particular version
 * of JVM and selection of capabilities (i.e.
 * shaping for i18n, etc).
 * <p>
 * This implementation is intended for operation
 * under the JDK1.1 API of the Java Platform.
 * Since the JDK is backward compatible with
 * JDK1.1 API, this class will also function on
 * Java 2.  The JDK introduces improved
 * API for rendering text however, so the GlyphPainter2
 * is recommended for the DK.
 *
 * @author  Timothy Prinzing
 * @see GlyphView
 */
class GlyphPainter1 extends GlyphView.GlyphPainter {

    /**
     * Determine the span the glyphs given a start location
     * (for tab expansion).
     */
    public float getSpan(GlyphView v, int p0, int p1,
                         TabExpander e, float x) {
        sync(v);
        Segment text = v.getText(p0, p1);
        int[] justificationData = getJustificationData(v);
        int width = Utilities.getTabbedTextWidth(v, text, metrics, (int) x, e, p0,
                                                 justificationData);
        javax.swing.text.SegmentCache.releaseSharedSegment(text);
        return width;
    }

    public float getHeight(GlyphView v) {
        sync(v);
        return metrics.getHeight();
    }

    /**
     * Fetches the ascent above the baseline for the glyphs
     * corresponding to the given range in the model.
     */
    public float getAscent(GlyphView v) {
        sync(v);
        return metrics.getAscent();
    }

    /**
     * Fetches the descent below the baseline for the glyphs
     * corresponding to the given range in the model.
     */
    public float getDescent(GlyphView v) {
        sync(v);
        return metrics.getDescent();
    }

    /**
     * Paints the glyphs representing the given range.
     */
    public void paint(GlyphView v, Graphics g, Shape a, int p0, int p1) {
        sync(v);
        Segment text;
        TabExpander expander = v.getTabExpander();
        Rectangle alloc = (a instanceof Rectangle) ? (Rectangle)a : a.getBounds();

        // determine the x coordinate to render the glyphs
        int x = alloc.x;
        int p = v.getStartOffset();
        int[] justificationData = getJustificationData(v);
        if (p != p0) {
            text = v.getText(p, p0);
            int width = Utilities.getTabbedTextWidth(v, text, metrics, x, expander, p,
                                                     justificationData);
            x += width;
            javax.swing.text.SegmentCache.releaseSharedSegment(text);
        }

        // determine the y coordinate to render the glyphs
        int y = alloc.y + metrics.getHeight() - metrics.getDescent();

        // render the glyphs
        text = v.getText(p0, p1);
        g.setFont(metrics.getFont());

        Utilities.drawTabbedText(v, text, x, y, g, expander,p0,
                                 justificationData);
        javax.swing.text.SegmentCache.releaseSharedSegment(text);
    }

    public Shape modelToView(GlyphView v, int pos, Position.Bias bias,
                             Shape a) throws BadLocationException {

        sync(v);
        Rectangle alloc = (a instanceof Rectangle) ? (Rectangle)a : a.getBounds();
        int p0 = v.getStartOffset();
        int p1 = v.getEndOffset();
        TabExpander expander = v.getTabExpander();
        Segment text;

        if(pos == p1) {
            // The caller of this is left to right and borders a right to
            // left view, return our end location.
            return new Rectangle(alloc.x + alloc.width, alloc.y, 0,
                                 metrics.getHeight());
        }
        if ((pos >= p0) && (pos <= p1)) {
            // determine range to the left of the position
            text = v.getText(p0, pos);
            int[] justificationData = getJustificationData(v);
            int width = Utilities.getTabbedTextWidth(v, text, metrics, alloc.x, expander, p0,
                                                     justificationData);
            javax.swing.text.SegmentCache.releaseSharedSegment(text);
            return new Rectangle(alloc.x + width, alloc.y, 0, metrics.getHeight());
        }
        throw new BadLocationException("modelToView - can't convert", p1);
    }

    /**
     * Provides a mapping from the view coordinate space to the logical
     * coordinate space of the model.
     *
     * @param v the view containing the view coordinates
     * @param x the X coordinate
     * @param y the Y coordinate
     * @param a the allocated region to render into
     * @param biasReturn always returns <code>Position.Bias.Forward</code>
     *   as the zero-th element of this array
     * @return the location within the model that best represents the
     *  given point in the view
     * @see View#viewToModel
     */
    public int viewToModel(GlyphView v, float x, float y, Shape a,
                           Position.Bias[] biasReturn) {

        sync(v);
        Rectangle alloc = (a instanceof Rectangle) ? (Rectangle)a : a.getBounds();
        int p0 = v.getStartOffset();
        int p1 = v.getEndOffset();
        TabExpander expander = v.getTabExpander();
        Segment text = v.getText(p0, p1);
        int[] justificationData = getJustificationData(v);
        int offs = Utilities.getTabbedTextOffset(v, text, metrics,
                                                 alloc.x, (int) x, expander, p0,
                                                 justificationData);
        javax.swing.text.SegmentCache.releaseSharedSegment(text);
        int retValue = p0 + offs;
        if(retValue == p1) {
            // No need to return backward bias as GlyphPainter1 is used for
            // ltr text only.
            retValue--;
        }
        biasReturn[0] = Position.Bias.Forward;
        return retValue;
    }

    /**
     * Determines the best location (in the model) to break
     * the given view.
     * This method attempts to break on a whitespace
     * location.  If a whitespace location can't be found, the
     * nearest character location is returned.
     *
     * @param v the view
     * @param p0 the location in the model where the
     *  fragment should start its representation >= 0
     * @param pos the graphic location along the axis that the
     *  broken view would occupy >= 0; this may be useful for
     *  things like tab calculations
     * @param len specifies the distance into the view
     *  where a potential break is desired >= 0
     * @return the model location desired for a break
     * @see View#breakView
     */
    public int getBoundedPosition(GlyphView v, int p0, float x, float len) {
        sync(v);
        TabExpander expander = v.getTabExpander();
        Segment s = v.getText(p0, v.getEndOffset());
        int[] justificationData = getJustificationData(v);
        int index = Utilities.getTabbedTextOffset(v, s, metrics, (int)x, (int)(x+len),
                                                  expander, p0, false,
                                                  justificationData);
        javax.swing.text.SegmentCache.releaseSharedSegment(s);
        int p1 = p0 + index;
        return p1;
    }

    void sync(GlyphView v) {
        Font f = v.getFont();
        if ((metrics == null) || (! f.equals(metrics.getFont()))) {
            // fetch a new FontMetrics
            Container c = v.getContainer();
            metrics = (c != null) ? c.getFontMetrics(f) :
                Toolkit.getDefaultToolkit().getFontMetrics(f);
        }
    }



    /**
     * @return justificationData from the ParagraphRow this GlyphView
     * is in or {@code null} if no justification is needed
     */
    private int[] getJustificationData(GlyphView v) {
        View parent = v.getParent();
        int [] ret = null;
        if (parent instanceof ParagraphView.Row) {
            ParagraphView.Row row = ((ParagraphView.Row) parent);
            ret = row.justificationData;
        }
        return ret;
    }

    // --- variables ---------------------------------------------

    FontMetrics metrics;
}
