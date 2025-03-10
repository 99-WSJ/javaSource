/*
 * Copyright (c) 2005, 2013, Oracle and/or its affiliates. All rights reserved.
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
package java8.java.awt;

import sun.awt.image.SunWritableRaster;
import sun.util.logging.PlatformLogger;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.SinglePixelPackedSampleModel;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

/**
 * The splash screen can be displayed at application startup, before the
 * Java Virtual Machine (JVM) starts. The splash screen is displayed as an
 * undecorated window containing an image. You can use GIF, JPEG, or PNG files
 * for the image. Animation is supported for the GIF format, while transparency
 * is supported both for GIF and PNG.  The window is positioned at the center
 * of the screen. The position on multi-monitor systems is not specified. It is
 * platform and implementation dependent.  The splash screen window is closed
 * automatically as soon as the first window is displayed by Swing/AWT (may be
 * also closed manually using the Java API, see below).
 * <P>
 * If your application is packaged in a jar file, you can use the
 * "SplashScreen-Image" option in a manifest file to show a splash screen.
 * Place the image in the jar archive and specify the path in the option.
 * The path should not have a leading slash.
 * <BR>
 * For example, in the <code>manifest.mf</code> file:
 * <PRE>
 * Manifest-Version: 1.0
 * Main-Class: Test
 * SplashScreen-Image: filename.gif
 * </PRE>
 * <P>
 * If the Java implementation provides the command-line interface and you run
 * your application by using the command line or a shortcut, use the Java
 * application launcher option to show a splash screen. The Oracle reference
 * implementation allows you to specify the splash screen image location with
 * the {@code -splash:} option.
 * <BR>
 * For example:
 * <PRE>
 * java -splash:filename.gif Test
 * </PRE>
 * The command line interface has higher precedence over the manifest
 * setting.
 * <p>
 * The splash screen will be displayed as faithfully as possible to present the
 * whole splash screen image given the limitations of the target platform and
 * display.
 * <p>
 * It is implied that the specified image is presented on the screen "as is",
 * i.e. preserving the exact color values as specified in the image file. Under
 * certain circumstances, though, the presented image may differ, e.g. when
 * applying color dithering to present a 32 bits per pixel (bpp) image on a 16
 * or 8 bpp screen. The native platform display configuration may also affect
 * the colors of the displayed image (e.g.  color profiles, etc.)
 * <p>
 * The {@code SplashScreen} class provides the API for controlling the splash
 * screen. This class may be used to close the splash screen, change the splash
 * screen image, get the splash screen native window position/size, and paint
 * in the splash screen. It cannot be used to create the splash screen. You
 * should use the options provided by the Java implementation for that.
 * <p>
 * This class cannot be instantiated. Only a single instance of this class
 * can exist, and it may be obtained by using the {@link #getSplashScreen()}
 * static method. In case the splash screen has not been created at
 * application startup via the command line or manifest file option,
 * the <code>getSplashScreen</code> method returns <code>null</code>.
 *
 * @author Oleg Semenov
 * @since 1.6
 */
public final class SplashScreen {

    SplashScreen(long ptr) { // non-public constructor
        splashPtr = ptr;
    }

    /**
     * Returns the {@code SplashScreen} object used for
     * Java startup splash screen control on systems that support display.
     *
     * @throws UnsupportedOperationException if the splash screen feature is not
     *         supported by the current toolkit
     * @throws HeadlessException if {@code GraphicsEnvironment.isHeadless()}
     *         returns true
     * @return the {@link java.awt.SplashScreen} instance, or <code>null</code> if there is
     *         none or it has already been closed
     */
    public static java.awt.SplashScreen getSplashScreen() {
        synchronized (java.awt.SplashScreen.class) {
            if (GraphicsEnvironment.isHeadless()) {
                throw new HeadlessException();
            }
            // SplashScreen class is now a singleton
            if (!wasClosed && theInstance == null) {
                java.security.AccessController.doPrivileged(
                    new java.security.PrivilegedAction<Void>() {
                        public Void run() {
                            System.loadLibrary("splashscreen");
                            return null;
                        }
                    });
                long ptr = _getInstance();
                if (ptr != 0 && _isVisible(ptr)) {
                    theInstance = new java.awt.SplashScreen(ptr);
                }
            }
            return theInstance;
        }
    }

