/*
 * Copyright (c) 1998, 2004, Oracle and/or its affiliates. All rights reserved.
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

import java.beans.beancontext.BeanContextServices;
import java.util.Iterator;

/**
 * <p>
 * One of the primary functions of a BeanContext is to act a as rendezvous
 * between JavaBeans, and BeanContextServiceProviders.
 * </p>
 * <p>
 * A JavaBean nested within a BeanContext, may ask that BeanContext to
 * provide an instance of a "service", based upon a reference to a Java
 * Class object that represents that service.
 * </p>
 * <p>
 * If such a service has been registered with the context, or one of its
 * nesting context's, in the case where a context delegate to its context
 * to satisfy a service request, then the BeanContextServiceProvider associated with
 * the service is asked to provide an instance of that service.
 * </p>
 * <p>
 * The ServcieProvider may always return the same instance, or it may
 * construct a new instance for each request.
 * </p>
 */

public interface BeanContextServiceProvider {

   /**
    * Invoked by <code>BeanContextServices</code>, this method
    * requests an instance of a
    * service from this <code>BeanContextServiceProvider</code>.
    *
    * @param bcs The <code>BeanContextServices</code> associated with this
    * particular request. This parameter enables the
    * <code>BeanContextServiceProvider</code> to distinguish service
    * requests from multiple sources.
    *
    * @param requestor          The object requesting the service
    *
    * @param serviceClass       The service requested
    *
    * @param serviceSelector the service dependent parameter
    * for a particular service, or <code>null</code> if not applicable.
    *
    * @return a reference to the requested service
    */
    Object getService(BeanContextServices bcs, Object requestor, Class serviceClass, Object serviceSelector);

    /**
     * Invoked by <code>BeanContextServices</code>,
     * this method releases a nested <code>BeanContextChild</code>'s
     * (or any arbitrary object associated with a
     * <code>BeanContextChild</code>) reference to the specified service.
     *
     * @param bcs the <code>BeanContextServices</code> associated with this
     * particular release request
     *
     * @param requestor the object requesting the service to be released
     *
     * @param service the service that is to be released
     */
    public void releaseService(BeanContextServices bcs, Object requestor, Object service);

    /**
     * Invoked by <code>BeanContextServices</code>, this method
     * gets the current service selectors for the specified service.
     * A service selector is a service specific parameter,
     * typical examples of which could include: a
     * parameter to a constructor for the service implementation class,
     * a value for a particular service's property, or a key into a
     * map of existing implementations.
     *
     * @param bcs           the <code>BeanContextServices</code> for this request
     * @param serviceClass  the specified service
     * @return   the current service selectors for the specified serviceClass
     */
    Iterator getCurrentServiceSelectors(BeanContextServices bcs, Class serviceClass);
}
