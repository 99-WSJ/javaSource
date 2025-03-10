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

package java8.com.sun.corba.se.impl.ior.iiop;

import com.sun.corba.se.impl.encoding.CDROutputStream;
import com.sun.corba.se.impl.ior.EncapsulationUtility;
import com.sun.corba.se.impl.ior.iiop.IIOPAddressImpl;
import com.sun.corba.se.spi.ior.*;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.ior.iiop.IIOPAddress;
import com.sun.corba.se.spi.ior.iiop.IIOPFactories;
import com.sun.corba.se.spi.ior.iiop.IIOPProfileTemplate;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;
import org.omg.IOP.TAG_INTERNET_IOP;

/**
 * @author
 * If getMinorVersion==0, this does not contain any tagged components
 */
public class IIOPProfileTemplateImpl extends TaggedProfileTemplateBase
    implements IIOPProfileTemplate
{
    private ORB orb ;
    private GIOPVersion giopVersion ;
    private IIOPAddress primary ;

    public boolean equals( Object obj )
    {
        if (!(obj instanceof com.sun.corba.se.impl.ior.iiop.IIOPProfileTemplateImpl))
            return false ;

        com.sun.corba.se.impl.ior.iiop.IIOPProfileTemplateImpl other = (com.sun.corba.se.impl.ior.iiop.IIOPProfileTemplateImpl)obj ;

        return super.equals( obj ) && giopVersion.equals( other.giopVersion ) &&
            primary.equals( other.primary ) ;
    }

    public int hashCode()
    {
        return super.hashCode() ^ giopVersion.hashCode() ^ primary.hashCode() ;
    }

    public TaggedProfile create( ObjectKeyTemplate oktemp, ObjectId id )
    {
        return IIOPFactories.makeIIOPProfile( orb, oktemp, id, this ) ;
    }

    public GIOPVersion getGIOPVersion()
    {
        return giopVersion ;
    }

    public IIOPAddress getPrimaryAddress()
    {
        return primary ;
    }

    public IIOPProfileTemplateImpl( ORB orb, GIOPVersion version, IIOPAddress primary )
    {
        this.orb = orb ;
        this.giopVersion = version ;
        this.primary = primary ;
        if (giopVersion.getMinor() == 0)
            // Adding tagged components is not allowed for IIOP 1.0,
            // so this template is complete and should be made immutable.
            makeImmutable() ;
    }

    public IIOPProfileTemplateImpl( InputStream istr )
    {
        byte major = istr.read_octet() ;
        byte minor = istr.read_octet() ;
        giopVersion = GIOPVersion.getInstance( major, minor ) ;
        primary = new IIOPAddressImpl( istr ) ;
        orb = (ORB)(istr.orb()) ;
        // Handle any tagged components (if applicable)
        if (minor > 0)
            EncapsulationUtility.readIdentifiableSequence(
                this, orb.getTaggedComponentFactoryFinder(), istr ) ;

        makeImmutable() ;
    }

    public void write( ObjectKeyTemplate okeyTemplate, ObjectId id, OutputStream os)
    {
        giopVersion.write( os ) ;
        primary.write( os ) ;

        // Note that this is NOT an encapsulation: do not marshal
        // the endianness flag.  However, the length is required.
        // Note that this cannot be accomplished with a codec!

        // Use the byte order of the given stream
        OutputStream encapsulatedOS =
            sun.corba.OutputStreamFactory.newEncapsOutputStream(
                (ORB)os.orb(), ((CDROutputStream)os).isLittleEndian() ) ;

        okeyTemplate.write( id, encapsulatedOS ) ;
        EncapsulationUtility.writeOutputStream( encapsulatedOS, os ) ;

        if (giopVersion.getMinor() > 0)
            EncapsulationUtility.writeIdentifiableSequence( this, os ) ;
    }

    /** Write out this IIOPProfileTemplateImpl only.
    */
    public void writeContents( OutputStream os)
    {
        giopVersion.write( os ) ;
        primary.write( os ) ;

        if (giopVersion.getMinor() > 0)
            EncapsulationUtility.writeIdentifiableSequence( this, os ) ;
    }

    public int getId()
    {
        return TAG_INTERNET_IOP.value ;
    }

    public boolean isEquivalent( TaggedProfileTemplate temp )
    {
        if (!(temp instanceof com.sun.corba.se.impl.ior.iiop.IIOPProfileTemplateImpl))
            return false ;

        com.sun.corba.se.impl.ior.iiop.IIOPProfileTemplateImpl tempimp = (com.sun.corba.se.impl.ior.iiop.IIOPProfileTemplateImpl)temp ;

        return primary.equals( tempimp.primary )  ;
    }

}
