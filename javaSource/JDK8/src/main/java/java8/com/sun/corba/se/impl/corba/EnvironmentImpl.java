/*
 * Copyright (c) 1996, 2002, Oracle and/or its affiliates. All rights reserved.
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

package java8.sun.corba.se.impl.corba;

import org.omg.CORBA.Environment;

public class EnvironmentImpl extends Environment {

    private Exception _exc;

    public EnvironmentImpl()
    {
    }

    public Exception exception()
    {
        return _exc;
    }

    public void exception(Exception exc)
    {
        _exc = exc;
    }

    public void clear()
    {
        _exc = null;
    }

}
