/*
 * Copyright (c) 2000, 2013, Oracle and/or its affiliates. All rights reserved.
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
package java8.java.beans;

import com.sun.beans.finder.PrimitiveWrapperMap;
import sun.swing.PrintColorUIResource;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.font.TextAttribute;
import java.beans.ConstructorProperties;
import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.EventHandler;
import java.beans.Expression;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PersistenceDelegate;
import java.beans.Statement;
import java.beans.XMLEncoder;
import java.lang.reflect.*;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.*;

import static sun.reflect.misc.ReflectUtil.isPackageAccessible;

/*
 * Like the <code>Intropector</code>, the <code>MetaData</code> class
 * contains <em>meta</em> objects that describe the way
 * classes should express their state in terms of their
 * own public APIs.
 *
 * @see java.beans.Intropector
 *
 * @author Philip Milne
 * @author Steve Langley
 */
class MetaData {

static final class NullPersistenceDelegate extends PersistenceDelegate {
    // Note this will be called by all classes when they reach the
    // top of their superclass chain.
    protected void initialize(Class<?> type, Object oldInstance, Object newInstance, Encoder out) {
    }
    protected Expression instantiate(Object oldInstance, Encoder out) { return null; }

    public void writeObject(Object oldInstance, Encoder out) {
    // System.out.println("NullPersistenceDelegate:writeObject " + oldInstance);
    }
}

/**
 * The persistence delegate for <CODE>enum</CODE> classes.
 *
 * @author Sergey A. Malenkov
 */
static final class EnumPersistenceDelegate extends PersistenceDelegate {
    protected boolean mutatesTo(Object oldInstance, Object newInstance) {
        return oldInstance == newInstance;
    }

    protected Expression instantiate(Object oldInstance, Encoder out) {
        Enum<?> e = (Enum<?>) oldInstance;
        return new Expression(e, Enum.class, "valueOf", new Object[]{e.getDeclaringClass(), e.name()});
    }
}

static final class PrimitivePersistenceDelegate extends PersistenceDelegate {
    protected boolean mutatesTo(Object oldInstance, Object newInstance) {
        return oldInstance.equals(newInstance);
    }

    protected Expression instantiate(Object oldInstance, Encoder out) {
        return new Expression(oldInstance, oldInstance.getClass(),
                  "new", new Object[]{oldInstance.toString()});
    }
}

static final class ArrayPersistenceDelegate extends PersistenceDelegate {
    protected boolean mutatesTo(Object oldInstance, Object newInstance) {
        return (newInstance != null &&
                oldInstance.getClass() == newInstance.getClass() && // Also ensures the subtype is correct.
                Array.getLength(oldInstance) == Array.getLength(newInstance));
        }

    protected Expression instantiate(Object oldInstance, Encoder out) {
        // System.out.println("instantiate: " + type + " " + oldInstance);
        Class<?> oldClass = oldInstance.getClass();
        return new Expression(oldInstance, Array.class, "newInstance",
                   new Object[]{oldClass.getComponentType(),
                                new Integer(Array.getLength(oldInstance))});
        }

    protected void initialize(Class<?> type, Object oldInstance, Object newInstance, Encoder out) {
        int n = Array.getLength(oldInstance);
        for (int i = 0; i < n; i++) {
            Object index = new Integer(i);
            // Expression oldGetExp = new Expression(Array.class, "get", new Object[]{oldInstance, index});
            // Expression newGetExp = new Expression(Array.class, "get", new Object[]{newInstance, index});
            Expression oldGetExp = new Expression(oldInstance, "get", new Object[]{index});
            Expression newGetExp = new Expression(newInstance, "get", new Object[]{index});
            try {
                Object oldValue = oldGetExp.getValue();
                Object newValue = newGetExp.getValue();
                out.writeExpression(oldGetExp);
                if (!Objects.equals(newValue, out.get(oldValue))) {
                    // System.out.println("Not equal: " + newGetExp + " != " + actualGetExp);
                    // invokeStatement(Array.class, "set", new Object[]{oldInstance, index, oldValue}, out);
                    DefaultPersistenceDelegate.invokeStatement(oldInstance, "set", new Object[]{index, oldValue}, out);
                }
            }
            catch (Exception e) {
                // System.err.println("Warning:: failed to write: " + oldGetExp);
                out.getExceptionListener().exceptionThrown(e);
            }
        }
    }
}

static final class ProxyPersistenceDelegate extends PersistenceDelegate {
    protected Expression instantiate(Object oldInstance, Encoder out) {
        Class<?> type = oldInstance.getClass();
        java.lang.reflect.Proxy p = (java.lang.reflect.Proxy)oldInstance;
        // This unappealing hack is not required but makes the
        // representation of EventHandlers much more concise.
        java.lang.reflect.InvocationHandler ih = java.lang.reflect.Proxy.getInvocationHandler(p);
        if (ih instanceof EventHandler) {
            EventHandler eh = (EventHandler)ih;
            Vector<Object> args = new Vector<>();
            args.add(type.getInterfaces()[0]);
            args.add(eh.getTarget());
            args.add(eh.getAction());
            if (eh.getEventPropertyName() != null) {
                args.add(eh.getEventPropertyName());
            }
            if (eh.getListenerMethodName() != null) {
                args.setSize(4);
                args.add(eh.getListenerMethodName());
            }
            return new Expression(oldInstance,
                                  EventHandler.class,
                                  "create",
                                  args.toArray());
        }
        return new Expression(oldInstance,
                              java.lang.reflect.Proxy.class,
                              "newProxyInstance",
                              new Object[]{type.getClassLoader(),
                                           type.getInterfaces(),
                                           ih});
    }
}

// Strings
static final class java_lang_String_PersistenceDelegate extends PersistenceDelegate {
    protected Expression instantiate(Object oldInstance, Encoder out) { return null; }

    public void writeObject(Object oldInstance, Encoder out) {
        // System.out.println("NullPersistenceDelegate:writeObject " + oldInstance);
    }
}

// Classes
static final class java_lang_Class_PersistenceDelegate extends PersistenceDelegate {
    protected boolean mutatesTo(Object oldInstance, Object newInstance) {
        return oldInstance.equals(newInstance);
    }

    protected Expression instantiate(Object oldInstance, Encoder out) {
        Class<?> c = (Class)oldInstance;
        // As of 1.3 it is not possible to call Class.forName("int"),
        // so we have to generate different code for primitive types.
        // This is needed for arrays whose subtype may be primitive.
        if (c.isPrimitive()) {
            Field field = null;
            try {
                field = PrimitiveWrapperMap.getType(c.getName()).getDeclaredField("TYPE");
            } catch (NoSuchFieldException ex) {
                System.err.println("Unknown primitive type: " + c);
            }
            return new Expression(oldInstance, field, "get", new Object[]{null});
        }
        else if (oldInstance == String.class) {
            return new Expression(oldInstance, "", "getClass", new Object[]{});
        }
        else if (oldInstance == Class.class) {
            return new Expression(oldInstance, String.class, "getClass", new Object[]{});
        }
        else {
            Expression newInstance = new Expression(oldInstance, Class.class, "forName", new Object[] { c.getName() });
            newInstance.loader = c.getClassLoader();
            return newInstance;
        }
    }
}

// Fields
static final class java_lang_reflect_Field_PersistenceDelegate extends PersistenceDelegate {
    protected boolean mutatesTo(Object oldInstance, Object newInstance) {
        return oldInstance.equals(newInstance);
    }

