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

package java8.java.util.jar;

import sun.misc.IOUtils;
import sun.misc.SharedSecrets;
import sun.security.action.GetPropertyAction;
import sun.security.util.ManifestEntryVerifier;
import sun.security.util.SignatureFileVerifier;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.security.AccessController;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.security.cert.Certificate;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * The <code>JarFile</code> class is used to read the contents of a jar file
 * from any file that can be opened with <code>java.io.RandomAccessFile</code>.
 * It extends the class <code>java.util.zip.ZipFile</code> with support
 * for reading an optional <code>Manifest</code> entry. The
 * <code>Manifest</code> can be used to specify meta-information about the
 * jar file and its entries.
 *
 * <p> Unless otherwise noted, passing a <tt>null</tt> argument to a constructor
 * or method in this class will cause a {@link NullPointerException} to be
 * thrown.
 *
 * If the verify flag is on when opening a signed jar file, the content of the
 * file is verified against its signature embedded inside the file. Please note
 * that the verification process does not include validating the signer's
 * certificate. A caller should inspect the return value of
 * {@link JarEntry#getCodeSigners()} to further determine if the signature
 * can be trusted.
 *
 * @author  David Connelly
 * @see     Manifest
 * @see     ZipFile
 * @see     JarEntry
 * @since   1.2
 */
public
class JarFile extends ZipFile {
    private SoftReference<Manifest> manRef;
    private JarEntry manEntry;
    private java.util.jar.JarVerifier jv;
    private boolean jvInitialized;
    private boolean verify;

    // indicates if Class-Path attribute present (only valid if hasCheckedSpecialAttributes true)
    private boolean hasClassPathAttribute;
    // true if manifest checked for special attributes
    private volatile boolean hasCheckedSpecialAttributes;

    // Set up JavaUtilJarAccess in SharedSecrets
    static {
        SharedSecrets.setJavaUtilJarAccess(new java.util.jar.JavaUtilJarAccessImpl());
    }

    /**
     * The JAR manifest file name.
     */
    public static final String MANIFEST_NAME = "META-INF/MANIFEST.MF";

    /**
     * Creates a new <code>JarFile</code> to read from the specified
     * file <code>name</code>. The <code>JarFile</code> will be verified if
     * it is signed.
     * @param name the name of the jar file to be opened for reading
     * @throws IOException if an I/O error has occurred
     * @throws SecurityException if access to the file is denied
     *         by the SecurityManager
     */
    public JarFile(String name) throws IOException {
        this(new File(name), true, ZipFile.OPEN_READ);
    }

    /**
     * Creates a new <code>JarFile</code> to read from the specified
     * file <code>name</code>.
     * @param name the name of the jar file to be opened for reading
     * @param verify whether or not to verify the jar file if
     * it is signed.
     * @throws IOException if an I/O error has occurred
     * @throws SecurityException if access to the file is denied
     *         by the SecurityManager
     */
    public JarFile(String name, boolean verify) throws IOException {
        this(new File(name), verify, ZipFile.OPEN_READ);
    }

    /**
     * Creates a new <code>JarFile</code> to read from the specified
     * <code>File</code> object. The <code>JarFile</code> will be verified if
     * it is signed.
     * @param file the jar file to be opened for reading
     * @throws IOException if an I/O error has occurred
     * @throws SecurityException if access to the file is denied
     *         by the SecurityManager
     */
    public JarFile(File file) throws IOException {
        this(file, true, ZipFile.OPEN_READ);
    }


    /**
     * Creates a new <code>JarFile</code> to read from the specified
     * <code>File</code> object.
     * @param file the jar file to be opened for reading
     * @param verify whether or not to verify the jar file if
     * it is signed.
     * @throws IOException if an I/O error has occurred
     * @throws SecurityException if access to the file is denied
     *         by the SecurityManager.
     */
    public JarFile(File file, boolean verify) throws IOException {
        this(file, verify, ZipFile.OPEN_READ);
    }


    /**
     * Creates a new <code>JarFile</code> to read from the specified
     * <code>File</code> object in the specified mode.  The mode argument
     * must be either <tt>OPEN_READ</tt> or <tt>OPEN_READ | OPEN_DELETE</tt>.
     *
     * @param file the jar file to be opened for reading
     * @param verify whether or not to verify the jar file if
     * it is signed.
     * @param mode the mode in which the file is to be opened
     * @throws IOException if an I/O error has occurred
     * @throws IllegalArgumentException
     *         if the <tt>mode</tt> argument is invalid
     * @throws SecurityException if access to the file is denied
     *         by the SecurityManager
     * @since 1.3
     */
    public JarFile(File file, boolean verify, int mode) throws IOException {
        super(file, mode);
        this.verify = verify;
    }

    /**
     * Returns the jar file manifest, or <code>null</code> if none.
     *
     * @return the jar file manifest, or <code>null</code> if none
     *
     * @throws IllegalStateException
     *         may be thrown if the jar file has been closed
     * @throws IOException  if an I/O error has occurred
     */
    public Manifest getManifest() throws IOException {
        return getManifestFromReference();
    }

    private Manifest getManifestFromReference() throws IOException {
        Manifest man = manRef != null ? manRef.get() : null;

        if (man == null) {

            JarEntry manEntry = getManEntry();

            // If found then load the manifest
            if (manEntry != null) {
                if (verify) {
                    byte[] b = getBytes(manEntry);
                    man = new Manifest(new ByteArrayInputStream(b));
                    if (!jvInitialized) {
                        jv = new java.util.jar.JarVerifier(b);
                    }
                } else {
                    man = new Manifest(super.getInputStream(manEntry));
                }
                manRef = new SoftReference<>(man);
            }
        }
        return man;
    }

    private native String[] getMetaInfEntryNames();

    /**
     * Returns the <code>JarEntry</code> for the given entry name or
     * <code>null</code> if not found.
     *
     * @param name the jar file entry name
     * @return the <code>JarEntry</code> for the given entry name or
     *         <code>null</code> if not found.
     *
     * @throws IllegalStateException
     *         may be thrown if the jar file has been closed
     *
     * @see JarEntry
     */
    public JarEntry getJarEntry(String name) {
        return (JarEntry)getEntry(name);
    }

    /**
     * Returns the <code>ZipEntry</code> for the given entry name or
     * <code>null</code> if not found.
     *
     * @param name the jar file entry name
     * @return the <code>ZipEntry</code> for the given entry name or
     *         <code>null</code> if not found
     *
     * @throws IllegalStateException
     *         may be thrown if the jar file has been closed
     *
     * @see ZipEntry
     */
    public ZipEntry getEntry(String name) {
        ZipEntry ze = super.getEntry(name);
        if (ze != null) {
            return new JarFileEntry(ze);
        }
        return null;
    }

    private class JarEntryIterator implements Enumeration<JarEntry>,
            Iterator<JarEntry>
    {
        final Enumeration<? extends ZipEntry> e = java.util.jar.JarFile.super.entries();

        public boolean hasNext() {
            return e.hasMoreElements();
        }

        public JarEntry next() {
            ZipEntry ze = e.nextElement();
            return new JarFileEntry(ze);
        }

        public boolean hasMoreElements() {
            return hasNext();
        }

        public JarEntry nextElement() {
            return next();
        }
    }

    /**
     * Returns an enumeration of the zip file entries.
     */
    public Enumeration<JarEntry> entries() {
        return new JarEntryIterator();
    }

    @Override
    public Stream<JarEntry> stream() {
        return StreamSupport.stream(Spliterators.spliterator(
                new JarEntryIterator(), size(),
                Spliterator.ORDERED | Spliterator.DISTINCT |
                        Spliterator.IMMUTABLE | Spliterator.NONNULL), false);
    }

    private class JarFileEntry extends JarEntry {
        JarFileEntry(ZipEntry ze) {
            super(ze);
        }
        public Attributes getAttributes() throws IOException {
            Manifest man = java.util.jar.JarFile.this.getManifest();
            if (man != null) {
                return man.getAttributes(getName());
            } else {
                return null;
            }
        }
        public Certificate[] getCertificates() {
            try {
                maybeInstantiateVerifier();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (certs == null && jv != null) {
                certs = jv.getCerts(java.util.jar.JarFile.this, this);
            }
            return certs == null ? null : certs.clone();
        }
        public CodeSigner[] getCodeSigners() {
            try {
                maybeInstantiateVerifier();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (signers == null && jv != null) {
                signers = jv.getCodeSigners(java.util.jar.JarFile.this, this);
            }
            return signers == null ? null : signers.clone();
        }
    }

    /*
     * Ensures that the JarVerifier has been created if one is
     * necessary (i.e., the jar appears to be signed.) This is done as
     * a quick check to avoid processing of the manifest for unsigned
     * jars.
     */
    private void maybeInstantiateVerifier() throws IOException {
        if (jv != null) {
            return;
        }

        if (verify) {
            String[] names = getMetaInfEntryNames();
            if (names != null) {
                for (int i = 0; i < names.length; i++) {
                    String name = names[i].toUpperCase(Locale.ENGLISH);
                    if (name.endsWith(".DSA") ||
                        name.endsWith(".RSA") ||
                        name.endsWith(".EC") ||
                        name.endsWith(".SF")) {
                        // Assume since we found a signature-related file
                        // that the jar is signed and that we therefore
                        // need a JarVerifier and Manifest
                        getManifest();
                        return;
                    }
                }
            }
            // No signature-related files; don't instantiate a
            // verifier
            verify = false;
        }
    }


    /*
     * Initializes the verifier object by reading all the manifest
     * entries and passing them to the verifier.
     */
    private void initializeVerifier() {
        ManifestEntryVerifier mev = null;

        // Verify "META-INF/" entries...
        try {
            String[] names = getMetaInfEntryNames();
            if (names != null) {
                for (int i = 0; i < names.length; i++) {
                    String uname = names[i].toUpperCase(Locale.ENGLISH);
                    if (MANIFEST_NAME.equals(uname)
                            || SignatureFileVerifier.isBlockOrSF(uname)) {
                        JarEntry e = getJarEntry(names[i]);
                        if (e == null) {
                            throw new JarException("corrupted jar file");
                        }
                        if (mev == null) {
                            mev = new ManifestEntryVerifier
                                (getManifestFromReference());
                        }
                        byte[] b = getBytes(e);
                        if (b != null && b.length > 0) {
                            jv.beginEntry(e, mev);
                            jv.update(b.length, b, 0, b.length, mev);
                            jv.update(-1, null, 0, 0, mev);
                        }
                    }
                }
            }
        } catch (IOException ex) {
            // if we had an error parsing any blocks, just
            // treat the jar file as being unsigned
            jv = null;
            verify = false;
            if (java.util.jar.JarVerifier.debug != null) {
                java.util.jar.JarVerifier.debug.println("jarfile parsing error!");
                ex.printStackTrace();
            }
        }

        // if after initializing the verifier we have nothing
        // signed, we null it out.

        if (jv != null) {

            jv.doneWithMeta();
            if (java.util.jar.JarVerifier.debug != null) {
                java.util.jar.JarVerifier.debug.println("done with meta!");
            }

            if (jv.nothingToVerify()) {
                if (java.util.jar.JarVerifier.debug != null) {
                    java.util.jar.JarVerifier.debug.println("nothing to verify!");
                }
                jv = null;
                verify = false;
            }
        }
    }

    /*
     * Reads all the bytes for a given entry. Used to process the
     * META-INF files.
     */
    private byte[] getBytes(ZipEntry ze) throws IOException {
        try (InputStream is = super.getInputStream(ze)) {
            return IOUtils.readFully(is, (int)ze.getSize(), true);
        }
    }

    /**
     * Returns an input stream for reading the contents of the specified
     * zip file entry.
     * @param ze the zip file entry
     * @return an input stream for reading the contents of the specified
     *         zip file entry
     * @throws ZipException if a zip file format error has occurred
     * @throws IOException if an I/O error has occurred
     * @throws SecurityException if any of the jar file entries
     *         are incorrectly signed.
     * @throws IllegalStateException
     *         may be thrown if the jar file has been closed
     */
    public synchronized InputStream getInputStream(ZipEntry ze)
        throws IOException
    {
        maybeInstantiateVerifier();
        if (jv == null) {
            return super.getInputStream(ze);
        }
        if (!jvInitialized) {
            initializeVerifier();
            jvInitialized = true;
            // could be set to null after a call to
            // initializeVerifier if we have nothing to
            // verify
            if (jv == null)
                return super.getInputStream(ze);
        }

        // wrap a verifier stream around the real stream
        return new java.util.jar.JarVerifier.VerifierStream(
            getManifestFromReference(),
            ze instanceof JarFileEntry ?
            (JarEntry) ze : getJarEntry(ze.getName()),
            super.getInputStream(ze),
            jv);
    }

    // Statics for hand-coded Boyer-Moore search
    private static final char[] CLASSPATH_CHARS = {'c','l','a','s','s','-','p','a','t','h'};
    // The bad character shift for "class-path"
    private static final int[] CLASSPATH_LASTOCC;
    // The good suffix shift for "class-path"
    private static final int[] CLASSPATH_OPTOSFT;

    static {
        CLASSPATH_LASTOCC = new int[128];
        CLASSPATH_OPTOSFT = new int[10];
        CLASSPATH_LASTOCC[(int)'c'] = 1;
        CLASSPATH_LASTOCC[(int)'l'] = 2;
        CLASSPATH_LASTOCC[(int)'s'] = 5;
        CLASSPATH_LASTOCC[(int)'-'] = 6;
        CLASSPATH_LASTOCC[(int)'p'] = 7;
        CLASSPATH_LASTOCC[(int)'a'] = 8;
        CLASSPATH_LASTOCC[(int)'t'] = 9;
        CLASSPATH_LASTOCC[(int)'h'] = 10;
        for (int i=0; i<9; i++)
            CLASSPATH_OPTOSFT[i] = 10;
        CLASSPATH_OPTOSFT[9]=1;
    }

    private JarEntry getManEntry() {
        if (manEntry == null) {
            // First look up manifest entry using standard name
            manEntry = getJarEntry(MANIFEST_NAME);
            if (manEntry == null) {
                // If not found, then iterate through all the "META-INF/"
                // entries to find a match.
                String[] names = getMetaInfEntryNames();
                if (names != null) {
                    for (int i = 0; i < names.length; i++) {
                        if (MANIFEST_NAME.equals(
                                                 names[i].toUpperCase(Locale.ENGLISH))) {
                            manEntry = getJarEntry(names[i]);
                            break;
                        }
                    }
                }
            }
        }
        return manEntry;
    }

   /**
    * Returns {@code true} iff this JAR file has a manifest with the
    * Class-Path attribute
    */
    boolean hasClassPathAttribute() throws IOException {
        checkForSpecialAttributes();
        return hasClassPathAttribute;
    }

    /**
     * Returns true if the pattern {@code src} is found in {@code b}.
     * The {@code lastOcc} and {@code optoSft} arrays are the precomputed
     * bad character and good suffix shifts.
     */
    private boolean match(char[] src, byte[] b, int[] lastOcc, int[] optoSft) {
        int len = src.length;
        int last = b.length - len;
        int i = 0;
        next:
        while (i<=last) {
            for (int j=(len-1); j>=0; j--) {
                char c = (char) b[i+j];
                c = (((c-'A')|('Z'-c)) >= 0) ? (char)(c + 32) : c;
                if (c != src[j]) {
                    i += Math.max(j + 1 - lastOcc[c&0x7F], optoSft[j]);
                    continue next;
                 }
            }
            return true;
        }
        return false;
    }

    /**
     * On first invocation, check if the JAR file has the Class-Path
     * attribute. A no-op on subsequent calls.
     */
    private void checkForSpecialAttributes() throws IOException {
        if (hasCheckedSpecialAttributes) return;
        if (!isKnownNotToHaveSpecialAttributes()) {
            JarEntry manEntry = getManEntry();
            if (manEntry != null) {
                byte[] b = getBytes(manEntry);
                if (match(CLASSPATH_CHARS, b, CLASSPATH_LASTOCC, CLASSPATH_OPTOSFT))
                    hasClassPathAttribute = true;
            }
        }
        hasCheckedSpecialAttributes = true;
    }

    private static String javaHome;
    private static volatile String[] jarNames;
    private boolean isKnownNotToHaveSpecialAttributes() {
        // Optimize away even scanning of manifest for jar files we
        // deliver which don't have a class-path attribute. If one of
        // these jars is changed to include such an attribute this code
        // must be changed.
        if (javaHome == null) {
            javaHome = AccessController.doPrivileged(
                new GetPropertyAction("java.home"));
        }
        if (jarNames == null) {
            String[] names = new String[11];
            String fileSep = File.separator;
            int i = 0;
            names[i++] = fileSep + "rt.jar";
            names[i++] = fileSep + "jsse.jar";
            names[i++] = fileSep + "jce.jar";
            names[i++] = fileSep + "charsets.jar";
            names[i++] = fileSep + "dnsns.jar";
            names[i++] = fileSep + "zipfs.jar";
            names[i++] = fileSep + "localedata.jar";
            names[i++] = fileSep = "cldrdata.jar";
            names[i++] = fileSep + "sunjce_provider.jar";
            names[i++] = fileSep + "sunpkcs11.jar";
            names[i++] = fileSep + "sunec.jar";
            jarNames = names;
        }

        String name = getName();
        String localJavaHome = javaHome;
        if (name.startsWith(localJavaHome)) {
            String[] names = jarNames;
            for (int i = 0; i < names.length; i++) {
                if (name.endsWith(names[i])) {
                    return true;
                }
            }
        }
        return false;
    }

    private synchronized void ensureInitialization() {
        try {
            maybeInstantiateVerifier();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (jv != null && !jvInitialized) {
            initializeVerifier();
            jvInitialized = true;
        }
    }

    JarEntry newEntry(ZipEntry ze) {
        return new JarFileEntry(ze);
    }

    Enumeration<String> entryNames(CodeSource[] cs) {
        ensureInitialization();
        if (jv != null) {
            return jv.entryNames(this, cs);
        }

        /*
         * JAR file has no signed content. Is there a non-signing
         * code source?
         */
        boolean includeUnsigned = false;
        for (int i = 0; i < cs.length; i++) {
            if (cs[i].getCodeSigners() == null) {
                includeUnsigned = true;
                break;
            }
        }
        if (includeUnsigned) {
            return unsignedEntryNames();
        } else {
            return new Enumeration<String>() {

                public boolean hasMoreElements() {
                    return false;
                }

                public String nextElement() {
                    throw new NoSuchElementException();
                }
            };
        }
    }

    /**
     * Returns an enumeration of the zip file entries
     * excluding internal JAR mechanism entries and including
     * signed entries missing from the ZIP directory.
     */
    Enumeration<JarEntry> entries2() {
        ensureInitialization();
        if (jv != null) {
            return jv.entries2(this, super.entries());
        }

        // screen out entries which are never signed
        final Enumeration<? extends ZipEntry> enum_ = super.entries();
        return new Enumeration<JarEntry>() {

            ZipEntry entry;

            public boolean hasMoreElements() {
                if (entry != null) {
                    return true;
                }
                while (enum_.hasMoreElements()) {
                    ZipEntry ze = enum_.nextElement();
                    if (java.util.jar.JarVerifier.isSigningRelated(ze.getName())) {
                        continue;
                    }
                    entry = ze;
                    return true;
                }
                return false;
            }

            public JarFileEntry nextElement() {
                if (hasMoreElements()) {
                    ZipEntry ze = entry;
                    entry = null;
                    return new JarFileEntry(ze);
                }
                throw new NoSuchElementException();
            }
        };
    }

    CodeSource[] getCodeSources(URL url) {
        ensureInitialization();
        if (jv != null) {
            return jv.getCodeSources(this, url);
        }

        /*
         * JAR file has no signed content. Is there a non-signing
         * code source?
         */
        Enumeration<String> unsigned = unsignedEntryNames();
        if (unsigned.hasMoreElements()) {
            return new CodeSource[]{java.util.jar.JarVerifier.getUnsignedCS(url)};
        } else {
            return null;
        }
    }

    private Enumeration<String> unsignedEntryNames() {
        final Enumeration<JarEntry> entries = entries();
        return new Enumeration<String>() {

            String name;

            /*
             * Grab entries from ZIP directory but screen out
             * metadata.
             */
            public boolean hasMoreElements() {
                if (name != null) {
                    return true;
                }
                while (entries.hasMoreElements()) {
                    String value;
                    ZipEntry e = entries.nextElement();
                    value = e.getName();
                    if (e.isDirectory() || java.util.jar.JarVerifier.isSigningRelated(value)) {
                        continue;
                    }
                    name = value;
                    return true;
                }
                return false;
            }

            public String nextElement() {
                if (hasMoreElements()) {
                    String value = name;
                    name = null;
                    return value;
                }
                throw new NoSuchElementException();
            }
        };
    }

    CodeSource getCodeSource(URL url, String name) {
        ensureInitialization();
        if (jv != null) {
            if (jv.eagerValidation) {
                CodeSource cs = null;
                JarEntry je = getJarEntry(name);
                if (je != null) {
                    cs = jv.getCodeSource(url, this, je);
                } else {
                    cs = jv.getCodeSource(url, name);
                }
                return cs;
            } else {
                return jv.getCodeSource(url, name);
            }
        }

        return java.util.jar.JarVerifier.getUnsignedCS(url);
    }

    void setEagerValidation(boolean eager) {
        try {
            maybeInstantiateVerifier();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (jv != null) {
            jv.setEagerValidation(eager);
        }
    }

    List<Object> getManifestDigests() {
        ensureInitialization();
        if (jv != null) {
            return jv.getManifestDigests();
        }
        return new ArrayList<Object>();
    }
}
