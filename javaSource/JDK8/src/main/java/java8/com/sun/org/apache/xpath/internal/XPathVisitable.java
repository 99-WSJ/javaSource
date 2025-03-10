/*
 * Copyright (c) 2007-2012, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * Copyright 2002-2004 The Apache Software Foundation.
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
 * $Id: XPathVisitable.java,v 1.1.2.1 2005/08/01 01:30:13 jeffsuttor Exp $
 */
package java8.com.sun.org.apache.xpath.internal;

import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathVisitor;

/**
 * A class that implements this interface will call a XPathVisitor
 * for itself and members within it's heararchy.  If the XPathVisitor's
 * method returns false, the sub-member heararchy will not be
 * traversed.
 */
public interface XPathVisitable
{
        /**
         * This will traverse the heararchy, calling the visitor for
         * each member.  If the called visitor method returns
         * false, the subtree should not be called.
         *
         * @param owner The owner of the visitor, where that path may be
         *              rewritten if needed.
         * @param visitor The visitor whose appropriate method will be called.
         */
        public void callVisitors(ExpressionOwner owner, XPathVisitor visitor);
}
