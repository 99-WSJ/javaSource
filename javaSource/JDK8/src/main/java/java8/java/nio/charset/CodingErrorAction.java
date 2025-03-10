/*
 * Copyright (c) 2001, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java8.java.nio.charset;


import java.nio.charset.CharacterCodingException;
import java.nio.charset.CoderResult;

/**
 * A typesafe enumeration for coding-error actions.
 *
 * <p> Instances of this class are used to specify how malformed-input and
 * unmappable-character errors are to be handled by charset <a
 * href="CharsetDecoder.html#cae">decoders</a> and <a
 * href="CharsetEncoder.html#cae">encoders</a>.  </p>
 *
 *
 * @author Mark Reinhold
 * @author JSR-51 Expert Group
 * @since 1.4
 */

public class CodingErrorAction {

    private String name;

    private CodingErrorAction(String name) {
        this.name = name;
    }

    /**
     * Action indicating that a coding error is to be handled by dropping the
     * erroneous input and resuming the coding operation.
     */
    public static final java.nio.charset.CodingErrorAction IGNORE
        = new java.nio.charset.CodingErrorAction("IGNORE");

    /**
     * Action indicating that a coding error is to be handled by dropping the
     * erroneous input, appending the coder's replacement value to the output
     * buffer, and resuming the coding operation.
     */
    public static final java.nio.charset.CodingErrorAction REPLACE
        = new java.nio.charset.CodingErrorAction("REPLACE");

    /**
     * Action indicating that a coding error is to be reported, either by
     * returning a {@link CoderResult} object or by throwing a {@link
     * CharacterCodingException}, whichever is appropriate for the method
     * implementing the coding process.
     */
    public static final java.nio.charset.CodingErrorAction REPORT
        = new java.nio.charset.CodingErrorAction("REPORT");

    /**
     * Returns a string describing this action.
     *
     * @return  A descriptive string
     */
    public String toString() {
        return name;
    }

}
