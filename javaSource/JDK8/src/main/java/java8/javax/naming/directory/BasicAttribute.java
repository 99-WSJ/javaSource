/*
 * Copyright (c) 1999, 2011, Oracle and/or its affiliates. All rights reserved.
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

package java8.javax.naming.directory;

import java.util.Vector;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.lang.reflect.Array;

import javax.naming.NamingException;
import javax.naming.NamingEnumeration;
import javax.naming.OperationNotSupportedException;
import javax.naming.directory.Attribute;
import javax.naming.directory.DirContext;

/**
  * This class provides a basic implementation of the <tt>Attribute</tt> interface.
  *<p>
  * This implementation does not support the schema methods
  * <tt>getAttributeDefinition()</tt> and <tt>getAttributeSyntaxDefinition()</tt>.
  * They simply throw <tt>OperationNotSupportedException</tt>.
  * Subclasses of <tt>BasicAttribute</tt> should override these methods if they
  * support them.
  *<p>
  * The <tt>BasicAttribute</tt> class by default uses <tt>Object.equals()</tt> to
  * determine equality of attribute values when testing for equality or
  * when searching for values, <em>except</em> when the value is an array.
  * For an array, each element of the array is checked using <tt>Object.equals()</tt>.
  * Subclasses of <tt>BasicAttribute</tt> can make use of schema information
  * when doing similar equality checks by overriding methods
  * in which such use of schema is meaningful.
  * Similarly, the <tt>BasicAttribute</tt> class by default returns the values passed to its
  * constructor and/or manipulated using the add/remove methods.
  * Subclasses of <tt>BasicAttribute</tt> can override <tt>get()</tt> and <tt>getAll()</tt>
  * to get the values dynamically from the directory (or implement
  * the <tt>Attribute</tt> interface directly instead of subclassing <tt>BasicAttribute</tt>).
  *<p>
  * Note that updates to <tt>BasicAttribute</tt> (such as adding or removing a value)
  * does not affect the corresponding representation of the attribute
  * in the directory.  Updates to the directory can only be effected
  * using operations in the <tt>DirContext</tt> interface.
  *<p>
  * A <tt>BasicAttribute</tt> instance is not synchronized against concurrent
  * multithreaded access. Multiple threads trying to access and modify a
  * <tt>BasicAttribute</tt> should lock the object.
  *
  * @author Rosanna Lee
  * @author Scott Seligman
  * @since 1.3
  */
public class BasicAttribute implements Attribute {
    /**
     * Holds the attribute's id. It is initialized by the public constructor and
     * cannot be null unless methods in BasicAttribute that use attrID
     * have been overridden.
     * @serial
     */
    protected String attrID;

    /**
     * Holds the attribute's values. Initialized by public constructors.
     * Cannot be null unless methods in BasicAttribute that use
     * values have been overridden.
     */
    protected transient Vector<Object> values;

    /**
     * A flag for recording whether this attribute's values are ordered.
     * @serial
     */
    protected boolean ordered = false;

    @SuppressWarnings("unchecked")
    public Object clone() {
        javax.naming.directory.BasicAttribute attr;
        try {
            attr = (javax.naming.directory.BasicAttribute)super.clone();
        } catch (CloneNotSupportedException e) {
            attr = new javax.naming.directory.BasicAttribute(attrID, ordered);
        }
        attr.values = (Vector<Object>)values.clone();
        return attr;
    }

