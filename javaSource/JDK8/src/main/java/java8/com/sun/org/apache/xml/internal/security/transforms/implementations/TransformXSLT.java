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
package java8.com.sun.org.apache.xml.internal.security.transforms.implementations;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.Transform;
import com.sun.org.apache.xml.internal.security.transforms.TransformSpi;
import com.sun.org.apache.xml.internal.security.transforms.TransformationException;
import com.sun.org.apache.xml.internal.security.transforms.Transforms;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import org.w3c.dom.Element;

import javax.xml.XMLConstants;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Class TransformXSLT
 *
 * Implements the <CODE>http://www.w3.org/TR/1999/REC-xslt-19991116</CODE>
 * transform.
 *
 * @author Christian Geuer-Pollmann
 */
public class TransformXSLT extends TransformSpi {

    /** Field implementedTransformURI */
    public static final String implementedTransformURI =
        Transforms.TRANSFORM_XSLT;

    static final String XSLTSpecNS              = "http://www.w3.org/1999/XSL/Transform";
    static final String defaultXSLTSpecNSprefix = "xslt";
    static final String XSLTSTYLESHEET          = "stylesheet";

    private static java.util.logging.Logger log =
        java.util.logging.Logger.getLogger(com.sun.org.apache.xml.internal.security.transforms.implementations.TransformXSLT.class.getName());

    /**
     * Method engineGetURI
     *
     * @inheritDoc
     */
    protected String engineGetURI() {
        return implementedTransformURI;
    }

    protected XMLSignatureInput enginePerformTransform(
        XMLSignatureInput input, OutputStream baos, Transform transformObject
    ) throws IOException, TransformationException {
        try {
            Element transformElement = transformObject.getElement();

            Element xsltElement =
                XMLUtils.selectNode(transformElement.getFirstChild(), XSLTSpecNS, "stylesheet", 0);

            if (xsltElement == null) {
                Object exArgs[] = { "xslt:stylesheet", "Transform" };

                throw new TransformationException("xml.WrongContent", exArgs);
            }

            TransformerFactory tFactory = TransformerFactory.newInstance();
            // Process XSLT stylesheets in a secure manner
            tFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, Boolean.TRUE);

            /*
             * This transform requires an octet stream as input. If the actual
             * input is an XPath node-set, then the signature application should
             * attempt to convert it to octets (apply Canonical XML]) as described
             * in the Reference Processing Model (section 4.3.3.2).
             */
            Source xmlSource =
                new StreamSource(new ByteArrayInputStream(input.getBytes()));
            Source stylesheet;

            /*
             * This complicated transformation of the stylesheet itself is necessary
             * because of the need to get the pure style sheet. If we simply say
             * Source stylesheet = new DOMSource(this.xsltElement);
             * whereby this.xsltElement is not the rootElement of the Document,
             * this causes problems;
             * so we convert the stylesheet to byte[] and use this as input stream
             */
            {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                Transformer transformer = tFactory.newTransformer();
                DOMSource source = new DOMSource(xsltElement);
                StreamResult result = new StreamResult(os);

                transformer.transform(source, result);

                stylesheet =
                    new StreamSource(new ByteArrayInputStream(os.toByteArray()));
            }

            Transformer transformer = tFactory.newTransformer(stylesheet);

            // Force Xalan to use \n as line separator on all OSes. This
            // avoids OS specific signature validation failures due to line
            // separator differences in the transformed output. Unfortunately,
            // this is not a standard JAXP property so will not work with non-Xalan
            // implementations.
            try {
                transformer.setOutputProperty("{http://xml.apache.org/xalan}line-separator", "\n");
            } catch (Exception e) {
                log.log(java.util.logging.Level.WARNING, "Unable to set Xalan line-separator property: " + e.getMessage());
            }

            if (baos == null) {
                ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
                StreamResult outputTarget = new StreamResult(baos1);
                transformer.transform(xmlSource, outputTarget);
                return new XMLSignatureInput(baos1.toByteArray());
            }
            StreamResult outputTarget = new StreamResult(baos);

            transformer.transform(xmlSource, outputTarget);
            XMLSignatureInput output = new XMLSignatureInput((byte[])null);
            output.setOutputStream(baos);
            return output;
        } catch (XMLSecurityException ex) {
            Object exArgs[] = { ex.getMessage() };

            throw new TransformationException("generic.EmptyMessage", exArgs, ex);
        } catch (TransformerConfigurationException ex) {
            Object exArgs[] = { ex.getMessage() };

            throw new TransformationException("generic.EmptyMessage", exArgs, ex);
        } catch (TransformerException ex) {
            Object exArgs[] = { ex.getMessage() };

            throw new TransformationException("generic.EmptyMessage", exArgs, ex);
        }
    }
}
