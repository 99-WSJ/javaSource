/*
 * Copyright (c) 2007, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java8.java.security.cert;

import sun.security.util.ObjectIdentifier;
import sun.security.x509.InvalidityDateExtension;

import javax.security.auth.x500.X500Principal;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.cert.CRLReason;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.Extension;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * An exception that indicates an X.509 certificate is revoked. A
 * {@code CertificateRevokedException} contains additional information
 * about the revoked certificate, such as the date on which the
 * certificate was revoked and the reason it was revoked.
 *
 * @author Sean Mullan
 * @since 1.7
 * @see CertPathValidatorException
 */
public class CertificateRevokedException extends CertificateException {

    private static final long serialVersionUID = 7839996631571608627L;

    /**
     * @serial the date on which the certificate was revoked
     */
    private Date revocationDate;
    /**
     * @serial the revocation reason
     */
    private final CRLReason reason;
    /**
     * @serial the {@code X500Principal} that represents the name of the
     * authority that signed the certificate's revocation status information
     */
    private final X500Principal authority;

    private transient Map<String, Extension> extensions;

    /**
     * Constructs a {@code CertificateRevokedException} with
     * the specified revocation date, reason code, authority name, and map
     * of extensions.
     *
     * @param revocationDate the date on which the certificate was revoked. The
     *    date is copied to protect against subsequent modification.
     * @param reason the revocation reason
     * @param extensions a map of X.509 Extensions. Each key is an OID String
     *    that maps to the corresponding Extension. The map is copied to
     *    prevent subsequent modification.
     * @param authority the {@code X500Principal} that represents the name
     *    of the authority that signed the certificate's revocation status
     *    information
     * @throws NullPointerException if {@code revocationDate},
     *    {@code reason}, {@code authority}, or
     *    {@code extensions} is {@code null}
     */
    public CertificateRevokedException(Date revocationDate, CRLReason reason,
        X500Principal authority, Map<String, Extension> extensions) {
        if (revocationDate == null || reason == null || authority == null ||
            extensions == null) {
            throw new NullPointerException();
        }
        this.revocationDate = new Date(revocationDate.getTime());
        this.reason = reason;
        this.authority = authority;
        this.extensions = new HashMap<String, Extension>(extensions);
    }

    /**
     * Returns the date on which the certificate was revoked. A new copy is
     * returned each time the method is invoked to protect against subsequent
     * modification.
     *
     * @return the revocation date
     */
    public Date getRevocationDate() {
        return (Date) revocationDate.clone();
    }

    /**
     * Returns the reason the certificate was revoked.
     *
     * @return the revocation reason
     */
    public CRLReason getRevocationReason() {
        return reason;
    }

    /**
     * Returns the name of the authority that signed the certificate's
     * revocation status information.
     *
     * @return the {@code X500Principal} that represents the name of the
     *     authority that signed the certificate's revocation status information
     */
    public X500Principal getAuthorityName() {
        return authority;
    }

    /**
     * Returns the invalidity date, as specified in the Invalidity Date
     * extension of this {@code CertificateRevokedException}. The
     * invalidity date is the date on which it is known or suspected that the
     * private key was compromised or that the certificate otherwise became
     * invalid. This implementation calls {@code getExtensions()} and
     * checks the returned map for an entry for the Invalidity Date extension
     * OID ("2.5.29.24"). If found, it returns the invalidity date in the
     * extension; otherwise null. A new Date object is returned each time the
     * method is invoked to protect against subsequent modification.
     *
     * @return the invalidity date, or {@code null} if not specified
     */
    public Date getInvalidityDate() {
        Extension ext = getExtensions().get("2.5.29.24");
        if (ext == null) {
            return null;
        } else {
            try {
                Date invalidity = InvalidityDateExtension.toImpl(ext).get("DATE");
                return new Date(invalidity.getTime());
            } catch (IOException ioe) {
                return null;
            }
        }
    }

    /**
     * Returns a map of X.509 extensions containing additional information
     * about the revoked certificate, such as the Invalidity Date
     * Extension. Each key is an OID String that maps to the corresponding
     * Extension.
     *
     * @return an unmodifiable map of X.509 extensions, or an empty map
     *    if there are no extensions
     */
    public Map<String, Extension> getExtensions() {
        return Collections.unmodifiableMap(extensions);
    }

    @Override
    public String getMessage() {
        return "Certificate has been revoked, reason: "
               + reason + ", revocation date: " + revocationDate
               + ", authority: " + authority + ", extensions: " + extensions;
    }

    /**
     * Serialize this {@code CertificateRevokedException} instance.
     *
     * @serialData the size of the extensions map (int), followed by all of
     * the extensions in the map, in no particular order. For each extension,
     * the following data is emitted: the OID String (Object), the criticality
     * flag (boolean), the length of the encoded extension value byte array
     * (int), and the encoded extension value bytes.
     */
    private void writeObject(ObjectOutputStream oos) throws IOException {
        // Write out the non-transient fields
        // (revocationDate, reason, authority)
        oos.defaultWriteObject();

        // Write out the size (number of mappings) of the extensions map
        oos.writeInt(extensions.size());

        // For each extension in the map, the following are emitted (in order):
        // the OID String (Object), the criticality flag (boolean), the length
        // of the encoded extension value byte array (int), and the encoded
        // extension value byte array. The extensions themselves are emitted
        // in no particular order.
        for (Map.Entry<String, Extension> entry : extensions.entrySet()) {
            Extension ext = entry.getValue();
            oos.writeObject(ext.getId());
            oos.writeBoolean(ext.isCritical());
            byte[] extVal = ext.getValue();
            oos.writeInt(extVal.length);
            oos.write(extVal);
        }
    }

    /**
     * Deserialize the {@code CertificateRevokedException} instance.
     */
    private void readObject(ObjectInputStream ois)
        throws IOException, ClassNotFoundException {
        // Read in the non-transient fields
        // (revocationDate, reason, authority)
        ois.defaultReadObject();

        // Defensively copy the revocation date
        revocationDate = new Date(revocationDate.getTime());

        // Read in the size (number of mappings) of the extensions map
        // and create the extensions map
        int size = ois.readInt();
        if (size == 0) {
            extensions = Collections.emptyMap();
        } else {
            extensions = new HashMap<String, Extension>(size);
        }

        // Read in the extensions and put the mappings in the extensions map
        for (int i = 0; i < size; i++) {
            String oid = (String) ois.readObject();
            boolean critical = ois.readBoolean();
            int length = ois.readInt();
            byte[] extVal = new byte[length];
            ois.readFully(extVal);
            Extension ext = sun.security.x509.Extension.newExtension
                (new ObjectIdentifier(oid), critical, extVal);
            extensions.put(oid, ext);
        }
    }
}
