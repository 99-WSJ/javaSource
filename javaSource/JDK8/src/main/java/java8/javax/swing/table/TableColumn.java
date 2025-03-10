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

package java8.javax.swing.table;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.SwingPropertyChangeSupport;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.lang.Integer;
import java.awt.Color;
import java.awt.Component;
import java.io.Serializable;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 *  A <code>TableColumn</code> represents all the attributes of a column in a
 *  <code>JTable</code>, such as width, resizability, minimum and maximum width.
 *  In addition, the <code>TableColumn</code> provides slots for a renderer and
 *  an editor that can be used to display and edit the values in this column.
 *  <p>
 *  It is also possible to specify renderers and editors on a per type basis
 *  rather than a per column basis - see the
 *  <code>setDefaultRenderer</code> method in the <code>JTable</code> class.
 *  This default mechanism is only used when the renderer (or
 *  editor) in the <code>TableColumn</code> is <code>null</code>.
 * <p>
 *  The <code>TableColumn</code> stores the link between the columns in the
 *  <code>JTable</code> and the columns in the <code>TableModel</code>.
 *  The <code>modelIndex</code> is the column in the
 *  <code>TableModel</code>, which will be queried for the data values for the
 *  cells in this column. As the column moves around in the view this
 *  <code>modelIndex</code> does not change.
 *  <p>
 * <b>Note:</b> Some implementations may assume that all
 *    <code>TableColumnModel</code>s are unique, therefore we would
 *    recommend that the same <code>TableColumn</code> instance
 *    not be added more than once to a <code>TableColumnModel</code>.
 *    To show <code>TableColumn</code>s with the same column of
 *    data from the model, create a new instance with the same
 *    <code>modelIndex</code>.
 *  <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running
 * the same version of Swing.  As of 1.4, support for long term storage
 * of all JavaBeans&trade;
 * has been added to the <code>java.beans</code> package.
 * Please see {@link java.beans.XMLEncoder}.
 *
 * @author Alan Chung
 * @author Philip Milne
 * @see javax.swing.table.TableColumnModel
 *
 * @see javax.swing.table.DefaultTableColumnModel
 * @see JTableHeader#getDefaultRenderer()
 * @see JTable#getDefaultRenderer(Class)
 * @see JTable#getDefaultEditor(Class)
 * @see JTable#getCellRenderer(int, int)
 * @see JTable#getCellEditor(int, int)
 */
public class TableColumn extends Object implements Serializable {

    /**
     * Obsolete as of Java 2 platform v1.3.  Please use string literals to identify
     * properties.
     */
    /*
     * Warning: The value of this constant, "columWidth" is wrong as the
     * name of the property is "columnWidth".
     */
    public final static String COLUMN_WIDTH_PROPERTY = "columWidth";

    /**
     * Obsolete as of Java 2 platform v1.3.  Please use string literals to identify
     * properties.
     */
    public final static String HEADER_VALUE_PROPERTY = "headerValue";

    /**
     * Obsolete as of Java 2 platform v1.3.  Please use string literals to identify
     * properties.
     */
    public final static String HEADER_RENDERER_PROPERTY = "headerRenderer";

    /**
     * Obsolete as of Java 2 platform v1.3.  Please use string literals to identify
     * properties.
     */
    public final static String CELL_RENDERER_PROPERTY = "cellRenderer";

//
//  Instance Variables
//

    /**
      * The index of the column in the model which is to be displayed by
      * this <code>TableColumn</code>. As columns are moved around in the
      * view <code>modelIndex</code> remains constant.
      */
    protected int       modelIndex;

    /**
     *  This object is not used internally by the drawing machinery of
     *  the <code>JTable</code>; identifiers may be set in the
     *  <code>TableColumn</code> as as an
     *  optional way to tag and locate table columns. The table package does
     *  not modify or invoke any methods in these identifier objects other
     *  than the <code>equals</code> method which is used in the
     *  <code>getColumnIndex()</code> method in the
     *  <code>DefaultTableColumnModel</code>.
     */
    protected Object    identifier;

    /** The width of the column. */
    protected int       width;

    /** The minimum width of the column. */
    protected int       minWidth;

    /** The preferred width of the column. */
    private int         preferredWidth;

    /** The maximum width of the column. */
    protected int       maxWidth;

