package j$.time;

import j$.CLASSNAMEp;
import j$.time.t.t;
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
import j$.time.u.x;
import java.io.Serializable;

public final class l implements u, x, Comparable, Serializable {
    private final LocalDateTime a;
    private final p b;

    static {
        LocalDateTime.c.A(p.h);
        LocalDateTime.d.A(p.g);
    }

    private static int A(l datetime1, l datetime2) {
        if (datetime1.l().equals(datetime2.l())) {
            return datetime1.R().compareTo(datetime2.R());
        }
        int cmp = (datetime1.toEpochSecond() > datetime2.toEpochSecond() ? 1 : (datetime1.toEpochSecond() == datetime2.toEpochSecond() ? 0 : -1));
        if (cmp == 0) {
            return datetime1.d().O() - datetime2.d().O();
        }
        return cmp;
    }

    public static l M(LocalDateTime dateTime, p offset) {
        return new l(dateTime, offset);
    }

    public static l O(i instant, o zone) {
        CLASSNAMEp.a(instant, "instant");
        CLASSNAMEp.a(zone, "zone");
        p offset = zone.A().d(instant);
        return new l(LocalDateTime.W(instant.M(), instant.O(), offset), offset);
    }

    private l(LocalDateTime dateTime, p offset) {
        CLASSNAMEp.a(dateTime, "dateTime");
        this.a = dateTime;
        CLASSNAMEp.a(offset, "offset");
        this.b = offset;
    }

    private l S(LocalDateTime dateTime, p offset) {
        if (this.a != dateTime || !this.b.equals(offset)) {
            return new l(dateTime, offset);
        }
        return this;
    }

    public boolean h(B field) {
        return (field instanceof j) || (field != null && field.K(this));
    }

    public G p(B field) {
        if (!(field instanceof j)) {
            return field.M(this);
        }
        if (field == j.INSTANT_SECONDS || field == j.OFFSET_SECONDS) {
            return field.p();
        }
        return this.a.p(field);
    }

    public int i(B field) {
        if (!(field instanceof j)) {
            return v.a(this, field);
        }
        int i = k.a[((j) field).ordinal()];
        if (i == 1) {
            throw new F("Invalid field 'InstantSeconds' for get() method, use getLong() instead");
        } else if (i != 2) {
            return this.a.i(field);
        } else {
            return l().U();
        }
    }

    public long f(B field) {
        if (!(field instanceof j)) {
            return field.A(this);
        }
        int i = k.a[((j) field).ordinal()];
        if (i == 1) {
            return toEpochSecond();
        }
        if (i != 2) {
            return this.a.f(field);
        }
        return (long) l().U();
    }

    public p l() {
        return this.b;
    }

    public LocalDateTime R() {
        return this.a;
    }

    public LocalDate Q() {
        return this.a.e();
    }

    public LocalTime d() {
        return this.a.d();
    }

    public int L() {
        return this.a.O();
    }

    /* renamed from: T */
    public l a(x adjuster) {
        if ((adjuster instanceof LocalDate) || (adjuster instanceof LocalTime) || (adjuster instanceof LocalDateTime)) {
            return S(this.a.a(adjuster), this.b);
        }
        if (adjuster instanceof i) {
            return O((i) adjuster, this.b);
        }
        if (adjuster instanceof p) {
            return S(this.a, (p) adjuster);
        }
        if (adjuster instanceof l) {
            return (l) adjuster;
        }
        return (l) adjuster.x(this);
    }

    /* renamed from: U */
    public l c(B field, long newValue) {
        if (!(field instanceof j)) {
            return (l) field.L(this, newValue);
        }
        j f = (j) field;
        int i = k.a[f.ordinal()];
        if (i == 1) {
            return O(i.S(newValue, (long) L()), this.b);
        }
        if (i != 2) {
            return S(this.a.c(field, newValue), this.b);
        }
        return S(this.a, p.X(f.O(newValue)));
    }

    /* renamed from: P */
    public l g(long amountToAdd, E unit) {
        if (unit instanceof k) {
            return S(this.a.g(amountToAdd, unit), this.b);
        }
        return (l) unit.p(this, amountToAdd);
    }

    public Object r(D d) {
        if (d == C.k() || d == C.m()) {
            return l();
        }
        if (d == C.n()) {
            return null;
        }
        if (d == C.i()) {
            return Q();
        }
        if (d == C.j()) {
            return d();
        }
        if (d == C.a()) {
            return t.a;
        }
        if (d == C.l()) {
            return k.NANOS;
        }
        return d.a(this);
    }

    public u x(u temporal) {
        return temporal.c(j.EPOCH_DAY, Q().toEpochDay()).c(j.NANO_OF_DAY, d().Y()).c(j.OFFSET_SECONDS, (long) l().U());
    }

    public long toEpochSecond() {
        return this.a.w(this.b);
    }

    /* renamed from: K */
    public int compareTo(l other) {
        int cmp = A(this, other);
        if (cmp == 0) {
            return R().compareTo(other.R());
        }
        return cmp;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof l)) {
            return false;
        }
        l other = (l) obj;
        if (!this.a.equals(other.a) || !this.b.equals(other.b)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.a.hashCode() ^ this.b.hashCode();
    }

    public String toString() {
        return this.a.toString() + this.b.toString();
    }
}
