/*
 * Copyright (c) 2002, Oracle and/or its affiliates. All rights reserved.
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

import com.sun.corba.se.impl.orb.ParserAction;
import com.sun.corba.se.spi.orb.Operation;

import java.util.Properties;

public abstract class ParserActionBase implements ParserAction {
    private String propertyName ;
    private boolean prefix ;
    private Operation operation ;
    private String fieldName ;

    public int hashCode()
    {
        return propertyName.hashCode() ^ operation.hashCode() ^
            fieldName.hashCode() ^ (prefix ? 0 : 1) ;
    }

    public boolean equals( Object obj )
    {
        if (obj == this)
            return true ;

        if (!(obj instanceof com.sun.corba.se.impl.orb.ParserActionBase))
            return false ;

        com.sun.corba.se.impl.orb.ParserActionBase other = (com.sun.corba.se.impl.orb.ParserActionBase)obj ;

        return propertyName.equals( other.propertyName ) &&
            prefix == other.prefix &&
            operation.equals( other.operation ) &&
            fieldName.equals( other.fieldName ) ;
    }

    public ParserActionBase( String propertyName, boolean prefix,
        Operation operation, String fieldName )
    {
        this.propertyName       = propertyName ;
        this.prefix             = prefix ;
        this.operation          = operation ;
        this.fieldName          = fieldName ;
    }

    public String getPropertyName()
    {
        return propertyName ;
    }

    public boolean isPrefix()
    {
        return prefix ;
    }

    public String getFieldName()
    {
        return fieldName ;
    }

    public abstract Object apply( Properties props ) ;

    protected Operation getOperation()
    {
        return operation ;
    }
}
