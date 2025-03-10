/*
 * Copyright (c) 2000, 2004, Oracle and/or its affiliates. All rights reserved.
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


package java8.javax.print.attribute;

import javax.print.attribute.IntegerSyntax;
import java.io.Serializable;
import java.util.Vector;

/**
 * Class SetOfIntegerSyntax is an abstract base class providing the common
 * implementation of all attributes whose value is a set of nonnegative
 * integers. This includes attributes whose value is a single range of integers
 * and attributes whose value is a set of ranges of integers.
 * <P>
 * You can construct an instance of SetOfIntegerSyntax by giving it in "string
 * form." The string consists of zero or more comma-separated integer groups.
 * Each integer group consists of either one integer, two integers separated by
 * a hyphen (<CODE>-</CODE>), or two integers separated by a colon
 * (<CODE>:</CODE>). Each integer consists of one or more decimal digits
 * (<CODE>0</CODE> through <CODE>9</CODE>). Whitespace characters cannot
 * appear within an integer but are otherwise ignored. For example:
 * <CODE>""</CODE>, <CODE>"1"</CODE>, <CODE>"5-10"</CODE>, <CODE>"1:2,
 * 4"</CODE>.
 * <P>
 * You can also construct an instance of SetOfIntegerSyntax by giving it in
 * "array form." Array form consists of an array of zero or more integer groups
 * where each integer group is a length-1 or length-2 array of
 * <CODE>int</CODE>s; for example, <CODE>int[0][]</CODE>,
 * <CODE>int[][]{{1}}</CODE>, <CODE>int[][]{{5,10}}</CODE>,
 * <CODE>int[][]{{1,2},{4}}</CODE>.
 * <P>
 * In both string form and array form, each successive integer group gives a
 * range of integers to be included in the set. The first integer in each group
 * gives the lower bound of the range; the second integer in each group gives
 * the upper bound of the range; if there is only one integer in the group, the
 * upper bound is the same as the lower bound. If the upper bound is less than
 * the lower bound, it denotes a null range (no values). If the upper bound is
 * equal to the lower bound, it denotes a range consisting of a single value. If
 * the upper bound is greater than the lower bound, it denotes a range
 * consisting of more than one value. The ranges may appear in any order and are
 * allowed to overlap. The union of all the ranges gives the set's contents.
 * Once a SetOfIntegerSyntax instance is constructed, its value is immutable.
 * <P>
 * The SetOfIntegerSyntax object's value is actually stored in "<I>canonical</I>
 * array form." This is the same as array form, except there are no null ranges;
 * the members of the set are represented in as few ranges as possible (i.e.,
 * overlapping ranges are coalesced); the ranges appear in ascending order; and
 * each range is always represented as a length-two array of <CODE>int</CODE>s
 * in the form {lower bound, upper bound}. An empty set is represented as a
 * zero-length array.
 * <P>
 * Class SetOfIntegerSyntax has operations to return the set's members in
 * canonical array form, to test whether a given integer is a member of the
 * set, and to iterate through the members of the set.
 * <P>
 *
 * @author  David Mendenhall
 * @author  Alan Kaminsky
 */
public abstract class SetOfIntegerSyntax implements Serializable, Cloneable {

    private static final long serialVersionUID = 3666874174847632203L;

    /**
     * This set's members in canonical array form.
     * @serial
     */
    private int[][] members;


    /**
     * Construct a new set-of-integer attribute with the given members in
     * string form.
     *
     * @param  members  Set members in string form. If null, an empty set is
     *                     constructed.
     *
     * @exception  IllegalArgumentException
     *     (Unchecked exception) Thrown if <CODE>members</CODE> does not
     *    obey  the proper syntax.
     */
    protected SetOfIntegerSyntax(String members) {
        this.members = parse (members);
    }

