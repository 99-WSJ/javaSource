/*
 * Copyright (c) 2000, 2006, Oracle and/or its affiliates. All rights reserved.
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

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/**
 * <p>Acts as an holder for SAX-style Source.</p>
 *
 * <p>Note that XSLT requires namespace support. Attempting to transform an
 * input source that is not
 * generated with a namespace-aware parser may result in errors.
 * Parsers can be made namespace aware by calling the
 * {@link javax.xml.parsers.SAXParserFactory#setNamespaceAware(boolean awareness)} method.</p>
 *
 * @author <a href="mailto:Jeff.Suttor@Sun.com">Jeff Suttor</a>
 */
public class SAXSource implements Source {

    /**
     * If {@link javax.xml.transform.TransformerFactory#getFeature}
     * returns true when passed this value as an argument,
     * the Transformer supports Source input of this type.
     */
    public static final String FEATURE =
        "http://javax.xml.transform.sax.SAXSource/feature";

    /**
     * <p>Zero-argument default constructor.  If this constructor is used, and
     * no SAX source is set using
     * {@link #setInputSource(InputSource inputSource)} , then the
     * <code>Transformer</code> will
     * create an empty source {@link InputSource} using
     * {@link InputSource#InputSource() new InputSource()}.</p>
     *
     * @see javax.xml.transform.Transformer#transform(Source xmlSource, Result outputTarget)
     */
    public SAXSource() { }

    /**
     * Create a <code>SAXSource</code>, using an {@link XMLReader}
     * and a SAX InputSource. The {@link javax.xml.transform.Transformer}
     * or {@link javax.xml.transform.sax.SAXTransformerFactory} will set itself
     * to be the reader's {@link org.xml.sax.ContentHandler}, and then will call
     * reader.parse(inputSource).
     *
     * @param reader An XMLReader to be used for the parse.
     * @param inputSource A SAX input source reference that must be non-null
     * and that will be passed to the reader parse method.
     */
    public SAXSource(XMLReader reader, InputSource inputSource) {
        this.reader      = reader;
        this.inputSource = inputSource;
    }

    /**
     * Create a <code>SAXSource</code>, using a SAX <code>InputSource</code>.
     * The {@link javax.xml.transform.Transformer} or
     * {@link javax.xml.transform.sax.SAXTransformerFactory} creates a
     * reader via {@link org.xml.sax.helpers.XMLReaderFactory}
     * (if setXMLReader is not used), sets itself as
     * the reader's {@link org.xml.sax.ContentHandler}, and calls
     * reader.parse(inputSource).
     *
     * @param inputSource An input source reference that must be non-null
     * and that will be passed to the parse method of the reader.
     */
    public SAXSource(InputSource inputSource) {
        this.inputSource = inputSource;
    }

    /**
     * Set the XMLReader to be used for the Source.
     *
     * @param reader A valid XMLReader or XMLFilter reference.
     */
    public void setXMLReader(XMLReader reader) {
        this.reader = reader;
    }

    /**
     * Get the XMLReader to be used for the Source.
     *
     * @return A valid XMLReader or XMLFilter reference, or null.
     */
    public XMLReader getXMLReader() {
        return reader;
    }

    /**
     * Set the SAX InputSource to be used for the Source.
     *
     * @param inputSource A valid InputSource reference.
     */
    public void setInputSource(InputSource inputSource) {
        this.inputSource = inputSource;
    }

    /**
     * Get the SAX InputSource to be used for the Source.
     *
     * @return A valid InputSource reference, or null.
     */
    public InputSource getInputSource() {
        return inputSource;
    }

    /**
     * Set the system identifier for this Source.  If an input source
     * has already been set, it will set the system ID or that
     * input source, otherwise it will create a new input source.
     *
     * <p>The system identifier is optional if there is a byte stream
     * or a character stream, but it is still useful to provide one,
     * since the application can use it to resolve relative URIs
     * and can include it in error messages and warnings (the parser
     * will attempt to open a connection to the URI only if
     * no byte stream or character stream is specified).</p>
     *
     * @param systemId The system identifier as a URI string.
     */
    public void setSystemId(String systemId) {

        if (null == inputSource) {
            inputSource = new InputSource(systemId);
        } else {
            inputSource.setSystemId(systemId);
        }
    }

    /**
     * <p>Get the base ID (URI or system ID) from where URIs
     * will be resolved.</p>
     *
     * @return Base URL for the <code>Source</code>, or <code>null</code>.
     */
    public String getSystemId() {

        if (inputSource == null) {
            return null;
        } else {
            return inputSource.getSystemId();
        }
    }

    /**
     * The XMLReader to be used for the source tree input. May be null.
     */
    private XMLReader reader;

    /**
     * <p>The SAX InputSource to be used for the source tree input.
     * Should not be <code>null</code>.</p>
     */
    private InputSource inputSource;

    /**
     * Attempt to obtain a SAX InputSource object from a Source
     * object.
     *
     * @param source Must be a non-null Source reference.
     *
     * @return An InputSource, or null if Source can not be converted.
     */
    public static InputSource sourceToInputSource(Source source) {

        if (source instanceof javax.xml.transform.sax.SAXSource) {
            return ((javax.xml.transform.sax.SAXSource) source).getInputSource();
        } else if (source instanceof StreamSource) {
            StreamSource ss      = (StreamSource) source;
            InputSource  isource = new InputSource(ss.getSystemId());

            isource.setByteStream(ss.getInputStream());
            isource.setCharacterStream(ss.getReader());
            isource.setPublicId(ss.getPublicId());

            return isource;
        } else {
            return null;
        }
    }
}
