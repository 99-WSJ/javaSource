/*
 * Copyright (c) 1998, 2006, Oracle and/or its affiliates. All rights reserved.
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


// java imports
//

import com.sun.jmx.snmp.SnmpDefinitions;
import com.sun.jmx.snmp.SnmpMessage;
import com.sun.jmx.snmp.SnmpMsg;
import com.sun.jmx.snmp.SnmpPdu;
import com.sun.jmx.snmp.SnmpPduFactory;
import com.sun.jmx.snmp.SnmpPduPacket;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpTooBigException;
import com.sun.jmx.snmp.SnmpV3Message;

import java.io.Serializable;

/**
 * Default implementation of the {@link SnmpPduFactory SnmpPduFactory} interface.
 * <BR>It uses the BER (basic encoding rules) standardized encoding scheme associated with ASN.1.
 * <P>
 * This implementation of the <CODE>SnmpPduFactory</CODE> is very
 * basic: it simply calls encoding and decoding methods from
 * {@link SnmpMsg}.
 * <BLOCKQUOTE>
 * <PRE>
 * public SnmpPdu decodeSnmpPdu(SnmpMsg msg)
 * throws SnmpStatusException {
 *   return msg.decodeSnmpPdu() ;
 * }
 *
 * public SnmpMsg encodeSnmpPdu(SnmpPdu pdu, int maxPktSize)
 * throws SnmpStatusException, SnmpTooBigException {
 *   SnmpMsg result = new SnmpMessage() ;       // for SNMP v1/v2
 * <I>or</I>
 *   SnmpMsg result = new SnmpV3Message() ;     // for SNMP v3
 *   result.encodeSnmpPdu(pdu, maxPktSize) ;
 *   return result ;
 * }
 * </PRE>
 * </BLOCKQUOTE>
 * To implement your own object, you can implement <CODE>SnmpPduFactory</CODE>
 * or extend <CODE>SnmpPduFactoryBER</CODE>.
 * <p><b>This API is a Sun Microsystems internal API  and is subject
 * to change without notice.</b></p>
 */

public class SnmpPduFactoryBER implements SnmpPduFactory, Serializable {
   private static final long serialVersionUID = -3525318344000547635L;

   /**
     * Calls {@link SnmpMsg#decodeSnmpPdu SnmpMsg.decodeSnmpPdu}
     * on the specified message and returns the resulting <CODE>SnmpPdu</CODE>.
     *
     * @param msg The SNMP message to be decoded.
     * @return The resulting SNMP PDU packet.
     * @exception SnmpStatusException If the encoding is invalid.
     *
     * @since 1.5
     */
    public SnmpPdu decodeSnmpPdu(SnmpMsg msg) throws SnmpStatusException {
        return msg.decodeSnmpPdu();
    }

    /**
     * Encodes the specified <CODE>SnmpPdu</CODE> and
     * returns the resulting <CODE>SnmpMsg</CODE>. If this
     * method returns null, the specified <CODE>SnmpPdu</CODE>
     * will be dropped and the current SNMP request will be
     * aborted.
     *
     * @param p The <CODE>SnmpPdu</CODE> to be encoded.
     * @param maxDataLength The size limit of the resulting encoding.
     * @return Null or a fully encoded <CODE>SnmpMsg</CODE>.
     * @exception SnmpStatusException If <CODE>pdu</CODE> contains
     *            illegal values and cannot be encoded.
     * @exception SnmpTooBigException If the resulting encoding does not
     *            fit into <CODE>maxPktSize</CODE> bytes.
     *
     * @since 1.5
     */
    public SnmpMsg encodeSnmpPdu(SnmpPdu p, int maxDataLength)
        throws SnmpStatusException, SnmpTooBigException {
        switch(p.version) {
        case SnmpDefinitions.snmpVersionOne:
        case SnmpDefinitions.snmpVersionTwo: {
            SnmpMessage result = new SnmpMessage();
            result.encodeSnmpPdu((SnmpPduPacket) p, maxDataLength);
            return result;
        }
        case SnmpDefinitions.snmpVersionThree: {
            SnmpV3Message result = new SnmpV3Message();
            result.encodeSnmpPdu(p, maxDataLength);
            return result;
        }
        default:
            return null;
        }
    }
}
