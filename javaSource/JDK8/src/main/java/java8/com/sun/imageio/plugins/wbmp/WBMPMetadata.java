/*
 * Copyright (c) 2003, 2004, Oracle and/or its affiliates. All rights reserved.
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

import com.sun.imageio.plugins.common.I18N;
import com.sun.imageio.plugins.common.ImageUtil;
import org.w3c.dom.Node;

import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataFormatImpl;
import javax.imageio.metadata.IIOMetadataNode;

public class WBMPMetadata extends IIOMetadata {

    static final String nativeMetadataFormatName =
        "javax_imageio_wbmp_1.0";

    public int wbmpType;

    public int width;
    public int height;

    public WBMPMetadata() {
        super(true,
              nativeMetadataFormatName,
              "com.sun.imageio.plugins.wbmp.WBMPMetadataFormat",
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
            throw new IllegalArgumentException(I18N.getString("WBMPMetadata0"));
        }
    }

    private Node getNativeTree() {
        IIOMetadataNode root =
            new IIOMetadataNode(nativeMetadataFormatName);

        addChildNode(root, "WBMPType", new Integer(wbmpType));
        addChildNode(root, "Width", new Integer(width));
        addChildNode(root, "Height", new Integer(height));

        return root;
    }

    public void setFromTree(String formatName, Node root) {
        throw new IllegalStateException(I18N.getString("WBMPMetadata1"));
    }

    public void mergeTree(String formatName, Node root) {
        throw new IllegalStateException(I18N.getString("WBMPMetadata1"));
    }

    public void reset() {
        throw new IllegalStateException(I18N.getString("WBMPMetadata1"));
    }

    private IIOMetadataNode addChildNode(IIOMetadataNode root,
                                         String name,
                                         Object object) {
        IIOMetadataNode child = new IIOMetadataNode(name);
        if (object != null) {
            child.setUserObject(object);
            child.setNodeValue(ImageUtil.convertObjectToString(object));
        }
        root.appendChild(child);
        return child;
    }


    protected IIOMetadataNode getStandardChromaNode() {

        IIOMetadataNode node = new IIOMetadataNode("Chroma");
        IIOMetadataNode subNode = new IIOMetadataNode("BlackIsZero");
        subNode.setAttribute("value", "TRUE");

        node.appendChild(subNode);
        return node;
    }


    protected IIOMetadataNode getStandardDimensionNode() {
        IIOMetadataNode dimension_node = new IIOMetadataNode("Dimension");
        IIOMetadataNode node = null; // scratch node

        // PixelAspectRatio not in image

        node = new IIOMetadataNode("ImageOrientation");
        node.setAttribute("value", "Normal");
        dimension_node.appendChild(node);

        return dimension_node;
    }

}
