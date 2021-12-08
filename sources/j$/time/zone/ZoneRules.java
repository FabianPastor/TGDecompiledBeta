package j$.time.zone;

import j$.time.Clock;
import j$.time.Duration;
import j$.time.Duration$$ExternalSyntheticBackport0;
import j$.time.Instant;
import j$.time.LocalDate;
import j$.time.LocalDateTime;
import j$.time.ZoneOffset;
import j$.util.Objects;
import j$.util.concurrent.ConcurrentHashMap;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.zone.ZoneOffsetTransition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentMap;

public final class ZoneRules implements Serializable {
    private static final ZoneOffsetTransitionRule[] EMPTY_LASTRULES = new ZoneOffsetTransitionRule[0];
    private static final LocalDateTime[] EMPTY_LDT_ARRAY = new LocalDateTime[0];
    private static final long[] EMPTY_LONG_ARRAY = new long[0];
    private static final int LAST_CACHED_YEAR = 2100;
    private static final ZoneOffsetTransition[] NO_TRANSITIONS = new ZoneOffsetTransition[0];
    private static final long serialVersionUID = 3044319355680032515L;
    private final ZoneOffsetTransitionRule[] lastRules;
    private final transient ConcurrentMap<Integer, ZoneOffsetTransition[]> lastRulesCache = new ConcurrentHashMap();
    private final long[] savingsInstantTransitions;
    private final LocalDateTime[] savingsLocalTransitions;
    private final ZoneOffset[] standardOffsets;
    private final long[] standardTransitions;
    private final TimeZone timeZone;
    private final ZoneOffset[] wallOffsets;

    public static ZoneRules of(ZoneOffset baseStandardOffset, ZoneOffset baseWallOffset, List<ZoneOffsetTransition> standardOffsetTransitionList, List<ZoneOffsetTransition> transitionList, List<ZoneOffsetTransitionRule> lastRules2) {
        Objects.requireNonNull(baseStandardOffset, "baseStandardOffset");
        Objects.requireNonNull(baseWallOffset, "baseWallOffset");
        Objects.requireNonNull(standardOffsetTransitionList, "standardOffsetTransitionList");
        Objects.requireNonNull(transitionList, "transitionList");
        Objects.requireNonNull(lastRules2, "lastRules");
        return new ZoneRules(baseStandardOffset, baseWallOffset, standardOffsetTransitionList, transitionList, lastRules2);
    }

    public static ZoneRules of(ZoneOffset offset) {
        Objects.requireNonNull(offset, "offset");
        return new ZoneRules(offset);
    }

    ZoneRules(ZoneOffset baseStandardOffset, ZoneOffset baseWallOffset, List<ZoneOffsetTransition> standardOffsetTransitionList, List<ZoneOffsetTransition> transitionList, List<ZoneOffsetTransitionRule> lastRules2) {
        this.standardTransitions = new long[standardOffsetTransitionList.size()];
        ZoneOffset[] zoneOffsetArr = new ZoneOffset[(standardOffsetTransitionList.size() + 1)];
        this.standardOffsets = zoneOffsetArr;
        zoneOffsetArr[0] = baseStandardOffset;
        for (int i = 0; i < standardOffsetTransitionList.size(); i++) {
            this.standardTransitions[i] = standardOffsetTransitionList.get(i).toEpochSecond();
            this.standardOffsets[i + 1] = standardOffsetTransitionList.get(i).getOffsetAfter();
        }
        List<java.time.LocalDateTime> localTransitionList = new ArrayList<>();
        List<java.time.ZoneOffset> localTransitionOffsetList = new ArrayList<>();
        localTransitionOffsetList.add(baseWallOffset);
        for (ZoneOffsetTransition trans : transitionList) {
            if (trans.isGap()) {
                localTransitionList.add(trans.getDateTimeBefore());
                localTransitionList.add(trans.getDateTimeAfter());
            } else {
                localTransitionList.add(trans.getDateTimeAfter());
                localTransitionList.add(trans.getDateTimeBefore());
            }
            localTransitionOffsetList.add(trans.getOffsetAfter());
        }
        this.savingsLocalTransitions = (LocalDateTime[]) localTransitionList.toArray(new LocalDateTime[localTransitionList.size()]);
        this.wallOffsets = (ZoneOffset[]) localTransitionOffsetList.toArray(new ZoneOffset[localTransitionOffsetList.size()]);
        this.savingsInstantTransitions = new long[transitionList.size()];
        for (int i2 = 0; i2 < transitionList.size(); i2++) {
            this.savingsInstantTransitions[i2] = transitionList.get(i2).toEpochSecond();
        }
        if (lastRules2.size() <= 16) {
            this.lastRules = (ZoneOffsetTransitionRule[]) lastRules2.toArray(new ZoneOffsetTransitionRule[lastRules2.size()]);
            this.timeZone = null;
            return;
        }
        throw new IllegalArgumentException("Too many transition rules");
    }

