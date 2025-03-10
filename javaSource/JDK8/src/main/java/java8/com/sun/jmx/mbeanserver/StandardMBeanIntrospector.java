/*
 * Copyright (c) 2005, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java8.com.sun.jmx.mbeanserver;

import java8.sun.jmx.mbeanserver.MBeanIntrospector;
import sun.reflect.misc.MethodUtil;

import javax.management.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.WeakHashMap;

/**
 * @since 1.6
 */
class StandardMBeanIntrospector extends MBeanIntrospector<Method> {
    private static final com.sun.jmx.mbeanserver.StandardMBeanIntrospector instance =
        new com.sun.jmx.mbeanserver.StandardMBeanIntrospector();

    static com.sun.jmx.mbeanserver.StandardMBeanIntrospector getInstance() {
        return instance;
    }

    @Override
    PerInterfaceMap<Method> getPerInterfaceMap() {
        return perInterfaceMap;
    }

    @Override
    MBeanInfoMap getMBeanInfoMap() {
        return mbeanInfoMap;
    }

    @Override
    MBeanAnalyzer<Method> getAnalyzer(Class<?> mbeanInterface)
            throws NotCompliantMBeanException {
        return MBeanAnalyzer.analyzer(mbeanInterface, this);
    }

    @Override
    boolean isMXBean() {
        return false;
    }

    @Override
    Method mFrom(Method m) {
        return m;
    }

    @Override
    String getName(Method m) {
        return m.getName();
    }

    @Override
    Type getGenericReturnType(Method m) {
        return m.getGenericReturnType();
    }

    @Override
    Type[] getGenericParameterTypes(Method m) {
        return m.getGenericParameterTypes();
    }

    @Override
    String[] getSignature(Method m) {
        Class<?>[] params = m.getParameterTypes();
        String[] sig = new String[params.length];
        for (int i = 0; i < params.length; i++)
            sig[i] = params[i].getName();
        return sig;
    }

    @Override
    void checkMethod(Method m) {
    }

    @Override
    Object invokeM2(Method m, Object target, Object[] args, Object cookie)
            throws InvocationTargetException, IllegalAccessException,
                   MBeanException {
        return MethodUtil.invoke(m, target, args);
    }

    @Override
    boolean validParameter(Method m, Object value, int paramNo, Object cookie) {
        return isValidParameter(m, value, paramNo);
    }

    @Override
    MBeanAttributeInfo getMBeanAttributeInfo(String attributeName,
            Method getter, Method setter) {

        final String description = "Attribute exposed for management";
        try {
            return new MBeanAttributeInfo(attributeName, description,
                                          getter, setter);
        } catch (IntrospectionException e) {
            throw new RuntimeException(e); // should not happen
        }
    }

    @Override
    MBeanOperationInfo getMBeanOperationInfo(String operationName,
            Method operation) {
        final String description = "Operation exposed for management";
        return new MBeanOperationInfo(description, operation);
    }

    @Override
    Descriptor getBasicMBeanDescriptor() {
        /* We don't bother saying mxbean=false, and we can't know whether
           the info is immutable until we know whether the MBean class
           (not interface) is a NotificationBroadcaster. */
        return ImmutableDescriptor.EMPTY_DESCRIPTOR;
    }

    @Override
    Descriptor getMBeanDescriptor(Class<?> resourceClass) {
        boolean immutable = isDefinitelyImmutableInfo(resourceClass);
        return new ImmutableDescriptor("mxbean=false",
                                       "immutableInfo=" + immutable);
    }

    /* Return true if and only if we can be sure that the given MBean implementation
     * class has immutable MBeanInfo.  A Standard MBean that is a
     * NotificationBroadcaster is allowed to return different values at
     * different times from its getNotificationInfo() method, which is when
     * we might not know if it is immutable.  But if it is a subclass of
     * NotificationBroadcasterSupport and does not override
     * getNotificationInfo(), then we know it won't change.
     */
    static boolean isDefinitelyImmutableInfo(Class<?> implClass) {
        if (!NotificationBroadcaster.class.isAssignableFrom(implClass))
            return true;
        synchronized (definitelyImmutable) {
            Boolean immutable = definitelyImmutable.get(implClass);
            if (immutable == null) {
                final Class<NotificationBroadcasterSupport> nbs =
                        NotificationBroadcasterSupport.class;
                if (nbs.isAssignableFrom(implClass)) {
                    try {
                        Method m = implClass.getMethod("getNotificationInfo");
                        immutable = (m.getDeclaringClass() == nbs);
                    } catch (Exception e) {
                        // Too bad, we'll say no for now.
                        return false;
                    }
                } else
                    immutable = false;
                definitelyImmutable.put(implClass, immutable);
            }
            return immutable;
        }
    }
    private static final WeakHashMap<Class<?>, Boolean> definitelyImmutable =
            new WeakHashMap<Class<?>, Boolean>();

    private static final PerInterfaceMap<Method>
        perInterfaceMap = new PerInterfaceMap<Method>();

    private static final MBeanInfoMap mbeanInfoMap = new MBeanInfoMap();
}
