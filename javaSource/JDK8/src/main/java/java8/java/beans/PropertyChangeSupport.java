/*
 * Copyright (c) 1996, 2013, Oracle and/or its affiliates. All rights reserved.
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

import java.beans.ChangeListenerMap;
import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeListenerProxy;
import java.beans.VetoableChangeSupport;
import java.io.*;
import java.util.Hashtable;
import java.util.Map.Entry;

/**
 * This is a utility class that can be used by beans that support bound
 * properties.  It manages a list of listeners and dispatches
 * {@link PropertyChangeEvent}s to them.  You can use an instance of this class
 * as a member field of your bean and delegate these types of work to it.
 * The {@link PropertyChangeListener} can be registered for all properties
 * or for a property specified by name.
 * <p>
 * Here is an example of {@code PropertyChangeSupport} usage that follows
 * the rules and recommendations laid out in the JavaBeans&trade; specification:
 * <pre>
 * public class MyBean {
 *     private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
 *
 *     public void addPropertyChangeListener(PropertyChangeListener listener) {
 *         this.pcs.addPropertyChangeListener(listener);
 *     }
 *
 *     public void removePropertyChangeListener(PropertyChangeListener listener) {
 *         this.pcs.removePropertyChangeListener(listener);
 *     }
 *
 *     private String value;
 *
 *     public String getValue() {
 *         return this.value;
 *     }
 *
 *     public void setValue(String newValue) {
 *         String oldValue = this.value;
 *         this.value = newValue;
 *         this.pcs.firePropertyChange("value", oldValue, newValue);
 *     }
 *
 *     [...]
 * }
 * </pre>
 * <p>
 * A {@code PropertyChangeSupport} instance is thread-safe.
 * <p>
 * This class is serializable.  When it is serialized it will save
 * (and restore) any listeners that are themselves serializable.  Any
 * non-serializable listeners will be skipped during serialization.
 *
 * @see VetoableChangeSupport
 */
public class PropertyChangeSupport implements Serializable {
    private PropertyChangeListenerMap map = new PropertyChangeListenerMap();

    /**
     * Constructs a <code>PropertyChangeSupport</code> object.
     *
     * @param sourceBean  The bean to be given as the source for any events.
     */
    public PropertyChangeSupport(Object sourceBean) {
        if (sourceBean == null) {
            throw new NullPointerException();
        }
        source = sourceBean;
    }

    /**
     * Add a PropertyChangeListener to the listener list.
     * The listener is registered for all properties.
     * The same listener object may be added more than once, and will be called
     * as many times as it is added.
     * If <code>listener</code> is null, no exception is thrown and no action
     * is taken.
     *
     * @param listener  The PropertyChangeListener to be added
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (listener == null) {
            return;
        }
        if (listener instanceof PropertyChangeListenerProxy) {
            PropertyChangeListenerProxy proxy =
                   (PropertyChangeListenerProxy)listener;
            // Call two argument add method.
            addPropertyChangeListener(proxy.getPropertyName(),
                                      proxy.getListener());
        } else {
            this.map.add(null, listener);
        }
    }

    /**
     * Remove a PropertyChangeListener from the listener list.
     * This removes a PropertyChangeListener that was registered
     * for all properties.
     * If <code>listener</code> was added more than once to the same event
     * source, it will be notified one less time after being removed.
     * If <code>listener</code> is null, or was never added, no exception is
     * thrown and no action is taken.
     *
     * @param listener  The PropertyChangeListener to be removed
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        if (listener == null) {
            return;
        }
        if (listener instanceof PropertyChangeListenerProxy) {
            PropertyChangeListenerProxy proxy =
                    (PropertyChangeListenerProxy)listener;
            // Call two argument remove method.
            removePropertyChangeListener(proxy.getPropertyName(),
                                         proxy.getListener());
        } else {
            this.map.remove(null, listener);
        }
    }

    /**
     * Returns an array of all the listeners that were added to the
     * PropertyChangeSupport object with addPropertyChangeListener().
     * <p>
     * If some listeners have been added with a named property, then
     * the returned array will be a mixture of PropertyChangeListeners
     * and <code>PropertyChangeListenerProxy</code>s. If the calling
     * method is interested in distinguishing the listeners then it must
     * test each element to see if it's a
     * <code>PropertyChangeListenerProxy</code>, perform the cast, and examine
     * the parameter.
     *
     * <pre>{@code
     * PropertyChangeListener[] listeners = bean.getPropertyChangeListeners();
     * for (int i = 0; i < listeners.length; i++) {
     *   if (listeners[i] instanceof PropertyChangeListenerProxy) {
     *     PropertyChangeListenerProxy proxy =
     *                    (PropertyChangeListenerProxy)listeners[i];
     *     if (proxy.getPropertyName().equals("foo")) {
     *       // proxy is a PropertyChangeListener which was associated
     *       // with the property named "foo"
     *     }
     *   }
     * }
     * }</pre>
     *
     * @see PropertyChangeListenerProxy
     * @return all of the <code>PropertyChangeListeners</code> added or an
     *         empty array if no listeners have been added
     * @since 1.4
     */
    public PropertyChangeListener[] getPropertyChangeListeners() {
        return this.map.getListeners();
    }

