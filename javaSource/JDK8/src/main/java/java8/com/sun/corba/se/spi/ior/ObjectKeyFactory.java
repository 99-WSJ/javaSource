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

import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import org.omg.CORBA_2_3.portable.InputStream;

/** Construct ObjectKey and ObjectKeyTemplate instances from their
 * CDR-marshalled representation.
 */
public interface ObjectKeyFactory
{
    /** Create an ObjectKey from its octet sequence representation.
     */
    ObjectKey create( byte[] key ) ;

    /** Create an ObjectKeyTemplate from its representation
     * in an InputStream.
     */
    ObjectKeyTemplate createTemplate( InputStream is ) ;
}
