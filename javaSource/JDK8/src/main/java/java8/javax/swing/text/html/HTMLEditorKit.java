/*
 * Copyright (c) 1997, 2013, Oracle and/or its affiliates. All rights reserved.
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

import sun.awt.AppContext;

import java.lang.reflect.Method;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.text.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.plaf.TextUI;
import java.util.*;
import javax.accessibility.*;
import javax.swing.text.html.AccessibleHTML;
import javax.swing.text.html.BRView;
import javax.swing.text.html.BlockView;
import javax.swing.text.html.CSS;
import javax.swing.text.html.CommentView;
import javax.swing.text.html.FormSubmitEvent;
import javax.swing.text.html.FormView;
import javax.swing.text.html.FrameSetView;
import javax.swing.text.html.FrameView;
import javax.swing.text.html.HRuleView;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;
import javax.swing.text.html.HTMLWriter;
import javax.swing.text.html.HiddenTagView;
import javax.swing.text.html.ImageView;
import javax.swing.text.html.InlineView;
import javax.swing.text.html.IsindexView;
import javax.swing.text.html.LineView;
import javax.swing.text.html.ListView;
import javax.swing.text.html.Map;
import javax.swing.text.html.MinimalHTMLWriter;
import javax.swing.text.html.NoFramesView;
import javax.swing.text.html.ObjectView;
import javax.swing.text.html.ParagraphView;
import javax.swing.text.html.ResourceLoader;
import javax.swing.text.html.StyleSheet;
import java.lang.ref.*;

/**
 * The Swing JEditorPane text component supports different kinds
 * of content via a plug-in mechanism called an EditorKit.  Because
 * HTML is a very popular format of content, some support is provided
 * by default.  The default support is provided by this class, which
 * supports HTML version 3.2 (with some extensions), and is migrating
 * toward version 4.0.
 * The &lt;applet&gt; tag is not supported, but some support is provided
 * for the &lt;object&gt; tag.
 * <p>
 * There are several goals of the HTML EditorKit provided, that have
 * an effect upon the way that HTML is modeled.  These
 * have influenced its design in a substantial way.
 * <dl>
 * <dt>
 * Support editing
 * <dd>
 * It might seem fairly obvious that a plug-in for JEditorPane
 * should provide editing support, but that fact has several
 * design considerations.  There are a substantial number of HTML
 * documents that don't properly conform to an HTML specification.
 * These must be normalized somewhat into a correct form if one
 * is to edit them.  Additionally, users don't like to be presented
 * with an excessive amount of structure editing, so using traditional
 * text editing gestures is preferred over using the HTML structure
 * exactly as defined in the HTML document.
 * <p>
 * The modeling of HTML is provided by the class <code>HTMLDocument</code>.
 * Its documentation describes the details of how the HTML is modeled.
 * The editing support leverages heavily off of the text package.
 *
 * <dt>
 * Extendable/Scalable
 * <dd>
 * To maximize the usefulness of this kit, a great deal of effort
 * has gone into making it extendable.  These are some of the
 * features.
 * <ol>
 *   <li>
 *   The parser is replaceable.  The default parser is the Hot Java
 *   parser which is DTD based.  A different DTD can be used, or an
 *   entirely different parser can be used.  To change the parser,
 *   reimplement the getParser method.  The default parser is
 *   dynamically loaded when first asked for, so the class files
 *   will never be loaded if an alternative parser is used.  The
 *   default parser is in a separate package called parser below
 *   this package.
 *   <li>
 *   The parser drives the ParserCallback, which is provided by
 *   HTMLDocument.  To change the callback, subclass HTMLDocument
 *   and reimplement the createDefaultDocument method to return
 *   document that produces a different reader.  The reader controls
 *   how the document is structured.  Although the Document provides
 *   HTML support by default, there is nothing preventing support of
 *   non-HTML tags that result in alternative element structures.
 *   <li>
 *   The default view of the models are provided as a hierarchy of
 *   View implementations, so one can easily customize how a particular
 *   element is displayed or add capabilities for new kinds of elements
 *   by providing new View implementations.  The default set of views
 *   are provided by the <code>HTMLFactory</code> class.  This can
 *   be easily changed by subclassing or replacing the HTMLFactory
 *   and reimplementing the getViewFactory method to return the alternative
 *   factory.
 *   <li>
 *   The View implementations work primarily off of CSS attributes,
 *   which are kept in the views.  This makes it possible to have
 *   multiple views mapped over the same model that appear substantially
 *   different.  This can be especially useful for printing.  For
 *   most HTML attributes, the HTML attributes are converted to CSS
 *   attributes for display.  This helps make the View implementations
 *   more general purpose
 * </ol>
 *
 * <dt>
 * Asynchronous Loading
 * <dd>
 * Larger documents involve a lot of parsing and take some time
 * to load.  By default, this kit produces documents that will be
 * loaded asynchronously if loaded using <code>JEditorPane.setPage</code>.
 * This is controlled by a property on the document.  The method
 * {@link #createDefaultDocument createDefaultDocument} can
 * be overriden to change this.  The batching of work is done
 * by the <code>HTMLDocument.HTMLReader</code> class.  The actual
 * work is done by the <code>DefaultStyledDocument</code> and
 * <code>AbstractDocument</code> classes in the text package.
 *
 * <dt>
 * Customization from current LAF
 * <dd>
 * HTML provides a well known set of features without exactly
 * specifying the display characteristics.  Swing has a theme
 * mechanism for its look-and-feel implementations.  It is desirable
 * for the look-and-feel to feed display characteristics into the
 * HTML views.  An user with poor vision for example would want
 * high contrast and larger than typical fonts.
 * <p>
 * The support for this is provided by the <code>StyleSheet</code>
 * class.  The presentation of the HTML can be heavily influenced
 * by the setting of the StyleSheet property on the EditorKit.
 *
 * <dt>
 * Not lossy
 * <dd>
 * An EditorKit has the ability to be read and save documents.
 * It is generally the most pleasing to users if there is no loss
 * of data between the two operation.  The policy of the HTMLEditorKit
 * will be to store things not recognized or not necessarily visible
 * so they can be subsequently written out.  The model of the HTML document
 * should therefore contain all information discovered while reading the
 * document.  This is constrained in some ways by the need to support
 * editing (i.e. incorrect documents sometimes must be normalized).
 * The guiding principle is that information shouldn't be lost, but
 * some might be synthesized to produce a more correct model or it might
 * be rearranged.
 * </dl>
 *
 * @author  Timothy Prinzing
 */
public class HTMLEditorKit extends StyledEditorKit implements Accessible {

    private JEditorPane theEditor;

    /**
     * Constructs an HTMLEditorKit, creates a StyleContext,
     * and loads the style sheet.
     */
    public HTMLEditorKit() {

    }

    /**
     * Get the MIME type of the data that this
     * kit represents support for.  This kit supports
     * the type <code>text/html</code>.
     *
     * @return the type
     */
    public String getContentType() {
        return "text/html";
    }

    /**
     * Fetch a factory that is suitable for producing
     * views of any models that are produced by this
     * kit.
     *
     * @return the factory
     */
    public ViewFactory getViewFactory() {
        return defaultFactory;
    }

    /**
     * Create an uninitialized text storage model
     * that is appropriate for this type of editor.
     *
     * @return the model
     */
    public Document createDefaultDocument() {
        StyleSheet styles = getStyleSheet();
        StyleSheet ss = new StyleSheet();

        ss.addStyleSheet(styles);

        HTMLDocument doc = new HTMLDocument(ss);
        doc.setParser(getParser());
        doc.setAsynchronousLoadPriority(4);
        doc.setTokenThreshold(100);
        return doc;
    }

    /**
     * Try to get an HTML parser from the document.  If no parser is set for
     * the document, return the editor kit's default parser.  It is an error
     * if no parser could be obtained from the editor kit.
     */
    private Parser ensureParser(HTMLDocument doc) throws IOException {
        Parser p = doc.getParser();
        if (p == null) {
            p = getParser();
        }
        if (p == null) {
            throw new IOException("Can't load parser");
        }
        return p;
    }

    /**
     * Inserts content from the given stream. If <code>doc</code> is
     * an instance of HTMLDocument, this will read
     * HTML 3.2 text. Inserting HTML into a non-empty document must be inside
     * the body Element, if you do not insert into the body an exception will
     * be thrown. When inserting into a non-empty document all tags outside
     * of the body (head, title) will be dropped.
     *
     * @param in  the stream to read from
     * @param doc the destination for the insertion
     * @param pos the location in the document to place the
     *   content
     * @exception IOException on any I/O error
     * @exception BadLocationException if pos represents an invalid
     *   location within the document
     * @exception RuntimeException (will eventually be a BadLocationException)
     *            if pos is invalid
     */
    public void read(Reader in, Document doc, int pos) throws IOException, BadLocationException {

        if (doc instanceof HTMLDocument) {
            HTMLDocument hdoc = (HTMLDocument) doc;
            if (pos > doc.getLength()) {
                throw new BadLocationException("Invalid location", pos);
            }

            Parser p = ensureParser(hdoc);
            ParserCallback receiver = hdoc.getReader(pos);
            Boolean ignoreCharset = (Boolean)doc.getProperty("IgnoreCharsetDirective");
            p.parse(in, receiver, (ignoreCharset == null) ? false : ignoreCharset.booleanValue());
            receiver.flush();
        } else {
            super.read(in, doc, pos);
        }
    }

