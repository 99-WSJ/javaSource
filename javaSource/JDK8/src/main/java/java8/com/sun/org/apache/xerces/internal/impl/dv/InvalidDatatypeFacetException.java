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

package java8.com.sun.org.apache.xerces.internal.impl.dv;

import com.sun.org.apache.xerces.internal.impl.dv.DatatypeException;

/**
 * Datatype exception for invalid facet. This exception is only used by
 * schema datatypes.
 *
 * @xerces.internal
 *
 * @author Sandy Gao, IBM
 *
 */
public class InvalidDatatypeFacetException extends DatatypeException {

    /** Serialization version. */
    static final long serialVersionUID = -4104066085909970654L;

    /**
     * Create a new datatype exception by providing an error code and a list
     * of error message substitution arguments.
     *
     * @param key  error code
     * @param args error arguments
     */
    public InvalidDatatypeFacetException(String key, Object[] args) {
        super(key, args);
    }

}