    /** The renderer used to draw the header of the column. */
    protected TableCellRenderer headerRenderer;

    /** The header value of the column. */
    protected Object            headerValue;

    /** The renderer used to draw the data cells of the column. */
    protected TableCellRenderer cellRenderer;

    /** The editor used to edit the data cells of the column. */
    protected TableCellEditor   cellEditor;

    /** If true, the user is allowed to resize the column; the default is true. */
    protected boolean   isResizable;

    /**
     * This field was not used in previous releases and there are
     * currently no plans to support it in the future.
     *
     * @deprecated as of Java 2 platform v1.3
     */
    /*
     *  Counter used to disable posting of resizing notifications until the
     *  end of the resize.
     */
    @Deprecated
    transient protected int     resizedPostingDisableCount;

    /**
     * If any <code>PropertyChangeListeners</code> have been registered, the
     * <code>changeSupport</code> field describes them.
     */
    private SwingPropertyChangeSupport changeSupport;

//
// Constructors
//

    /**
     *  Cover method, using a default model index of 0,
     *  default width of 75, a <code>null</code> renderer and a
     *  <code>null</code> editor.
     *  This method is intended for serialization.
     *  @see #TableColumn(int, int, TableCellRenderer, TableCellEditor)
     */
    public TableColumn() {
        this(0);
    }

    /**
     *  Cover method, using a default width of 75, a <code>null</code>
     *  renderer and a <code>null</code> editor.
     *  @see #TableColumn(int, int, TableCellRenderer, TableCellEditor)
     */
    public TableColumn(int modelIndex) {
        this(modelIndex, 75, null, null);
    }

    /**
     *  Cover method, using a <code>null</code> renderer and a
     *  <code>null</code> editor.
     *  @see #TableColumn(int, int, TableCellRenderer, TableCellEditor)
     */
    public TableColumn(int modelIndex, int width) {
        this(modelIndex, width, null, null);
    }

    /**
     *  Creates and initializes an instance of
     *  <code>TableColumn</code> with the specified model index,
     *  width, cell renderer, and cell editor;
     *  all <code>TableColumn</code> constructors delegate to this one.
     *  The value of <code>width</code> is used
     *  for both the initial and preferred width;
     *  if <code>width</code> is negative,
     *  they're set to 0.
     *  The minimum width is set to 15 unless the initial width is less,
     *  in which case the minimum width is set to
     *  the initial width.
     *
     *  <p>
     *  When the <code>cellRenderer</code>
     *  or <code>cellEditor</code> parameter is <code>null</code>,
     *  a default value provided by the <code>JTable</code>
     *  <code>getDefaultRenderer</code>
     *  or <code>getDefaultEditor</code> method, respectively,
     *  is used to
     *  provide defaults based on the type of the data in this column.
     *  This column-centric rendering strategy can be circumvented by overriding
     *  the <code>getCellRenderer</code> methods in <code>JTable</code>.
     *
     * @param modelIndex the index of the column
     *  in the model that supplies the data for this column in the table;
     *  the model index remains the same
     *  even when columns are reordered in the view
     * @param width this column's preferred width and initial width
     * @param cellRenderer the object used to render values in this column
     * @param cellEditor the object used to edit values in this column
     * @see #getMinWidth()
     * @see JTable#getDefaultRenderer(Class)
     * @see JTable#getDefaultEditor(Class)
     * @see JTable#getCellRenderer(int, int)
     * @see JTable#getCellEditor(int, int)
     */
    public TableColumn(int modelIndex, int width,
                                 TableCellRenderer cellRenderer,
                                 TableCellEditor cellEditor) {
        super();
        this.modelIndex = modelIndex;
        preferredWidth = this.width = Math.max(width, 0);

        this.cellRenderer = cellRenderer;
        this.cellEditor = cellEditor;

        // Set other instance variables to default values.
        minWidth = Math.min(15, this.width);
        maxWidth = Integer.MAX_VALUE;
        isResizable = true;
        resizedPostingDisableCount = 0;
        headerValue = null;
    }

//
// Modifying and Querying attributes
//

