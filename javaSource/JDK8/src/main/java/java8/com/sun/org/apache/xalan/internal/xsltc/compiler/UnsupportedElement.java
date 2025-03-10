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
 * $Id: UnsupportedElement.java,v 1.2.4.1 2005/09/05 09:26:51 pvedula Exp $
 */

package java8.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Parser;
import com.sun.org.apache.xalan.internal.xsltc.compiler.SyntaxTreeNode;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.*;

import java.util.Vector;

/**
 * @author Morten Jorgensen
 */
final class UnsupportedElement extends SyntaxTreeNode {

    private Vector _fallbacks = null;
    private ErrorMsg _message = null;
    private boolean _isExtension = false;

    /**
     * Basic consutrcor - stores element uri/prefix/localname
     */
    public UnsupportedElement(String uri, String prefix, String local, boolean isExtension) {
        super(uri, prefix, local);
        _isExtension = isExtension;
    }

    /**
     * There are different categories of unsupported elements (believe it
     * or not): there are elements within the XSLT namespace (these would
     * be elements that are not yet implemented), there are extensions of
     * other XSLT processors and there are unrecognised extension elements
     * of this XSLT processor. The error message passed to this method
     * should describe the unsupported element itself and what category
     * the element belongs in.
     */
    public void setErrorMessage(ErrorMsg message) {
        _message = message;
    }

    /**
     * Displays the contents of this element
     */
    public void display(int indent) {
        indent(indent);
        Util.println("Unsupported element = " + _qname.getNamespace() +
                     ":" + _qname.getLocalPart());
        displayContents(indent + IndentIncrement);
    }


    /**
     * Scan and process all fallback children of the unsupported element.
     */
    private void processFallbacks(Parser parser) {

        Vector children = getContents();
        if (children != null) {
            final int count = children.size();
            for (int i = 0; i < count; i++) {
                SyntaxTreeNode child = (SyntaxTreeNode)children.elementAt(i);
                if (child instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.Fallback) {
                    com.sun.org.apache.xalan.internal.xsltc.compiler.Fallback fallback = (com.sun.org.apache.xalan.internal.xsltc.compiler.Fallback)child;
                    fallback.activate();
                    fallback.parseContents(parser);
                    if (_fallbacks == null) {
                        _fallbacks = new Vector();
                    }
                    _fallbacks.addElement(child);
                }
            }
        }
    }

    /**
     * Find any fallback in the descendant nodes; then activate & parse it
     */
    public void parseContents(Parser parser) {
        processFallbacks(parser);
    }

    /**
     * Run type check on the fallback element (if any).
     */
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        if (_fallbacks != null) {
            int count = _fallbacks.size();
            for (int i = 0; i < count; i++) {
                com.sun.org.apache.xalan.internal.xsltc.compiler.Fallback fallback = (com.sun.org.apache.xalan.internal.xsltc.compiler.Fallback)_fallbacks.elementAt(i);
                fallback.typeCheck(stable);
            }
        }
        return Type.Void;
    }

    /**
     * Translate the fallback element (if any).
     */
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        if (_fallbacks != null) {
            int count = _fallbacks.size();
            for (int i = 0; i < count; i++) {
                com.sun.org.apache.xalan.internal.xsltc.compiler.Fallback fallback = (com.sun.org.apache.xalan.internal.xsltc.compiler.Fallback)_fallbacks.elementAt(i);
                fallback.translate(classGen, methodGen);
            }
        }
        // We only go into the else block in forward-compatibility mode, when
        // the unsupported element has no fallback.
        else {
            // If the unsupported element does not have any fallback child, then
            // at runtime, a runtime error should be raised when the unsupported
            // element is instantiated. Otherwise, no error is thrown.
            ConstantPoolGen cpg = classGen.getConstantPool();
            InstructionList il = methodGen.getInstructionList();

            final int unsupportedElem = cpg.addMethodref(BASIS_LIBRARY_CLASS, "unsupported_ElementF",
                                                         "(" + STRING_SIG + "Z)V");
            il.append(new PUSH(cpg, getQName().toString()));
            il.append(new PUSH(cpg, _isExtension));
            il.append(new INVOKESTATIC(unsupportedElem));
        }
    }
}
