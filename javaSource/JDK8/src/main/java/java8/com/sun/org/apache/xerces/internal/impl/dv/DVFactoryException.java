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

package java8.sun.org.apache.xerces.internal.impl.dv;

/**
 * A runtime exception that's thrown if an error happens when the application
 * tries to get a DV factory instance.
 *
 * @xerces.internal
 *
 */
public class DVFactoryException extends RuntimeException {

    /** Serialization version. */
    static final long serialVersionUID = -3738854697928682412L;

    public DVFactoryException() {
        super();
    }

    public DVFactoryException(String msg) {
        super(msg);
    }
}
