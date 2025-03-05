/*
 * Copyright (c) 2007-2012, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * Copyright 1999-2002,2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package java8.sun.org.apache.xerces.internal.impl.xpath.regex;

import java.util.Vector;

/**
 * @xerces.internal
 *
 */
class Op {
    static final int DOT = 0;
    static final int CHAR = 1;                  // Single character
    static final int RANGE = 3;                 // [a-zA-Z]
    static final int NRANGE = 4;                // [^a-zA-Z]
    static final int ANCHOR = 5;                // ^ $ ...
    static final int STRING = 6;                // literal String
    static final int CLOSURE = 7;               // X*
    static final int NONGREEDYCLOSURE = 8;      // X*?
    static final int QUESTION = 9;              // X?
    static final int NONGREEDYQUESTION = 10;    // X??
    static final int UNION = 11;                // X|Y
    static final int CAPTURE = 15;              // ( and )
    static final int BACKREFERENCE = 16;        // \1 \2 ...
    static final int LOOKAHEAD = 20;            // (?=...)
    static final int NEGATIVELOOKAHEAD = 21;    // (?!...)
    static final int LOOKBEHIND = 22;           // (?<=...)
    static final int NEGATIVELOOKBEHIND = 23;   // (?<!...)
    static final int INDEPENDENT = 24;          // (?>...)
    static final int MODIFIER = 25;             // (?ims-ims:...)
    static final int CONDITION = 26;            // (?(..)yes|no)

    static int nofinstances = 0;
    static final boolean COUNT = false;