    protected Expression instantiate(Object oldInstance, Encoder out) {
        Field f = (Field)oldInstance;
        return new Expression(oldInstance,
                f.getDeclaringClass(),
                "getField",
                new Object[]{f.getName()});
    }
}

// Methods
static final class java_lang_reflect_Method_PersistenceDelegate extends PersistenceDelegate {
    protected boolean mutatesTo(Object oldInstance, Object newInstance) {
        return oldInstance.equals(newInstance);
    }

    protected Expression instantiate(Object oldInstance, Encoder out) {
        Method m = (Method)oldInstance;
        return new Expression(oldInstance,
                m.getDeclaringClass(),
                "getMethod",
                new Object[]{m.getName(), m.getParameterTypes()});
    }
}

// Dates

/**
 * The persistence delegate for <CODE>java.util.Date</CODE> classes.
 * Do not extend DefaultPersistenceDelegate to improve performance and
 * to avoid problems with <CODE>java.sql.Date</CODE>,
 * <CODE>java.sql.Time</CODE> and <CODE>java.sql.Timestamp</CODE>.
 *
 * @author Sergey A. Malenkov
 */
static class java_util_Date_PersistenceDelegate extends PersistenceDelegate {
    protected boolean mutatesTo(Object oldInstance, Object newInstance) {
        if (!super.mutatesTo(oldInstance, newInstance)) {
            return false;
        }
        Date oldDate = (Date)oldInstance;
        Date newDate = (Date)newInstance;

        return oldDate.getTime() == newDate.getTime();
    }

    protected Expression instantiate(Object oldInstance, Encoder out) {
        Date date = (Date)oldInstance;
        return new Expression(date, date.getClass(), "new", new Object[] {date.getTime()});
    }
}

/**
 * The persistence delegate for <CODE>java.sql.Timestamp</CODE> classes.
 * It supports nanoseconds.
 *
 * @author Sergey A. Malenkov
 */
static final class java_sql_Timestamp_PersistenceDelegate extends java_util_Date_PersistenceDelegate {
    private static final Method getNanosMethod = getNanosMethod();

    private static Method getNanosMethod() {
        try {
            Class<?> c = Class.forName("java.sql.Timestamp", true, null);
            return c.getMethod("getNanos");
        } catch (ClassNotFoundException e) {
            return null;
        } catch (NoSuchMethodException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Invoke Timstamp getNanos.
     */
    private static int getNanos(Object obj) {
        if (getNanosMethod == null)
            throw new AssertionError("Should not get here");
        try {
            return (Integer)getNanosMethod.invoke(obj);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException)
                throw (RuntimeException)cause;
            if (cause instanceof Error)
                throw (Error)cause;
            throw new AssertionError(e);
        } catch (IllegalAccessException iae) {
            throw new AssertionError(iae);
        }
    }

    protected void initialize(Class<?> type, Object oldInstance, Object newInstance, Encoder out) {
        // assumes oldInstance and newInstance are Timestamps
        int nanos = getNanos(oldInstance);
        if (nanos != getNanos(newInstance)) {
            out.writeStatement(new Statement(oldInstance, "setNanos", new Object[] {nanos}));
        }
    }
}

// Collections

/*
The Hashtable and AbstractMap classes have no common ancestor yet may
be handled with a single persistence delegate: one which uses the methods
of the Map insterface exclusively. Attatching the persistence delegates
to the interfaces themselves is fraught however since, in the case of
the Map, both the AbstractMap and HashMap classes are declared to
implement the Map interface, leaving the obvious implementation prone
to repeating their initialization. These issues and questions around
the ordering of delegates attached to interfaces have lead us to
ignore any delegates attached to interfaces and force all persistence
delegates to be registered with concrete classes.
*/

/**
 * The base class for persistence delegates for inner classes
 * that can be created using {@link Collections}.
 *
 * @author Sergey A. Malenkov
 */
private static abstract class java_util_Collections extends PersistenceDelegate {
    protected boolean mutatesTo(Object oldInstance, Object newInstance) {
        if (!super.mutatesTo(oldInstance, newInstance)) {
            return false;
        }
        if ((oldInstance instanceof List) || (oldInstance instanceof Set) || (oldInstance instanceof Map)) {
            return oldInstance.equals(newInstance);
        }
        Collection<?> oldC = (Collection<?>) oldInstance;
        Collection<?> newC = (Collection<?>) newInstance;
        return (oldC.size() == newC.size()) && oldC.containsAll(newC);
    }

    protected void initialize(Class<?> type, Object oldInstance, Object newInstance, Encoder out) {
        // do not initialize these custom collections in default way
    }

    static final class EmptyList_PersistenceDelegate extends java.beans.MetaData.java_util_Collections {
        protected Expression instantiate(Object oldInstance, Encoder out) {
            return new Expression(oldInstance, Collections.class, "emptyList", null);
        }
    }

    static final class EmptySet_PersistenceDelegate extends java.beans.MetaData.java_util_Collections {
        protected Expression instantiate(Object oldInstance, Encoder out) {
            return new Expression(oldInstance, Collections.class, "emptySet", null);
        }
    }

    static final class EmptyMap_PersistenceDelegate extends java.beans.MetaData.java_util_Collections {
        protected Expression instantiate(Object oldInstance, Encoder out) {
            return new Expression(oldInstance, Collections.class, "emptyMap", null);
        }
    }

    static final class SingletonList_PersistenceDelegate extends java.beans.MetaData.java_util_Collections {
        protected Expression instantiate(Object oldInstance, Encoder out) {
            List<?> list = (List<?>) oldInstance;
            return new Expression(oldInstance, Collections.class, "singletonList", new Object[]{list.get(0)});
        }
    }

    static final class SingletonSet_PersistenceDelegate extends java.beans.MetaData.java_util_Collections {
        protected Expression instantiate(Object oldInstance, Encoder out) {
            Set<?> set = (Set<?>) oldInstance;
            return new Expression(oldInstance, Collections.class, "singleton", new Object[]{set.iterator().next()});
        }
    }

    static final class SingletonMap_PersistenceDelegate extends java.beans.MetaData.java_util_Collections {
        protected Expression instantiate(Object oldInstance, Encoder out) {
            Map<?,?> map = (Map<?,?>) oldInstance;
            Object key = map.keySet().iterator().next();
            return new Expression(oldInstance, Collections.class, "singletonMap", new Object[]{key, map.get(key)});
        }
    }

    static final class UnmodifiableCollection_PersistenceDelegate extends java.beans.MetaData.java_util_Collections {
        protected Expression instantiate(Object oldInstance, Encoder out) {
            List<?> list = new ArrayList<>((Collection<?>) oldInstance);
            return new Expression(oldInstance, Collections.class, "unmodifiableCollection", new Object[]{list});
        }
    }

    static final class UnmodifiableList_PersistenceDelegate extends java.beans.MetaData.java_util_Collections {
        protected Expression instantiate(Object oldInstance, Encoder out) {
            List<?> list = new LinkedList<>((Collection<?>) oldInstance);
            return new Expression(oldInstance, Collections.class, "unmodifiableList", new Object[]{list});
        }
    }

