package j$.time;

import j$.time.u.B;
import j$.time.u.C;
import j$.time.u.D;
import j$.time.u.F;
import j$.time.u.G;
import j$.time.u.j;
import j$.time.u.u;
import j$.time.u.v;
import j$.time.u.w;
import j$.time.u.x;
import j$.time.v.c;
import j$.util.concurrent.ConcurrentHashMap;
import java.io.Serializable;
import java.util.concurrent.ConcurrentMap;

public final class p extends o implements w, x, Comparable, Serializable {
    private static final ConcurrentMap d = new ConcurrentHashMap(16, 0.75f, 4);
    private static final ConcurrentMap e = new ConcurrentHashMap(16, 0.75f, 4);
    public static final p f = X(0);
    public static final p g = X(-64800);
    public static final p h = X(64800);
    private final int b;
    private final transient String c;

    /* JADX WARNING: Removed duplicated region for block: B:29:0x00b8  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x00c0  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static j$.time.p V(java.lang.String r8) {
        /*
            java.lang.String r0 = "offsetId"
            j$.CLASSNAMEp.a(r8, r0)
            java.util.concurrent.ConcurrentMap r0 = e
            java.lang.Object r0 = r0.get(r8)
            j$.time.p r0 = (j$.time.p) r0
            if (r0 == 0) goto L_0x0010
            return r0
        L_0x0010:
            int r1 = r8.length()
            r2 = 2
            r3 = 1
            r4 = 0
            if (r1 == r2) goto L_0x006f
            r2 = 3
            if (r1 == r2) goto L_0x008b
            r5 = 5
            if (r1 == r5) goto L_0x0065
            r6 = 6
            r7 = 4
            if (r1 == r6) goto L_0x005b
            r6 = 7
            if (r1 == r6) goto L_0x004e
            r2 = 9
            if (r1 != r2) goto L_0x0037
            int r1 = Y(r8, r3, r4)
            int r2 = Y(r8, r7, r3)
            int r3 = Y(r8, r6, r3)
            goto L_0x0092
        L_0x0037:
            j$.time.f r1 = new j$.time.f
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Invalid ID for ZoneOffset, invalid format: "
            r2.append(r3)
            r2.append(r8)
            java.lang.String r2 = r2.toString()
            r1.<init>(r2)
            throw r1
        L_0x004e:
            int r1 = Y(r8, r3, r4)
            int r2 = Y(r8, r2, r4)
            int r3 = Y(r8, r5, r4)
            goto L_0x0092
        L_0x005b:
            int r1 = Y(r8, r3, r4)
            int r2 = Y(r8, r7, r3)
            r3 = 0
            goto L_0x0092
        L_0x0065:
            int r1 = Y(r8, r3, r4)
            int r2 = Y(r8, r2, r4)
            r3 = 0
            goto L_0x0092
        L_0x006f:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            char r2 = r8.charAt(r4)
            r1.append(r2)
            java.lang.String r2 = "0"
            r1.append(r2)
            char r2 = r8.charAt(r3)
            r1.append(r2)
            java.lang.String r8 = r1.toString()
        L_0x008b:
            int r1 = Y(r8, r3, r4)
            r2 = 0
            r3 = 0
        L_0x0092:
            char r4 = r8.charAt(r4)
            r5 = 43
            r6 = 45
            if (r4 == r5) goto L_0x00b6
            if (r4 != r6) goto L_0x009f
            goto L_0x00b6
        L_0x009f:
            j$.time.f r5 = new j$.time.f
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "Invalid ID for ZoneOffset, plus/minus not found when expected: "
            r6.append(r7)
            r6.append(r8)
            java.lang.String r6 = r6.toString()
            r5.<init>(r6)
            throw r5
        L_0x00b6:
            if (r4 != r6) goto L_0x00c0
            int r5 = -r1
            int r6 = -r2
            int r7 = -r3
            j$.time.p r5 = W(r5, r6, r7)
            return r5
        L_0x00c0:
            j$.time.p r5 = W(r1, r2, r3)
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.time.p.V(java.lang.String):j$.time.p");
    }

    private static int Y(CharSequence offsetId, int pos, boolean precededByColon) {
        if (!precededByColon || offsetId.charAt(pos - 1) == ':') {
            char ch1 = offsetId.charAt(pos);
            char ch2 = offsetId.charAt(pos + 1);
            if (ch1 >= '0' && ch1 <= '9' && ch2 >= '0' && ch2 <= '9') {
                return ((ch1 - '0') * 10) + (ch2 - '0');
            }
            throw new f("Invalid ID for ZoneOffset, non numeric characters found: " + offsetId);
        }
        throw new f("Invalid ID for ZoneOffset, colon not found when expected: " + offsetId);
    }

    public static p W(int hours, int minutes, int seconds) {
        a0(hours, minutes, seconds);
        return X(Z(hours, minutes, seconds));
    }

    private static void a0(int hours, int minutes, int seconds) {
        if (hours < -18 || hours > 18) {
            throw new f("Zone offset hours not in valid range: value " + hours + " is not in the range -18 to 18");
        }
        if (hours > 0) {
            if (minutes < 0 || seconds < 0) {
                throw new f("Zone offset minutes and seconds must be positive because hours is positive");
            }
        } else if (hours < 0) {
            if (minutes > 0 || seconds > 0) {
                throw new f("Zone offset minutes and seconds must be negative because hours is negative");
            }
        } else if ((minutes > 0 && seconds < 0) || (minutes < 0 && seconds > 0)) {
            throw new f("Zone offset minutes and seconds must have the same sign");
        }
        if (minutes < -59 || minutes > 59) {
            throw new f("Zone offset minutes not in valid range: value " + minutes + " is not in the range -59 to 59");
        } else if (seconds < -59 || seconds > 59) {
            throw new f("Zone offset seconds not in valid range: value " + seconds + " is not in the range -59 to 59");
        } else if (Math.abs(hours) == 18 && (minutes | seconds) != 0) {
            throw new f("Zone offset not in valid range: -18:00 to +18:00");
        }
    }

    private static int Z(int hours, int minutes, int seconds) {
        return (hours * 3600) + (minutes * 60) + seconds;
    }

    public static p X(int totalSeconds) {
        if (totalSeconds < -64800 || totalSeconds > 64800) {
            throw new f("Zone offset not in valid range: -18:00 to +18:00");
        } else if (totalSeconds % 900 != 0) {
            return new p(totalSeconds);
        } else {
            Integer totalSecs = Integer.valueOf(totalSeconds);
            p result = (p) d.get(totalSecs);
            if (result != null) {
                return result;
            }
            d.putIfAbsent(totalSecs, new p(totalSeconds));
            p result2 = (p) d.get(totalSecs);
            e.putIfAbsent(result2.getId(), result2);
            return result2;
        }
    }

    private p(int totalSeconds) {
        this.b = totalSeconds;
        this.c = S(totalSeconds);
    }

    private static String S(int totalSeconds) {
        if (totalSeconds == 0) {
            return "Z";
        }
        int absTotalSeconds = Math.abs(totalSeconds);
        StringBuilder buf = new StringBuilder();
        int absHours = absTotalSeconds / 3600;
        int absMinutes = (absTotalSeconds / 60) % 60;
        buf.append(totalSeconds < 0 ? "-" : "+");
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

    public int U() {
        return this.b;
    }

    public String getId() {
        return this.c;
    }

    public c A() {
        return c.l(this);
    }

    public boolean h(B field) {
        if (field instanceof j) {
            if (field == j.OFFSET_SECONDS) {
                return true;
            }
            return false;
        } else if (field == null || !field.K(this)) {
            return false;
        } else {
            return true;
        }
    }

    public G p(B field) {
        return v.c(this, field);
    }

    public int i(B field) {
        if (field == j.OFFSET_SECONDS) {
            return this.b;
        }
        if (!(field instanceof j)) {
            return p(field).a(f(field), field);
        }
        throw new F("Unsupported field: " + field);
    }

    public long f(B field) {
        if (field == j.OFFSET_SECONDS) {
            return (long) this.b;
        }
        if (!(field instanceof j)) {
            return field.A(this);
        }
        throw new F("Unsupported field: " + field);
    }

    public Object r(D d2) {
        if (d2 == C.k() || d2 == C.m()) {
            return this;
        }
        return v.b(this, d2);
    }

    public u x(u temporal) {
        return temporal.c(j.OFFSET_SECONDS, (long) this.b);
    }

    /* renamed from: T */
    public int compareTo(p other) {
        return other.b - this.b;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof p) || this.b != ((p) obj).b) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.b;
    }

    public String toString() {
        return this.c;
    }
}
