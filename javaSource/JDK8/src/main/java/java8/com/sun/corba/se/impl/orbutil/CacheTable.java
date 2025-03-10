/*
 * Copyright (c) 1999, 2003, Oracle and/or its affiliates. All rights reserved.
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

package java8.sun.corba.se.impl.orbutil;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.logging.CORBALogDomains;
import com.sun.corba.se.spi.orb.ORB;

public class CacheTable {
    class Entry {
        Object key;
        int val;
        Entry next;  // this chains the collision list of table "map"
        Entry rnext; // this chains the collision list of table "rmap"
        public Entry(Object k, int v) {
            key = k;
            val = v;
            next = null;
            rnext = null;
        }
    }
    private boolean noReverseMap;
    // size must be power of 2
    static final int INITIAL_SIZE = 16;
    static final int MAX_SIZE = 1 << 30;
    int size;
    int entryCount;
    private Entry [] map;
    private Entry [] rmap;

    private ORB orb;
    private ORBUtilSystemException wrapper;

    private CacheTable() {}
    public  CacheTable(ORB orb, boolean u) {
        //System.out.println("using new cache table");
        this.orb = orb;
        wrapper = ORBUtilSystemException.get(orb,
            CORBALogDomains.RPC_ENCODING);
        noReverseMap = u;
        size = INITIAL_SIZE;
        entryCount = 0;
        initTables();
    }
    private void initTables() {
        map = new Entry[size];
        rmap = noReverseMap ? null : new Entry[size];
    }
    private void grow() {
        if (size == MAX_SIZE)
                return;
        Entry [] oldMap = map;
        int oldSize = size;
        size <<= 1;
        initTables();
        // now rehash the entries into the new table
        for (int i = 0; i < oldSize; i++) {
            for (Entry e = oldMap[i]; e != null; e = e.next)
                put_table(e.key, e.val);
        }
    }
    private int moduloTableSize(int h) {
        // these are the "supplemental hash function" copied from
        // java.util.HashMap, supposed to be "critical"
        h += ~(h << 9);
        h ^=  (h >>> 14);
        h +=  (h << 4);
        h ^=  (h >>> 10);
        return h & (size - 1);
    }
    private int hash(Object key) {
        return moduloTableSize(System.identityHashCode(key));
    }
    private int hash(int val) {
        return moduloTableSize(val);
    }
    public final void put(Object key, int val) {
        if (put_table(key, val)) {
            entryCount++;
            if (entryCount > size * 3 / 4)
                grow();
        }
    }
    private boolean put_table(Object key, int val) {
        int index = hash(key);
        for (Entry e = map[index]; e != null; e = e.next) {
            if (e.key == key) {
                if (e.val != val) {
                    throw wrapper.duplicateIndirectionOffset();
                }
                // if we get here we are trying to put in the same key/val pair
                // this is a no-op, so we just return
                return false;
            }
        }
        // this means the key is not present in our table
        // then it shouldnt be present in our reverse table either
        Entry newEntry = new Entry(key, val);
        newEntry.next = map[index];
        map[index] = newEntry;
        if (!noReverseMap) {
            int rindex = hash(val);
            newEntry.rnext = rmap[rindex];
            rmap[rindex] = newEntry;
        }
        return true;
    }
    public final boolean containsKey(Object key) {
        return (getVal(key) != -1);
    }
    public final int getVal(Object key) {
        int index = hash(key);
        for (Entry e = map[index]; e != null; e = e.next) {
            if (e.key == key)
                return e.val;
        }
        return -1;
    }
    public final boolean containsVal(int val) {
        return (getKey(val) != null);
    }
    public final boolean containsOrderedVal(int val) {
        return containsVal(val);
    }
    public final Object getKey(int val) {
        int index = hash(val);
        for (Entry e = rmap[index]; e != null; e = e.rnext) {
            if (e.val == val)
                return e.key;
        }
        return null;
    }
    public void done() {
        map = null;
        rmap = null;
    }
}
