/*
 * Copyright (c) 2000, 2012, Oracle and/or its affiliates. All rights reserved.
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

package java8.com.sun.jmx.snmp.agent;


// java imports
//

import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpValue;
import com.sun.jmx.snmp.SnmpVarBind;
import com.sun.jmx.snmp.agent.SnmpGenericMetaServer;
import com.sun.jmx.snmp.agent.SnmpMibSubRequest;
import com.sun.jmx.snmp.agent.SnmpMibTable;

import javax.management.*;
import java.util.Enumeration;
import java.util.Iterator;


/**
 * <p>
 * This class is a utility class that transforms SNMP GET / SET requests
 * into standard JMX getAttributes() setAttributes() requests.
 * </p>
 *
 * <p>
 * The transformation relies on the metadata information provided by the
 * {@link SnmpGenericMetaServer} object which is
 * passed as the first parameter to every method. This SnmpGenericMetaServer
 * object is usually a Metadata object generated by <code>mibgen</code>.
 * </p>
 *
 * <p><b><i>
 * This class is used internally by mibgen generated metadata objects and
 * you should never need to use it directly.
 * </b></i></p>
 * <p><b>This API is a Sun Microsystems internal API  and is subject
 * to change without notice.</b></p>
 **/

public class SnmpGenericObjectServer {

    // ----------------------------------------------------------------------
    //
    //    Protected variables
    //
    // ----------------------------------------------------------------------

    /**
     * The MBean server through which the MBeans will be accessed.
     **/
    protected final MBeanServer server;

    // ----------------------------------------------------------------------
    //
    // Constructors
    //
    // ----------------------------------------------------------------------

    /**
     * Builds a new SnmpGenericObjectServer. Usually there will be a single
     * object of this type per MIB.
     *
     * @param server The MBeanServer in which the MBean accessed by this
     *               MIB are registered.
     **/
    public SnmpGenericObjectServer(MBeanServer server) {
        this.server = server;
    }

    /**
     * Execute an SNMP GET request.
     *
     * <p>
     * This method first builds the list of attributes that need to be
     * retrieved from the MBean and then calls getAttributes() on the
     * MBean server. Then it updates the SnmpMibSubRequest with the values
     * retrieved from the MBean.
     * </p>
     *
     * <p>
     * The SNMP metadata information is obtained through the given
     * <code>meta</code> object, which usually is an instance of a
     * <code>mibgen</code> generated class.
     * </p>
     *
     * <p><b><i>
     * This method is called internally by <code>mibgen</code> generated
     * objects and you should never need to call it directly.
     * </i></b></p>
     *
     * @param meta  The metadata object impacted by the subrequest
     * @param name  The ObjectName of the MBean impacted by this subrequest
     * @param req   The SNMP subrequest to execute on the MBean
     * @param depth The depth of the SNMP object in the OID tree.
     *
     * @exception SnmpStatusException whenever an SNMP exception must be
     *      raised. Raising an exception will abort the request.<br>
     *      Exceptions should never be raised directly, but only by means of
     * <code>
     * req.registerGetException(<i>VariableId</i>,<i>SnmpStatusException</i>)
     * </code>
     **/
    public void get(SnmpGenericMetaServer meta, ObjectName name,
                    SnmpMibSubRequest req, int depth)
        throws SnmpStatusException {

        // java.lang.System.out.println(">>>>>>>>> GET " + name);

        final int           size     = req.getSize();
        final Object        data     = req.getUserData();
        final String[]      nameList = new String[size];
        final SnmpVarBind[] varList  = new SnmpVarBind[size];
        final long[]        idList   = new long[size];
        int   i = 0;

        for (Enumeration<SnmpVarBind> e=req.getElements(); e.hasMoreElements();) {
            final SnmpVarBind var= e.nextElement();
            try {
                final long id = var.oid.getOidArc(depth);
                nameList[i]   = meta.getAttributeName(id);
                varList[i]    = var;
                idList[i]     = id;

                // Check the access rights according to the MIB.
                // The MBean might be less restrictive (have a getter
                // while the MIB defines the variable as AFN)
                //
                meta.checkGetAccess(id,data);

                //java.lang.System.out.println(nameList[i] + " added.");
                i++;
            } catch(SnmpStatusException x) {
                //java.lang.System.out.println("exception for " + nameList[i]);
                //x.printStackTrace();
                req.registerGetException(var,x);
            }
        }

        AttributeList result = null;
        int errorCode = SnmpStatusException.noSuchInstance;

        try {
            result = server.getAttributes(name,nameList);
        } catch (InstanceNotFoundException f) {
            //java.lang.System.out.println(name + ": instance not found.");
            //f.printStackTrace();
            result = new AttributeList();
        } catch (ReflectionException r) {
            //java.lang.System.out.println(name + ": reflexion error.");
            //r.printStackTrace();
            result = new AttributeList();
        } catch (Exception x) {
            result = new AttributeList();
        }


        final Iterator<?> it = result.iterator();

        for (int j=0; j < i; j++) {
            if (!it.hasNext()) {
                //java.lang.System.out.println(name + "variable[" + j +
                //                           "] absent");
                final SnmpStatusException x =
                    new SnmpStatusException(errorCode);
                req.registerGetException(varList[j],x);
                continue;
            }

            final Attribute att = (Attribute) it.next();

            while ((j < i) && (! nameList[j].equals(att.getName()))) {
                //java.lang.System.out.println(name + "variable[" +j +
                //                           "] not found");
                final SnmpStatusException x =
                    new SnmpStatusException(errorCode);
                req.registerGetException(varList[j],x);
                j++;
            }

            if ( j == i) break;

            try {
                varList[j].value =
                    meta.buildSnmpValue(idList[j],att.getValue());
            } catch (SnmpStatusException x) {
                req.registerGetException(varList[j],x);
            }
            //java.lang.System.out.println(att.getName() + " retrieved.");
        }
        //java.lang.System.out.println(">>>>>>>>> END GET");
    }

