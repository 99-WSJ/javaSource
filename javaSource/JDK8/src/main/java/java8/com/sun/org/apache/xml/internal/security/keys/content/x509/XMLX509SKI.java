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
import com.sun.org.apache.xml.internal.security.utils.Base64;
import com.sun.org.apache.xml.internal.security.utils.Constants;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.security.cert.X509Certificate;
import java.util.Arrays;

/**
 * Handles SubjectKeyIdentifier (SKI) for X.509v3.
 *
 * @see <A HREF="http://docs.oracle.com/javase/1.5.0/docs/api/java/security/cert/X509Extension.html">
 * Interface X509Extension</A>
 */
public class XMLX509SKI extends SignatureElementProxy implements XMLX509DataContent {

    /** {@link org.apache.commons.logging} logging facility */
    private static java.util.logging.Logger log =
        java.util.logging.Logger.getLogger(com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509SKI.class.getName());

    /**
     * <CODE>SubjectKeyIdentifier (id-ce-subjectKeyIdentifier) (2.5.29.14)</CODE>:
     * This extension identifies the public key being certified. It enables
     * distinct keys used by the same subject to be differentiated
     * (e.g., as key updating occurs).
     * <BR />
     * A key identifier shall be unique with respect to all key identifiers
     * for the subject with which it is used. This extension is always non-critical.
     */
    public static final String SKI_OID = "2.5.29.14";

    /**
     * Constructor X509SKI
     *
     * @param doc
     * @param skiBytes
     */
    public XMLX509SKI(Document doc, byte[] skiBytes) {
        super(doc);
        this.addBase64Text(skiBytes);
    }

    /**
     * Constructor XMLX509SKI
     *
     * @param doc
     * @param x509certificate
     * @throws XMLSecurityException
     */
    public XMLX509SKI(Document doc, X509Certificate x509certificate)
        throws XMLSecurityException {
        super(doc);
        this.addBase64Text(com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509SKI.getSKIBytesFromCert(x509certificate));
    }

    /**
     * Constructor XMLX509SKI
     *
     * @param element
     * @param BaseURI
     * @throws XMLSecurityException
     */
    public XMLX509SKI(Element element, String BaseURI) throws XMLSecurityException {
        super(element, BaseURI);
    }

    /**
     * Method getSKIBytes
     *
     * @return the skibytes
     * @throws XMLSecurityException
     */
    public byte[] getSKIBytes() throws XMLSecurityException {
        return this.getBytesFromTextChild();
    }

    /**
     * Method getSKIBytesFromCert
     *
     * @param cert
     * @return ski bytes from the given certificate
     *
     * @throws XMLSecurityException
     * @see java.security.cert.X509Extension#getExtensionValue(String)
     */
    public static byte[] getSKIBytesFromCert(X509Certificate cert)
        throws XMLSecurityException {

        if (cert.getVersion() < 3) {
            Object exArgs[] = { Integer.valueOf(cert.getVersion()) };
            throw new XMLSecurityException("certificate.noSki.lowVersion", exArgs);
        }

        /*
         * Gets the DER-encoded OCTET string for the extension value
         * (extnValue) identified by the passed-in oid String. The oid
         * string is represented by a set of positive whole numbers
         * separated by periods.
         */
        byte[] extensionValue = cert.getExtensionValue(com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509SKI.SKI_OID);
        if (extensionValue == null) {
            throw new XMLSecurityException("certificate.noSki.null");
        }

        /**
         * Strip away first four bytes from the extensionValue
         * The first two bytes are the tag and length of the extensionValue
         * OCTET STRING, and the next two bytes are the tag and length of
         * the ski OCTET STRING.
         */
        byte skidValue[] = new byte[extensionValue.length - 4];

        System.arraycopy(extensionValue, 4, skidValue, 0, skidValue.length);

        if (log.isLoggable(java.util.logging.Level.FINE)) {
            log.log(java.util.logging.Level.FINE, "Base64 of SKI is " + Base64.encode(skidValue));
        }

        return skidValue;
    }

    /** @inheritDoc */
    public boolean equals(Object obj) {
        if (!(obj instanceof com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509SKI)) {
            return false;
        }

        com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509SKI other = (com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509SKI) obj;

        try {
            return Arrays.equals(other.getSKIBytes(), this.getSKIBytes());
        } catch (XMLSecurityException ex) {
            return false;
        }
    }

    public int hashCode() {
        int result = 17;
        try {
            byte[] bytes = getSKIBytes();
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
        return Constants._TAG_X509SKI;
    }
}
