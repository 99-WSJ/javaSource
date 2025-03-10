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
/*
 * Licensed Materials - Property of IBM
 * RMI-IIOP v1.0
 * Copyright IBM Corp. 1998 1999  All Rights Reserved
 *
 */

package java8.sun.corba.se.impl.io;

import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.UtilSystemException;
import com.sun.corba.se.spi.logging.CORBALogDomains;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBVersion;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import org.omg.CORBA.portable.ValueInputStream;

import java.io.IOException;
import java.io.NotActiveException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.HashMap;
import java.util.Map;

public abstract class InputStreamHook extends ObjectInputStream
{
    // These should be visible in all the nested classes
    static final OMGSystemException omgWrapper =
        OMGSystemException.get( CORBALogDomains.RPC_ENCODING ) ;

    static final UtilSystemException utilWrapper =
        UtilSystemException.get( CORBALogDomains.RPC_ENCODING ) ;

    private class HookGetFields extends GetField {
        private Map fields = null;

        HookGetFields(Map fields){
            this.fields = fields;
        }

        /**
         * Get the ObjectStreamClass that describes the fields in the stream.
         *
         * REVISIT!  This doesn't work since we have our own ObjectStreamClass.
         */
        public java.io.ObjectStreamClass getObjectStreamClass() {
            return null;
        }

        /**
         * Return true if the named field is defaulted and has no value
         * in this stream.
         */
        public boolean defaulted(String name)
            throws IOException, IllegalArgumentException  {
            return (!fields.containsKey(name));
        }

        /**
         * Get the value of the named boolean field from the persistent field.
         */
        public boolean get(String name, boolean defvalue)
            throws IOException, IllegalArgumentException {
            if (defaulted(name))
                return defvalue;
            else return ((Boolean)fields.get(name)).booleanValue();
        }

        /**
         * Get the value of the named char field from the persistent fields.
         */
        public char get(String name, char defvalue)
            throws IOException, IllegalArgumentException {
            if (defaulted(name))
                return defvalue;
            else return ((Character)fields.get(name)).charValue();

        }

        /**
         * Get the value of the named byte field from the persistent fields.
         */
        public byte get(String name, byte defvalue)
            throws IOException, IllegalArgumentException {
            if (defaulted(name))
                return defvalue;
            else return ((Byte)fields.get(name)).byteValue();

        }

        /**
         * Get the value of the named short field from the persistent fields.
         */
        public short get(String name, short defvalue)
            throws IOException, IllegalArgumentException {
            if (defaulted(name))
                return defvalue;
            else return ((Short)fields.get(name)).shortValue();

        }

        /**
         * Get the value of the named int field from the persistent fields.
         */
        public int get(String name, int defvalue)
            throws IOException, IllegalArgumentException {
            if (defaulted(name))
                return defvalue;
            else return ((Integer)fields.get(name)).intValue();

        }

        /**
         * Get the value of the named long field from the persistent fields.
         */
        public long get(String name, long defvalue)
            throws IOException, IllegalArgumentException {
            if (defaulted(name))
                return defvalue;
            else return ((Long)fields.get(name)).longValue();

        }

        /**
         * Get the value of the named float field from the persistent fields.
         */
        public float get(String name, float defvalue)
            throws IOException, IllegalArgumentException {
            if (defaulted(name))
                return defvalue;
            else return ((Float)fields.get(name)).floatValue();

        }

        /**
         * Get the value of the named double field from the persistent field.
         */
        public double get(String name, double defvalue)
            throws IOException, IllegalArgumentException  {
            if (defaulted(name))
                return defvalue;
            else return ((Double)fields.get(name)).doubleValue();

        }

        /**
         * Get the value of the named Object field from the persistent field.
         */
        public Object get(String name, Object defvalue)
            throws IOException, IllegalArgumentException {
            if (defaulted(name))
                return defvalue;
            else return fields.get(name);

        }

        public String toString(){
            return fields.toString();
        }
    }

    public InputStreamHook()
        throws IOException {
        super();
    }

    public void defaultReadObject()
        throws IOException, ClassNotFoundException, NotActiveException
    {
        readObjectState.beginDefaultReadObject(this);

        defaultReadObjectDelegate();

        readObjectState.endDefaultReadObject(this);
    }

    abstract void defaultReadObjectDelegate();

    abstract void readFields(Map fieldToValueMap)
        throws java.io.InvalidClassException, StreamCorruptedException,
               ClassNotFoundException, IOException;


