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
package java8.com.sun.org.apache.xml.internal.security.algorithms.implementations;

import com.sun.org.apache.xml.internal.security.algorithms.JCEMapper;
import com.sun.org.apache.xml.internal.security.algorithms.MessageDigestAlgorithm;
import com.sun.org.apache.xml.internal.security.algorithms.SignatureAlgorithmSpi;
import com.sun.org.apache.xml.internal.security.signature.XMLSignature;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureException;
import com.sun.org.apache.xml.internal.security.utils.Constants;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

public abstract class IntegrityHmac extends SignatureAlgorithmSpi {

    /** {@link org.apache.commons.logging} logging facility */
    private static java.util.logging.Logger log =
        java.util.logging.Logger.getLogger(com.sun.org.apache.xml.internal.security.algorithms.implementations.IntegrityHmac.class.getName());

    /** Field macAlgorithm */
    private Mac macAlgorithm = null;

    /** Field HMACOutputLength */
    private int HMACOutputLength = 0;
    private boolean HMACOutputLengthSet = false;

    /**
     * Method engineGetURI
     *
     *@inheritDoc
     */
    public abstract String engineGetURI();

    /**
     * Returns the output length of the hash/digest.
     */
    abstract int getDigestLength();

    /**
     * Method IntegrityHmac
     *
     * @throws XMLSignatureException
     */
    public IntegrityHmac() throws XMLSignatureException {
        String algorithmID = JCEMapper.translateURItoJCEID(this.engineGetURI());
        if (log.isLoggable(java.util.logging.Level.FINE)) {
            log.log(java.util.logging.Level.FINE, "Created IntegrityHmacSHA1 using " + algorithmID);
        }

        try {
            this.macAlgorithm = Mac.getInstance(algorithmID);
        } catch (java.security.NoSuchAlgorithmException ex) {
            Object[] exArgs = { algorithmID, ex.getLocalizedMessage() };

            throw new XMLSignatureException("algorithms.NoSuchAlgorithm", exArgs);
        }
    }

    /**
     * Proxy method for {@link java.security.Signature#setParameter(
     * AlgorithmParameterSpec)}
     * which is executed on the internal {@link java.security.Signature} object.
     *
     * @param params
     * @throws XMLSignatureException
     */
    protected void engineSetParameter(AlgorithmParameterSpec params) throws XMLSignatureException {
        throw new XMLSignatureException("empty");
    }

    public void reset() {
        HMACOutputLength = 0;
        HMACOutputLengthSet = false;
        this.macAlgorithm.reset();
    }

    /**
     * Proxy method for {@link java.security.Signature#verify(byte[])}
     * which is executed on the internal {@link java.security.Signature} object.
     *
     * @param signature
     * @return true if the signature is correct
     * @throws XMLSignatureException
     */
    protected boolean engineVerify(byte[] signature) throws XMLSignatureException {
        try {
            if (this.HMACOutputLengthSet && this.HMACOutputLength < getDigestLength()) {
                if (log.isLoggable(java.util.logging.Level.FINE)) {
                    log.log(java.util.logging.Level.FINE, "HMACOutputLength must not be less than " + getDigestLength());
                }
                Object[] exArgs = { String.valueOf(getDigestLength()) };
                throw new XMLSignatureException("algorithms.HMACOutputLengthMin", exArgs);
            } else {
                byte[] completeResult = this.macAlgorithm.doFinal();
                return MessageDigestAlgorithm.isEqual(completeResult, signature);
            }
        } catch (IllegalStateException ex) {
            throw new XMLSignatureException("empty", ex);
        }
    }

    /**
     * Proxy method for {@link java.security.Signature#initVerify(java.security.PublicKey)}
     * which is executed on the internal {@link java.security.Signature} object.
     *
     * @param secretKey
     * @throws XMLSignatureException
     */
    protected void engineInitVerify(Key secretKey) throws XMLSignatureException {
        if (!(secretKey instanceof SecretKey)) {
            String supplied = secretKey.getClass().getName();
            String needed = SecretKey.class.getName();
            Object exArgs[] = { supplied, needed };

            throw new XMLSignatureException("algorithms.WrongKeyForThisOperation", exArgs);
        }

        try {
            this.macAlgorithm.init(secretKey);
        } catch (InvalidKeyException ex) {
            // reinstantiate Mac object to work around bug in JDK
            // see: http://bugs.sun.com/view_bug.do?bug_id=4953555
            Mac mac = this.macAlgorithm;
            try {
                this.macAlgorithm = Mac.getInstance(macAlgorithm.getAlgorithm());
            } catch (Exception e) {
                // this shouldn't occur, but if it does, restore previous Mac
                if (log.isLoggable(java.util.logging.Level.FINE)) {
                    log.log(java.util.logging.Level.FINE, "Exception when reinstantiating Mac:" + e);
                }
                this.macAlgorithm = mac;
            }
            throw new XMLSignatureException("empty", ex);
        }
    }