    /**
     * Add a PropertyChangeListener for a specific property.  The listener
     * will be invoked only when a call on firePropertyChange names that
     * specific property.
     * The same listener object may be added more than once.  For each
     * property,  the listener will be invoked the number of times it was added
     * for that property.
     * If <code>propertyName</code> or <code>listener</code> is null, no
     * exception is thrown and no action is taken.
     *
     * @param propertyName  The name of the property to listen on.
     * @param listener  The PropertyChangeListener to be added
     */
    public void addPropertyChangeListener(
                String propertyName,
                PropertyChangeListener listener) {
        if (listener == null || propertyName == null) {
            return;
        }
        listener = this.map.extract(listener);
        if (listener != null) {
            this.map.add(propertyName, listener);
        }
    }

    /**
     * Remove a PropertyChangeListener for a specific property.
     * If <code>listener</code> was added more than once to the same event
     * source for the specified property, it will be notified one less time
     * after being removed.
     * If <code>propertyName</code> is null,  no exception is thrown and no
     * action is taken.
     * If <code>listener</code> is null, or was never added for the specified
     * property, no exception is thrown and no action is taken.
     *
     * @param propertyName  The name of the property that was listened on.
     * @param listener  The PropertyChangeListener to be removed
     */
    public void removePropertyChangeListener(
                String propertyName,
                PropertyChangeListener listener) {
        if (listener == null || propertyName == null) {
            return;
        }
        listener = this.map.extract(listener);
        if (listener != null) {
            this.map.remove(propertyName, listener);
        }
    }

