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
 * $Id: SignatureProperties.java,v 1.4 2005/05/10 16:03:46 mullan Exp $
 */
package java8.javax.xml.crypto.dsig;

import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureProperty;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import java.util.List;

/**
 * A representation of the XML <code>SignatureProperties</code> element as
 * defined in the <a href="http://www.w3.org/TR/xmldsig-core/">
 * W3C Recommendation for XML-Signature Syntax and Processing</a>.
 * The XML Schema Definition is defined as:
 * <pre><code>
 *&lt;element name="SignatureProperties" type="ds:SignaturePropertiesType"/&gt;
 *   &lt;complexType name="SignaturePropertiesType"&gt;
 *     &lt;sequence&gt;
 *       &lt;element ref="ds:SignatureProperty" maxOccurs="unbounded"/&gt;
 *     &lt;/sequence&gt;
 *     &lt;attribute name="Id" type="ID" use="optional"/&gt;
 *   &lt;/complexType&gt;
 * </code></pre>
 *
 * A <code>SignatureProperties</code> instance may be created by invoking the
 * {@link XMLSignatureFactory#newSignatureProperties newSignatureProperties}
 * method of the {@link XMLSignatureFactory} class; for example:
 *
 * <pre>
 *   XMLSignatureFactory factory = XMLSignatureFactory.getInstance("DOM");
 *   SignatureProperties properties =
 *      factory.newSignatureProperties(props, "signature-properties-1");
 * </pre>
 *
 * @author Sean Mullan
 * @author JSR 105 Expert Group
 * @since 1.6
 * @see XMLSignatureFactory#newSignatureProperties(List, String)
 * @see SignatureProperty
 */
public interface SignatureProperties extends XMLStructure {

    /**
     * URI that identifies the <code>SignatureProperties</code> element (this
     * can be specified as the value of the <code>type</code> parameter of the
     * {@link Reference} class to identify the referent's type).
     */
    final static String TYPE =
        "http://www.w3.org/2000/09/xmldsig#SignatureProperties";

    /**
     * Returns the Id of this <code>SignatureProperties</code>.
     *
     * @return the Id of this <code>SignatureProperties</code> (or
     *    <code>null</code> if not specified)
     */
    String getId();

    /**
     * Returns an {@link java.util.Collections#unmodifiableList unmodifiable
     * list} of one or more {@link SignatureProperty}s that are contained in
     * this <code>SignatureProperties</code>.
     *
     * @return an unmodifiable list of one or more
     *    <code>SignatureProperty</code>s
     */
    @SuppressWarnings("rawtypes")
    List getProperties();
}