    /**
     * Proxy method for {@link java.security.Signature#sign()}
     * which is executed on the internal {@link java.security.Signature} object.
     *
     * @return the result of the {@link java.security.Signature#sign()} method
     * @throws XMLSignatureException
     */
    protected byte[] engineSign() throws XMLSignatureException {
        try {
            if (this.HMACOutputLengthSet && this.HMACOutputLength < getDigestLength()) {
                if (log.isLoggable(java.util.logging.Level.FINE)) {
                    log.log(java.util.logging.Level.FINE, "HMACOutputLength must not be less than " + getDigestLength());
                }
                Object[] exArgs = { String.valueOf(getDigestLength()) };
                throw new XMLSignatureException("algorithms.HMACOutputLengthMin", exArgs);
            } else {
                return this.macAlgorithm.doFinal();
            }
        } catch (IllegalStateException ex) {
            throw new XMLSignatureException("empty", ex);
        }
    }

    /**
     * Method engineInitSign
     *
     * @param secretKey
     * @throws XMLSignatureException
     */
    protected void engineInitSign(Key secretKey) throws XMLSignatureException {
        if (!(secretKey instanceof SecretKey)) {
            String supplied = secretKey.getClass().getName();
            String needed = SecretKey.class.getName();
            Object exArgs[] = { supplied, needed };

            throw new XMLSignatureException("algorithms.WrongKeyForThisOperation", exArgs);
        }

        try {
            this.macAlgorithm.init(secretKey);
        } catch (InvalidKeyException ex) {
            throw new XMLSignatureException("empty", ex);
        }
    }

    /**
     * Method engineInitSign
     *
     * @param secretKey
     * @param algorithmParameterSpec
     * @throws XMLSignatureException
     */
    protected void engineInitSign(
        Key secretKey, AlgorithmParameterSpec algorithmParameterSpec
    ) throws XMLSignatureException {
        if (!(secretKey instanceof SecretKey)) {
            String supplied = secretKey.getClass().getName();
            String needed = SecretKey.class.getName();
            Object exArgs[] = { supplied, needed };

            throw new XMLSignatureException("algorithms.WrongKeyForThisOperation", exArgs);
        }

        try {
            this.macAlgorithm.init(secretKey, algorithmParameterSpec);
        } catch (InvalidKeyException ex) {
            throw new XMLSignatureException("empty", ex);
        } catch (InvalidAlgorithmParameterException ex) {
            throw new XMLSignatureException("empty", ex);
        }
    }

    /**
     * Method engineInitSign
     *
     * @param secretKey
     * @param secureRandom
     * @throws XMLSignatureException
     */
    protected void engineInitSign(Key secretKey, SecureRandom secureRandom)
        throws XMLSignatureException {
        throw new XMLSignatureException("algorithms.CannotUseSecureRandomOnMAC");
    }

    /**
     * Proxy method for {@link java.security.Signature#update(byte[])}
     * which is executed on the internal {@link java.security.Signature} object.
     *
     * @param input
     * @throws XMLSignatureException
     */
    protected void engineUpdate(byte[] input) throws XMLSignatureException {
        try {
            this.macAlgorithm.update(input);
        } catch (IllegalStateException ex) {
            throw new XMLSignatureException("empty", ex);
        }
    }

