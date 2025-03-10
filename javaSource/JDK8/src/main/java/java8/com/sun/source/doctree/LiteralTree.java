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

import java8.sun.source.doctree.InlineTagTree;

/**
 *
 * A tree node for an @literal or @code inline tag.
 *
 * <p>
 * {&#064;literal text}
 *
 * @since 1.8
 */
@jdk.Exported
public interface LiteralTree extends InlineTagTree {
    TextTree getBody();
}
