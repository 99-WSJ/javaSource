/*
 * Copyright (c) 2003, 2013, Oracle and/or its affiliates. All rights reserved.
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
 * The management interface for the runtime system of
 * the Java virtual machine.
 *
 * <p> A Java virtual machine has a single instance of the implementation
 * class of this interface.  This instance implementing this interface is
 * an <a href="ManagementFactory.html#MXBean">MXBean</a>
 * that can be obtained by calling
 * the {@link ManagementFactory#getRuntimeMXBean} method or
 * from the {@link ManagementFactory#getPlatformMBeanServer
 * platform <tt>MBeanServer</tt>} method.
 *
 * <p>The <tt>ObjectName</tt> for uniquely identifying the MXBean for
 * the runtime system within an MBeanServer is:
 * <blockquote>
 *    {@link ManagementFactory#RUNTIME_MXBEAN_NAME
 *           <tt>java.lang:type=Runtime</tt>}
 * </blockquote>
 *
 * It can be obtained by calling the
 * {@link PlatformManagedObject#getObjectName} method.
 *
 * <p> This interface defines several convenient methods for accessing
 * system properties about the Java virtual machine.
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
public interface RuntimeMXBean extends PlatformManagedObject {
    /**
     * Returns the name representing the running Java virtual machine.
     * The returned name string can be any arbitrary string and
     * a Java virtual machine implementation can choose
     * to embed platform-specific useful information in the
     * returned name string.  Each running virtual machine could have
     * a different name.
     *
     * @return the name representing the running Java virtual machine.
     */
    public String getName();

    /**
     * Returns the Java virtual machine implementation name.
     * This method is equivalent to {@link System#getProperty
     * System.getProperty("java.vm.name")}.
     *
     * @return the Java virtual machine implementation name.
     *
     * @throws  SecurityException
     *     if a security manager exists and its
     *     <code>checkPropertiesAccess</code> method doesn't allow access
     *     to this system property.
     * @see SecurityManager#checkPropertyAccess(String)
     * @see System#getProperty
     */
    public String getVmName();

    /**
     * Returns the Java virtual machine implementation vendor.
     * This method is equivalent to {@link System#getProperty
     * System.getProperty("java.vm.vendor")}.
     *
     * @return the Java virtual machine implementation vendor.
     *
     * @throws  SecurityException
     *     if a security manager exists and its
     *     <code>checkPropertiesAccess</code> method doesn't allow access
     *     to this system property.
     * @see SecurityManager#checkPropertyAccess(String)
     * @see System#getProperty
     */
    public String getVmVendor();

    /**
     * Returns the Java virtual machine implementation version.
     * This method is equivalent to {@link System#getProperty
     * System.getProperty("java.vm.version")}.
     *
     * @return the Java virtual machine implementation version.
     *
     * @throws  SecurityException
     *     if a security manager exists and its
     *     <code>checkPropertiesAccess</code> method doesn't allow access
     *     to this system property.
     * @see SecurityManager#checkPropertyAccess(String)
     * @see System#getProperty
     */
    public String getVmVersion();

    /**
     * Returns the Java virtual machine specification name.
     * This method is equivalent to {@link System#getProperty
     * System.getProperty("java.vm.specification.name")}.
     *
     * @return the Java virtual machine specification name.
     *
     * @throws  SecurityException
     *     if a security manager exists and its
     *     <code>checkPropertiesAccess</code> method doesn't allow access
     *     to this system property.
     * @see SecurityManager#checkPropertyAccess(String)
     * @see System#getProperty
     */
    public String getSpecName();

    /**
     * Returns the Java virtual machine specification vendor.
     * This method is equivalent to {@link System#getProperty
     * System.getProperty("java.vm.specification.vendor")}.
     *
     * @return the Java virtual machine specification vendor.
     *
     * @throws  SecurityException
     *     if a security manager exists and its
     *     <code>checkPropertiesAccess</code> method doesn't allow access
     *     to this system property.
     * @see SecurityManager#checkPropertyAccess(String)
     * @see System#getProperty
     */
    public String getSpecVendor();

    /**
     * Returns the Java virtual machine specification version.
     * This method is equivalent to {@link System#getProperty
     * System.getProperty("java.vm.specification.version")}.
     *
     * @return the Java virtual machine specification version.
     *
     * @throws  SecurityException
     *     if a security manager exists and its
     *     <code>checkPropertiesAccess</code> method doesn't allow access
     *     to this system property.
     * @see SecurityManager#checkPropertyAccess(String)
     * @see System#getProperty
     */
    public String getSpecVersion();


    /**
     * Returns the version of the specification for the management interface
     * implemented by the running Java virtual machine.
     *
     * @return the version of the specification for the management interface
     * implemented by the running Java virtual machine.
     */
    public String getManagementSpecVersion();

    /**
     * Returns the Java class path that is used by the system class loader
     * to search for class files.
     * This method is equivalent to {@link System#getProperty
     * System.getProperty("java.class.path")}.
     *
     * <p> Multiple paths in the Java class path are separated by the
     * path separator character of the platform of the Java virtual machine
     * being monitored.
     *
     * @return the Java class path.
     *
     * @throws  SecurityException
     *     if a security manager exists and its
     *     <code>checkPropertiesAccess</code> method doesn't allow access
     *     to this system property.
     * @see SecurityManager#checkPropertyAccess(String)
     * @see System#getProperty
     */
    public String getClassPath();

    /**
     * Returns the Java library path.
     * This method is equivalent to {@link System#getProperty
     * System.getProperty("java.library.path")}.
     *
     * <p> Multiple paths in the Java library path are separated by the
     * path separator character of the platform of the Java virtual machine
     * being monitored.
     *
     * @return the Java library path.
     *
     * @throws  SecurityException
     *     if a security manager exists and its
     *     <code>checkPropertiesAccess</code> method doesn't allow access
     *     to this system property.
     * @see SecurityManager#checkPropertyAccess(String)
     * @see System#getProperty
     */
    public String getLibraryPath();

    /**
     * Tests if the Java virtual machine supports the boot class path
     * mechanism used by the bootstrap class loader to search for class
     * files.
     *
     * @return <tt>true</tt> if the Java virtual machine supports the
     * class path mechanism; <tt>false</tt> otherwise.
     */
    public boolean isBootClassPathSupported();

    /**
     * Returns the boot class path that is used by the bootstrap class loader
     * to search for class files.
     *
     * <p> Multiple paths in the boot class path are separated by the
     * path separator character of the platform on which the Java
     * virtual machine is running.
     *
     * <p>A Java virtual machine implementation may not support
     * the boot class path mechanism for the bootstrap class loader
     * to search for class files.
     * The {@link #isBootClassPathSupported} method can be used
     * to determine if the Java virtual machine supports this method.
     *
     * @return the boot class path.
     *
     * @throws UnsupportedOperationException
     *     if the Java virtual machine does not support this operation.
     *
     * @throws  SecurityException
     *     if a security manager exists and the caller does not have
     *     ManagementPermission("monitor").
     */
    public String getBootClassPath();

    /**
     * Returns the input arguments passed to the Java virtual machine
     * which does not include the arguments to the <tt>main</tt> method.
     * This method returns an empty list if there is no input argument
     * to the Java virtual machine.
     * <p>
     * Some Java virtual machine implementations may take input arguments
     * from multiple different sources: for examples, arguments passed from
     * the application that launches the Java virtual machine such as
     * the 'java' command, environment variables, configuration files, etc.
     * <p>
     * Typically, not all command-line options to the 'java' command
     * are passed to the Java virtual machine.
     * Thus, the returned input arguments may not
     * include all command-line options.
     *
     * <p>
     * <b>MBeanServer access</b>:<br>
     * The mapped type of {@code List<String>} is <tt>String[]</tt>.
     *
     * @return a list of <tt>String</tt> objects; each element
     * is an argument passed to the Java virtual machine.
     *
     * @throws  SecurityException
     *     if a security manager exists and the caller does not have
     *     ManagementPermission("monitor").
     */
    public java.util.List<String> getInputArguments();

    /**
     * Returns the uptime of the Java virtual machine in milliseconds.
     *
     * @return uptime of the Java virtual machine in milliseconds.
     */
    public long getUptime();

    /**
     * Returns the start time of the Java virtual machine in milliseconds.
     * This method returns the approximate time when the Java virtual
     * machine started.
     *
     * @return start time of the Java virtual machine in milliseconds.
     *
     */
    public long getStartTime();

    /**
     * Returns a map of names and values of all system properties.
     * This method calls {@link System#getProperties} to get all
     * system properties.  Properties whose name or value is not
     * a <tt>String</tt> are omitted.
     *
     * <p>
     * <b>MBeanServer access</b>:<br>
     * The mapped type of {@code Map<String,String>} is
     * {@link javax.management.openmbean.TabularData TabularData}
     * with two items in each row as follows:
     * <blockquote>
     * <table border summary="Name and Type for each item">
     * <tr>
     *   <th>Item Name</th>
     *   <th>Item Type</th>
     *   </tr>
     * <tr>
     *   <td><tt>key</tt></td>
     *   <td><tt>String</tt></td>
     *   </tr>
     * <tr>
     *   <td><tt>value</tt></td>
     *   <td><tt>String</tt></td>
     *   </tr>
     * </table>
     * </blockquote>
     *
     * @return a map of names and values of all system properties.
     *
     * @throws  SecurityException
     *     if a security manager exists and its
     *     <code>checkPropertiesAccess</code> method doesn't allow access
     *     to the system properties.
     */
    public java.util.Map<String, String> getSystemProperties();
}
