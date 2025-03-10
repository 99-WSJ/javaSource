/*
 * Copyright (c) 2007-2012, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * Copyright 2001-2005 The Apache Software Foundation.
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
 * $Id: FilterParentPath.java,v 1.2.4.1 2005/09/12 10:24:55 pvedula Exp $
 */

package java8.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.*;
import com.sun.org.apache.xalan.internal.xsltc.compiler.SyntaxTreeNode;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ReferenceType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.*;

/**
 * @author Jacek Ambroziak
 * @author Santiago Pericas-Geertsen
 */
final class FilterParentPath extends Expression {

    private Expression _filterExpr;
    private Expression _path;
    private boolean _hasDescendantAxis = false;

    public FilterParentPath(Expression filterExpr, Expression path) {
        (_path = path).setParent(this);
        (_filterExpr = filterExpr).setParent(this);
    }

    public void setParser(Parser parser) {
        super.setParser(parser);
        _filterExpr.setParser(parser);
        _path.setParser(parser);
    }

    public String toString() {
        return "FilterParentPath(" + _filterExpr + ", " + _path + ')';
    }

    public void setDescendantAxis() {
        _hasDescendantAxis = true;
    }

    /**
     * Type check a FilterParentPath. If the filter is not a node-set add a
     * cast to node-set only if it is of reference type. This type coercion is
     * needed for expressions like $x/LINE where $x is a parameter reference.
     */
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        final Type ftype = _filterExpr.typeCheck(stable);
        if (ftype instanceof NodeSetType == false) {
            if (ftype instanceof ReferenceType)  {
                _filterExpr = new com.sun.org.apache.xalan.internal.xsltc.compiler.CastExpr(_filterExpr, Type.NodeSet);
            }
            /*
            else if (ftype instanceof ResultTreeType)  {
                _filterExpr = new CastExpr(_filterExpr, Type.NodeSet);
            }
            */
            else if (ftype instanceof NodeType)  {
                _filterExpr = new com.sun.org.apache.xalan.internal.xsltc.compiler.CastExpr(_filterExpr, Type.NodeSet);
            }
            else {
                throw new TypeCheckError(this);
            }
        }

        // Wrap single node path in a node set
        final Type ptype = _path.typeCheck(stable);
        if (!(ptype instanceof NodeSetType)) {
            _path = new com.sun.org.apache.xalan.internal.xsltc.compiler.CastExpr(_path, Type.NodeSet);
        }

        return _type = Type.NodeSet;
    }

    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        // Create new StepIterator
        final int initSI = cpg.addMethodref(STEP_ITERATOR_CLASS,
                                            "<init>",
                                            "("
                                            +NODE_ITERATOR_SIG
                                            +NODE_ITERATOR_SIG
                                            +")V");

        // Backwards branches are prohibited if an uninitialized object is
        // on the stack by section 4.9.4 of the JVM Specification, 2nd Ed.
        // We don't know whether this code might contain backwards branches,
        // so we mustn't create the new object until after we've created
        // the suspect arguments to its constructor.  Instead we calculate
        // the values of the arguments to the constructor first, store them
        // in temporary variables, create the object and reload the
        // arguments from the temporaries to avoid the problem.

        // Recursively compile 2 iterators
        _filterExpr.translate(classGen, methodGen);
        LocalVariableGen filterTemp =
                methodGen.addLocalVariable("filter_parent_path_tmp1",
                                           Util.getJCRefType(NODE_ITERATOR_SIG),
                                           null, null);
        filterTemp.setStart(il.append(new ASTORE(filterTemp.getIndex())));

        _path.translate(classGen, methodGen);
        LocalVariableGen pathTemp =
                methodGen.addLocalVariable("filter_parent_path_tmp2",
                                           Util.getJCRefType(NODE_ITERATOR_SIG),
                                           null, null);
        pathTemp.setStart(il.append(new ASTORE(pathTemp.getIndex())));

        il.append(new NEW(cpg.addClass(STEP_ITERATOR_CLASS)));
        il.append(DUP);
        filterTemp.setEnd(il.append(new ALOAD(filterTemp.getIndex())));
        pathTemp.setEnd(il.append(new ALOAD(pathTemp.getIndex())));

        // Initialize StepIterator with iterators from the stack
        il.append(new INVOKESPECIAL(initSI));

        // This is a special case for the //* path with or without predicates
        if (_hasDescendantAxis) {
            final int incl = cpg.addMethodref(NODE_ITERATOR_BASE,
                                              "includeSelf",
                                              "()" + NODE_ITERATOR_SIG);
            il.append(new INVOKEVIRTUAL(incl));
        }

        SyntaxTreeNode parent = getParent();

        boolean parentAlreadyOrdered =
            (parent instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.RelativeLocationPath)
                || (parent instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.FilterParentPath)
                || (parent instanceof KeyCall)
                || (parent instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.CurrentCall)
                || (parent instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.DocumentCall);

        if (!parentAlreadyOrdered) {
            final int order = cpg.addInterfaceMethodref(DOM_INTF,
                                                        ORDER_ITERATOR,
                                                        ORDER_ITERATOR_SIG);
            il.append(methodGen.loadDOM());
            il.append(SWAP);
            il.append(methodGen.loadContextNode());
            il.append(new INVOKEINTERFACE(order, 3));
        }
    }
}
