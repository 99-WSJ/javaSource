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

import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;

/**
 * This class defines the <i>Service Provider Interface</i> (<b>SPI</b>)
 * for the {@code AlgorithmParameters} class, which is used to manage
 * algorithm parameters.
 *
 * <p> All the abstract methods in this class must be implemented by each
 * cryptographic service provider who wishes to supply parameter management
 * for a particular algorithm.
 *
 * @author Jan Luehe
 *
 *
 * @see AlgorithmParameters
 * @see AlgorithmParameterSpec
 * @see java.security.spec.DSAParameterSpec
 *
 * @since 1.2
 */

public abstract class AlgorithmParametersSpi {

    /**
     * Initializes this parameters object using the parameters
     * specified in {@code paramSpec}.
     *
     * @param paramSpec the parameter specification.
     *
     * @exception InvalidParameterSpecException if the given parameter
     * specification is inappropriate for the initialization of this parameter
     * object.
     */
    protected abstract void engineInit(AlgorithmParameterSpec paramSpec)
        throws InvalidParameterSpecException;

    /**
     * Imports the specified parameters and decodes them
     * according to the primary decoding format for parameters.
     * The primary decoding format for parameters is ASN.1, if an ASN.1
     * specification for this type of parameters exists.
     *
     * @param params the encoded parameters.
     *
     * @exception IOException on decoding errors
     */
    protected abstract void engineInit(byte[] params)
        throws IOException;

    /**
     * Imports the parameters from {@code params} and
     * decodes them according to the specified decoding format.
     * If {@code format} is null, the
     * primary decoding format for parameters is used. The primary decoding
     * format is ASN.1, if an ASN.1 specification for these parameters
     * exists.
     *
     * @param params the encoded parameters.
     *
     * @param format the name of the decoding format.
     *
     * @exception IOException on decoding errors
     */
    protected abstract void engineInit(byte[] params, String format)
        throws IOException;

    /**
     * Returns a (transparent) specification of this parameters
     * object.
     * {@code paramSpec} identifies the specification class in which
     * the parameters should be returned. It could, for example, be
     * {@code DSAParameterSpec.class}, to indicate that the
     * parameters should be returned in an instance of the
     * {@code DSAParameterSpec} class.
     *
     * @param <T> the type of the parameter specification to be returned
     *
     * @param paramSpec the specification class in which
     * the parameters should be returned.
     *
     * @return the parameter specification.
     *
     * @exception InvalidParameterSpecException if the requested parameter
     * specification is inappropriate for this parameter object.
     */
    protected abstract
        <T extends AlgorithmParameterSpec>
        T engineGetParameterSpec(Class<T> paramSpec)
        throws InvalidParameterSpecException;

    /**
     * Returns the parameters in their primary encoding format.
     * The primary encoding format for parameters is ASN.1, if an ASN.1
     * specification for this type of parameters exists.
     *
     * @return the parameters encoded using their primary encoding format.
     *
     * @exception IOException on encoding errors.
     */
    protected abstract byte[] engineGetEncoded() throws IOException;

    /**
     * Returns the parameters encoded in the specified format.
     * If {@code format} is null, the
     * primary encoding format for parameters is used. The primary encoding
     * format is ASN.1, if an ASN.1 specification for these parameters
     * exists.
     *
     * @param format the name of the encoding format.
     *
     * @return the parameters encoded using the specified encoding scheme.
     *
     * @exception IOException on encoding errors.
     */
    protected abstract byte[] engineGetEncoded(String format)
        throws IOException;

    /**
     * Returns a formatted string describing the parameters.
     *
     * @return a formatted string describing the parameters.
     */
    protected abstract String engineToString();
}
