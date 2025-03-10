/*
 * Copyright (c) 2002, 2010, Oracle and/or its affiliates. All rights reserved.
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

import com.sun.corba.se.impl.oa.poa.POAImpl;
import com.sun.corba.se.impl.orbutil.concurrent.CondVar;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orbutil.fsm.*;
import org.omg.PortableServer.POAPackage.ObjectAlreadyActive;

/** AOMEntry represents a Servant or potential Servant in the ActiveObjectMap.
* It may be in several states to allow for long incarnate or etherealize operations.
* The methods on this class mostly represent input symbols to the state machine
* that controls the lifecycle of the entry.  A library is used to build the state
* machine rather than the more usual state pattern so that the state machine
* transitions are explicitly visible.
*/
public class AOMEntry extends FSMImpl {
    private final Thread[] etherealizer ;   // The actual etherealize operation
                                            // for this entry.  It is
                                            // represented as a Thread because
                                            // the POA.deactivate_object never
                                            // waits for the completion.
    private final int[] counter ;           // single element holder for counter
                                            // accessed in actions
    private final CondVar wait ;            // accessed in actions

    final POAImpl poa ;

    public static final State INVALID = new StateImpl( "Invalid" ) ;
    public static final State INCARN  = new StateImpl( "Incarnating" ) {
        public void postAction( FSM fsm ) {
            com.sun.corba.se.impl.oa.poa.AOMEntry entry = (com.sun.corba.se.impl.oa.poa.AOMEntry)fsm ;
            entry.wait.broadcast() ;
        }
    };
    public static final State VALID   = new StateImpl( "Valid" ) ;
    public static final State ETHP    = new StateImpl( "EtherealizePending" ) ;
    public static final State ETH     = new StateImpl( "Etherealizing" ) {
        public void preAction( FSM fsm ) {
            com.sun.corba.se.impl.oa.poa.AOMEntry entry = (com.sun.corba.se.impl.oa.poa.AOMEntry)fsm ;
            Thread etherealizer = entry.etherealizer[0] ;
            if (etherealizer != null)
                etherealizer.start() ;
        }

        public void postAction( FSM fsm ) {
            com.sun.corba.se.impl.oa.poa.AOMEntry entry = (com.sun.corba.se.impl.oa.poa.AOMEntry)fsm ;
            entry.wait.broadcast() ;
        }
    };
    public static final State DESTROYED = new StateImpl( "Destroyed" ) ;

    static final Input START_ETH    = new InputImpl( "startEtherealize" ) ;
    static final Input ETH_DONE     = new InputImpl( "etherealizeDone" ) ;
    static final Input INC_DONE     = new InputImpl( "incarnateDone" ) ;
    static final Input INC_FAIL     = new InputImpl( "incarnateFailure" ) ;
    static final Input ACTIVATE     = new InputImpl( "activateObject" ) ;
    static final Input ENTER        = new InputImpl( "enter" ) ;
    static final Input EXIT         = new InputImpl( "exit" ) ;

    private static Action incrementAction = new ActionBase( "increment" ) {
        public void doIt( FSM fsm, Input in ) {
            com.sun.corba.se.impl.oa.poa.AOMEntry entry = (com.sun.corba.se.impl.oa.poa.AOMEntry)fsm ;
            entry.counter[0]++ ;
        }
    } ;

    private static Action decrementAction = new ActionBase( "decrement" ) {
        public void doIt( FSM fsm, Input in ) {
            com.sun.corba.se.impl.oa.poa.AOMEntry entry = (com.sun.corba.se.impl.oa.poa.AOMEntry)fsm ;
            if (entry.counter[0] > 0)
                entry.counter[0]-- ;
            else
                throw entry.poa.lifecycleWrapper().aomEntryDecZero() ;
        }
    } ;

    private static Action throwIllegalStateExceptionAction = new ActionBase(
        "throwIllegalStateException" ) {
        public void doIt( FSM fsm, Input in ) {
            throw new IllegalStateException(
                "No transitions allowed from the DESTROYED state" ) ;
        }
    } ;

    private static Action oaaAction = new ActionBase( "throwObjectAlreadyActive" ) {
         public void doIt( FSM fsm, Input in ) {
             throw new RuntimeException( new ObjectAlreadyActive() ) ;
         }
    } ;

    private static Guard waitGuard = new GuardBase( "wait" ) {
        public Result evaluate( FSM fsm, Input in ) {
            com.sun.corba.se.impl.oa.poa.AOMEntry entry = (com.sun.corba.se.impl.oa.poa.AOMEntry)fsm ;
            try {
                entry.wait.await() ;
            } catch (InterruptedException exc) {
                // XXX Log this
                // NO-OP
            }

            return Result.DEFERED ;
        }
    } ;


