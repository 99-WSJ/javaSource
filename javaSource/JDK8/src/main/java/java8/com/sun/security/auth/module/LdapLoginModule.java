/*
 * Copyright (c) 2005, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java8.sun.security.auth.module;

import com.sun.security.auth.LdapPrincipal;
import com.sun.security.auth.UserPrincipal;

import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.security.auth.AuthPermission;
import javax.security.auth.Subject;
import javax.security.auth.callback.*;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import java.net.SocketPermission;
import java.security.AccessController;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * This {@link LoginModule} performs LDAP-based authentication.
 * A username and password is verified against the corresponding user
 * credentials stored in an LDAP directory.
 * This module requires the supplied {@link CallbackHandler} to support a
 * {@link NameCallback} and a {@link PasswordCallback}.
 * If authentication is successful then a new {@link LdapPrincipal} is created
 * using the user's distinguished name and a new {@link UserPrincipal} is
 * created using the user's username and both are associated
 * with the current {@link Subject}.
 *
 * <p> This module operates in one of three modes: <i>search-first</i>,
 * <i>authentication-first</i> or <i>authentication-only</i>.
 * A mode is selected by specifying a particular set of options.
 *
 * <p> In search-first mode, the LDAP directory is searched to determine the
 * user's distinguished name and then authentication is attempted.
 * An (anonymous) search is performed using the supplied username in
 * conjunction with a specified search filter.
 * If successful then authentication is attempted using the user's
 * distinguished name and the supplied password.
 * To enable this mode, set the <code>userFilter</code> option and omit the
 * <code>authIdentity</code> option.
 * Use search-first mode when the user's distinguished name is not
 * known in advance.
 *
 * <p> In authentication-first mode, authentication is attempted using the
 * supplied username and password and then the LDAP directory is searched.
 * If authentication is successful then a search is performed using the
 * supplied username in conjunction with a specified search filter.
 * To enable this mode, set the <code>authIdentity</code> and the
 * <code>userFilter</code> options.
 * Use authentication-first mode when accessing an LDAP directory
 * that has been configured to disallow anonymous searches.
 *
 * <p> In authentication-only mode, authentication is attempted using the
 * supplied username and password. The LDAP directory is not searched because
 * the user's distinguished name is already known.
 * To enable this mode, set the <code>authIdentity</code> option to a valid
 * distinguished name and omit the <code>userFilter</code> option.
 * Use authentication-only mode when the user's distinguished name is
 * known in advance.
 *
 * <p> The following option is mandatory and must be specified in this
 * module's login {@link Configuration}:
 * <dl><dt></dt><dd>
 * <dl>
 * <dt> <code>userProvider=<b>ldap_urls</b></code>
 * </dt>
 * <dd> This option identifies the LDAP directory that stores user entries.
 *      <b>ldap_urls</b> is a list of space-separated LDAP URLs
 *      (<a href="http://www.ietf.org/rfc/rfc2255.txt">RFC 2255</a>)
 *      that identifies the LDAP server to use and the position in
 *      its directory tree where user entries are located.
 *      When several LDAP URLs are specified then each is attempted,
 *      in turn, until the first successful connection is established.
 *      Spaces in the distinguished name component of the URL must be escaped
 *      using the standard mechanism of percent character ('<code>%</code>')
 *      followed by two hexadecimal digits (see {@link java.net.URI}).
 *      Query components must also be omitted from the URL.
 *
 *      <p>
 *      Automatic discovery of the LDAP server via DNS
 *      (<a href="http://www.ietf.org/rfc/rfc2782.txt">RFC 2782</a>)
 *      is supported (once DNS has been configured to support such a service).
 *      It is enabled by omitting the hostname and port number components from
 *      the LDAP URL. </dd>
 * </dl></dl>
 *
 * <p> This module also recognizes the following optional {@link Configuration}
 *     options:
 * <dl><dt></dt><dd>
 * <dl>
 * <dt> <code>userFilter=<b>ldap_filter</b></code> </dt>
 * <dd> This option specifies the search filter to use to locate a user's
 *      entry in the LDAP directory. It is used to determine a user's
 *      distinguished name.
 *      <code><b>ldap_filter</b></code> is an LDAP filter string
 *      (<a href="http://www.ietf.org/rfc/rfc2254.txt">RFC 2254</a>).
 *      If it contains the special token "<code><b>{USERNAME}</b></code>"
 *      then that token will be replaced with the supplied username value
 *      before the filter is used to search the directory. </dd>
 *
 * <dt> <code>authIdentity=<b>auth_id</b></code> </dt>
 * <dd> This option specifies the identity to use when authenticating a user
 *      to the LDAP directory.
 *      <code><b>auth_id</b></code> may be an LDAP distinguished name string
 *      (<a href="http://www.ietf.org/rfc/rfc2253.txt">RFC 2253</a>) or some
 *      other string name.
 *      It must contain the special token "<code><b>{USERNAME}</b></code>"
 *      which will be replaced with the supplied username value before the
 *      name is used for authentication.
 *      Note that if this option does not contain a distinguished name then
 *      the <code>userFilter</code> option must also be specified. </dd>
 *
 * <dt> <code>authzIdentity=<b>authz_id</b></code> </dt>
 * <dd> This option specifies an authorization identity for the user.
 *      <code><b>authz_id</b></code> is any string name.
 *      If it comprises a single special token with curly braces then
 *      that token is treated as a attribute name and will be replaced with a
 *      single value of that attribute from the user's LDAP entry.
 *      If the attribute cannot be found then the option is ignored.
 *      When this option is supplied and the user has been successfully
 *      authenticated then an additional {@link UserPrincipal}
 *      is created using the authorization identity and it is associated with
 *      the current {@link Subject}. </dd>
 *
 * <dt> <code>useSSL</code> </dt>
 * <dd> if <code>false</code>, this module does not establish an SSL connection
 *      to the LDAP server before attempting authentication. SSL is used to
 *      protect the privacy of the user's password because it is transmitted
 *      in the clear over LDAP.
 *      By default, this module uses SSL. </dd>
 *
 * <dt> <code>useFirstPass</code> </dt>
 * <dd> if <code>true</code>, this module retrieves the username and password
 *      from the module's shared state, using "javax.security.auth.login.name"
 *      and "javax.security.auth.login.password" as the respective keys. The
 *      retrieved values are used for authentication. If authentication fails,
 *      no attempt for a retry is made, and the failure is reported back to
 *      the calling application.</dd>
 *
 * <dt> <code>tryFirstPass</code> </dt>
 * <dd> if <code>true</code>, this module retrieves the username and password
 *      from the module's shared state, using "javax.security.auth.login.name"
 *       and "javax.security.auth.login.password" as the respective keys.  The
 *      retrieved values are used for authentication. If authentication fails,
 *      the module uses the {@link CallbackHandler} to retrieve a new username
 *      and password, and another attempt to authenticate is made. If the
 *      authentication fails, the failure is reported back to the calling
 *      application.</dd>
 *
 * <dt> <code>storePass</code> </dt>
 * <dd> if <code>true</code>, this module stores the username and password
 *      obtained from the {@link CallbackHandler} in the module's shared state,
 *      using
 *      "javax.security.auth.login.name" and
 *      "javax.security.auth.login.password" as the respective keys.  This is
 *      not performed if existing values already exist for the username and
 *      password in the shared state, or if authentication fails.</dd>
 *
 * <dt> <code>clearPass</code> </dt>
 * <dd> if <code>true</code>, this module clears the username and password
 *      stored in the module's shared state after both phases of authentication
 *      (login and commit) have completed.</dd>
 *
 * <dt> <code>debug</code> </dt>
 * <dd> if <code>true</code>, debug messages are displayed on the standard
 *      output stream.
 * </dl>
 * </dl>
 *
 * <p>
 * Arbitrary
 * <a href="{@docRoot}/../../../../../technotes/guides/jndi/jndi-ldap-gl.html#PROP">JNDI properties</a>
 * may also be specified in the {@link Configuration}.
 * They are added to the environment and passed to the LDAP provider.
 * Note that the following four JNDI properties are set by this module directly
 * and are ignored if also present in the configuration:
 * <ul>
 * <li> <code>java.naming.provider.url</code>
 * <li> <code>java.naming.security.principal</code>
 * <li> <code>java.naming.security.credentials</code>
 * <li> <code>java.naming.security.protocol</code>
 * </ul>
 *
 * <p>
 * Three sample {@link Configuration}s are shown below.
 * The first one activates search-first mode. It identifies the LDAP server
 * and specifies that users' entries be located by their <code>uid</code> and
 * <code>objectClass</code> attributes. It also specifies that an identity
 * based on the user's <code>employeeNumber</code> attribute should be created.
 * The second one activates authentication-first mode. It requests that the
 * LDAP server be located dynamically, that authentication be performed using
 * the supplied username directly but without the protection of SSL and that
 * users' entries be located by one of three naming attributes and their
 * <code>objectClass</code> attribute.
 * The third one activates authentication-only mode. It identifies alternative
 * LDAP servers, it specifies the distinguished name to use for
 * authentication and a fixed identity to use for authorization. No directory
 * search is performed.
 *
 * <pre>
 *
 *     ExampleApplication {
 *         com.sun.security.auth.module.LdapLoginModule REQUIRED
 *             userProvider="ldap://ldap-svr/ou=people,dc=example,dc=com"
 *             userFilter="(&(uid={USERNAME})(objectClass=inetOrgPerson))"
 *             authzIdentity="{EMPLOYEENUMBER}"
 *             debug=true;
 *     };
 *
 *     ExampleApplication {
 *         com.sun.security.auth.module.LdapLoginModule REQUIRED
 *             userProvider="ldap:///cn=users,dc=example,dc=com"
 *             authIdentity="{USERNAME}"
 *             userFilter="(&(|(samAccountName={USERNAME})(userPrincipalName={USERNAME})(cn={USERNAME}))(objectClass=user))"
 *             useSSL=false
 *             debug=true;
 *     };
 *
 *     ExampleApplication {
 *         com.sun.security.auth.module.LdapLoginModule REQUIRED
 *             userProvider="ldap://ldap-svr1 ldap://ldap-svr2"
 *             authIdentity="cn={USERNAME},ou=people,dc=example,dc=com"
 *             authzIdentity="staff"
 *             debug=true;
 *     };
 *
 * </pre>
 *
 * <dl>
 * <dt><b>Note:</b> </dt>
 * <dd>When a {@link SecurityManager} is active then an application
 *     that creates a {@link LoginContext} and uses a {@link LoginModule}
 *     must be granted certain permissions.
 *     <p>
 *     If the application creates a login context using an <em>installed</em>
 *     {@link Configuration} then the application must be granted the
 *     {@link AuthPermission} to create login contexts.
 *     For example, the following security policy allows an application in
 *     the user's current directory to instantiate <em>any</em> login context:
 *     <pre>
 *
 *     grant codebase "file:${user.dir}/" {
 *         permission javax.security.auth.AuthPermission "createLoginContext.*";
 *     };
 *     </pre>
 *
 *     Alternatively, if the application creates a login context using a
 *     <em>caller-specified</em> {@link Configuration} then the application
 *     must be granted the permissions required by the {@link LoginModule}.
 *     <em>This</em> module requires the following two permissions:
 *     <p>
 *     <ul>
 *     <li> The {@link SocketPermission} to connect to an LDAP server.
 *     <li> The {@link AuthPermission} to modify the set of {@link Principal}s
 *          associated with a {@link Subject}.
 *     </ul>
 *     <p>
 *     For example, the following security policy grants an application in the
 *     user's current directory all the permissions required by this module:
 *     <pre>
 *
 *     grant codebase "file:${user.dir}/" {
 *         permission java.net.SocketPermission "*:389", "connect";
 *         permission java.net.SocketPermission "*:636", "connect";
 *         permission javax.security.auth.AuthPermission "modifyPrincipals";
 *     };
 *     </pre>
 * </dd>
 * </dl>
 *
 * @since 1.6
 */
