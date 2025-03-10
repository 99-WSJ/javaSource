/*
 * Copyright (c) 2000, 2010, Oracle and/or its affiliates. All rights reserved.
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

package java8.com.sun.imageio.spi;

import javax.imageio.spi.ImageOutputStreamSpi;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.io.File;
import java.util.Locale;

public class FileImageOutputStreamSpi extends ImageOutputStreamSpi {

    private static final String vendorName = "Oracle Corporation";

    private static final String version = "1.0";

    private static final Class outputClass = File.class;

    public FileImageOutputStreamSpi() {
        super(vendorName, version, outputClass);
    }

    public String getDescription(Locale locale) {
        return "Service provider that instantiates a FileImageOutputStream from a File";
    }

    public ImageOutputStream createOutputStreamInstance(Object output,
                                                        boolean useCache,
                                                        File cacheDir) {
        if (output instanceof File) {
            try {
                return new FileImageOutputStream((File)output);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } else {
            throw new IllegalArgumentException();
        }
    }
}
