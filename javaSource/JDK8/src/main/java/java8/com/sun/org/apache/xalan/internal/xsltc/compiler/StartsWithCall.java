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
 * $Id: StartsWithCall.java,v 1.2.4.1 2005/09/05 09:05:28 pvedula Exp $
 */

package java8.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.*;

import java.util.Vector;

/**
 * @author Jacek Ambroziak
 * @author Santiago Pericas-Geertsen
 * @author Morten Jorgensen
 */
final class StartsWithCall extends FunctionCall {

    private Expression _base = null;
    private Expression _token = null;

    /**
     * Create a starts-with() call - two arguments, both strings
     */
    public StartsWithCall(com.sun.org.apache.xalan.internal.xsltc.compiler.QName fname, Vector arguments) {
        super(fname, arguments);
    }

    /**
     * Type check the two parameters for this function
     */
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {

        // Check that the function was passed exactly two arguments
        if (argumentCount() != 2) {
            ErrorMsg err = new ErrorMsg(ErrorMsg.ILLEGAL_ARG_ERR,
                                        getName(), this);
            throw new TypeCheckError(err);
        }

        // The first argument must be a String, or cast to a String
        _base = argument(0);
        Type baseType = _base.typeCheck(stable);
        if (baseType != Type.String)
            _base = new com.sun.org.apache.xalan.internal.xsltc.compiler.CastExpr(_base, Type.String);

        // The second argument must also be a String, or cast to a String
        _token = argument(1);
        Type tokenType = _token.typeCheck(stable);
        if (tokenType != Type.String)
            _token = new com.sun.org.apache.xalan.internal.xsltc.compiler.CastExpr(_token, Type.String);

        return _type = Type.Boolean;
    }

    /**
     * Compile the expression - leave boolean expression on stack
     */
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        _base.translate(classGen, methodGen);
        _token.translate(classGen, methodGen);
        il.append(new INVOKEVIRTUAL(cpg.addMethodref(STRING_CLASS,
                                                     "startsWith",
                                                     "("+STRING_SIG+")Z")));
    }
}
