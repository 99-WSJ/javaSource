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

package java8.javax.xml.transform.stax;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.Source;

/**
 * <p>Acts as a holder for an XML {@link Source} in the
 * form of a StAX reader,i.e.
 * {@link XMLStreamReader} or {@link XMLEventReader}.
 * <code>StAXSource</code> can be used in all cases that accept
 * a <code>Source</code>, e.g. {@link javax.xml.transform.Transformer},
 * {@link javax.xml.validation.Validator} which accept
 * <code>Source</code> as input.
 *
 * <p><code>StAXSource</code>s are consumed during processing
 * and are not reusable.</p>
 *
 * @author <a href="mailto:Neeraj.Bajaj@Sun.com">Neeraj Bajaj</a>
 * @author <a href="mailto:Jeff.Suttor@Sun.com">Jeff Suttor</a>
 *
 * @see <a href="http://jcp.org/en/jsr/detail?id=173">
 *  JSR 173: Streaming API for XML</a>
 * @see XMLStreamReader
 * @see XMLEventReader
 *
 * @since 1.6
 */
public class StAXSource implements Source {

    /** If {@link javax.xml.transform.TransformerFactory#getFeature(String name)}
     * returns true when passed this value as an argument,
     * the Transformer supports Source input of this type.
     */
    public static final String FEATURE =
        "http://javax.xml.transform.stax.StAXSource/feature";

    /** <p><code>XMLEventReader</code> to be used for source input.</p> */
    private XMLEventReader xmlEventReader = null;

    /** <p><code>XMLStreamReader</code> to be used for source input.</p> */
    private XMLStreamReader xmlStreamReader = null;

    /** <p>System identifier of source input.</p> */
    private String systemId = null;

    /**
     * <p>Creates a new instance of a <code>StAXSource</code>
     * by supplying an {@link XMLEventReader}.</p>
     *
     * <p><code>XMLEventReader</code> must be a
     * non-<code>null</code> reference.</p>
     *
     * <p><code>XMLEventReader</code> must be in
     * {@link XMLStreamConstants#START_DOCUMENT} or
     * {@link XMLStreamConstants#START_ELEMENT} state.</p>
     *
     * @param xmlEventReader <code>XMLEventReader</code> used to create
     *   this <code>StAXSource</code>.
     *
     * @throws XMLStreamException If <code>xmlEventReader</code> access
     *   throws an <code>Exception</code>.
     * @throws IllegalArgumentException If <code>xmlEventReader</code> ==
     *   <code>null</code>.
     * @throws IllegalStateException If <code>xmlEventReader</code>
     *   is not in <code>XMLStreamConstants.START_DOCUMENT</code> or
     *   <code>XMLStreamConstants.START_ELEMENT</code> state.
     */
    public StAXSource(final XMLEventReader xmlEventReader)
        throws XMLStreamException {

        if (xmlEventReader == null) {
            throw new IllegalArgumentException(
                    "StAXSource(XMLEventReader) with XMLEventReader == null");
        }

        // TODO: This is ugly ...
        // there is no way to know the current position(event) of
        // XMLEventReader.  peek() is the only way to know the next event.
        // The next event on the input stream should be
        // XMLStreamConstants.START_DOCUMENT or
        // XMLStreamConstants.START_ELEMENT.
        XMLEvent event = xmlEventReader.peek();
        int eventType = event.getEventType();
        if (eventType != XMLStreamConstants.START_DOCUMENT
                && eventType != XMLStreamConstants.START_ELEMENT) {
            throw new IllegalStateException(
                "StAXSource(XMLEventReader) with XMLEventReader "
                + "not in XMLStreamConstants.START_DOCUMENT or "
                + "XMLStreamConstants.START_ELEMENT state");
        }

        this.xmlEventReader = xmlEventReader;
        systemId = event.getLocation().getSystemId();
    }

