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
 * $Id: SymbolTable.java,v 1.5 2005/09/28 13:48:16 pvedula Exp $
 */

package java8.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.xalan.internal.xsltc.compiler.Constants;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Stylesheet;
import com.sun.org.apache.xalan.internal.xsltc.compiler.SyntaxTreeNode;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Template;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodType;

import java.util.Hashtable;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * @author Jacek Ambroziak
 * @author Santiago Pericas-Geertsen
 * @author Morten Jorgensen
 */
final class SymbolTable {

    // These hashtables are used for all stylesheets
    private final Hashtable _stylesheets = new Hashtable();
    private final Hashtable _primops     = new Hashtable();

    // These hashtables are used for some stylesheets
    private Hashtable _variables = null;
    private Hashtable _templates = null;
    private Hashtable _attributeSets = null;
    private Hashtable _aliases = null;
    private Hashtable _excludedURI = null;
    private Stack     _excludedURIStack = null;
    private Hashtable _decimalFormats = null;
    private Hashtable _keys = null;

    public com.sun.org.apache.xalan.internal.xsltc.compiler.DecimalFormatting getDecimalFormatting(com.sun.org.apache.xalan.internal.xsltc.compiler.QName name) {
        if (_decimalFormats == null) return null;
        return((com.sun.org.apache.xalan.internal.xsltc.compiler.DecimalFormatting)_decimalFormats.get(name));
    }

    public void addDecimalFormatting(com.sun.org.apache.xalan.internal.xsltc.compiler.QName name, com.sun.org.apache.xalan.internal.xsltc.compiler.DecimalFormatting symbols) {
        if (_decimalFormats == null) _decimalFormats = new Hashtable();
        _decimalFormats.put(name, symbols);
    }

    public com.sun.org.apache.xalan.internal.xsltc.compiler.Key getKey(com.sun.org.apache.xalan.internal.xsltc.compiler.QName name) {
        if (_keys == null) return null;
        return (com.sun.org.apache.xalan.internal.xsltc.compiler.Key) _keys.get(name);
    }

    public void addKey(com.sun.org.apache.xalan.internal.xsltc.compiler.QName name, com.sun.org.apache.xalan.internal.xsltc.compiler.Key key) {
        if (_keys == null) _keys = new Hashtable();
        _keys.put(name, key);
    }

    public Stylesheet addStylesheet(com.sun.org.apache.xalan.internal.xsltc.compiler.QName name, Stylesheet node) {
        return (Stylesheet)_stylesheets.put(name, node);
    }

    public Stylesheet lookupStylesheet(com.sun.org.apache.xalan.internal.xsltc.compiler.QName name) {
        return (Stylesheet)_stylesheets.get(name);
    }

    public Template addTemplate(Template template) {
        final com.sun.org.apache.xalan.internal.xsltc.compiler.QName name = template.getName();
        if (_templates == null) _templates = new Hashtable();
        return (Template)_templates.put(name, template);
    }

    public Template lookupTemplate(com.sun.org.apache.xalan.internal.xsltc.compiler.QName name) {
        if (_templates == null) return null;
        return (Template)_templates.get(name);
    }

    public com.sun.org.apache.xalan.internal.xsltc.compiler.Variable addVariable(com.sun.org.apache.xalan.internal.xsltc.compiler.Variable variable) {
        if (_variables == null) _variables = new Hashtable();
        final String name = variable.getName().getStringRep();
        return (com.sun.org.apache.xalan.internal.xsltc.compiler.Variable)_variables.put(name, variable);
    }

    public com.sun.org.apache.xalan.internal.xsltc.compiler.Param addParam(com.sun.org.apache.xalan.internal.xsltc.compiler.Param parameter) {
        if (_variables == null) _variables = new Hashtable();
        final String name = parameter.getName().getStringRep();
        return (com.sun.org.apache.xalan.internal.xsltc.compiler.Param)_variables.put(name, parameter);
    }

    public com.sun.org.apache.xalan.internal.xsltc.compiler.Variable lookupVariable(com.sun.org.apache.xalan.internal.xsltc.compiler.QName qname) {
        if (_variables == null) return null;
        final String name = qname.getStringRep();
        final Object obj = _variables.get(name);
        return obj instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.Variable ? (com.sun.org.apache.xalan.internal.xsltc.compiler.Variable)obj : null;
    }

    public com.sun.org.apache.xalan.internal.xsltc.compiler.Param lookupParam(com.sun.org.apache.xalan.internal.xsltc.compiler.QName qname) {
        if (_variables == null) return null;
        final String name = qname.getStringRep();
        final Object obj = _variables.get(name);
        return obj instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.Param ? (com.sun.org.apache.xalan.internal.xsltc.compiler.Param)obj : null;
    }

    public SyntaxTreeNode lookupName(com.sun.org.apache.xalan.internal.xsltc.compiler.QName qname) {
        if (_variables == null) return null;
        final String name = qname.getStringRep();
        return (SyntaxTreeNode)_variables.get(name);
    }

    public com.sun.org.apache.xalan.internal.xsltc.compiler.AttributeSet addAttributeSet(com.sun.org.apache.xalan.internal.xsltc.compiler.AttributeSet atts) {
        if (_attributeSets == null) _attributeSets = new Hashtable();
        return (com.sun.org.apache.xalan.internal.xsltc.compiler.AttributeSet)_attributeSets.put(atts.getName(), atts);
    }

