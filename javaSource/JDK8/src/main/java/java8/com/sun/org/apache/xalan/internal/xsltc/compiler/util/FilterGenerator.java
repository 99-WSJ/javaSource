/*
 * Copyright (c) 2007-2012, Oracle and/or its affiliates. All rights reserved.
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
 * $Id: FilterGenerator.java,v 1.2.4.1 2005/09/05 11:13:52 pvedula Exp $
 */

package java8.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Stylesheet;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;

/**
 * This class implements auxliary classes needed to compile
 * filters (predicates). These classes defined a single method
 * of type <tt>TestGenerator</tt>.
 * @author Jacek Ambroziak
 * @author Santiago Pericas-Geertsen
 */
public final class FilterGenerator extends ClassGenerator {
    private static int TRANSLET_INDEX = 5;   // translet
    private final Instruction _aloadTranslet;

    public FilterGenerator(String className, String superClassName,
                           String fileName,
                           int accessFlags, String[] interfaces,
                           Stylesheet stylesheet) {
        super(className, superClassName, fileName,
              accessFlags, interfaces, stylesheet);

        _aloadTranslet = new ALOAD(TRANSLET_INDEX);
    }

    /**
     * The index of the translet pointer within the execution of
     * the test method.
     */
    public final Instruction loadTranslet() {
        return _aloadTranslet;
    }

    /**
     * Returns <tt>true</tt> since this class is external to the
     * translet.
     */
    public boolean isExternal() {
        return true;
    }
}
