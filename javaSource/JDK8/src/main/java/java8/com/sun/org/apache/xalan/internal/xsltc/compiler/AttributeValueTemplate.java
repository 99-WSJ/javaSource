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
 * $Id: AttributeValueTemplate.java,v 1.2.4.1 2005/09/01 10:26:57 pvedula Exp $
 */

package java8.com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.*;
import com.sun.org.apache.xalan.internal.xsltc.compiler.SyntaxTreeNode;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.*;
import java8.sun.org.apache.xalan.internal.xsltc.compiler.*;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * @author Jacek Ambroziak
 * @author Santiago Pericas-Geertsen
 */
final class AttributeValueTemplate extends com.sun.org.apache.xalan.internal.xsltc.compiler.AttributeValue {

    final static int OUT_EXPR = 0;
    final static int IN_EXPR  = 1;
    final static int IN_EXPR_SQUOTES = 2;
    final static int IN_EXPR_DQUOTES = 3;
    final static String DELIMITER = "\uFFFE";      // A Unicode nonchar

    public AttributeValueTemplate(String value, Parser parser,
        SyntaxTreeNode parent)
    {
        setParent(parent);
        setParser(parser);

        try {
            parseAVTemplate(value, parser);
        }
        catch (NoSuchElementException e) {
            reportError(parent, parser,
                        ErrorMsg.ATTR_VAL_TEMPLATE_ERR, value);
        }
    }

    /**
     * Two-pass parsing of ATVs. In the first pass, double curly braces are
     * replaced by one, and expressions are delimited using DELIMITER. The
     * second pass splits up the resulting buffer into literal and non-literal
     * expressions. Errors are reported during the first pass.
     */
    private void parseAVTemplate(String text, Parser parser) {
        StringTokenizer tokenizer =
            new StringTokenizer(text, "{}\"\'", true);

        /*
          * First pass: replace double curly braces and delimit expressions
          * Simple automaton to parse ATVs, delimit expressions and report
          * errors.
          */
        String t = null;
        String lookahead = null;
        StringBuffer buffer = new StringBuffer();
        int state = OUT_EXPR;

        while (tokenizer.hasMoreTokens()) {
            // Use lookahead if available
            if (lookahead != null) {
                t = lookahead;
                lookahead = null;
            }
            else {
                t = tokenizer.nextToken();
            }

            if (t.length() == 1) {
                switch (t.charAt(0)) {
                    case '{':
                        switch (state) {
                            case OUT_EXPR:
                                lookahead = tokenizer.nextToken();
                                if (lookahead.equals("{")) {
                                    buffer.append(lookahead);    // replace {{ by {
                                    lookahead = null;
                                }
                                else {
                                    buffer.append(DELIMITER);
                                    state = IN_EXPR;
                                }
                                break;
                            case IN_EXPR:
                            case IN_EXPR_SQUOTES:
                            case IN_EXPR_DQUOTES:
                                reportError(getParent(), parser,
                                            ErrorMsg.ATTR_VAL_TEMPLATE_ERR, text);
                                break;
                        }
                        break;
                    case '}':
                        switch (state) {
                            case OUT_EXPR:
                                lookahead = tokenizer.nextToken();
                                if (lookahead.equals("}")) {
                                    buffer.append(lookahead);    // replace }} by }
                                    lookahead = null;
                                }
                                else {
                                    reportError(getParent(), parser,
                                            ErrorMsg.ATTR_VAL_TEMPLATE_ERR, text);
                                }
                                break;
                            case IN_EXPR:
                                buffer.append(DELIMITER);
                                state = OUT_EXPR;
                                break;
                            case IN_EXPR_SQUOTES:
                            case IN_EXPR_DQUOTES:
                                buffer.append(t);
                                break;
                        }
                        break;
                    case '\'':
                        switch (state) {
                            case IN_EXPR:
                                state = IN_EXPR_SQUOTES;
                                break;
                            case IN_EXPR_SQUOTES:
                                state = IN_EXPR;
                                break;
                            case OUT_EXPR:
                            case IN_EXPR_DQUOTES:
                                break;
                        }
                        buffer.append(t);
                        break;
                    case '\"':
                        switch (state) {
                            case IN_EXPR:
                                state = IN_EXPR_DQUOTES;
                                break;
                            case IN_EXPR_DQUOTES:
                                state = IN_EXPR;
                                break;
                            case OUT_EXPR:
                            case IN_EXPR_SQUOTES:
                                break;
                        }
                        buffer.append(t);
                        break;
                    default:
                        buffer.append(t);
                        break;
                }
            }
            else {
                buffer.append(t);
            }
        }

        // Must be in OUT_EXPR at the end of parsing
        if (state != OUT_EXPR) {
            reportError(getParent(), parser,
                        ErrorMsg.ATTR_VAL_TEMPLATE_ERR, text);
        }

        /*
          * Second pass: split up buffer into literal and non-literal expressions.
          */
        tokenizer = new StringTokenizer(buffer.toString(), DELIMITER, true);

        while (tokenizer.hasMoreTokens()) {
            t = tokenizer.nextToken();

            if (t.equals(DELIMITER)) {
                addElement(parser.parseExpression(this, tokenizer.nextToken()));
                tokenizer.nextToken();      // consume other delimiter
            }
            else {
                addElement(new LiteralExpr(t));
            }
        }
    }

    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        final Vector contents = getContents();
        final int n = contents.size();
        for (int i = 0; i < n; i++) {
            final Expression exp = (Expression)contents.elementAt(i);
            if (!exp.typeCheck(stable).identicalTo(Type.String)) {
                contents.setElementAt(new CastExpr(exp, Type.String), i);
            }
        }
        return _type = Type.String;
    }

    public String toString() {
        final StringBuffer buffer = new StringBuffer("AVT:[");
        final int count = elementCount();
        for (int i = 0; i < count; i++) {
            buffer.append(elementAt(i).toString());
            if (i < count - 1)
                buffer.append(' ');
        }
        return buffer.append(']').toString();
    }

    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        if (elementCount() == 1) {
            final Expression exp = (Expression)elementAt(0);
            exp.translate(classGen, methodGen);
        }
        else {
            final ConstantPoolGen cpg = classGen.getConstantPool();
            final InstructionList il = methodGen.getInstructionList();
            final int initBuffer = cpg.addMethodref(STRING_BUFFER_CLASS,
                                                    "<init>", "()V");
            final Instruction append =
                new INVOKEVIRTUAL(cpg.addMethodref(STRING_BUFFER_CLASS,
                                                   "append",
                                                   "(" + STRING_SIG + ")"
                                                   + STRING_BUFFER_SIG));

            final int toString = cpg.addMethodref(STRING_BUFFER_CLASS,
                                                  "toString",
                                                  "()"+STRING_SIG);
            il.append(new NEW(cpg.addClass(STRING_BUFFER_CLASS)));
            il.append(DUP);
            il.append(new INVOKESPECIAL(initBuffer));
            // StringBuffer is on the stack
            final Enumeration elements = elements();
            while (elements.hasMoreElements()) {
                final Expression exp = (Expression)elements.nextElement();
                exp.translate(classGen, methodGen);
                il.append(append);
            }
            il.append(new INVOKEVIRTUAL(toString));
        }
    }
}
