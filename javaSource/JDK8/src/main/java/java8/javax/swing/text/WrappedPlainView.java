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
package java8.javax.swing.text;

import java.awt.*;
import java.lang.ref.SoftReference;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;
import javax.swing.text.Position;
import javax.swing.text.SegmentCache;
import javax.swing.text.TabExpander;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

/**
 * View of plain text (text with only one font and color)
 * that does line-wrapping.  This view expects that its
 * associated element has child elements that represent
 * the lines it should be wrapping.  It is implemented
 * as a vertical box that contains logical line views.
 * The logical line views are nested classes that render
 * the logical line as multiple physical line if the logical
 * line is too wide to fit within the allocation.  The
 * line views draw upon the outer class for its state
 * to reduce their memory requirements.
 * <p>
 * The line views do all of their rendering through the
 * <code>drawLine</code> method which in turn does all of
 * its rendering through the <code>drawSelectedText</code>
 * and <code>drawUnselectedText</code> methods.  This
 * enables subclasses to easily specialize the rendering
 * without concern for the layout aspects.
 *
 * @author  Timothy Prinzing
 * @see     View
 */
public class WrappedPlainView extends BoxView implements TabExpander {

    /**
     * Creates a new WrappedPlainView.  Lines will be wrapped
     * on character boundaries.
     *
     * @param elem the element underlying the view
     */
    public WrappedPlainView(Element elem) {
        this(elem, false);
    }

    /**
     * Creates a new WrappedPlainView.  Lines can be wrapped on
     * either character or word boundaries depending upon the
     * setting of the wordWrap parameter.
     *
     * @param elem the element underlying the view
     * @param wordWrap should lines be wrapped on word boundaries?
     */
    public WrappedPlainView(Element elem, boolean wordWrap) {
        super(elem, Y_AXIS);
        this.wordWrap = wordWrap;
    }

    /**
     * Returns the tab size set for the document, defaulting to 8.
     *
     * @return the tab size
     */
    protected int getTabSize() {
        Integer i = (Integer) getDocument().getProperty(PlainDocument.tabSizeAttribute);
        int size = (i != null) ? i.intValue() : 8;
        return size;
    }

    /**
     * Renders a line of text, suppressing whitespace at the end
     * and expanding any tabs.  This is implemented to make calls
     * to the methods <code>drawUnselectedText</code> and
     * <code>drawSelectedText</code> so that the way selected and
     * unselected text are rendered can be customized.
     *
     * @param p0 the starting document location to use &gt;= 0
     * @param p1 the ending document location to use &gt;= p1
     * @param g the graphics context
     * @param x the starting X position &gt;= 0
     * @param y the starting Y position &gt;= 0
     * @see #drawUnselectedText
     * @see #drawSelectedText
     */
    protected void drawLine(int p0, int p1, Graphics g, int x, int y) {
        Element lineMap = getElement();
        Element line = lineMap.getElement(lineMap.getElementIndex(p0));
        Element elem;

        try {
            if (line.isLeaf()) {
                 drawText(line, p0, p1, g, x, y);
            } else {
                // this line contains the composed text.
                int idx = line.getElementIndex(p0);
                int lastIdx = line.getElementIndex(p1);
                for(; idx <= lastIdx; idx++) {
                    elem = line.getElement(idx);
                    int start = Math.max(elem.getStartOffset(), p0);
                    int end = Math.min(elem.getEndOffset(), p1);
                    x = drawText(elem, start, end, g, x, y);
                }
            }
        } catch (BadLocationException e) {
            throw new StateInvariantError("Can't render: " + p0 + "," + p1);
        }
    }

