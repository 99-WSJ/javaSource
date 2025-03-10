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
package java8.com.sun.org.apache.xml.internal.security.algorithms;

import com.sun.org.apache.xml.internal.security.algorithms.Algorithm;
import com.sun.org.apache.xml.internal.security.algorithms.SignatureAlgorithmSpi;
import com.sun.org.apache.xml.internal.security.algorithms.implementations.IntegrityHmac;
import com.sun.org.apache.xml.internal.security.algorithms.implementations.SignatureBaseRSA;
import com.sun.org.apache.xml.internal.security.algorithms.implementations.SignatureDSA;
import com.sun.org.apache.xml.internal.security.algorithms.implementations.SignatureECDSA;
import com.sun.org.apache.xml.internal.security.exceptions.AlgorithmAlreadyRegisteredException;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignature;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureException;
import com.sun.org.apache.xml.internal.security.utils.Constants;
import java8.sun.org.apache.xml.internal.security.algorithms.ClassLoaderUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.security.Key;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Allows selection of digital signature's algorithm, private keys, other
 * security parameters, and algorithm's ID.
 *
 * @author Christian Geuer-Pollmann
 */
public class SignatureAlgorithm extends Algorithm {

    /** {@link org.apache.commons.logging} logging facility */
    private static java.util.logging.Logger log =
        java.util.logging.Logger.getLogger(com.sun.org.apache.xml.internal.security.algorithms.SignatureAlgorithm.class.getName());

    /** All available algorithm classes are registered here */
    private static Map<String, Class<? extends SignatureAlgorithmSpi>> algorithmHash =
        new ConcurrentHashMap<String, Class<? extends SignatureAlgorithmSpi>>();

    /** Field signatureAlgorithm */
    private final SignatureAlgorithmSpi signatureAlgorithm;

    private final String algorithmURI;

    /**
     * Constructor SignatureAlgorithm
     *
     * @param doc
     * @param algorithmURI
     * @throws XMLSecurityException
     */
    public SignatureAlgorithm(Document doc, String algorithmURI) throws XMLSecurityException {
        super(doc, algorithmURI);
        this.algorithmURI = algorithmURI;

        signatureAlgorithm = getSignatureAlgorithmSpi(algorithmURI);
        signatureAlgorithm.engineGetContextFromElement(this.constructionElement);
    }

    /**
     * Constructor SignatureAlgorithm
     *
     * @param doc
     * @param algorithmURI
     * @param hmacOutputLength
     * @throws XMLSecurityException
     */
    public SignatureAlgorithm(
        Document doc, String algorithmURI, int hmacOutputLength
    ) throws XMLSecurityException {
        super(doc, algorithmURI);
        this.algorithmURI = algorithmURI;

        signatureAlgorithm = getSignatureAlgorithmSpi(algorithmURI);
        signatureAlgorithm.engineGetContextFromElement(this.constructionElement);

        signatureAlgorithm.engineSetHMACOutputLength(hmacOutputLength);
        ((IntegrityHmac)signatureAlgorithm).engineAddContextToElement(constructionElement);
    }

    /**
     * Constructor SignatureAlgorithm
     *
     * @param element
     * @param baseURI
     * @throws XMLSecurityException
     */
    public SignatureAlgorithm(Element element, String baseURI) throws XMLSecurityException {
        this(element, baseURI, false);
    }

    /**
     * Constructor SignatureAlgorithm
     *
     * @param element
     * @param baseURI
     * @param secureValidation
     * @throws XMLSecurityException
     */
    public SignatureAlgorithm(
        Element element, String baseURI, boolean secureValidation
    ) throws XMLSecurityException {
        super(element, baseURI);
        algorithmURI = this.getURI();

        Attr attr = element.getAttributeNodeNS(null, "Id");
        if (attr != null) {
            element.setIdAttributeNode(attr, true);
        }

        if (secureValidation && (XMLSignature.ALGO_ID_MAC_HMAC_NOT_RECOMMENDED_MD5.equals(algorithmURI)
            || XMLSignature.ALGO_ID_SIGNATURE_NOT_RECOMMENDED_RSA_MD5.equals(algorithmURI))) {
            Object exArgs[] = { algorithmURI };

            throw new XMLSecurityException("signature.signatureAlgorithm", exArgs);
        }

        signatureAlgorithm = getSignatureAlgorithmSpi(algorithmURI);
        signatureAlgorithm.engineGetContextFromElement(this.constructionElement);
    }

