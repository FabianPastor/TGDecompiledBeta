package j$.time.zone;

import j$.util.concurrent.ConcurrentHashMap;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.time.zone.ZoneRules;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

final class TzdbZoneRulesProvider extends ZoneRulesProvider {
    private List<String> regionIds;
    private final Map<String, Object> regionToRules = new ConcurrentHashMap();
    private String versionId;

    public TzdbZoneRulesProvider() {
        try {
            load(new DataInputStream(new BufferedInputStream(TzdbZoneRulesProvider.class.getClassLoader().getResource(System.getProperty("jre.tzdb.dat", "j$/time/zone/tzdb.dat")).openStream())));
        } catch (Exception ex) {
            throw new ZoneRulesException("Unable to load TZDB time-zone rules", ex);
        }
    }

    /* access modifiers changed from: protected */
    public Set<String> provideZoneIds() {
        return new HashSet(this.regionIds);
    }

    /* access modifiers changed from: protected */
    public ZoneRules provideRules(String zoneId, boolean forCaching) {
        Object obj = this.regionToRules.get(zoneId);
        if (obj != null) {
            try {
                if (obj instanceof byte[]) {
                    obj = Ser.read(new DataInputStream(new ByteArrayInputStream((byte[]) obj)));
                    this.regionToRules.put(zoneId, obj);
                }
                return (ZoneRules) obj;
            } catch (Exception ex) {
                throw new ZoneRulesException("Invalid binary time-zone data: TZDB:" + zoneId + ", version: " + this.versionId, ex);
            }
        } else {
            throw new ZoneRulesException("Unknown time-zone ID: " + zoneId);
        }
    }

    /* access modifiers changed from: protected */
    public NavigableMap<String, ZoneRules> provideVersions(String zoneId) {
        TreeMap<String, ZoneRules> map = new TreeMap<>();
        ZoneRules rules = getRules(zoneId, false);
        if (rules != null) {
            map.put(this.versionId, rules);
        }
        return map;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v0, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v2, resolved type: java.lang.Object[]} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void load(java.io.DataInputStream r13) {
        /*
            r12 = this;
            byte r0 = r13.readByte()
            java.lang.String r1 = "File format not recognised"
            r2 = 1
            if (r0 != r2) goto L_0x0085
            java.lang.String r0 = r13.readUTF()
            java.lang.String r2 = "TZDB"
            boolean r2 = r2.equals(r0)
            if (r2 == 0) goto L_0x007f
            short r1 = r13.readShort()
            r2 = 0
        L_0x001a:
            if (r2 >= r1) goto L_0x0025
            java.lang.String r3 = r13.readUTF()
            r12.versionId = r3
            int r2 = r2 + 1
            goto L_0x001a
        L_0x0025:
            short r2 = r13.readShort()
            java.lang.String[] r3 = new java.lang.String[r2]
            r4 = 0
        L_0x002c:
            if (r4 >= r2) goto L_0x0037
            java.lang.String r5 = r13.readUTF()
            r3[r4] = r5
            int r4 = r4 + 1
            goto L_0x002c
        L_0x0037:
            java.util.List r4 = java.util.Arrays.asList(r3)
            r12.regionIds = r4
            short r4 = r13.readShort()
            java.lang.Object[] r5 = new java.lang.Object[r4]
            r6 = 0
        L_0x0044:
            if (r6 >= r4) goto L_0x0054
            short r7 = r13.readShort()
            byte[] r7 = new byte[r7]
            r13.readFully(r7)
            r5[r6] = r7
            int r6 = r6 + 1
            goto L_0x0044
        L_0x0054:
            r6 = 0
        L_0x0055:
            if (r6 >= r1) goto L_0x007e
            short r7 = r13.readShort()
            java.util.Map<java.lang.String, java.lang.Object> r8 = r12.regionToRules
            r8.clear()
            r8 = 0
        L_0x0061:
            if (r8 >= r7) goto L_0x007b
            short r9 = r13.readShort()
            r9 = r3[r9]
            short r10 = r13.readShort()
            r11 = 65535(0xffff, float:9.1834E-41)
            r10 = r10 & r11
            r10 = r5[r10]
            java.util.Map<java.lang.String, java.lang.Object> r11 = r12.regionToRules
            r11.put(r9, r10)
            int r8 = r8 + 1
            goto L_0x0061
        L_0x007b:
            int r6 = r6 + 1
            goto L_0x0055
        L_0x007e:
            return
        L_0x007f:
            java.io.StreamCorruptedException r2 = new java.io.StreamCorruptedException
            r2.<init>(r1)
            throw r2
        L_0x0085:
            java.io.StreamCorruptedException r0 = new java.io.StreamCorruptedException
            r0.<init>(r1)
            goto L_0x008c
        L_0x008b:
            throw r0
        L_0x008c:
            goto L_0x008b
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.time.zone.TzdbZoneRulesProvider.load(java.io.DataInputStream):void");
    }

    public String toString() {
        return "TZDB[" + this.versionId + "]";
    }
}
