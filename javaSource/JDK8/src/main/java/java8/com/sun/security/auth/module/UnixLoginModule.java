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

package java8.sun.security.auth.module;

import com.sun.security.auth.UnixNumericGroupPrincipal;
import com.sun.security.auth.UnixNumericUserPrincipal;
import com.sun.security.auth.UnixPrincipal;
import java8.com.sun.security.auth.module.UnixSystem;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import java.util.LinkedList;
import java.util.Map;

/**
 * <p> This <code>LoginModule</code> imports a user's Unix
 * <code>Principal</code> information (<code>UnixPrincipal</code>,
 * <code>UnixNumericUserPrincipal</code>,
 * and <code>UnixNumericGroupPrincipal</code>)
 * and associates them with the current <code>Subject</code>.
 *
 * <p> This LoginModule recognizes the debug option.
 * If set to true in the login Configuration,
 * debug messages will be output to the output stream, System.out.
 *
 */
@jdk.Exported
public class UnixLoginModule implements LoginModule {

    // initial state
    private Subject subject;
    private CallbackHandler callbackHandler;
    private Map<String, ?> sharedState;
    private Map<String, ?> options;

    // configurable option
    private boolean debug = true;

    // UnixSystem to retrieve underlying system info
    private UnixSystem ss;

    // the authentication status
    private boolean succeeded = false;
    private boolean commitSucceeded = false;

    // Underlying system info
    private UnixPrincipal userPrincipal;
    private UnixNumericUserPrincipal UIDPrincipal;
    private UnixNumericGroupPrincipal GIDPrincipal;
    private LinkedList<UnixNumericGroupPrincipal> supplementaryGroups =
                new LinkedList<>();

    /**
     * Initialize this <code>LoginModule</code>.
     *
     * <p>
     *
     * @param subject the <code>Subject</code> to be authenticated. <p>
     *
     * @param callbackHandler a <code>CallbackHandler</code> for communicating
     *                  with the end user (prompting for usernames and
     *                  passwords, for example). <p>
     *
     * @param sharedState shared <code>LoginModule</code> state. <p>
     *
     * @param options options specified in the login
     *                  <code>Configuration</code> for this particular
     *                  <code>LoginModule</code>.
     */
    public void initialize(Subject subject, CallbackHandler callbackHandler,
                           Map<String,?> sharedState,
                           Map<String,?> options) {

        this.subject = subject;
        this.callbackHandler = callbackHandler;
        this.sharedState = sharedState;
        this.options = options;

        // initialize any configured options
        debug = "true".equalsIgnoreCase((String)options.get("debug"));
    }

    /**
     * Authenticate the user (first phase).
     *
     * <p> The implementation of this method attempts to retrieve the user's
     * Unix <code>Subject</code> information by making a native Unix
     * system call.
     *
     * <p>
     *
     * @exception FailedLoginException if attempts to retrieve the underlying
     *          system information fail.
     *
     * @return true in all cases (this <code>LoginModule</code>
     *          should not be ignored).
     */
    public boolean login() throws LoginException {

        long[] unixGroups = null;

        ss = new UnixSystem();

        if (ss == null) {
            succeeded = false;
            throw new FailedLoginException
                                ("Failed in attempt to import " +
                                "the underlying system identity information");
        } else {
            userPrincipal = new UnixPrincipal(ss.getUsername());
            UIDPrincipal = new UnixNumericUserPrincipal(ss.getUid());
            GIDPrincipal = new UnixNumericGroupPrincipal(ss.getGid(), true);
            if (ss.getGroups() != null && ss.getGroups().length > 0) {
                unixGroups = ss.getGroups();
                for (int i = 0; i < unixGroups.length; i++) {
                    UnixNumericGroupPrincipal ngp =
                        new UnixNumericGroupPrincipal
                        (unixGroups[i], false);
                    if (!ngp.getName().equals(GIDPrincipal.getName()))
                        supplementaryGroups.add(ngp);
                }
            }
            if (debug) {
                System.out.println("\t\t[UnixLoginModule]: " +
                        "succeeded importing info: ");
                System.out.println("\t\t\tuid = " + ss.getUid());
                System.out.println("\t\t\tgid = " + ss.getGid());
                unixGroups = ss.getGroups();
                for (int i = 0; i < unixGroups.length; i++) {
                    System.out.println("\t\t\tsupp gid = " + unixGroups[i]);
                }
            }
            succeeded = true;
            return true;
        }
    }

