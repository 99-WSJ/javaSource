/*
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

/*
 * Copyright (c) 2009 by Oracle Corporation. All Rights Reserved.
 */

/*
 * $Id: PropertyState.java 3024 2011-03-01 03:46:13Z joehw $
 */
package java8.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.util.Status;

public class PropertyState {

    public final Status status;
    public final Object state;

    public static final com.sun.org.apache.xerces.internal.util.PropertyState UNKNOWN = new com.sun.org.apache.xerces.internal.util.PropertyState(Status.UNKNOWN, null);
    public static final com.sun.org.apache.xerces.internal.util.PropertyState RECOGNIZED = new com.sun.org.apache.xerces.internal.util.PropertyState(Status.RECOGNIZED, null);
    public static final com.sun.org.apache.xerces.internal.util.PropertyState NOT_SUPPORTED = new com.sun.org.apache.xerces.internal.util.PropertyState(Status.NOT_SUPPORTED, null);
    public static final com.sun.org.apache.xerces.internal.util.PropertyState NOT_RECOGNIZED = new com.sun.org.apache.xerces.internal.util.PropertyState(Status.NOT_RECOGNIZED, null);
    public static final com.sun.org.apache.xerces.internal.util.PropertyState NOT_ALLOWED = new com.sun.org.apache.xerces.internal.util.PropertyState(Status.NOT_ALLOWED, null);


    public PropertyState(Status status, Object state) {
        this.status = status;
        this.state = state;
    }

    public static com.sun.org.apache.xerces.internal.util.PropertyState of(Status status) {
        return new com.sun.org.apache.xerces.internal.util.PropertyState(status, null);
    }

    public static com.sun.org.apache.xerces.internal.util.PropertyState is(Object value) {
        return new com.sun.org.apache.xerces.internal.util.PropertyState(Status.SET, value);
    }

    public boolean isExceptional() {
        return this.status.isExceptional();
    }
}
