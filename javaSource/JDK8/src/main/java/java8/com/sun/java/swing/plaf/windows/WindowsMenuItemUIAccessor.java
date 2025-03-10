/*
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
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

package java8.sun.java.swing.plaf.windows;

import com.sun.java.swing.plaf.windows.TMSchema.Part;
import com.sun.java.swing.plaf.windows.TMSchema.State;

import javax.swing.*;

/**
 * Accessor interface for WindowsMenuItemUI to allow for "multiple implementation
 * inheritance".
 *
 * @author Igor Kushnirskiy
 */
interface WindowsMenuItemUIAccessor {
    JMenuItem getMenuItem();
    State getState(JMenuItem menuItem);
    Part getPart(JMenuItem menuItem);
}