    private int drawText(Element elem, int p0, int p1, Graphics g, int x, int y) throws BadLocationException {
        p1 = Math.min(getDocument().getLength(), p1);
        AttributeSet attr = elem.getAttributes();

        if (Utilities.isComposedTextAttributeDefined(attr)) {
            g.setColor(unselected);
            x = Utilities.drawComposedText(this, attr, g, x, y,
                                        p0-elem.getStartOffset(),
                                        p1-elem.getStartOffset());
        } else {
            if (sel0 == sel1 || selected == unselected) {
                // no selection, or it is invisible
                x = drawUnselectedText(g, x, y, p0, p1);
            } else if ((p0 >= sel0 && p0 <= sel1) && (p1 >= sel0 && p1 <= sel1)) {
                x = drawSelectedText(g, x, y, p0, p1);
            } else if (sel0 >= p0 && sel0 <= p1) {
                if (sel1 >= p0 && sel1 <= p1) {
                    x = drawUnselectedText(g, x, y, p0, sel0);
                    x = drawSelectedText(g, x, y, sel0, sel1);
                    x = drawUnselectedText(g, x, y, sel1, p1);
                } else {
                    x = drawUnselectedText(g, x, y, p0, sel0);
                    x = drawSelectedText(g, x, y, sel0, p1);
                }
            } else if (sel1 >= p0 && sel1 <= p1) {
                x = drawSelectedText(g, x, y, p0, sel1);
                x = drawUnselectedText(g, x, y, sel1, p1);
            } else {
                x = drawUnselectedText(g, x, y, p0, p1);
            }
        }

        return x;
    }

    /**
     * Renders the given range in the model as normal unselected
     * text.
     *
     * @param g the graphics context
     * @param x the starting X coordinate &gt;= 0
     * @param y the starting Y coordinate &gt;= 0
     * @param p0 the beginning position in the model &gt;= 0
     * @param p1 the ending position in the model &gt;= p0
     * @return the X location of the end of the range &gt;= 0
     * @exception BadLocationException if the range is invalid
     */
    protected int drawUnselectedText(Graphics g, int x, int y,
                                     int p0, int p1) throws BadLocationException {
        g.setColor(unselected);
        Document doc = getDocument();
        Segment segment = javax.swing.text.SegmentCache.getSharedSegment();
        doc.getText(p0, p1 - p0, segment);
        int ret = Utilities.drawTabbedText(this, segment, x, y, g, this, p0);
        javax.swing.text.SegmentCache.releaseSharedSegment(segment);
        return ret;
    }

    /**
     * Renders the given range in the model as selected text.  This
     * is implemented to render the text in the color specified in
     * the hosting component.  It assumes the highlighter will render
     * the selected background.
     *
     * @param g the graphics context
     * @param x the starting X coordinate &gt;= 0
     * @param y the starting Y coordinate &gt;= 0
     * @param p0 the beginning position in the model &gt;= 0
     * @param p1 the ending position in the model &gt;= p0
     * @return the location of the end of the range.
     * @exception BadLocationException if the range is invalid
     */
    protected int drawSelectedText(Graphics g, int x,
                                   int y, int p0, int p1) throws BadLocationException {
        g.setColor(selected);
        Document doc = getDocument();
        Segment segment = javax.swing.text.SegmentCache.getSharedSegment();
        doc.getText(p0, p1 - p0, segment);
        int ret = Utilities.drawTabbedText(this, segment, x, y, g, this, p0);
        javax.swing.text.SegmentCache.releaseSharedSegment(segment);
        return ret;
    }

    /**
     * Gives access to a buffer that can be used to fetch
     * text from the associated document.
     *
     * @return the buffer
     */
    protected final Segment getLineBuffer() {
        if (lineBuffer == null) {
            lineBuffer = new Segment();
        }
        return lineBuffer;
    }

