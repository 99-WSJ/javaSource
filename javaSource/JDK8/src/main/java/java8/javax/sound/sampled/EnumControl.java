/*
 * Copyright (c) 1999, 2003, Oracle and/or its affiliates. All rights reserved.
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

package java8.javax.sound.sampled;

import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.Control;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.ReverbType;

/**
 * A <code>EnumControl</code> provides control over a set of
 * discrete possible values, each represented by an object.  In a
 * graphical user interface, such a control might be represented
 * by a set of buttons, each of which chooses one value or setting.  For
 * example, a reverb control might provide several preset reverberation
 * settings, instead of providing continuously adjustable parameters
 * of the sort that would be represented by <code>{@link FloatControl}</code>
 * objects.
 * <p>
 * Controls that provide a choice between only two settings can often be implemented
 * instead as a <code>{@link BooleanControl}</code>, and controls that provide
 * a set of values along some quantifiable dimension might be implemented
 * instead as a <code>FloatControl</code> with a coarse resolution.
 * However, a key feature of <code>EnumControl</code> is that the returned values
 * are arbitrary objects, rather than numerical or boolean values.  This means that each
 * returned object can provide further information.  As an example, the settings
 * of a <code>{@link javax.sound.sampled.EnumControl.Type#REVERB REVERB}</code> control are instances of
 * <code>{@link ReverbType}</code> that can be queried for the parameter values
 * used for each setting.
 *
 * @author Kara Kytle
 * @since 1.3
 */
public abstract class EnumControl extends Control {


    // TYPE DEFINES


    // INSTANCE VARIABLES


    /**
     * The set of possible values.
     */
    private Object[] values;


    /**
     * The current value.
     */
    private Object value;



    // CONSTRUCTORS


    /**
     * Constructs a new enumerated control object with the given parameters.
     *
     * @param type the type of control represented this enumerated control object
     * @param values the set of possible values for the control
     * @param value the initial control value
     */
    protected EnumControl(Type type, Object[] values, Object value) {

        super(type);

        this.values = values;
        this.value = value;
    }



    // METHODS


    /**
     * Sets the current value for the control.  The default implementation
     * simply sets the value as indicated.  If the value indicated is not
     * supported, an IllegalArgumentException is thrown.
     * Some controls require that their line be open before they can be affected
     * by setting a value.
     * @param value the desired new value
     * @throws IllegalArgumentException if the value indicated does not fall
     * within the allowable range
     */
    public void setValue(Object value) {
        if (!isValueSupported(value)) {
            throw new IllegalArgumentException("Requested value " + value + " is not supported.");
        }

        this.value = value;
    }


    /**
     * Obtains this control's current value.
     * @return the current value
     */
    public Object getValue() {
        return value;
    }


    /**
     * Returns the set of possible values for this control.
     * @return the set of possible values
     */
    public Object[] getValues() {

        Object[] localArray = new Object[values.length];

        for (int i = 0; i < values.length; i++) {
            localArray[i] = values[i];
        }

        return localArray;
    }


    /**
     * Indicates whether the value specified is supported.
     * @param value the value for which support is queried
     * @return <code>true</code> if the value is supported,
     * otherwise <code>false</code>
     */
    private boolean isValueSupported(Object value) {

        for (int i = 0; i < values.length; i++) {
            //$$fb 2001-07-20: Fix for bug 4400392: setValue() in ReverbControl always throws Exception
            //if (values.equals(values[i])) {
            if (value.equals(values[i])) {
                return true;
            }
        }

        return false;
    }



    // ABSTRACT METHOD IMPLEMENTATIONS: CONTROL


    /**
     * Provides a string representation of the control.
     * @return a string description
     */
    public String toString() {
        return new String(getType() + " with current value: " + getValue());
    }


    // INNER CLASSES


    /**
     * An instance of the <code>EnumControl.Type</code> inner class identifies one kind of
     * enumerated control.  Static instances are provided for the
     * common types.
     *
     * @see javax.sound.sampled.EnumControl
     *
     * @author Kara Kytle
     * @since 1.3
     */
    public static class Type extends Control.Type {


        // TYPE DEFINES

        /**
         * Represents a control over a set of possible reverberation settings.
         * Each reverberation setting is described by an instance of the
         * {@link ReverbType} class.  (To access these settings,
         * invoke <code>{@link javax.sound.sampled.EnumControl#getValues}</code> on an
         * enumerated control of type <code>REVERB</code>.)
         */
        public static final Type REVERB         = new Type("Reverb");


        // CONSTRUCTOR


        /**
         * Constructs a new enumerated control type.
         * @param name  the name of the new enumerated control type
         */
        protected Type(String name) {
            super(name);
        }
    } // class Type

} // class EnumControl