@jdk.Exported
public class LdapLoginModule implements LoginModule {

    // Use the default classloader for this class to load the prompt strings.
    private static final ResourceBundle rb = AccessController.doPrivileged(
            new PrivilegedAction<ResourceBundle>() {
                public ResourceBundle run() {
                    return ResourceBundle.getBundle(
                        "sun.security.util.AuthResources");
                }
            }
        );

    // Keys to retrieve the stored username and password
    private static final String USERNAME_KEY = "javax.security.auth.login.name";
    private static final String PASSWORD_KEY =
        "javax.security.auth.login.password";

    // Option names
    private static final String USER_PROVIDER = "userProvider";
    private static final String USER_FILTER = "userFilter";
    private static final String AUTHC_IDENTITY = "authIdentity";
    private static final String AUTHZ_IDENTITY = "authzIdentity";

    // Used for the username token replacement
    private static final String USERNAME_TOKEN = "{USERNAME}";
    private static final Pattern USERNAME_PATTERN =
        Pattern.compile("\\{USERNAME\\}");

    // Configurable options
    private String userProvider;
    private String userFilter;
    private String authcIdentity;
    private String authzIdentity;
    private String authzIdentityAttr = null;
    private boolean useSSL = true;
    private boolean authFirst = false;
    private boolean authOnly = false;
    private boolean useFirstPass = false;
    private boolean tryFirstPass = false;
    private boolean storePass = false;
    private boolean clearPass = false;
    private boolean debug = false;

