/*
 * Copyright (c) 2011, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java8.sun.source.doctree;

import java8.com.sun.source.doctree.IdentifierTree;

import java.util.List;

/**
 * A tree node for an @param block tag.
 *
 * <p>
 * &#064;param parameter-name description
 *
 * @since 1.8
 */
@jdk.Exported
public interface ParamTree extends BlockTagTree {
    boolean isTypeParameter();
    IdentifierTree getName();
    List<? extends DocTree> getDescription();
}
