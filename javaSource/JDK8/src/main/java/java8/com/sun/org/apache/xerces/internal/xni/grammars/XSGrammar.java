/*
 * Copyright (c) 2007-2012, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * Copyright 2002,2004 The Apache Software Foundation.
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

package java8.sun.org.apache.xerces.internal.xni.grammars;

import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.xs.XSModel;

/**
 * Representing a schema grammar. It contains declaratoin/definitions from
 * a certain namespace. When a grammar is preparsed, and its grammar type is
 * XML Schema, it can be casted to this interface. Objects of this interface
 * can be converted to XSModel, from which further information about components
 * in this grammar can be obtained.
 *
 * @author Sandy Gao, IBM
 *
 */
public interface XSGrammar extends Grammar {

    /**
     * Return an <code>XSModel</code> that represents components in this schema
     * grammar and any schema grammars that are imported by this grammar
     * directly or indirectly.
     *
     * @return  an <code>XSModel</code> representing this schema grammar
     */
    public XSModel toXSModel();

    /**
     * Return an <code>XSModel</code> that represents components in this schema
     * grammar and the grammars in the <code>grammars</code>parameter,
     * any schema grammars that are imported by them directly or indirectly.
     *
     * @return  an <code>XSModel</code> representing these schema grammars
     */
    public XSModel toXSModel(com.sun.org.apache.xerces.internal.xni.grammars.XSGrammar[] grammars);

} // interface XSGrammar
