/*
 * Copyright (c) 1999, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java8.javax.naming;

import java.util.Hashtable;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.NoInitialContextException;
import javax.naming.spi.NamingManager;
import com.sun.naming.internal.ResourceManager;

/**
 * This class is the starting context for performing naming operations.
 *<p>
 * All naming operations are relative to a context.
 * The initial context implements the Context interface and
 * provides the starting point for resolution of names.
 *<p>
 * <a name=ENVIRONMENT></a>
 * When the initial context is constructed, its environment
 * is initialized with properties defined in the environment parameter
 * passed to the constructor, and in any
 * <a href=Context.html#RESOURCEFILES>application resource files</a>.
 * In addition, a small number of standard JNDI properties may
 * be specified as system properties or as applet parameters
 * (through the use of {@link Context#APPLET}).
 * These special properties are listed in the field detail sections of the
 * <a href=Context.html#field_detail><tt>Context</tt></a> and
 * <a href=ldap/LdapContext.html#field_detail><tt>LdapContext</tt></a>
 * interface documentation.
 *<p>
 * JNDI determines each property's value by merging
 * the values from the following two sources, in order:
 * <ol>
 * <li>
 * The first occurrence of the property from the constructor's
 * environment parameter and (for appropriate properties) the applet
 * parameters and system properties.
 * <li>
 * The application resource files (<tt>jndi.properties</tt>).
 * </ol>
 * For each property found in both of these two sources, or in
 * more than one application resource file, the property's value
 * is determined as follows.  If the property is
 * one of the standard JNDI properties that specify a list of JNDI
 * factories (see <a href=Context.html#LISTPROPS><tt>Context</tt></a>),
 * all of the values are
 * concatenated into a single colon-separated list.  For other
 * properties, only the first value found is used.
 *
 *<p>
 * The initial context implementation is determined at runtime.
 * The default policy uses the environment property
 * "{@link Context#INITIAL_CONTEXT_FACTORY java.naming.factory.initial}",
 * which contains the class name of the initial context factory.
 * An exception to this policy is made when resolving URL strings, as described
 * below.
 *<p>
 * When a URL string (a <tt>String</tt> of the form
 * <em>scheme_id:rest_of_name</em>) is passed as a name parameter to
 * any method, a URL context factory for handling that scheme is
 * located and used to resolve the URL.  If no such factory is found,
 * the initial context specified by
 * <tt>"java.naming.factory.initial"</tt> is used.  Similarly, when a
 * <tt>CompositeName</tt> object whose first component is a URL string is
 * passed as a name parameter to any method, a URL context factory is
 * located and used to resolve the first name component.
 * See {@link NamingManager#getURLContext
 * <tt>NamingManager.getURLContext()</tt>} for a description of how URL
 * context factories are located.
 *<p>
 * This default policy of locating the initial context and URL context
 * factories may be overridden
 * by calling
 * <tt>NamingManager.setInitialContextFactoryBuilder()</tt>.
 *<p>
 * NoInitialContextException is thrown when an initial context cannot
 * be instantiated. This exception can be thrown during any interaction
 * with the InitialContext, not only when the InitialContext is constructed.
 * For example, the implementation of the initial context might lazily
 * retrieve the context only when actual methods are invoked on it.
 * The application should not have any dependency on when the existence
 * of an initial context is determined.
 *<p>
 * When the environment property "java.naming.factory.initial" is
 * non-null, the InitialContext constructor will attempt to create the
 * initial context specified therein. At that time, the initial context factory
 * involved might throw an exception if a problem is encountered. However,
 * it is provider implementation-dependent when it verifies and indicates
 * to the users of the initial context any environment property- or
 * connection- related problems. It can do so lazily--delaying until
 * an operation is performed on the context, or eagerly, at the time
 * the context is constructed.
 *<p>
 * An InitialContext instance is not synchronized against concurrent
 * access by multiple threads. Multiple threads each manipulating a
 * different InitialContext instance need not synchronize.
 * Threads that need to access a single InitialContext instance
 * concurrently should synchronize amongst themselves and provide the
 * necessary locking.
 *
 * @author Rosanna Lee
 * @author Scott Seligman
 *
 * @see Context
 * @see NamingManager#setInitialContextFactoryBuilder
 *      NamingManager.setInitialContextFactoryBuilder
 * @since JNDI 1.1 / Java 2 Platform, Standard Edition, v 1.3
 */

public class InitialContext implements Context {

