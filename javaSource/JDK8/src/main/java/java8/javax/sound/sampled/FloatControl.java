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

import javax.sound.sampled.Control;
import javax.sound.sampled.EnumControl;
import javax.sound.sampled.Line;

/**
 * A <code>FloatControl</code> object provides control over a range of
 * floating-point values.  Float controls are often
 * represented in graphical user interfaces by continuously
 * adjustable objects such as sliders or rotary knobs.  Concrete subclasses
 * of <code>FloatControl</code> implement controls, such as gain and pan, that
 * affect a line's audio signal in some way that an application can manipulate.
 * The <code>{@link javax.sound.sampled.FloatControl.Type}</code>
 * inner class provides static instances of types that are used to
 * identify some common kinds of float control.
 * <p>
 * The <code>FloatControl</code> abstract class provides methods to set and get
 * the control's current floating-point value.  Other methods obtain the possible
 * range of values and the control's resolution (the smallest increment between
 * returned values).  Some float controls allow ramping to a
 * new value over a specified period of time.  <code>FloatControl</code> also
 * includes methods that return string labels for the minimum, maximum, and midpoint
 * positions of the control.
 *
 * @see Line#getControls
 * @see Line#isControlSupported
 *
 * @author David Rivas
 * @author Kara Kytle
 * @since 1.3
 */
public abstract class FloatControl extends Control {


    // INSTANCE VARIABLES


    // FINAL VARIABLES

    /**
     * The minimum supported value.
     */
    private float minimum;

    /**
     * The maximum supported value.
     */
    private float maximum;

    /**
     * The control's precision.
     */
    private float precision;

    /**
     * The smallest time increment in which a value change
     * can be effected during a value shift, in microseconds.
     */
    private int updatePeriod;


    /**
     * A label for the units in which the control values are expressed,
     * such as "dB" for decibels.
     */
    private final String units;

    /**
     * A label for the minimum value, such as "Left."
     */
    private final String minLabel;

    /**
     * A label for the maximum value, such as "Right."
     */
    private final String maxLabel;

    /**
     * A label for the mid-point value, such as "Center."
     */
    private final String midLabel;


    // STATE VARIABLES

    /**
     * The current value.
     */
    private float value;



    // CONSTRUCTORS


    /**
     * Constructs a new float control object with the given parameters
     *
     * @param type the kind of control represented by this float control object
     * @param minimum the smallest value permitted for the control
     * @param maximum the largest value permitted for the control
     * @param precision the resolution or granularity of the control.
     * This is the size of the increment between discrete valid values.
     * @param updatePeriod the smallest time interval, in microseconds, over which the control
     * can change from one discrete value to the next during a {@link #shift(float,float,int) shift}
     * @param initialValue the value that the control starts with when constructed
     * @param units the label for the units in which the control's values are expressed,
     * such as "dB" or "frames per second"
     * @param minLabel the label for the minimum value, such as "Left" or "Off"
     * @param midLabel the label for the midpoint value, such as "Center" or "Default"
     * @param maxLabel the label for the maximum value, such as "Right" or "Full"
     *
     * @throws IllegalArgumentException if {@code minimum} is greater
     *     than {@code maximum} or {@code initialValue} does not fall
     *     within the allowable range
     */
    protected FloatControl(Type type, float minimum, float maximum,
            float precision, int updatePeriod, float initialValue,
            String units, String minLabel, String midLabel, String maxLabel) {

        super(type);

        if (minimum > maximum) {
            throw new IllegalArgumentException("Minimum value " + minimum
                    + " exceeds maximum value " + maximum + ".");
        }
        if (initialValue < minimum) {
            throw new IllegalArgumentException("Initial value " + initialValue
                    + " smaller than allowable minimum value " + minimum + ".");
        }
        if (initialValue > maximum) {
            throw new IllegalArgumentException("Initial value " + initialValue
                    + " exceeds allowable maximum value " + maximum + ".");
        }


        this.minimum = minimum;
        this.maximum = maximum;

        this.precision = precision;
        this.updatePeriod = updatePeriod;
        this.value = initialValue;

        this.units = units;
        this.minLabel = ( (minLabel == null) ? "" : minLabel);
        this.midLabel = ( (midLabel == null) ? "" : midLabel);
        this.maxLabel = ( (maxLabel == null) ? "" : maxLabel);
    }


    /**
     * Constructs a new float control object with the given parameters.
     * The labels for the minimum, maximum, and mid-point values are set
     * to zero-length strings.
     *
     * @param type the kind of control represented by this float control object
     * @param minimum the smallest value permitted for the control
     * @param maximum the largest value permitted for the control
     * @param precision the resolution or granularity of the control.
     * This is the size of the increment between discrete valid values.
     * @param updatePeriod the smallest time interval, in microseconds, over which the control
     * can change from one discrete value to the next during a {@link #shift(float,float,int) shift}
     * @param initialValue the value that the control starts with when constructed
     * @param units the label for the units in which the control's values are expressed,
     * such as "dB" or "frames per second"
     *
     * @throws IllegalArgumentException if {@code minimum} is greater
     *     than {@code maximum} or {@code initialValue} does not fall
     *     within the allowable range
     */
    protected FloatControl(Type type, float minimum, float maximum,
            float precision, int updatePeriod, float initialValue, String units) {
        this(type, minimum, maximum, precision, updatePeriod,
                initialValue, units, "", "", "");
    }



