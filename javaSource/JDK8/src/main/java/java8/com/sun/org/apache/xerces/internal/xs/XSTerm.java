/*
 * Copyright (c) 2007-2012, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * Copyright 2003,2004 The Apache Software Foundation.
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

package java8.sun.org.apache.xerces.internal.xs;

import com.sun.org.apache.xerces.internal.xs.XSObject;

/**
 * Describes a term that can be one of a model group, a wildcard, or an
 * element declaration. Objects implementing
 * <code>XSElementDeclaration</code>, <code>XSModelGroup</code> and
 * <code>XSWildcard</code> interfaces also implement this interface.
 */
public interface XSTerm extends XSObject {
}
