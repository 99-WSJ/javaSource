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

import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFaultElement;

/**
 * A container for <code>DetailEntry</code> objects. <code>DetailEntry</code>
 * objects give detailed error information that is application-specific and
 * related to the <code>SOAPBody</code> object that contains it.
 *<P>
 * A <code>Detail</code> object, which is part of a <code>SOAPFault</code>
 * object, can be retrieved using the method <code>SOAPFault.getDetail</code>.
 * The <code>Detail</code> interface provides two methods. One creates a new
 * <code>DetailEntry</code> object and also automatically adds it to
 * the <code>Detail</code> object. The second method gets a list of the
 * <code>DetailEntry</code> objects contained in a <code>Detail</code>
 * object.
 * <P>
 * The following code fragment, in which <i>sf</i> is a <code>SOAPFault</code>
 * object, gets its <code>Detail</code> object (<i>d</i>), adds a new
 * <code>DetailEntry</code> object to <i>d</i>, and then gets a list of all the
 * <code>DetailEntry</code> objects in <i>d</i>. The code also creates a
 * <code>Name</code> object to pass to the method <code>addDetailEntry</code>.
 * The variable <i>se</i>, used to create the <code>Name</code> object,
 * is a <code>SOAPEnvelope</code> object.
 * <PRE>
 *    Detail d = sf.getDetail();
 *    Name name = se.createName("GetLastTradePrice", "WOMBAT",
 *                                "http://www.wombat.org/trader");
 *    d.addDetailEntry(name);
 *    Iterator it = d.getDetailEntries();
 * </PRE>
 */
public interface Detail extends SOAPFaultElement {

    /**
     * Creates a new <code>DetailEntry</code> object with the given
     * name and adds it to this <code>Detail</code> object.
     *
     * @param name a <code>Name</code> object identifying the
     *         new <code>DetailEntry</code> object
     *
     * @exception SOAPException thrown when there is a problem in adding a
     * DetailEntry object to this Detail object.
     *
     * @see javax.xml.soap.Detail#addDetailEntry(QName qname)
     */
    public DetailEntry addDetailEntry(Name name) throws SOAPException;

    /**
     * Creates a new <code>DetailEntry</code> object with the given
     * QName and adds it to this <code>Detail</code> object. This method
     * is the preferred over the one using Name.
     *
     * @param qname a <code>QName</code> object identifying the
     *         new <code>DetailEntry</code> object
     *
     * @exception SOAPException thrown when there is a problem in adding a
     * DetailEntry object to this Detail object.
     *
     * @see javax.xml.soap.Detail#addDetailEntry(Name name)
     * @since SAAJ 1.3
     */
    public DetailEntry addDetailEntry(QName qname) throws SOAPException;

    /**
     * Gets an Iterator over all of the <code>DetailEntry</code>s in this <code>Detail</code> object.
     *
     * @return an <code>Iterator</code> object over the <code>DetailEntry</code>
     *             objects in this <code>Detail</code> object
     */
    public Iterator getDetailEntries();
}
