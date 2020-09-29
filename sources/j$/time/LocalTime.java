package j$.time;

import j$.CLASSNAMEb;
import j$.CLASSNAMEp;
import j$.time.format.DateTimeFormatter;
import j$.time.u.B;
import j$.time.u.C;
import j$.time.u.D;
import j$.time.u.E;
import j$.time.u.F;
import j$.time.u.G;
import j$.time.u.j;
import j$.time.u.k;
import j$.time.u.u;
import j$.time.u.v;
import j$.time.u.w;
import j$.time.u.x;
import java.io.Serializable;

public final class LocalTime implements u, x, Comparable, Serializable {
    public static final LocalTime e;
    public static final LocalTime f = new LocalTime(23, 59, 59, NUM);
    public static final LocalTime g;
    private static final LocalTime[] h = new LocalTime[24];
    private final byte a;
    private final byte b;
    private final byte c;
    private final int d;

    static {
        int i = 0;
        while (true) {
            LocalTime[] localTimeArr = h;
            if (i < localTimeArr.length) {
                localTimeArr[i] = new LocalTime(i, 0, 0, 0);
                i++;
            } else {
                g = localTimeArr[0];
                LocalTime localTime = localTimeArr[12];
                e = localTimeArr[0];
                return;
            }
        }
    }

    public static LocalTime Q(int hour, int minute) {
        j.HOUR_OF_DAY.P((long) hour);
        if (minute == 0) {
            return h[hour];
        }
        j.MINUTE_OF_HOUR.P((long) minute);
        return new LocalTime(hour, minute, 0, 0);
    }

    public static LocalTime R(int hour, int minute, int second, int nanoOfSecond) {
        j.HOUR_OF_DAY.P((long) hour);
        j.MINUTE_OF_HOUR.P((long) minute);
        j.SECOND_OF_MINUTE.P((long) second);
        j.NANO_OF_SECOND.P((long) nanoOfSecond);
        return K(hour, minute, second, nanoOfSecond);
    }

    public static LocalTime S(long nanoOfDay) {
        j.NANO_OF_DAY.P(nanoOfDay);
        int hours = (int) (nanoOfDay / 3600000000000L);
        long nanoOfDay2 = nanoOfDay - (((long) hours) * 3600000000000L);
        int minutes = (int) (nanoOfDay2 / 60000000000L);
        long nanoOfDay3 = nanoOfDay2 - (((long) minutes) * 60000000000L);
        int seconds = (int) (nanoOfDay3 / NUM);
        return K(hours, minutes, seconds, (int) (nanoOfDay3 - (((long) seconds) * NUM)));
    }

    public static LocalTime L(w temporal) {
        CLASSNAMEp.a(temporal, "temporal");
        LocalTime time = (LocalTime) temporal.r(C.j());
        if (time != null) {
            return time;
        }
        throw new f("Unable to obtain LocalTime from TemporalAccessor: " + temporal + " of type " + temporal.getClass().getName());
    }

    public static LocalTime parse(CharSequence text, DateTimeFormatter formatter) {
        CLASSNAMEp.a(formatter, "formatter");
        return (LocalTime) formatter.j(text, a.a);
    }

    private static LocalTime K(int hour, int minute, int second, int nanoOfSecond) {
        if ((minute | second | nanoOfSecond) == 0) {
            return h[hour];
        }
        return new LocalTime(hour, minute, second, nanoOfSecond);
    }

    private LocalTime(int hour, int minute, int second, int nanoOfSecond) {
        this.a = (byte) hour;
        this.b = (byte) minute;
        this.c = (byte) second;
        this.d = nanoOfSecond;
    }

    public boolean h(B field) {
        if (field instanceof j) {
            return field.r();
        }
        return field != null && field.K(this);
    }

    public G p(B field) {
        return v.c(this, field);
    }

    public int i(B field) {
        if (field instanceof j) {
            return M(field);
        }
        return v.a(this, field);
    }

    public long f(B field) {
        if (!(field instanceof j)) {
            return field.A(this);
        }
        if (field == j.NANO_OF_DAY) {
            return Y();
        }
        if (field == j.MICRO_OF_DAY) {
            return Y() / 1000;
        }
        return (long) M(field);
    }

