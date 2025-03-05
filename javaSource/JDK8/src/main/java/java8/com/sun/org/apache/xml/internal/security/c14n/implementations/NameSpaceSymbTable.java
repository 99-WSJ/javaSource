/*
 * Copyright (c) 2007-2013, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package java8.sun.org.apache.xml.internal.security.c14n.implementations;

import org.w3c.dom.Attr;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * A stack based Symbol Table.
 *<br>For speed reasons all the symbols are introduced in the same map,
 * and at the same time in a list so it can be removed when the frame is pop back.
 * @author Raul Benito
 */
public class NameSpaceSymbTable {

    private static final String XMLNS = "xmlns";
    private static final com.sun.org.apache.xml.internal.security.c14n.implementations.SymbMap initialMap = new com.sun.org.apache.xml.internal.security.c14n.implementations.SymbMap();

    static {
        com.sun.org.apache.xml.internal.security.c14n.implementations.NameSpaceSymbEntry ne = new com.sun.org.apache.xml.internal.security.c14n.implementations.NameSpaceSymbEntry("", null, true, XMLNS);
        ne.lastrendered = "";
        initialMap.put(XMLNS, ne);
    }

    /**The map betwen prefix-> entry table. */
    private com.sun.org.apache.xml.internal.security.c14n.implementations.SymbMap symb;

    /**The stacks for removing the definitions when doing pop.*/
    private List<com.sun.org.apache.xml.internal.security.c14n.implementations.SymbMap> level;
    private boolean cloned = true;

    /**
     * Default constractor
     **/
    public NameSpaceSymbTable() {
        level = new ArrayList<com.sun.org.apache.xml.internal.security.c14n.implementations.SymbMap>();
        //Insert the default binding for xmlns.
        symb = (com.sun.org.apache.xml.internal.security.c14n.implementations.SymbMap) initialMap.clone();
    }

    /**
     * Get all the unrendered nodes in the name space.
     * For Inclusive rendering
     * @param result the list where to fill the unrendered xmlns definitions.
     **/
    public void getUnrenderedNodes(Collection<Attr> result) {
        Iterator<com.sun.org.apache.xml.internal.security.c14n.implementations.NameSpaceSymbEntry> it = symb.entrySet().iterator();
        while (it.hasNext()) {
            com.sun.org.apache.xml.internal.security.c14n.implementations.NameSpaceSymbEntry n = it.next();
            //put them rendered?
            if ((!n.rendered) && (n.n != null)) {
                n = (com.sun.org.apache.xml.internal.security.c14n.implementations.NameSpaceSymbEntry) n.clone();
                needsClone();
                symb.put(n.prefix, n);
                n.lastrendered = n.uri;
                n.rendered = true;

                result.add(n.n);
            }
        }
    }

    /**
     * Push a frame for visible namespace.
     * For Inclusive rendering.
     **/
    public void outputNodePush() {
        push();
    }

    /**
     * Pop a frame for visible namespace.
     **/
    public void outputNodePop() {
        pop();
    }

    /**
     * Push a frame for a node.
     * Inclusive or Exclusive.
     **/
    public void push() {
        //Put the number of namespace definitions in the stack.
        level.add(null);
        cloned = false;
    }

    /**
     * Pop a frame.
     * Inclusive or Exclusive.
     **/
    public void pop() {
        int size = level.size() - 1;
        Object ob = level.remove(size);
        if (ob != null) {
            symb = (com.sun.org.apache.xml.internal.security.c14n.implementations.SymbMap)ob;
            if (size == 0) {
                cloned = false;
            } else {
                cloned = (level.get(size - 1) != symb);
            }
        } else {
            cloned = false;
        }
    }

    final void needsClone() {
        if (!cloned) {
            level.set(level.size() - 1, symb);
            symb = (com.sun.org.apache.xml.internal.security.c14n.implementations.SymbMap) symb.clone();
            cloned = true;
        }
    }


