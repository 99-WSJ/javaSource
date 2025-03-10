/*
 * Copyright (c) 2000, 2004, Oracle and/or its affiliates. All rights reserved.
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

package java8.javax.imageio.spi;

import java.io.File;
import java.io.IOException;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.IIOServiceProvider;
import javax.imageio.stream.ImageOutputStream;

/**
 * The service provider interface (SPI) for
 * <code>ImageOutputStream</code>s.  For more information on service
 * provider interfaces, see the class comment for the
 * <code>IIORegistry</code> class.
 *
 * <p> This interface allows arbitrary objects to be "wrapped" by
 * instances of <code>ImageOutputStream</code>.  For example, a
 * particular <code>ImageOutputStreamSpi</code> might allow a generic
 * <code>OutputStream</code> to be used as a destination; another
 * might output to a <code>File</code> or to a device such as a serial
 * port.
 *
 * <p> By treating the creation of <code>ImageOutputStream</code>s as
 * a pluggable service, it becomes possible to handle future output
 * destinations without changing the API.  Also, high-performance
 * implementations of <code>ImageOutputStream</code> (for example,
 * native implementations for a particular platform) can be installed
 * and used transparently by applications.
 *
 * @see IIORegistry
 * @see ImageOutputStream
 *
 */
public abstract class ImageOutputStreamSpi extends IIOServiceProvider {

    /**
     * A <code>Class</code> object indicating the legal object type
     * for use by the <code>createInputStreamInstance</code> method.
     */
    protected Class<?> outputClass;

    /**
     * Constructs a blank <code>ImageOutputStreamSpi</code>.  It is up
     * to the subclass to initialize instance variables and/or
     * override method implementations in order to provide working
     * versions of all methods.
     */
    protected ImageOutputStreamSpi() {
    }

    /**
     * Constructs an <code>ImageOutputStreamSpi</code> with a given
     * set of values.
     *
     * @param vendorName the vendor name.
     * @param version a version identifier.
     * @param outputClass a <code>Class</code> object indicating the
     * legal object type for use by the
     * <code>createOutputStreamInstance</code> method.
     *
     * @exception IllegalArgumentException if <code>vendorName</code>
     * is <code>null</code>.
     * @exception IllegalArgumentException if <code>version</code>
     * is <code>null</code>.
    */
    public ImageOutputStreamSpi(String vendorName,
                                String version,
                                Class<?> outputClass) {
        super(vendorName, version);
        this.outputClass = outputClass;
    }

    /**
     * Returns a <code>Class</code> object representing the class or
     * interface type that must be implemented by an output
     * destination in order to be "wrapped" in an
     * <code>ImageOutputStream</code> via the
     * <code>createOutputStreamInstance</code> method.
     *
     * <p> Typical return values might include
     * <code>OutputStream.class</code> or <code>File.class</code>, but
     * any class may be used.
     *
     * @return a <code>Class</code> variable.
     *
     * @see #createOutputStreamInstance(Object, boolean, File)
     */
    public Class<?> getOutputClass() {
        return outputClass;
    }

    /**
     * Returns <code>true</code> if the <code>ImageOutputStream</code>
     * implementation associated with this service provider can
     * optionally make use of a cache <code>File</code> for improved
     * performance and/or memory footrprint.  If <code>false</code>,
     * the value of the <code>cacheFile</code> argument to
     * <code>createOutputStreamInstance</code> will be ignored.
     *
     * <p> The default implementation returns <code>false</code>.
     *
     * @return <code>true</code> if a cache file can be used by the
     * output streams created by this service provider.
     */
    public boolean canUseCacheFile() {
        return false;
    }

    /**
     * Returns <code>true</code> if the <code>ImageOutputStream</code>
     * implementation associated with this service provider requires
     * the use of a cache <code>File</code>.
     *
     * <p> The default implementation returns <code>false</code>.
     *
     * @return <code>true</code> if a cache file is needed by the
     * output streams created by this service provider.
     */
    public boolean needsCacheFile() {
        return false;
    }

    /**
     * Returns an instance of the <code>ImageOutputStream</code>
     * implementation associated with this service provider.  If the
     * use of a cache file is optional, the <code>useCache</code>
     * parameter will be consulted.  Where a cache is required, or
     * not applicable, the value of <code>useCache</code> will be ignored.
     *
     * @param output an object of the class type returned by
     * <code>getOutputClass</code>.
     * @param useCache a <code>boolean</code> indicating whether a
     * cache file should be used, in cases where it is optional.
     * @param cacheDir a <code>File</code> indicating where the
     * cache file should be created, or <code>null</code> to use the
     * system directory.
     *
     * @return an <code>ImageOutputStream</code> instance.
     *
     * @exception IllegalArgumentException if <code>output</code> is
     * not an instance of the correct class or is <code>null</code>.
     * @exception IllegalArgumentException if a cache file is needed,
     * but <code>cacheDir</code> is non-<code>null</code> and is not a
     * directory.
     * @exception IOException if a cache file is needed but cannot be
     * created.
     *
     * @see #getOutputClass
     */
    public abstract
        ImageOutputStream createOutputStreamInstance(Object output,
                                                     boolean useCache,
                                                     File cacheDir)
        throws IOException;

    /**
     * Returns an instance of the <code>ImageOutputStream</code>
     * implementation associated with this service provider.  A cache
     * file will be created in the system-dependent default
     * temporary-file directory, if needed.
     *
     * @param output an object of the class type returned by
     * <code>getOutputClass</code>.
     *
     * @return an <code>ImageOutputStream</code> instance.
     *
     * @exception IllegalArgumentException if <code>output</code> is
     * not an instance of the correct class or is <code>null</code>.
     * @exception IOException if a cache file is needed but cannot be
     * created.
     *
     * @see #getOutputClass()
     */
    public ImageOutputStream createOutputStreamInstance(Object output)
        throws IOException {
        return createOutputStreamInstance(output, true, null);
    }
}
