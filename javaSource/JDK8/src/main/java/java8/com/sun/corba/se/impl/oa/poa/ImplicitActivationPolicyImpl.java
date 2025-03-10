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

package java8.com.sun.corba.se.impl.oa.poa;

import org.omg.CORBA.LocalObject;
import org.omg.CORBA.Policy;
import org.omg.PortableServer.IMPLICIT_ACTIVATION_POLICY_ID;
import org.omg.PortableServer.ImplicitActivationPolicy;
import org.omg.PortableServer.ImplicitActivationPolicyValue;

final class ImplicitActivationPolicyImpl
    extends LocalObject implements ImplicitActivationPolicy {

    public
        ImplicitActivationPolicyImpl(ImplicitActivationPolicyValue
                                     value) {
        this.value = value;
    }

    public ImplicitActivationPolicyValue value() {
        return value;
    }

    public int policy_type()
    {
        return IMPLICIT_ACTIVATION_POLICY_ID.value ;
    }

    public Policy copy() {
        return new com.sun.corba.se.impl.oa.poa.ImplicitActivationPolicyImpl(value);
    }

    public void destroy() {
        value = null;
    }

    private ImplicitActivationPolicyValue value;

    public String toString()
    {
        return "ImplicitActivationPolicy[" +
            ((value.value() == ImplicitActivationPolicyValue._IMPLICIT_ACTIVATION) ?
                "IMPLICIT_ACTIVATION" : "NO_IMPLICIT_ACTIVATION" + "]") ;
    }
}