    /**
     * Inserts HTML into an existing document.
     *
     * @param doc       the document to insert into
     * @param offset    the offset to insert HTML at
     * @param popDepth  the number of ElementSpec.EndTagTypes to generate before
     *        inserting
     * @param pushDepth the number of ElementSpec.StartTagTypes with a direction
     *        of ElementSpec.JoinNextDirection that should be generated
     *        before inserting, but after the end tags have been generated
     * @param insertTag the first tag to start inserting into document
     * @exception RuntimeException (will eventually be a BadLocationException)
     *            if pos is invalid
     */
    public void insertHTML(HTMLDocument doc, int offset, String html,
                           int popDepth, int pushDepth,
                           HTML.Tag insertTag) throws
                       BadLocationException, IOException {
        if (offset > doc.getLength()) {
            throw new BadLocationException("Invalid location", offset);
        }

        Parser p = ensureParser(doc);
        ParserCallback receiver = doc.getReader(offset, popDepth, pushDepth,
                                                insertTag);
        Boolean ignoreCharset = (Boolean)doc.getProperty
                                ("IgnoreCharsetDirective");
        p.parse(new StringReader(html), receiver, (ignoreCharset == null) ?
                false : ignoreCharset.booleanValue());
        receiver.flush();
    }

    /**
     * Write content from a document to the given stream
     * in a format appropriate for this kind of content handler.
     *
     * @param out  the stream to write to
     * @param doc  the source for the write
     * @param pos  the location in the document to fetch the
     *   content
     * @param len  the amount to write out
     * @exception IOException on any I/O error
     * @exception BadLocationException if pos represents an invalid
     *   location within the document
     */
    public void write(Writer out, Document doc, int pos, int len)
        throws IOException, BadLocationException {

        if (doc instanceof HTMLDocument) {
            HTMLWriter w = new HTMLWriter(out, (HTMLDocument)doc, pos, len);
            w.write();
        } else if (doc instanceof StyledDocument) {
            MinimalHTMLWriter w = new MinimalHTMLWriter(out, (StyledDocument)doc, pos, len);
            w.write();
        } else {
            super.write(out, doc, pos, len);
        }
    }

    /**
     * Called when the kit is being installed into the
     * a JEditorPane.
     *
     * @param c the JEditorPane
     */
    public void install(JEditorPane c) {
        c.addMouseListener(linkHandler);
        c.addMouseMotionListener(linkHandler);
        c.addCaretListener(nextLinkAction);
        super.install(c);
        theEditor = c;
    }

    /**
     * Called when the kit is being removed from the
     * JEditorPane.  This is used to unregister any
     * listeners that were attached.
     *
     * @param c the JEditorPane
     */
    public void deinstall(JEditorPane c) {
        c.removeMouseListener(linkHandler);
        c.removeMouseMotionListener(linkHandler);
        c.removeCaretListener(nextLinkAction);
        super.deinstall(c);
        theEditor = null;
    }

    /**
     * Default Cascading Style Sheet file that sets
     * up the tag views.
     */
    public static final String DEFAULT_CSS = "default.css";

    /**
     * Set the set of styles to be used to render the various
     * HTML elements.  These styles are specified in terms of
     * CSS specifications.  Each document produced by the kit
     * will have a copy of the sheet which it can add the
     * document specific styles to.  By default, the StyleSheet
     * specified is shared by all HTMLEditorKit instances.
     * This should be reimplemented to provide a finer granularity
     * if desired.
     */
    public void setStyleSheet(StyleSheet s) {
        if (s == null) {
            AppContext.getAppContext().remove(DEFAULT_STYLES_KEY);
        } else {
            AppContext.getAppContext().put(DEFAULT_STYLES_KEY, s);
        }
    }

    /**
     * Get the set of styles currently being used to render the
     * HTML elements.  By default the resource specified by
     * DEFAULT_CSS gets loaded, and is shared by all HTMLEditorKit
     * instances.
     */
    public StyleSheet getStyleSheet() {
        AppContext appContext = AppContext.getAppContext();
        StyleSheet defaultStyles = (StyleSheet) appContext.get(DEFAULT_STYLES_KEY);

        if (defaultStyles == null) {
            defaultStyles = new StyleSheet();
            appContext.put(DEFAULT_STYLES_KEY, defaultStyles);
            try {
                InputStream is = javax.swing.text.html.HTMLEditorKit.getResourceAsStream(DEFAULT_CSS);
                Reader r = new BufferedReader(
                        new InputStreamReader(is, "ISO-8859-1"));
                defaultStyles.loadRules(r, null);
                r.close();
            } catch (Throwable e) {
                // on error we simply have no styles... the html
                // will look mighty wrong but still function.
            }
        }
        return defaultStyles;
    }

    /**
     * Fetch a resource relative to the HTMLEditorKit classfile.
     * If this is called on 1.2 the loading will occur under the
     * protection of a doPrivileged call to allow the HTMLEditorKit
     * to function when used in an applet.
     *
     * @param name the name of the resource, relative to the
     *  HTMLEditorKit class
     * @return a stream representing the resource
     */
    static InputStream getResourceAsStream(String name) {
        try {
            return javax.swing.text.html.ResourceLoader.getResourceAsStream(name);
        } catch (Throwable e) {
            // If the class doesn't exist or we have some other
            // problem we just try to call getResourceAsStream directly.
            return javax.swing.text.html.HTMLEditorKit.class.getResourceAsStream(name);
        }
    }

    /**
     * Fetches the command list for the editor.  This is
     * the list of commands supported by the superclass
     * augmented by the collection of commands defined
     * locally for style operations.
     *
     * @return the command list
     */
    public Action[] getActions() {
        return TextAction.augmentList(super.getActions(), this.defaultActions);
    }

    /**
     * Copies the key/values in <code>element</code>s AttributeSet into
     * <code>set</code>. This does not copy component, icon, or element
     * names attributes. Subclasses may wish to refine what is and what
     * isn't copied here. But be sure to first remove all the attributes that
     * are in <code>set</code>.<p>
     * This is called anytime the caret moves over a different location.
     *
     */
    protected void createInputAttributes(Element element,
                                         MutableAttributeSet set) {
        set.removeAttributes(set);
        set.addAttributes(element.getAttributes());
        set.removeAttribute(StyleConstants.ComposedTextAttribute);

        Object o = set.getAttribute(StyleConstants.NameAttribute);
        if (o instanceof HTML.Tag) {
            HTML.Tag tag = (HTML.Tag)o;
            // PENDING: we need a better way to express what shouldn't be
            // copied when editing...
            if(tag == HTML.Tag.IMG) {
                // Remove the related image attributes, src, width, height
                set.removeAttribute(HTML.Attribute.SRC);
                set.removeAttribute(HTML.Attribute.HEIGHT);
                set.removeAttribute(HTML.Attribute.WIDTH);
                set.addAttribute(StyleConstants.NameAttribute,
                                 HTML.Tag.CONTENT);
            }
            else if (tag == HTML.Tag.HR || tag == HTML.Tag.BR) {
                // Don't copy HRs or BRs either.
                set.addAttribute(StyleConstants.NameAttribute,
                                 HTML.Tag.CONTENT);
            }
            else if (tag == HTML.Tag.COMMENT) {
                // Don't copy COMMENTs either
                set.addAttribute(StyleConstants.NameAttribute,
                                 HTML.Tag.CONTENT);
                set.removeAttribute(HTML.Attribute.COMMENT);
            }
            else if (tag == HTML.Tag.INPUT) {
                // or INPUT either
                set.addAttribute(StyleConstants.NameAttribute,
                                 HTML.Tag.CONTENT);
                set.removeAttribute(HTML.Tag.INPUT);
            }
            else if (tag instanceof HTML.UnknownTag) {
                // Don't copy unknowns either:(
                set.addAttribute(StyleConstants.NameAttribute,
                                 HTML.Tag.CONTENT);
                set.removeAttribute(HTML.Attribute.ENDTAG);
            }
        }
    }

    /**
     * Gets the input attributes used for the styled
     * editing actions.
     *
     * @return the attribute set
     */
    public MutableAttributeSet getInputAttributes() {
        if (input == null) {
            input = getStyleSheet().addStyle(null, null);
        }
        return input;
    }

    /**
     * Sets the default cursor.
     *
     * @since 1.3
     */
    public void setDefaultCursor(Cursor cursor) {
        defaultCursor = cursor;
    }

    /**
     * Returns the default cursor.
     *
     * @since 1.3
     */
    public Cursor getDefaultCursor() {
        return defaultCursor;
    }

    /**
     * Sets the cursor to use over links.
     *
     * @since 1.3
     */
    public void setLinkCursor(Cursor cursor) {
        linkCursor = cursor;
    }

    /**
     * Returns the cursor to use over hyper links.
     * @since 1.3
     */
    public Cursor getLinkCursor() {
        return linkCursor;
    }

    /**
     * Indicates whether an html form submission is processed automatically
     * or only <code>FormSubmitEvent</code> is fired.
     *
     * @return true  if html form submission is processed automatically,
     *         false otherwise.
     *
     * @see #setAutoFormSubmission
     * @since 1.5
     */
    public boolean isAutoFormSubmission() {
        return isAutoFormSubmission;
    }

    /**
     * Specifies if an html form submission is processed
     * automatically or only <code>FormSubmitEvent</code> is fired.
     * By default it is set to true.
     *
     * @see #isAutoFormSubmission()
     * @see FormSubmitEvent
     * @since 1.5
     */
    public void setAutoFormSubmission(boolean isAuto) {
        isAutoFormSubmission = isAuto;
    }

    /**
     * Creates a copy of the editor kit.
     *
     * @return the copy
     */
    public Object clone() {
        javax.swing.text.html.HTMLEditorKit o = (javax.swing.text.html.HTMLEditorKit)super.clone();
        if (o != null) {
            o.input = null;
            o.linkHandler = new LinkController();
        }
        return o;
    }

