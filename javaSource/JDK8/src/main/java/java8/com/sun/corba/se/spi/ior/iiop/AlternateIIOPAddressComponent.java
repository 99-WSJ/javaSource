/*
 * Copyright (c) 2004, Oracle and/or its affiliates. All rights reserved.
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

package java8.sun.corba.se.spi.ior.iiop;

import com.sun.corba.se.spi.ior.TaggedComponent;
import com.sun.corba.se.spi.ior.iiop.IIOPAddress;

/**
 * @author Ken Cavanaugh
 */
public interface AlternateIIOPAddressComponent extends TaggedComponent
{
    public IIOPAddress getAddress() ;
}