    private ZoneRules(long[] standardTransitions2, ZoneOffset[] standardOffsets2, long[] savingsInstantTransitions2, ZoneOffset[] wallOffsets2, ZoneOffsetTransitionRule[] lastRules2) {
        this.standardTransitions = standardTransitions2;
        this.standardOffsets = standardOffsets2;
        this.savingsInstantTransitions = savingsInstantTransitions2;
        this.wallOffsets = wallOffsets2;
        this.lastRules = lastRules2;
        if (savingsInstantTransitions2.length == 0) {
            this.savingsLocalTransitions = EMPTY_LDT_ARRAY;
        } else {
            List<java.time.LocalDateTime> localTransitionList = new ArrayList<>();
            for (int i = 0; i < savingsInstantTransitions2.length; i++) {
                ZoneOffsetTransition trans = new ZoneOffsetTransition(savingsInstantTransitions2[i], wallOffsets2[i], wallOffsets2[i + 1]);
                if (trans.isGap()) {
                    localTransitionList.add(trans.getDateTimeBefore());
                    localTransitionList.add(trans.getDateTimeAfter());
                } else {
                    localTransitionList.add(trans.getDateTimeAfter());
                    localTransitionList.add(trans.getDateTimeBefore());
                }
            }
            this.savingsLocalTransitions = (LocalDateTime[]) localTransitionList.toArray(new LocalDateTime[localTransitionList.size()]);
        }
        this.timeZone = null;
    }

    private ZoneRules(ZoneOffset offset) {
        ZoneOffset[] zoneOffsetArr = new ZoneOffset[1];
        this.standardOffsets = zoneOffsetArr;
        zoneOffsetArr[0] = offset;
        long[] jArr = EMPTY_LONG_ARRAY;
        this.standardTransitions = jArr;
        this.savingsInstantTransitions = jArr;
        this.savingsLocalTransitions = EMPTY_LDT_ARRAY;
        this.wallOffsets = zoneOffsetArr;
        this.lastRules = EMPTY_LASTRULES;
        this.timeZone = null;
    }

    ZoneRules(TimeZone tz) {
        ZoneOffset[] zoneOffsetArr = new ZoneOffset[1];
        this.standardOffsets = zoneOffsetArr;
        zoneOffsetArr[0] = offsetFromMillis(tz.getRawOffset());
        long[] jArr = EMPTY_LONG_ARRAY;
        this.standardTransitions = jArr;
        this.savingsInstantTransitions = jArr;
        this.savingsLocalTransitions = EMPTY_LDT_ARRAY;
        this.wallOffsets = zoneOffsetArr;
        this.lastRules = EMPTY_LASTRULES;
        this.timeZone = tz;
    }

    private static ZoneOffset offsetFromMillis(int offsetMillis) {
        return ZoneOffset.ofTotalSeconds(offsetMillis / 1000);
    }

    private void readObject(ObjectInputStream s) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    private Object writeReplace() {
        return new Ser(this.timeZone != null ? (byte) 100 : 1, this);
    }

    /* access modifiers changed from: package-private */
    public void writeExternal(DataOutput out) {
        out.writeInt(this.standardTransitions.length);
        for (long trans : this.standardTransitions) {
            Ser.writeEpochSec(trans, out);
        }
        for (ZoneOffset offset : this.standardOffsets) {
            Ser.writeOffset(offset, out);
        }
        out.writeInt(this.savingsInstantTransitions.length);
        for (long trans2 : this.savingsInstantTransitions) {
            Ser.writeEpochSec(trans2, out);
        }
        for (ZoneOffset offset2 : this.wallOffsets) {
            Ser.writeOffset(offset2, out);
        }
        out.writeByte(this.lastRules.length);
        for (ZoneOffsetTransitionRule rule : this.lastRules) {
            rule.writeExternal(out);
        }
    }