    private int M(B field) {
        switch (((j) field).ordinal()) {
            case 0:
                return this.d;
            case 1:
                throw new F("Invalid field 'NanoOfDay' for get() method, use getLong() instead");
            case 2:
                return this.d / 1000;
            case 3:
                throw new F("Invalid field 'MicroOfDay' for get() method, use getLong() instead");
            case 4:
                return this.d / 1000000;
            case 5:
                return (int) (Y() / 1000000);
            case 6:
                return this.c;
            case 7:
                return Z();
            case 8:
                return this.b;
            case 9:
                return (this.a * 60) + this.b;
            case 10:
                return this.a % 12;
            case 11:
                int ham = this.a % 12;
                if (ham % 12 == 0) {
                    return 12;
                }
                return ham;
            case 12:
                return this.a;
            case 13:
                byte b2 = this.a;
                if (b2 == 0) {
                    return 24;
                }
                return b2;
            case 14:
                return this.a / 12;
            default:
                throw new F("Unsupported field: " + field);
        }
    }

    public int P() {
        return this.c;
    }

    public int O() {
        return this.d;
    }

    /* renamed from: a0 */
    public LocalTime a(x adjuster) {
        if (adjuster instanceof LocalTime) {
            return (LocalTime) adjuster;
        }
        return (LocalTime) adjuster.x(this);
    }

    /* renamed from: b0 */
    public LocalTime c(B field, long newValue) {
        if (!(field instanceof j)) {
            return (LocalTime) field.L(this, newValue);
        }
        j f2 = (j) field;
        f2.P(newValue);
        long j = 0;
        switch (f2.ordinal()) {
            case 0:
                return e0((int) newValue);
            case 1:
                return S(newValue);
            case 2:
                return e0(((int) newValue) * 1000);
            case 3:
                return S(1000 * newValue);
            case 4:
                return e0(((int) newValue) * 1000000);
            case 5:
                return S(1000000 * newValue);
            case 6:
                return f0((int) newValue);
            case 7:
                return X(newValue - ((long) Z()));
            case 8:
                return d0((int) newValue);
            case 9:
                return V(newValue - ((long) ((this.a * 60) + this.b)));
            case 10:
                return U(newValue - ((long) (this.a % 12)));
            case 11:
                if (newValue != 12) {
                    j = newValue;
                }
                return U(j - ((long) (this.a % 12)));
            case 12:
                return c0((int) newValue);
            case 13:
                if (newValue != 24) {
                    j = newValue;
                }
                return c0((int) j);
            case 14:
                return U((newValue - ((long) (this.a / 12))) * 12);
            default:
                throw new F("Unsupported field: " + field);
        }
    }

    public LocalTime c0(int hour) {
        if (this.a == hour) {
            return this;
        }
        j.HOUR_OF_DAY.P((long) hour);
        return K(hour, this.b, this.c, this.d);
    }

    public LocalTime d0(int minute) {
        if (this.b == minute) {
            return this;
        }
        j.MINUTE_OF_HOUR.P((long) minute);
        return K(this.a, minute, this.c, this.d);
    }

    public LocalTime f0(int second) {
        if (this.c == second) {
            return this;
        }
        j.SECOND_OF_MINUTE.P((long) second);
        return K(this.a, this.b, second, this.d);
    }

    public LocalTime e0(int nanoOfSecond) {
        if (this.d == nanoOfSecond) {
            return this;
        }
        j.NANO_OF_SECOND.P((long) nanoOfSecond);
        return K(this.a, this.b, this.c, nanoOfSecond);
    }

    /* renamed from: T */
    public LocalTime g(long amountToAdd, E unit) {
        if (!(unit instanceof k)) {
            return (LocalTime) unit.p(this, amountToAdd);
        }
        switch (((k) unit).ordinal()) {
            case 0:
                return W(amountToAdd);
            case 1:
                return W((amountToAdd % 86400000000L) * 1000);
            case 2:
                return W((amountToAdd % 86400000) * 1000000);
            case 3:
                return X(amountToAdd);
            case 4:
                return V(amountToAdd);
            case 5:
                return U(amountToAdd);
            case 6:
                return U((amountToAdd % 2) * 12);
            default:
                throw new F("Unsupported unit: " + unit);
        }
    }

