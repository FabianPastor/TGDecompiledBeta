package j$.time;

import j$.CLASSNAMEp;
import j$.time.t.k;
import j$.time.t.m;
import j$.time.t.q;
import j$.time.u.B;
import j$.time.u.C;
import j$.time.u.D;
import j$.time.u.E;
import j$.time.u.F;
import j$.time.u.G;
import j$.time.u.j;
import j$.time.u.u;
import j$.time.u.x;
import j$.time.v.a;
import j$.time.v.c;
import java.io.Serializable;
import java.time.ZoneOffset;
import java.util.List;

public final class s implements u, m, Serializable {
    private final LocalDateTime a;
    private final p b;
    private final o c;

    public /* synthetic */ q b() {
        return k.d(this);
    }

    public /* bridge */ /* synthetic */ int compareTo(Object obj) {
        return q((m) obj);
    }

    public /* synthetic */ int q(m mVar) {
        return k.a(this, mVar);
    }

    public /* synthetic */ long toEpochSecond() {
        return k.h(this);
    }

    public static s K(LocalDateTime localDateTime, o zone) {
        return O(localDateTime, zone, (p) null);
    }

    public static s O(LocalDateTime localDateTime, o zone, p preferredOffset) {
        p offset;
        CLASSNAMEp.a(localDateTime, "localDateTime");
        CLASSNAMEp.a(zone, "zone");
        if (zone instanceof p) {
            return new s(localDateTime, (p) zone, zone);
        }
        c rules = zone.A();
        List<ZoneOffset> validOffsets = rules.h(localDateTime);
        if (validOffsets.size() == 1) {
            offset = (p) validOffsets.get(0);
        } else if (validOffsets.size() == 0) {
            a trans = rules.g(localDateTime);
            localDateTime = localDateTime.c0(trans.x().x());
            offset = trans.L();
        } else if (preferredOffset == null || !validOffsets.contains(preferredOffset)) {
            p pVar = (p) validOffsets.get(0);
            CLASSNAMEp.a(pVar, "offset");
            offset = pVar;
        } else {
            offset = preferredOffset;
        }
        return new s(localDateTime, offset, zone);
    }

    public static s L(i instant, o zone) {
        CLASSNAMEp.a(instant, "instant");
        CLASSNAMEp.a(zone, "zone");
        return x(instant.M(), instant.O(), zone);
    }

    public static s M(LocalDateTime localDateTime, p offset, o zone) {
        CLASSNAMEp.a(localDateTime, "localDateTime");
        CLASSNAMEp.a(offset, "offset");
        CLASSNAMEp.a(zone, "zone");
        if (zone.A().k(localDateTime, offset)) {
            return new s(localDateTime, offset, zone);
        }
        return x(localDateTime.w(offset), localDateTime.O(), zone);
    }

    private static s x(long epochSecond, int nanoOfSecond, o zone) {
        p offset = zone.A().d(i.S(epochSecond, (long) nanoOfSecond));
        return new s(LocalDateTime.W(epochSecond, nanoOfSecond, offset), offset, zone);
    }

    private s(LocalDateTime dateTime, p offset, o zone) {
        this.a = dateTime;
        this.b = offset;
        this.c = zone;
    }

    private s R(LocalDateTime newDateTime) {
        return O(newDateTime, this.c, this.b);
    }

    private s Q(LocalDateTime newDateTime) {
        return M(newDateTime, this.b, this.c);
    }

    private s S(p offset) {
        if (offset.equals(this.b) || !this.c.A().k(this.a, offset)) {
            return this;
        }
        return new s(this.a, offset, this.c);
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
            return k.c(this, field);
        }
        int i = r.a[((j) field).ordinal()];
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
        int i = r.a[((j) field).ordinal()];
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

    public o s() {
        return this.c;
    }

    /* renamed from: U */
    public LocalDateTime B() {
        return this.a;
    }

    /* renamed from: T */
    public LocalDate e() {
        return this.a.e();
    }

    public LocalTime d() {
        return this.a.d();
    }

    public int A() {
        return this.a.O();
    }

    /* renamed from: V */
    public s a(x adjuster) {
        if (adjuster instanceof LocalDate) {
            return R(LocalDateTime.V((LocalDate) adjuster, this.a.d()));
        }
        if (adjuster instanceof LocalTime) {
            return R(LocalDateTime.V(this.a.e(), (LocalTime) adjuster));
        }
        if (adjuster instanceof LocalDateTime) {
            return R((LocalDateTime) adjuster);
        }
        if (adjuster instanceof l) {
            l odt = (l) adjuster;
            return O(odt.R(), this.c, odt.l());
        } else if (adjuster instanceof i) {
            i instant = (i) adjuster;
            return x(instant.M(), instant.O(), this.c);
        } else if (adjuster instanceof p) {
            return S((p) adjuster);
        } else {
            return (s) adjuster.x(this);
        }
    }

    /* renamed from: W */
    public s c(B field, long newValue) {
        if (!(field instanceof j)) {
            return (s) field.L(this, newValue);
        }
        j f = (j) field;
        int i = r.a[f.ordinal()];
        if (i == 1) {
            return x(newValue, A(), this.c);
        }
        if (i != 2) {
            return R(this.a.c(field, newValue));
        }
        return S(p.X(f.O(newValue)));
    }

    /* renamed from: P */
    public s g(long amountToAdd, E unit) {
        if (!(unit instanceof j$.time.u.k)) {
            return (s) unit.p(this, amountToAdd);
        }
        if (unit.i()) {
            return R(this.a.g(amountToAdd, unit));
        }
        return Q(this.a.g(amountToAdd, unit));
    }

    public Object r(D d) {
        if (d == C.i()) {
            return e();
        }
        return k.f(this, d);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof s)) {
            return false;
        }
        s other = (s) obj;
        if (!this.a.equals(other.a) || !this.b.equals(other.b) || !this.c.equals(other.c)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return (this.a.hashCode() ^ this.b.hashCode()) ^ Integer.rotateLeft(this.c.hashCode(), 3);
    }

    public String toString() {
        String str = this.a.toString() + this.b.toString();
        if (this.b == this.c) {
            return str;
        }
        return str + '[' + this.c.toString() + ']';
    }
}
