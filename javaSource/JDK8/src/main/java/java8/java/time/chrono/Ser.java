/*
 * Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
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

/*
 * Copyright (c) 2011-2012, Stephen Colebourne & Michael Nascimento Santos
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of JSR-310 nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package java8.java.time.chrono;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.AbstractChronology;
import java.time.chrono.HijrahDate;
import java.time.chrono.MinguoDate;

/**
 * The shared serialization delegate for this package.
 *
 * @implNote
 * This class wraps the object being serialized, and takes a byte representing the type of the class to
 * be serialized.  This byte can also be used for versioning the serialization format.  In this case another
 * byte flag would be used in order to specify an alternative version of the type format.
 * For example {@code CHRONO_TYPE_VERSION_2 = 21}
 * <p>
 * In order to serialize the object it writes its byte and then calls back to the appropriate class where
 * the serialization is performed.  In order to deserialize the object it read in the type byte, switching
 * in order to select which class to call back into.
 * <p>
 * The serialization format is determined on a per class basis.  In the case of field based classes each
 * of the fields is written out with an appropriate size format in descending order of the field's size.  For
 * example in the case of {@link LocalDate} year is written before month.  Composite classes, such as
 * {@link LocalDateTime} are serialized as one object.  Enum classes are serialized using the index of their
 * element.
 * <p>
 * This class is mutable and should be created once per serialization.
 *
 * @serial include
 * @since 1.8
 */
