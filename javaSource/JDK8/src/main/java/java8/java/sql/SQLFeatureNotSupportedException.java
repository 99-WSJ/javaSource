/*
 * Copyright (c) 2005, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java8.java.sql;

import java.sql.SQLException;
import java.sql.SQLNonTransientException;

/**
 * The subclass of {@link SQLException} thrown when the SQLState class value is '<i>0A</i>'
 * ( the value is 'zero' A).
 * This indicates that the JDBC driver does not support an optional JDBC feature.
 * Optional JDBC features can fall into the fallowing categories:
 *
 *<UL>
 *<LI>no support for an optional feature
 *<LI>no support for an optional overloaded method
 *<LI>no support for an optional mode for a method.  The mode for a method is
 *determined based on constants passed as parameter values to a method
 *</UL>
 *
 * @since 1.6
 */
public class SQLFeatureNotSupportedException extends SQLNonTransientException {

        /**
         * Constructs a <code>SQLFeatureNotSupportedException</code> object.
         *  The <code>reason</code>, <code>SQLState</code> are initialized
         * to <code>null</code> and the vendor code is initialized to 0.
         *
         * The <code>cause</code> is not initialized, and may subsequently be
         * initialized by a call to the
         * {@link Throwable#initCause(Throwable)} method.
         * <p>
         * @since 1.6
         */
        public SQLFeatureNotSupportedException() {
                super();
        }

        /**
         * Constructs a <code>SQLFeatureNotSupportedException</code> object
         * with a given <code>reason</code>. The <code>SQLState</code>
         * is initialized to <code>null</code> and the vendor code is initialized
         * to 0.
         *
         * The <code>cause</code> is not initialized, and may subsequently be
         * initialized by a call to the
         * {@link Throwable#initCause(Throwable)} method.
         * <p>
         * @param reason a description of the exception
         * @since 1.6
         */
        public SQLFeatureNotSupportedException(String reason) {
                super(reason);
        }

        /**
         * Constructs a <code>SQLFeatureNotSupportedException</code> object
         * with a given <code>reason</code> and <code>SQLState</code>.
         *
         * The <code>cause</code> is not initialized, and may subsequently be
         * initialized by a call to the
         * {@link Throwable#initCause(Throwable)} method. The vendor code
         * is initialized to 0.
         * <p>
         * @param reason a description of the exception
         * @param SQLState an XOPEN or SQL:2003 code identifying the exception
         * @since 1.6
         */
        public SQLFeatureNotSupportedException(String reason, String SQLState) {
                super(reason,SQLState);
        }

        /**
         * Constructs a <code>SQLFeatureNotSupportedException</code> object
         *  with a given <code>reason</code>, <code>SQLState</code>  and
         * <code>vendorCode</code>.
         *
         * The <code>cause</code> is not initialized, and may subsequently be
         * initialized by a call to the
         * {@link Throwable#initCause(Throwable)} method.
         * <p>
         * @param reason a description of the exception
         * @param SQLState an XOPEN or SQL:2003 code identifying the exception
         * @param vendorCode a database vendor specific exception code
         * @since 1.6
         */
        public SQLFeatureNotSupportedException(String reason, String SQLState, int vendorCode) {
                super(reason,SQLState,vendorCode);
        }

    /**
     * Constructs a <code>SQLFeatureNotSupportedException</code> object
     *   with a given  <code>cause</code>.
     * The <code>SQLState</code> is initialized
     * to <code>null</code> and the vendor code is initialized to 0.
     * The <code>reason</code>  is initialized to <code>null</code> if
     * <code>cause==null</code> or to <code>cause.toString()</code> if
     * <code>cause!=null</code>.
     * <p>
     * @param cause the underlying reason for this <code>SQLException</code> (which is saved for later retrieval by the <code>getCause()</code> method); may be null indicating
     *     the cause is non-existent or unknown.
     * @since 1.6
     */
    public SQLFeatureNotSupportedException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a <code>SQLFeatureNotSupportedException</code> object
     * with a given
     * <code>reason</code> and  <code>cause</code>.
     * The <code>SQLState</code> is  initialized to <code>null</code>
     * and the vendor code is initialized to 0.
     * <p>
     * @param reason a description of the exception.
     * @param cause the underlying reason for this <code>SQLException</code> (which is saved for later retrieval by the <code>getCause()</code> method); may be null indicating
     *     the cause is non-existent or unknown.
     * @since 1.6
     */
    public SQLFeatureNotSupportedException(String reason, Throwable cause) {
        super(reason,cause);
    }

    /**
     * Constructs a <code>SQLFeatureNotSupportedException</code> object
     * with a given
     * <code>reason</code>, <code>SQLState</code> and  <code>cause</code>.
     * The vendor code is initialized to 0.
     * <p>
     * @param reason a description of the exception.
     * @param SQLState an XOPEN or SQL:2003 code identifying the exception
     * @param cause the (which is saved for later retrieval by the <code>getCause()</code> method); may be null indicating
     *     the cause is non-existent or unknown.
     * @since 1.6
     */
    public SQLFeatureNotSupportedException(String reason, String SQLState, Throwable cause) {
        super(reason,SQLState,cause);
    }

    /**
     *  Constructs a <code>SQLFeatureNotSupportedException</code> object
     * with a given
     * <code>reason</code>, <code>SQLState</code>, <code>vendorCode</code>
     * and  <code>cause</code>.
     * <p>
     * @param reason a description of the exception
     * @param SQLState an XOPEN or SQL:2003 code identifying the exception
     * @param vendorCode a database vendor-specific exception code
     * @param cause the underlying reason for this <code>SQLException</code> (which is saved for later retrieval by the <code>getCause()</code> method); may be null indicating
     *     the cause is non-existent or unknown.
     * @since 1.6
     */
    public SQLFeatureNotSupportedException(String reason, String SQLState, int vendorCode, Throwable cause) {
        super(reason,SQLState,vendorCode,cause);
    }

    private static final long serialVersionUID = -1026510870282316051L;
}
