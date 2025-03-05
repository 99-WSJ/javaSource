/*
 * Copyright (c) 2002, 2006, Oracle and/or its affiliates. All rights reserved.
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

package java8.javax.management.timer;

import javax.management.timer.TimerAlarmClock;

/**
 * <p>Definitions of the notifications sent by TimerAlarmClock
 * MBeans.</p>
 */
class TimerAlarmClockNotification
    extends javax.management.Notification {

    /* Serial version */
    private static final long serialVersionUID = -4841061275673620641L;

    /*
     * ------------------------------------------
     *  CONSTRUCTORS
     * ------------------------------------------
     */

    /**
     * Constructor.
     *
     * @param source the source.
     */
    public TimerAlarmClockNotification(javax.management.timer.TimerAlarmClock source) {
        super("", source, 0);
    }
}
