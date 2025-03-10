/*
 * Copyright (c) 1999, 2013, Oracle and/or its affiliates. All rights reserved.
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

import java.awt.image.RenderedImage;
import java.io.IOException;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ImageReaderWriterSpi;
import javax.imageio.stream.ImageOutputStream;

/**
 * The service provider interface (SPI) for <code>ImageWriter</code>s.
 * For more information on service provider classes, see the class comment
 * for the <code>IIORegistry</code> class.
 *
 * <p> Each <code>ImageWriterSpi</code> provides several types of information
 * about the <code>ImageWriter</code> class with which it is associated.
 *
 * <p> The name of the vendor who defined the SPI class and a
 * brief description of the class are available via the
 * <code>getVendorName</code>, <code>getDescription</code>,
 * and <code>getVersion</code> methods.
 * These methods may be internationalized to provide locale-specific
 * output.  These methods are intended mainly to provide short,
 * human-writable information that might be used to organize a pop-up
 * menu or other list.
 *
 * <p> Lists of format names, file suffixes, and MIME types associated
 * with the service may be obtained by means of the
 * <code>getFormatNames</code>, <code>getFileSuffixes</code>, and
 * <code>getMIMEType</code> methods.  These methods may be used to
 * identify candidate <code>ImageWriter</code>s for writing a
 * particular file or stream based on manual format selection, file
 * naming, or MIME associations.
 *
 * <p> A more reliable way to determine which <code>ImageWriter</code>s
 * are likely to be able to parse a particular data stream is provided
 * by the <code>canEncodeImage</code> method.  This methods allows the
 * service provider to inspect the actual image contents.
 *
 * <p> Finally, an instance of the <code>ImageWriter</code> class
 * associated with this service provider may be obtained by calling
 * the <code>createWriterInstance</code> method.  Any heavyweight
 * initialization, such as the loading of native libraries or creation
 * of large tables, should be deferred at least until the first
 * invocation of this method.
 *
 * @see IIORegistry
 * @see ImageTypeSpecifier
 * @see ImageWriter
 *
 */
public abstract class ImageWriterSpi extends ImageReaderWriterSpi {

    /**
     * A single-element array, initially containing
     * <code>ImageOutputStream.class</code>, to be returned from
     * <code>getOutputTypes</code>.
     * @deprecated Instead of using this field, directly create
     * the equivalent array <code>{ ImageOutputStream.class }</code>.
     */
    @Deprecated
    public static final Class[] STANDARD_OUTPUT_TYPE =
        { ImageOutputStream.class };

    /**
     * An array of <code>Class</code> objects to be returned from
     * <code>getOutputTypes</code>, initially <code>null</code>.
     */
    protected Class[] outputTypes = null;

    /**
     * An array of strings to be returned from
     * <code>getImageReaderSpiNames</code>, initially
     * <code>null</code>.
     */
    protected String[] readerSpiNames = null;

    /**
     * The <code>Class</code> of the writer, initially
     * <code>null</code>.
     */
    private Class writerClass = null;

    /**
     * Constructs a blank <code>ImageWriterSpi</code>.  It is up to
     * the subclass to initialize instance variables and/or override
     * method implementations in order to provide working versions of
     * all methods.
     */
    protected ImageWriterSpi() {
    }

