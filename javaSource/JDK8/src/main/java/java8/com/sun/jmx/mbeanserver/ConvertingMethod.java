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

import com.sun.jmx.mbeanserver.MXBeanMappingFactory;
import java8.sun.jmx.mbeanserver.DefaultMXBeanMappingFactory;
import java8.sun.jmx.mbeanserver.MXBeanMapping;
import sun.reflect.misc.MethodUtil;

import javax.management.Descriptor;
import javax.management.MBeanException;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import java.io.InvalidObjectException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

final class ConvertingMethod {
    static com.sun.jmx.mbeanserver.ConvertingMethod from(Method m) {
        try {
            return new com.sun.jmx.mbeanserver.ConvertingMethod(m);
        } catch (OpenDataException ode) {
            final String msg = "Method " + m.getDeclaringClass().getName() +
                "." + m.getName() + " has parameter or return type that " +
                "cannot be translated into an open type";
            throw new IllegalArgumentException(msg, ode);
        }
    }

    Method getMethod() {
        return method;
    }

    Descriptor getDescriptor() {
        return Introspector.descriptorForElement(method);
    }

    Type getGenericReturnType() {
        return method.getGenericReturnType();
    }

    Type[] getGenericParameterTypes() {
        return method.getGenericParameterTypes();
    }

    String getName() {
        return method.getName();
    }

    OpenType<?> getOpenReturnType() {
        return returnMapping.getOpenType();
    }

    OpenType<?>[] getOpenParameterTypes() {
        final OpenType<?>[] types = new OpenType<?>[paramMappings.length];
        for (int i = 0; i < paramMappings.length; i++)
            types[i] = paramMappings[i].getOpenType();
        return types;
    }

    /* Check that this method will be callable when we are going from
     * open types to Java types, for example when we are going from
     * an MXBean wrapper to the underlying resource.
     * The parameters will be converted to
     * Java types, so they must be "reconstructible".  The return
     * value will be converted to an Open Type, so if it is convertible
     * at all there is no further check needed.
     */
    void checkCallFromOpen() {
        try {
            for (MXBeanMapping paramConverter : paramMappings)
                paramConverter.checkReconstructible();
        } catch (InvalidObjectException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /* Check that this method will be callable when we are going from
     * Java types to open types, for example when we are going from
     * an MXBean proxy to the open types that it will be mapped to.
     * The return type will be converted back to a Java type, so it
     * must be "reconstructible".  The parameters will be converted to
     * open types, so if it is convertible at all there is no further
     * check needed.
     */
    void checkCallToOpen() {
        try {
            returnMapping.checkReconstructible();
        } catch (InvalidObjectException e) {
            throw new IllegalArgumentException(e);
        }
    }

    String[] getOpenSignature() {
        if (paramMappings.length == 0)
            return noStrings;

        String[] sig = new String[paramMappings.length];
        for (int i = 0; i < paramMappings.length; i++)
            sig[i] = paramMappings[i].getOpenClass().getName();
        return sig;
    }

    final Object toOpenReturnValue(MXBeanLookup lookup, Object ret)
            throws OpenDataException {
        return returnMapping.toOpenValue(ret);
    }

    final Object fromOpenReturnValue(MXBeanLookup lookup, Object ret)
            throws InvalidObjectException {
        return returnMapping.fromOpenValue(ret);
    }

    final Object[] toOpenParameters(MXBeanLookup lookup, Object[] params)
            throws OpenDataException {
        if (paramConversionIsIdentity || params == null)
            return params;
        final Object[] oparams = new Object[params.length];
        for (int i = 0; i < params.length; i++)
            oparams[i] = paramMappings[i].toOpenValue(params[i]);
        return oparams;
    }

    final Object[] fromOpenParameters(Object[] params)
            throws InvalidObjectException {
        if (paramConversionIsIdentity || params == null)
            return params;
        final Object[] jparams = new Object[params.length];
        for (int i = 0; i < params.length; i++)
            jparams[i] = paramMappings[i].fromOpenValue(params[i]);
        return jparams;
    }

    final Object toOpenParameter(MXBeanLookup lookup,
                                 Object param,
                                 int paramNo)
        throws OpenDataException {
        return paramMappings[paramNo].toOpenValue(param);
    }

    final Object fromOpenParameter(MXBeanLookup lookup,
                                   Object param,
                                   int paramNo)
        throws InvalidObjectException {
        return paramMappings[paramNo].fromOpenValue(param);
    }

    Object invokeWithOpenReturn(MXBeanLookup lookup,
                                Object obj, Object[] params)
            throws MBeanException, IllegalAccessException,
                   InvocationTargetException {
        MXBeanLookup old = MXBeanLookup.getLookup();
        try {
            MXBeanLookup.setLookup(lookup);
            return invokeWithOpenReturn(obj, params);
        } finally {
            MXBeanLookup.setLookup(old);
        }
    }

    private Object invokeWithOpenReturn(Object obj, Object[] params)
            throws MBeanException, IllegalAccessException,
                   InvocationTargetException {
        final Object[] javaParams;
        try {
            javaParams = fromOpenParameters(params);
        } catch (InvalidObjectException e) {
            // probably can't happen
            final String msg = methodName() + ": cannot convert parameters " +
                "from open values: " + e;
            throw new MBeanException(e, msg);
        }
        final Object javaReturn = MethodUtil.invoke(method, obj, javaParams);
        try {
            return returnMapping.toOpenValue(javaReturn);
        } catch (OpenDataException e) {
            // probably can't happen
            final String msg = methodName() + ": cannot convert return " +
                "value to open value: " + e;
            throw new MBeanException(e, msg);
        }
    }

    private String methodName() {
        return method.getDeclaringClass() + "." + method.getName();
    }

    private ConvertingMethod(Method m) throws OpenDataException {
        this.method = m;
        MXBeanMappingFactory mappingFactory = MXBeanMappingFactory.DEFAULT;
        returnMapping =
                mappingFactory.mappingForType(m.getGenericReturnType(), mappingFactory);
        Type[] params = m.getGenericParameterTypes();
        paramMappings = new MXBeanMapping[params.length];
        boolean identity = true;
        for (int i = 0; i < params.length; i++) {
            paramMappings[i] = mappingFactory.mappingForType(params[i], mappingFactory);
            identity &= DefaultMXBeanMappingFactory.isIdentity(paramMappings[i]);
        }
        paramConversionIsIdentity = identity;
    }

    private static final String[] noStrings = new String[0];

    private final Method method;
    private final MXBeanMapping returnMapping;
    private final MXBeanMapping[] paramMappings;
    private final boolean paramConversionIsIdentity;
}
