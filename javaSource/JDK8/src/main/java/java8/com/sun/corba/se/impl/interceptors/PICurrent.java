/*
 * Copyright (c) 2000, 2003, Oracle and/or its affiliates. All rights reserved.
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

package java8.sun.corba.se.impl.interceptors;

import com.sun.corba.se.impl.interceptors.SlotTable;
import com.sun.corba.se.impl.interceptors.SlotTableStack;
import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.spi.logging.CORBALogDomains;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.PortableInterceptor.Current;
import org.omg.PortableInterceptor.InvalidSlot;

/**
 * PICurrent is the implementation of Current as specified in the Portable
 * Interceptors Spec orbos/99-12-02.
 * IMPORTANT: PICurrent is implemented with the assumption that get_slot()
 * or set_slot() will not be called in ORBInitializer.pre_init() and
 * post_init().
 */
public class PICurrent extends org.omg.CORBA.LocalObject
    implements Current
{
    // slotCounter is used to keep track of ORBInitInfo.allocate_slot_id()
    private int slotCounter;

    // The ORB associated with this PICurrent object.
    private ORB myORB;

    private OMGSystemException wrapper ;

    // True if the orb is still initialzing and get_slot and set_slot are not
    // to be called.
    private boolean orbInitializing;

    // ThreadLocal contains a stack of SlotTable which are used
    // for resolve_initial_references( "PICurrent" );
    private ThreadLocal threadLocalSlotTable
        = new ThreadLocal( ) {
            protected Object initialValue( ) {
                SlotTable table = new SlotTable( myORB, slotCounter );
                return new SlotTableStack( myORB, table );
            }
        };

    /**
     * PICurrent constructor which will be called for every ORB
     * initialization.
     */
    PICurrent( ORB myORB ) {
        this.myORB = myORB;
        wrapper = OMGSystemException.get( myORB,
            CORBALogDomains.RPC_PROTOCOL ) ;
        this.orbInitializing = true;
        slotCounter = 0;
    }


    /**
     * This method will be called from ORBInitInfo.allocate_slot_id( ).
     * simply returns a slot id by incrementing slotCounter.
     */
    int allocateSlotId( ) {
        int slotId = slotCounter;
        slotCounter = slotCounter + 1;
        return slotId;
    }


    /**
     * This method gets the SlotTable which is on the top of the
     * ThreadLocalStack.
     */
    SlotTable getSlotTable( ) {
        SlotTable table = (SlotTable)
                ((SlotTableStack)threadLocalSlotTable.get()).peekSlotTable();
        return table;
    }

    /**
     * This method pushes a SlotTable on the SlotTableStack. When there is
     * a resolve_initial_references("PICurrent") after this call. The new
     * PICurrent will be returned.
     */
    void pushSlotTable( ) {
        SlotTableStack st = (SlotTableStack)threadLocalSlotTable.get();
        st.pushSlotTable( );
    }


    /**
     * This method pops a SlotTable on the SlotTableStack.
     */
    void popSlotTable( ) {
        SlotTableStack st = (SlotTableStack)threadLocalSlotTable.get();
        st.popSlotTable( );
    }

    /**
     * This method sets the slot data at the given slot id (index) in the
     * Slot Table which is on the top of the SlotTableStack.
     */
    public void set_slot( int id, Any data ) throws InvalidSlot
    {
        if( orbInitializing ) {
            // As per ptc/00-08-06 if the ORB is still initializing, disallow
            // calls to get_slot and set_slot.  If an attempt is made to call,
            // throw a BAD_INV_ORDER.
            throw wrapper.invalidPiCall3() ;
        }

        getSlotTable().set_slot( id, data );
    }

    /**
     * This method gets the slot data at the given slot id (index) from the
     * Slot Table which is on the top of the SlotTableStack.
     */
    public Any get_slot( int id ) throws InvalidSlot
    {
        if( orbInitializing ) {
            // As per ptc/00-08-06 if the ORB is still initializing, disallow
            // calls to get_slot and set_slot.  If an attempt is made to call,
            // throw a BAD_INV_ORDER.
            throw wrapper.invalidPiCall4() ;
        }

        return getSlotTable().get_slot( id );
    }

    /**
     * This method resets all the slot data to null in the
     * Slot Table which is on the top of SlotTableStack.
     */
    void resetSlotTable( ) {
        getSlotTable().resetSlots();
    }

    /**
     * Called from ORB when the ORBInitializers are about to start
     * initializing.
     */
    void setORBInitializing( boolean init ) {
        this.orbInitializing = init;
    }
}
