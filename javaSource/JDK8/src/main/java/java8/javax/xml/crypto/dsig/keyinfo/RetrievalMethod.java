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
 * $Id: RetrievalMethod.java,v 1.8 2005/05/10 16:35:35 mullan Exp $
 */
package java8.javax.xml.crypto.dsig.keyinfo;

import javax.xml.crypto.Data;
import javax.xml.crypto.URIReference;
import javax.xml.crypto.URIReferenceException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import java.util.List;

/**
 * A representation of the XML <code>RetrievalMethod</code> element as
 * defined in the <a href="http://www.w3.org/TR/xmldsig-core/">
 * W3C Recommendation for XML-Signature Syntax and Processing</a>.
 * A <code>RetrievalMethod</code> object is used to convey a reference to
 * <code>KeyInfo</code> information that is stored at another location.
 * The XML schema definition is defined as:
 *
 * <pre>
 *   &lt;element name="RetrievalMethod" type="ds:RetrievalMethodType"/&gt;
 *   &lt;complexType name="RetrievalMethodType"&gt;
 *     &lt;sequence&gt;
 *       &lt;element name="Transforms" type="ds:TransformsType" minOccurs="0"/&gt;
 *     &lt;/sequence&gt;
 *     &lt;attribute name="URI" type="anyURI"/&gt;
 *     &lt;attribute name="Type" type="anyURI" use="optional"/&gt;
 *   &lt;/complexType&gt;
 * </pre>
 *
 * A <code>RetrievalMethod</code> instance may be created by invoking one of the
 * {@link KeyInfoFactory#newRetrievalMethod newRetrievalMethod} methods
 * of the {@link KeyInfoFactory} class, and passing it the URI
 * identifying the location of the KeyInfo, an optional type URI identifying
 * the type of KeyInfo, and an optional list of {@link Transform}s; for example:
 * <pre>
 *   KeyInfoFactory factory = KeyInfoFactory.getInstance("DOM");
 *   RetrievalMethod rm = factory.newRetrievalMethod
 *      ("#KeyValue-1", KeyValue.DSA_TYPE, Collections.singletonList(Transform.BASE64));
 * </pre>
 *
 * @author Sean Mullan
 * @author JSR 105 Expert Group
 * @since 1.6
 * @see KeyInfoFactory#newRetrievalMethod(String)
 * @see KeyInfoFactory#newRetrievalMethod(String, String, List)
 */
public interface RetrievalMethod extends URIReference, XMLStructure {

    /**
     * Returns an {@link java.util.Collections#unmodifiableList unmodifiable
     * list} of {@link Transform}s of this <code>RetrievalMethod</code>.
     *
     * @return an unmodifiable list of <code>Transform</code> objects (may be
     *    empty but never <code>null</code>).
     */
    @SuppressWarnings("rawtypes")
    List getTransforms();

    /**
     * Returns the URI of the referenced <code>KeyInfo</code> information.
     *
     * @return the URI of the referenced <code>KeyInfo</code> information in
     *    RFC 2396 format (never <code>null</code>)
     */
    String getURI();

   /**
    * Dereferences the <code>KeyInfo</code> information referenced by this
    * <code>RetrievalMethod</code> and applies the specified
    * <code>Transform</code>s.
    *
    * @param context an <code>XMLCryptoContext</code> that may contain
    *    additional useful information for dereferencing the URI. The
    *    context's <code>baseURI</code> and <code>dereferencer</code>
    *    parameters (if specified) are used to resolve and dereference this
    *    <code>RetrievalMethod</code>
    * @return a <code>Data</code> object representing the raw contents of the
    *    <code>KeyInfo</code> information referenced by this
    *    <code>RetrievalMethod</code>. It is the caller's responsibility to
    *    convert the returned data to an appropriate
    *    <code>KeyInfo</code> object.
    * @throws NullPointerException if <code>context</code> is <code>null</code>
    * @throws URIReferenceException if there is an error while dereferencing
    */
    Data dereference(XMLCryptoContext context) throws URIReferenceException;
}