    // Authentication status
    private boolean succeeded = false;
    private boolean commitSucceeded = false;

    // Supplied username and password
    private String username;
    private char[] password;

    // User's identities
    private LdapPrincipal ldapPrincipal;
    private UserPrincipal userPrincipal;
    private UserPrincipal authzPrincipal;

    // Initial state
    private Subject subject;
    private CallbackHandler callbackHandler;
    private Map<String, Object> sharedState;
    private Map<String, ?> options;
    private LdapContext ctx;
    private Matcher identityMatcher = null;
    private Matcher filterMatcher = null;
    private Hashtable<String, Object> ldapEnvironment;
    private SearchControls constraints = null;

    /**
     * Initialize this <code>LoginModule</code>.
     *
     * @param subject the <code>Subject</code> to be authenticated.
     * @param callbackHandler a <code>CallbackHandler</code> to acquire the
     *                  username and password.
     * @param sharedState shared <code>LoginModule</code> state.
     * @param options options specified in the login
     *                  <code>Configuration</code> for this particular
     *                  <code>LoginModule</code>.
     */
    // Unchecked warning from (Map<String, Object>)sharedState is safe
    // since javax.security.auth.login.LoginContext passes a raw HashMap.
    @SuppressWarnings("unchecked")
    public void initialize(Subject subject, CallbackHandler callbackHandler,
                        Map<String, ?> sharedState, Map<String, ?> options) {

        this.subject = subject;
        this.callbackHandler = callbackHandler;
        this.sharedState = (Map<String, Object>)sharedState;
        this.options = options;

        ldapEnvironment = new Hashtable<String, Object>(9);
        ldapEnvironment.put(Context.INITIAL_CONTEXT_FACTORY,
            "com.sun.jndi.ldap.LdapCtxFactory");

        // Add any JNDI properties to the environment
        for (String key : options.keySet()) {
            if (key.indexOf(".") > -1) {
                ldapEnvironment.put(key, options.get(key));
            }
        }

        // initialize any configured options

        userProvider = (String)options.get(USER_PROVIDER);
        if (userProvider != null) {
            ldapEnvironment.put(Context.PROVIDER_URL, userProvider);
        }

        authcIdentity = (String)options.get(AUTHC_IDENTITY);
        if (authcIdentity != null &&
            (authcIdentity.indexOf(USERNAME_TOKEN) != -1)) {
            identityMatcher = USERNAME_PATTERN.matcher(authcIdentity);
        }

        userFilter = (String)options.get(USER_FILTER);
        if (userFilter != null) {
            if (userFilter.indexOf(USERNAME_TOKEN) != -1) {
                filterMatcher = USERNAME_PATTERN.matcher(userFilter);
            }
            constraints = new SearchControls();
            constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
            constraints.setReturningAttributes(new String[0]); //return no attrs
            constraints.setReturningObjFlag(true); // to get the full DN
        }

        authzIdentity = (String)options.get(AUTHZ_IDENTITY);
        if (authzIdentity != null &&
            authzIdentity.startsWith("{") && authzIdentity.endsWith("}")) {
            if (constraints != null) {
                authzIdentityAttr =
                    authzIdentity.substring(1, authzIdentity.length() - 1);
                constraints.setReturningAttributes(
                    new String[]{authzIdentityAttr});
            }
            authzIdentity = null; // set later, from the specified attribute
        }

        // determine mode
        if (authcIdentity != null) {
            if (userFilter != null) {
                authFirst = true; // authentication-first mode
            } else {
                authOnly = true; // authentication-only mode
            }
        }

        if ("false".equalsIgnoreCase((String)options.get("useSSL"))) {
            useSSL = false;
            ldapEnvironment.remove(Context.SECURITY_PROTOCOL);
        } else {
            ldapEnvironment.put(Context.SECURITY_PROTOCOL, "ssl");
        }

        tryFirstPass =
                "true".equalsIgnoreCase((String)options.get("tryFirstPass"));

        useFirstPass =
                "true".equalsIgnoreCase((String)options.get("useFirstPass"));

        storePass = "true".equalsIgnoreCase((String)options.get("storePass"));

        clearPass = "true".equalsIgnoreCase((String)options.get("clearPass"));

        debug = "true".equalsIgnoreCase((String)options.get("debug"));

        if (debug) {
            if (authFirst) {
                System.out.println("\t\t[LdapLoginModule] " +
                    "authentication-first mode; " +
                    (useSSL ? "SSL enabled" : "SSL disabled"));
            } else if (authOnly) {
                System.out.println("\t\t[LdapLoginModule] " +
                    "authentication-only mode; " +
                    (useSSL ? "SSL enabled" : "SSL disabled"));
            } else {
                System.out.println("\t\t[LdapLoginModule] " +
                    "search-first mode; " +
                    (useSSL ? "SSL enabled" : "SSL disabled"));
            }
        }
    }

