/*
 * Copyright (c) 2000, 2001, Oracle and/or its affiliates. All rights reserved.
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
package java8.java.util.prefs;

import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;
import java.util.prefs.WindowsPreferences;

/**
 * Implementation of  <tt>PreferencesFactory</tt> to return
 * WindowsPreferences objects.
 *
 * @author  Konstantin Kladko
 * @see Preferences
 * @see WindowsPreferences
 * @since 1.4
 */
class WindowsPreferencesFactory implements PreferencesFactory  {

    /**
     * Returns WindowsPreferences.userRoot
     */
    public Preferences userRoot() {
        return WindowsPreferences.userRoot;
    }

    /**
     * Returns WindowsPreferences.systemRoot
     */
    public Preferences systemRoot() {
        return WindowsPreferences.systemRoot;
    }
}
