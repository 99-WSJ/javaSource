/*
 * Copyright (c) 1999, 2010, Oracle and/or its affiliates. All rights reserved.
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

package java8.javax.naming.ldap;

import javax.naming.ldap.ExtendedRequest;

/**
  * This interface represents an LDAP extended operation response as defined in
  * <A HREF="http://www.ietf.org/rfc/rfc2251.txt">RFC 2251</A>.
  * <pre>
  *     ExtendedResponse ::= [APPLICATION 24] SEQUENCE {
  *          COMPONENTS OF LDAPResult,
  *          responseName     [10] LDAPOID OPTIONAL,
  *          response         [11] OCTET STRING OPTIONAL }
  * </pre>
  * It comprises an optional object identifier and an optional ASN.1 BER
  * encoded value.
  *
  *<p>
  * The methods in this class can be used by the application to get low
  * level information about the extended operation response. However, typically,
  * the application will be using methods specific to the class that
  * implements this interface. Such a class should have decoded the BER buffer
  * in the response and should provide methods that allow the user to
  * access that data in the response in a type-safe and friendly manner.
  *<p>
  * For example, suppose the LDAP server supported a 'get time' extended operation.
  * It would supply GetTimeRequest and GetTimeResponse classes.
  * The GetTimeResponse class might look like:
  *<blockquote><pre>
  * public class GetTimeResponse implements ExtendedResponse {
  *     public java.util.Date getDate() {...};
  *     public long getTime() {...};
  *     ....
  * }
  *</pre></blockquote>
  * A program would use then these classes as follows:
  *<blockquote><pre>
  * GetTimeResponse resp =
  *     (GetTimeResponse) ectx.extendedOperation(new GetTimeRequest());
  * java.util.Date now = resp.getDate();
  *</pre></blockquote>
  *
  * @author Rosanna Lee
  * @author Scott Seligman
  * @author Vincent Ryan
  *
  * @see ExtendedRequest
  * @since 1.3
  */

public interface ExtendedResponse extends java.io.Serializable {

    /**
      * Retrieves the object identifier of the response.
      * The LDAP protocol specifies that the response object identifier is optional.
      * If the server does not send it, the response will contain no ID (i.e. null).
      *
      * @return A possibly null object identifier string representing the LDAP
      *         <tt>ExtendedResponse.responseName</tt> component.
      */
    public String getID();

    /**
      * Retrieves the ASN.1 BER encoded value of the LDAP extended operation
      * response. Null is returned if the value is absent from the response
      * sent by the LDAP server.
      * The result is the raw BER bytes including the tag and length of
      * the response value. It does not include the response OID.
      *
      * @return A possibly null byte array representing the ASN.1 BER encoded
      *         contents of the LDAP <tt>ExtendedResponse.response</tt>
      *         component.
      */
    public byte[] getEncodedValue();

    //static final long serialVersionUID = -3320509678029180273L;
}