    /**
     * Begin user authentication.
     *
     * <p> Acquire the user's credentials and verify them against the
     * specified LDAP directory.
     *
     * @return true always, since this <code>LoginModule</code>
     *          should not be ignored.
     * @exception FailedLoginException if the authentication fails.
     * @exception LoginException if this <code>LoginModule</code>
     *          is unable to perform the authentication.
     */
    public boolean login() throws LoginException {

        if (userProvider == null) {
            throw new LoginException
                ("Unable to locate the LDAP directory service");
        }

        if (debug) {
            System.out.println("\t\t[LdapLoginModule] user provider: " +
                userProvider);
        }

        // attempt the authentication
        if (tryFirstPass) {

            try {
                // attempt the authentication by getting the
                // username and password from shared state
                attemptAuthentication(true);

                // authentication succeeded
                succeeded = true;
                if (debug) {
                    System.out.println("\t\t[LdapLoginModule] " +
                                "tryFirstPass succeeded");
                }
                return true;

            } catch (LoginException le) {
                // authentication failed -- try again below by prompting
                cleanState();
                if (debug) {
                    System.out.println("\t\t[LdapLoginModule] " +
                                "tryFirstPass failed: " + le.toString());
                }
            }

        } else if (useFirstPass) {

            try {
                // attempt the authentication by getting the
                // username and password from shared state
                attemptAuthentication(true);

                // authentication succeeded
                succeeded = true;
                if (debug) {
                    System.out.println("\t\t[LdapLoginModule] " +
                                "useFirstPass succeeded");
                }
                return true;

            } catch (LoginException le) {
                // authentication failed
                cleanState();
                if (debug) {
                    System.out.println("\t\t[LdapLoginModule] " +
                                "useFirstPass failed");
                }
                throw le;
            }
        }

        // attempt the authentication by prompting for the username and pwd
        try {
            attemptAuthentication(false);

            // authentication succeeded
           succeeded = true;
            if (debug) {
                System.out.println("\t\t[LdapLoginModule] " +
                                "authentication succeeded");
            }
            return true;

        } catch (LoginException le) {
            cleanState();
            if (debug) {
                System.out.println("\t\t[LdapLoginModule] " +
                                "authentication failed");
            }
            throw le;
        }
    }

