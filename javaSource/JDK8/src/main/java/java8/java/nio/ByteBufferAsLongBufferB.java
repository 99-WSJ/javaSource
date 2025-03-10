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

// -- This file was mechanically generated: Do not edit! -- //

package java8.java.nio;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.LongBuffer;

class ByteBufferAsLongBufferB                  // package-private
    extends LongBuffer
{



    protected final ByteBuffer bb;
    protected final int offset;



    ByteBufferAsLongBufferB(ByteBuffer bb) {   // package-private

        super(-1, 0,
              bb.remaining() >> 3,
              bb.remaining() >> 3);
        this.bb = bb;
        // enforce limit == capacity
        int cap = this.capacity();
        this.limit(cap);
        int pos = this.position();
        assert (pos <= cap);
        offset = pos;



    }

    ByteBufferAsLongBufferB(ByteBuffer bb,
                                     int mark, int pos, int lim, int cap,
                                     int off)
    {

        super(mark, pos, lim, cap);
        this.bb = bb;
        offset = off;



    }

    public LongBuffer slice() {
        int pos = this.position();
        int lim = this.limit();
        assert (pos <= lim);
        int rem = (pos <= lim ? lim - pos : 0);
        int off = (pos << 3) + offset;
        assert (off >= 0);
        return new java.nio.ByteBufferAsLongBufferB(bb, -1, 0, rem, rem, off);
    }

    public LongBuffer duplicate() {
        return new java.nio.ByteBufferAsLongBufferB(bb,
                                                    this.markValue(),
                                                    this.position(),
                                                    this.limit(),
                                                    this.capacity(),
                                                    offset);
    }

    public LongBuffer asReadOnlyBuffer() {

        return new java.nio.ByteBufferAsLongBufferRB(bb,
                                                 this.markValue(),
                                                 this.position(),
                                                 this.limit(),
                                                 this.capacity(),
                                                 offset);



    }



    protected int ix(int i) {
        return (i << 3) + offset;
    }

    public long get() {
        return java.nio.Bits.getLongB(bb, ix(nextGetIndex()));
    }

    public long get(int i) {
        return java.nio.Bits.getLongB(bb, ix(checkIndex(i)));
    }









    public LongBuffer put(long x) {

        java.nio.Bits.putLongB(bb, ix(nextPutIndex()), x);
        return this;



    }

    public LongBuffer put(int i, long x) {

        java.nio.Bits.putLongB(bb, ix(checkIndex(i)), x);
        return this;



    }

    public LongBuffer compact() {

        int pos = position();
        int lim = limit();
        assert (pos <= lim);
        int rem = (pos <= lim ? lim - pos : 0);

        ByteBuffer db = bb.duplicate();
        db.limit(ix(lim));
        db.position(ix(0));
        ByteBuffer sb = db.slice();
        sb.position(pos << 3);
        sb.compact();
        position(rem);
        limit(capacity());
        discardMark();
        return this;



    }

    public boolean isDirect() {
        return bb.isDirect();
    }

    public boolean isReadOnly() {
        return false;
    }











































    public ByteOrder order() {

        return ByteOrder.BIG_ENDIAN;




    }

}
