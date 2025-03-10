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

package java8.com.sun.imageio.plugins.jpeg;

import com.sun.imageio.plugins.jpeg.JPEG;
import java8.sun.imageio.plugins.jpeg.JPEGBuffer;
import java8.sun.imageio.plugins.jpeg.MarkerSegment;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.plugins.jpeg.JPEGHuffmanTable;
import javax.imageio.stream.ImageOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A DHT (Define Huffman Table) marker segment.
 */
class DHTMarkerSegment extends MarkerSegment {
    List tables = new ArrayList();

    DHTMarkerSegment(boolean needFour) {
        super(JPEG.DHT);
        tables.add(new Htable(JPEGHuffmanTable.StdDCLuminance, true, 0));
        if (needFour) {
            tables.add(new Htable(JPEGHuffmanTable.StdDCChrominance, true, 1));
        }
        tables.add(new Htable(JPEGHuffmanTable.StdACLuminance, false, 0));
        if (needFour) {
            tables.add(new Htable(JPEGHuffmanTable.StdACChrominance, false, 1));
        }
    }

    DHTMarkerSegment(JPEGBuffer buffer) throws IOException {
        super(buffer);
        int count = length;
        while (count > 0) {
            Htable newGuy = new Htable(buffer);
            tables.add(newGuy);
            count -= 1 + 16 + newGuy.values.length;
        }
        buffer.bufAvail -= length;
    }

    DHTMarkerSegment(JPEGHuffmanTable[] dcTables,
                     JPEGHuffmanTable[] acTables) {
        super(JPEG.DHT);
        for (int i = 0; i < dcTables.length; i++) {
            tables.add(new Htable(dcTables[i], true, i));
        }
        for (int i = 0; i < acTables.length; i++) {
            tables.add(new Htable(acTables[i], false, i));
        }
    }

    DHTMarkerSegment(Node node) throws IIOInvalidTreeException {
        super(JPEG.DHT);
        NodeList children = node.getChildNodes();
        int size = children.getLength();
        if ((size < 1) || (size > 4)) {
            throw new IIOInvalidTreeException("Invalid DHT node", node);
        }
        for (int i = 0; i < size; i++) {
            tables.add(new Htable(children.item(i)));
        }
    }

    protected Object clone() {
        com.sun.imageio.plugins.jpeg.DHTMarkerSegment newGuy = (com.sun.imageio.plugins.jpeg.DHTMarkerSegment) super.clone();
        newGuy.tables = new ArrayList(tables.size());
        Iterator iter = tables.iterator();
        while (iter.hasNext()) {
            Htable table = (Htable) iter.next();
            newGuy.tables.add(table.clone());
        }
        return newGuy;
    }

    IIOMetadataNode getNativeNode() {
        IIOMetadataNode node = new IIOMetadataNode("dht");
        for (int i= 0; i<tables.size(); i++) {
            Htable table = (Htable) tables.get(i);
            node.appendChild(table.getNativeNode());
        }
        return node;
    }

    /**
     * Writes the data for this segment to the stream in
     * valid JPEG format.
     */
    void write(ImageOutputStream ios) throws IOException {
        // We don't write DHT segments; the IJG library does.
    }

    void print() {
        printTag("DHT");
        System.out.println("Num tables: "
                           + Integer.toString(tables.size()));
        for (int i= 0; i<tables.size(); i++) {
            Htable table = (Htable) tables.get(i);
            table.print();
        }
        System.out.println();

    }

    Htable getHtableFromNode(Node node) throws IIOInvalidTreeException {
        return new Htable(node);
    }

    void addHtable(JPEGHuffmanTable table, boolean isDC, int id) {
        tables.add(new Htable(table, isDC, id));
    }

    /**
     * A Huffman table within a DHT marker segment.
     */
    class Htable implements Cloneable {
        int tableClass;  // 0 == DC, 1 == AC
        int tableID; // 0 - 4
        private static final int NUM_LENGTHS = 16;
        // # of codes of each length
        short [] numCodes = new short[NUM_LENGTHS];
        short [] values;

        Htable(JPEGBuffer buffer) {
            tableClass = buffer.buf[buffer.bufPtr] >>> 4;
            tableID = buffer.buf[buffer.bufPtr++] & 0xf;
            for (int i = 0; i < NUM_LENGTHS; i++) {
                numCodes[i] = (short) (buffer.buf[buffer.bufPtr++] & 0xff);
            }

            int numValues = 0;
            for (int i = 0; i < NUM_LENGTHS; i++) {
                numValues += numCodes[i];
            }
            values = new short[numValues];
            for (int i = 0; i < numValues; i++) {
                values[i] = (short) (buffer.buf[buffer.bufPtr++] & 0xff);
            }
        }

        Htable(JPEGHuffmanTable table, boolean isDC, int id) {
            tableClass = isDC ? 0 : 1;
            tableID = id;
            numCodes = table.getLengths();
            values = table.getValues();
        }

        Htable(Node node) throws IIOInvalidTreeException {
            if (node.getNodeName().equals("dhtable")) {
                NamedNodeMap attrs = node.getAttributes();
                int count = attrs.getLength();
                if (count != 2) {
                    throw new IIOInvalidTreeException
                        ("dhtable node must have 2 attributes", node);
                }
                tableClass = getAttributeValue(node, attrs, "class", 0, 1, true);
                tableID = getAttributeValue(node, attrs, "htableId", 0, 3, true);
                if (node instanceof IIOMetadataNode) {
                    IIOMetadataNode ourNode = (IIOMetadataNode) node;
                    JPEGHuffmanTable table =
                        (JPEGHuffmanTable) ourNode.getUserObject();
                    if (table == null) {
                        throw new IIOInvalidTreeException
                            ("dhtable node must have user object", node);
                    }
                    numCodes = table.getLengths();
                    values = table.getValues();
                } else {
                    throw new IIOInvalidTreeException
                        ("dhtable node must have user object", node);
                }
            } else {
                throw new IIOInvalidTreeException
                    ("Invalid node, expected dqtable", node);
            }

        }

        protected Object clone() {
            Htable newGuy = null;
            try {
                newGuy = (Htable) super.clone();
            } catch (CloneNotSupportedException e) {} // won't happen
            if (numCodes != null) {
                newGuy.numCodes = (short []) numCodes.clone();
            }
            if (values != null) {
                newGuy.values = (short []) values.clone();
            }
            return newGuy;
        }

        IIOMetadataNode getNativeNode() {
            IIOMetadataNode node = new IIOMetadataNode("dhtable");
            node.setAttribute("class", Integer.toString(tableClass));
            node.setAttribute("htableId", Integer.toString(tableID));

            node.setUserObject(new JPEGHuffmanTable(numCodes, values));

            return node;
        }


        void print() {
            System.out.println("Huffman Table");
            System.out.println("table class: "
                               + ((tableClass == 0) ? "DC":"AC"));
            System.out.println("table id: " + Integer.toString(tableID));

            (new JPEGHuffmanTable(numCodes, values)).toString();
            /*
              System.out.print("Lengths:");
              for (int i=0; i<16; i++) {
              System.out.print(" " + Integer.toString(numCodes[i]));
              }
              int count = 0;
              if (values.length > 16) {
              System.out.println("\nFirst 16 Values:");
              count = 16;
              } else {
              System.out.println("\nValues:");
              count = values.length;
              }
              for (int i=0; i<count; i++) {
              System.out.println(Integer.toString(values[i]&0xff));
              }
            */
        }
    }

}
