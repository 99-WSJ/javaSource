/*
 * Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package java8.com.sun.org.apache.xerces.internal.util;


import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;

import javax.xml.XMLConstants;
import java.util.*;

/**
 * <p>A read-only XNI wrapper around a JAXP NamespaceContext.</p>
 *
 * @author Michael Glavassevich, IBM
 *
 * @version $Id: JAXPNamespaceContextWrapper.java,v 1.2 2010-10-26 23:01:13 joehw Exp $
 */
public final class JAXPNamespaceContextWrapper implements NamespaceContext {

    private javax.xml.namespace.NamespaceContext fNamespaceContext;
    private SymbolTable fSymbolTable;
    private List fPrefixes;
    private final Vector fAllPrefixes = new Vector();

    private int[] fContext = new int[8];
    private int fCurrentContext;

    public JAXPNamespaceContextWrapper(SymbolTable symbolTable) {
        setSymbolTable(symbolTable);
    }

    public void setNamespaceContext(javax.xml.namespace.NamespaceContext context) {
        fNamespaceContext = context;
    }

    public javax.xml.namespace.NamespaceContext getNamespaceContext() {
        return fNamespaceContext;
    }

    public void setSymbolTable(SymbolTable symbolTable) {
        fSymbolTable = symbolTable;
    }

    public SymbolTable getSymbolTable() {
        return fSymbolTable;
    }

    public void setDeclaredPrefixes(List prefixes) {
        fPrefixes = prefixes;
    }

    public List getDeclaredPrefixes() {
        return fPrefixes;
    }

    /*
     * NamespaceContext methods
     */

    public String getURI(String prefix) {
        if (fNamespaceContext != null) {
            String uri = fNamespaceContext.getNamespaceURI(prefix);
            if (uri != null && !XMLConstants.NULL_NS_URI.equals(uri)) {
                return (fSymbolTable != null) ? fSymbolTable.addSymbol(uri) : uri.intern();
            }
        }
        return null;
    }

    public String getPrefix(String uri) {
        if (fNamespaceContext != null) {
            if (uri == null) {
                uri = XMLConstants.NULL_NS_URI;
            }
            String prefix = fNamespaceContext.getPrefix(uri);
            if (prefix == null) {
                prefix = XMLConstants.DEFAULT_NS_PREFIX;
            }
            return (fSymbolTable != null) ? fSymbolTable.addSymbol(prefix) : prefix.intern();
        }
        return null;
    }

    public Enumeration getAllPrefixes() {
        // There may be duplicate prefixes in the list so we
        // first transfer them to a set to ensure uniqueness.
        return Collections.enumeration(new TreeSet(fAllPrefixes));
    }

    public void pushContext() {
        // extend the array, if necessary
        if (fCurrentContext + 1 == fContext.length) {
            int[] contextarray = new int[fContext.length * 2];
            System.arraycopy(fContext, 0, contextarray, 0, fContext.length);
            fContext = contextarray;
        }
        // push context
        fContext[++fCurrentContext] = fAllPrefixes.size();
        if (fPrefixes != null) {
            fAllPrefixes.addAll(fPrefixes);
        }
    }

    public void popContext() {
        fAllPrefixes.setSize(fContext[fCurrentContext--]);
    }

    public boolean declarePrefix(String prefix, String uri) {
        return true;
    }

    public int getDeclaredPrefixCount() {
        return (fPrefixes != null) ? fPrefixes.size() : 0;
    }

    public String getDeclaredPrefixAt(int index) {
        return (String) fPrefixes.get(index);
    }

    public void reset() {
        fCurrentContext = 0;
        fContext[fCurrentContext] = 0;
        fAllPrefixes.clear();
    }

} // JAXPNamespaceContextWrapper
