/*
 * Copyright (c) 2005, 2011, Oracle and/or its affiliates. All rights reserved.
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

package java8.javax.xml.ws.soap;


import java.util.Set;
import javax.xml.ws.Binding;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.MessageFactory;

/** The <code>SOAPBinding</code> interface is an abstraction for
 *  the SOAP binding.
 *
 *  @since JAX-WS 2.0
**/
public interface SOAPBinding extends Binding {

  /**
   * A constant representing the identity of the SOAP 1.1 over HTTP binding.
   */
  public static final String SOAP11HTTP_BINDING = "http://schemas.xmlsoap.org/wsdl/soap/http";

  /**
   * A constant representing the identity of the SOAP 1.2 over HTTP binding.
   */
  public static final String SOAP12HTTP_BINDING = "http://www.w3.org/2003/05/soap/bindings/HTTP/";

  /**
   * A constant representing the identity of the SOAP 1.1 over HTTP binding
   * with MTOM enabled by default.
   */
  public static final String SOAP11HTTP_MTOM_BINDING = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true";

  /**
   * A constant representing the identity of the SOAP 1.2 over HTTP binding
   * with MTOM enabled by default.
   */
  public static final String SOAP12HTTP_MTOM_BINDING = "http://www.w3.org/2003/05/soap/bindings/HTTP/?mtom=true";


  /** Gets the roles played by the SOAP binding instance.
   *
   *  @return Set&lt;String> The set of roles played by the binding instance.
  **/
  public Set<String> getRoles();

  /** Sets the roles played by the SOAP binding instance.
   *
   *  @param roles    The set of roles played by the binding instance.
   *  @throws WebServiceException On an error in the configuration of
   *                  the list of roles.
  **/
  public void setRoles(Set<String> roles);

  /**
   * Returns <code>true</code> if the use of MTOM is enabled.
   *
   * @return <code>true</code> if and only if the use of MTOM is enabled.
  **/

  public boolean isMTOMEnabled();

  /**
   * Enables or disables use of MTOM.
   *
   * @param flag   A <code>boolean</code> specifying whether the use of MTOM should
   *               be enabled or disabled.
   * @throws WebServiceException If the specified setting is not supported
   *                  by this binding instance.
   *
   **/
  public void setMTOMEnabled(boolean flag);

  /**
   * Gets the SAAJ <code>SOAPFactory</code> instance used by this SOAP binding.
   *
   * @return SOAPFactory instance used by this SOAP binding.
  **/
  public SOAPFactory getSOAPFactory();

  /**
   * Gets the SAAJ <code>MessageFactory</code> instance used by this SOAP binding.
   *
   * @return MessageFactory instance used by this SOAP binding.
  **/
  public MessageFactory getMessageFactory();
}
