/*
 * Copyright (c) 2001, 2004, Oracle and/or its affiliates. All rights reserved.
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

package java8.sun.corba.se.pept.protocol;

import com.sun.corba.se.pept.broker.Broker;
import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.pept.transport.ContactInfo;

/**
 * <code>MessageMediator</code> is a central repository for artifacts
 * associated with an individual message.
 *
 * @author Harold Carr
 */
public interface MessageMediator
{
    /**
     * The {@link Broker Broker} associated
     * with an invocation.
     *
     * @return {@link Broker Broker}
     */
    public Broker getBroker();

    /**
     * Get the
     * {@link ContactInfo ContactInfo}
     * which created this <code>MessageMediator</code>.
     *
     * @return
     * {@link ContactInfo ContactInfo}
     */
    public ContactInfo getContactInfo();

    /**
     * Get the
     * {@link Connection Connection}
     * on which this message is sent or received.
     */
    public Connection getConnection();

    /**
     * Used to initialize message headers.
     *
     * Note: this should be moved to a <code>RequestDispatcher</code>.
     */
    public void initializeMessage();

    /**
     * Used to send the message (or its last fragment).
     *
     * Note: this should be moved to a <code>RequestDispatcher</code>.
     */
    public void finishSendingRequest();

    /**
     * Used to wait for a response for synchronous messages.
     *
     * @deprecated
     */
    @Deprecated
    public InputObject waitForResponse();

    /**
     * Used to set the
     * {@link OutputObject OutputObject}
     * used for the message.
     *
     * @param outputObject
     */
    public void setOutputObject(OutputObject outputObject);

    /**
     * Used to get the
     * {@link OutputObject OutputObject}
     * used for the message.
     *
     * @return
     * {@link OutputObject OutputObject}
     */
    public OutputObject getOutputObject();

    /**
     * Used to set the
     * {@link InputObject InputObject}
     * used for the message.
     *
     * @param inputObject
     */
    public void setInputObject(InputObject inputObject);

    /**
     * Used to get the
     * {@link InputObject InputObject}
     * used for the message.
     *
     * @return
     * {@link InputObject InputObject}
     */
    public InputObject getInputObject();
}

// End of file.
