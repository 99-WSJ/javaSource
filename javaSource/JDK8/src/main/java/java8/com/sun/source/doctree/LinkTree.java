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

import java.util.List;

/**
 * A tree node for an @link or &#064;linkplain inline tag.
 *
 * <p>
 * {&#064;link reference label} <br>
 * {&#064;linkplain reference label }
 *
 * @since 1.8
 */
@jdk.Exported
public interface LinkTree extends InlineTagTree {
    ReferenceTree getReference();
    List<? extends DocTree> getLabel();
}
