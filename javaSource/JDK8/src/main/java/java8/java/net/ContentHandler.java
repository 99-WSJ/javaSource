/*
 * Copyright (c) 1995, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java8.java.net;

import java.io.IOException;
import java.net.URLConnection;

/**
 * The abstract class {@code ContentHandler} is the superclass
 * of all classes that read an {@code Object} from a
 * {@code URLConnection}.
 * <p>
 * An application does not generally call the
 * {@code getContent} method in this class directly. Instead, an
 * application calls the {@code getContent} method in class
 * {@code URL} or in {@code URLConnection}.
 * The application's content handler factory (an instance of a class that
 * implements the interface {@code ContentHandlerFactory} set
 * up by a call to {@code setContentHandler}) is
 * called with a {@code String} giving the MIME type of the
 * object being received on the socket. The factory returns an
 * instance of a subclass of {@code ContentHandler}, and its
 * {@code getContent} method is called to create the object.
 * <p>
 * If no content handler could be found, URLConnection will
 * look for a content handler in a user-defineable set of places.
 * By default it looks in sun.net.www.content, but users can define a
 * vertical-bar delimited set of class prefixes to search through in
 * addition by defining the java.content.handler.pkgs property.
 * The class name must be of the form:
 * <pre>
 *     {package-prefix}.{major}.{minor}
 * e.g.
 *     YoyoDyne.experimental.text.plain
 * </pre>
 * If the loading of the content handler class would be performed by
 * a classloader that is outside of the delegation chain of the caller,
 * the JVM will need the RuntimePermission "getClassLoader".
 *
 * @author  James Gosling
 * @see     java.net.ContentHandler#getContent(URLConnection)
 * @see     java.net.ContentHandlerFactory
 * @see     java.net.URL#getContent()
 * @see     URLConnection
 * @see     URLConnection#getContent()
 * @see     URLConnection#setContentHandlerFactory(java.net.ContentHandlerFactory)
 * @since   JDK1.0
 */
abstract public class ContentHandler {
    /**
     * Given a URL connect stream positioned at the beginning of the
     * representation of an object, this method reads that stream and
     * creates an object from it.
     *
     * @param      urlc   a URL connection.
     * @return     the object read by the {@code ContentHandler}.
     * @exception  IOException  if an I/O error occurs while reading the object.
     */
    abstract public Object getContent(URLConnection urlc) throws IOException;

    /**
     * Given a URL connect stream positioned at the beginning of the
     * representation of an object, this method reads that stream and
     * creates an object that matches one of the types specified.
     *
     * The default implementation of this method should call getContent()
     * and screen the return type for a match of the suggested types.
     *
     * @param      urlc   a URL connection.
     * @param      classes      an array of types requested
     * @return     the object read by the {@code ContentHandler} that is
     *                 the first match of the suggested types.
     *                 null if none of the requested  are supported.
     * @exception  IOException  if an I/O error occurs while reading the object.
     * @since 1.3
     */
    @SuppressWarnings("rawtypes")
    public Object getContent(URLConnection urlc, Class[] classes) throws IOException {
        Object obj = getContent(urlc);

        for (int i = 0; i < classes.length; i++) {
          if (classes[i].isInstance(obj)) {
                return obj;
          }
        }
        return null;
    }

}
