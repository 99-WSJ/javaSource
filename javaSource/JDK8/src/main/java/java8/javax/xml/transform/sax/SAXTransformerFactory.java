/*
 * Copyright (c) 2000, 2005, Oracle and/or its affiliates. All rights reserved.
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

package java8.javax.xml.transform.sax;

import javax.xml.transform.*;
import javax.xml.transform.sax.TemplatesHandler;
import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.XMLFilter;

/**
 * This class extends TransformerFactory to provide SAX-specific
 * factory methods.  It provides two types of ContentHandlers,
 * one for creating Transformers, the other for creating Templates
 * objects.
 *
 * <p>If an application wants to set the ErrorHandler or EntityResolver
 * for an XMLReader used during a transformation, it should use a URIResolver
 * to return the SAXSource which provides (with getXMLReader) a reference to
 * the XMLReader.</p>
 */
public abstract class SAXTransformerFactory extends TransformerFactory {

    /** If {@link TransformerFactory#getFeature}
     * returns true when passed this value as an argument,
     * the TransformerFactory returned from
     * {@link TransformerFactory#newInstance} may
     * be safely cast to a SAXTransformerFactory.
     */
    public static final String FEATURE =
        "http://javax.xml.transform.sax.SAXTransformerFactory/feature";

    /** If {@link TransformerFactory#getFeature}
     * returns true when passed this value as an argument,
     * the {@link #newXMLFilter(Source src)}
     * and {@link #newXMLFilter(Templates templates)} methods are supported.
     */
    public static final String FEATURE_XMLFILTER =
        "http://javax.xml.transform.sax.SAXTransformerFactory/feature/xmlfilter";

    /**
     * The default constructor is protected on purpose.
     */
    protected SAXTransformerFactory() {}

    /**
     * Get a TransformerHandler object that can process SAX
     * ContentHandler events into a Result, based on the transformation
     * instructions specified by the argument.
     *
     * @param src The Source of the transformation instructions.
     *
     * @return TransformerHandler ready to transform SAX events.
     *
     * @throws TransformerConfigurationException If for some reason the
     * TransformerHandler can not be created.
     */
    public abstract TransformerHandler newTransformerHandler(Source src)
        throws TransformerConfigurationException;

    /**
     * Get a TransformerHandler object that can process SAX
     * ContentHandler events into a Result, based on the Templates argument.
     *
     * @param templates The compiled transformation instructions.
     *
     * @return TransformerHandler ready to transform SAX events.
     *
     * @throws TransformerConfigurationException If for some reason the
     * TransformerHandler can not be created.
     */
    public abstract TransformerHandler newTransformerHandler(
        Templates templates) throws TransformerConfigurationException;

    /**
     * Get a TransformerHandler object that can process SAX
     * ContentHandler events into a Result. The transformation
     * is defined as an identity (or copy) transformation, for example
     * to copy a series of SAX parse events into a DOM tree.
     *
     * @return A non-null reference to a TransformerHandler, that may
     * be used as a ContentHandler for SAX parse events.
     *
     * @throws TransformerConfigurationException If for some reason the
     * TransformerHandler cannot be created.
     */
    public abstract TransformerHandler newTransformerHandler()
        throws TransformerConfigurationException;

    /**
     * Get a TemplatesHandler object that can process SAX
     * ContentHandler events into a Templates object.
     *
     * @return A non-null reference to a TransformerHandler, that may
     * be used as a ContentHandler for SAX parse events.
     *
     * @throws TransformerConfigurationException If for some reason the
     * TemplatesHandler cannot be created.
     */
    public abstract TemplatesHandler newTemplatesHandler()
        throws TransformerConfigurationException;

    /**
     * Create an XMLFilter that uses the given Source as the
     * transformation instructions.
     *
     * @param src The Source of the transformation instructions.
     *
     * @return An XMLFilter object, or null if this feature is not supported.
     *
     * @throws TransformerConfigurationException If for some reason the
     * TemplatesHandler cannot be created.
     */
    public abstract XMLFilter newXMLFilter(Source src)
        throws TransformerConfigurationException;

    /**
     * Create an XMLFilter, based on the Templates argument..
     *
     * @param templates The compiled transformation instructions.
     *
     * @return An XMLFilter object, or null if this feature is not supported.
     *
     * @throws TransformerConfigurationException If for some reason the
     * TemplatesHandler cannot be created.
     */
    public abstract XMLFilter newXMLFilter(Templates templates)
        throws TransformerConfigurationException;
}
