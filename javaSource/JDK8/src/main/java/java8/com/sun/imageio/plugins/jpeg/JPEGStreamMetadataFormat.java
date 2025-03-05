/*
 * Copyright (c) 2001, Oracle and/or its affiliates. All rights reserved.
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

package java8.sun.imageio.plugins.jpeg;

import com.sun.imageio.plugins.jpeg.JPEG;

import javax.imageio.metadata.IIOMetadataFormat;

public class JPEGStreamMetadataFormat extends JPEGMetadataFormat {

    private static com.sun.imageio.plugins.jpeg.JPEGStreamMetadataFormat theInstance = null;

    private JPEGStreamMetadataFormat() {
        super(JPEG.nativeStreamMetadataFormatName,
              CHILD_POLICY_SEQUENCE);
        addStreamElements(getRootName());
    }

    public static synchronized IIOMetadataFormat getInstance() {
        if (theInstance == null) {
            theInstance = new com.sun.imageio.plugins.jpeg.JPEGStreamMetadataFormat();
        }
        return theInstance;
    }
}