    // METHODS


    /**
     * Sets the current value for the control.  The default implementation
     * simply sets the value as indicated.  If the value indicated is greater
     * than the maximum value, or smaller than the minimum value, an
     * IllegalArgumentException is thrown.
     * Some controls require that their line be open before they can be affected
     * by setting a value.
     * @param newValue desired new value
     * @throws IllegalArgumentException if the value indicated does not fall
     * within the allowable range
     */
    public void setValue(float newValue) {

        if (newValue > maximum) {
            throw new IllegalArgumentException("Requested value " + newValue + " exceeds allowable maximum value " + maximum + ".");
        }

        if (newValue < minimum) {
            throw new IllegalArgumentException("Requested value " + newValue + " smaller than allowable minimum value " + minimum + ".");
        }

        value = newValue;
    }


    /**
     * Obtains this control's current value.
     * @return the current value
     */
    public float getValue() {
        return value;
    }


    /**
     * Obtains the maximum value permitted.
     * @return the maximum allowable value
     */
    public float getMaximum() {
        return maximum;
    }


    /**
     * Obtains the minimum value permitted.
     * @return the minimum allowable value
     */
    public float getMinimum() {
        return minimum;
    }


    /**
     * Obtains the label for the units in which the control's values are expressed,
     * such as "dB" or "frames per second."
     * @return the units label, or a zero-length string if no label
     */
    public String getUnits() {
        return units;
    }


    /**
     * Obtains the label for the minimum value, such as "Left" or "Off."
     * @return the minimum value label, or a zero-length string if no label      * has been set
     */
    public String getMinLabel() {
        return minLabel;
    }


    /**
     * Obtains the label for the mid-point value, such as "Center" or "Default."
     * @return the mid-point value label, or a zero-length string if no label    * has been set
     */
    public String getMidLabel() {
        return midLabel;
    }


    /**
     * Obtains the label for the maximum value, such as "Right" or "Full."
     * @return the maximum value label, or a zero-length string if no label      * has been set
     */
    public String getMaxLabel() {
        return maxLabel;
    }


    /**
     * Obtains the resolution or granularity of the control, in the units
     * that the control measures.
     * The precision is the size of the increment between discrete valid values
     * for this control, over the set of supported floating-point values.
     * @return the control's precision
     */
    public float getPrecision() {
        return precision;
    }


    /**
     * Obtains the smallest time interval, in microseconds, over which the control's value can
     * change during a shift.  The update period is the inverse of the frequency with which
     * the control updates its value during a shift.  If the implementation does not support value shifting over
     * time, it should set the control's value to the final value immediately
     * and return -1 from this method.
     *
     * @return update period in microseconds, or -1 if shifting over time is unsupported
     * @see #shift
     */
    public int getUpdatePeriod() {
        return updatePeriod;
    }


    /**
     * Changes the control value from the initial value to the final
     * value linearly over the specified time period, specified in microseconds.
     * This method returns without blocking; it does not wait for the shift
     * to complete.  An implementation should complete the operation within the time
     * specified.  The default implementation simply changes the value
     * to the final value immediately.
     *
     * @param from initial value at the beginning of the shift
     * @param to final value after the shift
     * @param microseconds maximum duration of the shift in microseconds
     *
     * @throws IllegalArgumentException if either {@code from} or {@code to}
     *     value does not fall within the allowable range
     *
     * @see #getUpdatePeriod
     */
    public void shift(float from, float to, int microseconds) {
        // test "from" value, "to" value will be tested by setValue()
        if (from < minimum) {
            throw new IllegalArgumentException("Requested value " + from
                    + " smaller than allowable minimum value " + minimum + ".");
        }
        if (from > maximum) {
            throw new IllegalArgumentException("Requested value " + from
                    + " exceeds allowable maximum value " + maximum + ".");
        }
        setValue(to);
    }


    // ABSTRACT METHOD IMPLEMENTATIONS: CONTROL


    /**
     * Provides a string representation of the control
     * @return a string description
     */
    public String toString() {
        return new String(getType() + " with current value: " + getValue() + " " + units +
                          " (range: " + minimum + " - " + maximum + ")");
    }


    // INNER CLASSES


    /**
     * An instance of the <code>FloatControl.Type</code> inner class identifies one kind of
     * float control.  Static instances are provided for the
     * common types.
     *
     * @author Kara Kytle
     * @since 1.3
     */
    public static class Type extends Control.Type {


        // TYPE DEFINES


        // GAIN TYPES

