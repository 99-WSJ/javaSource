/*
 * Copyright (c) 1997, 2007, Oracle and/or its affiliates. All rights reserved.
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


/* Generated By:JJTree: Do not edit this line. JDMIpAddress.java */

package java8.sun.jmx.snmp.IPAcl;

import java.net.UnknownHostException;

class JDMIpAddress extends com.sun.jmx.snmp.IPAcl.Host {
  private static final long serialVersionUID = 849729919486384484L;

  protected StringBuffer address= new StringBuffer();

  JDMIpAddress(int id) {
    super(id);
  }

  JDMIpAddress(com.sun.jmx.snmp.IPAcl.Parser p, int id) {
    super(p, id);
  }

  public static com.sun.jmx.snmp.IPAcl.Node jjtCreate(int id) {
      return new com.sun.jmx.snmp.IPAcl.JDMIpAddress(id);
  }

  public static com.sun.jmx.snmp.IPAcl.Node jjtCreate(com.sun.jmx.snmp.IPAcl.Parser p, int id) {
      return new com.sun.jmx.snmp.IPAcl.JDMIpAddress(p, id);
  }

  protected String getHname() {
          return address.toString();
  }

  protected com.sun.jmx.snmp.IPAcl.PrincipalImpl createAssociatedPrincipal()
    throws UnknownHostException {
      return new com.sun.jmx.snmp.IPAcl.PrincipalImpl(address.toString());
  }
}
