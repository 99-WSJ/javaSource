/*
 * Copyright (c) 1999, 2006, Oracle and/or its affiliates. All rights reserved.
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

package java8.javax.management;

import java.security.AccessController;

import com.sun.jmx.mbeanserver.GetPropertyAction;

import javax.management.*;
import javax.management.AttributeValueExp;
import javax.management.BadAttributeValueExpException;
import javax.management.BadBinaryOpValueExpException;
import javax.management.BadStringOperationException;
import javax.management.InvalidApplicationException;
import javax.management.ObjectName;
import javax.management.QueryEval;
import javax.management.StringValueExp;
import javax.management.ValueExp;

/**
 * This class represents the name of the Java implementation class of
 * the MBean. It is used for performing queries based on the class of
 * the MBean.
 * @serial include
 *
 * <p>The <b>serialVersionUID</b> of this class is <code>-1081892073854801359L</code>.
 *
 * @since 1.5
 */
@SuppressWarnings("serial")  // serialVersionUID is not constant
class ClassAttributeValueExp extends AttributeValueExp {

    // Serialization compatibility stuff:
    // Two serial forms are supported in this class. The selected form depends
    // on system property "jmx.serial.form":
    //  - "1.0" for JMX 1.0
    //  - any other value for JMX 1.1 and higher
    //
    // Serial version for old serial form
    private static final long oldSerialVersionUID = -2212731951078526753L;
    //
    // Serial version for new serial form
    private static final long newSerialVersionUID = -1081892073854801359L;

    private static final long serialVersionUID;
    static {
        boolean compat = false;
        try {
            GetPropertyAction act = new GetPropertyAction("jmx.serial.form");
            String form = AccessController.doPrivileged(act);
            compat = (form != null && form.equals("1.0"));
        } catch (Exception e) {
            // OK: exception means no compat with 1.0, too bad
        }
        if (compat)
            serialVersionUID = oldSerialVersionUID;
        else
            serialVersionUID = newSerialVersionUID;
    }

    /**
     * @serial The name of the attribute
     *
     * <p>The <b>serialVersionUID</b> of this class is <code>-1081892073854801359L</code>.
     */
    private String attr;

    /**
     * Basic Constructor.
     */
    public ClassAttributeValueExp() {
        /* Compatibility: we have an attr field that we must hold on to
           for serial compatibility, even though our parent has one too.  */
        super("Class");
        attr = "Class";
    }


    /**
     * Applies the ClassAttributeValueExp on an MBean. Returns the name of
     * the Java implementation class of the MBean.
     *
     * @param name The name of the MBean on which the ClassAttributeValueExp will be applied.
     *
     * @return  The ValueExp.
     *
     * @exception BadAttributeValueExpException
     * @exception InvalidApplicationException
     */
    public ValueExp apply(ObjectName name)
            throws BadStringOperationException, BadBinaryOpValueExpException,
                   BadAttributeValueExpException, InvalidApplicationException {
        // getAttribute(name);
        Object result = getValue(name);
        if  (result instanceof String) {
            return new StringValueExp((String)result);
        } else {
            throw new BadAttributeValueExpException(result);
        }
    }

    /**
     * Returns the string "Class" representing its value
     */
    public String toString()  {
        return attr;
    }


    protected Object getValue(ObjectName name) {
        try {
            // Get the class of the object
            MBeanServer server = QueryEval.getMBeanServer();
            return server.getObjectInstance(name).getClassName();
        } catch (Exception re) {
            return null;
            /* In principle the MBean does exist because otherwise we
               wouldn't be evaluating the query on it.  But it could
               potentially have disappeared in between the time we
               discovered it and the time the query is evaluated.

               Also, the exception could be a SecurityException.

               Returning null from here will cause
               BadAttributeValueExpException, which will in turn cause
               this MBean to be omitted from the query result.  */
        }
    }

}
