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
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * This Object serves as Content for the ds:Transforms for exclusive
 * Canonicalization.
 * <BR />
 * It implements the {@link Element} interface
 * and can be used directly in a DOM tree.
 *
 * @author Christian Geuer-Pollmann
 */
public class InclusiveNamespaces extends ElementProxy implements TransformParam {

    /** Field _TAG_EC_INCLUSIVENAMESPACES */
    public static final String _TAG_EC_INCLUSIVENAMESPACES =
        "InclusiveNamespaces";

    /** Field _ATT_EC_PREFIXLIST */
    public static final String _ATT_EC_PREFIXLIST = "PrefixList";

    /** Field ExclusiveCanonicalizationNamespace */
    public static final String ExclusiveCanonicalizationNamespace =
        "http://www.w3.org/2001/10/xml-exc-c14n#";

    /**
     * Constructor XPathContainer
     *
     * @param doc
     * @param prefixList
     */
    public InclusiveNamespaces(Document doc, String prefixList) {
        this(doc, com.sun.org.apache.xml.internal.security.transforms.params.InclusiveNamespaces.prefixStr2Set(prefixList));
    }

    /**
     * Constructor InclusiveNamespaces
     *
     * @param doc
     * @param prefixes
     */
    public InclusiveNamespaces(Document doc, Set<String> prefixes) {
        super(doc);

        SortedSet<String> prefixList = null;
        if (prefixes instanceof SortedSet<?>) {
            prefixList = (SortedSet<String>)prefixes;
        } else {
            prefixList = new TreeSet<String>(prefixes);
        }

        StringBuilder sb = new StringBuilder();
        for (String prefix : prefixList) {
            if (prefix.equals("xmlns")) {
                sb.append("#default ");
            } else {
                sb.append(prefix + " ");
            }
        }

        this.constructionElement.setAttributeNS(
            null, com.sun.org.apache.xml.internal.security.transforms.params.InclusiveNamespaces._ATT_EC_PREFIXLIST, sb.toString().trim());
    }

    /**
     * Constructor InclusiveNamespaces
     *
     * @param element
     * @param BaseURI
     * @throws XMLSecurityException
     */
    public InclusiveNamespaces(Element element, String BaseURI)
        throws XMLSecurityException {
        super(element, BaseURI);
    }

    /**
     * Method getInclusiveNamespaces
     *
     * @return The Inclusive Namespace string
     */
    public String getInclusiveNamespaces() {
        return this.constructionElement.getAttributeNS(null, com.sun.org.apache.xml.internal.security.transforms.params.InclusiveNamespaces._ATT_EC_PREFIXLIST);
    }

    /**
     * Decodes the <code>inclusiveNamespaces</code> String and returns all
     * selected namespace prefixes as a Set. The <code>#default</code>
     * namespace token is represented as an empty namespace prefix
     * (<code>"xmlns"</code>).
     * <BR/>
     * The String <code>inclusiveNamespaces=" xenc    ds #default"</code>
     * is returned as a Set containing the following Strings:
     * <UL>
     * <LI><code>xmlns</code></LI>
     * <LI><code>xenc</code></LI>
     * <LI><code>ds</code></LI>
     * </UL>
     *
     * @param inclusiveNamespaces
     * @return A set to string
     */
    public static SortedSet<String> prefixStr2Set(String inclusiveNamespaces) {
        SortedSet<String> prefixes = new TreeSet<String>();

        if ((inclusiveNamespaces == null) || (inclusiveNamespaces.length() == 0)) {
            return prefixes;
        }

        String[] tokens = inclusiveNamespaces.split("\\s");
        for (String prefix : tokens) {
            if (prefix.equals("#default")) {
                prefixes.add("xmlns");
            } else {
                prefixes.add(prefix);
            }
        }

        return prefixes;
    }

    /**
     * Method getBaseNamespace
     *
     * @inheritDoc
     */
    public String getBaseNamespace() {
        return com.sun.org.apache.xml.internal.security.transforms.params.InclusiveNamespaces.ExclusiveCanonicalizationNamespace;
    }

    /**
     * Method getBaseLocalName
     *
     * @inheritDoc
     */
    public String getBaseLocalName() {
        return com.sun.org.apache.xml.internal.security.transforms.params.InclusiveNamespaces._TAG_EC_INCLUSIVENAMESPACES;
    }
}