    /**
     * Get a SignatureAlgorithmSpi object corresponding to the algorithmURI argument
     */
    private static SignatureAlgorithmSpi getSignatureAlgorithmSpi(String algorithmURI)
        throws XMLSignatureException {
        try {
            Class<? extends SignatureAlgorithmSpi> implementingClass =
                algorithmHash.get(algorithmURI);
            if (log.isLoggable(java.util.logging.Level.FINE)) {
                log.log(java.util.logging.Level.FINE, "Create URI \"" + algorithmURI + "\" class \""
                   + implementingClass + "\"");
            }
            return implementingClass.newInstance();
        }  catch (IllegalAccessException ex) {
            Object exArgs[] = { algorithmURI, ex.getMessage() };
            throw new XMLSignatureException("algorithms.NoSuchAlgorithm", exArgs, ex);
        } catch (InstantiationException ex) {
            Object exArgs[] = { algorithmURI, ex.getMessage() };
            throw new XMLSignatureException("algorithms.NoSuchAlgorithm", exArgs, ex);
        } catch (NullPointerException ex) {
            Object exArgs[] = { algorithmURI, ex.getMessage() };
            throw new XMLSignatureException("algorithms.NoSuchAlgorithm", exArgs, ex);
        }
    }


    /**
     * Proxy method for {@link java.security.Signature#sign()}
     * which is executed on the internal {@link java.security.Signature} object.
     *
     * @return the result of the {@link java.security.Signature#sign()} method
     * @throws XMLSignatureException
     */
    public byte[] sign() throws XMLSignatureException {
        return signatureAlgorithm.engineSign();
    }

    /**
     * Proxy method for {@link java.security.Signature#getAlgorithm}
     * which is executed on the internal {@link java.security.Signature} object.
     *
     * @return the result of the {@link java.security.Signature#getAlgorithm} method
     */
    public String getJCEAlgorithmString() {
        return signatureAlgorithm.engineGetJCEAlgorithmString();
    }

    /**
     * Method getJCEProviderName
     *
     * @return The Provider of this Signature Algorithm
     */
    public String getJCEProviderName() {
        return signatureAlgorithm.engineGetJCEProviderName();
    }

    /**
     * Proxy method for {@link java.security.Signature#update(byte[])}
     * which is executed on the internal {@link java.security.Signature} object.
     *
     * @param input
     * @throws XMLSignatureException
     */
    public void update(byte[] input) throws XMLSignatureException {
        signatureAlgorithm.engineUpdate(input);
    }

    /**
     * Proxy method for {@link java.security.Signature#update(byte)}
     * which is executed on the internal {@link java.security.Signature} object.
     *
     * @param input
     * @throws XMLSignatureException
     */
    public void update(byte input) throws XMLSignatureException {
        signatureAlgorithm.engineUpdate(input);
    }

    /**
     * Proxy method for {@link java.security.Signature#update(byte[], int, int)}
     * which is executed on the internal {@link java.security.Signature} object.
     *
     * @param buf
     * @param offset
     * @param len
     * @throws XMLSignatureException
     */
    public void update(byte buf[], int offset, int len) throws XMLSignatureException {
        signatureAlgorithm.engineUpdate(buf, offset, len);
    }

    /**
     * Proxy method for {@link java.security.Signature#initSign(java.security.PrivateKey)}
     * which is executed on the internal {@link java.security.Signature} object.
     *
     * @param signingKey
     * @throws XMLSignatureException
     */
    public void initSign(Key signingKey) throws XMLSignatureException {
        signatureAlgorithm.engineInitSign(signingKey);
    }