    /**
     * Proxy method for {@link java.security.Signature#update(byte)}
     * which is executed on the internal {@link java.security.Signature} object.
     *
     * @param input
     * @throws XMLSignatureException
     */
    protected void engineUpdate(byte input) throws XMLSignatureException {
        try {
            this.macAlgorithm.update(input);
        } catch (IllegalStateException ex) {
            throw new XMLSignatureException("empty", ex);
        }
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
    protected void engineUpdate(byte buf[], int offset, int len) throws XMLSignatureException {
        try {
            this.macAlgorithm.update(buf, offset, len);
        } catch (IllegalStateException ex) {
            throw new XMLSignatureException("empty", ex);
        }
    }

    /**
     * Method engineGetJCEAlgorithmString
     * @inheritDoc
     *
     */
    protected String engineGetJCEAlgorithmString() {
        return this.macAlgorithm.getAlgorithm();
    }

    /**
     * Method engineGetJCEAlgorithmString
     *
     * @inheritDoc
     */
    protected String engineGetJCEProviderName() {
        return this.macAlgorithm.getProvider().getName();
    }

    /**
     * Method engineSetHMACOutputLength
     *
     * @param HMACOutputLength
     */
    protected void engineSetHMACOutputLength(int HMACOutputLength) {
        this.HMACOutputLength = HMACOutputLength;
        this.HMACOutputLengthSet = true;
    }

    /**
     * Method engineGetContextFromElement
     *
     * @param element
     */
    protected void engineGetContextFromElement(Element element) {
        super.engineGetContextFromElement(element);

        if (element == null) {
            throw new IllegalArgumentException("element null");
        }

        Text hmaclength =
            XMLUtils.selectDsNodeText(element.getFirstChild(), Constants._TAG_HMACOUTPUTLENGTH, 0);

        if (hmaclength != null) {
            this.HMACOutputLength = Integer.parseInt(hmaclength.getData());
            this.HMACOutputLengthSet = true;
        }
    }

    /**
     * Method engineAddContextToElement
     *
     * @param element
     */
    public void engineAddContextToElement(Element element) {
        if (element == null) {
            throw new IllegalArgumentException("null element");
        }

        if (this.HMACOutputLengthSet) {
            Document doc = element.getOwnerDocument();
            Element HMElem =
                XMLUtils.createElementInSignatureSpace(doc, Constants._TAG_HMACOUTPUTLENGTH);
            Text HMText =
                doc.createTextNode(Integer.valueOf(this.HMACOutputLength).toString());

            HMElem.appendChild(HMText);
            XMLUtils.addReturnToElement(element);
            element.appendChild(HMElem);
            XMLUtils.addReturnToElement(element);
        }
    }

    /**
     * Class IntegrityHmacSHA1
     */
    public static class IntegrityHmacSHA1 extends com.sun.org.apache.xml.internal.security.algorithms.implementations.IntegrityHmac {

        /**
         * Constructor IntegrityHmacSHA1
         *
         * @throws XMLSignatureException
         */
        public IntegrityHmacSHA1() throws XMLSignatureException {
            super();
        }

        /**
         * Method engineGetURI
         * @inheritDoc
         *
         */
        public String engineGetURI() {
            return XMLSignature.ALGO_ID_MAC_HMAC_SHA1;
        }

        int getDigestLength() {
            return 160;
        }
    }

    /**
     * Class IntegrityHmacSHA256
     */
    public static class IntegrityHmacSHA256 extends com.sun.org.apache.xml.internal.security.algorithms.implementations.IntegrityHmac {

        /**
         * Constructor IntegrityHmacSHA256
         *
         * @throws XMLSignatureException
         */
        public IntegrityHmacSHA256() throws XMLSignatureException {
            super();
        }

        /**
         * Method engineGetURI
         *
         * @inheritDoc
         */
        public String engineGetURI() {
            return XMLSignature.ALGO_ID_MAC_HMAC_SHA256;
        }

        int getDigestLength() {
            return 256;
        }
    }

    /**
     * Class IntegrityHmacSHA384
     */
    public static class IntegrityHmacSHA384 extends com.sun.org.apache.xml.internal.security.algorithms.implementations.IntegrityHmac {

        /**
         * Constructor IntegrityHmacSHA384
         *
         * @throws XMLSignatureException
         */
        public IntegrityHmacSHA384() throws XMLSignatureException {
            super();
        }

        /**
         * Method engineGetURI
         * @inheritDoc
         *
         */
        public String engineGetURI() {
            return XMLSignature.ALGO_ID_MAC_HMAC_SHA384;
        }

        int getDigestLength() {
            return 384;
        }
    }

    /**
     * Class IntegrityHmacSHA512
     */
    public static class IntegrityHmacSHA512 extends com.sun.org.apache.xml.internal.security.algorithms.implementations.IntegrityHmac {

        /**
         * Constructor IntegrityHmacSHA512
         *
         * @throws XMLSignatureException
         */
        public IntegrityHmacSHA512() throws XMLSignatureException {
            super();
        }

        /**
         * Method engineGetURI
         * @inheritDoc
         *
         */
        public String engineGetURI() {
            return XMLSignature.ALGO_ID_MAC_HMAC_SHA512;
        }

        int getDigestLength() {
            return 512;
        }
    }

    /**
     * Class IntegrityHmacRIPEMD160
     */
    public static class IntegrityHmacRIPEMD160 extends com.sun.org.apache.xml.internal.security.algorithms.implementations.IntegrityHmac {

        /**
         * Constructor IntegrityHmacRIPEMD160
         *
         * @throws XMLSignatureException
         */
        public IntegrityHmacRIPEMD160() throws XMLSignatureException {
            super();
        }

        /**
         * Method engineGetURI
         *
         * @inheritDoc
         */
        public String engineGetURI() {
            return XMLSignature.ALGO_ID_MAC_HMAC_RIPEMD160;
        }

        int getDigestLength() {
            return 160;
        }
    }

    /**
     * Class IntegrityHmacMD5
     */
    public static class IntegrityHmacMD5 extends com.sun.org.apache.xml.internal.security.algorithms.implementations.IntegrityHmac {

        /**
         * Constructor IntegrityHmacMD5
         *
         * @throws XMLSignatureException
         */
        public IntegrityHmacMD5() throws XMLSignatureException {
            super();
        }

        /**
         * Method engineGetURI
         *
         * @inheritDoc
         */
        public String engineGetURI() {
            return XMLSignature.ALGO_ID_MAC_HMAC_NOT_RECOMMENDED_MD5;
        }

        int getDigestLength() {
            return 128;
        }
    }
}