    /**
     * Gets the attribute node that defines the binding for the prefix.
     * @param prefix the prefix to obtain the attribute.
     * @return null if there is no need to render the prefix. Otherwise the node of
     * definition.
     **/
    public Attr getMapping(String prefix) {
        com.sun.org.apache.xml.internal.security.c14n.implementations.NameSpaceSymbEntry entry = symb.get(prefix);
        if (entry == null) {
            //There is no definition for the prefix(a bug?).
            return null;
        }
        if (entry.rendered) {
            //No need to render an entry already rendered.
            return null;
        }
        // Mark this entry as render.
        entry = (com.sun.org.apache.xml.internal.security.c14n.implementations.NameSpaceSymbEntry) entry.clone();
        needsClone();
        symb.put(prefix, entry);
        entry.rendered = true;
        entry.lastrendered = entry.uri;
        // Return the node for outputing.
        return entry.n;
    }

    /**
     * Gets a definition without mark it as render.
     * For render in exclusive c14n the namespaces in the include prefixes.
     * @param prefix The prefix whose definition is neaded.
     * @return the attr to render, null if there is no need to render
     **/
    public Attr getMappingWithoutRendered(String prefix) {
        com.sun.org.apache.xml.internal.security.c14n.implementations.NameSpaceSymbEntry entry = symb.get(prefix);
        if (entry == null) {
            return null;
        }
        if (entry.rendered) {
            return null;
        }
        return entry.n;
    }

    /**
     * Adds the mapping for a prefix.
     * @param prefix the prefix of definition
     * @param uri the Uri of the definition
     * @param n the attribute that have the definition
     * @return true if there is already defined.
     **/
    public boolean addMapping(String prefix, String uri, Attr n) {
        com.sun.org.apache.xml.internal.security.c14n.implementations.NameSpaceSymbEntry ob = symb.get(prefix);
        if ((ob != null) && uri.equals(ob.uri)) {
            //If we have it previously defined. Don't keep working.
            return false;
        }
        //Creates and entry in the table for this new definition.
        com.sun.org.apache.xml.internal.security.c14n.implementations.NameSpaceSymbEntry ne = new com.sun.org.apache.xml.internal.security.c14n.implementations.NameSpaceSymbEntry(uri, n, false, prefix);
        needsClone();
        symb.put(prefix, ne);
        if (ob != null) {
            //We have a previous definition store it for the pop.
            //Check if a previous definition(not the inmidiatly one) has been rendered.
            ne.lastrendered = ob.lastrendered;
            if ((ob.lastrendered != null) && (ob.lastrendered.equals(uri))) {
                //Yes it is. Mark as rendered.
                ne.rendered = true;
            }
        }
        return true;
    }

    /**
     * Adds a definition and mark it as render.
     * For inclusive c14n.
     * @param prefix the prefix of definition
     * @param uri the Uri of the definition
     * @param n the attribute that have the definition
     * @return the attr to render, null if there is no need to render
     **/
    public Node addMappingAndRender(String prefix, String uri, Attr n) {
        com.sun.org.apache.xml.internal.security.c14n.implementations.NameSpaceSymbEntry ob = symb.get(prefix);

        if ((ob != null) && uri.equals(ob.uri)) {
            if (!ob.rendered) {
                ob = (com.sun.org.apache.xml.internal.security.c14n.implementations.NameSpaceSymbEntry) ob.clone();
                needsClone();
                symb.put(prefix, ob);
                ob.lastrendered = uri;
                ob.rendered = true;
                return ob.n;
            }
            return null;
        }

        com.sun.org.apache.xml.internal.security.c14n.implementations.NameSpaceSymbEntry ne = new com.sun.org.apache.xml.internal.security.c14n.implementations.NameSpaceSymbEntry(uri,n,true,prefix);
        ne.lastrendered = uri;
        needsClone();
        symb.put(prefix, ne);
        if ((ob != null) && (ob.lastrendered != null) && (ob.lastrendered.equals(uri))) {
            ne.rendered = true;
            return null;
        }
        return ne.n;
    }

    public int getLevel() {
        return level.size();
    }

    public void removeMapping(String prefix) {
        com.sun.org.apache.xml.internal.security.c14n.implementations.NameSpaceSymbEntry ob = symb.get(prefix);

        if (ob != null) {
            needsClone();
            symb.put(prefix, null);
        }
    }

    public void removeMappingIfNotRender(String prefix) {
        com.sun.org.apache.xml.internal.security.c14n.implementations.NameSpaceSymbEntry ob = symb.get(prefix);

        if (ob != null && !ob.rendered) {
            needsClone();
            symb.put(prefix, null);
        }
    }

