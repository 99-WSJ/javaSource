/*
 * Copyright (c) 2007-2013, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package java8.sun.org.apache.xml.internal.security.transforms.params;


import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.transforms.TransformParam;
import com.sun.org.apache.xml.internal.security.utils.ElementProxy;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Implements the parameters for a custom Transform which has a better performance
 * than the xfilter2.
 *
 * @author $Author: coheigea $
 */
public class XPathFilterCHGPContainer extends ElementProxy implements TransformParam {

    public static final String TRANSFORM_XPATHFILTERCHGP =
        "http://www.nue.et-inf.uni-siegen.de/~geuer-pollmann/#xpathFilter";

    /** Field _ATT_FILTER_VALUE_INTERSECT */
    private static final String _TAG_INCLUDE_BUT_SEARCH = "IncludeButSearch";

    /** Field _ATT_FILTER_VALUE_SUBTRACT */
    private static final String _TAG_EXCLUDE_BUT_SEARCH = "ExcludeButSearch";

    /** Field _ATT_FILTER_VALUE_UNION */
    private static final String _TAG_EXCLUDE = "Exclude";

    /** Field _TAG_XPATHCHGP */
    public static final String _TAG_XPATHCHGP = "XPathAlternative";

    /** Field _ATT_INCLUDESLASH */
    public static final String _ATT_INCLUDESLASH = "IncludeSlashPolicy";

    /** Field IncludeSlash           */
    public static final boolean IncludeSlash = true;

    /** Field ExcludeSlash           */
    public static final boolean ExcludeSlash = false;

    /**
     * Constructor XPathFilterCHGPContainer
     *
     */
    private XPathFilterCHGPContainer() {
        // no instantiation
    }

    /**
     * Constructor XPathFilterCHGPContainer
     *
     * @param doc
     * @param includeSlashPolicy
     * @param includeButSearch
     * @param excludeButSearch
     * @param exclude
     */
    private XPathFilterCHGPContainer(
        Document doc, boolean includeSlashPolicy, String includeButSearch,
        String excludeButSearch, String exclude
    ) {
        super(doc);

        if (includeSlashPolicy) {
            this.constructionElement.setAttributeNS(
                null, com.sun.org.apache.xml.internal.security.transforms.params.XPathFilterCHGPContainer._ATT_INCLUDESLASH, "true"
            );
        } else {
            this.constructionElement.setAttributeNS(
                null, com.sun.org.apache.xml.internal.security.transforms.params.XPathFilterCHGPContainer._ATT_INCLUDESLASH, "false"
            );
        }

        if ((includeButSearch != null) && (includeButSearch.trim().length() > 0)) {
            Element includeButSearchElem =
                ElementProxy.createElementForFamily(
                    doc, this.getBaseNamespace(), com.sun.org.apache.xml.internal.security.transforms.params.XPathFilterCHGPContainer._TAG_INCLUDE_BUT_SEARCH
                );

            includeButSearchElem.appendChild(
                this.doc.createTextNode(indentXPathText(includeButSearch))
            );
            XMLUtils.addReturnToElement(this.constructionElement);
            this.constructionElement.appendChild(includeButSearchElem);
        }

        if ((excludeButSearch != null) && (excludeButSearch.trim().length() > 0)) {
            Element excludeButSearchElem =
                ElementProxy.createElementForFamily(
                    doc, this.getBaseNamespace(), com.sun.org.apache.xml.internal.security.transforms.params.XPathFilterCHGPContainer._TAG_EXCLUDE_BUT_SEARCH
                );

            excludeButSearchElem.appendChild(
                this.doc.createTextNode(indentXPathText(excludeButSearch)));

            XMLUtils.addReturnToElement(this.constructionElement);
            this.constructionElement.appendChild(excludeButSearchElem);
        }

        if ((exclude != null) && (exclude.trim().length() > 0)) {
            Element excludeElem =
                ElementProxy.createElementForFamily(
                   doc, this.getBaseNamespace(), com.sun.org.apache.xml.internal.security.transforms.params.XPathFilterCHGPContainer._TAG_EXCLUDE);

            excludeElem.appendChild(this.doc.createTextNode(indentXPathText(exclude)));
            XMLUtils.addReturnToElement(this.constructionElement);
            this.constructionElement.appendChild(excludeElem);
        }

        XMLUtils.addReturnToElement(this.constructionElement);
    }

    /**
     * Method indentXPathText
     *
     * @param xp
     * @return the string with enters
     */
    static String indentXPathText(String xp) {
        if ((xp.length() > 2) && (!Character.isWhitespace(xp.charAt(0)))) {
            return "\n" + xp + "\n";
        }
        return xp;
    }

    /**
     * Constructor XPathFilterCHGPContainer
     *
     * @param element
     * @param BaseURI
     * @throws XMLSecurityException
     */
    private XPathFilterCHGPContainer(Element element, String BaseURI)
        throws XMLSecurityException {
        super(element, BaseURI);
    }