    /**
     * The environment associated with this InitialContext.
     * It is initialized to null and is updated by the constructor
     * that accepts an environment or by the <tt>init()</tt> method.
     * @see #addToEnvironment
     * @see #removeFromEnvironment
     * @see #getEnvironment
     */
    protected Hashtable<Object,Object> myProps = null;

    /**
     * Field holding the result of calling NamingManager.getInitialContext().
     * It is set by getDefaultInitCtx() the first time getDefaultInitCtx()
     * is called. Subsequent invocations of getDefaultInitCtx() return
     * the value of defaultInitCtx.
     * @see #getDefaultInitCtx
     */
    protected Context defaultInitCtx = null;

    /**
     * Field indicating whether the initial context has been obtained
     * by calling NamingManager.getInitialContext().
     * If true, its result is in <code>defaultInitCtx</code>.
     */
    protected boolean gotDefault = false;

    /**
     * Constructs an initial context with the option of not
     * initializing it.  This may be used by a constructor in
     * a subclass when the value of the environment parameter
     * is not yet known at the time the <tt>InitialContext</tt>
     * constructor is called.  The subclass's constructor will
     * call this constructor, compute the value of the environment,
     * and then call <tt>init()</tt> before returning.
     *
     * @param lazy
     *          true means do not initialize the initial context; false
     *          is equivalent to calling <tt>new InitialContext()</tt>
     * @throws  NamingException if a naming exception is encountered
     *
     * @see #init(Hashtable)
     * @since 1.3
     */
    protected InitialContext(boolean lazy) throws NamingException {
        if (!lazy) {
            init(null);
        }
    }

    /**
     * Constructs an initial context.
     * No environment properties are supplied.
     * Equivalent to <tt>new InitialContext(null)</tt>.
     *
     * @throws  NamingException if a naming exception is encountered
     *
     * @see #InitialContext(Hashtable)
     */
    public InitialContext() throws NamingException {
        init(null);
    }

    /**
     * Constructs an initial context using the supplied environment.
     * Environment properties are discussed in the class description.
     *
     * <p> This constructor will not modify <tt>environment</tt>
     * or save a reference to it, but may save a clone.
     * Caller should not modify mutable keys and values in
     * <tt>environment</tt> after it has been passed to the constructor.
     *
     * @param environment
     *          environment used to create the initial context.
     *          Null indicates an empty environment.
     *
     * @throws  NamingException if a naming exception is encountered
     */
    public InitialContext(Hashtable<?,?> environment)
        throws NamingException
    {
        if (environment != null) {
            environment = (Hashtable)environment.clone();
        }
        init(environment);
    }

    /**
     * Initializes the initial context using the supplied environment.
     * Environment properties are discussed in the class description.
     *
     * <p> This method will modify <tt>environment</tt> and save
     * a reference to it.  The caller may no longer modify it.
     *
     * @param environment
     *          environment used to create the initial context.
     *          Null indicates an empty environment.
     *
     * @throws  NamingException if a naming exception is encountered
     *
     * @see #InitialContext(boolean)
     * @since 1.3
     */
    @SuppressWarnings("unchecked")
    protected void init(Hashtable<?,?> environment)
        throws NamingException
    {
        myProps = (Hashtable<Object,Object>)
                ResourceManager.getInitialEnvironment(environment);

        if (myProps.get(Context.INITIAL_CONTEXT_FACTORY) != null) {
            // user has specified initial context factory; try to get it
            getDefaultInitCtx();
        }
    }

    /**
     * A static method to retrieve the named object.
     * This is a shortcut method equivalent to invoking:
     * <p>
     * <code>
     *        InitialContext ic = new InitialContext();
     *        Object obj = ic.lookup();
     * </code>
     * <p> If <tt>name</tt> is empty, returns a new instance of this context
     * (which represents the same naming context as this context, but its
     * environment may be modified independently and it may be accessed
     * concurrently).
     *
     * @param <T> the type of the returned object
     * @param name
     *          the name of the object to look up
     * @return  the object bound to <tt>name</tt>
     * @throws  NamingException if a naming exception is encountered
     *
     * @see #doLookup(String)
     * @see #lookup(Name)
     * @since 1.6
     */
    @SuppressWarnings("unchecked")
    public static <T> T doLookup(Name name)
        throws NamingException {
        return (T) (new javax.naming.InitialContext()).lookup(name);
    }

   /**
     * A static method to retrieve the named object.
     * See {@link #doLookup(Name)} for details.
     * @param <T> the type of the returned object
     * @param name
     *          the name of the object to look up
     * @return  the object bound to <tt>name</tt>
     * @throws  NamingException if a naming exception is encountered
     * @since 1.6
     */
    @SuppressWarnings("unchecked")
    public static <T> T doLookup(String name)
        throws NamingException {
        return (T) (new javax.naming.InitialContext()).lookup(name);
    }

