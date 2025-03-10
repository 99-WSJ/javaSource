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

import java.util.Locale;

import org.w3c.dom.Document;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;

/**
 * An object that represents the contents of the SOAP body
 * element in a SOAP message. A SOAP body element consists of XML data
 * that affects the way the application-specific content is processed.
 * <P>
 * A <code>SOAPBody</code> object contains <code>SOAPBodyElement</code>
 * objects, which have the content for the SOAP body.
 * A <code>SOAPFault</code> object, which carries status and/or
 * error information, is an example of a <code>SOAPBodyElement</code> object.
 *
 * @see SOAPFault
 */
public interface SOAPBody extends SOAPElement {

    /**
     * Creates a new <code>SOAPFault</code> object and adds it to
     * this <code>SOAPBody</code> object. The new <code>SOAPFault</code> will
     * have default values set for the mandatory child elements. The type of
     * the <code>SOAPFault</code> will be a SOAP 1.1 or a SOAP 1.2 <code>SOAPFault</code>
     * depending on the <code>protocol</code> specified while creating the
     * <code>MessageFactory</code> instance.
     * <p>
     * A <code>SOAPBody</code> may contain at most one <code>SOAPFault</code>
     * child element.
     *
     * @return the new <code>SOAPFault</code> object
     * @exception SOAPException if there is a SOAP error
     */
    public SOAPFault addFault() throws SOAPException;


    /**
     * Creates a new <code>SOAPFault</code> object and adds it to
     * this <code>SOAPBody</code> object. The type of the
     * <code>SOAPFault</code> will be a SOAP 1.1  or a SOAP 1.2
     * <code>SOAPFault</code> depending on the <code>protocol</code>
     * specified while creating the <code>MessageFactory</code> instance.
     * <p>
     * For SOAP 1.2 the <code>faultCode</code> parameter is the value of the
     * <i>Fault/Code/Value</i> element  and the <code>faultString</code> parameter
     * is the value of the <i>Fault/Reason/Text</i> element. For SOAP 1.1
     * the <code>faultCode</code> parameter is the value of the <code>faultcode</code>
     * element and the <code>faultString</code> parameter is the value of the <code>faultstring</code>
     * element.
     * <p>
     * A <code>SOAPBody</code> may contain at most one <code>SOAPFault</code>
     * child element.
     *
     * @param faultCode a <code>Name</code> object giving the fault
     *         code to be set; must be one of the fault codes defined in the Version
     *         of SOAP specification in use
     * @param faultString a <code>String</code> giving an explanation of
     *         the fault
     * @param locale a {@link Locale} object indicating
     *         the native language of the <code>faultString</code>
     * @return the new <code>SOAPFault</code> object
     * @exception SOAPException if there is a SOAP error
     * @see SOAPFault#setFaultCode
     * @see SOAPFault#setFaultString
     * @since SAAJ 1.2
     */
    public SOAPFault addFault(Name faultCode, String faultString, Locale locale) throws SOAPException;

    /**
     * Creates a new <code>SOAPFault</code> object and adds it to this
     * <code>SOAPBody</code> object. The type of the <code>SOAPFault</code>
     * will be a SOAP 1.1 or a SOAP 1.2 <code>SOAPFault</code> depending on
     * the <code>protocol</code> specified while creating the <code>MessageFactory</code>
     * instance.
     * <p>
     * For SOAP 1.2 the <code>faultCode</code> parameter is the value of the
     * <i>Fault/Code/Value</i> element  and the <code>faultString</code> parameter
     * is the value of the <i>Fault/Reason/Text</i> element. For SOAP 1.1
     * the <code>faultCode</code> parameter is the value of the <code>faultcode</code>
     * element and the <code>faultString</code> parameter is the value of the <code>faultstring</code>
     * element.
     * <p>
     * A <code>SOAPBody</code> may contain at most one <code>SOAPFault</code>
     * child element.
     *
     * @param faultCode
     *            a <code>QName</code> object giving the fault code to be
     *            set; must be one of the fault codes defined in the version
     *            of SOAP specification in use.
     * @param faultString
     *            a <code>String</code> giving an explanation of the fault
     * @param locale
     *            a {@link Locale Locale} object indicating the
     *            native language of the <code>faultString</code>
     * @return the new <code>SOAPFault</code> object
     * @exception SOAPException
     *                if there is a SOAP error
     * @see SOAPFault#setFaultCode
     * @see SOAPFault#setFaultString
     * @see javax.xml.soap.SOAPBody#addFault(Name faultCode, String faultString, Locale locale)
     *
     * @since SAAJ 1.3
     */
    public SOAPFault addFault(QName faultCode, String faultString, Locale locale)
        throws SOAPException;

    /**
     * Creates a new  <code>SOAPFault</code> object and adds it to this
     * <code>SOAPBody</code> object. The type of the <code>SOAPFault</code>
     * will be a SOAP 1.1 or a SOAP 1.2 <code>SOAPFault</code> depending on
     * the <code>protocol</code> specified while creating the <code>MessageFactory</code>
     * instance.
     * <p>
     * For SOAP 1.2 the <code>faultCode</code> parameter is the value of the
     * <i>Fault/Code/Value</i> element  and the <code>faultString</code> parameter
     * is the value of the <i>Fault/Reason/Text</i> element. For SOAP 1.1
     * the <code>faultCode</code> parameter is the value of the <i>faultcode</i>
     * element and the <code>faultString</code> parameter is the value of the <i>faultstring</i>
     * element.
     * <p>
     * In case of a SOAP 1.2 fault, the default value for the mandatory <code>xml:lang</code>
     * attribute on the <i>Fault/Reason/Text</i> element will be set to
     * <code>java.util.Locale.getDefault()</code>
     * <p>
     * A <code>SOAPBody</code> may contain at most one <code>SOAPFault</code>
     * child element.
     *
     * @param faultCode
     *            a <code>Name</code> object giving the fault code to be set;
     *            must be one of the fault codes defined in the version of SOAP
     *            specification in use
     * @param faultString
     *            a <code>String</code> giving an explanation of the fault
     * @return the new <code>SOAPFault</code> object
     * @exception SOAPException
     *                if there is a SOAP error
     * @see SOAPFault#setFaultCode
     * @see SOAPFault#setFaultString
     * @since SAAJ 1.2
     */
    public SOAPFault addFault(Name faultCode, String faultString)
        throws SOAPException;

