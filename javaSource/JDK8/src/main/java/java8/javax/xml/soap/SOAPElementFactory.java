/*
 * Copyright (c) 2004, 2012, Oracle and/or its affiliates. All rights reserved.
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

package java8.javax.xml.soap;

import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;

/**
 * <code>SOAPElementFactory</code> is a factory for XML fragments that
 * will eventually end up in the SOAP part. These fragments
 * can be inserted as children of the <code>SOAPHeader</code> or
 * <code>SOAPBody</code> or <code>SOAPEnvelope</code>.
 *
 * <p>Elements created using this factory do not have the properties
 * of an element that lives inside a SOAP header document. These
 * elements are copied into the XML document tree when they are
 * inserted.
 * @deprecated - Use <code>javax.xml.soap.SOAPFactory</code> for creating SOAPElements.
 * @see SOAPFactory
 */
public class SOAPElementFactory {

    private SOAPFactory soapFactory;

    private SOAPElementFactory(SOAPFactory soapFactory) {
        this.soapFactory = soapFactory;
    }

    /**
     * Create a <code>SOAPElement</code> object initialized with the
     * given <code>Name</code> object.
     *
     * @param name a <code>Name</code> object with the XML name for
     *             the new element
     *
     * @return the new <code>SOAPElement</code> object that was
     *         created
     *
     * @exception SOAPException if there is an error in creating the
     *            <code>SOAPElement</code> object
     *
     * @deprecated Use
     * javax.xml.soap.SOAPFactory.createElement(javax.xml.soap.Name)
     * instead
     *
     * @see SOAPFactory#createElement(Name)
     * @see SOAPFactory#createElement(javax.xml.namespace.QName)
     */
    public SOAPElement create(Name name) throws SOAPException {
        return soapFactory.createElement(name);
    }

    /**
     * Create a <code>SOAPElement</code> object initialized with the
     * given local name.
     *
     * @param localName a <code>String</code> giving the local name for
     *             the new element
     *
     * @return the new <code>SOAPElement</code> object that was
     *         created
     *
     * @exception SOAPException if there is an error in creating the
     *            <code>SOAPElement</code> object
     *
     * @deprecated Use
     * javax.xml.soap.SOAPFactory.createElement(String localName) instead
     *
     * @see SOAPFactory#createElement(String)
     */
    public SOAPElement create(String localName) throws SOAPException {
        return soapFactory.createElement(localName);
    }

    /**
     * Create a new <code>SOAPElement</code> object with the given
     * local name, prefix and uri.
     *
     * @param localName a <code>String</code> giving the local name
     *                  for the new element
     * @param prefix the prefix for this <code>SOAPElement</code>
     * @param uri a <code>String</code> giving the URI of the
     *            namespace to which the new element belongs
     *
     * @exception SOAPException if there is an error in creating the
     *            <code>SOAPElement</code> object
     *
     * @deprecated Use
     * javax.xml.soap.SOAPFactory.createElement(String localName,
     *                      String prefix,
     *                      String uri)
     * instead
     *
     * @see SOAPFactory#createElement(String, String, String)
     */
    public SOAPElement create(String localName, String prefix, String uri)
        throws SOAPException {
        return soapFactory.createElement(localName, prefix, uri);
    }

    /**
     * Creates a new instance of <code>SOAPElementFactory</code>.
     *
     * @return a new instance of a <code>SOAPElementFactory</code>
     *
     * @exception SOAPException if there was an error creating the
     *            default <code>SOAPElementFactory</code>
     */
    public static javax.xml.soap.SOAPElementFactory newInstance() throws SOAPException {
        try {
            return new javax.xml.soap.SOAPElementFactory(SOAPFactory.newInstance());
        } catch (Exception ex) {
            throw new SOAPException(
                "Unable to create SOAP Element Factory: " + ex.getMessage());
        }
    }
}
