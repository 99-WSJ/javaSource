/*
 * Copyright (c) 2000, 2008, Oracle and/or its affiliates. All rights reserved.
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
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.InvalidKeyException;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

// jmx import
import java.util.TreeSet;
//


/**
 * The <tt>CompositeDataSupport</tt> class is the <i>open data</i> class which
 * implements the <tt>CompositeData</tt> interface.
 *
 *
 * @since 1.5
 */
public class CompositeDataSupport
    implements CompositeData, Serializable {

    /* Serial version */
    static final long serialVersionUID = 8003518976613702244L;

    /**
     * @serial Internal representation of the mapping of item names to their
     * respective values.
     *         A {@link SortedMap} is used for faster retrieval of elements.
     */
    private final SortedMap<String, Object> contents;

    /**
     * @serial The <i>composite type </i> of this <i>composite data</i> instance.
     */
    private final CompositeType compositeType;

    /**
     * <p>Constructs a <tt>CompositeDataSupport</tt> instance with the specified
     * <tt>compositeType</tt>, whose item values
     * are specified by <tt>itemValues[]</tt>, in the same order as in
     * <tt>itemNames[]</tt>.
     * As a <tt>CompositeType</tt> does not specify any order on its items,
     * the <tt>itemNames[]</tt> parameter is used
     * to specify the order in which the values are given in <tt>itemValues[]</tt>.
     * The items contained in this <tt>CompositeDataSupport</tt> instance are
     * internally stored in a <tt>TreeMap</tt>,
     * thus sorted in ascending lexicographic order of their names, for faster
     * retrieval of individual item values.</p>
     *
     * <p>The constructor checks that all the constraints listed below for each
     * parameter are satisfied,
     * and throws the appropriate exception if they are not.</p>
     *
     * @param compositeType the <i>composite type </i> of this <i>composite
     * data</i> instance; must not be null.
     *
     * @param itemNames <tt>itemNames</tt> must list, in any order, all the
     * item names defined in <tt>compositeType</tt>; the order in which the
     * names are listed, is used to match values in <tt>itemValues[]</tt>; must
     * not be null or empty.
     *
     * @param itemValues the values of the items, listed in the same order as
     * their respective names in <tt>itemNames</tt>; each item value can be
     * null, but if it is non-null it must be a valid value for the open type
     * defined in <tt>compositeType</tt> for the corresponding item; must be of
     * the same size as <tt>itemNames</tt>; must not be null or empty.
     *
     * @throws IllegalArgumentException <tt>compositeType</tt> is null, or
     * <tt>itemNames[]</tt> or <tt>itemValues[]</tt> is null or empty, or one
     * of the elements in <tt>itemNames[]</tt> is a null or empty string, or
     * <tt>itemNames[]</tt> and <tt>itemValues[]</tt> are not of the same size.
     *
     * @throws OpenDataException <tt>itemNames[]</tt> or
     * <tt>itemValues[]</tt>'s size differs from the number of items defined in
     * <tt>compositeType</tt>, or one of the elements in <tt>itemNames[]</tt>
     * does not exist as an item name defined in <tt>compositeType</tt>, or one
     * of the elements in <tt>itemValues[]</tt> is not a valid value for the
     * corresponding item as defined in <tt>compositeType</tt>.
     */
    public CompositeDataSupport(
            CompositeType compositeType, String[] itemNames, Object[] itemValues)
            throws OpenDataException {
        this(makeMap(itemNames, itemValues), compositeType);
    }

    private static SortedMap<String, Object> makeMap(
            String[] itemNames, Object[] itemValues)
            throws OpenDataException {

        if (itemNames == null || itemValues == null)
            throw new IllegalArgumentException("Null itemNames or itemValues");
        if (itemNames.length == 0 || itemValues.length == 0)
            throw new IllegalArgumentException("Empty itemNames or itemValues");
        if (itemNames.length != itemValues.length) {
            throw new IllegalArgumentException(
                    "Different lengths: itemNames[" + itemNames.length +
                    "], itemValues[" + itemValues.length + "]");
        }

        SortedMap<String, Object> map = new TreeMap<String, Object>();
        for (int i = 0; i < itemNames.length; i++) {
            String name = itemNames[i];
            if (name == null || name.equals(""))
                throw new IllegalArgumentException("Null or empty item name");
            if (map.containsKey(name))
                throw new OpenDataException("Duplicate item name " + name);
            map.put(itemNames[i], itemValues[i]);
        }

        return map;
    }

    /**
     * <p>
     * Constructs a <tt>CompositeDataSupport</tt> instance with the specified <tt>compositeType</tt>, whose item names and corresponding values
     * are given by the mappings in the map <tt>items</tt>.
     * This constructor converts the keys to a string array and the values to an object array and calls
     * <tt>CompositeDataSupport(javax.management.openmbean.CompositeType, java.lang.String[], java.lang.Object[])</tt>.
     *
     * @param  compositeType  the <i>composite type </i> of this <i>composite data</i> instance;
     *                        must not be null.
     * @param  items  the mappings of all the item names to their values;
     *                <tt>items</tt> must contain all the item names defined in <tt>compositeType</tt>;
     *                must not be null or empty.
     *
     * @throws IllegalArgumentException <tt>compositeType</tt> is null, or
     * <tt>items</tt> is null or empty, or one of the keys in <tt>items</tt> is a null
     * or empty string.
     * @throws OpenDataException <tt>items</tt>' size differs from the
     * number of items defined in <tt>compositeType</tt>, or one of the
     * keys in <tt>items</tt> does not exist as an item name defined in
     * <tt>compositeType</tt>, or one of the values in <tt>items</tt>
     * is not a valid value for the corresponding item as defined in
     * <tt>compositeType</tt>.
     * @throws ArrayStoreException one or more keys in <tt>items</tt> is not of
     * the class <tt>java.lang.String</tt>.
     */
    public CompositeDataSupport(CompositeType compositeType,
                                Map<String,?> items)
            throws OpenDataException {
        this(makeMap(items), compositeType);
    }

    private static SortedMap<String, Object> makeMap(Map<String, ?> items) {
        if (items == null || items.isEmpty())
            throw new IllegalArgumentException("Null or empty items map");

        SortedMap<String, Object> map = new TreeMap<String, Object>();
        for (Object key : items.keySet()) {
            if (key == null || key.equals(""))
                throw new IllegalArgumentException("Null or empty item name");
            if (!(key instanceof String)) {
                throw new ArrayStoreException("Item name is not string: " + key);
                // This can happen because of erasure.  The particular
                // exception is a historical artifact - an implementation
                // detail that leaked into the API.
            }
            map.put((String) key, items.get(key));
        }
        return map;
    }

    private CompositeDataSupport(
            SortedMap<String, Object> items, CompositeType compositeType)
            throws OpenDataException {

        // Check compositeType is not null
        //
        if (compositeType == null) {
            throw new IllegalArgumentException("Argument compositeType cannot be null.");
        }

        // item names defined in compositeType:
        Set<String> namesFromType = compositeType.keySet();
        Set<String> namesFromItems = items.keySet();

        // This is just a comparison, but we do it this way for a better
        // exception message.
        if (!namesFromType.equals(namesFromItems)) {
            Set<String> extraFromType = new TreeSet<String>(namesFromType);
            extraFromType.removeAll(namesFromItems);
            Set<String> extraFromItems = new TreeSet<String>(namesFromItems);
            extraFromItems.removeAll(namesFromType);
            if (!extraFromType.isEmpty() || !extraFromItems.isEmpty()) {
                throw new OpenDataException(
                        "Item names do not match CompositeType: " +
                        "names in items but not in CompositeType: " + extraFromItems +
                        "; names in CompositeType but not in items: " + extraFromType);
            }
        }

        // Check each value, if not null, is of the open type defined for the
        // corresponding item
        for (String name : namesFromType) {
            Object value = items.get(name);
            if (value != null) {
                OpenType<?> itemType = compositeType.getType(name);
                if (!itemType.isValue(value)) {
                    throw new OpenDataException(
                            "Argument value of wrong type for item " + name +
                            ": value " + value + ", type " + itemType);
                }
            }
        }

        // Initialize internal fields: compositeType and contents
        //
        this.compositeType = compositeType;
        this.contents = items;
    }

    /**
     * Returns the <i>composite type </i> of this <i>composite data</i> instance.
     */
    public CompositeType getCompositeType() {

        return compositeType;
    }

    /**
     * Returns the value of the item whose name is <tt>key</tt>.
     *
     * @throws IllegalArgumentException  if <tt>key</tt> is a null or empty String.
     *
     * @throws InvalidKeyException  if <tt>key</tt> is not an existing item name for
     * this <tt>CompositeData</tt> instance.
     */
    public Object get(String key) {

        if ( (key == null) || (key.trim().equals("")) ) {
            throw new IllegalArgumentException("Argument key cannot be a null or empty String.");
        }
        if ( ! contents.containsKey(key.trim())) {
            throw new InvalidKeyException("Argument key=\""+ key.trim() +"\" is not an existing item name for this CompositeData instance.");
        }
        return contents.get(key.trim());
    }

    /**
     * Returns an array of the values of the items whose names are specified by
     * <tt>keys</tt>, in the same order as <tt>keys</tt>.
     *
     * @throws IllegalArgumentException  if an element in <tt>keys</tt> is a null
     * or empty String.
     *
     * @throws InvalidKeyException  if an element in <tt>keys</tt> is not an existing
     * item name for this <tt>CompositeData</tt> instance.
     */
    public Object[] getAll(String[] keys) {

        if ( (keys == null) || (keys.length == 0) ) {
            return new Object[0];
        }
        Object[] results = new Object[keys.length];
        for (int i=0; i<keys.length; i++) {
            results[i] = this.get(keys[i]);
        }
        return results;
    }

    /**
     * Returns <tt>true</tt> if and only if this <tt>CompositeData</tt> instance contains
     * an item whose name is <tt>key</tt>.
     * If <tt>key</tt> is a null or empty String, this method simply returns false.
     */
    public boolean containsKey(String key) {

        if ( (key == null) || (key.trim().equals("")) ) {
            return false;
        }
        return contents.containsKey(key);
    }

    /**
     * Returns <tt>true</tt> if and only if this <tt>CompositeData</tt> instance
     * contains an item
     * whose value is <tt>value</tt>.
     */
    public boolean containsValue(Object value) {

        return contents.containsValue(value);
    }

    /**
     * Returns an unmodifiable Collection view of the item values contained in this
     * <tt>CompositeData</tt> instance.
     * The returned collection's iterator will return the values in the ascending
     * lexicographic order of the corresponding
     * item names.
     */
    public Collection<?> values() {

        return Collections.unmodifiableCollection(contents.values());
    }

    /**
     * Compares the specified <var>obj</var> parameter with this
     * <code>CompositeDataSupport</code> instance for equality.
     * <p>
     * Returns <tt>true</tt> if and only if all of the following statements are true:
     * <ul>
     * <li><var>obj</var> is non null,</li>
     * <li><var>obj</var> also implements the <code>CompositeData</code> interface,</li>
     * <li>their composite types are equal</li>
     * <li>their contents, i.e. (name, value) pairs are equal. If a value contained in
     * the content is an array, the value comparison is done as if by calling
     * the {@link Arrays#deepEquals(Object[], Object[]) deepEquals} method
     * for arrays of object reference types or the appropriate overloading of
     * {@code Arrays.equals(e1,e2)} for arrays of primitive types</li>
     * </ul>
     * <p>
     * This ensures that this <tt>equals</tt> method works properly for
     * <var>obj</var> parameters which are different implementations of the
     * <code>CompositeData</code> interface, with the restrictions mentioned in the
     * {@link Collection#equals(Object) equals}
     * method of the <tt>java.util.Collection</tt> interface.
     *
     * @param  obj  the object to be compared for equality with this
     * <code>CompositeDataSupport</code> instance.
     * @return  <code>true</code> if the specified object is equal to this
     * <code>CompositeDataSupport</code> instance.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        // if obj is not a CompositeData, return false
        if (!(obj instanceof CompositeData)) {
            return false;
        }

        CompositeData other = (CompositeData) obj;

        // their compositeType should be equal
        if (!this.getCompositeType().equals(other.getCompositeType()) ) {
            return false;
        }

        if (contents.size() != other.values().size()) {
            return false;
        }

        for (Map.Entry<String,Object> entry : contents.entrySet()) {
            Object e1 = entry.getValue();
            Object e2 = other.get(entry.getKey());

            if (e1 == e2)
                continue;
            if (e1 == null)
                return false;

            boolean eq = e1.getClass().isArray() ?
                Arrays.deepEquals(new Object[] {e1}, new Object[] {e2}) :
                e1.equals(e2);

            if (!eq)
                return false;
        }

        // All tests for equality were successful
        //
        return true;
    }

    /**
     * Returns the hash code value for this <code>CompositeDataSupport</code> instance.
     * <p>
     * The hash code of a <code>CompositeDataSupport</code> instance is the sum of the hash codes
     * of all elements of information used in <code>equals</code> comparisons
     * (ie: its <i>composite type</i> and all the item values).
     * <p>
     * This ensures that <code> t1.equals(t2) </code> implies that <code> t1.hashCode()==t2.hashCode() </code>
     * for any two <code>CompositeDataSupport</code> instances <code>t1</code> and <code>t2</code>,
     * as required by the general contract of the method
     * {@link Object#hashCode() Object.hashCode()}.
     * <p>
     * Each item value's hash code is added to the returned hash code.
     * If an item value is an array,
     * its hash code is obtained as if by calling the
     * {@link Arrays#deepHashCode(Object[]) deepHashCode} method
     * for arrays of object reference types or the appropriate overloading
     * of {@code Arrays.hashCode(e)} for arrays of primitive types.
     *
     * @return the hash code value for this <code>CompositeDataSupport</code> instance
     */
    @Override
    public int hashCode() {
        int hashcode = compositeType.hashCode();

        for (Object o : contents.values()) {
            if (o instanceof Object[])
                hashcode += Arrays.deepHashCode((Object[]) o);
            else if (o instanceof byte[])
                hashcode += Arrays.hashCode((byte[]) o);
            else if (o instanceof short[])
                hashcode += Arrays.hashCode((short[]) o);
            else if (o instanceof int[])
                hashcode += Arrays.hashCode((int[]) o);
            else if (o instanceof long[])
                hashcode += Arrays.hashCode((long[]) o);
            else if (o instanceof char[])
                hashcode += Arrays.hashCode((char[]) o);
            else if (o instanceof float[])
                hashcode += Arrays.hashCode((float[]) o);
            else if (o instanceof double[])
                hashcode += Arrays.hashCode((double[]) o);
            else if (o instanceof boolean[])
                hashcode += Arrays.hashCode((boolean[]) o);
            else if (o != null)
                hashcode += o.hashCode();
        }

        return hashcode;
    }

    /**
     * Returns a string representation of this <code>CompositeDataSupport</code> instance.
     * <p>
     * The string representation consists of the name of this class (ie <code>javax.management.openmbean.CompositeDataSupport</code>),
     * the string representation of the composite type of this instance, and the string representation of the contents
     * (ie list the itemName=itemValue mappings).
     *
     * @return  a string representation of this <code>CompositeDataSupport</code> instance
     */
    @Override
    public String toString() {
        return new StringBuilder()
            .append(this.getClass().getName())
            .append("(compositeType=")
            .append(compositeType.toString())
            .append(",contents=")
            .append(contentString())
            .append(")")
            .toString();
    }

    private String contentString() {
        StringBuilder sb = new StringBuilder("{");
        String sep = "";
        for (Map.Entry<String, Object> entry : contents.entrySet()) {
            sb.append(sep).append(entry.getKey()).append("=");
            String s = Arrays.deepToString(new Object[] {entry.getValue()});
            sb.append(s.substring(1, s.length() - 1));
            sep = ", ";
        }
        sb.append("}");
        return sb.toString();
    }
}
