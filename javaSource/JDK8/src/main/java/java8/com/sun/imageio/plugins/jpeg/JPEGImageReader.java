/*
 * Copyright (c) 2000, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java8.sun.imageio.plugins.jpeg;

import com.sun.imageio.plugins.jpeg.JPEG;
import com.sun.imageio.plugins.jpeg.JPEGMetadata;
import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;

import javax.imageio.IIOException;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.plugins.jpeg.JPEGHuffmanTable;
import javax.imageio.plugins.jpeg.JPEGImageReadParam;
import javax.imageio.plugins.jpeg.JPEGQTable;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.color.CMMException;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class JPEGImageReader extends ImageReader {

    private boolean debug = false;

    /**
     * The following variable contains a pointer to the IJG library
     * structure for this reader.  It is assigned in the constructor
     * and then is passed in to every native call.  It is set to 0
     * by dispose to avoid disposing twice.
     */
    private long structPointer = 0;

    /** The input stream we read from */
    private ImageInputStream iis = null;

    /**
     * List of stream positions for images, reinitialized every time
     * a new input source is set.
     */
    private List imagePositions = null;

    /**
     * The number of images in the stream, or 0.
     */
    private int numImages = 0;

    static {
        java.security.AccessController.doPrivileged(
            new java.security.PrivilegedAction<Void>() {
                public Void run() {
                    System.loadLibrary("jpeg");
                    return null;
                }
            });
        initReaderIDs(ImageInputStream.class,
                      JPEGQTable.class,
                      JPEGHuffmanTable.class);
    }

    // The following warnings are converted to strings when used
    // as keys to get localized resources from JPEGImageReaderResources
    // and its children.

    /**
     * Warning code to be passed to warningOccurred to indicate
     * that the EOI marker is missing from the end of the stream.
     * This usually signals that the stream is corrupted, but
     * everything up to the last MCU should be usable.
     */
    protected static final int WARNING_NO_EOI = 0;

    /**
     * Warning code to be passed to warningOccurred to indicate
     * that a JFIF segment was encountered inside a JFXX JPEG
     * thumbnail and is being ignored.
     */
    protected static final int WARNING_NO_JFIF_IN_THUMB = 1;

    /**
     * Warning code to be passed to warningOccurred to indicate
     * that embedded ICC profile is invalid and will be ignored.
     */
    protected static final int WARNING_IGNORE_INVALID_ICC = 2;

    private static final int MAX_WARNING = WARNING_IGNORE_INVALID_ICC;

    /**
     * Image index of image for which header information
     * is available.
     */
    private int currentImage = -1;

    // The following is copied out from C after reading the header.
    // Unlike metadata, which may never be retrieved, we need this
    // if we are to read an image at all.

    /** Set by setImageData native code callback */
    private int width;
    /** Set by setImageData native code callback */
    private int height;
    /**
     * Set by setImageData native code callback.  A modified
     * IJG+NIFTY colorspace code.
     */
    private int colorSpaceCode;
    /**
     * Set by setImageData native code callback.  A modified
     * IJG+NIFTY colorspace code.
     */
    private int outColorSpaceCode;
    /** Set by setImageData native code callback */
    private int numComponents;
    /** Set by setImageData native code callback */
    private ColorSpace iccCS = null;


    /** If we need to post-convert in Java, convert with this op */
    private ColorConvertOp convert = null;

    /** The image we are going to fill */
    private BufferedImage image = null;

    /** An intermediate Raster to hold decoded data */
    private WritableRaster raster = null;

    /** A view of our target Raster that we can setRect to */
    private WritableRaster target = null;

    /** The databuffer for the above Raster */
    private DataBufferByte buffer = null;

    /** The region in the destination where we will write pixels */
    private Rectangle destROI = null;

    /** The list of destination bands, if any */
    private int [] destinationBands = null;

    /** Stream metadata, cached, even when the stream is changed. */
    private JPEGMetadata streamMetadata = null;

    /** Image metadata, valid for the imageMetadataIndex only. */
    private JPEGMetadata imageMetadata = null;
    private int imageMetadataIndex = -1;

    /**
     * Set to true every time we seek in the stream; used to
     * invalidate the native buffer contents in C.
     */
    private boolean haveSeeked = false;

    /**
     * Tables that have been read from a tables-only image at the
     * beginning of a stream.
     */
    private JPEGQTable [] abbrevQTables = null;
    private JPEGHuffmanTable[] abbrevDCHuffmanTables = null;
    private JPEGHuffmanTable[] abbrevACHuffmanTables = null;

    private int minProgressivePass = 0;
    private int maxProgressivePass = Integer.MAX_VALUE;

    /**
     * Variables used by progress monitoring.
     */
    private static final int UNKNOWN = -1;  // Number of passes
    private static final int MIN_ESTIMATED_PASSES = 10; // IJG default
    private int knownPassCount = UNKNOWN;
    private int pass = 0;
    private float percentToDate = 0.0F;
    private float previousPassPercentage = 0.0F;
    private int progInterval = 0;

    /**
     * Set to true once stream has been checked for stream metadata
     */
    private boolean tablesOnlyChecked = false;

    /** The referent to be registered with the Disposer. */
    private Object disposerReferent = new Object();

    /** The DisposerRecord that handles the actual disposal of this reader. */
    private DisposerRecord disposerRecord;

    /** Sets up static C structures. */
    private static native void initReaderIDs(Class iisClass,
                                             Class qTableClass,
                                             Class huffClass);

    public JPEGImageReader(ImageReaderSpi originator) {
        super(originator);
        structPointer = initJPEGImageReader();
        disposerRecord = new JPEGReaderDisposerRecord(structPointer);
        Disposer.addRecord(disposerReferent, disposerRecord);
    }

    /** Sets up per-reader C structure and returns a pointer to it. */
    private native long initJPEGImageReader();

    /**
     * Called by the native code or other classes to signal a warning.
     * The code is used to lookup a localized message to be used when
     * sending warnings to listeners.
     */
    protected void warningOccurred(int code) {
        cbLock.lock();
        try {
            if ((code < 0) || (code > MAX_WARNING)){
                throw new InternalError("Invalid warning index");
            }
            processWarningOccurred
                ("com.sun.imageio.plugins.jpeg.JPEGImageReaderResources",
                 Integer.toString(code));
        } finally {
            cbLock.unlock();
        }
    }

    /**
     * The library has it's own error facility that emits warning messages.
     * This routine is called by the native code when it has already
     * formatted a string for output.
     * XXX  For truly complete localization of all warning messages,
     * the sun_jpeg_output_message routine in the native code should
     * send only the codes and parameters to a method here in Java,
     * which will then format and send the warnings, using localized
     * strings.  This method will have to deal with all the parameters
     * and formats (%u with possibly large numbers, %02d, %02x, etc.)
     * that actually occur in the JPEG library.  For now, this prevents
     * library warnings from being printed to stderr.
     */
    protected void warningWithMessage(String msg) {
        cbLock.lock();
        try {
            processWarningOccurred(msg);
        } finally {
            cbLock.unlock();
        }
    }

    public void setInput(Object input,
                         boolean seekForwardOnly,
                         boolean ignoreMetadata)
    {
        setThreadLock();
        try {
            cbLock.check();

            super.setInput(input, seekForwardOnly, ignoreMetadata);
            this.ignoreMetadata = ignoreMetadata;
            resetInternalState();
            iis = (ImageInputStream) input; // Always works
            setSource(structPointer);
        } finally {
            clearThreadLock();
        }
    }

    /**
     * This method is called from native code in order to fill
     * native input buffer.
     *
     * We block any attempt to change the reading state during this
     * method, in order to prevent a corruption of the native decoder
     * state.
     *
     * @return number of bytes read from the stream.
     */
    private int readInputData(byte[] buf, int off, int len) throws IOException {
        cbLock.lock();
        try {
            return iis.read(buf, off, len);
        } finally {
            cbLock.unlock();
        }
    }

    /**
     * This method is called from the native code in order to
     * skip requested number of bytes in the input stream.
     *
     * @param n
     * @return
     * @throws IOException
     */
    private long skipInputBytes(long n) throws IOException {
        cbLock.lock();
        try {
            return iis.skipBytes(n);
        } finally {
            cbLock.unlock();
        }
    }

    private native void setSource(long structPointer);

    private void checkTablesOnly() throws IOException {
        if (debug) {
            System.out.println("Checking for tables-only image");
        }
        long savePos = iis.getStreamPosition();
        if (debug) {
            System.out.println("saved pos is " + savePos);
            System.out.println("length is " + iis.length());
        }
        // Read the first header
        boolean tablesOnly = readNativeHeader(true);
        if (tablesOnly) {
            if (debug) {
                System.out.println("tables-only image found");
                long pos = iis.getStreamPosition();
                System.out.println("pos after return from native is " + pos);
            }
            // This reads the tables-only image twice, once from C
            // and once from Java, but only if ignoreMetadata is false
            if (ignoreMetadata == false) {
                iis.seek(savePos);
                haveSeeked = true;
                streamMetadata = new JPEGMetadata(true, false,
                                                  iis, this);
                long pos = iis.getStreamPosition();
                if (debug) {
                    System.out.println
                        ("pos after constructing stream metadata is " + pos);
                }
            }
            // Now we are at the first image if there are any, so add it
            // to the list
            if (hasNextImage()) {
                imagePositions.add(new Long(iis.getStreamPosition()));
            }
        } else { // Not tables only, so add original pos to the list
            imagePositions.add(new Long(savePos));
            // And set current image since we've read it now
            currentImage = 0;
        }
        if (seekForwardOnly) {
            Long pos = (Long) imagePositions.get(imagePositions.size()-1);
            iis.flushBefore(pos.longValue());
        }
        tablesOnlyChecked = true;
    }

    public int getNumImages(boolean allowSearch) throws IOException {
        setThreadLock();
        try { // locked thread
            cbLock.check();

            return getNumImagesOnThread(allowSearch);
        } finally {
            clearThreadLock();
        }
    }

    private int getNumImagesOnThread(boolean allowSearch)
      throws IOException {
        if (numImages != 0) {
            return numImages;
        }
        if (iis == null) {
            throw new IllegalStateException("Input not set");
        }
        if (allowSearch == true) {
            if (seekForwardOnly) {
                throw new IllegalStateException(
                    "seekForwardOnly and allowSearch can't both be true!");
            }
            // Otherwise we have to read the entire stream

            if (!tablesOnlyChecked) {
                checkTablesOnly();
            }

            iis.mark();

            gotoImage(0);

            com.sun.imageio.plugins.jpeg.JPEGBuffer buffer = new com.sun.imageio.plugins.jpeg.JPEGBuffer(iis);
            buffer.loadBuf(0);

            boolean done = false;
            while (!done) {
                done = buffer.scanForFF(this);
                switch (buffer.buf[buffer.bufPtr] & 0xff) {
                case JPEG.SOI:
                    numImages++;
                    // FALL THROUGH to decrement buffer vars
                    // This first set doesn't have a length
                case 0: // not a marker, just a data 0xff
                case JPEG.RST0:
                case JPEG.RST1:
                case JPEG.RST2:
                case JPEG.RST3:
                case JPEG.RST4:
                case JPEG.RST5:
                case JPEG.RST6:
                case JPEG.RST7:
                case JPEG.EOI:
                    buffer.bufAvail--;
                    buffer.bufPtr++;
                    break;
                    // All the others have a length
                default:
                    buffer.bufAvail--;
                    buffer.bufPtr++;
                    buffer.loadBuf(2);
                    int length = ((buffer.buf[buffer.bufPtr++] & 0xff) << 8) |
                        (buffer.buf[buffer.bufPtr++] & 0xff);
                    buffer.bufAvail -= 2;
                    length -= 2; // length includes itself
                    buffer.skipData(length);
                }
            }


            iis.reset();

            return numImages;
        }

        return -1;  // Search is necessary for JPEG
    }

    /**
     * Sets the input stream to the start of the requested image.
     * <pre>
     * @exception IllegalStateException if the input source has not been
     * set.
     * @exception IndexOutOfBoundsException if the supplied index is
     * out of bounds.
     * </pre>
     */
    private void gotoImage(int imageIndex) throws IOException {
        if (iis == null) {
            throw new IllegalStateException("Input not set");
        }
        if (imageIndex < minIndex) {
            throw new IndexOutOfBoundsException();
        }
        if (!tablesOnlyChecked) {
            checkTablesOnly();
        }
        if (imageIndex < imagePositions.size()) {
            iis.seek(((Long)(imagePositions.get(imageIndex))).longValue());
        } else {
            // read to start of image, saving positions
            // First seek to the last position we already have, and skip the
            // entire image
            Long pos = (Long) imagePositions.get(imagePositions.size()-1);
            iis.seek(pos.longValue());
            skipImage();
            // Now add all intervening positions, skipping images
            for (int index = imagePositions.size();
                 index <= imageIndex;
                 index++) {
                // Is there an image?
                if (!hasNextImage()) {
                    throw new IndexOutOfBoundsException();
                }
                pos = new Long(iis.getStreamPosition());
                imagePositions.add(pos);
                if (seekForwardOnly) {
                    iis.flushBefore(pos.longValue());
                }
                if (index < imageIndex) {
                    skipImage();
                }  // Otherwise we are where we want to be
            }
        }

        if (seekForwardOnly) {
            minIndex = imageIndex;
        }

        haveSeeked = true;  // No way is native buffer still valid
    }

    /**
     * Skip over a complete image in the stream, leaving the stream
     * positioned such that the next byte to be read is the first
     * byte of the next image.  For JPEG, this means that we read
     * until we encounter an EOI marker or until the end of the stream.
     * If the stream ends before an EOI marker is encountered, an
     * IndexOutOfBoundsException is thrown.
     */
    private void skipImage() throws IOException {
        if (debug) {
            System.out.println("skipImage called");
        }
        boolean foundFF = false;
        for (int byteval = iis.read();
             byteval != -1;
             byteval = iis.read()) {

            if (foundFF == true) {
                if (byteval == JPEG.EOI) {
                    return;
                }
            }
            foundFF = (byteval == 0xff) ? true : false;
        }
        throw new IndexOutOfBoundsException();
    }

    /**
     * Returns <code>true</code> if there is an image beyond
     * the current stream position.  Does not disturb the
     * stream position.
     */
    private boolean hasNextImage() throws IOException {
        if (debug) {
            System.out.print("hasNextImage called; returning ");
        }
        iis.mark();
        boolean foundFF = false;
        for (int byteval = iis.read();
             byteval != -1;
             byteval = iis.read()) {

            if (foundFF == true) {
                if (byteval == JPEG.SOI) {
                    iis.reset();
                    if (debug) {
                        System.out.println("true");
                    }
                    return true;
                }
            }
            foundFF = (byteval == 0xff) ? true : false;
        }
        // We hit the end of the stream before we hit an SOI, so no image
        iis.reset();
        if (debug) {
            System.out.println("false");
        }
        return false;
    }

    /**
     * Push back the given number of bytes to the input stream.
     * Called by the native code at the end of each image so
     * that the next one can be identified from Java.
     */
    private void pushBack(int num) throws IOException {
        if (debug) {
            System.out.println("pushing back " + num + " bytes");
        }
        cbLock.lock();
        try {
            iis.seek(iis.getStreamPosition()-num);
            // The buffer is clear after this, so no need to set haveSeeked.
        } finally {
            cbLock.unlock();
        }
    }

    /**
     * Reads header information for the given image, if possible.
     */
    private void readHeader(int imageIndex, boolean reset)
        throws IOException {
        gotoImage(imageIndex);
        readNativeHeader(reset); // Ignore return
        currentImage = imageIndex;
    }

    private boolean readNativeHeader(boolean reset) throws IOException {
        boolean retval = false;
        retval = readImageHeader(structPointer, haveSeeked, reset);
        haveSeeked = false;
        return retval;
    }

    /**
     * Read in the header information starting from the current
     * stream position, returning <code>true</code> if the
     * header was a tables-only image.  After this call, the
     * native IJG decompression struct will contain the image
     * information required by most query calls below
     * (e.g. getWidth, getHeight, etc.), if the header was not
     * a tables-only image.
     * If reset is <code>true</code>, the state of the IJG
     * object is reset so that it can read a header again.
     * This happens automatically if the header was a tables-only
     * image.
     */
    private native boolean readImageHeader(long structPointer,
                                           boolean clearBuffer,
                                           boolean reset)
        throws IOException;

    /*
     * Called by the native code whenever an image header has been
     * read.  Whether we read metadata or not, we always need this
     * information, so it is passed back independently of
     * metadata, which may never be read.
     */
    private void setImageData(int width,
                              int height,
                              int colorSpaceCode,
                              int outColorSpaceCode,
                              int numComponents,
                              byte [] iccData) {
        this.width = width;
        this.height = height;
        this.colorSpaceCode = colorSpaceCode;
        this.outColorSpaceCode = outColorSpaceCode;
        this.numComponents = numComponents;

        if (iccData == null) {
            iccCS = null;
            return;
        }

        ICC_Profile newProfile = null;
        try {
            newProfile = ICC_Profile.getInstance(iccData);
        } catch (IllegalArgumentException e) {
            /*
             * Color profile data seems to be invalid.
             * Ignore this profile.
             */
            iccCS = null;
            warningOccurred(WARNING_IGNORE_INVALID_ICC);

            return;
        }
        byte[] newData = newProfile.getData();

        ICC_Profile oldProfile = null;
        if (iccCS instanceof ICC_ColorSpace) {
            oldProfile = ((ICC_ColorSpace)iccCS).getProfile();
        }
        byte[] oldData = null;
        if (oldProfile != null) {
            oldData = oldProfile.getData();
        }

        /*
         * At the moment we can't rely on the ColorSpace.equals()
         * and ICC_Profile.equals() because they do not detect
         * the case when two profiles are created from same data.
         *
         * So, we have to do data comparison in order to avoid
         * creation of different ColorSpace instances for the same
         * embedded data.
         */
        if (oldData == null ||
            !java.util.Arrays.equals(oldData, newData))
        {
            iccCS = new ICC_ColorSpace(newProfile);
            // verify new color space
            try {
                float[] colors = iccCS.fromRGB(new float[] {1f, 0f, 0f});
            } catch (CMMException e) {
                /*
                 * Embedded profile seems to be corrupted.
                 * Ignore this profile.
                 */
                iccCS = null;
                cbLock.lock();
                try {
                    warningOccurred(WARNING_IGNORE_INVALID_ICC);
                } finally {
                    cbLock.unlock();
                }
            }
        }
    }

    public int getWidth(int imageIndex) throws IOException {
        setThreadLock();
        try {
            if (currentImage != imageIndex) {
                cbLock.check();
                readHeader(imageIndex, true);
            }
            return width;
        } finally {
            clearThreadLock();
        }
    }

    public int getHeight(int imageIndex) throws IOException {
        setThreadLock();
        try {
            if (currentImage != imageIndex) {
                cbLock.check();
                readHeader(imageIndex, true);
            }
            return height;
        } finally {
            clearThreadLock();
        }
    }

    /////////// Color Conversion and Image Types

    /**
     * Return an ImageTypeSpecifier corresponding to the given
     * color space code, or null if the color space is unsupported.
     */
    private com.sun.imageio.plugins.jpeg.ImageTypeProducer getImageType(int code) {
        com.sun.imageio.plugins.jpeg.ImageTypeProducer ret = null;

        if ((code > 0) && (code < JPEG.NUM_JCS_CODES)) {
            ret = com.sun.imageio.plugins.jpeg.ImageTypeProducer.getTypeProducer(code);
        }
        return ret;
    }

    public ImageTypeSpecifier getRawImageType(int imageIndex)
        throws IOException {
        setThreadLock();
        try {
            if (currentImage != imageIndex) {
                cbLock.check();

                readHeader(imageIndex, true);
            }

            // Returns null if it can't be represented
            return getImageType(colorSpaceCode).getType();
        } finally {
            clearThreadLock();
        }
    }

    public Iterator getImageTypes(int imageIndex)
        throws IOException {
        setThreadLock();
        try {
            return getImageTypesOnThread(imageIndex);
        } finally {
            clearThreadLock();
        }
    }

    private Iterator getImageTypesOnThread(int imageIndex)
        throws IOException {
        if (currentImage != imageIndex) {
            cbLock.check();
            readHeader(imageIndex, true);
        }

        // We return an iterator containing the default, any
        // conversions that the library provides, and
        // all the other default types with the same number
        // of components, as we can do these as a post-process.
        // As we convert Rasters rather than images, images
        // with alpha cannot be converted in a post-process.

        // If this image can't be interpreted, this method
        // returns an empty Iterator.

        // Get the raw ITS, if there is one.  Note that this
        // won't always be the same as the default.
        com.sun.imageio.plugins.jpeg.ImageTypeProducer raw = getImageType(colorSpaceCode);

        // Given the encoded colorspace, build a list of ITS's
        // representing outputs you could handle starting
        // with the default.

        ArrayList<com.sun.imageio.plugins.jpeg.ImageTypeProducer> list = new ArrayList<com.sun.imageio.plugins.jpeg.ImageTypeProducer>(1);

        switch (colorSpaceCode) {
        case JPEG.JCS_GRAYSCALE:
            list.add(raw);
            list.add(getImageType(JPEG.JCS_RGB));
            break;
        case JPEG.JCS_RGB:
            list.add(raw);
            list.add(getImageType(JPEG.JCS_GRAYSCALE));
            list.add(getImageType(JPEG.JCS_YCC));
            break;
        case JPEG.JCS_RGBA:
            list.add(raw);
            break;
        case JPEG.JCS_YCC:
            if (raw != null) {  // Might be null if PYCC.pf not installed
                list.add(raw);
                list.add(getImageType(JPEG.JCS_RGB));
            }
            break;
        case JPEG.JCS_YCCA:
            if (raw != null) {  // Might be null if PYCC.pf not installed
                list.add(raw);
            }
            break;
        case JPEG.JCS_YCbCr:
            // As there is no YCbCr ColorSpace, we can't support
            // the raw type.

            // due to 4705399, use RGB as default in order to avoid
            // slowing down of drawing operations with result image.
            list.add(getImageType(JPEG.JCS_RGB));

            if (iccCS != null) {
                list.add(new com.sun.imageio.plugins.jpeg.ImageTypeProducer() {
                    protected ImageTypeSpecifier produce() {
                        return ImageTypeSpecifier.createInterleaved
                         (iccCS,
                          JPEG.bOffsRGB,  // Assume it's for RGB
                          DataBuffer.TYPE_BYTE,
                          false,
                          false);
                    }
                });

            }

            list.add(getImageType(JPEG.JCS_GRAYSCALE));
            list.add(getImageType(JPEG.JCS_YCC));
            break;
        case JPEG.JCS_YCbCrA:  // Default is to convert to RGBA
            // As there is no YCbCr ColorSpace, we can't support
            // the raw type.
            list.add(getImageType(JPEG.JCS_RGBA));
            break;
        }

        return new com.sun.imageio.plugins.jpeg.ImageTypeIterator(list.iterator());
    }

    /**
     * Checks the implied color conversion between the stream and
     * the target image, altering the IJG output color space if necessary.
     * If a java color conversion is required, then this sets up
     * <code>convert</code>.
     * If bands are being rearranged at all (either source or destination
     * bands are specified in the param), then the default color
     * conversions are assumed to be correct.
     * Throws an IIOException if there is no conversion available.
     */
    private void checkColorConversion(BufferedImage image,
                                      ImageReadParam param)
        throws IIOException {

        // If we are rearranging channels at all, the default
        // conversions remain in place.  If the user wants
        // raw channels then he should do this while reading
        // a Raster.
        if (param != null) {
            if ((param.getSourceBands() != null) ||
                (param.getDestinationBands() != null)) {
                // Accept default conversions out of decoder, silently
                return;
            }
        }

        // XXX - We do not currently support any indexed color models,
        // though we could, as IJG will quantize for us.
        // This is a performance and memory-use issue, as
        // users can read RGB and then convert to indexed in Java.

        ColorModel cm = image.getColorModel();

        if (cm instanceof IndexColorModel) {
            throw new IIOException("IndexColorModel not supported");
        }

        // Now check the ColorSpace type against outColorSpaceCode
        // We may want to tweak the default
        ColorSpace cs = cm.getColorSpace();
        int csType = cs.getType();
        convert = null;
        switch (outColorSpaceCode) {
        case JPEG.JCS_GRAYSCALE:  // Its gray in the file
            if  (csType == ColorSpace.TYPE_RGB) { // We want RGB
                // IJG can do this for us more efficiently
                setOutColorSpace(structPointer, JPEG.JCS_RGB);
                // Update java state according to changes
                // in the native part of decoder.
                outColorSpaceCode = JPEG.JCS_RGB;
                numComponents = 3;
            } else if (csType != ColorSpace.TYPE_GRAY) {
                throw new IIOException("Incompatible color conversion");
            }
            break;
        case JPEG.JCS_RGB:  // IJG wants to go to RGB
            if (csType ==  ColorSpace.TYPE_GRAY) {  // We want gray
                if (colorSpaceCode == JPEG.JCS_YCbCr) {
                    // If the jpeg space is YCbCr, IJG can do it
                    setOutColorSpace(structPointer, JPEG.JCS_GRAYSCALE);
                    // Update java state according to changes
                    // in the native part of decoder.
                    outColorSpaceCode = JPEG.JCS_GRAYSCALE;
                    numComponents = 1;
                }
            } else if ((iccCS != null) &&
                       (cm.getNumComponents() == numComponents) &&
                       (cs != iccCS)) {
                // We have an ICC profile but it isn't used in the dest
                // image.  So convert from the profile cs to the target cs
                convert = new ColorConvertOp(iccCS, cs, null);
                // Leave IJG conversion in place; we still need it
            } else if ((iccCS == null) &&
                       (!cs.isCS_sRGB()) &&
                       (cm.getNumComponents() == numComponents)) {
                // Target isn't sRGB, so convert from sRGB to the target
                convert = new ColorConvertOp(JPEG.JCS.sRGB, cs, null);
            } else if (csType != ColorSpace.TYPE_RGB) {
                throw new IIOException("Incompatible color conversion");
            }
            break;
        case JPEG.JCS_RGBA:
            // No conversions available; image must be RGBA
            if ((csType != ColorSpace.TYPE_RGB) ||
                (cm.getNumComponents() != numComponents)) {
                throw new IIOException("Incompatible color conversion");
            }
            break;
        case JPEG.JCS_YCC:
            {
                ColorSpace YCC = JPEG.JCS.getYCC();
                if (YCC == null) { // We can't do YCC at all
                    throw new IIOException("Incompatible color conversion");
                }
                if ((cs != YCC) &&
                    (cm.getNumComponents() == numComponents)) {
                    convert = new ColorConvertOp(YCC, cs, null);
                }
            }
            break;
        case JPEG.JCS_YCCA:
            {
                ColorSpace YCC = JPEG.JCS.getYCC();
                // No conversions available; image must be YCCA
                if ((YCC == null) || // We can't do YCC at all
                    (cs != YCC) ||
                    (cm.getNumComponents() != numComponents)) {
                    throw new IIOException("Incompatible color conversion");
                }
            }
            break;
        default:
            // Anything else we can't handle at all
            throw new IIOException("Incompatible color conversion");
        }
    }

    /**
     * Set the IJG output space to the given value.  The library will
     * perform the appropriate colorspace conversions.
     */
    private native void setOutColorSpace(long structPointer, int id);

    /////// End of Color Conversion & Image Types

    public ImageReadParam getDefaultReadParam() {
        return new JPEGImageReadParam();
    }

    public IIOMetadata getStreamMetadata() throws IOException {
        setThreadLock();
        try {
            if (!tablesOnlyChecked) {
                cbLock.check();
                checkTablesOnly();
            }
            return streamMetadata;
        } finally {
            clearThreadLock();
        }
    }

    public IIOMetadata getImageMetadata(int imageIndex)
        throws IOException {
        setThreadLock();
        try {
            // imageMetadataIndex will always be either a valid index or
            // -1, in which case imageMetadata will not be null.
            // So we can leave checking imageIndex for gotoImage.
            if ((imageMetadataIndex == imageIndex)
                && (imageMetadata != null)) {
                return imageMetadata;
            }

            cbLock.check();

            gotoImage(imageIndex);

            imageMetadata = new JPEGMetadata(false, false, iis, this);

            imageMetadataIndex = imageIndex;

            return imageMetadata;
        } finally {
            clearThreadLock();
        }
    }

    public BufferedImage read(int imageIndex, ImageReadParam param)
        throws IOException {
        setThreadLock();
        try {
            cbLock.check();
            try {
                readInternal(imageIndex, param, false);
            } catch (RuntimeException e) {
                resetLibraryState(structPointer);
                throw e;
            } catch (IOException e) {
                resetLibraryState(structPointer);
                throw e;
            }

            BufferedImage ret = image;
            image = null;  // don't keep a reference here
            return ret;
        } finally {
            clearThreadLock();
        }
    }

    private Raster readInternal(int imageIndex,
                                ImageReadParam param,
                                boolean wantRaster) throws IOException {
        readHeader(imageIndex, false);

        WritableRaster imRas = null;
        int numImageBands = 0;

        if (!wantRaster){
            // Can we read this image?
            Iterator imageTypes = getImageTypes(imageIndex);
            if (imageTypes.hasNext() == false) {
                throw new IIOException("Unsupported Image Type");
            }

            image = getDestination(param, imageTypes, width, height);
            imRas = image.getRaster();

            // The destination may still be incompatible.

            numImageBands = image.getSampleModel().getNumBands();

            // Check whether we can handle any implied color conversion

            // Throws IIOException if the stream and the image are
            // incompatible, and sets convert if a java conversion
            // is necessary
            checkColorConversion(image, param);

            // Check the source and destination bands in the param
            checkReadParamBandSettings(param, numComponents, numImageBands);
        } else {
            // Set the output color space equal to the input colorspace
            // This disables all conversions
            setOutColorSpace(structPointer, colorSpaceCode);
            image = null;
        }

        // Create an intermediate 1-line Raster that will hold the decoded,
        // subsampled, clipped, band-selected image data in a single
        // byte-interleaved buffer.  The above transformations
        // will occur in C for performance.  Every time this Raster
        // is filled we will call back to acceptPixels below to copy
        // this to whatever kind of buffer our image has.

        int [] srcBands = JPEG.bandOffsets[numComponents-1];
        int numRasterBands = (wantRaster ? numComponents : numImageBands);
        destinationBands = null;

        Rectangle srcROI = new Rectangle(0, 0, 0, 0);
        destROI = new Rectangle(0, 0, 0, 0);
        computeRegions(param, width, height, image, srcROI, destROI);

        int periodX = 1;
        int periodY = 1;

        minProgressivePass = 0;
        maxProgressivePass = Integer.MAX_VALUE;

        if (param != null) {
            periodX = param.getSourceXSubsampling();
            periodY = param.getSourceYSubsampling();

            int[] sBands = param.getSourceBands();
            if (sBands != null) {
                srcBands = sBands;
                numRasterBands = srcBands.length;
            }
            if (!wantRaster) {  // ignore dest bands for Raster
                destinationBands = param.getDestinationBands();
            }

            minProgressivePass = param.getSourceMinProgressivePass();
            maxProgressivePass = param.getSourceMaxProgressivePass();

            if (param instanceof JPEGImageReadParam) {
                JPEGImageReadParam jparam = (JPEGImageReadParam) param;
                if (jparam.areTablesSet()) {
                    abbrevQTables = jparam.getQTables();
                    abbrevDCHuffmanTables = jparam.getDCHuffmanTables();
                    abbrevACHuffmanTables = jparam.getACHuffmanTables();
                }
            }
        }

        int lineSize = destROI.width*numRasterBands;

        buffer = new DataBufferByte(lineSize);

        int [] bandOffs = JPEG.bandOffsets[numRasterBands-1];

        raster = Raster.createInterleavedRaster(buffer,
                                                destROI.width, 1,
                                                lineSize,
                                                numRasterBands,
                                                bandOffs,
                                                null);

        // Now that we have the Raster we'll decode to, get a view of the
        // target Raster that will permit a simple setRect for each scanline
        if (wantRaster) {
            target =  Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,
                                                     destROI.width,
                                                     destROI.height,
                                                     lineSize,
                                                     numRasterBands,
                                                     bandOffs,
                                                     null);
        } else {
            target = imRas;
        }
        int [] bandSizes = target.getSampleModel().getSampleSize();
        for (int i = 0; i < bandSizes.length; i++) {
            if (bandSizes[i] <= 0 || bandSizes[i] > 8) {
                throw new IIOException("Illegal band size: should be 0 < size <= 8");
            }
        }

        /*
         * If the process is sequential, and we have restart markers,
         * we could skip to the correct restart marker, if the library
         * lets us.  That's an optimization to investigate later.
         */

        // Check for update listeners (don't call back if none)
        boolean callbackUpdates = ((updateListeners != null)
                                   || (progressListeners != null));

        // Set up progression data
        initProgressData();
        // if we have a metadata object, we can count the scans
        // and set knownPassCount
        if (imageIndex == imageMetadataIndex) { // We have metadata
            knownPassCount = 0;
            for (Iterator iter = imageMetadata.markerSequence.iterator();
                 iter.hasNext();) {
                if (iter.next() instanceof SOSMarkerSegment) {
                    knownPassCount++;
                }
            }
        }
        progInterval = Math.max((target.getHeight()-1) / 20, 1);
        if (knownPassCount > 0) {
            progInterval *= knownPassCount;
        } else if (maxProgressivePass != Integer.MAX_VALUE) {
            progInterval *= (maxProgressivePass - minProgressivePass + 1);
        }

        if (debug) {
            System.out.println("**** Read Data *****");
            System.out.println("numRasterBands is " + numRasterBands);
            System.out.print("srcBands:");
            for (int i = 0; i<srcBands.length;i++)
                System.out.print(" " + srcBands[i]);
            System.out.println();
            System.out.println("destination bands is " + destinationBands);
            if (destinationBands != null) {
                for (int i = 0; i < destinationBands.length; i++) {
                    System.out.print(" " + destinationBands[i]);
                }
                System.out.println();
            }
            System.out.println("sourceROI is " + srcROI);
            System.out.println("destROI is " + destROI);
            System.out.println("periodX is " + periodX);
            System.out.println("periodY is " + periodY);
            System.out.println("minProgressivePass is " + minProgressivePass);
            System.out.println("maxProgressivePass is " + maxProgressivePass);
            System.out.println("callbackUpdates is " + callbackUpdates);
        }

        // Finally, we are ready to read

        processImageStarted(currentImage);

        boolean aborted = false;

        // Note that getData disables acceleration on buffer, but it is
        // just a 1-line intermediate data transfer buffer that will not
        // affect the acceleration of the resulting image.
        aborted = readImage(structPointer,
                            buffer.getData(),
                            numRasterBands,
                            srcBands,
                            bandSizes,
                            srcROI.x, srcROI.y,
                            srcROI.width, srcROI.height,
                            periodX, periodY,
                            abbrevQTables,
                            abbrevDCHuffmanTables,
                            abbrevACHuffmanTables,
                            minProgressivePass, maxProgressivePass,
                            callbackUpdates);

        if (aborted) {
            processReadAborted();
        } else {
            processImageComplete();
        }

        return target;

    }

    /**
     * This method is called back from C when the intermediate Raster
     * is full.  The parameter indicates the scanline in the target
     * Raster to which the intermediate Raster should be copied.
     * After the copy, we notify update listeners.
     */
    private void acceptPixels(int y, boolean progressive) {
        if (convert != null) {
            convert.filter(raster, raster);
        }
        target.setRect(destROI.x, destROI.y + y, raster);

        cbLock.lock();
        try {
            processImageUpdate(image,
                               destROI.x, destROI.y+y,
                               raster.getWidth(), 1,
                               1, 1,
                               destinationBands);
            if ((y > 0) && (y%progInterval == 0)) {
                int height = target.getHeight()-1;
                float percentOfPass = ((float)y)/height;
                if (progressive) {
                    if (knownPassCount != UNKNOWN) {
                        processImageProgress((pass + percentOfPass)*100.0F
                                             / knownPassCount);
                    } else if (maxProgressivePass != Integer.MAX_VALUE) {
                        // Use the range of allowed progressive passes
                        processImageProgress((pass + percentOfPass)*100.0F
                                             / (maxProgressivePass - minProgressivePass + 1));
                    } else {
                        // Assume there are a minimum of MIN_ESTIMATED_PASSES
                        // and that there is always one more pass
                        // Compute the percentage as the percentage at the end
                        // of the previous pass, plus the percentage of this
                        // pass scaled to be the percentage of the total remaining,
                        // assuming a minimum of MIN_ESTIMATED_PASSES passes and
                        // that there is always one more pass.  This is monotonic
                        // and asymptotic to 1.0, which is what we need.
                        int remainingPasses = // including this one
                            Math.max(2, MIN_ESTIMATED_PASSES-pass);
                        int totalPasses = pass + remainingPasses-1;
                        progInterval = Math.max(height/20*totalPasses,
                                                totalPasses);
                        if (y%progInterval == 0) {
                            percentToDate = previousPassPercentage +
                                (1.0F - previousPassPercentage)
                                * (percentOfPass)/remainingPasses;
                            if (debug) {
                                System.out.print("pass= " + pass);
                                System.out.print(", y= " + y);
                                System.out.print(", progInt= " + progInterval);
                                System.out.print(", % of pass: " + percentOfPass);
                                System.out.print(", rem. passes: "
                                                 + remainingPasses);
                                System.out.print(", prev%: "
                                                 + previousPassPercentage);
                                System.out.print(", %ToDate: " + percentToDate);
                                System.out.print(" ");
                            }
                            processImageProgress(percentToDate*100.0F);
                        }
                    }
                } else {
                    processImageProgress(percentOfPass * 100.0F);
                }
            }
        } finally {
            cbLock.unlock();
        }
    }

    private void initProgressData() {
        knownPassCount = UNKNOWN;
        pass = 0;
        percentToDate = 0.0F;
        previousPassPercentage = 0.0F;
        progInterval = 0;
    }

    private void passStarted (int pass) {
        cbLock.lock();
        try {
            this.pass = pass;
            previousPassPercentage = percentToDate;
            processPassStarted(image,
                               pass,
                               minProgressivePass,
                               maxProgressivePass,
                               0, 0,
                               1,1,
                               destinationBands);
        } finally {
            cbLock.unlock();
        }
    }

    private void passComplete () {
        cbLock.lock();
        try {
            processPassComplete(image);
        } finally {
            cbLock.unlock();
        }
    }

    void thumbnailStarted(int thumbnailIndex) {
        cbLock.lock();
        try {
            processThumbnailStarted(currentImage, thumbnailIndex);
        } finally {
            cbLock.unlock();
        }
    }

    // Provide access to protected superclass method
    void thumbnailProgress(float percentageDone) {
        cbLock.lock();
        try {
            processThumbnailProgress(percentageDone);
        } finally {
            cbLock.unlock();
        }
    }

    // Provide access to protected superclass method
    void thumbnailComplete() {
        cbLock.lock();
        try {
            processThumbnailComplete();
        } finally {
            cbLock.unlock();
        }
    }

    /**
     * Returns <code>true</code> if the read was aborted.
     */
    private native boolean readImage(long structPointer,
                                     byte [] buffer,
                                     int numRasterBands,
                                     int [] srcBands,
                                     int [] bandSizes,
                                     int sourceXOffset, int sourceYOffset,
                                     int sourceWidth, int sourceHeight,
                                     int periodX, int periodY,
                                     JPEGQTable [] abbrevQTables,
                                     JPEGHuffmanTable [] abbrevDCHuffmanTables,
                                     JPEGHuffmanTable [] abbrevACHuffmanTables,
                                     int minProgressivePass,
                                     int maxProgressivePass,
                                     boolean wantUpdates);

    public void abort() {
        setThreadLock();
        try {
            /**
             * NB: we do not check the call back lock here,
             * we allow to abort the reader any time.
             */

            super.abort();
            abortRead(structPointer);
        } finally {
            clearThreadLock();
        }
    }

    /** Set the C level abort flag. Keep it atomic for thread safety. */
    private native void abortRead(long structPointer);

    /** Resets library state when an exception occurred during a read. */
    private native void resetLibraryState(long structPointer);

    public boolean canReadRaster() {
        return true;
    }

    public Raster readRaster(int imageIndex, ImageReadParam param)
        throws IOException {
        setThreadLock();
        Raster retval = null;
        try {
            cbLock.check();
            /*
             * This could be further optimized by not resetting the dest.
             * offset and creating a translated raster in readInternal()
             * (see bug 4994702 for more info).
             */

            // For Rasters, destination offset is logical, not physical, so
            // set it to 0 before calling computeRegions, so that the destination
            // region is not clipped.
            Point saveDestOffset = null;
            if (param != null) {
                saveDestOffset = param.getDestinationOffset();
                param.setDestinationOffset(new Point(0, 0));
            }
            retval = readInternal(imageIndex, param, true);
            // Apply the destination offset, if any, as a logical offset
            if (saveDestOffset != null) {
                target = target.createWritableTranslatedChild(saveDestOffset.x,
                                                              saveDestOffset.y);
            }
        } catch (RuntimeException e) {
            resetLibraryState(structPointer);
            throw e;
        } catch (IOException e) {
            resetLibraryState(structPointer);
            throw e;
        } finally {
            clearThreadLock();
        }
        return retval;
    }

    public boolean readerSupportsThumbnails() {
        return true;
    }

    public int getNumThumbnails(int imageIndex) throws IOException {
        setThreadLock();
        try {
            cbLock.check();

            getImageMetadata(imageIndex);  // checks iis state for us
            // Now check the jfif segments
            com.sun.imageio.plugins.jpeg.JFIFMarkerSegment jfif =
                (com.sun.imageio.plugins.jpeg.JFIFMarkerSegment) imageMetadata.findMarkerSegment
                (com.sun.imageio.plugins.jpeg.JFIFMarkerSegment.class, true);
            int retval = 0;
            if (jfif != null) {
                retval = (jfif.thumb == null) ? 0 : 1;
                retval += jfif.extSegments.size();
            }
            return retval;
        } finally {
            clearThreadLock();
        }
    }

    public int getThumbnailWidth(int imageIndex, int thumbnailIndex)
        throws IOException {
        setThreadLock();
        try {
            cbLock.check();

            if ((thumbnailIndex < 0)
                || (thumbnailIndex >= getNumThumbnails(imageIndex))) {
                throw new IndexOutOfBoundsException("No such thumbnail");
            }
            // Now we know that there is a jfif segment
            com.sun.imageio.plugins.jpeg.JFIFMarkerSegment jfif =
                (com.sun.imageio.plugins.jpeg.JFIFMarkerSegment) imageMetadata.findMarkerSegment
                (com.sun.imageio.plugins.jpeg.JFIFMarkerSegment.class, true);
            return  jfif.getThumbnailWidth(thumbnailIndex);
        } finally {
            clearThreadLock();
        }
    }

    public int getThumbnailHeight(int imageIndex, int thumbnailIndex)
        throws IOException {
        setThreadLock();
        try {
            cbLock.check();

            if ((thumbnailIndex < 0)
                || (thumbnailIndex >= getNumThumbnails(imageIndex))) {
                throw new IndexOutOfBoundsException("No such thumbnail");
            }
            // Now we know that there is a jfif segment
            com.sun.imageio.plugins.jpeg.JFIFMarkerSegment jfif =
                (com.sun.imageio.plugins.jpeg.JFIFMarkerSegment) imageMetadata.findMarkerSegment
                (com.sun.imageio.plugins.jpeg.JFIFMarkerSegment.class, true);
            return  jfif.getThumbnailHeight(thumbnailIndex);
        } finally {
            clearThreadLock();
        }
    }

    public BufferedImage readThumbnail(int imageIndex,
                                       int thumbnailIndex)
        throws IOException {
        setThreadLock();
        try {
            cbLock.check();

            if ((thumbnailIndex < 0)
                || (thumbnailIndex >= getNumThumbnails(imageIndex))) {
                throw new IndexOutOfBoundsException("No such thumbnail");
            }
            // Now we know that there is a jfif segment and that iis is good
            com.sun.imageio.plugins.jpeg.JFIFMarkerSegment jfif =
                (com.sun.imageio.plugins.jpeg.JFIFMarkerSegment) imageMetadata.findMarkerSegment
                (com.sun.imageio.plugins.jpeg.JFIFMarkerSegment.class, true);
            return  jfif.getThumbnail(iis, thumbnailIndex, this);
        } finally {
            clearThreadLock();
        }
    }

    private void resetInternalState() {
        // reset C structures
        resetReader(structPointer);

        // reset local Java structures
        numImages = 0;
        imagePositions = new ArrayList();
        currentImage = -1;
        image = null;
        raster = null;
        target = null;
        buffer = null;
        destROI = null;
        destinationBands = null;
        streamMetadata = null;
        imageMetadata = null;
        imageMetadataIndex = -1;
        haveSeeked = false;
        tablesOnlyChecked = false;
        iccCS = null;
        initProgressData();
    }

    public void reset() {
        setThreadLock();
        try {
            cbLock.check();
            super.reset();
        } finally {
            clearThreadLock();
        }
    }

    private native void resetReader(long structPointer);

    public void dispose() {
        setThreadLock();
        try {
            cbLock.check();

            if (structPointer != 0) {
                disposerRecord.dispose();
                structPointer = 0;
            }
        } finally {
            clearThreadLock();
        }
    }

    private static native void disposeReader(long structPointer);

    private static class JPEGReaderDisposerRecord implements DisposerRecord {
        private long pData;

        public JPEGReaderDisposerRecord(long pData) {
            this.pData = pData;
        }

        public synchronized void dispose() {
            if (pData != 0) {
                disposeReader(pData);
                pData = 0;
            }
        }
    }

    private Thread theThread = null;
    private int theLockCount = 0;

    private synchronized void setThreadLock() {
        Thread currThread = Thread.currentThread();
        if (theThread != null) {
            if (theThread != currThread) {
                // it looks like that this reader instance is used
                // by multiple threads.
                throw new IllegalStateException("Attempt to use instance of " +
                                                this + " locked on thread " +
                                                theThread + " from thread " +
                                                currThread);
            } else {
                theLockCount ++;
            }
        } else {
            theThread = currThread;
            theLockCount = 1;
        }
    }

    private synchronized void clearThreadLock() {
        Thread currThread = Thread.currentThread();
        if (theThread == null || theThread != currThread) {
            throw new IllegalStateException("Attempt to clear thread lock " +
                                            " form wrong thread." +
                                            " Locked thread: " + theThread +
                                            "; current thread: " + currThread);
        }
        theLockCount --;
        if (theLockCount == 0) {
            theThread = null;
        }
    }

    private CallBackLock cbLock = new CallBackLock();

    private static class CallBackLock {

        private State lockState;

        CallBackLock() {
            lockState = State.Unlocked;
        }

        void check() {
            if (lockState != State.Unlocked) {
                throw new IllegalStateException("Access to the reader is not allowed");
            }
        }

        private void lock() {
            lockState = State.Locked;
        }

        private void unlock() {
            lockState = State.Unlocked;
        }

        private static enum State {
            Unlocked,
            Locked
        }
    }
}

