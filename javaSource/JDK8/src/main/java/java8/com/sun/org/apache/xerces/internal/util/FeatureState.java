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
 * $Id: FeatureState.java 3024 2011-03-01 03:46:13Z joehw $
 */

package java8.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.util.Status;

public class FeatureState {

    public final Status status;
    public final boolean state;

    public static final com.sun.org.apache.xerces.internal.util.FeatureState SET_ENABLED = new com.sun.org.apache.xerces.internal.util.FeatureState(Status.SET, true);
    public static final com.sun.org.apache.xerces.internal.util.FeatureState SET_DISABLED = new com.sun.org.apache.xerces.internal.util.FeatureState(Status.SET, false);
    public static final com.sun.org.apache.xerces.internal.util.FeatureState UNKNOWN = new com.sun.org.apache.xerces.internal.util.FeatureState(Status.UNKNOWN, false);
    public static final com.sun.org.apache.xerces.internal.util.FeatureState RECOGNIZED = new com.sun.org.apache.xerces.internal.util.FeatureState(Status.RECOGNIZED, false);
    public static final com.sun.org.apache.xerces.internal.util.FeatureState NOT_SUPPORTED = new com.sun.org.apache.xerces.internal.util.FeatureState(Status.NOT_SUPPORTED, false);
    public static final com.sun.org.apache.xerces.internal.util.FeatureState NOT_RECOGNIZED = new com.sun.org.apache.xerces.internal.util.FeatureState(Status.NOT_RECOGNIZED, false);
    public static final com.sun.org.apache.xerces.internal.util.FeatureState NOT_ALLOWED = new com.sun.org.apache.xerces.internal.util.FeatureState(Status.NOT_ALLOWED, false);

    public FeatureState(Status status, boolean state) {
        this.status = status;
        this.state = state;
    }

    public static com.sun.org.apache.xerces.internal.util.FeatureState of(Status status) {
        return new com.sun.org.apache.xerces.internal.util.FeatureState(status, false);
    }

    public static com.sun.org.apache.xerces.internal.util.FeatureState is(boolean value) {
        return new com.sun.org.apache.xerces.internal.util.FeatureState(Status.SET, value);
    }

    public boolean isExceptional() {
        return this.status.isExceptional();
    }
}
