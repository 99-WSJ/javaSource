/*
 * Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
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
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStore.*;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.Enumeration;

/**
 * This class defines the <i>Service Provider Interface</i> (<b>SPI</b>)
 * for the {@code KeyStore} class.
 * All the abstract methods in this class must be implemented by each
 * cryptographic service provider who wishes to supply the implementation
 * of a keystore for a particular keystore type.
 *
 * @author Jan Luehe
 *
 *
 * @see KeyStore
 *
 * @since 1.2
 */

public abstract class KeyStoreSpi {

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
     * @exception NoSuchAlgorithmException if the algorithm for recovering the
     * key cannot be found
     * @exception UnrecoverableKeyException if the key cannot be recovered
     * (e.g., the given password is wrong).
     */
    public abstract Key engineGetKey(String alias, char[] password)
        throws NoSuchAlgorithmException, UnrecoverableKeyException;

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
     * and the root certificate authority last), or null if the given alias
     * does not exist or does not contain a certificate chain
     */
    public abstract Certificate[] engineGetCertificateChain(String alias);

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
     * (if a chain exists) is returned.
     *
     * @param alias the alias name
     *
     * @return the certificate, or null if the given alias does not exist or
     * does not contain a certificate.
     */
    public abstract Certificate engineGetCertificate(String alias);

    /**
     * Returns the creation date of the entry identified by the given alias.
     *
     * @param alias the alias name
     *
     * @return the creation date of this entry, or null if the given alias does
     * not exist
     */
    public abstract Date engineGetCreationDate(String alias);

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
     * @exception KeyStoreException if the given key cannot be protected, or
     * this operation fails for some other reason
     */
    public abstract void engineSetKeyEntry(String alias, Key key,
                                           char[] password,
                                           Certificate[] chain)
        throws KeyStoreException;

    /**
     * Assigns the given key (that has already been protected) to the given
     * alias.
     *
     * <p>If the protected key is of type
     * {@code java.security.PrivateKey},
     * it must be accompanied by a certificate chain certifying the
     * corresponding public key.
     *
     * <p>If the given alias already exists, the keystore information
     * associated with it is overridden by the given key (and possibly
     * certificate chain).
     *
     * @param alias the alias name
     * @param key the key (in protected format) to be associated with the alias
     * @param chain the certificate chain for the corresponding public
     * key (only useful if the protected key is of type
     * {@code java.security.PrivateKey}).
     *
     * @exception KeyStoreException if this operation fails.
     */
    public abstract void engineSetKeyEntry(String alias, byte[] key,
                                           Certificate[] chain)
        throws KeyStoreException;

    /**
     * Assigns the given certificate to the given alias.
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
     * @exception KeyStoreException if the given alias already exists and does
     * not identify an entry containing a trusted certificate,
     * or this operation fails for some other reason.
     */
    public abstract void engineSetCertificateEntry(String alias,
                                                   Certificate cert)
        throws KeyStoreException;

    /**
     * Deletes the entry identified by the given alias from this keystore.
     *
     * @param alias the alias name
     *
     * @exception KeyStoreException if the entry cannot be removed.
     */
    public abstract void engineDeleteEntry(String alias)
        throws KeyStoreException;

    /**
     * Lists all the alias names of this keystore.
     *
     * @return enumeration of the alias names
     */
    public abstract Enumeration<String> engineAliases();

    /**
     * Checks if the given alias exists in this keystore.
     *
     * @param alias the alias name
     *
     * @return true if the alias exists, false otherwise
     */
    public abstract boolean engineContainsAlias(String alias);

    /**
     * Retrieves the number of entries in this keystore.
     *
     * @return the number of entries in this keystore
     */
    public abstract int engineSize();

    /**
     * Returns true if the entry identified by the given alias
     * was created by a call to {@code setKeyEntry},
     * or created by a call to {@code setEntry} with a
     * {@code PrivateKeyEntry} or a {@code SecretKeyEntry}.
     *
     * @param alias the alias for the keystore entry to be checked
     *
     * @return true if the entry identified by the given alias is a
     * key-related, false otherwise.
     */
    public abstract boolean engineIsKeyEntry(String alias);

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
     */
    public abstract boolean engineIsCertificateEntry(String alias);

    /**
     * Returns the (alias) name of the first keystore entry whose certificate
     * matches the given certificate.
     *
     * <p>This method attempts to match the given certificate with each
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
     * @return the alias name of the first entry with matching certificate,
     * or null if no such entry exists in this keystore.
     */
    public abstract String engineGetCertificateAlias(Certificate cert);

    /**
     * Stores this keystore to the given output stream, and protects its
     * integrity with the given password.
     *
     * @param stream the output stream to which this keystore is written.
     * @param password the password to generate the keystore integrity check
     *
     * @exception IOException if there was an I/O problem with data
     * @exception NoSuchAlgorithmException if the appropriate data integrity
     * algorithm could not be found
     * @exception CertificateException if any of the certificates included in
     * the keystore data could not be stored
     */
    public abstract void engineStore(OutputStream stream, char[] password)
        throws IOException, NoSuchAlgorithmException, CertificateException;

    /**
     * Stores this keystore using the given
     * {@code KeyStore.LoadStoreParmeter}.
     *
     * @param param the {@code KeyStore.LoadStoreParmeter}
     *          that specifies how to store the keystore,
     *          which may be {@code null}
     *
     * @exception IllegalArgumentException if the given
     *          {@code KeyStore.LoadStoreParmeter}
     *          input is not recognized
     * @exception IOException if there was an I/O problem with data
     * @exception NoSuchAlgorithmException if the appropriate data integrity
     *          algorithm could not be found
     * @exception CertificateException if any of the certificates included in
     *          the keystore data could not be stored
     *
     * @since 1.5
     */
    public void engineStore(LoadStoreParameter param)
                throws IOException, NoSuchAlgorithmException,
                CertificateException {
        throw new UnsupportedOperationException();
    }

    /**
     * Loads the keystore from the given input stream.
     *
     * <p>A password may be given to unlock the keystore
     * (e.g. the keystore resides on a hardware token device),
     * or to check the integrity of the keystore data.
     * If a password is not given for integrity checking,
     * then integrity checking is not performed.
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
    public abstract void engineLoad(InputStream stream, char[] password)
        throws IOException, NoSuchAlgorithmException, CertificateException;

    /**
     * Loads the keystore using the given
     * {@code KeyStore.LoadStoreParameter}.
     *
     * <p> Note that if this KeyStore has already been loaded, it is
     * reinitialized and loaded again from the given parameter.
     *
     * @param param the {@code KeyStore.LoadStoreParameter}
     *          that specifies how to load the keystore,
     *          which may be {@code null}
     *
     * @exception IllegalArgumentException if the given
     *          {@code KeyStore.LoadStoreParameter}
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
    public void engineLoad(LoadStoreParameter param)
                throws IOException, NoSuchAlgorithmException,
                CertificateException {

        if (param == null) {
            engineLoad((InputStream)null, (char[])null);
            return;
        }

        if (param instanceof KeyStore.SimpleLoadStoreParameter) {
            ProtectionParameter protection = param.getProtectionParameter();
            char[] password;
            if (protection instanceof PasswordProtection) {
                password = ((PasswordProtection)protection).getPassword();
            } else if (protection instanceof CallbackHandlerProtection) {
                CallbackHandler handler =
                    ((CallbackHandlerProtection)protection).getCallbackHandler();
                PasswordCallback callback =
                    new PasswordCallback("Password: ", false);
                try {
                    handler.handle(new Callback[] {callback});
                } catch (UnsupportedCallbackException e) {
                    throw new NoSuchAlgorithmException
                        ("Could not obtain password", e);
                }
                password = callback.getPassword();
                callback.clearPassword();
                if (password == null) {
                    throw new NoSuchAlgorithmException
                        ("No password provided");
                }
            } else {
                throw new NoSuchAlgorithmException("ProtectionParameter must"
                    + " be PasswordProtection or CallbackHandlerProtection");
            }
            engineLoad(null, password);
            return;
        }

        throw new UnsupportedOperationException();
    }

    /**
     * Gets a {@code KeyStore.Entry} for the specified alias
     * with the specified protection parameter.
     *
     * @param alias get the {@code KeyStore.Entry} for this alias
     * @param protParam the {@code ProtectionParameter}
     *          used to protect the {@code Entry},
     *          which may be {@code null}
     *
     * @return the {@code KeyStore.Entry} for the specified alias,
     *          or {@code null} if there is no such entry
     *
     * @exception KeyStoreException if the operation failed
     * @exception NoSuchAlgorithmException if the algorithm for recovering the
     *          entry cannot be found
     * @exception UnrecoverableEntryException if the specified
     *          {@code protParam} were insufficient or invalid
     * @exception UnrecoverableKeyException if the entry is a
     *          {@code PrivateKeyEntry} or {@code SecretKeyEntry}
     *          and the specified {@code protParam} does not contain
     *          the information needed to recover the key (e.g. wrong password)
     *
     * @since 1.5
     */
    public Entry engineGetEntry(String alias,
                        ProtectionParameter protParam)
                throws KeyStoreException, NoSuchAlgorithmException,
                UnrecoverableEntryException {

        if (!engineContainsAlias(alias)) {
            return null;
        }

        if (protParam == null) {
            if (engineIsCertificateEntry(alias)) {
                return new TrustedCertificateEntry
                                (engineGetCertificate(alias));
            } else {
                throw new UnrecoverableKeyException
                        ("requested entry requires a password");
            }
        }

        if (protParam instanceof PasswordProtection) {
            if (engineIsCertificateEntry(alias)) {
                throw new UnsupportedOperationException
                    ("trusted certificate entries are not password-protected");
            } else if (engineIsKeyEntry(alias)) {
                PasswordProtection pp =
                        (PasswordProtection)protParam;
                char[] password = pp.getPassword();

                Key key = engineGetKey(alias, password);
                if (key instanceof PrivateKey) {
                    Certificate[] chain = engineGetCertificateChain(alias);
                    return new PrivateKeyEntry((PrivateKey)key, chain);
                } else if (key instanceof SecretKey) {
                    return new SecretKeyEntry((SecretKey)key);
                }
            }
        }

        throw new UnsupportedOperationException();
    }

    /**
     * Saves a {@code KeyStore.Entry} under the specified alias.
     * The specified protection parameter is used to protect the
     * {@code Entry}.
     *
     * <p> If an entry already exists for the specified alias,
     * it is overridden.
     *
     * @param alias save the {@code KeyStore.Entry} under this alias
     * @param entry the {@code Entry} to save
     * @param protParam the {@code ProtectionParameter}
     *          used to protect the {@code Entry},
     *          which may be {@code null}
     *
     * @exception KeyStoreException if this operation fails
     *
     * @since 1.5
     */
    public void engineSetEntry(String alias, Entry entry,
                        ProtectionParameter protParam)
                throws KeyStoreException {

        // get password
        if (protParam != null &&
            !(protParam instanceof PasswordProtection)) {
            throw new KeyStoreException("unsupported protection parameter");
        }
        PasswordProtection pProtect = null;
        if (protParam != null) {
            pProtect = (PasswordProtection)protParam;
        }

        // set entry
        if (entry instanceof TrustedCertificateEntry) {
            if (protParam != null && pProtect.getPassword() != null) {
                // pre-1.5 style setCertificateEntry did not allow password
                throw new KeyStoreException
                    ("trusted certificate entries are not password-protected");
            } else {
                TrustedCertificateEntry tce =
                        (TrustedCertificateEntry)entry;
                engineSetCertificateEntry(alias, tce.getTrustedCertificate());
                return;
            }
        } else if (entry instanceof PrivateKeyEntry) {
            if (pProtect == null || pProtect.getPassword() == null) {
                // pre-1.5 style setKeyEntry required password
                throw new KeyStoreException
                    ("non-null password required to create PrivateKeyEntry");
            } else {
                engineSetKeyEntry
                    (alias,
                    ((PrivateKeyEntry)entry).getPrivateKey(),
                    pProtect.getPassword(),
                    ((PrivateKeyEntry)entry).getCertificateChain());
                return;
            }
        } else if (entry instanceof SecretKeyEntry) {
            if (pProtect == null || pProtect.getPassword() == null) {
                // pre-1.5 style setKeyEntry required password
                throw new KeyStoreException
                    ("non-null password required to create SecretKeyEntry");
            } else {
                engineSetKeyEntry
                    (alias,
                    ((SecretKeyEntry)entry).getSecretKey(),
                    pProtect.getPassword(),
                    (Certificate[])null);
                return;
            }
        }

        throw new KeyStoreException
                ("unsupported entry type: " + entry.getClass().getName());
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
     * @since 1.5
     */
    public boolean
        engineEntryInstanceOf(String alias,
                              Class<? extends Entry> entryClass)
    {
        if (entryClass == TrustedCertificateEntry.class) {
            return engineIsCertificateEntry(alias);
        }
        if (entryClass == PrivateKeyEntry.class) {
            return engineIsKeyEntry(alias) &&
                        engineGetCertificate(alias) != null;
        }
        if (entryClass == SecretKeyEntry.class) {
            return engineIsKeyEntry(alias) &&
                        engineGetCertificate(alias) == null;
        }
        return false;
    }
}
