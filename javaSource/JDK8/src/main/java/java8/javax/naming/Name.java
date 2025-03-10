/*
 * Copyright (c) 1999, 2004, Oracle and/or its affiliates. All rights reserved.
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

package java8.javax.naming;

import javax.naming.InvalidNameException;
import java.util.Enumeration;

/**
 * The <tt>Name</tt> interface represents a generic name -- an ordered
 * sequence of components.  It can be a composite name (names that
 * span multiple namespaces), or a compound name (names that are
 * used within individual hierarchical naming systems).
 *
 * <p> There can be different implementations of <tt>Name</tt>; for example,
 * composite names, URLs, or namespace-specific compound names.
 *
 * <p> The components of a name are numbered.  The indexes of a name
 * with N components range from 0 up to, but not including, N.  This
 * range may be written as [0,N).
 * The most significant component is at index 0.
 * An empty name has no components.
 *
 * <p> None of the methods in this interface accept null as a valid
 * value for a parameter that is a name or a name component.
 * Likewise, methods that return a name or name component never return null.
 *
 * <p> An instance of a <tt>Name</tt> may not be synchronized against
 * concurrent multithreaded access if that access is not read-only.
 *
 * @author Rosanna Lee
 * @author Scott Seligman
 * @author R. Vasudevan
 * @since 1.3
 */

