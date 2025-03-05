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
package java8.sun.corba.se.spi.orb;

public class StringPair {
    private String first ;
    private String second ;

    public boolean equals( Object obj )
    {
        if (this == obj)
            return true ;

        if (!(obj instanceof com.sun.corba.se.spi.orb.StringPair))
            return false ;

        com.sun.corba.se.spi.orb.StringPair other = (com.sun.corba.se.spi.orb.StringPair)obj ;

        return (first.equals( other.first ) &&
            second.equals( other.second )) ;
    }

    public int hashCode()
    {
        return first.hashCode() ^ second.hashCode() ;
    }

    public StringPair( String first, String second )
    {
        this.first = first ;
        this.second = second ;
    }

    public String getFirst()
    {
        return first ;
    }

    public String getSecond()
    {
        return second ;
    }
}
