/*
 * Copyright (c) 2001, 2003, Oracle and/or its affiliates. All rights reserved.
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
import com.sun.corba.se.pept.transport.ContactInfo;

/**
 * <code>ClientRequestDispatcher</code> coordinates the request (and possible
 * response) processing for a specific <em>protocol</em>.
 *
 * @author Harold Carr
 */
public interface ClientRequestDispatcher
{
    /**
     * At the beginning of a request the presentation block uses this
     * to obtain an
     * {@link OutputObject OutputObject}
     * to set data to be sent on a message.
     *
     * @param self -
     * @param methodName - the remote method name
     * @param isOneWay - <code>true</code> if the message is asynchronous
     * @param contactInfo - the
     * {@link ContactInfo ContactInfo}
     * which which created/chose this <code>ClientRequestDispatcher</code>
     *
     * @return
     * {@link OutputObject OutputObject}
     */
    public OutputObject beginRequest(Object self,
                                     String methodName,
                                     boolean isOneWay,
                                     ContactInfo contactInfo);

    /**
     * After the presentation block has set data on the
     * {@link OutputObject OutputObject}
     * it signals the PEPt runtime to send the encoded data by calling this
     * method.
     *
     * @param self -
     * @param outputObject
     *
     * @return
     * {@link InputObject InputObject}
     * if the message is synchronous.
     *
     * @throws
     * {@link org.omg.CORBA.portable.ApplicationException ApplicationException}
     * if the remote side raises an exception declared in the remote interface.
     *
     * @throws
     * {@link org.omg.CORBA.portable.RemarshalException RemarshalException}
     * if the PEPt runtime would like the presentation block to start over.
     */
    public InputObject marshalingComplete(Object self,
                                          OutputObject outputObject)
    // REVISIT EXCEPTIONS
        throws
            org.omg.CORBA.portable.ApplicationException,
            org.omg.CORBA.portable.RemarshalException;

    /**
     * After the presentation block completes a request it signals
     * the PEPt runtime by calling this method.
     *
     * This method may release resources.  In some cases it may cause
     * control or error messages to be sent.
     *
     * @param broker -
     * @param inputObject -
     */
    public void endRequest(Broker broker,
                           Object self,
                           InputObject inputObject);
}

// End of file.
