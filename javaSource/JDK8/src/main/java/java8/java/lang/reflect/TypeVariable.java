/*
 * Copyright (c) 2003, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java8.java.lang.reflect;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.MalformedParameterizedTypeException;
import java.lang.reflect.Type;

/**
 * TypeVariable is the common superinterface for type variables of kinds.
 * A type variable is created the first time it is needed by a reflective
 * method, as specified in this package.  If a type variable t is referenced
 * by a type (i.e, class, interface or annotation type) T, and T is declared
 * by the nth enclosing class of T (see JLS 8.1.2), then the creation of t
 * requires the resolution (see JVMS 5) of the ith enclosing class of T,
 * for i = 0 to n, inclusive. Creating a type variable must not cause the
 * creation of its bounds. Repeated creation of a type variable has no effect.
 *
 * <p>Multiple objects may be instantiated at run-time to
 * represent a given type variable. Even though a type variable is
 * created only once, this does not imply any requirement to cache
 * instances representing the type variable. However, all instances
 * representing a type variable must be equal() to each other.
 * As a consequence, users of type variables must not rely on the identity
 * of instances of classes implementing this interface.
 *
 * @param <D> the type of generic declaration that declared the
 * underlying type variable.
 *
 * @since 1.5
 */
public interface TypeVariable<D extends GenericDeclaration> extends Type, AnnotatedElement {
    /**
     * Returns an array of {@code Type} objects representing the
     * upper bound(s) of this type variable.  Note that if no upper bound is
     * explicitly declared, the upper bound is {@code Object}.
     *
     * <p>For each upper bound B: <ul> <li>if B is a parameterized
     * type or a type variable, it is created, (see {@link
     * java.lang.reflect.ParameterizedType ParameterizedType} for the
     * details of the creation process for parameterized types).
     * <li>Otherwise, B is resolved.  </ul>
     *
     * @throws TypeNotPresentException  if any of the
     *     bounds refers to a non-existent type declaration
     * @throws MalformedParameterizedTypeException if any of the
     *     bounds refer to a parameterized type that cannot be instantiated
     *     for any reason
     * @return an array of {@code Type}s representing the upper
     *     bound(s) of this type variable
    */
    Type[] getBounds();

    /**
     * Returns the {@code GenericDeclaration} object representing the
     * generic declaration declared this type variable.
     *
     * @return the generic declaration declared for this type variable.
     *
     * @since 1.5
     */
    D getGenericDeclaration();

    /**
     * Returns the name of this type variable, as it occurs in the source code.
     *
     * @return the name of this type variable, as it appears in the source code
     */
    String getName();

    /**
     * Returns an array of AnnotatedType objects that represent the use of
     * types to denote the upper bounds of the type parameter represented by
     * this TypeVariable. The order of the objects in the array corresponds to
     * the order of the bounds in the declaration of the type parameter.
     *
     * Returns an array of length 0 if the type parameter declares no bounds.
     *
     * @return an array of objects representing the upper bounds of the type variable
     * @since 1.8
     */
     AnnotatedType[] getAnnotatedBounds();
}