    /**
     * Returns an array of all the listeners which have been associated
     * with the named property.
     *
     * @param propertyName  The name of the property being listened to
     * @return all of the <code>PropertyChangeListeners</code> associated with
     *         the named property.  If no such listeners have been added,
     *         or if <code>propertyName</code> is null, an empty array is
     *         returned.
     * @since 1.4
     */
    public PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
        return this.map.getListeners(propertyName);
    }

    /**
     * Reports a bound property update to listeners
     * that have been registered to track updates of
     * all properties or a property with the specified name.
     * <p>
     * No event is fired if old and new values are equal and non-null.
     * <p>
     * This is merely a convenience wrapper around the more general
     * {@link #firePropertyChange(PropertyChangeEvent)} method.
     *
     * @param propertyName  the programmatic name of the property that was changed
     * @param oldValue      the old value of the property
     * @param newValue      the new value of the property
     */
    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        if (oldValue == null || newValue == null || !oldValue.equals(newValue)) {
            firePropertyChange(new PropertyChangeEvent(this.source, propertyName, oldValue, newValue));
        }
    }

    /**
     * Reports an integer bound property update to listeners
     * that have been registered to track updates of
     * all properties or a property with the specified name.
     * <p>
     * No event is fired if old and new values are equal.
     * <p>
     * This is merely a convenience wrapper around the more general
     * {@link #firePropertyChange(String, Object, Object)}  method.
     *
     * @param propertyName  the programmatic name of the property that was changed
     * @param oldValue      the old value of the property
     * @param newValue      the new value of the property
     */
    public void firePropertyChange(String propertyName, int oldValue, int newValue) {
        if (oldValue != newValue) {
            firePropertyChange(propertyName, Integer.valueOf(oldValue), Integer.valueOf(newValue));
        }
    }

    /**
     * Reports a boolean bound property update to listeners
     * that have been registered to track updates of
     * all properties or a property with the specified name.
     * <p>
     * No event is fired if old and new values are equal.
     * <p>
     * This is merely a convenience wrapper around the more general
     * {@link #firePropertyChange(String, Object, Object)}  method.
     *
     * @param propertyName  the programmatic name of the property that was changed
     * @param oldValue      the old value of the property
     * @param newValue      the new value of the property
     */
    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        if (oldValue != newValue) {
            firePropertyChange(propertyName, Boolean.valueOf(oldValue), Boolean.valueOf(newValue));
        }
    }

    /**
     * Fires a property change event to listeners
     * that have been registered to track updates of
     * all properties or a property with the specified name.
     * <p>
     * No event is fired if the given event's old and new values are equal and non-null.
     *
     * @param event  the {@code PropertyChangeEvent} to be fired
     */
    public void firePropertyChange(PropertyChangeEvent event) {
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();
        if (oldValue == null || newValue == null || !oldValue.equals(newValue)) {
            String name = event.getPropertyName();

            PropertyChangeListener[] common = this.map.get(null);
            PropertyChangeListener[] named = (name != null)
                        ? this.map.get(name)
                        : null;

            fire(common, event);
            fire(named, event);
        }
    }

    private static void fire(PropertyChangeListener[] listeners, PropertyChangeEvent event) {
        if (listeners != null) {
            for (PropertyChangeListener listener : listeners) {
                listener.propertyChange(event);
            }
        }
    }

    /**
     * Reports a bound indexed property update to listeners
     * that have been registered to track updates of
     * all properties or a property with the specified name.
     * <p>
     * No event is fired if old and new values are equal and non-null.
     * <p>
     * This is merely a convenience wrapper around the more general
     * {@link #firePropertyChange(PropertyChangeEvent)} method.
     *
     * @param propertyName  the programmatic name of the property that was changed
     * @param index         the index of the property element that was changed
     * @param oldValue      the old value of the property
     * @param newValue      the new value of the property
     * @since 1.5
     */
    public void fireIndexedPropertyChange(String propertyName, int index, Object oldValue, Object newValue) {
        if (oldValue == null || newValue == null || !oldValue.equals(newValue)) {
            firePropertyChange(new IndexedPropertyChangeEvent(source, propertyName, oldValue, newValue, index));
        }
    }

    /**
     * Reports an integer bound indexed property update to listeners
     * that have been registered to track updates of
     * all properties or a property with the specified name.
     * <p>
     * No event is fired if old and new values are equal.
     * <p>
     * This is merely a convenience wrapper around the more general
     * {@link #fireIndexedPropertyChange(String, int, Object, Object)} method.
     *
     * @param propertyName  the programmatic name of the property that was changed
     * @param index         the index of the property element that was changed
     * @param oldValue      the old value of the property
     * @param newValue      the new value of the property
     * @since 1.5
     */
    public void fireIndexedPropertyChange(String propertyName, int index, int oldValue, int newValue) {
        if (oldValue != newValue) {
            fireIndexedPropertyChange(propertyName, index, Integer.valueOf(oldValue), Integer.valueOf(newValue));
        }
    }

    /**
     * Reports a boolean bound indexed property update to listeners
     * that have been registered to track updates of
     * all properties or a property with the specified name.
     * <p>
     * No event is fired if old and new values are equal.
     * <p>
     * This is merely a convenience wrapper around the more general
     * {@link #fireIndexedPropertyChange(String, int, Object, Object)} method.
     *
     * @param propertyName  the programmatic name of the property that was changed
     * @param index         the index of the property element that was changed
     * @param oldValue      the old value of the property
     * @param newValue      the new value of the property
     * @since 1.5
     */
    public void fireIndexedPropertyChange(String propertyName, int index, boolean oldValue, boolean newValue) {
        if (oldValue != newValue) {
            fireIndexedPropertyChange(propertyName, index, Boolean.valueOf(oldValue), Boolean.valueOf(newValue));
        }
    }

    /**
     * Check if there are any listeners for a specific property, including
     * those registered on all properties.  If <code>propertyName</code>
     * is null, only check for listeners registered on all properties.
     *
     * @param propertyName  the property name.
     * @return true if there are one or more listeners for the given property
     */
    public boolean hasListeners(String propertyName) {
        return this.map.hasListeners(propertyName);
    }

    /**
     * @serialData Null terminated list of <code>PropertyChangeListeners</code>.
     * <p>
     * At serialization time we skip non-serializable listeners and
     * only serialize the serializable listeners.
     */
    private void writeObject(ObjectOutputStream s) throws IOException {
        Hashtable<String, java.beans.PropertyChangeSupport> children = null;
        PropertyChangeListener[] listeners = null;
        synchronized (this.map) {
            for (Entry<String, PropertyChangeListener[]> entry : this.map.getEntries()) {
                String property = entry.getKey();
                if (property == null) {
                    listeners = entry.getValue();
                } else {
                    if (children == null) {
                        children = new Hashtable<>();
                    }
                    java.beans.PropertyChangeSupport pcs = new java.beans.PropertyChangeSupport(this.source);
                    pcs.map.set(null, entry.getValue());
                    children.put(property, pcs);
                }
            }
        }
        ObjectOutputStream.PutField fields = s.putFields();
        fields.put("children", children);
        fields.put("source", this.source);
        fields.put("propertyChangeSupportSerializedDataVersion", 2);
        s.writeFields();

        if (listeners != null) {
            for (PropertyChangeListener l : listeners) {
                if (l instanceof Serializable) {
                    s.writeObject(l);
                }
            }
        }
        s.writeObject(null);
    }

    private void readObject(ObjectInputStream s) throws ClassNotFoundException, IOException {
        this.map = new PropertyChangeListenerMap();

        ObjectInputStream.GetField fields = s.readFields();

        @SuppressWarnings("unchecked")
        Hashtable<String, java.beans.PropertyChangeSupport> children = (Hashtable<String, java.beans.PropertyChangeSupport>) fields.get("children", null);
        this.source = fields.get("source", null);
        fields.get("propertyChangeSupportSerializedDataVersion", 2);

        Object listenerOrNull;
        while (null != (listenerOrNull = s.readObject())) {
            this.map.add(null, (PropertyChangeListener)listenerOrNull);
        }
        if (children != null) {
            for (Entry<String, java.beans.PropertyChangeSupport> entry : children.entrySet()) {
                for (PropertyChangeListener listener : entry.getValue().getPropertyChangeListeners()) {
                    this.map.add(entry.getKey(), listener);
                }
            }
        }
    }

    /**
     * The object to be provided as the "source" for any generated events.
     */
    private Object source;

    /**
     * @serialField children                                   Hashtable
     * @serialField source                                     Object
     * @serialField propertyChangeSupportSerializedDataVersion int
     */
    private static final ObjectStreamField[] serialPersistentFields = {
            new ObjectStreamField("children", Hashtable.class),
            new ObjectStreamField("source", Object.class),
            new ObjectStreamField("propertyChangeSupportSerializedDataVersion", Integer.TYPE)
    };

    /**
     * Serialization version ID, so we're compatible with JDK 1.1
     */
    static final long serialVersionUID = 6401253773779951803L;

    /**
     * This is a {@link ChangeListenerMap ChangeListenerMap} implementation
     * that works with {@link PropertyChangeListener PropertyChangeListener} objects.
     */
    private static final class PropertyChangeListenerMap extends ChangeListenerMap<PropertyChangeListener> {
        private static final PropertyChangeListener[] EMPTY = {};

        /**
         * Creates an array of {@link PropertyChangeListener PropertyChangeListener} objects.
         * This method uses the same instance of the empty array
         * when {@code length} equals {@code 0}.
         *
         * @param length  the array length
         * @return        an array with specified length
         */
        @Override
        protected PropertyChangeListener[] newArray(int length) {
            return (0 < length)
                    ? new PropertyChangeListener[length]
                    : EMPTY;
        }

        /**
         * Creates a {@link PropertyChangeListenerProxy PropertyChangeListenerProxy}
         * object for the specified property.
         *
         * @param name      the name of the property to listen on
         * @param listener  the listener to process events
         * @return          a {@code PropertyChangeListenerProxy} object
         */
        @Override
        protected PropertyChangeListener newProxy(String name, PropertyChangeListener listener) {
            return new PropertyChangeListenerProxy(name, listener);
        }

        /**
         * {@inheritDoc}
         */
        public final PropertyChangeListener extract(PropertyChangeListener listener) {
            while (listener instanceof PropertyChangeListenerProxy) {
                listener = ((PropertyChangeListenerProxy) listener).getListener();
            }
            return listener;
        }
    }
}
