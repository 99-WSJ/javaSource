/*
 * Copyright (c) 1997, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java8.java.security;

import javax.crypto.SecretKey;
import javax.security.auth.DestroyFailedException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import java.io.*;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.PrivateKey;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.util.*;

/**
 * This class represents a storage facility for cryptographic
 * keys and certificates.
 *
 * <p> A {@code KeyStore} manages different types of entries.
 * Each type of entry implements the {@code KeyStore.Entry} interface.
 * Three basic {@code KeyStore.Entry} implementations are provided:
 *
 * <ul>
 * <li><b>KeyStore.PrivateKeyEntry</b>
 * <p> This type of entry holds a cryptographic {@code PrivateKey},
 * which is optionally stored in a protected format to prevent
 * unauthorized access.  It is also accompanied by a certificate chain
 * for the corresponding public key.
 *
 * <p> Private keys and certificate chains are used by a given entity for
 * self-authentication. Applications for this authentication include software
 * distribution organizations which sign JAR files as part of releasing
 * and/or licensing software.
 *
 * <li><b>KeyStore.SecretKeyEntry</b>
 * <p> This type of entry holds a cryptographic {@code SecretKey},
 * which is optionally stored in a protected format to prevent
 * unauthorized access.
 *
 * <li><b>KeyStore.TrustedCertificateEntry</b>
 * <p> This type of entry contains a single public key {@code Certificate}
 * belonging to another party. It is called a <i>trusted certificate</i>
 * because the keystore owner trusts that the public key in the certificate
 * indeed belongs to the identity identified by the <i>subject</i> (owner)
 * of the certificate.
 *
 * <p>This type of entry can be used to authenticate other parties.
 * </ul>
 *
 * <p> Each entry in a keystore is identified by an "alias" string. In the
 * case of private keys and their associated certificate chains, these strings
 * distinguish among the different ways in which the entity may authenticate
 * itself. For example, the entity may authenticate itself using different
 * certificate authorities, or using different public key algorithms.
 *
 * <p> Whether aliases are case sensitive is implementation dependent. In order
 * to avoid problems, it is recommended not to use aliases in a KeyStore that
 * only differ in case.
 *
 * <p> Whether keystores are persistent, and the mechanisms used by the
 * keystore if it is persistent, are not specified here. This allows
 * use of a variety of techniques for protecting sensitive (e.g., private or
 * secret) keys. Smart cards or other integrated cryptographic engines
 * (SafeKeyper) are one option, and simpler mechanisms such as files may also
 * be used (in a variety of formats).
 *
 * <p> Typical ways to request a KeyStore object include
 * relying on the default type and providing a specific keystore type.
 *
 * <ul>
 * <li>To rely on the default type:
 * <pre>
 *    KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
 * </pre>
 * The system will return a keystore implementation for the default type.
 *
 * <li>To provide a specific keystore type:
 * <pre>
 *      KeyStore ks = KeyStore.getInstance("JKS");
 * </pre>
 * The system will return the most preferred implementation of the
 * specified keystore type available in the environment. <p>
 * </ul>
 *
 * <p> Before a keystore can be accessed, it must be
 * {@link #load(InputStream, char[]) loaded}.
 * <pre>
 *    KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
 *
 *    // get user password and file input stream
 *    char[] password = getPassword();
 *
 *    try (FileInputStream fis = new FileInputStream("keyStoreName")) {
 *        ks.load(fis, password);
 *    }
 * </pre>
 *
 * To create an empty keystore using the above {@code load} method,
 * pass {@code null} as the {@code InputStream} argument.
 *
 * <p> Once the keystore has been loaded, it is possible
 * to read existing entries from the keystore, or to write new entries
 * into the keystore:
 * <pre>
 *    KeyStore.ProtectionParameter protParam =
 *        new KeyStore.PasswordProtection(password);
 *
 *    // get my private key
 *    KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry)
 *        ks.getEntry("privateKeyAlias", protParam);
 *    PrivateKey myPrivateKey = pkEntry.getPrivateKey();
 *
 *    // save my secret key
 *    javax.crypto.SecretKey mySecretKey;
 *    KeyStore.SecretKeyEntry skEntry =
 *        new KeyStore.SecretKeyEntry(mySecretKey);
 *    ks.setEntry("secretKeyAlias", skEntry, protParam);
 *
 *    // store away the keystore
 *    try (FileOutputStream fos = new FileOutputStream("newKeyStoreName")) {
 *        ks.store(fos, password);
 *    }
 * </pre>
 *
 * Note that although the same password may be used to
 * load the keystore, to protect the private key entry,
 * to protect the secret key entry, and to store the keystore
 * (as is shown in the sample code above),
 * different passwords or other protection parameters
 * may also be used.
 *
 * <p> Every implementation of the Java platform is required to support
 * the following standard {@code KeyStore} type:
 * <ul>
 * <li>{@code PKCS12}</li>
 * </ul>
 * This type is described in the <a href=
 * "{@docRoot}/../technotes/guides/security/StandardNames.html#KeyStore">
 * KeyStore section</a> of the
 * Java Cryptography Architecture Standard Algorithm Name Documentation.
 * Consult the release documentation for your implementation to see if any
 * other types are supported.
 *
 * @author Jan Luehe
 *
 * @see PrivateKey
 * @see SecretKey
 * @see Certificate
 *
 * @since 1.2
 */

public class KeyStore {

    /*
     * Constant to lookup in the Security properties file to determine
     * the default keystore type.
     * In the Security properties file, the default keystore type is given as:
     * <pre>
     * keystore.type=jks
     * </pre>
     */
    private static final String KEYSTORE_TYPE = "keystore.type";

    // The keystore type
    private String type;

    // The provider
    private Provider provider;

    // The provider implementation
    private KeyStoreSpi keyStoreSpi;

    // Has this keystore been initialized (loaded)?
    private boolean initialized = false;

    /**
     * A marker interface for {@code KeyStore}
     * {@link #load(java.security.KeyStore.LoadStoreParameter) load}
     * and
     * {@link #store(java.security.KeyStore.LoadStoreParameter) store}
     * parameters.
     *
     * @since 1.5
     */
    public static interface LoadStoreParameter {
        /**
         * Gets the parameter used to protect keystore data.
         *
         * @return the parameter used to protect keystore data, or null
         */
        public ProtectionParameter getProtectionParameter();
    }

    /**
     * A marker interface for keystore protection parameters.
     *
     * <p> The information stored in a {@code ProtectionParameter}
     * object protects the contents of a keystore.
     * For example, protection parameters may be used to check
     * the integrity of keystore data, or to protect the
     * confidentiality of sensitive keystore data
     * (such as a {@code PrivateKey}).
     *
     * @since 1.5
     */
    public static interface ProtectionParameter { }

