/*
 * Copyright (c) 1997, 2012, Oracle and/or its affiliates. All rights reserved.
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

package java8.java.awt.datatransfer;

import java.awt.datatransfer.MimeTypeParseException;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Locale;


/**
 * A Multipurpose Internet Mail Extension (MIME) type, as defined
 * in RFC 2045 and 2046.
 *
 * THIS IS *NOT* - REPEAT *NOT* - A PUBLIC CLASS! DataFlavor IS
 * THE PUBLIC INTERFACE, AND THIS IS PROVIDED AS A ***PRIVATE***
 * (THAT IS AS IN *NOT* PUBLIC) HELPER CLASS!
 */
class MimeType implements Externalizable, Cloneable {

    /*
     * serialization support
     */

    static final long serialVersionUID = -6568722458793895906L;

    /**
     * Constructor for externalization; this constructor should not be
     * called directly by an application, since the result will be an
     * uninitialized, immutable <code>MimeType</code> object.
     */
    public MimeType() {
    }

    /**
     * Builds a <code>MimeType</code> from a <code>String</code>.
     *
     * @param rawdata text used to initialize the <code>MimeType</code>
     * @throws NullPointerException if <code>rawdata</code> is null
     */
    public MimeType(String rawdata) throws MimeTypeParseException {
        parse(rawdata);
    }

    /**
     * Builds a <code>MimeType</code> with the given primary and sub
     * type but has an empty parameter list.
     *
     * @param primary the primary type of this <code>MimeType</code>
     * @param sub the subtype of this <code>MimeType</code>
     * @throws NullPointerException if either <code>primary</code> or
     *         <code>sub</code> is null
     */
    public MimeType(String primary, String sub) throws MimeTypeParseException {
        this(primary, sub, new java.awt.datatransfer.MimeTypeParameterList());
    }

    /**
     * Builds a <code>MimeType</code> with a pre-defined
     * and valid (or empty) parameter list.
     *
     * @param primary the primary type of this <code>MimeType</code>
     * @param sub the subtype of this <code>MimeType</code>
     * @param mtpl the requested parameter list
     * @throws NullPointerException if either <code>primary</code>,
     *         <code>sub</code> or <code>mtpl</code> is null
     */
    public MimeType(String primary, String sub, java.awt.datatransfer.MimeTypeParameterList mtpl) throws
MimeTypeParseException {
        //    check to see if primary is valid
        if(isValidToken(primary)) {
            primaryType = primary.toLowerCase(Locale.ENGLISH);
        } else {
            throw new MimeTypeParseException("Primary type is invalid.");
        }

        //    check to see if sub is valid
        if(isValidToken(sub)) {
            subType = sub.toLowerCase(Locale.ENGLISH);
        } else {
            throw new MimeTypeParseException("Sub type is invalid.");
        }

        parameters = (java.awt.datatransfer.MimeTypeParameterList)mtpl.clone();
    }

    public int hashCode() {

        // We sum up the hash codes for all of the strings. This
        // way, the order of the strings is irrelevant
        int code = 0;
        code += primaryType.hashCode();
        code += subType.hashCode();
        code += parameters.hashCode();
        return code;
    } // hashCode()

    /**
     * <code>MimeType</code>s are equal if their primary types,
     * subtypes, and  parameters are all equal. No default values
     * are taken into account.
     * @param thatObject the object to be evaluated as a
     *    <code>MimeType</code>
     * @return <code>true</code> if <code>thatObject</code> is
     *    a <code>MimeType</code>; otherwise returns <code>false</code>
     */
    public boolean equals(Object thatObject) {
        if (!(thatObject instanceof java.awt.datatransfer.MimeType)) {
            return false;
        }
        java.awt.datatransfer.MimeType that = (java.awt.datatransfer.MimeType)thatObject;
        boolean isIt =
            ((this.primaryType.equals(that.primaryType)) &&
             (this.subType.equals(that.subType)) &&
             (this.parameters.equals(that.parameters)));
        return isIt;
    } // equals()

    /**
     * A routine for parsing the MIME type out of a String.
     *
     * @throws NullPointerException if <code>rawdata</code> is null
     */
    private void parse(String rawdata) throws MimeTypeParseException {
        int slashIndex = rawdata.indexOf('/');
        int semIndex = rawdata.indexOf(';');
        if((slashIndex < 0) && (semIndex < 0)) {
            //    neither character is present, so treat it
            //    as an error
            throw new MimeTypeParseException("Unable to find a sub type.");
        } else if((slashIndex < 0) && (semIndex >= 0)) {
            //    we have a ';' (and therefore a parameter list),
            //    but no '/' indicating a sub type is present
            throw new MimeTypeParseException("Unable to find a sub type.");
        } else if((slashIndex >= 0) && (semIndex < 0)) {
            //    we have a primary and sub type but no parameter list
            primaryType = rawdata.substring(0,slashIndex).
                trim().toLowerCase(Locale.ENGLISH);
            subType = rawdata.substring(slashIndex + 1).
                trim().toLowerCase(Locale.ENGLISH);
            parameters = new java.awt.datatransfer.MimeTypeParameterList();
        } else if (slashIndex < semIndex) {
            //    we have all three items in the proper sequence
            primaryType = rawdata.substring(0, slashIndex).
                trim().toLowerCase(Locale.ENGLISH);
            subType = rawdata.substring(slashIndex + 1,
                semIndex).trim().toLowerCase(Locale.ENGLISH);
            parameters = new
                    java.awt.datatransfer.MimeTypeParameterList(rawdata.substring(semIndex));
        } else {
            //    we have a ';' lexically before a '/' which means we have a primary type
            //    & a parameter list but no sub type
            throw new MimeTypeParseException("Unable to find a sub type.");
        }

        //    now validate the primary and sub types

        //    check to see if primary is valid
        if(!isValidToken(primaryType)) {
            throw new MimeTypeParseException("Primary type is invalid.");
        }

        //    check to see if sub is valid
        if(!isValidToken(subType)) {
            throw new MimeTypeParseException("Sub type is invalid.");
        }
    }

