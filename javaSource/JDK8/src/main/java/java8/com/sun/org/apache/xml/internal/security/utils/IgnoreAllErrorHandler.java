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
package java8.sun.org.apache.xml.internal.security.utils;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * This {@link ErrorHandler} does absolutely nothing but log
 * the events.
 *
 * @author Christian Geuer-Pollmann
 */
public class IgnoreAllErrorHandler implements ErrorHandler {

    /** {@link org.apache.commons.logging} logging facility */
    private static java.util.logging.Logger log =
        java.util.logging.Logger.getLogger(com.sun.org.apache.xml.internal.security.utils.IgnoreAllErrorHandler.class.getName());

    /** Field throwExceptions */
    private static final boolean warnOnExceptions =
        System.getProperty("com.sun.org.apache.xml.internal.security.test.warn.on.exceptions", "false").equals("true");

    /** Field throwExceptions           */
    private static final boolean throwExceptions =
        System.getProperty("com.sun.org.apache.xml.internal.security.test.throw.exceptions", "false").equals("true");


    /** @inheritDoc */
    public void warning(SAXParseException ex) throws SAXException {
        if (com.sun.org.apache.xml.internal.security.utils.IgnoreAllErrorHandler.warnOnExceptions) {
            log.log(java.util.logging.Level.WARNING, "", ex);
        }
        if (com.sun.org.apache.xml.internal.security.utils.IgnoreAllErrorHandler.throwExceptions) {
            throw ex;
        }
    }


    /** @inheritDoc */
    public void error(SAXParseException ex) throws SAXException {
        if (com.sun.org.apache.xml.internal.security.utils.IgnoreAllErrorHandler.warnOnExceptions) {
            log.log(java.util.logging.Level.SEVERE, "", ex);
        }
        if (com.sun.org.apache.xml.internal.security.utils.IgnoreAllErrorHandler.throwExceptions) {
            throw ex;
        }
    }


    /** @inheritDoc */
    public void fatalError(SAXParseException ex) throws SAXException {
        if (com.sun.org.apache.xml.internal.security.utils.IgnoreAllErrorHandler.warnOnExceptions) {
            log.log(java.util.logging.Level.WARNING, "", ex);
        }
        if (com.sun.org.apache.xml.internal.security.utils.IgnoreAllErrorHandler.throwExceptions) {
            throw ex;
        }
    }
}
