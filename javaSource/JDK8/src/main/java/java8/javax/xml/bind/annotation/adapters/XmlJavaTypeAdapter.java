/*
 * Copyright (c) 2004, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java8.javax.xml.bind.annotation.adapters;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaTypes;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.PACKAGE;


/**
 * Use an adapter that implements {@link XmlAdapter} for custom marshaling.
 *
 * <p> <b> Usage: </b> </p>
 *
 * <p> The <tt>@XmlJavaTypeAdapter</tt> annotation can be used with the
 * following program elements:
 * <ul>
 *   <li> a JavaBean property </li>
 *   <li> field </li>
 *   <li> parameter </li>
 *   <li> package </li>
 *   <li> from within {@link XmlJavaTypeAdapters} </li>
 * </ul>
 *
 * <p> When <tt>@XmlJavaTypeAdapter</tt> annotation is defined on a
 * class, it applies to all references to the class.
 * <p> When <tt>@XmlJavaTypeAdapter</tt> annotation is defined at the
 * package level it applies to all references from within the package
 * to <tt>@XmlJavaTypeAdapter.type()</tt>.
 * <p> When <tt>@XmlJavaTypeAdapter</tt> annotation is defined on the
 * field, property or parameter, then the annotation applies to the
 * field, property or the parameter only.
 * <p> A <tt>@XmlJavaTypeAdapter</tt> annotation on a field, property
 * or parameter overrides the <tt>@XmlJavaTypeAdapter</tt> annotation
 * associated with the class being referenced by the field, property
 * or parameter.
 * <p> A <tt>@XmlJavaTypeAdapter</tt> annotation on a class overrides
 * the <tt>@XmlJavaTypeAdapter</tt> annotation specified at the
 * package level for that class.
 *
 * <p>This annotation can be used with the following other annotations:
 * {@link XmlElement}, {@link XmlAttribute}, {@link XmlElementRef},
 * {@link XmlElementRefs}, {@link XmlAnyElement}. This can also be
 * used at the package level with the following annotations:
 * {@link XmlAccessorType}, {@link XmlSchema}, {@link XmlSchemaType},
 * {@link XmlSchemaTypes}.
 *
 * <p><b> Example: </b> See example in {@link XmlAdapter}
 *
 * @author <ul><li>Sekhar Vajjhala, Sun Microsystems Inc.</li> <li> Kohsuke Kawaguchi, Sun Microsystems Inc.</li></ul>
 * @since JAXB2.0
 * @see XmlAdapter
 */

@Retention(RUNTIME) @Target({PACKAGE,FIELD,METHOD,TYPE,PARAMETER})
public @interface XmlJavaTypeAdapter {
    /**
     * Points to the class that converts a value type to a bound type or vice versa.
     * See {@link XmlAdapter} for more details.
     */
    Class<? extends XmlAdapter> value();

    /**
     * If this annotation is used at the package level, then value of
     * the type() must be specified.
     */

    Class type() default DEFAULT.class;

    /**
     * Used in {@link javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter#type()} to
     * signal that the type be inferred from the signature
     * of the field, property, parameter or the class.
     */

    static final class DEFAULT {}

}
