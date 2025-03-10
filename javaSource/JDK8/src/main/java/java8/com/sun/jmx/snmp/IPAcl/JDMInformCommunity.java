/*
 * Copyright (c) 2000, 2003, Oracle and/or its affiliates. All rights reserved.
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

/* Generated By:JJTree: Do not edit this line. JDMInformCommunity.java */

package java8.com.sun.jmx.snmp.IPAcl;

import java8.sun.jmx.snmp.IPAcl.Parser;
import java8.sun.jmx.snmp.IPAcl.SimpleNode;

class JDMInformCommunity extends SimpleNode {
    protected String community= "";
    JDMInformCommunity(int id) {
        super(id);
    }

    JDMInformCommunity(Parser p, int id) {
        super(p, id);
    }

    public static com.sun.jmx.snmp.IPAcl.Node jjtCreate(int id) {
        return new com.sun.jmx.snmp.IPAcl.JDMInformCommunity(id);
    }

    public static com.sun.jmx.snmp.IPAcl.Node jjtCreate(Parser p, int id) {
        return new com.sun.jmx.snmp.IPAcl.JDMInformCommunity(p, id);
    }

    public String getCommunity() {
        return community;
    }
}