    /**
     * Changes the splash screen image. The new image is loaded from the
     * specified URL; GIF, JPEG and PNG image formats are supported.
     * The method returns after the image has finished loading and the window
     * has been updated.
     * The splash screen window is resized according to the size of
     * the image and is centered on the screen.
     *
     * @param imageURL the non-<code>null</code> URL for the new
     *        splash screen image
     * @throws NullPointerException if {@code imageURL} is <code>null</code>
     * @throws IOException if there was an error while loading the image
     * @throws IllegalStateException if the splash screen has already been
     *         closed
     */
    public void setImageURL(URL imageURL) throws NullPointerException, IOException, IllegalStateException {
        checkVisible();
        URLConnection connection = imageURL.openConnection();
        connection.connect();
        int length = connection.getContentLength();
        java.io.InputStream stream = connection.getInputStream();
        byte[] buf = new byte[length];
        int off = 0;
        while(true) {
            // check for available data
            int available = stream.available();
            if (available <= 0) {
                // no data available... well, let's try reading one byte
                // we'll see what happens then
                available = 1;
            }
            // check for enough room in buffer, realloc if needed
            // the buffer always grows in size 2x minimum
            if (off + available > length) {
                length = off*2;
                if (off + available > length) {
                    length = available+off;
                }
                byte[] oldBuf = buf;
                buf = new byte[length];
                System.arraycopy(oldBuf, 0, buf, 0, off);
            }
            // now read the data
            int result = stream.read(buf, off, available);
            if (result < 0) {
                break;
            }
            off += result;
        }
        synchronized(java.awt.SplashScreen.class) {
            checkVisible();
            if (!_setImageData(splashPtr, buf)) {
                throw new IOException("Bad image format or i/o error when loading image");
            }
            this.imageURL = imageURL;
        }
    }

    private void checkVisible() {
        if (!isVisible()) {
            throw new IllegalStateException("no splash screen available");
        }
    }
    /**
     * Returns the current splash screen image.
     *
     * @return URL for the current splash screen image file
     * @throws IllegalStateException if the splash screen has already been closed
     */
    public URL getImageURL() throws IllegalStateException {
        synchronized (java.awt.SplashScreen.class) {
            checkVisible();
            if (imageURL == null) {
                try {
                    String fileName = _getImageFileName(splashPtr);
                    String jarName = _getImageJarName(splashPtr);
                    if (fileName != null) {
                        if (jarName != null) {
                            imageURL = new URL("jar:"+(new File(jarName).toURL().toString())+"!/"+fileName);
                        } else {
                            imageURL = new File(fileName).toURL();
                        }
                    }
                }
                catch(java.net.MalformedURLException e) {
                    if (log.isLoggable(PlatformLogger.Level.FINE)) {
                        log.fine("MalformedURLException caught in the getImageURL() method", e);
                    }
                }
            }
            return imageURL;
        }
    }

    /**
     * Returns the bounds of the splash screen window as a {@link Rectangle}.
     * This may be useful if, for example, you want to replace the splash
     * screen with your window at the same location.
     * <p>
     * You cannot control the size or position of the splash screen.
     * The splash screen size is adjusted automatically when the image changes.
     * <p>
     * The image may contain transparent areas, and thus the reported bounds may
     * be larger than the visible splash screen image on the screen.
     *
     * @return a {@code Rectangle} containing the splash screen bounds
     * @throws IllegalStateException if the splash screen has already been closed
     */
    public Rectangle getBounds() throws IllegalStateException {
        synchronized (java.awt.SplashScreen.class) {
            checkVisible();
            return _getBounds(splashPtr);
        }
    }

    /**
     * Returns the size of the splash screen window as a {@link Dimension}.
     * This may be useful if, for example,
     * you want to draw on the splash screen overlay surface.
     * <p>
     * You cannot control the size or position of the splash screen.
     * The splash screen size is adjusted automatically when the image changes.
     * <p>
     * The image may contain transparent areas, and thus the reported size may
     * be larger than the visible splash screen image on the screen.
     *
     * @return a {@link Dimension} object indicating the splash screen size
     * @throws IllegalStateException if the splash screen has already been closed
     */
    public Dimension getSize() throws IllegalStateException {
        return getBounds().getSize();
    }