    /**
     * Get the value of an SNMP variable.
     *
     * <p><b><i>
     * You should never need to use this method directly.
     * </i></b></p>
     *
     * @param meta  The impacted metadata object
     * @param name  The ObjectName of the impacted MBean
     * @param id    The OID arc identifying the variable we're trying to set.
     * @param data  User contextual data allocated through the
     *        {@link com.sun.jmx.snmp.agent.SnmpUserDataFactory}
     *
     * @return The value of the variable.
     *
     * @exception SnmpStatusException whenever an SNMP exception must be
     *      raised. Raising an exception will abort the request. <br>
     *      Exceptions should never be raised directly, but only by means of
     * <code>
     * req.registerGetException(<i>VariableId</i>,<i>SnmpStatusException</i>)
     * </code>
     **/
    public SnmpValue get(SnmpGenericMetaServer meta, ObjectName name,
                         long id, Object data)
        throws SnmpStatusException {
        final String attname = meta.getAttributeName(id);
        Object result = null;

        try {
            result = server.getAttribute(name,attname);
        } catch (MBeanException m) {
            Exception t = m.getTargetException();
            if (t instanceof SnmpStatusException)
                throw (SnmpStatusException) t;
            throw new SnmpStatusException(SnmpStatusException.noSuchInstance);
        } catch (Exception e) {
            throw new SnmpStatusException(SnmpStatusException.noSuchInstance);
        }

        return meta.buildSnmpValue(id,result);
    }

