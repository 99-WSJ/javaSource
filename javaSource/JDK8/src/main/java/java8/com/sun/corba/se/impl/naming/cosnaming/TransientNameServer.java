/*
 * Copyright (c) 1997, 2003, Oracle and/or its affiliates. All rights reserved.
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

package java8.sun.corba.se.impl.naming.cosnaming;

import com.sun.corba.se.impl.logging.NamingSystemException;
import com.sun.corba.se.impl.naming.cosnaming.NamingUtils;
import com.sun.corba.se.impl.naming.cosnaming.TransientNameService;
import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import com.sun.corba.se.impl.orbutil.ORBConstants;
import com.sun.corba.se.spi.logging.CORBALogDomains;
import org.omg.CORBA.ORB;

import java.util.Properties;

/**
 * Class TransientNameServer is a standalone application which
 * implements a transient name service. It uses the TransientNameService
 * class for the name service implementation, and the BootstrapServer
 * for implementing bootstrapping, i.e., to get the initial NamingContext.
 * <p>
 * The BootstrapServer uses a Properties object specify the initial service
 * object references supported; such as Properties object is created containing
 * only a "NameService" entry together with the stringified object reference
 * for the initial NamingContext. The BootstrapServer's listening port
 * is set by first checking the supplied arguments to the name server
 * (-ORBInitialPort), and if not set, defaults to the standard port number.
 * The BootstrapServer is created supplying the Properties object, using no
 * external File object for storage, and the derived initial port number.
 * @see TransientNameService
 * @see BootstrapServer
 */
public class TransientNameServer
{
    static private boolean debug = false ;
    static NamingSystemException wrapper = NamingSystemException.get(
        CORBALogDomains.NAMING ) ;

    static public void trace( String msg ) {
        if (debug)
            System.out.println( msg ) ;
    }

    static public void initDebug( String[] args ) {
        // If debug was compiled to be true for testing purposes,
        // don't change it.
        if (debug)
            return ;

        for (int ctr=0; ctr<args.length; ctr++)
            if (args[ctr].equalsIgnoreCase( "-debug" )) {
                debug = true ;
            return ;
        }
        debug = false ;
    }

    private static org.omg.CORBA.Object initializeRootNamingContext( ORB orb ) {
        org.omg.CORBA.Object rootContext = null;
        try {
            com.sun.corba.se.spi.orb.ORB coreORB =
                (com.sun.corba.se.spi.orb.ORB)orb ;

            TransientNameService tns = new TransientNameService(coreORB );
            return tns.initialNamingContext();
        } catch (org.omg.CORBA.SystemException e) {
            throw wrapper.transNsCannotCreateInitialNcSys( e ) ;
        } catch (Exception e) {
            throw wrapper.transNsCannotCreateInitialNc( e ) ;
        }
    }

