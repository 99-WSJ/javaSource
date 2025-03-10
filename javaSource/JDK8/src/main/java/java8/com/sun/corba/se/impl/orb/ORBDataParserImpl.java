/*
 * Copyright (c) 2002, 2013, Oracle and/or its affiliates. All rights reserved.
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


package java8.com.sun.corba.se.impl.orb;

import com.sun.corba.se.impl.encoding.CodeSetComponentInfo;
import com.sun.corba.se.impl.legacy.connection.USLPort;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orb.ParserTable;
import com.sun.corba.se.pept.transport.Acceptor;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.logging.CORBALogDomains;
import com.sun.corba.se.spi.orb.*;
import com.sun.corba.se.spi.transport.CorbaContactInfoListFactory;
import com.sun.corba.se.spi.transport.IIOPPrimaryToContactInfo;
import com.sun.corba.se.spi.transport.IORToSocketInfo;
import com.sun.corba.se.spi.transport.ReadTimeouts;
import org.omg.CORBA.CompletionStatus;
import org.omg.PortableInterceptor.ORBInitializer;

import java.net.URL;


public class ORBDataParserImpl extends ParserImplTableBase implements ORBData
{
    private ORB orb ;
    private ORBUtilSystemException wrapper ;
    private String ORBInitialHost ;
    private int ORBInitialPort ;
    private String ORBServerHost ;
    private int ORBServerPort ;
    private String listenOnAllInterfaces;
    private com.sun.corba.se.spi.legacy.connection.ORBSocketFactory legacySocketFactory ;
    private com.sun.corba.se.spi.transport.ORBSocketFactory socketFactory;
    private USLPort[] userSpecifiedListenPorts ;
    private IORToSocketInfo iorToSocketInfo;
    private IIOPPrimaryToContactInfo iiopPrimaryToContactInfo;
    private String orbId ;
    private boolean orbServerIdPropertySpecified ;
    private URL servicesURL ;
    private String propertyInitRef ;
    private boolean allowLocalOptimization ;
    private GIOPVersion giopVersion ;
    private int highWaterMark ;
    private int lowWaterMark ;
    private int numberToReclaim ;
    private int giopFragmentSize ;
    private int giopBufferSize ;
    private int giop11BuffMgr ;
    private int giop12BuffMgr ;
    private short giopTargetAddressPreference ;
    private short giopAddressDisposition ;
    private boolean useByteOrderMarkers ;
    private boolean useByteOrderMarkersInEncaps ;
    private boolean alwaysSendCodeSetCtx ;
    private boolean persistentPortInitialized ;
    private int persistentServerPort ;
    private boolean persistentServerIdInitialized ;
    private int persistentServerId ;
    private boolean serverIsORBActivated ;
    private Class badServerIdHandlerClass ;
    private CodeSetComponentInfo.CodeSetComponent charData ;
    private CodeSetComponentInfo.CodeSetComponent wcharData ;
    private ORBInitializer[] orbInitializers ;
    private StringPair[] orbInitialReferences ;
    private String defaultInitRef ;
    private String[] debugFlags ;
    private Acceptor[] acceptors;
    private CorbaContactInfoListFactory corbaContactInfoListFactory;
    private String acceptorSocketType;
    private boolean acceptorSocketUseSelectThreadToWait;
    private boolean acceptorSocketUseWorkerThreadForEvent;
    private String connectionSocketType;
    private boolean connectionSocketUseSelectThreadToWait;
    private boolean connectionSocketUseWorkerThreadForEvent;
    private ReadTimeouts readTimeouts;
    private boolean disableDirectByteBufferUse;
    private boolean enableJavaSerialization;
    private boolean useRepId;

    // This is not initialized from ParserTable.
    private CodeSetComponentInfo codesets ;

// Public accessor methods ========================================================================

    public String getORBInitialHost()
    {
        return ORBInitialHost;
    }

    public int getORBInitialPort()
    {
        return ORBInitialPort;
    }

    public String getORBServerHost()
    {
        return ORBServerHost;
    }

    public String getListenOnAllInterfaces()
    {
        return listenOnAllInterfaces;
    }

    public int getORBServerPort()
    {
        return ORBServerPort;
    }

    public com.sun.corba.se.spi.legacy.connection.ORBSocketFactory getLegacySocketFactory()
    {
        return legacySocketFactory;
    }

    public com.sun.corba.se.spi.transport.ORBSocketFactory getSocketFactory()
    {
        return socketFactory;
    }

    public USLPort[] getUserSpecifiedListenPorts ()
    {
        return userSpecifiedListenPorts;
    }

    public IORToSocketInfo getIORToSocketInfo()
    {
        return iorToSocketInfo;
    }

    public IIOPPrimaryToContactInfo getIIOPPrimaryToContactInfo()
    {
        return iiopPrimaryToContactInfo;
    }

    public String getORBId()
    {
        return orbId;
    }

    public boolean getORBServerIdPropertySpecified()
    {
        return orbServerIdPropertySpecified;
    }

    public boolean isLocalOptimizationAllowed()
    {
        return allowLocalOptimization ;
    }

    public GIOPVersion getGIOPVersion()
    {
        return giopVersion;
    }

    public int getHighWaterMark()
    {
        return highWaterMark;
    }

    public int getLowWaterMark()
    {
        return lowWaterMark;
    }

    public int getNumberToReclaim()
    {
        return numberToReclaim;
    }

    public int getGIOPFragmentSize()
    {
        return giopFragmentSize;
    }

    public int getGIOPBufferSize()
    {
        return giopBufferSize;
    }

    public int getGIOPBuffMgrStrategy(GIOPVersion gv)
    {
        if(gv!=null){
            if (gv.equals(GIOPVersion.V1_0)) return 0; //Always grow for 1.0
            if (gv.equals(GIOPVersion.V1_1)) return giop11BuffMgr;
            if (gv.equals(GIOPVersion.V1_2)) return giop12BuffMgr;
        }
        //If a "faulty" GIOPVersion is passed, it's going to return 0;
        return 0;
    }

    /**
     * @return the GIOP Target Addressing preference of the ORB.
     * This ORB by default supports all addressing dispositions unless specified
     * otherwise via a java system property ORBConstants.GIOP_TARGET_ADDRESSING
     */
    public short getGIOPTargetAddressPreference()
    {
        return giopTargetAddressPreference;
    }

    public short getGIOPAddressDisposition()
    {
        return giopAddressDisposition;
    }

    public boolean useByteOrderMarkers()
    {
        return useByteOrderMarkers;
    }

    public boolean useByteOrderMarkersInEncapsulations()
    {
        return useByteOrderMarkersInEncaps;
    }

    public boolean alwaysSendCodeSetServiceContext()
    {
        return alwaysSendCodeSetCtx;
    }

    public boolean getPersistentPortInitialized()
    {
        return persistentPortInitialized ;
    }

    //public void setPersistentServerPort(int sp)
    //{
        //persistentServerPort = sp;
        //persistentPortInitialized = true;
    //}

    public int getPersistentServerPort()
    {
        if ( persistentPortInitialized ) // this is a user-activated server
            return persistentServerPort;
        else {
            throw wrapper.persistentServerportNotSet(
                CompletionStatus.COMPLETED_MAYBE );
        }
    }

    public boolean getPersistentServerIdInitialized()
    {
        return persistentServerIdInitialized;
    }

    /** Set the persistent-server-id of this server. This id is the same
     *  across multiple activations of this server. The id can be set to any
     *  integer value other than 0. This id must be set before any persistent
     *  objects can be created.
     */
    //public void setPersistentServerId(int id)
    //{
        //persistentServerId = id;
        //persistentServerIdInitialized = true;
    //}

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
    public int getPersistentServerId()
    {
        if ( persistentServerIdInitialized ) {
            return persistentServerId;
        } else {
            throw wrapper.persistentServeridNotSet(
                CompletionStatus.COMPLETED_MAYBE);
        }
    }

    public boolean getServerIsORBActivated()
    {
        return serverIsORBActivated ;
    }

    public Class getBadServerIdHandler()
    {
        return badServerIdHandlerClass ;
    }

     /**
     * Get the prefered code sets for connections. Should the client send the code set service context on every
     * request?
     */
    public CodeSetComponentInfo getCodeSetComponentInfo()
    {
        return codesets;
    }

    public ORBInitializer[] getORBInitializers()
    {
        return orbInitializers ;
    }

    public StringPair[] getORBInitialReferences()
    {
        return orbInitialReferences ;
    }

    public String getORBDefaultInitialReference()
    {
        return defaultInitRef ;
    }

    public String[] getORBDebugFlags()
    {
        return debugFlags ;
    }

    public Acceptor[] getAcceptors()
    {
        return acceptors;
    }

    public CorbaContactInfoListFactory getCorbaContactInfoListFactory()
    {
        return corbaContactInfoListFactory;
    }

    public String acceptorSocketType()
    {
        return acceptorSocketType;
    }
    public boolean acceptorSocketUseSelectThreadToWait()
    {
        return acceptorSocketUseSelectThreadToWait;
    }
    public boolean acceptorSocketUseWorkerThreadForEvent()
    {
        return acceptorSocketUseWorkerThreadForEvent;
    }
    public String connectionSocketType()
    {
        return connectionSocketType;
    }
    public boolean connectionSocketUseSelectThreadToWait()
    {
        return connectionSocketUseSelectThreadToWait;
    }
    public boolean connectionSocketUseWorkerThreadForEvent()
    {
        return connectionSocketUseWorkerThreadForEvent;
    }
    public boolean isJavaSerializationEnabled()
    {
        return enableJavaSerialization;
    }
    public ReadTimeouts getTransportTCPReadTimeouts()
    {
        return readTimeouts;
    }
    public boolean disableDirectByteBufferUse()
    {
        return disableDirectByteBufferUse ;
    }
    public boolean useRepId()
    {
        return useRepId;
    }

// Methods for constructing and initializing this object ===========================================

    public ORBDataParserImpl( ORB orb, DataCollector coll )
    {
        super( ParserTable.get().getParserData() ) ;
        this.orb = orb ;
        wrapper = ORBUtilSystemException.get( orb, CORBALogDomains.ORB_LIFECYCLE ) ;
        init( coll ) ;
        complete() ;
    }

    public void complete()
    {
        codesets = new CodeSetComponentInfo(charData, wcharData);
    }
}

// End of file.
