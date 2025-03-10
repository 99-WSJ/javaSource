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

import javax.imageio.spi.DigraphNode;
import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * A set of <code>Object</code>s with pairwise orderings between them.
 * The <code>iterator</code> method provides the elements in
 * topologically sorted order.  Elements participating in a cycle
 * are not returned.
 *
 * Unlike the <code>SortedSet</code> and <code>SortedMap</code>
 * interfaces, which require their elements to implement the
 * <code>Comparable</code> interface, this class receives ordering
 * information via its <code>setOrdering</code> and
 * <code>unsetPreference</code> methods.  This difference is due to
 * the fact that the relevant ordering between elements is unlikely to
 * be inherent in the elements themselves; rather, it is set
 * dynamically accoring to application policy.  For example, in a
 * service provider registry situation, an application might allow the
 * user to set a preference order for service provider objects
 * supplied by a trusted vendor over those supplied by another.
 *
 */
class PartiallyOrderedSet extends AbstractSet {

    // The topological sort (roughly) follows the algorithm described in
    // Horowitz and Sahni, _Fundamentals of Data Structures_ (1976),
    // p. 315.

    // Maps Objects to DigraphNodes that contain them
    private Map poNodes = new HashMap();

    // The set of Objects
    private Set nodes = poNodes.keySet();

    /**
     * Constructs a <code>PartiallyOrderedSet</code>.
     */
    public PartiallyOrderedSet() {}

    public int size() {
        return nodes.size();
    }

    public boolean contains(Object o) {
        return nodes.contains(o);
    }

    /**
     * Returns an iterator over the elements contained in this
     * collection, with an ordering that respects the orderings set
     * by the <code>setOrdering</code> method.
     */
    public Iterator iterator() {
        return new javax.imageio.spi.PartialOrderIterator(poNodes.values().iterator());
    }

    /**
     * Adds an <code>Object</code> to this
     * <code>PartiallyOrderedSet</code>.
     */
    public boolean add(Object o) {
        if (nodes.contains(o)) {
            return false;
        }

        javax.imageio.spi.DigraphNode node = new javax.imageio.spi.DigraphNode(o);
        poNodes.put(o, node);
        return true;
    }

    /**
     * Removes an <code>Object</code> from this
     * <code>PartiallyOrderedSet</code>.
     */
    public boolean remove(Object o) {
        javax.imageio.spi.DigraphNode node = (javax.imageio.spi.DigraphNode)poNodes.get(o);
        if (node == null) {
            return false;
        }

        poNodes.remove(o);
        node.dispose();
        return true;
    }

    public void clear() {
        poNodes.clear();
    }

    /**
     * Sets an ordering between two nodes.  When an iterator is
     * requested, the first node will appear earlier in the
     * sequence than the second node.  If a prior ordering existed
     * between the nodes in the opposite order, it is removed.
     *
     * @return <code>true</code> if no prior ordering existed
     * between the nodes, <code>false</code>otherwise.
     */
    public boolean setOrdering(Object first, Object second) {
        javax.imageio.spi.DigraphNode firstPONode =
            (javax.imageio.spi.DigraphNode)poNodes.get(first);
        javax.imageio.spi.DigraphNode secondPONode =
            (javax.imageio.spi.DigraphNode)poNodes.get(second);

        secondPONode.removeEdge(firstPONode);
        return firstPONode.addEdge(secondPONode);
    }

    /**
     * Removes any ordering between two nodes.
     *
     * @return true if a prior prefence existed between the nodes.
     */
    public boolean unsetOrdering(Object first, Object second) {
        javax.imageio.spi.DigraphNode firstPONode =
            (javax.imageio.spi.DigraphNode)poNodes.get(first);
        javax.imageio.spi.DigraphNode secondPONode =
            (javax.imageio.spi.DigraphNode)poNodes.get(second);

        return firstPONode.removeEdge(secondPONode) ||
            secondPONode.removeEdge(firstPONode);
    }

    /**
     * Returns <code>true</code> if an ordering exists between two
     * nodes.
     */
    public boolean hasOrdering(Object preferred, Object other) {
        javax.imageio.spi.DigraphNode preferredPONode =
            (javax.imageio.spi.DigraphNode)poNodes.get(preferred);
        javax.imageio.spi.DigraphNode otherPONode =
            (javax.imageio.spi.DigraphNode)poNodes.get(other);

        return preferredPONode.hasEdge(otherPONode);
    }
}

class PartialOrderIterator implements Iterator {

    LinkedList zeroList = new LinkedList();
    Map inDegrees = new HashMap(); // DigraphNode -> Integer

    public PartialOrderIterator(Iterator iter) {
        // Initialize scratch in-degree values, zero list
        while (iter.hasNext()) {
            javax.imageio.spi.DigraphNode node = (javax.imageio.spi.DigraphNode)iter.next();
            int inDegree = node.getInDegree();
            inDegrees.put(node, new Integer(inDegree));

            // Add nodes with zero in-degree to the zero list
            if (inDegree == 0) {
                zeroList.add(node);
            }
        }
    }

    public boolean hasNext() {
        return !zeroList.isEmpty();
    }

    public Object next() {
        javax.imageio.spi.DigraphNode first = (javax.imageio.spi.DigraphNode)zeroList.removeFirst();

        // For each out node of the output node, decrement its in-degree
        Iterator outNodes = first.getOutNodes();
        while (outNodes.hasNext()) {
            javax.imageio.spi.DigraphNode node = (javax.imageio.spi.DigraphNode)outNodes.next();
            int inDegree = ((Integer)inDegrees.get(node)).intValue() - 1;
            inDegrees.put(node, new Integer(inDegree));

            // If the in-degree has fallen to 0, place the node on the list
            if (inDegree == 0) {
                zeroList.add(node);
            }
        }

        return first.getData();
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
