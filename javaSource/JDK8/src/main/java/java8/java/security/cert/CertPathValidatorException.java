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

package java8.java.security.cert;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.security.GeneralSecurityException;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;

/**
 * An exception indicating one of a variety of problems encountered when
 * validating a certification path.
 * <p>
 * A {@code CertPathValidatorException} provides support for wrapping
 * exceptions. The {@link #getCause getCause} method returns the throwable,
 * if any, that caused this exception to be thrown.
 * <p>
 * A {@code CertPathValidatorException} may also include the
 * certification path that was being validated when the exception was thrown,
 * the index of the certificate in the certification path that caused the
 * exception to be thrown, and the reason that caused the failure. Use the
 * {@link #getCertPath getCertPath}, {@link #getIndex getIndex}, and
 * {@link #getReason getReason} methods to retrieve this information.
 *
 * <p>
 * <b>Concurrent Access</b>
 * <p>
 * Unless otherwise specified, the methods defined in this class are not
 * thread-safe. Multiple threads that need to access a single
 * object concurrently should synchronize amongst themselves and
 * provide the necessary locking. Multiple threads each manipulating
 * separate objects need not synchronize.
 *
 * @see CertPathValidator
 *
 * @since       1.4
 * @author      Yassir Elley
 */
public class CertPathValidatorException extends GeneralSecurityException {

    private static final long serialVersionUID = -3083180014971893139L;

    /**
     * @serial the index of the certificate in the certification path
     * that caused the exception to be thrown
     */
    private int index = -1;

    /**
     * @serial the {@code CertPath} that was being validated when
     * the exception was thrown
     */
    private CertPath certPath;

    /**
     * @serial the reason the validation failed
     */
    private Reason reason = BasicReason.UNSPECIFIED;

    /**
     * Creates a {@code CertPathValidatorException} with
     * no detail message.
     */
    public CertPathValidatorException() {
        this(null, null);
    }

    /**
     * Creates a {@code CertPathValidatorException} with the given
     * detail message. A detail message is a {@code String} that
     * describes this particular exception.
     *
     * @param msg the detail message
     */
    public CertPathValidatorException(String msg) {
        this(msg, null);
    }

    /**
     * Creates a {@code CertPathValidatorException} that wraps the
     * specified throwable. This allows any exception to be converted into a
     * {@code CertPathValidatorException}, while retaining information
     * about the wrapped exception, which may be useful for debugging. The
     * detail message is set to ({@code cause==null ? null : cause.toString()})
     * (which typically contains the class and detail message of
     * cause).
     *
     * @param cause the cause (which is saved for later retrieval by the
     * {@link #getCause getCause()} method). (A {@code null} value is
     * permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public CertPathValidatorException(Throwable cause) {
        this((cause == null ? null : cause.toString()), cause);
    }

    /**
     * Creates a {@code CertPathValidatorException} with the specified
     * detail message and cause.
     *
     * @param msg the detail message
     * @param cause the cause (which is saved for later retrieval by the
     * {@link #getCause getCause()} method). (A {@code null} value is
     * permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public CertPathValidatorException(String msg, Throwable cause) {
        this(msg, cause, null, -1);
    }

    /**
     * Creates a {@code CertPathValidatorException} with the specified
     * detail message, cause, certification path, and index.
     *
     * @param msg the detail message (or {@code null} if none)
     * @param cause the cause (or {@code null} if none)
     * @param certPath the certification path that was in the process of
     * being validated when the error was encountered
     * @param index the index of the certificate in the certification path
     * that caused the error (or -1 if not applicable). Note that
     * the list of certificates in a {@code CertPath} is zero based.
     * @throws IndexOutOfBoundsException if the index is out of range
     * {@code (index < -1 || (certPath != null && index >=
     * certPath.getCertificates().size()) }
     * @throws IllegalArgumentException if {@code certPath} is
     * {@code null} and {@code index} is not -1
     */
    public CertPathValidatorException(String msg, Throwable cause,
            CertPath certPath, int index) {
        this(msg, cause, certPath, index, BasicReason.UNSPECIFIED);
    }

