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

package java8.sun.corba.se.impl.orbutil;

import java.util.Arrays;

public abstract class ObjectWriter {
    public static com.sun.corba.se.impl.orbutil.ObjectWriter make(boolean isIndenting,
                                                                  int initialLevel, int increment )
    {
        if (isIndenting)
            return new IndentingObjectWriter( initialLevel, increment ) ;
        else
            return new SimpleObjectWriter() ;
    }

    public abstract void startObject( Object obj ) ;

    public abstract void startElement() ;

    public abstract void endElement() ;

    public abstract void endObject( String str ) ;

    public abstract void endObject() ;

    public String toString() { return result.toString() ; }

    public void append( boolean arg ) { result.append( arg ) ; }

    public void append( char arg ) { result.append( arg ) ; }

    public void append( short arg ) { result.append( arg ) ; }

    public void append( int arg ) { result.append( arg ) ; }

    public void append( long arg ) { result.append( arg ) ; }

    public void append( float arg ) { result.append( arg ) ; }

    public void append( double arg ) { result.append( arg ) ; }

    public void append( String arg ) { result.append( arg ) ; }

//=================================================================================================
// Implementation
//=================================================================================================

    protected StringBuffer result ;

    protected ObjectWriter()
    {
        result = new StringBuffer() ;
    }

    protected void appendObjectHeader( Object obj )
    {
        result.append( obj.getClass().getName() ) ;
        result.append( "<" ) ;
        result.append( System.identityHashCode( obj ) ) ;
        result.append( ">" ) ;
        Class compClass = obj.getClass().getComponentType() ;

        if (compClass != null) {
            result.append( "[" ) ;
            if (compClass == boolean.class) {
                boolean[] arr = (boolean[])obj ;
                result.append( arr.length ) ;
                result.append( "]" ) ;
            } else if (compClass == byte.class) {
                byte[] arr = (byte[])obj ;
                result.append( arr.length ) ;
                result.append( "]" ) ;
            } else if (compClass == short.class) {
                short[] arr = (short[])obj ;
                result.append( arr.length ) ;
                result.append( "]" ) ;
            } else if (compClass == int.class) {
                int[] arr = (int[])obj ;
                result.append( arr.length ) ;
                result.append( "]" ) ;
            } else if (compClass == long.class) {
                long[] arr = (long[])obj ;
                result.append( arr.length ) ;
                result.append( "]" ) ;
            } else if (compClass == char.class) {
                char[] arr = (char[])obj ;
                result.append( arr.length ) ;
                result.append( "]" ) ;
            } else if (compClass == float.class) {
                float[] arr = (float[])obj ;
                result.append( arr.length ) ;
                result.append( "]" ) ;
            } else if (compClass == double.class) {
                double[] arr = (double[])obj ;
                result.append( arr.length ) ;
                result.append( "]" ) ;
            } else { // array of object
                Object[] arr = (Object[])obj ;
                result.append( arr.length ) ;
                result.append( "]" ) ;
            }
        }

        result.append( "(" ) ;
    }

    /** Expected patterns:
    * startObject endObject( str )
    *   header( elem )\n
    * startObject ( startElement append* endElement ) * endObject
    *   header(\n
    *       append*\n *
    *   )\n
    */
    private static class IndentingObjectWriter extends com.sun.corba.se.impl.orbutil.ObjectWriter {
        private int level ;
        private int increment ;

        public IndentingObjectWriter( int initialLevel, int increment )
        {
            this.level = initialLevel ;
            this.increment = increment ;
            startLine() ;
        }

        private void startLine()
        {
            char[] fill = new char[ level * increment ] ;
            Arrays.fill( fill, ' ' ) ;
            result.append( fill ) ;
        }

        public void startObject( Object obj )
        {
            appendObjectHeader( obj ) ;
            level++ ;
        }

        public void startElement()
        {
            result.append( "\n" ) ;
            startLine() ;
        }

        public void endElement()
        {
        }

        public void endObject( String str )
        {
            level-- ;
            result.append( str ) ;
            result.append( ")" ) ;
        }

        public void endObject( )
        {
            level-- ;
            result.append( "\n" ) ;
            startLine() ;
            result.append( ")" ) ;
        }
    }

    private static class SimpleObjectWriter extends com.sun.corba.se.impl.orbutil.ObjectWriter {
        public void startObject( Object obj )
        {
            appendObjectHeader( obj ) ;
            result.append( " " ) ;
        }

        public void startElement()
        {
            result.append( " " ) ;
        }

        public void endObject( String str )
        {
            result.append( str ) ;
            result.append( ")" ) ;
        }

        public void endElement()
        {
        }

        public void endObject()
        {
            result.append( ")" ) ;
        }
    }
}
