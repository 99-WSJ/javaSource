/*
 * Copyright (c) 2007-2013, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package java8.sun.org.apache.xml.internal.security.encryption;

/**
 * A container for <code>ds:Transform</code>s.
 * <p>
 * It is defined as follows:
 * <xmp>
 * <complexType name='TransformsType'>
 *     <sequence>
 *         <element ref='ds:Transform' maxOccurs='unbounded'/>
 *     </sequence>
 * </complexType>
 * </xmp>
 *
 * @author Axl Mattheus
 * @see com.sun.org.apache.xml.internal.security.encryption.CipherReference
 */
public interface Transforms {
    /**
     * Temporary method to turn the XMLEncryption Transforms class
     * into a DS class.  The main logic is currently implemented in the
     * DS class, so we need to get to get the base class.
     * <p>
     * <b>Note</b> This will be removed in future versions
     */
    com.sun.org.apache.xml.internal.security.transforms.Transforms getDSTransforms();

}
