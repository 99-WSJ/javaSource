/*
 * Copyright (c) 2007-2012, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * $Id: ToTextSAXHandler.java,v 1.3 2005/09/28 13:49:08 pvedula Exp $
 */
package java8.sun.org.apache.xml.internal.serializer;

import java8.com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import java8.com.sun.org.apache.xml.internal.serializer.ToSAXHandler;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Properties;

/**
 * This class converts SAX-like event to SAX events for
 * xsl:output method "text".
 *
 * This class is only to be used internally. This class is not a public API.
 *
 * @xsl.usage internal
 */
public final class ToTextSAXHandler extends ToSAXHandler
{
    /**
     * From XSLTC
     * @see ExtendedContentHandler#endElement(String)
     */
    public void endElement(String elemName) throws SAXException
    {
        if (m_tracer != null)
            super.fireEndElem(elemName);
    }

    /**
     * @see ContentHandler#endElement(String, String, String)
     */
    public void endElement(String arg0, String arg1, String arg2)
        throws SAXException
    {
                if (m_tracer != null)
            super.fireEndElem(arg2);
    }

    public ToTextSAXHandler(ContentHandler hdlr, LexicalHandler lex, String encoding)
    {
        super(hdlr, lex, encoding);
    }

        /**
     * From XSLTC
     */
    public ToTextSAXHandler(ContentHandler handler, String encoding)
    {
        super(handler,encoding);
    }

    public void comment(char ch[], int start, int length)
        throws SAXException
    {
        if (m_tracer != null)
            super.fireCommentEvent(ch, start, length);
    }

    public void comment(String data) throws SAXException
    {
        final int length = data.length();
        if (length > m_charsBuff.length)
        {
            m_charsBuff = new char[length*2 + 1];
        }
        data.getChars(0, length, m_charsBuff, 0);
        comment(m_charsBuff, 0, length);
    }

    /**
     * @see Serializer#getOutputFormat()
     */
    public Properties getOutputFormat()
    {
        return null;
    }

    /**
     * @see Serializer#getOutputStream()
     */
    public OutputStream getOutputStream()
    {
        return null;
    }

    /**
     * @see Serializer#getWriter()
     */
    public Writer getWriter()
    {
        return null;
    }

    /**
     * Does nothing because
     * the indent attribute is ignored for text output.
     *
     */
    public void indent(int n) throws SAXException
    {
    }

    /**
     * @see Serializer#reset()
     */
    public boolean reset()
    {
        return false;
    }

    /**
     * @see DOMSerializer#serialize(Node)
     */
    public void serialize(Node node) throws IOException
    {
    }

    /**
     * @see SerializationHandler#setEscaping(boolean)
     */
    public boolean setEscaping(boolean escape)
    {
        return false;
    }

    /**
     * @see SerializationHandler#setIndent(boolean)
     */
    public void setIndent(boolean indent)
    {
    }

    /**
     * @see Serializer#setOutputFormat(Properties)
     */
    public void setOutputFormat(Properties format)
    {
    }

    /**
     * @see Serializer#setOutputStream(OutputStream)
     */
    public void setOutputStream(OutputStream output)
    {
    }

    /**
     * @see Serializer#setWriter(Writer)
     */
    public void setWriter(Writer writer)
    {
    }

    /**
     * @see ExtendedContentHandler#addAttribute(String, String, String, String, String)
     */
    public void addAttribute(
        String uri,
        String localName,
        String rawName,
        String type,
        String value,
        boolean XSLAttribute)
    {
    }

    /**
     * @see org.xml.sax.ext.DeclHandler#attributeDecl(String, String, String, String, String)
     */
    public void attributeDecl(
        String arg0,
        String arg1,
        String arg2,
        String arg3,
        String arg4)
        throws SAXException
    {
    }

    /**
     * @see org.xml.sax.ext.DeclHandler#elementDecl(String, String)
     */
    public void elementDecl(String arg0, String arg1) throws SAXException
    {
    }

