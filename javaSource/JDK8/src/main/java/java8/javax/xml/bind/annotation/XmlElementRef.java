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

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

/**
 * <p>
 * Maps a JavaBean property to a XML element derived from property's type.
 * <p>
 * <b>Usage</b>
 * <p>
 * <tt>&#64;XmlElementRef</tt> annotation can be used with a
 * JavaBean property or from within {@link XmlElementRefs}
 * <p>
 * This annotation dynamically associates an XML element name with the JavaBean
 * property. When a JavaBean property is annotated with {@link
 * XmlElement}, the XML element name is statically derived from the
 * JavaBean property name. However, when this annotation is used, the
 * XML element name is derived from the instance of the type of the
 * JavaBean property at runtime.
 *
 * <h3> XML Schema substitution group support </h3>
 * XML Schema allows a XML document author to use XML element names
 * that were not statically specified in the content model of a
 * schema using substitution groups. Schema derived code provides
 * support for substitution groups using an <i>element property</i>,
 * (section 5.5.5, "Element Property" of JAXB 2.0 specification). An
 * element property method signature is of the form:
 * <pre>
 *     public void setTerm(JAXBElement<? extends Operator>);
 *     public JAXBElement<? extends Operator> getTerm();
 * </pre>
 * <p>
 * An element factory method annotated with  {@link XmlElementDecl} is
 * used to create a <tt>JAXBElement</tt> instance, containing an XML
 * element name. The presence of &#64;XmlElementRef annotation on an
 * element property indicates that the element name from <tt>JAXBElement</tt>
 * instance be used instead of deriving an XML element name from the
 * JavaBean property name.
 *
 * <p>
 * The usage is subject to the following constraints:
 * <ul>
 *   <li> If the collection item type (for collection property) or
 *        property type (for single valued property) is
 *        {@link javax.xml.bind.JAXBElement}, then
 *        <tt>&#64;XmlElementRef}.name()</tt> and <tt>&#64;XmlElementRef.namespace()</tt> must
 *        point an element factory method  with an @XmlElementDecl
 *        annotation in a class annotated  with @XmlRegistry (usually
 *        ObjectFactory class generated by  the schema compiler) :
 *        <ul>
 *            <li> @XmlElementDecl.name() must equal @XmlElementRef.name()  </li>
 *            <li> @XmlElementDecl.namespace() must equal @XmlElementRef.namespace(). </li>
 *        </ul>
 *   </li>
 *   <li> If the collection item type (for collection property) or
 *        property type  (for single valued property) is not
 *        {@link javax.xml.bind.JAXBElement}, then the type referenced by the
 *        property or field must be annotated  with {@link XmlRootElement}. </li>
 *   <li> This annotation can be used with the following annotations:
 *        {@link XmlElementWrapper}, {@link XmlJavaTypeAdapter}.
 *   </ul>
 *
 * <p>See "Package Specification" in javax.xml.bind.package javadoc for
 * additional common information.</p>
 *
 * <p><b>Example 1: Ant Task Example</b></p>
 * The following Java class hierarchy models an Ant build
 * script.  An Ant task corresponds to a class in the class
 * hierarchy. The XML element name of an Ant task is indicated by the
 * &#64;XmlRootElement annotation on its corresponding class.
 * <pre>
 *     &#64;XmlRootElement(name="target")
 *     class Target {
 *         // The presence of &#64;XmlElementRef indicates that the XML
 *         // element name will be derived from the &#64;XmlRootElement
 *         // annotation on the type (for e.g. "jar" for JarTask).
 *         &#64;XmlElementRef
 *         List&lt;Task> tasks;
 *     }
 *
 *     abstract class Task {
 *     }
 *
 *     &#64;XmlRootElement(name="jar")
 *     class JarTask extends Task {
 *         ...
 *     }
 *
 *     &#64;XmlRootElement(name="javac")
 *     class JavacTask extends Task {
 *         ...
 *     }
 *
 *     &lt;!-- XML Schema fragment -->
 *     &lt;xs:element name="target" type="Target">
 *     &lt;xs:complexType name="Target">
 *       &lt;xs:sequence>
 *         &lt;xs:choice maxOccurs="unbounded">
 *           &lt;xs:element ref="jar">
 *           &lt;xs:element ref="javac">
 *         &lt;/xs:choice>
 *       &lt;/xs:sequence>
 *     &lt;/xs:complexType>
 *
 * </pre>
 * <p>
 * Thus the following code fragment:
 * <pre>
 *     Target target = new Target();
 *     target.tasks.add(new JarTask());
 *     target.tasks.add(new JavacTask());
 *     marshal(target);
 * </pre>
 * will produce the following XML output:
 * <pre>
 *     &lt;target>
 *       &lt;jar>
 *         ....
 *       &lt;/jar>
 *       &lt;javac>
 *         ....
 *       &lt;/javac>
 *     &lt;/target>
 * </pre>
 * <p>
 * It is not an error to have a class that extends <tt>Task</tt>
 * that doesn't have {@link XmlRootElement}. But they can't show up in an
 * XML instance (because they don't have XML element names).
 *
 * <p><b>Example 2: XML Schema Susbstitution group support</b>
 * <p> The following example shows the annotations for XML Schema
 * substitution groups.  The annotations and the ObjectFactory are
 * derived from the schema.
 *
 * <pre>
 *     &#64;XmlElement
 *     class Math {
 *         //  The value of {@link #type()}is
 *         //  JAXBElement.class , which indicates the XML
 *         //  element name ObjectFactory - in general a class marked
 *         //  with &#64;XmlRegistry. (See ObjectFactory below)
 *         //
 *         //  The {@link #name()} is "operator", a pointer to a
 *         // factory method annotated with a
 *         //  {@link XmlElementDecl} with the name "operator". Since
 *         //  "operator" is the head of a substitution group that
 *         //  contains elements "add" and "sub" elements, "operator"
 *         //  element can be substituted in an instance document by
 *         //  elements "add" or "sub". At runtime, JAXBElement
 *         //  instance contains the element name that has been
 *         //  substituted in the XML document.
 *         //
 *         &#64;XmlElementRef(type=JAXBElement.class,name="operator")
 *         JAXBElement&lt;? extends Operator> term;
 *     }
 *
 *     &#64;XmlRegistry
 *     class ObjectFactory {
 *         &#64;XmlElementDecl(name="operator")
 *         JAXBElement&lt;Operator> createOperator(Operator o) {...}
 *         &#64;XmlElementDecl(name="add",substitutionHeadName="operator")
 *         JAXBElement&lt;Operator> createAdd(Operator o) {...}
 *         &#64;XmlElementDecl(name="sub",substitutionHeadName="operator")
 *         JAXBElement&lt;Operator> createSub(Operator o) {...}
 *     }
 *
 *     class Operator {
 *         ...
 *     }
 * </pre>
 * <p>
 * Thus, the following code fragment
 * <pre>
 *     Math m = new Math();
 *     m.term = new ObjectFactory().createAdd(new Operator());
 *     marshal(m);
 * </pre>
 * will produce the following XML output:
 * <pre>
 *     &lt;math>
 *       &lt;add>...&lt;/add>
 *     &lt;/math>
 * </pre>
 *
 *
 * @author <ul><li>Kohsuke Kawaguchi, Sun Microsystems,Inc. </li><li>Sekhar Vajjhala, Sun Microsystems, Inc.</li></ul>
 * @see XmlElementRefs
 * @since JAXB2.0
 */
