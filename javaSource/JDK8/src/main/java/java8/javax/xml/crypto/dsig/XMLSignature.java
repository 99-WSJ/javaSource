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
 * ===========================================================================
 *
 * (C) Copyright IBM Corp. 2003 All Rights Reserved.
 *
 * ===========================================================================
 */
/*
 * $Id: XMLSignature.java,v 1.10 2005/05/10 16:03:48 mullan Exp $
 */
package java8.javax.xml.crypto.dsig;

import javax.xml.crypto.KeySelector;
import javax.xml.crypto.KeySelectorResult;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.XMLObject;
import javax.xml.crypto.dsig.XMLSignContext;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.XMLValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import java.security.Signature;
import java.util.List;

/**
 * A representation of the XML <code>Signature</code> element as
 * defined in the <a href="http://www.w3.org/TR/xmldsig-core/">
 * W3C Recommendation for XML-Signature Syntax and Processing</a>.
 * This class contains methods for signing and validating XML signatures
 * with behavior as defined by the W3C specification. The XML Schema Definition
 * is defined as:
 * <pre><code>
 * &lt;element name="Signature" type="ds:SignatureType"/&gt;
 * &lt;complexType name="SignatureType"&gt;
 *    &lt;sequence&gt;
 *      &lt;element ref="ds:SignedInfo"/&gt;
 *      &lt;element ref="ds:SignatureValue"/&gt;
 *      &lt;element ref="ds:KeyInfo" minOccurs="0"/&gt;
 *      &lt;element ref="ds:Object" minOccurs="0" maxOccurs="unbounded"/&gt;
 *    &lt;/sequence&gt;
 *    &lt;attribute name="Id" type="ID" use="optional"/&gt;
 * &lt;/complexType&gt;
 * </code></pre>
 * <p>
 * An <code>XMLSignature</code> instance may be created by invoking one of the
 * {@link XMLSignatureFactory#newXMLSignature newXMLSignature} methods of the
 * {@link XMLSignatureFactory} class.
 *
 * <p>If the contents of the underlying document containing the
 * <code>XMLSignature</code> are subsequently modified, the behavior is
 * undefined.
 *
 * <p>Note that this class is named <code>XMLSignature</code> rather than
 * <code>Signature</code> to avoid naming clashes with the existing
 * {@link Signature java.security.Signature} class.
 *
 * @see XMLSignatureFactory#newXMLSignature(SignedInfo, KeyInfo)
 * @see XMLSignatureFactory#newXMLSignature(SignedInfo, KeyInfo, List, String, String)
 * @author Joyce L. Leung
 * @author Sean Mullan
 * @author Erwin van der Koogh
 * @author JSR 105 Expert Group
 * @since 1.6
 */
public interface XMLSignature extends XMLStructure {

    /**
     * The XML Namespace URI of the W3C Recommendation for XML-Signature
     * Syntax and Processing.
     */
    final static String XMLNS = "http://www.w3.org/2000/09/xmldsig#";

    /**
     * Validates the signature according to the
     * <a href="http://www.w3.org/TR/xmldsig-core/#sec-CoreValidation">
     * core validation processing rules</a>. This method validates the
     * signature using the existing state, it does not unmarshal and
     * reinitialize the contents of the <code>XMLSignature</code> using the
     * location information specified in the context.
     *
     * <p>This method only validates the signature the first time it is
     * invoked. On subsequent invocations, it returns a cached result.
     *
     * @param validateContext the validating context
     * @return <code>true</code> if the signature passed core validation,
     *    otherwise <code>false</code>
     * @throws ClassCastException if the type of <code>validateContext</code>
     *    is not compatible with this <code>XMLSignature</code>
     * @throws NullPointerException if <code>validateContext</code> is
     *    <code>null</code>
     * @throws XMLSignatureException if an unexpected error occurs during
     *    validation that prevented the validation operation from completing
     */
    boolean validate(XMLValidateContext validateContext)
        throws XMLSignatureException;

    /**
     * Returns the key info of this <code>XMLSignature</code>.
     *
     * @return the key info (may be <code>null</code> if not specified)
     */
    KeyInfo getKeyInfo();

