/*
 * Copyright (c) 1996, 2003, Oracle and/or its affiliates. All rights reserved.
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

import org.omg.CosNaming.NameComponent;

/**
 * Class InternalBindingKey implements the necessary wrapper code
 * around the org.omg.CosNaming::NameComponent class to implement the proper
 * equals() method and the hashCode() method for use in a hash table.
 * It computes the hashCode once and stores it, and also precomputes
 * the lengths of the id and kind strings for faster comparison.
 */
public class InternalBindingKey
{
    // A key contains a name
    public NameComponent name;
    private int idLen;
    private int kindLen;
    private int hashVal;

    // Default Constructor
    public InternalBindingKey() {}

    // Normal constructor
    public InternalBindingKey(NameComponent n)
    {
        idLen = 0;
        kindLen = 0;
        setup(n);
    }

    // Setup the object
    protected void setup(NameComponent n) {
        this.name = n;
        // Precompute lengths and values since they will not change
        if( this.name.id != null ) {
            idLen = this.name.id.length();
        }
        if( this.name.kind != null ) {
            kindLen = this.name.kind.length();
        }
        hashVal = 0;
        if (idLen > 0)
            hashVal += this.name.id.hashCode();
        if (kindLen > 0)
            hashVal += this.name.kind.hashCode();
    }

    // Compare the keys by comparing name's id and kind
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (o instanceof com.sun.corba.se.impl.naming.cosnaming.InternalBindingKey) {
            com.sun.corba.se.impl.naming.cosnaming.InternalBindingKey that = (com.sun.corba.se.impl.naming.cosnaming.InternalBindingKey)o;
            // Both lengths must match
            if (this.idLen != that.idLen || this.kindLen != that.kindLen) {
                return false;
            }
            // If id is set is must be equal
            if (this.idLen > 0 && this.name.id.equals(that.name.id) == false) {
                return false;
            }
            // If kind is set it must be equal
            if (this.kindLen > 0 && this.name.kind.equals(that.name.kind) == false) {
                return false;
            }
            // Must be the same
            return true;
        } else {
            return false;
        }
    }
    // Return precomputed value
    public int hashCode() {
        return this.hashVal;
    }
}
