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
 * $Id: ParentLocationPath.java,v 1.2.4.1 2005/09/12 10:56:30 pvedula Exp $
 */

package java8.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.*;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Parser;
import com.sun.org.apache.xalan.internal.xsltc.compiler.SyntaxTreeNode;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.*;
import com.sun.org.apache.xml.internal.dtm.Axis;
import com.sun.org.apache.xml.internal.dtm.DTM;

/**
 * @author Jacek Ambroziak
 * @author Santiago Pericas-Geertsen
 */
final class ParentLocationPath extends com.sun.org.apache.xalan.internal.xsltc.compiler.RelativeLocationPath {
    private Expression _step;
    private final com.sun.org.apache.xalan.internal.xsltc.compiler.RelativeLocationPath _path;
    private Type stype;
    private boolean _orderNodes = false;
    private boolean _axisMismatch = false;

    public ParentLocationPath(com.sun.org.apache.xalan.internal.xsltc.compiler.RelativeLocationPath path, Expression step) {
        _path = path;
        _step = step;
        _path.setParent(this);
        _step.setParent(this);

        if (_step instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.Step) {
            _axisMismatch = checkAxisMismatch();
        }
    }

    public void setAxis(int axis) {
        _path.setAxis(axis);
    }

    public int getAxis() {
        return _path.getAxis();
    }

    public com.sun.org.apache.xalan.internal.xsltc.compiler.RelativeLocationPath getPath() {
        return(_path);
    }

    public Expression getStep() {
        return(_step);
    }

    public void setParser(Parser parser) {
        super.setParser(parser);
        _step.setParser(parser);
        _path.setParser(parser);
    }

    public String toString() {
        return "ParentLocationPath(" + _path + ", " + _step + ')';
    }

    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        stype = _step.typeCheck(stable);
        _path.typeCheck(stable);

        if (_axisMismatch) enableNodeOrdering();