    /* access modifiers changed from: package-private */
    public void writeExternalTimeZone(DataOutput out) {
        out.writeUTF(this.timeZone.getID());
    }

    static ZoneRules readExternal(DataInput in) {
        long[] stdTrans;
        long[] jArr;
        int stdSize = in.readInt();
        if (stdSize == 0) {
            stdTrans = EMPTY_LONG_ARRAY;
        } else {
            stdTrans = new long[stdSize];
        }
        for (int i = 0; i < stdSize; i++) {
            stdTrans[i] = Ser.readEpochSec(in);
        }
        ZoneOffset[] stdOffsets = new ZoneOffset[(stdSize + 1)];
        for (int i2 = 0; i2 < stdOffsets.length; i2++) {
            stdOffsets[i2] = Ser.readOffset(in);
        }
        int savSize = in.readInt();
        if (savSize == 0) {
            jArr = EMPTY_LONG_ARRAY;
        } else {
            jArr = new long[savSize];
        }
        long[] savTrans = jArr;
        for (int i3 = 0; i3 < savSize; i3++) {
            savTrans[i3] = Ser.readEpochSec(in);
        }
        ZoneOffset[] savOffsets = new ZoneOffset[(savSize + 1)];
        for (int i4 = 0; i4 < savOffsets.length; i4++) {
            savOffsets[i4] = Ser.readOffset(in);
        }
        int ruleSize = in.readByte();
        ZoneOffsetTransitionRule[] rules = ruleSize == 0 ? EMPTY_LASTRULES : new ZoneOffsetTransitionRule[ruleSize];
        for (int i5 = 0; i5 < ruleSize; i5++) {
            rules[i5] = ZoneOffsetTransitionRule.readExternal(in);
        }
        return new ZoneRules(stdTrans, stdOffsets, savTrans, savOffsets, rules);
    }

    static ZoneRules readExternalTimeZone(DataInput in) {
        return new ZoneRules(TimeZone.getTimeZone(in.readUTF()));
    }

    public boolean isFixedOffset() {
        TimeZone timeZone2 = this.timeZone;
        if (timeZone2 != null) {
            if (!timeZone2.useDaylightTime() && this.timeZone.getDSTSavings() == 0 && previousTransition(Instant.now()) == null) {
                return true;
            }
            return false;
        } else if (this.savingsInstantTransitions.length == 0) {
            return true;
        } else {
            return false;
        }
    }

    public ZoneOffset getOffset(Instant instant) {
        TimeZone timeZone2 = this.timeZone;
        if (timeZone2 != null) {
            return offsetFromMillis(timeZone2.getOffset(instant.toEpochMilli()));
        }
        if (this.savingsInstantTransitions.length == 0) {
            return this.standardOffsets[0];
        }
        long epochSec = instant.getEpochSecond();
        if (this.lastRules.length > 0) {
            long[] jArr = this.savingsInstantTransitions;
            if (epochSec > jArr[jArr.length - 1]) {
                ZoneOffset[] zoneOffsetArr = this.wallOffsets;
                ZoneOffsetTransition[] transArray = findTransitionArray(findYear(epochSec, zoneOffsetArr[zoneOffsetArr.length - 1]));
                ZoneOffsetTransition trans = null;
                for (int i = 0; i < transArray.length; i++) {
                    trans = transArray[i];
                    if (epochSec < trans.toEpochSecond()) {
                        return trans.getOffsetBefore();
                    }
                }
                return trans.getOffsetAfter();
            }
        }
        int index = Arrays.binarySearch(this.savingsInstantTransitions, epochSec);
        if (index < 0) {
            index = (-index) - 2;
        }
        return this.wallOffsets[index + 1];
    }

    public ZoneOffset getOffset(LocalDateTime localDateTime) {
        Object info = getOffsetInfo(localDateTime);
        if (info instanceof ZoneOffsetTransition) {
            return ((ZoneOffsetTransition) info).getOffsetBefore();
        }
        return (ZoneOffset) info;
    }

