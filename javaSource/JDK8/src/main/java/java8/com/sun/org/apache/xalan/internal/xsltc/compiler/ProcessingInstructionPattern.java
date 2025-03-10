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
 * $Id: ProcessingInstructionPattern.java,v 1.2.4.1 2005/09/12 11:04:08 pvedula Exp $
 */

package java8.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.*;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xml.internal.dtm.Axis;
import com.sun.org.apache.xml.internal.dtm.DTM;

/**
 * @author Morten Jorgensen
 */
final class ProcessingInstructionPattern extends com.sun.org.apache.xalan.internal.xsltc.compiler.StepPattern {

    private String _name = null;
    private boolean _typeChecked = false;

    /**
     * Handles calls with no parameter (current node is implicit parameter).
     */
    public ProcessingInstructionPattern(String name) {
        super(Axis.CHILD, DTM.PROCESSING_INSTRUCTION_NODE, null);
        _name = name;
        //if (_name.equals("*")) _typeChecked = true; no wildcard allowed!
    }

    /**
     *
     */
     public double getDefaultPriority() {
        return (_name != null) ? 0.0 : -0.5;
     }
    public String toString() {
        if (_predicates == null)
            return "processing-instruction("+_name+")";
        else
            return "processing-instruction("+_name+")"+_predicates;
    }

    public void reduceKernelPattern() {
        _typeChecked = true;
    }

    public boolean isWildcard() {
        return false;
    }

    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        if (hasPredicates()) {
            // Type check all the predicates (e -> position() = e)
            final int n = _predicates.size();
            for (int i = 0; i < n; i++) {
                final com.sun.org.apache.xalan.internal.xsltc.compiler.Predicate pred = (com.sun.org.apache.xalan.internal.xsltc.compiler.Predicate)_predicates.elementAt(i);
                pred.typeCheck(stable);
            }
        }
        return Type.NodeSet;
    }

    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();

        // context node is on the stack
        int gname = cpg.addInterfaceMethodref(DOM_INTF,
                                              "getNodeName",
                                              "(I)Ljava/lang/String;");
        int cmp = cpg.addMethodref(STRING_CLASS,
                                   "equals", "(Ljava/lang/Object;)Z");

        // Push current node on the stack
        il.append(methodGen.loadCurrentNode());
        il.append(SWAP);

        // Overwrite current node with matching node
        il.append(methodGen.storeCurrentNode());

        // If pattern not reduced then check kernel
        if (!_typeChecked) {
            il.append(methodGen.loadCurrentNode());
            final int getType = cpg.addInterfaceMethodref(DOM_INTF,
                                                          "getExpandedTypeID",
                                                          "(I)I");
            il.append(methodGen.loadDOM());
            il.append(methodGen.loadCurrentNode());
            il.append(new INVOKEINTERFACE(getType, 2));
            il.append(new PUSH(cpg, DTM.PROCESSING_INSTRUCTION_NODE));
            _falseList.add(il.append(new IF_ICMPEQ(null)));
        }

        // Load the requested processing instruction name
        il.append(new PUSH(cpg, _name));
        // Load the current processing instruction's name
        il.append(methodGen.loadDOM());
        il.append(methodGen.loadCurrentNode());
        il.append(new INVOKEINTERFACE(gname, 2));
        // Compare the two strings
        il.append(new INVOKEVIRTUAL(cmp));
        _falseList.add(il.append(new IFEQ(null)));

        // Compile the expressions within the predicates
        if (hasPredicates()) {
            final int n = _predicates.size();
            for (int i = 0; i < n; i++) {
                com.sun.org.apache.xalan.internal.xsltc.compiler.Predicate pred = (com.sun.org.apache.xalan.internal.xsltc.compiler.Predicate)_predicates.elementAt(i);
                Expression exp = pred.getExpr();
                exp.translateDesynthesized(classGen, methodGen);
                _trueList.append(exp._trueList);
                _falseList.append(exp._falseList);
            }
        }

        // Backpatch true list and restore current iterator/node
        InstructionHandle restore;
        restore = il.append(methodGen.storeCurrentNode());
        backPatchTrueList(restore);
        BranchHandle skipFalse = il.append(new GOTO(null));

        // Backpatch false list and restore current iterator/node
        restore = il.append(methodGen.storeCurrentNode());
        backPatchFalseList(restore);
        _falseList.add(il.append(new GOTO(null)));

        // True list falls through
        skipFalse.setTarget(il.append(NOP));
    }
}
