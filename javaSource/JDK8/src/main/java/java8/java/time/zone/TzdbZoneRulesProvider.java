/*
 * Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
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
 *
 *
 *
 *
 *
 * Copyright (c) 2009-2012, Stephen Colebourne & Michael Nascimento Santos
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of JSR-310 nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package java8.java.time.zone;

import java.io.*;
import java.time.zone.ZoneRules;
import java.time.zone.ZoneRulesException;
import java.time.zone.ZoneRulesProvider;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Loads time-zone rules for 'TZDB'.
 *
 * @since 1.8
 */
final class TzdbZoneRulesProvider extends ZoneRulesProvider {

    /**
     * All the regions that are available.
     */
    private List<String> regionIds;
    /**
     * Version Id of this tzdb rules
     */
    private String versionId;
    /**
     * Region to rules mapping
     */
    private final Map<String, Object> regionToRules = new ConcurrentHashMap<>();

    /**
     * Creates an instance.
     * Created by the {@code ServiceLoader}.
     *
     * @throws ZoneRulesException if unable to load
     */
    public TzdbZoneRulesProvider() {
        try {
            String libDir = System.getProperty("java.home") + File.separator + "lib";
            try (DataInputStream dis = new DataInputStream(
                     new BufferedInputStream(new FileInputStream(
                         new File(libDir, "tzdb.dat"))))) {
                load(dis);
            }
        } catch (Exception ex) {
            throw new ZoneRulesException("Unable to load TZDB time-zone rules", ex);
        }
    }

    @Override
    protected Set<String> provideZoneIds() {
        return new HashSet<>(regionIds);
    }

    @Override
    protected ZoneRules provideRules(String zoneId, boolean forCaching) {
        // forCaching flag is ignored because this is not a dynamic provider
        Object obj = regionToRules.get(zoneId);
        if (obj == null) {
            throw new ZoneRulesException("Unknown time-zone ID: " + zoneId);
        }
        try {
            if (obj instanceof byte[]) {
                byte[] bytes = (byte[]) obj;
                DataInputStream dis = new DataInputStream(new ByteArrayInputStream(bytes));
                obj = Ser.read(dis);
                regionToRules.put(zoneId, obj);
            }
            return (ZoneRules) obj;
        } catch (Exception ex) {
            throw new ZoneRulesException("Invalid binary time-zone data: TZDB:" + zoneId + ", version: " + versionId, ex);
        }
    }

    @Override
    protected NavigableMap<String, ZoneRules> provideVersions(String zoneId) {
        TreeMap<String, ZoneRules> map = new TreeMap<>();
        ZoneRules rules = getRules(zoneId, false);
        if (rules != null) {
            map.put(versionId, rules);
        }
        return map;
    }

    /**
     * Loads the rules from a DateInputStream, often in a jar file.
     *
     * @param dis  the DateInputStream to load, not null
     * @throws Exception if an error occurs
     */
    private void load(DataInputStream dis) throws Exception {
        if (dis.readByte() != 1) {
            throw new StreamCorruptedException("File format not recognised");
        }
        // group
        String groupId = dis.readUTF();
        if ("TZDB".equals(groupId) == false) {
            throw new StreamCorruptedException("File format not recognised");
        }
        // versions
        int versionCount = dis.readShort();
        for (int i = 0; i < versionCount; i++) {
            versionId = dis.readUTF();
        }
        // regions
        int regionCount = dis.readShort();
        String[] regionArray = new String[regionCount];
        for (int i = 0; i < regionCount; i++) {
            regionArray[i] = dis.readUTF();
        }
        regionIds = Arrays.asList(regionArray);
        // rules
        int ruleCount = dis.readShort();
        Object[] ruleArray = new Object[ruleCount];
        for (int i = 0; i < ruleCount; i++) {
            byte[] bytes = new byte[dis.readShort()];
            dis.readFully(bytes);
            ruleArray[i] = bytes;
        }
        // link version-region-rules
        for (int i = 0; i < versionCount; i++) {
            int versionRegionCount = dis.readShort();
            regionToRules.clear();
            for (int j = 0; j < versionRegionCount; j++) {
                String region = regionArray[dis.readShort()];
                Object rule = ruleArray[dis.readShort() & 0xffff];
                regionToRules.put(region, rule);
            }
        }
    }

    @Override
    public String toString() {
        return "TZDB[" + versionId + "]";
    }
}
