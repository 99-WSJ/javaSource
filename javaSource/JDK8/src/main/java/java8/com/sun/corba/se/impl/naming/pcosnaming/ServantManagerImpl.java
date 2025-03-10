/*
 * Copyright (c) 1999, 2003, Oracle and/or its affiliates. All rights reserved.
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

package java8.sun.corba.se.impl.naming.pcosnaming;

import com.sun.corba.se.impl.naming.pcosnaming.NameService;
import com.sun.corba.se.impl.naming.pcosnaming.NamingContextImpl;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.LocalObject;
import org.omg.PortableServer.ForwardRequest;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.ServantLocator;
import org.omg.PortableServer.ServantLocatorPackage.CookieHolder;

import java.io.*;
import java.util.Hashtable;

/**
 * @author      Rohit Garg
 * @since       JDK1.2
 */

public class ServantManagerImpl extends LocalObject implements ServantLocator
{

    // computed using serialver tool

    private static final long serialVersionUID = 4028710359865748280L;
    private ORB orb;

    private NameService theNameService;

    private File logDir;

    private Hashtable contexts;

    private com.sun.corba.se.impl.naming.pcosnaming.CounterDB counterDb;

    private int counter;

    private final static String objKeyPrefix = "NC";

    ServantManagerImpl(ORB orb, File logDir, NameService aNameService)
    {
        this.logDir = logDir;
        this.orb    = orb;
        // initialize the counter database
        counterDb   = new com.sun.corba.se.impl.naming.pcosnaming.CounterDB(logDir);
        contexts    = new Hashtable();
        theNameService = aNameService;
    }


    public Servant preinvoke(byte[] oid, POA adapter, String operation,
                             CookieHolder cookie) throws ForwardRequest
    {

        String objKey = new String(oid);

        Servant servant = (Servant) contexts.get(objKey);

        if (servant == null)
        {
                 servant =  readInContext(objKey);
        }

        return servant;
    }

    public void postinvoke(byte[] oid, POA adapter, String operation,
                           Object cookie, Servant servant)
    {
        // nada
    }

    public NamingContextImpl readInContext(String objKey)
    {
        NamingContextImpl context = (NamingContextImpl) contexts.get(objKey);
        if( context != null )
        {
                // Returning Context from Cache
                return context;
        }

        File contextFile = new File(logDir, objKey);
        if (contextFile.exists()) {
            try {
                FileInputStream fis = new FileInputStream(contextFile);
                ObjectInputStream ois = new ObjectInputStream(fis);
                context = (NamingContextImpl) ois.readObject();
                context.setORB( orb );
                context.setServantManagerImpl( this );
                context.setRootNameService( theNameService );
                ois.close();
            } catch (Exception ex) {
            }
        }

        if (context != null)
        {
                contexts.put(objKey, context);
        }
        return context;
    }

    public NamingContextImpl addContext(String objKey,
                                        NamingContextImpl context)
    {
        File contextFile =  new File(logDir, objKey);

        if (contextFile.exists())
        {
            context = readInContext(objKey);
        }
        else {
            try {
                FileOutputStream fos = new FileOutputStream(contextFile);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(context);
                oos.close();
            } catch (Exception ex) {
            }
        }
        try
        {
                contexts.remove( objKey );
        }
        catch( Exception e)
        {
        }
        contexts.put(objKey, context);

        return context;
    }

    public void updateContext( String objKey,
                                   NamingContextImpl context )
    {
        File contextFile =  new File(logDir, objKey);
        if (contextFile.exists())
        {
                contextFile.delete( );
                contextFile =  new File(logDir, objKey);
        }

        try {
                FileOutputStream fos = new FileOutputStream(contextFile);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(context);
                oos.close();
            } catch (Exception ex) {
                ex.printStackTrace( );
            }
    }

    public static String getRootObjectKey()
    {
        return objKeyPrefix + com.sun.corba.se.impl.naming.pcosnaming.CounterDB.rootCounter;
    }

    public String getNewObjectKey()
    {
        return objKeyPrefix + counterDb.getNextCounter();
    }



}

class CounterDB implements Serializable
{

    CounterDB (File logDir)
    {
        counterFileName = "counter";
        counterFile = new File(logDir, counterFileName);
        if (!counterFile.exists()) {
            counter = new Integer(rootCounter);
            writeCounter();
        } else {
            readCounter();
        }
    }

    private void readCounter()
    {
        try {
            FileInputStream fis = new FileInputStream(counterFile);
            ObjectInputStream ois = new ObjectInputStream(fis);
            counter = (Integer) ois.readObject();
            ois.close();
        } catch (Exception ex) {
                                }
    }

    private void writeCounter()
    {
        try {
            counterFile.delete();
            FileOutputStream fos = new FileOutputStream(counterFile);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(counter);
            oos.flush();
            oos.close();

        } catch (Exception ex) {
        }
    }

    public synchronized int getNextCounter()
    {
        int counterVal = counter.intValue();
        counter = new Integer(++counterVal);
        writeCounter();

        return counterVal;
    }



    private Integer counter;

    private static String counterFileName = "counter";

    private transient File counterFile;

    public  final static int rootCounter = 0;
}
