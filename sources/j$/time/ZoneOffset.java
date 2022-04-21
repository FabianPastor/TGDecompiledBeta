package j$.time;

import j$.time.temporal.ChronoField;
import j$.time.temporal.Temporal;
import j$.time.temporal.TemporalAccessor;
import j$.time.temporal.TemporalAdjuster;
import j$.time.temporal.TemporalField;
import j$.time.temporal.TemporalQueries;
import j$.time.temporal.TemporalQuery;
import j$.time.temporal.UnsupportedTemporalTypeException;
import j$.time.temporal.ValueRange;
import j$.time.zone.ZoneRules;
import j$.util.Objects;
import j$.util.concurrent.ConcurrentHashMap;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.concurrent.ConcurrentMap;

public final class ZoneOffset extends ZoneId implements TemporalAccessor, TemporalAdjuster, Comparable<ZoneOffset>, Serializable {
    private static final ConcurrentMap<String, ZoneOffset> ID_CACHE = new ConcurrentHashMap(16, 0.75f, 4);
    public static final ZoneOffset MAX = ofTotalSeconds(64800);
    private static final int MAX_SECONDS = 64800;
    public static final ZoneOffset MIN = ofTotalSeconds(-64800);
    private static final ConcurrentMap<Integer, ZoneOffset> SECONDS_CACHE = new ConcurrentHashMap(16, 0.75f, 4);
    public static final ZoneOffset UTC = ofTotalSeconds(0);
    private static final long serialVersionUID = 2357656521762053153L;
    private final transient String id;
    private final int totalSeconds;

    public static ZoneOffset of(String offsetId) {
        int seconds;
        int minutes;
        int hours;
        Objects.requireNonNull(offsetId, "offsetId");
        ZoneOffset offset = (ZoneOffset) ID_CACHE.get(offsetId);
        if (offset != null) {
            return offset;
        }
        switch (offsetId.length()) {
            case 2:
                offsetId = offsetId.charAt(0) + "0" + offsetId.charAt(1);
                break;
            case 3:
                break;
            case 5:
                hours = parseNumber(offsetId, 1, false);
                minutes = parseNumber(offsetId, 3, false);
                seconds = 0;
                break;
            case 6:
                hours = parseNumber(offsetId, 1, false);
                minutes = parseNumber(offsetId, 4, true);
                seconds = 0;
                break;
            case 7:
                hours = parseNumber(offsetId, 1, false);
                minutes = parseNumber(offsetId, 3, false);
                seconds = parseNumber(offsetId, 5, false);
                break;
            case 9:
                hours = parseNumber(offsetId, 1, false);
                minutes = parseNumber(offsetId, 4, true);
                seconds = parseNumber(offsetId, 7, true);
                break;
            default:
                throw new DateTimeException("Invalid ID for ZoneOffset, invalid format: " + offsetId);
        }
        hours = parseNumber(offsetId, 1, false);
        minutes = 0;
        seconds = 0;
        char first = offsetId.charAt(0);
        if (first != '+' && first != '-') {
            throw new DateTimeException("Invalid ID for ZoneOffset, plus/minus not found when expected: " + offsetId);
        } else if (first == '-') {
            return ofHoursMinutesSeconds(-hours, -minutes, -seconds);
        } else {
            return ofHoursMinutesSeconds(hours, minutes, seconds);
        }
    }

    private static int parseNumber(CharSequence offsetId, int pos, boolean precededByColon) {
        if (!precededByColon || offsetId.charAt(pos - 1) == ':') {
            char ch1 = offsetId.charAt(pos);
            char ch2 = offsetId.charAt(pos + 1);
            if (ch1 >= '0' && ch1 <= '9' && ch2 >= '0' && ch2 <= '9') {
                return ((ch1 - '0') * 10) + (ch2 - '0');
            }
            throw new DateTimeException("Invalid ID for ZoneOffset, non numeric characters found: " + offsetId);
        }
        throw new DateTimeException("Invalid ID for ZoneOffset, colon not found when expected: " + offsetId);
    }

    public static ZoneOffset ofHours(int hours) {
        return ofHoursMinutesSeconds(hours, 0, 0);
    }

    public static ZoneOffset ofHoursMinutes(int hours, int minutes) {
        return ofHoursMinutesSeconds(hours, minutes, 0);
    }

    public static ZoneOffset ofHoursMinutesSeconds(int hours, int minutes, int seconds) {
        validate(hours, minutes, seconds);
        return ofTotalSeconds(totalSeconds(hours, minutes, seconds));
    }

    public static ZoneOffset from(TemporalAccessor temporal) {
        Objects.requireNonNull(temporal, "temporal");
        ZoneOffset offset = (ZoneOffset) temporal.query(TemporalQueries.offset());
        if (offset != null) {
            return offset;
        }
        throw new DateTimeException("Unable to obtain ZoneOffset from TemporalAccessor: " + temporal + " of type " + temporal.getClass().getName());
    }

