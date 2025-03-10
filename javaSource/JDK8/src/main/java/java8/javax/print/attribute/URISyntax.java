/*
 * Copyright (c) 2000, 2004, Oracle and/or its affiliates. All rights reserved.
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


package java8.javax.print.attribute;

import java.io.Serializable;
import java.net.URI;

/**
 * Class URISyntax is an abstract base class providing the common
 * implementation of all attributes whose value is a Uniform Resource
 * Identifier (URI). Once constructed, a URI attribute's value is immutable.
 * <P>
 *
 * @author  Alan Kaminsky
 */
public abstract class URISyntax implements Serializable, Cloneable {

    private static final long serialVersionUID = -7842661210486401678L;

    /**
     * URI value of this URI attribute.
     * @serial
     */
    private URI uri;

    /**
     * Constructs a URI attribute with the specified URI.
     *
     * @param  uri  URI.
     *
     * @exception  NullPointerException
     *     (unchecked exception) Thrown if <CODE>uri</CODE> is null.
     */
    protected URISyntax(URI uri) {
        this.uri = verify (uri);
    }

    private static URI verify(URI uri) {
        if (uri == null) {
            throw new NullPointerException(" uri is null");
        }
        return uri;
    }

    /**
     * Returns this URI attribute's URI value.
     * @return the URI.
     */
    public URI getURI()  {
        return uri;
    }

    /**
     * Returns a hashcode for this URI attribute.
     *
     * @return  A hashcode value for this object.
     */
    public int hashCode() {
        return uri.hashCode();
    }

    /**
     * Returns whether this URI attribute is equivalent to the passed in
     * object.
     * To be equivalent, all of the following conditions must be true:
     * <OL TYPE=1>
     * <LI>
     * <CODE>object</CODE> is not null.
     * <LI>
     * <CODE>object</CODE> is an instance of class URISyntax.
     * <LI>
     * This URI attribute's underlying URI and <CODE>object</CODE>'s
     * underlying URI are equal.
     * </OL>
     *
     * @param  object  Object to compare to.
     *
     * @return  True if <CODE>object</CODE> is equivalent to this URI
     *          attribute, false otherwise.
     */
    public boolean equals(Object object) {
        return(object != null &&
               object instanceof javax.print.attribute.URISyntax &&
               this.uri.equals (((javax.print.attribute.URISyntax) object).uri));
    }

    /**
     * Returns a String identifying this URI attribute. The String is the
     * string representation of the attribute's underlying URI.
     *
     * @return  A String identifying this object.
     */
    public String toString() {
        return uri.toString();
    }

}