    /**
     * This is called by the nested wrapped line
     * views to determine the break location.  This can
     * be reimplemented to alter the breaking behavior.
     * It will either break at word or character boundaries
     * depending upon the break argument given at
     * construction.
     */
    protected int calculateBreakPosition(int p0, int p1) {
        int p;
        Segment segment = javax.swing.text.SegmentCache.getSharedSegment();
        loadText(segment, p0, p1);
        int currentWidth = getWidth();
        if (wordWrap) {
            p = p0 + Utilities.getBreakLocation(segment, metrics,
                                                tabBase, tabBase + currentWidth,
                                                this, p0);
        } else {
            p = p0 + Utilities.getTabbedTextOffset(segment, metrics,
                                                   tabBase, tabBase + currentWidth,
                                                   this, p0, false);
        }
        javax.swing.text.SegmentCache.releaseSharedSegment(segment);
        return p;
    }

    /**
     * Loads all of the children to initialize the view.
     * This is called by the <code>setParent</code> method.
     * Subclasses can reimplement this to initialize their
     * child views in a different manner.  The default
     * implementation creates a child view for each
     * child element.
     *
     * @param f the view factory
     */
    protected void loadChildren(ViewFactory f) {
        Element e = getElement();
        int n = e.getElementCount();
        if (n > 0) {
            View[] added = new View[n];
            for (int i = 0; i < n; i++) {
                added[i] = new WrappedLine(e.getElement(i));
            }
            replace(0, 0, added);
        }
    }

    /**
     * Update the child views in response to a
     * document event.
     */
    void updateChildren(DocumentEvent e, Shape a) {
        Element elem = getElement();
        DocumentEvent.ElementChange ec = e.getChange(elem);
        if (ec != null) {
            // the structure of this element changed.
            Element[] removedElems = ec.getChildrenRemoved();
            Element[] addedElems = ec.getChildrenAdded();
            View[] added = new View[addedElems.length];
            for (int i = 0; i < addedElems.length; i++) {
                added[i] = new WrappedLine(addedElems[i]);
            }
            replace(ec.getIndex(), removedElems.length, added);

            // should damge a little more intelligently.
            if (a != null) {
                preferenceChanged(null, true, true);
                getContainer().repaint();
            }
        }

        // update font metrics which may be used by the child views
        updateMetrics();
    }

    /**
     * Load the text buffer with the given range
     * of text.  This is used by the fragments
     * broken off of this view as well as this
     * view itself.
     */
    final void loadText(Segment segment, int p0, int p1) {
        try {
            Document doc = getDocument();
            doc.getText(p0, p1 - p0, segment);
        } catch (BadLocationException bl) {
            throw new StateInvariantError("Can't get line text");
        }
    }

    final void updateMetrics() {
        Component host = getContainer();
        Font f = host.getFont();
        metrics = host.getFontMetrics(f);
        tabSize = getTabSize() * metrics.charWidth('m');
    }

    // --- TabExpander methods ------------------------------------------

    /**
     * Returns the next tab stop position after a given reference position.
     * This implementation does not support things like centering so it
     * ignores the tabOffset argument.
     *
     * @param x the current position &gt;= 0
     * @param tabOffset the position within the text stream
     *   that the tab occurred at &gt;= 0.
     * @return the tab stop, measured in points &gt;= 0
     */
    public float nextTabStop(float x, int tabOffset) {
        if (tabSize == 0)
            return x;
        int ntabs = ((int) x - tabBase) / tabSize;
        return tabBase + ((ntabs + 1) * tabSize);
    }


    // --- View methods -------------------------------------

    /**
     * Renders using the given rendering surface and area
     * on that surface.  This is implemented to stash the
     * selection positions, selection colors, and font
     * metrics for the nested lines to use.
     *
     * @param g the rendering surface to use
     * @param a the allocated region to render into
     *
     * @see View#paint
     */
    public void paint(Graphics g, Shape a) {
        Rectangle alloc = (Rectangle) a;
        tabBase = alloc.x;
        JTextComponent host = (JTextComponent) getContainer();
        sel0 = host.getSelectionStart();
        sel1 = host.getSelectionEnd();
        unselected = (host.isEnabled()) ?
            host.getForeground() : host.getDisabledTextColor();
        Caret c = host.getCaret();
        selected = c.isSelectionVisible() && host.getHighlighter() != null ?
                        host.getSelectedTextColor() : unselected;
        g.setFont(host.getFont());

        // superclass paints the children
        super.paint(g, a);
    }

