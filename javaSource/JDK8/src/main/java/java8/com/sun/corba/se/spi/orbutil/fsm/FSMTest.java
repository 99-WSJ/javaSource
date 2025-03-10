/*
 * Copyright (c) 2002, 2003, Oracle and/or its affiliates. All rights reserved.
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

package java8.sun.corba.se.spi.orbutil.fsm;

import com.sun.corba.se.spi.orbutil.fsm.Action;
import com.sun.corba.se.spi.orbutil.fsm.FSM;
import com.sun.corba.se.spi.orbutil.fsm.FSMImpl;
import com.sun.corba.se.spi.orbutil.fsm.Guard;
import com.sun.corba.se.spi.orbutil.fsm.Input;
import com.sun.corba.se.spi.orbutil.fsm.InputImpl;
import com.sun.corba.se.spi.orbutil.fsm.State;
import com.sun.corba.se.spi.orbutil.fsm.StateEngine;
import com.sun.corba.se.spi.orbutil.fsm.StateEngineFactory;
import com.sun.corba.se.spi.orbutil.fsm.StateImpl;

class TestInput {
    TestInput( Input value, String msg )
    {
        this.value = value ;
        this.msg = msg ;
    }

    public String toString()
    {
        return "Input " + value + " : " + msg ;
    }

    public Input getInput()
    {
        return value ;
    }

    Input value ;
    String msg ;
}

class TestAction1 implements Action
{
    public void doIt( FSM fsm, Input in )
    {
        System.out.println( "TestAction1:" ) ;
        System.out.println( "\tlabel    = " + label ) ;
        System.out.println( "\toldState = " + oldState ) ;
        System.out.println( "\tnewState = " + newState ) ;
        if (label != in)
            throw new Error( "Unexcepted Input " + in ) ;
        if (oldState != fsm.getState())
            throw new Error( "Unexpected old State " + fsm.getState() ) ;
    }

    public TestAction1( State oldState, Input label, State newState )
    {
        this.oldState = oldState ;
        this.newState = newState ;
        this.label = label ;
    }

    private State oldState ;
    private Input label ;
    private State newState ;
}

class TestAction2 implements Action
{
    private State oldState ;
    private State newState ;

    public void doIt( FSM fsm, Input in )
    {
        System.out.println( "TestAction2:" ) ;
        System.out.println( "\toldState = " + oldState ) ;
        System.out.println( "\tnewState = " + newState ) ;
        System.out.println( "\tinput    = " + in ) ;
        if (oldState != fsm.getState())
            throw new Error( "Unexpected old State " + fsm.getState() ) ;
    }

    public TestAction2( State oldState, State newState )
    {
        this.oldState = oldState ;
        this.newState = newState ;
    }
}

class TestAction3 implements Action {
    private State oldState ;
    private Input label ;

    public void doIt( FSM fsm, Input in )
    {
        System.out.println( "TestAction1:" ) ;
        System.out.println( "\tlabel    = " + label ) ;
        System.out.println( "\toldState = " + oldState ) ;
        if (label != in)
            throw new Error( "Unexcepted Input " + in ) ;
    }

    public TestAction3( State oldState, Input label )
    {
        this.oldState = oldState ;
        this.label = label ;
    }
}

class NegateGuard implements Guard {
    Guard guard ;

    public NegateGuard( Guard guard )
    {
        this.guard = guard ;
    }

    public Result evaluate( FSM fsm, Input in )
    {
        return guard.evaluate( fsm, in ).complement() ;
    }
}

class MyFSM extends FSMImpl {
    public MyFSM( StateEngine se )
    {
        super( se, com.sun.corba.se.spi.orbutil.fsm.FSMTest.STATE1 ) ;
    }

    public int counter = 0 ;
}

public class FSMTest {
    public static final State   STATE1 = new StateImpl( "1" ) ;
    public static final State   STATE2 = new StateImpl( "2" ) ;
    public static final State   STATE3 = new StateImpl( "3" ) ;
    public static final State   STATE4 = new StateImpl( "4" ) ;

    public static final Input   INPUT1 = new InputImpl( "1" ) ;
    public static final Input   INPUT2 = new InputImpl( "2" ) ;
    public static final Input   INPUT3 = new InputImpl( "3" ) ;
    public static final Input   INPUT4 = new InputImpl( "4" ) ;

    private Guard counterGuard = new Guard() {
        public Result evaluate( FSM fsm, Input in )
        {
            com.sun.corba.se.spi.orbutil.fsm.MyFSM mfsm = (com.sun.corba.se.spi.orbutil.fsm.MyFSM) fsm ;
            return Result.convert( mfsm.counter < 3 ) ;
        }
    } ;

    private static void add1( StateEngine se, State oldState, Input in, State newState )
    {
        se.add( oldState, in, new com.sun.corba.se.spi.orbutil.fsm.TestAction1( oldState, in, newState ), newState ) ;
    }

    private static void add2( StateEngine se, State oldState, State newState )
    {
        se.setDefault( oldState, new com.sun.corba.se.spi.orbutil.fsm.TestAction2( oldState, newState ), newState ) ;
    }

    public static void main( String[] args )
    {
        com.sun.corba.se.spi.orbutil.fsm.TestAction3 ta3 = new com.sun.corba.se.spi.orbutil.fsm.TestAction3( STATE3, INPUT1 ) ;

        StateEngine se = StateEngineFactory.create() ;
        add1( se, STATE1, INPUT1, STATE1 ) ;
        add2( se, STATE1,         STATE2 ) ;

        add1( se, STATE2, INPUT1, STATE2 ) ;
        add1( se, STATE2, INPUT2, STATE2 ) ;
        add1( se, STATE2, INPUT3, STATE1 ) ;
        add1( se, STATE2, INPUT4, STATE3 ) ;

        se.add(   STATE3, INPUT1, ta3,  STATE3 ) ;
        se.add(   STATE3, INPUT1, ta3,  STATE4 ) ;
        add1( se, STATE3, INPUT2, STATE1 ) ;
        add1( se, STATE3, INPUT3, STATE2 ) ;
        add1( se, STATE3, INPUT4, STATE2 ) ;

        com.sun.corba.se.spi.orbutil.fsm.MyFSM fsm = new com.sun.corba.se.spi.orbutil.fsm.MyFSM( se ) ;
        com.sun.corba.se.spi.orbutil.fsm.TestInput in11 = new com.sun.corba.se.spi.orbutil.fsm.TestInput( INPUT1, "1.1" ) ;
        com.sun.corba.se.spi.orbutil.fsm.TestInput in12 = new com.sun.corba.se.spi.orbutil.fsm.TestInput( INPUT1, "1.2" ) ;
        com.sun.corba.se.spi.orbutil.fsm.TestInput in21 = new com.sun.corba.se.spi.orbutil.fsm.TestInput( INPUT2, "2.1" ) ;
        com.sun.corba.se.spi.orbutil.fsm.TestInput in22 = new com.sun.corba.se.spi.orbutil.fsm.TestInput( INPUT2, "2.2" ) ;
        com.sun.corba.se.spi.orbutil.fsm.TestInput in31 = new com.sun.corba.se.spi.orbutil.fsm.TestInput( INPUT3, "3.1" ) ;
        com.sun.corba.se.spi.orbutil.fsm.TestInput in32 = new com.sun.corba.se.spi.orbutil.fsm.TestInput( INPUT3, "3.2" ) ;
        com.sun.corba.se.spi.orbutil.fsm.TestInput in33 = new com.sun.corba.se.spi.orbutil.fsm.TestInput( INPUT3, "3.3" ) ;
        com.sun.corba.se.spi.orbutil.fsm.TestInput in41 = new com.sun.corba.se.spi.orbutil.fsm.TestInput( INPUT4, "4.1" ) ;

        fsm.doIt( in11.getInput() ) ;
        fsm.doIt( in12.getInput() ) ;
        fsm.doIt( in41.getInput() ) ;
        fsm.doIt( in11.getInput() ) ;
        fsm.doIt( in22.getInput() ) ;
        fsm.doIt( in31.getInput() ) ;
        fsm.doIt( in33.getInput() ) ;
        fsm.doIt( in41.getInput() ) ;
        fsm.doIt( in41.getInput() ) ;
        fsm.doIt( in41.getInput() ) ;
        fsm.doIt( in22.getInput() ) ;
        fsm.doIt( in32.getInput() ) ;
        fsm.doIt( in41.getInput() ) ;
        fsm.doIt( in11.getInput() ) ;
        fsm.doIt( in12.getInput() ) ;
        fsm.doIt( in11.getInput() ) ;
        fsm.doIt( in11.getInput() ) ;
        fsm.doIt( in11.getInput() ) ;
        fsm.doIt( in11.getInput() ) ;
        fsm.doIt( in11.getInput() ) ;
    }
}
