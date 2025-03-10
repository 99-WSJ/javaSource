/*
 * Copyright (c) 2003, 2008, Oracle and/or its affiliates. All rights reserved.
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

package java8.java.lang.management;

import java.lang.management.ManagementFactory;
import java.lang.management.PlatformManagedObject;

/**
 * The management interface for the class loading system of
 * the Java virtual machine.
 *
 * <p> A Java virtual machine has a single instance of the implementation
 * class of this interface.  This instance implementing this interface is
 * an <a href="ManagementFactory.html#MXBean">MXBean</a>
 * that can be obtained by calling
 * the {@link ManagementFactory#getClassLoadingMXBean} method or
 * from the {@link ManagementFactory#getPlatformMBeanServer
 * platform <tt>MBeanServer</tt>}.
 *
 * <p>The <tt>ObjectName</tt> for uniquely identifying the MXBean for
 * the class loading system within an <tt>MBeanServer</tt> is:
 * <blockquote>
 * {@link ManagementFactory#CLASS_LOADING_MXBEAN_NAME
 *        <tt>java.lang:type=ClassLoading</tt>}
 * </blockquote>
 *
 * It can be obtained by calling the
 * {@link PlatformManagedObject#getObjectName} method.
 *
 * @see ManagementFactory#getPlatformMXBeans(Class)
 * @see <a href="../../../javax/management/package-summary.html">
 *      JMX Specification.</a>
 * @see <a href="package-summary.html#examples">
 *      Ways to Access MXBeans</a>
 *
 * @author  Mandy Chung
 * @since   1.5
 */
public interface ClassLoadingMXBean extends PlatformManagedObject {

    /**
     * Returns the total number of classes that have been loaded since
     * the Java virtual machine has started execution.
     *
     * @return the total number of classes loaded.
     *
     */
    public long getTotalLoadedClassCount();

    /**
     * Returns the number of classes that are currently loaded in the
     * Java virtual machine.
     *
     * @return the number of currently loaded classes.
     */
    public int getLoadedClassCount();

    /**
     * Returns the total number of classes unloaded since the Java virtual machine
     * has started execution.
     *
     * @return the total number of unloaded classes.
     */
    public long getUnloadedClassCount();

    /**
     * Tests if the verbose output for the class loading system is enabled.
     *
     * @return <tt>true</tt> if the verbose output for the class loading
     * system is enabled; <tt>false</tt> otherwise.
     */
    public boolean isVerbose();

    /**
     * Enables or disables the verbose output for the class loading
     * system.  The verbose output information and the output stream
     * to which the verbose information is emitted are implementation
     * dependent.  Typically, a Java virtual machine implementation
     * prints a message each time a class file is loaded.
     *
     * <p>This method can be called by multiple threads concurrently.
     * Each invocation of this method enables or disables the verbose
     * output globally.
     *
     * @param value <tt>true</tt> to enable the verbose output;
     *              <tt>false</tt> to disable.
     *
     * @exception  SecurityException if a security manager
     *             exists and the caller does not have
     *             ManagementPermission("control").
     */
    public void setVerbose(boolean value);

}
