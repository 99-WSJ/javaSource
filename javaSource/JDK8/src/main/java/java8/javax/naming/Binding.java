/*
 * Copyright (c) 1999, 2013, Oracle and/or its affiliates. All rights reserved.
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

import javax.naming.NameClassPair;

/**
  * This class represents a name-to-object binding found in a context.
  *<p>
  * A context consists of name-to-object bindings.
  * The Binding class represents such a binding.  It consists
  * of a name and an object. The <code>Context.listBindings()</code>
  * method returns an enumeration of Binding.
  *<p>
  * Use subclassing for naming systems that generate contents of
  * a binding dynamically.
  *<p>
  * A Binding instance is not synchronized against concurrent access by multiple
  * threads. Threads that need to access a Binding concurrently should
  * synchronize amongst themselves and provide the necessary locking.
  *
  * @author Rosanna Lee
  * @author Scott Seligman
  * @since 1.3
  */

public class Binding extends NameClassPair {
    /**
     * Contains this binding's object.
     * It is initialized by the constructor and can be updated using
     * <tt>setObject</tt>.
     * @serial
     * @see #getObject
     * @see #setObject
     */
    private Object boundObj;

    /**
      * Constructs an instance of a Binding given its name and object.
      *<p>
      * <tt>getClassName()</tt> will return
      * the class name of <tt>obj</tt> (or null if <tt>obj</tt> is null)
      * unless the class name has been explicitly set using <tt>setClassName()</tt>
      *
      * @param  name    The non-null name of the object. It is relative
      *             to the <em>target context</em> (which is
      * named by the first parameter of the <code>listBindings()</code> method)
      * @param  obj     The possibly null object bound to name.
      * @see NameClassPair#setClassName
      */
    public Binding(String name, Object obj) {
        super(name, null);
        this.boundObj = obj;
    }

    /**
      * Constructs an instance of a Binding given its name, object, and whether
      * the name is relative.
      *<p>
      * <tt>getClassName()</tt> will return the class name of <tt>obj</tt>
      * (or null if <tt>obj</tt> is null) unless the class name has been
      * explicitly set using <tt>setClassName()</tt>
      *
      * @param  name    The non-null string name of the object.
      * @param  obj     The possibly null object bound to name.
      * @param isRelative true if <code>name</code> is a name relative
      *         to the target context (which is named by
      *         the first parameter of the <code>listBindings()</code> method);
      *         false if <code>name</code> is a URL string.
      * @see NameClassPair#isRelative
      * @see NameClassPair#setRelative
      * @see NameClassPair#setClassName
      */
    public Binding(String name, Object obj, boolean isRelative) {
        super(name, null, isRelative);
        this.boundObj = obj;
    }

    /**
      * Constructs an instance of a Binding given its name, class name, and object.
      *
      * @param  name    The non-null name of the object. It is relative
      *             to the <em>target context</em> (which is
      * named by the first parameter of the <code>listBindings()</code> method)
      * @param  className       The possibly null class name of the object
      *         bound to <tt>name</tt>. If null, the class name of <tt>obj</tt> is
      *         returned by <tt>getClassName()</tt>. If <tt>obj</tt> is also
      *         null, <tt>getClassName()</tt> will return null.
      * @param  obj     The possibly null object bound to name.
      * @see NameClassPair#setClassName
      */
    public Binding(String name, String className, Object obj) {
        super(name, className);
        this.boundObj = obj;
    }

    /**
      * Constructs an instance of a Binding given its
      * name, class name, object, and whether the name is relative.
      *
      * @param  name    The non-null string name of the object.
      * @param  className       The possibly null class name of the object
      *         bound to <tt>name</tt>. If null, the class name of <tt>obj</tt> is
      *         returned by <tt>getClassName()</tt>. If <tt>obj</tt> is also
      *         null, <tt>getClassName()</tt> will return null.
      * @param  obj     The possibly null object bound to name.
      * @param isRelative true if <code>name</code> is a name relative
      *         to the target context (which is named by
      *         the first parameter of the <code>listBindings()</code> method);
      *         false if <code>name</code> is a URL string.
      * @see NameClassPair#isRelative
      * @see NameClassPair#setRelative
      * @see NameClassPair#setClassName
      */
    public Binding(String name, String className, Object obj, boolean isRelative) {
        super(name, className, isRelative);
        this.boundObj = obj;
    }

    /**
      * Retrieves the class name of the object bound to the name of this binding.
      * If the class name has been set explicitly, return it.
      * Otherwise, if this binding contains a non-null object,
      * that object's class name is used. Otherwise, null is returned.
      *
      * @return A possibly null string containing class name of object bound.
      */
    public String getClassName() {
        String cname = super.getClassName();
        if (cname != null) {
            return cname;
        }
        if (boundObj != null)
            return boundObj.getClass().getName();
        else
            return null;
    }

    /**
      * Retrieves the object bound to the name of this binding.
      *
      * @return The object bound; null if this binding does not contain an object.
      * @see #setObject
      */

    public Object getObject() {
        return boundObj;
    }

    /**
     * Sets the object associated with this binding.
     * @param obj The possibly null object to use.
     * @see #getObject
     */
    public void setObject(Object obj) {
        boundObj = obj;
    }

    /**
      * Generates the string representation of this binding.
      * The string representation consists of the string representation
      * of the name/class pair and the string representation of
      * this binding's object, separated by ':'.
      * The contents of this string is useful
      * for debugging and is not meant to be interpreted programmatically.
      *
      * @return The non-null string representation of this binding.
      */

    public String toString() {
        return super.toString() + ":" + getObject();
    }

    /**
     * Use serialVersionUID from JNDI 1.1.1 for interoperability
     */
    private static final long serialVersionUID = 8839217842691845890L;
};
