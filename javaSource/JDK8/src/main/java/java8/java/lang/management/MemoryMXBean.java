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

import javax.management.openmbean.CompositeData;

/**
 * The management interface for the memory system of
 * the Java virtual machine.
 *
 * <p> A Java virtual machine has a single instance of the implementation
 * class of this interface.  This instance implementing this interface is
 * an <a href="ManagementFactory.html#MXBean">MXBean</a>
 * that can be obtained by calling
 * the {@link ManagementFactory#getMemoryMXBean} method or
 * from the {@link ManagementFactory#getPlatformMBeanServer
 * platform <tt>MBeanServer</tt>} method.
 *
 * <p>The <tt>ObjectName</tt> for uniquely identifying the MXBean for
 * the memory system within an MBeanServer is:
 * <blockquote>
 *    {@link ManagementFactory#MEMORY_MXBEAN_NAME
 *           <tt>java.lang:type=Memory</tt>}
 * </blockquote>
 *
 * It can be obtained by calling the
 * {@link PlatformManagedObject#getObjectName} method.
 *
 * <h3> Memory </h3>
 * The memory system of the Java virtual machine manages
 * the following kinds of memory:
 *
 * <h3> 1. Heap </h3>
 * The Java virtual machine has a <i>heap</i> that is the runtime
 * data area from which memory for all class instances and arrays
 * are allocated.  It is created at the Java virtual machine start-up.
 * Heap memory for objects is reclaimed by an automatic memory management
 * system which is known as a <i>garbage collector</i>.
 *
 * <p>The heap may be of a fixed size or may be expanded and shrunk.
 * The memory for the heap does not need to be contiguous.
 *
 * <h3> 2. Non-Heap Memory</h3>
 * The Java virtual machine manages memory other than the heap
 * (referred as <i>non-heap memory</i>).
 *
 * <p> The Java virtual machine has a <i>method area</i> that is shared
 * among all threads.
 * The method area belongs to non-heap memory.  It stores per-class structures
 * such as a runtime constant pool, field and method data, and the code for
 * methods and constructors.  It is created at the Java virtual machine
 * start-up.
 *
 * <p> The method area is logically part of the heap but a Java virtual
 * machine implementation may choose not to either garbage collect
 * or compact it.  Similar to the heap, the method area may be of a
 * fixed size or may be expanded and shrunk.  The memory for the
 * method area does not need to be contiguous.
 *
 * <p>In addition to the method area, a Java virtual machine
 * implementation may require memory for internal processing or
 * optimization which also belongs to non-heap memory.
 * For example, the JIT compiler requires memory for storing the native
 * machine code translated from the Java virtual machine code for
 * high performance.
 *
 * <h3>Memory Pools and Memory Managers</h3>
 * {@link MemoryPoolMXBean Memory pools} and
 * {@link MemoryManagerMXBean memory managers} are the abstract entities
 * that monitor and manage the memory system
 * of the Java virtual machine.
 *
 * <p>A memory pool represents a memory area that the Java virtual machine
 * manages.  The Java virtual machine has at least one memory pool
 * and it may create or remove memory pools during execution.
 * A memory pool can belong to either the heap or the non-heap memory.
 *
 * <p>A memory manager is responsible for managing one or more memory pools.
 * The garbage collector is one type of memory manager responsible
 * for reclaiming memory occupied by unreachable objects.  A Java virtual
 * machine may have one or more memory managers.   It may
 * add or remove memory managers during execution.
 * A memory pool can be managed by more than one memory manager.
 *
 * <h3>Memory Usage Monitoring</h3>
 *
 * Memory usage is a very important monitoring attribute for the memory system.
 * The memory usage, for example, could indicate:
 * <ul>
 *   <li>the memory usage of an application,</li>
 *   <li>the workload being imposed on the automatic memory management system,</li>
 *   <li>potential memory leakage.</li>
 * </ul>
 *
 * <p>
 * The memory usage can be monitored in three ways:
 * <ul>
 *   <li>Polling</li>
 *   <li>Usage Threshold Notification</li>
 *   <li>Collection Usage Threshold Notification</li>
 * </ul>
 *
 * Details are specified in the {@link MemoryPoolMXBean} interface.
 *
 * <p>The memory usage monitoring mechanism is intended for load-balancing
 * or workload distribution use.  For example, an application would stop
 * receiving any new workload when its memory usage exceeds a
 * certain threshold. It is not intended for an application to detect
 * and recover from a low memory condition.
 *
 * <h3>Notifications</h3>
 *
 * <p>This <tt>MemoryMXBean</tt> is a
 * {@link javax.management.NotificationEmitter NotificationEmitter}
 * that emits two types of memory {@link javax.management.Notification
 * notifications} if any one of the memory pools
 * supports a <a href="MemoryPoolMXBean.html#UsageThreshold">usage threshold</a>
 * or a <a href="MemoryPoolMXBean.html#CollectionThreshold">collection usage
 * threshold</a> which can be determined by calling the
 * {@link MemoryPoolMXBean#isUsageThresholdSupported} and
 * {@link MemoryPoolMXBean#isCollectionUsageThresholdSupported} methods.
 * <ul>
 *   <li>{@link MemoryNotificationInfo#MEMORY_THRESHOLD_EXCEEDED
 *       usage threshold exceeded notification} - for notifying that
 *       the memory usage of a memory pool is increased and has reached
 *       or exceeded its
 *       <a href="MemoryPoolMXBean.html#UsageThreshold"> usage threshold</a> value.
 *       </li>
 *   <li>{@link MemoryNotificationInfo#MEMORY_COLLECTION_THRESHOLD_EXCEEDED
 *       collection usage threshold exceeded notification} - for notifying that
 *       the memory usage of a memory pool is greater than or equal to its
 *       <a href="MemoryPoolMXBean.html#CollectionThreshold">
 *       collection usage threshold</a> after the Java virtual machine
 *       has expended effort in recycling unused objects in that
 *       memory pool.</li>
 * </ul>
 *
 * <p>
 * The notification emitted is a {@link javax.management.Notification}
 * instance whose {@link javax.management.Notification#setUserData
 * user data} is set to a {@link CompositeData CompositeData}
 * that represents a {@link MemoryNotificationInfo} object
 * containing information about the memory pool when the notification
 * was constructed. The <tt>CompositeData</tt> contains the attributes
 * as described in {@link MemoryNotificationInfo#from
 * MemoryNotificationInfo}.
 *
 * <hr>
 * <h3>NotificationEmitter</h3>
 * The <tt>MemoryMXBean</tt> object returned by
 * {@link ManagementFactory#getMemoryMXBean} implements
 * the {@link javax.management.NotificationEmitter NotificationEmitter}
 * interface that allows a listener to be registered within the
 * <tt>MemoryMXBean</tt> as a notification listener.
 *
 * Below is an example code that registers a <tt>MyListener</tt> to handle
 * notification emitted by the <tt>MemoryMXBean</tt>.
 *
 * <blockquote><pre>
 * class MyListener implements javax.management.NotificationListener {
 *     public void handleNotification(Notification notif, Object handback) {
 *         // handle notification
 *         ....
 *     }
 * }
 *
 * MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();
 * NotificationEmitter emitter = (NotificationEmitter) mbean;
 * MyListener listener = new MyListener();
 * emitter.addNotificationListener(listener, null, null);
 * </pre></blockquote>
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
public interface MemoryMXBean extends PlatformManagedObject {
    /**
     * Returns the approximate number of objects for which
     * finalization is pending.
     *
     * @return the approximate number objects for which finalization
     * is pending.
     */
    public int getObjectPendingFinalizationCount();

    /**
     * Returns the current memory usage of the heap that
     * is used for object allocation.  The heap consists
     * of one or more memory pools.  The <tt>used</tt>
     * and <tt>committed</tt> size of the returned memory
     * usage is the sum of those values of all heap memory pools
     * whereas the <tt>init</tt> and <tt>max</tt> size of the
     * returned memory usage represents the setting of the heap
     * memory which may not be the sum of those of all heap
     * memory pools.
     * <p>
     * The amount of used memory in the returned memory usage
     * is the amount of memory occupied by both live objects
     * and garbage objects that have not been collected, if any.
     *
     * <p>
     * <b>MBeanServer access</b>:<br>
     * The mapped type of <tt>MemoryUsage</tt> is
     * <tt>CompositeData</tt> with attributes as specified in
     * {@link MemoryUsage#from MemoryUsage}.
     *
     * @return a {@link MemoryUsage} object representing
     * the heap memory usage.
     */
    public MemoryUsage getHeapMemoryUsage();

    /**
     * Returns the current memory usage of non-heap memory that
     * is used by the Java virtual machine.
     * The non-heap memory consists of one or more memory pools.
     * The <tt>used</tt> and <tt>committed</tt> size of the
     * returned memory usage is the sum of those values of
     * all non-heap memory pools whereas the <tt>init</tt>
     * and <tt>max</tt> size of the returned memory usage
     * represents the setting of the non-heap
     * memory which may not be the sum of those of all non-heap
     * memory pools.
     *
     * <p>
     * <b>MBeanServer access</b>:<br>
     * The mapped type of <tt>MemoryUsage</tt> is
     * <tt>CompositeData</tt> with attributes as specified in
     * {@link MemoryUsage#from MemoryUsage}.
     *
     * @return a {@link MemoryUsage} object representing
     * the non-heap memory usage.
     */
    public MemoryUsage getNonHeapMemoryUsage();

    /**
     * Tests if verbose output for the memory system is enabled.
     *
     * @return <tt>true</tt> if verbose output for the memory
     * system is enabled; <tt>false</tt> otherwise.
     */
    public boolean isVerbose();

    /**
     * Enables or disables verbose output for the memory
     * system.  The verbose output information and the output stream
     * to which the verbose information is emitted are implementation
     * dependent.  Typically, a Java virtual machine implementation
     * prints a message whenever it frees memory at garbage collection.
     *
     * <p>
     * Each invocation of this method enables or disables verbose
     * output globally.
     *
     * @param value <tt>true</tt> to enable verbose output;
     *              <tt>false</tt> to disable.
     *
     * @exception  SecurityException if a security manager
     *             exists and the caller does not have
     *             ManagementPermission("control").
     */
    public void setVerbose(boolean value);

    /**
     * Runs the garbage collector.
     * The call <code>gc()</code> is effectively equivalent to the
     * call:
     * <blockquote><pre>
     * System.gc()
     * </pre></blockquote>
     *
     * @see     System#gc()
     */
    public void gc();

}