    public LocalTime U(long hoursToAdd) {
        if (hoursToAdd == 0) {
            return this;
        }
        return K(((((int) (hoursToAdd % 24)) + this.a) + 24) % 24, this.b, this.c, this.d);
    }

    public LocalTime V(long minutesToAdd) {
        if (minutesToAdd == 0) {
            return this;
        }
        int mofd = (this.a * 60) + this.b;
        int newMofd = ((((int) (minutesToAdd % 1440)) + mofd) + 1440) % 1440;
        if (mofd == newMofd) {
            return this;
        }
        return K(newMofd / 60, newMofd % 60, this.c, this.d);
    }

    public LocalTime X(long secondstoAdd) {
        if (secondstoAdd == 0) {
            return this;
        }
        int sofd = (this.a * 3600) + (this.b * 60) + this.c;
        int newSofd = ((((int) (secondstoAdd % 86400)) + sofd) + 86400) % 86400;
        if (sofd == newSofd) {
            return this;
        }
        return K(newSofd / 3600, (newSofd / 60) % 60, newSofd % 60, this.d);
    }

    public LocalTime W(long nanosToAdd) {
        if (nanosToAdd == 0) {
            return this;
        }
        long nofd = Y();
        long newNofd = (((nanosToAdd % 86400000000000L) + nofd) + 86400000000000L) % 86400000000000L;
        if (nofd == newNofd) {
            return this;
        }
        return K((int) (newNofd / 3600000000000L), (int) ((newNofd / 60000000000L) % 60), (int) ((newNofd / NUM) % 60), (int) (newNofd % NUM));
    }

    public Object r(D d2) {
        if (d2 == C.a() || d2 == C.n() || d2 == C.m() || d2 == C.k()) {
            return null;
        }
        if (d2 == C.j()) {
            return this;
        }
        if (d2 == C.i()) {
            return null;
        }
        if (d2 == C.l()) {
            return k.NANOS;
        }
        return d2.a(this);
    }

    public u x(u temporal) {
        return temporal.c(j.NANO_OF_DAY, Y());
    }

    public String format(DateTimeFormatter formatter) {
        CLASSNAMEp.a(formatter, "formatter");
        return formatter.b(this);
    }

    public int Z() {
        return (this.a * 3600) + (this.b * 60) + this.c;
    }

    public long Y() {
        return (((long) this.a) * 3600000000000L) + (((long) this.b) * 60000000000L) + (((long) this.c) * NUM) + ((long) this.d);
    }

    /* renamed from: A */
    public int compareTo(LocalTime other) {
        int cmp = CLASSNAMEb.a(this.a, other.a);
        if (cmp != 0) {
            return cmp;
        }
        int cmp2 = CLASSNAMEb.a(this.b, other.b);
        if (cmp2 != 0) {
            return cmp2;
        }
        int cmp3 = CLASSNAMEb.a(this.c, other.c);
        if (cmp3 == 0) {
            return CLASSNAMEb.a(this.d, other.d);
        }
        return cmp3;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof LocalTime)) {
            return false;
        }
        LocalTime other = (LocalTime) obj;
        if (this.a == other.a && this.b == other.b && this.c == other.c && this.d == other.d) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        long nod = Y();
        return (int) ((nod >>> 32) ^ nod);
    }

    public String toString() {
        StringBuilder buf = new StringBuilder(18);
        int hourValue = this.a;
        int minuteValue = this.b;
        int secondValue = this.c;
        int nanoValue = this.d;
        buf.append(hourValue < 10 ? "0" : "");
        buf.append(hourValue);
        String str = ":0";
        buf.append(minuteValue < 10 ? str : ":");
        buf.append(minuteValue);
        if (secondValue > 0 || nanoValue > 0) {
            if (secondValue >= 10) {
                str = ":";
            }
            buf.append(str);
            buf.append(secondValue);
            if (nanoValue > 0) {
                buf.append('.');
                if (nanoValue % 1000000 == 0) {
                    buf.append(Integer.toString((nanoValue / 1000000) + 1000).substring(1));
                } else if (nanoValue % 1000 == 0) {
                    buf.append(Integer.toString((nanoValue / 1000) + 1000000).substring(1));
                } else {
                    buf.append(Integer.toString(NUM + nanoValue).substring(1));
                }
            }
        }
        return buf.toString();
    }
}
