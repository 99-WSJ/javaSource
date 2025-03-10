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

package java8.com.sun.security.auth.module;

import com.sun.security.auth.*;
import com.sun.security.auth.module.NTSystem;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import java.security.Principal;
import java.util.Map;
import java.util.Set;

/**
 * <p> This <code>LoginModule</code>
 * renders a user's NT security information as some number of
 * <code>Principal</code>s
 * and associates them with a <code>Subject</code>.
 *
 * <p> This LoginModule recognizes the debug option.
 * If set to true in the login Configuration,
 * debug messages will be output to the output stream, System.out.
 *
 * <p> This LoginModule also recognizes the debugNative option.
 * If set to true in the login Configuration,
 * debug messages from the native component of the module
 * will be output to the output stream, System.out.
 *
 * @see LoginModule
 */
@jdk.Exported
public class NTLoginModule implements LoginModule {

    private NTSystem ntSystem;

    // initial state
    private Subject subject;
    private CallbackHandler callbackHandler;
    private Map<String, ?> sharedState;
    private Map<String, ?> options;

    // configurable option
    private boolean debug = false;
    private boolean debugNative = false;

    // the authentication status
    private boolean succeeded = false;
    private boolean commitSucceeded = false;

    private NTUserPrincipal userPrincipal;              // user name
    private NTSidUserPrincipal userSID;                 // user SID
    private NTDomainPrincipal userDomain;               // user domain
    private NTSidDomainPrincipal domainSID;             // domain SID
    private NTSidPrimaryGroupPrincipal primaryGroup;    // primary group
    private NTSidGroupPrincipal groups[];               // supplementary groups
    private NTNumericCredential iToken;                 // impersonation token

    /**
     * Initialize this <code>LoginModule</code>.
     *
     * <p>
     *
     * @param subject the <code>Subject</code> to be authenticated. <p>
     *
     * @param callbackHandler a <code>CallbackHandler</code> for communicating
     *          with the end user (prompting for usernames and
     *          passwords, for example). This particular LoginModule only
     *          extracts the underlying NT system information, so this
     *          parameter is ignored.<p>
     *
     * @param sharedState shared <code>LoginModule</code> state. <p>
     *
     * @param options options specified in the login
     *                  <code>Configuration</code> for this particular
     *                  <code>LoginModule</code>.
     */
    public void initialize(Subject subject, CallbackHandler callbackHandler,
                           Map<String,?> sharedState,
                           Map<String,?> options)
    {

        this.subject = subject;
        this.callbackHandler = callbackHandler;
        this.sharedState = sharedState;
        this.options = options;

        // initialize any configured options
        debug = "true".equalsIgnoreCase((String)options.get("debug"));
        debugNative="true".equalsIgnoreCase((String)options.get("debugNative"));

        if (debugNative == true) {
            debug = true;
        }
    }

    /**
     * Import underlying NT system identity information.
     *
     * <p>
     *
     * @return true in all cases since this <code>LoginModule</code>
     *          should not be ignored.
     *
     * @exception FailedLoginException if the authentication fails. <p>
     *
     * @exception LoginException if this <code>LoginModule</code>
     *          is unable to perform the authentication.
     */
    public boolean login() throws LoginException {

        succeeded = false; // Indicate not yet successful

        ntSystem = new NTSystem(debugNative);
        if (ntSystem == null) {
            if (debug) {
                System.out.println("\t\t[NTLoginModule] " +
                                   "Failed in NT login");
            }
            throw new FailedLoginException
                ("Failed in attempt to import the " +
                 "underlying NT system identity information");
        }

        if (ntSystem.getName() == null) {
            throw new FailedLoginException
                ("Failed in attempt to import the " +
                 "underlying NT system identity information");
        }
        userPrincipal = new NTUserPrincipal(ntSystem.getName());
        if (debug) {
            System.out.println("\t\t[NTLoginModule] " +
                               "succeeded importing info: ");
            System.out.println("\t\t\tuser name = " +
                userPrincipal.getName());
        }

        if (ntSystem.getUserSID() != null) {
            userSID = new NTSidUserPrincipal(ntSystem.getUserSID());
            if (debug) {
                System.out.println("\t\t\tuser SID = " +
                        userSID.getName());
            }
        }
        if (ntSystem.getDomain() != null) {
            userDomain = new NTDomainPrincipal(ntSystem.getDomain());
            if (debug) {
                System.out.println("\t\t\tuser domain = " +
                        userDomain.getName());
            }
        }
        if (ntSystem.getDomainSID() != null) {
            domainSID =
                new NTSidDomainPrincipal(ntSystem.getDomainSID());
            if (debug) {
                System.out.println("\t\t\tuser domain SID = " +
                        domainSID.getName());
            }
        }
        if (ntSystem.getPrimaryGroupID() != null) {
            primaryGroup =
                new NTSidPrimaryGroupPrincipal(ntSystem.getPrimaryGroupID());
            if (debug) {
                System.out.println("\t\t\tuser primary group = " +
                        primaryGroup.getName());
            }
        }
        if (ntSystem.getGroupIDs() != null &&
            ntSystem.getGroupIDs().length > 0) {

            String groupSIDs[] = ntSystem.getGroupIDs();
            groups = new NTSidGroupPrincipal[groupSIDs.length];
            for (int i = 0; i < groupSIDs.length; i++) {
                groups[i] = new NTSidGroupPrincipal(groupSIDs[i]);
                if (debug) {
                    System.out.println("\t\t\tuser group = " +
                        groups[i].getName());
                }
            }
        }
        if (ntSystem.getImpersonationToken() != 0) {
            iToken = new NTNumericCredential(ntSystem.getImpersonationToken());
            if (debug) {
                System.out.println("\t\t\timpersonation token = " +
                        ntSystem.getImpersonationToken());
            }
        }

        succeeded = true;
        return succeeded;
    }