    /**
     * Execute an SNMP SET request.
     *
     * <p>
     * This method first builds the list of attributes that need to be
     * set on the MBean and then calls setAttributes() on the
     * MBean server. Then it updates the SnmpMibSubRequest with the new
     * values retrieved from the MBean.
     * </p>
     *
     * <p>
     * The SNMP metadata information is obtained through the given
     * <code>meta</code> object, which usually is an instance of a
     * <code>mibgen</code> generated class.
     * </p>
     *
     * <p><b><i>
     * This method is called internally by <code>mibgen</code> generated
     * objects and you should never need to call it directly.
     * </i></b></p>
     *
     * @param meta  The metadata object impacted by the subrequest
     * @param name  The ObjectName of the MBean impacted by this subrequest
     * @param req   The SNMP subrequest to execute on the MBean
     * @param depth The depth of the SNMP object in the OID tree.
     *
     * @exception SnmpStatusException whenever an SNMP exception must be
     *      raised. Raising an exception will abort the request. <br>
     *      Exceptions should never be raised directly, but only by means of
     * <code>
     * req.registerGetException(<i>VariableId</i>,<i>SnmpStatusException</i>)
     * </code>
     **/
    public void set(SnmpGenericMetaServer meta, ObjectName name,
                    SnmpMibSubRequest req, int depth)
        throws SnmpStatusException {

        final int size               = req.getSize();
        final AttributeList attList  = new AttributeList(size);
        final String[]      nameList = new String[size];
        final SnmpVarBind[] varList  = new SnmpVarBind[size];
        final long[]        idList   = new long[size];
        int   i = 0;

        for (Enumeration<SnmpVarBind> e=req.getElements(); e.hasMoreElements();) {
            final SnmpVarBind var= e.nextElement();
            try {
                final long id = var.oid.getOidArc(depth);
                final String attname = meta.getAttributeName(id);
                final Object attvalue=
                    meta.buildAttributeValue(id,var.value);
                final Attribute att = new Attribute(attname,attvalue);
                attList.add(att);
                nameList[i]   = attname;
                varList[i]    = var;
                idList[i]     = id;
                i++;
            } catch(SnmpStatusException x) {
                req.registerSetException(var,x);
            }
        }

        AttributeList result;
        int errorCode = SnmpStatusException.noAccess;

        try {
            result = server.setAttributes(name,attList);
        } catch (InstanceNotFoundException f) {
            result = new AttributeList();
            errorCode = SnmpStatusException.snmpRspInconsistentName;
        } catch (ReflectionException r) {
            errorCode = SnmpStatusException.snmpRspInconsistentName;
            result = new AttributeList();
        } catch (Exception x) {
            result = new AttributeList();
        }

        final Iterator<?> it = result.iterator();

        for (int j=0; j < i; j++) {
            if (!it.hasNext()) {
                final SnmpStatusException x =
                    new SnmpStatusException(errorCode);
                req.registerSetException(varList[j],x);
                continue;
            }

            final Attribute att = (Attribute) it.next();

            while ((j < i) && (! nameList[j].equals(att.getName()))) {
                final SnmpStatusException x =
                    new SnmpStatusException(SnmpStatusException.noAccess);
                req.registerSetException(varList[j],x);
                j++;
            }

            if ( j == i) break;

            try {
                varList[j].value =
                    meta.buildSnmpValue(idList[j],att.getValue());
            } catch (SnmpStatusException x) {
                req.registerSetException(varList[j],x);
            }

        }
    }

    /**
     * Set the value of an SNMP variable.
     *
     * <p><b><i>
     * You should never need to use this method directly.
     * </i></b></p>
     *
     * @param meta  The impacted metadata object
     * @param name  The ObjectName of the impacted MBean
     * @param x     The new requested SnmpValue
     * @param id    The OID arc identifying the variable we're trying to set.
     * @param data  User contextual data allocated through the
     *        {@link com.sun.jmx.snmp.agent.SnmpUserDataFactory}
     *
     * @return The new value of the variable after the operation.
     *
     * @exception SnmpStatusException whenever an SNMP exception must be
     *      raised. Raising an exception will abort the request. <br>
     *      Exceptions should never be raised directly, but only by means of
     * <code>
     * req.registerSetException(<i>VariableId</i>,<i>SnmpStatusException</i>)
     * </code>
     **/
    public SnmpValue set(SnmpGenericMetaServer meta, ObjectName name,
                         SnmpValue x, long id, Object data)
        throws SnmpStatusException {
        final String attname = meta.getAttributeName(id);
        final Object attvalue=
            meta.buildAttributeValue(id,x);
        final Attribute att = new Attribute(attname,attvalue);

        Object result = null;

        try {
            server.setAttribute(name,att);
            result = server.getAttribute(name,attname);
        } catch(InvalidAttributeValueException iv) {
            throw new
                SnmpStatusException(SnmpStatusException.snmpRspWrongValue);
        } catch (InstanceNotFoundException f) {
            throw new
                SnmpStatusException(SnmpStatusException.snmpRspInconsistentName);
        } catch (ReflectionException r) {
            throw new
                SnmpStatusException(SnmpStatusException.snmpRspInconsistentName);
        } catch (MBeanException m) {
            Exception t = m.getTargetException();
            if (t instanceof SnmpStatusException)
                throw (SnmpStatusException) t;
            throw new
                SnmpStatusException(SnmpStatusException.noAccess);
        } catch (Exception e) {
            throw new
                SnmpStatusException(SnmpStatusException.noAccess);
        }

        return meta.buildSnmpValue(id,result);
    }

