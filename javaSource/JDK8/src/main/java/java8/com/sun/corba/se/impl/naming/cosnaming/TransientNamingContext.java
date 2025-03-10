/*
 * Copyright (c) 1996, 2003, Oracle and/or its affiliates. All rights reserved.
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

package java8.sun.corba.se.impl.naming.cosnaming;

// Import general CORBA classes

import com.sun.corba.se.impl.logging.NamingSystemException;
import com.sun.corba.se.impl.naming.cosnaming.InternalBindingKey;
import com.sun.corba.se.impl.naming.cosnaming.InternalBindingValue;
import com.sun.corba.se.impl.naming.cosnaming.NamingContextDataStore;
import com.sun.corba.se.impl.naming.cosnaming.NamingContextImpl;
import com.sun.corba.se.impl.naming.cosnaming.TransientBindingIterator;
import com.sun.corba.se.impl.naming.cosnaming.TransientNameService;
import com.sun.corba.se.impl.orbutil.LogKeywords;
import com.sun.corba.se.spi.logging.CORBALogDomains;
import org.omg.CORBA.Object;
import org.omg.CORBA.SystemException;
import org.omg.CosNaming.*;
import org.omg.PortableServer.POA;

import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class TransientNamingContext implements the methods defined
 * by NamingContextDataStore, and extends the NamingContextImpl class to
 * provide a servant implementation of CosNaming::NamingContext.
 * The TransientNamingContext uses a hash table
 * to store the mappings between bindings and object references and the
 * hash table is not persistent; thereby the name "transient".
 * This class should not be used directly; instead, the class
 * TransientNameService should be instantiated.
 * <p>
 * The keys in the hash table are InternalBindingKey objects, containing
 * a single NameComponent and implementing the proper functions, i.e.,
 * equals() and hashCode() in an efficient manner. The values in the hash
 * table are InternalBindingValues and store a org.omg.CosNaming::Binding and
 * the object reference associated with the binding. For iteration,
 * TransientBindingIterator objects are created, which are passed a cloned
 * copy of the hashtable. Since elements are inserted and deleted and
 * never modified, this provides stable iterators at the cost of cloning
 * the hash table.
 * <p>
 * To create and destroy object references, the TransientNamingContext
 * uses the orb.connect() and orb.disconnect() methods.
 *
 * @see NamingContextImpl
 * @see NamingContextDataStore
 * @see TransientBindingIterator
 * @see TransientNameService
 */
public class TransientNamingContext extends NamingContextImpl implements NamingContextDataStore
{
    private Logger readLogger, updateLogger, lifecycleLogger;

    // XXX: the wrapper calls are all preceded by logger updates.
    // These can be combined, and then we simply use 3 NamingSystemException wrappers,
    // for read, update, and lifecycl.
    private NamingSystemException wrapper ;

    /**
     * Constructs a new TransientNamingContext object.
     * @param orb an orb object.
     * @param initial the initial naming context.
     * @exception Exception a Java exception thrown of the base class cannot
     * initialize.
     */
    public TransientNamingContext(com.sun.corba.se.spi.orb.ORB orb,
        Object initial,
        POA nsPOA )
        throws Exception
    {
        super(orb, nsPOA );
        wrapper = NamingSystemException.get( orb, CORBALogDomains.NAMING ) ;

        this.localRoot = initial;
        readLogger = orb.getLogger( CORBALogDomains.NAMING_READ);
        updateLogger = orb.getLogger( CORBALogDomains.NAMING_UPDATE);
        lifecycleLogger = orb.getLogger(
            CORBALogDomains.NAMING_LIFECYCLE);
        lifecycleLogger.fine( "Root TransientNamingContext LIFECYCLE.CREATED" );
    }

