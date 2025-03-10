/*
 * Copyright (c) 2000, 2001, Oracle and/or its affiliates. All rights reserved.
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

import javax.sql.RowSetInternal;
import java.sql.*;

/**
 * An object that implements the <code>RowSetWriter</code> interface,
 * called a <i>writer</i>. A writer may be registered with a <code>RowSet</code>
 * object that supports the reader/writer paradigm.
 * <P>
 * If a disconnected <code>RowSet</code> object modifies some of its data,
 * and it has a writer associated with it, it may be implemented so that it
 * calls on the writer's <code>writeData</code> method internally
 * to write the updates back to the data source. In order to do this, the writer
 * must first establish a connection with the rowset's data source.
 * <P>
 * If the data to be updated has already been changed in the data source, there
 * is a conflict, in which case the writer will not write
 * the changes to the data source.  The algorithm the writer uses for preventing
 * or limiting conflicts depends entirely on its implementation.
 *
 * @since 1.4
 */

public interface RowSetWriter {

  /**
   * Writes the changes in this <code>RowSetWriter</code> object's
   * rowset back to the data source from which it got its data.
   *
   * @param caller the <code>RowSet</code> object (1) that has implemented the
   *         <code>RowSetInternal</code> interface, (2) with which this writer is
   *        registered, and (3) that called this method internally
   * @return <code>true</code> if the modified data was written; <code>false</code>
   *          if not, which will be the case if there is a conflict
   * @exception SQLException if a database access error occurs
   */
  boolean writeData(RowSetInternal caller) throws SQLException;

}
