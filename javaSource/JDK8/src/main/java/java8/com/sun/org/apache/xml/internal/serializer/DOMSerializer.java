/*
 * Copyright (c) 2007-2012, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * Copyright 1999-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * $Id: DOMSerializer.java,v 1.2.4.1 2005/09/15 08:15:15 suresh_emailid Exp $
 */
package java8.sun.org.apache.xml.internal.serializer;

import com.sun.org.apache.xml.internal.serializer.Serializer;
import org.w3c.dom.Node;

import java.io.IOException;

/**
 * Interface for a DOM serializer implementation.
 * <p>
 * The DOMSerializer is a facet of a serializer and is obtained from the
 * asDOMSerializer() method of the Serializer interface.
 * A serializer may or may not support a DOM serializer, if it does not then the
 * return value from asDOMSerializer() is null.
 * <p>
 * Example:
 * <pre>
 * Document     doc;
 * Serializer   ser;
 * OutputStream os;
 *
 * ser = ...;
 * os = ...;
 *
 * ser.setOutputStream( os );
 * DOMSerialzier dser = ser.asDOMSerializer();
 * dser.serialize(doc);
 * </pre>
 *
 * @see Serializer
 *
 * @xsl.usage general
 *
 */
public interface DOMSerializer
{
    /**
     * Serializes the DOM node. Throws an exception only if an I/O
     * exception occured while serializing.
     *
     * This interface is a public API.
     *
     * @param node the DOM node to serialize
     * @throws IOException if an I/O exception occured while serializing
     */
    public void serialize(Node node) throws IOException;
}
