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

import java.text.Format;

/**
 * <code>FieldPosition</code> is a simple class used by <code>Format</code>
 * and its subclasses to identify fields in formatted output. Fields can
 * be identified in two ways:
 * <ul>
 *  <li>By an integer constant, whose names typically end with
 *      <code>_FIELD</code>. The constants are defined in the various
 *      subclasses of <code>Format</code>.
 *  <li>By a <code>Format.Field</code> constant, see <code>ERA_FIELD</code>
 *      and its friends in <code>DateFormat</code> for an example.
 * </ul>
 * <p>
 * <code>FieldPosition</code> keeps track of the position of the
 * field within the formatted output with two indices: the index
 * of the first character of the field and the index of the last
 * character of the field.
 *
 * <p>
 * One version of the <code>format</code> method in the various
 * <code>Format</code> classes requires a <code>FieldPosition</code>
 * object as an argument. You use this <code>format</code> method
 * to perform partial formatting or to get information about the
 * formatted output (such as the position of a field).
 *
 * <p>
 * If you are interested in the positions of all attributes in the
 * formatted string use the <code>Format</code> method
 * <code>formatToCharacterIterator</code>.
 *
 * @author      Mark Davis
 * @see         Format
 */
public class FieldPosition {

    /**
     * Input: Desired field to determine start and end offsets for.
     * The meaning depends on the subclass of Format.
     */
    int field = 0;

    /**
     * Output: End offset of field in text.
     * If the field does not occur in the text, 0 is returned.
     */
    int endIndex = 0;

    /**
     * Output: Start offset of field in text.
     * If the field does not occur in the text, 0 is returned.
     */
    int beginIndex = 0;

    /**
     * Desired field this FieldPosition is for.
     */
    private Format.Field attribute;

    /**
     * Creates a FieldPosition object for the given field.  Fields are
     * identified by constants, whose names typically end with _FIELD,
     * in the various subclasses of Format.
     *
     * @param field the field identifier
     * @see java.text.NumberFormat#INTEGER_FIELD
     * @see java.text.NumberFormat#FRACTION_FIELD
     * @see java.text.DateFormat#YEAR_FIELD
     * @see java.text.DateFormat#MONTH_FIELD
     */
    public FieldPosition(int field) {
        this.field = field;
    }

    /**
     * Creates a FieldPosition object for the given field constant. Fields are
     * identified by constants defined in the various <code>Format</code>
     * subclasses. This is equivalent to calling
     * <code>new FieldPosition(attribute, -1)</code>.
     *
     * @param attribute Format.Field constant identifying a field
     * @since 1.4
     */
    public FieldPosition(Format.Field attribute) {
        this(attribute, -1);
    }

    /**
     * Creates a <code>FieldPosition</code> object for the given field.
     * The field is identified by an attribute constant from one of the
     * <code>Field</code> subclasses as well as an integer field ID
     * defined by the <code>Format</code> subclasses. <code>Format</code>
     * subclasses that are aware of <code>Field</code> should give precedence
     * to <code>attribute</code> and ignore <code>fieldID</code> if
     * <code>attribute</code> is not null. However, older <code>Format</code>
     * subclasses may not be aware of <code>Field</code> and rely on
     * <code>fieldID</code>. If the field has no corresponding integer
     * constant, <code>fieldID</code> should be -1.
     *
     * @param attribute Format.Field constant identifying a field
     * @param fieldID integer constant identifying a field
     * @since 1.4
     */
    public FieldPosition(Format.Field attribute, int fieldID) {
        this.attribute = attribute;
        this.field = fieldID;
    }

    /**
     * Returns the field identifier as an attribute constant
     * from one of the <code>Field</code> subclasses. May return null if
     * the field is specified only by an integer field ID.
     *
     * @return Identifier for the field
     * @since 1.4
     */
    public Format.Field getFieldAttribute() {
        return attribute;
    }

    /**
     * Retrieves the field identifier.
     *
     * @return the field identifier
     */
    public int getField() {
        return field;
    }

    /**
     * Retrieves the index of the first character in the requested field.
     *
     * @return the begin index
     */
    public int getBeginIndex() {
        return beginIndex;
    }

    /**
     * Retrieves the index of the character following the last character in the
     * requested field.
     *
     * @return the end index
     */
    public int getEndIndex() {
        return endIndex;
    }

    /**
     * Sets the begin index.  For use by subclasses of Format.
     *
     * @param bi the begin index
     * @since 1.2
     */
    public void setBeginIndex(int bi) {
        beginIndex = bi;
    }

    /**
     * Sets the end index.  For use by subclasses of Format.
     *
     * @param ei the end index
     * @since 1.2
     */
    public void setEndIndex(int ei) {
        endIndex = ei;
    }

    /**
     * Returns a <code>Format.FieldDelegate</code> instance that is associated
     * with the FieldPosition. When the delegate is notified of the same
     * field the FieldPosition is associated with, the begin/end will be
     * adjusted.
     */
    Format.FieldDelegate getFieldDelegate() {
        return new Delegate();
    }

    /**
     * Overrides equals
     */
    public boolean equals(Object obj)
    {
        if (obj == null) return false;
        if (!(obj instanceof java.text.FieldPosition))
            return false;
        java.text.FieldPosition other = (java.text.FieldPosition) obj;
        if (attribute == null) {
            if (other.attribute != null) {
                return false;
            }
        }
        else if (!attribute.equals(other.attribute)) {
            return false;
        }
        return (beginIndex == other.beginIndex
            && endIndex == other.endIndex
            && field == other.field);
    }

    /**
     * Returns a hash code for this FieldPosition.
     * @return a hash code value for this object
     */
    public int hashCode() {
        return (field << 24) | (beginIndex << 16) | endIndex;
    }

    /**
     * Return a string representation of this FieldPosition.
     * @return  a string representation of this object
     */
    public String toString() {
        return getClass().getName() +
            "[field=" + field + ",attribute=" + attribute +
            ",beginIndex=" + beginIndex +
            ",endIndex=" + endIndex + ']';
    }


    /**
     * Return true if the receiver wants a <code>Format.Field</code> value and
     * <code>attribute</code> is equal to it.
     */
    private boolean matchesField(Format.Field attribute) {
        if (this.attribute != null) {
            return this.attribute.equals(attribute);
        }
        return false;
    }

    /**
     * Return true if the receiver wants a <code>Format.Field</code> value and
     * <code>attribute</code> is equal to it, or true if the receiver
     * represents an inteter constant and <code>field</code> equals it.
     */
    private boolean matchesField(Format.Field attribute, int field) {
        if (this.attribute != null) {
            return this.attribute.equals(attribute);
        }
        return (field == this.field);
    }


    /**
     * An implementation of FieldDelegate that will adjust the begin/end
     * of the FieldPosition if the arguments match the field of
     * the FieldPosition.
     */
    private class Delegate implements Format.FieldDelegate {
        /**
         * Indicates whether the field has been  encountered before. If this
         * is true, and <code>formatted</code> is invoked, the begin/end
         * are not updated.
         */
        private boolean encounteredField;

        public void formatted(Format.Field attr, Object value, int start,
                              int end, StringBuffer buffer) {
            if (!encounteredField && matchesField(attr)) {
                setBeginIndex(start);
                setEndIndex(end);
                encounteredField = (start != end);
            }
        }

        public void formatted(int fieldID, Format.Field attr, Object value,
                              int start, int end, StringBuffer buffer) {
            if (!encounteredField && matchesField(attr, fieldID)) {
                setBeginIndex(start);
                setEndIndex(end);
                encounteredField = (start != end);
            }
        }
    }
}
