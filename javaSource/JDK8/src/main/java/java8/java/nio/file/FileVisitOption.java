/*
 * Copyright (c) 2007, 2010, Oracle and/or its affiliates. All rights reserved.
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

package java8.java.nio.file;

import java.nio.file.Files;

/**
 * Defines the file tree traversal options.
 *
 * @since 1.7
 *
 * @see Files#walkFileTree
 */

public enum FileVisitOption {
    /**
     * Follow symbolic links.
     */
    FOLLOW_LINKS;
}
