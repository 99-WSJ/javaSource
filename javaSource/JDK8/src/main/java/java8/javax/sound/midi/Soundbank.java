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

package java8.javax.sound.midi;

import javax.sound.midi.Instrument;
import javax.sound.midi.Patch;
import javax.sound.midi.SoundbankResource;
import javax.sound.midi.Synthesizer;
import java.net.URL;


/**
 * A <code>Soundbank</code> contains a set of <code>Instruments</code>
 * that can be loaded into a <code>Synthesizer</code>.
 * Note that a Java Sound <code>Soundbank</code> is different from a MIDI bank.
 * MIDI permits up to 16383 banks, each containing up to 128 instruments
 * (also sometimes called programs, patches, or timbres).
 * However, a <code>Soundbank</code> can contain 16383 times 128 instruments,
 * because the instruments within a <code>Soundbank</code> are indexed by both
 * a MIDI program number and a MIDI bank number (via a <code>Patch</code>
 * object). Thus, a <code>Soundbank</code> can be thought of as a collection
 * of MIDI banks.
 * <p>
 * <code>Soundbank</code> includes methods that return <code>String</code>
 * objects containing the sound bank's name, manufacturer, version number, and
 * description.  The precise content and format of these strings is left
 * to the implementor.
 * <p>
 * Different synthesizers use a variety of synthesis techniques.  A common
 * one is wavetable synthesis, in which a segment of recorded sound is
 * played back, often with looping and pitch change.  The Downloadable Sound
 * (DLS) format uses segments of recorded sound, as does the Headspace Engine.
 * <code>Soundbanks</code> and <code>Instruments</code> that are based on
 * wavetable synthesis (or other uses of stored sound recordings) should
 * typically implement the <code>getResources()</code>
 * method to provide access to these recorded segments.  This is optional,
 * however; the method can return an zero-length array if the synthesis technique
 * doesn't use sampled sound (FM synthesis and physical modeling are examples
 * of such techniques), or if it does but the implementor chooses not to make the
 * samples accessible.
 *
 * @see Synthesizer#getDefaultSoundbank
 * @see Synthesizer#isSoundbankSupported
 * @see Synthesizer#loadInstruments(javax.sound.midi.Soundbank, Patch[])
 * @see Patch
 * @see Instrument
 * @see SoundbankResource
 *
 * @author David Rivas
 * @author Kara Kytle
 */

public interface Soundbank {


    /**
     * Obtains the name of the sound bank.
     * @return a <code>String</code> naming the sound bank
     */
    public String getName();

    /**
     * Obtains the version string for the sound bank.
     * @return a <code>String</code> that indicates the sound bank's version
     */
    public String getVersion();

    /**
     * Obtains a <code>string</code> naming the company that provides the
     * sound bank
     * @return the vendor string
     */
    public String getVendor();

    /**
     * Obtains a textual description of the sound bank, suitable for display.
     * @return a <code>String</code> that describes the sound bank
     */
    public String getDescription();


    /**
     * Extracts a list of non-Instrument resources contained in the sound bank.
     * @return an array of resources, excluding instruments.  If the sound bank contains
     * no resources (other than instruments), returns an array of length 0.
     */
    public SoundbankResource[] getResources();


    /**
     * Obtains a list of instruments contained in this sound bank.
     * @return an array of the <code>Instruments</code> in this
     * <code>SoundBank</code>
     * If the sound bank contains no instruments, returns an array of length 0.
     *
     * @see Synthesizer#getLoadedInstruments
     * @see #getInstrument(Patch)
     */
    public Instrument[] getInstruments();

    /**
     * Obtains an <code>Instrument</code> from the given <code>Patch</code>.
     * @param patch a <code>Patch</code> object specifying the bank index
     * and program change number
     * @return the requested instrument, or <code>null</code> if the
     * sound bank doesn't contain that instrument
     *
     * @see #getInstruments
     * @see Synthesizer#loadInstruments(javax.sound.midi.Soundbank, Patch[])
     */
    public Instrument getInstrument(Patch patch);


}
