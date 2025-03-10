/*
 * Copyright (c) 1999, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java8.javax.accessibility;

import javax.accessibility.Accessible;

/**
 * Class AccessibleTable describes a user-interface component that
 * presents data in a two-dimensional table format.
 *
 * @author      Lynn Monsanto
 * @since 1.3
 */
public interface AccessibleTable {

    /**
     * Returns the caption for the table.
     *
     * @return the caption for the table
     */
    public Accessible getAccessibleCaption();

    /**
     * Sets the caption for the table.
     *
     * @param a the caption for the table
     */
    public void setAccessibleCaption(Accessible a);

    /**
     * Returns the summary description of the table.
     *
     * @return the summary description of the table
     */
    public Accessible getAccessibleSummary();

    /**
     * Sets the summary description of the table
     *
     * @param a the summary description of the table
     */
    public void setAccessibleSummary(Accessible a);

    /**
     * Returns the number of rows in the table.
     *
     * @return the number of rows in the table
     */
    public int getAccessibleRowCount();

    /**
     * Returns the number of columns in the table.
     *
     * @return the number of columns in the table
     */
    public int getAccessibleColumnCount();

    /**
     * Returns the Accessible at a specified row and column
     * in the table.
     *
     * @param r zero-based row of the table
     * @param c zero-based column of the table
     * @return the Accessible at the specified row and column
     */
    public Accessible getAccessibleAt(int r, int c);

    /**
     * Returns the number of rows occupied by the Accessible at
     * a specified row and column in the table.
     *
     * @param r zero-based row of the table
     * @param c zero-based column of the table
     * @return the number of rows occupied by the Accessible at a
     * given specified (row, column)
     */
    public int getAccessibleRowExtentAt(int r, int c);

    /**
     * Returns the number of columns occupied by the Accessible at
     * a specified row and column in the table.
     *
     * @param r zero-based row of the table
     * @param c zero-based column of the table
     * @return the number of columns occupied by the Accessible at a
     * given specified row and column
     */
    public int getAccessibleColumnExtentAt(int r, int c);

    /**
     * Returns the row headers as an AccessibleTable.
     *
     * @return an AccessibleTable representing the row
     * headers
     */
    public javax.accessibility.AccessibleTable getAccessibleRowHeader();

    /**
     * Sets the row headers.
     *
     * @param table an AccessibleTable representing the
     * row headers
     */
    public void setAccessibleRowHeader(javax.accessibility.AccessibleTable table);

    /**
     * Returns the column headers as an AccessibleTable.
     *
     * @return an AccessibleTable representing the column
     * headers
     */
    public javax.accessibility.AccessibleTable getAccessibleColumnHeader();

    /**
     * Sets the column headers.
     *
     * @param table an AccessibleTable representing the
     * column headers
     */
    public void setAccessibleColumnHeader(javax.accessibility.AccessibleTable table);

    /**
     * Returns the description of the specified row in the table.
     *
     * @param r zero-based row of the table
     * @return the description of the row
     */
    public Accessible getAccessibleRowDescription(int r);

    /**
     * Sets the description text of the specified row of the table.
     *
     * @param r zero-based row of the table
     * @param a the description of the row
     */
    public void setAccessibleRowDescription(int r, Accessible a);

    /**
     * Returns the description text of the specified column in the table.
     *
     * @param c zero-based column of the table
     * @return the text description of the column
     */
    public Accessible getAccessibleColumnDescription(int c);

    /**
     * Sets the description text of the specified column in the table.
     *
     * @param c zero-based column of the table
     * @param a the text description of the column
     */
    public void setAccessibleColumnDescription(int c, Accessible a);

    /**
     * Returns a boolean value indicating whether the accessible at
     * a specified row and column is selected.
     *
     * @param r zero-based row of the table
     * @param c zero-based column of the table
     * @return the boolean value true if the accessible at the
     * row and column is selected. Otherwise, the boolean value
     * false
     */
    public boolean isAccessibleSelected(int r, int c);

    /**
     * Returns a boolean value indicating whether the specified row
     * is selected.
     *
     * @param r zero-based row of the table
     * @return the boolean value true if the specified row is selected.
     * Otherwise, false.
     */
    public boolean isAccessibleRowSelected(int r);

    /**
     * Returns a boolean value indicating whether the specified column
     * is selected.
     *
     * @param c zero-based column of the table
     * @return the boolean value true if the specified column is selected.
     * Otherwise, false.
     */
    public boolean isAccessibleColumnSelected(int c);

    /**
     * Returns the selected rows in a table.
     *
     * @return an array of selected rows where each element is a
     * zero-based row of the table
     */
    public int [] getSelectedAccessibleRows();

    /**
     * Returns the selected columns in a table.
     *
     * @return an array of selected columns where each element is a
     * zero-based column of the table
     */
    public int [] getSelectedAccessibleColumns();
}