    /**
     * Sets the size of the view.  This should cause
     * layout of the view along the given axis, if it
     * has any layout duties.
     *
     * @param width the width &gt;= 0
     * @param height the height &gt;= 0
     */
    public void setSize(float width, float height) {
        updateMetrics();
        if ((int) width != getWidth()) {
            // invalidate the view itself since the desired widths
            // of the children will be based upon this views width.
            preferenceChanged(null, true, true);
            widthChanging = true;
        }
        super.setSize(width, height);
        widthChanging = false;
    }

    /**
     * Determines the preferred span for this view along an
     * axis.  This is implemented to provide the superclass
     * behavior after first making sure that the current font
     * metrics are cached (for the nested lines which use
     * the metrics to determine the height of the potentially
     * wrapped lines).
     *
     * @param axis may be either View.X_AXIS or View.Y_AXIS
     * @return  the span the view would like to be rendered into.
     *           Typically the view is told to render into the span
     *           that is returned, although there is no guarantee.
     *           The parent may choose to resize or break the view.
     * @see View#getPreferredSpan
     */
    public float getPreferredSpan(int axis) {
        updateMetrics();
        return super.getPreferredSpan(axis);
    }

    /**
     * Determines the minimum span for this view along an
     * axis.  This is implemented to provide the superclass
     * behavior after first making sure that the current font
     * metrics are cached (for the nested lines which use
     * the metrics to determine the height of the potentially
     * wrapped lines).
     *
     * @param axis may be either View.X_AXIS or View.Y_AXIS
     * @return  the span the view would like to be rendered into.
     *           Typically the view is told to render into the span
     *           that is returned, although there is no guarantee.
     *           The parent may choose to resize or break the view.
     * @see View#getMinimumSpan
     */
    public float getMinimumSpan(int axis) {
        updateMetrics();
        return super.getMinimumSpan(axis);
    }

    /**
     * Determines the maximum span for this view along an
     * axis.  This is implemented to provide the superclass
     * behavior after first making sure that the current font
     * metrics are cached (for the nested lines which use
     * the metrics to determine the height of the potentially
     * wrapped lines).
     *
     * @param axis may be either View.X_AXIS or View.Y_AXIS
     * @return  the span the view would like to be rendered into.
     *           Typically the view is told to render into the span
     *           that is returned, although there is no guarantee.
     *           The parent may choose to resize or break the view.
     * @see View#getMaximumSpan
     */
    public float getMaximumSpan(int axis) {
        updateMetrics();
        return super.getMaximumSpan(axis);
    }

    /**
     * Gives notification that something was inserted into the
     * document in a location that this view is responsible for.
     * This is implemented to simply update the children.
     *
     * @param e the change information from the associated document
     * @param a the current allocation of the view
     * @param f the factory to use to rebuild if the view has children
     * @see View#insertUpdate
     */
    public void insertUpdate(DocumentEvent e, Shape a, ViewFactory f) {
        updateChildren(e, a);

        Rectangle alloc = ((a != null) && isAllocationValid()) ?
            getInsideAllocation(a) : null;
        int pos = e.getOffset();
        View v = getViewAtPosition(pos, alloc);
        if (v != null) {
            v.insertUpdate(e, alloc, f);
        }
    }

    /**
     * Gives notification that something was removed from the
     * document in a location that this view is responsible for.
     * This is implemented to simply update the children.
     *
     * @param e the change information from the associated document
     * @param a the current allocation of the view
     * @param f the factory to use to rebuild if the view has children
     * @see View#removeUpdate
     */
    public void removeUpdate(DocumentEvent e, Shape a, ViewFactory f) {
        updateChildren(e, a);

        Rectangle alloc = ((a != null) && isAllocationValid()) ?
            getInsideAllocation(a) : null;
        int pos = e.getOffset();
        View v = getViewAtPosition(pos, alloc);
        if (v != null) {
            v.removeUpdate(e, alloc, f);
        }
    }

