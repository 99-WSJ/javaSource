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
package java8.sun.org.apache.xml.internal.security.keys.content.keyvalues;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.content.keyvalues.KeyValueContent;
import com.sun.org.apache.xml.internal.security.utils.Constants;
import com.sun.org.apache.xml.internal.security.utils.I18n;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

public class RSAKeyValue extends SignatureElementProxy implements KeyValueContent {

    /**
     * Constructor RSAKeyValue
     *
     * @param element
     * @param BaseURI
     * @throws XMLSecurityException
     */
    public RSAKeyValue(Element element, String BaseURI) throws XMLSecurityException {
        super(element, BaseURI);
    }

    /**
     * Constructor RSAKeyValue
     *
     * @param doc
     * @param modulus
     * @param exponent
     */
    public RSAKeyValue(Document doc, BigInteger modulus, BigInteger exponent) {
        super(doc);

        XMLUtils.addReturnToElement(this.constructionElement);
        this.addBigIntegerElement(modulus, Constants._TAG_MODULUS);
        this.addBigIntegerElement(exponent, Constants._TAG_EXPONENT);
    }

    /**
     * Constructor RSAKeyValue
     *
     * @param doc
     * @param key
     * @throws IllegalArgumentException
     */
    public RSAKeyValue(Document doc, Key key) throws IllegalArgumentException {
        super(doc);

        XMLUtils.addReturnToElement(this.constructionElement);

        if (key instanceof RSAPublicKey ) {
            this.addBigIntegerElement(
                ((RSAPublicKey) key).getModulus(), Constants._TAG_MODULUS
            );
            this.addBigIntegerElement(
                ((RSAPublicKey) key).getPublicExponent(), Constants._TAG_EXPONENT
            );
        } else {
            Object exArgs[] = { Constants._TAG_RSAKEYVALUE, key.getClass().getName() };

            throw new IllegalArgumentException(I18n.translate("KeyValue.IllegalArgument", exArgs));
        }
    }

    /** @inheritDoc */
    public PublicKey getPublicKey() throws XMLSecurityException {
        try {
            KeyFactory rsaFactory = KeyFactory.getInstance("RSA");

            RSAPublicKeySpec rsaKeyspec =
                new RSAPublicKeySpec(
                    this.getBigIntegerFromChildElement(
                        Constants._TAG_MODULUS, Constants.SignatureSpecNS
                    ),
                    this.getBigIntegerFromChildElement(
                        Constants._TAG_EXPONENT, Constants.SignatureSpecNS
                    )
                );
            PublicKey pk = rsaFactory.generatePublic(rsaKeyspec);

            return pk;
        } catch (NoSuchAlgorithmException ex) {
            throw new XMLSecurityException("empty", ex);
        } catch (InvalidKeySpecException ex) {
            throw new XMLSecurityException("empty", ex);
        }
    }

    /** @inheritDoc */
    public String getBaseLocalName() {
        return Constants._TAG_RSAKEYVALUE;
    }
}
