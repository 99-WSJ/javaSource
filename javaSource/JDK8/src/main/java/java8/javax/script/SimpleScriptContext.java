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

package java8.javax.script;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.SimpleBindings;
import java.util.*;
import java.io.*;

/**
 * Simple implementation of ScriptContext.
 *
 * @author Mike Grogan
 * @since 1.6
 */
public class SimpleScriptContext  implements ScriptContext {

    /**
     * This is the writer to be used to output from scripts.
     * By default, a <code>PrintWriter</code> based on <code>System.out</code>
     * is used. Accessor methods getWriter, setWriter are used to manage
     * this field.
     * @see System#out
     * @see PrintWriter
     */
    protected Writer writer;

    /**
     * This is the writer to be used to output errors from scripts.
     * By default, a <code>PrintWriter</code> based on <code>System.err</code> is
     * used. Accessor methods getErrorWriter, setErrorWriter are used to manage
     * this field.
     * @see System#err
     * @see PrintWriter
     */
    protected Writer errorWriter;

    /**
     * This is the reader to be used for input from scripts.
     * By default, a <code>InputStreamReader</code> based on <code>System.in</code>
     * is used and default charset is used by this reader. Accessor methods
     * getReader, setReader are used to manage this field.
     * @see System#in
     * @see InputStreamReader
     */
    protected Reader reader;


    /**
     * This is the engine scope bindings.
     * By default, a <code>SimpleBindings</code> is used. Accessor
     * methods setBindings, getBindings are used to manage this field.
     * @see SimpleBindings
     */
    protected Bindings engineScope;

    /**
     * This is the global scope bindings.
     * By default, a null value (which means no global scope) is used. Accessor
     * methods setBindings, getBindings are used to manage this field.
     */
    protected Bindings globalScope;

    /**
     * Create a {@code SimpleScriptContext}.
     */
    public SimpleScriptContext() {
        engineScope = new SimpleBindings();
        globalScope = null;
        reader = new InputStreamReader(System.in);
        writer = new PrintWriter(System.out , true);
        errorWriter = new PrintWriter(System.err, true);
    }

    /**
     * Sets a <code>Bindings</code> of attributes for the given scope.  If the value
     * of scope is <code>ENGINE_SCOPE</code> the given <code>Bindings</code> replaces the
     * <code>engineScope</code> field.  If the value
     * of scope is <code>GLOBAL_SCOPE</code> the given <code>Bindings</code> replaces the
     * <code>globalScope</code> field.
     *
     * @param bindings The <code>Bindings</code> of attributes to set.
     * @param scope The value of the scope in which the attributes are set.
     *
     * @throws IllegalArgumentException if scope is invalid.
     * @throws NullPointerException if the value of scope is <code>ENGINE_SCOPE</code> and
     * the specified <code>Bindings</code> is null.
     */
    public void setBindings(Bindings bindings, int scope) {

        switch (scope) {

            case ENGINE_SCOPE:
                if (bindings == null) {
                    throw new NullPointerException("Engine scope cannot be null.");
                }
                engineScope = bindings;
                break;
            case GLOBAL_SCOPE:
                globalScope = bindings;
                break;
            default:
                throw new IllegalArgumentException("Invalid scope value.");
        }
    }


    /**
     * Retrieves the value of the attribute with the given name in
     * the scope occurring earliest in the search order.  The order
     * is determined by the numeric value of the scope parameter (lowest
     * scope values first.)
     *
     * @param name The name of the the attribute to retrieve.
     * @return The value of the attribute in the lowest scope for
     * which an attribute with the given name is defined.  Returns
     * null if no attribute with the name exists in any scope.
     * @throws NullPointerException if the name is null.
     * @throws IllegalArgumentException if the name is empty.
     */
    public Object getAttribute(String name) {
        if (engineScope.containsKey(name)) {
            return getAttribute(name, ENGINE_SCOPE);
        } else if (globalScope != null && globalScope.containsKey(name)) {
            return getAttribute(name, GLOBAL_SCOPE);
        }

        return null;
    }

