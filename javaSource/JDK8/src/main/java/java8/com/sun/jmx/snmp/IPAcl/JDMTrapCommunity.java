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


/* Generated By:JJTree: Do not edit this line. JDMTrapCommunity.java */

package java8.sun.jmx.snmp.IPAcl;

class JDMTrapCommunity extends com.sun.jmx.snmp.IPAcl.SimpleNode {
  protected String community= "";
  JDMTrapCommunity(int id) {
    super(id);
  }

  JDMTrapCommunity(com.sun.jmx.snmp.IPAcl.Parser p, int id) {
    super(p, id);
  }

  public static com.sun.jmx.snmp.IPAcl.Node jjtCreate(int id) {
      return new com.sun.jmx.snmp.IPAcl.JDMTrapCommunity(id);
  }

  public static com.sun.jmx.snmp.IPAcl.Node jjtCreate(com.sun.jmx.snmp.IPAcl.Parser p, int id) {
      return new com.sun.jmx.snmp.IPAcl.JDMTrapCommunity(p, id);
  }

  public String getCommunity() {
        return community;
  }
}