    private static void validate(int hours, int minutes, int seconds) {
        if (hours < -18 || hours > 18) {
            throw new DateTimeException("Zone offset hours not in valid range: value " + hours + " is not in the range -18 to 18");
        }
        if (hours > 0) {
            if (minutes < 0 || seconds < 0) {
                throw new DateTimeException("Zone offset minutes and seconds must be positive because hours is positive");
            }
        } else if (hours < 0) {
            if (minutes > 0 || seconds > 0) {
                throw new DateTimeException("Zone offset minutes and seconds must be negative because hours is negative");
            }
        } else if ((minutes > 0 && seconds < 0) || (minutes < 0 && seconds > 0)) {
            throw new DateTimeException("Zone offset minutes and seconds must have the same sign");
        }
        if (minutes < -59 || minutes > 59) {
            throw new DateTimeException("Zone offset minutes not in valid range: value " + minutes + " is not in the range -59 to 59");
        } else if (seconds < -59 || seconds > 59) {
            throw new DateTimeException("Zone offset seconds not in valid range: value " + seconds + " is not in the range -59 to 59");
        } else if (Math.abs(hours) == 18 && (minutes | seconds) != 0) {
            throw new DateTimeException("Zone offset not in valid range: -18:00 to +18:00");
        }
    }

    private static int totalSeconds(int hours, int minutes, int seconds) {
        return (hours * 3600) + (minutes * 60) + seconds;
    }

    public static ZoneOffset ofTotalSeconds(int totalSeconds2) {
        if (totalSeconds2 < -64800 || totalSeconds2 > 64800) {
            throw new DateTimeException("Zone offset not in valid range: -18:00 to +18:00");
        } else if (totalSeconds2 % 900 != 0) {
            return new ZoneOffset(totalSeconds2);
        } else {
            Integer totalSecs = Integer.valueOf(totalSeconds2);
            ConcurrentMap<Integer, ZoneOffset> concurrentMap = SECONDS_CACHE;
            ZoneOffset result = (ZoneOffset) concurrentMap.get(totalSecs);
            if (result != null) {
                return result;
            }
            concurrentMap.putIfAbsent(totalSecs, new ZoneOffset(totalSeconds2));
            ZoneOffset result2 = (ZoneOffset) concurrentMap.get(totalSecs);
            ID_CACHE.putIfAbsent(result2.getId(), result2);
            return result2;
        }
    }

    private ZoneOffset(int totalSeconds2) {
        this.totalSeconds = totalSeconds2;
        this.id = buildId(totalSeconds2);
    }

    private static String buildId(int totalSeconds2) {
        if (totalSeconds2 == 0) {
            return "Z";
        }
        int absTotalSeconds = Math.abs(totalSeconds2);
        StringBuilder buf = new StringBuilder();
        int absHours = absTotalSeconds / 3600;
        int absMinutes = (absTotalSeconds / 60) % 60;
        buf.append(totalSeconds2 < 0 ? "-" : "+");
        buf.append(absHours < 10 ? "0" : "");
        buf.append(absHours);
        String str = ":0";
        buf.append(absMinutes < 10 ? str : ":");
        buf.append(absMinutes);
        int absSeconds = absTotalSeconds % 60;
        if (absSeconds != 0) {
            if (absSeconds >= 10) {
                str = ":";
            }
            buf.append(str);
            buf.append(absSeconds);
        }
        return buf.toString();
    }

    public int getTotalSeconds() {
        return this.totalSeconds;
    }

    public String getId() {
        return this.id;
    }

    public ZoneRules getRules() {
        return ZoneRules.of(this);
    }

    public boolean isSupported(TemporalField field) {
        if (field instanceof ChronoField) {
            if (field == ChronoField.OFFSET_SECONDS) {
                return true;
            }
            return false;
        } else if (field == null || !field.isSupportedBy(this)) {
            return false;
        } else {
            return true;
        }
    }

    public ValueRange range(TemporalField field) {
        return TemporalAccessor.CC.$default$range(this, field);
    }

    public int get(TemporalField field) {
        if (field == ChronoField.OFFSET_SECONDS) {
            return this.totalSeconds;
        }
        if (!(field instanceof ChronoField)) {
            return range(field).checkValidIntValue(getLong(field), field);
        }
        throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
    }

    public long getLong(TemporalField field) {
        if (field == ChronoField.OFFSET_SECONDS) {
            return (long) this.totalSeconds;
        }
        if (!(field instanceof ChronoField)) {
            return field.getFrom(this);
        }
        throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
    }

    public <R> R query(TemporalQuery<R> temporalQuery) {
        if (temporalQuery == TemporalQueries.offset() || temporalQuery == TemporalQueries.zone()) {
            return this;
        }
        return TemporalAccessor.CC.$default$query(this, temporalQuery);
    }

    public Temporal adjustInto(Temporal temporal) {
        return temporal.with(ChronoField.OFFSET_SECONDS, (long) this.totalSeconds);
    }

    public int compareTo(ZoneOffset other) {
        return other.totalSeconds - this.totalSeconds;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ZoneOffset) || this.totalSeconds != ((ZoneOffset) obj).totalSeconds) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.totalSeconds;
    }

    public String toString() {
        return this.id;
    }

    private Object writeReplace() {
        return new Ser((byte) 8, this);
    }

    private void readObject(ObjectInputStream s) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    /* access modifiers changed from: package-private */
    public void write(DataOutput out) {
        out.writeByte(8);
        writeExternal(out);
    }

    /* access modifiers changed from: package-private */
    public void writeExternal(DataOutput out) {
        int offsetSecs = this.totalSeconds;
        int offsetByte = offsetSecs % 900 == 0 ? offsetSecs / 900 : 127;
        out.writeByte(offsetByte);
        if (offsetByte == 127) {
            out.writeInt(offsetSecs);
        }
    }

    static ZoneOffset readExternal(DataInput in) {
        int offsetByte = in.readByte();
        return ofTotalSeconds(offsetByte == 127 ? in.readInt() : offsetByte * 900);
    }
}
