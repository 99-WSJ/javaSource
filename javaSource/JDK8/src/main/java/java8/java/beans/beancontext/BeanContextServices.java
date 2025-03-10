/*
 * Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
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

import java.beans.beancontext.BeanContext;
import java.beans.beancontext.BeanContextServicesListener;
import java.util.Iterator;
import java.util.TooManyListenersException;


/**
 * <p>
 * The BeanContextServices interface provides a mechanism for a BeanContext
 * to expose generic "services" to the BeanContextChild objects within.
 * </p>
 */
public interface BeanContextServices extends BeanContext, BeanContextServicesListener {

    /**
     * Adds a service to this BeanContext.
     * <code>BeanContextServiceProvider</code>s call this method
     * to register a particular service with this context.
     * If the service has not previously been added, the
     * <code>BeanContextServices</code> associates
     * the service with the <code>BeanContextServiceProvider</code> and
     * fires a <code>BeanContextServiceAvailableEvent</code> to all
     * currently registered <code>BeanContextServicesListeners</code>.
     * The method then returns <code>true</code>, indicating that
     * the addition of the service was successful.
     * If the given service has already been added, this method
     * simply returns <code>false</code>.
     * @param serviceClass     the service to add
     * @param serviceProvider  the <code>BeanContextServiceProvider</code>
     * associated with the service
     * @return true if the service was successful added, false otherwise
     */
    boolean addService(Class serviceClass, BeanContextServiceProvider serviceProvider);

    /**
     * BeanContextServiceProviders wishing to remove
     * a currently registered service from this context
     * may do so via invocation of this method. Upon revocation of
     * the service, the <code>BeanContextServices</code> fires a
     * <code>BeanContextServiceRevokedEvent</code> to its
     * list of currently registered
     * <code>BeanContextServiceRevokedListeners</code> and
     * <code>BeanContextServicesListeners</code>.
     * @param serviceClass the service to revoke from this BeanContextServices
     * @param serviceProvider the BeanContextServiceProvider associated with
     * this particular service that is being revoked
     * @param revokeCurrentServicesNow a value of <code>true</code>
     * indicates an exceptional circumstance where the
     * <code>BeanContextServiceProvider</code> or
     * <code>BeanContextServices</code> wishes to immediately
     * terminate service to all currently outstanding references
     * to the specified service.
     */
    void revokeService(Class serviceClass, BeanContextServiceProvider serviceProvider, boolean revokeCurrentServicesNow);

    /**
     * Reports whether or not a given service is
     * currently available from this context.
     * @param serviceClass the service in question
     * @return true if the service is available
     */
    boolean hasService(Class serviceClass);

    /**
     * A <code>BeanContextChild</code>, or any arbitrary object
     * associated with a <code>BeanContextChild</code>, may obtain
     * a reference to a currently registered service from its
     * nesting <code>BeanContextServices</code>
     * via invocation of this method. When invoked, this method
     * gets the service by calling the getService() method on the
     * underlying <code>BeanContextServiceProvider</code>.
     * @param child the <code>BeanContextChild</code>
     * associated with this request
     * @param requestor the object requesting the service
     * @param serviceClass class of the requested service
     * @param serviceSelector the service dependent parameter
     * @param bcsrl the
     * <code>BeanContextServiceRevokedListener</code> to notify
     * if the service should later become revoked
     * @throws TooManyListenersException if there are too many listeners
     * @return a reference to this context's named
     * Service as requested or <code>null</code>
     */
    Object getService(BeanContextChild child, Object requestor, Class serviceClass, Object serviceSelector, BeanContextServiceRevokedListener bcsrl) throws TooManyListenersException;

    /**
     * Releases a <code>BeanContextChild</code>'s
     * (or any arbitrary object associated with a BeanContextChild)
     * reference to the specified service by calling releaseService()
     * on the underlying <code>BeanContextServiceProvider</code>.
     * @param child the <code>BeanContextChild</code>
     * @param requestor the requestor
     * @param service the service
     */
    void releaseService(BeanContextChild child, Object requestor, Object service);

    /**
     * Gets the currently available services for this context.
     * @return an <code>Iterator</code> consisting of the
     * currently available services
     */
    Iterator getCurrentServiceClasses();

    /**
     * Gets the list of service dependent service parameters
     * (Service Selectors) for the specified service, by
     * calling getCurrentServiceSelectors() on the
     * underlying BeanContextServiceProvider.
     * @param serviceClass the specified service
     * @return the currently available service selectors
     * for the named serviceClass
     */
    Iterator getCurrentServiceSelectors(Class serviceClass);

    /**
     * Adds a <code>BeanContextServicesListener</code> to this BeanContext
     * @param bcsl the <code>BeanContextServicesListener</code> to add
     */
    void addBeanContextServicesListener(BeanContextServicesListener bcsl);

    /**
     * Removes a <code>BeanContextServicesListener</code>
     * from this <code>BeanContext</code>
     * @param bcsl the <code>BeanContextServicesListener</code>
     * to remove from this context
     */
    void removeBeanContextServicesListener(BeanContextServicesListener bcsl);
}
