/*
 * Copyright (c) 2000, 2003, Oracle and/or its affiliates. All rights reserved.
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
package java8.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.encoding.CDRInputStream_1_0;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;

public class CDRInputStream_1_1 extends CDRInputStream_1_0
{
    // See notes in CDROutputStream
    protected int fragmentOffset = 0;

    public GIOPVersion getGIOPVersion() {
        return GIOPVersion.V1_1;
    }

    // Template method
    public CDRInputStreamBase dup() {
        CDRInputStreamBase result = super.dup();

        ((com.sun.corba.se.impl.encoding.CDRInputStream_1_1)result).fragmentOffset = this.fragmentOffset;

        return result;
    }

    protected int get_offset() {
        return bbwi.position() + fragmentOffset;
    }

    protected void alignAndCheck(int align, int n) {


        checkBlockLength(align, n);

        // WARNING: Must compute real alignment after calling
        // checkBlockLength since it may move the position
        int alignment = computeAlignment(bbwi.position(), align);

        if (bbwi.position() + n + alignment  > bbwi.buflen) {

            // Some other ORBs may have found a way to send 1.1
            // fragments which put alignment bytes at the end
            // of a fragment
            if (bbwi.position() + alignment == bbwi.buflen)
            {
                bbwi.position(bbwi.position() + alignment);
            }

            grow(align, n);

            // We must recalculate the alignment after a possible
            // fragmentation since the new bbwi.position() (after the header)
            // may require a different alignment.

            alignment = computeAlignment(bbwi.position(), align);
        }

        bbwi.position(bbwi.position() + alignment);
    }

    //
    // This can be overridden....
    //
    protected void grow(int align, int n) {

        bbwi.needed = n;

        // Save the size of the current buffer for
        // possible fragmentOffset calculation
        int oldSize = bbwi.position();

        bbwi = bufferManagerRead.underflow(bbwi);

        if (bbwi.fragmented) {

            // By this point we should be guaranteed to have
            // a new fragment whose header has already been
            // unmarshalled.  bbwi.position() should point to the
            // end of the header.
            fragmentOffset += (oldSize - bbwi.position());

            markAndResetHandler.fragmentationOccured(bbwi);

            // Clear the flag
            bbwi.fragmented = false;
        }
    }

    // Mark/reset ---------------------------------------

    private class FragmentableStreamMemento extends StreamMemento
    {
        private int fragmentOffset_;

        public FragmentableStreamMemento()
        {
            super();

            fragmentOffset_ = fragmentOffset;
        }
    }

    public Object createStreamMemento() {
        return new FragmentableStreamMemento();
    }

    public void restoreInternalState(Object streamMemento)
    {
        super.restoreInternalState(streamMemento);

        fragmentOffset
            = ((FragmentableStreamMemento)streamMemento).fragmentOffset_;
    }

    // --------------------------------------------------

    public char read_wchar() {
        // In GIOP 1.1, interoperability with wchar is limited
        // to 2 byte fixed width encodings.  CORBA formal 99-10-07 15.3.1.6.
        // WARNING:  For UTF-16, this means that there can be no
        // byte order marker, so it must default to big endian!
        alignAndCheck(2, 2);

        // Because of the alignAndCheck, we should be guaranteed
        // 2 bytes of real data.
        char[] result = getConvertedChars(2, getWCharConverter());

        // Did the provided bytes convert to more than one
        // character?  This may come up as more unicode values are
        // assigned, and a single 16 bit Java char isn't enough.
        // Better to use strings for i18n purposes.
        if (getWCharConverter().getNumChars() > 1)
            throw wrapper.btcResultMoreThanOneChar() ;

        return result[0];
    }

    public String read_wstring() {
        // In GIOP 1.1, interoperability with wchar is limited
        // to 2 byte fixed width encodings.  CORBA formal 99-10-07 15.3.1.6.
        int len = read_long();

        // Workaround for ORBs which send string lengths of
        // zero to mean empty string.
        //
        // IMPORTANT: Do not replace 'new String("")' with "", it may result
        // in a Serialization bug (See serialization.zerolengthstring) and
        // bug id: 4728756 for details
        if (len == 0)
            return new String("");

        checkForNegativeLength(len);

        // Don't include the two byte null for the
        // following computations.  Remember that since we're limited
        // to a 2 byte fixed width code set, the "length" was the
        // number of such 2 byte code points plus a 2 byte null.
        len = len - 1;

        char[] result = getConvertedChars(len * 2, getWCharConverter());

        // Skip over the 2 byte null
        read_short();

        return new String(result, 0, getWCharConverter().getNumChars());
    }

}
