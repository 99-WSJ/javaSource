/*
 * Copyright (c) 2007-2012, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * Copyright 1999-2004 The Apache Software Foundation.
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
 * $Id: XPathFactory.java,v 1.1.2.1 2005/08/01 01:30:14 jeffsuttor Exp $
 */
package java8.com.sun.org.apache.xpath.internal;

import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import com.sun.org.apache.xpath.internal.XPath;

import javax.xml.transform.SourceLocator;

/**
 * Factory class for creating an XPath.  Implementors of XPath derivatives
 * will need to make a derived class of this.
 * @xsl.usage advanced
 */
public interface XPathFactory
{

  /**
   * Create an XPath.
   *
   * @param exprString The XPath expression string.
   * @param locator The location of the expression string, mainly for diagnostic
   *                purposes.
   * @param prefixResolver This will be called in order to resolve prefixes
   *        to namespace URIs.
   * @param type One of {@link XPath#SELECT} or
   *             {@link XPath#MATCH}.
   *
   * @return an XPath ready for execution.
   */
  XPath create(String exprString, SourceLocator locator,
               PrefixResolver prefixResolver, int type);
}