    static final class UnmodifiableRandomAccessList_PersistenceDelegate extends java.beans.MetaData.java_util_Collections {
        protected Expression instantiate(Object oldInstance, Encoder out) {
            List<?> list = new ArrayList<>((Collection<?>) oldInstance);
            return new Expression(oldInstance, Collections.class, "unmodifiableList", new Object[]{list});
        }
    }

    static final class UnmodifiableSet_PersistenceDelegate extends java.beans.MetaData.java_util_Collections {
        protected Expression instantiate(Object oldInstance, Encoder out) {
            Set<?> set = new HashSet<>((Set<?>) oldInstance);
            return new Expression(oldInstance, Collections.class, "unmodifiableSet", new Object[]{set});
        }
    }

    static final class UnmodifiableSortedSet_PersistenceDelegate extends java.beans.MetaData.java_util_Collections {
        protected Expression instantiate(Object oldInstance, Encoder out) {
            SortedSet<?> set = new TreeSet<>((SortedSet<?>) oldInstance);
            return new Expression(oldInstance, Collections.class, "unmodifiableSortedSet", new Object[]{set});
        }
    }

    static final class UnmodifiableMap_PersistenceDelegate extends java.beans.MetaData.java_util_Collections {
        protected Expression instantiate(Object oldInstance, Encoder out) {
            Map<?,?> map = new HashMap<>((Map<?,?>) oldInstance);
            return new Expression(oldInstance, Collections.class, "unmodifiableMap", new Object[]{map});
        }
    }

    static final class UnmodifiableSortedMap_PersistenceDelegate extends java.beans.MetaData.java_util_Collections {
        protected Expression instantiate(Object oldInstance, Encoder out) {
            SortedMap<?,?> map = new TreeMap<>((SortedMap<?,?>) oldInstance);
            return new Expression(oldInstance, Collections.class, "unmodifiableSortedMap", new Object[]{map});
        }
    }

    static final class SynchronizedCollection_PersistenceDelegate extends java.beans.MetaData.java_util_Collections {
        protected Expression instantiate(Object oldInstance, Encoder out) {
            List<?> list = new ArrayList<>((Collection<?>) oldInstance);
            return new Expression(oldInstance, Collections.class, "synchronizedCollection", new Object[]{list});
        }
    }

    static final class SynchronizedList_PersistenceDelegate extends java.beans.MetaData.java_util_Collections {
        protected Expression instantiate(Object oldInstance, Encoder out) {
            List<?> list = new LinkedList<>((Collection<?>) oldInstance);
            return new Expression(oldInstance, Collections.class, "synchronizedList", new Object[]{list});
        }
    }

    static final class SynchronizedRandomAccessList_PersistenceDelegate extends java.beans.MetaData.java_util_Collections {
        protected Expression instantiate(Object oldInstance, Encoder out) {
            List<?> list = new ArrayList<>((Collection<?>) oldInstance);
            return new Expression(oldInstance, Collections.class, "synchronizedList", new Object[]{list});
        }
    }

    static final class SynchronizedSet_PersistenceDelegate extends java.beans.MetaData.java_util_Collections {
        protected Expression instantiate(Object oldInstance, Encoder out) {
            Set<?> set = new HashSet<>((Set<?>) oldInstance);
            return new Expression(oldInstance, Collections.class, "synchronizedSet", new Object[]{set});
        }
    }

    static final class SynchronizedSortedSet_PersistenceDelegate extends java.beans.MetaData.java_util_Collections {
        protected Expression instantiate(Object oldInstance, Encoder out) {
            SortedSet<?> set = new TreeSet<>((SortedSet<?>) oldInstance);
            return new Expression(oldInstance, Collections.class, "synchronizedSortedSet", new Object[]{set});
        }
    }

    static final class SynchronizedMap_PersistenceDelegate extends java.beans.MetaData.java_util_Collections {
        protected Expression instantiate(Object oldInstance, Encoder out) {
            Map<?,?> map = new HashMap<>((Map<?,?>) oldInstance);
            return new Expression(oldInstance, Collections.class, "synchronizedMap", new Object[]{map});
        }
    }

    static final class SynchronizedSortedMap_PersistenceDelegate extends java.beans.MetaData.java_util_Collections {
        protected Expression instantiate(Object oldInstance, Encoder out) {
            SortedMap<?,?> map = new TreeMap<>((SortedMap<?,?>) oldInstance);
            return new Expression(oldInstance, Collections.class, "synchronizedSortedMap", new Object[]{map});
        }
    }

    static final class CheckedCollection_PersistenceDelegate extends java.beans.MetaData.java_util_Collections {
        protected Expression instantiate(Object oldInstance, Encoder out) {
            Object type = java.beans.MetaData.getPrivateFieldValue(oldInstance, "java.util.Collections$CheckedCollection.type");
            List<?> list = new ArrayList<>((Collection<?>) oldInstance);
            return new Expression(oldInstance, Collections.class, "checkedCollection", new Object[]{list, type});
        }
    }

    static final class CheckedList_PersistenceDelegate extends java.beans.MetaData.java_util_Collections {
        protected Expression instantiate(Object oldInstance, Encoder out) {
            Object type = java.beans.MetaData.getPrivateFieldValue(oldInstance, "java.util.Collections$CheckedCollection.type");
            List<?> list = new LinkedList<>((Collection<?>) oldInstance);
            return new Expression(oldInstance, Collections.class, "checkedList", new Object[]{list, type});
        }
    }

    static final class CheckedRandomAccessList_PersistenceDelegate extends java.beans.MetaData.java_util_Collections {
        protected Expression instantiate(Object oldInstance, Encoder out) {
            Object type = java.beans.MetaData.getPrivateFieldValue(oldInstance, "java.util.Collections$CheckedCollection.type");
            List<?> list = new ArrayList<>((Collection<?>) oldInstance);
            return new Expression(oldInstance, Collections.class, "checkedList", new Object[]{list, type});
        }
    }

    static final class CheckedSet_PersistenceDelegate extends java.beans.MetaData.java_util_Collections {
        protected Expression instantiate(Object oldInstance, Encoder out) {
            Object type = java.beans.MetaData.getPrivateFieldValue(oldInstance, "java.util.Collections$CheckedCollection.type");
            Set<?> set = new HashSet<>((Set<?>) oldInstance);
            return new Expression(oldInstance, Collections.class, "checkedSet", new Object[]{set, type});
        }
    }

    static final class CheckedSortedSet_PersistenceDelegate extends java.beans.MetaData.java_util_Collections {
        protected Expression instantiate(Object oldInstance, Encoder out) {
            Object type = java.beans.MetaData.getPrivateFieldValue(oldInstance, "java.util.Collections$CheckedCollection.type");
            SortedSet<?> set = new TreeSet<>((SortedSet<?>) oldInstance);
            return new Expression(oldInstance, Collections.class, "checkedSortedSet", new Object[]{set, type});
        }
    }

    static final class CheckedMap_PersistenceDelegate extends java.beans.MetaData.java_util_Collections {
        protected Expression instantiate(Object oldInstance, Encoder out) {
            Object keyType   = java.beans.MetaData.getPrivateFieldValue(oldInstance, "java.util.Collections$CheckedMap.keyType");
            Object valueType = java.beans.MetaData.getPrivateFieldValue(oldInstance, "java.util.Collections$CheckedMap.valueType");
            Map<?,?> map = new HashMap<>((Map<?,?>) oldInstance);
            return new Expression(oldInstance, Collections.class, "checkedMap", new Object[]{map, keyType, valueType});
        }
    }