    /**
     * Creates a new <code>SOAPFault</code> object and adds it to this <code>SOAPBody</code>
     * object. The type of the <code>SOAPFault</code>
     * will be a SOAP 1.1 or a SOAP 1.2 <code>SOAPFault</code> depending on
     * the <code>protocol</code> specified while creating the <code>MessageFactory</code>
     * instance.
     * <p>
     * For SOAP 1.2 the <code>faultCode</code> parameter is the value of the
     * <i>Fault/Code/Value</i> element  and the <code>faultString</code> parameter
     * is the value of the <i>Fault/Reason/Text</i> element. For SOAP 1.1
     * the <code>faultCode</code> parameter is the value of the <i>faultcode</i>
     * element and the <code>faultString</code> parameter is the value of the <i>faultstring</i>
     * element.
     * <p>
     * In case of a SOAP 1.2 fault, the default value for the mandatory <code>xml:lang</code>
     * attribute on the <i>Fault/Reason/Text</i> element will be set to
     * <code>java.util.Locale.getDefault()</code>
     * <p>
     * A <code>SOAPBody</code> may contain at most one <code>SOAPFault</code>
     * child element
     *
     * @param faultCode
     *            a <code>QName</code> object giving the fault code to be
     *            set; must be one of the fault codes defined in the version
     *            of  SOAP specification in use
     * @param faultString
     *            a <code>String</code> giving an explanation of the fault
     * @return the new <code>SOAPFault</code> object
     * @exception SOAPException
     *                if there is a SOAP error
     * @see SOAPFault#setFaultCode
     * @see SOAPFault#setFaultString
     * @see javax.xml.soap.SOAPBody#addFault(Name faultCode, String faultString)
     * @since SAAJ 1.3
     */
    public SOAPFault addFault(QName faultCode, String faultString)
        throws SOAPException;

    /**
     * Indicates whether a <code>SOAPFault</code> object exists in this
     * <code>SOAPBody</code> object.
     *
     * @return <code>true</code> if a <code>SOAPFault</code> object exists
     *         in this <code>SOAPBody</code> object; <code>false</code>
     *         otherwise
     */
    public boolean hasFault();

    /**
     * Returns the <code>SOAPFault</code> object in this <code>SOAPBody</code>
     * object.
     *
     * @return the <code>SOAPFault</code> object in this <code>SOAPBody</code>
     *         object if present, null otherwise.
     */
    public SOAPFault getFault();

    /**
     * Creates a new <code>SOAPBodyElement</code> object with the specified
     * name and adds it to this <code>SOAPBody</code> object.
     *
     * @param name
     *            a <code>Name</code> object with the name for the new <code>SOAPBodyElement</code>
     *            object
     * @return the new <code>SOAPBodyElement</code> object
     * @exception SOAPException
     *                if a SOAP error occurs
     * @see javax.xml.soap.SOAPBody#addBodyElement(QName)
     */
    public SOAPBodyElement addBodyElement(Name name) throws SOAPException;


    /**
     * Creates a new <code>SOAPBodyElement</code> object with the specified
     * QName and adds it to this <code>SOAPBody</code> object.
     *
     * @param qname
     *            a <code>QName</code> object with the qname for the new
     *            <code>SOAPBodyElement</code> object
     * @return the new <code>SOAPBodyElement</code> object
     * @exception SOAPException
     *                if a SOAP error occurs
     * @see javax.xml.soap.SOAPBody#addBodyElement(Name)
     * @since SAAJ 1.3
     */
    public SOAPBodyElement addBodyElement(QName qname) throws SOAPException;

    /**
     * Adds the root node of the DOM <code>{@link Document}</code>
     * to this <code>SOAPBody</code> object.
     * <p>
     * Calling this method invalidates the <code>document</code> parameter.
     * The client application should discard all references to this <code>Document</code>
     * and its contents upon calling <code>addDocument</code>. The behavior
     * of an application that continues to use such references is undefined.
     *
     * @param document
     *            the <code>Document</code> object whose root node will be
     *            added to this <code>SOAPBody</code>.
     * @return the <code>SOAPBodyElement</code> that represents the root node
     *         that was added.
     * @exception SOAPException
     *                if the <code>Document</code> cannot be added
     * @since SAAJ 1.2
     */
    public SOAPBodyElement addDocument(Document document)
        throws SOAPException;

    /**
     * Creates a new DOM <code>{@link Document}</code> and sets
     * the first child of this <code>SOAPBody</code> as it's document
     * element. The child <code>SOAPElement</code> is removed as part of the
     * process.
     *
     * @return the <code>{@link Document}</code> representation
     *         of the <code>SOAPBody</code> content.
     *
     * @exception SOAPException
     *                if there is not exactly one child <code>SOAPElement</code> of the <code>
     *              <code>SOAPBody</code>.
     *
     * @since SAAJ 1.3
     */
    public Document extractContentAsDocument()
        throws SOAPException;
}