    /**
     * Creates a {@code CertPathValidatorException} with the specified
     * detail message, cause, certification path, index, and reason.
     *
     * @param msg the detail message (or {@code null} if none)
     * @param cause the cause (or {@code null} if none)
     * @param certPath the certification path that was in the process of
     * being validated when the error was encountered
     * @param index the index of the certificate in the certification path
     * that caused the error (or -1 if not applicable). Note that
     * the list of certificates in a {@code CertPath} is zero based.
     * @param reason the reason the validation failed
     * @throws IndexOutOfBoundsException if the index is out of range
     * {@code (index < -1 || (certPath != null && index >=
     * certPath.getCertificates().size()) }
     * @throws IllegalArgumentException if {@code certPath} is
     * {@code null} and {@code index} is not -1
     * @throws NullPointerException if {@code reason} is {@code null}
     *
     * @since 1.7
     */
    public CertPathValidatorException(String msg, Throwable cause,
            CertPath certPath, int index, Reason reason) {
        super(msg, cause);
        if (certPath == null && index != -1) {
            throw new IllegalArgumentException();
        }
        if (index < -1 ||
            (certPath != null && index >= certPath.getCertificates().size())) {
            throw new IndexOutOfBoundsException();
        }
        if (reason == null) {
            throw new NullPointerException("reason can't be null");
        }
        this.certPath = certPath;
        this.index = index;
        this.reason = reason;
    }

    /**
     * Returns the certification path that was being validated when
     * the exception was thrown.
     *
     * @return the {@code CertPath} that was being validated when
     * the exception was thrown (or {@code null} if not specified)
     */
    public CertPath getCertPath() {
        return this.certPath;
    }

    /**
     * Returns the index of the certificate in the certification path
     * that caused the exception to be thrown. Note that the list of
     * certificates in a {@code CertPath} is zero based. If no
     * index has been set, -1 is returned.
     *
     * @return the index that has been set, or -1 if none has been set
     */
    public int getIndex() {
        return this.index;
    }

    /**
     * Returns the reason that the validation failed. The reason is
     * associated with the index of the certificate returned by
     * {@link #getIndex}.
     *
     * @return the reason that the validation failed, or
     *    {@code BasicReason.UNSPECIFIED} if a reason has not been
     *    specified
     *
     * @since 1.7
     */
    public Reason getReason() {
        return this.reason;
    }

    private void readObject(ObjectInputStream stream)
        throws ClassNotFoundException, IOException {
        stream.defaultReadObject();
        if (reason == null) {
            reason = BasicReason.UNSPECIFIED;
        }
        if (certPath == null && index != -1) {
            throw new InvalidObjectException("certpath is null and index != -1");
        }
        if (index < -1 ||
            (certPath != null && index >= certPath.getCertificates().size())) {
            throw new InvalidObjectException("index out of range");
        }
    }

    /**
     * The reason the validation algorithm failed.
     *
     * @since 1.7
     */
    public static interface Reason extends java.io.Serializable { }


    /**
     * The BasicReason enumerates the potential reasons that a certification
     * path of any type may be invalid.
     *
     * @since 1.7
     */
    public static enum BasicReason implements Reason {
        /**
         * Unspecified reason.
         */
        UNSPECIFIED,

        /**
         * The certificate is expired.
         */
        EXPIRED,

        /**
         * The certificate is not yet valid.
         */
        NOT_YET_VALID,

        /**
         * The certificate is revoked.
         */
        REVOKED,

        /**
         * The revocation status of the certificate could not be determined.
         */
        UNDETERMINED_REVOCATION_STATUS,

        /**
         * The signature is invalid.
         */
        INVALID_SIGNATURE,

        /**
         * The public key or the signature algorithm has been constrained.
         */
        ALGORITHM_CONSTRAINED
    }
}
