/*
 * Copyright (c) 1999, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java8.javax.management;

import com.sun.jmx.mbeanserver.Introspector;

import javax.management.*;
import javax.management.Descriptor;
import javax.management.DescriptorKey;
import javax.management.MBeanFeatureInfo;
import javax.management.MBeanParameterInfo;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Objects;

/**
 * Describes a constructor exposed by an MBean.  Instances of this
 * class are immutable.  Subclasses may be mutable but this is not
 * recommended.
 *
 * @since 1.5
 */
public class MBeanConstructorInfo extends MBeanFeatureInfo implements Cloneable {

    /* Serial version */
    static final long serialVersionUID = 4433990064191844427L;

    static final javax.management.MBeanConstructorInfo[] NO_CONSTRUCTORS =
        new javax.management.MBeanConstructorInfo[0];

    /** @see MBeanInfo#arrayGettersSafe */
    private final transient boolean arrayGettersSafe;

    /**
     * @serial The signature of the method, that is, the class names of the arguments.
     */
    private final MBeanParameterInfo[] signature;

    /**
     * Constructs an <CODE>MBeanConstructorInfo</CODE> object.  The
     * {@link Descriptor} of the constructed object will include
     * fields contributed by any annotations on the {@code
     * Constructor} object that contain the {@link DescriptorKey}
     * meta-annotation.
     *
     * @param description A human readable description of the operation.
     * @param constructor The <CODE>java.lang.reflect.Constructor</CODE>
     * object describing the MBean constructor.
     */
    public MBeanConstructorInfo(String description, Constructor<?> constructor) {
        this(constructor.getName(), description,
             constructorSignature(constructor),
             Introspector.descriptorForElement(constructor));
    }

    /**
     * Constructs an <CODE>MBeanConstructorInfo</CODE> object.
     *
     * @param name The name of the constructor.
     * @param signature <CODE>MBeanParameterInfo</CODE> objects
     * describing the parameters(arguments) of the constructor.  This
     * may be null with the same effect as a zero-length array.
     * @param description A human readable description of the constructor.
     */
    public MBeanConstructorInfo(String name,
                                String description,
                                MBeanParameterInfo[] signature) {
        this(name, description, signature, null);
    }

    /**
     * Constructs an <CODE>MBeanConstructorInfo</CODE> object.
     *
     * @param name The name of the constructor.
     * @param signature <CODE>MBeanParameterInfo</CODE> objects
     * describing the parameters(arguments) of the constructor.  This
     * may be null with the same effect as a zero-length array.
     * @param description A human readable description of the constructor.
     * @param descriptor The descriptor for the constructor.  This may be null
     * which is equivalent to an empty descriptor.
     *
     * @since 1.6
     */
    public MBeanConstructorInfo(String name,
                                String description,
                                MBeanParameterInfo[] signature,
                                Descriptor descriptor) {
        super(name, description, descriptor);

        if (signature == null || signature.length == 0)
            signature = MBeanParameterInfo.NO_PARAMS;
        else
            signature = signature.clone();
        this.signature = signature;
        this.arrayGettersSafe =
            MBeanInfo.arrayGettersSafe(this.getClass(),
                                       javax.management.MBeanConstructorInfo.class);
    }


    /**
     * <p>Returns a shallow clone of this instance.  The clone is
     * obtained by simply calling <tt>super.clone()</tt>, thus calling
     * the default native shallow cloning mechanism implemented by
     * <tt>Object.clone()</tt>.  No deeper cloning of any internal
     * field is made.</p>
     *
     * <p>Since this class is immutable, cloning is chiefly of
     * interest to subclasses.</p>
     */
     public Object clone () {
         try {
             return super.clone() ;
         } catch (CloneNotSupportedException e) {
             // should not happen as this class is cloneable
             return null;
         }
     }

    /**
     * <p>Returns the list of parameters for this constructor.  Each
     * parameter is described by an <CODE>MBeanParameterInfo</CODE>
     * object.</p>
     *
     * <p>The returned array is a shallow copy of the internal array,
     * which means that it is a copy of the internal array of
     * references to the <CODE>MBeanParameterInfo</CODE> objects but
     * that each referenced <CODE>MBeanParameterInfo</CODE> object is
     * not copied.</p>
     *
     * @return  An array of <CODE>MBeanParameterInfo</CODE> objects.
     */
    public MBeanParameterInfo[] getSignature() {
        if (signature.length == 0)
            return signature;
        else
            return signature.clone();
    }

    private MBeanParameterInfo[] fastGetSignature() {
        if (arrayGettersSafe)
            return signature;
        else
            return getSignature();
    }

    public String toString() {
        return
            getClass().getName() + "[" +
            "description=" + getDescription() + ", " +
            "name=" + getName() + ", " +
            "signature=" + Arrays.asList(fastGetSignature()) + ", " +
            "descriptor=" + getDescriptor() +
            "]";
    }

    /**
     * Compare this MBeanConstructorInfo to another.
     *
     * @param o the object to compare to.
     *
     * @return true if and only if <code>o</code> is an MBeanConstructorInfo such
     * that its {@link #getName()}, {@link #getDescription()},
     * {@link #getSignature()}, and {@link #getDescriptor()}
     * values are equal (not necessarily
     * identical) to those of this MBeanConstructorInfo.  Two
     * signature arrays are equal if their elements are pairwise
     * equal.
     */
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof javax.management.MBeanConstructorInfo))
            return false;
        javax.management.MBeanConstructorInfo p = (javax.management.MBeanConstructorInfo) o;
        return (Objects.equals(p.getName(), getName()) &&
                Objects.equals(p.getDescription(), getDescription()) &&
                Arrays.equals(p.fastGetSignature(), fastGetSignature()) &&
                Objects.equals(p.getDescriptor(), getDescriptor()));
    }

    /* Unlike attributes and operations, it's quite likely we'll have
       more than one constructor with the same name and even
       description, so we include the parameter array in the hashcode.
       We don't include the description, though, because it could be
       quite long and yet the same between constructors.  Likewise for
       the descriptor.  */
    public int hashCode() {
        return Objects.hash(getName()) ^ Arrays.hashCode(fastGetSignature());
    }

    private static MBeanParameterInfo[] constructorSignature(Constructor<?> cn) {
        final Class<?>[] classes = cn.getParameterTypes();
        final Annotation[][] annots = cn.getParameterAnnotations();
        return MBeanOperationInfo.parameters(classes, annots);
    }
}
