/*
 * Copyright (c) 1998, 2000, Oracle and/or its affiliates. All rights reserved.
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
 * File: ./org/omg/CORBA/ValueMember.java
 * From: ./ir.idl
 * Date: Fri Aug 28 16:03:31 1998
 *   By: idltojava Java IDL 1.2 Aug 11 1998 02:00:18
 */

package java8.org.omg.CORBA;

/**
 * A description in the Interface Repository of
 * a member of a <code>value</code> object.
 */
public final class ValueMember implements org.omg.CORBA.portable.IDLEntity {

    //  instance variables

    /**
     * The name of the <code>value</code> member described by this
     * <code>ValueMember</code> object.
     * @serial
     */
    public String name;

    /**
     * The repository ID of the <code>value</code> member described by
     * this <code>ValueMember</code> object;
     * @serial
     */
    public String id;

    /**
     * The repository ID of the <code>value</code> in which this member
     * is defined.
     * @serial
     */
    public String defined_in;

    /**
     * The version of the <code>value</code> in which this member is defined.
     * @serial
     */
    public String version;

    /**
     * The type of of this <code>value</code> member.
     * @serial
     */
    public org.omg.CORBA.TypeCode type;

    /**
     * The typedef that represents the IDL type of the <code>value</code>
     * member described by this <code>ValueMember</code> object.
     * @serial
     */
    public org.omg.CORBA.IDLType type_def;

    /**
     * The type of access (public, private) for the <code>value</code>
     * member described by this <code>ValueMember</code> object.
     * @serial
     */
    public short access;
    //  constructors

    /**
     * Constructs a default <code>ValueMember</code> object.
     */
    public ValueMember() { }

    /**
     * Constructs a <code>ValueMember</code> object initialized with
     * the given values.
     *
     *@param __name The name of the <code>value</code> member described by this
     * <code>ValueMember</code> object.
     *@param __id The repository ID of the <code>value</code> member described by
     * this <code>ValueMember</code> object;
     *@param __defined_in The repository ID of the <code>value</code> in which this member
     * is defined.
     *@param __version The version of the <code>value</code> in which this member is defined.
     *@param __type The type of of this <code>value</code> member.
     *@param __type_def The typedef that represents the IDL type of the <code>value</code>
     * member described by this <code>ValueMember</code> object.
     *@param __access The type of access (public, private) for the <code>value</code>
     * member described by this <code>ValueMember</code> object.
     */
    public ValueMember(String __name, String __id, String __defined_in, String __version, org.omg.CORBA.TypeCode __type, org.omg.CORBA.IDLType __type_def, short __access) {
        name = __name;
        id = __id;
        defined_in = __defined_in;
        version = __version;
        type = __type;
        type_def = __type_def;
        access = __access;
    }
}