    /**
     * Creates a new XPathFilterCHGPContainer; needed for generation.
     *
     * @param doc
     * @param includeSlashPolicy
     * @param includeButSearch
     * @param excludeButSearch
     * @param exclude
     * @return the created object
     */
    public static com.sun.org.apache.xml.internal.security.transforms.params.XPathFilterCHGPContainer getInstance(
        Document doc, boolean includeSlashPolicy, String includeButSearch,
        String excludeButSearch, String exclude
    ) {
        return new com.sun.org.apache.xml.internal.security.transforms.params.XPathFilterCHGPContainer(
            doc, includeSlashPolicy, includeButSearch, excludeButSearch, exclude);
    }

    /**
     * Creates a XPathFilterCHGPContainer from an existing Element; needed for verification.
     *
     * @param element
     * @param BaseURI
     *
     * @throws XMLSecurityException
     * @return the created object.
     */
    public static com.sun.org.apache.xml.internal.security.transforms.params.XPathFilterCHGPContainer getInstance(
        Element element, String BaseURI
    ) throws XMLSecurityException {
        return new com.sun.org.apache.xml.internal.security.transforms.params.XPathFilterCHGPContainer(element, BaseURI);
    }

    /**
     * Method getXStr
     *
     * @param type
     * @return The Xstr
     */
    private String getXStr(String type) {
        if (this.length(this.getBaseNamespace(), type) != 1) {
            return "";
        }

        Element xElem =
            XMLUtils.selectNode(
                this.constructionElement.getFirstChild(), this.getBaseNamespace(), type, 0
            );

        return XMLUtils.getFullTextChildrenFromElement(xElem);
    }

    /**
     * Method getIncludeButSearch
     *
     * @return the string
     */
    public String getIncludeButSearch() {
        return this.getXStr(com.sun.org.apache.xml.internal.security.transforms.params.XPathFilterCHGPContainer._TAG_INCLUDE_BUT_SEARCH);
    }

    /**
     * Method getExcludeButSearch
     *
     * @return the string
     */
    public String getExcludeButSearch() {
        return this.getXStr(com.sun.org.apache.xml.internal.security.transforms.params.XPathFilterCHGPContainer._TAG_EXCLUDE_BUT_SEARCH);
    }

    /**
     * Method getExclude
     *
     * @return the string
     */
    public String getExclude() {
        return this.getXStr(com.sun.org.apache.xml.internal.security.transforms.params.XPathFilterCHGPContainer._TAG_EXCLUDE);
    }

    /**
     * Method getIncludeSlashPolicy
     *
     * @return the string
     */
    public boolean getIncludeSlashPolicy() {
        return this.constructionElement.getAttributeNS(
            null, com.sun.org.apache.xml.internal.security.transforms.params.XPathFilterCHGPContainer._ATT_INCLUDESLASH).equals("true");
    }

    /**
     * Returns the first Text node which contains information from the XPath
     * Filter String. We must use this stupid hook to enable the here() function
     * to work.
     *
     * $todo$ I dunno whether this crashes: <XPath> he<!-- comment -->re()/ds:Signature[1]</XPath>
     * @param type
     * @return the first Text node which contains information from the XPath 2 Filter String
     */
    private Node getHereContextNode(String type) {

        if (this.length(this.getBaseNamespace(), type) != 1) {
            return null;
        }

        return XMLUtils.selectNodeText(
            this.constructionElement.getFirstChild(), this.getBaseNamespace(), type, 0
        );
    }

    /**
     * Method getHereContextNodeIncludeButSearch
     *
     * @return the string
     */
    public Node getHereContextNodeIncludeButSearch() {
        return this.getHereContextNode(com.sun.org.apache.xml.internal.security.transforms.params.XPathFilterCHGPContainer._TAG_INCLUDE_BUT_SEARCH);
    }

    /**
     * Method getHereContextNodeExcludeButSearch
     *
     * @return the string
     */
    public Node getHereContextNodeExcludeButSearch() {
        return this.getHereContextNode(com.sun.org.apache.xml.internal.security.transforms.params.XPathFilterCHGPContainer._TAG_EXCLUDE_BUT_SEARCH);
    }

    /**
     * Method getHereContextNodeExclude
     *
     * @return the string
     */
    public Node getHereContextNodeExclude() {
        return this.getHereContextNode(com.sun.org.apache.xml.internal.security.transforms.params.XPathFilterCHGPContainer._TAG_EXCLUDE);
    }

    /**
     * Method getBaseLocalName
     *
     * @inheritDoc
     */
    public final String getBaseLocalName() {
        return com.sun.org.apache.xml.internal.security.transforms.params.XPathFilterCHGPContainer._TAG_XPATHCHGP;
    }

    /**
     * Method getBaseNamespace
     *
     * @inheritDoc
     */
    public final String getBaseNamespace() {
        return TRANSFORM_XPATHFILTERCHGP;
    }
}