    /**
     * Returns the signed info of this <code>XMLSignature</code>.
     *
     * @return the signed info (never <code>null</code>)
     */
    SignedInfo getSignedInfo();

    /**
     * Returns an {@link java.util.Collections#unmodifiableList unmodifiable
     * list} of {@link XMLObject}s contained in this <code>XMLSignature</code>.
     *
     * @return an unmodifiable list of <code>XMLObject</code>s (may be empty
     *    but never <code>null</code>)
     */
    @SuppressWarnings("rawtypes")
    List getObjects();

    /**
     * Returns the optional Id of this <code>XMLSignature</code>.
     *
     * @return the Id (may be <code>null</code> if not specified)
     */
    String getId();

    /**
     * Returns the signature value of this <code>XMLSignature</code>.
     *
     * @return the signature value
     */
    SignatureValue getSignatureValue();

    /**
     * Signs this <code>XMLSignature</code>.
     *
     * <p>If this method throws an exception, this <code>XMLSignature</code> and
     * the <code>signContext</code> parameter will be left in the state that
     * it was in prior to the invocation.
     *
     * @param signContext the signing context
     * @throws ClassCastException if the type of <code>signContext</code> is
     *    not compatible with this <code>XMLSignature</code>
     * @throws NullPointerException if <code>signContext</code> is
     *    <code>null</code>
     * @throws MarshalException if an exception occurs while marshalling
     * @throws XMLSignatureException if an unexpected exception occurs while
     *    generating the signature
     */
    void sign(XMLSignContext signContext) throws MarshalException,
        XMLSignatureException;

    /**
     * Returns the result of the {@link KeySelector}, if specified, after
     * this <code>XMLSignature</code> has been signed or validated.
     *
     * @return the key selector result, or <code>null</code> if a key
     *    selector has not been specified or this <code>XMLSignature</code>
     *    has not been signed or validated
     */
    KeySelectorResult getKeySelectorResult();

    /**
     * A representation of the XML <code>SignatureValue</code> element as
     * defined in the <a href="http://www.w3.org/TR/xmldsig-core/">
     * W3C Recommendation for XML-Signature Syntax and Processing</a>.
     * The XML Schema Definition is defined as:
     * <p>
     * <pre>
     *   &lt;element name="SignatureValue" type="ds:SignatureValueType"/&gt;
     *     &lt;complexType name="SignatureValueType"&gt;
     *       &lt;simpleContent&gt;
     *         &lt;extension base="base64Binary"&gt;
     *           &lt;attribute name="Id" type="ID" use="optional"/&gt;
     *         &lt;/extension&gt;
     *       &lt;/simpleContent&gt;
     *     &lt;/complexType&gt;
     * </pre>
     *
     * @author Sean Mullan
     * @author JSR 105 Expert Group
     */
    public interface SignatureValue extends XMLStructure {
        /**
         * Returns the optional <code>Id</code> attribute of this
         * <code>SignatureValue</code>, which permits this element to be
         * referenced from elsewhere.
         *
         * @return the <code>Id</code> attribute (may be <code>null</code> if
         *    not specified)
         */
        String getId();

        /**
         * Returns the signature value of this <code>SignatureValue</code>.
         *
         * @return the signature value (may be <code>null</code> if the
         *    <code>XMLSignature</code> has not been signed yet). Each
         *    invocation of this method returns a new clone of the array to
         *    prevent subsequent modification.
         */
        byte[] getValue();

        /**
         * Validates the signature value. This method performs a
         * cryptographic validation of the signature calculated over the
         * <code>SignedInfo</code> of the <code>XMLSignature</code>.
         *
         * <p>This method only validates the signature the first
         * time it is invoked. On subsequent invocations, it returns a cached
         * result.
         *
         * @return <code>true</code> if the signature was
         *    validated successfully; <code>false</code> otherwise
         * @param validateContext the validating context
         * @throws NullPointerException if <code>validateContext</code> is
         *    <code>null</code>
         * @throws XMLSignatureException if an unexpected exception occurs while
         *    validating the signature
         */
        boolean validate(XMLValidateContext validateContext)
            throws XMLSignatureException;
    }
}
