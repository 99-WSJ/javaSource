/*
 * Copyright (c) 1996, 2013, Oracle and/or its affiliates. All rights reserved.
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
import java.sql.SQLTransientException;

/**
 * <P>The subclass of {@link SQLException} thrown when the timeout specified by
 * {@code Statement.setQueryTimeout}, {@code DriverManager.setLoginTimeout},
 * {@code DataSource.setLoginTimeout},{@code XADataSource.setLoginTimeout}
 * has expired.
 * <P> This exception does not correspond to a standard SQLState.
 *
 * @since 1.6
 */

public class SQLTimeoutException extends SQLTransientException {
        /**
         * Constructs a <code>SQLTimeoutException</code> object.
         * The <code>reason</code>, <code>SQLState</code> are initialized
         * to <code>null</code> and the vendor code is initialized to 0.
         *
         * The <code>cause</code> is not initialized, and may subsequently be
         * initialized by a call to the
         * {@link Throwable#initCause(Throwable)} method.
         * <p>
         * @since 1.6
         */
        public SQLTimeoutException() {
                super();
        }

        /**
         * Constructs a <code>SQLTimeoutException</code> object
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
        public SQLTimeoutException(String reason) {
                super(reason);
        }

        /**
         * Constructs a <code>SQLTimeoutException</code> object
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
        public SQLTimeoutException(String reason, String SQLState) {
                super(reason, SQLState);
        }

        /**
         * Constructs a <code>SQLTimeoutException</code> object
         * with a given <code>reason</code>, <code>SQLState</code>  and
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
        public SQLTimeoutException(String reason, String SQLState, int vendorCode) {
                super(reason, SQLState, vendorCode);
        }

    /**
     * Constructs a <code>SQLTimeoutException</code> object
     * with a given  <code>cause</code>.
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
    public SQLTimeoutException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a <code>SQLTimeoutException</code> object
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
    public SQLTimeoutException(String reason, Throwable cause) {
        super(reason, cause);
    }

    /**
     * Constructs a <code>SQLTimeoutException</code> object
     *  with a given
     * <code>reason</code>, <code>SQLState</code> and  <code>cause</code>.
     * The vendor code is initialized to 0.
     * <p>
     * @param reason a description of the exception.
     * @param SQLState an XOPEN or SQL:2003 code identifying the exception
     * @param cause the underlying reason for this <code>SQLException</code> (which is saved for later retrieval by the <code>getCause()</code> method); may be null indicating
     *     the cause is non-existent or unknown.
     * @since 1.6
     */
    public SQLTimeoutException(String reason, String SQLState, Throwable cause) {
        super(reason, SQLState, cause);
    }

    /**
     *  Constructs a <code>SQLTimeoutException</code> object
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
    public SQLTimeoutException(String reason, String SQLState, int vendorCode, Throwable cause) {
        super(reason, SQLState, vendorCode, cause);
    }

    private static final long serialVersionUID = -4487171280562520262L;
}