    public boolean removeMappingIfRender(String prefix) {
        com.sun.org.apache.xml.internal.security.c14n.implementations.NameSpaceSymbEntry ob = symb.get(prefix);

        if (ob != null && ob.rendered) {
            needsClone();
            symb.put(prefix, null);
        }
        return false;
    }
}

/**
 * The internal structure of NameSpaceSymbTable.
 **/
class NameSpaceSymbEntry implements Cloneable {

    String prefix;

    /**The URI that the prefix defines */
    String uri;

    /**The last output in the URI for this prefix (This for speed reason).*/
    String lastrendered = null;

    /**This prefix-URI has been already render or not.*/
    boolean rendered = false;

    /**The attribute to include.*/
    Attr n;

    NameSpaceSymbEntry(String name, Attr n, boolean rendered, String prefix) {
        this.uri = name;
        this.rendered = rendered;
        this.n = n;
        this.prefix = prefix;
    }

    /** @inheritDoc */
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
};

class SymbMap implements Cloneable {
    int free = 23;
    com.sun.org.apache.xml.internal.security.c14n.implementations.NameSpaceSymbEntry[] entries;
    String[] keys;

    SymbMap() {
        entries = new com.sun.org.apache.xml.internal.security.c14n.implementations.NameSpaceSymbEntry[free];
        keys = new String[free];
    }

    void put(String key, com.sun.org.apache.xml.internal.security.c14n.implementations.NameSpaceSymbEntry value) {
        int index = index(key);
        Object oldKey = keys[index];
        keys[index] = key;
        entries[index] = value;
        if ((oldKey == null || !oldKey.equals(key)) && (--free == 0)) {
            free = entries.length;
            int newCapacity = free << 2;
            rehash(newCapacity);
        }
    }

    List<com.sun.org.apache.xml.internal.security.c14n.implementations.NameSpaceSymbEntry> entrySet() {
        List<com.sun.org.apache.xml.internal.security.c14n.implementations.NameSpaceSymbEntry> a = new ArrayList<com.sun.org.apache.xml.internal.security.c14n.implementations.NameSpaceSymbEntry>();
        for (int i = 0;i < entries.length;i++) {
            if ((entries[i] != null) && !("".equals(entries[i].uri))) {
                a.add(entries[i]);
            }
        }
        return a;
    }

    protected int index(Object obj) {
        Object[] set = keys;
        int length = set.length;
        //abs of index
        int index = (obj.hashCode() & 0x7fffffff) % length;
        Object cur = set[index];

        if (cur == null || (cur.equals(obj))) {
            return index;
        }
        length--;
        do {
            index = index == length ? 0 : ++index;
            cur = set[index];
        } while (cur != null && (!cur.equals(obj)));
        return index;
    }

    /**
     * rehashes the map to the new capacity.
     *
     * @param newCapacity an <code>int</code> value
     */
    protected void rehash(int newCapacity) {
        int oldCapacity = keys.length;
        String oldKeys[] = keys;
        com.sun.org.apache.xml.internal.security.c14n.implementations.NameSpaceSymbEntry oldVals[] = entries;

        keys = new String[newCapacity];
        entries = new com.sun.org.apache.xml.internal.security.c14n.implementations.NameSpaceSymbEntry[newCapacity];

        for (int i = oldCapacity; i-- > 0;) {
            if (oldKeys[i] != null) {
                String o = oldKeys[i];
                int index = index(o);
                keys[index] = o;
                entries[index] = oldVals[i];
            }
        }
    }

    com.sun.org.apache.xml.internal.security.c14n.implementations.NameSpaceSymbEntry get(String key) {
        return entries[index(key)];
    }

    protected Object clone()  {
        try {
            com.sun.org.apache.xml.internal.security.c14n.implementations.SymbMap copy = (com.sun.org.apache.xml.internal.security.c14n.implementations.SymbMap) super.clone();
            copy.entries = new com.sun.org.apache.xml.internal.security.c14n.implementations.NameSpaceSymbEntry[entries.length];
            System.arraycopy(entries, 0, copy.entries, 0, entries.length);
            copy.keys = new String[keys.length];
            System.arraycopy(keys, 0, copy.keys, 0, keys.length);

            return copy;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
