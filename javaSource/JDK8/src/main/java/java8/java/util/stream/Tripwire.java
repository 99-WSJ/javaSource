/*
 * Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
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
package java8.java.util.stream;

import sun.util.logging.PlatformLogger;

import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * Utility class for detecting inadvertent uses of boxing in
 * {@code java.util.stream} classes.  The detection is turned on or off based on
 * whether the system property {@code org.openjdk.java.util.stream.tripwire} is
 * considered {@code true} according to {@link Boolean#getBoolean(String)}.
 * This should normally be turned off for production use.
 *
 * @apiNote
 * Typical usage would be for boxing code to do:
 * <pre>{@code
 *     if (Tripwire.ENABLED)
 *         Tripwire.trip(getClass(), "{0} calling Sink.OfInt.accept(Integer)");
 * }</pre>
 *
 * @since 1.8
 */
final class Tripwire {
    private static final String TRIPWIRE_PROPERTY = "org.openjdk.java.util.stream.tripwire";

    /** Should debugging checks be enabled? */
    static final boolean ENABLED = AccessController.doPrivileged(
            (PrivilegedAction<Boolean>) () -> Boolean.getBoolean(TRIPWIRE_PROPERTY));

    private Tripwire() { }

    /**
     * Produces a log warning, using {@code PlatformLogger.getLogger(className)},
     * using the supplied message.  The class name of {@code trippingClass} will
     * be used as the first parameter to the message.
     *
     * @param trippingClass Name of the class generating the message
     * @param msg A message format string of the type expected by
     * {@link PlatformLogger}
     */
    static void trip(Class<?> trippingClass, String msg) {
        PlatformLogger.getLogger(trippingClass.getName()).warning(msg, trippingClass.getName());
    }
}
