/*
 * Copyright (c) 2007-2012, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * Copyright 2001, 2002,2004 The Apache Software Foundation.
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

package java8.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.DOMStringList;

import java.util.Vector;

/**
 * DOM Level 3
 *
 * This class implements the DOM Level 3 Core interface DOMStringList.
 *
 * @xerces.internal
 *
 * @author Neil Delima, IBM
 */
public class DOMStringListImpl implements DOMStringList {

        //A collection of DOMString values
    private Vector fStrings;

    /**
     * Construct an empty list of DOMStringListImpl
     */
    public DOMStringListImpl() {
        fStrings = new Vector();
    }

    /**
     * Construct an empty list of DOMStringListImpl
     */
    public DOMStringListImpl(Vector params) {
        fStrings = params;
    }

        /**
         * @see DOMStringList#item(int)
         */
        public String item(int index) {
        try {
            return (String) fStrings.elementAt(index);
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
        }

        /**
         * @see DOMStringList#getLength()
         */
        public int getLength() {
                return fStrings.size();
        }

        /**
         * @see DOMStringList#contains(String)
         */
        public boolean contains(String param) {
                return fStrings.contains(param) ;
        }

    /**
     * DOM Internal:
     * Add a <code>DOMString</code> to the list.
     *
     * @param domString A string to add to the list
     */
    public void add(String param) {
        fStrings.add(param);
    }

}
