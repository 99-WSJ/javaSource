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

class ByteBufferAsFloatBufferB                  // package-private
    extends FloatBuffer
{



    protected final ByteBuffer bb;
    protected final int offset;



    ByteBufferAsFloatBufferB(ByteBuffer bb) {   // package-private

        super(-1, 0,
              bb.remaining() >> 2,
              bb.remaining() >> 2);
        this.bb = bb;
        // enforce limit == capacity
        int cap = this.capacity();
        this.limit(cap);
        int pos = this.position();
        assert (pos <= cap);
        offset = pos;



    }

    ByteBufferAsFloatBufferB(ByteBuffer bb,
                                     int mark, int pos, int lim, int cap,
                                     int off)
    {

        super(mark, pos, lim, cap);
        this.bb = bb;
        offset = off;



    }

    @Override
    public FloatBuffer slice() {
        int pos = this.position();
        int lim = this.limit();
        assert (pos <= lim);
        int rem = (pos <= lim ? lim - pos : 0);
        int off = (pos << 2) + offset;
        assert (off >= 0);
        return new java.nio.ByteBufferAsFloatBufferB(bb, -1, 0, rem, rem, off);
    }

    @Override
    public FloatBuffer duplicate() {
        return new java.nio.ByteBufferAsFloatBufferB(bb,
                                                    this.markValue(),
                                                    this.position(),
                                                    this.limit(),
                                                    this.capacity(),
                                                    offset);
    }

    @Override
    public FloatBuffer asReadOnlyBuffer() {

        return new java.nio.ByteBufferAsFloatBufferRB(bb,
                                                 this.markValue(),
                                                 this.position(),
                                                 this.limit(),
                                                 this.capacity(),
                                                 offset);



    }



    protected int ix(int i) {
        return (i << 2) + offset;
    }

    public float get() {
        return java.nio.Bits.getFloatB(bb, ix(nextGetIndex()));
    }

    public float get(int i) {
        return java.nio.Bits.getFloatB(bb, ix(checkIndex(i)));
    }









    public FloatBuffer put(float x) {

        java.nio.Bits.putFloatB(bb, ix(nextPutIndex()), x);
        return this;



    }

    public FloatBuffer put(int i, float x) {

        java.nio.Bits.putFloatB(bb, ix(checkIndex(i)), x);
        return this;



    }

    public FloatBuffer compact() {

        int pos = position();
        int lim = limit();
        assert (pos <= lim);
        int rem = (pos <= lim ? lim - pos : 0);

        ByteBuffer db = bb.duplicate();
        db.limit(ix(lim));
        db.position(ix(0));
        ByteBuffer sb = db.slice();
        sb.position(pos << 2);
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
