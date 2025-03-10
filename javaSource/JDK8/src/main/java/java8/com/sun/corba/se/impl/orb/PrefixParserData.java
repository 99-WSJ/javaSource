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

package java8.sun.corba.se.impl.orb;

import com.sun.corba.se.impl.orb.ParserDataBase;
import com.sun.corba.se.spi.orb.Operation;
import com.sun.corba.se.spi.orb.PropertyParser;
import com.sun.corba.se.spi.orb.StringPair;

import java.util.Properties;

public class PrefixParserData extends ParserDataBase {

    private StringPair[] testData ;
    private Class componentType ;

    public PrefixParserData( String  propertyName,
        Operation operation, String fieldName, Object defaultValue,
        Object testValue, StringPair[] testData, Class componentType )
    {
        super( propertyName, operation, fieldName, defaultValue, testValue ) ;
        this.testData = testData ;
        this.componentType = componentType ;
    }

    public void addToParser( PropertyParser parser )
    {
        parser.addPrefix( getPropertyName(), getOperation(), getFieldName(),
            componentType ) ;
    }

    public void addToProperties( Properties props )
    {
        for (int ctr=0; ctr<testData.length; ctr++) {
            StringPair sp = testData[ctr] ;

            String propName = getPropertyName() ;
            if (propName.charAt( propName.length() - 1 ) != '.')
                propName += "." ;

            props.setProperty( propName + sp.getFirst(), sp.getSecond() ) ;
        }
    }
}