    /**
     * @see org.xml.sax.ext.DeclHandler#externalEntityDecl(String, String, String)
     */
    public void externalEntityDecl(String arg0, String arg1, String arg2)
        throws SAXException
    {
    }

    /**
     * @see org.xml.sax.ext.DeclHandler#internalEntityDecl(String, String)
     */
    public void internalEntityDecl(String arg0, String arg1)
        throws SAXException
    {
    }

    /**
     * @see ContentHandler#endPrefixMapping(String)
     */
    public void endPrefixMapping(String arg0) throws SAXException
    {
    }

    /**
     * @see ContentHandler#ignorableWhitespace(char[], int, int)
     */
    public void ignorableWhitespace(char[] arg0, int arg1, int arg2)
        throws SAXException
    {
    }

    /**
     * From XSLTC
     * @see ContentHandler#processingInstruction(String, String)
     */
    public void processingInstruction(String arg0, String arg1)
        throws SAXException
    {
        if (m_tracer != null)
            super.fireEscapingEvent(arg0, arg1);
    }

    /**
     * @see ContentHandler#setDocumentLocator(Locator)
     */
    public void setDocumentLocator(Locator arg0)
    {
        super.setDocumentLocator(arg0);
    }

    /**
     * @see ContentHandler#skippedEntity(String)
     */
    public void skippedEntity(String arg0) throws SAXException
    {
    }

    /**
     * @see ContentHandler#startElement(String, String, String, Attributes)
     */
    public void startElement(
        String arg0,
        String arg1,
        String arg2,
        Attributes arg3)
        throws SAXException
    {
        flushPending();
        super.startElement(arg0, arg1, arg2, arg3);
    }

    /**
     * @see LexicalHandler#endCDATA()
     */
    public void endCDATA() throws SAXException
    {
    }

    /**
     * @see LexicalHandler#endDTD()
     */
    public void endDTD() throws SAXException
    {
    }

    /**
     * @see LexicalHandler#startCDATA()
     */
    public void startCDATA() throws SAXException
    {
    }


    /**
     * @see LexicalHandler#startEntity(String)
     */
    public void startEntity(String arg0) throws SAXException
    {
    }


    /**
     * From XSLTC
     * @see ExtendedContentHandler#startElement(String)
     */
    public void startElement(
    String elementNamespaceURI,
    String elementLocalName,
    String elementName) throws SAXException
    {
        super.startElement(elementNamespaceURI, elementLocalName, elementName);
    }

    public void startElement(
    String elementName) throws SAXException
    {
        super.startElement(elementName);
    }


    /**
     * From XSLTC
     * @see ContentHandler#endDocument()
     */
    public void endDocument() throws SAXException {

        flushPending();
        m_saxHandler.endDocument();

        if (m_tracer != null)
            super.fireEndDoc();
    }

    /**
         *
     * @see ExtendedContentHandler#characters(String)
     */
    public void characters(String characters)
    throws SAXException
    {
        final int length = characters.length();
        if (length > m_charsBuff.length)
        {
            m_charsBuff = new char[length*2 + 1];
        }
        characters.getChars(0, length, m_charsBuff, 0);

        m_saxHandler.characters(m_charsBuff, 0, length);

    }
    /**
         * @see ContentHandler#characters(char[], int, int)
     */
    public void characters(char[] characters, int offset, int length)
    throws SAXException
    {

        m_saxHandler.characters(characters, offset, length);

        // time to fire off characters event
                if (m_tracer != null)
            super.fireCharEvent(characters, offset, length);
    }

    /**
     * From XSLTC
     */
    public void addAttribute(String name, String value)
    {
        // do nothing
    }


    public boolean startPrefixMapping(
        String prefix,
        String uri,
        boolean shouldFlush)
        throws SAXException
    {
        // no namespace support for HTML
        return false;
    }


    public void startPrefixMapping(String prefix, String uri)
        throws SAXException
    {
        // no namespace support for HTML
    }


    public void namespaceAfterStartElement(
        final String prefix,
        final String uri)
        throws SAXException
    {
        // no namespace support for HTML
    }

}
