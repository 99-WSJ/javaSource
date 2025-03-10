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
package java8.sun.org.apache.xml.internal.security.transforms;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
import com.sun.org.apache.xml.internal.security.exceptions.AlgorithmAlreadyRegisteredException;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.implementations.*;
import com.sun.org.apache.xml.internal.security.utils.Constants;
import com.sun.org.apache.xml.internal.security.utils.HelperNodeList;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java8.com.sun.org.apache.xml.internal.security.transforms.TransformSpi;
import java8.com.sun.org.apache.xml.internal.security.transforms.Transforms;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implements the behaviour of the <code>ds:Transform</code> element.
 *
 * This <code>Transform</code>(Factory) class acts as the Factory and Proxy of
 * the implementing class that supports the functionality of <a
 * href=http://www.w3.org/TR/xmldsig-core/#sec-TransformAlg>a Transform
 * algorithm</a>.
 * Implements the Factory and Proxy pattern for ds:Transform algorithms.
 *
 * @author Christian Geuer-Pollmann
 * @see Transforms
 * @see TransformSpi
 */
public final class Transform extends SignatureElementProxy {

    /** {@link org.apache.commons.logging} logging facility */
    private static java.util.logging.Logger log =
        java.util.logging.Logger.getLogger(com.sun.org.apache.xml.internal.security.transforms.Transform.class.getName());

    /** All available Transform classes are registered here */
    private static Map<String, Class<? extends TransformSpi>> transformSpiHash =
        new ConcurrentHashMap<String, Class<? extends TransformSpi>>();

    private final TransformSpi transformSpi;

    /**
     * Generates a Transform object that implements the specified
     * <code>Transform algorithm</code> URI.
     *
     * @param doc the proxy {@link Document}
     * @param algorithmURI <code>Transform algorithm</code> URI representation,
     * such as specified in
     * <a href=http://www.w3.org/TR/xmldsig-core/#sec-TransformAlg>Transform algorithm </a>
     * @throws InvalidTransformException
     */
    public Transform(Document doc, String algorithmURI) throws InvalidTransformException {
        this(doc, algorithmURI, (NodeList)null);
    }

    /**
     * Generates a Transform object that implements the specified
     * <code>Transform algorithm</code> URI.
     *
     * @param algorithmURI <code>Transform algorithm</code> URI representation,
     * such as specified in
     * <a href=http://www.w3.org/TR/xmldsig-core/#sec-TransformAlg>Transform algorithm </a>
     * @param contextChild the child element of <code>Transform</code> element
     * @param doc the proxy {@link Document}
     * @throws InvalidTransformException
     */
    public Transform(Document doc, String algorithmURI, Element contextChild)
        throws InvalidTransformException {
        super(doc);
        HelperNodeList contextNodes = null;

        if (contextChild != null) {
            contextNodes = new HelperNodeList();

            XMLUtils.addReturnToElement(doc, contextNodes);
            contextNodes.appendChild(contextChild);
            XMLUtils.addReturnToElement(doc, contextNodes);
        }

        transformSpi = initializeTransform(algorithmURI, contextNodes);
    }

    /**
     * Constructs {@link com.sun.org.apache.xml.internal.security.transforms.Transform}
     *
     * @param doc the {@link Document} in which <code>Transform</code> will be
     * placed
     * @param algorithmURI URI representation of <code>Transform algorithm</code>
     * @param contextNodes the child node list of <code>Transform</code> element
     * @throws InvalidTransformException
     */
    public Transform(Document doc, String algorithmURI, NodeList contextNodes)
        throws InvalidTransformException {
        super(doc);
        transformSpi = initializeTransform(algorithmURI, contextNodes);
    }

