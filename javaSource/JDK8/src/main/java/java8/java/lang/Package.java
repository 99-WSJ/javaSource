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

package java8.java.lang;

import sun.net.www.ParseUtil;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

/**
 * {@code Package} objects contain version information
 * about the implementation and specification of a Java package.
 * This versioning information is retrieved and made available
 * by the {@link java.lang.ClassLoader} instance that
 * loaded the class(es).  Typically, it is stored in the manifest that is
 * distributed with the classes.
 *
 * <p>The set of classes that make up the package may implement a
 * particular specification and if so the specification title, version number,
 * and vendor strings identify that specification.
 * An application can ask if the package is
 * compatible with a particular version, see the {@link
 * #isCompatibleWith isCompatibleWith}
 * method for details.
 *
 * <p>Specification version numbers use a syntax that consists of nonnegative
 * decimal integers separated by periods ".", for example "2.0" or
 * "1.2.3.4.5.6.7".  This allows an extensible number to be used to represent
 * major, minor, micro, etc. versions.  The version specification is described
 * by the following formal grammar:
 * <blockquote>
 * <dl>
 * <dt><i>SpecificationVersion:</i>
 * <dd><i>Digits RefinedVersion<sub>opt</sub></i>

 * <dt><i>RefinedVersion:</i>
 * <dd>{@code .} <i>Digits</i>
 * <dd>{@code .} <i>Digits RefinedVersion</i>
 *
 * <dt><i>Digits:</i>
 * <dd><i>Digit</i>
 * <dd><i>Digits</i>
 *
 * <dt><i>Digit:</i>
 * <dd>any character for which {@link Character#isDigit} returns {@code true},
 * e.g. 0, 1, 2, ...
 * </dl>
 * </blockquote>
 *
 * <p>The implementation title, version, and vendor strings identify an
 * implementation and are made available conveniently to enable accurate
 * reporting of the packages involved when a problem occurs. The contents
 * all three implementation strings are vendor specific. The
 * implementation version strings have no specified syntax and should
 * only be compared for equality with desired version identifiers.
 *
 * <p>Within each {@code ClassLoader} instance all classes from the same
 * java package have the same Package object.  The static methods allow a package
 * to be found by name or the set of all packages known to the current class
 * loader to be found.
 *
 * @see java.lang.ClassLoader#definePackage
 */
public class Package implements AnnotatedElement {
    /**
     * Return the name of this package.
     *
     * @return  The fully-qualified name of this package as defined in section 6.5.3 of
     *          <cite>The Java&trade; Language Specification</cite>,
     *          for example, {@code java.lang}
     */
    public String getName() {
        return pkgName;
    }


    /**
     * Return the title of the specification that this package implements.
     * @return the specification title, null is returned if it is not known.
     */
    public String getSpecificationTitle() {
        return specTitle;
    }

    /**
     * Returns the version number of the specification
     * that this package implements.
     * This version string must be a sequence of nonnegative decimal
     * integers separated by "."'s and may have leading zeros.
     * When version strings are compared the most significant
     * numbers are compared.
     * @return the specification version, null is returned if it is not known.
     */
    public String getSpecificationVersion() {
        return specVersion;
    }

    /**
     * Return the name of the organization, vendor,
     * or company that owns and maintains the specification
     * of the classes that implement this package.
     * @return the specification vendor, null is returned if it is not known.
     */
    public String getSpecificationVendor() {
        return specVendor;
    }

    /**
     * Return the title of this package.
     * @return the title of the implementation, null is returned if it is not known.
     */
    public String getImplementationTitle() {
        return implTitle;
    }

    /**
     * Return the version of this implementation. It consists of any string
     * assigned by the vendor of this implementation and does
     * not have any particular syntax specified or expected by the Java
     * runtime. It may be compared for equality with other
     * package version strings used for this implementation
     * by this vendor for this package.
     * @return the version of the implementation, null is returned if it is not known.
     */
    public String getImplementationVersion() {
        return implVersion;
    }

    /**
     * Returns the name of the organization,
     * vendor or company that provided this implementation.
     * @return the vendor that implemented this package..
     */
    public String getImplementationVendor() {
        return implVendor;
    }

    /**
     * Returns true if this package is sealed.
     *
     * @return true if the package is sealed, false otherwise
     */
    public boolean isSealed() {
        return sealBase != null;
    }

    /**
     * Returns true if this package is sealed with respect to the specified
     * code source url.
     *
     * @param url the code source url
     * @return true if this package is sealed with respect to url
     */
    public boolean isSealed(URL url) {
        return url.equals(sealBase);
    }

