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
package java8.sun.org.apache.xml.internal.security.keys.content.x509;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509DataContent;
import com.sun.org.apache.xml.internal.security.utils.Constants;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.ByteArrayInputStream;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;

public class XMLX509Certificate extends SignatureElementProxy implements XMLX509DataContent {

    /** Field JCA_CERT_ID */
    public static final String JCA_CERT_ID = "X.509";

    /**
     * Constructor X509Certificate
     *
     * @param element
     * @param BaseURI
     * @throws XMLSecurityException
     */
    public XMLX509Certificate(Element element, String BaseURI) throws XMLSecurityException {
        super(element, BaseURI);
    }

    /**
     * Constructor X509Certificate
     *
     * @param doc
     * @param certificateBytes
     */
    public XMLX509Certificate(Document doc, byte[] certificateBytes) {
        super(doc);

        this.addBase64Text(certificateBytes);
    }

    /**
     * Constructor XMLX509Certificate
     *
     * @param doc
     * @param x509certificate
     * @throws XMLSecurityException
     */
    public XMLX509Certificate(Document doc, X509Certificate x509certificate)
        throws XMLSecurityException {
        super(doc);

        try {
            this.addBase64Text(x509certificate.getEncoded());
        } catch (java.security.cert.CertificateEncodingException ex) {
            throw new XMLSecurityException("empty", ex);
        }
    }

    /**
     * Method getCertificateBytes
     *
     * @return the certificate bytes
     * @throws XMLSecurityException
     */
    public byte[] getCertificateBytes() throws XMLSecurityException {
        return this.getBytesFromTextChild();
    }

    /**
     * Method getX509Certificate
     *
     * @return the x509 certificate
     * @throws XMLSecurityException
     */
    public X509Certificate getX509Certificate() throws XMLSecurityException {
        try {
            byte certbytes[] = this.getCertificateBytes();
            CertificateFactory certFact =
                CertificateFactory.getInstance(com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509Certificate.JCA_CERT_ID);
            X509Certificate cert =
                (X509Certificate) certFact.generateCertificate(
                    new ByteArrayInputStream(certbytes)
                );

            if (cert != null) {
                return cert;
            }

            return null;
        } catch (CertificateException ex) {
            throw new XMLSecurityException("empty", ex);
        }
    }

    /**
     * Method getPublicKey
     *
     * @return the publickey
     * @throws XMLSecurityException
     */
    public PublicKey getPublicKey() throws XMLSecurityException {
        X509Certificate cert = this.getX509Certificate();

        if (cert != null) {
            return cert.getPublicKey();
        }

        return null;
    }

    /** @inheritDoc */
    public boolean equals(Object obj) {
        if (!(obj instanceof com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509Certificate)) {
            return false;
        }
        com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509Certificate other = (com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509Certificate) obj;
        try {
            return Arrays.equals(other.getCertificateBytes(), this.getCertificateBytes());
        } catch (XMLSecurityException ex) {
            return false;
        }
    }

    public int hashCode() {
        int result = 17;
        try {
            byte[] bytes = getCertificateBytes();
            for (int i = 0; i < bytes.length; i++) {
                result = 31 * result + bytes[i];
            }
        } catch (XMLSecurityException e) {
            if (log.isLoggable(java.util.logging.Level.FINE)) {
                log.log(java.util.logging.Level.FINE, e.getMessage(), e);
            }
        }
        return result;
    }

    /** @inheritDoc */
    public String getBaseLocalName() {
        return Constants._TAG_X509CERTIFICATE;
    }
}