    /**
      * Determines whether obj is equal to this attribute.
      * Two attributes are equal if their attribute-ids, syntaxes
      * and values are equal.
      * If the attribute values are unordered, the order that the values were added
      * are irrelevant. If the attribute values are ordered, then the
      * order the values must match.
      * If obj is null or not an Attribute, false is returned.
      *<p>
      * By default <tt>Object.equals()</tt> is used when comparing the attribute
      * id and its values except when a value is an array. For an array,
      * each element of the array is checked using <tt>Object.equals()</tt>.
      * A subclass may override this to make
      * use of schema syntax information and matching rules,
      * which define what it means for two attributes to be equal.
      * How and whether a subclass makes
      * use of the schema information is determined by the subclass.
      * If a subclass overrides <tt>equals()</tt>, it should also override
      * <tt>hashCode()</tt>
      * such that two attributes that are equal have the same hash code.
      *
      * @param obj      The possibly null object to check.
      * @return true if obj is equal to this attribute; false otherwise.
      * @see #hashCode
      * @see #contains
      */
    public boolean equals(Object obj) {
        if ((obj != null) && (obj instanceof Attribute)) {
            Attribute target = (Attribute)obj;

            // Check order first
            if (isOrdered() != target.isOrdered()) {
                return false;
            }
            int len;
            if (attrID.equals(target.getID()) &&
                (len=size()) == target.size()) {
                try {
                    if (isOrdered()) {
                        // Go through both list of values
                        for (int i = 0; i < len; i++) {
                            if (!valueEquals(get(i), target.get(i))) {
                                return false;
                            }
                        }
                    } else {
                        // order is not relevant; check for existence
                        Enumeration<?> theirs = target.getAll();
                        while (theirs.hasMoreElements()) {
                            if (find(theirs.nextElement()) < 0)
                                return false;
                        }
                    }
                } catch (NamingException e) {
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    /**
      * Calculates the hash code of this attribute.
      *<p>
      * The hash code is computed by adding the hash code of
      * the attribute's id and that of all of its values except for
      * values that are arrays.
      * For an array, the hash code of each element of the array is summed.
      * If a subclass overrides <tt>hashCode()</tt>, it should override
      * <tt>equals()</tt>
      * as well so that two attributes that are equal have the same hash code.
      *
      * @return an int representing the hash code of this attribute.
      * @see #equals
      */
    public int hashCode() {
        int hash = attrID.hashCode();
        int num = values.size();
        Object val;
        for (int i = 0; i < num; i ++) {
            val = values.elementAt(i);
            if (val != null) {
                if (val.getClass().isArray()) {
                    Object it;
                    int len = Array.getLength(val);
                    for (int j = 0 ; j < len ; j++) {
                        it = Array.get(val, j);
                        if (it != null) {
                            hash += it.hashCode();
                        }
                    }
                } else {
                    hash += val.hashCode();
                }
            }
        }
        return hash;
    }

    /**
      * Generates the string representation of this attribute.
      * The string consists of the attribute's id and its values.
      * This string is meant for debugging and not meant to be
      * interpreted programmatically.
      * @return The non-null string representation of this attribute.
      */
    public String toString() {
        StringBuffer answer = new StringBuffer(attrID + ": ");
        if (values.size() == 0) {
            answer.append("No values");
        } else {
            boolean start = true;
            for (Enumeration<Object> e = values.elements(); e.hasMoreElements(); ) {
                if (!start)
                    answer.append(", ");
                answer.append(e.nextElement());
                start = false;
            }
        }
        return answer.toString();
    }

    /**
      * Constructs a new instance of an unordered attribute with no value.
      *
      * @param id The attribute's id. It cannot be null.
      */
    public BasicAttribute(String id) {
        this(id, false);
    }

    /**
      * Constructs a new instance of an unordered attribute with a single value.
      *
      * @param id The attribute's id. It cannot be null.
      * @param value The attribute's value. If null, a null
      *        value is added to the attribute.
      */
    public BasicAttribute(String id, Object value) {
        this(id, value, false);
    }

    /**
      * Constructs a new instance of a possibly ordered attribute with no value.
      *
      * @param id The attribute's id. It cannot be null.
      * @param ordered true means the attribute's values will be ordered;
      * false otherwise.
      */
    public BasicAttribute(String id, boolean ordered) {
        attrID = id;
        values = new Vector<>();
        this.ordered = ordered;
    }

    /**
      * Constructs a new instance of a possibly ordered attribute with a
      * single value.
      *
      * @param id The attribute's id. It cannot be null.
      * @param value The attribute's value. If null, a null
      *        value is added to the attribute.
      * @param ordered true means the attribute's values will be ordered;
      * false otherwise.
      */
    public BasicAttribute(String id, Object value, boolean ordered) {
        this(id, ordered);
        values.addElement(value);
    }

    /**
      * Retrieves an enumeration of this attribute's values.
      *<p>
      * By default, the values returned are those passed to the
      * constructor and/or manipulated using the add/replace/remove methods.
      * A subclass may override this to retrieve the values dynamically
      * from the directory.
      */
    public NamingEnumeration<?> getAll() throws NamingException {
      return new ValuesEnumImpl();
    }

    /**
      * Retrieves one of this attribute's values.
      *<p>
      * By default, the value returned is one of those passed to the
      * constructor and/or manipulated using the add/replace/remove methods.
      * A subclass may override this to retrieve the value dynamically
      * from the directory.
      */
    public Object get() throws NamingException {
        if (values.size() == 0) {
            throw new
        NoSuchElementException("Attribute " + getID() + " has no value");
        } else {
            return values.elementAt(0);
        }
    }

    public int size() {
      return values.size();
    }

    public String getID() {
        return attrID;
    }

    /**
      * Determines whether a value is in this attribute.
      *<p>
      * By default,
      * <tt>Object.equals()</tt> is used when comparing <tt>attrVal</tt>
      * with this attribute's values except when <tt>attrVal</tt> is an array.
      * For an array, each element of the array is checked using
      * <tt>Object.equals()</tt>.
      * A subclass may use schema information to determine equality.
      */
    public boolean contains(Object attrVal) {
        return (find(attrVal) >= 0);
    }

    // For finding first element that has a null in JDK1.1 Vector.
    // In the Java 2 platform, can just replace this with Vector.indexOf(target);
    private int find(Object target) {
        Class<?> cl;
        if (target == null) {
            int ct = values.size();
            for (int i = 0 ; i < ct ; i++) {
                if (values.elementAt(i) == null)
                    return i;
            }
        } else if ((cl=target.getClass()).isArray()) {
            int ct = values.size();
            Object it;
            for (int i = 0 ; i < ct ; i++) {
                it = values.elementAt(i);
                if (it != null && cl == it.getClass()
                    && arrayEquals(target, it))
                    return i;
            }
        } else {
            return values.indexOf(target, 0);
        }
        return -1;  // not found
    }

    /**
     * Determines whether two attribute values are equal.
     * Use arrayEquals for arrays and <tt>Object.equals()</tt> otherwise.
     */
    private static boolean valueEquals(Object obj1, Object obj2) {
        if (obj1 == obj2) {
            return true; // object references are equal
        }
        if (obj1 == null) {
            return false; // obj2 was not false
        }
        if (obj1.getClass().isArray() &&
            obj2.getClass().isArray()) {
            return arrayEquals(obj1, obj2);
        }
        return (obj1.equals(obj2));
    }

    /**
     * Determines whether two arrays are equal by comparing each of their
     * elements using <tt>Object.equals()</tt>.
     */
    private static boolean arrayEquals(Object a1, Object a2) {
        int len;
        if ((len = Array.getLength(a1)) != Array.getLength(a2))
            return false;

        for (int j = 0; j < len; j++) {
            Object i1 = Array.get(a1, j);
            Object i2 = Array.get(a2, j);
            if (i1 == null || i2 == null) {
                if (i1 != i2)
                    return false;
            } else if (!i1.equals(i2)) {
                return false;
            }
        }
        return true;
    }

    /**
      * Adds a new value to this attribute.
      *<p>
      * By default, <tt>Object.equals()</tt> is used when comparing <tt>attrVal</tt>
      * with this attribute's values except when <tt>attrVal</tt> is an array.
      * For an array, each element of the array is checked using
      * <tt>Object.equals()</tt>.
      * A subclass may use schema information to determine equality.
      */
    public boolean add(Object attrVal) {
        if (isOrdered() || (find(attrVal) < 0)) {
            values.addElement(attrVal);
            return true;
        } else {
            return false;
        }
    }

    /**
      * Removes a specified value from this attribute.
      *<p>
      * By default, <tt>Object.equals()</tt> is used when comparing <tt>attrVal</tt>
      * with this attribute's values except when <tt>attrVal</tt> is an array.
      * For an array, each element of the array is checked using
      * <tt>Object.equals()</tt>.
      * A subclass may use schema information to determine equality.
      */
    public boolean remove(Object attrval) {
        // For the Java 2 platform, can just use "return removeElement(attrval);"
        // Need to do the following to handle null case

        int i = find(attrval);
        if (i >= 0) {
            values.removeElementAt(i);
            return true;
        }
        return false;
    }

    public void clear() {
        values.setSize(0);
    }

//  ---- ordering methods

    public boolean isOrdered() {
        return ordered;
    }

    public Object get(int ix) throws NamingException {
        return values.elementAt(ix);
    }

    public Object remove(int ix) {
        Object answer = values.elementAt(ix);
        values.removeElementAt(ix);
        return answer;
    }

    public void add(int ix, Object attrVal) {
        if (!isOrdered() && contains(attrVal)) {
            throw new IllegalStateException(
                "Cannot add duplicate to unordered attribute");
        }
        values.insertElementAt(attrVal, ix);
    }

    public Object set(int ix, Object attrVal) {
        if (!isOrdered() && contains(attrVal)) {
            throw new IllegalStateException(
                "Cannot add duplicate to unordered attribute");
        }

        Object answer = values.elementAt(ix);
        values.setElementAt(attrVal, ix);
        return answer;
    }

// ----------------- Schema methods

    /**
      * Retrieves the syntax definition associated with this attribute.
      *<p>
      * This method by default throws OperationNotSupportedException. A subclass
      * should override this method if it supports schema.
      */
    public DirContext getAttributeSyntaxDefinition() throws NamingException {
            throw new OperationNotSupportedException("attribute syntax");
    }

    /**
      * Retrieves this attribute's schema definition.
      *<p>
      * This method by default throws OperationNotSupportedException. A subclass
      * should override this method if it supports schema.
      */
    public DirContext getAttributeDefinition() throws NamingException {
        throw new OperationNotSupportedException("attribute definition");
    }


//  ---- serialization methods

    /**
     * Overridden to avoid exposing implementation details
     * @serialData Default field (the attribute ID -- a String),
     * followed by the number of values (an int), and the
     * individual values.
     */
    private void writeObject(java.io.ObjectOutputStream s)
            throws java.io.IOException {
        s.defaultWriteObject(); // write out the attrID
        s.writeInt(values.size());
        for (int i = 0; i < values.size(); i++) {
            s.writeObject(values.elementAt(i));
        }
    }

    /**
     * Overridden to avoid exposing implementation details.
     */
    private void readObject(java.io.ObjectInputStream s)
            throws java.io.IOException, ClassNotFoundException {
        s.defaultReadObject();  // read in the attrID
        int n = s.readInt();    // number of values
        values = new Vector<>(n);
        while (--n >= 0) {
            values.addElement(s.readObject());
        }
    }


    class ValuesEnumImpl implements NamingEnumeration<Object> {
        Enumeration<Object> list;

        ValuesEnumImpl() {
            list = values.elements();
        }

        public boolean hasMoreElements() {
            return list.hasMoreElements();
        }

        public Object nextElement() {
            return(list.nextElement());
        }

        public Object next() throws NamingException {
            return list.nextElement();
        }

        public boolean hasMore() throws NamingException {
            return list.hasMoreElements();
        }

        public void close() throws NamingException {
            list = null;
        }
    }

    /**
     * Use serialVersionUID from JNDI 1.1.1 for interoperability.
     */
    private static final long serialVersionUID = 6743528196119291326L;
}
