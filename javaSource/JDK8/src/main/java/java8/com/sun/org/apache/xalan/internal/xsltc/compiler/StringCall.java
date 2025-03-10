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
 * $Id: StringCall.java,v 1.2.4.1 2005/09/05 09:08:15 pvedula Exp $
 */

package java8.com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.*;
import java8.sun.org.apache.xalan.internal.xsltc.compiler.Expression;
import java8.sun.org.apache.xalan.internal.xsltc.compiler.FunctionCall;
import java8.sun.org.apache.xalan.internal.xsltc.compiler.SymbolTable;

import java.util.Vector;

/**
 * @author Jacek Ambroziak
 * @author Santiago Pericas-Geertsen
 */
final class StringCall extends FunctionCall {
    public StringCall(com.sun.org.apache.xalan.internal.xsltc.compiler.QName fname, Vector arguments) {
        super(fname, arguments);
    }

    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        final int argc = argumentCount();
        if (argc > 1) {
            ErrorMsg err = new ErrorMsg(ErrorMsg.ILLEGAL_ARG_ERR, this);
            throw new TypeCheckError(err);
        }

        if (argc > 0) {
            argument().typeCheck(stable);
        }
        return _type = Type.String;
    }

    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        final InstructionList il = methodGen.getInstructionList();
        Type targ;

        if (argumentCount() == 0) {
            il.append(methodGen.loadContextNode());
            targ = Type.Node;
        }
        else {
            final Expression arg = argument();
            arg.translate(classGen, methodGen);
            arg.startIterator(classGen, methodGen);
            targ = arg.getType();
        }

        if (!targ.identicalTo(Type.String)) {
            targ.translateTo(classGen, methodGen, Type.String);
        }
    }
}
