/*
 * Copyright (c) 2003, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java8.java.security.spec;

import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.PSSParameterSpec;

/**
 * This class specifies the set of parameters used with mask generation
 * function MGF1 in OAEP Padding and RSA-PSS signature scheme, as
 * defined in the
 * <a href="http://www.ietf.org/rfc/rfc3447.txt">PKCS #1 v2.1</a>
 * standard.
 *
 * <p>Its ASN.1 definition in PKCS#1 standard is described below:
 * <pre>
 * MGF1Parameters ::= OAEP-PSSDigestAlgorthms
 * </pre>
 * where
 * <pre>
 * OAEP-PSSDigestAlgorithms    ALGORITHM-IDENTIFIER ::= {
 *   { OID id-sha1 PARAMETERS NULL   }|
 *   { OID id-sha224 PARAMETERS NULL   }|
 *   { OID id-sha256 PARAMETERS NULL }|
 *   { OID id-sha384 PARAMETERS NULL }|
 *   { OID id-sha512 PARAMETERS NULL },
 *   ...  -- Allows for future expansion --
 * }
 * </pre>
 * @see PSSParameterSpec
 * @see javax.crypto.spec.OAEPParameterSpec
 *
 * @author Valerie Peng
 *
 * @since 1.5
 */
public class MGF1ParameterSpec implements AlgorithmParameterSpec {

    /**
     * The MGF1ParameterSpec which uses "SHA-1" message digest.
     */
    public static final java.security.spec.MGF1ParameterSpec SHA1 =
        new java.security.spec.MGF1ParameterSpec("SHA-1");
    /**
     * The MGF1ParameterSpec which uses "SHA-224" message digest.
     */
    public static final java.security.spec.MGF1ParameterSpec SHA224 =
        new java.security.spec.MGF1ParameterSpec("SHA-224");
    /**
     * The MGF1ParameterSpec which uses "SHA-256" message digest.
     */
    public static final java.security.spec.MGF1ParameterSpec SHA256 =
        new java.security.spec.MGF1ParameterSpec("SHA-256");
    /**
     * The MGF1ParameterSpec which uses "SHA-384" message digest.
     */
    public static final java.security.spec.MGF1ParameterSpec SHA384 =
        new java.security.spec.MGF1ParameterSpec("SHA-384");
    /**
     * The MGF1ParameterSpec which uses SHA-512 message digest.
     */
    public static final java.security.spec.MGF1ParameterSpec SHA512 =
        new java.security.spec.MGF1ParameterSpec("SHA-512");

    private String mdName;

    /**
     * Constructs a parameter set for mask generation function MGF1
     * as defined in the PKCS #1 standard.
     *
     * @param mdName the algorithm name for the message digest
     * used in this mask generation function MGF1.
     * @exception NullPointerException if {@code mdName} is null.
     */
    public MGF1ParameterSpec(String mdName) {
        if (mdName == null) {
            throw new NullPointerException("digest algorithm is null");
        }
        this.mdName = mdName;
    }

    /**
     * Returns the algorithm name of the message digest used by the mask
     * generation function.
     *
     * @return the algorithm name of the message digest.
     */
    public String getDigestAlgorithm() {
        return mdName;
    }
}