    /**
     * Parse the given string, returning canonical array form.
     */
    private static int[][] parse(String members) {
        // Create vector to hold int[] elements, each element being one range
        // parsed out of members.
        Vector theRanges = new Vector();

        // Run state machine over members.
        int n = (members == null ? 0 : members.length());
        int i = 0;
        int state = 0;
        int lb = 0;
        int ub = 0;
        char c;
        int digit;
        while (i < n) {
            c = members.charAt(i ++);
            switch (state) {

            case 0: // Before first integer in first group
                if (Character.isWhitespace(c)) {
                    state = 0;
                }
                else if ((digit = Character.digit(c, 10)) != -1) {
                    lb = digit;
                    state = 1;
                } else {
                    throw new IllegalArgumentException();
                }
                break;

            case 1: // In first integer in a group
                if (Character.isWhitespace(c)){
                        state = 2;
                } else if ((digit = Character.digit(c, 10)) != -1) {
                    lb = 10 * lb + digit;
                    state = 1;
                } else if (c == '-' || c == ':') {
                    state = 3;
                } else if (c == ',') {
                    accumulate (theRanges, lb, lb);
                    state = 6;
                } else {
                    throw new IllegalArgumentException();
                }
                break;

            case 2: // After first integer in a group
                if (Character.isWhitespace(c)) {
                    state = 2;
                }
                else if (c == '-' || c == ':') {
                    state = 3;
                }
                else if (c == ',') {
                    accumulate(theRanges, lb, lb);
                    state = 6;
                } else {
                    throw new IllegalArgumentException();
                }
                break;

            case 3: // Before second integer in a group
                if (Character.isWhitespace(c)) {
                    state = 3;
                } else if ((digit = Character.digit(c, 10)) != -1) {
                    ub = digit;
                    state = 4;
                } else {
                    throw new IllegalArgumentException();
                }
                break;

            case 4: // In second integer in a group
                if (Character.isWhitespace(c)) {
                    state = 5;
                } else if ((digit = Character.digit(c, 10)) != -1) {
                    ub = 10 * ub + digit;
                    state = 4;
                } else if (c == ',') {
                    accumulate(theRanges, lb, ub);
                    state = 6;
                } else {
                    throw new IllegalArgumentException();
                }
                break;

            case 5: // After second integer in a group
                if (Character.isWhitespace(c)) {
                    state = 5;
                } else if (c == ',') {
                    accumulate(theRanges, lb, ub);
                    state = 6;
                } else {
                    throw new IllegalArgumentException();
                }
                break;

            case 6: // Before first integer in second or later group
                if (Character.isWhitespace(c)) {
                    state = 6;
                } else if ((digit = Character.digit(c, 10)) != -1) {
                    lb = digit;
                    state = 1;
                } else {
                    throw new IllegalArgumentException();
                }
                break;
            }
        }

        // Finish off the state machine.
        switch (state) {
        case 0: // Before first integer in first group
            break;
        case 1: // In first integer in a group
        case 2: // After first integer in a group
            accumulate(theRanges, lb, lb);
            break;
        case 4: // In second integer in a group
        case 5: // After second integer in a group
            accumulate(theRanges, lb, ub);
            break;
        case 3: // Before second integer in a group
        case 6: // Before first integer in second or later group
            throw new IllegalArgumentException();
        }

        // Return canonical array form.
        return canonicalArrayForm (theRanges);
    }

    /**
     * Accumulate the given range (lb .. ub) into the canonical array form
     * into the given vector of int[] objects.
     */
    private static void accumulate(Vector ranges, int lb,int ub) {
        // Make sure range is non-null.
        if (lb <= ub) {
            // Stick range at the back of the vector.
            ranges.add(new int[] {lb, ub});

            // Work towards the front of the vector to integrate the new range
            // with the existing ranges.
            for (int j = ranges.size()-2; j >= 0; -- j) {
            // Get lower and upper bounds of the two ranges being compared.
                int[] rangea = (int[]) ranges.elementAt (j);
                int lba = rangea[0];
                int uba = rangea[1];
                int[] rangeb = (int[]) ranges.elementAt (j+1);
                int lbb = rangeb[0];
                int ubb = rangeb[1];

                /* If the two ranges overlap or are adjacent, coalesce them.
                 * The two ranges overlap if the larger lower bound is less
                 * than or equal to the smaller upper bound. The two ranges
                 * are adjacent if the larger lower bound is one greater
                 * than the smaller upper bound.
                 */
                if (Math.max(lba, lbb) - Math.min(uba, ubb) <= 1) {
                    // The coalesced range is from the smaller lower bound to
                    // the larger upper bound.
                    ranges.setElementAt(new int[]
                                           {Math.min(lba, lbb),
                                                Math.max(uba, ubb)}, j);
                    ranges.remove (j+1);
                } else if (lba > lbb) {

                    /* If the two ranges don't overlap and aren't adjacent but
                     * are out of order, swap them.
                     */
                    ranges.setElementAt (rangeb, j);
                    ranges.setElementAt (rangea, j+1);
                } else {
                /* If the two ranges don't overlap and aren't adjacent and
                 * aren't out of order, we're done early.
                 */
                    break;
                }
            }
        }
    }

