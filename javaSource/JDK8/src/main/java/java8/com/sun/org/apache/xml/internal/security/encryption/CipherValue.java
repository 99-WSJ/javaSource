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
package java8.com.sun.org.apache.xml.internal.security.encryption;

/**
 * <code>CipherValue</code> is the wrapper for cipher text.
 *
 * @author Axl Mattheus
 */
public interface CipherValue {
    /**
     * Returns the Base 64 encoded, encrypted octets that is the
     * <code>CipherValue</code>.
     *
     * @return cipher value.
     */
    String getValue();

    /**
     * Sets the Base 64 encoded, encrypted octets that is the
     * <code>CipherValue</code>.
     *
     * @param value the cipher value.
     */
    void setValue(String value);
}