    /**
     * Commit the authentication (second phase).
     *
     * <p> This method is called if the LoginContext's
     * overall authentication succeeded
     * (the relevant REQUIRED, REQUISITE, SUFFICIENT and OPTIONAL LoginModules
     * succeeded).
     *
     * <p> If this LoginModule's own authentication attempt
     * succeeded (the importing of the Unix authentication information
     * succeeded), then this method associates the Unix Principals
     * with the <code>Subject</code> currently tied to the
     * <code>LoginModule</code>.  If this LoginModule's
     * authentication attempted failed, then this method removes
     * any state that was originally saved.
     *
     * <p>
     *
     * @exception LoginException if the commit fails
     *
     * @return true if this LoginModule's own login and commit attempts
     *          succeeded, or false otherwise.
     */
    public boolean commit() throws LoginException {
        if (succeeded == false) {
            if (debug) {
                System.out.println("\t\t[UnixLoginModule]: " +
                    "did not add any Principals to Subject " +
                    "because own authentication failed.");
            }
            return false;
        } else {
            if (subject.isReadOnly()) {
                throw new LoginException
                    ("commit Failed: Subject is Readonly");
            }
            if (!subject.getPrincipals().contains(userPrincipal))
                subject.getPrincipals().add(userPrincipal);
            if (!subject.getPrincipals().contains(UIDPrincipal))
                subject.getPrincipals().add(UIDPrincipal);
            if (!subject.getPrincipals().contains(GIDPrincipal))
                subject.getPrincipals().add(GIDPrincipal);
            for (int i = 0; i < supplementaryGroups.size(); i++) {
                if (!subject.getPrincipals().contains
                    (supplementaryGroups.get(i)))
                    subject.getPrincipals().add(supplementaryGroups.get(i));
            }

            if (debug) {
                System.out.println("\t\t[UnixLoginModule]: " +
                    "added UnixPrincipal,");
                System.out.println("\t\t\t\tUnixNumericUserPrincipal,");
                System.out.println("\t\t\t\tUnixNumericGroupPrincipal(s),");
                System.out.println("\t\t\t to Subject");
            }

            commitSucceeded = true;
            return true;
        }
    }

    /**
     * Abort the authentication (second phase).
     *
     * <p> This method is called if the LoginContext's
     * overall authentication failed.
     * (the relevant REQUIRED, REQUISITE, SUFFICIENT and OPTIONAL LoginModules
     * did not succeed).
     *
     * <p> This method cleans up any state that was originally saved
     * as part of the authentication attempt from the <code>login</code>
     * and <code>commit</code> methods.
     *
     * <p>
     *
     * @exception LoginException if the abort fails
     *
     * @return false if this LoginModule's own login and/or commit attempts
     *          failed, and true otherwise.
     */
    public boolean abort() throws LoginException {
        if (debug) {
            System.out.println("\t\t[UnixLoginModule]: " +
                "aborted authentication attempt");
        }

        if (succeeded == false) {
            return false;
        } else if (succeeded == true && commitSucceeded == false) {

            // Clean out state
            succeeded = false;
            ss = null;
            userPrincipal = null;
            UIDPrincipal = null;
            GIDPrincipal = null;
            supplementaryGroups = new LinkedList<UnixNumericGroupPrincipal>();
        } else {
            // overall authentication succeeded and commit succeeded,
            // but someone else's commit failed
            logout();
        }
        return true;
    }

    /**
     * Logout the user
     *
     * <p> This method removes the Principals associated
     * with the <code>Subject</code>.
     *
     * <p>
     *
     * @exception LoginException if the logout fails
     *
     * @return true in all cases (this <code>LoginModule</code>
     *          should not be ignored).
     */
    public boolean logout() throws LoginException {

        if (subject.isReadOnly()) {
                throw new LoginException
                    ("logout Failed: Subject is Readonly");
            }
        // remove the added Principals from the Subject
        subject.getPrincipals().remove(userPrincipal);
        subject.getPrincipals().remove(UIDPrincipal);
        subject.getPrincipals().remove(GIDPrincipal);
        for (int i = 0; i < supplementaryGroups.size(); i++) {
            subject.getPrincipals().remove(supplementaryGroups.get(i));
        }

        // clean out state
        ss = null;
        succeeded = false;
        commitSucceeded = false;
        userPrincipal = null;
        UIDPrincipal = null;
        GIDPrincipal = null;
        supplementaryGroups = new LinkedList<UnixNumericGroupPrincipal>();

        if (debug) {
            System.out.println("\t\t[UnixLoginModule]: " +
                "logged out Subject");
        }
        return true;
    }
}
