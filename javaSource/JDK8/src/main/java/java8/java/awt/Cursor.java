/*
 * Copyright (c) 1996, 2013, Oracle and/or its affiliates. All rights reserved.
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

import sun.awt.AWTAccessor;
import sun.util.logging.PlatformLogger;

import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.beans.ConstructorProperties;
import java.io.File;
import java.io.FileInputStream;
import java.security.AccessController;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * A class to encapsulate the bitmap representation of the mouse cursor.
 *
 * @see Component#setCursor
 * @author      Amy Fowler
 */
public class Cursor implements java.io.Serializable {

    /**
     * The default cursor type (gets set if no cursor is defined).
     */
    public static final int     DEFAULT_CURSOR                  = 0;

    /**
     * The crosshair cursor type.
     */
    public static final int     CROSSHAIR_CURSOR                = 1;

    /**
     * The text cursor type.
     */
    public static final int     TEXT_CURSOR                     = 2;

    /**
     * The wait cursor type.
     */
    public static final int     WAIT_CURSOR                     = 3;

    /**
     * The south-west-resize cursor type.
     */
    public static final int     SW_RESIZE_CURSOR                = 4;

    /**
     * The south-east-resize cursor type.
     */
    public static final int     SE_RESIZE_CURSOR                = 5;

    /**
     * The north-west-resize cursor type.
     */
    public static final int     NW_RESIZE_CURSOR                = 6;

    /**
     * The north-east-resize cursor type.
     */
    public static final int     NE_RESIZE_CURSOR                = 7;

    /**
     * The north-resize cursor type.
     */
    public static final int     N_RESIZE_CURSOR                 = 8;

    /**
     * The south-resize cursor type.
     */
    public static final int     S_RESIZE_CURSOR                 = 9;

    /**
     * The west-resize cursor type.
     */
    public static final int     W_RESIZE_CURSOR                 = 10;

    /**
     * The east-resize cursor type.
     */
    public static final int     E_RESIZE_CURSOR                 = 11;

    /**
     * The hand cursor type.
     */
    public static final int     HAND_CURSOR                     = 12;

    /**
     * The move cursor type.
     */
    public static final int     MOVE_CURSOR                     = 13;

    /**
      * @deprecated As of JDK version 1.7, the {@link #getPredefinedCursor(int)}
      * method should be used instead.
      */
    @Deprecated
    protected static java.awt.Cursor predefined[] = new java.awt.Cursor[14];

    /**
     * This field is a private replacement for 'predefined' array.
     */
    private final static java.awt.Cursor[] predefinedPrivate = new java.awt.Cursor[14];

    /* Localization names and default values */
    static final String[][] cursorProperties = {
        { "AWT.DefaultCursor", "Default Cursor" },
        { "AWT.CrosshairCursor", "Crosshair Cursor" },
        { "AWT.TextCursor", "Text Cursor" },
        { "AWT.WaitCursor", "Wait Cursor" },
        { "AWT.SWResizeCursor", "Southwest Resize Cursor" },
        { "AWT.SEResizeCursor", "Southeast Resize Cursor" },
        { "AWT.NWResizeCursor", "Northwest Resize Cursor" },
        { "AWT.NEResizeCursor", "Northeast Resize Cursor" },
        { "AWT.NResizeCursor", "North Resize Cursor" },
        { "AWT.SResizeCursor", "South Resize Cursor" },
        { "AWT.WResizeCursor", "West Resize Cursor" },
        { "AWT.EResizeCursor", "East Resize Cursor" },
        { "AWT.HandCursor", "Hand Cursor" },
        { "AWT.MoveCursor", "Move Cursor" },
    };

    /**
     * The chosen cursor type initially set to
     * the <code>DEFAULT_CURSOR</code>.
     *
     * @serial
     * @see #getType()
     */
    int type = DEFAULT_CURSOR;

    /**
     * The type associated with all custom cursors.
     */
    public static final int     CUSTOM_CURSOR                   = -1;

    /*
     * hashtable, filesystem dir prefix, filename, and properties for custom cursors support
     */

    private static final Hashtable<String, java.awt.Cursor> systemCustomCursors = new Hashtable<>(1);
    private static final String systemCustomCursorDirPrefix = initCursorDir();

    private static String initCursorDir() {
        String jhome = AccessController.doPrivileged(
               new sun.security.action.GetPropertyAction("java.home"));
        return jhome +
            File.separator + "lib" + File.separator + "images" +
            File.separator + "cursors" + File.separator;
    }