    private static class CounterGuard extends GuardBase {
        private int value ;

        public CounterGuard( int value )
        {
            super( "counter>" + value ) ;
            this.value = value ;
        }

        public Result evaluate( FSM fsm, Input in )
        {
            com.sun.corba.se.impl.oa.poa.AOMEntry entry = (com.sun.corba.se.impl.oa.poa.AOMEntry)fsm ;
            return Result.convert( entry.counter[0] > value ) ;
        }
    } ;

    private static GuardBase greaterZeroGuard = new CounterGuard( 0 ) ;
    private static Guard zeroGuard = new Guard.Complement( greaterZeroGuard ) ;
    private static GuardBase greaterOneGuard = new CounterGuard( 1 ) ;
    private static Guard oneGuard = new Guard.Complement( greaterOneGuard ) ;

    private static StateEngine engine ;

    static {
        engine = StateEngineFactory.create() ;

        //          State,   Input,     Guard,                  Action,             new State

        engine.add( INVALID, ENTER,                             incrementAction,    INCARN      ) ;
        engine.add( INVALID, ACTIVATE,                          null,               VALID       ) ;
        engine.setDefault( INVALID ) ;

        engine.add( INCARN,  ENTER,     waitGuard,              null,               INCARN      ) ;
        engine.add( INCARN,  EXIT,                              null,               INCARN      ) ;
        engine.add( INCARN,  START_ETH, waitGuard,              null,               INCARN      ) ;
        engine.add( INCARN,  INC_DONE,                          null,               VALID       ) ;
        engine.add( INCARN,  INC_FAIL,                          decrementAction,    INVALID     ) ;
        engine.add( INCARN,  ACTIVATE,                          oaaAction,          INCARN      ) ;

        engine.add( VALID,   ENTER,                             incrementAction,    VALID       ) ;
        engine.add( VALID,   EXIT,                              decrementAction,    VALID       ) ;
        engine.add( VALID,   START_ETH, greaterZeroGuard,       null,               ETHP        ) ;
        engine.add( VALID,   START_ETH, zeroGuard,              null,               ETH         ) ;
        engine.add( VALID,   ACTIVATE,                          oaaAction,          VALID       ) ;

        engine.add( ETHP,    ENTER,     waitGuard,              null,               ETHP        ) ;
        engine.add( ETHP,    START_ETH,                         null,               ETHP        ) ;
        engine.add( ETHP,    EXIT,      greaterOneGuard,        decrementAction,    ETHP        ) ;
        engine.add( ETHP,    EXIT,      oneGuard,               decrementAction,    ETH         ) ;
        engine.add( ETHP,    ACTIVATE,                          oaaAction,          ETHP        ) ;

        engine.add( ETH,     START_ETH,                         null,               ETH         ) ;
        engine.add( ETH,     ETH_DONE,                          null,               DESTROYED   ) ;
        engine.add( ETH,     ACTIVATE,                          oaaAction,          ETH         ) ;
        engine.add( ETH,     ENTER,     waitGuard,              null,               ETH         ) ;

        engine.setDefault( DESTROYED, throwIllegalStateExceptionAction, DESTROYED ) ;

        engine.done() ;
    }

    public AOMEntry( POAImpl poa )
    {
        super( engine, INVALID, ((ORB)poa.getORB()).poaFSMDebugFlag ) ;
        this.poa = poa ;
        etherealizer = new Thread[1] ;
        etherealizer[0] = null ;
        counter = new int[1] ;
        counter[0] = 0 ;
        wait = new CondVar( poa.poaMutex,
            ((ORB)poa.getORB()).poaConcurrencyDebugFlag ) ;
    }

    // Methods that drive the FSM: the real interface to this class
    // Most just call the doIt method, but startEtherealize needs
    // the etherealizer.
    public void startEtherealize( Thread etherealizer )
    {
        this.etherealizer[0] = etherealizer ;
        doIt( START_ETH ) ;
    }

    public void etherealizeComplete() { doIt( ETH_DONE ) ; }
    public void incarnateComplete() { doIt( INC_DONE ) ; }
    public void incarnateFailure() { doIt( INC_FAIL ) ; }
    public void activateObject() throws ObjectAlreadyActive {
         try {
             doIt( ACTIVATE ) ;
         } catch (RuntimeException exc) {
             Throwable thr = exc.getCause() ;
             if (thr instanceof ObjectAlreadyActive)
                 throw (ObjectAlreadyActive)thr ;
             else
                 throw exc ;
         }
    }
    public void enter() { doIt( ENTER ) ; }
    public void exit() { doIt( EXIT ) ; }
}