    /**
     * @param element <code>ds:Transform</code> element
     * @param BaseURI the URI of the resource where the XML instance was stored
     * @throws InvalidTransformException
     * @throws TransformationException
     * @throws XMLSecurityException
     */
    public Transform(Element element, String BaseURI)
        throws InvalidTransformException, TransformationException, XMLSecurityException {
        super(element, BaseURI);

        // retrieve Algorithm Attribute from ds:Transform
        String algorithmURI = element.getAttributeNS(null, Constants._ATT_ALGORITHM);

        if (algorithmURI == null || algorithmURI.length() == 0) {
            Object exArgs[] = { Constants._ATT_ALGORITHM, Constants._TAG_TRANSFORM };
            throw new TransformationException("xml.WrongContent", exArgs);
        }

        Class<? extends TransformSpi> transformSpiClass = transformSpiHash.get(algorithmURI);
        if (transformSpiClass == null) {
            Object exArgs[] = { algorithmURI };
            throw new InvalidTransformException("signature.Transform.UnknownTransform", exArgs);
        }
        try {
            transformSpi = transformSpiClass.newInstance();
        } catch (InstantiationException ex) {
            Object exArgs[] = { algorithmURI };
            throw new InvalidTransformException(
                "signature.Transform.UnknownTransform", exArgs, ex
            );
        } catch (IllegalAccessException ex) {
            Object exArgs[] = { algorithmURI };
            throw new InvalidTransformException(
                "signature.Transform.UnknownTransform", exArgs, ex
            );
        }
    }

    /**
     * Registers implementing class of the Transform algorithm with algorithmURI
     *
     * @param algorithmURI algorithmURI URI representation of <code>Transform algorithm</code>
     * @param implementingClass <code>implementingClass</code> the implementing
     * class of {@link TransformSpi}
     * @throws AlgorithmAlreadyRegisteredException if specified algorithmURI
     * is already registered
     */
    @SuppressWarnings("unchecked")
    public static void register(String algorithmURI, String implementingClass)
        throws AlgorithmAlreadyRegisteredException, ClassNotFoundException,
            InvalidTransformException {
        // are we already registered?
        Class<? extends TransformSpi> transformSpi = transformSpiHash.get(algorithmURI);
        if (transformSpi != null) {
            Object exArgs[] = { algorithmURI, transformSpi };
            throw new AlgorithmAlreadyRegisteredException("algorithm.alreadyRegistered", exArgs);
        }
        Class<? extends TransformSpi> transformSpiClass =
            (Class<? extends TransformSpi>)
                ClassLoaderUtils.loadClass(implementingClass, com.sun.org.apache.xml.internal.security.transforms.Transform.class);
        transformSpiHash.put(algorithmURI, transformSpiClass);
    }

    /**
     * Registers implementing class of the Transform algorithm with algorithmURI
     *
     * @param algorithmURI algorithmURI URI representation of <code>Transform algorithm</code>
     * @param implementingClass <code>implementingClass</code> the implementing
     * class of {@link TransformSpi}
     * @throws AlgorithmAlreadyRegisteredException if specified algorithmURI
     * is already registered
     */
    public static void register(String algorithmURI, Class<? extends TransformSpi> implementingClass)
        throws AlgorithmAlreadyRegisteredException {
        // are we already registered?
        Class<? extends TransformSpi> transformSpi = transformSpiHash.get(algorithmURI);
        if (transformSpi != null) {
            Object exArgs[] = { algorithmURI, transformSpi };
            throw new AlgorithmAlreadyRegisteredException("algorithm.alreadyRegistered", exArgs);
        }
        transformSpiHash.put(algorithmURI, implementingClass);
    }

    /**
     * This method registers the default algorithms.
     */
    public static void registerDefaultAlgorithms() {
        transformSpiHash.put(
            Transforms.TRANSFORM_BASE64_DECODE, TransformBase64Decode.class
        );
        transformSpiHash.put(
            Transforms.TRANSFORM_C14N_OMIT_COMMENTS, TransformC14N.class
        );
        transformSpiHash.put(
            Transforms.TRANSFORM_C14N_WITH_COMMENTS, TransformC14NWithComments.class
        );
        transformSpiHash.put(
            Transforms.TRANSFORM_C14N11_OMIT_COMMENTS, TransformC14N11.class
        );
        transformSpiHash.put(
            Transforms.TRANSFORM_C14N11_WITH_COMMENTS, TransformC14N11_WithComments.class
        );
        transformSpiHash.put(
            Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS, TransformC14NExclusive.class
        );
        transformSpiHash.put(
            Transforms.TRANSFORM_C14N_EXCL_WITH_COMMENTS, TransformC14NExclusiveWithComments.class
        );
        transformSpiHash.put(
            Transforms.TRANSFORM_XPATH, TransformXPath.class
        );
        transformSpiHash.put(
            Transforms.TRANSFORM_ENVELOPED_SIGNATURE, TransformEnvelopedSignature.class
        );
        transformSpiHash.put(
            Transforms.TRANSFORM_XSLT, TransformXSLT.class
        );
        transformSpiHash.put(
            Transforms.TRANSFORM_XPATH2FILTER, TransformXPath2Filter.class
        );
    }

