/*
 * Copyright (c) 2005, 2011, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
/*
 * $Id: XPathType.java,v 1.4 2005/05/10 16:40:17 mullan Exp $
 */
package java8.javax.xml.crypto.dsig.spec;

import javax.xml.crypto.dsig.spec.XPathFilter2ParameterSpec;
import java.util.Collections;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

/**
 * The XML Schema Definition of the <code>XPath</code> element as defined in the
 * <a href="http://www.w3.org/TR/xmldsig-filter2">
 * W3C Recommendation for XML-Signature XPath Filter 2.0</a>:
 * <pre><code>
 * &lt;schema xmlns="http://www.w3.org/2001/XMLSchema"
 *         xmlns:xf="http://www.w3.org/2002/06/xmldsig-filter2"
 *         targetNamespace="http://www.w3.org/2002/06/xmldsig-filter2"
 *         version="0.1" elementFormDefault="qualified"&gt;
 *
 * &lt;element name="XPath"
 *          type="xf:XPathType"/&gt;
 *
 * &lt;complexType name="XPathType"&gt;
 *   &lt;simpleContent&gt;
 *     &lt;extension base="string"&gt;
 *       &lt;attribute name="Filter"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="string"&gt;
 *             &lt;enumeration value="intersect"/&gt;
 *             &lt;enumeration value="subtract"/&gt;
 *             &lt;enumeration value="union"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *     &lt;/extension&gt;
 *   &lt;/simpleContent&gt;
 * &lt;/complexType&gt;
 * </code></pre>
 *
 * @author Sean Mullan
 * @author JSR 105 Expert Group
 * @since 1.6
 * @see XPathFilter2ParameterSpec
 */
public class XPathType {

    /**
     * Represents the filter set operation.
     */
    public static class Filter {
        private final String operation;

        private Filter(String operation) {
            this.operation = operation;
        }

        /**
         * Returns the string form of the operation.
         *
         * @return the string form of the operation
         */
        public String toString() {
            return operation;
        }

        /**
         * The intersect filter operation.
         */
        public static final Filter INTERSECT = new Filter("intersect");

        /**
         * The subtract filter operation.
         */
        public static final Filter SUBTRACT = new Filter("subtract");

        /**
         * The union filter operation.
         */
        public static final Filter UNION = new Filter("union");
    }

    private final String expression;
    private final Filter filter;
    private Map<String,String> nsMap;

    /**
     * Creates an <code>XPathType</code> instance with the specified XPath
     * expression and filter.
     *
     * @param expression the XPath expression to be evaluated
     * @param filter the filter operation ({@link Filter#INTERSECT},
     *    {@link Filter#SUBTRACT}, or {@link Filter#UNION})
     * @throws NullPointerException if <code>expression</code> or
     *    <code>filter</code> is <code>null</code>
     */
    public XPathType(String expression, Filter filter) {
        if (expression == null) {
            throw new NullPointerException("expression cannot be null");
        }
        if (filter == null) {
            throw new NullPointerException("filter cannot be null");
        }
        this.expression = expression;
        this.filter = filter;
        this.nsMap = Collections.emptyMap();
    }

    /**
     * Creates an <code>XPathType</code> instance with the specified XPath
     * expression, filter, and namespace map. The map is copied to protect
     * against subsequent modification.
     *
     * @param expression the XPath expression to be evaluated
     * @param filter the filter operation ({@link Filter#INTERSECT},
     *    {@link Filter#SUBTRACT}, or {@link Filter#UNION})
     * @param namespaceMap the map of namespace prefixes. Each key is a
     *    namespace prefix <code>String</code> that maps to a corresponding
     *    namespace URI <code>String</code>.
     * @throws NullPointerException if <code>expression</code>,
     *    <code>filter</code> or <code>namespaceMap</code> are
     *    <code>null</code>
     * @throws ClassCastException if any of the map's keys or entries are
     *    not of type <code>String</code>
     */
    @SuppressWarnings("rawtypes")
    public XPathType(String expression, Filter filter, Map namespaceMap) {
        this(expression, filter);
        if (namespaceMap == null) {
            throw new NullPointerException("namespaceMap cannot be null");
        }
        Map<?,?> copy = new HashMap<>((Map<?,?>)namespaceMap);
        Iterator<? extends Map.Entry<?,?>> entries = copy.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<?,?> me = entries.next();
            if (!(me.getKey() instanceof String) ||
                !(me.getValue() instanceof String)) {
                throw new ClassCastException("not a String");
            }
        }

        @SuppressWarnings("unchecked")
        Map<String,String> temp = (Map<String,String>)copy;

        nsMap = Collections.unmodifiableMap(temp);
    }

    /**
     * Returns the XPath expression to be evaluated.
     *
     * @return the XPath expression to be evaluated
     */
    public String getExpression() {
        return expression;
    }

    /**
     * Returns the filter operation.
     *
     * @return the filter operation
     */
    public Filter getFilter() {
        return filter;
    }

    /**
     * Returns a map of namespace prefixes. Each key is a namespace prefix
     * <code>String</code> that maps to a corresponding namespace URI
     * <code>String</code>.
     * <p>
     * This implementation returns an {@link Collections#unmodifiableMap
     * unmodifiable map}.
     *
     * @return a <code>Map</code> of namespace prefixes to namespace URIs
     *    (may be empty, but never <code>null</code>)
     */
    @SuppressWarnings("rawtypes")
    public Map getNamespaceMap() {
        return nsMap;
    }
}