    /**
     * Compare this package's specification version with a
     * desired version. It returns true if
     * this packages specification version number is greater than or equal
     * to the desired version number. <p>
     *
     * Version numbers are compared by sequentially comparing corresponding
     * components of the desired and specification strings.
     * Each component is converted as a decimal integer and the values
     * compared.
     * If the specification value is greater than the desired
     * value true is returned. If the value is less false is returned.
     * If the values are equal the period is skipped and the next pair of
     * components is compared.
     *
     * @param desired the version string of the desired version.
     * @return true if this package's version number is greater
     *          than or equal to the desired version number
     *
     * @exception NumberFormatException if the desired or current version
     *          is not of the correct dotted form.
     */
    public boolean isCompatibleWith(String desired)
        throws NumberFormatException
    {
        if (specVersion == null || specVersion.length() < 1) {
            throw new NumberFormatException("Empty version string");
        }

        String [] sa = specVersion.split("\\.", -1);
        int [] si = new int[sa.length];
        for (int i = 0; i < sa.length; i++) {
            si[i] = Integer.parseInt(sa[i]);
            if (si[i] < 0)
                throw NumberFormatException.forInputString("" + si[i]);
        }

        String [] da = desired.split("\\.", -1);
        int [] di = new int[da.length];
        for (int i = 0; i < da.length; i++) {
            di[i] = Integer.parseInt(da[i]);
            if (di[i] < 0)
                throw NumberFormatException.forInputString("" + di[i]);
        }

        int len = java.lang.Math.max(di.length, si.length);
        for (int i = 0; i < len; i++) {
            int d = (i < di.length ? di[i] : 0);
            int s = (i < si.length ? si[i] : 0);
            if (s < d)
                return false;
            if (s > d)
                return true;
        }
        return true;
    }

    /**
     * Find a package by name in the callers {@code ClassLoader} instance.
     * The callers {@code ClassLoader} instance is used to find the package
     * instance corresponding to the named class. If the callers
     * {@code ClassLoader} instance is null then the set of packages loaded
     * by the system {@code ClassLoader} instance is searched to find the
     * named package. <p>
     *
     * Packages have attributes for versions and specifications only if the class
     * loader created the package instance with the appropriate attributes. Typically,
     * those attributes are defined in the manifests that accompany the classes.
     *
     * @param name a package name, for example, java.lang.
     * @return the package of the requested name. It may be null if no package
     *          information is available from the archive or codebase.
     */
    @CallerSensitive
    public static java.lang.Package getPackage(String name) {
        java.lang.ClassLoader l = java.lang.ClassLoader.getClassLoader(Reflection.getCallerClass());
        if (l != null) {
            return l.getPackage(name);
        } else {
            return getSystemPackage(name);
        }
    }

    /**
     * Get all the packages currently known for the caller's {@code ClassLoader}
     * instance.  Those packages correspond to classes loaded via or accessible by
     * name to that {@code ClassLoader} instance.  If the caller's
     * {@code ClassLoader} instance is the bootstrap {@code ClassLoader}
     * instance, which may be represented by {@code null} in some implementations,
     * only packages corresponding to classes loaded by the bootstrap
     * {@code ClassLoader} instance will be returned.
     *
     * @return a new array of packages known to the callers {@code ClassLoader}
     * instance.  An zero length array is returned if none are known.
     */
    @CallerSensitive
    public static java.lang.Package[] getPackages() {
        java.lang.ClassLoader l = java.lang.ClassLoader.getClassLoader(Reflection.getCallerClass());
        if (l != null) {
            return l.getPackages();
        } else {
            return getSystemPackages();
        }
    }

    /**
     * Get the package for the specified class.
     * The class's class loader is used to find the package instance
     * corresponding to the specified class. If the class loader
     * is the bootstrap class loader, which may be represented by
     * {@code null} in some implementations, then the set of packages
     * loaded by the bootstrap class loader is searched to find the package.
     * <p>
     * Packages have attributes for versions and specifications only
     * if the class loader created the package
     * instance with the appropriate attributes. Typically those
     * attributes are defined in the manifests that accompany
     * the classes.
     *
     * @param c the class to get the package of.
     * @return the package of the class. It may be null if no package
     *          information is available from the archive or codebase.  */
    static java.lang.Package getPackage(java.lang.Class<?> c) {
        String name = c.getName();
        int i = name.lastIndexOf('.');
        if (i != -1) {
            name = name.substring(0, i);
            java.lang.ClassLoader cl = c.getClassLoader();
            if (cl != null) {
                return cl.getPackage(name);
            } else {
                return getSystemPackage(name);
            }
        } else {
            return null;
        }
    }

