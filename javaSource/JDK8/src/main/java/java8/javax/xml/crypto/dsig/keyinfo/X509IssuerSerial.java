/*
 * Copyright (c) 2005, Oracle and/or its affiliates. All rights reserved.
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
 * $Id: X509IssuerSerial.java,v 1.4 2005/05/10 16:35:35 mullan Exp $
 */
package java8.javax.xml.crypto.dsig.keyinfo;

import java.math.BigInteger;
import java.security.cert.X509Certificate;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;

/**
 * A representation of the XML <code>X509IssuerSerial</code> element as
 * defined in the <a href="http://www.w3.org/TR/xmldsig-core/">
 * W3C Recommendation for XML-Signature Syntax and Processing</a>.
 * An <code>X509IssuerSerial</code> object contains an X.509 issuer
 * distinguished name (DN) and serial number pair. The XML schema definition is
 * defined as:
 *
 * <pre>
 *   &lt;element name="X509IssuerSerial" type="ds:X509IssuerSerialType"/&gt;
 *   &lt;complexType name="X509IssuerSerialType"&gt;
 *     &lt;sequence&gt;
 *       &lt;element name="X509IssuerName" type="string"/&gt;
 *       &lt;element name="X509SerialNumber" type="integer"/&gt;
 *     &lt;/sequence&gt;
 *   &lt;/complexType&gt;
 * </pre>
 *
 * An <code>X509IssuerSerial</code> instance may be created by invoking the
 * {@link KeyInfoFactory#newX509IssuerSerial newX509IssuerSerial} method
 * of the {@link KeyInfoFactory} class, and passing it a
 * <code>String</code> and <code>BigInteger</code> representing the X.500
 * DN and serial number. Here is an example of creating an
 * <code>X509IssuerSerial</code> from the issuer DN and serial number of an
 * existing {@link X509Certificate}:
 * <pre>
 * KeyInfoFactory factory = KeyInfoFactory.getInstance("DOM");
 * X509IssuerSerial issuer = factory.newX509IssuerSerial
 *     (cert.getIssuerX500Principal().getName(), cert.getSerialNumber());
 * </pre>
 *
 * @author Sean Mullan
 * @author JSR 105 Expert Group
 * @since 1.6
 * @see X509Data#getContent
 * @see KeyInfoFactory#newX509IssuerSerial(String, BigInteger)
 */
public interface X509IssuerSerial extends XMLStructure {

    /**
     * Returns the X.500 distinguished name of this
     * <code>X509IssuerSerial</code> in
     * <a href="http://www.ietf.org/rfc/rfc2253.txt">RFC 2253</a> String format.
     *
     * @return the X.500 distinguished name in RFC 2253 String format (never
     *    <code>null</code>)
     */
    String getIssuerName();

    /**
     * Returns the serial number of this <code>X509IssuerSerial</code>.
     *
     * @return the serial number (never <code>null</code>)
     */
    BigInteger getSerialNumber();
}
