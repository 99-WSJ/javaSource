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

import com.sun.org.apache.xerces.internal.dom.DOMImplementationListImpl;
import com.sun.org.apache.xerces.internal.dom.DOMImplementationSourceImpl;
import com.sun.org.apache.xerces.internal.dom.PSVIDOMImplementationImpl;
import com.sun.org.apache.xerces.internal.impl.xs.XSImplementationImpl;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DOMImplementationList;

import java.util.Vector;

/**
 * Allows to retrieve <code>XSImplementation</code>, DOM Level 3 Core and LS implementations
 * and PSVI implementation.
 * <p>See also the <a href='http://www.w3.org/TR/2004/REC-DOM-Level-3-Core-20040407/core.html#DOMImplementationSource'>Document Object Model (DOM) Level 3 Core Specification</a>.
 *
 * @xerces.internal
 *
 * @author Elena Litani, IBM
 */
public class DOMXSImplementationSourceImpl
    extends DOMImplementationSourceImpl {

    /**
     * A method to request a DOM implementation.
     * @param features A string that specifies which features are required.
     *   This is a space separated list in which each feature is specified
     *   by its name optionally followed by a space and a version number.
     *   This is something like: "XML 1.0 Traversal Events 2.0"
     * @return An implementation that has the desired features, or
     *   <code>null</code> if this source has none.
     */
    public DOMImplementation getDOMImplementation(String features) {
        DOMImplementation impl = super.getDOMImplementation(features);
        if (impl != null){
            return impl;
        }
        // if not try the PSVIDOMImplementation
        impl = PSVIDOMImplementationImpl.getDOMImplementation();
        if (testImpl(impl, features)) {
            return impl;
        }
        // if not try the XSImplementation
        impl = XSImplementationImpl.getDOMImplementation();
        if (testImpl(impl, features)) {
            return impl;
        }

        return null;
    }

    /**
     * A method to request a list of DOM implementations that support the
     * specified features and versions, as specified in .
     * @param features A string that specifies which features and versions
     *   are required. This is a space separated list in which each feature
     *   is specified by its name optionally followed by a space and a
     *   version number. This is something like: "XML 3.0 Traversal +Events
     *   2.0"
     * @return A list of DOM implementations that support the desired
     *   features.
     */
    public DOMImplementationList getDOMImplementationList(String features) {
        final Vector implementations = new Vector();

        // first check whether the CoreDOMImplementation would do
        DOMImplementationList list = super.getDOMImplementationList(features);
        //Add core DOMImplementations
        for (int i=0; i < list.getLength(); i++ ) {
            implementations.addElement(list.item(i));
        }

        DOMImplementation impl = PSVIDOMImplementationImpl.getDOMImplementation();
        if (testImpl(impl, features)) {
            implementations.addElement(impl);
        }

        impl = XSImplementationImpl.getDOMImplementation();
        if (testImpl(impl, features)) {
            implementations.addElement(impl);
        }
        return new DOMImplementationListImpl(implementations);
    }
}
