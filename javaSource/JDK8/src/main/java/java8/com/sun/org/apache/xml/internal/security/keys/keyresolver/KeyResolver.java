/*
 * Copyright (c) 2007-2013, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package java8.sun.org.apache.xml.internal.security.keys.keyresolver;

import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverException;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.*;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.crypto.SecretKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * KeyResolver is factory class for subclass of KeyResolverSpi that
 * represent child element of KeyInfo.
 */
public class KeyResolver {

    /** {@link org.apache.commons.logging} logging facility */
    private static java.util.logging.Logger log =
        java.util.logging.Logger.getLogger(com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolver.class.getName());

    /** Field resolverVector */
    private static List<com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolver> resolverVector = new CopyOnWriteArrayList<com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolver>();

    /** Field resolverSpi */
    private final KeyResolverSpi resolverSpi;

    /**
     * Constructor.
     *
     * @param keyResolverSpi a KeyResolverSpi instance
     */
    private KeyResolver(KeyResolverSpi keyResolverSpi) {
        resolverSpi = keyResolverSpi;
    }

    /**
     * Method length
     *
     * @return the length of resolvers registered
     */
    public static int length() {
        return resolverVector.size();
    }

    /**
     * Method getX509Certificate
     *
     * @param element
     * @param baseURI
     * @param storage
     * @return The certificate represented by the element.
     *
     * @throws KeyResolverException
     */
    public static final X509Certificate getX509Certificate(
        Element element, String baseURI, StorageResolver storage
    ) throws KeyResolverException {
        for (com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolver resolver : resolverVector) {
            if (resolver == null) {
                Object exArgs[] = {
                                   (((element != null)
                                       && (element.getNodeType() == Node.ELEMENT_NODE))
                                       ? element.getTagName() : "null")
                };

                throw new KeyResolverException("utils.resolver.noClass", exArgs);
            }
            if (log.isLoggable(java.util.logging.Level.FINE)) {
                log.log(java.util.logging.Level.FINE, "check resolvability by class " + resolver.getClass());
            }

            X509Certificate cert = resolver.resolveX509Certificate(element, baseURI, storage);
            if (cert != null) {
                return cert;
            }
        }

        Object exArgs[] = {
                           (((element != null) && (element.getNodeType() == Node.ELEMENT_NODE))
                           ? element.getTagName() : "null")
                          };

        throw new KeyResolverException("utils.resolver.noClass", exArgs);
    }

    /**
     * Method getPublicKey
     *
     * @param element
     * @param baseURI
     * @param storage
     * @return the public key contained in the element
     *
     * @throws KeyResolverException
     */
    public static final PublicKey getPublicKey(
        Element element, String baseURI, StorageResolver storage
    ) throws KeyResolverException {
        for (com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolver resolver : resolverVector) {
            if (resolver == null) {
                Object exArgs[] = {
                                   (((element != null)
                                       && (element.getNodeType() == Node.ELEMENT_NODE))
                                       ? element.getTagName() : "null")
                };

                throw new KeyResolverException("utils.resolver.noClass", exArgs);
            }
            if (log.isLoggable(java.util.logging.Level.FINE)) {
                log.log(java.util.logging.Level.FINE, "check resolvability by class " + resolver.getClass());
            }

            PublicKey cert = resolver.resolvePublicKey(element, baseURI, storage);
            if (cert != null) {
                return cert;
            }
        }

        Object exArgs[] = {
                           (((element != null) && (element.getNodeType() == Node.ELEMENT_NODE))
                           ? element.getTagName() : "null")
                          };

        throw new KeyResolverException("utils.resolver.noClass", exArgs);
    }

