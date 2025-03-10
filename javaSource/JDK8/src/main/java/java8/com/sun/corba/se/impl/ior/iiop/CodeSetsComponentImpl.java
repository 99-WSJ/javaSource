/*
 * Copyright (c) 2000, 2004, Oracle and/or its affiliates. All rights reserved.
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

import com.sun.corba.se.impl.encoding.CodeSetComponentInfo;
import com.sun.corba.se.impl.encoding.MarshalInputStream;
import com.sun.corba.se.impl.encoding.MarshalOutputStream;
import com.sun.corba.se.spi.ior.TaggedComponentBase;
import com.sun.corba.se.spi.ior.iiop.CodeSetsComponent;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;
import org.omg.IOP.TAG_CODE_SETS;

/**
 * @author
 */
public class CodeSetsComponentImpl extends TaggedComponentBase
    implements CodeSetsComponent
{
    CodeSetComponentInfo csci ;

    public boolean equals( Object obj )
    {
        if (!(obj instanceof com.sun.corba.se.impl.ior.iiop.CodeSetsComponentImpl))
            return false ;

        com.sun.corba.se.impl.ior.iiop.CodeSetsComponentImpl other = (com.sun.corba.se.impl.ior.iiop.CodeSetsComponentImpl)obj ;

        return csci.equals( other.csci ) ;
    }

    public int hashCode()
    {
        return csci.hashCode() ;
    }

    public String toString()
    {
        return "CodeSetsComponentImpl[csci=" + csci + "]" ;
    }

    public CodeSetsComponentImpl()
    {
        // Uses our default code sets (see CodeSetComponentInfo)
        csci = new CodeSetComponentInfo() ;
    }

    public CodeSetsComponentImpl( InputStream is )
    {
        csci = new CodeSetComponentInfo() ;
        csci.read( (MarshalInputStream)is ) ;
    }

    public CodeSetsComponentImpl(com.sun.corba.se.spi.orb.ORB orb)
    {
        if (orb == null)
            csci = new CodeSetComponentInfo();
        else
            csci = orb.getORBData().getCodeSetComponentInfo();
    }

    public CodeSetComponentInfo getCodeSetComponentInfo()
    {
        return csci ;
    }

    public void writeContents(OutputStream os)
    {
        csci.write( (MarshalOutputStream)os ) ;
    }

    public int getId()
    {
        return TAG_CODE_SETS.value ; // 1 in CORBA 2.3.1 13.6.3
    }
}