/**
 * An internal helper class that wraps producer's iterator
 * and extracts specifier instances on demand.
 */
class ImageTypeIterator implements Iterator<ImageTypeSpecifier> {
     private Iterator<com.sun.imageio.plugins.jpeg.ImageTypeProducer> producers;
     private ImageTypeSpecifier theNext = null;

     public ImageTypeIterator(Iterator<com.sun.imageio.plugins.jpeg.ImageTypeProducer> producers) {
         this.producers = producers;
     }

     public boolean hasNext() {
         if (theNext != null) {
             return true;
         }
         if (!producers.hasNext()) {
             return false;
         }
         do {
             theNext = producers.next().getType();
         } while (theNext == null && producers.hasNext());

         return (theNext != null);
     }

     public ImageTypeSpecifier next() {
         if (theNext != null || hasNext()) {
             ImageTypeSpecifier t = theNext;
             theNext = null;
             return t;
         } else {
             throw new NoSuchElementException();
         }
     }

     public void remove() {
         producers.remove();
     }
}

/**
 * An internal helper class that provides means for deferred creation
 * of ImageTypeSpecifier instance required to describe available
 * destination types.
 *
 * This implementation only supports standard
 * jpeg color spaces (defined by corresponding JCS color space code).
 *
 * To support other color spaces one can override produce() method to
 * return custom instance of ImageTypeSpecifier.
 */
