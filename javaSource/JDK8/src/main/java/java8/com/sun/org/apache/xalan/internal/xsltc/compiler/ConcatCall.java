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
 * $Id: ConcatCall.java,v 1.2.4.1 2005/09/01 12:07:31 pvedula Exp $
 */

package java8.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.*;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;

import java.util.Vector;

/**
 * @author Jacek Ambroziak
 * @author Santiago Pericas-Geertsen
 */
final class ConcatCall extends FunctionCall {
    public ConcatCall(com.sun.org.apache.xalan.internal.xsltc.compiler.QName fname, Vector arguments) {
        super(fname, arguments);
    }

    public Type typeCheck(com.sun.org.apache.xalan.internal.xsltc.compiler.SymbolTable stable) throws TypeCheckError {
        for (int i = 0; i < argumentCount(); i++) {
            final Expression exp = argument(i);
            if (!exp.typeCheck(stable).identicalTo(Type.String)) {
                setArgument(i, new com.sun.org.apache.xalan.internal.xsltc.compiler.CastExpr(exp, Type.String));
            }
        }
        return _type = Type.String;
    }

    /** translate leaves a String on the stack */
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        final int nArgs = argumentCount();

        switch (nArgs) {
        case 0:
            il.append(new PUSH(cpg, EMPTYSTRING));
            break;

        case 1:
            argument().translate(classGen, methodGen);
            break;

        default:
            final int initBuffer = cpg.addMethodref(STRING_BUFFER_CLASS,
                                                    "<init>", "()V");
            final Instruction append =
                new INVOKEVIRTUAL(cpg.addMethodref(STRING_BUFFER_CLASS,
                                                   "append",
                                                   "("+STRING_SIG+")"
                                                   +STRING_BUFFER_SIG));

            final int toString = cpg.addMethodref(STRING_BUFFER_CLASS,
                                                  "toString",
                                                  "()"+STRING_SIG);

            il.append(new NEW(cpg.addClass(STRING_BUFFER_CLASS)));
            il.append(DUP);
            il.append(new INVOKESPECIAL(initBuffer));
            for (int i = 0; i < nArgs; i++) {
                argument(i).translate(classGen, methodGen);
                il.append(append);
            }
            il.append(new INVOKEVIRTUAL(toString));
        }
    }
}