    /**
     * Fetch the parser to use for reading HTML streams.
     * This can be reimplemented to provide a different
     * parser.  The default implementation is loaded dynamically
     * to avoid the overhead of loading the default parser if
     * it's not used.  The default parser is the HotJava parser
     * using an HTML 3.2 DTD.
     */
    protected Parser getParser() {
        if (defaultParser == null) {
            try {
                Class c = Class.forName("javax.swing.text.html.parser.ParserDelegator");
                defaultParser = (Parser) c.newInstance();
            } catch (Throwable e) {
            }
        }
        return defaultParser;
    }

    // ----- Accessibility support -----
    private AccessibleContext accessibleContext;

    /**
     * returns the AccessibleContext associated with this editor kit
     *
     * @return the AccessibleContext associated with this editor kit
     * @since 1.4
     */
    public AccessibleContext getAccessibleContext() {
        if (theEditor == null) {
            return null;
        }
        if (accessibleContext == null) {
            javax.swing.text.html.AccessibleHTML a = new javax.swing.text.html.AccessibleHTML(theEditor);
            accessibleContext = a.getAccessibleContext();
        }
        return accessibleContext;
    }

    // --- variables ------------------------------------------

    private static final Cursor MoveCursor = Cursor.getPredefinedCursor
                                    (Cursor.HAND_CURSOR);
    private static final Cursor DefaultCursor = Cursor.getPredefinedCursor
                                    (Cursor.DEFAULT_CURSOR);

    /** Shared factory for creating HTML Views. */
    private static final ViewFactory defaultFactory = new HTMLFactory();

    MutableAttributeSet input;
    private static final Object DEFAULT_STYLES_KEY = new Object();
    private LinkController linkHandler = new LinkController();
    private static Parser defaultParser = null;
    private Cursor defaultCursor = DefaultCursor;
    private Cursor linkCursor = MoveCursor;
    private boolean isAutoFormSubmission = true;

    /**
     * Class to watch the associated component and fire
     * hyperlink events on it when appropriate.
     */
    public static class LinkController extends MouseAdapter implements MouseMotionListener, Serializable {
        private Element curElem = null;
        /**
         * If true, the current element (curElem) represents an image.
         */
        private boolean curElemImage = false;
        private String href = null;
        /** This is used by viewToModel to avoid allocing a new array each
         * time. */
        private transient Position.Bias[] bias = new Position.Bias[1];
        /**
         * Current offset.
         */
        private int curOffset;

        /**
         * Called for a mouse click event.
         * If the component is read-only (ie a browser) then
         * the clicked event is used to drive an attempt to
         * follow the reference specified by a link.
         *
         * @param e the mouse event
         * @see MouseListener#mouseClicked
         */
        public void mouseClicked(MouseEvent e) {
            JEditorPane editor = (JEditorPane) e.getSource();

            if (! editor.isEditable() && editor.isEnabled() &&
                    SwingUtilities.isLeftMouseButton(e)) {
                Point pt = new Point(e.getX(), e.getY());
                int pos = editor.viewToModel(pt);
                if (pos >= 0) {
                    activateLink(pos, editor, e);
                }
            }
        }

        // ignore the drags
        public void mouseDragged(MouseEvent e) {
        }

        // track the moving of the mouse.
        public void mouseMoved(MouseEvent e) {
            JEditorPane editor = (JEditorPane) e.getSource();
            if (!editor.isEnabled()) {
                return;
            }

            javax.swing.text.html.HTMLEditorKit kit = (javax.swing.text.html.HTMLEditorKit)editor.getEditorKit();
            boolean adjustCursor = true;
            Cursor newCursor = kit.getDefaultCursor();
            if (!editor.isEditable()) {
                Point pt = new Point(e.getX(), e.getY());
                int pos = editor.getUI().viewToModel(editor, pt, bias);
                if (bias[0] == Position.Bias.Backward && pos > 0) {
                    pos--;
                }
                if (pos >= 0 &&(editor.getDocument() instanceof HTMLDocument)){
                    HTMLDocument hdoc = (HTMLDocument)editor.getDocument();
                    Element elem = hdoc.getCharacterElement(pos);
                    if (!doesElementContainLocation(editor, elem, pos,
                                                    e.getX(), e.getY())) {
                        elem = null;
                    }
                    if (curElem != elem || curElemImage) {
                        Element lastElem = curElem;
                        curElem = elem;
                        String href = null;
                        curElemImage = false;
                        if (elem != null) {
                            AttributeSet a = elem.getAttributes();
                            AttributeSet anchor = (AttributeSet)a.
                                                   getAttribute(HTML.Tag.A);
                            if (anchor == null) {
                                curElemImage = (a.getAttribute(StyleConstants.
                                            NameAttribute) == HTML.Tag.IMG);
                                if (curElemImage) {
                                    href = getMapHREF(editor, hdoc, elem, a,
                                                      pos, e.getX(), e.getY());
                                }
                            }
                            else {
                                href = (String)anchor.getAttribute
                                    (HTML.Attribute.HREF);
                            }
                        }

                        if (href != this.href) {
                            // reference changed, fire event(s)
                            fireEvents(editor, hdoc, href, lastElem, e);
                            this.href = href;
                            if (href != null) {
                                newCursor = kit.getLinkCursor();
                            }
                        }
                        else {
                            adjustCursor = false;
                        }
                    }
                    else {
                        adjustCursor = false;
                    }
                    curOffset = pos;
                }
            }
            if (adjustCursor && editor.getCursor() != newCursor) {
                editor.setCursor(newCursor);
            }
        }

        /**
         * Returns a string anchor if the passed in element has a
         * USEMAP that contains the passed in location.
         */
        private String getMapHREF(JEditorPane html, HTMLDocument hdoc,
                                  Element elem, AttributeSet attr, int offset,
                                  int x, int y) {
            Object useMap = attr.getAttribute(HTML.Attribute.USEMAP);
            if (useMap != null && (useMap instanceof String)) {
                javax.swing.text.html.Map m = hdoc.getMap((String)useMap);
                if (m != null && offset < hdoc.getLength()) {
                    Rectangle bounds;
                    TextUI ui = html.getUI();
                    try {
                        Shape lBounds = ui.modelToView(html, offset,
                                                   Position.Bias.Forward);
                        Shape rBounds = ui.modelToView(html, offset + 1,
                                                   Position.Bias.Backward);
                        bounds = lBounds.getBounds();
                        bounds.add((rBounds instanceof Rectangle) ?
                                    (Rectangle)rBounds : rBounds.getBounds());
                    } catch (BadLocationException ble) {
                        bounds = null;
                    }
                    if (bounds != null) {
                        AttributeSet area = m.getArea(x - bounds.x,
                                                      y - bounds.y,
                                                      bounds.width,
                                                      bounds.height);
                        if (area != null) {
                            return (String)area.getAttribute(HTML.Attribute.
                                                             HREF);
                        }
                    }
                }
            }
            return null;
        }

        /**
         * Returns true if the View representing <code>e</code> contains
         * the location <code>x</code>, <code>y</code>. <code>offset</code>
         * gives the offset into the Document to check for.
         */
        private boolean doesElementContainLocation(JEditorPane editor,
                                                   Element e, int offset,
                                                   int x, int y) {
            if (e != null && offset > 0 && e.getStartOffset() == offset) {
                try {
                    TextUI ui = editor.getUI();
                    Shape s1 = ui.modelToView(editor, offset,
                                              Position.Bias.Forward);
                    if (s1 == null) {
                        return false;
                    }
                    Rectangle r1 = (s1 instanceof Rectangle) ? (Rectangle)s1 :
                                    s1.getBounds();
                    Shape s2 = ui.modelToView(editor, e.getEndOffset(),
                                              Position.Bias.Backward);
                    if (s2 != null) {
                        Rectangle r2 = (s2 instanceof Rectangle) ? (Rectangle)s2 :
                                    s2.getBounds();
                        r1.add(r2);
                    }
                    return r1.contains(x, y);
                } catch (BadLocationException ble) {
                }
            }
            return true;
        }

        /**
         * Calls linkActivated on the associated JEditorPane
         * if the given position represents a link.<p>This is implemented
         * to forward to the method with the same name, but with the following
         * args both == -1.
         *
         * @param pos the position
         * @param editor the editor pane
         */
        protected void activateLink(int pos, JEditorPane editor) {
            activateLink(pos, editor, null);
        }

        /**
         * Calls linkActivated on the associated JEditorPane
         * if the given position represents a link. If this was the result
         * of a mouse click, <code>x</code> and
         * <code>y</code> will give the location of the mouse, otherwise
         * they will be {@literal <} 0.
         *
         * @param pos the position
         * @param html the editor pane
         */
        void activateLink(int pos, JEditorPane html, MouseEvent mouseEvent) {
            Document doc = html.getDocument();
            if (doc instanceof HTMLDocument) {
                HTMLDocument hdoc = (HTMLDocument) doc;
                Element e = hdoc.getCharacterElement(pos);
                AttributeSet a = e.getAttributes();
                AttributeSet anchor = (AttributeSet)a.getAttribute(HTML.Tag.A);
                HyperlinkEvent linkEvent = null;
                String description;
                int x = -1;
                int y = -1;

                if (mouseEvent != null) {
                    x = mouseEvent.getX();
                    y = mouseEvent.getY();
                }

                if (anchor == null) {
                    href = getMapHREF(html, hdoc, e, a, pos, x, y);
                }
                else {
                    href = (String)anchor.getAttribute(HTML.Attribute.HREF);
                }

                if (href != null) {
                    linkEvent = createHyperlinkEvent(html, hdoc, href, anchor,
                                                     e, mouseEvent);
                }
                if (linkEvent != null) {
                    html.fireHyperlinkUpdate(linkEvent);
                }
            }
        }

