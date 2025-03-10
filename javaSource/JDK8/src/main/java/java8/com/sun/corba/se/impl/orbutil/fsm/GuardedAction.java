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

package java8.sun.corba.se.impl.orbutil.fsm;

import com.sun.corba.se.spi.orbutil.fsm.*;

public class GuardedAction {
    private static Guard trueGuard = new GuardBase( "true" ) {
        public Result evaluate( FSM fsm, Input in )
        {
            return Result.ENABLED ;
        }
    } ;

    private Guard guard ;
    private Action action ;
    private State nextState ;

    public GuardedAction( Action action, State nextState )
    {
        this.guard = trueGuard ;
        this.action = action ;
        this.nextState = nextState ;
    }

    public GuardedAction( Guard guard, Action action, State nextState )
    {
        this.guard = guard ;
        this.action = action ;
        this.nextState = nextState ;
    }

    public String toString()
    {
        return "GuardedAction[action=" + action + " guard=" + guard +
            " nextState=" + nextState + "]" ;
    }

    public Action getAction() { return action ; }
    public Guard getGuard() { return guard ; }
    public State getNextState() { return nextState ; }
}