    static final class CheckedSortedMap_PersistenceDelegate extends java.beans.MetaData.java_util_Collections {
        protected Expression instantiate(Object oldInstance, Encoder out) {
            Object keyType   = java.beans.MetaData.getPrivateFieldValue(oldInstance, "java.util.Collections$CheckedMap.keyType");
            Object valueType = java.beans.MetaData.getPrivateFieldValue(oldInstance, "java.util.Collections$CheckedMap.valueType");
            SortedMap<?,?> map = new TreeMap<>((SortedMap<?,?>) oldInstance);
            return new Expression(oldInstance, Collections.class, "checkedSortedMap", new Object[]{map, keyType, valueType});
        }
    }
}

/**
 * The persistence delegate for <CODE>java.util.EnumMap</CODE> classes.
 *
 * @author Sergey A. Malenkov
 */
static final class java_util_EnumMap_PersistenceDelegate extends PersistenceDelegate {
    protected boolean mutatesTo(Object oldInstance, Object newInstance) {
        return super.mutatesTo(oldInstance, newInstance) && (getType(oldInstance) == getType(newInstance));
    }

    protected Expression instantiate(Object oldInstance, Encoder out) {
        return new Expression(oldInstance, EnumMap.class, "new", new Object[] {getType(oldInstance)});
    }

    private static Object getType(Object instance) {
        return java.beans.MetaData.getPrivateFieldValue(instance, "java.util.EnumMap.keyType");
    }
}

/**
 * The persistence delegate for <CODE>java.util.EnumSet</CODE> classes.
 *
 * @author Sergey A. Malenkov
 */
static final class java_util_EnumSet_PersistenceDelegate extends PersistenceDelegate {
    protected boolean mutatesTo(Object oldInstance, Object newInstance) {
        return super.mutatesTo(oldInstance, newInstance) && (getType(oldInstance) == getType(newInstance));
    }

    protected Expression instantiate(Object oldInstance, Encoder out) {
        return new Expression(oldInstance, EnumSet.class, "noneOf", new Object[] {getType(oldInstance)});
    }

    private static Object getType(Object instance) {
        return java.beans.MetaData.getPrivateFieldValue(instance, "java.util.EnumSet.elementType");
    }
}

// Collection
static class java_util_Collection_PersistenceDelegate extends DefaultPersistenceDelegate {
    protected void initialize(Class<?> type, Object oldInstance, Object newInstance, Encoder out) {
        Collection<?> oldO = (Collection)oldInstance;
        Collection<?> newO = (Collection)newInstance;

        if (newO.size() != 0) {
            invokeStatement(oldInstance, "clear", new Object[]{}, out);
        }
        for (Iterator<?> i = oldO.iterator(); i.hasNext();) {
            invokeStatement(oldInstance, "add", new Object[]{i.next()}, out);
        }
    }
}

// List
static class java_util_List_PersistenceDelegate extends DefaultPersistenceDelegate {
    protected void initialize(Class<?> type, Object oldInstance, Object newInstance, Encoder out) {
        List<?> oldO = (List<?>)oldInstance;
        List<?> newO = (List<?>)newInstance;
        int oldSize = oldO.size();
        int newSize = (newO == null) ? 0 : newO.size();
        if (oldSize < newSize) {
            invokeStatement(oldInstance, "clear", new Object[]{}, out);
            newSize = 0;
        }
        for (int i = 0; i < newSize; i++) {
            Object index = new Integer(i);

            Expression oldGetExp = new Expression(oldInstance, "get", new Object[]{index});
            Expression newGetExp = new Expression(newInstance, "get", new Object[]{index});
            try {
                Object oldValue = oldGetExp.getValue();
                Object newValue = newGetExp.getValue();
                out.writeExpression(oldGetExp);
                if (!Objects.equals(newValue, out.get(oldValue))) {
                    invokeStatement(oldInstance, "set", new Object[]{index, oldValue}, out);
                }
            }
            catch (Exception e) {
                out.getExceptionListener().exceptionThrown(e);
            }
        }
        for (int i = newSize; i < oldSize; i++) {
            invokeStatement(oldInstance, "add", new Object[]{oldO.get(i)}, out);
        }
    }
}


// Map
static class java_util_Map_PersistenceDelegate extends DefaultPersistenceDelegate {
    protected void initialize(Class<?> type, Object oldInstance, Object newInstance, Encoder out) {
        // System.out.println("Initializing: " + newInstance);
        Map<?,?> oldMap = (Map)oldInstance;
        Map<?,?> newMap = (Map)newInstance;
        // Remove the new elements.
        // Do this first otherwise we undo the adding work.
        if (newMap != null) {
            for (Object newKey : newMap.keySet().toArray()) {
               // PENDING: This "key" is not in the right environment.
                if (!oldMap.containsKey(newKey)) {
                    invokeStatement(oldInstance, "remove", new Object[]{newKey}, out);
                }
            }
        }
        // Add the new elements.
        for ( Object oldKey : oldMap.keySet() ) {
            Expression oldGetExp = new Expression(oldInstance, "get", new Object[]{oldKey});
            // Pending: should use newKey.
            Expression newGetExp = new Expression(newInstance, "get", new Object[]{oldKey});
            try {
                Object oldValue = oldGetExp.getValue();
                Object newValue = newGetExp.getValue();
                out.writeExpression(oldGetExp);
                if (!Objects.equals(newValue, out.get(oldValue))) {
                    invokeStatement(oldInstance, "put", new Object[]{oldKey, oldValue}, out);
                } else if ((newValue == null) && !newMap.containsKey(oldKey)) {
                    // put oldValue(=null?) if oldKey is absent in newMap
                    invokeStatement(oldInstance, "put", new Object[]{oldKey, oldValue}, out);
                }
            }
            catch (Exception e) {
                out.getExceptionListener().exceptionThrown(e);
            }
        }
    }
}

static final class java_util_AbstractCollection_PersistenceDelegate extends java_util_Collection_PersistenceDelegate {}
static final class java_util_AbstractList_PersistenceDelegate extends java_util_List_PersistenceDelegate {}
static final class java_util_AbstractMap_PersistenceDelegate extends java_util_Map_PersistenceDelegate {}
static final class java_util_Hashtable_PersistenceDelegate extends java_util_Map_PersistenceDelegate {}


// Beans
static final class java_beans_beancontext_BeanContextSupport_PersistenceDelegate extends java_util_Collection_PersistenceDelegate {}

// AWT

/**
 * The persistence delegate for {@link Insets}.
 * It is impossible to use {@link DefaultPersistenceDelegate}
 * because this class does not have any properties.
 *
 * @author Sergey A. Malenkov
 */
static final class java_awt_Insets_PersistenceDelegate extends PersistenceDelegate {
    protected boolean mutatesTo(Object oldInstance, Object newInstance) {
        return oldInstance.equals(newInstance);
    }

