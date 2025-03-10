/*
 * Copyright (c) 2000, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java8.javax.security.auth.kerberos;

import java.io.*;
import java.util.Date;
import java.util.Arrays;
import java.net.InetAddress;
import javax.crypto.SecretKey;
import javax.security.auth.Refreshable;
import javax.security.auth.Destroyable;
import javax.security.auth.RefreshFailedException;
import javax.security.auth.DestroyFailedException;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.kerberos.KeyImpl;

import sun.misc.HexDumpEncoder;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.Asn1Exception;
import sun.security.util.*;

/**
 * This class encapsulates a Kerberos ticket and associated
 * information as viewed from the client's point of view. It captures all
 * information that the Key Distribution Center (KDC) sends to the client
 * in the reply message KDC-REP defined in the Kerberos Protocol
 * Specification (<a href=http://www.ietf.org/rfc/rfc4120.txt>RFC 4120</a>).
 * <p>
 * All Kerberos JAAS login modules that authenticate a user to a KDC should
 * use this class. Where available, the login module might even read this
 * information from a ticket cache in the operating system instead of
 * directly communicating with the KDC. During the commit phase of the JAAS
 * authentication process, the JAAS login module should instantiate this
 * class and store the instance in the private credential set of a
 * {@link javax.security.auth.Subject Subject}.<p>
 *
 * It might be necessary for the application to be granted a
 * {@link javax.security.auth.PrivateCredentialPermission
 * PrivateCredentialPermission} if it needs to access a KerberosTicket
 * instance from a Subject. This permission is not needed when the
 * application depends on the default JGSS Kerberos mechanism to access the
 * KerberosTicket. In that case, however, the application will need an
 * appropriate
 * {@link javax.security.auth.kerberos.ServicePermission ServicePermission}.
 * <p>
 * Note that this class is applicable to both ticket granting tickets and
 * other regular service tickets. A ticket granting ticket is just a
 * special case of a more generalized service ticket.
 *
 * @see javax.security.auth.Subject
 * @see javax.security.auth.PrivateCredentialPermission
 * @see javax.security.auth.login.LoginContext
 * @see org.ietf.jgss.GSSCredential
 * @see org.ietf.jgss.GSSManager
 *
 * @author Mayank Upadhyay
 * @since 1.4
 */
