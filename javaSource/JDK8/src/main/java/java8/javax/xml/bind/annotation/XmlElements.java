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

package java8.javax.xml.bind.annotation;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * <p>
 * A container for multiple @{@link XmlElement} annotations.
 *
 * Multiple annotations of the same type are not allowed on a program
 * element. This annotation therefore serves as a container annotation
 * for multiple &#64;XmlElements as follows:
 *
 * <pre>
 * &#64;XmlElements({ @XmlElement(...),@XmlElement(...) })
 * </pre>
 *
 * <p>The <tt>@XmlElements</tt> annnotation can be used with the
 * following program elements: </p>
 * <ul>
 *   <li> a JavaBean property </li>
 *   <li> non static, non transient field </li>
 * </ul>
 *
 * This annotation is intended for annotation a JavaBean collection
 * property (e.g. List).
 *
 * <p><b>Usage</b></p>
 *
 * <p>The usage is subject to the following constraints:
 * <ul>
 *   <li> This annotation can be used with the following
 *        annotations: @{@link XmlIDREF}, @{@link XmlElementWrapper}. </li>
 *   <li> If @XmlIDREF is also specified on the JavaBean property,
 *        then each &#64;XmlElement.type() must contain a JavaBean
 *        property annotated with <tt>&#64;XmlID</tt>.</li>
 * </ul>
 *
 * <p>See "Package Specification" in javax.xml.bind.package javadoc for
 * additional common information.</p>
 *
 * <hr>
 *
 * <p><b>Example 1:</b> Map to a list of elements</p>
 * <pre>
 *
 *    // Mapped code fragment
 *    public class Foo {
 *        &#64;XmlElements(
 *            &#64;XmlElement(name="A", type=Integer.class),
 *            &#64;XmlElement(name="B", type=Float.class)
 *         }
 *         public List items;
 *    }
 *
 *    &lt;!-- XML Representation for a List of {1,2.5}
 *            XML output is not wrapped using another element -->
 *    ...
 *    &lt;A> 1 &lt;/A>
 *    &lt;B> 2.5 &lt;/B>
 *    ...
 *
 *    &lt;!-- XML Schema fragment -->
 *    &lt;xs:complexType name="Foo">
 *      &lt;xs:sequence>
 *        &lt;xs:choice minOccurs="0" maxOccurs="unbounded">
 *          &lt;xs:element name="A" type="xs:int"/>
 *          &lt;xs:element name="B" type="xs:float"/>
 *        &lt;xs:choice>
 *      &lt;/xs:sequence>
 *    &lt;/xs:complexType>
 *
 * </pre>
 *
 * <p><b>Example 2:</b> Map to a list of elements wrapped with another element
 * </p>
 * <pre>
 *
 *    // Mapped code fragment
 *    public class Foo {
 *        &#64;XmlElementWrapper(name="bar")
 *        &#64;XmlElements(
 *            &#64;XmlElement(name="A", type=Integer.class),
 *            &#64;XmlElement(name="B", type=Float.class)
 *        }
 *        public List items;
 *    }
 *
 *    &lt;!-- XML Schema fragment -->
 *    &lt;xs:complexType name="Foo">
 *      &lt;xs:sequence>
 *        &lt;xs:element name="bar">
 *          &lt;xs:complexType>
 *            &lt;xs:choice minOccurs="0" maxOccurs="unbounded">
 *              &lt;xs:element name="A" type="xs:int"/>
 *              &lt;xs:element name="B" type="xs:float"/>
 *            &lt;/xs:choice>
 *          &lt;/xs:complexType>
 *        &lt;/xs:element>
 *      &lt;/xs:sequence>
 *    &lt;/xs:complexType>
 * </pre>
 *
 * <p><b>Example 3:</b> Change element name based on type using an adapter.
 * </p>
 * <pre>
 *    class Foo {
 *       &#64;XmlJavaTypeAdapter(QtoPAdapter.class)
 *       &#64;XmlElements({
 *           &#64;XmlElement(name="A",type=PX.class),
 *           &#64;XmlElement(name="B",type=PY.class)
 *       })
 *       Q bar;
 *    }
 *
 *    &#64;XmlType abstract class P {...}
 *    &#64;XmlType(name="PX") class PX extends P {...}
 *    &#64;XmlType(name="PY") class PY extends P {...}
 *
 *    &lt;!-- XML Schema fragment -->
 *    &lt;xs:complexType name="Foo">
 *      &lt;xs:sequence>
 *        &lt;xs:element name="bar">
 *          &lt;xs:complexType>
 *            &lt;xs:choice minOccurs="0" maxOccurs="unbounded">
 *              &lt;xs:element name="A" type="PX"/>
 *              &lt;xs:element name="B" type="PY"/>
 *            &lt;/xs:choice>
 *          &lt;/xs:complexType>
 *        &lt;/xs:element>
 *      &lt;/xs:sequence>
 *    &lt;/xs:complexType>
 * </pre>
 *
 * @author <ul><li>Kohsuke Kawaguchi, Sun Microsystems, Inc.</li><li>Sekhar Vajjhala, Sun Microsystems, Inc.</li></ul>
 * @see XmlElement
 * @see XmlElementRef
 * @see XmlElementRefs
 * @see XmlJavaTypeAdapter
 * @since JAXB2.0
 */
@Retention(RUNTIME) @Target({FIELD,METHOD})
public @interface XmlElements {
    /**
     * Collection of @{@link XmlElement} annotations
     */
    XmlElement[] value();
}
