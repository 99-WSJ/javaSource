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

import java.sql.SQLWarning;

/**
 * An exception  thrown as a <code>DataTruncation</code> exception
 * (on writes) or reported as a
 * <code>DataTruncation</code> warning (on reads)
 *  when a data values is unexpectedly truncated for reasons other than its having
 *  exceeded <code>MaxFieldSize</code>.
 *
 * <P>The SQLstate for a <code>DataTruncation</code> during read is <code>01004</code>.
 * <P>The SQLstate for a <code>DataTruncation</code> during write is <code>22001</code>.
 */

public class DataTruncation extends SQLWarning {

    /**
     * Creates a <code>DataTruncation</code> object
     * with the SQLState initialized
     * to 01004 when <code>read</code> is set to <code>true</code> and 22001
     * when <code>read</code> is set to <code>false</code>,
     * the reason set to "Data truncation", the
     * vendor code set to 0, and
     * the other fields set to the given values.
     * The <code>cause</code> is not initialized, and may subsequently be
     * initialized by a call to the
     * {@link Throwable#initCause(Throwable)} method.
     * <p>
     *
     * @param index The index of the parameter or column value
     * @param parameter true if a parameter value was truncated
     * @param read true if a read was truncated
     * @param dataSize the original size of the data
     * @param transferSize the size after truncation
     */
    public DataTruncation(int index, boolean parameter,
                          boolean read, int dataSize,
                          int transferSize) {
        super("Data truncation", read == true?"01004":"22001");
        this.index = index;
        this.parameter = parameter;
        this.read = read;
        this.dataSize = dataSize;
        this.transferSize = transferSize;

    }

    /**
     * Creates a <code>DataTruncation</code> object
     * with the SQLState initialized
     * to 01004 when <code>read</code> is set to <code>true</code> and 22001
     * when <code>read</code> is set to <code>false</code>,
     * the reason set to "Data truncation", the
     * vendor code set to 0, and
     * the other fields set to the given values.
     * <p>
     *
     * @param index The index of the parameter or column value
     * @param parameter true if a parameter value was truncated
     * @param read true if a read was truncated
     * @param dataSize the original size of the data
     * @param transferSize the size after truncation
     * @param cause the underlying reason for this <code>DataTruncation</code>
     * (which is saved for later retrieval by the <code>getCause()</code> method);
     * may be null indicating the cause is non-existent or unknown.
     *
     * @since 1.6
     */
    public DataTruncation(int index, boolean parameter,
                          boolean read, int dataSize,
                          int transferSize, Throwable cause) {
        super("Data truncation", read == true?"01004":"22001",cause);
        this.index = index;
        this.parameter = parameter;
        this.read = read;
        this.dataSize = dataSize;
        this.transferSize = transferSize;
    }

    /**
     * Retrieves the index of the column or parameter that was truncated.
     *
     * <P>This may be -1 if the column or parameter index is unknown, in
     * which case the <code>parameter</code> and <code>read</code> fields should be ignored.
     *
     * @return the index of the truncated parameter or column value
     */
    public int getIndex() {
        return index;
    }

    /**
     * Indicates whether the value truncated was a parameter value or
         * a column value.
     *
     * @return <code>true</code> if the value truncated was a parameter;
         *         <code>false</code> if it was a column value
     */
    public boolean getParameter() {
        return parameter;
    }

    /**
     * Indicates whether or not the value was truncated on a read.
     *
     * @return <code>true</code> if the value was truncated when read from
         *         the database; <code>false</code> if the data was truncated on a write
     */
    public boolean getRead() {
        return read;
    }

    /**
     * Gets the number of bytes of data that should have been transferred.
     * This number may be approximate if data conversions were being
     * performed.  The value may be <code>-1</code> if the size is unknown.
     *
     * @return the number of bytes of data that should have been transferred
     */
    public int getDataSize() {
        return dataSize;
    }

    /**
     * Gets the number of bytes of data actually transferred.
     * The value may be <code>-1</code> if the size is unknown.
     *
     * @return the number of bytes of data actually transferred
     */
    public int getTransferSize() {
        return transferSize;
    }

        /**
        * @serial
        */
    private int index;

        /**
        * @serial
        */
    private boolean parameter;

        /**
        * @serial
        */
    private boolean read;

        /**
        * @serial
        */
    private int dataSize;

        /**
        * @serial
        */
    private int transferSize;

    /**
     * @serial
     */
    private static final long serialVersionUID = 6464298989504059473L;

}
