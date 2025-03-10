/*
 * Copyright (c) 2003, Oracle and/or its affiliates. All rights reserved.
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

package java8.java.util;

import java.util.IllegalFormatException;

/**
 * Unchecked exception thrown when a conversion and flag are incompatible.
 *
 * <p> Unless otherwise specified, passing a <tt>null</tt> argument to any
 * method or constructor in this class will cause a {@link
 * NullPointerException} to be thrown.
 *
 * @since 1.5
 */
public class FormatFlagsConversionMismatchException
    extends IllegalFormatException
{
    private static final long serialVersionUID = 19120414L;

    private String f;

    private char c;

    /**
     * Constructs an instance of this class with the specified flag
     * and conversion.
     *
     * @param  f
     *         The flag
     *
     * @param  c
     *         The conversion
     */
    public FormatFlagsConversionMismatchException(String f, char c) {
        if (f == null)
            throw new NullPointerException();
        this.f = f;
        this.c = c;
    }

    /**
     * Returns the incompatible flag.
     *
     * @return  The flag
     */
     public String getFlags() {
        return f;
    }

    /**
     * Returns the incompatible conversion.
     *
     * @return  The conversion
     */
    public char getConversion() {
        return c;
    }

    public String getMessage() {
        return "Conversion = " + c + ", Flags = " + f;
    }
}
