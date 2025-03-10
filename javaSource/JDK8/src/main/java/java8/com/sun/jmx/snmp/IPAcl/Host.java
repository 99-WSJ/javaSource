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


package java8.sun.jmx.snmp.IPAcl;



// java import
//

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.acl.NotOwnerException;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;

import static com.sun.jmx.defaults.JmxProperties.SNMP_LOGGER;

/**
 * The class defines an abstract representation of a host.
 *
 */
abstract class Host extends SimpleNode implements Serializable {

    public Host(int id) {
        super(id);
    }

    public Host(Parser p, int id) {
        super(p, id);
    }

    protected abstract com.sun.jmx.snmp.IPAcl.PrincipalImpl createAssociatedPrincipal()
        throws UnknownHostException;

    protected abstract String getHname();

    public void buildAclEntries(com.sun.jmx.snmp.IPAcl.PrincipalImpl owner, com.sun.jmx.snmp.IPAcl.AclImpl acl) {
        // Create a principal
        //
        com.sun.jmx.snmp.IPAcl.PrincipalImpl p=null;
        try {
            p = createAssociatedPrincipal();
        } catch(UnknownHostException e) {
            if (SNMP_LOGGER.isLoggable(Level.FINEST)) {
                SNMP_LOGGER.logp(Level.FINEST, com.sun.jmx.snmp.IPAcl.Host.class.getName(),
                        "buildAclEntries",
                        "Cannot create ACL entry; got exception", e);
            }
            throw new IllegalArgumentException("Cannot create ACL entry for " + e.getMessage());
        }

        // Create an AclEntry
        //
        AclEntryImpl entry= null;
        try {
            entry = new AclEntryImpl(p);
            // Add permission
            //
            registerPermission(entry);
            acl.addEntry(owner, entry);
        } catch(UnknownHostException e) {
            if (SNMP_LOGGER.isLoggable(Level.FINEST)) {
                SNMP_LOGGER.logp(Level.FINEST, com.sun.jmx.snmp.IPAcl.Host.class.getName(),
                        "buildAclEntries",
                        "Cannot create ACL entry; got exception", e);
            }
            return;
        } catch(NotOwnerException a) {
            if (SNMP_LOGGER.isLoggable(Level.FINEST)) {
                SNMP_LOGGER.logp(Level.FINEST, com.sun.jmx.snmp.IPAcl.Host.class.getName(),
                        "buildAclEntries",
                        "Cannot create ACL entry; got exception", a);
            }
            return;
        }
    }

    private void registerPermission(AclEntryImpl entry) {
        JDMHost host= (JDMHost) jjtGetParent();
        JDMManagers manager= (JDMManagers) host.jjtGetParent();
        JDMAclItem acl= (JDMAclItem) manager.jjtGetParent();
        com.sun.jmx.snmp.IPAcl.JDMAccess access= acl.getAccess();
        access.putPermission(entry);
        com.sun.jmx.snmp.IPAcl.JDMCommunities comm= acl.getCommunities();
        comm.buildCommunities(entry);
    }

    public void buildTrapEntries(Hashtable<InetAddress, Vector<String>> dest) {

        com.sun.jmx.snmp.IPAcl.JDMHostTrap host= (com.sun.jmx.snmp.IPAcl.JDMHostTrap) jjtGetParent();
        com.sun.jmx.snmp.IPAcl.JDMTrapInterestedHost hosts= (com.sun.jmx.snmp.IPAcl.JDMTrapInterestedHost) host.jjtGetParent();
        JDMTrapItem trap = (JDMTrapItem) hosts.jjtGetParent();
        JDMTrapCommunity community = trap.getCommunity();
        String comm = community.getCommunity();

        InetAddress add = null;
        try {
            add = InetAddress.getByName(getHname());
        } catch(UnknownHostException e) {
            if (SNMP_LOGGER.isLoggable(Level.FINEST)) {
                SNMP_LOGGER.logp(Level.FINEST, com.sun.jmx.snmp.IPAcl.Host.class.getName(),
                        "buildTrapEntries",
                        "Cannot create TRAP entry; got exception", e);
            }
            return;
        }

        Vector<String> list = null;
        if (dest.containsKey(add)){
            list = dest.get(add);
            if (!list.contains(comm)){
                list.addElement(comm);
            }
        } else {
            list = new Vector<String>();
            list.addElement(comm);
            dest.put(add,list);
        }
    }

    public void buildInformEntries(Hashtable<InetAddress, Vector<String>> dest) {

        JDMHostInform host= (JDMHostInform) jjtGetParent();
        JDMInformInterestedHost hosts= (JDMInformInterestedHost) host.jjtGetParent();
        JDMInformItem inform = (JDMInformItem) hosts.jjtGetParent();
        com.sun.jmx.snmp.IPAcl.JDMInformCommunity community = inform.getCommunity();
        String comm = community.getCommunity();

        InetAddress add = null;
        try {
            add = InetAddress.getByName(getHname());
        } catch(UnknownHostException e) {
            if (SNMP_LOGGER.isLoggable(Level.FINEST)) {
                SNMP_LOGGER.logp(Level.FINEST, com.sun.jmx.snmp.IPAcl.Host.class.getName(),
                        "buildTrapEntries",
                        "Cannot create INFORM entry; got exception", e);
            }
            return;
        }

        Vector<String> list = null;
        if (dest.containsKey(add)){
            list = dest.get(add);
            if (!list.contains(comm)){
                list.addElement(comm);
            }
        } else {
            list = new Vector<String>();
            list.addElement(comm);
            dest.put(add,list);
        }
    }



}