    /**
     * A password-based implementation of {@code ProtectionParameter}.
     *
     * @since 1.5
     */
    public static class PasswordProtection implements
                ProtectionParameter, javax.security.auth.Destroyable {

        private final char[] password;
        private final String protectionAlgorithm;
        private final AlgorithmParameterSpec protectionParameters;
        private volatile boolean destroyed = false;

        /**
         * Creates a password parameter.
         *
         * <p> The specified {@code password} is cloned before it is stored
         * in the new {@code PasswordProtection} object.
         *
         * @param password the password, which may be {@code null}
         */
        public PasswordProtection(char[] password) {
            this.password = (password == null) ? null : password.clone();
            this.protectionAlgorithm = null;
            this.protectionParameters = null;
        }

        /**
         * Creates a password parameter and specifies the protection algorithm
         * and associated parameters to use when encrypting a keystore entry.
         * <p>
         * The specified {@code password} is cloned before it is stored in the
         * new {@code PasswordProtection} object.
         *
         * @param password the password, which may be {@code null}
         * @param protectionAlgorithm the encryption algorithm name, for
         *     example, {@code PBEWithHmacSHA256AndAES_256}.
         *     See the Cipher section in the <a href=
         * "{@docRoot}/../technotes/guides/security/StandardNames.html#Cipher">
         * Java Cryptography Architecture Standard Algorithm Name
         * Documentation</a>
         *     for information about standard encryption algorithm names.
         * @param protectionParameters the encryption algorithm parameter
         *     specification, which may be {@code null}
         * @exception NullPointerException if {@code protectionAlgorithm} is
         *     {@code null}
         *
         * @since 1.8
         */
        public PasswordProtection(char[] password, String protectionAlgorithm,
            AlgorithmParameterSpec protectionParameters) {
            if (protectionAlgorithm == null) {
                throw new NullPointerException("invalid null input");
            }
            this.password = (password == null) ? null : password.clone();
            this.protectionAlgorithm = protectionAlgorithm;
            this.protectionParameters = protectionParameters;
        }

        /**
         * Gets the name of the protection algorithm.
         * If none was set then the keystore provider will use its default
         * protection algorithm. The name of the default protection algorithm
         * for a given keystore type is set using the
         * {@code 'keystore.<type>.keyProtectionAlgorithm'} security property.
         * For example, the
         * {@code keystore.PKCS12.keyProtectionAlgorithm} property stores the
         * name of the default key protection algorithm used for PKCS12
         * keystores. If the security property is not set, an
         * implementation-specific algorithm will be used.
         *
         * @return the algorithm name, or {@code null} if none was set
         *
         * @since 1.8
         */
        public String getProtectionAlgorithm() {
            return protectionAlgorithm;
        }

        /**
         * Gets the parameters supplied for the protection algorithm.
         *
         * @return the algorithm parameter specification, or {@code  null},
         *     if none was set
         *
         * @since 1.8
         */
        public AlgorithmParameterSpec getProtectionParameters() {
            return protectionParameters;
        }

        /**
         * Gets the password.
         *
         * <p>Note that this method returns a reference to the password.
         * If a clone of the array is created it is the caller's
         * responsibility to zero out the password information
         * after it is no longer needed.
         *
         * @see #destroy()
         * @return the password, which may be {@code null}
         * @exception IllegalStateException if the password has
         *              been cleared (destroyed)
         */
        public synchronized char[] getPassword() {
            if (destroyed) {
                throw new IllegalStateException("password has been cleared");
            }
            return password;
        }

        /**
         * Clears the password.
         *
         * @exception DestroyFailedException if this method was unable
         *      to clear the password
         */
        public synchronized void destroy() throws DestroyFailedException {
            destroyed = true;
            if (password != null) {
                Arrays.fill(password, ' ');
            }
        }

        /**
         * Determines if password has been cleared.
         *
         * @return true if the password has been cleared, false otherwise
         */
        public synchronized boolean isDestroyed() {
            return destroyed;
        }
    }

    /**
     * A ProtectionParameter encapsulating a CallbackHandler.
     *
     * @since 1.5
     */
    public static class CallbackHandlerProtection
            implements ProtectionParameter {

        private final CallbackHandler handler;

        /**
         * Constructs a new CallbackHandlerProtection from a
         * CallbackHandler.
         *
         * @param handler the CallbackHandler
         * @exception NullPointerException if handler is null
         */
        public CallbackHandlerProtection(CallbackHandler handler) {
            if (handler == null) {
                throw new NullPointerException("handler must not be null");
            }
            this.handler = handler;
        }

        /**
         * Returns the CallbackHandler.
         *
         * @return the CallbackHandler.
         */
        public CallbackHandler getCallbackHandler() {
            return handler;
        }

    }

    /**
     * A marker interface for {@code KeyStore} entry types.
     *
     * @since 1.5
     */
    public static interface Entry {

        /**
         * Retrieves the attributes associated with an entry.
         * <p>
         * The default implementation returns an empty {@code Set}.
         *
         * @return an unmodifiable {@code Set} of attributes, possibly empty
         *
         * @since 1.8
         */
        public default Set<Attribute> getAttributes() {
            return Collections.<Attribute>emptySet();
        }

        /**
         * An attribute associated with a keystore entry.
         * It comprises a name and one or more values.
         *
         * @since 1.8
         */
        public interface Attribute {
            /**
             * Returns the attribute's name.
             *
             * @return the attribute name
             */
            public String getName();

            /**
             * Returns the attribute's value.
             * Multi-valued attributes encode their values as a single string.
             *
             * @return the attribute value
             */
            public String getValue();
        }
    }

    /**
     * A {@code KeyStore} entry that holds a {@code PrivateKey}
     * and corresponding certificate chain.
     *
     * @since 1.5
     */
    public static final class PrivateKeyEntry implements Entry {

        private final PrivateKey privKey;
        private final Certificate[] chain;
        private final Set<Attribute> attributes;

        /**
         * Constructs a {@code PrivateKeyEntry} with a
         * {@code PrivateKey} and corresponding certificate chain.
         *
         * <p> The specified {@code chain} is cloned before it is stored
         * in the new {@code PrivateKeyEntry} object.
         *
         * @param privateKey the {@code PrivateKey}
         * @param chain an array of {@code Certificate}s
         *      representing the certificate chain.
         *      The chain must be ordered and contain a
         *      {@code Certificate} at index 0
         *      corresponding to the private key.
         *
         * @exception NullPointerException if
         *      {@code privateKey} or {@code chain}
         *      is {@code null}
         * @exception IllegalArgumentException if the specified chain has a
         *      length of 0, if the specified chain does not contain
         *      {@code Certificate}s of the same type,
         *      or if the {@code PrivateKey} algorithm
         *      does not match the algorithm of the {@code PublicKey}
         *      in the end entity {@code Certificate} (at index 0)
         */
        public PrivateKeyEntry(PrivateKey privateKey, Certificate[] chain) {
            this(privateKey, chain, Collections.<Attribute>emptySet());
        }

        /**
         * Constructs a {@code PrivateKeyEntry} with a {@code PrivateKey} and
         * corresponding certificate chain and associated entry attributes.
         *
         * <p> The specified {@code chain} and {@code attributes} are cloned
         * before they are stored in the new {@code PrivateKeyEntry} object.
         *
         * @param privateKey the {@code PrivateKey}
         * @param chain an array of {@code Certificate}s
         *      representing the certificate chain.
         *      The chain must be ordered and contain a
         *      {@code Certificate} at index 0
         *      corresponding to the private key.
         * @param attributes the attributes
         *
         * @exception NullPointerException if {@code privateKey}, {@code chain}
         *      or {@code attributes} is {@code null}
         * @exception IllegalArgumentException if the specified chain has a
         *      length of 0, if the specified chain does not contain
         *      {@code Certificate}s of the same type,
         *      or if the {@code PrivateKey} algorithm
         *      does not match the algorithm of the {@code PublicKey}
         *      in the end entity {@code Certificate} (at index 0)
         *
         * @since 1.8
         */
        public PrivateKeyEntry(PrivateKey privateKey, Certificate[] chain,
           Set<Attribute> attributes) {

            if (privateKey == null || chain == null || attributes == null) {
                throw new NullPointerException("invalid null input");
            }
            if (chain.length == 0) {
                throw new IllegalArgumentException
                                ("invalid zero-length input chain");
            }

            Certificate[] clonedChain = chain.clone();
            String certType = clonedChain[0].getType();
            for (int i = 1; i < clonedChain.length; i++) {
                if (!certType.equals(clonedChain[i].getType())) {
                    throw new IllegalArgumentException
                                ("chain does not contain certificates " +
                                "of the same type");
                }
            }
            if (!privateKey.getAlgorithm().equals
                        (clonedChain[0].getPublicKey().getAlgorithm())) {
                throw new IllegalArgumentException
                                ("private key algorithm does not match " +
                                "algorithm of public key in end entity " +
                                "certificate (at index 0)");
            }
            this.privKey = privateKey;

            if (clonedChain[0] instanceof X509Certificate &&
                !(clonedChain instanceof X509Certificate[])) {

                this.chain = new X509Certificate[clonedChain.length];
                System.arraycopy(clonedChain, 0,
                                this.chain, 0, clonedChain.length);
            } else {
                this.chain = clonedChain;
            }

            this.attributes =
                Collections.unmodifiableSet(new HashSet<>(attributes));
        }

