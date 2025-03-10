/*
 * Copyright (c) 1999, 2007, Oracle and/or its affiliates. All rights reserved.
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

package java8.sun.jmx.defaults;

import java.util.logging.Logger;

/**
 * This contains the property list defined for this
 * JMX implementation.
 *
 *
 * @since 1.5
 */
public class JmxProperties {

    // private constructor defined to "hide" the default public constructor
    private JmxProperties() {
    }

    // PUBLIC STATIC CONSTANTS
    //------------------------

    /**
     * References the property that specifies the directory where
     * the native libraries will be stored before the MLet Service
     * loads them into memory.
     * <p>
     * Property Name: <B>jmx.mlet.library.dir</B>
     */
    public static final String JMX_INITIAL_BUILDER =
            "javax.management.builder.initial";

    /**
     * References the property that specifies the directory where
     * the native libraries will be stored before the MLet Service
     * loads them into memory.
     * <p>
     * Property Name: <B>jmx.mlet.library.dir</B>
     */
    public static final String MLET_LIB_DIR = "jmx.mlet.library.dir";

    /**
     * References the property that specifies the full name of the JMX
     * specification implemented by this product.
     * <p>
     * Property Name: <B>jmx.specification.name</B>
     */
    public static final String JMX_SPEC_NAME = "jmx.specification.name";

    /**
     * References the property that specifies the version of the JMX
     * specification implemented by this product.
     * <p>
     * Property Name: <B>jmx.specification.version</B>
     */
    public static final String JMX_SPEC_VERSION = "jmx.specification.version";

    /**
     * References the property that specifies the vendor of the JMX
     * specification implemented by this product.
     * <p>
     * Property Name: <B>jmx.specification.vendor</B>
     */
    public static final String JMX_SPEC_VENDOR = "jmx.specification.vendor";

    /**
     * References the property that specifies the full name of this product
     * implementing the  JMX specification.
     * <p>
     * Property Name: <B>jmx.implementation.name</B>
     */
    public static final String JMX_IMPL_NAME = "jmx.implementation.name";

    /**
     * References the property that specifies the name of the vendor of this
     * product implementing the  JMX specification.
     * <p>
     * Property Name: <B>jmx.implementation.vendor</B>
     */
    public static final String JMX_IMPL_VENDOR = "jmx.implementation.vendor";

    /**
     * References the property that specifies the version of this product
     * implementing the  JMX specification.
     * <p>
     * Property Name: <B>jmx.implementation.version</B>
     */
    public static final String JMX_IMPL_VERSION = "jmx.implementation.version";

    /**
     * Logger name for MBean Server information.
     */
    public static final String MBEANSERVER_LOGGER_NAME =
            "javax.management.mbeanserver";

    /**
     * Logger for MBean Server information.
     */
    public static final Logger MBEANSERVER_LOGGER =
            Logger.getLogger(MBEANSERVER_LOGGER_NAME);

    /**
     * Logger name for MLet service information.
     */
    public static final String MLET_LOGGER_NAME =
            "javax.management.mlet";

    /**
     * Logger for MLet service information.
     */
    public static final Logger MLET_LOGGER =
            Logger.getLogger(MLET_LOGGER_NAME);

    /**
     * Logger name for Monitor information.
     */
    public static final String MONITOR_LOGGER_NAME =
            "javax.management.monitor";

    /**
     * Logger for Monitor information.
     */
    public static final Logger MONITOR_LOGGER =
            Logger.getLogger(MONITOR_LOGGER_NAME);

    /**
     * Logger name for Timer information.
     */
    public static final String TIMER_LOGGER_NAME =
            "javax.management.timer";

    /**
     * Logger for Timer information.
     */
    public static final Logger TIMER_LOGGER =
            Logger.getLogger(TIMER_LOGGER_NAME);

    /**
     * Logger name for Event Management information.
     */
    public static final String NOTIFICATION_LOGGER_NAME =
            "javax.management.notification";

    /**
     * Logger for Event Management information.
     */
    public static final Logger NOTIFICATION_LOGGER =
            Logger.getLogger(NOTIFICATION_LOGGER_NAME);

    /**
     * Logger name for Relation Service.
     */
    public static final String RELATION_LOGGER_NAME =
            "javax.management.relation";

    /**
     * Logger for Relation Service.
     */
    public static final Logger RELATION_LOGGER =
            Logger.getLogger(RELATION_LOGGER_NAME);

    /**
     * Logger name for Model MBean.
     */
    public static final String MODELMBEAN_LOGGER_NAME =
            "javax.management.modelmbean";

    /**
     * Logger for Model MBean.
     */
    public static final Logger MODELMBEAN_LOGGER =
            Logger.getLogger(MODELMBEAN_LOGGER_NAME);

    /**
     * Logger name for all other JMX classes.
     */
    public static final String MISC_LOGGER_NAME =
            "javax.management.misc";

    /**
     * Logger for all other JMX classes.
     */
    public static final Logger MISC_LOGGER =
            Logger.getLogger(MISC_LOGGER_NAME);

    /**
     * Logger name for SNMP.
     */
    public static final String SNMP_LOGGER_NAME =
            "javax.management.snmp";

    /**
     * Logger for SNMP.
     */
    public static final Logger SNMP_LOGGER =
            Logger.getLogger(SNMP_LOGGER_NAME);

    /**
     * Logger name for SNMP Adaptor.
     */
    public static final String SNMP_ADAPTOR_LOGGER_NAME =
            "javax.management.snmp.daemon";

    /**
     * Logger for SNMP Adaptor.
     */
    public static final Logger SNMP_ADAPTOR_LOGGER =
            Logger.getLogger(SNMP_ADAPTOR_LOGGER_NAME);
}
