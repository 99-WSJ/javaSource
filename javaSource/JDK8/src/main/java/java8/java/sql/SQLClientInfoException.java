/*
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
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

import java.sql.ClientInfoStatus;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * The subclass of {@link SQLException} is thrown when one or more client info properties
 * could not be set on a <code>Connection</code>.  In addition to the information provided
 * by <code>SQLException</code>, a <code>SQLClientInfoException</code> provides a list of client info
 * properties that were not set.
 *
 * Some databases do not allow multiple client info properties to be set
 * atomically.  For those databases, it is possible that some of the client
 * info properties had been set even though the <code>Connection.setClientInfo</code>
 * method threw an exception.  An application can use the <code>getFailedProperties </code>
 * method to retrieve a list of client info properties that were not set.  The
 * properties are identified by passing a
 * <code>Map&lt;String,ClientInfoStatus&gt;</code> to
 * the appropriate <code>SQLClientInfoException</code> constructor.
 * <p>
 * @see ClientInfoStatus
 * @see Connection#setClientInfo
 * @since 1.6
 */
public class SQLClientInfoException extends SQLException {




        private Map<String, ClientInfoStatus>   failedProperties;

        /**
     * Constructs a <code>SQLClientInfoException</code>  Object.
     * The <code>reason</code>,
     * <code>SQLState</code>, and failedProperties list are initialized to
     * <code> null</code> and the vendor code is initialized to 0.
     * The <code>cause</code> is not initialized, and may subsequently be
     * initialized by a call to the
     * {@link Throwable#initCause(Throwable)} method.
     * <p>
     *
     * @since 1.6
     */
        public SQLClientInfoException() {

                this.failedProperties = null;
        }

        /**
     * Constructs a <code>SQLClientInfoException</code> object initialized with a
     * given <code>failedProperties</code>.
     * The <code>reason</code> and <code>SQLState</code> are initialized
     * to <code>null</code> and the vendor code is initialized to 0.
     *
     * The <code>cause</code> is not initialized, and may subsequently be
     * initialized by a call to the
     * {@link Throwable#initCause(Throwable)} method.
     * <p>
     *
     * @param failedProperties          A Map containing the property values that could not
     *                                  be set.  The keys in the Map
     *                                  contain the names of the client info
     *                                  properties that could not be set and
     *                                  the values contain one of the reason codes
     *                                  defined in <code>ClientInfoStatus</code>
     * <p>
     * @since 1.6
     */
        public SQLClientInfoException(Map<String, ClientInfoStatus> failedProperties) {

                this.failedProperties = failedProperties;
        }

        /**
     * Constructs a <code>SQLClientInfoException</code> object initialized with
     * a given <code>cause</code> and <code>failedProperties</code>.
     *
     * The <code>reason</code>  is initialized to <code>null</code> if
     * <code>cause==null</code> or to <code>cause.toString()</code> if
     * <code>cause!=null</code> and the vendor code is initialized to 0.
     *
     * <p>
     *
     * @param failedProperties          A Map containing the property values that could not
     *                                  be set.  The keys in the Map
     *                                  contain the names of the client info
     *                                  properties that could not be set and
     *                                  the values contain one of the reason codes
     *                                  defined in <code>ClientInfoStatus</code>
     * @param cause                                     the (which is saved for later retrieval by the <code>getCause()</code> method); may be null indicating
     *     the cause is non-existent or unknown.
     * <p>
     * @since 1.6
     */
        public SQLClientInfoException(Map<String, ClientInfoStatus> failedProperties,
                                                           Throwable cause) {

                super(cause != null?cause.toString():null);
                initCause(cause);
                this.failedProperties = failedProperties;
        }

        /**
     * Constructs a <code>SQLClientInfoException</code> object initialized with a
     * given <code>reason</code> and <code>failedProperties</code>.
     * The <code>SQLState</code> is initialized
     * to <code>null</code> and the vendor code is initialized to 0.
     *
     * The <code>cause</code> is not initialized, and may subsequently be
     * initialized by a call to the
     * {@link Throwable#initCause(Throwable)} method.
     * <p>
     *
     * @param reason                            a description of the exception
     * @param failedProperties          A Map containing the property values that could not
     *                                  be set.  The keys in the Map
     *                                  contain the names of the client info
     *                                  properties that could not be set and
     *                                  the values contain one of the reason codes
     *                                  defined in <code>ClientInfoStatus</code>
     * <p>
     * @since 1.6
     */
        public SQLClientInfoException(String reason,
                Map<String, ClientInfoStatus> failedProperties) {

                super(reason);
                this.failedProperties = failedProperties;
        }

        /**
     * Constructs a <code>SQLClientInfoException</code> object initialized with a
     * given <code>reason</code>, <code>cause</code> and
     * <code>failedProperties</code>.
     * The  <code>SQLState</code> is initialized
     * to <code>null</code> and the vendor code is initialized to 0.
     * <p>
     *
     * @param reason                            a description of the exception
     * @param failedProperties          A Map containing the property values that could not
     *                                  be set.  The keys in the Map
     *                                  contain the names of the client info
     *                                  properties that could not be set and
     *                                  the values contain one of the reason codes
     *                                  defined in <code>ClientInfoStatus</code>
     * @param cause                                     the underlying reason for this <code>SQLException</code> (which is saved for later retrieval by the <code>getCause()</code> method); may be null indicating
     *     the cause is non-existent or unknown.
     * <p>
     * @since 1.6
     */
        public SQLClientInfoException(String reason,
                                                           Map<String, ClientInfoStatus> failedProperties,
                                                           Throwable cause) {

                super(reason);
                initCause(cause);
                this.failedProperties = failedProperties;
        }

