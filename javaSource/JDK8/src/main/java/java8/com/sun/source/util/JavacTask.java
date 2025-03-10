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

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;
import com.sun.tools.javac.api.BasicJavacTask;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.util.Context;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import java.io.IOException;

/**
 * Provides access to functionality specific to the JDK Java Compiler, javac.
 *
 * @author Peter von der Ah&eacute;
 * @author Jonathan Gibbons
 * @since 1.6
 */
@jdk.Exported
public abstract class JavacTask implements CompilationTask {

    /**
     * Get the {@code JavacTask} for a {@code ProcessingEnvironment}.
     * If the compiler is being invoked using a
     * {@link CompilationTask CompilationTask},
     * then that task will be returned.
     * @param processingEnvironment the processing environment
     * @return the {@code JavacTask} for a {@code ProcessingEnvironment}
     * @since 1.8
     */
    public static JavacTask instance(ProcessingEnvironment processingEnvironment) {
        if (!processingEnvironment.getClass().getName().equals(
                "com.sun.tools.javac.processing.JavacProcessingEnvironment"))
            throw new IllegalArgumentException();
        Context c = ((JavacProcessingEnvironment) processingEnvironment).getContext();
        JavacTask t = c.get(JavacTask.class);
        return (t != null) ? t : new BasicJavacTask(c, true);
    }

    /**
     * Parse the specified files returning a list of abstract syntax trees.
     *
     * @return a list of abstract syntax trees
     * @throws IOException if an unhandled I/O error occurred in the compiler.
     * @throws IllegalStateException if the operation cannot be performed at this time.
     */
    public abstract Iterable<? extends CompilationUnitTree> parse()
        throws IOException;

    /**
     * Complete all analysis.
     *
     * @return a list of elements that were analyzed
     * @throws IOException if an unhandled I/O error occurred in the compiler.
     * @throws IllegalStateException if the operation cannot be performed at this time.
     */
    public abstract Iterable<? extends Element> analyze() throws IOException;

    /**
     * Generate code.
     *
     * @return a list of files that were generated
     * @throws IOException if an unhandled I/O error occurred in the compiler.
     * @throws IllegalStateException if the operation cannot be performed at this time.
     */
    public abstract Iterable<? extends JavaFileObject> generate() throws IOException;

    /**
     * The specified listener will receive notification of events
     * describing the progress of this compilation task.
     *
     * If another listener is receiving notifications as a result of a prior
     * call of this method, then that listener will no longer receive notifications.
     *
     * Informally, this method is equivalent to calling {@code removeTaskListener} for
     * any listener that has been previously set, followed by {@code addTaskListener}
     * for the new listener.
     *
     * @throws IllegalStateException if the specified listener has already been added.
     */
    public abstract void setTaskListener(TaskListener taskListener);

    /**
     * The specified listener will receive notification of events
     * describing the progress of this compilation task.
     *
     * This method may be called at any time before or during the compilation.
     *
     * @throws IllegalStateException if the specified listener has already been added.
     * @since 1.8
     */
    public abstract void addTaskListener(TaskListener taskListener);

    /**
     * The specified listener will no longer receive notification of events
     * describing the progress of this compilation task.
     *
     * This method may be called at any time before or during the compilation.
     *
     * @since 1.8
     */
    public abstract void removeTaskListener(TaskListener taskListener);

    /**
     * Get a type mirror of the tree node determined by the specified path.
     * This method has been superceded by methods on
     * {@link Trees Trees}.
     * @see Trees#getTypeMirror
     */
    public abstract TypeMirror getTypeMirror(Iterable<? extends Tree> path);

    /**
     * Get a utility object for dealing with program elements.
     */
    public abstract Elements getElements();

    /**
     * Get a utility object for dealing with type mirrors.
     */
    public abstract Types getTypes();
}