    private void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        if (changeSupport != null) {
            changeSupport.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    private void firePropertyChange(String propertyName, int oldValue, int newValue) {
        if (oldValue != newValue) {
            firePropertyChange(propertyName, Integer.valueOf(oldValue), Integer.valueOf(newValue));
        }
    }

    private void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        if (oldValue != newValue) {
            firePropertyChange(propertyName, Boolean.valueOf(oldValue), Boolean.valueOf(newValue));
        }
    }

    /**
     * Sets the model index for this column. The model index is the
     * index of the column in the model that will be displayed by this
     * <code>TableColumn</code>. As the <code>TableColumn</code>
     * is moved around in the view the model index remains constant.
     * @param  modelIndex  the new modelIndex
     * @beaninfo
     *  bound: true
     *  description: The model index.
     */
    public void setModelIndex(int modelIndex) {
        int old = this.modelIndex;
        this.modelIndex = modelIndex;
        firePropertyChange("modelIndex", old, modelIndex);
    }

    /**
     * Returns the model index for this column.
     * @return the <code>modelIndex</code> property
     */
    public int getModelIndex() {
        return modelIndex;
    }

    /**
     * Sets the <code>TableColumn</code>'s identifier to
     * <code>anIdentifier</code>. <p>
     * Note: identifiers are not used by the <code>JTable</code>,
     * they are purely a
     * convenience for the external tagging and location of columns.
     *
     * @param      identifier           an identifier for this column
     * @see        #getIdentifier
     * @beaninfo
     *  bound: true
     *  description: A unique identifier for this column.
     */
    public void setIdentifier(Object identifier) {
        Object old = this.identifier;
        this.identifier = identifier;
        firePropertyChange("identifier", old, identifier);
    }


    /**
     *  Returns the <code>identifier</code> object for this column.
     *  Note identifiers are not used by <code>JTable</code>,
     *  they are purely a convenience for external use.
     *  If the <code>identifier</code> is <code>null</code>,
     *  <code>getIdentifier()</code> returns <code>getHeaderValue</code>
     *  as a default.
     *
     * @return  the <code>identifier</code> property
     * @see     #setIdentifier
     */
    public Object getIdentifier() {
        return (identifier != null) ? identifier : getHeaderValue();

    }

    /**
     * Sets the <code>Object</code> whose string representation will be
     * used as the value for the <code>headerRenderer</code>.  When the
     * <code>TableColumn</code> is created, the default <code>headerValue</code>
     * is <code>null</code>.
     * @param headerValue  the new headerValue
     * @see       #getHeaderValue
     * @beaninfo
     *  bound: true
     *  description: The text to be used by the header renderer.
     */
    public void setHeaderValue(Object headerValue) {
        Object old = this.headerValue;
        this.headerValue = headerValue;
        firePropertyChange("headerValue", old, headerValue);
    }

    /**
     * Returns the <code>Object</code> used as the value for the header
     * renderer.
     *
     * @return  the <code>headerValue</code> property
     * @see     #setHeaderValue
     */
    public Object getHeaderValue() {
        return headerValue;
    }

    //
    // Renderers and Editors
    //

    /**
     * Sets the <code>TableCellRenderer</code> used to draw the
     * <code>TableColumn</code>'s header to <code>headerRenderer</code>.
     * <p>
     * It is the header renderers responsibility to render the sorting
     * indicator.  If you are using sorting and specify a renderer your
     * renderer must render the sorting indication.
     *
     * @param headerRenderer  the new headerRenderer
     *
     * @see       #getHeaderRenderer
     * @beaninfo
     *  bound: true
     *  description: The header renderer.
     */
    public void setHeaderRenderer(TableCellRenderer headerRenderer) {
        TableCellRenderer old = this.headerRenderer;
        this.headerRenderer = headerRenderer;
        firePropertyChange("headerRenderer", old, headerRenderer);
    }

    /**
     * Returns the <code>TableCellRenderer</code> used to draw the header of the
     * <code>TableColumn</code>. When the <code>headerRenderer</code> is
     * <code>null</code>, the <code>JTableHeader</code>
     * uses its <code>defaultRenderer</code>. The default value for a
     * <code>headerRenderer</code> is <code>null</code>.
     *
     * @return  the <code>headerRenderer</code> property
     * @see     #setHeaderRenderer
     * @see     #setHeaderValue
     * @see     JTableHeader#getDefaultRenderer()
     */
    public TableCellRenderer getHeaderRenderer() {
        return headerRenderer;
    }