    /**
     * Gives notification from the document that attributes were changed
     * in a location that this view is responsible for.
     *
     * @param e the change information from the associated document
     * @param a the current allocation of the view
     * @param f the factory to use to rebuild if the view has children
     * @see View#changedUpdate
     */
    public void changedUpdate(DocumentEvent e, Shape a, ViewFactory f) {
        updateChildren(e, a);
    }

    // --- variables -------------------------------------------

    FontMetrics metrics;
    Segment lineBuffer;
    boolean widthChanging;
    int tabBase;
    int tabSize;
    boolean wordWrap;

    int sel0;
    int sel1;
    Color unselected;
    Color selected;


    /**
     * Simple view of a line that wraps if it doesn't
     * fit withing the horizontal space allocated.
     * This class tries to be lightweight by carrying little
     * state of it's own and sharing the state of the outer class
     * with it's sibblings.
     */
    class WrappedLine extends View {

        WrappedLine(Element elem) {
            super(elem);
            lineCount = -1;
        }

        /**
         * Determines the preferred span for this view along an
         * axis.
         *
         * @param axis may be either X_AXIS or Y_AXIS
         * @return   the span the view would like to be rendered into.
         *           Typically the view is told to render into the span
         *           that is returned, although there is no guarantee.
         *           The parent may choose to resize or break the view.
         * @see View#getPreferredSpan
         */
        public float getPreferredSpan(int axis) {
            switch (axis) {
            case View.X_AXIS:
                float width = getWidth();
                if (width == Integer.MAX_VALUE) {
                    // We have been initially set to MAX_VALUE, but we don't
                    // want this as our preferred.
                    return 100f;
                }
                return width;
            case View.Y_AXIS:
                if (lineCount < 0 || widthChanging) {
                    breakLines(getStartOffset());
                }
                return lineCount * metrics.getHeight();
            default:
                throw new IllegalArgumentException("Invalid axis: " + axis);
            }
        }

        /**
         * Renders using the given rendering surface and area on that
         * surface.  The view may need to do layout and create child
         * views to enable itself to render into the given allocation.
         *
         * @param g the rendering surface to use
         * @param a the allocated region to render into
         * @see View#paint
         */
        public void paint(Graphics g, Shape a) {
            Rectangle alloc = (Rectangle) a;
            int y = alloc.y + metrics.getAscent();
            int x = alloc.x;

            JTextComponent host = (JTextComponent)getContainer();
            Highlighter h = host.getHighlighter();
            LayeredHighlighter dh = (h instanceof LayeredHighlighter) ?
                                     (LayeredHighlighter)h : null;

            int start = getStartOffset();
            int end = getEndOffset();
            int p0 = start;
            int[] lineEnds = getLineEnds();
            for (int i = 0; i < lineCount; i++) {
                int p1 = (lineEnds == null) ? end :
                                             start + lineEnds[i];
                if (dh != null) {
                    int hOffset = (p1 == end)
                                  ? (p1 - 1)
                                  : p1;
                    dh.paintLayeredHighlights(g, p0, hOffset, a, host, this);
                }
                drawLine(p0, p1, g, x, y);

                p0 = p1;
                y += metrics.getHeight();
            }
        }