        /**
         * Gets the {@code PrivateKey} from this entry.
         *
         * @return the {@code PrivateKey} from this entry
         */
        public PrivateKey getPrivateKey() {
            return privKey;
        }

        /**
         * Gets the {@code Certificate} chain from this entry.
         *
         * <p> The stored chain is cloned before being returned.
         *
         * @return an array of {@code Certificate}s corresponding
         *      to the certificate chain for the public key.
         *      If the certificates are of type X.509,
         *      the runtime type of the returned array is
         *      {@code X509Certificate[]}.
         */
        public Certificate[] getCertificateChain() {
            return chain.clone();
        }

        /**
         * Gets the end entity {@code Certificate}
         * from the certificate chain in this entry.
         *
         * @return the end entity {@code Certificate} (at index 0)
         *      from the certificate chain in this entry.
         *      If the certificate is of type X.509,
         *      the runtime type of the returned certificate is
         *      {@code X509Certificate}.
         */
        public Certificate getCertificate() {
            return chain[0];
        }

        /**
         * Retrieves the attributes associated with an entry.
         * <p>
         *
         * @return an unmodifiable {@code Set} of attributes, possibly empty
         *
         * @since 1.8
         */
        @Override
        public Set<Attribute> getAttributes() {
            return attributes;
        }

