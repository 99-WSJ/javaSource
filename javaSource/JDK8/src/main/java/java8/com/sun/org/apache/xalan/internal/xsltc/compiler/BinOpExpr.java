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
 * $Id: BinOpExpr.java,v 1.2.4.1 2005/09/01 11:42:27 pvedula Exp $
 */

package java8.com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.*;
import java8.sun.org.apache.xalan.internal.xsltc.compiler.*;

/**
 * @author Jacek Ambroziak
 * @author Santiago Pericas-Geertsen
 */
final class BinOpExpr extends Expression {
    public static final int PLUS  = 0;
    public static final int MINUS = 1;
    public static final int TIMES = 2;
    public static final int DIV   = 3;
    public static final int MOD   = 4;

    private static final String[] Ops = {
        "+", "-", "*", "/", "%"
    };

    private int _op;
    private Expression _left, _right;

    public BinOpExpr(int op, Expression left, Expression right) {
        _op = op;
        (_left = left).setParent(this);
        (_right = right).setParent(this);
    }

    /**
     * Returns true if this expressions contains a call to position(). This is
     * needed for context changes in node steps containing multiple predicates.
     */
    public boolean hasPositionCall() {
        if (_left.hasPositionCall()) return true;
        if (_right.hasPositionCall()) return true;
        return false;
    }

    /**
     * Returns true if this expressions contains a call to last()
     */
    public boolean hasLastCall() {
            return (_left.hasLastCall() || _right.hasLastCall());
    }

    public void setParser(Parser parser) {
        super.setParser(parser);
        _left.setParser(parser);
        _right.setParser(parser);
    }

    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        final Type tleft = _left.typeCheck(stable);
        final Type tright = _right.typeCheck(stable);
        final MethodType ptype = lookupPrimop(stable, Ops[_op],
                                              new MethodType(Type.Void,
                                                             tleft, tright));
        if (ptype != null) {
            final Type arg1 = (Type) ptype.argsType().elementAt(0);
            if (!arg1.identicalTo(tleft)) {
                _left = new CastExpr(_left, arg1);
            }
            final Type arg2 = (Type) ptype.argsType().elementAt(1);
            if (!arg2.identicalTo(tright)) {
                _right = new CastExpr(_right, arg1);
            }
            return _type = ptype.resultType();
        }
        throw new TypeCheckError(this);
    }

    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        final InstructionList il = methodGen.getInstructionList();

        _left.translate(classGen, methodGen);
        _right.translate(classGen, methodGen);

        switch (_op) {
        case PLUS:
            il.append(_type.ADD());
            break;
        case MINUS:
            il.append(_type.SUB());
            break;
        case TIMES:
            il.append(_type.MUL());
            break;
        case DIV:
            il.append(_type.DIV());
            break;
        case MOD:
            il.append(_type.REM());
            break;
        default:
            ErrorMsg msg = new ErrorMsg(ErrorMsg.ILLEGAL_BINARY_OP_ERR, this);
            getParser().reportError(Constants.ERROR, msg);
        }
    }

    public String toString() {
        return Ops[_op] + '(' + _left + ", " + _right + ')';
    }
}
