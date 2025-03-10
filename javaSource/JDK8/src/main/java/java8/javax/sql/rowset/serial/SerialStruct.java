/*
 * Copyright (c) 2003, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java8.javax.sql.rowset.serial;

import java.sql.*;
import javax.sql.*;
import java.io.*;
import java.math.*;
import java.util.Arrays;
import java.util.Map;
import java.util.Vector;

import javax.sql.rowset.*;
import javax.sql.rowset.serial.SQLOutputImpl;
import javax.sql.rowset.serial.SerialArray;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialClob;
import javax.sql.rowset.serial.SerialException;
import javax.sql.rowset.serial.SerialRef;

/**
 * A serialized mapping in the Java programming language of an SQL
 * structured type. Each attribute that is not already serialized
 * is mapped to a serialized form, and if an attribute is itself
 * a structured type, each of its attributes that is not already
 * serialized is mapped to a serialized form.
 * <P>
 * In addition, the structured type is custom mapped to a class in the
 * Java programming language if there is such a mapping, as are
 * its attributes, if appropriate.
 * <P>
 * The <code>SerialStruct</code> class provides a constructor for creating
 * an instance from a <code>Struct</code> object, a method for retrieving
 * the SQL type name of the SQL structured type in the database, and methods
 * for retrieving its attribute values.
 *
 * <h3> Thread safety </h3>
 *
 * A SerialStruct is not safe for use by multiple concurrent threads.  If a
 * SerialStruct is to be used by more than one thread then access to the
 * SerialStruct should be controlled by appropriate synchronization.
 *
 */
public class SerialStruct implements Struct, Serializable, Cloneable {


    /**
     * The SQL type name for the structured type that this
     * <code>SerialStruct</code> object represents.  This is the name
     * used in the SQL definition of the SQL structured type.
     *
     * @serial
     */
    private String SQLTypeName;

    /**
     * An array of <code>Object</code> instances in  which each
     * element is an attribute of the SQL structured type that this
     * <code>SerialStruct</code> object represents.  The attributes are
     * ordered according to their order in the definition of the
     * SQL structured type.
     *
     * @serial
     */
    private Object attribs[];

    /**
     * Constructs a <code>SerialStruct</code> object from the given
     * <code>Struct</code> object, using the given <code>java.util.Map</code>
     * object for custom mapping the SQL structured type or any of its
     * attributes that are SQL structured types.
     *
     * @param in an instance of {@code Struct}
     * @param map a <code>java.util.Map</code> object in which
     *        each entry consists of 1) a <code>String</code> object
     *        giving the fully qualified name of a UDT and 2) the
     *        <code>Class</code> object for the <code>SQLData</code> implementation
     *        that defines how the UDT is to be mapped
     * @throws SerialException if an error occurs
     * @see Struct
     */
     public SerialStruct(Struct in, Map<String,Class<?>> map)
         throws SerialException
     {

        try {

        // get the type name
        SQLTypeName = in.getSQLTypeName();
        System.out.println("SQLTypeName: " + SQLTypeName);

        // get the attributes of the struct
        attribs = in.getAttributes(map);

        /*
         * the array may contain further Structs
         * and/or classes that have been mapped,
         * other types that we have to serialize
         */
        mapToSerial(map);

        } catch (SQLException e) {
            throw new SerialException(e.getMessage());
        }
    }

     /**
      * Constructs a <code>SerialStruct</code> object from the
      * given <code>SQLData</code> object, using the given type
      * map to custom map it to a class in the Java programming
      * language.  The type map gives the SQL type and the class
      * to which it is mapped.  The <code>SQLData</code> object
      * defines the class to which the SQL type will be mapped.
      *
      * @param in an instance of the <code>SQLData</code> class
      *           that defines the mapping of the SQL structured
      *           type to one or more objects in the Java programming language
      * @param map a <code>java.util.Map</code> object in which
      *        each entry consists of 1) a <code>String</code> object
      *        giving the fully qualified name of a UDT and 2) the
      *        <code>Class</code> object for the <code>SQLData</code> implementation
      *        that defines how the UDT is to be mapped
      * @throws SerialException if an error occurs
      */
    public SerialStruct(SQLData in, Map<String,Class<?>> map)
        throws SerialException
    {

        try {

        //set the type name
        SQLTypeName = in.getSQLTypeName();

        Vector<Object> tmp = new Vector<>();
        in.writeSQL(new SQLOutputImpl(tmp, map));
        attribs = tmp.toArray();

        } catch (SQLException e) {
            throw new SerialException(e.getMessage());
        }
    }


    /**
     * Retrieves the SQL type name for this <code>SerialStruct</code>
     * object. This is the name used in the SQL definition of the
     * structured type
     *
     * @return a <code>String</code> object representing the SQL
     *         type name for the SQL structured type that this
     *         <code>SerialStruct</code> object represents
     * @throws SerialException if an error occurs
     */
    public String getSQLTypeName() throws SerialException {
        return SQLTypeName;
    }

    /**
     * Retrieves an array of <code>Object</code> values containing the
     * attributes of the SQL structured type that this
     * <code>SerialStruct</code> object represents.
     *
     * @return an array of <code>Object</code> values, with each
     *         element being an attribute of the SQL structured type
     *         that this <code>SerialStruct</code> object represents
     * @throws SerialException if an error occurs
     */
    public Object[]  getAttributes() throws SerialException {
        Object[] val = this.attribs;
        return (val == null) ? null : Arrays.copyOf(val, val.length);
    }

