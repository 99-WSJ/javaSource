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

package java8.com.sun.source.doctree;

import java8.sun.source.doctree.BlockTagTree;
import java8.sun.source.doctree.DocTree;

import java.util.List;

/**
 * A tree node for an @author block tag.
 *
 * <p>
 * &#064;author name-text.
 *
 * @since 1.8
 */
@jdk.Exported
public interface AuthorTree extends BlockTagTree {
    List<? extends DocTree> getName();
}
