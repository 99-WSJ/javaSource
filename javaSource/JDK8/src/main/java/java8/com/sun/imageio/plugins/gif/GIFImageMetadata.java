/*
 * Copyright (c) 2000, 2005, Oracle and/or its affiliates. All rights reserved.
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

package java8.sun.imageio.plugins.gif;

import org.w3c.dom.Node;

import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadataFormatImpl;
import javax.imageio.metadata.IIOMetadataNode;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;

public class GIFImageMetadata extends GIFMetadata {

    // package scope
    static final String
        nativeMetadataFormatName = "javax_imageio_gif_image_1.0";

    static final String[] disposalMethodNames = {
        "none",
        "doNotDispose",
        "restoreToBackgroundColor",
        "restoreToPrevious",
        "undefinedDisposalMethod4",
        "undefinedDisposalMethod5",
        "undefinedDisposalMethod6",
        "undefinedDisposalMethod7"
    };

    // Fields from Image Descriptor
    public int imageLeftPosition;
    public int imageTopPosition;
    public int imageWidth;
    public int imageHeight;
    public boolean interlaceFlag = false;
    public boolean sortFlag = false;
    public byte[] localColorTable = null;

    // Fields from Graphic Control Extension
    public int disposalMethod = 0;
    public boolean userInputFlag = false;
    public boolean transparentColorFlag = false;
    public int delayTime = 0;
    public int transparentColorIndex = 0;

    // Fields from Plain Text Extension
    public boolean hasPlainTextExtension = false;
    public int textGridLeft;
    public int textGridTop;
    public int textGridWidth;
    public int textGridHeight;
    public int characterCellWidth;
    public int characterCellHeight;
    public int textForegroundColor;
    public int textBackgroundColor;
    public byte[] text;

    // Fields from ApplicationExtension
    // List of byte[]
    public List applicationIDs = null; // new ArrayList();

    // List of byte[]
    public List authenticationCodes = null; // new ArrayList();

    // List of byte[]
    public List applicationData = null; // new ArrayList();

    // Fields from CommentExtension
    // List of byte[]
    public List comments = null; // new ArrayList();

    protected GIFImageMetadata(boolean standardMetadataFormatSupported,
                               String nativeMetadataFormatName,
                               String nativeMetadataFormatClassName,
                               String[] extraMetadataFormatNames,
                               String[] extraMetadataFormatClassNames)
    {
        super(standardMetadataFormatSupported,
              nativeMetadataFormatName,
              nativeMetadataFormatClassName,
              extraMetadataFormatNames,
              extraMetadataFormatClassNames);
    }

    public GIFImageMetadata() {
        this(true,
              nativeMetadataFormatName,
              "com.sun.imageio.plugins.gif.GIFImageMetadataFormat",
              null, null);
    }

    public boolean isReadOnly() {
        return true;
    }

    public Node getAsTree(String formatName) {
        if (formatName.equals(nativeMetadataFormatName)) {
            return getNativeTree();
        } else if (formatName.equals
                   (IIOMetadataFormatImpl.standardMetadataFormatName)) {
            return getStandardTree();
        } else {
            throw new IllegalArgumentException("Not a recognized format!");
        }
    }

    private String toISO8859(byte[] data) {
        try {
            return new String(data, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    private Node getNativeTree() {
        IIOMetadataNode node; // scratch node
        IIOMetadataNode root =
            new IIOMetadataNode(nativeMetadataFormatName);

        // Image descriptor
        node = new IIOMetadataNode("ImageDescriptor");
        node.setAttribute("imageLeftPosition",
                          Integer.toString(imageLeftPosition));
        node.setAttribute("imageTopPosition",
                          Integer.toString(imageTopPosition));
        node.setAttribute("imageWidth", Integer.toString(imageWidth));
        node.setAttribute("imageHeight", Integer.toString(imageHeight));
        node.setAttribute("interlaceFlag",
                          interlaceFlag ? "TRUE" : "FALSE");
        root.appendChild(node);

        // Local color table
        if (localColorTable != null) {
            node = new IIOMetadataNode("LocalColorTable");
            int numEntries = localColorTable.length/3;
            node.setAttribute("sizeOfLocalColorTable",
                              Integer.toString(numEntries));
            node.setAttribute("sortFlag",
                              sortFlag ? "TRUE" : "FALSE");

            for (int i = 0; i < numEntries; i++) {
                IIOMetadataNode entry =
                    new IIOMetadataNode("ColorTableEntry");
                entry.setAttribute("index", Integer.toString(i));
                int r = localColorTable[3*i] & 0xff;
                int g = localColorTable[3*i + 1] & 0xff;
                int b = localColorTable[3*i + 2] & 0xff;
                entry.setAttribute("red", Integer.toString(r));
                entry.setAttribute("green", Integer.toString(g));
                entry.setAttribute("blue", Integer.toString(b));
                node.appendChild(entry);
            }
            root.appendChild(node);
        }

        // Graphic control extension
        node = new IIOMetadataNode("GraphicControlExtension");
        node.setAttribute("disposalMethod",
                          disposalMethodNames[disposalMethod]);
        node.setAttribute("userInputFlag",
                          userInputFlag ? "TRUE" : "FALSE");
        node.setAttribute("transparentColorFlag",
                          transparentColorFlag ? "TRUE" : "FALSE");
        node.setAttribute("delayTime",
                          Integer.toString(delayTime));
        node.setAttribute("transparentColorIndex",
                          Integer.toString(transparentColorIndex));
        root.appendChild(node);

        if (hasPlainTextExtension) {
            node = new IIOMetadataNode("PlainTextExtension");
            node.setAttribute("textGridLeft",
                              Integer.toString(textGridLeft));
            node.setAttribute("textGridTop",
                              Integer.toString(textGridTop));
            node.setAttribute("textGridWidth",
                              Integer.toString(textGridWidth));
            node.setAttribute("textGridHeight",
                              Integer.toString(textGridHeight));
            node.setAttribute("characterCellWidth",
                              Integer.toString(characterCellWidth));
            node.setAttribute("characterCellHeight",
                              Integer.toString(characterCellHeight));
            node.setAttribute("textForegroundColor",
                              Integer.toString(textForegroundColor));
            node.setAttribute("textBackgroundColor",
                              Integer.toString(textBackgroundColor));
            node.setAttribute("text", toISO8859(text));

            root.appendChild(node);
        }

        // Application extensions
        int numAppExtensions = applicationIDs == null ?
            0 : applicationIDs.size();
        if (numAppExtensions > 0) {
            node = new IIOMetadataNode("ApplicationExtensions");
            for (int i = 0; i < numAppExtensions; i++) {
                IIOMetadataNode appExtNode =
                    new IIOMetadataNode("ApplicationExtension");
                byte[] applicationID = (byte[])applicationIDs.get(i);
                appExtNode.setAttribute("applicationID",
                                        toISO8859(applicationID));
                byte[] authenticationCode = (byte[])authenticationCodes.get(i);
                appExtNode.setAttribute("authenticationCode",
                                        toISO8859(authenticationCode));
                byte[] appData = (byte[])applicationData.get(i);
                appExtNode.setUserObject((byte[])appData.clone());
                node.appendChild(appExtNode);
            }

            root.appendChild(node);
        }

        // Comment extensions
        int numComments = comments == null ? 0 : comments.size();
        if (numComments > 0) {
            node = new IIOMetadataNode("CommentExtensions");
            for (int i = 0; i < numComments; i++) {
                IIOMetadataNode commentNode =
                    new IIOMetadataNode("CommentExtension");
                byte[] comment = (byte[])comments.get(i);
                commentNode.setAttribute("value", toISO8859(comment));
                node.appendChild(commentNode);
            }

            root.appendChild(node);
        }

        return root;
    }

    public IIOMetadataNode getStandardChromaNode() {
        IIOMetadataNode chroma_node = new IIOMetadataNode("Chroma");
        IIOMetadataNode node = null; // scratch node

        node = new IIOMetadataNode("ColorSpaceType");
        node.setAttribute("name", "RGB");
        chroma_node.appendChild(node);

        node = new IIOMetadataNode("NumChannels");
        node.setAttribute("value", transparentColorFlag ? "4" : "3");
        chroma_node.appendChild(node);

        // Gamma not in format

        node = new IIOMetadataNode("BlackIsZero");
        node.setAttribute("value", "TRUE");
        chroma_node.appendChild(node);

        if (localColorTable != null) {
            node = new IIOMetadataNode("Palette");
            int numEntries = localColorTable.length/3;
            for (int i = 0; i < numEntries; i++) {
                IIOMetadataNode entry =
                    new IIOMetadataNode("PaletteEntry");
                entry.setAttribute("index", Integer.toString(i));
                entry.setAttribute("red",
                           Integer.toString(localColorTable[3*i] & 0xff));
                entry.setAttribute("green",
                           Integer.toString(localColorTable[3*i + 1] & 0xff));
                entry.setAttribute("blue",
                           Integer.toString(localColorTable[3*i + 2] & 0xff));
                node.appendChild(entry);
            }
            chroma_node.appendChild(node);
        }

        // BackgroundIndex not in image
        // BackgroundColor not in format

        return chroma_node;
    }

    public IIOMetadataNode getStandardCompressionNode() {
        IIOMetadataNode compression_node = new IIOMetadataNode("Compression");
        IIOMetadataNode node = null; // scratch node

        node = new IIOMetadataNode("CompressionTypeName");
        node.setAttribute("value", "lzw");
        compression_node.appendChild(node);

        node = new IIOMetadataNode("Lossless");
        node.setAttribute("value", "TRUE");
        compression_node.appendChild(node);

        node = new IIOMetadataNode("NumProgressiveScans");
        node.setAttribute("value", interlaceFlag ? "4" : "1");
        compression_node.appendChild(node);

        // BitRate not in format

        return compression_node;
    }

    public IIOMetadataNode getStandardDataNode() {
        IIOMetadataNode data_node = new IIOMetadataNode("Data");
        IIOMetadataNode node = null; // scratch node

        // PlanarConfiguration not in format

        node = new IIOMetadataNode("SampleFormat");
        node.setAttribute("value", "Index");
        data_node.appendChild(node);

        // BitsPerSample not in image
        // SignificantBitsPerSample not in format
        // SampleMSB not in format

        return data_node;
    }

    public IIOMetadataNode getStandardDimensionNode() {
        IIOMetadataNode dimension_node = new IIOMetadataNode("Dimension");
        IIOMetadataNode node = null; // scratch node

        // PixelAspectRatio not in image

        node = new IIOMetadataNode("ImageOrientation");
        node.setAttribute("value", "Normal");
        dimension_node.appendChild(node);

        // HorizontalPixelSize not in format
        // VerticalPixelSize not in format
        // HorizontalPhysicalPixelSpacing not in format
        // VerticalPhysicalPixelSpacing not in format
        // HorizontalPosition not in format
        // VerticalPosition not in format

        node = new IIOMetadataNode("HorizontalPixelOffset");
        node.setAttribute("value", Integer.toString(imageLeftPosition));
        dimension_node.appendChild(node);

        node = new IIOMetadataNode("VerticalPixelOffset");
        node.setAttribute("value", Integer.toString(imageTopPosition));
        dimension_node.appendChild(node);

        // HorizontalScreenSize not in image
        // VerticalScreenSize not in image

        return dimension_node;
    }

    // Document not in image

    public IIOMetadataNode getStandardTextNode() {
        if (comments == null) {
            return null;
        }
        Iterator commentIter = comments.iterator();
        if (!commentIter.hasNext()) {
            return null;
        }

        IIOMetadataNode text_node = new IIOMetadataNode("Text");
        IIOMetadataNode node = null; // scratch node

        while (commentIter.hasNext()) {
            byte[] comment = (byte[])commentIter.next();
            String s = null;
            try {
                s = new String(comment, "ISO-8859-1");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("Encoding ISO-8859-1 unknown!");
            }

            node = new IIOMetadataNode("TextEntry");
            node.setAttribute("value", s);
            node.setAttribute("encoding", "ISO-8859-1");
            node.setAttribute("compression", "none");
            text_node.appendChild(node);
        }

        return text_node;
    }

    public IIOMetadataNode getStandardTransparencyNode() {
        if (!transparentColorFlag) {
            return null;
        }

        IIOMetadataNode transparency_node =
            new IIOMetadataNode("Transparency");
        IIOMetadataNode node = null; // scratch node

        // Alpha not in format

        node = new IIOMetadataNode("TransparentIndex");
        node.setAttribute("value",
                          Integer.toString(transparentColorIndex));
        transparency_node.appendChild(node);

        // TransparentColor not in format
        // TileTransparencies not in format
        // TileOpacities not in format

        return transparency_node;
    }

    public void setFromTree(String formatName, Node root)
        throws IIOInvalidTreeException
    {
        throw new IllegalStateException("Metadata is read-only!");
    }

    protected void mergeNativeTree(Node root) throws IIOInvalidTreeException
    {
        throw new IllegalStateException("Metadata is read-only!");
    }

    protected void mergeStandardTree(Node root) throws IIOInvalidTreeException
    {
        throw new IllegalStateException("Metadata is read-only!");
    }

    public void reset() {
        throw new IllegalStateException("Metadata is read-only!");
    }
}
