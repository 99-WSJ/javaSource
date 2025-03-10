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

package java8.java.awt.print;

import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterGraphics;


/**
 * The <code>Printable</code> interface is implemented
 * by the <code>print</code> methods of the current
 * page painter, which is called by the printing
 * system to render a page.  When building a
 * {@link Pageable}, pairs of {@link PageFormat}
 * instances and instances that implement
 * this interface are used to describe each page. The
 * instance implementing <code>Printable</code> is called to
 * print the page's graphics.
 * <p>
 * A <code>Printable(..)</code> may be set on a <code>PrinterJob</code>.
 * When the client subsequently initiates printing by calling
 * <code>PrinterJob.print(..)</code> control
 * <p>
 * is handed to the printing system until all pages have been printed.
 * It does this by calling <code>Printable.print(..)</code> until
 * all pages in the document have been printed.
 * In using the <code>Printable</code> interface the printing
 * commits to image the contents of a page whenever
 * requested by the printing system.
 * <p>
 * The parameters to <code>Printable.print(..)</code> include a
 * <code>PageFormat</code> which describes the printable area of
 * the page, needed for calculating the contents that will fit the
 * page, and the page index, which specifies the zero-based print
 * stream index of the requested page.
 * <p>
 * For correct printing behaviour, the following points should be
 * observed:
 * <ul>
 * <li> The printing system may request a page index more than once.
 * On each occasion equal PageFormat parameters will be supplied.
 *
 * <li>The printing system will call <code>Printable.print(..)</code>
 * with page indexes which increase monotonically, although as noted above,
 * the <code>Printable</code> should expect multiple calls for a page index
 * and that page indexes may be skipped, when page ranges are specified
 * by the client, or by a user through a print dialog.
 *
 * <li>If multiple collated copies of a document are requested, and the
 * printer cannot natively support this, then the document may be imaged
 * multiple times. Printing will start each copy from the lowest print
 * stream page index page.
 *
 * <li>With the exception of re-imaging an entire document for multiple
 * collated copies, the increasing page index order means that when
 * page N is requested if a client needs to calculate page break position,
 * it may safely discard any state related to pages &lt; N, and make current
 * that for page N. "State" usually is just the calculated position in the
 * document that corresponds to the start of the page.
 *
 * <li>When called by the printing system the <code>Printable</code> must
 * inspect and honour the supplied PageFormat parameter as well as the
 * page index.  The format of the page to be drawn is specified by the
 * supplied PageFormat. The size, orientation and imageable area of the page
 * is therefore already determined and rendering must be within this
 * imageable area.
 * This is key to correct printing behaviour, and it has the
 * implication that the client has the responsibility of tracking
 * what content belongs on the specified page.
 *
 * <li>When the <code>Printable</code> is obtained from a client-supplied
 * <code>Pageable</code> then the client may provide different PageFormats
 * for each page index. Calculations of page breaks must account for this.
 * </ul>
 * <p>
 * @see Pageable
 * @see PageFormat
 * @see java.awt.print.PrinterJob
 */
public interface Printable {

    /**
     * Returned from {@link #print(Graphics, PageFormat, int)}
     * to signify that the requested page was rendered.
     */
    int PAGE_EXISTS = 0;

    /**
     * Returned from <code>print</code> to signify that the
     * <code>pageIndex</code> is too large and that the requested page
     * does not exist.
     */
    int NO_SUCH_PAGE = 1;

    /**
     * Prints the page at the specified index into the specified
     * {@link Graphics} context in the specified
     * format.  A <code>PrinterJob</code> calls the
     * <code>Printable</code> interface to request that a page be
     * rendered into the context specified by
     * <code>graphics</code>.  The format of the page to be drawn is
     * specified by <code>pageFormat</code>.  The zero based index
     * of the requested page is specified by <code>pageIndex</code>.
     * If the requested page does not exist then this method returns
     * NO_SUCH_PAGE; otherwise PAGE_EXISTS is returned.
     * The <code>Graphics</code> class or subclass implements the
     * {@link PrinterGraphics} interface to provide additional
     * information.  If the <code>Printable</code> object
     * aborts the print job then it throws a {@link PrinterException}.
     * @param graphics the context into which the page is drawn
     * @param pageFormat the size and orientation of the page being drawn
     * @param pageIndex the zero based index of the page to be drawn
     * @return PAGE_EXISTS if the page is rendered successfully
     *         or NO_SUCH_PAGE if <code>pageIndex</code> specifies a
     *         non-existent page.
     * @exception PrinterException
     *         thrown when the print job is terminated.
     */
    int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
                 throws PrinterException;

}
