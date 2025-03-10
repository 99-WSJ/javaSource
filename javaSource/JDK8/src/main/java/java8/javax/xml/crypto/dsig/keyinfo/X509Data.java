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
 * $Id: X509Data.java,v 1.4 2005/05/10 16:35:35 mullan Exp $
 */
package java8.javax.xml.crypto.dsig.keyinfo;

import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.RetrievalMethod;
import javax.xml.crypto.dsig.keyinfo.X509IssuerSerial;
import java.security.cert.X509CRL;
import java.util.List;

/**
 * A representation of the XML <code>X509Data</code> element as defined in
 * the <a href="http://www.w3.org/TR/xmldsig-core/">
 * W3C Recommendation for XML-Signature Syntax and Processing</a>. An
 * <code>X509Data</code> object contains one or more identifers of keys
 * or X.509 certificates (or certificates' identifiers or a revocation list).
 * The XML Schema Definition is defined as:
 *
 * <pre>
 *    &lt;element name="X509Data" type="ds:X509DataType"/&gt;
 *    &lt;complexType name="X509DataType"&gt;
 *        &lt;sequence maxOccurs="unbounded"&gt;
 *          &lt;choice&gt;
 *            &lt;element name="X509IssuerSerial" type="ds:X509IssuerSerialType"/&gt;
 *            &lt;element name="X509SKI" type="base64Binary"/&gt;
 *            &lt;element name="X509SubjectName" type="string"/&gt;
 *            &lt;element name="X509Certificate" type="base64Binary"/&gt;
 *            &lt;element name="X509CRL" type="base64Binary"/&gt;
 *            &lt;any namespace="##other" processContents="lax"/&gt;
 *          &lt;/choice&gt;
 *        &lt;/sequence&gt;
 *    &lt;/complexType&gt;
 *
 *    &lt;complexType name="X509IssuerSerialType"&gt;
 *      &lt;sequence&gt;
 *        &lt;element name="X509IssuerName" type="string"/&gt;
 *        &lt;element name="X509SerialNumber" type="integer"/&gt;
 *      &lt;/sequence&gt;
 *    &lt;/complexType&gt;
 * </pre>
 *
 * An <code>X509Data</code> instance may be created by invoking the
 * {@link KeyInfoFactory#newX509Data newX509Data} methods of the
 * {@link KeyInfoFactory} class and passing it a list of one or more
 * {@link XMLStructure}s representing X.509 content; for example:
 * <pre>
 *   KeyInfoFactory factory = KeyInfoFactory.getInstance("DOM");
 *   X509Data x509Data = factory.newX509Data
 *       (Collections.singletonList("cn=Alice"));
 * </pre>
 *
 * @author Sean Mullan
 * @author JSR 105 Expert Group
 * @since 1.6
 * @see KeyInfoFactory#newX509Data(List)
 */
//@@@ check for illegal combinations of data violating MUSTs in W3c spec
public interface X509Data extends XMLStructure {

    /**
     * URI identifying the X509Data KeyInfo type:
     * http://www.w3.org/2000/09/xmldsig#X509Data. This can be specified as
     * the value of the <code>type</code> parameter of the
     * {@link RetrievalMethod} class to describe a remote
     * <code>X509Data</code> structure.
     */
    final static String TYPE = "http://www.w3.org/2000/09/xmldsig#X509Data";

    /**
     * URI identifying the binary (ASN.1 DER) X.509 Certificate KeyInfo type:
     * http://www.w3.org/2000/09/xmldsig#rawX509Certificate. This can be
     * specified as the value of the <code>type</code> parameter of the
     * {@link RetrievalMethod} class to describe a remote X509 Certificate.
     */
    final static String RAW_X509_CERTIFICATE_TYPE =
        "http://www.w3.org/2000/09/xmldsig#rawX509Certificate";

    /**
     * Returns an {@link java.util.Collections#unmodifiableList unmodifiable
     * list} of the content in this <code>X509Data</code>. Valid types are
     * {@link String} (subject names), <code>byte[]</code> (subject key ids),
     * {@link java.security.cert.X509Certificate}, {@link X509CRL},
     * or {@link XMLStructure} ({@link X509IssuerSerial}
     * objects or elements from an external namespace).
     *
     * @return an unmodifiable list of the content in this <code>X509Data</code>
     *    (never <code>null</code> or empty)
     */
    @SuppressWarnings("rawtypes")
    List getContent();
}