    /**
     * Gets the value of an attribute in a given scope.
     *
     * @param name The name of the attribute to retrieve.
     * @param scope The scope in which to retrieve the attribute.
     * @return The value of the attribute. Returns <code>null</code> is the name
     * does not exist in the given scope.
     *
     * @throws IllegalArgumentException
     *         if the name is empty or if the value of scope is invalid.
     * @throws NullPointerException if the name is null.
     */
    public Object getAttribute(String name, int scope) {

        switch (scope) {

            case ENGINE_SCOPE:
                return engineScope.get(name);

            case GLOBAL_SCOPE:
                if (globalScope != null) {
                    return globalScope.get(name);
                }
                return null;

            default:
                throw new IllegalArgumentException("Illegal scope value.");
        }
    }

    /**
     * Remove an attribute in a given scope.
     *
     * @param name The name of the attribute to remove
     * @param scope The scope in which to remove the attribute
     *
     * @return The removed value.
     * @throws IllegalArgumentException
     *         if the name is empty or if the scope is invalid.
     * @throws NullPointerException if the name is null.
     */
    public Object removeAttribute(String name, int scope) {

        switch (scope) {

            case ENGINE_SCOPE:
                if (getBindings(ENGINE_SCOPE) != null) {
                    return getBindings(ENGINE_SCOPE).remove(name);
                }
                return null;

            case GLOBAL_SCOPE:
                if (getBindings(GLOBAL_SCOPE) != null) {
                    return getBindings(GLOBAL_SCOPE).remove(name);
                }
                return null;

            default:
                throw new IllegalArgumentException("Illegal scope value.");
        }
    }

    /**
     * Sets the value of an attribute in a given scope.
     *
     * @param name The name of the attribute to set
     * @param value The value of the attribute
     * @param scope The scope in which to set the attribute
     *
     * @throws IllegalArgumentException
     *         if the name is empty or if the scope is invalid.
     * @throws NullPointerException if the name is null.
     */
    public void setAttribute(String name, Object value, int scope) {

        switch (scope) {

            case ENGINE_SCOPE:
                engineScope.put(name, value);
                return;

            case GLOBAL_SCOPE:
                if (globalScope != null) {
                    globalScope.put(name, value);
                }
                return;

            default:
                throw new IllegalArgumentException("Illegal scope value.");
        }
    }

    /** {@inheritDoc} */
    public Writer getWriter() {
        return writer;
    }

    /** {@inheritDoc} */
    public Reader getReader() {
        return reader;
    }

    /** {@inheritDoc} */
    public void setReader(Reader reader) {
        this.reader = reader;
    }

    /** {@inheritDoc} */
    public void setWriter(Writer writer) {
        this.writer = writer;
    }

    /** {@inheritDoc} */
    public Writer getErrorWriter() {
        return errorWriter;
    }

    /** {@inheritDoc} */
    public void setErrorWriter(Writer writer) {
        this.errorWriter = writer;
    }

    /**
     * Get the lowest scope in which an attribute is defined.
     * @param name Name of the attribute
     * .
     * @return The lowest scope.  Returns -1 if no attribute with the given
     * name is defined in any scope.
     * @throws NullPointerException if name is null.
     * @throws IllegalArgumentException if name is empty.
     */
    public int getAttributesScope(String name) {
        if (engineScope.containsKey(name)) {
            return ENGINE_SCOPE;
        } else if (globalScope != null && globalScope.containsKey(name)) {
            return GLOBAL_SCOPE;
        } else {
            return -1;
        }
    }

    /**
     * Returns the value of the <code>engineScope</code> field if specified scope is
     * <code>ENGINE_SCOPE</code>.  Returns the value of the <code>globalScope</code> field if the specified scope is
     * <code>GLOBAL_SCOPE</code>.
     *
     * @param scope The specified scope
     * @return The value of either the  <code>engineScope</code> or <code>globalScope</code> field.
     * @throws IllegalArgumentException if the value of scope is invalid.
     */
    public Bindings getBindings(int scope) {
        if (scope == ENGINE_SCOPE) {
            return engineScope;
        } else if (scope == GLOBAL_SCOPE) {
            return globalScope;
        } else {
            throw new IllegalArgumentException("Illegal scope value.");
        }
    }

    /** {@inheritDoc} */
    public List<Integer> getScopes() {
        return scopes;
    }

    private static List<Integer> scopes;
    static {
        scopes = new ArrayList<Integer>(2);
        scopes.add(ENGINE_SCOPE);
        scopes.add(GLOBAL_SCOPE);
        scopes = Collections.unmodifiableList(scopes);
    }
}
