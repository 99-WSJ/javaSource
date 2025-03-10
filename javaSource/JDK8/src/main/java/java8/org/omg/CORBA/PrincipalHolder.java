/*
 * Copyright (c) 1996, 2004, Oracle and/or its affiliates. All rights reserved.
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

import org.omg.CORBA.ORB;
import org.omg.CORBA.Principal;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;


/**
 * The Holder for <tt>Principal</tt>.  For more information on
 * Holder files, see <a href="doc-files/generatedfiles.html#holder">
 * "Generated Files: Holder Files"</a>.<P>
 * A container class for values of type <code>Principal</code>
 * that is used to store "out" and "inout" parameters in IDL methods.
 * If an IDL method signature has an IDL <code>Principal</code> as an "out"
 * or "inout" parameter, the programmer must pass an instance of
 * <code>PrincipalHolder</code> as the corresponding
 * parameter in the method invocation; for "inout" parameters, the programmer
 * must also fill the "in" value to be sent to the server.
 * Before the method invocation returns, the ORB will fill in the
 * value corresponding to the "out" value returned from the server.
 * <P>
 * If <code>myPrincipalHolder</code> is an instance of <code>PrincipalHolder</code>,
 * the value stored in its <code>value</code> field can be accessed with
 * <code>myPrincipalHolder.value</code>.
 *
 * @since       JDK1.2
 * @deprecated Deprecated by CORBA 2.2.
 */
@Deprecated
public final class PrincipalHolder implements Streamable {
    /**
     * The <code>Principal</code> value held by this <code>PrincipalHolder</code>
     * object.
     */
    public Principal value;

    /**
     * Constructs a new <code>PrincipalHolder</code> object with its
     * <code>value</code> field initialized to <code>null</code>.
     */
    public PrincipalHolder() {
    }

    /**
     * Constructs a new <code>PrincipalHolder</code> object with its
     * <code>value</code> field initialized to the given
     * <code>Principal</code> object.
     * @param initial the <code>Principal</code> with which to initialize
     *                the <code>value</code> field of the newly-created
     *                <code>PrincipalHolder</code> object
     */
    public PrincipalHolder(Principal initial) {
        value = initial;
    }

    public void _read(InputStream input) {
        value = input.read_Principal();
    }

    public void _write(OutputStream output) {
        output.write_Principal(value);
    }

    public org.omg.CORBA.TypeCode _type() {
        return ORB.init().get_primitive_tc(TCKind.tk_Principal);
    }

}
