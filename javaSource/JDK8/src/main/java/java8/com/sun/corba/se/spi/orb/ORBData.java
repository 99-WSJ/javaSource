/*
 * Copyright (c) 2002, 2004, Oracle and/or its affiliates. All rights reserved.
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

package java8.sun.corba.se.spi.orb;

import com.sun.corba.se.impl.encoding.CodeSetComponentInfo;
import com.sun.corba.se.impl.legacy.connection.USLPort;
import com.sun.corba.se.pept.transport.Acceptor;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.StringPair;
import com.sun.corba.se.spi.transport.CorbaContactInfoListFactory;
import com.sun.corba.se.spi.transport.IIOPPrimaryToContactInfo;
import com.sun.corba.se.spi.transport.IORToSocketInfo;
import com.sun.corba.se.spi.transport.ReadTimeouts;
import org.omg.PortableInterceptor.ORBInitializer;

public interface ORBData {
    public String getORBInitialHost() ;

    public int getORBInitialPort() ;

    public String getORBServerHost() ;

    public int getORBServerPort() ;

    public String getListenOnAllInterfaces();

    public com.sun.corba.se.spi.legacy.connection.ORBSocketFactory getLegacySocketFactory () ;

    public com.sun.corba.se.spi.transport.ORBSocketFactory getSocketFactory();

    public USLPort[] getUserSpecifiedListenPorts () ;

    public IORToSocketInfo getIORToSocketInfo();

    public IIOPPrimaryToContactInfo getIIOPPrimaryToContactInfo();

    public String getORBId() ;

    public boolean getORBServerIdPropertySpecified() ;

    public boolean isLocalOptimizationAllowed() ;

    public GIOPVersion getGIOPVersion() ;

    public int getHighWaterMark() ;

    public int getLowWaterMark() ;

    public int getNumberToReclaim() ;

    public int getGIOPFragmentSize() ;

    public int getGIOPBufferSize() ;

    public int getGIOPBuffMgrStrategy(GIOPVersion gv) ;

    /**
     * @return the GIOP Target Addressing preference of the ORB.
     * This ORB by default supports all addressing dispositions unless specified
     * otherwise via a java system property ORBConstants.GIOP_TARGET_ADDRESSING
     */
    public short getGIOPTargetAddressPreference() ;

    public short getGIOPAddressDisposition() ;

    public boolean useByteOrderMarkers() ;

    public boolean useByteOrderMarkersInEncapsulations() ;

    public boolean alwaysSendCodeSetServiceContext() ;

    public boolean getPersistentPortInitialized() ;

    public int getPersistentServerPort();

    public boolean getPersistentServerIdInitialized() ;

    /** Return the persistent-server-id of this server. This id is the same
     *  across multiple activations of this server. This is in contrast to
     *  com.sun.corba.se.impl.iiop.ORB.getTransientServerId() which
     *  returns a transient id that is guaranteed to be different
     *  across multiple activations of
     *  this server. The user/environment is required to supply the
     *  persistent-server-id every time this server is started, in
     *  the ORBServerId parameter, System properties, or other means.
     *  The user is also required to ensure that no two persistent servers
     *  on the same host have the same server-id.
     */
    public int getPersistentServerId();

    public boolean getServerIsORBActivated() ;

    public Class getBadServerIdHandler();

    /**
    * Get the prefered code sets for connections. Should the client send the
    * code set service context on every request?
    */
    public CodeSetComponentInfo getCodeSetComponentInfo() ;

    public ORBInitializer[] getORBInitializers();

    public StringPair[] getORBInitialReferences();

    public String getORBDefaultInitialReference() ;

    public String[] getORBDebugFlags();

    public Acceptor[] getAcceptors();

    public CorbaContactInfoListFactory getCorbaContactInfoListFactory();

    public String acceptorSocketType();
    public boolean acceptorSocketUseSelectThreadToWait();
    public boolean acceptorSocketUseWorkerThreadForEvent();
    public String connectionSocketType();
    public boolean connectionSocketUseSelectThreadToWait();
    public boolean connectionSocketUseWorkerThreadForEvent();

    public ReadTimeouts getTransportTCPReadTimeouts();
    public boolean disableDirectByteBufferUse() ;
    public boolean isJavaSerializationEnabled();
    public boolean useRepId();
}

// End of file.