@Retention(RUNTIME)
@Target({FIELD,METHOD})
public @interface XmlElementRef {
    /**
     * The Java type being referenced.
     * <p>
     * If the value is DEFAULT.class, the type is inferred from the
     * the type of the JavaBean property.
     */
    Class type() default DEFAULT.class;

    /**
     * This parameter and {@link #name()} are used to determine the
     * XML element for the JavaBean property.
     *
     * <p> If <tt>type()</tt> is <tt>JAXBElement.class</tt> , then
     * <tt>namespace()</tt> and <tt>name()</tt>
     * point to a factory method with {@link XmlElementDecl}. The XML
     * element name is the element name from the factory method's
     * {@link XmlElementDecl} annotation or if an element from its
     * substitution group (of which it is a head element) has been
     * substituted in the XML document, then the element name is from the
     * {@link XmlElementDecl} on the substituted element.
     *
     * <p> If {@link #type()} is not <tt>JAXBElement.class</tt>, then
     * the XML element name is the XML element name statically
     * associated with the type using the annotation {@link
     * XmlRootElement} on the type. If the type is not annotated with
     * an {@link XmlElementDecl}, then it is an error.
     *
     * <p> If <tt>type()</tt> is not <tt>JAXBElement.class</tt>, then
     * this value must be "".
     *
     */
    String namespace() default "";
    /**
     *
     * @see #namespace()
     */
    String name() default "##default";

    /**
     * Used in {@link javax.xml.bind.annotation.XmlElementRef#type()} to
     * signal that the type be inferred from the signature
     * of the property.
     */
    static final class DEFAULT {}

    /**
     * Customize the element declaration to be required.
     * <p>
     * If required() is true, then Javabean property is mapped to
     * an XML schema element declaration with minOccurs="1".
     * maxOccurs is "1" for a single valued property and "unbounded"
     * for a multivalued property.
     *
     * <p>
     * If required() is false, then the Javabean property is mapped
     * to XML Schema element declaration with minOccurs="0".
     * maxOccurs is "1" for a single valued property and "unbounded"
     * for a multivalued property.
     *
     * <p>
     * For compatibility with JAXB 2.1, this property defaults to <tt>true</tt>,
     * despite the fact that {@link XmlElement#required()} defaults to false.
     *
     * @since 2.2
     */
    boolean required() default true;
}
