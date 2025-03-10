/*
 * Copyright (c) 1996, 2005, Oracle and/or its affiliates. All rights reserved.
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
 * (C) Copyright Taligent, Inc. 1996, 1997 - All Rights Reserved
 * (C) Copyright IBM Corp. 1996 - 1998 - All Rights Reserved
 *
 * The original version of this source code and documentation
 * is copyrighted and owned by Taligent, Inc., a wholly-owned
 * subsidiary of IBM. These materials are provided under terms
 * of a License Agreement between Taligent and Sun. This technology
 * is protected by multiple US and International patents.
 *
 * This notice and attribution to Taligent may not be removed.
 * Taligent is a registered trademark of Taligent, Inc.
 *
 */

package java8.java.util;

import java.util.ResourceBundle;

/**
 * Signals that a resource is missing.
 * @see Exception
 * @see ResourceBundle
 * @author      Mark Davis
 * @since       JDK1.1
 */
public
class MissingResourceException extends RuntimeException {

    /**
     * Constructs a MissingResourceException with the specified information.
     * A detail message is a String that describes this particular exception.
     * @param s the detail message
     * @param className the name of the resource class
     * @param key the key for the missing resource.
     */
    public MissingResourceException(String s, String className, String key) {
        super(s);
        this.className = className;
        this.key = key;
    }

    /**
     * Constructs a <code>MissingResourceException</code> with
     * <code>message</code>, <code>className</code>, <code>key</code>,
     * and <code>cause</code>. This constructor is package private for
     * use by <code>ResourceBundle.getBundle</code>.
     *
     * @param message
     *        the detail message
     * @param className
     *        the name of the resource class
     * @param key
     *        the key for the missing resource.
     * @param cause
     *        the cause (which is saved for later retrieval by the
     *        {@link Throwable.getCause()} method). (A null value is
     *        permitted, and indicates that the cause is nonexistent
     *        or unknown.)
     */
    MissingResourceException(String message, String className, String key, Throwable cause) {
        super(message, cause);
        this.className = className;
        this.key = key;
    }

    /**
     * Gets parameter passed by constructor.
     *
     * @return the name of the resource class
     */
    public String getClassName() {
        return className;
    }

    /**
     * Gets parameter passed by constructor.
     *
     * @return the key for the missing resource
     */
    public String getKey() {
        return key;
    }

    //============ privates ============

    // serialization compatibility with JDK1.1
    private static final long serialVersionUID = -4876345176062000401L;

    /**
     * The class name of the resource bundle requested by the user.
     * @serial
     */
    private String className;

    /**
     * The name of the specific resource requested by the user.
     * @serial
     */
    private String key;
}
