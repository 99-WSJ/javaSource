/*
 * Copyright (c) 2005, 2010, Oracle and/or its affiliates. All rights reserved.
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
package java8.java.lang;

import java.util.Collection;
import java.util.IdentityHashMap;

/*
 * Class to track and run user level shutdown hooks registered through
 * <tt>{@link Runtime#addShutdownHook Runtime.addShutdownHook}</tt>.
 *
 * @see java.lang.Runtime#addShutdownHook
 * @see java.lang.Runtime#removeShutdownHook
 */

class ApplicationShutdownHooks {
    /* The set of registered hooks */
    private static IdentityHashMap<java.lang.Thread, java.lang.Thread> hooks;
    static {
        try {
            java.lang.Shutdown.add(1 /* shutdown hook invocation order */,
                false /* not registered if shutdown in progress */,
                new java.lang.Runnable() {
                    public void run() {
                        runHooks();
                    }
                }
            );
            hooks = new IdentityHashMap<>();
        } catch (java.lang.IllegalStateException e) {
            // application shutdown hooks cannot be added if
            // shutdown is in progress.
            hooks = null;
        }
    }


    private ApplicationShutdownHooks() {}

    /* Add a new shutdown hook.  Checks the shutdown state and the hook itself,
     * but does not do any security checks.
     */
    static synchronized void add(java.lang.Thread hook) {
        if(hooks == null)
            throw new java.lang.IllegalStateException("Shutdown in progress");

        if (hook.isAlive())
            throw new java.lang.IllegalArgumentException("Hook already running");

        if (hooks.containsKey(hook))
            throw new java.lang.IllegalArgumentException("Hook previously registered");

        hooks.put(hook, hook);
    }

    /* Remove a previously-registered hook.  Like the add method, this method
     * does not do any security checks.
     */
    static synchronized boolean remove(java.lang.Thread hook) {
        if(hooks == null)
            throw new java.lang.IllegalStateException("Shutdown in progress");

        if (hook == null)
            throw new NullPointerException();

        return hooks.remove(hook) != null;
    }

    /* Iterates over all application hooks creating a new thread for each
     * to run in. Hooks are run concurrently and this method waits for
     * them to finish.
     */
    static void runHooks() {
        Collection<java.lang.Thread> threads;
        synchronized(java.lang.ApplicationShutdownHooks.class) {
            threads = hooks.keySet();
            hooks = null;
        }

        for (java.lang.Thread hook : threads) {
            hook.start();
        }
        for (java.lang.Thread hook : threads) {
            try {
                hook.join();
            } catch (java.lang.InterruptedException x) { }
        }
    }
}
