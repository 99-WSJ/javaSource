/*
 * Copyright (c) 2005, 2006, Oracle and/or its affiliates. All rights reserved.
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

package java8.sun.org.apache.xalan.internal.xsltc.trax;

import com.sun.org.apache.xalan.internal.xsltc.trax.SAX2StAXBaseWriter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.Locator2;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Namespace;
import java.util.*;

/**
 * @author Sunitha Reddy
 */
public class SAX2StAXEventWriter extends SAX2StAXBaseWriter {


    private XMLEventWriter writer;


    private XMLEventFactory eventFactory;


    private List namespaceStack = new ArrayList();


    private boolean needToCallStartDocument = false;


    public SAX2StAXEventWriter() {

        eventFactory = XMLEventFactory.newInstance();

    }


    public SAX2StAXEventWriter(XMLEventWriter writer) {

        this.writer = writer;
        eventFactory = XMLEventFactory.newInstance();

    }

    public SAX2StAXEventWriter(XMLEventWriter writer,
            XMLEventFactory factory) {

        this.writer = writer;
        if (factory != null) {

            this.eventFactory = factory;

        } else {

            eventFactory = XMLEventFactory.newInstance();

        }

    }

    public XMLEventWriter getEventWriter() {

        return writer;

    }


    public void setEventWriter(XMLEventWriter writer) {

        this.writer = writer;

    }


    public XMLEventFactory getEventFactory() {

        return eventFactory;

    }


    public void setEventFactory(XMLEventFactory factory) {

        this.eventFactory = factory;

    }

    public void startDocument() throws SAXException {

        super.startDocument();

        namespaceStack.clear();

        eventFactory.setLocation(getCurrentLocation());

        // Encoding and version info will be available only after startElement
        // is called for first time. So, defer START_DOCUMENT event of StAX till
        // that point of time.
        needToCallStartDocument = true;
    }

    private void writeStartDocument() throws SAXException {
        try {
            if (docLocator == null)
                writer.add(eventFactory.createStartDocument());
            else {
                try{
                    writer.add(eventFactory.createStartDocument(((Locator2)docLocator).getEncoding(),((Locator2)docLocator).getXMLVersion()));
                } catch(ClassCastException e){
                    writer.add(eventFactory.createStartDocument());
                }
            }
        } catch (XMLStreamException e) {
            throw new SAXException(e);
        }
        needToCallStartDocument = false;
    }

    public void endDocument() throws SAXException {

        eventFactory.setLocation(getCurrentLocation());

        try {

            writer.add(eventFactory.createEndDocument());

        } catch (XMLStreamException e) {

            throw new SAXException(e);

        }

        super.endDocument();

        // clear the namespaces
        namespaceStack.clear();

    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        if (needToCallStartDocument) {
            writeStartDocument();
        }

        // set document location
        eventFactory.setLocation(getCurrentLocation());

        // create attribute and namespace events
        Collection[] events = {null, null};
        createStartEvents(attributes, events);

        namespaceStack.add(events[0]);

        try {

            String[] qname = {null, null};
            parseQName(qName, qname);

            writer.add(eventFactory.createStartElement(qname[0], uri,
                    qname[1], events[1].iterator(), events[0].iterator()));

        } catch (XMLStreamException e) {

            throw new SAXException(e);

        } finally {

            super.startElement(uri, localName, qName, attributes);

        }

    }

    public void endElement(String uri, String localName, String qName)
            throws SAXException {

        super.endElement(uri, localName, qName);

        eventFactory.setLocation(getCurrentLocation());

        // parse name
        String[] qname = {null, null};
        parseQName(qName, qname);

        // get namespaces
        Collection nsList = (Collection) namespaceStack.remove(namespaceStack.size() - 1);
        Iterator nsIter = nsList.iterator();

        try {

            writer.add(eventFactory.createEndElement(qname[0], uri, qname[1],
                    nsIter));

        } catch (XMLStreamException e) {

            throw new SAXException(e);

        }

    }

