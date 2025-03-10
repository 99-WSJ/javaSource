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

package java8.javax.xml.bind.annotation;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Generates a wrapper element around XML representation.
 *
 * This is primarily intended to be used to produce a wrapper
 * XML element around collections. The annotation therefore supports
 * two forms of serialization shown below.
 *
 * <pre>
 *    //Example: code fragment
 *      int[] names;
 *
 *    // XML Serialization Form 1 (Unwrapped collection)
 *    &lt;names> ... &lt;/names>
 *    &lt;names> ... &lt;/names>
 *
 *    // XML Serialization Form 2 ( Wrapped collection )
 *    &lt;wrapperElement>
 *       &lt;names> value-of-item &lt;/names>
 *       &lt;names> value-of-item &lt;/names>
 *       ....
 *    &lt;/wrapperElement>
 * </pre>
 *
 * <p> The two serialized XML forms allow a null collection to be
 * represented either by absence or presence of an element with a
 * nillable attribute.
 *
 * <p> <b>Usage</b> </p>
 * <p>
 * The <tt>@XmlElementWrapper</tt> annotation can be used with the
 * following program elements:
 * <ul>
 *   <li> JavaBean property </li>
 *   <li> non static, non transient field </li>
 * </ul>
 *
 * <p>The usage is subject to the following constraints:
 * <ul>
 *   <li> The property must be a collection property </li>
 *   <li> This annotation can be used with the following annotations:
 *            {@link XmlElement},
 *            {@link XmlElements},
 *            {@link XmlElementRef},
 *            {@link XmlElementRefs},
 *            {@link XmlJavaTypeAdapter}.</li>
 * </ul>
 *
 * <p>See "Package Specification" in javax.xml.bind.package javadoc for
 * additional common information.</p>
 *
 * @author <ul><li>Kohsuke Kawaguchi, Sun Microsystems, Inc.</li><li>Sekhar Vajjhala, Sun Microsystems, Inc.</li></ul>
 * @see XmlElement
 * @see XmlElements
 * @see XmlElementRef
 * @see XmlElementRefs
 * @since JAXB2.0
 *
 */

@Retention(RUNTIME) @Target({FIELD, METHOD})
public @interface XmlElementWrapper {
    /**
     * Name of the XML wrapper element. By default, the XML wrapper
     * element name is derived from the JavaBean property name.
     */
    String name() default "##default";

    /**
     * XML target namespace of the XML wrapper element.
     * <p>
     * If the value is "##default", then the namespace is determined
     * as follows:
     * <ol>
     *  <li>
     *  If the enclosing package has {@link XmlSchema} annotation,
     *  and its {@link XmlSchema#elementFormDefault() elementFormDefault}
     *  is {@link XmlNsForm#QUALIFIED QUALIFIED}, then the namespace of
     *  the enclosing class.
     *
     *  <li>
     *  Otherwise "" (which produces unqualified element in the default
     *  namespace.
     * </ol>
     */
    String namespace() default "##default";

    /**
     * If true, the absence of the collection is represented by
     * using <tt>xsi:nil='true'</tt>. Otherwise, it is represented by
     * the absence of the element.
     */
    boolean nillable() default false;

    /**
     * Customize the wrapper element declaration to be required.
     *
     * <p>
     * If required() is true, then the corresponding generated
     * XML schema element declaration will have <tt>minOccurs="1"</tt>,
     * to indicate that the wrapper element is always expected.
     *
     * <p>
     * Note that this only affects the schema generation, and
     * not the unmarshalling or marshalling capability. This is
     * simply a mechanism to let users express their application constraints
     * better.
     *
     * @since JAXB 2.1
     */
    boolean required() default false;
}
