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

import sun.security.jca.GetInstance;
import sun.security.jca.GetInstance.Instance;
import sun.security.jca.JCAUtil;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGeneratorSpi;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Provider.Service;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Iterator;
import java.util.List;

/**
 * The KeyPairGenerator class is used to generate pairs of
 * public and private keys. Key pair generators are constructed using the
 * {@code getInstance} factory methods (static methods that
 * return instances of a given class).
 *
 * <p>A Key pair generator for a particular algorithm creates a public/private
 * key pair that can be used with this algorithm. It also associates
 * algorithm-specific parameters with each of the generated keys.
 *
 * <p>There are two ways to generate a key pair: in an algorithm-independent
 * manner, and in an algorithm-specific manner.
 * The only difference between the two is the initialization of the object:
 *
 * <ul>
 * <li><b>Algorithm-Independent Initialization</b>
 * <p>All key pair generators share the concepts of a keysize and a
 * source of randomness. The keysize is interpreted differently for different
 * algorithms (e.g., in the case of the <i>DSA</i> algorithm, the keysize
 * corresponds to the length of the modulus).
 * There is an
 * {@link #initialize(int, SecureRandom) initialize}
 * method in this KeyPairGenerator class that takes these two universally
 * shared types of arguments. There is also one that takes just a
 * {@code keysize} argument, and uses the {@code SecureRandom}
 * implementation of the highest-priority installed provider as the source
 * of randomness. (If none of the installed providers supply an implementation
 * of {@code SecureRandom}, a system-provided source of randomness is
 * used.)
 *
 * <p>Since no other parameters are specified when you call the above
 * algorithm-independent {@code initialize} methods, it is up to the
 * provider what to do about the algorithm-specific parameters (if any) to be
 * associated with each of the keys.
 *
 * <p>If the algorithm is the <i>DSA</i> algorithm, and the keysize (modulus
 * size) is 512, 768, or 1024, then the <i>Sun</i> provider uses a set of
 * precomputed values for the {@code p}, {@code q}, and
 * {@code g} parameters. If the modulus size is not one of the above
 * values, the <i>Sun</i> provider creates a new set of parameters. Other
 * providers might have precomputed parameter sets for more than just the
 * three modulus sizes mentioned above. Still others might not have a list of
 * precomputed parameters at all and instead always create new parameter sets.
 *
 * <li><b>Algorithm-Specific Initialization</b>
 * <p>For situations where a set of algorithm-specific parameters already
 * exists (e.g., so-called <i>community parameters</i> in DSA), there are two
 * {@link #initialize(AlgorithmParameterSpec)
 * initialize} methods that have an {@code AlgorithmParameterSpec}
 * argument. One also has a {@code SecureRandom} argument, while the
 * the other uses the {@code SecureRandom}
 * implementation of the highest-priority installed provider as the source
 * of randomness. (If none of the installed providers supply an implementation
 * of {@code SecureRandom}, a system-provided source of randomness is
 * used.)
 * </ul>
 *
 * <p>In case the client does not explicitly initialize the KeyPairGenerator
 * (via a call to an {@code initialize} method), each provider must
 * supply (and document) a default initialization.
 * For example, the <i>Sun</i> provider uses a default modulus size (keysize)
 * of 1024 bits.
 *
 * <p>Note that this class is abstract and extends from
 * {@code KeyPairGeneratorSpi} for historical reasons.
 * Application developers should only take notice of the methods defined in
 * this {@code KeyPairGenerator} class; all the methods in
 * the superclass are intended for cryptographic service providers who wish to
 * supply their own implementations of key pair generators.
 *
 * <p> Every implementation of the Java platform is required to support the
 * following standard {@code KeyPairGenerator} algorithms and keysizes in
 * parentheses:
 * <ul>
 * <li>{@code DiffieHellman} (1024)</li>
 * <li>{@code DSA} (1024)</li>
 * <li>{@code RSA} (1024, 2048)</li>
 * </ul>
 * These algorithms are described in the <a href=
 * "{@docRoot}/../technotes/guides/security/StandardNames.html#KeyPairGenerator">
 * KeyPairGenerator section</a> of the
 * Java Cryptography Architecture Standard Algorithm Name Documentation.
 * Consult the release documentation for your implementation to see if any
 * other algorithms are supported.
 *
 * @author Benjamin Renaud
 *
 * @see AlgorithmParameterSpec
 */

public abstract class KeyPairGenerator extends KeyPairGeneratorSpi {

    private final String algorithm;

    // The provider
    Provider provider;