    private static final String     systemCustomCursorPropertiesFile = systemCustomCursorDirPrefix + "cursors.properties";

    private static       Properties systemCustomCursorProperties = null;

    private static final String CursorDotPrefix  = "Cursor.";
    private static final String DotFileSuffix    = ".File";
    private static final String DotHotspotSuffix = ".HotSpot";
    private static final String DotNameSuffix    = ".Name";

    /*
     * JDK 1.1 serialVersionUID
     */
    private static final long serialVersionUID = 8028237497568985504L;

    private static final PlatformLogger log = PlatformLogger.getLogger("java.awt.Cursor");

    static {
        /* ensure that the necessary native libraries are loaded */
        Toolkit.loadLibraries();
        if (!GraphicsEnvironment.isHeadless()) {
            initIDs();
        }

        AWTAccessor.setCursorAccessor(
            new AWTAccessor.CursorAccessor() {
                public long getPData(java.awt.Cursor cursor) {
                    return cursor.pData;
                }

                public void setPData(java.awt.Cursor cursor, long pData) {
                    cursor.pData = pData;
                }

                public int getType(java.awt.Cursor cursor) {
                    return cursor.type;
                }
            });
    }

    /**
     * Initialize JNI field and method IDs for fields that may be
     * accessed from C.
     */
    private static native void initIDs();

    /**
     * Hook into native data.
     */
    private transient long pData;

    private transient Object anchor = new Object();

    static class CursorDisposer implements sun.java2d.DisposerRecord {
        volatile long pData;
        public CursorDisposer(long pData) {
            this.pData = pData;
        }
        public void dispose() {
            if (pData != 0) {
                finalizeImpl(pData);
            }
        }
    }
    transient CursorDisposer disposer;
    private void setPData(long pData) {
        this.pData = pData;
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        if (disposer == null) {
            disposer = new CursorDisposer(pData);
            // anchor is null after deserialization
            if (anchor == null) {
                anchor = new Object();
            }
            sun.java2d.Disposer.addRecord(anchor, disposer);
        } else {
            disposer.pData = pData;
        }
    }

    /**
     * The user-visible name of the cursor.
     *
     * @serial
     * @see #getName()
     */
    protected String name;

    /**
     * Returns a cursor object with the specified predefined type.
     *
     * @param type the type of predefined cursor
     * @return the specified predefined cursor
     * @throws IllegalArgumentException if the specified cursor type is
     *         invalid
     */
    static public java.awt.Cursor getPredefinedCursor(int type) {
        if (type < java.awt.Cursor.DEFAULT_CURSOR || type > java.awt.Cursor.MOVE_CURSOR) {
            throw new IllegalArgumentException("illegal cursor type");
        }
        java.awt.Cursor c = predefinedPrivate[type];
        if (c == null) {
            predefinedPrivate[type] = c = new java.awt.Cursor(type);
        }
        // fill 'predefined' array for backwards compatibility.
        if (predefined[type] == null) {
            predefined[type] = c;
        }
        return c;
    }