    /**
     * Binds the object to the name component as the specified binding type.
     * It creates a InternalBindingKey object and a InternalBindingValue
     * object and inserts them in the hash table.
     * @param n A single org.omg.CosNaming::NameComponent under which the
     * object will be bound.
     * @param obj An object reference to be bound under the supplied name.
     * @param bt The type of the binding (i.e., as object or as context).
     * @exception SystemException One of a fixed set of CORBA
     * system exceptions.
     */
    public final void Bind(NameComponent n, Object obj,
                           BindingType bt)
        throws SystemException
    {
        // Create a key and a value
        InternalBindingKey key = new InternalBindingKey(n);
        NameComponent[] name = new NameComponent[1];
        name[0] = n;
        Binding b = new Binding(name,bt);
        InternalBindingValue value = new InternalBindingValue(b,null);
        value.theObjectRef = obj;
        // insert it
        InternalBindingValue oldValue =
            (InternalBindingValue)this.theHashtable.put(key,value);

        if (oldValue != null) {
            updateLogger.warning( LogKeywords.NAMING_BIND + "Name " +
                getName( n ) + " Was Already Bound" );
            throw wrapper.transNcBindAlreadyBound() ;
        }
        if( updateLogger.isLoggable( Level.FINE ) ) {
            updateLogger.fine( LogKeywords.NAMING_BIND_SUCCESS +
                "Name Component: " + n.id + "." + n.kind );
        }
    }

    /**
     * Resolves the supplied name to an object reference and returns
     * the type of the resolved binding. It creates a InternalBindingKey
     * and uses the key for looking up in the hash table. If nothing
     * is found an exception is thrown, otherwise the object reference
     * is returned and the binding type set.
     * @param n a NameComponent which is the name to be resolved.
     * @param bth the BindingType as an out parameter.
     * @return the object reference bound under the supplied name, null if not
     * found.
     * @exception SystemException One of a fixed set of CORBA
     * system exceptions.
     */
    public final Object Resolve(NameComponent n,
                                              BindingTypeHolder bth)
        throws SystemException
    {
        // Is the initial naming context requested?
        if ( (n.id.length() == 0)
           &&(n.kind.length() == 0 ) )
        {
            bth.value = BindingType.ncontext;
            return localRoot;
        }

        // Create a key and lookup the value
        InternalBindingKey key = new InternalBindingKey(n);

        InternalBindingValue value =
            (InternalBindingValue) this.theHashtable.get(key);
        if (value == null) return null;
        if( readLogger.isLoggable( Level.FINE ) ) {
            readLogger.fine( LogKeywords.NAMING_RESOLVE_SUCCESS
                + "Namecomponent :" + getName( n ) );
        }

        // Copy out binding type and object reference
        bth.value = value.theBinding.binding_type;
        return value.theObjectRef;
    }

    /**
     * Deletes the binding with the supplied name. It creates a
     * InternalBindingKey and uses it to remove the value associated
     * with the key. If nothing is found an exception is thrown, otherwise
     * the element is removed from the hash table.
     * @param n a NameComponent which is the name to unbind
     * @return the object reference bound to the name, or null if not found.
     * @exception SystemException One of a fixed set of CORBA
     * system exceptions.
     */
    public final Object Unbind(NameComponent n)
        throws SystemException
    {
        // Create a key and remove it from the hashtable
        InternalBindingKey key = new InternalBindingKey(n);
        InternalBindingValue value =
            (InternalBindingValue)this.theHashtable.remove(key);

        // Return what was found
        if (value == null) {
            if( updateLogger.isLoggable( Level.FINE ) ) {
                updateLogger.fine( LogKeywords.NAMING_UNBIND_FAILURE +
                    " There was no binding with the name " + getName( n ) +
                    " to Unbind " );
            }
            return null;
        } else {
            if( updateLogger.isLoggable( Level.FINE ) ) {
                updateLogger.fine( LogKeywords.NAMING_UNBIND_SUCCESS +
                    " NameComponent:  " + getName( n ) );
            }
            return value.theObjectRef;
       }
    }

