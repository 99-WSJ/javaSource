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

package java8.com.sun.corba.se.impl.ior;

import com.sun.corba.se.impl.ior.ObjectReferenceProducerBase;
import com.sun.corba.se.spi.ior.*;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.StreamableValue;
import org.omg.PortableInterceptor.ObjectReferenceTemplate;
import org.omg.PortableInterceptor.ObjectReferenceTemplateHelper;

/** This is an implementation of the ObjectReferenceTemplate abstract value
* type defined by the portable interceptors IDL.
* Note that this is a direct Java implementation
* of the abstract value type: there is no stateful value type defined in IDL,
* since defining the state in IDL is awkward and inefficient.  The best way
* to define the state is to use internal data structures that can be written
* to and read from CORBA streams.
*/
public class ObjectReferenceTemplateImpl extends ObjectReferenceProducerBase
    implements ObjectReferenceTemplate, StreamableValue
{
    transient private IORTemplate iorTemplate ;

    public ObjectReferenceTemplateImpl( InputStream is )
    {
        super( (ORB)(is.orb()) ) ;
        _read( is ) ;
    }

    public ObjectReferenceTemplateImpl( ORB orb, IORTemplate iortemp )
    {
        super( orb ) ;
        iorTemplate = iortemp ;
    }

    public boolean equals( Object obj )
    {
        if (!(obj instanceof com.sun.corba.se.impl.ior.ObjectReferenceTemplateImpl))
            return false ;

        com.sun.corba.se.impl.ior.ObjectReferenceTemplateImpl other = (com.sun.corba.se.impl.ior.ObjectReferenceTemplateImpl)obj ;

        return (iorTemplate != null) &&
            iorTemplate.equals( other.iorTemplate ) ;
    }

    public int hashCode()
    {
        return iorTemplate.hashCode() ;
    }

    // Note that this repository ID must reflect the implementation
    // of the abstract valuetype (that is, this class), not the
    // repository ID of the org.omg.PortableInterceptor.ObjectReferenceTemplate
    // class.  This allows for multiple independent implementations
    // of the abstract valuetype, should that become necessary.
    public static final String repositoryId =
        "IDL:com/sun/corba/se/impl/ior/ObjectReferenceTemplateImpl:1.0" ;

    public String[] _truncatable_ids()
    {
        return new String[] { repositoryId } ;
    }

    public TypeCode _type()
    {
        return ObjectReferenceTemplateHelper.type() ;
    }

    /** Read the data into a (presumably) empty ORTImpl.  This sets the
    * orb to the ORB of the InputStream.
    */
    public void _read( InputStream is )
    {
        org.omg.CORBA_2_3.portable.InputStream istr =
            (org.omg.CORBA_2_3.portable.InputStream)is ;
        iorTemplate = IORFactories.makeIORTemplate( istr ) ;
        orb = (ORB)(istr.orb()) ;
    }

    /** Write the state to the OutputStream.
     */
    public void _write( OutputStream os )
    {
        org.omg.CORBA_2_3.portable.OutputStream ostr =
            (org.omg.CORBA_2_3.portable.OutputStream)os ;

        iorTemplate.write( ostr ) ;
    }

    public String server_id ()
    {
        int val = iorTemplate.getObjectKeyTemplate().getServerId() ;
        return Integer.toString( val ) ;
    }

    public String orb_id ()
    {
        return iorTemplate.getObjectKeyTemplate().getORBId() ;
    }

    public String[] adapter_name()
    {
        ObjectAdapterId poaid =
            iorTemplate.getObjectKeyTemplate().getObjectAdapterId() ;

        return poaid.getAdapterName() ;
    }

    public IORFactory getIORFactory()
    {
        return iorTemplate ;
    }

    public IORTemplateList getIORTemplateList()
    {
        IORTemplateList tl = IORFactories.makeIORTemplateList() ;
        tl.add( iorTemplate ) ;
        tl.makeImmutable() ;
        return tl ;
    }
}