    protected Expression instantiate(Object oldInstance, Encoder out) {
        Insets insets = (Insets) oldInstance;
        Object[] args = new Object[] {
                insets.top,
                insets.left,
                insets.bottom,
                insets.right,
        };
        return new Expression(insets, insets.getClass(), "new", args);
    }
}

/**
 * The persistence delegate for {@link Font}.
 * It is impossible to use {@link DefaultPersistenceDelegate}
 * because size of the font can be float value.
 *
 * @author Sergey A. Malenkov
 */
static final class java_awt_Font_PersistenceDelegate extends PersistenceDelegate {
    protected boolean mutatesTo(Object oldInstance, Object newInstance) {
        return oldInstance.equals(newInstance);
    }

    protected Expression instantiate(Object oldInstance, Encoder out) {
        Font font = (Font) oldInstance;

        int count = 0;
        String family = null;
        int style = Font.PLAIN;
        int size = 12;

        Map<TextAttribute, ?> basic = font.getAttributes();
        Map<TextAttribute, Object> clone = new HashMap<>(basic.size());
        for (TextAttribute key : basic.keySet()) {
            Object value = basic.get(key);
            if (value != null) {
                clone.put(key, value);
            }
            if (key == TextAttribute.FAMILY) {
                if (value instanceof String) {
                    count++;
                    family = (String) value;
                }
            }
            else if (key == TextAttribute.WEIGHT) {
                if (TextAttribute.WEIGHT_REGULAR.equals(value)) {
                    count++;
                } else if (TextAttribute.WEIGHT_BOLD.equals(value)) {
                    count++;
                    style |= Font.BOLD;
                }
            }
            else if (key == TextAttribute.POSTURE) {
                if (TextAttribute.POSTURE_REGULAR.equals(value)) {
                    count++;
                } else if (TextAttribute.POSTURE_OBLIQUE.equals(value)) {
                    count++;
                    style |= Font.ITALIC;
                }
            } else if (key == TextAttribute.SIZE) {
                if (value instanceof Number) {
                    Number number = (Number) value;
                    size = number.intValue();
                    if (size == number.floatValue()) {
                        count++;
                    }
                }
            }
        }
        Class<?> type = font.getClass();
        if (count == clone.size()) {
            return new Expression(font, type, "new", new Object[]{family, style, size});
        }
        if (type == Font.class) {
            return new Expression(font, type, "getFont", new Object[]{clone});
        }
        return new Expression(font, type, "new", new Object[]{Font.getFont(clone)});
    }
}

/**
 * The persistence delegate for {@link AWTKeyStroke}.
 * It is impossible to use {@link DefaultPersistenceDelegate}
 * because this class have no public constructor.
 *
 * @author Sergey A. Malenkov
 */
static final class java_awt_AWTKeyStroke_PersistenceDelegate extends PersistenceDelegate {
    protected boolean mutatesTo(Object oldInstance, Object newInstance) {
        return oldInstance.equals(newInstance);
    }

    protected Expression instantiate(Object oldInstance, Encoder out) {
        AWTKeyStroke key = (AWTKeyStroke) oldInstance;

        char ch = key.getKeyChar();
        int code = key.getKeyCode();
        int mask = key.getModifiers();
        boolean onKeyRelease = key.isOnKeyRelease();

        Object[] args = null;
        if (ch == KeyEvent.CHAR_UNDEFINED) {
            args = !onKeyRelease
                    ? new Object[]{code, mask}
                    : new Object[]{code, mask, onKeyRelease};
        } else if (code == KeyEvent.VK_UNDEFINED) {
            if (!onKeyRelease) {
                args = (mask == 0)
                        ? new Object[]{ch}
                        : new Object[]{ch, mask};
            } else if (mask == 0) {
                args = new Object[]{ch, onKeyRelease};
            }
        }
        if (args == null) {
            throw new IllegalStateException("Unsupported KeyStroke: " + key);
        }
        Class<?> type = key.getClass();
        String name = type.getName();
        // get short name of the class
        int index = name.lastIndexOf('.') + 1;
        if (index > 0) {
            name = name.substring(index);
        }
        return new Expression( key, type, "get" + name, args );
    }
}

static class StaticFieldsPersistenceDelegate extends PersistenceDelegate {
    protected void installFields(Encoder out, Class<?> cls) {
        if (Modifier.isPublic(cls.getModifiers()) && isPackageAccessible(cls)) {
            Field fields[] = cls.getFields();
            for(int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                // Don't install primitives, their identity will not be preserved
                // by wrapping.
                if (Object.class.isAssignableFrom(field.getType())) {
                    out.writeExpression(new Expression(field, "get", new Object[]{null}));
                }
            }
        }
    }

    protected Expression instantiate(Object oldInstance, Encoder out) {
        throw new RuntimeException("Unrecognized instance: " + oldInstance);
    }

    public void writeObject(Object oldInstance, Encoder out) {
        if (out.getAttribute(this) == null) {
            out.setAttribute(this, Boolean.TRUE);
            installFields(out, oldInstance.getClass());
        }
        super.writeObject(oldInstance, out);
    }
}

// SystemColor
static final class java_awt_SystemColor_PersistenceDelegate extends StaticFieldsPersistenceDelegate {}

// TextAttribute
static final class java_awt_font_TextAttribute_PersistenceDelegate extends StaticFieldsPersistenceDelegate {}

// MenuShortcut
static final class java_awt_MenuShortcut_PersistenceDelegate extends PersistenceDelegate {
    protected boolean mutatesTo(Object oldInstance, Object newInstance) {
        return oldInstance.equals(newInstance);
    }

