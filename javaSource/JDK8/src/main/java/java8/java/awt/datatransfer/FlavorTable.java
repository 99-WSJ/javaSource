/*
 * Copyright (c) 2000, 2004, Oracle and/or its affiliates. All rights reserved.
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

package java8.java.awt.datatransfer;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorMap;
import java.util.List;


/**
 * A FlavorMap which relaxes the traditional 1-to-1 restriction of a Map. A
 * flavor is permitted to map to any number of natives, and likewise a native
 * is permitted to map to any number of flavors. FlavorTables need not be
 * symmetric, but typically are.
 *
 * @author David Mendenhall
 *
 * @since 1.4
 */
public interface FlavorTable extends FlavorMap {

    /**
     * Returns a <code>List</code> of <code>String</code> natives to which the
     * specified <code>DataFlavor</code> corresponds. The <code>List</code>
     * will be sorted from best native to worst. That is, the first native will
     * best reflect data in the specified flavor to the underlying native
     * platform. The returned <code>List</code> is a modifiable copy of this
     * <code>FlavorTable</code>'s internal data. Client code is free to modify
     * the <code>List</code> without affecting this object.
     *
     * @param flav the <code>DataFlavor</code> whose corresponding natives
     *        should be returned. If <code>null</code> is specified, all
     *        natives currently known to this <code>FlavorTable</code> are
     *        returned in a non-deterministic order.
     * @return a <code>java.util.List</code> of <code>java.lang.String</code>
     *         objects which are platform-specific representations of platform-
     *         specific data formats
     */
    List<String> getNativesForFlavor(DataFlavor flav);

    /**
     * Returns a <code>List</code> of <code>DataFlavor</code>s to which the
     * specified <code>String</code> corresponds. The <code>List</code> will be
     * sorted from best <code>DataFlavor</code> to worst. That is, the first
     * <code>DataFlavor</code> will best reflect data in the specified
     * native to a Java application. The returned <code>List</code> is a
     * modifiable copy of this <code>FlavorTable</code>'s internal data.
     * Client code is free to modify the <code>List</code> without affecting
     * this object.
     *
     * @param nat the native whose corresponding <code>DataFlavor</code>s
     *        should be returned. If <code>null</code> is specified, all
     *        <code>DataFlavor</code>s currently known to this
     *        <code>FlavorTable</code> are returned in a non-deterministic
     *        order.
     * @return a <code>java.util.List</code> of <code>DataFlavor</code>
     *         objects into which platform-specific data in the specified,
     *         platform-specific native can be translated
     */
    List<DataFlavor> getFlavorsForNative(String nat);
}
