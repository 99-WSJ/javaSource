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

package java8.sun.corba.se.impl.ior;

import com.sun.corba.se.impl.ior.FreezableList;
import com.sun.corba.se.impl.ior.IORImpl;
import com.sun.corba.se.spi.ior.*;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

import java.util.ArrayList;
import java.util.Iterator;

public class IORTemplateListImpl extends FreezableList implements IORTemplateList
{
    /* This class must override add( int, Object ) and set( int, Object )
     * so that adding an IORTemplateList to an IORTemplateList just results
     * in a list of TaggedProfileTemplates.
     */
    public Object set( int index, Object element )
    {
        if (element instanceof IORTemplate) {
            return super.set( index, element ) ;
        } else if (element instanceof IORTemplateList) {
            Object result = remove( index ) ;
            add( index, element ) ;
            return result ;
        } else
            throw new IllegalArgumentException() ;
    }

    public void add( int index, Object element )
    {
        if (element instanceof IORTemplate) {
            super.add( index, element ) ;
        } else if (element instanceof IORTemplateList) {
            IORTemplateList tl = (IORTemplateList)element ;
            addAll( index, tl ) ;
        } else
            throw new IllegalArgumentException() ;
    }

    public IORTemplateListImpl()
    {
        super( new ArrayList() ) ;
    }

    public IORTemplateListImpl( InputStream is )
    {
        this() ;
        int size = is.read_long() ;
        for (int ctr=0; ctr<size; ctr++) {
            IORTemplate iortemp = IORFactories.makeIORTemplate( is ) ;
            add( iortemp ) ;
        }

        makeImmutable() ;
    }

    public void makeImmutable()
    {
        makeElementsImmutable() ;
        super.makeImmutable() ;
    }

    public void write( OutputStream os )
    {
        os.write_long( size() ) ;
        Iterator iter = iterator() ;
        while (iter.hasNext()) {
            IORTemplate iortemp = (IORTemplate)(iter.next()) ;
            iortemp.write( os ) ;
        }
    }

    public IOR makeIOR( ORB orb, String typeid, ObjectId oid )
    {
        return new IORImpl( orb, typeid, this, oid ) ;
    }

    public boolean isEquivalent( IORFactory other )
    {
        if (!(other instanceof IORTemplateList))
            return false ;

        IORTemplateList list = (IORTemplateList)other ;

        Iterator thisIterator = iterator() ;
        Iterator listIterator = list.iterator() ;
        while (thisIterator.hasNext() && listIterator.hasNext()) {
            IORTemplate thisTemplate = (IORTemplate)thisIterator.next() ;
            IORTemplate listTemplate = (IORTemplate)listIterator.next() ;
            if (!thisTemplate.isEquivalent( listTemplate ))
                return false ;
        }

        return thisIterator.hasNext() == listIterator.hasNext() ;
    }
}
