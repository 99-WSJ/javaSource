/*
 * Copyright (c) 2002, 2003, Oracle and/or its affiliates. All rights reserved.
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

import com.sun.jmx.snmp.SnmpEngineId;
import com.sun.jmx.snmp.SnmpUsmKeyHandler;

/**
 * This engine is conformant with the RFC 2571. It is the main object within an SNMP entity (agent, manager...).
 * To an engine is associated an {@link SnmpEngineId}.
 * Engine instantiation is based on a factory {@link com.sun.jmx.snmp.SnmpEngineFactory  SnmpEngineFactory}.
 * When an <CODE> SnmpEngine </CODE> is created, a User based Security Model (USM) is initialized. The security configuration is located in a text file.
 * The text file is read when the engine is created.
 * <p>Note that the engine is not used when the agent is SNMPv1/SNMPv2 only.
<P> The USM configuration text file is remotely updatable using the USM Mib.</P>
<P> User that are configured in the Usm text file are nonVolatile. </P>
<P> Usm Mib userEntry supported storage type values are : volatile or nonVolatile only. Other values are rejected and a wrongValue is returned) </P>
<ul>
<li> volatile means that user entry is not flushed in security file </li>
<li> nonVolatile means that user entry is flushed in security file </li>
<li> If a nonVolatile row is set to be volatile, it will be not flushed in the file </li>
<li>If a volatile row created from the UsmMib is set to nonVolatile, it will be flushed in the file (if the file exist and is writable otherwise an inconsistentValue is returned)</li>
</ul>
 * <p><b>This API is a Sun Microsystems internal API  and is subject
 * to change without notice.</b></p>
 * @since 1.5
 */
public interface SnmpEngine {
    /**
     * Gets the engine time in seconds. This is the time from the last reboot.
     * @return The time from the last reboot.
     */
    public int getEngineTime();
    /**
     * Gets the engine Id. This is unique for each engine.
     * @return The engine Id object.
     */
    public SnmpEngineId getEngineId();

    /**
     * Gets the engine boot number. This is the number of time this engine has rebooted. Each time an <CODE>SnmpEngine</CODE> is instantiated, it will read this value in its Lcd, and store back the value incremented by one.
     * @return The engine's number of reboot.
     */
    public int getEngineBoots();

    /**
     * Gets the Usm key handler.
     * @return The key handler.
     */
    public SnmpUsmKeyHandler getUsmKeyHandler();
}