    /**
     * Sets the <code>TableCellRenderer</code> used by <code>JTable</code>
     * to draw individual values for this column.
     *
     * @param cellRenderer  the new cellRenderer
     * @see     #getCellRenderer
     * @beaninfo
     *  bound: true
     *  description: The renderer to use for cell values.
     */
    public void setCellRenderer(TableCellRenderer cellRenderer) {
        TableCellRenderer old = this.cellRenderer;
        this.cellRenderer = cellRenderer;
        firePropertyChange("cellRenderer", old, cellRenderer);
    }

    /**
     * Returns the <code>TableCellRenderer</code> used by the
     * <code>JTable</code> to draw
     * values for this column.  The <code>cellRenderer</code> of the column
     * not only controls the visual look for the column, but is also used to
     * interpret the value object supplied by the <code>TableModel</code>.
     * When the <code>cellRenderer</code> is <code>null</code>,
     * the <code>JTable</code> uses a default renderer based on the
     * class of the cells in that column. The default value for a
     * <code>cellRenderer</code> is <code>null</code>.
     *
     * @return  the <code>cellRenderer</code> property
     * @see     #setCellRenderer
     * @see     JTable#setDefaultRenderer
     */
    public TableCellRenderer getCellRenderer() {
        return cellRenderer;
    }

    /**
     * Sets the editor to used by when a cell in this column is edited.
     *
     * @param cellEditor  the new cellEditor
     * @see     #getCellEditor
     * @beaninfo
     *  bound: true
     *  description: The editor to use for cell values.
     */
    public void setCellEditor(TableCellEditor cellEditor){
        TableCellEditor old = this.cellEditor;
        this.cellEditor = cellEditor;
        firePropertyChange("cellEditor", old, cellEditor);
    }

    /**
     * Returns the <code>TableCellEditor</code> used by the
     * <code>JTable</code> to edit values for this column.  When the
     * <code>cellEditor</code> is <code>null</code>, the <code>JTable</code>
     * uses a default editor based on the
     * class of the cells in that column. The default value for a
     * <code>cellEditor</code> is <code>null</code>.
     *
     * @return  the <code>cellEditor</code> property
     * @see     #setCellEditor
     * @see     JTable#setDefaultEditor
     */
    public TableCellEditor getCellEditor() {
        return cellEditor;
    }

    /**
     * This method should not be used to set the widths of columns in the
     * <code>JTable</code>, use <code>setPreferredWidth</code> instead.
     * Like a layout manager in the
     * AWT, the <code>JTable</code> adjusts a column's width automatically
     * whenever the
     * table itself changes size, or a column's preferred width is changed.
     * Setting widths programmatically therefore has no long term effect.
     * <p>
     * This method sets this column's width to <code>width</code>.
     * If <code>width</code> exceeds the minimum or maximum width,
     * it is adjusted to the appropriate limiting value.
     * @param  width  the new width
     * @see     #getWidth
     * @see     #setMinWidth
     * @see     #setMaxWidth
     * @see     #setPreferredWidth
     * @see     JTable#doLayout()
     * @beaninfo
     *  bound: true
     *  description: The width of the column.
     */
    public void setWidth(int width) {
        int old = this.width;
        this.width = Math.min(Math.max(width, minWidth), maxWidth);
        firePropertyChange("width", old, this.width);
    }

    /**
     * Returns the width of the <code>TableColumn</code>. The default width is
     * 75.
     *
     * @return  the <code>width</code> property
     * @see     #setWidth
     */
    public int getWidth() {
        return width;
    }

    /**
     * Sets this column's preferred width to <code>preferredWidth</code>.
     * If <code>preferredWidth</code> exceeds the minimum or maximum width,
     * it is adjusted to the appropriate limiting value.
     * <p>
     * For details on how the widths of columns in the <code>JTable</code>
     * (and <code>JTableHeader</code>) are calculated from the
     * <code>preferredWidth</code>,
     * see the <code>doLayout</code> method in <code>JTable</code>.
     *
     * @param  preferredWidth the new preferred width
     * @see     #getPreferredWidth
     * @see     JTable#doLayout()
     * @beaninfo
     *  bound: true
     *  description: The preferred width of the column.
     */
    public void setPreferredWidth(int preferredWidth) {
        int old = this.preferredWidth;
        this.preferredWidth = Math.min(Math.max(preferredWidth, minWidth), maxWidth);
        firePropertyChange("preferredWidth", old, this.preferredWidth);
    }