    /**
     * This method is used for registering {@link KeyResolverSpi}s which are
     * available to <I>all</I> {@link com.sun.org.apache.xml.internal.security.keys.KeyInfo} objects. This means that
     * personalized {@link KeyResolverSpi}s should only be registered directly
     * to the {@link com.sun.org.apache.xml.internal.security.keys.KeyInfo} using
     * {@link com.sun.org.apache.xml.internal.security.keys.KeyInfo#registerInternalKeyResolver}.
     * Please note that this method will create a new copy of the underlying array, as the
     * underlying collection is a CopyOnWriteArrayList.
     *
     * @param className
     * @param globalResolver Whether the KeyResolverSpi is a global resolver or not
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    public static void register(String className, boolean globalResolver)
        throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        KeyResolverSpi keyResolverSpi =
            (KeyResolverSpi) Class.forName(className).newInstance();
        keyResolverSpi.setGlobalResolver(globalResolver);
        register(keyResolverSpi, false);
    }

    /**
     * This method is used for registering {@link KeyResolverSpi}s which are
     * available to <I>all</I> {@link com.sun.org.apache.xml.internal.security.keys.KeyInfo} objects. This means that
     * personalized {@link KeyResolverSpi}s should only be registered directly
     * to the {@link com.sun.org.apache.xml.internal.security.keys.KeyInfo} using
     * {@link com.sun.org.apache.xml.internal.security.keys.KeyInfo#registerInternalKeyResolver}.
     * Please note that this method will create a new copy of the underlying array, as the
     * underlying collection is a CopyOnWriteArrayList.
     *
     * @param className
     * @param globalResolver Whether the KeyResolverSpi is a global resolver or not
     */
    public static void registerAtStart(String className, boolean globalResolver) {
        KeyResolverSpi keyResolverSpi = null;
        Exception ex = null;
        try {
            keyResolverSpi = (KeyResolverSpi) Class.forName(className).newInstance();
        } catch (ClassNotFoundException e) {
            ex = e;
        } catch (IllegalAccessException e) {
            ex = e;
        } catch (InstantiationException e) {
            ex = e;
        }

        if (ex != null) {
            throw (IllegalArgumentException) new
            IllegalArgumentException("Invalid KeyResolver class name").initCause(ex);
        }
        keyResolverSpi.setGlobalResolver(globalResolver);
        register(keyResolverSpi, true);
    }

    /**
     * This method is used for registering {@link KeyResolverSpi}s which are
     * available to <I>all</I> {@link com.sun.org.apache.xml.internal.security.keys.KeyInfo} objects. This means that
     * personalized {@link KeyResolverSpi}s should only be registered directly
     * to the {@link com.sun.org.apache.xml.internal.security.keys.KeyInfo} using
     * {@link com.sun.org.apache.xml.internal.security.keys.KeyInfo#registerInternalKeyResolver}.
     * Please note that this method will create a new copy of the underlying array, as the
     * underlying collection is a CopyOnWriteArrayList.
     *
     * @param keyResolverSpi a KeyResolverSpi instance to register
     * @param start whether to register the KeyResolverSpi at the start of the list or not
     */
    public static void register(
        KeyResolverSpi keyResolverSpi,
        boolean start
    ) {
        com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolver resolver = new com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolver(keyResolverSpi);
        if (start) {
            resolverVector.add(0, resolver);
        } else {
            resolverVector.add(resolver);
        }
    }

