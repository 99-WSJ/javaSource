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

package java8.javax.security.sasl;

import javax.security.auth.callback.CallbackHandler;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslClientFactory;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServer;
import javax.security.sasl.SaslServerFactory;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import java.security.Provider;
import java.security.Security;

/**
 * A static class for creating SASL clients and servers.
 *<p>
 * This class defines the policy of how to locate, load, and instantiate
 * SASL clients and servers.
 *<p>
 * For example, an application or library gets a SASL client by doing
 * something like:
 *<blockquote><pre>
 * SaslClient sc = Sasl.createSaslClient(mechanisms,
 *     authorizationId, protocol, serverName, props, callbackHandler);
 *</pre></blockquote>
 * It can then proceed to use the instance to create an authentication connection.
 *<p>
 * Similarly, a server gets a SASL server by using code that looks as follows:
 *<blockquote><pre>
 * SaslServer ss = Sasl.createSaslServer(mechanism,
 *     protocol, serverName, props, callbackHandler);
 *</pre></blockquote>
 *
 * @since 1.5
 *
 * @author Rosanna Lee
 * @author Rob Weltman
 */
public class Sasl {
    // Cannot create one of these
    private Sasl() {
    }

    /**
     * The name of a property that specifies the quality-of-protection to use.
     * The property contains a comma-separated, ordered list
     * of quality-of-protection values that the
     * client or server is willing to support.  A qop value is one of
     * <ul>
     * <li>{@code "auth"} - authentication only</li>
     * <li>{@code "auth-int"} - authentication plus integrity protection</li>
     * <li>{@code "auth-conf"} - authentication plus integrity and confidentiality
     * protection</li>
     * </ul>
     *
     * The order of the list specifies the preference order of the client or
     * server. If this property is absent, the default qop is {@code "auth"}.
     * The value of this constant is {@code "javax.security.sasl.qop"}.
     */
    public static final String QOP = "javax.security.sasl.qop";

    /**
     * The name of a property that specifies the cipher strength to use.
     * The property contains a comma-separated, ordered list
     * of cipher strength values that
     * the client or server is willing to support. A strength value is one of
     * <ul>
     * <li>{@code "low"}</li>
     * <li>{@code "medium"}</li>
     * <li>{@code "high"}</li>
     * </ul>
     * The order of the list specifies the preference order of the client or
     * server.  An implementation should allow configuration of the meaning
     * of these values.  An application may use the Java Cryptography
     * Extension (JCE) with JCE-aware mechanisms to control the selection of
     * cipher suites that match the strength values.
     * <BR>
     * If this property is absent, the default strength is
     * {@code "high,medium,low"}.
     * The value of this constant is {@code "javax.security.sasl.strength"}.
     */
    public static final String STRENGTH = "javax.security.sasl.strength";

    /**
     * The name of a property that specifies whether the
     * server must authenticate to the client. The property contains
     * {@code "true"} if the server must
     * authenticate the to client; {@code "false"} otherwise.
     * The default is {@code "false"}.
     * <br>The value of this constant is
     * {@code "javax.security.sasl.server.authentication"}.
     */
    public static final String SERVER_AUTH =
    "javax.security.sasl.server.authentication";

    /**
     * The name of a property that specifies the bound server name for
     * an unbound server. A server is created as an unbound server by setting
     * the {@code serverName} argument in {@link #createSaslServer} as null.
     * The property contains the bound host name after the authentication
     * exchange has completed. It is only available on the server side.
     * <br>The value of this constant is
     * {@code "javax.security.sasl.bound.server.name"}.
     */
    public static final String BOUND_SERVER_NAME =
    "javax.security.sasl.bound.server.name";

    /**
     * The name of a property that specifies the maximum size of the receive
     * buffer in bytes of {@code SaslClient}/{@code SaslServer}.
     * The property contains the string representation of an integer.
     * <br>If this property is absent, the default size
     * is defined by the mechanism.
     * <br>The value of this constant is {@code "javax.security.sasl.maxbuffer"}.
     */
    public static final String MAX_BUFFER = "javax.security.sasl.maxbuffer";

    /**
     * The name of a property that specifies the maximum size of the raw send
     * buffer in bytes of {@code SaslClient}/{@code SaslServer}.
     * The property contains the string representation of an integer.
     * The value of this property is negotiated between the client and server
     * during the authentication exchange.
     * <br>The value of this constant is {@code "javax.security.sasl.rawsendsize"}.
     */
    public static final String RAW_SEND_SIZE = "javax.security.sasl.rawsendsize";

