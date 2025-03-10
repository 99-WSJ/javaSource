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

import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadataFormat;
import java.awt.color.ICC_Profile;
import java.util.ArrayList;
import java.util.List;

public class JPEGImageMetadataFormat extends JPEGMetadataFormat {

    private static com.sun.imageio.plugins.jpeg.JPEGImageMetadataFormat theInstance = null;

    private JPEGImageMetadataFormat() {
        super(JPEG.nativeImageMetadataFormatName,
              CHILD_POLICY_ALL);

        addElement("JPEGvariety",
                   JPEG.nativeImageMetadataFormatName,
                   CHILD_POLICY_CHOICE);

        addElement("markerSequence",
                   JPEG.nativeImageMetadataFormatName,
                   CHILD_POLICY_SEQUENCE);

        addElement("app0JFIF", "JPEGvariety", CHILD_POLICY_SOME);

        addStreamElements("markerSequence");

        addElement("app14Adobe", "markerSequence", CHILD_POLICY_EMPTY);

        addElement("sof", "markerSequence", 1, 4);

        addElement("sos", "markerSequence", 1, 4);

        addElement("JFXX", "app0JFIF", 1, Integer.MAX_VALUE);

        addElement("app0JFXX", "JFXX", CHILD_POLICY_CHOICE);

        addElement("app2ICC", "app0JFIF", CHILD_POLICY_EMPTY);

        addAttribute("app0JFIF",
                     "majorVersion",
                     DATATYPE_INTEGER,
                     false,
                     "1",
                     "0", "255",
                     true, true);
        addAttribute("app0JFIF",
                     "minorVersion",
                     DATATYPE_INTEGER,
                     false,
                     "2",
                     "0", "255",
                     true, true);
        List resUnits = new ArrayList();
        resUnits.add("0");
        resUnits.add("1");
        resUnits.add("2");
        addAttribute("app0JFIF",
                     "resUnits",
                     DATATYPE_INTEGER,
                     false,
                     "0",
                     resUnits);
        addAttribute("app0JFIF",
                     "Xdensity",
                     DATATYPE_INTEGER,
                     false,
                     "1",
                     "1", "65535",
                     true, true);
        addAttribute("app0JFIF",
                     "Ydensity",
                     DATATYPE_INTEGER,
                     false,
                     "1",
                     "1", "65535",
                     true, true);
        addAttribute("app0JFIF",
                     "thumbWidth",
                     DATATYPE_INTEGER,
                     false,
                     "0",
                     "0", "255",
                     true, true);
        addAttribute("app0JFIF",
                     "thumbHeight",
                     DATATYPE_INTEGER,
                     false,
                     "0",
                     "0", "255",
                     true, true);

        addElement("JFIFthumbJPEG", "app0JFXX", CHILD_POLICY_SOME);
        addElement("JFIFthumbPalette", "app0JFXX", CHILD_POLICY_EMPTY);
        addElement("JFIFthumbRGB", "app0JFXX", CHILD_POLICY_EMPTY);

        List codes = new ArrayList();
        codes.add("16"); // Hex 10
        codes.add("17"); // Hex 11
        codes.add("19"); // Hex 13
        addAttribute("app0JFXX",
                     "extensionCode",
                     DATATYPE_INTEGER,
                     false,
                     null,
                     codes);

        addChildElement("markerSequence", "JFIFthumbJPEG");

        addAttribute("JFIFthumbPalette",
                     "thumbWidth",
                     DATATYPE_INTEGER,
                     false,
                     null,
                     "0", "255",
                     true, true);
        addAttribute("JFIFthumbPalette",
                     "thumbHeight",
                     DATATYPE_INTEGER,
                     false,
                     null,
                     "0", "255",
                     true, true);

        addAttribute("JFIFthumbRGB",
                     "thumbWidth",
                     DATATYPE_INTEGER,
                     false,
                     null,
                     "0", "255",
                     true, true);
        addAttribute("JFIFthumbRGB",
                     "thumbHeight",
                     DATATYPE_INTEGER,
                     false,
                     null,
                     "0", "255",
                     true, true);

        addObjectValue("app2ICC", ICC_Profile.class, false, null);

        addAttribute("app14Adobe",
                     "version",
                     DATATYPE_INTEGER,
                     false,
                     "100",
                     "100", "255",
                     true, true);
        addAttribute("app14Adobe",
                     "flags0",
                     DATATYPE_INTEGER,
                     false,
                     "0",
                     "0", "65535",
                     true, true);
        addAttribute("app14Adobe",
                     "flags1",
                     DATATYPE_INTEGER,
                     false,
                     "0",
                     "0", "65535",
                     true, true);

        List transforms = new ArrayList();
        transforms.add("0");
        transforms.add("1");
        transforms.add("2");
        addAttribute("app14Adobe",
                     "transform",
                     DATATYPE_INTEGER,
                     true,
                     null,
                     transforms);

        addElement("componentSpec", "sof", CHILD_POLICY_EMPTY);

        List procs = new ArrayList();
        procs.add("0");
        procs.add("1");
        procs.add("2");
        addAttribute("sof",
                     "process",
                     DATATYPE_INTEGER,
                     false,
                     null,
                     procs);
        addAttribute("sof",
                     "samplePrecision",
                     DATATYPE_INTEGER,
                     false,
                     "8");
        addAttribute("sof",
                     "numLines",
                     DATATYPE_INTEGER,
                     false,
                     null,
                     "0", "65535",
                     true, true);
        addAttribute("sof",
                     "samplesPerLine",
                     DATATYPE_INTEGER,
                     false,
                     null,
                     "0", "65535",
                     true, true);
        List comps = new ArrayList();
        comps.add("1");
        comps.add("2");
        comps.add("3");
        comps.add("4");
        addAttribute("sof",
                     "numFrameComponents",
                     DATATYPE_INTEGER,
                     false,
                     null,
                     comps);

        addAttribute("componentSpec",
                     "componentId",
                     DATATYPE_INTEGER,
                     true,
                     null,
                     "0", "255",
                     true, true);
        addAttribute("componentSpec",
                     "HsamplingFactor",
                     DATATYPE_INTEGER,
                     true,
                     null,
                     "1", "255",
                     true, true);
        addAttribute("componentSpec",
                     "VsamplingFactor",
                     DATATYPE_INTEGER,
                     true,
                     null,
                     "1", "255",
                     true, true);
        List tabids = new ArrayList();
        tabids.add("0");
        tabids.add("1");
        tabids.add("2");
        tabids.add("3");
        addAttribute("componentSpec",
                     "QtableSelector",
                     DATATYPE_INTEGER,
                     true,
                     null,
                     tabids);

        addElement("scanComponentSpec", "sos", CHILD_POLICY_EMPTY);

        addAttribute("sos",
                     "numScanComponents",
                     DATATYPE_INTEGER,
                     true,
                     null,
                     comps);
        addAttribute("sos",
                     "startSpectralSelection",
                      DATATYPE_INTEGER,
                     false,
                     "0",
                     "0", "63",
                     true, true);
        addAttribute("sos",
                     "endSpectralSelection",
                      DATATYPE_INTEGER,
                     false,
                     "63",
                     "0", "63",
                     true, true);
        addAttribute("sos",
                     "approxHigh",
                      DATATYPE_INTEGER,
                     false,
                     "0",
                     "0", "15",
                     true, true);
        addAttribute("sos",
                     "approxLow",
                      DATATYPE_INTEGER,
                     false,
                     "0",
                     "0", "15",
                     true, true);

        addAttribute("scanComponentSpec",
                     "componentSelector",
                     DATATYPE_INTEGER,
                     true,
                     null,
                     "0", "255",
                     true, true);
        addAttribute("scanComponentSpec",
                     "dcHuffTable",
                     DATATYPE_INTEGER,
                     true,
                     null,
                     tabids);
        addAttribute("scanComponentSpec",
                     "acHuffTable",
                     DATATYPE_INTEGER,
                     true,
                     null,
                     tabids);
    }

    public boolean canNodeAppear(String elementName,
                                 ImageTypeSpecifier imageType) {
        // All images can have these
        if (elementName.equals(getRootName())
            || elementName.equals("JPEGvariety")
            || isInSubtree(elementName, "markerSequence")) {
            return true;
        }

        // If it is an element in the app0jfif subtree, just check
        // that the image type is JFIF compliant.
        if ((isInSubtree(elementName, "app0JFIF"))
            && JPEG.isJFIFcompliant(imageType, true)) {
            return true;
        }

        return false;
    }


    public static synchronized IIOMetadataFormat getInstance() {
        if (theInstance == null) {
            theInstance = new com.sun.imageio.plugins.jpeg.JPEGImageMetadataFormat();
        }
        return theInstance;
    }
}