        /**
     * Constructs a <code>SQLClientInfoException</code> object initialized with a
     * given  <code>reason</code>, <code>SQLState</code>  and
     * <code>failedProperties</code>.
     * The <code>cause</code> is not initialized, and may subsequently be
     * initialized by a call to the
     * {@link Throwable#initCause(Throwable)} method. The vendor code
     * is initialized to 0.
     * <p>
     *
     * @param reason                            a description of the exception
     * @param SQLState                          an XOPEN or SQL:2003 code identifying the exception
     * @param failedProperties          A Map containing the property values that could not
     *                                  be set.  The keys in the Map
     *                                  contain the names of the client info
     *                                  properties that could not be set and
     *                                  the values contain one of the reason codes
     *                                  defined in <code>ClientInfoStatus</code>
     * <p>
     * @since 1.6
     */
        public SQLClientInfoException(String reason,
                                                           String SQLState,
                                                           Map<String, ClientInfoStatus> failedProperties) {

                super(reason, SQLState);
                this.failedProperties = failedProperties;
        }

        /**
     * Constructs a <code>SQLClientInfoException</code> object initialized with a
     * given  <code>reason</code>, <code>SQLState</code>, <code>cause</code>
     * and <code>failedProperties</code>.  The vendor code is initialized to 0.
     * <p>
     *
     * @param reason                            a description of the exception
     * @param SQLState                          an XOPEN or SQL:2003 code identifying the exception
     * @param failedProperties          A Map containing the property values that could not
     *                                  be set.  The keys in the Map
     *                                  contain the names of the client info
     *                                  properties that could not be set and
     *                                  the values contain one of the reason codes
     *                                  defined in <code>ClientInfoStatus</code>
     * @param cause                                     the underlying reason for this <code>SQLException</code> (which is saved for later retrieval by the <code>getCause()</code> method); may be null indicating
     *     the cause is non-existent or unknown.
     * <p>
     * @since 1.6
     */
        public SQLClientInfoException(String reason,
                                                           String SQLState,
                                                           Map<String, ClientInfoStatus> failedProperties,
                                                           Throwable cause) {

                super(reason, SQLState);
                initCause(cause);
                this.failedProperties = failedProperties;
        }

        /**
     * Constructs a <code>SQLClientInfoException</code> object initialized with a
     * given  <code>reason</code>, <code>SQLState</code>,
     * <code>vendorCode</code>  and <code>failedProperties</code>.
     * The <code>cause</code> is not initialized, and may subsequently be
     * initialized by a call to the
     * {@link Throwable#initCause(Throwable)} method.
     * <p>
     *
     * @param reason                            a description of the exception
     * @param SQLState                          an XOPEN or SQL:2003 code identifying the exception
     * @param vendorCode                        a database vendor-specific exception code
     * @param failedProperties          A Map containing the property values that could not
     *                                  be set.  The keys in the Map
     *                                  contain the names of the client info
     *                                  properties that could not be set and
     *                                  the values contain one of the reason codes
     *                                  defined in <code>ClientInfoStatus</code>
     * <p>
     * @since 1.6
     */
        public SQLClientInfoException(String reason,
                                                           String SQLState,
                                                           int vendorCode,
                                                           Map<String, ClientInfoStatus> failedProperties) {

                super(reason, SQLState, vendorCode);
                this.failedProperties = failedProperties;
        }

        /**
     * Constructs a <code>SQLClientInfoException</code> object initialized with a
     * given  <code>reason</code>, <code>SQLState</code>,
     * <code>cause</code>, <code>vendorCode</code> and
     * <code>failedProperties</code>.
     * <p>
     *
     * @param reason                            a description of the exception
     * @param SQLState                          an XOPEN or SQL:2003 code identifying the exception
     * @param vendorCode                        a database vendor-specific exception code
     * @param failedProperties          A Map containing the property values that could not
     *                                  be set.  The keys in the Map
     *                                  contain the names of the client info
     *                                  properties that could not be set and
     *                                  the values contain one of the reason codes
     *                                  defined in <code>ClientInfoStatus</code>
     * @param cause                     the underlying reason for this <code>SQLException</code> (which is saved for later retrieval by the <code>getCause()</code> method); may be null indicating
     *     the cause is non-existent or unknown.
     * <p>
     * @since 1.6
     */
        public SQLClientInfoException(String reason,
                                                           String SQLState,
                                                           int vendorCode,
                                                           Map<String, ClientInfoStatus> failedProperties,
                                                           Throwable cause) {

                super(reason, SQLState, vendorCode);
                initCause(cause);
                this.failedProperties = failedProperties;
        }

    /**
     * Returns the list of client info properties that could not be set.  The
     * keys in the Map  contain the names of the client info
     * properties that could not be set and the values contain one of the
     * reason codes defined in <code>ClientInfoStatus</code>
     * <p>
     *
     * @return Map list containing the client info properties that could
     * not be set
     * <p>
     * @since 1.6
     */
        public Map<String, ClientInfoStatus> getFailedProperties() {

                return this.failedProperties;
        }

    private static final long serialVersionUID = -4319604256824655880L;
}
