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
package java8.sun.source.util;

import javax.tools.StandardLocation;
import java.util.ServiceLoader;

/**
 * The interface for a javac plug-in.
 *
 * <p>The javac plug-in mechanism allows a user to specify one or more plug-ins
 * on the javac command line, to be started soon after the compilation
 * has begun. Plug-ins are identified by a user-friendly name. Each plug-in that
 * is started will be passed an array of strings, which may be used to
 * provide the plug-in with values for any desired options or other arguments.
 *
 * <p>Plug-ins are located via a {@link ServiceLoader},
 * using the same class path as annotation processors (i.e.
 * {@link StandardLocation#ANNOTATION_PROCESSOR_PATH ANNOTATION_PROCESSOR_PATH} or
 * {@code -processorpath}).
 *
 * <p>It is expected that a typical plug-in will simply register a
 * {@link TaskListener} to be informed of events during the execution
 * of the compilation, and that the rest of the work will be done
 * by the task listener.
 *
 * @since 1.8
 */
@jdk.Exported
public interface Plugin {
    /**
     * Get the user-friendly name of this plug-in.
     * @return the user-friendly name of the plug-in
     */
    String getName();

    /**
     * Initialize the plug-in for a given compilation task.
     * @param task The compilation task that has just been started
     * @param args Arguments, if any, for the plug-in
     */
    void init(JavacTask task, String... args);
}
