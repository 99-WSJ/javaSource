/*
 * Copyright (c) 2000, 2003, Oracle and/or its affiliates. All rights reserved.
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

package java8.sun.corba.se.impl.protocol.giopmsgheaders;

import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;

/**
 * This interface captures the CancelRequestMessage contract.
 *
 * @author Ram Jeyaraman 05/14/2000
 */

public interface CancelRequestMessage extends Message {
    int CANCEL_REQ_MSG_SIZE = 4;
    int getRequestId();
}