public interface Name
    extends Cloneable, java.io.Serializable, Comparable<Object>
{

   /**
    * The class fingerprint that is set to indicate
    * serialization compatibility with a previous
    * version of the class.
    */
    static final long serialVersionUID = -3617482732056931635L;

    /**
     * Generates a new copy of this name.
     * Subsequent changes to the components of this name will not
     * affect the new copy, and vice versa.
     *
     * @return  a copy of this name
     *
     * @see Object#clone()
     */
    public Object clone();

    /**
     * Compares this name with another name for order.
     * Returns a negative integer, zero, or a positive integer as this
     * name is less than, equal to, or greater than the given name.
     *
     * <p> As with <tt>Object.equals()</tt>, the notion of ordering for names
     * depends on the class that implements this interface.
     * For example, the ordering may be
     * based on lexicographical ordering of the name components.
     * Specific attributes of the name, such as how it treats case,
     * may affect the ordering.  In general, two names of different
     * classes may not be compared.
     *
     * @param   obj the non-null object to compare against.
     * @return  a negative integer, zero, or a positive integer as this name
     *          is less than, equal to, or greater than the given name
     * @throws  ClassCastException if obj is not a <tt>Name</tt> of a
     *          type that may be compared with this name
     *
     * @see Comparable#compareTo(Object)
     */
    public int compareTo(Object obj);

    /**
     * Returns the number of components in this name.
     *
     * @return  the number of components in this name
     */
    public int size();

    /**
     * Determines whether this name is empty.
     * An empty name is one with zero components.
     *
     * @return  true if this name is empty, false otherwise
     */
    public boolean isEmpty();

    /**
     * Retrieves the components of this name as an enumeration
     * of strings.  The effect on the enumeration of updates to
     * this name is undefined.  If the name has zero components,
     * an empty (non-null) enumeration is returned.
     *
     * @return  an enumeration of the components of this name, each a string
     */
    public Enumeration<String> getAll();

    /**
     * Retrieves a component of this name.
     *
     * @param posn
     *          the 0-based index of the component to retrieve.
     *          Must be in the range [0,size()).
     * @return  the component at index posn
     * @throws  ArrayIndexOutOfBoundsException
     *          if posn is outside the specified range
     */
    public String get(int posn);

    /**
     * Creates a name whose components consist of a prefix of the
     * components of this name.  Subsequent changes to
     * this name will not affect the name that is returned and vice versa.
     *
     * @param posn
     *          the 0-based index of the component at which to stop.
     *          Must be in the range [0,size()].
     * @return  a name consisting of the components at indexes in
     *          the range [0,posn).
     * @throws  ArrayIndexOutOfBoundsException
     *          if posn is outside the specified range
     */
    public javax.naming.Name getPrefix(int posn);

    /**
     * Creates a name whose components consist of a suffix of the
     * components in this name.  Subsequent changes to
     * this name do not affect the name that is returned and vice versa.
     *
     * @param posn
     *          the 0-based index of the component at which to start.
     *          Must be in the range [0,size()].
     * @return  a name consisting of the components at indexes in
     *          the range [posn,size()).  If posn is equal to
     *          size(), an empty name is returned.
     * @throws  ArrayIndexOutOfBoundsException
     *          if posn is outside the specified range
     */
    public javax.naming.Name getSuffix(int posn);

    /**
     * Determines whether this name starts with a specified prefix.
     * A name <tt>n</tt> is a prefix if it is equal to
     * <tt>getPrefix(n.size())</tt>.
     *
     * @param n
     *          the name to check
     * @return  true if <tt>n</tt> is a prefix of this name, false otherwise
     */
    public boolean startsWith(javax.naming.Name n);

    /**
     * Determines whether this name ends with a specified suffix.
     * A name <tt>n</tt> is a suffix if it is equal to
     * <tt>getSuffix(size()-n.size())</tt>.
     *
     * @param n
     *          the name to check
     * @return  true if <tt>n</tt> is a suffix of this name, false otherwise
     */
    public boolean endsWith(javax.naming.Name n);

    /**
     * Adds the components of a name -- in order -- to the end of this name.
     *
     * @param suffix
     *          the components to add
     * @return  the updated name (not a new one)
     *
     * @throws  InvalidNameException if <tt>suffix</tt> is not a valid name,
     *          or if the addition of the components would violate the syntax
     *          rules of this name
     */
    public javax.naming.Name addAll(javax.naming.Name suffix) throws InvalidNameException;

    /**
     * Adds the components of a name -- in order -- at a specified position
     * within this name.
     * Components of this name at or after the index of the first new
     * component are shifted up (away from 0) to accommodate the new
     * components.
     *
     * @param n
     *          the components to add
     * @param posn
     *          the index in this name at which to add the new
     *          components.  Must be in the range [0,size()].
     * @return  the updated name (not a new one)
     *
     * @throws  ArrayIndexOutOfBoundsException
     *          if posn is outside the specified range
     * @throws  InvalidNameException if <tt>n</tt> is not a valid name,
     *          or if the addition of the components would violate the syntax
     *          rules of this name
     */
    public javax.naming.Name addAll(int posn, javax.naming.Name n) throws InvalidNameException;

    /**
     * Adds a single component to the end of this name.
     *
     * @param comp
     *          the component to add
     * @return  the updated name (not a new one)
     *
     * @throws  InvalidNameException if adding <tt>comp</tt> would violate
     *          the syntax rules of this name
     */
    public javax.naming.Name add(String comp) throws InvalidNameException;

    /**
     * Adds a single component at a specified position within this name.
     * Components of this name at or after the index of the new component
     * are shifted up by one (away from index 0) to accommodate the new
     * component.
     *
     * @param comp
     *          the component to add
     * @param posn
     *          the index at which to add the new component.
     *          Must be in the range [0,size()].
     * @return  the updated name (not a new one)
     *
     * @throws  ArrayIndexOutOfBoundsException
     *          if posn is outside the specified range
     * @throws  InvalidNameException if adding <tt>comp</tt> would violate
     *          the syntax rules of this name
     */
    public javax.naming.Name add(int posn, String comp) throws InvalidNameException;

    /**
     * Removes a component from this name.
     * The component of this name at the specified position is removed.
     * Components with indexes greater than this position
     * are shifted down (toward index 0) by one.
     *
     * @param posn
     *          the index of the component to remove.
     *          Must be in the range [0,size()).
     * @return  the component removed (a String)
     *
     * @throws  ArrayIndexOutOfBoundsException
     *          if posn is outside the specified range
     * @throws  InvalidNameException if deleting the component
     *          would violate the syntax rules of the name
     */
    public Object remove(int posn) throws InvalidNameException;
}