        /**
         * Creates and returns a new instance of HyperlinkEvent. If
         * <code>hdoc</code> is a frame document a HTMLFrameHyperlinkEvent
         * will be created.
         */
        HyperlinkEvent createHyperlinkEvent(JEditorPane html,
                                            HTMLDocument hdoc, String href,
                                            AttributeSet anchor,
                                            Element element,
                                            MouseEvent mouseEvent) {
            URL u;
            try {
                URL base = hdoc.getBase();
                u = new URL(base, href);
                // Following is a workaround for 1.2, in which
                // new URL("file://...", "#...") causes the filename to
                // be lost.
                if (href != null && "file".equals(u.getProtocol()) &&
                    href.startsWith("#")) {
                    String baseFile = base.getFile();
                    String newFile = u.getFile();
                    if (baseFile != null && newFile != null &&
                        !newFile.startsWith(baseFile)) {
                        u = new URL(base, baseFile + href);
                    }
                }
            } catch (MalformedURLException m) {
                u = null;
            }
            HyperlinkEvent linkEvent;

            if (!hdoc.isFrameDocument()) {
                linkEvent = new HyperlinkEvent(
                        html, HyperlinkEvent.EventType.ACTIVATED, u, href,
                        element, mouseEvent);
            } else {
                String target = (anchor != null) ?
                    (String)anchor.getAttribute(HTML.Attribute.TARGET) : null;
                if ((target == null) || (target.equals(""))) {
                    target = hdoc.getBaseTarget();
                }
                if ((target == null) || (target.equals(""))) {
                    target = "_self";
                }
                    linkEvent = new HTMLFrameHyperlinkEvent(
                        html, HyperlinkEvent.EventType.ACTIVATED, u, href,
                        element, mouseEvent, target);
            }
            return linkEvent;
        }

        void fireEvents(JEditorPane editor, HTMLDocument doc, String href,
                        Element lastElem, MouseEvent mouseEvent) {
            if (this.href != null) {
                // fire an exited event on the old link
                URL u;
                try {
                    u = new URL(doc.getBase(), this.href);
                } catch (MalformedURLException m) {
                    u = null;
                }
                HyperlinkEvent exit = new HyperlinkEvent(editor,
                                 HyperlinkEvent.EventType.EXITED, u, this.href,
                                 lastElem, mouseEvent);
                editor.fireHyperlinkUpdate(exit);
            }
            if (href != null) {
                // fire an entered event on the new link
                URL u;
                try {
                    u = new URL(doc.getBase(), href);
                } catch (MalformedURLException m) {
                    u = null;
                }
                HyperlinkEvent entered = new HyperlinkEvent(editor,
                                            HyperlinkEvent.EventType.ENTERED,
                                            u, href, curElem, mouseEvent);
                editor.fireHyperlinkUpdate(entered);
            }
        }
    }

    /**
     * Interface to be supported by the parser.  This enables
     * providing a different parser while reusing some of the
     * implementation provided by this editor kit.
     */
    public static abstract class Parser {
        /**
         * Parse the given stream and drive the given callback
         * with the results of the parse.  This method should
         * be implemented to be thread-safe.
         */
        public abstract void parse(Reader r, ParserCallback cb, boolean ignoreCharSet) throws IOException;

    }

    /**
     * The result of parsing drives these callback methods.
     * The open and close actions should be balanced.  The
     * <code>flush</code> method will be the last method
     * called, to give the receiver a chance to flush any
     * pending data into the document.
     * <p>Refer to DocumentParser, the default parser used, for further
     * information on the contents of the AttributeSets, the positions, and
     * other info.
     *
     * @see javax.swing.text.html.parser.DocumentParser
     */
    public static class ParserCallback {
        /**
         * This is passed as an attribute in the attributeset to indicate
         * the element is implied eg, the string '&lt;&gt;foo&lt;\t&gt;'
         * contains an implied html element and an implied body element.
         *
         * @since 1.3
         */
        public static final Object IMPLIED = "_implied_";


        public void flush() throws BadLocationException {
        }

        public void handleText(char[] data, int pos) {
        }

        public void handleComment(char[] data, int pos) {
        }

        public void handleStartTag(HTML.Tag t, MutableAttributeSet a, int pos) {
        }

        public void handleEndTag(HTML.Tag t, int pos) {
        }

        public void handleSimpleTag(HTML.Tag t, MutableAttributeSet a, int pos) {
        }

        public void handleError(String errorMsg, int pos){
        }

        /**
         * This is invoked after the stream has been parsed, but before
         * <code>flush</code>. <code>eol</code> will be one of \n, \r
         * or \r\n, which ever is encountered the most in parsing the
         * stream.
         *
         * @since 1.3
         */
        public void handleEndOfLineString(String eol) {
        }
    }

    /**
     * A factory to build views for HTML.  The following
     * table describes what this factory will build by
     * default.
     *
     * <table summary="Describes the tag and view created by this factory by default">
     * <tr>
     * <th align=left>Tag<th align=left>View created
     * </tr><tr>
     * <td>HTML.Tag.CONTENT<td>InlineView
     * </tr><tr>
     * <td>HTML.Tag.IMPLIED<td>javax.swing.text.html.ParagraphView
     * </tr><tr>
     * <td>HTML.Tag.P<td>javax.swing.text.html.ParagraphView
     * </tr><tr>
     * <td>HTML.Tag.H1<td>javax.swing.text.html.ParagraphView
     * </tr><tr>
     * <td>HTML.Tag.H2<td>javax.swing.text.html.ParagraphView
     * </tr><tr>
     * <td>HTML.Tag.H3<td>javax.swing.text.html.ParagraphView
     * </tr><tr>
     * <td>HTML.Tag.H4<td>javax.swing.text.html.ParagraphView
     * </tr><tr>
     * <td>HTML.Tag.H5<td>javax.swing.text.html.ParagraphView
     * </tr><tr>
     * <td>HTML.Tag.H6<td>javax.swing.text.html.ParagraphView
     * </tr><tr>
     * <td>HTML.Tag.DT<td>javax.swing.text.html.ParagraphView
     * </tr><tr>
     * <td>HTML.Tag.MENU<td>ListView
     * </tr><tr>
     * <td>HTML.Tag.DIR<td>ListView
     * </tr><tr>
     * <td>HTML.Tag.UL<td>ListView
     * </tr><tr>
     * <td>HTML.Tag.OL<td>ListView
     * </tr><tr>
     * <td>HTML.Tag.LI<td>BlockView
     * </tr><tr>
     * <td>HTML.Tag.DL<td>BlockView
     * </tr><tr>
     * <td>HTML.Tag.DD<td>BlockView
     * </tr><tr>
     * <td>HTML.Tag.BODY<td>BlockView
     * </tr><tr>
     * <td>HTML.Tag.HTML<td>BlockView
     * </tr><tr>
     * <td>HTML.Tag.CENTER<td>BlockView
     * </tr><tr>
     * <td>HTML.Tag.DIV<td>BlockView
     * </tr><tr>
     * <td>HTML.Tag.BLOCKQUOTE<td>BlockView
     * </tr><tr>
     * <td>HTML.Tag.PRE<td>BlockView
     * </tr><tr>
     * <td>HTML.Tag.BLOCKQUOTE<td>BlockView
     * </tr><tr>
     * <td>HTML.Tag.PRE<td>BlockView
     * </tr><tr>
     * <td>HTML.Tag.IMG<td>ImageView
     * </tr><tr>
     * <td>HTML.Tag.HR<td>HRuleView
     * </tr><tr>
     * <td>HTML.Tag.BR<td>BRView
     * </tr><tr>
     * <td>HTML.Tag.TABLE<td>javax.swing.text.html.TableView
     * </tr><tr>
     * <td>HTML.Tag.INPUT<td>FormView
     * </tr><tr>
     * <td>HTML.Tag.SELECT<td>FormView
     * </tr><tr>
     * <td>HTML.Tag.TEXTAREA<td>FormView
     * </tr><tr>
     * <td>HTML.Tag.OBJECT<td>ObjectView
     * </tr><tr>
     * <td>HTML.Tag.FRAMESET<td>FrameSetView
     * </tr><tr>
     * <td>HTML.Tag.FRAME<td>FrameView
     * </tr>
     * </table>
     */
    public static class HTMLFactory implements ViewFactory {