    public List<ZoneOffset> getValidOffsets(LocalDateTime localDateTime) {
        Object info = getOffsetInfo(localDateTime);
        if (info instanceof ZoneOffsetTransition) {
            return ((ZoneOffsetTransition) info).getValidOffsets();
        }
        return Collections.singletonList((ZoneOffset) info);
    }

    public ZoneOffsetTransition getTransition(LocalDateTime localDateTime) {
        Object info = getOffsetInfo(localDateTime);
        if (info instanceof ZoneOffsetTransition) {
            return (ZoneOffsetTransition) info;
        }
        return null;
    }

    private Object getOffsetInfo(LocalDateTime dt) {
        int i = 0;
        if (this.timeZone != null) {
            ZoneOffsetTransition[] transArray = findTransitionArray(dt.getYear());
            if (transArray.length == 0) {
                return offsetFromMillis(this.timeZone.getOffset(dt.toEpochSecond(this.standardOffsets[0]) * 1000));
            }
            Object info = null;
            int length = transArray.length;
            while (i < length) {
                ZoneOffsetTransition trans = transArray[i];
                info = findOffsetInfo(dt, trans);
                if ((info instanceof ZoneOffsetTransition) || info.equals(trans.getOffsetBefore())) {
                    return info;
                }
                i++;
            }
            return info;
        } else if (this.savingsInstantTransitions.length == 0) {
            return this.standardOffsets[0];
        } else {
            if (this.lastRules.length > 0) {
                LocalDateTime[] localDateTimeArr = this.savingsLocalTransitions;
                if (dt.isAfter(localDateTimeArr[localDateTimeArr.length - 1])) {
                    ZoneOffsetTransition[] transArray2 = findTransitionArray(dt.getYear());
                    Object info2 = null;
                    int length2 = transArray2.length;
                    while (i < length2) {
                        ZoneOffsetTransition trans2 = transArray2[i];
                        info2 = findOffsetInfo(dt, trans2);
                        if ((info2 instanceof ZoneOffsetTransition) || info2.equals(trans2.getOffsetBefore())) {
                            return info2;
                        }
                        i++;
                    }
                    return info2;
                }
            }
            int index = Arrays.binarySearch(this.savingsLocalTransitions, dt);
            if (index == -1) {
                return this.wallOffsets[0];
            }
            if (index < 0) {
                index = (-index) - 2;
            } else {
                LocalDateTime[] localDateTimeArr2 = this.savingsLocalTransitions;
                if (index < localDateTimeArr2.length - 1 && localDateTimeArr2[index].equals(localDateTimeArr2[index + 1])) {
                    index++;
                }
            }
            if ((index & 1) != 0) {
                return this.wallOffsets[(index / 2) + 1];
            }
            LocalDateTime[] localDateTimeArr3 = this.savingsLocalTransitions;
            LocalDateTime dtBefore = localDateTimeArr3[index];
            LocalDateTime dtAfter = localDateTimeArr3[index + 1];
            ZoneOffset[] zoneOffsetArr = this.wallOffsets;
            ZoneOffset offsetBefore = zoneOffsetArr[index / 2];
            ZoneOffset offsetAfter = zoneOffsetArr[(index / 2) + 1];
            if (offsetAfter.getTotalSeconds() > offsetBefore.getTotalSeconds()) {
                return new ZoneOffsetTransition(dtBefore, offsetBefore, offsetAfter);
            }
            return new ZoneOffsetTransition(dtAfter, offsetBefore, offsetAfter);
        }
    }

    private Object findOffsetInfo(LocalDateTime dt, ZoneOffsetTransition trans) {
        LocalDateTime localTransition = trans.getDateTimeBefore();
        if (trans.isGap()) {
            if (dt.isBefore(localTransition)) {
                return trans.getOffsetBefore();
            }
            if (dt.isBefore(trans.getDateTimeAfter())) {
                return trans;
            }
            return trans.getOffsetAfter();
        } else if (!dt.isBefore(localTransition)) {
            return trans.getOffsetAfter();
        } else {
            if (dt.isBefore(trans.getDateTimeAfter())) {
                return trans.getOffsetBefore();
            }
            return trans;
        }
    }