        /**
         * Represents a control for the overall gain on a line.
         * <p>
         * Gain is a quantity in decibels (dB) that is added to the intrinsic
         * decibel level of the audio signal--that is, the level of
         * the signal before it is altered by the gain control.  A positive
         * gain amplifies (boosts) the signal's volume, and a negative gain
         * attenuates (cuts) it.
         * The gain setting defaults to a value of 0.0 dB, meaning the signal's
         * loudness is unaffected.   Note that gain measures dB, not amplitude.
         * The relationship between a gain in decibels and the corresponding
         * linear amplitude multiplier is:
         *
         *<CENTER><CODE> linearScalar = pow(10.0, gainDB/20.0) </CODE></CENTER>
         * <p>
         * The <code>FloatControl</code> class has methods to impose a maximum and
         * minimum allowable value for gain.  However, because an audio signal might
         * already be at a high amplitude, the maximum setting does not guarantee
         * that the signal will be undistorted when the gain is applied to it (unless
         * the maximum is zero or negative). To avoid numeric overflow from excessively
         * large gain settings, a gain control can implement
         * clipping, meaning that the signal's amplitude will be limited to the maximum
         * value representable by its audio format, instead of wrapping around.
         * <p>
         * These comments apply to gain controls in general, not just master gain controls.
         * A line can have more than one gain control.  For example, a mixer (which is
         * itself a line) might have a master gain control, an auxiliary return control,
         * a reverb return control, and, on each of its source lines, an individual aux
         * send and reverb send.
         *
         * @see #AUX_SEND
         * @see #AUX_RETURN
         * @see #REVERB_SEND
         * @see #REVERB_RETURN
         * @see #VOLUME
         */
        public static final Type MASTER_GAIN            = new Type("Master Gain");

        /**
         * Represents a control for the auxiliary send gain on a line.
         *
         * @see #MASTER_GAIN
         * @see #AUX_RETURN
         */
        public static final Type AUX_SEND                       = new Type("AUX Send");

        /**
         * Represents a control for the auxiliary return gain on a line.
         *
         * @see #MASTER_GAIN
         * @see #AUX_SEND
         */
        public static final Type AUX_RETURN                     = new Type("AUX Return");

        /**
         * Represents a control for the pre-reverb gain on a line.
         * This control may be used to affect how much
         * of a line's signal is directed to a mixer's internal reverberation unit.
         *
         * @see #MASTER_GAIN
         * @see #REVERB_RETURN
         * @see EnumControl.Type#REVERB
         */
        public static final Type REVERB_SEND            = new Type("Reverb Send");

        /**
         * Represents a control for the post-reverb gain on a line.
         * This control may be used to control the relative amplitude
         * of the signal returned from an internal reverberation unit.
         *
         * @see #MASTER_GAIN
         * @see #REVERB_SEND
         */
        public static final Type REVERB_RETURN          = new Type("Reverb Return");


        // VOLUME

        /**
         * Represents a control for the volume on a line.
         */
        /*
         * $$kk: 08.30.99: ISSUE: what units?  linear or dB?
         */
        public static final Type VOLUME                         = new Type("Volume");


        // PAN

        /**
         * Represents a control for the relative pan (left-right positioning)
         * of the signal.  The signal may be mono; the pan setting affects how
         * it is distributed by the mixer in a stereo mix.  The valid range of values is -1.0
         * (left channel only) to 1.0 (right channel
         * only).  The default is 0.0 (centered).
         *
         * @see #BALANCE
         */
        public static final Type PAN                            = new Type("Pan");


        // BALANCE

        /**
         * Represents a control for the relative balance of a stereo signal
         * between two stereo speakers.  The valid range of values is -1.0 (left channel only) to 1.0 (right channel
         * only).  The default is 0.0 (centered).
         *
         * @see #PAN
         */
        public static final Type BALANCE                        = new Type("Balance");


        // SAMPLE RATE

        /**
         * Represents a control that changes the sample rate of audio playback.  The net effect
         * of changing the sample rate depends on the relationship between
         * the media's natural rate and the rate that is set via this control.
         * The natural rate is the sample rate that is specified in the data line's
         * <code>AudioFormat</code> object.  For example, if the natural rate
         * of the media is 11025 samples per second and the sample rate is set
         * to 22050 samples per second, the media will play back at twice the
         * normal speed.
         * <p>
         * Changing the sample rate with this control does not affect the data line's
         * audio format.  Also note that whenever you change a sound's sample rate, a
         * change in the sound's pitch results.  For example, doubling the sample
         * rate has the effect of doubling the frequencies in the sound's spectrum,
         * which raises the pitch by an octave.
         */
        public static final Type SAMPLE_RATE            = new Type("Sample Rate");


        // CONSTRUCTOR

        /**
         * Constructs a new float control type.
         * @param name  the name of the new float control type
         */
        protected Type(String name) {
            super(name);
        }

    } // class Type

} // class FloatControl
