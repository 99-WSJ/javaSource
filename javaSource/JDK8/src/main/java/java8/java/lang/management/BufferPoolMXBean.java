/*
 * Copyright (c) 2007, 2011, Oracle and/or its affiliates. All rights reserved.
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

import java.lang.management.PlatformManagedObject;

/**
 * The management interface for a buffer pool, for example a pool of
 * {@link java.nio.ByteBuffer#allocateDirect direct} or {@link
 * java.nio.MappedByteBuffer mapped} buffers.
 *
 * <p> A class implementing this interface is an
 * {@link javax.management.MXBean}. A Java
 * virtual machine has one or more implementations of this interface. The {@link
 * java.lang.management.ManagementFactory#getPlatformMXBeans getPlatformMXBeans}
 * method can be used to obtain the list of {@code BufferPoolMXBean} objects
 * representing the management interfaces for pools of buffers as follows:
 * <pre>
 *     List&lt;BufferPoolMXBean&gt; pools = ManagementFactory.getPlatformMXBeans(BufferPoolMXBean.class);
 * </pre>
 *
 * <p> The management interfaces are also registered with the platform {@link
 * javax.management.MBeanServer MBeanServer}. The {@link
 * javax.management.ObjectName ObjectName} that uniquely identifies the
 * management interface within the {@code MBeanServer} takes the form:
 * <pre>
 *     java.nio:type=BufferPool,name=<i>pool name</i>
 * </pre>
 * where <em>pool name</em> is the {@link #getName name} of the buffer pool.
 *
 * @since   1.7
 */
public interface BufferPoolMXBean extends PlatformManagedObject {

    /**
     * Returns the name representing this buffer pool.
     *
     * @return  The name of this buffer pool.
     */
    String getName();

    /**
     * Returns an estimate of the number of buffers in the pool.
     *
     * @return  An estimate of the number of buffers in this pool
     */
    long getCount();

    /**
     * Returns an estimate of the total capacity of the buffers in this pool.
     * A buffer's capacity is the number of elements it contains and the value
     * returned by this method is an estimate of the total capacity of buffers
     * in the pool in bytes.
     *
     * @return  An estimate of the total capacity of the buffers in this pool
     *          in bytes
     */
    long getTotalCapacity();

    /**
     * Returns an estimate of the memory that the Java virtual machine is using
     * for this buffer pool. The value returned by this method may differ
     * from the estimate of the total {@link #getTotalCapacity capacity} of
     * the buffers in this pool. This difference is explained by alignment,
     * memory allocator, and other implementation specific reasons.
     *
     * @return  An estimate of the memory that the Java virtual machine is using
     *          for this buffer pool in bytes, or {@code -1L} if an estimate of
     *          the memory usage is not available
     */
    long getMemoryUsed();
}
