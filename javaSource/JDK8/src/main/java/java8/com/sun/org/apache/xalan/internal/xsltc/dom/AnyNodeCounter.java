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
 * $Id: AnyNodeCounter.java,v 1.2.4.1 2005/09/06 05:54:53 pvedula Exp $
 */

package java8.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.Translet;
import com.sun.org.apache.xalan.internal.xsltc.dom.NodeCounter;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;

/**
 * @author Jacek Ambroziak
 * @author Santiago Pericas-Geertsen
 */
public abstract class AnyNodeCounter extends NodeCounter {
    public AnyNodeCounter(Translet translet,
                          DOM document, DTMAxisIterator iterator) {
        super(translet, document, iterator);
    }

    public AnyNodeCounter(Translet translet,
                          DOM document,
                          DTMAxisIterator iterator,
                          boolean hasFrom) {
        super(translet, document, iterator, hasFrom);
    }

    public NodeCounter setStartNode(int node) {
        _node = node;
        _nodeType = _document.getExpandedTypeID(node);
        return this;
    }

    public String getCounter() {
        int result;
        if (_value != Integer.MIN_VALUE) {
            //See Errata E24
            if (_value == 0) return "0";
            else if (Double.isNaN(_value)) return "NaN";
            else if (_value < 0 && Double.isInfinite(_value)) return "-Infinity";
            else if (Double.isInfinite(_value)) return "Infinity";
            else return formatNumbers((int)_value);
        }
        else {
            int next = _node;
            final int root = _document.getDocument();
            result = 0;
            while (next >= root && !matchesFrom(next)) {
                if (matchesCount(next)) {
                    ++result;
                }
                next--;
//%HZ%:  Is this the best way of finding the root?  Is it better to check
//%HZ%:  parent(next)?
                /*
                if (next == root) {
                    break;
                }
                else {
                    --next;
                }
                */
            }
        }
        return formatNumbers(result);
    }

    public static NodeCounter getDefaultNodeCounter(Translet translet,
                                                    DOM document,
                                                    DTMAxisIterator iterator) {
        return new DefaultAnyNodeCounter(translet, document, iterator);
    }

    static class DefaultAnyNodeCounter extends com.sun.org.apache.xalan.internal.xsltc.dom.AnyNodeCounter {
        public DefaultAnyNodeCounter(Translet translet,
                                     DOM document, DTMAxisIterator iterator) {
            super(translet, document, iterator);
        }

        public String getCounter() {
            int result;
            if (_value != Integer.MIN_VALUE) {
                    //See Errata E24
                    if (_value == 0) return "0";
                    else if (Double.isNaN(_value)) return "NaN";
                    else if (_value < 0 && Double.isInfinite(_value)) return "-Infinity";
                    else if (Double.isInfinite(_value)) return "Infinity";
                    else result = (int) _value;
            }
            else {
                int next = _node;
                result = 0;
                final int ntype = _document.getExpandedTypeID(_node);
                final int root = _document.getDocument();
                while (next >= 0) {
                    if (ntype == _document.getExpandedTypeID(next)) {
                        result++;
                    }
//%HZ%:  Is this the best way of finding the root?  Is it better to check
//%HZ%:  parent(next)?
                    if (next == root) {
                        break;
                    }
                    else {
                        --next;
                    }
                }
            }
            return formatNumbers(result);
        }
    }
}