        return _type = Type.NodeSet;
    }

    public void enableNodeOrdering() {
        SyntaxTreeNode parent = getParent();
        if (parent instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.ParentLocationPath)
            ((com.sun.org.apache.xalan.internal.xsltc.compiler.ParentLocationPath)parent).enableNodeOrdering();
        else {
            _orderNodes = true;
        }
    }

    /**
     * This method is used to determine if this parent location path is a
     * combination of two step's with axes that will create duplicate or
     * unordered nodes.
     */
    public boolean checkAxisMismatch() {

        int left = _path.getAxis();
        int right = ((com.sun.org.apache.xalan.internal.xsltc.compiler.Step)_step).getAxis();

        if (((left == Axis.ANCESTOR) || (left == Axis.ANCESTORORSELF)) &&
            ((right == Axis.CHILD) ||
             (right == Axis.DESCENDANT) ||
             (right == Axis.DESCENDANTORSELF) ||
             (right == Axis.PARENT) ||
             (right == Axis.PRECEDING) ||
             (right == Axis.PRECEDINGSIBLING)))
            return true;

        if ((left == Axis.CHILD) &&
            (right == Axis.ANCESTOR) ||
            (right == Axis.ANCESTORORSELF) ||
            (right == Axis.PARENT) ||
            (right == Axis.PRECEDING))
            return true;

        if ((left == Axis.DESCENDANT) || (left == Axis.DESCENDANTORSELF))
            return true;

        if (((left == Axis.FOLLOWING) || (left == Axis.FOLLOWINGSIBLING)) &&
            ((right == Axis.FOLLOWING) ||
             (right == Axis.PARENT) ||
             (right == Axis.PRECEDING) ||
             (right == Axis.PRECEDINGSIBLING)))
            return true;

        if (((left == Axis.PRECEDING) || (left == Axis.PRECEDINGSIBLING)) &&
            ((right == Axis.DESCENDANT) ||
             (right == Axis.DESCENDANTORSELF) ||
             (right == Axis.FOLLOWING) ||
             (right == Axis.FOLLOWINGSIBLING) ||
             (right == Axis.PARENT) ||
             (right == Axis.PRECEDING) ||
             (right == Axis.PRECEDINGSIBLING)))
            return true;

        if ((right == Axis.FOLLOWING) && (left == Axis.CHILD)) {
            // Special case for '@*/following::*' expressions. The resulting
            // iterator is initialised with the parent's first child, and this
            // can cause duplicates in the output if the parent has more than
            // one attribute that matches the left step.
            if (_path instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.Step) {
                int type = ((com.sun.org.apache.xalan.internal.xsltc.compiler.Step)_path).getNodeType();
                if (type == DTM.ATTRIBUTE_NODE) return true;
            }
        }

        return false;
    }

    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {

        // Compile path iterator
        _path.translate(classGen, methodGen); // iterator on stack....

        translateStep(classGen, methodGen);
    }

    public void translateStep(ClassGenerator classGen, MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();

        // Backwards branches are prohibited if an uninitialized object is
        // on the stack by section 4.9.4 of the JVM Specification, 2nd Ed.
        // We don't know whether this code might contain backwards branches
        // so we mustn't create the new object until after we've created
        // the suspect arguments to its constructor.  Instead we calculate
        // the values of the arguments to the constructor first, store them
        // in temporary variables, create the object and reload the
        // arguments from the temporaries to avoid the problem.

        LocalVariableGen pathTemp
                = methodGen.addLocalVariable("parent_location_path_tmp1",
                                         Util.getJCRefType(NODE_ITERATOR_SIG),
                                         null, null);
        pathTemp.setStart(il.append(new ASTORE(pathTemp.getIndex())));

        _step.translate(classGen, methodGen);
        LocalVariableGen stepTemp
                = methodGen.addLocalVariable("parent_location_path_tmp2",
                                         Util.getJCRefType(NODE_ITERATOR_SIG),
                                         null, null);
        stepTemp.setStart(il.append(new ASTORE(stepTemp.getIndex())));

        // Create new StepIterator
        final int initSI = cpg.addMethodref(STEP_ITERATOR_CLASS,
                                            "<init>",
                                            "("
                                            +NODE_ITERATOR_SIG
                                            +NODE_ITERATOR_SIG
                                            +")V");
        il.append(new NEW(cpg.addClass(STEP_ITERATOR_CLASS)));
        il.append(DUP);

        pathTemp.setEnd(il.append(new ALOAD(pathTemp.getIndex())));
        stepTemp.setEnd(il.append(new ALOAD(stepTemp.getIndex())));

        // Initialize StepIterator with iterators from the stack
        il.append(new INVOKESPECIAL(initSI));

        // This is a special case for the //* path with or without predicates
        Expression stp = _step;
        if (stp instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.ParentLocationPath)
            stp = ((com.sun.org.apache.xalan.internal.xsltc.compiler.ParentLocationPath)stp).getStep();

        if ((_path instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.Step) && (stp instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.Step)) {
            final int path = ((com.sun.org.apache.xalan.internal.xsltc.compiler.Step)_path).getAxis();
            final int step = ((com.sun.org.apache.xalan.internal.xsltc.compiler.Step)stp).getAxis();
            if ((path == Axis.DESCENDANTORSELF && step == Axis.CHILD) ||
                (path == Axis.PRECEDING        && step == Axis.PARENT)) {
                final int incl = cpg.addMethodref(NODE_ITERATOR_BASE,
                                                  "includeSelf",
                                                  "()" + NODE_ITERATOR_SIG);
                il.append(new INVOKEVIRTUAL(incl));
            }
        }

        /*
         * If this pattern contains a sequence of descendant iterators we
         * run the risk of returning the same node several times. We put
         * a new iterator on top of the existing one to assure node order
         * and prevent returning a single node multiple times.
         */
        if (_orderNodes) {
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