    static com.sun.org.apache.xerces.internal.impl.xpath.regex.Op createDot() {
        if (com.sun.org.apache.xerces.internal.impl.xpath.regex.Op.COUNT)  com.sun.org.apache.xerces.internal.impl.xpath.regex.Op.nofinstances ++;
        return new com.sun.org.apache.xerces.internal.impl.xpath.regex.Op(com.sun.org.apache.xerces.internal.impl.xpath.regex.Op.DOT);
    }
    static CharOp createChar(int data) {
        if (com.sun.org.apache.xerces.internal.impl.xpath.regex.Op.COUNT)  com.sun.org.apache.xerces.internal.impl.xpath.regex.Op.nofinstances ++;
        return new CharOp(com.sun.org.apache.xerces.internal.impl.xpath.regex.Op.CHAR, data);
    }
    static CharOp createAnchor(int data) {
        if (com.sun.org.apache.xerces.internal.impl.xpath.regex.Op.COUNT)  com.sun.org.apache.xerces.internal.impl.xpath.regex.Op.nofinstances ++;
        return new CharOp(com.sun.org.apache.xerces.internal.impl.xpath.regex.Op.ANCHOR, data);
    }
    static CharOp createCapture(int number, com.sun.org.apache.xerces.internal.impl.xpath.regex.Op next) {
        if (com.sun.org.apache.xerces.internal.impl.xpath.regex.Op.COUNT)  com.sun.org.apache.xerces.internal.impl.xpath.regex.Op.nofinstances ++;
        CharOp op = new CharOp(com.sun.org.apache.xerces.internal.impl.xpath.regex.Op.CAPTURE, number);
        op.next = next;
        return op;
    }
    static UnionOp createUnion(int size) {
        if (com.sun.org.apache.xerces.internal.impl.xpath.regex.Op.COUNT)  com.sun.org.apache.xerces.internal.impl.xpath.regex.Op.nofinstances ++;
        //System.err.println("Creates UnionOp");
        return new UnionOp(com.sun.org.apache.xerces.internal.impl.xpath.regex.Op.UNION, size);
    }
    static ChildOp createClosure(int id) {
        if (com.sun.org.apache.xerces.internal.impl.xpath.regex.Op.COUNT)  com.sun.org.apache.xerces.internal.impl.xpath.regex.Op.nofinstances ++;
        return new ModifierOp(com.sun.org.apache.xerces.internal.impl.xpath.regex.Op.CLOSURE, id, -1);
    }
    static ChildOp createNonGreedyClosure() {
        if (com.sun.org.apache.xerces.internal.impl.xpath.regex.Op.COUNT)  com.sun.org.apache.xerces.internal.impl.xpath.regex.Op.nofinstances ++;
        return new ChildOp(com.sun.org.apache.xerces.internal.impl.xpath.regex.Op.NONGREEDYCLOSURE);
    }
    static ChildOp createQuestion(boolean nongreedy) {
        if (com.sun.org.apache.xerces.internal.impl.xpath.regex.Op.COUNT)  com.sun.org.apache.xerces.internal.impl.xpath.regex.Op.nofinstances ++;
        return new ChildOp(nongreedy ? com.sun.org.apache.xerces.internal.impl.xpath.regex.Op.NONGREEDYQUESTION : com.sun.org.apache.xerces.internal.impl.xpath.regex.Op.QUESTION);
    }
    static RangeOp createRange(Token tok) {
        if (com.sun.org.apache.xerces.internal.impl.xpath.regex.Op.COUNT)  com.sun.org.apache.xerces.internal.impl.xpath.regex.Op.nofinstances ++;
        return new RangeOp(com.sun.org.apache.xerces.internal.impl.xpath.regex.Op.RANGE, tok);
    }
    static ChildOp createLook(int type, com.sun.org.apache.xerces.internal.impl.xpath.regex.Op next, com.sun.org.apache.xerces.internal.impl.xpath.regex.Op branch) {
        if (com.sun.org.apache.xerces.internal.impl.xpath.regex.Op.COUNT)  com.sun.org.apache.xerces.internal.impl.xpath.regex.Op.nofinstances ++;
        ChildOp op = new ChildOp(type);
        op.setChild(branch);
        op.next = next;
        return op;
    }
    static CharOp createBackReference(int refno) {
        if (com.sun.org.apache.xerces.internal.impl.xpath.regex.Op.COUNT)  com.sun.org.apache.xerces.internal.impl.xpath.regex.Op.nofinstances ++;
        return new CharOp(com.sun.org.apache.xerces.internal.impl.xpath.regex.Op.BACKREFERENCE, refno);
    }
    static StringOp createString(String literal) {
        if (com.sun.org.apache.xerces.internal.impl.xpath.regex.Op.COUNT)  com.sun.org.apache.xerces.internal.impl.xpath.regex.Op.nofinstances ++;
        return new StringOp(com.sun.org.apache.xerces.internal.impl.xpath.regex.Op.STRING, literal);
    }
    static ChildOp createIndependent(com.sun.org.apache.xerces.internal.impl.xpath.regex.Op next, com.sun.org.apache.xerces.internal.impl.xpath.regex.Op branch) {
        if (com.sun.org.apache.xerces.internal.impl.xpath.regex.Op.COUNT)  com.sun.org.apache.xerces.internal.impl.xpath.regex.Op.nofinstances ++;
        ChildOp op = new ChildOp(com.sun.org.apache.xerces.internal.impl.xpath.regex.Op.INDEPENDENT);
        op.setChild(branch);
        op.next = next;
        return op;
    }
    static ModifierOp createModifier(com.sun.org.apache.xerces.internal.impl.xpath.regex.Op next, com.sun.org.apache.xerces.internal.impl.xpath.regex.Op branch, int add, int mask) {
        if (com.sun.org.apache.xerces.internal.impl.xpath.regex.Op.COUNT)  com.sun.org.apache.xerces.internal.impl.xpath.regex.Op.nofinstances ++;
        ModifierOp op = new ModifierOp(com.sun.org.apache.xerces.internal.impl.xpath.regex.Op.MODIFIER, add, mask);
        op.setChild(branch);
        op.next = next;
        return op;
    }
    static ConditionOp createCondition(com.sun.org.apache.xerces.internal.impl.xpath.regex.Op next, int ref, com.sun.org.apache.xerces.internal.impl.xpath.regex.Op conditionflow, com.sun.org.apache.xerces.internal.impl.xpath.regex.Op yesflow, com.sun.org.apache.xerces.internal.impl.xpath.regex.Op noflow) {
        if (com.sun.org.apache.xerces.internal.impl.xpath.regex.Op.COUNT)  com.sun.org.apache.xerces.internal.impl.xpath.regex.Op.nofinstances ++;
        ConditionOp op = new ConditionOp(com.sun.org.apache.xerces.internal.impl.xpath.regex.Op.CONDITION, ref, conditionflow, yesflow, noflow);
        op.next = next;
        return op;
    }

    int type;
    com.sun.org.apache.xerces.internal.impl.xpath.regex.Op next = null;

    protected Op(int type) {
        this.type = type;
    }

