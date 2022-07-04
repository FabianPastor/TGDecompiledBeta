package j$.time;

import j$.time.format.DateTimeFormatterBuilder;
import j$.time.format.TextStyle;
import j$.time.temporal.TemporalAccessor;
import j$.time.temporal.TemporalField;
import j$.time.temporal.TemporalQueries;
import j$.time.temporal.TemporalQuery;
import j$.time.temporal.UnsupportedTemporalTypeException;
import j$.time.temporal.ValueRange;
import j$.time.zone.ZoneRules;
import j$.time.zone.ZoneRulesException;
import j$.time.zone.ZoneRulesProvider;
import j$.util.DesugarTimeZone;
import j$.util.Objects;
import java.io.DataOutput;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

public abstract class ZoneId implements Serializable {
    public static final Map<String, String> SHORT_IDS;
    private static final long serialVersionUID = 8352817235686L;

    public abstract String getId();

    public abstract ZoneRules getRules();

    /* access modifiers changed from: package-private */
    public abstract void write(DataOutput dataOutput);

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
        SHORT_IDS = Collections.unmodifiableMap(map);
    }

    public static ZoneId systemDefault() {
        return DesugarTimeZone.toZoneId(TimeZone.getDefault());
    }

    public static Set<String> getAvailableZoneIds() {
        return ZoneRulesProvider.getAvailableZoneIds();
    }

    public static ZoneId of(String zoneId, Map<String, String> aliasMap) {
        Objects.requireNonNull(zoneId, "zoneId");
        Objects.requireNonNull(aliasMap, "aliasMap");
        String id = aliasMap.get(zoneId);
        return of(id != null ? id : zoneId);
    }

    public static ZoneId of(String zoneId) {
        return of(zoneId, true);
    }

    public static ZoneId ofOffset(String prefix, ZoneOffset offset) {
        Objects.requireNonNull(prefix, "prefix");
        Objects.requireNonNull(offset, "offset");
        if (prefix.length() == 0) {
            return offset;
        }
        if (prefix.equals("GMT") || prefix.equals("UTC") || prefix.equals("UT")) {
            if (offset.getTotalSeconds() != 0) {
                prefix = prefix.concat(offset.getId());
            }
            return new ZoneRegion(prefix, offset.getRules());
        }
        throw new IllegalArgumentException("prefix should be GMT, UTC or UT, is: " + prefix);
    }

    static ZoneId of(String zoneId, boolean checkAvailable) {
        Objects.requireNonNull(zoneId, "zoneId");
        if (zoneId.length() <= 1 || zoneId.startsWith("+") || zoneId.startsWith("-")) {
            return ZoneOffset.of(zoneId);
        }
        if (zoneId.startsWith("UTC") || zoneId.startsWith("GMT")) {
            return ofWithPrefix(zoneId, 3, checkAvailable);
        }
        if (zoneId.startsWith("UT")) {
            return ofWithPrefix(zoneId, 2, checkAvailable);
        }
        return ZoneRegion.ofId(zoneId, checkAvailable);
    }

    private static ZoneId ofWithPrefix(String zoneId, int prefixLength, boolean checkAvailable) {
        String prefix = zoneId.substring(0, prefixLength);
        if (zoneId.length() == prefixLength) {
            return ofOffset(prefix, ZoneOffset.UTC);
        }
        if (zoneId.charAt(prefixLength) != '+' && zoneId.charAt(prefixLength) != '-') {
            return ZoneRegion.ofId(zoneId, checkAvailable);
        }
        try {
            ZoneOffset offset = ZoneOffset.of(zoneId.substring(prefixLength));
            if (offset == ZoneOffset.UTC) {
                return ofOffset(prefix, offset);
            }
            return ofOffset(prefix, offset);
        } catch (DateTimeException ex) {
            throw new DateTimeException("Invalid ID for offset-based ZoneId: " + zoneId, ex);
        }
    }

    public static ZoneId from(TemporalAccessor temporal) {
        ZoneId obj = (ZoneId) temporal.query(TemporalQueries.zone());
        if (obj != null) {
            return obj;
        }
        throw new DateTimeException("Unable to obtain ZoneId from TemporalAccessor: " + temporal + " of type " + temporal.getClass().getName());
    }

    ZoneId() {
        if (getClass() != ZoneOffset.class && getClass() != ZoneRegion.class) {
            throw new AssertionError("Invalid subclass");
        }
    }

    public String getDisplayName(TextStyle style, Locale locale) {
        return new DateTimeFormatterBuilder().appendZoneText(style).toFormatter(locale).format(toTemporal());
    }

    private TemporalAccessor toTemporal() {
        return new TemporalAccessor() {
            public /* synthetic */ int get(TemporalField temporalField) {
                return TemporalAccessor.CC.$default$get(this, temporalField);
            }

            public /* synthetic */ ValueRange range(TemporalField temporalField) {
                return TemporalAccessor.CC.$default$range(this, temporalField);
            }

            public boolean isSupported(TemporalField field) {
                return false;
            }

            public long getLong(TemporalField field) {
                throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
            }

            public <R> R query(TemporalQuery<R> temporalQuery) {
                if (temporalQuery == TemporalQueries.zoneId()) {
                    return ZoneId.this;
                }
                return TemporalAccessor.CC.$default$query(this, temporalQuery);
            }
        };
    }

    public ZoneId normalized() {
        try {
            ZoneRules rules = getRules();
            if (rules.isFixedOffset()) {
                return rules.getOffset(Instant.EPOCH);
            }
        } catch (ZoneRulesException e) {
        }
        return this;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ZoneId) {
            return getId().equals(((ZoneId) obj).getId());
        }
        return false;
    }

    public int hashCode() {
        return getId().hashCode();
    }

    private void readObject(ObjectInputStream s) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    public String toString() {
        return getId();
    }

    private Object writeReplace() {
        return new Ser((byte) 7, this);
    }
}