    /**
     * <p>Creates a new instance of a <code>StAXSource</code>
     * by supplying an {@link XMLStreamReader}.</p>
     *
     * <p><code>XMLStreamReader</code> must be a
     * non-<code>null</code> reference.</p>
     *
     * <p><code>XMLStreamReader</code> must be in
     * {@link XMLStreamConstants#START_DOCUMENT} or
     * {@link XMLStreamConstants#START_ELEMENT} state.</p>
     *
     * @param xmlStreamReader <code>XMLStreamReader</code> used to create
     *   this <code>StAXSource</code>.
     *
     * @throws IllegalArgumentException If <code>xmlStreamReader</code> ==
     *   <code>null</code>.
     * @throws IllegalStateException If <code>xmlStreamReader</code>
     *   is not in <code>XMLStreamConstants.START_DOCUMENT</code> or
     *   <code>XMLStreamConstants.START_ELEMENT</code> state.
     */
    public StAXSource(final XMLStreamReader xmlStreamReader) {

        if (xmlStreamReader == null) {
            throw new IllegalArgumentException(
                    "StAXSource(XMLStreamReader) with XMLStreamReader == null");
        }

        int eventType = xmlStreamReader.getEventType();
        if (eventType != XMLStreamConstants.START_DOCUMENT
                && eventType != XMLStreamConstants.START_ELEMENT) {
            throw new IllegalStateException(
                    "StAXSource(XMLStreamReader) with XMLStreamReader"
                    + "not in XMLStreamConstants.START_DOCUMENT or "
                    + "XMLStreamConstants.START_ELEMENT state");
        }

        this.xmlStreamReader = xmlStreamReader;
        systemId = xmlStreamReader.getLocation().getSystemId();
    }

    /**
     * <p>Get the <code>XMLEventReader</code> used by this
     * <code>StAXSource</code>.</p>
     *
     * <p><code>XMLEventReader</code> will be <code>null</code>.
     * if this <code>StAXSource</code> was created with a
     * <code>XMLStreamReader</code>.</p>
     *
     * @return <code>XMLEventReader</code> used by this
     *   <code>StAXSource</code>.
     */
    public XMLEventReader getXMLEventReader() {

        return xmlEventReader;
    }

    /**
     * <p>Get the <code>XMLStreamReader</code> used by this
     * <code>StAXSource</code>.</p>
     *
     * <p><code>XMLStreamReader</code> will be <code>null</code>
     * if this <code>StAXSource</code> was created with a
     * <code>XMLEventReader</code>.</p>
     *
     * @return <code>XMLStreamReader</code> used by this
     *   <code>StAXSource</code>.
     */
    public XMLStreamReader getXMLStreamReader() {

        return xmlStreamReader;
    }

    /**
     * <p>In the context of a <code>StAXSource</code>, it is not appropriate
     * to explicitly set the system identifier.
     * The <code>XMLStreamReader</code> or <code>XMLEventReader</code>
     * used to construct this <code>StAXSource</code> determines the
     * system identifier of the XML source.</p>
     *
     * <p>An {@link UnsupportedOperationException} is <strong>always</strong>
     * thrown by this method.</p>
     *
     * @param systemId Ignored.
     *
     * @throws UnsupportedOperationException Is <strong>always</strong>
     *   thrown by this method.
     */
    public void setSystemId(final String systemId) {

        throw new UnsupportedOperationException(
                "StAXSource#setSystemId(systemId) cannot set the "
                + "system identifier for a StAXSource");
    }

    /**
     * <p>Get the system identifier used by this
     * <code>StAXSource</code>.</p>
     *
     * <p>The <code>XMLStreamReader</code> or <code>XMLEventReader</code>
     * used to construct this <code>StAXSource</code> is queried to determine
     * the system identifier of the XML source.</p>
     *
     * <p>The system identifier may be <code>null</code> or
     * an empty <code>""</code> <code>String</code>.</p>
     *
     * @return System identifier used by this <code>StAXSource</code>.
     */
    public String getSystemId() {

        return systemId;
    }
}
