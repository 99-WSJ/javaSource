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
package java8.sun.corba.se.spi.oa;

import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.POASystemException;
import com.sun.corba.se.impl.oa.poa.Policies;
import com.sun.corba.se.spi.copyobject.ObjectCopierFactory;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.IORTemplate;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.logging.CORBALogDomains;
import com.sun.corba.se.spi.oa.OADestroyed;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.PIHandler;
import org.omg.CORBA.Policy;
import org.omg.PortableInterceptor.ObjectReferenceFactory;
import org.omg.PortableInterceptor.ObjectReferenceTemplate;

abstract public class ObjectAdapterBase extends org.omg.CORBA.LocalObject
    implements ObjectAdapter
{
    private ORB orb;

    // Exception wrappers
    private final POASystemException _iorWrapper ;
    private final POASystemException _invocationWrapper ;
    private final POASystemException _lifecycleWrapper ;
    private final OMGSystemException _omgInvocationWrapper ;
    private final OMGSystemException _omgLifecycleWrapper ;

    // Data related to the construction of object references and
    // supporting the Object Reference Template.
    private IORTemplate iortemp;
    private byte[] adapterId ;
    private ObjectReferenceTemplate adapterTemplate ;
    private ObjectReferenceFactory currentFactory ;

    public ObjectAdapterBase( ORB orb )
    {
        this.orb = orb ;
        _iorWrapper = POASystemException.get( orb,
            CORBALogDomains.OA_IOR ) ;
        _lifecycleWrapper = POASystemException.get( orb,
            CORBALogDomains.OA_LIFECYCLE ) ;
        _omgLifecycleWrapper = OMGSystemException.get( orb,
            CORBALogDomains.OA_LIFECYCLE ) ;
        _invocationWrapper = POASystemException.get( orb,
            CORBALogDomains.OA_INVOCATION ) ;
        _omgInvocationWrapper = OMGSystemException.get( orb,
            CORBALogDomains.OA_INVOCATION ) ;
    }

    public final POASystemException iorWrapper()
    {
        return _iorWrapper ;
    }

    public final POASystemException lifecycleWrapper()
    {
        return _lifecycleWrapper ;
    }

    public final OMGSystemException omgLifecycleWrapper()
    {
        return _omgLifecycleWrapper ;
    }

    public final POASystemException invocationWrapper()
    {
        return _invocationWrapper ;
    }

    public final OMGSystemException omgInvocationWrapper()
    {
        return _omgInvocationWrapper ;
    }

    /*
     * This creates the complete template.
     * When it is done, reference creation can proceed.
     */
    final public void initializeTemplate( ObjectKeyTemplate oktemp,
        boolean notifyORB, Policies policies, String codebase,
        String objectAdapterManagerId, ObjectAdapterId objectAdapterId)
    {
        adapterId = oktemp.getAdapterId() ;

        iortemp = IORFactories.makeIORTemplate(oktemp) ;

        // This calls acceptors which create profiles and may
        // add tagged components to those profiles.
        orb.getCorbaTransportManager().addToIORTemplate(
            iortemp, policies,
            codebase, objectAdapterManagerId, objectAdapterId);

        adapterTemplate = IORFactories.makeObjectReferenceTemplate( orb,
            iortemp ) ;
        currentFactory = adapterTemplate ;

        if (notifyORB) {
            PIHandler pih = orb.getPIHandler() ;
            if (pih != null)
                // This runs the IORInterceptors.
                pih.objectAdapterCreated( this ) ;
        }

        iortemp.makeImmutable() ;
    }

    final public org.omg.CORBA.Object makeObject( String repId, byte[] oid )
    {
        return currentFactory.make_object( repId, oid ) ;
    }

    final public byte[] getAdapterId()
    {
        return adapterId ;
    }

    final public ORB getORB()
    {
        return orb ;
    }

    abstract public Policy getEffectivePolicy( int type ) ;

    final public IORTemplate getIORTemplate()
    {
        return iortemp ;
    }

    abstract public int getManagerId() ;

    abstract public short getState() ;

    final public ObjectReferenceTemplate getAdapterTemplate()
    {
        return adapterTemplate ;
    }

    final public ObjectReferenceFactory getCurrentFactory()
    {
        return currentFactory ;
    }

    final public void setCurrentFactory( ObjectReferenceFactory factory )
    {
        currentFactory = factory ;
    }

    abstract public org.omg.CORBA.Object getLocalServant( byte[] objectId ) ;

    abstract public void getInvocationServant( OAInvocationInfo info ) ;

    abstract public void returnServant() ;

    abstract public void enter() throws OADestroyed ;

    abstract public void exit() ;

    abstract protected ObjectCopierFactory getObjectCopierFactory() ;

    // Note that all current subclasses share the same implementation of this method,
    // but overriding it would make sense for OAs that use a different InvocationInfo.
    public OAInvocationInfo makeInvocationInfo( byte[] objectId )
    {
        OAInvocationInfo info = new OAInvocationInfo( this, objectId ) ;
        info.setCopierFactory( getObjectCopierFactory() ) ;
        return info ;
    }

    abstract public String[] getInterfaces( Object servant, byte[] objectId ) ;
}
