/*
 * Copyright (c) 2007-2013, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package java8.sun.org.apache.xml.internal.security.utils;

import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Christian Geuer-Pollmann
 */
public class HelperNodeList implements NodeList {

    /** Field nodes */
    List<Node> nodes = new ArrayList<Node>();
    boolean allNodesMustHaveSameParent = false;

    /**
     *
     */
    public HelperNodeList() {
        this(false);
    }


    /**
     * @param allNodesMustHaveSameParent
     */
    public HelperNodeList(boolean allNodesMustHaveSameParent) {
        this.allNodesMustHaveSameParent = allNodesMustHaveSameParent;
    }

    /**
     * Method item
     *
     * @param index
     * @return node with index i
     */
    public Node item(int index) {
        return nodes.get(index);
    }

    /**
     * Method getLength
     *
     *  @return length of the list
     */
    public int getLength() {
        return nodes.size();
    }

    /**
     * Method appendChild
     *
     * @param node
     * @throws IllegalArgumentException
     */
    public void appendChild(Node node) throws IllegalArgumentException {
        if (this.allNodesMustHaveSameParent && this.getLength() > 0
            && this.item(0).getParentNode() != node.getParentNode()) {
            throw new IllegalArgumentException("Nodes have not the same Parent");
        }
        nodes.add(node);
    }

    /**
     * @return the document that contains this nodelist
     */
    public Document getOwnerDocument() {
        if (this.getLength() == 0) {
            return null;
        }
        return XMLUtils.getOwnerDocument(this.item(0));
    }
}
