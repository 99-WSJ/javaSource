/*
 * Copyright (c) 2005, 2008, Oracle and/or its affiliates. All rights reserved.
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

package java8.sun.jmx.mbeanserver;

import com.sun.jmx.mbeanserver.MXBeanLookup;

import javax.management.Attribute;
import javax.management.MBeanServerConnection;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

import static com.sun.jmx.mbeanserver.Util.newMap;

/**
   <p>Helper class for an {@link InvocationHandler} that forwards methods from an
   MXBean interface to a named
   MXBean in an MBean Server and handles translation between the
   arbitrary Java types in the interface and the Open Types used
   by the MXBean.</p>

   @since 1.6
*/
public class MXBeanProxy {
    public MXBeanProxy(Class<?> mxbeanInterface) {

        if (mxbeanInterface == null)
            throw new IllegalArgumentException("Null parameter");

        final com.sun.jmx.mbeanserver.MBeanAnalyzer<com.sun.jmx.mbeanserver.ConvertingMethod> analyzer;
        try {
            analyzer =
                com.sun.jmx.mbeanserver.MXBeanIntrospector.getInstance().getAnalyzer(mxbeanInterface);
        } catch (NotCompliantMBeanException e) {
            throw new IllegalArgumentException(e);
        }
        analyzer.visit(new Visitor());
    }

    private class Visitor
            implements com.sun.jmx.mbeanserver.MBeanAnalyzer.MBeanVisitor<com.sun.jmx.mbeanserver.ConvertingMethod> {
        public void visitAttribute(String attributeName,
                                   com.sun.jmx.mbeanserver.ConvertingMethod getter,
                                   com.sun.jmx.mbeanserver.ConvertingMethod setter) {
            if (getter != null) {
                getter.checkCallToOpen();
                Method getterMethod = getter.getMethod();
                handlerMap.put(getterMethod,
                               new GetHandler(attributeName, getter));
            }
            if (setter != null) {
                // return type is void, no need for checkCallToOpen
                Method setterMethod = setter.getMethod();
                handlerMap.put(setterMethod,
                               new SetHandler(attributeName, setter));
            }
        }

        public void visitOperation(String operationName,
                                   com.sun.jmx.mbeanserver.ConvertingMethod operation) {
            operation.checkCallToOpen();
            Method operationMethod = operation.getMethod();
            String[] sig = operation.getOpenSignature();
            handlerMap.put(operationMethod,
                           new InvokeHandler(operationName, sig, operation));
        }
    }

    private static abstract class Handler {
        Handler(String name, com.sun.jmx.mbeanserver.ConvertingMethod cm) {
            this.name = name;
            this.convertingMethod = cm;
        }

        String getName() {
            return name;
        }

        com.sun.jmx.mbeanserver.ConvertingMethod getConvertingMethod() {
            return convertingMethod;
        }

        abstract Object invoke(MBeanServerConnection mbsc,
                               ObjectName name, Object[] args) throws Exception;

        private final String name;
        private final com.sun.jmx.mbeanserver.ConvertingMethod convertingMethod;
    }

    private static class GetHandler extends Handler {
        GetHandler(String attributeName, com.sun.jmx.mbeanserver.ConvertingMethod cm) {
            super(attributeName, cm);
        }

        @Override
        Object invoke(MBeanServerConnection mbsc, ObjectName name, Object[] args)
                throws Exception {
            assert(args == null || args.length == 0);
            return mbsc.getAttribute(name, getName());
        }
    }

    private static class SetHandler extends Handler {
        SetHandler(String attributeName, com.sun.jmx.mbeanserver.ConvertingMethod cm) {
            super(attributeName, cm);
        }

        @Override
        Object invoke(MBeanServerConnection mbsc, ObjectName name, Object[] args)
                throws Exception {
            assert(args.length == 1);
            Attribute attr = new Attribute(getName(), args[0]);
            mbsc.setAttribute(name, attr);
            return null;
        }
    }

    private static class InvokeHandler extends Handler {
        InvokeHandler(String operationName, String[] signature,
                      com.sun.jmx.mbeanserver.ConvertingMethod cm) {
            super(operationName, cm);
            this.signature = signature;
        }

        Object invoke(MBeanServerConnection mbsc, ObjectName name, Object[] args)
                throws Exception {
            return mbsc.invoke(name, getName(), args, signature);
        }

        private final String[] signature;
    }

    public Object invoke(MBeanServerConnection mbsc, ObjectName name,
                         Method method, Object[] args)
            throws Throwable {

        Handler handler = handlerMap.get(method);
        com.sun.jmx.mbeanserver.ConvertingMethod cm = handler.getConvertingMethod();
        MXBeanLookup lookup = MXBeanLookup.lookupFor(mbsc);
        MXBeanLookup oldLookup = MXBeanLookup.getLookup();
        try {
            MXBeanLookup.setLookup(lookup);
            Object[] openArgs = cm.toOpenParameters(lookup, args);
            Object result = handler.invoke(mbsc, name, openArgs);
            return cm.fromOpenReturnValue(lookup, result);
        } finally {
            MXBeanLookup.setLookup(oldLookup);
        }
    }

    private final Map<Method, Handler> handlerMap = newMap();
}
