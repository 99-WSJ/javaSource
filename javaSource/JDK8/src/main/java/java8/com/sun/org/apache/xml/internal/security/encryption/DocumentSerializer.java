/*
 * Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
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
package java8.sun.org.apache.xml.internal.security.encryption;

import com.sun.org.apache.xml.internal.security.encryption.AbstractSerializer;
import com.sun.org.apache.xml.internal.security.encryption.XMLEncryptionException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;

/**
 * Converts <code>String</code>s into <code>Node</code>s and visa versa.
 */
public class DocumentSerializer extends AbstractSerializer {

    protected DocumentBuilderFactory dbf;

    /**
     * @param source
     * @param ctx
     * @return the Node resulting from the parse of the source
     * @throws XMLEncryptionException
     */
    public Node deserialize(byte[] source, Node ctx) throws XMLEncryptionException {
        byte[] fragment = createContext(source, ctx);
        return deserialize(ctx, new InputSource(new ByteArrayInputStream(fragment)));
    }

    /**
     * @param source
     * @param ctx
     * @return the Node resulting from the parse of the source
     * @throws XMLEncryptionException
     */
    public Node deserialize(String source, Node ctx) throws XMLEncryptionException {
        String fragment = createContext(source, ctx);
        return deserialize(ctx, new InputSource(new StringReader(fragment)));
    }

    /**
     * @param ctx
     * @param inputSource
     * @return the Node resulting from the parse of the source
     * @throws XMLEncryptionException
     */
    private Node deserialize(Node ctx, InputSource inputSource) throws XMLEncryptionException {
        try {
            if (dbf == null) {
                dbf = DocumentBuilderFactory.newInstance();
                dbf.setNamespaceAware(true);
                dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, Boolean.TRUE);
                dbf.setAttribute("http://xml.org/sax/features/namespaces", Boolean.TRUE);
                dbf.setValidating(false);
            }
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document d = db.parse(inputSource);

            Document contextDocument = null;
            if (Node.DOCUMENT_NODE == ctx.getNodeType()) {
                contextDocument = (Document)ctx;
            } else {
                contextDocument = ctx.getOwnerDocument();
            }

            Element fragElt =
                    (Element) contextDocument.importNode(d.getDocumentElement(), true);
            DocumentFragment result = contextDocument.createDocumentFragment();
            Node child = fragElt.getFirstChild();
            while (child != null) {
                fragElt.removeChild(child);
                result.appendChild(child);
                child = fragElt.getFirstChild();
            }
            return result;
        } catch (SAXException se) {
            throw new XMLEncryptionException("empty", se);
        } catch (ParserConfigurationException pce) {
            throw new XMLEncryptionException("empty", pce);
        } catch (IOException ioe) {
            throw new XMLEncryptionException("empty", ioe);
        }
    }

}
