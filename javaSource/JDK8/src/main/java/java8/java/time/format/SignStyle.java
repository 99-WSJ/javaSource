/*
 * Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
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

/*
 *
 *
 *
 *
 *
 * Copyright (c) 2008-2012, Stephen Colebourne & Michael Nascimento Santos
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of JSR-310 nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package java8.java.time.format;

import java.time.format.DateTimeFormatterBuilder;

/**
 * Enumeration of ways to handle the positive/negative sign.
 * <p>
 * The formatting engine allows the positive and negative signs of numbers
 * to be controlled using this enum.
 * See {@link DateTimeFormatterBuilder} for usage.
 *
 * @implSpec
 * This is an immutable and thread-safe enum.
 *
 * @since 1.8
 */
public enum SignStyle {

    /**
     * Style to output the sign only if the value is negative.
     * <p>
     * In strict parsing, the negative sign will be accepted and the positive sign rejected.
     * In lenient parsing, any sign will be accepted.
     */
    NORMAL,
    /**
     * Style to always output the sign, where zero will output '+'.
     * <p>
     * In strict parsing, the absence of a sign will be rejected.
     * In lenient parsing, any sign will be accepted, with the absence
     * of a sign treated as a positive number.
     */
    ALWAYS,
    /**
     * Style to never output sign, only outputting the absolute value.
     * <p>
     * In strict parsing, any sign will be rejected.
     * In lenient parsing, any sign will be accepted unless the width is fixed.
     */
    NEVER,
    /**
     * Style to block negative values, throwing an exception on printing.
     * <p>
     * In strict parsing, any sign will be rejected.
     * In lenient parsing, any sign will be accepted unless the width is fixed.
     */
    NOT_NEGATIVE,
    /**
     * Style to always output the sign if the value exceeds the pad width.
     * A negative value will always output the '-' sign.
     * <p>
     * In strict parsing, the sign will be rejected unless the pad width is exceeded.
     * In lenient parsing, any sign will be accepted, with the absence
     * of a sign treated as a positive number.
     */
    EXCEEDS_PAD;

    /**
     * Parse helper.
     *
     * @param positive  true if positive sign parsed, false for negative sign
     * @param strict  true if strict, false if lenient
     * @param fixedWidth  true if fixed width, false if not
     * @return
     */
    boolean parse(boolean positive, boolean strict, boolean fixedWidth) {
        switch (ordinal()) {
            case 0: // NORMAL
                // valid if negative or (positive and lenient)
                return !positive || !strict;
            case 1: // ALWAYS
            case 4: // EXCEEDS_PAD
                return true;
            default:
                // valid if lenient and not fixed width
                return !strict && !fixedWidth;
        }
    }

}