    /**
     * Creates a KeyPairGenerator object for the specified algorithm.
     *
     * @param algorithm the standard string name of the algorithm.
     * See the KeyPairGenerator section in the <a href=
     * "{@docRoot}/../technotes/guides/security/StandardNames.html#KeyPairGenerator">
     * Java Cryptography Architecture Standard Algorithm Name Documentation</a>
     * for information about standard algorithm names.
     */
    protected KeyPairGenerator(String algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * Returns the standard name of the algorithm for this key pair generator.
     * See the KeyPairGenerator section in the <a href=
     * "{@docRoot}/../technotes/guides/security/StandardNames.html#KeyPairGenerator">
     * Java Cryptography Architecture Standard Algorithm Name Documentation</a>
     * for information about standard algorithm names.
     *
     * @return the standard string name of the algorithm.
     */
    public String getAlgorithm() {
        return this.algorithm;
    }

    private static java.security.KeyPairGenerator getInstance(Instance instance,
                                                              String algorithm) {
        java.security.KeyPairGenerator kpg;
        if (instance.impl instanceof java.security.KeyPairGenerator) {
            kpg = (java.security.KeyPairGenerator)instance.impl;
        } else {
            KeyPairGeneratorSpi spi = (KeyPairGeneratorSpi)instance.impl;
            kpg = new Delegate(spi, algorithm);
        }
        kpg.provider = instance.provider;
        return kpg;
    }

    /**
     * Returns a KeyPairGenerator object that generates public/private
     * key pairs for the specified algorithm.
     *
     * <p> This method traverses the list of registered security Providers,
     * starting with the most preferred Provider.
     * A new KeyPairGenerator object encapsulating the
     * KeyPairGeneratorSpi implementation from the first
     * Provider that supports the specified algorithm is returned.
     *
     * <p> Note that the list of registered providers may be retrieved via
     * the {@link Security#getProviders() Security.getProviders()} method.
     *
     * @param algorithm the standard string name of the algorithm.
     * See the KeyPairGenerator section in the <a href=
     * "{@docRoot}/../technotes/guides/security/StandardNames.html#KeyPairGenerator">
     * Java Cryptography Architecture Standard Algorithm Name Documentation</a>
     * for information about standard algorithm names.
     *
     * @return the new KeyPairGenerator object.
     *
     * @exception NoSuchAlgorithmException if no Provider supports a
     *          KeyPairGeneratorSpi implementation for the
     *          specified algorithm.
     *
     * @see Provider
     */
    public static java.security.KeyPairGenerator getInstance(String algorithm)
            throws NoSuchAlgorithmException {
        List<Service> list =
                GetInstance.getServices("KeyPairGenerator", algorithm);
        Iterator<Service> t = list.iterator();
        if (t.hasNext() == false) {
            throw new NoSuchAlgorithmException
                (algorithm + " KeyPairGenerator not available");
        }
        // find a working Spi or KeyPairGenerator subclass
        NoSuchAlgorithmException failure = null;
        do {
            Service s = t.next();
            try {
                Instance instance =
                    GetInstance.getInstance(s, KeyPairGeneratorSpi.class);
                if (instance.impl instanceof java.security.KeyPairGenerator) {
                    return getInstance(instance, algorithm);
                } else {
                    return new Delegate(instance, t, algorithm);
                }
            } catch (NoSuchAlgorithmException e) {
                if (failure == null) {
                    failure = e;
                }
            }
        } while (t.hasNext());
        throw failure;
    }

    /**
     * Returns a KeyPairGenerator object that generates public/private
     * key pairs for the specified algorithm.
     *
     * <p> A new KeyPairGenerator object encapsulating the
     * KeyPairGeneratorSpi implementation from the specified provider
     * is returned.  The specified provider must be registered
     * in the security provider list.
     *
     * <p> Note that the list of registered providers may be retrieved via
     * the {@link Security#getProviders() Security.getProviders()} method.
     *
     * @param algorithm the standard string name of the algorithm.
     * See the KeyPairGenerator section in the <a href=
     * "{@docRoot}/../technotes/guides/security/StandardNames.html#KeyPairGenerator">
     * Java Cryptography Architecture Standard Algorithm Name Documentation</a>
     * for information about standard algorithm names.
     *
     * @param provider the string name of the provider.
     *
     * @return the new KeyPairGenerator object.
     *
     * @exception NoSuchAlgorithmException if a KeyPairGeneratorSpi
     *          implementation for the specified algorithm is not
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
    public static java.security.KeyPairGenerator getInstance(String algorithm,
                                                             String provider)
            throws NoSuchAlgorithmException, NoSuchProviderException {
        Instance instance = GetInstance.getInstance("KeyPairGenerator",
                KeyPairGeneratorSpi.class, algorithm, provider);
        return getInstance(instance, algorithm);
    }

    /**
     * Returns a KeyPairGenerator object that generates public/private
     * key pairs for the specified algorithm.
     *
     * <p> A new KeyPairGenerator object encapsulating the
     * KeyPairGeneratorSpi implementation from the specified Provider
     * object is returned.  Note that the specified Provider object
     * does not have to be registered in the provider list.
     *
     * @param algorithm the standard string name of the algorithm.
     * See the KeyPairGenerator section in the <a href=
     * "{@docRoot}/../technotes/guides/security/StandardNames.html#KeyPairGenerator">
     * Java Cryptography Architecture Standard Algorithm Name Documentation</a>
     * for information about standard algorithm names.
     *
     * @param provider the provider.
     *
     * @return the new KeyPairGenerator object.
     *
     * @exception NoSuchAlgorithmException if a KeyPairGeneratorSpi
     *          implementation for the specified algorithm is not available
     *          from the specified Provider object.
     *
     * @exception IllegalArgumentException if the specified provider is null.
     *
     * @see Provider
     *
     * @since 1.4
     */
    public static java.security.KeyPairGenerator getInstance(String algorithm,
                                                             Provider provider) throws NoSuchAlgorithmException {
        Instance instance = GetInstance.getInstance("KeyPairGenerator",
                KeyPairGeneratorSpi.class, algorithm, provider);
        return getInstance(instance, algorithm);
    }

    /**
     * Returns the provider of this key pair generator object.
     *
     * @return the provider of this key pair generator object
     */
    public final Provider getProvider() {
        disableFailover();
        return this.provider;
    }

    void disableFailover() {
        // empty, overridden in Delegate
    }

    /**
     * Initializes the key pair generator for a certain keysize using
     * a default parameter set and the {@code SecureRandom}
     * implementation of the highest-priority installed provider as the source
     * of randomness.
     * (If none of the installed providers supply an implementation of
     * {@code SecureRandom}, a system-provided source of randomness is
     * used.)
     *
     * @param keysize the keysize. This is an
     * algorithm-specific metric, such as modulus length, specified in
     * number of bits.
     *
     * @exception InvalidParameterException if the {@code keysize} is not
     * supported by this KeyPairGenerator object.
     */
    public void initialize(int keysize) {
        initialize(keysize, JCAUtil.getSecureRandom());
    }

    /**
     * Initializes the key pair generator for a certain keysize with
     * the given source of randomness (and a default parameter set).
     *
     * @param keysize the keysize. This is an
     * algorithm-specific metric, such as modulus length, specified in
     * number of bits.
     * @param random the source of randomness.
     *
     * @exception InvalidParameterException if the {@code keysize} is not
     * supported by this KeyPairGenerator object.
     *
     * @since 1.2
     */
    public void initialize(int keysize, SecureRandom random) {
        // This does nothing, because either
        // 1. the implementation object returned by getInstance() is an
        //    instance of KeyPairGenerator which has its own
        //    initialize(keysize, random) method, so the application would
        //    be calling that method directly, or
        // 2. the implementation returned by getInstance() is an instance
        //    of Delegate, in which case initialize(keysize, random) is
        //    overridden to call the corresponding SPI method.
        // (This is a special case, because the API and SPI method have the
        // same name.)
    }

    /**
     * Initializes the key pair generator using the specified parameter
     * set and the {@code SecureRandom}
     * implementation of the highest-priority installed provider as the source
     * of randomness.
     * (If none of the installed providers supply an implementation of
     * {@code SecureRandom}, a system-provided source of randomness is
     * used.).
     *
     * <p>This concrete method has been added to this previously-defined
     * abstract class.
     * This method calls the KeyPairGeneratorSpi
     * {@link KeyPairGeneratorSpi#initialize(
     * AlgorithmParameterSpec,
     * SecureRandom) initialize} method,
     * passing it {@code params} and a source of randomness (obtained
     * from the highest-priority installed provider or system-provided if none
     * of the installed providers supply one).
     * That {@code initialize} method always throws an
     * UnsupportedOperationException if it is not overridden by the provider.
     *
     * @param params the parameter set used to generate the keys.
     *
     * @exception InvalidAlgorithmParameterException if the given parameters
     * are inappropriate for this key pair generator.
     *
     * @since 1.2
     */
    public void initialize(AlgorithmParameterSpec params)
            throws InvalidAlgorithmParameterException {
        initialize(params, JCAUtil.getSecureRandom());
    }

    /**
     * Initializes the key pair generator with the given parameter
     * set and source of randomness.
     *
     * <p>This concrete method has been added to this previously-defined
     * abstract class.
     * This method calls the KeyPairGeneratorSpi {@link
     * KeyPairGeneratorSpi#initialize(
     * AlgorithmParameterSpec,
     * SecureRandom) initialize} method,
     * passing it {@code params} and {@code random}.
     * That {@code initialize}
     * method always throws an
     * UnsupportedOperationException if it is not overridden by the provider.
     *
     * @param params the parameter set used to generate the keys.
     * @param random the source of randomness.
     *
     * @exception InvalidAlgorithmParameterException if the given parameters
     * are inappropriate for this key pair generator.
     *
     * @since 1.2
     */
    public void initialize(AlgorithmParameterSpec params,
                           SecureRandom random)
        throws InvalidAlgorithmParameterException
    {
        // This does nothing, because either
        // 1. the implementation object returned by getInstance() is an
        //    instance of KeyPairGenerator which has its own
        //    initialize(params, random) method, so the application would
        //    be calling that method directly, or
        // 2. the implementation returned by getInstance() is an instance
        //    of Delegate, in which case initialize(params, random) is
        //    overridden to call the corresponding SPI method.
        // (This is a special case, because the API and SPI method have the
        // same name.)
    }

    /**
     * Generates a key pair.
     *
     * <p>If this KeyPairGenerator has not been initialized explicitly,
     * provider-specific defaults will be used for the size and other
     * (algorithm-specific) values of the generated keys.
     *
     * <p>This will generate a new key pair every time it is called.
     *
     * <p>This method is functionally equivalent to
     * {@link #generateKeyPair() generateKeyPair}.
     *
     * @return the generated key pair
     *
     * @since 1.2
     */
    public final KeyPair genKeyPair() {
        return generateKeyPair();
    }

    /**
     * Generates a key pair.
     *
     * <p>If this KeyPairGenerator has not been initialized explicitly,
     * provider-specific defaults will be used for the size and other
     * (algorithm-specific) values of the generated keys.
     *
     * <p>This will generate a new key pair every time it is called.
     *
     * <p>This method is functionally equivalent to
     * {@link #genKeyPair() genKeyPair}.
     *
     * @return the generated key pair
     */
    public KeyPair generateKeyPair() {
        // This does nothing (except returning null), because either:
        //
        // 1. the implementation object returned by getInstance() is an
        //    instance of KeyPairGenerator which has its own implementation
        //    of generateKeyPair (overriding this one), so the application
        //    would be calling that method directly, or
        //
        // 2. the implementation returned by getInstance() is an instance
        //    of Delegate, in which case generateKeyPair is
        //    overridden to invoke the corresponding SPI method.
        //
        // (This is a special case, because in JDK 1.1.x the generateKeyPair
        // method was used both as an API and a SPI method.)
        return null;
    }


    /*
     * The following class allows providers to extend from KeyPairGeneratorSpi
     * rather than from KeyPairGenerator. It represents a KeyPairGenerator
     * with an encapsulated, provider-supplied SPI object (of type
     * KeyPairGeneratorSpi).
     * If the provider implementation is an instance of KeyPairGeneratorSpi,
     * the getInstance() methods above return an instance of this class, with
     * the SPI object encapsulated.
     *
     * Note: All SPI methods from the original KeyPairGenerator class have been
     * moved up the hierarchy into a new class (KeyPairGeneratorSpi), which has
     * been interposed in the hierarchy between the API (KeyPairGenerator)
     * and its original parent (Object).
     */

    //
    // error failover notes:
    //
    //  . we failover if the implementation throws an error during init
    //    by retrying the init on other providers
    //
    //  . we also failover if the init succeeded but the subsequent call
    //    to generateKeyPair() fails. In order for this to work, we need
    //    to remember the parameters to the last successful call to init
    //    and initialize() the next spi using them.
    //
    //  . although not specified, KeyPairGenerators could be thread safe,
    //    so we make sure we do not interfere with that
    //
    //  . failover is not available, if:
    //    . getInstance(algorithm, provider) was used
    //    . a provider extends KeyPairGenerator rather than
    //      KeyPairGeneratorSpi (JDK 1.1 style)
    //    . once getProvider() is called
    //

    private static final class Delegate extends java.security.KeyPairGenerator {

        // The provider implementation (delegate)
        private volatile KeyPairGeneratorSpi spi;

        private final Object lock = new Object();

        private Iterator<Service> serviceIterator;

        private final static int I_NONE   = 1;
        private final static int I_SIZE   = 2;
        private final static int I_PARAMS = 3;

        private int initType;
        private int initKeySize;
        private AlgorithmParameterSpec initParams;
        private SecureRandom initRandom;

        // constructor
        Delegate(KeyPairGeneratorSpi spi, String algorithm) {
            super(algorithm);
            this.spi = spi;
        }

        Delegate(Instance instance, Iterator<Service> serviceIterator,
                String algorithm) {
            super(algorithm);
            spi = (KeyPairGeneratorSpi)instance.impl;
            provider = instance.provider;
            this.serviceIterator = serviceIterator;
            initType = I_NONE;
        }

        /**
         * Update the active spi of this class and return the next
         * implementation for failover. If no more implemenations are
         * available, this method returns null. However, the active spi of
         * this class is never set to null.
         */
        private KeyPairGeneratorSpi nextSpi(KeyPairGeneratorSpi oldSpi,
                boolean reinit) {
            synchronized (lock) {
                // somebody else did a failover concurrently
                // try that spi now
                if ((oldSpi != null) && (oldSpi != spi)) {
                    return spi;
                }
                if (serviceIterator == null) {
                    return null;
                }
                while (serviceIterator.hasNext()) {
                    Service s = serviceIterator.next();
                    try {
                        Object inst = s.newInstance(null);
                        // ignore non-spis
                        if (inst instanceof KeyPairGeneratorSpi == false) {
                            continue;
                        }
                        if (inst instanceof java.security.KeyPairGenerator) {
                            continue;
                        }
                        KeyPairGeneratorSpi spi = (KeyPairGeneratorSpi)inst;
                        if (reinit) {
                            if (initType == I_SIZE) {
                                spi.initialize(initKeySize, initRandom);
                            } else if (initType == I_PARAMS) {
                                spi.initialize(initParams, initRandom);
                            } else if (initType != I_NONE) {
                                throw new AssertionError
                                    ("KeyPairGenerator initType: " + initType);
                            }
                        }
                        provider = s.getProvider();
                        this.spi = spi;
                        return spi;
                    } catch (Exception e) {
                        // ignore
                    }
                }
                disableFailover();
                return null;
            }
        }

        void disableFailover() {
            serviceIterator = null;
            initType = 0;
            initParams = null;
            initRandom = null;
        }

        // engine method
        public void initialize(int keysize, SecureRandom random) {
            if (serviceIterator == null) {
                spi.initialize(keysize, random);
                return;
            }
            RuntimeException failure = null;
            KeyPairGeneratorSpi mySpi = spi;
            do {
                try {
                    mySpi.initialize(keysize, random);
                    initType = I_SIZE;
                    initKeySize = keysize;
                    initParams = null;
                    initRandom = random;
                    return;
                } catch (RuntimeException e) {
                    if (failure == null) {
                        failure = e;
                    }
                    mySpi = nextSpi(mySpi, false);
                }
            } while (mySpi != null);
            throw failure;
        }

        // engine method
        public void initialize(AlgorithmParameterSpec params,
                SecureRandom random) throws InvalidAlgorithmParameterException {
            if (serviceIterator == null) {
                spi.initialize(params, random);
                return;
            }
            Exception failure = null;
            KeyPairGeneratorSpi mySpi = spi;
            do {
                try {
                    mySpi.initialize(params, random);
                    initType = I_PARAMS;
                    initKeySize = 0;
                    initParams = params;
                    initRandom = random;
                    return;
                } catch (Exception e) {
                    if (failure == null) {
                        failure = e;
                    }
                    mySpi = nextSpi(mySpi, false);
                }
            } while (mySpi != null);
            if (failure instanceof RuntimeException) {
                throw (RuntimeException)failure;
            }
            // must be an InvalidAlgorithmParameterException
            throw (InvalidAlgorithmParameterException)failure;
        }

        // engine method
        public KeyPair generateKeyPair() {
            if (serviceIterator == null) {
                return spi.generateKeyPair();
            }
            RuntimeException failure = null;
            KeyPairGeneratorSpi mySpi = spi;
            do {
                try {
                    return mySpi.generateKeyPair();
                } catch (RuntimeException e) {
                    if (failure == null) {
                        failure = e;
                    }
                    mySpi = nextSpi(mySpi, true);
                }
            } while (mySpi != null);
            throw failure;
        }
    }

}