    /**
     * Proxy method for {@link java.security.Signature#initSign(java.security.PrivateKey,
     * SecureRandom)}
     * which is executed on the internal {@link java.security.Signature} object.
     *
     * @param signingKey
     * @param secureRandom
     * @throws XMLSignatureException
     */
    public void initSign(Key signingKey, SecureRandom secureRandom) throws XMLSignatureException {
        signatureAlgorithm.engineInitSign(signingKey, secureRandom);
    }

    /**
     * Proxy method for {@link java.security.Signature#initSign(java.security.PrivateKey)}
     * which is executed on the internal {@link java.security.Signature} object.
     *
     * @param signingKey
     * @param algorithmParameterSpec
     * @throws XMLSignatureException
     */
    public void initSign(
        Key signingKey, AlgorithmParameterSpec algorithmParameterSpec
    ) throws XMLSignatureException {
        signatureAlgorithm.engineInitSign(signingKey, algorithmParameterSpec);
    }

    /**
     * Proxy method for {@link java.security.Signature#setParameter(
     * AlgorithmParameterSpec)}
     * which is executed on the internal {@link java.security.Signature} object.
     *
     * @param params
     * @throws XMLSignatureException
     */
    public void setParameter(AlgorithmParameterSpec params) throws XMLSignatureException {
        signatureAlgorithm.engineSetParameter(params);
    }

    /**
     * Proxy method for {@link java.security.Signature#initVerify(java.security.PublicKey)}
     * which is executed on the internal {@link java.security.Signature} object.
     *
     * @param verificationKey
     * @throws XMLSignatureException
     */
    public void initVerify(Key verificationKey) throws XMLSignatureException {
        signatureAlgorithm.engineInitVerify(verificationKey);
    }

    /**
     * Proxy method for {@link java.security.Signature#verify(byte[])}
     * which is executed on the internal {@link java.security.Signature} object.
     *
     * @param signature
     * @return true if if the signature is valid.
     *
     * @throws XMLSignatureException
     */
    public boolean verify(byte[] signature) throws XMLSignatureException {
        return signatureAlgorithm.engineVerify(signature);
    }

    /**
     * Returns the URI representation of Transformation algorithm
     *
     * @return the URI representation of Transformation algorithm
     */
    public final String getURI() {
        return constructionElement.getAttributeNS(null, Constants._ATT_ALGORITHM);
    }

    /**
     * Registers implementing class of the Transform algorithm with algorithmURI
     *
     * @param algorithmURI algorithmURI URI representation of <code>Transform algorithm</code>.
     * @param implementingClass <code>implementingClass</code> the implementing class of
     * {@link SignatureAlgorithmSpi}
     * @throws AlgorithmAlreadyRegisteredException if specified algorithmURI is already registered
     * @throws XMLSignatureException
     */
    @SuppressWarnings("unchecked")
    public static void register(String algorithmURI, String implementingClass)
       throws AlgorithmAlreadyRegisteredException, ClassNotFoundException,
           XMLSignatureException {
        if (log.isLoggable(java.util.logging.Level.FINE)) {
            log.log(java.util.logging.Level.FINE, "Try to register " + algorithmURI + " " + implementingClass);
        }

        // are we already registered?
        Class<? extends SignatureAlgorithmSpi> registeredClass = algorithmHash.get(algorithmURI);
        if (registeredClass != null) {
            Object exArgs[] = { algorithmURI, registeredClass };
            throw new AlgorithmAlreadyRegisteredException(
                "algorithm.alreadyRegistered", exArgs
            );
        }
        try {
            Class<? extends SignatureAlgorithmSpi> clazz =
                (Class<? extends SignatureAlgorithmSpi>)
                    ClassLoaderUtils.loadClass(implementingClass, com.sun.org.apache.xml.internal.security.algorithms.SignatureAlgorithm.class);
            algorithmHash.put(algorithmURI, clazz);
        } catch (NullPointerException ex) {
            Object exArgs[] = { algorithmURI, ex.getMessage() };
            throw new XMLSignatureException("algorithms.NoSuchAlgorithm", exArgs, ex);
        }
    }

