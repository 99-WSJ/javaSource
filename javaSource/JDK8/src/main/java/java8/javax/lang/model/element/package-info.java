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

/**
 * Interfaces used to model elements of the Java programming language.
 *
 * The term "element" in this package is used to refer to program
 * elements, the declared entities that make up a program.  Elements
 * include classes, interfaces, methods, constructors, and fields.
 * The interfaces in this package do not model the structure of a
 * program inside a method body; for example there is no
 * representation of a {@code for} loop or {@code try}-{@code finally}
 * block.  However, the interfaces can model some structures only
 * appearing inside method bodies, such as local variables and
 * anonymous classes.
 *
 * <p>When used in the context of annotation processing, an accurate
 * model of the element being represented must be returned.  As this
 * is a language model, the source code provides the fiducial
 * (reference) representation of the construct in question rather than
 * a representation in an executable output like a class file.
 * Executable output may serve as the basis for creating a modeling
 * element.  However, the process of translating source code to
 * executable output may not permit recovering some aspects of the
 * source code representation.  For example, annotations with
 * {@linkplain java.lang.annotation.RetentionPolicy#SOURCE source}
 * {@linkplain java.lang.annotation.Retention retention} cannot be
 * recovered from class files and class files might not be able to
 * provide source position information.
 *
 * Names of parameters may not be recoverable from class files.
 *
 * The {@linkplain javax.lang.model.element.Modifier modifiers} on an
 * element may differ in some cases including:
 *
 * <ul>
 * <li> {@code strictfp} on a class or interface
 * <li> {@code final} on a parameter
 * <li> {@code protected}, {@code private}, and {@code static} on classes and interfaces
 * </ul>
 *
 * Additionally, synthetic constructs in a class file, such as
 * accessor methods used in implementing nested classes and bridge
 * methods used in implementing covariant returns, are translation
 * artifacts outside of this model.
 *
 * <p>During annotation processing, operating on incomplete or
 * erroneous programs is necessary; however, there are fewer
 * guarantees about the nature of the resulting model.  If the source
 * code is not syntactically well-formed or has some other
 * irrecoverable error that could not be removed by the generation of
 * new types, a model may or may not be provided as a quality of
 * implementation issue.
 * If a program is syntactically valid but erroneous in some other
 * fashion, any returned model must have no less information than if
 * all the method bodies in the program were replaced by {@code "throw
 * new RuntimeException();"}.  If a program refers to a missing type XYZ,
 * the returned model must contain no less information than if the
 * declaration of type XYZ were assumed to be {@code "class XYZ {}"},
 * {@code "interface XYZ {}"}, {@code "enum XYZ {}"}, or {@code
 * "@interface XYZ {}"}. If a program refers to a missing type {@code
 * XYZ<K1, ... ,Kn>}, the returned model must contain no less
 * information than if the declaration of XYZ were assumed to be
 * {@code "class XYZ<T1, ... ,Tn> {}"} or {@code "interface XYZ<T1,
 * ... ,Tn> {}"}
 *
 * <p> Unless otherwise specified in a particular implementation, the
 * collections returned by methods in this package should be expected
 * to be unmodifiable by the caller and unsafe for concurrent access.
 *
 * <p> Unless otherwise specified, methods in this package will throw
 * a {@code NullPointerException} if given a {@code null} argument.
 *
 * @author Joseph D. Darcy
 * @author Scott Seligman
 * @author Peter von der Ah&eacute;
 * @since 1.6
 */
package java8.javax.lang.model.element;
