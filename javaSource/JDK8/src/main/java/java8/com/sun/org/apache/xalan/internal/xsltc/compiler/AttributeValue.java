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
 * $Id: AttributeValue.java,v 1.2.4.1 2005/09/01 10:25:49 pvedula Exp $
 */

package java8.com.sun.org.apache.xalan.internal.xsltc.compiler;


import com.sun.org.apache.xalan.internal.xsltc.compiler.SyntaxTreeNode;
import java8.sun.org.apache.xalan.internal.xsltc.compiler.Expression;
import java8.sun.org.apache.xalan.internal.xsltc.compiler.Parser;
import java8.sun.org.apache.xalan.internal.xsltc.compiler.SimpleAttributeValue;

/**
 * @author Jacek Ambroziak
 * @author Santiago Pericas-Geertsen
 */
abstract class AttributeValue extends Expression {

    public static final com.sun.org.apache.xalan.internal.xsltc.compiler.AttributeValue create(SyntaxTreeNode parent,
                                                                                               String text, Parser parser) {

        com.sun.org.apache.xalan.internal.xsltc.compiler.AttributeValue result;
        if (text.indexOf('{') != -1) {
            result = new AttributeValueTemplate(text, parser, parent);
        }
        else if (text.indexOf('}') != -1) {
            result = new AttributeValueTemplate(text, parser, parent);
        }
        else {
            result = new SimpleAttributeValue(text);
            result.setParser(parser);
            result.setParent(parent);
        }
        return result;
    }
}
