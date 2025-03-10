/*
 * Copyright (c) 2007, 2009, Oracle and/or its affiliates. All rights reserved.
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

package java8.java.nio.file;

import java.nio.file.Path;

/**
 * Unchecked exception thrown when path string cannot be converted into a
 * {@link Path} because the path string contains invalid characters, or
 * the path string is invalid for other file system specific reasons.
 */

public class InvalidPathException
    extends IllegalArgumentException
{
    static final long serialVersionUID = 4355821422286746137L;

    private String input;
    private int index;

    /**
     * Constructs an instance from the given input string, reason, and error
     * index.
     *
     * @param  input   the input string
     * @param  reason  a string explaining why the input was rejected
     * @param  index   the index at which the error occurred,
     *                 or <tt>-1</tt> if the index is not known
     *
     * @throws  NullPointerException
     *          if either the input or reason strings are <tt>null</tt>
     *
     * @throws  IllegalArgumentException
     *          if the error index is less than <tt>-1</tt>
     */
    public InvalidPathException(String input, String reason, int index) {
        super(reason);
        if ((input == null) || (reason == null))
            throw new NullPointerException();
        if (index < -1)
            throw new IllegalArgumentException();
        this.input = input;
        this.index = index;
    }

    /**
     * Constructs an instance from the given input string and reason.  The
     * resulting object will have an error index of <tt>-1</tt>.
     *
     * @param  input   the input string
     * @param  reason  a string explaining why the input was rejected
     *
     * @throws  NullPointerException
     *          if either the input or reason strings are <tt>null</tt>
     */
    public InvalidPathException(String input, String reason) {
        this(input, reason, -1);
    }

    /**
     * Returns the input string.
     *
     * @return  the input string
     */
    public String getInput() {
        return input;
    }

    /**
     * Returns a string explaining why the input string was rejected.
     *
     * @return  the reason string
     */
    public String getReason() {
        return super.getMessage();
    }

    /**
     * Returns an index into the input string of the position at which the
     * error occurred, or <tt>-1</tt> if this position is not known.
     *
     * @return  the error index
     */
    public int getIndex() {
        return index;
    }

    /**
     * Returns a string describing the error.  The resulting string
     * consists of the reason string followed by a colon character
     * (<tt>':'</tt>), a space, and the input string.  If the error index is
     * defined then the string <tt>" at index "</tt> followed by the index, in
     * decimal, is inserted after the reason string and before the colon
     * character.
     *
     * @return  a string describing the error
     */
    public String getMessage() {
        StringBuffer sb = new StringBuffer();
        sb.append(getReason());
        if (index > -1) {
            sb.append(" at index ");
            sb.append(index);
        }
        sb.append(": ");
        sb.append(input);
        return sb.toString();
    }
}
