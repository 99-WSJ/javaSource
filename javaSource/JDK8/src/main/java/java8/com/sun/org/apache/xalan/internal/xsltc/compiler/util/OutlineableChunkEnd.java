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
 * $Id: OutlineableChunkEnd.java,v 1.10 2010-11-01 04:34:19 joehw Exp $
 */
package java8.com.sun.org.apache.xalan.internal.xsltc.compiler.util;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.OutlineableChunkStart;
import java8.sun.org.apache.xalan.internal.xsltc.compiler.util.MarkerInstruction;

/**
 * <p>Marks the end of a region of byte code that can be copied into a new
 * method.  See the {@link OutlineableChunkStart} pseudo-instruction for
 * details.</p>
 */
class OutlineableChunkEnd extends MarkerInstruction {
    /**
     * A constant instance of {@link com.sun.org.apache.xalan.internal.xsltc.compiler.util.OutlineableChunkEnd}.  As it has no fields,
     * there should be no need to create an instance of this class.
     */
    public static final Instruction OUTLINEABLECHUNKEND =
                                                new com.sun.org.apache.xalan.internal.xsltc.compiler.util.OutlineableChunkEnd();

    /**
     * Private default constructor.  As it has no fields,
     * there should be no need to create an instance of this class.  See
     * {@link com.sun.org.apache.xalan.internal.xsltc.compiler.util.OutlineableChunkEnd#OUTLINEABLECHUNKEND}.
     */
    private OutlineableChunkEnd() {
    }

    /**
     * Get the name of this instruction.  Used for debugging.
     * @return the instruction name
     */
    public String getName() {
        return com.sun.org.apache.xalan.internal.xsltc.compiler.util.OutlineableChunkEnd.class.getName();
    }

    /**
     * Get the name of this instruction.  Used for debugging.
     * @return the instruction name
     */
    public String toString() {
        return getName();
    }

    /**
     * Get the name of this instruction.  Used for debugging.
     * @return the instruction name
     */
    public String toString(boolean verbose) {
        return getName();
    }
}
