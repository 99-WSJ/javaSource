/*
 * Copyright (c) 1999, 2003, Oracle and/or its affiliates. All rights reserved.
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

import javax.naming.Binding;
import javax.naming.Context;

/**
 * This class represents the object name and class name pair of a binding
 * found in a context.
 *<p>
 * A context consists of name-to-object bindings.
 * The NameClassPair class represents the name and the
 * class of the bound object. It consists
 * of a name and a string representing the
 * package-qualified class name.
 *<p>
 * Use subclassing for naming systems that generate contents of
 * a name/class pair dynamically.
 *<p>
 * A NameClassPair instance is not synchronized against concurrent
 * access by multiple threads. Threads that need to access a NameClassPair
 * concurrently should synchronize amongst themselves and provide
 * the necessary locking.
 *
 * @author Rosanna Lee
 * @author Scott Seligman
 *
 * @see Context#list
 * @since 1.3
 */

 /*
  * <p>
  * The serialized form of a NameClassPair object consists of the name (a
  * String), class name (a String), and isRelative flag (a boolean).
  */

public class NameClassPair implements java.io.Serializable {
    /**
     * Contains the name of this NameClassPair.
     * It is initialized by the constructor and can be updated using
     * <tt>setName()</tt>.
     * @serial
     * @see #getName
     * @see #setName
     */
    private String name;

    /**
     *Contains the class name contained in this NameClassPair.
     * It is initialized by the constructor and can be updated using
     * <tt>setClassName()</tt>.
     * @serial
     * @see #getClassName
     * @see #setClassName
     */
    private String className;

    /**
     * Contains the full name of this NameClassPair within its
     * own namespace.
     * It is initialized using <tt>setNameInNamespace()</tt>
     * @serial
     * @see #getNameInNamespace
     * @see #setNameInNamespace
     */
    private String fullName = null;


    /**
     * Records whether the name of this <tt>NameClassPair</tt>
     * is relative to the target context.
     * It is initialized by the constructor and can be updated using
     * <tt>setRelative()</tt>.
     * @serial
     * @see #isRelative
     * @see #setRelative
     * @see #getName
     * @see #setName
     */
    private boolean isRel = true;

    /**
     * Constructs an instance of a NameClassPair given its
     * name and class name.
     *
     * @param   name    The non-null name of the object. It is relative
     *                  to the <em>target context</em> (which is
     * named by the first parameter of the <code>list()</code> method)
     * @param   className       The possibly null class name of the object
     *          bound to name. It is null if the object bound is null.
     * @see #getClassName
     * @see #setClassName
     * @see #getName
     * @see #setName
     */
    public NameClassPair(String name, String className) {
        this.name = name;
        this.className = className;
    }

    /**
     * Constructs an instance of a NameClassPair given its
     * name, class name, and whether it is relative to the listing context.
     *
     * @param   name    The non-null name of the object.
     * @param   className       The possibly null class name of the object
     *  bound to name.  It is null if the object bound is null.
     * @param isRelative true if <code>name</code> is a name relative
     *          to the target context (which is named by the first parameter
     *          of the <code>list()</code> method); false if <code>name</code>
     *          is a URL string.
     * @see #getClassName
     * @see #setClassName
     * @see #getName
     * @see #setName
     * @see #isRelative
     * @see #setRelative
     */
    public NameClassPair(String name, String className, boolean isRelative) {
        this.name = name;
        this.className = className;
        this.isRel = isRelative;
    }

    /**
     * Retrieves the class name of the object bound to the name of this binding.
     * If a reference or some other indirect information is bound,
     * retrieves the class name of the eventual object that
     * will be returned by <tt>Binding.getObject()</tt>.
     *
     * @return  The possibly null class name of object bound.
     *          It is null if the object bound is null.
     * @see Binding#getObject
     * @see Binding#getClassName
     * @see #setClassName
     */
    public String getClassName() {
        return className;
    }

    /**
     * Retrieves the name of this binding.
     * If <tt>isRelative()</tt> is true, this name is relative to the
     * target context (which is named by the first parameter of the
     * <tt>list()</tt>).
     * If <tt>isRelative()</tt> is false, this name is a URL string.
     *
     * @return  The non-null name of this binding.
     * @see #isRelative
     * @see #setName
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this binding.
     *
     * @param   name the non-null string to use as the name.
     * @see #getName
     * @see #setRelative
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the class name of this binding.
     *
     * @param   name the possibly null string to use as the class name.
     * If null, <tt>Binding.getClassName()</tt> will return
     * the actual class name of the object in the binding.
     * The class name will be null if the object bound is null.
     * @see #getClassName
     * @see Binding#getClassName
     */
    public void setClassName(String name) {
        this.className = name;
    }

    /**
     * Determines whether the name of this binding is
     * relative to the target context (which is named by
     * the first parameter of the <code>list()</code> method).
     *
     * @return true if the name of this binding is relative to the
     *          target context;
     *          false if the name of this binding is a URL string.
     * @see #setRelative
     * @see #getName
     */
    public boolean isRelative() {
        return isRel;
    }

    /**
     * Sets whether the name of this binding is relative to the target
     * context (which is named by the first parameter of the <code>list()</code>
     * method).
     *
     * @param r If true, the name of binding is relative to the target context;
     *          if false, the name of binding is a URL string.
     * @see #isRelative
     * @see #setName
     */
    public void setRelative(boolean r) {
        isRel = r;
    }

    /**
     * Retrieves the full name of this binding.
     * The full name is the absolute name of this binding within
     * its own namespace. See {@link Context#getNameInNamespace()}.
     * <p>
     *
     * In naming systems for which the notion of full name does not
     * apply to this binding an <tt>UnsupportedOperationException</tt>
     * is thrown.
     * This exception is also thrown when a service provider written before
     * the introduction of the method is in use.
     * <p>
     * The string returned by this method is not a JNDI composite name and
     * should not be passed directly to context methods.
     *
     * @return The full name of this binding.
     * @throws UnsupportedOperationException if the notion of full name
     *         does not apply to this binding in the naming system.
     * @since 1.5
     * @see #setNameInNamespace
     * @see #getName
     */
    public String getNameInNamespace() {
        if (fullName == null) {
            throw new UnsupportedOperationException();
        }
        return fullName;
    }

    /**
     * Sets the full name of this binding.
     * This method must be called to set the full name whenever a
     * <tt>NameClassPair</tt> is created and a full name is
     * applicable to this binding.
     * <p>
     * Setting the full name to null, or not setting it at all, will
     * cause <tt>getNameInNamespace()</tt> to throw an exception.
     *
     * @param fullName The full name to use.
     * @since 1.5
     * @see #getNameInNamespace
     * @see #setName
     */
    public void setNameInNamespace(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Generates the string representation of this name/class pair.
     * The string representation consists of the name and class name separated
     * by a colon (':').
     * The contents of this string is useful
     * for debugging and is not meant to be interpreted programmatically.
     *
     * @return The string representation of this name/class pair.
     */
    public String toString() {
        return (isRelative() ? "" : "(not relative)") + getName() + ": " +
                getClassName();
    }


    /**
     * Use serialVersionUID from JNDI 1.1.1 for interoperability
     */
    private static final long serialVersionUID = 5620776610160863339L;
}
