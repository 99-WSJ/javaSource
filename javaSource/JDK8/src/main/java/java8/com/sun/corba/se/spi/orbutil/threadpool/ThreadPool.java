/*
 * Copyright (c) 2003, 2012, Oracle and/or its affiliates. All rights reserved.
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

package java8.sun.corba.se.spi.orbutil.threadpool;

import com.sun.corba.se.spi.orbutil.threadpool.NoSuchWorkQueueException;
import com.sun.corba.se.spi.orbutil.threadpool.WorkQueue;

import java.io.Closeable;

/** This interface defines a thread pool execution service.  The ORB uses this
 * interface, which preceeds the JDK 5 ExecutorService.  Note that the close
 * method must be called in order to reclaim thread resources.
 */
public interface ThreadPool extends Closeable
{

    /**
    * This method will return any instance of the WorkQueue. If the ThreadPool
    * instance only services one WorkQueue then that WorkQueue instance will
    * be returned. If there are more than one WorkQueues serviced by this
    * ThreadPool, then this method would return a WorkQueue based on the
    * implementation of the class that implements this interface. For PE 8.0 we
    * would return a WorkQueue in a roundrobin fashion everytime this method
    * is called. In the future we could allow pluggability of  Policy objects for this.
    */
    public WorkQueue getAnyWorkQueue();

    /**
    * This method will return an instance of the of the WorkQueue given a queueId.
    * This will be useful in situations where there are more than one WorkQueues
    * managed by the ThreadPool and the user of the ThreadPool wants to always use
    * the same WorkQueue for doing the Work.
    * If the number of WorkQueues in the ThreadPool are 10, then queueIds will go
    * from 0-9
    *
    * @throws NoSuchWorkQueueException thrown when queueId passed is invalid
    */
    public WorkQueue getWorkQueue(int queueId) throws NoSuchWorkQueueException;

    /**
    * This method will return the number of WorkQueues serviced by the threadpool.
    */
    public int numberOfWorkQueues();

    /**
    * This method will return the minimum number of threads maintained by the threadpool.
    */
    public int minimumNumberOfThreads();

    /**
    * This method will return the maximum number of threads in the threadpool at any
    * point in time, for the life of the threadpool
    */
    public int maximumNumberOfThreads();

    /**
    * This method will return the time in milliseconds when idle threads in the threadpool are
    * removed.
    */
    public long idleTimeoutForThreads();

    /**
    * This method will return the current number of threads in the threadpool. This method
    * returns a value which is not synchronized.
    */
    public int currentNumberOfThreads();

    /**
    * This method will return the number of available threads in the threadpool which are
     * waiting for work. This method returns a value which is not synchronized.
    */
    public int numberOfAvailableThreads();

    /**
    * This method will return the number of busy threads in the threadpool
    * This method returns a value which is not synchronized.
    */
    public int numberOfBusyThreads();

    /**
    * This method returns the number of Work items processed by the threadpool
    */
    public long currentProcessedCount();

     /**
     * This method returns the average elapsed time taken to complete a Work
     * item.
     */
    public long averageWorkCompletionTime();

    /**
    * This method will return the name of the threadpool.
    */
    public String getName();

}

// End of file.
