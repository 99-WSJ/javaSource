/*
 * Copyright (c) 2000, 2013, Oracle and/or its affiliates. All rights reserved.
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


package java8.javax.management.openmbean;


// java import
//
import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;

// jmx import
//
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.TabularData;


/**
 * The <code>SimpleType</code> class is the <i>open type</i> class whose instances describe
 * all <i>open data</i> values which are neither arrays,
 * nor {@link CompositeData CompositeData} values,
 * nor {@link TabularData TabularData} values.
 * It predefines all its possible instances as static fields, and has no public constructor.
 * <p>
 * Given a <code>SimpleType</code> instance describing values whose Java class name is <i>className</i>,
 * the internal fields corresponding to the name and description of this <code>SimpleType</code> instance
 * are also set to <i>className</i>.
 * In other words, its methods <code>getClassName</code>, <code>getTypeName</code> and <code>getDescription</code>
 * all return the same string value <i>className</i>.
 *
 * @since 1.5
 */
public final class SimpleType<T> extends OpenType<T> {

    /* Serial version */
    static final long serialVersionUID = 2215577471957694503L;

    // SimpleType instances.
    // IF YOU ADD A SimpleType, YOU MUST UPDATE OpenType and typeArray

    /**
     * The <code>SimpleType</code> instance describing values whose
     * Java class name is <code>java.lang.Void</code>.
     */
    public static final javax.management.openmbean.SimpleType<Void> VOID =
        new javax.management.openmbean.SimpleType<Void>(Void.class);

    /**
     * The <code>SimpleType</code> instance describing values whose
     * Java class name is <code>java.lang.Boolean</code>.
     */
    public static final javax.management.openmbean.SimpleType<Boolean> BOOLEAN =
        new javax.management.openmbean.SimpleType<Boolean>(Boolean.class);

    /**
     * The <code>SimpleType</code> instance describing values whose
     * Java class name is <code>java.lang.Character</code>.
     */
    public static final javax.management.openmbean.SimpleType<Character> CHARACTER =
        new javax.management.openmbean.SimpleType<Character>(Character.class);

    /**
     * The <code>SimpleType</code> instance describing values whose
     * Java class name is <code>java.lang.Byte</code>.
     */
    public static final javax.management.openmbean.SimpleType<Byte> BYTE =
        new javax.management.openmbean.SimpleType<Byte>(Byte.class);

    /**
     * The <code>SimpleType</code> instance describing values whose
     * Java class name is <code>java.lang.Short</code>.
     */
    public static final javax.management.openmbean.SimpleType<Short> SHORT =
        new javax.management.openmbean.SimpleType<Short>(Short.class);

    /**
     * The <code>SimpleType</code> instance describing values whose
     * Java class name is <code>java.lang.Integer</code>.
     */
    public static final javax.management.openmbean.SimpleType<Integer> INTEGER =
        new javax.management.openmbean.SimpleType<Integer>(Integer.class);

    /**
     * The <code>SimpleType</code> instance describing values whose
     * Java class name is <code>java.lang.Long</code>.
     */
    public static final javax.management.openmbean.SimpleType<Long> LONG =
        new javax.management.openmbean.SimpleType<Long>(Long.class);

    /**
     * The <code>SimpleType</code> instance describing values whose
     * Java class name is <code>java.lang.Float</code>.
     */
    public static final javax.management.openmbean.SimpleType<Float> FLOAT =
        new javax.management.openmbean.SimpleType<Float>(Float.class);

    /**
     * The <code>SimpleType</code> instance describing values whose
     * Java class name is <code>java.lang.Double</code>.
     */
    public static final javax.management.openmbean.SimpleType<Double> DOUBLE =
        new javax.management.openmbean.SimpleType<Double>(Double.class);

    /**
     * The <code>SimpleType</code> instance describing values whose
     * Java class name is <code>java.lang.String</code>.
     */
    public static final javax.management.openmbean.SimpleType<String> STRING =
        new javax.management.openmbean.SimpleType<String>(String.class);

    /**
     * The <code>SimpleType</code> instance describing values whose
     * Java class name is <code>java.math.BigDecimal</code>.
     */
    public static final javax.management.openmbean.SimpleType<BigDecimal> BIGDECIMAL =
        new javax.management.openmbean.SimpleType<BigDecimal>(BigDecimal.class);

    /**
     * The <code>SimpleType</code> instance describing values whose
     * Java class name is <code>java.math.BigInteger</code>.
     */
    public static final javax.management.openmbean.SimpleType<BigInteger> BIGINTEGER =
        new javax.management.openmbean.SimpleType<BigInteger>(BigInteger.class);

    /**
     * The <code>SimpleType</code> instance describing values whose
     * Java class name is <code>java.util.Date</code>.
     */
    public static final javax.management.openmbean.SimpleType<Date> DATE =
        new javax.management.openmbean.SimpleType<Date>(Date.class);

    /**
     * The <code>SimpleType</code> instance describing values whose
     * Java class name is <code>javax.management.ObjectName</code>.
     */
    public static final javax.management.openmbean.SimpleType<ObjectName> OBJECTNAME =
        new javax.management.openmbean.SimpleType<ObjectName>(ObjectName.class);

