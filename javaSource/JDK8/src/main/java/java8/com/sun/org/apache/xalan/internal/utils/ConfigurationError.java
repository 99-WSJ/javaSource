/*
 * Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * Copyright 2001-2004 The Apache Software Foundation.
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
 * $Id: ObjectFactory.java,v 1.2.4.1 2005/09/15 02:39:54 jeffsuttor Exp $
 */

package java8.sun.org.apache.xalan.internal.utils;

/**
 * A configuration error. This was an internal class in ObjectFactory previously
 */
public final class ConfigurationError
    extends Error {

    //
    // Data
    //

    /** Exception. */
    private Exception exception;

    //
    // Constructors
    //

    /**
     * Construct a new instance with the specified detail string and
     * exception.
     */
    ConfigurationError(String msg, Exception x) {
        super(msg);
        this.exception = x;
    } // <init>(String,Exception)

    //
    // methods
    //

    /** Returns the exception associated to this error. */
    public Exception getException() {
        return exception;
    } // getException():Exception

} // class ConfigurationError
