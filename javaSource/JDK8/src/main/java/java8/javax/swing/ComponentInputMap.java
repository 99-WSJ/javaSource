/*
 * Copyright (c) 1999, 2011, Oracle and/or its affiliates. All rights reserved.
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
package java8.javax.swing;

import javax.swing.*;

/**
 * A <code>ComponentInputMap</code> is an <code>InputMap</code>
 * associated with a particular <code>JComponent</code>.
 * The component is automatically notified whenever
 * the <code>ComponentInputMap</code> changes.
 * <code>ComponentInputMap</code>s are used for
 * <code>WHEN_IN_FOCUSED_WINDOW</code> bindings.
 *
 * @author Scott Violet
 * @since 1.3
 */
@SuppressWarnings("serial")
public class ComponentInputMap extends InputMap {
    /** Component binding is created for. */
    private JComponent          component;

    /**
     * Creates a <code>ComponentInputMap</code> associated with the
     * specified component.
     *
     * @param component  a non-null <code>JComponent</code>
     * @throws IllegalArgumentException  if <code>component</code> is null
     */
    public ComponentInputMap(JComponent component) {
        this.component = component;
        if (component == null) {
            throw new IllegalArgumentException("ComponentInputMaps must be associated with a non-null JComponent");
        }
    }

    /**
     * Sets the parent, which must be a <code>ComponentInputMap</code>
     * associated with the same component as this
     * <code>ComponentInputMap</code>.
     *
     * @param map  a <code>ComponentInputMap</code>
     *
     * @throws IllegalArgumentException  if <code>map</code>
     *         is not a <code>ComponentInputMap</code>
     *         or is not associated with the same component
     */
    public void setParent(InputMap map) {
        if (getParent() == map) {
            return;
        }
        if (map != null && (!(map instanceof javax.swing.ComponentInputMap) ||
                 ((javax.swing.ComponentInputMap)map).getComponent() != getComponent())) {
            throw new IllegalArgumentException("ComponentInputMaps must have a parent ComponentInputMap associated with the same component");
        }
        super.setParent(map);
        getComponent().componentInputMapChanged(this);
    }

    /**
     * Returns the component the <code>InputMap</code> was created for.
     */
    public JComponent getComponent() {
        return component;
    }

    /**
     * Adds a binding for <code>keyStroke</code> to <code>actionMapKey</code>.
     * If <code>actionMapKey</code> is null, this removes the current binding
     * for <code>keyStroke</code>.
     */
    public void put(KeyStroke keyStroke, Object actionMapKey) {
        super.put(keyStroke, actionMapKey);
        if (getComponent() != null) {
            getComponent().componentInputMapChanged(this);
        }
    }

    /**
     * Removes the binding for <code>key</code> from this object.
     */
    public void remove(KeyStroke key) {
        super.remove(key);
        if (getComponent() != null) {
            getComponent().componentInputMapChanged(this);
        }
    }

    /**
     * Removes all the mappings from this object.
     */
    public void clear() {
        int oldSize = size();
        super.clear();
        if (oldSize > 0 && getComponent() != null) {
            getComponent().componentInputMapChanged(this);
        }
    }
}
