package j$.time;

import j$.CLASSNAMEp;
import j$.time.v.c;
import j$.time.v.d;
import j$.util.DesugarTimeZone;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public abstract class o implements Serializable {
    public static final Map a;

    public abstract c A();

    public abstract String getId();

    static {
        Map<String, String> map = new HashMap<>(64);
        map.put("ACT", "Australia/Darwin");
        map.put("AET", "Australia/Sydney");
        map.put("AGT", "America/Argentina/Buenos_Aires");
        map.put("ART", "Africa/Cairo");
        map.put("AST", "America/Anchorage");
        map.put("BET", "America/Sao_Paulo");
        map.put("BST", "Asia/Dhaka");
        map.put("CAT", "Africa/Harare");
        map.put("CNT", "America/St_Johns");
        map.put("CST", "America/Chicago");
        map.put("CTT", "Asia/Shanghai");
        map.put("EAT", "Africa/Addis_Ababa");
        map.put("ECT", "Europe/Paris");
        map.put("IET", "America/Indiana/Indianapolis");
        map.put("IST", "Asia/Kolkata");
        map.put("JST", "Asia/Tokyo");
        map.put("MIT", "Pacific/Apia");
        map.put("NET", "Asia/Yerevan");
        map.put("NST", "Pacific/Auckland");
        map.put("PLT", "Asia/Karachi");
        map.put("PNT", "America/Phoenix");
        map.put("PRT", "America/Puerto_Rico");
        map.put("PST", "America/Los_Angeles");
        map.put("SST", "Pacific/Guadalcanal");
        map.put("VST", "Asia/Ho_Chi_Minh");
        map.put("EST", "-05:00");
        map.put("MST", "-07:00");
        map.put("HST", "-10:00");
        a = Collections.unmodifiableMap(map);
    }

    public static o R() {
        return DesugarTimeZone.a(TimeZone.getDefault());
    }

    public static o M(String zoneId, Map aliasMap) {
        CLASSNAMEp.a(zoneId, "zoneId");
        CLASSNAMEp.a(aliasMap, "aliasMap");
        String id = (String) aliasMap.get(zoneId);
        return L(id != null ? id : zoneId);
    }

    public static o L(String zoneId) {
        return O(zoneId, true);
    }

    public static o P(String prefix, p offset) {
        CLASSNAMEp.a(prefix, "prefix");
        CLASSNAMEp.a(offset, "offset");
        if (prefix.length() == 0) {
            return offset;
        }
        if (prefix.equals("GMT") || prefix.equals("UTC") || prefix.equals("UT")) {
            if (offset.U() != 0) {
                prefix = prefix.concat(offset.getId());
            }
            return new q(prefix, offset.A());
        }
        throw new IllegalArgumentException("prefix should be GMT, UTC or UT, is: " + prefix);
    }

    static o O(String zoneId, boolean checkAvailable) {
        CLASSNAMEp.a(zoneId, "zoneId");
        if (zoneId.length() <= 1 || zoneId.startsWith("+") || zoneId.startsWith("-")) {
            return p.V(zoneId);
        }
        if (zoneId.startsWith("UTC") || zoneId.startsWith("GMT")) {
            return Q(zoneId, 3, checkAvailable);
        }
        if (zoneId.startsWith("UT")) {
            return Q(zoneId, 2, checkAvailable);
        }
        return q.T(zoneId, checkAvailable);
    }

    private static o Q(String zoneId, int prefixLength, boolean checkAvailable) {
        String prefix = zoneId.substring(0, prefixLength);
        if (zoneId.length() == prefixLength) {
            return P(prefix, p.f);
        }
        if (zoneId.charAt(prefixLength) != '+' && zoneId.charAt(prefixLength) != '-') {
            return q.T(zoneId, checkAvailable);
        }
        try {
            p offset = p.V(zoneId.substring(prefixLength));
            if (offset == p.f) {
                return P(prefix, offset);
            }
            return P(prefix, offset);
        } catch (f ex) {
            throw new f("Invalid ID for offset-based ZoneId: " + zoneId, ex);
        }
    }

    o() {
        if (getClass() != p.class && getClass() != q.class) {
            throw new AssertionError("Invalid subclass");
        }
    }

    public o K() {
        try {
            c rules = A();
            if (rules.j()) {
                return rules.d(i.c);
            }
        } catch (d e) {
        }
        return this;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof o) {
            return getId().equals(((o) obj).getId());
        }
        return false;
    }

    public int hashCode() {
        return getId().hashCode();
    }

    public String toString() {
        return getId();
    }
}
