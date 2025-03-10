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
 * $Id: StringBufferPool.java,v 1.2.4.1 2005/09/15 08:15:54 suresh_emailid Exp $
 */
package java8.sun.org.apache.xml.internal.utils;

import com.sun.org.apache.xml.internal.utils.FastStringBuffer;
import com.sun.org.apache.xml.internal.utils.ObjectPool;

/**
 * This class pools string buffers, since they are reused so often.
 * String buffers are good candidates for pooling, because of
 * their supporting character arrays.
 * @xsl.usage internal
 */
public class StringBufferPool
{

  /** The global pool of string buffers.   */
  private static ObjectPool m_stringBufPool =
    new ObjectPool(FastStringBuffer.class);

  /**
   * Get the first free instance of a string buffer, or create one
   * if there are no free instances.
   *
   * @return A string buffer ready for use.
   */
  public synchronized static FastStringBuffer get()
  {
    return (FastStringBuffer) m_stringBufPool.getInstance();
  }

  /**
   * Return a string buffer back to the pool.
   *
   * @param sb Must be a non-null reference to a string buffer.
   */
  public synchronized static void free(FastStringBuffer sb)
  {
    // Since this isn't synchronized, setLength must be
    // done before the instance is freed.
    // Fix attributed to Peter Speck <speck@ruc.dk>.
    sb.setLength(0);
    m_stringBufPool.freeInstance(sb);
  }
}
