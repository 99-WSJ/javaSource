/*
 * Copyright (c) 2007-2012, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * Copyright 2003-2004 The Apache Software Foundation.
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
 * $Id: Utils.java,v 1.1.4.1 2005/09/08 11:03:21 suresh_emailid Exp $
 */
package java8.sun.org.apache.xml.internal.serializer.utils;

/**
 * This class contains utilities used by the serializer.
 *
 * This class is not a public API, it is only public because it is
 * used by com.sun.org.apache.xml.internal.serializer.
 *
 * @xsl.usage internal
 */
public final class Utils
{
    /**
     * A singleton Messages object is used to load the
     * given resource bundle just once, it is
     * used by multiple transformations as long as the JVM stays up.
     */
    public static final com.sun.org.apache.xml.internal.serializer.utils.Messages messages=
        new com.sun.org.apache.xml.internal.serializer.utils.Messages(
            "com.sun.org.apache.xml.internal.serializer.utils.SerializerMessages");
}