    private static String getURLScheme(String str) {
        int colon_posn = str.indexOf(':');
        int slash_posn = str.indexOf('/');

        if (colon_posn > 0 && (slash_posn == -1 || colon_posn < slash_posn))
            return str.substring(0, colon_posn);
        return null;
    }

    /**
     * Retrieves the initial context by calling
     * <code>NamingManager.getInitialContext()</code>
     * and cache it in defaultInitCtx.
     * Set <code>gotDefault</code> so that we know we've tried this before.
     * @return The non-null cached initial context.
     * @exception NoInitialContextException If cannot find an initial context.
     * @exception NamingException If a naming exception was encountered.
     */
    protected Context getDefaultInitCtx() throws NamingException{
        if (!gotDefault) {
            defaultInitCtx = NamingManager.getInitialContext(myProps);
            gotDefault = true;
        }
        if (defaultInitCtx == null)
            throw new NoInitialContextException();

        return defaultInitCtx;
    }

    /**
     * Retrieves a context for resolving the string name <code>name</code>.
     * If <code>name</code> name is a URL string, then attempt
     * to find a URL context for it. If none is found, or if
     * <code>name</code> is not a URL string, then return
     * <code>getDefaultInitCtx()</code>.
     *<p>
     * See getURLOrDefaultInitCtx(Name) for description
     * of how a subclass should use this method.
     * @param name The non-null name for which to get the context.
     * @return A URL context for <code>name</code> or the cached
     *         initial context. The result cannot be null.
     * @exception NoInitialContextException If cannot find an initial context.
     * @exception NamingException In a naming exception is encountered.
     * @see NamingManager#getURLContext
     */
    protected Context getURLOrDefaultInitCtx(String name)
        throws NamingException {
        if (NamingManager.hasInitialContextFactoryBuilder()) {
            return getDefaultInitCtx();
        }
        String scheme = getURLScheme(name);
        if (scheme != null) {
            Context ctx = NamingManager.getURLContext(scheme, myProps);
            if (ctx != null) {
                return ctx;
            }
        }
        return getDefaultInitCtx();
    }

    /**
     * Retrieves a context for resolving <code>name</code>.
     * If the first component of <code>name</code> name is a URL string,
     * then attempt to find a URL context for it. If none is found, or if
     * the first component of <code>name</code> is not a URL string,
     * then return <code>getDefaultInitCtx()</code>.
     *<p>
     * When creating a subclass of InitialContext, use this method as
     * follows.
     * Define a new method that uses this method to get an initial
     * context of the desired subclass.
     * <blockquote><pre>
     * protected XXXContext getURLOrDefaultInitXXXCtx(Name name)
     * throws NamingException {
     *  Context answer = getURLOrDefaultInitCtx(name);
     *  if (!(answer instanceof XXXContext)) {
     *    if (answer == null) {
     *      throw new NoInitialContextException();
     *    } else {
     *      throw new NotContextException("Not an XXXContext");
     *    }
     *  }
     *  return (XXXContext)answer;
     * }
     * </pre></blockquote>
     * When providing implementations for the new methods in the subclass,
     * use this newly defined method to get the initial context.
     * <blockquote><pre>
     * public Object XXXMethod1(Name name, ...) {
     *  throws NamingException {
     *    return getURLOrDefaultInitXXXCtx(name).XXXMethod1(name, ...);
     * }
     * </pre></blockquote>
     *
     * @param name The non-null name for which to get the context.
     * @return A URL context for <code>name</code> or the cached
     *         initial context. The result cannot be null.
     * @exception NoInitialContextException If cannot find an initial context.
     * @exception NamingException In a naming exception is encountered.
     *
     * @see NamingManager#getURLContext
     */
    protected Context getURLOrDefaultInitCtx(Name name)
        throws NamingException {
        if (NamingManager.hasInitialContextFactoryBuilder()) {
            return getDefaultInitCtx();
        }
        if (name.size() > 0) {
            String first = name.get(0);
            String scheme = getURLScheme(first);
            if (scheme != null) {
                Context ctx = NamingManager.getURLContext(scheme, myProps);
                if (ctx != null) {
                    return ctx;
                }
            }
        }
        return getDefaultInitCtx();
    }

// Context methods
// Most Javadoc is deferred to the Context interface.

