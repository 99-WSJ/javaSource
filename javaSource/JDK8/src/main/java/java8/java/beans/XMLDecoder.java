/*
 * Copyright (c) 2000, 2012, Oracle and/or its affiliates. All rights reserved.
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
package java8.java.beans;

import com.sun.beans.decoder.DocumentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

import java.beans.ExceptionListener;
import java.beans.Statement;
import java.beans.XMLEncoder;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * The <code>XMLDecoder</code> class is used to read XML documents
 * created using the <code>XMLEncoder</code> and is used just like
 * the <code>ObjectInputStream</code>. For example, one can use
 * the following fragment to read the first object defined
 * in an XML document written by the <code>XMLEncoder</code>
 * class:
 * <pre>
 *       XMLDecoder d = new XMLDecoder(
 *                          new BufferedInputStream(
 *                              new FileInputStream("Test.xml")));
 *       Object result = d.readObject();
 *       d.close();
 * </pre>
 *
 *<p>
 * For more information you might also want to check out
 * <a
 href="http://java.sun.com/products/jfc/tsc/articles/persistence3">Long Term Persistence of JavaBeans Components: XML Schema</a>,
 * an article in <em>The Swing Connection.</em>
 * @see XMLEncoder
 * @see java.io.ObjectInputStream
 *
 * @since 1.4
 *
 * @author Philip Milne
 */
public class XMLDecoder implements AutoCloseable {
    private final AccessControlContext acc = AccessController.getContext();
    private final DocumentHandler handler = new DocumentHandler();
    private final InputSource input;
    private Object owner;
    private Object[] array;
    private int index;

    /**
     * Creates a new input stream for reading archives
     * created by the <code>XMLEncoder</code> class.
     *
     * @param in The underlying stream.
     *
     * @see XMLEncoder#XMLEncoder(java.io.OutputStream)
     */
    public XMLDecoder(InputStream in) {
        this(in, null);
    }

    /**
     * Creates a new input stream for reading archives
     * created by the <code>XMLEncoder</code> class.
     *
     * @param in The underlying stream.
     * @param owner The owner of this stream.
     *
     */
    public XMLDecoder(InputStream in, Object owner) {
        this(in, owner, null);
    }

    /**
     * Creates a new input stream for reading archives
     * created by the <code>XMLEncoder</code> class.
     *
     * @param in the underlying stream.
     * @param owner the owner of this stream.
     * @param exceptionListener the exception handler for the stream;
     *        if <code>null</code> the default exception listener will be used.
     */
    public XMLDecoder(InputStream in, Object owner, ExceptionListener exceptionListener) {
        this(in, owner, exceptionListener, null);
    }

    /**
     * Creates a new input stream for reading archives
     * created by the <code>XMLEncoder</code> class.
     *
     * @param in the underlying stream.  <code>null</code> may be passed without
     *        error, though the resulting XMLDecoder will be useless
     * @param owner the owner of this stream.  <code>null</code> is a legal
     *        value
     * @param exceptionListener the exception handler for the stream, or
     *        <code>null</code> to use the default
     * @param cl the class loader used for instantiating objects.
     *        <code>null</code> indicates that the default class loader should
     *        be used
     * @since 1.5
     */
    public XMLDecoder(InputStream in, Object owner,
                      ExceptionListener exceptionListener, ClassLoader cl) {
        this(new InputSource(in), owner, exceptionListener, cl);
    }


    /**
     * Creates a new decoder to parse XML archives
     * created by the {@code XMLEncoder} class.
     * If the input source {@code is} is {@code null},
     * no exception is thrown and no parsing is performed.
     * This behavior is similar to behavior of other constructors
     * that use {@code InputStream} as a parameter.
     *
     * @param is  the input source to parse
     *
     * @since 1.7
     */
    public XMLDecoder(InputSource is) {
        this(is, null, null, null);
    }