        /**
         * Creates a view from an element.
         *
         * @param elem the element
         * @return the view
         */
        public View create(Element elem) {
            AttributeSet attrs = elem.getAttributes();
            Object elementName =
                attrs.getAttribute(AbstractDocument.ElementNameAttribute);
            Object o = (elementName != null) ?
                null : attrs.getAttribute(StyleConstants.NameAttribute);
            if (o instanceof HTML.Tag) {
                HTML.Tag kind = (HTML.Tag) o;
                if (kind == HTML.Tag.CONTENT) {
                    return new InlineView(elem);
                } else if (kind == HTML.Tag.IMPLIED) {
                    String ws = (String) elem.getAttributes().getAttribute(
                        CSS.Attribute.WHITE_SPACE);
                    if ((ws != null) && ws.equals("pre")) {
                        return new javax.swing.text.html.LineView(elem);
                    }
                    return new ParagraphView(elem);
                } else if ((kind == HTML.Tag.P) ||
                           (kind == HTML.Tag.H1) ||
                           (kind == HTML.Tag.H2) ||
                           (kind == HTML.Tag.H3) ||
                           (kind == HTML.Tag.H4) ||
                           (kind == HTML.Tag.H5) ||
                           (kind == HTML.Tag.H6) ||
                           (kind == HTML.Tag.DT)) {
                    // paragraph
                    return new ParagraphView(elem);
                } else if ((kind == HTML.Tag.MENU) ||
                           (kind == HTML.Tag.DIR) ||
                           (kind == HTML.Tag.UL)   ||
                           (kind == HTML.Tag.OL)) {
                    return new ListView(elem);
                } else if (kind == HTML.Tag.BODY) {
                    return new BodyBlockView(elem);
                } else if (kind == HTML.Tag.HTML) {
                    return new BlockView(elem, View.Y_AXIS);
                } else if ((kind == HTML.Tag.LI) ||
                           (kind == HTML.Tag.CENTER) ||
                           (kind == HTML.Tag.DL) ||
                           (kind == HTML.Tag.DD) ||
                           (kind == HTML.Tag.DIV) ||
                           (kind == HTML.Tag.BLOCKQUOTE) ||
                           (kind == HTML.Tag.PRE) ||
                           (kind == HTML.Tag.FORM)) {
                    // vertical box
                    return new BlockView(elem, View.Y_AXIS);
                } else if (kind == HTML.Tag.NOFRAMES) {
                    return new javax.swing.text.html.NoFramesView(elem, View.Y_AXIS);
                } else if (kind==HTML.Tag.IMG) {
                    return new ImageView(elem);
                } else if (kind == HTML.Tag.ISINDEX) {
                    return new javax.swing.text.html.IsindexView(elem);
                } else if (kind == HTML.Tag.HR) {
                    return new javax.swing.text.html.HRuleView(elem);
                } else if (kind == HTML.Tag.BR) {
                    return new javax.swing.text.html.BRView(elem);
                } else if (kind == HTML.Tag.TABLE) {
                    return new javax.swing.text.html.TableView(elem);
                } else if ((kind == HTML.Tag.INPUT) ||
                           (kind == HTML.Tag.SELECT) ||
                           (kind == HTML.Tag.TEXTAREA)) {
                    return new FormView(elem);
                } else if (kind == HTML.Tag.OBJECT) {
                    return new ObjectView(elem);
                } else if (kind == HTML.Tag.FRAMESET) {
                     if (elem.getAttributes().isDefined(HTML.Attribute.ROWS)) {
                         return new javax.swing.text.html.FrameSetView(elem, View.Y_AXIS);
                     } else if (elem.getAttributes().isDefined(HTML.Attribute.COLS)) {
                         return new javax.swing.text.html.FrameSetView(elem, View.X_AXIS);
                     }
                     throw new RuntimeException("Can't build a"  + kind + ", " + elem + ":" +
                                     "no ROWS or COLS defined.");
                } else if (kind == HTML.Tag.FRAME) {
                    return new javax.swing.text.html.FrameView(elem);
                } else if (kind instanceof HTML.UnknownTag) {
                    return new javax.swing.text.html.HiddenTagView(elem);
                } else if (kind == HTML.Tag.COMMENT) {
                    return new javax.swing.text.html.CommentView(elem);
                } else if (kind == HTML.Tag.HEAD) {
                    // Make the head never visible, and never load its
                    // children. For Cursor positioning,
                    // getNextVisualPositionFrom is overriden to always return
                    // the end offset of the element.
                    return new BlockView(elem, View.X_AXIS) {
                        public float getPreferredSpan(int axis) {
                            return 0;
                        }
                        public float getMinimumSpan(int axis) {
                            return 0;
                        }
                        public float getMaximumSpan(int axis) {
                            return 0;
                        }
                        protected void loadChildren(ViewFactory f) {
                        }
                        public Shape modelToView(int pos, Shape a,
                               Position.Bias b) throws BadLocationException {
                            return a;
                        }
                        public int getNextVisualPositionFrom(int pos,
                                     Position.Bias b, Shape a,
                                     int direction, Position.Bias[] biasRet) {
                            return getElement().getEndOffset();
                        }
                    };
                } else if ((kind == HTML.Tag.TITLE) ||
                           (kind == HTML.Tag.META) ||
                           (kind == HTML.Tag.LINK) ||
                           (kind == HTML.Tag.STYLE) ||
                           (kind == HTML.Tag.SCRIPT) ||
                           (kind == HTML.Tag.AREA) ||
                           (kind == HTML.Tag.MAP) ||
                           (kind == HTML.Tag.PARAM) ||
                           (kind == HTML.Tag.APPLET)) {
                    return new javax.swing.text.html.HiddenTagView(elem);
                }
            }
            // If we get here, it's either an element we don't know about
            // or something from StyledDocument that doesn't have a mapping to HTML.
            String nm = (elementName != null) ? (String)elementName :
                                                elem.getName();
            if (nm != null) {
                if (nm.equals(AbstractDocument.ContentElementName)) {
                    return new LabelView(elem);
                } else if (nm.equals(AbstractDocument.ParagraphElementName)) {
                    return new ParagraphView(elem);
                } else if (nm.equals(AbstractDocument.SectionElementName)) {
                    return new BoxView(elem, View.Y_AXIS);
                } else if (nm.equals(StyleConstants.ComponentElementName)) {
                    return new ComponentView(elem);
                } else if (nm.equals(StyleConstants.IconElementName)) {
                    return new IconView(elem);
                }
            }

            // default to text display
            return new LabelView(elem);
        }

        static class BodyBlockView extends BlockView implements ComponentListener {
            public BodyBlockView(Element elem) {
                super(elem,View.Y_AXIS);
            }
            // reimplement major axis requirements to indicate that the
            // block is flexible for the body element... so that it can
            // be stretched to fill the background properly.
            protected SizeRequirements calculateMajorAxisRequirements(int axis, SizeRequirements r) {
                r = super.calculateMajorAxisRequirements(axis, r);
                r.maximum = Integer.MAX_VALUE;
                return r;
            }

            protected void layoutMinorAxis(int targetSpan, int axis, int[] offsets, int[] spans) {
                Container container = getContainer();
                Container parentContainer;
                if (container != null
                    && (container instanceof JEditorPane)
                    && (parentContainer = container.getParent()) != null
                    && (parentContainer instanceof JViewport)) {
                    JViewport viewPort = (JViewport)parentContainer;
                    if (cachedViewPort != null) {
                        JViewport cachedObject = cachedViewPort.get();
                        if (cachedObject != null) {
                            if (cachedObject != viewPort) {
                                cachedObject.removeComponentListener(this);
                            }
                        } else {
                            cachedViewPort = null;
                        }
                    }
                    if (cachedViewPort == null) {
                        viewPort.addComponentListener(this);
                        cachedViewPort = new WeakReference<JViewport>(viewPort);
                    }

                    componentVisibleWidth = viewPort.getExtentSize().width;
                    if (componentVisibleWidth > 0) {
                    Insets insets = container.getInsets();
                    viewVisibleWidth = componentVisibleWidth - insets.left - getLeftInset();
                    //try to use viewVisibleWidth if it is smaller than targetSpan
                    targetSpan = Math.min(targetSpan, viewVisibleWidth);
                    }
                } else {
                    if (cachedViewPort != null) {
                        JViewport cachedObject = cachedViewPort.get();
                        if (cachedObject != null) {
                            cachedObject.removeComponentListener(this);
                        }
                        cachedViewPort = null;
                    }
                }
                super.layoutMinorAxis(targetSpan, axis, offsets, spans);
            }

            public void setParent(View parent) {
                //if parent == null unregister component listener
                if (parent == null) {
                    if (cachedViewPort != null) {
                        Object cachedObject;
                        if ((cachedObject = cachedViewPort.get()) != null) {
                            ((JComponent)cachedObject).removeComponentListener(this);
                        }
                        cachedViewPort = null;
                    }
                }
                super.setParent(parent);
            }

            public void componentResized(ComponentEvent e) {
                if ( !(e.getSource() instanceof JViewport) ) {
                    return;
                }
                JViewport viewPort = (JViewport)e.getSource();
                if (componentVisibleWidth != viewPort.getExtentSize().width) {
                    Document doc = getDocument();
                    if (doc instanceof AbstractDocument) {
                        AbstractDocument document = (AbstractDocument)getDocument();
                        document.readLock();
                        try {
                            layoutChanged(X_AXIS);
                            preferenceChanged(null, true, true);
                        } finally {
                            document.readUnlock();
                        }

                    }
                }
            }
            public void componentHidden(ComponentEvent e) {
            }
            public void componentMoved(ComponentEvent e) {
            }
            public void componentShown(ComponentEvent e) {
            }
            /*
             * we keep weak reference to viewPort if and only if BodyBoxView is listening for ComponentEvents
             * only in that case cachedViewPort is not equal to null.
             * we need to keep this reference in order to remove BodyBoxView from viewPort listeners.
             *
             */
            private Reference<JViewport> cachedViewPort = null;
            private boolean isListening = false;
            private int viewVisibleWidth = Integer.MAX_VALUE;
            private int componentVisibleWidth = Integer.MAX_VALUE;
        }

    }

