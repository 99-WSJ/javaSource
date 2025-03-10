/*
 * Copyright (c) 2000, 2007, Oracle and/or its affiliates. All rights reserved.
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

package java8.javax.print;

import java.io.OutputStream;

import java.util.ArrayList;
import java.util.Iterator;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.StreamPrintService;

import sun.awt.AppContext;
import java.util.ServiceLoader;
import java.util.ServiceConfigurationError;

/**
 * A <code>StreamPrintServiceFactory</code> is the factory for
 * {@link StreamPrintService} instances,
 * which can print to an output stream in a particular
 * document format described as a mime type.
 * A typical output document format may be Postscript(TM).
 * <p>
 * This class is implemented by a service and located by the
 * implementation using the
 * <a href="../../../technotes/guides/jar/jar.html#Service Provider">
 * SPI JAR File specification</a>.
 * <p>
 * Applications locate instances of this class by calling the
 * {@link #lookupStreamPrintServiceFactories(DocFlavor, String)} method.
 * <p>
 * Applications can use a <code>StreamPrintService</code> obtained from a
 * factory in place of a <code>PrintService</code> which represents a
 * physical printer device.
 */

public abstract class StreamPrintServiceFactory {

    static class Services {
        private ArrayList listOfFactories = null;
    }

    private static Services getServices() {
        Services services =
            (Services)AppContext.getAppContext().get(Services.class);
        if (services == null) {
            services = new Services();
            AppContext.getAppContext().put(Services.class, services);
        }
        return services;
    }

    private static ArrayList getListOfFactories() {
        return getServices().listOfFactories;
    }

    private static ArrayList initListOfFactories() {
        ArrayList listOfFactories = new ArrayList();
        getServices().listOfFactories = listOfFactories;
        return listOfFactories;
    }

    /**
     * Locates factories for print services that can be used with
     * a print job to output a stream of data in the
     * format specified by {@code outputMimeType}.
     * <p>
     * The {@code outputMimeType} parameter describes the document type that
     * you want to create, whereas the {@code flavor} parameter describes the
     * format in which the input data will be provided by the application
     * to the {@code StreamPrintService}.
     * <p>
     * Although null is an acceptable value to use in the lookup of stream
     * printing services, it's typical to search for a particular
     * desired format, such as Postscript(TM).
     * <p>
     * @param flavor of the input document type - null means match all
     * types.
     * @param outputMimeType representing the required output format, used to
     * identify suitable stream printer factories. A value of null means
     * match all formats.
     * @return - matching factories for stream print service instance,
     *           empty if no suitable factories could be located.
     */
     public static javax.print.StreamPrintServiceFactory[]
         lookupStreamPrintServiceFactories(DocFlavor flavor,
                                           String outputMimeType) {

         ArrayList list = getFactories(flavor, outputMimeType);
         return (javax.print.StreamPrintServiceFactory[])
               (list.toArray(new javax.print.StreamPrintServiceFactory[list.size()]));
     }

    /** Queries the factory for the document format that is emitted
     * by printers obtained from this factory.
     *
     * @return the output format described as a mime type.
     */
    public abstract String getOutputFormat();

    /**
     * Queries the factory for the document flavors that can be accepted
     * by printers obtained from this factory.
     * @return array of supported doc flavors.
     */
    public abstract DocFlavor[] getSupportedDocFlavors();

    /**
     * Returns a <code>StreamPrintService</code> that can print to
     * the specified output stream.
     * The output stream is created and managed by the application.
     * It is the application's responsibility to close the stream and
     * to ensure that this Printer is not reused.
     * The application should not close this stream until any print job
     * created from the printer is complete. Doing so earlier may generate
     * a <code>PrinterException</code> and an event indicating that the
     * job failed.
     * <p>
     * Whereas a <code>PrintService</code> connected to a physical printer
     * can be reused,
     * a <code>StreamPrintService</code> connected to a stream cannot.
     * The underlying <code>StreamPrintService</code> may be disposed by
     * the print system with
     * the {@link StreamPrintService#dispose() dispose} method
     * before returning from the
     * {@link DocPrintJob#print(Doc, javax.print.attribute.PrintRequestAttributeSet) print}
     * method of <code>DocPrintJob</code> so that the print system knows
     * this printer is no longer usable.
     * This is equivalent to a physical printer going offline - permanently.
     * Applications may supply a null print stream to create a queryable
     * service. It is not valid to create a PrintJob for such a stream.
     * Implementations which allocate resources on construction should examine
     * the stream and may wish to only allocate resources if the stream is
     * non-null.
     * <p>
     * @param out destination stream for generated output.
     * @return a PrintService which will generate the format specified by the
     * DocFlavor supported by this Factory.
     */
    public abstract StreamPrintService getPrintService(OutputStream out);


    private static ArrayList getAllFactories() {
        synchronized (javax.print.StreamPrintServiceFactory.class) {

          ArrayList listOfFactories = getListOfFactories();
            if (listOfFactories != null) {
                return listOfFactories;
            } else {
                listOfFactories = initListOfFactories();
            }

            try {
                java.security.AccessController.doPrivileged(
                     new java.security.PrivilegedExceptionAction() {
                        public Object run() {
                            Iterator<javax.print.StreamPrintServiceFactory> iterator =
                                ServiceLoader.load
                                (javax.print.StreamPrintServiceFactory.class).iterator();
                            ArrayList lof = getListOfFactories();
                            while (iterator.hasNext()) {
                                try {
                                    lof.add(iterator.next());
                                }  catch (ServiceConfigurationError err) {
                                     /* In the applet case, we continue */
                                    if (System.getSecurityManager() != null) {
                                        err.printStackTrace();
                                    } else {
                                        throw err;
                                    }
                                }
                            }
                            return null;
                        }
                });
            } catch (java.security.PrivilegedActionException e) {
            }
            return listOfFactories;
        }
    }

    private static boolean isMember(DocFlavor flavor, DocFlavor[] flavors) {
        for (int f=0; f<flavors.length; f++ ) {
            if (flavor.equals(flavors[f])) {
                return true;
            }
        }
        return false;
    }

    private static ArrayList getFactories(DocFlavor flavor, String outType) {

        if (flavor == null && outType == null) {
            return getAllFactories();
        }

        ArrayList list = new ArrayList();
        Iterator iterator = getAllFactories().iterator();
        while (iterator.hasNext()) {
            javax.print.StreamPrintServiceFactory factory =
                (javax.print.StreamPrintServiceFactory)iterator.next();
            if ((outType == null ||
                 outType.equalsIgnoreCase(factory.getOutputFormat())) &&
                (flavor == null ||
                 isMember(flavor, factory.getSupportedDocFlavors()))) {
                list.add(factory);
            }
        }

        return list;
    }

}
