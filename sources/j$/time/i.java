package j$.time;

import j$.CLASSNAMEe;
import j$.CLASSNAMEf;
import j$.CLASSNAMEi;
import j$.CLASSNAMEj;
import j$.CLASSNAMEk;
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

public final class i implements u, x, Comparable, Serializable {
    public static final i c = new i(0, 0);
    private final long a;
    private final int b;

    static {
        S(-31557014167219200L, 0);
        S(31556889864403199L, NUM);
    }

    public static i P() {
        return e.e().b();
    }

    public static i R(long epochSecond) {
        return K(epochSecond, 0);
    }

    public static i S(long epochSecond, long nanoAdjustment) {
        return K(CLASSNAMEe.a(epochSecond, CLASSNAMEf.a(nanoAdjustment, NUM)), (int) CLASSNAMEi.a(nanoAdjustment, NUM));
    }

    public static i Q(long epochMilli) {
        return K(CLASSNAMEf.a(epochMilli, (long) 1000), 1000000 * CLASSNAMEj.a(epochMilli, 1000));
    }

    public static i L(w temporal) {
        if (temporal instanceof i) {
            return (i) temporal;
        }
        CLASSNAMEp.a(temporal, "temporal");
        try {
            return S(temporal.f(j.INSTANT_SECONDS), (long) temporal.i(j.NANO_OF_SECOND));
        } catch (f ex) {
            throw new f("Unable to obtain Instant from TemporalAccessor: " + temporal + " of type " + temporal.getClass().getName(), ex);
        }
    }

    private static i K(long seconds, int nanoOfSecond) {
        if ((((long) nanoOfSecond) | seconds) == 0) {
            return c;
        }
        if (seconds >= -31557014167219200L && seconds <= 31556889864403199L) {
            return new i(seconds, nanoOfSecond);
        }
        throw new f("Instant exceeds minimum or maximum instant");
    }

    private i(long epochSecond, int nanos) {
        this.a = epochSecond;
        this.b = nanos;
    }

    public boolean h(B field) {
        if (field instanceof j) {
            if (field == j.INSTANT_SECONDS || field == j.NANO_OF_SECOND || field == j.MICRO_OF_SECOND || field == j.MILLI_OF_SECOND) {
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
        if (!(field instanceof j)) {
            return p(field).a(field.A(this), field);
        }
        int ordinal = ((j) field).ordinal();
        if (ordinal == 0) {
            return this.b;
        }
        if (ordinal == 2) {
            return this.b / 1000;
        }
        if (ordinal == 4) {
            return this.b / 1000000;
        }
        if (ordinal == 28) {
            j.INSTANT_SECONDS.O(this.a);
        }
        throw new F("Unsupported field: " + field);
    }

    public long f(B field) {
        if (!(field instanceof j)) {
            return field.A(this);
        }
        int ordinal = ((j) field).ordinal();
        if (ordinal == 0) {
            return (long) this.b;
        }
        if (ordinal == 2) {
            return (long) (this.b / 1000);
        }
        if (ordinal == 4) {
            return (long) (this.b / 1000000);
        }
        if (ordinal == 28) {
            return this.a;
        }
        throw new F("Unsupported field: " + field);
    }

    public long M() {
        return this.a;
    }

    public int O() {
        return this.b;
    }

    /* renamed from: Z */
    public i a(x adjuster) {
        return (i) adjuster.x(this);
    }

    /* renamed from: a0 */
    public i c(B field, long newValue) {
        if (!(field instanceof j)) {
            return (i) field.L(this, newValue);
        }
        j f = (j) field;
        f.P(newValue);
        int ordinal = f.ordinal();
        if (ordinal == 0) {
            return newValue != ((long) this.b) ? K(this.a, (int) newValue) : this;
        }
        if (ordinal == 2) {
            int nval = ((int) newValue) * 1000;
            return nval != this.b ? K(this.a, nval) : this;
        } else if (ordinal == 4) {
            int nval2 = ((int) newValue) * 1000000;
            return nval2 != this.b ? K(this.a, nval2) : this;
        } else if (ordinal == 28) {
            return newValue != this.a ? K(newValue, this.b) : this;
        } else {
            throw new F("Unsupported field: " + field);
        }
    }

    /* renamed from: U */
    public i g(long amountToAdd, E unit) {
        if (!(unit instanceof k)) {
            return (i) unit.p(this, amountToAdd);
        }
        switch (((k) unit).ordinal()) {
            case 0:
                return W(amountToAdd);
            case 1:
                return T(amountToAdd / 1000000, (amountToAdd % 1000000) * 1000);
            case 2:
                return V(amountToAdd);
            case 3:
                return X(amountToAdd);
            case 4:
                return X(CLASSNAMEk.a(amountToAdd, (long) 60));
            case 5:
                return X(CLASSNAMEk.a(amountToAdd, (long) 3600));
            case 6:
                return X(CLASSNAMEk.a(amountToAdd, (long) 43200));
            case 7:
                return X(CLASSNAMEk.a(amountToAdd, (long) 86400));
            default:
                throw new F("Unsupported unit: " + unit);
        }
    }

    public i X(long secondsToAdd) {
        return T(secondsToAdd, 0);
    }

    public i V(long millisToAdd) {
        return T(millisToAdd / 1000, (millisToAdd % 1000) * 1000000);
    }

    public i W(long nanosToAdd) {
        return T(0, nanosToAdd);
    }

    private i T(long secondsToAdd, long nanosToAdd) {
        if ((secondsToAdd | nanosToAdd) == 0) {
            return this;
        }
        return S(CLASSNAMEe.a(CLASSNAMEe.a(this.a, secondsToAdd), nanosToAdd / NUM), ((long) this.b) + (nanosToAdd % NUM));
    }

    public Object r(D d) {
        if (d == C.l()) {
            return k.NANOS;
        }
        if (d == C.a() || d == C.n() || d == C.m() || d == C.k() || d == C.i() || d == C.j()) {
            return null;
        }
        return d.a(this);
    }

    public u x(u temporal) {
        return temporal.c(j.INSTANT_SECONDS, this.a).c(j.NANO_OF_SECOND, (long) this.b);
    }

    public long Y() {
        long j = this.a;
        if (j >= 0 || this.b <= 0) {
            return CLASSNAMEe.a(CLASSNAMEk.a(this.a, (long) 1000), (long) (this.b / 1000000));
        }
        return CLASSNAMEe.a(CLASSNAMEk.a(j + 1, (long) 1000), (long) ((this.b / 1000000) - 1000));
    }

    /* renamed from: A */
    public int compareTo(i otherInstant) {
        int cmp = (this.a > otherInstant.a ? 1 : (this.a == otherInstant.a ? 0 : -1));
        if (cmp != 0) {
            return cmp;
        }
        return this.b - otherInstant.b;
    }

    public boolean equals(Object otherInstant) {
        if (this == otherInstant) {
            return true;
        }
        if (!(otherInstant instanceof i)) {
            return false;
        }
        i other = (i) otherInstant;
        if (this.a == other.a && this.b == other.b) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        long j = this.a;
        return ((int) (j ^ (j >>> 32))) + (this.b * 51);
    }

    public String toString() {
        return DateTimeFormatter.l.b(this);
    }
}
