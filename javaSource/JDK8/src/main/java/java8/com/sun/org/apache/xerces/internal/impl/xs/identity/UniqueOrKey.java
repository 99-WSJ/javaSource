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

package java8.sun.org.apache.xerces.internal.impl.xs.identity;

import com.sun.org.apache.xerces.internal.impl.xs.identity.IdentityConstraint;

/**
 * Schema unique or key identity constraint.
 * These two kinds of identity constraint have been combined to save
 * the creation of a separate Vector object for any element that
 * has both.  A short int is used to distinguish which this object is.
 *
 * @xerces.internal
 *
 * @author Andy Clark, IBM
 */
public class UniqueOrKey
    extends IdentityConstraint {

    //
    // Constructors
    //

    /** Constructs a unique or a key identity constraint. */
    public UniqueOrKey(String namespace, String identityConstraintName,
                       String elemName, short type) {
        super(namespace, identityConstraintName, elemName);
        this.type = type;
    } // <init>(String,String)

    //
    // Public methods
    //

} // class Unique
