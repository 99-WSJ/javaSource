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

package java8.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.encoding.CDRInputObject;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.protocol.CorbaMessageMediatorImpl;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import com.sun.corba.se.impl.protocol.giopmsgheaders.MessageBase;
import com.sun.corba.se.pept.broker.Broker;
import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.protocol.ClientRequestDispatcher;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.pept.transport.ContactInfo;
import com.sun.corba.se.pept.transport.ContactInfoList;
import com.sun.corba.se.pept.transport.OutboundConnectionCache;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.ior.iiop.IIOPProfile;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.protocol.RequestDispatcherRegistry;
import com.sun.corba.se.spi.transport.CorbaConnection;
import com.sun.corba.se.spi.transport.CorbaContactInfo;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;

import java.nio.ByteBuffer;

/**
 * @author Harold Carr
 */
public abstract class CorbaContactInfoBase
    implements
        CorbaContactInfo
{
    protected ORB orb;
    protected CorbaContactInfoList contactInfoList;
    // NOTE: This may be different from same named one in CorbaContactInfoList.
    protected IOR effectiveTargetIOR;
    protected short addressingDisposition;
    protected OutboundConnectionCache connectionCache;

    ////////////////////////////////////////////////////
    //
    // pept.transport.ContactInfo
    //

    public Broker getBroker()
    {
        return orb;
    }

    public ContactInfoList getContactInfoList()
    {
        return contactInfoList;
    }

    public ClientRequestDispatcher getClientRequestDispatcher()
    {
        int scid =
            getEffectiveProfile().getObjectKeyTemplate().getSubcontractId() ;
        RequestDispatcherRegistry scr = orb.getRequestDispatcherRegistry() ;
        return scr.getClientRequestDispatcher( scid ) ;
    }

    // Note: not all derived classes will use a connection cache.
    // These are convenience methods that may not be used.
    public void setConnectionCache(OutboundConnectionCache connectionCache)
    {
        this.connectionCache = connectionCache;
    }

    public OutboundConnectionCache getConnectionCache()
    {
        return connectionCache;
    }

    // Called when client making an invocation.
    public MessageMediator createMessageMediator(Broker broker,
                                                 ContactInfo contactInfo,
                                                 Connection connection,
                                                 String methodName,
                                                 boolean isOneWay)
    {
        // REVISIT: Would like version, ior, requestid, etc., decisions
        // to be in client subcontract.  Cannot pass these to this
        // factory method because it breaks generic abstraction.
        // Maybe set methods on mediator called from subcontract
        // after creation?
        CorbaMessageMediator messageMediator =
            new CorbaMessageMediatorImpl(
                (ORB) broker,
                contactInfo,
                connection,
                GIOPVersion.chooseRequestVersion( (ORB)broker,
                     effectiveTargetIOR),
                effectiveTargetIOR,
                ((CorbaConnection)connection).getNextRequestId(),
                getAddressingDisposition(),
                methodName,
                isOneWay);

        return messageMediator;
    }

    // Called when connection handling a read event.
    public MessageMediator createMessageMediator(Broker broker,Connection conn)
    {
        ORB orb = (ORB) broker;
        CorbaConnection connection = (CorbaConnection) conn;

        if (orb.transportDebugFlag) {
            if (connection.shouldReadGiopHeaderOnly()) {
                dprint(
                ".createMessageMediator: waiting for message header on connection: "
                + connection);
            } else {
                dprint(
                ".createMessageMediator: waiting for message on connection: "
                + connection);
            }
        }

        Message msg = null;

        if (connection.shouldReadGiopHeaderOnly()) {
            // read giop header only
            msg = MessageBase.readGIOPHeader(orb, connection);
        } else {
            // read entire giop message
            msg = MessageBase.readGIOPMessage(orb, connection);
        }

        ByteBuffer byteBuffer = msg.getByteBuffer();
        msg.setByteBuffer(null);
        CorbaMessageMediator messageMediator =
            new CorbaMessageMediatorImpl(orb, connection, msg, byteBuffer);

        return messageMediator;
    }

    // Called when connection reading message body
    public MessageMediator finishCreatingMessageMediator(Broker broker,
                               Connection conn, MessageMediator messageMediator)
    {
        ORB orb = (ORB) broker;
        CorbaConnection connection = (CorbaConnection) conn;
        CorbaMessageMediator corbaMessageMediator =
                      (CorbaMessageMediator)messageMediator;

        if (orb.transportDebugFlag) {
            dprint(
            ".finishCreatingMessageMediator: waiting for message body on connection: "
                + connection);
        }

        Message msg = corbaMessageMediator.getDispatchHeader();
        msg.setByteBuffer(corbaMessageMediator.getDispatchBuffer());

        // read giop body only
        msg = MessageBase.readGIOPBody(orb, connection, msg);

        ByteBuffer byteBuffer = msg.getByteBuffer();
        msg.setByteBuffer(null);
        corbaMessageMediator.setDispatchHeader(msg);
        corbaMessageMediator.setDispatchBuffer(byteBuffer);

        return corbaMessageMediator;
    }

    public OutputObject createOutputObject(MessageMediator messageMediator)
    {
        CorbaMessageMediator corbaMessageMediator = (CorbaMessageMediator)
            messageMediator;

        OutputObject outputObject =
            sun.corba.OutputStreamFactory.newCDROutputObject(orb, messageMediator,
                                corbaMessageMediator.getRequestHeader(),
                                corbaMessageMediator.getStreamFormatVersion());

        messageMediator.setOutputObject(outputObject);
        return outputObject;
    }

    public InputObject createInputObject(Broker broker,
                                         MessageMediator messageMediator)
    {
        // REVISIT: Duplicate of acceptor code.
        CorbaMessageMediator corbaMessageMediator = (CorbaMessageMediator)
            messageMediator;
        return new CDRInputObject((ORB)broker,
                                  (CorbaConnection)messageMediator.getConnection(),
                                  corbaMessageMediator.getDispatchBuffer(),
                                  corbaMessageMediator.getDispatchHeader());
    }

    ////////////////////////////////////////////////////
    //
    // spi.transport.CorbaContactInfo
    //

    public short getAddressingDisposition()
    {
        return addressingDisposition;
    }

    public void setAddressingDisposition(short addressingDisposition)
    {
        this.addressingDisposition = addressingDisposition;
    }

    // REVISIT - remove this.
    public IOR getTargetIOR()
    {
        return  contactInfoList.getTargetIOR();
    }

    public IOR getEffectiveTargetIOR()
    {
        return effectiveTargetIOR ;
    }

    public IIOPProfile getEffectiveProfile()
    {
        return effectiveTargetIOR.getProfile();
    }

    ////////////////////////////////////////////////////
    //
    // java.lang.Object
    //

    public String toString()
    {
        return
            "CorbaContactInfoBase["
            + "]";
    }


    ////////////////////////////////////////////////////
    //
    // Implementation
    //

    protected void dprint(String msg)
    {
        ORBUtility.dprint("CorbaContactInfoBase", msg);
    }
}

// End of file.
