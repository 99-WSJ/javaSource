/*
 * Copyright (c) 2003, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java8.javax.xml.xpath;

import java.io.PrintWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.InvalidClassException;

/**
 * <code>XPathException</code> represents a generic XPath exception.</p>
 *
 * @author  <a href="Norman.Walsh@Sun.com">Norman Walsh</a>
 * @author <a href="mailto:Jeff.Suttor@Sun.COM">Jeff Suttor</a>
 * @since 1.5
 */
public class XPathException extends Exception {

    private static final ObjectStreamField[] serialPersistentFields = {
        new ObjectStreamField( "cause", Throwable.class )
    };

    /**
     * <p>Stream Unique Identifier.</p>
     */
    private static final long serialVersionUID = -1837080260374986980L;

    /**
     * <p>Constructs a new <code>XPathException</code>
     * with the specified detail <code>message</code>.</p>
     *
     * <p>The <code>cause</code> is not initialized.</p>
     *
     * <p>If <code>message</code> is <code>null</code>,
     * then a <code>NullPointerException</code> is thrown.</p>
     *
     * @param message The detail message.
     *
     * @throws NullPointerException When <code>message</code> is
     *   <code>null</code>.
     */
    public XPathException(String message) {
        super(message);
        if ( message == null ) {
            throw new NullPointerException ( "message can't be null");
        }
    }

    /**
     * <p>Constructs a new <code>XPathException</code>
     * with the specified <code>cause</code>.</p>
     *
     * <p>If <code>cause</code> is <code>null</code>,
     * then a <code>NullPointerException</code> is thrown.</p>
     *
     * @param cause The cause.
     *
     * @throws NullPointerException if <code>cause</code> is <code>null</code>.
     */
    public XPathException(Throwable cause) {
        super(cause);
        if ( cause == null ) {
            throw new NullPointerException ( "cause can't be null");
        }
    }

    /**
     * <p>Get the cause of this XPathException.</p>
     *
     * @return Cause of this XPathException.
     */
    public Throwable getCause() {
        return super.getCause();
    }

    /**
     * Writes "cause" field to the stream.
     * The cause is got from the parent class.
     *
     * @param out stream used for serialization.
     * @throws IOException thrown by <code>ObjectOutputStream</code>
     *
     */
    private void writeObject(ObjectOutputStream out)
            throws IOException
    {
        ObjectOutputStream.PutField fields = out.putFields();
        fields.put("cause", (Throwable) super.getCause());
        out.writeFields();
    }

    /**
     * Reads the "cause" field from the stream.
     * And initializes the "cause" if it wasn't
     * done before.
     *
     * @param in stream used for deserialization
     * @throws IOException thrown by <code>ObjectInputStream</code>
     * @throws ClassNotFoundException  thrown by <code>ObjectInputStream</code>
     */
    private void readObject(ObjectInputStream in)
            throws IOException, ClassNotFoundException
    {
        ObjectInputStream.GetField fields = in.readFields();
        Throwable scause = (Throwable) fields.get("cause", null);

        if (super.getCause() == null && scause != null) {
            try {
                super.initCause(scause);
            } catch(IllegalStateException e) {
                throw new InvalidClassException("Inconsistent state: two causes");
            }
        }
    }

    /**
     * <p>Print stack trace to specified <code>PrintStream</code>.</p>
     *
     * @param s Print stack trace to this <code>PrintStream</code>.
     */
    public void printStackTrace(java.io.PrintStream s) {
        if (getCause() != null) {
            getCause().printStackTrace(s);
          s.println("--------------- linked to ------------------");
        }

        super.printStackTrace(s);
    }

    /**
     * <p>Print stack trace to <code>System.err</code>.</p>
     */
    public void printStackTrace() {
        printStackTrace(System.err);
    }

    /**
     * <p>Print stack trace to specified <code>PrintWriter</code>.</p>
     *
     * @param s Print stack trace to this <code>PrintWriter</code>.
     */
    public void printStackTrace(PrintWriter s) {

        if (getCause() != null) {
            getCause().printStackTrace(s);
          s.println("--------------- linked to ------------------");
        }

        super.printStackTrace(s);
    }
}
