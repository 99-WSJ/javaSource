/*
 * Copyright (c) 1996, 1999, Oracle and/or its affiliates. All rights reserved.
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

package java8.org.omg.CORBA;

import org.omg.CORBA.ARG_IN;
import org.omg.CORBA.ARG_INOUT;
import org.omg.CORBA.ARG_OUT;
import org.omg.CORBA.Any;

/**
 * An object used in the DII and DSI to describe
 * arguments and return values. <code>NamedValue</code> objects
 * are also used in the <code>Context</code>
 * object routines to pass lists of property names and values.
 * <P>
 * A <code>NamedValue</code> object contains:
 * <UL>
 * <LI>a name -- If the <code>NamedValue</code> object is used to
 * describe arguments to a request, the name will be an argument
 * identifier specified in the OMG IDL interface definition
 * for the operation being described.
 * <LI>a value -- an <code>Any</code> object
 * <LI>an argument mode flag -- one of the following:
 *   <UL>
 *    <LI><code>ARG_IN.value</code>
 *    <LI><code>ARG_OUT.value</code>
 *    <LI><code>ARG_INOUT.value</code>
 *    <LI>zero -- if this <code>NamedValue</code> object represents a property
 *                in a <code>Context</code> object rather than a parameter or
 *                return value
 *   </UL>
 * </UL>
 * <P>
 * The class <code>NamedValue</code> has three methods, which
 * access its fields.  The following code fragment demonstrates
 * creating a <code>NamedValue</code> object and then accessing
 * its fields:
 * <PRE>
 *    ORB orb = ORB.init(args, null);
 *    String s = "argument_1";
 *    org.omg.CORBA.Any myAny = orb.create_any();
 *    myAny.insert_long(12345);
 *    int in = org.omg.CORBA.ARG_IN.value;

 *    org.omg.CORBA.NamedValue nv = orb.create_named_value(
 *        s, myAny, in);
 *    System.out.println("This nv name is " + nv.name());
 *    try {
 *        System.out.println("This nv value is " + nv.value().extract_long());
 *        System.out.println("This nv flag is " + nv.flags());
 *    } catch (org.omg.CORBA.BAD_OPERATION b) {
 *      System.out.println("extract failed");
 *    }
 * </PRE>
 *
 * <P>
 * If this code fragment were put into a <code>main</code> method,
 * the output would be something like the following:
 * <PRE>
 *    This nv name is argument_1
 *    This nv value is 12345
 *    This nv flag is 1
 * </PRE>
 * <P>
 * Note that the method <code>value</code> returns an <code>Any</code>
 * object. In order to access the <code>long</code> contained in the
 * <code>Any</code> object,
 * we used the method <code>extract_long</code>.
 *
 * @see Any
 * @see ARG_IN
 * @see ARG_INOUT
 * @see ARG_OUT
 *
 * @since       JDK1.2
 */

public abstract class NamedValue {

    /**
     * Retrieves the name for this <code>NamedValue</code> object.
     *
     * @return                  a <code>String</code> object representing
     *                    the name of this <code>NamedValue</code> object
     */

    public abstract String name();

    /**
     * Retrieves the value for this <code>NamedValue</code> object.
     *
     * @return                  an <code>Any</code> object containing
     *                    the value of this <code>NamedValue</code> object
     */

    public abstract Any value();

    /**
     * Retrieves the argument mode flag for this <code>NamedValue</code> object.
     *
     * @return                  an <code>int</code> representing the argument
     *                    mode for this <code>NamedValue</code> object
     */

    public abstract int flags();

}