    /**
     * List the contents of this NamingContext. It creates a new
     * TransientBindingIterator object and passes it a clone of the
     * hash table and an orb object. It then uses the
     * newly created object to return the required number of bindings.
     * @param how_many The number of requested bindings in the BindingList.
     * @param bl The BindingList as an out parameter.
     * @param bi The BindingIterator as an out parameter.
     * @exception SystemException One of a fixed set of CORBA
     * system exceptions.
     */
    public final void List(int how_many, BindingListHolder bl,
                           BindingIteratorHolder bi)
        throws SystemException
    {
        try {
            // Create a new binding iterator servant with a copy of this
            // hashtable. nsPOA is passed to the object so that it can
            // de-activate itself from the Active Object Map when
            // Binding Iterator.destroy is called.
            TransientBindingIterator bindingIterator =
                new TransientBindingIterator(this.orb,
                (Hashtable)this.theHashtable.clone(), nsPOA);
            // Have it set the binding list
            bindingIterator.list(how_many,bl);

            byte[] objectId = nsPOA.activate_object( bindingIterator );
            Object obj = nsPOA.id_to_reference( objectId );

            // Get the object reference for the binding iterator servant
            org.omg.CosNaming.BindingIterator bindingRef =
                org.omg.CosNaming.BindingIteratorHelper.narrow( obj );

            bi.value = bindingRef;
        } catch (SystemException e) {
            readLogger.warning( LogKeywords.NAMING_LIST_FAILURE + e );
            throw e;
        } catch (Exception e) {
            // Convert to a CORBA system exception
            readLogger.severe( LogKeywords.NAMING_LIST_FAILURE + e );
            throw wrapper.transNcListGotExc( e ) ;
        }
    }

    /**
     * Create a new NamingContext. It creates a new TransientNamingContext
     * object, passing it the orb object.
     * @return an object reference for a new NamingContext object implemented
     * by this Name Server.
     * @exception SystemException One of a fixed set of CORBA
     * system exceptions.
     */
    public final NamingContext NewContext()
        throws SystemException
    {
        try {
            // Create a new servant
            com.sun.corba.se.impl.naming.cosnaming.TransientNamingContext transContext =
                new com.sun.corba.se.impl.naming.cosnaming.TransientNamingContext(
                (com.sun.corba.se.spi.orb.ORB) orb,localRoot, nsPOA);

            byte[] objectId = nsPOA.activate_object( transContext );
            Object obj = nsPOA.id_to_reference( objectId );
            lifecycleLogger.fine( "TransientNamingContext " +
                "LIFECYCLE.CREATE SUCCESSFUL" );
            return org.omg.CosNaming.NamingContextHelper.narrow( obj );

        } catch (SystemException e) {
            lifecycleLogger.log(
                Level.WARNING, LogKeywords.LIFECYCLE_CREATE_FAILURE, e );
            throw e;
        } catch (Exception e) {
            lifecycleLogger.log(
                Level.WARNING, LogKeywords.LIFECYCLE_CREATE_FAILURE, e );
            throw wrapper.transNcNewctxGotExc( e ) ;
        }
    }

    /**
     * Destroys this NamingContext by disconnecting from the ORB.
     * @exception SystemException One of a fixed set of CORBA
     * system exceptions.
     */
    public final void Destroy()
        throws SystemException
    {
        // Destroy the object reference by disconnecting from the ORB
        try {
            byte[] objectId = nsPOA.servant_to_id( this );
            if( objectId != null ) {
                nsPOA.deactivate_object( objectId );
            }
            if( lifecycleLogger.isLoggable( Level.FINE ) ) {
                lifecycleLogger.fine(
                    LogKeywords.LIFECYCLE_DESTROY_SUCCESS );
            }
        } catch (SystemException e) {
            lifecycleLogger.log( Level.WARNING,
                LogKeywords.LIFECYCLE_DESTROY_FAILURE, e );
            throw e;
        } catch (Exception e) {
            lifecycleLogger.log( Level.WARNING,
                LogKeywords.LIFECYCLE_DESTROY_FAILURE, e );
            throw wrapper.transNcDestroyGotExc( e ) ;
        }
    }

    /**
     * A Utility Method For Logging..
     */
    private String getName( NameComponent n ) {
        return n.id + "." + n.kind;
    }

    /**
     * Return whether this NamingContext contains any bindings. It forwards
     * this request to the hash table.
     * @return true if this NamingContext contains no bindings.
     */
    public final boolean IsEmpty()
    {
        return this.theHashtable.isEmpty();
    }

    // A hashtable to store the bindings
    private final Hashtable  theHashtable = new Hashtable();

    /**
     * The local root naming context.
     */
    public Object localRoot;
}
