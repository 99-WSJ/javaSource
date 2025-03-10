/*
 * Copyright (c) 2000, Oracle and/or its affiliates. All rights reserved.
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

package java8.javax.imageio.spi;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A node in a directed graph.  In addition to an arbitrary
 * <code>Object</code> containing user data associated with the node,
 * each node maintains a <code>Set</code>s of nodes which are pointed
 * to by the current node (available from <code>getOutNodes</code>).
 * The in-degree of the node (that is, number of nodes that point to
 * the current node) may be queried.
 *
 */
class DigraphNode implements Cloneable, Serializable {

    /** The data associated with this node. */
    protected Object data;

    /**
     * A <code>Set</code> of neighboring nodes pointed to by this
     * node.
     */
    protected Set outNodes = new HashSet();

    /** The in-degree of the node. */
    protected int inDegree = 0;

    /**
     * A <code>Set</code> of neighboring nodes that point to this
     * node.
     */
    private Set inNodes = new HashSet();

    public DigraphNode(Object data) {
        this.data = data;
    }

    /** Returns the <code>Object</code> referenced by this node. */
    public Object getData() {
        return data;
    }

    /**
     * Returns an <code>Iterator</code> containing the nodes pointed
     * to by this node.
     */
    public Iterator getOutNodes() {
        return outNodes.iterator();
    }

    /**
     * Adds a directed edge to the graph.  The outNodes list of this
     * node is updated and the in-degree of the other node is incremented.
     *
     * @param node a <code>DigraphNode</code>.
     *
     * @return <code>true</code> if the node was not previously the
     * target of an edge.
     */
    public boolean addEdge(javax.imageio.spi.DigraphNode node) {
        if (outNodes.contains(node)) {
            return false;
        }

        outNodes.add(node);
        node.inNodes.add(this);
        node.incrementInDegree();
        return true;
    }

    /**
     * Returns <code>true</code> if an edge exists between this node
     * and the given node.
     *
     * @param node a <code>DigraphNode</code>.
     *
     * @return <code>true</code> if the node is the target of an edge.
     */
    public boolean hasEdge(javax.imageio.spi.DigraphNode node) {
        return outNodes.contains(node);
    }

    /**
     * Removes a directed edge from the graph.  The outNodes list of this
     * node is updated and the in-degree of the other node is decremented.
     *
     * @return <code>true</code> if the node was previously the target
     * of an edge.
     */
    public boolean removeEdge(javax.imageio.spi.DigraphNode node) {
        if (!outNodes.contains(node)) {
            return false;
        }

        outNodes.remove(node);
        node.inNodes.remove(this);
        node.decrementInDegree();
        return true;
    }

    /**
     * Removes this node from the graph, updating neighboring nodes
     * appropriately.
     */
    public void dispose() {
        Object[] inNodesArray = inNodes.toArray();
        for(int i=0; i<inNodesArray.length; i++) {
            javax.imageio.spi.DigraphNode node = (javax.imageio.spi.DigraphNode) inNodesArray[i];
            node.removeEdge(this);
        }

        Object[] outNodesArray = outNodes.toArray();
        for(int i=0; i<outNodesArray.length; i++) {
            javax.imageio.spi.DigraphNode node = (javax.imageio.spi.DigraphNode) outNodesArray[i];
            removeEdge(node);
        }
    }

    /** Returns the in-degree of this node. */
    public int getInDegree() {
        return inDegree;
    }

    /** Increments the in-degree of this node. */
    private void incrementInDegree() {
        ++inDegree;
    }

    /** Decrements the in-degree of this node. */
    private void decrementInDegree() {
        --inDegree;
    }
}
