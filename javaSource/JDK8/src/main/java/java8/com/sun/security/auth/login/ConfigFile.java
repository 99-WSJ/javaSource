/*
 * Copyright (c) 2000, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java8.com.sun.security.auth.login;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import java.net.URI;

// NOTE: As of JDK 8, this class instantiates
// sun.security.provider.ConfigFile.Spi and forwards all methods to that
// implementation. All implementation fixes and enhancements should be made to
// sun.security.provider.ConfigFile.Spi and not this class.
// See JDK-8005117 for more information.

/**
 * This class represents a default implementation for
 * {@code javax.security.auth.login.Configuration}.
 *
 * <p> This object stores the runtime login configuration representation,
 * and is the amalgamation of multiple static login
 * configurations that resides in files.
 * The algorithm for locating the login configuration file(s) and reading their
 * information into this {@code Configuration} object is:
 *
 * <ol>
 * <li>
 *   Loop through the security properties,
 *   <i>login.config.url.1</i>, <i>login.config.url.2</i>, ...,
 *   <i>login.config.url.X</i>.
 *   Each property value specifies a {@code URL} pointing to a
 *   login configuration file to be loaded.  Read in and load
 *   each configuration.
 *
 * <li>
 *   The {@code java.lang.System} property
 *   <i>java.security.auth.login.config</i>
 *   may also be set to a {@code URL} pointing to another
 *   login configuration file
 *   (which is the case when a user uses the -D switch at runtime).
 *   If this property is defined, and its use is allowed by the
 *   security property file (the Security property,
 *   <i>policy.allowSystemProperty</i> is set to <i>true</i>),
 *   also load that login configuration.
 *
 * <li>
 *   If the <i>java.security.auth.login.config</i> property is defined using
 *   "==" (rather than "="), then ignore all other specified
 *   login configurations and only load this configuration.
 *
 * <li>
 *   If no system or security properties were set, try to read from the file,
 *   ${user.home}/.java.login.config, where ${user.home} is the value
 *   represented by the "user.home" System property.
 * </ol>
 *
 * <p> The configuration syntax supported by this implementation
 * is exactly that syntax specified in the
 * {@code javax.security.auth.login.Configuration} class.
 *
 * @see javax.security.auth.login.LoginContext
 * @see java.security.Security security properties
 */
@jdk.Exported
public class ConfigFile extends Configuration {

    private final sun.security.provider.ConfigFile.Spi spi;

    /**
     * Create a new {@code Configuration} object.
     *
     * @throws SecurityException if the {@code Configuration} can not be
     *                           initialized
     */
    public ConfigFile() {
        spi = new sun.security.provider.ConfigFile.Spi();
    }

    /**
     * Create a new {@code Configuration} object from the specified {@code URI}.
     *
     * @param uri the {@code URI}
     * @throws SecurityException if the {@code Configuration} can not be
     *                           initialized
     * @throws NullPointerException if {@code uri} is null
     */
    public ConfigFile(URI uri) {
        spi = new sun.security.provider.ConfigFile.Spi(uri);
    }

    /**
     * Retrieve an entry from the {@code Configuration} using an application
     * name as an index.
     *
     * @param applicationName the name used to index the {@code Configuration}
     * @return an array of {@code AppConfigurationEntry} which correspond to
     *         the stacked configuration of {@code LoginModule}s for this
     *         application, or null if this application has no configured
     *         {@code LoginModule}s.
     */
    @Override
    public AppConfigurationEntry[] getAppConfigurationEntry
        (String applicationName) {

        return spi.engineGetAppConfigurationEntry(applicationName);
    }

    /**
     * Refresh and reload the {@code Configuration} by re-reading all of the
     * login configurations.
     *
     * @throws SecurityException if the caller does not have permission
     *                           to refresh the {@code Configuration}
     */
    @Override
    public void refresh() {
        spi.engineRefresh();
    }
}