    /**
     * Complete user authentication.
     *
     * <p> This method is called if the LoginContext's
     * overall authentication succeeded
     * (the relevant REQUIRED, REQUISITE, SUFFICIENT and OPTIONAL LoginModules
     * succeeded).
     *
     * <p> If this LoginModule's own authentication attempt
     * succeeded (checked by retrieving the private state saved by the
     * <code>login</code> method), then this method associates an
     * <code>LdapPrincipal</code> and one or more <code>UserPrincipal</code>s
     * with the <code>Subject</code> located in the
     * <code>LoginModule</code>.  If this LoginModule's own
     * authentication attempted failed, then this method removes
     * any state that was originally saved.
     *
     * @exception LoginException if the commit fails
     * @return true if this LoginModule's own login and commit
     *          attempts succeeded, or false otherwise.
     */
    public boolean commit() throws LoginException {

        if (succeeded == false) {
            return false;
        } else {
            if (subject.isReadOnly()) {
                cleanState();
                throw new LoginException ("Subject is read-only");
            }
            // add Principals to the Subject
            Set<Principal> principals = subject.getPrincipals();
            if (! principals.contains(ldapPrincipal)) {
                principals.add(ldapPrincipal);
            }
            if (debug) {
                System.out.println("\t\t[LdapLoginModule] " +
                                   "added LdapPrincipal \"" +
                                   ldapPrincipal +
                                   "\" to Subject");
            }

            if (! principals.contains(userPrincipal)) {
                principals.add(userPrincipal);
            }
            if (debug) {
                System.out.println("\t\t[LdapLoginModule] " +
                                   "added UserPrincipal \"" +
                                   userPrincipal +
                                   "\" to Subject");
            }

            if (authzPrincipal != null &&
                (! principals.contains(authzPrincipal))) {
                principals.add(authzPrincipal);

                if (debug) {
                    System.out.println("\t\t[LdapLoginModule] " +
                                   "added UserPrincipal \"" +
                                   authzPrincipal +
                                   "\" to Subject");
                }
            }
        }
        // in any case, clean out state
        cleanState();
        commitSucceeded = true;
        return true;
    }