    // See java.io.ObjectInputStream.GetField
    // Remember that this is equivalent to defaultReadObject
    // in RMI-IIOP
    public GetField readFields()
        throws IOException, ClassNotFoundException, NotActiveException {

        HashMap fieldValueMap = new HashMap();

        // We were treating readFields same as defaultReadObject. It is
        // incorrect if the state is readOptionalData. If this line
        // is uncommented, it will throw a stream corrupted exception.
        // _REVISIT_: The ideal fix would be to add a new state. In
        // writeObject user may do one of the following
        // 1. Call defaultWriteObject()
        // 2. Put out optional fields
        // 3. Call writeFields
        // We have the state defined for (1) and (2) but not for (3), so
        // we should ideally introduce a new state for 3 and have the
        // beginDefaultReadObject do nothing.
        //readObjectState.beginDefaultReadObject(this);

        readFields(fieldValueMap);

        readObjectState.endDefaultReadObject(this);

        return new HookGetFields(fieldValueMap);
    }

    // The following is a State pattern implementation of what
    // should be done when the sender's Serializable has a
    // writeObject method.  This was especially necessary for
    // RMI-IIOP stream format version 2.  Please see the
    // state diagrams in the docs directory of the workspace.
    //
    // On the reader's side, the main factors are whether or not
    // we have a readObject method and whether or not the
    // sender wrote default data

    protected void setState(ReadObjectState newState) {
        readObjectState = newState;
    }

    protected abstract byte getStreamFormatVersion();
    abstract org.omg.CORBA_2_3.portable.InputStream getOrbStream();

    // Description of possible actions
    protected static class ReadObjectState {
        public void beginUnmarshalCustomValue(com.sun.corba.se.impl.io.InputStreamHook stream,
                                              boolean calledDefaultWriteObject,
                                              boolean hasReadObject) throws IOException {}

        public void endUnmarshalCustomValue(com.sun.corba.se.impl.io.InputStreamHook stream) throws IOException {}
        public void beginDefaultReadObject(com.sun.corba.se.impl.io.InputStreamHook stream) throws IOException {}
        public void endDefaultReadObject(com.sun.corba.se.impl.io.InputStreamHook stream) throws IOException {}
        public void readData(com.sun.corba.se.impl.io.InputStreamHook stream) throws IOException {}
    }

    protected ReadObjectState readObjectState = DEFAULT_STATE;

    protected static final ReadObjectState DEFAULT_STATE = new DefaultState();
    protected static final ReadObjectState IN_READ_OBJECT_OPT_DATA
        = new InReadObjectOptionalDataState();
    protected static final ReadObjectState IN_READ_OBJECT_NO_MORE_OPT_DATA
        = new InReadObjectNoMoreOptionalDataState();
    protected static final ReadObjectState IN_READ_OBJECT_DEFAULTS_SENT
        = new InReadObjectDefaultsSentState();
    protected static final ReadObjectState NO_READ_OBJECT_DEFAULTS_SENT
        = new NoReadObjectDefaultsSentState();

    protected static final ReadObjectState IN_READ_OBJECT_REMOTE_NOT_CUSTOM_MARSHALED
        = new InReadObjectRemoteDidNotUseWriteObjectState();
    protected static final ReadObjectState IN_READ_OBJECT_PAST_DEFAULTS_REMOTE_NOT_CUSTOM
        = new InReadObjectPastDefaultsRemoteDidNotUseWOState();

    protected static class DefaultState extends ReadObjectState {

        public void beginUnmarshalCustomValue(com.sun.corba.se.impl.io.InputStreamHook stream,
                                              boolean calledDefaultWriteObject,
                                              boolean hasReadObject)
            throws IOException {

            if (hasReadObject) {
                if (calledDefaultWriteObject)
                    stream.setState(IN_READ_OBJECT_DEFAULTS_SENT);
                else {
                    try {
                        if (stream.getStreamFormatVersion() == 2)
                            ((ValueInputStream)stream.getOrbStream()).start_value();
                    } catch( Exception e ) {
                        // This will happen for Big Integer which uses
                        // writeFields in it's writeObject. We should be past
                        // start_value by now.
                        // NOTE: If we don't log any exception here we should
                        // be fine. If there is an error, it will be caught
                        // while reading the optional data.

                    }
                    stream.setState(IN_READ_OBJECT_OPT_DATA);
                }
            } else {
                if (calledDefaultWriteObject)
                    stream.setState(NO_READ_OBJECT_DEFAULTS_SENT);
                else
                    // XXX I18N and logging needed.
                    throw new StreamCorruptedException("No default data sent");
            }
        }
    }

    // REVISIT.  If a readObject exits here without reading
    // default data, we won't skip it.  This could be done automatically
    // as in line 1492 in IIOPInputStream.
    protected static class InReadObjectRemoteDidNotUseWriteObjectState extends ReadObjectState {

        public void beginUnmarshalCustomValue(com.sun.corba.se.impl.io.InputStreamHook stream,
                                              boolean calledDefaultWriteObject,
                                              boolean hasReadObject)
        {
            throw utilWrapper.badBeginUnmarshalCustomValue() ;
        }

        public void endDefaultReadObject(com.sun.corba.se.impl.io.InputStreamHook stream) {
            stream.setState(IN_READ_OBJECT_PAST_DEFAULTS_REMOTE_NOT_CUSTOM);
        }

