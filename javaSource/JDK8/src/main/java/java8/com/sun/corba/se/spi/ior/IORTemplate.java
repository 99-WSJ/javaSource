/*
 * Copyright (c) 2003, Oracle and/or its affiliates. All rights reserved.
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

import com.sun.corba.se.spi.ior.IORFactory;
import com.sun.corba.se.spi.ior.MakeImmutable;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;

import java.util.Iterator;
import java.util.List;

/** An IORTemplate provides all of the data necessary to create an IOR except
 * for the typeId and ObjectId.  It is a list of TaggedProfileTemplates.
 */
public interface IORTemplate extends List, IORFactory, MakeImmutable {
    /** Iterate over all TaggedProfileTemplates in this IORTemplate
     * with the given id.
     */
    Iterator iteratorById( int id ) ;

    ObjectKeyTemplate getObjectKeyTemplate() ;
}
