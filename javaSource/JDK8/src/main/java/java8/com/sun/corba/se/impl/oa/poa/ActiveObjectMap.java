/*
 * Copyright (c) 1997, 2003, Oracle and/or its affiliates. All rights reserved.
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

package java8.sun.corba.se.impl.oa.poa;

import com.sun.corba.se.impl.oa.poa.AOMEntry;
import com.sun.corba.se.impl.oa.poa.POAImpl;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.PortableServer.Servant;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**  The ActiveObjectMap maintains associations between servants and
 * their keys.  There are two variants, to support whether or not
 * multiple IDs per servant are allowed.  This class suppots bidirectional
 * traversal of the key-servant association.  Access to an instance of this
 * class is serialized by the POA mutex.
 */
public abstract class ActiveObjectMap
{
    public static class Key {
        public byte[] id;

        Key(byte[] id) {
            this.id = id;
        }

        public String toString() {
            StringBuffer buffer = new StringBuffer();
            for(int i = 0; i < id.length; i++) {
                buffer.append(Integer.toString((int) id[i], 16));
                if (i != id.length-1)
                    buffer.append(":");
            }
            return buffer.toString();
        }

        public boolean equals(Object key) {
            if (!(key instanceof Key))
                return false;
            Key k = (Key) key;
            if (k.id.length != this.id.length)
                return false;
            for(int i = 0; i < this.id.length; i++)
                if (this.id[i] != k.id[i])
                    return false;
            return true;
        }

        // Use the same hash function as for String
        public int hashCode() {
            int h = 0;
            for (int i = 0; i < id.length; i++)
                h = 31*h + id[i];
            return h;
        }
    }

    protected POAImpl poa ;

    protected ActiveObjectMap( POAImpl poa )
    {
        this.poa = poa ;
    }

    public static com.sun.corba.se.impl.oa.poa.ActiveObjectMap create(POAImpl poa, boolean multipleIDsAllowed )
    {
        if (multipleIDsAllowed)
            return new com.sun.corba.se.impl.oa.poa.MultipleObjectMap( poa ) ;
        else
            return new com.sun.corba.se.impl.oa.poa.SingleObjectMap(poa ) ;
    }

    private Map keyToEntry = new HashMap() ;     // Map< Key, AOMEntry >
    private Map entryToServant = new HashMap() ; // Map< AOMEntry, Servant >
    private Map servantToEntry = new HashMap() ; // Map< Servant, AOMEntry >

    public final boolean contains(Servant value)
    {
        return servantToEntry.containsKey( value ) ;
    }

    public final boolean containsKey(Key key)
    {
        return keyToEntry.containsKey(key);
    }

    /** get Returbs the entry assigned to the key, or creates a new
    * entry in state INVALID if none is present.
    */
    public final AOMEntry get(Key key)
    {
        AOMEntry result = (AOMEntry)keyToEntry.get(key);
        if (result == null) {
            result = new AOMEntry( poa ) ;
            putEntry( key, result ) ;
        }

        return result ;
    }

    public final Servant getServant( AOMEntry entry )
    {
        return (Servant)entryToServant.get( entry ) ;
    }

    public abstract Key getKey(AOMEntry value) throws WrongPolicy ;

    public Key getKey(Servant value) throws WrongPolicy
    {
        AOMEntry entry = (AOMEntry)servantToEntry.get( value ) ;
        return getKey( entry ) ;
    }

    protected void putEntry( Key key, AOMEntry value )
    {
        keyToEntry.put( key, value ) ;
    }

    public final void putServant( Servant servant, AOMEntry value )
    {
        entryToServant.put( value, servant ) ;
        servantToEntry.put( servant, value ) ;
    }

    protected abstract void removeEntry( AOMEntry entry, Key key ) ;

    public final void remove( Key key )
    {
        AOMEntry entry = (AOMEntry)keyToEntry.remove( key ) ;
        Servant servant = (Servant)entryToServant.remove( entry ) ;
        if (servant != null)
            servantToEntry.remove( servant ) ;

        removeEntry( entry, key ) ;
    }

    public abstract boolean hasMultipleIDs(AOMEntry value) ;

    protected  void clear()
    {
        keyToEntry.clear();
    }

    public final Set keySet()
    {
        return keyToEntry.keySet() ;
    }
}

class SingleObjectMap extends com.sun.corba.se.impl.oa.poa.ActiveObjectMap
{
    private Map entryToKey = new HashMap() ;    // Map< AOMEntry, Key >

    public SingleObjectMap( POAImpl poa )
    {
        super( poa ) ;
    }

    public  Key getKey(AOMEntry value) throws WrongPolicy
    {
        return (Key)entryToKey.get( value ) ;
    }

    protected void putEntry(Key key, AOMEntry value)
    {
        super.putEntry( key, value);

        entryToKey.put( value, key ) ;
    }

    public  boolean hasMultipleIDs(AOMEntry value)
    {
        return false;
    }

    // This case does not need the key.
    protected void removeEntry(AOMEntry entry, Key key)
    {
        entryToKey.remove( entry ) ;
    }

    public  void clear()
    {
        super.clear() ;
        entryToKey.clear() ;
    }
}

class MultipleObjectMap extends com.sun.corba.se.impl.oa.poa.ActiveObjectMap
{
    private Map entryToKeys = new HashMap() ;   // Map< AOMEntry, Set< Key > >

    public MultipleObjectMap( POAImpl poa )
    {
        super( poa ) ;
    }

    public  Key getKey(AOMEntry value) throws WrongPolicy
    {
        throw new WrongPolicy() ;
    }

    protected void putEntry(Key key, AOMEntry value)
    {
        super.putEntry( key, value);

        Set set = (Set)entryToKeys.get( value ) ;
        if (set == null) {
            set = new HashSet() ;
            entryToKeys.put( value, set ) ;
        }
        set.add( key ) ;
    }

    public  boolean hasMultipleIDs(AOMEntry value)
    {
        Set set = (Set)entryToKeys.get( value ) ;
        if (set == null)
            return false ;
        return set.size() > 1 ;
    }

    protected void removeEntry(AOMEntry entry, Key key)
    {
        Set keys = (Set)entryToKeys.get( entry ) ;
        if (keys != null) {
            keys.remove( key ) ;
            if (keys.isEmpty())
                entryToKeys.remove( entry ) ;
        }
    }

    public  void clear()
    {
        super.clear() ;
        entryToKeys.clear() ;
    }
}
