/*
 * Copyright (c) 2000, 2013, Oracle and/or its affiliates. All rights reserved.
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

import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import com.sun.corba.se.spi.ior.IdentifiableBase;
import com.sun.corba.se.spi.ior.TaggedComponent;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA_2_3.portable.InputStream;


/** Base class to use for implementing TaggedComponents.  It implements
 * the getIOPComponent method using the TaggedComponent.write() method.
 * @author Ken Cavanaugh
 */
public abstract class TaggedComponentBase extends IdentifiableBase
    implements TaggedComponent
{
    public org.omg.IOP.TaggedComponent getIOPComponent(
        org.omg.CORBA.ORB orb )
    {
        EncapsOutputStream os =
            sun.corba.OutputStreamFactory.newEncapsOutputStream((ORB)orb);
        write( os ) ;
        InputStream is = (InputStream)(os.create_input_stream() ) ;
        return org.omg.IOP.TaggedComponentHelper.read( is ) ;
    }
}
