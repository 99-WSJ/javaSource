/*
 * Copyright (c) 2007-2012, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * Copyright 2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package java8.sun.org.apache.xerces.internal.impl.dv.util;

import com.sun.org.apache.xerces.internal.xs.XSException;
import com.sun.org.apache.xerces.internal.xs.datatypes.ByteList;

import java.util.AbstractList;

/**
 * Implementation of <code>com.sun.org.apache.xerces.internal.xs.datatypes.ByteList</code>.
 *
 * @xerces.internal
 *
 * @author Ankit Pasricha, IBM
 *
 * @version $Id: ByteListImpl.java,v 1.7 2010-11-01 04:39:46 joehw Exp $
 */
public class ByteListImpl extends AbstractList implements ByteList {

    // actually data stored in a byte array
    protected final byte[] data;

    // canonical representation of the data
    protected String canonical;

    public ByteListImpl(byte[] data) {
        this.data = data;
    }

    /**
     * The number of <code>byte</code>s in the list. The range of
     * valid child object indices is 0 to <code>length-1</code> inclusive.
     */
    public int getLength() {
        return data.length;
    }

    /**
     * Checks if the <code>byte</code> <code>item</code> is a
     * member of this list.
     * @param item  <code>byte</code> whose presence in this list
     *   is to be tested.
     * @return  True if this list contains the <code>byte</code>
     *   <code>item</code>.
     */
    public boolean contains(byte item) {
        for (int i = 0; i < data.length; ++i) {
            if (data[i] == item) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the <code>index</code>th item in the collection. The index
     * starts at 0.
     * @param index  index into the collection.
     * @return  The <code>byte</code> at the <code>index</code>th
     *   position in the <code>ByteList</code>.
     * @exception XSException
     *   INDEX_SIZE_ERR: if <code>index</code> is greater than or equal to the
     *   number of objects in the list.
     */
    public byte item(int index)
        throws XSException {

        if(index < 0 || index > data.length - 1) {
            throw new XSException(XSException.INDEX_SIZE_ERR, null);
        }
        return data[index];
    }

    /*
     * List methods
     */

    public Object get(int index) {
        if (index >= 0 && index < data.length) {
            return new Byte(data[index]);
        }
        throw new IndexOutOfBoundsException("Index: " + index);
    }

    public int size() {
        return getLength();
    }
}
