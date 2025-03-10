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
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package java8.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DOMImplementationList;

import java.util.Vector;

/**
 * <p>This class implements the DOM Level 3 Core interface DOMImplementationList.</p>
 *
 * @xerces.internal
 *
 * @author Neil Delima, IBM
 * @since DOM Level 3 Core
 */
public class DOMImplementationListImpl implements DOMImplementationList {

    //A collection of DOMImplementations
    private Vector fImplementations;

    /**
     * Construct an empty list of DOMImplementations
     */
    public DOMImplementationListImpl() {
        fImplementations = new Vector();
    }

    /**
     * Construct an empty list of DOMImplementations
     */
    public DOMImplementationListImpl(Vector params) {
        fImplementations = params;
    }

    /**
     * Returns the indexth item in the collection.
     *
     * @param index The index of the DOMImplemetation from the list to return.
     */
    public DOMImplementation item(int index) {
        try {
            return (DOMImplementation) fImplementations.elementAt(index);
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Returns the number of DOMImplementations in the list.
     *
     * @return An integer indicating the number of DOMImplementations.
     */
    public int getLength() {
        return fImplementations.size();
    }
}