    // --- Action implementations ------------------------------

/** The bold action identifier
*/
    public static final String  BOLD_ACTION = "html-bold-action";
/** The italic action identifier
*/
    public static final String  ITALIC_ACTION = "html-italic-action";
/** The paragraph left indent action identifier
*/
    public static final String  PARA_INDENT_LEFT = "html-para-indent-left";
/** The paragraph right indent action identifier
*/
    public static final String  PARA_INDENT_RIGHT = "html-para-indent-right";
/** The  font size increase to next value action identifier
*/
    public static final String  FONT_CHANGE_BIGGER = "html-font-bigger";
/** The font size decrease to next value action identifier
*/
    public static final String  FONT_CHANGE_SMALLER = "html-font-smaller";
/** The Color choice action identifier
     The color is passed as an argument
*/
    public static final String  COLOR_ACTION = "html-color-action";
/** The logical style choice action identifier
     The logical style is passed in as an argument
*/
    public static final String  LOGICAL_STYLE_ACTION = "html-logical-style-action";
    /**
     * Align images at the top.
     */
    public static final String  IMG_ALIGN_TOP = "html-image-align-top";

    /**
     * Align images in the middle.
     */
    public static final String  IMG_ALIGN_MIDDLE = "html-image-align-middle";

    /**
     * Align images at the bottom.
     */
    public static final String  IMG_ALIGN_BOTTOM = "html-image-align-bottom";

    /**
     * Align images at the border.
     */
    public static final String  IMG_BORDER = "html-image-border";


    /** HTML used when inserting tables. */
    private static final String INSERT_TABLE_HTML = "<table border=1><tr><td></td></tr></table>";

    /** HTML used when inserting unordered lists. */
    private static final String INSERT_UL_HTML = "<ul><li></li></ul>";

    /** HTML used when inserting ordered lists. */
    private static final String INSERT_OL_HTML = "<ol><li></li></ol>";

    /** HTML used when inserting hr. */
    private static final String INSERT_HR_HTML = "<hr>";

    /** HTML used when inserting pre. */
    private static final String INSERT_PRE_HTML = "<pre></pre>";

    private static final NavigateLinkAction nextLinkAction =
        new NavigateLinkAction("next-link-action");

    private static final NavigateLinkAction previousLinkAction =
        new NavigateLinkAction("previous-link-action");

    private static final ActivateLinkAction activateLinkAction =
        new ActivateLinkAction("activate-link-action");

    private static final Action[] defaultActions = {
        new InsertHTMLTextAction("InsertTable", INSERT_TABLE_HTML,
                                 HTML.Tag.BODY, HTML.Tag.TABLE),
        new InsertHTMLTextAction("InsertTableRow", INSERT_TABLE_HTML,
                                 HTML.Tag.TABLE, HTML.Tag.TR,
                                 HTML.Tag.BODY, HTML.Tag.TABLE),
        new InsertHTMLTextAction("InsertTableDataCell", INSERT_TABLE_HTML,
                                 HTML.Tag.TR, HTML.Tag.TD,
                                 HTML.Tag.BODY, HTML.Tag.TABLE),
        new InsertHTMLTextAction("InsertUnorderedList", INSERT_UL_HTML,
                                 HTML.Tag.BODY, HTML.Tag.UL),
        new InsertHTMLTextAction("InsertUnorderedListItem", INSERT_UL_HTML,
                                 HTML.Tag.UL, HTML.Tag.LI,
                                 HTML.Tag.BODY, HTML.Tag.UL),
        new InsertHTMLTextAction("InsertOrderedList", INSERT_OL_HTML,
                                 HTML.Tag.BODY, HTML.Tag.OL),
        new InsertHTMLTextAction("InsertOrderedListItem", INSERT_OL_HTML,
                                 HTML.Tag.OL, HTML.Tag.LI,
                                 HTML.Tag.BODY, HTML.Tag.OL),
        new InsertHRAction(),
        new InsertHTMLTextAction("InsertPre", INSERT_PRE_HTML,
                                 HTML.Tag.BODY, HTML.Tag.PRE),
        nextLinkAction, previousLinkAction, activateLinkAction,

        new BeginAction(beginAction, false),
        new BeginAction(selectionBeginAction, true)
    };

    // link navigation support
    private boolean foundLink = false;
    private int prevHypertextOffset = -1;
    private Object linkNavigationTag;


    /**
     * An abstract Action providing some convenience methods that may
     * be useful in inserting HTML into an existing document.
     * <p>NOTE: None of the convenience methods obtain a lock on the
     * document. If you have another thread modifying the text these
     * methods may have inconsistent behavior, or return the wrong thing.
     */
    public static abstract class HTMLTextAction extends StyledTextAction {
        public HTMLTextAction(String name) {
            super(name);
        }

        /**
         * @return HTMLDocument of <code>e</code>.
         */
        protected HTMLDocument getHTMLDocument(JEditorPane e) {
            Document d = e.getDocument();
            if (d instanceof HTMLDocument) {
                return (HTMLDocument) d;
            }
            throw new IllegalArgumentException("document must be HTMLDocument");
        }

        /**
         * @return HTMLEditorKit for <code>e</code>.
         */
        protected javax.swing.text.html.HTMLEditorKit getHTMLEditorKit(JEditorPane e) {
            EditorKit k = e.getEditorKit();
            if (k instanceof javax.swing.text.html.HTMLEditorKit) {
                return (javax.swing.text.html.HTMLEditorKit) k;
            }
            throw new IllegalArgumentException("EditorKit must be HTMLEditorKit");
        }

        /**
         * Returns an array of the Elements that contain <code>offset</code>.
         * The first elements corresponds to the root.
         */
        protected Element[] getElementsAt(HTMLDocument doc, int offset) {
            return getElementsAt(doc.getDefaultRootElement(), offset, 0);
        }

        /**
         * Recursive method used by getElementsAt.
         */
        private Element[] getElementsAt(Element parent, int offset,
                                        int depth) {
            if (parent.isLeaf()) {
                Element[] retValue = new Element[depth + 1];
                retValue[depth] = parent;
                return retValue;
            }
            Element[] retValue = getElementsAt(parent.getElement
                          (parent.getElementIndex(offset)), offset, depth + 1);
            retValue[depth] = parent;
            return retValue;
        }

        /**
         * Returns number of elements, starting at the deepest leaf, needed
         * to get to an element representing <code>tag</code>. This will
         * return -1 if no elements is found representing <code>tag</code>,
         * or 0 if the parent of the leaf at <code>offset</code> represents
         * <code>tag</code>.
         */
        protected int elementCountToTag(HTMLDocument doc, int offset,
                                        HTML.Tag tag) {
            int depth = -1;
            Element e = doc.getCharacterElement(offset);
            while (e != null && e.getAttributes().getAttribute
                   (StyleConstants.NameAttribute) != tag) {
                e = e.getParentElement();
                depth++;
            }
            if (e == null) {
                return -1;
            }
            return depth;
        }

        /**
         * Returns the deepest element at <code>offset</code> matching
         * <code>tag</code>.
         */
        protected Element findElementMatchingTag(HTMLDocument doc, int offset,
                                                 HTML.Tag tag) {
            Element e = doc.getDefaultRootElement();
            Element lastMatch = null;
            while (e != null) {
                if (e.getAttributes().getAttribute
                   (StyleConstants.NameAttribute) == tag) {
                    lastMatch = e;
                }
                e = e.getElement(e.getElementIndex(offset));
            }
            return lastMatch;
        }
    }


    /**
     * InsertHTMLTextAction can be used to insert an arbitrary string of HTML
     * into an existing HTML document. At least two HTML.Tags need to be
     * supplied. The first Tag, parentTag, identifies the parent in
     * the document to add the elements to. The second tag, addTag,
     * identifies the first tag that should be added to the document as
     * seen in the HTML string. One important thing to remember, is that
     * the parser is going to generate all the appropriate tags, even if
     * they aren't in the HTML string passed in.<p>
     * For example, lets say you wanted to create an action to insert
     * a table into the body. The parentTag would be HTML.Tag.BODY,
     * addTag would be HTML.Tag.TABLE, and the string could be something
     * like &lt;table&gt;&lt;tr&gt;&lt;td&gt;&lt;/td&gt;&lt;/tr&gt;&lt;/table&gt;.
     * <p>There is also an option to supply an alternate parentTag and
     * addTag. These will be checked for if there is no parentTag at
     * offset.
     */
    public static class InsertHTMLTextAction extends HTMLTextAction {
        public InsertHTMLTextAction(String name, String html,
                                    HTML.Tag parentTag, HTML.Tag addTag) {
            this(name, html, parentTag, addTag, null, null);
        }

        public InsertHTMLTextAction(String name, String html,
                                    HTML.Tag parentTag,
                                    HTML.Tag addTag,
                                    HTML.Tag alternateParentTag,
                                    HTML.Tag alternateAddTag) {
            this(name, html, parentTag, addTag, alternateParentTag,
                 alternateAddTag, true);
        }

        /* public */
        InsertHTMLTextAction(String name, String html,
                                    HTML.Tag parentTag,
                                    HTML.Tag addTag,
                                    HTML.Tag alternateParentTag,
                                    HTML.Tag alternateAddTag,
                                    boolean adjustSelection) {
            super(name);
            this.html = html;
            this.parentTag = parentTag;
            this.addTag = addTag;
            this.alternateParentTag = alternateParentTag;
            this.alternateAddTag = alternateAddTag;
            this.adjustSelection = adjustSelection;
        }

        /**
         * A cover for HTMLEditorKit.insertHTML. If an exception it
         * thrown it is wrapped in a RuntimeException and thrown.
         */
        protected void insertHTML(JEditorPane editor, HTMLDocument doc,
                                  int offset, String html, int popDepth,
                                  int pushDepth, HTML.Tag addTag) {
            try {
                getHTMLEditorKit(editor).insertHTML(doc, offset, html,
                                                    popDepth, pushDepth,
                                                    addTag);
            } catch (IOException ioe) {
                throw new RuntimeException("Unable to insert: " + ioe);
            } catch (BadLocationException ble) {
                throw new RuntimeException("Unable to insert: " + ble);
            }
        }

