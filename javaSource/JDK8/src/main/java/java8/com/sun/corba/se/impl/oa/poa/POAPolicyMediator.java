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

package java8.com.sun.corba.se.impl.oa.poa;

import com.sun.corba.se.impl.oa.poa.Policies;
import org.omg.PortableServer.ForwardRequest;
import org.omg.PortableServer.POAPackage.*;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.ServantManager;

/** POAPolicyMediator defines an interface to which the POA delegates all
 * policy specific operations.  This permits code paths for different
 * policies to be optimized by creating the correct code at POA creation
 * time.  Also note that as much as possible, this interface does not
 * do any concurrency control, except as noted.  The POA is responsible
 * for concurrency control.
 */
public interface POAPolicyMediator {
    /** Return the policies object that was used to create this
    * POAPolicyMediator.
    */
    Policies getPolicies() ;

    /** Return the subcontract ID to use in the IIOP profile in IORs
    * created by this POAPolicyMediator's POA.  This is initialized
    * according to the policies and the POA used to construct this
    * POAPolicyMediator in the POAPolicyMediatorFactory.
    */
    int getScid() ;

    /** Return the server ID to use in the IIOP profile in IORs
    * created by this POAPolicyMediator's POA.  This is initialized
    * according to the policies and the POA used to construct this
    * POAPolicyMediator in the POAPolicyMediatorFactory.
    */
    int getServerId() ;

    /** Get the servant to use for an invocation with the
    * given id and operation.
    * @param id the object ID for which we are requesting a servant
    * @param operation the name of the operation to be performed on
    * the servant
    * @return the resulting Servant.
    */
    Object getInvocationServant( byte[] id,
        String operation ) throws ForwardRequest ;

    /** Release a servant that was obtained from getInvocationServant.
    */
    void returnServant() ;

    /** Etherealize all servants associated with this POAPolicyMediator.
    * Does nothing if the retention policy is non-retain.
    */
    void etherealizeAll() ;

    /** Delete everything in the active object map.
    */
    void clearAOM() ;

    /** Return the servant manager.  Will throw WrongPolicy
    * if the request processing policy is not USE_SERVANT_MANAGER.
    */
    ServantManager getServantManager() throws WrongPolicy ;

    /** Set the servant manager.  Will throw WrongPolicy
    * if the request processing policy is not USE_SERVANT_MANAGER.
    */
    void setServantManager( ServantManager servantManager ) throws WrongPolicy ;

    /** Return the default servant.   Will throw WrongPolicy
    * if the request processing policy is not USE_DEFAULT_SERVANT.
    */
    Servant getDefaultServant() throws NoServant, WrongPolicy ;

    /** Set the default servant.   Will throw WrongPolicy
    * if the request processing policy is not USE_DEFAULT_SERVANT.
    */
    void setDefaultServant( Servant servant ) throws WrongPolicy ;

    void activateObject( byte[] id, Servant servant )
        throws ObjectAlreadyActive, ServantAlreadyActive, WrongPolicy ;

    /** Deactivate the object that is associated with the given id.
    * Returns the servant for id.
    */
    Servant deactivateObject( byte[] id ) throws ObjectNotActive, WrongPolicy ;

    /** Allocate a new, unique system ID.  Requires the ID assignment policy
    * to be SYSTEM.
    */
    byte[] newSystemId() throws WrongPolicy ;

    byte[] servantToId( Servant servant ) throws ServantNotActive, WrongPolicy ;

    Servant idToServant( byte[] id ) throws ObjectNotActive, WrongPolicy ;
}
