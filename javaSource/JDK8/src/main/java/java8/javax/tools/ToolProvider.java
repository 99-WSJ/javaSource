/*
 * Copyright (c) 2005, 2012, Oracle and/or its affiliates. All rights reserved.
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

package java8.javax.tools;

import javax.tools.DocumentationTool;
import javax.tools.JavaCompiler;
import java.io.File;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;
import static java.util.logging.Level.*;

/**
 * Provides methods for locating tool providers, for example,
 * providers of compilers.  This class complements the
 * functionality of {@link java.util.ServiceLoader}.
 *
 * @author Peter von der Ah&eacute;
 * @since 1.6
 */
public class ToolProvider {

    private static final String propertyName = "sun.tools.ToolProvider";
    private static final String loggerName   = "javax.tools";

    /*
     * Define the system property "sun.tools.ToolProvider" to enable
     * debugging:
     *
     *     java ... -Dsun.tools.ToolProvider ...
     */
    static <T> T trace(Level level, Object reason) {
        // NOTE: do not make this method private as it affects stack traces
        try {
            if (System.getProperty(propertyName) != null) {
                StackTraceElement[] st = Thread.currentThread().getStackTrace();
                String method = "???";
                String cls = javax.tools.ToolProvider.class.getName();
                if (st.length > 2) {
                    StackTraceElement frame = st[2];
                    method = String.format((Locale)null, "%s(%s:%s)",
                                           frame.getMethodName(),
                                           frame.getFileName(),
                                           frame.getLineNumber());
                    cls = frame.getClassName();
                }
                Logger logger = Logger.getLogger(loggerName);
                if (reason instanceof Throwable) {
                    logger.logp(level, cls, method,
                                reason.getClass().getName(), (Throwable)reason);
                } else {
                    logger.logp(level, cls, method, String.valueOf(reason));
                }
            }
        } catch (SecurityException ex) {
            System.err.format((Locale)null, "%s: %s; %s%n",
                              javax.tools.ToolProvider.class.getName(),
                              reason,
                              ex.getLocalizedMessage());
        }
        return null;
    }

    private static final String defaultJavaCompilerName
        = "com.sun.tools.javac.api.JavacTool";

    /**
     * Gets the Java&trade; programming language compiler provided
     * with this platform.
     * @return the compiler provided with this platform or
     * {@code null} if no compiler is provided
     */
    public static JavaCompiler getSystemJavaCompiler() {
        return instance().getSystemTool(JavaCompiler.class, defaultJavaCompilerName);
    }

    private static final String defaultDocumentationToolName
        = "com.sun.tools.javadoc.api.JavadocTool";

    /**
     * Gets the Java&trade; programming language documentation tool provided
     * with this platform.
     * @return the documentation tool provided with this platform or
     * {@code null} if no documentation tool is provided
     */
    public static DocumentationTool getSystemDocumentationTool() {
        return instance().getSystemTool(DocumentationTool.class, defaultDocumentationToolName);
    }

    /**
     * Returns the class loader for tools provided with this platform.
     * This does not include user-installed tools.  Use the
     * {@linkplain java.util.ServiceLoader service provider mechanism}
     * for locating user installed tools.
     *
     * @return the class loader for tools provided with this platform
     * or {@code null} if no tools are provided
     */
    public static ClassLoader getSystemToolClassLoader() {
        try {
            Class<? extends JavaCompiler> c =
                    instance().getSystemToolClass(JavaCompiler.class, defaultJavaCompilerName);
            return c.getClassLoader();
        } catch (Throwable e) {
            return trace(WARNING, e);
        }
    }


    private static javax.tools.ToolProvider instance;

    private static synchronized javax.tools.ToolProvider instance() {
        if (instance == null)
            instance = new javax.tools.ToolProvider();
        return instance;
    }

    // Cache for tool classes.
    // Use weak references to avoid keeping classes around unnecessarily
    private Map<String, Reference<Class<?>>> toolClasses = new HashMap<String, Reference<Class<?>>>();

    // Cache for tool classloader.
    // Use a weak reference to avoid keeping it around unnecessarily
    private Reference<ClassLoader> refToolClassLoader = null;


    private ToolProvider() { }

    private <T> T getSystemTool(Class<T> clazz, String name) {
        Class<? extends T> c = getSystemToolClass(clazz, name);
        try {
            return c.asSubclass(clazz).newInstance();
        } catch (Throwable e) {
            trace(WARNING, e);
            return null;
        }
    }

    private <T> Class<? extends T> getSystemToolClass(Class<T> clazz, String name) {
        Reference<Class<?>> refClass = toolClasses.get(name);
        Class<?> c = (refClass == null ? null : refClass.get());
        if (c == null) {
            try {
                c = findSystemToolClass(name);
            } catch (Throwable e) {
                return trace(WARNING, e);
            }
            toolClasses.put(name, new WeakReference<Class<?>>(c));
        }
        return c.asSubclass(clazz);
    }

    private static final String[] defaultToolsLocation = { "lib", "tools.jar" };

    private Class<?> findSystemToolClass(String toolClassName)
        throws MalformedURLException, ClassNotFoundException
    {
        // try loading class directly, in case tool is on the bootclasspath
        try {
            return Class.forName(toolClassName, false, null);
        } catch (ClassNotFoundException e) {
            trace(FINE, e);

            // if tool not on bootclasspath, look in default tools location (tools.jar)
            ClassLoader cl = (refToolClassLoader == null ? null : refToolClassLoader.get());
            if (cl == null) {
                File file = new File(System.getProperty("java.home"));
                if (file.getName().equalsIgnoreCase("jre"))
                    file = file.getParentFile();
                for (String name : defaultToolsLocation)
                    file = new File(file, name);

                // if tools not found, no point in trying a URLClassLoader
                // so rethrow the original exception.
                if (!file.exists())
                    throw e;

                URL[] urls = { file.toURI().toURL() };
                trace(FINE, urls[0].toString());

                cl = URLClassLoader.newInstance(urls);
                refToolClassLoader = new WeakReference<ClassLoader>(cl);
            }

            return Class.forName(toolClassName, false, cl);
        }
    }
}
