/*
 * Copyright (c) 2003, 2004, Oracle and/or its affiliates. All rights reserved.
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

package java8.sun.javadoc;


import java8.com.sun.javadoc.AnnotationValue;
import java8.com.sun.javadoc.MethodDoc;

/**
 * Represents an element of an annotation type.
 *
 * @author Scott Seligman
 * @since 1.5
 */
public interface AnnotationTypeElementDoc extends MethodDoc {

    /**
     * Returns the default value of this element.
     * Returns null if this element has no default.
     *
     * @return the default value of this element.
     */
    AnnotationValue defaultValue();
}
