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
package java8.sun.org.apache.xml.internal.security.transforms;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;

/**
 *
 * @author Christian Geuer-Pollmann
 */
public class InvalidTransformException extends XMLSecurityException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor InvalidTransformException
     *
     */
    public InvalidTransformException() {
        super();
    }

    /**
     * Constructor InvalidTransformException
     *
     * @param msgId
     */
    public InvalidTransformException(String msgId) {
        super(msgId);
    }

    /**
     * Constructor InvalidTransformException
     *
     * @param msgId
     * @param exArgs
     */
    public InvalidTransformException(String msgId, Object exArgs[]) {
        super(msgId, exArgs);
    }

    /**
     * Constructor InvalidTransformException
     *
     * @param msgId
     * @param originalException
     */
    public InvalidTransformException(String msgId, Exception originalException) {
        super(msgId, originalException);
    }

    /**
     * Constructor InvalidTransformException
     *
     * @param msgId
     * @param exArgs
     * @param originalException
     */
    public InvalidTransformException(String msgId, Object exArgs[], Exception originalException) {
        super(msgId, exArgs, originalException);
    }
}
