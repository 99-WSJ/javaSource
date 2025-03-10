/*
 * Copyright (c) 2001, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java8.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpDefinitions;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpVarBind;
import com.sun.jmx.snmp.agent.SnmpMibAgent;
import com.sun.jmx.snmp.agent.SnmpMibRequest;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.logging.Level;

import static com.sun.jmx.defaults.JmxProperties.SNMP_ADAPTOR_LOGGER;

/**
 * A simple MIB agent that implements SNMP calls (get, set, getnext and getbulk) in a way that only errors or exceptions are returned. Every call done on this agent fails. Error handling is done according to the manager's SNMP protocol version.
 * <P>It is used by <CODE>SnmpAdaptorServer</CODE> for its default agent behavior. When a received Oid doesn't match, this agent is called to fill the result list with errors.</P>
 * <p><b>This API is a Sun Microsystems internal API  and is subject
 * to change without notice.</b></p>
 * @since 1.5
 *
 */

public class SnmpErrorHandlerAgent extends SnmpMibAgent
        implements Serializable {
    private static final long serialVersionUID = 7751082923508885650L;

    public SnmpErrorHandlerAgent() {}

    /**
     * Initializes the MIB (with no registration of the MBeans into the
     * MBean server). Does nothing.
     *
     * @exception IllegalAccessException The MIB cannot be initialized.
     */

    @Override
    public void init() throws IllegalAccessException {
    }

    /**
     * Initializes the MIB but each single MBean representing the MIB
     * is inserted into the MBean server.
     *
     * @param server The MBean server to register the service with.
     * @param name The object name.
     *
     * @return The passed name parameter.
     *
     * @exception Exception
     */

    @Override
    public ObjectName preRegister(MBeanServer server, ObjectName name)
        throws Exception {
        return name;
    }

    /**
     * Gets the root object identifier of the MIB.
     * <P>The root object identifier is the object identifier uniquely
     * identifying the MIB.
     *
     * @return The returned oid is null.
     */

    @Override
    public long[] getRootOid() {
        return null;
    }

    /**
     * Processes a <CODE>get</CODE> operation. It will throw an exception for V1 requests or it will set exceptions within the list for V2 requests.
     *
     * @param inRequest The SnmpMibRequest object holding the list of variable to be retrieved.
     *
     * @exception SnmpStatusException An error occurred during the operation.
     */

    @Override
    public void get(SnmpMibRequest inRequest) throws SnmpStatusException {

        SNMP_ADAPTOR_LOGGER.logp(Level.FINEST,
                com.sun.jmx.snmp.agent.SnmpErrorHandlerAgent.class.getName(),
                "get", "Get in Exception");

        if(inRequest.getVersion() == SnmpDefinitions.snmpVersionOne)
            throw new SnmpStatusException(SnmpStatusException.noSuchName);

        Enumeration<SnmpVarBind> l = inRequest.getElements();
        while(l.hasMoreElements()) {
            SnmpVarBind varbind = l.nextElement();
            varbind.setNoSuchObject();
        }
    }

    /**
     * Checks if a <CODE>set</CODE> operation can be performed.
     * If the operation can not be performed, the method should emit a
     * <CODE>SnmpStatusException</CODE>.
     *
     * @param inRequest The SnmpMibRequest object holding the list of variables to
     *            be set. This list is composed of
     *            <CODE>SnmpVarBind</CODE> objects.
     *
     * @exception SnmpStatusException The <CODE>set</CODE> operation
     *    cannot be performed.
     */

    @Override
    public void check(SnmpMibRequest inRequest) throws SnmpStatusException {

        SNMP_ADAPTOR_LOGGER.logp(Level.FINEST,
                com.sun.jmx.snmp.agent.SnmpErrorHandlerAgent.class.getName(),
                "check", "Check in Exception");

        throw new SnmpStatusException(SnmpDefinitions.snmpRspNotWritable);
    }

    /**
     * Processes a <CODE>set</CODE> operation. Should never be called (check previously called having failed).
     *
     * @param inRequest The SnmpMibRequest object holding the list of variable to be set.
     *
     * @exception SnmpStatusException An error occurred during the operation.
     */

    @Override
    public void set(SnmpMibRequest inRequest) throws SnmpStatusException {

        SNMP_ADAPTOR_LOGGER.logp(Level.FINEST,
                com.sun.jmx.snmp.agent.SnmpErrorHandlerAgent.class.getName(),
                "set", "Set in Exception, CANNOT be called");

        throw new SnmpStatusException(SnmpDefinitions.snmpRspNotWritable);
    }

    /**
     * Processes a <CODE>getNext</CODE> operation. It will throw an exception for V1 requests or it will set exceptions within the list for V2 requests..
     *
     * @param inRequest The SnmpMibRequest object holding the list of variables to be retrieved.
     *
     * @exception SnmpStatusException An error occurred during the operation.
     */

    @Override
    public void getNext(SnmpMibRequest inRequest) throws SnmpStatusException {

        SNMP_ADAPTOR_LOGGER.logp(Level.FINEST,
                com.sun.jmx.snmp.agent.SnmpErrorHandlerAgent.class.getName(),
                "getNext", "GetNext in Exception");

        if(inRequest.getVersion() == SnmpDefinitions.snmpVersionOne)
            throw new SnmpStatusException(SnmpStatusException.noSuchName);

        Enumeration<SnmpVarBind> l = inRequest.getElements();
        while(l.hasMoreElements()) {
            SnmpVarBind varbind = l.nextElement();
            varbind.setEndOfMibView();
        }
    }

    /**
     * Processes a <CODE>getBulk</CODE> operation. It will throw an exception if the request is a V1 one or it will set exceptions within the list for V2 ones.
     *
     * @param inRequest The SnmpMibRequest object holding the list of variable to be retrieved.
     *
     * @exception SnmpStatusException An error occurred during the operation.
     */

    @Override
    public void getBulk(SnmpMibRequest inRequest, int nonRepeat, int maxRepeat)
        throws SnmpStatusException {

        SNMP_ADAPTOR_LOGGER.logp(Level.FINEST,
                com.sun.jmx.snmp.agent.SnmpErrorHandlerAgent.class.getName(),
                "getBulk", "GetBulk in Exception");

        if(inRequest.getVersion() == SnmpDefinitions.snmpVersionOne)
            throw new SnmpStatusException(SnmpDefinitions.snmpRspGenErr, 0);

        Enumeration<SnmpVarBind> l = inRequest.getElements();
        while(l.hasMoreElements()) {
            SnmpVarBind varbind = l.nextElement();
            varbind.setEndOfMibView();
        }
    }

}
