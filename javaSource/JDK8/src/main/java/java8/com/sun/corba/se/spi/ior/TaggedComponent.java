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

package java8.sun.corba.se.spi.ior;

import com.sun.corba.se.spi.ior.Identifiable;
import org.omg.CORBA.ORB;

/** Generic interface for all tagged components.  Users of the ORB may
* create implementations of this class and also corresponding factories
* of type TaggedComponentFactory.  The factories can be registered with an
* ORB instance, in which case they will be used to unmarshal IORs containing
* the registered tagged component.
*/
public interface TaggedComponent extends Identifiable
{
    org.omg.IOP.TaggedComponent getIOPComponent( ORB orb ) ;
}
