/*
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

/*
 *
 *
 *
 *
 *
 * Copyright (c) 2000 World Wide Web Consortium,
 * (Massachusetts Institute of Technology, Institut National de
 * Recherche en Informatique et en Automatique, Keio University). All
 * Rights Reserved. This program is distributed under the W3C's Software
 * Intellectual Property License. This program is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See W3C License http://www.w3.org/Consortium/Legal/ for more
 * details.
 */

package java8.org.w3c.dom.html;

import org.w3c.dom.DOMException;
import org.w3c.dom.html.HTMLCollection;
import org.w3c.dom.html.HTMLElement;

/**
 *  The <code>THEAD</code> , <code>TFOOT</code> , and <code>TBODY</code>
 * elements.
 * <p>See also the <a href='http://www.w3.org/TR/2000/CR-DOM-Level-2-20000510'>Document Object Model (DOM) Level 2 Specification</a>.
 */
public interface HTMLTableSectionElement extends HTMLElement {
    /**
     *  Horizontal alignment of data in cells. See the <code>align</code>
     * attribute for HTMLTheadElement for details.
     */
    public String getAlign();
    public void setAlign(String align);

    /**
     *  Alignment character for cells in a column. See the  char attribute
     * definition in HTML 4.0.
     */
    public String getCh();
    public void setCh(String ch);

    /**
     *  Offset of alignment character. See the  charoff attribute definition
     * in HTML 4.0.
     */
    public String getChOff();
    public void setChOff(String chOff);

    /**
     *  Vertical alignment of data in cells. See the <code>valign</code>
     * attribute for HTMLTheadElement for details.
     */
    public String getVAlign();
    public void setVAlign(String vAlign);

    /**
     *  The collection of rows in this table section.
     */
    public HTMLCollection getRows();

    /**
     *  Insert a row into this section. The new row is inserted immediately
     * before the current <code>index</code> th row in this section. If
     * <code>index</code> is equal to the number of rows in this section, the
     * new row is appended.
     * @param index  The row number where to insert a new row. This index
     *   starts from 0 and is relative only to the rows contained inside this
     *   section, not all the rows in the table.
     * @return  The newly created row.
     * @exception DOMException
     *    INDEX_SIZE_ERR: Raised if the specified index is greater than the
     *   number of rows of if the index is neagative.
     */
    public HTMLElement insertRow(int index)
                                 throws DOMException;

    /**
     *  Delete a row from this section.
     * @param index  The index of the row to be deleted. This index starts
     *   from 0 and is relative only to the rows contained inside this
     *   section, not all the rows in the table.
     * @exception DOMException
     *    INDEX_SIZE_ERR: Raised if the specified index is greater than or
     *   equal to the number of rows or if the index is negative.
     */
    public void deleteRow(int index)
                          throws DOMException;

}