    public void comment(char[] ch, int start, int length) throws SAXException {
        if (needToCallStartDocument) {
            // Drat. We were trying to postpone this until the first element so that we could get
            // the locator, but we can't output a comment before the start document, so we're just
            // going to have to do without the locator if it hasn't been set yet.
            writeStartDocument();
        }

        super.comment(ch, start, length);

        eventFactory.setLocation(getCurrentLocation());
        try {

            writer.add(eventFactory.createComment(new String(ch, start,
                    length)));

        } catch (XMLStreamException e) {

            throw new SAXException(e);

        }

    }

    public void characters(char[] ch, int start, int length)
            throws SAXException {

        super.characters(ch, start, length);

        try {

            if (!isCDATA) {

                eventFactory.setLocation(getCurrentLocation());
                writer.add(eventFactory.createCharacters(new String(ch,
                        start, length)));

            }

        } catch (XMLStreamException e) {

            throw new SAXException(e);

        }

    }

    public void ignorableWhitespace(char[] ch, int start, int length)
            throws SAXException {

        super.ignorableWhitespace(ch, start, length);
        characters(ch, start, length);

    }

    public void processingInstruction(String target, String data)
            throws SAXException {

        if (needToCallStartDocument) {
            // Drat. We were trying to postpone this until the first element so that we could get
            // the locator, but we can't output a PI before the start document, so we're just
            // going to have to do without the locator if it hasn't been set yet.
            writeStartDocument();
        }

        super.processingInstruction(target, data);
        try {

            writer.add(eventFactory.createProcessingInstruction(target, data));

        } catch (XMLStreamException e) {

            throw new SAXException(e);

        }

    }

    public void endCDATA() throws SAXException {

        eventFactory.setLocation(getCurrentLocation());
        try {

            writer.add(eventFactory.createCData(CDATABuffer.toString()));

        } catch (XMLStreamException e) {

            throw new SAXException(e);

        }

        super.endCDATA();

    }


    protected void createStartEvents(Attributes attributes, Collection[] events) {

        Map nsMap = null;
        List attrs = null;

        // create namespaces
        if (namespaces != null) {
            final int nDecls = namespaces.size();
            for (int i = 0; i < nDecls; i++) {
                final String prefix = (String) namespaces.elementAt(i++);
                String uri = (String) namespaces.elementAt(i);
                Namespace ns = createNamespace(prefix, uri);
                if (nsMap == null) {
                    nsMap = new HashMap();
                }
                nsMap.put(prefix, ns);
            }
        }

        // create attributes
        String[] qname = {null, null};
        for (int i = 0, s = attributes.getLength(); i < s; i++) {

            parseQName(attributes.getQName(i), qname);

            String attrPrefix = qname[0];
            String attrLocal = qname[1];

            String attrQName = attributes.getQName(i);
            String attrValue = attributes.getValue(i);
            String attrURI = attributes.getURI(i);

            if ("xmlns".equals(attrQName) || "xmlns".equals(attrPrefix)) {
                // namespace declaration disguised as an attribute. If the
                // namespace has already been declared, skip it, otherwise
                // write it as an namespace
                if (nsMap == null) {
                    nsMap = new HashMap();
                }

                if (!nsMap.containsKey(attrLocal)) {
                    Namespace ns = createNamespace(attrLocal, attrValue);
                    nsMap.put(attrLocal, ns);
                }

            } else {

                Attribute attribute;
                if (attrPrefix.length() > 0) {

                    attribute = eventFactory.createAttribute(attrPrefix,
                            attrURI, attrLocal, attrValue);

                } else {

                    attribute = eventFactory.createAttribute(attrLocal,
                            attrValue);

                }

                if (attrs == null) {

                    attrs = new ArrayList();

                }
                attrs.add(attribute);

            }
        }

        events[0] = (nsMap == null ? Collections.EMPTY_LIST : nsMap.values());
        events[1] = (attrs == null ? Collections.EMPTY_LIST : attrs);

    }

    protected Namespace createNamespace(String prefix, String uri) {

        if (prefix == null || prefix.length() == 0) {

            return eventFactory.createNamespace(uri);

        } else {

            return eventFactory.createNamespace(prefix, uri);

        }

    }

}