    protected Expression instantiate(Object oldInstance, Encoder out) {
        java.awt.MenuShortcut m = (java.awt.MenuShortcut)oldInstance;
        return new Expression(oldInstance, m.getClass(), "new",
                   new Object[]{new Integer(m.getKey()), Boolean.valueOf(m.usesShiftModifier())});
    }
}

// Component
static final class java_awt_Component_PersistenceDelegate extends DefaultPersistenceDelegate {
    protected void initialize(Class<?> type, Object oldInstance, Object newInstance, Encoder out) {
        super.initialize(type, oldInstance, newInstance, out);
        java.awt.Component c = (java.awt.Component)oldInstance;
        java.awt.Component c2 = (java.awt.Component)newInstance;
        // The "background", "foreground" and "font" properties.
        // The foreground and font properties of Windows change from
        // null to defined values after the Windows are made visible -
        // special case them for now.
        if (!(oldInstance instanceof java.awt.Window)) {
            Object oldBackground = c.isBackgroundSet() ? c.getBackground() : null;
            Object newBackground = c2.isBackgroundSet() ? c2.getBackground() : null;
            if (!Objects.equals(oldBackground, newBackground)) {
                invokeStatement(oldInstance, "setBackground", new Object[] { oldBackground }, out);
            }
            Object oldForeground = c.isForegroundSet() ? c.getForeground() : null;
            Object newForeground = c2.isForegroundSet() ? c2.getForeground() : null;
            if (!Objects.equals(oldForeground, newForeground)) {
                invokeStatement(oldInstance, "setForeground", new Object[] { oldForeground }, out);
            }
            Object oldFont = c.isFontSet() ? c.getFont() : null;
            Object newFont = c2.isFontSet() ? c2.getFont() : null;
            if (!Objects.equals(oldFont, newFont)) {
                invokeStatement(oldInstance, "setFont", new Object[] { oldFont }, out);
            }
        }

        // Bounds
        java.awt.Container p = c.getParent();
        if (p == null || p.getLayout() == null) {
            // Use the most concise construct.
            boolean locationCorrect = c.getLocation().equals(c2.getLocation());
            boolean sizeCorrect = c.getSize().equals(c2.getSize());
            if (!locationCorrect && !sizeCorrect) {
                invokeStatement(oldInstance, "setBounds", new Object[]{c.getBounds()}, out);
            }
            else if (!locationCorrect) {
                invokeStatement(oldInstance, "setLocation", new Object[]{c.getLocation()}, out);
            }
            else if (!sizeCorrect) {
                invokeStatement(oldInstance, "setSize", new Object[]{c.getSize()}, out);
            }
        }
    }
}

// Container
static final class java_awt_Container_PersistenceDelegate extends DefaultPersistenceDelegate {
    protected void initialize(Class<?> type, Object oldInstance, Object newInstance, Encoder out) {
        super.initialize(type, oldInstance, newInstance, out);
        // Ignore the children of a JScrollPane.
        // Pending(milne) find a better way to do this.
        if (oldInstance instanceof javax.swing.JScrollPane) {
            return;
        }
        java.awt.Container oldC = (java.awt.Container)oldInstance;
        java.awt.Component[] oldChildren = oldC.getComponents();
        java.awt.Container newC = (java.awt.Container)newInstance;
        java.awt.Component[] newChildren = (newC == null) ? new java.awt.Component[0] : newC.getComponents();

        BorderLayout layout = ( oldC.getLayout() instanceof BorderLayout )
                ? ( BorderLayout )oldC.getLayout()
                : null;

        JLayeredPane oldLayeredPane = (oldInstance instanceof JLayeredPane)
                ? (JLayeredPane) oldInstance
                : null;

        // Pending. Assume all the new children are unaltered.
        for(int i = newChildren.length; i < oldChildren.length; i++) {
            Object[] args = ( layout != null )
                    ? new Object[] {oldChildren[i], layout.getConstraints( oldChildren[i] )}
                    : (oldLayeredPane != null)
                            ? new Object[] {oldChildren[i], oldLayeredPane.getLayer(oldChildren[i]), Integer.valueOf(-1)}
                            : new Object[] {oldChildren[i]};

            invokeStatement(oldInstance, "add", args, out);
        }
    }
}

// Choice
static final class java_awt_Choice_PersistenceDelegate extends DefaultPersistenceDelegate {
    protected void initialize(Class<?> type, Object oldInstance, Object newInstance, Encoder out) {
        super.initialize(type, oldInstance, newInstance, out);
        java.awt.Choice m = (java.awt.Choice)oldInstance;
        java.awt.Choice n = (java.awt.Choice)newInstance;
        for (int i = n.getItemCount(); i < m.getItemCount(); i++) {
            invokeStatement(oldInstance, "add", new Object[]{m.getItem(i)}, out);
        }
    }
}

// Menu
static final class java_awt_Menu_PersistenceDelegate extends DefaultPersistenceDelegate {
    protected void initialize(Class<?> type, Object oldInstance, Object newInstance, Encoder out) {
        super.initialize(type, oldInstance, newInstance, out);
        java.awt.Menu m = (java.awt.Menu)oldInstance;
        java.awt.Menu n = (java.awt.Menu)newInstance;
        for (int i = n.getItemCount(); i < m.getItemCount(); i++) {
            invokeStatement(oldInstance, "add", new Object[]{m.getItem(i)}, out);
        }
    }
}

// MenuBar
static final class java_awt_MenuBar_PersistenceDelegate extends DefaultPersistenceDelegate {
    protected void initialize(Class<?> type, Object oldInstance, Object newInstance, Encoder out) {
        super.initialize(type, oldInstance, newInstance, out);
        java.awt.MenuBar m = (java.awt.MenuBar)oldInstance;
        java.awt.MenuBar n = (java.awt.MenuBar)newInstance;
        for (int i = n.getMenuCount(); i < m.getMenuCount(); i++) {
            invokeStatement(oldInstance, "add", new Object[]{m.getMenu(i)}, out);
        }
    }
}

// List
static final class java_awt_List_PersistenceDelegate extends DefaultPersistenceDelegate {
    protected void initialize(Class<?> type, Object oldInstance, Object newInstance, Encoder out) {
        super.initialize(type, oldInstance, newInstance, out);
        java.awt.List m = (java.awt.List)oldInstance;
        java.awt.List n = (java.awt.List)newInstance;
        for (int i = n.getItemCount(); i < m.getItemCount(); i++) {
            invokeStatement(oldInstance, "add", new Object[]{m.getItem(i)}, out);
        }
    }
}


// LayoutManagers

// BorderLayout
static final class java_awt_BorderLayout_PersistenceDelegate extends DefaultPersistenceDelegate {
    private static final String[] CONSTRAINTS = {
            BorderLayout.NORTH,
            BorderLayout.SOUTH,
            BorderLayout.EAST,
            BorderLayout.WEST,
            BorderLayout.CENTER,
            BorderLayout.PAGE_START,
            BorderLayout.PAGE_END,
            BorderLayout.LINE_START,
            BorderLayout.LINE_END,
    };
    @Override
    protected void initialize(Class<?> type, Object oldInstance,
                              Object newInstance, Encoder out) {
        super.initialize(type, oldInstance, newInstance, out);
        BorderLayout oldLayout = (BorderLayout) oldInstance;
        BorderLayout newLayout = (BorderLayout) newInstance;
        for (String constraints : CONSTRAINTS) {
            Object oldC = oldLayout.getLayoutComponent(constraints);
            Object newC = newLayout.getLayoutComponent(constraints);
            // Pending, assume any existing elements are OK.
            if (oldC != null && newC == null) {
                invokeStatement(oldInstance, "addLayoutComponent",
                                new Object[] { oldC, constraints }, out);
            }
        }
    }
}

// CardLayout
static final class java_awt_CardLayout_PersistenceDelegate extends DefaultPersistenceDelegate {
    protected void initialize(Class<?> type, Object oldInstance,
                              Object newInstance, Encoder out) {
        super.initialize(type, oldInstance, newInstance, out);
        if (getVector(newInstance).isEmpty()) {
            for (Object card : getVector(oldInstance)) {
                Object[] args = {java.beans.MetaData.getPrivateFieldValue(card, "java.awt.CardLayout$Card.name"),
                                 java.beans.MetaData.getPrivateFieldValue(card, "java.awt.CardLayout$Card.comp")};
                invokeStatement(oldInstance, "addLayoutComponent", args, out);
            }
        }
    }
    protected boolean mutatesTo(Object oldInstance, Object newInstance) {
        return super.mutatesTo(oldInstance, newInstance) && getVector(newInstance).isEmpty();
    }
    private static Vector<?> getVector(Object instance) {
        return (Vector<?>) java.beans.MetaData.getPrivateFieldValue(instance, "java.awt.CardLayout.vector");
    }
}

// GridBagLayout
static final class java_awt_GridBagLayout_PersistenceDelegate extends DefaultPersistenceDelegate {
    protected void initialize(Class<?> type, Object oldInstance,
                              Object newInstance, Encoder out) {
        super.initialize(type, oldInstance, newInstance, out);
        if (getHashtable(newInstance).isEmpty()) {
            for (Map.Entry<?,?> entry : getHashtable(oldInstance).entrySet()) {
                Object[] args = {entry.getKey(), entry.getValue()};
                invokeStatement(oldInstance, "addLayoutComponent", args, out);
            }
        }
    }
    protected boolean mutatesTo(Object oldInstance, Object newInstance) {
        return super.mutatesTo(oldInstance, newInstance) && getHashtable(newInstance).isEmpty();
    }
    private static Hashtable<?,?> getHashtable(Object instance) {
        return (Hashtable<?,?>) java.beans.MetaData.getPrivateFieldValue(instance, "java.awt.GridBagLayout.comptable");
    }
}

// Swing

// JFrame (If we do this for Window instead of JFrame, the setVisible call
// will be issued before we have added all the children to the JFrame and
// will appear blank).
static final class javax_swing_JFrame_PersistenceDelegate extends DefaultPersistenceDelegate {
    protected void initialize(Class<?> type, Object oldInstance, Object newInstance, Encoder out) {
        super.initialize(type, oldInstance, newInstance, out);
        java.awt.Window oldC = (java.awt.Window)oldInstance;
        java.awt.Window newC = (java.awt.Window)newInstance;
        boolean oldV = oldC.isVisible();
        boolean newV = newC.isVisible();
        if (newV != oldV) {
            // false means: don't execute this statement at write time.
            boolean executeStatements = out.executeStatements;
            out.executeStatements = false;
            invokeStatement(oldInstance, "setVisible", new Object[]{Boolean.valueOf(oldV)}, out);
            out.executeStatements = executeStatements;
        }
    }
}

// Models

// DefaultListModel
static final class javax_swing_DefaultListModel_PersistenceDelegate extends DefaultPersistenceDelegate {
    protected void initialize(Class<?> type, Object oldInstance, Object newInstance, Encoder out) {
        // Note, the "size" property will be set here.
        super.initialize(type, oldInstance, newInstance, out);
        javax.swing.DefaultListModel<?> m = (javax.swing.DefaultListModel<?>)oldInstance;
        javax.swing.DefaultListModel<?> n = (javax.swing.DefaultListModel<?>)newInstance;
        for (int i = n.getSize(); i < m.getSize(); i++) {
            invokeStatement(oldInstance, "add", // Can also use "addElement".
                    new Object[]{m.getElementAt(i)}, out);
        }
    }
}

// DefaultComboBoxModel
static final class javax_swing_DefaultComboBoxModel_PersistenceDelegate extends DefaultPersistenceDelegate {
    protected void initialize(Class<?> type, Object oldInstance, Object newInstance, Encoder out) {
        super.initialize(type, oldInstance, newInstance, out);
        javax.swing.DefaultComboBoxModel<?> m = (javax.swing.DefaultComboBoxModel<?>)oldInstance;
        for (int i = 0; i < m.getSize(); i++) {
            invokeStatement(oldInstance, "addElement", new Object[]{m.getElementAt(i)}, out);
        }
    }
}


// DefaultMutableTreeNode
static final class javax_swing_tree_DefaultMutableTreeNode_PersistenceDelegate extends DefaultPersistenceDelegate {
    protected void initialize(Class<?> type, Object oldInstance, Object
                              newInstance, Encoder out) {
        super.initialize(type, oldInstance, newInstance, out);
        javax.swing.tree.DefaultMutableTreeNode m =
            (javax.swing.tree.DefaultMutableTreeNode)oldInstance;
        javax.swing.tree.DefaultMutableTreeNode n =
            (javax.swing.tree.DefaultMutableTreeNode)newInstance;
        for (int i = n.getChildCount(); i < m.getChildCount(); i++) {
            invokeStatement(oldInstance, "add", new
                Object[]{m.getChildAt(i)}, out);
        }
    }
}

// ToolTipManager
static final class javax_swing_ToolTipManager_PersistenceDelegate extends PersistenceDelegate {
    protected Expression instantiate(Object oldInstance, Encoder out) {
        return new Expression(oldInstance, javax.swing.ToolTipManager.class,
                              "sharedInstance", new Object[]{});
    }
}

// JTabbedPane
static final class javax_swing_JTabbedPane_PersistenceDelegate extends DefaultPersistenceDelegate {
    protected void initialize(Class<?> type, Object oldInstance, Object newInstance, Encoder out) {
        super.initialize(type, oldInstance, newInstance, out);
        javax.swing.JTabbedPane p = (javax.swing.JTabbedPane)oldInstance;
        for (int i = 0; i < p.getTabCount(); i++) {
            invokeStatement(oldInstance, "addTab",
                                          new Object[]{
                                              p.getTitleAt(i),
                                              p.getIconAt(i),
                                              p.getComponentAt(i)}, out);
        }
    }
}

// Box
static final class javax_swing_Box_PersistenceDelegate extends DefaultPersistenceDelegate {
    protected boolean mutatesTo(Object oldInstance, Object newInstance) {
        return super.mutatesTo(oldInstance, newInstance) && getAxis(oldInstance).equals(getAxis(newInstance));
    }