public class KerberosTicket implements Destroyable, Refreshable,
         Serializable {

    private static final long serialVersionUID = 7395334370157380539L;

    // XXX Make these flag indices public
    private static final int FORWARDABLE_TICKET_FLAG = 1;
    private static final int FORWARDED_TICKET_FLAG   = 2;
    private static final int PROXIABLE_TICKET_FLAG   = 3;
    private static final int PROXY_TICKET_FLAG       = 4;
    private static final int POSTDATED_TICKET_FLAG   = 6;
    private static final int RENEWABLE_TICKET_FLAG   = 8;
    private static final int INITIAL_TICKET_FLAG     = 9;

    private static final int NUM_FLAGS = 32;

    /**
     *
     * ASN.1 DER Encoding of the Ticket as defined in the
     * Kerberos Protocol Specification RFC4120.
     *
     * @serial
     */

    private byte[] asn1Encoding;

    /**
     *{@code KeyImpl} is serialized by writing out the ASN1 Encoded bytes
     * of the encryption key. The ASN1 encoding is defined in RFC4120 and as
     * follows:
     * <pre>
     * EncryptionKey   ::= SEQUENCE {
     *          keytype    [0] Int32 -- actually encryption type --,
     *          keyvalue   [1] OCTET STRING
     * }
     * </pre>
     *
     * @serial
     */

    private javax.security.auth.kerberos.KeyImpl sessionKey;

    /**
     *
     * Ticket Flags as defined in the Kerberos Protocol Specification RFC4120.
     *
     * @serial
     */

    private boolean[] flags;

    /**
     *
     * Time of initial authentication
     *
     * @serial
     */

    private Date authTime;

    /**
     *
     * Time after which the ticket is valid.
     * @serial
     */
    private Date startTime;

    /**
     *
     * Time after which the ticket will not be honored. (its expiration time).
     *
     * @serial
     */

    private Date endTime;

    /**
     *
     * For renewable Tickets it indicates the maximum endtime that may be
     * included in a renewal. It can be thought of as the absolute expiration
     * time for the ticket, including all renewals. This field may be null
     * for tickets that are not renewable.
     *
     * @serial
     */

    private Date renewTill;

    /**
     *
     * Client that owns the service ticket
     *
     * @serial
     */

    private KerberosPrincipal client;

    /**
     *
     * The service for which the ticket was issued.
     *
     * @serial
     */

    private KerberosPrincipal server;

    /**
     *
     * The addresses from where the ticket may be used by the client.
     * This field may be null when the ticket is usable from any address.
     *
     * @serial
     */


    private InetAddress[] clientAddresses;

    private transient boolean destroyed = false;

    /**
     * Constructs a KerberosTicket using credentials information that a
     * client either receives from a KDC or reads from a cache.
     *
     * @param asn1Encoding the ASN.1 encoding of the ticket as defined by
     * the Kerberos protocol specification.
     * @param client the client that owns this service
     * ticket
     * @param server the service that this ticket is for
     * @param sessionKey the raw bytes for the session key that must be
     * used to encrypt the authenticator that will be sent to the server
     * @param keyType the key type for the session key as defined by the
     * Kerberos protocol specification.
     * @param flags the ticket flags. Each element in this array indicates
     * the value for the corresponding bit in the ASN.1 BitString that
     * represents the ticket flags. If the number of elements in this array
     * is less than the number of flags used by the Kerberos protocol,
     * then the missing flags will be filled in with false.
     * @param authTime the time of initial authentication for the client
     * @param startTime the time after which the ticket will be valid. This
     * may be null in which case the value of authTime is treated as the
     * startTime.
     * @param endTime the time after which the ticket will no longer be
     * valid
     * @param renewTill an absolute expiration time for the ticket,
     * including all renewal that might be possible. This field may be null
     * for tickets that are not renewable.
     * @param clientAddresses the addresses from where the ticket may be
     * used by the client. This field may be null when the ticket is usable
     * from any address.
     */
    public KerberosTicket(byte[] asn1Encoding,
                         KerberosPrincipal client,
                         KerberosPrincipal server,
                         byte[] sessionKey,
                         int keyType,
                         boolean[] flags,
                         Date authTime,
                         Date startTime,
                         Date endTime,
                         Date renewTill,
                         InetAddress[] clientAddresses) {

        init(asn1Encoding, client, server, sessionKey, keyType, flags,
            authTime, startTime, endTime, renewTill, clientAddresses);
    }

    private void init(byte[] asn1Encoding,
                         KerberosPrincipal client,
                         KerberosPrincipal server,
                         byte[] sessionKey,
                         int keyType,
                         boolean[] flags,
                         Date authTime,
                         Date startTime,
                         Date endTime,
                         Date renewTill,
                         InetAddress[] clientAddresses) {
        if (sessionKey == null)
           throw new IllegalArgumentException("Session key for ticket"
                                              + " cannot be null");
        init(asn1Encoding, client, server,
             new javax.security.auth.kerberos.KeyImpl(sessionKey, keyType), flags, authTime,
             startTime, endTime, renewTill, clientAddresses);
    }

    private void init(byte[] asn1Encoding,
                         KerberosPrincipal client,
                         KerberosPrincipal server,
                         javax.security.auth.kerberos.KeyImpl sessionKey,
                         boolean[] flags,
                         Date authTime,
                         Date startTime,
                         Date endTime,
                         Date renewTill,
                         InetAddress[] clientAddresses) {
        if (asn1Encoding == null)
           throw new IllegalArgumentException("ASN.1 encoding of ticket"
                                              + " cannot be null");
        this.asn1Encoding = asn1Encoding.clone();

        if (client == null)
           throw new IllegalArgumentException("Client name in ticket"
                                              + " cannot be null");
        this.client = client;

        if (server == null)
           throw new IllegalArgumentException("Server name in ticket"
                                              + " cannot be null");
        this.server = server;

        // Caller needs to make sure `sessionKey` will not be null
        this.sessionKey = sessionKey;

        if (flags != null) {
           if (flags.length >= NUM_FLAGS)
                this.flags = flags.clone();
           else {
                this.flags = new boolean[NUM_FLAGS];
                // Fill in whatever we have
                for (int i = 0; i < flags.length; i++)
                    this.flags[i] = flags[i];
           }
        } else
           this.flags = new boolean[NUM_FLAGS];

        if (this.flags[RENEWABLE_TICKET_FLAG]) {
           if (renewTill == null)
                throw new IllegalArgumentException("The renewable period "
                       + "end time cannot be null for renewable tickets.");

           this.renewTill = new Date(renewTill.getTime());
        }

        if (authTime != null) {
            this.authTime = new Date(authTime.getTime());
        }
        if (startTime != null) {
            this.startTime = new Date(startTime.getTime());
        } else {
            this.startTime = this.authTime;
        }

        if (endTime == null)
           throw new IllegalArgumentException("End time for ticket validity"
                                              + " cannot be null");
        this.endTime = new Date(endTime.getTime());

        if (clientAddresses != null)
           this.clientAddresses = clientAddresses.clone();
    }

    /**
     * Returns the client principal associated with this ticket.
     *
     * @return the client principal.
     */
    public final KerberosPrincipal getClient() {
        return client;
    }

    /**
     * Returns the service principal associated with this ticket.
     *
     * @return the service principal.
     */
    public final KerberosPrincipal getServer() {
        return server;
    }

    /**
     * Returns the session key associated with this ticket.
     *
     * @return the session key.
     */
    public final SecretKey getSessionKey() {
        if (destroyed)
            throw new IllegalStateException("This ticket is no longer valid");
        return sessionKey;
    }

    /**
     * Returns the key type of the session key associated with this
     * ticket as defined by the Kerberos Protocol Specification.
     *
     * @return the key type of the session key associated with this
     * ticket.
     *
     * @see #getSessionKey()
     */
    public final int getSessionKeyType() {
        if (destroyed)
            throw new IllegalStateException("This ticket is no longer valid");
        return sessionKey.getKeyType();
    }

    /**
     * Determines if this ticket is forwardable.
     *
     * @return true if this ticket is forwardable, false if not.
     */
    public final boolean isForwardable() {
        return flags[FORWARDABLE_TICKET_FLAG];
    }

    /**
     * Determines if this ticket had been forwarded or was issued based on
     * authentication involving a forwarded ticket-granting ticket.
     *
     * @return true if this ticket had been forwarded or was issued based on
     * authentication involving a forwarded ticket-granting ticket,
     * false otherwise.
     */
    public final boolean isForwarded() {
        return flags[FORWARDED_TICKET_FLAG];
    }

    /**
     * Determines if this ticket is proxiable.
     *
     * @return true if this ticket is proxiable, false if not.
     */
    public final boolean isProxiable() {
        return flags[PROXIABLE_TICKET_FLAG];
    }

    /**
     * Determines is this ticket is a proxy-ticket.
     *
     * @return true if this ticket is a proxy-ticket, false if not.
     */
    public final boolean isProxy() {
        return flags[PROXY_TICKET_FLAG];
    }


    /**
     * Determines is this ticket is post-dated.
     *
     * @return true if this ticket is post-dated, false if not.
     */
    public final boolean isPostdated() {
        return flags[POSTDATED_TICKET_FLAG];
    }

    /**
     * Determines is this ticket is renewable. If so, the {@link #refresh()
     * refresh} method can be called, assuming the validity period for
     * renewing is not already over.
     *
     * @return true if this ticket is renewable, false if not.
     */
    public final boolean isRenewable() {
        return flags[RENEWABLE_TICKET_FLAG];
    }

    /**
     * Determines if this ticket was issued using the Kerberos AS-Exchange
     * protocol, and not issued based on some ticket-granting ticket.
     *
     * @return true if this ticket was issued using the Kerberos AS-Exchange
     * protocol, false if not.
     */
    public final boolean isInitial() {
        return flags[INITIAL_TICKET_FLAG];
    }

    /**
     * Returns the flags associated with this ticket. Each element in the
     * returned array indicates the value for the corresponding bit in the
     * ASN.1 BitString that represents the ticket flags.
     *
     * @return the flags associated with this ticket.
     */
    public final boolean[]  getFlags() {
        return (flags == null? null: flags.clone());
    }

    /**
     * Returns the time that the client was authenticated.
     *
     * @return the time that the client was authenticated
     *         or null if not set.
     */
    public final Date getAuthTime() {
        return (authTime == null) ? null : (Date)authTime.clone();
    }

    /**
     * Returns the start time for this ticket's validity period.
     *
     * @return the start time for this ticket's validity period
     *         or null if not set.
     */
    public final Date getStartTime() {
        return (startTime == null) ? null : (Date)startTime.clone();
    }

    /**
     * Returns the expiration time for this ticket's validity period.
     *
     * @return the expiration time for this ticket's validity period.
     */
    public final Date getEndTime() {
        return (Date) endTime.clone();
    }

    /**
     * Returns the latest expiration time for this ticket, including all
     * renewals. This will return a null value for non-renewable tickets.
     *
     * @return the latest expiration time for this ticket.
     */
    public final Date getRenewTill() {
        return (renewTill == null) ? null: (Date)renewTill.clone();
    }

    /**
     * Returns a list of addresses from where the ticket can be used.
     *
     * @return ths list of addresses or null, if the field was not
     * provided.
     */
    public final InetAddress[] getClientAddresses() {
        return (clientAddresses == null) ? null: clientAddresses.clone();
    }

    /**
     * Returns an ASN.1 encoding of the entire ticket.
     *
     * @return an ASN.1 encoding of the entire ticket.
     */
    public final byte[] getEncoded() {
        if (destroyed)
            throw new IllegalStateException("This ticket is no longer valid");
        return asn1Encoding.clone();
    }

    /** Determines if this ticket is still current.  */
    public boolean isCurrent() {
        return (System.currentTimeMillis() <= getEndTime().getTime());
    }

    /**
     * Extends the validity period of this ticket. The ticket will contain
     * a new session key if the refresh operation succeeds. The refresh
     * operation will fail if the ticket is not renewable or the latest
     * allowable renew time has passed. Any other error returned by the
     * KDC will also cause this method to fail.
     *
     * Note: This method is not synchronized with the the accessor
     * methods of this object. Hence callers need to be aware of multiple
     * threads that might access this and try to renew it at the same
     * time.
     *
     * @throws RefreshFailedException if the ticket is not renewable, or
     * the latest allowable renew time has passed, or the KDC returns some
     * error.
     *
     * @see #isRenewable()
     * @see #getRenewTill()
     */
    public void refresh() throws RefreshFailedException {

        if (destroyed)
            throw new RefreshFailedException("A destroyed ticket "
                                             + "cannot be renewd.");

        if (!isRenewable())
            throw new RefreshFailedException("This ticket is not renewable");

        if (System.currentTimeMillis() > getRenewTill().getTime())
            throw new RefreshFailedException("This ticket is past "
                                             + "its last renewal time.");
        Throwable e = null;
        sun.security.krb5.Credentials krb5Creds = null;

        try {
            krb5Creds = new sun.security.krb5.Credentials(asn1Encoding,
                                                    client.toString(),
                                                    server.toString(),
                                                    sessionKey.getEncoded(),
                                                    sessionKey.getKeyType(),
                                                    flags,
                                                    authTime,
                                                    startTime,
                                                    endTime,
                                                    renewTill,
                                                    clientAddresses);
            krb5Creds = krb5Creds.renew();
        } catch (sun.security.krb5.KrbException krbException) {
            e = krbException;
        } catch (IOException ioException) {
            e = ioException;
        }

        if (e != null) {
            RefreshFailedException rfException
                = new RefreshFailedException("Failed to renew Kerberos Ticket "
                                             + "for client " + client
                                             + " and server " + server
                                             + " - " + e.getMessage());
            rfException.initCause(e);
            throw rfException;
        }

        /*
         * In case multiple threads try to refresh it at the same time.
         */
        synchronized (this) {
            try {
                this.destroy();
            } catch (DestroyFailedException dfException) {
                // Squelch it since we don't care about the old ticket.
            }
            init(krb5Creds.getEncoded(),
                 new KerberosPrincipal(krb5Creds.getClient().getName()),
                 new KerberosPrincipal(krb5Creds.getServer().getName(),
                                        KerberosPrincipal.KRB_NT_SRV_INST),
                 krb5Creds.getSessionKey().getBytes(),
                 krb5Creds.getSessionKey().getEType(),
                 krb5Creds.getFlags(),
                 krb5Creds.getAuthTime(),
                 krb5Creds.getStartTime(),
                 krb5Creds.getEndTime(),
                 krb5Creds.getRenewTill(),
                 krb5Creds.getClientAddresses());
            destroyed = false;
        }
    }

    /**
     * Destroys the ticket and destroys any sensitive information stored in
     * it.
     */
    public void destroy() throws DestroyFailedException {
        if (!destroyed) {
            Arrays.fill(asn1Encoding, (byte) 0);
            client = null;
            server = null;
            sessionKey.destroy();
            flags = null;
            authTime = null;
            startTime = null;
            endTime = null;
            renewTill = null;
            clientAddresses = null;
            destroyed = true;
        }
    }

    /**
     * Determines if this ticket has been destroyed.
     */
    public boolean isDestroyed() {
        return destroyed;
    }

    public String toString() {
        if (destroyed)
            throw new IllegalStateException("This ticket is no longer valid");
        StringBuffer caddrBuf = new StringBuffer();
        if (clientAddresses != null) {
            for (int i = 0; i < clientAddresses.length; i++) {
                caddrBuf.append("clientAddresses[" + i + "] = " +
                                 clientAddresses[i].toString());
            }
        }
        return ("Ticket (hex) = " + "\n" +
                 (new HexDumpEncoder()).encodeBuffer(asn1Encoding) + "\n" +
                "Client Principal = " + client.toString() + "\n" +
                "Server Principal = " + server.toString() + "\n" +
                "Session Key = " + sessionKey.toString() + "\n" +
                "Forwardable Ticket " + flags[FORWARDABLE_TICKET_FLAG] + "\n" +
                "Forwarded Ticket " + flags[FORWARDED_TICKET_FLAG] + "\n" +
                "Proxiable Ticket " + flags[PROXIABLE_TICKET_FLAG] + "\n" +
                "Proxy Ticket " + flags[PROXY_TICKET_FLAG] + "\n" +
                "Postdated Ticket " + flags[POSTDATED_TICKET_FLAG] + "\n" +
                "Renewable Ticket " + flags[RENEWABLE_TICKET_FLAG] + "\n" +
                "Initial Ticket " + flags[RENEWABLE_TICKET_FLAG] + "\n" +
                "Auth Time = " + String.valueOf(authTime) + "\n" +
                "Start Time = " + String.valueOf(startTime) + "\n" +
                "End Time = " + endTime.toString() + "\n" +
                "Renew Till = " + String.valueOf(renewTill) + "\n" +
                "Client Addresses " +
                (clientAddresses == null ? " Null " : caddrBuf.toString() +
                "\n"));
    }

    /**
     * Returns a hashcode for this KerberosTicket.
     *
     * @return a hashCode() for the {@code KerberosTicket}
     * @since 1.6
     */
    public int hashCode() {
        int result = 17;
        if (isDestroyed()) {
            return result;
        }
        result = result * 37 + Arrays.hashCode(getEncoded());
        result = result * 37 + endTime.hashCode();
        result = result * 37 + client.hashCode();
        result = result * 37 + server.hashCode();
        result = result * 37 + sessionKey.hashCode();

        // authTime may be null
        if (authTime != null) {
            result = result * 37 + authTime.hashCode();
        }

        // startTime may be null
        if (startTime != null) {
            result = result * 37 + startTime.hashCode();
        }

        // renewTill may be null
        if (renewTill != null) {
            result = result * 37 + renewTill.hashCode();
        }

        // clientAddress may be null, the array's hashCode is 0
        result = result * 37 + Arrays.hashCode(clientAddresses);
        return result * 37 + Arrays.hashCode(flags);
    }

    /**
     * Compares the specified Object with this KerberosTicket for equality.
     * Returns true if the given object is also a
     * {@code KerberosTicket} and the two
     * {@code KerberosTicket} instances are equivalent.
     *
     * @param other the Object to compare to
     * @return true if the specified object is equal to this KerberosTicket,
     * false otherwise. NOTE: Returns false if either of the KerberosTicket
     * objects has been destroyed.
     * @since 1.6
     */
    public boolean equals(Object other) {

        if (other == this)
            return true;

        if (! (other instanceof javax.security.auth.kerberos.KerberosTicket)) {
            return false;
        }

        javax.security.auth.kerberos.KerberosTicket otherTicket = ((javax.security.auth.kerberos.KerberosTicket) other);
        if (isDestroyed() || otherTicket.isDestroyed()) {
            return false;
        }

        if (!Arrays.equals(getEncoded(), otherTicket.getEncoded()) ||
                !endTime.equals(otherTicket.getEndTime()) ||
                !server.equals(otherTicket.getServer()) ||
                !client.equals(otherTicket.getClient()) ||
                !sessionKey.equals(otherTicket.getSessionKey()) ||
                !Arrays.equals(clientAddresses, otherTicket.getClientAddresses()) ||
                !Arrays.equals(flags, otherTicket.getFlags())) {
            return false;
        }

        // authTime may be null
        if (authTime == null) {
            if (otherTicket.getAuthTime() != null)
                return false;
        } else {
            if (!authTime.equals(otherTicket.getAuthTime()))
                return false;
        }

        // startTime may be null
        if (startTime == null) {
            if (otherTicket.getStartTime() != null)
                return false;
        } else {
            if (!startTime.equals(otherTicket.getStartTime()))
                return false;
        }

        if (renewTill == null) {
            if (otherTicket.getRenewTill() != null)
                return false;
        } else {
            if (!renewTill.equals(otherTicket.getRenewTill()))
                return false;
        }

        return true;
    }

    private void readObject(ObjectInputStream s)
        throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        if (sessionKey == null) {
           throw new InvalidObjectException("Session key cannot be null");
        }
        try {
            init(asn1Encoding, client, server, sessionKey,
                 flags, authTime, startTime, endTime,
                 renewTill, clientAddresses);
        } catch (IllegalArgumentException iae) {
            throw (InvalidObjectException)
                new InvalidObjectException(iae.getMessage()).initCause(iae);
        }
    }
}