class ImageTypeProducer {

    private ImageTypeSpecifier type = null;
    boolean failed = false;
    private int csCode;

    public ImageTypeProducer(int csCode) {
        this.csCode = csCode;
    }

    public ImageTypeProducer() {
        csCode = -1; // undefined
    }

    public synchronized ImageTypeSpecifier getType() {
        if (!failed && type == null) {
            try {
                type = produce();
            } catch (Throwable e) {
                failed = true;
            }
        }
        return type;
    }

    private static final com.sun.imageio.plugins.jpeg.ImageTypeProducer[] defaultTypes =
            new com.sun.imageio.plugins.jpeg.ImageTypeProducer[JPEG.NUM_JCS_CODES];

    public synchronized static com.sun.imageio.plugins.jpeg.ImageTypeProducer getTypeProducer(int csCode) {
        if (csCode < 0 || csCode >= JPEG.NUM_JCS_CODES) {
            return null;
        }
        if (defaultTypes[csCode] == null) {
            defaultTypes[csCode] = new com.sun.imageio.plugins.jpeg.ImageTypeProducer(csCode);
        }
        return defaultTypes[csCode];
    }

    protected ImageTypeSpecifier produce() {
        switch (csCode) {
            case JPEG.JCS_GRAYSCALE:
                return ImageTypeSpecifier.createFromBufferedImageType
                        (BufferedImage.TYPE_BYTE_GRAY);
            case JPEG.JCS_RGB:
                return ImageTypeSpecifier.createInterleaved(JPEG.JCS.sRGB,
                        JPEG.bOffsRGB,
                        DataBuffer.TYPE_BYTE,
                        false,
                        false);
            case JPEG.JCS_RGBA:
                return ImageTypeSpecifier.createPacked(JPEG.JCS.sRGB,
                        0xff000000,
                        0x00ff0000,
                        0x0000ff00,
                        0x000000ff,
                        DataBuffer.TYPE_INT,
                        false);
            case JPEG.JCS_YCC:
                if (JPEG.JCS.getYCC() != null) {
                    return ImageTypeSpecifier.createInterleaved(
                            JPEG.JCS.getYCC(),
                        JPEG.bandOffsets[2],
                        DataBuffer.TYPE_BYTE,
                        false,
                        false);
                } else {
                    return null;
                }
            case JPEG.JCS_YCCA:
                if (JPEG.JCS.getYCC() != null) {
                    return ImageTypeSpecifier.createInterleaved(
                            JPEG.JCS.getYCC(),
                        JPEG.bandOffsets[3],
                        DataBuffer.TYPE_BYTE,
                        true,
                        false);
                } else {
                    return null;
                }
            default:
                return null;
        }
    }
}
