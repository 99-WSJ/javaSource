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
 * $Id: ExpressionOwner.java,v 1.1.2.1 2005/08/01 01:30:12 jeffsuttor Exp $
 */
package java8.sun.org.apache.xpath.internal;

import com.sun.org.apache.xpath.internal.Expression;

/**
 * Classes that implement this interface own an expression, which
 * can be rewritten.
 */
public interface ExpressionOwner
{
  /**
   * Get the raw Expression object that this class wraps.
   *
   * @return the raw Expression object, which should not normally be null.
   */
  public Expression getExpression();

  /**
   * Set the raw expression object for this object.
   *
   * @param exp the raw Expression object, which should not normally be null.
   */
  public void setExpression(Expression exp);


}
