/*
 * Copyright (c) 2002, 2010, Oracle and/or its affiliates. All rights reserved.
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
package java8.sun.corba.se.spi.orb;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.logging.CORBALogDomains;
import com.sun.corba.se.spi.orb.DataCollector;
import com.sun.corba.se.spi.orb.PropertyParser;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

// XXX This could probably be further extended by using more reflection and
// a dynamic proxy that satisfies the interfaces that are inherited by the
// more derived class.  Do we want to go that far?
public abstract class ParserImplBase {
    private ORBUtilSystemException wrapper ;

    protected abstract PropertyParser makeParser() ;

    /** Override this method if there is some needed initialization
    * that takes place after argument parsing.  It is always called
    * at the end of setFields.
    */
    protected void complete()
    {
    }

    public ParserImplBase()
    {
        // Do nothing in this case: no parsing takes place
        wrapper = ORBUtilSystemException.get(
            CORBALogDomains.ORB_LIFECYCLE ) ;
    }

    public void init( DataCollector coll )
    {
        PropertyParser parser = makeParser() ;
        coll.setParser( parser ) ;
        Properties props = coll.getProperties() ;
        Map map = parser.parse( props ) ;
        setFields( map ) ;
    }

    private Field getAnyField( String name )
    {
        Field result = null ;

        try {
            Class cls = this.getClass() ;
            result = cls.getDeclaredField( name ) ;
            while (result == null) {
                cls = cls.getSuperclass() ;
                if (cls == null)
                    break ;

                result = cls.getDeclaredField( name ) ;
            }
        } catch (Exception exc) {
            throw wrapper.fieldNotFound( exc, name ) ;
        }

        if (result == null)
            throw wrapper.fieldNotFound( name ) ;

        return result ;
    }

    protected void setFields( Map map )
    {
        Set entries = map.entrySet() ;
        Iterator iter = entries.iterator() ;
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry)(iter.next()) ;
            final String name = (String)(entry.getKey()) ;
            final Object value = entry.getValue() ;

            try {
                AccessController.doPrivileged(
                    new PrivilegedExceptionAction() {
                        public Object run() throws IllegalAccessException,
                            IllegalArgumentException
                        {
                            Field field = getAnyField( name ) ;
                            field.setAccessible( true ) ;
                            field.set( com.sun.corba.se.spi.orb.ParserImplBase.this, value ) ;
                            return null ;
                        }
                    }
                ) ;
            } catch (PrivilegedActionException exc) {
                // Since exc wraps the actual exception, use exc.getCause()
                // instead of exc.
                throw wrapper.errorSettingField( exc.getCause(), name,
                    value.toString() ) ;
            }
        }

        // Make sure that any extra initialization takes place after all the
        // fields are set from the map.
        complete() ;
    }
}
