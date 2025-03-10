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

package java8.java.net;

import sun.net.www.ParseUtil;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * A URL Connection to a Java ARchive (JAR) file or an entry in a JAR
 * file.
 *
 * <p>The syntax of a JAR URL is:
 *
 * <pre>
 * jar:&lt;url&gt;!/{entry}
 * </pre>
 *
 * <p>for example:
 *
 * <p>{@code jar:http://www.foo.com/bar/baz.jar!/COM/foo/Quux.class}
 *
 * <p>Jar URLs should be used to refer to a JAR file or entries in
 * a JAR file. The example above is a JAR URL which refers to a JAR
 * entry. If the entry name is omitted, the URL refers to the whole
 * JAR file:
 *
 * {@code jar:http://www.foo.com/bar/baz.jar!/}
 *
 * <p>Users should cast the generic URLConnection to a
 * JarURLConnection when they know that the URL they created is a JAR
 * URL, and they need JAR-specific functionality. For example:
 *
 * <pre>
 * URL url = new URL("jar:file:/home/duke/duke.jar!/");
 * JarURLConnection jarConnection = (JarURLConnection)url.openConnection();
 * Manifest manifest = jarConnection.getManifest();
 * </pre>
 *
 * <p>JarURLConnection instances can only be used to read from JAR files.
 * It is not possible to get a {@link java.io.OutputStream} to modify or write
 * to the underlying JAR file using this class.
 * <p>Examples:
 *
 * <dl>
 *
 * <dt>A Jar entry
 * <dd>{@code jar:http://www.foo.com/bar/baz.jar!/COM/foo/Quux.class}
 *
 * <dt>A Jar file
 * <dd>{@code jar:http://www.foo.com/bar/baz.jar!/}
 *
 * <dt>A Jar directory
 * <dd>{@code jar:http://www.foo.com/bar/baz.jar!/COM/foo/}
 *
 * </dl>
 *
 * <p>{@code !/} is referred to as the <em>separator</em>.
 *
 * <p>When constructing a JAR url via {@code new URL(context, spec)},
 * the following rules apply:
 *
 * <ul>
 *
 * <li>if there is no context URL and the specification passed to the
 * URL constructor doesn't contain a separator, the URL is considered
 * to refer to a JarFile.
 *
 * <li>if there is a context URL, the context URL is assumed to refer
 * to a JAR file or a Jar directory.
 *
 * <li>if the specification begins with a '/', the Jar directory is
 * ignored, and the spec is considered to be at the root of the Jar
 * file.
 *
 * <p>Examples:
 *
 * <dl>
 *
 * <dt>context: <b>jar:http://www.foo.com/bar/jar.jar!/</b>,
 * spec:<b>baz/entry.txt</b>
 *
 * <dd>url:<b>jar:http://www.foo.com/bar/jar.jar!/baz/entry.txt</b>
 *
 * <dt>context: <b>jar:http://www.foo.com/bar/jar.jar!/baz</b>,
 * spec:<b>entry.txt</b>
 *
 * <dd>url:<b>jar:http://www.foo.com/bar/jar.jar!/baz/entry.txt</b>
 *
 * <dt>context: <b>jar:http://www.foo.com/bar/jar.jar!/baz</b>,
 * spec:<b>/entry.txt</b>
 *
 * <dd>url:<b>jar:http://www.foo.com/bar/jar.jar!/entry.txt</b>
 *
 * </dl>
 *
 * </ul>
 *
 * @see URL
 * @see URLConnection
 *
 * @see JarFile
 * @see java.util.jar.JarInputStream
 * @see Manifest
 * @see java.util.zip.ZipEntry
 *
 * @author Benjamin Renaud
 * @since 1.2
 */
public abstract class JarURLConnection extends URLConnection {

    private URL jarFileURL;
    private String entryName;

    /**
     * The connection to the JAR file URL, if the connection has been
     * initiated. This should be set by connect.
     */
    protected URLConnection jarFileURLConnection;

    /**
     * Creates the new JarURLConnection to the specified URL.
     * @param url the URL
     * @throws MalformedURLException if no legal protocol
     * could be found in a specification string or the
     * string could not be parsed.
     */

    protected JarURLConnection(URL url) throws MalformedURLException {
        super(url);
        parseSpecs(url);
    }

    /* get the specs for a given url out of the cache, and compute and
     * cache them if they're not there.
     */
    private void parseSpecs(URL url) throws MalformedURLException {
        String spec = url.getFile();

        int separator = spec.indexOf("!/");
        /*
         * REMIND: we don't handle nested JAR URLs
         */
        if (separator == -1) {
            throw new MalformedURLException("no !/ found in url spec:" + spec);
        }

        jarFileURL = new URL(spec.substring(0, separator++));
        entryName = null;

        /* if ! is the last letter of the innerURL, entryName is null */
        if (++separator != spec.length()) {
            entryName = spec.substring(separator, spec.length());
            entryName = ParseUtil.decode (entryName);
        }
    }

    /**
     * Returns the URL for the Jar file for this connection.
     *
     * @return the URL for the Jar file for this connection.
     */
    public URL getJarFileURL() {
        return jarFileURL;
    }

    /**
     * Return the entry name for this connection. This method
     * returns null if the JAR file URL corresponding to this
     * connection points to a JAR file and not a JAR file entry.
     *
     * @return the entry name for this connection, if any.
     */
    public String getEntryName() {
        return entryName;
    }

    /**
     * Return the JAR file for this connection.
     *
     * @return the JAR file for this connection. If the connection is
     * a connection to an entry of a JAR file, the JAR file object is
     * returned
     *
     * @exception IOException if an IOException occurs while trying to
     * connect to the JAR file for this connection.
     *
     * @see #connect
     */
    public abstract JarFile getJarFile() throws IOException;

    /**
     * Returns the Manifest for this connection, or null if none.
     *
     * @return the manifest object corresponding to the JAR file object
     * for this connection.
     *
     * @exception IOException if getting the JAR file for this
     * connection causes an IOException to be thrown.
     *
     * @see #getJarFile
     */
    public Manifest getManifest() throws IOException {
        return getJarFile().getManifest();
    }

    /**
     * Return the JAR entry object for this connection, if any. This
     * method returns null if the JAR file URL corresponding to this
     * connection points to a JAR file and not a JAR file entry.
     *
     * @return the JAR entry object for this connection, or null if
     * the JAR URL for this connection points to a JAR file.
     *
     * @exception IOException if getting the JAR file for this
     * connection causes an IOException to be thrown.
     *
     * @see #getJarFile
     * @see #getJarEntry
     */
    public JarEntry getJarEntry() throws IOException {
        return getJarFile().getJarEntry(entryName);
    }

    /**
     * Return the Attributes object for this connection if the URL
     * for it points to a JAR file entry, null otherwise.
     *
     * @return the Attributes object for this connection if the URL
     * for it points to a JAR file entry, null otherwise.
     *
     * @exception IOException if getting the JAR entry causes an
     * IOException to be thrown.
     *
     * @see #getJarEntry
     */
    public Attributes getAttributes() throws IOException {
        JarEntry e = getJarEntry();
        return e != null ? e.getAttributes() : null;
    }

    /**
     * Returns the main Attributes for the JAR file for this
     * connection.
     *
     * @return the main Attributes for the JAR file for this
     * connection.
     *
     * @exception IOException if getting the manifest causes an
     * IOException to be thrown.
     *
     * @see #getJarFile
     * @see #getManifest
     */
    public Attributes getMainAttributes() throws IOException {
        Manifest man = getManifest();
        return man != null ? man.getMainAttributes() : null;
    }

    /**
     * Return the Certificate object for this connection if the URL
     * for it points to a JAR file entry, null otherwise. This method
     * can only be called once
     * the connection has been completely verified by reading
     * from the input stream until the end of the stream has been
     * reached. Otherwise, this method will return {@code null}
     *
     * @return the Certificate object for this connection if the URL
     * for it points to a JAR file entry, null otherwise.
     *
     * @exception IOException if getting the JAR entry causes an
     * IOException to be thrown.
     *
     * @see #getJarEntry
     */
    public java.security.cert.Certificate[] getCertificates()
         throws IOException
    {
        JarEntry e = getJarEntry();
        return e != null ? e.getCertificates() : null;
    }
}