    private static final javax.management.openmbean.SimpleType<?>[] typeArray = {
        VOID, BOOLEAN, CHARACTER, BYTE, SHORT, INTEGER, LONG, FLOAT,
        DOUBLE, STRING, BIGDECIMAL, BIGINTEGER, DATE, OBJECTNAME,
    };


    private transient Integer myHashCode = null;        // As this instance is immutable, these two values
    private transient String  myToString = null;        // need only be calculated once.


    /* *** Constructor *** */

    private SimpleType(Class<T> valueClass) {
        super(valueClass.getName(), valueClass.getName(), valueClass.getName(),
              false);
    }


    /* *** SimpleType specific information methods *** */

    /**
     * Tests whether <var>obj</var> is a value for this
     * <code>SimpleType</code> instance.  <p> This method returns
     * <code>true</code> if and only if <var>obj</var> is not null and
     * <var>obj</var>'s class name is the same as the className field
     * defined for this <code>SimpleType</code> instance (ie the class
     * name returned by the {@link OpenType#getClassName()
     * getClassName} method).
     *
     * @param obj the object to be tested.
     *
     * @return <code>true</code> if <var>obj</var> is a value for this
     * <code>SimpleType</code> instance.
     */
    public boolean isValue(Object obj) {

        // if obj is null, return false
        //
        if (obj == null) {
            return false;
        }

        // Test if obj's class name is the same as for this instance
        //
        return this.getClassName().equals(obj.getClass().getName());
    }


    /* *** Methods overriden from class Object *** */

    /**
     * Compares the specified <code>obj</code> parameter with this <code>SimpleType</code> instance for equality.
     * <p>
     * Two <code>SimpleType</code> instances are equal if and only if their
     * {@link OpenType#getClassName() getClassName} methods return the same value.
     *
     * @param  obj  the object to be compared for equality with this <code>SimpleType</code> instance;
     *              if <var>obj</var> is <code>null</code> or is not an instance of the class <code>SimpleType</code>,
     *              <code>equals</code> returns <code>false</code>.
     *
     * @return  <code>true</code> if the specified object is equal to this <code>SimpleType</code> instance.
     */
    public boolean equals(Object obj) {

        /* If it weren't for readReplace(), we could replace this method
           with just:
           return (this == obj);
        */

        if (!(obj instanceof javax.management.openmbean.SimpleType<?>))
            return false;

        javax.management.openmbean.SimpleType<?> other = (javax.management.openmbean.SimpleType<?>) obj;

        // Test if other's className field is the same as for this instance
        //
        return this.getClassName().equals(other.getClassName());
    }

    /**
     * Returns the hash code value for this <code>SimpleType</code> instance.
     * The hash code of a <code>SimpleType</code> instance is the the hash code of
     * the string value returned by the {@link OpenType#getClassName() getClassName} method.
     * <p>
     * As <code>SimpleType</code> instances are immutable, the hash code for this instance is calculated once,
     * on the first call to <code>hashCode</code>, and then the same value is returned for subsequent calls.
     *
     * @return  the hash code value for this <code>SimpleType</code> instance
     */
    public int hashCode() {

        // Calculate the hash code value if it has not yet been done (ie 1st call to hashCode())
        //
        if (myHashCode == null) {
            myHashCode = Integer.valueOf(this.getClassName().hashCode());
        }

        // return always the same hash code for this instance (immutable)
        //
        return myHashCode.intValue();
    }

    /**
     * Returns a string representation of this <code>SimpleType</code> instance.
     * <p>
     * The string representation consists of
     * the name of this class (ie <code>javax.management.openmbean.SimpleType</code>) and the type name
     * for this instance (which is the java class name of the values this <code>SimpleType</code> instance represents).
     * <p>
     * As <code>SimpleType</code> instances are immutable, the string representation for this instance is calculated once,
     * on the first call to <code>toString</code>, and then the same value is returned for subsequent calls.
     *
     * @return  a string representation of this <code>SimpleType</code> instance
     */
    public String toString() {

        // Calculate the string representation if it has not yet been done (ie 1st call to toString())
        //
        if (myToString == null) {
            myToString = this.getClass().getName()+ "(name="+ getTypeName() +")";
        }

        // return always the same string representation for this instance (immutable)
        //
        return myToString;
    }

    private static final Map<javax.management.openmbean.SimpleType<?>, javax.management.openmbean.SimpleType<?>> canonicalTypes =
        new HashMap<javax.management.openmbean.SimpleType<?>, javax.management.openmbean.SimpleType<?>>();
    static {
        for (int i = 0; i < typeArray.length; i++) {
            final javax.management.openmbean.SimpleType<?> type = typeArray[i];
            canonicalTypes.put(type, type);
        }
    }

    /**
     * Replace an object read from an {@link
     * java.io.ObjectInputStream} with the unique instance for that
     * value.
     *
     * @return the replacement object.
     *
     * @exception ObjectStreamException if the read object cannot be
     * resolved.
     */
    public Object readResolve() throws ObjectStreamException {
        final javax.management.openmbean.SimpleType<?> canonical = canonicalTypes.get(this);
        if (canonical == null) {
            // Should not happen
            throw new InvalidObjectException("Invalid SimpleType: " + this);
        }
        return canonical;
    }
}
