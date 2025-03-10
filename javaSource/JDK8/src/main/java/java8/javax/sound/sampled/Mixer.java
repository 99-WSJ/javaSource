/*
 * Copyright (c) 1999, 2013, Oracle and/or its affiliates. All rights reserved.
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


import javax.sound.sampled.*;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Port;
import javax.sound.sampled.SourceDataLine;

/**
 * A mixer is an audio device with one or more lines.  It need not be
 * designed for mixing audio signals.  A mixer that actually mixes audio
 * has multiple input (source) lines and at least one output (target) line.
 * The former are often instances of classes that implement
 * <code>{@link SourceDataLine}</code>,
 * and the latter, <code>{@link TargetDataLine}</code>.  <code>{@link Port}</code>
 * objects, too, are either source lines or target lines.
 * A mixer can accept prerecorded, loopable sound as input, by having
 * some of its source lines be instances of objects that implement the
 * <code>{@link Clip}</code> interface.
 * <p>
 * Through methods of the <code>Line</code> interface, which <code>Mixer</code> extends,
 * a mixer might provide a set of controls that are global to the mixer.  For example,
 * the mixer can have a master gain control.  These global controls are distinct
 * from the controls belonging to each of the mixer's individual lines.
 * <p>
 * Some mixers, especially
 * those with internal digital mixing capabilities, may provide
 * additional capabilities by implementing the <code>DataLine</code> interface.
 * <p>
 * A mixer can support synchronization of its lines.  When one line in
 * a synchronized group is started or stopped, the other lines in the group
 * automatically start or stop simultaneously with the explicitly affected one.
 *
 * @author Kara Kytle
 * @since 1.3
 */
public interface Mixer extends Line {

    /**
     * Obtains information about this mixer, including the product's name,
     * version, vendor, etc.
     * @return a mixer info object that describes this mixer
     * @see javax.sound.sampled.Mixer.Info
     */
    public Info getMixerInfo();


    /**
     * Obtains information about the set of source lines supported
     * by this mixer.
     * Some source lines may only be available when this mixer is open.
     * @return array of <code>Line.Info</code> objects representing source lines
     * for this mixer.  If no source lines are supported,
     * an array of length 0 is returned.
     */
    public Line.Info[] getSourceLineInfo();

    /**
     * Obtains information about the set of target lines supported
     * by this mixer.
     * Some target lines may only be available when this mixer is open.
     * @return array of <code>Line.Info</code> objects representing target lines
     * for this mixer.  If no target lines are supported,
     * an array of length 0 is returned.
     */
    public Line.Info[] getTargetLineInfo();


    /**
     * Obtains information about source lines of a particular type supported
     * by the mixer.
     * Some source lines may only be available when this mixer is open.
     * @param info a <code>Line.Info</code> object describing lines about which information
     * is queried
     * @return an array of <code>Line.Info</code> objects describing source lines matching
     * the type requested.  If no matching source lines are supported, an array of length 0
     * is returned.
     */
    public Line.Info[] getSourceLineInfo(Line.Info info);


    /**
     * Obtains information about target lines of a particular type supported
     * by the mixer.
     * Some target lines may only be available when this mixer is open.
     * @param info a <code>Line.Info</code> object describing lines about which information
     * is queried
     * @return an array of <code>Line.Info</code> objects describing target lines matching
     * the type requested.  If no matching target lines are supported, an array of length 0
     * is returned.
     */
    public Line.Info[] getTargetLineInfo(Line.Info info);


    /**
     * Indicates whether the mixer supports a line (or lines) that match
     * the specified <code>Line.Info</code> object.
     * Some lines may only be supported when this mixer is open.
     * @param info describes the line for which support is queried
     * @return <code>true</code> if at least one matching line is
     * supported, <code>false</code> otherwise
     */
    public boolean isLineSupported(Line.Info info);

    /**
     * Obtains a line that is available for use and that matches the description
     * in the specified <code>Line.Info</code> object.
     *
     * <p>If a <code>DataLine</code> is requested, and <code>info</code>
     * is an instance of <code>DataLine.Info</code> specifying at
     * least one fully qualified audio format, the last one
     * will be used as the default format of the returned
     * <code>DataLine</code>.
     *
     * @param info describes the desired line
     * @return a line that is available for use and that matches the description
     * in the specified {@code Line.Info} object
     * @throws LineUnavailableException if a matching line
     * is not available due to resource restrictions
     * @throws IllegalArgumentException if this mixer does
     * not support any lines matching the description
     * @throws SecurityException if a matching line
     * is not available due to security restrictions
     */
    public Line getLine(Line.Info info) throws LineUnavailableException;

    //$$fb 2002-04-12: fix for 4667258: behavior of Mixer.getMaxLines(Line.Info) method doesn't match the spec
    /**
     * Obtains the approximate maximum number of lines of the requested type that can be open
     * simultaneously on the mixer.
     *
     * Certain types of mixers do not have a hard bound and may allow opening more lines.
     * Since certain lines are a shared resource, a mixer may not be able to open the maximum
     * number of lines if another process has opened lines of this mixer.
     *
     * The requested type is any line that matches the description in
     * the provided <code>Line.Info</code> object.  For example, if the info
     * object represents a speaker
     * port, and the mixer supports exactly one speaker port, this method
     * should return 1.  If the info object represents a source data line
     * and the mixer supports the use of 32 source data lines simultaneously,
     * the return value should be 32.
     * If there is no limit, this function returns <code>AudioSystem.NOT_SPECIFIED</code>.
     * @param info a <code>Line.Info</code> that describes the line for which
     * the number of supported instances is queried
     * @return the maximum number of matching lines supported, or <code>AudioSystem.NOT_SPECIFIED</code>
     */
    public int getMaxLines(Line.Info info);