        /**
         * This is invoked when inserting at a boundary. It determines
         * the number of pops, and then the number of pushes that need
         * to be performed, and then invokes insertHTML.
         * @since 1.3
         */
        protected void insertAtBoundary(JEditorPane editor, HTMLDocument doc,
                                        int offset, Element insertElement,
                                        String html, HTML.Tag parentTag,
                                        HTML.Tag addTag) {
            insertAtBoundry(editor, doc, offset, insertElement, html,
                            parentTag, addTag);
        }

        /**
         * This is invoked when inserting at a boundary. It determines
         * the number of pops, and then the number of pushes that need
         * to be performed, and then invokes insertHTML.
         * @deprecated As of Java 2 platform v1.3, use insertAtBoundary
         */
        @Deprecated
        protected void insertAtBoundry(JEditorPane editor, HTMLDocument doc,
                                       int offset, Element insertElement,
                                       String html, HTML.Tag parentTag,
                                       HTML.Tag addTag) {
            // Find the common parent.
            Element e;
            Element commonParent;
            boolean isFirst = (offset == 0);

            if (offset > 0 || insertElement == null) {
                e = doc.getDefaultRootElement();
                while (e != null && e.getStartOffset() != offset &&
                       !e.isLeaf()) {
                    e = e.getElement(e.getElementIndex(offset));
                }
                commonParent = (e != null) ? e.getParentElement() : null;
            }
            else {
                // If inserting at the origin, the common parent is the
                // insertElement.
                commonParent = insertElement;
            }
            if (commonParent != null) {
                // Determine how many pops to do.
                int pops = 0;
                int pushes = 0;
                if (isFirst && insertElement != null) {
                    e = commonParent;
                    while (e != null && !e.isLeaf()) {
                        e = e.getElement(e.getElementIndex(offset));
                        pops++;
                    }
                }
                else {
                    e = commonParent;
                    offset--;
                    while (e != null && !e.isLeaf()) {
                        e = e.getElement(e.getElementIndex(offset));
                        pops++;
                    }

                    // And how many pushes
                    e = commonParent;
                    offset++;
                    while (e != null && e != insertElement) {
                        e = e.getElement(e.getElementIndex(offset));
                        pushes++;
                    }
                }
                pops = Math.max(0, pops - 1);

                // And insert!
                insertHTML(editor, doc, offset, html, pops, pushes, addTag);
            }
        }

        /**
         * If there is an Element with name <code>tag</code> at
         * <code>offset</code>, this will invoke either insertAtBoundary
         * or <code>insertHTML</code>. This returns true if there is
         * a match, and one of the inserts is invoked.
         */
        /*protected*/
        boolean insertIntoTag(JEditorPane editor, HTMLDocument doc,
                              int offset, HTML.Tag tag, HTML.Tag addTag) {
            Element e = findElementMatchingTag(doc, offset, tag);
            if (e != null && e.getStartOffset() == offset) {
                insertAtBoundary(editor, doc, offset, e, html,
                                 tag, addTag);
                return true;
            }
            else if (offset > 0) {
                int depth = elementCountToTag(doc, offset - 1, tag);
                if (depth != -1) {
                    insertHTML(editor, doc, offset, html, depth, 0, addTag);
                    return true;
                }
            }
            return false;
        }

        /**
         * Called after an insertion to adjust the selection.
         */
        /* protected */
        void adjustSelection(JEditorPane pane, HTMLDocument doc,
                             int startOffset, int oldLength) {
            int newLength = doc.getLength();
            if (newLength != oldLength && startOffset < newLength) {
                if (startOffset > 0) {
                    String text;
                    try {
                        text = doc.getText(startOffset - 1, 1);
                    } catch (BadLocationException ble) {
                        text = null;
                    }
                    if (text != null && text.length() > 0 &&
                        text.charAt(0) == '\n') {
                        pane.select(startOffset, startOffset);
                    }
                    else {
                        pane.select(startOffset + 1, startOffset + 1);
                    }
                }
                else {
                    pane.select(1, 1);
                }
            }
        }

        /**
         * Inserts the HTML into the document.
         *
         * @param ae the event
         */
        public void actionPerformed(ActionEvent ae) {
            JEditorPane editor = getEditor(ae);
            if (editor != null) {
                HTMLDocument doc = getHTMLDocument(editor);
                int offset = editor.getSelectionStart();
                int length = doc.getLength();
                boolean inserted;
                // Try first choice
                if (!insertIntoTag(editor, doc, offset, parentTag, addTag) &&
                    alternateParentTag != null) {
                    // Then alternate.
                    inserted = insertIntoTag(editor, doc, offset,
                                             alternateParentTag,
                                             alternateAddTag);
                }
                else {
                    inserted = true;
                }
                if (adjustSelection && inserted) {
                    adjustSelection(editor, doc, offset, length);
                }
            }
        }

        /** HTML to insert. */
        protected String html;
        /** Tag to check for in the document. */
        protected HTML.Tag parentTag;
        /** Tag in HTML to start adding tags from. */
        protected HTML.Tag addTag;
        /** Alternate Tag to check for in the document if parentTag is
         * not found. */
        protected HTML.Tag alternateParentTag;
        /** Alternate tag in HTML to start adding tags from if parentTag
         * is not found and alternateParentTag is found. */
        protected HTML.Tag alternateAddTag;
        /** True indicates the selection should be adjusted after an insert. */
        boolean adjustSelection;
    }


    /**
     * InsertHRAction is special, at actionPerformed time it will determine
     * the parent HTML.Tag based on the paragraph element at the selection
     * start.
     */
    static class InsertHRAction extends InsertHTMLTextAction {
        InsertHRAction() {
            super("InsertHR", "<hr>", null, HTML.Tag.IMPLIED, null, null,
                  false);
        }

        /**
         * Inserts the HTML into the document.
         *
         * @param ae the event
         */
        public void actionPerformed(ActionEvent ae) {
            JEditorPane editor = getEditor(ae);
            if (editor != null) {
                HTMLDocument doc = getHTMLDocument(editor);
                int offset = editor.getSelectionStart();
                Element paragraph = doc.getParagraphElement(offset);
                if (paragraph.getParentElement() != null) {
                    parentTag = (HTML.Tag)paragraph.getParentElement().
                                  getAttributes().getAttribute
                                  (StyleConstants.NameAttribute);
                    super.actionPerformed(ae);
                }
            }
        }

    }

    /*
     * Returns the object in an AttributeSet matching a key
     */
    static private Object getAttrValue(AttributeSet attr, HTML.Attribute key) {
        Enumeration names = attr.getAttributeNames();
        while (names.hasMoreElements()) {
            Object nextKey = names.nextElement();
            Object nextVal = attr.getAttribute(nextKey);
            if (nextVal instanceof AttributeSet) {
                Object value = getAttrValue((AttributeSet)nextVal, key);
                if (value != null) {
                    return value;
                }
            } else if (nextKey == key) {
                return nextVal;
            }
        }
        return null;
    }

    /*
     * Action to move the focus on the next or previous hypertext link
     * or object. TODO: This method relies on support from the
     * javax.accessibility package.  The text package should support
     * keyboard navigation of text elements directly.
     */
    static class NavigateLinkAction extends TextAction implements CaretListener {

        private static final FocusHighlightPainter focusPainter =
            new FocusHighlightPainter(null);
        private final boolean focusBack;

        /*
         * Create this action with the appropriate identifier.
         */
        public NavigateLinkAction(String actionName) {
            super(actionName);
            focusBack = "previous-link-action".equals(actionName);
        }

        /**
         * Called when the caret position is updated.
         *
         * @param e the caret event
         */
        public void caretUpdate(CaretEvent e) {
            Object src = e.getSource();
            if (src instanceof JTextComponent) {
                JTextComponent comp = (JTextComponent) src;
                javax.swing.text.html.HTMLEditorKit kit = getHTMLEditorKit(comp);
                if (kit != null && kit.foundLink) {
                    kit.foundLink = false;
                    // TODO: The AccessibleContext for the editor should register
                    // as a listener for CaretEvents and forward the events to
                    // assistive technologies listening for such events.
                    comp.getAccessibleContext().firePropertyChange(
                        AccessibleContext.ACCESSIBLE_HYPERTEXT_OFFSET,
                        Integer.valueOf(kit.prevHypertextOffset),
                        Integer.valueOf(e.getDot()));
                }
            }
        }