    /**
     * Abort user authentication.
     *
     * <p> This method is called if the overall authentication failed.
     * (the relevant REQUIRED, REQUISITE, SUFFICIENT and OPTIONAL LoginModules
     * did not succeed).
     *
     * <p> If this LoginModule's own authentication attempt
     * succeeded (checked by retrieving the private state saved by the
     * <code>login</code> and <code>commit</code> methods),
     * then this method cleans up any state that was originally saved.
     *
     * @exception LoginException if the abort fails.
     * @return false if this LoginModule's own login and/or commit attempts
     *          failed, and true otherwise.
     */
    public boolean abort() throws LoginException {
        if (debug)
            System.out.println("\t\t[LdapLoginModule] " +
                "aborted authentication");

        if (succeeded == false) {
            return false;
        } else if (succeeded == true && commitSucceeded == false) {

            // Clean out state
            succeeded = false;
            cleanState();

            ldapPrincipal = null;
            userPrincipal = null;
            authzPrincipal = null;
        } else {
            // overall authentication succeeded and commit succeeded,
            // but someone else's commit failed
            logout();
        }
        return true;
    }

    /**
     * Logout a user.
     *
     * <p> This method removes the Principals
     * that were added by the <code>commit</code> method.
     *
     * @exception LoginException if the logout fails.
     * @return true in all cases since this <code>LoginModule</code>
     *          should not be ignored.
     */
    public boolean logout() throws LoginException {
        if (subject.isReadOnly()) {
            cleanState();
            throw new LoginException ("Subject is read-only");
        }
        Set<Principal> principals = subject.getPrincipals();
        principals.remove(ldapPrincipal);
        principals.remove(userPrincipal);
        if (authzIdentity != null) {
            principals.remove(authzPrincipal);
        }

        // clean out state
        cleanState();
        succeeded = false;
        commitSucceeded = false;

        ldapPrincipal = null;
        userPrincipal = null;
        authzPrincipal = null;

        if (debug) {
            System.out.println("\t\t[LdapLoginModule] logged out Subject");
        }
        return true;
    }

