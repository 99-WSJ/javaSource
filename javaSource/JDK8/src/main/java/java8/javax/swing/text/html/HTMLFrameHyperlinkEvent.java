/*
 * Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
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
package java8.javax.swing.text.html;

import java.awt.event.InputEvent;
import javax.swing.text.*;
import javax.swing.event.HyperlinkEvent;
import java.net.URL;

/**
 * HTMLFrameHyperlinkEvent is used to notify interested
 * parties that link was activated in a frame.
 *
 * @author Sunita Mani
 */

public class HTMLFrameHyperlinkEvent extends HyperlinkEvent {

    /**
     * Creates a new object representing a html frame
     * hypertext link event.
     *
     * @param source the object responsible for the event
     * @param type the event type
     * @param targetURL the affected URL
     * @param targetFrame the Frame to display the document in
     */
    public HTMLFrameHyperlinkEvent(Object source, EventType type, URL targetURL,
                                   String targetFrame) {
        super(source, type, targetURL);
        this.targetFrame = targetFrame;
    }


    /**
     * Creates a new object representing a hypertext link event.
     *
     * @param source the object responsible for the event
     * @param type the event type
     * @param targetURL the affected URL
     * @param desc a description
     * @param targetFrame the Frame to display the document in
     */
    public HTMLFrameHyperlinkEvent(Object source, EventType type, URL targetURL, String desc,
                                   String targetFrame) {
        super(source, type, targetURL, desc);
        this.targetFrame = targetFrame;
    }

    /**
     * Creates a new object representing a hypertext link event.
     *
     * @param source the object responsible for the event
     * @param type the event type
     * @param targetURL the affected URL
     * @param sourceElement the element that corresponds to the source
     *                      of the event
     * @param targetFrame the Frame to display the document in
     */
    public HTMLFrameHyperlinkEvent(Object source, EventType type, URL targetURL,
                                   Element sourceElement, String targetFrame) {
        super(source, type, targetURL, null, sourceElement);
        this.targetFrame = targetFrame;
    }


    /**
     * Creates a new object representing a hypertext link event.
     *
     * @param source the object responsible for the event
     * @param type the event type
     * @param targetURL the affected URL
     * @param desc a description
     * @param sourceElement the element that corresponds to the source
     *                      of the event
     * @param targetFrame the Frame to display the document in
     */
    public HTMLFrameHyperlinkEvent(Object source, EventType type, URL targetURL, String desc,
                                   Element sourceElement, String targetFrame) {
        super(source, type, targetURL, desc, sourceElement);
        this.targetFrame = targetFrame;
    }

    /**
     * Creates a new object representing a hypertext link event.
     *
     * @param source the object responsible for the event
     * @param type the event type
     * @param targetURL the affected URL
     * @param desc a description
     * @param sourceElement the element that corresponds to the source
     *                      of the event
     * @param inputEvent  InputEvent that triggered the hyperlink event
     * @param targetFrame the Frame to display the document in
     * @since 1.7
     */
    public HTMLFrameHyperlinkEvent(Object source, EventType type, URL targetURL,
                                   String desc, Element sourceElement,
                                   InputEvent inputEvent, String targetFrame) {
        super(source, type, targetURL, desc, sourceElement, inputEvent);
        this.targetFrame = targetFrame;
    }

    /**
     * returns the target for the link.
     */
    public String getTarget() {
        return targetFrame;
    }

    private String targetFrame;
}
