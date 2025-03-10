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
 * $Id: CallTemplate.java,v 1.2.4.1 2005/09/12 10:02:41 pvedula Exp $
 */

package java8.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Constants;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Parser;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Stylesheet;
import com.sun.org.apache.xalan.internal.xsltc.compiler.SyntaxTreeNode;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Template;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.*;
import com.sun.org.apache.xml.internal.utils.XML11Char;

import java.util.Vector;

/**
 * @author Jacek Ambroziak
 * @author Santiago Pericas-Geertsen
 * @author Erwin Bolwidt <ejb@klomp.org>
 */
final class CallTemplate extends Instruction {

    /**
     * Name of template to call.
     */
    private com.sun.org.apache.xalan.internal.xsltc.compiler.QName _name;

    /**
     * The array of effective parameters in this CallTemplate. An object in
     * this array can be either a WithParam or a Param if no WithParam
     * exists for a particular parameter.
     */
    private Object[] _parameters = null;

    /**
     * The corresponding template which this CallTemplate calls.
     */
    private Template _calleeTemplate = null;

    public void display(int indent) {
        indent(indent);
        System.out.print("CallTemplate");
        Util.println(" name " + _name);
        displayContents(indent + IndentIncrement);
    }

    public boolean hasWithParams() {
        return elementCount() > 0;
    }

    public void parseContents(Parser parser) {
        final String name = getAttribute("name");
        if (name.length() > 0) {
            if (!XML11Char.isXML11ValidQName(name)) {
                ErrorMsg err = new ErrorMsg(ErrorMsg.INVALID_QNAME_ERR, name, this);
                parser.reportError(Constants.ERROR, err);
            }
            _name = parser.getQNameIgnoreDefaultNs(name);
        }
        else {
            reportError(this, parser, ErrorMsg.REQUIRED_ATTR_ERR, "name");
        }
        parseChildren(parser);
    }

    /**
     * Verify that a template with this name exists.
     */
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        final Template template = stable.lookupTemplate(_name);
        if (template != null) {
            typeCheckContents(stable);
        }
        else {
            ErrorMsg err = new ErrorMsg(ErrorMsg.TEMPLATE_UNDEF_ERR,_name,this);
            throw new TypeCheckError(err);
        }
        return Type.Void;
    }

    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        final Stylesheet stylesheet = classGen.getStylesheet();
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();

        // If there are Params in the stylesheet or WithParams in this call?
        if (stylesheet.hasLocalParams() || hasContents()) {
            _calleeTemplate = getCalleeTemplate();

            // Build the parameter list if the called template is simple named
            if (_calleeTemplate != null) {
                buildParameterList();
            }
            // This is only needed when the called template is not
            // a simple named template.
            else {
                // Push parameter frame
                final int push = cpg.addMethodref(TRANSLET_CLASS,
                                                  PUSH_PARAM_FRAME,
                                                  PUSH_PARAM_FRAME_SIG);
                il.append(classGen.loadTranslet());
                il.append(new INVOKEVIRTUAL(push));
                translateContents(classGen, methodGen);
            }
        }

        // Generate a valid Java method name
        final String className = stylesheet.getClassName();
        String methodName = Util.escape(_name.toString());

        // Load standard arguments
        il.append(classGen.loadTranslet());
        il.append(methodGen.loadDOM());
        il.append(methodGen.loadIterator());
        il.append(methodGen.loadHandler());
        il.append(methodGen.loadCurrentNode());

        // Initialize prefix of method signature
        StringBuffer methodSig = new StringBuffer("(" + DOM_INTF_SIG
            + NODE_ITERATOR_SIG + TRANSLET_OUTPUT_SIG + NODE_SIG);

        // If calling a simply named template, push actual arguments
        if (_calleeTemplate != null) {
            Vector calleeParams = _calleeTemplate.getParameters();
            int numParams = _parameters.length;

            for (int i = 0; i < numParams; i++) {
                SyntaxTreeNode node = (SyntaxTreeNode)_parameters[i];
                methodSig.append(OBJECT_SIG);   // append Object to signature

                // Push 'null' if Param to indicate no actual parameter specified
                if (node instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.Param) {
                    il.append(ACONST_NULL);
                }
                else {  // translate WithParam
                    node.translate(classGen, methodGen);
                }
            }
        }

        // Complete signature and generate invokevirtual call
        methodSig.append(")V");
        il.append(new INVOKEVIRTUAL(cpg.addMethodref(className,
                                                     methodName,
                                                     methodSig.toString())));

        // Do not need to call Translet.popParamFrame() if we are
        // calling a simple named template.
        if (_calleeTemplate == null && (stylesheet.hasLocalParams() || hasContents())) {
            // Pop parameter frame
            final int pop = cpg.addMethodref(TRANSLET_CLASS,
                                             POP_PARAM_FRAME,
                                             POP_PARAM_FRAME_SIG);
            il.append(classGen.loadTranslet());
            il.append(new INVOKEVIRTUAL(pop));
        }
    }

    /**
     * Return the simple named template which this CallTemplate calls.
     * Return false if there is no matched template or the matched
     * template is not a simple named template.
     */
    public Template getCalleeTemplate() {
        Template foundTemplate
            = getXSLTC().getParser().getSymbolTable().lookupTemplate(_name);

        return foundTemplate.isSimpleNamedTemplate() ? foundTemplate : null;
    }

    /**
     * Build the list of effective parameters in this CallTemplate.
     * The parameters of the called template are put into the array first.
     * Then we visit the WithParam children of this CallTemplate and replace
     * the Param with a corresponding WithParam having the same name.
     */
    private void buildParameterList() {
        // Put the parameters from the called template into the array first.
        // This is to ensure the order of the parameters.
        Vector defaultParams = _calleeTemplate.getParameters();
        int numParams = defaultParams.size();
        _parameters = new Object[numParams];
        for (int i = 0; i < numParams; i++) {
            _parameters[i] = defaultParams.elementAt(i);
        }

        // Replace a Param with a WithParam if they have the same name.
        int count = elementCount();
        for (int i = 0; i < count; i++) {
            Object node = elementAt(i);

            // Ignore if not WithParam
            if (node instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.WithParam) {
                com.sun.org.apache.xalan.internal.xsltc.compiler.WithParam withParam = (com.sun.org.apache.xalan.internal.xsltc.compiler.WithParam)node;
                com.sun.org.apache.xalan.internal.xsltc.compiler.QName name = withParam.getName();

                // Search for a Param with the same name
                for (int k = 0; k < numParams; k++) {
                    Object object = _parameters[k];
                    if (object instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.Param
                        && ((com.sun.org.apache.xalan.internal.xsltc.compiler.Param)object).getName().equals(name)) {
                        withParam.setDoParameterOptimization(true);
                        _parameters[k] = withParam;
                        break;
                    }
                    else if (object instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.WithParam
                        && ((com.sun.org.apache.xalan.internal.xsltc.compiler.WithParam)object).getName().equals(name)) {
                        withParam.setDoParameterOptimization(true);
                        _parameters[k] = withParam;
                        break;
                    }
                }
            }
        }
     }
}
