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

package java8.sun.imageio.plugins.wbmp;

import com.sun.imageio.plugins.common.ReaderUtil;
import com.sun.imageio.plugins.wbmp.WBMPImageReader;
import com.sun.imageio.plugins.wbmp.WBMPMetadata;

import javax.imageio.IIOException;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.util.Locale;

public class WBMPImageReaderSpi extends ImageReaderSpi {

    private static final int MAX_WBMP_WIDTH = 1024;
    private static final int MAX_WBMP_HEIGHT = 768;

    private static String [] writerSpiNames =
        {"com.sun.imageio.plugins.wbmp.WBMPImageWriterSpi"};
    private static String[] formatNames = {"wbmp", "WBMP"};
    private static String[] entensions = {"wbmp"};
    private static String[] mimeType = {"image/vnd.wap.wbmp"};

    private boolean registered = false;

    public WBMPImageReaderSpi() {
        super("Oracle Corporation",
              "1.0",
              formatNames,
              entensions,
              mimeType,
              "com.sun.imageio.plugins.wbmp.WBMPImageReader",
              new Class[] { ImageInputStream.class },
              writerSpiNames,
              true,
              null, null, null, null,
              true,
              WBMPMetadata.nativeMetadataFormatName,
              "com.sun.imageio.plugins.wbmp.WBMPMetadataFormat",
              null, null);
    }

    public void onRegistration(ServiceRegistry registry,
                               Class<?> category) {
        if (registered) {
            return;
        }
        registered = true;
    }

    public String getDescription(Locale locale) {
        return "Standard WBMP Image Reader";
    }

    public boolean canDecodeInput(Object source) throws IOException {
        if (!(source instanceof ImageInputStream)) {
            return false;
        }

        ImageInputStream stream = (ImageInputStream)source;

        stream.mark();
        try {
            int type = stream.readByte();   // TypeField
            int fixHeaderField = stream.readByte();
            // check WBMP "header"
            if (type != 0 || fixHeaderField != 0) {
                // while WBMP reader does not support ext WBMP headers
                return false;
            }

            int width = ReaderUtil.readMultiByteInteger(stream);
            int height = ReaderUtil.readMultiByteInteger(stream);
            // check image dimension
            if (width <= 0 || height <= 0) {
                return false;
            }

            long dataLength = stream.length();
            if (dataLength == -1) {
                // We can't verify that amount of data in the stream
                // corresponds to image dimension because we do not know
                // the length of the data stream.
                // Assuming that wbmp image are used for mobile devices,
                // let's introduce an upper limit for image dimension.
                // In case if exact amount of raster data is unknown,
                // let's reject images with dimension above the limit.
                return (width < MAX_WBMP_WIDTH) && (height < MAX_WBMP_HEIGHT);
            }

            dataLength -= stream.getStreamPosition();

            long scanSize = (width / 8) + ((width % 8) == 0 ? 0 : 1);

            return (dataLength == scanSize * height);
        } finally {
            stream.reset();
        }
    }

    public ImageReader createReaderInstance(Object extension)
        throws IIOException {
        return new WBMPImageReader(this);
    }
}
