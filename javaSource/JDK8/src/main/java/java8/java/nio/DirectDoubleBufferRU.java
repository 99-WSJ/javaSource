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

import sun.nio.ch.DirectBuffer;

import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.ReadOnlyBufferException;


class DirectDoubleBufferRU



    extends java.nio.DirectDoubleBufferU

    implements DirectBuffer
{















































































































































    // For duplicates and slices
    //
    DirectDoubleBufferRU(DirectBuffer db,         // package-private
                               int mark, int pos, int lim, int cap,
                               int off)
    {








        super(db, mark, pos, lim, cap, off);

    }

    public DoubleBuffer slice() {
        int pos = this.position();
        int lim = this.limit();
        assert (pos <= lim);
        int rem = (pos <= lim ? lim - pos : 0);
        int off = (pos << 3);
        assert (off >= 0);
        return new java.nio.DirectDoubleBufferRU(this, -1, 0, rem, rem, off);
    }

    public DoubleBuffer duplicate() {
        return new java.nio.DirectDoubleBufferRU(this,
                                              this.markValue(),
                                              this.position(),
                                              this.limit(),
                                              this.capacity(),
                                              0);
    }

    public DoubleBuffer asReadOnlyBuffer() {








        return duplicate();

    }


























































    public DoubleBuffer put(double x) {




        throw new ReadOnlyBufferException();

    }

    public DoubleBuffer put(int i, double x) {




        throw new ReadOnlyBufferException();

    }

    public DoubleBuffer put(DoubleBuffer src) {




































        throw new ReadOnlyBufferException();

    }

    public DoubleBuffer put(double[] src, int offset, int length) {
























        throw new ReadOnlyBufferException();

    }

    public DoubleBuffer compact() {












        throw new ReadOnlyBufferException();

    }

    public boolean isDirect() {
        return true;
    }

    public boolean isReadOnly() {
        return true;
    }















































    public ByteOrder order() {





        return ((ByteOrder.nativeOrder() != ByteOrder.BIG_ENDIAN)
                ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN);

    }


























}