    /**
     * Creates a graphics context (as a {@link Graphics2D} object) for the splash
     * screen overlay image, which allows you to draw over the splash screen.
     * Note that you do not draw on the main image but on the image that is
     * displayed over the main image using alpha blending. Also note that drawing
     * on the overlay image does not necessarily update the contents of splash
     * screen window. You should call {@code update()} on the
     * <code>SplashScreen</code> when you want the splash screen to be
     * updated immediately.
     * <p>
     * The pixel (0, 0) in the coordinate space of the graphics context
     * corresponds to the origin of the splash screen native window bounds (see
     * {@link #getBounds()}).
     *
     * @return graphics context for the splash screen overlay surface
     * @throws IllegalStateException if the splash screen has already been closed
     */
    public Graphics2D createGraphics() throws IllegalStateException {
        synchronized (java.awt.SplashScreen.class) {
            if (image==null) {
                Dimension dim = getSize();
                image = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB);
            }
            return image.createGraphics();
        }
    }

    /**
     * Updates the splash window with current contents of the overlay image.
     *
     * @throws IllegalStateException if the overlay image does not exist;
     *         for example, if {@code createGraphics} has never been called,
     *         or if the splash screen has already been closed
     */
    public void update() throws IllegalStateException {
        BufferedImage image;
        synchronized (java.awt.SplashScreen.class) {
            checkVisible();
            image = this.image;
        }
        if (image == null) {
            throw new IllegalStateException("no overlay image available");
        }
        DataBuffer buf = image.getRaster().getDataBuffer();
        if (!(buf instanceof DataBufferInt)) {
            throw new AssertionError("Overlay image DataBuffer is of invalid type == "+buf.getClass().getName());
        }
        int numBanks = buf.getNumBanks();
        if (numBanks!=1) {
            throw new AssertionError("Invalid number of banks =="+numBanks+" in overlay image DataBuffer");
        }
        if (!(image.getSampleModel() instanceof SinglePixelPackedSampleModel)) {
            throw new AssertionError("Overlay image has invalid sample model == "+image.getSampleModel().getClass().getName());
        }
        SinglePixelPackedSampleModel sm = (SinglePixelPackedSampleModel)image.getSampleModel();
        int scanlineStride = sm.getScanlineStride();
        Rectangle rect = image.getRaster().getBounds();
        // Note that we steal the data array here, but just for reading
        // so we do not need to mark the DataBuffer dirty...
        int[] data = SunWritableRaster.stealData((DataBufferInt)buf, 0);
        synchronized(java.awt.SplashScreen.class) {
            checkVisible();
            _update(splashPtr, data, rect.x, rect.y, rect.width, rect.height, scanlineStride);
        }
    }

    /**
     * Hides the splash screen, closes the window, and releases all associated
     * resources.
     *
     * @throws IllegalStateException if the splash screen has already been closed
     */
    public void close() throws IllegalStateException {
        synchronized (java.awt.SplashScreen.class) {
            checkVisible();
            _close(splashPtr);
            image = null;
            java.awt.SplashScreen.markClosed();
        }
    }

    static void markClosed() {
        synchronized (java.awt.SplashScreen.class) {
            wasClosed = true;
            theInstance = null;
        }
    }


    /**
     * Determines whether the splash screen is visible. The splash screen may
     * be hidden using {@link #close()}, it is also hidden automatically when
     * the first AWT/Swing window is made visible.
     * <p>
     * Note that the native platform may delay presenting the splash screen
     * native window on the screen. The return value of {@code true} for this
     * method only guarantees that the conditions to hide the splash screen
     * window have not occurred yet.
     *
     * @return true if the splash screen is visible (has not been closed yet),
     *         false otherwise
     */
    public boolean isVisible() {
        synchronized (java.awt.SplashScreen.class) {
            return !wasClosed && _isVisible(splashPtr);
        }
    }

    private BufferedImage image; // overlay image

    private final long splashPtr; // pointer to native Splash structure
    private static boolean wasClosed = false;

    private URL imageURL;

    /**
     * The instance reference for the singleton.
     * (<code>null</code> if no instance exists yet.)
     *
     * @see #getSplashScreen
     * @see #close
     */
    private static java.awt.SplashScreen theInstance = null;

    private static final PlatformLogger log = PlatformLogger.getLogger("java.awt.SplashScreen");

    private native static void _update(long splashPtr, int[] data, int x, int y, int width, int height, int scanlineStride);
    private native static boolean _isVisible(long splashPtr);
    private native static Rectangle _getBounds(long splashPtr);
    private native static long _getInstance();
    private native static void _close(long splashPtr);
    private native static String _getImageFileName(long splashPtr);
    private native static String _getImageJarName(long SplashPtr);
    private native static boolean _setImageData(long SplashPtr, byte[] data);

};
