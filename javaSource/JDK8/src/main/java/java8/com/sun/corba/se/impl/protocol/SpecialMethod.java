/*
 * Copyright (c) 1998, 2003, Oracle and/or its affiliates. All rights reserved.
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

package java8.com.sun.corba.se.impl.protocol;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.logging.CORBALogDomains;
import com.sun.corba.se.spi.oa.NullServant;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class SpecialMethod {
    public abstract boolean isNonExistentMethod() ;
    public abstract String getName();
    public abstract CorbaMessageMediator invoke(Object servant,
                                                CorbaMessageMediator request,
                                                byte[] objectId,
                                                ObjectAdapter objectAdapter);

    public static final com.sun.corba.se.impl.protocol.SpecialMethod getSpecialMethod(String operation) {
        for(int i = 0; i < methods.length; i++)
            if (methods[i].getName().equals(operation))
                return methods[i];
        return null;
    }

    static com.sun.corba.se.impl.protocol.SpecialMethod[] methods = {
        new com.sun.corba.se.impl.protocol.IsA(),
        new com.sun.corba.se.impl.protocol.GetInterface(),
        new com.sun.corba.se.impl.protocol.NonExistent(),
        new com.sun.corba.se.impl.protocol.NotExistent()
    };
}

class NonExistent extends com.sun.corba.se.impl.protocol.SpecialMethod {
    public boolean isNonExistentMethod()
    {
        return true ;
    }

    public String getName() {           // _non_existent
        return "_non_existent";
    }

    public CorbaMessageMediator invoke(Object servant,
                                       CorbaMessageMediator request,
                                       byte[] objectId,
                                       ObjectAdapter objectAdapter)
    {
        boolean result = (servant == null) || (servant instanceof NullServant) ;
        CorbaMessageMediator response =
            request.getProtocolHandler().createResponse(request, null);
        ((OutputStream)response.getOutputObject()).write_boolean(result);
        return response;
    }
}

class NotExistent extends com.sun.corba.se.impl.protocol.NonExistent {
    public String getName() {           // _not_existent
        return "_not_existent";
    }
}

class IsA extends com.sun.corba.se.impl.protocol.SpecialMethod {      // _is_a
    public boolean isNonExistentMethod()
    {
        return false ;
    }

    public String getName() {
        return "_is_a";
    }
    public CorbaMessageMediator invoke(Object servant,
                                       CorbaMessageMediator request,
                                       byte[] objectId,
                                       ObjectAdapter objectAdapter)
    {
        if ((servant == null) || (servant instanceof NullServant)) {
            ORB orb = (ORB)request.getBroker() ;
            ORBUtilSystemException wrapper = ORBUtilSystemException.get( orb,
                CORBALogDomains.OA_INVOCATION ) ;

            return request.getProtocolHandler().createSystemExceptionResponse(
                request, wrapper.badSkeleton(), null);
        }

        String[] ids = objectAdapter.getInterfaces( servant, objectId );
        String clientId =
            ((InputStream)request.getInputObject()).read_string();
        boolean answer = false;
        for(int i = 0; i < ids.length; i++)
            if (ids[i].equals(clientId)) {
                answer = true;
                break;
            }

        CorbaMessageMediator response =
            request.getProtocolHandler().createResponse(request, null);
        ((OutputStream)response.getOutputObject()).write_boolean(answer);
        return response;
    }
}

class GetInterface extends com.sun.corba.se.impl.protocol.SpecialMethod {     // _get_interface
    public boolean isNonExistentMethod()
    {
        return false ;
    }

    public String getName() {
        return "_interface";
    }
    public CorbaMessageMediator invoke(Object servant,
                                       CorbaMessageMediator request,
                                       byte[] objectId,
                                       ObjectAdapter objectAdapter)
    {
        ORB orb = (ORB)request.getBroker() ;
        ORBUtilSystemException wrapper = ORBUtilSystemException.get( orb,
            CORBALogDomains.OA_INVOCATION ) ;

        if ((servant == null) || (servant instanceof NullServant)) {
            return request.getProtocolHandler().createSystemExceptionResponse(
                request, wrapper.badSkeleton(), null);
        } else {
            return request.getProtocolHandler().createSystemExceptionResponse(
                request, wrapper.getinterfaceNotImplemented(), null);
        }
    }
}

// End of file.