    /**
     * Constructs an <code>ImageWriterSpi</code> with a given
     * set of values.
     *
     * @param vendorName the vendor name, as a non-<code>null</code>
     * <code>String</code>.
     * @param version a version identifier, as a non-<code>null</code>
     * <code>String</code>.
     * @param names a non-<code>null</code> array of
     * <code>String</code>s indicating the format names.  At least one
     * entry must be present.
     * @param suffixes an array of <code>String</code>s indicating the
     * common file suffixes.  If no suffixes are defined,
     * <code>null</code> should be supplied.  An array of length 0
     * will be normalized to <code>null</code>.
     * @param MIMETypes an array of <code>String</code>s indicating
     * the format's MIME types.  If no suffixes are defined,
     * <code>null</code> should be supplied.  An array of length 0
     * will be normalized to <code>null</code>.
     * @param writerClassName the fully-qualified name of the
     * associated <code>ImageWriterSpi</code> class, as a
     * non-<code>null</code> <code>String</code>.
     * @param outputTypes an array of <code>Class</code> objects of
     * length at least 1 indicating the legal output types.
     * @param readerSpiNames an array <code>String</code>s of length
     * at least 1 naming the classes of all associated
     * <code>ImageReader</code>s, or <code>null</code>.  An array of
     * length 0 is normalized to <code>null</code>.
     * @param supportsStandardStreamMetadataFormat a
     * <code>boolean</code> that indicates whether a stream metadata
     * object can use trees described by the standard metadata format.
     * @param nativeStreamMetadataFormatName a
     * <code>String</code>, or <code>null</code>, to be returned from
     * <code>getNativeStreamMetadataFormatName</code>.
     * @param nativeStreamMetadataFormatClassName a
     * <code>String</code>, or <code>null</code>, to be used to instantiate
     * a metadata format object to be returned from
     * <code>getNativeStreamMetadataFormat</code>.
     * @param extraStreamMetadataFormatNames an array of
     * <code>String</code>s, or <code>null</code>, to be returned from
     * <code>getExtraStreamMetadataFormatNames</code>.  An array of length
     * 0 is normalized to <code>null</code>.
     * @param extraStreamMetadataFormatClassNames an array of
     * <code>String</code>s, or <code>null</code>, to be used to instantiate
     * a metadata format object to be returned from
     * <code>getStreamMetadataFormat</code>.  An array of length
     * 0 is normalized to <code>null</code>.
     * @param supportsStandardImageMetadataFormat a
     * <code>boolean</code> that indicates whether an image metadata
     * object can use trees described by the standard metadata format.
     * @param nativeImageMetadataFormatName a
     * <code>String</code>, or <code>null</code>, to be returned from
     * <code>getNativeImageMetadataFormatName</code>.
     * @param nativeImageMetadataFormatClassName a
     * <code>String</code>, or <code>null</code>, to be used to instantiate
     * a metadata format object to be returned from
     * <code>getNativeImageMetadataFormat</code>.
     * @param extraImageMetadataFormatNames an array of
     * <code>String</code>s to be returned from
     * <code>getExtraImageMetadataFormatNames</code>.  An array of length 0
     * is normalized to <code>null</code>.
     * @param extraImageMetadataFormatClassNames an array of
     * <code>String</code>s, or <code>null</code>, to be used to instantiate
     * a metadata format object to be returned from
     * <code>getImageMetadataFormat</code>.  An array of length
     * 0 is normalized to <code>null</code>.
     *
     * @exception IllegalArgumentException if <code>vendorName</code>
     * is <code>null</code>.
     * @exception IllegalArgumentException if <code>version</code>
     * is <code>null</code>.
     * @exception IllegalArgumentException if <code>names</code>
     * is <code>null</code> or has length 0.
     * @exception IllegalArgumentException if <code>writerClassName</code>
     * is <code>null</code>.
     * @exception IllegalArgumentException if <code>outputTypes</code>
     * is <code>null</code> or has length 0.
     */
    public ImageWriterSpi(String vendorName,
                          String version,
                          String[] names,
                          String[] suffixes,
                          String[] MIMETypes,
                          String writerClassName,
                          Class[] outputTypes,
                          String[] readerSpiNames,
                          boolean supportsStandardStreamMetadataFormat,
                          String nativeStreamMetadataFormatName,
                          String nativeStreamMetadataFormatClassName,
                          String[] extraStreamMetadataFormatNames,
                          String[] extraStreamMetadataFormatClassNames,
                          boolean supportsStandardImageMetadataFormat,
                          String nativeImageMetadataFormatName,
                          String nativeImageMetadataFormatClassName,
                          String[] extraImageMetadataFormatNames,
                          String[] extraImageMetadataFormatClassNames) {
        super(vendorName, version,
              names, suffixes, MIMETypes, writerClassName,
              supportsStandardStreamMetadataFormat,
              nativeStreamMetadataFormatName,
              nativeStreamMetadataFormatClassName,
              extraStreamMetadataFormatNames,
              extraStreamMetadataFormatClassNames,
              supportsStandardImageMetadataFormat,
              nativeImageMetadataFormatName,
              nativeImageMetadataFormatClassName,
              extraImageMetadataFormatNames,
              extraImageMetadataFormatClassNames);

        if (outputTypes == null) {
            throw new IllegalArgumentException
                ("outputTypes == null!");
        }
        if (outputTypes.length == 0) {
            throw new IllegalArgumentException
                ("outputTypes.length == 0!");
        }

        this.outputTypes = (outputTypes == STANDARD_OUTPUT_TYPE) ?
            new Class<?>[] { ImageOutputStream.class } :
            outputTypes.clone();

        // If length == 0, leave it null
        if (readerSpiNames != null && readerSpiNames.length > 0) {
            this.readerSpiNames = (String[])readerSpiNames.clone();
        }
    }