    /**
     * Retrieve the primary type of this object.
     */
    public String getPrimaryType() {
        return primaryType;
    }

    /**
     * Retrieve the sub type of this object.
     */
    public String getSubType() {
        return subType;
    }

    /**
     * Retrieve a copy of this object's parameter list.
     */
    public java.awt.datatransfer.MimeTypeParameterList getParameters() {
        return (java.awt.datatransfer.MimeTypeParameterList)parameters.clone();
    }

    /**
     * Retrieve the value associated with the given name, or null if there
     * is no current association.
     */
    public String getParameter(String name) {
        return parameters.get(name);
    }

    /**
     * Set the value to be associated with the given name, replacing
     * any previous association.
     *
     * @throw IllegalArgumentException if parameter or value is illegal
     */
    public void setParameter(String name, String value) {
        parameters.set(name, value);
    }

    /**
     * Remove any value associated with the given name.
     *
     * @throw IllegalArgumentExcpetion if parameter may not be deleted
     */
    public void removeParameter(String name) {
        parameters.remove(name);
    }

    /**
     * Return the String representation of this object.
     */
    public String toString() {
        return getBaseType() + parameters.toString();
    }

    /**
     * Return a String representation of this object
     * without the parameter list.
     */
    public String getBaseType() {
        return primaryType + "/" + subType;
    }

    /**
     * Returns <code>true</code> if the primary type and the
     * subtype of this object are the same as the specified
     * <code>type</code>; otherwise returns <code>false</code>.
     *
     * @param type the type to compare to <code>this</code>'s type
     * @return <code>true</code> if the primary type and the
     *    subtype of this object are the same as the
     *    specified <code>type</code>; otherwise returns
     *    <code>false</code>
     */
    public boolean match(java.awt.datatransfer.MimeType type) {
        if (type == null)
            return false;
        return primaryType.equals(type.getPrimaryType())
                    && (subType.equals("*")
                            || type.getSubType().equals("*")
                            || (subType.equals(type.getSubType())));
    }

    /**
     * Returns <code>true</code> if the primary type and the
     * subtype of this object are the same as the content type
     * described in <code>rawdata</code>; otherwise returns
     * <code>false</code>.
     *
     * @param rawdata the raw data to be examined
     * @return <code>true</code> if the primary type and the
     *    subtype of this object are the same as the content type
     *    described in <code>rawdata</code>; otherwise returns
     *    <code>false</code>; if <code>rawdata</code> is
     *    <code>null</code>, returns <code>false</code>
     */
    public boolean match(String rawdata) throws MimeTypeParseException {
        if (rawdata == null)
            return false;
        return match(new java.awt.datatransfer.MimeType(rawdata));
    }

    /**
     * The object implements the writeExternal method to save its contents
     * by calling the methods of DataOutput for its primitive values or
     * calling the writeObject method of ObjectOutput for objects, strings
     * and arrays.
     * @exception IOException Includes any I/O exceptions that may occur
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        String s = toString(); // contains ASCII chars only
        // one-to-one correspondence between ASCII char and byte in UTF string
        if (s.length() <= 65535) { // 65535 is max length of UTF string
            out.writeUTF(s);
        } else {
            out.writeByte(0);
            out.writeByte(0);
            out.writeInt(s.length());
            out.write(s.getBytes());
        }
    }

    /**
     * The object implements the readExternal method to restore its
     * contents by calling the methods of DataInput for primitive
     * types and readObject for objects, strings and arrays.  The
     * readExternal method must read the values in the same sequence
     * and with the same types as were written by writeExternal.
     * @exception ClassNotFoundException If the class for an object being
     *              restored cannot be found.
     */
    public void readExternal(ObjectInput in) throws IOException,
ClassNotFoundException {
        String s = in.readUTF();
        if (s == null || s.length() == 0) { // long mime type
            byte[] ba = new byte[in.readInt()];
            in.readFully(ba);
            s = new String(ba);
        }
        try {
            parse(s);
        } catch(MimeTypeParseException e) {
            throw new IOException(e.toString());
        }
    }

    /**
     * Returns a clone of this object.
     * @return a clone of this object
     */

    public Object clone() {
        java.awt.datatransfer.MimeType newObj = null;
        try {
            newObj = (java.awt.datatransfer.MimeType)super.clone();
        } catch (CloneNotSupportedException cannotHappen) {
        }
        newObj.parameters = (java.awt.datatransfer.MimeTypeParameterList)parameters.clone();
        return newObj;
    }

    private String    primaryType;
    private String    subType;
    private java.awt.datatransfer.MimeTypeParameterList parameters;

    //    below here be scary parsing related things

    /**
     * Determines whether or not a given character belongs to a legal token.
     */
    private static boolean isTokenChar(char c) {
        return ((c > 040) && (c < 0177)) && (TSPECIALS.indexOf(c) < 0);
    }

    /**
     * Determines whether or not a given string is a legal token.
     *
     * @throws NullPointerException if <code>s</code> is null
     */
    private boolean isValidToken(String s) {
        int len = s.length();
        if(len > 0) {
            for (int i = 0; i < len; ++i) {
                char c = s.charAt(i);
                if (!isTokenChar(c)) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * A string that holds all the special chars.
     */

    private static final String TSPECIALS = "()<>@,;:\\\"/[]?=";

} // class MimeType