    int size() {                                // for UNION
        return 0;
    }
    com.sun.org.apache.xerces.internal.impl.xpath.regex.Op elementAt(int index) {                   // for UNIoN
        throw new RuntimeException("Internal Error: type="+this.type);
    }
    com.sun.org.apache.xerces.internal.impl.xpath.regex.Op getChild() {                             // for CLOSURE, QUESTION
        throw new RuntimeException("Internal Error: type="+this.type);
    }
                                                // ModifierOp
    int getData() {                             // CharOp  for CHAR, BACKREFERENCE, CAPTURE, ANCHOR,
        throw new RuntimeException("Internal Error: type="+this.type);
    }
    int getData2() {                            // ModifierOp
        throw new RuntimeException("Internal Error: type="+this.type);
    }
    com.sun.org.apache.xerces.internal.impl.xpath.regex.RangeToken getToken() {                     // RANGE, NRANGE
        throw new RuntimeException("Internal Error: type="+this.type);
    }
    String getString() {                        // STRING
        throw new RuntimeException("Internal Error: type="+this.type);
    }

    // ================================================================
    static class CharOp extends com.sun.org.apache.xerces.internal.impl.xpath.regex.Op {
        int charData;
        CharOp(int type, int data) {
            super(type);
            this.charData = data;
        }
        int getData() {
            return this.charData;
        }
    }

    // ================================================================
    static class UnionOp extends com.sun.org.apache.xerces.internal.impl.xpath.regex.Op {
        Vector branches;
        UnionOp(int type, int size) {
            super(type);
            this.branches = new Vector(size);
        }
        void addElement(com.sun.org.apache.xerces.internal.impl.xpath.regex.Op op) {
            this.branches.addElement(op);
        }
        int size() {
            return this.branches.size();
        }
        com.sun.org.apache.xerces.internal.impl.xpath.regex.Op elementAt(int index) {
            return (com.sun.org.apache.xerces.internal.impl.xpath.regex.Op)this.branches.elementAt(index);
        }
    }

    // ================================================================
    static class ChildOp extends com.sun.org.apache.xerces.internal.impl.xpath.regex.Op {
        com.sun.org.apache.xerces.internal.impl.xpath.regex.Op child;
        ChildOp(int type) {
            super(type);
        }
        void setChild(com.sun.org.apache.xerces.internal.impl.xpath.regex.Op child) {
            this.child = child;
        }
        com.sun.org.apache.xerces.internal.impl.xpath.regex.Op getChild() {
            return this.child;
        }
    }
    // ================================================================
    static class ModifierOp extends ChildOp {
        int v1;
        int v2;
        ModifierOp(int type, int v1, int v2) {
            super(type);
            this.v1 = v1;
            this.v2 = v2;
        }
        int getData() {
            return this.v1;
        }
        int getData2() {
            return this.v2;
        }
    }
    // ================================================================
    static class RangeOp extends com.sun.org.apache.xerces.internal.impl.xpath.regex.Op {
        Token tok;
        RangeOp(int type, Token tok) {
            super(type);
            this.tok = tok;
        }
        com.sun.org.apache.xerces.internal.impl.xpath.regex.RangeToken getToken() {
            return (com.sun.org.apache.xerces.internal.impl.xpath.regex.RangeToken)this.tok;
        }
    }
    // ================================================================
    static class StringOp extends com.sun.org.apache.xerces.internal.impl.xpath.regex.Op {
        String string;
        StringOp(int type, String literal) {
            super(type);
            this.string = literal;
        }
        String getString() {
            return this.string;
        }
    }
    // ================================================================
    static class ConditionOp extends com.sun.org.apache.xerces.internal.impl.xpath.regex.Op {
        int refNumber;
        com.sun.org.apache.xerces.internal.impl.xpath.regex.Op condition;
        com.sun.org.apache.xerces.internal.impl.xpath.regex.Op yes;
        com.sun.org.apache.xerces.internal.impl.xpath.regex.Op no;
        ConditionOp(int type, int refno, com.sun.org.apache.xerces.internal.impl.xpath.regex.Op conditionflow, com.sun.org.apache.xerces.internal.impl.xpath.regex.Op yesflow, com.sun.org.apache.xerces.internal.impl.xpath.regex.Op noflow) {
            super(type);
            this.refNumber = refno;
            this.condition = conditionflow;
            this.yes = yesflow;
            this.no = noflow;
        }
    }
}
