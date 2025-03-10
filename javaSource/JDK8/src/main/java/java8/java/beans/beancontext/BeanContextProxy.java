/*
 * Copyright (c) 1998, 2002, Oracle and/or its affiliates. All rights reserved.
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

package java8.java.beans.beancontext;

import java.beans.beancontext.BeanContextChild;

/**
 * <p>
 * This interface is implemented by a JavaBean that does
 * not directly have a BeanContext(Child) associated with
 * it (via implementing that interface or a subinterface thereof),
 * but has a public BeanContext(Child) delegated from it.
 * For example, a subclass of java.awt.Container may have a BeanContext
 * associated with it that all Component children of that Container shall
 * be contained within.
 * </p>
 * <p>
 * An Object may not implement this interface and the
 * BeanContextChild interface
 * (or any subinterfaces thereof) they are mutually exclusive.
 * </p>
 * <p>
 * Callers of this interface shall examine the return type in order to
 * obtain a particular subinterface of BeanContextChild as follows:
 * <code>
 * BeanContextChild bcc = o.getBeanContextProxy();
 *
 * if (bcc instanceof BeanContext) {
 *      // ...
 * }
 * </code>
 * or
 * <code>
 * BeanContextChild bcc = o.getBeanContextProxy();
 * BeanContext      bc  = null;
 *
 * try {
 *     bc = (BeanContext)bcc;
 * } catch (ClassCastException cce) {
 *     // cast failed, bcc is not an instanceof BeanContext
 * }
 * </code>
 * </p>
 * <p>
 * The return value is a constant for the lifetime of the implementing
 * instance
 * </p>
 * @author Laurence P. G. Cable
 * @since 1.2
 *
 * @see BeanContextChild
 * @see java.beans.beancontext.BeanContextChildSupport
 */

public interface BeanContextProxy {

    /**
     * Gets the <code>BeanContextChild</code> (or subinterface)
     * associated with this object.
     * @return the <code>BeanContextChild</code> (or subinterface)
     * associated with this object
     */
    BeanContextChild getBeanContextProxy();
}
