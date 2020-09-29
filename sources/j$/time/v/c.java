package j$.time.v;

import j$.CLASSNAMEf;
import j$.CLASSNAMEn;
import j$.CLASSNAMEo;
import j$.CLASSNAMEp;
import j$.time.LocalDate;
import j$.time.LocalDateTime;
import j$.time.e;
import j$.time.i;
import j$.time.p;
import j$.util.concurrent.ConcurrentHashMap;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentMap;

public final class c implements Serializable {
    private static final long[] i = new long[0];
    private static final b[] j = new b[0];
    private static final LocalDateTime[] k = new LocalDateTime[0];
    private static final a[] l = new a[0];
    private final long[] a;
    private final p[] b;
    private final long[] c;
    private final LocalDateTime[] d;
    private final p[] e;
    private final b[] f;
    private final TimeZone g;
    private final transient ConcurrentMap h = new ConcurrentHashMap();

    public static c l(p offset) {
        CLASSNAMEp.a(offset, "offset");
        return new c(offset);
    }

    private c(p offset) {
        p[] pVarArr = new p[1];
        this.b = pVarArr;
        pVarArr[0] = offset;
        long[] jArr = i;
        this.a = jArr;
        this.c = jArr;
        this.d = k;
        this.e = pVarArr;
        this.f = j;
        this.g = null;
    }

    c(TimeZone tz) {
        p[] pVarArr = new p[1];
        this.b = pVarArr;
        pVarArr[0] = m(tz.getRawOffset());
        long[] jArr = i;
        this.a = jArr;
        this.c = jArr;
        this.d = k;
        this.e = this.b;
        this.f = j;
        this.g = tz;
    }

    private static p m(int offsetMillis) {
        return p.X(offsetMillis / 1000);
    }

    public boolean j() {
        TimeZone timeZone = this.g;
        if (timeZone != null) {
            if (!timeZone.useDaylightTime() && this.g.getDSTSavings() == 0 && n(i.P()) == null) {
                return true;
            }
            return false;
        } else if (this.c.length == 0) {
            return true;
        } else {
            return false;
        }
    }

    public p d(i instant) {
        TimeZone timeZone = this.g;
        if (timeZone != null) {
            return m(timeZone.getOffset(instant.Y()));
        }
        if (this.c.length == 0) {
            return this.b[0];
        }
        long epochSec = instant.M();
        if (this.f.length > 0) {
            long[] jArr = this.c;
            if (epochSec > jArr[jArr.length - 1]) {
                p[] pVarArr = this.e;
                a[] transArray = b(c(epochSec, pVarArr[pVarArr.length - 1]));
                a trans = null;
                for (int i2 = 0; i2 < transArray.length; i2++) {
                    trans = transArray[i2];
                    if (epochSec < trans.toEpochSecond()) {
                        return trans.M();
                    }
                }
                return trans.L();
            }
        }
        int index = Arrays.binarySearch(this.c, epochSec);
        if (index < 0) {
            index = (-index) - 2;
        }
        return this.e[index + 1];
    }

    public List h(LocalDateTime localDateTime) {
        Object info = e(localDateTime);
        if (info instanceof a) {
            return ((a) info).O();
        }
        return Collections.singletonList((p) info);
    }

    public a g(LocalDateTime localDateTime) {
        Object info = e(localDateTime);
        if (info instanceof a) {
            return (a) info;
        }
        return null;
    }

    private Object e(LocalDateTime dt) {
        int i2 = 0;
        if (this.g != null) {
            a[] transArray = b(dt.Q());
            if (transArray.length == 0) {
                return m(this.g.getOffset(dt.w(this.b[0]) * 1000));
            }
            Object info = null;
            int length = transArray.length;
            while (i2 < length) {
                a trans = transArray[i2];
                info = a(dt, trans);
                if ((info instanceof a) || info.equals(trans.M())) {
                    return info;
                }
                i2++;
            }
            return info;
        } else if (this.c.length == 0) {
            return this.b[0];
        } else {
            if (this.f.length > 0) {
                LocalDateTime[] localDateTimeArr = this.d;
                if (dt.R(localDateTimeArr[localDateTimeArr.length - 1])) {
                    a[] transArray2 = b(dt.Q());
                    Object info2 = null;
                    int length2 = transArray2.length;
                    while (i2 < length2) {
                        a trans2 = transArray2[i2];
                        info2 = a(dt, trans2);
                        if ((info2 instanceof a) || info2.equals(trans2.M())) {
                            return info2;
                        }
                        i2++;
                    }
                    return info2;
                }
            }
            int index = Arrays.binarySearch(this.d, dt);
            if (index == -1) {
                return this.e[0];
            }
            if (index < 0) {
                index = (-index) - 2;
            } else {
                LocalDateTime[] localDateTimeArr2 = this.d;
                if (index < localDateTimeArr2.length - 1 && localDateTimeArr2[index].equals(localDateTimeArr2[index + 1])) {
                    index++;
                }
            }
            if ((index & 1) != 0) {
                return this.e[(index / 2) + 1];
            }
            LocalDateTime[] localDateTimeArr3 = this.d;
            LocalDateTime dtBefore = localDateTimeArr3[index];
            LocalDateTime dtAfter = localDateTimeArr3[index + 1];
            p[] pVarArr = this.e;
            p offsetBefore = pVarArr[index / 2];
            p offsetAfter = pVarArr[(index / 2) + 1];
            if (offsetAfter.U() > offsetBefore.U()) {
                return new a(dtBefore, offsetBefore, offsetAfter);
            }
            return new a(dtAfter, offsetBefore, offsetAfter);
        }
    }