    /**
     * The name of a property that specifies whether to reuse previously
     * authenticated session information. The property contains "true" if the
     * mechanism implementation may attempt to reuse previously authenticated
     * session information; it contains "false" if the implementation must
     * not reuse previously authenticated session information.  A setting of
     * "true" serves only as a hint: it does not necessarily entail actual
     * reuse because reuse might not be possible due to a number of reasons,
     * including, but not limited to, lack of mechanism support for reuse,
     * expiration of reusable information, and the peer's refusal to support
     * reuse.
     *
     * The property's default value is "false".  The value of this constant
     * is "javax.security.sasl.reuse".
     *
     * Note that all other parameters and properties required to create a
     * SASL client/server instance must be provided regardless of whether
     * this property has been supplied. That is, you cannot supply any less
     * information in anticipation of reuse.
     *
     * Mechanism implementations that support reuse might allow customization
     * of its implementation, for factors such as cache size, timeouts, and
     * criteria for reusability. Such customizations are
     * implementation-dependent.
     */
     public static final String REUSE = "javax.security.sasl.reuse";

    /**
     * The name of a property that specifies
     * whether mechanisms susceptible to simple plain passive attacks (e.g.,
     * "PLAIN") are not permitted. The property
     * contains {@code "true"} if such mechanisms are not permitted;
     * {@code "false"} if such mechanisms are permitted.
     * The default is {@code "false"}.
     * <br>The value of this constant is
     * {@code "javax.security.sasl.policy.noplaintext"}.
     */
    public static final String POLICY_NOPLAINTEXT =
    "javax.security.sasl.policy.noplaintext";

    /**
     * The name of a property that specifies whether
     * mechanisms susceptible to active (non-dictionary) attacks
     * are not permitted.
     * The property contains {@code "true"}
     * if mechanisms susceptible to active attacks
     * are not permitted; {@code "false"} if such mechanisms are permitted.
     * The default is {@code "false"}.
     * <br>The value of this constant is
     * {@code "javax.security.sasl.policy.noactive"}.
     */
    public static final String POLICY_NOACTIVE =
    "javax.security.sasl.policy.noactive";

    /**
     * The name of a property that specifies whether
     * mechanisms susceptible to passive dictionary attacks are not permitted.
     * The property contains {@code "true"}
     * if mechanisms susceptible to dictionary attacks are not permitted;
     * {@code "false"} if such mechanisms are permitted.
     * The default is {@code "false"}.
     *<br>
     * The value of this constant is
     * {@code "javax.security.sasl.policy.nodictionary"}.
     */
    public static final String POLICY_NODICTIONARY =
    "javax.security.sasl.policy.nodictionary";

    /**
     * The name of a property that specifies whether mechanisms that accept
     * anonymous login are not permitted. The property contains {@code "true"}
     * if mechanisms that accept anonymous login are not permitted;
     * {@code "false"}
     * if such mechanisms are permitted. The default is {@code "false"}.
     *<br>
     * The value of this constant is
     * {@code "javax.security.sasl.policy.noanonymous"}.
     */
    public static final String POLICY_NOANONYMOUS =
    "javax.security.sasl.policy.noanonymous";

     /**
      * The name of a property that specifies whether mechanisms that implement
      * forward secrecy between sessions are required. Forward secrecy
      * means that breaking into one session will not automatically
      * provide information for breaking into future sessions.
      * The property
      * contains {@code "true"} if mechanisms that implement forward secrecy
      * between sessions are required; {@code "false"} if such mechanisms
      * are not required. The default is {@code "false"}.
      *<br>
      * The value of this constant is
      * {@code "javax.security.sasl.policy.forward"}.
      */
    public static final String POLICY_FORWARD_SECRECY =
    "javax.security.sasl.policy.forward";

    /**
     * The name of a property that specifies whether
     * mechanisms that pass client credentials are required. The property
     * contains {@code "true"} if mechanisms that pass
     * client credentials are required; {@code "false"}
     * if such mechanisms are not required. The default is {@code "false"}.
     *<br>
     * The value of this constant is
     * {@code "javax.security.sasl.policy.credentials"}.
     */
    public static final String POLICY_PASS_CREDENTIALS =
    "javax.security.sasl.policy.credentials";

