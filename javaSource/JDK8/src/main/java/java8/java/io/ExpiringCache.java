/*
 * Copyright (c) 2002, 2011, Oracle and/or its affiliates. All rights reserved.
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

/*
 */

package java8.java.io;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

class ExpiringCache {
    private long millisUntilExpiration;
    private Map<String,Entry> map;
    // Clear out old entries every few queries
    private int queryCount;
    private int queryOverflow = 300;
    private int MAX_ENTRIES = 200;

    static class Entry {
        private long   timestamp;
        private String val;

        Entry(long timestamp, String val) {
            this.timestamp = timestamp;
            this.val = val;
        }

        long   timestamp()                  { return timestamp;           }
        void   setTimestamp(long timestamp) { this.timestamp = timestamp; }

        String val()                        { return val;                 }
        void   setVal(String val)           { this.val = val;             }
    }

    ExpiringCache() {
        this(30000);
    }

    @SuppressWarnings("serial")
    ExpiringCache(long millisUntilExpiration) {
        this.millisUntilExpiration = millisUntilExpiration;
        map = new LinkedHashMap<String,Entry>() {
            protected boolean removeEldestEntry(Map.Entry<String,Entry> eldest) {
              return size() > MAX_ENTRIES;
            }
          };
    }

    synchronized String get(String key) {
        if (++queryCount >= queryOverflow) {
            cleanup();
        }
        Entry entry = entryFor(key);
        if (entry != null) {
            return entry.val();
        }
        return null;
    }

    synchronized void put(String key, String val) {
        if (++queryCount >= queryOverflow) {
            cleanup();
        }
        Entry entry = entryFor(key);
        if (entry != null) {
            entry.setTimestamp(System.currentTimeMillis());
            entry.setVal(val);
        } else {
            map.put(key, new Entry(System.currentTimeMillis(), val));
        }
    }

    synchronized void clear() {
        map.clear();
    }

    private Entry entryFor(String key) {
        Entry entry = map.get(key);
        if (entry != null) {
            long delta = System.currentTimeMillis() - entry.timestamp();
            if (delta < 0 || delta >= millisUntilExpiration) {
                map.remove(key);
                entry = null;
            }
        }
        return entry;
    }

    private void cleanup() {
        Set<String> keySet = map.keySet();
        // Avoid ConcurrentModificationExceptions
        String[] keys = new String[keySet.size()];
        int i = 0;
        for (String key: keySet) {
            keys[i++] = key;
        }
        for (int j = 0; j < keys.length; j++) {
            entryFor(keys[j]);
        }
        queryCount = 0;
    }
}