        /*
         * The operation to perform when this action is triggered.
         */
        public void actionPerformed(ActionEvent e) {
            JTextComponent comp = getTextComponent(e);
            if (comp == null || comp.isEditable()) {
                return;
            }

            Document doc = comp.getDocument();
            javax.swing.text.html.HTMLEditorKit kit = getHTMLEditorKit(comp);
            if (doc == null || kit == null) {
                return;
            }

            // TODO: Should start successive iterations from the
            // current caret position.
            ElementIterator ei = new ElementIterator(doc);
            int currentOffset = comp.getCaretPosition();
            int prevStartOffset = -1;
            int prevEndOffset = -1;

            // highlight the next link or object after the current caret position
            Element nextElement;
            while ((nextElement = ei.next()) != null) {
                String name = nextElement.getName();
                AttributeSet attr = nextElement.getAttributes();

                Object href = getAttrValue(attr, HTML.Attribute.HREF);
                if (!(name.equals(HTML.Tag.OBJECT.toString())) && href == null) {
                    continue;
                }

                int elementOffset = nextElement.getStartOffset();
                if (focusBack) {
                    if (elementOffset >= currentOffset &&
                        prevStartOffset >= 0) {

                        kit.foundLink = true;
                        comp.setCaretPosition(prevStartOffset);
                        moveCaretPosition(comp, kit, prevStartOffset,
                                          prevEndOffset);
                        kit.prevHypertextOffset = prevStartOffset;
                        return;
                    }
                } else { // focus forward
                    if (elementOffset > currentOffset) {

                        kit.foundLink = true;
                        comp.setCaretPosition(elementOffset);
                        moveCaretPosition(comp, kit, elementOffset,
                                          nextElement.getEndOffset());
                        kit.prevHypertextOffset = elementOffset;
                        return;
                    }
                }
                prevStartOffset = nextElement.getStartOffset();
                prevEndOffset = nextElement.getEndOffset();
            }
            if (focusBack && prevStartOffset >= 0) {
                kit.foundLink = true;
                comp.setCaretPosition(prevStartOffset);
                moveCaretPosition(comp, kit, prevStartOffset, prevEndOffset);
                kit.prevHypertextOffset = prevStartOffset;
            }
        }

        /*
         * Moves the caret from mark to dot
         */
        private void moveCaretPosition(JTextComponent comp, javax.swing.text.html.HTMLEditorKit kit,
                                       int mark, int dot) {
            Highlighter h = comp.getHighlighter();
            if (h != null) {
                int p0 = Math.min(dot, mark);
                int p1 = Math.max(dot, mark);
                try {
                    if (kit.linkNavigationTag != null) {
                        h.changeHighlight(kit.linkNavigationTag, p0, p1);
                    } else {
                        kit.linkNavigationTag =
                                h.addHighlight(p0, p1, focusPainter);
                    }
                } catch (BadLocationException e) {
                }
            }
        }

        private javax.swing.text.html.HTMLEditorKit getHTMLEditorKit(JTextComponent comp) {
            if (comp instanceof JEditorPane) {
                EditorKit kit = ((JEditorPane) comp).getEditorKit();
                if (kit instanceof javax.swing.text.html.HTMLEditorKit) {
                    return (javax.swing.text.html.HTMLEditorKit) kit;
                }
            }
            return null;
        }

        /**
         * A highlight painter that draws a one-pixel border around
         * the highlighted area.
         */
        static class FocusHighlightPainter extends
            DefaultHighlighter.DefaultHighlightPainter {

            FocusHighlightPainter(Color color) {
                super(color);
            }

            /**
             * Paints a portion of a highlight.
             *
             * @param g the graphics context
             * @param offs0 the starting model offset &ge; 0
             * @param offs1 the ending model offset &ge; offs1
             * @param bounds the bounding box of the view, which is not
             *        necessarily the region to paint.
             * @param c the editor
             * @param view View painting for
             * @return region in which drawing occurred
             */
            public Shape paintLayer(Graphics g, int offs0, int offs1,
                                    Shape bounds, JTextComponent c, View view) {

                Color color = getColor();

                if (color == null) {
                    g.setColor(c.getSelectionColor());
                }
                else {
                    g.setColor(color);
                }
                if (offs0 == view.getStartOffset() &&
                    offs1 == view.getEndOffset()) {
                    // Contained in view, can just use bounds.
                    Rectangle alloc;
                    if (bounds instanceof Rectangle) {
                        alloc = (Rectangle)bounds;
                    }
                    else {
                        alloc = bounds.getBounds();
                    }
                    g.drawRect(alloc.x, alloc.y, alloc.width - 1, alloc.height);
                    return alloc;
                }
                else {
                    // Should only render part of View.
                    try {
                        // --- determine locations ---
                        Shape shape = view.modelToView(offs0, Position.Bias.Forward,
                                                       offs1,Position.Bias.Backward,
                                                       bounds);
                        Rectangle r = (shape instanceof Rectangle) ?
                            (Rectangle)shape : shape.getBounds();
                        g.drawRect(r.x, r.y, r.width - 1, r.height);
                        return r;
                    } catch (BadLocationException e) {
                        // can't render
                    }
                }
                // Only if exception
                return null;
            }
        }
    }

    /*
     * Action to activate the hypertext link that has focus.
     * TODO: This method relies on support from the
     * javax.accessibility package.  The text package should support
     * keyboard navigation of text elements directly.
     */
    static class ActivateLinkAction extends TextAction {

        /**
         * Create this action with the appropriate identifier.
         */
        public ActivateLinkAction(String actionName) {
            super(actionName);
        }

        /*
         * activates the hyperlink at offset
         */
        private void activateLink(String href, HTMLDocument doc,
                                  JEditorPane editor, int offset) {
            try {
                URL page =
                    (URL)doc.getProperty(Document.StreamDescriptionProperty);
                URL url = new URL(page, href);
                HyperlinkEvent linkEvent = new HyperlinkEvent
                    (editor, HyperlinkEvent.EventType.
                     ACTIVATED, url, url.toExternalForm(),
                     doc.getCharacterElement(offset));
                editor.fireHyperlinkUpdate(linkEvent);
            } catch (MalformedURLException m) {
            }
        }

        /*
         * Invokes default action on the object in an element
         */
        private void doObjectAction(JEditorPane editor, Element elem) {
            View view = getView(editor, elem);
            if (view != null && view instanceof ObjectView) {
                Component comp = ((ObjectView)view).getComponent();
                if (comp != null && comp instanceof Accessible) {
                    AccessibleContext ac = comp.getAccessibleContext();
                    if (ac != null) {
                        AccessibleAction aa = ac.getAccessibleAction();
                        if (aa != null) {
                            aa.doAccessibleAction(0);
                        }
                    }
                }
            }
        }

        /*
         * Returns the root view for a document
         */
        private View getRootView(JEditorPane editor) {
            return editor.getUI().getRootView(editor);
        }

        /*
         * Returns a view associated with an element
         */
        private View getView(JEditorPane editor, Element elem) {
            Object lock = lock(editor);
            try {
                View rootView = getRootView(editor);
                int start = elem.getStartOffset();
                if (rootView != null) {
                    return getView(rootView, elem, start);
                }
                return null;
            } finally {
                unlock(lock);
            }
        }

        private View getView(View parent, Element elem, int start) {
            if (parent.getElement() == elem) {
                return parent;
            }
            int index = parent.getViewIndex(start, Position.Bias.Forward);

            if (index != -1 && index < parent.getViewCount()) {
                return getView(parent.getView(index), elem, start);
            }
            return null;
        }

        /*
         * If possible acquires a lock on the Document.  If a lock has been
         * obtained a key will be retured that should be passed to
         * <code>unlock</code>.
         */
        private Object lock(JEditorPane editor) {
            Document document = editor.getDocument();

            if (document instanceof AbstractDocument) {
                ((AbstractDocument)document).readLock();
                return document;
            }
            return null;
        }

        /*
         * Releases a lock previously obtained via <code>lock</code>.
         */
        private void unlock(Object key) {
            if (key != null) {
                ((AbstractDocument)key).readUnlock();
            }
        }

        /*
         * The operation to perform when this action is triggered.
         */
        public void actionPerformed(ActionEvent e) {

            JTextComponent c = getTextComponent(e);
            if (c.isEditable() || !(c instanceof JEditorPane)) {
                return;
            }
            JEditorPane editor = (JEditorPane)c;

            Document d = editor.getDocument();
            if (d == null || !(d instanceof HTMLDocument)) {
                return;
            }
            HTMLDocument doc = (HTMLDocument)d;

            ElementIterator ei = new ElementIterator(doc);
            int currentOffset = editor.getCaretPosition();

            // invoke the next link or object action
            String urlString = null;
            String objString = null;
            Element currentElement;
            while ((currentElement = ei.next()) != null) {
                String name = currentElement.getName();
                AttributeSet attr = currentElement.getAttributes();

                Object href = getAttrValue(attr, HTML.Attribute.HREF);
                if (href != null) {
                    if (currentOffset >= currentElement.getStartOffset() &&
                        currentOffset <= currentElement.getEndOffset()) {

                        activateLink((String)href, doc, editor, currentOffset);
                        return;
                    }
                } else if (name.equals(HTML.Tag.OBJECT.toString())) {
                    Object obj = getAttrValue(attr, HTML.Attribute.CLASSID);
                    if (obj != null) {
                        if (currentOffset >= currentElement.getStartOffset() &&
                            currentOffset <= currentElement.getEndOffset()) {

                            doObjectAction(editor, currentElement);
                            return;
                        }
                    }
                }
            }
        }
    }

    private static int getBodyElementStart(JTextComponent comp) {
        Element rootElement = comp.getDocument().getRootElements()[0];
        for (int i = 0; i < rootElement.getElementCount(); i++) {
            Element currElement = rootElement.getElement(i);
            if("body".equals(currElement.getName())) {
                return currElement.getStartOffset();
            }
        }
        return 0;
    }

    /*
     * Move the caret to the beginning of the document.
     * @see DefaultEditorKit#beginAction
     * @see HTMLEditorKit#getActions
     */

    static class BeginAction extends TextAction {

        /* Create this object with the appropriate identifier. */
        BeginAction(String nm, boolean select) {
            super(nm);
            this.select = select;
        }

        /** The operation to perform when this action is triggered. */
        public void actionPerformed(ActionEvent e) {
            JTextComponent target = getTextComponent(e);
            int bodyStart = getBodyElementStart(target);

            if (target != null) {
                if (select) {
                    target.moveCaretPosition(bodyStart);
                } else {
                    target.setCaretPosition(bodyStart);
                }
            }
        }

        private boolean select;
    }
}