    /**
     * Retrieves the attributes for the SQL structured type that
     * this <code>SerialStruct</code> represents as an array of
     * <code>Object</code> values, using the given type map for
     * custom mapping if appropriate.
     *
     * @param map a <code>java.util.Map</code> object in which
     *        each entry consists of 1) a <code>String</code> object
     *        giving the fully qualified name of a UDT and 2) the
     *        <code>Class</code> object for the <code>SQLData</code> implementation
     *        that defines how the UDT is to be mapped
     * @return an array of <code>Object</code> values, with each
     *         element being an attribute of the SQL structured
     *         type that this <code>SerialStruct</code> object
     *         represents
     * @throws SerialException if an error occurs
     */
    public Object[] getAttributes(Map<String,Class<?>> map)
        throws SerialException
    {
        Object[] val = this.attribs;
        return (val == null) ? null : Arrays.copyOf(val, val.length);
    }


    /**
     * Maps attributes of an SQL structured type that are not
     * serialized to a serialized form, using the given type map
     * for custom mapping when appropriate.  The following types
     * in the Java programming language are mapped to their
     * serialized forms:  <code>Struct</code>, <code>SQLData</code>,
     * <code>Ref</code>, <code>Blob</code>, <code>Clob</code>, and
     * <code>Array</code>.
     * <P>
     * This method is called internally and is not used by an
     * application programmer.
     *
     * @param map a <code>java.util.Map</code> object in which
     *        each entry consists of 1) a <code>String</code> object
     *        giving the fully qualified name of a UDT and 2) the
     *        <code>Class</code> object for the <code>SQLData</code> implementation
     *        that defines how the UDT is to be mapped
     * @throws SerialException if an error occurs
     */
    private void mapToSerial(Map<String,Class<?>> map) throws SerialException {

        try {

        for (int i = 0; i < attribs.length; i++) {
            if (attribs[i] instanceof Struct) {
                attribs[i] = new javax.sql.rowset.serial.SerialStruct((Struct)attribs[i], map);
            } else if (attribs[i] instanceof SQLData) {
                attribs[i] = new javax.sql.rowset.serial.SerialStruct((SQLData)attribs[i], map);
            } else if (attribs[i] instanceof Blob) {
                attribs[i] = new SerialBlob((Blob)attribs[i]);
            } else if (attribs[i] instanceof Clob) {
                attribs[i] = new SerialClob((Clob)attribs[i]);
            } else if (attribs[i] instanceof Ref) {
                attribs[i] = new SerialRef((Ref)attribs[i]);
            } else if (attribs[i] instanceof Array) {
                attribs[i] = new SerialArray((Array)attribs[i], map);
            }
        }

        } catch (SQLException e) {
            throw new SerialException(e.getMessage());
        }
        return;
    }

    /**
     * Compares this SerialStruct to the specified object.  The result is
     * {@code true} if and only if the argument is not {@code null} and is a
     * {@code SerialStruct} object whose attributes are identical to this
     * object's attributes
     *
     * @param  obj The object to compare this {@code SerialStruct} against
     *
     * @return {@code true} if the given object represents a {@code SerialStruct}
     *          equivalent to this SerialStruct, {@code false} otherwise
     *
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof javax.sql.rowset.serial.SerialStruct) {
            javax.sql.rowset.serial.SerialStruct ss = (javax.sql.rowset.serial.SerialStruct)obj;
            return SQLTypeName.equals(ss.SQLTypeName) &&
                    Arrays.equals(attribs, ss.attribs);
        }
        return false;
    }

    /**
     * Returns a hash code for this {@code SerialStruct}. The hash code for a
     * {@code SerialStruct} object is computed using the hash codes
     * of the attributes of the {@code SerialStruct} object and its
     * {@code SQLTypeName}
     *
     * @return  a hash code value for this object.
     */
    public int hashCode() {
        return ((31 + Arrays.hashCode(attribs)) * 31) * 31
                + SQLTypeName.hashCode();
    }

    /**
     * Returns a clone of this {@code SerialStruct}. The copy will contain a
     * reference to a clone of the underlying attribs array, not a reference
     * to the original underlying attribs array of this {@code SerialStruct} object.
     *
     * @return  a clone of this SerialStruct
     */
    public Object clone() {
        try {
            javax.sql.rowset.serial.SerialStruct ss = (javax.sql.rowset.serial.SerialStruct) super.clone();
            ss.attribs = Arrays.copyOf(attribs, attribs.length);
            return ss;
        } catch (CloneNotSupportedException ex) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }

    }

    /**
     * readObject is called to restore the state of the {@code SerialStruct} from
     * a stream.
     */
    private void readObject(ObjectInputStream s)
            throws IOException, ClassNotFoundException {

       ObjectInputStream.GetField fields = s.readFields();
       Object[] tmp = (Object[])fields.get("attribs", null);
       attribs = tmp == null ? null : tmp.clone();
       SQLTypeName = (String)fields.get("SQLTypeName", null);
    }

    /**
     * writeObject is called to save the state of the {@code SerialStruct}
     * to a stream.
     */
    private void writeObject(ObjectOutputStream s)
            throws IOException, ClassNotFoundException {

        ObjectOutputStream.PutField fields = s.putFields();
        fields.put("attribs", attribs);
        fields.put("SQLTypeName", SQLTypeName);
        s.writeFields();
    }

    /**
     * The identifier that assists in the serialization of this
     * <code>SerialStruct</code> object.
     */
    static final long serialVersionUID = -8322445504027483372L;
}
