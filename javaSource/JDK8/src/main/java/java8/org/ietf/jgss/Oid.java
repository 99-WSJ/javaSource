/*
 * Copyright (c) 2000, 2011, Oracle and/or its affiliates. All rights reserved.
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

package java8.org.ietf.jgss;

import org.ietf.jgss.GSSException;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

import java.io.IOException;
import java.io.InputStream;

/**
 * This class represents Universal Object Identifiers (Oids) and their
 * associated operations.<p>
 *
 * Oids are hierarchically globally-interpretable identifiers used
 * within the GSS-API framework to identify mechanisms and name formats.<p>
 *
 * The structure and encoding of Oids is defined in ISOIEC-8824 and
 * ISOIEC-8825.  For example the Oid representation of Kerberos V5
 * mechanism is "1.2.840.113554.1.2.2"<p>
 *
 * The GSSName name class contains public static Oid objects
 * representing the standard name types defined in GSS-API.
 *
 * @author Mayank Upadhyay
 * @since 1.4
 */
public class Oid {

    private ObjectIdentifier oid;
    private byte[] derEncoding;

    /**
     * Constructs an Oid object from a string representation of its
     * integer components.
     *
     * @param strOid the dot separated string representation of the oid.
     * For instance, "1.2.840.113554.1.2.2".
     * @exception GSSException may be thrown when the string is incorrectly
     *     formatted
     */
    public Oid(String strOid) throws GSSException {

        try {
            oid = new ObjectIdentifier(strOid);
            derEncoding = null;
        } catch (Exception e) {
            throw new GSSException(GSSException.FAILURE,
                          "Improperly formatted Object Identifier String - "
                          + strOid);
        }
    }

    /**
     * Creates an Oid object from its ASN.1 DER encoding.  This refers to
     * the full encoding including tag and length.  The structure and
     * encoding of Oids is defined in ISOIEC-8824 and ISOIEC-8825.  This
     * method is identical in functionality to its byte array counterpart.
     *
     * @param derOid stream containing the DER encoded oid
     * @exception GSSException may be thrown when the DER encoding does not
     *  follow the prescribed format.
     */
    public Oid(InputStream derOid) throws GSSException {
        try {
            DerValue derVal = new DerValue(derOid);
            derEncoding = derVal.toByteArray();
            oid = derVal.getOID();
        } catch (IOException e) {
            throw new GSSException(GSSException.FAILURE,
                          "Improperly formatted ASN.1 DER encoding for Oid");
        }
    }


    /**
     * Creates an Oid object from its ASN.1 DER encoding.  This refers to
     * the full encoding including tag and length.  The structure and
     * encoding of Oids is defined in ISOIEC-8824 and ISOIEC-8825.  This
     * method is identical in functionality to its InputStream conterpart.
     *
     * @param data byte array containing the DER encoded oid
     * @exception GSSException may be thrown when the DER encoding does not
     *     follow the prescribed format.
     */
    public Oid(byte [] data) throws GSSException {
        try {
            DerValue derVal = new DerValue(data);
            derEncoding = derVal.toByteArray();
            oid = derVal.getOID();
        } catch (IOException e) {
            throw new GSSException(GSSException.FAILURE,
                          "Improperly formatted ASN.1 DER encoding for Oid");
        }
    }

    /**
     * Only for calling by initializators used with declarations.
     *
     * @param strOid
     */
    static Oid getInstance(String strOid) {
        Oid retVal = null;
        try {
            retVal =  new Oid(strOid);
        } catch (GSSException e) {
            // squelch it!
        }
        return retVal;
    }

    /**
     * Returns a string representation of the oid's integer components
     * in dot separated notation.
     *
     * @return string representation in the following format: "1.2.3.4.5"
     */
    public String toString() {
        return oid.toString();
    }

    /**
     * Tests if two Oid objects represent the same Object identifier
     * value.
     *
     * @return <code>true</code> if the two Oid objects represent the same
     * value, <code>false</code> otherwise.
     * @param other the Oid object that has to be compared to this one
     */
    public boolean equals(Object other) {

        //check if both reference the same object
        if (this == other)
            return (true);

        if (other instanceof Oid)
            return this.oid.equals((Object)((Oid) other).oid);
        else if (other instanceof ObjectIdentifier)
            return this.oid.equals(other);
        else
            return false;
    }


    /**
     * Returns the full ASN.1 DER encoding for this oid object, which
     * includes the tag and length.
     *
     * @return byte array containing the DER encoding of this oid object.
     * @exception GSSException may be thrown when the oid can't be encoded
     */
    public byte[] getDER() throws GSSException {

        if (derEncoding == null) {
            DerOutputStream dout = new DerOutputStream();
            try {
                dout.putOID(oid);
            } catch (IOException e) {
                throw new GSSException(GSSException.FAILURE, e.getMessage());
            }
            derEncoding = dout.toByteArray();
        }

        return derEncoding.clone();
    }

    /**
     * A utility method to test if this Oid value is contained within the
     * supplied Oid array.
     *
     * @param oids the array of Oid's to search
     * @return true if the array contains this Oid value, false otherwise
     */
    public boolean containedIn(Oid[] oids) {

        for (int i = 0; i < oids.length; i++) {
            if (oids[i].equals(this))
                return (true);
        }

        return (false);
    }


    /**
     * Returns a hashcode value for this Oid.
     *
     * @return a hashCode value
     */
    public int hashCode() {
        return oid.hashCode();
    }
}