    /**
     * Return the hash code computed from the package name.
     * @return the hash code computed from the package name.
     */
    public int hashCode(){
        return pkgName.hashCode();
    }

    /**
     * Returns the string representation of this Package.
     * Its value is the string "package " and the package name.
     * If the package title is defined it is appended.
     * If the package version is defined it is appended.
     * @return the string representation of the package.
     */
    public String toString() {
        String spec = specTitle;
        String ver =  specVersion;
        if (spec != null && spec.length() > 0)
            spec = ", " + spec;
        else
            spec = "";
        if (ver != null && ver.length() > 0)
            ver = ", version " + ver;
        else
            ver = "";
        return "package " + pkgName + spec + ver;
    }

    private java.lang.Class<?> getPackageInfo() {
        if (packageInfo == null) {
            try {
                packageInfo = java.lang.Class.forName(pkgName + ".package-info", false, loader);
            } catch (ClassNotFoundException ex) {
                // store a proxy for the package info that has no annotations
                class PackageInfoProxy {}
                packageInfo = PackageInfoProxy.class;
            }
        }
        return packageInfo;
    }

    /**
     * @throws NullPointerException {@inheritDoc}
     * @since 1.5
     */
    public <A extends Annotation> A getAnnotation(java.lang.Class<A> annotationClass) {
        return getPackageInfo().getAnnotation(annotationClass);
    }

    /**
     * {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     * @since 1.5
     */
    @Override
    public boolean isAnnotationPresent(java.lang.Class<? extends Annotation> annotationClass) {
        return AnnotatedElement.super.isAnnotationPresent(annotationClass);
    }

    /**
     * @throws NullPointerException {@inheritDoc}
     * @since 1.8
     */
    @Override
    public  <A extends Annotation> A[] getAnnotationsByType(java.lang.Class<A> annotationClass) {
        return getPackageInfo().getAnnotationsByType(annotationClass);
    }

    /**
     * @since 1.5
     */
    public Annotation[] getAnnotations() {
        return getPackageInfo().getAnnotations();
    }

    /**
     * @throws NullPointerException {@inheritDoc}
     * @since 1.8
     */
    @Override
    public <A extends Annotation> A getDeclaredAnnotation(java.lang.Class<A> annotationClass) {
        return getPackageInfo().getDeclaredAnnotation(annotationClass);
    }

    /**
     * @throws NullPointerException {@inheritDoc}
     * @since 1.8
     */
    @Override
    public <A extends Annotation> A[] getDeclaredAnnotationsByType(java.lang.Class<A> annotationClass) {
        return getPackageInfo().getDeclaredAnnotationsByType(annotationClass);
    }

    /**
     * @since 1.5
     */
    public Annotation[] getDeclaredAnnotations()  {
        return getPackageInfo().getDeclaredAnnotations();
    }

    /**
     * Construct a package instance with the specified version
     * information.
     * @param name the name of the package
     * @param spectitle the title of the specification
     * @param specversion the version of the specification
     * @param specvendor the organization that maintains the specification
     * @param impltitle the title of the implementation
     * @param implversion the version of the implementation
     * @param implvendor the organization that maintains the implementation
     */
    Package(String name,
            String spectitle, String specversion, String specvendor,
            String impltitle, String implversion, String implvendor,
            URL sealbase, java.lang.ClassLoader loader)
    {
        pkgName = name;
        implTitle = impltitle;
        implVersion = implversion;
        implVendor = implvendor;
        specTitle = spectitle;
        specVersion = specversion;
        specVendor = specvendor;
        sealBase = sealbase;
        this.loader = loader;
    }

