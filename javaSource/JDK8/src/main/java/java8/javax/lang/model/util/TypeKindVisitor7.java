/*
 * Copyright (c) 2010, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java8.javax.lang.model.util;

import javax.lang.model.type.*;
import javax.annotation.processing.SupportedSourceVersion;
import static javax.lang.model.SourceVersion.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.util.TypeKindVisitor6;
import javax.lang.model.util.TypeKindVisitor8;

/**
 * A visitor of types based on their {@linkplain TypeKind kind} with
 * default behavior appropriate for the {@link SourceVersion#RELEASE_7
 * RELEASE_7} source version.  For {@linkplain
 * TypeMirror types} <tt><i>XYZ</i></tt> that may have more than one
 * kind, the <tt>visit<i>XYZ</i></tt> methods in this class delegate
 * to the <tt>visit<i>XYZKind</i></tt> method corresponding to the
 * first argument's kind.  The <tt>visit<i>XYZKind</i></tt> methods
 * call {@link #defaultAction defaultAction}, passing their arguments
 * to {@code defaultAction}'s corresponding parameters.
 *
 * <p> Methods in this class may be overridden subject to their
 * general contract.  Note that annotating methods in concrete
 * subclasses with {@link Override @Override} will help
 * ensure that methods are overridden as intended.
 *
 * <p> <b>WARNING:</b> The {@code TypeVisitor} interface implemented
 * by this class may have methods added to it in the future to
 * accommodate new, currently unknown, language structures added to
 * future versions of the Java&trade; programming language.
 * Therefore, methods whose names begin with {@code "visit"} may be
 * added to this class in the future; to avoid incompatibilities,
 * classes which extend this class should not declare any instance
 * methods with names beginning with {@code "visit"}.
 *
 * <p>When such a new visit method is added, the default
 * implementation in this class will be to call the {@link
 * #visitUnknown visitUnknown} method.  A new type kind visitor class
 * will also be introduced to correspond to the new language level;
 * this visitor will have different default behavior for the visit
 * method in question.  When the new visitor is introduced, all or
 * portions of this visitor may be deprecated.
 *
 * <p>Note that adding a default implementation of a new visit method
 * in a visitor class will occur instead of adding a <em>default
 * method</em> directly in the visitor interface since a Java SE 8
 * language feature cannot be used to this version of the API since
 * this version is required to be runnable on Java SE 7
 * implementations.  Future versions of the API that are only required
 * to run on Java SE 8 and later may take advantage of default methods
 * in this situation.
 *
 * @param <R> the return type of this visitor's methods.  Use {@link
 *            Void} for visitors that do not need to return results.
 * @param <P> the type of the additional parameter to this visitor's
 *            methods.  Use {@code Void} for visitors that do not need an
 *            additional parameter.
 *
 * @see TypeKindVisitor6
 * @see TypeKindVisitor8
 * @since 1.7
 */
@SupportedSourceVersion(RELEASE_7)
public class TypeKindVisitor7<R, P> extends TypeKindVisitor6<R, P> {
    /**
     * Constructor for concrete subclasses to call; uses {@code null}
     * for the default value.
     */
    protected TypeKindVisitor7() {
        super(null);
    }

    /**
     * Constructor for concrete subclasses to call; uses the argument
     * for the default value.
     *
     * @param defaultValue the value to assign to {@link #DEFAULT_VALUE}
     */
    protected TypeKindVisitor7(R defaultValue) {
        super(defaultValue);
    }

    /**
     * This implementation visits a {@code UnionType} by calling
     * {@code defaultAction}.
     *
     * @param t  {@inheritDoc}
     * @param p  {@inheritDoc}
     * @return the result of {@code defaultAction}
     */
    @Override
    public R visitUnion(UnionType t, P p) {
        return defaultAction(t, p);
    }
}