    /* JADX WARNING: type inference failed for: r11v11, types: [java.lang.Object[]] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private j$.time.zone.ZoneOffsetTransition[] findTransitionArray(int r23) {
        /*
            r22 = this;
            r0 = r22
            r1 = r23
            java.lang.Integer r2 = java.lang.Integer.valueOf(r23)
            java.util.concurrent.ConcurrentMap<java.lang.Integer, j$.time.zone.ZoneOffsetTransition[]> r3 = r0.lastRulesCache
            java.lang.Object r3 = r3.get(r2)
            j$.time.zone.ZoneOffsetTransition[] r3 = (j$.time.zone.ZoneOffsetTransition[]) r3
            if (r3 == 0) goto L_0x0013
            return r3
        L_0x0013:
            java.util.TimeZone r4 = r0.timeZone
            if (r4 == 0) goto L_0x00df
            r4 = 1800(0x708, float:2.522E-42)
            if (r1 >= r4) goto L_0x001e
            j$.time.zone.ZoneOffsetTransition[] r4 = NO_TRANSITIONS
            return r4
        L_0x001e:
            int r4 = r1 + -1
            r6 = 12
            r7 = 31
            r8 = 0
            j$.time.LocalDateTime r4 = j$.time.LocalDateTime.of((int) r4, (int) r6, (int) r7, (int) r8, (int) r8)
            j$.time.ZoneOffset[] r6 = r0.standardOffsets
            r6 = r6[r8]
            long r6 = r4.toEpochSecond(r6)
            java.util.TimeZone r8 = r0.timeZone
            r9 = 1000(0x3e8, double:4.94E-321)
            long r11 = r6 * r9
            int r8 = r8.getOffset(r11)
            r11 = 31968000(0x1e7cb00, double:1.57942906E-316)
            long r11 = r11 + r6
            j$.time.zone.ZoneOffsetTransition[] r3 = NO_TRANSITIONS
        L_0x0041:
            int r13 = (r6 > r11 ? 1 : (r6 == r11 ? 0 : -1))
            if (r13 >= 0) goto L_0x00cc
            r13 = 7776000(0x76a700, double:3.8418545E-317)
            long r13 = r13 + r6
            java.util.TimeZone r15 = r0.timeZone
            r16 = r6
            long r5 = r13 * r9
            int r5 = r15.getOffset(r5)
            if (r8 == r5) goto L_0x00c1
            r6 = r16
        L_0x0057:
            long r16 = r13 - r6
            r18 = 1
            int r5 = (r16 > r18 ? 1 : (r16 == r18 ? 0 : -1))
            if (r5 <= 0) goto L_0x0080
            long r9 = r13 + r6
            r15 = r4
            r4 = 2
            long r4 = j$.time.Duration$$ExternalSyntheticBackport0.m(r9, r4)
            java.util.TimeZone r9 = r0.timeZone
            r20 = r11
            r16 = 1000(0x3e8, double:4.94E-321)
            long r10 = r4 * r16
            int r9 = r9.getOffset(r10)
            if (r9 != r8) goto L_0x0078
            r6 = r4
            goto L_0x007a
        L_0x0078:
            r9 = r4
            r13 = r9
        L_0x007a:
            r4 = r15
            r11 = r20
            r9 = 1000(0x3e8, double:4.94E-321)
            goto L_0x0057
        L_0x0080:
            r15 = r4
            r20 = r11
            java.util.TimeZone r4 = r0.timeZone
            r9 = 1000(0x3e8, double:4.94E-321)
            long r11 = r6 * r9
            int r4 = r4.getOffset(r11)
            if (r4 == r8) goto L_0x0091
            r4 = r6
            r13 = r4
        L_0x0091:
            j$.time.ZoneOffset r4 = offsetFromMillis(r8)
            java.util.TimeZone r5 = r0.timeZone
            r9 = 1000(0x3e8, double:4.94E-321)
            long r11 = r13 * r9
            int r5 = r5.getOffset(r11)
            j$.time.ZoneOffset r8 = offsetFromMillis(r5)
            int r11 = r0.findYear(r13, r8)
            if (r11 != r1) goto L_0x00bf
            int r11 = r3.length
            int r11 = r11 + 1
            java.lang.Object[] r11 = java.util.Arrays.copyOf(r3, r11)
            r3 = r11
            j$.time.zone.ZoneOffsetTransition[] r3 = (j$.time.zone.ZoneOffsetTransition[]) r3
            int r11 = r3.length
            int r11 = r11 + -1
            j$.time.zone.ZoneOffsetTransition r12 = new j$.time.zone.ZoneOffsetTransition
            r12.<init>((long) r13, (j$.time.ZoneOffset) r4, (j$.time.ZoneOffset) r8)
            r3[r11] = r12
            r8 = r5
            goto L_0x00c6
        L_0x00bf:
            r8 = r5
            goto L_0x00c6
        L_0x00c1:
            r15 = r4
            r20 = r11
            r6 = r16
        L_0x00c6:
            r6 = r13
            r4 = r15
            r11 = r20
            goto L_0x0041
        L_0x00cc:
            r15 = r4
            r16 = r6
            r20 = r11
            r4 = 1916(0x77c, float:2.685E-42)
            if (r4 > r1) goto L_0x00de
            r4 = 2100(0x834, float:2.943E-42)
            if (r1 >= r4) goto L_0x00de
            java.util.concurrent.ConcurrentMap<java.lang.Integer, j$.time.zone.ZoneOffsetTransition[]> r4 = r0.lastRulesCache
            r4.putIfAbsent(r2, r3)
        L_0x00de:
            return r3
        L_0x00df:
            j$.time.zone.ZoneOffsetTransitionRule[] r4 = r0.lastRules
            int r5 = r4.length
            j$.time.zone.ZoneOffsetTransition[] r3 = new j$.time.zone.ZoneOffsetTransition[r5]
            r5 = 0
        L_0x00e5:
            int r6 = r4.length
            if (r5 >= r6) goto L_0x00f3
            r6 = r4[r5]
            j$.time.zone.ZoneOffsetTransition r6 = r6.createTransition(r1)
            r3[r5] = r6
            int r5 = r5 + 1
            goto L_0x00e5
        L_0x00f3:
            r5 = 2100(0x834, float:2.943E-42)
            if (r1 >= r5) goto L_0x00fc
            java.util.concurrent.ConcurrentMap<java.lang.Integer, j$.time.zone.ZoneOffsetTransition[]> r5 = r0.lastRulesCache
            r5.putIfAbsent(r2, r3)
        L_0x00fc:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.time.zone.ZoneRules.findTransitionArray(int):j$.time.zone.ZoneOffsetTransition[]");
    }

    public ZoneOffset getStandardOffset(Instant instant) {
        TimeZone timeZone2 = this.timeZone;
        if (timeZone2 != null) {
            return offsetFromMillis(timeZone2.getRawOffset());
        }
        if (this.savingsInstantTransitions.length == 0) {
            return this.standardOffsets[0];
        }
        int index = Arrays.binarySearch(this.standardTransitions, instant.getEpochSecond());
        if (index < 0) {
            index = (-index) - 2;
        }
        return this.standardOffsets[index + 1];
    }

    public Duration getDaylightSavings(Instant instant) {
        TimeZone timeZone2 = this.timeZone;
        if (timeZone2 != null) {
            return Duration.ofMillis((long) (timeZone2.getOffset(instant.toEpochMilli()) - this.timeZone.getRawOffset()));
        }
        if (this.savingsInstantTransitions.length == 0) {
            return Duration.ZERO;
        }
        return Duration.ofSeconds((long) (getOffset(instant).getTotalSeconds() - getStandardOffset(instant).getTotalSeconds()));
    }

    public boolean isDaylightSavings(Instant instant) {
        return !getStandardOffset(instant).equals(getOffset(instant));
    }

    public boolean isValidOffset(LocalDateTime localDateTime, ZoneOffset offset) {
        return getValidOffsets(localDateTime).contains(offset);
    }

    public ZoneOffsetTransition nextTransition(Instant instant) {
        int index;
        if (this.timeZone != null) {
            long epochSec = instant.getEpochSecond();
            int year = findYear(epochSec, getOffset(instant));
            for (ZoneOffsetTransition trans : findTransitionArray(year)) {
                if (epochSec < trans.toEpochSecond()) {
                    return trans;
                }
            }
            if (year < NUM) {
                for (ZoneOffsetTransition trans2 : findTransitionArray(year + 1)) {
                    if (epochSec < trans2.toEpochSecond()) {
                        return trans2;
                    }
                }
            }
            int curOffsetMillis = this.timeZone.getOffset((1 + epochSec) * 1000);
            long max = (Clock.systemUTC().millis() / 1000) + 31968000;
            for (long probeSec = 31104000 + epochSec; probeSec <= max; probeSec += 7776000) {
                int probeOffsetMillis = this.timeZone.getOffset(probeSec * 1000);
                if (curOffsetMillis != probeOffsetMillis) {
                    int year2 = findYear(probeSec, offsetFromMillis(probeOffsetMillis));
                    for (ZoneOffsetTransition trans3 : findTransitionArray(year2 - 1)) {
                        if (epochSec < trans3.toEpochSecond()) {
                            return trans3;
                        }
                    }
                    return findTransitionArray(year2)[0];
                }
            }
            return null;
        } else if (this.savingsInstantTransitions.length == 0) {
            return null;
        } else {
            long epochSec2 = instant.getEpochSecond();
            long[] jArr = this.savingsInstantTransitions;
            if (epochSec2 < jArr[jArr.length - 1]) {
                int index2 = Arrays.binarySearch(jArr, epochSec2);
                if (index2 < 0) {
                    index = (-index2) - 1;
                } else {
                    index = index2 + 1;
                }
                long j = this.savingsInstantTransitions[index];
                ZoneOffset[] zoneOffsetArr = this.wallOffsets;
                return new ZoneOffsetTransition(j, zoneOffsetArr[index], zoneOffsetArr[index + 1]);
            } else if (this.lastRules.length == 0) {
                return null;
            } else {
                ZoneOffset[] zoneOffsetArr2 = this.wallOffsets;
                int year3 = findYear(epochSec2, zoneOffsetArr2[zoneOffsetArr2.length - 1]);
                for (ZoneOffsetTransition trans4 : findTransitionArray(year3)) {
                    if (epochSec2 < trans4.toEpochSecond()) {
                        return trans4;
                    }
                }
                if (year3 < NUM) {
                    return findTransitionArray(year3 + 1)[0];
                }
                return null;
            }
        }
    }

    public ZoneOffsetTransition previousTransition(Instant instant) {
        if (this.timeZone != null) {
            long epochSec = instant.getEpochSecond();
            if (instant.getNano() > 0 && epochSec < Long.MAX_VALUE) {
                epochSec++;
            }
            int year = findYear(epochSec, getOffset(instant));
            ZoneOffsetTransition[] transArray = findTransitionArray(year);
            for (int i = transArray.length - 1; i >= 0; i--) {
                if (epochSec > transArray[i].toEpochSecond()) {
                    return transArray[i];
                }
            }
            if (year <= 1800) {
                return null;
            }
            ZoneOffsetTransition[] transArray2 = findTransitionArray(year - 1);
            for (int i2 = transArray2.length - 1; i2 >= 0; i2--) {
                if (epochSec > transArray2[i2].toEpochSecond()) {
                    return transArray2[i2];
                }
            }
            int curOffsetMillis = this.timeZone.getOffset((epochSec - 1) * 1000);
            long min = LocalDate.of(1800, 1, 1).toEpochDay() * 86400;
            for (long probeSec = Math.min(epochSec - 31104000, (Clock.systemUTC().millis() / 1000) + 31968000); min <= probeSec; probeSec -= 7776000) {
                int probeOffsetMillis = this.timeZone.getOffset(probeSec * 1000);
                if (curOffsetMillis != probeOffsetMillis) {
                    int year2 = findYear(probeSec, offsetFromMillis(probeOffsetMillis));
                    ZoneOffsetTransition[] transArray3 = findTransitionArray(year2 + 1);
                    for (int i3 = transArray3.length - 1; i3 >= 0; i3--) {
                        if (epochSec > transArray3[i3].toEpochSecond()) {
                            return transArray3[i3];
                        }
                    }
                    ZoneOffsetTransition[] transArray4 = findTransitionArray(year2);
                    return transArray4[transArray4.length - 1];
                }
            }
            return null;
        } else if (this.savingsInstantTransitions.length == 0) {
            return null;
        } else {
            long epochSec2 = instant.getEpochSecond();
            if (instant.getNano() > 0 && epochSec2 < Long.MAX_VALUE) {
                epochSec2++;
            }
            long[] jArr = this.savingsInstantTransitions;
            long lastHistoric = jArr[jArr.length - 1];
            if (this.lastRules.length > 0 && epochSec2 > lastHistoric) {
                ZoneOffset[] zoneOffsetArr = this.wallOffsets;
                ZoneOffset lastHistoricOffset = zoneOffsetArr[zoneOffsetArr.length - 1];
                int year3 = findYear(epochSec2, lastHistoricOffset);
                ZoneOffsetTransition[] transArray5 = findTransitionArray(year3);
                for (int i4 = transArray5.length - 1; i4 >= 0; i4--) {
                    if (epochSec2 > transArray5[i4].toEpochSecond()) {
                        return transArray5[i4];
                    }
                }
                int year4 = year3 - 1;
                if (year4 > findYear(lastHistoric, lastHistoricOffset)) {
                    ZoneOffsetTransition[] transArray6 = findTransitionArray(year4);
                    return transArray6[transArray6.length - 1];
                }
            }
            int index = Arrays.binarySearch(this.savingsInstantTransitions, epochSec2);
            if (index < 0) {
                index = (-index) - 1;
            }
            if (index <= 0) {
                return null;
            }
            long j = this.savingsInstantTransitions[index - 1];
            ZoneOffset[] zoneOffsetArr2 = this.wallOffsets;
            return new ZoneOffsetTransition(j, zoneOffsetArr2[index - 1], zoneOffsetArr2[index]);
        }
    }

    private int findYear(long epochSecond, ZoneOffset offset) {
        return LocalDate.ofEpochDay(Duration$$ExternalSyntheticBackport0.m(((long) offset.getTotalSeconds()) + epochSecond, 86400)).getYear();
    }

    public List<ZoneOffsetTransition> getTransitions() {
        List<ZoneOffsetTransition> list = new ArrayList<>();
        int i = 0;
        while (true) {
            long[] jArr = this.savingsInstantTransitions;
            if (i >= jArr.length) {
                return Collections.unmodifiableList(list);
            }
            long j = jArr[i];
            ZoneOffset[] zoneOffsetArr = this.wallOffsets;
            list.add(new ZoneOffsetTransition(j, zoneOffsetArr[i], zoneOffsetArr[i + 1]));
            i++;
        }
    }

    public List<ZoneOffsetTransitionRule> getTransitionRules() {
        return Collections.unmodifiableList(Arrays.asList(this.lastRules));
    }

    public boolean equals(Object otherRules) {
        if (this == otherRules) {
            return true;
        }
        if (!(otherRules instanceof ZoneRules)) {
            return false;
        }
        ZoneRules other = (ZoneRules) otherRules;
        if (!Objects.equals(this.timeZone, other.timeZone) || !Arrays.equals(this.standardTransitions, other.standardTransitions) || !Arrays.equals(this.standardOffsets, other.standardOffsets) || !Arrays.equals(this.savingsInstantTransitions, other.savingsInstantTransitions) || !Arrays.equals(this.wallOffsets, other.wallOffsets) || !Arrays.equals(this.lastRules, other.lastRules)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return ((((Objects.hashCode(this.timeZone) ^ Arrays.hashCode(this.standardTransitions)) ^ Arrays.hashCode(this.standardOffsets)) ^ Arrays.hashCode(this.savingsInstantTransitions)) ^ Arrays.hashCode(this.wallOffsets)) ^ Arrays.hashCode(this.lastRules);
    }

    public String toString() {
        if (this.timeZone != null) {
            return "ZoneRules[timeZone=" + this.timeZone.getID() + "]";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("ZoneRules[currentStandardOffset=");
        ZoneOffset[] zoneOffsetArr = this.standardOffsets;
        sb.append(zoneOffsetArr[zoneOffsetArr.length - 1]);
        sb.append("]");
        return sb.toString();
    }
}