    private Object a(LocalDateTime dt, a trans) {
        LocalDateTime localTransition = trans.r();
        if (trans.P()) {
            if (dt.S(localTransition)) {
                return trans.M();
            }
            if (dt.S(trans.p())) {
                return trans;
            }
            return trans.L();
        } else if (!dt.S(localTransition)) {
            return trans.L();
        } else {
            if (dt.S(trans.p())) {
                return trans.M();
            }
            return trans;
        }
    }

    /* JADX WARNING: type inference failed for: r11v11, types: [java.lang.Object[]] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private j$.time.v.a[] b(int r23) {
        /*
            r22 = this;
            r0 = r22
            r1 = r23
            java.lang.Integer r2 = java.lang.Integer.valueOf(r23)
            java.util.concurrent.ConcurrentMap r3 = r0.h
            java.lang.Object r3 = r3.get(r2)
            j$.time.v.a[] r3 = (j$.time.v.a[]) r3
            if (r3 == 0) goto L_0x0013
            return r3
        L_0x0013:
            java.util.TimeZone r4 = r0.g
            if (r4 == 0) goto L_0x00df
            r4 = 1800(0x708, float:2.522E-42)
            if (r1 >= r4) goto L_0x001e
            j$.time.v.a[] r4 = l
            return r4
        L_0x001e:
            int r4 = r1 + -1
            r6 = 12
            r7 = 31
            r8 = 0
            j$.time.LocalDateTime r4 = j$.time.LocalDateTime.T(r4, r6, r7, r8, r8)
            j$.time.p[] r6 = r0.b
            r6 = r6[r8]
            long r6 = r4.w(r6)
            java.util.TimeZone r8 = r0.g
            r9 = 1000(0x3e8, double:4.94E-321)
            long r11 = r6 * r9
            int r8 = r8.getOffset(r11)
            r11 = 31968000(0x1e7cb00, double:1.57942906E-316)
            long r11 = r11 + r6
            j$.time.v.a[] r3 = l
        L_0x0041:
            int r13 = (r6 > r11 ? 1 : (r6 == r11 ? 0 : -1))
            if (r13 >= 0) goto L_0x00cc
            r13 = 7776000(0x76a700, double:3.8418545E-317)
            long r13 = r13 + r6
            java.util.TimeZone r15 = r0.g
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
            long r4 = j$.CLASSNAMEf.a(r9, r4)
            java.util.TimeZone r9 = r0.g
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
            java.util.TimeZone r4 = r0.g
            r9 = 1000(0x3e8, double:4.94E-321)
            long r11 = r6 * r9
            int r4 = r4.getOffset(r11)
            if (r4 == r8) goto L_0x0091
            r4 = r6
            r13 = r4
        L_0x0091:
            j$.time.p r4 = m(r8)
            java.util.TimeZone r5 = r0.g
            r9 = 1000(0x3e8, double:4.94E-321)
            long r11 = r13 * r9
            int r5 = r5.getOffset(r11)
            j$.time.p r8 = m(r5)
            int r11 = r0.c(r13, r8)
            if (r11 != r1) goto L_0x00bf
            int r11 = r3.length
            int r11 = r11 + 1
            java.lang.Object[] r11 = java.util.Arrays.copyOf(r3, r11)
            r3 = r11
            j$.time.v.a[] r3 = (j$.time.v.a[]) r3
            int r11 = r3.length
            int r11 = r11 + -1
            j$.time.v.a r12 = new j$.time.v.a
            r12.<init>((long) r13, (j$.time.p) r4, (j$.time.p) r8)
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
            java.util.concurrent.ConcurrentMap r4 = r0.h
            r4.putIfAbsent(r2, r3)
        L_0x00de:
            return r3
        L_0x00df:
            j$.time.v.b[] r4 = r0.f
            int r5 = r4.length
            j$.time.v.a[] r3 = new j$.time.v.a[r5]
            r5 = 0
            int r6 = r4.length
            if (r5 < r6) goto L_0x00f2
            r5 = 2100(0x834, float:2.943E-42)
            if (r1 >= r5) goto L_0x00f1
            java.util.concurrent.ConcurrentMap r5 = r0.h
            r5.putIfAbsent(r2, r3)
        L_0x00f1:
            return r3
        L_0x00f2:
            r2 = r4[r5]
            r2 = 0
            goto L_0x00f7
        L_0x00f6:
            throw r2
        L_0x00f7:
            goto L_0x00f6
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.time.v.c.b(int):j$.time.v.a[]");
    }

    public p f(i instant) {
        TimeZone timeZone = this.g;
        if (timeZone != null) {
            return m(timeZone.getRawOffset());
        }
        if (this.c.length == 0) {
            return this.b[0];
        }
        int index = Arrays.binarySearch(this.a, instant.M());
        if (index < 0) {
            index = (-index) - 2;
        }
        return this.b[index + 1];
    }

    public boolean i(i instant) {
        return !f(instant).equals(d(instant));
    }

    public boolean k(LocalDateTime localDateTime, p offset) {
        return h(localDateTime).contains(offset);
    }

    public a n(i instant) {
        if (this.g != null) {
            long epochSec = instant.M();
            if (instant.O() > 0 && epochSec < Long.MAX_VALUE) {
                epochSec++;
            }
            int year = c(epochSec, d(instant));
            a[] transArray = b(year);
            for (int i2 = transArray.length - 1; i2 >= 0; i2--) {
                if (epochSec > transArray[i2].toEpochSecond()) {
                    return transArray[i2];
                }
            }
            if (year <= 1800) {
                return null;
            }
            a[] transArray2 = b(year - 1);
            for (int i3 = transArray2.length - 1; i3 >= 0; i3--) {
                if (epochSec > transArray2[i3].toEpochSecond()) {
                    return transArray2[i3];
                }
            }
            int curOffsetMillis = this.g.getOffset((epochSec - 1) * 1000);
            long min = LocalDate.a0(1800, 1, 1).toEpochDay() * 86400;
            for (long probeSec = Math.min(epochSec - 31104000, (e.e().c() / 1000) + 31968000); min <= probeSec; probeSec -= 7776000) {
                int probeOffsetMillis = this.g.getOffset(probeSec * 1000);
                if (curOffsetMillis != probeOffsetMillis) {
                    int year2 = c(probeSec, m(probeOffsetMillis));
                    a[] transArray3 = b(year2 + 1);
                    for (int i4 = transArray3.length - 1; i4 >= 0; i4--) {
                        if (epochSec > transArray3[i4].toEpochSecond()) {
                            return transArray3[i4];
                        }
                    }
                    a[] transArray4 = b(year2);
                    return transArray4[transArray4.length - 1];
                }
            }
            return null;
        } else if (this.c.length == 0) {
            return null;
        } else {
            long epochSec2 = instant.M();
            if (instant.O() > 0 && epochSec2 < Long.MAX_VALUE) {
                epochSec2++;
            }
            long[] jArr = this.c;
            long lastHistoric = jArr[jArr.length - 1];
            if (this.f.length > 0 && epochSec2 > lastHistoric) {
                p[] pVarArr = this.e;
                p lastHistoricOffset = pVarArr[pVarArr.length - 1];
                int year3 = c(epochSec2, lastHistoricOffset);
                a[] transArray5 = b(year3);
                for (int i5 = transArray5.length - 1; i5 >= 0; i5--) {
                    if (epochSec2 > transArray5[i5].toEpochSecond()) {
                        return transArray5[i5];
                    }
                }
                int year4 = year3 - 1;
                if (year4 > c(lastHistoric, lastHistoricOffset)) {
                    a[] transArray6 = b(year4);
                    return transArray6[transArray6.length - 1];
                }
            }
            int index = Arrays.binarySearch(this.c, epochSec2);
            if (index < 0) {
                index = (-index) - 1;
            }
            if (index <= 0) {
                return null;
            }
            long j2 = this.c[index - 1];
            p[] pVarArr2 = this.e;
            return new a(j2, pVarArr2[index - 1], pVarArr2[index]);
        }
    }

    private int c(long epochSecond, p offset) {
        return LocalDate.b0(CLASSNAMEf.a(((long) offset.U()) + epochSecond, (long) 86400)).U();
    }

    public boolean equals(Object otherRules) {
        if (this == otherRules) {
            return true;
        }
        if (!(otherRules instanceof c)) {
            return false;
        }
        c other = (c) otherRules;
        if (!CLASSNAMEo.a(this.g, other.g) || !Arrays.equals(this.a, other.a) || !Arrays.equals(this.b, other.b) || !Arrays.equals(this.c, other.c) || !Arrays.equals(this.e, other.e) || !Arrays.equals(this.f, other.f)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return ((((CLASSNAMEn.a(this.g) ^ Arrays.hashCode(this.a)) ^ Arrays.hashCode(this.b)) ^ Arrays.hashCode(this.c)) ^ Arrays.hashCode(this.e)) ^ Arrays.hashCode(this.f);
    }

    public String toString() {
        if (this.g != null) {
            return "ZoneRules[timeZone=" + this.g.getID() + "]";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("ZoneRules[currentStandardOffset=");
        p[] pVarArr = this.b;
        sb.append(pVarArr[pVarArr.length - 1]);
        sb.append("]");
        return sb.toString();
    }
}