    /**
     * Creates a new decoder to parse XML archives
     * created by the {@code XMLEncoder} class.
     *
     * @param is     the input source to parse
     * @param owner  the owner of this decoder
     * @param el     the exception handler for the parser,
     *               or {@code null} to use the default exception handler
     * @param cl     the class loader used for instantiating objects,
     *               or {@code null} to use the default class loader
     *
     * @since 1.7
     */
    private XMLDecoder(InputSource is, Object owner, ExceptionListener el, ClassLoader cl) {
        this.input = is;
        this.owner = owner;
        setExceptionListener(el);
        this.handler.setClassLoader(cl);
        this.handler.setOwner(this);
    }

    /**
     * This method closes the input stream associated
     * with this stream.
     */
    public void close() {
        if (parsingComplete()) {
            close(this.input.getCharacterStream());
            close(this.input.getByteStream());
        }
    }

    private void close(Closeable in) {
        if (in != null) {
            try {
                in.close();
            }
            catch (IOException e) {
                getExceptionListener().exceptionThrown(e);
            }
        }
    }

    private boolean parsingComplete() {
        if (this.input == null) {
            return false;
        }
        if (this.array == null) {
            if ((this.acc == null) && (null != System.getSecurityManager())) {
                throw new SecurityException("AccessControlContext is not set");
            }
            AccessController.doPrivileged(new PrivilegedAction<Void>() {
                public Void run() {
                    java.beans.XMLDecoder.this.handler.parse(java.beans.XMLDecoder.this.input);
                    return null;
                }
            }, this.acc);
            this.array = this.handler.getObjects();
        }
        return true;
    }

    /**
     * Sets the exception handler for this stream to <code>exceptionListener</code>.
     * The exception handler is notified when this stream catches recoverable
     * exceptions.
     *
     * @param exceptionListener The exception handler for this stream;
     * if <code>null</code> the default exception listener will be used.
     *
     * @see #getExceptionListener
     */
    public void setExceptionListener(ExceptionListener exceptionListener) {
        if (exceptionListener == null) {
            exceptionListener = Statement.defaultExceptionListener;
        }
        this.handler.setExceptionListener(exceptionListener);
    }

    /**
     * Gets the exception handler for this stream.
     *
     * @return The exception handler for this stream.
     *     Will return the default exception listener if this has not explicitly been set.
     *
     * @see #setExceptionListener
     */
    public ExceptionListener getExceptionListener() {
        return this.handler.getExceptionListener();
    }

    /**
     * Reads the next object from the underlying input stream.
     *
     * @return the next object read
     *
     * @throws ArrayIndexOutOfBoundsException if the stream contains no objects
     *         (or no more objects)
     *
     * @see XMLEncoder#writeObject
     */
    public Object readObject() {
        return (parsingComplete())
                ? this.array[this.index++]
                : null;
    }

    /**
     * Sets the owner of this decoder to <code>owner</code>.
     *
     * @param owner The owner of this decoder.
     *
     * @see #getOwner
     */
    public void setOwner(Object owner) {
        this.owner = owner;
    }

    /**
     * Gets the owner of this decoder.
     *
     * @return The owner of this decoder.
     *
     * @see #setOwner
     */
    public Object getOwner() {
        return owner;
    }

    /**
     * Creates a new handler for SAX parser
     * that can be used to parse embedded XML archives
     * created by the {@code XMLEncoder} class.
     *
     * The {@code owner} should be used if parsed XML document contains
     * the method call within context of the &lt;java&gt; element.
     * The {@code null} value may cause illegal parsing in such case.
     * The same problem may occur, if the {@code owner} class
     * does not contain expected method to call. See details <a
     * href="http://java.sun.com/products/jfc/tsc/articles/persistence3/">here</a>.
     *
     * @param owner  the owner of the default handler
     *               that can be used as a value of &lt;java&gt; element
     * @param el     the exception handler for the parser,
     *               or {@code null} to use the default exception handler
     * @param cl     the class loader used for instantiating objects,
     *               or {@code null} to use the default class loader
     * @return an instance of {@code DefaultHandler} for SAX parser
     *
     * @since 1.7
     */
    public static DefaultHandler createHandler(Object owner, ExceptionListener el, ClassLoader cl) {
        DocumentHandler handler = new DocumentHandler();
        handler.setOwner(owner);
        handler.setExceptionListener(el);
        handler.setClassLoader(cl);
        return handler;
    }
}