    protected Expression instantiate(Object oldInstance, Encoder out) {
        return new Expression(oldInstance, oldInstance.getClass(), "new", new Object[] {getAxis(oldInstance)});
    }

    private Integer getAxis(Object object) {
        Box box = (Box) object;
        return (Integer) java.beans.MetaData.getPrivateFieldValue(box.getLayout(), "javax.swing.BoxLayout.axis");
    }
}

// JMenu
// Note that we do not need to state the initialiser for
// JMenuItems since the getComponents() method defined in
// Container will return all of the sub menu items that
// need to be added to the menu item.
// Not so for JMenu apparently.
static final class javax_swing_JMenu_PersistenceDelegate extends DefaultPersistenceDelegate {
    protected void initialize(Class<?> type, Object oldInstance, Object newInstance, Encoder out) {
        super.initialize(type, oldInstance, newInstance, out);
        javax.swing.JMenu m = (javax.swing.JMenu)oldInstance;
        java.awt.Component[] c = m.getMenuComponents();
        for (int i = 0; i < c.length; i++) {
            invokeStatement(oldInstance, "add", new Object[]{c[i]}, out);
        }
    }
}

/**
 * The persistence delegate for {@link MatteBorder}.
 * It is impossible to use {@link DefaultPersistenceDelegate}
 * because this class does not have writable properties.
 *
 * @author Sergey A. Malenkov
 */
static final class javax_swing_border_MatteBorder_PersistenceDelegate extends PersistenceDelegate {
    protected Expression instantiate(Object oldInstance, Encoder out) {
        MatteBorder border = (MatteBorder) oldInstance;
        Insets insets = border.getBorderInsets();
        Object object = border.getTileIcon();
        if (object == null) {
            object = border.getMatteColor();
        }
        Object[] args = new Object[] {
                insets.top,
                insets.left,
                insets.bottom,
                insets.right,
                object,
        };
        return new Expression(border, border.getClass(), "new", args);
    }
}

/* XXX - doens't seem to work. Debug later.
static final class javax_swing_JMenu_PersistenceDelegate extends DefaultPersistenceDelegate {
    protected void initialize(Class<?> type, Object oldInstance, Object newInstance, Encoder out) {
        super.initialize(type, oldInstance, newInstance, out);
        javax.swing.JMenu m = (javax.swing.JMenu)oldInstance;
        javax.swing.JMenu n = (javax.swing.JMenu)newInstance;
        for (int i = n.getItemCount(); i < m.getItemCount(); i++) {
            invokeStatement(oldInstance, "add", new Object[]{m.getItem(i)}, out);
        }
    }
}
*/

/**
 * The persistence delegate for {@link PrintColorUIResource}.
 * It is impossible to use {@link DefaultPersistenceDelegate}
 * because this class has special rule for serialization:
 * it should be converted to {@link ColorUIResource}.
 *
 * @see PrintColorUIResource#writeReplace
 *
 * @author Sergey A. Malenkov
 */
static final class sun_swing_PrintColorUIResource_PersistenceDelegate extends PersistenceDelegate {
    protected boolean mutatesTo(Object oldInstance, Object newInstance) {
        return oldInstance.equals(newInstance);
    }