    /**
     * <p> This method is called if the LoginContext's
     * overall authentication succeeded
     * (the relevant REQUIRED, REQUISITE, SUFFICIENT and OPTIONAL LoginModules
     * succeeded).
     *
     * <p> If this LoginModule's own authentication attempt
     * succeeded (checked by retrieving the private state saved by the
     * <code>login</code> method), then this method associates some
     * number of various <code>Principal</code>s
     * with the <code>Subject</code> located in the
     * <code>LoginModuleContext</code>.  If this LoginModule's own
     * authentication attempted failed, then this method removes
     * any state that was originally saved.
     *
     * <p>
     *
     * @exception LoginException if the commit fails.
     *
     * @return true if this LoginModule's own login and commit
     *          attempts succeeded, or false otherwise.
     */
    public boolean commit() throws LoginException {
        if (succeeded == false) {
            if (debug) {
                System.out.println("\t\t[NTLoginModule]: " +
                    "did not add any Principals to Subject " +
                    "because own authentication failed.");
            }
            return false;
        }
        if (subject.isReadOnly()) {
            throw new LoginException ("Subject is ReadOnly");
        }
        Set<Principal> principals = subject.getPrincipals();

        // we must have a userPrincipal - everything else is optional
        if (!principals.contains(userPrincipal)) {
            principals.add(userPrincipal);
        }
        if (userSID != null && !principals.contains(userSID)) {
            principals.add(userSID);
        }

        if (userDomain != null && !principals.contains(userDomain)) {
            principals.add(userDomain);
        }
        if (domainSID != null && !principals.contains(domainSID)) {
            principals.add(domainSID);
        }

        if (primaryGroup != null && !principals.contains(primaryGroup)) {
            principals.add(primaryGroup);
        }
        for (int i = 0; groups != null && i < groups.length; i++) {
            if (!principals.contains(groups[i])) {
                principals.add(groups[i]);
            }
        }

        Set<Object> pubCreds = subject.getPublicCredentials();
        if (iToken != null && !pubCreds.contains(iToken)) {
            pubCreds.add(iToken);
        }
        commitSucceeded = true;
        return true;
    }


    /**
     * <p> This method is called if the LoginContext's
     * overall authentication failed.
     * (the relevant REQUIRED, REQUISITE, SUFFICIENT and OPTIONAL LoginModules
     * did not succeed).
     *
     * <p> If this LoginModule's own authentication attempt
     * succeeded (checked by retrieving the private state saved by the
     * <code>login</code> and <code>commit</code> methods),
     * then this method cleans up any state that was originally saved.
     *
     * <p>
     *
     * @exception LoginException if the abort fails.
     *
     * @return false if this LoginModule's own login and/or commit attempts
     *          failed, and true otherwise.
     */
    public boolean abort() throws LoginException {
        if (debug) {
            System.out.println("\t\t[NTLoginModule]: " +
                "aborted authentication attempt");
        }

        if (succeeded == false) {
            return false;
        } else if (succeeded == true && commitSucceeded == false) {
            ntSystem = null;
            userPrincipal = null;
            userSID = null;
            userDomain = null;
            domainSID = null;
            primaryGroup = null;
            groups = null;
            iToken = null;
            succeeded = false;
        } else {
            // overall authentication succeeded and commit succeeded,
            // but someone else's commit failed
            logout();
        }
        return succeeded;
    }

    /**
     * Logout the user.
     *
     * <p> This method removes the <code>NTUserPrincipal</code>,
     * <code>NTDomainPrincipal</code>, <code>NTSidUserPrincipal</code>,
     * <code>NTSidDomainPrincipal</code>, <code>NTSidGroupPrincipal</code>s,
     * and <code>NTSidPrimaryGroupPrincipal</code>
     * that may have been added by the <code>commit</code> method.
     *
     * <p>
     *
     * @exception LoginException if the logout fails.
     *
     * @return true in all cases since this <code>LoginModule</code>
     *          should not be ignored.
     */
    public boolean logout() throws LoginException {

        if (subject.isReadOnly()) {
            throw new LoginException ("Subject is ReadOnly");
        }
        Set<Principal> principals = subject.getPrincipals();
        if (principals.contains(userPrincipal)) {
            principals.remove(userPrincipal);
        }
        if (principals.contains(userSID)) {
            principals.remove(userSID);
        }
        if (principals.contains(userDomain)) {
            principals.remove(userDomain);
        }
        if (principals.contains(domainSID)) {
            principals.remove(domainSID);
        }
        if (principals.contains(primaryGroup)) {
            principals.remove(primaryGroup);
        }
        for (int i = 0; groups != null && i < groups.length; i++) {
            if (principals.contains(groups[i])) {
                principals.remove(groups[i]);
            }
        }

        Set<Object> pubCreds = subject.getPublicCredentials();
        if (pubCreds.contains(iToken)) {
            pubCreds.remove(iToken);
        }

        succeeded = false;
        commitSucceeded = false;
        userPrincipal = null;
        userDomain = null;
        userSID = null;
        domainSID = null;
        groups = null;
        primaryGroup = null;
        iToken = null;
        ntSystem = null;

        if (debug) {
                System.out.println("\t\t[NTLoginModule] " +
                                "completed logout processing");
        }
        return true;
    }
}
