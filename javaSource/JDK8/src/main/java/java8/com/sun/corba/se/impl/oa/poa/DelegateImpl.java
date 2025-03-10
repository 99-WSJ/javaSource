/*
 * Copyright (c) 1999, 2004, Oracle and/or its affiliates. All rights reserved.
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
package java8.com.sun.corba.se.impl.oa.poa;

import com.sun.corba.se.impl.logging.POASystemException;
import com.sun.corba.se.impl.oa.poa.POAFactory;
import com.sun.corba.se.impl.oa.poa.POAImpl;
import com.sun.corba.se.spi.logging.CORBALogDomains;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;

import java.util.EmptyStackException;

public class DelegateImpl implements org.omg.PortableServer.portable.Delegate
{
    private ORB orb ;
    private POASystemException wrapper ;
    private POAFactory factory;

    public DelegateImpl(ORB orb, POAFactory factory){
        this.orb = orb ;
        this.wrapper = POASystemException.get( orb,
            CORBALogDomains.OA ) ;
        this.factory = factory;
    }

    public org.omg.CORBA.ORB orb(Servant self)
    {
        return orb;
    }

    public org.omg.CORBA.Object this_object(Servant self)
    {
        byte[] oid;
        POA poa;
        try {
            oid = orb.peekInvocationInfo().id();
            poa = (POA)orb.peekInvocationInfo().oa();
            String repId = self._all_interfaces(poa,oid)[0] ;
            return poa.create_reference_with_id(oid, repId);
        } catch (EmptyStackException notInInvocationE) {
            //Not within an invocation context
            POAImpl defaultPOA = null;
            try {
                defaultPOA = (POAImpl)self._default_POA();
            } catch (ClassCastException exception){
                throw wrapper.defaultPoaNotPoaimpl( exception ) ;
            }

            try {
                if (defaultPOA.getPolicies().isImplicitlyActivated() ||
                    (defaultPOA.getPolicies().isUniqueIds() &&
                     defaultPOA.getPolicies().retainServants())) {
                    return defaultPOA.servant_to_reference(self);
                } else {
                    throw wrapper.wrongPoliciesForThisObject() ;
                }
            } catch ( org.omg.PortableServer.POAPackage.ServantNotActive e) {
                throw wrapper.thisObjectServantNotActive( e ) ;
            } catch ( org.omg.PortableServer.POAPackage.WrongPolicy e) {
                throw wrapper.thisObjectWrongPolicy( e ) ;
            }
        } catch (ClassCastException e) {
            throw wrapper.defaultPoaNotPoaimpl( e ) ;
        }
    }

    public POA poa(Servant self)
    {
        try {
            return (POA)orb.peekInvocationInfo().oa();
        } catch (EmptyStackException exception){
            POA returnValue = factory.lookupPOA(self);
            if (returnValue != null) {
                return returnValue;
            }

            throw wrapper.noContext( exception ) ;
        }
    }

    public byte[] object_id(Servant self)
    {
        try{
            return orb.peekInvocationInfo().id();
        } catch (EmptyStackException exception){
            throw wrapper.noContext(exception) ;
        }
    }

    public POA default_POA(Servant self)
    {
        return factory.getRootPOA();
    }

    public boolean is_a(Servant self, String repId)
    {
        String[] repositoryIds = self._all_interfaces(poa(self),object_id(self));
        for ( int i=0; i<repositoryIds.length; i++ )
            if ( repId.equals(repositoryIds[i]) )
                return true;

        return false;
    }

    public boolean non_existent(Servant self)
    {
        //REVISIT
        try{
            byte[] oid = orb.peekInvocationInfo().id();
            if( oid == null) return true;
            else return false;
        } catch (EmptyStackException exception){
            throw wrapper.noContext(exception) ;
        }
    }

    // The get_interface() method has been replaced by get_interface_def()

    public org.omg.CORBA.Object get_interface_def(Servant Self)
    {
        throw wrapper.methodNotImplemented() ;
    }
}