    /*
     * Construct a package using the attributes from the specified manifest.
     *
     * @param name the package name
     * @param man the optional manifest for the package
     * @param url the optional code source url for the package
     */
    private Package(String name, Manifest man, URL url, java.lang.ClassLoader loader) {
        String path = name.replace('.', '/').concat("/");
        String sealed = null;
        String specTitle= null;
        String specVersion= null;
        String specVendor= null;
        String implTitle= null;
        String implVersion= null;
        String implVendor= null;
        URL sealBase= null;
        Attributes attr = man.getAttributes(path);
        if (attr != null) {
            specTitle   = attr.getValue(Name.SPECIFICATION_TITLE);
            specVersion = attr.getValue(Name.SPECIFICATION_VERSION);
            specVendor  = attr.getValue(Name.SPECIFICATION_VENDOR);
            implTitle   = attr.getValue(Name.IMPLEMENTATION_TITLE);
            implVersion = attr.getValue(Name.IMPLEMENTATION_VERSION);
            implVendor  = attr.getValue(Name.IMPLEMENTATION_VENDOR);
            sealed      = attr.getValue(Name.SEALED);
        }
        attr = man.getMainAttributes();
        if (attr != null) {
            if (specTitle == null) {
                specTitle = attr.getValue(Name.SPECIFICATION_TITLE);
            }
            if (specVersion == null) {
                specVersion = attr.getValue(Name.SPECIFICATION_VERSION);
            }
            if (specVendor == null) {
                specVendor = attr.getValue(Name.SPECIFICATION_VENDOR);
            }
            if (implTitle == null) {
                implTitle = attr.getValue(Name.IMPLEMENTATION_TITLE);
            }
            if (implVersion == null) {
                implVersion = attr.getValue(Name.IMPLEMENTATION_VERSION);
            }
            if (implVendor == null) {
                implVendor = attr.getValue(Name.IMPLEMENTATION_VENDOR);
            }
            if (sealed == null) {
                sealed = attr.getValue(Name.SEALED);
            }
        }
        if ("true".equalsIgnoreCase(sealed)) {
            sealBase = url;
        }
        pkgName = name;
        this.specTitle = specTitle;
        this.specVersion = specVersion;
        this.specVendor = specVendor;
        this.implTitle = implTitle;
        this.implVersion = implVersion;
        this.implVendor = implVendor;
        this.sealBase = sealBase;
        this.loader = loader;
    }

    /*
     * Returns the loaded system package for the specified name.
     */
    static java.lang.Package getSystemPackage(String name) {
        synchronized (pkgs) {
            java.lang.Package pkg = pkgs.get(name);
            if (pkg == null) {
                name = name.replace('.', '/').concat("/");
                String fn = getSystemPackage0(name);
                if (fn != null) {
                    pkg = defineSystemPackage(name, fn);
                }
            }
            return pkg;
        }
    }

    /*
     * Return an array of loaded system packages.
     */
    static java.lang.Package[] getSystemPackages() {
        // First, update the system package map with new package names
        String[] names = getSystemPackages0();
        synchronized (pkgs) {
            for (int i = 0; i < names.length; i++) {
                defineSystemPackage(names[i], getSystemPackage0(names[i]));
            }
            return pkgs.values().toArray(new java.lang.Package[pkgs.size()]);
        }
    }

    private static java.lang.Package defineSystemPackage(final String iname,
                                                         final String fn)
    {
        return AccessController.doPrivileged(new PrivilegedAction<java.lang.Package>() {
            public java.lang.Package run() {
                String name = iname;
                // Get the cached code source url for the file name
                URL url = urls.get(fn);
                if (url == null) {
                    // URL not found, so create one
                    File file = new File(fn);
                    try {
                        url = ParseUtil.fileToEncodedURL(file);
                    } catch (MalformedURLException e) {
                    }
                    if (url != null) {
                        urls.put(fn, url);
                        // If loading a JAR file, then also cache the manifest
                        if (file.isFile()) {
                            mans.put(fn, loadManifest(fn));
                        }
                    }
                }
                // Convert to "."-separated package name
                name = name.substring(0, name.length() - 1).replace('/', '.');
                java.lang.Package pkg;
                Manifest man = mans.get(fn);
                if (man != null) {
                    pkg = new java.lang.Package(name, man, url, null);
                } else {
                    pkg = new java.lang.Package(name, null, null, null,
                                      null, null, null, null, null);
                }
                pkgs.put(name, pkg);
                return pkg;
            }
        });
    }

    /*
     * Returns the Manifest for the specified JAR file name.
     */
    private static Manifest loadManifest(String fn) {
        try (FileInputStream fis = new FileInputStream(fn);
             JarInputStream jis = new JarInputStream(fis, false))
        {
            return jis.getManifest();
        } catch (IOException e) {
            return null;
        }
    }

    // The map of loaded system packages
    private static Map<String, java.lang.Package> pkgs = new HashMap<>(31);

    // Maps each directory or zip file name to its corresponding url
    private static Map<String, URL> urls = new HashMap<>(10);

    // Maps each code source url for a jar file to its manifest
    private static Map<String, Manifest> mans = new HashMap<>(10);

    private static native String getSystemPackage0(String name);
    private static native String[] getSystemPackages0();

    /*
     * Private storage for the package name and attributes.
     */
    private final String pkgName;
    private final String specTitle;
    private final String specVersion;
    private final String specVendor;
    private final String implTitle;
    private final String implVersion;
    private final String implVendor;
    private final URL sealBase;
    private transient final java.lang.ClassLoader loader;
    private transient java.lang.Class<?> packageInfo;
}