        public void readData(com.sun.corba.se.impl.io.InputStreamHook stream) {
            stream.throwOptionalDataIncompatibleException();
        }
    }

    protected static class InReadObjectPastDefaultsRemoteDidNotUseWOState extends ReadObjectState {

        public void beginUnmarshalCustomValue(com.sun.corba.se.impl.io.InputStreamHook stream,
                                              boolean calledDefaultWriteObject,
                                              boolean hasReadObject)
        {
            throw utilWrapper.badBeginUnmarshalCustomValue() ;
        }

        public void beginDefaultReadObject(com.sun.corba.se.impl.io.InputStreamHook stream) throws IOException
        {
            // XXX I18N and logging needed.
            throw new StreamCorruptedException("Default data already read");
        }


        public void readData(com.sun.corba.se.impl.io.InputStreamHook stream) {
            stream.throwOptionalDataIncompatibleException();
        }
    }

    protected void throwOptionalDataIncompatibleException()
    {
        throw omgWrapper.rmiiiopOptionalDataIncompatible2() ;
    }


    protected static class InReadObjectDefaultsSentState extends ReadObjectState {

        public void beginUnmarshalCustomValue(com.sun.corba.se.impl.io.InputStreamHook stream,
                                              boolean calledDefaultWriteObject,
                                              boolean hasReadObject) {
            // This should never happen.
            throw utilWrapper.badBeginUnmarshalCustomValue() ;
        }

        public void endUnmarshalCustomValue(com.sun.corba.se.impl.io.InputStreamHook stream) {

            // In stream format version 2, we can skip over
            // the optional data this way.  In stream format version 1,
            // we will probably wind up with an error if we're
            // unmarshaling a superclass.
            if (stream.getStreamFormatVersion() == 2) {
                ((ValueInputStream)stream.getOrbStream()).start_value();
                ((ValueInputStream)stream.getOrbStream()).end_value();
            }

            stream.setState(DEFAULT_STATE);
        }

        public void endDefaultReadObject(com.sun.corba.se.impl.io.InputStreamHook stream) throws IOException {

            // Read the fake valuetype header in stream format version 2
            if (stream.getStreamFormatVersion() == 2)
                ((ValueInputStream)stream.getOrbStream()).start_value();

            stream.setState(IN_READ_OBJECT_OPT_DATA);
        }

        public void readData(com.sun.corba.se.impl.io.InputStreamHook stream) throws IOException {
            org.omg.CORBA.ORB orb = stream.getOrbStream().orb();
            if ((orb == null) ||
                    !(orb instanceof ORB)) {
                throw new StreamCorruptedException(
                                     "Default data must be read first");
            }
            ORBVersion clientOrbVersion =
                ((ORB)orb).getORBVersion();

            // Fix Date interop bug. For older versions of the ORB don't do
            // anything for readData(). Before this used to throw
            // StreamCorruptedException for older versions of the ORB where
            // calledDefaultWriteObject always returns true.
            if ((ORBVersionFactory.getPEORB().compareTo(clientOrbVersion) <= 0) ||
                    (clientOrbVersion.equals(ORBVersionFactory.getFOREIGN()))) {
                // XXX I18N and logging needed.
                throw new StreamCorruptedException("Default data must be read first");
            }
        }
    }

    protected static class InReadObjectOptionalDataState extends ReadObjectState {

        public void beginUnmarshalCustomValue(com.sun.corba.se.impl.io.InputStreamHook stream,
                                              boolean calledDefaultWriteObject,
                                              boolean hasReadObject)
        {
            // This should never happen.
            throw utilWrapper.badBeginUnmarshalCustomValue() ;
        }

        public void endUnmarshalCustomValue(com.sun.corba.se.impl.io.InputStreamHook stream) throws IOException
        {
            if (stream.getStreamFormatVersion() == 2) {
                ((ValueInputStream)stream.getOrbStream()).end_value();
            }
            stream.setState(DEFAULT_STATE);
        }

        public void beginDefaultReadObject(com.sun.corba.se.impl.io.InputStreamHook stream) throws IOException
        {
            // XXX I18N and logging needed.
            throw new StreamCorruptedException("Default data not sent or already read/passed");
        }


    }

    protected static class InReadObjectNoMoreOptionalDataState
        extends InReadObjectOptionalDataState {

        public void readData(com.sun.corba.se.impl.io.InputStreamHook stream) throws IOException {
            stream.throwOptionalDataIncompatibleException();
        }
    }

    protected static class NoReadObjectDefaultsSentState extends ReadObjectState {
        public void endUnmarshalCustomValue(com.sun.corba.se.impl.io.InputStreamHook stream) throws IOException {
            // Code should read default fields before calling this

            if (stream.getStreamFormatVersion() == 2) {
                ((ValueInputStream)stream.getOrbStream()).start_value();
                ((ValueInputStream)stream.getOrbStream()).end_value();
            }

            stream.setState(DEFAULT_STATE);
        }
    }
}
