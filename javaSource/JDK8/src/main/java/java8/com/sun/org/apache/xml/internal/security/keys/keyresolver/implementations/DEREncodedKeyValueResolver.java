/*
 * Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java8.com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.content.DEREncodedKeyValue;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverException;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import com.sun.org.apache.xml.internal.security.utils.Constants;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import org.w3c.dom.Element;

import javax.crypto.SecretKey;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

/**
 * KeyResolverSpi implementation which resolves public keys from a
 * <code>dsig11:DEREncodedKeyValue</code> element.
 *
 * @author Brent Putman (putmanb@georgetown.edu)
 */
public class DEREncodedKeyValueResolver extends KeyResolverSpi {

    /** {@link org.apache.commons.logging} logging facility */
    private static java.util.logging.Logger log =
        java.util.logging.Logger.getLogger(com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.DEREncodedKeyValueResolver.class.getName());

    /** {@inheritDoc}. */
    public boolean engineCanResolve(Element element, String baseURI, StorageResolver storage) {
        return XMLUtils.elementIsInSignature11Space(element, Constants._TAG_DERENCODEDKEYVALUE);
    }

    /** {@inheritDoc}. */
    public PublicKey engineLookupAndResolvePublicKey(Element element, String baseURI, StorageResolver storage)
        throws KeyResolverException {

        if (log.isLoggable(java.util.logging.Level.FINE)) {
            log.log(java.util.logging.Level.FINE, "Can I resolve " + element.getTagName());
        }

        if (!engineCanResolve(element, baseURI, storage)) {
            return null;
        }

        try {
            DEREncodedKeyValue derKeyValue = new DEREncodedKeyValue(element, baseURI);
            return derKeyValue.getPublicKey();
        } catch (XMLSecurityException e) {
            if (log.isLoggable(java.util.logging.Level.FINE)) {
                log.log(java.util.logging.Level.FINE, "XMLSecurityException", e);
            }
        }

        return null;
    }

    /** {@inheritDoc}. */
    public X509Certificate engineLookupResolveX509Certificate(Element element, String baseURI, StorageResolver storage)
        throws KeyResolverException {
        return null;
    }

    /** {@inheritDoc}. */
    public SecretKey engineLookupAndResolveSecretKey(Element element, String baseURI, StorageResolver storage)
        throws KeyResolverException {
        return null;
    }

    /** {@inheritDoc}. */
    public PrivateKey engineLookupAndResolvePrivateKey(Element element, String baseURI, StorageResolver storage)
        throws KeyResolverException {
        return null;
    }



}
