/*
 * Copyright (c) 2002, 2004, Oracle and/or its affiliates. All rights reserved.
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

package java8.sun.corba.se.impl.naming.namingutil;

import com.sun.corba.se.impl.orbutil.ORBConstants;

/**
 *  EndpointInfo is used internally by CorbaLoc object to store the
 *  host information used in creating the Service Object reference
 *  from the -ORBInitDef and -ORBDefaultInitDef definitions.
 *
 *  @Author Hemanth
 */
public class IIOPEndpointInfo
{
    // Version information
    private int major, minor;

    // Host Name and Port Number
    private String host;
    private int port;

    IIOPEndpointInfo( ) {
        // Default IIOP Version
        major = ORBConstants.DEFAULT_INS_GIOP_MAJOR_VERSION;
        minor = ORBConstants.DEFAULT_INS_GIOP_MINOR_VERSION;
        // Default host is localhost
        host = ORBConstants.DEFAULT_INS_HOST;
        // Default INS Port
        port = ORBConstants.DEFAULT_INS_PORT;
    }

    public void setHost( String theHost ) {
        host = theHost;
    }

    public String getHost( ) {
        return host;
    }

    public void setPort( int thePort ) {
        port = thePort;
    }

    public int getPort( ) {
        return port;
    }

    public void setVersion( int theMajor, int theMinor ) {
        major = theMajor;
        minor = theMinor;
    }

    public int getMajor( ) {
        return major;
    }

    public int getMinor( ) {
        return minor;
    }

    /** Internal Debug Method.
     */
    public void dump( ) {
        System.out.println( " Major -> " + major + " Minor -> " + minor );
        System.out.println( "host -> " + host );
        System.out.println( "port -> " + port );
    }
}
