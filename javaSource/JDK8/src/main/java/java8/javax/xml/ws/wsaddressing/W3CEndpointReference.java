/*
 * Copyright (c) 2005, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java8.javax.xml.ws.wsaddressing;


import org.w3c.dom.Element;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.WebServiceException;
import java.util.List;
import java.util.Map;


/**
 * This class represents a W3C Addressing EndpointReferece which is
 * a remote reference to a web service endpoint that supports the
 * W3C WS-Addressing 1.0 - Core Recommendation.
 * <p>
 * Developers should use this class in their SEIs if they want to
 * pass/return endpoint references that represent the W3C WS-Addressing
 * recommendation.
 * <p>
 * JAXB will use the JAXB annotations and bind this class to XML infoset
 * that is consistent with that defined by WS-Addressing.  See
 * <a href="http://www.w3.org/TR/2006/REC-ws-addr-core-20060509/">
 * WS-Addressing</a>
 * for more information on WS-Addressing EndpointReferences.
 *
 * @since JAX-WS 2.1
 */

// XmlRootElement allows this class to be marshalled on its own
@XmlRootElement(name="EndpointReference",namespace= javax.xml.ws.wsaddressing.W3CEndpointReference.NS)
@XmlType(name="EndpointReferenceType",namespace= javax.xml.ws.wsaddressing.W3CEndpointReference.NS)
public final class W3CEndpointReference extends EndpointReference {

    private final static JAXBContext w3cjc = getW3CJaxbContext();

    // should be changed to package private, keeping original modifier to keep backwards compatibility
    protected static final String NS = "http://www.w3.org/2005/08/addressing";

    // default constructor forbidden ...
    // should be private, keeping original modifier to keep backwards compatibility
    protected W3CEndpointReference() {
    }

    /**
     * Creates an EPR from infoset representation
     *
     * @param source A source object containing valid XmlInfoset
     * instance consistent with the W3C WS-Addressing Core
     * recommendation.
     *
     * @throws WebServiceException
     *   If the source does NOT contain a valid W3C WS-Addressing
     *   EndpointReference.
     * @throws NullPointerException
     *   If the <code>null</code> <code>source</code> value is given
     */
    public W3CEndpointReference(Source source) {
        try {
            javax.xml.ws.wsaddressing.W3CEndpointReference epr = w3cjc.createUnmarshaller().unmarshal(source, javax.xml.ws.wsaddressing.W3CEndpointReference.class).getValue();
            this.address = epr.address;
            this.metadata = epr.metadata;
            this.referenceParameters = epr.referenceParameters;
            this.elements = epr.elements;
            this.attributes = epr.attributes;
        } catch (JAXBException e) {
            throw new WebServiceException("Error unmarshalling W3CEndpointReference " ,e);
        } catch (ClassCastException e) {
            throw new WebServiceException("Source did not contain W3CEndpointReference", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void writeTo(Result result){
        try {
            Marshaller marshaller = w3cjc.createMarshaller();
            marshaller.marshal(this, result);
        } catch (JAXBException e) {
            throw new WebServiceException("Error marshalling W3CEndpointReference. ", e);
        }
    }

    private static JAXBContext getW3CJaxbContext() {
        try {
            return JAXBContext.newInstance(javax.xml.ws.wsaddressing.W3CEndpointReference.class);
        } catch (JAXBException e) {
            throw new WebServiceException("Error creating JAXBContext for W3CEndpointReference. ", e);
        }
    }

    // private but necessary properties for databinding
    @XmlElement(name="Address",namespace=NS)
    private Address address;
    @XmlElement(name="ReferenceParameters",namespace=NS)
    private Elements referenceParameters;
    @XmlElement(name="Metadata",namespace=NS)
    private Elements metadata;
    // attributes and elements are not private for performance reasons
    // (JAXB can bypass reflection)
    @XmlAnyAttribute
    Map<QName,String> attributes;
    @XmlAnyElement
    List<Element> elements;


    @XmlType(name="address", namespace= javax.xml.ws.wsaddressing.W3CEndpointReference.NS)
    private static class Address {
        protected Address() {}
        @XmlValue
        String uri;
        @XmlAnyAttribute
        Map<QName,String> attributes;
    }


    @XmlType(name="elements", namespace= javax.xml.ws.wsaddressing.W3CEndpointReference.NS)
    private static class Elements {
        protected Elements() {}
        @XmlAnyElement
        List<Element> elements;
        @XmlAnyAttribute
        Map<QName,String> attributes;
    }

}
