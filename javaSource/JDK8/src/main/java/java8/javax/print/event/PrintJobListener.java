/*
 * Copyright (c) 2000, 2001, Oracle and/or its affiliates. All rights reserved.
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

package java8.javax.print.event;

import javax.print.event.PrintJobEvent;

/**
  * Implementations of this listener interface should be attached to a
  * {@link javax.print.DocPrintJob DocPrintJob} to monitor the status of
  * the printer job.
  * These callback methods may be invoked on the thread processing the
  * print job, or a service created notification thread. In either case
  * the client should not perform lengthy processing in these callbacks.
  */

public interface PrintJobListener {

    /**
     * Called to notify the client that data has been successfully
     * transferred to the print service, and the client may free
     * local resources allocated for that data.  The client should
     * not assume that the data has been completely printed after
     * receiving this event.
     * If this event is not received the client should wait for a terminal
     * event (completed/canceled/failed) before freeing the resources.
     * @param pje the job generating this event
     */
    public void printDataTransferCompleted(PrintJobEvent pje) ;


    /**
     * Called to notify the client that the job completed successfully.
     * @param pje the job generating this event
     */
    public void printJobCompleted(PrintJobEvent pje) ;


    /**
     * Called to notify the client that the job failed to complete
     * successfully and will have to be resubmitted.
     * @param pje the job generating this event
     */
    public void printJobFailed(PrintJobEvent pje) ;


    /**
     * Called to notify the client that the job was canceled
     * by a user or a program.
     * @param pje the job generating this event
     */
    public void printJobCanceled(PrintJobEvent pje) ;


    /**
     * Called to notify the client that no more events will be delivered.
     * One cause of this event being generated is if the job
     * has successfully completed, but the printing system
     * is limited in capability and cannot verify this.
     * This event is required to be delivered if none of the other
     * terminal events (completed/failed/canceled) are delivered.
     * @param pje the job generating this event
     */
    public void printJobNoMoreEvents(PrintJobEvent pje) ;


    /**
     * Called to notify the client that an error has occurred that the
     * user might be able to fix.  One example of an error that can
     * generate this event is when the printer runs out of paper.
     * @param pje the job generating this event
     */
    public void printJobRequiresAttention(PrintJobEvent pje) ;

}
