/*
 * Copyright (c) 1999, 2013, Oracle and/or its affiliates. All rights reserved.
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

/**
 * Contains class and interfaces for supporting SASL.
 *
 * This package defines classes and interfaces for SASL mechanisms.
 * It is used by developers to add authentication support for
 * connection-based protocols that use SASL.
 *
 * <h3>SASL Overview</h3>
 *
 * Simple Authentication and Security Layer (SASL) specifies a
 * challenge-response protocol in which data is exchanged between the
 * client and the server for the purposes of
 * authentication and (optional) establishment of a security layer on
 * which to carry on subsequent communications.  It is used with
 * connection-based protocols such as LDAPv3 or IMAPv4.  SASL is
 * described in
 * <A HREF="http://www.ietf.org/rfc/rfc2222.txt">RFC 2222</A>.
 *
 *
 * There are various <em>mechanisms</em> defined for SASL.
 * Each mechanism defines the data that must be exchanged between the
 * client and server in order for the authentication to succeed.
 * This data exchange required for a particular mechanism is referred to
 * to as its <em>protocol profile</em>.
 * The following are some examples of mechanisms that have been defined by
 * the Internet standards community.
 * <ul>
 * <li>DIGEST-MD5 (<A HREF="http://www.ietf.org/rfc/rfc2831.txt">RFC 2831</a>).
 * This mechanism defines how HTTP Digest Authentication can be used as a SASL
 * mechanism.
 * <li>Anonymous (<A HREF="http://www.ietf.org/rfc/rfc2245.txt">RFC 2245</a>).
 * This mechanism is anonymous authentication in which no credentials are
 * necessary.
 * <li>External (<A HREF="http://www.ietf.org/rfc/rfc2222.txt">RFC 2222</A>).
 * This mechanism obtains authentication information
 * from an external source (such as TLS or IPsec).
 * <li>S/Key (<A HREF="http://www.ietf.org/rfc/rfc2222.txt">RFC 2222</A>).
 * This mechanism uses the MD4 digest algorithm to exchange data based on
 * a shared secret.
 * <li>GSSAPI (<A HREF="http://www.ietf.org/rfc/rfc2222.txt">RFC 2222</A>).
 * This mechanism uses the
 * <A HREF="http://www.ietf.org/rfc/rfc2078.txt">GSSAPI</A>
 * for obtaining authentication information.
 * </ul>
 *
 * Some of these mechanisms provide both authentication and establishment
 * of a security layer, others only authentication.  Anonymous and
 * S/Key do not provide for any security layers.  GSSAPI and DIGEST-MD5
 * allow negotiation of the security layer.  For External, the
 * security layer is determined by the external protocol.
 *
 * <h3>Usage</h3>
 *
 * Users of this API are typically developers who produce
 * client library implementations for connection-based protocols,
 * such as LDAPv3 and IMAPv4,
 * and developers who write servers (such as LDAP servers and IMAP servers).
 * Developers who write client libraries use the
 * {@code SaslClient} and {@code SaslClientFactory} interfaces.
 * Developers who write servers use the
 * {@code SaslServer} and {@code SaslServerFactory} interfaces.
 *
 * Among these two groups of users, each can be further divided into two groups:
 * those who <em>produce</em> the SASL mechanisms and those
 * who <em>use</em> the SASL mechanisms.
 * The producers of SASL mechanisms need to provide implementations
 * for these interfaces, while users of the SASL mechanisms use
 * the APIs in this package to access those implementations.
 *
 * <h2>Related Documentation</h2>
 *
 * Please refer to the
 * <a href="../../../../technotes/guides/security/sasl/sasl-refguide.html">Java
 * SASL Programming Guide</a> for information on how to use this API.
 *
 * @since 1.5
 */
package java8.javax.security.sasl;