    public com.sun.org.apache.xalan.internal.xsltc.compiler.AttributeSet lookupAttributeSet(com.sun.org.apache.xalan.internal.xsltc.compiler.QName name) {
        if (_attributeSets == null) return null;
        return (com.sun.org.apache.xalan.internal.xsltc.compiler.AttributeSet)_attributeSets.get(name);
    }

    /**
     * Add a primitive operator or function to the symbol table. To avoid
     * name clashes with user-defined names, the prefix <tt>PrimopPrefix</tt>
     * is prepended.
     */
    public void addPrimop(String name, MethodType mtype) {
        Vector methods = (Vector)_primops.get(name);
        if (methods == null) {
            _primops.put(name, methods = new Vector());
        }
        methods.addElement(mtype);
    }

    /**
     * Lookup a primitive operator or function in the symbol table by
     * prepending the prefix <tt>PrimopPrefix</tt>.
     */
    public Vector lookupPrimop(String name) {
        return (Vector)_primops.get(name);
    }

    /**
     * This is used for xsl:attribute elements that have a "namespace"
     * attribute that is currently not defined using xmlns:
     */
    private int _nsCounter = 0;

    public String generateNamespacePrefix() {
        return("ns"+(_nsCounter++));
    }

    /**
     * Use a namespace prefix to lookup a namespace URI
     */
    private SyntaxTreeNode _current = null;

    public void setCurrentNode(SyntaxTreeNode node) {
        _current = node;
    }

    public String lookupNamespace(String prefix) {
        if (_current == null) return(Constants.EMPTYSTRING);
        return(_current.lookupNamespace(prefix));
    }

    /**
     * Adds an alias for a namespace prefix
     */
    public void addPrefixAlias(String prefix, String alias) {
        if (_aliases == null) _aliases = new Hashtable();
        _aliases.put(prefix,alias);
    }

    /**
     * Retrieves any alias for a given namespace prefix
     */
    public String lookupPrefixAlias(String prefix) {
        if (_aliases == null) return null;
        return (String)_aliases.get(prefix);
    }

    /**
     * Register a namespace URI so that it will not be declared in the output
     * unless it is actually referenced in the output.
     */
    public void excludeURI(String uri) {
        // The null-namespace cannot be excluded
        if (uri == null) return;

        // Create new hashtable of exlcuded URIs if none exists
        if (_excludedURI == null) _excludedURI = new Hashtable();

        // Register the namespace URI
        Integer refcnt = (Integer)_excludedURI.get(uri);
        if (refcnt == null)
            refcnt = new Integer(1);
        else
            refcnt = new Integer(refcnt.intValue() + 1);
        _excludedURI.put(uri,refcnt);
    }

    /**
     * Exclude a series of namespaces given by a list of whitespace
     * separated namespace prefixes.
     */
    public void excludeNamespaces(String prefixes) {
        if (prefixes != null) {
            StringTokenizer tokens = new StringTokenizer(prefixes);
            while (tokens.hasMoreTokens()) {
                final String prefix = tokens.nextToken();
                final String uri;
                if (prefix.equals("#default"))
                    uri = lookupNamespace(Constants.EMPTYSTRING);
                else
                    uri = lookupNamespace(prefix);
                if (uri != null) excludeURI(uri);
            }
        }
    }

    /**
     * Check if a namespace should not be declared in the output (unless used)
     */
    public boolean isExcludedNamespace(String uri) {
        if (uri != null && _excludedURI != null) {
            final Integer refcnt = (Integer)_excludedURI.get(uri);
            return (refcnt != null && refcnt.intValue() > 0);
        }
        return false;
    }

    /**
     * Turn of namespace declaration exclusion
     */
    public void unExcludeNamespaces(String prefixes) {
        if (_excludedURI == null) return;
        if (prefixes != null) {
            StringTokenizer tokens = new StringTokenizer(prefixes);
            while (tokens.hasMoreTokens()) {
                final String prefix = tokens.nextToken();
                final String uri;
                if (prefix.equals("#default"))
                    uri = lookupNamespace(Constants.EMPTYSTRING);
                else
                    uri = lookupNamespace(prefix);
                Integer refcnt = (Integer)_excludedURI.get(uri);
                if (refcnt != null)
                    _excludedURI.put(uri, new Integer(refcnt.intValue() - 1));
            }
        }
    }
    /**
     * Exclusion of namespaces by a stylesheet does not extend to any stylesheet
     * imported or included by the stylesheet.  Upon entering the context of a
     * new stylesheet, a call to this method is needed to clear the current set
     * of excluded namespaces temporarily.  Every call to this method requires
     * a corresponding call to {@link #popExcludedNamespacesContext()}.
     */
    public void pushExcludedNamespacesContext() {
        if (_excludedURIStack == null) {
            _excludedURIStack = new Stack();
        }
        _excludedURIStack.push(_excludedURI);
        _excludedURI = null;
    }

    /**
     * Exclusion of namespaces by a stylesheet does not extend to any stylesheet
     * imported or included by the stylesheet.  Upon exiting the context of a
     * stylesheet, a call to this method is needed to restore the set of
     * excluded namespaces that was in effect prior to entering the context of
     * the current stylesheet.
     */
    public void popExcludedNamespacesContext() {
        _excludedURI = (Hashtable) _excludedURIStack.pop();
        if (_excludedURIStack.isEmpty()) {
            _excludedURIStack = null;
        }
    }

}