    /**
     * This method is used for registering {@link KeyResolverSpi}s which are
     * available to <I>all</I> {@link com.sun.org.apache.xml.internal.security.keys.KeyInfo} objects. This means that
     * personalized {@link KeyResolverSpi}s should only be registered directly
     * to the {@link com.sun.org.apache.xml.internal.security.keys.KeyInfo} using
     * {@link com.sun.org.apache.xml.internal.security.keys.KeyInfo#registerInternalKeyResolver}.
     * The KeyResolverSpi instances are not registered as a global resolver.
     *
     *
     * @param classNames
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    public static void registerClassNames(List<String> classNames)
        throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        List<com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolver> keyResolverList = new ArrayList<com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolver>(classNames.size());
        for (String className : classNames) {
            KeyResolverSpi keyResolverSpi =
                (KeyResolverSpi) Class.forName(className).newInstance();
            keyResolverSpi.setGlobalResolver(false);
            keyResolverList.add(new com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolver(keyResolverSpi));
        }
        resolverVector.addAll(keyResolverList);
    }

    /**
     * This method registers the default resolvers.
     */
    public static void registerDefaultResolvers() {

        List<com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolver> keyResolverList = new ArrayList<com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolver>();
        keyResolverList.add(new com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolver(new RSAKeyValueResolver()));
        keyResolverList.add(new com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolver(new DSAKeyValueResolver()));
        keyResolverList.add(new com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolver(new X509CertificateResolver()));
        keyResolverList.add(new com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolver(new X509SKIResolver()));
        keyResolverList.add(new com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolver(new RetrievalMethodResolver()));
        keyResolverList.add(new com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolver(new X509SubjectNameResolver()));
        keyResolverList.add(new com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolver(new X509IssuerSerialResolver()));
        keyResolverList.add(new com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolver(new DEREncodedKeyValueResolver()));
        keyResolverList.add(new com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolver(new KeyInfoReferenceResolver()));
        keyResolverList.add(new com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolver(new X509DigestResolver()));

        resolverVector.addAll(keyResolverList);
    }

    /**
     * Method resolvePublicKey
     *
     * @param element
     * @param baseURI
     * @param storage
     * @return resolved public key from the registered from the elements
     *
     * @throws KeyResolverException
     */
    public PublicKey resolvePublicKey(
        Element element, String baseURI, StorageResolver storage
    ) throws KeyResolverException {
        return resolverSpi.engineLookupAndResolvePublicKey(element, baseURI, storage);
    }

    /**
     * Method resolveX509Certificate
     *
     * @param element
     * @param baseURI
     * @param storage
     * @return resolved X509certificate key from the registered from the elements
     *
     * @throws KeyResolverException
     */
    public X509Certificate resolveX509Certificate(
        Element element, String baseURI, StorageResolver storage
    ) throws KeyResolverException {
        return resolverSpi.engineLookupResolveX509Certificate(element, baseURI, storage);
    }

    /**
     * @param element
     * @param baseURI
     * @param storage
     * @return resolved SecretKey key from the registered from the elements
     * @throws KeyResolverException
     */
    public SecretKey resolveSecretKey(
        Element element, String baseURI, StorageResolver storage
    ) throws KeyResolverException {
        return resolverSpi.engineLookupAndResolveSecretKey(element, baseURI, storage);
    }

    /**
     * Method setProperty
     *
     * @param key
     * @param value
     */
    public void setProperty(String key, String value) {
        resolverSpi.engineSetProperty(key, value);
    }

    /**
     * Method getProperty
     *
     * @param key
     * @return the property set for this resolver
     */
    public String getProperty(String key) {
        return resolverSpi.engineGetProperty(key);
    }


    /**
     * Method understandsProperty
     *
     * @param propertyToTest
     * @return true if the resolver understands property propertyToTest
     */
    public boolean understandsProperty(String propertyToTest) {
        return resolverSpi.understandsProperty(propertyToTest);
    }


    /**
     * Method resolverClassName
     *
     * @return the name of the resolver.
     */
    public String resolverClassName() {
        return resolverSpi.getClass().getName();
    }

    /**
     * Iterate over the KeyResolverSpi instances
     */
    static class ResolverIterator implements Iterator<KeyResolverSpi> {
        List<com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolver> res;
        Iterator<com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolver> it;

        public ResolverIterator(List<com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolver> list) {
            res = list;
            it = res.iterator();
        }

        public boolean hasNext() {
            return it.hasNext();
        }

        public KeyResolverSpi next() {
            com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolver resolver = it.next();
            if (resolver == null) {
                throw new RuntimeException("utils.resolver.noClass");
            }

            return resolver.resolverSpi;
        }

        public void remove() {
            throw new UnsupportedOperationException("Can't remove resolvers using the iterator");
        }
    };

    public static Iterator<KeyResolverSpi> iterator() {
        return new ResolverIterator(resolverVector);
    }
}
