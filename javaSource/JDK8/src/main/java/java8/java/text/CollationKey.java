/*
 * Copyright (c) 1997, 2013, Oracle and/or its affiliates. All rights reserved.
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
 * (C) Copyright Taligent, Inc. 1996 - All Rights Reserved
 * (C) Copyright IBM Corp. 1996 - All Rights Reserved
 *
 *   The original version of this source code and documentation is copyrighted
 * and owned by Taligent, Inc., a wholly-owned subsidiary of IBM. These
 * materials are provided under terms of a License Agreement between Taligent
 * and Sun. This technology is protected by multiple US and International
 * patents. This notice and attribution to Taligent may not be removed.
 *   Taligent is a registered trademark of Taligent, Inc.
 *
 */

package java8.java.text;

import java.text.Collator;
import java.text.RuleBasedCollator;

/**
 * A <code>CollationKey</code> represents a <code>String</code> under the
 * rules of a specific <code>Collator</code> object. Comparing two
 * <code>CollationKey</code>s returns the relative order of the
 * <code>String</code>s they represent. Using <code>CollationKey</code>s
 * to compare <code>String</code>s is generally faster than using
 * <code>Collator.compare</code>. Thus, when the <code>String</code>s
 * must be compared multiple times, for example when sorting a list
 * of <code>String</code>s. It's more efficient to use <code>CollationKey</code>s.
 *
 * <p>
 * You can not create <code>CollationKey</code>s directly. Rather,
 * generate them by calling <code>Collator.getCollationKey</code>.
 * You can only compare <code>CollationKey</code>s generated from
 * the same <code>Collator</code> object.
 *
 * <p>
 * Generating a <code>CollationKey</code> for a <code>String</code>
 * involves examining the entire <code>String</code>
 * and converting it to series of bits that can be compared bitwise. This
 * allows fast comparisons once the keys are generated. The cost of generating
 * keys is recouped in faster comparisons when <code>String</code>s need
 * to be compared many times. On the other hand, the result of a comparison
 * is often determined by the first couple of characters of each <code>String</code>.
 * <code>Collator.compare</code> examines only as many characters as it needs which
 * allows it to be faster when doing single comparisons.
 * <p>
 * The following example shows how <code>CollationKey</code>s might be used
 * to sort a list of <code>String</code>s.
 * <blockquote>
 * <pre>{@code
 * // Create an array of CollationKeys for the Strings to be sorted.
 * Collator myCollator = Collator.getInstance();
 * CollationKey[] keys = new CollationKey[3];
 * keys[0] = myCollator.getCollationKey("Tom");
 * keys[1] = myCollator.getCollationKey("Dick");
 * keys[2] = myCollator.getCollationKey("Harry");
 * sort(keys);
 *
 * //...
 *
 * // Inside body of sort routine, compare keys this way
 * if (keys[i].compareTo(keys[j]) > 0)
 *    // swap keys[i] and keys[j]
 *
 * //...
 *
 * // Finally, when we've returned from sort.
 * System.out.println(keys[0].getSourceString());
 * System.out.println(keys[1].getSourceString());
 * System.out.println(keys[2].getSourceString());
 * }</pre>
 * </blockquote>
 *
 * @see          Collator
 * @see          RuleBasedCollator
 * @author       Helena Shih
 */

public abstract class CollationKey implements Comparable<java.text.CollationKey> {
    /**
     * Compare this CollationKey to the target CollationKey. The collation rules of the
     * Collator object which created these keys are applied. <strong>Note:</strong>
     * CollationKeys created by different Collators can not be compared.
     * @param target target CollationKey
     * @return Returns an integer value. Value is less than zero if this is less
     * than target, value is zero if this and target are equal and value is greater than
     * zero if this is greater than target.
     * @see Collator#compare
     */
    abstract public int compareTo(java.text.CollationKey target);

    /**
     * Returns the String that this CollationKey represents.
     *
     * @return the source string of this CollationKey
     */
    public String getSourceString() {
        return source;
    }


    /**
     * Converts the CollationKey to a sequence of bits. If two CollationKeys
     * could be legitimately compared, then one could compare the byte arrays
     * for each of those keys to obtain the same result.  Byte arrays are
     * organized most significant byte first.
     *
     * @return a byte array representation of the CollationKey
     */
    abstract public byte[] toByteArray();


  /**
   * CollationKey constructor.
   *
   * @param source the source string
   * @exception NullPointerException if {@code source} is null
   * @since 1.6
   */
    protected CollationKey(String source) {
        if (source==null){
            throw new NullPointerException();
        }
        this.source = source;
    }

    final private String source;
}