    /**
     * Checks whether an SNMP SET request can be successfully performed.
     *
     * <p>
     * For each variable in the subrequest, this method calls
     * checkSetAccess() on the meta object, and then tries to invoke the
     * check<i>AttributeName</i>() method on the MBean. If this method
     * is not defined then it is assumed that the SET won't fail.
     * </p>
     *
     * <p><b><i>
     * This method is called internally by <code>mibgen</code> generated
     * objects and you should never need to call it directly.
     * </i></b></p>
     *
     * @param meta  The metadata object impacted by the subrequest
     * @param name  The ObjectName of the MBean impacted by this subrequest
     * @param req   The SNMP subrequest to execute on the MBean
     * @param depth The depth of the SNMP object in the OID tree.
     *
     * @exception SnmpStatusException if the requested SET operation must
     *      be rejected. Raising an exception will abort the request. <br>
     *      Exceptions should never be raised directly, but only by means of
     * <code>
     * req.registerCheckException(<i>VariableId</i>,<i>SnmpStatusException</i>)
     * </code>
     *
     **/
    public void check(SnmpGenericMetaServer meta, ObjectName name,
                      SnmpMibSubRequest req, int depth)
        throws SnmpStatusException {

        final Object data = req.getUserData();

        for (Enumeration<SnmpVarBind> e=req.getElements(); e.hasMoreElements();) {
            final SnmpVarBind var= e.nextElement();
            try {
                final long id = var.oid.getOidArc(depth);
                // call meta.check() here, and meta.check will call check()
                check(meta,name,var.value,id,data);
            } catch(SnmpStatusException x) {
                req.registerCheckException(var,x);
            }
        }
    }

    /**
     * Checks whether a SET operation can be performed on a given SNMP
     * variable.
     *
     * @param meta  The impacted metadata object
     * @param name  The ObjectName of the impacted MBean
     * @param x     The new requested SnmpValue
     * @param id    The OID arc identifying the variable we're trying to set.
     * @param data  User contextual data allocated through the
     *        {@link com.sun.jmx.snmp.agent.SnmpUserDataFactory}
     *
     * <p>
     * This method calls checkSetAccess() on the meta object, and then
     * tries to invoke the check<i>AttributeName</i>() method on the MBean.
     * If this method is not defined then it is assumed that the SET
     * won't fail.
     * </p>
     *
     * <p><b><i>
     * This method is called internally by <code>mibgen</code> generated
     * objects and you should never need to call it directly.
     * </i></b></p>
     *
     * @exception SnmpStatusException if the requested SET operation must
     *      be rejected. Raising an exception will abort the request. <br>
     *      Exceptions should never be raised directly, but only by means of
     * <code>
     * req.registerCheckException(<i>VariableId</i>,<i>SnmpStatusException</i>)
     * </code>
     *
     **/
    // XXX xxx ZZZ zzz Maybe we should go through the MBeanInfo here?
    public void check(SnmpGenericMetaServer meta, ObjectName name,
                      SnmpValue x, long id, Object data)
        throws SnmpStatusException {

        meta.checkSetAccess(x,id,data);
        try {
            final String attname = meta.getAttributeName(id);
            final Object attvalue= meta.buildAttributeValue(id,x);
            final  Object[] params = new Object[1];
            final  String[] signature = new String[1];

            params[0]    = attvalue;
            signature[0] = attvalue.getClass().getName();
            server.invoke(name,"check"+attname,params,signature);

        } catch( SnmpStatusException e) {
            throw e;
        }
        catch (InstanceNotFoundException i) {
            throw new
                SnmpStatusException(SnmpStatusException.snmpRspInconsistentName);
        } catch (ReflectionException r) {
            // checkXXXX() not defined => do nothing
        } catch (MBeanException m) {
            Exception t = m.getTargetException();
            if (t instanceof SnmpStatusException)
                throw (SnmpStatusException) t;
            throw new SnmpStatusException(SnmpStatusException.noAccess);
        } catch (Exception e) {
            throw new
                SnmpStatusException(SnmpStatusException.noAccess);
        }
    }

    public void registerTableEntry(SnmpMibTable meta, SnmpOid rowOid,
                                   ObjectName objname, Object entry)
        throws SnmpStatusException {
        if (objname == null)
           throw new
             SnmpStatusException(SnmpStatusException.snmpRspInconsistentName);
        try  {
            if (entry != null && !server.isRegistered(objname))
                server.registerMBean(entry, objname);
        } catch (InstanceAlreadyExistsException e) {
            throw new
              SnmpStatusException(SnmpStatusException.snmpRspInconsistentName);
        } catch (MBeanRegistrationException e) {
            throw new SnmpStatusException(SnmpStatusException.snmpRspNoAccess);
        } catch (NotCompliantMBeanException e) {
            throw new SnmpStatusException(SnmpStatusException.snmpRspGenErr);
        } catch (RuntimeOperationsException e) {
            throw new SnmpStatusException(SnmpStatusException.snmpRspGenErr);
        } catch(Exception e) {
            throw new SnmpStatusException(SnmpStatusException.snmpRspGenErr);
        }
    }

}