    public Object lookup(String name) throws NamingException {
        return getURLOrDefaultInitCtx(name).lookup(name);
    }

    public Object lookup(Name name) throws NamingException {
        return getURLOrDefaultInitCtx(name).lookup(name);
    }

    public void bind(String name, Object obj) throws NamingException {
        getURLOrDefaultInitCtx(name).bind(name, obj);
    }

    public void bind(Name name, Object obj) throws NamingException {
        getURLOrDefaultInitCtx(name).bind(name, obj);
    }

    public void rebind(String name, Object obj) throws NamingException {
        getURLOrDefaultInitCtx(name).rebind(name, obj);
    }

    public void rebind(Name name, Object obj) throws NamingException {
        getURLOrDefaultInitCtx(name).rebind(name, obj);
    }

    public void unbind(String name) throws NamingException  {
        getURLOrDefaultInitCtx(name).unbind(name);
    }

    public void unbind(Name name) throws NamingException  {
        getURLOrDefaultInitCtx(name).unbind(name);
    }

    public void rename(String oldName, String newName) throws NamingException {
        getURLOrDefaultInitCtx(oldName).rename(oldName, newName);
    }

    public void rename(Name oldName, Name newName)
        throws NamingException
    {
        getURLOrDefaultInitCtx(oldName).rename(oldName, newName);
    }

    public NamingEnumeration<NameClassPair> list(String name)
        throws NamingException
    {
        return (getURLOrDefaultInitCtx(name).list(name));
    }

    public NamingEnumeration<NameClassPair> list(Name name)
        throws NamingException
    {
        return (getURLOrDefaultInitCtx(name).list(name));
    }

    public NamingEnumeration<Binding> listBindings(String name)
            throws NamingException  {
        return getURLOrDefaultInitCtx(name).listBindings(name);
    }

    public NamingEnumeration<Binding> listBindings(Name name)
            throws NamingException  {
        return getURLOrDefaultInitCtx(name).listBindings(name);
    }

    public void destroySubcontext(String name) throws NamingException  {
        getURLOrDefaultInitCtx(name).destroySubcontext(name);
    }

    public void destroySubcontext(Name name) throws NamingException  {
        getURLOrDefaultInitCtx(name).destroySubcontext(name);
    }

    public Context createSubcontext(String name) throws NamingException  {
        return getURLOrDefaultInitCtx(name).createSubcontext(name);
    }

    public Context createSubcontext(Name name) throws NamingException  {
        return getURLOrDefaultInitCtx(name).createSubcontext(name);
    }

    public Object lookupLink(String name) throws NamingException  {
        return getURLOrDefaultInitCtx(name).lookupLink(name);
    }

    public Object lookupLink(Name name) throws NamingException {
        return getURLOrDefaultInitCtx(name).lookupLink(name);
    }

    public NameParser getNameParser(String name) throws NamingException {
        return getURLOrDefaultInitCtx(name).getNameParser(name);
    }

    public NameParser getNameParser(Name name) throws NamingException {
        return getURLOrDefaultInitCtx(name).getNameParser(name);
    }

    /**
     * Composes the name of this context with a name relative to
     * this context.
     * Since an initial context may never be named relative
     * to any context other than itself, the value of the
     * <tt>prefix</tt> parameter must be an empty name (<tt>""</tt>).
     */
    public String composeName(String name, String prefix)
            throws NamingException {
        return name;
    }

    /**
     * Composes the name of this context with a name relative to
     * this context.
     * Since an initial context may never be named relative
     * to any context other than itself, the value of the
     * <tt>prefix</tt> parameter must be an empty name.
     */
    public Name composeName(Name name, Name prefix)
        throws NamingException
    {
        return (Name)name.clone();
    }

    public Object addToEnvironment(String propName, Object propVal)
            throws NamingException {
        myProps.put(propName, propVal);
        return getDefaultInitCtx().addToEnvironment(propName, propVal);
    }

    public Object removeFromEnvironment(String propName)
            throws NamingException {
        myProps.remove(propName);
        return getDefaultInitCtx().removeFromEnvironment(propName);
    }

    public Hashtable<?,?> getEnvironment() throws NamingException {
        return getDefaultInitCtx().getEnvironment();
    }

    public void close() throws NamingException {
        myProps = null;
        if (defaultInitCtx != null) {
            defaultInitCtx.close();
            defaultInitCtx = null;
        }
        gotDefault = false;
    }

    public String getNameInNamespace() throws NamingException {
        return getDefaultInitCtx().getNameInNamespace();
    }
};
