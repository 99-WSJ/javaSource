/*
 * Copyright (c) 2001, 2003, Oracle and/or its affiliates. All rights reserved.
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

import com.sun.jmx.snmp.SnmpDefinitions;
import com.sun.jmx.snmp.SnmpPdu;
import com.sun.jmx.snmp.SnmpSecurityParameters;
import com.sun.jmx.snmp.SnmpV3Message;

import java.io.Serializable;

/**
 * Is the fully decoded representation of an SNMP V3 packet.
 * <P>
 *
 * Classes are derived from <CODE>SnmpPdu</CODE> to
 * represent the different forms of SNMP pdu
 * ({@link com.sun.jmx.snmp.SnmpScopedPduRequest SnmpScopedPduRequest},
 * {@link com.sun.jmx.snmp.SnmpScopedPduBulk SnmpScopedPduBulk}).
 * <BR>The <CODE>SnmpScopedPduPacket</CODE> class defines the attributes
 * common to every scoped SNMP packets.
 *
 * <p><b>This API is a Sun Microsystems internal API  and is subject
 * to change without notice.</b></p>
 * @see SnmpV3Message
 *
 * @since 1.5
 */
public abstract class SnmpScopedPduPacket extends SnmpPdu
    implements Serializable {
    /**
     * Message max size the pdu sender can deal with.
     */
    public int msgMaxSize = 0;

    /**
     * Message identifier.
     */
    public int msgId = 0;

    /**
     * Message flags. Reportable flag  and security level.</P>
     *<PRE>
     * --  .... ...1   authFlag
     * --  .... ..1.   privFlag
     * --  .... .1..   reportableFlag
     * --              Please observe:
     * --  .... ..00   is OK, means noAuthNoPriv
     * --  .... ..01   is OK, means authNoPriv
     * --  .... ..10   reserved, must NOT be used.
     * --  .... ..11   is OK, means authPriv
     *</PRE>
     */
    public byte msgFlags = 0;

    /**
     * The security model the security sub system MUST use in order to deal with this pdu (eg: User based Security Model Id = 3).
     */
    public int msgSecurityModel = 0;

    /**
     * The context engine Id in which the pdu must be handled (Generaly the local engine Id).
     */
    public byte[] contextEngineId = null;

    /**
     * The context name in which the OID have to be interpreted.
     */
    public byte[] contextName = null;

    /**
     * The security parameters. This is an opaque member that is
     * interpreted by the concerned security model.
     */
    public SnmpSecurityParameters securityParameters = null;

    /**
     * Constructor. Is only called by a son. Set the version to <CODE>SnmpDefinitions.snmpVersionThree</CODE>.
     */
    protected SnmpScopedPduPacket() {
        version = SnmpDefinitions.snmpVersionThree;
    }
}
