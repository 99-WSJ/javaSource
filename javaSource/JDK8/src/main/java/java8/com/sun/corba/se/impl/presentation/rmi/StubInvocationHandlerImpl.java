/*
 * Copyright (c) 2003, 2006, Oracle and/or its affiliates. All rights reserved.
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

package java8.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.impl.presentation.rmi.DynamicAccessPermission;
import com.sun.corba.se.pept.transport.ContactInfoList;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orbutil.proxy.LinkedInvocationHandler;
import com.sun.corba.se.spi.presentation.rmi.DynamicMethodMarshaller;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import com.sun.corba.se.spi.protocol.CorbaClientDelegate;
import com.sun.corba.se.spi.protocol.LocalClientRequestDispatcher;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.CORBA.portable.ServantObject;

import javax.rmi.CORBA.Util;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.PrivilegedAction;

public final class StubInvocationHandlerImpl implements LinkedInvocationHandler
{
    private transient PresentationManager.ClassData classData ;
    private transient PresentationManager pm ;
    private transient org.omg.CORBA.Object stub ;
    private transient Proxy self ;

    public void setProxy( Proxy self )
    {
        this.self = self ;
    }

    public Proxy getProxy()
    {
        return self ;
    }

    public StubInvocationHandlerImpl( PresentationManager pm,
        PresentationManager.ClassData classData, org.omg.CORBA.Object stub )
    {
        SecurityManager s = System.getSecurityManager();
        if (s != null) {
            s.checkPermission(new DynamicAccessPermission("access"));
        }
        this.classData = classData ;
        this.pm = pm ;
        this.stub = stub ;
    }

    private boolean isLocal()
    {
        boolean result = false ;
        Delegate delegate = StubAdapter.getDelegate( stub ) ;

        if (delegate instanceof CorbaClientDelegate) {
            CorbaClientDelegate cdel = (CorbaClientDelegate)delegate ;
            ContactInfoList cil = cdel.getContactInfoList() ;
            if (cil instanceof CorbaContactInfoList) {
                CorbaContactInfoList ccil = (CorbaContactInfoList)cil ;
                LocalClientRequestDispatcher lcrd =
                    ccil.getLocalClientRequestDispatcher() ;
                result = lcrd.useLocalInvocation( null ) ;
            }
        }

        return result ;
    }

    /** Invoke the given method with the args and return the result.
     *  This may result in a remote invocation.
     *  @param proxy The proxy used for this class (null if not using java.lang.reflect.Proxy)
     */
    public Object invoke( Object proxy, final Method method,
        Object[] args ) throws Throwable
    {
        String giopMethodName = classData.getIDLNameTranslator().
            getIDLName( method )  ;
        DynamicMethodMarshaller dmm =
            pm.getDynamicMethodMarshaller( method ) ;

        Delegate delegate = null ;
        try {
            delegate = StubAdapter.getDelegate( stub ) ;
        } catch (SystemException ex) {
            throw Util.mapSystemException(ex) ;
        }

        if (!isLocal()) {
            try {
                org.omg.CORBA_2_3.portable.InputStream in = null ;
                try {
                    // create request
                    org.omg.CORBA_2_3.portable.OutputStream out =
                        (org.omg.CORBA_2_3.portable.OutputStream)
                        delegate.request( stub, giopMethodName, true);

                    // marshal arguments
                    dmm.writeArguments( out, args ) ;

                    // finish invocation
                    in = (org.omg.CORBA_2_3.portable.InputStream)
                        delegate.invoke( stub, out);

                    // unmarshal result
                    return dmm.readResult( in ) ;
                } catch (ApplicationException ex) {
                    throw dmm.readException( ex ) ;
                } catch (RemarshalException ex) {
                    return invoke( proxy, method, args ) ;
                } finally {
                    delegate.releaseReply( stub, in );
                }
            } catch (SystemException ex) {
                throw Util.mapSystemException(ex) ;
            }
        } else {
            // local branch
            ORB orb = (ORB)delegate.orb( stub ) ;
            ServantObject so = delegate.servant_preinvoke( stub, giopMethodName,
                method.getDeclaringClass() );
            if (so == null) {
                return invoke( stub, method, args ) ;
            }
            try {
                Object[] copies = dmm.copyArguments( args, orb ) ;

                if (!method.isAccessible()) {
                    // Make sure that we can invoke a method from a normally
                    // inaccessible package, as this reflective class must always
                    // be able to invoke a non-public method.
                    AccessController.doPrivileged(new PrivilegedAction() {
                        public Object run() {
                            method.setAccessible( true ) ;
                            return null ;
                        }
                    } ) ;
                }

                Object result = method.invoke( so.servant, copies ) ;

                return dmm.copyResult( result, orb ) ;
            } catch (InvocationTargetException ex) {
                Throwable mex = ex.getCause() ;
                // mex should never be null, as null cannot be thrown
                Throwable exCopy = (Throwable)Util.copyObject(mex,orb);
                if (dmm.isDeclaredException( exCopy ))
                    throw exCopy ;
                else
                    throw Util.wrapException(exCopy);
            } catch (Throwable thr) {
                if (thr instanceof ThreadDeath)
                    throw (ThreadDeath)thr ;

                // This is not a user thrown exception from the
                // method call, so don't copy it.  This is either
                // an error or a reflective invoke exception.
                throw Util.wrapException( thr ) ;
            } finally {
                delegate.servant_postinvoke( stub, so);
            }
        }
    }
}