final class Ser implements Externalizable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = -6103370247208168577L;

    static final byte CHRONO_TYPE = 1;
    static final byte CHRONO_LOCAL_DATE_TIME_TYPE = 2;
    static final byte CHRONO_ZONE_DATE_TIME_TYPE = 3;
    static final byte JAPANESE_DATE_TYPE = 4;
    static final byte JAPANESE_ERA_TYPE = 5;
    static final byte HIJRAH_DATE_TYPE = 6;
    static final byte MINGUO_DATE_TYPE = 7;
    static final byte THAIBUDDHIST_DATE_TYPE = 8;
    static final byte CHRONO_PERIOD_TYPE = 9;

    /** The type being serialized. */
    private byte type;
    /** The object being serialized. */
    private Object object;

    /**
     * Constructor for deserialization.
     */
    public Ser() {
    }

    /**
     * Creates an instance for serialization.
     *
     * @param type  the type
     * @param object  the object
     */
    Ser(byte type, Object object) {
        this.type = type;
        this.object = object;
    }

    //-----------------------------------------------------------------------
    /**
     * Implements the {@code Externalizable} interface to write the object.
     * @serialData
     * Each serializable class is mapped to a type that is the first byte
     * in the stream.  Refer to each class {@code writeReplace}
     * serialized form for the value of the type and sequence of values for the type.
     * <ul>
     * <li><a href="../../../serialized-form.html#java.time.chrono.HijrahChronology">HijrahChronology.writeReplace</a>
     * <li><a href="../../../serialized-form.html#java.time.chrono.IsoChronology">IsoChronology.writeReplace</a>
     * <li><a href="../../../serialized-form.html#java.time.chrono.JapaneseChronology">JapaneseChronology.writeReplace</a>
     * <li><a href="../../../serialized-form.html#java.time.chrono.MinguoChronology">MinguoChronology.writeReplace</a>
     * <li><a href="../../../serialized-form.html#java.time.chrono.ThaiBuddhistChronology">ThaiBuddhistChronology.writeReplace</a>
     * <li><a href="../../../serialized-form.html#java.time.chrono.ChronoLocalDateTimeImpl">ChronoLocalDateTime.writeReplace</a>
     * <li><a href="../../../serialized-form.html#java.time.chrono.ChronoZonedDateTimeImpl">ChronoZonedDateTime.writeReplace</a>
     * <li><a href="../../../serialized-form.html#java.time.chrono.JapaneseDate">JapaneseDate.writeReplace</a>
     * <li><a href="../../../serialized-form.html#java.time.chrono.JapaneseEra">JapaneseEra.writeReplace</a>
     * <li><a href="../../../serialized-form.html#java.time.chrono.HijrahDate">HijrahDate.writeReplace</a>
     * <li><a href="../../../serialized-form.html#java.time.chrono.MinguoDate">MinguoDate.writeReplace</a>
     * <li><a href="../../../serialized-form.html#java.time.chrono.ThaiBuddhistDate">ThaiBuddhistDate.writeReplace</a>
     * </ul>
     *
     * @param out  the data stream to write to, not null
     */
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        writeInternal(type, object, out);
    }

    private static void writeInternal(byte type, Object object, ObjectOutput out) throws IOException {
        out.writeByte(type);
        switch (type) {
            case CHRONO_TYPE:
                ((AbstractChronology) object).writeExternal(out);
                break;
            case CHRONO_LOCAL_DATE_TIME_TYPE:
                ((ChronoLocalDateTimeImpl<?>) object).writeExternal(out);
                break;
            case CHRONO_ZONE_DATE_TIME_TYPE:
                ((ChronoZonedDateTimeImpl<?>) object).writeExternal(out);
                break;
            case JAPANESE_DATE_TYPE:
                ((JapaneseDate) object).writeExternal(out);
                break;
            case JAPANESE_ERA_TYPE:
                ((JapaneseEra) object).writeExternal(out);
                break;
            case HIJRAH_DATE_TYPE:
                ((HijrahDate) object).writeExternal(out);
                break;
            case MINGUO_DATE_TYPE:
                ((MinguoDate) object).writeExternal(out);
                break;
            case THAIBUDDHIST_DATE_TYPE:
                ((ThaiBuddhistDate) object).writeExternal(out);
                break;
            case CHRONO_PERIOD_TYPE:
                ((java.time.chrono.ChronoPeriodImpl) object).writeExternal(out);
                break;
            default:
                throw new InvalidClassException("Unknown serialized type");
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Implements the {@code Externalizable} interface to read the object.
     * @serialData
     * The streamed type and parameters defined by the type's {@code writeReplace}
     * method are read and passed to the corresponding static factory for the type
     * to create a new instance.  That instance is returned as the de-serialized
     * {@code Ser} object.
     *
     * <ul>
     * <li><a href="../../../serialized-form.html#java.time.chrono.HijrahChronology">HijrahChronology</a> - Chronology.of(id)
     * <li><a href="../../../serialized-form.html#java.time.chrono.IsoChronology">IsoChronology</a> - Chronology.of(id)
     * <li><a href="../../../serialized-form.html#java.time.chrono.JapaneseChronology">JapaneseChronology</a> - Chronology.of(id)
     * <li><a href="../../../serialized-form.html#java.time.chrono.MinguoChronology">MinguoChronology</a> - Chronology.of(id)
     * <li><a href="../../../serialized-form.html#java.time.chrono.ThaiBuddhistChronology">ThaiBuddhistChronology</a> - Chronology.of(id)
     * <li><a href="../../../serialized-form.html#java.time.chrono.ChronoLocalDateTimeImpl">ChronoLocalDateTime</a> - date.atTime(time)
     * <li><a href="../../../serialized-form.html#java.time.chrono.ChronoZonedDateTimeImpl">ChronoZonedDateTime</a> - dateTime.atZone(offset).withZoneSameLocal(zone)
     * <li><a href="../../../serialized-form.html#java.time.chrono.JapaneseDate">JapaneseDate</a> - JapaneseChronology.INSTANCE.date(year, month, dayOfMonth)
     * <li><a href="../../../serialized-form.html#java.time.chrono.JapaneseEra">JapaneseEra</a> - JapaneseEra.of(eraValue)
     * <li><a href="../../../serialized-form.html#java.time.chrono.HijrahDate">HijrahDate</a> - HijrahChronology chrono.date(year, month, dayOfMonth)
     * <li><a href="../../../serialized-form.html#java.time.chrono.MinguoDate">MinguoDate</a> - MinguoChronology.INSTANCE.date(year, month, dayOfMonth)
     * <li><a href="../../../serialized-form.html#java.time.chrono.ThaiBuddhistDate">ThaiBuddhistDate</a> - ThaiBuddhistChronology.INSTANCE.date(year, month, dayOfMonth)
     * </ul>
     *
     * @param in  the data stream to read from, not null
     */
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        type = in.readByte();
        object = readInternal(type, in);
    }

    static Object read(ObjectInput in) throws IOException, ClassNotFoundException {
        byte type = in.readByte();
        return readInternal(type, in);
    }

    private static Object readInternal(byte type, ObjectInput in) throws IOException, ClassNotFoundException {
        switch (type) {
            case CHRONO_TYPE: return AbstractChronology.readExternal(in);
            case CHRONO_LOCAL_DATE_TIME_TYPE: return ChronoLocalDateTimeImpl.readExternal(in);
            case CHRONO_ZONE_DATE_TIME_TYPE: return ChronoZonedDateTimeImpl.readExternal(in);
            case JAPANESE_DATE_TYPE:  return JapaneseDate.readExternal(in);
            case JAPANESE_ERA_TYPE: return JapaneseEra.readExternal(in);
            case HIJRAH_DATE_TYPE: return HijrahDate.readExternal(in);
            case MINGUO_DATE_TYPE: return MinguoDate.readExternal(in);
            case THAIBUDDHIST_DATE_TYPE: return ThaiBuddhistDate.readExternal(in);
            case CHRONO_PERIOD_TYPE: return java.time.chrono.ChronoPeriodImpl.readExternal(in);
            default: throw new StreamCorruptedException("Unknown serialized type");
        }
    }

    /**
     * Returns the object that will replace this one.
     *
     * @return the read object, should never be null
     */
    private Object readResolve() {
         return object;
    }

}
