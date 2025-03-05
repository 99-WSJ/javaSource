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


/* Generated By:JJTree: Do not edit this line. JDMTrapNum.java */

package java8.sun.jmx.snmp.IPAcl;

class JDMTrapNum extends SimpleNode {
  protected int low=0;
  protected int high=0;

  JDMTrapNum(int id) {
    super(id);
  }

  JDMTrapNum(Parser p, int id) {
    super(p, id);
  }

  public static Node jjtCreate(int id) {
      return new com.sun.jmx.snmp.IPAcl.JDMTrapNum(id);
  }

  public static Node jjtCreate(Parser p, int id) {
      return new com.sun.jmx.snmp.IPAcl.JDMTrapNum(p, id);
  }
}
