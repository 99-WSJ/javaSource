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
 * $Id: FoundIndex.java,v 1.2.4.1 2005/09/14 19:50:20 jeffsuttor Exp $
 */
package java8.com.sun.org.apache.xpath.internal;

/**
 * Class to let us know when it's time to do
 * a search from the parent because of indexing.
 * @xsl.usage internal
 */
public class FoundIndex extends RuntimeException
{
    static final long serialVersionUID = -4643975335243078270L;

  /**
   * Constructor FoundIndex
   *
   */
  public FoundIndex(){}
}