    /**
     * Convert the given vector of int[] objects to canonical array form.
     */
    private static int[][] canonicalArrayForm(Vector ranges) {
        return (int[][]) ranges.toArray (new int[ranges.size()][]);
    }

    /**
     * Construct a new set-of-integer attribute with the given members in
     * array form.
     *
     * @param  members  Set members in array form. If null, an empty set is
     *                     constructed.
     *
     * @exception  NullPointerException
     *     (Unchecked exception) Thrown if any element of
     *     <CODE>members</CODE> is null.
     * @exception  IllegalArgumentException
     *     (Unchecked exception) Thrown if any element of
     *     <CODE>members</CODE> is not a length-one or length-two array or if
     *     any non-null range in <CODE>members</CODE> has a lower bound less
     *     than zero.
     */
    protected SetOfIntegerSyntax(int[][] members) {
        this.members = parse (members);
    }

    /**
     * Parse the given array form, returning canonical array form.
     */
    private static int[][] parse(int[][] members) {
        // Create vector to hold int[] elements, each element being one range
        // parsed out of members.
        Vector ranges = new Vector();

        // Process all integer groups in members.
        int n = (members == null ? 0 : members.length);
        for (int i = 0; i < n; ++ i) {
            // Get lower and upper bounds of the range.
            int lb, ub;
            if (members[i].length == 1) {
                lb = ub = members[i][0];
            } else if (members[i].length == 2) {
                lb = members[i][0];
                ub = members[i][1];
            } else {
                throw new IllegalArgumentException();
            }

            // Verify valid bounds.
            if (lb <= ub && lb < 0) {
                throw new IllegalArgumentException();
            }

            // Accumulate the range.
            accumulate(ranges, lb, ub);
        }

                // Return canonical array form.
                return canonicalArrayForm (ranges);
                }

    /**
     * Construct a new set-of-integer attribute containing a single integer.
     *
     * @param  member  Set member.
     *
     * @exception  IllegalArgumentException
     *     (Unchecked exception) Thrown if <CODE>member</CODE> is less than
     *     zero.
     */
    protected SetOfIntegerSyntax(int member) {
        if (member < 0) {
            throw new IllegalArgumentException();
        }
        members = new int[][] {{member, member}};
    }

    /**
     * Construct a new set-of-integer attribute containing a single range of
     * integers. If the lower bound is greater than the upper bound (a null
     * range), an empty set is constructed.
     *
     * @param  lowerBound  Lower bound of the range.
     * @param  upperBound  Upper bound of the range.
     *
     * @exception  IllegalArgumentException
     *     (Unchecked exception) Thrown if the range is non-null and
     *     <CODE>lowerBound</CODE> is less than zero.
     */
    protected SetOfIntegerSyntax(int lowerBound, int upperBound) {
        if (lowerBound <= upperBound && lowerBound < 0) {
            throw new IllegalArgumentException();
        }
        members = lowerBound <=upperBound ?
            new int[][] {{lowerBound, upperBound}} :
            new int[0][];
    }


    /**
     * Obtain this set-of-integer attribute's members in canonical array form.
     * The returned array is "safe;" the client may alter it without affecting
     * this set-of-integer attribute.
     *
     * @return  This set-of-integer attribute's members in canonical array form.
     */
    public int[][] getMembers() {
        int n = members.length;
        int[][] result = new int[n][];
        for (int i = 0; i < n; ++ i) {
            result[i] = new int[] {members[i][0], members[i][1]};
        }
        return result;
    }