        /**
         * Provides a mapping from the document model coordinate space
         * to the coordinate space of the view mapped to it.
         *
         * @param pos the position to convert
         * @param a the allocated region to render into
         * @return the bounding box of the given position is returned
         * @exception BadLocationException  if the given position does not represent a
         *   valid location in the associated document
         * @see View#modelToView
         */
        public Shape modelToView(int pos, Shape a, Position.Bias b)
                throws BadLocationException {
            Rectangle alloc = a.getBounds();
            alloc.height = metrics.getHeight();
            alloc.width = 1;

            int p0 = getStartOffset();
            if (pos < p0 || pos > getEndOffset()) {
                throw new BadLocationException("Position out of range", pos);
            }

            int testP = (b == Position.Bias.Forward) ? pos :
                        Math.max(p0, pos - 1);
            int line = 0;
            int[] lineEnds = getLineEnds();
            if (lineEnds != null) {
                line = findLine(testP - p0);
                if (line > 0) {
                    p0 += lineEnds[line - 1];
                }
                alloc.y += alloc.height * line;
            }

            if (pos > p0) {
                Segment segment = javax.swing.text.SegmentCache.getSharedSegment();
                loadText(segment, p0, pos);
                alloc.x += Utilities.getTabbedTextWidth(segment, metrics,
                        alloc.x, javax.swing.text.WrappedPlainView.this, p0);
                javax.swing.text.SegmentCache.releaseSharedSegment(segment);
            }
            return alloc;
        }

        /**
         * Provides a mapping from the view coordinate space to the logical
         * coordinate space of the model.
         *
         * @param fx the X coordinate
         * @param fy the Y coordinate
         * @param a the allocated region to render into
         * @return the location within the model that best represents the
         *  given point in the view
         * @see View#viewToModel
         */
        public int viewToModel(float fx, float fy, Shape a, Position.Bias[] bias) {
            // PENDING(prinz) implement bias properly
            bias[0] = Position.Bias.Forward;

            Rectangle alloc = (Rectangle) a;
            int x = (int) fx;
            int y = (int) fy;
            if (y < alloc.y) {
                // above the area covered by this icon, so the the position
                // is assumed to be the start of the coverage for this view.
                return getStartOffset();
            } else if (y > alloc.y + alloc.height) {
                // below the area covered by this icon, so the the position
                // is assumed to be the end of the coverage for this view.
                return getEndOffset() - 1;
            } else {
                // positioned within the coverage of this view vertically,
                // so we figure out which line the point corresponds to.
                // if the line is greater than the number of lines contained, then
                // simply use the last line as it represents the last possible place
                // we can position to.
                alloc.height = metrics.getHeight();
                int line = (alloc.height > 0 ?
                            (y - alloc.y) / alloc.height : lineCount - 1);
                if (line >= lineCount) {
                    return getEndOffset() - 1;
                } else {
                    int p0 = getStartOffset();
                    int p1;
                    if (lineCount == 1) {
                        p1 = getEndOffset();
                    } else {
                        int[] lineEnds = getLineEnds();
                        p1 = p0 + lineEnds[line];
                        if (line > 0) {
                            p0 += lineEnds[line - 1];
                        }
                    }

                    if (x < alloc.x) {
                        // point is to the left of the line
                        return p0;
                    } else if (x > alloc.x + alloc.width) {
                        // point is to the right of the line
                        return p1 - 1;
                    } else {
                        // Determine the offset into the text
                        Segment segment = javax.swing.text.SegmentCache.getSharedSegment();
                        loadText(segment, p0, p1);
                        int n = Utilities.getTabbedTextOffset(segment, metrics,
                                                   alloc.x, x,
                                                   javax.swing.text.WrappedPlainView.this, p0);
                        javax.swing.text.SegmentCache.releaseSharedSegment(segment);
                        return Math.min(p0 + n, p1 - 1);
                    }
                }
            }
        }

        public void insertUpdate(DocumentEvent e, Shape a, ViewFactory f) {
            update(e, a);
        }

        public void removeUpdate(DocumentEvent e, Shape a, ViewFactory f) {
            update(e, a);
        }