    protected Expression instantiate(Object oldInstance, Encoder out) {
        Color color = (Color) oldInstance;
        Object[] args = new Object[] {color.getRGB()};
        return new Expression(color, ColorUIResource.class, "new", args);
    }
}

    private static final Map<String,Field> fields = Collections.synchronizedMap(new WeakHashMap<String, Field>());
    private static Hashtable<String, PersistenceDelegate> internalPersistenceDelegates = new Hashtable<>();

    private static PersistenceDelegate nullPersistenceDelegate = new NullPersistenceDelegate();
    private static PersistenceDelegate enumPersistenceDelegate = new EnumPersistenceDelegate();
    private static PersistenceDelegate primitivePersistenceDelegate = new PrimitivePersistenceDelegate();
    private static PersistenceDelegate defaultPersistenceDelegate = new DefaultPersistenceDelegate();
    private static PersistenceDelegate arrayPersistenceDelegate;
    private static PersistenceDelegate proxyPersistenceDelegate;

    static {

        internalPersistenceDelegates.put("java.net.URI",
                                         new PrimitivePersistenceDelegate());

        // it is possible because MatteBorder is assignable from MatteBorderUIResource
        internalPersistenceDelegates.put("javax.swing.plaf.BorderUIResource$MatteBorderUIResource",
                                         new javax_swing_border_MatteBorder_PersistenceDelegate());

        // it is possible because FontUIResource is supported by java_awt_Font_PersistenceDelegate
        internalPersistenceDelegates.put("javax.swing.plaf.FontUIResource",
                                         new java_awt_Font_PersistenceDelegate());

        // it is possible because KeyStroke is supported by java_awt_AWTKeyStroke_PersistenceDelegate
        internalPersistenceDelegates.put("javax.swing.KeyStroke",
                                         new java_awt_AWTKeyStroke_PersistenceDelegate());

        internalPersistenceDelegates.put("java.sql.Date", new java_util_Date_PersistenceDelegate());
        internalPersistenceDelegates.put("java.sql.Time", new java_util_Date_PersistenceDelegate());

        internalPersistenceDelegates.put("java.util.JumboEnumSet", new java_util_EnumSet_PersistenceDelegate());
        internalPersistenceDelegates.put("java.util.RegularEnumSet", new java_util_EnumSet_PersistenceDelegate());
    }

    @SuppressWarnings("rawtypes")
    public synchronized static PersistenceDelegate getPersistenceDelegate(Class type) {
        if (type == null) {
            return nullPersistenceDelegate;
        }
        if (Enum.class.isAssignableFrom(type)) {
            return enumPersistenceDelegate;
        }
        if (null != XMLEncoder.primitiveTypeFor(type)) {
            return primitivePersistenceDelegate;
        }
        // The persistence delegate for arrays is non-trivial; instantiate it lazily.
        if (type.isArray()) {
            if (arrayPersistenceDelegate == null) {
                arrayPersistenceDelegate = new ArrayPersistenceDelegate();
            }
            return arrayPersistenceDelegate;
        }
        // Handle proxies lazily for backward compatibility with 1.2.
        try {
            if (java.lang.reflect.Proxy.isProxyClass(type)) {
                if (proxyPersistenceDelegate == null) {
                    proxyPersistenceDelegate = new ProxyPersistenceDelegate();
                }
                return proxyPersistenceDelegate;
            }
        }
        catch(Exception e) {}
        // else if (type.getDeclaringClass() != null) {
        //     return new DefaultPersistenceDelegate(new String[]{"this$0"});
        // }

        String typeName = type.getName();
        PersistenceDelegate pd = (PersistenceDelegate)getBeanAttribute(type, "persistenceDelegate");
        if (pd == null) {
            pd = internalPersistenceDelegates.get(typeName);
            if (pd != null) {
                return pd;
            }
            internalPersistenceDelegates.put(typeName, defaultPersistenceDelegate);
            try {
                String name =  type.getName();
                Class c = Class.forName("java.beans.MetaData$" + name.replace('.', '_')
                                        + "_PersistenceDelegate");
                pd = (PersistenceDelegate)c.newInstance();
                internalPersistenceDelegates.put(typeName, pd);
            }
            catch (ClassNotFoundException e) {
                String[] properties = getConstructorProperties(type);
                if (properties != null) {
                    pd = new DefaultPersistenceDelegate(properties);
                    internalPersistenceDelegates.put(typeName, pd);
                }
            }
            catch (Exception e) {
                System.err.println("Internal error: " + e);
            }
        }

        return (pd != null) ? pd : defaultPersistenceDelegate;
    }

    private static String[] getConstructorProperties(Class<?> type) {
        String[] names = null;
        int length = 0;
        for (Constructor<?> constructor : type.getConstructors()) {
            String[] value = getAnnotationValue(constructor);
            if ((value != null) && (length < value.length) && isValid(constructor, value)) {
                names = value;
                length = value.length;
            }
        }
        return names;
    }

    private static String[] getAnnotationValue(Constructor<?> constructor) {
        ConstructorProperties annotation = constructor.getAnnotation(ConstructorProperties.class);
        return (annotation != null)
                ? annotation.value()
                : null;
    }

    private static boolean isValid(Constructor<?> constructor, String[] names) {
        Class[] parameters = constructor.getParameterTypes();
        if (names.length != parameters.length) {
            return false;
        }
        for (String name : names) {
            if (name == null) {
                return false;
            }
        }
        return true;
    }

    private static Object getBeanAttribute(Class<?> type, String attribute) {
        try {
            return Introspector.getBeanInfo(type).getBeanDescriptor().getValue(attribute);
        } catch (IntrospectionException exception) {
            return null;
        }
    }

    static Object getPrivateFieldValue(Object instance, String name) {
        Field field = fields.get(name);
        if (field == null) {
            int index = name.lastIndexOf('.');
            final String className = name.substring(0, index);
            final String fieldName = name.substring(1 + index);
            field = AccessController.doPrivileged(new PrivilegedAction<Field>() {
                public Field run() {
                    try {
                        Field field = Class.forName(className).getDeclaredField(fieldName);
                        field.setAccessible(true);
                        return field;
                    }
                    catch (ClassNotFoundException exception) {
                        throw new IllegalStateException("Could not find class", exception);
                    }
                    catch (NoSuchFieldException exception) {
                        throw new IllegalStateException("Could not find field", exception);
                    }
                }
            });
            fields.put(name, field);
        }
        try {
            return field.get(instance);
        }
        catch (IllegalAccessException exception) {
            throw new IllegalStateException("Could not get value of the field", exception);
        }
    }
}