    /**
     * Returns the URI representation of Transformation algorithm
     *
     * @return the URI representation of Transformation algorithm
     */
    public String getURI() {
        return this.constructionElement.getAttributeNS(null, Constants._ATT_ALGORITHM);
    }

    /**
     * Transforms the input, and generates {@link XMLSignatureInput} as output.
     *
     * @param input input {@link XMLSignatureInput} which can supplied Octet
     * Stream and NodeSet as Input of Transformation
     * @return the {@link XMLSignatureInput} class as the result of
     * transformation
     * @throws CanonicalizationException
     * @throws IOException
     * @throws InvalidCanonicalizerException
     * @throws TransformationException
     */
    public XMLSignatureInput performTransform(XMLSignatureInput input)
        throws IOException, CanonicalizationException,
               InvalidCanonicalizerException, TransformationException {
        return performTransform(input, null);
    }

    /**
     * Transforms the input, and generates {@link XMLSignatureInput} as output.
     *
     * @param input input {@link XMLSignatureInput} which can supplied Octect
     * Stream and NodeSet as Input of Transformation
     * @param os where to output the result of the last transformation
     * @return the {@link XMLSignatureInput} class as the result of
     * transformation
     * @throws CanonicalizationException
     * @throws IOException
     * @throws InvalidCanonicalizerException
     * @throws TransformationException
     */
    public XMLSignatureInput performTransform(
        XMLSignatureInput input, OutputStream os
    ) throws IOException, CanonicalizationException,
        InvalidCanonicalizerException, TransformationException {
        XMLSignatureInput result = null;

        try {
            result = transformSpi.enginePerformTransform(input, os, this);
        } catch (ParserConfigurationException ex) {
            Object exArgs[] = { this.getURI(), "ParserConfigurationException" };
            throw new CanonicalizationException(
                "signature.Transform.ErrorDuringTransform", exArgs, ex);
        } catch (SAXException ex) {
            Object exArgs[] = { this.getURI(), "SAXException" };
            throw new CanonicalizationException(
                "signature.Transform.ErrorDuringTransform", exArgs, ex);
        }

        return result;
    }

    /** @inheritDoc */
    public String getBaseLocalName() {
        return Constants._TAG_TRANSFORM;
    }

    /**
     * Initialize the transform object.
     */
    private TransformSpi initializeTransform(String algorithmURI, NodeList contextNodes)
        throws InvalidTransformException {

        this.constructionElement.setAttributeNS(null, Constants._ATT_ALGORITHM, algorithmURI);

        Class<? extends TransformSpi> transformSpiClass = transformSpiHash.get(algorithmURI);
        if (transformSpiClass == null) {
            Object exArgs[] = { algorithmURI };
            throw new InvalidTransformException("signature.Transform.UnknownTransform", exArgs);
        }
        TransformSpi newTransformSpi = null;
        try {
            newTransformSpi = transformSpiClass.newInstance();
        } catch (InstantiationException ex) {
            Object exArgs[] = { algorithmURI };
            throw new InvalidTransformException(
                "signature.Transform.UnknownTransform", exArgs, ex
            );
        } catch (IllegalAccessException ex) {
            Object exArgs[] = { algorithmURI };
            throw new InvalidTransformException(
                "signature.Transform.UnknownTransform", exArgs, ex
            );
        }

        if (log.isLoggable(java.util.logging.Level.FINE)) {
            log.log(java.util.logging.Level.FINE, "Create URI \"" + algorithmURI + "\" class \""
                      + newTransformSpi.getClass() + "\"");
            log.log(java.util.logging.Level.FINE, "The NodeList is " + contextNodes);
        }

        // give it to the current document
        if (contextNodes != null) {
            for (int i = 0; i < contextNodes.getLength(); i++) {
                this.constructionElement.appendChild(contextNodes.item(i).cloneNode(true));
            }
        }
        return newTransformSpi;
    }

}