        private void update(DocumentEvent ev, Shape a) {
            int oldCount = lineCount;
            breakLines(ev.getOffset());
            if (oldCount != lineCount) {
                javax.swing.text.WrappedPlainView.this.preferenceChanged(this, false, true);
                // have to repaint any views after the receiver.
                getContainer().repaint();
            } else if (a != null) {
                Component c = getContainer();
                Rectangle alloc = (Rectangle) a;
                c.repaint(alloc.x, alloc.y, alloc.width, alloc.height);
            }
        }

        /**
         * Returns line cache. If the cache was GC'ed, recreates it.
         * If there's no cache, returns null
         */
        final int[] getLineEnds() {
            if (lineCache == null) {
                return null;
            } else {
                int[] lineEnds = lineCache.get();
                if (lineEnds == null) {
                    // Cache was GC'ed, so rebuild it
                    return breakLines(getStartOffset());
                } else {
                    return lineEnds;
                }
            }
        }

        /**
         * Creates line cache if text breaks into more than one physical line.
         * @param startPos position to start breaking from
         * @return the cache created, ot null if text breaks into one line
         */
        final int[] breakLines(int startPos) {
            int[] lineEnds = (lineCache == null) ? null : lineCache.get();
            int[] oldLineEnds = lineEnds;
            int start = getStartOffset();
            int lineIndex = 0;
            if (lineEnds != null) {
                lineIndex = findLine(startPos - start);
                if (lineIndex > 0) {
                    lineIndex--;
                }
            }

            int p0 = (lineIndex == 0) ? start : start + lineEnds[lineIndex - 1];
            int p1 = getEndOffset();
            while (p0 < p1) {
                int p = calculateBreakPosition(p0, p1);
                p0 = (p == p0) ? ++p : p;      // 4410243

                if (lineIndex == 0 && p0 >= p1) {
                    // do not use cache if there's only one line
                    lineCache = null;
                    lineEnds = null;
                    lineIndex = 1;
                    break;
                } else if (lineEnds == null || lineIndex >= lineEnds.length) {
                    // we have 2+ lines, and the cache is not big enough
                    // we try to estimate total number of lines
                    double growFactor = ((double)(p1 - start) / (p0 - start));
                    int newSize = (int)Math.ceil((lineIndex + 1) * growFactor);
                    newSize = Math.max(newSize, lineIndex + 2);
                    int[] tmp = new int[newSize];
                    if (lineEnds != null) {
                        System.arraycopy(lineEnds, 0, tmp, 0, lineIndex);
                    }
                    lineEnds = tmp;
                }
                lineEnds[lineIndex++] = p0 - start;
            }

            lineCount = lineIndex;
            if (lineCount > 1) {
                // check if the cache is too big
                int maxCapacity = lineCount + lineCount / 3;
                if (lineEnds.length > maxCapacity) {
                    int[] tmp = new int[maxCapacity];
                    System.arraycopy(lineEnds, 0, tmp, 0, lineCount);
                    lineEnds = tmp;
                }
            }

            if (lineEnds != null && lineEnds != oldLineEnds) {
                lineCache = new SoftReference<int[]>(lineEnds);
            }
            return lineEnds;
        }

        /**
         * Binary search in the cache for line containing specified offset
         * (which is relative to the beginning of the view). This method
         * assumes that cache exists.
         */
        private int findLine(int offset) {
            int[] lineEnds = lineCache.get();
            if (offset < lineEnds[0]) {
                return 0;
            } else if (offset > lineEnds[lineCount - 1]) {
                return lineCount;
            } else {
                return findLine(lineEnds, offset, 0, lineCount - 1);
            }
        }

        private int findLine(int[] array, int offset, int min, int max) {
            if (max - min <= 1) {
                return max;
            } else {
                int mid = (max + min) / 2;
                return (offset < array[mid]) ?
                        findLine(array, offset, min, mid) :
                        findLine(array, offset, mid, max);
            }
        }

        int lineCount;
        SoftReference<int[]> lineCache = null;
    }
}