    /**
     * The name of a property that specifies the credentials to use.
     * The property contains a mechanism-specific Java credential object.
     * Mechanism implementations may examine the value of this property
     * to determine whether it is a class that they support.
     * The property may be used to supply credentials to a mechanism that
     * supports delegated authentication.
     *<br>
     * The value of this constant is
     * {@code "javax.security.sasl.credentials"}.
     */
    public static final String CREDENTIALS = "javax.security.sasl.credentials";

    /**
     * Creates a {@code SaslClient} using the parameters supplied.
     *
     * This method uses the
<a href="{@docRoot}/../technotes/guides/security/crypto/CryptoSpec.html#Provider">JCA Security Provider Framework</a>, described in the
     * "Java Cryptography Architecture API Specification &amp; Reference", for
     * locating and selecting a {@code SaslClient} implementation.
     *
     * First, it
     * obtains an ordered list of {@code SaslClientFactory} instances from
     * the registered security providers for the "SaslClientFactory" service
     * and the specified SASL mechanism(s). It then invokes
     * {@code createSaslClient()} on each factory instance on the list
     * until one produces a non-null {@code SaslClient} instance. It returns
     * the non-null {@code SaslClient} instance, or null if the search fails
     * to produce a non-null {@code SaslClient} instance.
     *<p>
     * A security provider for SaslClientFactory registers with the
     * JCA Security Provider Framework keys of the form <br>
     * {@code SaslClientFactory.}<em>{@code mechanism_name}</em>
     * <br>
     * and values that are class names of implementations of
     * {@code javax.security.sasl.SaslClientFactory}.
     *
     * For example, a provider that contains a factory class,
     * {@code com.wiz.sasl.digest.ClientFactory}, that supports the
     * "DIGEST-MD5" mechanism would register the following entry with the JCA:
     * {@code SaslClientFactory.DIGEST-MD5 com.wiz.sasl.digest.ClientFactory}
     *<p>
     * See the
     * "Java Cryptography Architecture API Specification &amp; Reference"
     * for information about how to install and configure security service
     *  providers.
     *
     * @param mechanisms The non-null list of mechanism names to try. Each is the
     * IANA-registered name of a SASL mechanism. (e.g. "GSSAPI", "CRAM-MD5").
     * @param authorizationId The possibly null protocol-dependent
     * identification to be used for authorization.
     * If null or empty, the server derives an authorization
     * ID from the client's authentication credentials.
     * When the SASL authentication completes successfully,
     * the specified entity is granted access.
     *
     * @param protocol The non-null string name of the protocol for which
     * the authentication is being performed (e.g., "ldap").
     *
     * @param serverName The non-null fully-qualified host name of the server
     * to authenticate to.
     *
     * @param props The possibly null set of properties used to
     * select the SASL mechanism and to configure the authentication
     * exchange of the selected mechanism.
     * For example, if {@code props} contains the
     * {@code Sasl.POLICY_NOPLAINTEXT} property with the value
     * {@code "true"}, then the selected
     * SASL mechanism must not be susceptible to simple plain passive attacks.
     * In addition to the standard properties declared in this class,
     * other, possibly mechanism-specific, properties can be included.
     * Properties not relevant to the selected mechanism are ignored,
     * including any map entries with non-String keys.
     *
     * @param cbh The possibly null callback handler to used by the SASL
     * mechanisms to get further information from the application/library
     * to complete the authentication. For example, a SASL mechanism might
     * require the authentication ID, password and realm from the caller.
     * The authentication ID is requested by using a {@code NameCallback}.
     * The password is requested by using a {@code PasswordCallback}.
     * The realm is requested by using a {@code RealmChoiceCallback} if there is a list
     * of realms to choose from, and by using a {@code RealmCallback} if
     * the realm must be entered.
     *
     *@return A possibly null {@code SaslClient} created using the parameters
     * supplied. If null, cannot find a {@code SaslClientFactory}
     * that will produce one.
     *@exception SaslException If cannot create a {@code SaslClient} because
     * of an error.
     */
    public static SaslClient createSaslClient(
        String[] mechanisms,
        String authorizationId,
        String protocol,
        String serverName,
        Map<String,?> props,
        CallbackHandler cbh) throws SaslException {

        SaslClient mech = null;
        SaslClientFactory fac;
        String className;
        String mechName;

        for (int i = 0; i < mechanisms.length; i++) {
            if ((mechName=mechanisms[i]) == null) {
                throw new NullPointerException(
                    "Mechanism name cannot be null");
            } else if (mechName.length() == 0) {
                continue;
            }
            String mechFilter = "SaslClientFactory." + mechName;
            Provider[] provs = Security.getProviders(mechFilter);
            for (int j = 0; provs != null && j < provs.length; j++) {
                className = provs[j].getProperty(mechFilter);
                if (className == null) {
                    // Case is ignored
                    continue;
                }

                fac = (SaslClientFactory) loadFactory(provs[j], className);
                if (fac != null) {
                    mech = fac.createSaslClient(
                        new String[]{mechanisms[i]}, authorizationId,
                        protocol, serverName, props, cbh);
                    if (mech != null) {
                        return mech;
                    }
                }
            }
        }

        return null;
    }

