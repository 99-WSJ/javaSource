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
 * $Id: Copy.java,v 1.2.4.1 2005/09/01 12:14:32 pvedula Exp $
 */

package java8.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.*;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Constants;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Parser;
import com.sun.org.apache.xalan.internal.xsltc.compiler.SyntaxTreeNode;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.*;

/**
 * @author Jacek Ambroziak
 * @author Santiago Pericas-Geertsen
 */
final class Copy extends com.sun.org.apache.xalan.internal.xsltc.compiler.Instruction {
    private com.sun.org.apache.xalan.internal.xsltc.compiler.UseAttributeSets _useSets;

    public void parseContents(Parser parser) {
        final String useSets = getAttribute("use-attribute-sets");
        if (useSets.length() > 0) {
            if (!Util.isValidQNames(useSets)) {
                ErrorMsg err = new ErrorMsg(ErrorMsg.INVALID_QNAME_ERR, useSets, this);
                parser.reportError(Constants.ERROR, err);
            }
            _useSets = new com.sun.org.apache.xalan.internal.xsltc.compiler.UseAttributeSets(useSets, parser);
        }
        parseChildren(parser);
    }

    public void display(int indent) {
        indent(indent);
        Util.println("Copy");
        indent(indent + IndentIncrement);
        displayContents(indent + IndentIncrement);
    }

    public Type typeCheck(com.sun.org.apache.xalan.internal.xsltc.compiler.SymbolTable stable) throws TypeCheckError {
        if (_useSets != null) {
            _useSets.typeCheck(stable);
        }
        typeCheckContents(stable);
        return Type.Void;
    }

    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();

        final LocalVariableGen name =
            methodGen.addLocalVariable2("name",
                                        Util.getJCRefType(STRING_SIG),
                                        null);
        final LocalVariableGen length =
            methodGen.addLocalVariable2("length",
                                        Util.getJCRefType("I"),
                                        null);

        // Get the name of the node to copy and save for later
        il.append(methodGen.loadDOM());
        il.append(methodGen.loadCurrentNode());
        il.append(methodGen.loadHandler());
        final int cpy = cpg.addInterfaceMethodref(DOM_INTF,
                                                  "shallowCopy",
                                                  "("
                                                  + NODE_SIG
                                                  + TRANSLET_OUTPUT_SIG
                                                  + ")" + STRING_SIG);
        il.append(new INVOKEINTERFACE(cpy, 3));
        il.append(DUP);
        name.setStart(il.append(new ASTORE(name.getIndex())));
        final BranchHandle ifBlock1 = il.append(new IFNULL(null));

        // Get the length of the node name and save for later
        il.append(new ALOAD(name.getIndex()));
        final int lengthMethod = cpg.addMethodref(STRING_CLASS,"length","()I");
        il.append(new INVOKEVIRTUAL(lengthMethod));
        il.append(DUP);
        length.setStart(il.append(new ISTORE(length.getIndex())));

        // Ignore attribute sets if current node is ROOT. DOM.shallowCopy()
        // returns "" for ROOT, so skip attribute sets if length == 0
        final BranchHandle ifBlock4 = il.append(new IFEQ(null));

        // Copy in attribute sets if specified
        if (_useSets != null) {
            // If the parent of this element will result in an element being
            // output then we know that it is safe to copy out the attributes
            final SyntaxTreeNode parent = getParent();
            if ((parent instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.LiteralElement) ||
                (parent instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.LiteralElement)) {
                _useSets.translate(classGen, methodGen);
            }
            // If not we have to check to see if the copy will result in an
            // element being output.
            else {
                // check if element; if not skip to translate body
                il.append(new ILOAD(length.getIndex()));
                final BranchHandle ifBlock2 = il.append(new IFEQ(null));
                // length != 0 -> element -> do attribute sets
                _useSets.translate(classGen, methodGen);
                // not an element; root
                ifBlock2.setTarget(il.append(NOP));
            }
        }

        // Instantiate body of xsl:copy
        ifBlock4.setTarget(il.append(NOP));
        translateContents(classGen, methodGen);

        // Call the output handler's endElement() if we copied an element
        // (The DOM.shallowCopy() method calls startElement().)
        length.setEnd(il.append(new ILOAD(length.getIndex())));
        final BranchHandle ifBlock3 = il.append(new IFEQ(null));
        il.append(methodGen.loadHandler());
        name.setEnd(il.append(new ALOAD(name.getIndex())));
        il.append(methodGen.endElement());

        final InstructionHandle end = il.append(NOP);
        ifBlock1.setTarget(end);
        ifBlock3.setTarget(end);
        methodGen.removeLocalVariable(name);
        methodGen.removeLocalVariable(length);
    }
}
