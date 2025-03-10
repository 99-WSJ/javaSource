/*
 * Copyright (c) 2002, 2003, Oracle and/or its affiliates. All rights reserved.
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

package java8.sun.jmx.snmp.defaults;


// java import
//

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * This class represents a set of default directories used by Java DMK.
 *
 * <p><b>This API is a Sun Microsystems internal API  and is subject
 * to change without notice.</b></p>
 * @since 1.5
 */
public class DefaultPaths {
    private static final String INSTALL_PATH_RESOURCE_NAME = "com/sun/jdmk/defaults/install.path";
    // private constructor defined to "hide" the default public constructor
    private DefaultPaths() {

    }

    // PUBLIC STATIC METHODS
    //----------------------

    /**
     * Returns the installation directory for Java DMK.
     *
     * The default value of the installation directory is:
     * <CODE>&lt;base_dir&gt; + File.separator + SUNWjdmk + File.separator + jdmk5.0 </CODE>
     *
     * @return Java DMK installation directory.
     */
    public static String getInstallDir() {
        if (installDir == null)
            return useRessourceFile();
        else
            return installDir;
    }

    /**
     * Returns the installation directory for Java DMK concatenated with dirname.
     *
     * The default value of the installation directory is:
     * <CODE>&lt;base_dir&gt; + File.separator + SUNWjdmk + File.separator + jdmk5.0 </CODE>
     *
     * @param dirname The directory to be appended.
     *
     * @return Java DMK installation directory + <CODE>File.separator</CODE> + <CODE>dirname</CODE>.
     */
    public static String getInstallDir(String dirname) {
        if (installDir == null) {
            if (dirname == null) {
                return getInstallDir();
            } else {
                return getInstallDir() + File.separator + dirname;
            }
        } else {
            if (dirname == null) {
                return installDir;
            } else {
                return installDir + File.separator + dirname;
            }
        }
    }

    /**
     * Sets the installation directory for Java DMK.
     *
     * @param dirname The directory where Java DMK resides.
     */
    public static void setInstallDir(String dirname) {
        installDir = dirname;
    }

    /**
     * Returns the <CODE>etc</CODE> directory for Java DMK.
     * <P>
     * The default value of the <CODE>etc</CODE> directory is:
     * <UL>
     * <LI><CODE>DefaultPaths.getInstallDir("etc")</CODE>.
     * </UL>
     *
     * @return Java DMK <CODE>etc</CODE> directory.
     */
    public static String getEtcDir() {
        if (etcDir == null)
            return getInstallDir("etc");
        else
            return etcDir;
    }

    /**
     * Returns the <CODE>etc</CODE> directory for Java DMK concatenated with dirname.
     * <P>
     * The default value of the <CODE>etc</CODE> directory is:
     * <UL>
     * <LI><CODE>DefaultPaths.getInstallDir("etc")</CODE>.
     * </UL>
     *
     * @param dirname The directory to be appended.
     *
     * @return Java DMK <CODE>etc</CODE> directory + <CODE>File.separator</CODE> + <CODE>dirname</CODE>.
     */
    public static String getEtcDir(String dirname) {
        if (etcDir == null) {
            if (dirname == null) {
                return getEtcDir();
            } else {
                return getEtcDir() + File.separator + dirname;
            }
        } else {
            if (dirname == null) {
                return etcDir;
            } else {
                return etcDir + File.separator + dirname;
            }
        }
    }

    /**
     * Sets the <CODE>etc</CODE> directory for Java DMK.
     *
     * @param dirname The <CODE>etc</CODE> directory for Java DMK.
     */
    public static void setEtcDir(String dirname) {
        etcDir = dirname;
    }

    /**
     * Returns the <CODE>tmp</CODE> directory for the product.
     * <P>
     * The default value of the <CODE>tmp</CODE> directory is:
     * <UL>
     * <LI><CODE>DefaultPaths.getInstallDir("tmp")</CODE>.
     * </UL>
     *
     * @return Java DMK <CODE>tmp</CODE> directory.
     */
    public static String getTmpDir() {
         if (tmpDir == null)
            return getInstallDir("tmp");
        else
            return tmpDir;
    }

    /**
     * Returns the <CODE>tmp</CODE> directory for Java DMK concatenated with dirname.
     * <P>
     * The default value of the <CODE>tmp</CODE> directory is:
     * <UL>
     * <LI><CODE>DefaultPaths.getInstallDir("tmp")</CODE>.
     * </UL>
     *
     * @param dirname The directory to be appended.
     *
     * @return Java DMK <CODE>tmp</CODE> directory + <CODE>File.separator</CODE> + <CODE>dirname</CODE>.
     */
    public static String getTmpDir(String dirname) {
        if (tmpDir == null) {
            if (dirname == null) {
                return getTmpDir();
            } else {
                return getTmpDir() + File.separator + dirname;
            }
        } else {
            if (dirname == null) {
                return tmpDir;
            } else {
                return tmpDir + File.separator + dirname;
            }
        }
    }

    /**
     * Sets the <CODE>tmp</CODE> directory for the product
     *
     * @param dirname The <CODE>tmp</CODE> directory for Java DMK.
     */
    public static void setTmpDir(String dirname) {
        tmpDir = dirname;
    }


    // PRIVATE STATIC METHODS
    //-----------------------

    private static String useRessourceFile() {
        InputStream in = null;
        BufferedReader r = null;
        try {
            in =
                com.sun.jmx.snmp.defaults.DefaultPaths.class.getClassLoader().getResourceAsStream(INSTALL_PATH_RESOURCE_NAME);
            if(in == null) return null;
            r = new BufferedReader(new InputStreamReader(in));
            installDir = r.readLine();
        }catch(Exception e) {
        }
        finally {
            try {
                if(in != null) in.close();
                if(r != null) r.close();
            }catch(Exception e) {}
        }
        return installDir;
    }

    // PRIVATE VARIABLES
    //------------------

    /**
     * Directories used by Java DMK.
     */
    private static String etcDir;
    private static String tmpDir;
    private static String installDir;
}
