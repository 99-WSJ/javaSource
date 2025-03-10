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
 * $Id: CastCall.java,v 1.2.4.1 2005/09/01 11:47:58 pvedula Exp $
 */

package java8.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.*;

import java.util.Vector;

/**
 * @author Santiago Pericas-Geertsen
 */
final class CastCall extends FunctionCall {

    /**
     * Name of the class that is the target of the cast. Must be a
     * fully-qualified Java class Name.
     */
    private String _className;

    /**
     * A reference to the expression being casted.
     */
    private Expression _right;

    /**
     * Constructor.
     */
    public CastCall(com.sun.org.apache.xalan.internal.xsltc.compiler.QName fname, Vector arguments) {
        super(fname, arguments);
    }

    /**
     * Type check the two parameters for this function
     */
    public Type typeCheck(com.sun.org.apache.xalan.internal.xsltc.compiler.SymbolTable stable) throws TypeCheckError {
        // Check that the function was passed exactly two arguments
        if (argumentCount() != 2) {
            throw new TypeCheckError(new ErrorMsg(ErrorMsg.ILLEGAL_ARG_ERR,
                                                  getName(), this));
        }

        // The first argument must be a literal String
        Expression exp = argument(0);
        if (exp instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.LiteralExpr) {
            _className = ((com.sun.org.apache.xalan.internal.xsltc.compiler.LiteralExpr) exp).getValue();
            _type = Type.newObjectType(_className);
        }
        else {
            throw new TypeCheckError(new ErrorMsg(ErrorMsg.NEED_LITERAL_ERR,
                                                  getName(), this));
        }

         // Second argument must be of type reference or object
        _right = argument(1);
        Type tright = _right.typeCheck(stable);
        if (tright != Type.Reference &&
            tright instanceof ObjectType == false)
        {
            throw new TypeCheckError(new ErrorMsg(ErrorMsg.DATA_CONVERSION_ERR,
                                                  tright, _type, this));
        }

        return _type;
    }

    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();

        _right.translate(classGen, methodGen);
        il.append(new CHECKCAST(cpg.addClass(_className)));
    }
}
