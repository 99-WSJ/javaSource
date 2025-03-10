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


package java8.sun.jmx.snmp;


import com.sun.jmx.snmp.SnmpIpAddress;
import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpPduPacket;

/**
 * Represents an SNMPv1-trap PDU.
 * <P>
 * You will not usually need to use this class, except if you
 * decide to implement your own
 * {@link com.sun.jmx.snmp.SnmpPduFactory SnmpPduFactory} object.
 * <P>
 * The <CODE>SnmpPduTrap</CODE> extends {@link SnmpPduPacket SnmpPduPacket}
 * and defines attributes specific to an SNMPv1 trap (see RFC1157).
 *
 * <p><b>This API is a Sun Microsystems internal API  and is subject
 * to change without notice.</b></p>
 */

public class SnmpPduTrap extends SnmpPduPacket {
    private static final long serialVersionUID = -3670886636491433011L;

    /**
     * Enterprise object identifier.
     * @serial
     */
    public SnmpOid        enterprise ;

    /**
     * Agent address. If the agent address source was not an IPv4 one (eg : IPv6), this field is null.
     * @serial
     */
    public SnmpIpAddress  agentAddr ;

    /**
     * Generic trap number.
     * <BR>
     * The possible values are defined in
     * {@link com.sun.jmx.snmp.SnmpDefinitions#trapColdStart SnmpDefinitions}.
     * @serial
     */
    public int            genericTrap ;

    /**
     * Specific trap number.
     * @serial
     */
    public int            specificTrap ;

    /**
     * Time-stamp.
     * @serial
     */
    public long            timeStamp ;



    /**
     * Builds a new trap PDU.
     * <BR><CODE>type</CODE> and <CODE>version</CODE> fields are initialized with
     * {@link com.sun.jmx.snmp.SnmpDefinitions#pduV1TrapPdu pduV1TrapPdu}
     * and {@link com.sun.jmx.snmp.SnmpDefinitions#snmpVersionOne snmpVersionOne}.
     */
    public SnmpPduTrap() {
        type = pduV1TrapPdu ;
        version = snmpVersionOne ;
    }
}