    /**
     * Attempt authentication
     *
     * @param getPasswdFromSharedState boolean that tells this method whether
     *          to retrieve the password from the sharedState.
     * @exception LoginException if the authentication attempt fails.
     */
    private void attemptAuthentication(boolean getPasswdFromSharedState)
        throws LoginException {

        // first get the username and password
        getUsernamePassword(getPasswdFromSharedState);

        if (password == null || password.length == 0) {
            throw (LoginException)
                new FailedLoginException("No password was supplied");
        }

        String dn = "";

        if (authFirst || authOnly) {

            String id = replaceUsernameToken(identityMatcher, authcIdentity);

            // Prepare to bind using user's username and password
            ldapEnvironment.put(Context.SECURITY_CREDENTIALS, password);
            ldapEnvironment.put(Context.SECURITY_PRINCIPAL, id);

            if (debug) {
                System.out.println("\t\t[LdapLoginModule] " +
                    "attempting to authenticate user: " + username);
            }

            try {
                // Connect to the LDAP server (using simple bind)
                ctx = new InitialLdapContext(ldapEnvironment, null);

            } catch (NamingException e) {
                throw (LoginException)
                    new FailedLoginException("Cannot bind to LDAP server")
                        .initCause(e);
            }

            // Authentication has succeeded

            // Locate the user's distinguished name
            if (userFilter != null) {
                dn = findUserDN(ctx);
            } else {
                dn = id;
            }

        } else {

            try {
                // Connect to the LDAP server (using anonymous bind)
                ctx = new InitialLdapContext(ldapEnvironment, null);

            } catch (NamingException e) {
                throw (LoginException)
                    new FailedLoginException("Cannot connect to LDAP server")
                        .initCause(e);
            }

            // Locate the user's distinguished name
            dn = findUserDN(ctx);

            try {

                // Prepare to bind using user's distinguished name and password
                ctx.addToEnvironment(Context.SECURITY_AUTHENTICATION, "simple");
                ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, dn);
                ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, password);

                if (debug) {
                    System.out.println("\t\t[LdapLoginModule] " +
                        "attempting to authenticate user: " + username);
                }
                // Connect to the LDAP server (using simple bind)
                ctx.reconnect(null);

                // Authentication has succeeded

            } catch (NamingException e) {
                throw (LoginException)
                    new FailedLoginException("Cannot bind to LDAP server")
                        .initCause(e);
            }
        }

        // Save input as shared state only if authentication succeeded
        if (storePass &&
            !sharedState.containsKey(USERNAME_KEY) &&
            !sharedState.containsKey(PASSWORD_KEY)) {
            sharedState.put(USERNAME_KEY, username);
            sharedState.put(PASSWORD_KEY, password);
        }

        // Create the user principals
        userPrincipal = new UserPrincipal(username);
        if (authzIdentity != null) {
            authzPrincipal = new UserPrincipal(authzIdentity);
        }

        try {

            ldapPrincipal = new LdapPrincipal(dn);

        } catch (InvalidNameException e) {
            if (debug) {
                System.out.println("\t\t[LdapLoginModule] " +
                                   "cannot create LdapPrincipal: bad DN");
            }
            throw (LoginException)
                new FailedLoginException("Cannot create LdapPrincipal")
                    .initCause(e);
        }
    }

    /**
     * Search for the user's entry.
     * Determine the distinguished name of the user's entry and optionally
     * an authorization identity for the user.
     *
     * @param ctx an LDAP context to use for the search
     * @return the user's distinguished name or an empty string if none
     *         was found.
     * @exception LoginException if the user's entry cannot be found.
     */
    private String findUserDN(LdapContext ctx) throws LoginException {

        String userDN = "";

        // Locate the user's LDAP entry
        if (userFilter != null) {
            if (debug) {
                System.out.println("\t\t[LdapLoginModule] " +
                    "searching for entry belonging to user: " + username);
            }
        } else {
            if (debug) {
                System.out.println("\t\t[LdapLoginModule] " +
                    "cannot search for entry belonging to user: " + username);
            }
            throw (LoginException)
                new FailedLoginException("Cannot find user's LDAP entry");
        }

        try {
            NamingEnumeration<SearchResult> results = ctx.search("",
                replaceUsernameToken(filterMatcher, userFilter), constraints);

            // Extract the distinguished name of the user's entry
            // (Use the first entry if more than one is returned)
            if (results.hasMore()) {
                SearchResult entry = results.next();

                // %%% - use the SearchResult.getNameInNamespace method
                //        available in JDK 1.5 and later.
                //        (can remove call to constraints.setReturningObjFlag)
                userDN = ((Context)entry.getObject()).getNameInNamespace();

                if (debug) {
                    System.out.println("\t\t[LdapLoginModule] found entry: " +
                        userDN);
                }

                // Extract a value from user's authorization identity attribute
                if (authzIdentityAttr != null) {
                    Attribute attr =
                        entry.getAttributes().get(authzIdentityAttr);
                    if (attr != null) {
                        Object val = attr.get();
                        if (val instanceof String) {
                            authzIdentity = (String) val;
                        }
                    }
                }

                results.close();

            } else {
                // Bad username
                if (debug) {
                    System.out.println("\t\t[LdapLoginModule] user's entry " +
                        "not found");
                }
            }

        } catch (NamingException e) {
            // ignore
        }

        if (userDN.equals("")) {
            throw (LoginException)
                new FailedLoginException("Cannot find user's LDAP entry");
        } else {
            return userDN;
        }
    }

    /**
     * Replace the username token
     *
     * @param string the target string
     * @return the modified string
     */
    private String replaceUsernameToken(Matcher matcher, String string) {
        return matcher != null ? matcher.replaceAll(username) : string;
    }

    /**
     * Get the username and password.
     * This method does not return any value.
     * Instead, it sets global name and password variables.
     *
     * <p> Also note that this method will set the username and password
     * values in the shared state in case subsequent LoginModules
     * want to use them via use/tryFirstPass.
     *
     * @param getPasswdFromSharedState boolean that tells this method whether
     *          to retrieve the password from the sharedState.
     * @exception LoginException if the username/password cannot be acquired.
     */
    private void getUsernamePassword(boolean getPasswdFromSharedState)
        throws LoginException {

        if (getPasswdFromSharedState) {
            // use the password saved by the first module in the stack
            username = (String)sharedState.get(USERNAME_KEY);
            password = (char[])sharedState.get(PASSWORD_KEY);
            return;
        }

        // prompt for a username and password
        if (callbackHandler == null)
            throw new LoginException("No CallbackHandler available " +
                "to acquire authentication information from the user");

        Callback[] callbacks = new Callback[2];
        callbacks[0] = new NameCallback(rb.getString("username."));
        callbacks[1] = new PasswordCallback(rb.getString("password."), false);

        try {
            callbackHandler.handle(callbacks);
            username = ((NameCallback)callbacks[0]).getName();
            char[] tmpPassword = ((PasswordCallback)callbacks[1]).getPassword();
            password = new char[tmpPassword.length];
            System.arraycopy(tmpPassword, 0,
                                password, 0, tmpPassword.length);
            ((PasswordCallback)callbacks[1]).clearPassword();

        } catch (java.io.IOException ioe) {
            throw new LoginException(ioe.toString());

        } catch (UnsupportedCallbackException uce) {
            throw new LoginException("Error: " + uce.getCallback().toString() +
                        " not available to acquire authentication information" +
                        " from the user");
        }
    }

    /**
     * Clean out state because of a failed authentication attempt
     */
    private void cleanState() {
        username = null;
        if (password != null) {
            Arrays.fill(password, ' ');
            password = null;
        }
        try {
            if (ctx != null) {
                ctx.close();
            }
        } catch (NamingException e) {
            // ignore
        }
        ctx = null;

        if (clearPass) {
            sharedState.remove(USERNAME_KEY);
            sharedState.remove(PASSWORD_KEY);
        }
    }
}
