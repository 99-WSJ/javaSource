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
 * $Id: TopLevelElement.java,v 1.5 2005/09/28 13:48:17 pvedula Exp $
 */

package java8.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.SyntaxTreeNode;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.*;

import java.util.Vector;

class TopLevelElement extends SyntaxTreeNode {

    /*
     * List of dependencies with other variables, parameters or
     * keys defined at the top level.
     */
    protected Vector _dependencies = null;

    /**
     * Type check all the children of this node.
     */
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        return typeCheckContents(stable);
    }

    /**
     * Translate this node into JVM bytecodes.
     */
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        ErrorMsg msg = new ErrorMsg(ErrorMsg.NOT_IMPLEMENTED_ERR,
                                    getClass(), this);
        getParser().reportError(FATAL, msg);
    }

    /**
     * Translate this node into a fresh instruction list.
     * The original instruction list is saved and restored.
     */
    public InstructionList compile(ClassGenerator classGen,
                                   MethodGenerator methodGen) {
        final InstructionList result, save = methodGen.getInstructionList();
        methodGen.setInstructionList(result = new InstructionList());
        translate(classGen, methodGen);
        methodGen.setInstructionList(save);
        return result;
    }

    public void display(int indent) {
        indent(indent);
        Util.println("TopLevelElement");
        displayContents(indent + IndentIncrement);
    }

    /**
     * Add a dependency with other top-level elements like
     * variables, parameters or keys.
     */
    public void addDependency(com.sun.org.apache.xalan.internal.xsltc.compiler.TopLevelElement other) {
        if (_dependencies == null) {
            _dependencies = new Vector();
        }
        if (!_dependencies.contains(other)) {
            _dependencies.addElement(other);
        }
    }

    /**
     * Get the list of dependencies with other top-level elements
     * like variables, parameteres or keys.
     */
    public Vector getDependencies() {
        return _dependencies;
    }

}