    /**
     * Returns a system-specific custom cursor object matching the
     * specified name.  Cursor names are, for example: "Invalid.16x16"
     *
     * @param name a string describing the desired system-specific custom cursor
     * @return the system specific custom cursor named
     * @exception HeadlessException if
     * <code>GraphicsEnvironment.isHeadless</code> returns true
     */
    static public java.awt.Cursor getSystemCustomCursor(final String name)
        throws AWTException, HeadlessException {
        GraphicsEnvironment.checkHeadless();
        java.awt.Cursor cursor = systemCustomCursors.get(name);

        if (cursor == null) {
            synchronized(systemCustomCursors) {
                if (systemCustomCursorProperties == null)
                    loadSystemCustomCursorProperties();
            }

            String prefix = CursorDotPrefix + name;
            String key    = prefix + DotFileSuffix;

            if (!systemCustomCursorProperties.containsKey(key)) {
                if (log.isLoggable(PlatformLogger.Level.FINER)) {
                    log.finer("Cursor.getSystemCustomCursor(" + name + ") returned null");
                }
                return null;
            }

            final String fileName =
                systemCustomCursorProperties.getProperty(key);

            String localized = systemCustomCursorProperties.getProperty(prefix + DotNameSuffix);

            if (localized == null) localized = name;

            String hotspot = systemCustomCursorProperties.getProperty(prefix + DotHotspotSuffix);

            if (hotspot == null)
                throw new AWTException("no hotspot property defined for cursor: " + name);

            StringTokenizer st = new StringTokenizer(hotspot, ",");

            if (st.countTokens() != 2)
                throw new AWTException("failed to parse hotspot property for cursor: " + name);

            int x = 0;
            int y = 0;

            try {
                x = Integer.parseInt(st.nextToken());
                y = Integer.parseInt(st.nextToken());
            } catch (NumberFormatException nfe) {
                throw new AWTException("failed to parse hotspot property for cursor: " + name);
            }

            try {
                final int fx = x;
                final int fy = y;
                final String flocalized = localized;

                cursor = AccessController.<java.awt.Cursor>doPrivileged(
                    new java.security.PrivilegedExceptionAction<java.awt.Cursor>() {
                    public java.awt.Cursor run() throws Exception {
                        Toolkit toolkit = Toolkit.getDefaultToolkit();
                        Image image = toolkit.getImage(
                           systemCustomCursorDirPrefix + fileName);
                        return toolkit.createCustomCursor(
                                    image, new Point(fx,fy), flocalized);
                    }
                });
            } catch (Exception e) {
                throw new AWTException(
                    "Exception: " + e.getClass() + " " + e.getMessage() +
                    " occurred while creating cursor " + name);
            }

            if (cursor == null) {
                if (log.isLoggable(PlatformLogger.Level.FINER)) {
                    log.finer("Cursor.getSystemCustomCursor(" + name + ") returned null");
                }
            } else {
                systemCustomCursors.put(name, cursor);
            }
        }

        return cursor;
    }

    /**
     * Return the system default cursor.
     */
    static public java.awt.Cursor getDefaultCursor() {
        return getPredefinedCursor(java.awt.Cursor.DEFAULT_CURSOR);
    }

    /**
     * Creates a new cursor object with the specified type.
     * @param type the type of cursor
     * @throws IllegalArgumentException if the specified cursor type
     * is invalid
     */
    @ConstructorProperties({"type"})
    public Cursor(int type) {
        if (type < java.awt.Cursor.DEFAULT_CURSOR || type > java.awt.Cursor.MOVE_CURSOR) {
            throw new IllegalArgumentException("illegal cursor type");
        }
        this.type = type;

        // Lookup localized name.
        name = Toolkit.getProperty(cursorProperties[type][0],
                                   cursorProperties[type][1]);
    }

    /**
     * Creates a new custom cursor object with the specified name.<p>
     * Note:  this constructor should only be used by AWT implementations
     * as part of their support for custom cursors.  Applications should
     * use Toolkit.createCustomCursor().
     * @param name the user-visible name of the cursor.
     * @see Toolkit#createCustomCursor
     */
    protected Cursor(String name) {
        this.type = java.awt.Cursor.CUSTOM_CURSOR;
        this.name = name;
    }

    /**
     * Returns the type for this cursor.
     */
    public int getType() {
        return type;
    }

    /**
     * Returns the name of this cursor.
     * @return    a localized description of this cursor.
     * @since     1.2
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a string representation of this cursor.
     * @return    a string representation of this cursor.
     * @since     1.2
     */
    public String toString() {
        return getClass().getName() + "[" + getName() + "]";
    }

    /*
     * load the cursor.properties file
     */
    private static void loadSystemCustomCursorProperties() throws AWTException {
        synchronized(systemCustomCursors) {
            systemCustomCursorProperties = new Properties();

            try {
                AccessController.<Object>doPrivileged(
                      new java.security.PrivilegedExceptionAction<Object>() {
                    public Object run() throws Exception {
                        FileInputStream fis = null;
                        try {
                            fis = new FileInputStream(
                                           systemCustomCursorPropertiesFile);
                            systemCustomCursorProperties.load(fis);
                        } finally {
                            if (fis != null)
                                fis.close();
                        }
                        return null;
                    }
                });
            } catch (Exception e) {
                systemCustomCursorProperties = null;
                 throw new AWTException("Exception: " + e.getClass() + " " +
                   e.getMessage() + " occurred while loading: " +
                                        systemCustomCursorPropertiesFile);
            }
        }
    }

    private native static void finalizeImpl(long pData);
}