    /**
     * Returns the preferred width of the <code>TableColumn</code>.
     * The default preferred width is 75.
     *
     * @return  the <code>preferredWidth</code> property
     * @see     #setPreferredWidth
     */
    public int getPreferredWidth() {
        return preferredWidth;
    }

    /**
     * Sets the <code>TableColumn</code>'s minimum width to
     * <code>minWidth</code>,
     * adjusting the new minimum width if necessary to ensure that
     * 0 &lt;= <code>minWidth</code> &lt;= <code>maxWidth</code>.
     * For example, if the <code>minWidth</code> argument is negative,
     * this method sets the <code>minWidth</code> property to 0.
     *
     * <p>
     * If the value of the
     * <code>width</code> or <code>preferredWidth</code> property
     * is less than the new minimum width,
     * this method sets that property to the new minimum width.
     *
     * @param minWidth  the new minimum width
     * @see     #getMinWidth
     * @see     #setPreferredWidth
     * @see     #setMaxWidth
     * @beaninfo
     *  bound: true
     *  description: The minimum width of the column.
     */
    public void setMinWidth(int minWidth) {
        int old = this.minWidth;
        this.minWidth = Math.max(Math.min(minWidth, maxWidth), 0);
        if (width < this.minWidth) {
            setWidth(this.minWidth);
        }
        if (preferredWidth < this.minWidth) {
            setPreferredWidth(this.minWidth);
        }
        firePropertyChange("minWidth", old, this.minWidth);
    }

    /**
     * Returns the minimum width for the <code>TableColumn</code>. The
     * <code>TableColumn</code>'s width can't be made less than this either
     * by the user or programmatically.
     *
     * @return  the <code>minWidth</code> property
     * @see     #setMinWidth
     * @see     #TableColumn(int, int, TableCellRenderer, TableCellEditor)
     */
    public int getMinWidth() {
        return minWidth;
    }

    /**
     * Sets the <code>TableColumn</code>'s maximum width to
     * <code>maxWidth</code> or,
     * if <code>maxWidth</code> is less than the minimum width,
     * to the minimum width.
     *
     * <p>
     * If the value of the
     * <code>width</code> or <code>preferredWidth</code> property
     * is more than the new maximum width,
     * this method sets that property to the new maximum width.
     *
     * @param maxWidth  the new maximum width
     * @see     #getMaxWidth
     * @see     #setPreferredWidth
     * @see     #setMinWidth
     * @beaninfo
     *  bound: true
     *  description: The maximum width of the column.
     */
    public void setMaxWidth(int maxWidth) {
        int old = this.maxWidth;
        this.maxWidth = Math.max(minWidth, maxWidth);
        if (width > this.maxWidth) {
            setWidth(this.maxWidth);
        }
        if (preferredWidth > this.maxWidth) {
            setPreferredWidth(this.maxWidth);
        }
        firePropertyChange("maxWidth", old, this.maxWidth);
    }

    /**
     * Returns the maximum width for the <code>TableColumn</code>. The
     * <code>TableColumn</code>'s width can't be made larger than this
     * either by the user or programmatically.  The default maxWidth
     * is Integer.MAX_VALUE.
     *
     * @return  the <code>maxWidth</code> property
     * @see     #setMaxWidth
     */
    public int getMaxWidth() {
        return maxWidth;
    }

    /**
     * Sets whether this column can be resized.
     *
     * @param isResizable  if true, resizing is allowed; otherwise false
     * @see     #getResizable
     * @beaninfo
     *  bound: true
     *  description: Whether or not this column can be resized.
     */
    public void setResizable(boolean isResizable) {
        boolean old = this.isResizable;
        this.isResizable = isResizable;
        firePropertyChange("isResizable", old, this.isResizable);
    }

    /**
     * Returns true if the user is allowed to resize the
     * <code>TableColumn</code>'s
     * width, false otherwise. You can change the width programmatically
     * regardless of this setting.  The default is true.
     *
     * @return  the <code>isResizable</code> property
     * @see     #setResizable
     */
    public boolean getResizable() {
        return isResizable;
    }