        /**
         * Returns a string representation of this PrivateKeyEntry.
         * @return a string representation of this PrivateKeyEntry.
         */
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Private key entry and certificate chain with "
                + chain.length + " elements:\r\n");
            for (Certificate cert : chain) {
                sb.append(cert);
                sb.append("\r\n");
            }
            return sb.toString();
        }

    }

    /**
     * A {@code KeyStore} entry that holds a {@code SecretKey}.
     *
     * @since 1.5
     */
    public static final class SecretKeyEntry implements Entry {

        private final SecretKey sKey;
        private final Set<Attribute> attributes;

        /**
         * Constructs a {@code SecretKeyEntry} with a
         * {@code SecretKey}.
         *
         * @param secretKey the {@code SecretKey}
         *
         * @exception NullPointerException if {@code secretKey}
         *      is {@code null}
         */
        public SecretKeyEntry(SecretKey secretKey) {
            if (secretKey == null) {
                throw new NullPointerException("invalid null input");
            }
            this.sKey = secretKey;
            this.attributes = Collections.<Attribute>emptySet();
        }

        /**
         * Constructs a {@code SecretKeyEntry} with a {@code SecretKey} and
         * associated entry attributes.
         *
         * <p> The specified {@code attributes} is cloned before it is stored
         * in the new {@code SecretKeyEntry} object.
         *
         * @param secretKey the {@code SecretKey}
         * @param attributes the attributes
         *
         * @exception NullPointerException if {@code secretKey} or
         *     {@code attributes} is {@code null}
         *
         * @since 1.8
         */
        public SecretKeyEntry(SecretKey secretKey, Set<Attribute> attributes) {

            if (secretKey == null || attributes == null) {
                throw new NullPointerException("invalid null input");
            }
            this.sKey = secretKey;
            this.attributes =
                Collections.unmodifiableSet(new HashSet<>(attributes));
        }

        /**
         * Gets the {@code SecretKey} from this entry.
         *
         * @return the {@code SecretKey} from this entry
         */
        public SecretKey getSecretKey() {
            return sKey;
        }

        /**
         * Retrieves the attributes associated with an entry.
         * <p>
         *
         * @return an unmodifiable {@code Set} of attributes, possibly empty
         *
         * @since 1.8
         */
        @Override
        public Set<Attribute> getAttributes() {
            return attributes;
        }

        /**
         * Returns a string representation of this SecretKeyEntry.
         * @return a string representation of this SecretKeyEntry.
         */
        public String toString() {
            return "Secret key entry with algorithm " + sKey.getAlgorithm();
        }
    }

    /**
     * A {@code KeyStore} entry that holds a trusted
     * {@code Certificate}.
     *
     * @since 1.5
     */
    public static final class TrustedCertificateEntry implements Entry {

        private final Certificate cert;
        private final Set<Attribute> attributes;

        /**
         * Constructs a {@code TrustedCertificateEntry} with a
         * trusted {@code Certificate}.
         *
         * @param trustedCert the trusted {@code Certificate}
         *
         * @exception NullPointerException if
         *      {@code trustedCert} is {@code null}
         */
        public TrustedCertificateEntry(Certificate trustedCert) {
            if (trustedCert == null) {
                throw new NullPointerException("invalid null input");
            }
            this.cert = trustedCert;
            this.attributes = Collections.<Attribute>emptySet();
        }

        /**
         * Constructs a {@code TrustedCertificateEntry} with a
         * trusted {@code Certificate} and associated entry attributes.
         *
         * <p> The specified {@code attributes} is cloned before it is stored
         * in the new {@code TrustedCertificateEntry} object.
         *
         * @param trustedCert the trusted {@code Certificate}
         * @param attributes the attributes
         *
         * @exception NullPointerException if {@code trustedCert} or
         *     {@code attributes} is {@code null}
         *
         * @since 1.8
         */
        public TrustedCertificateEntry(Certificate trustedCert,
           Set<Attribute> attributes) {
            if (trustedCert == null || attributes == null) {
                throw new NullPointerException("invalid null input");
            }
            this.cert = trustedCert;
            this.attributes =
                Collections.unmodifiableSet(new HashSet<>(attributes));
        }

        /**
         * Gets the trusted {@code Certficate} from this entry.
         *
         * @return the trusted {@code Certificate} from this entry
         */
        public Certificate getTrustedCertificate() {
            return cert;
        }

        /**
         * Retrieves the attributes associated with an entry.
         * <p>
         *
         * @return an unmodifiable {@code Set} of attributes, possibly empty
         *
         * @since 1.8
         */
        @Override
        public Set<Attribute> getAttributes() {
            return attributes;
        }

        /**
         * Returns a string representation of this TrustedCertificateEntry.
         * @return a string representation of this TrustedCertificateEntry.
         */
        public String toString() {
            return "Trusted certificate entry:\r\n" + cert.toString();
        }
    }

    /**
     * Creates a KeyStore object of the given type, and encapsulates the given
     * provider implementation (SPI object) in it.
     *
     * @param keyStoreSpi the provider implementation.
     * @param provider the provider.
     * @param type the keystore type.
     */
    protected KeyStore(KeyStoreSpi keyStoreSpi, Provider provider, String type)
    {
        this.keyStoreSpi = keyStoreSpi;
        this.provider = provider;
        this.type = type;
    }

    /**
     * Returns a keystore object of the specified type.
     *
     * <p> This method traverses the list of registered security Providers,
     * starting with the most preferred Provider.
     * A new KeyStore object encapsulating the
     * KeyStoreSpi implementation from the first
     * Provider that supports the specified type is returned.
     *
     * <p> Note that the list of registered providers may be retrieved via
     * the {@link Security#getProviders() Security.getProviders()} method.
     *
     * @param type the type of keystore.
     * See the KeyStore section in the <a href=
     * "{@docRoot}/../technotes/guides/security/StandardNames.html#KeyStore">
     * Java Cryptography Architecture Standard Algorithm Name Documentation</a>
     * for information about standard keystore types.
     *
     * @return a keystore object of the specified type.
     *
     * @exception KeyStoreException if no Provider supports a
     *          KeyStoreSpi implementation for the
     *          specified type.
     *
     * @see Provider
     */
    public static java.security.KeyStore getInstance(String type)
        throws KeyStoreException
    {
        try {
            Object[] objs = Security.getImpl(type, "KeyStore", (String)null);
            return new java.security.KeyStore((KeyStoreSpi)objs[0], (Provider)objs[1], type);
        } catch (NoSuchAlgorithmException nsae) {
            throw new KeyStoreException(type + " not found", nsae);
        } catch (NoSuchProviderException nspe) {
            throw new KeyStoreException(type + " not found", nspe);
        }
    }

    /**
     * Returns a keystore object of the specified type.
     *
     * <p> A new KeyStore object encapsulating the
     * KeyStoreSpi implementation from the specified provider
     * is returned.  The specified provider must be registered
     * in the security provider list.
     *
     * <p> Note that the list of registered providers may be retrieved via
     * the {@link Security#getProviders() Security.getProviders()} method.
     *
     * @param type the type of keystore.
     * See the KeyStore section in the <a href=
     * "{@docRoot}/../technotes/guides/security/StandardNames.html#KeyStore">
     * Java Cryptography Architecture Standard Algorithm Name Documentation</a>
     * for information about standard keystore types.
     *
     * @param provider the name of the provider.
     *
     * @return a keystore object of the specified type.
     *
     * @exception KeyStoreException if a KeyStoreSpi
     *          implementation for the specified type is not
     *          available from the specified provider.
     *
     * @exception NoSuchProviderException if the specified provider is not
     *          registered in the security provider list.
     *
     * @exception IllegalArgumentException if the provider name is null
     *          or empty.
     *
     * @see Provider
     */
    public static java.security.KeyStore getInstance(String type, String provider)
        throws KeyStoreException, NoSuchProviderException
    {
        if (provider == null || provider.length() == 0)
            throw new IllegalArgumentException("missing provider");
        try {
            Object[] objs = Security.getImpl(type, "KeyStore", provider);
            return new java.security.KeyStore((KeyStoreSpi)objs[0], (Provider)objs[1], type);
        } catch (NoSuchAlgorithmException nsae) {
            throw new KeyStoreException(type + " not found", nsae);
        }
    }

    /**
     * Returns a keystore object of the specified type.
     *
     * <p> A new KeyStore object encapsulating the
     * KeyStoreSpi implementation from the specified Provider
     * object is returned.  Note that the specified Provider object
     * does not have to be registered in the provider list.
     *
     * @param type the type of keystore.
     * See the KeyStore section in the <a href=
     * "{@docRoot}/../technotes/guides/security/StandardNames.html#KeyStore">
     * Java Cryptography Architecture Standard Algorithm Name Documentation</a>
     * for information about standard keystore types.
     *
     * @param provider the provider.
     *
     * @return a keystore object of the specified type.
     *
     * @exception KeyStoreException if KeyStoreSpi
     *          implementation for the specified type is not available
     *          from the specified Provider object.
     *
     * @exception IllegalArgumentException if the specified provider is null.
     *
     * @see Provider
     *
     * @since 1.4
     */
    public static java.security.KeyStore getInstance(String type, Provider provider)
        throws KeyStoreException
    {
        if (provider == null)
            throw new IllegalArgumentException("missing provider");
        try {
            Object[] objs = Security.getImpl(type, "KeyStore", provider);
            return new java.security.KeyStore((KeyStoreSpi)objs[0], (Provider)objs[1], type);
        } catch (NoSuchAlgorithmException nsae) {
            throw new KeyStoreException(type + " not found", nsae);
        }
    }

    /**
     * Returns the default keystore type as specified by the
     * {@code keystore.type} security property, or the string
     * {@literal "jks"} (acronym for {@literal "Java keystore"})
     * if no such property exists.
     *
     * <p>The default keystore type can be used by applications that do not
     * want to use a hard-coded keystore type when calling one of the
     * {@code getInstance} methods, and want to provide a default keystore
     * type in case a user does not specify its own.
     *
     * <p>The default keystore type can be changed by setting the value of the
     * {@code keystore.type} security property to the desired keystore type.
     *
     * @return the default keystore type as specified by the
     * {@code keystore.type} security property, or the string {@literal "jks"}
     * if no such property exists.
     * @see Security security properties
     */
    public final static String getDefaultType() {
        String kstype;
        kstype = AccessController.doPrivileged(new PrivilegedAction<String>() {
            public String run() {
                return Security.getProperty(KEYSTORE_TYPE);
            }
        });
        if (kstype == null) {
            kstype = "jks";
        }
        return kstype;
    }

    /**
     * Returns the provider of this keystore.
     *
     * @return the provider of this keystore.
     */
    public final Provider getProvider()
    {
        return this.provider;
    }

    /**
     * Returns the type of this keystore.
     *
     * @return the type of this keystore.
     */
    public final String getType()
    {
        return this.type;
    }

    /**
     * Returns the key associated with the given alias, using the given
     * password to recover it.  The key must have been associated with
     * the alias by a call to {@code setKeyEntry},
     * or by a call to {@code setEntry} with a
     * {@code PrivateKeyEntry} or {@code SecretKeyEntry}.
     *
     * @param alias the alias name
     * @param password the password for recovering the key
     *
     * @return the requested key, or null if the given alias does not exist
     * or does not identify a key-related entry.
     *
     * @exception KeyStoreException if the keystore has not been initialized
     * (loaded).
     * @exception NoSuchAlgorithmException if the algorithm for recovering the
     * key cannot be found
     * @exception UnrecoverableKeyException if the key cannot be recovered
     * (e.g., the given password is wrong).
     */
    public final Key getKey(String alias, char[] password)
        throws KeyStoreException, NoSuchAlgorithmException,
            UnrecoverableKeyException
    {
        if (!initialized) {
            throw new KeyStoreException("Uninitialized keystore");
        }
        return keyStoreSpi.engineGetKey(alias, password);
    }

    /**
     * Returns the certificate chain associated with the given alias.
     * The certificate chain must have been associated with the alias
     * by a call to {@code setKeyEntry},
     * or by a call to {@code setEntry} with a
     * {@code PrivateKeyEntry}.
     *
     * @param alias the alias name
     *
     * @return the certificate chain (ordered with the user's certificate first
     * followed by zero or more certificate authorities), or null if the given alias
     * does not exist or does not contain a certificate chain
     *
     * @exception KeyStoreException if the keystore has not been initialized
     * (loaded).
     */
    public final Certificate[] getCertificateChain(String alias)
        throws KeyStoreException
    {
        if (!initialized) {
            throw new KeyStoreException("Uninitialized keystore");
        }
        return keyStoreSpi.engineGetCertificateChain(alias);
    }

    /**
     * Returns the certificate associated with the given alias.
     *
     * <p> If the given alias name identifies an entry
     * created by a call to {@code setCertificateEntry},
     * or created by a call to {@code setEntry} with a
     * {@code TrustedCertificateEntry},
     * then the trusted certificate contained in that entry is returned.
     *
     * <p> If the given alias name identifies an entry
     * created by a call to {@code setKeyEntry},
     * or created by a call to {@code setEntry} with a
     * {@code PrivateKeyEntry},
     * then the first element of the certificate chain in that entry
     * is returned.
     *
     * @param alias the alias name
     *
     * @return the certificate, or null if the given alias does not exist or
     * does not contain a certificate.
     *
     * @exception KeyStoreException if the keystore has not been initialized
     * (loaded).
     */
    public final Certificate getCertificate(String alias)
        throws KeyStoreException
    {
        if (!initialized) {
            throw new KeyStoreException("Uninitialized keystore");
        }
        return keyStoreSpi.engineGetCertificate(alias);
    }

    /**
     * Returns the creation date of the entry identified by the given alias.
     *
     * @param alias the alias name
     *
     * @return the creation date of this entry, or null if the given alias does
     * not exist
     *
     * @exception KeyStoreException if the keystore has not been initialized
     * (loaded).
     */
    public final Date getCreationDate(String alias)
        throws KeyStoreException
    {
        if (!initialized) {
            throw new KeyStoreException("Uninitialized keystore");
        }
        return keyStoreSpi.engineGetCreationDate(alias);
    }

    /**
     * Assigns the given key to the given alias, protecting it with the given
     * password.
     *
     * <p>If the given key is of type {@code java.security.PrivateKey},
     * it must be accompanied by a certificate chain certifying the
     * corresponding public key.
     *
     * <p>If the given alias already exists, the keystore information
     * associated with it is overridden by the given key (and possibly
     * certificate chain).
     *
     * @param alias the alias name
     * @param key the key to be associated with the alias
     * @param password the password to protect the key
     * @param chain the certificate chain for the corresponding public
     * key (only required if the given key is of type
     * {@code java.security.PrivateKey}).
     *
     * @exception KeyStoreException if the keystore has not been initialized
     * (loaded), the given key cannot be protected, or this operation fails
     * for some other reason
     */
    public final void setKeyEntry(String alias, Key key, char[] password,
                                  Certificate[] chain)
        throws KeyStoreException
    {
        if (!initialized) {
            throw new KeyStoreException("Uninitialized keystore");
        }
        if ((key instanceof PrivateKey) &&
            (chain == null || chain.length == 0)) {
            throw new IllegalArgumentException("Private key must be "
                                               + "accompanied by certificate "
                                               + "chain");
        }
        keyStoreSpi.engineSetKeyEntry(alias, key, password, chain);
    }

    /**
     * Assigns the given key (that has already been protected) to the given
     * alias.
     *
     * <p>If the protected key is of type
     * {@code java.security.PrivateKey}, it must be accompanied by a
     * certificate chain certifying the corresponding public key. If the
     * underlying keystore implementation is of type {@code jks},
     * {@code key} must be encoded as an
     * {@code EncryptedPrivateKeyInfo} as defined in the PKCS #8 standard.
     *
     * <p>If the given alias already exists, the keystore information
     * associated with it is overridden by the given key (and possibly
     * certificate chain).
     *
     * @param alias the alias name
     * @param key the key (in protected format) to be associated with the alias
     * @param chain the certificate chain for the corresponding public
     *          key (only useful if the protected key is of type
     *          {@code java.security.PrivateKey}).
     *
     * @exception KeyStoreException if the keystore has not been initialized
     * (loaded), or if this operation fails for some other reason.
     */
    public final void setKeyEntry(String alias, byte[] key,
                                  Certificate[] chain)
        throws KeyStoreException
    {
        if (!initialized) {
            throw new KeyStoreException("Uninitialized keystore");
        }
        keyStoreSpi.engineSetKeyEntry(alias, key, chain);
    }

    /**
     * Assigns the given trusted certificate to the given alias.
     *
     * <p> If the given alias identifies an existing entry
     * created by a call to {@code setCertificateEntry},
     * or created by a call to {@code setEntry} with a
     * {@code TrustedCertificateEntry},
     * the trusted certificate in the existing entry
     * is overridden by the given certificate.
     *
     * @param alias the alias name
     * @param cert the certificate
     *
     * @exception KeyStoreException if the keystore has not been initialized,
     * or the given alias already exists and does not identify an
     * entry containing a trusted certificate,
     * or this operation fails for some other reason.
     */
    public final void setCertificateEntry(String alias, Certificate cert)
        throws KeyStoreException
    {
        if (!initialized) {
            throw new KeyStoreException("Uninitialized keystore");
        }
        keyStoreSpi.engineSetCertificateEntry(alias, cert);
    }

    /**
     * Deletes the entry identified by the given alias from this keystore.
     *
     * @param alias the alias name
     *
     * @exception KeyStoreException if the keystore has not been initialized,
     * or if the entry cannot be removed.
     */
    public final void deleteEntry(String alias)
        throws KeyStoreException
    {
        if (!initialized) {
            throw new KeyStoreException("Uninitialized keystore");
        }
        keyStoreSpi.engineDeleteEntry(alias);
    }

    /**
     * Lists all the alias names of this keystore.
     *
     * @return enumeration of the alias names
     *
     * @exception KeyStoreException if the keystore has not been initialized
     * (loaded).
     */
    public final Enumeration<String> aliases()
        throws KeyStoreException
    {
        if (!initialized) {
            throw new KeyStoreException("Uninitialized keystore");
        }
        return keyStoreSpi.engineAliases();
    }

    /**
     * Checks if the given alias exists in this keystore.
     *
     * @param alias the alias name
     *
     * @return true if the alias exists, false otherwise
     *
     * @exception KeyStoreException if the keystore has not been initialized
     * (loaded).
     */
    public final boolean containsAlias(String alias)
        throws KeyStoreException
    {
        if (!initialized) {
            throw new KeyStoreException("Uninitialized keystore");
        }
        return keyStoreSpi.engineContainsAlias(alias);
    }

    /**
     * Retrieves the number of entries in this keystore.
     *
     * @return the number of entries in this keystore
     *
     * @exception KeyStoreException if the keystore has not been initialized
     * (loaded).
     */
    public final int size()
        throws KeyStoreException
    {
        if (!initialized) {
            throw new KeyStoreException("Uninitialized keystore");
        }
        return keyStoreSpi.engineSize();
    }

    /**
     * Returns true if the entry identified by the given alias
     * was created by a call to {@code setKeyEntry},
     * or created by a call to {@code setEntry} with a
     * {@code PrivateKeyEntry} or a {@code SecretKeyEntry}.
     *
     * @param alias the alias for the keystore entry to be checked
     *
     * @return true if the entry identified by the given alias is a
     * key-related entry, false otherwise.
     *
     * @exception KeyStoreException if the keystore has not been initialized
     * (loaded).
     */
    public final boolean isKeyEntry(String alias)
        throws KeyStoreException
    {
        if (!initialized) {
            throw new KeyStoreException("Uninitialized keystore");
        }
        return keyStoreSpi.engineIsKeyEntry(alias);
    }

    /**
     * Returns true if the entry identified by the given alias
     * was created by a call to {@code setCertificateEntry},
     * or created by a call to {@code setEntry} with a
     * {@code TrustedCertificateEntry}.
     *
     * @param alias the alias for the keystore entry to be checked
     *
     * @return true if the entry identified by the given alias contains a
     * trusted certificate, false otherwise.
     *
     * @exception KeyStoreException if the keystore has not been initialized
     * (loaded).
     */
    public final boolean isCertificateEntry(String alias)
        throws KeyStoreException
    {
        if (!initialized) {
            throw new KeyStoreException("Uninitialized keystore");
        }
        return keyStoreSpi.engineIsCertificateEntry(alias);
    }

    /**
     * Returns the (alias) name of the first keystore entry whose certificate
     * matches the given certificate.
     *
     * <p> This method attempts to match the given certificate with each
     * keystore entry. If the entry being considered was
     * created by a call to {@code setCertificateEntry},
     * or created by a call to {@code setEntry} with a
     * {@code TrustedCertificateEntry},
     * then the given certificate is compared to that entry's certificate.
     *
     * <p> If the entry being considered was
     * created by a call to {@code setKeyEntry},
     * or created by a call to {@code setEntry} with a
     * {@code PrivateKeyEntry},
     * then the given certificate is compared to the first
     * element of that entry's certificate chain.
     *
     * @param cert the certificate to match with.
     *
     * @return the alias name of the first entry with a matching certificate,
     * or null if no such entry exists in this keystore.
     *
     * @exception KeyStoreException if the keystore has not been initialized
     * (loaded).
     */
    public final String getCertificateAlias(Certificate cert)
        throws KeyStoreException
    {
        if (!initialized) {
            throw new KeyStoreException("Uninitialized keystore");
        }
        return keyStoreSpi.engineGetCertificateAlias(cert);
    }

    /**
     * Stores this keystore to the given output stream, and protects its
     * integrity with the given password.
     *
     * @param stream the output stream to which this keystore is written.
     * @param password the password to generate the keystore integrity check
     *
     * @exception KeyStoreException if the keystore has not been initialized
     * (loaded).
     * @exception IOException if there was an I/O problem with data
     * @exception NoSuchAlgorithmException if the appropriate data integrity
     * algorithm could not be found
     * @exception CertificateException if any of the certificates included in
     * the keystore data could not be stored
     */
    public final void store(OutputStream stream, char[] password)
        throws KeyStoreException, IOException, NoSuchAlgorithmException,
            CertificateException
    {
        if (!initialized) {
            throw new KeyStoreException("Uninitialized keystore");
        }
        keyStoreSpi.engineStore(stream, password);
    }

    /**
     * Stores this keystore using the given {@code LoadStoreParameter}.
     *
     * @param param the {@code LoadStoreParameter}
     *          that specifies how to store the keystore,
     *          which may be {@code null}
     *
     * @exception IllegalArgumentException if the given
     *          {@code LoadStoreParameter}
     *          input is not recognized
     * @exception KeyStoreException if the keystore has not been initialized
     *          (loaded)
     * @exception IOException if there was an I/O problem with data
     * @exception NoSuchAlgorithmException if the appropriate data integrity
     *          algorithm could not be found
     * @exception CertificateException if any of the certificates included in
     *          the keystore data could not be stored
     *
     * @since 1.5
     */
    public final void store(LoadStoreParameter param)
                throws KeyStoreException, IOException,
                NoSuchAlgorithmException, CertificateException {
        if (!initialized) {
            throw new KeyStoreException("Uninitialized keystore");
        }
        keyStoreSpi.engineStore(param);
    }

    /**
     * Loads this KeyStore from the given input stream.
     *
     * <p>A password may be given to unlock the keystore
     * (e.g. the keystore resides on a hardware token device),
     * or to check the integrity of the keystore data.
     * If a password is not given for integrity checking,
     * then integrity checking is not performed.
     *
     * <p>In order to create an empty keystore, or if the keystore cannot
     * be initialized from a stream, pass {@code null}
     * as the {@code stream} argument.
     *
     * <p> Note that if this keystore has already been loaded, it is
     * reinitialized and loaded again from the given input stream.
     *
     * @param stream the input stream from which the keystore is loaded,
     * or {@code null}
     * @param password the password used to check the integrity of
     * the keystore, the password used to unlock the keystore,
     * or {@code null}
     *
     * @exception IOException if there is an I/O or format problem with the
     * keystore data, if a password is required but not given,
     * or if the given password was incorrect. If the error is due to a
     * wrong password, the {@link Throwable#getCause cause} of the
     * {@code IOException} should be an
     * {@code UnrecoverableKeyException}
     * @exception NoSuchAlgorithmException if the algorithm used to check
     * the integrity of the keystore cannot be found
     * @exception CertificateException if any of the certificates in the
     * keystore could not be loaded
     */
    public final void load(InputStream stream, char[] password)
        throws IOException, NoSuchAlgorithmException, CertificateException
    {
        keyStoreSpi.engineLoad(stream, password);
        initialized = true;
    }

    /**
     * Loads this keystore using the given {@code LoadStoreParameter}.
     *
     * <p> Note that if this KeyStore has already been loaded, it is
     * reinitialized and loaded again from the given parameter.
     *
     * @param param the {@code LoadStoreParameter}
     *          that specifies how to load the keystore,
     *          which may be {@code null}
     *
     * @exception IllegalArgumentException if the given
     *          {@code LoadStoreParameter}
     *          input is not recognized
     * @exception IOException if there is an I/O or format problem with the
     *          keystore data. If the error is due to an incorrect
     *         {@code ProtectionParameter} (e.g. wrong password)
     *         the {@link Throwable#getCause cause} of the
     *         {@code IOException} should be an
     *         {@code UnrecoverableKeyException}
     * @exception NoSuchAlgorithmException if the algorithm used to check
     *          the integrity of the keystore cannot be found
     * @exception CertificateException if any of the certificates in the
     *          keystore could not be loaded
     *
     * @since 1.5
     */
    public final void load(LoadStoreParameter param)
                throws IOException, NoSuchAlgorithmException,
                CertificateException {

        keyStoreSpi.engineLoad(param);
        initialized = true;
    }

    /**
     * Gets a keystore {@code Entry} for the specified alias
     * with the specified protection parameter.
     *
     * @param alias get the keystore {@code Entry} for this alias
     * @param protParam the {@code ProtectionParameter}
     *          used to protect the {@code Entry},
     *          which may be {@code null}
     *
     * @return the keystore {@code Entry} for the specified alias,
     *          or {@code null} if there is no such entry
     *
     * @exception NullPointerException if
     *          {@code alias} is {@code null}
     * @exception NoSuchAlgorithmException if the algorithm for recovering the
     *          entry cannot be found
     * @exception UnrecoverableEntryException if the specified
     *          {@code protParam} were insufficient or invalid
     * @exception UnrecoverableKeyException if the entry is a
     *          {@code PrivateKeyEntry} or {@code SecretKeyEntry}
     *          and the specified {@code protParam} does not contain
     *          the information needed to recover the key (e.g. wrong password)
     * @exception KeyStoreException if the keystore has not been initialized
     *          (loaded).
     * @see #setEntry(String, java.security.KeyStore.Entry, java.security.KeyStore.ProtectionParameter)
     *
     * @since 1.5
     */
    public final Entry getEntry(String alias, ProtectionParameter protParam)
                throws NoSuchAlgorithmException, UnrecoverableEntryException,
                KeyStoreException {

        if (alias == null) {
            throw new NullPointerException("invalid null input");
        }
        if (!initialized) {
            throw new KeyStoreException("Uninitialized keystore");
        }
        return keyStoreSpi.engineGetEntry(alias, protParam);
    }

    /**
     * Saves a keystore {@code Entry} under the specified alias.
     * The protection parameter is used to protect the
     * {@code Entry}.
     *
     * <p> If an entry already exists for the specified alias,
     * it is overridden.
     *
     * @param alias save the keystore {@code Entry} under this alias
     * @param entry the {@code Entry} to save
     * @param protParam the {@code ProtectionParameter}
     *          used to protect the {@code Entry},
     *          which may be {@code null}
     *
     * @exception NullPointerException if
     *          {@code alias} or {@code entry}
     *          is {@code null}
     * @exception KeyStoreException if the keystore has not been initialized
     *          (loaded), or if this operation fails for some other reason
     *
     * @see #getEntry(String, java.security.KeyStore.ProtectionParameter)
     *
     * @since 1.5
     */
    public final void setEntry(String alias, Entry entry,
                        ProtectionParameter protParam)
                throws KeyStoreException {
        if (alias == null || entry == null) {
            throw new NullPointerException("invalid null input");
        }
        if (!initialized) {
            throw new KeyStoreException("Uninitialized keystore");
        }
        keyStoreSpi.engineSetEntry(alias, entry, protParam);
    }

    /**
     * Determines if the keystore {@code Entry} for the specified
     * {@code alias} is an instance or subclass of the specified
     * {@code entryClass}.
     *
     * @param alias the alias name
     * @param entryClass the entry class
     *
     * @return true if the keystore {@code Entry} for the specified
     *          {@code alias} is an instance or subclass of the
     *          specified {@code entryClass}, false otherwise
     *
     * @exception NullPointerException if
     *          {@code alias} or {@code entryClass}
     *          is {@code null}
     * @exception KeyStoreException if the keystore has not been
     *          initialized (loaded)
     *
     * @since 1.5
     */
    public final boolean
        entryInstanceOf(String alias,
                        Class<? extends java.security.KeyStore.Entry> entryClass)
        throws KeyStoreException
    {

        if (alias == null || entryClass == null) {
            throw new NullPointerException("invalid null input");
        }
        if (!initialized) {
            throw new KeyStoreException("Uninitialized keystore");
        }
        return keyStoreSpi.engineEntryInstanceOf(alias, entryClass);
    }

    /**
     * A description of a to-be-instantiated KeyStore object.
     *
     * <p>An instance of this class encapsulates the information needed to
     * instantiate and initialize a KeyStore object. That process is
     * triggered when the {@linkplain #getKeyStore} method is called.
     *
     * <p>This makes it possible to decouple configuration from KeyStore
     * object creation and e.g. delay a password prompt until it is
     * needed.
     *
     * @see java.security.KeyStore
     * @see javax.net.ssl.KeyStoreBuilderParameters
     * @since 1.5
     */
    public static abstract class Builder {

        // maximum times to try the callbackhandler if the password is wrong
        static final int MAX_CALLBACK_TRIES = 3;

        /**
         * Construct a new Builder.
         */
        protected Builder() {
            // empty
        }

        /**
         * Returns the KeyStore described by this object.
         *
         * @return the {@code KeyStore} described by this object
         * @exception KeyStoreException if an error occurred during the
         *   operation, for example if the KeyStore could not be
         *   instantiated or loaded
         */
        public abstract java.security.KeyStore getKeyStore() throws KeyStoreException;

        /**
         * Returns the ProtectionParameters that should be used to obtain
         * the {@link java.security.KeyStore.Entry Entry} with the given alias.
         * The {@code getKeyStore} method must be invoked before this
         * method may be called.
         *
         * @return the ProtectionParameters that should be used to obtain
         *   the {@link java.security.KeyStore.Entry Entry} with the given alias.
         * @param alias the alias of the KeyStore entry
         * @throws NullPointerException if alias is null
         * @throws KeyStoreException if an error occurred during the
         *   operation
         * @throws IllegalStateException if the getKeyStore method has
         *   not been invoked prior to calling this method
         */
        public abstract ProtectionParameter getProtectionParameter(String alias)
            throws KeyStoreException;

        /**
         * Returns a new Builder that encapsulates the given KeyStore.
         * The {@linkplain #getKeyStore} method of the returned object
         * will return {@code keyStore}, the {@linkplain
         * #getProtectionParameter getProtectionParameter()} method will
         * return {@code protectionParameters}.
         *
         * <p> This is useful if an existing KeyStore object needs to be
         * used with Builder-based APIs.
         *
         * @return a new Builder object
         * @param keyStore the KeyStore to be encapsulated
         * @param protectionParameter the ProtectionParameter used to
         *   protect the KeyStore entries
         * @throws NullPointerException if keyStore or
         *   protectionParameters is null
         * @throws IllegalArgumentException if the keyStore has not been
         *   initialized
         */
        public static Builder newInstance(final java.security.KeyStore keyStore,
                final ProtectionParameter protectionParameter) {
            if ((keyStore == null) || (protectionParameter == null)) {
                throw new NullPointerException();
            }
            if (keyStore.initialized == false) {
                throw new IllegalArgumentException("KeyStore not initialized");
            }
            return new Builder() {
                private volatile boolean getCalled;

                public java.security.KeyStore getKeyStore() {
                    getCalled = true;
                    return keyStore;
                }

                public ProtectionParameter getProtectionParameter(String alias)
                {
                    if (alias == null) {
                        throw new NullPointerException();
                    }
                    if (getCalled == false) {
                        throw new IllegalStateException
                            ("getKeyStore() must be called first");
                    }
                    return protectionParameter;
                }
            };
        }

        /**
         * Returns a new Builder object.
         *
         * <p>The first call to the {@link #getKeyStore} method on the returned
         * builder will create a KeyStore of type {@code type} and call
         * its {@link java.security.KeyStore#load load()} method.
         * The {@code inputStream} argument is constructed from
         * {@code file}.
         * If {@code protection} is a
         * {@code PasswordProtection}, the password is obtained by
         * calling the {@code getPassword} method.
         * Otherwise, if {@code protection} is a
         * {@code CallbackHandlerProtection}, the password is obtained
         * by invoking the CallbackHandler.
         *
         * <p>Subsequent calls to {@link #getKeyStore} return the same object
         * as the initial call. If the initial call to failed with a
         * KeyStoreException, subsequent calls also throw a
         * KeyStoreException.
         *
         * <p>The KeyStore is instantiated from {@code provider} if
         * non-null. Otherwise, all installed providers are searched.
         *
         * <p>Calls to {@link #getProtectionParameter getProtectionParameter()}
         * will return a {@link java.security.KeyStore.PasswordProtection PasswordProtection}
         * object encapsulating the password that was used to invoke the
         * {@code load} method.
         *
         * <p><em>Note</em> that the {@link #getKeyStore} method is executed
         * within the {@link AccessControlContext} of the code invoking this
         * method.
         *
         * @return a new Builder object
         * @param type the type of KeyStore to be constructed
         * @param provider the provider from which the KeyStore is to
         *   be instantiated (or null)
         * @param file the File that contains the KeyStore data
         * @param protection the ProtectionParameter securing the KeyStore data
         * @throws NullPointerException if type, file or protection is null
         * @throws IllegalArgumentException if protection is not an instance
         *   of either PasswordProtection or CallbackHandlerProtection; or
         *   if file does not exist or does not refer to a normal file
         */
        public static Builder newInstance(String type, Provider provider,
                File file, ProtectionParameter protection) {
            if ((type == null) || (file == null) || (protection == null)) {
                throw new NullPointerException();
            }
            if ((protection instanceof PasswordProtection == false) &&
                (protection instanceof CallbackHandlerProtection == false)) {
                throw new IllegalArgumentException
                ("Protection must be PasswordProtection or " +
                 "CallbackHandlerProtection");
            }
            if (file.isFile() == false) {
                throw new IllegalArgumentException
                    ("File does not exist or it does not refer " +
                     "to a normal file: " + file);
            }
            return new FileBuilder(type, provider, file, protection,
                AccessController.getContext());
        }

        private static final class FileBuilder extends Builder {

            private final String type;
            private final Provider provider;
            private final File file;
            private ProtectionParameter protection;
            private ProtectionParameter keyProtection;
            private final AccessControlContext context;

            private java.security.KeyStore keyStore;

            private Throwable oldException;

            FileBuilder(String type, Provider provider, File file,
                    ProtectionParameter protection,
                    AccessControlContext context) {
                this.type = type;
                this.provider = provider;
                this.file = file;
                this.protection = protection;
                this.context = context;
            }

            public synchronized java.security.KeyStore getKeyStore() throws KeyStoreException
            {
                if (keyStore != null) {
                    return keyStore;
                }
                if (oldException != null) {
                    throw new KeyStoreException
                        ("Previous KeyStore instantiation failed",
                         oldException);
                }
                PrivilegedExceptionAction<java.security.KeyStore> action =
                        new PrivilegedExceptionAction<java.security.KeyStore>() {
                    public java.security.KeyStore run() throws Exception {
                        if (protection instanceof CallbackHandlerProtection == false) {
                            return run0();
                        }
                        // when using a CallbackHandler,
                        // reprompt if the password is wrong
                        int tries = 0;
                        while (true) {
                            tries++;
                            try {
                                return run0();
                            } catch (IOException e) {
                                if ((tries < MAX_CALLBACK_TRIES)
                                        && (e.getCause() instanceof UnrecoverableKeyException)) {
                                    continue;
                                }
                                throw e;
                            }
                        }
                    }
                    public java.security.KeyStore run0() throws Exception {
                        java.security.KeyStore ks;
                        if (provider == null) {
                            ks = java.security.KeyStore.getInstance(type);
                        } else {
                            ks = java.security.KeyStore.getInstance(type, provider);
                        }
                        InputStream in = null;
                        char[] password = null;
                        try {
                            in = new FileInputStream(file);
                            if (protection instanceof PasswordProtection) {
                                password =
                                ((PasswordProtection)protection).getPassword();
                                keyProtection = protection;
                            } else {
                                CallbackHandler handler =
                                    ((CallbackHandlerProtection)protection)
                                    .getCallbackHandler();
                                PasswordCallback callback = new PasswordCallback
                                    ("Password for keystore " + file.getName(),
                                    false);
                                handler.handle(new Callback[] {callback});
                                password = callback.getPassword();
                                if (password == null) {
                                    throw new KeyStoreException("No password" +
                                                                " provided");
                                }
                                callback.clearPassword();
                                keyProtection = new PasswordProtection(password);
                            }
                            ks.load(in, password);
                            return ks;
                        } finally {
                            if (in != null) {
                                in.close();
                            }
                        }
                    }
                };
                try {
                    keyStore = AccessController.doPrivileged(action, context);
                    return keyStore;
                } catch (PrivilegedActionException e) {
                    oldException = e.getCause();
                    throw new KeyStoreException
                        ("KeyStore instantiation failed", oldException);
                }
            }

            public synchronized ProtectionParameter
                        getProtectionParameter(String alias) {
                if (alias == null) {
                    throw new NullPointerException();
                }
                if (keyStore == null) {
                    throw new IllegalStateException
                        ("getKeyStore() must be called first");
                }
                return keyProtection;
            }
        }

        /**
         * Returns a new Builder object.
         *
         * <p>Each call to the {@link #getKeyStore} method on the returned
         * builder will return a new KeyStore object of type {@code type}.
         * Its {@link java.security.KeyStore#load(java.security.KeyStore.LoadStoreParameter) load()}
         * method is invoked using a
         * {@code LoadStoreParameter} that encapsulates
         * {@code protection}.
         *
         * <p>The KeyStore is instantiated from {@code provider} if
         * non-null. Otherwise, all installed providers are searched.
         *
         * <p>Calls to {@link #getProtectionParameter getProtectionParameter()}
         * will return {@code protection}.
         *
         * <p><em>Note</em> that the {@link #getKeyStore} method is executed
         * within the {@link AccessControlContext} of the code invoking this
         * method.
         *
         * @return a new Builder object
         * @param type the type of KeyStore to be constructed
         * @param provider the provider from which the KeyStore is to
         *   be instantiated (or null)
         * @param protection the ProtectionParameter securing the Keystore
         * @throws NullPointerException if type or protection is null
         */
        public static Builder newInstance(final String type,
                final Provider provider, final ProtectionParameter protection) {
            if ((type == null) || (protection == null)) {
                throw new NullPointerException();
            }
            final AccessControlContext context = AccessController.getContext();
            return new Builder() {
                private volatile boolean getCalled;
                private IOException oldException;

                private final PrivilegedExceptionAction<java.security.KeyStore> action
                        = new PrivilegedExceptionAction<java.security.KeyStore>() {

                    public java.security.KeyStore run() throws Exception {
                        java.security.KeyStore ks;
                        if (provider == null) {
                            ks = java.security.KeyStore.getInstance(type);
                        } else {
                            ks = java.security.KeyStore.getInstance(type, provider);
                        }
                        LoadStoreParameter param = new SimpleLoadStoreParameter(protection);
                        if (protection instanceof CallbackHandlerProtection == false) {
                            ks.load(param);
                        } else {
                            // when using a CallbackHandler,
                            // reprompt if the password is wrong
                            int tries = 0;
                            while (true) {
                                tries++;
                                try {
                                    ks.load(param);
                                    break;
                                } catch (IOException e) {
                                    if (e.getCause() instanceof UnrecoverableKeyException) {
                                        if (tries < MAX_CALLBACK_TRIES) {
                                            continue;
                                        } else {
                                            oldException = e;
                                        }
                                    }
                                    throw e;
                                }
                            }
                        }
                        getCalled = true;
                        return ks;
                    }
                };

                public synchronized java.security.KeyStore getKeyStore()
                        throws KeyStoreException {
                    if (oldException != null) {
                        throw new KeyStoreException
                            ("Previous KeyStore instantiation failed",
                             oldException);
                    }
                    try {
                        return AccessController.doPrivileged(action, context);
                    } catch (PrivilegedActionException e) {
                        Throwable cause = e.getCause();
                        throw new KeyStoreException
                            ("KeyStore instantiation failed", cause);
                    }
                }

                public ProtectionParameter getProtectionParameter(String alias)
                {
                    if (alias == null) {
                        throw new NullPointerException();
                    }
                    if (getCalled == false) {
                        throw new IllegalStateException
                            ("getKeyStore() must be called first");
                    }
                    return protection;
                }
            };
        }

    }

    static class SimpleLoadStoreParameter implements LoadStoreParameter {

        private final ProtectionParameter protection;

        SimpleLoadStoreParameter(ProtectionParameter protection) {
            this.protection = protection;
        }

        public ProtectionParameter getProtectionParameter() {
            return protection;
        }
    }

}