    /**
     * Main startup routine. It instantiates a TransientNameService
     * object and a BootstrapServer object, and then allows invocations to
     * happen.
     * @param args an array of strings representing the startup arguments.
     */
    public static void main(String args[]) {
        initDebug( args ) ;

        boolean invalidHostOption = false;
        boolean orbInitialPort0 = false;

        // Determine the initial bootstrap port to use
        int initialPort = 0;
        try {
            trace( "Transient name server started with args " + args ) ;

            // Create an ORB object
            Properties props = System.getProperties() ;

            props.put( ORBConstants.SERVER_ID_PROPERTY, ORBConstants.NAME_SERVICE_SERVER_ID ) ;
            props.put( "org.omg.CORBA.ORBClass",
                "com.sun.corba.se.impl.orb.ORBImpl" );

            try {
                // Try environment
                String ips = System.getProperty( ORBConstants.INITIAL_PORT_PROPERTY ) ;
                if (ips != null && ips.length() > 0 ) {
                    initialPort = Integer.parseInt(ips);
                    // -Dorg.omg.CORBA.ORBInitialPort=0 is invalid
                    if( initialPort == 0 ) {
                        orbInitialPort0 = true;
                        throw wrapper.transientNameServerBadPort() ;
                    }
                }
                String hostName =
                    System.getProperty( ORBConstants.INITIAL_HOST_PROPERTY ) ;
                if( hostName != null ) {
                    invalidHostOption = true;
                    throw wrapper.transientNameServerBadHost() ;
                }
            } catch (NumberFormatException e) {
                // do nothing
            }

            // Let arguments override
            for (int i=0;i<args.length;i++) {
                // Was the initial port specified?
                if (args[i].equals("-ORBInitialPort") &&
                    i < args.length-1) {
                    initialPort = Integer.parseInt(args[i+1]);
                    // -ORBInitialPort 0 is invalid
                    if( initialPort == 0 ) {
                        orbInitialPort0 = true;
                        throw wrapper.transientNameServerBadPort() ;
                    }
                }
                if (args[i].equals("-ORBInitialHost" ) ) {
                    invalidHostOption = true;
                    throw wrapper.transientNameServerBadHost() ;
                }
            }

            // If initialPort is not set, then we need to set the Default
            // Initial Port Property for the ORB
            if( initialPort == 0 ) {
                initialPort = ORBConstants.DEFAULT_INITIAL_PORT;
                props.put( ORBConstants.INITIAL_PORT_PROPERTY,
                    Integer.toString(initialPort) );
            }

            // Set -ORBInitialPort = Persistent Server Port so that ORBImpl
            // will start Boot Strap.
            props.put( ORBConstants.PERSISTENT_SERVER_PORT_PROPERTY,
               Integer.toString(initialPort) );

            ORB corb = ORB.init( args, props ) ;
            trace( "ORB object returned from init: " + corb ) ;

            org.omg.CORBA.Object ns = initializeRootNamingContext( corb ) ;
            ((com.sun.corba.se.org.omg.CORBA.ORB)corb).register_initial_reference(
                "NamingService", ns ) ;

            String stringifiedIOR = null;

            if( ns != null ) {
                stringifiedIOR = corb.object_to_string(ns) ;
            } else {
                 NamingUtils.errprint(CorbaResourceUtil.getText(
                     "tnameserv.exception", initialPort));
                 NamingUtils.errprint(CorbaResourceUtil.getText(
                     "tnameserv.usage"));
                System.exit( 1 );
            }

            trace( "name service created" ) ;

            // This is used for handshaking by the IBM test framework!
            // Do not modify, unless another synchronization protocol is
            // used to replace this hack!

            System.out.println(CorbaResourceUtil.getText(
                "tnameserv.hs1", stringifiedIOR));
            System.out.println(CorbaResourceUtil.getText(
                "tnameserv.hs2", initialPort));
            System.out.println(CorbaResourceUtil.getText("tnameserv.hs3"));

            // Serve objects.
            Object sync = new Object();
            synchronized (sync) {sync.wait();}
        } catch (Exception e) {
            if( invalidHostOption ) {
                // Let the User Know that -ORBInitialHost is not valid for
                // tnameserver
                NamingUtils.errprint( CorbaResourceUtil.getText(
                    "tnameserv.invalidhostoption" ) );
            } else if( orbInitialPort0 ) {
                // Let the User Know that -ORBInitialPort 0 is not valid for
                // tnameserver
                NamingUtils.errprint( CorbaResourceUtil.getText(
                    "tnameserv.orbinitialport0" ));
            } else {
                NamingUtils.errprint(CorbaResourceUtil.getText(
                    "tnameserv.exception", initialPort));
                NamingUtils.errprint(CorbaResourceUtil.getText(
                    "tnameserv.usage"));
            }

            e.printStackTrace() ;
        }
    }

    /**
     * Private constructor since no object of this type should be instantiated.
     */
    private TransientNameServer() {}
}