    /**
     * Registers implementing class of the Transform algorithm with algorithmURI
     *
     * @param algorithmURI algorithmURI URI representation of <code>Transform algorithm</code>.
     * @param implementingClass <code>implementingClass</code> the implementing class of
     * {@link SignatureAlgorithmSpi}
     * @throws AlgorithmAlreadyRegisteredException if specified algorithmURI is already registered
     * @throws XMLSignatureException
     */
    public static void register(String algorithmURI, Class<? extends SignatureAlgorithmSpi> implementingClass)
       throws AlgorithmAlreadyRegisteredException, ClassNotFoundException,
           XMLSignatureException {
        if (log.isLoggable(java.util.logging.Level.FINE)) {
            log.log(java.util.logging.Level.FINE, "Try to register " + algorithmURI + " " + implementingClass);
        }

        // are we already registered?
        Class<? extends SignatureAlgorithmSpi> registeredClass = algorithmHash.get(algorithmURI);
        if (registeredClass != null) {
            Object exArgs[] = { algorithmURI, registeredClass };
            throw new AlgorithmAlreadyRegisteredException(
                "algorithm.alreadyRegistered", exArgs
            );
        }
        algorithmHash.put(algorithmURI, implementingClass);
    }

    /**
     * This method registers the default algorithms.
     */
    public static void registerDefaultAlgorithms() {
        algorithmHash.put(SignatureDSA.URI, SignatureDSA.class);
        algorithmHash.put(
            XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA1, SignatureBaseRSA.SignatureRSASHA1.class
        );
        algorithmHash.put(
            XMLSignature.ALGO_ID_MAC_HMAC_SHA1, IntegrityHmac.IntegrityHmacSHA1.class
        );
        algorithmHash.put(
            XMLSignature.ALGO_ID_SIGNATURE_NOT_RECOMMENDED_RSA_MD5,
            SignatureBaseRSA.SignatureRSAMD5.class
        );
        algorithmHash.put(
            XMLSignature.ALGO_ID_SIGNATURE_RSA_RIPEMD160,
            SignatureBaseRSA.SignatureRSARIPEMD160.class
        );
        algorithmHash.put(
            XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA256, SignatureBaseRSA.SignatureRSASHA256.class
        );
        algorithmHash.put(
            XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA384, SignatureBaseRSA.SignatureRSASHA384.class
        );
        algorithmHash.put(
            XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA512, SignatureBaseRSA.SignatureRSASHA512.class
        );
        algorithmHash.put(
            XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA1, SignatureECDSA.SignatureECDSASHA1.class
        );
        algorithmHash.put(
            XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA256, SignatureECDSA.SignatureECDSASHA256.class
        );
        algorithmHash.put(
            XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA384, SignatureECDSA.SignatureECDSASHA384.class
        );
        algorithmHash.put(
            XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA512, SignatureECDSA.SignatureECDSASHA512.class
        );
        algorithmHash.put(
            XMLSignature.ALGO_ID_MAC_HMAC_NOT_RECOMMENDED_MD5, IntegrityHmac.IntegrityHmacMD5.class
        );
        algorithmHash.put(
            XMLSignature.ALGO_ID_MAC_HMAC_RIPEMD160, IntegrityHmac.IntegrityHmacRIPEMD160.class
        );
        algorithmHash.put(
            XMLSignature.ALGO_ID_MAC_HMAC_SHA256, IntegrityHmac.IntegrityHmacSHA256.class
        );
        algorithmHash.put(
            XMLSignature.ALGO_ID_MAC_HMAC_SHA384, IntegrityHmac.IntegrityHmacSHA384.class
        );
        algorithmHash.put(
            XMLSignature.ALGO_ID_MAC_HMAC_SHA512, IntegrityHmac.IntegrityHmacSHA512.class
        );
    }

    /**
     * Method getBaseNamespace
     *
     * @return URI of this element
     */
    public String getBaseNamespace() {
        return Constants.SignatureSpecNS;
    }

    /**
     * Method getBaseLocalName
     *
     * @return Local name
     */
    public String getBaseLocalName() {
        return Constants._TAG_SIGNATUREMETHOD;
    }
}
