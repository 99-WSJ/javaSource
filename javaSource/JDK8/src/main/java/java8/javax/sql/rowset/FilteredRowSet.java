/*
 * Copyright (c) 2003, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java8.javax.sql.rowset;

import java.sql.*;
import javax.sql.*;
import javax.naming.*;
import javax.sql.rowset.Predicate;
import javax.sql.rowset.WebRowSet;
import java.io.*;
import java.math.*;

/**
 * The standard interface that all standard implementations of
 * <code>FilteredRowSet</code> must implement. The <code>FilteredRowSetImpl</code> class
 * provides the reference implementation which may be extended if required.
 * Alternatively, a vendor is free to implement its own version
 * by implementing this interface.
 *
 * <h3>1.0 Background</h3>
 *
 * There are occasions when a <code>RowSet</code> object has a need to provide a degree
 * of filtering to its contents. One possible solution is to provide
 * a query language for all standard <code>RowSet</code> implementations; however,
 * this is an impractical approach for lightweight components such as disconnected
 * <code>RowSet</code>
 * objects. The <code>FilteredRowSet</code> interface seeks to address this need
 * without supplying a heavyweight query language along with the processing that
 * such a query language would require.
 * <p>
 * A JDBC <code>FilteredRowSet</code> standard implementation implements the
 * <code>RowSet</code> interfaces and extends the
 * <code>CachedRowSet</code>&trade; class. The
 * <code>CachedRowSet</code> class provides a set of protected cursor manipulation
 * methods, which a <code>FilteredRowSet</code> implementation can override
 * to supply filtering support.
 *
 * <h3>2.0 Predicate Sharing</h3>
 *
 * If a <code>FilteredRowSet</code> implementation is shared using the
 * inherited <code>createShared</code> method in parent interfaces, the
 * <code>Predicate</code> should be shared without modification by all
 * <code>FilteredRowSet</code> instance clones.
 *
 * <h3>3.0 Usage</h3>
 * <p>
 * By implementing a <code>Predicate</code> (see example in <a href="Predicate.html">Predicate</a>
 * class JavaDoc), a <code>FilteredRowSet</code> could then be used as described
 * below.
 *
 * <pre>
 * {@code
 *     FilteredRowSet frs = new FilteredRowSetImpl();
 *     frs.populate(rs);
 *
 *     Range name = new Range("Alpha", "Bravo", "columnName");
 *     frs.setFilter(name);
 *
 *     frs.next() // only names from "Alpha" to "Bravo" will be returned
 * }
 * </pre>
 * In the example above, we initialize a <code>Range</code> object which
 * implements the <code>Predicate</code> interface. This object expresses
 * the following constraints: All rows outputted or modified from this
 * <code>FilteredRowSet</code> object must fall between the values 'Alpha' and
 * 'Bravo' both values inclusive, in the column 'columnName'. If a filter is
 * applied to a <code>FilteredRowSet</code> object that contains no data that
 * falls within the range of the filter, no rows are returned.
 * <p>
 * This framework allows multiple classes implementing predicates to be
 * used in combination to achieved the required filtering result with
 * out the need for query language processing.
 *
 * <h3>4.0 Updating a <code>FilteredRowSet</code> Object</h3>
 * The predicate set on a <code>FilteredRowSet</code> object
 * applies a criterion on all rows in a
 * <code>RowSet</code> object to manage a subset of rows in a <code>RowSet</code>
 * object. This criterion governs the subset of rows that are visible and also
 * defines which rows can be modified, deleted or inserted.
 * <p>
 * Therefore, the predicate set on a <code>FilteredRowSet</code> object must be
 * considered as bi-directional and the set criterion as the gating mechanism
 * for all views and updates to the <code>FilteredRowSet</code> object. Any attempt
 * to update the <code>FilteredRowSet</code> that violates the criterion will
 * result in a <code>SQLException</code> object being thrown.
 * <p>
 * The <code>FilteredRowSet</code> range criterion can be modified by applying
 * a new <code>Predicate</code> object to the <code>FilteredRowSet</code>
 * instance at any time. This is  possible if no additional references to the
 * <code>FilteredRowSet</code> object are detected. A new filter has has an
 * immediate effect on criterion enforcement within the
 * <code>FilteredRowSet</code> object, and all subsequent views and updates will be
 * subject to similar enforcement.
 *
 * <h3>5.0 Behavior of Rows Outside the Filter</h3>
 * Rows that fall outside of the filter set on a <code>FilteredRowSet</code>
 * object cannot be modified until the filter is removed or a
 * new filter is applied.
 * <p>
 * Furthermore, only rows that fall within the bounds of a filter will be
 * synchronized with the data source.
 *
 * @author Jonathan Bruce
 */

public interface FilteredRowSet extends WebRowSet {

   /**
    * Applies the given <code>Predicate</code> object to this
    * <code>FilteredRowSet</code>
    * object. The filter applies controls both to inbound and outbound views,
    * constraining which rows are visible and which
    * rows can be manipulated.
    * <p>
    * A new <code>Predicate</code> object may be set at any time. This has the
    * effect of changing constraints on the <code>RowSet</code> object's data.
    * In addition, modifying the filter at runtime presents issues whereby
    * multiple components may be operating on one <code>FilteredRowSet</code> object.
    * Application developers must take responsibility for managing multiple handles
    * to <code>FilteredRowSet</code> objects when their underling <code>Predicate</code>
    * objects change.
    *
    * @param p a <code>Predicate</code> object defining the filter for this
    * <code>FilteredRowSet</code> object. Setting a <b>null</b> value
    * will clear the predicate, allowing all rows to become visible.
    *
    * @throws SQLException if an error occurs when setting the
    *     <code>Predicate</code> object
    */
    public void setFilter(Predicate p) throws SQLException;

   /**
    * Retrieves the active filter for this <code>FilteredRowSet</code> object.
    *
    * @return p the <code>Predicate</code> for this <code>FilteredRowSet</code>
    * object; <code>null</code> if no filter has been set.
    */
    public Predicate getFilter() ;
}
