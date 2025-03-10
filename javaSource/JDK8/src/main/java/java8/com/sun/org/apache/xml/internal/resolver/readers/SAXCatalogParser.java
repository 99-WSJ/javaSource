/*
 * Copyright (c) 2007-2012, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
// SAXCatalogParser.java - An interface for reading catalog files

/*
 * Copyright 2001-2004 The Apache Software Foundation or its licensors,
 * as applicable.
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

package java8.com.sun.org.apache.xml.internal.resolver.readers;

import com.sun.org.apache.xml.internal.resolver.Catalog;
import com.sun.org.apache.xml.internal.resolver.readers.SAXCatalogReader;
import org.xml.sax.ContentHandler;
import org.xml.sax.DocumentHandler;

/**
 * The SAXCatalogParser interface.
 *
 * <p>This interface must be implemented in order for a class to
 * participate as a parser for the SAXCatalogReader.
 *
 * @see Catalog
 * @see SAXCatalogReader
 *
 * @author Norman Walsh
 * <a href="mailto:Norman.Walsh@Sun.COM">Norman.Walsh@Sun.COM</a>
 *
 */
public interface SAXCatalogParser extends ContentHandler, DocumentHandler {
    /** Set the Catalog for which parsing is being performed. */
    public void setCatalog(Catalog catalog);
}
