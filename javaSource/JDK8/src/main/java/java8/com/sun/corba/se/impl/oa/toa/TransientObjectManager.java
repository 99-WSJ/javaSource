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
/*
 * Licensed Materials - Property of IBM
 * RMI-IIOP v1.0
 * Copyright IBM Corp. 1998 1999  All Rights Reserved
 *
 */

package java8.sun.corba.se.impl.oa.toa;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.orb.ORB;

public final class TransientObjectManager {
    private ORB orb ;
    private int maxSize = 128;
    private com.sun.corba.se.impl.oa.toa.Element[] elementArray;
    private com.sun.corba.se.impl.oa.toa.Element freeList;

    void dprint( String msg ) {
        ORBUtility.dprint( this, msg ) ;
    }

    public TransientObjectManager( ORB orb )
    {
        this.orb = orb ;

        elementArray = new com.sun.corba.se.impl.oa.toa.Element[maxSize];
        elementArray[maxSize-1] = new com.sun.corba.se.impl.oa.toa.Element(maxSize-1,null);
        for ( int i=maxSize-2; i>=0; i-- )
            elementArray[i] = new com.sun.corba.se.impl.oa.toa.Element(i,elementArray[i+1]);
        freeList = elementArray[0];
    }

    public synchronized byte[] storeServant(Object servant, Object servantData)
    {
        if ( freeList == null )
            doubleSize();

        com.sun.corba.se.impl.oa.toa.Element elem = freeList;
        freeList = (com.sun.corba.se.impl.oa.toa.Element)freeList.servant;

        byte[] result = elem.getKey(servant, servantData);
        if (orb.transientObjectManagerDebugFlag)
            dprint( "storeServant returns key for element " + elem ) ;
        return result ;
    }

    public synchronized Object lookupServant(byte transientKey[])
    {
        int index = ORBUtility.bytesToInt(transientKey,0);
        int counter = ORBUtility.bytesToInt(transientKey,4);

        if (orb.transientObjectManagerDebugFlag)
            dprint( "lookupServant called with index=" + index + ", counter=" + counter ) ;

        if (elementArray[index].counter == counter &&
            elementArray[index].valid ) {
            if (orb.transientObjectManagerDebugFlag)
                dprint( "\tcounter is valid" ) ;
            return elementArray[index].servant;
        }

        // servant not found
        if (orb.transientObjectManagerDebugFlag)
            dprint( "\tcounter is invalid" ) ;
        return null;
    }

    public synchronized Object lookupServantData(byte transientKey[])
    {
        int index = ORBUtility.bytesToInt(transientKey,0);
        int counter = ORBUtility.bytesToInt(transientKey,4);

        if (orb.transientObjectManagerDebugFlag)
            dprint( "lookupServantData called with index=" + index + ", counter=" + counter ) ;

        if (elementArray[index].counter == counter &&
            elementArray[index].valid ) {
            if (orb.transientObjectManagerDebugFlag)
                dprint( "\tcounter is valid" ) ;
            return elementArray[index].servantData;
        }

        // servant not found
        if (orb.transientObjectManagerDebugFlag)
            dprint( "\tcounter is invalid" ) ;
        return null;
    }

    public synchronized void deleteServant(byte transientKey[])
    {
        int index = ORBUtility.bytesToInt(transientKey,0);
        if (orb.transientObjectManagerDebugFlag)
            dprint( "deleting servant at index=" + index ) ;

        elementArray[index].delete(freeList);
        freeList = elementArray[index];
    }

    public synchronized byte[] getKey(Object servant)
    {
        for ( int i=0; i<maxSize; i++ )
            if ( elementArray[i].valid &&
                 elementArray[i].servant == servant )
                return elementArray[i].toBytes();

        // if we come here Object does not exist
        return null;
    }

    private void doubleSize()
    {
        // Assume caller is synchronized

        com.sun.corba.se.impl.oa.toa.Element old[] = elementArray;
        int oldSize = maxSize;
        maxSize *= 2;
        elementArray = new com.sun.corba.se.impl.oa.toa.Element[maxSize];

        for ( int i=0; i<oldSize; i++ )
            elementArray[i] = old[i];

        elementArray[maxSize-1] = new com.sun.corba.se.impl.oa.toa.Element(maxSize-1,null);
        for ( int i=maxSize-2; i>=oldSize; i-- )
            elementArray[i] = new com.sun.corba.se.impl.oa.toa.Element(i,elementArray[i+1]);
        freeList = elementArray[oldSize];
    }
}


final class Element {
    Object servant=null;     // also stores "next pointer" in free list
    Object servantData=null;
    int index=-1;
    int counter=0;
    boolean valid=false; // valid=true if this Element contains
    // a valid servant

    Element(int i, Object next)
    {
        servant = next;
        index = i;
    }

    byte[] getKey(Object servant, Object servantData)
    {
        this.servant = servant;
        this.servantData = servantData;
        this.valid = true;

        return toBytes();
    }

    byte[] toBytes()
    {
        // Convert the index+counter into an 8-byte (big-endian) key.

        byte key[] = new byte[8];
        ORBUtility.intToBytes(index, key, 0);
        ORBUtility.intToBytes(counter, key, 4);

        return key;
    }

    void delete(com.sun.corba.se.impl.oa.toa.Element freeList)
    {
        if ( !valid )    // prevent double deletion
            return;
        counter++;
        servantData = null;
        valid = false;

        // add this to freeList
        servant = freeList;
    }

    public String toString()
    {
        return "Element[" + index + ", " + counter + "]" ;
    }
}