    /**
     * Resizes the <code>TableColumn</code> to fit the width of its header cell.
     * This method does nothing if the header renderer is <code>null</code>
     * (the default case). Otherwise, it sets the minimum, maximum and preferred
     * widths of this column to the widths of the minimum, maximum and preferred
     * sizes of the Component delivered by the header renderer.
     * The transient "width" property of this TableColumn is also set to the
     * preferred width. Note this method is not used internally by the table
     * package.
     *
     * @see     #setPreferredWidth
     */
    public void sizeWidthToFit() {
        if (headerRenderer == null) {
            return;
        }
        Component c = headerRenderer.getTableCellRendererComponent(null,
                                getHeaderValue(), false, false, 0, 0);

        setMinWidth(c.getMinimumSize().width);
        setMaxWidth(c.getMaximumSize().width);
        setPreferredWidth(c.getPreferredSize().width);

        setWidth(getPreferredWidth());
    }

    /**
     * This field was not used in previous releases and there are
     * currently no plans to support it in the future.
     *
     * @deprecated as of Java 2 platform v1.3
     */
    @Deprecated
    public void disableResizedPosting() {
        resizedPostingDisableCount++;
    }

    /**
     * This field was not used in previous releases and there are
     * currently no plans to support it in the future.
     *
     * @deprecated as of Java 2 platform v1.3
     */
    @Deprecated
    public void enableResizedPosting() {
        resizedPostingDisableCount--;
    }

//
// Property Change Support
//

    /**
     * Adds a <code>PropertyChangeListener</code> to the listener list.
     * The listener is registered for all properties.
     * <p>
     * A <code>PropertyChangeEvent</code> will get fired in response to an
     * explicit call to <code>setFont</code>, <code>setBackground</code>,
     * or <code>setForeground</code> on the
     * current component.  Note that if the current component is
     * inheriting its foreground, background, or font from its
     * container, then no event will be fired in response to a
     * change in the inherited property.
     *
     * @param listener  the listener to be added
     *
     */
    public synchronized void addPropertyChangeListener(
                                PropertyChangeListener listener) {
        if (changeSupport == null) {
            changeSupport = new SwingPropertyChangeSupport(this);
        }
        changeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Removes a <code>PropertyChangeListener</code> from the listener list.
     * The <code>PropertyChangeListener</code> to be removed was registered
     * for all properties.
     *
     * @param listener  the listener to be removed
     *
     */

    public synchronized void removePropertyChangeListener(
                                PropertyChangeListener listener) {
        if (changeSupport != null) {
            changeSupport.removePropertyChangeListener(listener);
        }
    }

    /**
     * Returns an array of all the <code>PropertyChangeListener</code>s added
     * to this TableColumn with addPropertyChangeListener().
     *
     * @return all of the <code>PropertyChangeListener</code>s added or an empty
     *         array if no listeners have been added
     * @since 1.4
     */
    public synchronized PropertyChangeListener[] getPropertyChangeListeners() {
        if (changeSupport == null) {
            return new PropertyChangeListener[0];
        }
        return changeSupport.getPropertyChangeListeners();
    }

//
// Protected Methods
//

    /**
     * As of Java 2 platform v1.3, this method is not called by the <code>TableColumn</code>
     * constructor.  Previously this method was used by the
     * <code>TableColumn</code> to create a default header renderer.
     * As of Java 2 platform v1.3, the default header renderer is <code>null</code>.
     * <code>JTableHeader</code> now provides its own shared default
     * renderer, just as the <code>JTable</code> does for its cell renderers.
     *
     * @return the default header renderer
     * @see JTableHeader#createDefaultRenderer()
     */
    protected TableCellRenderer createDefaultHeaderRenderer() {
        DefaultTableCellRenderer label = new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value,
                         boolean isSelected, boolean hasFocus, int row, int column) {
                if (table != null) {
                    JTableHeader header = table.getTableHeader();
                    if (header != null) {
                        setForeground(header.getForeground());
                        setBackground(header.getBackground());
                        setFont(header.getFont());
                    }
                }

                setText((value == null) ? "" : value.toString());
                setBorder(UIManager.getBorder("TableHeader.cellBorder"));
                return this;
            }
        };
        label.setHorizontalAlignment(JLabel.CENTER);
        return label;
    }

} // End of class TableColumn
