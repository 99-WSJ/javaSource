/*
 * Copyright (c) 2001, 2006, Oracle and/or its affiliates. All rights reserved.
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
package java8.sun.jmx.snmp.internal;

import com.sun.jmx.snmp.*;
import com.sun.jmx.snmp.internal.SnmpDecryptedPdu;
import com.sun.jmx.snmp.internal.SnmpSecurityCache;
import com.sun.jmx.snmp.internal.SnmpSubSystem;

/**
 * Security sub system interface. To allow engine integration, a security sub system must implement this interface.
 * <p><b>This API is a Sun Microsystems internal API  and is subject
 * to change without notice.</b></p>
 */
public interface SnmpSecuritySubSystem extends SnmpSubSystem {
     /**
     * Instantiates an <CODE>SnmpSecurityCache</CODE> that is dependant to the model implementation. This call is routed to the dedicated model according to the model ID.
     * @param id The model ID.
     * @return The model dependant security cache.
     */
    public SnmpSecurityCache createSecurityCache(int id) throws SnmpUnknownSecModelException;
    /**
     * To release the previously created cache. This call is routed to the dedicated model according to the model ID.
     * @param id The model ID.
     * @param cache The security cache to release.
     */
    public void releaseSecurityCache(int id,
                                     SnmpSecurityCache cache) throws SnmpUnknownSecModelException;

     /**
     * Called when a request is to be sent to the network. It must be securized. This call is routed to the dedicated model according to the model ID.
     * <BR>The specified parameters are defined in RFC 2572 (see also the {@link com.sun.jmx.snmp.SnmpV3Message} class).
     * @param cache The cache that has been created by calling <CODE>createSecurityCache</CODE> on this model.
     * @param version The SNMP protocol version.
     * @param msgID The current request id.
     * @param msgMaxSize The message max size.
     * @param msgFlags The message flags (reportable, Auth and Priv).
     * @param msgSecurityModel This current security model.
     * @param params The security parameters that contain the model dependant parameters.
     * @param contextEngineID The context engine ID.
     * @param contextName The context name.
     * @param data The marshalled varbind list
     * @param dataLength The marshalled varbind list length.
     * @param outputBytes The buffer to fill with securized request. This is a representation independant marshalled format. This buffer will be sent to the network.
     * @return The marshalled byte number.
     */
    public int generateRequestMsg(SnmpSecurityCache cache,
                                  int version,
                                  int msgID,
                                  int msgMaxSize,
                                  byte msgFlags,
                                  int msgSecurityModel,
                                  SnmpSecurityParameters params,
                                  byte[] contextEngineID,
                                  byte[] contextName,
                                  byte[] data,
                                  int dataLength,
                                  byte[] outputBytes)
        throws SnmpTooBigException, SnmpStatusException, SnmpSecurityException, SnmpUnknownSecModelException;

    /**
     * Called when a response is to be sent to the network. It must be securized. This call is routed to the dedicated model according to the model ID.
     * <BR>The specified parameters are defined in RFC 2572 (see also the {@link com.sun.jmx.snmp.SnmpV3Message} class).
     * @param cache The cache that has been created by calling <CODE>createSecurityCache</CODE> on this model.
     * @param version The SNMP protocol version.
     * @param msgID The current request id.
     * @param msgMaxSize The message max size.
     * @param msgFlags The message flags (reportable, Auth and Priv).
     * @param msgSecurityModel This current security model.
     * @param params The security parameters that contain the model dependant parameters.
     * @param contextEngineID The context engine ID.
     * @param contextName The context name.
     * @param data The marshalled varbind list
     * @param dataLength The marshalled varbind list length.
     * @param outputBytes The buffer to fill with securized request. This is a representation independant marshalled format. This buffer will be sent to the network.
     * @return The marshalled byte number.
     */
    public int generateResponseMsg(SnmpSecurityCache cache,
                                   int version,
                                   int msgID,
                                   int msgMaxSize,
                                   byte msgFlags,
                                   int msgSecurityModel,
                                   SnmpSecurityParameters params,
                                   byte[] contextEngineID,
                                   byte[] contextName,
                                   byte[] data,
                                   int dataLength,
                                   byte[] outputBytes)
        throws SnmpTooBigException, SnmpStatusException,
               SnmpSecurityException, SnmpUnknownSecModelException;
      /**
     * Called when a request is received from the network. It handles authentication and privacy. This call is routed to the dedicated model according to the model ID.
     * <BR>The specified parameters are defined in RFC 2572 (see also the {@link com.sun.jmx.snmp.SnmpV3Message} class).
     * @param cache The cache that has been created by calling <CODE>createSecurityCache</CODE> on this model.
     * @param version The SNMP protocol version.
     * @param msgID The current request id.
     * @param msgMaxSize The message max size.
     * @param msgFlags The message flags (reportable, Auth and Priv)
     * @param msgSecurityModel This current security model.
     * @param params The security parameters in a marshalled format. The informations cointained in this array are model dependant.
     * @param contextEngineID The context engine ID or null if encrypted.
     * @param contextName The context name or null if encrypted.
     * @param data The marshalled varbind list or null if encrypted.
     * @param encryptedPdu The encrypted pdu or null if not encrypted.
     * @param decryptedPdu The decrypted pdu. If no decryption is to be done, the passed context engine ID, context name and data could be used to fill this object.
     * @return The decoded security parameters.

     */
    public SnmpSecurityParameters
        processIncomingRequest(SnmpSecurityCache cache,
                               int version,
                               int msgID,
                               int msgMaxSize,
                               byte msgFlags,
                               int msgSecurityModel,
                               byte[] params,
                               byte[] contextEngineID,
                               byte[] contextName,
                               byte[] data,
                               byte[] encryptedPdu,
                               SnmpDecryptedPdu decryptedPdu)
        throws SnmpStatusException, SnmpSecurityException, SnmpUnknownSecModelException;
          /**
     * Called when a response is received from the network. It handles authentication and privacy. This call is routed to the dedicated model according to the model ID.
     * <BR>The specified parameters are defined in RFC 2572 (see also the {@link com.sun.jmx.snmp.SnmpV3Message} class).
     * @param cache The cache that has been created by calling <CODE>createSecurityCache</CODE> on this model.
     * @param version The SNMP protocol version.
     * @param msgID The current request id.
     * @param msgMaxSize The message max size.
     * @param msgFlags The message flags (reportable, Auth and Priv).
     * @param msgSecurityModel This current security model.
     * @param params The security parameters in a marshalled format. The informations cointained in this array are model dependant.
     * @param contextEngineID The context engine ID or null if encrypted.
     * @param contextName The context name or null if encrypted.
     * @param data The marshalled varbind list or null if encrypted.
     * @param encryptedPdu The encrypted pdu or null if not encrypted.
     * @param decryptedPdu The decrypted pdu. If no decryption is to be done, the passed context engine ID, context name and data could be used to fill this object.
     * @return The security parameters.

     */
    public SnmpSecurityParameters processIncomingResponse(SnmpSecurityCache cache,
                                                          int version,
                                                          int msgID,
                                                          int msgMaxSize,
                                                          byte msgFlags,
                                                          int msgSecurityModel,
                                                          byte[] params,
                                                          byte[] contextEngineID,
                                                          byte[] contextName,
                                                          byte[] data,
                                                          byte[] encryptedPdu,
                                                          SnmpDecryptedPdu decryptedPdu)
        throws SnmpStatusException, SnmpSecurityException, SnmpUnknownSecModelException;
}