    /**
     * Returns <code>true</code> if the format that this writer
     * outputs preserves pixel data bit-accurately.  The default
     * implementation returns <code>true</code>.
     *
     * @return <code>true</code> if the format preserves full pixel
     * accuracy.
     */
    public boolean isFormatLossless() {
        return true;
    }

    /**
     * Returns an array of <code>Class</code> objects indicating what
     * types of objects may be used as arguments to the writer's
     * <code>setOutput</code> method.
     *
     * <p> For most writers, which only output to an
     * <code>ImageOutputStream</code>, a single-element array
     * containing <code>ImageOutputStream.class</code> should be
     * returned.
     *
     * @return a non-<code>null</code> array of
     * <code>Class</code>objects of length at least 1.
     */
    public Class[] getOutputTypes() {
        return (Class[])outputTypes.clone();
    }

    /**
     * Returns <code>true</code> if the <code>ImageWriter</code>
     * implementation associated with this service provider is able to
     * encode an image with the given layout.  The layout
     * (<i>i.e.</i>, the image's <code>SampleModel</code> and
     * <code>ColorModel</code>) is described by an
     * <code>ImageTypeSpecifier</code> object.
     *
     * <p> A return value of <code>true</code> is not an absolute
     * guarantee of successful encoding; the encoding process may still
     * produce errors due to factors such as I/O errors, inconsistent
     * or malformed data structures, etc.  The intent is that a
     * reasonable inspection of the basic structure of the image be
     * performed in order to determine if it is within the scope of
     * the encoding format.  For example, a service provider for a
     * format that can only encode greyscale would return
     * <code>false</code> if handed an RGB <code>BufferedImage</code>.
     * Similarly, a service provider for a format that can encode
     * 8-bit RGB imagery might refuse to encode an image with an
     * associated alpha channel.
     *
     * <p> Different <code>ImageWriter</code>s, and thus service
     * providers, may choose to be more or less strict.  For example,
     * they might accept an image with premultiplied alpha even though
     * it will have to be divided out of each pixel, at some loss of
     * precision, in order to be stored.
     *
     * @param type an <code>ImageTypeSpecifier</code> specifying the
     * layout of the image to be written.
     *
     * @return <code>true</code> if this writer is likely to be able
     * to encode images with the given layout.
     *
     * @exception IllegalArgumentException if <code>type</code>
     * is <code>null</code>.
     */
    public abstract boolean canEncodeImage(ImageTypeSpecifier type);

    /**
     * Returns <code>true</code> if the <code>ImageWriter</code>
     * implementation associated with this service provider is able to
     * encode the given <code>RenderedImage</code> instance.  Note
     * that this includes instances of
     * <code>java.awt.image.BufferedImage</code>.
     *
     * <p> See the discussion for
     * <code>canEncodeImage(ImageTypeSpecifier)</code> for information
     * on the semantics of this method.
     *
     * @param im an instance of <code>RenderedImage</code> to be encoded.
     *
     * @return <code>true</code> if this writer is likely to be able
     * to encode this image.
     *
     * @exception IllegalArgumentException if <code>im</code>
     * is <code>null</code>.
     */
    public boolean canEncodeImage(RenderedImage im) {
        return canEncodeImage(ImageTypeSpecifier.createFromRenderedImage(im));
    }

