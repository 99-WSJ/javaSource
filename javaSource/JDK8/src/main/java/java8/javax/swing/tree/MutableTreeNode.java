/*
 * Copyright (c) 1997, 1999, Oracle and/or its affiliates. All rights reserved.
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

package java8.javax.swing.tree;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 * Defines the requirements for a tree node object that can change --
 * by adding or removing child nodes, or by changing the contents
 * of a user object stored in the node.
 *
 * @see DefaultMutableTreeNode
 * @see javax.swing.JTree
 *
 * @author Rob Davis
 * @author Scott Violet
 */

public interface MutableTreeNode extends TreeNode
{
    /**
     * Adds <code>child</code> to the receiver at <code>index</code>.
     * <code>child</code> will be messaged with <code>setParent</code>.
     */
    void insert(javax.swing.tree.MutableTreeNode child, int index);

    /**
     * Removes the child at <code>index</code> from the receiver.
     */
    void remove(int index);

    /**
     * Removes <code>node</code> from the receiver. <code>setParent</code>
     * will be messaged on <code>node</code>.
     */
    void remove(javax.swing.tree.MutableTreeNode node);

    /**
     * Resets the user object of the receiver to <code>object</code>.
     */
    void setUserObject(Object object);

    /**
     * Removes the receiver from its parent.
     */
    void removeFromParent();

    /**
     * Sets the parent of the receiver to <code>newParent</code>.
     */
    void setParent(javax.swing.tree.MutableTreeNode newParent);
}
