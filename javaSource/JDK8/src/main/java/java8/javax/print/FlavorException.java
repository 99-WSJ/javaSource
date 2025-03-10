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

package java8.javax.print;

import javax.print.DocFlavor;
import javax.print.PrintException;

/**
 * Interface FlavorException is a mixin interface which a subclass of {@link
 * PrintException PrintException} can implement to report an error condition
 * involving a doc flavor or flavors (class {@link DocFlavor
 * DocFlavor}). The Print Service API does not define any print exception
 * classes that implement interface FlavorException, that being left to the
 * Print Service implementor's discretion.
 *
 */
public interface FlavorException {

    /**
     * Returns the unsupported flavors.
     * @return the unsupported doc flavors.
     */
    public DocFlavor[] getUnsupportedFlavors();

}
