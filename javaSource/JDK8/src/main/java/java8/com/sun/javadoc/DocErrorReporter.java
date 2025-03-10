/*
 * Copyright (c) 1998, 2006, Oracle and/or its affiliates. All rights reserved.
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

package java8.sun.javadoc;

/**
 * This interface provides error, warning and notice printing.
 *
 * @since 1.2
 * @author Robert Field
 */
public interface DocErrorReporter {

    /**
     * Print error message and increment error count.
     *
     * @param msg message to print
     */
    void printError(String msg);

    /**
     * Print an error message and increment error count.
     *
     * @param pos the position item where the error occurs
     * @param msg message to print
     * @since 1.4
     */
    void printError(SourcePosition pos, String msg);

    /**
     * Print warning message and increment warning count.
     *
     * @param msg message to print
     */
    void printWarning(String msg);

    /**
     * Print warning message and increment warning count.
     *
     * @param pos the position item where the warning occurs
     * @param msg message to print
     * @since 1.4
     */
    void printWarning(SourcePosition pos, String msg);

    /**
     * Print a message.
     *
     * @param msg message to print
     */
    void printNotice(String msg);

    /**
     * Print a message.
     *
     * @param pos the position item where the message occurs
     * @param msg message to print
     * @since 1.4
     */
    void printNotice(SourcePosition pos, String msg);
}