    private static Object loadFactory(Provider p, String className)
        throws SaslException {
        try {
            /*
             * Load the implementation class with the same class loader
             * that was used to load the provider.
             * In order to get the class loader of a class, the
             * caller's class loader must be the same as or an ancestor of
             * the class loader being returned. Otherwise, the caller must
             * have "getClassLoader" permission, or a SecurityException
             * will be thrown.
             */
            ClassLoader cl = p.getClass().getClassLoader();
            Class<?> implClass;
            implClass = Class.forName(className, true, cl);
            return implClass.newInstance();
        } catch (ClassNotFoundException e) {
            throw new SaslException("Cannot load class " + className, e);
        } catch (InstantiationException e) {
            throw new SaslException("Cannot instantiate class " + className, e);
        } catch (IllegalAccessException e) {
            throw new SaslException("Cannot access class " + className, e);
        } catch (SecurityException e) {
            throw new SaslException("Cannot access class " + className, e);
        }
    }


    /**
     * Creates a {@code SaslServer} for the specified mechanism.
     *
     * This method uses the
<a href="{@docRoot}/../technotes/guides/security/crypto/CryptoSpec.html#Provider">JCA Security Provider Framework</a>,
     * described in the
     * "Java Cryptography Architecture API Specification &amp; Reference", for
     * locating and selecting a {@code SaslServer} implementation.
     *
     * First, it
     * obtains an ordered list of {@code SaslServerFactory} instances from
     * the registered security providers for the "SaslServerFactory" service
     * and the specified mechanism. It then invokes
     * {@code createSaslServer()} on each factory instance on the list
     * until one produces a non-null {@code SaslServer} instance. It returns
     * the non-null {@code SaslServer} instance, or null if the search fails
     * to produce a non-null {@code SaslServer} instance.
     *<p>
     * A security provider for SaslServerFactory registers with the
     * JCA Security Provider Framework keys of the form <br>
     * {@code SaslServerFactory.}<em>{@code mechanism_name}</em>
     * <br>
     * and values that are class names of implementations of
     * {@code javax.security.sasl.SaslServerFactory}.
     *
     * For example, a provider that contains a factory class,
     * {@code com.wiz.sasl.digest.ServerFactory}, that supports the
     * "DIGEST-MD5" mechanism would register the following entry with the JCA:
     * {@code SaslServerFactory.DIGEST-MD5  com.wiz.sasl.digest.ServerFactory}
     *<p>
     * See the
     * "Java Cryptography Architecture API Specification &amp; Reference"
     * for information about how to install and configure security
     * service providers.
     *
     * @param mechanism The non-null mechanism name. It must be an
     * IANA-registered name of a SASL mechanism. (e.g. "GSSAPI", "CRAM-MD5").
     * @param protocol The non-null string name of the protocol for which
     * the authentication is being performed (e.g., "ldap").
     * @param serverName The fully qualified host name of the server, or null
     * if the server is not bound to any specific host name. If the mechanism
     * does not allow an unbound server, a {@code SaslException} will
     * be thrown.
     * @param props The possibly null set of properties used to
     * select the SASL mechanism and to configure the authentication
     * exchange of the selected mechanism.
     * For example, if {@code props} contains the
     * {@code Sasl.POLICY_NOPLAINTEXT} property with the value
     * {@code "true"}, then the selected
     * SASL mechanism must not be susceptible to simple plain passive attacks.
     * In addition to the standard properties declared in this class,
     * other, possibly mechanism-specific, properties can be included.
     * Properties not relevant to the selected mechanism are ignored,
     * including any map entries with non-String keys.
     *
     * @param cbh The possibly null callback handler to used by the SASL
     * mechanisms to get further information from the application/library
     * to complete the authentication. For example, a SASL mechanism might
     * require the authentication ID, password and realm from the caller.
     * The authentication ID is requested by using a {@code NameCallback}.
     * The password is requested by using a {@code PasswordCallback}.
     * The realm is requested by using a {@code RealmChoiceCallback} if there is a list
     * of realms to choose from, and by using a {@code RealmCallback} if
     * the realm must be entered.
     *
     *@return A possibly null {@code SaslServer} created using the parameters
     * supplied. If null, cannot find a {@code SaslServerFactory}
     * that will produce one.
     *@exception SaslException If cannot create a {@code SaslServer} because
     * of an error.
     **/
    public static SaslServer
        createSaslServer(String mechanism,
                    String protocol,
                    String serverName,
                    Map<String,?> props,
                    CallbackHandler cbh)
        throws SaslException {

        SaslServer mech = null;
        SaslServerFactory fac;
        String className;

        if (mechanism == null) {
            throw new NullPointerException("Mechanism name cannot be null");
        } else if (mechanism.length() == 0) {
            return null;
        }

        String mechFilter = "SaslServerFactory." + mechanism;
        Provider[] provs = Security.getProviders(mechFilter);
        for (int j = 0; provs != null && j < provs.length; j++) {
            className = provs[j].getProperty(mechFilter);
            if (className == null) {
                throw new SaslException("Provider does not support " +
                    mechFilter);
            }
            fac = (SaslServerFactory) loadFactory(provs[j], className);
            if (fac != null) {
                mech = fac.createSaslServer(
                    mechanism, protocol, serverName, props, cbh);
                if (mech != null) {
                    return mech;
                }
            }
        }

        return null;
    }

