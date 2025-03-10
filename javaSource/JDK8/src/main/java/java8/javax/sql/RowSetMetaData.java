/*
 * Copyright (c) 2000, 2005, Oracle and/or its affiliates. All rights reserved.
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

package java8.javax.sql;

import java.sql.*;

/**
 * An object that contains information about the columns in a
 * <code>RowSet</code> object.  This interface is
 * an extension of the <code>ResultSetMetaData</code> interface with
 * methods for setting the values in a <code>RowSetMetaData</code> object.
 * When a <code>RowSetReader</code> object reads data into a <code>RowSet</code>
 * object, it creates a <code>RowSetMetaData</code> object and initializes it
 * using the methods in the <code>RowSetMetaData</code> interface.  Then the
 * reader passes the <code>RowSetMetaData</code> object to the rowset.
 * <P>
 * The methods in this interface are invoked internally when an application
 * calls the method <code>RowSet.execute</code>; an application
 * programmer would not use them directly.
 *
 * @since 1.4
 */

public interface RowSetMetaData extends ResultSetMetaData {

  /**
   * Sets the number of columns in the <code>RowSet</code> object to
   * the given number.
   *
   * @param columnCount the number of columns in the <code>RowSet</code> object
   * @exception SQLException if a database access error occurs
   */
  void setColumnCount(int columnCount) throws SQLException;

  /**
   * Sets whether the designated column is automatically numbered,
   * The default is for a <code>RowSet</code> object's
   * columns not to be automatically numbered.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param property <code>true</code> if the column is automatically
   *                 numbered; <code>false</code> if it is not
   *
   * @exception SQLException if a database access error occurs
   */
  void setAutoIncrement(int columnIndex, boolean property) throws SQLException;

  /**
   * Sets whether the designated column is case sensitive.
   * The default is <code>false</code>.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param property <code>true</code> if the column is case sensitive;
   *                 <code>false</code> if it is not
   *
   * @exception SQLException if a database access error occurs
   */
  void setCaseSensitive(int columnIndex, boolean property) throws SQLException;

  /**
   * Sets whether the designated column can be used in a where clause.
   * The default is <code>false</code>.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param property <code>true</code> if the column can be used in a
   *                 <code>WHERE</code> clause; <code>false</code> if it cannot
   *
   * @exception SQLException if a database access error occurs
   */
  void setSearchable(int columnIndex, boolean property) throws SQLException;

  /**
   * Sets whether the designated column is a cash value.
   * The default is <code>false</code>.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param property <code>true</code> if the column is a cash value;
   *                 <code>false</code> if it is not
   *
   * @exception SQLException if a database access error occurs
   */
  void setCurrency(int columnIndex, boolean property) throws SQLException;

  /**
   * Sets whether the designated column's value can be set to
   * <code>NULL</code>.
   * The default is <code>ResultSetMetaData.columnNullableUnknown</code>
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param property one of the following constants:
   *                 <code>ResultSetMetaData.columnNoNulls</code>,
   *                 <code>ResultSetMetaData.columnNullable</code>, or
   *                 <code>ResultSetMetaData.columnNullableUnknown</code>
   *
   * @exception SQLException if a database access error occurs
   */
  void setNullable(int columnIndex, int property) throws SQLException;

  /**
   * Sets whether the designated column is a signed number.
   * The default is <code>false</code>.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param property <code>true</code> if the column is a signed number;
   *                 <code>false</code> if it is not
   *
   * @exception SQLException if a database access error occurs
   */
  void setSigned(int columnIndex, boolean property) throws SQLException;

  /**
   * Sets the designated column's normal maximum width in chars to the
   * given <code>int</code>.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param size the normal maximum number of characters for
   *           the designated column
   *
   * @exception SQLException if a database access error occurs
   */
  void setColumnDisplaySize(int columnIndex, int size) throws SQLException;

  /**
   * Sets the suggested column title for use in printouts and
   * displays, if any, to the given <code>String</code>.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param label the column title
   * @exception SQLException if a database access error occurs
   */
  void setColumnLabel(int columnIndex, String label) throws SQLException;

  /**
   * Sets the name of the designated column to the given <code>String</code>.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param columnName the designated column's name
   * @exception SQLException if a database access error occurs
   */
  void setColumnName(int columnIndex, String columnName) throws SQLException;

  /**
   * Sets the name of the designated column's table's schema, if any, to
   * the given <code>String</code>.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param schemaName the schema name
   * @exception SQLException if a database access error occurs
   */
  void setSchemaName(int columnIndex, String schemaName) throws SQLException;

  /**
   * Sets the designated column's number of decimal digits to the
   * given <code>int</code>.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param precision the total number of decimal digits
   * @exception SQLException if a database access error occurs
   */
  void setPrecision(int columnIndex, int precision) throws SQLException;

  /**
   * Sets the designated column's number of digits to the
   * right of the decimal point to the given <code>int</code>.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param scale the number of digits to right of decimal point
   * @exception SQLException if a database access error occurs
   */
  void setScale(int columnIndex, int scale) throws SQLException;

  /**
   * Sets the designated column's table name, if any, to the given
   * <code>String</code>.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param tableName the column's table name
   * @exception SQLException if a database access error occurs
   */
  void setTableName(int columnIndex, String tableName) throws SQLException;

  /**
   * Sets the designated column's table's catalog name, if any, to the given
   * <code>String</code>.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param catalogName the column's catalog name
   * @exception SQLException if a database access error occurs
   */
  void setCatalogName(int columnIndex, String catalogName) throws SQLException;

  /**
   * Sets the designated column's SQL type to the one given.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param SQLType the column's SQL type
   * @exception SQLException if a database access error occurs
   * @see Types
   */
  void setColumnType(int columnIndex, int SQLType) throws SQLException;

  /**
   * Sets the designated column's type name that is specific to the
   * data source, if any, to the given <code>String</code>.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param typeName data source specific type name.
   * @exception SQLException if a database access error occurs
   */
  void setColumnTypeName(int columnIndex, String typeName) throws SQLException;

}
