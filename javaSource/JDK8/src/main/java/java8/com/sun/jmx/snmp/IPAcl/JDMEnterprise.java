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


/* Generated By:JJTree: Do not edit this line. JDMEnterprise.java */

package java8.sun.jmx.snmp.IPAcl;

class JDMEnterprise extends SimpleNode {
  protected String enterprise= "";

  JDMEnterprise(int id) {
    super(id);
  }

  JDMEnterprise(Parser p, int id) {
    super(p, id);
  }

  public static com.sun.jmx.snmp.IPAcl.Node jjtCreate(int id) {
      return new com.sun.jmx.snmp.IPAcl.JDMEnterprise(id);
  }

  public static com.sun.jmx.snmp.IPAcl.Node jjtCreate(Parser p, int id) {
      return new com.sun.jmx.snmp.IPAcl.JDMEnterprise(p, id);
  }
}