    /**
     * Obtains the set of all source lines currently open to this mixer.
     *
     * @return the source lines currently open to the mixer.
     * If no source lines are currently open to this mixer,  an
     * array of length 0 is returned.
     * @throws SecurityException if the matching lines
     * are not available due to security restrictions
     */
    public Line[] getSourceLines();

    /**
     * Obtains the set of all target lines currently open from this mixer.
     *
     * @return target lines currently open from the mixer.
     * If no target lines are currently open from this mixer, an
     * array of length 0 is returned.
     * @throws SecurityException if the matching lines
     * are not available due to security restrictions
     */
    public Line[] getTargetLines();

    /**
     * Synchronizes two or more lines.  Any subsequent command that starts or stops
     * audio playback or capture for one of these lines will exert the
     * same effect on the other lines in the group, so that they start or stop playing or
     * capturing data simultaneously.
     *
     * @param lines the lines that should be synchronized
     * @param maintainSync <code>true</code> if the synchronization
     * must be precisely maintained (i.e., the synchronization must be sample-accurate)
     * at all times during operation of the lines , or <code>false</code>
     * if precise synchronization is required only during start and stop operations
     *
     * @throws IllegalArgumentException if the lines cannot be synchronized.
     * This may occur if the lines are of different types or have different
     * formats for which this mixer does not support synchronization, or if
     * all lines specified do not belong to this mixer.
     */
    public void synchronize(Line[] lines, boolean maintainSync);

    /**
     * Releases synchronization for the specified lines.  The array must
     * be identical to one for which synchronization has already been
     * established; otherwise an exception may be thrown.  However, <code>null</code>
     * may be specified, in which case all currently synchronized lines that belong
     * to this mixer are unsynchronized.
     * @param lines the synchronized lines for which synchronization should be
     * released, or <code>null</code> for all this mixer's synchronized lines
     *
     * @throws IllegalArgumentException if the lines cannot be unsynchronized.
     * This may occur if the argument specified does not exactly match a set
     * of lines for which synchronization has already been established.
     */
    public void unsynchronize(Line[] lines);


    /**
     * Reports whether this mixer supports synchronization of the specified set of lines.
     *
     * @param lines the set of lines for which synchronization support is queried
     * @param maintainSync <code>true</code> if the synchronization
     * must be precisely maintained (i.e., the synchronization must be sample-accurate)
     * at all times during operation of the lines , or <code>false</code>
     * if precise synchronization is required only during start and stop operations
     *
     * @return <code>true</code> if the lines can be synchronized, <code>false</code>
     * otherwise
     */
    public boolean isSynchronizationSupported(Line[] lines, boolean maintainSync);


    /**
     * The <code>Mixer.Info</code> class represents information about an audio mixer,
     * including the product's name, version, and vendor, along with a textual
     * description.  This information may be retrieved through the
     * {@link javax.sound.sampled.Mixer#getMixerInfo() getMixerInfo}
     * method of the <code>Mixer</code> interface.
     *
     * @author Kara Kytle
     * @since 1.3
     */
    public static class Info {

        /**
         * Mixer name.
         */
        private final String name;

        /**
         * Mixer vendor.
         */
        private final String vendor;

        /**
         * Mixer description.
         */
        private final String description;

        /**
         * Mixer version.
         */
        private final String version;

        /**
         * Constructs a mixer's info object, passing it the given
         * textual information.
         * @param name the name of the mixer
         * @param vendor the company who manufactures or creates the hardware
         * or software mixer
         * @param description descriptive text about the mixer
         * @param version version information for the mixer
         */
        protected Info(String name, String vendor, String description, String version) {

            this.name = name;
            this.vendor = vendor;
            this.description = description;
            this.version = version;
        }


        /**
         * Indicates whether two info objects are equal, returning <code>true</code> if
         * they are identical.
         * @param obj the reference object with which to compare this info
         * object
         * @return <code>true</code> if this info object is the same as the
         * <code>obj</code> argument; <code>false</code> otherwise
         */
        public final boolean equals(Object obj) {
            return super.equals(obj);
        }

        /**
         * Finalizes the hashcode method.
         *
         * @return the hashcode for this object
         */
        public final int hashCode() {
            return super.hashCode();
        }

        /**
         * Obtains the name of the mixer.
         * @return a string that names the mixer
         */
        public final String getName() {
            return name;
        }

        /**
         * Obtains the vendor of the mixer.
         * @return a string that names the mixer's vendor
         */
        public final String getVendor() {
            return vendor;
        }

        /**
         * Obtains the description of the mixer.
         * @return a textual description of the mixer
         */
        public final String getDescription() {
            return description;
        }

        /**
         * Obtains the version of the mixer.
         * @return textual version information for the mixer
         */
        public final String getVersion() {
            return version;
        }

        /**
         * Provides a string representation of the mixer info.
         * @return a string describing the info object
         */
        public final String toString() {
            return (name + ", version " + version);
        }
    } // class Info
}