    /**
     * Returns an instance of the <code>ImageWriter</code>
     * implementation associated with this service provider.
     * The returned object will initially be in an initial state as if
     * its <code>reset</code> method had been called.
     *
     * <p> The default implementation simply returns
     * <code>createWriterInstance(null)</code>.
     *
     * @return an <code>ImageWriter</code> instance.
     *
     * @exception IOException if an error occurs during loading,
     * or initialization of the writer class, or during instantiation
     * or initialization of the writer object.
     */
    public ImageWriter createWriterInstance() throws IOException {
        return createWriterInstance(null);
    }

    /**
     * Returns an instance of the <code>ImageWriter</code>
     * implementation associated with this service provider.
     * The returned object will initially be in an initial state
     * as if its <code>reset</code> method had been called.
     *
     * <p> An <code>Object</code> may be supplied to the plug-in at
     * construction time.  The nature of the object is entirely
     * plug-in specific.
     *
     * <p> Typically, a plug-in will implement this method using code
     * such as <code>return new MyImageWriter(this)</code>.
     *
     * @param extension a plug-in specific extension object, which may
     * be <code>null</code>.
     *
     * @return an <code>ImageWriter</code> instance.
     *
     * @exception IOException if the attempt to instantiate
     * the writer fails.
     * @exception IllegalArgumentException if the
     * <code>ImageWriter</code>'s constructor throws an
     * <code>IllegalArgumentException</code> to indicate that the
     * extension object is unsuitable.
     */
    public abstract ImageWriter createWriterInstance(Object extension)
        throws IOException;

    /**
     * Returns <code>true</code> if the <code>ImageWriter</code> object
     * passed in is an instance of the <code>ImageWriter</code>
     * associated with this service provider.
     *
     * @param writer an <code>ImageWriter</code> instance.
     *
     * @return <code>true</code> if <code>writer</code> is recognized
     *
     * @exception IllegalArgumentException if <code>writer</code> is
     * <code>null</code>.
     */
    public boolean isOwnWriter(ImageWriter writer) {
        if (writer == null) {
            throw new IllegalArgumentException("writer == null!");
        }
        String name = writer.getClass().getName();
        return name.equals(pluginClassName);
    }

    /**
     * Returns an array of <code>String</code>s containing all the
     * fully qualified names of all the <code>ImageReaderSpi</code>
     * classes that can understand the internal metadata
     * representation used by the <code>ImageWriter</code> associated
     * with this service provider, or <code>null</code> if there are
     * no such <code>ImageReaders</code> specified.  If a
     * non-<code>null</code> value is returned, it must have non-zero
     * length.
     *
     * <p> The first item in the array must be the name of the service
     * provider for the "preferred" reader, as it will be used to
     * instantiate the <code>ImageReader</code> returned by
     * <code>ImageIO.getImageReader(ImageWriter)</code>.
     *
     * <p> This mechanism may be used to obtain
     * <code>ImageReaders</code> that will generated non-pixel
     * meta-data (see <code>IIOExtraDataInfo</code>) in a structure
     * understood by an <code>ImageWriter</code>.  By reading the
     * image and obtaining this data from one of the
     * <code>ImageReaders</code> obtained with this method and passing
     * it on to the <code>ImageWriter</code>, a client program can
     * read an image, modify it in some way, and write it back out
     * preserving all meta-data, without having to understand anything
     * about the internal structure of the meta-data, or even about
     * the image format.
     *
     * @return an array of <code>String</code>s of length at least 1
     * containing names of <code>ImageReaderSpi</code>s, or
     * <code>null</code>.
     *
     * @see javax.imageio.ImageIO#getImageReader(ImageWriter)
     * @see ImageReaderSpi#getImageWriterSpiNames()
     */
    public String[] getImageReaderSpiNames() {
        return readerSpiNames == null ?
            null : (String[])readerSpiNames.clone();
    }
}