    /**
     * Gets an enumeration of known factories for producing {@code SaslClient}.
     * This method uses the same algorithm for locating factories as
     * {@code createSaslClient()}.
     * @return A non-null enumeration of known factories for producing
     * {@code SaslClient}.
     * @see #createSaslClient
     */
    public static Enumeration<SaslClientFactory> getSaslClientFactories() {
        Set<Object> facs = getFactories("SaslClientFactory");
        final Iterator<Object> iter = facs.iterator();
        return new Enumeration<SaslClientFactory>() {
            public boolean hasMoreElements() {
                return iter.hasNext();
            }
            public SaslClientFactory nextElement() {
                return (SaslClientFactory)iter.next();
            }
        };
    }

    /**
     * Gets an enumeration of known factories for producing {@code SaslServer}.
     * This method uses the same algorithm for locating factories as
     * {@code createSaslServer()}.
     * @return A non-null enumeration of known factories for producing
     * {@code SaslServer}.
     * @see #createSaslServer
     */
    public static Enumeration<SaslServerFactory> getSaslServerFactories() {
        Set<Object> facs = getFactories("SaslServerFactory");
        final Iterator<Object> iter = facs.iterator();
        return new Enumeration<SaslServerFactory>() {
            public boolean hasMoreElements() {
                return iter.hasNext();
            }
            public SaslServerFactory nextElement() {
                return (SaslServerFactory)iter.next();
            }
        };
    }

    private static Set<Object> getFactories(String serviceName) {
        HashSet<Object> result = new HashSet<Object>();

        if ((serviceName == null) || (serviceName.length() == 0) ||
            (serviceName.endsWith("."))) {
            return result;
        }


        Provider[] providers = Security.getProviders();
        HashSet<String> classes = new HashSet<String>();
        Object fac;

        for (int i = 0; i < providers.length; i++) {
            classes.clear();

            // Check the keys for each provider.
            for (Enumeration<Object> e = providers[i].keys(); e.hasMoreElements(); ) {
                String currentKey = (String)e.nextElement();
                if (currentKey.startsWith(serviceName)) {
                    // We should skip the currentKey if it contains a
                    // whitespace. The reason is: such an entry in the
                    // provider property contains attributes for the
                    // implementation of an algorithm. We are only interested
                    // in entries which lead to the implementation
                    // classes.
                    if (currentKey.indexOf(" ") < 0) {
                        String className = providers[i].getProperty(currentKey);
                        if (!classes.contains(className)) {
                            classes.add(className);
                            try {
                                fac = loadFactory(providers[i], className);
                                if (fac != null) {
                                    result.add(fac);
                                }
                            }catch (Exception ignore) {
                            }
                        }
                    }
                }
            }
        }
        return Collections.unmodifiableSet(result);
    }
}