    /**
     * Determine if this set-of-integer attribute contains the given value.
     *
     * @param  x  Integer value.
     *
     * @return  True if this set-of-integer attribute contains the value
     *          <CODE>x</CODE>, false otherwise.
     */
    public boolean contains(int x) {
        // Do a linear search to find the range that contains x, if any.
        int n = members.length;
        for (int i = 0; i < n; ++ i) {
            if (x < members[i][0]) {
                return false;
            } else if (x <= members[i][1]) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determine if this set-of-integer attribute contains the given integer
     * attribute's value.
     *
     * @param  attribute  Integer attribute.
     *
     * @return  True if this set-of-integer attribute contains
     *          <CODE>theAttribute</CODE>'s value, false otherwise.
     */
    public boolean contains(IntegerSyntax attribute) {
        return contains (attribute.getValue());
    }

    /**
     * Determine the smallest integer in this set-of-integer attribute that is
     * greater than the given value. If there are no integers in this
     * set-of-integer attribute greater than the given value, <CODE>-1</CODE> is
     * returned. (Since a set-of-integer attribute can only contain nonnegative
     * values, <CODE>-1</CODE> will never appear in the set.) You can use the
     * <CODE>next()</CODE> method to iterate through the integer values in a
     * set-of-integer attribute in ascending order, like this:
     * <PRE>
     *     SetOfIntegerSyntax attribute = . . .;
     *     int i = -1;
     *     while ((i = attribute.next (i)) != -1)
     *         {
     *         foo (i);
     *         }
     * </PRE>
     *
     * @param  x  Integer value.
     *
     * @return  The smallest integer in this set-of-integer attribute that is
     *          greater than <CODE>x</CODE>, or <CODE>-1</CODE> if no integer in
     *          this set-of-integer attribute is greater than <CODE>x</CODE>.
     */
    public int next(int x) {
        // Do a linear search to find the range that contains x, if any.
        int n = members.length;
        for (int i = 0; i < n; ++ i) {
            if (x < members[i][0]) {
                return members[i][0];
            } else if (x < members[i][1]) {
                return x + 1;
            }
        }
        return -1;
    }

    /**
     * Returns whether this set-of-integer attribute is equivalent to the passed
     * in object. To be equivalent, all of the following conditions must be
     * true:
     * <OL TYPE=1>
     * <LI>
     * <CODE>object</CODE> is not null.
     * <LI>
     * <CODE>object</CODE> is an instance of class SetOfIntegerSyntax.
     * <LI>
     * This set-of-integer attribute's members and <CODE>object</CODE>'s
     * members are the same.
     * </OL>
     *
     * @param  object  Object to compare to.
     *
     * @return  True if <CODE>object</CODE> is equivalent to this
     *          set-of-integer attribute, false otherwise.
     */
    public boolean equals(Object object) {
        if (object != null && object instanceof javax.print.attribute.SetOfIntegerSyntax) {
            int[][] myMembers = this.members;
            int[][] otherMembers = ((javax.print.attribute.SetOfIntegerSyntax) object).members;
            int m = myMembers.length;
            int n = otherMembers.length;
            if (m == n) {
                for (int i = 0; i < m; ++ i) {
                    if (myMembers[i][0] != otherMembers[i][0] ||
                        myMembers[i][1] != otherMembers[i][1]) {
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Returns a hash code value for this set-of-integer attribute. The hash
     * code is the sum of the lower and upper bounds of the ranges in the
     * canonical array form, or 0 for an empty set.
     */
    public int hashCode() {
        int result = 0;
        int n = members.length;
        for (int i = 0; i < n; ++ i) {
            result += members[i][0] + members[i][1];
        }
        return result;
    }

    /**
     * Returns a string value corresponding to this set-of-integer attribute.
     * The string value is a zero-length string if this set is empty. Otherwise,
     * the string value is a comma-separated list of the ranges in the canonical
     * array form, where each range is represented as <CODE>"<I>i</I>"</CODE> if
     * the lower bound equals the upper bound or
     * <CODE>"<I>i</I>-<I>j</I>"</CODE> otherwise.
     */
    public String toString() {
        StringBuffer result = new StringBuffer();
        int n = members.length;
        for (int i = 0; i < n; i++) {
            if (i > 0) {
                result.append (',');
            }
            result.append (members[i][0]);
            if (members[i][0] != members[i][1]) {
                result.append ('-');
                result.append (members[i][1]);
            }
        }
        return result.toString();
    }

}
